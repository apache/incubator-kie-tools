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
package org.eclipse.emf.common.notify;



/**
 * A factory for creating adapters and associating them with notifiers.
 * An implementation may choose to associate one adapter with multiple notifiers.
 */
public interface AdapterFactory
{
  /**
   * Returns whether this factory supports adapters for the given type.
   * @param type the key indicating the type of adapter in question.
   * @return whether this factory supports adapters for the given type.
   * @see Adapter#isAdapterForType
   */
  boolean isFactoryForType(Object type);

  /**
   * Returns either an associated adapter for the object, or the object itself,
   * depending on whether the object is a notifier that supports an adapter of the given type.
   * This is essentially just a convenience method 
   * that allows a factory to act as a filter for converting objects to adapters.
   * If the type isn't supported or the specific object can't be adapted to it,
   * <code>null</code> will typically be returned.
   * @param object arbitrary object to adapt.
   * @param type the key indicating the type of adapter required.
   * @return either an associated adapter, the object itself, or <code>null</code> if the type isn't supported or the object cannot be adapted to it.
   */
  Object adapt(Object object, Object type);

  /**
   * Returns either a previously associated adapter or a newly associated adapter, as appropriate.
   * It will check if the right type of adapter is already associated with the target 
   * and will return it in that case;
   * otherwise, it will {@link #adaptNew create} a new adapter if possible.
   * @param target the notifier to adapt.
   * @param type the key indicating the type of adapter required.
   * @return a previously existing associated adapter, a new associated adapter if possible, or <code>null</code> otherwise.
   * @see Adapter#setTarget
   * @see #adaptNew
   */
  Adapter adapt(Notifier target, Object type);

  /**
   * Creates a new associated adapter of the given type;
   * it may optionally call {@link Adapter#setTarget setTarget} on the adapter, 
   * and it may optionally add the adapter to the {@link Notifier#eAdapters target.eAdapters()}. 
   * This is typically not called directly by clients.
   * @param target the notifier to adapt.
   * @param type the key indicating the type of adapter required.
   * @return a new associated adapter if possible, or <code>null</code> otherwise.
   * @see Adapter#setTarget
   * @see Notifier#eAdapters
   */
  Adapter adaptNew(Notifier target, Object type);

  /**
   * Creates a new associated adapter of each type of adapter supported by this factory, as necessary.
   * This is typically used to adapt newly created objects.
   * @param notifier notifier to adapt.
   * @see #adaptNew
   */
  void adaptAllNew(Notifier notifier);
}
