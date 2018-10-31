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

package org.uberfire.ssh.client.editor.component.keys;

import java.util.Collection;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.ssh.client.editor.component.keys.key.SSHKeyEditor;
import org.uberfire.ssh.service.shared.editor.PortableSSHPublicKey;

@Dependent
public class SSHKeysDisplayer implements SSHKeysDisplayerView.Presenter,
                                         IsElement {

    private final SSHKeysDisplayerView view;
    private final ManagedInstance<SSHKeyEditor> editors;

    private Command addCommand;
    private ParameterizedCommand<PortableSSHPublicKey> deleteCommand;

    @Inject
    public SSHKeysDisplayer(final SSHKeysDisplayerView view, final ManagedInstance<SSHKeyEditor> editors) {
        this.view = view;
        this.editors = editors;
        view.init(this);
    }

    public void init(final Command addCommand, final ParameterizedCommand<PortableSSHPublicKey> deleteCommand) {

        PortablePreconditions.checkNotNull("addCommand", addCommand);
        PortablePreconditions.checkNotNull("deleteCommand", deleteCommand);

        this.addCommand = addCommand;
        this.deleteCommand = deleteCommand;
    }

    public void render(Collection<PortableSSHPublicKey> keys) {
        clear();

        keys.forEach(this::addKeyEditor);
    }

    private void addKeyEditor(final PortableSSHPublicKey key) {
        final SSHKeyEditor editor = editors.get();
        editor.render(key, () -> onDelete(key));
        view.add(editor);
    }

    protected void onDelete(final PortableSSHPublicKey key) {
        deleteCommand.execute(key);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @PreDestroy
    public void clear() {
        view.clear();
        editors.destroyAll();
    }

    @Override
    public void notifyAdd() {
        if (addCommand != null) {
            addCommand.execute();
        }
    }
}
