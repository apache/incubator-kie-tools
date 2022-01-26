/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.popups.list;

import java.util.List;

import org.gwtproject.core.client.GWT;
import org.gwtproject.uibinder.client.UiBinder;
import org.gwtproject.uibinder.client.UiField;
import org.gwtproject.uibinder.client.UiTemplate;
import org.gwtproject.user.client.Command;
import org.gwtproject.user.client.ui.ListBox;
import org.gwtproject.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.commons.Pair;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class FormListPopupViewImpl
        extends BaseModal
        implements FormListPopupView {

    private Presenter presenter;

    @UiTemplate
    interface AddNewKBasePopupViewImplBinder
            extends
            UiBinder<Widget, FormListPopupViewImpl> {

    }

    private static AddNewKBasePopupViewImplBinder uiBinder = new FormListPopupViewImpl_AddNewKBasePopupViewImplBinderImpl();

    @UiField
    ListBox listItems;

    public FormListPopupViewImpl() {
        final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( new Command() {
            @Override
            public void execute() {
                presenter.onOk();
                hide();
            }
        }, new Command() {
            @Override
            public void execute() {
                hide();
            }
        }
        );

        add( new ModalBody() {{
            add( uiBinder.createAndBindUi( FormListPopupViewImpl.this ) );
        }} );
        add( footer );
        setTitle( CommonConstants.INSTANCE.New() );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setItems( final List<Pair<String, String>> items ) {
        listItems.clear();
        for ( Pair<String, String> item : items ) {
            listItems.addItem( item.getK1(),
                               item.getK2() );
        }
    }

    @Override
    public Pair<String, String> getSelectedItem() {
        final int selectedIndex = listItems.getSelectedIndex();
        if ( selectedIndex == -1 ) {
            return Pair.newPair( "", "" );
        }
        final String text = listItems.getItemText( selectedIndex );
        final String value = listItems.getValue( selectedIndex );
        return Pair.newPair( text, value );
    }

    @Override
    public void showFieldEmptyWarning() {
        ErrorPopup.showMessage( CommonConstants.INSTANCE.PleaseSetAName() );
    }

}
