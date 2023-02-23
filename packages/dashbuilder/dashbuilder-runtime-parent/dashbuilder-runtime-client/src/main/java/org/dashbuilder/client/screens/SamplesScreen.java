/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.client.services.SamplesService;
import org.dashbuilder.client.widgets.SampleCard;
import org.dashbuilder.client.widgets.SamplesCardRow;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;

/**
 * Screen displayed when there's no dashboards available and samples url is configured
 *
 */
@ApplicationScoped
@WorkbenchScreen(identifier = SamplesScreen.ID)
public class SamplesScreen {

    public static final String ID = "SamplesScreen";

    private static final AppConstants i18n = AppConstants.INSTANCE;

    @Inject
    View view;

    @Inject
    RouterScreen router;

    @Inject
    SamplesService samplesService;

    @Inject
    ManagedInstance<SampleCard> sampleCardInstance;

    @Inject
    ManagedInstance<SamplesCardRow> samplesCardRowInstance;

    public interface View extends UberElemental<SamplesScreen> {

        void addRows(List<SamplesCardRow> rows);

        void clear();
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @WorkbenchPartTitle
    public String title() {
        return i18n.samplesScreenTitle();
    }

    @WorkbenchPartView
    protected View getPart() {
        return view;
    }

    @OnOpen
    protected void onOpen() {
        var rows = new ArrayList<SamplesCardRow>();
        samplesService.samplesByCategory().forEach((cat, samples) -> {
            var samplesCards = new ArrayList<SampleCard>();
            samples.forEach(sample -> {
                var card = sampleCardInstance.get();
                card.setSample(sample, () -> router.loadDashboard(sample.getSourceUrl()));
                samplesCards.add(card);
            });
            var row = samplesCardRowInstance.get();
            row.setCategoryAndSamples(cat, samplesCards);
            rows.add(row);
        });
        view.addRows(rows);
    }

    @OnClose
    public void clear() {
        sampleCardInstance.destroyAll();
        samplesCardRowInstance.destroyAll();
        view.clear();
    }
}
