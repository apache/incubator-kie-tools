/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoTextTooltip;
import org.kie.workbench.common.stunner.client.lienzo.util.LirnzoSvgPaths;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.AbstractToolboxControlProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.Context;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.ToolboxCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.ToolboxCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.actions.AbstractActionToolboxCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.actions.RemoveToolboxCommand;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxButtonGrid;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxBuilder;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxButtonGridBuilder;
import org.kie.workbench.common.stunner.core.client.components.views.CanvasTooltip;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

/**
 * A DMN-specific implementation of {@see FlowActionsToolboxControlProvider}
 * that provides specific behaviour for DMN DefinitionSets.
 */
@Dependent
public class DMNActionsToolboxControlProvider extends AbstractToolboxControlProvider {

    private final DefinitionManager definitionManager;

    private final Set<String> dmnDefinitionIds = new HashSet<>();

    private final RemoveToolboxCommand removeToolboxCommand;

    private final Event<EditExpressionEvent> event;

    private final SessionManager sessionManager;

    protected DMNActionsToolboxControlProvider() {
        this(null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public DMNActionsToolboxControlProvider(final ToolboxFactory toolboxFactory,
                                            final ToolboxCommandFactory toolboxCommandFactory,
                                            final DefinitionManager definitionManager,
                                            final SessionManager sessionManager,
                                            final Event<EditExpressionEvent> event) {
        super(toolboxFactory);

        this.removeToolboxCommand = toolboxCommandFactory.newRemoveToolboxCommand();
        this.definitionManager = definitionManager;
        this.sessionManager = sessionManager;
        this.event = event;

        final DMNDefinitionSet definitionSet = (DMNDefinitionSet) definitionManager.definitionSets().getDefinitionSetByType(DMNDefinitionSet.class);
        this.dmnDefinitionIds.addAll(definitionManager.adapters().forDefinitionSet().getDefinitions(definitionSet));
    }

    @Override
    public boolean supports(final Object definition) {
        final String definitionId = definitionManager.adapters().forDefinition().getId(definition);
        return dmnDefinitionIds.contains(definitionId);
    }

    @Override
    public ToolboxButtonGrid getGrid(final AbstractCanvasHandler context,
                                     final Element item) {
        final ToolboxButtonGridBuilder buttonGridBuilder = toolboxFactory.toolboxGridBuilder();
        return buttonGridBuilder
                .setRows(3)
                .setColumns(1)
                .setIconSize(DEFAULT_ICON_SIZE)
                .setPadding(DEFAULT_PADDING)
                .build();
    }

    @Override
    public ToolboxBuilder.Direction getOn() {
        return ToolboxBuilder.Direction.NORTH_WEST;
    }

    @Override
    public ToolboxBuilder.Direction getTowards() {
        return ToolboxBuilder.Direction.SOUTH_WEST;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ToolboxCommand<AbstractCanvasHandler, ?>> getCommands(final AbstractCanvasHandler context,
                                                                      final Element item) {
        final List<ToolboxCommand<AbstractCanvasHandler, ?>> commands = new LinkedList<>();
        commands.add(removeToolboxCommand);
        final Node<Definition<Object>, Edge> node = (Node<Definition<Object>, Edge>) item;
        if (node.getContent().getDefinition() instanceof Decision) {
            final AbstractActionToolboxCommand editCommand = new EditDecisionToolboxCommand<>(new LienzoTextTooltip(),
                                                                                              sessionManager.getCurrentSession(),
                                                                                              event);
            editCommand.setIcon(LirnzoSvgPaths.createSVGIcon(LirnzoSvgPaths.getGearIcon()));
            commands.add(editCommand);
        }
        return commands;
    }

    private static class EditDecisionToolboxCommand<I> extends AbstractActionToolboxCommand<I> {

        private final ClientSession session;
        private final Event<EditExpressionEvent> event;
        private final CanvasTooltip<String> canvasTextTooltip;

        protected EditDecisionToolboxCommand() {
            this(null,
                 null,
                 null);
        }

        public EditDecisionToolboxCommand(final CanvasTooltip<String> canvasTextTooltip,
                                          final ClientSession session,
                                          final Event<EditExpressionEvent> event) {
            super(canvasTextTooltip);
            this.session = session;
            this.event = event;
            this.canvasTextTooltip = canvasTextTooltip;
        }

        @Override
        public String getTitle() {
            return "Edit";
        }

        @Override
        public void click(final Context<AbstractCanvasHandler> context,
                          final Element element) {
            super.click(context,
                        element);
            canvasTextTooltip.hide();

            final View content = (View) element.getContent();
            final Decision decision = (Decision) content.getDefinition();
            event.fire(new EditExpressionEvent(session,
                                               Optional.of(decision),
                                               decision));
        }

        @Override
        public void destroy() {
        }
    }
}
