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

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.impl.java.JavaReflectionClass;
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.rebind.api.model.impl.NoConstructMapping;
import org.jboss.errai.marshalling.rebind.api.model.impl.SimpleConstructorMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Mike Brock
 */
public class MappingDefinition {
  private final MetaClass toMap;

  private boolean lazy;
  private final boolean doNotGenerate;

  private Class<? extends Marshaller> clientMarshallerClass;
  private Class<? extends Marshaller> serverMarshallerClass;

  private Marshaller<Object> marshallerInstance;

  private InstantiationMapping instantiationMapping;

  private final List<MemberMapping> memberMappings;

  public MappingDefinition(final Marshaller<Object> marshaller, final MetaClass toMap, final boolean doNotGenerate) {
    this.toMap = toMap;
    setMarshallerInstance(marshaller);
    instantiationMapping = new NoConstructMapping();
    this.doNotGenerate = doNotGenerate;
    this.memberMappings = new ArrayList<MemberMapping>();
  }

  protected MappingDefinition(final Class<?> toMap) {
    this(toMap, false);
  }

  public MappingDefinition(final Class<?> toMap, final boolean doNotGenerate) {
    this(JavaReflectionClass.newUncachedInstance(toMap), doNotGenerate);
  }

  public MappingDefinition(final MetaClass toMap, final boolean doNotGenerate) {
    this.toMap = toMap;
    setInstantiationMapping(new SimpleConstructorMapping());
    this.doNotGenerate = doNotGenerate;
    this.memberMappings = new ArrayList<MemberMapping>();
  }


  public MetaClass getMappingClass() {
    return toMap;
  }

  public Class<? extends Marshaller> getClientMarshallerClass() {
    return clientMarshallerClass;
  }

  public void setClientMarshallerClass(final Class<? extends Marshaller> clientMarshallerClass) {
    this.clientMarshallerClass = clientMarshallerClass;
  }

  public Class<? extends Marshaller> getServerMarshallerClass() {
    return serverMarshallerClass;
  }

  public void setServerMarshallerClass(final Class<? extends Marshaller> serverMarshallerClass) {
    this.serverMarshallerClass = serverMarshallerClass;
  }

  public boolean alreadyGenerated() {
    return doNotGenerate;
  }

  public void setInstantiationMapping(final InstantiationMapping mapping) {
    mapping.setMappingClass(toMap);
    instantiationMapping = mapping;
  }

  public void setInheritedInstantiationMapping(final InstantiationMapping mapping) {
    instantiationMapping = mapping;
  }

  public void addMemberMapping(final MemberMapping mapping) {
    mapping.setMappingClass(toMap);
    memberMappings.add(mapping);
  }

  public void addInheritedMapping(final MemberMapping mapping) {
    memberMappings.add(mapping);
  }

  public InstantiationMapping getInstantiationMapping() {
    return instantiationMapping;
  }

  public List<MemberMapping> getMemberMappings() {
    return memberMappings;
  }

  private volatile List<MemberMapping> _readableMemberMappingsCache;

  public List<MemberMapping> getReadableMemberMappings() {
    if (_readableMemberMappingsCache != null) {
      return _readableMemberMappingsCache;
    }

    final List<MemberMapping> readableMemberMappings = new ArrayList<MemberMapping>();
    for (final MemberMapping memberMapping : memberMappings) {
      if (memberMapping.canRead()) {
        readableMemberMappings.add(memberMapping);
      }
    }
    return _readableMemberMappingsCache = Collections.unmodifiableList(readableMemberMappings);
  }

  private volatile List<MemberMapping> _writableMemberMappingsCache;

  public List<MemberMapping> getWritableMemberMappings() {
    if (_writableMemberMappingsCache != null) {
      return _writableMemberMappingsCache;
    }

    final List<MemberMapping> writableMemberMappings = new ArrayList<MemberMapping>();
    for (final MemberMapping memberMapping : memberMappings) {
      if (memberMapping.canWrite()) {
        writableMemberMappings.add(memberMapping);
      }
    }
    return _writableMemberMappingsCache = Collections.unmodifiableList(writableMemberMappings);
  }

  public Marshaller<Object> getMarshallerInstance() {
    return marshallerInstance;
  }

  public void setMarshallerInstance(final Marshaller marshallerInstance) {
    this.marshallerInstance = marshallerInstance;
  }

  public List<Mapping> getAllMappings() {
    final List<Mapping> mappingList = new ArrayList<Mapping>();
    if (getInstantiationMapping() != null) {
      mappingList.addAll(Arrays.asList(getInstantiationMapping().getMappings()));
    }
    mappingList.addAll(getMemberMappings());

    return Collections.unmodifiableList(mappingList);
  }

  public boolean isLazy() {
    return lazy;
  }

  public void setLazy(boolean lazy) {
    this.lazy = lazy;
  }

  @Override
  public String toString() {
    return "MappingDefinition [mappingClass=" + getMappingClass() + ", clientMarshallerClass="
            + getClientMarshallerClass() + ", serverMarshallerClass=" + getServerMarshallerClass() + "]";
  }

}
