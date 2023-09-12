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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.numeric.integers;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.numeric.AbstractNumericEditableColumnGeneratorTest;

@RunWith(GwtMockitoTestRunner.class)
public class ShortEditableColumnGeneratorTest extends AbstractNumericEditableColumnGeneratorTest<Short, ShortEditableColumnGenerator> {

    private String correctFlatValue = "-32767";

    private Short correctValue = (short)-32767;

    private String wrongFlatValue = "0.12";

    @Override
    protected ShortEditableColumnGenerator getGeneratorInstance(TranslationService translationService) {
        return new ShortEditableColumnGenerator(translationService);
    }

    @Override
    protected String[] getSupportedTypes() {
        return new String[]{Short.class.getName()};
    }

    @Override
    protected String getCorrectFlatValue() {
        return correctFlatValue;
    }

    @Override
    protected String getWrongFlatValue() {
        return wrongFlatValue;
    }

    @Override
    protected Short getCorrectValue() {
        return correctValue;
    }
}
