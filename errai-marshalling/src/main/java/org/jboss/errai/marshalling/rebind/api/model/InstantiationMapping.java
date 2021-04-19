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

package org.jboss.errai.marshalling.rebind.api.model;

import java.util.List;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassMember;

/**
 * @author Mike Brock
 */
public interface InstantiationMapping {
  public Mapping[] getMappings();
  public Mapping[] getMappingsInKeyOrder(List<String> keys);
  public int getIndex(String key);
  
  public Class<?>[] getSignature();

  public MetaClassMember getMember();

  public void setMappingClass(MetaClass clazz);
  public MetaClass getMappingClass();
}
