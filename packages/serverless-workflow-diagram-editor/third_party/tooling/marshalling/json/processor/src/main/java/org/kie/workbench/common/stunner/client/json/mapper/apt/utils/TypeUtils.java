/*
 * Copyright © 2022 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.json.mapper.apt.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.github.javaparser.ast.CompilationUnit;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.stunner.client.json.mapper.apt.context.GenerationContext;
import org.kie.workbench.common.stunner.client.json.mapper.apt.exception.GenerationException;

public class TypeUtils {

  private final Types types;
  private final Elements elements;

  private final BoxedTypes boxedTypes;

  private final GenerationContext context;

  public TypeUtils(GenerationContext context) {
    this.types = context.getProcessingEnv().getTypeUtils();
    this.elements = context.getProcessingEnv().getElementUtils();
    this.boxedTypes = new BoxedTypes();
    this.context = context;
  }

  public BoxedTypes getBoxedTypes() {
    return boxedTypes;
  }

  public Collection<VariableElement> getAllFieldsIn(TypeElement type) {
    Map<String, VariableElement> fields = new LinkedHashMap<>();
    ElementFilter.fieldsIn(type.getEnclosedElements())
        .forEach(field -> fields.put(field.getSimpleName().toString(), field));

    List<TypeElement> alltypes = getSuperTypes(elements, type);
    for (TypeElement atype : alltypes) {
      ElementFilter.fieldsIn(atype.getEnclosedElements()).stream()
          .filter(field -> !fields.containsKey(field.getSimpleName().toString()))
          .forEach(field -> fields.put(field.getSimpleName().toString(), field));
    }
    return fields.values();
  }

  /**
   * see: typetools/checker-framework Determine all type elements for the classes and interfaces
   * referenced in the extends/implements clauses of the given type element. TODO: can we learn from
   * the implementation of com.sun.tools.javac.model.JavacElements.getAllMembers(TypeElement)?
   */
  public List<TypeElement> getSuperTypes(Elements elements, TypeElement type) {

    List<TypeElement> superelems = new ArrayList<>();
    if (type == null) {
      return superelems;
    }

    // Set up a stack containing type, which is our starting point.
    Deque<TypeElement> stack = new ArrayDeque<>();
    stack.push(type);

    while (!stack.isEmpty()) {
      TypeElement current = stack.pop();

      // For each direct supertype of the current type element, if it
      // hasn't already been visited, push it onto the stack and
      // add it to our superelems set.
      TypeMirror supertypecls = current.getSuperclass();
      if (supertypecls.getKind() != TypeKind.NONE) {
        TypeElement supercls = (TypeElement) ((DeclaredType) supertypecls).asElement();
        if (!superelems.contains(supercls)) {
          stack.push(supercls);
          superelems.add(supercls);
        }
      }
      for (TypeMirror supertypeitf : current.getInterfaces()) {
        TypeElement superitf = (TypeElement) ((DeclaredType) supertypeitf).asElement();
        if (!superelems.contains(superitf)) {
          stack.push(superitf);
          superelems.add(superitf);
        }
      }
    }

    // Include java.lang.Object as implicit superclass for all classes and interfaces.
    TypeElement jlobject = elements.getTypeElement(Object.class.getCanonicalName());
    if (!superelems.contains(jlobject)) {
      superelems.add(jlobject);
    }

    return Collections.unmodifiableList(superelems);
  }

  public boolean hasGetter(VariableElement variable) {
    return getGetter(variable) != null;
  }

  public ExecutableElement getGetter(VariableElement variable) {
    List<String> method = compileGetterMethodName(variable);
    return MoreElements.asType(variable.getEnclosingElement()).getEnclosedElements().stream()
        .filter(e -> e.getKind().equals(ElementKind.METHOD))
        .filter(e -> method.contains(e.toString()))
        .filter(e -> !e.getModifiers().contains(Modifier.PRIVATE))
        .filter(e -> !e.getModifiers().contains(Modifier.STATIC))
        .map(MoreElements::asExecutable)
        .filter(elm -> elm.getParameters().isEmpty())
        .filter(elm -> types.isSameType(elm.getReturnType(), variable.asType()))
        .findFirst()
        .orElseThrow(
            () ->
                new GenerationException(
                    String.format(
                        "Unable to find suitable getter for %s.%s",
                        variable.getEnclosingElement(), variable.getSimpleName())));
  }

  public List<String> compileGetterMethodName(VariableElement variable) {
    String varName = variable.getSimpleName().toString();
    boolean isBoolean = isBoolean(variable);
    List<String> result = new ArrayList<>();
    result.add("get" + StringUtils.capitalize(varName) + "()");
    if (isBoolean) {
      result.add("is" + StringUtils.capitalize(varName) + "()");
    }
    return result;
  }

  public boolean hasSetter(VariableElement variable) {
    return getSetter(variable) != null;
  }

  public ExecutableElement getSetter(VariableElement variable) {
    String method = compileSetterMethodName(variable);
    return MoreElements.asType(variable.getEnclosingElement()).getEnclosedElements().stream()
        .filter(e -> e.getKind().equals(ElementKind.METHOD))
        .filter(e -> e.toString().equals(method))
        .filter(e -> !e.getModifiers().contains(Modifier.PRIVATE))
        .filter(e -> !e.getModifiers().contains(Modifier.STATIC))
        .map(MoreElements::asExecutable)
        .filter(elm -> elm.getParameters().size() == 1)
        .filter(elm -> types.isSameType(elm.getParameters().get(0).asType(), variable.asType()))
        .findFirst()
        .orElseThrow(
            () ->
                new GenerationException(
                    String.format(
                        "Unable to find suitable setter for %s.%s",
                        variable.getEnclosingElement(), variable.getSimpleName())));
  }

  private String compileSetterMethodName(VariableElement variable) {
    String varName = variable.getSimpleName().toString();
    StringBuffer sb = new StringBuffer();
    sb.append("set");
    sb.append(StringUtils.capitalize(varName));
    sb.append("(");
    sb.append(variable.asType());
    sb.append(")");
    return sb.toString();
  }

  public boolean isBoxedType(TypeMirror type) {
    return boxedTypes.isBoxedType(type);
  }

  public boolean isBoxedTypeOrString(TypeMirror type) {
    return boxedTypes.isBoxedType(type) || boxedTypes.isString(type);
  }

  public boolean isBoolean(VariableElement variable) {
    return variable.asType().getKind().equals(TypeKind.BOOLEAN)
        || variable.asType().toString().equals(Boolean.class.getCanonicalName());
  }

  public boolean isAssignableFrom(TypeMirror typeMirror, Class<?> targetClass) {
    return types.isAssignable(
        typeMirror, types.getDeclaredType(elements.getTypeElement(targetClass.getCanonicalName())));
  }

  public boolean isSimpleType(TypeMirror property) {
    return property.getKind().isPrimitive() || isBoxedTypeOrString(property);
  }

  private static final String BEAN_JSON_SERIALIZER_IMPL = "_JsonSerializerImpl";

  private static final String BEAN_JSON_DESERIALIZER_IMPL = "_JsonDeserializerImpl";

  public String getJsonSerializerImplQualifiedName(VariableElement variable) {
    return getJsonSerializerImplQualifiedName(MoreTypes.asTypeElement(variable.asType()));
  }

  public String getJsonSerializerImplQualifiedName(TypeElement type) {
    return elements.getPackageOf(type) + "." + getJsonSerializerImplName(type);
  }

  public String getJsonSerializerImplName(TypeElement type) {
    return (type.getEnclosingElement().getKind().equals(ElementKind.PACKAGE)
            ? ""
            : MoreElements.asType(type.getEnclosingElement()).getSimpleName().toString() + "_")
        + type.getSimpleName()
        + BEAN_JSON_SERIALIZER_IMPL;
  }

  public String getJsonDeserializerImplQualifiedName(TypeElement type, CompilationUnit cu) {
    return elements.getPackageOf(type) + "." + getJsonDeserializerImplName(type);
  }

  public String getJsonDeserializerImplName(TypeElement type) {
    return (type.getEnclosingElement().getKind().equals(ElementKind.PACKAGE)
            ? ""
            : MoreElements.asType(type.getEnclosingElement()).getSimpleName().toString() + "_")
        + type.getSimpleName()
        + BEAN_JSON_DESERIALIZER_IMPL;
  }

  public boolean isIterable(TypeMirror property) {
    return !property.getKind().isPrimitive() && isAssignableFrom(property, Iterable.class);
  }

  public class BoxedTypes {

    private Set<TypeMirror> boxedTypes = new HashSet<>();

    private BoxedTypes() {

      boxedTypes.add(asBoxedType(TypeKind.BOOLEAN));
      boxedTypes.add(asBoxedType(TypeKind.BYTE));
      boxedTypes.add(asBoxedType(TypeKind.CHAR));
      boxedTypes.add(asBoxedType(TypeKind.DOUBLE));
      boxedTypes.add(asBoxedType(TypeKind.FLOAT));
      boxedTypes.add(asBoxedType(TypeKind.INT));
      boxedTypes.add(asBoxedType(TypeKind.LONG));
      boxedTypes.add(asBoxedType(TypeKind.SHORT));
    }

    boolean isBoxedType(TypeMirror type) {
      return boxedTypes.stream().filter(t -> types.isSameType(t, type)).findAny().isPresent();
    }

    public boolean isBoolean(TypeMirror type) {
      if (type.getKind().equals(TypeKind.BOOLEAN)) {
        return true;
      }
      return types.isSameType(asBoxedType(TypeKind.BOOLEAN), type);
    }

    public boolean isByte(TypeMirror type) {
      if (type.getKind().equals(TypeKind.BYTE)) {
        return true;
      }
      return types.isSameType(asBoxedType(TypeKind.BYTE), type);
    }

    public boolean isChar(TypeMirror type) {
      if (type.getKind().equals(TypeKind.CHAR)) {
        return true;
      }
      return types.isSameType(asBoxedType(TypeKind.CHAR), type);
    }

    public boolean isDouble(TypeMirror type) {
      if (type.getKind().equals(TypeKind.DOUBLE)) {
        return true;
      }
      return types.isSameType(asBoxedType(TypeKind.DOUBLE), type);
    }

    public boolean isFloat(TypeMirror type) {
      if (type.getKind().equals(TypeKind.FLOAT)) {
        return true;
      }
      return types.isSameType(asBoxedType(TypeKind.FLOAT), type);
    }

    public boolean isInt(TypeMirror type) {
      if (type.getKind().equals(TypeKind.INT)) {
        return true;
      }
      return types.isSameType(asBoxedType(TypeKind.INT), type);
    }

    public boolean isLong(TypeMirror type) {
      if (type.getKind().equals(TypeKind.LONG)) {
        return true;
      }
      return types.isSameType(asBoxedType(TypeKind.LONG), type);
    }

    public boolean isShort(TypeMirror type) {
      if (type.getKind().equals(TypeKind.SHORT)) {
        return true;
      }
      return types.isSameType(asBoxedType(TypeKind.SHORT), type);
    }

    public boolean isString(TypeMirror type) {
      TypeMirror string = elements.getTypeElement(String.class.getCanonicalName()).asType();
      return types.isSameType(type, string);
    }

    private TypeMirror asBoxedType(TypeKind type) {
      PrimitiveType primitive = types.getPrimitiveType(type);
      return types.boxedClass(primitive).asType();
    }
  }
}
