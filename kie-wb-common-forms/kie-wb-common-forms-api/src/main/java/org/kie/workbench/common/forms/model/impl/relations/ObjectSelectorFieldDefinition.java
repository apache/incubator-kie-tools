/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.model.impl.relations;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.impl.HasMask;

@Portable
@Bindable
public class ObjectSelectorFieldDefinition extends FieldDefinition implements EntityRelationField, HasMask {

    public static final String CODE = "ObjectSelector";

    /*
    Expression to mask the value based on the object properties. The object properties should be surrounded by '{' and
    '}', for example {propertyName}.
    For example, an expression to mask a user object could be like: "{lastName}, {name}", when a given user instance is
    masked it will result on a text like "Shakespeare, William".
     */
    @FieldDef( label = "Value Mask" )
    protected String mask;

    public ObjectSelectorFieldDefinition() {
        super( CODE );
    }

    @Override
    public String getMask() {
        return mask;
    }

    @Override
    public void setMask( String mask ) {
        this.mask = mask;
    }

    @Override
    protected void doCopyFrom( FieldDefinition other ) {
        if ( other instanceof HasMask ) {
            setMask( ( (HasMask) other ).getMask() );
        }
    }
}
