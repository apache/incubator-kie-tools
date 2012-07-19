/*
 * Copyright 2012 JBoss Inc
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
package org.drools.guvnor.client.mvp;

import javax.inject.Inject;

import org.drools.guvnor.client.workbench.Position;
import org.drools.guvnor.vfs.Path;
import org.drools.guvnor.vfs.impl.PathImpl;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * 
 */
public abstract class AbstractEditorActivity
    implements
    WorkbenchActivity {

    @Inject
    private PlaceManager  placeManager;

    private EditorService presenter;

    @Override
    public Position getDefaultPosition() {
        return Position.ROOT;
    }

    @Override
    public boolean mayStop() {
        if ( presenter != null ) {
            return presenter.onMayClose();
        }
        return true;
    }

    @Override
    public void onStop() {
        presenter.onClose();
    }

    @Override
    public boolean mayClosePlace() {
        if ( presenter != null ) {
            return presenter.onMayClose();
        }
        return true;
    }

    @Override
    public void onClosePlace() {
        if ( presenter == null ) {
            return;
        }
        presenter.onClose();
        presenter = null;
    }

    @Override
    public void onRevealPlace(AcceptItem acceptPanel) {
        if ( presenter == null ) {
            presenter = getPresenter();
            if ( presenter == null ) {
                return;
            }

            IPlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
            String uri = placeRequest.getParameter( "path",
                                                    null );
            Path path = new PathImpl( uri );
            ((EditorService) presenter).onStart( path );
        }

        acceptPanel.add( getTitle(),
                         getWidget() );
        presenter.onReveal();
    }

    public abstract EditorService getPresenter();

    public abstract String getTitle();

    //TODO {manstis} This can be deleted once the static popup menu is removed
    public abstract String getIdentifier();

    public abstract IsWidget getWidget();

    @Override
    public void onLostFocus() {
        presenter.onLostFocus();
    }

    @Override
    public void onFocus() {
        presenter.onFocus();
    }

}
