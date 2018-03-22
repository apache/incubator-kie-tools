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
package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

public class AdHocOrderingProvider
        implements SelectorDataProvider {

    private enum ORDERING {

        SEQUENTIAL("Sequential",
                   "org.kie.workbench.common.stunner.bpmn.client.dataproviders.AdHocOrderingProvider.sequential"),
        PARALLEL("Parallel",
                 "org.kie.workbench.common.stunner.bpmn.client.dataproviders.AdHocOrderingProvider.parallel");

        private final String value;

        private final String i18nKey;

        ORDERING(String value,
                 String i18nKey) {
            this.value = value;
            this.i18nKey = i18nKey;
        }

        public String value() {
            return value;
        }

        public String i18nKey() {
            return i18nKey;
        }

    }

    private static Map<Object, Integer> valuePosition;

    private final ClientTranslationService translationService;

    @Inject
    public AdHocOrderingProvider(final ClientTranslationService translationService) {
        this.translationService = translationService;
    }

    @PostConstruct
    protected void init() {
        valuePosition = new HashMap<>();
        valuePosition.put(null,
                          -1);
        valuePosition.put(ORDERING.SEQUENTIAL.value(),
                          0);
        valuePosition.put(ORDERING.PARALLEL.value(),
                          1);
    }

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SelectorData getSelectorData(final FormRenderingContext context) {
        Map<Object, String> values = new TreeMap<>(Comparator.comparing(o -> valuePosition.get(o)));
        Arrays.stream(ORDERING.values())
                .forEach(scope -> values.put(scope.value(),
                                             translationService.getValue(scope.i18nKey())));

        return new SelectorData(values,
                                ORDERING.SEQUENTIAL.value());
    }
}