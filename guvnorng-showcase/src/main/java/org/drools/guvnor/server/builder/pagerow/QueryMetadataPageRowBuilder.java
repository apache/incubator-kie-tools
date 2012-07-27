package org.drools.guvnor.server.builder.pagerow;

import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.QueryMetadataPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRow;
import org.drools.guvnor.server.impl.CategoryFilter;
import org.drools.guvnor.server.impl.ModuleFilter;
import org.drools.guvnor.server.security.RoleType;
import org.drools.guvnor.server.util.QueryPageRowCreator;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.RepositoryFilter;
//import org.jboss.seam.security.Identity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QueryMetadataPageRowBuilder
        implements
        PageRowBuilder<QueryMetadataPageRequest, Iterator<AssetItem>> {

    private QueryMetadataPageRequest pageRequest;
    private Iterator<AssetItem> iterator;
    //private Identity identity;

    public List<QueryPageRow> build() {
        validate();
        int skipped = 0;
        Integer pageSize = pageRequest.getPageSize();
        int startRowIndex = pageRequest.getStartRowIndex();
        RepositoryFilter packageFilter = new ModuleFilter(/*identity*/);
        RepositoryFilter categoryFilter = new CategoryFilter(/*identity*/);
        List<QueryPageRow> rowList = new ArrayList<QueryPageRow>();

        while (iterator.hasNext() && (pageSize == null || rowList.size() < pageSize)) {
            AssetItem assetItem = iterator.next();

            // Filter surplus assets
            if (checkPackagePermissionHelper(packageFilter,
                    assetItem,
                    RoleType.PACKAGE_READONLY.getName()) || checkCategoryPermissionHelper(categoryFilter,
                    assetItem,
                    RoleType.ANALYST_READ.getName())) {

                // Cannot use AssetItemIterator.skip() as it skips non-filtered
                // assets whereas startRowIndex is the index of the
                // first displayed asset (i.e. filtered)
                if (skipped >= startRowIndex) {
                    rowList.add(QueryPageRowCreator.makeQueryPageRow(assetItem));
                }
                skipped++;
            }
        }
        return rowList;
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

    private boolean checkPackagePermissionHelper(RepositoryFilter filter,
                                                 AssetItem item,
                                                 String roleType) {
        return filter.accept(getConfigDataHelper(item.getModule().getUUID()),
                roleType);
    }

    private Module getConfigDataHelper(String uuidStr) {
        Module data = new Module();
        data.setUuid(uuidStr);
        return data;
    }

    public void validate() {
        if (pageRequest == null) {
            throw new IllegalArgumentException("PageRequest cannot be null");
        }

        if (iterator == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }

    }

    public QueryMetadataPageRowBuilder withPageRequest(QueryMetadataPageRequest pageRequest) {
        this.pageRequest = pageRequest;
        return this;
    }

    public QueryMetadataPageRowBuilder withIdentity(/*Identity identity*/) {
        //this.identity = identity;
        return this;
    }

    public QueryMetadataPageRowBuilder withContent(Iterator<AssetItem> iterator) {
        this.iterator = iterator;
        return this;
    }

}
