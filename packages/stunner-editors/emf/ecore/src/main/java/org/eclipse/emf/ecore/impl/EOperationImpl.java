/**
 * Copyright (c) 2002-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 *   Christian Damus (Zeligsoft) - 255469
 */
package org.eclipse.emf.ecore.impl;


import com.google.gwt.user.client.rpc.GwtTransient;
import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.util.Array;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.InvocationTargetException;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.BasicInvocationDelegate;
import org.eclipse.emf.ecore.util.DelegatingEcoreEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
//import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EOperation</b></em>'.
 * @extends EOperation.Internal
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.impl.EOperationImpl#getEContainingClass <em>EContaining Class</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EOperationImpl#getETypeParameters <em>EType Parameters</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EOperationImpl#getEParameters <em>EParameters</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EOperationImpl#getEExceptions <em>EExceptions</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EOperationImpl#getEGenericExceptions <em>EGeneric Exceptions</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EOperationImpl extends ETypedElementImpl implements EOperation, EOperation.Internal
{
  @GwtTransient
  protected int operationID = -1;

  /**
   * The cached value of the '{@link #getETypeParameters() <em>EType Parameters</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getETypeParameters()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EList<ETypeParameter> eTypeParameters;

  /**
   * The cached value of the '{@link #getEParameters() <em>EParameters</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEParameters()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EList<EParameter> eParameters;

  /**
   * The cached value of the '{@link #getEExceptions() <em>EExceptions</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEExceptions()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EList<EClassifier> eExceptions;

  /**
   * The cached value of the '{@link #getEGenericExceptions() <em>EGeneric Exceptions</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEGenericExceptions()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EList<EGenericType> eGenericExceptions;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EOperationImpl()
  {
    super();
  }

  @Override
  protected void freeze()
  {
    if (eParameters != null)
    {
      for (int i = 0, size = eParameters.size(); i < size; ++i)
      {
        freeze(eParameters.get(i));
      }
    }
    super.freeze();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EcorePackage.Literals.EOPERATION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EClass getEContainingClass()
  {
    return (eContainerFeatureID() == EcorePackage.EOPERATION__ECONTAINING_CLASS) ? (EClass)eContainer : null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<EParameter> getEParameters()
  {
    if (eParameters == null)
    {
      eParameters = new EObjectContainmentWithInverseEList<EParameter>(EParameter.class, this, EcorePackage.EOPERATION__EPARAMETERS, EcorePackage.EPARAMETER__EOPERATION);
    }
    return eParameters;
  }

  public EList<EClassifier> getEExceptions()
  {
    if (eExceptions == null)
    {
      eExceptions = 
        new DelegatingEcoreEList<EClassifier>(this)
        {
          private static final long serialVersionUID = 1L;

          @Override
          protected List<EClassifier> delegateList()
          {
            return null;
          }

          @Override
          protected List<EClassifier> delegateBasicList()
          {
            return 
              new AbstractSequentialList<EClassifier>() 
              {
                @Override
                public ListIterator<EClassifier> listIterator(int index)
                {
                  return basicListIterator(index);
                }

                @Override
                public int size()
                {
                  return delegateSize();
                }
              };
          }

          @Override
          protected Iterator<EClassifier> delegateIterator()
          {
            return iterator();
          }

          @Override
          protected ListIterator<EClassifier> delegateListIterator()
          {
            return listIterator();
          }

          protected EGenericType wrap(EClassifier eClassifier)
          {
            EGenericType eGenericType = EcoreFactory.eINSTANCE.createEGenericType();
            eGenericType.setEClassifier(eClassifier);
            return eGenericType;
          }

          protected EClassifier unwrap(EGenericType eGenericType)
          {
            EClassifier result = ((EGenericTypeImpl)eGenericType).basicGetERawType();
            if (result  != null)
            {
              return result;
            }
            else
            {
              return EcorePackage.Literals.EJAVA_OBJECT;
            }
          }

          @Override
          protected void delegateAdd(int index, EClassifier eClassifier)
          {
            getEGenericExceptions().add(index, wrap(eClassifier));
          }

          @Override
          protected void delegateClear()
          {
            getEGenericExceptions().clear();
          }

          @Override
          protected void delegateAdd(EClassifier eClassifier)
          {
            getEGenericExceptions().add(wrap(eClassifier));
          }

          @Override
          protected boolean delegateContains(Object object)
          {
            for (EClassifier eClassifier : this)
            {
              if (object == eClassifier)
              {
                return true;
              }
            }
            return false;
          }

          @Override
          protected boolean delegateContainsAll(Collection<?> collection)
          {
            for (Object object : collection)
            {
              if (!delegateContains(object))
              {
                return false;
              }
            }
            return true;
          }

          @Override
          protected boolean delegateEquals(Object object)
          {
            if (object instanceof List<?>)
            {
              List<?> list = (List<?>)object;
              if (list.size() == delegateSize())
              {
                for (Iterator<?> i = list.iterator(), j = iterator(); i.hasNext(); )
                {
                  if (i.next() != j.next())
                  {
                    return false;
                  }
                }
                return true;
              }
            }
            return false;
          }

          @Override
          protected EClassifier delegateGet(int index)
          {
            EGenericType eGenericType = getEGenericExceptions().get(index);
            return unwrap(eGenericType);
          }

          @Override
          protected int delegateHashCode()
          {
            int hashCode = 1;
            for (EGenericType eGenericType : getEGenericExceptions())
            {
              Object object = unwrap(eGenericType);
              hashCode = 31 * hashCode + (object == null ? 0 : object.hashCode());
            }
            return hashCode;
          }

          @Override
          protected int delegateIndexOf(Object object)
          {
            int index = 0;
            for (EGenericType eGenericType : getEGenericExceptions())
            {
              if (object == unwrap(eGenericType))
              {
                return index;
              }
              ++index;
            }
            return -1;
          }

          @Override
          protected boolean delegateIsEmpty()
          {
            return getEGenericExceptions().isEmpty();
          }

          @Override
          protected int delegateLastIndexOf(Object object)
          {
            EList<EGenericType> eGenericExceptions = getEGenericExceptions();
            for (int i = eGenericExceptions.size() - 1; i >= 0; --i)
            {
              if (unwrap(eGenericExceptions.get(i)) == object)
              {
                return i;
              }
            }
            return -1;
          }

          @Override
          protected EClassifier delegateRemove(int index)
          {
            EGenericType eGenericType = getEGenericExceptions().remove(index);
            return unwrap(eGenericType);
          }

          @Override
          protected EClassifier delegateSet(int index, EClassifier eClassifier)
          {
            EGenericType eGenericType = getEGenericExceptions().get(index);
            EClassifier result = unwrap(eGenericType);

            // If this is just a proxy being resolved...
            //
            if (resolveProxy(result) == eClassifier)
            {
              // Force the raw type to be resolved so we don't resolve this endlessly.
              //
              eGenericType.getERawType();
            }
            else
            {
              // Update the classifier and hence the raw type as normal.
              //
              eGenericType.setEClassifier(eClassifier);
            }
            return result;
          }

          @Override
          protected int delegateSize()
          {
            return getEGenericExceptions().size();
          }

          @Override
          protected Object[] delegateToArray()
          {
            int size = delegateSize();
            Object[] result = new Object[size];
            
            int index = 0;
            for (EGenericType eGenericType : getEGenericExceptions())
            {
              result[index++] = unwrap(eGenericType);
            }
            return result;
          }

          @Override
          protected <T> T[] delegateToArray(T[] array)
          {
            int size = delegateSize();
            if (array.length < size)
            {
              @SuppressWarnings("unchecked") T[] newArray = (T[])Array.newInstance(array.getClass().getComponentType(), size);
              array = newArray;
            }
          
            if (array.length > size)
            {
              array[size] = null;
            }

            int index = 0;
            for (EGenericType eGenericType : getEGenericExceptions())
            {
              @SuppressWarnings("unchecked") T rawType = (T)unwrap(eGenericType);
              array[index++] = rawType;
            }

            return array;
          }

          @Override
          protected String delegateToString()
          {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("[");
            EList<EGenericType> eGenericExceptions = getEGenericExceptions();
            for (int i = 0, size = delegateSize(); i < size; )
            {
              stringBuffer.append(String.valueOf(unwrap(eGenericExceptions.get(i))));
              if (++i < size)
              {
                stringBuffer.append(", ");
              }
            }
            stringBuffer.append("]");
            return stringBuffer.toString();
          }

          @Override
          protected boolean isInstance(Object object)
          {
            return object instanceof EClassifier;
          }

          @Override
          public int getFeatureID()
          {
            return EcorePackage.EOPERATION__EEXCEPTIONS;
          }

          @Override
          protected boolean useEquals()
          {
            return true;
          }

          @Override
          protected boolean canContainNull()
          {
            return false;
          }

          @Override
          protected boolean isUnique()
          {
            return true;
          }

          @Override
          protected boolean hasInverse()
          {
            return false;
          }

          @Override
          protected boolean hasManyInverse()
          {
            return false;
          }

          @Override
          protected boolean hasNavigableInverse()
          {
            return false;
          }

          @Override
          protected boolean isEObject()
          {
            return true;
          }

          @Override
          protected boolean isContainment()
          {
            return false;
          }

          @Override
          protected boolean hasProxies()
          {
            return true;
          }

          @Override
          protected boolean hasInstanceClass()
          {
            return true;
          }
          
          @Override
          public boolean isSet()
          {
            return isSetEExceptions();
          }
          
          @Override
          protected NotificationImpl createNotification(int eventType, Object oldObject, Object newObject, int index, boolean wasSet)
          {
            // The notification for this list is being thrown by the
            // delegating list
            //
            return null;
          }
          
          @Override
          protected void dispatchNotification(Notification notification)
          {
            // Do nothing
          }          
        };
    }
    return eExceptions;
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void unsetEExceptions()
  {
    if (eExceptions != null) ((InternalEList.Unsettable<?>)eExceptions).unset();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean isSetEExceptions()
  {
    return  eExceptions != null && !eExceptions.isEmpty() && !isSetEGenericExceptions();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<EGenericType> getEGenericExceptions()
  {
    if (eGenericExceptions == null)
    {
      eGenericExceptions = 
        new EObjectContainmentEList.Unsettable<EGenericType>(EGenericType.class, this, EcorePackage.EOPERATION__EGENERIC_EXCEPTIONS)
        {
          private static final long serialVersionUID = 1L;

          @Override
          public boolean isSet()
          {
            for (EGenericType eGenericType : this)
            {
              if (eGenericType.getETypeParameter() != null || !eGenericType.getETypeArguments().isEmpty())
              {
                return true;
              }
            }
            return false;
          }
          
          protected EClassifier unwrap(EGenericType eGenericType)
          {
            EClassifier result = eGenericType.getERawType();
            if (result != null)
            {
              return result;
            }
            else
            {
              return EcorePackage.Literals.EJAVA_OBJECT;
            }
          }

          @Override
          protected boolean hasShadow()
          {
            return true;
          }
          
          @Override
          protected NotificationChain shadowAdd(EGenericType eGenericType, NotificationChain notifications)
          {
            ENotificationImpl notification =
              new ENotificationImpl
                (owner, Notification.ADD, EcorePackage.EOPERATION__EEXCEPTIONS, null, unwrap(eGenericType), indexOf(eGenericType), false);
            if (notifications == null)
            {
              notifications = notification;
            }
            else
            {
              notifications.add(notification);
            }
            return notifications;
          }
          
          @Override
          protected NotificationChain shadowRemove(EGenericType eGenericType, NotificationChain notifications)
          {
            ENotificationImpl notification =
              new ENotificationImpl
                (owner, Notification.REMOVE, EcorePackage.EOPERATION__EEXCEPTIONS, unwrap(eGenericType), null, indexOf(eGenericType), false);
            if (notifications == null)
            {
              notifications = notification;
            }
            else
            {
              notifications.add(notification);
            }
            return notifications;
          }
          
          @Override
          protected NotificationChain shadowSet(EGenericType oldEGenericType, EGenericType newEGenericType, NotificationChain notifications)
          {
            ENotificationImpl notification =
              new ENotificationImpl
                (owner, 
                 Notification.SET, 
                 EcorePackage.EOPERATION__EEXCEPTIONS, 
                 unwrap(oldEGenericType), 
                 unwrap(newEGenericType), 
                 indexOf(oldEGenericType), 
                 false);
            if (notifications == null)
            {
              notifications = notification;
            }
            else
            {
              notifications.add(notification);
            }
            return notifications;
          }
          
          @Override
          public EGenericType move(int targetIndex, int sourceIndex)
          {
            EGenericType result = super.move(targetIndex, sourceIndex);
            if (isNotificationRequired())
            {
              dispatchNotification
                (new ENotificationImpl
                   (EOperationImpl.this,
                    Notification.MOVE, 
                    EcorePackage.Literals.EOPERATION__EEXCEPTIONS,
                    sourceIndex,
                    unwrap(result), 
                    targetIndex));
            }
            return result;
          }

          @Override
          public void unset()
          {
            // Don't really unset it.
            clear();
          }

          @Override
          protected NotificationImpl createNotification(int eventType, Object oldObject, Object newObject, int index, boolean wasSet)
          {
            switch (eventType)
            {
              case Notification.ADD:
              {
                return super.createNotification(eventType, oldObject, newObject, index, size > 1);
              }
              case Notification.ADD_MANY:
              {
                return super.createNotification(eventType, oldObject, newObject, index, size - ((List<?>)newObject).size() > 0);
              }
              default:
              {
                return super.createNotification(eventType, oldObject, newObject, index, true);
              }
            }
          }
        };

      // Force this to be initialized as well.
      getEExceptions();
    }
    return eGenericExceptions;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void unsetEGenericExceptions()
  {
    if (eGenericExceptions != null) ((InternalEList.Unsettable<?>)eGenericExceptions).unset();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isSetEGenericExceptions()
  {
    return eGenericExceptions != null && ((InternalEList.Unsettable<?>)eGenericExceptions).isSet();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated modifiable
   */
  public int getOperationID()
  {
    return operationID;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean isOverrideOf(EOperation someOperation)
  {
    if (someOperation.getEContainingClass().isSuperTypeOf(getEContainingClass()) && someOperation.getName().equals(getName()))
    {
      EList<EParameter> parameters = getEParameters();
      EList<EParameter> otherParameters = someOperation.getEParameters();
      if (parameters.size() == otherParameters.size())
      {
        for (Iterator<EParameter> i = parameters.iterator(), j = otherParameters.iterator(); i.hasNext(); )
        {
          EParameter parameter = i.next();
          EParameter otherParameter = j.next();
          if (!parameter.getEType().getInstanceTypeName().equals(otherParameter.getEType().getInstanceTypeName()))
          {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }

  public void setOperationID(int operationID)
  {
    this.operationID = operationID;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ETypeParameter> getETypeParameters()
  {
    if (eTypeParameters == null)
    {
      eTypeParameters = new EObjectContainmentEList.Resolving<ETypeParameter>(ETypeParameter.class, this, EcorePackage.EOPERATION__ETYPE_PARAMETERS);
    }
    return eTypeParameters;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case EcorePackage.EOPERATION__EANNOTATIONS:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getEAnnotations()).basicAdd(otherEnd, msgs);
      case EcorePackage.EOPERATION__ECONTAINING_CLASS:
        if (eInternalContainer() != null)
          msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, EcorePackage.EOPERATION__ECONTAINING_CLASS, msgs);
      case EcorePackage.EOPERATION__EPARAMETERS:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getEParameters()).basicAdd(otherEnd, msgs);
    }
    return eDynamicInverseAdd(otherEnd, featureID, msgs);
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
      case EcorePackage.EOPERATION__EANNOTATIONS:
        return ((InternalEList<?>)getEAnnotations()).basicRemove(otherEnd, msgs);
      case EcorePackage.EOPERATION__EGENERIC_TYPE:
        return basicUnsetEGenericType(msgs);
      case EcorePackage.EOPERATION__ECONTAINING_CLASS:
        return eBasicSetContainer(null, EcorePackage.EOPERATION__ECONTAINING_CLASS, msgs);
      case EcorePackage.EOPERATION__ETYPE_PARAMETERS:
        return ((InternalEList<?>)getETypeParameters()).basicRemove(otherEnd, msgs);
      case EcorePackage.EOPERATION__EPARAMETERS:
        return ((InternalEList<?>)getEParameters()).basicRemove(otherEnd, msgs);
      case EcorePackage.EOPERATION__EGENERIC_EXCEPTIONS:
        return ((InternalEList<?>)getEGenericExceptions()).basicRemove(otherEnd, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
      case EcorePackage.EOPERATION__ECONTAINING_CLASS:
        return eInternalContainer().eInverseRemove(this, EcorePackage.ECLASS__EOPERATIONS, EClass.class, msgs);
    }
    return eDynamicBasicRemoveFromContainer(msgs);
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
      case EcorePackage.EOPERATION__EANNOTATIONS:
        return getEAnnotations();
      case EcorePackage.EOPERATION__NAME:
        return getName();
      case EcorePackage.EOPERATION__ORDERED:
        return isOrdered();
      case EcorePackage.EOPERATION__UNIQUE:
        return isUnique();
      case EcorePackage.EOPERATION__LOWER_BOUND:
        return getLowerBound();
      case EcorePackage.EOPERATION__UPPER_BOUND:
        return getUpperBound();
      case EcorePackage.EOPERATION__MANY:
        return isMany();
      case EcorePackage.EOPERATION__REQUIRED:
        return isRequired();
      case EcorePackage.EOPERATION__ETYPE:
        if (resolve) return getEType();
        return basicGetEType();
      case EcorePackage.EOPERATION__EGENERIC_TYPE:
        return getEGenericType();
      case EcorePackage.EOPERATION__ECONTAINING_CLASS:
        return getEContainingClass();
      case EcorePackage.EOPERATION__ETYPE_PARAMETERS:
        return getETypeParameters();
      case EcorePackage.EOPERATION__EPARAMETERS:
        return getEParameters();
      case EcorePackage.EOPERATION__EEXCEPTIONS:
        return getEExceptions();
      case EcorePackage.EOPERATION__EGENERIC_EXCEPTIONS:
        return getEGenericExceptions();
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
      case EcorePackage.EOPERATION__EANNOTATIONS:
        getEAnnotations().clear();
        getEAnnotations().addAll((Collection<? extends EAnnotation>)newValue);
        return;
      case EcorePackage.EOPERATION__NAME:
        setName((String)newValue);
        return;
      case EcorePackage.EOPERATION__ORDERED:
        setOrdered((Boolean)newValue);
        return;
      case EcorePackage.EOPERATION__UNIQUE:
        setUnique((Boolean)newValue);
        return;
      case EcorePackage.EOPERATION__LOWER_BOUND:
        setLowerBound((Integer)newValue);
        return;
      case EcorePackage.EOPERATION__UPPER_BOUND:
        setUpperBound((Integer)newValue);
        return;
      case EcorePackage.EOPERATION__ETYPE:
        setEType((EClassifier)newValue);
        return;
      case EcorePackage.EOPERATION__EGENERIC_TYPE:
        setEGenericType((EGenericType)newValue);
        return;
      case EcorePackage.EOPERATION__ETYPE_PARAMETERS:
        getETypeParameters().clear();
        getETypeParameters().addAll((Collection<? extends ETypeParameter>)newValue);
        return;
      case EcorePackage.EOPERATION__EPARAMETERS:
        getEParameters().clear();
        getEParameters().addAll((Collection<? extends EParameter>)newValue);
        return;
      case EcorePackage.EOPERATION__EEXCEPTIONS:
        getEExceptions().clear();
        getEExceptions().addAll((Collection<? extends EClassifier>)newValue);
        return;
      case EcorePackage.EOPERATION__EGENERIC_EXCEPTIONS:
        getEGenericExceptions().clear();
        getEGenericExceptions().addAll((Collection<? extends EGenericType>)newValue);
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
      case EcorePackage.EOPERATION__EANNOTATIONS:
        getEAnnotations().clear();
        return;
      case EcorePackage.EOPERATION__NAME:
        setName(NAME_EDEFAULT);
        return;
      case EcorePackage.EOPERATION__ORDERED:
        setOrdered(ORDERED_EDEFAULT);
        return;
      case EcorePackage.EOPERATION__UNIQUE:
        setUnique(UNIQUE_EDEFAULT);
        return;
      case EcorePackage.EOPERATION__LOWER_BOUND:
        setLowerBound(LOWER_BOUND_EDEFAULT);
        return;
      case EcorePackage.EOPERATION__UPPER_BOUND:
        setUpperBound(UPPER_BOUND_EDEFAULT);
        return;
      case EcorePackage.EOPERATION__ETYPE:
        unsetEType();
        return;
      case EcorePackage.EOPERATION__EGENERIC_TYPE:
        unsetEGenericType();
        return;
      case EcorePackage.EOPERATION__ETYPE_PARAMETERS:
        getETypeParameters().clear();
        return;
      case EcorePackage.EOPERATION__EPARAMETERS:
        getEParameters().clear();
        return;
      case EcorePackage.EOPERATION__EEXCEPTIONS:
        unsetEExceptions();
        return;
      case EcorePackage.EOPERATION__EGENERIC_EXCEPTIONS:
        unsetEGenericExceptions();
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
      case EcorePackage.EOPERATION__EANNOTATIONS:
        return eAnnotations != null && !eAnnotations.isEmpty();
      case EcorePackage.EOPERATION__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case EcorePackage.EOPERATION__ORDERED:
        return ((eFlags & ORDERED_EFLAG) != 0) != ORDERED_EDEFAULT;
      case EcorePackage.EOPERATION__UNIQUE:
        return ((eFlags & UNIQUE_EFLAG) != 0) != UNIQUE_EDEFAULT;
      case EcorePackage.EOPERATION__LOWER_BOUND:
        return lowerBound != LOWER_BOUND_EDEFAULT;
      case EcorePackage.EOPERATION__UPPER_BOUND:
        return upperBound != UPPER_BOUND_EDEFAULT;
      case EcorePackage.EOPERATION__MANY:
        return isMany() != MANY_EDEFAULT;
      case EcorePackage.EOPERATION__REQUIRED:
        return isRequired() != REQUIRED_EDEFAULT;
      case EcorePackage.EOPERATION__ETYPE:
        return isSetEType();
      case EcorePackage.EOPERATION__EGENERIC_TYPE:
        return isSetEGenericType();
      case EcorePackage.EOPERATION__ECONTAINING_CLASS:
        return getEContainingClass() != null;
      case EcorePackage.EOPERATION__ETYPE_PARAMETERS:
        return eTypeParameters != null && !eTypeParameters.isEmpty();
      case EcorePackage.EOPERATION__EPARAMETERS:
        return eParameters != null && !eParameters.isEmpty();
      case EcorePackage.EOPERATION__EEXCEPTIONS:
        return isSetEExceptions();
      case EcorePackage.EOPERATION__EGENERIC_EXCEPTIONS:
        return isSetEGenericExceptions();
    }
    return eDynamicIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
      case EcorePackage.EOPERATION___GET_EANNOTATION__STRING:
        return getEAnnotation((String)arguments.get(0));
      case EcorePackage.EOPERATION___GET_OPERATION_ID:
        return getOperationID();
      case EcorePackage.EOPERATION___IS_OVERRIDE_OF__EOPERATION:
        return isOverrideOf((EOperation)arguments.get(0));
    }
    return eDynamicInvoke(operationID, arguments);
  }

  @GwtTransient
  protected EOperation.Internal.InvocationDelegate invocationDelegate;
  
  public InvocationDelegate getInvocationDelegate()
  {
    if (invocationDelegate == null)
    {
      InvocationDelegate.Factory factory = EcoreUtil.getInvocationDelegateFactory(this);
      if (factory != null)
      {
        invocationDelegate = factory.createInvocationDelegate(this);
      }
      if (invocationDelegate == null)
      {
        invocationDelegate = new BasicInvocationDelegate(this);
      }
    }
    
    return invocationDelegate;
  }
  
  public void setInvocationDelegate(InvocationDelegate invocationDelegate)
  {
    this.invocationDelegate = invocationDelegate;
  }
  
}
