/**
 * Copyright (c) 2003-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore.xml.namespace.impl;


import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.xml.namespace.*;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class XMLNamespaceFactoryImpl extends EFactoryImpl implements XMLNamespaceFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static XMLNamespaceFactory init()
  {
    try
    {
      XMLNamespaceFactory theXMLNamespaceFactory = (XMLNamespaceFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.w3.org/XML/1998/namespace"); 
      if (theXMLNamespaceFactory != null)
      {
        return theXMLNamespaceFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new XMLNamespaceFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XMLNamespaceFactoryImpl()
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
      case XMLNamespacePackage.XML_NAMESPACE_DOCUMENT_ROOT: return createXMLNamespaceDocumentRoot();
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
      case XMLNamespacePackage.SPACE_TYPE:
        return createSpaceTypeFromString(eDataType, initialValue);
      case XMLNamespacePackage.LANG_TYPE:
        return createLangTypeFromString(eDataType, initialValue);
      case XMLNamespacePackage.LANG_TYPE_NULL:
        return createLangTypeNullFromString(eDataType, initialValue);
      case XMLNamespacePackage.SPACE_TYPE_OBJECT:
        return createSpaceTypeObjectFromString(eDataType, initialValue);
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
      case XMLNamespacePackage.SPACE_TYPE:
        return convertSpaceTypeToString(eDataType, instanceValue);
      case XMLNamespacePackage.LANG_TYPE:
        return convertLangTypeToString(eDataType, instanceValue);
      case XMLNamespacePackage.LANG_TYPE_NULL:
        return convertLangTypeNullToString(eDataType, instanceValue);
      case XMLNamespacePackage.SPACE_TYPE_OBJECT:
        return convertSpaceTypeObjectToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XMLNamespaceDocumentRoot createXMLNamespaceDocumentRoot()
  {
    XMLNamespaceDocumentRootImpl xmlNamespaceDocumentRoot = new XMLNamespaceDocumentRootImpl();
    return xmlNamespaceDocumentRoot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SpaceType createSpaceTypeFromString(EDataType eDataType, String initialValue)
  {
    SpaceType result = SpaceType.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertSpaceTypeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createLangTypeFromString(EDataType eDataType, String initialValue)
  {
    if (initialValue == null) return null;
    String result = null;
    RuntimeException exception = null;
    try
    {
      result = (String)XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.Literals.LANGUAGE, initialValue);
      if (result != null && Diagnostician.INSTANCE.validate(eDataType, result, null, null))
      {
        return result;
      }
    }
    catch (RuntimeException e)
    {
      exception = e;
    }
    try
    {
      result = createLangTypeNullFromString(XMLNamespacePackage.Literals.LANG_TYPE_NULL, initialValue);
      if (result != null && Diagnostician.INSTANCE.validate(eDataType, result, null, null))
      {
        return result;
      }
    }
    catch (RuntimeException e)
    {
      exception = e;
    }
    if (result != null || exception == null) return result;
    
    throw exception;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertLangTypeToString(EDataType eDataType, Object instanceValue)
  {
    if (instanceValue == null) return null;
    if (XMLTypePackage.Literals.LANGUAGE.isInstance(instanceValue))
    {
      try
      {
        String value = XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.Literals.LANGUAGE, instanceValue);
        if (value != null) return value;
      }
      catch (Exception e)
      {
        // Keep trying other member types until all have failed.
      }
    }
    if (XMLNamespacePackage.Literals.LANG_TYPE_NULL.isInstance(instanceValue))
    {
      try
      {
        String value = convertLangTypeNullToString(XMLNamespacePackage.Literals.LANG_TYPE_NULL, instanceValue);
        if (value != null) return value;
      }
      catch (Exception e)
      {
        // Keep trying other member types until all have failed.
      }
    }
    throw new IllegalArgumentException("Invalid value: '"+instanceValue+"' for datatype :"+eDataType.getName());
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createLangTypeNullFromString(EDataType eDataType, String initialValue)
  {
    return (String)XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.Literals.STRING, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertLangTypeNullToString(EDataType eDataType, Object instanceValue)
  {
    return XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.Literals.STRING, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SpaceType createSpaceTypeObjectFromString(EDataType eDataType, String initialValue)
  {
    return createSpaceTypeFromString(XMLNamespacePackage.Literals.SPACE_TYPE, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertSpaceTypeObjectToString(EDataType eDataType, Object instanceValue)
  {
    return convertSpaceTypeToString(XMLNamespacePackage.Literals.SPACE_TYPE, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XMLNamespacePackage getXMLNamespacePackage()
  {
    return (XMLNamespacePackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static XMLNamespacePackage getPackage()
  {
    return XMLNamespacePackage.eINSTANCE;
  }

} //XMLNamespaceFactoryImpl
