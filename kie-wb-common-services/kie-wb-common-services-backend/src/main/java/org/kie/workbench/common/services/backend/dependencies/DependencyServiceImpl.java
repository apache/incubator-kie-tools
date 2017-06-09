/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.kie.workbench.common.services.backend.dependencies;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.enterprise.context.ApplicationScoped;

import org.appformer.maven.integration.MavenRepository;
import org.eclipse.aether.artifact.Artifact;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Repository;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependencies;
import org.kie.workbench.common.services.shared.dependencies.NormalEnhancedDependency;
import org.kie.workbench.common.services.shared.dependencies.TransitiveEnhancedDependency;

import static org.kie.workbench.common.services.backend.dependencies.DependencyTestUtils.*;

@Service
@ApplicationScoped
public class DependencyServiceImpl
        implements DependencyService {

    public DependencyServiceImpl() {
    }

    @Override
    public Collection<Dependency> loadDependencies( final Collection<GAV> gavs ) {
        final ArrayList<Dependency> dependencies = new ArrayList<Dependency>();

        for ( final GAV gav : gavs ) {
            dependencies.addAll( loadDependencies( gav ) );
        }

        return dependencies;
    }

    @Override
    public Collection<Dependency> loadDependencies( final GAV gav ) {
        return toDependencies( getMavenRepository().getArtifactDependecies( gav.toString() ) );
    }

    @Override
    public Set<String> loadPackageNames( final GAV gav ) {
        final Artifact artifact = getMavenRepository().resolveArtifact( gav.toString() );

        if ( artifact != null ) {
            return stripPackageNamesFromJar( artifact.getFile() );
        } else {
            return new HashSet<>();
        }
    }

    @Override
    public EnhancedDependencies loadEnhancedDependencies( final Collection<Dependency> dependencies) {
        EnhancedDependencies result = new EnhancedDependencies();

        for ( final Dependency dependency : dependencies ) {
            result.add( getEnhancedDependency( dependency ) );
        }

        return result;
    }

    private NormalEnhancedDependency getEnhancedDependency( final Dependency dependency ) {
        final NormalEnhancedDependency enhancedDependency = new NormalEnhancedDependency( dependency,
                                                                                          loadPackageNames( dependency ) );

        for ( Dependency transitiveDependency : loadDependencies( dependency ) ) {
            enhancedDependency.addTransitiveDependency( new TransitiveEnhancedDependency( transitiveDependency,
                                                                                          loadPackageNames( transitiveDependency ) ) );
        }

        return enhancedDependency;
    }

    private Set<String> stripPackageNamesFromJar( final File file ) {
        final Set<String> packageNames = new HashSet<String>();
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile( file );
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                String pathName = entries.nextElement().getName();

                if ( pathName.endsWith( ".class" ) ) {
                    String fqcn = pathName.replace( '/', '.' ).substring( 0, pathName.lastIndexOf( '.' ) );
                    packageNames.add( fqcn.substring( 0, fqcn.lastIndexOf( '.' ) ) );
                }
            }
        } catch (IOException e) {
            throw ExceptionUtilities.handleException( e );
        } finally {
            if ( zipFile != null ) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    throw ExceptionUtilities.handleException( e );
                }
            }
        }
        return packageNames;
    }

    protected MavenRepository getMavenRepository() {
        return MavenRepository.getMavenRepository();
    }

}
