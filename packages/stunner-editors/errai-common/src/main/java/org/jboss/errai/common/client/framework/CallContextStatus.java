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


package org.jboss.errai.common.client.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is used internally (in generated code for remote call interceptors) to store status information about an
 * interceptor's call context.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class CallContextStatus {
  private boolean proceeding = false;
  private boolean interceptorChainStarted = false;
  private final List<Class<?>> interceptors;
  
  public CallContextStatus(final Class<?>... interceptors) {
    this.interceptors = new ArrayList<Class<?>>(Arrays.asList(interceptors));
  }
    
  public void proceed() {
    this.proceeding = true;
    if (!interceptors.isEmpty()) {
      if (interceptorChainStarted) {
        interceptors.remove(0);
      }
      else {
        interceptorChainStarted = true;
      }
    }
  }

  public void setProceeding(final boolean proceeding) {
    this.proceeding = proceeding;
  }
  
  public boolean isProceeding() {
    return proceeding;
  }
  
  public Class<?> getNextInterceptor() {
    if (!interceptors.isEmpty()) {
      return interceptors.get(0);
    }
    else {
      return null;
    }
  }
}
