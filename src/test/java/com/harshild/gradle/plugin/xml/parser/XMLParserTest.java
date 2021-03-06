package com.harshild.gradle.plugin.xml.parser;

import com.harshild.GradleTestHelper;
import com.harshild.gradle.plugin.models.xml.parse.XmlRootProject;
import com.harshild.gradle.plugin.xml.parser.XMLParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by harshild on 2/7/2017.
 */
public class XMLParserTest {
    @Rule
    public final TemporaryFolder testResourceDir = new TemporaryFolder();
    private File xmlFile;
    private File xmlFile1;

    String fileContent;
    String fileContentWithNamespace;
    String fileContentWithNamespace2;


    @Before
    public void setUp() throws IOException {
        xmlFile = testResourceDir.newFile("test.xml");
        xmlFile1 = testResourceDir.newFile("test1.xml");
        fileContent = "<project>\n" +
                "\n" +
                "    <groupId>junit</groupId>\n" +
                "    <artifactId>junit</artifactId>\n" +
                "    <version>4.12</version>\n"+
                "</project>";

        fileContentWithNamespace = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "    <groupId>junit</groupId>\n" +
                "    <artifactId>junit</artifactId>\n" +
                "    <version>4.12</version>"+
                "</project>";

        fileContentWithNamespace2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "    <groupId>junit_test</groupId>\n" +
                "    <artifactId>junit_test</artifactId>\n" +
                "    <version>4.12</version>"+
                "</project>";

        GradleTestHelper.writeFile(xmlFile,fileContentWithNamespace);
    }

    @Test
    public void itShouldBeAbleToParseXML() throws Exception {

        GradleTestHelper.writeFile(xmlFile,fileContent);

        XmlRootProject xmlRootProject = new XMLParser<XmlRootProject>()
                .parseXML(xmlFile,XmlRootProject.class);

        assertFalse(xmlRootProject ==null);
        assertEquals("junit", xmlRootProject.getGroupId());
        assertEquals("junit", xmlRootProject.getArtifactId());
        assertEquals("4.12", xmlRootProject.getVersion());

    }

    @Test
    public void itShouldBeAbleToParseXMLAndIgnoreNamespace() throws Exception {
        GradleTestHelper.writeFile(xmlFile,fileContentWithNamespace);

        XmlRootProject xmlRootProject = new XMLParser<XmlRootProject>()
                .parseXML(xmlFile,XmlRootProject.class);

        assertFalse(xmlRootProject ==null);
        assertEquals("junit", xmlRootProject.getGroupId());
        assertEquals("junit", xmlRootProject.getArtifactId());
        assertEquals("4.12", xmlRootProject.getVersion());

    }

    @Test
    public void itShouldBeAbleToParseMultipleXMLsAndIgnoreNamespace() throws Exception {
        GradleTestHelper.writeFile(xmlFile,fileContentWithNamespace);
        GradleTestHelper.writeFile(xmlFile1,fileContentWithNamespace2);

        Map<String,String> map = new HashMap<>();
        map.put("1",xmlFile.getAbsolutePath());
        map.put("2",xmlFile1.getAbsolutePath());



        List<XmlRootProject> xmlRootProjectList = new XMLParser<XmlRootProject>()
                .parseXMLs(map,XmlRootProject.class);

        assertTrue(xmlRootProjectList.size() == 2);
        assertEquals("junit", xmlRootProjectList.get(0).getGroupId());
        assertEquals("junit", xmlRootProjectList.get(0).getArtifactId());
        assertEquals("4.12", xmlRootProjectList.get(0).getVersion());

        assertEquals("junit_test", xmlRootProjectList.get(1).getGroupId());
        assertEquals("junit_test", xmlRootProjectList.get(1).getArtifactId());
        assertEquals("4.12", xmlRootProjectList.get(1).getVersion());

    }
}
