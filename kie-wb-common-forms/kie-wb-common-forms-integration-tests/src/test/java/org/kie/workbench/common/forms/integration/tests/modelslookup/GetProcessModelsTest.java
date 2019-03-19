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

package org.kie.workbench.common.forms.integration.tests.modelslookup;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMProcessModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.impl.BPMFinderServiceImpl;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GetProcessModelsTest extends AbstractGetModelsTest {

    private static final String
            ORDER_COPY = "order-copy",
            ORDER = "order",
            NEW_EMPLOYEE = "new-employee",
            DOCUMENT_FQN = "org.jbpm.document.Document",
            DEFINITIONS_PACKAGE = "/src/main/resources/com/myteam/modelslookup/";

    private static final Map<String, String> ORDER_VARIABLES = new HashMap<String, String>() {{
        put("name", String.class.getTypeName());
        put("password", String.class.getTypeName());
        put("item", ITEM_FQN);
        put("pick", Boolean.class.getTypeName());
        put("order", ORDER_FQN);
        put("address", ADDRESS_FQN);
    }};

    private static final Map<String, String> NEW_EMPLOYEE_VARIABLES = new HashMap<String, String>() {{
        put("cv", DOCUMENT_FQN);
        put("passedInterview", Boolean.class.getTypeName());
    }};

    private static final Map<String, Map<String, String>> ORDER_TASK_MODELS = new HashMap<String, Map<String, String>>() {{
        put("login", new HashMap<String, String>() {{
            put("_name", String.class.getTypeName());
            put("_password", String.class.getTypeName());
        }});
        put("pick_items", Collections.singletonMap("_order", ORDER_FQN));
        put("checkout", new HashMap<String, String>() {{
            put("_address", ADDRESS_FQN);
            put("order_", ORDER_FQN);
        }});
    }};
    private static final Map<String, Map<String, String>> NEW_EMPLOYEE_TASK_MODELS = new HashMap<String, Map<String, String>>() {{
        put("read_cv", Collections.singletonMap("cv_", DOCUMENT_FQN));
        put("stalk_on_linkedin", Collections.emptyMap());
        put("interview", Collections.singletonMap("_passedInterview", Boolean.class.getTypeName()));
        put("hire", Collections.emptyMap());
    }};

    private static BPMFinderServiceImpl finderService;
    private final String ORDER_RENAMED = "order-renamed";

    private List<JBPMProcessModel> availableProcessModels;

    @Before
    public void init() {
        finderService = weldContainer.select(BPMFinderServiceImpl.class).get();
    }

    @Test
    public void testGetModelsAfterCopyRenameAndDelete() throws IOException, URISyntaxException {
        assertOriginalState();

        //copying process changes it's id and asset name, but not process name
        copyProcess(ORDER, ORDER_COPY);
        assertModelsAfterCopy();

        //renaming the process changes only the asset name, but keeps process name and process id
        renameProcess(ORDER_COPY, ORDER_RENAMED);
        assertModelsAfterCopy();

        //this method changes the process name (in UI has to be done in process properties)
        changeProcessName(ORDER_RENAMED, ORDER, ORDER_COPY);
        assertModelsAfterProcessRename();

        deleteProcess(ORDER_RENAMED);
        assertOriginalState();
    }

    private void assertOriginalState() {
        availableProcessModels = finderService.getAvailableProcessModels(rootPath);
        assertThat(availableProcessModels).hasSize(2);
        assertModelWithName(ORDER, ORDER_VARIABLES, ORDER_TASK_MODELS);
        assertModelWithName(NEW_EMPLOYEE, NEW_EMPLOYEE_VARIABLES, NEW_EMPLOYEE_TASK_MODELS);
    }

    private void assertModelsAfterProcessRename() throws IOException {
        availableProcessModels = finderService.getAvailableProcessModels(rootPath);
        assertThat(availableProcessModels).hasSize(3);
        assertModelWithName(NEW_EMPLOYEE, NEW_EMPLOYEE_VARIABLES, NEW_EMPLOYEE_TASK_MODELS);
        assertModelWithName(ORDER_RENAMED, ORDER_VARIABLES, ORDER_TASK_MODELS);
        assertModelWithName(ORDER, ORDER_VARIABLES, ORDER_TASK_MODELS);
    }

    private void assertModelsAfterCopy() {
        availableProcessModels = finderService.getAvailableProcessModels(rootPath);
        assertThat(availableProcessModels).hasSize(3);
        //there are two models with process name order (because asset rename doesn't change process name)
        assertModelWithName(ORDER, ORDER_VARIABLES, ORDER_TASK_MODELS);
        assertModelWithName(NEW_EMPLOYEE, NEW_EMPLOYEE_VARIABLES, NEW_EMPLOYEE_TASK_MODELS);
    }

    private void assertModelWithName(String process, Map<String, String> variables, Map<String, Map<String, String>> tasks) {
        for (JBPMProcessModel model : availableProcessModels) {
            if (process.equals(model.getProcessFormModel().getProcessName())) {
                assertThat(getProcessVariables(model)).isEqualTo(variables);
                assertThat(getTaskModels(model)).isEqualTo(tasks);
            }
        }
    }

    private Map<String, String> getProcessVariables(JBPMProcessModel model) {
        return model.getProcessFormModel().getProperties().stream().collect(Collectors.toMap(
                ModelProperty::getName,
                p -> p.getTypeInfo().getClassName()
        ));
    }

    private Map<String, Map<String, String>> getTaskModels(JBPMProcessModel model) {
        return model.getTaskFormModels().stream().collect(Collectors.toMap(
                TaskFormModel::getTaskName,
                getTaskVariables()
        ));
    }

    private static Function<TaskFormModel, Map<String, String>> getTaskVariables() {
        return t -> t.getProperties().stream().collect(Collectors.toMap(
                ModelProperty::getName,
                p -> p.getTypeInfo().getClassName()
        ));
    }

    private void copyProcess(String oldName, String newName) throws IOException {
        copyResource(getProcessPath(oldName), newName + ".bpmn2");
        changeProcessId(newName, "src.order", "src." + newName);
        clearCache();
    }

    private void deleteProcess(String process) throws IOException, URISyntaxException {
        deleteResource(getProcessPath(process));
    }

    private String getProcessPath(String process) {
        return String.format("project" + DEFINITIONS_PACKAGE + "%s.bpmn2", process);
    }

    private void changeProcessName(String process, String oldName, String newName) throws IOException {
        setProcessAttribute(process, "name", oldName, newName);
    }

    private void renameProcess(String oldName, String newName) throws IOException {
        renameResource(getProcessPath(oldName), newName + ".bpmn2");
    }

    private void changeProcessId(String process, String oldId, String newId) throws IOException {
        final File file = getNioPath(getProcessPath(process)).toFile();
        String fileContent = FileUtils.readFileToString(file, Charset.defaultCharset());
        FileUtils.write(file, fileContent.replaceAll(oldId, newId), Charset.defaultCharset());
    }

    private void setProcessAttribute(String process, String attribute, String oldValue, String newValue) throws IOException {
        final File file = getNioPath(getProcessPath(process)).toFile();
        String fileContent = FileUtils.readFileToString(file, Charset.defaultCharset());
        final int processNodeIndex = fileContent.indexOf("<bpmn2:process");
        final String nameAttribute = " " + attribute + "=";
        final int nameAttributeIndex = fileContent.indexOf(nameAttribute, processNodeIndex);
        final int nameIndex = nameAttributeIndex + nameAttribute.length();
        final int nameEndIndex = nameIndex + oldValue.length() + 2;
        fileContent = fileContent.substring(0, nameIndex) + "\"" + newValue + "\"" + fileContent.substring(nameEndIndex);
        FileUtils.write(file, fileContent, Charset.defaultCharset());
    }
}
