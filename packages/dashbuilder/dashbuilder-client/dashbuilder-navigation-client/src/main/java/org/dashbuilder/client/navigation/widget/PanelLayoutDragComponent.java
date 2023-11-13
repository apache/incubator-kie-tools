/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.dashbuilder.client.navigation.widget;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;

@ApplicationScoped
public class PanelLayoutDragComponent implements LayoutDragComponent {

    public static final String PAGE_NAME_PARAMETER = "Page Name";
    PerspectivePluginManager perspectivePluginManager;

    @Inject
    public PanelLayoutDragComponent(PerspectivePluginManager perspectivePluginManager) {
        this.perspectivePluginManager = perspectivePluginManager;
    }

    @Override
    public IsWidget getShowWidget(RenderingContext ctx) {
        PanelHeader header = GWT.create(PanelHeader.class);
        PanelCollapse panel = GWT.create(PanelCollapse.class);
        PanelGroup group = GWT.create(PanelGroup.class);

        var perspectiveId = ctx.getComponent().getProperties().get(PAGE_NAME_PARAMETER);
        if (perspectiveId == null) {
            return null;
        }

        perspectivePluginManager.buildPerspectiveWidget(perspectiveId,
                panel::add,
                issue -> panel.add(new Label("Error with infinite recursion. Review the embedded page")));

        header.setDataTargetWidget(panel);
        header.setDataToggle(Toggle.COLLAPSE);
        header.setText(perspectiveId);

        panel.setToggle(true);

        group.add(header);
        group.add(panel);
        group.asWidget().getElement().addClassName("uf-perspective-col");

        return group;
    }

}
