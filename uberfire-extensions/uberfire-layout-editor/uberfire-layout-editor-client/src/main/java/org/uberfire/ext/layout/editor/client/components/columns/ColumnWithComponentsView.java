package org.uberfire.ext.layout.editor.client.components.columns;

import com.google.gwt.core.client.Scheduler;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.infra.ColumnDrop;
import org.uberfire.ext.layout.editor.client.infra.ContainerResizeEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;
import static org.uberfire.ext.layout.editor.client.infra.HTML5DnDHelper.extractDndData;
import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.hasCSSClass;

@Dependent
@Templated
public class ColumnWithComponentsView
        implements UberElement<ColumnWithComponents>,
        ColumnWithComponents.View, IsElement {

    private static final String COL_CSS_CLASS = "col-md-";

    private ColumnWithComponents presenter;

    private final int originalLeftRightWidth = 15;

    @Inject
    @DataField
    Div colWithComponents;

    @Inject
    @DataField
    Div row;

    @Inject
    @DataField
    private Div content;

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
    private Document document;

    String cssSize = "";

    @Override
    public void init( ColumnWithComponents presenter ) {
        this.presenter = presenter;
        setupEvents();
    }

    private void setupEvents() {
        setupLeftEvents();
        setupRightEvents();
        setupOnResize();
        setupResize();
        setupResizeEvents();
    }

    private void setupResizeEvents() {
        resizeLeft.setOnclick( event -> presenter.resizeLeft() );
        resizeRight.setOnclick( event -> presenter.resizeRight() );
    }

    @Override
    public void setupResize() {
        resizeLeft.getStyle().setProperty( "display", "none" );
        resizeRight.getStyle().setProperty( "display", "none" );
    }

    private void setupOnResize() {
        document.getBody().setOnresize( event -> calculateSize() );
    }

    public void dockSelectEvent( @Observes UberfireDocksInteractionEvent event ) {
        calculateSize();
    }

    private void setupRightEvents() {
        right.setOndragenter( e -> {
            e.preventDefault();
            if ( presenter.shouldPreviewDrop() ) {
                addCSSClass( right, "columnDropPreview" );
                addCSSClass( right, "dropPreview" );
                addCSSClass( content, "centerPreview" );
            }
        } );
        right.setOndragleave( e -> {
            e.preventDefault();
            if ( presenter.shouldPreviewDrop() ) {
                removeCSSClass( right, "columnDropPreview" );
                removeCSSClass( right, "dropPreview" );
                removeCSSClass( content, "centerPreview" );
            }
        } );
        right.setOndrop( e -> {
            e.preventDefault();
            if ( presenter.shouldPreviewDrop() ) {
                removeCSSClass( right, "columnDropPreview" );
                removeCSSClass( right, "dropPreview" );
                removeCSSClass( content, "centerPreview" );
                presenter.onDrop( ColumnDrop.Orientation.RIGHT, extractDndData( e ) );
            }
        } );
        right.setOndragover( e -> {
            e.preventDefault();
        } );
        right.setOnmouseover( e -> {
            e.preventDefault();
            if ( presenter.canResizeRight() ) {
                resizeRight.getStyle().setProperty( "display", "block" );
            }
        } );
        right.setOnmouseout( e -> {
            if ( presenter.canResizeRight() ) {
                resizeRight.getStyle().setProperty( "display", "none" );
            }
        } );
    }

    private void setupLeftEvents() {
        left.setOndragenter( e -> {
            e.preventDefault();
            if ( presenter.shouldPreviewDrop() ) {
                addCSSClass( left, "columnDropPreview" );
                addCSSClass( left, "dropPreview" );
                addCSSClass( content, "centerPreview" );
            }
        } );
        left.setOndragover( e -> e.preventDefault() );
        left.setOndragleave( e -> {
            e.preventDefault();
            if ( presenter.shouldPreviewDrop() ) {
                removeCSSClass( left, "columnDropPreview" );
                removeCSSClass( left, "dropPreview" );
                removeCSSClass( content, "centerPreview" );
            }
        } );
        left.setOndrop( e -> {
            e.preventDefault();
            if ( presenter.shouldPreviewDrop() ) {
                removeCSSClass( left, "columnDropPreview" );
                removeCSSClass( left, "dropPreview" );
                removeCSSClass( content, "centerPreview" );
                presenter.onDrop( ColumnDrop.Orientation.LEFT, extractDndData( e ) );
            }
        } );
        left.setOnmouseover( e -> {
            e.preventDefault();
            if ( presenter.canResizeLeft() ) {
                resizeLeft.getStyle().setProperty( "display", "block" );
            }
        } );
        left.setOnmouseout( e -> {
            if ( presenter.canResizeLeft() ) {
                resizeLeft.getStyle().setProperty( "display", "none" );
            }
        } );
    }

    @Override
    public void setSize( String size ) {
        if ( hasCssSizeClass() ) {
            removeCSSClass( colWithComponents, cssSize );
        }
        cssSize = COL_CSS_CLASS + size;
        addCSSClass( colWithComponents, cssSize );
        addCSSClass( colWithComponents, "container" );
    }

    private boolean hasCssSizeClass() {
        return !cssSize.isEmpty() && hasCSSClass( colWithComponents, cssSize );
    }

    @Override
    public void addRow( UberElement<Row> view ) {
        content.appendChild( view.getElement() );
    }

    @Override
    public void clear() {
        removeAllChildren( content );
    }

    public void resizeEventObserver( @Observes ContainerResizeEvent event ) {
        calculateSize();
    }

    @Override
    public void calculateSize() {

        Scheduler.get().scheduleDeferred( () -> {

            final int colWidth = row.getBoundingClientRect().getWidth().intValue();

            int padding = 2;
            final int contentWidth = colWidth - ( originalLeftRightWidth * 2 ) - padding;

            left.getStyle().setProperty( "width", originalLeftRightWidth + "px" );
            right.getStyle().setProperty( "width", originalLeftRightWidth + "px" );

            content.getStyle().setProperty( "width", contentWidth + "px" );
            presenter.calculateSizeChilds();
        } );
    }

}