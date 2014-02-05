/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;

import java.util.*;


public class SuperclassSelector extends Composite {

    interface SuperclassSelectorUIBinder
            extends UiBinder<Widget, SuperclassSelector> {

    }

    @UiField
    ListBox superclassList;

    private DataModelerContext context;

    private DataObjectTO dataObject;

    private static SuperclassSelectorUIBinder uiBinder = GWT.create(SuperclassSelectorUIBinder.class);

    public static final String NOT_SELECTED = "NOT_SELECTED";

    public SuperclassSelector() {
        initWidget(uiBinder.createAndBindUi(this));
        initList();
    }

    public ListBox getSuperclassList() {
        return superclassList;
    }

    public DataModelerContext getContext() {
        return context;
    }

    public void setContext(DataModelerContext context) {
        this.context = context;
        initList();
    }

    public void setDataObject(DataObjectTO dataObject) {
        this.dataObject = dataObject;
        initList();
    }

    public void setEnabled(boolean enabled) {
        this.superclassList.setEnabled(enabled);
    }

    private DataModelTO getDataModel() {
        return getContext() != null ? getContext().getDataModel() : null;
    }

    public void initList() {
        superclassList.clear();
        superclassList.addItem("", NOT_SELECTED);

        if (getDataModel() != null) {
            SortedMap<String, String> sortedClassNames = new TreeMap<String, String>();
             boolean isExtensible = false;
             String className;
             String classLabel;

            // first, all data object form this model in order
            for (DataObjectTO internalDataObject : getDataModel().getDataObjects()) {
                className = internalDataObject.getClassName();
                classLabel = DataModelerUtils.getDataObjectFullLabel(internalDataObject);
                isExtensible = !internalDataObject.isAbstract() && !internalDataObject.isFinal() && !internalDataObject.isInterface();
                if (isExtensible) {
                    if (dataObject != null && className.toLowerCase().equals(dataObject.getClassName().toLowerCase())) continue;
                    sortedClassNames.put(classLabel, className);
                }
            }
            for (Map.Entry<String, String> classNameEntry : sortedClassNames.entrySet()) {
                superclassList.addItem(classNameEntry.getKey(), classNameEntry.getValue());
            }

            // Then add all external types, ordered
            sortedClassNames.clear();
            for (DataObjectTO externalDataObject : getDataModel().getExternalClasses()) {
                className = externalDataObject.getClassName();
                classLabel = DataModelerUtils.EXTERNAL_PREFIX + className;
                isExtensible = !externalDataObject.isAbstract() && !externalDataObject.isFinal() && !externalDataObject.isInterface();
                if (isExtensible) {
                    if (dataObject != null && className.toLowerCase().equals(dataObject.getClassName().toLowerCase())) continue;
                    sortedClassNames.put(classLabel, className);
                }
            }
            for (Map.Entry<String, String> classNameEntry : sortedClassNames.entrySet()) {
                superclassList.addItem(classNameEntry.getKey(), classNameEntry.getValue());
            }

            if (dataObject != null && dataObject.getSuperClassName() != null) {
                superclassList.setSelectedValue(dataObject.getSuperClassName());
            } else {
                superclassList.setSelectedValue(NOT_SELECTED);
            }
        }
    }
}