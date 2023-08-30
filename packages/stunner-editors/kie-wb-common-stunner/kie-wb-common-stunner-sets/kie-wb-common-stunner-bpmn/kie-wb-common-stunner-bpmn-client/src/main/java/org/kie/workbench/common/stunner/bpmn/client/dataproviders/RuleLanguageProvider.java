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
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleLanguage;
import org.kie.workbench.common.stunner.core.util.SafeComparator;

@Dependent
public class RuleLanguageProvider implements SelectorDataProvider {

    private enum LANGUAGE {

        DRL(RuleLanguage.DRL, "org.kie.workbench.common.stunner.bpmn.client.dataproviders.RuleLanguageProvider.DRL"),
        DMN(RuleLanguage.DMN, "org.kie.workbench.common.stunner.bpmn.client.dataproviders.RuleLanguageProvider.DMN");

        private final String value;

        private final String i18nKey;

        LANGUAGE(String value,
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
    public RuleLanguageProvider(final TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostConstruct
    protected void init() {
        valuePosition = new HashMap<>();
        valuePosition.put(LANGUAGE.DMN.value(), 0);
        valuePosition.put(LANGUAGE.DRL.value(), 1);
    }

    private SafeComparator<Object> getComparator() {
        return SafeComparator.TO_STRING_REVERSE_COMPARATOR;
    }

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SelectorData getSelectorData(final FormRenderingContext context) {
        Map<Object, String> values = new TreeMap<>(SafeComparator.of(this::getComparator));
        Arrays.stream(LANGUAGE.values())
                .forEach(ruleLanguage ->
                                 values.put(ruleLanguage.value(),
                                            translationService.getTranslation(ruleLanguage.i18nKey())));

        return new SelectorData(values, LANGUAGE.DRL.value());
    }
}