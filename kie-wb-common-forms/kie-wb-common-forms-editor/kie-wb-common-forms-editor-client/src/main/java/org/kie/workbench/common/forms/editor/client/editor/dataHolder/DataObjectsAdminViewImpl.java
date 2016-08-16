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
package org.kie.workbench.common.forms.editor.client.editor.dataHolder;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.ListDataProvider;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.editor.client.editor.FormEditorPresenter;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.model.DataHolder;
import org.uberfire.ext.widgets.common.client.common.StyleHelper;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKButton;

@Dependent
@Templated
public class DataObjectsAdminViewImpl extends Composite implements FormEditorPresenter.DataHolderAdminView {

    @DataField
    private Element dataObjectIDGroup = DOM.createDiv();

    @Inject
    @DataField
    private TextBox dataObjectID;

    @Inject
    @DataField
    private HelpBlock dataObjectIDHelp;

    @DataField
    private Element dataObjectTypeGroup = DOM.createDiv();

    @Inject
    @DataField
    private ListBox dataObjectType;

    @Inject
    @DataField
    private HelpBlock dataObjectTypeHelp;

    @Inject
    @DataField
    private Button newDataObject;

    @Inject
    @DataField
    private CellTable<DataHolder> dataObjectTable;

    private TranslationService translationService;

    private ListDataProvider<DataHolder> dataHolderListDataProvider = new ListDataProvider<DataHolder>();

    private FormEditorPresenter presenter;

    private BaseModal modal;

    @Inject
    public DataObjectsAdminViewImpl( TranslationService translationService ) {
        this.translationService = translationService;
        modal = new BaseModal();
    }

    @PostConstruct
    protected void initialize() {
        dataObjectTable.setBordered( true );
        dataObjectTable.setStriped( true );
        dataObjectTable.setHover( true );
    }

    public void init( final FormEditorPresenter presenter ) {

        this.presenter = presenter;

        modal.setTitle( translationService.getTranslation( FormEditorConstants.FormEditorViewImplDataObjects ) );

        modal.setBody( this );

        modal.add( new ModalFooterOKButton( new Command() {
            @Override
            public void execute() {
                modal.hide();
            }
        } ) );

        //Init data objects table
        dataObjectTable.setEmptyTableWidget( new Label( translationService.getTranslation( FormEditorConstants.DataObjectsAdminViewImplEmptyDataObjectsTable ) ) );

        dataHolderListDataProvider.addDataDisplay( dataObjectTable );

        final TextColumn<DataHolder> nameColumn = new TextColumn<DataHolder>() {

            @Override
            public void render( Cell.Context context,
                                DataHolder object,
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
            public String getValue( final DataHolder dataHolder ) {
                return dataHolder.getName();
            }
        };

        dataObjectTable.addColumn( nameColumn,
                translationService.getTranslation( FormEditorConstants.DataObjectsAdminViewImplDataObjectID ) );
        dataObjectTable.setColumnWidth(nameColumn, 30, Style.Unit.PCT);

        final TextColumn<DataHolder> typeColumn = new TextColumn<DataHolder>() {

            @Override
            public void render( Cell.Context context,
                                DataHolder object,
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
            public String getValue( final DataHolder dataHolder ) {
                return dataHolder.getType();
            }
        };

        dataObjectTable.addColumn( typeColumn,
                translationService.getTranslation( FormEditorConstants.DataObjectsAdminViewImplDataObjectType ) );

        final ButtonCell deleteCell = new ButtonCell( ButtonType.DANGER, IconType.TRASH );
        final Column<DataHolder, String> deleteDataObject = new Column<DataHolder, String>( deleteCell ) {
            @Override
            public String getValue( final DataHolder global ) {
                return translationService.getTranslation( FormEditorConstants.DataObjectsAdminViewImplRemove );
            }
        };

        deleteDataObject.setFieldUpdater(new FieldUpdater<DataHolder, String>() {
            public void update(final int index,
                               final DataHolder dataHolder,
                               final String value) {
                boolean doIt = false;
                if (presenter.hasBindedFields(dataHolder)) {
                    doIt = Window.confirm( translationService.getTranslation( FormEditorConstants.DataObjectsAdminViewImplDataObjectIsBindedMessage ) );
                } else {
                    doIt = Window.confirm( translationService.getTranslation( FormEditorConstants.DataObjectsAdminViewImplAreYouSureRemoveDataObject ) );
                }
                if (doIt) presenter.removeDataHolder(dataHolder.getName());
            }
        });

        dataObjectTable.addColumn(deleteDataObject);
    }

    @Override
    public void initView() {
        doInit(true);
        modal.show();
    }

    @Override
    public void refreshView() {
        doInit( false );
    }

    protected void doInit( boolean clearTypes ) {
        clearState( clearTypes );
        dataHolderListDataProvider.setList( presenter.getFormDefinition().getDataHolders() );
        dataHolderListDataProvider.refresh();
    }

    @EventHandler("newDataObject")
    protected void addDataObject( ClickEvent event ) {
        if ( validate() ) {
            presenter.addDataHolder( getDataObjectID(), getDataObjectType() );
            refreshView();
        }
    }

    public String getDataObjectID() {
        return dataObjectID.getText();
    }

    public String getDataObjectType() {
        return dataObjectType.getSelectedValue();
    }

    public boolean validate() {
        boolean validateId = validateDataObjectId();
        boolean validateType = validateDataObjectType();
        return validateId && validateType;
    }

    protected boolean validateDataObjectId() {
        boolean valid = true;

        String value = getDataObjectID();
        String errorMsg = "";
        if (value == null || value.isEmpty()) {
            errorMsg = translationService.getTranslation( FormEditorConstants.DataObjectsAdminViewImplIdCannotBeEmpty );
            valid = false;
        } else if ( presenter.getFormDefinition().getDataHolderByName(value) != null ) {
            errorMsg = translationService.getTranslation( FormEditorConstants.DataObjectsAdminViewImplIdAreadyExists );
            valid = false;
        }

        if ( !valid ) {
            StyleHelper.addUniqueEnumStyleName( dataObjectIDGroup, ValidationState.class, ValidationState.ERROR );
        } else {
            StyleHelper.addUniqueEnumStyleName( dataObjectIDGroup, ValidationState.class, ValidationState.NONE );
        }
        dataObjectIDHelp.setText( errorMsg );

        return valid;
    }

    protected boolean validateDataObjectType() {
        boolean valid = true;

        String value = getDataObjectType();
        String errorMsg = "";
        if (value == null || value.isEmpty()) {
            errorMsg = translationService.getTranslation( FormEditorConstants.DataObjectsAdminViewImplTypeCannotBeEmpty );
            valid = false;
        }

        if ( !valid ) {
            StyleHelper.addUniqueEnumStyleName( dataObjectTypeGroup, ValidationState.class, ValidationState.ERROR );
        } else {
            StyleHelper.addUniqueEnumStyleName( dataObjectTypeGroup, ValidationState.class, ValidationState.NONE );
        }
        dataObjectTypeHelp.setText(errorMsg);

        return valid;
    }

    @Override
    public void addDataType(String dataType) {
        dataObjectType.addItem(dataType);
    }

    protected void clearState( boolean clearTypes ) {
        StyleHelper.addUniqueEnumStyleName( dataObjectTypeGroup, ValidationState.class, ValidationState.NONE );
        StyleHelper.addUniqueEnumStyleName( dataObjectIDGroup, ValidationState.class, ValidationState.NONE );
        dataObjectID.setValue("");
        dataObjectIDHelp.setText("");
        if ( clearTypes ) {
            dataObjectType.clear();
            dataObjectType.addItem( "" );
        } else {
            dataObjectType.setSelectedIndex( 0 );
        }
        dataObjectTypeHelp.setText("");
    }
}
