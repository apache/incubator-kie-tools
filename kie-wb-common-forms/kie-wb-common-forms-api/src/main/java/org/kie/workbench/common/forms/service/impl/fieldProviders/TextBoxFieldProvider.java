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

package org.kie.workbench.common.forms.service.impl.fieldProviders;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.model.FieldTypeInfo;
import org.kie.workbench.common.forms.model.impl.basic.textBox.CharacterBoxFieldDefinition;
import org.kie.workbench.common.forms.model.impl.basic.textBox.TextBoxBase;
import org.kie.workbench.common.forms.model.impl.basic.textBox.TextBoxFieldDefinition;

@Dependent
public class TextBoxFieldProvider extends BasicTypeFieldProvider<TextBoxBase> {

    @Override
    public String getProviderCode() {
        return TextBoxFieldDefinition.CODE;
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType( String.class );
        registerPropertyType( Character.class );
        registerPropertyType( char.class );


// TODO: implement this fieldTypes
        registerPropertyType( BigDecimal.class );
        registerPropertyType( BigInteger.class );
        registerPropertyType( Byte.class );
        registerPropertyType( byte.class );
        registerPropertyType( Double.class );
        registerPropertyType( double.class );
        registerPropertyType( Float.class );
        registerPropertyType( float.class );
        registerPropertyType( Integer.class );
        registerPropertyType( int.class );
        registerPropertyType( Long.class );
        registerPropertyType( long.class );
        registerPropertyType( Short.class );
        registerPropertyType( short.class );
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public TextBoxBase getDefaultField() {
        return new TextBoxFieldDefinition();
    }

    @Override
    public TextBoxBase createFieldByType( FieldTypeInfo typeInfo ) {
        if ( typeInfo.getType().equals( Character.class.getName() ) ||
                typeInfo.getType().equals( char.class.getName() ) ) {
            return new CharacterBoxFieldDefinition();
        }
        return getDefaultField();
    }
}
