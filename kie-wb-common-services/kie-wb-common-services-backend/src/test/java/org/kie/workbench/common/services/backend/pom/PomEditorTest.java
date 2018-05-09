/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.backend.pom;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PomEditorTest {

    private static final String CURRICULUM_COURSE_PRJ = "/target/test-classes/curriculumcourse/";
    private static final String DINNER_PARTY_PRJ = "/target/test-classes/dinnerparty/";
    private static final String EMPLOYEE_ROSTERING_PRJ = "/target/test-classes/employee-rostering/";
    private static final String EVALUATION_PRJ = "/target/test-classes/employee-rostering/";
    private static final String ITORDERS_PRJ = "/target/test-classes/itorders/";
    private static final String MORTGAGES_PRJ = "/target/test-classes/mortgages/";
    private static final String OPTACLOUD_PRJ = "/target/test-classes/optacloud/";
    private final Logger logger = LoggerFactory.getLogger(PomEditorTest.class);
    private PomEditor editor;
    private String currentDir;
    private WeldContainer weldContainer;

    @Before
    public void setUp() {
        currentDir = new File(".").getAbsolutePath();
        editor = new PomEditor();
    }

    @After
    public void tearDown() {
        if (weldContainer != null) {
            weldContainer.close();
        }
    }

    private void testDefault(String prj) {
        Path path = Paths.get("file://" + currentDir + prj + "pom.xml");
        Path pathCopy = Paths.get("file://" + currentDir + prj + "copy_pom.xml");
        Files.copy(path, pathCopy);
        try {
            Model original = editor.getModel(path);
            assertTrue(original.getBuild().getPlugins().size() == 1);

            Model modelUpdated = editor.updatePomWithoutWrite(path);
            assertEquals(modelUpdated.getPackaging(), "kjar");
            assertNotNull(modelUpdated);
            assertTrue(modelUpdated.getBuild().getPlugins().size() == 1);
            List<Dependency> deps = modelUpdated.getDependencies();
            for (Dependency dep : deps) {
                assertTrue(dep.getVersion() != null);
            }
        } catch (IOException ioex) {
            logger.error(ioex.getMessage(), ioex);
            throw new AssertionError(ioex.getMessage());
        } catch (XmlPullParserException xmlEx) {
            logger.error(xmlEx.getMessage(), xmlEx);
            throw new AssertionError(xmlEx.getMessage());
        } finally {
            Files.delete(path);
            Files.copy(pathCopy, path);
            Files.delete(pathCopy);
        }
    }

    @Test
    public void updateCurriculumCourse() {
        testDefault(CURRICULUM_COURSE_PRJ);
    }

    @Test
    public void updateDinnerParty() {
        testDefault(DINNER_PARTY_PRJ);
    }

    @Test
    public void updateEmployeeRostering() {
        testDefault(EMPLOYEE_ROSTERING_PRJ);
    }

    @Test
    public void updateEvaluation() {
        testDefault(EVALUATION_PRJ);
    }

    @Test
    public void updateItOrders() {
        testDefault(ITORDERS_PRJ);
    }

    @Test
    public void updateMortgages() {
        testDefault(MORTGAGES_PRJ);
    }

    @Test
    public void updateOptacloud() {
        testDefault(OPTACLOUD_PRJ);
    }

    @Test
    public void updateGenericPom() {
        String prj = "/target/test-classes/generic/";
        Path jsonPath = Paths.get("file:" + currentDir + prj + "/pom-migration.json");
        Path path = Paths.get("file:" + currentDir + prj + "pom.xml");
        Path pathCopy = Paths.get("file:" + currentDir + prj + "copy_pom.xml");
        Files.copy(path, pathCopy);
        try {

            Model original = editor.getModel(path);
            assertEquals(original.getPackaging(), "jar");
            assertTrue(original.getBuild().getPlugins().size() == 1);
            assertTrue(original.getDependencies().size() == 3);
            assertTrue(original.getRepositories().size() == 0);
            assertTrue(original.getPluginRepositories().size() == 0);

            Model modelUpdated = editor.updatePomWithoutWrite(path, jsonPath.toAbsolutePath().toString());
            assertNotNull(modelUpdated);
            assertEquals(modelUpdated.getPackaging(), "kjar");
            assertTrue(modelUpdated.getBuild().getPlugins().size() == 1);
            assertTrue(modelUpdated.getDependencies().size() == 6);
            assertTrue(modelUpdated.getRepositories().size() == 2);
            assertTrue(modelUpdated.getPluginRepositories().size() == 2);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AssertionError(e.getMessage());
        } finally {
            Files.delete(path);
            Files.copy(pathCopy, path);
            Files.delete(pathCopy);
        }
    }

    @Test
    public void updateGenericPomWithBrokenParams() {
        String prj = "/target/test-classes/generic/";
        Path path = Paths.get("file:" + currentDir + prj + "pom.xml");
        Path pathCopy = Paths.get("file:" + currentDir + prj + "copy_pom.xml");
        Files.copy(path, pathCopy);
        try {

            Model original = editor.getModel(path);
            assertTrue(original.getBuild().getPlugins().size() == 1);
            assertTrue(original.getDependencies().size() == 3);
            assertTrue(original.getRepositories().size() == 0);
            assertTrue(original.getPluginRepositories().size() == 0);

            Model modelUpdated;
            modelUpdated = editor.updatePomWithoutWrite(path, null);
            assertTrue(modelUpdated.getGroupId() == null);
            modelUpdated = editor.updatePomWithoutWrite(null, null);
            assertTrue(modelUpdated.getGroupId() == null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AssertionError(e.getMessage());
        } finally {
            Files.delete(path);
            Files.copy(pathCopy, path);
            Files.delete(pathCopy);
        }
    }

    @Test
    public void updateGenericWithCheckVersionOfExistingDep() {
        String prj = "/target/test-classes/generic_with_version/";
        Path jsonPath = Paths.get("file:" + currentDir + prj + "/pom-migration.json");
        Path path = Paths.get("file:" + currentDir + prj + "pom.xml");
        Path pathCopy = Paths.get("file:" + currentDir + prj + "copy_pom.xml");
        Files.copy(path, pathCopy);
        try {

            Model original = editor.getModel(path);
            assertEquals(original.getPackaging(), "kjar");
            assertTrue(original.getDependencies().size() == 1);
            assertTrue(original.getRepositories().size() == 0);
            assertTrue(original.getPluginRepositories().size() == 0);

            Model modelUpdated = editor.updatePomWithoutWrite(path, jsonPath.toAbsolutePath().toString());
            assertNotNull(modelUpdated);
            assertEquals(modelUpdated.getPackaging(), "kjar");
            assertTrue(modelUpdated.getBuild().getPlugins().size() == 1);
            assertTrue(modelUpdated.getDependencies().size() == 7);
            assertTrue(modelUpdated.getRepositories().size() == 2);
            assertTrue(modelUpdated.getPluginRepositories().size() == 2);
            for(Dependency dep: modelUpdated.getDependencies()){
                assertTrue(dep.getVersion() != null);
                if(dep.getGroupId().equals("org.group") && dep.getArtifactId().equals("workitems")){
                    assertTrue(dep.getVersion().equals("1.0.0-FINAL"));
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AssertionError(e.getMessage());
        } finally {
            Files.delete(path);
            Files.copy(pathCopy, path);
            Files.delete(pathCopy);
        }
    }
}
