/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.menu.dev;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.kie.workbench.common.stunner.client.widgets.menu.MenuUtils;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.util.AnchorListItems;
import org.kie.workbench.common.stunner.core.util.ButtonGroups;
import org.kie.workbench.common.stunner.core.util.Buttons;
import org.kie.workbench.common.stunner.core.util.DropDownMenus;
import org.uberfire.workbench.model.menu.MenuItem;

/**
 * The menu builder that provides a dropdown menu item in order to create new diagrams.
 */
@ApplicationScoped
public class ShapeSetsMenuItemsBuilder {

    private final ShapeManager shapeManager;

    public interface Callback {

        void onClick(ShapeSet shapeSet);
    }

    @Inject
    public ShapeSetsMenuItemsBuilder(final ShapeManager shapeManager) {
        this.shapeManager = shapeManager;
    }

    public MenuItem build(final String title,
                          final String prefix,
                          final Callback callback) {
        final DropDownMenu menu = new DropDownMenus.Builder().addStyleName("pull-right").build();

        final Collection<ShapeSet<?>> shapeSets = shapeManager.getShapeSets();
        if (null != shapeSets) {
            shapeSets.stream().forEach(shapeSet -> {
                menu.add(new AnchorListItems.Builder(prefix + " " + shapeSet.getDescription())
                                 .setTitle(prefix + " " + shapeSet.getDescription())
                                 .setIcon(IconType.PLUS)
                                 .addClickHandler(event -> callback.onClick(shapeSet)).build()
                );
            });
        }

        return MenuUtils.buildItem(new ButtonGroups.Builder()
                                           .add(new Buttons.Builder()
                                                        .setToggleCaret(false)
                                                        .setDataToggle(Toggle.DROPDOWN)
                                                        .setSize(ButtonSize.SMALL)
                                                        .setText(title)
                                                        .setTitle(title).build()
                                           )
                                           .add(menu).build()
        );
    }
}
