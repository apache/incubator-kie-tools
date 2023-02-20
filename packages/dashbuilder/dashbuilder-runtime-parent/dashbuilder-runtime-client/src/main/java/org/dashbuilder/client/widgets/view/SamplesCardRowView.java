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

package org.dashbuilder.client.widgets.view;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHeadingElement;
import org.dashbuilder.client.widgets.SampleCard;
import org.dashbuilder.client.widgets.SamplesCardRow;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class SamplesCardRowView implements SamplesCardRow.View {

    @Inject
    @DataField
    HTMLDivElement samplesCardRowContainer;

    @Inject
    @DataField
    @Named("h3")
    HTMLHeadingElement rowTitle;

    @Inject
    @DataField
    HTMLDivElement samplesRowLine;

    @Override
    public HTMLElement getElement() {
        return samplesCardRowContainer;
    }

    @Override
    public void init(SamplesCardRow presenter) {
        // empty
    }

    @Override
    public void setTitleAndSamples(String title,
                                   List<SampleCard> sampleCards) {
        samplesCardRowContainer.id = title + "-container";
        rowTitle.textContent = title;
        rowTitle.id = title;
        sampleCards.forEach(card -> samplesRowLine.appendChild(card.getElement()));
    }

}
