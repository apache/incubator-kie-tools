/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.mvp;

import org.uberfire.client.workbench.WorkbenchPartPresenter;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * A mock Workbench Part view.
 */
public class MockWorkbenchPartView
    implements
    WorkbenchPartPresenter.View {

    @Override
    public Widget asWidget() {
        return null;
    }

    @Override
    public void onResize() {
    }

    @Override
    public void init(WorkbenchPartPresenter presenter) {
    }

    @Override
    public WorkbenchPartPresenter getPresenter() {
        return null;
    }

    @Override
    public void setWrappedWidget(IsWidget widget) {
    }

    @Override
    public IsWidget getWrappedWidget() {
        return null;
    }

}
