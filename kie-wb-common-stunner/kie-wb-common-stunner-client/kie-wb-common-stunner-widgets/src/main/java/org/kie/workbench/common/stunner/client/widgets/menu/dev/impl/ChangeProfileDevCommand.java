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
package org.kie.workbench.common.stunner.client.widgets.menu.dev.impl;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.AbstractMenuDevCommand;
import org.kie.workbench.common.stunner.client.widgets.profile.ProfileSelector;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.uberfire.client.views.pfly.modal.Bs3Modal;

@Dependent
public class ChangeProfileDevCommand extends AbstractMenuDevCommand {

    private final Instance<Bs3Modal> modalFactory;
    private final ProfileSelector profileSelector;

    protected ChangeProfileDevCommand() {
        this(null, null, null);
    }

    @Inject
    public ChangeProfileDevCommand(final SessionManager sessionManager,
                                   final Instance<Bs3Modal> modalFactory,
                                   final ProfileSelector profileSelector) {
        super(sessionManager);
        this.modalFactory = modalFactory;
        this.profileSelector = profileSelector;
    }

    @Override
    public String getText() {
        return "Change Profile";
    }

    @Override
    public void execute() {
        profileSelector.bind(this::getSession);
        showModal();
    }

    private void showModal() {
        final HTMLElement selectorView = profileSelector.getView().getElement();
        final Bs3Modal modal = modalFactory.get();
        modal.setFooterContent(new FlowPanel());
        modal.setModalTitle("Choose profile");
        modal.setContent(ElementWrapperWidget.getWidget(selectorView));
        modal.show();
    }
}
