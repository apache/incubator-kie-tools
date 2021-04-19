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
import org.jboss.errai.marshalling.rebind.api.InheritedMappings;
import org.jboss.errai.marshalling.rebind.api.model.MappingDefinition;
import org.jboss.errai.marshalling.rebind.api.model.impl.AccessorMapping;
import org.jboss.errai.marshalling.rebind.api.model.impl.ReadMapping;
import org.jboss.errai.marshalling.rebind.api.model.impl.SimpleConstructorMapping;
import org.jboss.errai.marshalling.rebind.api.model.impl.WriteMapping;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.EmptyStackException;

/**
 * @author Mike Brock
 */
@CustomMapping(Throwable.class)
@InheritedMappings(
        {ArithmeticException.class, IOException.class, IllegalArgumentException.class,
                 EmptyStackException.class, ArrayStoreException.class,
        ClassCastException.class, ConcurrentModificationException.class, IndexOutOfBoundsException.class,
        NegativeArraySizeException.class, NullPointerException.class, StringIndexOutOfBoundsException.class,
        UnsupportedOperationException.class, AssertionError.class, RuntimeException.class}
)
public class ThrowableDefinition extends MappingDefinition {
  public ThrowableDefinition() {
    super(Throwable.class);

    SimpleConstructorMapping constructorMapping = new SimpleConstructorMapping();
    constructorMapping.mapParmToIndex("message", 0, String.class);
    setInstantiationMapping(constructorMapping);
    addMemberMapping(new WriteMapping("cause", Throwable.class, "initCause"));

    addMemberMapping(new AccessorMapping("stackTrace", StackTraceElement[].class, "setStackTrace", "getStackTrace"));

    addMemberMapping(new ReadMapping("message", String.class, "getMessage"));
    addMemberMapping(new ReadMapping("cause", Throwable.class, "getCause"));
  }
}
