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

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Document;
import elemental2.dom.Element;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLUListElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ssh.client.editor.component.keys.key.SSHKeyEditor;

@Templated
public class SSHKeysDisplayerViewImpl implements SSHKeysDisplayerView,
                                                 IsElement {

    @Inject
    @DataField
    private HTMLButtonElement add;

    @Inject
    @DataField
    private HTMLUListElement keysContainer;

    @Inject
    private Document document;

    @Inject
    private Elemental2DomUtil util;

    private Presenter presenter;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clear() {
        util.removeAllElementChildren(keysContainer);
    }

    @Override
    public void add(SSHKeyEditor editor) {
        Element li = document.createElement("li");

        li.className = "list-group-item";

        li.appendChild(editor.getElement());

        keysContainer.appendChild(li);
    }

    @EventHandler("add")
    public void onAdd(ClickEvent event) {
        presenter.notifyAdd();
    }
}
