/*
 * Copyright 2012 JBoss Inc
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
package org.drools.workbench.jcr2vfsmigration.config;

import java.io.File;
import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class VfrImportConfig {

    protected static final Logger logger = LoggerFactory.getLogger( VfrImportConfig.class );

    private static String DEFAULT_MIGRATION_FILE_SYSTEM = "guvnor-jcr2vfs-migration";

    public static final String formatstr = "runMigration  [options...]";

    private File importTempDir;
    private File outputVfsRepository;

    private boolean forceOverwriteOutputVfsRepository;
    private String outputRepoName;

    public File getImportTempDir() {
        return importTempDir;
    }

    public File getOutputVfsRepository() {
        return outputVfsRepository;
    }

    public String getOutputRepoName() {
        return outputRepoName;
    }

    // ************************************************************************
    // Configuration methods
    // ************************************************************************

    public boolean parseArgs( String[] args ) {
        Options options = new Options();
        options.addOption( "h", "help", false, "help for the command." );
        options.addOption( "i", "importTempDir", true, "The temporary import directory" );
        options.addOption( "o", "outputVfsRepository", true, "The Guvnor 6 VFS repository" );
        options.addOption( "r", "repoName", true, "The Guvnor 6 VFS Repository name" );
        options.addOption( "f", "forceOverwriteOutputVfsRepository", false, "Force overwriting the Guvnor 6 VFS repository" );

        CommandLine commandLine;
        HelpFormatter formatter = new HelpFormatter();
        try {
            commandLine = new BasicParser().parse( options, args );
        } catch ( ParseException e ) {
            formatter.printHelp( formatstr, options );
            return false;
        }

        if ( commandLine.hasOption( "h" ) ) {
            formatter.printHelp( formatstr, options );
            return false;
        }

        return ( parseArgInputTempDir( commandLine ) && parseArgOutputVfsRepository( commandLine ) );
    }

    private boolean parseArgInputTempDir( CommandLine commandLine ) {
        importTempDir = new File( commandLine.getOptionValue( "i", "./jcrExport" ) );
        try {
            if ( importTempDir.isFile() ) {
                System.out.println( "The export directory specified (" + importTempDir.getAbsolutePath() + ") is not a directory." );
                return false;
            }
        } catch ( Exception e ) {
            System.out.println( "The export directory (" + importTempDir + ") has issues: " + e );
            return false;
        }
        return true;
    }

    private boolean parseArgOutputVfsRepository( CommandLine commandLine ) {
        outputRepoName = commandLine.getOptionValue( "r", DEFAULT_MIGRATION_FILE_SYSTEM );
        outputVfsRepository = new File( commandLine.getOptionValue( "o", "outputVfs" ) );
        forceOverwriteOutputVfsRepository = commandLine.hasOption( "f" );
        if ( outputVfsRepository.exists() ) {
            if ( forceOverwriteOutputVfsRepository ) {
                try {
                    FileUtils.deleteDirectory( outputVfsRepository );
                } catch ( IOException e ) {
                    System.out.println( "Force deleting outputVfsRepository (" + outputVfsRepository.getAbsolutePath() + ") failed: " + e );
                    return false;
                }
            } else {
                System.out.println( "The outputVfsRepository (" + outputVfsRepository.getAbsolutePath() + ") already exists." );
                return false;
            }
        }
        try {
            outputVfsRepository = outputVfsRepository.getCanonicalFile();
            FileUtils.forceMkdir(outputVfsRepository);
        } catch ( IOException e ) {
            System.out.println( "The outputVfsRepository (" + outputVfsRepository + ") has issues: " + e );
            return false;
        }
        return true;
    }

}
