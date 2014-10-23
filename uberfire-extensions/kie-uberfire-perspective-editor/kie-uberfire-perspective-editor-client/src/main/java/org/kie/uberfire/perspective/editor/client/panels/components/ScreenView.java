package org.kie.uberfire.perspective.editor.client.panels.components;

import java.util.Collection;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.uberfire.perspective.editor.client.api.ExternalPerspectiveEditorComponent;
import org.kie.uberfire.perspective.editor.client.panels.components.popup.EditExternalScreen;
import org.kie.uberfire.perspective.editor.client.panels.components.popup.EditScreen;
import org.kie.uberfire.perspective.editor.client.panels.dnd.DropColumnPanel;
import org.kie.uberfire.perspective.editor.client.structure.ColumnEditorUI;
import org.kie.uberfire.perspective.editor.client.structure.EditorWidget;
import org.kie.uberfire.perspective.editor.client.structure.PerspectiveEditorUI;
import org.kie.uberfire.perspective.editor.client.structure.ScreenEditorWidgetUI;
import org.kie.uberfire.perspective.editor.client.util.DragType;
import org.kie.uberfire.perspective.editor.model.ScreenEditor;

public class ScreenView extends Composite {

    private ExternalPerspectiveEditorComponent externalComponent;
    private DragType type = DragType.SCREEN;

    private ScreenEditorWidgetUI screenEditor;

    @UiField
    FluidContainer fluidContainer;

    private EditorWidget parent;

    interface ScreenEditorMainViewBinder
            extends
            UiBinder<Widget, ScreenView> {

    }

    private static ScreenEditorMainViewBinder uiBinder = GWT.create( ScreenEditorMainViewBinder.class );

    public ScreenView( ColumnEditorUI parent,
                       DragType type ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.type = type;
        this.parent = parent;
        this.screenEditor = new ScreenEditorWidgetUI( parent, fluidContainer );
        build();
    }

    public ScreenView( ColumnEditorUI parent,
                       DragType type,
                       String externalComponentFQCN ) {
        this.externalComponent = lookupForExternalComponent( externalComponentFQCN );
        initWidget( uiBinder.createAndBindUi( this ) );
        this.type = type;
        this.parent = parent;
        this.screenEditor = new ScreenEditorWidgetUI( parent, fluidContainer );
        build();
    }

    public ScreenView( ColumnEditorUI parent,
                       ScreenEditor editor ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.parent = parent;
        this.screenEditor = new ScreenEditorWidgetUI( parent, fluidContainer );
        if ( editor.isAExternalComponent() ) {
            loadExternalComponent(editor);
        }
        loadScreenParameters( this.screenEditor, editor );
        build();
    }

    private void loadExternalComponent( ScreenEditor editor ) {
        this.type = DragType.EXTERNAL;
        this.externalComponent = lookupForExternalComponent( editor.getExternalComponentFQCN() );
        this.externalComponent.setup( editor.getPlaceName(), editor.toParametersMap() );
    }

    private void build() {
        screenEditor.getWidget().add( generateMainRow() );
    }

    private FluidRow generateMainRow() {
        FluidRow row = new FluidRow();
        row.add( generateRowLabelColumn() );
        row.add( generateButtonColumn() );
        return row;
    }

    private Column generateRowLabelColumn() {
        Column column = new Column( 6 );
        Label row1;
        if ( typeIsExternalScreen() ) {
            row1 = generateLabel( this.externalComponent.getPlaceName() );
        } else {
            row1 = generateLabel( type.label() );
        }
        column.add( row1 );
        return column;
    }

    private Column generateButtonColumn() {
        Column buttonColumn = new Column( 6 );
        buttonColumn.getElement().getStyle().setProperty( "textAlign", "right" );
        buttonColumn.add( generateEditPropertyButton() );
        buttonColumn.add( generateRemoveButton() );
        return buttonColumn;
    }

    private Button generateEditPropertyButton() {
        Button remove = new Button( "Configure" );
        remove.setSize( ButtonSize.MINI );
        remove.setType( ButtonType.PRIMARY );
        remove.getElement().getStyle().setProperty( "marginRight", "3px" );
        remove.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if ( typeIsExternalScreen() ) {
                    EditExternalScreen editUserForm = new EditExternalScreen( screenEditor, externalComponent );
                    editUserForm.show();
                } else {
                    EditScreen editUserForm = new EditScreen( screenEditor );
                    editUserForm.show();
                }

            }
        } );
        return remove;
    }

    private boolean typeIsExternalScreen() {
        return type == DragType.EXTERNAL;
    }

    private boolean typeIsRegularScreen() {
        return type == DragType.SCREEN;
    }

    private Button generateRemoveButton() {
        Button remove = new Button( "Remove" );
        remove.setSize( ButtonSize.MINI );
        remove.setType( ButtonType.DANGER );
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

    private void removeThisWidgetFromParent() {
        parent.getWidget().remove( this );
        screenEditor.removeFromParent();
    }

    private void addDropColumnPanel() {
        ColumnEditorUI columnEditorUIParent = (ColumnEditorUI) parent;
        columnEditorUIParent.getWidget().add( new DropColumnPanel( columnEditorUIParent ) );
    }

    private Label generateLabel( String row ) {
        Label label = new Label( row );
        label.getElement().getStyle().setProperty( "marginLeft", "3px" );
        return label;
    }

    private void loadScreenParameters( ScreenEditorWidgetUI parent,
                                       ScreenEditor editor ) {
        PerspectiveEditorUI perspectiveEditor = getPerspectiveEditor();
        perspectiveEditor.loadProperties( parent.hashCode() + "", editor );

    }

    private PerspectiveEditorUI getPerspectiveEditor() {
        SyncBeanManager beanManager = IOC.getBeanManager();
        IOCBeanDef<PerspectiveEditorUI> perspectiveEditorIOCBeanDef = beanManager.lookupBean( PerspectiveEditorUI.class );
        return perspectiveEditorIOCBeanDef.getInstance();
    }

    private ExternalPerspectiveEditorComponent lookupForExternalComponent( String externalComponentFQCN ) {
        ExternalPerspectiveEditorComponent externalPerspectiveEditorComponent = null;
        SyncBeanManager beanManager = IOC.getBeanManager();
        final Collection<IOCBeanDef<ExternalPerspectiveEditorComponent>> externalComponents = beanManager.lookupBeans( ExternalPerspectiveEditorComponent.class );
        for ( IOCBeanDef iocBeanDef : externalComponents ) {
            if ( iocBeanDef.getInstance().getClass().getName().equalsIgnoreCase( externalComponentFQCN ) ) {
                externalPerspectiveEditorComponent = (ExternalPerspectiveEditorComponent) iocBeanDef.getInstance();
            }
        }
        return externalPerspectiveEditorComponent;
    }

}
