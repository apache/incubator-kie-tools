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

package org.uberfire.client.docks.view.items;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconPosition;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Span;
import org.uberfire.client.util.CSSLocatorsUtils;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.ParameterizedCommand;

public abstract class AbstractDockItem extends Composite {

    private final boolean opened;
    private final UberfireDock dock;

    AbstractDockItem(UberfireDock dock) {
        this.dock = dock;
        this.opened = false;
    }

    public static AbstractDockItem create(UberfireDock dock,
                                          ParameterizedCommand<String> openCommand,
                                          ParameterizedCommand<String> closeCommand) {
        if (dock.getDockPosition() == UberfireDockPosition.SOUTH) {
            return new SouthDockItem(dock,
                                     openCommand,
                                     closeCommand);
        } else {
            return new SideDockItem(dock,
                                    openCommand,
                                    closeCommand);
        }
    }

    void configureText(final Button itemButton,
                       final String text) {
        Span itemButtonText = GWT.create(Span.class);
        itemButtonText.setText(text);
        itemButton.insert(itemButtonText,
                          0);
    }

    void configureIcon(Button itemButton,
                       ImageResource imageResource) {
        if (getDock().getIconType() != null) {
            itemButton.setIcon(getIcon());
            itemButton.setIconFixedWidth(true);
            itemButton.setIconSize(IconSize.LARGE);
            itemButton.setIconPosition(IconPosition.LEFT);
        } else {
            configureImageIcon(itemButton,
                               imageResource);
        }
    }

    void configureImageIcon(final Button itemButton,
                            final ImageResource imageResource) {
        if (imageResource != null) {
            final Image imageIcon = new Image(imageResource);
            imageIcon.getElement().getStyle().setWidth(14,
                                                       Style.Unit.PX);
            imageIcon.getElement().getStyle().setHeight(14,
                                                        Style.Unit.PX);

            if (itemButton.getWidgetCount() > 1) {
                imageIcon.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
                imageIcon.getElement().getStyle().setTop(3,
                                                         Style.Unit.PX);
                imageIcon.getElement().getStyle().setLeft(3,
                                                          Style.Unit.PX);

                itemButton.getElement().getStyle().setPaddingLeft(20,
                                                                  Style.Unit.PX);
                itemButton.getElement().getStyle().setPosition(Style.Position.RELATIVE);
            }

            itemButton.insert(imageIcon,
                              0);
        }
    }

    private IconType getIcon() {
        if (dock.getIconType() == null) {
            return null;
        }

        try {
            return IconType.valueOf(dock.getIconType());
        } catch (Exception e) {
            return IconType.FOLDER_OPEN;
        }
    }

    public UberfireDock getDock() {
        return dock;
    }

    public String getIdentifier() {
        return dock.getIdentifier();
    }

    public String getLabel() {
        return dock.getLabel();
    }

    public abstract void openAndExecuteExpandCommand();

    public abstract void open();

    public abstract void close();

    void setupCSSLocators(UberfireDock dock) {
        getElement().addClassName(CSSLocatorsUtils.buildLocator("qe-docks-item",
                                                                dock.getDockPosition().getShortName(),
                                                                dock.getIdentifier()));
    }
}
