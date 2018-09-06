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

    @Override
    public void init(RightPanelView.Presenter rightPanelPresenter) {
        this.rightPanelPresenter = rightPanelPresenter;
    }

    @Override
    public DivElement getDivElement(String factName, FactModelTree factModelTree) {
        ListGroupItemView listGroupItemView = commonGetListGroupItemView(false);
        populateListGroupItemView(listGroupItemView, factName, factModelTree);
        return listGroupItemView.getDivElement();
    }

    @Override
    public DivElement getDivElement(String factName, String factModelTreeClass) {
        ListGroupItemView listGroupItemView = commonGetListGroupItemView(true);
        populateListGroupItemView(listGroupItemView, factName, factModelTreeClass);
        return listGroupItemView.getDivElement();
    }

    @Override
    public void onToggleRowExpansion(ListGroupItemView listGroupItemView, boolean currentlyShown) {
        if (listGroupItemViewList.contains(listGroupItemView)) {
            if (currentlyShown) {
                listGroupItemView.closeRow();
            } else {
                if (listGroupItemView.isToExpand()) {
                    FactModelTree factModelTree = rightPanelPresenter.getFactModelTree(listGroupItemView.getFactType());
                    populateListGroupItemView(listGroupItemView, listGroupItemView.getFactName(), factModelTree);
                    listGroupItemView.setToExpand(false);
                }
                listGroupItemView.expandRow();
            }
        }
    }

    protected void populateListGroupItemView(ListGroupItemView toPopulate, String factName, FactModelTree factModelTree) {
        if (factName.equals(factModelTree.getFactName())) {
            toPopulate.setFactName(factName);
        } else {
            toPopulate.setFactNameAndType(factName, factModelTree.getFactName());
        }
        factModelTree.getSimpleProperties().forEach((key, value) -> toPopulate.addFactField(fieldItemPresenter.getLIElement(factName, key, value)));
        factModelTree.getExpandableProperties().forEach(
                (key, value) -> toPopulate.addExpandableFactField(getDivElement(key, value)));
    }

    protected void populateListGroupItemView(ListGroupItemView toPopulate, String factName, String factModelTreeClass) {
        toPopulate.setFactNameAndType(factName, factModelTreeClass);
    }

    /**
     * @param toExpand If <code>true</code>, on onToggleRowExpansion inner properties will be populated
     * @return
     */
    protected ListGroupItemView commonGetListGroupItemView(boolean toExpand) {
        ListGroupItemView listGroupItemView = viewsProvider.getListGroupItemView();
        listGroupItemView.init(this);
        listGroupItemView.setToExpand(toExpand);
        listGroupItemViewList.add(listGroupItemView);
        return listGroupItemView;
    }
}
