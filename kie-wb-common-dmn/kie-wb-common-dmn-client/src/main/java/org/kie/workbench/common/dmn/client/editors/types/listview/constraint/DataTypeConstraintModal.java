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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint;

import java.util.Optional;
import java.util.function.BiConsumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.ConstraintType;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.DataTypeConstraintEnumeration;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression.DataTypeConstraintExpression;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRange;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.ext.editor.commons.client.file.popups.elemental2.Elemental2Modal;

import static org.kie.workbench.common.dmn.api.definition.v1_1.ConstraintType.ENUMERATION;
import static org.kie.workbench.common.dmn.api.definition.v1_1.ConstraintType.EXPRESSION;
import static org.kie.workbench.common.dmn.api.definition.v1_1.ConstraintType.RANGE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent.NONE;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Dependent
public class DataTypeConstraintModal extends Elemental2Modal<DataTypeConstraintModal.View> {

    static final String WIDTH = "550px";

    static final String CONSTRAINT_INITIAL_VALUE = "";

    private final DataTypeConstraintEnumeration constraintEnumeration;

    private final DataTypeConstraintExpression constraintExpression;

    private final DataTypeConstraintRange constraintRange;

    private DataTypeConstraintComponent currentComponent = NONE;

    private String constraintValue = CONSTRAINT_INITIAL_VALUE;

    private ConstraintType constraintType;

    private BiConsumer<String, ConstraintType> onSave;

    @Inject
    public DataTypeConstraintModal(final View view,
                                   final DataTypeConstraintEnumeration constraintEnumeration,
                                   final DataTypeConstraintExpression constraintExpression,
                                   final DataTypeConstraintRange constraintRange) {
        super(view);
        this.constraintEnumeration = constraintEnumeration;
        this.constraintExpression = constraintExpression;
        this.constraintRange = constraintRange;
    }

    @PostConstruct
    public void setup() {

        superSetup();
        setWidth(WIDTH);

        getView().init(this);
    }

    public void save() {
        doSave(getComponentConstraintValue());
    }

    void clearAll() {
        constraintType = null;
        doSave(CONSTRAINT_INITIAL_VALUE);
    }

    void doSave(final String value) {
        constraintValue = value;
        getOnSave().accept(value, this.constraintType);
        hide();
    }

    void load(final String type,
              final String value,
              final ConstraintType constraintType) {

        this.constraintValue = value;

        if (!StringUtils.isEmpty(value) && constraintType == null) {
            this.constraintType = inferComponentType(value);
        } else {
            this.constraintType = constraintType;
        }

        prepareView(type, value, constraintType);
    }

    void setupComponent(ConstraintType constraintType) {
        if (constraintType == null) {
            constraintType = inferComponentType(getConstraintValue());
        }
        this.constraintType = constraintType;
        currentComponent = getComponentByType(constraintType);
        currentComponent.setValue(getConstraintValue());
    }

    String getConstraintValue() {
        return constraintValue;
    }

    DataTypeConstraintComponent getCurrentComponent() {
        return currentComponent;
    }

    private String getComponentConstraintValue() {
        return getCurrentComponent().getValue();
    }

    private DataTypeConstraintComponent getComponentByType(final ConstraintType constraintType) {
        switch (constraintType) {
            case ENUMERATION:
                return getConstraintEnumeration();
            case EXPRESSION:
                return getConstraintExpression();
            case RANGE:
                return getConstraintRange();
            default:
                throw new UnsupportedOperationException("The type '" + constraintType + "' is not a valid component.");
        }
    }

    void prepareView(final String type,
                     final String constraintValue,
                     final ConstraintType constraintType) {

        getView().setType(type);

        if (!isEmpty(constraintValue) || constraintType != null) {
            getView().loadComponent(constraintType);
        } else {
            getView().setupEmptyContainer();
        }
    }

    public ConstraintType getConstraintType() {
        return constraintType;
    }

    public void setConstraintType(final ConstraintType constraintType) {
        this.constraintType = constraintType;
    }

    ConstraintType inferComponentType(final String constraintValue) {

        final String value = Optional.ofNullable(constraintValue).orElse("");

        if (isRange(value)) {
            return RANGE;
        } else if (isEnumeration(value)) {
            return ENUMERATION;
        } else {
            return EXPRESSION;
        }
    }

    private boolean isEnumeration(final String constraintValue) {
        return !constraintValue.startsWith("(")
            && !constraintValue.startsWith("[")
            && !constraintValue.endsWith("]")
            && !constraintValue.endsWith(")")
            && constraintValue.contains(",");
    }

    private boolean isRange(final String constraintValue) {
        final int countMatches = constraintValue.split("(\\.\\.)", -1).length - 1;
        return countMatches == 1;
    }

    public void show(final BiConsumer<String, ConstraintType> onSaveConsumer) {

        onSave = onSaveConsumer;

        superShow();
        getView().onShow();
    }

    void superShow() {
        super.show();
    }

    private DataTypeConstraintEnumeration getConstraintEnumeration() {
        return constraintEnumeration;
    }

    private DataTypeConstraintExpression getConstraintExpression() {
        return constraintExpression;
    }

    private DataTypeConstraintRange getConstraintRange() {
        return constraintRange;
    }

    BiConsumer<String, ConstraintType> getOnSave() {
        return onSave;
    }

    protected void setWidth(final String width) {
        super.setWidth(width);
    }

    public interface View extends Elemental2Modal.View<DataTypeConstraintModal> {

        void setType(final String type);

        void setupEmptyContainer();

        void loadComponent(final ConstraintType constraintType);

        void onShow();
    }
}
