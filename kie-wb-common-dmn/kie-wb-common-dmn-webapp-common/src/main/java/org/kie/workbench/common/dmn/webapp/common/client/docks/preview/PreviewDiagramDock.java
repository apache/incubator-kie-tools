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
package org.kie.workbench.common.dmn.webapp.common.client.docks.preview;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreen;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.stunner.kogito.api.docks.DiagramEditorDock;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
public class PreviewDiagramDock implements DiagramEditorDock {

    protected static final double DOCK_SIZE = 400d;

    protected UberfireDocks uberfireDocks;

    protected TranslationService translationService;

    protected UberfireDock uberfireDock;

    protected boolean isOpened = false;

    protected String owningPerspectiveId;

    public PreviewDiagramDock() {
        // CDI proxy
    }

    @Inject
    public PreviewDiagramDock(final UberfireDocks uberfireDocks,
                              final TranslationService translationService) {
        this.uberfireDocks = uberfireDocks;
        this.translationService = translationService;
    }

    @Override
    public void init(final String owningPerspectiveId) {
        this.owningPerspectiveId = owningPerspectiveId;
        this.uberfireDock = makeUberfireDock();
    }

    @Override
    public void destroy() {
        uberfireDocks.remove(getUberfireDock());
    }

    @Override
    public void open() {
        if (isOpened()) {
            return;
        }

        isOpened = true;
        uberfireDocks.add(getUberfireDock());
        uberfireDocks.show(position(), owningPerspectiveId());
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

    protected UberfireDock makeUberfireDock() {
        final UberfireDock uberfireDock = new UberfireDock(position(), icon(), placeRequest(), owningPerspectiveId());
        return uberfireDock.withSize(DOCK_SIZE).withLabel(dockLabel());
    }

    protected UberfireDockPosition position() {
        return UberfireDockPosition.EAST;
    }

    protected String icon() {
        return IconType.EYE.toString();
    }

    protected DefaultPlaceRequest placeRequest() {
        return new DefaultPlaceRequest(PreviewDiagramScreen.SCREEN_ID);
    }

    protected String owningPerspectiveId() {
        return owningPerspectiveId;
    }

    protected UberfireDock getUberfireDock() {
        return uberfireDock;
    }

    protected String dockLabel() {
        return translationService.getTranslation(DMNEditorConstants.DMNPreviewDiagramDock_Title);
    }
}
