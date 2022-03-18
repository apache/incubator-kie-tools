/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.codegen.test.meta.build;

import com.google.common.collect.Lists;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.impl.ClassBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.test.AbstractCodegenTest;
import org.jboss.errai.codegen.test.model.tree.Parent;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tests for {@link org.jboss.errai.codegen.meta.impl.build.BuildMetaClass}.
 *
 * Note:
 * This unit test is not based on {@link org.jboss.errai.codegen.test.meta.AbstractMetaClassTest} because
 * a {@link org.jboss.errai.codegen.meta.impl.build.BuildMetaClass} cannot be created from an existing
 * class.
 *
 * @author Johannes Barop <jb@barop.de>
 */
public class BuildMetaClassTest extends AbstractCodegenTest {

  @Test
  public void testGetFields() {
    final ClassStructureBuilder<?> classBuilder = ClassBuilder
        .define("Child", Parent.class)
        .publicScope()
        .body()
        .privateField("childPrivate", int.class).finish()
        .packageField("childPackage", int.class).finish()
        .protectedField("childProtected", int.class).finish()
        .publicField("childPublic", int.class).finish();
    final MetaClass child = classBuilder.getClassDefinition();
    final ArrayList<String> fields = new ArrayList<String>();
    for (MetaField field : child.getFields()) {
      fields.add(field.getDeclaringClass().getCanonicalName() + "." + field.getName());
    }

    final List<String> expectedFields = Lists.newLinkedList();
    expectedFields.add(child.getCanonicalName() + "." + "childPublic");
    expectedFields.add(Parent.class.getCanonicalName() + "." + "parentPublic");

    Collections.sort(fields);
    Collections.sort(expectedFields);

    assertEquals(expectedFields.toString(), fields.toString());
  }

  @Test
  public void testGetDeclaredFields() {
    final ClassStructureBuilder<?> classBuilder = ClassBuilder
        .define("Child", Parent.class)
        .publicScope()
        .body()
        .privateField("childPrivate", int.class).finish()
        .packageField("childPackage", int.class).finish()
        .protectedField("childProtected", int.class).finish()
        .publicField("childPublic", int.class).finish();
    final MetaClass child = classBuilder.getClassDefinition();
    final ArrayList<String> fields = new ArrayList<String>();
    for (MetaField field : child.getDeclaredFields()) {
      fields.add(field.getDeclaringClass().getCanonicalName() + "." + field.getName());
    }

    final List<String> expectedFields = Lists.newLinkedList();
    expectedFields.add(child.getCanonicalName() + "." + "childPrivate");
    expectedFields.add(child.getCanonicalName() + "." + "childPackage");
    expectedFields.add(child.getCanonicalName() + "." + "childProtected");
    expectedFields.add(child.getCanonicalName() + "." + "childPublic");

    Collections.sort(fields);
    Collections.sort(expectedFields);

    assertEquals(expectedFields.toString(), fields.toString());
  }
}
