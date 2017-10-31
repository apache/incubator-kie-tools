/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.services.verifier.api.client.index;

import org.drools.workbench.services.verifier.api.client.AnalyzerConfigurationMock;
import org.drools.workbench.services.verifier.api.client.index.keys.Values;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ActionTest {

    private Action action;

    @Before
    public void setUp() throws
                        Exception {
        action = new Action( mock( Column.class ),
                             ActionSuperType.FIELD_ACTION,
                             new Values( true ),
                             new AnalyzerConfigurationMock() ) {
        };
    }

    @Test
    public void valueSet() throws
                           Exception {
        assertEquals( 1,
                      action.getValues()
                              .size() );
        assertEquals( true,
                      action.getValues()
                              .iterator()
                              .next() );
    }

    @Test
    public void changeValue() throws
                              Exception {
        action.setValue( new Values( false ) );

        assertEquals( 1,
                      action.getValues()
                              .size() );
        assertEquals( false,
                      action.getValues()
                              .iterator()
                              .next() );
    }
}