/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerErrorCallback;
import org.kie.workbench.common.screens.datamodeller.client.command.AddPropertyCommand;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommand;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommandBuilder;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchContextChangeEvent;
import org.kie.workbench.common.screens.datamodeller.client.handlers.DomainHandler;
import org.kie.workbench.common.screens.datamodeller.client.handlers.DomainHandlerRegistry;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.util.ObjectPropertyComparator;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.events.ChangeType;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldCreatedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldDeletedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldSelectedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectSelectedEvent;
import org.kie.workbench.common.screens.datamodeller.model.maindomain.MainDomainAnnotations;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.PathPlaceRequest;

@Dependent
public class DataObjectBrowser
        implements IsWidget,
        DataObjectBrowserView.Presenter {

    protected DataModelCommandBuilder commandBuilder;

    protected DataObject dataObject;

    protected DataModelerContext context;

    protected ListDataProvider<ObjectProperty> dataProvider = new ListDataProvider<ObjectProperty>( new ArrayList<ObjectProperty>( ) );

    protected ValidatorService validatorService;

    protected DomainHandlerRegistry handlerRegistry;

    protected Caller<DataModelerService> modelerService;

    protected Event<DataModelerEvent> dataModelerEvent;

    protected Event<DataModelerWorkbenchContextChangeEvent> dataModelerWBContextEvent;

    protected Event<LockRequiredEvent> lockRequiredEvent;

    protected PlaceManager placeManager;

    protected NewFieldPopup newFieldPopup;

    protected DataObjectBrowserView view;

    protected boolean readonly = true;

    @Inject
    public DataObjectBrowser( DomainHandlerRegistry handlerRegistry,
            DataModelCommandBuilder commandBuilder,
            Caller<DataModelerService> modelerService,
            ValidatorService validatorService,
            Event<DataModelerEvent> dataModelerEvent,
            Event<DataModelerWorkbenchContextChangeEvent> dataModelerWBContextEvent,
            Event<LockRequiredEvent> lockRequiredEvent,
            PlaceManager placeManager,
            NewFieldPopup newFieldPopup,
            DataObjectBrowserView view ) {

        this.handlerRegistry = handlerRegistry;
        this.commandBuilder = commandBuilder;
        this.modelerService = modelerService;
        this.validatorService = validatorService;
        this.dataModelerEvent = dataModelerEvent;
        this.dataModelerWBContextEvent = dataModelerWBContextEvent;
        this.lockRequiredEvent = lockRequiredEvent;
        this.placeManager = placeManager;
        this.newFieldPopup = newFieldPopup;
        this.view = view;

        view.init( this );
        view.setTableHeight( DataObjectBrowserHelper.calculateTableHeight( 0 ) );
        view.setDataProvider( dataProvider );
    }

    @PostConstruct
    protected void init() {

        setReadonly( true );
        newFieldPopup.addPopupHandler( new NewFieldPopupView.NewFieldPopupHandler() {
            @Override
            public void onCreate( String fieldName,
                    String fieldLabel,
                    String type,
                    boolean multiple ) {
                onCreateNewProperty( dataObject,
                        DataModelerUtils.unCapitalize( fieldName ),
                        fieldLabel,
                        type,
                        multiple,
                        true );
            }

            @Override
            public void onCreateAndContinue( String fieldName,
                    String fieldLabel,
                    String type,
                    boolean multiple ) {
                onCreateNewProperty( dataObject,
                        DataModelerUtils.unCapitalize( fieldName ),
                        fieldLabel,
                        type,
                        multiple,
                        false );
            }

            @Override
            public void onCancel() {
                newFieldPopup.hide();
            }
        } );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public DataModelerContext getContext() {
        return context;
    }

    public void setContext( DataModelerContext context ) {
        this.context = context;
        if ( context != null ) {
            setDataObject( context.getDataObject() );
            setReadonly( context.isReadonly() );
        } else {
            setReadonly( true );
        }
    }

    private void onCreateNewProperty( final DataObject dataObject,
            final String propertyName,
            final String propertyLabel,
            final String propertyType,
            final Boolean isMultiple,
            final boolean closePopup ) {
        if ( dataObject != null ) {
            validatorService.isValidIdentifier( propertyName, new ValidatorCallback() {
                @Override
                public void onFailure() {
                    newFieldPopup.setErrorMessage( Constants.INSTANCE.validation_error_invalid_object_attribute_identifier( propertyName ) );
                }

                @Override
                public void onSuccess() {
                    validatorService.isUniqueAttributeName( propertyName, dataObject, new ValidatorWithReasonCallback() {

                        @Override
                        public void onFailure() {
                            showFailure( ValidatorService.MANAGED_PROPERTY_EXISTS );
                        }

                        @Override
                        public void onFailure( String reason ) {
                            showFailure( reason );
                        }

                        private void showFailure( String reason ) {
                            if ( ValidatorService.UN_MANAGED_PROPERTY_EXISTS.equals( reason ) ) {
                                ObjectProperty unmanagedProperty = getDataObject().getUnManagedProperty( propertyName );
                                newFieldPopup.setErrorMessage( Constants.INSTANCE.validation_error_object_un_managed_attribute_already_exists( unmanagedProperty.getName(), unmanagedProperty.getClassName() ) );
                            } else {
                                newFieldPopup.setErrorMessage( Constants.INSTANCE.validation_error_object_attribute_already_exists( propertyName ) );
                            }
                        }

                        @Override
                        public void onSuccess() {
                            if ( propertyType != null && !"".equals( propertyType ) && !UIUtil.NOT_SELECTED.equals( propertyType ) ) {

                                boolean multiple = isMultiple && !getContext().getHelper().isPrimitiveType( propertyType ); //extra check
                                addNewProperty( getDataObject(), propertyName, propertyLabel, propertyType, multiple );
                                if ( closePopup ) {
                                    newFieldPopup.hide();
                                } else {
                                    newFieldPopup.resetInput();
                                }

                            } else {
                                newFieldPopup.setErrorMessage( Constants.INSTANCE.validation_error_missing_object_attribute_type() );
                            }
                        }
                    } );
                }
            } );
        }
    }

    private void setDataObject( DataObject dataObject ) {
        this.dataObject = dataObject;
        setObjectSelectorLabel( dataObject );

        List<ObjectProperty> dataObjectProperties = ( dataObject != null ) ?
                DataModelerUtils.getManagedProperties( dataObject ) : Collections.<ObjectProperty>emptyList();

        ArrayList<ObjectProperty> sortBuffer = new ArrayList<ObjectProperty>();
        if ( dataObject != null ) {
            sortBuffer.addAll( dataObjectProperties );
        }

        Collections.sort( sortBuffer, new ObjectPropertyComparator( "name", true ) );

        adjustTableSize( sortBuffer.size() );
        dataProvider.getList().clear();
        dataProvider.getList().addAll( sortBuffer );
        view.redrawTable();
    }

    private void adjustTableSize( int rows ) {
        int height = DataObjectBrowserHelper.calculateTableHeight( rows );
        int currentHeight = view.getTableHeight();
        if ( height != currentHeight ) {
            view.setTableHeight( height );
        }
    }

    private void addNewProperty( DataObject dataObject,
            final String propertyName,
            final String propertyLabel,
            final String propertyType,
            final Boolean isMultiple ) {

        AddPropertyCommand command = commandBuilder.buildAddPropertyCommand(
                getContext(),
                DataModelerEvent.DATA_OBJECT_BROWSER,
                dataObject, propertyName, propertyLabel,
                propertyType, isMultiple );

        command.execute();
        ObjectProperty property = command.getProperty();

        adjustTableSize( dataProvider.getList().size() + 1 );
        dataProvider.getList().add( property );
        view.setSelectedRow( property, true );

        executePostCommandProcessing( command );
    }

    public void onDeleteProperty( final ObjectProperty objectProperty,
            final int index ) {
        checkUsagesAndDeleteDataObjectProperty( objectProperty, index );
    }

    private void deleteProperty( final ObjectProperty objectProperty,
            final int index ) {
        if ( dataObject != null ) {
            adjustTableSize( dataProvider.getList().size() -1 );
            dataObject.getProperties().remove( objectProperty );

            dataProvider.getList().remove( index );
            dataProvider.flush();
            dataProvider.refresh();

            getContext().getHelper().dataObjectUnReferenced( objectProperty.getClassName(), dataObject.getClassName() );
            notifyFieldDeleted( objectProperty );
            if ( dataProvider.getList().size() == 0 ) {
                context.setObjectProperty( null );
                notifyObjectSelected();
            } else {
                int nextSelectedRow = index > 0 ? ( index - 1 ) : 0;
                view.setSelectedRow( dataProvider.getList().get( nextSelectedRow ), true );
            }
        }
    }

    private void checkUsagesAndDeleteDataObjectProperty( final ObjectProperty objectProperty,
            final int index ) {

        final String className = dataObject.getClassName();
        final String fieldName = objectProperty.getName();

        if ( getContext() != null ) {

            final Path currentPath = getContext().getEditorModelContent() != null ? getContext().getEditorModelContent().getPath() : null;

            modelerService.call( new RemoteCallback<List<Path>>() {

                @Override
                public void callback( List<Path> paths ) {

                    if ( paths != null && paths.size() > 0 ) {
                        //If usages for this field were detected in project assets
                        //show the confirmation message to the user.

                        view.showUsagesPopupForDeletion(
                                Constants.INSTANCE.modelEditor_confirm_deletion_of_used_field( objectProperty.getName() ),
                                paths,
                                new Command() {
                                    @Override
                                    public void execute() {
                                        deleteProperty( objectProperty, index );
                                    }
                                },
                                new Command() {
                                    @Override
                                    public void execute() {
                                        //do nothing.
                                    }
                                }
                        );

                    } else {
                        //no usages, just proceed with the deletion.
                        deleteProperty( objectProperty, index );
                    }
                }
            } ).findFieldUsages( currentPath, className, fieldName );
        }
    }

    private void setObjectSelectorLabel( DataObject dataObject ) {
        String label = dataObject != null ? DataModelerUtils.getDataObjectFullLabel( dataObject, false ) : "";
        String title = dataObject != null ? dataObject.getClassName() : "";
        view.setObjectSelectorLabel( label, title );
    }

    public DataModel getDataModel() {
        return getContext() != null ? getContext().getDataModel() : null;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    public void onSelectPropertyType( ObjectProperty property ) {
        DataObject dataObject = getDataModel().getDataObject( property.getClassName() );
        if ( dataObject != null ) {
            openDataObject( dataObject );
        }
    }

    private void setReadonly( boolean readonly ) {
        this.readonly = readonly;
        view.setReadonly( readonly );
    }

    public boolean isReadonly() {
        return readonly;
    }

    private void executePostCommandProcessing( DataModelCommand command ) {
        List<DomainHandler> handlers = handlerRegistry.getDomainHandlers();
        for ( DomainHandler handler : handlers ) {
            handler.postCommandProcessing( command );
        }
    }

    public void redrawFields() {
        view.redrawTable();
    }

    @Override
    public void onSelectCurrentDataObject() {
        ObjectProperty currentSelection = view.getSelectedRow();
        if ( currentSelection != null ) {
            //If we are about to go to the data object editor
            //and a row was selected we must un-select it.
            view.setSelectedRow( currentSelection, false );
        }
        context.setObjectProperty( null );
        notifyObjectSelected();
    }

    @Override
    public void onNewProperty() {
        lockRequiredEvent.fire( new LockRequiredEvent() );
        if ( getContext() != null ) {
            newFieldPopup.init( getContext() );
            newFieldPopup.show();
        }
    }

    @Override
    public void onSelectProperty( ObjectProperty selectedProperty ) {
        if ( selectedProperty != null ) {
            context.setObjectProperty( selectedProperty );
            notifyFieldSelected();
        }
    }

    @Override
    public void onSortByName( boolean ascending ) {
        sortTable( new ObjectPropertyComparator( "name", ascending ) );
    }

    @Override
    public void onSortByLabel( boolean ascending ) {
        sortTable( new ObjectPropertyComparator( "label", ascending ) );
    }

    @Override
    public void onSortByType( boolean ascending ) {
        sortTable( new ObjectPropertyComparator( "className", ascending ) );
    }

    private void sortTable( Comparator<ObjectProperty> comparator ) {
        Collections.sort( dataProvider.getList(), comparator );
    }

    @Override
    public String getPropertyTypeDisplayValue( ObjectProperty property ) {
        String displayName = property.getClassName();

        if ( property.isBaseType() ) {
            displayName = DataModelerUtils.extractClassName( displayName );
        } else {
            String label = getContext().getHelper().getObjectLabelByClassName( displayName );
            if ( label != null && !"".equals( label ) ) {
                displayName = label;
            }
        }

        if ( property.isMultiple() ) {
            displayName += " [" + Constants.INSTANCE.objectBrowser_typeLabelMultiple() + "]";
        }
        return displayName;
    }

    @Override
    public boolean isSelectablePropertyType( ObjectProperty property ) {

        return !property.isBaseType() &&
                !getDataObject().getClassName().equals( property.getClassName() ) &&
                !getDataModel().isExternal( property.getClassName() );
    }

    private void onDataObjectChange( @Observes DataObjectChangeEvent event ) {
        if ( event.isFromContext( context != null ? context.getContextId() : null ) ) {
            if ( event.getChangeType() == ChangeType.CLASS_NAME_CHANGE ||
                    event.getChangeType() == ChangeType.PACKAGE_NAME_CHANGE ||
                    event.getChangeType() == ChangeType.OBJECT_NAME_CHANGE ||
                    MainDomainAnnotations.LABEL_ANNOTATION.equals( event.getAnnotationClassName() )
                    ) {

                setObjectSelectorLabel( dataObject );

                // For self references: in case name or package changes redraw properties table
                dataProvider.refresh();
                dataProvider.flush();
            }
        }
    }

    private void onDataObjectPropertyChange( @Observes DataObjectFieldChangeEvent event ) {
        if ( event.isFromContext( context != null ? context.getContextId() : null ) ) {

            if ( event.getChangeType() == ChangeType.FIELD_NAME_CHANGE ||
                    event.getChangeType() == ChangeType.FIELD_TYPE_CHANGE ||
                    event.getChangeType() == ChangeType.FIELD_ANNOTATION_VALUE_CHANGE ||
                    event.getChangeType() == ChangeType.FIELD_ANNOTATION_ADD_CHANGE ||
                    event.getChangeType() == ChangeType.FIELD_ANNOTATION_REMOVE_CHANGE ) {

                List<ObjectProperty> props = dataProvider.getList();
                for ( int i = 0; i < props.size(); i++ ) {
                    if ( event.getCurrentField() == props.get( i ) ) {
                        view.redrawRow( i );
                        break;
                    }
                }
            }
        }
    }

    private void onDataObjectFieldCreated( @Observes DataObjectFieldCreatedEvent event ) {
        if ( event.isFromContext( context != null ? context.getContextId() : null ) &&
                !DataModelerEvent.DATA_OBJECT_BROWSER.equals( event.getSource() ) ) {
            setDataObject( dataObject );
        }
    }

    private void notifyFieldDeleted( ObjectProperty deletedProperty ) {
        dataModelerEvent.fire( new DataObjectFieldDeletedEvent( getContext().getContextId(), DataModelerEvent.DATA_OBJECT_BROWSER, getDataObject(), deletedProperty ) );
    }

    private void notifyObjectSelected() {
        dataModelerWBContextEvent.fire( new DataModelerWorkbenchContextChangeEvent() );
        dataModelerEvent.fire( new DataObjectSelectedEvent( getContext().getContextId(), DataModelerEvent.DATA_MODEL_BROWSER, getDataObject() ) );

    }

    private void notifyFieldSelected() {
        dataModelerWBContextEvent.fire( new DataModelerWorkbenchContextChangeEvent() );
        dataModelerEvent.fire( new DataObjectFieldSelectedEvent( getContext().getContextId(), DataModelerEvent.DATA_MODEL_BROWSER, getDataObject(), context.getObjectProperty() ) );
    }

    private void openDataObject( final DataObject dataObject ) {
        final Path objectPath = getContext().getDataObjectPath( dataObject.getClassName() );
        if ( objectPath != null ) {
            view.showBusyIndicator( org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.Loading() );
            modelerService.call( new RemoteCallback<Boolean>() {
                @Override
                public void callback( Boolean exists ) {
                    view.hideBusyIndicator();
                    if ( Boolean.TRUE.equals( exists ) ) {
                        placeManager.goTo( new PathPlaceRequest( objectPath ) );
                    } else {
                        view.showYesNoCancelPopup( CommonConstants.INSTANCE.Warning(),
                                Constants.INSTANCE.objectBrowser_message_file_not_exists_or_renamed( objectPath.toURI() ),
                                new Command() {
                                    @Override
                                    public void execute() {
                                        //do nothing.
                                    }
                                },
                                CommonConstants.INSTANCE.Close(),
                                ButtonType.WARNING,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null
                        );
                    }
                }
            }, new DataModelerErrorCallback( CommonConstants.INSTANCE.ExceptionNoSuchFile0( objectPath.toURI() ) ) ).exists( objectPath );
        }
    }

    public static class DataObjectBrowserHelper {

        private static final int ROW_HEIGHT = 30;

        /**
         * If there are more than 15 rows, let the table be scrolled.
         */
        public static final int MAX_ROWS = 15;

        public static final int MAX_TABLE_HEIGHT = ( MAX_ROWS + 1 ) * ROW_HEIGHT;

        public static final int MIN_TABLE_HEIGHT = 3 * ROW_HEIGHT;

        public static int calculateTableHeight( int rows ) {
            int height;
            if ( rows >= MAX_ROWS ) {
                height = MAX_TABLE_HEIGHT;
            } else if ( rows == 0 ) {
                height = MIN_TABLE_HEIGHT;
            } else {
                height = (rows + 1) * ROW_HEIGHT;
                height = height < MIN_TABLE_HEIGHT ? MIN_TABLE_HEIGHT : height;
            }
            return height;
        }

    }
}