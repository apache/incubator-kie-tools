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
import java.util.List;
import javax.inject.Inject;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.RepositoryModuleService;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;
import org.drools.workbench.jcr2vfsmigration.jcrExport.asset.PlainTextAssetExporter;
import org.drools.workbench.jcr2vfsmigration.util.FileManager;
import org.drools.workbench.jcr2vfsmigration.xml.format.XmlFormat;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.XmlAsset;
import org.uberfire.backend.vfs.Path;

public class ModuleAssetExporter {

    @Inject
    FileManager fileManager;

    @Inject
    protected RepositoryModuleService jcrRepositoryModuleService;

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    @Preferred
    private RulesRepository rulesRepository;

    @Inject
    PlainTextAssetExporter plainTextAssetExporter;

    public void exportAll() {
        System.out.println("  Asset export started");

        Module[] jcrModules = jcrRepositoryModuleService.listModules();
        List<Module> modules = new ArrayList<Module>( Arrays.asList( jcrModules ));
        Module globalModule = jcrRepositoryModuleService.loadGlobalModule();
        modules.add( globalModule );

        for (Module jcrModule : modules) {

            StringBuilder xml = new StringBuilder();
            PrintWriter pw = null;
            try {
                // TODO what if the uuid was null or the filename with uuid too long, and as a result an "1.xml" => perform export during module export?
                // file was created for this module's assets?
                pw = fileManager.createAssetExportFileWriter( jcrModule.getUuid() );
            } catch ( FileNotFoundException e ) {
                System.out.println( e.getMessage() );
                continue;
            }

            // TODO need 'generic' asset formatter (for just this, or would it make things easier)?
            xml.append( "<assets>" );
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

                        XmlAsset asset = export( jcrModule, assetItemJCR, null );
                        if ( asset != null ) {
                            XmlFormat xmlFormat = asset.getXmlFormat();
                            xmlFormat.format( xml, asset );
                        }

                        System.out.format("    Done.%n");
                    }
                } catch (SerializationException e) {
                    System.out.println("SerializationException exporting asset: " + assetName +" from module: "+jcrModule.getName());
                    throw new IllegalStateException(e);
                } catch (Exception e) {
                    System.out.println("Exception migrating exporting: " + assetName +" from module: "+jcrModule.getName());
                    throw new IllegalStateException(e);
                }

                if (response.isLastPage()) {
                    hasMorePages = false;
                } else {
                    startRowIndex += pageSize;
                }
            }
            xml.append( "</assets" );
            pw.print( xml.toString() );
            pw.close();
        }
    }

    private XmlAsset export(Module jcrModule, AssetItem jcrAssetItem, Path previousVersionPath) throws SerializationException {
        if ( AssetFormats.DRL_MODEL.equals(jcrAssetItem.getFormat())) {
//            return factModelsMigrater.migrate(jcrModule, jcrAssetItem, previousVersionPath);
        } else if (AssetFormats.BUSINESS_RULE.equals(jcrAssetItem.getFormat())) {
//            return guidedEditorMigrater.migrate(jcrModule, jcrAssetItem, previousVersionPath);
        } else if (AssetFormats.DECISION_TABLE_GUIDED.equals(jcrAssetItem.getFormat())) {
//            return guidedDecisionTableMigrater.migrate(jcrModule, jcrAssetItem, previousVersionPath);
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
//            return plainTextAssetWithPackagePropertyMigrater.migrate(jcrModule, jcrAssetItem, previousVersionPath);
        } else if (AssetFormats.DECISION_SPREADSHEET_XLS.equals(jcrAssetItem.getFormat())
                || AssetFormats.SCORECARD_SPREADSHEET_XLS.equals(jcrAssetItem.getFormat())
                || "png".equals(jcrAssetItem.getFormat())
                || "gif".equals(jcrAssetItem.getFormat())
                || "jpg".equals(jcrAssetItem.getFormat())
                || "pdf".equals(jcrAssetItem.getFormat())
                || "doc".equals(jcrAssetItem.getFormat())
                || "odt".equals(jcrAssetItem.getFormat())) {
//            return attachementAssetMigrater.migrate(jcrModule, jcrAssetItem, previousVersionPath);
        } else if (AssetFormats.MODEL.equals(jcrAssetItem.getFormat())) {
            System.out.println("    WARNING: POJO Model jar [" + jcrAssetItem.getName() + "] is not supported by export tool. Please add your POJO model jar to Guvnor manually.");
        } else if (AssetFormats.SCORECARD_GUIDED.equals(jcrAssetItem.getFormat())) {
//            return guidedScoreCardMigrater.migrate(jcrModule, jcrAssetItem, previousVersionPath);
        } else if (AssetFormats.TEST_SCENARIO.equals(jcrAssetItem.getFormat())) {
//            return testScenarioMigrater.migrate(jcrModule, jcrAssetItem, previousVersionPath);
        } else if ("package".equals(jcrAssetItem.getFormat())) {
            //Ignore
        } else { //another format is migrated as a attachmentAsset
            System.out.format("    WARNING: asset [%s] with format[%s] is not a known format by export tool. It will be exported as attachmentAsset %n", jcrAssetItem.getName(), jcrAssetItem.getFormat());
//            return attachementAssetMigrater.migrate(jcrModule, jcrAssetItem, previousVersionPath);
        }
        return null;
    }
}
