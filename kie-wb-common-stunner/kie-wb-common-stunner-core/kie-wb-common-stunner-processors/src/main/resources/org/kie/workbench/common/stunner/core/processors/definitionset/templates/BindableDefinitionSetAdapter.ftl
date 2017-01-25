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

import javax.annotation.Generated;
import javax.enterprise.context.ApplicationScoped;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionSetAdapterProxy;

@Generated("${generatedByClassName}")
@ApplicationScoped
public class ${className} extends ${parentAdapterClassName}<Object> {

    private static final Map<Class, String> descriptionFieldNames = new HashMap<Class, String>(${valuePropNamesSize}) {{
        <#list valuePropNames as valuePropName>
            put( ${valuePropName.className}.class, "${valuePropName.methodName}" );
        </#list>
    }};

    private static final Map<Class, Annotation> qualifiers = new HashMap<Class, Annotation>(${qualifiersSize}) {{
        <#list qualifiers as qualifier>
            put( ${qualifier.className}.class,
                new ${qualifier.methodName}() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return ${qualifier.methodName}.class;
                }
            });
        </#list>
    }};

    private static final Set<String> definitionIds = new HashSet<String>(${definitionIdsSize}) {{
        <#list definitionIds as definitionId>
            add( "${definitionId}" );
        </#list>
    }};

    private static final Map<Class, Class> graphFactoryTypes = new HashMap<Class, Class>(${graphFactoryTypesSize}) {{
    <#list graphFactoryTypes as graphFactoryType>
        put( ${graphFactoryType.className}.class, ${graphFactoryType.methodName}.class );
    </#list>
        }};

    protected ${className}() {
    }

    @Inject
    public ${className}(${adapterFactoryClassName} adapterFactory) {
        super(adapterFactory);
    }

    @Override
    protected void setBindings(final BindableDefinitionSetAdapter<Object> adapter) {
        adapter.setBindings( descriptionFieldNames, graphFactoryTypes, qualifiers, definitionIds );
    }

}
