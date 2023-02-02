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

package org.dashbuilder.client.widgets;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.client.screens.RouterScreen;
import org.dashbuilder.client.services.SamplesService.SampleInfo;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElemental;

/**
 * Shows a sample in cards
 *
 */
@Dependent
public class SampleCard implements IsElement {

    static final AppConstants i18n = AppConstants.INSTANCE;

    @Inject
    View view;

    @Inject
    RouterScreen routerScreen;

    @Inject
    PlaceManager placeManager;

    public interface View extends UberElemental<SampleCard> {

        void setSampleData(String sampleName, String sampleUrl, Runnable sampleClickCallback);

        void setSampleSvg(String sampleSvg);

    }

    @PostConstruct
    public void init() {
        this.view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return this.view.getElement();
    }

    public void setSample(SampleInfo sample, Runnable sampleClickCallback) {
        view.setSampleData(sample.getName(), sample.getSourceUrl(), sampleClickCallback);
        DomGlobal.fetch(sample.getSvgUrl()).then(r -> {
            if (r.status == 404) {
                return null;
            }
            return r.text();
        }).then(txt -> {
            if (txt != null && !txt.trim().isEmpty()) {
                view.setSampleSvg(txt);
            }
            return null;
        });
    }

}
