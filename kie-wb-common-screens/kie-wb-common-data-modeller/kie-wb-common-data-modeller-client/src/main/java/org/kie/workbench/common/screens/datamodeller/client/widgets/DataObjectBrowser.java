/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.TooltipCellDecorator;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerErrorCallback;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.resources.images.ImagesResources;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.util.ObjectPropertyComparator;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.client.widgets.refactoring.ShowUsagesPopup;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldCreatedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldDeletedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldSelectedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectSelectedEvent;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.PathPlaceRequest;

public class DataObjectBrowser extends Composite {

    interface DataObjectEditorUIBinder
            extends UiBinder<Widget, DataObjectBrowser> {

    };

    private static DataObjectEditorUIBinder uiBinder = GWT.create( DataObjectEditorUIBinder.class );

    @UiField
    VerticalPanel mainPanel;

    @UiField
    Button objectButton;

    @UiField(provided = true)
    CellTable<ObjectProperty> dataObjectPropertiesTable = new CellTable<ObjectProperty>( 1000, GWT.<CellTable.SelectableResources>create( CellTable.SelectableResources.class ) );

    @UiField
    Label newPropertyHeader;

    @UiField
    Label newPropertyIdLabel;

    @UiField
    com.github.gwtbootstrap.client.ui.TextBox newPropertyId;

    @UiField
    Label newPropertyLabelLabel;

    @UiField
    com.github.gwtbootstrap.client.ui.TextBox newPropertyLabel;

    @UiField
    Label newPropertyTypeLabel;

    @UiField
    com.github.gwtbootstrap.client.ui.ListBox newPropertyType;

    @UiField
    CheckBox isNewPropertyMultiple;

    @UiField
    Button newPropertyButton;

    private DataObject dataObject;

    private DataModelerContext context;

    private ListDataProvider<ObjectProperty> dataObjectPropertiesProvider = new ListDataProvider<ObjectProperty>();

    @Inject
    private ValidatorService validatorService;

    @Inject
    private Caller<DataModelerService> modelerService;

    @Inject
    Event<DataModelerEvent> dataModelerEvent;

    @Inject
    private PlaceManager placeManager;

    private boolean readonly = true;

    private boolean skipNextFieldNotification = false;

    private String editorId;

    public DataObjectBrowser() {
        initWidget( uiBinder.createAndBindUi( this ) );

        newPropertyId.getElement().getStyle().setWidth( 180, Style.Unit.PX );
        newPropertyLabel.getElement().getStyle().setWidth( 160, Style.Unit.PX );
        newPropertyType.getElement().getStyle().setWidth( 200, Style.Unit.PX );
        newPropertyType.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                selectedTypeChanged();
            }
        } );

        objectButton.setType( ButtonType.LINK );
        objectButton.addClickHandler( new ClickHandler() {
            @Override public void onClick( ClickEvent event ) {
                //TODO review this
                if ( getDataObject().getProperties().size() > 0 ) {
                    skipNextFieldNotification = true;
                }
                notifyObjectSelected();
            }
        } );

        dataObjectPropertiesProvider.setList( new ArrayList<ObjectProperty>() );

        //Init data objects table

        dataObjectPropertiesTable.setEmptyTableWidget( new com.github.gwtbootstrap.client.ui.Label( Constants.INSTANCE.objectBrowser_emptyTable() ) );

        //Init property name column

        final TextColumn<ObjectProperty> propertyNameColumn = new TextColumn<ObjectProperty>() {

            @Override
            public void render( Cell.Context context,
                                ObjectProperty object,
                                SafeHtmlBuilder sb ) {
                SafeHtml startDiv = new SafeHtml() {
                    @Override
                    public String asString() {
                        return "<div style=\"cursor: pointer;\">";
                    }
                };
                SafeHtml endDiv = new SafeHtml() {
                    @Override
                    public String asString() {
                        return "</div>";
                    }
                };

                sb.append( startDiv );
                super.render( context, object, sb );
                sb.append( endDiv );
            }

            @Override
            public String getValue( final ObjectProperty objectProperty ) {
                return objectProperty.getName();
            }
        };

        propertyNameColumn.setSortable( true );
        dataObjectPropertiesTable.addColumn( propertyNameColumn, Constants.INSTANCE.objectBrowser_columnName() );
        //dataObjectPropertiesTable.setColumnWidth(propertyNameColumn, 100, Style.Unit.PX);
        dataObjectPropertiesTable.setColumnWidth( propertyNameColumn, 30, Style.Unit.PCT);

        ColumnSortEvent.ListHandler<ObjectProperty> propertyNameColHandler = new ColumnSortEvent.ListHandler<ObjectProperty>( dataObjectPropertiesProvider.getList() );
        propertyNameColHandler.setComparator( propertyNameColumn, new ObjectPropertyComparator( "name" ) );
        dataObjectPropertiesTable.addColumnSortHandler( propertyNameColHandler );

        //Init property Label column

        final TextColumn<ObjectProperty> propertyLabelColumn = new TextColumn<ObjectProperty>() {

            @Override
            public void render( Cell.Context context,
                                ObjectProperty object,
                                SafeHtmlBuilder sb ) {
                SafeHtml startDiv = new SafeHtml() {
                    @Override
                    public String asString() {
                        return "<div style=\"cursor: pointer;\">";
                    }
                };
                SafeHtml endDiv = new SafeHtml() {
                    @Override
                    public String asString() {
                        return "</div>";
                    }
                };

                sb.append( startDiv );
                super.render( context, object, sb );
                sb.append( endDiv );
            }

            @Override
            public String getValue( final ObjectProperty objectProperty ) {
                return AnnotationValueHandler.getStringValue( objectProperty, AnnotationDefinitionTO.LABEL_ANNOTATION, AnnotationDefinitionTO.VALUE_PARAM );
            }
        };

        propertyLabelColumn.setSortable( true );
        dataObjectPropertiesTable.addColumn( propertyLabelColumn, Constants.INSTANCE.objectBrowser_columnLabel() );
        dataObjectPropertiesTable.setColumnWidth( propertyLabelColumn, 30, Style.Unit.PCT);

        ColumnSortEvent.ListHandler<ObjectProperty> propertyLabelColHandler = new ColumnSortEvent.ListHandler<ObjectProperty>( dataObjectPropertiesProvider.getList() );
        propertyNameColHandler.setComparator( propertyLabelColumn, new ObjectPropertyComparator( "label" ) );
        dataObjectPropertiesTable.addColumnSortHandler( propertyLabelColHandler );

        //Init property type browsing column
        ClickableImageResourceCell typeImageCell = new ClickableImageResourceCell( true, 25 );
        final TooltipCellDecorator<ImageResource> typeImageDecorator = new TooltipCellDecorator<ImageResource>( typeImageCell );
        typeImageDecorator.setText( Constants.INSTANCE.objectBrowser_action_goToDataObjectDefinition() );

        final Column<ObjectProperty, ImageResource> typeImageColumn = new Column<ObjectProperty, ImageResource>( typeImageDecorator ) {
            @Override
            public ImageResource getValue( final ObjectProperty property ) {

                if ( !property.isBaseType() &&
                        !getDataObject().getClassName().equals( property.getClassName() ) &&
                        !getDataModel().isExternal( property.getClassName() ) ) {
                    return ImagesResources.INSTANCE.BrowseObject();
                } else {
                    return null;
                }
            }
        };

        typeImageColumn.setFieldUpdater( new FieldUpdater<ObjectProperty, ImageResource>() {
            public void update( final int index,
                                final ObjectProperty property,
                                final ImageResource value ) {

                onTypeCellSelection( property );
            }
        } );

        dataObjectPropertiesTable.addColumn( typeImageColumn );
        //dataObjectPropertiesTable.setColumnWidth( typeImageColumn, 32, Style.Unit.PX );

        //Init property type column
        final TextColumn<ObjectProperty> propertyTypeColumn = new TextColumn<ObjectProperty>() {

            @Override
            public void render( Cell.Context context,
                                ObjectProperty object,
                                SafeHtmlBuilder sb ) {
                SafeHtml startDiv = new SafeHtml() {
                    @Override
                    public String asString() {
                        return "<div style=\"cursor: pointer;\">";
                    }
                };
                SafeHtml endDiv = new SafeHtml() {
                    @Override
                    public String asString() {
                        return "</div>";
                    }
                };

                sb.append( startDiv );
                super.render( context, object, sb );
                sb.append( endDiv );
            }

            @Override
            public String getValue( final ObjectProperty objectProperty ) {
                return propertyTypeDisplay( objectProperty );
            }
        };
        propertyTypeColumn.setSortable( true );
        dataObjectPropertiesTable.addColumn( propertyTypeColumn, Constants.INSTANCE.objectBrowser_columnType() );
        dataObjectPropertiesTable.setColumnWidth( propertyTypeColumn, 40, Style.Unit.PCT );

        //Init delete column
        ClickableImageResourceCell deleteImageCell = new ClickableImageResourceCell( true, 25 );
        final TooltipCellDecorator<ImageResource> decorator = new TooltipCellDecorator<ImageResource>( deleteImageCell );
        decorator.setPlacement( Placement.LEFT );
        decorator.setText( Constants.INSTANCE.objectBrowser_action_deleteProperty() );

        final Column<ObjectProperty, ImageResource> deletePropertyColumnImg = new Column<ObjectProperty, ImageResource>( decorator ) {
            @Override
            public ImageResource getValue( final ObjectProperty global ) {
                if ( !isReadonly() ) {
                    return ImagesResources.INSTANCE.Delete();
                } else {
                    return null;
                }
            }
        };

        deletePropertyColumnImg.setFieldUpdater( new FieldUpdater<ObjectProperty, ImageResource>() {
            public void update( final int index,
                                final ObjectProperty property,
                                final ImageResource value ) {

                if ( !isReadonly() ) {
                    checkAndDeleteDataObjectProperty( property, index );
                }
            }
        } );

        dataObjectPropertiesTable.addColumn( deletePropertyColumnImg );
        //dataObjectPropertiesTable.setColumnWidth( deletePropertyColumnImg, 32, Style.Unit.PX );

        ColumnSortEvent.ListHandler<ObjectProperty> propertyTypeColHandler = new ColumnSortEvent.ListHandler<ObjectProperty>( dataObjectPropertiesProvider.getList() );
        propertyTypeColHandler.setComparator( propertyTypeColumn, new ObjectPropertyComparator( "className" ) );
        dataObjectPropertiesTable.addColumnSortHandler( propertyTypeColHandler );

        //dataObjectPropertiesTable.getColumnSortList().push(propertyTypeColumn);
        dataObjectPropertiesTable.getColumnSortList().push( propertyNameColumn );

        //Init the selection model
        SingleSelectionModel<ObjectProperty> selectionModel = new SingleSelectionModel<ObjectProperty>();
        dataObjectPropertiesTable.setSelectionModel( selectionModel );
        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange( SelectionChangeEvent event ) {
                ObjectProperty selectedProperty = ( (SingleSelectionModel<ObjectProperty>) dataObjectPropertiesTable.getSelectionModel() ).getSelectedObject();
                notifyFieldSelected( selectedProperty );
            }
        } );

        dataObjectPropertiesTable.setKeyboardSelectionPolicy( HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.BOUND_TO_SELECTION );

        dataObjectPropertiesProvider.addDataDisplay( dataObjectPropertiesTable );
        dataObjectPropertiesProvider.refresh();

        newPropertyButton.setIcon( IconType.PLUS_SIGN );

        setReadonly( true );
    }

    public DataModelerContext getContext() {
        return context;
    }

    public void setContext( DataModelerContext context ) {
        this.context = context;
    }

    public void setEditorId( String editorId ) {
        this.editorId = editorId;
    }

    public String getEditorId() {
        return editorId;
    }

    private void initTypeList() {
        if ( getDataModel() != null ) {
            DataModelerUtils.initTypeList( newPropertyType, getContext().getHelper().getOrderedBaseTypes().values(), getDataModel().getDataObjects(), getDataModel().getExternalClasses(), true );
        } else {
            DataModelerUtils.initList( newPropertyType, true );
        }
    }

    public void refreshTypeList( boolean keepSelection ) {
        String selectedValue = newPropertyType.getValue();
        initTypeList();
        if ( keepSelection && selectedValue != null ) {
            newPropertyType.setSelectedValue( selectedValue );
        }
    }

    private void createNewProperty( final DataObject dataObject,
                                    final String propertyName,
                                    final String propertyLabel,
                                    final String propertyType,
                                    final Boolean isMultiple ) {
        if ( dataObject != null ) {
            validatorService.isValidIdentifier( propertyName, new ValidatorCallback() {
                @Override
                public void onFailure() {
                    ErrorPopup.showMessage( Constants.INSTANCE.validation_error_invalid_object_attribute_identifier( propertyName ) );
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
                                ErrorPopup.showMessage( Constants.INSTANCE.validation_error_object_un_managed_attribute_already_exists( unmanagedProperty.getName(), unmanagedProperty.getClassName() ) );
                            } else {
                                ErrorPopup.showMessage( Constants.INSTANCE.validation_error_object_attribute_already_exists( propertyName ) );
                            }
                        }

                        @Override
                        public void onSuccess() {
                            if ( propertyType != null && !"".equals( propertyType ) && !DataModelerUtils.NOT_SELECTED.equals( propertyType ) ) {

                                boolean multiple = isMultiple && !getContext().getHelper().isPrimitiveType( propertyType ); //extra check
                                ObjectProperty property = new ObjectPropertyImpl( propertyName,
                                                                                  propertyType,
                                                                                  multiple);

                                if ( propertyLabel != null && !"".equals( propertyLabel ) ) {
                                    Annotation annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.LABEL_ANNOTATION ) );
                                    annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, propertyLabel );
                                    property.addAnnotation( annotation );
                                }
                                addDataObjectProperty( property );
                                resetInput();
                            } else {
                                ErrorPopup.showMessage( Constants.INSTANCE.validation_error_missing_object_attribute_type() );
                            }
                        }
                    } );
                }
            } );
        }
    }

    private void setDataObject( DataObject dataObject ) {
        this.dataObject = dataObject;
        objectButton.setText( DataModelerUtils.getDataObjectFullLabel( getDataObject() ) );

        //We create a new selection model due to a bug found in GWT when we change e.g. from one data object with 9 rows
        // to one with 3 rows and the table was sorted.
        //Several tests has been done and the final workaround (not too bad) we found is to
        // 1) sort the table again
        // 2) create a new selection model
        // 3) populate the table with new items
        // 3) select the first row
        SingleSelectionModel selectionModel2 = new SingleSelectionModel<ObjectProperty>();
        dataObjectPropertiesTable.setSelectionModel( selectionModel2 );

        selectionModel2.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange( SelectionChangeEvent event ) {
                ObjectProperty selectedProperty = ( (SingleSelectionModel<ObjectProperty>) dataObjectPropertiesTable.getSelectionModel() ).getSelectedObject();
                notifyFieldSelected( selectedProperty );
            }
        } );

        List<ObjectProperty> dataObjectProperties = ( dataObject != null ) ? DataModelerUtils.getManagedProperties( dataObject ) : Collections.<ObjectProperty>emptyList();

        ArrayList<ObjectProperty> sortBuffer = new ArrayList<ObjectProperty>();
        if ( dataObject != null ) {
            sortBuffer.addAll( dataObjectProperties );
        }
        Collections.sort( sortBuffer, new ObjectPropertyComparator( "name" ) );

        dataObjectPropertiesProvider.getList().clear();
        dataObjectPropertiesProvider.getList().addAll( sortBuffer );
        dataObjectPropertiesProvider.flush();
        dataObjectPropertiesProvider.refresh();

        dataObjectPropertiesTable.getColumnSortList().push( new ColumnSortList.ColumnSortInfo( dataObjectPropertiesTable.getColumn( 2 ), true ) );

        if ( dataObjectProperties.size() > 0 ) {
            dataObjectPropertiesTable.setKeyboardSelectedRow( 0 );
            selectionModel2.setSelected( sortBuffer.get( 0 ), true );
        }

        //set the first row selected again. Sounds crazy, but's part of the workaround, don't remove this line.
        if ( dataObjectProperties.size() > 0 ) {
            dataObjectPropertiesTable.setKeyboardSelectedRow( 0 );
        }
    }

    private void addDataObjectProperty( ObjectProperty objectProperty ) {
        if ( dataObject != null ) {
            dataObject.getProperties().add( objectProperty );

            if ( !objectProperty.isBaseType() ) {
                getContext().getHelper().dataObjectReferenced( objectProperty.getClassName(), dataObject.getClassName() );
            }

            dataObjectPropertiesProvider.getList().add( objectProperty );
            dataObjectPropertiesProvider.flush();
            dataObjectPropertiesProvider.refresh();
            dataObjectPropertiesTable.setKeyboardSelectedRow( dataObjectPropertiesProvider.getList().size() - 1 );
            notifyFieldCreated( objectProperty );
        }
    }

    private void checkAndDeleteDataObjectProperty( final ObjectProperty objectProperty,
                                                   final int index ) {
        checkUsageAndDeleteDataObjectProperty( objectProperty, index );
    }

    private void deleteDataObjectProperty( final ObjectProperty objectProperty,
                                           final int index ) {
        if ( dataObject != null ) {
            dataObject.getProperties().remove( objectProperty );

            dataObjectPropertiesProvider.getList().remove( index );
            dataObjectPropertiesProvider.flush();
            dataObjectPropertiesProvider.refresh();

            getContext().getHelper().dataObjectUnReferenced( objectProperty.getClassName(), dataObject.getClassName() );
            notifyFieldDeleted( objectProperty );
        }
    }

    private void checkUsageAndDeleteDataObjectProperty( final ObjectProperty objectProperty,
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

                        ShowUsagesPopup showUsagesPopup = ShowUsagesPopup.newUsagesPopupForDeletion(
                                Constants.INSTANCE.modelEditor_confirm_deletion_of_used_field( objectProperty.getName() ),
                                paths,
                                new Command() {
                                    @Override
                                    public void execute() {
                                        deleteDataObjectProperty( objectProperty, index );
                                    }
                                },
                                new Command() {
                                    @Override
                                    public void execute() {
                                        //do nothing.
                                    }
                                }
                                                                                                   );

                        showUsagesPopup.setCloseVisible( false );
                        showUsagesPopup.show();

                    } else {
                        //no usages, just proceed with the deletion.
                        deleteDataObjectProperty( objectProperty, index );
                    }
                }
            } ).findFieldUsages( currentPath, className, fieldName );
        }
    }

    private String propertyTypeDisplay( ObjectProperty property ) {
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
            displayName += " ["+Constants.INSTANCE.objectBrowser_typeLabelMultiple()+"]";
        }
        return displayName;
    }

    private void selectedTypeChanged() {
        String selectedType = newPropertyType.getValue();
        if ( getContext() != null && getContext().getHelper() != null ) {
            if ( getContext().getHelper().isPrimitiveType( selectedType ) ) {
                isNewPropertyMultiple.setEnabled( false );
                isNewPropertyMultiple.setValue( false );
            } else {
                isNewPropertyMultiple.setEnabled( true );
            }
        }
    }

    private DataModel getDataModel() {
        return getContext() != null ? getContext().getDataModel() : null;
    }

    private Project getProject() {
        return getContext() != null ? getContext().getCurrentProject() : null;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    public void onTypeCellSelection( ObjectProperty property ) {

        DataObject dataObject = getDataModel().getDataObject( property.getClassName() );
        if ( dataObject != null ) {
            openDataObject( dataObject );
        }
    }

    private void enableNewPropertyAction( boolean enable ) {
        newPropertyId.setEnabled( enable );
        newPropertyLabel.setEnabled( enable );
        newPropertyType.setEnabled( enable );
        newPropertyButton.setEnabled( enable );
    }

    private void resetInput() {
        newPropertyId.setText( null );
        newPropertyLabel.setText( null );
        initTypeList();
        newPropertyType.setSelectedValue( DataModelerUtils.NOT_SELECTED );
        isNewPropertyMultiple.setValue( false );
        isNewPropertyMultiple.setEnabled( true );
    }

    private void setReadonly( boolean readonly ) {
        this.readonly = readonly;
        enableNewPropertyAction( !readonly );
    }

    public boolean isReadonly() {
        return readonly;
    }

    //Event handlers

    @UiHandler("newPropertyButton")
    void newPropertyClick( ClickEvent event ) {
        createNewProperty( dataObject,
                           DataModelerUtils.unCapitalize( newPropertyId.getText() ),
                           newPropertyLabel.getText(),
                           newPropertyType.getValue(),
                           isNewPropertyMultiple.getValue() );
    }

    //Event Observers

    private void onDataObjectSelected( @Observes DataObjectSelectedEvent event ) {
        if ( event.isFromContext( context != null ? context.getContextId() : null ) ) {
            DataObject dataObject = event.getCurrentDataObject();
            resetInput();
            setDataObject( dataObject );
            if ( dataObject == null ) {
                setReadonly( true );
            } else {
                setReadonly( getContext() == null || getContext().isReadonly() );
            }
        }
    }

    private void onDataObjectChange( @Observes DataObjectChangeEvent event ) {
        if ( event.isFromContext( context != null ? context.getContextId() : null ) ) {
            if ( "name".equals( event.getPropertyName() ) ||
                    "packageName".equals( event.getPropertyName() ) ||
                    "label".equals( event.getPropertyName() ) ) {

                // For self references: in case name or package changes redraw properties table
                if ( dataObject.getClassName().equals( event.getCurrentDataObject().getClassName() ) ) {
                    dataObjectPropertiesProvider.refresh();
                    dataObjectPropertiesTable.redraw();
                }

                initTypeList();
            }
        }
    }

    private void onDataObjectPropertyChange( @Observes DataObjectFieldChangeEvent event ) {
        if ( event.isFromContext( context != null ? context.getContextId() : null ) ) {
            if ( "name".equals( event.getPropertyName() ) ||
                    "className".equals( event.getPropertyName() ) ||
                    "label".equals( event.getPropertyName() ) ) {

                List<ObjectProperty> props = dataObjectPropertiesProvider.getList();
                for ( int i = 0; i < props.size(); i++ ) {
                    if ( event.getCurrentField() == props.get( i ) ) {
                        dataObjectPropertiesTable.redrawRow( i );
                        break;
                    }
                }
            }
        }
    }

    // Event notifications
    private void notifyFieldSelected( ObjectProperty selectedProperty ) {
        if ( !skipNextFieldNotification ) {
            dataModelerEvent.fire( new DataObjectFieldSelectedEvent( getContext().getContextId(), DataModelerEvent.DATA_OBJECT_BROWSER, getDataModel(), getDataObject(), selectedProperty ) );
        }
        skipNextFieldNotification = false;
    }

    private void notifyFieldDeleted( ObjectProperty deletedProperty ) {
        dataModelerEvent.fire( new DataObjectFieldDeletedEvent( getContext().getContextId(), DataModelerEvent.DATA_OBJECT_BROWSER, getDataModel(), getDataObject(), deletedProperty ) );
    }

    private void notifyFieldCreated( ObjectProperty createdProperty ) {
        dataModelerEvent.fire( new DataObjectFieldCreatedEvent( getContext().getContextId(), DataModelerEvent.DATA_OBJECT_BROWSER, getDataModel(), getDataObject(), createdProperty ) );
    }

    private void notifyObjectSelected() {
        dataModelerEvent.fire( new DataObjectSelectedEvent( getContext().getContextId(), DataModelerEvent.DATA_OBJECT_BROWSER, getDataModel(), getDataObject() ) );
    }

    private void openDataObject( final DataObject dataObject ) {
        final Path objectPath = getContext().getDataObjectPath( dataObject.getClassName() );
        if ( objectPath != null ) {
            BusyPopup.showMessage( org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.Loading() );
            modelerService.call( new RemoteCallback<Boolean>() {
                @Override
                public void callback( Boolean exists ) {
                    BusyPopup.close();
                    if ( Boolean.TRUE.equals( exists ) ) {
                        placeManager.goTo( new PathPlaceRequest( objectPath ) );
                    } else {
                        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup( CommonConstants.INSTANCE.Warning(),
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
                        yesNoCancelPopup.setCloseVisible( false );
                        yesNoCancelPopup.show();
                    }
                }
            }, new DataModelerErrorCallback( CommonConstants.INSTANCE.ExceptionNoSuchFile0( objectPath.toURI() ) ) ).exists( objectPath );
        }
    }
}