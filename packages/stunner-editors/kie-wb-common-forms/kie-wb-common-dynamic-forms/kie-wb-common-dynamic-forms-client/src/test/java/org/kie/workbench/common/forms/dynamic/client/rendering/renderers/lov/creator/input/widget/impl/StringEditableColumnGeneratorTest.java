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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.runner.RunWith;

@RunWith(GwtMockitoTestRunner.class)
public class StringEditableColumnGeneratorTest extends AbstractEditableColumnGeneratorTest<String, StringEditableColumnGenerator> {

    @Override
    protected StringEditableColumnGenerator getGeneratorInstance(TranslationService translationService) {
        return new StringEditableColumnGenerator(translationService);
    }

    @Override
    protected String[] getSupportedTypes() {
        return new String[]{String.class.getName(), Object.class.getName()};
    }
}
