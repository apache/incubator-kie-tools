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

package org.kie.workbench.common.widgets.client.widget;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class KSessionSelectorViewImplTest {

    private KSessionSelectorViewImpl kSessionSelectorView;

    @Before
    public void setUp() throws Exception {
        kSessionSelectorView = spy( new KSessionSelectorViewImpl() );

    }

    @Test
    public void testSetSelected() throws Exception {
        kSessionSelectorView.setSelected( "kbaseName",
                                          "ksessionName" );

        verify( kSessionSelectorView ).fireValueChanged();
    }

    @Test
    public void testName() throws Exception {
        kSessionSelectorView.onKSessionSelected( null );

        verify( kSessionSelectorView ).fireValueChanged();
    }
}