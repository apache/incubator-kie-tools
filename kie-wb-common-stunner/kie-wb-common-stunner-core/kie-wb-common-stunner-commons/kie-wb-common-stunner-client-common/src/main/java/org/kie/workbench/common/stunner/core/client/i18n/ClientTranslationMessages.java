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

package org.kie.workbench.common.stunner.core.client.i18n;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.ModelBeanViolation;

@ApplicationScoped
public class ClientTranslationMessages extends CoreTranslationMessages {

    private final StunnerTranslationService translationService;

    @Inject
    public ClientTranslationMessages(final StunnerTranslationService translationService) {
        this.translationService = translationService;
    }

    public String getDiagramValidationsErrorMessage(final String key,
                                                    final Collection<DiagramElementViolation<RuleViolation>> result) {
        return getDiagramValidationsErrorMessage(translationService,
                                                 key,
                                                 result);
    }

    public String getCanvasValidationsErrorMessage(final String key,
                                                   final Iterable<CanvasViolation> result) {
        return getCanvasValidationsErrorMessage(translationService,
                                                key,
                                                result);
    }

    public String getCanvasCommandValidationsErrorMessage(final Iterable<CanvasViolation> result) {
        return getCanvasCommandValidationsErrorMessage(translationService,
                                                       result);
    }

    public String getRuleValidationMessage(final RuleViolation violation) {
        return getRuleValidationMessage(translationService,
                                        violation);
    }

    public String getBeanValidationMessage(final ModelBeanViolation violation) {
        return getBeanValidationMessage(translationService,
                                        violation);
    }

    public String getDomainValidationMessage(final DomainViolation violation) {
        return getDomainValidationMessage(translationService,
                                          violation);
    }
}
