package org.drools.workbench.jcr2vfsmigration.migrater;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.DiscussionRecord;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.RepositoryModuleService;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.guvnor.server.util.Discussion;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;
import org.drools.workbench.jcr2vfsmigration.migrater.asset.AttachementAssetMigrater;
import org.drools.workbench.jcr2vfsmigration.migrater.asset.FactModelsMigrater;
import org.drools.workbench.jcr2vfsmigration.migrater.asset.GuidedDecisionTableMigrater;
import org.drools.workbench.jcr2vfsmigration.migrater.asset.GuidedEditorMigrater;
import org.drools.workbench.jcr2vfsmigration.migrater.asset.GuidedScoreCardMigrater;
import org.drools.workbench.jcr2vfsmigration.migrater.asset.PlainTextAssetMigrater;
import org.drools.workbench.jcr2vfsmigration.migrater.asset.PlainTextAssetWithPackagePropertyMigrater;
import org.drools.workbench.jcr2vfsmigration.migrater.asset.TestScenarioMigrater;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.kie.workbench.services.shared.metadata.MetadataService;
import org.kie.workbench.services.shared.metadata.model.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;


@ApplicationScoped
public class AssetMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(AssetMigrater.class);

    @Inject
    protected RepositoryModuleService jcrRepositoryModuleService;
    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;    
    @Inject @Preferred
    private RulesRepository rulesRepository;
    
    @Inject
    protected FactModelsMigrater factModelsMigrater;
    @Inject
    protected GuidedEditorMigrater guidedEditorMigrater;
    @Inject
    protected PlainTextAssetMigrater plainTextAssetMigrater;
    @Inject
    protected PlainTextAssetWithPackagePropertyMigrater plainTextAssetWithPackagePropertyMigrater;
    @Inject
    protected GuidedDecisionTableMigrater guidedDecisionTableMigrater;
    @Inject
    protected AttachementAssetMigrater attachementAssetMigrater;
    @Inject
    protected GuidedScoreCardMigrater guidedScoreCardMigrater;
    @Inject
    protected TestScenarioMigrater testScenarioMigrater;
    @Inject
    protected MetadataService metadataService;        
    @Inject
    protected MigrationPathManager migrationPathManager;
    @Inject
    private Paths paths;
    
    private String header = null;
    
    @Produces
    public PackageHeaderInfo getPackageHeaderInfo() {
        return new PackageHeaderInfo(header);
    }
    
    public void migrateAll() {
        logger.info("  Asset migration started");
        Module[] jcrModules = jcrRepositoryModuleService.listModules();
        for (Module jcrModule : jcrModules) {
            
            //Load drools.package first if it exists            
            try {
                List<String> formats = new ArrayList<String>();
                formats.add("package");
                AssetPageRequest request = new AssetPageRequest(jcrModule.getUuid(),
                        formats, 
                        null,
                        0,
                        10);
                PageResponse<AssetPageRow> response = jcrRepositoryAssetService.findAssetPage(request);
                if (response.getTotalRowSize() >0) {
                    AssetPageRow row = response.getPageRowList().get(0);
                    AssetItem assetItemJCR = rulesRepository.loadAssetByUUID(row.getUuid());
                    header = assetItemJCR.getContent();
                }

            } catch (SerializationException e) {
                throw new IllegalStateException(e);
            }
            
            
            boolean hasMorePages = true;
            int startRowIndex = 0;
            final int pageSize = 100;
            PageResponse<AssetPageRow> response;
            while (hasMorePages) {
                AssetPageRequest request = new AssetPageRequest(jcrModule.getUuid(),
                        null, // get all formats
                        null,
                        startRowIndex,
                        pageSize);
                
                try {
                    response = jcrRepositoryAssetService.findAssetPage(request);
                    for (AssetPageRow row : response.getPageRowList()) {              
                        //TODO: Git wont check in a version if the file is not changed in this version. Eg, the version 3 of "testFunction.function"
                        //We need to find a way to force a git check in. Otherwise migrated version history is not consistent with the version history in old Guvnor.
                        
                        //Migrate historical versions first, this includes the head version(i.e., the latest version)
                        migrateAssetHistory(jcrModule, row.getUuid());
                        
                        //Still need to migrate the "current version" even though in most cases the "current version" (actually it is not a version in version 
                        //control, its just the current content on jcr node) is equal to the latest version that had been checked in. 
                        //Eg, when we import mortgage example, we just dump the mortgage package to a jcr node, no version check in.    
                        AssetItem assetItemJCR = rulesRepository.loadAssetByUUID(row.getUuid());
                        migrate(jcrModule, assetItemJCR);
                        logger.debug("    Asset ({}) with format ({}) migrated.",
                                assetItemJCR.getName(), assetItemJCR.getFormat());

                        //Migrate asset discussions
                        migrateAssetDiscussions(jcrModule, row.getUuid());
                    }
                } catch (SerializationException e) {
                    throw new IllegalStateException(e);
                }
                if (response.isLastPage()) {
                    hasMorePages = false;
                } else {
                    startRowIndex += pageSize;
                }
            }
        }
        logger.info("  Asset migration ended");
    }

    private void migrate(Module jcrModule, AssetItem jcrAssetItem) {
        if (AssetFormats.DRL_MODEL.equals(jcrAssetItem.getFormat())) {
            factModelsMigrater.migrate(jcrModule, jcrAssetItem);
        } else if (AssetFormats.BUSINESS_RULE.equals(jcrAssetItem.getFormat())) {
            guidedEditorMigrater.migrate(jcrModule, jcrAssetItem);
        } else if (AssetFormats.DECISION_TABLE_GUIDED.equals(jcrAssetItem.getFormat())) {
            guidedDecisionTableMigrater.migrate(jcrModule, jcrAssetItem);
        } else if (AssetFormats.ENUMERATION.equals(jcrAssetItem.getFormat())
                || AssetFormats.DSL.equals(jcrAssetItem.getFormat())
                || AssetFormats.DSL_TEMPLATE_RULE.equals(jcrAssetItem.getFormat())
                || AssetFormats.FORM_DEFINITION.equals(jcrAssetItem.getFormat())
                || AssetFormats.SPRING_CONTEXT.equals(jcrAssetItem.getFormat())
                || AssetFormats.SERVICE_CONFIG.equals(jcrAssetItem.getFormat())
                || AssetFormats.WORKITEM_DEFINITION.equals(jcrAssetItem.getFormat())
                || AssetFormats.CHANGE_SET.equals(jcrAssetItem.getFormat())
                || AssetFormats.RULE_FLOW_RF.equals(jcrAssetItem.getFormat())
                || AssetFormats.BPMN_PROCESS.equals(jcrAssetItem.getFormat())
                || AssetFormats.BPMN2_PROCESS.equals(jcrAssetItem.getFormat())
                || "ftl".equals(jcrAssetItem.getFormat())
                || "json".equals(jcrAssetItem.getFormat())
                || "fw".equals(jcrAssetItem.getFormat())) {
            plainTextAssetMigrater.migrate(jcrModule, jcrAssetItem);
        } else if (AssetFormats.DRL.equals(jcrAssetItem.getFormat())
                || AssetFormats.FUNCTION.equals(jcrAssetItem.getFormat())) {
            plainTextAssetWithPackagePropertyMigrater.migrate(jcrModule, jcrAssetItem);
        } else if (AssetFormats.DECISION_SPREADSHEET_XLS.equals(jcrAssetItem.getFormat())
                 ||AssetFormats.SCORECARD_SPREADSHEET_XLS.equals(jcrAssetItem.getFormat())
                 ||"png".equals(jcrAssetItem.getFormat())
                 ||"pdf".equals(jcrAssetItem.getFormat())
                 ||"doc".equals(jcrAssetItem.getFormat())
                 ||"odt".equals(jcrAssetItem.getFormat())) {
            attachementAssetMigrater.migrate(jcrModule, jcrAssetItem);
        } else if (AssetFormats.MODEL.equals(jcrAssetItem.getFormat())) {
            // TODO return error message
            logger.info("      POJO Model jar [" + jcrAssetItem.getName() + "] is not supported by migration tool. Please add your POJO model jar to Guvnor manually.");
        } else if (AssetFormats.SCORECARD_GUIDED.equals(jcrAssetItem.getFormat())) {
            guidedScoreCardMigrater.migrate(jcrModule, jcrAssetItem);
        } else if (AssetFormats.TEST_SCENARIO.equals(jcrAssetItem.getFormat())) {
            testScenarioMigrater.migrate(jcrModule, jcrAssetItem);
        } else if ("package".equals(jcrAssetItem.getFormat())) {
            //Ignore
        } else {
            // TODO REPLACE ME WITH ACTUAL CODE
            logger.debug("      TODO migrate asset ({}) with format({}).", jcrAssetItem.getName(), jcrAssetItem.getFormat());
        }
        // TODO When all assetFormats types have been tried, the last else should throw an IllegalArgumentException
    }
    
    public void migrateAssetHistory(Module jcrModule, String assetUUID) throws SerializationException {
        //loadItemHistory wont return the current version
        TableDataResult history = jcrRepositoryAssetService.loadItemHistory(assetUUID);
        TableDataRow[] rows = history.data;
        Arrays.sort( rows,
                new Comparator<TableDataRow>() {
                    public int compare( TableDataRow r1,
                                        TableDataRow r2 ) {
                        Integer v2 = Integer.valueOf( r2.values[0] );
                        Integer v1 = Integer.valueOf( r1.values[0] );

                        return v1.compareTo( v2 );
                    }
                } );

        for (TableDataRow row : rows) {
            String versionSnapshotUUID = row.id;

            AssetItem historicalAssetJCR = rulesRepository.loadAssetByUUID(versionSnapshotUUID);
            migrate(jcrModule, historicalAssetJCR);
            logger.debug("    Asset ({}) with format ({}) migrated: version [{}], comment[{}], lastModified[{}]",
                    historicalAssetJCR.getName(), historicalAssetJCR.getFormat(), historicalAssetJCR.getVersionNumber(), historicalAssetJCR.getCheckinComment(), historicalAssetJCR.getLastModified().getTime());
        }
    }

    public void migrateAssetDiscussions(Module jcrModule, String assetUUID)  throws SerializationException {
        //avoid using RepositoryAssetService as it calls assets' content handler
        AssetItem assetItemJCR = rulesRepository.loadAssetByUUID(assetUUID);
        List<DiscussionRecord> discussions = new Discussion().fromString( assetItemJCR.getStringProperty( Discussion.DISCUSSION_PROPERTY_KEY ) );
        
        if(discussions.size() == 0) {
            return;
        }
        
         //final org.kie.commons.java.nio.file.Path nioPath = paths.convert( path );
        Metadata metadata = new Metadata();
        for(DiscussionRecord discussion: discussions) {
            metadata.addDiscussion( new org.kie.workbench.services.shared.metadata.model.DiscussionRecord( discussion.timestamp, discussion.author, discussion.note ) );
        }

        Path path = migrationPathManager.generatePathForAsset(jcrModule, assetItemJCR);
        metadataService.setUpAttributes(path, metadata);
    }

    // TODO delete code below once we have all of its functionality
//
//    @Inject
//    private RulesRepositoryVFS rulesRepositoryVFS;
//
//    public void migrateAssets() {
//        Module[] modules = jcrRepositoryModuleService.listModules();
//
//        for(Module module : modules) {
//            //TODO: Migrate package. Should already be done by ModuleMigrator
//
//            //TODO: Migrate Guvnor package based permissions: admin/package.admin/package.developer/package.readonly
//            //(and dont forget to migrate category based permission, ie, analyst/analyst.readonly)
//
//            AssetPageRequest request = new AssetPageRequest( module.getUuid(),
//                                                             null, //get assets with all formats when format=null
//                                                             null,
//                                                             0,
//                                                             0);// return all assets in 1 page if pageSize=0
//            try {
//                PageResponse<AssetPageRow> response = jcrRepositoryAssetService.findAssetPage(request);
//                for (AssetPageRow row : response.getPageRowList()) {
//
//                    //Migrate asset history first
//                    migrateAssetHistory(row.getUuid());
//
//
//                    Asset assetJCR = jcrRepositoryAssetService.loadRuleAsset(row.getUuid());
//                    Asset assetVFS = assetJCR;
//                    //migrate asset binary content. The binary content of assets from previous Guvnor version can not be reused by VFS directly.
//                    //Make sure we deserialize/serialize the binary content of assets
//                    PortableObject binaryContent = assetJCR.getContent();
//                    assetVFS.setContent(binaryContent);
//
//                    //migrate discussions:
//                    migrateAssetDiscussions(assetJCR, assetVFS, row.getUuid());
//
//                    //migrate state:
//                    migrateAssetState(assetJCR, assetVFS, row.getUuid());
//
//                    rulesRepositoryVFS.checkinVersion(assetJCR);
//                }
//            } catch (SerializationException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }
//

//
//    public void migrateAssetState(Asset assetJCR, Asset assetVFS, String assetUUID) {
//        String stateName = assetJCR.getState();
//        //If we need to convert old state to new state:
//        //assetVFS.setState(stateName);
//    }
//
 
}
