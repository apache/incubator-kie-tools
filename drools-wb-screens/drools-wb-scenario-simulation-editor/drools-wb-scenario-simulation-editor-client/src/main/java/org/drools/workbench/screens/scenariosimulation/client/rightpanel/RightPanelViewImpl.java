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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class RightPanelViewImpl
        extends Composite
        implements RightPanelView {

    private Presenter presenter;

    @DataField("rightPanelTabs")
    private UListElement rightPanelTabs = Document.get().createULElement();

    @DataField("editorTab")
    protected LIElement editorTab = Document.get().createLIElement();  // protected for test purpose

    @DataField("cheatSheetTab")
    protected LIElement cheatSheetTab = Document.get().createLIElement();  // protected for test purpose

    @DataField("editorTabContent")
    protected DivElement editorTabContent = Document.get().createDivElement();  // protected for test purpose

    @DataField("cheatSheetTabContent")
    protected DivElement cheatSheetTabContent = Document.get().createDivElement(); // protected for test purpose

    public RightPanelViewImpl() {

    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("editorTab")
    public void onEditorTabClick(ClickEvent event) {
        presenter.onEditorTabActivated();
    }

    @EventHandler("cheatSheetTab")
    public void onCheatSheetTabClick(ClickEvent event) {
        presenter.onCheatSheetTabActivated();
    }

    @Override
    public void showEditorTab() {
        showTab(editorTab, editorTabContent);
    }

    @Override
    public void hideCheatSheetTab() {
        hideTab(cheatSheetTab, cheatSheetTabContent);
    }

    @Override
    public void showCheatSheetTab() {
        showTab(cheatSheetTab, cheatSheetTabContent);
    }

    @Override
    public void hideEditorTab() {
        hideTab(editorTab, editorTabContent);
    }

    private void showTab(LIElement tab, DivElement content) {
        tab.setAttribute("class", "active");
        content.removeAttribute("hidden");
    }

    private void hideTab(LIElement tab, DivElement content) {
        tab.removeClassName("active");
        content.setAttribute("hidden", null);
    }
}
