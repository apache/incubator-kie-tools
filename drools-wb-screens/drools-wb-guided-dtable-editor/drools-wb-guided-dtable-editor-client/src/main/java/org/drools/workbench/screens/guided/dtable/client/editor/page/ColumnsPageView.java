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

package org.drools.workbench.screens.guided.dtable.client.editor.page;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.drools.workbench.screens.guided.dtable.client.editor.page.accordion.GuidedDecisionTableAccordion;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class ColumnsPageView implements ColumnsPagePresenter.View,
                                        IsElement {

    @DataField("columns-note-info")
    private HTMLDivElement columnsNoteInfo;

    @DataField("insert-column")
    private HTMLButtonElement insertButton;

    @DataField("rule-inheritance-container")
    private HTMLDivElement ruleInheritanceContainer;

    @DataField("rule-name-option-container")
    private HTMLDivElement ruleNameOptionContainer;

    @DataField("accordion-container")
    private HTMLDivElement accordionContainer;

    private Elemental2DomUtil elemental2DomUtil;

    private ColumnsPagePresenter presenter;

    @Inject
    public ColumnsPageView(final HTMLDivElement columnsNoteInfo,
                           final HTMLButtonElement insertButton,
                           final HTMLDivElement ruleInheritanceContainer,
                           final HTMLDivElement ruleNameOptionContainer,
                           final HTMLDivElement accordionContainer,
                           final Elemental2DomUtil elemental2DomUtil) {

        this.columnsNoteInfo = columnsNoteInfo;
        this.insertButton = insertButton;
        this.ruleInheritanceContainer = ruleInheritanceContainer;
        this.ruleNameOptionContainer = ruleNameOptionContainer;
        this.accordionContainer = accordionContainer;
        this.elemental2DomUtil = elemental2DomUtil;
    }

    @Override
    public void init(final ColumnsPagePresenter presenter) {

        this.presenter = presenter;

        setColumnsNoteInfoAsHidden();
    }

    @Override
    public void setAccordion(final GuidedDecisionTableAccordion accordion) {

        final GuidedDecisionTableAccordion.View view = accordion.getView();
        final HTMLElement htmlElement = elemental2DomUtil.asHTMLElement(view.getElement());

        elemental2DomUtil.removeAllElementChildren(accordionContainer);

        accordionContainer.appendChild(htmlElement);
    }

    @Override
    public void setRuleInheritanceWidget(final IsWidget isWidget) {

        final Widget widget = isWidget.asWidget();

        elemental2DomUtil.removeAllElementChildren(ruleInheritanceContainer);
        elemental2DomUtil.appendWidgetToElement(ruleInheritanceContainer, widget);
    }

    @Override
    public void setRuleNameOptionWidget(final ShowRuleNameOptionPresenter showRuleNameOptionPresenter) {
        ruleNameOptionContainer.appendChild(showRuleNameOptionPresenter.getView().getElement());
    }

    @EventHandler("insert-column")
    public void onInsertColumnClick(final ClickEvent event) {
        presenter.openNewGuidedDecisionTableColumnWizard();
    }

    @Override
    public void setColumnsNoteInfoAsVisible() {
        columnsNoteInfo.hidden = false;
    }

    @Override
    public void setColumnsNoteInfoAsHidden() {
        columnsNoteInfo.hidden = true;
    }
}
