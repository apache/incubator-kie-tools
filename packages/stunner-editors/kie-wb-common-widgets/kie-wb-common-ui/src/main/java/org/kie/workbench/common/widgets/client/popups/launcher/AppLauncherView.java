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

package org.kie.workbench.common.widgets.client.popups.launcher;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.Node;
import elemental2.dom.NodeList;
import org.gwtproject.user.client.ui.Composite;
import io.crysknife.client.ManagedInstance;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;

@Dependent
@Templated(stylesheet = "AppLauncherView.css")
public class AppLauncherView extends Composite implements AppLauncherPresenter.AppLauncherView {

    @Inject
    private ManagedInstance<AppLauncherItemView> appLauncherItemViews;

    @Inject
    @DataField("left-column")
    private HTMLDivElement leftColumn;

    @Inject
    @DataField("right-column")
    private HTMLDivElement rightColumn;

    public void addAppLauncher(final String name, final String url, final String iconClass) {
        final AppLauncherItemView app = appLauncherItemViews.get();
        app.setName(name);
        app.setIcon((iconClass == null || iconClass.isEmpty()) ? "fa-cube" : iconClass);
        app.setURL(url);

        if (leftColumn.childNodes.getLength() == rightColumn.childNodes.getLength()) {
            leftColumn.appendChild(app.getElement());
        } else {
            rightColumn.appendChild(app.getElement());
        }
    }

    @Override
    public void removeAllAppLauncher() {
        appLauncherItemViews.destroyAll();
        removeAllNodes(leftColumn);
        removeAllNodes(rightColumn);
    }

    public void removeAllNodes(final Element element) {
        final NodeList nodeList = element.childNodes;
        int length = nodeList.getLength();
        for (int i = 0; i < length; i++) {
            element.removeChild((Node)nodeList.item(0));
        }
    }

}