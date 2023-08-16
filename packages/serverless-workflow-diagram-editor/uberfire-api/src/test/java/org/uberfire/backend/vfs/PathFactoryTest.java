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

package org.uberfire.backend.vfs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PathFactoryTest {

    @Test
    public void lockTest() {

        Path path = mock(Path.class);
        when(path.toURI()).thenReturn("default://main@myteam/dora/src/main/resources/com/myteam/dora/sample.drl");


        assertEquals("default://locks@system/system/myteam/main/dora/src/main/resources/com/myteam/dora/sample.drl.ulock",
                     PathFactory.newLock(path).toURI());

        Path lockPath = PathFactory.newLockPath(path);

        Path extractedPath = PathFactory.fromLock(lockPath);

        assertEquals(path.toURI(),
                     extractedPath.toURI());
    }
}