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


package org.jboss.errai.validation.client.dynamic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.validation.Constraint;

import com.google.gwt.regexp.shared.RegExp;
import org.jboss.errai.common.client.logging.util.StringFormat;

/**
 * Used for looking up {@link GeneratedDynamicValidator GeneratedDynamicValidators}.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class DynamicValidatorKey {

  private static final RegExp objArrayRegExp = RegExp.compile("^\\[+L[^;]+;$");
  private static final Map<String, String> typeAliases = new HashMap<>();

  static {
    typeAliases.put(Collections.emptyList().getClass().getName(), List.class.getName());
    typeAliases.put(Collections.singletonList(null).getClass().getName(), List.class.getName());
    typeAliases.put(ArrayList.class.getName(), List.class.getName());
    typeAliases.put(LinkedList.class.getName(), List.class.getName());

    typeAliases.put(Collections.emptySet().getClass().getName(), Set.class.getName());
    typeAliases.put(Collections.singleton(null).getClass().getName(), Set.class.getName());
    typeAliases.put(HashSet.class.getName(), Set.class.getName());
    typeAliases.put(LinkedHashSet.class.getName(), Set.class.getName());
    typeAliases.put(TreeSet.class.getName(), Set.class.getName());

    typeAliases.put(Collections.emptyMap().getClass().getName(), Map.class.getName());
    typeAliases.put(Collections.singletonMap(null, null).getClass().getName(), Map.class.getName());
    typeAliases.put(HashMap.class.getName(), Map.class.getName());
    typeAliases.put(LinkedHashMap.class.getName(), Map.class.getName());
    typeAliases.put(TreeMap.class.getName(), Map.class.getName());

    typeAliases.put(List.class.getName(), Collection.class.getName());
    typeAliases.put(Set.class.getName(), Collection.class.getName());
    
    typeAliases.put(Integer.class.getName(), Number.class.getName());
    typeAliases.put(Long.class.getName(), Number.class.getName());
    typeAliases.put(Double.class.getName(), Number.class.getName());
    typeAliases.put(Short.class.getName(), Number.class.getName());
    typeAliases.put(Float.class.getName(), Number.class.getName());
    typeAliases.put(BigDecimal.class.getName(), Number.class.getName());
    typeAliases.put(BigInteger.class.getName(), Number.class.getName());
  }

  private static Optional<String> getTypeAlias(final String valueType) {
    final Optional<String> mappedAlias;
    if (isNonPrimitiveArrayType(valueType)) {
      final int compTypeStart = valueType.indexOf('L') + 1;
      final int compTypeEndExclusive = valueType.length() - 1;
      final String componentType = valueType.substring(compTypeStart, compTypeEndExclusive);
      final String arrayPrefix = valueType.substring(0, compTypeStart);

      mappedAlias = Optional.ofNullable(getTypeAlias(componentType).map(type -> arrayPrefix + type + ";"))
              .filter(o -> o.isPresent())
              .orElseGet(() -> Optional.ofNullable(objArrayTypeWithReducedDimension(arrayPrefix)));
    }
    else {
      mappedAlias = Optional
                .ofNullable(Optional.ofNullable(typeAliases.get(valueType)))
                .filter(mapAlias -> mapAlias.isPresent())
                .orElseGet(() -> Optional.ofNullable(Object.class.getName()));
    }

    return mappedAlias.filter(alias -> !alias.equals(valueType));
  }

  private static String objArrayTypeWithReducedDimension(final String arrayPrefix) {
    if ("[L".equals(arrayPrefix)) {
      return Object.class.getName();
    }
    else {
      return arrayPrefix.substring(1) + Object.class.getName() + ";";
    }
  }

  private static boolean isNonPrimitiveArrayType(final String type) {
    return objArrayRegExp.test(type);
  }

  private final String constraint;
  private final String valueType;

  /**
   * @param constraint
   *          The fully-qualified class name of a {@link Constraint}.
   * @param valueType
   *          The fully-qualified class name of a validatable type.
   */
  public DynamicValidatorKey(final String constraint, final String valueType) {
    this.constraint = constraint;
    this.valueType = valueType;
  }

  /**
   * @return The fully-qualified class name of the {@link Constraint} for this key.
   */
  public String getConstraint() {
    return constraint;
  }

  /**
   * @return The fully-qualified class name of the validatable type for this key.
   */
  public String getValueType() {
    return valueType;
  }

  /**
   * @return True iff {@link #getAlias()} returns a non-empty {@link Optional}.
   */
  public boolean hasAlias() {
    return getAlias().isPresent();
  }

  /**
   * @return If present returns a {@link DynamicValidatorKey} for the same constraint as this one, but a different
   *         {@link #getValueType() value type} that is a super-type of the one for this key.
   */
  public Optional<DynamicValidatorKey> getAlias() {
    return getTypeAlias(valueType).map(alias -> new DynamicValidatorKey(constraint, alias));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((constraint == null) ? 0 : constraint.hashCode());
    result = prime * result + ((valueType == null) ? 0 : valueType.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final DynamicValidatorKey other = (DynamicValidatorKey) obj;
    if (constraint == null) {
      if (other.constraint != null)
        return false;
    }
    else if (!constraint.equals(other.constraint))
      return false;
    if (valueType == null) {
      if (other.valueType != null)
        return false;
    }
    else if (!valueType.equals(other.valueType))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return StringFormat.format("DynamicValidatorKey(constraint=%s,valueType=%s)", constraint, valueType);
  }

}
