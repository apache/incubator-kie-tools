/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
public class VfsImportConfig {

    private static final Logger logger = LoggerFactory.getLogger(VfsImportConfig.class);

    private static final String DEFAULT_MIGRATION_FILE_SYSTEM = "guvnor-jcr2vfs-migration";

    private static final String FORMAT_STR = "runMigration  [options...]";

    private File importTempDir;
    private File outputVfsRepository;

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
            formatter.printHelp( FORMAT_STR, options );
            return false;
        }

        if ( commandLine.hasOption( "h" ) ) {
            formatter.printHelp( FORMAT_STR, options );
            return false;
        }

        return ( parseArgInputTempDir( commandLine ) && parseArgOutputVfsRepository( commandLine ) );
    }

    private boolean parseArgInputTempDir( CommandLine commandLine ) {
        importTempDir = new File( commandLine.getOptionValue( "i", "./jcrExport" ) );
        try {
            if ( importTempDir.isFile() ) {
                logger.error( "The specified export directory specified ({}) is not a directory!", importTempDir.getAbsolutePath() );
                return false;
            }
        } catch ( Exception e ) {
            logger.error( "The specified export directory ({}) has issues!", importTempDir, e );
            return false;
        }
        return true;
    }

    private boolean parseArgOutputVfsRepository( CommandLine commandLine ) {
        outputRepoName = commandLine.getOptionValue( "r", DEFAULT_MIGRATION_FILE_SYSTEM );
        outputVfsRepository = new File( commandLine.getOptionValue( "o", "outputVfs" ) );
        boolean forceOverwriteOutputVfsRepository = commandLine.hasOption( "f" );
        if ( outputVfsRepository.exists() ) {
            if ( forceOverwriteOutputVfsRepository ) {
                try {
                    FileUtils.deleteDirectory( outputVfsRepository );
                } catch ( IOException e ) {
                    logger.error( "Force deleting outputVfsRepository ({}) failed!", outputVfsRepository.getAbsolutePath(), e );
                    return false;
                }
            } else {
                logger.error( "The outputVfsRepository ({}) already exists!", outputVfsRepository.getAbsolutePath() );
                return false;
            }
        }
        try {
            outputVfsRepository = outputVfsRepository.getCanonicalFile();
            FileUtils.forceMkdir(outputVfsRepository);
        } catch ( IOException e ) {
            logger.error( "The outputVfsRepository ({}) has issues!", outputVfsRepository, e );
            return false;
        }
        return true;
    }

}
