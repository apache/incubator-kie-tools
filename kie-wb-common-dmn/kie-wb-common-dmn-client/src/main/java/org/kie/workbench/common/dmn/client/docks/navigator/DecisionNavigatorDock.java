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

package org.kie.workbench.common.dmn.client.docks.navigator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.kogito.api.docks.DiagramEditorDock;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionNavigatorPresenter_DecisionNavigator;

@ApplicationScoped
public class DecisionNavigatorDock implements DiagramEditorDock {

    protected static final double DOCK_SIZE = 400d;

    protected UberfireDocks uberfireDocks;

    protected DecisionNavigatorPresenter decisionNavigatorPresenter;

    protected TranslationService translationService;

    protected UberfireDock uberfireDock;

    protected boolean isOpened = false;

    protected String perspective;

    public DecisionNavigatorDock() {
        // CDI proxy
    }

    @Inject
    public DecisionNavigatorDock(final UberfireDocks uberfireDocks,
                                 final DecisionNavigatorPresenter decisionNavigatorPresenter,
                                 final TranslationService translationService) {
        this.uberfireDocks = uberfireDocks;
        this.decisionNavigatorPresenter = decisionNavigatorPresenter;
        this.translationService = translationService;
    }

    @Override
    public void init(final String perspective) {
        this.perspective = perspective;
        this.uberfireDock = makeUberfireDock();
    }

    @Override
    public void destroy() {
        uberfireDocks.remove(getUberfireDock());
    }

    public void setupCanvasHandler(final CanvasHandler handler) {
        decisionNavigatorPresenter.setHandler(handler);
    }

    public void resetContent() {
        decisionNavigatorPresenter.removeAllElements();
    }

    @Override
    public void open() {

        if (isOpened()) {
            return;
        }

        isOpened = true;
        uberfireDocks.add(getUberfireDock());
        uberfireDocks.show(position(), perspective());
        uberfireDocks.open(getUberfireDock());
    }

    @Override
    public void close() {

        if (!isOpened()) {
            return;
        }

        isOpened = false;
        uberfireDocks.close(getUberfireDock());
        destroy();
    }

    protected boolean isOpened() {
        return isOpened;
    }

    protected void setOpened(final boolean opened) {
        isOpened = opened;
    }

    protected UberfireDock makeUberfireDock() {

        final UberfireDock uberfireDock = new UberfireDock(position(), icon(), placeRequest(), perspective());

        return uberfireDock.withSize(DOCK_SIZE).withLabel(dockLabel());
    }

    protected String perspective() {
        return perspective;
    }

    protected UberfireDock getUberfireDock() {
        return uberfireDock;
    }

    protected UberfireDockPosition position() {
        return UberfireDockPosition.WEST;
    }

    protected String icon() {
        return IconType.MAP.toString();
    }

    protected String dockLabel() {
        return translationService.format(DecisionNavigatorPresenter_DecisionNavigator);
    }

    protected DefaultPlaceRequest placeRequest() {
        return new DefaultPlaceRequest(DecisionNavigatorPresenter.IDENTIFIER);
    }
}
