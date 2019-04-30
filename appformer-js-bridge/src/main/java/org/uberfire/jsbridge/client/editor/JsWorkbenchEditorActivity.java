/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.jsbridge.client.editor;

import java.util.function.Consumer;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.AbstractWorkbenchEditorActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchEditorActivity;
import org.uberfire.jsbridge.client.loading.AppFormerJsActivityLoader;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

@AppFormerJsActivityLoader.Shadowed
public class JsWorkbenchEditorActivity extends AbstractWorkbenchEditorActivity implements WorkbenchEditorActivity {

    private JsNativeEditor editor;

    @Inject
    public JsWorkbenchEditorActivity(final PlaceManager placeManager) {
        super(placeManager);
    }

    public JsWorkbenchEditorActivity withEditor(final JsNativeEditor editor) {
        this.editor = editor;
        return this;
    }

    // Lifecycle

    @Override
    public void onStartup(final ObservablePath path, final PlaceRequest place) {
        super.onStartup(path, place);
        editor.af_onEditorStartup(path, place);
    }

    @Override
    public void onOpen() {
        super.onOpen();
        editor.af_onOpen();
    }

    @Override
    public void onSave() {
        super.onSave();
        editor.af_onSave();
    }

    @Override
    public void onFocus() {
        super.onFocus();
        editor.af_onFocus();
    }

    @Override
    public void onLostFocus() {
        super.onLostFocus();
        editor.af_onLostFocus();
    }

    @Override
    public boolean onMayClose() {
        return super.onMayClose() && editor.af_onMayClose();
    }

    @Override
    public void onClose() {
        super.onClose();
        editor.af_onClose();
    }

    @Override
    public void onShutdown() {
        super.onShutdown();
        editor.af_onShutdown();
    }

    // Properties

    @Override
    public boolean isDirty() {
        return super.isDirty() || editor.af_isDirty();
    }

    @Override
    public String getTitle() {
        return editor.af_componentTitle();
    }

    @Override
    public IsWidget getWidget() {
        return ElementWrapperWidget.getWidget(editor.getElement());
    }

    @Override
    public void getMenus(final Consumer<Menus> consumer) {
        consumer.accept(null);
    }

    @Override
    public ToolBar getToolBar() {
        return null; //TODO: Implement?
    }

    @Override
    public String getIdentifier() {
        return editor.getComponentId();
    }
}
