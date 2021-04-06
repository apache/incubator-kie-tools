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

package org.jboss.errai.marshalling.server;

import org.jboss.errai.marshalling.client.Marshalling;
import org.jboss.errai.marshalling.client.MarshallingSessionProviderFactory;
import org.jboss.errai.marshalling.client.api.MarshallingSession;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Mike Brock
 */
public abstract class ServerMarshalling extends Marshalling {
  @SuppressWarnings("unchecked")
  public static <T> T fromJSON(final InputStream inputStream, final Class<T> type) throws IOException {
    final MarshallingSession session = MarshallingSessionProviderFactory.getDecoding();
    return (T) session.getMarshallerInstance(type.getName()).demarshall(JSONStreamDecoder.decode(inputStream), session);
  }

  public static Object fromJSON(final InputStream inputStream) throws IOException {
    return fromJSON(inputStream, Object.class);
  }
}
