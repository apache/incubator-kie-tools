/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import java.util.List;
import java.util.logging.Logger;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.plugin.client.Coordinate;
import org.drools.workbench.services.verifier.plugin.client.DTableUpdateManager;
import org.drools.workbench.services.verifier.plugin.client.api.DeleteColumns;
import org.drools.workbench.services.verifier.plugin.client.api.MakeRule;
import org.drools.workbench.services.verifier.plugin.client.api.NewColumn;
import org.drools.workbench.services.verifier.plugin.client.api.RemoveRule;
import org.drools.workbench.services.verifier.plugin.client.api.Update;
import org.drools.workbench.services.verifier.plugin.client.builders.ModelMetaDataEnhancer;


public class DTableUpdateManagerImpl
        implements DTableUpdateManager {

    private static final Logger LOGGER = Logger.getLogger( "DTable Analyzer" );

    private VerifierWebWorkerConnectionImpl webWorker;
    private FieldTypeProducer fieldTypeProducer;

    public DTableUpdateManagerImpl( final VerifierWebWorkerConnectionImpl webWorker,
                                    final FieldTypeProducer fieldTypeProducer ) {
        this.webWorker = webWorker;
        this.fieldTypeProducer = fieldTypeProducer;
    }

    @Override
    public void update( final GuidedDecisionTable52 model,
                        final List<Coordinate> coordinates ) {
        webWorker.send( new Update( model,
                                    coordinates ) );
    }

    @Override
    public void newColumn( final GuidedDecisionTable52 model,
                           final int columnIndex ) {
        webWorker.send( new NewColumn( model,
                                       new ModelMetaDataEnhancer( model ).getHeaderMetaData(),
                                       fieldTypeProducer.getFactTypes(),
                                       columnIndex ) );
    }

    @Override
    public void deleteColumns( final int firstColumnIndex,
                               final int numberOfColumns ) {
        webWorker.send( new DeleteColumns( firstColumnIndex,
                                           numberOfColumns ) );
    }

    @Override
    public void removeRule( final Integer rowDeleted ) {
        webWorker.send( new RemoveRule( rowDeleted ) );
    }

    @Override
    public void makeRule( final GuidedDecisionTable52 model,
                          int index ) {
        webWorker.send( new MakeRule( model,
                                      new ModelMetaDataEnhancer( model ).getHeaderMetaData(),
                                      fieldTypeProducer.getFactTypes(),
                                      index ) );
    }

}
