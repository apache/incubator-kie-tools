/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.appformer.maven.integration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.Artifact;
import org.appformer.maven.integration.embedder.EmbeddedPomParser;
import org.appformer.maven.support.DependencyFilter;
import org.appformer.maven.support.MinimalPomParser;
import org.appformer.maven.support.PomModel;
import org.appformer.maven.support.AFReleaseId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.appformer.maven.integration.embedder.MavenProjectLoader.parseMavenPom;

public class ArtifactResolver {

    private static final Logger log = LoggerFactory.getLogger(ArtifactResolver.class);

    private final PomParser pomParser;

    private final MavenRepository mavenRepository;

    public ArtifactResolver() {
        mavenRepository = MavenRepository.getMavenRepository();
        pomParser = new EmbeddedPomParser();
    }

    private ArtifactResolver(MavenProject mavenProject) {
        mavenRepository = MavenRepository.getMavenRepository(mavenProject);
        pomParser = new EmbeddedPomParser(mavenProject);
    }

    private ArtifactResolver(PomParser pomParser) {
        mavenRepository = MavenRepository.getMavenRepository();
        this.pomParser = pomParser;
    }

    public Artifact resolveArtifact(AFReleaseId releaseId ) {
        return mavenRepository.resolveArtifact(releaseId);
    }

    public List<DependencyDescriptor> getArtifactDependecies( String artifactName ) {
        return mavenRepository.getArtifactDependecies(artifactName);
    }

    public List<DependencyDescriptor> getPomDirectDependencies( DependencyFilter dependencyFilter ) {
        return pomParser.getPomDirectDependencies(dependencyFilter);
    }

    public Collection<DependencyDescriptor> getAllDependecies() {
        return getAllDependecies( (releaseId, scope) -> true );
    }

    public Collection<DependencyDescriptor> getAllDependecies( DependencyFilter dependencyFilter ) {
        Set<DependencyDescriptor> dependencies = new HashSet<DependencyDescriptor>();
        for (DependencyDescriptor dep : getPomDirectDependencies(dependencyFilter)) {
            dependencies.add( dep );
            for (DependencyDescriptor transitiveDep : getArtifactDependecies( dep.toString() )) {
                if (dependencyFilter.accept( dep.getReleaseId(), dep.getScope() )) {
                    dependencies.add( transitiveDep );
                }
            }
        }
        return dependencies;
    }

    public static ArtifactResolver getResolverFor(AFReleaseId releaseId, boolean allowDefaultPom) {
        File pomFile = getPomFileForGAV( releaseId, allowDefaultPom );
        if (pomFile != null) {
            ArtifactResolver artifactResolver = getResolverFor(pomFile);
            if (artifactResolver != null) {
                return artifactResolver;
            }
        }
        return allowDefaultPom ? new ArtifactResolver() : null;
    }

    public static ArtifactResolver getResolverFor(URI uri) {
        return getResolverFor(new File(uri));
    }

    public static ArtifactResolver getResolverFor(File pomFile) {
        try {
            return new ArtifactResolver(parseMavenPom(pomFile));
        } catch (RuntimeException e) {
            log.warn("Cannot use native maven pom parser, fall back to the internal one", e);
            PomParser pomParser = createInternalPomParser(pomFile);
            if (pomParser != null) {
                return new ArtifactResolver(pomParser);
            }
        }
        return null;
    }

    public static ArtifactResolver getResolverFor(InputStream pomStream) {
        MavenProject mavenProject = parseMavenPom(pomStream);
        return new ArtifactResolver(mavenProject);
    }

    private static File getPomFileForGAV(AFReleaseId releaseId, boolean allowDefaultPom) {
        String artifactName = releaseId.getGroupId() + ":" + releaseId.getArtifactId() + ":pom:" + releaseId.getVersion();
        Artifact artifact = MavenRepository.getMavenRepository().resolveArtifact(artifactName, !allowDefaultPom);
        return artifact != null ? artifact.getFile() : null;
    }

    public static ArtifactResolver getResolverFor(InputStream pomStream, AFReleaseId releaseId, boolean allowDefaultPom ) {
        if (pomStream != null) {
            ArtifactResolver artifactResolver = getResolverFor(pomStream);
            if (artifactResolver != null) {
                return artifactResolver;
            }
        }
        return getResolverFor(releaseId, allowDefaultPom);
    }

    public static ArtifactResolver getResolverFor(PomModel pomModel ) {
        return pomModel instanceof MavenPomModelGenerator.MavenModel ?
               new ArtifactResolver( ( (MavenPomModelGenerator.MavenModel) pomModel ).getMavenProject() ) :
               new ArtifactResolver();
    }

    private static InternalPomParser createInternalPomParser(File pomFile ) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(pomFile);
            return new InternalPomParser( MinimalPomParser.parse( pomFile.getAbsolutePath(), fis ));
        } catch (FileNotFoundException e) {
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) { }
            }
        }
        return null;
    }

    private static class InternalPomParser implements PomParser {
        private final PomModel pomModel;

        private InternalPomParser(PomModel pomModel) {
            this.pomModel = pomModel;
        }

        @Override
        public List<DependencyDescriptor> getPomDirectDependencies( DependencyFilter filter ) {
            List<DependencyDescriptor> deps = new ArrayList<DependencyDescriptor>();
            for (AFReleaseId rId : pomModel.getDependencies(filter )) {
                deps.add(new DependencyDescriptor(rId));
            }
            return deps;
        }
    }
}
