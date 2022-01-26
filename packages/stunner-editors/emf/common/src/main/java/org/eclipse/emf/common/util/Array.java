/**
 * Copyright (c) 2010 Ed Merks and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Ed Merks - Initial API and implementation
 */
package org.eclipse.emf.common.util;

public final class Array
{
  public static Object newInstance(Class<?> componentType, int size)
  {
    Reflect.Helper helper = Reflect.HELPER_REGISTRY.get(componentType);
    return
      helper != null ?
        helper.newArrayInstance(size) :
        new Object[size];
  }
  
  public static int getLength(Object array)
  {
    if (array instanceof boolean[])
    {
      return ((boolean[])array).length;
    }
    else if (array instanceof byte[])
    {
      return ((byte[])array).length;
    }
    else if (array instanceof char[])
    {
      return ((char[])array).length;
    }
    else if (array instanceof double[])
    {
      return ((double[])array).length;
    }
    else if (array instanceof float[])
    {
      return ((float[])array).length;
    }
    else if (array instanceof int[])
    {
      return ((int[])array).length;
    }
    else if (array instanceof long[])
    {
      return ((long[])array).length;
    }
    else if (array instanceof short[])
    {
      return ((short[])array).length;
    }
    else
    {
      return ((Object[])array).length;
    }
  }
}
