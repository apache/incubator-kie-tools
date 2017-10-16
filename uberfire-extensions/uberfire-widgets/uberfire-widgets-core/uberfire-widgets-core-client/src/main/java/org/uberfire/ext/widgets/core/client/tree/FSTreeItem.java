/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.core.client.tree;

import java.util.function.Supplier;

import com.google.gwt.user.client.ui.FlowPanel;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.constants.IconType;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class FSTreeItem extends TreeItem<FSTreeItem> {

    private final FSType fstype;

    FSTreeItem(final FSType type,
               final String value,
               final Supplier<FlowPanel> contentProvider) {
        super(createType(type),
              value,
              value,
              createIcon(type),
              contentProvider);
        this.fstype = checkNotNull("type",
                                   type);
    }

    public FSTreeItem(final FSType type,
                      final String value) {
        super(createType(type),
              value,
              value,
              createIcon(type));
        this.fstype = checkNotNull("type",
                                   type);
    }

    private static final Type createType(final FSType type) {
        switch (type) {
            case ITEM:
                return Type.ITEM;
            case FOLDER:
                return Type.CONTAINER;
        }
        return Type.ROOT;
    }

    private static final Icon createIcon(final FSType type) {
        IconType iconType = IconType.FOLDER;
        switch (type) {
            case ITEM:
                iconType = IconType.FILE_O;
                break;
            case FOLDER:
                iconType = IconType.FOLDER;
                break;
            case ROOT:
                iconType = IconType.FOLDER;
                break;
        }
        return new Icon(iconType);
    }

    public FSTreeItem addItem(final FSType type,
                              final String value) {
        final FSTreeItem treeItem = new FSTreeItem(type,
                                                   value);
        super.addItem(treeItem);
        return treeItem;
    }

    public FSType getFSType() {
        return this.fstype;
    }

    @Override
    protected void onOpenState() {
        super.onOpenState();
        getIcon().setType(IconType.FOLDER_OPEN);
    }

    @Override
    protected void onCloseState() {
        super.onCloseState();
        getIcon().setType(IconType.FOLDER);
    }

    private Icon getIcon() {
        return (Icon) getIconWidget();
    }

    public enum FSType {
        ROOT,
        FOLDER,
        ITEM,
        LOADING
    }
}