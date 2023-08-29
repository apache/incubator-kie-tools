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


package org.uberfire.client.views.pfly.multipage;

import com.google.gwt.user.client.Random;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class MultiPageEditorImplTest {

    @Spy @InjectMocks
    private MultiPageEditorImpl multiPageEditorImpl;

    @Mock
    private MultiPageEditorViewImpl multiPageEditorViewImplMock;

    @Test
    public void testSetSearchButtonVisibility() {
        final boolean visible = Random.nextBoolean();

        multiPageEditorImpl.setTabBarVisible(visible);

        verify(multiPageEditorViewImplMock).setTabBarVisible(visible);
    }
}
