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
package org.drools.workbench.screens.scenariosimulation.client.commands;

import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelView;
import org.uberfire.mvp.Command;

/**
 * <code>Command</code> to <b>enable</b> the <code>RightPanelView</code>
 */
@Dependent
public class EnableRightPanelCommand implements Command {

    private RightPanelView.Presenter rightPanelPresenter;

    /**
     * The string to use for filtering in right panel
     */
    private final String filterTerm;

    /**
     * flag to decide which kind of filter (<b>equals</b> or <b>not euals</b>) is to be applied.
     * Default to false (= <b>equals</b> filter)
     */
    private final boolean notEqualsSearch;

    /**
     * The string to <b>eventually</b> use to select the property in the right panel
     */
    private final String propertyName;


    public EnableRightPanelCommand() {
        this.filterTerm = null;
        notEqualsSearch = false;
        propertyName = null;
    }

    /**
     * Execute this command to show all the first-level data models <b>enabled</b> (i.e. <b>selectable</b> to map to an <i>instance</i> header/column)
     * and their properties <b>disabled</b> (i.e. <b>not double-clickable</b>)
     * @param rightPanelPresenter
     */
    public EnableRightPanelCommand(RightPanelView.Presenter rightPanelPresenter) {
        this.rightPanelPresenter = rightPanelPresenter;
        this.filterTerm = null;
        notEqualsSearch = false;
        propertyName = null;
    }

    /**
     * Execute this command to to show only the data model with the given name, <b>disabled</b> (i.e. <b>not selectable</b>)
     * and their properties <b>enabled</b> (i.e. <b>selectable</b> to map to a <i>property</i> header/column below the belonging data model instance one)
     *
     * @param rightPanelPresenter
     * @param filterTerm the term used to filter the right panel ()relates to instance name)
     * @param propertyName the string to <b>eventually</b> use to select the property in the right panel
     * @param notEqualsSearch
     */
    public EnableRightPanelCommand(RightPanelView.Presenter rightPanelPresenter, String filterTerm, String propertyName, boolean notEqualsSearch) {
        this.rightPanelPresenter = rightPanelPresenter;
        this.filterTerm = filterTerm;
        this.notEqualsSearch = notEqualsSearch;
        this.propertyName = propertyName;
    }

    @Override
    public void execute() {
        if (filterTerm == null) {
            rightPanelPresenter.onEnableEditorTab();
        } else {
            rightPanelPresenter.onEnableEditorTab(filterTerm, propertyName, notEqualsSearch);
        }
    }
}
