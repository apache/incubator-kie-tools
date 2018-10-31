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

package org.uberfire.ssh.client.editor.component.empty;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;

@Dependent
public class SSHKeysEditorEmptyStateDisplayer implements SSHKeysEditorEmptyStateDisplayerView.Presenter,
                                                         IsElement {

    private final SSHKeysEditorEmptyStateDisplayerView view;

    private Command addCommand;

    @Inject
    public SSHKeysEditorEmptyStateDisplayer(final SSHKeysEditorEmptyStateDisplayerView view) {
        this.view = view;
        view.init(this);
    }

    public void init(Command addCommand) {

        PortablePreconditions.checkNotNull("addCommand", addCommand);

        this.addCommand = addCommand;
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public void notifyAdd() {
        if (addCommand != null) {
            addCommand.execute();
        }
    }
}
