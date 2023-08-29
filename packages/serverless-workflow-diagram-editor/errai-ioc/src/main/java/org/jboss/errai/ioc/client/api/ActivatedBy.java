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


package org.jboss.errai.ioc.client.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.errai.ioc.client.container.BeanActivator;
import org.jboss.errai.ioc.client.container.SyncBeanDef;

/**
 * Registers a {@link BeanActivator} for the annotated managed bean. The provided activator will be
 * used at runtime for checking whether or not the managed IOC bean is activated (see
 * {@link SyncBeanDef#isActivated()}).
 * 
 * Note that the managed IOC bean can be asynchronously loaded (when annotated with
 * {@link LoadAsync}), but the activator itself can not.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ActivatedBy {

  /**
   * The activator type to use. Instances are managed by Errai IOC.
   */
  Class<? extends BeanActivator> value();

}
