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

package org.drools.workbench.screens.guided.dtable.client.widget.table.themes;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRenderer;

public class GuidedDecisionTableRenderer extends BaseGridRenderer {

    public GuidedDecisionTableRenderer( final GuidedDecisionTableUiModel uiModel,
                                        final GuidedDecisionTable52 model ) {
        super( new GuidedDecisionTableTheme( uiModel,
                                             model ) );
    }

    @Override
    public Group renderHeaderBodyDivider( final double width ) {
        final Group g = new Group();
        final Line dividerLine1 = theme.getGridHeaderBodyDivider();
        final Line dividerLine2 = theme.getGridHeaderBodyDivider();
        dividerLine1.setPoints( new Point2DArray( new Point2D( 0,
                                                               getHeaderHeight() - 1.5 ),
                                                  new Point2D( width,
                                                               getHeaderHeight() - 1.5 ) ) );
        dividerLine2.setPoints( new Point2DArray( new Point2D( 0,
                                                               getHeaderHeight() + 0.5 ),
                                                  new Point2D( width,
                                                               getHeaderHeight() + 0.5 ) ) );
        g.add( dividerLine1 );
        g.add( dividerLine2 );
        return g;
    }

}
