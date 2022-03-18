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
package org.drools.workbench.screens.scenariosimulation.client.editor.menu;

/**
 * This is the first <i>ScenaraioSimulation</i> specific interface - i.e. it is bound to a specific use case. Not every implementation
 * would need this. Menu initialization may be done in other different ways
 */
public interface HeaderMenuPresenter extends BaseMenuView.BaseMenuPresenter {

    /**
     * This method is required to initialze the menus
     */
    void initMenu();
    
}
