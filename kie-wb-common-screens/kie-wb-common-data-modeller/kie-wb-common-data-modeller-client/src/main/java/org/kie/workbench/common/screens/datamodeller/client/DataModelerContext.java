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

    private boolean readonly = false;

    private List<String> currentProjectPackages = new ArrayList<String>();

    private EditorModelContent editorModelContent;

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

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly( boolean readonly ) {
        this.readonly = readonly;
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
