/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.jcr2vfsmigration.vfsImport;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.drools.workbench.jcr2vfsmigration.common.FileManager;
import org.drools.workbench.jcr2vfsmigration.util.MigrationPathManager;
import org.drools.workbench.jcr2vfsmigration.util.PackageImportHelper;
import org.drools.workbench.jcr2vfsmigration.vfsImport.asset.AttachmentAssetImporter;
import org.drools.workbench.jcr2vfsmigration.vfsImport.asset.FactModelImporter;
import org.drools.workbench.jcr2vfsmigration.vfsImport.asset.GuidedDecisionTableImporter;
import org.drools.workbench.jcr2vfsmigration.vfsImport.asset.GuidedEditorImporter;
import org.drools.workbench.jcr2vfsmigration.vfsImport.asset.GuidedScoreCardImporter;
import org.drools.workbench.jcr2vfsmigration.vfsImport.asset.PlainTextAssetImporter;
import org.drools.workbench.jcr2vfsmigration.vfsImport.asset.PlainTextAssetWithPackagePropertyImporter;
import org.drools.workbench.jcr2vfsmigration.vfsImport.asset.TestScenarioImporter;
import org.drools.workbench.jcr2vfsmigration.xml.format.ModulesXmlFormat;
import org.drools.workbench.jcr2vfsmigration.xml.format.XmlAssetsFormat;
import org.drools.workbench.jcr2vfsmigration.xml.model.Module;
import org.drools.workbench.jcr2vfsmigration.xml.model.Modules;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.AttachmentAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.BusinessRuleAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.DataModelAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.GuidedDecisionTableAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.PlainTextAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.XmlAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.XmlAssets;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@ApplicationScoped
public class ModuleAssetImporter {

    private static final Logger logger = LoggerFactory.getLogger(ModuleAssetImporter.class);

    @Inject
    private Paths paths;

    @Inject
    private FileManager fileManager;

    @Inject
    private MigrationPathManager migrationPathManager;

    @Inject
    private PackageImportHelper packageImportHelper;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private FactModelImporter factModelImporter;

    @Inject
    private ProjectService projectService;

    @Inject
    private PlainTextAssetImporter plainTextAssetImporter;

    @Inject
    private PlainTextAssetWithPackagePropertyImporter plainTextAssetWithPackagePropertyImporter;

    @Inject
    private GuidedScoreCardImporter guidedScoreCardImporter;

    @Inject
    private GuidedEditorImporter guidedEditorImporter;

    @Inject
    private GuidedDecisionTableImporter guidedDecisionTableImporter;

    @Inject
    private TestScenarioImporter testScenarioImporter;

    @Inject
    private AttachmentAssetImporter attachmentAssetImporter;

    private ModulesXmlFormat modulesXmlFormat = new ModulesXmlFormat();
    private XmlAssetsFormat xmlAssetsFormat = new XmlAssetsFormat();

    public void importAll() {
        logger.info( "  Module import started" );
        Document xml;
        try {
            File modulesXmlFile = fileManager.getModulesExportFile();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            xml = db.parse( modulesXmlFile );
            NodeList children = xml.getChildNodes();
            if ( children.getLength() > 1 ) {
                throw new Exception( "Wrong modules.xml format" );
            }

            Modules modules = modulesXmlFormat.parse( children.item( 0 ) );

            // import 'normal' modules
            for ( Iterator<Module> moduleIterator = modules.getModules().iterator(); moduleIterator.hasNext(); ) {
                importModule( moduleIterator.next() );
            }

            // import 'global' module
            importModule( modules.getGlobalModule() );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        logger.info( "  Module import ended" );
    }

    private void importModule( Module module ) {
        logger.info("    Importing module [{}] (UUID={})", module.getName(), module.getUuid());
        //Set up project structure:
        String normalizedModuleName = module.getNormalizedPackageName();
        String[] nameSplit = normalizedModuleName.split( "\\." );

        StringBuilder groupIdBuilder = new StringBuilder();
        groupIdBuilder.append( nameSplit[ 0 ] );
        for ( int i = 1; i < nameSplit.length - 1; i++ ) {
            groupIdBuilder.append( "." );
            groupIdBuilder.append( nameSplit[ i ] );
        }

        String groupId = groupIdBuilder.toString();
        String artifactId = nameSplit[ nameSplit.length - 1 ];
        GAV gav = new GAV( groupId,
                           artifactId,
                           "0.0.1" );
        POM pom = new POM( gav );
        pom.setName( normalizedModuleName );
        Path modulePath = migrationPathManager.generateRootPath();
        try {
            projectService.newProject( modulePath,
                                       pom,
                                       "http://localhost" );
        } catch ( GAVAlreadyExistsException gae ) {
            logger.warn( "Project's GAV [{}] already exists at [{}]!", pom.getGav(), toString( gae.getRepositories() ), gae );
        }

        try {
            importAssets( module );
        } catch ( Exception e ) {
            // just log the error and continue importing the rest
            // it is better to try to import as many things as possible, instead of failing fast directly
            logger.error("Exception while importing assets for module '{}'.", module.getName(), e);
        }

        // Import globals
        String globals = module.getGlobalsString();
        if ( globals == null || "".equals( globals ) ) {
            return;
        }

        Path path = migrationPathManager.generatePathForGlobal( module );
        final org.uberfire.java.nio.file.Path nioPath = paths.convert( path );

        String contentWithImport = packageImportHelper.assertPackageImportDRL( globals, module.getPackageHeaderInfo(), path );
        String contentWithPackage = packageImportHelper.assertPackageName( contentWithImport, null );

        ioService.write( nioPath,
                         contentWithPackage,
                         (Map<String, ?>) null,    // cast is for disambiguation
                         new CommentedOption( module.getLastContributor(),
                                              null,
                                              module.getCheckinComment(),
                                              module.getLastModified() )
                       );
    }

    private String toString( final Set<MavenRepositoryMetadata> repositories ) {
        final StringBuilder sb = new StringBuilder();
        for ( MavenRepositoryMetadata md : repositories ) {
            sb.append( md.getId() ).append( " : " ).append( md.getUrl() ).append( " : " ).append( md.getSource() ).append( ", " );
        }
        sb.delete( sb.length() - 2,
                   sb.length() - 1 );
        return sb.toString();
    }

    private void importAssets( Module module ) throws IOException, SAXException, ParserConfigurationException {
        Document xml;
        File assetsXmlFile = fileManager.getAssetExportFile(module.getAssetExportFileName());

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        xml = db.parse(assetsXmlFile);
        NodeList rootNodeList = xml.getChildNodes();
        if (rootNodeList.getLength() > 1) {
            throw new RuntimeException("Wrong asset file XML format!");
        }
        Node assetsNode = rootNodeList.item(0);

        XmlAssets xmlAssets = xmlAssetsFormat.parse(assetsNode);

        for (XmlAsset xmlAsset : xmlAssets.getAssets()) {
            if (xmlAsset == null) {
                logger.warn("      Skipping null asset during import.");
                continue;
            }
            logger.info("      Importing asset [{}.{}].", xmlAsset.getName(), xmlAsset.getAssetType());
            try {
                importAssetHistory(module, xmlAsset);
                importAsset(module, xmlAsset, null);
            } catch (Exception e) {
                // just log error and continue importing the rest of the assets
                // it is better to at least try to import the rest as there is a high chance that the other assets
                // will be imported successfully
                logger.error("Exception while importing asset [{}.{}].", xmlAsset.getName(), xmlAsset.getAssetType(), e);
            }
        }
    }


    private Path importAsset( Module module,
                              XmlAsset xmlAsset,
                              Path previousVersionPath ) {
        switch ( xmlAsset.getAssetType() ) {
            case DRL_MODEL:
                return factModelImporter.importAsset( module, (DataModelAsset) xmlAsset, previousVersionPath );

            case ENUMERATION:
            case DSL:
            case DSL_TEMPLATE_RULE:
            case RULE_TEMPLATE:
            case FORM_DEFINITION:
            case SPRING_CONTEXT:
            case SERVICE_CONFIG:
            case WORKITEM_DEFINITION:
            case CHANGE_SET:
            case RULE_FLOW_RF:
            case BPMN_PROCESS:
            case BPMN2_PROCESS:
            case FTL:
            case JSON:
            case FW:
                return plainTextAssetImporter.importAsset( module, (PlainTextAsset) xmlAsset, previousVersionPath );

            case DRL:
            case FUNCTION:
                return plainTextAssetWithPackagePropertyImporter.importAsset( module, (PlainTextAsset) xmlAsset, previousVersionPath );

            case DECISION_SPREADSHEET_XLS:
            case SCORECARD_SPREADSHEET_XLS:
            case PNG:
            case GIF:
            case JPG:
            case PDF:
            case DOC:
            case ODT:
                return attachmentAssetImporter.importAsset( module, (AttachmentAsset) xmlAsset, previousVersionPath );

            case SCORECARD_GUIDED:
                return guidedScoreCardImporter.importAsset( module, (PlainTextAsset) xmlAsset, previousVersionPath );

            case BUSINESS_RULE:
                return guidedEditorImporter.importAsset( module, (BusinessRuleAsset) xmlAsset, previousVersionPath );

            case DECISION_TABLE_GUIDED:
                return guidedDecisionTableImporter.importAsset( module, (GuidedDecisionTableAsset) xmlAsset, previousVersionPath );

            case TEST_SCENARIO:
                return testScenarioImporter.importAsset( module, (PlainTextAsset) xmlAsset, previousVersionPath );

            case UNSUPPORTED:

            default:
                return attachmentAssetImporter.importAsset( module, (AttachmentAsset) xmlAsset, previousVersionPath );
        }
    }

    private void importAssetHistory( Module module,
                                     XmlAsset xmlAsset ) {
        Path previousVersionPath = null;
        XmlAssets history = xmlAsset.getAssetHistory();
        if ( history == null || history.getAssets().size() == 0 ) {
            return;
        }
        for ( XmlAsset hAsset : history.getAssets() ) {
            previousVersionPath = importAsset( module, hAsset, previousVersionPath );
        }
    }

}
