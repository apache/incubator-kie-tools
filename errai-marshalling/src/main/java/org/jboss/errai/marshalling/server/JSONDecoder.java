/*
 * Copyright (C) 2010 Red Hat, Inc. and/or its affiliates.
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

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.jboss.errai.marshalling.client.api.json.EJValue;

/**
 * Decodes a JSON string or character array, and provides a proper collection of elements
 */
public class JSONDecoder {
  public static EJValue decode(final String o) {
    try {
      return new JSONStreamDecoder(new ByteArrayInputStream(o.getBytes("UTF-8"))).parse();
    } catch (UnsupportedEncodingException e) {
      throw new AssertionError("UTF-8 not supported by this JRE?");
    }
  }
}
