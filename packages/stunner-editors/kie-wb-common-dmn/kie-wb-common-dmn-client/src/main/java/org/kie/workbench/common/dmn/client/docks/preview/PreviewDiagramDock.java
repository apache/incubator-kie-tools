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
package org.kie.workbench.common.dmn.client.docks.preview;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.stunner.client.widgets.editor.DiagramEditorDock;
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
    public void init() {
        if (uberfireDock == null) {
            uberfireDock = makeUberfireDock();
            uberfireDocks.add(getUberfireDock());
            uberfireDocks.show(position());
        }
    }

    @Override
    public void destroy() {
        if (uberfireDock != null) {
            uberfireDocks.remove(getUberfireDock());
            uberfireDock = null;
        }
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

    protected boolean isOpened() {
        return isOpened;
    }

    protected UberfireDock makeUberfireDock() {
        final UberfireDock uberfireDock = new UberfireDock(position(), icon(), placeRequest());
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

    protected UberfireDock getUberfireDock() {
        return uberfireDock;
    }

    protected String dockLabel() {
        return translationService.getTranslation(DMNEditorConstants.DMNPreviewDiagramDock_Title);
    }
}
