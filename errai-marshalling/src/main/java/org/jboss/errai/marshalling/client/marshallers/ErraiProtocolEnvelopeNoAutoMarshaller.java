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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.WrappedPortable;
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.json.EJObject;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.util.MarshallUtil;

/**
 * @author Mike Brock
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class ErraiProtocolEnvelopeNoAutoMarshaller implements Marshaller<Map<String, Object>> {
  public static final ErraiProtocolEnvelopeNoAutoMarshaller INSTANCE = new ErraiProtocolEnvelopeNoAutoMarshaller();

  @Override
  public Map<String, Object> demarshall(final EJValue o, final MarshallingSession ctx) {
    return doDemarshall(new HashMap<String, Object>(), o, ctx);
  }

  protected Map<String, Object> doDemarshall(final Map<String, Object> impl, final EJValue o, final MarshallingSession ctx) {
    final EJObject jsonObject = o.isObject();
    if (jsonObject == null)
      return null;

    for (final String key : jsonObject.keySet()) {
      final EJValue v = jsonObject.get(key);
      if (!v.isNull()) {
        final String type = ctx.determineTypeFor(null, v);

        if (type == null) {
          impl.put(key, v.toString());
        }
        else {
          impl.put(key, ctx.getMarshallerInstance(type).demarshall(v, ctx));
        }
      }
      else {
        impl.put(key, null);
      }
    }
    return impl;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String marshall(final Map<String, Object> o, final MarshallingSession ctx) {
    final StringBuilder buf = new StringBuilder();

    buf.append("{");
    Object key, val;
    int i = 0;
    for (final Map.Entry<String, Object> entry : o.entrySet()) {
      if (i++ > 0) {
        buf.append(",");
      }
      key = entry.getKey();
      val = entry.getValue();

      final Marshaller valueMarshaller;
      buf.append("\"").append(key).append("\"");

      buf.append(":");

      if (val == null) {
        buf.append("null");
      }
      else {
        if ((val instanceof Number && !(val instanceof BigInteger || val instanceof BigDecimal))
                || val instanceof Boolean || val instanceof Character) {

          valueMarshaller = MarshallUtil.getQualifiedNumberMarshaller(val);
        }
        else {
          if (!ctx.getMappingContext().hasMarshaller(val.getClass().getName())) {
            val = val.toString();
            valueMarshaller = StringMarshaller.INSTANCE;
          }
          else {
            valueMarshaller = MarshallUtil.getMarshaller(MarshallUtil.maybeUnwrap(val), ctx);
          }
        }
        if (val instanceof WrappedPortable) {
          val = ((WrappedPortable) val).unwrap();
        }
        buf.append(valueMarshaller.marshall(val, ctx));
      }
    }

    return buf.append("}").toString();
  }

  @Override
  public Map<String, Object>[] getEmptyArray() {
    throw new UnsupportedOperationException("Not implemented!");
  }
  
}
