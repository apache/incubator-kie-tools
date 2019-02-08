/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.backend.definition.v1_1.dd;

import java.util.Objects;

import javax.xml.namespace.QName;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.backend.marshalling.v1_2.xstream.DMNModelInstrumentedBaseConverter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;

import static org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDIExtensionsRegister.COMPONENT_WIDTH_ALIAS;

public class ComponentWidthsConverter extends DMNModelInstrumentedBaseConverter {

    public ComponentWidthsConverter(final XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(final Object parent,
                                      final String nodeName,
                                      final Object child) {
        super.assignChildElement(parent, nodeName, child);

        final ComponentWidths componentWidths = (ComponentWidths) parent;

        componentWidths.getWidths().add((Double) child);
    }

    @Override
    protected void assignAttributes(final HierarchicalStreamReader reader,
                                    final Object parent) {
        super.assignAttributes(reader, parent);

        final ComponentWidths componentWidths = (ComponentWidths) parent;

        componentWidths.setDmnElementRef(new QName(reader.getAttribute("dmnElementRef")));
    }

    @Override
    protected void writeChildren(final HierarchicalStreamWriter writer,
                                 final MarshallingContext context,
                                 final Object parent) {
        super.writeChildren(writer, context, parent);

        final ComponentWidths componentWidths = (ComponentWidths) parent;

        componentWidths.getWidths().forEach(width -> {
            if (Objects.nonNull(width)) {
                writeChildrenNode(writer,
                                  context,
                                  width,
                                  COMPONENT_WIDTH_ALIAS);
            }
        });
    }

    @Override
    protected void writeAttributes(final HierarchicalStreamWriter writer,
                                   final Object parent) {
        super.writeAttributes(writer, parent);

        final ComponentWidths componentWidths = (ComponentWidths) parent;

        writer.addAttribute("dmnElementRef", componentWidths.getDmnElementRef().toString());
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new ComponentWidths();
    }

    @Override
    public boolean canConvert(final Class clazz) {
        return clazz.equals(ComponentWidths.class);
    }
}
