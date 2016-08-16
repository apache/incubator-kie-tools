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

package org.kie.workbench.common.forms.processing.engine.handling.imp;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;

public class FormFieldImpl implements FormField {
    private String alias;
    private String bindedName = null;
    private boolean validateOnChange = false;
    private IsWidget widget;

    public FormFieldImpl( String alias, IsWidget widget ) {
        assert alias != null;
        assert widget != null;

        this.alias = alias;
        this.widget = widget;
    }

    public FormFieldImpl( String alias, boolean validateOnChange, IsWidget widget ) {
        this( alias, widget );
        this.validateOnChange = validateOnChange;
    }

    public FormFieldImpl( String alias, String bindedName, boolean validateOnChange, IsWidget widget ) {
        this( alias, widget );
        this.bindedName = bindedName;
        this.validateOnChange = validateOnChange;
    }

    @Override
    public String getFieldName() {
        return alias;
    }

    @Override
    public String getFieldBinding() {
        return bindedName;
    }

    @Override
    public boolean isValidateOnChange() {
        return validateOnChange;
    }

    @Override
    public IsWidget getWidget() {
        return widget;
    }
}
