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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor;

import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FormFieldImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.forms.model.MultipleInstanceVariableFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.MultipleInstanceVariableFieldType;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Node;

import static org.kie.workbench.common.stunner.core.client.util.ClientUtils.getSelectedNode;

@Renderer(type = MultipleInstanceVariableFieldType.class)
public class MultipleInstanceVariableFieldRenderer
        extends FieldRenderer<MultipleInstanceVariableFieldDefinition, DefaultFormGroup> {

    private final MultipleInstanceVariableEditorWidget widget;

    private final SessionManager sessionManager;

    private final ClientTranslationService translationService;

    @Inject
    public MultipleInstanceVariableFieldRenderer(MultipleInstanceVariableEditorWidget widget,
                                                 SessionManager sessionManager,
                                                 ClientTranslationService translationService) {
        this.widget = widget;
        this.sessionManager = sessionManager;
        this.translationService = translationService;
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup = formGroupsInstance.get();
        formGroup.render(widget, field);
        return formGroup;
    }

    @Override
    public String getName() {
        return MultipleInstanceVariableFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        widget.setReadOnly(readOnly);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void registerCustomFieldValidators(FormFieldImpl field) {
        final ClientSession session = sessionManager.getCurrentSession();
        final Diagram diagram = session.getCanvasHandler().getDiagram();
        final Node node = getSelectedNode(diagram, sessionManager.getCurrentSession());
        field.getCustomValidators().add(new MultipleInstanceVariableValidator(node, translationService));
    }
}
