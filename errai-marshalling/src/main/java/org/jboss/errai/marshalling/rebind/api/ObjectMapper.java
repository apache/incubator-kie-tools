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

package org.jboss.errai.marshalling.rebind.api;

import org.jboss.errai.codegen.builder.ClassStructureBuilder;

/**
 * This class will actually figure out how to deconstruct and object and put it back together
 * by generating mappings, which represent value bind and value read statements.
 *
 * @author Mike Brock <cbrock@redhat.com>
 */
public interface ObjectMapper {
  ClassStructureBuilder<?> getMarshaller(String marshallerClassName);
}
