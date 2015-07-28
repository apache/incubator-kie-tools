/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.home.client.widgets.carousel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.CarouselSlide;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.html.Paragraph;

/**
 * A Carousel image
 */
public class CarouselEntryWidget extends Composite {

    interface CarouselItemBinder
            extends
            UiBinder<CarouselSlide, CarouselEntryWidget> {

    }

    private static CarouselItemBinder uiBinder = GWT.create( CarouselItemBinder.class );

    @UiField
    Image imageElement;

    @UiField
    Heading headingElement;

    @UiField
    Paragraph subHeadingElement;

    @UiField
    CarouselSlide slide;

    public CarouselEntryWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void setActive( final boolean active ) {
        slide.setActive( active );
    }

    public void setImageUri( final SafeUri uri ) {
        imageElement.setUrl( uri.asString() );
    }

    public void setHeading( final SafeHtml heading ) {
        headingElement.setText( heading.asString() );
    }

    public void setSubHeading( final SafeHtml subHeading ) {
        subHeadingElement.setText( subHeading.asString() );
    }

}
