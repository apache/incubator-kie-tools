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


package org.kie.workbench.common.stunner.svg.gen.translator.impl;

import java.nio.file.Paths;

import org.kie.workbench.common.stunner.svg.gen.exception.TranslatorException;
import org.kie.workbench.common.stunner.svg.gen.model.ViewRefDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.ViewRefDefinitionImpl;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGDocumentTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGElementTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGTranslatorContext;
import org.w3c.dom.Element;

public class SVGUseTranslator implements SVGElementTranslator<Element, ViewRefDefinition> {

    public static final String HREF = "href";
    public static final String TAG_NAME = "use";

    @Override
    public String getTagName() {
        return "use";
    }

    @Override
    public ViewRefDefinition translate(final Element element,
                                       final SVGTranslatorContext context) throws TranslatorException {
        final String href = element.getAttributeNS(SVGDocumentTranslator.XLINK_URI,
                                                   HREF);
        // task-manual.svg#shape
        final int i2 = href.lastIndexOf("#");
        final String filePath = href.substring(0,
                                               i2);
        final String refViewId = href.substring(i2 + 1,
                                                href.length());
        final String parent = getId((Element) element.getParentNode());
        final String path = context.getPath().trim().length() > 0 ?
                Paths.get(context.getPath() + "/" + filePath).toString() :
                filePath;
        return new ViewRefDefinitionImpl(href,
                                         parent,
                                         refViewId,
                                         path);
    }

    @Override
    public Class<Element> getInputType() {
        return Element.class;
    }

    private String getId(final Element element) {
        return element.getAttribute("id");
    }
}
