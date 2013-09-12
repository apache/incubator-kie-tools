/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.drltext.client.editor;

import javax.annotation.PostConstruct;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import org.drools.workbench.screens.drltext.client.widget.FactTypeBrowserWidget;
import org.drools.workbench.screens.drltext.client.widget.RuleContentWidget;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.drools.workbench.models.commons.shared.oracle.PackageDataModelOracle;
import org.uberfire.client.common.BusyPopup;

public class DRLEditorViewImpl
        extends Composite
        implements DRLEditorView {

    private RuleContentWidget ruleContentWidget = null;
    private FactTypeBrowserWidget browser = null;

    @PostConstruct
    public void init() {
        this.ruleContentWidget = new RuleContentWidget();

        final FactTypeBrowserWidget.ClickEvent ce = new FactTypeBrowserWidget.ClickEvent() {
            public void selected( String text ) {
                ruleContentWidget.insertText( text );
            }
        };

        final Grid layout = new Grid( 1,
                                      2 );

        this.browser = new FactTypeBrowserWidget( ce );

        layout.setWidget( 0,
                          0,
                          browser );
        layout.setWidget( 0,
                          1,
                          ruleContentWidget );

        layout.getColumnFormatter().setWidth( 0,
                                              "10%" );
        layout.getColumnFormatter().setWidth( 1,
                                              "90%" );
        layout.getCellFormatter().setAlignment( 0,
                                                0,
                                                HasHorizontalAlignment.ALIGN_LEFT,
                                                HasVerticalAlignment.ALIGN_TOP );
        layout.getCellFormatter().setAlignment( 0,
                                                1,
                                                HasHorizontalAlignment.ALIGN_LEFT,
                                                HasVerticalAlignment.ALIGN_TOP );
        layout.setWidth( "95%" );

        initWidget( layout );
    }

    @Override
    public void setContent( final String content,
                            final PackageDataModelOracle dataModel ) {
        ruleContentWidget.setContent( content, 15 );
        browser.setDataModel( dataModel );
    }

    @Override
    public String getContent() {
        return ruleContentWidget.getContent();
    }

    @Override
    public boolean isDirty() {
        return ruleContentWidget.isDirty();
    }

    @Override
    public void setNotDirty() {
        ruleContentWidget.setNotDirty();
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }

    @Override
    public void alertReadOnly() {
        Window.alert( CommonConstants.INSTANCE.CantSaveReadOnly() );
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

}
