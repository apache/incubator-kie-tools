/*
* Copyright 2017 Red Hat, Inc. and/or its affiliates.
*  
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*  
*    http://www.apache.org/licenses/LICENSE-2.0
*  
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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
import org.kie.workbench.common.forms.model.FieldDataType;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Generated("${generatedByClassName}")
@ApplicationScoped
public class ModuleFormGenerationResourcesProvider implements FormGenerationResourcesProvider {

    private Map<String, FormDefinitionSettings> definitionSettings = new HashMap<>();

    private Map<String, FieldStatusModifier> fieldStatusModifiers = new HashMap<>();

    private Map<String, String> fieldStatusModifiersReferences = new HashMap<>();

    public ModuleFormGenerationResourcesProvider() {
        <#list forms as form>
        definitionSettings.put( "${form.modelClass}", new ${form.builderClass}().getSettings() );
        </#list>
        <#list fieldModifiers as fieldModifier>
        fieldStatusModifiers.put( "${fieldModifier.fieldModifierName}", new ${fieldModifier.fieldModifierName}() );
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
    ${form.builderCode}
    </#list>
    <#list fieldDefinitions as fieldDefinition>
    ${fieldDefinition.sourceCode}
    </#list>
}
