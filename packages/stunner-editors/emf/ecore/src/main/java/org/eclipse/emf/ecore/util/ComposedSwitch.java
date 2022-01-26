/**
 * Copyright (c) 2009 TIBCO Software Inc. and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Adrian Price
 */
package org.eclipse.emf.ecore.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;


/**
 * Composes multiple switches in order to handle instances of classes defined in multiple packages.
 * @since 2.7
 */
public class ComposedSwitch<T> extends Switch<T>
{
  private final Map<EPackage, Switch<T>> registry = new HashMap<EPackage, Switch<T>>();
  private final List<Switch<T>> switches = new ArrayList<Switch<T>>();

  /**
   * Creates a new ComposedSwitch.
   */
  public ComposedSwitch()
  {
    super();
  }

  /**
   * Creates a new ComposedSwitch.
   * @param switches The switches to which the composed switch will delegate. The list should generally include switches for all the
   * packages the composed switch will be expected to handle.
   */
  public ComposedSwitch(Collection<? extends Switch<T>> switches)
  {
    for (Switch<T> sw : switches)
    {
      addSwitch(sw);
    }
  }

  public void addSwitch(Switch<T> sw)
  {
    synchronized (switches)
    {
      if (!switches.contains(sw))
      {
        switches.add(sw);
      }
    }
  }

  public void removeSwitch(Switch<T> sw)
  {
    synchronized (switches)
    {
      if (switches.contains(sw))
      {
        switches.remove(sw);
        Iterator<Switch<T>> it = registry.values().iterator();
        while (it.hasNext())
        {
          if (it.next() == sw)
          {
            it.remove();
          }
        }
      }
    }
  }

  @Override
  protected T doSwitch(EClass theEClass, EObject theEObject)
  {
    Switch<T> delegate = findDelegate(theEClass.getEPackage());
    if (delegate == null)
    {
      List<EClass> eSuperTypes = theEClass.getESuperTypes();
      return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch(eSuperTypes.get(0), theEObject);
    }
    else
    {
      T result = delegatedDoSwitch(delegate, theEClass, theEObject);
      return result == null ? defaultCase(theEObject) : result;
    }
  }

  /**
   * Call delegate.{@link Switch#doSwitch(EClass, EObject) doSwitch}(theEClass, theEObject).
   * @since 2.8
   */
  protected T delegatedDoSwitch(Switch<T> delegate, EClass theEClass, EObject theEObject)
  {
    return delegate.doSwitch(theEClass, theEObject);
  }

  /**
   * Finds a suitable delegate for the given package.
   * @since 2.8
   */
  protected Switch<T> findDelegate(EPackage ePackage)
  {
    synchronized (switches)
    {
      Switch<T> delegate = registry.get(ePackage);
      if (delegate == null && !registry.containsKey(ePackage))
      {
        for (Switch<T> sw : switches)
        {
          if (sw.isSwitchFor(ePackage))
          {
            delegate = sw;
            break;
          }
        }
        registry.put(ePackage, delegate);
      }
      return delegate;
    }
  }

  @Override
  public boolean isSwitchFor(EPackage ePackage)
  {
    return findDelegate(ePackage) != null;
  }
}
