/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.services.project.backend.server.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.core.util.AbstractXStreamConverter;
import org.kie.workbench.common.services.project.service.model.QualifierModel;
import org.kie.workbench.common.services.project.service.model.WorkItemHandlerModel;

public class WorkItemHandelerConverter
        extends AbstractXStreamConverter {

    public WorkItemHandelerConverter() {
        super(WorkItemHandlerModel.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        WorkItemHandlerModel wih = (WorkItemHandlerModel) value;
        writer.addAttribute("type", wih.getType());
        QualifierModel qualifier = wih.getQualifierModel();
        if (qualifier != null) {
            if (qualifier.isSimple()) {
                writer.addAttribute("qualifier", qualifier.getType());
            } else {
                writeObject(writer, context, "qualifier", qualifier);
            }
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final WorkItemHandlerModel wih = new WorkItemHandlerModel();
        wih.setType(reader.getAttribute("type"));
        String qualifierType = reader.getAttribute("qualifier");
        if (qualifierType != null) {
            wih.newQualifierModel(qualifierType);
        }

        readNodes( reader, new AbstractXStreamConverter.NodeReader() {
            public void onNode(HierarchicalStreamReader reader,
                               String name,
                               String value) {
                if ( "qualifier".equals( name ) ) {
                    QualifierModel qualifier = readObject(reader, context, QualifierModel.class);
                    wih.setQualifierModel(qualifier);
                }
            }
        } );
        return wih;
    }
}
