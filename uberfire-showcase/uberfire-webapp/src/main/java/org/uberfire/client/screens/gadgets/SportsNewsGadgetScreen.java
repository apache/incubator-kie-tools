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

package org.uberfire.client.screens.gadgets;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen(identifier = "SportsNewsGadget")
public class SportsNewsGadgetScreen {

    private static final String URL = "http://www.gmodules.com/ig/ifr?url=http://igwidgets.com/lig/gw/f/islk/89/slkm/ik/s/9878767676530/87/hm/espn-sports-rss-feeds.xml&amp;up_entries=3&amp;up_summaries=100&amp;up_extrafeed=http%3A%2F%2Fsoccernet.espn.go.com%2Frss%2Fnews&amp;up_extratitle=Soccer&amp;up_subject=ESPN&amp;up_selectedTab=&amp;synd=open&amp;w=320&amp;h=300&amp;title=Ultimate+Sports+News&amp;border=http%3A%2F%2Fwww.gmodules.com%2Fig%2Fimages%2F&amp;output=js";

    private Frame frame;

    @PostConstruct
    public void init() {
        frame = new Frame();
        frame.setWidth("100%");
        frame.setHeight("300px");
        frame.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        frame.setUrl(UriUtils.fromString(GWT.getModuleBaseURL() + "google.gadget?src=" + URL).asString());
    }

    @WorkbenchPartTitle
    public String getName() {
        return "Sports News";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return frame;
    }
}
