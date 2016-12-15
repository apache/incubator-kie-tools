/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.kie.workbench.common.stunner.core.definition.property.type.EnumType;
import org.kie.workbench.common.stunner.core.definition.util.DefinitionUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class EnumTypeSerializer implements Bpmn2OryxPropertySerializer<Object> {

    DefinitionUtils definitionUtils;

    protected EnumTypeSerializer() {
        this( null );
    }

    @Inject
    public EnumTypeSerializer( DefinitionUtils definitionUtils ) {
        this.definitionUtils = definitionUtils;
    }

    @Override
    public boolean accepts( PropertyType type ) {
        return EnumType.name.equals( type.getName() );
    }

    @Override
    public Object parse( Object property, String value ) {
        if ( null == value ) {
            return null;

        } else {
            // Assume that enum names that contains an "_" symbol (underscore) are serialized
            // string by replacing this symbol by a whitespace and vice-versa.
            // Eg: For TaskType.TASK_BUSINESS_RULE -> in oryx the json string value is "Business Rule".
            final String v = value.toUpperCase().replaceAll( " ", "_" );
            return definitionUtils.getPropertyAllowedValue( property, v );

        }

    }

    @Override
    public String serialize( Object property, Object value ) {
        return StringUtils.capitalize( value.toString().toLowerCase() );

    }

}
