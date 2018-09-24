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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.DivElement;
import org.drools.workbench.screens.scenariosimulation.client.models.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;

@Dependent
public class ListGroupItemPresenter implements ListGroupItemView.Presenter {

    @Inject
    ViewsProvider viewsProvider;

    @Inject
    FieldItemPresenter fieldItemPresenter;

    RightPanelView.Presenter rightPanelPresenter;

    List<ListGroupItemView> listGroupItemViewList = new ArrayList<>();

    private AtomicBoolean disabled = new AtomicBoolean(true);

    public void setDisabled(boolean disabled) {
        this.disabled.set(disabled);
        if (disabled) {
            listGroupItemViewList.forEach(ListGroupItemView::closeRow);
        }
    }

    @Override
    public void init(RightPanelView.Presenter rightPanelPresenter) {
        this.rightPanelPresenter = rightPanelPresenter;
        fieldItemPresenter.setRightPanelPresenter(rightPanelPresenter);
    }

    @Override
    public DivElement getDivElement(String factName, FactModelTree factModelTree) {
        ListGroupItemView listGroupItemView = commonGetListGroupItemView("", false);
        populateListGroupItemView(listGroupItemView, "", factName, factModelTree);
        return listGroupItemView.getDivElement();
    }

    @Override
    public DivElement getDivElement(String fullPath, String factName, String factModelTreeClass) {
        ListGroupItemView listGroupItemView = commonGetListGroupItemView(fullPath, true);
        populateListGroupItemView(listGroupItemView, factName, factModelTreeClass);
        return listGroupItemView.getDivElement();
    }

    @Override
    public void onToggleRowExpansion(ListGroupItemView listGroupItemView, boolean currentlyShown) {
        if (disabled.get()) {
            return;
        }
        if (listGroupItemViewList.contains(listGroupItemView)) {
            if (currentlyShown) {
                listGroupItemView.closeRow();
            } else {
                if (listGroupItemView.isToExpand()) {
                    FactModelTree factModelTree = rightPanelPresenter.getFactModelTree(listGroupItemView.getFactType());
                    populateListGroupItemView(listGroupItemView, listGroupItemView.getParentPath(), listGroupItemView.getFactName(), factModelTree);
                    listGroupItemView.setToExpand(false);
                }
                listGroupItemView.expandRow();
            }
        }
    }

    /**
     * Populate the "Assets" list. When
     * @param toPopulate
     * @param parentPath
     * @param factName
     * @param factModelTree the <code>FactModelTree</code> with all properties of a given type
     */
    protected void populateListGroupItemView(ListGroupItemView toPopulate, String parentPath, String factName, FactModelTree factModelTree) {
        if (factName.equals(factModelTree.getFactName())) {  // the name of the property equals the type of the factModelTree: this means that we are populating the "root" of the class
            toPopulate.setFactName(factName);
        } else {
            toPopulate.setFactNameAndType(factName, factModelTree.getFactName()); // the name of the property differ from the type of the factModelTree: this means that we are populating children of the class
        }
        String fullPath = parentPath.isEmpty() ? factName : parentPath + "." + factName;
        factModelTree.getSimpleProperties().forEach((key, value) -> toPopulate.addFactField(fieldItemPresenter.getLIElement(fullPath, factName, key, value)));
        factModelTree.getExpandableProperties().forEach(
                (key, value) -> toPopulate.addExpandableFactField(getDivElement(fullPath, key, value)));
    }

    /**
     * Set the property' <b>name</b> (factName) and <b>type</b> (factModelTreeClass) of a given <code>ListGroupItemView</code>
     * @param toPopulate
     * @param factName the property' name
     * @param factType the property' type
     */
    protected void populateListGroupItemView(ListGroupItemView toPopulate, String factName, String factType) {
        toPopulate.setFactNameAndType(factName, factType);
    }

    /**
     * @param parentPath the parent' path - empty for <b>top-level</b> elements
     * @param toExpand If <code>true</code>, on {@link #onToggleRowExpansion(ListGroupItemView, boolean)} inner properties will be populated
     * @return
     */
    protected ListGroupItemView commonGetListGroupItemView(String parentPath, boolean toExpand) {
        ListGroupItemView listGroupItemView = viewsProvider.getListGroupItemView();
        listGroupItemView.init(this);
        listGroupItemView.setToExpand(toExpand);
        listGroupItemView.setParentPath(parentPath);
        listGroupItemViewList.add(listGroupItemView);
        return listGroupItemView;
    }
}
