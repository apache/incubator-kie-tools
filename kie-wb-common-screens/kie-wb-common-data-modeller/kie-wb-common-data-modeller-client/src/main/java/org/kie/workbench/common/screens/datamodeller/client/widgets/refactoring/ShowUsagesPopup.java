/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.refactoring;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownHandler;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;
import org.uberfire.mvp.Command;

public class ShowUsagesPopup extends BaseModal {

    interface ShowUsagesPopupWidgetBinder
            extends
            UiBinder<Widget, ShowUsagesPopup> {

    }

    private ShowUsagesPopupWidgetBinder uiBinder = GWT.create( ShowUsagesPopupWidgetBinder.class );

    @UiField
    protected Paragraph message;

    @UiField
    protected SimpleTable<UsedByRow> usedByTable;

    @UiField
    protected Button yesButton;

    @UiField
    protected Button cancelButton;

    private List<Path> usedByFiles;

    private ListDataProvider<UsedByRow> usedByFilesProvider = new ListDataProvider<UsedByRow>();

    private
    Command yesCommand;

    private
    Command cancelCommand;

    protected ShowUsagesPopup( final String title,
                               final String message,
                               final List<Path> usedByFiles,
                               final Command yesCommand,
                               final String yesButtonText,
                               final ButtonType yesButtonType,
                               final IconType yesButtonIconType,
                               final Command cancelCommand,
                               final String cancelButtonText,
                               final ButtonType cancelButtonType,
                               final IconType cancelButtonIconType ) {

        setTitle( title );

        this.usedByFiles = usedByFiles;
        this.yesCommand = yesCommand;
        this.cancelCommand = cancelCommand;

        setBody( uiBinder.createAndBindUi( ShowUsagesPopup.this ) );

        if ( yesCommand == null ) {
            yesButton.setVisible( false );
        }
        if ( cancelCommand == null ) {
            cancelButton.setVisible( false );
        }

        if ( yesButtonType != null ) {
            yesButton.setType( yesButtonType );
        }
        if ( yesButtonText != null ) {
            yesButton.setText( yesButtonText );
        }
        if ( yesButtonIconType != null ) {
            yesButton.setIcon( yesButtonIconType );
        }

        if ( cancelButtonType != null ) {
            cancelButton.setType( cancelButtonType );
        }
        if ( cancelButtonText != null ) {
            cancelButton.setText( cancelButtonText );
        }
        if ( cancelButtonIconType != null ) {
            cancelButton.setIcon( cancelButtonIconType );
        }

        //setWidth(  );
        this.message.setHTML( message );
        initTable();
        addShownHandler( new ModalShownHandler() {
            @Override
            public void onShown( ModalShownEvent shownEvent ) {
                loadTable();
            }
        } );
    }

    protected ShowUsagesPopup( final String message,
                               final List<Path> usedByFiles,
                               final Command yesCommand,
                               final String yesButtonText,
                               final Command cancelCommand,
                               final String cancelButtonText ) {

        this( Constants.INSTANCE.usages_popup_title(),
              message,
              usedByFiles,
              yesCommand,
              yesButtonText,
              null,
              null,
              cancelCommand,
              cancelButtonText,
              null,
              null );
    }

    public static ShowUsagesPopup newUsagesPopup( final String message,
                                                  final List<Path> usedByFiles,
                                                  final Command yesCommand,
                                                  final String yesButtonText,
                                                  final Command cancelCommand,
                                                  final String cancelButtonText ) {

        return new ShowUsagesPopup( message,
                                    usedByFiles,
                                    yesCommand,
                                    yesButtonText,
                                    cancelCommand,
                                    cancelButtonText );
    }

    public static ShowUsagesPopup newUsagesPopup( final String message,
                                                  final List<Path> usedByFiles,
                                                  final Command yesCommand,
                                                  final String yesButtonText,
                                                  final ButtonType yesButtonType,
                                                  final IconType yesButtonIconType,
                                                  final Command cancelCommand,
                                                  final String cancelButtonText,
                                                  final ButtonType cancelButtonType,
                                                  final IconType cancelButtonIconType ) {

        return new ShowUsagesPopup( Constants.INSTANCE.usages_popup_title(),
                                    message,
                                    usedByFiles,
                                    yesCommand,
                                    yesButtonText,
                                    yesButtonType,
                                    yesButtonIconType,
                                    cancelCommand,
                                    cancelButtonText,
                                    cancelButtonType,
                                    cancelButtonIconType );
    }

    public static ShowUsagesPopup newUsagesPopupForDeletion( final String message,
                                                             final List<Path> usedByFiles,
                                                             final Command yesCommand,
                                                             final Command cancelCommand ) {

        return newUsagesPopup( message,
                               usedByFiles,
                               yesCommand,
                               Constants.INSTANCE.usages_popup_action_yes_delete_anyway(),
                               ButtonType.DANGER,
                               IconType.TRASH,
                               cancelCommand,
                               null,
                               ButtonType.PRIMARY,
                               null );
    }

    public static ShowUsagesPopup newUsagesPopupForRenaming( final String message,
                                                             final List<Path> usedByFiles,
                                                             final Command yesCommand,
                                                             final Command cancelCommand ) {

        return newUsagesPopup( message,
                               usedByFiles,
                               yesCommand,
                               Constants.INSTANCE.usages_popup_action_yes_rename_anyway(),
                               ButtonType.DANGER,
                               IconType.TRASH,
                               cancelCommand,
                               null,
                               ButtonType.PRIMARY,
                               null );
    }

    public static ShowUsagesPopup newUsagesPopupForChanging( final String message,
                                                             final List<Path> usedByFiles,
                                                             final Command yesCommand,
                                                             final Command cancelCommand ) {

        return newUsagesPopup( message,
                               usedByFiles,
                               yesCommand,
                               Constants.INSTANCE.usages_popup_action_yes_change_anyway(),
                               ButtonType.DANGER,
                               IconType.TRASH,
                               cancelCommand,
                               null,
                               ButtonType.PRIMARY,
                               null );
    }

    private void initTable() {
        usedByTable.columnPickerButton.setVisible( true );

        usedByFilesProvider.addDataDisplay( usedByTable );
        Column<UsedByRow, String> nameColumn = new TextColumn<UsedByRow>() {
            @Override
            public String getValue( UsedByRow row ) {
                return row.getPath() != null ? row.getPath().getFileName() : null;
            }

            @Override
            public void render( Cell.Context context,
                                UsedByRow object,
                                SafeHtmlBuilder sb ) {
                final String currentValue = getValue( object );
                if ( currentValue != null ) {
                    sb.append( SafeHtmlUtils.fromTrustedString( "<div title=\"" ) );
                    sb.append( SafeHtmlUtils.fromString( currentValue ) );
                    sb.append( SafeHtmlUtils.fromTrustedString( "\">" ) );
                }
                super.render( context, object, sb );
                if ( currentValue != null ) {
                    sb.append( SafeHtmlUtils.fromTrustedString( "</div>" ) );
                }
            }
        };
        usedByTable.addColumn( nameColumn, Constants.INSTANCE.usages_popup_file_name_column() );
        usedByTable.setColumnWidth( nameColumn, 40, Style.Unit.PCT );

        Column<UsedByRow, String> pathColumn = new TextColumn<UsedByRow>() {
            @Override
            public String getValue( UsedByRow row ) {
                String pathStr = null;
                if ( row.getPath() != null && row.getPath().getFileName() != null ) {
                    pathStr = row.getPath().toURI().substring( 0, row.getPath().toURI().lastIndexOf( '/' ) );
                }
                return pathStr;
            }

            @Override
            public void render( Cell.Context context,
                                UsedByRow object,
                                SafeHtmlBuilder sb ) {
                final String currentValue = getValue( object );
                if ( currentValue != null ) {
                    sb.append( SafeHtmlUtils.fromTrustedString( "<div title=\"" ) );
                    sb.append( SafeHtmlUtils.fromString( currentValue ) );
                    sb.append( SafeHtmlUtils.fromTrustedString( "\">" ) );
                }
                super.render( context, object, sb );
                if ( currentValue != null ) {
                    sb.append( SafeHtmlUtils.fromTrustedString( "</div>" ) );
                }
            }
        };
        usedByTable.addColumn( pathColumn, Constants.INSTANCE.usages_popup_file_path_column() );
        usedByTable.setColumnWidth( pathColumn, 60, Style.Unit.PCT );
    }

    private void loadTable() {
        if ( usedByFiles != null && !usedByFiles.isEmpty() ) {
            usedByFilesProvider.getList().addAll( createUsedByRows( usedByFiles ) );
        }
    }

    @UiHandler("yesButton")
    public void onYesButtonClick( final ClickEvent e ) {
        if ( yesCommand != null ) {
            yesCommand.execute();
        }
        hide();
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClick( final ClickEvent e ) {
        if ( cancelCommand != null ) {
            cancelCommand.execute();
        }
        hide();
    }

    public static class UsedByRow {

        Path path;

        public UsedByRow( Path path ) {
            this.path = path;
        }

        public Path getPath() {
            return path;
        }

        public void setPath( Path path ) {
            this.path = path;
        }
    }

    private List<UsedByRow> createUsedByRows( List<Path> usedByFiles ) {
        List<UsedByRow> result = new ArrayList<UsedByRow>();
        if ( usedByFiles != null ) {
            for ( Path path : usedByFiles ) {
                result.add( new UsedByRow( path ) );
            }
        }
        return result;
    }

}