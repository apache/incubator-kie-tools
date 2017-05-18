/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
public class AssetListTest {

    @Mock
    private Command updateCommand;

    @Mock
    private AssetList.View view;

    private AssetList assetList;

    @Before
    public void setup() {
        assetList = new AssetList(view);
        assetList.addChangeHandler(updateCommand);
    }

    @Test
    public void viewInit() throws Exception {
        verify(view).init(assetList);
        verify(view).setStep(15);
        verify(view).setPageNumber(1);
    }

    @Test
    public void zeroOrNegativeValuesAreNotAllowedAsPageNumber() throws Exception {

        assetList.onPageNumberChange(-1);

        verify(view).setPageNumber(1);
    }

    @Test
    public void changingHowManyItemsShownOnPageResetsToFirstPage() throws Exception {
        reset(view);
        assetList.onChangeAmountOfItemsShown(25);
        verify(view).setPageNumber(1);
    }

    @Test
    public void clear() throws Exception {

        assetList.onChangeAmountOfItemsShown(25);

        assetList.add(mock(HTMLElement.class));
        assetList.add(mock(HTMLElement.class));

        reset(view);

        assetList.clear();

        verify(view).clearAssets();
        verify(view).hideEmptyState();

        assetList.add(mock(HTMLElement.class));
        verify(view).range(1,
                           1);
        verify(view,
               never()).setStep(anyInt());
    }

    @Test
    public void testResetPageRangeIndicator() throws Exception {

        assetList.onChangeAmountOfItemsShown(25);

        assetList.add(mock(HTMLElement.class));
        assetList.add(mock(HTMLElement.class));

        reset(view);

        assetList.resetPageRangeIndicator();

        verify(view).hideEmptyState();

        assetList.add(mock(HTMLElement.class));
        verify(view).range(1,
                           25);
    }

    @Test
    public void showEmptyState() throws Exception {
        assetList.showEmptyState("hi",
                                 "just saying hi");

        verify(view).showEmptyStateMessage("hi",
                                           "just saying hi");
    }
}