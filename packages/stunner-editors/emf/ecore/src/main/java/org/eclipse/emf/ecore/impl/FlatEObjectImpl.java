/**
 * Copyright (c) 2006-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore.impl;


import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.ECrossReferenceEList;

import com.google.gwt.user.client.rpc.GwtTransient;


/**
 * An implementation of the model object '<em><b>EObject</b></em>'.
 * This implementation flattens the fields for storing 
 * the {@link #eProxyURI}, the {@link #eContents}, and the {@link #eCrossReferences},
 * which in {@link EObjectImpl} are stored in the properties holder.
 * This reduces the likelihood of needing to allocate a properties holder and speeds up the access to these fields.
 */
public class FlatEObjectImpl extends EObjectImpl
{
  @GwtTransient
  protected URI eProxyURI;
  @GwtTransient
  protected EList<EObject> eContents;
  @GwtTransient
  protected EList<EObject> eCrossReferences;

  /**
   * Creates an EObject that is faster and more space efficient 
   * for the case where {@link #eSetProxyURI(URI)}, {@link #eContents()}, {@link #eCrossReferences()}
   * are expected to be frequently used.
   */
  protected FlatEObjectImpl() 
  {
    super();
  }

  @Override
  protected EPropertiesHolder eProperties()
  {
    if (eProperties == null)
    {
      eProperties = new EPropertiesHolderBaseImpl();
    }
    return eProperties;
  }

  @Override
  public boolean eIsProxy()
  {
    return eProxyURI != null;
  }
  
  @Override
  public URI eProxyURI()
  {
    return eProxyURI;
  }

  @Override
  public void eSetProxyURI(URI uri)
  {
    eProxyURI = uri;
  }

  @Override
  public EList<EObject> eContents()
  {
    if (eContents == null)
    {
      eContents = EContentsEList.createEContentsEList(this);
    }
    return eContents;
  }

  @Override
  public EList<EObject> eCrossReferences()
  {
    if (eCrossReferences == null)
    {
      eCrossReferences = ECrossReferenceEList.createECrossReferenceEList(this);
    }
    return eCrossReferences;
  }
}
