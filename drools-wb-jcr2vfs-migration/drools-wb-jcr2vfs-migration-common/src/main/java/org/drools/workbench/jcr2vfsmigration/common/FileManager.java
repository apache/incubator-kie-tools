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
package org.drools.workbench.jcr2vfsmigration.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FileManager {

    private static final String XML_EXTENSION = ".xml";
    private static final String BIN_DIR = "bin";

    private static final String MODULES_FILE = "modules" + XML_EXTENSION;
    private static final String CATEGORIES_FILE = "categories" + XML_EXTENSION;

    private File tempDir;
    private File binDir;

    public FileManager() {
    }

    public void setExportTempDir( File tempDir ) {
        this.tempDir = tempDir;
        this.binDir = new File( tempDir, BIN_DIR );
        binDir.mkdirs();
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

    public PrintWriter createAssetExportFileWriter( String fileName ) throws FileNotFoundException {
        return createFileWriter( getFile( fileName + XML_EXTENSION ) );
    }

    /**
     * Test if the asset filename can be created, using the module uuid.
     * Just in case the filename might become too long (windows), in which case we can try to shorten it a bit.
     */
    public boolean createAssetExportFile( String fileName ) {
        return doCreateFile( new File( tempDir, fileName + XML_EXTENSION ) );
    }

    public File getAssetExportFile( String fileName ) throws FileNotFoundException {
        return getFile( fileName + XML_EXTENSION );
    }

    public boolean writeBinaryContent( String fileName, byte[] bytes ) {
        if ( fileName == null ) return false;
        File bFile = new File( binDir, fileName );
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream( new FileOutputStream( bFile ) );
            bos.write( bytes );
        } catch ( Exception e ) {
            System.out.println("Error while creating binary file " + fileName + "; " + e.getMessage());
            return false;
        } finally {
            try {
                if ( bos != null ) bos.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public byte[] readBinaryContent( String fileName ) {
        if ( fileName == null ) return null;
        BufferedInputStream bis = null;
        try {
            File bFile = getBinaryFile( fileName );
            bis = new BufferedInputStream( new FileInputStream( bFile ) );
            byte[] bytes = new byte[ ( int ) bFile.length() ];

            int offset = 0;
            int numRead = 0;
            while ( offset < bytes.length &&
                    ( numRead = bis.read( bytes, offset, bytes.length - offset ) ) >= 0 ) {
                offset += numRead;
            }

            if ( offset < bytes.length ) {
                System.out.println( "Binary file " + fileName + " was not completely read" );
            }

            return bytes;
        } catch ( Exception e ) {
            System.out.println("Error while creating binary file " + fileName + "; " + e.getMessage());
        } finally {
            try {
                if ( bis != null ) bis.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private boolean doCreateFile( File file ) {
        boolean success = false;
        try {
            success = file.createNewFile();
        } catch ( IOException ioe ) {
            System.out.println("Error while creating file " + file.getName() + "; " + ioe.getMessage());
        }
        return success;
    }

    private PrintWriter createFileWriter( String fileName ) {
        PrintWriter pw = null;
        File f = new File (tempDir, fileName );
        doCreateFile( f );
        pw = createFileWriter( f );
        return pw;
    }

    private PrintWriter createFileWriter( File file ) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter( file );
        } catch ( FileNotFoundException fnfe ) {
            System.out.println( "Error creating file writer for: " + file.getName() + "; " + fnfe.getMessage() );
        }
        return pw;
    }

    private File getFile( String fileName ) throws FileNotFoundException {
        File f = new File( tempDir, fileName );
        if ( !f.exists() ) throw new FileNotFoundException( "File " + fileName + " not found" );
        return f;
    }

    private File getBinaryFile( String fileName ) throws FileNotFoundException {
        File f = new File( binDir, fileName );
        if ( !f.exists() ) throw new FileNotFoundException( "Binary file " + fileName + " not found" );
        return f;
    }
}
