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

package org.kie.workbench.common.forms.editor.client.editor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.ext.layout.editor.client.LayoutEditorPropertiesScreen;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
public class FormFieldPropertiesEditorDock {

    private static final double DOCK_SIZE = 300d;

    private UberfireDocks uberfireDocks;

    private TranslationService translationService;

    private UberfireDock uberfireDock;

    private boolean isOpened = false;

    private String perspective;
    
    
    
    public FormFieldPropertiesEditorDock() {
    }
    
    @Inject
    public FormFieldPropertiesEditorDock(UberfireDocks uberfireDocks, 
                                         TranslationService translationService) {
        this.uberfireDocks = uberfireDocks;
        this.translationService = translationService;
    }


    public void init(final String perspective) {
        this.perspective = perspective;
        this.uberfireDock = makeUberfireDock();
    }


    public void open() {

        if (isOpened()) {
            return;
        }

        isOpened = true;
        uberfireDocks.add(getUberfireDock());
        uberfireDocks.show(position(), perspective());
        uberfireDocks.open(getUberfireDock());
    }

    public void close() {

        if (!isOpened()) {
            return;
        }

        isOpened = false;
        uberfireDocks.close(getUberfireDock());
        uberfireDocks.remove(getUberfireDock());
    }

    boolean isOpened() {
        return isOpened;
    }

    void setOpened(final boolean opened) {
        isOpened = opened;
    }

    UberfireDock makeUberfireDock() {

        final UberfireDock uberfireDock = new UberfireDock(position(), icon(), placeRequest(), perspective());

        return uberfireDock.withSize(DOCK_SIZE).withLabel(dockLabel());
    }

    String perspective() {
        return perspective;
    }

    UberfireDock getUberfireDock() {
        return uberfireDock;
    }

    UberfireDockPosition position() {
        return UberfireDockPosition.EAST;
    }

    private String icon() {
        return IconType.PENCIL.toString();
    }

    private String dockLabel() {
        return translationService.format(FormEditorConstants.FielPropertiesEditor);
    }

    private DefaultPlaceRequest placeRequest() {
        return new DefaultPlaceRequest(LayoutEditorPropertiesScreen.SCREEN_ID);
    }
}