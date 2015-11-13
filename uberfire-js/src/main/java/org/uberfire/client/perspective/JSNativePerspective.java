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

package org.uberfire.client.perspective;

import java.util.Collection;
import java.util.Collections;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.jsapi.JSPlaceRequest;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.plugin.JSNativePlugin;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.ContextDisplayMode;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.ContextDefinitionImpl;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;

@Dependent
public class JSNativePerspective {

    @Inject
    private PanelManager panelManager;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private WorkbenchServicesProxy wbServices;

    private JavaScriptObject obj;

    private static final Collection<String> ROLES = Collections.emptyList();

    private static final Collection<String> TRAITS = Collections.emptyList();

    public void build( final JavaScriptObject obj ) {
        if ( this.obj != null ) {
            throw new RuntimeException( "Can't build more than once." );
        }
        this.obj = obj;
    }

    public native String getId()  /*-{
        return this.@org.uberfire.client.perspective.JSNativePerspective::obj.id;
    }-*/;

    public native boolean isDefault()  /*-{
        if ((typeof this.@org.uberfire.client.perspective.JSNativePerspective::obj.is_default === "boolean")) {
            return this.@org.uberfire.client.perspective.JSNativePerspective::obj.is_default;
        }
        return false;
    }-*/;

    public native boolean isTransient()  /*-{
        var jso = this.@org.uberfire.client.perspective.JSNativePerspective::obj;
        if ((typeof jso.is_transient === "boolean")) {
            return jso.is_transient;
        }
        return true;
    }-*/;

    private native String getPanelTypeAsString()  /*-{
        return this.@org.uberfire.client.perspective.JSNativePerspective::obj.panel_type;
    }-*/;

    private native String getContextDisplayModeAsString()  /*-{
        return this.@org.uberfire.client.perspective.JSNativePerspective::obj.context_display_mode;
    }-*/;

    private native String getContextId()  /*-{
        return this.@org.uberfire.client.perspective.JSNativePerspective::obj.context_id;
    }-*/;

    public void onStartup( final PlaceRequest place ) {
        if ( JSNativePlugin.hasMethod( obj, "on_startup" ) ) {
            executeOnStartup( obj, JSPlaceRequest.fromPlaceRequest( place ) );
        }
    }

    public void onOpen() {
        if ( JSNativePlugin.hasMethod( obj, "on_open" ) ) {
            executeOnOpen( obj );
        }
    }

    public void onClose() {
        if ( JSNativePlugin.hasMethod( obj, "on_close" ) ) {
            executeOnClose( obj );
        }
    }

    public void onShutdown() {
        if ( JSNativePlugin.hasMethod( obj, "on_shutdown" ) ) {
            executeOnShutdown( obj );
        }
    }

    public Collection<String> getRoles() {
        return ROLES;
    }

    public Collection<String> getTraits() {
        return TRAITS;
    }

    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition perspectiveDefinition = new PerspectiveDefinitionImpl( getDefaultPanelType() );
        perspectiveDefinition.setName( getId() );
        final String contextId = getContextId();
        if ( contextId != null ) {
            perspectiveDefinition.setContextDefinition( new ContextDefinitionImpl( new DefaultPlaceRequest( contextId ) ) );
        }
        perspectiveDefinition.setContextDisplayMode( getContextDisplayMode() );

        final JSPanelDefinition view = getView( obj );

        final JsArray<JSPartDefinition> parts = view.getParts();
        final JsArray<JSPanelDefinition> panels = view.getChildren();

        final PanelDefinition root = perspectiveDefinition.getRoot();

        buildParts( root, parts );
        buildPanels( root, panels );

        return perspectiveDefinition;
    }

    private String getDefaultPanelType() {
        return getPanelType( getPanelTypeAsString(), MultiTabWorkbenchPanelPresenter.class.getName() );
    }

    private ContextDisplayMode getContextDisplayMode() {
        return getContextDisplayMode( getContextDisplayModeAsString(), ContextDisplayMode.SHOW );
    }

    private ContextDisplayMode getContextDisplayMode( final String contextDisplayMode,
                                                      final ContextDisplayMode defaultType ) {
        if ( contextDisplayMode == null ) {
            return defaultType;
        }

        try {
            return ContextDisplayMode.valueOf( contextDisplayMode.toUpperCase() );
        } catch ( Exception ex ) {
            return defaultType;
        }
    }

    private String getPanelType( final String panelType,
                                 final String defaultType ) {
        if ( panelType == null ) {
            return defaultType;
        }
        return panelType;
    }

    private void buildParts( final PanelDefinition panel,
                             final JsArray<JSPartDefinition> parts ) {
        if ( parts != null ) {
            for ( int i = 0; i < parts.length(); i++ ) {
                final JSPartDefinition part = parts.get( i );
                final PlaceRequest placeRequest = new DefaultPlaceRequest( part.getPlaceName() );

                if ( part.getParameters() != null ) {
                    final JSONObject json = new JSONObject( part.getParameters() );
                    for ( final String key : json.keySet() ) {
                        placeRequest.addParameter( key, json.get( key ).isString().stringValue() );
                    }
                }

                final PartDefinition partDefinition = new PartDefinitionImpl( placeRequest );
                partDefinition.setContextDisplayMode( JSNativePerspective.this.getContextDisplayMode( part.getContextDisplayModeAsString(), ContextDisplayMode.SHOW ) );
                if ( part.getContextId() != null ) {
                    partDefinition.setContextDefinition( new ContextDefinitionImpl( new DefaultPlaceRequest( part.getContextId() ) ) );
                }

                panel.addPart( partDefinition );
            }
        }
    }

    private void buildPanels( final PanelDefinition panel,
                              final JsArray<JSPanelDefinition> panels ) {
        if ( panels != null ) {
            for ( int i = 0; i < panels.length(); i++ ) {
                final JSPanelDefinition activePanelDef = panels.get( i );

                final PanelDefinition newPanel = new PanelDefinitionImpl( getPanelType( activePanelDef.getPanelTypeAsString(), MultiTabWorkbenchPanelPresenter.class.getName() ) );

                newPanel.setContextDisplayMode( JSNativePerspective.this.getContextDisplayMode( activePanelDef.getContextDisplayModeAsString(), ContextDisplayMode.SHOW ) );
                if ( activePanelDef.getContextId() != null ) {
                    newPanel.setContextDefinition( new ContextDefinitionImpl( new DefaultPlaceRequest( activePanelDef.getContextId() ) ) );
                }

                if ( activePanelDef.getWidth() > 0 ) {
                    newPanel.setWidth( activePanelDef.getWidth() );
                }

                if ( activePanelDef.getMinWidth() > 0 ) {
                    newPanel.setMinWidth( activePanelDef.getMinWidth() );
                }

                if ( activePanelDef.getHeight() > 0 ) {
                    newPanel.setHeight( activePanelDef.getHeight() );
                }

                if ( activePanelDef.getMinHeight() > 0 ) {
                    newPanel.setHeight( activePanelDef.getMinHeight() );
                }

                buildParts( newPanel, activePanelDef.getParts() );

                buildPanels( newPanel, activePanelDef.getChildren() );

                panel.insertChild( CompassPosition.valueOf( activePanelDef.getPosition().toUpperCase() ), newPanel );
            }
        }
    }

    private static native JSPanelDefinition getView( final JavaScriptObject o ) /*-{
        return o.view;
    }-*/;

    private static native void executeOnStartup( final JavaScriptObject o, JSPlaceRequest place ) /*-{
        o.on_open( place );
    }-*/;

    private static native void executeOnOpen( final JavaScriptObject o ) /*-{
        o.on_open();
    }-*/;

    private static native void executeOnClose( final JavaScriptObject o ) /*-{
        o.on_close();
    }-*/;

    private static native void executeOnShutdown( final JavaScriptObject o ) /*-{
        o.on_shutdown();
    }-*/;

    public PanelManager getPanelManager() {
        return panelManager;
    }

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    public WorkbenchServicesProxy getWbServices() {
        return wbServices;
    }
}
