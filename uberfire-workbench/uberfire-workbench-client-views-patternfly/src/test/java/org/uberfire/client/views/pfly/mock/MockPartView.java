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

package org.uberfire.client.views.pfly.mock;

import org.jboss.errai.ioc.client.api.TestMock;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter.View;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

@TestMock
public class MockPartView implements View {

    @Override
    public void init( WorkbenchPartPresenter presenter ) {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public Widget asWidget() {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public void onResize() {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public WorkbenchPartPresenter getPresenter() {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public void setWrappedWidget( IsWidget widget ) {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public IsWidget getWrappedWidget() {
        throw new UnsupportedOperationException( "Not implemented." );
    }

}
