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
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.uberfire.client.resources.WebAppResource;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.mvp.ParameterizedCommand;

public class SouthDockItem
        extends AbstractDockItem {

    private static WebAppResource CSS = GWT.create(WebAppResource.class);
    private final ParameterizedCommand<String> openCommand;
    private final ParameterizedCommand<String> closeCommand;
    @UiField
    Button itemButton;
    private boolean opened;
    private ViewBinder uiBinder = GWT.create(ViewBinder.class);

    SouthDockItem(final UberfireDock dock,
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
        configureText(itemButton,
                      getDock().getLabel());
        configureIcon(itemButton,
                      getDock().getImageIcon());
        itemButton.getElement().addClassName(CSS.CSS().southDockItem());
        itemButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!opened) {
                    open();
                    openCommand.execute(getIdentifier());
                } else {
                    close();
                    closeCommand.execute(getIdentifier());
                }
            }
        });
    }

    @Override
    public void openAndExecuteExpandCommand() {
        open();
        openCommand.execute(getIdentifier());
    }

    @Override
    public void open() {
        opened = true;
        itemButton.setType(ButtonType.INFO);
        if (getDock().getImageIconFocused() != null) {
            itemButton.remove(0);
            configureImageIcon(itemButton,
                               getDock().getImageIconFocused());
        }
    }

    @Override
    public void close() {
        opened = false;
        itemButton.setType(ButtonType.LINK);
        if (getDock().getImageIcon() != null) {
            itemButton.remove(0);
            configureImageIcon(itemButton,
                               getDock().getImageIcon());
        }
    }

    interface ViewBinder
            extends
            UiBinder<Widget, SouthDockItem> {

    }
}
