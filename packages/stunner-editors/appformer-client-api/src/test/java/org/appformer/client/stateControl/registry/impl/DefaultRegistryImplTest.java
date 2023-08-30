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


package org.appformer.client.stateControl.registry.impl;

import org.appformer.client.stateControl.registry.RegistryChangeListener;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultRegistryImplTest {

    private static final Command COMMAND1 = new Command(1);
    private static final Command COMMAND2 = new Command(2);
    private static final Command COMMAND3 = new Command(3);
    private static final Command COMMAND4 = new Command(4);

    @Mock
    private RegistryChangeListener changeListener;

    private DefaultRegistryImpl<Command> registry;

    @Before
    public void init() {
        registry = new DefaultRegistryImpl<>();

        registry.setRegistryChangeListener(changeListener);
    }

    @Test
    public void basicTest() {
        registry.register(COMMAND1);
        registry.register(COMMAND2);
        registry.register(COMMAND3);
        registry.register(COMMAND4);

        verify(changeListener, times(4)).notifyRegistryChange();

        assertFalse(registry.isEmpty());

        Assertions.assertThat(registry.getHistory())
                .hasSize(4)
                .containsExactly(COMMAND4, COMMAND3, COMMAND2, COMMAND1);

        Assertions.assertThat(registry.peek())
                .isSameAs(COMMAND4);

        Assertions.assertThat(registry.getHistory())
                .hasSize(4)
                .containsExactly(COMMAND4, COMMAND3, COMMAND2, COMMAND1);

        Assertions.assertThat(registry.pop())
                .isSameAs(COMMAND4);

        verify(changeListener, times(5)).notifyRegistryChange();

        Assertions.assertThat(registry.getHistory())
                .hasSize(3)
                .containsExactly(COMMAND3, COMMAND2, COMMAND1);

        assertFalse(registry.isEmpty());

        Assertions.assertThat(registry.pop())
                .isSameAs(COMMAND3);

        verify(changeListener, times(6)).notifyRegistryChange();

        assertFalse(registry.isEmpty());

        Assertions.assertThat(registry.getHistory())
                .hasSize(2)
                .containsExactly(COMMAND2, COMMAND1);

        registry.clear();
        verify(changeListener, times(7)).notifyRegistryChange();

        assertTrue(registry.isEmpty());
        Assertions.assertThat(registry.getHistory())
                .isEmpty();
    }

    @Test
    public void testAddReachingMax() {
        registry.setMaxSize(2);

        registry.register(COMMAND1);
        registry.register(COMMAND2);

        verify(changeListener, times(2)).notifyRegistryChange();
        assertFalse(registry.isEmpty());

        Assertions.assertThat(registry.getHistory())
                .hasSize(2)
                .containsExactly(COMMAND2, COMMAND1);

        registry.register(COMMAND3);

        verify(changeListener, times(3)).notifyRegistryChange();
        assertFalse(registry.isEmpty());

        Assertions.assertThat(registry.getHistory())
                .hasSize(2)
                .containsExactly(COMMAND3, COMMAND2);

        registry.register(COMMAND4);

        verify(changeListener, times(4)).notifyRegistryChange();
        assertFalse(registry.isEmpty());

        Assertions.assertThat(registry.getHistory())
                .hasSize(2)
                .containsExactly(COMMAND4, COMMAND3);
    }

    @Test
    public void testSettingWrongMax() {
        Assertions.assertThatThrownBy(() -> registry.setMaxSize(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The registry size should be a positive number");
    }

    public static class Command {

        private Integer id;

        public Command(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }
    }
}
