/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client;

/**
 * The idea is that Project Explorer swaps the "View" it is communicating with depending on whether the Business or Technical views
 * are selected. The Project Explorer's presenter performs the same actions no matter what "View" is selected by calling methods
 * defined on this interface. BusinessView + TechnicalView become redundant. Both implement this interface.
 */
public interface ExplorerPresenter {

    void selectBusinessView();

    void selectTechnicalView();

}
