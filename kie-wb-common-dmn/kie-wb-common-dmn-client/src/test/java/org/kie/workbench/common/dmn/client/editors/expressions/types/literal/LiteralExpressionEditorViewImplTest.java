/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types.literal;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.mediator.IMediator;
import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.client.widgets.grid.BoundaryTransformMediator;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

import static org.junit.Assert.*;

@RunWith(LienzoMockitoTestRunner.class)
public class LiteralExpressionEditorViewImplTest {

    private static final String TEXT = "text";

    @Mock
    private TranslationService ts;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    private LiteralExpressionEditorViewImpl view;

    @Before
    public void setup() {
        this.view = new LiteralExpressionEditorViewImpl(ts,
                                                        sessionManager,
                                                        sessionCommandManager);
    }

    @Test
    public void checkLayerConfiguration() {
        assertEquals(1,
                     view.getGridLayer().getChildNodes().size());
        assertEquals(view.getGridWidget(),
                     view.getGridLayer().getChildNodes().get(0));
        assertTrue(view.getGridWidget().isSelected());
        assertTrue(view.getGridLayer().isGridPinned());

        final Mediators m = view.getGridPanel().getViewport().getMediators();
        final List<IMediator> mediators = new ArrayList<>();
        m.iterator().forEachRemaining(mediators::add);
        assertEquals(2,
                     mediators.size());
        assertTrue(mediators.get(1) instanceof RestrictedMousePanMediator);

        final RestrictedMousePanMediator mediator = (RestrictedMousePanMediator) mediators.get(1);
        assertTrue(mediator.getTransformMediator() instanceof BoundaryTransformMediator);
    }

    @Test
    public void checkGridWidgetDefinition() {
        final GridWidget gridWidget = view.makeGridWidget();
        assertEquals(1,
                     gridWidget.getModel().getRows().size());
        assertEquals(1,
                     gridWidget.getModel().getColumns().size());
    }

    @Test
    public void checkGetUiModelMapper() {
        assertTrue(view.makeUiModelMapper() instanceof LiteralExpressionUIModelMapper);
    }

    @Test
    public void checkSetExpression() {
        final LiteralExpression expression = new LiteralExpression();
        expression.setText(TEXT);
        view.setExpression(expression);

        assertEquals(TEXT,
                     view.getGridWidget().getModel().getCell(0,
                                                             0).getValue().getValue().toString());
    }
}
