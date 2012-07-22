/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.repository;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.inject.Inject;

import org.drools.guvnor.backend.VFSService;
import org.drools.guvnor.vfs.impl.PathImpl;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.java.nio.file.Files;
import org.drools.java.nio.file.Path;
import org.drools.java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*import javax.jcr.*;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;*/

/**
 * A ModuleItem object aggregates a set of assets (for example, rules). This is
 * advantageous for systems using the JBoss Rules engine where the application
 * might make use of many related assets.
 * <p/>
 * A ModuleItem refers to module nodes within the RulesRepository. It contains
 * the "master copy" of assets (which may be linked into other module or other
 * types of containers). This is a container "node".
 */
public class ModuleItem extends VersionableItem {
    @Inject
    private VFSService vfsService;


    private static final Logger log = LoggerFactory.getLogger(ModuleItem.class);

    /**
     * This is the name of the asset "subfolder" where assets are kept for this
     * package.
     */
    public static final String ASSET_FOLDER_NAME = "assets";

    /**
     * The dublin core format attribute.
     */
    public static final String MODULE_FORMAT = "package";

    /**
     * The name of the module node type
     */
    public static final String MODULE_TYPE_NAME = "drools:packageNodeType";

    public static final String HEADER_PROPERTY_NAME = "drools:header";
    public static final String EXTERNAL_URI_PROPERTY_NAME = "drools:externalURI";
    public static final String CATEGORY_RULE_KEYS_PROPERTY_NAME = "categoryRuleKeys";
    public static final String CATEGORY_RULE_VALUES_PROPERTY_NAME = "categoryRuleValues";
    public static final String WORKSPACE_PROPERTY_NAME = "drools:workspace";
    public static final String DEPENDENCIES_PROPERTY_NAME = "drools:dependencies";

    private static final String COMPILED_PACKAGE_PROPERTY_NAME = "drools:compiledPackage";
    private final String BINARY_UP_TO_DATE = "drools:binaryUpToDate";

    /**
     * Constructs an object of type ModuleItem corresponding the specified
     * node
     *
     * @param rulesRepository the rulesRepository that instantiated this object
     * @param node            the node to which this object corresponds
     * @throws RulesRepositoryException
     */
    public ModuleItem(Path assetPath) throws RulesRepositoryException {
        super(assetPath);
        
/*        super(rulesRepository,
                node);*/

/*        try {
            //make sure this node is a module node
            if (!(this.node.getPrimaryNodeType().getName().equals(MODULE_TYPE_NAME) || isHistoricalVersion())) {
                String message = this.node.getName() + " is not a node of type " + MODULE_TYPE_NAME + ". It is a node of type: " + this.node.getPrimaryNodeType().getName();
                log.error(message);
                throw new RulesRepositoryException(message);
            }
        } catch (Exception e) {
            log.error("Caught exception: " + e);
            throw new RulesRepositoryException(e);
        }*/
    }

    ModuleItem() {
        super(null);
    }

    /**
     * Return the name of the module.
     */
    public String getName() {       
        return super.getName();
    }

    /**
     * @return true if this module is actually a snapshot.
     */
    public boolean isSnapshot() {
        //JLIU: what to do? it seems we dont need the concept of a physically existing area for snapshot anymore as we can always recreate the snapshot using git history
        return false;
        
/*        try {
            return (!this.rulesRepository.isNotSnapshot(this.node.getParent()));
        } catch (RepositoryException e) {
            throw new IllegalStateException(e);
        }*/
    }

    /**
     * Set this to indicate if the binary is up to date, or not.
     */
    public void updateBinaryUpToDate(boolean status) {
      //JLIU ? where is the compiled binary stored?
        
/*        try {
            checkIsUpdateable();
            this.checkout();
            node.setProperty(BINARY_UP_TO_DATE,
                    status);
        } catch (RepositoryException e) {
            log.error("fail to update drools:binaryUpToDate of " + getName(),
                    e);
        }*/
    }

    /**
     * Return true if the binary is "up to date".
     *
     * @return
     */
    public boolean isBinaryUpToDate() {
        //JLIU ? where is the compiled binary stored?
        return false;
        
/*        try {
            return this.node.hasProperty(BINARY_UP_TO_DATE) && node.getProperty(BINARY_UP_TO_DATE).getBoolean();
        } catch (RepositoryException e) {
            log.error("fail to get drools:binaryUpToDate of " + getName(),
                    e);
            throw new RulesRepositoryException(e);
        }*/
    }

    /**
     * returns the name of the snapshot, if this module is really a snapshot.
     * If it is not, it will just return the name of the module, so use wisely
     * !
     */
    public String getSnapshotName() {
    	//JLIU: TODO:
    	return null;
/*        try {
            return this.node.getName();
        } catch (RepositoryException e) {
            throw new RulesRepositoryException(e);
        }*/
    }

    
    //workspace is dreprecated
/*    *//**
     * @return the workspace this module belongs to.
     * @throws RulesRepositoryException
     *//*
    public String[] getWorkspaces() throws RulesRepositoryException {
        return getStringPropertyArray(WORKSPACE_PROPERTY_NAME);
    }

    *//**
     * This sets the Workspace
     *
     * @param workspace
     *//*
    public void updateWorkspace(String[] workspace) {
        this.updateStringArrayProperty(workspace,
                WORKSPACE_PROPERTY_NAME,
                false);
    }

    *//**
     * This adds a workspace
     *
     * @param workspace
     *//*
    public void addWorkspace(String workspace) {
        String[] existingWorkspaces = getStringPropertyArray(WORKSPACE_PROPERTY_NAME);
        boolean found = false;
        for (String existingWorkspace : existingWorkspaces) {
            if (existingWorkspace.equals(workspace)) {
                found = true;
                break;
            }
        }
        if (!found) {
            String[] newWorkspaces = new String[existingWorkspaces.length + 1];
            System.arraycopy(existingWorkspaces, 0, newWorkspaces, 0, existingWorkspaces.length);
            newWorkspaces[existingWorkspaces.length] = workspace;
            this.updateStringArrayProperty(newWorkspaces,
                    WORKSPACE_PROPERTY_NAME,
                    false);
        }
    }

    *//**
     * This removes a workspace
     *
     * @param workspace
     *//*
    public void removeWorkspace(String workspace) {
        String[] existingWorkspaces = getStringPropertyArray(WORKSPACE_PROPERTY_NAME);
        if (existingWorkspaces.length == 0) {
            return;
        }

        List<String> existingWorkspaceList = new ArrayList<String>(existingWorkspaces.length);
        Collections.addAll(existingWorkspaceList, existingWorkspaces);
        existingWorkspaceList.remove(workspace);
        if (existingWorkspaceList.size() != existingWorkspaces.length) {
            this.updateStringArrayProperty(existingWorkspaceList.toArray(new String[existingWorkspaceList.size()]),
                    WORKSPACE_PROPERTY_NAME,
                    false);
        }
    }
*/
    /**
     * Adds an asset to the current module with no category (not recommended !).
     * Without categories, its going to be hard to find rules later on (unless
     * modules are enough for you).
     */
    public AssetItem addAsset(String assetName,
                              String description) {
        //JLIU: Create an empty file under this dir first?
        
        return addAsset(assetName,
                description,
                null,
                null);
    }

    /**
     * This adds an asset to the current physical module (you can move it later).
     * With the given category.
     * <p/>
     * This will NOT check the asset in, just create the basic record.
     *
     * @param assetName       The name of the asset (the file name minus the extension)
     * @param description     A description of the asset.
     * @param initialCategory The initial category the asset is placed in (can belong to
     *                        multiple ones later).
     * @param format          The dublin core format (which also determines what editor is
     *                        used) - this is effectively the file extension.
     */
    public AssetItem addAsset(String assetName,
                              String description,
                              String initialCategory,
                              String format) {
        
        Path newAssetItemPath = Paths.get(assetPath.toString() + assetName);
        Files.createFile( newAssetItemPath, null );

        AssetItem asset = new AssetItem(newAssetItemPath);
        asset.updateDescription(description);
        asset.updateFormat(format);
        asset.addCategory(initialCategory);
        asset.updateState(StateItem.DRAFT_STATE_NAME);
        //TODO: Checkin comment
        Calendar lastModified = Calendar.getInstance();
/*        asset.setProperty(AssetItem.LAST_MODIFIED_PROPERTY_NAME,
                lastModified);*/
        if (initialCategory != null) {
            asset.addCategory(initialCategory);
        }
        return asset;
        
/*        Node assetNode;
        try {
            assetName = assetName.trim();
            Node assetsFolder = this.node.getNode(ASSET_FOLDER_NAME);
            String assetPath = NodeUtils.makeJSR170ComplaintName(assetName);
            assetNode = assetsFolder.addNode(assetPath,
                    AssetItem.ASSET_NODE_TYPE_NAME);
            assetNode.setProperty(AssetItem.TITLE_PROPERTY_NAME,
                    assetName);

            assetNode.setProperty(AssetItem.DESCRIPTION_PROPERTY_NAME,
                    description);
            if (format != null) {
                assetNode.setProperty(AssetItem.FORMAT_PROPERTY_NAME,
                        format);
            } else {
                assetNode.setProperty(AssetItem.FORMAT_PROPERTY_NAME,
                        AssetItem.DEFAULT_CONTENT_FORMAT);
            }

            assetNode.setProperty(VersionableItem.CHECKIN_COMMENT,
                    "Initial");

            Calendar lastModified = Calendar.getInstance();

            assetNode.setProperty(AssetItem.LAST_MODIFIED_PROPERTY_NAME,
                    lastModified);
            assetNode.setProperty(AssetItem.MODULE_NAME_PROPERTY,
                    this.getName());
            assetNode.setProperty(CREATOR_PROPERTY_NAME,
                    this.node.getSession().getUserID());

            rulesRepository.getSession().save();

            AssetItem asset = new AssetItem(this.rulesRepository,
                    assetNode);

            asset.updateState(StateItem.DRAFT_STATE_NAME);

            if (initialCategory != null) {
                asset.addCategory(initialCategory);
            }

            return asset;

        } catch (RepositoryException e) {
            if (e instanceof ItemExistsException) {
                throw new RulesRepositoryException("An asset of that name already exists in that module.",
                        e);
            } else {
                throw new RulesRepositoryException(e);
            }
        }*/

    }

    //JLIU: dont know what to do? how to implement symbolic with jgit?
    
    /**
     * This adds an asset which is imported from global area.
     * <p/>
     * This will NOT check the asset in, just create the basic record.
     *
     * @param sharedAssetName The name of the imported asset
     */
    public AssetItem addAssetImportedFromGlobalArea(String sharedAssetName) {
        return null;
        
/*        try {
            //assetName = assetName.trim();
            Node assetsFolder = this.node.getNode(ASSET_FOLDER_NAME);

            Session session = rulesRepository.getSession();
            Workspace workspace = session.getWorkspace();
            ModuleItem globalArea = rulesRepository.loadGlobalArea();
            AssetItem globalAssetItem = globalArea.loadAsset(sharedAssetName);
            
            ensureMixinType(globalAssetItem, "mix:shareable");

            String path = assetsFolder.getPath() + "/" + globalAssetItem.getName();
            workspace.clone(workspace.getName(),
                    globalAssetItem.getNode().getPath(),
                    path,
                    false);

            Node assetNode = assetsFolder.getNode(globalAssetItem.getName());

            return new AssetItem(this.rulesRepository,
                    assetNode);
        } catch (RepositoryException e) {
            if (e instanceof ItemExistsException) {
                throw new RulesRepositoryException("An asset of that name already exists in that module.",
                        e);
            } else {
                throw new RulesRepositoryException(e);
            }
        }
*/
    }

    //JLIU: no need anymore
/*    public static void ensureMixinType(AssetItem assetItem, String mixin)
            throws RepositoryException {
        if (!assetItem.getNode().isNodeType(mixin)) {
            assetItem.checkout();
            assetItem.getNode().addMixin(mixin);
            assetItem.checkin("add " + mixin);
        }
    }
    
    private boolean hasMixin(Node node) {
        try {
            NodeType[] nodeTypes = node.getMixinNodeTypes();
            for (NodeType nodeType : nodeTypes) {
                if (nodeType.isNodeType("mix:shareable")) {
                    return true;
                }
            }
        } catch (RepositoryException e) {

        }

        return false;
    }*/

    /**
     * This will permanently delete this module.
     */
    public void remove() {
    	//JLIU: TODO:

/*        checkIsUpdateable();
        try {
            log.info("USER:" + getCurrentUserName() + " REMOVEING module [" + getName() + "]");
            this.node.remove();
        } catch (RepositoryException e) {
            throw new RulesRepositoryException("Was not able to delete module.",
                    e);
        }*/
    }

    //JLIU: NO NEED
    /**
     * To avoid updating dependency attribute for every asset operation like
     * adding/renaming/deleting etc, we calculate dependency path on the fly.
     *
     * @return String[] The dependency path.
     */
    public String[] getDependencies() {
    	//JLIU: TODO:
    	return null;
    	
/*        Map<String, String> result = new HashMap<String, String>();
        try {
            Node content = getVersionContentNode();
            Iterator<AssetItem> assets = new AssetItemIterator(content.getNode(
                    ASSET_FOLDER_NAME).getNodes(),
                    this.rulesRepository);
            while (assets.hasNext()) {
                AssetItem asset = assets.next();

                result.put(asset.getName(),
                        encodeDependencyPath(
                                asset.getName(),
                                isHistoricalVersion() ? Long.toString(asset.getVersionNumber()) : "LATEST"));
            }
        } catch (RepositoryException e) {
            throw new RulesRepositoryException(e);
        }

        String[] existingDependencies = getStringPropertyArray(DEPENDENCIES_PROPERTY_NAME);
        for (String existingDependency : existingDependencies) {
            String path = decodeDependencyPath(existingDependency)[0];
            if (result.containsKey(path)) {
                result.put(path,
                        existingDependency);
            }
        }

        return result.values().toArray(new String[result.size()]);*/
    }

/*    public void updateDependency(String dependencyPath) {
        String[] existingDependencies = getStringPropertyArray(DEPENDENCIES_PROPERTY_NAME);
        boolean found = false;
        for (int i = 0; i < existingDependencies.length; i++) {
            if (decodeDependencyPath(existingDependencies[i])[0]
                    .equals(decodeDependencyPath(dependencyPath)[0])) {
                found = true;
                existingDependencies[i] = dependencyPath;
                this.updateStringArrayProperty(existingDependencies,
                        DEPENDENCIES_PROPERTY_NAME,
                        false);
                break;
            }
        }
        if (!found) {
            String[] newDependencies = new String[existingDependencies.length + 1];
            System.arraycopy(existingDependencies, 0, newDependencies, 0, existingDependencies.length);
            newDependencies[existingDependencies.length] = dependencyPath;
            this.updateStringArrayProperty(newDependencies,
                    DEPENDENCIES_PROPERTY_NAME,
                    false);
        }
    }

    public static String encodeDependencyPath(String dependencyPath,
                                              String dependencyVersion) {
        return dependencyPath + "?version=" + dependencyVersion;
    }

    public static String[] decodeDependencyPath(String dependencyPath) {
        if (dependencyPath.indexOf("?version=") >= 0) {
            return dependencyPath.split("\\?version=");
        } else {
            return new String[]{dependencyPath, "LATEST"};
        }
    }

*/    // The following should be kept for reference on how to add a reference that
    //is either locked to a version or follows head - FOR SHARING ASSETS
    //    /**
    //     * Adds a rule to the rule package node this object represents.  The reference to the rule
    //     * will optionally follow the head version of the specified rule's node or the specific
    //     * current version.
    //     *
    //     * @param ruleItem the ruleItem corresponding to the node to add to the rule package this
    //     *                 object represents
    //     * @param followRuleHead if true, the reference to the rule node will follow the head version
    //     *                       of the node, even if new versions are added. If false, will refer
    //     *                       specifically to the current version.
    //     * @throws RulesRepositoryException
    //     */
    //    public void addRuleReference(RuleItem ruleItem, boolean followRuleHead) throws RulesRepositoryException {
    //        try {
    //            ValueFactory factory = this.node.getSession().getValueFactory();
    //            int i = 0;
    //            Value[] newValueArray = null;
    //
    //            try {
    //                Value[] oldValueArray = this.node.getProperty(RULE_REFERENCE_PROPERTY_NAME).getValues();
    //                newValueArray = new Value[oldValueArray.length + 1];
    //
    //                for(i=0; i<oldValueArray.length; i++) {
    //                    newValueArray[i] = oldValueArray[i];
    //                }
    //            }
    //            catch(PathNotFoundException e) {
    //                //the property has not been created yet. do so now
    //                newValueArray = new Value[1];
    //            }
    //            finally {
    //                if(newValueArray != null) { //just here to make the compiler happy
    //                    if(followRuleHead) {
    //                        newValueArray[i] = factory.createValue(ruleItem.getNode());
    //                    }
    //                    else {
    //                        //this is the magic that ties it to a specific version
    //                        newValueArray[i] = factory.createValue(ruleItem.getNode().getBaseVersion());
    //                    }
    //                    this.node.checkout();
    //                    this.node.setProperty(RULE_REFERENCE_PROPERTY_NAME, newValueArray);
    //                    this.node.getSession().save();
    //                    this.node.checkin();
    //                }
    //                else {
    //                    throw new RulesRepositoryException("Unexpected null pointer for newValueArray");
    //                }
    //            }
    //        }
    //        catch(UnsupportedRepositoryOperationException e) {
    //            String message = "";
    //            try {
    //                message = "Error: Caught UnsupportedRepositoryOperationException when attempting to get base version for rule: " + ruleItem.getNode().getName() + ". Are you sure your JCR repository supports versioning? ";
    //                log.error(message + e);
    //            }
    //            catch (RepositoryException e1) {
    //                log.error("Caught exception: " + e1);
    //                throw new RulesRepositoryException(message, e1);
    //            }
    //            log.error("Caught exception: " + e);
    //            throw new RulesRepositoryException(e);
    //        }
    //        catch(Exception e) {
    //            log.error("Caught exception: " + e);
    //            throw new RulesRepositoryException(e);
    //        }
    //    }

    //MN: The following should be kept as a reference on how to remove a version tracking reference
    //as a compliment to the above method (which is also commented out !).
    //    /**
    //     * Removes the specified rule from the rule package node this object represents.
    //     *
    //     * @param ruleItem the ruleItem corresponding to the node to remove from the rule package
    //     *                 this object represents
    //     * @throws RulesRepositoryException
    //     */
    //    public void removeRuleReference(AssetItem ruleItem) throws RulesRepositoryException {
    //        try {
    //            Value[] oldValueArray = this.node.getProperty( RULE_REFERENCE_PROPERTY_NAME ).getValues();
    //            Value[] newValueArray = new Value[oldValueArray.length - 1];
    //
    //            boolean wasThere = false;
    //
    //            int j = 0;
    //            for ( int i = 0; i < oldValueArray.length; i++ ) {
    //                Node ruleNode = this.node.getSession().getNodeByUUID( oldValueArray[i].getString() );
    //                AssetItem currentRuleItem = new AssetItem( this.rulesRepository,
    //                                                         ruleNode );
    //                if ( currentRuleItem.equals( ruleItem ) ) {
    //                    wasThere = true;
    //                } else {
    //                    newValueArray[j] = oldValueArray[i];
    //                    j++;
    //                }
    //            }
    //
    //            if ( !wasThere ) {
    //                return;
    //            } else {
    //                this.node.checkout();
    //                this.node.setProperty( RULE_REFERENCE_PROPERTY_NAME,
    //                                       newValueArray );
    //                this.node.getSession().save();
    //                this.node.checkin();
    //            }
    //        } catch ( PathNotFoundException e ) {
    //            //the property has not been created yet.
    //            return;
    //        } catch ( Exception e ) {
    //            log.error( "Caught exception",
    //                       e );
    //            throw new RulesRepositoryException( e );
    //        }
    //    }

    //MN: This should be kept as a reference for
    //    /**
    //     * Gets a list of RuleItem objects for each rule node in this rule package
    //     *
    //     * @return the List object holding the RuleItem objects in this rule package
    //     * @throws RulesRepositoryException
    //     */
    //    public List getRules() throws RulesRepositoryException {
    //        try {
    //            Value[] valueArray = this.node.getProperty(RULE_REFERENCE_PROPERTY_NAME).getValues();
    //            List returnList = new ArrayList();
    //
    //            for(int i=0; i<valueArray.length; i++) {
    //                Node ruleNode = this.node.getSession().getNodeByUUID(valueArray[i].getString());
    //                returnList.add(new RuleItem(this.rulesRepository, ruleNode));
    //            }
    //            return returnList;
    //        }
    //        catch(PathNotFoundException e) {
    //            //the property has not been created yet.
    //            return new ArrayList();
    //        }
    //        catch(Exception e) {
    //            log.error("Caught exception: " + e);
    //            throw new RulesRepositoryException(e);
    //        }
    //    }

    /**
     * Return an iterator for the rules in this module
     */
    public Iterator<AssetItem> getAssets() {
    	//JLIU: TODO:
    	return null;
/*    	
        try {
            Node content = getVersionContentNode();
            return new VersionedAssetItemIterator(content.getNode(ASSET_FOLDER_NAME).getNodes(),
                    this.rulesRepository,
                    this.getDependencies());
        } catch (RepositoryException e) {
            throw new RulesRepositoryException(e);
        }*/

    }
    
    //JLIU: What to do with query?

    /**
     * This will query any assets stored under this module. For example, you
     * can pass in <code>"drools:format = 'drl'"</code> to get a list of only a
     * certain type of asset.
     *
     * @param fieldPredicates A predicate string (SQL style).
     * @return A list of matches.
     */
    public AssetItemIterator queryAssets(String fieldPredicates,
                                         boolean seekArchived) {
    	//JLIU: TODO:
    	return null;
    	
/*        try {
            String sql;
            if (isHistoricalVersion()) {
                sql = "SELECT * FROM nt:frozenNode";
            } else {
                sql = "SELECT * FROM " + AssetItem.ASSET_NODE_TYPE_NAME;
            }

            sql += " WHERE jcr:path LIKE '" + getVersionContentNode().getPath() + "/" + ASSET_FOLDER_NAME + "[%]/%'";
            if (fieldPredicates.length() > 0) {
                sql += " and " + fieldPredicates;

            }

            if (!seekArchived) {
                sql += " AND " + AssetItem.CONTENT_PROPERTY_ARCHIVE_FLAG + " = 'false'";
            }

            sql += " ORDER BY " + AssetItem.TITLE_PROPERTY_NAME;
            
            //Adding this explicit order by ensures NodeIterator.getSize() returns a value other than -1.
            //See http://markmail.org/message/mxmk5hkxrdtcc3hl
            sql += ", jcr:score DESC";

            Query q = node.getSession().getWorkspace().getQueryManager().createQuery(sql,
                    Query.SQL);

            long time = System.currentTimeMillis();
            QueryResult res = q.execute();

            NodeIterator it = res.getNodes();
            long taken = System.currentTimeMillis() - time;
            if (taken > 2000) {
                log.debug("QueryExec time is: " + (System.currentTimeMillis() - time));
                log.debug("SQL is " + sql);
                log.debug(it.getClass().getName());
            }

            //return new AssetItemIterator(it, this.rulesRepository);
            return new VersionedAssetItemIterator(it,
                    this.rulesRepository,
                    this.getDependencies());
        } catch (RepositoryException e) {
            throw new RulesRepositoryException(e);
        }
*/    }

    public AssetItemIterator queryAssets(String fieldPredicates) {
        return queryAssets(fieldPredicates,
                false);
    }

    public AssetItemIterator listArchivedAssets() {
        return queryAssets(AssetItem.CONTENT_PROPERTY_ARCHIVE_FLAG + " = 'true'",
                true);
    }

    public AssetItemIterator listAssetsByFormat(List<String> formatInList) {
        return listAssetsByFormat(formatInList.toArray(new String[formatInList.size()]));
    }

    /**
     * @return The header contents as pertains to a package of rule assets.
     */
    //    public String getHeader() {
    //        return this.getStringProperty( HEADER_PROPERTY_NAME );
    //    }

    //    public void updateHeader(String header) {
    //        updateStringProperty( header, HEADER_PROPERTY_NAME );
    public AssetItemIterator listAssetsWithVersionsSpecifiedByDependenciesByFormat(String... assetFormats) {
        AssetItemIterator assetItemIterator = listAssetsByFormat(assetFormats);
        ((VersionedAssetItemIterator) assetItemIterator).setReturnAssetsWithVersionsSpecifiedByDependencies(true);
        return assetItemIterator;
    }

    /**
     * This will load an iterator for assets of the given format type.
     */
    public AssetItemIterator listAssetsByFormat(String... formats) {
    	//JLIU: We do listAssetsByFormat manually until we figure out how to do query with jgit. 
    	
    	StringBuffer globPattern = new StringBuffer();
    	for(String format : formats) {
    		globPattern.append("*." + format);
    		globPattern.append(",");
    	}

    	//TODO: in order to use VFSService, we have to convert nio.path to vfs.path again? 
    	PathImpl vfsPath = new PathImpl(assetPath.toString());
    	DirectoryStream<org.drools.guvnor.vfs.Path> directoryStream = vfsService.newDirectoryStream(vfsPath, globPattern.toString());
    	List<org.drools.java.nio.file.Path> assetItemPaths = new ArrayList<org.drools.java.nio.file.Path>();
    	
    	for ( final org.drools.guvnor.vfs.Path path : directoryStream ) {
    		org.drools.java.nio.file.Path nioPath = fromPath(path);
    		assetItemPaths.add(nioPath);
    	}
    	
    	return new AssetItemIterator(assetItemPaths);

/*        if (formats.length == 1) {
            return queryAssets(FORMAT_PROPERTY_NAME + "='" + formats[0] + "'");
        } else {
            StringBuilder predicateBuilder = new StringBuilder(" ( ");
            for (int i = 0; i < formats.length; i++) {
                predicateBuilder.append(FORMAT_PROPERTY_NAME).append("='").append(formats[i]).append("'");
                if (i != formats.length - 1) {
                    predicateBuilder.append(" OR ");
                }
            }
            predicateBuilder.append(" ) ");
            return queryAssets(predicateBuilder.toString());
        }*/
    }
    private org.drools.java.nio.file.Path fromPath(final org.drools.guvnor.vfs.Path path) {
        //HACK: REVISIT: how to encode. We dont want to encode the whole URI string, we only want to encode the path element
        String pathString = path.toURI();
        pathString = pathString.replaceAll(" ", "%20");
        return Paths.get(URI.create(pathString));
    }
    public AssetItemIterator listAssetsNotOfFormat(String[] formats) {
        if (formats.length == 1) {
            return queryAssets("not drools:format='" + formats[0] + "'");
        } else {
            StringBuilder predicateBuilder = new StringBuilder("not ( ");
            for (int i = 0; i < formats.length; i++) {
                predicateBuilder.append("drools:format='").append(formats[i]).append("'");
                if (!(i == formats.length - 1)) {
                    predicateBuilder.append(" OR ");
                }
            }
            predicateBuilder.append(" ) ");
            return queryAssets(predicateBuilder.toString());
        }
    }

    /**
     * Load a specific asset by name.
     */
    public AssetItem loadAsset(String name) {        
        Path newAssetItemPath = Paths.get(assetPath.toString() + name);
        AssetItem asset = new AssetItem(newAssetItemPath);
        return asset;
        
        
/*        try {
            Node content = getVersionContentNode();
            return new AssetItem(
                    this.rulesRepository,
                    content.getNode(ASSET_FOLDER_NAME).getNode(name));
        } catch (RepositoryException e) {
            throw new RulesRepositoryException(e);
        }*/
    }

    /**
     * Load a specific asset by name.
     */
    public AssetItem loadAsset(String name, long versionNumber) {
        AssetItem asset = loadAsset(name);

        AssetHistoryIterator it = asset.getHistory();
        while (it.hasNext()) {
            AssetItem historical = it.next();
            long version = historical.getVersionNumber();
            if (version == versionNumber) {
                return historical;
            }
        }
        throw new RulesRepositoryException(
                "Unable to load asset [" + name + "] with version[" + versionNumber + "]");
    }

    /**
     * Returns true if this module contains an asset of the given name.
     */
    public boolean containsAsset(String name) {
    	//JLIU: TODO:
    	return false;
    	
/*        Node content;
        try {
            content = getVersionContentNode();
            return content.getNode(ASSET_FOLDER_NAME).hasNode(name);
        } catch (RepositoryException e) {
            throw new RulesRepositoryException(e);
        }*/
    }

    /**
     * Nicely formats the information contained by the node that this object
     * encapsulates
     */
    public String toString() {
    	//JLIU: TODO:
    	return null;
    	
/*        try {
            return "Content of the module named " + this.node.getName() + ":"
                    + "Description: " + this.getDescription() + "\n"
                    + "Format: " + this.getFormat() + "\n"
                    + "Last modified: " + this.getLastModified() + "\n"
                    + "Title: " + this.getTitle() + "\n"
                    + "----\n";
        } catch (Exception e) {
            log.error("Caught Exception",
                    e);
            return "";
        }*/
    }

    /**
     * @return An iterator over the nodes history.
     */
    public ModuleHistoryIterator getHistory() {
    	//JLIU: TODO:
    	return null;
    	
/*        return new ModuleHistoryIterator(this.rulesRepository,
                this.node);*/
    }

    @Override
    public ModuleItem getPrecedingVersion() throws RulesRepositoryException {
        //JLIU: TODO, get precedingVersion from jgit. 
        return null;
/*        try {
            Node precedingVersionNode = this.getPrecedingVersionNode();
            if (precedingVersionNode != null) {
                return new ModuleItem(this.rulesRepository,
                        precedingVersionNode);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Caught exception",
                    e);
            throw new RulesRepositoryException(e);
        }*/
    }

    @Override
    public ModuleItem getSucceedingVersion() throws RulesRepositoryException {
        //JLIU: TODO, get precedingVersion from jgit. 
        return null;
        
/*        try {
            Node succeedingVersionNode = this.getSucceedingVersionNode();
            if (succeedingVersionNode != null) {
                return new ModuleItem(this.rulesRepository,
                        succeedingVersionNode);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Caught exception",
                    e);
            throw new RulesRepositoryException(e);
        }*/
    }

    /**
     * This will return a list of assets for a given state. It works through the
     * assets that belong to this module, and if they are not in the correct
     * state, walks backwards until it finds one in the correct state.
     * <p/>
     * If it walks all the way back up the versions looking for the "latest"
     * version with the appropriate state, and can't find one, that asset is not
     * included in the result.
     * <p/>
     * This will exclude any items that have the "ignoreState" set (so for
     * example, retired items, invalid items etc).
     *
     * @param state       The state of assets to retrieve.
     * @param ignoreState The statuses to not include in the results (it will look at
     *                    the status of the latest one).
     */
    public Iterator<AssetItem> getAssetsWithStatus(final StateItem state,
                                                   final StateItem ignoreState) {
        List<AssetItem> result = new LinkedList<AssetItem>();
        for (Iterator<AssetItem> rules = getAssets(); rules.hasNext(); ) {
            AssetItem head = rules.next();
            if (head.sameState(state)) {
                result.add(head);
            } else if (head.sameState(ignoreState)) {
                //ignore this one
            } else {
                List<AssetItem> fullHistory = new LinkedList<AssetItem>();
                for (Iterator<AssetItem> iter = head.getHistory(); iter.hasNext(); ) {
                    AssetItem element = iter.next();
                    if (!(element.getVersionNumber() == 0)) {
                        fullHistory.add(element);
                    }
                }

                sortHistoryByVersionNumber(fullHistory);

                for (AssetItem prevRule : fullHistory) {
                    if (prevRule.sameState(state)) {
                        result.add(prevRule);
                        break;
                    }
                }
            }
        }
        return result.iterator();
    }

    void sortHistoryByVersionNumber(List<AssetItem> fullHistory) {
        Collections.sort(fullHistory,
                new Comparator<AssetItem>() {
                    public int compare(AssetItem a1,
                                       AssetItem a2) {
                        long la1 = a1.getVersionNumber();
                        long la2 = a2.getVersionNumber();
                        return la1 == la2 ? 0 : (la1 < la2 ? 1 : -1);
                    }
                });
    }

    /**
     * This will return a list of assets for a given state. It works through the
     * assets that belong to this module, and if they are not in the correct
     * state, walks backwards until it finds one in the correct state.
     * <p/>
     * If it walks all the way back up the versions looking for the "latest"
     * version with the appropriate state, and can't find one, that asset is not
     * included in the result.
     */
    public Iterator<AssetItem> getAssetsWithStatus(final StateItem state) {
        return getAssetsWithStatus(state,
                null);
    }

    /**
     * @return The external URI which will be used to sync this module to an
     *         external resource. Generally this will resolve to a directory in
     *         (for example) Subversion - with each asset being a file (with the
     *         format property as the file extension).
     */
    public String getExternalURI() {
        return this.getStringProperty(EXTERNAL_URI_PROPERTY_NAME);
    }

    //    }

    public void updateExternalURI(String uri) {
        updateStringProperty(uri,
                EXTERNAL_URI_PROPERTY_NAME);
    }

    public void setCatRules(String map) {
        updateStringProperty(map,
                CATEGORY_RULE_KEYS_PROPERTY_NAME);
    }

    public void updateCategoryRules(String keys,
                                    String values) throws RulesRepositoryException {
        //System.out.println("(updateCategoryRules) keys: " + keys + " Values: " + values );
        try {

            this.checkout();
            this.updateStringProperty(keys,
                    CATEGORY_RULE_KEYS_PROPERTY_NAME);
            this.updateStringProperty(values,
                    CATEGORY_RULE_VALUES_PROPERTY_NAME);

        } catch (Exception e) {
            log.error("Caught Exception",
                    e);
            throw new RulesRepositoryException(e);
        }
    }

    private static HashMap<String, String> convertFromObjectGraphs(final String[] keys,
                                                                   final String[] values) {
        HashMap<String, String> hash = new HashMap<String, String>();

        for (int i = 0; i < keys.length; i++) {
            hash.put(keys[i],
                    values[i]);
        }
        return hash;
    }

    public String[] convertStringToArray(String tagName) {
        //System.out.println("(convertStringToArray) Tags: " + tagName);
        List<String> list = new ArrayList<String>();

        StringTokenizer tok = new StringTokenizer(tagName,
                ",");
        while (tok.hasMoreTokens()) {
            String currentTagName = tok.nextToken();
            list.add(currentTagName);
        }

        return list.toArray(new String[0]);

    }

    public HashMap<String, String> getCategoryRules() {
        return convertFromObjectGraphs(convertStringToArray(getCategoryRules(true)),
                convertStringToArray(getCategoryRules(false)));
    }

    public String getCategoryRules(boolean keys) {
        if (keys) {
            return getStringProperty(CATEGORY_RULE_KEYS_PROPERTY_NAME);
        }
        return getStringProperty(CATEGORY_RULE_VALUES_PROPERTY_NAME);
    }

    /**
     * Update the checkin comment.
     */
    public void updateCheckinComment(String comment) {
        updateStringProperty(comment,
                VersionableItem.CHECKIN_COMMENT);
    }

    /**
     * This will change the status of this module, and all the contained
     * assets. No new versions are created of anything.
     *
     * @param newState The status tag to change it to.
     */
    public void changeStatus(String newState) {
        //JLIU: TODO

/*        StateItem stateItem = rulesRepository.getState(newState);
        updateState(stateItem);
        for (Iterator<AssetItem> iter = getAssets(); iter.hasNext(); ) {
            iter.next().updateState(stateItem);
        }*/
    }

    public ModuleItem updateCompiledBinary(InputStream data) {
        //JLIU: TODO
        return null;
/*        checkout();
        try {
            Binary binary = this.node.getSession().getValueFactory().createBinary(data);
            this.node.setProperty(COMPILED_PACKAGE_PROPERTY_NAME,
                    binary);
            this.node.setProperty(LAST_MODIFIED_PROPERTY_NAME,
                    Calendar.getInstance());
            return this;
        } catch (RepositoryException e) {
            log.error("Unable to update the assets binary content",
                    e);
            throw new RulesRepositoryException(e);
        }*/
    }

    /**
     * This is a convenience method for returning the binary data as a byte
     * array.
     */
    public byte[] getCompiledBinaryBytes() {
        //JLIU: Where to store compiled binary? (suppose its a jar/zip file?)
    	return null;
    	
/*        try {
            Node ruleNode = getVersionContentNode();
            if (ruleNode.hasProperty(COMPILED_PACKAGE_PROPERTY_NAME)) {
                Property data = ruleNode.getProperty(COMPILED_PACKAGE_PROPERTY_NAME);
                InputStream in = data.getBinary().getStream();

                // Create the byte array to hold the data
                byte[] bytes = new byte[(int) data.getLength()];

                // Read in the bytes
                int offset = 0;
                int numRead = 0;
                while (offset < bytes.length
                        && (numRead = in.read(bytes,
                        offset,
                        bytes.length - offset)) >= 0) {
                    offset += numRead;
                }

                // Ensure all the bytes have been read in
                if (offset < bytes.length) {
                    throw new RulesRepositoryException("Could not completely read binary package for " + getName());
                }

                // Close the input stream and return bytes
                in.close();
                return bytes;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage(),
                    e);
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RulesRepositoryException(e);
        }*/
    }

    /**
     * Creates a nested package.
     */
    public ModuleItem createSubModule(String subModuleName) /*throws RepositoryException*/ {
        //JLIU: do we still support submodule?
        return null;
/*
        this.checkout();
        log.info("USER: {} CREATEING submodule [{}] under [{}]",
                new Object[]{getCurrentUserName(), subModuleName, getName()});
        Node subModulesNode;
        try {
            subModulesNode = node.getNode(RulesRepository.MODULE_AREA);
        } catch (PathNotFoundException e) {
            subModulesNode = node.addNode(RulesRepository.MODULE_AREA,
                    "nt:folder");
        }
        //        subPkgsNode.checkout();
        String assetPath = NodeUtils.makeJSR170ComplaintName(subModuleName);
        Node ruleSubPackageNode = subModulesNode.addNode(assetPath,
                ModuleItem.MODULE_TYPE_NAME);

        ruleSubPackageNode.addNode(ModuleItem.ASSET_FOLDER_NAME,
                "drools:versionableAssetFolder");

        ruleSubPackageNode.setProperty(ModuleItem.TITLE_PROPERTY_NAME,
                subModuleName);

        ruleSubPackageNode.setProperty(AssetItem.DESCRIPTION_PROPERTY_NAME,
                "");
        ruleSubPackageNode.setProperty(AssetItem.FORMAT_PROPERTY_NAME,
                ModuleItem.MODULE_FORMAT);
        ruleSubPackageNode.setProperty(ModuleItem.CREATOR_PROPERTY_NAME,
                this.rulesRepository.getSession().getUserID());
        Calendar lastModified = Calendar.getInstance();
        ruleSubPackageNode.setProperty(ModuleItem.LAST_MODIFIED_PROPERTY_NAME,
                lastModified);
        ruleSubPackageNode.setProperty(ModuleItem.CONTENT_PROPERTY_ARCHIVE_FLAG,
                false);

        return new ModuleItem(this.rulesRepository,
                ruleSubPackageNode);*/
    }

    /**
     * Returns a {@link ModuleIterator} of its children
     *
     * @return a {@link ModuleIterator} of its children
     */
    public ModuleIterator listSubModules() {
    	//JLIU: TODO:
    	return null;
    	
/*        try {
            return new ModuleIterator(getRulesRepository(),
                    node.getNode(RulesRepository.MODULE_AREA).getNodes());
        } catch (PathNotFoundException e) {
            return new ModuleIterator();
        } catch (RepositoryException e) {
            throw new RulesRepositoryException(e);
        }*/
    }

    private String getCurrentUserName() {
    	//JLIU: TODO:
    	return null;
/*    	
        return this.rulesRepository.getSession().getUserID();*/
    }
}
