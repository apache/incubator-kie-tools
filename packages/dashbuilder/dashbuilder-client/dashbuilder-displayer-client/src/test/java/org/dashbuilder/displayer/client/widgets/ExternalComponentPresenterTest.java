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


package org.dashbuilder.displayer.client.widgets;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.displayer.client.component.ExternalComponentDispatcher;
import org.dashbuilder.displayer.client.widgets.ExternalComponentPresenter.View;
import org.dashbuilder.displayer.external.ExternalComponentMessage;
import org.dashbuilder.displayer.external.ExternalComponentMessageHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ExternalComponentPresenterTest {

    private static final String TEST_URL = "http://acme.com/";

    @Mock
    View view;

    @Mock
    ExternalComponentDispatcher dispatcher;

    @Mock
    ExternalComponentMessageHelper messageHelper;

    @InjectMocks
    ExternalComponentPresenter externalComponentPresenter;

    @Test
    public void testSendMessage() {
        ExternalComponentMessage message = new ExternalComponentMessage();

        externalComponentPresenter.sendMessage(message);

        verify(messageHelper).withId(eq(message), eq(externalComponentPresenter.getId()));
        verify(view).postMessage(eq(message));
    }

    @Test
    public void testBuildUrlWithoutPartition() {
        var expectedUrl = TEST_URL + ExternalComponentPresenter.COMPONENT_SERVER_PATH + "/mycomp/index.html";
        externalComponentPresenter.hostPageUrl = TEST_URL;
        externalComponentPresenter.withComponentId("myComp");
        verify(view).setComponentURL(eq(expectedUrl));
    }

    @Test
    public void testBuildUrlPartition() {
        String expectedUrl = TEST_URL + ExternalComponentPresenter.COMPONENT_SERVER_PATH +
                "/partition/mycomp/index.html";
        externalComponentPresenter.hostPageUrl = TEST_URL;
        externalComponentPresenter.withComponentIdAndPartition("myComp", "partition");
        verify(view).setComponentURL(eq(expectedUrl));
    }

    @Test
    public void testBuildUrlWithCustomHostTest() {
        String expectedUrl = "http://custom.com/partition/mycomp/index.html";
        externalComponentPresenter.withComponentBaseUrlIdAndPartition("http://custom.com/", "myComp", "partition");
        verify(view).setComponentURL(eq(expectedUrl));

    }

}
