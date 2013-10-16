/*
 * Copyright 2012 JBoss Inc
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
package org.kie.workbench.common.screens.explorer.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * An item in a package
 */
@Portable
public class FolderItem {

    private Object item;
    private String itemName;
    private FolderItemType type;
    private String age;

    public FolderItem() {
        //For Errai-marshalling
    }

    public FolderItem( final Object item,
                       final String itemName,
                       final FolderItemType type ) {
        this.item = checkNotNull( "item", item );
        this.itemName = checkNotNull( "itemName", itemName );
        this.type = checkNotNull( "type", type );
    }

    public Object getItem() {
        return item;
    }

    public String getFileName() {
        return this.itemName;
    }

    public FolderItemType getType() {
        return type;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof FolderItem ) ) {
            return false;
        }

        FolderItem folderItem = (FolderItem) o;

        if ( !item.equals( folderItem.item ) ) {
            return false;
        }

        if ( !itemName.equals( folderItem.itemName ) ) {
            return false;
        }

        return type.equals( folderItem.type );
    }

    @Override
    public int hashCode() {
        int result = item.hashCode();
        result = 31 * result + itemName.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

}
