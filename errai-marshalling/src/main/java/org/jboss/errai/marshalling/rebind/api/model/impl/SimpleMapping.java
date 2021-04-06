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

package org.jboss.errai.marshalling.rebind.api.model.impl;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.marshalling.rebind.api.model.Mapping;

/**
 * @author Mike Brock
 */
public class SimpleMapping implements Mapping {
  protected final String key;
  protected MetaClass toMap;
  protected MetaClass type;
  protected final MetaClass targetType;

  protected SimpleMapping(final String key, final MetaClass targetType) {
    if (key == null) {
      throw new NullPointerException("key is null");
    }

    this.type = targetType.asBoxed(); 
    this.targetType = targetType.getErased().asBoxed();
    this.key = key;
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public MetaClass getType() {
    return type;
  }

  @Override
  public void setType(final MetaClass type) {
    this.type = type.asBoxed();
  }

  @Override
  public MetaClass getTargetType() {
    return targetType;
  }

  @Override
  public void setMappingClass(final MetaClass clazz) {
    this.toMap = clazz;
  }
}
