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
package org.kie.workbench.common.kogito.client.editor;

import java.util.Objects;
import java.util.function.Supplier;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.promise.Promise;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

/**
 * This is a trimmed down {@code org.uberfire.ext.editor.commons.client.BaseEditor} for Kogito.
 * @param <CONTENT> The domain model of the editor
 */
public abstract class BaseKogitoEditor<CONTENT> {

    private boolean isReadOnly;

    private BaseEditorView baseEditorView;
    private PlaceManager placeManager;
    private Menus menus;
    private PlaceRequest place;
    private Integer originalHash;

    protected BaseKogitoEditor() {
        //CDI proxy
    }

    protected BaseKogitoEditor(final BaseEditorView baseView,
                               final PlaceManager placeManager) {
        this.baseEditorView = baseView;
        this.placeManager = placeManager;
    }

    protected void init(final PlaceRequest place) {
        this.place = place;
        this.isReadOnly = this.place.getParameter("readOnly", null) != null;

        makeMenuBar();

        buildMenuBar();
    }

    protected abstract void makeMenuBar();

    protected abstract void buildMenuBar();

    public void disableMenuItem(final MenuItems menuItem) {
        setEnableMenuItem(menuItem, false);
    }

    public void enableMenuItem(final MenuItems menuItem) {
        setEnableMenuItem(menuItem, true);
    }

    private void setEnableMenuItem(final MenuItems menuItem,
                                   final boolean isEnabled) {
        if (getMenus().getItemsMap().containsKey(menuItem)) {
            getMenus().getItemsMap().get(menuItem).setEnabled(isEnabled);
        }
    }

    protected PlaceRequest getPlaceRequest() {
        return place;
    }

    protected PlaceManager getPlaceManager() {
        return placeManager;
    }

    protected Menus getMenus() {
        return menus;
    }

    protected void setMenus(final Menus menus) {
        this.menus = menus;
    }

    protected BaseEditorView getBaseEditorView() {
        return baseEditorView;
    }

    protected IsWidget getTitle() {
        return baseEditorView.getTitleWidget();
    }

    protected Supplier<CONTENT> getContentSupplier() {
        return () -> null;
    }

    public void setOriginalContentHash(final Integer originalHash) {
        this.originalHash = originalHash;
    }

    protected Integer getOriginalContentHash() {
        return originalHash;
    }

    protected Integer getCurrentContentHash() {
        try {
            return getContentSupplier().get().hashCode();
        } catch (final Exception e) {
            return null;
        }
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public boolean mayClose() {
        return !isDirty() || baseEditorView.confirmClose();
    }

    /**
     * Used by Kogito to determine whether the content has unsaved changes.
     * @return true if there are unsaved changes.
     */
    public boolean isDirty() {
        return !Objects.equals(getCurrentContentHash(), getOriginalContentHash());
    }

    /**
     * Used by Kogito to set the XML content of the editor.
     */
    public abstract void setContent(final String value);

    /**
     * Used by Kogito to get the XML content of the editor. This should return a {@link String}
     * representation of the editors content to persist to an underlying persistent store.
     */
    public abstract Promise getContent();

    /**
     * Used by Kogito to reset the editors "dirty" state following a successful save.
     */
    public abstract void resetContentHash();
}
