/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.server.builder.pagerow;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.server.util.AssetPageRowPopulator;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
//import org.jboss.seam.security.Identity;

public class AssetPageRowBuilder
    implements
    PageRowBuilder<PageRequest, AssetItemIterator> {
    private PageRequest       pageRequest;
    private AssetItemIterator iterator;
    //private Identity identity;

    public List<AssetPageRow> build() {
        validate();
        Integer pageSize = pageRequest.getPageSize();
        iterator.skip( pageRequest.getStartRowIndex() );
        List<AssetPageRow> rowList = new ArrayList<AssetPageRow>();

        while ( iterator.hasNext() && (pageSize == null || rowList.size() < pageSize) ) {
            AssetItem assetItem = iterator.next();
            AssetPageRowPopulator assetPageRowPopulator = new AssetPageRowPopulator();
            rowList.add( assetPageRowPopulator.populateFrom( assetItem ) );
        }
        return rowList;
    }

    public void validate() {
        if ( pageRequest == null ) {
            throw new IllegalArgumentException( "PageRequest cannot be null" );
        }

        if ( iterator == null ) {
            throw new IllegalArgumentException( "Content cannot be null" );
        }

    }

    public AssetPageRowBuilder withPageRequest(PageRequest pageRequest) {
        this.pageRequest = pageRequest;
        return this;
    }

    public AssetPageRowBuilder withIdentity(/*Identity identity*/) {
        //this.identity = identity;
        return this;
    }

    public AssetPageRowBuilder withContent(AssetItemIterator iterator) {
        this.iterator = iterator;
        return this;
    }
}
