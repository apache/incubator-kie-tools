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

package org.jboss.errai.marshalling.client;

import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.protocols.MarshallingSessionProvider;

/**
 * @author Mike Brock
 */
public class MarshallingSessionProviderFactory {
  private static MarshallingSessionProvider sessionProvider;

  public static boolean isMarshallingSessionProviderRegistered() {
    return sessionProvider != null;
  }

  public static void setMarshallingSessionProvider(final MarshallingSessionProvider provider) {
//    if (sessionProvider != null) {
//      throw new RuntimeException("session provider already set");
//    }
    sessionProvider = provider;
  }

  public static MarshallingSessionProvider getProvider() {
    return sessionProvider;
  }

  public static MarshallingSession getEncoding() {
    return sessionProvider.getEncoding();
  }

  public static MarshallingSession getDecoding() {
    return sessionProvider.getDecoding();
  }
}
