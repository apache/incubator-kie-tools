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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy;

import java.util.List;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;

@RunWith(MockitoJUnitRunner.class)
public class BuiltinAggregatorUtilsTest {

    @Mock
    private TranslationService translationService;

    private BuiltinAggregatorUtils builtinAggregatorUtils;

    @Before
    public void setup() {
        this.builtinAggregatorUtils = new BuiltinAggregatorUtils(translationService);

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).getTranslation(Mockito.<String>any());
    }

    @Test
    public void testGetAllValues() {
        final List<BuiltinAggregator> builtinAggregators = builtinAggregatorUtils.getAllValues();

        assertThat(builtinAggregators.size()).isEqualTo(BuiltinAggregator.values().length + 1);
        assertThat(builtinAggregators.get(0)).isNull();
        assertThat(builtinAggregators.subList(1, builtinAggregators.size())).containsOnly(BuiltinAggregator.values());
    }

    @Test
    public void testToString() {
        assertThat(builtinAggregatorUtils.toString(null)).isEqualTo(DMNEditorConstants.DecisionTableEditor_NullBuiltinAggregator);

        assertThat(builtinAggregatorUtils.toString(BuiltinAggregator.COUNT)).isEqualTo(BuiltinAggregator.COUNT.value());
        assertThat(builtinAggregatorUtils.toString(BuiltinAggregator.MAX)).isEqualTo(BuiltinAggregator.MAX.value());
        assertThat(builtinAggregatorUtils.toString(BuiltinAggregator.MIN)).isEqualTo(BuiltinAggregator.MIN.value());
        assertThat(builtinAggregatorUtils.toString(BuiltinAggregator.SUM)).isEqualTo(BuiltinAggregator.SUM.value());
    }

    @Test
    public void testToEnum() {
        assertThat(builtinAggregatorUtils.toEnum(DMNEditorConstants.DecisionTableEditor_NullBuiltinAggregator)).isNull();

        assertThat(builtinAggregatorUtils.toEnum(BuiltinAggregator.COUNT.value())).isEqualTo(BuiltinAggregator.COUNT);
        assertThat(builtinAggregatorUtils.toEnum(BuiltinAggregator.MAX.value())).isEqualTo(BuiltinAggregator.MAX);
        assertThat(builtinAggregatorUtils.toEnum(BuiltinAggregator.MIN.value())).isEqualTo(BuiltinAggregator.MIN);
        assertThat(builtinAggregatorUtils.toEnum(BuiltinAggregator.SUM.value())).isEqualTo(BuiltinAggregator.SUM);
    }
}
