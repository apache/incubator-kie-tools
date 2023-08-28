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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class SamplesCardRow implements IsElement {

    static final AppConstants i18n = AppConstants.INSTANCE;

    @Inject
    View view;

    private String category;

    public interface View extends UberElemental<SamplesCardRow> {

        void setTitleAndSamples(String title,
                                List<SampleCard> sampleCards);

    }

    @PostConstruct
    public void init() {
        this.view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return this.view.getElement();
    }

    public void setCategoryAndSamples(String category,
                                      List<SampleCard> cards) {
        this.category = category;
        view.setTitleAndSamples(category, cards);
    }

    public String getCategory() {
        return category;
    }

    public static String produceCategoryTitleId(String title) {
        return title + "-container";
    }

}
