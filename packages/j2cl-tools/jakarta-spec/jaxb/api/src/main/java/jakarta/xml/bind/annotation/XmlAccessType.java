/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.annotation;

/**
 * Used by XmlAccessorType to control serialization of fields or properties.
 *
 * @author Sekhar Vajjhala, Sun Microsystems, Inc.
 * @since 1.6, JAXB 2.0
 * @see XmlAccessorType
 */
public enum XmlAccessType {
  /**
   * Every getter/setter pair in a Jakarta XML Binding-bound class will be automatically bound to
   * XML, unless annotated by {@link XmlTransient}.
   *
   * <p>Fields are bound to XML only when they are explicitly annotated by some of the Jakarta XML
   * Binding annotations.
   */
  PROPERTY,
  /**
   * Every non static, non transient field in a Jakarta XML Binding-bound class will be
   * automatically bound to XML, unless annotated by {@link XmlTransient}.
   *
   * <p>Getter/setter pairs are bound to XML only when they are explicitly annotated by some of the
   * Jakarta XML Binding annotations.
   */
  FIELD,
  /**
   * Every public getter/setter pair and every public field will be automatically bound to XML,
   * unless annotated by {@link XmlTransient}.
   *
   * <p>Fields or getter/setter pairs that are private, protected, or defaulted to package-only
   * access are bound to XML only when they are explicitly annotated by the appropriate Jakarta XML
   * Binding annotations.
   */
  PUBLIC_MEMBER,
  /**
   * None of the fields or properties is bound to XML unless they are specifically annotated with
   * some of the Jakarta XML Binding annotations.
   */
  NONE
}
