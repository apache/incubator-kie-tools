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


package org.jboss.errai.config.rebind;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Brock
 */
public class EnvironmentConfig {
  private final Map<String, String> frameworkProperties;

  EnvironmentConfig(final Map<String, String> frameworkProperties) {
    this.frameworkProperties = new HashMap<String, String>(frameworkProperties);
  }

  public Map<String, String> getFrameworkProperties() {
    return frameworkProperties;
  }

  public String getFrameworkOrSystemProperty(String property) {
    final String value = frameworkProperties.get(property);
    if (value == null) {
      return System.getProperty(property);
    }
    else {
      return value;
    }
  }
}
