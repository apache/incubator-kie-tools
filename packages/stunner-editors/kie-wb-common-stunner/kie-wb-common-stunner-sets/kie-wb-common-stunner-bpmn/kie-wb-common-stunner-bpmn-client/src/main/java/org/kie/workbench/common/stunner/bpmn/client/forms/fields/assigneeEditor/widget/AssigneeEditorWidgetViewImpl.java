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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLTableCellElement;
import elemental2.dom.HTMLTableSectionElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtproject.dom.client.Style;
import org.gwtproject.user.client.ui.Composite;
import org.gwtproject.user.client.ui.HasValue;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.kie.workbench.common.forms.common.rendering.client.widgets.FormWidget;
import org.uberfire.commons.Pair;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;

@Templated
@Dependent
public class AssigneeEditorWidgetViewImpl extends Composite implements AssigneeEditorWidgetView,
                                                                       FormWidget<String> {

    private static final String ACTION_ENABLED = "kie-wb-common-stunner-assignee-table-add-action";
    private static final String ACTION_DISABLED = "kie-wb-common-stunner-assignee-table-add-action-disabled";

    private Presenter presenter;

    private Map<AssigneeListItem, HTMLElement> elements = new HashMap<>();
    private boolean readOnly = false;

    @Named("th")
    @Inject
    @DataField
    private HTMLTableCellElement nameth;

    @Inject
    @DataField
    private HTMLAnchorElement addAnchor;

    @Inject
    @DataField
    @Named("span")
    private HTMLElement addAnchorLabel;

    @Named("tbody")
    @Inject
    @DataField
    protected HTMLTableSectionElement assigneeRows;

    protected List<Pair<LiveSearchDropDown<String>, HTMLButtonElement>> assigneeRowsElements = new ArrayList<>();

    @Override
    public HasValue<String> wrapped() {
        return presenter;
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;

        nameth.textContent = (presenter.getNameHeader());
        addAnchorLabel.textContent  = (presenter.getAddLabel());
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
        HTMLElement tableRow = (HTMLElement) DomGlobal.document.createElement("tr");

        HTMLElement liveSearchTd = (HTMLElement) DomGlobal.document.createElement("td");

        listItem.getLiveSearchDropDown().asWidget().getElement().getStyle().setWidth(100, Style.Unit.PCT);

        DOMUtil.appendWidgetToElement(liveSearchTd, listItem.getLiveSearchDropDown());
        listItem.getLiveSearchDropDown().setEnabled(!readOnly);
        HTMLElement actionTd = (HTMLElement) DomGlobal.document.createElement("td");

        HTMLButtonElement button = (HTMLButtonElement) DomGlobal.document.createElement("button");
        button.className = ("btn btn-link fa fa-trash");
        button.addEventListener("click", event -> {
            listItem.notifyRemoval();
            DOMUtil.removeFromParent(tableRow);
        }, false);
        button.disabled = (readOnly);

        actionTd.appendChild(button);

        tableRow.appendChild(liveSearchTd);
        tableRow.appendChild(actionTd);

        assigneeRows.appendChild(tableRow);
        assigneeRowsElements.add(new Pair<>(listItem.getLiveSearchDropDown(), button));
        elements.put(listItem, tableRow);
    }

    @Override
    public void enableAddButton() {
        addAnchor.hidden = (false);
    }

    @Override
    public void disableAddButton() {
        addAnchor.hidden = (true);
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
            element.getK2().disabled = (readOnly);
        });
    }

    @EventHandler("addAnchor")
    public void onAddAssigneeClick(@ForEvent("click") elemental2.dom.Event event) {
        presenter.addAssignee();
    }
}
