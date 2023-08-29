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


package org.kie.workbench.common.stunner.svg.gen.model.impl;

import java.util.Collection;
import java.util.function.Consumer;

import org.kie.workbench.common.stunner.svg.gen.model.PrimitiveDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;

public class SVGModelUtils {

    public static String toValidJavaId(final String id) {
        return id.replaceAll("-", "_");
    }

    public static void visit(final ViewDefinition<?> viewDefinition,
                             final Consumer<PrimitiveDefinition> definitionConsumer) {
        definitionConsumer.accept(viewDefinition);
        visit(viewDefinition.getChildren(),
              definitionConsumer);
    }

    public static void visit(final Collection<PrimitiveDefinition> definitions,
                             final Consumer<PrimitiveDefinition> definitionConsumer) {
        for (final PrimitiveDefinition child : definitions) {
            if (child instanceof GroupDefinition) {
                visit(((GroupDefinition) child).getChildren(),
                      definitionConsumer);
            } else {
                definitionConsumer.accept(child);
            }
        }
    }
}
