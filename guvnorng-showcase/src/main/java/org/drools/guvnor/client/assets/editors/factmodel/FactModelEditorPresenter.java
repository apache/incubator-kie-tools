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

import org.drools.guvnor.client.mvp.EditorService;
import org.drools.guvnor.client.mvp.PlaceManager;
import org.drools.guvnor.shared.common.vo.assets.factmodel.FactModels;
import org.drools.guvnor.vfs.VFSService;
import org.drools.java.nio.file.ExtendedPath;
import org.jboss.errai.ioc.client.api.Caller;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * 
 */
@Dependent
public class FactModelEditorPresenter
    implements
    EditorService {

    @Inject
    View                 view;

    @Inject
    Caller<VFSService>   vfsServices;

    @Inject
    private PlaceManager placeManager;

    private ExtendedPath path = null;

    public interface View
        extends
        IsWidget {

        void setContent(FactModels asset);

        FactModels getContent();

        void setFocus();

        void setDirty(boolean dirty);

        boolean isDirty();
    }

    @Override
    public void onStart() {

        //TODO {manstis} Need to load the FactModels object from the backend. See FileExplorerActivity
        //for more detail as to how this might work. If the AbstractAsset is passed as a parameter in
        //the Place then the following can be used to extract the asset to be edited.
        //- PlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        //- final FactModels asset = placeRequest.getParameter( "asset", null );
        FactModels asset = new FactModels();
        view.setContent( asset );

        //        PlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        //        final String uriPath = placeRequest.getParameter( "path",
        //                                                          null );
        //        vfsServices.call( new RemoteCallback<ExtendedPath>() {
        //            @Override
        //            public void callback(ExtendedPath extendedPath) {
        //                vfsServices.call( new RemoteCallback<String>() {
        //                    @Override
        //                    public void callback(String response) {
        //                        if ( response == null ) {
        //                            view.setContent( "-- empty --" );
        //                        } else {
        //                            view.setContent( response );
        //                        }
        //                    }
        //                } ).readAllString( extendedPath );
        //            }
        //        } ).get( uriPath );
    }

    public void doSave() {
        //TODO {manstis} Need to save the FactModels object to the backend
        //        vfsServices.call( new RemoteCallback<ExtendedPath>() {
        //            @Override
        //            public void callback(ExtendedPath response) {
        //                view.setDirty( false );
        //            }
        //        } ).write( path,
        //                   view.getContent() );
    }

    @Override
    public boolean isDirty() {
        return view.isDirty();
    }

    @Override
    public void onClose() {
        this.path = null;
    }

    @Override
    public boolean mayClose() {
        return false;
    }

    @Override
    public void onReveal() {
        view.setFocus();
    }

    @Override
    public void onHide() {
    }

    @Override
    public void mayOnHide() {
    }

}
