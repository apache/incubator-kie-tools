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

package org.uberfire.client.gadgets;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen(identifier = "IPInfoGadget")
public class IPInfoGadgetScreen {

    private static final String URL = "http://www.gmodules.com/ig/ifr?url=http://aruljohn.com/gadget/ip.xml&amp;synd=open&amp;w=320&amp;h=150&amp;title=IP+Info&amp;border=http%3A%2F%2Fwww.gmodules.com%2Fig%2Fimages%2F&amp;output=js";

    private Frame frame;

    @PostConstruct
    public void init() {
        frame = new Frame();
        frame.setWidth("100%");
        frame.setHeight("300px");
        frame.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        frame.setUrl(UriUtils.fromString("/google.gadget?src=" + URL).asString());
    }

    @WorkbenchPartTitle
    public String getName() {
        return "IP Info";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return frame;
    }
}
