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

package org.kie.workbench.common.forms.model.impl.basic.selectors;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.FieldDef;

@Portable
@Bindable
public class StringSelectorOption implements SelectorOption<String> {

    @FieldDef( label = "Value")
    private String value;

    @FieldDef( label = "Text")
    private String text;

    @FieldDef( label = "Is default value", position = 2 )
    private Boolean defaultValue = false;

    public StringSelectorOption() {
    }

    public StringSelectorOption( @MapsTo( "value" ) String value,
                                 @MapsTo( "text" ) String text,
                                 @MapsTo( "defaultValue" ) Boolean defaultValue ) {
        this.value = value;
        this.text = text;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }

    @Override
    public Boolean isDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue( Boolean defaultValue ) {
        this.defaultValue = defaultValue;
    }
}
