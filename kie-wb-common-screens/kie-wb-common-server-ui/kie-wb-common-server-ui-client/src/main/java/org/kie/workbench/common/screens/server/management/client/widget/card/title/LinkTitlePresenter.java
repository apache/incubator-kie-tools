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

package org.kie.workbench.common.screens.server.management.client.widget.card.title;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

@Dependent
public class LinkTitlePresenter implements TitlePresenter {

    public interface View extends UberView<LinkTitlePresenter> {

        void setText(final String value);
    }

    private final View view;
    private Command command;

    @Inject
    public LinkTitlePresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        this.view.init(this);
    }

    @Override
    public View getView() {
        return view;
    }

    public void setup(final String title,
                      final Command command) {
        this.command = checkNotNull("command",
                                    command);
        view.setText(title);
    }

    public void onSelect() {
        command.execute();
    }
}
