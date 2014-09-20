package org.kie.uberfire.plugin.client.widget.split;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.mvp.Command;

public class VerticalSplit extends Splitter {

    private double offset;
    private Widget rightArea;
    private Widget leftArea;
    private Widget contentArea;
    private Command onResize;

    public VerticalSplit() {
        super();
        addDomHandler( new MouseMoveHandler() {
            @Override
            public void onMouseMove( MouseMoveEvent event ) {
                if ( mouseDown ) {
                    double leftWidthPct = ( ( event.getClientX() - getContentAbsoluteLeft() - offset ) / getContentWidth() ) * 100;
                    double rightWidthPct = 100 - leftWidthPct;
                    setLeftAreaWidth( leftWidthPct );
                    setRightAreaWidth( rightWidthPct );
                    getElement().getStyle().setLeft( leftArea.getOffsetWidth() - 3, Style.Unit.PX );
                    onResize.execute();
                    event.preventDefault();
                }
            }
        }, MouseMoveEvent.getType() );
    }

    @Override
    protected void buildOffset( MouseDownEvent event ) {
        offset = event.getClientX() - getAbsoluteLeft();
    }

    public void init( final Widget leftArea,
                      final Widget rightArea,
                      final Widget contentArea,
                      final Command onResize ) {
        this.leftArea = leftArea;
        this.rightArea = rightArea;
        this.contentArea = contentArea;
        this.onResize = onResize;
    }

    private void setRightAreaWidth( double width ) {
        rightArea.setWidth( width + "%" );
    }

    private void setLeftAreaWidth( double width ) {
        leftArea.setWidth( width + "%" );
    }

    private int getContentWidth() {
        return contentArea.getOffsetWidth();
    }

    private int getContentAbsoluteLeft() {
        return contentArea.getAbsoluteLeft();
    }

}
