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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.java.JavaFunctionEditorDefinition;

@RunWith(LienzoMockitoTestRunner.class)
public class JavaFunctionSupplementaryGridTest extends BaseFunctionSupplementaryGridTest<JavaFunctionEditorDefinition> {

    protected JavaFunctionEditorDefinition getEditorDefinition() {
        return new JavaFunctionEditorDefinition(gridPanel,
                                                gridLayer,
                                                definitionUtils,
                                                sessionManager,
                                                sessionCommandManager,
                                                canvasCommandFactory,
                                                editorSelectedEvent,
                                                cellEditorControls,
                                                listSelector,
                                                translationService,
                                                expressionEditorDefinitionsSupplier);
    }

    protected String[] getExpectedNames() {
        return new String[]{JavaFunctionEditorDefinition.VARIABLE_CLASS, JavaFunctionEditorDefinition.VARIABLE_METHOD_SIGNATURE};
    }
}
