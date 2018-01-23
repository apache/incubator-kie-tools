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
import java.util.Map;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindablePropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindablePropertyAdapterProxy;
import org.kie.workbench.common.stunner.core.definition.property.*;
import org.kie.workbench.common.stunner.core.definition.property.type.*;

@Generated("${generatedByClassName}")
@ApplicationScoped
public class ${className} extends ${parentAdapterClassName}<Object, Object> {

    private static final Map<Class, String> propValueFieldNames = new HashMap<Class, String>(${valuePropNamesSize}) {{
        <#list valuePropNames as valuePropName>
            put( ${valuePropName.className}.class, "${valuePropName.methodName}" );
        </#list>

    }};

    private static final Map<Class, String> propAllowedValuesFieldNames = new HashMap<Class, String>(${allowedValuesPropNamesSize}) {{
        <#list allowedValuesPropNames as allowedValuesPropName>
            put( ${allowedValuesPropName.className}.class, "${allowedValuesPropName.methodName}" );
        </#list>
    }};

    private static final Map<Class, String> propTypeFieldNames = new HashMap<Class, String>(${propTypePropNamesSize}) {{
        <#list propTypePropNames as propTypePropName>
              put( ${propTypePropName.className}.class, "${propTypePropName.methodName}" );
        </#list>
    }};

    private static final Map<Class, PropertyType> propTypes = new HashMap<Class, PropertyType>(${valuePropNamesSize}) {{
        <#list propTypes as pType>
            put( ${pType.className}.class, new ${pType.methodName}() );
        </#list>
    }};

    private static final Map<Class, String> propCaptionFieldNames = new HashMap<Class, String>(${captionPropNamesSize}) {{
        <#list captionPropNames as captionPropName>
              put( ${captionPropName.className}.class, "${captionPropName.methodName}" );
        </#list>
    }};

    private static final Map<Class, String> propDescriptionFieldNames = new HashMap<Class, String>(${descriptionPropNamesSize}) {{
        <#list descriptionPropNames as descriptionPropName>
              put( ${descriptionPropName.className}.class, "${descriptionPropName.methodName}" );
        </#list>
    }};

    private static final Map<Class, String> propReadOnlyFieldNames = new HashMap<Class, String>(${readOnlyPropNamesSize}) {{
        <#list readOnlyPropNames as readOnlyPropName>
            put( ${readOnlyPropName.className}.class, "${readOnlyPropName.methodName}" );
        </#list>
    }};

    private static final Map<Class, String> propOptionalFieldNames = new HashMap<Class, String>(${optionalPropNamesSize}) {{
        <#list optionalPropNames as optionalPropName>
           put( ${optionalPropName.className}.class, "${optionalPropName.methodName}" );
        </#list>
    }};

    protected ${className}() {
    }

    @Inject
    public ${className}(${adapterFactoryClassName} adapterFactory) {
        super(adapterFactory);
    }

    @Override
    protected void setBindings(final BindablePropertyAdapter<Object, Object> adapter) {
        adapter.setBindings(    propTypeFieldNames,
                                propTypes,
                                propCaptionFieldNames,
                                propDescriptionFieldNames,
                                propReadOnlyFieldNames,
                                propOptionalFieldNames,
                                propValueFieldNames,
                                propAllowedValuesFieldNames );
    }

}
