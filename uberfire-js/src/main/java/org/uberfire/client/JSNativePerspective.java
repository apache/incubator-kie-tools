package org.uberfire.client;

import java.util.Collection;
import java.util.Collections;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.perspective.JSPanelDefinition;
import org.uberfire.client.perspective.JSPartDefinition;
import org.uberfire.client.workbench.widgets.panels.PanelManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.services.WorkbenchServices;

@Dependent
public class JSNativePerspective {

    @Inject
    private PanelManager panelManager;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<WorkbenchServices> wbServices;

    private JavaScriptObject obj;
    private PerspectiveDefinition perspectiveDefinition = null;

    private static final Collection<String> ROLES = Collections.emptyList();

    private static final Collection<String> TRAITS = Collections.emptyList();

    public void build( final JavaScriptObject obj ) {
        if ( this.obj != null ) {
            throw new RuntimeException( "Can't build more than once." );
        }
        this.obj = obj;
        buildPerspective();
    }

    public native String getId()  /*-{
        return this.@org.uberfire.client.JSNativePerspective::obj.id;
    }-*/;

    public void onReveal() {
        if ( JSNativePlugin.hasMethod( obj, "on_reveal" ) ) {
            executeOnReveal( obj );
        }
    }

    public void onClose() {
        if ( JSNativePlugin.hasMethod( obj, "on_close" ) ) {
            executeOnClose( obj );
        }
    }

    public Collection<String> getRoles() {
        return ROLES;
    }

    public Collection<String> getTraits() {
        return TRAITS;
    }

    public PerspectiveDefinition getPerspectiveDefinition() {
        return perspectiveDefinition;
    }

    private void buildPerspective() {
        perspectiveDefinition = new PerspectiveDefinitionImpl();
        perspectiveDefinition.setName( getId() );

        final JSPanelDefinition view = getView( obj );
        final JsArray<JSPartDefinition> parts = view.getParts();
        final JsArray<JSPanelDefinition> panels = view.getChildren();

        final PanelDefinition root = perspectiveDefinition.getRoot();

        buildParts( root, parts );
        buildPanels( root, panels );
    }

    private void buildParts( final PanelDefinition panel,
                             final JsArray<JSPartDefinition> parts ) {
        if ( parts != null ) {
            for ( int i = 0; i < parts.length(); i++ ) {
                final JSPartDefinition part = parts.get( i );
                final PlaceRequest placeRequest = new DefaultPlaceRequest( part.getPlaceName() );
                panel.addPart( new PartDefinitionImpl( placeRequest ) );
            }
        }
    }

    private void buildPanels( final PanelDefinition panel,
                              final JsArray<JSPanelDefinition> panels ) {
        if ( panels != null ) {
            for ( int i = 0; i < panels.length(); i++ ) {
                final JSPanelDefinition activePanelDef = panels.get( i );
                final PanelDefinition newPanel = new PanelDefinitionImpl();
                if ( activePanelDef.getWidth() > 0 ) {
                    newPanel.setWidth( activePanelDef.getWidth() );
                }

//                newPanel.setMinWidth( activePanelDef.getMinWidth() );
//                newPanel.setHeight( activePanelDef.getHeight() );

                buildParts( newPanel, activePanelDef.getParts() );

                buildPanels( newPanel, activePanelDef.getChildren() );

                panel.insertChild( Position.valueOf( activePanelDef.getPosition().toUpperCase() ), newPanel );
            }
        }
    }

    private static native JSPanelDefinition getView( final JavaScriptObject o ) /*-{
        return o.view;
    }-*/;

    private static native void executeOnReveal( final JavaScriptObject o ) /*-{
        o.on_reveal();
    }-*/;

    private static native void executeOnClose( final JavaScriptObject o ) /*-{
        o.on_close();
    }-*/;

    public void onStart() {
        //To change body of created methods use File | Settings | File Templates.
    }

    public void onStart( final PlaceRequest place ) {
        //To change body of created methods use File | Settings | File Templates.
    }

    public PanelManager getPanelManager() {
        return panelManager;
    }

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    public Caller<WorkbenchServices> getWbServices() {
        return wbServices;
    }
}
