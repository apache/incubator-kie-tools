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

package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.options;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.screens.datamodeller.client.handlers.DomainHandler;
import org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.JPADomainHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.ResourceOptions;

@Dependent
public class JPANewResourceOptions
        extends Composite
        implements ResourceOptions {

    interface JPANewResourceOptionsUIBinder
            extends
            UiBinder<Widget, JPANewResourceOptions> {

    }

    private static JPANewResourceOptionsUIBinder uiBinder = GWT.create( JPANewResourceOptionsUIBinder.class );

    @UiField
    CheckBox persistable;

    //@UiField
    TextBox tableName = new TextBox();

    //@UiField
    InlineLabel tableNameLabel;

    //@UiField
    HelpBlock tableNameHelpInline;

    @Inject
    private JPADomainHandler handler;

    public JPANewResourceOptions() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public boolean isPersitable() {
        return persistable.getValue();
    }

    public void setPersistable( boolean persistable ) {
        this.persistable.setValue( persistable );
    }

    public String getTableName() {
        return tableName.getValue();
    }

    public void setTableName( String tableName ) {
        this.tableName.setText( tableName );
    }

    @Override
    public void restoreOptionsDefaults() {
        setPersistable( false );
        setTableName( null );
    }

    @Override
    public Map<String, Object> getOptions() {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put( "persistable", isPersitable() );
        options.put( "tableName", getTableName() );
        return options;
    }

    @Override
    public DomainHandler getHandler() {
        return handler;
    }

    @Override
    public Widget getWidget() {
        return super.asWidget();
    }
}
