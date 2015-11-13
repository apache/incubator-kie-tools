/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.plugin.client.perspective.editor.generator;

import java.util.Collection;
import java.util.Collections;

import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

public class PerspectiveEditorActivity implements PerspectiveActivity {

    private LayoutTemplate editor;
    private PerspectiveEditorScreenActivity screen;
    private PlaceRequest place;

    private static final Collection<String> ROLES = Collections.emptyList();

    private static final Collection<String> TRAITS = Collections.emptyList();

    public PerspectiveEditorActivity(final LayoutTemplate editor,
            PerspectiveEditorScreenActivity screen) {
        this.editor = editor;
        this.screen = screen;
    }

    public void update(final LayoutTemplate editor,
                       PerspectiveEditorScreenActivity screen ){
        this.editor = editor;
        this.screen = screen;
    }

    @Override
    public PlaceRequest getPlace() {
        return place;
    }

    @Override
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        return buildPerspective();
    }

    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition perspectiveDefinition = new PerspectiveDefinitionImpl( getDefaultPanelType() );
        perspectiveDefinition.setName( editor.getName() );

        final PanelDefinition root = perspectiveDefinition.getRoot();

        final PlaceRequest placeRequest = new DefaultPlaceRequest( screen.getIdentifier() );
        final PartDefinition partDefinition = new PartDefinitionImpl( placeRequest );
        root.addPart( partDefinition );

        return perspectiveDefinition;
    }

    private String getDefaultPanelType() {
        return SimpleWorkbenchPanelPresenter.class.getName();
    }

    @Override
    public String getIdentifier() {
        return editor.getName();
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public Menus getMenus() {
        return null;
    }

    @Override
    public ToolBar getToolBar() {
        return null;
    }

    @Override
    public String getSignatureId() {
        return editor.getName();
    }

    @Override
    public Collection<String> getRoles() {
        return ROLES;
    }

    @Override
    public Collection<String> getTraits() {
        return TRAITS;
    }
}
