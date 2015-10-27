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
package org.kie.workbench.common.services.backend.whitelist;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.backend.builder.NoBuilderFoundException;
import org.kie.workbench.common.services.backend.file.AntPathMatcher;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Files;

/**
 * Represents a "white list" of permitted package names for use with authoring
 */
@Service
@ApplicationScoped
public class PackageNameWhiteListServiceImpl
        implements PackageNameWhiteListService {

    private static final Logger logger = LoggerFactory.getLogger( PackageNameWhiteListServiceImpl.class );

    private IOService ioService;

    private PackageNameSearchProvider packageNameSearchProvider;

    public PackageNameWhiteListServiceImpl() {
    }

    @Inject
    public PackageNameWhiteListServiceImpl( final @Named("ioStrategy") IOService ioService,
                                            final PackageNameSearchProvider packageNameSearchProvider ) {
        this.ioService = ioService;
        this.packageNameSearchProvider = packageNameSearchProvider;
    }

    /**
     * Filter the provided Package names by the Project's white list
     * @param project Project for which to filter Package names
     * @param packageNames All Package names in the Project
     * @return A filtered collection of Package names
     */
    public WhiteList filterPackageNames( final Project project,
                                         final Collection<String> packageNames ) {
        try {
            if ( packageNames == null ) {
                return new WhiteList();
            } else if ( project instanceof KieProject ) {

                Set<String> packageNamesFromDirectDependencies = null;
                packageNamesFromDirectDependencies = packageNameSearchProvider.newTopLevelPackageNamesSearch( project.getPom() ).search();

                Set<String> patterns = getDeclaredWhiteListPatterns( project );
                patterns.addAll( makePatterns( packageNamesFromDirectDependencies ) );

                return new PackageNameWhiteListProvider( packageNames,
                                                         patterns ).getFilteredPackageNames();
            } else {
                return new WhiteList( packageNames );
            }
        } catch (NoBuilderFoundException e) {
            logger.info( "Could not create white list for project: " + project.getProjectName() );
        }

        return new WhiteList();
    }

    private Set<String> getDeclaredWhiteListPatterns( final Project project ) {

        final String content = readPackageNameWhiteList( (KieProject) project );
        if ( isEmpty( content ) ) {
            return new HashSet<String>();
        } else {
            return makePatterns( parsePackageNamePatterns( content ) );
        }
    }

    private Set<String> makePatterns( final Collection<String> packageNames ) {
        HashSet<String> patterns = new HashSet<String>();

        //Convert to Paths as we're delegating to an Ant-style pattern matcher.
        //Convert once outside of the nested loops for performance reasons.
        for (String packageName : packageNames) {
            patterns.add( packageName.replaceAll( "\\.",
                                                  AntPathMatcher.DEFAULT_PATH_SEPARATOR ) );
        }
        return patterns;
    }

    protected String readPackageNameWhiteList( final KieProject project ) {
        final org.uberfire.java.nio.file.Path packageNamesWhiteListPath = Paths.convert( project.getPackageNamesWhiteList() );
        if ( Files.exists( packageNamesWhiteListPath ) ) {
            return ioService.readAllString( packageNamesWhiteListPath );
        } else {
            return "";
        }
    }

    //See https://bugzilla.redhat.com/show_bug.cgi?id=1205180. Use OS-independent line splitting.
    private Set<String> parsePackageNamePatterns( final String content ) {
        try {
            return new HashSet<String>( IOUtils.readLines( new StringReader( content ) ) );

        } catch (IOException ioe) {
            logger.warn( "Unable to parse package names from '" + content + "'. Falling back to empty list." );
            return new HashSet<String>();
        }
    }

    private boolean isEmpty( final String content ) {
        return (content == null || content.trim().isEmpty());
    }
}

