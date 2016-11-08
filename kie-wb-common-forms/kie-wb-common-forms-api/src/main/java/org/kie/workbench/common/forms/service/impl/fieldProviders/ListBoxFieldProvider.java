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
import java.util.function.Predicate;
import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.model.impl.basic.selectors.listBox.ListBoxBase;
import org.kie.workbench.common.forms.model.impl.basic.selectors.listBox.EnumListBoxFieldDefinition;
import org.kie.workbench.common.forms.model.FieldTypeInfo;
import org.kie.workbench.common.forms.model.impl.basic.selectors.listBox.StringListBoxFieldDefinition;

@Dependent
public class ListBoxFieldProvider extends SelectorFieldProvider<ListBoxBase> {

    @Override
    public String getProviderCode() {
        return ListBoxBase.CODE;
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType( String.class );
    }

    @Override
    public int getPriority() {
        return 6;
    }

    @Override
    public ListBoxBase getDefaultField() {
        return new StringListBoxFieldDefinition();
    }

    @Override
    public ListBoxBase createFieldByType( FieldTypeInfo typeInfo ) {
        if ( typeInfo.isEnum() ) {
            return new EnumListBoxFieldDefinition();
        }

        for ( String type : supportedTypes ) {
            if ( type.equals( typeInfo.getType() ) ) {
                return new StringListBoxFieldDefinition();
            }
        }

        return new EnumListBoxFieldDefinition();
    }

    @Override
    public boolean supports( Class clazz ) {
        return super.supports( clazz ) || clazz.isEnum();
    }

    @Override
    protected boolean isSupported( FieldTypeInfo typeInfo ) {
        return super.isSupported( typeInfo ) || typeInfo.isEnum();
    }
}
