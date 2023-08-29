/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;

public class SignalScopeProvider
        implements SelectorDataProvider {

    private enum SCOPE {

        DEFAULT("",
                "org.kie.workbench.common.stunner.bpmn.client.dataproviders.default"),
        PROCESS_INSTANCE("processInstance",
                         "org.kie.workbench.common.stunner.bpmn.client.dataproviders.processInstance"),
        PROJECT("project",
                "org.kie.workbench.common.stunner.bpmn.client.dataproviders.project"),
        EXTERNAL("external",
                 "org.kie.workbench.common.stunner.bpmn.client.dataproviders.external");

        private final String value;

        private final String i18nKey;

        SCOPE(String value,
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

    private final TranslationService translationService;

    @Inject
    public SignalScopeProvider(final TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostConstruct
    protected void init() {
        valuePosition = new HashMap<>();
        valuePosition.put(null,
                          -1);
        valuePosition.put(SCOPE.DEFAULT.value(),
                          0);
        valuePosition.put(SCOPE.PROCESS_INSTANCE.value(),
                          1);
        valuePosition.put(SCOPE.PROJECT.value(),
                          2);
        valuePosition.put(SCOPE.EXTERNAL.value(),
                          3);
    }

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SelectorData getSelectorData(final FormRenderingContext context) {
        Map<Object, String> values = new TreeMap<>((o1, o2) -> valuePosition.get(o1).compareTo(valuePosition.get(o2)));
        Arrays.stream(SCOPE.values())
                .forEach(scope -> values.put(scope.value(),
                                             translationService.getTranslation(scope.i18nKey())));

        return new SelectorData(values,
                                SCOPE.DEFAULT.value());
    }
}