/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.refactoring.backend.server.indexing;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.kie.workbench.common.services.refactoring.Resource;
import org.kie.workbench.common.services.refactoring.ResourceReference;
import org.kie.workbench.common.services.refactoring.SharedPart;
import org.kie.workbench.common.services.refactoring.backend.server.impact.ResourceReferenceCollector;
import org.kie.workbench.common.services.refactoring.backend.server.util.KObjectUtil;
import org.kie.workbench.common.services.refactoring.model.index.terms.IndexTerm;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.ext.metadata.engine.Indexer;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

/**
 * All (KIE) {@link Indexer} implementations should extend this class.
 */
public abstract class AbstractFileIndexer implements Indexer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractFileIndexer.class);

    @Inject
    @Named("ioStrategy")
    protected IOService ioService;

    @Inject
    protected KieModuleService moduleService;

    /**
     * This method fills a {@link DefaultIndexBuilder} instance with the default information.
     * If the index builder could not be built, it should either throw an exception or return null.
     * @param path The {@link Path} of the resource to be indexed.
     * @return A {@link DefaultIndexBuilder} instance with the information to be indexed
     * @throws Exception if something goes wrong
     */
    protected abstract IndexBuilder fillIndexBuilder(final Path path) throws Exception;

    /**
     * This method should not be overridden by implementation classes!
     * </p>
     * However, we can not make this method final because otherwise Weld/Errai will complain
     * when they try to make proxy beans.
     */
    @Override
    public KObject toKObject(Path path) {
        KObject index = null;

        try {
            // create a builder with the default information
            IndexBuilder builder = fillIndexBuilder(path);

            Set<KProperty<?>> indexElements = null;
            if (builder != null) {
                // build index document
                indexElements = builder.build();
            } else {
                indexElements = Collections.emptySet();
            }

            index = KObjectUtil.toKObject(path,
                                          IndexTerm.REFACTORING_CLASSIFIER,
                                          indexElements);
        } catch (Exception e) {
            // Unexpected parsing or processing error
            logger.error("Unable to index '" + path.toUri().toString() + "'.",
                         e.getMessage(),
                         e);
        }

        return index;
    }

    protected DefaultIndexBuilder getIndexBuilder(Path path) {
        final Module module = moduleService.resolveModule(Paths.convert(path));
        if (module == null) {
            logger.error("Unable to index " + path.toUri().toString() + ": module could not be resolved.");
            return null;
        }

        final Package pkg = moduleService.resolvePackage(Paths.convert(path));
        if (pkg == null) {
            logger.error("Unable to index " + path.toUri().toString() + ": package could not be resolved.");
            return null;
        }

        // responsible for basic index info: module name, branch, etc
        return new DefaultIndexBuilder(Paths.convert(path).getFileName(),
                                       module,
                                       pkg);
    }

    /**
     * This method adds the index terms necessary for impact analysis to the {@link DefaultIndexBuilder}, which is basically
     * the lucene doc for a resource.
     * @param builder The {@link DefaultIndexBuilder} instance for a resource
     * @param resRefCollector A collector containing info (resources, references) on the asset indexed
     * to other resources that an indexed resource has.
     */
    protected void addReferencedResourcesToIndexBuilder(DefaultIndexBuilder builder,
                                                        ResourceReferenceCollector resRefCollector) {
        Collection<ResourceReference> referencedResources = resRefCollector.getResourceReferences();
        if (!referencedResources.isEmpty()) {
            for (ResourceReference resourceRef : referencedResources) {
                builder.addGenerator(resourceRef);
            }
        }
        Collection<SharedPart> sharedReferences = resRefCollector.getSharedReferences();
        if (!sharedReferences.isEmpty()) {
            for (SharedPart sharedRef : sharedReferences) {
                builder.addGenerator(sharedRef);
            }
        }

        Collection<Resource> resources = resRefCollector.getResources();
        if (!resources.isEmpty()) {
            for (Resource res : resources) {
                builder.addGenerator(res);
            }
        }
    }

    /**
     * This method should not be overridden by implementing classes!
     * </p>
     * However, we can not make this method final because otherwise Weld/Errai will complain
     * when they try to make proxy beans.
     */
    @Override
    public KObjectKey toKObjectKey(final Path path) {
        return KObjectUtil.toKObjectKey(path,
                                        IndexTerm.REFACTORING_CLASSIFIER);
    }
}
