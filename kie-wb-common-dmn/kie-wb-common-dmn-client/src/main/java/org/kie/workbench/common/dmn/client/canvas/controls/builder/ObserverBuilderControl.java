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

package org.kie.workbench.common.dmn.client.canvas.controls.builder;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNElement;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationMessages;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.util.StringUtils;

@DMNEditor
@Dependent
@Observer
public class ObserverBuilderControl extends org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.ObserverBuilderControl {

    @Inject
    public ObserverBuilderControl(final ClientDefinitionManager clientDefinitionManager,
                                  final ClientFactoryService clientFactoryServices,
                                  final RuleManager ruleManager,
                                  final @DMNEditor DefaultCanvasCommandFactory canvasCommandFactory,
                                  final ClientTranslationMessages translationMessages,
                                  final GraphBoundsIndexer graphBoundsIndexer,
                                  final Event<CanvasSelectionEvent> canvasSelectionEvent) {
        super(clientDefinitionManager,
              clientFactoryServices,
              ruleManager,
              canvasCommandFactory,
              translationMessages,
              graphBoundsIndexer,
              canvasSelectionEvent);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void updateElementFromDefinition(final Element element,
                                               final Object definition) {

        final Object content = element.getContent();
        if (!(content instanceof View)) {
            return;
        }

        final Object newDefinition = ((View) content).getDefinition();
        if (newDefinition instanceof HasName && definition instanceof HasName) {
            ((HasName) newDefinition).getName().setValue(((HasName) definition).getName().getValue());
        }

        if (newDefinition instanceof DynamicReadOnly && definition instanceof DynamicReadOnly) {
            ((DynamicReadOnly) newDefinition).setAllowOnlyVisualChange(((DynamicReadOnly) definition).isAllowOnlyVisualChange());
        }

        if (newDefinition instanceof HasVariable && definition instanceof HasVariable) {
            ((HasVariable) newDefinition).setVariable(((HasVariable) definition).getVariable());
        }

        if (newDefinition instanceof HasExpression && definition instanceof HasExpression) {
            ((HasExpression) newDefinition).setExpression(((HasExpression) definition).getExpression());
        }

        if (newDefinition instanceof DMNElement && definition instanceof DMNElement) {
            final DMNElement dmnElement = (DMNElement) definition;
            if (!StringUtils.isEmpty(dmnElement.getId().getValue())) {
                ((DMNElement) newDefinition).getId().setValue(dmnElement.getId().getValue());
            }
        }
    }
}