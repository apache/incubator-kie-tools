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

import com.google.gwt.dom.client.LIElement;
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
@WorkbenchScreen(identifier="HomeScreen")
public class HomeScreen extends Composite {

    @Inject
    @DataField
    private FlowPanel homeImagePanel;

    @Inject
    @DataField
    private ParagraphElement intro;

    @Inject
    @DataField
    private SpanElement upcoming;

    @Inject
    @DataField
    private LIElement feature1;

    @Inject
    @DataField
    private LIElement feature2;

    @Inject
    @DataField
    private LIElement feature3;

    @Inject
    @DataField
    private LIElement feature4;

    @Inject
    @DataField
    private LIElement feature5;

    @Inject
    @DataField
    private LIElement feature6;

    @Inject
    @DataField
    private LIElement feature7;

    @Inject
    @DataField
    private LIElement feature8;

    @Inject
    @DataField
    private LIElement feature9;

    @Inject
    @DataField
    private LIElement feature10;

    @Inject
    @DataField
    private LIElement feature11;

    @Inject
    @DataField
    private LIElement feature12;

    @Inject
    @DataField
    private LIElement feature13;

    @Inject
    @DataField
    private SpanElement arch;

    @Inject
    @DataField
    private LIElement arch1;

    @Inject
    @DataField
    private LIElement arch2;

    @Inject
    @DataField
    private LIElement arch3;

    @Inject
    @DataField
    private LIElement arch4;

    @Inject
    @DataField
    private LIElement arch5;

    @Inject
    @DataField
    private SpanElement arch6a;

    @Inject
    @DataField
    private SpanElement arch6b;

    @Inject
    @DataField
    private SpanElement furtherinfo;

    @Inject
    @DataField
    private ParagraphElement license;
    
    @WorkbenchPartTitle
    public String getScreenTitle() {
        return "Welcome to Dashbuilder";
    }

    @PostConstruct
    void doLayout() {
        Image image = new Image(AppResource.INSTANCE.images().pieChartLogo());
        homeImagePanel.add(image);

        intro.setInnerText(AppConstants.INSTANCE.home_intro());
        upcoming.setInnerText(AppConstants.INSTANCE.home_upcoming());

        feature1.setInnerText(AppConstants.INSTANCE.home_feature1());
        feature2.setInnerText(AppConstants.INSTANCE.home_feature2());
        feature3.setInnerText(AppConstants.INSTANCE.home_feature3());
        feature4.setInnerText(AppConstants.INSTANCE.home_feature4());
        feature5.setInnerText(AppConstants.INSTANCE.home_feature5());
        feature6.setInnerText(AppConstants.INSTANCE.home_feature6());
        feature7.setInnerText(AppConstants.INSTANCE.home_feature7());
        feature8.setInnerText(AppConstants.INSTANCE.home_feature8());
        feature9.setInnerText(AppConstants.INSTANCE.home_feature9());
        feature10.setInnerText(AppConstants.INSTANCE.home_feature10());
        feature11.setInnerText(AppConstants.INSTANCE.home_feature11());
        feature12.setInnerText(AppConstants.INSTANCE.home_feature12());
        feature13.setInnerText(AppConstants.INSTANCE.home_feature13());

        arch.setInnerText(AppConstants.INSTANCE.home_arch());
        arch1.setInnerText(AppConstants.INSTANCE.home_arch1());
        arch2.setInnerText(AppConstants.INSTANCE.home_arch2());
        arch3.setInnerText(AppConstants.INSTANCE.home_arch3());
        arch4.setInnerText(AppConstants.INSTANCE.home_arch4());
        arch5.setInnerText(AppConstants.INSTANCE.home_arch5());
        arch6a.setInnerText(AppConstants.INSTANCE.home_arch6a());
        arch6b.setInnerText(AppConstants.INSTANCE.home_arch6b());

        furtherinfo.setInnerText(AppConstants.INSTANCE.home_furtherinfo());
        license.setInnerText(AppConstants.INSTANCE.home_license());
    }
}
