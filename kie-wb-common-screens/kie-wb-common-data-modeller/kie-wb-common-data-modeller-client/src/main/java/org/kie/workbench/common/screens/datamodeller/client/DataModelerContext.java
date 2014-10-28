/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.EditorModelContent;
import org.kie.workbench.common.screens.datamodeller.model.PropertyTypeTO;

/**
 * Data modeler context shared between the different widgets that composes the editor.
 */
public class DataModelerContext {

    private DataModelHelper helper;

    private Map<String, AnnotationDefinitionTO> annotationDefinitions;

    private List<PropertyTypeTO> baseTypes;

    private boolean dirty = false;

    private boolean readonly = false;

    private List<String> currentProjectPackages = new ArrayList<String>();

    private EditorModelContent editorModelContent;

    /**
     * Status relative to the edition tabs. This is a kind of sub status, that tells us if there are pending changes
     * in whatever of the edition tabs.
     * It may happen that there are no pending changes in the edition tabs but the .java file is dirty.
     * e.g. If you make some changes in the "Editor" tab and goes to the "Source" tab, the code will be automatically
     * re generated to include the changes. After the code is re regenerated the editionStatus will be changed from
     * EDITOR_CHANGED to NO_CHANGES. This means that the "Source" tab is synchronized with the "Editor" tab, but the
     * .java file still needs to be saved. And this the editor as the mail component is dirty.
     */
    public static enum EditionStatus {

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

    public static enum ParseStatus {

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

    private EditionStatus editionStatus = EditionStatus.NO_CHANGES;

    private ParseStatus parseStatus = ParseStatus.NOT_PARSED;

    public DataModelerContext() {
    }

    public void init(List<PropertyTypeTO> baseTypes) {
        this.baseTypes = baseTypes;
        helper = new DataModelHelper();
        helper.setBaseTypes(baseTypes);
    }

    public DataModelTO getDataModel() {
        return editorModelContent != null ? editorModelContent.getDataModel() : null;
    }

    public DataModelHelper getHelper() {
        return helper;
    }

    public void setHelper(DataModelHelper helper) {
        this.helper = helper;
    }

    public Map<String, AnnotationDefinitionTO> getAnnotationDefinitions() {
        return annotationDefinitions;
    }

    public void setAnnotationDefinitions(Map<String, AnnotationDefinitionTO> annotationDefinitions) {
        this.annotationDefinitions = annotationDefinitions;
    }

    public List<PropertyTypeTO> getBaseTypes() {
        return baseTypes;
    }

    public ParseStatus getParseStatus() {
        return parseStatus;
    }

    public void setParseStatus( ParseStatus parseStatus ) {
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

    /**
     * Editor level dirty status.
     *
     * @return true if there have been whatever change since the last save/load operation, no mater if the change
     * was in the "Editor" tab or in the "Source" tab, and if the tabs are synchronized. We still need to save the
     * changes in the .java file.
     */
    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly( boolean readonly ) {
        this.readonly = readonly;
    }

    public boolean isEditorChanged() {
        return editionStatus == EditionStatus.EDITOR_CHANGED;
    }

    public boolean isSourceChanged() {
        return editionStatus == EditionStatus.SOURCE_CHANGED;
    }

    public void appendPackages(Collection<Package> packages) {
        if (packages != null) {
            for (Package packageToAppend : packages) {
                if (!"".equals(packageToAppend.getPackageName()) && !currentProjectPackages.contains(packageToAppend.getPackageName())) {
                    currentProjectPackages.add(packageToAppend.getPackageName());
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
                if (!currentProjectPackages.contains(subPackage)) currentProjectPackages.add(subPackage);
            }
        }
    }

    public List<String> getCurrentProjectPackages() {
        return currentProjectPackages;
    }

    public void cleanPackages() {
        if (currentProjectPackages != null) currentProjectPackages.clear();
    }

    public boolean isDataObjectLoaded() {
        return getDataObject() != null;
    }

    public Project getCurrentProject() {
        if ( editorModelContent != null ) {
            return editorModelContent.getCurrentProject();
        }
        return null;
    }

    public DataObjectTO getDataObject() {
        if ( editorModelContent != null ) {
            return editorModelContent.getDataObject();
        }
        return null;
    }

    public void setDataObject(DataObjectTO dataObjectTO) {
        if (editorModelContent != null) {
            editorModelContent.setDataObject( dataObjectTO );
        }
    }

    public EditionStatus getEditionStatus() {
        return editionStatus;
    }

    public void setEditionStatus( EditionStatus editionStatus ) {
        this.editionStatus = editionStatus;
    }

    public EditorModelContent getEditorModelContent() {
        return editorModelContent;
    }

    public void setEditorModelContent(EditorModelContent editorModelContent) {
        this.editorModelContent = editorModelContent;
        if ( editorModelContent.getDataModel() != null) {
            //TODO, likely this helper is no longer needed.
            helper.setDataModel( editorModelContent.getDataModel());
        }
        cleanPackages();
        appendPackages( editorModelContent.getCurrentProjectPackages() );
    }

    public void clear() {
        if (annotationDefinitions != null) annotationDefinitions.clear();
        if (baseTypes != null) baseTypes.clear();
        if (getDataModel() != null && getDataModel().getDataObjects() != null) getDataModel().getDataObjects().clear();
        cleanPackages();
        helper = new DataModelHelper();
    }
}
