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
package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

/**
 * This class is used to represent a <b>complex</b> (i.e. expandable) property, i.e. a class containing other properties
 */
@Dependent
@Templated
public class ListGroupItemViewImpl implements ListGroupItemView {

    public static final String FA_ANGLE_DOWN = "fa-angle-down";
    public static final String LIST_VIEW_PF_EXPAND_ACTIVE = "list-view-pf-expand-active";
    public static final String HIDDEN = "hidden";

    @DataField("listGroupItem")
    DivElement listGroupItem = Document.get().createDivElement();

    @DataField("listGroupItemHeader")
    DivElement listGroupItemHeader = Document.get().createDivElement();

    @DataField("listGroupItemContainer")
    DivElement listGroupItemContainer = Document.get().createDivElement();

    @DataField("faAngleRight")
    SpanElement faAngleRight = Document.get().createSpanElement();

    @DataField("fullClassName")
    DivElement fullClassName = Document.get().createDivElement();

    @DataField("factProperties")
    UListElement factProperties = Document.get().createULElement();

    Presenter presenter;

    String parentPath = "";

    String factName;

    String factType;

    boolean toExpand = false;

    @Override
    public Widget asWidget() {
        return null;
    }

    @EventHandler("listGroupItemHeader")
    public void onListGroupItemHeaderClick(ClickEvent event) {
        presenter.onToggleRowExpansion(this, listGroupItemHeader.getClassName().contains(LIST_VIEW_PF_EXPAND_ACTIVE));
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setToExpand(boolean toExpand) {
        this.toExpand = toExpand;
    }

    @Override
    public boolean isToExpand() {
        return toExpand;
    }

    @Override
    public void setFactName(String factName) {
        this.factName = factName;
        this.factType = factName;
        fullClassName.setInnerText(factName);
        listGroupItem.setAttribute("id", "listGroupItem-" + factName);
    }

    @Override
    public void setFactNameAndType(String factName, String factType) {
        this.factName = factName;
        this.factType = factType;
        String innerHtml = new StringBuilder()
                .append("<b>")
                .append(factName)
                .append("</b> ")
                .append(factType)
                .toString();
        fullClassName.setInnerHTML(innerHtml);
        fullClassName.setAttribute("factName", factName);
        fullClassName.setAttribute("factType", factType);
        fullClassName.setAttribute("parentPath", parentPath);
        listGroupItem.setAttribute("id", "listGroupItem-" + factName);
    }

    @Override
    public void setParentPath(String parentPath) {
        this.parentPath = this.parentPath.isEmpty() ? parentPath : this.parentPath + "." + parentPath;
    }

    @Override
    public String getParentPath() {
        return parentPath;
    }

    @Override
    public String getFactName() {
        return factName;
    }

    @Override
    public String getFactType() {
        return factType;
    }

    @Override
    public void addFactField(LIElement fieldElement) {
        factProperties.appendChild(fieldElement);
    }

    @Override
    public void addExpandableFactField(DivElement fieldElement) {
        listGroupItemContainer.appendChild(fieldElement);
    }

    @Override
    public DivElement getDivElement() {
        return listGroupItem;
    }

    @Override
    public void closeRow() {
        listGroupItemHeader.removeClassName(LIST_VIEW_PF_EXPAND_ACTIVE);
        listGroupItemContainer.addClassName(HIDDEN);
        faAngleRight.removeClassName(FA_ANGLE_DOWN);
    }

    @Override
    public void expandRow() {
        listGroupItemHeader.addClassName(LIST_VIEW_PF_EXPAND_ACTIVE);
        listGroupItemContainer.removeClassName(HIDDEN);
        faAngleRight.addClassName(FA_ANGLE_DOWN);
    }
}
