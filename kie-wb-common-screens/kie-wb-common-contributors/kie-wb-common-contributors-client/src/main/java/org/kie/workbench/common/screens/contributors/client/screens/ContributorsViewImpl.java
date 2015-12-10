/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.contributors.client.screens;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.displayer.client.Displayer;
import org.kie.workbench.common.screens.contributors.client.resources.i18n.ContributorsConstants;
import org.kie.workbench.common.screens.contributors.client.resources.i18n.ContributorsI18n;

/**
 * A dashboard showing KPIs for all the commits over any of the GIT managed repos.
 */
public class ContributorsViewImpl extends Composite implements ContributorsView {

    interface ContributorsViewBinder extends UiBinder<Widget, ContributorsViewImpl>{}
    private static final ContributorsViewBinder uiBinder = GWT.create(ContributorsViewBinder.class);

    @UiField(provided = true)
    Displayer commitsEvolutionDisplayer;

    @UiField(provided = true)
    Displayer yearsSelectorDisplayer;

    @UiField(provided = true)
    Displayer quarterSelectorDisplayer;

    @UiField(provided = true)
    Displayer dayOfWeekSelectorDisplayer;

    @UiField(provided = true)
    Displayer commitsPerOrganization;

    @UiField(provided = true)
    Displayer allCommitsDisplayer;

    @UiField(provided = true)
    Displayer organizationSelectorDisplayer;

    @UiField(provided = true)
    Displayer repositorySelectorDisplayer;

    @UiField(provided = true)
    Displayer authorSelectorDisplayer;

    @UiField(provided = true)
    Displayer topAuthorSelectorDisplayer;

    @Override
    public void init(ContributorsScreen presenter,
                     Displayer commitsPerOrganization,
                     Displayer commitsEvolutionDisplayer,
                     Displayer organizationSelectorDisplayer,
                     Displayer repositorySelectorDisplayer,
                     Displayer authorSelectorDisplayer,
                     Displayer topAuthorSelectorDisplayer,
                     Displayer yearsSelectorDisplayer,
                     Displayer quarterSelectorDisplayer,
                     Displayer dayOfWeekSelectorDisplayer,
                     Displayer allCommitsDisplayer) {

        this.commitsPerOrganization = commitsPerOrganization;
        this.commitsEvolutionDisplayer = commitsEvolutionDisplayer;
        this.organizationSelectorDisplayer = organizationSelectorDisplayer;
        this.repositorySelectorDisplayer = repositorySelectorDisplayer;
        this.authorSelectorDisplayer = authorSelectorDisplayer;
        this.topAuthorSelectorDisplayer = topAuthorSelectorDisplayer;
        this.yearsSelectorDisplayer = yearsSelectorDisplayer;
        this.quarterSelectorDisplayer = quarterSelectorDisplayer;
        this.dayOfWeekSelectorDisplayer = dayOfWeekSelectorDisplayer;
        this.allCommitsDisplayer = allCommitsDisplayer;

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public ContributorsI18n getI18nService() {
        return ContributorsConstants.INSTANCE;
    }
}
