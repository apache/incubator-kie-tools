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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class AssetListFirstIndexTest {

    private final int givenStep;
    private final int givenPageNumber;
    private final int expectedStartIndex;

    @Mock
    private AssetList.View view;

    private AssetList assetList;

    public AssetListFirstIndexTest(final int givenStep,
                                   final int givenPageNumber,
                                   final int expectedStartIndex) {

        this.givenStep = givenStep;
        this.givenPageNumber = givenPageNumber;
        this.expectedStartIndex = expectedStartIndex;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> caseSensitivity() {
        return Arrays.asList(
                new Object[][]{
                        // Step, Page Number, Start Index
                        {15, 1, 0},
                        {15, 2, 15},
                        {45, 1, 0},
                        {15, 0, 0},
                        {5, 10, 45}
                }
        );
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        assetList = new AssetList(view);
        assetList.addChangeHandler(mock(Command.class));
    }

    @Test
    public void startIndexTest() throws Exception {

        assetList.onChangeAmountOfItemsShown(givenStep);
        assetList.onPageNumberChange(givenPageNumber);

        assertEquals(expectedStartIndex,
                     assetList.getFirstIndex());
    }
}
