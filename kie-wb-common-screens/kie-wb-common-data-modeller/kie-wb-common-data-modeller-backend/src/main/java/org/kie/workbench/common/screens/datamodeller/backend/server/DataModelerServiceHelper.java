/**
 * Copyright 2012 JBoss Inc
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.forge.roaster.model.SyntaxError;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.screens.datamodeller.model.DataModelerError;
import org.kie.workbench.common.services.backend.builder.LRUBuilderCache;
import org.kie.workbench.common.services.backend.builder.LRUProjectDependenciesClassLoaderCache;
import org.kie.workbench.common.services.datamodeller.driver.model.DriverError;
import org.kie.workbench.common.services.datamodeller.util.MapClassLoader;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.rpc.SessionInfo;

@ApplicationScoped
public class DataModelerServiceHelper {

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private User identity;

    @Inject
    @Named("ioStrategy")
    IOService ioService;

    @Inject
    protected KieProjectService projectService;

    @Inject
    @Named("LRUProjectDependenciesClassLoaderCache")
    private LRUProjectDependenciesClassLoaderCache dependenciesClassLoaderCache;

    @Inject
    private LRUBuilderCache builderCache;

    public List<DataModelerError> toDataModelerError( List<DriverError> errors ) {
        List<DataModelerError> result = new ArrayList<DataModelerError>();
        if ( errors == null ) {
            return result;
        }

        for ( DriverError error : errors ) {
            DataModelerError dataModelerError = new DataModelerError(
                    error.getId(),
                    error.getMessage(),
                    Level.ERROR,
                    error.getFile(),
                    error.getLine(),
                    error.getColumn() );
            result.add( dataModelerError );
        }
        return result;
    }

    public List<DataModelerError> toDataModelerError( List<SyntaxError> syntaxErrors,
                                                      Path file ) {
        List<DataModelerError> errors = new ArrayList<DataModelerError>();
        DataModelerError error;
        for ( SyntaxError syntaxError : syntaxErrors ) {
            error = new DataModelerError( syntaxError.getDescription(),
                                          syntaxError.isError() ? Level.ERROR : Level.WARNING,
                                          Paths.convert( file ) );
            error.setColumn( syntaxError.getColumn() );
            error.setLine( syntaxError.getLine() );
            errors.add( error );
        }
        return errors;
    }

    public Map<String, org.uberfire.backend.vfs.Path> toVFSPaths( Map<String, Path> nioPaths ) {
        Map<String, org.uberfire.backend.vfs.Path> vfsPaths = new HashMap<String, org.uberfire.backend.vfs.Path>();
        if ( nioPaths != null ) {
            for ( String key : nioPaths.keySet() ) {
                vfsPaths.put( key, Paths.convert( nioPaths.get( key ) ) );
            }
        }
        return vfsPaths;
    }

    public List<ValidationMessage> toValidationMessage( List<DataModelerError> errors ) {
        List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        ValidationMessage validationMessage;

        if ( errors == null ) {
            return validationMessages;
        }
        for ( DataModelerError error : errors ) {
            validationMessage = new ValidationMessage();
            validationMessage.setPath( error.getFile() );
            validationMessage.setText( error.getMessage() );
            validationMessage.setColumn( error.getColumn() );
            validationMessage.setLine( error.getLine() );
            validationMessage.setId( error.getId() );
            if ( error.getLevel() != null ) {
                validationMessage.setLevel( error.getLevel() );
            }
            validationMessages.add( validationMessage );
        }
        return validationMessages;
    }

    public CommentedOption makeCommentedOption( String commitMessage ) {
        final String name = identity.getIdentifier();
        final Date when = new Date();

        final CommentedOption option = new CommentedOption( sessionInfo.getId(),
                                                            name,
                                                            null,
                                                            commitMessage,
                                                            when );
        return option;
    }

    public Package ensurePackageStructure( final Project project,
                                           final String packageName ) {

        if ( packageName == null || "".equals( packageName ) || project == null ) {
            return null;
        }

        Package defaultPackage = projectService.resolveDefaultPackage( project );
        Package subPackage = defaultPackage;
        Path subDirPath = Paths.convert( defaultPackage.getPackageMainSrcPath() );
        String subDirName;

        StringTokenizer tokenizer = new StringTokenizer( packageName, "." );
        while ( tokenizer.hasMoreTokens() ) {
            subDirName = tokenizer.nextToken();
            subDirPath = subDirPath.resolve( subDirName );
            if ( !ioService.exists( subDirPath ) ) {
                //create the package using the projectService.
                subPackage = projectService.newPackage( subPackage, subDirName );
            } else {
                subPackage = projectService.resolvePackage( Paths.convert( subDirPath ) );
            }
        }

        return subPackage;
    }

    public String getCanonicalFileName( org.uberfire.backend.vfs.Path path ) {
        if ( path == null ) {
            return null;
        }
        String fileName = path.getFileName();
        return fileName.substring( 0, fileName.indexOf( "." ) );
    }

    public ClassLoader getProjectClassLoader( KieProject project ) {

        final KieModule module = builderCache.assertBuilder( project ).getKieModuleIgnoringErrors();
        ClassLoader dependenciesClassLoader = dependenciesClassLoaderCache.assertDependenciesClassLoader( project );
        ClassLoader projectClassLoader;
        if ( module instanceof InternalKieModule ) {
            //will always be an internal kie module
            InternalKieModule internalModule = (InternalKieModule) module;
            projectClassLoader = new MapClassLoader( internalModule.getClassesMap( true ), dependenciesClassLoader );
        } else {
            projectClassLoader = KieModuleMetaData.Factory.newKieModuleMetaData( module ).getClassLoader();
        }
        return projectClassLoader;
    }

}
