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


package org.jboss.errai.ioc.async.client.test;

import org.jboss.errai.common.rebind.CacheUtil;
import org.jboss.errai.config.rebind.EnvUtil.EnvironmentConfigCache;
import org.jboss.errai.ioc.async.test.beanmanager.client.AsyncBeanManagerTests;
import org.jboss.errai.ioc.async.test.constructor.client.AsyncConstructorInjectionTests;
import org.jboss.errai.ioc.async.test.scopes.dependent.client.AsyncDependentScopeIntegrationTest;
import org.jboss.errai.ioc.async.test.scopes.dependent.client.AsyncSpecializationIntegrationTest;
import org.jboss.errai.ioc.tests.decorator.client.DecoratorAPITests;
import org.jboss.errai.ioc.tests.extensions.client.IOCLoggingInjectorTest;
import org.jboss.errai.ioc.tests.lifecycle.client.local.IOCLifecycleTest;
import org.jboss.errai.ioc.tests.qualifiers.client.QualifierEqualityTests;
import org.jboss.errai.ioc.tests.qualifiers.client.QualifierRegressionTests;
import org.jboss.errai.ioc.tests.wiring.client.AlternativeBeanIntegrationTest;
import org.jboss.errai.ioc.tests.wiring.client.BasicIOCTest;
import org.jboss.errai.ioc.tests.wiring.client.DenyListingBeansIntegrationTest;
import org.jboss.errai.ioc.tests.wiring.client.DisposerTest;
import org.jboss.tests.errai.ioc.wiring.client.AllowListingBeansIntegrationTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
@RunWith(Suite.class)
@SuiteClasses({
  AsyncBeanManagerTests.class,
  AsyncConstructorInjectionTests.class,
  AsyncDependentScopeIntegrationTest.class,
  AsyncSpecializationIntegrationTest.class,

  // Rerun as async
  BasicIOCTest.class,
  IOCLoggingInjectorTest.class,
  IOCLifecycleTest.class,
  DecoratorAPITests.class,
  QualifierEqualityTests.class,
  QualifierRegressionTests.class,
  AlternativeBeanIntegrationTest.class,
  DenyListingBeansIntegrationTest.class,
  AllowListingBeansIntegrationTest.class,
  DisposerTest.class
})
public class AsyncIOCTests {

  @BeforeClass
  public static void setup() {
    CacheUtil.getCache(EnvironmentConfigCache.class).addPermanentFrameworkProperty("errai.ioc.async_bean_manager", "true");
  }

  @AfterClass
  public static void tearDown() {
    CacheUtil.getCache(EnvironmentConfigCache.class).addPermanentFrameworkProperty("errai.ioc.async_bean_manager", "false");
  }

}
