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

package org.jboss.errai.marshalling.client.protocols;

import org.jboss.errai.marshalling.client.MarshallingSessionProviderFactory;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.marshallers.ErraiProtocolEnvelopeMarshaller;

import java.util.Map;

/**
 * A simple convenience class for the client and server bus' to use to encode/decode wire messages.
 *
 * @author Mike Brock
 */
public class ErraiProtocol {
  private static final ErraiProtocolEnvelopeMarshaller protocolMarshaller = new ErraiProtocolEnvelopeMarshaller();

  /**
   * Encode a standard Errai Protocol payload of the key-value pairs to be sent across the wire.
   *
   * @param message A map of the key-value pairs to be encoded.
   * @return The encoded JSON
   */
  public static String encodePayload(final Map<String, Object> message) {
    return protocolMarshaller.marshall(message, MarshallingSessionProviderFactory.getEncoding());
  }

  /**
   * Decode a standard Errai Protocol payload to a Map of key value pairs.
   *
   * @param value The root JSON element to start parsing from.
   * @return The decoded Map.
   */
  public static Map<String, Object> decodePayload(final EJValue value) {
    return protocolMarshaller.demarshall(value, MarshallingSessionProviderFactory.getDecoding());
  }
}
