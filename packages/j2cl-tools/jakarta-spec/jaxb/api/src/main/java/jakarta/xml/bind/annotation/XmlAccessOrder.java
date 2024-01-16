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
 * Used by XmlAccessorOrder to control the ordering of properties and fields in a Jakarta XML
 * Binding bound class.
 *
 * @author Sekhar Vajjhala, Sun Microsystems, Inc.
 * @since 1.6, JAXB 2.0
 * @see XmlAccessorOrder
 */
public enum XmlAccessOrder {
  /** The ordering of fields and properties in a class is undefined. */
  UNDEFINED,
  /**
   * The ordering of fields and properties in a class is in alphabetical order as determined by the
   * method java.lang.String.compareTo(String anotherString).
   */
  ALPHABETICAL
}
