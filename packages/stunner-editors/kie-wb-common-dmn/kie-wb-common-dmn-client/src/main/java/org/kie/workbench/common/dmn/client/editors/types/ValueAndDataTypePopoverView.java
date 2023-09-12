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

package org.kie.workbench.common.dmn.client.editors.types;

import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.PopoverView;
import org.uberfire.client.mvp.UberElement;

/**
 * Definition of the _view_ to edit a domain object implementing {@link HasValueAndTypeRef}
 */
public interface ValueAndDataTypePopoverView extends PopoverView,
                                                     UberElement<ValueAndDataTypePopoverView.Presenter> {

    /**
     * Definition of the _presenter_ to edit a domain object implementing {@link HasValueAndTypeRef}
     */
    interface Presenter extends HasCellEditorControls.Editor<HasValueAndTypeRef> {

        /**
         * Sets the domain object value. The {@link String} value from the UI that has been _normalised_.
         * {@see ValueAndDataTypePopoverView.Presenter#normaliseValue}. The value should be converted to the domain
         * object. {@see HasValueAndTypeRef#toModelValue}.
         * @param value The non-null value.
         */
        void setValue(final String value);

        /**
         * Sets the domain object typeRef.
         * @param typeRef The non-null typeRef.
         */
        void setTypeRef(final QName typeRef);

        /**
         * Returns the {@link String} for the _value_ label.
         * @return The non-null label.
         */
        String getValueLabel();

        /**
         * Returns a _normalised_ value that can be used to populate the domain model from the value entered by Users.
         * @param value The value to be normalised.
         * @return A normalised value.
         */
        String normaliseValue(final String value);
    }

    /**
     * Initialises the UI for the domain object.
     * @param dmnModel The DMN domain object.
     */
    void setDMNModel(final DMNModelInstrumentedBase dmnModel);

    /**
     * Initialises the UI _value_ editor content. The value should be converted to {@link String} from the
     * domain object. {@see HasValueAndTypeRef#toWidgetValue}.
     * @param value The value to set in the UI.
     */
    void initValue(final String value);

    /**
     * Initialises the UI _typeRef_ editor content.
     * @param typeRef The typeRef to set in the UI.
     */
    void initSelectedTypeRef(final QName typeRef);
}
