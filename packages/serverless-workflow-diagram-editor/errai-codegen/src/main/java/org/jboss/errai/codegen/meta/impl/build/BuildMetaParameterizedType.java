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

package org.jboss.errai.codegen.meta.impl.build;

import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.impl.AbstractMetaParameterizedType;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class BuildMetaParameterizedType extends AbstractMetaParameterizedType {
  private final MetaType[] types;
  private final MetaType ownerType;
  private final MetaType rawType;

  public BuildMetaParameterizedType(MetaType[] types, MetaType ownerType, MetaType rawType) {
    this.types = types;
    this.ownerType = ownerType;
    this.rawType = rawType;
  }

  @Override
  public MetaType[] getTypeParameters() {
    return types;
  }

  @Override
  public MetaType getOwnerType() {
    return ownerType;
  }

  @Override
  public MetaType getRawType() {
    return rawType;
  }

  @Override
  public String getName() {
    return ownerType.getName();
  }
}
