/*
 * Copyright (c) 2005, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package jakarta.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The <code>Resource</code> annotation marks a resource that is needed by the application. This
 * annotation may be applied to an application component class, or to fields or methods of the
 * component class. When the annotation is applied to a field or method, the container will inject
 * an instance of the requested resource into the application component when the component is
 * initialized. If the annotation is applied to the component class, the annotation declares a
 * resource that the application will look up at runtime.
 *
 * <p>Even though this annotation is not marked <code>Inherited</code>, deployment tools are
 * required to examine all superclasses of any component class to discover all uses of this
 * annotation in all superclasses. All such annotation instances specify resources that are needed
 * by the application component. Note that this annotation may appear on private fields and methods
 * of superclasses; the container is required to perform injection in these cases as well.
 *
 * @since 1.6, Common Annotations 1.0
 */
@Target({TYPE, FIELD, METHOD})
@Retention(RUNTIME)
@Repeatable(Resources.class)
public @interface Resource {
  /**
   * The JNDI name of the resource. For field annotations, the default is the field name. For method
   * annotations, the default is the JavaBeans property name corresponding to the method. For class
   * annotations, there is no default and this must be specified.
   */
  String name() default "";

  /**
   * The name of the resource that the reference points to. It can link to any compatible resource
   * using the global JNDI names.
   *
   * @since 1.7, Common Annotations 1.1
   */
  String lookup() default "";

  /**
   * The Java type of the resource. For field annotations, the default is the type of the field. For
   * method annotations, the default is the type of the JavaBeans property. For class annotations,
   * there is no default and this must be specified.
   */
  Class<?> type() default java.lang.Object.class;

  /** The two possible authentication types for a resource. */
  enum AuthenticationType {
    CONTAINER,
    APPLICATION
  }

  /**
   * The authentication type to use for this resource. This may be specified for resources
   * representing a connection factory of any supported type, and must not be specified for
   * resources of other types.
   */
  AuthenticationType authenticationType() default AuthenticationType.CONTAINER;

  /**
   * Indicates whether this resource can be shared between this component and other components. This
   * may be specified for resources representing a connection factory of any supported type, and
   * must not be specified for resources of other types.
   */
  boolean shareable() default true;

  /**
   * A product-specific name that this resource should be mapped to. The <code>mappedName</code>
   * element provides for mapping the resource reference to the name of a resource known to the
   * applicaiton server. The mapped name could be of any form.
   *
   * <p>Application servers are not required to support any particular form or type of mapped name,
   * nor the ability to use mapped names. The mapped name is product-dependent and often
   * installation-dependent. No use of a mapped name is portable.
   */
  String mappedName() default "";

  /**
   * Description of this resource. The description is expected to be in the default language of the
   * system on which the application is deployed. The description can be presented to the Deployer
   * to help in choosing the correct resource.
   */
  String description() default "";
}
