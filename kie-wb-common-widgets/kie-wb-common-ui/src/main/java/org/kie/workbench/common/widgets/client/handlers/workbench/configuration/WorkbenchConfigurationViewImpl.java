/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.widgets.client.handlers.workbench.configuration;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

@ApplicationScoped
public class WorkbenchConfigurationViewImpl extends BaseModal implements WorkbenchConfigurationPresenter.WorkbenchConfigurationView {

    interface WorkbenchConfigurationViewBinder extends UiBinder<Widget, WorkbenchConfigurationViewImpl> {

    }

    @UiField
    HTMLPanel view;

    private static WorkbenchConfigurationViewBinder uiBinder = GWT.create( WorkbenchConfigurationViewBinder.class );

    private WorkbenchConfigurationPresenter presenter;

    private WorkbenchConfigurationHandler activeHandler;

    private final Command okCommand = new Command() {

        @Override
        public void execute() {
            onOKButtonClick();
            hide();
        }
    };

    private final Command cancelCommand = new Command() {

        @Override
        public void execute() {
            hide();
        }
    };

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( okCommand, cancelCommand );

    public WorkbenchConfigurationViewImpl() {
        footer.enableOkButton( true );
        add( new ModalBody() {{
            add( uiBinder.createAndBindUi( WorkbenchConfigurationViewImpl.this ) );
        }} );
        add( footer );
    }

    private void setup() {
        if ( activeHandler != null ) {
            view.clear();
            activeHandler.loadUserWorkbenchPreferences();
            for ( Pair<String, ? extends Composite> p : activeHandler.getExtensions() ) {
                view.add( p.getK2() );
            }
        }
    }

    @Override
    public void init( final WorkbenchConfigurationPresenter presenter ) {
        this.presenter = presenter;

    }

    @Override
    public void setActiveHandler( final WorkbenchConfigurationHandler activeHandler ) {
        this.activeHandler = activeHandler;
    }

    @Override
    public void show() {
        setup();
        super.show();
    }

    private void onOKButtonClick() {
        if ( activeHandler != null ) {
            activeHandler.configurationSetting( false );
            activeHandler.saveUserWorkbenchPreferences();
        }
    }
}
