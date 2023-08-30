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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the target Errai IOC bean can be switched on and off at GWT rebind time
 * by setting a Java System Property.
 * 
 * @author Mike Brock
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnabledByProperty {

  /**
   * The name of the system property that enables or disables this bean.
   */
  String value();

  /**
   * Normally, the target bean is enabled if the specified system property exists and
   * matches the declared {@code matchValue}. If {@code negated} is {@code true}
   * then this sense is reversed: the bean is disabled if the specified system property
   * equals the {@code matchValue}.
   */
  boolean negated() default false;

  /**
   * When enabled, the matchValue is used as the default value in case the system property is not set.
   */
  boolean matchByDefault() default false;

  /**
   * Whether it should consider case sensitive when comparing the match value.
   */
  boolean caseSensitive() default false;

  /**
   * Expected property value to match for considering this bean enabled
   */
  String matchValue() default "true";

}
