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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.comboBoxEditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.stunner.bpmn.forms.model.ComboBoxFieldDefinition;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

@Dependent
@Renderer(fieldDefinition = ComboBoxFieldDefinition.class)
public class ComboBoxFieldRenderer
        extends AbstractComboBoxFieldRenderer<ComboBoxFieldDefinition> {

    public static final String TYPE_NAME = ComboBoxFieldDefinition.FIELD_TYPE.getTypeName();

    @Inject
    public ComboBoxFieldRenderer(final ComboBoxWidgetView comboBoxEditor, final ClientTranslationService translationService) {
        super(comboBoxEditor, translationService);
    }

    @Override
    public String getName() {
        return TYPE_NAME;
    }
}
