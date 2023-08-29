/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.drools.workbench.screens.scenariosimulation.client.editor.menu;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class BaseMenuViewImpl<M extends BaseMenu> extends Composite implements BaseMenuView<M> {

    protected M presenter;

    @DataField("contextMenuDropdown")
    private UListElement contextMenuDropdown = Document.get().createULElement();

    @Override
    public void init(final M presenter) {
        this.presenter = presenter;
    }

    @Override
    public UListElement getContextMenuDropdown() {
        return contextMenuDropdown;
    }

    @EventHandler()
    public void onContextMenuEvent(ContextMenuEvent event) {
        presenter.onContextMenuEvent(event);
    }

}
