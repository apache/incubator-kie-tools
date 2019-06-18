/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.included.grid;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.included.PMMLIncludedModelActiveRecord;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class PMMLCardComponentTest extends BaseCardComponentTest<PMMLCardComponent, PMMLCardComponent.ContentView, PMMLIncludedModelActiveRecord> {

    @Override
    protected PMMLCardComponent getCard(final PMMLCardComponent.ContentView cardView) {
        return new PMMLCardComponent(cardView, refreshDecisionComponentsEvent);
    }

    @Override
    protected PMMLCardComponent.ContentView getCardView() {
        return mock(PMMLCardComponent.ContentView.class);
    }

    @Override
    protected Class<PMMLIncludedModelActiveRecord> getActiveRecordClass() {
        return PMMLIncludedModelActiveRecord.class;
    }

    @Test
    public void testRefreshView() {
        final PMMLIncludedModelActiveRecord includedModel = mock(PMMLIncludedModelActiveRecord.class);
        final String path = "/bla/bla/bla/111111111111111222222222222222333333333333333444444444444444/file.dmn";
        final int modelCount = 12;

        when(includedModel.getNamespace()).thenReturn(path);
        when(includedModel.getModelCount()).thenReturn(modelCount);
        doReturn(includedModel).when(card).getIncludedModel();

        card.refreshView();

        verify(cardView).setPath("...111111222222222222222333333333333333444444444444444/file.dmn");
        verify(cardView).setModelCount(modelCount);
    }
}
