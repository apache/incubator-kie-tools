/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;


import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;


import org.uberfire.client.common.StackItemHeaderViewImpl;

public class Util {


    interface HeaderTemplate
            extends
            SafeHtmlTemplates {

        @Template("{0} {1}")
        SafeHtml message(SafeHtml imageHTML, SafeHtml message);
    }

    private static final HeaderTemplate HEADER_TEMPLATE = GWT.create(HeaderTemplate.class);

    /**
     * Get a string representation of the header that includes an image and some
     * text.
     *
     * @param image the {@link com.google.gwt.resources.client.ImageResource} to add next to the header
     * @param text  the header text
     * @return the header as a string
     */
    public static SafeHtml getHeader(ImageResource image,
                                     final String text) {
        return HEADER_TEMPLATE.message(SafeHtmlUtils.fromTrustedString(AbstractImagePrototype.create(image).getHTML()),
                new SafeHtml() {
                    public String asString() {
                        return text;
                    }
                });
    }

    /**
     * Get a string representation of the header that includes an image and some
     * text.
     *
     * @param image the {@link ImageResource} to add next to the header
     * @param text  the header text
     * @return the header as a string
     */
    public static StackItemHeaderViewImpl getHeaderHTML(ImageResource image,
                                                        String text) {

        StackItemHeaderViewImpl stackItemHeaderViewImpl = new StackItemHeaderViewImpl();

        stackItemHeaderViewImpl.setText(text);
        stackItemHeaderViewImpl.setImageResource(image);

        return stackItemHeaderViewImpl;
    }

    /**
     * The URL that will be used to open up assets in a feed.
     * (by tacking asset id on the end, of course !).
     */
    public static String getSelfURL() {
        String selfURL = Window.Location.getHref();
        if (selfURL.contains("#")) {
            selfURL = selfURL.substring(0,
                    selfURL.indexOf("#"));
        }
        return selfURL;
    }
}