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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.widgets.client.docks.AbstractWorkbenchDocksHandler;
import org.kie.workbench.common.workbench.client.events.LayoutEditorFocusEvent;
import org.kie.workbench.common.workbench.client.events.LayoutEditorLostFocusEvent;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.ext.layout.editor.client.LayoutEditorPropertiesScreen;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
public class FormEditorDocksHandler extends AbstractWorkbenchDocksHandler {
    
    @Inject
    private TranslationService translationService;

    @Override
    public Collection<UberfireDock> provideDocks(String perspectiveIdentifier) {
        List<UberfireDock> result = new ArrayList<>();
        
        UberfireDock dock = new UberfireDock(UberfireDockPosition.EAST, IconType.PENCIL.toString(),
                                            new DefaultPlaceRequest(LayoutEditorPropertiesScreen.SCREEN_ID), perspectiveIdentifier)
                                            .withSize(300).withLabel(translationService.format(FormEditorConstants.FielPropertiesEditor));
        result.add(dock);
        
        return result;
    }
    
    public void onLayoutEditorFocus(@Observes LayoutEditorFocusEvent event) {
        if (FormEditorPresenter.ID.equals(event.getEditorId())) {
            refreshDocks(true, false);
        }
    }
    
    public void onLayoutEditorLostFocus(@Observes LayoutEditorLostFocusEvent event) {
        if (FormEditorPresenter.ID.equals(event.getEditorId())) {
            refreshDocks(true, true);
        }
    }

}
