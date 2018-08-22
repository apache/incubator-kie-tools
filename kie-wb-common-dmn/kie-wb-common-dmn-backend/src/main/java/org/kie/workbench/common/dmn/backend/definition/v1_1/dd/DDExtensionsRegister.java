/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import javax.xml.namespace.QName;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import org.kie.dmn.api.marshalling.v1_1.DMNExtensionRegister;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;

public class DDExtensionsRegister implements DMNExtensionRegister {

    @Override
    public void registerExtensionConverters(XStream xstream) {
        xstream.processAnnotations(DMNDiagram.class);
        xstream.processAnnotations(DMNShape.class);
    }

    @Override
    public void beforeMarshal(Object o, QNameMap qmap) {
        if (o instanceof DMNModelInstrumentedBase) {
            DMNModelInstrumentedBase base = (DMNModelInstrumentedBase) o;

            String ddPrefix = base.getPrefixForNamespaceURI(DMNDiagram.DMNV11_DD).orElse("dd");
            qmap.registerMapping(new QName(DMNDiagram.DMNV11_DD, "DMNDiagram", ddPrefix), "DMNDiagram");
            qmap.registerMapping(new QName(DMNDiagram.DMNV11_DD, "DMNShape", ddPrefix), "DMNShape");
            qmap.registerMapping(new QName(DMNDiagram.DMNV11_DD, "DMNFontStyle", ddPrefix), "DMNFontStyle");
            qmap.registerMapping(new QName(DMNDiagram.DMNV11_DD, "BorderSize", ddPrefix), "BorderSize");

            String dcPrefix = base.getPrefixForNamespaceURI(DMNDiagram.DMNV11_DC).orElse("dc");
            qmap.registerMapping(new QName(DMNDiagram.DMNV11_DC, "Bounds", dcPrefix), "Bounds");
            qmap.registerMapping(new QName(DMNDiagram.DMNV11_DC, "BgColor", dcPrefix), "BgColor");
            qmap.registerMapping(new QName(DMNDiagram.DMNV11_DC, "BorderColor", dcPrefix), "BorderColor");
            qmap.registerMapping(new QName(DMNDiagram.DMNV11_DC, "FontColor", dcPrefix), "FontColor");
        }
    }
}
