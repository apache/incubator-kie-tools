/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.google.gwt.event.shared.GwtEvent;

public class AddItemEvent
        extends GwtEvent<AddItemHandler> {


    private static Type<AddItemHandler> TYPE;
    private String itemName;

    public static Type<AddItemHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<AddItemHandler>();
        }
        return TYPE;
    }

    public AddItemEvent(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public static <T> void fire(HasAddItemHandlers source, String itemName) {
        if (TYPE != null) {
            AddItemEvent event = new AddItemEvent(itemName);
            source.fireEvent(event);
        }
    }

    @Override
    public Type<AddItemHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AddItemHandler handler) {
        handler.onAddItem(this);
    }
}
