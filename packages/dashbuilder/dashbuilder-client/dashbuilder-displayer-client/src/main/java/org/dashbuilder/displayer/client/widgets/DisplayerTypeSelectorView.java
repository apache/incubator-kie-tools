/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.widgets;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.resources.i18n.DisplayerTypeConstants;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;

@Dependent
public class DisplayerTypeSelectorView extends Composite implements DisplayerTypeSelector.View {

    interface ViewBinder extends UiBinder<Widget, DisplayerTypeSelectorView> {}
    private static ViewBinder uiBinder = GWT.create(ViewBinder.class);

    Map<DisplayerType, String> displayerNames = new HashMap<DisplayerType, String>();

    @UiField
    NavTabs navTabs;

    @UiField
    TabPane displayerSubTypePane;

    DisplayerTypeSelector presenter;

    @Override
    public void init(DisplayerTypeSelector presenter) {
        this.presenter = presenter;

        displayerNames.put(DisplayerType.BARCHART, DisplayerTypeConstants.INSTANCE.displayer_type_selector_tab_bar());
        displayerNames.put(DisplayerType.PIECHART, DisplayerTypeConstants.INSTANCE.displayer_type_selector_tab_pie());
        displayerNames.put(DisplayerType.LINECHART, DisplayerTypeConstants.INSTANCE.displayer_type_selector_tab_line());
        displayerNames.put(DisplayerType.AREACHART, DisplayerTypeConstants.INSTANCE.displayer_type_selector_tab_area());
        displayerNames.put(DisplayerType.BUBBLECHART, DisplayerTypeConstants.INSTANCE.displayer_type_selector_tab_bubble());
        displayerNames.put(DisplayerType.METERCHART, DisplayerTypeConstants.INSTANCE.displayer_type_selector_tab_meter());
        displayerNames.put(DisplayerType.METRIC, DisplayerTypeConstants.INSTANCE.displayer_type_selector_tab_metric());
        displayerNames.put(DisplayerType.MAP, DisplayerTypeConstants.INSTANCE.displayer_type_selector_tab_map());
        displayerNames.put(DisplayerType.TABLE, DisplayerTypeConstants.INSTANCE.displayer_type_selector_tab_table());
        displayerNames.put(DisplayerType.SELECTOR, DisplayerTypeConstants.INSTANCE.displayer_type_selector_tab_selector());

        initWidget(uiBinder.createAndBindUi(this));
        displayerSubTypePane.add(presenter.getSubtypeSelector());
        displayerSubTypePane.setActive(true);
    }

    @Override
    public void clear() {
        navTabs.clear();
    }

    @Override
    public void show(DisplayerType type) {
        String displayerName = displayerNames.get(type);
        DisplayerTab tab = new DisplayerTab(displayerName, type);
        tab.setDataTargetWidget(displayerSubTypePane);
        navTabs.add(tab);
    }

    @Override
    public void select(DisplayerType type) {
        for (int i=0; i<navTabs.getWidgetCount(); i++) {
            DisplayerTab tab = (DisplayerTab) navTabs.getWidget(i);
            tab.setActive(tab.type.equals(type));
        }
    }

    private class DisplayerTab extends TabListItem {
        String name;
        DisplayerType type;

        public DisplayerTab(final String name, final DisplayerType type) {
            super(name);
            this.name = name;
            this.type = type;

            super.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    event.stopPropagation();
                    if (!presenter.getSelectedType().equals(type)) {
                        presenter.onSelect(type);
                    }
                }
            });
        }
    }
}
