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

package org.kie.workbench.common.stunner.core.client.i18n;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.i18n.AbstractTranslationService;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

@Singleton
public class ClientTranslationService extends AbstractTranslationService {

    private final TranslationService erraiTranslationService;

    @Inject
    public ClientTranslationService(final TranslationService erraiTranslationService) {
        this.erraiTranslationService = erraiTranslationService;
    }

    @Override
    public String getValue(final String key) {
        return erraiTranslationService.getTranslation(key);
    }

    @Override
    public String getValue(final String key,
                           final Object... args) {
        return erraiTranslationService.format(key,
                                              args);
    }

    /**
     * Handles both common rule violation types
     * and client side canvas violation types as well.
     */
    @Override
    public String getViolationMessage(final RuleViolation ruleViolation) {
        if (ruleViolation instanceof CanvasViolation) {
            return getCanvasViolationMessage((CanvasViolation) ruleViolation);
        }
        return getRuleViolationMessage(ruleViolation);
    }

    private String getCanvasViolationMessage(final CanvasViolation canvasViolation) {
        return getRuleViolationMessage(canvasViolation.getRuleViolation());
    }
}
