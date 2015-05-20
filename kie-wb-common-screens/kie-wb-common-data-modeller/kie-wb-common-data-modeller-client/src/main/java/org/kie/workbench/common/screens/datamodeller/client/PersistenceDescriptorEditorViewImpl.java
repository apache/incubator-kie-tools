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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.DivWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.ClassRow;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.ClassRowImpl;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.PersistenceUnitPropertyGrid;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.ProjectClassList;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.PropertyRow;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.XMLViewer;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorEditorContent;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.Property;
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
    HelpInline persistenceUnitHelpInline;

    @UiField
    ListBox persistenceProviderDropdown;

    @UiField
    HelpInline persistenceProviderHelpInline;

    @UiField
    ListBox datasourceDropdown;

    @UiField
    HelpInline datasourceHelpInline;

    @UiField
    RadioButton transactionTypeRadioButton;

    @UiField
    HelpInline transactionTypeHelpInline;

    @UiField
    DivWidget propertiesGridPanel;

    @Inject
    PersistenceUnitPropertyGrid persistenceUnitProperties;

    @UiField
    DivWidget persistenceUnitClassesPanel;

    @Inject
    ProjectClassList persistenceUnitClasses;

    @Inject
    private XMLViewer xmlViewer;

    PersistenceDescriptorEditorContent content;

    PersistenceDescriptorModel model;

    Presenter presenter;

    public PersistenceDescriptorEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
        loadProviders();
        loadDataSources();
    }

    @PostConstruct
    void init() {
        propertiesGridPanel.add( persistenceUnitProperties );
        persistenceUnitClassesPanel.add( persistenceUnitClasses );
    }

    @Override
    public void setContent( PersistenceDescriptorEditorContent content, boolean readonly ) {
        this.content = content;
        this.model = content != null ? content.getDescriptorModel() : null;
        if ( model != null && model.getPersistenceUnit() != null ) {
            persistenceUnitTextBox.setText( model.getPersistenceUnit().getName() );
            persistenceProviderDropdown.setSelectedValue( model.getPersistenceUnit().getProvider() );
            datasourceDropdown.setSelectedValue( model.getPersistenceUnit().getJtaDataSource() );
            transactionTypeRadioButton.setValue( true );

            persistenceUnitProperties.fillList( wrappPropertiesList( content.getDescriptorModel().getPersistenceUnit().getProperties() ) );
            persistenceUnitClasses.fillList( wrappClassesList( content.getDescriptorModel().getPersistenceUnit().getClasses() ) );
        }
    }

    @Override
    public PersistenceDescriptorEditorContent getContent() {
        if ( model != null && model.getPersistenceUnit() != null ) {
            model.getPersistenceUnit().setClasses( unWrappClassesList( persistenceUnitClasses.getClasses() ) );
            model.getPersistenceUnit().setProperties( unWrappPropertiesList( persistenceUnitProperties.getProperties() ) );
        }
        return content;
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
    public void loadClasses( List<String> classes ) {
        persistenceUnitClasses.fillList( wrappClassesList( classes ) );
    }

    private void loadProviders() {
        //TODO load the list of platform registered JPA providers.
        persistenceProviderDropdown.clear();
        persistenceProviderDropdown.addItem( "Hibernate", "org.hibernate.ejb.HibernatePersistence" );
    }

    private void loadDataSources() {
        //TODO load the list of server defined data sources
        datasourceDropdown.clear();
        datasourceDropdown.addItem( "Not selected", "NOT_SELECTED" );
        datasourceDropdown.addItem( "ExampleDS", "java:jboss/datasources/ExampleDS" );
        datasourceDropdown.addItem( "Test1DS", "java:jboss/datasources/Test1DS" );
        datasourceDropdown.addItem( "Test2DS", "java:jboss/datasources/Test2DS" );
    }

    @UiHandler( "persistenceUnitTextBox" )
    void onPersistenceUnitChanged( ValueChangeEvent<String> event ) {
        presenter.onPersistenceUnitNameChanged( event.getValue() );
    }

    @UiHandler( "persistenceProviderDropdown" )
    void onPersistenceProviderChanged( ChangeEvent event ) {
        presenter.onPersistenceProviderChanged( persistenceProviderDropdown.getValue() );
    }

    @UiHandler( "datasourceDropdown" )
    void onJTADataSourceChanged( ChangeEvent event ) {
        presenter.onJTADataSourceChanged( datasourceDropdown.getValue() );
    }

    private List<PropertyRow> wrappPropertiesList( List<Property> properties ) {
        List<PropertyRow> wrapperList = new ArrayList<PropertyRow>(  );
        if ( properties == null ) return null;
        for ( Property property : properties ) {
            wrapperList.add( new PropertyWrapperRow( property ) );
        }
        return wrapperList;
    }

    private List<Property> unWrappPropertiesList( List<PropertyRow> propertyRows ) {
        List<Property> properties = new ArrayList<Property>(  );
        if ( propertyRows == null ) return null;
        for ( PropertyRow propertyRow : propertyRows ) {
            properties.add( new Property( propertyRow.getName(), propertyRow.getValue() ) );
        }
        return properties;
    }

    public static class PropertyWrapperRow implements PropertyRow {

        private Property property = new Property(  );

        public PropertyWrapperRow( Property property ) {
            if ( property != null ) {
                this.property = property;
            }
        }

        @Override public String getName() {
            return property.getName();
        }

        @Override public void setName( String name ) {
            property.setName( name );
        }

        @Override public String getValue() {
            return property.getValue();
        }

        @Override public void setValue( String value ) {
            property.setValue( value );
        }
    }

    private List<ClassRow> wrappClassesList( List<String> classes ) {
        List<ClassRow> classRows = new ArrayList<ClassRow>(  );
        if ( classes == null ) return null;
        for ( String clazz : classes ) {
            classRows.add( new ClassRowImpl( clazz ) );
        }
        return classRows;
    }

    private List<String> unWrappClassesList( List<ClassRow> classRows ) {
        List<String> classes = new ArrayList<String>(  );
        if ( classRows == null ) return null;
        for ( ClassRow classRow : classRows ) {
            classes.add( classRow.getClassName() );
        }
        return classes;
    }
}