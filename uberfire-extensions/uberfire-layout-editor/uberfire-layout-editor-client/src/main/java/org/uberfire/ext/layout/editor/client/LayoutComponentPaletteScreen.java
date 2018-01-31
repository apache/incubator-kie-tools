/*
 * Copyright 2016 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.layout.editor.client;

import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.layout.editor.client.resources.i18n.CommonConstants;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPalettePresenter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@WorkbenchScreen(identifier = LayoutComponentPaletteScreen.SCREEN_ID)
public class LayoutComponentPaletteScreen {

    public static final String SCREEN_ID = "LavoutComponentPaletteScreen";

    private LayoutComponentPalettePresenter componentPalette;

    public LayoutComponentPaletteScreen() {
    }

    @Inject
    public LayoutComponentPaletteScreen(LayoutComponentPalettePresenter componentPalette) {
        this.componentPalette = componentPalette;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return CommonConstants.INSTANCE.Components();
    }

    @WorkbenchPartView
    public IsElement getView() {
        return componentPalette.getView();
    }
}