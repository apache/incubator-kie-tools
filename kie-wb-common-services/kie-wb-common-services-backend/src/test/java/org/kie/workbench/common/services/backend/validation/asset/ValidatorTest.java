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

package org.kie.workbench.common.services.backend.validation.asset;

import org.drools.compiler.kie.builder.impl.MessageImpl;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class ValidatorTest {

    @Mock
    ValidatorFileSystemProvider validatorFileSystemProvider;

    @Mock
    KieBuilder kieBuilder;

    private Validator validator;

    @Before
    public void setUp() throws Exception {
        validator = new Validator( validatorFileSystemProvider ) {
            @Override
            protected KieBuilder makeKieBuilder() {
                return kieBuilder;
            }
        };
    }

    @Test
    public void testGetKieBuilder() throws Exception {
        assertEquals( kieBuilder,
                      validator.getKieBuilder() );
    }

    @Test
    public void testMakeMessage() throws Exception {
        final ValidationMessage validationMessage = validator.convertMessage( new MessageImpl( 0,
                                                                                               Message.Level.ERROR,
                                                                                               "/myProject/File.txt",
                                                                                               "Text file not supported" ) );

        assertEquals( 0, validationMessage.getId() );
        assertEquals( Level.ERROR, validationMessage.getLevel() );
        assertEquals( "Text file not supported", validationMessage.getText() );
    }

    @Test
    public void testAddMessage1() throws Exception {
        validator.addMessage( "",
                              new MessageImpl( 0,
                                               Message.Level.ERROR,
                                               "/myProject/File.txt",
                                               "Text file not supported" ) );

        assertTrue( validator.validationMessages.isEmpty() );

    }

    @Test
    public void testAddMessage2() throws Exception {
        validator.addMessage( "",
                              new MessageImpl( 0,
                                               Message.Level.ERROR,
                                               null,
                                               "Text file not supported" ) );

        assertFalse( validator.validationMessages.isEmpty() );

    }
}