/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssetListPagingTest {

    @Mock
    private AssetList.View view;

    @Mock
    private Command updateCommand;

    private AssetList assetList;

    @Before
    public void setup() {
        assetList = new AssetList(view);
        assetList.addChangeHandler(updateCommand);
    }

    @Test
    public void addElementToView() throws Exception {
        final HTMLElement element = mock(HTMLElement.class);

        assetList.add(element);

        verify(view).add(element);
    }

    @Test
    public void disableGoingBackWhenOnFirstPage() throws Exception {

        addMockElements(14);

        reset(view);

        assetList.add(mock(HTMLElement.class));

        verify(view).setForwardDisabled(eq(false));
        verify(view).setBackwardDisabled(eq(true));
    }

    @Test
    public void canNotGoBackFromFirstPage() throws Exception {

        reset(view);

        addMockElements(14);

        assetList.onToPrevious();

        verify(view,
               never()).setPageNumber(anyInt());
        verify(updateCommand,
               never()).execute();
    }

    @Test
    public void canNotGoForwardIfPageIsNotFull() throws Exception {

        reset(view);

        addMockElements(5);

        assetList.onToNextPage();

        verify(view,
               never()).setPageNumber(anyInt());
        verify(updateCommand,
               never()).execute();
    }

    @Test
    public void goForward() throws Exception {

        reset(view);

        addMockElements(15);

        assetList.onToNextPage();

        verify(view).setPageNumber(2);
        verify(updateCommand).execute();
    }

    @Test
    public void goBack() throws Exception {

        assetList.onPageNumberChange(3);

        reset(view,
              updateCommand);

        addMockElements(15);

        assetList.onToPrevious();

        verify(view).setPageNumber(2);
        verify(updateCommand).execute();
    }

    @Test
    public void enableEveryThingWhenOnMiddle() throws Exception {

        addMockElements(14);

        reset(view);

        assetList.onPageNumberChange(2);

        assetList.add(mock(HTMLElement.class));

        verify(view).setForwardDisabled(eq(false));
        verify(view).setBackwardDisabled(eq(false));
    }

    @Test
    public void disableForwardIfPageNotFull() throws Exception {

        assetList.onPageNumberChange(2);

        assetList.add(mock(HTMLElement.class));

        verify(view).setForwardDisabled(eq(true));
        verify(view).setBackwardDisabled(eq(false));
    }

    @Test
    public void blockEveryThingOnFirstPageThatIsNotFull() throws Exception {

        assetList.add(mock(HTMLElement.class));

        verify(view).setForwardDisabled(eq(true));
        verify(view).setBackwardDisabled(eq(true));
    }

    private void addMockElements(final int amount) {
        for (int i = 0; i < amount; i++) {
            assetList.add(mock(HTMLElement.class));
        }
    }
}
