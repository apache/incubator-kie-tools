/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.handlers;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.InlineLabel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

@ApplicationScoped
public class JPADomainOptionsHandler
        extends Composite
        implements DomainOptionsHandler {

    interface JavaFileOptionsUIBinder
        extends
            UiBinder<Widget, JPADomainOptionsHandler> {

    }

    private static JavaFileOptionsUIBinder uiBinder = GWT.create( JavaFileOptionsUIBinder.class );

    @UiField CheckBox persistable;

    @UiField
    Icon persistableHelpIcon;

    //@UiField
    TextBox tableName = new TextBox();

    //@UiField
    InlineLabel tableNameLabel;

    //@UiField
    HelpInline tableNameHelpInline;

    public JPADomainOptionsHandler() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public boolean isPersitable() {
        return persistable.getValue();
    }

    public String getTableName() {
        return tableName.getValue();
    }

    @Override
    public String getName() {
        return "JPADomainOptionsHandler";
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public void restoreDefaults() {
        persistable.setValue( false );
        tableName.setText( "" );
    }

    @Override
    public Map<String, Object> getOptions() {
        Map<String, Object> options = new HashMap<String, Object>( );
        options.put( "persistable", isPersitable() );
        options.put( "tableName", getTableName() );
        return options;
    }

    @Override
    public Widget getWidget() {
        return this.asWidget();
    }
}
