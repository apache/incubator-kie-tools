/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.experimental.client.disabled.component;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.experimental.client.resources.i18n.UberfireExperimentalConstants;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinition;

@Dependent
public class DisabledFeatureComponent implements IsElement {

    private DisabledFeatureComponentView view;

    private ExperimentalFeatureDefRegistry defRegistry;

    private TranslationService translationService;

    @Inject
    public DisabledFeatureComponent(DisabledFeatureComponentView view, ExperimentalFeatureDefRegistry defRegistry, TranslationService translationService) {
        this.view = view;
        this.defRegistry = defRegistry;
        this.translationService = translationService;
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void show(String featureId) {
        ExperimentalFeatureDefinition feature = defRegistry.getFeatureById(featureId);

        PortablePreconditions.checkNotNull("experimentalFeature", feature);

        String featureName = Optional.ofNullable(translationService.getTranslation(feature.getNameKey())).orElse(featureId);
        String text;

        if (feature.isGlobal()) {
            text = translationService.format(UberfireExperimentalConstants.disabledGlobalExperimentalFeature, featureName);
        } else {
            text = translationService.format(UberfireExperimentalConstants.disabledExperimentalFeature, featureName);
        }

        view.show(text);
    }
}
