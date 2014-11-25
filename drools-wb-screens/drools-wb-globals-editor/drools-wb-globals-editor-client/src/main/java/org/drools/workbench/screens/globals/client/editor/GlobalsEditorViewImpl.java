package org.drools.workbench.screens.globals.client.editor;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.drools.workbench.screens.globals.client.resources.i18n.GlobalsEditorConstants;
import org.drools.workbench.screens.globals.model.Global;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;

/**
 * The Globals Editor View implementation
 */
public class GlobalsEditorViewImpl
        extends KieEditorViewImpl
        implements GlobalsEditorView {

    interface GlobalsEditorViewBinder
            extends
            UiBinder<Widget, GlobalsEditorViewImpl> {

    }

    private static GlobalsEditorViewBinder uiBinder = GWT.create( GlobalsEditorViewBinder.class );

    @UiField
    Button addGlobalButton;

    @UiField(provided = true)
    CellTable<Global> table = new CellTable<Global>();

    @Inject
    private AddGlobalPopup addGlobalPopup;

    private List<Global> globals = new ArrayList<Global>();
    private ListDataProvider<Global> dataProvider = new ListDataProvider<Global>();
    private final Command addGlobalCommand = makeAddGlobalCommand();

    private List<String> fullyQualifiedClassNames;

    private boolean isReadOnly = false;

    public GlobalsEditorViewImpl() {
        setup();
        initWidget( uiBinder.createAndBindUi( this ) );

        //Disable until content is loaded
        addGlobalButton.setEnabled(false);
    }

    private void setup() {
        //Setup table
        table.setStriped( true );
        table.setCondensed( true );
        table.setBordered( true );
        table.setEmptyTableWidget( new Label( GlobalsEditorConstants.INSTANCE.noGlobalsDefined() ) );

        //Columns
        final TextColumn<Global> aliasColumn = new TextColumn<Global>() {

            @Override
            public String getValue( final Global global ) {
                return global.getAlias();
            }
        };

        final TextColumn<Global> classNameColumn = new TextColumn<Global>() {

            @Override
            public String getValue( final Global global ) {
                return global.getClassName();
            }
        };

        final ButtonCell deleteGlobalButton = new ButtonCell( ButtonSize.SMALL );
        deleteGlobalButton.setType( ButtonType.DANGER );
        deleteGlobalButton.setIcon( IconType.MINUS_SIGN );
        final Column<Global, String> deleteGlobalColumn = new Column<Global, String>( deleteGlobalButton ) {
            @Override
            public String getValue( final Global global ) {
                return GlobalsEditorConstants.INSTANCE.remove();
            }
        };
        deleteGlobalColumn.setFieldUpdater( new FieldUpdater<Global, String>() {
            public void update( final int index,
                                final Global global,
                                final String value ) {
                if ( isReadOnly ) {
                    return;
                }
                if ( Window.confirm( GlobalsEditorConstants.INSTANCE.promptForRemovalOfGlobal0( global.getAlias() ) ) ) {
                    dataProvider.getList().remove( index );
                }
            }
        } );

        table.addColumn( aliasColumn,
                         new TextHeader( GlobalsEditorConstants.INSTANCE.alias() ) );
        table.addColumn( classNameColumn,
                         new TextHeader( GlobalsEditorConstants.INSTANCE.className() ) );
        table.addColumn( deleteGlobalColumn,
                         GlobalsEditorConstants.INSTANCE.remove() );

        //Link data
        dataProvider.addDataDisplay( table );
        dataProvider.setList( globals );
    }

    @Override
    public void setContent( final List<Global> globals,
                            final List<String> fullyQualifiedClassNames,
                            final boolean isReadOnly ) {
        this.globals = globals;
        this.fullyQualifiedClassNames = fullyQualifiedClassNames;
        this.dataProvider.setList( globals );
        this.addGlobalButton.setEnabled( !isReadOnly );
        this.isReadOnly = isReadOnly;
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }

    @UiHandler("addGlobalButton")
    public void onClickAddGlobalButton( final ClickEvent event ) {
        addGlobalPopup.setContent( addGlobalCommand,
                                   fullyQualifiedClassNames );
        addGlobalPopup.show();
    }

    private Command makeAddGlobalCommand() {
        return new Command() {

            @Override
            public void execute() {
                final String alias = addGlobalPopup.getAlias();
                final String className = addGlobalPopup.getClassName();
                dataProvider.getList().add( new Global( alias,
                                                        className ) );
            }
        };
    }
}
