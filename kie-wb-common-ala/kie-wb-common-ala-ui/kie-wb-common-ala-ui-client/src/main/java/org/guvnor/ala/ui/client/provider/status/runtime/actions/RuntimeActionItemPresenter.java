/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.provider.status.runtime.actions;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

@Dependent
public class RuntimeActionItemPresenter {

    public interface View
            extends UberElement<RuntimeActionItemPresenter> {

        void setLabel(final String label);

        void setEnabled(final boolean enabled);
    }

    private final View view;

    private Command command;

    @Inject
    public RuntimeActionItemPresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setup(final String label,
                      final Command command) {
        view.setLabel(label);
        this.command = command;
    }

    public void setEnabled(final boolean enabled) {
        view.setEnabled(enabled);
    }

    public View getView() {
        return view;
    }

    protected void onActionClick() {
        if (command != null) {
            command.execute();
        }
    }
}
