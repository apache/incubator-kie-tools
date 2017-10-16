/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.client.handlers.workbench.configuration;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.client.mvp.UberView;

@ApplicationScoped
public class WorkbenchConfigurationPresenter {

    public interface WorkbenchConfigurationView extends UberView<WorkbenchConfigurationPresenter> {

        void show();

        void hide();

        void setActiveHandler(final WorkbenchConfigurationHandler activeHandler);

        void setTitle(String title);
    }

    @Inject
    private WorkbenchConfigurationView view;

    private WorkbenchConfigurationHandler activeHandler = null;

    @PostConstruct
    private void setup() {
        view.init(this);
    }

    public void show(final WorkbenchConfigurationHandler handler) {
        activeHandler = PortablePreconditions.checkNotNull("handler",
                                                           handler);

        activeHandler.initHandler();
        view.setActiveHandler(activeHandler);
        view.show();
        view.setTitle(getActiveHandlerDescription());
    }

    public void complete() {
        view.hide();
    }

    private String getActiveHandlerDescription() {
        if (activeHandler != null) {
            return activeHandler.getDescription();
        } else {
            return "";
        }
    }
}
