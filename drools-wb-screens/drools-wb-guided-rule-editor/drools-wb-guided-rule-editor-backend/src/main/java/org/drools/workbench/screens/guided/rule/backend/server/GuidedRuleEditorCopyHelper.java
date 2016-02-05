/*
* Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.rule.backend.server;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.commons.backend.rule.RuleModelDRLPersistenceImpl;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.type.GuidedRuleDRLResourceTypeDefinition;
import org.drools.workbench.screens.guided.rule.type.GuidedRuleDSLRResourceTypeDefinition;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.helper.CopyHelper;
import org.uberfire.io.IOService;
import org.uberfire.workbench.type.FileNameUtil;

/**
 * CopyHelper for Guided Rules
 */
@ApplicationScoped
public class GuidedRuleEditorCopyHelper implements CopyHelper {

    private IOService ioService;
    private GuidedRuleDRLResourceTypeDefinition drlResourceType;
    private GuidedRuleDSLRResourceTypeDefinition dslrResourceType;
    private GuidedRuleEditorServiceUtilities utilities;
    private CommentedOptionFactory commentedOptionFactory;
    private DataModelService dataModelService;

    public GuidedRuleEditorCopyHelper() {
        //Zero-parameter constructor for CDI proxies
    }

    @Inject
    public GuidedRuleEditorCopyHelper( final @Named("ioStrategy") IOService ioService,
                                       final GuidedRuleDRLResourceTypeDefinition drlResourceType,
                                       final GuidedRuleDSLRResourceTypeDefinition dslrResourceType,
                                       final GuidedRuleEditorServiceUtilities utilities,
                                       final CommentedOptionFactory commentedOptionFactory,
                                       final DataModelService dataModelService ) {
        this.ioService = ioService;
        this.drlResourceType = drlResourceType;
        this.dslrResourceType = dslrResourceType;
        this.utilities = utilities;
        this.commentedOptionFactory = commentedOptionFactory;
        this.dataModelService = dataModelService;
    }

    @Override
    public boolean supports( final Path destination ) {
        return ( drlResourceType.accept( destination ) || dslrResourceType.accept( destination ) );
    }

    @Override
    public void postProcess( final Path source,
                             final Path destination ) {
        //Load existing file
        final org.uberfire.java.nio.file.Path _destination = Paths.convert( destination );
        final String drl = ioService.readAllString( _destination );
        final String[] dsls = utilities.loadDslsForPackage( destination );
        final List<String> globals = utilities.loadGlobalsForPackage( destination );

        //Update rule name
        RuleModel model = null;
        String ruleName = null;
        if ( drlResourceType.accept( destination ) ) {
            model = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                         globals,
                                                                         dataModelService.getDataModel( destination ) );
            ruleName = FileNameUtil.removeExtension( destination,
                                                     drlResourceType );
        } else if ( dslrResourceType.accept( destination ) ) {
            model = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL( drl,
                                                                                 globals,
                                                                                 dataModelService.getDataModel( destination ),
                                                                                 dsls );
            ruleName = FileNameUtil.removeExtension( destination,
                                                     dslrResourceType );
        }

        if ( model != null ) {
            //Save file
            model.name = ruleName;
            ioService.write( _destination,
                             RuleModelDRLPersistenceImpl.getInstance().marshal( model ),
                             commentedOptionFactory.makeCommentedOption( "File [" + source.toURI() + "] copied to [" + destination.toURI() + "]." ) );
        }
    }

}
