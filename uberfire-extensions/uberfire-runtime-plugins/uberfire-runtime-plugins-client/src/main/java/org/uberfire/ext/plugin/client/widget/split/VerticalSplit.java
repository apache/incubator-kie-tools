/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.plugin.client.widget.split;

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
