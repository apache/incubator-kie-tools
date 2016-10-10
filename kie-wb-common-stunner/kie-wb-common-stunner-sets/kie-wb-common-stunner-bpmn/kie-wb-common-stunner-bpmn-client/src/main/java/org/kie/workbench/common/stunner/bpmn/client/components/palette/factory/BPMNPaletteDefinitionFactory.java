/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.components.palette.factory;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.definition.*;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.BindableDefSetPaletteDefinitionFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPaletteBuilder;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Dependent
public class BPMNPaletteDefinitionFactory extends BindableDefSetPaletteDefinitionFactory {

    private static final Map<String, String> CAT_TITLES = new HashMap<String, String>() {{
        put( Categories.ACTIVITIES, "Activities" );
        put( Categories.SUBPROCESSES, "Subprocesses" );
        put( Categories.CONNECTING_OBJECTS, "Connecting objects" );
        put( Categories.EVENTS, "Events" );
        put( Categories.GATEWAYS, "Gateways" );
        put( Categories.LANES, "Lanes" );
    }};

    private static final Map<String, String> MORPH_GROUP_TITLES = new HashMap<String, String>() {{
        put( BaseTask.class.getName(), "Tasks" );
        put( BaseStartEvent.class.getName(), "Start Events" );
        put( BaseEndEvent.class.getName(), "End Events" );
        put( BaseSubprocess.class.getName(), "Subprocesses" );
        put( BaseGateway.class.getName(), "Gateways" );
    }};

    @Inject
    public BPMNPaletteDefinitionFactory( final ShapeManager shapeManager,
                                         final DefinitionSetPaletteBuilder paletteBuilder ) {
        super( shapeManager, paletteBuilder );
    }

    @Override
    protected void configureBuilder() {
        super.configureBuilder();
        // Exclude BPMN Diagram from palette model.
        exclude( BPMNDiagram.class );

    }

    @Override
    protected String getCategoryTitle( final String id ) {
        return CAT_TITLES.get( id );
    }

    @Override
    protected String getCategoryDescription( final String id ) {
        return CAT_TITLES.get( id );
    }

    @Override
    protected String getMorphGroupTitle( final String morphBaseId,
                                         final Object definition ) {
        return MORPH_GROUP_TITLES.get( morphBaseId );
    }

    @Override
    protected String getMorphGroupDescription( final String morphBaseId,
                                               final Object definition ) {
        return MORPH_GROUP_TITLES.get( morphBaseId );
    }

    @Override
    protected Class<?> getDefinitionSetType() {
        return BPMNDefinitionSet.class;
    }

}
