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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.backend.marshalling.v1_2.xstream.DMNModelInstrumentedBaseConverter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;

public class ExternalLinksConverter extends DMNModelInstrumentedBaseConverter {

    static final String NAME = "name";

    static final String URL = "url";

    public ExternalLinksConverter(final XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignAttributes(final HierarchicalStreamReader reader,
                                    final Object parent) {
        superAssignAttributes(reader, parent);
        final ExternalLink externalLink = (ExternalLink) parent;
        externalLink.setName(reader.getAttribute(NAME));
        externalLink.setUrl(reader.getAttribute(URL));
    }

    void superAssignAttributes(final HierarchicalStreamReader reader,
                               final Object parent) {
        super.assignAttributes(reader, parent);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new ExternalLink();
    }

    @Override
    protected void writeAttributes(final HierarchicalStreamWriter writer,
                                   final Object parent) {
        superWriteAttributes(writer, parent);
        final ExternalLink externalLink = (ExternalLink) parent;
        writer.addAttribute(URL, externalLink.getUrl());
        writer.addAttribute(NAME, externalLink.getName());
    }

    void superWriteAttributes(final HierarchicalStreamWriter writer,
                              final Object parent) {
        super.writeAttributes(writer, parent);
    }

    @Override
    public boolean canConvert(final Class aClass) {
        return aClass.equals(ExternalLink.class);
    }
}
