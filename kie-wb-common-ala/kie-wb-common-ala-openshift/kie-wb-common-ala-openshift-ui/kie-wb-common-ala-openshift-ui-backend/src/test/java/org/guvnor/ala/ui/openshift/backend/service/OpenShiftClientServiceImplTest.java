/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.openshift.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.ala.ui.openshift.model.DefaultSettings;
import org.guvnor.ala.ui.openshift.model.TemplateDescriptorModel;
import org.guvnor.ala.ui.openshift.model.TemplateParam;
import org.guvnor.ala.ui.openshift.service.OpenShiftClientService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class OpenShiftClientServiceImplTest {

    private static final String DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE = "DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE";

    private static final String DEFAULT_OPEN_SHIFT_IMAGE_STREAMS_VALUE = "DEFAULT_OPEN_SHIFT_IMAGE_STREAMS_VALUE";

    private static final String DEFAULT_OPEN_SHIFT_SECRETS_VALUE = "DEFAULT_OPEN_SHIFT_SECRETS_VALUE";

    private static final String TEMPLATE = "org/guvnor/ala/ui/openshift/backend/service/openshift-client-service-test.json";

    private static final int PARAMS_COUNT = 5;

    private OpenShiftClientService service;

    @Before
    public void setUp() {
        service = new OpenShiftClientServiceImpl();
    }

    @Test
    public void testGetDefaultSettings() {
        System.getProperties().setProperty(DefaultSettings.DEFAULT_OPEN_SHIFT_TEMPLATE,
                                           DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE);
        System.getProperties().setProperty(DefaultSettings.DEFAULT_OPEN_SHIFT_IMAGE_STREAMS,
                                           DEFAULT_OPEN_SHIFT_IMAGE_STREAMS_VALUE);
        System.getProperties().setProperty(DefaultSettings.DEFAULT_OPEN_SHIFT_SECRETS,
                                           DEFAULT_OPEN_SHIFT_SECRETS_VALUE);

        DefaultSettings defaultSettings = service.getDefaultSettings();
        assertEquals(DEFAULT_OPEN_SHIFT_TEMPLATE_VALUE,
                     defaultSettings.getValue(DefaultSettings.DEFAULT_OPEN_SHIFT_TEMPLATE));
        assertEquals(DEFAULT_OPEN_SHIFT_IMAGE_STREAMS_VALUE,
                     defaultSettings.getValue(DefaultSettings.DEFAULT_OPEN_SHIFT_IMAGE_STREAMS));
        assertEquals(DEFAULT_OPEN_SHIFT_SECRETS_VALUE,
                     defaultSettings.getValue(DefaultSettings.DEFAULT_OPEN_SHIFT_SECRETS));
    }

    @Test
    public void testGetTemplateModel() throws Exception {
        String url = getClass().getClassLoader().getResource(TEMPLATE).toString();
        TemplateDescriptorModel model = service.getTemplateModel(url);
        List<TemplateParam> expectedParams = buildExpectedParams(PARAMS_COUNT);
        assertEquals(expectedParams,
                     model.getParams());
    }

    private List<TemplateParam> buildExpectedParams(int count) {
        List<TemplateParam> params = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String name = String.format("PARAM%d",
                                        i);
            String displayName = String.format("PARAM%d display name",
                                               i);
            String description = String.format("PARAM%d description",
                                               i);
            boolean required = i % 2 == 0;
            String value = String.format("PARAM%d value",
                                         i);
            params.add(new TemplateParam(name,
                                         displayName,
                                         description,
                                         required,
                                         value));
        }
        return params;
    }
}
