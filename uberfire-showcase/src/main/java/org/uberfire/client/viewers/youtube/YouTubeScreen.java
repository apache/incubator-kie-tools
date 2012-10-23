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

package org.uberfire.client.viewers.youtube;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.events.YouTubeVideo;

import static com.google.gwt.dom.client.Style.Unit.*;

@Dependent
@WorkbenchScreen(identifier = "YouTubeScreen")
public class YouTubeScreen
        extends Composite {

    interface ViewBinder
            extends
            UiBinder<Widget, YouTubeScreen> {

    }

    private static final String URL = "http://www.youtube.com/embed/xnmSR62_4Us?rel=0";

    @UiField
    protected Frame iframe;

    private static ViewBinder uiBinder = GWT.create(ViewBinder.class);

    @PostConstruct
    public void init() {
        initWidget(uiBinder.createAndBindUi(this));
        iframe.setWidth("640px");
        iframe.setHeight("480px");
        iframe.getElement().getStyle().setBorderWidth(0, PX);
        iframe.setUrl(UriUtils.fromString(URL).asString());
    }

    @WorkbenchPartTitle
    public String getName() {
        return "YouTube Video";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return this;
    }

    public void reloadContent(@Observes YouTubeVideo content) {
        iframe.setUrl(UriUtils.fromString(content.getURL()).asString());
    }

}
