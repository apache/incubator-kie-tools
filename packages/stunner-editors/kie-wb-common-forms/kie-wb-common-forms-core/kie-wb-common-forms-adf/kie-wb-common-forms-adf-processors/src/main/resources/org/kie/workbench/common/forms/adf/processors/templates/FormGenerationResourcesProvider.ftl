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
 
package ${package};

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.forms.adf.definitions.settings.ColSpan;
import org.kie.workbench.common.forms.adf.service.building.FieldStatusModifier;
import org.kie.workbench.common.forms.adf.service.building.FormGenerationResourcesProvider;
import org.kie.workbench.common.forms.adf.service.definitions.FormDefinitionSettings;
import org.kie.workbench.common.forms.adf.service.definitions.I18nSettings;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FormElement;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutColumnDefinition;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;

@Generated("${generatedByClassName}")
@ApplicationScoped
public class ModuleFormGenerationResourcesProvider implements FormGenerationResourcesProvider {

    private Map<String, FormDefinitionSettings> definitionSettings = new HashMap<>();

    private Map<String, FieldStatusModifier> fieldStatusModifiers = new HashMap<>();

    private Map<String, String> fieldStatusModifiersReferences = new HashMap<>();

    public ModuleFormGenerationResourcesProvider() {
        <#list forms as form>
        definitionSettings.put("${form.modelClass}", new ${form.builderClass}().getSettings());
        </#list>
        <#list fieldDefinitions as fieldDefinition>
        fieldStatusModifiers.put("${fieldDefinition.fieldModifierName}", new ${fieldDefinition.fieldModifierName}());
        </#list>
    }

    @Override
    public Map<String, FormDefinitionSettings> getDefinitionSettings() {
        return definitionSettings;
    }

    @Override
    public Map<String, FieldStatusModifier> getFieldModifiers() {
        return fieldStatusModifiers;
    }

    @Override
    public Map<String, String> getFieldModifierReferences() {
        return fieldStatusModifiersReferences;
    }
    <#list forms as form>

    class ${form.builderClass} {
        public FormDefinitionSettings getSettings() {
            FormDefinitionSettings settings = new FormDefinitionSettings("${form.modelClass}");
        <#if form.i18nBundle??>
            settings.setI18nSettings(new I18nSettings("${form.i18nBundle}"));
        <#else>
            settings.setI18nSettings(new I18nSettings());
        </#if>
            settings.setLayout(new LayoutDefinition(new LayoutColumnDefinition[] { <#list form.layoutColumns as column><#if column_index != 0>, </#if>new LayoutColumnDefinition(ColSpan.${column})</#list> }));
            List<FormElement> elements = new ArrayList<FormElement>();
            <#list form.elements as element>
            elements.add(${element.methodName}());
            </#list>
            settings.getFormElements().addAll(elements);
            return settings;
        }
            <#list form.elements as element>

        private FormElement ${element.methodName}() {
            FieldElement field = new FieldElement("${element.name}", "${element.binding}", new TypeInfoImpl(TypeKind.${element.type}, "${element.className}", ${element.list}));
            field.setPreferredType(${element.preferredType}.class);
            field.setLabelKey("${element.label}");
            field.setHelpMessageKey("${element.helpMessage}");
            field.setRequired(${element.required});
            field.setReadOnly(${element.readOnly});
            <#list element.params?keys as param>
            field.getParams().put("${param}", "${element.params[param]}");
            </#list>
            field.getLayoutSettings().setAfterElement("${element.afterElement}");
            field.getLayoutSettings().setHorizontalSpan(${element.horizontalSpan});
            field.getLayoutSettings().setVerticalSpan(${element.verticalSpan});
            field.getLayoutSettings().setWrap(${element.wrap});
            <#if element.fieldModifier != "">
            fieldStatusModifiersReferences.put("${element.modelClass}.${element.name}", "${element.fieldModifier}");
            </#if>
            return field;
        }
            </#list>
    }
    </#list>
    <#list fieldDefinitions as fieldDefinition>

    class ${fieldDefinition.fieldModifierName} implements FieldStatusModifier<${fieldDefinition.modelClassName}> {
        @Override
        public void modifyFieldStatus(FieldDefinition field, ${fieldDefinition.modelClassName} model) {
            if (model != null) {
            <#if fieldDefinition.labelGetter??>
                field.setLabel(model.${fieldDefinition.labelGetter}());
            </#if>
            <#if fieldDefinition.helpMessageGetter??>
                field.setHelpMessage(model.${fieldDefinition.helpMessageGetter}());
            </#if>
            <#if fieldDefinition.readOnlyGetter??>
                field.setReadOnly(Boolean.TRUE.equals(model.${fieldDefinition.readOnlyGetter}()));
            </#if>
            <#if fieldDefinition.requiredGetter??>
                field.setRequired(Boolean.TRUE.equals(model.${fieldDefinition.requiredGetter}()));
            </#if>
            }
        }
    }
    </#list>
}
