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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

import com.google.gwt.user.client.rpc.SerializationException;
import org.apache.commons.lang.StringUtils;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.RepositoryModuleService;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.ModuleIterator;
import org.drools.repository.RulesRepository;
import org.drools.workbench.jcr2vfsmigration.common.FileManager;
import org.drools.workbench.jcr2vfsmigration.jcrExport.asset.AttachmentAssetExporter;
import org.drools.workbench.jcr2vfsmigration.jcrExport.asset.ExportContext;
import org.drools.workbench.jcr2vfsmigration.jcrExport.asset.FactModelExporter;
import org.drools.workbench.jcr2vfsmigration.jcrExport.asset.GuidedDecisionTableExporter;
import org.drools.workbench.jcr2vfsmigration.jcrExport.asset.GuidedEditorExporter;
import org.drools.workbench.jcr2vfsmigration.jcrExport.asset.PlainTextAssetExporter;
import org.drools.workbench.jcr2vfsmigration.jcrExport.asset.PlainTextAssetWithPackagePropertyExporter;
import org.drools.workbench.jcr2vfsmigration.util.ExportUtils;
import org.drools.workbench.jcr2vfsmigration.xml.format.ModulesXmlFormat;
import org.drools.workbench.jcr2vfsmigration.xml.format.XmlAssetsFormat;
import org.drools.workbench.jcr2vfsmigration.xml.model.ModuleType;
import org.drools.workbench.jcr2vfsmigration.xml.model.Modules;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.IgnoredAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.XmlAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.XmlAssets;

public class ModuleAssetExporter {

    private static int assetFileName = 1;
    private static final String GLOBAL_KEYWORD = "global ";

    @Inject
    private FileManager fileManager;

    @Inject
    private ExportUtils exportUtils;

    @Inject
    private RepositoryModuleService jcrRepositoryModuleService;

    @Inject
    private RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    @Preferred
    private RulesRepository rulesRepository;

    @Inject
    private PlainTextAssetExporter plainTextAssetExporter;

    @Inject
    private PlainTextAssetWithPackagePropertyExporter plainTextAssetWithPackagePropertyExporter;

    @Inject
    private GuidedEditorExporter guidedEditorExporter;

    @Inject
    private GuidedDecisionTableExporter guidedDecisionTableExporter;

    @Inject
    private FactModelExporter factModelExporter;

    @Inject
    private AttachmentAssetExporter attachmentAssetExporter;

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
        String normalizedPackageName = exportUtils.normalizePackageName( jcrModule.getName() );
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

        // Export globalsString
        StringBuffer sbGlobal = new StringBuffer();
        List<String> lGlobals = ExportUtils.parseGlobals( packageHeaderInfo );
        if (lGlobals.size() > 0) {
            for(String global : lGlobals) {
                sbGlobal.append(GLOBAL_KEYWORD);
                sbGlobal.append(global);
                sbGlobal.append("\n");
            }
        }

        String assetExportFileName = setupAssetExportFile( jcrModule.getUuid() );

        boolean assetExportSuccess = exportModuleAssets( jcrModule, assetExportFileName );
        if ( !assetExportSuccess ) System.out.println( "An error ocurred during asset export for module " + jcrModule.getUuid() );

        return new org.drools.workbench.jcr2vfsmigration.xml.model.Module( moduleType,
                jcrModule.getUuid(),
                moduleName,
                jcrModule.getLastContributor(),
                jcrModule.getCheckinComment(),
                jcrModule.getLastModified(),
                normalizedPackageName,
                packageHeaderInfo,
                sbGlobal.toString(),
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
                    AssetItem assetItemJCR = rulesRepository.loadAssetByUUID( row.getUuid() );
                    assetName =assetItemJCR.getName();
                    System.out.format("    Asset [%s] with format [%s] is being migrated... %n",
                            assetItemJCR.getName(), assetItemJCR.getFormat());
                    //TODO: Git wont check in a version if the file is not changed in this version. Eg, the version 3 of "testFunction.function"
                    //We need to find a way to force a git check in. Otherwise migrated version history is not consistent with the version history in old Guvnor.

                    //Still need to migrate the "current version" even though in most cases the "current version" (actually it is not a version in version
                    //control, its just the current content on jcr node) is equal to the latest version that had been checked in.
                    //Eg, when we import mortgage example, we just dump the mortgage package to a jcr node, no version check in.
                    XmlAsset xmlAsset = export( ExportContext.getInstance( jcrModule, assetItemJCR, assetFileName ) );
                    xmlAsset.setAssetHistory( exportAssetHistory( ExportContext.getInstance( jcrModule, row.getUuid() ) ) );
                    assets.add( xmlAsset );

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

    private XmlAsset export( ExportContext exportContext ) {
        String name = exportContext.getJcrAssetItem().getName();
        String format = exportContext.getJcrAssetItem().getFormat();

        if ( AssetFormats.DRL_MODEL.equals( format ) ) {
            return factModelExporter.export( exportContext );

        } else if (AssetFormats.BUSINESS_RULE.equals( format )) {
            return guidedEditorExporter.export( exportContext );

        } else if (AssetFormats.DECISION_TABLE_GUIDED.equals( format )) {
            return guidedDecisionTableExporter.export( exportContext );

        } else if (AssetFormats.ENUMERATION.equals( format )
                || AssetFormats.DSL.equals( format )
                || AssetFormats.DSL_TEMPLATE_RULE.equals( format )
                || AssetFormats.RULE_TEMPLATE.equals( format )
                || AssetFormats.FORM_DEFINITION.equals( format )
                || AssetFormats.SPRING_CONTEXT.equals( format )
                || AssetFormats.SERVICE_CONFIG.equals( format )
                || AssetFormats.WORKITEM_DEFINITION.equals( format )
                || AssetFormats.CHANGE_SET.equals( format )
                || AssetFormats.RULE_FLOW_RF.equals( format )
                || AssetFormats.BPMN_PROCESS.equals( format )
                || AssetFormats.BPMN2_PROCESS.equals( format )
                || "ftl".equals( format )
                || "json".equals( format )
                || "fw".equals( format )) {
            return plainTextAssetExporter.export( exportContext );

        } else if (AssetFormats.DRL.equals( format )
                || AssetFormats.FUNCTION.equals( format )) {
            return plainTextAssetWithPackagePropertyExporter.export( exportContext );

        } else if (AssetFormats.DECISION_SPREADSHEET_XLS.equals( format )
                || AssetFormats.SCORECARD_SPREADSHEET_XLS.equals( format )
                || "png".equals( format )
                || "gif".equals( format )
                || "jpg".equals( format )
                || "pdf".equals( format )
                || "doc".equals( format )
                || "odt".equals( format )) {
            return attachmentAssetExporter.export( exportContext );

        } else if (AssetFormats.MODEL.equals( format )) {
            System.out.println("    WARNING: POJO Model jar [" + name + "] is not supported by export tool. Please add your POJO model jar to Guvnor manually.");

        } else if (AssetFormats.SCORECARD_GUIDED.equals( format )) {
            // No special treatment or attributes needed; use PlainTextAsset
            return plainTextAssetExporter.export( exportContext );

        } else if (AssetFormats.TEST_SCENARIO.equals( format )) {
            // No special treatment or attributes needed; use PlainTextAsset
            return plainTextAssetExporter.export( exportContext );

        } else if ("package".equals( format )) {
            //Ignore

        } else { //another format is migrated as a attachmentAsset
            System.out.format("    WARNING: asset [%s] with format[%s] is not a known format by export tool. It will be exported as attachmentAsset %n", name, format );
            return attachmentAssetExporter.export( exportContext );
        }
        return new IgnoredAsset();
    }

    private XmlAssets exportAssetHistory( ExportContext historyContext ) throws SerializationException {
        XmlAssets xmlAssets = new XmlAssets();

        //loadItemHistory wont return the current version
        String currentVersionAssetName="";
        try {
            TableDataResult history = jcrRepositoryAssetService.loadItemHistory( historyContext.getAssetUUID() );
            TableDataRow[] rows = history.data;
            Arrays.sort( rows,
                    new Comparator<TableDataRow>() {
                        public int compare( TableDataRow r1,
                                TableDataRow r2 ) {
                            Integer v2 = Integer.valueOf( r2.values[ 0 ] );
                            Integer v1 = Integer.valueOf( r1.values[ 0 ] );

                            return v1.compareTo( v2 );
                        }
                    } );

            String historicalAssetExportFileName = "h_" + historyContext.getAssetExportFileName();
            for (TableDataRow row : rows) {
                AssetItem historicalAssetJCR = rulesRepository.loadAssetByUUID( row.id );
                currentVersionAssetName = historicalAssetJCR.getName();

                ExportContext historicalAssetExportContext = ExportContext.getInstance( historyContext.getJcrModule(),
                                                                                        historicalAssetJCR,
                                                                                        historicalAssetExportFileName );
                xmlAssets.addAsset( export( historicalAssetExportContext ) );

                System.out.format( "    Asset (%s) with format (%s) migrated: version [%s], comment[%s], lastModified[%s]",
                        historicalAssetJCR.getName(), historicalAssetJCR.getFormat(), historicalAssetJCR.getVersionNumber(), historicalAssetJCR.getCheckinComment(), historicalAssetJCR.getLastModified().getTime() );
            }
        } catch ( RuntimeException e ){
            System.out.println( "Exception migrating assetHistory at version: " + currentVersionAssetName +
                    " from module: " + historyContext.getJcrModule().getName() );
        }
        return  xmlAssets;
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
