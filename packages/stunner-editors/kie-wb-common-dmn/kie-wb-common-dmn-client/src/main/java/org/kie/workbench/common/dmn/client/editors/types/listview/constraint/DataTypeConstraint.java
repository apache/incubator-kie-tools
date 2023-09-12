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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint;

import java.util.function.BiConsumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.api.definition.model.ConstraintType;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class DataTypeConstraint {

    static final String NONE = "";

    private final View view;

    private final ManagedInstance<DataTypeConstraintModal> constraintModalManagedInstance;

    private DataTypeConstraintModal constraintModal;

    private boolean isEditModeEnabled = false;

    private String constraintValue = NONE;

    private ConstraintType constraintType = ConstraintType.NONE;

    private DataTypeListItem listItem;

    @Inject
    public DataTypeConstraint(final View view,
                              final ManagedInstance<DataTypeConstraintModal> constraintModalManagedInstance) {
        this.view = view;
        this.constraintModalManagedInstance = constraintModalManagedInstance;
    }

    @PostConstruct
    void setup() {

        view.init(this);

        disableEditMode();
    }

    public void init(final DataTypeListItem listItem) {

        this.listItem = listItem;
        this.constraintValue = listItem.getDataType().getConstraint();
        this.constraintType = listItem.getDataType().getConstraintType();

        refreshView();
    }

    public String getValue() {
        return constraintValue;
    }

    public ConstraintType getConstraintType() {
        return constraintType;
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public void enableEditMode() {
        isEditModeEnabled = true;
        view.showAnchor();
    }

    public void disableEditMode() {
        isEditModeEnabled = false;
        view.hideAnchor();
    }

    public void refreshView() {

        view.setText(getValue());

        if (isEditModeEnabled()) {
            enableEditMode();
        } else {
            disableEditMode();
        }
    }

    void openModal() {
        constraintModal().load(getListItem());
        constraintModal().show(getOnShowConsumer());
    }

    BiConsumer<String, ConstraintType> getOnShowConsumer() {
        return (newConstraintValue, newConstraintType) -> {
            setConstraint(newConstraintValue, newConstraintType);
            refreshView();
        };
    }

    DataTypeListItem getListItem() {
        return listItem;
    }

    boolean isEditModeEnabled() {
        return isEditModeEnabled;
    }

    public void disable() {
        setConstraint(NONE, ConstraintType.NONE);
        view.disable();
    }

    public void enable() {
        view.enable();
    }

    private void setConstraint(final String value,
                               final ConstraintType type) {
        constraintValue = value;
        constraintType = type;
    }

    DataTypeConstraintModal constraintModal() {
        /*
         * The 'constraintModal' field is lazily instantiated for performance reasons.
         * When the Data Type list has many Data Types, the 'DataTypeConstraintModal' instantiation
         * considerably decreases the performance of the Data Type list. So, this approach postpones initialization.
         * */
        if (constraintModal == null) {
            constraintModal = constraintModalManagedInstance.get();
        }
        return constraintModal;
    }

    /**
     * DO NOT CALL THIS METHOD.
     * This method is used just for testing.
     */
    DataTypeConstraintModal getConstraintModal() {
        return constraintModal;
    }

    public interface View extends UberElemental<DataTypeConstraint>,
                                  IsElement {

        void showAnchor();

        void hideAnchor();

        void setText(final String text);

        void enable();

        void disable();
    }
}
