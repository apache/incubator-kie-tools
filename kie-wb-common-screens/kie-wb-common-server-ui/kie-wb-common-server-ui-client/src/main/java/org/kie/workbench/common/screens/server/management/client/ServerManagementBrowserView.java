/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.client;

import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Description;
import org.gwtbootstrap3.client.ui.DescriptionData;
import org.gwtbootstrap3.client.ui.DescriptionTitle;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.screens.server.management.client.box.BoxPresenter;
import org.kie.workbench.common.screens.server.management.client.header.HeaderPresenter;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;

@Dependent
public class ServerManagementBrowserView extends Composite
        implements ServerManagementBrowserPresenter.View {

    interface Binder
            extends
            UiBinder<Widget, ServerManagementBrowserView> {

    }

    @UiField
    ListGroup list;

    @UiField
    PanelHeader header;

    private static Binder uiBinder = GWT.create( Binder.class );

    @PostConstruct
    public void setup() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setHeader( final HeaderPresenter header ) {
        this.header.add( header.getView() );
    }

    @Override
    public void addBox( final BoxPresenter container ) {
        list.add( container.getView().asWidget() );
    }

    @Override
    public void addBox( final BoxPresenter container,
                        final BoxPresenter parentContainer ) {
        list.insert( container.getView().asWidget(), list.getWidgetIndex( parentContainer.getView() ) + 1 );
    }

    @Override
    public void removeBox( final BoxPresenter value ) {
        list.remove( value.getView() );
    }

    @Override
    public void cleanup() {
        list.clear();
    }

    @Override
    public void confirmDeleteOperation( final Collection<String> serverNames,
                                        final Collection<List<String>> container2delete,
                                        final Command onConfirm ) {
        YesNoCancelPopup.newYesNoCancelPopup(
                "Delete",
                buildMessage( serverNames, container2delete ),
                new Command() {
                    @Override
                    public void execute() {
                        onConfirm.execute();
                    }
                },
                org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants.INSTANCE.YES(),
                ButtonType.DANGER,
                IconType.TRASH,

                new Command() {
                    @Override
                    public void execute() {
                    }
                },
                org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants.INSTANCE.NO(),
                ButtonType.DEFAULT, null, null, null, null, null ).show();

    }

    private String buildMessage( final Collection<String> serverNames,
                                 final Collection<List<String>> container2delete ) {
        final Description ds = new Description();
        final DescriptionTitle title = new DescriptionTitle();
        ds.add( title );
        if ( !serverNames.isEmpty() ) {
            title.setText( Constants.INSTANCE.confirm_delete_servers() );
            for ( final String s : serverNames ) {
                final DescriptionData server = new DescriptionData();
                server.setText( s );
                ds.add( server );
            }
        }
        if ( !container2delete.isEmpty() ) {
            if ( serverNames.isEmpty() ) {
                title.setText( Constants.INSTANCE.confirm_delete_containers() );
            } else {
                title.setText( Constants.INSTANCE.and_containers() );
            }
            for ( final List<String> entry : container2delete ) {
                for ( final String s : entry ) {
                    final DescriptionData server = new DescriptionData();
                    server.setText( s );
                    ds.add( server );
                }
            }
        }
        return ds.getElement().getInnerHTML();
    }
}
