/**
 * Copyright (c) 2002-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.common.util;


/**
 * An extensible enumerator implementation.
 */
public abstract class AbstractEnumerator implements Enumerator
{
  /**
   * The name of the enumerator.
   */
  private final String name;

  /**
   * The <code>int</code> value of the enumerator.
   */
  private final int value;
  
  /**
   * The literal value of the enumerator.
   */
  private final String literal;

  /**
   * Creates an initialized instance.
   * @param value the <code>int</code> value of the enumerator.
   * @param name the name of the enumerator, which is also used as the literal value.
   */
  protected AbstractEnumerator(int value, String name)
  {
    this.name = literal = name;
    this.value = value;
  }
  
  /**
   * Creates an initialized instance.
   * @param value the <code>int</code> value of the enumerator.
   * @param name the name of the enumerator.
   * @param literal the literal value of the enumerator.
   */
  protected AbstractEnumerator(int value, String name, String literal)
  {
    this.name = name;
    this.value = value;
    this.literal = literal;
  }

  /**
   * Returns the name of the enumerator.
   * @return the name.
   */
  public final String getName()
  {
    return name;
  }

  /**
   * Returns the <code>int</code> value of the enumerator.
   * @return the value.
   */
  public final int getValue()
  {
    return value;
  }
  
  /**
   * Returns the literal value of the enumerator.
   * @return the literal.
   */
  public final String getLiteral()
  {
    return literal;
  }

  /**
   * Returns the literal value of the enumerator, which is its string representation.
   * @return the literal.
   */
  @Override
  public final String toString()
  {
    return literal;
  }
}
