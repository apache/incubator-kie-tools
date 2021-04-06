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

package org.jboss.errai.marshalling.server.protocol;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.errai.marshalling.client.protocols.ErraiProtocol;

/**
 * @author Mike Brock
 */
public class ErraiProtocolServer extends ErraiProtocol {

  private static List<PayloadPreprocessor> preprocessors = new ArrayList<>();

  public static ByteArrayInputStream encodePayloadToByteArrayInputStream(final Map<String, Object> payload) {
    try {
      // Process the payload before the encoding process.
      preprocessors.forEach(preprocessor -> preprocessor.process(payload));

      return new ByteArrayInputStream(encodePayload(payload).getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new AssertionError("UTF-8 appears not to be supported by this JRE, but that's impossible");
    }
  }

  public static void addPreprocessor(PayloadPreprocessor preprocessor) {
    preprocessors.add(preprocessor);
  }

  public static void removePreprocessor(PayloadPreprocessor preprocessor) {
    preprocessors.remove(preprocessor);
  }

  public static void clearPreprocessors() {
    preprocessors.clear();
  }
}
