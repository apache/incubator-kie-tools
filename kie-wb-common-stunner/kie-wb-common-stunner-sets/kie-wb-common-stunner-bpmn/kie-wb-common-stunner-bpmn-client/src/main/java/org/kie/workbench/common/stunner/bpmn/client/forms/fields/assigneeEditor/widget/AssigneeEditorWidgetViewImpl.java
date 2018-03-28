/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.widget;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TableCell;
import org.jboss.errai.common.client.dom.TableSection;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.common.rendering.client.widgets.FormWidget;

@Templated
public class AssigneeEditorWidgetViewImpl extends Composite implements AssigneeEditorWidgetView,
                                                                       FormWidget<String> {

    private Presenter presenter;

    private Map<AssigneeListItem, HTMLElement> elements = new HashMap<AssigneeListItem, HTMLElement>();

    @Inject
    private Document document;

    @Named("th")
    @Inject
    @DataField
    private TableCell nameth;

    @Inject
    @DataField
    private Anchor addAnchor;

    @Inject
    @DataField
    private Span addAnchorLabel;

    @Named("tbody")
    @Inject
    @DataField
    protected TableSection assigneeRows;

    @Override
    public HasValue<String> wrapped() {
        return presenter;
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;

        nameth.setTextContent(presenter.getNameHeader());
        addAnchorLabel.setTextContent(presenter.getAddLabel());
    }

    @Override
    public boolean isDuplicateName(String name) {
        return false;
    }

    @Override
    public void clearList() {
        elements.clear();
        DOMUtil.removeAllChildren(assigneeRows);
    }

    @Override
    public void add(AssigneeListItem listItem) {
        HTMLElement tableRow = document.createElement("tr");

        HTMLElement liveSearchTd = document.createElement("td");

        listItem.getLiveSearchDropDown().asWidget().getElement().getStyle().setWidth(100, Style.Unit.PCT);

        DOMUtil.appendWidgetToElement(liveSearchTd, listItem.getLiveSearchDropDown());
        HTMLElement actionTd = document.createElement("td");

        Button button = (Button) document.createElement("button");
        button.setClassName("btn btn-link fa fa-trash");
        button.addEventListener("click", event -> {
            listItem.notifyRemoval();
            DOMUtil.removeFromParent(tableRow);
        }, false);

        actionTd.appendChild(button);

        tableRow.appendChild(liveSearchTd);
        tableRow.appendChild(actionTd);

        assigneeRows.appendChild(tableRow);
        elements.put(listItem, tableRow);
    }

    @Override
    public void enableAddButton() {
        addAnchor.setHidden(false);
    }

    @Override
    public void disableAddButton() {
        addAnchor.setHidden(true);
    }

    @EventHandler("addAnchor")
    public void onAddAssigneeClick(ClickEvent event) {
        presenter.addAssignee();
    }
}
