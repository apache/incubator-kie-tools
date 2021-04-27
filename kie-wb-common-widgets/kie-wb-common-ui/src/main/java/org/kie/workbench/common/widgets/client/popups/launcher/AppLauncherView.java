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

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Element;
import org.jboss.errai.common.client.dom.NodeList;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static com.google.common.base.Strings.isNullOrEmpty;

@Dependent
@Templated(stylesheet = "AppLauncherView.css")
public class AppLauncherView extends Composite implements AppLauncherPresenter.AppLauncherView {

    @Inject
    private ManagedInstance<AppLauncherItemView> appLauncherItemViews;

    @Inject
    @DataField("left-column")
    private Div leftColumn;

    @Inject
    @DataField("right-column")
    private Div rightColumn;

    public void addAppLauncher(final String name, final String url, final String iconClass) {
        final AppLauncherItemView app = appLauncherItemViews.get();
        app.setName(name);
        app.setIcon(isNullOrEmpty(iconClass) ? "fa-cube" : iconClass);
        app.setURL(url);

        if (leftColumn.getChildNodes().getLength() == rightColumn.getChildNodes().getLength()) {
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
        final NodeList nodeList = element.getChildNodes();
        int length = nodeList.getLength();
        for (int i = 0; i < length; i++) {
            element.removeChild(nodeList.item(0));
        }
    }

}