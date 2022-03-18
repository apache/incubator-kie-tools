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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.client.resources.WebAppResource;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.mvp.ParameterizedCommand;

public class SingleSideDockItem
        extends AbstractDockItem {

    private static WebAppResource CSS = GWT.create(WebAppResource.class);
    private final ParameterizedCommand<String> openCommand;

    @UiField
    Button itemButton;

    private ViewBinder uiBinder = GWT.create(ViewBinder.class);

    public SingleSideDockItem(UberfireDock dock,
                              final ParameterizedCommand<String> openCommand) {
        super(dock);
        this.openCommand = openCommand;
        initWidget(uiBinder.createAndBindUi(this));
        createButtom();
        setupLocators(dock);
    }

    private void createButtom() {
        itemButton.addStyleName(CSS.CSS().singleDockItem());
        itemButton.addStyleName(CSS.CSS().sideDockItem());
        itemButton.setIcon(IconType.CHEVRON_RIGHT);
        itemButton.setIconFixedWidth(true);
        itemButton.setSize(ButtonSize.SMALL);
        itemButton.setType(ButtonType.LINK);
        itemButton.addClickHandler(event -> openAndExecuteExpandCommand());
    }

    @Override
    public void openAndExecuteExpandCommand() {
        openCommand.execute(getIdentifier());
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }

    interface ViewBinder
            extends
            UiBinder<Widget, SingleSideDockItem> {

    }
}