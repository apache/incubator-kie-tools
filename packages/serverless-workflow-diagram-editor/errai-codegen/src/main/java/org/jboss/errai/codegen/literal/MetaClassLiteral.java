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
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;

import static org.jboss.errai.codegen.builder.callstack.LoadClassReference.getClassReference;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class MetaClassLiteral extends LiteralValue<MetaClass> implements TypeLiteral {

  public MetaClassLiteral(final MetaClass value) {
    super(value);
  }

  @Override
  public String generate(final Context context) {
    return getCanonicalString(context);
  }

  public String getCanonicalString(final Context context) {
    if (context == null) {
      return getValue().getFullyQualifiedName() + ".class";
    }
    else {
      return getClassReference(getValue(), context, false) + ".class";
    }
  }

  public MetaClass getType() {
    return MetaClassFactory.get(Class.class);
  }

  public MetaClass getActualType() {
    return getValue();
  }
}
