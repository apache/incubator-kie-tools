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


import java.util.Date;
import java.util.Map;

import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

//import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
//import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EcoreFactoryImpl extends EFactoryImpl implements EcoreFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static EcoreFactory init()
  {
    try
    {
      EcoreFactory theEcoreFactory = (EcoreFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/emf/2002/Ecore"); 
      if (theEcoreFactory != null)
      {
        return theEcoreFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new EcoreFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EcoreFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case EcorePackage.EATTRIBUTE: return createEAttribute();
      case EcorePackage.EANNOTATION: return createEAnnotation();
      case EcorePackage.ECLASS: return createEClass();
      case EcorePackage.EDATA_TYPE: return createEDataType();
      case EcorePackage.EENUM: return createEEnum();
      case EcorePackage.EENUM_LITERAL: return createEEnumLiteral();
      case EcorePackage.EFACTORY: return createEFactory();
      case EcorePackage.EOBJECT: return createEObject();
      case EcorePackage.EOPERATION: return createEOperation();
      case EcorePackage.EPACKAGE: return createEPackage();
      case EcorePackage.EPARAMETER: return createEParameter();
      case EcorePackage.EREFERENCE: return createEReference();
      case EcorePackage.ESTRING_TO_STRING_MAP_ENTRY: return (EObject)createEStringToStringMapEntry();
      case EcorePackage.EGENERIC_TYPE: return createEGenericType();
      case EcorePackage.ETYPE_PARAMETER: return createETypeParameter();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
      case EcorePackage.EBOOLEAN:
        return createEBooleanFromString(eDataType, initialValue);
      case EcorePackage.EBOOLEAN_OBJECT:
        return createEBooleanObjectFromString(eDataType, initialValue);
      case EcorePackage.EBYTE:
        return createEByteFromString(eDataType, initialValue);
      case EcorePackage.EBYTE_ARRAY:
        return createEByteArrayFromString(eDataType, initialValue);
      case EcorePackage.EBYTE_OBJECT:
        return createEByteObjectFromString(eDataType, initialValue);
      case EcorePackage.ECHAR:
        return createECharFromString(eDataType, initialValue);
      case EcorePackage.ECHARACTER_OBJECT:
        return createECharacterObjectFromString(eDataType, initialValue);
      case EcorePackage.EDATE:
        return createEDateFromString(eDataType, initialValue);
      case EcorePackage.EDOUBLE:
        return createEDoubleFromString(eDataType, initialValue);
      case EcorePackage.EDOUBLE_OBJECT:
        return createEDoubleObjectFromString(eDataType, initialValue);
      case EcorePackage.EFLOAT:
        return createEFloatFromString(eDataType, initialValue);
      case EcorePackage.EFLOAT_OBJECT:
        return createEFloatObjectFromString(eDataType, initialValue);
      case EcorePackage.EINT:
        return createEIntFromString(eDataType, initialValue);
      case EcorePackage.EINTEGER_OBJECT:
        return createEIntegerObjectFromString(eDataType, initialValue);
      case EcorePackage.EJAVA_CLASS:
        return createEJavaClassFromString(eDataType, initialValue);
      case EcorePackage.EJAVA_OBJECT:
        return createEJavaObjectFromString(eDataType, initialValue);
      case EcorePackage.ELONG:
        return createELongFromString(eDataType, initialValue);
      case EcorePackage.ELONG_OBJECT:
        return createELongObjectFromString(eDataType, initialValue);
      case EcorePackage.ESHORT:
        return createEShortFromString(eDataType, initialValue);
      case EcorePackage.ESHORT_OBJECT:
        return createEShortObjectFromString(eDataType, initialValue);
      case EcorePackage.ESTRING:
        return createEStringFromString(eDataType, initialValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
      case EcorePackage.EBOOLEAN:
        return convertEBooleanToString(eDataType, instanceValue);
      case EcorePackage.EBOOLEAN_OBJECT:
        return convertEBooleanObjectToString(eDataType, instanceValue);
      case EcorePackage.EBYTE:
        return convertEByteToString(eDataType, instanceValue);
      case EcorePackage.EBYTE_ARRAY:
        return convertEByteArrayToString(eDataType, instanceValue);
      case EcorePackage.EBYTE_OBJECT:
        return convertEByteObjectToString(eDataType, instanceValue);
      case EcorePackage.ECHAR:
        return convertECharToString(eDataType, instanceValue);
      case EcorePackage.ECHARACTER_OBJECT:
        return convertECharacterObjectToString(eDataType, instanceValue);
      case EcorePackage.EDATE:
        return convertEDateToString(eDataType, instanceValue);
      case EcorePackage.EDOUBLE:
        return convertEDoubleToString(eDataType, instanceValue);
      case EcorePackage.EDOUBLE_OBJECT:
        return convertEDoubleObjectToString(eDataType, instanceValue);
      case EcorePackage.EFLOAT:
        return convertEFloatToString(eDataType, instanceValue);
      case EcorePackage.EFLOAT_OBJECT:
        return convertEFloatObjectToString(eDataType, instanceValue);
      case EcorePackage.EINT:
        return convertEIntToString(eDataType, instanceValue);
      case EcorePackage.EINTEGER_OBJECT:
        return convertEIntegerObjectToString(eDataType, instanceValue);
      case EcorePackage.EJAVA_CLASS:
        return convertEJavaClassToString(eDataType, instanceValue);
      case EcorePackage.EJAVA_OBJECT:
        return convertEJavaObjectToString(eDataType, instanceValue);
      case EcorePackage.ELONG:
        return convertELongToString(eDataType, instanceValue);
      case EcorePackage.ELONG_OBJECT:
        return convertELongObjectToString(eDataType, instanceValue);
      case EcorePackage.ESHORT:
        return convertEShortToString(eDataType, instanceValue);
      case EcorePackage.ESHORT_OBJECT:
        return convertEShortObjectToString(eDataType, instanceValue);
      case EcorePackage.ESTRING:
        return convertEStringToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EObject createEObject()
  {
    EObjectImpl eObject = new EObjectImpl();
    return eObject;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute createEAttribute()
  {
    EAttributeImpl eAttribute = new EAttributeImpl();
    return eAttribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAnnotation createEAnnotation()
  {
    EAnnotationImpl eAnnotation = new EAnnotationImpl();
    return eAnnotation;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass createEClass()
  {
    EClassImpl eClass = new EClassImpl();
    return eClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType createEDataType()
  {
    EDataTypeImpl eDataType = new EDataTypeImpl();
    return eDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EParameter createEParameter()
  {
    EParameterImpl eParameter = new EParameterImpl();
    return eParameter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation createEOperation()
  {
    EOperationImpl eOperation = new EOperationImpl();
    return eOperation;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EPackage createEPackage()
  {
    EPackageImpl ePackage = new EPackageImpl();
    return ePackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EFactory createEFactory()
  {
    EFactoryImpl eFactory = new EFactoryImpl();
    return eFactory;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnumLiteral createEEnumLiteral()
  {
    EEnumLiteralImpl eEnumLiteral = new EEnumLiteralImpl();
    return eEnumLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum createEEnum()
  {
    EEnumImpl eEnum = new EEnumImpl();
    return eEnum;
  }

  protected Boolean booleanValueOf(String initialValue)
  {
    if ("true".equalsIgnoreCase(initialValue))
    {
      return Boolean.TRUE;
    }
    else if ("false".equalsIgnoreCase(initialValue))
    {
      return Boolean.FALSE;
    }
    else
    {
      throw new IllegalArgumentException("Expecting true or false");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Boolean createEBooleanObjectFromString(EDataType metaObject, String initialValue) 
  {
    return initialValue == null ? null : booleanValueOf(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEBooleanObjectToString(EDataType metaObject, Object instanceValue) 
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Character createECharacterObjectFromString(EDataType metaObject, String initialValue) 
  {
    if (initialValue == null)
    {
      return null;
    }

    char charValue = 0;
    try
    {
      charValue = (char)Integer.parseInt(initialValue);
    }
    catch (NumberFormatException e)
    {
      char[] carray = initialValue.toCharArray();
      charValue = carray[0];
    }
    return charValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertECharacterObjectToString(EDataType metaObject, Object instanceValue) 
  {
    if (instanceValue instanceof Character)
    {
      return Integer.toString((Character)instanceValue);
    }

    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Date createEDateFromString(EDataType eDataType, String initialValue)
  {
    if (initialValue == null)
    {
      return null;
    }

    Exception exception = null;
    for (int i = 0; i < EDATE_FORMATS.length; ++i)
    {
      try
      {
        return EDATE_FORMATS[i].parse(initialValue);
      }
      catch (IllegalArgumentException parseException)
      {
        exception = parseException;
      }
    }
    throw new WrappedException(exception);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEDateToString(EDataType eDataType, Object instanceValue)
  {
    if (instanceValue == null)
    {
      return null;
    }
    else
    {
      return EDATE_FORMATS[0].format((Date)instanceValue);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Double createEDoubleObjectFromString(EDataType metaObject, String initialValue) 
  {
    return initialValue == null ? null : Double.valueOf(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEDoubleObjectToString(EDataType metaObject, Object instanceValue) 
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Float createEFloatObjectFromString(EDataType metaObject, String initialValue) 
  {
    return initialValue == null ? null : Float.valueOf(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEFloatObjectToString(EDataType metaObject, Object instanceValue) 
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Integer createEIntegerObjectFromString(EDataType metaObject, String initialValue) 
  {
    return initialValue == null ? null : Integer.valueOf(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEIntegerObjectToString(EDataType metaObject, Object instanceValue) 
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference createEReference()
  {
    EReferenceImpl eReference = new EReferenceImpl();
    return eReference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry<String, String> createEStringToStringMapEntry()
  {
    EStringToStringMapEntryImpl eStringToStringMapEntry = new EStringToStringMapEntryImpl();
    return eStringToStringMapEntry;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EGenericType createEGenericType()
  {
    EGenericTypeImpl eGenericType = new EGenericTypeImpl();
    return eGenericType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ETypeParameter createETypeParameter()
  {
    ETypeParameterImpl eTypeParameter = new ETypeParameterImpl();
    return eTypeParameter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EcorePackage getEcorePackage()
  {
    return (EcorePackage)getEPackage();
  }

  /**
   * @deprecated
   */
  @Deprecated
  public static EcorePackage getPackage()
  {
    return EcorePackage.eINSTANCE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createEStringFromString(EDataType metaObject, String initialValue) 
  {
    return initialValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEStringToString(EDataType metaObject, Object instanceValue) 
  {
    return (String)instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Integer createEIntFromString(EDataType metaObject, String initialValue) 
  {
    return initialValue == null ? null : Integer.valueOf(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEIntToString(EDataType metaObject, Object instanceValue) 
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Boolean createEBooleanFromString(EDataType metaObject, String initialValue) 
  {
    return initialValue == null ? null : booleanValueOf(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEBooleanToString(EDataType metaObject, Object instanceValue) 
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Byte createEByteObjectFromString(EDataType metaObject, String initialValue) 
  {
    return initialValue == null ? null : Byte.valueOf(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEByteObjectToString(EDataType metaObject, Object instanceValue) 
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Float createEFloatFromString(EDataType metaObject, String initialValue) 
  {
    return initialValue == null ? null : Float.valueOf(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEFloatToString(EDataType metaObject, Object instanceValue) 
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Character createECharFromString(EDataType metaObject, String initialValue) 
  {
    if (initialValue == null)
    {
      return null;
    }
    char charValue = 0;
    try
    {
      charValue = (char)Integer.parseInt(initialValue);
    }
    catch (NumberFormatException e)
    {
      char[] carray = initialValue.toCharArray();
      charValue = carray[0];
    }
    return charValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertECharToString(EDataType metaObject, Object instanceValue) 
  {
    if (instanceValue instanceof Character)
    {
      return Integer.toString((Character)instanceValue);
    }

    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Long createELongFromString(EDataType metaObject, String initialValue) 
  {
    return initialValue == null ? null : Long.valueOf(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertELongToString(EDataType metaObject, Object instanceValue) 
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Double createEDoubleFromString(EDataType metaObject, String initialValue) 
  {
    return initialValue == null ? null : Double.valueOf(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEDoubleToString(EDataType metaObject, Object instanceValue) 
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Byte createEByteFromString(EDataType metaObject, String initialValue) 
  {
    return initialValue == null ? null : Byte.valueOf(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEByteToString(EDataType metaObject, Object instanceValue) 
  {
    return instanceValue == null ? null : instanceValue.toString();
  }


  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public byte[] createEByteArrayFromString(EDataType eDataType, String initialValue)
  {
    return hexStringToBytes(initialValue);
  }

  protected static byte hexCharToByte(char character)
  {
    return EFactoryImpl.hexCharToByte(character);
  }

  protected static final char [] HEX_DIGITS =  EFactoryImpl.HEX_DIGITS;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEByteArrayToString(EDataType eDataType, Object instanceValue)
  {
    if (instanceValue == null)
    {
      return null;
    }
    else
    {
      byte [] bytes = (byte[])instanceValue;
      return bytesToHexString(bytes, bytes.length);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Short createEShortFromString(EDataType metaObject, String initialValue) 
  {
    return initialValue == null ? null : Short.valueOf(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEShortToString(EDataType metaObject, Object instanceValue) 
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Class<?> createEJavaClassFromString(EDataType metaObject, String initialValue) 
  {
    return null;
    /*
    try
    {
      if (initialValue == null) return null;
      else if ("boolean".equals(initialValue)) return boolean.class;
      else if ("byte".equals(initialValue)) return byte.class;
      else if ("char".equals(initialValue)) return char.class;
      else if ("double".equals(initialValue)) return double.class;
      else if ("float".equals(initialValue)) return float.class;
      else if ("int".equals(initialValue)) return int.class;
      else if ("long".equals(initialValue)) return long.class;
      else if ("short".equals(initialValue)) return short.class;
      else return Class.forName(initialValue);
    }
    catch (ClassNotFoundException e)
    {
      throw new WrappedException(e);
    }
    */
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEJavaClassToString(EDataType metaObject, Object instanceValue)
  {
    return instanceValue == null ? "" : ((Class<?>)instanceValue).getName();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Object createEJavaObjectFromString(EDataType eDataType, String initialValue)
  {
    return createFromString(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEJavaObjectToString(EDataType eDataType, Object instanceValue)
  {
    return convertToString(instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Long createELongObjectFromString(EDataType metaObject, String initialValue) 
  {
    return initialValue == null ? null : Long.valueOf(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertELongObjectToString(EDataType metaObject, Object instanceValue) 
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Short createEShortObjectFromString(EDataType metaObject, String initialValue) 
  {
    return initialValue == null ? null : Short.valueOf(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEShortObjectToString(EDataType metaObject, Object instanceValue) 
  {
    return instanceValue == null ? null : instanceValue.toString();
  }
} //EcoreFactoryImpl
