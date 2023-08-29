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


package org.uberfire.client.views.pfly.multipage;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;

import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.shared.event.TabShownHandler;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.uberfire.client.views.pfly.tab.ResizeTabPanel;
import org.uberfire.client.views.pfly.tab.TabPanelEntry;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditorView;
import org.uberfire.client.workbench.widgets.multipage.Page;

@Dependent
public class MultiPageEditorViewImpl extends ResizeTabPanel implements MultiPageEditorView {

    private Event<MultiPageEditorSelectedPageEvent> selectedPageEvent;

    @Override
    public void init() {
        super.init();

        this.addShowHandler(e -> {
            onResize();
            final TabPanelEntry tabPanelEntry = MultiPageEditorViewImpl.this.findEntryForTabWidget(e.getTab());
            final PageViewImpl page = (PageViewImpl) tabPanelEntry.getContentPane().getWidget(0);
            page.onFocus();
        });

        this.addShownHandler(getTabShownHandler());

        this.tabBar.addStyleName("nav-tabs-pf");
        this.addStyleName("uf-multi-page-editor");
    }

    public void enableSelectedPageEvent(final Event<MultiPageEditorSelectedPageEvent> selectedPageEvent) {
        this.selectedPageEvent = selectedPageEvent;
    }

    public void addPage(final Page page) {

        final TabPanelEntry tab = makeTabPanelEntry(page);

        addItem(tab);
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

    TabShownHandler getTabShownHandler() {
        return event -> {
            onResize();

            final TabListItem tab = event.getTab();
            final TabPanelEntry tabPanelEntry = findEntryForTabWidget(tab);
            final PageViewImpl page = (PageViewImpl) tabPanelEntry.getContentPane().getWidget(0);

            getSelectedPageEvent().ifPresent(spEvent -> {
                spEvent.fire(new MultiPageEditorSelectedPageEvent());
            });

            page.onLostFocus();
        };
    }

    private Optional<Event<MultiPageEditorSelectedPageEvent>> getSelectedPageEvent() {
        return Optional.ofNullable(selectedPageEvent);
    }
}
