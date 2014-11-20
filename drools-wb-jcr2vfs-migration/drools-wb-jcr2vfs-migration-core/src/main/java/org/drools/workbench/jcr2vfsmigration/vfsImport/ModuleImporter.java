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
package org.drools.workbench.jcr2vfsmigration.vfsImport;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryModuleService;
import org.drools.workbench.jcr2vfsmigration.jcrExport.ModuleExporter;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.drools.workbench.jcr2vfsmigration.util.FileManager;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@ApplicationScoped
public class ModuleImporter {

    protected static final Logger logger = LoggerFactory.getLogger( ModuleImporter.class );

    @Inject
    FileManager fileManager;

    @Inject
    protected MigrationPathManager migrationPathManager;

    @Inject
    protected ProjectService projectService;

    public void importAll() {
        System.out.println( "  Module import started" );

        Document xml = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            xml = db.parse( fileManager.getModulesExportFile() );
            NodeList nodeList = xml.getElementsByTagName( ModuleExporter.MODULE );
            for ( int i = 0; i < nodeList.getLength(); i++ ) {
                Node module = nodeList.item( i );
                NodeList moduleAttributes = module.getChildNodes();
                for ( int j = 0; j < moduleAttributes.getLength(); j++ ) {
                    Node attributeNode = moduleAttributes.item( j );
                    if ( ModuleExporter.MODULE_NAME.equalsIgnoreCase( attributeNode.getNodeName() ) ) {
                        importModule( attributeNode.getTextContent() );
                    }
                }
            }
            nodeList = xml.getElementsByTagName( ModuleExporter.GLOBAL_MODULE );
            Node module = nodeList.item( 0 );
            NodeList moduleAttributes = module.getChildNodes();
            for ( int j = 0; j < moduleAttributes.getLength(); j++ ) {
                Node attributeNode = moduleAttributes.item( j );
                if ( ModuleExporter.MODULE_NAME.equalsIgnoreCase( attributeNode.getNodeName() ) ) {
                    importModule( attributeNode.getTextContent() );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        System.out.println( "  Module import ended" );
    }

    private void importModule( String moduleName ) {
        //Set up project structure:

        String normalizedModuleName = migrationPathManager.normalizePackageName( moduleName );
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

        Path modulePath = migrationPathManager.generateRootPath();
        projectService.newProject( makeRepository( modulePath ),
                                   normalizedModuleName,
                                   pom,
                                   "http://localhost" );
    }

    private org.guvnor.structure.repositories.Repository makeRepository( final Path repositoryRoot ) {
        return new GitRepository() {

            @Override
            public Path getRoot() {
                return repositoryRoot;
            }
        };
    }

}
