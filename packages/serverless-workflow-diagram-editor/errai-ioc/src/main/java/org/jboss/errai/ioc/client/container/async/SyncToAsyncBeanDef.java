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


package org.jboss.errai.ioc.client.container.async;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.jboss.errai.common.client.util.CreationalCallback;
import org.jboss.errai.ioc.client.container.SyncBeanDef;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class SyncToAsyncBeanDef<T> implements AsyncBeanDef<T> {

  private final SyncBeanDef<T> beanDef;

  public SyncToAsyncBeanDef(final SyncBeanDef<T> beanDef) {
    this.beanDef = beanDef;
  }

  @Override
  public Class<T> getType() {
    return beanDef.getType();
  }

  @Override
  public Class<?> getBeanClass() {
    return beanDef.getBeanClass();
  }

  @Override
  public Class<? extends Annotation> getScope() {
    return beanDef.getScope();
  }

  @Override
  public Set<Annotation> getQualifiers() {
    return beanDef.getQualifiers();
  }

  @Override
  public boolean matches(final Set<Annotation> annotations) {
    return beanDef.matches(annotations);
  }

  @Override
  public String getName() {
    return beanDef.getName();
  }

  @Override
  public boolean isActivated() {
    return beanDef.isActivated();
  }

  @Override
  public void getInstance(final CreationalCallback<T> callback) {
    callback.callback(beanDef.getInstance());
  }

  @Override
  public void newInstance(final CreationalCallback<T> callback) {
    callback.callback(beanDef.newInstance());
  }

  @Override
  public boolean isAssignableTo(final Class<?> type) {
    return beanDef.isAssignableTo(type);
  }

}
