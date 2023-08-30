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


package org.jboss.errai.enterprise.client.cdi;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.Any;

import org.jboss.errai.enterprise.client.cdi.api.Message;
import org.jboss.errai.enterprise.client.cdi.api.MessageCallback;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Mike Brock
 */
public abstract class AbstractCDIEventCallback<T> implements MessageCallback {
  /**
   * The qualifiers a message must contain to be propagated.
   */
  protected final Set<String> qualifierSet = new HashSet<String>();

  public Set<String> getQualifiers() {
    return qualifierSet;
  }

  @SuppressWarnings("unchecked")
  @Override
  public final void callback(final Message message) {
    Set<String> msgQualifiers = message.get(Set.class, CDIProtocol.Qualifiers);

    if (msgQualifiers == null) {
      msgQualifiers = Collections.emptySet();
    }

    if (message.hasPart(CDIProtocol.FromClient)) {
      if (isDefault() || msgQualifiers.containsAll(qualifierSet)) {
        fireEvent((T) message.get(Object.class, CDIProtocol.BeanReference));
      }
    }
    else {
      // Our server-side CDI integration module knows of all client-side event
      // observers and sends a separate message specific to each matching
      // observer.
      if (msgQualifiers.equals(qualifierSet)) {
        fireEvent((T) message.get(Object.class, CDIProtocol.BeanReference));
      }
    }
  }

  protected boolean isDefault() {
    return qualifierSet.size() == 1 && qualifierSet.contains(Any.class.getName());
  }

  protected abstract void fireEvent(T event);
}
