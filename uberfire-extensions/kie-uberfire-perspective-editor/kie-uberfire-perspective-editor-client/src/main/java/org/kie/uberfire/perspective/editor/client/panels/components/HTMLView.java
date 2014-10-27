package org.kie.uberfire.perspective.editor.client.panels.components;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.perspective.editor.client.panels.components.popup.EditHTML;
import org.kie.uberfire.perspective.editor.client.panels.dnd.DropColumnPanel;
import org.kie.uberfire.perspective.editor.client.structure.ColumnEditorUI;
import org.kie.uberfire.perspective.editor.client.structure.EditorWidget;
import org.kie.uberfire.perspective.editor.client.structure.HTMLEditorWidgetUI;
import org.kie.uberfire.perspective.editor.client.util.DragType;

public class HTMLView extends Composite {

    private HTMLEditorWidgetUI htmlEditor;

    private static final String DEFAULT_HTML = "Add HTML Code to Display Content";

    @UiField
    FluidContainer fluidContainer;

    private EditorWidget parent;

    interface HTMLEditorMainViewBinder
            extends
            UiBinder<Widget, HTMLView> {

    }

    private static HTMLEditorMainViewBinder uiBinder = GWT.create( HTMLEditorMainViewBinder.class );

    public HTMLView( ColumnEditorUI parent, String htmlCode ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.parent = parent;
        this.htmlEditor = new HTMLEditorWidgetUI( parent, fluidContainer, htmlCode );
        build();
    }

    public HTMLView( ColumnEditorUI parent ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.parent = parent;
        this.htmlEditor = new HTMLEditorWidgetUI( parent, fluidContainer );
        build();
        showEditScreen();
    }

    private void build() {
        htmlEditor.getWidget().clear();
        htmlEditor.getWidget().add( generateMainRow( ) );
        if (htmlEditor.getHtmlCode() != null) {
            HTMLPanel htmlPanel = new HTMLPanel(htmlEditor.getHtmlCode());
            htmlEditor.getWidget().add( htmlPanel );
        }
    }

    private FluidRow generateMainRow() {
        FluidRow row = new FluidRow();
        row.add( generateButtonColumn() );
        return row;
    }

    private Column generateButtonColumn() {
        Column buttonColumn = new Column( 12 );
        buttonColumn.getElement().getStyle().setProperty( "textAlign", "right" );
        buttonColumn.add( generateEditPropertyButton() );
        buttonColumn.add( generateRemoveButton() );
        return buttonColumn;
    }

    private Button generateEditPropertyButton() {
        Button remove = new Button( "Configure" );
        remove.setSize( ButtonSize.MINI );
        remove.setType( ButtonType.PRIMARY );
        remove.setIcon( IconType.EDIT );
        remove.getElement().getStyle().setProperty( "marginRight", "3px" );
        remove.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                showEditScreen();
            }
        } );
        return remove;
    }

    private void showEditScreen() {
        EditHTML editUserForm = new EditHTML( htmlEditor, new EditHTML.Listener() {
            public void onSave() {
                build();
            }
            public void onClose() {
            }
        });
        editUserForm.show();
    }

    private Button generateRemoveButton() {
        Button remove = new Button( "Remove" );
        remove.setSize( ButtonSize.MINI );
        remove.setType( ButtonType.DANGER );
        remove.setIcon( IconType.REMOVE );
        remove.getElement().getStyle().setProperty( "marginRight", "3px" );
        remove.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                removeThisWidgetFromParent();
                addDropColumnPanel();
            }
        } );
        return remove;
    }

    private void addDropColumnPanel() {
        ColumnEditorUI columnEditorUIParent =(ColumnEditorUI) parent;
        columnEditorUIParent.getWidget().add( new DropColumnPanel( columnEditorUIParent ) );
    }

    private void removeThisWidgetFromParent() {
        parent.getWidget().remove( this );
        htmlEditor.removeFromParent();
    }
}