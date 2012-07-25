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
package org.drools.guvnor.client.editors.enumeditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.shared.AssetService;
import org.drools.guvnor.shared.common.vo.asset.AbstractAsset;
import org.drools.guvnor.shared.common.vo.assets.enums.EnumModel;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.IsDirty;
import org.uberfire.client.annotations.OnFocus;
import org.uberfire.client.annotations.OnMayClose;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * 
 */
@Dependent
@WorkbenchEditor(identifier = "EnumEditor", fileTypes = "enumeration")
public class EnumEditorPresenter {

    @Inject
    View                 view;

    @Inject
    Caller<AssetService> assetService;

    private Path         path;

    public interface View
        extends
        IsWidget {

        void setContent(EnumModel content);

        EnumModel getContent();

        void setFocus();

        void setDirty(boolean dirty);

        boolean isDirty();
    }

    @OnStart
    public void onStart(Path path) {
        this.path = path;
        assetService.call( new RemoteCallback<AbstractAsset>() {
            @Override
            public void callback(AbstractAsset response) {
                view.setContent( (EnumModel) response );
            }
        } ).loadAsset( path,
                       "enumeration" );
    }

    @IsDirty
    public boolean isDirty() {
        return view.isDirty();
    }

    @OnMayClose
    public boolean onMayClose() {
        return Window.confirm( "Are you sure you want to close?" );
    }

    @OnReveal
    public void onReveal() {
        view.setFocus();
    }

    @OnFocus
    public void onFocus() {
        view.setFocus();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Enumeration Editor [" + path.toURI() + "]";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

}
