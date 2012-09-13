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

package org.uberfire.client.editors.google;

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
@WorkbenchScreen(identifier = "GoogleGadgetScreen")
public class GoogleGadgetScreen {

    private static final String URL = "http://www.gmodules.com/ig/ifr?url=http://www.labpixies.com/campaigns/calories/calories.xml&amp;up_k1=&amp;up_k2=&amp;up_k3=&amp;up_breakfast=&amp;up_lunch=&amp;up_dinner=&amp;up_misc=&amp;up_calories_quota=&amp;up_calorie_settings=&amp;up_calorie_gadget_settings=&amp;up_first_load=1&amp;up_curr_tab=s&amp;up_last_search=bread&amp;up_c1=&amp;up_c2=&amp;up_my_items_count=0&amp;synd=open&amp;w=320&amp;h=300&amp;title=__MSG_title__&amp;border=%23ffffff%7C3px%2C1px+solid+%23999999&amp;output=js";

    private Frame frame;

    @PostConstruct
    public void init() {
        frame = new Frame();
        frame.setWidth("100%");
        frame.setHeight("300px"); //TODO {manstis} Hack so gadget is not truncated
        frame.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        frame.setUrl(UriUtils.fromString("/google.gadget?src=" + URL).asString());
    }

    @WorkbenchPartTitle
    public String getName() {
        return "Demo Simple Gag";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return frame;
    }
}
