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
package org.drools.guvnor.server.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.core.util.BinaryRuleBaseLoader;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest;
import org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse;
import org.drools.guvnor.client.rpc.SnapshotDiffs;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.server.builder.ClassLoaderBuilder;
import org.drools.guvnor.server.cache.RuleBaseCache;
import org.drools.guvnor.server.contenthandler.ModelContentHandler;
import org.drools.guvnor.shared.ModuleService;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.ModuleItem;
import org.drools.repository.RepositoryFilter;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.util.LoggingHelper;


import com.google.gwt.user.client.rpc.SerializationException;

@Service
@ApplicationScoped
public class RepositoryModuleService
        implements
    ModuleService {

    private static final long          serialVersionUID = 901123;

    private static final LoggingHelper log              = LoggingHelper.getLogger( RepositoryModuleService.class );

    @Inject
    private RulesRepository            rulesRepository;
/*
    @Inject
    private ServiceSecurity            serviceSecurity;

    @Inject
    private Identity                   identity;*/

    @Inject
    private RepositoryModuleOperations repositoryModuleOperations;

/*    @Inject
    private RepositoryAssetOperations  repositoryAssetOperations;*/

    @Inject
    private ServiceImplementation      serviceImplementation;

    /**
     * Role-based Authorization check: This method only returns modules that the
     * user has permission to access. User has permission to access the
     * particular module when: The user has a package.readonly role or higher
     * (i.e., package.admin, package.developer) to this module.
     */
    public Module[] listModules() {
        return listModules( null );
    }

    public Module[] listModules(String workspace) {
        RepositoryFilter pf = new ModuleFilter(/* identity */);
        return repositoryModuleOperations.listModules( false,
                                                       workspace,
                                                       pf );
    }

    public Module[] listArchivedModules() {
        return listArchivedModules( null );
    }

    public Module[] listArchivedModules(String workspace) {
        return repositoryModuleOperations.listModules(
                                                       true,
                                                       workspace,
                                                       new ModuleFilter(/* identity */) );
    }

    public Module loadGlobalModule() {
        return repositoryModuleOperations.loadGlobalModule();
    }

    public void rebuildPackages() throws SerializationException {
        Iterator<ModuleItem> pkit = rulesRepository.listModules();
        StringBuilder errs = new StringBuilder();
        while ( pkit.hasNext() ) {
            ModuleItem pkg = pkit.next();
            try {
                BuilderResult builderResult = this.buildPackage( pkg.getUUID(),
                                                                 true );
                if ( builderResult != null ) {
                    errs.append( "Unable to build package name [" ).append( pkg.getName() ).append( "]\n" );
                    StringBuilder buf = createStringBuilderFrom( builderResult );
                    log.warn( buf.toString() );
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                log.error( "An error occurred building package [" + pkg.getName() + "]\n" );
                errs.append( "An error occurred building package [" ).append( pkg.getName() ).append( "]\n" );
            }
        }
    }

    private StringBuilder createStringBuilderFrom(BuilderResult res) {
        StringBuilder buf = new StringBuilder();
        for ( int i = 0; i < res.getLines().size(); i++ ) {
            buf.append( res.getLines().get( i ).toString() );
            buf.append( '\n' );
        }
        return buf;
    }

    public String buildModuleSource(String moduleUUID) throws SerializationException {
        //serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid( moduleUUID );
        return repositoryModuleOperations.buildModuleSource( moduleUUID );
    }

    public String copyModule(String sourceModuleName,
                             String destModuleName) throws SerializationException {
        //serviceSecurity.checkSecurityIsAdmin();
        return repositoryModuleOperations.copyModules( sourceModuleName,
                                                       destModuleName );
    }

    public void removeModule(String uuid) {
        //serviceSecurity.checkSecurityIsPackageAdminWithPackageUuid( uuid );
        repositoryModuleOperations.removeModule( uuid );
    }

    public String renameModule(String uuid,
                               String newName) {
        //serviceSecurity.checkSecurityIsPackageAdminWithPackageUuid( uuid );

        return repositoryModuleOperations.renameModule( uuid,
                                                        newName );
    }

    public byte[] exportModules(String moduleName) {
        //serviceSecurity.checkSecurityIsPackageAdminWithPackageName( moduleName );
    	//JLIU: TODO:
    	return null;
/*        return repositoryModuleOperations.exportModules( moduleName );*/
    }

    public void importPackages(byte[] byteArray,
                               boolean importAsNew) {
    	//JLIU: TODO:
/*        repositoryModuleOperations.importPackages( byteArray,
                                                   importAsNew );*/
    }

    public String createModule(String name,
                               String description,
                               String format) throws RulesRepositoryException {
        return repositoryModuleOperations.createModule( name,
                                                        description,
                                                        format );
    }

    public String createModule(String name,
                               String description,
                               String format,
                               String[] workspace) throws RulesRepositoryException {
        //serviceSecurity.checkSecurityIsAdmin();
        return repositoryModuleOperations.createModule( name,
                                                        description,
                                                        format,
                                                        workspace );
    }

    /*
     * @WebRemote public String createPackage(String name, String description,
     * String format) throws RulesRepositoryException {
     * serviceSecurity.checkSecurityIsAdmin(); return
     * repositoryPackageOperations.createPackage( name, description, new
     * String[]{} ); }
     */
    /*
     * @WebRemote public String createPackage(String name, String description,
     * String format, String[] workspace) throws RulesRepositoryException {
     * return createPackage( name, description, new String[]{} ); }
     */
    public String createSubModule(String name,
                                  String description,
                                  String parentNode) throws SerializationException {
        //serviceSecurity.checkSecurityIsAdmin();
    	//JLIU: no more sub modules
    	return null;
/*        return repositoryModuleOperations.createSubModule( name,
                                                           description,
                                                           parentNode );*/
    }

    //JLIU: no more UUID?
/*    public Module loadModule(String uuid) {
        ModuleItem moduleItem = rulesRepository.loadModuleByUUID( uuid );
        // the uuid passed in is the uuid of that deployment bundle, not the
        // module uudi.
        // we have to figure out the module name.
       // serviceSecurity.checkSecurityIsPackageReadOnlyWithPackageName( moduleItem.getName() );
        return repositoryModuleOperations.loadModule( moduleItem );
    }*/

    public Module loadModule(String moduleName) {
        ModuleItem moduleItem = rulesRepository.loadModule( moduleName );
        return repositoryModuleOperations.loadModule( moduleItem );
    }
    
    public void saveModule(Module data) throws SerializationException {
        //serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid( data.getUuid() );
        repositoryModuleOperations.saveModule( data );
    }

    public BuilderResult buildPackage(String packageUUID,
                                      boolean force) throws SerializationException {
        return buildPackage( packageUUID,
                             force,
                             null,
                             null,
                             null,
                             false,
                             null,
                             null,
                             false,
                             null );
    }

    public BuilderResult buildPackage(String packageUUID,
                                      boolean force,
                                      String buildMode,
                                      String statusOperator,
                                      String statusDescriptionValue,
                                      boolean enableStatusSelector,
                                      String categoryOperator,
                                      String category,
                                      boolean enableCategorySelector,
                                      String customSelectorName) throws SerializationException {
        //serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid( packageUUID );
        return repositoryModuleOperations.buildModule( packageUUID,
                                                       force,
                                                       buildMode,
                                                       statusOperator,
                                                       statusDescriptionValue,
                                                       enableStatusSelector,
                                                       categoryOperator,
                                                       category,
                                                       enableCategorySelector,
                                                       customSelectorName );
    }

    public void createModuleSnapshot(String moduleName,
                                     String snapshotName,
                                     boolean replaceExisting,
                                     String comment) {
        //serviceSecurity.checkSecurityIsPackageAdminWithPackageName( moduleName );
    	
    	//JLIU: No more snapshots
/*        repositoryModuleOperations.createModuleSnapshot( moduleName,
                                                         snapshotName,
                                                         replaceExisting,
                                                         comment );*/

    }

    public void copyOrRemoveSnapshot(String moduleName,
                                     String snapshotName,
                                     boolean delete,
                                     String newSnapshotName) throws SerializationException {
    	//JLIU: No more snapshots
    	
/*        serviceSecurity.checkSecurityIsPackageAdminWithPackageName( moduleName );
        repositoryModuleOperations.copyOrRemoveSnapshot( moduleName,
                                                         snapshotName,
                                                         delete,
                                                         newSnapshotName );*/
    }

    public String[] listRulesInPackage(String packageName) throws SerializationException {
        //serviceSecurity.checkSecurityIsPackageReadOnlyWithPackageName( packageName );
        return repositoryModuleOperations.listRulesInPackage( packageName );
    }

    public String[] listImagesInModule(String moduleName) throws SerializationException {
        //serviceSecurity.checkSecurityIsPackageReadOnlyWithPackageName( moduleName );
        return repositoryModuleOperations.listImagesInModule( moduleName );
    }

	//JLIU: No more snapshots
/*    public void rebuildSnapshots() throws SerializationException {
        serviceSecurity.checkSecurityIsAdmin();

        Iterator<ModuleItem> pkit = rulesRepository.listModules();
        while ( pkit.hasNext() ) {
            ModuleItem pkg = pkit.next();
            String[] snaps = rulesRepository.listModuleSnapshots( pkg.getName() );
            for ( String snapName : snaps ) {
                ModuleItem snap = rulesRepository.loadModuleSnapshot( pkg.getName(),
                                                                      snapName );
                BuilderResult builderResult = this.buildPackage( snap.getUUID(),
                                                                 true );
                if ( builderResult.hasLines() ) {
                    StringBuilder stringBuilder = createStringBuilderFrom( builderResult );
                    throw new DetailedSerializationException( "Unable to rebuild snapshot [" + snapName,
                                                              stringBuilder.toString() + "]" );
                }
            }
        }
    }

    public SnapshotInfo[] listSnapshots(String moduleName) {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageName( moduleName );

        String[] snaps = rulesRepository.listModuleSnapshots( moduleName );
        SnapshotInfo[] snapshotInfos = new SnapshotInfo[snaps.length];
        for ( int i = 0; i < snaps.length; i++ ) {
            ModuleItem moduleItem = rulesRepository.loadModuleSnapshot( moduleName,
                                                                        snaps[i] );
            snapshotInfos[i] = moduleItemToSnapshotItem( snaps[i],
                                                         moduleItem );
        }
        return snapshotInfos;
    }

    public SnapshotInfo loadSnapshotInfo(String packageName,
                                         String snapshotName) {
        serviceSecurity.checkSecurityIsPackageAdminWithPackageName( packageName );

        return moduleItemToSnapshotItem( snapshotName,
                                         rulesRepository.loadModuleSnapshot( packageName,
                                                                             snapshotName ) );
    }

    private SnapshotInfo moduleItemToSnapshotItem(String snapshotName,
                                                  ModuleItem packageItem) {
        SnapshotInfo snapshotInfo = new SnapshotInfo();
        snapshotInfo.setComment( packageItem.getCheckinComment() );
        snapshotInfo.setName( snapshotName );
        snapshotInfo.setUuid( packageItem.getUUID() );
        return snapshotInfo;
    }
*/
    public String[] listTypesInPackage(String packageUUID) throws SerializationException {
        //serviceSecurity.checkSecurityPackageReadOnlyWithPackageUuid( packageUUID );

        ModuleItem pkg = this.rulesRepository.loadModuleByUUID( packageUUID );
        List<String> res = new ArrayList<String>();
        AssetItemIterator it = pkg.listAssetsByFormat( AssetFormats.MODEL,
                                                       AssetFormats.DRL_MODEL );

        JarInputStream jis = null;

        try {
            while ( it.hasNext() ) {
                AssetItem asset = it.next();
                if ( !asset.isArchived() ) {
                    if ( asset.getFormat().equals( AssetFormats.MODEL ) ) {
                        jis = typesForModel( res,
                                             asset );
                    } else {
                        typesForOthers( res,
                                        asset );
                    }

                }
            }
            return res.toArray( new String[res.size()] );
        } catch ( IOException e ) {
            log.error( "Unable to read the jar files in the package: " + e.getMessage() );
            throw new DetailedSerializationException( "Unable to read the jar files in the package.",
                                                      e.getMessage() );
        } finally {
            IOUtils.closeQuietly( jis );
        }

    }

    //JLIU: TODO
/*    public void updateDependency(String uuid,
                                 String dependencyPath) {
        ModuleItem item = rulesRepository.loadModuleByUUID( uuid );
        item.updateDependency( dependencyPath );
        item.checkin( "Update dependency" );
    }

    public String[] getDependencies(String uuid) {
        ModuleItem item = rulesRepository.loadModuleByUUID( uuid );
        return item.getDependencies();
    }
*/
    private JarInputStream typesForModel(List<String> res,
                                         AssetItem asset) throws IOException {
        if ( !asset.isBinary() ) {
            return null;
        }
        if ( asset.getBinaryContentAttachment() == null ) {
            return null;
        }

        JarInputStream jis;
        jis = new JarInputStream( asset.getBinaryContentAttachment() );
        JarEntry entry = null;
        while ( (entry = jis.getNextJarEntry()) != null ) {
            if ( !entry.isDirectory() ) {
                if ( entry.getName().endsWith( ".class" ) && !entry.getName().endsWith( "package-info.class" ) ) {
                    res.add( ModelContentHandler.convertPathToName( entry.getName() ) );
                }
            }
        }
        return jis;
    }

    private void typesForOthers(List<String> res,
                                AssetItem asset) {
        // its delcared model
        DrlParser parser = new DrlParser();
        try {
            PackageDescr desc = parser.parse( asset.getContent() );
            List<TypeDeclarationDescr> types = desc.getTypeDeclarations();
            for ( TypeDeclarationDescr typeDeclarationDescr : types ) {
                res.add( typeDeclarationDescr.getTypeName() );
            }
        } catch ( DroolsParserException e ) {
            log.error( "An error occurred parsing rule: " + e.getMessage() );

        }
    }

    public void installSampleRepository() throws SerializationException {
    	//JLIU: TODO
/*        rulesRepository.importRepository( this.getClass().getResourceAsStream( "/mortgage-sample-repository.xml" ) );
        this.rebuildPackages();
        this.rebuildSnapshots();*/
    }

	//JLIU: No more snapshots
 /*   *//**
     * @deprecated in favour of
     *             {@link #compareSnapshots(SnapshotComparisonPageRequest)}
     *//*
    public SnapshotDiffs compareSnapshots(String moduleName,
                                          String firstSnapshotName,
                                          String secondSnapshotName) {
        return repositoryModuleOperations.compareSnapshots( moduleName,
                                                            firstSnapshotName,
                                                            secondSnapshotName );
    }

    public SnapshotComparisonPageResponse compareSnapshots(SnapshotComparisonPageRequest request) {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        return repositoryModuleOperations.compareSnapshots( request );
    }

*/    /*
     * Set the Rule base in a cache
     */
    private RuleBase loadCacheRuleBase(ModuleItem packageItem) throws DetailedSerializationException {

        if ( packageItem.isBinaryUpToDate() && RuleBaseCache.getInstance().contains( packageItem.getUUID() ) ) {
            return RuleBaseCache.getInstance().get( packageItem.getUUID() );
        } else {

            // we have to build the package, and try again.
            if ( packageItem.isBinaryUpToDate() ) {
                RuleBase ruleBase = loadRuleBase( packageItem );
                RuleBaseCache.getInstance().put( packageItem.getUUID(),
                                                 ruleBase );
                return ruleBase;
            } else {
                BuilderResult result = repositoryModuleOperations.buildModule( packageItem,
                                                                               false );
                if ( result == null || result.getLines().size() == 0 ) {
                    RuleBase ruleBase = loadRuleBase( packageItem );
                    RuleBaseCache.getInstance().put( packageItem.getUUID(),
                                                     ruleBase );
                    return ruleBase;
                } else {
                    throw new DetailedSerializationException( "Build error",
                                                              result.getLines() );
                }
            }

        }
    }

    private ClassLoaderBuilder createClassLoaderBuilder(ModuleItem packageItem) {
        return new ClassLoaderBuilder( packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat( AssetFormats.MODEL ) );
    }

    private RuleBase deserKnowledgebase(ModuleItem item,
                                        ClassLoader classloader) throws IOException,
                                                                ClassNotFoundException {
        RuleBase rulebase = RuleBaseFactory.newRuleBase( new RuleBaseConfiguration( classloader ) );
        BinaryRuleBaseLoader rbl = new BinaryRuleBaseLoader( rulebase,
                                                             classloader );
        rbl.addPackage( new ByteArrayInputStream( item.getCompiledBinaryBytes() ) );
        return rulebase;
    }

    private RuleBase loadRuleBase(ModuleItem item) throws DetailedSerializationException {
        try {
            return deserKnowledgebase( item,
                                       createClassLoaderBuilder( item ).buildClassLoader() );
        } catch ( ClassNotFoundException e ) {
            log.error( "Unable to load rule base.",
                       e );
            throw new DetailedSerializationException( "A required class was not found.",
                                                      e.getMessage() );
        } catch ( Exception e ) {
            log.error( "Unable to load rule base.",
                       e );
            log.info( "...but trying to rebuild binaries..." );
            try {
                BuilderResult builderResult = repositoryModuleOperations.buildModule(
                                                                                      item,
                                                                                      true );
                if ( builderResult != null && builderResult.getLines().size() > 0 ) {
                    log.error( "There were errors when rebuilding the knowledgebase." );
                    throw new DetailedSerializationException( "There were errors when rebuilding the knowledgebase.",
                                                              "" );
                }
            } catch ( Exception e1 ) {
                log.error( "Unable to rebuild the rulebase: " + e.getMessage() );
                throw new DetailedSerializationException( "Unable to rebuild the rulebase.",
                                                          e.getMessage() );
            }
            try {
                return deserKnowledgebase( item,
                                           createClassLoaderBuilder( item ).buildClassLoader() );
            } catch ( Exception e2 ) {
                log.error( "Unable to reload knowledgebase: " + e.getMessage() );
                throw new DetailedSerializationException( "Unable to reload knowledgebase.",
                                                          e.getMessage() );
            }

        }
    }

}
