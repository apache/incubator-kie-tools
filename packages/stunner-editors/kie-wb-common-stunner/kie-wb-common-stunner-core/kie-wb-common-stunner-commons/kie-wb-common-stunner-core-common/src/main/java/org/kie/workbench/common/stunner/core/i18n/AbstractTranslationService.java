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


package org.kie.workbench.common.stunner.core.i18n;

import java.util.Objects;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public abstract class AbstractTranslationService implements StunnerTranslationService {

    public static final String I18N_SEPARATOR = ".";

    public static final String TITLE_SUFFIX = "title";
    public static final String DESCRIPTION_SUFFIX = "description";
    public static final String SVG_NODE_ID_SUFFIX = "svgNodeId";
    public static final String CAPTION_SUFFIX = "caption";
    public static final String LABEL_SUFFIX = "label";

    @Override
    public String getDefinitionSetDescription(String defSetId) {
        checkNotNull("defSetId", defSetId);
        return getValue(defSetId + I18N_SEPARATOR + DESCRIPTION_SUFFIX);
    }

    @Override
    public String getDefinitionTitle(String defId) {
        checkNotNull("defId", defId);
        return getValue(defId + I18N_SEPARATOR + TITLE_SUFFIX);
    }

    @Override
    public String getDefinitionDescription(String defId) {
        checkNotNull("defId", defId);
        return getValue(defId + I18N_SEPARATOR + DESCRIPTION_SUFFIX);
    }

    @Override
    public Optional<String> getDefinitionSetSvgNodeId(String defId) {
        return Optional.ofNullable(getValue(defId + I18N_SEPARATOR + SVG_NODE_ID_SUFFIX));
    }

    @Override
    public String getPropertyCaption(String propId) {
        checkNotNull("propId", propId);
        return Optional
                .ofNullable(getValue(propId + I18N_SEPARATOR + CAPTION_SUFFIX))
                .orElseGet(() -> Optional.ofNullable(getValue(propId + I18N_SEPARATOR + LABEL_SUFFIX))
                        .orElseGet(() -> Optional.ofNullable(getDefinitionTitle(propId))
                                .orElse(propId)));
    }

    @Override
    public String getViolationMessage(RuleViolation ruleViolation) {
        return getRuleViolationMessage(ruleViolation);
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
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
