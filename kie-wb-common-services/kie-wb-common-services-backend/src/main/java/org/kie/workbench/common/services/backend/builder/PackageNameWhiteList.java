/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.backend.builder;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.services.backend.file.AntPathMatcher;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Files;

/**
 * Represents a "white list" of permitted package names for use with authoring
 */
public class PackageNameWhiteList {

    private static final Logger logger = LoggerFactory.getLogger( PackageNameWhiteList.class );

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private IOService ioService;

    @Inject
    public PackageNameWhiteList( final @Named("ioStrategy") IOService ioService ) {
        this.ioService = ioService;
    }

    /**
     * Filter the provided Package names by the Project's white list
     * @param project Project for which to filter Package names
     * @param packageNames All Package names in the Project
     * @return A filtered collection of Package names
     */
    public Set<String> filterPackageNames( final Project project,
                                           final Collection<String> packageNames ) {
        final Set<String> packageNamesWhiteList = new HashSet<String>();
        if ( packageNames == null ) {
            return packageNamesWhiteList;
        }
        packageNamesWhiteList.addAll( packageNames );
        if ( !( project instanceof KieProject ) ) {
            return packageNamesWhiteList;
        }

        final String content = readPackageNameWhiteList( (KieProject) project );
        if ( !( content == null || content.trim().isEmpty() ) ) {

            //If a White List is defined build set of acceptable Package Names from it
            packageNamesWhiteList.clear();
            final List<String> patterns = parsePackageNamePatterns( content );

            //Convert to Paths as we're delegating to an Ant-style pattern matcher.
            //Convert once outside of the nested loops for performance reasons.
            for ( int i = 0; i < patterns.size(); i++ ) {
                patterns.set( i,
                              patterns.get( i ).replaceAll( "\\.",
                                                            AntPathMatcher.DEFAULT_PATH_SEPARATOR ) );
            }
            final HashMap<String, String> packageNamePaths = new HashMap<String, String>();
            for ( String packageName : packageNames ) {
                packageNamePaths.put( packageName,
                                      packageName.replaceAll( "\\.",
                                                              AntPathMatcher.DEFAULT_PATH_SEPARATOR ) );
            }

            //Add Package Names matching the White List to the available packages
            for ( String pattern : patterns ) {
                for ( Map.Entry<String, String> pnp : packageNamePaths.entrySet() ) {
                    if ( ANT_PATH_MATCHER.match( pattern,
                                                 pnp.getValue() ) ) {
                        packageNamesWhiteList.add( pnp.getKey() );
                    }
                }
            }
        }

        return packageNamesWhiteList;
    }

    protected String readPackageNameWhiteList( final KieProject project ) {
        final org.uberfire.java.nio.file.Path packageNamesWhiteListPath = Paths.convert( project.getPackageNamesWhiteList() );
        if ( Files.exists( packageNamesWhiteListPath ) ) {
            final String content = ioService.readAllString( packageNamesWhiteListPath );
            return content;
        }
        return "";
    }

    //See https://bugzilla.redhat.com/show_bug.cgi?id=1205180. Use OS-independent line splitting.
    private List<String> parsePackageNamePatterns( final String content ) {
        try {
            return IOUtils.readLines( new StringReader( content ) );

        } catch ( IOException ioe ) {
            logger.warn( "Unable to parse package names from '" + content + "'. Falling back to empty list." );
            return Collections.emptyList();
        }
    }

}
