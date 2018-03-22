/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser;

import java.util.Set;

import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.IntegerFieldParser;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.ObjectParser;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.StringFieldParser;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public abstract class ElementParser<T extends Element<View>> extends ObjectParser implements ContextualParser {

    protected final T element;
    private Context context;

    public ElementParser(final String name,
                         final T element) {
        super(name);
        this.element = element;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(final Context context) {
        this.context = context;
        Object definition = element.getContent().getDefinition();
        // Resource id field.
        super.addParser(new StringFieldParser("resourceId",
                                              element.getUUID()));
        // Properties array.
        Object def = element.getContent().getDefinition();
        Set<?> properties = context.getDefinitionManager().adapters().forDefinition().getProperties(def);
        ObjectParser propertiesParser = new ObjectParser("properties");
        super.addParser(propertiesParser);
        if (null != properties && !properties.isEmpty()) {
            for (Object property : properties) {
                PropertyAdapter propertyAdapter = context.getDefinitionManager().adapters().registry().getPropertyAdapter(property.getClass());
                PropertyType propertyType = propertyAdapter.getType(property);
                String oryxPropId =
                        context.getOryxManager().getMappingsManager().getOryxPropertyId(def.getClass(),
                                                                                        property.getClass());
                Object value = propertyAdapter.getValue(property);
                String valueStr = value != null ?
                        context.getOryxManager().getPropertyManager().serialize(property,
                                                                                propertyType,
                                                                                value) : "";
                propertiesParser.addParser(new StringFieldParser(oryxPropId,
                                                                 valueStr));
            }
        }
        // Custom extended prpoerties, if any.
        parseExtendedProperties(propertiesParser);
        // Stencil id field.
        String defId = context.getOryxManager().getMappingsManager().getOryxDefinitionId(definition);
        super.addParser(new ObjectParser("stencil").addParser(new StringFieldParser("id",
                                                                                    defId)));
        // Bounds.
        Bounds.Bound ul = element.getContent().getBounds().getUpperLeft();
        Bounds.Bound lr = element.getContent().getBounds().getLowerRight();
        parseBounds(ul,
                    lr);
    }

    protected void parseExtendedProperties(final ObjectParser propertiesParser) {

    }

    protected void parseBounds(final Bounds.Bound ul,
                               final Bounds.Bound lr) {
        // Bounds.
        ObjectParser ulBoundParser = new ObjectParser("upperLeft")
                .addParser(new IntegerFieldParser("x",
                                                  ul.getX().intValue()))
                .addParser(new IntegerFieldParser("y",
                                                  ul.getY().intValue()));
        ObjectParser lrBoundParser = new ObjectParser("lowerRight")
                .addParser(new IntegerFieldParser("x",
                                                  lr.getX().intValue()))
                .addParser(new IntegerFieldParser("y",
                                                  lr.getY().intValue()));
        ObjectParser boundsParser = new ObjectParser("bounds")
                .addParser(lrBoundParser)
                .addParser(ulBoundParser);
        super.addParser(boundsParser);
    }

    @Override
    protected void setCurrentParser(final Parser p) {
        super.setCurrentParser(p);
        if (current instanceof ContextualParser) {
            ((ContextualParser) current).initialize(context);
        }
    }
}
