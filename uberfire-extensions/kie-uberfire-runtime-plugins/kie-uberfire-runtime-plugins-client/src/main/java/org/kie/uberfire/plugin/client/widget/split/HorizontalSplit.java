package org.kie.uberfire.plugin.client.widget.split;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.mvp.Command;

public class HorizontalSplit extends Splitter {

    private double offset;
    private Widget topArea;
    private Widget bottomArea;
    private Widget contentArea;
    private Command onResize;

    public HorizontalSplit() {
        super();
        addDomHandler( new MouseMoveHandler() {
            @Override
            public void onMouseMove( MouseMoveEvent event ) {
                if ( mouseDown ) {
                    double topHeightPct = ( ( event.getClientY() - getContentAbsoluteTop() - offset ) / getContentHeight() ) * 100;
                    double bottomHeightPct = 100 - topHeightPct;
                    setTopAreaHeight( topHeightPct );
                    setBottomHeight( bottomHeightPct );
                    getElement().getStyle().setTop( topArea.getOffsetHeight() - 6, Style.Unit.PX );
                    onResize.execute();
                    event.preventDefault();
                }
            }
        }, MouseMoveEvent.getType() );
    }

    @Override
    protected void buildOffset( MouseDownEvent event ) {
        offset = event.getClientY() - getAbsoluteTop();
    }

    public void init( final Widget topArea,
                      final Widget bottomArea,
                      final Widget contentArea,
                      final Command onResize) {
        this.topArea = topArea;
        this.bottomArea = bottomArea;
        this.contentArea = contentArea;
        this.onResize = onResize;
    }

    private void setBottomHeight( double height ) {
        bottomArea.setHeight( height + "%" );
    }

    private void setTopAreaHeight( double height ) {
        topArea.setHeight( height + "%" );
    }

    private int getContentHeight() {
        return contentArea.getOffsetHeight();
    }

    private int getContentAbsoluteTop() {
        return contentArea.getAbsoluteTop();
    }

}
