/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.api;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.explorer.model.FolderItem;

@Portable
public class AssetInfo {

    private FolderItem folderItem;

    private Date lastModifiedTime;

    private Date createdTime;

    public AssetInfo( @MapsTo( "folderItem" ) final FolderItem folderItem,
                      @MapsTo( "lastModifiedTime" ) final Date lastModifiedTime,
                      @MapsTo( "createdTime" ) final Date createdTime ) {
        this.folderItem = folderItem;
        this.lastModifiedTime = lastModifiedTime;
        this.createdTime = createdTime;
    }

    public FolderItem getFolderItem() {
        return folderItem;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof AssetInfo ) ) {
            return false;
        }

        final AssetInfo assetInfo = (AssetInfo) o;

        if ( getFolderItem() != null ? !getFolderItem().equals( assetInfo.getFolderItem() ) : assetInfo.getFolderItem() != null ) {
            return false;
        }
        if ( getLastModifiedTime() != null ? !getLastModifiedTime().equals( assetInfo.getLastModifiedTime() ) : assetInfo.getLastModifiedTime() != null ) {
            return false;
        }
        return !( getCreatedTime() != null ? !getCreatedTime().equals( assetInfo.getCreatedTime() ) : assetInfo.getCreatedTime() != null );

    }

    @Override
    public int hashCode() {
        int result = getFolderItem() != null ? getFolderItem().hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( getLastModifiedTime() != null ? getLastModifiedTime().hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( getCreatedTime() != null ? getCreatedTime().hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
