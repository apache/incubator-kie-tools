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

import com.google.gwt.user.client.rpc.GwtTransient;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EType Parameter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.impl.ETypeParameterImpl#getEBounds <em>EBounds</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ETypeParameterImpl extends ENamedElementImpl implements ETypeParameter
{
  /**
   * The cached value of the '{@link #getEBounds() <em>EBounds</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEBounds()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EList<EGenericType> eBounds;

  /**
   * The cached set of all generic types that reference this type parameter.
   * It is populated as the type parameter is {@link EGenericType#setETypeParameter(ETypeParameter) set} on the generic type.
   * @see EGenericTypeImpl#setETypeParameter(ETypeParameter)
   */
  @GwtTransient
  protected Set<EGenericType> eGenericTypes;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ETypeParameterImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EcorePackage.Literals.ETYPE_PARAMETER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<EGenericType> getEBounds()
  {
    if (eBounds == null)
    {
      eBounds = 
        new EObjectContainmentEList<EGenericType>(EGenericType.class, this, EcorePackage.ETYPE_PARAMETER__EBOUNDS)
        {
          private static final long serialVersionUID = 1L;

          @Override
          public NotificationChain inverseAdd(EGenericType object, NotificationChain notifications)
          {
            notifications =  super.inverseAdd(object, notifications);
            synchronized (ETypeParameterImpl.this)
            {
              @SuppressWarnings("unchecked") 
              Set<EGenericTypeImpl> eGenericTypes = (Set<EGenericTypeImpl>)(Set<?>)getEGenericTypes();
              for (EGenericTypeImpl eGenericType : eGenericTypes)
              {
                notifications = eGenericType.setERawType(eGenericType.getErasure(ETypeParameterImpl.this), notifications);
              }
            }
            return notifications;
          }

          @Override
          public NotificationChain inverseRemove(EGenericType object, NotificationChain notifications)
          {
            notifications = super.inverseRemove(object, notifications);
            synchronized (ETypeParameterImpl.this)
            {
              @SuppressWarnings("unchecked") Set<EGenericTypeImpl> eGenericTypes = (Set<EGenericTypeImpl>)(Set<?>)getEGenericTypes();
              for (EGenericTypeImpl eGenericType : eGenericTypes)
              {
                notifications = eGenericType.setERawType(eGenericType.getErasure(ETypeParameterImpl.this), notifications);
              }
            } 
            return notifications;
          }
        };
    }
    return eBounds;
  }

  /**
   */
  protected Set<EGenericType> getEGenericTypes()
  {
    if (eGenericTypes == null)
    {
      eGenericTypes = 
        new HashMap<EGenericType, Object>()
        {
          private static final long serialVersionUID = 1L;

          private HashMap<EGenericType, Object> map()
          {
            return this;
          }
          
          @Override
          public Set<EGenericType> keySet()
          {
            // Create a key set that supports add.
            //
            return 
              new AbstractSet<EGenericType>() 
              {
                @Override
                public Iterator<EGenericType> iterator() 
                {
                  final Iterator<Map.Entry<EGenericType, Object>> delegateIterator = map().entrySet().iterator();
                  return 
                    new Iterator<EGenericType>()
                    {
                      public boolean hasNext()
                      {
                        return delegateIterator.hasNext();
                      }

                      public EGenericType next()
                      {
                        return delegateIterator.next().getKey();
                      }

                      public void remove()
                      {
                        delegateIterator.remove();
                      }
                    };
                }
   
                @Override
                public int size() 
                {
                  return map().size();
                }
   
                @Override
                public boolean contains(Object object) 
                {
                  return containsKey(object);
                }
   
                @Override
                public boolean add(EGenericType eGenericType)
                {
                  return map().put(eGenericType, "") == null;
                }
                
                @Override
                public boolean addAll(Collection<? extends EGenericType> eGenericTypes)
                {
                  boolean result = false;
                  for (EGenericType eGenericType : eGenericTypes)
                  {
                    if (map().put(eGenericType, "") == null)
                    {
                      result = true;
                    }
                  }
                  return result;
                }

                @Override
                public boolean remove(Object object) 
                {
                  if (containsKey(object)) 
                  {
                     map().remove(object);
                     return true;
                  }
                  else
                  {
                    return false;
                  }
                }
  
                @Override
                public void clear() 
                {
                  map().clear();
                }
             };
          }
        }.keySet();
    }
    return eGenericTypes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case EcorePackage.ETYPE_PARAMETER__EANNOTATIONS:
        return ((InternalEList<?>)getEAnnotations()).basicRemove(otherEnd, msgs);
      case EcorePackage.ETYPE_PARAMETER__EBOUNDS:
        return ((InternalEList<?>)getEBounds()).basicRemove(otherEnd, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case EcorePackage.ETYPE_PARAMETER__EANNOTATIONS:
        return getEAnnotations();
      case EcorePackage.ETYPE_PARAMETER__NAME:
        return getName();
      case EcorePackage.ETYPE_PARAMETER__EBOUNDS:
        return getEBounds();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case EcorePackage.ETYPE_PARAMETER__EANNOTATIONS:
        getEAnnotations().clear();
        getEAnnotations().addAll((Collection<? extends EAnnotation>)newValue);
        return;
      case EcorePackage.ETYPE_PARAMETER__NAME:
        setName((String)newValue);
        return;
      case EcorePackage.ETYPE_PARAMETER__EBOUNDS:
        getEBounds().clear();
        getEBounds().addAll((Collection<? extends EGenericType>)newValue);
        return;
    }
    eDynamicSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case EcorePackage.ETYPE_PARAMETER__EANNOTATIONS:
        getEAnnotations().clear();
        return;
      case EcorePackage.ETYPE_PARAMETER__NAME:
        setName(NAME_EDEFAULT);
        return;
      case EcorePackage.ETYPE_PARAMETER__EBOUNDS:
        getEBounds().clear();
        return;
    }
    eDynamicUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case EcorePackage.ETYPE_PARAMETER__EANNOTATIONS:
        return eAnnotations != null && !eAnnotations.isEmpty();
      case EcorePackage.ETYPE_PARAMETER__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case EcorePackage.ETYPE_PARAMETER__EBOUNDS:
        return eBounds != null && !eBounds.isEmpty();
    }
    return eDynamicIsSet(featureID);
  }

} //ETypeParameterImpl
