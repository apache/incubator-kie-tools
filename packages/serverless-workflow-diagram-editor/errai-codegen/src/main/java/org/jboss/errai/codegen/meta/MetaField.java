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


package org.jboss.errai.codegen.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.jboss.errai.codegen.util.GenUtil;

public abstract class MetaField extends AbstractHasAnnotations implements MetaClassMember {

  /**
   * Returns an actual MetaClass (a class, interface, primitive type, array, or
   * enum, but not a type variable or a wildcard) representing an <b>erased</b>
   * type that is assignable to this field.
   *
   * @see #getGenericType()
   * @return a MetaClass representing a type that is assignable to this field.
   *         Never null.
   */
  public abstract MetaClass getType();

  /**
   * Returns the actual unerased type of this field, which could be a MetaClass
   * (class, enum, interface, array, primitive, etc), a bounded or unbounded
   * type variable, or a wildcard. Unlike with {@link #getType()}, any type
   * parameters on the field's type will be preserved in the returned MetaType
   * object.
   *
   * @return The field type as declared. Never null.
   */
  public abstract MetaType getGenericType();

  /**
   * Returns this field's name without any type information or qualifiers.
   *
   * @return The field name. Never null.
   */
  @Override
  public abstract String getName();

  /**
   * Returns the annotations present on this field.
   *
   * @return A shared reference to the array of the annotations on this field.
   *         Returns an empty array (never null) if the field has no
   *         annotations. Callers should refrain from modifying the returned
   *         array.
   */
  @Override
  public abstract Annotation[] getAnnotations();

  /**
   * Returns a string which includes the declaring class's name and the field
   * type and field name, as well as all declared annotations for that field.
   * Do not rely on the format of this string remaining consistent across
   * releases of Errai.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(MetaField.class.getName()).append(":");
    sb.append(getDeclaringClassName()).append(".");

    Annotation[] annos = getAnnotations();
    if (annos != null) {
      for (Annotation anno : annos) {
        sb.append(anno.toString()).append(" ");
      }
    }

    sb.append(GenUtil.scopeOf(this).getCanonicalName()).append(" ")
    .append(GenUtil.modifiersOf(this).toJavaString()).append(" ")
    .append(this.getType()).append(" ").append(getName());

    return sb.toString();
  }

  private String _hashString;

  /**
   * Returns a string that uniquely identifies this field for purposes of
   * comparison with other implementations of {@link MetaField}. The returned
   * string includes the declaring class name, the field name, and the field's
   * type.
   *
   * @return
   */
  public String hashString() {
    if (_hashString != null) return _hashString;
    return _hashString = MetaField.class.getName()
            + ":" + getDeclaringClass().getFullyQualifiedName() + "." + getName() + "::" + getType().getFullyQualifiedName();
  }

  @Override
  public int hashCode() {
    return hashString().hashCode();
  }


  /**
   * Compares this MetaField with another MetaField. Differing implenentations
   * (for example, GWT vs Java Reflection) compare equal as long as they
   * represent the same field of the same class.
   */
  @Override
  public boolean equals(Object o) {
    return o instanceof MetaField && ((MetaField) o).hashString().equals(hashString());
  }

  /**
   * Returns the java.lang.reflect.Field object representing this MetaField.
   *
   * @return The Java Reflection Field object representing the same field as
   *         this MetaField. Never null.
   * @throws IllegalStateException
   *           if the field or its containing class cannot be located using Java
   *           Reflection.
   */
  public Field asField() {
    try {
      final Class<?> aClass = getDeclaringClass().asClass();
      return aClass.getDeclaredField(getName());
    }
    catch (Throwable e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Special-purpose implementation of MetaField that represents the
   * {@code length} property of an array.
   */
  public static class ArrayLengthMetaField extends MetaField implements HasAnnotations {

    private MetaClass componentType;

    public ArrayLengthMetaField(MetaClass componentType) {
      this.componentType = componentType;
    }

    @Override
    public MetaClass getType() {
      return MetaClassFactory.get(int.class);
    }

    @Override
    public MetaType getGenericType() {
      return null;
    }

    @Override
    public String getName() {
      return "length";
    }

    @Override
    public Annotation[] getAnnotations() {
      return new Annotation[0];
    }

    @Override
    public MetaClass getDeclaringClass() {
      return componentType;
    }

    @Override
    public String getDeclaringClassName() {
      return componentType.getFullyQualifiedName();
    }

    @Override
    public boolean isAbstract() {
      return false;
    }

    @Override
    public boolean isPublic() {
      return true;
    }

    @Override
    public boolean isPrivate() {
      return false;
    }

    @Override
    public boolean isProtected() {
      return false;
    }

    @Override
    public boolean isFinal() {
      return false;
    }

    @Override
    public boolean isStatic() {
      return false;
    }

    @Override
    public boolean isTransient() {
      return false;
    }

    @Override
    public boolean isSynthetic() {
      return false;
    }

    @Override
    public boolean isVolatile() {
      return false;
    }

    @Override
    public boolean isSynchronized() {
      return false;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
      return false;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotation) {
      return null;
    }
  }
}
