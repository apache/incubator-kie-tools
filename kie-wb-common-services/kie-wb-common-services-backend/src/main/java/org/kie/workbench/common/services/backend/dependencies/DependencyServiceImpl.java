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
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.enterprise.context.ApplicationScoped;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.scanner.MavenRepository;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;

import static org.kie.workbench.common.services.backend.dependencies.DependencyTestUtils.*;

@Service
@ApplicationScoped
public class DependencyServiceImpl
        implements DependencyService {

    public DependencyServiceImpl() {
    }

    @Override
    public Collection<Dependency> loadDependencies( final GAV gav ) {
        return toDependencies( getMavenRepository().getArtifactDependecies( gav.toString() ) );
    }

    @Override
    public Set<String> loadPackageNamesForDependency( final GAV gav ) {
        return stripPackageNamesFromJar( getMavenRepository().resolveArtifact( gav.toString() ).getFile() );
    }

    private Set<String> stripPackageNamesFromJar( final File file ) {
        final Set<String> packageNames = new HashSet<String>();
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile( file );
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
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
