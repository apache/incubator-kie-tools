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

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.ConstraintType;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintParserWarningEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.DataTypeConstraintEnumeration;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression.DataTypeConstraintExpression;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRange;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeShortcuts;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.ext.editor.commons.client.file.popups.elemental2.Elemental2Modal;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.ENUMERATION;
import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.EXPRESSION;
import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.NONE;
import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.RANGE;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Dependent
public class DataTypeConstraintModal extends Elemental2Modal<DataTypeConstraintModal.View> {

    static final String WIDTH = "550px";

    static final String CONSTRAINT_INITIAL_VALUE = "";

    private final DataTypeConstraintEnumeration constraintEnumeration;

    private final DataTypeConstraintExpression constraintExpression;

    private final DataTypeConstraintRange constraintRange;

    private final DataTypeShortcuts dataTypeShortcuts;

    private DataTypeConstraintComponent currentComponent = DataTypeConstraintComponent.NONE;

    private String constraintValue = CONSTRAINT_INITIAL_VALUE;

    private ConstraintType constraintType = NONE;

    private BiConsumer<String, ConstraintType> onSave;

    private String constraintValueType = "";

    @Inject
    public DataTypeConstraintModal(final View view,
                                   final DataTypeShortcuts dataTypeShortcuts,
                                   final DataTypeConstraintEnumeration constraintEnumeration,
                                   final DataTypeConstraintExpression constraintExpression,
                                   final DataTypeConstraintRange constraintRange) {
        super(view);
        this.dataTypeShortcuts = dataTypeShortcuts;
        this.constraintEnumeration = constraintEnumeration;
        this.constraintExpression = constraintExpression;
        this.constraintRange = constraintRange;
    }

    @PostConstruct
    public void setup() {

        superSetup();
        setWidth(WIDTH);

        this.constraintRange.setModal(this);

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
        getOnSave().accept(value, constraintType);
        hide();
    }

    void load(final DataTypeListItem dataTypeListItem) {
        this.constraintValue = dataTypeListItem.getDataType().getConstraint();
        this.constraintValueType = dataTypeListItem.getType();

        if (!StringUtils.isEmpty(constraintValue) && isNone(constraintType)) {
            this.constraintType = inferComponentType(constraintValue);
        } else {
            this.constraintType = dataTypeListItem.getDataType().getConstraintType();
        }

        prepareView();
    }

    void setupComponent(final ConstraintType type) {

        constraintType = isNone(type) ? inferComponentType(getConstraintValue()) : type;
        currentComponent = getComponentByType(getConstraintType());
        // Constraint type must be set before value to ensure underlying widget is initialised before an
        // attempt is made to set the value. This was masked in Business Central where setting the value results
        // in an asynchronous call to the server that completes after the Constraint type had been set.
        currentComponent.setConstraintValueType(getConstraintValueType());
        currentComponent.setValue(getConstraintValue());
        currentComponent.getElement().setAttribute("class", componentCssClass());

        if (constraintType != RANGE) {
            enableOkButton();
        }
    }

    private String componentCssClass() {
        return asCssClass(getConstraintValueType());
    }

    private String asCssClass(final String type) {
        return "kie-" + type.replaceAll(" ", "-").toLowerCase();
    }

    boolean isNone(final ConstraintType type) {
        return type == null || Objects.equals(type, NONE);
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

    void prepareView() {

        getView().setType(getConstraintValueType());
        getView().setDataType(constraintValueType);

        if (!isEmpty(getConstraintValue()) || !isNone(getConstraintType())) {
            getView().loadComponent(getConstraintType());
        } else {
            getView().setupEmptyContainer();
        }
    }

    void setConstraintType(final ConstraintType constraintType) {
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
        getView().setupOnHideHandler(this::onHide);
        dataTypeShortcuts.disable();
    }

    @Override
    public void hide() {
        superHide();
        onHide();
    }

    void onHide() {
        dataTypeShortcuts.enable();
    }

    void superShow() {
        super.show();
    }

    void superHide() {
        super.hide();
    }

    void onDataTypeConstraintParserWarningEvent(final @Observes DataTypeConstraintParserWarningEvent e) {
        getView().showConstraintWarningMessage();
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

    public void enableOkButton() {
        getView().enableOkButton();
    }

    public void disableOkButton() {
        getView().disableOkButton();
    }

    DataTypeConstraintComponent getCurrentComponent() {
        return currentComponent;
    }

    String getConstraintValue() {
        return constraintValue;
    }

    String getConstraintValueType() {
        return constraintValueType;
    }

    ConstraintType getConstraintType() {
        return constraintType;
    }

    public interface View extends Elemental2Modal.View<DataTypeConstraintModal> {

        void setType(final String type);

        void setDataType(final String dataType);

        void setupEmptyContainer();

        void loadComponent(final ConstraintType constraintType);

        void onShow();

        void showConstraintWarningMessage();

        void setupOnHideHandler(final Command handler);

        void enableOkButton();

        void disableOkButton();
    }
}
