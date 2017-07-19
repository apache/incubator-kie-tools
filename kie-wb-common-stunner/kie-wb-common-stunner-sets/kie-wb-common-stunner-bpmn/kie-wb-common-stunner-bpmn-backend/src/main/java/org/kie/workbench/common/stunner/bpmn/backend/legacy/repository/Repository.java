/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.legacy.repository;

import java.util.Collection;

import org.uberfire.java.nio.file.NoSuchFileException;

/**
 * Repository is responsible for managing its components that are as follows:
 * <ul>
 * <li>Asset - component that can be of any type and is stored in a custom location</li>
 * </ul>
 */
public interface Repository {

    /**
     * Returns name used to identify this repository.
     */
    public String getName();

    /**
     * Retrieves all directories stored under <code>startAt</code> location.
     * NOTE: Directory should be always relative to the repository root
     * @param startAt - location where directories should be fetched from
     * @return - list of directories
     */
    Collection<Directory> listDirectories(String startAt);

    /**
     * Retrieves all directories stored under <code>startAt</code> location including all sub folders.
     * NOTE: Directory should be always relative to the repository root
     * @param startAt - location where directories should be fetched from
     * @param filter - filter that allows to narrow the results
     * @return - list of assets found
     */
    Collection<Asset> listAssetsRecursively(String startAt,
                                            Filter filter);

    /**
     * Stores new directory in given location, in case of sub folders existence in the location
     * all sub folders are created as well.
     * @param location - location in the repository to be created
     * @return - returns identifier of the new directory
     */
    Directory createDirectory(String location);

    /**
     * Examines repository if given directory exists in the repository
     * NOTE: Directory should be always relative to the repository root
     * @param directory - directory to check
     * @return - true if and only if given directory exists
     */
    boolean directoryExists(String directory);

    /**
     * Deletes directory from repository including its content
     * NOTE: Directory should be always relative to the repository root
     * @param directory - directory to be deleted
     * @param failIfNotEmpty - indicates if delete operation should fail in case given directory is not empty
     */
    boolean deleteDirectory(String directory,
                            boolean failIfNotEmpty);

    /**
     * Copy directory given by <code>uniqueId</code> into destination given by <code>location</code>
     * @param sourceDirectory - source directory path relative to repository root
     * @param location - destination where directory will be copied to
     * @return - true when copy operation was successful otherwise false
     */
    boolean copyDirectory(String sourceDirectory,
                          String location);

    /**
     * Moves directory given by <code>uniqueId</code> into destination given by <code>location</code>
     * and renames it with given <code>name</code>
     * @param sourceDirectory - source directory path relative to repository root
     * @param location - final destination where directory should be moved to
     * @param name - name of the directory after move, if null is given name is not changed
     * @return - returns true if move operation was successful otherwise false
     */
    boolean moveDirectory(String sourceDirectory,
                          String location,
                          String name);

    /**
     * Retrieves all assets stored in the given location.
     * NOTE: This will not load the actual content of the asset but only its meta data
     * @param location - location that assets should be collected from
     * @return - list of available assets
     */
    Collection<Asset> listAssets(String location);

    /**
     * Retrieves all assets stored in the given location.
     * NOTE: This will not load the actual content of the asset but only its meta data
     * @param location - location that assets should be collected from
     * @param filter - allows to defined filter criteria to fetch only assets of interest
     * @return - list of available assets
     */
    Collection<Asset> listAssets(String location,
                                 Filter filter);

    /**
     * Loads an asset given by the <code>assetUniqueId</code> including actual content of the asset.
     * @param assetUniqueId - unique identifier of the asset to load
     * @return return loaded asset including content
     * @throws NoSuchFileException - throws in case of asset given by id does not exist
     */
    Asset loadAsset(String assetUniqueId) throws NoSuchFileException;

    /**
     * Loads an asset given by the <code>path</code> including actual content of the asset.
     * @param path - complete path of the asset to load (relative to the repository root)
     * @return return loaded asset including content
     * @throws NoSuchFileException - throws in case of asset given by id does not exist
     */
    Asset loadAssetFromPath(String path) throws NoSuchFileException;

    /**
     * Stores given asset in the repository. <code>asset</code> need to have all meta data and content available
     * for the operation to successfully complete.
     * @param asset - asset to be stored
     * @return returns asset unique identifier that can be used to locate it
     */
    String createAsset(Asset asset);

    /**
     * Updates content of the asset
     * @param asset - asset to be stored with new content in it, all other data (like name, location) should be same
     * @return - returns uniqueId of the asset
     * @throws NoSuchFileException - throws in case of asset given by id does not exist
     */
    String updateAsset(Asset asset,
                       String commitMessage,
                       String sessionId) throws NoSuchFileException;

    /**
     * Deletes asset from repository identified by <code>assetUniqueId</code> if exists
     * @param assetUniqueId - unique identifier of the asset
     * @return return true if and only if operation completed successfully otherwise false
     */
    boolean deleteAsset(String assetUniqueId);

    /**
     * Deletes asset from repository given by the <code>path</code> if exists
     * @param path - complete path of the asset to delete
     * @return return true if and only if operation completed successfully otherwise false
     */
    boolean deleteAssetFromPath(String path);

    /**
     * Examines repository if asset given by the <code>assetUniqueId</code> exists
     * @param assetUniqueId - unique identifier of the asset
     * @return true if and only if asset exists otherwise false
     */
    boolean assetExists(String assetUniqueId);

    /**
     * Copy asset given by <code>uniqueId</code> into destination given by <code>location</code>
     * @param uniqueId - source asset unique id
     * @param location - destination where asset will be copied to
     * @return - true when copy operation was successful otherwise false
     */
    boolean copyAsset(String uniqueId,
                      String location);

    /**
     * Moves asset given by <code>uniqueId</code> into destination given by <code>location</code>
     * and renames it with given <code>name</code>
     * @param uniqueId - source asset unique id
     * @param location - final destination where asset should be moved to
     * @param name - name of the asset after move, if null is given name is not changed
     * @return - returns true if move operation was successful otherwise false
     */
    boolean moveAsset(String uniqueId,
                      String location,
                      String name);
}
