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


package org.jboss.errai.codegen;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import org.jboss.errai.codegen.builder.AnonymousClassStructureBuilder;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.impl.ObjectBuilder;
import org.jboss.errai.codegen.exception.CyclicalObjectGraphException;
import org.jboss.errai.codegen.exception.GenerationException;
import org.jboss.errai.codegen.exception.NotLiteralizableException;
import org.jboss.errai.codegen.literal.NullLiteral;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.util.Stmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.errai.codegen.util.PrettyPrinter.prettyPrintJava;

//import com.google.gwt.dev.util.collect.IdentityHashSet;

/**
 * Utility class for creating code-generated snapshots of certain types of live value objects.
 * The classes and interfaces that SnapshotMaker works with have the following characteristics:
 * <ul>
 *  <li>It must be an interface or a non-final class with a public no-args constructor.
 *  <li>None of the public methods take arguments (except {@code equals(Object)}, which is always ignored)
 *  <li>Each public method must be handled by the given MethodBodyCallback, or return one
 *      of the following values for which a snapshot can be generated automatically:
 *    <ul>
 *      <li>{@code void}
 *      <li>a Java primitive type
 *      <li>a {@link Context#addLiteralizableClass(Class) literalizable type} in the current code generator context
 *      <li>a type that is explicitly mentioned as a "type to recurse on" (these types must in turn follow this set of rules)
 *    </ul>
 *  </ul>
 *
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 * @author Mike Brock
 */
public final class SnapshotMaker {

  final static Logger logger = LoggerFactory.getLogger(SnapshotMaker.class);

  /**
   * Callback interface for providing custom method bodies in snapshots. There are three major use cases:
   * <ol>
   *  <li>To implement methods that take parameters (snapshots of
   *      these methods cannot be generated automatically)
   *  <li>To return a reference to some object that's already in the
   *      scope of the snapshot (such as a reference to a parent object
   *      from a getParent() method)
   *  <li>To implement additional methods in the case that the snapshot
   *      type is not the same as the type to extend.
   * </ol>
   *
   * @author Jonathan Fuerth <jfuerth@gmail.com>
   */
  public interface MethodBodyCallback {

    /**
     * Optionally returns the statement that should be used as the body of the
     * given method for the given object's snapshot. If the default snapshot
     * behaviour provided by SnapshotMaker is sufficient for the given method,
     * this callback can simply return null.
     *
     * @param method
     *          The method to provide the body for.
     * @param o
     *          The instance object that we are taking the snapshot of. You can
     *          use this reference if you need to invoke {@code method}.
     * @param containingClass
     *          The class that will contain the generated method. During the
     *          callback, you can generate additional methods and fields within
     *          this class if you like.
     * @return The Statement to use as the method body (must return a type
     *         compatible with {@code method}'s return type), or null if the
     *         snapshot maker should generate the method body by invoking method
     *         on {@code o} and returning a Literal of its value.
     */
    Statement generateMethodBody(MetaMethod method, Object o, ClassStructureBuilder<?> containingClass);

  }

  /** This class should not be instantiated. */
  private SnapshotMaker() {}

  /**
   * Code-generates an object whose methods return (snapshots of) the same
   * values as the given object.
   *
   * @param o
   *          The object to snapshot.
   * @param typeToSnapshot
   *          The type to read the snapshot attributes from. Must be a
   *          superclass of o or an interface implemented by o, and methods not
   *          supplied by {@code methodBodyCallback} must meed the requirements
   *          laid out in the class-level SnapshotMaker documentation.
   * @param typeToExtend
   *          The type of the snapshot to produce. Must be a subclass or
   *          subinterface of typeToSnapshot, and the additional methods present
   *          in typeToExtend vs. typeToSnapshot must be provided by the
   *          MethodMaker callback, since they can't be generated from o.
   * @param methodBodyCallback
   *          A callback that can provide method bodies, preventing the standard
   *          snapshot behaviour for those methods. This callback is optional;
   *          null is acceptable as "no callback."
   * @param typesToRecurseOn
   *          The types for which the snapshot maker should be applied
   *          recursively.
   * @return A Statement representing the value of the object
   * @throws CyclicalObjectGraphException
   *           if any objects reachable from {@code o} form a reference cycle.
   *           The simplest example of this would be a method on {@code o} that
   *           returns {@code o} itself. You may be able to work around such a
   *           problem by supplying a canned representation of one of the
   *           objects in the cycle.
   */
  public static Statement makeSnapshotAsSubclass(
      final Object o,
      final Class<?> typeToSnapshot,
      final Class<?> typeToExtend,
      final MethodBodyCallback methodBodyCallback,
      final Class<?> ... typesToRecurseOn) {
    final MetaClass metaTypeToSnapshot = MetaClassFactory.get(typeToSnapshot);
    final MetaClass metaTypeToExtend = MetaClassFactory.get(typeToExtend);
    final MetaClass[] metaTypesToRecurseOn = new MetaClass[typesToRecurseOn.length];
    for (int i = 0; i < typesToRecurseOn.length; i++) {
      metaTypesToRecurseOn[i] = MetaClassFactory.get(typesToRecurseOn[i]);
    }
    return makeSnapshotAsSubclass(o, metaTypeToSnapshot, metaTypeToExtend, methodBodyCallback, metaTypesToRecurseOn);
  }

  /**
   * Code-generates an object whose methods return (snapshots of) the same
   * values as the given object.
   *
   * @param o
   *          The object to snapshot.
   * @param typeToSnapshot
   *          The type to read the snapshot attributes from. Must be a
   *          superclass of o or an interface implemented by o, and methods not
   *          supplied by {@code methodBodyCallback} must meed the requirements
   *          laid out in the class-level SnapshotMaker documentation.
   * @param typeToExtend
   *          The type of the snapshot to produce. Must be a subclass or
   *          subinterface of typeToSnapshot, and the additional methods present
   *          in typeToExtend vs. typeToSnapshot must be provided by the
   *          MethodMaker callback, since they can't be generated from o.
   * @param methodBodyCallback
   *          A callback that can provide method bodies, preventing the standard
   *          snapshot behaviour for those methods. This callback is optional;
   *          null is acceptable as "no callback."
   * @param typesToRecurseOn
   *          The types for which the snapshot maker should be applied
   *          recursively.
   * @return A Statement representing the value of the object
   * @throws CyclicalObjectGraphException
   *           if any objects reachable from {@code o} form a reference cycle.
   *           The simplest example of this would be a method on {@code o} that
   *           returns {@code o} itself. You may be able to work around such a
   *           problem by supplying a canned representation of one of the objects
   *           in the cycle.
   */
  public static Statement makeSnapshotAsSubclass(
      final Object o,
      final MetaClass typeToSnapshot,
      final MetaClass typeToExtend,
      final MethodBodyCallback methodBodyCallback,
      final MetaClass ... typesToRecurseOn) {

    return makeSnapshotAsSubclass(
        o,
        typeToSnapshot,
        typeToExtend,
        methodBodyCallback,
        new HashSet<MetaClass>(Arrays.asList(typesToRecurseOn)),
        new IdentityHashMap<Object, Statement>(),
        Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>()));
  }

  /**
   * Implementation for the same-named public methods.
   *
   * @param o
   *          The object to snapshot.
   * @param typeToSnapshot
   *          The type to read the snapshot attributes from. Must be a
   *          superclass of o or an interface implemented by o, and methods not
   *          supplied by {@code methodBodyCallback} must meed the requirements
   *          laid out in the class-level SnapshotMaker documentation.
   * @param typeToExtend
   *          The type of the snapshot to produce. Must be a subclass or
   *          subinterface of typeToSnapshot, and the additional methods present
   *          in typeToExtend vs. typeToSnapshot must be provided by the
   *          MethodMaker callback, since they can't be generated from o.
   * @param typesToRecurseOn
   *          Types for which this method should be called recursively.
   * @param methodBodyCallback
   *          A callback that can provide method bodies, preventing the standard
   *          snapshot behaviour for those methods. This callback is optional;
   *          null is acceptable as "no callback."
   * @param existingSnapshots
   *          Object instances for which a snapshot has already been completed.
   *          Bootstrap this with an empty IdentityHashMap.
   * @param unfinishedSnapshots
   *          Object instances for which a partially-completed snapshot exists.
   *          If one of these objects is returned by a method in {@code o}, this
   *          causes a CyclicalObjectGraphException.
   * @return A Statement of type {@code typeToExtend} that represents the
   *         current publicly visible state of {@code o}.
   */
  private static Statement makeSnapshotAsSubclass(
      final Object o,
      final MetaClass typeToSnapshot,
      final MetaClass typeToExtend,
      final MethodBodyCallback methodBodyCallback,
      final Set<MetaClass> typesToRecurseOn,
      final IdentityHashMap<Object, Statement> existingSnapshots,
      final Set<Object> unfinishedSnapshots) {

    if (o == null) {
      return NullLiteral.INSTANCE;
    }

    if (!typeToSnapshot.isAssignableFrom(o.getClass())) {
      throw new IllegalArgumentException(
          "Given object (of type " + o.getClass().getName() +
              ") is not an instance of requested type to snapshot " + typeToSnapshot.getName());
    }

    if (logger.isDebugEnabled()) {
      logger.debug("** Making snapshot of " + o);
      logger.debug("   Existing snapshots: " + existingSnapshots);
    }

    final List<MetaMethod> sortedMethods = Arrays.asList(typeToExtend.getMethods());
    Collections.sort(sortedMethods, new Comparator<MetaMethod>() {
      @Override
      public int compare(MetaMethod m1, MetaMethod m2) {
        return m1.getName().compareTo(m2.getName());
      }
    });

    logger.debug("   Creating a new statement");
    return new Statement() {
      String generatedCache;

      /**
       * We retain a mapping of return values to the methods that returned them,
       * in case we need to provide diagnostic information when an exception is
       * thrown.
       */
      IdentityHashMap<Object, MetaMethod> methodReturnVals = new IdentityHashMap<Object, MetaMethod>();

      @Override
      public String generate(Context context) {
        if (logger.isDebugEnabled()) {
          logger.debug("++ Statement.generate() for " + o);
        }

        if (generatedCache != null) return generatedCache;

        // create a subcontext and record the types we will allow the LiteralFactory to create automatic
        // snapshots for.
        final Context subContext = Context.create(context);
        subContext.addLiteralizableMetaClasses(typesToRecurseOn);

        final AnonymousClassStructureBuilder builder = ObjectBuilder.newInstanceOf(typeToExtend.getErased(), context)
            .extend();
        unfinishedSnapshots.add(o);
        for (final MetaMethod method : sortedMethods) {
          if (method.isFinal() || method.getName().equals("toString")) continue;

          if (logger.isDebugEnabled()) {
            logger.debug("  method " + method.getName());
            logger.debug("    return type " + method.getReturnType());
          }

          if (methodBodyCallback != null) {
            final Statement providedMethod = methodBodyCallback.generateMethodBody(method, o, builder);
            if (providedMethod != null) {
              logger.debug("    body provided by callback");
              builder
                  .publicOverridesMethod(method.getName(), Parameter.of(method.getParameters()))
                  .append(providedMethod)
                  .finish();
              continue;
            }
          }

          if (method.getName().equals("equals") || method.getName().equals("hashCode")
              || method.getName().equals("clone") || method.getName().equals("finalize")) {
            // we skip these if not provided by the callback
            if (logger.isDebugEnabled()) {
              logger.debug("    skipping special-case method " + method.getName());
            }
            continue;
          }

          if (method.getParameters().length > 0) {
            throw new GenerationException("Method " + method + " in " + typeToSnapshot +
                    " takes parameters. Such methods must be handled by the MethodBodyCallback," +
                    " because they cannot be snapshotted.");
          }

          if (method.getReturnType().equals(MetaClassFactory.get(void.class))) {
            builder.publicOverridesMethod(method.getName()).finish();
            if (logger.isDebugEnabled()) {
              logger.debug("  finished method " + method.getName());
            }
            continue;
          }
          try {

            final Object retval = typeToExtend.asClass().getMethod(method.getName()).invoke(o);
            methodReturnVals.put(retval, method);

            if (logger.isDebugEnabled()) {
              logger.debug("    retval=" + retval);
            }

            Statement methodBody;
            if (existingSnapshots.containsKey(retval)) {
              logger.debug("    using existing snapshot");
              methodBody = existingSnapshots.get(retval);
            }
            else if (subContext.isLiteralizableClass(method.getReturnType().getErased())) {
              if (unfinishedSnapshots.contains(retval)) {
                throw new CyclicalObjectGraphException(unfinishedSnapshots);
              }

              // use Stmt.create(context) to pass the context along.
              if (logger.isDebugEnabled()) {
                logger.debug("    >> recursing for " + retval);
              }

              methodBody = Stmt.create(subContext).nestedCall(makeSnapshotAsSubclass(
                  retval, method.getReturnType(), method.getReturnType(),
                  methodBodyCallback, typesToRecurseOn, existingSnapshots, unfinishedSnapshots)).returnValue();
            }
            else {
              logger.debug("    relying on literal factory");
              methodBody = Stmt.load(retval).returnValue();
            }

            if (logger.isDebugEnabled()) {
              logger.debug("  finished method " + method.getName());
            }

            builder.publicOverridesMethod(method.getName()).append(methodBody).finish();
            existingSnapshots.put(retval, methodBody);
          }
          catch (final GenerationException e) {
            e.appendFailureInfo("In attempt to snapshot return value of "
                + typeToExtend.getFullyQualifiedName() + "." + method.getName() + "()");
            throw e;
          }
          catch (final RuntimeException e) {
            throw e;
          }
          catch (final Exception e) {
            throw new GenerationException("Failed to extract value for snapshot", e);
          }
        }

        if (logger.isDebugEnabled()) {
          logger.debug("    finished: " + builder);
        }

        try {
          generatedCache = prettyPrintJava(builder.finish().toJavaString());
        } catch (final NotLiteralizableException e) {
          final MetaMethod m = methodReturnVals.get(e.getNonLiteralizableObject());
          if (m != null) {
            e.appendFailureInfo("This value came from method " +
                  m.getDeclaringClass().getFullyQualifiedNameWithTypeParms() + "." + m.getName() +
                  ", which has return type " + m.getReturnType());
          }
          throw e;
        } catch (final GenerationException e) {
          e.appendFailureInfo("While generating a snapshot of " + o.toString() +
              " (actual type: " + o.getClass().getName() +
              "; type to extend: " + typeToExtend.getFullyQualifiedName() + ")");
          throw e;
        }
        unfinishedSnapshots.remove(o);
        return generatedCache;
      }

      @Override
      public MetaClass getType() {
        return typeToExtend;
      }
    };
  }

}
