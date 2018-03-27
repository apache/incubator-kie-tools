/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridDataCache;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(LienzoMockitoTestRunner.class)
public class BaseEditorDefinitionTest {

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private TranslationService translationService;

    @Mock
    private LiteralExpressionGrid editor;

    private BaseEditorDefinition<LiteralExpression, DMNGridData> definition;

    @Before
    public void setup() {
        this.definition = new BaseEditorDefinition<LiteralExpression, DMNGridData>(gridPanel,
                                                                                   gridLayer,
                                                                                   definitionUtils,
                                                                                   sessionManager,
                                                                                   sessionCommandManager,
                                                                                   canvasCommandFactory,
                                                                                   cellEditorControls,
                                                                                   listSelector,
                                                                                   translationService) {
            @Override
            protected DMNGridData makeGridData(final Optional<LiteralExpression> expression) {
                return new DMNGridData();
            }

            @Override
            public ExpressionType getType() {
                return ExpressionType.LITERAL_EXPRESSION;
            }

            @Override
            public String getName() {
                return LiteralExpression.class.getName();
            }

            @Override
            public Optional<LiteralExpression> getModelClass() {
                return Optional.of(new LiteralExpression());
            }

            @Override
            public Optional<BaseExpressionGrid> getEditor(final GridCellTuple parent,
                                                          final Optional nodeUUID,
                                                          final HasExpression hasExpression,
                                                          final Optional expression,
                                                          final Optional optional,
                                                          final int nesting) {
                return Optional.of(editor);
            }
        };
    }

    @Test
    public void testCacheWhenNested() {
        final Optional<String> UUID = Optional.empty();
        final GridDataCache.CacheResult<DMNGridData> result1 = definition.getData(UUID, definition.getModelClass());
        assertCacheResult(result1, false);

        final GridDataCache.CacheResult<DMNGridData> result2 = definition.getData(UUID, definition.getModelClass());
        assertCacheResult(result2, false);

        final DMNGridData uiModel1 = result1.getGridData();
        final DMNGridData uiModel2 = result2.getGridData();
        assertThat(uiModel1).isNotSameAs(uiModel2);
    }

    @Test
    public void testCacheWhenNotNested() {
        final Optional<String> UUID = Optional.of("uuid");
        final GridDataCache.CacheResult<DMNGridData> result1 = definition.getData(UUID, definition.getModelClass());
        assertCacheResult(result1, false);

        final GridDataCache.CacheResult<DMNGridData> result2 = definition.getData(UUID, definition.getModelClass());
        assertCacheResult(result2, true);

        final DMNGridData uiModel1 = result1.getGridData();
        final DMNGridData uiModel2 = result2.getGridData();

        assertThat(uiModel1).isSameAs(uiModel2);
    }

    private void assertCacheResult(final GridDataCache.CacheResult<DMNGridData> result, final boolean hit) {
        assertThat(result).isNotNull();
        assertThat(result.isCacheHit()).isEqualTo(hit);
        assertThat(result.getGridData()).isInstanceOf(DMNGridData.class);
    }
}
