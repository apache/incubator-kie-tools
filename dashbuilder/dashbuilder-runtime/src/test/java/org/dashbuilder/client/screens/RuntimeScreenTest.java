/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dashbuilder.client.screens;

import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class RuntimeScreenTest {

    @Mock
    PlaceManager placeManager;

    @InjectMocks
    RuntimeScreen runtimeScreen;

    @Test
    public void testGoToIndexWithIndexPage() {
        String randomPage = "randomPage";
        List<LayoutTemplate> templates = Arrays.asList(new LayoutTemplate(randomPage),
                                                       new LayoutTemplate(RuntimeScreen.INDEX_PAGE_NAME));
        
        runtimeScreen.goToIndex(templates);

        verify(placeManager).goTo(RuntimeScreen.INDEX_PAGE_NAME);
        verify(placeManager, times(0)).goTo(randomPage);
    }

    @Test
    public void testGoToIndexWithSinglePage() {
        String randomPage = "randomPage";
        List<LayoutTemplate> templates = Arrays.asList(new LayoutTemplate(randomPage));

        runtimeScreen.goToIndex(templates);

        verify(placeManager).goTo(randomPage);
    }

    @Test
    public void testGoToIndexWithoutIndex() {
        List<LayoutTemplate> templates = Arrays.asList(new LayoutTemplate("page1"),
                                                       new LayoutTemplate("page2"));
        runtimeScreen.goToIndex(templates);
        verify(placeManager, times(0)).goTo(anyString());
    }

}