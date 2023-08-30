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


package org.jboss.errai.common.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

/**
 * Dispatches a created bean via a callback.
 *
 * @author eric.wittmann@redhat.com
 */
public class AsyncBeanFactory {
  
  /**
   * Deliver/dispatch the new bean instance asynchronously.
   * @param bean
   * @param callback
   */
  public static final void createBean(final Object bean, @SuppressWarnings("rawtypes") final CreationalCallback callback) {
    GWT.runAsync(new RunAsyncCallback() {
      @SuppressWarnings("unchecked")
      public void onSuccess() {
        callback.callback(bean);
      }
      public void onFailure(Throwable caught) {
        // can't really fail
      }
    });
  }

}
