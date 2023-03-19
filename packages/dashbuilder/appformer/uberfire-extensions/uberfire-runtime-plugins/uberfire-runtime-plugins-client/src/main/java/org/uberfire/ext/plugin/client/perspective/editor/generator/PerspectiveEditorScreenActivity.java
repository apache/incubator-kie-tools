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

package org.uberfire.ext.plugin.client.perspective.editor.generator;

import java.util.function.Consumer;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.ext.layout.editor.api.editor.LayoutInstance;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.generator.LayoutGenerator;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.NamedPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

public class PerspectiveEditorScreenActivity implements WorkbenchScreenActivity {

    private final LayoutGenerator layoutGenerator;
    private LayoutTemplate layoutTemplate;
    private PlaceRequest place;
    private String identifier;

    private Panel mainPanel = new FlowPanel();

    public PerspectiveEditorScreenActivity(LayoutTemplate layoutTemplate,
                                           final LayoutGenerator layoutGenerator) {
        this.layoutTemplate = layoutTemplate;
        this.layoutGenerator = layoutGenerator;
        this.identifier = buildScreenId(layoutTemplate.getName());
    }

    public static String buildScreenId(String perspectiveId) {
        // AF-905: [Layout Editor] Errors while creating a page with the name of an existing screen
        // Make sure the generated id. doesn't clash with any existing screen
        return perspectiveId + " [Screen]";
    }

    public LayoutTemplate getLayoutTemplate() {
        return layoutTemplate;
    }

    public void setLayoutTemplate(LayoutTemplate layoutTemplate) {
        this.layoutTemplate = layoutTemplate;
    }

    @Override
    public void onStartup(PlaceRequest place) {
        this.place = place;
    }

    @Override
    public PlaceRequest getPlace() {
        return place;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean onMayClose() {
        return true;
    }

    @Override
    public void onClose() {
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public Position getDefaultPosition() {
        return new NamedPosition("mainContainer");
    }

    @Override
    public PlaceRequest getOwningPlace() {
        return null;
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void onLostFocus() {
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public IsWidget getTitleDecoration() {
        return null;
    }

    @Override
    public IsWidget getWidget() {
        return mainPanel;
    }

    @Override
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(null);
    }

    @Override
    public ToolBar getToolBar() {
        return null;
    }

    @Override
    public void onOpen() {
        LayoutInstance layoutInstance = layoutGenerator.build(layoutTemplate);
        mainPanel.clear();
        mainPanel.getElement().addClassName("uf-perspective-component");
        mainPanel.add(ElementWrapperWidget.getWidget(layoutInstance.getElement()));
    }

    @Override
    public String contextId() {
        return getIdentifier();
    }

    @Override
    public ActivityResourceType getResourceType() {
        return ActivityResourceType.SCREEN;
    }
}
