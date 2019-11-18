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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.uberfire.client.resources.WebAppResource;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.mvp.ParameterizedCommand;

public class SideDockItem extends AbstractSideDockItem {

    private static WebAppResource CSS = GWT.create(WebAppResource.class);
    private final ParameterizedCommand<String> openCommand;
    private final ParameterizedCommand<String> closeCommand;

    @UiField
    Button itemButton;

    @UiField
    Tooltip itemTooltip;

    private boolean opened;

    private ViewBinder uiBinder = GWT.create(ViewBinder.class);

    SideDockItem(UberfireDock dock,
                 final ParameterizedCommand<String> openCommand,
                 final ParameterizedCommand<String> closeCommand) {
        super(dock);
        this.openCommand = openCommand;
        this.closeCommand = closeCommand;
        initWidget(uiBinder.createAndBindUi(this));

        createButton();
        setupCSSLocators(dock);
    }

    void createButton() {
        itemButton.setSize(ButtonSize.SMALL);
        itemButton.setType(ButtonType.LINK);
        configureIcon(itemButton, getDock().getImageIcon());

        String tooltip = getDock().getTooltip();
        if (tooltip == null) {
            tooltip = getDock().getLabel();
        }

        configureTooltip(itemTooltip, tooltip);

        itemButton.addStyleName(CSS.CSS().sideDockItem());
        itemButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!isOpened()) {
                    openAndExecuteExpandCommand();
                } else {
                    closeAndExecuteCommand();
                }
            }
        });
    }

    @Override
    public void open() {
        opened = true;
        itemButton.setActive(true);
        itemButton.setType(ButtonType.INFO);
        if (getDock().getImageIconFocused() != null) {
            itemButton.remove(0);
            configureImageIcon(itemButton,
                               getDock().getImageIconFocused());
        }
    }

    @Override
    public void openAndExecuteExpandCommand() {
        open();
        openCommand.execute(getIdentifier());
    }

    @Override
    public void close() {
        opened = false;
        itemButton.setActive(false);
        itemButton.setType(ButtonType.LINK);
        if (getDock().getImageIcon() != null) {
            itemButton.remove(0);
            configureImageIcon(itemButton,
                               getDock().getImageIcon());
        }
    }

    public void closeAndExecuteCommand() {
        close();
        closeCommand.execute(getIdentifier());
    }

    public boolean isOpened() {
        return opened;
    }

    interface ViewBinder
            extends
            UiBinder<Widget, SideDockItem> {

    }
}
