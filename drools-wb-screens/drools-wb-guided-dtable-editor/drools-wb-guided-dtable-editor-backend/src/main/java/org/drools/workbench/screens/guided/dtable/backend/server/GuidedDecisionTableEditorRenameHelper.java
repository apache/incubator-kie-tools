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
package org.drools.workbench.screens.guided.dtable.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.type.GuidedDTableResourceTypeDefinition;
import org.drools.workbench.screens.guided.rule.backend.server.GuidedRuleEditorServiceUtilities;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.helper.RenameHelper;
import org.uberfire.io.IOService;
import org.uberfire.workbench.type.FileNameUtil;

/**
 * RenameHelper for Guided Rule Templates
 */
@ApplicationScoped
public class GuidedDecisionTableEditorRenameHelper implements RenameHelper {

    @Inject
    private GuidedDTableResourceTypeDefinition resourceType;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private GuidedRuleEditorServiceUtilities utilities;

    @Override
    public boolean supports( final Path destination ) {
        return ( resourceType.accept( destination ) );
    }

    @Override
    public void postProcess( final Path source,
                             final Path destination ) {
        //Load existing file
        final org.uberfire.java.nio.file.Path _destination = Paths.convert( destination );
        final String content = ioService.readAllString( Paths.convert( destination ) );
        final GuidedDecisionTable52 model = GuidedDTXMLPersistence.getInstance().unmarshal( content );

        //Update table name
        final String tableName = FileNameUtil.removeExtension( destination,
                                                               resourceType );
        model.setTableName( tableName );

        //Save file
        ioService.write( _destination,
                         GuidedDTXMLPersistence.getInstance().marshal( model ),
                         utilities.makeCommentedOption( "File [" + source.toURI() + "] renamed to [" + destination.toURI() + "]." ) );
    }

}
