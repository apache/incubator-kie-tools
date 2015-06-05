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

package org.uberfire.client.screens.youtube;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.events.YouTubeVideo;

@Dependent
@WorkbenchScreen(identifier = "YouTubeScreen")
@Templated
public class YouTubeScreen extends Composite {

    private static final String URL = "http://www.youtube.com/embed/xnmSR62_4Us?rel=0";

    @Inject
    @DataField
    protected Frame iframe;

    @PostConstruct
    public void init() {
        iframe.setUrl(UriUtils.fromString(URL).asString());
    }

    @WorkbenchPartTitle
    public String getName() {
        return "YouTube Video";
    }

    public void reloadContent(@Observes YouTubeVideo content) {
        iframe.setUrl(UriUtils.fromString(content.getURL()).asString());
    }

}
