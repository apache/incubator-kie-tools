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
import org.gwtbootstrap3.client.ui.TextArea;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.model.impl.basic.textArea.TextAreaFieldDefinition;

@Dependent
public class TextAreaFieldRenderer extends FieldRenderer<TextAreaFieldDefinition> {

    @Override
    public String getName() {
        return "TextArea";
    }

    private TextArea textArea = new TextArea();

    @Override
    public void initInputWidget() {
        textArea = new TextArea();
        textArea.setPlaceholder( field.getPlaceHolder() );
        textArea.setVisibleLines( field.getRows() );
        textArea.setEnabled( !field.getReadonly() );
        textArea.setVisibleLines( field.getRows() );
    }

    @Override
    public IsWidget getInputWidget() {
        return textArea;
    }

    @Override
    public IsWidget getPrettyViewWidget() {
        return new HTML();
    }

    @Override
    public String getSupportedCode() {
        return TextAreaFieldDefinition.CODE;
    }

    @Override
    protected void setReadOnly( boolean readOnly ) {
        textArea.setEnabled( !readOnly );
    }
}
