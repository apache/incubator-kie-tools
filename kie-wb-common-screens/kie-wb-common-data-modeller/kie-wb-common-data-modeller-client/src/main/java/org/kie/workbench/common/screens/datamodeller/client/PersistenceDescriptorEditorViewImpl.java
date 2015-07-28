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

package org.kie.workbench.common.screens.datamodeller.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.Radio;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.PersistenceUnitPropertyGrid;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.ProjectClassList;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.XMLViewer;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;

public class PersistenceDescriptorEditorViewImpl
        extends KieEditorViewImpl
        implements PersistenceDescriptorEditorView {

    interface PersistenceDescriptorEditorViewBinder
            extends
            UiBinder<Widget, PersistenceDescriptorEditorViewImpl> {

    }

    private static PersistenceDescriptorEditorViewBinder uiBinder = GWT.create( PersistenceDescriptorEditorViewBinder.class );

    @UiField
    TextBox persistenceUnitTextBox;

    @UiField
    HelpBlock persistenceUnitHelpInline;

    @UiField
    TextBox persistenceProviderTextBox;

    @UiField
    HelpBlock persistenceProviderHelpInline;

    @UiField
    TextBox datasourceTextBox;

    @UiField
    HelpBlock datasourceHelpInline;

    @UiField
    Radio transactionTypeJTARadioButton;

    @UiField
    Radio transactionTypeResourceLocalRadioButton;

    @UiField
    HelpBlock transactionTypeHelpInline;

    @UiField
    PanelBody propertiesGridPanel;

    @Inject
    PersistenceUnitPropertyGrid persistenceUnitProperties;

    @UiField
    PanelBody persistenceUnitClassesPanel;

    @Inject
    ProjectClassList persistenceUnitClasses;

    @Inject
    private XMLViewer xmlViewer;

    Presenter presenter;

    public PersistenceDescriptorEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    void init() {
        propertiesGridPanel.add( persistenceUnitProperties );
        persistenceUnitClassesPanel.add( persistenceUnitClasses );
    }

    @Override
    public String getPersistenceUnitName() {
        return persistenceUnitTextBox.getText();
    }

    @Override
    public void setPersistenceUnitName( String persistenceUnitName ) {
        persistenceUnitTextBox.setText( persistenceUnitName );
    }

    @Override
    public String getPersistenceProvider() {
        return persistenceProviderTextBox.getText();
    }

    @Override
    public void setPersistenceProvider( String persistenceProvider ) {
        persistenceProviderTextBox.setText( persistenceProvider );
    }

    @Override
    public String getJTADataSource() {
        return datasourceTextBox.getText();
    }

    @Override
    public void setJTADataSource( String jtaDataSource ) {
        datasourceTextBox.setText( jtaDataSource );
    }

    @Override
    public boolean getJTATransactions() {
        return transactionTypeJTARadioButton.getValue();
    }

    @Override
    public void setJTATransactions( boolean jtaTransactions ) {
        transactionTypeJTARadioButton.setValue( jtaTransactions );
    }

    @Override
    public boolean getResourceLocalTransactions() {
        return transactionTypeResourceLocalRadioButton.getValue();
    }

    @Override
    public void setResourceLocalTransactions( boolean resourceLocalTransactions ) {
        transactionTypeResourceLocalRadioButton.setValue( resourceLocalTransactions );
    }

    @Override
    public void setResourceLocalTransactionsVisible( boolean visible ) {
        transactionTypeResourceLocalRadioButton.setVisible( visible );
    }

    @Override
    public void setTransactionTypeHelpMessage( String message ) {
        transactionTypeHelpInline.setText( message );
    }

    @Override
    public void setSource( String source ) {
        xmlViewer.setContent( source );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
        persistenceUnitClasses.addLoadClassesHandler( presenter );
    }

    @Override
    public Widget getSourceEditor() {
        return xmlViewer;
    }

    @Override
    public PersistenceUnitPropertyGrid getPersistenceUnitProperties() {
        return persistenceUnitProperties;
    }

    @Override
    public ProjectClassList getPersistenceUnitClasses() {
        return persistenceUnitClasses;
    }

    @Override
    public void redraw() {
        persistenceUnitProperties.redraw();
        persistenceUnitClasses.redraw();
    }

    @Override
    public void clear() {
        setPersistenceUnitName( null );
        setPersistenceProvider( null );
        setJTADataSource( null );
        setJTATransactions( false );
        setResourceLocalTransactions( false );
        setResourceLocalTransactionsVisible( false );
        setTransactionTypeHelpMessage( null );
    }

    @Override
    public void setReadOnly( boolean readOnly ) {
        persistenceUnitTextBox.setReadOnly( readOnly );
        persistenceProviderTextBox.setReadOnly( readOnly );
        datasourceTextBox.setReadOnly( readOnly );
        transactionTypeJTARadioButton.setEnabled( !readOnly );
        transactionTypeResourceLocalRadioButton.setEnabled( !readOnly );
        persistenceUnitProperties.setReadOnly( readOnly );
        persistenceUnitClasses.setReadOnly( readOnly );
    }

    @UiHandler("persistenceUnitTextBox")
    void onPersistenceUnitChanged( ValueChangeEvent<String> event ) {
        presenter.onPersistenceUnitNameChange();
    }

    @UiHandler("persistenceProviderTextBox")
    void onPersistenceProviderChanged( ChangeEvent event ) {
        presenter.onPersistenceProviderChange();
    }

    @UiHandler("datasourceTextBox")
    void onJTADataSourceChanged( ChangeEvent event ) {
        presenter.onJTADataSourceChange();
    }

    @UiHandler("transactionTypeResourceLocalRadioButton")
    void onTransactionTypeResourceLocalRadioButtonChanged( ClickEvent event ) {
        presenter.onJTATransactionsChange();
    }

    @UiHandler("transactionTypeJTARadioButton")
    void onTransactionTypeJTARadioButtonChanged( ClickEvent event ) {
        presenter.onResourceLocalTransactionsChange();
    }
}