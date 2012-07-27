package org.drools.guvnor.server.builder.pagerow;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.QueryPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRow;
import org.drools.guvnor.server.impl.CategoryFilter;
import org.drools.guvnor.server.impl.ModuleFilter;
import org.drools.guvnor.server.security.RoleType;
import org.drools.guvnor.server.util.QueryPageRowCreator;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.RepositoryFilter;
//import org.jboss.seam.security.Identity;

public class QueryFullTextPageRowBuilder
    implements
    PageRowBuilder<QueryPageRequest, Iterator<AssetItem>> {

    private QueryPageRequest    pageRequest;
    private Iterator<AssetItem> iterator;
    //private Identity identity;

    public List<QueryPageRow> build() {
        validate();
        int skipped = 0;
        Integer pageSize = pageRequest.getPageSize();
        int startRowIndex = pageRequest.getStartRowIndex();
        RepositoryFilter filter = new ModuleFilter(/*identity*/);
        RepositoryFilter categoryFilter = new CategoryFilter(/*identity*/);

        List<QueryPageRow> rowList = new ArrayList<QueryPageRow>();

        while ( iterator.hasNext() && (pageSize == null || rowList.size() < pageSize) ) {
            AssetItem assetItem = iterator.next();

            // Filter surplus assets
            if ( checkPackagePermissionHelper( filter, assetItem, RoleType.PACKAGE_READONLY.getName() )
                    || checkCategoryPermissionHelper(categoryFilter, assetItem, RoleType.ANALYST_READ.getName())) {
                // Cannot use AssetItemIterator.skip() as it skips non-filtered
                // assets whereas startRowIndex is the index of the
                // first displayed asset (i.e. filtered)
                if ( skipped >= startRowIndex ) {
                    rowList.add( QueryPageRowCreator.makeQueryPageRow( assetItem ) );
                }
                skipped++;
            }
        }
        return rowList;
    }

    private boolean checkPackagePermissionHelper(RepositoryFilter filter,
                                                 AssetItem item,
                                                 String roleType) {
        return filter.accept( getConfigDataHelper( item.getModule().getUUID() ),
                              roleType );
    }

    private boolean checkCategoryPermissionHelper(RepositoryFilter filter,
                                                  AssetItem item,
                                                  String roleType) {
        List<CategoryItem> tempCateList = item.getCategories();
        for (CategoryItem categoryItem : tempCateList) {
            if (filter.accept(categoryItem.getFullPath(),
                    roleType)) {
                return true;
            }
        }

        return false;
    }
    
    private Module getConfigDataHelper(String uuidStr) {
        Module data = new Module();
        data.setUuid( uuidStr );
        return data;
    }

    public void validate() {
        if ( pageRequest == null ) {
            throw new IllegalArgumentException( "PageRequest cannot be null" );
        }

        if ( iterator == null ) {
            throw new IllegalArgumentException( "Content cannot be null" );
        }

    }

    public QueryFullTextPageRowBuilder withPageRequest(QueryPageRequest pageRequest) {
        this.pageRequest = pageRequest;
        return this;
    }

    public QueryFullTextPageRowBuilder withIdentity(/*Identity identity*/) {
        //this.identity = identity;
        return this;
    }

    public QueryFullTextPageRowBuilder withContent(Iterator<AssetItem> iterator) {
        this.iterator = iterator;
        return this;
    }
}
