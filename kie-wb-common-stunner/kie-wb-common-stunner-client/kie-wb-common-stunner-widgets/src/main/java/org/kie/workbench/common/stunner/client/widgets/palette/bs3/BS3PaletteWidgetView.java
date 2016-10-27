/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.palette.bs3;

import com.ait.lienzo.client.core.shape.Group;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.client.widgets.palette.PaletteWidgetView;
import org.kie.workbench.common.stunner.core.client.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeGlyph;
import org.uberfire.client.mvp.UberView;

import javax.enterprise.context.Dependent;

@Dependent
public class BS3PaletteWidgetView extends Composite implements PaletteWidgetView<IsWidget>, UberView<BS3PaletteWidgetImpl> {

    interface ViewBinder extends UiBinder<Widget, BS3PaletteWidgetView> {

    }

    private static BS3PaletteWidgetView.ViewBinder uiBinder = GWT.create( BS3PaletteWidgetView.ViewBinder.class );

    @UiField
    FlowPanel mainPanel;

    @UiField
    FlowPanel emptyViewPanel;

    @UiField
    FlowPanel palettePanel;

    ShapeGlyphDragHandler<Group> shapeGlyphDragHandler;
    private BS3PaletteWidgetImpl presenter;

    public BS3PaletteWidgetView() {
    }

    @Override
    public void init( final BS3PaletteWidgetImpl presenter ) {
        this.presenter = presenter;
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void showEmptyView( final boolean visible ) {
        emptyViewPanel.setVisible( visible );
        palettePanel.setVisible( !visible );
    }

    @Override
    public void showDragProxy( final String itemId,
                               final double x,
                               final double y ) {
        final ShapeGlyph<Group> glyph = ( ShapeGlyph<Group> ) presenter.getShapeGlyph( itemId );
        shapeGlyphDragHandler.show( glyph, x, y, new ShapeGlyphDragHandler.Callback() {

            @Override
            public void onMove( final double x,
                                final double y ) {
                presenter.onDragProxyMove( itemId, x, y );

            }

            @Override
            public void onComplete( final double x,
                                    final double y ) {
                presenter.onDragProxyComplete( itemId, x, y );

            }

        } );

    }

    @Override
    public void setBackgroundColor( final String color ) {
        palettePanel.getElement().getStyle().setBackgroundColor( color );

    }

    @Override
    public void setMarginTop( final int mTop ) {
        palettePanel.getElement().getStyle().setMarginTop( mTop, Style.Unit.PX );

    }

    @Override
    public void show( final IsWidget paletteView ) {
        palettePanel.add( paletteView );
    }

    @Override
    public void show( final IsWidget paletteView,
                      final int width,
                      final int height ) {
        palettePanel.add( paletteView );
        palettePanel.getElement().getStyle().setWidth( width, Style.Unit.PX );
        palettePanel.getElement().getStyle().setHeight( height, Style.Unit.PX );
    }

    @Override
    public void clear() {
        palettePanel.clear();
    }

    @Override
    public void destroy() {
        mainPanel.clear();
    }

}
