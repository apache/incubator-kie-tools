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
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryModuleService;
import org.drools.workbench.jcr2vfsmigration.util.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ModuleExporter {

    protected static final Logger logger = LoggerFactory.getLogger( ModuleExporter.class );

    public static final String GLOBAL_MODULE = "globalModule";
    public static final String MODULE = "module";
    public static final String MODULE_UUID = "moduleUUID";
    public static final String MODULE_NAME = "moduleName";

    @Inject
    protected RepositoryModuleService jcrRepositoryModuleService;

    @Inject
    FileManager fileManager;

    public void exportAll() {

        System.out.println( "  Module export started" );
        Module globalModule = jcrRepositoryModuleService.loadGlobalModule();
        Module[] jcrModules = jcrRepositoryModuleService.listModules();

        if ( globalModule == null && jcrModules.length == 0 ) {
            System.out.println( "  No modules to be exported" );
            return;
        }

        PrintWriter pw = fileManager.createModuleExportFileWriter();
        pw.println( "<modules>" );

        for ( Module jcrModule : jcrModules ) {
            export( MODULE, jcrModule, pw );
        }

        export( GLOBAL_MODULE, globalModule, pw );

        pw.println( "</modules>" );
        pw.close();

        System.out.println( "  Module export ended" );
    }

    private void export( String moduleType, Module jcrModule, PrintWriter pw ) {
        pw.println( "  <" + moduleType + ">" );
        pw.println( "    <" + MODULE_UUID + ">" + jcrModule.getUuid() + "</" + MODULE_UUID + ">" );
        pw.println( "    <" + MODULE_NAME + ">" + jcrModule.getName() + "</" + MODULE_NAME + ">" );
        pw.println( "  </" + moduleType + ">" );
        System.out.format( "Module [%s] exported. %n", jcrModule.getName() );
    }
}
