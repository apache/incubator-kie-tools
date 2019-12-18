/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.kogito.webapp.base.client.perspectives;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;

/**
 * This class is meant to be *extended* by using modules (Kogito' showcases) to provide custom values
 */
@ApplicationScoped
public class PerspectiveConfiguration {

    private Class<? extends AbstractWorkbenchPanelPresenter> perspectivePanelType = StaticWorkbenchPanelPresenter.class;

    /**
     * This is the <code>AbstractWorkbenchPanelPresenter</code> that will be used inside the <code>PerspectiveDefinition</code>
     * @return
     */
    public Class<? extends AbstractWorkbenchPanelPresenter> getPerspectivePanelType() {
        return perspectivePanelType;
    }
}
