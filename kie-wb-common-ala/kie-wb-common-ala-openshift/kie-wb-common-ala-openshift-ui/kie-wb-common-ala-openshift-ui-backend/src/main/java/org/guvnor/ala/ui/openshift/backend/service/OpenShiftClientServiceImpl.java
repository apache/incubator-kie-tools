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

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.ala.openshift.access.OpenShiftTemplate;
import org.guvnor.ala.ui.openshift.model.DefaultSettings;
import org.guvnor.ala.ui.openshift.model.TemplateDescriptorModel;
import org.guvnor.ala.ui.openshift.model.TemplateParam;
import org.guvnor.ala.ui.openshift.service.OpenShiftClientService;
import org.jboss.errai.bus.server.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Service
@ApplicationScoped
public class OpenShiftClientServiceImpl
        implements OpenShiftClientService {

    private static final Logger logger = LoggerFactory.getLogger(OpenShiftClientServiceImpl.class);

    private static DefaultSettings defaultSettingsInstance;

    private static final String PROJECT_NAME_EXPRESSION = "(([a-z]+)|([0-9]+)|([\\-]+))+";

    public OpenShiftClientServiceImpl() {
        //Empty constructor for Weld proxying
    }

    @Override
    public DefaultSettings getDefaultSettings() {
        if (defaultSettingsInstance == null) {
            defaultSettingsInstance = new DefaultSettings();
            defaultSettingsInstance.setValue(DefaultSettings.DEFAULT_OPEN_SHIFT_TEMPLATE,
                                             System.getProperty(DefaultSettings.DEFAULT_OPEN_SHIFT_TEMPLATE));
            defaultSettingsInstance.setValue(DefaultSettings.DEFAULT_OPEN_SHIFT_IMAGE_STREAMS,
                                             System.getProperty(DefaultSettings.DEFAULT_OPEN_SHIFT_IMAGE_STREAMS));
            defaultSettingsInstance.setValue(DefaultSettings.DEFAULT_OPEN_SHIFT_SECRETS,
                                             System.getProperty(DefaultSettings.DEFAULT_OPEN_SHIFT_SECRETS));
            logger.debug("OpenShift default settings were set to");
            logger.debug(DefaultSettings.DEFAULT_OPEN_SHIFT_TEMPLATE + " = " + defaultSettingsInstance.getValue(DefaultSettings.DEFAULT_OPEN_SHIFT_TEMPLATE));
            logger.debug(DefaultSettings.DEFAULT_OPEN_SHIFT_IMAGE_STREAMS + " = " + defaultSettingsInstance.getValue(DefaultSettings.DEFAULT_OPEN_SHIFT_IMAGE_STREAMS));
            logger.debug(DefaultSettings.DEFAULT_OPEN_SHIFT_SECRETS + " = " + defaultSettingsInstance.getValue(DefaultSettings.DEFAULT_OPEN_SHIFT_SECRETS));
        }
        return defaultSettingsInstance;
    }

    @Override
    public TemplateDescriptorModel getTemplateModel(final String url) {
        checkNotNull("url",
                     url);

        OpenShiftTemplate template = new OpenShiftTemplate(url);
        List<TemplateParam> params = buildTemplateParams(template);
        final TemplateDescriptorModel descriptorModel = new TemplateDescriptorModel(params);
        return descriptorModel;
    }

    @Override
    public boolean isValidProjectName(final String projectName) {
        return projectName != null &&
                !projectName.startsWith("-") &&
                !projectName.endsWith("-") &&
                Pattern.compile(PROJECT_NAME_EXPRESSION).matcher(projectName).matches();
    }

    private List<TemplateParam> buildTemplateParams(final OpenShiftTemplate template) {
        return template.getParameters().stream().map(param -> new TemplateParam(param.getName(),
                                                                                param.getDisplayName(),
                                                                                param.getDescription(),
                                                                                param.isRequired(),
                                                                                param.getValue())).collect(Collectors.toList());
    }
}
