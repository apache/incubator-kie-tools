package org.kie.workbench.common.screens.home.client.widgets.carousel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Carousel
 */
public class CarouselWidget extends Widget {

    interface CarouselBinder
            extends
            UiBinder<DivElement, CarouselWidget> {

    }

    private static CarouselBinder uiBinder = GWT.create( CarouselBinder.class );

    @UiField
    DivElement itemsElement;

    public CarouselWidget() {
        setElement( uiBinder.createAndBindUi( this ) );
    }

    public void addCarouselEntry( final CarouselEntryWidget entry ) {
        itemsElement.appendChild( entry.getElement() );
    }

}
