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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;

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
    private Path path;
    private Path pathCopy;

    @Before
    public void setUp() {
        currentDir = new File(".").getAbsolutePath();
        editor = new PomEditor();
    }

    @After
    public void tearDown() {
        Files.delete(path);
        Files.copy(pathCopy, path);
        Files.delete(pathCopy);
    }

    private void testDefault(String prj) {
        path = Paths.get("file://" + currentDir + prj + "pom.xml");
        pathCopy = Paths.get("file://" + currentDir + prj + "copy_pom.xml");
        Files.copy(path, pathCopy);
        try {
            Model original = editor.getModel(path);
            assertThat(original.getBuild().getPlugins().size()).isEqualTo(1);;

            Model modelUpdated = editor.updatePomWithoutWrite(path);
            assertThat(modelUpdated.getPackaging()).isEqualToIgnoringCase("kjar");
            assertThat(modelUpdated).isNotNull();
            assertThat(modelUpdated.getBuild().getPlugins()).hasSize(1);
            List<Dependency> deps = modelUpdated.getDependencies();
            for (Dependency dep : deps) {
                assertThat(dep.getVersion()).isNotNull();
            }
        } catch (IOException ioex) {
            logger.error(ioex.getMessage(), ioex);
            throw new AssertionError(ioex.getMessage());
        } catch (XmlPullParserException xmlEx) {
            logger.error(xmlEx.getMessage(), xmlEx);
            throw new AssertionError(xmlEx.getMessage());
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
        path = Paths.get("file:" + currentDir + prj + "pom.xml");
        pathCopy = Paths.get("file:" + currentDir + prj + "copy_pom.xml");
        Files.copy(path, pathCopy);
        try {

            Model original = editor.getModel(path);
            assertThat(original.getPackaging()).isEqualToIgnoringCase("jar");
            assertThat(original.getBuild().getPlugins()).hasSize(1);
            assertThat(original.getDependencies()).hasSize(3);
            assertThat(original.getRepositories()).hasSize(0);
            assertThat(original.getPluginRepositories()).hasSize(0);

            Model modelUpdated = editor.updatePomWithoutWrite(path, jsonPath.toAbsolutePath().toString());
            assertThat(modelUpdated).isNotNull();
            assertThat(modelUpdated.getPackaging()).isEqualToIgnoringCase("kjar");
            assertThat(modelUpdated.getBuild().getPlugins()).hasSize(1);
            assertThat(modelUpdated.getDependencies()).hasSize(6);
            assertThat(modelUpdated.getRepositories()).hasSize(2);
            assertThat(modelUpdated.getPluginRepositories()).hasSize(2);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AssertionError(e.getMessage());
        }
    }

    @Test
    public void updateGenericPomWithUpdateRepo() {
        String prj = "/target/test-classes/generic_update_repo/";
        Path jsonPath = Paths.get("file:" + currentDir + prj + "/pom-migration.json");
        path = Paths.get("file:" + currentDir + prj + "pom.xml");
        pathCopy = Paths.get("file:" + currentDir + prj + "copy_pom.xml");
        Files.copy(path, pathCopy);
        try {

            Model original = editor.getModel(path);
            assertThat(original.getPackaging()).isEqualToIgnoringCase("jar");
            assertThat(original.getBuild().getPlugins()).hasSize(1);
            assertThat(original.getDependencies()).hasSize(3);
            assertThat(original.getRepositories()).hasSize(2);
            assertThat(original.getPluginRepositories()).hasSize(1);
            List<org.apache.maven.model.Repository> repos = original.getRepositories();
            for(org.apache.maven.model.Repository repo : repos){
                if(repo.getId().equals("guvnor-m2-repo")){
                    assertThat(repo.getUrl()).isEqualToIgnoringCase("http://localhost:8080/business-central/maven2/");
                }
                if(repo.getId().equals("productization-repository")){
                    assertThat(repo.getUrl()).isEqualToIgnoringCase("http://download.lab.bos.redhat.com/brewroot/repos/jb-ip-6.1-build/latest/maven/");
                }
            }

            Model modelUpdated = editor.updatePomWithoutWrite(path, jsonPath.toAbsolutePath().toString());
            assertThat(modelUpdated).isNotNull();
            assertThat(modelUpdated.getPackaging()).isEqualToIgnoringCase("kjar");
            assertThat(modelUpdated.getBuild().getPlugins()).hasSize(1);
            assertThat(modelUpdated.getDependencies()).hasSize(6);
            assertThat(modelUpdated.getRepositories()).hasSize(3);
            assertThat(modelUpdated.getPluginRepositories()).hasSize(3);

            List<org.apache.maven.model.Repository> reposUpdated = modelUpdated.getRepositories();
            for(org.apache.maven.model.Repository repo : reposUpdated){
                if(repo.getId().equals("guvnor-m2-repo")){
                    assertThat(repo.getUrl()).isEqualToIgnoringCase("http://127.0.0.1:8080/business-central/maven3/");
                }
                if(repo.getId().equals("productization-repository")){
                    throw new AssertionError("repositories not removed");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AssertionError(e.getMessage());
        }
    }

    @Test
    public void updateGenericPomNoOptionalJson() {
        String prj = "/target/test-classes/generic_no_json/";
        path = Paths.get("file:" + currentDir + prj + "pom.xml");
        pathCopy = Paths.get("file:" + currentDir + prj + "copy_pom.xml");
        Files.copy(path, pathCopy);
        try {

            Model original = editor.getModel(path);
            assertThat(original.getPackaging()).isEqualToIgnoringCase("jar");
            assertThat(original.getBuild().getPlugins()).hasSize(1);
            assertThat(original.getDependencies()).hasSize(3);
            assertThat(original.getRepositories()).hasSize(2);
            assertThat(original.getPluginRepositories()).hasSize(1);

            Model modelUpdated = editor.updatePomWithoutWrite(path);
            assertThat(modelUpdated).isNotNull();
            assertThat(modelUpdated.getPackaging()).isEqualToIgnoringCase("kjar");
            assertThat(modelUpdated.getBuild().getPlugins()).hasSize(1);
            assertThat(modelUpdated.getDependencies()).hasSize(6);
            assertThat(modelUpdated.getRepositories()).hasSize(0);
            assertThat(modelUpdated.getPluginRepositories()).hasSize(0);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AssertionError(e.getMessage());
        }
    }

    @Test
    public void updateGenericPomWithBrokenParams() {
        String prj = "/target/test-classes/generic/";
        path = Paths.get("file:" + currentDir + prj + "pom.xml");
        pathCopy = Paths.get("file:" + currentDir + prj + "copy_pom.xml");
        Files.copy(path, pathCopy);
        try {
            Model original = editor.getModel(path);
            assertThat(original.getBuild().getPlugins()).hasSize(1);
            assertThat(original.getDependencies()).hasSize(3);
            assertThat(original.getRepositories()).hasSize(0);
            assertThat(original.getPluginRepositories()).hasSize(0);

            Model modelUpdated;
            modelUpdated = editor.updatePomWithoutWrite(path, null);
            assertThat(modelUpdated.getGroupId()).isNullOrEmpty();
            modelUpdated = editor.updatePomWithoutWrite(null, null);
            assertThat(modelUpdated.getGroupId()).isNullOrEmpty();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AssertionError(e.getMessage());
        }
    }

    @Test
    public void updateGenericWithCheckVersionOfExistingDep() {
        String prj = "/target/test-classes/generic_with_version/";
        Path jsonPath = Paths.get("file:" + currentDir + prj + "/pom-migration.json");
        path = Paths.get("file:" + currentDir + prj + "pom.xml");
        pathCopy = Paths.get("file:" + currentDir + prj + "copy_pom.xml");
        Files.copy(path, pathCopy);
        try {

            Model original = editor.getModel(path);
            assertThat(original.getPackaging()).isEqualToIgnoringCase("kjar");
            assertThat(original.getDependencies()).hasSize(1);
            assertThat(original.getRepositories()).hasSize(0);
            assertThat(original.getPluginRepositories()).hasSize(0);

            Model modelUpdated = editor.updatePomWithoutWrite(path, jsonPath.toAbsolutePath().toString());
            assertThat(modelUpdated).isNotNull();
            assertThat(modelUpdated.getPackaging()).isEqualToIgnoringCase("kjar");
            assertThat(modelUpdated.getBuild().getPlugins()).hasSize(1);
            assertThat(modelUpdated.getDependencies()).hasSize(7);
            assertThat(modelUpdated.getRepositories()).hasSize(2);
            assertThat(modelUpdated.getPluginRepositories()).hasSize(2);
            for(Dependency dep: modelUpdated.getDependencies()){
                assertThat(dep.getVersion()).isNotNull();
                if(dep.getGroupId().equals("org.group") && dep.getArtifactId().equals("workitems")){
                    assertThat(dep.getVersion()).isEqualTo("1.0.0-FINAL");
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AssertionError(e.getMessage());
        }
    }
}
