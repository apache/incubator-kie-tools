/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.model.EditorModelContent;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.uberfire.backend.vfs.Path;

/**
 * Data modeler context shared between the different widgets that composes the editor.
 */
public class DataModelerContext {

    private DataModelHelper helper;

    private Map<String, AnnotationDefinition> annotationDefinitions = new HashMap<String, AnnotationDefinition>();

    private List<PropertyType> baseTypes = new ArrayList<PropertyType>();

    private boolean readonly = false;

    private Set<String> currentProjectPackages = new HashSet<String>();

    private EditorModelContent editorModelContent;

    private ObjectProperty objectProperty;

    private String contextId;

    /**
     * Status relative to the edition tabs. This is a kind of sub status, that tells us if there are pending changes
     * in whatever of the edition tabs.
     * It may happen that there are no pending changes in the edition tabs but the .java file is dirty.
     * e.g. If you make some changes in the "Editor" tab and goes to the "Source" tab, the code will be automatically
     * re generated to include the changes. After the code is re regenerated the editionStatus will be changed from
     * EDITOR_CHANGED to NO_CHANGES. This means that the "Source" tab is synchronized with the "Editor" tab, but the
     * .java file still needs to be saved. And thus the editor as the main component is dirty.
     */
    public enum EditionStatus {

        /**
         * No pending changes to process.
         */
        NO_CHANGES,

        /**
         * Changes has been done in "Editor" tab and the code wasn't regenerated yet.
         */
        EDITOR_CHANGED,

        /**
         * Changes has been done in the "Source" tab and the data object wasn't regenerated (parsed) yet.
         */
        SOURCE_CHANGED
    }

    public enum ParseStatus {

        /**
         * The source has been parsed without errors, so we basically have the model built to be shown in the editor tab.
         */
        PARSED,

        /**
         * The source is not parsed. This not necessary means that there are parse errors, for example when the
         * user is changing the code in the source tab the parse status will be NOT_PARSED. It means that if the
         * user wants to open the "Editor" tab the code needs to be parsed.
         */
        NOT_PARSED,

        /**
         * There have been parsing errors in the last parse try. e.g when the file was loaded at editor opening time, or
         * when the user tried to switch from the "source tab" to the "editor tab".
         */
        PARSE_ERRORS
    }

    /**
     * States the edition mode that is working at this moment.
     *
     */
    public enum EditionMode {

        /**
         * The source editor is editing the code.
         */
        SOURCE_MODE,

        /**
         * The graphical editor is editing the content.
         */
        GRAPHICAL_MODE
    }

    private EditionStatus editionStatus = EditionStatus.NO_CHANGES;

    private ParseStatus parseStatus = ParseStatus.NOT_PARSED;

    private EditionMode editionMode = EditionMode.GRAPHICAL_MODE;

    public DataModelerContext() {
    }

    public DataModelerContext(String contextId) {
        this.contextId = contextId;
        helper = new DataModelHelper(contextId);
    }

    public void init(List<PropertyType> baseTypes) {
        this.baseTypes = baseTypes;
        helper.setBaseTypes(baseTypes);
    }

    public DataModel getDataModel() {
        return editorModelContent != null ? editorModelContent.getDataModel() : null;
    }

    public DataModelHelper getHelper() {
        return helper;
    }

    public Map<String, AnnotationDefinition> getAnnotationDefinitions() {
        return annotationDefinitions;
    }

    public void setAnnotationDefinitions(Map<String, AnnotationDefinition> annotationDefinitions) {
        this.annotationDefinitions = annotationDefinitions;
    }

    public AnnotationDefinition getAnnotationDefinition(String className) {
        return getAnnotationDefinitions().get(className);
    }

    public List<PropertyType> getBaseTypes() {
        return baseTypes;
    }

    public ParseStatus getParseStatus() {
        return parseStatus;
    }

    public void setParseStatus(ParseStatus parseStatus) {
        this.parseStatus = parseStatus;
    }

    public boolean isParsed() {
        return getParseStatus() == ParseStatus.PARSED;
    }

    public boolean isNotParsed() {
        return getParseStatus() == ParseStatus.NOT_PARSED;
    }

    public boolean isParseErrors() {
        return getParseStatus() == ParseStatus.PARSE_ERRORS;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public boolean isEditorChanged() {
        return editionStatus == EditionStatus.EDITOR_CHANGED;
    }

    public boolean isSourceChanged() {
        return editionStatus == EditionStatus.SOURCE_CHANGED;
    }

    public void appendPackages(Collection<String> packages) {
        if (packages != null) {
            for (String packageToAppend : packages) {
                if (!"".equals(packageToAppend) && !currentProjectPackages.contains(packageToAppend)) {
                    currentProjectPackages.add(packageToAppend);
                }
            }
        }
    }

    public void appendPackage(String packageName) {

        if (packageName != null && !"".equals(packageName)) {
            String[] subPackages = DataModelerUtils.calculateSubPackages(packageName);
            String subPackage = null;
            for (int i = 0; subPackages != null && i < subPackages.length; i++) {
                subPackage = subPackages[i];
                if (!currentProjectPackages.contains(subPackage)) {
                    currentProjectPackages.add(subPackage);
                }
            }
        }
    }

    public Set<String> getCurrentProjectPackages() {
        return currentProjectPackages;
    }

    public void cleanPackages() {
        if (currentProjectPackages != null) {
            currentProjectPackages.clear();
        }
    }

    public boolean isDataObjectLoaded() {
        return getDataObject() != null;
    }

    public KieModule getCurrentProject() {
        if (editorModelContent != null) {
            return editorModelContent.getCurrentModule();
        }
        return null;
    }

    public DataObject getDataObject() {
        if (editorModelContent != null) {
            return editorModelContent.getDataObject();
        }
        return null;
    }

    public void setDataObject(DataObject dataObject) {
        if (editorModelContent != null) {
            editorModelContent.setDataObject(dataObject);
        }
    }

    public ObjectProperty getObjectProperty() {
        return objectProperty;
    }

    public void setObjectProperty(ObjectProperty objectProperty) {
        this.objectProperty = objectProperty;
    }

    public Path getDataObjectPath(String className) {
        return (editorModelContent != null && editorModelContent.getDataObjectPaths() != null) ? editorModelContent.getDataObjectPaths().get(className) : null;
    }

    public EditionStatus getEditionStatus() {
        return editionStatus;
    }

    public void setEditionStatus(EditionStatus editionStatus) {
        this.editionStatus = editionStatus;
    }

    public EditionMode getEditionMode() {
        return editionMode;
    }

    public void setEditionMode(EditionMode editionMode) {
        this.editionMode = editionMode;
    }

    public EditorModelContent getEditorModelContent() {
        return editorModelContent;
    }

    public void setEditorModelContent(EditorModelContent editorModelContent) {
        this.editorModelContent = editorModelContent;
        if (editorModelContent.getDataModel() != null) {
            //TODO, likely this helper is no longer needed.
            helper.setDataModel(editorModelContent.getDataModel());
        }
        cleanPackages();
        appendPackages(editorModelContent.getCurrentModulePackages());
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public void clear() {
        annotationDefinitions = null;
        baseTypes = null;
        if (getDataModel() != null && getDataModel().getDataObjects() != null) {
            getDataModel().getDataObjects().clear();
        }
        cleanPackages();
        helper = new DataModelHelper(contextId);
    }
}
