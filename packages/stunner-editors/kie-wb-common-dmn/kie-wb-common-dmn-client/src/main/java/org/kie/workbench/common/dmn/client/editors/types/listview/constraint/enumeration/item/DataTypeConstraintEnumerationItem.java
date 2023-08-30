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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Element;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.ConstraintPlaceholderHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.DataTypeConstraintEnumeration;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Dependent
public class DataTypeConstraintEnumerationItem {

    static final String NULL = "null";

    private final View view;

    private final ConstraintPlaceholderHelper placeholderHelper;

    private DataTypeConstraintEnumeration dataTypeConstraintEnumeration;

    private String value;

    private String oldValue;

    @Inject
    public DataTypeConstraintEnumerationItem(final View view,
                                             final ConstraintPlaceholderHelper placeholderHelper) {
        this.view = view;
        this.placeholderHelper = placeholderHelper;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public void setValue(final String newValue) {
        setNonNullValue(newValue);
        view.setValue(getValue());
    }

    public String getValue() {
        return value;
    }

    public int getOrder() {
        return view.getOrder();
    }

    public Element getElement() {
        return view.getElement();
    }

    public void enableEditMode() {

        setOldValue(getValue());

        view.showValueInput();
        view.focusValueInput();
        view.enableHighlight();
        view.showSaveButton();
        view.showClearButton();
        view.hideDeleteButton();
    }

    public void disableEditMode() {
        view.showValueText();
        view.disableHighlight();
        view.hideSaveButton();
        view.hideClearButton();
        view.showDeleteButton();
    }

    public void save(final String newValue) {
        setValue(newValue);
        disableEditMode();
    }

    public void remove() {
        getEnumerationItems().remove(this);
        refreshEnumerationList();
    }

    void discardEditMode() {
        setValue(getOldValue());
        disableEditMode();
    }

    private List<DataTypeConstraintEnumerationItem> getEnumerationItems() {
        return dataTypeConstraintEnumeration.getEnumerationItems();
    }

    private void refreshEnumerationList() {
        dataTypeConstraintEnumeration.refreshView();
    }

    Command getScrollToThisItemCallback() {
        return () -> dataTypeConstraintEnumeration.scrollToPosition(getOrder());
    }

    void setOldValue(final String value) {
        this.oldValue = value;
    }

    String getOldValue() {
        return oldValue;
    }

    public void setDataTypeConstraintEnumeration(final DataTypeConstraintEnumeration dataTypeConstraintEnumeration) {
        this.dataTypeConstraintEnumeration = dataTypeConstraintEnumeration;
    }

    public void setConstraintValueType(final String constraintValueType) {
        view.setComponentSelector(constraintValueType);
        view.setPlaceholder(placeholderHelper.getPlaceholderSample(constraintValueType));
    }

    private void setNonNullValue(final String newValue) {
        value = isEmpty(newValue) ? NULL : newValue;
    }

    public void setOrder(final int order) {
        view.setOrder(order);
    }

    public interface View extends UberElemental<DataTypeConstraintEnumerationItem>,
                                  IsElement {

        void showValueText();

        void showValueInput();

        void focusValueInput();

        void showSaveButton();

        void hideSaveButton();

        void enableHighlight();

        void disableHighlight();

        void setValue(final String value);

        void setPlaceholder(final String placeholder);

        void setComponentSelector(final String type);

        void showClearButton();

        void hideDeleteButton();

        void hideClearButton();

        void showDeleteButton();

        int getOrder();

        void setOrder(final int order);
    }
}
