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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox;

import java.util.Map;
import java.util.Set;
import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.kie.workbench.common.forms.common.rendering.client.widgets.util.DefaultValueListBoxRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.SelectorFieldRenderer;
import org.kie.workbench.common.forms.model.impl.basic.selectors.SelectorOption;
import org.kie.workbench.common.forms.model.impl.basic.selectors.listBox.ListBoxBase;

@Dependent
public class ListBoxFieldRenderer<F extends ListBoxBase, O extends SelectorOption<T>, T>
        extends SelectorFieldRenderer<F, O, T> {

    protected DefaultValueListBoxRenderer<T> optionsRenderer = new DefaultValueListBoxRenderer();

    protected ValueListBox<T> widgetList = new ValueListBox<T>( optionsRenderer );

    @Override
    public String getName() {
        return "ListBox";
    }

    @Override
    protected void refreshInput( Map<T, String> optionsValues, T defaultValue ) {
        Set<T> values = optionsValues.keySet();

        if ( field.getRequired() ) {
            if ( defaultValue == null && !values.isEmpty() ) {
                defaultValue = values.iterator().next();
            }
        }

        if ( defaultValue != null ) {
            widgetList.setValue( defaultValue );
        }

        optionsRenderer.setValues( optionsValues );
        widgetList.setAcceptableValues( optionsValues.keySet() );
    }

    @Override
    public void initInputWidget() {
        widgetList.setEnabled( !field.getReadonly() );
        refreshSelectorOptions();
    }

    @Override
    public IsWidget getPrettyViewWidget() {
        return new HTML();
    }

    @Override
    public IsWidget getInputWidget() {
        return widgetList;
    }

    @Override
    public String getSupportedCode() {
        return ListBoxBase.CODE;
    }

    @Override
    protected void setReadOnly( boolean readOnly ) {
        widgetList.setEnabled( !readOnly );
    }
}
