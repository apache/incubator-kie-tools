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
package org.eclipse.emf.ecore.xml.type.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.xml.type.*;
import org.eclipse.emf.ecore.xml.type.internal.DataValue.Base64;
import org.eclipse.emf.ecore.xml.type.internal.DataValue.HexBin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class XMLTypeFactoryImpl extends EFactoryImpl implements XMLTypeFactory
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Object createAnySimpleType(String literal)
  {
    return literal;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertAnySimpleType(Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT 
   */
  public String createAnyURI(String literal)
  {
    // Per Schema 1.0 it is not clear if the result returned should be a valid URI. 
    // For the future if we plant to support IRIs then it is better not to massage
    // the initialValue. 
    // We should thought consider where would be the best way to validate anyURI values -- EL
    
    /*initialValue = collapseWhiteSpace(initialValue);
    if (initialValue != null)
    {
      //encode special characters using XLink 5.4 algorithm
      initialValue = URI.encode(initialValue);
      // Support for relative URLs
      // According to Java 1.1: URLs may also be specified with a
      // String and the URL object that it is related to.
      try 
      {
        new URI(URI.BASE_URI, initialValue);
      }
      catch (URI.MalformedURIException e)
      {
        throw new InvalidDatatypeValueException("Invalid anyURI value: '"+initialValue+"' :"+e.toString());
      }
    }*/
    return literal;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertAnyURI(String instanceValue)
  {
    return instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public byte[] createBase64Binary(String literal)
  {
    if (literal == null) return null;
    byte[] value = Base64.decode(collapseWhiteSpace(literal));
    if (value == null)
    {
      throw new InvalidDatatypeValueException("Invalid base64Binary value: '" + literal + "'");
    }
    return value;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertBase64Binary(byte[] instanceValue)
  {
    return instanceValue == null ? null : Base64.encode(instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean createBoolean(String initialValue)
  {
    return initialValue == null ? false : primitiveBooleanValueOf(initialValue);	
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertBoolean(boolean instanceValue)
  {
    return instanceValue ? "true" : "false";
  }	
    
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Boolean createBooleanObject(String literal)
  {
    return literal == null ? null : booleanValueOf(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertBooleanObject(Boolean instanceValue)
  {
    return instanceValue == null ? null : convertBoolean(instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public byte createByte(String literal)
  {
    return literal == null ? 0 : Byte.parseByte(collapseWhiteSpaceAndLeadingPlus(literal));
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertByte(byte instanceValue)
  {
    return Byte.toString(instanceValue);
  }
    
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Byte createByteObject(String literal)
  {
    return literal == null ? null : Byte.valueOf(collapseWhiteSpaceAndLeadingPlus(literal));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertByteObject(Byte instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public double createDouble(String literal)
  {
    if (literal == null)
    {
      return 0.0;
    }
    else
    {
      String normalizedLiteral = collapseWhiteSpace(literal);
      if (normalizedLiteral.endsWith("INF"))
      {
        int length = normalizedLiteral.length();
        if (length == 4)
        {
          char ch = normalizedLiteral.charAt(0);
          if (ch == '+')
          {
            return Double.POSITIVE_INFINITY;
          }
          else if (ch == '-')
          {
            return Double.NEGATIVE_INFINITY;
          }
        }
        else if (length == 3)
        {
          return Double.POSITIVE_INFINITY;
        }
      }
      return Double.parseDouble(normalizedLiteral);
    }
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertDouble(double instanceValue)
  {
    return instanceValue == Double.POSITIVE_INFINITY ? "INF" : instanceValue == Double.NEGATIVE_INFINITY ? "-INF" : Double.toString(instanceValue);
  }	
    
  private static final Double DOUBLE_POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
  private static final Double DOUBLE_NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Double createDoubleObject(String literal)
  {
    if (literal == null)
    {
      return null;
    }
    else
    {
      String normalizedLiteral = collapseWhiteSpace(literal);
      if (normalizedLiteral.endsWith("INF"))
      {
        int length = normalizedLiteral.length();
        if (length == 4)
        {
          char ch = normalizedLiteral.charAt(0);
          if (ch == '+')
          {
            return DOUBLE_POSITIVE_INFINITY;
          }
          else if (ch == '-')
          {
            return DOUBLE_NEGATIVE_INFINITY;
          }
        }
        else if (length == 3)
        {
          return DOUBLE_POSITIVE_INFINITY;
        }
      }
      return Double.valueOf(normalizedLiteral);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertDoubleObject(Double instanceValue)
  {
    return instanceValue == null ? null : convertDouble(instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public List<String> createENTITIES(String literal)
  {
    return createENTITIESBase(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List<String> createENTITIESFromString(EDataType eDataType, String initialValue)
  {
    return createENTITIESBaseFromString(XMLTypePackage.Literals.ENTITIES_BASE, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertENTITIES(List<? extends String> instanceValue)
  {
    return convertENTITIESBase(instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertENTITIESToString(EDataType eDataType, Object instanceValue)
  {
    return convertENTITIESBaseToString(XMLTypePackage.Literals.ENTITIES_BASE, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List<String> createENTITIESBase(String literal)
  {
    if (literal == null) return null;
    List<String> result = new ArrayList<String>();
    for (String item : split(literal))
    {
      result.add(createENTITY(item));
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List<String> createENTITIESBaseFromString(EDataType eDataType, String initialValue)
  {
    return createENTITIESBase(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertENTITIESBase(List<? extends String> instanceValue)
  {
    if (instanceValue == null) return null;
    if (instanceValue.isEmpty()) return "";
    StringBuffer result = new StringBuffer();
    for (Object item : instanceValue)
    {
      result.append(convertENTITY((String)item));
      result.append(' ');
    }
    return result.substring(0, result.length() - 1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertENTITIESBaseToString(EDataType eDataType, Object instanceValue)
  {
    if (instanceValue == null) return null;
    List<?> list = (List<?>)instanceValue;
    if (list.isEmpty()) return "";
    StringBuffer result = new StringBuffer();
    for (Iterator<?> i = list.iterator(); i.hasNext(); )
    {
      result.append(convertENTITYToString(XMLTypePackage.Literals.ENTITY, i.next()));
      result.append(' ');
    }
    return result.substring(0, result.length() - 1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createENTITY(String literal)
  {
    return collapseWhiteSpace(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertENTITY(String instanceValue)
  {
    return instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public float createFloat(String literal)
  {
    if (literal == null)
    {
      return 0.0F;
    }
    else
    {
      String normalizedLiteral = collapseWhiteSpace(literal);
      if (normalizedLiteral.endsWith("INF"))
      {
        int length = normalizedLiteral.length();
        if (length == 4)
        {
          char ch = normalizedLiteral.charAt(0);
          if (ch == '+')
          {
            return Float.POSITIVE_INFINITY;
          }
          else if (ch == '-')
          {
            return Float.NEGATIVE_INFINITY;
          }
        }
        else if (length == 3)
        {
          return Float.POSITIVE_INFINITY;
        }
      }
      return Float.parseFloat(normalizedLiteral);
    }
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertFloat(float instanceValue)
  {
    return instanceValue == Float.POSITIVE_INFINITY ? "INF" : instanceValue == Float.NEGATIVE_INFINITY ? "-INF" : Float.toString(instanceValue);
  }	
    
  private static final Float FLOAT_POSITIVE_INFINITY = Float.POSITIVE_INFINITY;
  private static final Float FLOAT_NEGATIVE_INFINITY = Float.NEGATIVE_INFINITY;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Float createFloatObject(String literal)
  {
    if (literal == null)
    {
      return null;
    }
    else
    {
      String normalizedLiteral = collapseWhiteSpace(literal);
      if (normalizedLiteral.endsWith("INF"))
      {
        int length = normalizedLiteral.length();
        if (length == 4)
        {
          char ch = normalizedLiteral.charAt(0);
          if (ch == '+')
          {
            return FLOAT_POSITIVE_INFINITY;
          }
          else if (ch == '-')
          {
            return FLOAT_NEGATIVE_INFINITY;
          }
        }
        else if (length == 3)
        {
          return FLOAT_POSITIVE_INFINITY;
        }
      }
      return Float.valueOf(normalizedLiteral);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertFloatObject(Float instanceValue)
  {
    return instanceValue == null ? null : convertFloat(instanceValue);
  }

  public String convertGDay(Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }


  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public byte[] createHexBinary(String literal)
  {
    if (literal == null) return null;
    byte[] value = HexBin.decode(collapseWhiteSpace(literal));
    if (value == null)
    {
      throw new InvalidDatatypeValueException("Invalid hexBinary value: '" + literal + "'");
    }
    return value;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertHexBinary(byte[] instanceValue)
  {
    return instanceValue == null ? null : HexBin.encode(instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createID(String literal)
  {
    return collapseWhiteSpace(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createIDFromString(EDataType eDataType, String initialValue)
  {
    return createID(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertID(String instanceValue)
  {
    return instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createIDREF(String literal)
  {
    return collapseWhiteSpace(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createIDREFFromString(EDataType eDataType, String initialValue)
  {
    return createIDREF(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertIDREF(String instanceValue)
  {
    return instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public List<String> createIDREFS(String literal)
  {
    return createIDREFSBase(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List<String> createIDREFSFromString(EDataType eDataType, String initialValue)
  {
    return createIDREFSBaseFromString(XMLTypePackage.Literals.IDREFS_BASE, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertIDREFS(List<? extends String> instanceValue)
  {
    return convertIDREFSBase(instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertIDREFSToString(EDataType eDataType, Object instanceValue)
  {
    return convertIDREFSBaseToString(XMLTypePackage.Literals.IDREFS_BASE, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List<String> createIDREFSBase(String literal)
  {
    if (literal == null) return null;
    List<String> result = new ArrayList<String>();
    for (String item : split(literal))
    {
      result.add(createIDREF(item));
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List<String> createIDREFSBaseFromString(EDataType eDataType, String initialValue)
  {
    return createIDREFSBase(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertIDREFSBase(List<? extends String> instanceValue)
  {
    if (instanceValue == null) return null;
    if (instanceValue.isEmpty()) return "";
    StringBuffer result = new StringBuffer();
    for (Object item : instanceValue)
    {
      result.append(convertIDREF((String)item));
      result.append(' ');
    }
    return result.substring(0, result.length() - 1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public String convertIDREFSBaseToString(EDataType eDataType, Object instanceValue)
  {
    return convertIDREFSBase((List<? extends String>)instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public int createInt(String initialValue)
  {
    return initialValue == null ? 0 : Integer.parseInt(collapseWhiteSpaceAndLeadingPlus(initialValue));
  }	

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertInt(int instanceValue)
  {
    return Integer.toString(instanceValue);
  }
    
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Integer createIntObject(String literal)
  {
    return literal == null ? null : Integer.valueOf(collapseWhiteSpaceAndLeadingPlus(literal));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertIntObject(Integer instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createLanguage(String literal)
  {
    return collapseWhiteSpace(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertLanguage(String instanceValue)
  {
    return instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public long createLong(String literal)
  {
    return literal == null ? 0L : Long.parseLong(collapseWhiteSpaceAndLeadingPlus(literal));
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertLong(long instanceValue)
  {
    return Long.toString(instanceValue);
  }
    
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Long createLongObject(String literal)
  {
    return literal == null ? null : Long.valueOf(collapseWhiteSpaceAndLeadingPlus(literal));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertLongObject(Long instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createName(String literal)
  {
    return collapseWhiteSpace(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertName(String instanceValue)
  {
    return instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createNCName(String literal)
  {
    return collapseWhiteSpace(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertNCName(String instanceValue)
  {
    return instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertNegativeInteger(String instanceValue)
  {
    return convertNonPositiveInteger(instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertNegativeIntegerToString(EDataType eDataType, Object instanceValue)
  {
    return convertNonPositiveIntegerToString(XMLTypePackage.Literals.NON_POSITIVE_INTEGER, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createNMTOKEN(String literal)
  {
    return collapseWhiteSpace(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertNMTOKEN(String instanceValue)
  {
    return instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List<String> createNMTOKENS(String literal)
  {
    return createNMTOKENSBase(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List<String> createNMTOKENSFromString(EDataType eDataType, String initialValue)
  {
    return createNMTOKENSBaseFromString(XMLTypePackage.Literals.NMTOKENS_BASE, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertNMTOKENS(List<? extends String> instanceValue)
  {
    return convertNMTOKENSBase(instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertNMTOKENSToString(EDataType eDataType, Object instanceValue)
  {
    return convertNMTOKENSBaseToString(XMLTypePackage.Literals.NMTOKENS_BASE, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List<String> createNMTOKENSBase(String literal)
  {
    if (literal == null) return null;
    List<String> result = new ArrayList<String>();
    for (String item : split(literal))
    {
      result.add(createNMTOKEN(item));
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List<String> createNMTOKENSBaseFromString(EDataType eDataType, String initialValue)
  {
    return createNMTOKENSBase(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertNMTOKENSBase(List<? extends String> instanceValue)
  {
    if (instanceValue == null) return null;
    if (instanceValue.isEmpty()) return "";
    StringBuffer result = new StringBuffer();
    for (Object item : instanceValue)
    {
      result.append(convertNMTOKEN((String)item));
      result.append(' ');
    }
    return result.substring(0, result.length() - 1);
  }

  public String convertNMTOKENSBaseToString(EDataType eDataType, Object instanceValue)
  {
    if (instanceValue == null) return null;
    List<?> list = (List<?>)instanceValue;
    if (list.isEmpty()) return "";
    StringBuffer result = new StringBuffer();
    for (Iterator<?> i = list.iterator(); i.hasNext(); )
    {
      result.append(convertNMTOKENToString(XMLTypePackage.Literals.NMTOKEN, i.next()));
      result.append(' ');
    }
    return result.substring(0, result.length() - 1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createNonNegativeInteger(String literal)
  {
    return createInteger(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createNonNegativeIntegerFromString(EDataType eDataType, String initialValue)
  {
    return createIntegerFromString(XMLTypePackage.Literals.INTEGER, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createNormalizedString(String literal)
  {
    return replaceWhiteSpace(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertNormalizedString(String instanceValue)
  {
    return instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createPositiveInteger(String literal)
  {
    return createNonNegativeInteger(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createPositiveIntegerFromString(EDataType eDataType, String initialValue)
  {
    return createNonNegativeIntegerFromString(XMLTypePackage.Literals.NON_NEGATIVE_INTEGER, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertPositiveInteger(String instanceValue)
  {
    return convertNonNegativeInteger(instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertPositiveIntegerToString(EDataType eDataType, Object instanceValue)
  {
    return convertNonNegativeIntegerToString(XMLTypePackage.Literals.NON_NEGATIVE_INTEGER, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createQName(String literal)
  {
    return (String)super.createFromString(XMLTypePackage.Literals.QNAME, literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public short createShort(String literal)
  {
    return literal == null ? 0 : Short.parseShort(collapseWhiteSpaceAndLeadingPlus(literal));
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertShort(short instanceValue)
  {
    return Short.toString(instanceValue);
  }	
    
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Short createShortObject(String literal)
  {
    return literal == null ? null : Short.valueOf(collapseWhiteSpaceAndLeadingPlus(literal));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertShortObject(Short instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createString(String initialValue)
  {	
    return initialValue;
  }	

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertString(String instanceValue)
  {
    return instanceValue;	
  }	
  

  @Deprecated
  public String convertTime(Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createToken(String literal)
  {
    return collapseWhiteSpace(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertToken(String instanceValue)
  {
    return instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public short createUnsignedByte(String literal)
  {
    return literal == null ? 0 : Short.parseShort(collapseWhiteSpaceAndLeadingPlus(literal));
  }	
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertUnsignedByte(short instanceValue)
  {
    return Short.toString(instanceValue);
  }	
    
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Short createUnsignedByteObject(String literal)
  {
    return literal == null ? null : Short.valueOf(collapseWhiteSpaceAndLeadingPlus(literal));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertUnsignedByteObject(Short instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public long createUnsignedInt(String literal)
  {
    return literal == null ? 0 : Long.parseLong(collapseWhiteSpaceAndLeadingPlus(literal));
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertUnsignedInt(long instanceValue)
  {
    return Long.toString(instanceValue);
  }	
    
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Long createUnsignedIntObject(String literal)
  {
    return literal == null ? null : Long.valueOf(collapseWhiteSpaceAndLeadingPlus(literal));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertUnsignedIntObject(Long instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createUnsignedLong(String literal)
  {
    return createNonNegativeInteger(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createUnsignedLongFromString(EDataType eDataType, String initialValue)
  {
    return createNonNegativeIntegerFromString(XMLTypePackage.Literals.NON_NEGATIVE_INTEGER, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertUnsignedLong(String instanceValue)
  {
    return convertNonNegativeInteger(instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertUnsignedLongToString(EDataType eDataType, Object instanceValue)
  {
    return convertNonNegativeIntegerToString(XMLTypePackage.Literals.NON_NEGATIVE_INTEGER, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public int createUnsignedShort(String literal)
  {
    return literal == null ? 0 : Integer.parseInt(collapseWhiteSpaceAndLeadingPlus(literal));
  }	
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertUnsignedShort(int instanceValue)
  {
    return Integer.toString(instanceValue);
  }	
    
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Integer createUnsignedShortObject(String literal)
  {
    return literal == null ? null : Integer.valueOf(collapseWhiteSpaceAndLeadingPlus(literal));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertUnsignedShortObject(Integer instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static XMLTypeFactory init()
  {
    try
    {
      XMLTypeFactory theXMLTypeFactory = (XMLTypeFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/emf/2003/XMLType"); 
      if (theXMLTypeFactory != null)
      {
        return theXMLTypeFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new XMLTypeFactoryImpl();
  }

  /*
  protected static final DateTimeFormat [] EDATE_FORMATS;
  static 
  {
    DateTimeFormat[] result;
    try
    {
      result=
        new DateTimeFormat[] 
        {
          DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ssZ"),
          DateTimeFormat.getFormat("yyyy-MM-ddZ")
        };
    }
    catch (Throwable exception)
    {
      result = new DateTimeFormat[0];
    }
    EDATE_FORMATS = result;
  }
  */

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XMLTypeFactoryImpl()
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
      case XMLTypePackage.ANY_TYPE: return createAnyType();
      case XMLTypePackage.PROCESSING_INSTRUCTION: return createProcessingInstruction();
      case XMLTypePackage.SIMPLE_ANY_TYPE: return createSimpleAnyType();
      case XMLTypePackage.XML_TYPE_DOCUMENT_ROOT: return createXMLTypeDocumentRoot();
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
      case XMLTypePackage.ANY_SIMPLE_TYPE:
        return createAnySimpleTypeFromString(eDataType, initialValue);
      case XMLTypePackage.ANY_URI:
        return createAnyURIFromString(eDataType, initialValue);
      case XMLTypePackage.BASE64_BINARY:
        return createBase64BinaryFromString(eDataType, initialValue);
      case XMLTypePackage.BOOLEAN:
        return createBooleanFromString(eDataType, initialValue);
      case XMLTypePackage.BOOLEAN_OBJECT:
        return createBooleanObjectFromString(eDataType, initialValue);
      case XMLTypePackage.BYTE:
        return createByteFromString(eDataType, initialValue);
      case XMLTypePackage.BYTE_OBJECT:
        return createByteObjectFromString(eDataType, initialValue);
      case XMLTypePackage.DATE:
        return createDateFromString(eDataType, initialValue);
      case XMLTypePackage.DATE_TIME:
        return createDateTimeFromString(eDataType, initialValue);
      case XMLTypePackage.DECIMAL:
        return createDecimalFromString(eDataType, initialValue);
      case XMLTypePackage.DOUBLE:
        return createDoubleFromString(eDataType, initialValue);
      case XMLTypePackage.DOUBLE_OBJECT:
        return createDoubleObjectFromString(eDataType, initialValue);
      case XMLTypePackage.DURATION:
        return createDurationFromString(eDataType, initialValue);
      case XMLTypePackage.ENTITIES:
        return createENTITIESFromString(eDataType, initialValue);
      case XMLTypePackage.ENTITIES_BASE:
        return createENTITIESBaseFromString(eDataType, initialValue);
      case XMLTypePackage.ENTITY:
        return createENTITYFromString(eDataType, initialValue);
      case XMLTypePackage.FLOAT:
        return createFloatFromString(eDataType, initialValue);
      case XMLTypePackage.FLOAT_OBJECT:
        return createFloatObjectFromString(eDataType, initialValue);
      case XMLTypePackage.GDAY:
        return createGDayFromString(eDataType, initialValue);
      case XMLTypePackage.GMONTH:
        return createGMonthFromString(eDataType, initialValue);
      case XMLTypePackage.GMONTH_DAY:
        return createGMonthDayFromString(eDataType, initialValue);
      case XMLTypePackage.GYEAR:
        return createGYearFromString(eDataType, initialValue);
      case XMLTypePackage.GYEAR_MONTH:
        return createGYearMonthFromString(eDataType, initialValue);
      case XMLTypePackage.HEX_BINARY:
        return createHexBinaryFromString(eDataType, initialValue);
      case XMLTypePackage.ID:
        return createIDFromString(eDataType, initialValue);
      case XMLTypePackage.IDREF:
        return createIDREFFromString(eDataType, initialValue);
      case XMLTypePackage.IDREFS:
        return createIDREFSFromString(eDataType, initialValue);
      case XMLTypePackage.IDREFS_BASE:
        return createIDREFSBaseFromString(eDataType, initialValue);
      case XMLTypePackage.INT:
        return createIntFromString(eDataType, initialValue);
      case XMLTypePackage.INTEGER:
        return createIntegerFromString(eDataType, initialValue);
      case XMLTypePackage.INT_OBJECT:
        return createIntObjectFromString(eDataType, initialValue);
      case XMLTypePackage.LANGUAGE:
        return createLanguageFromString(eDataType, initialValue);
      case XMLTypePackage.LONG:
        return createLongFromString(eDataType, initialValue);
      case XMLTypePackage.LONG_OBJECT:
        return createLongObjectFromString(eDataType, initialValue);
      case XMLTypePackage.NAME:
        return createNameFromString(eDataType, initialValue);
      case XMLTypePackage.NC_NAME:
        return createNCNameFromString(eDataType, initialValue);
      case XMLTypePackage.NEGATIVE_INTEGER:
        return createNegativeIntegerFromString(eDataType, initialValue);
      case XMLTypePackage.NMTOKEN:
        return createNMTOKENFromString(eDataType, initialValue);
      case XMLTypePackage.NMTOKENS:
        return createNMTOKENSFromString(eDataType, initialValue);
      case XMLTypePackage.NMTOKENS_BASE:
        return createNMTOKENSBaseFromString(eDataType, initialValue);
      case XMLTypePackage.NON_NEGATIVE_INTEGER:
        return createNonNegativeIntegerFromString(eDataType, initialValue);
      case XMLTypePackage.NON_POSITIVE_INTEGER:
        return createNonPositiveIntegerFromString(eDataType, initialValue);
      case XMLTypePackage.NORMALIZED_STRING:
        return createNormalizedStringFromString(eDataType, initialValue);
      case XMLTypePackage.NOTATION:
        return createNOTATIONFromString(eDataType, initialValue);
      case XMLTypePackage.POSITIVE_INTEGER:
        return createPositiveIntegerFromString(eDataType, initialValue);
      case XMLTypePackage.QNAME:
        return createQNameFromString(eDataType, initialValue);
      case XMLTypePackage.SHORT:
        return createShortFromString(eDataType, initialValue);
      case XMLTypePackage.SHORT_OBJECT:
        return createShortObjectFromString(eDataType, initialValue);
      case XMLTypePackage.STRING:
        return createStringFromString(eDataType, initialValue);
      case XMLTypePackage.TIME:
        return createTimeFromString(eDataType, initialValue);
      case XMLTypePackage.TOKEN:
        return createTokenFromString(eDataType, initialValue);
      case XMLTypePackage.UNSIGNED_BYTE:
        return createUnsignedByteFromString(eDataType, initialValue);
      case XMLTypePackage.UNSIGNED_BYTE_OBJECT:
        return createUnsignedByteObjectFromString(eDataType, initialValue);
      case XMLTypePackage.UNSIGNED_INT:
        return createUnsignedIntFromString(eDataType, initialValue);
      case XMLTypePackage.UNSIGNED_INT_OBJECT:
        return createUnsignedIntObjectFromString(eDataType, initialValue);
      case XMLTypePackage.UNSIGNED_LONG:
        return createUnsignedLongFromString(eDataType, initialValue);
      case XMLTypePackage.UNSIGNED_SHORT:
        return createUnsignedShortFromString(eDataType, initialValue);
      case XMLTypePackage.UNSIGNED_SHORT_OBJECT:
        return createUnsignedShortObjectFromString(eDataType, initialValue);
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
      case XMLTypePackage.ANY_SIMPLE_TYPE:
        return convertAnySimpleTypeToString(eDataType, instanceValue);
      case XMLTypePackage.ANY_URI:
        return convertAnyURIToString(eDataType, instanceValue);
      case XMLTypePackage.BASE64_BINARY:
        return convertBase64BinaryToString(eDataType, instanceValue);
      case XMLTypePackage.BOOLEAN:
        return convertBooleanToString(eDataType, instanceValue);
      case XMLTypePackage.BOOLEAN_OBJECT:
        return convertBooleanObjectToString(eDataType, instanceValue);
      case XMLTypePackage.BYTE:
        return convertByteToString(eDataType, instanceValue);
      case XMLTypePackage.BYTE_OBJECT:
        return convertByteObjectToString(eDataType, instanceValue);
      case XMLTypePackage.DATE:
        return convertDateToString(eDataType, instanceValue);
      case XMLTypePackage.DATE_TIME:
        return convertDateTimeToString(eDataType, instanceValue);
      case XMLTypePackage.DECIMAL:
        return convertDecimalToString(eDataType, instanceValue);
      case XMLTypePackage.DOUBLE:
        return convertDoubleToString(eDataType, instanceValue);
      case XMLTypePackage.DOUBLE_OBJECT:
        return convertDoubleObjectToString(eDataType, instanceValue);
      case XMLTypePackage.DURATION:
        return convertDurationToString(eDataType, instanceValue);
      case XMLTypePackage.ENTITIES:
        return convertENTITIESToString(eDataType, instanceValue);
      case XMLTypePackage.ENTITIES_BASE:
        return convertENTITIESBaseToString(eDataType, instanceValue);
      case XMLTypePackage.ENTITY:
        return convertENTITYToString(eDataType, instanceValue);
      case XMLTypePackage.FLOAT:
        return convertFloatToString(eDataType, instanceValue);
      case XMLTypePackage.FLOAT_OBJECT:
        return convertFloatObjectToString(eDataType, instanceValue);
      case XMLTypePackage.GDAY:
        return convertGDayToString(eDataType, instanceValue);
      case XMLTypePackage.GMONTH:
        return convertGMonthToString(eDataType, instanceValue);
      case XMLTypePackage.GMONTH_DAY:
        return convertGMonthDayToString(eDataType, instanceValue);
      case XMLTypePackage.GYEAR:
        return convertGYearToString(eDataType, instanceValue);
      case XMLTypePackage.GYEAR_MONTH:
        return convertGYearMonthToString(eDataType, instanceValue);
      case XMLTypePackage.HEX_BINARY:
        return convertHexBinaryToString(eDataType, instanceValue);
      case XMLTypePackage.ID:
        return convertIDToString(eDataType, instanceValue);
      case XMLTypePackage.IDREF:
        return convertIDREFToString(eDataType, instanceValue);
      case XMLTypePackage.IDREFS:
        return convertIDREFSToString(eDataType, instanceValue);
      case XMLTypePackage.IDREFS_BASE:
        return convertIDREFSBaseToString(eDataType, instanceValue);
      case XMLTypePackage.INT:
        return convertIntToString(eDataType, instanceValue);
      case XMLTypePackage.INTEGER:
        return convertIntegerToString(eDataType, instanceValue);
      case XMLTypePackage.INT_OBJECT:
        return convertIntObjectToString(eDataType, instanceValue);
      case XMLTypePackage.LANGUAGE:
        return convertLanguageToString(eDataType, instanceValue);
      case XMLTypePackage.LONG:
        return convertLongToString(eDataType, instanceValue);
      case XMLTypePackage.LONG_OBJECT:
        return convertLongObjectToString(eDataType, instanceValue);
      case XMLTypePackage.NAME:
        return convertNameToString(eDataType, instanceValue);
      case XMLTypePackage.NC_NAME:
        return convertNCNameToString(eDataType, instanceValue);
      case XMLTypePackage.NEGATIVE_INTEGER:
        return convertNegativeIntegerToString(eDataType, instanceValue);
      case XMLTypePackage.NMTOKEN:
        return convertNMTOKENToString(eDataType, instanceValue);
      case XMLTypePackage.NMTOKENS:
        return convertNMTOKENSToString(eDataType, instanceValue);
      case XMLTypePackage.NMTOKENS_BASE:
        return convertNMTOKENSBaseToString(eDataType, instanceValue);
      case XMLTypePackage.NON_NEGATIVE_INTEGER:
        return convertNonNegativeIntegerToString(eDataType, instanceValue);
      case XMLTypePackage.NON_POSITIVE_INTEGER:
        return convertNonPositiveIntegerToString(eDataType, instanceValue);
      case XMLTypePackage.NORMALIZED_STRING:
        return convertNormalizedStringToString(eDataType, instanceValue);
      case XMLTypePackage.NOTATION:
        return convertNOTATIONToString(eDataType, instanceValue);
      case XMLTypePackage.POSITIVE_INTEGER:
        return convertPositiveIntegerToString(eDataType, instanceValue);
      case XMLTypePackage.QNAME:
        return convertQNameToString(eDataType, instanceValue);
      case XMLTypePackage.SHORT:
        return convertShortToString(eDataType, instanceValue);
      case XMLTypePackage.SHORT_OBJECT:
        return convertShortObjectToString(eDataType, instanceValue);
      case XMLTypePackage.STRING:
        return convertStringToString(eDataType, instanceValue);
      case XMLTypePackage.TIME:
        return convertTimeToString(eDataType, instanceValue);
      case XMLTypePackage.TOKEN:
        return convertTokenToString(eDataType, instanceValue);
      case XMLTypePackage.UNSIGNED_BYTE:
        return convertUnsignedByteToString(eDataType, instanceValue);
      case XMLTypePackage.UNSIGNED_BYTE_OBJECT:
        return convertUnsignedByteObjectToString(eDataType, instanceValue);
      case XMLTypePackage.UNSIGNED_INT:
        return convertUnsignedIntToString(eDataType, instanceValue);
      case XMLTypePackage.UNSIGNED_INT_OBJECT:
        return convertUnsignedIntObjectToString(eDataType, instanceValue);
      case XMLTypePackage.UNSIGNED_LONG:
        return convertUnsignedLongToString(eDataType, instanceValue);
      case XMLTypePackage.UNSIGNED_SHORT:
        return convertUnsignedShortToString(eDataType, instanceValue);
      case XMLTypePackage.UNSIGNED_SHORT_OBJECT:
        return convertUnsignedShortObjectToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AnyType createAnyType()
  {
    AnyTypeImpl anyType = new AnyTypeImpl();
    return anyType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ProcessingInstruction createProcessingInstruction()
  {
    ProcessingInstructionImpl processingInstruction = new ProcessingInstructionImpl();
    return processingInstruction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SimpleAnyType createSimpleAnyType()
  {
    SimpleAnyTypeImpl simpleAnyType = new SimpleAnyTypeImpl();
    return simpleAnyType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XMLTypeDocumentRoot createXMLTypeDocumentRoot()
  {
    XMLTypeDocumentRootImpl xmlTypeDocumentRoot = new XMLTypeDocumentRootImpl();
    return xmlTypeDocumentRoot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Object createAnySimpleTypeFromString(EDataType eDataType, String initialValue)
  {
    return createAnySimpleType(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertAnySimpleTypeToString(EDataType eDataType, Object instanceValue)
  {
    return convertAnySimpleType(instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createAnyURIFromString(EDataType eDataType, String initialValue)
  {
    return createAnyURI(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertAnyURIToString(EDataType eDataType, Object instanceValue)
  {
    return convertAnyURI((String)instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public byte[] createBase64BinaryFromString(EDataType eDataType, String initialValue)
  {
    return createBase64Binary(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertBase64BinaryToString(EDataType eDataType, Object instanceValue)
  {
    return convertBase64Binary((byte[])instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Boolean createBooleanFromString(EDataType eDataType, String initialValue)
  {
    return createBooleanObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertBooleanToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Boolean createBooleanObjectFromString(EDataType eDataType, String initialValue)
  {
    return initialValue == null ? null : booleanValueOf(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertBooleanObjectToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertInteger(String instanceValue)
  {
    return super.convertToString(XMLTypePackage.Literals.INTEGER, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertIntegerToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Integer createIntObjectFromString(EDataType eDataType, String initialValue)
  {
    return createIntObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertIntObjectToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Long createLongFromString(EDataType eDataType, String initialValue)
  {
    return createLongObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertLongToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Long createLongObjectFromString(EDataType eDataType, String initialValue)
  {
    return createLongObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertLongObjectToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Integer createIntFromString(EDataType eDataType, String initialValue)
  {
    return createIntObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertIntToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createInteger(String literal)
  {
    return (String)super.createFromString(XMLTypePackage.Literals.INTEGER, literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createIntegerFromString(EDataType eDataType, String initialValue)
  {
    return (String)super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Short createShortFromString(EDataType eDataType, String initialValue)
  {
    return createShortObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertShortToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Short createShortObjectFromString(EDataType eDataType, String initialValue)
  {
    return createShortObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertShortObjectToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Byte createByteFromString(EDataType eDataType, String initialValue)
  {
    return createByteObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertByteToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Byte createByteObjectFromString(EDataType eDataType, String initialValue)
  {
    return createByteObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertByteObjectToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createDate(String literal)
  {
    return (String)super.createFromString(XMLTypePackage.Literals.DATE, literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Object createDateFromString(EDataType eDataType, String initialValue)
  {
    return createDate(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertDate(String instanceValue)
  {
    return super.convertToString(XMLTypePackage.Literals.DATE, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertDateToString(EDataType eDataType, Object instanceValue)
  {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createDateTime(String literal)
  {
    return (String)super.createFromString(XMLTypePackage.Literals.DATE_TIME, literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Object createDateTimeFromString(EDataType eDataType, String initialValue)
  {
    return createDateTime(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertDateTime(String instanceValue)
  {
    return super.convertToString(XMLTypePackage.Literals.DATE_TIME, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertDateTimeToString(EDataType eDataType, Object instanceValue)
  {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createDecimal(String literal)
  {
    return (String)super.createFromString(XMLTypePackage.Literals.DECIMAL, literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createDecimalFromString(EDataType eDataType, String initialValue)
  {
    return (String)super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertDecimal(String instanceValue)
  {
    return super.convertToString(XMLTypePackage.Literals.DECIMAL, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertDecimalToString(EDataType eDataType, Object instanceValue)
  {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createStringFromString(EDataType eDataType, String initialValue)
  {
    return initialValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertStringToString(EDataType eDataType, Object instanceValue)
  {
    return (String)instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createTime(String literal)
  {
    return (String)super.createFromString(XMLTypePackage.Literals.TIME, literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Double createDoubleFromString(EDataType eDataType, String initialValue)
  {
    return createDoubleObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertDoubleToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : convertDouble((Double)instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Double createDoubleObjectFromString(EDataType eDataType, String initialValue)
  {
    return createDoubleObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertDoubleObjectToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : convertDouble((Double)instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createDuration(String literal)
  {
    return (String)super.createFromString(XMLTypePackage.Literals.DURATION, literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Object createDurationFromString(EDataType eDataType, String initialValue)
  {
    return createDuration(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertDuration(String instanceValue)
  {
    return super.convertToString(XMLTypePackage.Literals.DURATION, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertDurationToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createNormalizedStringFromString(EDataType eDataType, String initialValue)
  {
    return createNormalizedString(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertNormalizedStringToString(EDataType eDataType, Object instanceValue)
  {    
    return (String)instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createNOTATION(String literal)
  {
    return (String)super.createFromString(XMLTypePackage.Literals.NOTATION, literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createTokenFromString(EDataType eDataType, String initialValue)
  {
    return createToken(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertTokenToString(EDataType eDataType, Object instanceValue)
  {
    return (String)instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createNameFromString(EDataType eDataType, String initialValue)
  {
    // do not validate on load. Check validity using Diagnostician.
    return createName(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertNameToString(EDataType eDataType, Object instanceValue)
  {
    return (String)instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createNCNameFromString(EDataType eDataType, String initialValue)
  {
    // do not validate on load. Check validity using Diagnostician.
    return createNCName(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertNCNameToString(EDataType eDataType, Object instanceValue)
  {
    return (String)instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createNegativeInteger(String literal)
  {
    return createNonPositiveInteger(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createNegativeIntegerFromString(EDataType eDataType, String initialValue)
  {
    return createNonPositiveIntegerFromString(XMLTypePackage.Literals.NON_POSITIVE_INTEGER, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createENTITYFromString(EDataType eDataType, String initialValue)
  {
    return createENTITY(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertENTITYToString(EDataType eDataType, Object instanceValue)
  {
    return (String)instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Float createFloatFromString(EDataType eDataType, String initialValue)
  {
    return createFloatObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertFloatToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : convertFloat((Float)instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Float createFloatObjectFromString(EDataType eDataType, String initialValue)
  {
    return createFloatObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertFloatObjectToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : convertFloat((Float)instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createGDay(String literal)
  {
    return (String)super.createFromString(XMLTypePackage.Literals.GDAY, literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Object createGDayFromString(EDataType eDataType, String initialValue)
  {
    return createGDay(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertGDay(String instanceValue)
  {
    return super.convertToString(XMLTypePackage.Literals.GDAY, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertGDayToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createGMonth(String literal)
  {
    return (String)super.createFromString(XMLTypePackage.Literals.GMONTH, literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Object createGMonthFromString(EDataType eDataType, String initialValue)
  {
    return createGMonth(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertGMonth(String instanceValue)
  {
    return super.convertToString(XMLTypePackage.Literals.GMONTH, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertGMonthToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createGMonthDay(String literal)
  {
    return (String)super.createFromString(XMLTypePackage.Literals.GMONTH_DAY, literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Object createGMonthDayFromString(EDataType eDataType, String initialValue)
  {
    return createGMonthDay(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertGMonthDay(String instanceValue)
  {
    return super.convertToString(XMLTypePackage.Literals.GMONTH_DAY, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertGMonthDayToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createGYear(String literal)
  {
    return (String)super.createFromString(XMLTypePackage.Literals.GYEAR, literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Object createGYearFromString(EDataType eDataType, String initialValue)
  {
    return createGYear(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertGYear(String instanceValue)
  {
    return super.convertToString(XMLTypePackage.Literals.GYEAR, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertGYearToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createGYearMonth(String literal)
  {
    return (String)super.createFromString(XMLTypePackage.Literals.GYEAR_MONTH, literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Object createGYearMonthFromString(EDataType eDataType, String initialValue)
  {
    return createGYearMonth(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertGYearMonth(String instanceValue)
  {
    return super.convertToString(XMLTypePackage.Literals.GYEAR_MONTH, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertGYearMonthToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public byte[] createHexBinaryFromString(EDataType eDataType, String initialValue)
  {
    return createHexBinary(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertHexBinaryToString(EDataType eDataType, Object instanceValue)
  {
    return convertHexBinary((byte[])instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertIDToString(EDataType eDataType, Object instanceValue)
  {
    return (String)instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertIDREFToString(EDataType eDataType, Object instanceValue)
  {
    return (String)instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createLanguageFromString(EDataType eDataType, String initialValue)
  {
    return createLanguage(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertLanguageToString(EDataType eDataType, Object instanceValue)
  {
    return (String)instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertNonPositiveInteger(String instanceValue)
  {
    return convertInteger(instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertNonPositiveIntegerToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createNMTOKENFromString(EDataType eDataType, String initialValue)
  {
    return createNMTOKEN(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertNMTOKENToString(EDataType eDataType, Object instanceValue)
  {
    return (String)instanceValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertNonNegativeInteger(String instanceValue)
  {
    return convertInteger(instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertNonNegativeIntegerToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createNonPositiveInteger(String literal)
  {
    return createInteger(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createNonPositiveIntegerFromString(EDataType eDataType, String initialValue)
  {
    return createIntegerFromString(XMLTypePackage.Literals.INTEGER, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Object createNOTATIONFromString(EDataType eDataType, String initialValue)
  {
    return createNOTATION(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertNOTATION(String instanceValue)
  {
    return super.convertToString(XMLTypePackage.Literals.NOTATION, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertNOTATIONToString(EDataType eDataType, Object instanceValue)
  {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Object createQNameFromString(EDataType eDataType, String initialValue)
  {
    return createQName(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertQName(String instanceValue)
  {
    return super.convertToString(XMLTypePackage.Literals.QNAME, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertQNameToString(EDataType eDataType, Object instanceValue)
  {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Object createTimeFromString(EDataType eDataType, String initialValue)
  {
    return createTime(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertTime(String instanceValue)
  {
    return super.convertToString(XMLTypePackage.Literals.TIME, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertTimeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Long createUnsignedIntFromString(EDataType eDataType, String initialValue)
  {
    return createUnsignedIntObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertUnsignedIntToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Long createUnsignedIntObjectFromString(EDataType eDataType, String initialValue)
  {
    return createUnsignedIntObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertUnsignedIntObjectToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Integer createUnsignedShortFromString(EDataType eDataType, String initialValue)
  {
    return createUnsignedShortObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertUnsignedShortToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Integer createUnsignedShortObjectFromString(EDataType eDataType, String initialValue)
  {
    return initialValue == null ? null : Integer.valueOf(collapseWhiteSpace(initialValue));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertUnsignedShortObjectToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Short createUnsignedByteFromString(EDataType eDataType, String initialValue)
  {
    return createUnsignedByteObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertUnsignedByteToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Short createUnsignedByteObjectFromString(EDataType eDataType, String initialValue)
  {
    return createUnsignedByteObject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertUnsignedByteObjectToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XMLTypePackage getXMLTypePackage()
  {
    return (XMLTypePackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static XMLTypePackage getPackage()
  {
    return XMLTypePackage.eINSTANCE;
  }

  protected Boolean booleanValueOf(String initialValue)
  {
    initialValue = collapseWhiteSpace(initialValue);
    if ("true".equals(initialValue) || "1".equals(initialValue))
    {
      return Boolean.TRUE;
    }
    else if ("false".equals(initialValue) || "0".equals(initialValue))
    {
      return Boolean.FALSE;
    }
    throw new InvalidDatatypeValueException("Invalid boolean value: '" + initialValue + "'");
  }
  
  protected boolean primitiveBooleanValueOf(String initialValue)
  {
    initialValue = collapseWhiteSpace(initialValue);
    if ("true".equals(initialValue) || "1".equals(initialValue))
    {
      return true;
    }
    else if ("false".equals(initialValue) || "0".equals(initialValue))
    {
      return false;
    }
    throw new InvalidDatatypeValueException("Invalid boolean value: '" + initialValue + "'");
  }

  private String collapseWhiteSpaceAndLeadingPlus(String value)
  {
    // All calls guard for null value already.
    //
    String result = super.collapseWhiteSpace(value);
    return result.length() > 0 && result.charAt(0) == '+' ? result.substring(1) : result;
  }

} //XMLTypeFactoryImpl
