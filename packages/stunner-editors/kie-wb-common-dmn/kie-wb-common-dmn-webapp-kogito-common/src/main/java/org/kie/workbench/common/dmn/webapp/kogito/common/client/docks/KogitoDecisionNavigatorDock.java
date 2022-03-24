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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.docks;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.appformer.client.context.Channel;
import org.appformer.client.context.EditorContextProvider;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;

@Specializes
@ApplicationScoped
public class KogitoDecisionNavigatorDock extends DecisionNavigatorDock {

    private EditorContextProvider context;

    public KogitoDecisionNavigatorDock() {
        // CDI proxy
    }

    @Inject
    public KogitoDecisionNavigatorDock(final UberfireDocks uberfireDocks,
                                       final DecisionNavigatorPresenter decisionNavigatorPresenter,
                                       final TranslationService translationService,
                                       final EditorContextProvider context) {
        super(uberfireDocks,
              decisionNavigatorPresenter,
              translationService);
        this.context = context;
    }

    @Override
    public void init() {
        this.uberfireDock = makeUberfireDock();

        uberfireDocks.add(getUberfireDock());
        uberfireDocks.show(position());
    }

    @Override
    public void open() {
        if (isOpened()) {
            return;
        }

        isOpened = true;
        uberfireDocks.open(getUberfireDock());
    }

    @Override
    public void close() {
        if (!isOpened()) {
            return;
        }

        isOpened = false;
        uberfireDocks.close(getUberfireDock());
    }

    @Override
    protected UberfireDockPosition position() {
        return getUberfireDockPosition();
    }

    UberfireDockPosition getUberfireDockPosition() {
        final Channel channel = context.getChannel();
        switch (channel) {
            case GITHUB:
            case VSCODE_DESKTOP:
            case VSCODE_WEB:
            case ONLINE_MULTI_FILE:
                return UberfireDockPosition.EAST;
            default:
                return UberfireDockPosition.WEST;
        }
    }
}
