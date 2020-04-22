/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasFieldPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.BaseDecisionTableColumnPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.modals.HasList;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.DecisionTableColumnPlugin;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.widget.BindingTextBox;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;

import static org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils.nil;

@Dependent
public class FieldPage<T extends HasFieldPage & DecisionTableColumnPlugin> extends BaseDecisionTableColumnPage<T> {

    private View view;

    @Inject
    public FieldPage(final View view,
                     final TranslationService translationService) {
        super(translationService);

        this.view = view;
    }

    @Override
    protected UberElement<?> getView() {
        return view;
    }

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.FieldPage_Field);
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {

        final boolean isFieldBindingValid = plugin().isFieldBindingValid();
        final boolean isFactFieldValid = !nil(plugin().getFactField()) || isConstraintValuePredicate();
        final boolean isComplete = isFactFieldValid && isFieldBindingValid;

        fieldBindingWarningToggle(isFieldBindingValid);
        factFieldWarningToggle(isFactFieldValid);

        callback.callback(isComplete);
    }

    void fieldBindingWarningToggle(final boolean isFieldBindingValid) {
        if (!isFieldBindingValid) {
            view.showFieldBindingWarning();
        } else {
            view.hideFieldBindingWarning();
        }
    }

    void factFieldWarningToggle(final boolean isFactFieldValid) {
        if (!isFactFieldValid) {
            view.showSelectFieldWarning();
        } else {
            view.hideSelectFieldWarning();
        }
    }

    @Override
    public void prepareView() {
        view.init(this);

        setupPatternWarningMessages();
        setupPredicateBindingInfoBox();
        setupBinding();
        setupField();
    }

    void setupField() {
        if (isConstraintValuePredicate()) {
            setupPredicateFieldView();
        } else {
            setupListFieldView();
        }
    }

    private void setupPredicateFieldView() {
        view.enablePredicateFieldView();
        view.setField(getFactField());
    }

    private void setupListFieldView() {
        view.enableListFieldView();

        forEachFactField((field) -> view.addItem(field, field));
    }

    void setupPatternWarningMessages() {
        final boolean showWarningMessage = !hasEditingPattern();

        view.patternWarningToggle(showWarningMessage);
    }

    void setEditingCol(final String selectedValue) {
        plugin().setFactField(selectedValue);
    }

    void forEachFactField(Consumer<String> loadedFieldConsumer) {
        if (hasEditingPattern()) {
            final AsyncPackageDataModelOracle oracle = presenter.getDataModelOracle();

            oracle.getFieldCompletions(factType(),
                                       getAccessor(),
                                       fieldsLoadedCallback(loadedFieldConsumer));
        }
    }

    private FieldAccessorsAndMutators getAccessor() {
        return plugin().getAccessor();
    }

    void setupPredicateBindingInfoBox() {

        final boolean isConstraintValuePredicate = constraintValue() == BaseSingleFieldConstraint.TYPE_PREDICATE;

        if (isConstraintValuePredicate) {
            view.showPredicateBindingInfo();
        } else {
            view.hidePredicateBindingInfo();
        }
    }

    void setupBinding() {
        if (canSetupBinding()) {
            view.setupBinding(newBindingTextBox());
        }

        view.bindingToggle(canSetupBinding());
    }

    TextBox newBindingTextBox() {
        final BindingTextBox bindingTextBox = GWT.create(BindingTextBox.class);

        bindingTextBox.setText(plugin().getBinding());
        bindingTextBox.addKeyUpHandler(event -> {
            plugin().setBinding(bindingTextBox.getText());
        });

        return bindingTextBox;
    }

    boolean canSetupBinding() {
        return plugin().isBindable();
    }

    Callback<ModelField[]> fieldsLoadedCallback(Consumer<String> loadedFieldConsumer) {
        final AsyncPackageDataModelOracle oracle = presenter.getDataModelOracle();

        return modelFields -> {
            final List<String> fieldNames = collectNames(modelFields);

            view.setupEmptyFieldList();

            if (filterEnumFields()) {
                fieldNames
                        .stream()
                        .filter(fieldName -> !oracle.hasEnums(factType(),
                                                              fieldName))
                        .forEach(loadedFieldConsumer);
            } else {
                fieldNames
                        .forEach(loadedFieldConsumer);
            }

            view.selectField(getFactField());
        };
    }

    boolean isConstraintValuePredicate() {
        return constraintValue() == BaseSingleFieldConstraint.TYPE_PREDICATE;
    }

    private int constraintValue() {
        return plugin().constraintValue();
    }

    boolean filterEnumFields() {
        return plugin().filterEnumFields();
    }

    boolean hasEditingPattern() {
        return !nil(factType());
    }

    String factType() {
        return plugin().patternWrapper().getFactType();
    }

    private List<String> collectNames(final ModelField[] modelFields) {
        return Stream
                .of(modelFields)
                .map(ModelField::getName)
                .collect(Collectors.toList());
    }

    public String getFactField() {
        return plugin().getFactField();
    }

    public interface View extends HasList,
                                  UberElement<FieldPage> {

        void patternWarningToggle(final boolean isVisible);

        void setupEmptyFieldList();

        void selectField(final String factField);

        void setField(final String factField);

        void showSelectFieldWarning();

        void hideSelectFieldWarning();

        void enableListFieldView();

        void enablePredicateFieldView();

        void setupBinding(IsWidget widget);

        void showFieldBindingWarning();

        void hideFieldBindingWarning();

        void showPredicateBindingInfo();

        void hidePredicateBindingInfo();

        void bindingToggle(final boolean isVisible);
    }
}
