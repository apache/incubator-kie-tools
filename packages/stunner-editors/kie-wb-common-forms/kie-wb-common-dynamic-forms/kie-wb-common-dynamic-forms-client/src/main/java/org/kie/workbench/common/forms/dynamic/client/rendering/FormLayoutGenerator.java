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


package org.kie.workbench.common.forms.dynamic.client.rendering;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.uberfire.ext.layout.editor.api.editor.LayoutInstance;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.generator.AbstractLayoutGenerator;

@Any
@Dependent
public class FormLayoutGenerator extends AbstractLayoutGenerator {

    private FormGeneratorDriver driver;

    @Inject
    public FormLayoutGenerator(FormGeneratorDriver driver) {
        this.driver = driver;
    }

    @Override
    public LayoutInstance build(LayoutTemplate layoutTemplate) {
        return super.build(layoutTemplate, driver);
    }

    public HTMLElement buildLayout(FormRenderingContext renderingContext) {
        driver.clear();
        driver.setRenderingContext(renderingContext);

        if (renderingContext == null || renderingContext.getRootForm() == null) {
            return driver.createContainer();
        }

        LayoutInstance layoutInstance = super.build(renderingContext.getRootForm().getLayoutTemplate(), driver);
        return layoutInstance.getElement();
    }

    public List<FieldLayoutComponent> getLayoutFields() {
        return driver.getLayoutFields();
    }

    public FieldLayoutComponent getFieldLayoutComponentForField(FieldDefinition field) {
        return driver.getFieldLayoutComponentForField(field);
    }

    public void clear() {
        driver.clear();
    }
}
