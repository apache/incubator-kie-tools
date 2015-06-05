package org.uberfire.client.workbench;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;
import org.uberfire.workbench.model.PerspectiveDefinition;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        private int absoluteTop;
        private int absoluteLeft;
        private int clientHeight;
        private int clientWidth;

        public OriginalStyleInfo( final Widget w ){
            absoluteLeft = w.getAbsoluteLeft();
            absoluteTop = w.getAbsoluteTop();
            clientHeight = w.getElement().getClientHeight();
            clientWidth = w.getElement().getClientWidth();

            final Style style = w.getElement().getStyle();
            position = style.getPosition();
            top = style.getTop();
            left = style.getLeft();
            width = style.getWidth();
            height = style.getHeight();
            zIndex = style.getZIndex();
        }

        /**
         * Restores to {@code w} all style values to those most recently set on this instance.
         *
         * @param w the widget to restore styles on.
         */
        public void restore( final Widget w ) {
            final Style style = w.getElement().getStyle();
            style.setProperty( "position", position );
            style.setProperty( "top", top );
            style.setProperty( "left", left );
            style.setProperty( "width", width );
            style.setProperty( "height", height );
            style.setProperty( "zIndex", zIndex );
        }

        public int getAbsoluteTop() {
            return absoluteTop;
        }

        public int getAbsoluteLeft() {
            return absoluteLeft;
        }

        public int getClientHeight() {
            return clientHeight;
        }

        public int getClientWidth() {
            return clientWidth;
        }

    }

    private static final int MAXIMIZED_PANEL_Z_INDEX = 100;

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
    public void maximize( final Widget w ) {
        if ( maximizedWidgetOriginalStyles.get( w ) != null ) {
            return;
        }

        // this allows application-specified background colour, animation, borders, etc.
        w.addStyleName( "uf-maximized-panel" );

        new ExpandAnimation(w).run();

        if ( w instanceof RequiresResize ) {
            ((RequiresResize) w).onResize();
        }
    }

    @Override
    public void unmaximize( Widget w ) {

        w.removeStyleName( "uf-maximized-panel" );

        new ColapseAnimation(w).run();

        if ( w instanceof RequiresResize ) {
            ((RequiresResize) w).onResize();
        }

    }

    private abstract class AbstractResizeAnimation extends Animation {

        protected final Style style;
        protected final Widget w;

        public AbstractResizeAnimation(final Widget w) {
            this.w = w;
            style = w.getElement().getStyle();
        }

        @Override
        protected void onUpdate(double progress) {
            final double width = newTarget(w.getElement().getClientWidth(), getTargetWidth(), progress);
            style.setWidth(width, Unit.PX);
            final double height = newTarget(w.getElement().getClientHeight(), getTargetHeight(), progress);
            style.setHeight(height, Unit.PX);
            final double top = newTarget(w.getAbsoluteTop(), getTargetTop(), progress);
            style.setTop(top, Unit.PX);
            final double left = newTarget(w.getAbsoluteLeft(), getTargetLeft(), progress);
            style.setLeft(left, Unit.PX);
        }

        public abstract int getTargetWidth();

        public abstract int getTargetHeight();

        public abstract int getTargetTop();

        public abstract int getTargetLeft();

        public void run() {
            super.run(1000);
        }

        private double newTarget(int current, int target, double progress) {
            return Math.round(current + ((target - current) * progress));
        }
    }

    private class ExpandAnimation extends AbstractResizeAnimation {

        public ExpandAnimation(final Widget w) {
            super(w);
        }

        @Override
        protected void onStart() {
            maximizedWidgetOriginalStyles.put( w, new OriginalStyleInfo( w ) );
            style.setZIndex(MAXIMIZED_PANEL_Z_INDEX);
            style.setHeight(w.getElement().getClientHeight(), Unit.PX);
            style.setWidth(w.getElement().getClientWidth(), Unit.PX);
            style.setTop(w.getAbsoluteTop(), Unit.PX);
            style.setLeft(w.getAbsoluteLeft(), Unit.PX);
            style.setPosition(Position.FIXED);
        }

        @Override
        public int getTargetWidth() {
            return perspectiveRootContainer.getOffsetWidth();
        }

        @Override
        public int getTargetHeight() {
            return perspectiveRootContainer.getOffsetHeight();
        }

        @Override
        public int getTargetTop() {
            return perspectiveRootContainer.getAbsoluteTop();
        }

        @Override
        public int getTargetLeft() {
            return perspectiveRootContainer.getAbsoluteLeft();
        }

     }

    private class ColapseAnimation extends AbstractResizeAnimation {

        private final OriginalStyleInfo originalStyleInfo;

        public ColapseAnimation(final Widget w) {
            super(w);
            originalStyleInfo = maximizedWidgetOriginalStyles.remove(w);
        }

        @Override
        public int getTargetWidth() {
            return originalStyleInfo.getClientWidth();
        }

        @Override
        public int getTargetHeight() {
            return originalStyleInfo.getClientHeight();
        }

        @Override
        public int getTargetTop() {
            return originalStyleInfo.getAbsoluteTop();
        }

        @Override
        public int getTargetLeft() {
            return originalStyleInfo.getAbsoluteLeft();
        }

        @Override
        protected void onComplete() {
            originalStyleInfo.restore( w );
        }

    }
}
