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

package org.jboss.errai.codegen.literal;

import org.jboss.errai.codegen.Context;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class StringLiteral extends LiteralValue<String> {
  public StringLiteral(final String value) {
    super(value);
  }

  @Override
  public String getCanonicalString(final Context context) {
    final StringBuilder builder = new StringBuilder("\"");
    for (final char c : getValue().toCharArray()) {
      switch (c) {
        case '\\':
        case '"':
          builder.append("\\").append(c);
          break;
        case '\n':
          builder.append("\\n");
          break;
        case '\r':
          builder.append("\\r");
          break;
        case '\t':
          builder.append("\\t");
          break;
        default:
          builder.append(c);
      }
    }
    return builder.append("\"").toString();
  }
}
