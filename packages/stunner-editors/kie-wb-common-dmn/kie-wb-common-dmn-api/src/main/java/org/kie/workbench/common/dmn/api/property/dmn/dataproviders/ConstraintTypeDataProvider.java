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

package org.kie.workbench.common.dmn.api.property.dmn.dataproviders;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.model.ConstraintType;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;

public class ConstraintTypeDataProvider implements SelectorDataProvider {

    private final TranslationService translationService;

    private static Map<Object, Integer> valuePosition;

    private static final String KEY_PREFIX = "org.kie.workbench.common.dmn.api.definition.model.ConstraintType.";

    @Inject
    public ConstraintTypeDataProvider(final TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostConstruct
    protected void init() {
        valuePosition = new HashMap<>();

        final ConstraintType[] enums = ConstraintType.class.getEnumConstants();

        for (int i = 0; i < enums.length; i++) {
            valuePosition.put(enums[i].value(), i);
        }

        valuePosition.put("", -1);
    }

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    public SelectorData getSelectorData(final FormRenderingContext context) {
        final Map<Object, String> values = new TreeMap<>(Comparator.comparing(o -> valuePosition.get(o)));
        Arrays.stream(ConstraintType.values())
                .forEach(scope -> values.put(scope.value(),
                                             translationService.getTranslation(KEY_PREFIX + scope.value())));

        values.put("", translationService.getTranslation(KEY_PREFIX + "selectType"));
        return new SelectorData(values,
                                ConstraintType.EXPRESSION.value());
    }
}
