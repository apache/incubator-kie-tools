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

package org.kie.workbench.common.screens.explorer.client.utils;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

@RunWith( Parameterized.class )
public class UtilsHasBranchChangedTest {

    private final String branch1;

    private final String branch2;

    private final boolean hasBranchChanged;


    public UtilsHasBranchChangedTest( final boolean hasBranchChanged,
                                      final String branch1,
                                      final String branch2 ) {

        this.hasBranchChanged = hasBranchChanged;
        this.branch1 = branch1;
        this.branch2 = branch2;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList( new Object[][]{
                {true, null, null},
                {true, null, "master"},
                {true, "master", null},
                {true, "master", "dev"},
                {false, "master", "master"}
        } );
    }

    @Test
    public void testIsInBranch() throws Exception {
        assertEquals( hasBranchChanged, Utils.hasBranchChanged( branch1,
                                                                branch2 ) );
    }

}