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

package org.uberfire.mocks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.uberfire.mocks.ParametrizedCommandMock.executeParametrizedCommandWith;

@RunWith(MockitoJUnitRunner.class)
public class ParametrizedCommandMockTest {
    
    
    private static final String TEST_STR = "TEST";
    private static final String NOT_A_TEST_STR = "NOT A TEST STR";
    
    @Mock
    UsesParametrizedCommand usesParametrizedCommand;
    
    @Before
    public void setup() {
        executeParametrizedCommandWith(0, new Foo(TEST_STR))
            .when(usesParametrizedCommand)
            .theKindOfMethodYouWantToMock(any(ParameterizedCommand.class));
    }
    
    @Test
    public void testParametrizedCommandMockTest() {
        usesParametrizedCommand
                .theKindOfMethodYouWantToMock(foo -> assertEquals(TEST_STR, foo.getStr()));
    }
    
    public static class Foo {
        private String str;

        public Foo(String str) {
            super();
            this.str = str;
        }
        
        public String getStr() {
            return str;
        }
    }
    
    public static class UsesParametrizedCommand {

        public void theKindOfMethodYouWantToMock(ParameterizedCommand<Foo> cmd) {
            cmd.execute(new Foo(NOT_A_TEST_STR));
        }
    }

}
