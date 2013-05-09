/*
 * Copyright 2011 JBoss Inc
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
package org.drools.workbench.screens.guided.dtable.client.handlers;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;

/**
 * A control providing options for creating a Guided Decision Table asset
 */
public class GuidedDecisionTableOptions extends Composite {

    interface GuidedDecisionTableOptionsBinder
            extends
            UiBinder<Widget, GuidedDecisionTableOptions> {

    }

    private static GuidedDecisionTableOptionsBinder uiBinder = GWT.create( GuidedDecisionTableOptionsBinder.class );

    @UiField
    CheckBox chkUseWizard;

    private boolean isUsingWizard = false;

    private GuidedDecisionTable52.TableFormat tableFormat = GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY;

    public GuidedDecisionTableOptions() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public boolean isUsingWizard() {
        return this.isUsingWizard;
    }

    public GuidedDecisionTable52.TableFormat getTableFormat() {
        return this.tableFormat;
    }

    @UiHandler(value = "chkUseWizard")
    void chkUseWizardClick( ClickEvent event ) {
        this.isUsingWizard = chkUseWizard.getValue();
    }

    @UiHandler(value = "optExtendedEntry")
    void optExtendedEntryClick( ClickEvent event ) {
        tableFormat = GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY;
    }

    @UiHandler(value = "optLimitedEntry")
    void optLimitedEntryClick( ClickEvent event ) {
        tableFormat = GuidedDecisionTable52.TableFormat.LIMITED_ENTRY;
    }

}
