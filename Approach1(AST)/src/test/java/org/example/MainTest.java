package org.example;//package org.example;/.

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.example.Main;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @BeforeEach
    void setUp() {
        Main.clearExtractedPlugins();
    }

    @Test
    void testExtractCorrectPlugins() {
        String jenkinsfileContent =
                "pipeline { stages { stage('Build') { steps { git url: 'https://github.com/repo.git'; " +
                        "docker.build('image') } } } }";

        List<ASTNode> nodes = Main.parseJenkinsfile(jenkinsfileContent);
        Main.analyzeAst(nodes);

        List<String> plugins = Main.getExtractedPlugins();
        assertEquals(2, plugins.size());
        assertTrue(plugins.contains("git"));
        assertTrue(plugins.contains("docker"));
    }

    @Test
    void testExtractCorrectNumberOfPlugins() {
        String jenkinsfileContent =
                "pipeline { stages { stage('Test') { steps { junit 'results.xml'; docker.build('image') } } } }";

        List<ASTNode> nodes = Main.parseJenkinsfile(jenkinsfileContent);
        Main.analyzeAst(nodes);

        List<String> plugins = Main.getExtractedPlugins();
        assertEquals(2, plugins.size());
    }

    @Test
    void testNoPluginsExtracted() {
        String jenkinsfileContent =
                "pipeline { stages { stage('Deploy') { steps { echo 'No plugins here' } } } }";

        List<ASTNode> nodes = Main.parseJenkinsfile(jenkinsfileContent);
        Main.analyzeAst(nodes);

        List<String> plugins = Main.getExtractedPlugins();
        assertEquals(0, plugins.size());
    }

    @Test
    void testNestedPluginExtractionInScriptBlock() {
        String jenkinsfileContent =
                "pipeline { stages { stage('Build') { steps { script { git url: 'https://github.com/repo.git'; " +
                        "sh 'mvn clean install'; docker.build('image') } } } } }";

        List<ASTNode> nodes = Main.parseJenkinsfile(jenkinsfileContent);
        Main.analyzeAst(nodes);

        List<String> plugins = Main.getExtractedPlugins();
        assertEquals(2, plugins.size());
        assertTrue(plugins.contains("git"));
        assertTrue(plugins.contains("docker"));
    }

    @Test
    void testMultiplePluginOccurrences() {
        String jenkinsfileContent =
                "pipeline { stages { stage('Build') { steps { docker.build('image1'); docker.build('image2') } } } }";

        List<ASTNode> nodes = Main.parseJenkinsfile(jenkinsfileContent);
        Main.analyzeAst(nodes);

        List<String> plugins = Main.getExtractedPlugins();
        assertEquals(2, plugins.size());
        assertTrue(plugins.stream().allMatch(plugin -> plugin.equals("docker")));
    }
}
