package org.kie.workbench.common.screens.home.client.carousel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Carousel
 */
public class Carousel extends Widget {

    interface CarouselBinder
            extends
            UiBinder<DivElement, Carousel> {

    }

    private static CarouselBinder uiBinder = GWT.create( CarouselBinder.class );

    @UiField
    DivElement itemsElement;

    public Carousel() {
        setElement( uiBinder.createAndBindUi( this ) );
    }

    public void addCarouselEntry( final CarouselEntry entry ) {
        itemsElement.appendChild( entry.getElement() );
    }

}
