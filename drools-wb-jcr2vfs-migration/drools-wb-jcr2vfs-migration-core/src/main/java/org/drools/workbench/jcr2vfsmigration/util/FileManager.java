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
package org.drools.workbench.jcr2vfsmigration.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FileManager {

    private static final String MODULES_FILE = "modules.xml";
    private static final String CATEGORIES_FILE = "categories.xml";

    private File tempDir;

    public FileManager() {
    }

    public void setExportTempDir( File tempDir ) {
        this.tempDir = tempDir;
    }

    public PrintWriter createModuleExportFileWriter() {
        return createFileWriter( MODULES_FILE );
    }

    public File getModulesExportFile() throws FileNotFoundException {
        return getFile( MODULES_FILE );
    }

    public PrintWriter createCategoryExportFileWriter() {
        return createFileWriter( CATEGORIES_FILE );
    }

    public File getCategoriesExportFile() throws FileNotFoundException {
        return getFile( CATEGORIES_FILE );
    }

    private PrintWriter createFileWriter( String fileName ) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter( new File( tempDir, fileName ) );
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
            System.out.println("Error creating the " + fileName + " file");
        }
        return pw;
    }

    private File getFile( String fileName ) throws FileNotFoundException {
        File f = new File( tempDir, fileName );
        if ( !f.exists() ) throw new FileNotFoundException( "File " + fileName + " not found" );
        return f;
    }
}
