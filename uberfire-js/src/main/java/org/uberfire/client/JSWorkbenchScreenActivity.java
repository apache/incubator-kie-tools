package org.uberfire.client;

import java.util.Collection;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

public class JSWorkbenchScreenActivity implements WorkbenchScreenActivity {

    private final PlaceManager placeManager;

    private PlaceRequest place;

    private final JSNativePlugin nativePlugin;

    public JSWorkbenchScreenActivity( final JSNativePlugin nativePlugin,
                                      final PlaceManager placeManager ) {
        this.nativePlugin = nativePlugin;
        this.placeManager = placeManager;
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        this.place = place;
        nativePlugin.onStartup( place );

        if ( nativePlugin.getType() != null && nativePlugin.getType().equalsIgnoreCase( "angularjs" ) ) {
            bind();
        }
    }

    @Override
    public boolean onMayClose() {
        return nativePlugin.onMayClose();
    }

    @Override
    public void onClose() {
        nativePlugin.onClose();
    }

    @Override
    public void onShutdown() {
        nativePlugin.onShutdown();
    }

    @Override
    public Position getDefaultPosition() {
        return CompassPosition.ROOT;
    }

    @Override
    public void onFocus() {
        nativePlugin.onFocus();
    }

    @Override
    public void onLostFocus() {
        nativePlugin.onLostFocus();
    }

    @Override
    public String getTitle() {
        return nativePlugin.getTitle();
    }

    @Override
    public IsWidget getTitleDecoration() {
        return null;
    }

    @Override
    public IsWidget getWidget() {
        return new HTML( nativePlugin.getElement().getInnerHTML() );
    }

    @Override
    public Menus getMenus() {
        return null;
    }

    @Override
    public ToolBar getToolBar() {
        return null;
    }

    @Override
    public void onOpen() {
        nativePlugin.onOpen();
        placeManager.executeOnOpenCallback( this.place );
    }

    @Override
    public String getSignatureId() {
        return nativePlugin.getId();
    }

    @Override
    public Collection<String> getRoles() {
        return nativePlugin.getRoles();
    }

    @Override
    public Collection<String> getTraits() {
        return nativePlugin.getTraits();
    }

    // Alias registerPlugin with a global JS function.
    private native String bind() /*-{
        $wnd.angular.bootstrap($wnd.document, []);
    }-*/;

    @Override
    public String contextId() {
        return nativePlugin.getContextId();
    }
}
