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

package ${packageName};

import org.kie.workbench.common.stunner.core.definition.morph.BindableMorphProperty;
import org.kie.workbench.common.stunner.core.definition.morph.MorphProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import org.jboss.errai.common.client.api.annotations.Portable;

@Generated("${generatedByClassName}")
@Portable
public class ${className} extends ${parentClassName} {

    private static final  Map<Class<?>, Collection<MorphProperty>> PROPERTY_MORPH_DEFINITIONS =
        new HashMap<Class<?>, Collection<MorphProperty>>( 1 ) {{
            put( ${morphBaseClassName}.class,
                new ArrayList<MorphProperty>( ${morphPropertiesSize} ) {{
                    <#list morphProperties as morphProperty>
                        add( new ${morphProperty.name}MorphProperty() );
                    </#list>
                }} );
        }};

    public ${className}() {
    }

    @Override
    protected Map<Class<?>, Collection<MorphProperty>> getBindableMorphProperties() {
        return PROPERTY_MORPH_DEFINITIONS;
    }

    @Override
    protected Class<?> getDefaultType() {
        return ${defaultTypeClassName}.class;
    }

    <#list morphProperties as morphProperty>

        @Portable
        public static class ${morphProperty.name}MorphProperty extends ${bindableMorphPropertyParentClassName}<${morphProperty.className}, Object> {

            private final static ${morphProperty.valueBinderClassName} BINDER = new ${morphProperty.valueBinderClassName}();

            public ${morphProperty.name}MorphProperty() {
            }

            @Override
            public Class<?> getPropertyClass() {
                return ${morphProperty.className}.class;
            }

            @Override
            public Map getMorphTargetClasses() {
                return BINDER.getMorphTargets();
            }

            @Override
            public Object getValue( final ${morphProperty.className} property ) {
                return BINDER.getValue( property );
            }

        }

    </#list>
}
