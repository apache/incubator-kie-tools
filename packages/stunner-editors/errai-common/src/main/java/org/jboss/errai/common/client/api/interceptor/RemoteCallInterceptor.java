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

import org.jboss.errai.common.client.api.RemoteCallback;

/**
 * Represents an interceptor for asynchronous remote method calls.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 * 
 * @param <T>
 *          type of {@link CallContext}
 */
public interface RemoteCallInterceptor<T extends RemoteCallContext> extends CallInterceptor<T> {

  /**
   * Interposes on the execution of remote method calls that should be intercepted.
   * <p>
   * Note that in contrast to local/synchronous method call interceptors, this method does not return a result as the
   * actual remote call is executed asynchronously and the result is not available when this method returns.
   * <p>
   * To execute the actual remote call, invoke {@link RemoteCallContext#proceed()}.
   * <p>
   * To get access to the remote call's result, call {@link RemoteCallContext#proceed(RemoteCallback)} or
   * {@link RemoteCallContext#proceed(RemoteCallback, ErrorCallback)}.
   * <p>
   * To change the result, call {@link RemoteCallContext#setResult(Object)}.
   * 
   * @param context
   *          the call context of the intercepted method, not null.
   */
  public void aroundInvoke(T context);
}
