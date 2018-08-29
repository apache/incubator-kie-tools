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
 *
 */

package org.kie.workbench.common.screens.library.client.widgets.example.errors;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleProjectError;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImportProjectErrorPresenterTest {

    public static final String VALIDATOR = "Validator";
    public static final String VALIDATOR_ID = "org.id.with.dots." + VALIDATOR;
    public static final String TRANSLATED_MESSAGE = "A nice translated message";
    public static final String DESCRIPTION = "description";
    @Mock
    private TranslationService ts;

    @Mock
    private ExampleProjectErrorPresenter.View view;

    private ExampleProjectErrorPresenter presenter;

    @Before
    public void setUp() {

        when(ts.format(eq(VALIDATOR),
                       eq(DESCRIPTION))).thenReturn(TRANSLATED_MESSAGE);
        when(ts.getTranslation(eq(VALIDATOR))).thenReturn(TRANSLATED_MESSAGE);

        this.presenter = new ExampleProjectErrorPresenter(view,
                                                          ts);
    }

    @Test
    public void testGetId() {
        String id = this.presenter.getId(VALIDATOR_ID);
        assertEquals(VALIDATOR,
                     id);

        id = this.presenter.getId(VALIDATOR_ID);
        assertEquals(VALIDATOR,
                     id);
    }

    @Test
    public void testTranslateError() {
        ExampleProjectError error = new ExampleProjectError(VALIDATOR_ID,
                                                            DESCRIPTION);
        String translatedMessager = this.presenter.translateError(error);
        assertEquals("- " + TRANSLATED_MESSAGE,
                     translatedMessager);
    }
}