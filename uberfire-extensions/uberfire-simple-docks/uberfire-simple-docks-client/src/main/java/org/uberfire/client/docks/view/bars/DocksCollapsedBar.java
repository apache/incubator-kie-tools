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

package org.uberfire.client.docks.view.bars;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.docks.view.items.AbstractDockItem;
import org.uberfire.client.docks.view.items.SingleSideDockItem;
import org.uberfire.client.resources.WebAppResource;
import org.uberfire.client.util.CSSLocatorsUtils;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.ParameterizedCommand;

public class DocksCollapsedBar
        extends Composite {

    @UiField
    FlowPanel docksBarPanel;
    private UberfireDockPosition position;
    private WebAppResource CSS = GWT.create(WebAppResource.class);
    private AbstractDockItem firstDockItem;
    private SingleSideDockItem singleSideDockItem;
    private ViewBinder uiBinder = GWT.create(ViewBinder.class);
    private List<AbstractDockItem> docksItems = new ArrayList<AbstractDockItem>();

    public DocksCollapsedBar(UberfireDockPosition position) {
        initWidget(uiBinder.createAndBindUi(this));
        this.position = position;
        setCSS(position);
    }

    private void setCSS(UberfireDockPosition position) {
        if (position == UberfireDockPosition.SOUTH) {
            docksBarPanel.addStyleName(CSS.CSS().gradientTopBottom());
        } else {
            docksBarPanel.addStyleName(CSS.CSS().gradientBottomTop());
        }
        setupCSSLocators(position);
    }

    private void setupCSSLocators(UberfireDockPosition position) {

        docksBarPanel.addStyleName(CSSLocatorsUtils.buildLocator("qe-docks-bar",
                                                                 position.getShortName()));
    }

    public void addDock(final UberfireDock dock,
                        final ParameterizedCommand<String> openCommand,
                        final ParameterizedCommand<String> closeCommand) {

        AbstractDockItem dockItem = AbstractDockItem.create(dock,
                                                            openCommand,
                                                            closeCommand);

        if (dock.getDockPosition().allowSingleDockItem()) {
            handleSingleDockItem(dockItem,
                                 dock,
                                 openCommand);
        }

        docksBarPanel.add(dockItem);

        docksItems.add(dockItem);
    }

    private void handleSingleDockItem(AbstractDockItem dockItem,
                                      UberfireDock dock,
                                      ParameterizedCommand<String> openCommand) {
        if (docksItems.isEmpty()) {
            createSingleDockItem(dockItem,
                                 dock,
                                 openCommand);
        } else if (singleDockMode()) {
            clearSingleDockItem();
        }
    }

    public boolean singleDockMode() {
        return position.allowSingleDockItem() && docksItems.size() == 1;
    }

    private void createSingleDockItem(AbstractDockItem dockItem,
                                      UberfireDock dock,
                                      ParameterizedCommand<String> openCommand) {
        firstDockItem = dockItem;
        firstDockItem.addStyleName(CSS.CSS().hideElement());
        singleSideDockItem = new SingleSideDockItem(dock,
                                                    openCommand);
        docksBarPanel.add(singleSideDockItem);
    }

    private void clearSingleDockItem() {
        firstDockItem.removeStyleName(CSS.CSS().hideElement());
        docksBarPanel.remove(singleSideDockItem);
    }

    public void clear() {
        docksBarPanel.clear();
        docksItems = new ArrayList<>();
    }

    public void setDockClosed(final UberfireDock dockOpen) {
        for (AbstractDockItem docksItem : docksItems) {
            if (docksItem.getDock().equals(dockOpen)) {
                docksItem.open();
            } else {
                docksItem.close();
            }
        }
    }

    public void closeAllDocks() {
        for (AbstractDockItem docksItem : docksItems) {
            docksItem.close();
        }
    }

    public UberfireDockPosition getPosition() {
        return position;
    }

    public List<AbstractDockItem> getDocksItems() {
        return docksItems;
    }

    public void expand(UberfireDock targetDock) {
        for (AbstractDockItem abstractDockItem : getDocksItems()) {
            UberfireDock candidate = abstractDockItem.getDock();
            if (candidate.getPlaceRequest().equals(targetDock.getPlaceRequest())) {
                abstractDockItem.openAndExecuteExpandCommand();
            }
        }
    }

    interface ViewBinder
            extends
            UiBinder<Widget, DocksCollapsedBar> {

    }
}
