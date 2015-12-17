/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.client.handlers.workbench.configuration;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox;
import org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel;

@Dependent
public class ConfigurationComboBoxItemWidget extends Composite {

    interface ConfigurationItemWidgetBinder extends UiBinder<Widget, ConfigurationComboBoxItemWidget> {

    }

    private static ConfigurationItemWidgetBinder uiBinder = GWT.create( ConfigurationItemWidgetBinder.class );

    @UiField
    PropertyEditorComboBox extensionItem;

    @UiField
    PropertyEditorItemLabel extensionItemLabel;

    private String widgetId;

    @PostConstruct
    private void setup() {
        super.initWidget( uiBinder.createAndBindUi( this ) );
    }

    public PropertyEditorComboBox getExtensionItem() {
        return this.extensionItem;
    }

    public PropertyEditorItemLabel getExtensionItemLabel() {
        return this.extensionItemLabel;
    }

    public Pair<String, String> getSelectedItem() {
        return extensionItem.getSelectedPair( extensionItem.getSelectedIndex() );
    }

    public void setSelectedItem( final String text ) {
        extensionItem.setSelectItemByText( text );
    }

    public void initExtensionItem( final List<Pair<String, String>> items ) {
        for ( Pair<String, String> p : items ) {
            extensionItem.addItem( p );
        }
    }

    public String getWidgetId() {
        return widgetId;
    }

    public void setWidgetId( final String widgetId ) {
        this.widgetId = widgetId;
    }

    public void clear() {
        extensionItem.clear();
    }
}