package org.kie.uberfire.perspective.editor.client.panels.components;

import java.util.Collection;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
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
    private IsWidget externalComponentPreview;
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
        showEditScreen();
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
        showEditScreen();
    }

    public ScreenView( ColumnEditorUI parent,
                       ScreenEditor editor ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.parent = parent;
        this.screenEditor = new ScreenEditorWidgetUI( parent, fluidContainer );
        if ( editor.isAExternalComponent() ) {
            loadExternalComponent(editor);
        }
        loadScreenParameters(this.screenEditor, editor);
        build();
    }

    private void loadExternalComponent( ScreenEditor editor ) {
        this.type = DragType.EXTERNAL;
        this.externalComponent = lookupForExternalComponent( editor.getExternalComponentFQCN() );
        this.externalComponent.setup( editor.getPlaceName(), editor.toParametersMap() );
        this.externalComponentPreview = externalComponent.getPreview(editor.toParametersMap());
    }

    private void build() {
        screenEditor.getWidget().clear();
        screenEditor.getWidget().add(generateMainRow());
        if (externalComponentPreview != null) {
            screenEditor.getWidget().add( externalComponentPreview );
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

    private boolean typeIsExternalScreen() {
        return type == DragType.EXTERNAL;
    }

    private boolean typeIsRegularScreen() {
        return type == DragType.SCREEN;
    }

    private void showEditScreen() {
        if ( typeIsExternalScreen() ) {
            EditExternalScreen editUserForm = new EditExternalScreen( screenEditor, externalComponent, new EditExternalScreen.Listener() {
                public void onSave() {
                    externalComponentPreview = externalComponent.getPreview(externalComponent.getParametersMap());
                    build();
                }
                public void onClose() {
                }
            });
            editUserForm.show();
        } else {
            EditScreen editUserForm = new EditScreen( screenEditor );
            editUserForm.show();
        }
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

    private void removeThisWidgetFromParent() {
        parent.getWidget().remove( this );
        screenEditor.removeFromParent();
    }

    private void addDropColumnPanel() {
        ColumnEditorUI columnEditorUIParent = (ColumnEditorUI) parent;
        columnEditorUIParent.getWidget().add( new DropColumnPanel( columnEditorUIParent ) );
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
