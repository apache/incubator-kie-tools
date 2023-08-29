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


package org.kie.workbench.common.widgets.client.popups.about;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AboutPopupTest {

    @Mock
    private AboutPopup.View view;

    @Mock
    private ManagedInstance<AboutPopupConfig> aboutPopupConfigs;

    private AboutPopupConfigMock config = new AboutPopupConfigMock();

    @InjectMocks
    private AboutPopup aboutPopup;

    @Test
    public void setupWithOneCompleteConfigTest() {
        doReturn(false).when(aboutPopupConfigs).isUnsatisfied();
        doReturn(false).when(aboutPopupConfigs).isAmbiguous();
        doReturn(config).when(aboutPopupConfigs).get();

        aboutPopup.setup();

        verify(view).init(aboutPopup);
        verify(view).setProductName(config.productName());
        verify(view).setProductVersion(config.productVersion());
        verify(view).setProductLicense(config.productLicense());
        verify(view).setProductImageUrl(config.productImageUrl());
        verify(view).setBackgroundImageUrl(config.backgroundImageUrl());
    }

    @Test
    public void setupWithOneConfigWithoutBackgroundImageTest() {
        doReturn(false).when(aboutPopupConfigs).isUnsatisfied();
        doReturn(false).when(aboutPopupConfigs).isAmbiguous();
        doReturn(new AboutPopupConfigMock("")).when(aboutPopupConfigs).get();

        aboutPopup.setup();

        verify(view).init(aboutPopup);
        verify(view).setProductName(config.productName());
        verify(view).setProductVersion(config.productVersion());
        verify(view).setProductLicense(config.productLicense());
        verify(view).setProductImageUrl(config.productImageUrl());
        verify(view,
               never()).setBackgroundImageUrl(anyString());
    }

    @Test(expected = RuntimeException.class)
    public void setupWithNoConfigTest() {
        doReturn(true).when(aboutPopupConfigs).isUnsatisfied();
        doReturn(false).when(aboutPopupConfigs).isAmbiguous();

        aboutPopup.setup();
    }

    @Test(expected = RuntimeException.class)
    public void setupWithSeveralConfigsTest() {
        doReturn(false).when(aboutPopupConfigs).isUnsatisfied();
        doReturn(true).when(aboutPopupConfigs).isAmbiguous();

        aboutPopup.setup();
    }

    @Test
    public void showTest() {
        aboutPopup.show();

        verify(view).show();
    }

    class AboutPopupConfigMock implements AboutPopupConfig {

        private String backgroundImageUrl;

        public AboutPopupConfigMock() {
            this("backgroundImageUrl");
        }

        public AboutPopupConfigMock(final String backgroundImageUrl) {
            this.backgroundImageUrl = backgroundImageUrl;
        }

        @Override
        public String productName() {
            return "productName";
        }

        @Override
        public String productVersion() {
            return "productVersion";
        }

        @Override
        public String productLicense() {
            return "productLicense";
        }

        @Override
        public String productImageUrl() {
            return "productImageUrl";
        }

        @Override
        public String backgroundImageUrl() {
            return this.backgroundImageUrl;
        }
    }
}
