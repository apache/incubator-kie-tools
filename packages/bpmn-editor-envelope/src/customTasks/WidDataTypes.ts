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

/**
 * MVEL Data Type Converter
 * Converts MVEL types to their Java equivalents
 */
export class MvelDataType {
  private static readonly TYPE_MAPPINGS = new Map<string, string>([
    // Primitive types
    ["String", "String"],
    ["Integer", "Integer"],
    ["Boolean", "Boolean"],
    ["Float", "Float"],
    ["Double", "Double"],
    ["Long", "Long"],
    ["Short", "Short"],
    ["Byte", "Byte"],
    ["Character", "Character"],
    ["char", "Character"],
    ["int", "Integer"],
    ["boolean", "Boolean"],
    ["float", "Float"],
    ["double", "Double"],
    ["long", "Long"],
    ["short", "Short"],
    ["byte", "Byte"],

    // Object types
    ["Object", "Object"],
    ["List", "java.util.List"],
    ["ArrayList", "java.util.ArrayList"],
    ["LinkedList", "java.util.LinkedList"],
    ["Set", "java.util.Set"],
    ["HashSet", "java.util.HashSet"],
    ["TreeSet", "java.util.TreeSet"],
    ["Map", "java.util.Map"],
    ["HashMap", "java.util.HashMap"],
    ["TreeMap", "java.util.TreeMap"],
    ["Collection", "java.util.Collection"],

    // Date and time
    ["Date", "java.util.Date"],
    ["Calendar", "java.util.Calendar"],
    ["Timestamp", "java.sql.Timestamp"],
    ["Time", "java.sql.Time"],

    // IO types
    ["File", "java.io.File"],
    ["InputStream", "java.io.InputStream"],
    ["OutputStream", "java.io.OutputStream"],

    // Math types
    ["BigDecimal", "java.math.BigDecimal"],
    ["BigInteger", "java.math.BigInteger"],

    // Common application types
    ["StringBuffer", "StringBuffer"],
    ["StringBuilder", "StringBuilder"],
    ["Properties", "java.util.Properties"],

    // Null and void
    ["null", "null"],
    ["void", "void"],
    ["Void", "Void"],
  ]);

  /**
   * Converts MVEL type to Java type
   * @param mvelType - The MVEL type string
   * @returns The corresponding Java type string
   */
  public static getJavaTypeByMvelType(mvelType: string): string {
    if (!mvelType || mvelType.trim() === "") {
      return "Object";
    }

    const cleanType = mvelType.trim();

    // Check for exact match first
    if (this.TYPE_MAPPINGS.has(cleanType)) {
      return this.TYPE_MAPPINGS.get(cleanType)!;
    }

    // Check for array types
    if (cleanType.endsWith("[]")) {
      const baseType = cleanType.substring(0, cleanType.length - 2);
      const javaBaseType = this.getJavaTypeByMvelType(baseType);
      return javaBaseType + "[]";
    }

    // Check for generic types (e.g., List<String>)
    const genericMatch = cleanType.match(/^(\w+)<(.+)>$/);
    if (genericMatch) {
      const [, containerType, genericType] = genericMatch;
      const javaContainerType = this.getJavaTypeByMvelType(containerType);
      const javaGenericType = this.getJavaTypeByMvelType(genericType);
      return `${javaContainerType}<${javaGenericType}>`;
    }

    // If no mapping found, assume it's already a Java type or return as-is
    return cleanType;
  }

  /**
   * Checks if the given type is a primitive type
   * @param type - The type to check
   * @returns true if the type is primitive, false otherwise
   */
  public static isPrimitiveType(type: string): boolean {
    const primitives = ["int", "boolean", "float", "double", "long", "short", "byte", "char"];
    return primitives.includes(type.toLowerCase());
  }

  /**
   * Gets the wrapper class for a primitive type
   * @param primitiveType - The primitive type
   * @returns The wrapper class name
   */
  public static getWrapperClass(primitiveType: string): string {
    const wrappers = new Map<string, string>([
      ["int", "Integer"],
      ["boolean", "Boolean"],
      ["float", "Float"],
      ["double", "Double"],
      ["long", "Long"],
      ["short", "Short"],
      ["byte", "Byte"],
      ["char", "Character"],
    ]);

    return wrappers.get(primitiveType.toLowerCase()) || primitiveType;
  }

  /**
   * Gets all supported MVEL types
   * @returns Array of supported MVEL type names
   */
  public static getSupportedTypes(): string[] {
    return Array.from(this.TYPE_MAPPINGS.keys());
  }
}
