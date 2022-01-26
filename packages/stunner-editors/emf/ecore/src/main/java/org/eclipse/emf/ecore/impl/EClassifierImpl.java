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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.InvocationTargetException;
import org.eclipse.emf.common.util.Reflect;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EMeta Object</b></em>'.
 * @extends BasicExtendedMetaData.EClassifierExtendedMetaData.Holder
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassifierImpl#getInstanceClassName <em>Instance Class Name</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassifierImpl#getInstanceClass <em>Instance Class</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassifierImpl#getDefaultValue <em>Default Value</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassifierImpl#getInstanceTypeName <em>Instance Type Name</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassifierImpl#getEPackage <em>EPackage</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassifierImpl#getETypeParameters <em>EType Parameters</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class EClassifierImpl extends ENamedElementImpl implements EClassifier, BasicExtendedMetaData.EClassifierExtendedMetaData.Holder
{
  @GwtTransient
  protected int metaObjectID = -1;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EClassifierImpl()
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
    return EcorePackage.Literals.ECLASSIFIER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @modifiable
   */
  public int getClassifierID()
  {
    if (metaObjectID == -1)
    {
      metaObjectID = computeClassifierID();
    }
    return metaObjectID;
  }

  private final int computeClassifierID()
  {
    EPackage ePackage = getEPackage();
    return
    
      ePackage != null ?
        ePackage.getEClassifiers().indexOf(this) : 
        -1;
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
      case EcorePackage.ECLASSIFIER__EANNOTATIONS:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getEAnnotations()).basicAdd(otherEnd, msgs);
      case EcorePackage.ECLASSIFIER__EPACKAGE:
        if (eInternalContainer() != null)
          msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, EcorePackage.ECLASSIFIER__EPACKAGE, msgs);
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
      case EcorePackage.ECLASSIFIER__EANNOTATIONS:
        return ((InternalEList<?>)getEAnnotations()).basicRemove(otherEnd, msgs);
      case EcorePackage.ECLASSIFIER__EPACKAGE:
        return eBasicSetContainer(null, EcorePackage.ECLASSIFIER__EPACKAGE, msgs);
      case EcorePackage.ECLASSIFIER__ETYPE_PARAMETERS:
        return ((InternalEList<?>)getETypeParameters()).basicRemove(otherEnd, msgs);
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
      case EcorePackage.ECLASSIFIER__EPACKAGE:
        return eInternalContainer().eInverseRemove(this, EcorePackage.EPACKAGE__ECLASSIFIERS, EPackage.class, msgs);
    }
    return eDynamicBasicRemoveFromContainer(msgs);
  }

  public void setClassifierID(int id)
  {
    metaObjectID = id;
  }

  /**
   * Returns whether the object is an instance of this classifier.
   * @param object the object in question.
   * @return whether the object is an instance.
   * @see Class#isInstance
   */
  public boolean isInstance(Object object)
  {
    if (object != null)
    {
      Class<?> instanceClass = getInstanceClass();
      if (instanceClass != null)
      {
        if (instanceClass.isPrimitive())
        {
          if (instanceClass == boolean.class)
          {
            return object instanceof Boolean;
          }
          else if (instanceClass == int.class)
          {
            return object instanceof Integer;
          }
          else if (instanceClass == float.class)
          {
            return object instanceof Float;
          }
          else if (instanceClass == byte.class)
          {
            return object instanceof Byte;
          }
          else if (instanceClass == char.class)
          {
            return object instanceof Character;
          }
          else if (instanceClass == double.class)
          {
            return object instanceof Double;
          }
          else if (instanceClass == short.class)
          {
            return object instanceof Short;
          }
          else if (instanceClass == long.class)
          {
            return object instanceof Long;
          }
        }
        else
        {
          return Reflect.isInstance(instanceClass, object);
        }
      }
      else if (object instanceof EObject)
      {
        return dynamicIsInstance((EObject)object);
      }
    }

    return false;
  }

  protected boolean dynamicIsInstance(EObject eObject)
  {
    return eObject.eClass() == this;
  }

  /**
   * The default value of the '{@link #getInstanceClassName() <em>Instance Class Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInstanceClassName()
   * @generated NOT
   * @ordered
   */
  protected static final String INSTANCE_CLASS_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getInstanceClassName() <em>Instance Class Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInstanceClassName()
   * @generated NOT
   * @ordered
   */
  @GwtTransient
  protected String instanceClassName = INSTANCE_CLASS_NAME_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Deprecated
  public String getInstanceClassNameGen()
  {
    return instanceClassName;
  }

  public String getInstanceClassName()
  {
    return instanceClassName != null ? instanceClassName : generatedInstanceClassName;
  }

  @GwtTransient
  protected String generatedInstanceClassName;

  public void setGeneratedInstanceClass(boolean isGenerated)
  {
    if (isGenerated)
    {
      if (generatedInstanceClassName == null)
      {
        generatedInstanceClassName = instanceClassName;
        instanceClassName = null;
      }
    }
    else if (generatedInstanceClassName != null)
    {
      instanceClassName = generatedInstanceClassName;
      generatedInstanceClassName = null;
    }
  }

  protected void basicSetInstanceClassName(String value)
  {
    if (instanceClassName == null && generatedInstanceClassName != null)
    {
      instanceClassName = generatedInstanceClassName;
      generatedInstanceClassName = null;
    }
    setInstanceClassNameGen(value == null ? null : value.intern());
    if (instanceClass != null)
    {
      setInstanceClassGen(null);
    }
  }

  public void setInstanceClassName(String value)
  {
    basicSetInstanceClassName(value);

    // Forward the interned value.
    //
    basicSetInstanceTypeName(instanceClassName);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void setInstanceClassNameGen(String newInstanceClassName)
  {
    String oldInstanceClassName = instanceClassName;
    instanceClassName = newInstanceClassName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.ECLASSIFIER__INSTANCE_CLASS_NAME, oldInstanceClassName, instanceClassName));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void unsetInstanceClassName()
  {
    setInstanceClassName(null);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean isSetInstanceClassName()
  {
    return instanceClassName != null && instanceClassName == instanceTypeName;
  }

  /**
   * The default value of the '{@link #getInstanceClass() <em>Instance Class</em>}' attribute.
   * @see #getInstanceClass()
   */
  protected static final Class<?> INSTANCE_CLASS_EDEFAULT = null;

  /**
   * The default value of the '{@link #getDefaultValue() <em>Default Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDefaultValue()
   * @generated
   * @ordered
   */
  protected static final Object DEFAULT_VALUE_EDEFAULT = null;

  /**
   * The default value of the '{@link #getInstanceTypeName() <em>Instance Type Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInstanceTypeName()
   * @generated NOT
   * @ordered
   */
  @SuppressWarnings("unused")
  private static final String INSTANCE_TYPE_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getInstanceTypeName() <em>Instance Type Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInstanceTypeName()
   * @generated NOT
   * @ordered
   */
  @GwtTransient
  protected String instanceTypeName;

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
   * The cached value of the '{@link #getInstanceClass() <em>Instance Class</em>}' attribute.
   * @see #getInstanceClass()
   */
  @GwtTransient
  protected Class<?> instanceClass = INSTANCE_CLASS_EDEFAULT;

  public Class<?> getInstanceClass() 
  {
    if (instanceClass == null && (instanceClassName != null || generatedInstanceClassName != null))
    {
      Class<?> primitiveClass = getPrimitiveOrArrayClass();
      if (primitiveClass != null)
      {
        setInstanceClassGen(primitiveClass);
      }
      else
      {
        try
        {
          setInstanceClassGen(getClassForName(getInstanceClassName()));
        }
        catch (RuntimeException e)
        {
          // Ignore exceptions.
        }
      }
    }
    return getInstanceClassGen();
  }

  /**
   * Returns the <code>Class</code> object associated with the class or interface with the given name, as from a {@link
   * java.lang.Class#forName(String)} call; however, if this classifier belongs to a package, that package's class loader is
   * used. Since the package may be model-specific code in another plug-in, its class loader may be able to see classes
   * that Ecore's can't.
   */
  protected Class<?> getClassForName(String name)
  {
    return null;
    // TODO
    // EPackage p = getEPackage();
    // return p != null ? Class.forName(name, true, p.getClass().getClassLoader()) : Class.forName(name);
  }
  
  protected Class<?> getPrimitiveOrArrayClass() 
  {
    String className = getInstanceClassName();
    int arrayIndex = className.indexOf('[');
    if (arrayIndex != -1)
    {
      String componentClassName = className.substring(0, arrayIndex);
      StringBuffer result = new StringBuffer();
      do result.append('['); while ((arrayIndex = className.indexOf('[', ++arrayIndex)) != -1);
      if (componentClassName.equals("boolean"))
        result.append('Z');
      else if (componentClassName.equals("byte"))
        result.append('B');
      else if (componentClassName.equals("char"))
        result.append('C');
      else if (componentClassName.equals("double"))
        result.append('D');
      else if (componentClassName.equals("float"))
        result.append('F');
      else if (componentClassName.equals("int"))
        result.append('I');
      else if (componentClassName.equals("long"))
        result.append('J');
      else if (componentClassName.equals("short"))
        result.append('S');
      else {
        result.append('L');
        result.append(componentClassName);
        result.append(';');
      }
      try
      {
        return getClassForName(result.toString());
      }
      catch (RuntimeException e) 
      {
        // Continue and return null.
      }
    }
    else if (className.indexOf('.') == -1)
    {
      if (className.equals("boolean"))
        return boolean.class;
      else if (className.equals("byte"))
        return byte.class;
      else if (className.equals("char"))
        return char.class;
      else if (className.equals("double"))
        return double.class;
      else if (className.equals("float"))
        return float.class;
      else if (className.equals("int"))
        return int.class;
      else if (className.equals("long"))
        return long.class;
      else if (className.equals("short"))
        return short.class;
    }
    return null;
  }

  /**
   */
  public Class<?> getInstanceClassGen()
  {
    return instanceClass;
  }

  public void setInstanceClass(Class<?> value)
  {
    if (value == null)
    {
      setInstanceClassNameGen(null);
      basicSetInstanceTypeName(null);
    }
    else if (value.isArray())
    {
      String indices = "[]";
      for (Class<?> component = value.getComponentType(); ; component = component.getComponentType())
      {
        if (!component.isArray())
        {
          String name = (component.getName() + indices).intern();
          setInstanceClassNameGen(name);
          basicSetInstanceTypeName(name);
          break;
        }
        indices += "[]";
      }
    }
    else
    {
      String name = value.getName().intern();
      setInstanceClassNameGen(name);
      basicSetInstanceTypeName(name);
    }

    setInstanceClassGen(value);
  }

  /**
   */
  public void setInstanceClassGen(Class<?> newInstanceClass)
  {
    instanceClass = newInstanceClass;
  }

  /**
   */
  public Object getDefaultValue()
  {
    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String getInstanceTypeName()
  {
    return instanceTypeName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void setInstanceTypeName(String newInstanceTypeName)
  {
    String oldInstanceTypeName = instanceTypeName;
    if (newInstanceTypeName == null)
    {
      instanceTypeName = null;
      basicSetInstanceClassName(null);
    }
    else
    {
      instanceTypeName = newInstanceTypeName.intern();
      // If it's a parameterized type...
      //
      int index = newInstanceTypeName.indexOf('<');
      if (index != -1)
      {
        // The instance class name is the erasure.
        //
        String newInstanceClassName = newInstanceTypeName.substring(0, index);
        
        // If the name isn't a qualified name and isn't a primitive type, it's assumed to denote some arbitrary type parameter.
        //
        if (newInstanceTypeName.indexOf('.') == -1 &&
              !newInstanceClassName.equals("boolean") &&
              !newInstanceClassName.equals("byte") &&
              !newInstanceClassName.equals("char") &&
              !newInstanceClassName.equals("double") &&
              !newInstanceClassName.equals("float") &&
              !newInstanceClassName.equals("int") &&
              !newInstanceClassName.equals("long") &&
              !newInstanceClassName.equals("short"))
        {
          newInstanceClassName = "java.lang.Object";
        }
          
        int end = newInstanceTypeName.lastIndexOf('>');
        if (end != -1)
        {
          // Be sure to pick up any trailing [] brackets.
          //
          newInstanceClassName += newInstanceTypeName.substring(end + 1);
        }
        basicSetInstanceClassName(newInstanceClassName);
      }
      else
      {
        String newInstanceClassName = newInstanceTypeName;
        if (newInstanceTypeName.indexOf('.') == -1)
        {
          index = newInstanceTypeName.indexOf('[');
          if (index != -1)
          {
            newInstanceClassName = newInstanceTypeName.substring(0, index);
          }
          if (!newInstanceClassName.equals("boolean") &&
                !newInstanceClassName.equals("byte") &&
                !newInstanceClassName.equals("char") &&
                !newInstanceClassName.equals("double") &&
                !newInstanceClassName.equals("float") &&
                !newInstanceClassName.equals("int") &&
                !newInstanceClassName.equals("long") &&
                !newInstanceClassName.equals("short"))
          {
            newInstanceClassName = "java.lang.Object";
            if (index != -1)
            {
              newInstanceClassName += newInstanceTypeName.substring(index);
            }
          }
          else
          {
            newInstanceClassName = newInstanceTypeName;
          }
        }

        // We set it and get back the interned string.
        // This way, when instanceClassName == instanceTypeName we know we should serialize only the class name.
        //
        basicSetInstanceClassName(newInstanceClassName);
        if (newInstanceClassName == newInstanceTypeName)
        {
          instanceTypeName = instanceClassName;
        }
      }
    }
    
    if (eNotificationRequired())
    {
      eNotify
        (new ENotificationImpl(this,  Notification.SET,  EcorePackage.ECLASSIFIER__INSTANCE_TYPE_NAME,  oldInstanceTypeName, newInstanceTypeName));
    }
  }

  protected void basicSetInstanceTypeName(String newInstanceTypeName)
  {
    String oldInstanceTypeName = instanceTypeName;
    instanceTypeName = newInstanceTypeName;
    if (eNotificationRequired())
    {
      eNotify
        (new ENotificationImpl(this,  Notification.SET,  EcorePackage.ECLASSIFIER__INSTANCE_TYPE_NAME,  oldInstanceTypeName, newInstanceTypeName));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void unsetInstanceTypeName()
  {
    setInstanceTypeName(null);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean isSetInstanceTypeName()
  {
    return instanceTypeName != null && instanceTypeName != instanceClassName && instanceTypeName != generatedInstanceClassName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EPackage getEPackageGen()
  {
    if (eContainerFeatureID() != EcorePackage.ECLASSIFIER__EPACKAGE) return null;
    return (EPackage)eContainer();
  }

  /**
   * A package protected typed cache for the containing package.
   */
  @GwtTransient
  EPackage ePackage;

  /**
   */
  public EPackage getEPackage()
  {
    // If there is a cached result, return it.
    // We expect that if the package is set to be a proxy, 
    // the code in EPackageImpl.eSetProxyURI override will have cleared this value.
    //
    if (ePackage != null)
    {
      return ePackage;
    }
    else
    {
      // If the result is not a proxy, cache it.
      //
      EPackage result = getEPackageGen();
      if (result != null && !result.eIsProxy())
      {
        ePackage = result;
      }
      return result;
    }
  }
  
  @Override
  protected void eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID)
  {
    // Ensure that cached ePackage is forgotten.
    ePackage = null;
    super.eBasicSetContainer(newContainer, newContainerFeatureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EPackage basicGetEPackage()
  {
    if (eContainerFeatureID() != EcorePackage.ECLASSIFIER__EPACKAGE) return null;
    return (EPackage)eInternalContainer();
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
      eTypeParameters = new EObjectContainmentEList.Resolving<ETypeParameter>(ETypeParameter.class, this, EcorePackage.ECLASSIFIER__ETYPE_PARAMETERS);
    }
    return eTypeParameters;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public String toString()
  {
    // TODO Update this after old tests pass and then update the old tests to reflect this change.
    //
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (instanceClassName: ");
    result.append(instanceClassName);
    result.append(')');
    return result.toString();
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
      case EcorePackage.ECLASSIFIER__EANNOTATIONS:
        return getEAnnotations();
      case EcorePackage.ECLASSIFIER__NAME:
        return getName();
      case EcorePackage.ECLASSIFIER__INSTANCE_CLASS_NAME:
        return getInstanceClassName();
      case EcorePackage.ECLASSIFIER__INSTANCE_CLASS:
        return getInstanceClass();
      case EcorePackage.ECLASSIFIER__DEFAULT_VALUE:
        return getDefaultValue();
      case EcorePackage.ECLASSIFIER__INSTANCE_TYPE_NAME:
        return getInstanceTypeName();
      case EcorePackage.ECLASSIFIER__EPACKAGE:
        if (resolve) return getEPackage();
        return basicGetEPackage();
      case EcorePackage.ECLASSIFIER__ETYPE_PARAMETERS:
        return getETypeParameters();
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
      case EcorePackage.ECLASSIFIER__EANNOTATIONS:
        getEAnnotations().clear();
        getEAnnotations().addAll((Collection<? extends EAnnotation>)newValue);
        return;
      case EcorePackage.ECLASSIFIER__NAME:
        setName((String)newValue);
        return;
      case EcorePackage.ECLASSIFIER__INSTANCE_CLASS_NAME:
        setInstanceClassName((String)newValue);
        return;
      case EcorePackage.ECLASSIFIER__INSTANCE_TYPE_NAME:
        setInstanceTypeName((String)newValue);
        return;
      case EcorePackage.ECLASSIFIER__ETYPE_PARAMETERS:
        getETypeParameters().clear();
        getETypeParameters().addAll((Collection<? extends ETypeParameter>)newValue);
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
      case EcorePackage.ECLASSIFIER__EANNOTATIONS:
        getEAnnotations().clear();
        return;
      case EcorePackage.ECLASSIFIER__NAME:
        setName(NAME_EDEFAULT);
        return;
      case EcorePackage.ECLASSIFIER__INSTANCE_CLASS_NAME:
        unsetInstanceClassName();
        return;
      case EcorePackage.ECLASSIFIER__INSTANCE_TYPE_NAME:
        unsetInstanceTypeName();
        return;
      case EcorePackage.ECLASSIFIER__ETYPE_PARAMETERS:
        getETypeParameters().clear();
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
      case EcorePackage.ECLASSIFIER__EANNOTATIONS:
        return eAnnotations != null && !eAnnotations.isEmpty();
      case EcorePackage.ECLASSIFIER__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case EcorePackage.ECLASSIFIER__INSTANCE_CLASS_NAME:
        return isSetInstanceClassName();
      case EcorePackage.ECLASSIFIER__INSTANCE_CLASS:
        return getInstanceClass() != null;
      case EcorePackage.ECLASSIFIER__DEFAULT_VALUE:
        return DEFAULT_VALUE_EDEFAULT == null ? getDefaultValue() != null : !DEFAULT_VALUE_EDEFAULT.equals(getDefaultValue());
      case EcorePackage.ECLASSIFIER__INSTANCE_TYPE_NAME:
        return isSetInstanceTypeName();
      case EcorePackage.ECLASSIFIER__EPACKAGE:
        return basicGetEPackage() != null;
      case EcorePackage.ECLASSIFIER__ETYPE_PARAMETERS:
        return eTypeParameters != null && !eTypeParameters.isEmpty();
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
      case EcorePackage.ECLASSIFIER___GET_EANNOTATION__STRING:
        return getEAnnotation((String)arguments.get(0));
      case EcorePackage.ECLASSIFIER___IS_INSTANCE__OBJECT:
        return isInstance(arguments.get(0));
      case EcorePackage.ECLASSIFIER___GET_CLASSIFIER_ID:
        return getClassifierID();
    }
    return eDynamicInvoke(operationID, arguments);
  }

  @GwtTransient
  protected BasicExtendedMetaData.EClassifierExtendedMetaData eClassifierExtendedMetaData;

  public BasicExtendedMetaData.EClassifierExtendedMetaData getExtendedMetaData()
  {
    return eClassifierExtendedMetaData;
  }

  public void setExtendedMetaData(BasicExtendedMetaData.EClassifierExtendedMetaData eClassifierExtendedMetaData)
  {
    this.eClassifierExtendedMetaData = eClassifierExtendedMetaData;
  }

  @Override
  public void setName(String newName)
  {
    if (eContainer instanceof EPackageImpl)
    {
      ((EPackageImpl)eContainer).eNameToEClassifierMap = null;
    }
    super.setName(newName);
  }
}
