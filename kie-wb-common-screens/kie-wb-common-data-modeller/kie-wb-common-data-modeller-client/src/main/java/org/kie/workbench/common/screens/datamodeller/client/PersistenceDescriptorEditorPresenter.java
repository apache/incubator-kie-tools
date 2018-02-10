/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.ClassRow;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.ClassRowImpl;
import org.kie.workbench.common.screens.datamodeller.client.widgets.datasourceselector.DataSourceInfo;
import org.kie.workbench.common.screens.datamodeller.client.widgets.datasourceselector.DataSourceSelector;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.PropertyRow;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.type.PersistenceDescriptorType;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorEditorContent;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.Property;
import org.kie.workbench.common.screens.datamodeller.model.persistence.TransactionType;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorEditorService;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorService;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.annotations.Customizable;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.views.pfly.multipage.PageImpl;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchEditor( identifier = "PersistenceDescriptorEditor",
        supportedTypes = { PersistenceDescriptorType.class },
        priority = Integer.MAX_VALUE )
public class PersistenceDescriptorEditorPresenter
        extends KieEditor<PersistenceDescriptorEditorContent>
        implements PersistenceDescriptorEditorView.Presenter {

    private PersistenceDescriptorEditorView view;

    private PersistenceDescriptorType type;

    private DataSourceSelector dataSourceSelector;

    private Caller<PersistenceDescriptorEditorService> editorService;

    private Caller<PersistenceDescriptorService> descriptorService;

    private Caller<DataModelerService> dataModelerService;

    private ValidationPopup validationPopup;

    private PersistenceDescriptorEditorContent content;

    private boolean createIfNotExists = false;

    @Inject
    public PersistenceDescriptorEditorPresenter( final PersistenceDescriptorEditorView baseView,
                                                 final PersistenceDescriptorType type,
                                                 @Customizable final DataSourceSelector dataSourceSelector,
                                                 final Caller<PersistenceDescriptorEditorService> editorService,
                                                 final Caller<PersistenceDescriptorService> descriptorService,
                                                 final Caller<DataModelerService> dataModelerService,
                                                 final ValidationPopup validationPopup ) {
        super( baseView );
        this.view = baseView;
        baseView.setPresenter( this );
        this.type = type;
        this.dataSourceSelector = dataSourceSelector;
        this.editorService = editorService;
        this.descriptorService = descriptorService;
        this.dataModelerService = dataModelerService;
        this.validationPopup = validationPopup;
        view.showDataSourceSelector( dataSourceIntegrationEnabled( ) );
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
            final PlaceRequest place ) {
        this.createIfNotExists = "true".equals( place.getParameter( "createIfNotExists", null ) );
        init( path, place, type );
    }

    @OnFocus
    public void onFocus() {
        //for some reason the tables needs to be repainted when the view is restored.
        view.redraw();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        if ( versionRecordManager.getCurrentPath() != null ) {
            super.getTitleText();
        } else {
            //TODO check why I'm getting this case and remove after checked
            return type.getDescription();
        }
        return super.getTitleText();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return super.getWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        makeMenuBar();
        return super.menus;
    }

    @Override
    protected void loadContent() {
        editorService.call( getLoadModelSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback( view ) ).loadContent(
                versionRecordManager.getCurrentPath(),
                createIfNotExists );
    }

    @OnMayClose
    public boolean onMayClose() {
        if ( isDirty( getContent().hashCode() ) ) {
            return view.confirmClose();
        }
        return true;
    }

    @OnClose
    public void OnClose() {
        super.OnClose();
    }

    private RemoteCallback<PersistenceDescriptorEditorContent> getLoadModelSuccessCallback() {

        return new RemoteCallback<PersistenceDescriptorEditorContent>() {
            @Override
            public void callback( PersistenceDescriptorEditorContent content ) {

                if ( versionRecordManager.getCurrentPath() == null ) {
                    return;
                }
                if ( content.isCreated() ) {
                    versionRecordManager.reloadVersions( content.getPath() );
                }

                resetEditorPages( content.getOverview() );
                addSourcePage();

                createOriginalHash( content );
                setContent( content );
                view.setReadOnly( isReadOnly );
                updateSource( content.getSource() );

                view.hideBusyIndicator();
            }
        };
    }

    private void setContent( PersistenceDescriptorEditorContent content ) {
        this.content = content;
        PersistenceDescriptorModel descriptorModel = content != null ? content.getDescriptorModel() : null;
        PersistenceUnitModel persistenceUnitModel = descriptorModel != null ? descriptorModel.getPersistenceUnit() : null;
        view.clear();
        if ( persistenceUnitModel != null ) {
            view.setPersistenceUnitName( persistenceUnitModel.getName() );
            view.setPersistenceProvider( persistenceUnitModel.getProvider() );
            view.setJTADataSource( persistenceUnitModel.getJtaDataSource() );

            view.setJTATransactions( false );
            view.setResourceLocalTransactions( false );

            if ( TransactionType.JTA.equals( persistenceUnitModel.getTransactionType() ) ) {
                view.setJTATransactions( true );
            } else if ( TransactionType.RESOURCE_LOCAL.equals( persistenceUnitModel.getTransactionType() ) ) {
                view.setResourceLocalTransactions( true );
                view.setResourceLocalTransactionsVisible( true );
                updateResourceLocalTransactionsAlert();
            }

            view.getPersistenceUnitProperties().setProperties( wrappPropertiesList( persistenceUnitModel.getProperties() ) );
            view.getPersistenceUnitClasses().setClasses( wrappClassesList( persistenceUnitModel.getClasses() ) );
        }
    }

    public PersistenceDescriptorEditorContent getContent() {
        updateContent();
        return content;
    }

    protected void addSourcePage() {

        addPage( new PageImpl( view.getSourceEditor(),
                CommonConstants.INSTANCE.SourceTabTitle() ) {
            @Override
            public void onFocus() {
                onSourceTabSelected();
            }

            @Override
            public void onLostFocus() {

            }

        } );
    }

    protected void updateSource(String source) {
        view.setSource( source );
    }

    protected void save() {
        savePopUpPresenter.show( versionRecordManager.getCurrentPath(),
                new ParameterizedCommand<String>() {
                    @Override
                    public void execute( final String commitMessage ) {
                        editorService.call( getSaveSuccessCallback( getContent().hashCode() ) ).save( versionRecordManager.getCurrentPath(),
                                getContent(),
                                metadata,
                                commitMessage );
                    }
                }
        );
        concurrentUpdateSessionInfo = null;
    }

    @Override
    public void onSourceTabSelected() {
        PersistenceDescriptorModel persistenceDescriptor = getContent().getDescriptorModel();
        if ( persistenceDescriptor != null ) {
            view.showBusyIndicator( Constants.INSTANCE.persistence_descriptor_editor_loading_source_message() );
            descriptorService.call( new RemoteCallback<String>() {
                                        @Override
                                        public void callback( String source ) {
                                            view.hideBusyIndicator();
                                            updateSource( source );
                                        }
                                    }, new HasBusyIndicatorDefaultErrorCallback( view )
            ).toSource( versionRecordManager.getCurrentPath(), persistenceDescriptor );
        }
    }

    //Presenter methods
    @Override
    public void onPersistenceUnitNameChange() {
        ensurePersistenceUnit();
        getContent().getDescriptorModel().getPersistenceUnit().setName( view.getPersistenceUnitName() );
    }

    @Override
    public void onPersistenceProviderChange() {
        ensurePersistenceUnit();
        getContent().getDescriptorModel().getPersistenceUnit().setProvider( view.getPersistenceProvider() );
    }

    @Override
    public void onJTADataSourceChange() {
        ensurePersistenceUnit();
        getContent().getDescriptorModel().getPersistenceUnit().setJtaDataSource( view.getJTADataSource() );
    }

    @Override
    public void onSelectDataSource() {
        dataSourceSelector.setGlobalSelection();
        dataSourceSelector.show( new ParameterizedCommand<DataSourceInfo>() {
            @Override
            public void execute( DataSourceInfo dataSourceInfo ) {
                ensurePersistenceUnit();
                if ( dataSourceInfo.isDeployed() ) {
                    getContent().getDescriptorModel().getPersistenceUnit().setJtaDataSource(
                            dataSourceInfo.getJndi() );
                    view.setJTADataSource( dataSourceInfo.getJndi() );
                }
            }
        }, new Command() {
            @Override
            public void execute() {
                //do nothing
            }
        } );
    }

    private boolean dataSourceIntegrationEnabled() {
        return ApplicationPreferences.getBooleanPref( "persistence-descriptor-editor-options.integrate-datasources" );
    }

    @Override
    public void onJTATransactionsChange() {
        ensurePersistenceUnit();
        getContent().getDescriptorModel().getPersistenceUnit().setTransactionType(
                view.getJTATransactions() ? TransactionType.JTA : TransactionType.RESOURCE_LOCAL
        );
        updateResourceLocalTransactionsAlert();
    }

    @Override
    public void onResourceLocalTransactionsChange() {
        ensurePersistenceUnit();
        getContent().getDescriptorModel().getPersistenceUnit().setTransactionType(
            view.getResourceLocalTransactions() ? TransactionType.RESOURCE_LOCAL : TransactionType.JTA
        );
        updateResourceLocalTransactionsAlert();
    }

    private void updateResourceLocalTransactionsAlert( ) {
        if ( view.getResourceLocalTransactions() ) {
            view.setTransactionTypeHelpMessage( Constants.INSTANCE.persistence_descriptor_editor_resource_local_transactions_not_supported_message() );
        } else {
            view.setTransactionTypeHelpMessage( null );
        }
    }

    @Override
    public void onLoadClasses() {
        view.showBusyIndicator( Constants.INSTANCE.persistence_descriptor_editor_loading_classes_message() );
        dataModelerService.call( getLoadClassesSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback( view ) ).findPersistableClasses( versionRecordManager.getCurrentPath() );
    }

    @Override
    public void onLoadClass( final String className ) {
        view.showBusyIndicator( Constants.INSTANCE.persistence_descriptor_editor_loading_classes_message() );
        dataModelerService.call( getLoadClassSuccessCallback( className ),
                new HasBusyIndicatorDefaultErrorCallback( view ) ).isPersistableClass( className, versionRecordManager.getCurrentPath() );
    }

    @Override
    protected Command onValidate() {
        return new Command() {
            @Override
            public void execute() {
                onValidateDescriptor();
            }
        };
    }

    protected void onValidateDescriptor( ) {
        updateContent( );
        descriptorService.call( getValidationSuccessCallback(), new DefaultErrorCallback( ) ).validate( versionRecordManager.getCurrentPath( ), content.getDescriptorModel( ) );
    }

    private RemoteCallback<List<ValidationMessage>> getValidationSuccessCallback() {
        return new RemoteCallback<List<ValidationMessage>>() {
            @Override
            public void callback( List<ValidationMessage> messages ) {
                if ( messages == null || messages.isEmpty( ) ) {
                    notification.fire( new NotificationEvent(
                            CommonConstants.INSTANCE.ItemValidatedSuccessfully( ),
                            NotificationEvent.NotificationType.SUCCESS ) );
                } else {
                    validationPopup.showTranslatedMessages( messages );
                }
            }
        };
    }

    protected void updateContent() {
        content.getDescriptorModel().getPersistenceUnit().setProperties(
                unWrappPropertiesList( view.getPersistenceUnitProperties().getProperties() ) );
        content.getDescriptorModel().getPersistenceUnit().setClasses(
                unWrappClassesList( view.getPersistenceUnitClasses().getClasses() ) );
    }

    private RemoteCallback<List<String>> getLoadClassesSuccessCallback() {
        return new RemoteCallback<List<String>>() {
            @Override
            public void callback( List<String> classes ) {
                view.hideBusyIndicator();
                appendPersistableClasses( classes );
                view.getPersistenceUnitClasses().setNewClassHelpMessage( null );
                view.getPersistenceUnitClasses().setNewClassName( null );
            }
        };
    }

    private RemoteCallback<Boolean> getLoadClassSuccessCallback( final String className ) {
        return new RemoteCallback<Boolean>() {
            @Override
            public void callback( Boolean persistable ) {
                view.hideBusyIndicator();
                if ( persistable ) {
                    List<String> classes = new ArrayList<String>(  );
                    classes.add( className );
                    appendPersistableClasses( classes );
                    view.getPersistenceUnitClasses().setNewClassHelpMessage( null );
                    view.getPersistenceUnitClasses().setNewClassName( null );
                } else {
                    view.getPersistenceUnitClasses().setNewClassHelpMessage(
                            Constants.INSTANCE.persistence_descriptor_editor_peristable_class_not_found( className ) );
                }
            }
        };
    }

    private void appendPersistableClasses( List<String> classes ) {
        if ( classes != null && classes.size() > 0 ) {
            List<String> currentClasses = unWrappClassesList( view.getPersistenceUnitClasses().getClasses() );
            for ( String clazz : classes ) {
                if ( !currentClasses.contains( clazz ) ) {
                    currentClasses.add( clazz );
                }
            }
            view.getPersistenceUnitClasses().setClasses( wrappClassesList( currentClasses ) );
        }
    }

    private void ensurePersistenceUnit() {
        if ( content.getDescriptorModel().getPersistenceUnit() == null ) {
            content.getDescriptorModel().setPersistenceUnit( new PersistenceUnitModel() );
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

}