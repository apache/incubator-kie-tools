/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.screens.library.client.screens.project;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.displayer.client.Displayer;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Element;
import org.jboss.errai.common.client.dom.Label;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class ProjectMetricsView implements ProjectMetricsScreen.View, IsElement {

    @Inject
    @DataField
    Label headerTitle;

    @Inject
    @DataField
    Div topContribSelectorDiv;

    @Inject
    @DataField
    Div dateSelectorDiv;

    @Inject
    @DataField
    Div commitsPerAuthorDiv;

    @Inject
    @DataField
    Div commitsOverTimeDiv;

    @Inject
    @DataField
    Div commitsByYearDiv;

    @Inject
    @DataField
    Div commitsByQuarterDiv;

    @Inject
    @DataField
    Div commitsByDayDiv;

    @Inject
    @DataField
    Div commitsAllDiv;

    private Map<Element,IsWidget> widgetMap = new HashMap<>();
    private ProjectMetricsScreen presenter;

    @Override
    public void init(ProjectMetricsScreen presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setHeaderTitle(String title) {
        headerTitle.setTextContent(title);
    }

    @Override
    public void setCommitsOverTimeDisplayer(Displayer displayer) {
        updateDisplayer(commitsOverTimeDiv, displayer);
    }

    @Override
    public void setCommitsPerAuthorDisplayer(Displayer displayer) {
        updateDisplayer(commitsPerAuthorDiv, displayer);
    }

    @Override
    public void setCommitsByYearDisplayer(Displayer displayer) {
        updateDisplayer(commitsByYearDiv, displayer);
    }

    @Override
    public void setCommitsByQuarterDisplayer(Displayer displayer) {
        updateDisplayer(commitsByQuarterDiv, displayer);
    }

    @Override
    public void setCommitsByDayOfWeekDisplayer(Displayer displayer) {
        updateDisplayer(commitsByDayDiv, displayer);
    }

    @Override
    public void setAllCommitsDisplayer(Displayer displayer) {
        updateDisplayer(commitsAllDiv, displayer);
    }

    @Override
    public void setTopContribSelectorDisplayer(Displayer displayer) {
        updateDisplayer(topContribSelectorDiv, displayer);
    }

    @Override
    public void setDateSelectorDisplayer(Displayer displayer) {
        updateDisplayer(dateSelectorDiv, displayer);
    }

    @Override
    public void clear() {
        for (Element element : widgetMap.keySet()) {
            if (widgetMap.containsKey(element)) {
                DOMUtil.removeFromParent(widgetMap.get(element));
            }
            DOMUtil.removeAllChildren(element);
        }
        widgetMap.clear();
    }

    private void updateDisplayer(Div div, Displayer displayer) {
        if (widgetMap.containsKey(div)) {
            DOMUtil.removeFromParent(widgetMap.get(div));
        }
        widgetMap.put(div, displayer);
        DOMUtil.removeAllChildren(div);
        DOMUtil.appendWidgetToElement(div, displayer);
    }
}