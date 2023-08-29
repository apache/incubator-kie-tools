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


package org.jboss.errai.codegen.meta.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaParameterizedType;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.MetaTypeVariable;
import org.jboss.errai.codegen.meta.MetaWildcardType;
import org.jboss.errai.common.client.api.Assert;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public abstract class AbstractMetaParameterizedType implements MetaParameterizedType {
  @Override
  public boolean isAssignableFrom(final MetaParameterizedType fromType) {
    final MetaType[] compareFrom = Assert.notNull(fromType).getTypeParameters();
    final MetaType[] compareTo = getTypeParameters();

    if (compareTo.length != compareFrom.length) return false;

    for (int i = 0; i < compareTo.length; i++) {
      final MetaType from = compareFrom[i];
      final MetaType to = compareTo[i];
      if (from instanceof MetaClass && to instanceof MetaClass) {
        if (!((MetaClass) from).isAssignableTo((MetaClass) to)) {
          return false;
        }
      }
      else if (to instanceof MetaParameterizedType) {
        return false;
      }
      else if (to instanceof MetaWildcardType) {
        if (from instanceof MetaClass) {
          final MetaClass fromMC = (MetaClass) from;
          final boolean violatesUpperBound = getConcreteBounds(((MetaWildcardType) to).getUpperBounds())
            .filter(bound -> !fromMC.isAssignableTo(bound))
            .findAny()
            .isPresent();
          final boolean violatesLowerBound = getConcreteBounds(((MetaWildcardType) to).getLowerBounds())
            .filter(bound -> !bound.isAssignableTo(fromMC))
            .findAny()
            .isPresent();

          if (violatesLowerBound || violatesUpperBound) {
            return false;
          }
        }
        else {
          return false;
        }
      }
      else if (from instanceof MetaTypeVariable && to instanceof MetaClass) {
        final boolean hasAssignableUpperBound = getConcreteBounds(((MetaTypeVariable) from).getBounds())
          .filter(fromBound -> fromBound.isAssignableFrom((MetaClass) to))
          .findAny()
          .isPresent();
        if (!hasAssignableUpperBound) {
          return false;
        }
      }
    }

    return true;
  }

  private static Stream<MetaClass> getConcreteBounds(final MetaType[] bounds) {
    return Arrays
      .stream(bounds)
      .flatMap(bound -> {
        if (bound instanceof MetaClass) {
          return Collections.singletonList(bound).stream();
        }
        else if (bound instanceof MetaTypeVariable) {
          final MetaTypeVariable mtv = (MetaTypeVariable) bound;
          return getConcreteBounds(mtv.getBounds());
        }
        else {
          return Collections.emptyList().stream();
        }
      }).map(mt -> (MetaClass) mt);
  }

  @Override
  public final String toString() {
    final StringBuilder buf = new StringBuilder("<");
    final MetaType[] parms = getTypeParameters();
    for (int i = 0; i < parms.length; i++) {
      if (parms[i] instanceof MetaClass) {
        buf.append(((MetaClass) parms[i]).getFullyQualifiedName());
      }
      else {
        buf.append(parms[i].toString());
      }
      if (i + 1 < parms.length) buf.append(',');
    }
    return buf.append('>').toString();
  }

}
