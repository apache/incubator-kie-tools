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


package org.jboss.errai.ioc.rebind.ioc.graph.impl;

import org.jboss.errai.codegen.meta.HasAnnotations;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.DependencyType;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.ProducerInstanceDependency;

/**
 * @see ProducerInstanceDependency
 * @author Max Barkley <mbarkley@redhat.com>
 */
class ProducerInstanceDependencyImpl extends BaseDependency implements ProducerInstanceDependency {

  MetaClassMember producingMember;

  ProducerInstanceDependencyImpl(final InjectableReference abstractInjectable, final DependencyType dependencyType, final MetaClassMember producingMember) {
    super(abstractInjectable, dependencyType);
    this.producingMember = producingMember;
  }

  @Override
  public MetaClassMember getProducingMember() {
    return producingMember;
  }

  @Override
  protected HasAnnotations getAnnotated() {
    return producingMember;
  }

}
