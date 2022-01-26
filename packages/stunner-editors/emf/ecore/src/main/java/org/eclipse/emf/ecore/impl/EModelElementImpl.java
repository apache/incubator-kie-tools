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
package org.eclipse.emf.ecore.impl;


import com.google.gwt.user.client.rpc.GwtTransient;
import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.InvocationTargetException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EModel Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.impl.EModelElementImpl#getEAnnotations <em>EAnnotations</em>}</li>
 * </ul>
 * </p>
 *
 * @generated not
 */
public abstract class EModelElementImpl extends MinimalEObjectImpl.Container implements EModelElement
{
  /**
   * The cached value of the '{@link #getEAnnotations() <em>EAnnotations</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEAnnotations()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EList<EAnnotation> eAnnotations;

  /**
   * An extensible set of bit flags.
   */
  @GwtTransient
  protected int eFlags;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EModelElementImpl()
  {
    super();
  }

  /**
   * The bit of {@link #eFlags} that is used to represent being {@link #freeze() frozen}.
   */
  protected static final int EFROZEN = 1;

  /**
   * The last bit used by this class; derived classes may use bit values higher than this.
   */
  protected static final int ELAST_EMODEL_ELEMENT_FLAG = EFROZEN;

  protected void freeze()
  {
    setFrozen();
  }

  protected void freeze(Object eModelElement)
  {
    if (eModelElement instanceof EModelElementImpl)
    { 
      ((EModelElementImpl)eModelElement).freeze();
    }
  }

  protected void setFrozen()
  {
    eFlags |= EFROZEN;
  }

  protected boolean isFrozen()
  {
    return (eFlags & EFROZEN) != 0;
  }

  @Override
  protected void eSetDirectResource(Internal resource)
  {
    assert !isFrozen() : "A frozen model should not be modified";
    super.eSetDirectResource(resource);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EcorePackage.Literals.EMODEL_ELEMENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<EAnnotation> getEAnnotations()
  {
    if (eAnnotations == null)
    {
      eAnnotations = new EObjectContainmentWithInverseEList<EAnnotation>(EAnnotation.class, this, EcorePackage.EMODEL_ELEMENT__EANNOTATIONS, EcorePackage.EANNOTATION__EMODEL_ELEMENT);
    }
    return eAnnotations;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EAnnotation getEAnnotation(String source)
  {
    if (eAnnotations != null)
    {
      if (eAnnotations instanceof BasicEList<?>)
      {
        int size = eAnnotations.size();
        if (size > 0)
        {
          EAnnotation [] eAnnotationArray = (EAnnotation[])((BasicEList<?>)eAnnotations).data();
          if (source == null)
          {
            for (int i = 0; i < size; ++i)
            {
              EAnnotation eAnnotation = eAnnotationArray[i];
              if (eAnnotation.getSource() == null)
              {
                return eAnnotation;
              }
            }
          }
          else
          {
            for (int i = 0; i < size; ++i)
            {
              EAnnotation eAnnotation = eAnnotationArray[i];
              if (source.equals(eAnnotation.getSource()))
              {
                return eAnnotation;
              }
            }
          }
        }
      }
      else
      {
        if (source == null)
        {
          for (EAnnotation eAnnotation : eAnnotations)
          {
            if (eAnnotation.getSource() == null)
            {
              return eAnnotation;
            }
          }
        }
        else
        {
          for (EAnnotation eAnnotation : eAnnotations)
          {
            if (source.equals(eAnnotation.getSource()))
            {
              return eAnnotation;
            }
          }
        }
      }
    }

    return null;
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
      case EcorePackage.EMODEL_ELEMENT__EANNOTATIONS:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getEAnnotations()).basicAdd(otherEnd, msgs);
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
      case EcorePackage.EMODEL_ELEMENT__EANNOTATIONS:
        return ((InternalEList<?>)getEAnnotations()).basicRemove(otherEnd, msgs);
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
      case EcorePackage.EMODEL_ELEMENT__EANNOTATIONS:
        return getEAnnotations();
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
      case EcorePackage.EMODEL_ELEMENT__EANNOTATIONS:
        getEAnnotations().clear();
        getEAnnotations().addAll((Collection<? extends EAnnotation>)newValue);
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
      case EcorePackage.EMODEL_ELEMENT__EANNOTATIONS:
        getEAnnotations().clear();
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
      case EcorePackage.EMODEL_ELEMENT__EANNOTATIONS:
        return eAnnotations != null && !eAnnotations.isEmpty();
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
      case EcorePackage.EMODEL_ELEMENT___GET_EANNOTATION__STRING:
        return getEAnnotation((String)arguments.get(0));
    }
    return eDynamicInvoke(operationID, arguments);
  }

  @Override
  public String eURIFragmentSegment(EStructuralFeature eStructuralFeature, EObject eObject)
  {
    if (eObject instanceof ENamedElement)
    {
      ENamedElement eNamedElement = (ENamedElement)eObject;
      String name = eNamedElement.getName();
      if (name != null)
      {
        int count = 0;
        for (Object otherEObject : eContents())
        {
          if (otherEObject == eObject)
          {
            break;
          }
          if (otherEObject instanceof ENamedElement)
          {
            ENamedElement otherENamedElement = (ENamedElement)otherEObject;
            if (name.equals(otherENamedElement.getName()))
            {
              ++count;
            }
          }
        }
        name = eEncodeValue(name);
        return 
          count > 0 ?
            name + "." + count : 
            name;
      }
    }
    else if (eObject instanceof EAnnotation)
    {
      EAnnotation eAnnotation = (EAnnotation)eObject;
      String source = eAnnotation.getSource();
      if (source != null)
      {
        int count = 0;
        for (Object otherEObject : eContents())
        {
          if (otherEObject == eObject)
          {
            break;
          }
          if (otherEObject instanceof EAnnotation)
          {
            EAnnotation otherEAnnotation = (EAnnotation)otherEObject;
            if (source.equals(otherEAnnotation.getSource()))
            {
              ++count;
            }
          }
        }
        
        StringBuffer result = new StringBuffer(source.length() + 5);
        result.append('%');
        result.append(URI.encodeSegment(source,  false));
        result.append('%');
        if (count > 0)
        {
          result.append('.');
          result.append(count);
        }
        return result.toString();
      }
    }
    return super.eURIFragmentSegment(eStructuralFeature, eObject);
  }
  
  @Override
  public EObject eObjectForURIFragmentSegment(String uriFragmentSegment)
  {
    int length = uriFragmentSegment.length();
    if (length > 0)
    {
      // Is the first character a special character, i.e., something other than '@'?
      //
      char firstCharacter = uriFragmentSegment.charAt(0);
      if (firstCharacter != '@')
      {
        // Is it the start of a source URI of an annotation?
        //
        if (firstCharacter == '%')
        {
          // Find the closing '%'
          //
          int index = uriFragmentSegment.lastIndexOf("%");
          boolean hasCount = false;
          if (index == length - 1 || (hasCount = uriFragmentSegment.charAt(index + 1) == '.'))
          {
            // Decode all encoded characters.
            //
            String source = URI.decode(uriFragmentSegment.substring(1, index));
            
            // Check for a count, i.e., a '.' followed by a number.
            //
            int count = 0;
            if (hasCount)
            {
              try
              {
                count = Integer.parseInt(uriFragmentSegment.substring(index + 2));
              }
              catch (NumberFormatException exception)
              {
                throw new WrappedException(exception);
              }
            }
            
            // Look for the annotation with the matching source.
            //
            for (Object object : eContents())
            {
              if (object instanceof EAnnotation)
              {
                EAnnotation eAnnotation = (EAnnotation)object;
                if (source.equals(eAnnotation.getSource()) && count-- == 0)
                {
                  return eAnnotation;
                }
              }
            }
            return null;
          }
        }

        // Look for trailing count.
        //
        int index = uriFragmentSegment.lastIndexOf(".");
        String name = index == -1 ? uriFragmentSegment : uriFragmentSegment.substring(0, index);
        int count = 0;
        if (index != -1)
        {
          try
          {
            count = Integer.parseInt(uriFragmentSegment.substring(index + 1));
          }
          catch (NumberFormatException exception)
          {
            // Interpret it as part of the name.
            //
            name = uriFragmentSegment;
          }
        }

        name = URI.decode(name);
    
        // Look for a matching named element.
        //
        for (Object object : eContents())
        {
          if (object instanceof ENamedElement)
          {
            ENamedElement eNamedElement = (ENamedElement)object;
            if (name.equals(eNamedElement.getName()) && count-- == 0)
            {
              return eNamedElement;
            }
          }
        }
  
        return null;
      }
    }
    
    return super.eObjectForURIFragmentSegment(uriFragmentSegment);
  }

}
