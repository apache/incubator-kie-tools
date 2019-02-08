/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Element;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.DataTypeConstraintEnumeration;
import org.uberfire.client.mvp.UberElemental;

import static java.util.Collections.swap;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Dependent
public class DataTypeConstraintEnumerationItem {

    static final String NULL = "null";

    private final View view;

    private DataTypeConstraintEnumeration dataTypeConstraintEnumeration;

    private String value;

    private String oldValue;

    @Inject
    public DataTypeConstraintEnumerationItem(final View view) {
        this.view = view;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public void setValue(final String newValue) {
        value = isEmpty(newValue) ? NULL : newValue;
        view.setValue(getValue());
    }

    public String getValue() {
        return value;
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
    }

    public void disableEditMode() {
        view.showValueText();
        view.disableHighlight();
        view.hideSaveButton();
    }

    public void save(final String value) {
        setValue(value);
        refreshEnumerationList();
    }

    public void remove() {
        getEnumerationItems().remove(this);
        refreshEnumerationList();
    }

    void discardEditMode() {
        setValue(getOldValue());
        disableEditMode();
    }

    void moveUp() {
        moveEnumerationItem(-1);
    }

    void moveDown() {
        moveEnumerationItem(1);
    }

    void moveEnumerationItem(final int reference) {

        final int oldIndex = getCurrentIndex();
        final int newIndex = getNewIndex(reference);

        swap(getEnumerationItems(), oldIndex, newIndex);
        refreshEnumerationList();
    }

    private int getNewIndex(final int reference) {

        final int oldIndex = getCurrentIndex();
        final int newIndex = oldIndex + reference;
        final int modifier;

        if (newIndex < 0) {
            modifier = getEnumerationItems().size();
        } else if (newIndex == getEnumerationItems().size()) {
            modifier = -getEnumerationItems().size();
        } else {
            modifier = 0;
        }

        return newIndex + modifier;
    }

    private int getCurrentIndex() {
        return getEnumerationItems().indexOf(this);
    }

    List<DataTypeConstraintEnumerationItem> getEnumerationItems() {
        return dataTypeConstraintEnumeration.getEnumerationItems();
    }

    private void refreshEnumerationList() {
        dataTypeConstraintEnumeration.refreshView();
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
    }
}
