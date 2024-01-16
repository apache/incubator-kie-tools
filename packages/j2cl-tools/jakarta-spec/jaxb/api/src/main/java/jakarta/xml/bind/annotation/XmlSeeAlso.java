/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Instructs Jakarta XML Binding to also bind other classes when binding this class.
 *
 * <p>Java makes it impractical/impossible to list all sub-classes of a given class. This often gets
 * in a way of Jakarta XML Binding users, as it Jakarta XML Binding cannot automatically list up the
 * classes that need to be known to {@link JAXBContext}.
 *
 * <p>For example, with the following class definitions:
 *
 * <pre>
 * class Animal {}
 * class Dog extends Animal {}
 * class Cat extends Animal {}
 * </pre>
 *
 * <p>The user would be required to create {@link JAXBContext} as {@code
 * JAXBContext.newInstance(Dog.class,Cat.class)} ({@code Animal} will be automatically picked up
 * since {@code Dog} and {@code Cat} refers to it.)
 *
 * <p>{@link XmlSeeAlso} annotation would allow you to write:
 *
 * <pre>
 * &#64;XmlSeeAlso({Dog.class,Cat.class})
 * class Animal {}
 * class Dog extends Animal {}
 * class Cat extends Animal {}
 * </pre>
 *
 * <p>This would allow you to do {@code JAXBContext.newInstance(Animal.class)}. By the help of this
 * annotation, Jakarta XML Binding implementations will be able to correctly bind {@code Dog} and
 * {@code Cat}.
 *
 * @author Kohsuke Kawaguchi
 * @since 1.6, JAXB 2.1
 */
@Target({ElementType.TYPE})
@Retention(RUNTIME)
public @interface XmlSeeAlso {
  Class<?>[] value();
}
