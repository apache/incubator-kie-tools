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
import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindablePropertySetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindablePropertySetAdapterProxy;

@Generated("${generatedByClassName}")
@ApplicationScoped
public class ${className} extends ${parentAdapterClassName}<Object> {

    private static final Map<Class, String> nameFieldNames = new HashMap<Class, String>(${nameFieldNamesSize}) {{
        <#list nameFieldNames as nameFieldName>
            put( ${nameFieldName.className}.class, "${nameFieldName.methodName}" );
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

    protected ${className}() {
    }

    @Inject
    public ${className}(${adapterFactoryClassName} adapterFactory) {
        super(adapterFactory);
    }

    @Override
    protected void setBindings( final BindablePropertySetAdapter<Object> adapter ) {

        adapter.setBindings( nameFieldNames, propertiesFieldNames );

    }

}
