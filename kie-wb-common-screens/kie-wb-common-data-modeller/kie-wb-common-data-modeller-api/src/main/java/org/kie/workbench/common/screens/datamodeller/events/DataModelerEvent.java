/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.events;

import org.guvnor.common.services.project.model.Module;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.Method;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.uberfire.backend.vfs.Path;

@Portable
public class DataModelerEvent {

    public static final String DATA_MODEL_BROWSER = "DATA_MODEL_BROWSER";
    public static final String DATA_OBJECT_BROWSER = "DATA_OBJECT_BROWSER";
    public static final String DATA_OBJECT_EDITOR = "DATA_OBJECT_EDITOR";
    protected DataObject currentDataObject;
    protected ObjectProperty currentField;
    protected Method currentMethod;
    protected Module currentModule;
    protected String source;
    protected String contextId;
    protected Path path;

    public DataModelerEvent() {
    }

    public DataModelerEvent(DataObject currentDataObject) {
        this.currentDataObject = currentDataObject;
    }

    public DataModelerEvent(String contextId,
                            String source,
                            DataObject currentDataObject) {
        this(contextId,
             source,
             currentDataObject,
             null);
    }

    public DataModelerEvent(String source,
                            DataObject currentDataObject) {
        this(null,
             source,
             currentDataObject,
             null);
    }

    public DataModelerEvent(String contextId,
                            String source,
                            DataObject currentDataObject,
                            ObjectProperty currentField) {
        this.contextId = contextId;
        this.source = source;
        this.currentDataObject = currentDataObject;
        this.currentField = currentField;
    }

    public DataModelerEvent(String contextId,
                            Module currentModule,
                            DataObject currentDataObject) {
        this.contextId = contextId;
        this.currentModule = currentModule;
        this.currentDataObject = currentDataObject;
    }

    public DataModelerEvent(Module currentModule,
                            DataObject currentDataObject) {
        this.currentModule = currentModule;
        this.currentDataObject = currentDataObject;
    }

    public DataObject getCurrentDataObject() {
        return currentDataObject;
    }

    public DataModelerEvent withCurrentDataObject(DataObject currentDataObject) {
        setCurrentDataObject(currentDataObject);
        return this;
    }

    public void setCurrentDataObject(DataObject currentDataObject) {
        this.currentDataObject = currentDataObject;
    }

    public ObjectProperty getCurrentField() {
        return currentField;
    }

    public DataModelerEvent withCurrentField(ObjectProperty currentField) {
        setCurrentField(currentField);
        return this;
    }

    public void setCurrentField(ObjectProperty currentField) {
        this.currentField = currentField;
    }

    public DataModelerEvent withCurrentProject(Module currentModule) {
        setCurrentModule(currentModule);
        return this;
    }

    public void setCurrentMethod(Method currentMethod) {
        this.currentMethod = currentMethod;
    }

    public Method getCurrentMethod() {
        return currentMethod;
    }

    public DataModelerEvent withCurrentMethod(Method currentMethod) {
        setCurrentMethod(currentMethod);
        return this;
    }

    public Module getCurrentModule() {
        return currentModule;
    }

    public void setCurrentModule(Module currentModule) {
        this.currentModule = currentModule;
    }

    public String getSource() {
        return source;
    }

    public DataModelerEvent withSource(String source) {
        setSource(source);
        return this;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getContextId() {
        return contextId;
    }

    public DataModelerEvent withContextId(String contextId) {
        setContextId(contextId);
        return this;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public Path getPath() {
        return path;
    }

    public DataModelerEvent withPath(Path path) {
        setPath(path);
        return this;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public boolean isFrom(Module module) {
        return this.currentModule != null && this.currentModule.equals(module);
    }

    public boolean isFrom(String source) {
        return this.source != null && this.source.equals(source);
    }

    public boolean isFromContext(String contextId) {
        return this.contextId != null && this.contextId.equals(contextId);
    }
}
