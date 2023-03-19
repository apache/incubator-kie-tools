/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TabPanel;

public class RendererSelectorTabListView extends Composite implements RendererSelector.TabListView {

    interface RendererSelectorBinder extends UiBinder<Widget, RendererSelectorTabListView> {}
    private static final RendererSelectorBinder uiBinder = GWT.create(RendererSelectorBinder.class);

    @UiField
    TabPanel tabPanel;

    @UiField
    TabContent tabContent;

    @UiField
    NavTabs navTabs;

    RendererSelector presenter = null;
    TabListItem selectedTab = null;

    @Override
    public void init(final RendererSelector presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width + "px");
        tabPanel.setWidth(width + "px");
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height + "px");
        tabPanel.setHeight(height + "px");
    }

    @Override
    public void clearRendererSelector() {
        tabContent.clear();
        navTabs.clear();
        selectedTab = null;
    }

    @Override
    public void addRendererItem(final String renderer) {
        final TabPane pane = new TabPane();
        final TabListItem tabListItem = new TabListItem();

        tabListItem.setDataTargetWidget(pane);
        tabListItem.setText(renderer);
        tabListItem.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                selectedTab = tabListItem;
                presenter.onRendererSelected();
            }
        });

        tabContent.add(pane);
        navTabs.add(tabListItem);
    }

    @Override
    public void setSelectedRendererIndex(int index) {
        TabListItem tabListItem = (TabListItem) navTabs.getWidget(index);
        tabListItem.setActive(true);
        tabListItem.showTab();
    }

    @Override
    public String getRendererSelected() {
        return selectedTab == null ? null : selectedTab.getText().trim();
    }
}