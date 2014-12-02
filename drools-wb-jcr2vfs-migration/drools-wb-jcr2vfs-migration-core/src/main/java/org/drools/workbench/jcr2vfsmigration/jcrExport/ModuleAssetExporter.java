/*
 * Copyright 2014 JBoss Inc
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
package org.drools.workbench.jcr2vfsmigration.jcrExport;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;

import com.google.gwt.user.client.rpc.SerializationException;
import org.apache.commons.lang.StringUtils;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.RepositoryModuleService;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.ModuleIterator;
import org.drools.repository.RulesRepository;
import org.drools.workbench.jcr2vfsmigration.jcrExport.asset.GuidedDecisionTableExporter;
import org.drools.workbench.jcr2vfsmigration.jcrExport.asset.GuidedEditorExporter;
import org.drools.workbench.jcr2vfsmigration.jcrExport.asset.PlainTextAssetExporter;
import org.drools.workbench.jcr2vfsmigration.jcrExport.asset.PlainTextAssetWithPackagePropertyExporter;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.drools.workbench.jcr2vfsmigration.util.FileManager;
import org.drools.workbench.jcr2vfsmigration.xml.format.ModulesXmlFormat;
import org.drools.workbench.jcr2vfsmigration.xml.format.XmlAssetsFormat;
import org.drools.workbench.jcr2vfsmigration.xml.model.ModuleType;
import org.drools.workbench.jcr2vfsmigration.xml.model.Modules;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.AttachmentAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.XmlAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.XmlAssets;
import org.uberfire.backend.vfs.Path;

public class ModuleAssetExporter {

    private static int assetFileName = 1;
    private static int attachmentFileNameCounter = 1;

    @Inject
    FileManager fileManager;

    @Inject
    protected MigrationPathManager migrationPathManager;

    @Inject
    protected RepositoryModuleService jcrRepositoryModuleService;

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    @Preferred
    private RulesRepository rulesRepository;

    @Inject
    PlainTextAssetExporter plainTextAssetExporter;

    @Inject
    PlainTextAssetWithPackagePropertyExporter plainTextAssetWithPackagePropertyExporter;

    @Inject
    GuidedEditorExporter guidedEditorExporter;

    @Inject
    GuidedDecisionTableExporter guidedDecisionTableExporter;

    ModulesXmlFormat modulesXmlFormat = new ModulesXmlFormat();
    XmlAssetsFormat xmlAssetsFormat = new XmlAssetsFormat();

    public void exportAll() {

        System.out.println( "  Module export started" );
        Module jcrGlobalModule = jcrRepositoryModuleService.loadGlobalModule();
        Module[] jcrModules = jcrRepositoryModuleService.listModules();

        if ( jcrGlobalModule == null && jcrModules.length == 0 ) {
            System.out.println( "  No modules to be exported" );
            return;
        }

        Collection<org.drools.workbench.jcr2vfsmigration.xml.model.Module> normalModules = new ArrayList<org.drools.workbench.jcr2vfsmigration.xml.model.Module>( 5 );
        for ( Module jcrModule : jcrModules ) {
            normalModules.add( export( ModuleType.NORMAL, jcrModule ) );
        }

        org.drools.workbench.jcr2vfsmigration.xml.model.Module globalModule = export( ModuleType.GLOBAL, jcrGlobalModule );

        Modules modules = new Modules( globalModule, normalModules );

        StringBuilder xml = new StringBuilder();
        modulesXmlFormat.format( xml, modules );

        PrintWriter pw = fileManager.createModuleExportFileWriter();
        pw.print( xml.toString() );
        pw.close();

        System.out.println( "  Module export ended" );
    }

    private org.drools.workbench.jcr2vfsmigration.xml.model.Module export( ModuleType moduleType, Module jcrModule ) {
        System.out.format( "Module [%s] exported. %n", jcrModule.getName() );

        //setting CategoryRules to jcr module (needed in asset migration)
        for( ModuleIterator packageItems = rulesRepository.listModules(); packageItems.hasNext(); ) {
            ModuleItem packageItem = packageItems.next();
            if( packageItem.getUUID().equals( jcrModule.getUuid() ) ){
                jcrModule.setCatRules( packageItem.getCategoryRules() );
                break;
            }
        }

        // Save module name for later
        String moduleName = jcrModule.getName();
        String normalizedPackageName = migrationPathManager.normalizePackageName( jcrModule.getName() );
        jcrModule.setName( normalizedPackageName );

        // Export package header info
        String packageHeaderInfo = null;
        try {
            List<String> formats = new ArrayList<String>();
            formats.add("package");
            AssetPageRequest request = new AssetPageRequest(jcrModule.getUuid(),
                    formats,
                    null,
                    0,
                    10);
            PageResponse<AssetPageRow> response = jcrRepositoryAssetService.findAssetPage(request);
            if (response.getTotalRowSize() > 0) {
                AssetPageRow row = response.getPageRowList().get(0);
                AssetItem assetItemJCR = rulesRepository.loadAssetByUUID(row.getUuid());

                packageHeaderInfo = assetItemJCR.getContent();
            }
        } catch ( SerializationException e ) {
            throw new IllegalStateException( e );
        }

        String assetExportFileName = setupAssetExportFile( jcrModule.getUuid() );

        boolean assetExportSuccess = exportModuleAssets( jcrModule, assetExportFileName );
        if ( !assetExportSuccess ) System.out.println( "An error ocurred during asset export for module " + jcrModule.getUuid() );

        return new org.drools.workbench.jcr2vfsmigration.xml.model.Module( moduleType,
                jcrModule.getUuid(),
                moduleName,
                normalizedPackageName,
                packageHeaderInfo,
                jcrModule.getCatRules(),
                assetExportFileName );
    }

    private boolean exportModuleAssets( Module jcrModule, String assetFileName ) {
        System.out.println( "  Asset export started for module " + jcrModule.getUuid() );

        Collection<XmlAsset> assets = new ArrayList<XmlAsset>( 10 );

        StringBuilder xml = new StringBuilder();
        PrintWriter pw = null;
        try {
            pw = fileManager.createAssetExportFileWriter( assetFileName );
        } catch ( FileNotFoundException e ) {
            System.out.println( e.getMessage() );
            return false;
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
            String assetName="";
            try {
                response = jcrRepositoryAssetService.findAssetPage(request);
                for (AssetPageRow row : response.getPageRowList()) {
                    AssetItem assetItemJCR = rulesRepository.loadAssetByUUID(row.getUuid());
                    assetName =assetItemJCR.getName();
                    System.out.format("    Asset [%s] with format [%s] is being migrated... %n",
                            assetItemJCR.getName(), assetItemJCR.getFormat());
                    //TODO: Git wont check in a version if the file is not changed in this version. Eg, the version 3 of "testFunction.function"
                    //We need to find a way to force a git check in. Otherwise migrated version history is not consistent with the version history in old Guvnor.

                    //Migrate historical versions first, this includes the head version(i.e., the latest version)

// TODO?                        migrateAssetHistory(jcrModule, row.getUuid());

                    //Still need to migrate the "current version" even though in most cases the "current version" (actually it is not a version in version
                    //control, its just the current content on jcr node) is equal to the latest version that had been checked in.
                    //Eg, when we import mortgage example, we just dump the mortgage package to a jcr node, no version check in.

                    XmlAsset asset = export( jcrModule, assetItemJCR, assetFileName, null );
                    if ( asset != null ) {
                        assets.add( asset );
                    } else System.out.println( "WARNING: null asset returned in export: " + assetItemJCR.getName() );

                    System.out.format("    Done.%n");
                }
            } catch (SerializationException e) {
                System.out.println("SerializationException exporting asset: " + assetName +" from module: " + jcrModule.getName());
                return false;
            } catch (Exception e) {
                System.out.println("Exception migrating exporting: " + assetName +" from module: " + jcrModule.getName());
                return false;
            }

            if (response.isLastPage()) {
                hasMorePages = false;
            } else {
                startRowIndex += pageSize;
            }
        }
        xmlAssetsFormat.format( xml, new XmlAssets( assets ) );
        pw.print( xml.toString() );
        pw.close();
        return true;
    }

    private XmlAsset export(Module jcrModule, AssetItem jcrAssetItem, String assetExportFileName, Path previousVersionPath) {
        if ( AssetFormats.DRL_MODEL.equals(jcrAssetItem.getFormat())) {
//            return factModelsMigrater.migrate(jcrModule, jcrAssetItem, previousVersionPath);

        } else if (AssetFormats.BUSINESS_RULE.equals(jcrAssetItem.getFormat())) {
            return guidedEditorExporter.export( jcrModule, jcrAssetItem );

        } else if (AssetFormats.DECISION_TABLE_GUIDED.equals(jcrAssetItem.getFormat())) {
            return guidedDecisionTableExporter.export( jcrModule, jcrAssetItem );

        } else if (AssetFormats.ENUMERATION.equals(jcrAssetItem.getFormat())
                || AssetFormats.DSL.equals(jcrAssetItem.getFormat())
                || AssetFormats.DSL_TEMPLATE_RULE.equals(jcrAssetItem.getFormat())
                || AssetFormats.RULE_TEMPLATE.equals(jcrAssetItem.getFormat())
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
            return plainTextAssetExporter.export( jcrModule, jcrAssetItem );

        } else if (AssetFormats.DRL.equals(jcrAssetItem.getFormat())
                || AssetFormats.FUNCTION.equals(jcrAssetItem.getFormat())) {
            return plainTextAssetWithPackagePropertyExporter.export( jcrModule, jcrAssetItem );

        } else if (AssetFormats.DECISION_SPREADSHEET_XLS.equals(jcrAssetItem.getFormat())
                || AssetFormats.SCORECARD_SPREADSHEET_XLS.equals(jcrAssetItem.getFormat())
                || "png".equals(jcrAssetItem.getFormat())
                || "gif".equals(jcrAssetItem.getFormat())
                || "jpg".equals(jcrAssetItem.getFormat())
                || "pdf".equals(jcrAssetItem.getFormat())
                || "doc".equals(jcrAssetItem.getFormat())
                || "odt".equals(jcrAssetItem.getFormat())) {
            return exportAttachment( jcrModule, jcrAssetItem, assetExportFileName );

        } else if (AssetFormats.MODEL.equals(jcrAssetItem.getFormat())) {
            System.out.println("    WARNING: POJO Model jar [" + jcrAssetItem.getName() + "] is not supported by export tool. Please add your POJO model jar to Guvnor manually.");

        } else if (AssetFormats.SCORECARD_GUIDED.equals(jcrAssetItem.getFormat())) {
            // No special treatment or attributes needed; use PlainTextAsset
            return plainTextAssetExporter.export( jcrModule, jcrAssetItem );

        } else if (AssetFormats.TEST_SCENARIO.equals(jcrAssetItem.getFormat())) {
            // No special treatment or attributes needed; use PlainTextAsset
            return plainTextAssetExporter.export( jcrModule, jcrAssetItem );

        } else if ("package".equals(jcrAssetItem.getFormat())) {
            //Ignore

        } else { //another format is migrated as a attachmentAsset
            System.out.format("    WARNING: asset [%s] with format[%s] is not a known format by export tool. It will be exported as attachmentAsset %n", jcrAssetItem.getName(), jcrAssetItem.getFormat());
            return exportAttachment( jcrModule, jcrAssetItem, assetExportFileName );
        }
        return null;
    }

    private AttachmentAsset exportAttachment( Module jcrModule, AssetItem jcrAssetItem, String assetExportFileName ) {
        // No specific exporter for this, since nothing special needs to be done, just write bytes to file.
        String attachmentName = assetExportFileName + "_" + attachmentFileNameCounter++;
        if ( fileManager.writeBinaryContent( attachmentName, jcrAssetItem.getBinaryContentAsBytes() ) )
            return new AttachmentAsset( jcrAssetItem.getName(), jcrAssetItem.getFormat(), attachmentName );
        return null;
    }

    // Attempt creation of the asset export file firstly with the module's uuid. If this were null or the file could not
    // be successfully created, then try again with a shorter (i.e. simple number) name.
    private String setupAssetExportFile( String moduleUuid ) {
        StringBuilder fileNameBuilder = new StringBuilder();
        boolean success = false;
        if ( StringUtils.isNotBlank( moduleUuid ) ) {
            fileNameBuilder.insert( 0, moduleUuid );
            success = fileManager.createAssetExportFile( fileNameBuilder.toString() );
        }
        if ( !success ) {
            fileNameBuilder.replace( 0, fileNameBuilder.lastIndexOf( "." ), Integer.toString( assetFileName++ ) );
            success = fileManager.createAssetExportFile( fileNameBuilder.toString() );
            if ( ! success ) {
                System.out.println( "Module asset file could not be created" );
                return null;
            }
        }
        return fileNameBuilder.toString();
    }
}
