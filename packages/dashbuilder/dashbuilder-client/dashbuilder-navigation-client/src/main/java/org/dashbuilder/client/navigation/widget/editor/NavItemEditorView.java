/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.navigation.widget.editor;

import com.google.gwt.core.client.Duration;
import org.dashbuilder.client.navigation.resources.i18n.NavigationConstants;

public abstract class NavItemEditorView<P extends NavItemEditor> implements org.jboss.errai.ui.client.local.api.IsElement, NavItemEditor.View<P> {

    NavigationConstants i18n = NavigationConstants.INSTANCE;

    @Override
    public String i18nNewItem(String item) {
        return i18n.newItem(item);
    }

    @Override
    public String i18nNewItemName(String item) {
        return i18n.newItemName(item);
    }

    @Override
    public String i18nGotoItem(String item) {
        return i18n.gotoItem(item);
    }

    @Override
    public String i18nDeleteItem() {
        return i18n.deleteItem();
    }

    @Override
    public String i18nMoveUp() {
        return i18n.moveUp();
    }

    @Override
    public String i18nMoveDown() {
        return i18n.moveDown();
    }

    @Override
    public String i18nMoveFirst() {
        return i18n.moveFirst();
    }

    @Override
    public String i18nMoveLast() {
        return i18n.moveLast();
    }

    @Override
    public String generateId() {
        return Double.toString(Duration.currentTimeMillis());
    }
}

