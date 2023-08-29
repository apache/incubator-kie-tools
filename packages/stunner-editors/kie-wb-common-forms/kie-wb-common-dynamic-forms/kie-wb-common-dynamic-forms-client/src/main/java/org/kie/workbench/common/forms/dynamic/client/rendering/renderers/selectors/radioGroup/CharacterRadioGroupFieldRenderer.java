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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.radioGroup;

import javax.enterprise.context.Dependent;

import org.jboss.errai.databinding.client.api.Converter;
import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.common.rendering.client.widgets.selectors.radiogroup.CharacterRadioGroup;
import org.kie.workbench.common.forms.common.rendering.client.widgets.selectors.radiogroup.RadioGroupBase;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.CharacterRadioGroupFieldDefinition;

@Dependent
@Renderer(fieldDefinition = CharacterRadioGroupFieldDefinition.class)
public class CharacterRadioGroupFieldRenderer
        extends RadioGroupFieldRendererBase<CharacterRadioGroupFieldDefinition, CharacterSelectorOption, Character> {

    @Override
    protected RadioGroupBase<Character> getRadioGroup() {
        return new CharacterRadioGroup(fieldNS);
    }

    @Override
    public Converter getConverter() {
        return null;
    }
}
