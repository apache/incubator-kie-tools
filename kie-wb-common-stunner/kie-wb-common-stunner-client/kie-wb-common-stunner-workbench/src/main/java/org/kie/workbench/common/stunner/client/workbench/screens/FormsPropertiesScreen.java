/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.workbench.screens;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.BindableProxyFactory;
import org.jboss.errai.databinding.client.HasProperties;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.factory.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.session.CanvasSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDisposedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionPausedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionResumedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasFullSession;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.uberfire.client.annotations.*;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@WorkbenchScreen( identifier = FormsPropertiesScreen.SCREEN_ID )
public class FormsPropertiesScreen {

    public static final String SCREEN_ID = "FormsPropertiesScreen";

    @Inject
    ClientDefinitionManager clientDefinitionManager;

    @Inject
    CanvasCommandFactory commandFactory;

    @Inject
    private DynamicFormRenderer formRenderer;

    @Inject
    ErrorPopupPresenter errorPopupPresenter;

    @Inject
    PlaceManager placeManager;

    @Inject
    Event<ChangeTitleWidgetEvent> changeTitleNotification;

    private PlaceRequest placeRequest;
    private DefaultCanvasFullSession canvasSession;

    @PostConstruct
    public void init() {
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        this.placeRequest = placeRequest;
    }

    @OnOpen
    public void onOpen() {
    }

    @OnClose
    public void onClose() {
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return null;
    }

    private void showError( final String message ) {
        errorPopupPresenter.showMessage( message );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Properties";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return formRenderer.asWidget();
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "stunnerPropertiesScreenContext";
    }

    private AbstractCanvasHandler getCanvasHandler() {
        return canvasSession != null ? canvasSession.getCanvasHandler() : null;
    }

    @SuppressWarnings( "unchecked" )
    void onCanvasElementSelectedEvent( @Observes CanvasElementSelectedEvent event ) {
        checkNotNull( "event", event );
        if ( null != getCanvasHandler() ) {
            final String uuid = event.getElementUUID();
            final Element<? extends View<?>> element = null != uuid ? getCanvasHandler().getGraphIndex().get( uuid ) : null;
            if ( null != element ) {
                final Object definition = element.getContent().getDefinition();
                BindableProxy proxy = ( BindableProxy ) BindableProxyFactory.getBindableProxy( definition );
                formRenderer.renderDefaultForm( proxy.deepUnwrap(), () -> {
                    formRenderer.addFieldChangeHandler( ( fieldName, newValue ) -> {
                        try {
                            // TODO - Pere: We have to review this. Meanwhile, note that this is working only for properties
                            // that are direct members of the definitions ( ex: Task#width or StartEvent#radius ).
                            // But it's not working for the properties that are inside property sets, for example an error
                            // occurs when updating "documentation", as thisl callback "fieldName" = "documentation", but
                            // in order to obtain the property it should be "general.documentation".
                            final HasProperties hasProperties = ( HasProperties ) DataBinder.forModel( definition ).getModel();
                            String pId = getModifiedPropertyId( hasProperties, fieldName );
                            FormsPropertiesScreen.this.executeUpdateProperty( element, pId, newValue );

                        } catch ( Exception ex ) {
                            GWT.log( "Something wrong happened refreshing the canvas for field '" + fieldName + "': " + ex.getCause() );
                        }
                    } );
                } );

            } else {
                doClear();

            }

        }

    }

    void CanvasClearSelectionEvent( @Observes CanvasClearSelectionEvent clearSelectionEvent ) {
        checkNotNull( "clearSelectionEvent", clearSelectionEvent );
        doClear();
    }

    private String getModifiedPropertyId( HasProperties model, String fieldName ) {
        int separatorIndex = fieldName.indexOf( "." );
        // Check if it is a nested property, if it is we must obtain the nested property instead of the root one.
        if ( separatorIndex != -1 ) {
            String rootProperty = fieldName.substring( 0, separatorIndex );
            fieldName = fieldName.substring( separatorIndex + 1 );
            Object property = model.get( rootProperty );
            model = ( HasProperties ) DataBinder.forModel( property ).getModel();
            return getModifiedPropertyId( model, fieldName );
        }
        Object property = model.get( fieldName );
        return clientDefinitionManager.adapters().forProperty().getId( property );
    }

    void onCanvasSessionOpened( @Observes SessionOpenedEvent sessionOpenedEvent ) {
        checkNotNull( "sessionOpenedEvent", sessionOpenedEvent );
        doOpenSession( sessionOpenedEvent.getSession() );
    }

    void onCanvasSessionResumed( @Observes SessionResumedEvent sessionResumedEvent ) {
        checkNotNull( "sessionResumedEvent", sessionResumedEvent );
        doOpenSession( sessionResumedEvent.getSession() );
    }

    void onCanvasSessionDisposed( @Observes SessionDisposedEvent sessionDisposedEvent ) {
        checkNotNull( "sessionDisposedEvent", sessionDisposedEvent );
        doCloseSession();
    }

    void onCanvasSessionPaused( @Observes SessionPausedEvent sessionPausedEvent ) {
        checkNotNull( "sessionPausedEvent", sessionPausedEvent );
        doCloseSession();
    }

    private void doOpenSession( final CanvasSession session ) {
        this.canvasSession = ( DefaultCanvasFullSession ) session;
    }

    private void doCloseSession() {
        this.canvasSession = null;
        doClear();
    }

    private void doClear() {
        // TODO formRenderer.unBind();
        // changeTitleNotification.fire(new ChangeTitleWidgetEvent(placeRequest, "Properties"));
    }

    private void executeUpdateProperty( final Element<? extends View<?>> element,
                                        final String propertyId,
                                        final Object value ) {
        final CanvasCommandManager<AbstractCanvasHandler> commandManager = canvasSession.getCanvasCommandManager();
        commandManager.execute( getCanvasHandler(), commandFactory.UPDATE_PROPERTY( element, propertyId, value ) );

    }

    private void executeMove( final Element<? extends View<?>> element,
                              final double x,
                              final double y ) {
        final CanvasCommandManager<AbstractCanvasHandler> commandManager = canvasSession.getCanvasCommandManager();
        commandManager.execute( getCanvasHandler(), commandFactory.UPDATE_POSITION( element, x, y ) );

    }

}
