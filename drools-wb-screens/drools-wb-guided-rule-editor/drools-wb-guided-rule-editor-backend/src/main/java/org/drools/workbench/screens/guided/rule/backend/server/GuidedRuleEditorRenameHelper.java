/*
* Copyright 2014 JBoss Inc
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
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.helper.RenameHelper;
import org.uberfire.io.IOService;
import org.uberfire.workbench.type.FileNameUtil;

/**
 * RenameHelper for Guided Rules
 */
@ApplicationScoped
public class GuidedRuleEditorRenameHelper implements RenameHelper {

    @Inject
    private GuidedRuleDRLResourceTypeDefinition drlResourceType;

    @Inject
    private GuidedRuleDSLRResourceTypeDefinition dslrResourceType;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private GuidedRuleEditorServiceUtilities utilities;

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

        final RuleModel model = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL( drl,
                                                                                             globals,
                                                                                             dataModelService.getDataModel( destination ),
                                                                                             dsls );
        //Update rule name
        String ruleName = model.name;
        if ( drlResourceType.accept( destination ) ) {
            ruleName = FileNameUtil.removeExtension( destination,
                                                     drlResourceType );
        } else if ( dslrResourceType.accept( destination ) ) {
            ruleName = FileNameUtil.removeExtension( destination,
                                                     dslrResourceType );
        }
        model.name = ruleName;

        //Save file
        ioService.write( _destination,
                         RuleModelDRLPersistenceImpl.getInstance().marshal( model ),
                         utilities.makeCommentedOption( "File [" + source.toURI() + "] renamed to [" + destination.toURI() + "]." ) );
    }

}
