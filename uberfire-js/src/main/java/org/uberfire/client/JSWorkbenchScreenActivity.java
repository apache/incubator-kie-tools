package org.uberfire.client;

import java.util.Collection;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.AcceptItem;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

public class JSWorkbenchScreenActivity implements WorkbenchScreenActivity {

    private PlaceManager placeManager;

    private PlaceRequest place;

    private Command callback;

    private JSNativePlugin nativePlugin;

    public JSWorkbenchScreenActivity( final JSNativePlugin nativePlugin,
                                      final PlaceManager placeManager ) {
        this.nativePlugin = nativePlugin;
        this.placeManager = placeManager;
    }

    @Override
    public void launch( final PlaceRequest place,
                        final Command callback ) {
        this.place = place;
        this.callback = callback;
    }

    @Override
    public void launch( final AcceptItem acceptPanel,
                        final PlaceRequest place,
                        final Command callback ) {
        launch( place, callback );
        onStartup( place );
        acceptPanel.add( new UIPart( getTitle(), getTitleDecoration(), getWidget() ) );

        if ( nativePlugin.getType() != null && nativePlugin.getType().equalsIgnoreCase( "angularjs" ) ) {
            bind();
        }

        onOpen();
    }

    @Override
    public void onStartup() {
        nativePlugin.onStartup();
    }

    @Override
    public void onStartup( final PlaceRequest place ) {
        nativePlugin.onStartup( place );
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
    public Position getDefaultPosition() {
        return Position.ROOT;
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

        executeOnRevealCallback();
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

    private void executeOnRevealCallback() {
        if ( callback != null ) {
            callback.execute();
        }
        placeManager.executeOnRevealCallback( this.place );
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
