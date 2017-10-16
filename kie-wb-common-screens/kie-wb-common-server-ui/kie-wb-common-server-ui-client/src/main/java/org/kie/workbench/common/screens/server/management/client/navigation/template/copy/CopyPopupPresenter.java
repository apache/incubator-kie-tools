/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.client.navigation.template.copy;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.ParameterizedCommand;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

@ApplicationScoped
public class CopyPopupPresenter {

    public interface View extends UberView<CopyPopupPresenter> {

        void clear();

        void display();

        String getNewTemplateName();

        void errorOnTemplateNameFromGroup();

        void hide();

        void errorOnTemplateNameFromGroup(String message);
    }

    private final View view;

    private ParameterizedCommand<String> command;

    @Inject
    public CopyPopupPresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    public void copy(final ParameterizedCommand<String> command) {
        this.command = checkNotNull("command",
                                    command);
        view.clear();
        view.display();
    }

    public void errorDuringProcessing(final String message) {
        view.errorOnTemplateNameFromGroup(message);
    }

    public void save() {
        if (view.getNewTemplateName().trim().isEmpty()) {
            view.errorOnTemplateNameFromGroup();
            return;
        }
        command.execute(view.getNewTemplateName());
    }

    public void hide() {
        view.hide();
    }
}
