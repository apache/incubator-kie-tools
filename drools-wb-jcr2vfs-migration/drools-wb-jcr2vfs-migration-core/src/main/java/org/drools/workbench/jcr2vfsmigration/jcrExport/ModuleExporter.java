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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.server.RepositoryModuleService;
import org.drools.workbench.jcr2vfsmigration.util.FileManager;
import org.drools.workbench.jcr2vfsmigration.xml.format.ModulesXmlFormat;
import org.drools.workbench.jcr2vfsmigration.xml.model.Module;
import org.drools.workbench.jcr2vfsmigration.xml.model.ModuleType;
import org.drools.workbench.jcr2vfsmigration.xml.model.Modules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ModuleExporter {

    protected static final Logger logger = LoggerFactory.getLogger( ModuleExporter.class );

    @Inject
    protected RepositoryModuleService jcrRepositoryModuleService;

    @Inject
    FileManager fileManager;

    ModulesXmlFormat modulesXmlFormat = new ModulesXmlFormat();

    public void exportAll() {

        System.out.println( "  Module export started" );
        org.drools.guvnor.client.rpc.Module jcrGlobalModule = jcrRepositoryModuleService.loadGlobalModule();
        org.drools.guvnor.client.rpc.Module[] jcrModules = jcrRepositoryModuleService.listModules();

        if ( jcrGlobalModule == null && jcrModules.length == 0 ) {
            System.out.println( "  No modules to be exported" );
            return;
        }

        Collection<Module> normalModules = new ArrayList<Module>( 5 );
        for ( org.drools.guvnor.client.rpc.Module jcrModule : jcrModules ) {
            normalModules.add( export( ModuleType.NORMAL, jcrModule ) );
        }

        Module globalModule = export( ModuleType.GLOBAL, jcrGlobalModule );

        Modules modules = new Modules( globalModule, normalModules );

        StringBuilder xml = new StringBuilder();
        modulesXmlFormat.format( xml, modules );

        PrintWriter pw = fileManager.createModuleExportFileWriter();
        pw.print( xml.toString() );
        pw.close();

        System.out.println( "  Module export ended" );
    }

    private Module export( ModuleType moduleType, org.drools.guvnor.client.rpc.Module jcrModule ) {
        System.out.format( "Module [%s] exported. %n", jcrModule.getName() );
        return new Module( moduleType, jcrModule.getUuid(), jcrModule.getName(), jcrModule.getCatRules() );
    }
}
