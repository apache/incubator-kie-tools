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
package org.kie.workbench.common.stunner.kogito.client.docks;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.stunner.kogito.client.resources.i18n.KogitoClientMessages;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorPropertiesScreen;
import org.uberfire.client.workbench.docks.UberfireDocks;

@ApplicationScoped
public class DiagramEditorPropertiesDock extends BaseDiagramEditorDock {

    public DiagramEditorPropertiesDock() {
        // CDI proxy
        this(null, null);
    }

    @Inject
    public DiagramEditorPropertiesDock(final UberfireDocks uberfireDocks,
                                       final TranslationService translationService) {
        super(uberfireDocks, translationService);
    }

    @Override
    protected final String icon() {
        return IconType.PENCIL_SQUARE_O.toString();
    }

    @Override
    protected final String getScreenId() {
        return DiagramEditorPropertiesScreen.SCREEN_ID;
    }

    @Override
    protected final String getLabelKey() {
        return KogitoClientMessages.PROPERTIES_DOCK_TITLE;
    }
}
