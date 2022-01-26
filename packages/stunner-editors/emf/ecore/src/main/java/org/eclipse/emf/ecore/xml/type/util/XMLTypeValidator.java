/**
 * Copyright (c) 2004-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore.xml.type.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.emf.ecore.xml.type.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Validator</b> for the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.ecore.xml.type.XMLTypePackage
 * @generated
 */
public class XMLTypeValidator extends EObjectValidator
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final XMLTypeValidator INSTANCE = new XMLTypeValidator();

  /**
   * A constant for the {@link org.eclipse.emf.common.util.Diagnostic#getSource() source} of diagnostic {@link org.eclipse.emf.common.util.Diagnostic#getCode() codes} from this package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.common.util.Diagnostic#getSource()
   * @see org.eclipse.emf.common.util.Diagnostic#getCode()
   * @generated
   */
  public static final String DIAGNOSTIC_SOURCE = "org.eclipse.emf.ecore.xml.type";

  /**
   * A constant with a fixed name that can be used as the base value for additional hand written constants.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final int GENERATED_DIAGNOSTIC_CODE_COUNT = 0;
  
  public static final int WELL_FORMED_XML_GREGORIAN_CALENDAR = GENERATED_DIAGNOSTIC_CODE_COUNT;

  /**
   * A constant with a fixed name that can be used as the base value for additional hand written constants in a derived class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected static final int DIAGNOSTIC_CODE_COUNT = WELL_FORMED_XML_GREGORIAN_CALENDAR;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XMLTypeValidator()
  {
    super();
  }

  /**
   * Returns the package of this validator switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EPackage getEPackage()
  {
    return XMLTypePackage.eINSTANCE;
  }

  /**
   * Calls <code>validateXXX</code> for the corresponding classifier of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected boolean validate(int classifierID, Object value, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    switch (classifierID)
    {
      case XMLTypePackage.ANY_TYPE:
        return validateAnyType((AnyType)value, diagnostics, context);
      case XMLTypePackage.PROCESSING_INSTRUCTION:
        return validateProcessingInstruction((ProcessingInstruction)value, diagnostics, context);
      case XMLTypePackage.SIMPLE_ANY_TYPE:
        return validateSimpleAnyType((SimpleAnyType)value, diagnostics, context);
      case XMLTypePackage.XML_TYPE_DOCUMENT_ROOT:
        return validateXMLTypeDocumentRoot((XMLTypeDocumentRoot)value, diagnostics, context);
      case XMLTypePackage.ANY_SIMPLE_TYPE:
        return validateAnySimpleType(value, diagnostics, context);
      case XMLTypePackage.ANY_URI:
        return validateAnyURI((String)value, diagnostics, context);
      case XMLTypePackage.BASE64_BINARY:
        return validateBase64Binary((byte[])value, diagnostics, context);
      case XMLTypePackage.BOOLEAN:
        return validateBoolean((Boolean)value, diagnostics, context);
      case XMLTypePackage.BOOLEAN_OBJECT:
        return validateBooleanObject((Boolean)value, diagnostics, context);
      case XMLTypePackage.BYTE:
        return validateByte((Byte)value, diagnostics, context);
      case XMLTypePackage.BYTE_OBJECT:
        return validateByteObject((Byte)value, diagnostics, context);
      case XMLTypePackage.DATE:
        return validateDate((String)value, diagnostics, context);
      case XMLTypePackage.DATE_TIME:
        return validateDateTime((String)value, diagnostics, context);
      case XMLTypePackage.DECIMAL:
        return validateDecimal((String)value, diagnostics, context);
      case XMLTypePackage.DOUBLE:
        return validateDouble((Double)value, diagnostics, context);
      case XMLTypePackage.DOUBLE_OBJECT:
        return validateDoubleObject((Double)value, diagnostics, context);
      case XMLTypePackage.DURATION:
        return validateDuration((String)value, diagnostics, context);
      case XMLTypePackage.ENTITIES:
        return validateENTITIES((List<?>)value, diagnostics, context);
      case XMLTypePackage.ENTITIES_BASE:
        return validateENTITIESBase((List<?>)value, diagnostics, context);
      case XMLTypePackage.ENTITY:
        return validateENTITY((String)value, diagnostics, context);
      case XMLTypePackage.FLOAT:
        return validateFloat((Float)value, diagnostics, context);
      case XMLTypePackage.FLOAT_OBJECT:
        return validateFloatObject((Float)value, diagnostics, context);
      case XMLTypePackage.GDAY:
        return validateGDay((String)value, diagnostics, context);
      case XMLTypePackage.GMONTH:
        return validateGMonth((String)value, diagnostics, context);
      case XMLTypePackage.GMONTH_DAY:
        return validateGMonthDay((String)value, diagnostics, context);
      case XMLTypePackage.GYEAR:
        return validateGYear((String)value, diagnostics, context);
      case XMLTypePackage.GYEAR_MONTH:
        return validateGYearMonth((String)value, diagnostics, context);
      case XMLTypePackage.HEX_BINARY:
        return validateHexBinary((byte[])value, diagnostics, context);
      case XMLTypePackage.ID:
        return validateID((String)value, diagnostics, context);
      case XMLTypePackage.IDREF:
        return validateIDREF((String)value, diagnostics, context);
      case XMLTypePackage.IDREFS:
        return validateIDREFS((List<?>)value, diagnostics, context);
      case XMLTypePackage.IDREFS_BASE:
        return validateIDREFSBase((List<?>)value, diagnostics, context);
      case XMLTypePackage.INT:
        return validateInt((Integer)value, diagnostics, context);
      case XMLTypePackage.INTEGER:
        return validateInteger((String)value, diagnostics, context);
      case XMLTypePackage.INT_OBJECT:
        return validateIntObject((Integer)value, diagnostics, context);
      case XMLTypePackage.LANGUAGE:
        return validateLanguage((String)value, diagnostics, context);
      case XMLTypePackage.LONG:
        return validateLong((Long)value, diagnostics, context);
      case XMLTypePackage.LONG_OBJECT:
        return validateLongObject((Long)value, diagnostics, context);
      case XMLTypePackage.NAME:
        return validateName((String)value, diagnostics, context);
      case XMLTypePackage.NC_NAME:
        return validateNCName((String)value, diagnostics, context);
      case XMLTypePackage.NEGATIVE_INTEGER:
        return validateNegativeInteger((String)value, diagnostics, context);
      case XMLTypePackage.NMTOKEN:
        return validateNMTOKEN((String)value, diagnostics, context);
      case XMLTypePackage.NMTOKENS:
        return validateNMTOKENS((List<?>)value, diagnostics, context);
      case XMLTypePackage.NMTOKENS_BASE:
        return validateNMTOKENSBase((List<?>)value, diagnostics, context);
      case XMLTypePackage.NON_NEGATIVE_INTEGER:
        return validateNonNegativeInteger((String)value, diagnostics, context);
      case XMLTypePackage.NON_POSITIVE_INTEGER:
        return validateNonPositiveInteger((String)value, diagnostics, context);
      case XMLTypePackage.NORMALIZED_STRING:
        return validateNormalizedString((String)value, diagnostics, context);
      case XMLTypePackage.NOTATION:
        return validateNOTATION((String)value, diagnostics, context);
      case XMLTypePackage.POSITIVE_INTEGER:
        return validatePositiveInteger((String)value, diagnostics, context);
      case XMLTypePackage.QNAME:
        return validateQName((String)value, diagnostics, context);
      case XMLTypePackage.SHORT:
        return validateShort((Short)value, diagnostics, context);
      case XMLTypePackage.SHORT_OBJECT:
        return validateShortObject((Short)value, diagnostics, context);
      case XMLTypePackage.STRING:
        return validateString((String)value, diagnostics, context);
      case XMLTypePackage.TIME:
        return validateTime((String)value, diagnostics, context);
      case XMLTypePackage.TOKEN:
        return validateToken((String)value, diagnostics, context);
      case XMLTypePackage.UNSIGNED_BYTE:
        return validateUnsignedByte((Short)value, diagnostics, context);
      case XMLTypePackage.UNSIGNED_BYTE_OBJECT:
        return validateUnsignedByteObject((Short)value, diagnostics, context);
      case XMLTypePackage.UNSIGNED_INT:
        return validateUnsignedInt((Long)value, diagnostics, context);
      case XMLTypePackage.UNSIGNED_INT_OBJECT:
        return validateUnsignedIntObject((Long)value, diagnostics, context);
      case XMLTypePackage.UNSIGNED_LONG:
        return validateUnsignedLong((String)value, diagnostics, context);
      case XMLTypePackage.UNSIGNED_SHORT:
        return validateUnsignedShort((Integer)value, diagnostics, context);
      case XMLTypePackage.UNSIGNED_SHORT_OBJECT:
        return validateUnsignedShortObject((Integer)value, diagnostics, context);
      default:
        return true;
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateAnyType(AnyType anyType, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(anyType, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateProcessingInstruction(ProcessingInstruction processingInstruction, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(processingInstruction, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateSimpleAnyType(SimpleAnyType simpleAnyType, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(simpleAnyType, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateXMLTypeDocumentRoot(XMLTypeDocumentRoot xmlTypeDocumentRoot, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(xmlTypeDocumentRoot, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateAnySimpleType(Object anySimpleType, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateAnyURI(String anyURI, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateBase64Binary(byte[] base64Binary, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateBoolean(boolean boolean_, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateBooleanObject(Boolean booleanObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateByte(byte byte_, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateByteObject(Byte byteObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateDate(String date, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateDateTime(String dateTime, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }


  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateDecimal(String decimal, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateDouble(double double_, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateDoubleObject(Double doubleObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateDuration(String duration, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  @Deprecated
  public boolean validateDuration(Object duration, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateENTITIES(List<?> entities, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateENTITIESBase_ItemType(entities, diagnostics, context);
    if (result || diagnostics != null) result &= validateENTITIES_MinLength(entities, diagnostics, context);
    return result;
  }

  /**
   * Validates the MinLength constraint of '<em>ENTITIES</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateENTITIES_MinLength(List<?> entities, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    int length = entities.size();
    boolean result = length >= 1;
    if (!result && diagnostics != null)
      reportMinLengthViolation(XMLTypePackage.Literals.ENTITIES, entities, length, 1, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateENTITIESBase(List<?> entitiesBase, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateENTITIESBase_ItemType(entitiesBase, diagnostics, context);
    return result;
  }

  /**
   * Validates the ItemType constraint of '<em>ENTITIES Base</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateENTITIESBase_ItemType(List<?> entitiesBase, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    for (Iterator<?> i = entitiesBase.iterator(); i.hasNext() && (result || diagnostics != null); )
    {
      Object item = i.next();
      if (XMLTypePackage.Literals.ENTITY.isInstance(item))
      {
        result &= validateENTITY((String)item, diagnostics, context);
      }
      else
      {
        result = false;
        reportDataValueTypeViolation(XMLTypePackage.Literals.ENTITY, item, diagnostics, context);
      }
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateENTITY(String entity, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateNCName_Pattern(entity, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateFloat(float float_, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateFloatObject(Float floatObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateGDay(String gDay, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateGMonth(String gMonth, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateGMonthDay(String gMonthDay, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateGYear(String gYear, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateGYearMonth(String gYearMonth, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateHexBinary(byte[] hexBinary, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateID(String id, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateNCName_Pattern(id, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateIDREF(String idref, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateNCName_Pattern(idref, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateIDREFS(List<?> idrefs, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateIDREFSBase_ItemType(idrefs, diagnostics, context);
    if (result || diagnostics != null) result &= validateIDREFS_MinLength(idrefs, diagnostics, context);
    return result;
  }

  /**
   * Validates the MinLength constraint of '<em>IDREFS</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateIDREFS_MinLength(List<?> idrefs, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    int length = idrefs.size();
    boolean result = length >= 1;
    if (!result && diagnostics != null)
      reportMinLengthViolation(XMLTypePackage.Literals.IDREFS, idrefs, length, 1, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateIDREFSBase(List<?> idrefsBase, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateIDREFSBase_ItemType(idrefsBase, diagnostics, context);
    return result;
  }

  /**
   * Validates the ItemType constraint of '<em>IDREFS Base</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateIDREFSBase_ItemType(List<?> idrefsBase, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    for (Iterator<?> i = idrefsBase.iterator(); i.hasNext() && (result || diagnostics != null); )
    {
      Object item = i.next();
      if (XMLTypePackage.Literals.IDREF.isInstance(item))
      {
        result &= validateIDREF((String)item, diagnostics, context);
      }
      else
      {
        result = false;
        reportDataValueTypeViolation(XMLTypePackage.Literals.IDREF, item, diagnostics, context);
      }
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateInt(int int_, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateInteger(String integer, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateIntObject(Integer intObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateLanguage(String language, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateLanguage_Pattern(language, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @see #validateLanguage_Pattern
   */
  public static final  PatternMatcher [][] LANGUAGE__PATTERN__VALUES =
    new PatternMatcher [][]
    {
      new PatternMatcher []
      {
        XMLTypeUtil.createPatternMatcher("[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*")
      }
    };

  /**
   * Validates the Pattern constraint of '<em>Language</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateLanguage_Pattern(String language, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validatePattern(XMLTypePackage.Literals.LANGUAGE, language, LANGUAGE__PATTERN__VALUES, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateLong(long long_, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateLongObject(Long longObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateName(String name, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateName_Pattern(name, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @see #validateName_Pattern
   */
  public static final  PatternMatcher [][] NAME__PATTERN__VALUES =
    new PatternMatcher [][]
    {
      new PatternMatcher []
      {
        XMLTypeUtil.createPatternMatcher("\\i\\c*")
      }
    };

  /**
   * Validates the Pattern constraint of '<em>Name</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateName_Pattern(String name, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validatePattern(XMLTypePackage.Literals.NAME, name, NAME__PATTERN__VALUES, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateNCName(String ncName, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateNCName_Pattern(ncName, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @see #validateNCName_Pattern
   */
  public static final  PatternMatcher [][] NC_NAME__PATTERN__VALUES =
    new PatternMatcher [][]
    {
      new PatternMatcher []
      {
        XMLTypeUtil.createPatternMatcher("[\\i-[:]][\\c-[:]]*")
      },
      new PatternMatcher []
      {
        XMLTypeUtil.createPatternMatcher("\\i\\c*")
      }
    };

  /**
   * Validates the Pattern constraint of '<em>NC Name</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateNCName_Pattern(String ncName, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validatePattern(XMLTypePackage.Literals.NC_NAME, ncName, NC_NAME__PATTERN__VALUES, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateNegativeInteger(String negativeInteger, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateNegativeInteger_Max(negativeInteger, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @see #validateNegativeInteger_Max
   */
  public static final String NEGATIVE_INTEGER__MAX__VALUE = "-1";

  /**
   * Validates the Max constraint of '<em>Negative Integer</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateNegativeInteger_Max(String negativeInteger, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = negativeInteger.compareTo(NEGATIVE_INTEGER__MAX__VALUE) <= 0;
    if (!result && diagnostics != null)
      reportMaxViolation(XMLTypePackage.Literals.NEGATIVE_INTEGER, negativeInteger, NEGATIVE_INTEGER__MAX__VALUE, true, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateNMTOKEN(String nmtoken, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateNMTOKEN_Pattern(nmtoken, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @see #validateNMTOKEN_Pattern
   */
  public static final  PatternMatcher [][] NMTOKEN__PATTERN__VALUES =
    new PatternMatcher [][]
    {
      new PatternMatcher []
      {
        XMLTypeUtil.createPatternMatcher("\\c+")
      }
    };

  /**
   * Validates the Pattern constraint of '<em>NMTOKEN</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateNMTOKEN_Pattern(String nmtoken, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validatePattern(XMLTypePackage.Literals.NMTOKEN, nmtoken, NMTOKEN__PATTERN__VALUES, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateNMTOKENS(List<?> nmtokens, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateNMTOKENSBase_ItemType(nmtokens, diagnostics, context);
    if (result || diagnostics != null) result &= validateNMTOKENS_MinLength(nmtokens, diagnostics, context);
    return result;
  }

  /**
   * Validates the MinLength constraint of '<em>NMTOKENS</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateNMTOKENS_MinLength(List<?> nmtokens, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    int length = nmtokens.size();
    boolean result = length >= 1;
    if (!result && diagnostics != null)
      reportMinLengthViolation(XMLTypePackage.Literals.NMTOKENS, nmtokens, length, 1, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateNMTOKENSBase(List<?> nmtokensBase, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateNMTOKENSBase_ItemType(nmtokensBase, diagnostics, context);
    return result;
  }

  /**
   * Validates the ItemType constraint of '<em>NMTOKENS Base</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateNMTOKENSBase_ItemType(List<?> nmtokensBase, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    for (Iterator<?> i = nmtokensBase.iterator(); i.hasNext() && (result || diagnostics != null); )
    {
      Object item = i.next();
      if (XMLTypePackage.Literals.NMTOKEN.isInstance(item))
      {
        result &= validateNMTOKEN((String)item, diagnostics, context);
      }
      else
      {
        result = false;
        reportDataValueTypeViolation(XMLTypePackage.Literals.NMTOKEN, item, diagnostics, context);
      }
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateNonNegativeInteger(String nonNegativeInteger, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateNonNegativeInteger_Min(nonNegativeInteger, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @see #validateNonNegativeInteger_Min
   */
  public static final String NON_NEGATIVE_INTEGER__MIN__VALUE = "0";

  /**
   * Validates the Min constraint of '<em>Non Negative Integer</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateNonNegativeInteger_Min(String nonNegativeInteger, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = nonNegativeInteger.compareTo(NON_NEGATIVE_INTEGER__MIN__VALUE) >= 0;
    if (!result && diagnostics != null)
      reportMinViolation(XMLTypePackage.Literals.NON_NEGATIVE_INTEGER, nonNegativeInteger, NON_NEGATIVE_INTEGER__MIN__VALUE, true, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateNonPositiveInteger(String nonPositiveInteger, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateNonPositiveInteger_Max(nonPositiveInteger, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @see #validateNonPositiveInteger_Max
   */
  public static final String NON_POSITIVE_INTEGER__MAX__VALUE = "0";

  /**
   * Validates the Max constraint of '<em>Non Positive Integer</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateNonPositiveInteger_Max(String nonPositiveInteger, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = nonPositiveInteger.compareTo(NON_POSITIVE_INTEGER__MAX__VALUE) <= 0;
    if (!result && diagnostics != null)
      reportMaxViolation(XMLTypePackage.Literals.NON_POSITIVE_INTEGER, nonPositiveInteger, NON_POSITIVE_INTEGER__MAX__VALUE, true, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateNormalizedString(String normalizedString, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateNOTATION(String notation, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validatePositiveInteger(String positiveInteger, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validatePositiveInteger_Min(positiveInteger, diagnostics, context);
    return result;
  }

  @Deprecated
  public boolean validateNOTATION(Object notation, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @see #validatePositiveInteger_Min
   */
  public static final String POSITIVE_INTEGER__MIN__VALUE = "1";

  /**
   * Validates the Min constraint of '<em>Positive Integer</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validatePositiveInteger_Min(String positiveInteger, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = positiveInteger.compareTo(POSITIVE_INTEGER__MIN__VALUE) >= 0;
    if (!result && diagnostics != null)
      reportMinViolation(XMLTypePackage.Literals.POSITIVE_INTEGER, positiveInteger, POSITIVE_INTEGER__MIN__VALUE, true, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateQName(String qName, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  @Deprecated
  public boolean validateQName(Object qName, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateShort(short short_, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateShortObject(Short shortObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateString(String string, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateTime(String time, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateToken(String token, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateUnsignedByte(short unsignedByte, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateUnsignedByte_Min(unsignedByte, diagnostics, context);
    if (result || diagnostics != null) result &= validateUnsignedByte_Max(unsignedByte, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @see #validateUnsignedByte_Min
   */
  public static final short UNSIGNED_BYTE__MIN__VALUE = 0;

  /**
   * Validates the Min constraint of '<em>Unsigned Byte</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateUnsignedByte_Min(short unsignedByte, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = unsignedByte >= UNSIGNED_BYTE__MIN__VALUE;
    if (!result && diagnostics != null)
      reportMinViolation(XMLTypePackage.Literals.UNSIGNED_BYTE, unsignedByte, UNSIGNED_BYTE__MIN__VALUE, true, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @see #validateUnsignedByte_Max
   */
  public static final short UNSIGNED_BYTE__MAX__VALUE = 255;

  /**
   * Validates the Max constraint of '<em>Unsigned Byte</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateUnsignedByte_Max(short unsignedByte, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = unsignedByte <= UNSIGNED_BYTE__MAX__VALUE;
    if (!result && diagnostics != null)
      reportMaxViolation(XMLTypePackage.Literals.UNSIGNED_BYTE, unsignedByte, UNSIGNED_BYTE__MAX__VALUE, true, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateUnsignedByteObject(Short unsignedByteObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateUnsignedByte_Min(unsignedByteObject, diagnostics, context);
    if (result || diagnostics != null) result &= validateUnsignedByte_Max(unsignedByteObject, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateUnsignedInt(long unsignedInt, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateUnsignedInt_Min(unsignedInt, diagnostics, context);
    if (result || diagnostics != null) result &= validateUnsignedInt_Max(unsignedInt, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @see #validateUnsignedInt_Min
   */
  public static final long UNSIGNED_INT__MIN__VALUE = 0L;

  /**
   * Validates the Min constraint of '<em>Unsigned Int</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateUnsignedInt_Min(long unsignedInt, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = unsignedInt >= UNSIGNED_INT__MIN__VALUE;
    if (!result && diagnostics != null)
      reportMinViolation(XMLTypePackage.Literals.UNSIGNED_INT, unsignedInt, UNSIGNED_INT__MIN__VALUE, true, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @see #validateUnsignedInt_Max
   */
  public static final long UNSIGNED_INT__MAX__VALUE = 4294967295L;

  /**
   * Validates the Max constraint of '<em>Unsigned Int</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateUnsignedInt_Max(long unsignedInt, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = unsignedInt <= UNSIGNED_INT__MAX__VALUE;
    if (!result && diagnostics != null)
      reportMaxViolation(XMLTypePackage.Literals.UNSIGNED_INT, unsignedInt, UNSIGNED_INT__MAX__VALUE, true, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateUnsignedIntObject(Long unsignedIntObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateUnsignedInt_Min(unsignedIntObject, diagnostics, context);
    if (result || diagnostics != null) result &= validateUnsignedInt_Max(unsignedIntObject, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateUnsignedLong(String unsignedLong, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateUnsignedLong_Min(unsignedLong, diagnostics, context);
    if (result || diagnostics != null) result &= validateUnsignedLong_Max(unsignedLong, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @see #validateUnsignedLong_Min
   */
  public static final String UNSIGNED_LONG__MIN__VALUE = "0";

  /**
   * Validates the Min constraint of '<em>Unsigned Long</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateUnsignedLong_Min(String unsignedLong, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = unsignedLong.compareTo(UNSIGNED_LONG__MIN__VALUE) >= 0;
    if (!result && diagnostics != null)
      reportMinViolation(XMLTypePackage.Literals.UNSIGNED_LONG, unsignedLong, UNSIGNED_LONG__MIN__VALUE, true, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @see #validateUnsignedLong_Max
   */
  public static final String UNSIGNED_LONG__MAX__VALUE = "18446744073709551615";

  /**
   * Validates the Max constraint of '<em>Unsigned Long</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateUnsignedLong_Max(String unsignedLong, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = unsignedLong.compareTo(UNSIGNED_LONG__MAX__VALUE) <= 0;
    if (!result && diagnostics != null)
      reportMaxViolation(XMLTypePackage.Literals.UNSIGNED_LONG, unsignedLong, UNSIGNED_LONG__MAX__VALUE, true, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateUnsignedShort(int unsignedShort, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateUnsignedShort_Min(unsignedShort, diagnostics, context);
    if (result || diagnostics != null) result &= validateUnsignedShort_Max(unsignedShort, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @see #validateUnsignedShort_Min
   */
  public static final int UNSIGNED_SHORT__MIN__VALUE = 0;

  /**
   * Validates the Min constraint of '<em>Unsigned Short</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateUnsignedShort_Min(int unsignedShort, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = unsignedShort >= UNSIGNED_SHORT__MIN__VALUE;
    if (!result && diagnostics != null)
      reportMinViolation(XMLTypePackage.Literals.UNSIGNED_SHORT, unsignedShort, UNSIGNED_SHORT__MIN__VALUE, true, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @see #validateUnsignedShort_Max
   */
  public static final int UNSIGNED_SHORT__MAX__VALUE = 65535;

  /**
   * Validates the Max constraint of '<em>Unsigned Short</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateUnsignedShort_Max(int unsignedShort, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = unsignedShort <= UNSIGNED_SHORT__MAX__VALUE;
    if (!result && diagnostics != null)
      reportMaxViolation(XMLTypePackage.Literals.UNSIGNED_SHORT, unsignedShort, UNSIGNED_SHORT__MAX__VALUE, true, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateUnsignedShortObject(Integer unsignedShortObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = validateUnsignedShort_Min(unsignedShortObject, diagnostics, context);
    if (result || diagnostics != null) result &= validateUnsignedShort_Max(unsignedShortObject, diagnostics, context);
    return result;
  }
  
  /**
   * Returns the resource locator that will be used to fetch messages for this validator's diagnostics.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public ResourceLocator getResourceLocator()
  {
    return EcorePlugin.INSTANCE;
  }

} //XMLTypeValidator
