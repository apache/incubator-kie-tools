package org.kie.uberfire.wires.core.client.actions;

import com.emitrom.lienzo.client.core.event.NodeMouseClickHandler;
import com.emitrom.lienzo.client.core.shape.Picture;
import com.emitrom.lienzo.client.core.shape.Rectangle;
import com.emitrom.lienzo.shared.core.types.ColorName;
import com.google.gwt.resources.client.ImageResource;

public class StencilActionBuilder {

    private static final int HEIGHT_BOUNDING = 20;
    private static final int WIDTH_BOUNDING = 20;
    private static final int HEIGHT_PICTURE = 16;
    private static final int WIDTH_PICTURE = 16;

    public ActionShape build( final String pictureCategory,
                              final NodeMouseClickHandler clickHandler,
                              final ImageResource img ) {
        final Rectangle bounding = getBoundingImage( clickHandler );
        final Picture icon = new Picture( img,
                                          WIDTH_PICTURE,
                                          HEIGHT_PICTURE,
                                          false,
                                          pictureCategory );

        final ActionShape shape = new ActionShape();
        shape.setPicture( icon );
        shape.setBounding( bounding );
        return shape;
    }

    private Rectangle getBoundingImage( final NodeMouseClickHandler clickHandler ) {
        final Rectangle bounding = new Rectangle( WIDTH_BOUNDING,
                                                  HEIGHT_BOUNDING ).setX( 0 ).setY( 0 ).setStrokeColor( ColorName.WHITE.getValue() );
        bounding.addNodeMouseClickHandler( clickHandler );
        return bounding;
    }

}
