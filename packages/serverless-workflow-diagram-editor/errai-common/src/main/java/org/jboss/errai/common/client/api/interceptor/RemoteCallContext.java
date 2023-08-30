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


package org.jboss.errai.common.client.api.interceptor;

import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

/**
 * Represents the context of an intercepted remote call.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public abstract class RemoteCallContext extends CallContext {
  private Object result;

  /**
   * Returns the result of the intercepted remote call.
   *
   * @return intercepted method result, may be null.
   */
  public Object getResult() {
    return result;
  }

  /**
   * Sets the result of the intercepted remote call. When invoked from an
   * {@link ErrorCallback} it will prevent the error from bubbling up and
   * instead cause the {@link RemoteCallback} specified in the previous
   * interceptor or at the actual remote call site to be invoked with the
   * provided result.
   *
   * @param result
   *          The result to return to the caller of the intercepted method.
   */
  public void setResult(final Object result) {
    this.result = result;
  }

  /**
   * Proceeds to the next interceptor in the chain or with the execution of the
   * intercepted method if all interceptors have been executed.
   *
   * @param callback
   *          The remote callback that receives the return value from the call.
   *          This callback is guaranteed to be invoked before the callback
   *          provided on the actual call site. Cannot be null.
   */
  public abstract void proceed(RemoteCallback<?> callback);

  /**
   * Proceeds to the next interceptor in the chain or with the execution of the
   * intercepted method if all interceptors have been executed.
   *
   * @param callback
   *          The remote callback that receives the return value from the call.
   *          This callback is guaranteed to be invoked before the callback
   *          provided on the actual call site. Cannot be null.
   *
   * @param errorCallback
   *          The error callback that receives transmission errors and
   *          exceptions thrown by the remote service. This error callback is
   *          guaranteed to be invoked before the error callback provided on the
   *          actual call site. Cannot be null.
   */
  public abstract void proceed(RemoteCallback<?> callback, ErrorCallback<?> errorCallback);
}
