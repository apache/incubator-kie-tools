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

package org.jboss.errai.marshalling.client.util;

import java.util.Collection;
import java.util.Iterator;

import org.jboss.errai.marshalling.client.Marshalling;
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.marshallers.QualifyingMarshallerWrapper;

/**
 * @author Mike Brock
 */
public class EncDecUtil {
  public static void arrayMarshall(final StringBuilder buf, final Collection o, final MarshallingSession ctx) {
    final Iterator iter = o.iterator();

    buf.append("[");
    Object elem;

    int i = 0;
    while (iter.hasNext()) {
      if (i++ > 0) {
        buf.append(",");
      }
      elem = iter.next();
      
      if (elem != null) {
        final Marshaller<Object> marshaller;
        if (Marshalling.needsQualification(elem)) {
          marshaller = MarshallUtil.getQualifiedNumberMarshaller(elem);
        }
        else {
          marshaller = MarshallUtil.getMarshaller(elem, ctx);
        }
  
        buf.append(marshaller.marshall(MarshallUtil.maybeUnwrap(elem), ctx));
      } 
      else {
        buf.append("null");
      }
    }
    buf.append("]");
  }


  /**
   * Ensure the marshaller is qualified on the wire using a wrapping marshaller.
   * @param marshaller
   * @param <T>
   * @return
   */
  public static <T> Marshaller<T> qualifyMarshaller(final Marshaller<T> marshaller, Class<T> type) {
    return new QualifyingMarshallerWrapper<T>(marshaller, type);
  }
}
