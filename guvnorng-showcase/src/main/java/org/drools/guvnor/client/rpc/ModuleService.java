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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.SerializationException;

@RemoteServiceRelativePath("moduleService")
public interface ModuleService
        extends
        RemoteService {

    /**
     * This returns a list of modules where assets may be added. Only the UUID
     * and the name need to be populated.
     */
    public Module[] listModules();

    /**
     * This returns a list of modules where assets may be added. Only the UUID
     * and the name need to be populated.
     */
    public Module[] listModules(String workspace);

    /**
     * This returns a list of archived modules.
     */
    public Module[] listArchivedModules();

    /**
     * This returns the global module.
     */
    public Module loadGlobalModule();

    public SnapshotInfo loadSnapshotInfo(String packageName, String snapshotName);

    /**
     * This creates a module of the given name, and checks it in.
     *
     * @return UUID of the created item.
     */
    public String createModule(String name,
                                String description,
                                String format) throws SerializationException;

    /**
     * This creates a module of the given name, and checks it in.
     *
     * @return UUID of the created item.
     */
    public String createSubModule(String name,
                                   String description,
                                   String parentPackage) throws SerializationException;

    /**
     * Loads a module by its uuid.
     *
     * @return Well, its pretty obvious if you think about it for a minute.
     *         Really.
     */
    public Module loadModule(String uuid);

    /**
     * Saves the module in place (does not create a new version of
     * anything).
     *
     * @return A ValidatedReponse, with any errors to be reported. No payload is
     *         in the response. If there are any errors, the user should be
     *         given the option to review them, and correct them if needed (but
     *         a save will not be prevented this way - as its not an exception).
     */
    public void saveModule(Module data) throws SerializationException;

    /**
     * Create a module snapshot for deployment.
     *
     * @param moduleName     THe name of the module to copy.
     * @param snapshotName    The name of the snapshot. Has to be unique unless existing one
     *                        is to be replaced.
     * @param replaceExisting Replace the existing one (must be true to replace an existing
     *                        snapshot of the same name).
     * @param comment         A comment to be added to the copied one.
     */
    public void createModuleSnapshot(String moduleName,
                                      String snapshotName,
                                      boolean replaceExisting,
                                      String comment);

    /**
     * This alters an existing snapshot, it can be used to copy or delete it.
     *
     * @param moduleName     The module name that we are dealing with.
     * @param snapshotName    The snapshot name (this must exist)
     * @param delete          true if the snapshotName is to be removed.
     * @param newSnapshotName The name of the target snapshot that the contents will be
     *                        copied to.
     */
    public void copyOrRemoveSnapshot(String moduleName,
                                     String snapshotName,
                                     boolean delete,
                                     String newSnapshotName) throws SerializationException;

    /**
     * Build the package (may be a snapshot) and return the result.
     * <p/>
     * This will then store the result in the package as an attachment.
     * <p/>
     * if a non null selectorName is passed in it will lookup a selector as
     * configured in the systems selectors.properties file. This will then apply
     * the filter to the package being built.
     */
    public BuilderResult buildPackage(String packageUUID,
                                      boolean force,
                                      String buildMode,
                                      String operator,
                                      String statusDescriptionValue,
                                      boolean enableStatusSelector,
                                      String categoryOperator,
                                      String category,
                                      boolean enableCategorySelector,
                                      String customSelectorName) throws SerializationException;

    /**
     * This will return the effective DRL for a package. This would be the
     * equivalent if all the rules were written by hand in the one file. It may
     * not actually be compiled this way in the implementation, so this is for
     * display and debugging assistance only.
     * <p/>
     * It should still generate
     *
     * @throws SerializationException
     */
    public String buildModuleSource(String packageUUID) throws SerializationException;

    /**
     * Copy the module (everything).
     *
     * @param sourceModuleName
     * @param destModuleName
     */
    public String copyModule(String sourceModuleName,
                              String destModuleName) throws SerializationException;

    /**
     * Permanently remove a module (delete it).
     *
     * @param uuid of the module.
     */
    public void removeModule(String uuid);

    /**
     * Rename a module.
     */
    public String renameModule(String uuid,
                                String newName);

    /**
     * This will force a rebuild of all snapshots binary data. No errors are
     * expected, as there will be no change. If there are errors, an expert will
     * need to look at them.
     */
    public void rebuildSnapshots() throws SerializationException;

    /**
     * This will force a rebuild of all packages binary data. No errors are
     * expected, as there will be no change. If there are errors, an expert will
     * need to look at them.
     */
    public void rebuildPackages() throws SerializationException;

    /**
     * This will list the rules available in a package. This has an upper limit
     * of what it will return (it just doesn't make sense to show a list of 20K
     * items !).
     */
    public String[] listRulesInPackage(String packageName) throws SerializationException;

    /**
     * This will list the images available in a module. This has an upper limit
     * of what it will return (it just doesn't make sense to show a list of 20K
     * items !).
     */
    public String[] listImagesInModule(String packageName) throws SerializationException;

    /**
     * This will load a list of snapshots for the given module. Snapshots are
     * created by taking a labelled copy of a module, at a point in time, for
     * instance for deployment.
     */
    public SnapshotInfo[] listSnapshots(String moduleName);

    /**
     * List the fact types (class names) in the scope of a given package. This
     * may not include things on the "system" classpath, but only things
     * specifically scoped to the package (eg in jars that have been uploaded to
     * it as an asset).
     */
    public String[] listTypesInPackage(String packageUUID) throws SerializationException;

    /**
     * Installs the sample repository, wiping out what was already there.
     * Generally shouldn't call this unless it is new !
     */
    public void installSampleRepository() throws SerializationException;

    /**
     * Compare two snapshots.
     *
     * @deprecated in favour of {@link compareSnapshots(SnapshotComparisonRequest)}
     */
    public SnapshotDiffs compareSnapshots(String moduleName,
                                          String firstSnapshotName,
                                          String secondSnapshotName);

    public SnapshotComparisonPageResponse compareSnapshots(SnapshotComparisonPageRequest request);



    public void updateDependency(String uuid, String dependencyPath);

    public String[] getDependencies(String uuid);


}
