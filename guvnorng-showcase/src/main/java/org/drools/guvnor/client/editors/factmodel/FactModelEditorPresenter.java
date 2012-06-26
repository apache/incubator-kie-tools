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
package org.drools.guvnor.client.editors.factmodel;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.EditorScreenService;
import org.drools.guvnor.shared.AssetService;
import org.drools.guvnor.shared.common.vo.asset.AbstractAsset;
import org.drools.guvnor.shared.common.vo.assets.factmodel.FactModels;
import org.drools.guvnor.vfs.Path;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * 
 */
@Dependent
public class FactModelEditorPresenter
    implements
    EditorScreenService {

    @Inject
    View                 view;

    @Inject
    Caller<AssetService> assetService;

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
    public void onStart(Path path) {
        assetService.call( new RemoteCallback<AbstractAsset>() {
            @Override
            public void callback(AbstractAsset response) {
                view.setContent( (FactModels) response );
            }
        } ).loadAsset( path,
                       "model.drl" );
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
    }

    @Override
    public boolean mayClose() {
        return Window.confirm( "Are you sure you want to close?" );
    }

    @Override
    public void onReveal() {
        view.setFocus();
    }

    @Override
    public void onHide() {
    }

    @Override
    public boolean mayHide() {
        return true;
    }

}
