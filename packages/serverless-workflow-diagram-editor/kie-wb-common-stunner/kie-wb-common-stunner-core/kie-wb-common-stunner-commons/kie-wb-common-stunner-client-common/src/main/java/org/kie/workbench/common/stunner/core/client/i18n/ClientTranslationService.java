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


package org.kie.workbench.common.stunner.core.client.i18n;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.j2cl.tools.di.ui.translation.client.TranslationService;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.i18n.AbstractTranslationService;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.core.validation.DiagramElementNameProvider;

@Singleton
public class ClientTranslationService extends AbstractTranslationService {

    private final TranslationService erraiTranslationService;
    private final ManagedInstance<DiagramElementNameProvider> elementNameProviders;
    private SessionManager sessionManager;
    private DefinitionUtils definitionUtils;

    @Inject
    public ClientTranslationService(final TranslationService erraiTranslationService,
                                    final ManagedInstance<DiagramElementNameProvider> elementNameProviders,
                                    final SessionManager sessionManager,
                                    final DefinitionUtils definitionUtils) {
        this.erraiTranslationService = erraiTranslationService;
        this.elementNameProviders = elementNameProviders;
        this.sessionManager = sessionManager;
        this.definitionUtils = definitionUtils;
    }

    @Override
    public String getValue(final String key) {
        return erraiTranslationService.getTranslation(key);
    }

    public String getNotNullValue(final String key) {
        final String value = getValue(key);
        if (null == value) {
            return key;
        }
        return value;
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
        return super.getViolationMessage(ruleViolation);
    }

    private String getCanvasViolationMessage(final CanvasViolation canvasViolation) {
        return getRuleViolationMessage(canvasViolation.getRuleViolation());
    }

    @Override
    public Optional<String> getElementName(final String uuid) {
        return getNameProvider(uuid)
                .map(nameProvider -> nameProvider.getElementName(uuid))
                .orElse(Optional.of(defaultElementName(uuid)));
    }

    private String defaultElementName(String uuid) {
        final Node<? extends Definition, ?> node = sessionManager.getCurrentSession()
                .getCanvasHandler()
                .getDiagram()
                .getGraph()
                .getNode(uuid);
        Optional<Object> definition = Optional.ofNullable(node)
                .map(Node::getContent)
                .map(Definition::getDefinition);

        return definition
                .map(definitionUtils::getName)
                .filter(StringUtils::nonEmpty)
                .orElse(uuid);
    }

    private Optional<DiagramElementNameProvider> getNameProvider(final String uuid) {
        return StreamSupport
                .stream(elementNameProviders.spliterator(), false)
                .filter(elementNameProvider -> Objects.equals(elementNameProvider.getDefinitionSetId(), getSessionDefinitionSetId()))
                .findFirst();
    }

    private String getSessionDefinitionSetId() {
        return sessionManager.getCurrentSession().getCanvasHandler().getDiagram().getMetadata().getDefinitionSetId();
    }
}
