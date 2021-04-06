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

package org.jboss.errai.marshalling.client.marshallers;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.protocols.MessageParts;
import org.jboss.errai.marshalling.client.Marshalling;
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.json.EJObject;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.util.MarshallUtil;

/**
 * @author Mike Brock
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class ErraiProtocolEnvelopeMarshaller implements Marshaller<Map<String, Object>> {
  public static final ErraiProtocolEnvelopeMarshaller INSTANCE = new ErraiProtocolEnvelopeMarshaller();

  @Override
  public Map<String, Object> demarshall(final EJValue o, final MarshallingSession ctx) {
    return doDemarshall(new HashMap<String, Object>(), o, ctx);
  }

  protected Map<String, Object> doDemarshall(final Map<String, Object> impl,
                                             final EJValue o,
                                             final MarshallingSession ctx) {
    final EJObject jsonObject = o.isObject();

    for (final String key : jsonObject.keySet()) {
      if (MessageParts.SessionID.name().equals(key))
        continue;
      final EJValue v = jsonObject.get(key);
      if (!v.isNull()) {
        final Marshaller<Object> marshallerInstance = ctx.getMarshallerInstance(ctx.determineTypeFor(null, v));
        if (marshallerInstance == null) {
          if (MessageParts.Throwable.name().equals(key)) {
            EJValue msg = v.isObject().get("message");
            if (!msg.isNull() && msg.isString() != null) {
              impl.put(key, new Throwable(msg.isString().stringValue()));  
            }
            else {
              impl.put(key, new Throwable("No details provided"));
            }
            continue;
          }
          else {
            throw new RuntimeException("no marshaller for: " + ctx.determineTypeFor(null, v));
          }
        }
        impl.put(key, marshallerInstance.demarshall(v, ctx));
      }
      else {
        impl.put(key, null);
      }
    }
    return impl;
  }

  @Override
  public String marshall(final Map<String, Object> o, final MarshallingSession ctx) {
    final StringBuilder buf = new StringBuilder();

    buf.append("{");
    Object key, val;
    int i = 0;
    for (final Map.Entry<String, Object> entry : o.entrySet()) {
      key = entry.getKey();
      val = entry.getValue();

      if (MessageParts.SessionID.name().equals(key))
        continue;

      if (i++ > 0) {
        buf.append(",");
      }

      final Marshaller<Object> valueMarshaller;
      buf.append("\"").append(key).append("\"").append(":");

      if (val == null) {
        buf.append("null");
      }
      else {
        if (Marshalling.needsQualification(val)) {
          valueMarshaller = MarshallUtil.getQualifiedNumberMarshaller(val);
        }
        else {
          valueMarshaller = MarshallUtil.getMarshaller(val, ctx);
        }
        
        buf.append(valueMarshaller.marshall(MarshallUtil.maybeUnwrap(val), ctx));
      }
    }

    return buf.append("}").toString();
  }

  @Override
  public Map<String, Object>[] getEmptyArray() {
    throw new UnsupportedOperationException("Not implemented!");
  }

}
