/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.svg.gen.translator.impl;

import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.svg.gen.exception.TranslatorException;
import org.kie.workbench.common.stunner.svg.gen.model.ViewRefDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.GroupDefinition;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGElementTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGTranslatorContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SVGGroupTranslator
        extends AbstractSVGPrimitiveTranslator<Element, GroupDefinition>
        implements SVGElementTranslator<Element, GroupDefinition> {

    @Override
    public String getTagName() {
        return "g";
    }

    @Override
    protected GroupDefinition doTranslate(final Element element,
                                          final SVGTranslatorContext context) throws TranslatorException {
        final NodeList childNodes = element.getElementsByTagName(SVGUseTranslator.TAG_NAME);
        // Only look for view references inside the group. Discard the group itself for any other elements.
        if (null != childNodes && childNodes.getLength() > 0) {
            final String id = getOrSetId(element);
            final GroupDefinition groupDefinition = new GroupDefinition(id);
            for (int i = 0; i < childNodes.getLength(); i++) {
                final Node child = childNodes.item(i);
                final SVGElementTranslator<Element, Object> elementTranslator = context.getElementTranslator(SVGUseTranslator.TAG_NAME);
                final ViewRefDefinition childViewRef = (ViewRefDefinition) elementTranslator.translate((Element) child,
                                                                                                       context);
                context.addSVGViewRef(childViewRef);
            }
            return groupDefinition;
        }
        return null;
    }

    private String getOrSetId(final Element element) {
        final String id = getId(element);
        if (null == id || id.trim().length() == 0) {
            final String uuid = UUID.uuid(4);
            element.setAttribute(ID,
                                 uuid);
            return uuid;
        }
        return id;
    }

    @Override
    public Class<Element> getInputType() {
        return Element.class;
    }
}
