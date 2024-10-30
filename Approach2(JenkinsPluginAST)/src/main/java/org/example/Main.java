package org.example;

import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.control.SourceUnit;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import groovy.lang.GroovyShell;
import groovy.lang.Binding;
import hudson.ExtensionList;
import hudson.PluginWrapper;
import jenkins.model.Jenkins;
import hudson.Extension;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.*;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private final Map<String, PluginStepInfo> stepToPluginMapping;

    public static class PluginStepInfo {
        private final String pluginName;
        private final String pluginVersion;
        private final String className;

        public PluginStepInfo(String pluginName, String pluginVersion, String className) {
            this.pluginName = pluginName;
            this.pluginVersion = pluginVersion;
            this.className = className;
        }

        @Override
        public String toString() {
            return String.format("Plugin: %s (v%s), Implementation: %s",
                    pluginName, pluginVersion, className);
        }
    }

    public Main() {
        stepToPluginMapping = new HashMap<>();
        loadStepMappings();
    }

    private void loadStepMappings() {
        Jenkins jenkins = Jenkins.get();
        if (jenkins == null) {
            throw new IllegalStateException("Jenkins instance not available");
        }

        ExtensionList<StepDescriptor> stepDescriptors = jenkins.getExtensionList(StepDescriptor.class);

        for (StepDescriptor descriptor : stepDescriptors) {
            String stepName = descriptor.getFunctionName();
            Class<?> stepClass = descriptor.getClass();

            PluginWrapper plugin = jenkins.getPluginManager().whichPlugin(stepClass);

            if (plugin != null) {
                PluginStepInfo info = new PluginStepInfo(
                        plugin.getShortName(),
                        plugin.getVersion(),
                        stepClass.getName()
                );
                stepToPluginMapping.put(stepName, info);
                LOGGER.fine(() -> String.format("Mapped step '%s' to %s", stepName, info));
            } else {
                PluginStepInfo info = new PluginStepInfo(
                        "jenkins-core",
                        Jenkins.VERSION,
                        stepClass.getName()
                );
                stepToPluginMapping.put(stepName, info);
                LOGGER.fine(() -> String.format("Mapped core step '%s' to %s", stepName, info));
            }
        }
    }

    public Map<String, Set<StepUsageInfo>> analyzeJenkinsfile(String jenkinsfileContent) {
        Map<String, Set<StepUsageInfo>> usedPlugins = new HashMap<>();

        ModuleNode moduleNode = parseJenkinsfile(jenkinsfileContent);

        moduleNode.getStatementBlock().visit(new CodeVisitorSupport() {
            @Override
            public void visitMethodCallExpression(MethodCallExpression call) {
                String stepName = call.getMethodAsString();
                if (stepToPluginMapping.containsKey(stepName)) {
                    PluginStepInfo info = stepToPluginMapping.get(stepName);

                    StepUsageInfo usageInfo = new StepUsageInfo(
                            stepName,
                            call.getLineNumber(),
                            call.getLastLineNumber(),
                            extractArguments(call)
                    );

                    usedPlugins.computeIfAbsent(info.pluginName, k -> new HashSet<>())
                            .add(usageInfo);
                }
                super.visitMethodCallExpression(call);
            }
        });

        return usedPlugins;
    }

    public static class StepUsageInfo {
        private final String stepName;
        private final int lineNumber;
        private final int lastLineNumber;
        private final Map<String, Object> arguments;

        public StepUsageInfo(String stepName, int lineNumber, int lastLineNumber, Map<String, Object> arguments) {
            this.stepName = stepName;
            this.lineNumber = lineNumber;
            this.lastLineNumber = lastLineNumber;
            this.arguments = arguments;
        }

        @Override
        public String toString() {
            return String.format("%s (lines %d-%d) with args: %s",
                    stepName, lineNumber, lastLineNumber, arguments);
        }
    }

    private ModuleNode parseJenkinsfile(String content) {
        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(CpsScript.class.getName());

        CompilationUnit cu = new CompilationUnit(config);

        SourceUnit sourceUnit = SourceUnit.create("Jenkinsfile", content);
        cu.addSource(sourceUnit);

        try {
            cu.compile(Phases.CONVERSION);
            return sourceUnit.getAST();
        } catch (Exception e) {
            LOGGER.severe("Failed to parse Jenkinsfile: " + e.getMessage());
            throw new RuntimeException("Failed to parse Jenkinsfile", e);
        }
    }

    private Map<String, Object> extractArguments(MethodCallExpression call) {
        Map<String, Object> args = new HashMap<>();

        Expression arguments = call.getArguments();
        if (arguments instanceof TupleExpression) {
            TupleExpression tuple = (TupleExpression) arguments;
            for (Expression exp : tuple.getExpressions()) {
                if (exp instanceof NamedArgumentListExpression) {
                    NamedArgumentListExpression named = (NamedArgumentListExpression) exp;
                    for (MapEntryExpression entry : named.getMapEntryExpressions()) {
                        String key = entry.getKeyExpression().getText();
                        String value = entry.getValueExpression().getText();
                        args.put(key, value);
                    }
                } else if (exp instanceof ConstantExpression) {
                    args.put("value", exp.getText());
                }
            }
        }
        return args;
    }

    public String analyzeJenkinsfileFromPath(String jenkinsfilePath) {
        try {
            String jenkinsfileContent = new String(Files.readAllBytes(Paths.get(jenkinsfilePath)), StandardCharsets.UTF_8);

            Map<String, Set<StepUsageInfo>> usedPlugins = analyzeJenkinsfile(jenkinsfileContent);

            StringBuilder result = new StringBuilder("Plugins and steps used in Jenkinsfile:\n");
            usedPlugins.forEach((plugin, steps) -> {
                result.append("\nPlugin: ").append(plugin).append("\n");
                steps.forEach(step -> result.append("  - ").append(step).append("\n"));
            });

            return result.toString();

        } catch (Exception e) {
            return "Error analyzing Jenkinsfile: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        String jenkinsfilePath = args[0];

//        String jenkinsfilePath = "/home/abhisheksharma/abhishek/RedHat/JenkinsToTektonMigration/Approach2/src/main/java/org/example/Jenkinsfile";
        Main analyzer = new Main();
        String analysis = analyzer.analyzeJenkinsfileFromPath(jenkinsfilePath);
        System.out.println(analysis);
    }
}