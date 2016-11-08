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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.model.impl.basic.textBox.TextBoxBase;

@Dependent
public class TextBoxFieldRenderer extends FieldRenderer<TextBoxBase> {

    private TextBox textBox = new TextBox();

    @Override
    public String getName() {
        return "TextBox";
    }

    @Override
    public void initInputWidget() {
        textBox = new TextBox();
        textBox.setId( field.getId() );
        textBox.setPlaceholder( field.getPlaceHolder() );
        textBox.setMaxLength( field.getMaxLength() );
        textBox.setEnabled( !field.getReadonly() );
    }

    @Override
    public IsWidget getInputWidget() {
        return textBox;
    }

    @Override
    public IsWidget getPrettyViewWidget() {
        return new HTML();
    }

    @Override
    public String getSupportedCode() {
        return TextBoxBase.CODE;
    }

    @Override
    protected void setReadOnly( boolean readOnly ) {
        textBox.setEnabled( !readOnly );
    }
}
