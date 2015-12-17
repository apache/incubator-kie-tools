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
import org.guvnor.common.services.project.model.WorkItemHandlerModel;

public class WorkItemHandlerConverter
        extends AbstractXStreamConverter {

    public WorkItemHandlerConverter() {
        super(WorkItemHandlerModel.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        WorkItemHandlerModel wih = (WorkItemHandlerModel) value;
        writer.addAttribute("type", wih.getType());
        writer.addAttribute("name", wih.getName());
    }

    public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final WorkItemHandlerModel wih = new WorkItemHandlerModel();
        wih.setType(reader.getAttribute("type"));
        wih.setName(reader.getAttribute("name"));

        return wih;
    }
}
