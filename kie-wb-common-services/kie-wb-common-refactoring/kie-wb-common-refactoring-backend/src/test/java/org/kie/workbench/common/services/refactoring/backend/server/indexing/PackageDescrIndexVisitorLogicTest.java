/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.refactoring.backend.server.indexing;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import javassist.Modifier;
import org.drools.compiler.compiler.ReturnValueDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.compiler.lang.descr.ConnectiveDescr;
import org.drools.compiler.lang.descr.OperatorDescr;
import org.drools.compiler.lang.descr.PatternSourceDescr;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.drools.compiler.lang.descr.RestrictionDescr;
import org.junit.Test;
import org.mvel2.asm.ClassReader;
import org.mvel2.asm.ClassVisitor;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Opcodes;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeElementsScanner;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * This test is focused on reflection-based code that inspects the {@link PackageDescrIndexVisitor} logic for inconsistencies.
 */
public class PackageDescrIndexVisitorLogicTest {

    private static final Logger logger = LoggerFactory.getLogger(PackageDescrIndexVisitorLogicTest.class);

    // @formatter:off
    private Reflections descrReflections = new Reflections(
            ClasspathHelper.forPackage(BaseDescr.class.getPackage().getName(), BaseDescr.class.getClassLoader()),
            new TypeElementsScanner(), new SubTypesScanner());
    // @formatter:on

    @Test
    public void visitLogicTest() {
        Set<Class> visitMethodArguments = getVisitMethodArguments();

        Set<Class<? extends BaseDescr>> descrClasses = descrReflections.getSubTypesOf(BaseDescr.class);

        descrClasses.remove(ReturnValueDescr.class); // used in jbpm-flow
        descrClasses.remove(ProcessDescr.class); // used in jbpm-flow

        descrClasses.remove(CompositePackageDescr.class); // treated as a PackageDescr

        descrClasses.remove(OperatorDescr.class); // no reference info
        descrClasses.remove(ConnectiveDescr.class); // not enough info to reliably reference a

        descrClasses.remove(PatternSourceDescr.class); // "abstract" class, even though it isn't
        descrClasses.remove(RestrictionDescr.class); // "abstract" class, even though it isn't

        String accDescrClassName = "org.drools.compiler.lang.descr.AccessorDescr";
        try {
            descrClasses.remove(Class.forName(accDescrClassName));
            // the AccessorDescr class is dead code..
            // we reference by name so this doesn't break when we remove the class
        } catch(Exception e) {
            // no-op
        }

        String restrictionClassName = "org.drools.compiler.lang.descr.Restriction";
        try {
            descrClasses.remove(Class.forName(restrictionClassName));
            // the Restriction class is dead code..
            // we reference by name so this doesn't break when we remove the class
        } catch(Exception e) {
            // no-op
        }

        Iterator<Class<? extends BaseDescr>> iter = descrClasses.iterator();
        while (iter.hasNext()) {
            Class descrImpl = iter.next();
            if (Modifier.isAbstract(descrImpl.getModifiers())
                    || descrImpl.isLocalClass()
                    || descrImpl.isMemberClass() ) {
                iter.remove();
            }
        }

        SetView<Class<? extends BaseDescr>> diff = Sets.difference(descrClasses, visitMethodArguments);
        TreeSet<Class> orderedDiff = new TreeSet<>(new ClassComparator());
        orderedDiff.addAll(diff);
        for (Class missingVisitMethodParam : orderedDiff) {
            logger.info("Create method for: " + missingVisitMethodParam.getSimpleName() );
        }
        if( ! orderedDiff.isEmpty() ) {
            fail( "visit(...) method missing for " + orderedDiff.iterator().next().getName());
        }
    }

    @Test
    public void visitMethodUseTest() throws IOException {
        List<Class> calledVisitMethodArgumentsInObjectVisitMethod = new ArrayList<>();

        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM5) {

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if (name.equals("visit") && desc.equals("(Ljava/lang/Object;)V")) {

                    MethodVisitor oriMv = new MethodVisitor(Opcodes.ASM5) {

                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                            if (name.equals("visit")) {
                                desc = desc.replaceAll(".*\\(L", "");
                                desc = desc.replaceAll(";\\).*", "");
                                desc = desc.replaceAll("/", ".");
                                Class argClass;
                                try {
                                    argClass = Class.forName(desc);
                                    calledVisitMethodArgumentsInObjectVisitMethod.add(argClass);
                                } catch (ClassNotFoundException e) {
                                    fail("Class not found: " + e.getMessage());
                                }
                            }
                        }

                    };
                    return oriMv;
                }
                return null;
            }

        };

        String classLoc = PackageDescrIndexVisitor.class.getSimpleName() + ".class";
        InputStream in = getClass().getResourceAsStream(classLoc);
        assertNotNull("Resource not found: " + classLoc, in);

        ClassReader classReader = new ClassReader(in);
        classReader.accept(visitor, 0);

        Set<Class> visitMethodArguments = getVisitMethodArguments();
        Class firstErrorClass = null;
        boolean fail = false;
        for (Class visitMethodArgClass : visitMethodArguments) {
            if( ! calledVisitMethodArgumentsInObjectVisitMethod.contains(visitMethodArgClass) ) {
                if( firstErrorClass == null ) {
                    firstErrorClass = visitMethodArgClass;
                    fail = true;
                }
                System.out.println( visitMethodArgClass.getSimpleName() + " visit method not called in visit(Object) !" );
            }
        }
        if( fail ) {
            assertFalse( firstErrorClass.getSimpleName() + " visit method not called in visit(Object) !",
                    fail );
        }

        for( Class calledMethodArgClass : calledVisitMethodArgumentsInObjectVisitMethod ) {
            assertTrue("Infinite loop detected!", visitMethodArguments.contains(calledMethodArgClass));
        }
    }

    private Set<Class> getVisitMethodArguments() {
        Method[] visitorMethods = PackageDescrIndexVisitor.class.getDeclaredMethods();

        Set<Class> visitMethodArguments = new TreeSet<>(new ClassComparator());
        for (Method method : visitorMethods) {
            if (method.getName().equals("visit") && method.getParameterCount() == 1) {
                Class paramType = method.getParameterTypes()[0];
                if (BaseDescr.class.isAssignableFrom(paramType)
                        && !paramType.isLocalClass()
                        && !Modifier.isAbstract(paramType.getModifiers())) {
                    visitMethodArguments.add(paramType);
                }
            }
        }

        visitMethodArguments.remove(Object.class);
        return visitMethodArguments;
    }

    private class ClassComparator implements Comparator<Class> {

        @Override
        public int compare(Class o1, Class o2) {
            if (o1 == o2) {
                return 0;
            }
            if (o2 == null) {
                return 1;
            } else if (o1 == null) {
                return -1;
            } else {
                return o1.getName().compareTo(o2.getName());
            }
        }
    }

}