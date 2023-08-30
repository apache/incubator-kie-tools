/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.dynamic.service.shared;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.forms.model.FormDefinition;

public abstract class AbstractFormRenderingContext<T> implements FormRenderingContext<T> {

    protected String namespace;

    protected Map<String, FormDefinition> availableForms = new HashMap<String, FormDefinition>();

    protected String rootFormId;

    protected T model;

    protected FormRenderingContext parentContext;

    protected RenderMode renderMode = RenderMode.EDIT_MODE;

    public AbstractFormRenderingContext(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public FormDefinition getRootForm() {
        return availableForms.get(rootFormId);
    }

    @Override
    public void setRootForm(FormDefinition rootForm) {
        this.rootFormId = rootForm.getId();
        availableForms.put(rootFormId,
                           rootForm);
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setModel(T model) {
        this.model = model;
    }

    @Override
    public T getModel() {
        return model;
    }

    @Override
    public FormRenderingContext getParentContext() {
        return parentContext;
    }

    @Override
    public void setParentContext(FormRenderingContext parentContext) {
        this.parentContext = parentContext;
    }

    @Override
    public RenderMode getRenderMode() {
        return renderMode;
    }

    @Override
    public void setRenderMode(RenderMode renderMode) {
        this.renderMode = renderMode;
    }

    @Override
    public Map<String, FormDefinition> getAvailableForms() {
        return availableForms;
    }

    protected abstract AbstractFormRenderingContext<T> getNewInstance(String namespace);

    @Override
    public FormRenderingContext getCopyFor(String namespace, String formKey, T model) {
        if (formKey == null || formKey.isEmpty()) {
            return null;
        }
        AbstractFormRenderingContext copy = getNewInstance(namespace);
        copy.setRenderMode(renderMode);
        copy.setRootForm(availableForms.get(formKey));
        copy.setModel(model);
        copy.availableForms = availableForms;
        copy.setParentContext(this);
        return copy;
    }
}
