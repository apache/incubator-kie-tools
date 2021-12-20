/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.common.client.api;

/**
 * Callback interface for receiving the response of an RPC call, regardless of the RPC mechanism.
 *
 * @see MessageBuilder#createCall()
 * @see MessageBuilder#createCall(RemoteCallback, Class)
 * @see MessageBuilder#createCall(RemoteCallback, ErrorCallback, Class)
 * @see the Errai JAX-RS client module
 * @param <R>
 *          type of response the callback expects. Use {@link Void} for methods returning {@code void}.
 */
public interface RemoteCallback<R> {

  /**
   * Invoked by the RPC proxy after the remote method has been executed and its
   * response has been received.
   *
   * @param response
   *          the response returned from the remote call. Will be null if the
   *          remote method returns null or has a void return type.
   */
  public void callback(R response);
}
