/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.widgets.metadata.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.source.ViewDRLSourceWidget;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorImpl;
import org.uberfire.client.views.pfly.multipage.PageImpl;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.ext.editor.commons.client.BaseEditorView;

public class KieEditorWrapperViewImpl
        extends MultiPageEditorImpl
        implements KieEditorWrapperView {

    protected static final int EDITOR_TAB_INDEX = 0;
    protected static final int OVERVIEW_TAB_INDEX = 1;

    private KieEditorWrapperPresenter presenter;

    @Override
    public void setPresenter( KieEditorWrapperPresenter presenter ) {

        this.presenter = presenter;
    }

    @Override
    public MultiPageEditor getMultiPage() {
        return this;
    }

    @Override
    public void addMainEditorPage( BaseEditorView baseView ) {
        addPage( new PageImpl( baseView,
                               CommonConstants.INSTANCE.EditTabTitle() ) {
            @Override
            public void onFocus() {
                presenter.onEditTabSelected();
            }

            @Override
            public void onLostFocus() {
                presenter.onEditTabUnselected();
            }
        } );
    }

    @Override
    public void addOverviewPage( final OverviewWidgetPresenter overviewWidget,
                                 final Command onFocus ) {
        addPage( new PageImpl( overviewWidget,
                               CommonConstants.INSTANCE.Overview() ) {
            @Override
            public void onFocus() {
                onFocus.execute();
                presenter.onOverviewSelected();
            }

            @Override
            public void onLostFocus() {

            }
        } );
    }

    @Override
    public void addSourcePage( ViewDRLSourceWidget sourceWidget ) {
        addPage( new PageImpl( sourceWidget,
                               CommonConstants.INSTANCE.SourceTabTitle() ) {
            @Override
            public void onFocus() {
                presenter.onSourceTabSelected();
            }

            @Override
            public void onLostFocus() {

            }
        } );
    }

    @Override
    public void addImportsTab( IsWidget importsWidget ) {
        addWidget( importsWidget,
                   CommonConstants.INSTANCE.ConfigTabTitle() );
    }

    @Override
    public boolean isEditorTabSelected() {
        return selectedPage() == EDITOR_TAB_INDEX;
    }

    @Override
    public boolean isOverviewTabSelected() {
        return selectedPage() == OVERVIEW_TAB_INDEX;
    }

    @Override
    public int getSelectedTabIndex() {
        return selectedPage();
    }

    @Override
    public void selectOverviewTab() {
        setSelectedTab( OVERVIEW_TAB_INDEX );
    }

    @Override
    public void selectEditorTab() {
        setSelectedTab( EDITOR_TAB_INDEX );
    }

    @Override
    public void setSelectedTab( int tabIndex ) {
        selectPage( tabIndex );
    }
}
