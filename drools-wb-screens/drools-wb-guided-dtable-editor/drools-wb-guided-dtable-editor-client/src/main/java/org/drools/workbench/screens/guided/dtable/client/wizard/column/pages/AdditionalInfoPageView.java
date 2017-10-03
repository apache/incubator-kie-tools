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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class AdditionalInfoPageView implements IsElement,
                                               AdditionalInfoPage.View {

    @Inject
    @DataField("warning")
    private Div warning;

    @Inject
    @DataField("warningMessage")
    private Span warningMessage;

    @Inject
    @DataField("headerFormItem")
    private Div headerFormItem;

    @Inject
    @DataField("hideColumnFormItem")
    private Div hideColumnFormItem;

    @Inject
    @DataField("updateEngineWithChangesFormItem")
    private Div updateEngineWithChangesFormItem;

    @Inject
    @DataField("logicallyInsertFormItem")
    private Div logicallyInsertFormItem;

    @Inject
    @DataField("header")
    private TextBox header;

    @Inject
    @DataField("hideColumn")
    private Input hideColumn;

    @Inject
    @DataField("updateEngineWithChanges")
    private Input updateEngineWithChanges;

    @Inject
    @DataField("logicallyInsert")
    private Input logicallyInsert;

    private AdditionalInfoPage page;

    private TranslationService translationService;

    public AdditionalInfoPageView() {
        //CDI proxy
    }

    @Inject
    public AdditionalInfoPageView(final TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostConstruct
    public void initPopovers() {
        hideColumn.setAttribute("type",
                                "checkbox");
        hideColumn.setAttribute("data-toggle",
                                "popover");
        logicallyInsert.setAttribute("type",
                                     "checkbox");
        logicallyInsert.setAttribute("data-toggle",
                                     "popover");
        updateEngineWithChanges.setAttribute("type",
                                             "checkbox");
        updateEngineWithChanges.setAttribute("data-toggle",
                                             "popover");

        DecisionTableColumnViewUtils.setupPopover(hideColumn,
                                                  translate(GuidedDecisionTableErraiConstants.AdditionalInfoPage_HideColumnDescription));
        DecisionTableColumnViewUtils.setupPopover(logicallyInsert,
                                                  translate(GuidedDecisionTableErraiConstants.AdditionalInfoPage_LogicalInsertDescription));
        DecisionTableColumnViewUtils.setupPopover(updateEngineWithChanges,
                                                  translate(GuidedDecisionTableErraiConstants.AdditionalInfoPage_UpdateEngineDescription));
    }

    @EventHandler("header")
    public void onSelectHeader(final KeyUpEvent event) {
        page.setHeader(header.getText());
    }

    @EventHandler("hideColumn")
    public void onSelectHideColumn(final ChangeEvent event) {
        page.setHideColumn(hideColumn.getChecked());
    }

    @EventHandler("logicallyInsert")
    public void onSelectLogicallyInsert(final ChangeEvent event) {
        page.setInsertLogical(logicallyInsert.getChecked());
    }

    @EventHandler("updateEngineWithChanges")
    public void onSelectUpdateEngineWithChanges(final ChangeEvent event) {
        page.setUpdate(updateEngineWithChanges.getChecked());
    }

    @Override
    public void init(final AdditionalInfoPage page) {
        this.page = page;
    }

    @Override
    public void showHideColumn(final boolean isHidden) {
        hideColumnFormItem.setHidden(false);

        hideColumn.setChecked(isHidden);
    }

    @Override
    public void showHeader() {
        header.setText(page.getHeader());

        headerFormItem.setHidden(false);
    }

    @Override
    public void showLogicallyInsert(final boolean isLogicallyInsert) {
        logicallyInsertFormItem.setHidden(false);

        logicallyInsert.setChecked(isLogicallyInsert);
    }

    @Override
    public void showUpdateEngineWithChanges(final boolean isUpdateEngine) {
        updateEngineWithChangesFormItem.setHidden(false);

        updateEngineWithChanges.setChecked(isUpdateEngine);
    }

    @Override
    public void showWarning(String message) {
        warningMessage.setTextContent(message);
        warning.setHidden(false);
    }

    @Override
    public void hideWarning() {
        warning.setHidden(true);
        warningMessage.setTextContent("");
    }

    @Override
    public void clear() {
        headerFormItem.setHidden(true);
        hideColumnFormItem.setHidden(true);
        logicallyInsertFormItem.setHidden(true);
        updateEngineWithChangesFormItem.setHidden(true);
    }

    private String translate(final String key,
                             final Object... args) {
        return translationService.format(key,
                                         args);
    }
}
