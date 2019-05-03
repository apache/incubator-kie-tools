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

package org.kie.workbench.common.dmn.client.widgets.grid.columns.factory;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextAreaDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.mockito.Mockito.spy;

@RunWith(LienzoMockitoTestRunner.class)
public class TextAreaSingletonDOMElementFactoryTest extends BaseSingletonDOMElementFactoryTest<TextAreaSingletonDOMElementFactory, TextAreaDOMElement> {

    @Override
    protected TextAreaSingletonDOMElementFactory getFactoryForAttachDomElementTest() {
        return new TextAreaSingletonDOMElementFactory(gridPanel,
                                                      gridLayer,
                                                      gridWidget,
                                                      sessionManager,
                                                      sessionCommandManager,
                                                      (gc) -> new DeleteCellValueCommand(gc,
                                                                                         () -> uiModelMapper,
                                                                                         gridLayer::batch),
                                                      (gcv) -> new SetCellValueCommand(gcv,
                                                                                       () -> uiModelMapper,
                                                                                       gridLayer::batch)) {
            @Override
            public TextAreaDOMElement createDomElement(final GridLayer gridLayer,
                                                       final GridWidget gridWidget) {
                return spy(super.createDomElement(gridLayer,
                                                  gridWidget));
            }
        };
    }

    @Override
    protected TextAreaSingletonDOMElementFactory getFactoryForFlushTest() {
        return new TextAreaSingletonDOMElementFactory(gridPanel,
                                                      gridLayer,
                                                      gridWidget,
                                                      sessionManager,
                                                      sessionCommandManager,
                                                      (gc) -> new DeleteCellValueCommand(gc,
                                                                                         () -> uiModelMapper,
                                                                                         gridLayer::batch),
                                                      (gcv) -> new SetCellValueCommand(gcv,
                                                                                       () -> uiModelMapper,
                                                                                       gridLayer::batch));
    }
}
