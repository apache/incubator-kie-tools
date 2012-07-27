/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.server.impl;

import static org.drools.guvnor.server.util.ClassicDRLImporter.getRuleName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageRow;
import org.drools.guvnor.client.rpc.SnapshotDiff;
import org.drools.guvnor.client.rpc.SnapshotDiffs;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.drools.guvnor.server.builder.ModuleAssembler;
import org.drools.guvnor.server.builder.ModuleAssemblerConfiguration;
import org.drools.guvnor.server.builder.ModuleAssemblerManager;
import org.drools.guvnor.server.builder.pagerow.SnapshotComparisonPageRowBuilder;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.ICanHasAttachment;
import org.drools.guvnor.server.security.RoleType;
import org.drools.guvnor.server.util.BuilderResultHelper;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.guvnor.server.util.ModuleFactory;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.ModuleItem;
import org.drools.repository.ModuleIterator;
import org.drools.repository.RepositoryFilter;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.util.LoggingHelper;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Handles operations for modules
 */
@Service
@ApplicationScoped
public class RepositoryModuleOperations {

    private static final LoggingHelper log = LoggingHelper.getLogger( RepositoryModuleOperations.class );

    /**
     * Maximum number of assets to display in "list assets in module" method
     */
    private static final int MAX_ASSETS_TO_SHOW_IN_MODULE_LIST = 5000;

    @Inject
    private RulesRepository rulesRepository;

/*    @Inject
    private Identity identity;*/

    @Deprecated
    public void setRulesRepositoryForTest(RulesRepository repository) {
        // TODO use GuvnorTestBase with a real RepositoryAssetOperations instead
        this.rulesRepository = repository;
    }

    protected Module[] listModules(boolean archive,
                                               String workspace,
                                               RepositoryFilter filter) {
        List<Module> result = new ArrayList<Module>();
        ModuleIterator modules = rulesRepository.listModules();
        handleIterateModules( archive,
                workspace,
                filter,
                result,
                modules );

        sortModules( result );
        return result.toArray( new Module[result.size()] );
    }

    private void handleIterateModules(boolean archive,
                                       String workspace,
                                       RepositoryFilter filter,
                                       List<Module> result,
                                       ModuleIterator modules) {
        modules.setArchivedIterator( archive );
        while (modules.hasNext()) {
            ModuleItem packageItem = modules.next();

            Module data = new Module();
            data.setUuid( packageItem.getUUID() );
            data.setName( packageItem.getName() );
            data.setArchived( packageItem.isArchived() );
            //data.setWorkspaces( packageItem.getWorkspaces() );
            handleIsModuleListed( archive,
                    workspace,
                    filter,
                    result,
                    data );

            data.subModules = listSubModules( packageItem,
                    archive,
                    null,
                    filter );

        }
    }

    private Module[] listSubModules(ModuleItem parentModule,
                                                boolean archive,
                                                String workspace,
                                                RepositoryFilter filter) {
        List<Module> children = new LinkedList<Module>();

        handleIterateModules( archive,
                workspace,
                filter,
                children,
                parentModule.listSubModules() );

        sortModules( children );
        return children.toArray( new Module[children.size()] );
    }

    void sortModules(List<Module> result) {
        Collections.sort( result,
                new Comparator<Module>() {

                    public int compare(final Module d1,
                                       final Module d2) {
                        return d1.getName().compareTo( d2.getName() );
                    }

                } );
    }

    private void handleIsModuleListed(boolean archive,
                                        String workspace,
                                        RepositoryFilter filter,
                                        List<Module> result,
                                        Module data) {
        if ( !archive && (filter == null || filter.accept( data,
                RoleType.PACKAGE_READONLY.getName() )) && (workspace == null || isWorkspace( workspace,
                data.getWorkspaces() )) ) {
            result.add( data );
        } else if ( archive && data.isArchived() && (filter == null || filter.accept( data,
                RoleType.PACKAGE_READONLY.getName() )) && (workspace == null || isWorkspace( workspace,
                data.getWorkspaces() )) ) {
            result.add( data );
        }
    }

    private boolean isWorkspace(String workspace,
                                String[] workspaces) {

        for (String w : workspaces) {
            if ( w.equals( workspace ) ) {
                return true;
            }
        }
        return false;
    }

    protected Module loadGlobalModule() {
        ModuleItem item = rulesRepository.loadGlobalArea();

        Module data = ModuleFactory.createModuleWithOutDependencies( item );

        if ( data.isSnapshot() ) {
            data.setSnapshotName( item.getSnapshotName() );
        }

        return data;
    }

    protected String copyModules(String sourceModuleName,
                                 String destModuleName) throws SerializationException {

        try {
            //log.info( "USER:" + getCurrentUserName() + " COPYING module [" + sourceModuleName + "] to  module [" + destModuleName + "]" );

            final String newModuleUUID = rulesRepository.copyModule( sourceModuleName,
                                                                     destModuleName );
            fixProcessPackageNames( newModuleUUID );
            return newModuleUUID;

        } catch ( RulesRepositoryException e ) {
            log.error( "Unable to copy module.",
                       e );
            throw e;
        }
    }

    //Ensure all Processes (RuleFlow, BPMN, BPMN2) have their package name updated to that of the containing Guvnor package
    private void fixProcessPackageNames(final String moduleUUID) {
        final ModuleItem newModule = rulesRepository.loadModuleByUUID( moduleUUID );
        final AssetItemIterator assetIterator = newModule.listAssetsByFormat( new String[]{AssetFormats.RULE_FLOW_RF, AssetFormats.BPMN_PROCESS, AssetFormats.BPMN2_PROCESS} );

        while ( assetIterator.hasNext() ) {
            final AssetItem asset = assetIterator.next();
            final String assetFormat = asset.getFormat();
            final ContentHandler contentHandler = ContentManager.getHandler( assetFormat );
            if ( contentHandler instanceof ICanHasAttachment ) {
                ICanHasAttachment attachmentHandler = (ICanHasAttachment) contentHandler;
                try {
                    attachmentHandler.onAttachmentAdded( asset );
                } catch ( IOException ioe ) {
                    log.error( "Unable to rename Process [type=" + assetFormat + "] package for asset [" + asset.getName() + "]",
                               ioe );
                }
            }
        }
    }

    protected void removeModule(String uuid) {

        try {
            ModuleItem item = rulesRepository.loadModuleByUUID( uuid );
            //log.info( "USER:" + getCurrentUserName() + " REMOVEING module [" + item.getName() + "]" );
            item.remove();
            rulesRepository.save();
        } catch (RulesRepositoryException e) {
            log.error( "Unable to remove module.",
                    e );
            throw e;
        }
    }

    protected String renameModule(String uuid,
                                  String newName) {
        //log.info( "USER:" + getCurrentUserName() + " RENAMING module [UUID: " + uuid + "] to module [" + newName + "]" );

        rulesRepository.renameModule( uuid,
                                      newName );

        fixProcessPackageNames( uuid );

        return uuid;
    }

    //JLIU
/*    protected byte[] exportModules(String moduleName) {
        log.info( "USER:" + getCurrentUserName() + " export module [name: " + moduleName + "] " );

        try {
            return rulesRepository.dumpModuleFromRepositoryXml( moduleName );
        } catch (PathNotFoundException e) {
            throw new RulesRepositoryException( e );
        } catch (IOException e) {
            throw new RulesRepositoryException( e );
        } catch (RepositoryException e) {
            throw new RulesRepositoryException( e );
        }
    }

    // TODO: Not working. GUVNOR-475
    protected void importPackages(byte[] byteArray,
                                  boolean importAsNew) {
        rulesRepository.importPackageToRepository( byteArray,
                importAsNew );
    }*/

    protected String createModule(String name, String description,
            String format) throws RulesRepositoryException {

/*        log.info("USER: " + getCurrentUserName() + " CREATING module [" + name
                + "]");*/
        ModuleItem item = rulesRepository.createModule(name,
                description, format);

        return item.getUUID();
    }
    
    protected String createModule(String name,
                                   String description,
                                   String format,
                                   String[] workspace) throws RulesRepositoryException {

        //log.info( "USER: " + getCurrentUserName() + " CREATING module [" + name + "]" );
        ModuleItem item = rulesRepository.createModule( name,
                description,
                format,
                workspace,
                "Initial");

        return item.getUUID();
    }
    
    //JLIU: no more sub module    
/*    protected String createSubModule(String name,
                                      String description,
                                      String parentNode) throws SerializationException {
        log.info( "USER: " + getCurrentUserName() + " CREATING subModule [" + name + "], parent [" + parentNode + "]" );
        ModuleItem item = rulesRepository.createSubModule( name,
                description,
                parentNode );
        return item.getUUID();
    }
*/
    protected Module loadModule(ModuleItem packageItem) {
        Module data = ModuleFactory.createModuleWithDependencies( packageItem );
        if ( data.isSnapshot() ) {
            data.setSnapshotName( packageItem.getSnapshotName() );
        }
        return data;
    }

    public void saveModule(Module data) throws SerializationException {
        //log.info( "USER:" + getCurrentUserName() + " SAVING module [" + data.getName() + "]" );

        ModuleItem moduleItem = rulesRepository.loadModule( data.getName() );

        // If module is being unarchived.
        boolean unarchived = (!data.isArchived() && moduleItem.isArchived());
        Calendar lastModified = moduleItem.getLastModified();

        //TODO: Drools specific
        DroolsHeader.updateDroolsHeader( data.getHeader(),
                moduleItem );
        updateCategoryRules( data,
                moduleItem );

        moduleItem.updateExternalURI( data.getExternalURI() );
        moduleItem.updateDescription( data.getDescription() );
        moduleItem.archiveItem( data.isArchived() );
        moduleItem.updateBinaryUpToDate( false );
        if(!data.getFormat().equals("")) {
            moduleItem.updateFormat(data.getFormat());
        }
        //JLIU: TODO: 
        //RuleBaseCache.getInstance().remove( data.getUuid() );
        moduleItem.checkin( data.getDescription() );

        // If module is archived, archive all the assets under it
        if ( data.isArchived() ) {
            handleArchivedForSaveModule( data,
                    moduleItem );
        } else if ( unarchived ) {
            handleUnarchivedForSaveModule( data,
                    moduleItem,
                    lastModified );
        }
    }

    void updateCategoryRules(Module data,
                             ModuleItem item) {
        KeyValueTO keyValueTO = convertMapToCsv( data.getCatRules() );
        item.updateCategoryRules( keyValueTO.getKeys(),
                keyValueTO.getValues() );
    }

    // HashMap DOES NOT guarantee order in different iterations!
    private static KeyValueTO convertMapToCsv(final Map map) {
        StringBuilder keysBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder();
        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            if ( keysBuilder.length() > 0 ) {
                keysBuilder.append( "," );
            }

            if ( valuesBuilder.length() > 0 ) {
                valuesBuilder.append( "," );
            }

            keysBuilder.append( entry.getKey() );
            valuesBuilder.append( entry.getValue() );
        }
        return new KeyValueTO( keysBuilder.toString(),
                valuesBuilder.toString() );
    }

    private static class KeyValueTO {
        private final String keys;
        private final String values;

        public KeyValueTO(final String keys,
                          final String values) {
            this.keys = keys;
            this.values = values;
        }

        public String getKeys() {
            return keys;
        }

        public String getValues() {
            return values;
        }
    }

    void handleArchivedForSaveModule(Module data,
                                      ModuleItem item) {
        for (Iterator<AssetItem> iter = item.getAssets(); iter.hasNext(); ) {
            AssetItem assetItem = iter.next();
            if ( !assetItem.isArchived() ) {
                assetItem.archiveItem( true );
                assetItem.checkin( data.getDescription() );
            }
        }
    }

    void handleUnarchivedForSaveModule(Module data,
                                        ModuleItem item,
                                        Calendar lastModified) {
        for (Iterator<AssetItem> iter = item.getAssets(); iter.hasNext(); ) {
            AssetItem assetItem = iter.next();
            // Unarchive the assets archived after the package
            // ( == at the same time that the package was archived)
            if ( assetItem.getLastModified().compareTo( lastModified ) >= 0 ) {
                assetItem.archiveItem( false );
                assetItem.checkin( data.getDescription() );
            }
        }
    }

    
    //JLIU: No more snapshots
/*    public void createModuleSnapshot(String moduleName,
                                         String snapshotName,
                                         boolean replaceExisting,
                                         String comment) {

        log.info( "USER:" + getCurrentUserName() + " CREATING MODULE SNAPSHOT for module: [" + moduleName + "] snapshot name: [" + snapshotName );

        if ( replaceExisting ) {
            if(rulesRepository.containsSnapshot(moduleName, snapshotName)) {
                rulesRepository.removeModuleSnapshot( moduleName, snapshotName );
            }
        }

        rulesRepository.createModuleSnapshot( moduleName,
                snapshotName );
        ModuleItem item = rulesRepository.loadModuleSnapshot( moduleName,
                snapshotName );
        item.updateCheckinComment( comment );
        rulesRepository.save();

    }

    protected void copyOrRemoveSnapshot(String moduleName,
                                        String snapshotName,
                                        boolean delete,
                                        String newSnapshotName) throws SerializationException {

        if ( delete ) {
            log.info( "USER:" + getCurrentUserName() + " REMOVING SNAPSHOT for module: [" + moduleName + "] snapshot: [" + snapshotName + "]" );
            rulesRepository.removeModuleSnapshot( moduleName,
                    snapshotName );
        } else {
            if ( newSnapshotName.equals( "" ) ) {
                throw new SerializationException( "Need to have a new snapshot name." );
            }
            log.info( "USER:" + getCurrentUserName() + " COPYING SNAPSHOT for module: [" + moduleName + "] snapshot: [" + snapshotName + "] to [" + newSnapshotName + "]" );

            rulesRepository.copyModuleSnapshot( moduleName,
                    snapshotName,
                    newSnapshotName );
        }

    }
*/
    //This builds a module. The build result is a deployment bundle. This is taken care by module specific Assembler. 
    //In the case of Drools, the build result is a Drools package binary. 
    public BuilderResult buildModule(String moduleUUID,
                                      boolean force,
                                      String buildMode,
                                      String statusOperator,
                                      String statusDescriptionValue,
                                      boolean enableStatusSelector,
                                      String categoryOperator,
                                      String category,
                                      boolean enableCategorySelector,
                                      String customSelectorName) throws SerializationException {

        ModuleItem moduleItem = rulesRepository.loadModuleByUUID( moduleUUID );
        try {
            return buildModule( moduleItem,
                                 force,
                    createConfiguration( buildMode,
                            statusOperator,
                            statusDescriptionValue,
                            enableStatusSelector,
                            categoryOperator,
                            category,
                            enableCategorySelector,
                            customSelectorName ) );
        } catch (NoClassDefFoundError e) {
            throw new DetailedSerializationException( "Unable to find a class that was needed when building the module  [" + e.getMessage() + "]",
                    "Perhaps you are missing them from the model jars, or from the BRMS itself (lib directory)." );
        } catch (UnsupportedClassVersionError e) {
            throw new DetailedSerializationException( "Can not build the module. One or more of the classes that are needed were compiled with an unsupported Java version.",
                    "For example the pojo classes were compiled with Java 1.6 and Guvnor is running on Java 1.5. [" + e.getMessage() + "]" );
        }
    }

    private BuilderResult buildModule(ModuleItem item,
                                       boolean force,
                                       ModuleAssemblerConfiguration moduleAssemblerConfiguration) throws DetailedSerializationException {
        if ( !force && item.isBinaryUpToDate() ) {
            // we can just return all OK if its up to date.
            return BuilderResult.emptyResult();
        }
        
        ModuleAssembler moduleAssembler = ModuleAssemblerManager.getModuleAssembler(item.getFormat(), item, moduleAssemblerConfiguration);
        moduleAssembler.compile();

        if ( moduleAssembler.hasErrors() ) {
            BuilderResult result = new BuilderResult();
            BuilderResultHelper builderResultHelper = new BuilderResultHelper();
            result.addLines( builderResultHelper.generateBuilderResults( moduleAssembler.getErrors() ) );
            return result;
        }

        return BuilderResult.emptyResult();
    }

    private ModuleAssemblerConfiguration createConfiguration(String buildMode, String statusOperator, String statusDescriptionValue, boolean enableStatusSelector, String categoryOperator, String category, boolean enableCategorySelector, String selectorConfigName) {
        ModuleAssemblerConfiguration moduleAssemblerConfiguration = new ModuleAssemblerConfiguration();
        moduleAssemblerConfiguration.setBuildMode( buildMode );
        moduleAssemblerConfiguration.setStatusOperator( statusOperator );
        moduleAssemblerConfiguration.setStatusDescriptionValue( statusDescriptionValue );
        moduleAssemblerConfiguration.setEnableStatusSelector( enableStatusSelector );
        moduleAssemblerConfiguration.setCategoryOperator( categoryOperator );
        moduleAssemblerConfiguration.setCategoryValue( category );
        moduleAssemblerConfiguration.setEnableCategorySelector( enableCategorySelector );
        moduleAssemblerConfiguration.setCustomSelectorConfigName( selectorConfigName );
        return moduleAssemblerConfiguration;
    }

    //JLIU
/*    private String getCurrentUserName() {
        return rulesRepository.getSession().getUserID();
    }*/

    protected BuilderResult buildModule(ModuleItem item,
                                         boolean force) throws DetailedSerializationException {
        return buildModule( item,
                             force,
                createConfiguration(
                        null,
                        null,
                        null,
                        false,
                        null,
                        null,
                        false,
                        null ) );
    }

    protected String buildModuleSource(String moduleUUID) throws SerializationException {
        ModuleItem item = rulesRepository.loadModuleByUUID( moduleUUID );
        ModuleAssembler moduleAssembler = ModuleAssemblerManager.getModuleAssembler(item.getFormat(), item, null);
        return moduleAssembler.getCompiledSource();
    }

    //Drools specific
    protected String[] listRulesInPackage(String packageName) throws SerializationException {
        ModuleItem item = rulesRepository.loadModule( packageName );

        ModuleAssembler moduleAssembler = ModuleAssemblerManager.getModuleAssembler(item.getFormat(), item, null);

        List<String> result = new ArrayList<String>();
        try {

            String drl = moduleAssembler.getCompiledSource();
            if ( drl == null || "".equals( drl ) ) {
                return new String[0];
            } else {
                parseRulesToPackageList( moduleAssembler,
                        result );
            }

            return result.toArray( new String[result.size()] );
        } catch (DroolsParserException e) {
            log.error( "Unable to list rules in package",
                    e );
            return new String[0];
        }
    }

    protected String[] listImagesInModule(String moduleName) throws SerializationException {
        ModuleItem item = rulesRepository.loadModule( moduleName );
        List<String> retList = new ArrayList<String>();
        Iterator<AssetItem> iter = item.getAssets();
        while (iter.hasNext()) {
            AssetItem pitem = iter.next();
            if ( pitem.getFormat().equalsIgnoreCase( "png" ) || pitem.getFormat().equalsIgnoreCase( "gif" ) || pitem.getFormat().equalsIgnoreCase( "jpg" ) ) {
                retList.add( pitem.getName() );
            }
        }
        return retList.toArray( new String[]{} );
    }

    void parseRulesToPackageList(ModuleAssembler asm,
                                 List<String> result) throws DroolsParserException {
        int count = 0;
        StringTokenizer stringTokenizer = new StringTokenizer( asm.getCompiledSource(),
                "\n\r" );
        while (stringTokenizer.hasMoreTokens()) {
            String line = stringTokenizer.nextToken().trim();
            if ( line.startsWith( "rule " ) ) {
                String name = getRuleName( line );
                result.add( name );
                count++;
                if ( count == MAX_ASSETS_TO_SHOW_IN_MODULE_LIST ) {
                    result.add( "More then " + MAX_ASSETS_TO_SHOW_IN_MODULE_LIST + " rules." );
                    break;
                }
            }
        }
    }
    
    //JLIU: No more snapshot

 /*   *//**
     * @deprecated in favour of {@link compareSnapshots(SnapshotComparisonPageRequest)}
     *//*
    protected SnapshotDiffs compareSnapshots(String moduleName,
                                             String firstSnapshotName,
                                             String secondSnapshotName) {
        SnapshotDiffs diffs = new SnapshotDiffs();
        List<SnapshotDiff> list = new ArrayList<SnapshotDiff>();

        ModuleItem leftModule = rulesRepository.loadModuleSnapshot( moduleName,
                firstSnapshotName );
        ModuleItem rightModule = rulesRepository.loadModuleSnapshot( moduleName,
                secondSnapshotName );

        // Older one has to be on the left.
        if ( isRightOlderThanLeft( leftModule,
                rightModule ) ) {
            ModuleItem temp = leftModule;
            leftModule = rightModule;
            rightModule = temp;

            diffs.leftName = secondSnapshotName;
            diffs.rightName = firstSnapshotName;
        } else {
            diffs.leftName = firstSnapshotName;
            diffs.rightName = secondSnapshotName;
        }

        Iterator<AssetItem> leftExistingIter = leftModule.getAssets();
        while (leftExistingIter.hasNext()) {
            AssetItem left = leftExistingIter.next();
            if ( isModuleItemDeleted( rightModule,
                    left ) ) {
                SnapshotDiff diff = new SnapshotDiff();

                diff.name = left.getName();
                diff.diffType = SnapshotDiff.TYPE_DELETED;
                diff.leftUuid = left.getUUID();

                list.add( diff );
            }
        }

        Iterator<AssetItem> rightExistingIter = rightModule.getAssets();
        while (rightExistingIter.hasNext()) {
            AssetItem right = rightExistingIter.next();
            AssetItem left = null;
            if ( right != null && leftModule.containsAsset( right.getName() ) ) {
                left = leftModule.loadAsset( right.getName() );
            }

            // Asset is deleted or added
            if ( right == null || left == null ) {
                SnapshotDiff diff = new SnapshotDiff();

                if ( left == null ) {
                    diff.name = right.getName();
                    diff.diffType = SnapshotDiff.TYPE_ADDED;
                    diff.rightUuid = right.getUUID();
                }

                list.add( diff );
            } else if ( isAssetArchivedOrRestored( right,
                    left ) ) { // Has the asset
                // been archived
                // or restored
                SnapshotDiff diff = new SnapshotDiff();

                diff.name = right.getName();
                diff.leftUuid = left.getUUID();
                diff.rightUuid = right.getUUID();

                if ( left.isArchived() ) {
                    diff.diffType = SnapshotDiff.TYPE_RESTORED;
                } else {
                    diff.diffType = SnapshotDiff.TYPE_ARCHIVED;
                }

                list.add( diff );
            } else if ( isAssetItemUpdated( right,
                    left ) ) { // Has the asset been
                // updated
                SnapshotDiff diff = new SnapshotDiff();

                diff.name = right.getName();
                diff.leftUuid = left.getUUID();
                diff.rightUuid = right.getUUID();
                diff.diffType = SnapshotDiff.TYPE_UPDATED;

                list.add( diff );
            }
        }

        diffs.diffs = list.toArray( new SnapshotDiff[list.size()] );
        return diffs;
    }
*/
    private boolean isAssetArchivedOrRestored(AssetItem right,
                                              AssetItem left) {
        return right.isArchived() != left.isArchived();
    }

    private boolean isAssetItemUpdated(AssetItem right,
                                       AssetItem left) {
        return right.getLastModified().compareTo( left.getLastModified() ) != 0;
    }

    private boolean isModuleItemDeleted(ModuleItem rightModuleItem,
                                         AssetItem left) {
        return !rightModuleItem.containsAsset( left.getName() );
    }

    private boolean isRightOlderThanLeft(ModuleItem leftModuleItem,
                                         ModuleItem rightModuleItem) {
        return leftModuleItem.getLastModified().compareTo( rightModuleItem.getLastModified() ) > 0;
    }

    //JLIU: No more snapshot
/*    protected SnapshotComparisonPageResponse compareSnapshots(SnapshotComparisonPageRequest request) {

        SnapshotComparisonPageResponse response = new SnapshotComparisonPageResponse();

        // Do query (bit of a cheat really!)
        long start = System.currentTimeMillis();
        SnapshotDiffs diffs = compareSnapshots( request.getPackageName(),
                request.getFirstSnapshotName(),
                request.getSecondSnapshotName() );
        log.debug( "Search time: " + (System.currentTimeMillis() - start) );

        // Populate response
        response.setLeftSnapshotName( diffs.leftName );
        response.setRightSnapshotName( diffs.rightName );

        List<SnapshotComparisonPageRow> rowList = new SnapshotComparisonPageRowBuilder()
                .withPageRequest( request )
                .withIdentity(identity)
                .withContent( diffs )
                .build();

        response.setPageRowList( rowList );
        response.setStartRowIndex( request.getStartRowIndex() );
        response.setTotalRowSize( diffs.diffs.length );
        response.setTotalRowSizeExact( true );
        response.setLastPage( (request.getStartRowIndex() + rowList.size() == diffs.diffs.length) );

        long methodDuration = System.currentTimeMillis() - start;
        log.debug( "Compared Snapshots ('" + request.getFirstSnapshotName() + "') and ('" + request.getSecondSnapshotName() + "') in package ('" + request.getPackageName() + "') in " + methodDuration + " ms." );

        return response;
    }

*/}
