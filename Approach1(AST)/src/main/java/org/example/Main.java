//package org.example;
//
//import org.codehaus.groovy.ast.*;
//import org.codehaus.groovy.ast.builder.AstBuilder;
//import org.codehaus.groovy.ast.expr.*;
//import org.codehaus.groovy.ast.stmt.BlockStatement;
//import org.codehaus.groovy.ast.stmt.ExpressionStatement;
//import org.codehaus.groovy.ast.stmt.Statement;
//import org.codehaus.groovy.control.CompilePhase;
//import org.codehaus.groovy.control.CompilerConfiguration;
//import org.codehaus.groovy.control.Phases;
//import org.codehaus.groovy.control.SourceUnit;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.util.List;
//import java.util.ArrayList;
//
//public class Main {
//
//    public static void main(String[] args) {
//        String jenkinsfilePath = "/home/abhisheksharma/abhishek/RedHat/JenkinsToTektonMigration/Approach1(AST)/src/main/java/org/example/Jenkinsfile";
//        try {
//            String jenkinsfileContent = readJenkinsfile(jenkinsfilePath);
//            System.out.println(jenkinsfileContent);
//            List<ASTNode> nodes = parseJenkinsfile(jenkinsfileContent);
////            System.out.println(nodes);
//            analyzeAst(nodes);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    private static String readJenkinsfile(String path) throws IOException {
//        return new String(Files.readAllBytes(new File(path).toPath()));
//    }
//
//    private static List<ASTNode> parseJenkinsfile(String content) {
//        AstBuilder builder = new AstBuilder();
//        return builder.buildFromString(CompilePhase.CONVERSION, true, content);
//    }
//
//    private static void analyzeAst(List<ASTNode> nodes) {
//        // Implement your analysis logic here
//        System.out.println(nodes.size());
//        for (ASTNode node : nodes) {
//            if (node instanceof BlockStatement) {
//                BlockStatement blockStatement = (BlockStatement) node;
//                for (Statement stmt : blockStatement.getStatements()) {
//                    if (stmt instanceof ExpressionStatement) {
//                        Expression expr = ((ExpressionStatement) stmt).getExpression();
//                        if (expr instanceof MethodCallExpression) {
//                            MethodCallExpression methodCall = (MethodCallExpression) expr;
//                            if ("pipeline".equals(methodCall.getMethodAsString())) {
//                                analyzePipeline(methodCall);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private static void analyzePipeline(MethodCallExpression pipelineCall) {
//        System.out.println("Pipeline structure:");
//        Expression arguments = pipelineCall.getArguments();
//        if (arguments instanceof ArgumentListExpression) {
//            ArgumentListExpression argList = (ArgumentListExpression) arguments;
//            for (Expression arg : argList.getExpressions()) {
//                if (arg instanceof ClosureExpression) {
//                    analyzeClosureContent((ClosureExpression) arg, 1);
//                }
//            }
//        }
//    }
//
//    private static void analyzeClosureContent(ClosureExpression closure, int indent) {
//        Statement code = closure.getCode();
//        if (code instanceof BlockStatement) {
//            BlockStatement block = (BlockStatement) code;
//            for (Statement stmt : block.getStatements()) {
//                if (stmt instanceof ExpressionStatement) {
//                    Expression expr = ((ExpressionStatement) stmt).getExpression();
//                    if (expr instanceof MethodCallExpression) {
//                        MethodCallExpression methodCall = (MethodCallExpression) expr;
//                        String methodName = methodCall.getMethodAsString();
//                        System.out.println("  ".repeat(indent) + methodName);
//
//                        if ("stages".equals(methodName)) {
//                            analyzeStages(methodCall, indent + 1);
//                        } else if ("environment".equals(methodName)) {
//                            analyzeEnvironment(methodCall, indent + 1);
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private static void analyzeStages(MethodCallExpression stagesCall, int indent) {
//        Expression arguments = stagesCall.getArguments();
//        if (arguments instanceof ArgumentListExpression) {
//            ArgumentListExpression argList = (ArgumentListExpression) arguments;
//            for (Expression arg : argList.getExpressions()) {
//                if (arg instanceof ClosureExpression) {
//                    analyzeStagesContent((ClosureExpression) arg, indent);
//                }
//            }
//        }
//    }
//
//    private static void analyzeStagesContent(ClosureExpression closure, int indent) {
//        Statement code = closure.getCode();
//        if (code instanceof BlockStatement) {
//            BlockStatement block = (BlockStatement) code;
//            for (Statement stmt : block.getStatements()) {
//                if (stmt instanceof ExpressionStatement) {
//                    Expression expr = ((ExpressionStatement) stmt).getExpression();
//                    if (expr instanceof MethodCallExpression) {
//                        MethodCallExpression methodCall = (MethodCallExpression) expr;
//                        if ("stage".equals(methodCall.getMethodAsString())) {
//                            analyzeStage(methodCall, indent);
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//    private static void analyzeStage(MethodCallExpression stageCall, int indent) {
//        Expression arguments = stageCall.getArguments();
//        if (arguments instanceof ArgumentListExpression) {
//            ArgumentListExpression argList = (ArgumentListExpression) arguments;
//            if (argList.getExpressions().size() >= 2) {
//                Expression nameExpr = argList.getExpression(0);
//                Expression closureExpr = argList.getExpression(1);
//                if (nameExpr instanceof ConstantExpression && closureExpr instanceof ClosureExpression) {
//                    String stageName = ((ConstantExpression) nameExpr).getValue().toString();
//                    System.out.println("  ".repeat(indent) + "Stage: " + stageName);
//                    analyzeStageContent((ClosureExpression) closureExpr, indent + 1);
//                }
//            }
//        }
//    }
//
//    private static void analyzeStageContent(ClosureExpression closure, int indent) {
//        Statement code = closure.getCode();
//        if (code instanceof BlockStatement) {
//            BlockStatement block = (BlockStatement) code;
//            for (Statement stmt : block.getStatements()) {
//                if (stmt instanceof ExpressionStatement) {
//                    Expression expr = ((ExpressionStatement) stmt).getExpression();
//                    if (expr instanceof MethodCallExpression) {
//                        MethodCallExpression methodCall = (MethodCallExpression) expr;
//                        String methodName = methodCall.getMethodAsString();
//                        System.out.println("  ".repeat(indent) + methodName);
//
//                        if ("steps".equals(methodName)) {
//                            analyzeSteps(methodCall, indent + 1);
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private static void analyzeSteps(MethodCallExpression stepsCall, int indent) {
//        Expression arguments = stepsCall.getArguments();
//        if (arguments instanceof ArgumentListExpression) {
//            ArgumentListExpression argList = (ArgumentListExpression) arguments;
//            for (Expression arg : argList.getExpressions()) {
//                if (arg instanceof ClosureExpression) {
//                    analyzeStepsContent((ClosureExpression) arg, indent);
//                }
//            }
//        }
//    }
//
//    private static void analyzeStepsContent(ClosureExpression closure, int indent) {
//        Statement code = closure.getCode();
//        if (code instanceof BlockStatement) {
//            BlockStatement block = (BlockStatement) code;
//            for (Statement stmt : block.getStatements()) {
//                if (stmt instanceof ExpressionStatement) {
//                    Expression expr = ((ExpressionStatement) stmt).getExpression();
//                    analyzeStep(expr, indent);
//                }
//            }
//        }
//    }
//
//    private static void analyzeStep(Expression expr, int indent) {
//        if (expr instanceof MethodCallExpression) {
//            MethodCallExpression methodCall = (MethodCallExpression) expr;
//            String stepName = methodCall.getMethodAsString();
//            System.out.println("  ".repeat(indent) + "Step: " + stepName);
//
//            if ("script".equals(stepName)) {
//                analyzeScriptStep(methodCall, indent + 1);
//            }
//        }
//    }
//
//    private static void analyzeScriptStep(MethodCallExpression scriptCall, int indent) {
//        Expression arguments = scriptCall.getArguments();
//        if (arguments instanceof ArgumentListExpression) {
//            ArgumentListExpression argList = (ArgumentListExpression) arguments;
//            for (Expression arg : argList.getExpressions()) {
//                if (arg instanceof ClosureExpression) {
//                    analyzeScriptContent((ClosureExpression) arg, indent);
//                }
//            }
//        }
//    }
//
//    private static void analyzeScriptContent(ClosureExpression closure, int indent) {
//        Statement code = closure.getCode();
//        if (code instanceof BlockStatement) {
//            BlockStatement block = (BlockStatement) code;
//            for (Statement stmt : block.getStatements()) {
//                if (stmt instanceof ExpressionStatement) {
//                    Expression expr = ((ExpressionStatement) stmt).getExpression();
//                    if (expr instanceof MethodCallExpression) {
//                        MethodCallExpression methodCall = (MethodCallExpression) expr;
//                        String methodName = methodCall.getMethodAsString();
//                        System.out.println("  ".repeat(indent) + "Script action: " + methodName);
//                    }
//                }
//            }
//        }
//    }
//
//    private static void analyzeEnvironment(MethodCallExpression envCall, int indent) {
//        Expression arguments = envCall.getArguments();
//        if (arguments instanceof ArgumentListExpression) {
//            ArgumentListExpression argList = (ArgumentListExpression) arguments;
//            for (Expression arg : argList.getExpressions()) {
//                if (arg instanceof ClosureExpression) {
//                    ClosureExpression closure = (ClosureExpression) arg;
//                    Statement code = closure.getCode();
//                    if (code instanceof BlockStatement) {
//                        BlockStatement block = (BlockStatement) code;
//                        for (Statement stmt : block.getStatements()) {
//                            if (stmt instanceof ExpressionStatement) {
//                                Expression expr = ((ExpressionStatement) stmt).getExpression();
//                                if (expr instanceof BinaryExpression) {
//                                    BinaryExpression binary = (BinaryExpression) expr;
//                                    String varName = binary.getLeftExpression().getText();
//                                    String varValue = binary.getRightExpression().getText();
//                                    System.out.println("  ".repeat(indent) + varName + " = " + varValue);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}


package org.example;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static List<String> extractedPlugins = new ArrayList<>();
    public static void main(String[] args) {



        String jenkinsfilePath = "/home/abhisheksharma/abhishek/RedHat/JenkinsToTektonMigration/Approach1(AST)/src/main/java/org/example/Jenkinsfile";
        try {
            String jenkinsfileContent = readJenkinsfile(jenkinsfilePath);
            List<ASTNode> nodes = parseJenkinsfile(jenkinsfileContent);
            analyzeAst(nodes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readJenkinsfile(String path) throws IOException {
        return new String(Files.readAllBytes(new File(path).toPath()));
    }

    public static List<ASTNode> parseJenkinsfile(String content) {
        AstBuilder builder = new AstBuilder();
        return builder.buildFromString(CompilePhase.CONVERSION, true, content);
    }

    public static void analyzeAst(List<ASTNode> nodes) {
        for (ASTNode node : nodes) {
            if (node instanceof BlockStatement) {
                BlockStatement blockStatement = (BlockStatement) node;
                for (Statement stmt : blockStatement.getStatements()) {
                    if (stmt instanceof ExpressionStatement) {
                        Expression expr = ((ExpressionStatement) stmt).getExpression();
                        if (expr instanceof MethodCallExpression) {
                            MethodCallExpression methodCall = (MethodCallExpression) expr;
                            if ("pipeline".equals(methodCall.getMethodAsString())) {
                                analyzePipeline(methodCall);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void analyzePipeline(MethodCallExpression pipelineCall) {
        System.out.println("Pipeline structure:");
        Expression arguments = pipelineCall.getArguments();
        if (arguments instanceof ArgumentListExpression) {
            ArgumentListExpression argList = (ArgumentListExpression) arguments;
            for (Expression arg : argList.getExpressions()) {
                if (arg instanceof ClosureExpression) {
                    analyzeClosureContent((ClosureExpression) arg, 1);
                }
            }
        }
    }

    private static void analyzeClosureContent(ClosureExpression closure, int indent) {
        Statement code = closure.getCode();
        if (code instanceof BlockStatement) {
            BlockStatement block = (BlockStatement) code;
            for (Statement stmt : block.getStatements()) {
                if (stmt instanceof ExpressionStatement) {
                    Expression expr = ((ExpressionStatement) stmt).getExpression();
                    if (expr instanceof MethodCallExpression) {
                        MethodCallExpression methodCall = (MethodCallExpression) expr;
                        String methodName = methodCall.getMethodAsString();
                        System.out.println("  ".repeat(indent) + methodName);

                        if ("stages".equals(methodName)) {
                            analyzeStages(methodCall, indent + 1);
                        } else if ("environment".equals(methodName)) {
                            analyzeEnvironment(methodCall, indent + 1);
                        }
                    }
                }
            }
        }
    }

    private static void analyzeStages(MethodCallExpression stagesCall, int indent) {
        Expression arguments = stagesCall.getArguments();
        if (arguments instanceof ArgumentListExpression) {
            ArgumentListExpression argList = (ArgumentListExpression) arguments;
            for (Expression arg : argList.getExpressions()) {
                if (arg instanceof ClosureExpression) {
                    analyzeStagesContent((ClosureExpression) arg, indent);
                }
            }
        }
    }

    private static void analyzeStagesContent(ClosureExpression closure, int indent) {
        Statement code = closure.getCode();
        if (code instanceof BlockStatement) {
            BlockStatement block = (BlockStatement) code;
            for (Statement stmt : block.getStatements()) {
                if (stmt instanceof ExpressionStatement) {
                    Expression expr = ((ExpressionStatement) stmt).getExpression();
                    if (expr instanceof MethodCallExpression) {
                        MethodCallExpression methodCall = (MethodCallExpression) expr;
                        if ("stage".equals(methodCall.getMethodAsString())) {
                            analyzeStage(methodCall, indent);
                        }
                    }
                }
            }
        }
    }

    private static void analyzeStage(MethodCallExpression stageCall, int indent) {
        Expression arguments = stageCall.getArguments();
        if (arguments instanceof ArgumentListExpression) {
            ArgumentListExpression argList = (ArgumentListExpression) arguments;
            if (argList.getExpressions().size() >= 2) {
                Expression nameExpr = argList.getExpression(0);
                Expression closureExpr = argList.getExpression(1);
                if (nameExpr instanceof ConstantExpression && closureExpr instanceof ClosureExpression) {
                    String stageName = ((ConstantExpression) nameExpr).getValue().toString();
                    System.out.println("  ".repeat(indent) + "Stage: " + stageName);
                    analyzeStageContent((ClosureExpression) closureExpr, indent + 1);
                }
            }
        }
    }

    private static void analyzeStageContent(ClosureExpression closure, int indent) {
        Statement code = closure.getCode();
        if (code instanceof BlockStatement) {
            BlockStatement block = (BlockStatement) code;
            for (Statement stmt : block.getStatements()) {
                if (stmt instanceof ExpressionStatement) {
                    Expression expr = ((ExpressionStatement) stmt).getExpression();
                    if (expr instanceof MethodCallExpression) {
                        MethodCallExpression methodCall = (MethodCallExpression) expr;
                        String methodName = methodCall.getMethodAsString();
                        System.out.println("  ".repeat(indent) + methodName);

                        if ("steps".equals(methodName)) {
                            analyzeSteps(methodCall, indent + 1);
                        }
                    }
                }
            }
        }
    }

    private static void analyzeSteps(MethodCallExpression stepsCall, int indent) {
        Expression arguments = stepsCall.getArguments();
        if (arguments instanceof ArgumentListExpression) {
            ArgumentListExpression argList = (ArgumentListExpression) arguments;
            for (Expression arg : argList.getExpressions()) {
                if (arg instanceof ClosureExpression) {
                    analyzeStepsContent((ClosureExpression) arg, indent);
                }
            }
        }
    }

    private static void analyzeStepsContent(ClosureExpression closure, int indent) {
        Statement code = closure.getCode();
        if (code instanceof BlockStatement) {
            BlockStatement block = (BlockStatement) code;
            for (Statement stmt : block.getStatements()) {
                if (stmt instanceof ExpressionStatement) {
                    Expression expr = ((ExpressionStatement) stmt).getExpression();
                    analyzeStep(expr, indent);
                }
            }
        }
    }

    private static void analyzeStep(Expression expr, int indent) {
        if (expr instanceof MethodCallExpression) {
            MethodCallExpression methodCall = (MethodCallExpression) expr;
            String stepName = methodCall.getMethodAsString();
            String objectExpression = methodCall.getObjectExpression().getText();

            if (!objectExpression.equals("this")) {
                System.out.println("  ".repeat(indent) + "Plugin: " + objectExpression + " (Method: " + stepName + ")");
                extractedPlugins.add(objectExpression);  // Store plugin name for testing
            } else {
                System.out.println("  ".repeat(indent) + "Step: " + stepName);
            }

            if ("script".equals(stepName)) {
                analyzeScriptStep(methodCall, indent + 1);
            } else {
                analyzeStepArguments(methodCall, indent + 1);
            }
        }
    }

    private static void analyzeScriptStep(MethodCallExpression scriptCall, int indent) {
        Expression arguments = scriptCall.getArguments();
        if (arguments instanceof ArgumentListExpression) {
            ArgumentListExpression argList = (ArgumentListExpression) arguments;
            for (Expression arg : argList.getExpressions()) {
                if (arg instanceof ClosureExpression) {
                    analyzeScriptContent((ClosureExpression) arg, indent);
                }
            }
        }
    }

    private static void analyzeScriptContent(ClosureExpression closure, int indent) {
        Statement code = closure.getCode();
        if (code instanceof BlockStatement) {
            BlockStatement block = (BlockStatement) code;
            for (Statement stmt : block.getStatements()) {
                if (stmt instanceof ExpressionStatement) {
                    Expression expr = ((ExpressionStatement) stmt).getExpression();
                    analyzeStep(expr, indent);
                }
            }
        }
    }

    private static void analyzeStepArguments(MethodCallExpression methodCall, int indent) {
        Expression arguments = methodCall.getArguments();
        if (arguments instanceof ArgumentListExpression) {
            ArgumentListExpression argList = (ArgumentListExpression) arguments;
            for (Expression arg : argList.getExpressions()) {
                if (arg instanceof ConstantExpression) {
                    System.out.println("  ".repeat(indent) + "Argument: " + ((ConstantExpression) arg).getValue());
                } else if (arg instanceof ClosureExpression) {
                    System.out.println("  ".repeat(indent) + "Closure argument:");
                    analyzeClosureContent((ClosureExpression) arg, indent + 1);
                }
            }
        }
    }

    private static void analyzeEnvironment(MethodCallExpression envCall, int indent) {
        Expression arguments = envCall.getArguments();
        if (arguments instanceof ArgumentListExpression) {
            ArgumentListExpression argList = (ArgumentListExpression) arguments;
            for (Expression arg : argList.getExpressions()) {
                if (arg instanceof ClosureExpression) {
                    ClosureExpression closure = (ClosureExpression) arg;
                    Statement code = closure.getCode();
                    if (code instanceof BlockStatement) {
                        BlockStatement block = (BlockStatement) code;
                        for (Statement stmt : block.getStatements()) {
                            if (stmt instanceof ExpressionStatement) {
                                Expression expr = ((ExpressionStatement) stmt).getExpression();
                                if (expr instanceof BinaryExpression) {
                                    BinaryExpression binary = (BinaryExpression) expr;
                                    String varName = binary.getLeftExpression().getText();
                                    String varValue = binary.getRightExpression().getText();
                                    System.out.println("  ".repeat(indent) + varName + " = " + varValue);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add a public method to retrieve extracted plugins for testing
    public static List<String> getExtractedPlugins() {
        return new ArrayList<>(extractedPlugins);
    }

    public static void clearExtractedPlugins() {
        extractedPlugins.clear();
    }
}