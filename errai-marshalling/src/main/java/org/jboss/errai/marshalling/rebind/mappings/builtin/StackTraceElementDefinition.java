/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.marshalling.rebind.mappings.builtin;

import org.jboss.errai.marshalling.rebind.api.CustomMapping;
import org.jboss.errai.marshalling.rebind.api.model.MappingDefinition;
import org.jboss.errai.marshalling.rebind.api.model.impl.ReadMapping;
import org.jboss.errai.marshalling.rebind.api.model.impl.SimpleConstructorMapping;

/**
 * @author Mike Brock
 */
@CustomMapping(StackTraceElement.class)
public class StackTraceElementDefinition extends MappingDefinition {
  public StackTraceElementDefinition() {
    super(StackTraceElement.class);

    SimpleConstructorMapping constructorMapping = new SimpleConstructorMapping();
    constructorMapping.mapParmToIndex("declaringClass", 0, String.class);
    constructorMapping.mapParmToIndex("methodName", 1, String.class);
    constructorMapping.mapParmToIndex("fileName", 2, String.class);
    constructorMapping.mapParmToIndex("lineNumber", 3, int.class);

    setInstantiationMapping(constructorMapping);

    addMemberMapping(new ReadMapping("fileName", String.class, "getFileName"));
    addMemberMapping(new ReadMapping("methodName", String.class, "getMethodName"));
    addMemberMapping(new ReadMapping("lineNumber", Integer.class, "getLineNumber"));
    addMemberMapping(new ReadMapping("declaringClass", String.class, "getClassName"));
  }
}
