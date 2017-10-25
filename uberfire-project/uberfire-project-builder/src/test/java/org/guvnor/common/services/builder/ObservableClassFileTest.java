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

package org.guvnor.common.services.builder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ObservableClassFileTest {

    @Mock
    private Path path;

    private ObservableClassFile observer;

    @Before
    public void setup() {
        this.observer = new ObservableClassFile();
    }

    @Test
    public void testAcceptWithoutClassFile() {
        doReturn("Cheese.txt").when(path).getFileName();
        assertFalse(observer.accept(path));
    }

    @Test
    public void testAcceptWithClassFile() {
        doReturn("Cheese." + ObservableClassFile.EXTENSION).when(path).getFileName();
        assertTrue(observer.accept(path));
    }
}
