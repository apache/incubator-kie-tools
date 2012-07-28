/*
 * Copyright 2010 JBoss Inc
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

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.widgets.MessageWidget;
import org.drools.guvnor.client.widgets.MetaDataWidget;


import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The editor for fact models (DRL declared types).
 */
public class ModuleEditorWrapperView extends Composite
    implements
    RequiresResize,
    ModuleEditorWrapperPresenter.View {

	private boolean                   isDirty             = false;
    private ConstantsCore constants = GWT.create(ConstantsCore.class);

    private Module packageConfigData;
    private boolean isHistoricalReadOnly = false;
    
    VerticalPanel layout = new VerticalPanel();

    public ModuleEditorWrapperView(Module packageConfigData, boolean isHistoricalReadOnly) {
        this.packageConfigData = packageConfigData;
        this.isHistoricalReadOnly = isHistoricalReadOnly;
        
        initWidget(layout);        
        //setRefreshHandler();
        render();
        setWidth("100%");
    }
    
    private void render() {
        final TabPanel tPanel = new TabPanel();
        tPanel.setWidth("100%");

        ArtifactEditor artifactEditor = new ArtifactEditor(packageConfigData, this.isHistoricalReadOnly);

/*        Command refreshCommand = new Command() {
            public void execute() {
                refresh();
            }
        };   
        */
        //AbstractModuleEditor moduleEditor = clientFactory.getPerspectiveFactory().getModuleEditor(packageConfigData, clientFactory, eventBus, this.isHistoricalReadOnly, refreshCommand);
        
        layout.clear();
        
/*        Widget actionToolBar = clientFactory.getPerspectiveFactory().getModuleEditorActionToolbar(packageConfigData, clientFactory, eventBus, this.isHistoricalReadOnly, refreshCommand );
        layout.add(actionToolBar);

        AssetViewerActivity assetViewerActivity = new AssetViewerActivity(packageConfigData.getUuid(),
                clientFactory);
        assetViewerActivity.start(new AcceptItem() {
                    public void add(String tabTitle, IsWidget widget) {
                        ScrollPanel pnl = new ScrollPanel();
                        pnl.setWidth("100%");
                        pnl.add(widget);
                        tPanel.add(pnl, constants.Assets());
                    }
                }, null);*/

        ScrollPanel pnl = new ScrollPanel();
        pnl.setWidth("100%");
        pnl.add(artifactEditor);
        tPanel.add(pnl, constants.AttributeForModuleEditor());
        tPanel.selectTab(0);

/*        pnl = new ScrollPanel();
        pnl.setWidth("100%");
        pnl.add(moduleEditor);
        tPanel.add(pnl, constants.Edit());
        tPanel.selectTab(0);*/

        tPanel.setHeight("100%");
        layout.add(tPanel);
        layout.setHeight("100%");
    }

    @Override
    public void setFocus() {
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    @Override
    public void onResize() {
    }



    public void onAfterSave() {
    }

    public void onSave() {
        //not needed.

    }

}
