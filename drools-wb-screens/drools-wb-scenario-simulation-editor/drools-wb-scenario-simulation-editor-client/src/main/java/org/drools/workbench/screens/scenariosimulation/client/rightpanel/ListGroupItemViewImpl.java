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

import java.util.Objects;

import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.FA_ANGLE_DOWN;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.HIDDEN;

/**
 * This class is used to represent a <b>complex</b> (i.e. expandable) property, i.e. a class containing other properties
 */
@Dependent
@Templated
public class ListGroupItemViewImpl implements ListGroupItemView {

    public static final String LIST_VIEW_PF_EXPAND_ACTIVE = "list-view-pf-expand-active";

    @DataField("listGroupExpansion")
    protected DivElement listGroupExpansion = Document.get().createDivElement();

    @DataField("listGroupItem")
    protected DivElement listGroupItem = Document.get().createDivElement();

    @DataField("listGroupItemHeader")
    protected DivElement listGroupItemHeader = Document.get().createDivElement();

    @DataField("listGroupItemContainer")
    protected DivElement listGroupItemContainer = Document.get().createDivElement();

    @DataField("faAngleRight")
    protected SpanElement faAngleRight = Document.get().createSpanElement();

    @DataField("listGroupElement")
    protected DivElement listGroupElement = Document.get().createDivElement();

    @DataField("fullClassName")
    protected SpanElement fullClassName = Document.get().createSpanElement();

    @DataField("checkElement")
    protected SpanElement checkElement = Document.get().createSpanElement();

    @DataField("factProperties")
    protected UListElement factProperties = Document.get().createULElement();

    protected Presenter presenter;

    protected String parentPath = "";

    protected String factName;

    protected String factType;

    protected boolean toExpand = false;

    protected boolean instanceAssigned = false;

    @Override
    public Widget asWidget() {
        return null;
    }

    @Override
    public String getActualClassName() {
        return parentPath.isEmpty() ? factName : parentPath + "." + factName;
    }

    @EventHandler("listGroupElement")
    public void onFullClassNameClick(ClickEvent event) {
        if (!listGroupElement.getClassName().contains(ConstantHolder.DISABLED)) {
            showCheck(true);
            presenter.onSelectedElement(this);
        }
    }

    @EventHandler("faAngleRight")
    public void onFaAngleRightClick(ClickEvent event) {
        presenter.onToggleRowExpansion(this, isShown());
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
    public void unselect() {
        showCheck(false);
    }

    @Override
    public void showCheck(boolean show) {
        if (show) {
            checkElement.getStyle().setDisplay(Style.Display.BLOCK);
            listGroupItem.addClassName(ConstantHolder.SELECTED);
        } else {
            checkElement.getStyle().setDisplay(Style.Display.NONE);
            listGroupItem.removeClassName(ConstantHolder.SELECTED);
        }
    }

    @Override
    public boolean isCheckShown() {
        return !Objects.equals(Style.Display.NONE.getCssName(), checkElement.getStyle().getDisplay());
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
                .append(factName)
                .append(" [")
                .append(factType)
                .append("]")
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
        if (this.parentPath.isEmpty()) {
            listGroupElement.removeClassName(ConstantHolder.DISABLED);
        } else {
            listGroupElement.addClassName(ConstantHolder.DISABLED);
        }
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
    public DivElement getListGroupExpansion() {
        return listGroupExpansion;
    }

    public DivElement getListGroupItem() {
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

    @Override
    public boolean isShown() {
        return listGroupItemHeader.getClassName().contains(LIST_VIEW_PF_EXPAND_ACTIVE);
    }

    @Override
    public void setInstanceAssigned(boolean instanceAssigned) {
        this.instanceAssigned = instanceAssigned;
    }

    @Override
    public boolean isInstanceAssigned() {
        return instanceAssigned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ListGroupItemViewImpl that = (ListGroupItemViewImpl) o;
        return Objects.equals(getParentPath(), that.getParentPath()) &&
                Objects.equals(getFactName(), that.getFactName()) &&
                Objects.equals(getFactType(), that.getFactType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParentPath(), getFactName(), getFactType());
    }

    @Override
    public String toString() {
        return "ListGroupItemViewImpl{" +
                "fullClassName=" + fullClassName +
                ", parentPath='" + parentPath + '\'' +
                ", factName='" + factName + '\'' +
                ", factType='" + factType + '\'' +
                '}';
    }
}
