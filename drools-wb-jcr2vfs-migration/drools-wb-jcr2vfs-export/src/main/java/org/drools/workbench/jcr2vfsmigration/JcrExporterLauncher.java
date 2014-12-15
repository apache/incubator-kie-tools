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
package org.drools.workbench.jcr2vfsmigration;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public class JcrExporterLauncher {

    private Weld weld;
    private WeldContainer weldContainer;
    private JcrExporter exporter;

    public static void main( String[] args ) {
        try {
            new JcrExporterLauncher().run( args );
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( -1 );
        }
    }

    /**
     * Use this method instead of #main() when you want to use the app directly from code. Method does not use System.exit()
     * and instead throws {@link RuntimeException} when an error occurs.
     * @param args application arguments - same as for #main() method
     */
    public void run( String... args ) {
        startUp();
        try {
            launchExport( args );
        } finally {
            shutdown();
        }
    }

    public void launchExport( String... args ) {
        if ( exporter.parseArgs( args ) ) {
            exporter.exportAll();
        }
    }

    public void shutdown() {
        weld.shutdown();
    }

    public void startUp() {
        weld = new Weld();
        weldContainer = weld.initialize();
        exporter = weldContainer.instance().select( JcrExporter.class ).get();
    }
}
