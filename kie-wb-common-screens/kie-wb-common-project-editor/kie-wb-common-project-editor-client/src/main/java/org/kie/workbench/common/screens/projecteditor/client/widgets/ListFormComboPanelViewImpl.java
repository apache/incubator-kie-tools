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

package org.kie.workbench.common.screens.projecteditor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.ListBox;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

public class ListFormComboPanelViewImpl
        extends Composite
        implements ListFormComboPanelView {

    private Presenter presenter;
    private Form form;

    @UiTemplate("ListFormComboPanelViewImpl.ui.xml")
    interface ListFormComboPanelViewImplBinder
            extends
            UiBinder<Widget, ListFormComboPanelViewImpl> {

    }

    private static ListFormComboPanelViewImplBinder uiBinder = GWT.create( ListFormComboPanelViewImplBinder.class );

    @UiField
    ListBox list;

    @UiField
    Column kSessionForm;

    @UiField
    Button makeDefaultButton;

    @UiField
    Button addButton;

    @UiField
    Button renameButton;

    @UiField
    Button deleteButton;

    public ListFormComboPanelViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void addItem( String fullName ) {
        list.addItem( fullName );
    }

    @Override
    public void remove( String fullName ) {
        for ( int i = 0; i < list.getItemCount(); i++ ) {
            if ( list.getItemText( i ).equals( fullName ) ) {
                list.removeItem( i );
                break;
            }
        }
    }

    @Override
    public void clearList() {
        list.clear();
    }

    @Override
    public void setForm( Form form ) {
        kSessionForm.clear();
        this.form = form;
        kSessionForm.add( form );
    }

    @Override
    public void setSelected( String fullName ) {
        for ( int i = 0; i < list.getItemCount(); i++ ) {
            if ( list.getItemText( i ).equals( fullName ) ) {
                list.setSelectedIndex( i );
                break;
            }
        }
    }

    @Override
    public void showPleaseSelectAnItem() {
        ErrorPopup.showMessage( ProjectEditorResources.CONSTANTS.PleaseSelectAnItem() );
    }

    @Override
    public void showThereAlreadyExistAnItemWithTheGivenNamePleaseSelectAnotherName() {
        ErrorPopup.showMessage( ProjectEditorResources.CONSTANTS.ThereAlreadyExistAnItemWithTheGivenNamePleaseSelectAnotherName() );
    }

    @Override
    public void showXsdIDError() {
        ErrorPopup.showMessage( ProjectEditorResources.CONSTANTS.XsdIDError() );

    }

    @UiHandler("list")
    public void handleChange( ChangeEvent event ) {
        String value = list.getValue( list.getSelectedIndex() );
        presenter.onSelect( value );
    }

    @UiHandler("addButton")
    public void add( ClickEvent clickEvent ) {
        presenter.onAdd();
    }

    @UiHandler("renameButton")
    public void rename( ClickEvent clickEvent ) {
        presenter.onRename();
    }

    @UiHandler("deleteButton")
    public void delete( ClickEvent clickEvent ) {
        presenter.onRemove();
    }

    @UiHandler("makeDefaultButton")
    public void makeDefault( ClickEvent clickEvent ) {
        presenter.onMakeDefault();
    }

    @Override
    public void enableItemEditingButtons() {
        renameButton.setEnabled( true );
        deleteButton.setEnabled( true );
    }

    @Override
    public void disableItemEditingButtons() {
        renameButton.setEnabled( false );
        deleteButton.setEnabled( false );
    }

    @Override
    public void enableMakeDefault() {
        makeDefaultButton.setEnabled( true );
    }

    @Override
    public void disableMakeDefault() {
        makeDefaultButton.setEnabled( false );
    }

    @Override
    public void makeReadOnly() {
        addButton.setEnabled( false );
        renameButton.setEnabled( false );
        deleteButton.setEnabled( false );
        makeDefaultButton.setEnabled( false );
        form.makeReadOnly();
    }
}
