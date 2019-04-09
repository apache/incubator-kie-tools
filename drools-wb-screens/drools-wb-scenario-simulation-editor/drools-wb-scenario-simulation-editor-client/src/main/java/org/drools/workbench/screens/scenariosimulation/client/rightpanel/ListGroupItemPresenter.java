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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.DivElement;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;

@Dependent
public class ListGroupItemPresenter implements ListGroupItemView.Presenter {

    @Inject
    protected ViewsProvider viewsProvider;

    @Inject
    protected FieldItemPresenter fieldItemPresenter;

    protected TestToolsView.Presenter testToolsPresenter;

    protected Map<String, ListGroupItemView> listGroupItemViewMap = new HashMap<>();

    private AtomicBoolean disabled = new AtomicBoolean(true);

    private String factName = null;

    @Override
    public void enable() {
        this.disabled.set(false);
        factName = null;
    }

    @Override
    public void enable(String factName) {
        this.disabled.set(false);
        this.factName = factName;
    }

    @Override
    public void unselectAll() {
        listGroupItemViewMap.values().forEach(ListGroupItemView::unselect);
    }

    @Override
    public void showAll() {
        fieldItemPresenter.showAll();
    }

    @Override
    public void selectProperty(String factName, List<String> propertyParts) {
        final ListGroupItemView listGroupItemView = listGroupItemViewMap.get(factName);
        if (!listGroupItemView.isShown()) {
            onToggleRowExpansion(listGroupItemView, false);
        }
        String key;
        for (int i = 1; i < propertyParts.size(); i++) {
            String subPart = String.join(".", propertyParts.subList(0, i));
            key = factName + "." + subPart;
            final ListGroupItemView subListGroupItemView = listGroupItemViewMap.get(key);
            if (subListGroupItemView != null && !subListGroupItemView.isShown()) {
                onToggleRowExpansion(subListGroupItemView, false);
            }
        }
        key = factName + "." + String.join(".", propertyParts);
        if (fieldItemPresenter.fieldItemMap.containsKey(key)) {
            fieldItemPresenter.fieldItemMap.get(key).onFieldElementClick();
        }
    }

    @Override
    public void hideProperty(String factName, List<String> propertyParts) {
        String key = factName + "." + String.join(".", propertyParts);
        if (fieldItemPresenter.fieldItemMap.containsKey(key)) {
            fieldItemPresenter.fieldItemMap.get(key).hide();
        }
    }

    @Override
    public void disable() {
        this.disabled.set(true);
        factName = null;
        listGroupItemViewMap.values().forEach(ListGroupItemView::closeRow);
        unselectAll();
        fieldItemPresenter.unselectAll();
    }

    @Override
    public void init(TestToolsView.Presenter testToolsPresenter) {
        this.testToolsPresenter = testToolsPresenter;
        fieldItemPresenter.setListGroupItemPresenter(this);
    }

    @Override
    public DivElement getDivElement(String factName, FactModelTree factModelTree) {
        final ListGroupItemView listGroupItemView = commonGetListGroupItemView("", factName, false);
        populateListGroupItemView(listGroupItemView, "", factName, factModelTree);
        return listGroupItemView.getListGroupItem();
    }

    @Override
    public DivElement getDivElement(String fullPath, String factName, String factModelTreeClass) {
        final ListGroupItemView listGroupItemView = commonGetListGroupItemView(fullPath, factName, true);
        populateListGroupItemView(listGroupItemView, factName, factModelTreeClass);
        return listGroupItemView.getListGroupExpansion();
    }

    @Override
    public void onToggleRowExpansion(ListGroupItemView listGroupItemView, boolean currentlyShown) {
        if (disabled.get()) {
            return;
        }
        if (currentlyShown) {
            listGroupItemView.closeRow();
        } else {
            if (listGroupItemView.isToExpand()) {
                FactModelTree factModelTree =
                        testToolsPresenter.getFactModelTreeFromFactTypeMap(listGroupItemView.getFactType())
                                .orElseGet(() -> testToolsPresenter.getFactModelTreeFromHiddenMap(listGroupItemView.getFactType()));
                if (factModelTree != null) {
                    populateListGroupItemView(listGroupItemView, listGroupItemView.getParentPath(), listGroupItemView.getFactName(), factModelTree);
                    listGroupItemView.setToExpand(false);
                }
            }
            listGroupItemView.expandRow();
        }
    }

    @Override
    public void onSelectedElement(ListGroupItemView selected) {
        testToolsPresenter.setSelectedElement(selected);
        listGroupItemViewMap.values().stream().filter(listGroupItemView -> !listGroupItemView.equals(selected)).forEach(ListGroupItemView::unselect);
        fieldItemPresenter.unselectAll();
    }

    @Override
    public void onSelectedElement(FieldItemView selected) {
        testToolsPresenter.setSelectedElement(selected);
        listGroupItemViewMap.values().forEach(ListGroupItemView::unselect);
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
     * @param factName
     * @param toExpand If <code>true</code>, on {@link #onToggleRowExpansion(ListGroupItemView, boolean)} inner properties will be populated
     * @return
     */
    protected ListGroupItemView commonGetListGroupItemView(String parentPath, String factName, boolean toExpand) {
        String key = parentPath.isEmpty() ? factName : parentPath + "." + factName;
        ListGroupItemView toReturn = viewsProvider.getListGroupItemView();
        toReturn.init(this);
        toReturn.setToExpand(toExpand);
        toReturn.setParentPath(parentPath);
        listGroupItemViewMap.put(key, toReturn);
        return toReturn;
    }
}
