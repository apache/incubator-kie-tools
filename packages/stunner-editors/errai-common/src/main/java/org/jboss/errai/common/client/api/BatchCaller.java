/*
 * Copyright (C) 2013 Red Hat, Inc. and/or its affiliates.
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
 * An interface that can be used as an injection point for batched invocations of remote methods using generated
 * proxies. In contrast to {@link Caller}, no remote request will be sent until {@link #sendBatch()} is called.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface BatchCaller {

  /**
   * Returns an instance of the proxy that can schedule invocations on service methods on the service described by
   * {@code T}.
   * 
   * @param callback
   *          the callback to be invoked when the remote call has completed in success. In the case of an error, a
   *          default error callback will be notified. Which one depends on the proxy implementation.
   *          <p>
   *          The provided callback must not be null.
   * @param remoteService
   *          The interface describing the remote endpoint.
   * 
   * @return an instance of the proxy that can invoke service methods on the service described by {@code T}.
   */
  public <T> T call(RemoteCallback<?> callback, Class<T> remoteService);

  /**
   * Returns an instance of the proxy that can schedule invocations on service methods on the service described by
   * {@code T}.
   * 
   * @param callback
   *          the callback to be invoked when the remote call has completed in success. In the case of an error, a
   *          default error callback will be notified. Which one depends on the proxy implementation.
   *          <p>
   *          The callback must not be null.
   * @param errorCallback
   *          the callback to be invoked when the remote call has completed in failure. No callback is invoked in the
   *          case of success.
   *          <p>
   *          The provided callback must not be null.
   * @param remoteService
   *          The interface describing the remote endpoint.
   * 
   * @return an instance of the proxy that can invoke service methods on the service described by {@code T}.
   */
  public <T> T call(RemoteCallback<?> callback, ErrorCallback<?> errorCallback, Class<T> remoteService);

  /**
   * Invokes the accumulated remote requests using a single server round trip.
   */
  public void sendBatch();

  /**
   * Invokes the accumulated remote requests using a single server round trip.
   * 
   * @param callback
   *          the callback to be invoked when all remote calls have completed in success. Must not be null.
   */
  public void sendBatch(RemoteCallback<Void> callback);

  /**
   * Invokes the accumulated remote requests using a single server round trip.
   * 
   * @param errorCallback
   *          the callback to be invoked for all remote calls that have completed in failure. No callback is invoked
   *          in the case of success.
   */
  public void sendBatch(ErrorCallback<?> errorCallback);

  /**
   * Invokes the accumulated remote requests using a single server round trip.
   * 
   * @param callback
   *          the callback to be invoked when all remote calls have completed in success. Must not be null.
   * @param errorCallback
   *          the callback to be invoked for all remote calls that have completed in failure.
   */
  public void sendBatch(RemoteCallback<Void> callback, ErrorCallback<?> errorCallback);

}
