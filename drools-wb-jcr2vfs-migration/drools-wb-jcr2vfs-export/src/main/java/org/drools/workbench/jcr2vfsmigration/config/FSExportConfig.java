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
public class FSExportConfig {
    private static final Logger logger = LoggerFactory.getLogger(FSExportConfig.class);

    private static final String FORMAT_STR = "runMigration  [options...]";

    private File inputJcrRepository;
    private File exportTempDir;
    private boolean forceOverwriteTempOutputDirectory;

    public File getInputJcrRepository() {
        return inputJcrRepository;
    }

    public File getExportTempDir() {
        return exportTempDir;
    }

    // ************************************************************************
    // Configuration methods
    // ************************************************************************

    public boolean parseArgs( String[] args ) {
        Options options = new Options();
        options.addOption( "h", "help", false, "help for the command." );
        options.addOption( "i", "inputJcrRepository", true, "The Guvnor 5 JCR repository" );
        options.addOption( "o", "exportTempDir", true, "The temporary export directory" );
        options.addOption( "f", "forceOverwriteTempOutputDirectory", false, "Force overwriting the temporary output directory" );

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

        return ( parseArgInputJcrRepository( commandLine ) && parseArgExportTempDir( commandLine ) );
    }

    private boolean parseArgInputJcrRepository( CommandLine commandLine ) {
        inputJcrRepository = new File( commandLine.getOptionValue( "i", "inputJcr" ) );
        if ( !inputJcrRepository.exists() ) {
            logger.error( "The inputJcrRepository ({}) does not exist. Please make sure your inputJcrRepository exists," +
                    "or use -i to specify alternative location.",  inputJcrRepository.getAbsolutePath() );
            return false;
        }

        try {
            inputJcrRepository = inputJcrRepository.getCanonicalFile();
        } catch ( IOException e ) {
            logger.error( "The inputJcrRepository ({}) has issues!", inputJcrRepository, e );
            return false;
        }

        return true;
    }

    private boolean parseArgExportTempDir( CommandLine commandLine ) {
        exportTempDir = new File( commandLine.getOptionValue( "o", "./jcrExport" ) );
        forceOverwriteTempOutputDirectory = commandLine.hasOption( "f" );
        try {
            if ( exportTempDir.isFile() ) {
                logger.error( "The specified export location ({}) is not a directory!", exportTempDir.getAbsolutePath() );
                return false;
            }
            if ( exportTempDir.exists() ) {
                if ( forceOverwriteTempOutputDirectory ) {
                    FileUtils.deleteDirectory( exportTempDir );
                } else {
                    logger.error( "The export directory ({}) already exists!", exportTempDir.getAbsolutePath() );
                    return false;
                }
            }
            FileUtils.forceMkdir( exportTempDir );
        } catch ( Exception e ) {
            logger.error( "The export directory ({}) has issues!", exportTempDir, e );
            return false;
        }
        return true;
    }
}
