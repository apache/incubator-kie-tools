/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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
package org.drools.workbench.screens.guided.template.server.indexing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.models.guided.template.backend.RuleTemplateModelXMLPersistenceImpl;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.drools.workbench.screens.guided.template.type.GuidedRuleTemplateResourceTypeDefinition;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.AbstractFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class GuidedRuleTemplateFileIndexer extends AbstractFileIndexer {

    private static final Logger logger = LoggerFactory.getLogger( GuidedRuleTemplateFileIndexer.class );

    @Inject
    protected GuidedRuleTemplateResourceTypeDefinition type;

    @Override
    public boolean supportsPath( final Path path ) {
        return type.accept( Paths.convert( path ) );
    }

    @Override
    public DefaultIndexBuilder fillIndexBuilder( final Path path ) throws Exception {
        final String content = ioService.readAllString( path );
        final TemplateModel model = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal( content );

        final DefaultIndexBuilder builder = getIndexBuilder(path);
        if( builder == null ) {
            return null;
        }

        final GuidedRuleTemplateIndexVisitor visitor = new GuidedRuleTemplateIndexVisitor( builder, model );
        visitor.visit();
        addReferencedResourcesToIndexBuilder(builder, visitor);

        return builder;
    }

}
