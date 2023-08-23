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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;

import static org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils.findBuiltInTypeByName;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.ConstraintPlaceholderHelper_SampleDefault;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.ConstraintPlaceholderHelper_SentenceDefault;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Dependent
public class ConstraintPlaceholderHelper {

    private static final String CONSTRAINT_PLACEHOLDER_SENTENCE_PREFIX = "ConstraintPlaceholderHelper.Sentence";

    private static final String CONSTRAINT_PLACEHOLDER_SAMPLE_PREFIX = "ConstraintPlaceholderHelper.Sample";

    private final TranslationService translationService;

    @Inject
    public ConstraintPlaceholderHelper(final TranslationService translationService) {
        this.translationService = translationService;
    }

    public String getPlaceholderSentence(final String type) {
        final String sentence = getTranslation(type, CONSTRAINT_PLACEHOLDER_SENTENCE_PREFIX);
        return !isEmpty(sentence) ? sentence : defaultSentence();
    }

    public String getPlaceholderSample(final String type) {
        final String sentence = getTranslation(type, CONSTRAINT_PLACEHOLDER_SAMPLE_PREFIX);
        return !isEmpty(sentence) ? sentence : defaultSample();
    }

    private String getTranslation(final String type,
                                  final String prefix) {

        final Optional<BuiltInType> builtInType = findBuiltInTypeByName(type);
        final String i18nKey = prefix + builtInType.map(this::builtInTypeAsKey).orElse("");

        return translationService.getTranslation(i18nKey);
    }

    private String builtInTypeAsKey(final BuiltInType builtInType) {

        final String[] names = builtInType.getNames();
        final String name = names[names.length - 1];

        return capitalize(name);
    }

    private String capitalize(final String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private String defaultSentence() {
        return translationService.format(ConstraintPlaceholderHelper_SentenceDefault);
    }

    private String defaultSample() {
        return translationService.format(ConstraintPlaceholderHelper_SampleDefault);
    }
}
