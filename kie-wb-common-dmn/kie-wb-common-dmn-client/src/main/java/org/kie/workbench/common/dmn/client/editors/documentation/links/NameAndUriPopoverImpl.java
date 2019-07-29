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

package org.kie.workbench.common.dmn.client.editors.documentation.links;

import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink;

@ApplicationScoped
public class NameAndUriPopoverImpl implements NameAndUrlPopoverView.Presenter {

    private NameAndUrlPopoverView view;

    public NameAndUriPopoverImpl() {
        //CDI proxy
    }

    @Inject
    public NameAndUriPopoverImpl(final NameAndUrlPopoverView view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public void show(final Optional editorTitle) {
        view.show(editorTitle);
    }

    @Override
    public void hide() {
        view.hide();
    }

    @Override
    public void setOnExternalLinkCreated(final Consumer<DMNExternalLink> onExternalLinkCreated) {
        view.setOnExternalLinkCreated(onExternalLinkCreated);
    }
}
