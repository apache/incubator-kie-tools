/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.migration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.tools.ant.ExitException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.fields.test.TestMetaDataEntryManager;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FieldSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormDefinitionSerializerImpl;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormModelSerializer;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class MigrationToolTest {

    private static final File NIOGIT_DIR = Paths.get("target/niogit")
            .toFile();

    private static final String
            GIT_SUFFIX = ".git",
            PROJECT_A1 = "projectA1",
            PROJECT_B1 = "projectB1",
            PROJECT_B2 = "projectB2",
            PROJECT_B3 = "projectB3",
            PROJECT_WITH_HISTORY_1 = "projectWithHistory1",
            PROJECT_WITH_HISTORY_2 = "projectWithHistory2",
            PROJECT_WITH_FORMS = "projectWithForms",
            SPACE_A = "spaceA",
            SPACE_B = "spaceB",
            SPACE_C = "spaceC",
            SPACE_SYSTEM = "system",
            SPACE_EXAMPLE = "example";

    private static final FormDefinitionSerializer FORM_SERIALIZER = new FormDefinitionSerializerImpl(
            new FieldSerializer(),
            new FormModelSerializer(),
            new TestMetaDataEntryManager());

    private static SecurityManager originalSecurityManager;

    private static class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission perm) {
        }

        @Override
        public void checkPermission(Permission perm, Object context) {
        }

        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new ExitException(status);
        }
    }

    @BeforeClass
    public static void runToolAndCloneProjectRepos() throws GitAPIException {
        originalSecurityManager = System.getSecurityManager();
        System.setSecurityManager(new NoExitSecurityManager());

        try {
            runMigrationTool();
        } catch (ExitException e ) {
            assertThat(e.getStatus()).isEqualTo(0);
        } finally {
            System.setSecurityManager(originalSecurityManager);
        }

        cloneAllProjectRepos();
    }

    @Test
    public void testNewProjectStructure() {
        final String[] actualSpaceDirs = NIOGIT_DIR.list();

        assertThat(actualSpaceDirs).containsExactlyInAnyOrder(SPACE_A, SPACE_B, SPACE_C, SPACE_EXAMPLE, SPACE_SYSTEM);

        assertThat(getProjectRepos(SPACE_SYSTEM)).containsExactlyInAnyOrder("metadata.git", "plugins.git", "datasets.git", "preferences.git", "system.git");
        assertThat(getProjectRepos(SPACE_A)).containsExactly(PROJECT_A1 + GIT_SUFFIX);
        assertThat(getProjectRepos(SPACE_B)).containsExactlyInAnyOrder(
                PROJECT_B1 + GIT_SUFFIX,
                PROJECT_B2 + GIT_SUFFIX,
                PROJECT_B3 + GIT_SUFFIX,
                PROJECT_WITH_HISTORY_1 + GIT_SUFFIX,
                PROJECT_WITH_HISTORY_2 + GIT_SUFFIX,
                PROJECT_WITH_FORMS + GIT_SUFFIX);
        assertThat(getProjectRepos(SPACE_C)).isEmpty();

        checkProjectFiles(SPACE_A, PROJECT_A1);
        checkProjectFiles(SPACE_B, PROJECT_B1);
        checkProjectFiles(SPACE_B, PROJECT_B2);
        checkProjectFiles(SPACE_B, PROJECT_B3);
    }

    @Test
    public void testNewPomStructure() throws IOException, XmlPullParserException {
        final File
                projectDir = getProjectDir(SPACE_B, PROJECT_B2),
                pom = new File(projectDir, "pom.xml");
        final MavenXpp3Reader reader = new MavenXpp3Reader();
        final Model model = reader.read(new FileInputStream(pom));

        assertThat(model.getVersion()).isEqualTo("1.2.3");
        assertThat(model.getDependencies()).hasSize(6);
        assertThat(model.getBuild().getPlugins()).hasSize(1);
        assertThat(model.getPackaging()).isEqualTo("kjar");
    }

    @Test
    public void testProjectGitHistory() throws IOException, GitAPIException {
        final int
                latestCommit = 0,
                previousCommit = 1;

        final String
                javaComment = "// ",
                firstCommitMsg = "first",
                thirdCommitMsg = "third",
                fourthCommitMsg = "fourth",
                fifthCommitMsg = "fifth",
                drl1Location = "src/main/resources/bxms/projectwithhistory1/drlWithGitHistory1.drl",
                drl2Location = "src/main/resources/bxms/projectwithhistory2/drlWithGitHistory2.drl";

        final File
                projectWithHistory1 = getProjectDir(SPACE_B, PROJECT_WITH_HISTORY_1),
                projectWithHistory2 = getProjectDir(SPACE_B, PROJECT_WITH_HISTORY_2),
                drl1 = new File(projectWithHistory1, drl1Location),
                drl2 = new File(projectWithHistory2, drl2Location);

        final Git
                repoFirst = Git.open(projectWithHistory1),
                repoSecond = Git.open(projectWithHistory2);

        final List<String>
                commitMessagesDrl1 = getDrlCommitMessages(repoFirst, drl1Location),
                commitMessagesDrl2 = getDrlCommitMessages(repoSecond, drl2Location);

        assertThat(commitMessagesDrl1).hasSize(4);
        assertThat(commitMessagesDrl2).hasSize(3);

        assertThat(commitMessagesDrl1.get(latestCommit)).startsWith(fifthCommitMsg);
        assertThat(commitMessagesDrl1.get(previousCommit)).startsWith(fourthCommitMsg);
        assertThat(commitMessagesDrl2.get(latestCommit)).startsWith(thirdCommitMsg);
        assertThat(commitMessagesDrl2.get(previousCommit)).startsWith(firstCommitMsg);

        assertThat(getDrlLineWithContent(drl1)).isEqualTo(javaComment + fifthCommitMsg);
        assertThat(getDrlLineWithContent(drl2)).isEqualTo(javaComment + thirdCommitMsg);

        resetRepoToPreviousDrlCommit(repoFirst);
        resetRepoToPreviousDrlCommit(repoSecond);

        assertThat(getDrlLineWithContent(drl1)).isEqualTo(javaComment + fourthCommitMsg);
        assertThat(getDrlLineWithContent(drl2)).isEqualTo(javaComment + firstCommitMsg);
    }

    @Test
    public void testTaskForm() throws IOException {
        final FormDefinition taskFormDef = getFormFromResources("taskWithDifferentIO-taskform");

        checkFields(
                taskFormDef,
                new HashMap<String, List<String>>() {{
                    put("boolean", Arrays.asList("_boolean (boolean)", "CheckBox", "_boolean"));
                    put("cv", Arrays.asList("_cv (cv)", "Document", "_cv"));
                    put("float", Arrays.asList("_float (float)", "DecimalBox", "_float"));
                    put("integer", Arrays.asList("_integer (integer)", "IntegerBox", "_integer"));
                    put("string", Arrays.asList("_string (string)", "TextBox", "_string"));
                    put("person", Arrays.asList("person", "SubForm", "_person"));
                }});

        assertThat(((TaskFormModel) taskFormDef.getModel()).getTaskName()).isEqualTo("taskWithDifferentIO");
    }

    @Test
    public void testPersonForm() throws IOException {
        final FormDefinition personFormDef = getFormFromResources("PersonForm");
        final LayoutTemplate layout = personFormDef.getLayoutTemplate();

        checkFields(
                personFormDef,
                new HashMap<String, List<String>>() {{
                    put("person_address", Arrays.asList("address (person)", "SubForm", "address"));
                    put("person_addressList", Arrays.asList("addressList (person)", "MultipleSubForm", "addressList"));
                    put("person_age", Arrays.asList("age (person)", "IntegerBox", "age"));
                    put("person_birthdate", Arrays.asList("birthdate (person)", "DatePicker", "birthdate"));
                    put("person_married", Arrays.asList("married (person)", "CheckBox", "married"));
                    put("person_name", Arrays.asList("name (person)", "TextBox", "name"));
                    put("person_salary", Arrays.asList("salary (person)", "DecimalBox", "salary"));
                    put("person_sex", Arrays.asList("sex (person)", "TextBox", "sex"));
                }});

        assertThat(layout.getRows()).hasSize(4);
        assertThat(layout.getRows().get(0).getLayoutColumns()).hasSize(2);
        assertThat(personFormDef.getFieldByName("person_salary").getHelpMessage()).isEqualTo("Please enter your salary in dollars.");
        assertThat(personFormDef.getFieldByName("person_birthdate").getRequired()).isTrue();
        assertThat(personFormDef.getFieldByName("person_married").getReadOnly()).isTrue();
        assertThat(((DataObjectFormModel) personFormDef.getModel()).getType()).isEqualTo("bxms.projectWithForms.Person");
    }

    @Test
    public void testProcessForm() throws IOException {
        final FormDefinition processFormDef = getFormFromResources("formmodeler-migration.UpdateUserProfile-taskform");

        checkFields(processFormDef,
                    new HashMap<String, List<String>>() {{
                        put("boolean", Arrays.asList("boolean (boolean)", "CheckBox", "boolean"));
                        put("cv", Arrays.asList("cv (cv)", "Document", "cv"));
                        put("float", Arrays.asList("float (float)", "DecimalBox", "float"));
                        put("integer", Arrays.asList("integer (integer)", "IntegerBox", "integer"));
                        put("string", Arrays.asList("string (string)", "TextBox", "string"));
                        put("person", Arrays.asList("person", "SubForm", "person"));
                    }});

        assertThat(((BusinessProcessFormModel) processFormDef.getModel()).getProcessName()).isEqualTo("UpdateUserProfile");
    }

    @Test
    public void testHelperSubForms() throws IOException {
        final File helperFormsDir = getFile(SPACE_B, PROJECT_WITH_FORMS, "src/main/resources/bxms/projectWithForms/");
        final List<String> helperFormActualFields = getFormFromResources("taskWithSameIO-taskform-person")
                .getFields()
                .stream()
                .map(FieldDefinition::getName)
                .collect(Collectors.toList());

        assertThat(helperFormsDir.list()).contains(
                "formmodeler-migration.UpdateUserProfile-taskform-person.frm",
                "taskWithDifferentIO-taskform-person.frm",
                "taskWithSameIO-taskform-person.frm");
        assertThat(helperFormActualFields).containsExactlyInAnyOrder(
                "person_address",
                "person_addressList",
                "person_birthdate",
                "person_married",
                "person_salary",
                "person_sex");
    }

    private void checkProjectFiles(final String spaceName, final String projectName) {
        final String
                assetId = projectName.substring(projectName.length() - 2),
                projectNamePkg = projectName.toLowerCase(),
                assetIdResource = assetId.toLowerCase(),
                drlPath = String.format("/src/main/resources/bxms/%s/drl%s.drl", projectNamePkg, assetIdResource),
                processPath = String.format("/src/main/resources/bxms/%s/process%s.bpmn2", projectNamePkg, assetIdResource),
                dataObjectPath = String.format("/src/main/java/bxms/%s/Object%s.java", projectNamePkg, assetId);

        final File
                drl = getFile(spaceName, projectName, drlPath),
                process = getFile(spaceName, projectName, processPath),
                dataObject = getFile(spaceName, projectName, dataObjectPath);

        assertThat(drl).exists();
        assertThat(process).exists();
        assertThat(dataObject).exists();
    }

    private void resetRepoToPreviousDrlCommit(final Git projectRepo) throws GitAPIException {
        final String previousDrlCommitRef = "HEAD~2";

        projectRepo.reset()
                .setMode(ResetCommand.ResetType.HARD)
                .setRef(previousDrlCommitRef)
                .call();
    }

    private void checkFields(final FormDefinition form, final Map<String, List<String>> expectedFields) {
        final Map<String, List<String>> actualFields = form.getFields()
                .stream()
                .collect(Collectors.toMap(
                        FieldDefinition::getName,
                        f -> Arrays.asList(f.getLabel(),
                                           f.getFieldType().getTypeName(),
                                           f.getBinding())));

        assertThat(actualFields).isEqualTo(expectedFields);
    }

    private List<String> getDrlCommitMessages(final Git repo, final String drlLocation) throws GitAPIException {
        final Iterable<RevCommit> projectCommits = repo.log()
                .addPath(drlLocation)
                .call();

        final List<String> commitMessages = new ArrayList<>();

        for (RevCommit c : projectCommits) {
            commitMessages.add(c.getFullMessage());
        }

        return commitMessages;
    }

    private String getDrlLineWithContent(final File drlWithGitHistory) throws IOException {
        final int lineWithContent = 2;

        return FileUtils.readLines(drlWithGitHistory, Charset.defaultCharset())
                .get(lineWithContent);
    }

    private FormDefinition getFormFromResources(final String formName) throws IOException {
        final String formLocation = String.format("src/main/resources/bxms/%s/%s.frm", PROJECT_WITH_FORMS, formName);
        final File form = getFile(SPACE_B, PROJECT_WITH_FORMS, formLocation);
        final String formContent = FileUtils.readFileToString(form, Charset.defaultCharset());

        return FORM_SERIALIZER.deserialize(formContent);
    }

    private static void runMigrationTool() {
        String[] args = {"-b", "-t", NIOGIT_DIR.getAbsolutePath()};
        Main.main(args);
    }

    private static void cloneAllProjectRepos() throws GitAPIException {
        cloneProjectRepo(SPACE_A, PROJECT_A1);
        cloneProjectRepo(SPACE_B, PROJECT_B1);
        cloneProjectRepo(SPACE_B, PROJECT_B2);
        cloneProjectRepo(SPACE_B, PROJECT_B3);
        cloneProjectRepo(SPACE_B, PROJECT_WITH_FORMS);
        cloneProjectRepo(SPACE_B, PROJECT_WITH_HISTORY_1);
        cloneProjectRepo(SPACE_B, PROJECT_WITH_HISTORY_2);
    }

    private static void cloneProjectRepo(final String spaceName, final String projectName) throws GitAPIException {
        final File projectDir = getProjectDir(spaceName, projectName);
        final String projectRepoDir = getProjectRepoDir(spaceName, projectName).getAbsolutePath();
        Git.cloneRepository()
                .setURI(projectRepoDir)
                .setDirectory(projectDir)
                .call();
    }

    private static List<String> getProjectRepos(final String spaceName) {
        String[] spaceFiles = new File(NIOGIT_DIR, spaceName)
                .list();
        return Arrays.stream(spaceFiles)
                .filter(f -> f.endsWith(GIT_SUFFIX))
                .collect(Collectors.toList());
    }

    private static File getProjectRepoDir(final String spaceName, final String projectName) {
        final File spaceDir = getSpaceDir(spaceName);
        return new File(spaceDir, projectName + GIT_SUFFIX);
    }

    private static File getProjectDir(final String spaceName, final String projectName) {
        final File spaceDir = getSpaceDir(spaceName);
        return new File(spaceDir, projectName);
    }

    private static File getSpaceDir(final String spaceName) {
        return new File(NIOGIT_DIR, spaceName);
    }

    private static File getFile(final String spaceName, final String projectName, final String fileLocation) {
        final File projectDir = getProjectDir(spaceName, projectName);
        return new File(projectDir, fileLocation);
    }
}
