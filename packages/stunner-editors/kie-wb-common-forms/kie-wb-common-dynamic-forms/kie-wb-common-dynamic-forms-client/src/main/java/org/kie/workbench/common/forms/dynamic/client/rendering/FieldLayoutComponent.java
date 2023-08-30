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

import java.util.Optional;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormLayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;

@Dependent
public class FieldLayoutComponent implements FormLayoutComponent,
                                             LayoutDragComponent {

    protected FlowPanel content = new FlowPanel();

    protected FieldRendererManager fieldRendererManager;

    protected TranslationService translationService;

    protected FieldDefinition field;

    protected FieldRenderer fieldRenderer;

    protected FormRenderingContext renderingContext;

    @Inject
    public FieldLayoutComponent(FieldRendererManager fieldRendererManager, TranslationService translationService) {
        this.fieldRendererManager = fieldRendererManager;
        this.translationService = translationService;
    }

    public void init(FormRenderingContext renderingContext,
                     FieldDefinition field) {
        this.renderingContext = renderingContext;

        this.field = field;

        initComponent();
    }

    protected void initComponent() {
        fieldRenderer = fieldRendererManager.getRendererForField(field);
        if (fieldRenderer != null) {
            fieldRenderer.init(renderingContext, field);
        }
    }

    @Override
    public IsWidget getShowWidget(RenderingContext ctx) {
        return generateContent(ctx);
    }
    
    public Optional<IsWidget> getContentPart(String partId, RenderingContext componentContext) {
        return Optional.ofNullable(fieldRenderer.getFieldPartWidget(partId));
    }

    protected IsWidget generateContent(RenderingContext ctx) {
        if (fieldRenderer != null) {
            renderContent();
        }

        return content;
    }

    protected void renderContent() {
        content.clear();
        content.add(fieldRenderer.renderWidget());
    }
    
    public void addComponentParts(LayoutComponent component) {
        Set<String> parts = fieldRenderer.getFieldParts();
        component.removePartIf(partId ->  !parts.contains(partId));
        parts.forEach(component::addPartIfAbsent);
    }

    public String getFieldId() {
        return field.getId();
    }

    public String getFormId() {
        return renderingContext.getRootForm().getId();
    }

    public FieldRenderer getFieldRenderer() {
        return fieldRenderer;
    }

    public FieldDefinition getField() {
        return field;
    }

    @PreDestroy
    public void destroy() {
        content.clear();
    }
}
