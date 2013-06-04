package org.kie.workbench.common.screens.home.client.widgets.carousel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Carousel image
 */
public class CarouselEntryWidget extends Widget {

    interface CarouselItemBinder
            extends
            UiBinder<DivElement, CarouselEntryWidget> {

    }

    private static CarouselItemBinder uiBinder = GWT.create( CarouselItemBinder.class );

    @UiField
    ImageElement imageElement;

    @UiField
    HeadingElement headingElement;

    @UiField
    ParagraphElement subHeadingElement;

    public CarouselEntryWidget() {
        setElement( uiBinder.createAndBindUi( this ) );
    }

    public void setActive( final boolean active ) {
        if ( active ) {
            getElement().addClassName( "active" );
        } else {
            getElement().removeClassName( "active" );
        }
    }

    public void setImageUri( final SafeUri uri ) {
        imageElement.setSrc( uri.asString() );
    }

    public void setHeading( final SafeHtml heading ) {
        headingElement.setInnerText( heading.asString() );
    }

    public void setSubHeading( final SafeHtml subHeading ) {
        subHeadingElement.setInnerText( subHeading.asString() );
    }

}
