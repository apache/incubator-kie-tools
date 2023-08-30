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


package org.jboss.errai.codegen.test;

import java.util.Arrays;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.SnapshotMaker;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.impl.ClassBuilder;
import org.jboss.errai.codegen.exception.CyclicalObjectGraphException;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.test.model.Person;
import org.jboss.errai.codegen.test.model.PersonImpl;
import org.jboss.errai.codegen.test.model.SnapshotInterfaceWithCollections;
import org.jboss.errai.codegen.test.model.SnapshotInterfaceWithCollectionsImpl;
import org.jboss.errai.codegen.util.Stmt;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

// Note: extends from AbstractCodeGenTest to inherit overridden assertEquals(String, String) methods which are whitespace
//       insensitive.
public class SnapshotMakerTest extends AbstractCodegenTest {

  @Test
  public void testGenerateSnapshotOfMethod() throws Exception {
    Person mother = new PersonImpl("mom", 30, null);
    Person child = new PersonImpl("kid", 5, mother);
    Statement snapshotStmt = SnapshotMaker.makeSnapshotAsSubclass(child, Person.class, Person.class, null, Person.class);

    final String generated
            = snapshotStmt.generate(Context.create());

    final String expectedValue =
            "new org.jboss.errai.codegen.test.model.Person() {\n" +
                    "  public int getAge() {\n" +
                    "    return 5;\n" +
                    "  }\n" +
                    "  public org.jboss.errai.codegen.test.model.Person getMother() {\n" +
                    "    return new org.jboss.errai.codegen.test.model.Person() {\n" +
                    "      public int getAge() {\n" +
                    "        return 30;\n" +
                    "      }\n" +
                    "      public org.jboss.errai.codegen.test.model.Person getMother() {\n" +
                    "        return null;\n" +
                    "      }\n" +
                    "      public String getName() {\n" +
                    "        return \"mom\";\n" +
                    "      }\n" +
                    "    };\n" +
                    "  }\n" +
                    "  public String getName() {\n" +
                    "    return \"kid\";\n" +
                    "  }\n" +
                    "}";

    assertEquals(expectedValue, generated);
  }

  @Test
  public void testNoStackOverflowOnObjectCycle() {
    PersonImpl cycle1 = new PersonImpl("cycle1", 30, null);
    Person cycle2 = new PersonImpl("cycle2", 5, cycle1);
    cycle1.setMother(cycle2);

    try {
      Statement snapshotStmt = SnapshotMaker.makeSnapshotAsSubclass(cycle2, Person.class, Person.class, null, Person.class);
      snapshotStmt.generate(Context.create());
      Assert.fail("Instance cycle was not detected");
    }
    catch (CyclicalObjectGraphException e) {
      assertTrue(e.getObjectsInvolvedInCycle().contains(cycle1));
      assertTrue(e.getObjectsInvolvedInCycle().contains(cycle2));
    }
  }

  /**
   * This test confirms the SnapshotMaker is called automatically by the code generator's literal reification logic
   * for interfaces and classes explicitly market "literalizable" in the context.
   *
   * @throws Exception
   */
  @Test
  public void testCollectionsLiteralizableInSnapshots() throws Exception {
    Person jonathan = new PersonImpl("Jonathan F.", 20, new PersonImpl("Jonathans's Parent", 50, null));
    Person christian = new PersonImpl("Christian S.", 20, new PersonImpl("Christians's Parent", 50, null));
    Person mike = new PersonImpl("Mike B.", 0, new PersonImpl("Mike's Parent", 50, null));

    SnapshotInterfaceWithCollections personCollection
            = new SnapshotInterfaceWithCollectionsImpl(Arrays.asList(jonathan, christian, mike));

    // create a manual context
    final Context ctx = Context.create();

    // tell the code generator that classes that implement these interfaces are literalizable
    ctx.addLiteralizableClass(SnapshotInterfaceWithCollections.class);
    ctx.addLiteralizableClass(Person.class);

    // initialize a new builder with this context and try and build 'snapshot' as a code snapshot
    final String generated = Stmt.create(ctx).load(personCollection).toJavaString();

    final String expectedValue =
            "new org.jboss.errai.codegen.test.model.SnapshotInterfaceWithCollections() {\n" +
                    "  public java.util.List getPersons() {\n" +
                    "    return new java.util.ArrayList() {\n" +
                    "      {\n" +
                    "        add(new org.jboss.errai.codegen.test.model.Person() {\n" +
                    "          public int getAge() {\n" +
                    "            return 20;\n" +
                    "          }\n" +
                    "          public org.jboss.errai.codegen.test.model.Person getMother() {\n" +
                    "            return new org.jboss.errai.codegen.test.model.Person() {\n" +
                    "              public int getAge() {\n" +
                    "                return 50;\n" +
                    "              }\n" +
                    "              public org.jboss.errai.codegen.test.model.Person getMother() {\n" +
                    "                return null;\n" +
                    "              }\n" +
                    "              public String getName() {\n" +
                    "                return \"Jonathans's Parent\";\n" +
                    "              }\n" +
                    "            };\n" +
                    "          }\n" +
                    "          public String getName() {\n" +
                    "            return \"Jonathan F.\";\n" +
                    "          }\n" +
                    "        });\n" +
                    "        add(new org.jboss.errai.codegen.test.model.Person() {\n" +
                    "          public int getAge() {\n" +
                    "            return 20;\n" +
                    "          }\n" +
                    "          public org.jboss.errai.codegen.test.model.Person getMother() {\n" +
                    "            return new org.jboss.errai.codegen.test.model.Person() {\n" +
                    "              public int getAge() {\n" +
                    "                return 50;\n" +
                    "              }\n" +
                    "              public org.jboss.errai.codegen.test.model.Person getMother() {\n" +
                    "                return null;\n" +
                    "              }\n" +
                    "              public String getName() {\n" +
                    "                return \"Christians's Parent\";\n" +
                    "              }\n" +
                    "            };\n" +
                    "          }\n" +
                    "          public String getName() {\n" +
                    "            return \"Christian S.\";\n" +
                    "          }\n" +
                    "        });\n" +
                    "        add(new org.jboss.errai.codegen.test.model.Person() {\n" +
                    "          public int getAge() {\n" +
                    "            return 0;\n" +
                    "          }\n" +
                    "          public org.jboss.errai.codegen.test.model.Person getMother() {\n" +
                    "            return new org.jboss.errai.codegen.test.model.Person() {\n" +
                    "              public int getAge() {\n" +
                    "                return 50;\n" +
                    "              }\n" +
                    "              public org.jboss.errai.codegen.test.model.Person getMother() {\n" +
                    "                return null;\n" +
                    "              }\n" +
                    "              public String getName() {\n" +
                    "                return \"Mike's Parent\";\n" +
                    "              }\n" +
                    "            };\n" +
                    "          }\n" +
                    "          public String getName() {\n" +
                    "            return \"Mike B.\";\n" +
                    "          }\n" +
                    "        });\n" +
                    "      }\n" +
                    "    };\n" +
                    "  }\n" +
                    "}\n";

    assertEquals(expectedValue, generated);
  }

  @Test
  public void testMethodBodyCallback() {
    Person mom = new PersonImpl("mom", 30, null);
    Person kid1 = new PersonImpl("Kid 1", 3, mom);
    Person kid2 = new PersonImpl("Kid 2", 4, mom);
    Person kid3 = new PersonImpl("Kid 3", 5, mom);

    ClassStructureBuilder<?> peopleClassBuilder = ClassBuilder.define("com.foo.People").publicScope().body();
    BlockBuilder<?> method = peopleClassBuilder.publicMethod(void.class, "makePeople").body();

    Statement momVar = Stmt
        .declareVariable(Person.class)
        .asFinal()
        .named("mom")
        .initializeWith(SnapshotMaker.makeSnapshotAsSubclass(mom, Person.class, Person.class, null, Person.class));
    method.append(momVar);

    SnapshotMaker.MethodBodyCallback mbcb = new SnapshotMaker.MethodBodyCallback() {
      @Override
      public Statement generateMethodBody(MetaMethod method, Object o, ClassStructureBuilder<?> containingClass) {
        if (method.getName().equals("getMother")) {
          return Stmt.loadVariable("mom").returnValue();
        }
        return null;
      }
    };

    method.append(Stmt.declareVariable("kid1", SnapshotMaker.makeSnapshotAsSubclass(kid1, Person.class, Person.class, mbcb, Person.class)));
    method.append(Stmt.declareVariable("kid2", SnapshotMaker.makeSnapshotAsSubclass(kid2, Person.class, Person.class, mbcb, Person.class)));
    method.append(Stmt.declareVariable("kid3", SnapshotMaker.makeSnapshotAsSubclass(kid3, Person.class, Person.class, mbcb, Person.class)));

    method.finish();

    final String generated = peopleClassBuilder.toJavaString();

    String expected = "package com.foo;\n" +
            "\n" +
            "import org.jboss.errai.codegen.test.model.Person;\n" +
            "\n" +
            "public class People {\n" +
            "  public void makePeople() {\n" +
            "    final Person mom = new Person() {\n" +
            "      public int getAge() {\n" +
            "        return 30;\n" +
            "      }\n" +
            "      public Person getMother() {\n" +
            "        return null;\n" +
            "      }\n" +
            "      public String getName() {\n" +
            "        return \"mom\";\n" +
            "      }\n" +
            "    };\n" +
            "    Person kid1 = new Person() {\n" +
            "      public int getAge() {\n" +
            "        return 3;\n" +
            "      }\n" +
            "      public Person getMother() {\n" +
            "        return mom;\n" +
            "      }\n" +
            "      public String getName() {\n" +
            "        return \"Kid 1\";\n" +
            "      }\n" +
            "    };\n" +
            "    Person kid2 = new Person() {\n" +
            "      public int getAge() {\n" +
            "        return 4;\n" +
            "      }\n" +
            "      public Person getMother() {\n" +
            "        return mom;\n" +
            "      }\n" +
            "      public String getName() {\n" +
            "        return \"Kid 2\";\n" +
            "      }\n" +
            "    };\n" +
            "    Person kid3 = new Person() {\n" +
            "      public int getAge() {\n" +
            "        return 5;\n" +
            "      }\n" +
            "      public Person getMother() {\n" +
            "        return mom;\n" +
            "      }\n" +
            "      public String getName() {\n" +
            "        return \"Kid 3\";\n" +
            "      }\n" +
            "    };\n" +
            "  }\n" +
            "}\n";
    assertEquals(expected, generated);
  }
}
