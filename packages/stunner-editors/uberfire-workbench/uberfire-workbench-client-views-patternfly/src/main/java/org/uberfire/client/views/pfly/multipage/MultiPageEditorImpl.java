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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.client.workbench.widgets.multipage.Page;

@Dependent
public class MultiPageEditorImpl implements MultiPageEditor {

    @Inject
    private MultiPageEditorViewImpl view;

    @Inject
    private Event<MultiPageEditorSelectedPageEvent> selectedPageEvent;

    @PostConstruct
    public void init() {
        view.enableSelectedPageEvent(selectedPageEvent);
    }

    @Override
    public void addPage(final Page page) {
        view.addPage(page);
    }

    @Override
    public void selectPage(final int index) {
        view.selectPage(index);
    }

    @Override
    public int selectedPage() {
        return view.selectedPage();
    }

    @Override
    public void addTabBarWidget(final IsWidget customWidget) {
        view.addTabBarWidget(customWidget);
    }

    @Override
    public void setTabBarVisible(final boolean visible) {
        view.setTabBarVisible(visible);
    }

    @Override
    public void clear() {
        view.clear();
    }

    @Override
    public Widget asWidget() {
        return view;
    }
}
