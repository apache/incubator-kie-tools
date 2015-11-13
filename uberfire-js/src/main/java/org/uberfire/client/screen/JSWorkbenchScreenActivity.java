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

package org.uberfire.client.screen;

import java.util.Collection;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

public class JSWorkbenchScreenActivity implements WorkbenchScreenActivity {

    private final PlaceManager placeManager;

    private PlaceRequest place;

    private final JSNativeScreen nativePlugin;

    public JSWorkbenchScreenActivity( final JSNativeScreen nativePlugin,
                                      final PlaceManager placeManager ) {
        this.nativePlugin = nativePlugin;
        this.placeManager = placeManager;
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        this.place = place;
        nativePlugin.onStartup( place );
    }

    @Override
    public PlaceRequest getPlace() {
        return place;
    }

    @Override
    public String getIdentifier() {
        return nativePlugin.getId();
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
    public PlaceRequest getOwningPlace() {
        return null;
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
        if ( nativePlugin.getType() != null && nativePlugin.getType().equalsIgnoreCase( "angularjs" ) ) {
            bind();
        }
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

    @Override
    public Integer preferredHeight() {
        return null;
    }

    @Override
    public Integer preferredWidth() {
        return null;
    }
}
