/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.rule.client.editor.plugin;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.widget.RuleModellerWidget;
import org.uberfire.mvp.Command;

public interface RuleModellerActionPlugin {

    /**
     * Check whether this plugin supports the action
     */
    boolean accept(final IAction iAction);

    /**
     * Create empty IAction this plugin supports
     */
    IAction createIAction(final RuleModeller ruleModeller);

    /**
     * Unique identifier of this plugin
     */
    String getId();

    /**
     * Get the text that gets displayed in RHS action selector
     */
    String getActionAddDescription();

    /**
     * Create a Widget wrapping corresponding IAction
     */
    RuleModellerWidget createWidget(final RuleModeller ruleModeller,
                                    final EventBus eventBus,
                                    final IAction iAction,
                                    final Boolean readOnly);

    /**
     * Add plugin to action list asynchronously
     */
    void addPluginToActionList(final RuleModeller ruleModeller,
                               final Command addCommand);
}
