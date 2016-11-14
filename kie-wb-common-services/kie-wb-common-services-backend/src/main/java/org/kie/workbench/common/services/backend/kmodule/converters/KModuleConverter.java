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

package org.kie.workbench.common.services.backend.kmodule.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.core.util.AbstractXStreamConverter;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;

public class KModuleConverter
        extends AbstractXStreamConverter {

    public KModuleConverter() {
        super(KModuleModel.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        KModuleModel kModule = (KModuleModel) value;

        //https://issues.jboss.org/browse/DROOLS-1023 introduced "version-less" XSDs
        writer.addAttribute("xmlns", "http://www.drools.org/xsd/kmodule");
        writer.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

        for (KBaseModel kBaseModule : kModule.getKBases().values()) {
            writeObject(writer, context, "kbase", kBaseModule);
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final KModuleModel kModule = new KModuleModel();

        readNodes(reader, new AbstractXStreamConverter.NodeReader() {
            public void onNode(HierarchicalStreamReader reader, String name, String value) {
                if ("kbase".equals(name)) {
                    KBaseModel kBaseModule = readObject(reader, context, KBaseModel.class);
                    kModule.getKBases().put(kBaseModule.getName(), kBaseModule);
                }
            }
        });

        return kModule;
    }
}
