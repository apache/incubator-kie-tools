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


package org.jboss.errai.ioc.tests.decorator.client.res;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Brock
 */
public class TestDataCollector {
  private static final Map<String, Integer> beforeInvoke = new HashMap<String, Integer>();
  private static final Map<String, Integer> afterInvoke = new HashMap<String, Integer>();

  public static final Map<String, Object> properties = new HashMap<String, Object>();

  public static void beforeInvoke(String a, Integer b) {
    beforeInvoke.put(a, b);
  }

  public static void afterInvoke(String a, Integer b) {
    afterInvoke.put(a, b);
  }

  public static void property(String a, Object b) {
    properties.put(a, b);
  }

  public static Map<String, Integer> getBeforeInvoke() {
    return beforeInvoke;
  }

  public static Map<String, Integer> getAfterInvoke() {
    return afterInvoke;
  }

  public static Map<String, Object> getProperties() {
    return properties;
  }
}
