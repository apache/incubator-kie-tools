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

package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.options;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.kie.workbench.common.screens.datamodeller.client.handlers.DomainHandler;
import org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.JPADomainHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.ResourceOptions;
import org.uberfire.client.views.pfly.widgets.HelpIcon;

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

    @UiField
    CheckBox audited;

    @UiField
    HelpIcon auditedHelpIcon;

    @Inject
    private JPADomainHandler handler;

    public JPANewResourceOptions() {
        initWidget( uiBinder.createAndBindUi( this ) );

        setAuditOptionsVisible( false );
        persistable.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                onPersistableChanged();
            }
        } );

    }

    public boolean isPersitable() {
        return persistable.getValue();
    }

    public void setPersistable( boolean persistable ) {
        this.persistable.setValue( persistable );
    }

    public boolean getAudited() {
        return audited.getValue();
    }

    public void setAudited( boolean audited ) {
        this.audited.setValue( audited );
    }

    @Override
    public void restoreOptionsDefaults() {
        setPersistable( false );
        setAudited( false );
        setAuditOptionsVisible( false );
    }

    @Override
    public Map<String, Object> getOptions() {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put( "persistable", isPersitable() );
        if ( isPersitable() && handler.isDataObjectAuditEnabled() ) {
            options.put( "audited", getAudited() );
        }
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

    private void onPersistableChanged() {
        if ( isPersitable() && handler.isDataObjectAuditEnabled() ) {
            setAuditOptionsVisible( true );
        } else {
            setAudited( false );
            setAuditOptionsVisible( false );
        }
    }

    private void setAuditOptionsVisible( boolean visible ) {
        audited.setVisible( visible );
        auditedHelpIcon.setVisible( visible );

    }
}