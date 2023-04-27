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

package org.dashbuilder.client.navigation.widget;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;

@ApplicationScoped
public class ScreenLayoutDragComponent implements LayoutDragComponent {

    public static final String SCREEN_NAME_PARAMETER = "Screen Name";
    protected List<String> availableWorkbenchScreensIds = new ArrayList<String>();
    PerspectivePluginManager perspectivePluginManager;

    @Inject
    public ScreenLayoutDragComponent(PerspectivePluginManager perspectivePluginManager) {
        this.perspectivePluginManager = perspectivePluginManager;
    }

    @Override
    public IsWidget getShowWidget(RenderingContext ctx) {
        FlowPanel panel = GWT.create(FlowPanel.class);
        panel.asWidget().getElement().addClassName("uf-perspective-col");
        var perspectiveId = ctx.getComponent().getProperties().get(SCREEN_NAME_PARAMETER);
        if (perspectiveId == null) {
            return null;
        }

        perspectivePluginManager.buildPerspectiveWidget(perspectiveId, screen -> panel.add(screen), issue -> panel
                .add(new Label("Error with infinite recursion. Review the embedded page")));
        return panel;
    }

}
