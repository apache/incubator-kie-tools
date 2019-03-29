/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.kmodule;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.guvnor.common.services.project.model.WorkItemHandlerModel;
import org.kie.soup.commons.xstream.XStreamUtils;
import org.kie.workbench.common.services.backend.kmodule.converters.ClockTypeConverter;
import org.kie.workbench.common.services.backend.kmodule.converters.FileLoggerConverter;
import org.kie.workbench.common.services.backend.kmodule.converters.KBaseConverter;
import org.kie.workbench.common.services.backend.kmodule.converters.KModuleConverter;
import org.kie.workbench.common.services.backend.kmodule.converters.KSessionConverter;
import org.kie.workbench.common.services.backend.kmodule.converters.ListenerConverter;
import org.kie.workbench.common.services.backend.kmodule.converters.QualifierConverter;
import org.kie.workbench.common.services.backend.kmodule.converters.WorkItemHandlerConverter;
import org.kie.workbench.common.services.shared.kmodule.ClockTypeOption;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;
import org.kie.workbench.common.services.shared.kmodule.ListenerModel;
import org.kie.workbench.common.services.shared.kmodule.QualifierModel;

public class KModuleContentHandler {

    public KModuleModel toModel(String xml) {
        return (KModuleModel) createXStream().fromXML(xml);
    }

    public String toString(KModuleModel model) {
        return createXStream().toXML(model);
    }

    private XStream createXStream() {
        XStream xStream = XStreamUtils.createTrustingXStream(new DomDriver());

        xStream.registerConverter(new KModuleConverter());
        xStream.registerConverter(new KBaseConverter());
        xStream.registerConverter(new KSessionConverter());
        xStream.registerConverter(new ClockTypeConverter());
        xStream.registerConverter(new ListenerConverter());
        xStream.registerConverter(new QualifierConverter());
        xStream.registerConverter(new WorkItemHandlerConverter());
        xStream.registerConverter(new FileLoggerConverter());

        xStream.alias("kmodule", KModuleModel.class);
        xStream.alias("kbase", KBaseModel.class);
        xStream.alias("ksession", KSessionModel.class);
        xStream.alias("clockType", ClockTypeOption.class);
        xStream.alias("listener", ListenerModel.class);
        xStream.alias("qualifier", QualifierModel.class);
        xStream.alias("workItemHandler", WorkItemHandlerModel.class);

        return xStream;
    }
}
