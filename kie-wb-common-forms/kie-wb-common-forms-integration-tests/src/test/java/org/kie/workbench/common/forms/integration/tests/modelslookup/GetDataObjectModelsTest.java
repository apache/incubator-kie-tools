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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.service.shared.ModelFinderService;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class GetDataObjectModelsTest extends AbstractGetModelsTest {

    private static DataModelerService dataModelerService;
    private static ModelFinderService modelFinderService;

    private static final String
            DO_PACKAGE_PATH = "/src/main/java/com/myteam/modelslookup/",
            DO_PATH_FORMAT = PROJECT_ROOT + DO_PACKAGE_PATH + "%s.java";

    private static final String
            ORDER = "Order",
            ORDER_COPY = "OrderCopy",
            ORDER_RENAMED = "OrderRenamed",
            ORDER_RENAMED_FQN = DO_PACKAGE + ORDER_RENAMED,
            ORDER_COPY_FQN = DO_PACKAGE + ORDER_COPY,
            CREATED_DO = "CreatedDataObject",
            CREATED_DO_FQN = DO_PACKAGE + CREATED_DO;

    private static final Set<String>
            ADDRESS_FIELDS = new HashSet<>(Arrays.asList("street", "number", "city", "zip")),
            ORDER_FIELDS = new HashSet<>(Collections.singletonList("listOfItems")),
            PERSON_FIELDS = new HashSet<>(Arrays.asList("name", "address", "salary", "married")),
            ITEM_FIELDS = new HashSet<>(Arrays.asList("id", "name", "price"));

    private static Map<String, Set<String>> expectedModel = new HashMap<String, Set<String>>() {{
        put(ADDRESS_FQN, ADDRESS_FIELDS);
        put(PERSON_FQN, PERSON_FIELDS);
        put(ITEM_FQN, ITEM_FIELDS);
        put(ORDER_FQN, ORDER_FIELDS);
    }};

    @BeforeClass
    public static void setup() throws Exception {
        dataModelerService = weldContainer.select(DataModelerService.class).get();

        modelFinderService = weldContainer.select(ModelFinderService.class).get();
    }

    @Test
    public void testGetModelsAfterCopyRenameCreateAndDelete() throws URISyntaxException, IOException {
        assertOriginalModel();
        assertModelsAfterCopy();
        assertModelsAfterRename();
        assertModelsAfterCreate();
        assertModelsAfterDelete();
    }

    private void assertOriginalModel() {
        assertExpectedLoaded(expectedModel);
    }

    private void assertModelsAfterCopy() throws IOException, URISyntaxException {
        copyDO(ORDER, ORDER_COPY);
        expectedModel.put(ORDER_COPY_FQN, ORDER_FIELDS);
        assertExpectedLoaded(expectedModel);
    }

    private void assertModelsAfterRename() throws IOException, URISyntaxException {
        renameDO(ORDER_COPY, ORDER_RENAMED);
        expectedModel.remove(ORDER_COPY_FQN);
        expectedModel.put(ORDER_RENAMED_FQN, ORDER_FIELDS);
        assertExpectedLoaded(expectedModel);
    }

    private void assertModelsAfterCreate() throws URISyntaxException {
        createDO(CREATED_DO);
        expectedModel.put(CREATED_DO_FQN, Collections.emptySet());
        assertExpectedLoaded(expectedModel);
    }

    private void assertModelsAfterDelete() throws IOException, URISyntaxException {
        deleteDO(CREATED_DO);
        deleteDO(ORDER_RENAMED);
        expectedModel.remove(CREATED_DO_FQN);
        expectedModel.remove(ORDER_RENAMED_FQN);
        assertExpectedLoaded(expectedModel);
    }

    private void assertExpectedLoaded(Map<String, Set<String>> expectedDataObjects) {
        final Collection<DataObjectFormModel> dataObjects = modelFinderService.getAllModels(rootPath);
        final Map<String, Set<String>> actualDataObjects = getDataObjectsMap(dataObjects);
        assertThat(actualDataObjects).isEqualTo(expectedDataObjects);
    }

    private void renameDO(String oldName, String newName) throws URISyntaxException, IOException {
        final java.nio.file.Path targetPath = renameResource(getDOPath(oldName), newName + ".java");
        refactorClass(targetPath.toFile(), oldName, newName);
        File[] files = targetPath.getParent().toFile().listFiles();
        refactorReferencesInOtherClasses(files, oldName, newName);
        clearCache();
    }

    private void copyDO(String source, String newName) throws URISyntaxException, IOException {
        final java.nio.file.Path targetPath = copyResource(String.format(DO_PATH_FORMAT, source), newName + ".java");
        refactorClass(targetPath.toFile(), source, newName);
        clearCache();
    }

    private void deleteDO(String dataObject) throws URISyntaxException, IOException {
        deleteResource(String.format(DO_PATH_FORMAT, dataObject));
    }

    private void createDO(String name) throws URISyntaxException {
        Path dataObjectPath = PathFactory.newPath(name + ".java",
                                                  "file://" + ROOT_URL.toURI().getPath() + DO_PACKAGE_PATH);
        dataModelerService.createJavaFile(dataObjectPath, name + ".java", "comment");
        clearCache();
    }

    private void refactorClass(File file, String oldName, String newName) throws IOException {
        String fileContent = FileUtils.readFileToString(file, Charset.defaultCharset());
        //update class name and constructors
        fileContent = replaceAllSurroundedBy(fileContent, Arrays.asList(" class ", " "), Arrays.asList(" ", "("), oldName, newName);
        FileUtils.write(file, fileContent, Charset.defaultCharset());
    }

    private void refactorReferencesInOtherClasses(File[] files, String oldFieldType, String newFieldType) throws IOException {
        final String fieldTypeFQN = DO_PACKAGE + oldFieldType;
        for (File file : files) {
            if (!isCandidateForRefactoring(oldFieldType, newFieldType, file)) {
                continue;
            }
            String fileContent = FileUtils.readFileToString(file, Charset.defaultCharset());
            if (needsRefactoring(fieldTypeFQN, fileContent)) {
                fileContent = refactorFields(oldFieldType, newFieldType, fileContent);
                FileUtils.write(file, fileContent, Charset.defaultCharset());
            }
        }
    }

    private boolean needsRefactoring(String fieldTypeFQN, String fileContent) {
        return fileContent.contains(fieldTypeFQN);
    }

    private boolean isCandidateForRefactoring(String oldClassName, String newClassName, File file) {
        final String fileName = file.getName();
        final String sourceFileName = oldClassName + ".java";
        final String targetFileName = newClassName + ".java";
        return !Objects.equals(fileName, sourceFileName) && !Objects.equals(fileName, targetFileName);
    }

    private String refactorFields(String oldFieldType, String newFieldType, String fileContent) {
        final String oldFieldName = StringUtils.uncapitalize(oldFieldType);
        final String newFieldName = StringUtils.uncapitalize(newFieldType);
        //replace field types, strings, getters and setters
        String updatedFileContent = replaceAllSurroundedBy(fileContent, Arrays.asList(DO_PACKAGE, "\"", "get", "set"), Arrays.asList("", "\"", "(", "("), oldFieldType, newFieldType);
        //replace field declarations, method parameters and inner field references
        updatedFileContent = replaceAllSurroundedBy(updatedFileContent, Arrays.asList(" ", " ", " ", ".", ".", "."), Arrays.asList(";", ",", ")", " ", ";", ","), oldFieldName, newFieldName);
        return updatedFileContent;
    }

    private String replaceAllSurroundedBy(String fileContent, List<String> prefixes, List<String> suffixes, String oldName, String newName) {
        String updatedFileContent = fileContent;
        for (int i = 0; i < prefixes.size(); i++) {
            String prefix = prefixes.get(i);
            String suffix = suffixes.get(i);
            String regexSuffix = suffix;
            if (")".equals(suffix) || "(".equals(suffix)) {
                regexSuffix = "[" + suffix + "]";
            }
            updatedFileContent = updatedFileContent.replaceAll(prefix + oldName + regexSuffix, prefix + newName + suffix);
        }
        return updatedFileContent;
    }

    private String getDOPath(String dataObject) {
        return String.format(DO_PATH_FORMAT, dataObject);
    }

    private Map<String, Set<String>> getDataObjectsMap(Collection<DataObjectFormModel> dataObjects) {
        return dataObjects.stream().collect(Collectors.toMap(
                DataObjectFormModel::getClassName,
                getModelPropertyNameSet()
        ));
    }

    private static Function<DataObjectFormModel, Set<String>> getModelPropertyNameSet() {
        return d -> d.getProperties().stream().map(ModelProperty::getName).collect(Collectors.toSet());
    }
}
