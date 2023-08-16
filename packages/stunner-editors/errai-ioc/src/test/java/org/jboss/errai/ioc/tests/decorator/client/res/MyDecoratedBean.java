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

import javax.inject.Singleton;

/**
 * @author Mike Brock
 */
@Singleton
public class MyDecoratedBean {
  private final Map<String, Integer> testMap = new HashMap<String, Integer>();

  private boolean flag;

  @LogCall
  public void someMethod(final String text, final Integer blah) {
    testMap.put(text, blah);
  }

  public Map<String, Integer> getTestMap() {
    return testMap;
  }

  public boolean isFlag() {
    return flag;
  }

  public void setFlag(boolean flag) {
    this.flag = flag;
  }
}
