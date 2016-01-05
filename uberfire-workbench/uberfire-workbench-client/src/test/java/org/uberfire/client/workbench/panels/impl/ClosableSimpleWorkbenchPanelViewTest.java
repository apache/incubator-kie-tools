/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench.panels.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ClosableSimpleWorkbenchPanelViewTest extends AbstractSimpleWorkbenchPanelViewTest {

    @InjectMocks
    private ClosableSimpleWorkbenchPanelView view;

    // Not a @Mock or @GwtMock because we want to test the view.init() method
    private ClosableSimpleWorkbenchPanelPresenter presenter;

    @Before
    public void setup() {
        super.setup();

        presenter = mock( ClosableSimpleWorkbenchPanelPresenter.class );

        view.setup(); // PostConstruct
        view.init( presenter );
    }

    @Override
    protected AbstractDockingWorkbenchPanelView<?> getViewToTest() {
        return view;
    }

    @Test
    public void shouldAddPresenterOnInit() {
        assertEquals( presenter, view.getPresenter() );
    }

    @Test
    public void shouldEnableCloseParts() {
        verify( listBar, never() ).disableClosePart();
        verify( listBar ).enableClosePart();
    }

}