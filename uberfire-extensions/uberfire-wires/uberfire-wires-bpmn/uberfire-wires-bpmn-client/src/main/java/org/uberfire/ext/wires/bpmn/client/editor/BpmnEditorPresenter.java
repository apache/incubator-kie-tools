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
package org.uberfire.ext.wires.bpmn.client.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.editor.commons.client.BaseEditor;
import org.uberfire.ext.wires.bpmn.api.model.impl.BpmnEditorContent;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.ProcessNode;
import org.uberfire.ext.wires.bpmn.api.service.BpmnService;
import org.uberfire.ext.wires.bpmn.client.resources.i18n.BpmnEditorConstants;
import org.uberfire.ext.wires.bpmn.client.type.BpmnResourceType;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.ext.editor.commons.client.menu.MenuItems.*;

@Dependent
@WorkbenchEditor(identifier = "BPMN Editor", supportedTypes = { BpmnResourceType.class }, priority = Integer.MAX_VALUE)
public class BpmnEditorPresenter
        extends BaseEditor {

    @Inject
    private BpmnResourceType resourceType;

    @Inject
    private Caller<BpmnService> service;

    private BpmnEditorView view;

    private ProcessNode process;

    @Inject
    public BpmnEditorPresenter( final BpmnEditorView baseView ) {
        super( baseView );
        this.view = baseView;
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        init( path,
              place,
              resourceType,
              true,
              false,
              SAVE,
              COPY,
              RENAME,
              DELETE );
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return BpmnEditorConstants.INSTANCE.bpmnEditorTitle();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @WorkbenchPartView
    public UberView<BpmnEditorPresenter> getWidget() {
        return (UberView<BpmnEditorPresenter>) super.baseView;
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose( process.hashCode() );
    }

    @Override
    protected void loadContent() {
        //TODO {manstis} When we move to KIE-WB this class can extend KieBaseEditor and be refactored
        service.call( getModelSuccessCallback() ).loadContent( versionRecordManager.getCurrentPath() );
    }

    private RemoteCallback<BpmnEditorContent> getModelSuccessCallback() {
        //TODO {manstis} When we move to KIE-WB this class can extend KieBaseEditor and be refactored
        return new RemoteCallback<BpmnEditorContent>() {

            @Override
            public void callback( final BpmnEditorContent content ) {
                //Path is set to null when the Editor is closed (which can happen before async calls complete).
                if ( versionRecordManager.getCurrentPath() == null ) {
                    return;
                }

                process = content.getProcess();

                view.setContent( content,
                                 isReadOnly );
                view.hideBusyIndicator();
            }
        };
    }

}
