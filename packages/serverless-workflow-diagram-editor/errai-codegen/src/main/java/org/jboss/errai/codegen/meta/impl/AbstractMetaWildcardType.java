/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.codegen.meta.impl;

import org.jboss.errai.codegen.meta.MetaWildcardType;

/**
 * Base implementation for implementations of {@link MetaWildcardType},
 * providing uniform hashCode, equals, and toString implementations.
 *
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public abstract class AbstractMetaWildcardType implements MetaWildcardType {

  @Override
  public final String toString() {
    return getName();
  }

  @Override
  public final boolean equals(Object other) {
    return other instanceof AbstractMetaWildcardType &&
            getName().equals(((AbstractMetaWildcardType) other).getName());
  }

  @Override
  public final int hashCode() {
    return getName().hashCode();
  }
}
