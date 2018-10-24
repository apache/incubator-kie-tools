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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.uberfire.client.mvp.UberElemental;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Dependent
public class DataTypeConstraint {

    private final View view;

    private DataType dataType;

    @Inject
    public DataTypeConstraint(final View view) {
        this.view = view;
    }

    public void init(final DataType dataType) {
        this.dataType = dataType;
        refreshView();
    }

    void refreshView() {
        updateConstraintInput();
        toggleConstraintInput();
    }

    void updateConstraintInput() {
        view.setConstraintValue(getConstraint());
    }

    void toggleConstraintInput() {

        final boolean hasConstraint = !isEmpty(getConstraint());

        if (hasConstraint) {
            view.enableConstraint();
        } else {
            view.disableConstraint();
        }
    }

    public String getValue() {
        return view.getConstraintValue();
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    DataType getDataType() {
        return dataType;
    }

    private String getConstraint() {
        return getDataType().getConstraint();
    }

    public interface View extends UberElemental<DataTypeSelect>,
                                  IsElement {

        void enableConstraint();

        void disableConstraint();

        String getConstraintValue();

        void setConstraintValue(final String value);
    }
}
