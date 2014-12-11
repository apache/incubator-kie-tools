package org.uberfire.client.workbench;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;
import org.uberfire.workbench.model.PerspectiveDefinition;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The default layout implementation.
 */
@ApplicationScoped
public class WorkbenchLayoutImpl implements WorkbenchLayout {

    /**
     * Holder for style information that was modified in order to maximize a panel.
     */
    public class OriginalStyleInfo {

        private String position;
        private String top;
        private String left;
        private String width;
        private String height;
        private String zIndex;

        /**
         * Restores to {@code w} all style values to those most recently set on this instance.
         *
         * @param w the widget to restore styles on.
         */
        public void restore( Widget w ) {
            Style style = w.getElement().getStyle();
            style.setProperty( "position", position );
            style.setProperty( "top", top );
            style.setProperty( "left", left );
            style.setProperty( "width", width );
            style.setProperty( "height", height );
            style.setProperty( "zIndex", zIndex );
        }

        public String getPosition() {
            return position;
        }

        public void setPosition( String position ) {
            this.position = position;
        }

        public String getTop() {
            return top;
        }

        public void setTop( String top ) {
            this.top = top;
        }

        public String getLeft() {
            return left;
        }

        public void setLeft( String left ) {
            this.left = left;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth( String width ) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight( String height ) {
            this.height = height;
        }

        public String getZIndex() {
            return zIndex;
        }

        public void setZIndex( String zIndex ) {
            this.zIndex = zIndex;
        }

    }

    private static final int MAXIMIZED_PANEL_Z_INDEX = 100000;

    /**
     * Top-level widget of the whole workbench layout. This panel contains the nested container panels for headers,
     * footers, and the current perspective. During a normal startup of UberFire, this panel would be added directly to
     * the RootLayoutPanel.
     */
    @Inject // using @Inject here because a real HeaderPanel can't be constructed in a GwtMockito test
    private HeaderPanel root;

    /**
     * The panel within which the current perspective's root view resides. This panel lasts the lifetime of the app; it's
     * cleared and repopulated with the new perspective's root view each time
     * {@link org.uberfire.client.workbench.PanelManager#setPerspective(PerspectiveDefinition)} gets called.
     */
    private final SimpleLayoutPanel perspectiveRootContainer = new SimpleLayoutPanel();

    /**
     * The panel within which the current perspective's header widgets reside. This panel lasts the lifetime of the app;
     * it's cleared and repopulated with the new perspective's root view each time
     * {@link #setHeaderContents(java.util.List)} gets called.
     */
    private final Panel headerPanel = new FlowPanel();

    /**
     * The panel within which the current perspective's footer widgets reside. This panel lasts the lifetime of the app;
     * it's cleared and repopulated with the new perspective's root view each time
     * {@link #setFooterContents(java.util.List)} gets called. The actual panel that's used for this is specified by the
     * concrete subclass's constructor.
     */
    private final Panel footerPanel = new FlowPanel();

    @Inject
    private WorkbenchDragAndDropManager dndManager;

    /**
     * We read the drag boundary panel out of this, and sandwich it between the root panel and the perspective container panel.
     */
    @Inject
    private WorkbenchPickupDragController dragController;

    @PostConstruct
    private void init() {
        perspectiveRootContainer.ensureDebugId( "perspectiveRootContainer" );
        headerPanel.ensureDebugId( "workbenchHeaderPanel" );
        footerPanel.ensureDebugId( "workbenchFooterPanel" );
        dragController.getBoundaryPanel().ensureDebugId( "workbenchDragBoundary" );
    }

    @Override
    public HeaderPanel getRoot() {
        return root;
    }

    @Override
    public HasWidgets getPerspectiveContainer() {
        return perspectiveRootContainer;
    }

    @Override
    public void setHeaderContents( List<Header> headers ) {
        headerPanel.clear();
        root.remove( headerPanel );
        if ( !headers.isEmpty() ) {
            for ( Header h : headers ) {
                headerPanel.add( h );
            }
            root.setHeaderWidget( headerPanel );
        }
    }

    @Override
    public void setFooterContents( List<Footer> footers ) {
        footerPanel.clear();
        root.remove( footerPanel );
        if ( !footers.isEmpty() ) {
            for ( Footer f : footers ) {
                footerPanel.add( f );
            }
            root.setFooterWidget( footerPanel );
        }
    }

    @Override
    public void onBootstrap() {
        dndManager.unregisterDropControllers();

        AbsolutePanel dragBoundary = dragController.getBoundaryPanel();
        dragBoundary.add( perspectiveRootContainer );
        Layouts.setToFillParent( perspectiveRootContainer );
        Layouts.setToFillParent( dragBoundary );
        root.setContentWidget( dragBoundary );
    }

    @Override
    public void onResize() {
        resizeTo( Window.getClientWidth(), Window.getClientHeight() );
    }

    @Override
    public void resizeTo(int width, int height) {
        root.setPixelSize( width, height );

        // The dragBoundary can't be a LayoutPanel, so it doesn't support ProvidesResize/RequiresResize.
        // We start the cascade of onResize() calls at its immediate child.
        perspectiveRootContainer.onResize();

        new Timer() {
            @Override
            public void run() {
                updateMaximizedPanelSizes();
            }
        }.schedule( 5 );
    }

    private void updateMaximizedPanelSizes() {
        for ( Widget w : maximizedWidgetOriginalStyles.keySet() ) {
            Style style = w.getElement().getStyle();
            style.setTop( perspectiveRootContainer.getAbsoluteTop(), Unit.PX );
            style.setLeft( perspectiveRootContainer.getAbsoluteLeft(), Unit.PX );
            style.setWidth( perspectiveRootContainer.getOffsetWidth(), Unit.PX );
            style.setHeight( perspectiveRootContainer.getOffsetHeight(), Unit.PX );
            
            if ( w instanceof RequiresResize ) {
                ((RequiresResize) w).onResize();
            }
        }
    }

    private final Map<Widget, OriginalStyleInfo> maximizedWidgetOriginalStyles = new HashMap<Widget, OriginalStyleInfo>();

    @Override
    public void maximize( Widget w ) {
        if ( maximizedWidgetOriginalStyles.get( w ) != null ) {
            return;
        }

        // this allows application-specified background colour, animation, borders, etc.
        w.addStyleName( "uf-maximized-panel" );

        Style style = w.getElement().getStyle();
        OriginalStyleInfo backup = new OriginalStyleInfo();

        backup.setPosition( style.getPosition() );
        style.setPosition( Position.FIXED );

        backup.setTop( style.getTop() );
        style.setTop( perspectiveRootContainer.getAbsoluteTop(), Unit.PX );

        backup.setLeft( style.getLeft() );
        style.setLeft( perspectiveRootContainer.getAbsoluteLeft(), Unit.PX );

        backup.setWidth( style.getWidth() );
        style.setWidth( perspectiveRootContainer.getOffsetWidth(), Unit.PX );

        backup.setHeight( style.getHeight() );
        style.setHeight( perspectiveRootContainer.getOffsetHeight(), Unit.PX );

        backup.setZIndex( style.getZIndex() );
        style.setZIndex( MAXIMIZED_PANEL_Z_INDEX );

        maximizedWidgetOriginalStyles.put( w, backup );

        if ( w instanceof RequiresResize ) {
            ((RequiresResize) w).onResize();
        }
    }

    @Override
    public void unmaximize( Widget w ) {

        w.removeStyleName( "uf-maximized-panel" );

        OriginalStyleInfo originalStyleInfo = maximizedWidgetOriginalStyles.remove( w );
        if ( originalStyleInfo != null ) {
            originalStyleInfo.restore( w );
        }

        if ( w instanceof RequiresResize ) {
            ((RequiresResize) w).onResize();
        }

    }

}
