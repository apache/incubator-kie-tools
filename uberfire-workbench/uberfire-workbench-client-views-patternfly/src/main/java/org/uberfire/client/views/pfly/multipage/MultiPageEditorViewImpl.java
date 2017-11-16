/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.multipage;

import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.shared.event.TabShowEvent;
import org.gwtbootstrap3.client.shared.event.TabShowHandler;
import org.gwtbootstrap3.client.shared.event.TabShownEvent;
import org.gwtbootstrap3.client.shared.event.TabShownHandler;
import org.uberfire.client.views.pfly.tab.ResizeTabPanel;
import org.uberfire.client.views.pfly.tab.TabPanelEntry;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditorView;
import org.uberfire.client.workbench.widgets.multipage.Multiple;
import org.uberfire.client.workbench.widgets.multipage.Page;

@Dependent
@Multiple
public class MultiPageEditorViewImpl extends ResizeTabPanel implements MultiPageEditorView {

    @Override
    public void init() {
        super.init();

        this.addShowHandler(new TabShowHandler() {

            @Override
            public void onShow(TabShowEvent e) {
                onResize();
                final TabPanelEntry tabPanelEntry = MultiPageEditorViewImpl.this.findEntryForTabWidget(e.getTab());
                final PageViewImpl page = (PageViewImpl) tabPanelEntry.getContentPane().getWidget(0);
                page.onFocus();
            }
        });

        this.addShownHandler(new TabShownHandler() {

            @Override
            public void onShown(TabShownEvent e) {
                onResize();
                final TabPanelEntry tabPanelEntry = MultiPageEditorViewImpl.this.findEntryForTabWidget(e.getTab());
                final PageViewImpl page = (PageViewImpl) tabPanelEntry.getContentPane().getWidget(0);
                page.onLostFocus();
            }
        });

        this.tabBar.addStyleName("nav-tabs-pf");
        this.addStyleName("uf-multi-page-editor");
    }

    public void addPage(final Page page) {

        final TabPanelEntry tab = makeTabPanelEntry(page);

        addItem(tab);
        setAsActive(tab);
    }

    @Override
    public void addPage(int index, final Page page) {

        final TabPanelEntry tab = makeTabPanelEntry(page);

        insertItem(tab, index);
        setAsActive(tab);
    }

    TabPanelEntry makeTabPanelEntry(final Page page) {

        final String title = page.getLabel();
        final Widget contents = page.getView().asWidget();

        return new TabPanelEntry(title, contents);
    }

    void setAsActive(final TabPanelEntry tab) {
        if (this.getActiveTab() == null) {
            tab.showTab();
            tab.setActive(true);
        }
    }

    public void selectPage(int index) {
        this.selectTabIndex(index);
    }

    public int selectedPage() {
        return this.getSelectedTabIndex();
    }

    @Override
    public void disablePage(int index) {

        final Widget tab = getTabBar().getWidget(index);

        tab.addStyleName("disabled");
        disableWidget(tab);
    }

    @Override
    public void enablePage(int index) {

        final Widget tab = getTabBar().getWidget(index);

        tab.removeStyleName("disabled");
        enableWidget(tab);
    }

    private void enableWidget(final Widget tab) {
        style(tab).clearProperty("pointerEvents");
    }

    private void disableWidget(final Widget tab) {
        style(tab).setProperty("pointerEvents", "none");
    }

    private Style style(final Widget tab) {
        return tab.getElement().getStyle();
    }
}
