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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.uberfire.commons.Pair;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;

@Templated
public class AssigneeEditorWidgetViewImpl extends Composite implements AssigneeEditorWidgetView,
                                                                       FormWidget<String> {

    private static final String ACTION_ENABLED = "kie-wb-common-stunner-assignee-table-add-action";
    private static final String ACTION_DISABLED = "kie-wb-common-stunner-assignee-table-add-action-disabled";

    private Presenter presenter;

    private Map<AssigneeListItem, HTMLElement> elements = new HashMap<>();
    private boolean readOnly = false;

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

    protected List<Pair<LiveSearchDropDown<String>, Button>> assigneeRowsElements = new ArrayList<>();

    @Override
    public HasValue<String> wrapped() {
        return presenter;
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;

        nameth.setTextContent(presenter.getNameHeader());
        addAnchorLabel.setTextContent(presenter.getAddLabel());
    }

    @Override
    public boolean isDuplicateName(final String name) {
        return false;
    }

    @Override
    public void clearList() {
        elements.clear();
        assigneeRowsElements.clear();
        DOMUtil.removeAllChildren(assigneeRows);
    }

    @Override
    public void add(final AssigneeListItem listItem) {
        HTMLElement tableRow = document.createElement("tr");

        HTMLElement liveSearchTd = document.createElement("td");

        listItem.getLiveSearchDropDown().asWidget().getElement().getStyle().setWidth(100, Style.Unit.PCT);

        DOMUtil.appendWidgetToElement(liveSearchTd, listItem.getLiveSearchDropDown());
        listItem.getLiveSearchDropDown().setEnabled(!readOnly);
        HTMLElement actionTd = document.createElement("td");

        Button button = (Button) document.createElement("button");
        button.setClassName("btn btn-link fa fa-trash");
        button.addEventListener("click", event -> {
            listItem.notifyRemoval();
            DOMUtil.removeFromParent(tableRow);
        }, false);
        button.setDisabled(readOnly);

        actionTd.appendChild(button);

        tableRow.appendChild(liveSearchTd);
        tableRow.appendChild(actionTd);

        assigneeRows.appendChild(tableRow);
        assigneeRowsElements.add(new Pair<>(listItem.getLiveSearchDropDown(), button));
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

    @Override
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
        DOMUtil.removeCSSClass(addAnchor, ACTION_DISABLED);
        DOMUtil.removeCSSClass(addAnchor, ACTION_ENABLED);
        if (readOnly) {
            DOMUtil.addCSSClass(addAnchor, ACTION_DISABLED);
        } else {
            DOMUtil.addCSSClass(addAnchor, ACTION_ENABLED);
        }
        assigneeRowsElements.forEach(element -> {
            element.getK1().setEnabled(!readOnly);
            element.getK2().setDisabled(readOnly);
        });
    }

    @EventHandler("addAnchor")
    public void onAddAssigneeClick(ClickEvent event) {
        presenter.addAssignee();
    }
}
