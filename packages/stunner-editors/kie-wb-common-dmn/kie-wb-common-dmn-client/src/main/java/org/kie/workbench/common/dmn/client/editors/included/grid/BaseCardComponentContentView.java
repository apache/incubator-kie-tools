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

package org.kie.workbench.common.dmn.client.editors.included.grid;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLParagraphElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;

public abstract class BaseCardComponentContentView implements BaseCardComponent.ContentView {

    @DataField("path")
    private final HTMLParagraphElement path;

    @DataField("path-link")
    private final HTMLAnchorElement pathLink;

    @DataField("remove-button")
    private final HTMLButtonElement removeButton;

    private BaseCardComponent presenter;

    @Inject
    protected BaseCardComponentContentView(final HTMLParagraphElement path,
                                           final HTMLAnchorElement pathLink,
                                           final HTMLButtonElement removeButton) {
        this.path = path;
        this.pathLink = pathLink;
        this.removeButton = removeButton;
    }

    @Override
    public void init(final BaseCardComponent presenter) {
        this.presenter = presenter;
    }

    @EventHandler("path-link")
    public void onPathLinkClick(final ClickEvent e) {
        presenter.openPathLink();
    }

    @EventHandler("remove-button")
    public void onRemoveButtonClick(final ClickEvent e) {
        presenter.remove();
    }

    @Override
    public void setPath(final String path) {
        this.pathLink.style.display = "none";
        this.path.textContent = path;
    }

    @Override
    public void setPathLink(final String path) {
        this.path.style.display = "none";
        this.pathLink.textContent = path;
    }
}
