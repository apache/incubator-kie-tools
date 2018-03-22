/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.i18n;

import java.util.Optional;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public abstract class AbstractTranslationService implements StunnerTranslationService {

    public static final String I18N_SEPARATOR = ".";

    public static final String TITLE_SUFFIX = "title";
    public static final String DESCRIPTION_SUFFIX = "description";
    public static final String CAPTION_SUFFIX = "caption";

    @Override
    public String getDefinitionSetDescription(String defSetId) {
        PortablePreconditions.checkNotNull("defSetId",
                                           defSetId);
        return getValue(defSetId + I18N_SEPARATOR + DESCRIPTION_SUFFIX);
    }

    @Override
    public String getPropertySetName(String propSetId) {
        return getValue(propSetId + I18N_SEPARATOR + CAPTION_SUFFIX);
    }

    @Override
    public String getDefinitionTitle(String defId) {
        PortablePreconditions.checkNotNull("defId",
                                           defId);
        return getValue(defId + I18N_SEPARATOR + TITLE_SUFFIX);
    }

    @Override
    public String getDefinitionDescription(String defId) {
        PortablePreconditions.checkNotNull("defId",
                                           defId);
        return getValue(defId + I18N_SEPARATOR + DESCRIPTION_SUFFIX);
    }

    @Override
    public String getPropertyCaption(String propId) {
        PortablePreconditions.checkNotNull("propId",
                                           propId);
        return getValue(propId + I18N_SEPARATOR + CAPTION_SUFFIX);
    }

    @Override
    public String getPropertyDescription(String propId) {
        PortablePreconditions.checkNotNull("propId",
                                           propId);
        return getValue(propId + I18N_SEPARATOR + DESCRIPTION_SUFFIX);
    }

    protected String getRuleViolationMessage(final RuleViolation ruleViolation) {
        final String type = ruleViolation.getClass().getName();
        final Optional<Object[]> arguments = ruleViolation.getArguments();
        return arguments.isPresent() ?
                getValue(type,
                         arguments.get()) :
                getValue(type);
    }
}
