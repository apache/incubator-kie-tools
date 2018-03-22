/*
* Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package ${packageName};

import ${parentAdapterClassName};
import ${adapterFactoryClassName};

import javax.annotation.Generated;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionAdapterProxy;

@Generated("${generatedByClassName}")
@ApplicationScoped
public class ${className} extends ${parentAdapterClassName}<Object> {

    private static final Map<Class, Class> baseTypes = new HashMap<Class, Class>(${baseTypesSize}) {{
        <#list baseTypes as baseType>
            put( ${baseType.className}.class, ${baseType.methodName}.class );
        </#list>
    }};

    private static final Map<Class, String> idFieldNames = new HashMap<Class, String>(${idFieldNamesSize}) {{
        <#list idFieldNames as idFieldName>
            put( ${idFieldName.className}.class, "${idFieldName.methodName}" );
        </#list>
    }};

    private static final Map<Class, String> categoryFieldNames = new HashMap<Class, String>(${categoryFieldNamesSize}) {{
        <#list categoryFieldNames as categoryFieldName>
            put( ${categoryFieldName.className}.class, "${categoryFieldName.methodName}" );
        </#list>
    }};

    private static final Map<Class, String> titleFieldNames = new HashMap<Class, String>(${titleFieldNamesSize}) {{
        <#list titleFieldNames as titleFieldName>
            put( ${titleFieldName.className}.class, "${titleFieldName.methodName}" );
        </#list>
    }};

    private static final Map<Class, String> descriptionFieldNames = new HashMap<Class, String>(${descriptionFieldNamesSize}) {{
        <#list descriptionFieldNames as descriptionFieldName>
              put( ${descriptionFieldName.className}.class, "${descriptionFieldName.methodName}" );
        </#list>
    }};

    private static final Map<Class, String> labelsFieldNames = new HashMap<Class, String>(${labelsFieldNamesSize}) {{
        <#list labelsFieldNames as labelsFieldName>
              put( ${labelsFieldName.className}.class, "${labelsFieldName.methodName}" );
        </#list>
    }};

    private static final Map<Class, Class> graphFactoryFieldNames = new HashMap<Class, Class>(${graphFactoryFieldNamesSize}) {{
        <#list graphFactoryFieldNames as graphFactoryFieldName>
              put( ${graphFactoryFieldName.className}.class, ${graphFactoryFieldName.methodName}.class );
        </#list>
    }};

    private static final Map<Class, Set<String>> propertySetsFieldNames = new HashMap<Class, Set<String>>(${propertySetsFieldNamesSize}) {{
        <#list propertySetsFieldNames as propertySetsFieldName>
           put( ${propertySetsFieldName.className}.class, new HashSet<String>() {{
                <#list propertySetsFieldName.elements as subElem>
                    add ( "${subElem}" );
                </#list>
            }} );
        </#list>
    }};

    private static final Map<Class, Set<String>> propertiesFieldNames = new HashMap<Class, Set<String>>(${propertiesFieldNamesSize}) {{
        <#list propertiesFieldNames as propertiesFieldName>
            put( ${propertiesFieldName.className}.class, new HashSet<String>() {{
            <#list propertiesFieldName.elements as subElem>
                add ( "${subElem}" );
            </#list>
            }} );
        </#list>
    }};

    private static final Map<${metaTypeClass}, Class> metaPropertyTypes = new HashMap<${metaTypeClass}, Class>(${metaTypesSize}) {{
        <#list metaTypes as metaType>
            put( ${metaType.className}, ${metaType.methodName} );
        </#list>
    }};

    protected ${className}() {
    }

    @Inject
    public ${className}(${adapterFactoryClassName} adapterFactory) {
        super(adapterFactory);
    }

    @Override
    protected void setBindings(final BindableDefinitionAdapter<Object> adapter) {
        adapter.setBindings( metaPropertyTypes,
                baseTypes,
                propertySetsFieldNames,
                propertiesFieldNames,
                graphFactoryFieldNames,
                idFieldNames,
                labelsFieldNames,
                titleFieldNames,
                categoryFieldNames,
                descriptionFieldNames);
    }

}
