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

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * An item in a package
 */
@Portable
public class FolderItem {

    private Object item;
    private String itemName;
    private FolderItemType type;
    private String lockedBy;
    private Boolean lockOwned;

    public FolderItem( @MapsTo("item") final Object item,
                       @MapsTo("itemName") final String itemName,
                       @MapsTo("type") final FolderItemType type,
                       @MapsTo("lockedBy") final String lockedBy,
                       @MapsTo("lockedOwned") final Boolean lockOwned ) {

        this( item,
              itemName,
              type );
        this.lockedBy = lockedBy;
        this.lockOwned = lockOwned;
    }

    public FolderItem( final Object item,
                       final String itemName,
                       final FolderItemType type ) {

        this.item = checkNotNull( "item",
                                  item );
        this.itemName = checkNotNull( "itemName",
                                      itemName );
        this.type = checkNotNull( "type",
                                  type );
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

    public String getLockedBy() {
        return lockedBy;
    }

    public boolean isLockOwned() {
        return lockOwned;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((item == null) ? 0 : item.hashCode());
        result = prime * result + ((itemName == null) ? 0 : itemName.hashCode());
        result = prime * result + ((lockOwned == null) ? 0 : lockOwned.hashCode());
        result = prime * result + ((lockedBy == null) ? 0 : lockedBy.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        FolderItem other = (FolderItem) obj;
        if ( item == null ) {
            if ( other.item != null )
                return false;
        } else if ( !item.equals( other.item ) )
            return false;
        if ( itemName == null ) {
            if ( other.itemName != null )
                return false;
        } else if ( !itemName.equals( other.itemName ) )
            return false;
        if ( lockOwned == null ) {
            if ( other.lockOwned != null )
                return false;
        } else if ( !lockOwned.equals( other.lockOwned ) )
            return false;
        if ( lockedBy == null ) {
            if ( other.lockedBy != null )
                return false;
        } else if ( !lockedBy.equals( other.lockedBy ) )
            return false;
        if ( type != other.type )
            return false;
        return true;
    }

}