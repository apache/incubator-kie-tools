/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.core.client.actions;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen(identifier = "WiresActionsScreen")
public class ActionsScreen extends Composite {

    interface ViewBinder extends UiBinder<Widget, ActionsScreen> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    PanelGroup accordion;

    @UiField
    PanelHeader headerActions;

    @UiField
    PanelCollapse collapseActions;

    @UiField
    public SimplePanel actions;

    @Inject
    private SyncBeanManager iocManager;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );

        accordion.setId( DOM.createUniqueId() );
        headerActions.setDataParent( accordion.getId() );
        headerActions.setDataTargetWidget( collapseActions );
        
        actions.add( iocManager.lookupBean( ActionsGroup.class ).getInstance() );
    }

    @WorkbenchPartTitle
    @Override
    public String getTitle() {
        return "Actions";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return this;
    }

}
