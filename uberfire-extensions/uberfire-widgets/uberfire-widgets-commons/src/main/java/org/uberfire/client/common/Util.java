/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.client.common;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;

public class Util {

    /**
     * Get a string representation of the header that includes an image and some text.
     * @param image the {@link ImageResource} to add next to the header
     * @param text the header text
     * @return the header as a string
     */
    public static String getHeader( final ImageResource image,
                                    final String text ) {
        return AbstractImagePrototype.create( image ).getHTML() + " " + text;
    }

    /**
     * Get a HTML representation of the header that includes an image and some text.
     * @param image the {@link ImageResource} to add next to the header
     * @param text the header text
     * @return the header as HTML
     */
    public static HTML getHeaderHTML( final ImageResource image,
                                      final String text ) {
        HeaderHTML headerHTML = new HeaderHTML();
        headerHTML.setText( text );
        headerHTML.setImageResource( image );
        return new HTML( headerHTML.getElement().getString() );
    }

    /**
     * Get a SafeHtml representation of the header that includes an image and some text.
     * @param image the {@link ImageResource} to add next to the header
     * @param text the header text
     * @return the header as SafeHtml
     */
    public static SafeHtml getHeaderSafeHtml( final ImageResource image,
                                              final String text ) {
        HeaderHTML headerHTML = new HeaderHTML();
        headerHTML.setText( text );
        headerHTML.setImageResource( image );
        return toSafeHtml( headerHTML.getElement().getString() );
    }

    /**
     * The URL that will be used to open up assets in a feed.
     * (by tacking asset id on the end, of course !).
     */
    public static String getSelfURL() {
        String selfURL = Window.Location.getHref();
        if ( selfURL.contains( "#" ) ) {
            selfURL = selfURL.substring( 0,
                                         selfURL.indexOf( "#" ) );
        }
        return selfURL;
    }

    /**
     * Convert String to a SafeHtml
     * @param html
     * @return
     */
    public static SafeHtml toSafeHtml( final String html ) {
        final SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant( html );
        return builder.toSafeHtml();
    }
}