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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class PackageNameFormPopupViewImpl
        extends BaseModal
        implements PackageNameFormPopupView {

    private Presenter presenter;

    interface PackageNameFormPopupViewImplBinder
            extends
            UiBinder<Widget, PackageNameFormPopupViewImpl> {

    }

    private static PackageNameFormPopupViewImplBinder uiBinder = GWT.create( PackageNameFormPopupViewImplBinder.class );

    @UiField
    TextBox selectedNameTextBox;

    @UiField
    DropDownMenu nameDropDown;

    public PackageNameFormPopupViewImpl() {
        add( new ModalBody() {{
            add( uiBinder.createAndBindUi( PackageNameFormPopupViewImpl.this ) );
        }} );
        add( new ModalFooterOKCancelButtons( new Command() {
            @Override
            public void execute() {
                presenter.onOk();
            }
        }, new Command() {
            @Override
            public void execute() {
                hide();
            }
        }
        ) );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public String getName() {
        return selectedNameTextBox.getText();
    }

    @Override
    public void addItem( final String packageName ) {
        AnchorListItem navLink = new AnchorListItem( packageName );
        navLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                selectedNameTextBox.setText( packageName );
            }
        } );
        nameDropDown.add( navLink );
    }

    @Override
    public void setName( String name ) {
        selectedNameTextBox.setText( name );
    }

    @Override
    public void showFieldEmptyWarning() {
        ErrorPopup.showMessage( CommonConstants.INSTANCE.PleaseSetAName() );
    }
}
