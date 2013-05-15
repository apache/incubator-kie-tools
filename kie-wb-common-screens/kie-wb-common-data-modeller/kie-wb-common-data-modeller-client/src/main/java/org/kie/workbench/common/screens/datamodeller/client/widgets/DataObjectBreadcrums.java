/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets;

import com.github.gwtbootstrap.client.ui.Breadcrumbs;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectSelectedEvent;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class DataObjectBreadcrums extends Breadcrumbs {

    int size = 5;
    
    private List<BufferElement> buffer = new ArrayList<BufferElement>();

    @Inject
    private Event<DataModelerEvent> dataModelerEvent;

    private DataModelerContext context;

    public DataObjectBreadcrums(int size) {
        super();
        this.size = size;
    }

    public DataObjectBreadcrums() {
        super();
    }

    public void setSize(int size) {
        this.size = size;
    }

    public DataModelerContext getContext() {
        return context;
    }

    public void setContext(DataModelerContext context) {
        this.context = context;
    }

    @Override
    public void clear() {
        super.clear();
        buffer.clear();
    }

    public void add(final DataObjectTO dataObject) {

        //scan the buffer to see if the item is in the breadcrumb
        int currentPosition = -1;
        int j = 0;
        boolean addElement = false;

        for (BufferElement bufferedElement : buffer) {
            if (bufferedElement.getDataObject().equals(dataObject)) {
                // the object to be inserted is already in the buffer
                currentPosition = j;
                break;
            }
            j++;
        }

        if (currentPosition >= 0) {
            if (currentPosition < (buffer.size()-1)) {
                //the element exists in the buffer and isn't the last.
                //remove it from current position.
                buffer.remove(currentPosition);
                addElement = true;
            } else {
                //the elements is already in the last position
                addElement = false;
            }
        } else {
            addElement = true;
        }

        if (addElement) {

            if (buffer.size() >= size) {
                //remove first element
                buffer.remove(0);
            }

            buffer.add(new BufferElement(dataObject));

            rebuild();
        }
    }

    private void adjustNavigation(DataObjectTO dataObjectTO) {
        //we are selecting an already existing object.
        //if we have A -> B -> C -> D, and we select B
        //the breadcrumb should show A->B

        List<BufferElement> remainingItems = new ArrayList<BufferElement>();

        for (BufferElement bufferElement : buffer) {
            remainingItems.add(bufferElement);
            if (dataObjectTO.getClassName().equals(bufferElement.getDataObject().getClassName())) break;            
        }
        buffer.clear();
        buffer.addAll(remainingItems);
        rebuild();
    }

    private void rebuild() {
        super.clear();
        for (BufferElement element : buffer) {
             add(element.getWidget());
        }
    }

    class BufferElement {

        DataObjectTO dataObject;
        Widget widget;

        BufferElement(DataObjectTO dataObject) {
            this.dataObject = dataObject;
        }

        public DataObjectTO getDataObject() {
            return dataObject;
        }

        public void setDataObject(DataObjectTO dataObject) {
            this.dataObject = dataObject;
        }

        public Widget getWidget() {
            //create the new widget
            NavLink navLink = new NavLink(dataObject.getName());
            navLink.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    adjustNavigation(dataObject);
                    notifyObjectSelected(dataObject);
                }
            });
            return navLink;
        }
    }

    private DataModelTO getDataModel() {
        return getContext().getDataModel();
    }

    // Event Observers
    private void onDataObjectSelected(@Observes DataObjectSelectedEvent event) {

        if (event.isFrom(getDataModel())) {
            if (event.getCurrentDataObject() != null) {
                if (event.isFrom(DataModelerEvent.DATA_OBJECT_BROWSER)) {
                    //it's a type selection in the editor
                    //locate the object and select my row, but it's not needed to fire selection
                    add(event.getCurrentDataObject());
                } else if (event.isFrom(DataModelerEvent.DATA_MODEL_BROWSER)) {
                    clear();
                    add(event.getCurrentDataObject());
                }
            }
        }
    }

    private void onDataObjectChange(@Observes DataObjectChangeEvent event) {
        if (event.isFrom(getDataModel())) {
            if ("name".equals(event.getPropertyName())) {
                rebuild();
            }
        }
    }

    // Event notifications
    private void notifyObjectSelected(DataObjectTO dataObject) {
        dataModelerEvent.fire(new DataObjectSelectedEvent(DataModelerEvent.DATA_MODEL_BREAD_CRUMB, getDataModel(), dataObject));
    }

}