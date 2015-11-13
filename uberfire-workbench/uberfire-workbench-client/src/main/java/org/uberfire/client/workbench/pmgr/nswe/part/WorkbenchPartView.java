/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.client.workbench.pmgr.nswe.part;

import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

/**
 * A Workbench panel part.
 */
public class WorkbenchPartView
extends SimpleLayoutPanel
implements WorkbenchPartPresenter.View {

    private WorkbenchPartPresenter presenter;

    private final ScrollPanel sp = new ScrollPanel();

    @Override
    public void init( WorkbenchPartPresenter presenter ) {
        this.presenter = presenter;
        Layouts.setToFillParent( this );
    }

    @Override
    public WorkbenchPartPresenter getPresenter() {
        return this.presenter;
    }

    @Override
    public void setWrappedWidget( final IsWidget widget ) {
        sp.setWidget( widget );
    }

    @Override
    public IsWidget getWrappedWidget() {
        return sp.getWidget();
    }

    public WorkbenchPartView() {
        setWidget( sp );
        // ScrollPanel creates an additional internal div that we need to style
        sp.getElement().getFirstChildElement().setClassName( "uf-scroll-panel" );
    }

}
