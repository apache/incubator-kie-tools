package org.uberfire.ext.layout.editor.client.components.columns;

import com.google.gwt.core.client.Scheduler;
import org.jboss.errai.common.client.dom.*;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.infra.ColumnDrop;
import org.uberfire.ext.layout.editor.client.infra.ContainerResizeEvent;
import org.uberfire.ext.layout.editor.client.infra.DragComponentEndEvent;
import org.uberfire.ext.layout.editor.client.infra.DragHelperComponentColumn;
import org.uberfire.ext.layout.editor.client.widgets.KebabWidget;
import org.uberfire.mvp.Command;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;
import static org.uberfire.ext.layout.editor.client.infra.CSSClassNameHelper.*;
import static org.uberfire.ext.layout.editor.client.infra.DomUtil.*;
import static org.uberfire.ext.layout.editor.client.infra.HTML5DnDHelper.extractDndData;

@Dependent
@Templated
public class ComponentColumnView
        implements UberElement<ComponentColumn>,
        ComponentColumn.View, IsElement {

    public static final String COL_CSS_CLASS = "col-md-";

    private ComponentColumn presenter;

    @Inject
    @DataField
    private Div col;

    @Inject
    @DataField
    private Div colUp;

    @Inject
    @DataField
    private Div row;

    @Inject
    @DataField
    private Div colDown;

    @Inject
    @DataField
    private Div left;

    @Inject
    @DataField( "resize-left" )
    private Button resizeLeft;

    @Inject
    @DataField
    private Div right;

    @Inject
    @DataField( "resize-right" )
    private Button resizeRight;

    @Inject
    @DataField
    private Div content;

    @Inject
    private KebabWidget kebabWidget;

    @Inject
    private Document document;


    String cssSize = "";

    private final int originalLeftRightWidth = 15;

    private ColumnDrop.Orientation contentDropOrientation;

    @Inject
    private DragHelperComponentColumn helper;

    @Override
    public void init( ComponentColumn presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupWidget() {
        setupEvents();
        setupKebabWidget();
        setupResize();
        setupOnResize();
    }

    private void setupOnResize() {
        document.getBody().setOnresize( event -> calculateSize() );
    }

    @Override
    public void setupResize() {
        resizeLeft.getStyle().setProperty( "display", "none" );
        resizeRight.getStyle().setProperty( "display", "none" );
    }

    public void dockSelectEvent( @Observes UberfireDocksInteractionEvent event ) {
        calculateSize();
    }

    private void setupKebabWidget() {
        kebabWidget.init( () -> presenter.remove(),
                          () -> presenter.edit() );
    }

    private void setupEvents() {
        setupLeftEvents();
        setupRightEvents();
        setupColUpEvents();
        setupColDownEvents();
        setupContentEvents();
        setupColEvents();
        setupRowEvents();
        setupResizeEvents();
    }

    private void setupRowEvents() {
        row.setOnmouseout( event -> {
            removeClassName( colUp, "componentDropInColumnPreview" );
            removeClassName( colDown, "componentDropInColumnPreview" );
        } );
    }

    private void setupResizeEvents() {
        resizeLeft.setOnclick( event -> presenter.resizeLeft() );
        resizeRight.setOnclick( event -> presenter.resizeRight() );
    }

    private void setupColEvents() {
        col.setOnmouseup( e -> {
            e.preventDefault();
            if ( hasClassName( col, "rowDndPreview" ) ) {
                removeClassName( col, "rowDndPreview" );
            }
        } );
        col.setOnmouseover( e -> {
            e.preventDefault();
        } );
        col.setOnmouseout( event -> {
            removeClassName( colUp, "componentDropInColumnPreview" );
            removeClassName( colDown, "componentDropInColumnPreview" );
        } );
    }

    private void setupColUpEvents() {

        colUp.setOndragleave( event -> {
            removeClassName( colUp, "componentDropInColumnPreview" );
        } );
        colUp.setOndragexit( event -> {
            removeClassName( colUp, "componentDropInColumnPreview" );
        } );

        colUp.setOndragover( event -> {
            event.preventDefault();
            if ( presenter.shouldPreviewDrop() ) {
                contentDropOrientation = ColumnDrop.Orientation.UP;
                addClassName( colUp, "componentDropInColumnPreview" );
            }
        } );
        colUp.setOndrop( e -> {
            if ( contentDropOrientation != null ) {
                presenter.onDrop( contentDropOrientation, extractDndData( e ) );
            }
            removeClassName( colUp, "componentDropInColumnPreview" );
            removeClassName( colDown, "componentDropInColumnPreview" );
        } );
        colUp.setOnmouseout( event -> {
            removeClassName( colUp, "componentDropInColumnPreview" );
        } );
    }


    private void setupColDownEvents() {
        colDown.setOndrop( e -> {
            if ( contentDropOrientation != null ) {
                presenter.onDrop( contentDropOrientation, extractDndData( e ) );
            }
            removeClassName( colUp, "componentDropInColumnPreview" );
            removeClassName( colDown, "componentDropInColumnPreview" );
        } );
    }

    private void setupRightEvents() {
        right.setOndragenter( e -> {
            e.preventDefault();
            if ( presenter.shouldPreviewDrop() && presenter.enableSideDnD() ) {
                addClassName( right, "columnDropPreview dropPreview" );
                addClassName( content, "centerPreview" );
                removeClassName( colUp, "componentDropInColumnPreview" );
            }
        } );
        right.setOndragleave( e -> {
            e.preventDefault();
            removeClassName( right, "columnDropPreview" );
            removeClassName( right, "dropPreview" );
            removeClassName( content, "centerPreview" );
        } );
        right.setOndragover( event -> event.preventDefault() );
        right.setOndrop( e -> {
            e.preventDefault();
            if ( presenter.enableSideDnD() && presenter.shouldPreviewDrop() ) {
                removeClassName( right, "columnDropPreview" );
                removeClassName( right, "dropPreview" );
                removeClassName( content, "centerPreview" );
                presenter.onDrop( ColumnDrop.Orientation.RIGHT, extractDndData( e ) );
            }
        } );
        right.setOnmouseover( e -> {
            e.preventDefault();
            if ( presenter.canResizeRight() ) {
                resizeRight.getStyle().setProperty( "display", "block" );
            }
        } );
        right.setOnmouseout( e -> {
            e.preventDefault();
            if ( presenter.canResizeRight() ) {
                resizeRight.getStyle().setProperty( "display", "none" );
            }
        } );
    }

    private void setupContentEvents() {
        content.setOndragover( e -> {
            e.preventDefault();
            if ( presenter.shouldPreviewDrop() ) {
                if ( dragOverUp( content, e ) ) {
                    addClassName( colUp, "componentDropInColumnPreview" );
                    removeClassName( colDown, "componentDropInColumnPreview" );
                    contentDropOrientation = ColumnDrop.Orientation.UP;

                } else {
                    addClassName( colDown, "componentDropInColumnPreview" );
                    removeClassName( colUp, "componentDropInColumnPreview" );
                    contentDropOrientation = ColumnDrop.Orientation.DOWN;
                }
            }
        } );
        content.setOndragleave( e -> {
            e.preventDefault();
            //ederign
            removeClassName( colDown, "componentDropInColumnPreview" );
            contentDropOrientation = null;
        } );
        content.setOndrop( e -> {
            if ( contentDropOrientation != null ) {
                presenter.onDrop( contentDropOrientation, extractDndData( e ) );
            }
            removeClassName( colUp, "componentDropInColumnPreview" );
            removeClassName( colDown, "componentDropInColumnPreview" );
        } );
        content.setOnmouseout( e -> {
            removeClassName( content, "componentMovePreview" );
        } );
        content.setOnmouseover( e -> {
            e.preventDefault();
            addClassName( content, "componentMovePreview" );
        } );

        content.setOndragend( e -> {
            e.stopPropagation();
            removeClassName( row, "rowDndPreview" );
            presenter.dragEndComponent();
        } );
        content.setOndragstart( e -> {
            e.stopPropagation();
            addClassName( row, "rowDndPreview" );
            presenter.dragStartComponent();
        } );
    }


    private void setupLeftEvents() {
        left.setOndragleave( e -> {
            e.preventDefault();
            removeClassName( left, "columnDropPreview" );
            removeClassName( left, "dropPreview" );
            removeClassName( content, "centerPreview" );
        } );
        left.setOndrop( e -> {
            e.preventDefault();
            if ( presenter.enableSideDnD() && presenter.shouldPreviewDrop() ) {
                removeClassName( left, "columnDropPreview" );
                removeClassName( left, "dropPreview" );
                removeClassName( content, "centerPreview" );
                presenter.onDrop( ColumnDrop.Orientation.LEFT, extractDndData( e ) );
            }
        } );

        left.setOndragover( event -> {
            if ( presenter.enableSideDnD() && presenter.shouldPreviewDrop() ) {
                event.preventDefault();
            }
        } );
        left.setOndragexit( event -> {
            event.preventDefault();
            removeClassName( left, "columnDropPreview" );
            removeClassName( left, "dropPreview" );
            removeClassName( content, "centerPreview" );
        } );
        left.setOndragenter( e -> {
            e.preventDefault();
            if ( presenter.enableSideDnD() && presenter.shouldPreviewDrop() ) {
                addClassName( left, "columnDropPreview dropPreview" );
                addClassName( content, "centerPreview" );
                removeClassName( colUp, "componentDropInColumnPreview" );
            }
        } );
        left.setOnmouseover( e -> {
            e.preventDefault();
            if ( presenter.canResizeLeft() ) {
                resizeLeft.getStyle().setProperty( "display", "block" );
            }
        } );
        left.setOnmouseout( e -> {
            e.preventDefault();
            if ( presenter.canResizeLeft() ) {
                resizeLeft.getStyle().setProperty( "display", "none" );
            }
        } );
    }

    public void resizeEventObserver( @Observes ContainerResizeEvent event ) {
        calculateSize();
    }

    @Override
    public void calculateSize() {
        Scheduler.get().scheduleDeferred( () -> {
            controlPadding();
            calculateLeftRightWidth();
            calculateContentWidth();
            addClassName( col, "container" );
        } );
    }

    private void controlPadding() {
        if ( !presenter.isInnerColumn() ) {
            addClassName( col, "no-padding" );
        } else {
            if ( hasClassName( col, "no-padding" ) ) {
                removeClassName( col, "no-padding" );
            }
        }
    }

    private void calculateLeftRightWidth() {
        if ( originalLeftRightWidth >= 0 ) {
            left.getStyle().setProperty( "width", originalLeftRightWidth + "px" );
            right.getStyle().setProperty( "width", originalLeftRightWidth + "px" );
        }
    }


    private void calculateContentWidth() {
        int smallSpace = 2;
        final int colWidth = Integer.parseInt( extractOffSetWidth( col ) );
        final int contentWidth = colWidth - ( originalLeftRightWidth * 2 ) - smallSpace;
        if ( contentWidth >= 0 ) {
            content.getStyle().setProperty( "width", contentWidth + "px" );
            colDown.getStyle().setProperty( "width", "100%" );
            colUp.getStyle().setProperty( "width", "100%" );
        }
    }

    @Override
    public void setSize( String size ) {
        if ( !col.getClassName().isEmpty() ) {
            removeClassName( col, cssSize );
        }
        cssSize = COL_CSS_CLASS + size;
        addClassName( col, cssSize );
    }


    @Override
    public void clearContent() {
        removeAllChildren( content );
    }

    @Override
    public void setContent() {
        Scheduler.get().scheduleDeferred( () -> {
            removeAllChildren( content );
            HTMLElement previewWidget = getPreviewWidget();
            previewWidget.getStyle().setProperty( "cursor", "default" );
            previewWidget.setClassName( "le-widget" );
            content.appendChild( kebabWidget.getElement() );
            content.appendChild( previewWidget );
        } );
    }


    @Override
    public void showConfigComponentModal( Command configurationFinish, Command configurationCanceled ) {
        helper.showConfigModal( configurationFinish, configurationCanceled );
    }

    @Override
    public boolean hasModalConfiguration() {
        return helper.hasModalConfiguration();
    }

    @Override
    public void setup( LayoutComponent layoutComponent ) {
        helper.setLayoutComponent( layoutComponent );
    }


    private HTMLElement getPreviewWidget() {
        HTMLElement cast = ( HTMLElement ) helper.getPreviewWidget( ElementWrapperWidget.getWidget( content ) )
                .asWidget().getElement().cast();
        return cast;
    }


    private boolean hasColPreview( HTMLElement element ) {
        return hasClassName( element, "componentDropInColumnPreview" );
    }


    private boolean dragOverUp( Div div, Event e ) {
        final int absoluteTop = extractAbsoluteTop( div );
        final int absoluteBottom = extractAbsoluteBottom( div );
        int dragOverY = Integer.parseInt( extractClientY( e ) );

        return ( dragOverY - absoluteTop ) < ( absoluteBottom - dragOverY );
    }

    public void cleanUp( @Observes DragComponentEndEvent dragComponentEndEvent ) {
        removeClassName( colUp, "componentDropInColumnPreview" );
    }
}