/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.screens;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import org.dashbuilder.client.resources.AppResource;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@Templated
@WorkbenchScreen(identifier="GalleryHomeScreen")
public class GalleryHomeScreen extends Composite {

    @Inject
    @DataField
    private FlowPanel galleryImagePanel;

    @Inject
    @DataField
    private ParagraphElement paragraph1;

    @Inject
    @DataField
    private SpanElement span2a;

    @Inject
    @DataField
    private SpanElement span2b;

    @Inject
    @DataField
    private AnchorElement gitHubLink;

    @WorkbenchPartTitle
    public String getScreenTitle() {
        return AppConstants.INSTANCE.gallerytree_home();
    }

    @PostConstruct
    void doLayout() {
        Image image = new Image(AppResource.INSTANCE.images().barChartLogo());
        galleryImagePanel.add(image);
        paragraph1.setInnerText(AppConstants.INSTANCE.gallerytree_home_p1());
        span2a.setInnerText(AppConstants.INSTANCE.gallerytree_home_s2a());
        span2b.setInnerText(AppConstants.INSTANCE.gallerytree_home_s2b());
        gitHubLink.setInnerText(AppConstants.INSTANCE.gallerytree_home_ghublink());
    }
}
