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
package org.drools.guvnor.client.assets.editors.factmodel;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.AcceptItem;
import org.drools.guvnor.client.mvp.Activity;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.mvp.PlaceManager;
import org.drools.guvnor.client.mvp.PlaceRequest;
import org.drools.guvnor.client.mvp.ScreenService;
import org.drools.guvnor.client.workbench.Position;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

/**
 * 
 */
@Dependent
@NameToken("FactModelEditor")
public class FactModelEditorActivity
    implements
    Activity {

    @Inject
    private IOCBeanManager           manager;

    @Inject
    private PlaceManager             placeManager;

    private FactModelEditorPresenter presenter;

    public FactModelEditorActivity() {
    }

    @Override
    public void start() {
    }

    @Override
    public Position getPreferredPosition() {
        return Position.SELF;
    }

    public void onStop() {
        if ( presenter != null && presenter instanceof ScreenService ) {
            ((ScreenService) presenter).onClose();
        }
    }

    public boolean mayStop() {
        if ( presenter != null && presenter instanceof ScreenService ) {
            return ((ScreenService) presenter).mayClose();
        }
        return true;
    }

    public void revealPlace(AcceptItem acceptPanel) {
        if ( presenter == null ) {
            presenter = manager.lookupBean( FactModelEditorPresenter.class ).getInstance();
            if ( presenter instanceof ScreenService ) {
                ((ScreenService) presenter).onStart();
            }
        }

        if ( presenter instanceof ScreenService ) {
            acceptPanel.add( getTabTitle(),
                             presenter.view );
            ((ScreenService) presenter).onReveal();
        }
    }

    private String getTabTitle() {
        PlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        final String uriPath = placeRequest.getParameter( "path",
                                                          null );
        return "FactModel Editor [" + uriPath + "]";
    }

    /**
     * True - Close the place False - Do not close the place
     */
    public boolean mayClosePlace() {
        if ( presenter instanceof ScreenService ) {
            return ((ScreenService) presenter).mayClose();
        }

        return true;
    }

    public void closePlace() {
        if ( presenter == null ) {
            return;
        }

        if ( presenter instanceof ScreenService ) {
            ((ScreenService) presenter).onClose();
        }
        presenter = null;
    }

    public String getNameToken() {
        return "FactModelEditor";
    }

}
