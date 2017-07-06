/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.DTCellValueWidgetFactory;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasAdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.BaseDecisionTableColumnPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.DecisionTableColumnPlugin;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;

import static org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils.nil;

@Dependent
public class AdditionalInfoPage<T extends HasAdditionalInfoPage & DecisionTableColumnPlugin> extends BaseDecisionTableColumnPage<T> {

    private View view;

    private boolean headerEnabled = false;

    private boolean hideColumnEnabled = false;

    private boolean logicallyInsertEnabled = false;

    private boolean updateEngineWithChangesEnabled = false;

    @Inject
    public AdditionalInfoPage(final View view,
                              final TranslationService translationService) {
        super(translationService);

        this.view = view;
    }

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.AdditionalInfoPage_AdditionalInfo);
    }

    @Override
    public void prepareView() {
        view.init(this);
        view.clear();

        setup();
    }

    @Override
    protected UberElement<?> getView() {
        return view;
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        boolean isNotEmpty = isHeaderNotEmpty();
        boolean isUnique = isHeaderUnique();
        boolean isValid = isNotEmpty && isUnique;

        if (!isNotEmpty) {
            view.showWarning(translate(GuidedDecisionTableErraiConstants.YouMustEnterAColumnHeaderValueDescription));
        }
        if (!isUnique) {
            view.showWarning(translate(GuidedDecisionTableErraiConstants.ThatColumnNameIsAlreadyInUsePleasePickAnother));
        }

        if (isValid) {
            view.hideWarning();
        }
        callback.callback(isValid);
    }

    public void enableHideColumn() {
        hideColumnEnabled = true;
    }

    public void enableLogicallyInsert() {
        logicallyInsertEnabled = true;
    }

    public void enableUpdateEngineWithChanges() {
        updateEngineWithChangesEnabled = true;
    }

    public void enableHeader() {
        headerEnabled = true;
    }

    public String getHeader() {
        return plugin().getHeader();
    }

    void setHeader(final String header) {
        plugin().setHeader(header);
    }

    CheckBox newHideColumnCheckBox() {
        return DTCellValueWidgetFactory.getHideColumnIndicator(plugin().editingCol());
    }

    private void setup() {
        setupHeader();
        setupHideColumn();
        setupLogicallyInsert();
        setupUpdateEngineWithChanges();
    }

    void setupHeader() {
        if (headerEnabled) {
            view.showHeader();
        }
    }

    void setupHideColumn() {
        if (hideColumnEnabled) {
            view.showHideColumn(newHideColumnCheckBox());
        }
    }

    void setupLogicallyInsert() {
        if (logicallyInsertEnabled && plugin().showLogicallyInsert()) {
            view.showLogicallyInsert(plugin().isLogicallyInsert());
        }
    }

    void setupUpdateEngineWithChanges() {
        if (updateEngineWithChangesEnabled && plugin().showUpdateEngineWithChanges()) {
            view.showUpdateEngineWithChanges(plugin().isUpdateEngine());
        }
    }

    private boolean isHeaderNotEmpty() {
        return headerEnabled && !nil(getHeader());
    }

    boolean isHeaderUnique() {
        return !plugin()
                .getAlreadyUsedColumnHeaders()
                .stream()
                .filter(header -> plugin().isNewColumn() || !originalColumnHeader().equals(header))
                .anyMatch(header -> header.equals(getHeader()));
    }

    private String originalColumnHeader() {
        final DTColumnConfig52 originalCol = plugin().getOriginalColumnConfig52();

        return originalCol.getHeader();
    }

    void setInsertLogical(final Boolean insertLogical) {
        plugin().setInsertLogical(insertLogical);
    }

    public void setUpdate(final Boolean update) {
        plugin().setUpdate(update);
    }

    public interface View extends UberElement<AdditionalInfoPage> {

        void showHideColumn(final CheckBox checkBox);

        void showHeader();

        void showLogicallyInsert(final boolean isLogicallyInsert);

        void showUpdateEngineWithChanges(final boolean isUpdateEngine);

        void showWarning(String message);

        void hideWarning();

        void clear();
    }
}
