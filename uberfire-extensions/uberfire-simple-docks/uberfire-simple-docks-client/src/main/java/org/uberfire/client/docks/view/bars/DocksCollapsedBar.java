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
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.ParameterizedCommand;

public class DocksCollapsedBar
        extends Composite {

    private UberfireDockPosition position;

    private WebAppResource CSS = GWT.create(WebAppResource.class);

    private AbstractDockItem firstDockItem;

    private SingleSideDockItem singleSideDockItem;

    interface ViewBinder
            extends
            UiBinder<Widget, DocksCollapsedBar> {

    }

    private ViewBinder uiBinder = GWT.create(ViewBinder.class);

    @UiField
    FlowPanel docksBarPanel;

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
    }

    public void addDock(final UberfireDock dock,
                        final ParameterizedCommand<String> selectCommand,
                        final ParameterizedCommand<String> deselectCommand) {

        AbstractDockItem dockItem = AbstractDockItem.create(dock, selectCommand, deselectCommand);

        if (dock.getDockPosition().allowSingleDockItem()) {
            handleSingleDockItem(dockItem, dock, selectCommand, deselectCommand);
        }

        docksBarPanel.add(dockItem);

        docksItems.add(dockItem);

    }


    private void handleSingleDockItem(AbstractDockItem dockItem, UberfireDock dock, ParameterizedCommand<String> selectCommand, ParameterizedCommand<String> deselectCommand) {
        if (docksItems.isEmpty()) {
            createSingleDockItem(dockItem, dock, selectCommand, deselectCommand);

        } else if (singleDockMode()) {
            clearSingleDockItem();
        }
    }

    public boolean singleDockMode() {
        return position.allowSingleDockItem() && docksItems.size() == 1;
    }

    private void createSingleDockItem(AbstractDockItem dockItem, UberfireDock dock, ParameterizedCommand<String> selectCommand, ParameterizedCommand<String> deselectCommand) {
        firstDockItem = dockItem;
        firstDockItem.addStyleName(CSS.CSS().hideElement());
        singleSideDockItem = new SingleSideDockItem(dock, selectCommand, deselectCommand);
        docksBarPanel.add(singleSideDockItem);
    }

    private void clearSingleDockItem() {
        firstDockItem.removeStyleName(CSS.CSS().hideElement());
        docksBarPanel.remove(singleSideDockItem);
    }

    public void setupDnD() {
        createDropHandler();
        for (AbstractDockItem docksItem : docksItems) {
            docksItem.setupDnD();
        }
    }

    private void createDropHandler() {

    }

    public void clear() {
        docksBarPanel.clear();
        docksItems = new ArrayList<AbstractDockItem>();
    }

    public void setDockSelected(final UberfireDock dockSelected) {
        for (AbstractDockItem docksItem : docksItems) {
            if (docksItem.getDock().equals(dockSelected)) {
                docksItem.select();
            } else {
                docksItem.deselect();
            }
        }
    }

    public void deselectAllDocks() {
        for (AbstractDockItem docksItem : docksItems) {
            docksItem.deselect();
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
            if(candidate.equals(targetDock)){
                abstractDockItem.selectAndExecuteExpandCommand();
            }
        }
    }


}
