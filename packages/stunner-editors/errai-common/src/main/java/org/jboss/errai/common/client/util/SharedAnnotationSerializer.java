/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.common.client.util;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class SharedAnnotationSerializer {

  public static String serialize(final Annotation qualifier, final AnnotationPropertyAccessor entry) {
    if (entry != null) {
      final StringBuilder builder = new StringBuilder(qualifier.annotationType().getName());
      if (!entry.accessorsByPropertyName.isEmpty()) {
        builder.append('(');
        for (final Map.Entry<String, Function<Annotation, String>> e : entry.accessorsByPropertyName.entrySet()) {
          builder.append(e.getKey())
                 .append('=')
                 .append(e.getValue().apply(qualifier))
                 .append(',');
        }
        builder.replace(builder.length()-1, builder.length(), ")");
      }

      return builder.toString();
    }
    else {
      return qualifier.annotationType().getName();
    }
  }

  public static String stringify(final Object value) {
    return String.valueOf(value);
  }

  public static String stringify(final Class<?>[] value) {
    return Arrays.toString(value);
  }

  public static String stringify(final byte[] value) {
    return Arrays.toString(value);
  }

  public static String stringify(final int[] value) {
    return Arrays.toString(value);
  }

  public static String stringify(final short[] value) {
    return Arrays.toString(value);
  }

  public static String stringify(final char[] value) {
    return Arrays.toString(value);
  }

  public static String stringify(final float[] value) {
    return Arrays.toString(value);
  }

  public static String stringify(final double[] value) {
    return Arrays.toString(value);
  }

  public static String stringify(final String[] value) {
    return Arrays.toString(value);
  }
}
