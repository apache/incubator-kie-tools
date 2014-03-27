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

package org.kie.workbench.common.services.datamodeller.codegen;

import org.apache.velocity.VelocityContext;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;

import java.io.Writer;


/**
 * Keeps all the information for generating a set of java files for a given model.
 */
public class GenerationContext {

    /**
     * Path to the template set that will be used by default.
     */
    private static final String DEFAULT_TEMPLATES_PATH = "org/kie/workbench/common/services/datamodeller/codegen";
    
    private static final String DEFAULT_INITIAL_TEMPLATE = "initial";

    /**
     * Location of the templates
     */
    private String templatesPath;

    /**
     * Initial template name.
     */
    private String initialTemplate;

    private String currentTemplate;

    private Writer currentOutput;

    /**
     * Optional outputPath to write the generated pojos in the local filesystem. This parameter
     * can be used for testing purposes.
     */
    private String outputPath;

    /**
     * Stores the model to be generated.
     */
    private DataModel dataModel;

    private GenerationListener generationListener;
    
    private VelocityContext velocityContext;

    /**
     * Iteration variable, keeps a reference to the next data object to be generated.
     */
    //TODO eliminate this if no longer needed
    private DataObject currentDataObject;

    public GenerationContext(DataModel dataModel, boolean defaultTemplates) {
        this.dataModel = dataModel;
        if (defaultTemplates) {
            setTemplatesPath(DEFAULT_TEMPLATES_PATH);
            setInitialTemplate(DEFAULT_INITIAL_TEMPLATE);
        }
    }

    public GenerationContext(DataModel dataModel) {
        this(dataModel, true);
    }

    public String getTemplatesPath() {
        return templatesPath;
    }

    public void setTemplatesPath(String templatesPath) {
        this.templatesPath = templatesPath;
    }

    public String getInitialTemplate() {
        return initialTemplate;
    }

    public void setInitialTemplate(String initialTemplate) {
        this.initialTemplate = initialTemplate;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public DataObject getCurrentDataObject() {
        return currentDataObject;
    }

    public DataModel getDataModel() {
        return dataModel;
    }

    public void setCurrentDataObject(DataObject currentDataObject) {
        this.currentDataObject = currentDataObject;
    }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public String getCurrentTemplate() {
        return currentTemplate;
    }

    public void setCurrentTemplate(String currentTemplate) {
        this.currentTemplate = currentTemplate;
    }

    public GenerationListener getGenerationListener() {
        return generationListener;
    }

    public void setGenerationListener(GenerationListener generationListener) {
        this.generationListener = generationListener;
    }

    public Writer getCurrentOutput() {
        return currentOutput;
    }

    public void setCurrentOutput(Writer currentOutput) {
        this.currentOutput = currentOutput;
    }

    public VelocityContext getVelocityContext() {
        return velocityContext;
    }

    public void setVelocityContext(VelocityContext velocityContext) {
        this.velocityContext = velocityContext;
    }
}