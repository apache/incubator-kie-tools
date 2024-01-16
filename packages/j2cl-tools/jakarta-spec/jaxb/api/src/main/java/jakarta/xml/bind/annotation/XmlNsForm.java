/*
 * Copyright (c) 2004, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.annotation;

/**
 * Enumeration of XML Schema namespace qualifications.
 *
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for additional common
 * information.
 *
 * <p><b>Usage</b>
 *
 * <p>The namespace qualification values are used in the annotations defined in this packge. The
 * enumeration values are mapped as follows:
 *
 * <table class="striped">
 *   <caption style="display:none">Mapping of enumeration values</caption>
 *   <thead>
 *     <tr>
 *       <th scope="col">Enum Value</th>
 *       <th scope="col">XML Schema Value</th>
 *     </tr>
 *   </thead>
 *
 *   <tbody>
 *     <tr>
 *       <th scope="row">UNQUALIFIED</th>
 *       <td>unqualified</td>
 *     </tr>
 *     <tr>
 *       <th scope="row">QUALIFIED</th>
 *       <td>qualified</td>
 *     </tr>
 *     <tr>
 *       <th scope="row">UNSET</th>
 *       <td>namespace qualification attribute is absent from the
 *           XML Schema fragment</td>
 *     </tr>
 *   </tbody>
 * </table>
 *
 * @author Sekhar Vajjhala, Sun Microsystems, Inc.
 * @since 1.6, JAXB 2.0
 */
public enum XmlNsForm {
  UNQUALIFIED,
  QUALIFIED,
  UNSET
}
