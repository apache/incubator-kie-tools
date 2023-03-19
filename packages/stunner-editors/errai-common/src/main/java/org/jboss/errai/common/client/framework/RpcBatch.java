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

package org.jboss.errai.common.client.framework;

/**
 * Represents a batch for remote procedure calls. It's used to accumulate remote calls that will get executed in a
 * single server round trip when {@link #flush()} is called.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 * 
 * @param <T>
 */
public interface RpcBatch<T> {

  /**
   * Adds a request of type <T> to the queue.
   * 
   * @param request
   *          the request to send when {@link #flush()} is called.
   */
  public void addRequest(T request);

  /**
   * Sends all accumulated requests in a single server round trip.
   */
  public void flush();
}
