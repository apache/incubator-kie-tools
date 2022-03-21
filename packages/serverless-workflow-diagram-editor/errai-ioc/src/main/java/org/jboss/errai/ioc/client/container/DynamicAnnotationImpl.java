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

package org.jboss.errai.ioc.client.container;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
class DynamicAnnotationImpl implements DynamicAnnotation {

  static DynamicAnnotation create(final String serialized) {
    final int openParenIndex = serialized.indexOf('(');
    final String fqcn;
    final Map<String, String> members;
    if (openParenIndex > -1) {
      fqcn = serialized.substring(0, openParenIndex);
      members = new HashMap<>();

      final String[] rawPairs = serialized.substring(openParenIndex+1, serialized.length()-1).split(",");
      for (final String rawPair : rawPairs) {
        final String[] splitPair = rawPair.split("=");
        members.put(splitPair[0], splitPair[1]);
      }
    }
    else {
      fqcn = serialized;
      members = Collections.emptyMap();
    }

    return new DynamicAnnotationImpl(fqcn, members);
  }

  private final String fqcn;
  private final Map<String, String> members;

  private DynamicAnnotationImpl(final String fqcn, final Map<String, String> members) {
    this.fqcn = fqcn;
    this.members = Collections.unmodifiableMap(members);
  }

  @Override
  public Class<? extends Annotation> annotationType() {
    return DynamicAnnotation.class;
  }

  @Override
  public String getName() {
    return fqcn;
  }

  @Override
  public Map<String, String> getMembers() {
    return members;
  }

}
