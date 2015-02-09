/*
 * Copyright 2015 JBoss Inc
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
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.editor.commons.client.BaseEditor;
import org.uberfire.ext.wires.bpmn.api.model.BpmnModel;
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
    public BpmnEditorPresenter( final BpmnEditorView baseView ) {
        super( baseView );
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
        return super.mayClose( getContent().hashCode() );
    }

    @Override
    protected void loadContent() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected BpmnModel getContent() {
        return new BpmnModel();
    }

}
