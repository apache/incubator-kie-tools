/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.cms.widget;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.navigation.layout.LayoutRecursionIssue;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.plugin.event.PluginDeleted;
import org.uberfire.ext.plugin.event.PluginSaved;
import org.uberfire.ext.plugin.model.Plugin;

/**
 * Runtime perspective widget
 */
@Dependent
public class PerspectiveWidget implements IsWidget {

    public interface View extends UberView<PerspectiveWidget> {

        void showContent(IsWidget widget);

        void notFoundError();

        void infiniteRecursionError();

    }

    View view;
    PerspectivePluginManager perspectivePluginManager;
    String perspectiveId;

    @Inject
    public PerspectiveWidget(View view, PerspectivePluginManager perspectivePluginManager) {
        this.view = view;
        this.perspectivePluginManager = perspectivePluginManager;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void showPerspective(String id) {
        perspectiveId = id;
        if (id == null || !perspectivePluginManager.existsPerspectivePlugin(id)) {
            view.notFoundError();
        } else {
            perspectivePluginManager.buildPerspectiveWidget(id, view::showContent, this::onDeadlock);
        }
    }

    private void onDeadlock(LayoutRecursionIssue issue) {
        view.infiniteRecursionError();
    }

    private void refreshPerspective(Plugin plugin) {
        if (perspectiveId != null && perspectiveId.equals(plugin.getName())) {
            showPerspective(perspectiveId);
        }
    }

    // Capture changes on the perspective

    public void onPlugInSaved(@Observes final PluginSaved event) {
        refreshPerspective(event.getPlugin());
    }

    public void onPlugInDeleted(@Observes final PluginDeleted event) {
        refreshPerspective(event.getPlugin());
    }
}
