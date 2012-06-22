/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
public abstract class AbstractEditorScreenActivity
    implements
    Activity {

    @Inject
    private PlaceManager        placeManager;

    private EditorScreenService presenter;

    @Override
    public void start() {
    }

    @Override
    public Position getPreferredPosition() {
        return Position.ROOT;
    }

    public void onStop() {
        presenter.onClose();
    }

    public boolean mayStop() {
        if ( presenter != null ) {
            return presenter.mayClose();
        }
        return true;
    }

    public void revealPlace(AcceptItem acceptPanel) {
        if ( presenter == null ) {
            presenter = getPresenter();

            IPlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
            String uri = placeRequest.getParameter( "path",
                                                    null );
            Path path = new PathImpl( uri );
            ((EditorScreenService) presenter).onStart( path );
        }
        if ( presenter == null ) {
            return;
        }

        acceptPanel.add( getTitle(),
                         getWidget() );
        presenter.onReveal();
    }

    public abstract EditorScreenService getPresenter();

    public abstract String getTitle();

    public abstract IsWidget getWidget();

    @Override
    public boolean mayClosePlace() {
        if ( presenter != null ) {
            return presenter.mayClose();
        }
        return true;
    }

    @Override
    public void closePlace() {
        if ( presenter == null ) {
            return;
        }
        presenter.onClose();
        presenter = null;
    }

    @Override
    public void hide() {
    }

    @Override
    public void show() {
    }

}
