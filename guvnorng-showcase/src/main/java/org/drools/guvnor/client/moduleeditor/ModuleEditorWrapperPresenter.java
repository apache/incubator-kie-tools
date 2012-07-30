/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.moduleeditor;


import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.shared.ModuleService;
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
import org.uberfire.client.common.LoadingPopup;

/**
 * This is the module editor.
 */
@Dependent
@WorkbenchEditor(identifier = "RepositoryEditor")
public class ModuleEditorWrapperPresenter extends Composite {
    private ConstantsCore constants = GWT.create(ConstantsCore.class);

    private Module artifact;
    private boolean isHistoricalReadOnly = false;


    @Inject
    View                 view;

    @Inject
    Caller<ModuleService> moduleService;
    
    
    public interface View
        extends
        IsWidget {

        void setFocus();

        void setDirty(boolean dirty);

        boolean isDirty();
    }
    
/*    public ModuleEditorWrapperPresenter(Module data) {
        this(data, false);
    }

    public ModuleEditorWrapperPresenter(Module data,
                               boolean isHistoricalReadOnly) {
        this.artifact = data;
        this.isHistoricalReadOnly = isHistoricalReadOnly;
    }*/

    @OnStart
    public void onStart(Path path) {
        //this.path = path;
        moduleService.call( new RemoteCallback<Module>() {
            @Override
            public void callback(Module response) {
            	LoadingPopup.close();
            	artifact = response;
            	//render();
            }
        } ).loadModule( path.getFileName() );
    }    
    
/*    *//**
     * Will refresh all the data.
     *//*
    public void refresh() {
        LoadingPopup.showMessage(constants.RefreshingPackageData());
        moduleService.call( new RemoteCallback<Module>() {
            @Override
            public void callback(Module data) {
                LoadingPopup.close();
                packageConfigData = data;
                render();
            }
        } ).loadModule(this.packageConfigData.getUuid());
    }*/
    
    //JLIU: TODO: refresh handler
/*    private void setRefreshHandler() {
        eventBus.addHandler(RefreshModuleEditorEvent.TYPE,
                new RefreshModuleEditorEvent.Handler() {
                    public void onRefreshModule(
                            RefreshModuleEditorEvent refreshModuleEditorEvent) {
                        String moduleUUID = refreshModuleEditorEvent.getUuid();
                        if(moduleUUID!=null && moduleUUID.equals(packageConfigData.getUuid())) {
                            refresh();                                
                        }
                    
                    }
                });
    }*/

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
        return "Module Editor []";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }
}
