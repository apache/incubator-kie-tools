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
package org.drools.guvnor.client.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.SerializationException;

@RemoteServiceRelativePath("assetService")
public interface AssetService
    extends
    RemoteService {

    /**
     * This will quickly return a list of assets
     * 
     * @deprecated in favour of {@link quickFindAsset(QueryPageRequest)}
     */
    public TableDataResult quickFindAsset(String searchText,
                                          boolean searchArchived,
                                          int skip,
                                          int numRows) throws SerializationException;

    /**
     * Runs a full text search using JCR.
     * 
     * @param text
     * @param seekArchived
     * @param skip
     * @param numRows
     * @return
     * @throws SerializationException
     * @deprecated in favour of {@link queryFullText(QueryPageRequest)}
     */
    public TableDataResult queryFullText(String text,
                                         boolean seekArchived,
                                         int skip,
                                         int numRows) throws SerializationException;

    /**
     * Returns the lockers user name
     * 
     * @param uuid
     * @return Lockers user name or null if there is no lock.
     */
    public String getAssetLockerUserName(String uuid);

    /**
     * Locks the asset, if a lock already exists this over writes it.
     * 
     * @param uuid
     */
    public void lockAsset(String uuid);

    /**
     * Unlocks the asset.
     * 
     * @param uuid
     */
    public void unLockAsset(String uuid);

    /**
     * This will quickly return a list of assets
     * 
     * @param queryRequest
     *            The parameters for the search
     */
    public PageResponse<QueryPageRow> quickFindAsset(QueryPageRequest queryRequest) throws SerializationException;

    public void archiveAsset(String uuid);

    public void unArchiveAsset(String uuid);

    /**
     * Archive assets based on uuid
     * 
     * @param uuids
     */
    public void archiveAssets(String[] uuids,
                              boolean value);

    /**
     * Remove an asset based on uuid
     * 
     * @param uuid
     */
    public void removeAsset(String uuid);

    /**
     * Remove assets based on uuid
     * 
     * @param uuids
     */
    public void removeAssets(String[] uuids);

    /**
     * This will return the effective source for an asset (in DRL). Used as an
     * aid for debugging.
     */
    public String buildAssetSource(Asset asset) throws SerializationException;

    /**
     * This will build the asset and return any build results (errors). This is
     * only to report on the results - it will generally not store any state or
     * apply any changed.
     */
    public BuilderResult validateAsset(Asset asset) throws SerializationException;

    /**
     * Rename an asset.
     */
    public String renameAsset(String uuid,
                              String newName);

    /**
     * This loads up all the stuff for a rule asset based on the UUID (always
     * latest and editable version).
     */
    public Asset loadRuleAsset(String UUID) throws SerializationException;

    public Asset[] loadRuleAssets(String[] UUIDs) throws SerializationException;

    /**
     * This checks in a new version of an asset.
     * 
     * @return the UUID of the asset you are checking in, null if there was some
     *         problem (and an exception was not thrown).
     */
    public String checkinVersion(Asset asset) throws SerializationException;

    /**
     * This will restore the specified version in the repository, saving, and
     * creating a new version (with all the restored content).
     */
    public void restoreVersion(String versionUUID,
                               String assetUUID,
                               String comment);

    /**
     * This will load the history of the given asset or package, in a summary
     * format suitable for display in a table.
     */
    public TableDataResult loadItemHistory(String uuid) throws SerializationException;

    /**
     * This will load the history of the given asset, in a summary format
     * suitable for display in a table.
     */
    public TableDataResult loadAssetHistory(String packageUUID,
                                            String assetName) throws SerializationException;

    /**
     * This will load all archived assets, in a summary format suitable for
     * display in a table.
     * 
     * @deprecated in favor of {@link loadArchivedAssets(PageRequest)}
     */
    public TableDataResult loadArchivedAssets(int skip,
                                              int numRows) throws SerializationException;

    /**
     * This will load all archived assets, in a summary format suitable for
     * display in a table.
     */

    public PageResponse<AdminArchivedPageRow> loadArchivedAssets(PageRequest request) throws SerializationException;

    /**
     * Supports filtering and pagination.
     * 
     * @param request
     *            never null, contains filter and pagination values
     * @return never null, contains the {@link List} of {@link AssetPageRow}
     * @throws SerializationException
     */
    public PageResponse<AssetPageRow> findAssetPage(AssetPageRequest request) throws SerializationException;

    /**
     * Given a format, this will return assets that match. It can also be used
     * for "pagination" by passing in start and finish row numbers.
     * 
     * @param packageUUID
     *            The package uuid to search inside.
     * @param format
     *            The format to filter on. If this is empty - it will look for
     *            all non "known" asset types (ie "misc" stuff).
     * @param numRows
     *            The number of rows to return. -1 means all.
     * @param startRow
     *            The starting row number if paging - if numRows is -1 then this
     *            is ignored.
     * @deprecated by {@link #findAssetPage(AssetPageRequest)}
     */
    public TableDataResult listAssets(String packageUUID,
                                      String formats[],
                                      int skip,
                                      int numRows,
                                      String tableConfig) throws SerializationException;

    /**
     * Given a format, this will return assets that match. It can also be used
     * for "pagination" by passing in start and finish row numbers.
     * 
     * @param packageName
     *            The name of package to search inside.
     * @param format
     *            The format to filter on. If this is empty - it will look for
     *            all non "known" asset types (ie "misc" stuff).
     * @param numRows
     *            The number of rows to return. -1 means all.
     * @param startRow
     *            The starting row number if paging - if numRows is -1 then this
     *            is ignored.
     * @deprecated by {@link #findAssetPage(AssetPageRequest)}
     */
    public TableDataResult listAssetsWithPackageName(String packageName,
                                                     String formats[],
                                                     int skip,
                                                     int numRows,
                                                     String tableConfig) throws SerializationException;

    /**
     * Copies an asset into a new destination package.
     * 
     * @param assetUUID
     *            The source assetID.
     * @param newPackage
     *            The destination package (may be the same as the current source
     *            package, but in that case the asset has to have a different
     *            name).
     * @param newName
     *            The new name of the asset.
     */
    public String copyAsset(String assetUUID,
                            String newPackage,
                            String newName);

    /**
     * Prompt an asset into Global area.
     * 
     * @param assetUUID
     *            The source assetID.
     */
    public void promoteAssetToGlobalArea(String assetUUID);

    /**
     * This moves an asset to the given target package.
     */
    public void changeAssetPackage(String uuid,
                                   String newPackage,
                                   String comment);

    /**
     * Return a list of discussion items for a given asset...
     */
    public List<DiscussionRecord> loadDiscussionForAsset(String assetId);

    /**
     * Append a discussion item for the current user.
     */
    public List<DiscussionRecord> addToDiscussionForAsset(String assetId,
                                                          String comment);

    /** Only for admins, they can nuke it from orbit to clear it out */
    public void clearAllDiscussionsForAsset(String assetId);

    /**
     * This will change the state of an asset.
     * 
     * @param uuid
     *            The UUID of the asset we are tweaking.
     * @param newState
     *            The new state to set. It must be valid in the repo.
     */
    public void changeState(String uuid,
                            String newState);

    /**
     * This will change the state of package.
     * 
     * @param uuid
     *            The UUID of the asset we are tweaking.
     * @param newState
     *            The new state to set. It must be valid in the repo.
     */
    public void changePackageState(String uuid,
                                   String newStatee);

    /**
     * This will return the number of Assets matching the given criteria
     * 
     * @param request
     * @return
     */
    public long getAssetCount(AssetPageRequest request) throws SerializationException;

    /**
     * Convert an Asset to a target asset format
     * 
     * @param uuid
     * @param targetFormat
     * @return
     */
    public ConversionResult convertAsset(String uuid,
                                         String targetFormat) throws SerializationException;

}
