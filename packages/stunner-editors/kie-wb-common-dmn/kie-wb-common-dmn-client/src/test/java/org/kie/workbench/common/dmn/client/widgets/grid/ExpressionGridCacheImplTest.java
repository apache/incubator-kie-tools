/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.widgets.grid;

import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExpressionGridCacheImplTest {

    private static final String UUID = "uuid";

    @Mock
    private BaseExpressionGrid editor;

    private ExpressionGridCacheImpl cache;

    @Before
    public void setup() {
        this.cache = new ExpressionGridCacheImpl();
    }

    @Test
    public void testPutExpressionGridWhenNoEditor() {
        cache.putExpressionGrid(UUID,
                                Optional.empty());

        assertThat(cache.getContent()).isEmpty();
    }

    @Test
    public void testPutExpressionGridWhenEditorIsCacheable() {
        final Map<String, Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> content = cache.getContent();
        when(editor.isCacheable()).thenReturn(true);

        cache.putExpressionGrid(UUID,
                                Optional.of(editor));

        assertThat(content).isNotEmpty();
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.containsKey(UUID)).isTrue();
        assertThat(content.get(UUID)).isEqualTo(Optional.of(editor));
    }

    @Test
    public void testPutExpressionGridWhenEditorIsNotCacheable() {
        when(editor.isCacheable()).thenReturn(false);

        cache.putExpressionGrid(UUID,
                                Optional.of(editor));

        assertThat(cache.getContent()).isEmpty();
    }

    @Test
    public void testGetExpressionWhenIsPresent() {
        when(editor.isCacheable()).thenReturn(true);

        cache.putExpressionGrid(UUID, Optional.of(editor));

        final Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> content = cache.getExpressionGrid(UUID);
        assertThat(content).isPresent();
        assertThat(content.get()).isEqualTo(editor);
    }

    @Test
    public void testGetExpressionWhenNotPresent() {
        assertThat(cache.getExpressionGrid("")).isNotPresent();
    }

    @Test
    public void testRemoveExpressionGrid() {
        final Map<String, Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> content = cache.getContent();
        when(editor.isCacheable()).thenReturn(true);
        cache.putExpressionGrid(UUID, Optional.of(editor));

        cache.removeExpressionGrid("");

        assertThat(content).isNotEmpty();
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.containsKey(UUID)).isTrue();
        assertThat(content.get(UUID)).isEqualTo(Optional.of(editor));

        cache.removeExpressionGrid(UUID);

        assertThat(content).isEmpty();
    }

    @Test
    public void testDoInit() {
        when(editor.isCacheable()).thenReturn(true);

        cache.putExpressionGrid(UUID, Optional.of(editor));

        assertThat(cache.getContent()).isNotEmpty();

        cache.doInit();

        assertThat(cache.getContent()).isEmpty();
    }

    @Test
    public void testDoDestroy() {
        when(editor.isCacheable()).thenReturn(true);

        cache.putExpressionGrid(UUID, Optional.of(editor));

        assertThat(cache.getContent()).isNotEmpty();

        cache.doDestroy();

        assertThat(cache.getContent()).isEmpty();
    }
}
