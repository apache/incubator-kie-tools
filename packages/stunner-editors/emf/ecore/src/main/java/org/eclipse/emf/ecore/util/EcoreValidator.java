/**
 * Copyright (c) 2006-2012 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *   Christian Damus (Zeligsoft) - 255469
 */
package org.eclipse.emf.ecore.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.common.util.InvocationTargetException;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.UniqueEList;

import org.eclipse.emf.ecore.*;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.ecore.xml.type.util.XMLTypeValidator;

/**
 * <!-- begin-user-doc -->
 * The <b>Validator</b> for the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.ecore.EcorePackage
 * @generated
 */
public class EcoreValidator extends EObjectValidator
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final EcoreValidator INSTANCE = new EcoreValidator();

  /**
   * A key to be used in <code>context</code> maps to indicate that stricter validation should be performed 
   * to ensure that the name of each named element is a well formed Java identifier. 
   * The value of the entry must be a {@link Boolean}.
   * The default value is <code>Boolean.TRUE</code>.
   * @see EValidator#validate(EObject, DiagnosticChain, Map)
   * @see #validateENamedElement_WellFormedName(ENamedElement, DiagnosticChain, Map)
   * @since 2.4
   */
  public static final String STRICT_NAMED_ELEMENT_NAMES = "org.eclipse.emf.ecore.model.ENamedElement_WellFormedName";

  /**
   * A constant for the {@link org.eclipse.emf.common.util.Diagnostic#getSource() source} of diagnostic {@link org.eclipse.emf.common.util.Diagnostic#getCode() codes} from this package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.common.util.Diagnostic#getSource()
   * @see org.eclipse.emf.common.util.Diagnostic#getCode()
   * @generated NOT
   */
  public static final String DIAGNOSTIC_SOURCE = "org.eclipse.emf.ecore.model";

  /**
   * A constant with a fixed name that can be used as the base value for additional hand written constants.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final int GENERATED_DIAGNOSTIC_CODE_COUNT = 0;

  /**
   * @see #validateEClass_AtMostOneID(EClass, DiagnosticChain, Map)
   */
  public static final int AT_MOST_ONE_ID = GENERATED_DIAGNOSTIC_CODE_COUNT + 1;

  /**
   * @see #validateEGenericType_ConsistentArguments(EGenericType, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_ARGUMENTS_INCORRECT_NUMBER = 2;

  /**
   * @see #validateEGenericType_ConsistentArguments(EGenericType, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_ARGUMENTS_INVALID_SUBSTITUTION = 3;

  /**
   * @see #validateEGenericType_ConsistentArguments(EGenericType, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_ARGUMENTS_NONE = 4;

  /**
   * @see #validateEGenericType_ConsistentArguments(EGenericType, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_ARGUMENTS_NONE_ALLOWED = 5;

  /**
   * @see #validateETypedElement_ConsistentBounds(ETypedElement, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_BOUNDS = 6;

  /**
   * @see #validateEGenericType_ConsistentBounds(EGenericType, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_BOUNDS_NOT_ALLOWED = 7;

  /**
   * @see #validateEGenericType_ConsistentBounds(EGenericType, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_BOUNDS_NO_BOUNDS_WITH_TYPE_PARAMETER_OR_CLASSIFIER = 8;

  /**
   * @see #validateEGenericType_ConsistentBounds(EGenericType, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_BOUNDS_NO_LOWER_AND_UPPER = 9;

  /**
   * @see #validateEReference_ConsistentKeys(EReference, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_KEYS = 10;

  /**
   * @see #validateEReference_ConsistentOpposite(EReference, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_OPPOSITE_BAD_TRANSIENT = 11;

  /**
   * @see #validateEReference_ConsistentOpposite(EReference, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_OPPOSITE_BOTH_CONTAINMENT = 12;

  /**
   * @see #validateEReference_ConsistentOpposite(EReference, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_OPPOSITE_NOT_FROM_TYPE = 13;

  /**
   * @see #validateEReference_ConsistentOpposite(EReference, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_OPPOSITE_NOT_MATCHING = 14;

  /**
   * @see #validateEClass_ConsistentSuperTypes(EClass, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_SUPER_TYPES_CONFLICT = 15;

  /**
   * @see #validateEClass_ConsistentSuperTypes(EClass, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_SUPER_TYPES_DUPLICATE = 16;

  /**
   * @see #validateEAttribute_ConsistentTransient(EAttribute, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_TRANSIENT = 17;

  /**
   * @see #validateEGenericType_ConsistentType(EGenericType, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_TYPE_CLASS_REQUIRED = 18;

  /**
   * @see #validateEGenericType_ConsistentType(EGenericType, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_TYPE_CLASS_NOT_PERMITTED = 19;

  /**
   * @see #validateEGenericType_ConsistentType(EGenericType, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_TYPE_DATA_TYPE_NOT_PERMITTED = 20;

  /**
   * @see #validateEGenericType_ConsistentType(EGenericType, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_TYPE_NO_TYPE_PARAMETER_AND_CLASSIFIER = 21;

  /**
   * @see #validateEGenericType_ConsistentType(EGenericType, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_TYPE_PRIMITIVE_TYPE_NOT_PERMITTED = 22;

  /**
   * @see #validateEGenericType_ConsistentType(EGenericType, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_TYPE_TYPE_PARAMETER_NOT_IN_SCOPE = 23;

  /**
   * @see #validateEGenericType_ConsistentType(EGenericType, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_TYPE_WILDCARD_NOT_PERMITTED = 24;

  /**
   * @see #validateEClass_InterfaceIsAbstract(EClass, DiagnosticChain, Map)
   */
  public static final int INTERFACE_IS_ABSTRACT = 25;

  /**
   * @see #validateEClass_NoCircularSuperTypes(EClass, DiagnosticChain, Map)
   */
  public static final int NO_CIRCULAR_SUPER_TYPES = 26;

  /**
   * @see #validateEOperation_NoRepeatingVoid(EOperation, DiagnosticChain, Map)
   */
  public static final int NO_REPEATING_VOID = 27;

  /**
   * @see #validateEReference_SingleContainer(EReference, DiagnosticChain, Map)
   */
  public static final int SINGLE_CONTAINER = 28;

  /**
   * @see #validateEPackage_UniqueClassifierNames(EPackage, DiagnosticChain, Map)
   */
  public static final int UNIQUE_CLASSIFIER_NAMES = 29;

  /**
   * @see #validateEEnum_UniqueEnumeratorNames(EEnum, DiagnosticChain, Map)
   */
  public static final int UNIQUE_ENUMERATOR_LITERALS = 30;

  /**
   * @see #validateEEnum_UniqueEnumeratorNames(EEnum, DiagnosticChain, Map)
   */
  public static final int UNIQUE_ENUMERATOR_NAMES = 31;

  /**
   * @see #validateEClass_UniqueFeatureNames(EClass, DiagnosticChain, Map)
   */
  public static final int UNIQUE_FEATURE_NAMES = 32;

  /**
   * @see #validateEPackage_UniqueNsURIs(EPackage, DiagnosticChain, Map)
   */
  public static final int UNIQUE_NS_URIS = 33;

  /**
   * @see #validateEClass_UniqueOperationSignatures(EClass, DiagnosticChain, Map)
   */
  public static final int UNIQUE_OPERATION_SIGNATURES = 34;

  /**
   *@see #validateEOperation_UniqueParameterNames(EOperation, DiagnosticChain, Map)
   */
  public static final int UNIQUE_PARAMETER_NAMES = 35;

  /**
   * @see #validateEPackage_UniqueSubpackageNames(EPackage, DiagnosticChain, Map)
   */
  public static final int UNIQUE_SUBPACKAGE_NAMES = 36;

  /**
   *@see #validateEOperation_UniqueParameterNames(EOperation, DiagnosticChain, Map)
   *@see #validateEClassifier_UniqueTypeParameterNames(EClassifier, DiagnosticChain, Map)
   */
  public static final int UNIQUE_TYPE_PARAMETER_NAMES = 37;

  /**
   * @see #validateEStructuralFeature_ValidDefaultValueLiteral(EStructuralFeature, DiagnosticChain, Map)
   */
  public static final int VALID_DEFAULT_VALUE_LITERAL = 38;

  /**
   * @see #validateETypedElement_ValidLowerBound(ETypedElement, DiagnosticChain, Map)
   */
  public static final int VALID_LOWER_BOUND = 39;

  /**
   * @see #validateETypedElement_ValidType(ETypedElement, DiagnosticChain, Map)
   */
  public static final int VALID_TYPE = 40;

  /**
   * @see #validateETypedElement_ValidUpperBound(ETypedElement, DiagnosticChain, Map)
   */
  public static final int VALID_UPPER_BOUND = 41;

  /**
   * @see #validateEClassifier_WellFormedInstanceTypeName(EClassifier, DiagnosticChain, Map)
   */
  public static final int WELL_FORMED_INSTANCE_TYPE_NAME = 42;

  /**
   * @see #validateEClass_WellFormedMapEntryClass(EClass, DiagnosticChain, Map)
   */
  public static final int WELL_FORMED_MAP_ENTRY_CLASS = 43;

  /**
   * @see #validateEClass_WellFormedMapEntryClass(EClass, DiagnosticChain, Map)
   */
  public static final int WELL_FORMED_NAME = 44;

  /**
   * @see #validateEPackage_WellFormedNsPrefix(EPackage, DiagnosticChain, Map)
   */
  public static final int WELL_FORMED_NS_PREFIX = 45;

  /**
   * @see #validateEPackage_WellFormedNsURI(EPackage, DiagnosticChain, Map)
   */
  public static final int WELL_FORMED_NS_URI = 46;

  /**
   * @see #validateEAnnotation_WellFormedSourceURI(EAnnotation, DiagnosticChain, Map)
   */
  public static final int WELL_FORMED_SOURCE_URI = 47;

  /**
   * @see #validateEClass_DisjointFeatureAndOperationSignatures(EClass, DiagnosticChain, Map)
   */
  public static final int DISJOINT_FEATURE_AND_OPERATION_SIGNATURES = 48;

  /**
   * @see #validateEClass_WellFormedMapEntryClass(EClass, DiagnosticChain, Map)
   */
  public static final int WELL_FORMED_MAP_ENTRY_NO_INSTANCE_CLASS_NAME = 49;

  /**
   * @see #validateEReference_ConsistentUnique(EReference, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_UNIQUE = 50;

  /**
   * @see #validateEReference_ConsistentContainer(EReference, DiagnosticChain, Map)
   */
  public static final int CONSISTENT_CONTAINER = 51;
  
  /**
   * A constant with a fixed name that can be used as the base value for additional hand written constants in a derived class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected static final int DIAGNOSTIC_CODE_COUNT = CONSISTENT_CONTAINER;

  /**
   * The cached base package validator.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected XMLTypeValidator xmlTypeValidator;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EcoreValidator()
  {
    super();
    xmlTypeValidator = XMLTypeValidator.INSTANCE;
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
    return EcorePackage.eINSTANCE;
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
      case EcorePackage.EATTRIBUTE:
        return validateEAttribute((EAttribute)value, diagnostics, context);
      case EcorePackage.EANNOTATION:
        return validateEAnnotation((EAnnotation)value, diagnostics, context);
      case EcorePackage.ECLASS:
        return validateEClass((EClass)value, diagnostics, context);
      case EcorePackage.ECLASSIFIER:
        return validateEClassifier((EClassifier)value, diagnostics, context);
      case EcorePackage.EDATA_TYPE:
        return validateEDataType((EDataType)value, diagnostics, context);
      case EcorePackage.EENUM:
        return validateEEnum((EEnum)value, diagnostics, context);
      case EcorePackage.EENUM_LITERAL:
        return validateEEnumLiteral((EEnumLiteral)value, diagnostics, context);
      case EcorePackage.EFACTORY:
        return validateEFactory((EFactory)value, diagnostics, context);
      case EcorePackage.EMODEL_ELEMENT:
        return validateEModelElement((EModelElement)value, diagnostics, context);
      case EcorePackage.ENAMED_ELEMENT:
        return validateENamedElement((ENamedElement)value, diagnostics, context);
      case EcorePackage.EOBJECT:
        return validateEObject((EObject)value, diagnostics, context);
      case EcorePackage.EOPERATION:
        return validateEOperation((EOperation)value, diagnostics, context);
      case EcorePackage.EPACKAGE:
        return validateEPackage((EPackage)value, diagnostics, context);
      case EcorePackage.EPARAMETER:
        return validateEParameter((EParameter)value, diagnostics, context);
      case EcorePackage.EREFERENCE:
        return validateEReference((EReference)value, diagnostics, context);
      case EcorePackage.ESTRUCTURAL_FEATURE:
        return validateEStructuralFeature((EStructuralFeature)value, diagnostics, context);
      case EcorePackage.ETYPED_ELEMENT:
        return validateETypedElement((ETypedElement)value, diagnostics, context);
      case EcorePackage.ESTRING_TO_STRING_MAP_ENTRY:
        return validateEStringToStringMapEntry((Map.Entry<?, ?>)value, diagnostics, context);
      case EcorePackage.EGENERIC_TYPE:
        return validateEGenericType((EGenericType)value, diagnostics, context);
      case EcorePackage.ETYPE_PARAMETER:
        return validateETypeParameter((ETypeParameter)value, diagnostics, context);
      case EcorePackage.EBOOLEAN:
        return validateEBoolean((Boolean)value, diagnostics, context);
      case EcorePackage.EBOOLEAN_OBJECT:
        return validateEBooleanObject((Boolean)value, diagnostics, context);
      case EcorePackage.EBYTE:
        return validateEByte((Byte)value, diagnostics, context);
      case EcorePackage.EBYTE_ARRAY:
        return validateEByteArray((byte[])value, diagnostics, context);
      case EcorePackage.EBYTE_OBJECT:
        return validateEByteObject((Byte)value, diagnostics, context);
      case EcorePackage.ECHAR:
        return validateEChar((Character)value, diagnostics, context);
      case EcorePackage.ECHARACTER_OBJECT:
        return validateECharacterObject((Character)value, diagnostics, context);
      case EcorePackage.EDATE:
        return validateEDate((Date)value, diagnostics, context);
      case EcorePackage.EDIAGNOSTIC_CHAIN:
        return validateEDiagnosticChain((DiagnosticChain)value, diagnostics, context);
      case EcorePackage.EDOUBLE:
        return validateEDouble((Double)value, diagnostics, context);
      case EcorePackage.EDOUBLE_OBJECT:
        return validateEDoubleObject((Double)value, diagnostics, context);
      case EcorePackage.EE_LIST:
        return validateEEList((EList<?>)value, diagnostics, context);
      case EcorePackage.EENUMERATOR:
        return validateEEnumerator((Enumerator)value, diagnostics, context);
      case EcorePackage.EFEATURE_MAP:
        return validateEFeatureMap((FeatureMap)value, diagnostics, context);
      case EcorePackage.EFEATURE_MAP_ENTRY:
        return validateEFeatureMapEntry((FeatureMap.Entry)value, diagnostics, context);
      case EcorePackage.EFLOAT:
        return validateEFloat((Float)value, diagnostics, context);
      case EcorePackage.EFLOAT_OBJECT:
        return validateEFloatObject((Float)value, diagnostics, context);
      case EcorePackage.EINT:
        return validateEInt((Integer)value, diagnostics, context);
      case EcorePackage.EINTEGER_OBJECT:
        return validateEIntegerObject((Integer)value, diagnostics, context);
      case EcorePackage.EJAVA_CLASS:
        return validateEJavaClass((Class<?>)value, diagnostics, context);
      case EcorePackage.EJAVA_OBJECT:
        return validateEJavaObject(value, diagnostics, context);
      case EcorePackage.ELONG:
        return validateELong((Long)value, diagnostics, context);
      case EcorePackage.ELONG_OBJECT:
        return validateELongObject((Long)value, diagnostics, context);
      case EcorePackage.EMAP:
        return validateEMap((Map<?, ?>)value, diagnostics, context);
      case EcorePackage.ERESOURCE:
        return validateEResource((Resource)value, diagnostics, context);
      case EcorePackage.ERESOURCE_SET:
        return validateEResourceSet((ResourceSet)value, diagnostics, context);
      case EcorePackage.ESHORT:
        return validateEShort((Short)value, diagnostics, context);
      case EcorePackage.ESHORT_OBJECT:
        return validateEShortObject((Short)value, diagnostics, context);
      case EcorePackage.ESTRING:
        return validateEString((String)value, diagnostics, context);
      case EcorePackage.ETREE_ITERATOR:
        return validateETreeIterator((TreeIterator<?>)value, diagnostics, context);
      case EcorePackage.EINVOCATION_TARGET_EXCEPTION:
        return validateEInvocationTargetException((InvocationTargetException)value, diagnostics, context);
      default:
        return true;
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEAttribute(EAttribute eAttribute, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(eAttribute, diagnostics, context)) return false;
    boolean result = validate_EveryMultiplicityConforms(eAttribute, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryDataValueConforms(eAttribute, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(eAttribute, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(eAttribute, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryProxyResolves(eAttribute, diagnostics, context);
    if (result || diagnostics != null) result &= validate_UniqueID(eAttribute, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryKeyUnique(eAttribute, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(eAttribute, diagnostics, context);
    if (result || diagnostics != null) result &= validateENamedElement_WellFormedName(eAttribute, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidLowerBound(eAttribute, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidUpperBound(eAttribute, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ConsistentBounds(eAttribute, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidType(eAttribute, diagnostics, context);
    if (result || diagnostics != null) result &= validateEStructuralFeature_ValidDefaultValueLiteral(eAttribute, diagnostics, context);
    if (result || diagnostics != null) result &= validateEAttribute_ConsistentTransient(eAttribute, diagnostics, context);
    return result;
  }

  /**
   * Validates the ConsistentTransient constraint of '<em>EAttribute</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEAttribute_ConsistentTransient(EAttribute eAttribute, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    EDataType eAttributeType = eAttribute.getEAttributeType();
    boolean result = 
      isEffectivelyTransient(eAttribute) ||
        eAttributeType == null || 
        eAttributeType.isSerializable() ||
        FeatureMapUtil.isFeatureMapEntry(eAttributeType);
    if (!result && diagnostics != null)
    {
      String attributeName = eAttribute.getName();
      if (eAttribute.getEContainingClass() != null)
      {
        attributeName = eAttribute.getEContainingClass().getName() + "." + attributeName;
      }
      diagnostics.add
        (createDiagnostic
          (Diagnostic.ERROR,
           DIAGNOSTIC_SOURCE,
           CONSISTENT_TRANSIENT,
           "_UI_EAttributeConsistentTransient_diagnostic", 
           new String[] { attributeName },
           new Object[] { eAttribute, EcorePackage.Literals.ESTRUCTURAL_FEATURE__TRANSIENT },
           context));
    }
    return result;
  }
  
  private static boolean isEffectivelyTransient(EStructuralFeature eStructuralFeature)
  {
    if (eStructuralFeature.isTransient())
    {
      EStructuralFeature group = ExtendedMetaData.INSTANCE.getGroup(eStructuralFeature);
      return group == null || isEffectivelyTransient(group);
    }
    else
    {
      return false;
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEAnnotation(EAnnotation eAnnotation, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(eAnnotation, diagnostics, context)) return false;
    boolean result = validate_EveryMultiplicityConforms(eAnnotation, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryDataValueConforms(eAnnotation, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(eAnnotation, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(eAnnotation, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryProxyResolves(eAnnotation, diagnostics, context);
    if (result || diagnostics != null) result &= validate_UniqueID(eAnnotation, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryKeyUnique(eAnnotation, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(eAnnotation, diagnostics, context);
    if (result || diagnostics != null) result &= validateEAnnotation_WellFormedSourceURI(eAnnotation, diagnostics, context);
    return result;
  }

  /**
   * Validates the WellFormedSourceURI constraint of '<em>EAnnotation</em>'.
   * <!-- begin-user-doc -->
   * The source URI must either be either <code>null</code> or {@link #isWellFormedURI(String) well formed}.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEAnnotation_WellFormedSourceURI(EAnnotation eAnnotation, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    String source = eAnnotation.getSource();
    boolean result = source == null || isWellFormedURI(source);
    if (!result && diagnostics != null)
    {
      diagnostics.add
        (createDiagnostic
          (Diagnostic.ERROR,
           DIAGNOSTIC_SOURCE,
           WELL_FORMED_SOURCE_URI,
           "_UI_EAnnotationSourceURINotWellFormed_diagnostic",
           new Object[] { source },
           new Object[] { eAnnotation, EcorePackage.Literals.EANNOTATION__SOURCE },
           context));
    }
    return result;
  }

  /**
   * A well formed URI string must have a non-zero length,
   * and must encode any special characters such as the space character.
   * As such, creating a {@link URI#createURI(String, boolean) URI},
   * ignoring the properly encoded characters,
   * and converting that to a {@link URI#toString() string},
   * must yield this URI string itself.
   * @param uri the URI string in question.
   * @return whether the URI is well formed.
   */
  protected static boolean isWellFormedURI(String uri)
  {
    try
    {
      return uri != null && uri.length() != 0 && uri.equals(URI.createURI(uri, true).toString());
    }
    catch (Throwable exception)
    {
      return false;
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEClass(EClass eClass, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(eClass, diagnostics, context)) return false;
    boolean result = validate_EveryMultiplicityConforms(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryDataValueConforms(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryProxyResolves(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validate_UniqueID(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryKeyUnique(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validateENamedElement_WellFormedName(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validateEClassifier_WellFormedInstanceTypeName(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validateEClassifier_UniqueTypeParameterNames(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validateEClass_InterfaceIsAbstract(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validateEClass_AtMostOneID(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validateEClass_UniqueFeatureNames(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validateEClass_UniqueOperationSignatures(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validateEClass_NoCircularSuperTypes(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validateEClass_WellFormedMapEntryClass(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validateEClass_ConsistentSuperTypes(eClass, diagnostics, context);
    if (result || diagnostics != null) result &= validateEClass_DisjointFeatureAndOperationSignatures(eClass, diagnostics, context);
    return result;
  }

  /**
   * Validates the InterfaceIsAbstract constraint of '<em>EClass</em>'.
   * <!-- begin-user-doc -->
   * A class that is an interface must be abstract.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEClass_InterfaceIsAbstract(EClass eClass, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = !eClass.isInterface() || eClass.isAbstract();
    if (!result && diagnostics != null)
    {
      diagnostics.add
        (createDiagnostic
          (Diagnostic.ERROR,
           DIAGNOSTIC_SOURCE,
           INTERFACE_IS_ABSTRACT,
           "_UI_EClassInterfaceNotAbstract_diagnostic",
           null,
           new Object[] { eClass, EcorePackage.Literals.ECLASS__ABSTRACT },
           context));
    }
    return result;
  }

  /**
   * Validates the AtMostOneID constraint of '<em>EClass</em>'.
   * <!-- begin-user-doc -->
   * There can be at most one attribute that is an ID.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEClass_AtMostOneID(EClass eClass, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    EAttribute eIDAttribute = eClass.getEIDAttribute();

    // A document root can have multiple ID attributes because there can be multiple global element or attribute declarations of type ID 
    // and these will be the ID in the complex types that reference them,
    // i.e., they aren't really the ID of the document root.
    //
    if (eIDAttribute != null && !ExtendedMetaData.INSTANCE.isDocumentRoot(eClass))
    {
      List<EAttribute> eIDAttributes = new ArrayList<EAttribute>();
      eIDAttributes.add(eIDAttribute);

      for (EAttribute eAttribute : eClass.getEAllAttributes())
      {
        if (eAttribute.isID() && eIDAttribute != eAttribute)
        {
          if (diagnostics == null)
          {
            return false;
          }
          else
          {
            result = false;
            eIDAttributes.add(eAttribute);
          }
        }
      }

      if (!result)
      {
        // We do not want to diagnose any errors that have already been diagnosed by a super type.
        //
        for (EClass eSuperType : eClass.getESuperTypes())
        {
          EList<EStructuralFeature> eAllStructuralFeatures = eSuperType.getEAllStructuralFeatures();
          if (eAllStructuralFeatures.containsAll(eIDAttributes))
          {
            return false;
          }
        }

        List<String> labels = new ArrayList<String>();
        List<Object> objects = new ArrayList<Object>();
        objects.add(eClass);
        for (EAttribute eAttribute : eIDAttributes)
        {
          labels.add(getFeatureLabel(eAttribute,context));
          objects.add(eAttribute);
        }
        objects.add(EcorePackage.Literals.ECLASS__EALL_ATTRIBUTES);
        diagnostics.add
          (createDiagnostic
            (Diagnostic.ERROR,
             DIAGNOSTIC_SOURCE,
             AT_MOST_ONE_ID,
             "_UI_EClassAtMostOneID_diagnostic",
             labels.toArray(new Object[labels.size()]),
             objects.toArray(new Object [objects.size()]),
             context));
      }
    }
    return result;
  }

  /**
   * Validates the UniqueFeatureNames constraint of '<em>EClass</em>'.
   * <!-- begin-user-doc -->
   * No two features may have matching names.
   * Feature names are matched ignoring their case and their underscore separators.
   * It is an error to have two features with names that are equal but only a warning to have two features with names that match.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEClass_UniqueFeatureNames(EClass eClass, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    int size = eClass.getFeatureCount();
    if (size > 0)
    {
      // Build a list of the keys
      //
      Map<String, List<EStructuralFeature>> keys = new HashMap<String, List<EStructuralFeature>>();
      for (int i = 0; i < size; ++i)
      {
        EStructuralFeature eStructuralFeature = eClass.getEStructuralFeature(i);
        String name = eStructuralFeature.getName();

        // Don't bother complaining about things with no name,  since there are constraints for that problem.
        //
        if (name != null)
        {
          // Drop the _ separators and normalize the case.
          //
          String key = name.replace("_", "").toLowerCase();
          List<EStructuralFeature> eStructuralFeatures = keys.get(key);
          if (eStructuralFeatures == null)
          {
            eStructuralFeatures = new ArrayList<EStructuralFeature>();
            keys.put(key, eStructuralFeatures);
          }
          eStructuralFeatures.add(eStructuralFeature);

          if (eStructuralFeatures.size() > 1)
          {
            if (diagnostics == null)
            {
              return false;
            }
            else
            {
              result = false;
            }
          }
        }
      }

      if (!result)
      {
        // We do not want to diagnose any errors that have already been diagnosed by a super type.
        //
        for (EClass eSuperType : eClass.getESuperTypes())
        {
          EList<EStructuralFeature> eAllStructuralFeatures = eSuperType.getEAllStructuralFeatures();
          for (Iterator<Map.Entry<String, List<EStructuralFeature>>> i = keys.entrySet().iterator(); i.hasNext(); )
          {
            Map.Entry<String, List<EStructuralFeature>> entry = i.next();
            if (eAllStructuralFeatures.containsAll(entry.getValue()))
            {
              i.remove();
            }
          }
        }
        
        for (Map.Entry<String, List<EStructuralFeature>> entry : keys.entrySet())
        {
          List<EStructuralFeature> eStructuralFeatures = entry.getValue();
          if (eStructuralFeatures.size() > 1)
          {
            List<String> names = new UniqueEList<String>();
            List<Object> objects = new ArrayList<Object>();
            objects.add(eClass);
            for (EStructuralFeature eStructuralFeature : eStructuralFeatures)
            {
              names.add(eStructuralFeature.getName());
              objects.add(eStructuralFeature);
            }
            objects.add(EcorePackage.Literals.ECLASS__EALL_STRUCTURAL_FEATURES);
            
            if (names.size() == objects.size() - 2)
            {
              diagnostics.add
                (createDiagnostic
                  (Diagnostic.WARNING,
                   DIAGNOSTIC_SOURCE,
                   UNIQUE_FEATURE_NAMES,
                   "_UI_EClassDissimilarEStructuralFeatureName_diagnostic",
                   names.toArray(new Object[names.size()]),
                   objects.toArray(new Object[objects.size()]),
                   context));
              
            }
            else
            {
              diagnostics.add
                (createDiagnostic
                  (Diagnostic.ERROR,
                   DIAGNOSTIC_SOURCE,
                   UNIQUE_FEATURE_NAMES,
                   "_UI_EClassUniqueEStructuralFeatureName_diagnostic",
                   new Object [] { names.get(0) },
                   objects.toArray(new Object[objects.size()]),
                   context));
            }
          }
        }
      }
    }
    return result;
  }

  /**
   * Validates the UniqueOperationSignatures constraint of '<em>EClass</em>'.
   * <!-- begin-user-doc -->
   * No two operations defined in the same class may have matching signatures.
   * The signature is determined by the name of the operation and the types of its parameters.
   * If the name is the same and the types match, the signatures match.
   * Types match if they are the same classifier, or both classifiers have instance class names that are the same.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEClass_UniqueOperationSignatures(EClass eClass, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return uniqueOperationSignaturesValidator.validateEOperationSignatures(eClass, eClass.getEOperations(), eClass.getEOperations(), diagnostics, context);
  }

  private class EOperationSignatureValidator
  {
    protected String messageKey;
    protected int messageCode;
    protected boolean ignoreOperationsWithSuppressedVisibility;

    public EOperationSignatureValidator(String messageKey, int messageCode)
    {
      this.messageKey = messageKey;
      this.messageCode = messageCode;
    }
    
    public EOperationSignatureValidator(String messageKey, int messageCode, boolean ignoreOperationsWithSuppressedVisibility)
    {
      this.messageKey = messageKey;
      this.messageCode = messageCode;
      this.ignoreOperationsWithSuppressedVisibility = ignoreOperationsWithSuppressedVisibility;
    }

    public final boolean validateEOperationSignatures
      (EClass eClass, EList<EOperation> eOperations, Collection<EOperation> otherEOperations, DiagnosticChain diagnostics, Map<Object, Object> context)
    {
      boolean result = true;
      for (EOperation eOperation : eOperations)
      {
        if (!ignoreOperationsWithSuppressedVisibility || !EcoreUtil.isSuppressedVisibility(eOperation))
        {
          String name = eOperation.getName();
          if (name != null)
          {
            EList<EParameter> eParameters = eOperation.getEParameters();
            int eParameterSize = eParameters.size();
            LOOP:
            for (EOperation otherEOperation : otherEOperations)
            {
              // Match against every other operation but this one.
              //
              if (otherEOperation == eOperation)
              {
                break;
              }
              else
              {
                String otherName = otherEOperation.getName();
                if (name.equals(otherName))
                {
                  EList<EParameter> otherEParmeters = otherEOperation.getEParameters();
                  if (otherEParmeters.size() == eParameterSize)
                  {
                    for (int i = 0; i < eParameterSize; ++i)
                    {
                      EParameter eParameter = eParameters.get(i);
                      EParameter otherEParameter = otherEParmeters.get(i);
                      EClassifier eType = eParameter.getEType();
                      EClassifier otherEType = otherEParameter.getEType();
  
                      // There is no match if the types are different
                      // and they don't each specify the same non-null instance class name.
                      //
                      if (eType != otherEType)
                      {
                        if (eType != null && otherEType != null)
                        {
                          String instanceClassName = eType.getInstanceClassName();
                          String otherInstanceClassName = otherEType.getInstanceClassName();
                          if (instanceClassName != otherInstanceClassName || instanceClassName == null || eParameter.isMany() != otherEParameter.isMany())
                          {
                            continue LOOP;
                          }
                        }
                        else
                        {
                          continue LOOP;
                        }
                      }
                      else if (eParameter.isMany() != otherEParameter.isMany())
                      {
                        continue LOOP;
                      }
                    }
                    if (diagnostics == null)
                    {
                      return false;
                    }
                    else
                    {
                      result = false;
  
                      EModelElement target = getTarget(otherEOperation);
                      diagnostics.add
                        (createDiagnostic
                          (Diagnostic.ERROR,
                           DIAGNOSTIC_SOURCE,
                           messageCode,
                           messageKey,
                           messageCode == DISJOINT_FEATURE_AND_OPERATION_SIGNATURES ? 
                             new Object[] { getObjectLabel(eOperation, context), getObjectLabel(target, context) } :
                             new Object[] { getObjectLabel(target, context), getObjectLabel(eOperation, context) },
                           messageCode == DISJOINT_FEATURE_AND_OPERATION_SIGNATURES ? 
                             new Object[] { eClass, eOperation, target, EcorePackage.Literals.ECLASS__EALL_OPERATIONS } :
                             new Object[] { eClass, target, eOperation, EcorePackage.Literals.ECLASS__EALL_OPERATIONS },
                           context));
                    }
                  }
                }
              }
            }
          }
        }
      }
      return result;
    }
      
    protected EModelElement getTarget(EOperation targetEOperation)
    {
      return targetEOperation;
    }
  }
  
  private final EOperationSignatureValidator uniqueOperationSignaturesValidator =
    new EOperationSignatureValidator("_UI_EClassUniqueEOperationSignatures_diagnostic", UNIQUE_OPERATION_SIGNATURES);

  /**
   * There are other constraints we should check such as consistency of the return type, 
   * correctness of the signature with respect to type substitution,
   * and so on...
   * Validates the UniqueOperationSignatures constraint of '<em>EClass</em>'.
   * <!-- begin-user-doc -->
   * No two operation defined in the same class may have matching signatures.
   * The signature is determined by the name of the operation and the types of its parameters.
   * If the name is the same and the types match, the signatures match.
   * Types match if they are same classifier, of both classifiers have instance class names that are the same.
   * <!-- end-user-doc -->
   * @generated NOT YET
   */
  @SuppressWarnings("unused")
  private boolean validateEClass_UniqueOperationSignatures2(EClass eClass, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    EList<EOperation> eAllOperations = eClass.getEAllOperations();
    if (!eAllOperations.isEmpty())
    {
      // Iterate over all the operations but skip all the operations in the first super.
      //
      List<EOperation> eOperations = eAllOperations;
      EList<EClass> eSuperTypes = eClass.getESuperTypes();
      if (!eSuperTypes.isEmpty())
      {
        eOperations = eAllOperations.subList(eSuperTypes.get(0).getEAllOperations().size(), eAllOperations.size());
      }
      for (EOperation eOperation : eOperations)
      {
        String name = eOperation.getName();
        if (name != null)
        {
          EList<EParameter> eParameters = eOperation.getEParameters();
          int eParameterSize = eParameters.size();
          LOOP:
          for (EOperation otherEOperation : eAllOperations)
          {
            // Match against every other operation but this one.
            //
            if (otherEOperation == eOperation)
            {
              break;
            }
            else
            {
              String otherName = otherEOperation.getName();
              if (name.equals(otherName))
              {
                EList<EParameter> otherEParmeters = otherEOperation.getEParameters();
                if (otherEParmeters.size() == eParameterSize)
                {
                  for (int i = 0; i < eParameterSize; ++i)
                  {
                    EParameter eParameter = eParameters.get(i);
                    EParameter otherEParameter = otherEParmeters.get(i);
                    EClassifier eType = eParameter.getEType();
                    EClassifier otherEType = otherEParameter.getEType();

                    // There is no match if the types are different
                    // and they don't each specify the same non-null instance class name.
                    //
                    if (eType != otherEType)
                    {
                      if (eType != null && otherEType != null)
                      {
                        String instanceClassName = eType.getInstanceClassName();
                        String otherInstanceClassName = otherEType.getInstanceClassName();
                        if (instanceClassName != otherInstanceClassName && instanceClassName != null && otherInstanceClassName != null)
                        {
                          continue LOOP;
                        }
                      }
                    }
                  }
                  if (diagnostics == null)
                  {
                    return false;
                  }
                  else
                  {
                    result = false;

                    // We do not want to diagnose any error that have already been diagnosed by a super type.
                    //
                    for (EClass eSuperType : eClass.getEAllSuperTypes())
                    {
                      EList<EOperation> superTypeEAllOperations = eSuperType.getEAllOperations();
                      if (superTypeEAllOperations.contains(eOperation) && superTypeEAllOperations.contains(otherEOperation))
                      {
                        continue LOOP;
                      }
                    }

                    diagnostics.add
                      (createDiagnostic
                        (Diagnostic.ERROR,
                         DIAGNOSTIC_SOURCE,
                         UNIQUE_OPERATION_SIGNATURES,
                         "_UI_EClassUniqueEOperationSignatures_diagnostic",
                         new Object[] { getObjectLabel(eOperation, context), getObjectLabel(otherEOperation, context) },
                         new Object[] { eClass, eOperation, otherEOperation, EcorePackage.Literals.ECLASS__EALL_OPERATIONS },
                         context));
                  }
                }
              }
            }
          }
        }
      }
    }
    return result;
  }

  /**
   * Validates the NoCircularSuperTypes constraint of '<em>EClass</em>'.
   * <!-- begin-user-doc -->
   * A super type must not appear in its own list of all super types.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEClass_NoCircularSuperTypes(EClass eClass, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    EList<EClass> eAllSuperTypes = eClass.getEAllSuperTypes();
    boolean result = !eAllSuperTypes.contains(eClass);
    if (result)
    {
      for (EClass otherEClass : eAllSuperTypes)
      {
        if (otherEClass.getEAllSuperTypes().contains(eClass))
        {
          result = false;
          break;
        }
      }
    }
    if (!result && diagnostics != null)
    {
      diagnostics.add
        (createDiagnostic
          (Diagnostic.ERROR,
           DIAGNOSTIC_SOURCE,
           NO_CIRCULAR_SUPER_TYPES,
           "_UI_EClassNoCircularSuperTypes_diagnostic",
           null,
           new Object[] { eClass, EcorePackage.Literals.ECLASS__EALL_SUPER_TYPES },
           context));
    }
    return result;
  }

  /**
   * Validates the WellFormedMapEntryClass constraint of '<em>EClass</em>'.
   * <!-- begin-user-doc -->
   * A map entry class must have features named 'key' and 'value'.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEClass_WellFormedMapEntryClass(EClass eClass, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    if (eClass.getInstanceClassName() == "java.util.Map$Entry")
    {
      EStructuralFeature keyFeature = eClass.getEStructuralFeature("key");
      if (keyFeature == null)
      {
        if (diagnostics == null)
        {
          return false;
        }
        else
        {
          result = false;
          diagnostics.add
            (createDiagnostic
              (Diagnostic.ERROR,
               DIAGNOSTIC_SOURCE,
               WELL_FORMED_MAP_ENTRY_CLASS,
               "_UI_EClassNotWellFormedMapEntry_diagnostic",
               new Object[] { "key" },
               new Object[] { eClass, EcorePackage.Literals.ECLASS__EALL_STRUCTURAL_FEATURES },
               context));
        }
      }
      EStructuralFeature valueFeature = eClass.getEStructuralFeature("value");
      if (valueFeature == null)
      {
        if (diagnostics == null)
        {
          return false;
        }
        else
        {
          result = false;
          diagnostics.add
            (createDiagnostic
              (Diagnostic.ERROR,
               DIAGNOSTIC_SOURCE,
               WELL_FORMED_MAP_ENTRY_CLASS,
               "_UI_EClassNotWellFormedMapEntry_diagnostic", 
               new Object[] { "value" },
               new Object[] { eClass, EcorePackage.Literals.ECLASS__EALL_STRUCTURAL_FEATURES },
               context));
        }
      }
    }
    else
    {
      for (EClass eSuperType : eClass.getEAllSuperTypes())
      {
        if (eSuperType.getInstanceClassName() == "java.util.Map$Entry")
        {
          if (diagnostics == null)
          {
            return false;
          }
          else
          {
            result = false;
            diagnostics.add
              (createDiagnostic
                (Diagnostic.ERROR,
                 DIAGNOSTIC_SOURCE,
                 WELL_FORMED_MAP_ENTRY_NO_INSTANCE_CLASS_NAME,
                 "_UI_EClassNotWellFormedMapEntryNoInstanceClassName_diagnostic",
                 null,
                 new Object[] { eClass, EcorePackage.Literals.ECLASSIFIER__INSTANCE_TYPE_NAME },
                 context));
          }
        }
      }
    }
    return result;
  }

  /**
   * Validates the ConsistentSuperTypes constraint of '<em>EClass</em>'.
   * <!-- begin-user-doc -->
   * The same class must not occur more than once among the generic super types
   * nor among all the generic super types
   * where occurrences in the latter represent conflicting instantiations of the same classifier.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEClass_ConsistentSuperTypes(EClass eClass, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;

    // Maintain a list of classifiers for looking up conflicts.
    //
    ArrayList<EClassifier> superTypes = new ArrayList<EClassifier>();

    // Look for duplicates among the generic super types.
    //
    EList<EGenericType> eGenericSuperTypes = eClass.getEGenericSuperTypes();
    for (EGenericType eGenericSuperType : eGenericSuperTypes)
    {
      // Ignore it if it isn't a class. Not being a class is diagnosed for the generic type itself.
      //
      EClassifier eClassifier = eGenericSuperType.getEClassifier();
      if (eClassifier instanceof EClass)
      {
        int index = superTypes.indexOf(eClassifier);
        if (index != -1)
        {
          if (diagnostics == null)
          {
            return false;
          }
          else
          {
            result = false;
            diagnostics.add
              (createDiagnostic
                (Diagnostic.ERROR,
                 DIAGNOSTIC_SOURCE,
                 CONSISTENT_SUPER_TYPES_DUPLICATE,
                 "_UI_EClassNoDuplicateSuperTypes_diagnostic",
                 new Object [] { eGenericSuperTypes.indexOf(eGenericSuperType), index },
                 new Object[] { eClass, eGenericSuperTypes.get(index), eGenericSuperType, EcorePackage.Literals.ECLASS__EGENERIC_SUPER_TYPES },
                 context));
          }
        }
      }
      superTypes.add(eClassifier);
    }

    if (result)
    {
      superTypes.clear();
      EList<EGenericType> eAllGenericSuperTypes = eClass.getEAllGenericSuperTypes();
      for (EGenericType eGenericSuperType : eAllGenericSuperTypes)
      {
        EClassifier eClassifier = eGenericSuperType.getEClassifier();
        if (eClassifier instanceof EClass)
        {
          int index = superTypes.indexOf(eClassifier);
          if (index != -1)
          {
            if (diagnostics == null)
            {
              return false;
            }
            else
            {
              result = false;
              diagnostics.add
                (createDiagnostic
                  (Diagnostic.ERROR,
                   DIAGNOSTIC_SOURCE,
                   CONSISTENT_SUPER_TYPES_CONFLICT,
                   "_UI_EClassConsistentSuperTypes_diagnostic",
                   new Object [] { getObjectLabel(eClassifier, context) },
                   new Object[] { eClass, eGenericSuperType,  eAllGenericSuperTypes.get(index), EcorePackage.Literals.ECLASS__EALL_GENERIC_SUPER_TYPES },
                   context));
            }
          }
        }
        superTypes.add(eClassifier);
      }
    }
    return result;
  }

  /**
   * Validates the DisjointFeatureAndOperationSignatures constraint of '<em>EClass</em>'.
   * <!-- begin-user-doc -->
   * Each feature defined in the class is 
   * interpreted as implicitly defining the operations 
   * with the signatures corresponding to the generated accessors for that feature
   * hence the same type of constraint as {@link #validateEClass_UniqueOperationSignatures(EClass, DiagnosticChain, Map)} applies.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEClass_DisjointFeatureAndOperationSignatures(EClass eClass, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    EList<EOperation> eOperations = eClass.getEOperations();
    final Map<EOperation, EStructuralFeature> implicitEOperationToEStructuralFeatureMap = new LinkedHashMap<EOperation, EStructuralFeature>();
    if (!eOperations.isEmpty())
    {
      for (EStructuralFeature eStructuralFeature : eClass.getEStructuralFeatures())
      {
        String featureName = eStructuralFeature.getName();
        EClassifier eType = eStructuralFeature.getEType();
        if (featureName != null && featureName.length() != 0 && eType != null)
        {
          featureName = featureName.substring(0,1).toUpperCase() + featureName.substring(1);
          if (!EcoreUtil.isSuppressedVisibility(eStructuralFeature, EcoreUtil.GET))
          {
            String getAccessor = (eStructuralFeature.isMany() || !"boolean".equals(eType.getInstanceClassName()) ? "get" : "is") + featureName;
            if ("getClass".equals(getAccessor))
            {
              getAccessor = "getClass_";
            }
            EOperation eOperation = EcoreFactory.eINSTANCE.createEOperation();
            eOperation.setName(getAccessor);
            eOperation.setUpperBound(eStructuralFeature.getUpperBound());
            eOperation.setOrdered(eStructuralFeature.isOrdered());
            eOperation.setUnique(eStructuralFeature.isUnique());
            eOperation.setEType(eType);
            implicitEOperationToEStructuralFeatureMap.put(eOperation, eStructuralFeature);
          }
          if (!eStructuralFeature.isMany() && eStructuralFeature.isChangeable() && !EcoreUtil.isSuppressedVisibility(eStructuralFeature, EcoreUtil.SET))
          {
            String setAccessor = "set" + featureName;
            EOperation eOperation = EcoreFactory.eINSTANCE.createEOperation();
            eOperation.setName(setAccessor);
            EParameter eParameter = EcoreFactory.eINSTANCE.createEParameter();
            eParameter.setName(featureName);
            eParameter.setEType(eType);
            eOperation.getEParameters().add(eParameter);
            implicitEOperationToEStructuralFeatureMap.put(eOperation, eStructuralFeature);
          }
          if (eStructuralFeature.isUnsettable())
          {
            if (!EcoreUtil.isSuppressedVisibility(eStructuralFeature, EcoreUtil.IS_SET))
            {
              String isSetAccessor =  "isSet" + featureName;
              EOperation eOperation = EcoreFactory.eINSTANCE.createEOperation();
              eOperation.setName(isSetAccessor);
              eOperation.setEType(EcorePackage.Literals.EBOOLEAN);
              implicitEOperationToEStructuralFeatureMap.put(eOperation, eStructuralFeature);
            }
            if (!EcoreUtil.isSuppressedVisibility(eStructuralFeature, EcoreUtil.UNSET))
            {
              String unsetAccessor =  "unset" + featureName;
              EOperation eOperation = EcoreFactory.eINSTANCE.createEOperation();
              eOperation.setName(unsetAccessor);
              implicitEOperationToEStructuralFeatureMap.put(eOperation, eStructuralFeature);
            }
          }
        }
      }

      result =
        new EOperationSignatureValidator("_UI_EClassDisjointFeatureAndOperationSignatures_diagnostic", DISJOINT_FEATURE_AND_OPERATION_SIGNATURES, true)
        {
          @Override
          protected EModelElement getTarget(EOperation otherEOperation)
          {
            return implicitEOperationToEStructuralFeatureMap.get(otherEOperation);
          }
        }.validateEOperationSignatures(eClass, eOperations, implicitEOperationToEStructuralFeatureMap.keySet(), diagnostics, context);
    }
    return result;
  }
 
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEClassifier(EClassifier eClassifier, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(eClassifier, diagnostics, context)) return false;
    boolean result = validate_EveryMultiplicityConforms(eClassifier, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryDataValueConforms(eClassifier, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(eClassifier, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(eClassifier, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryProxyResolves(eClassifier, diagnostics, context);
    if (result || diagnostics != null) result &= validate_UniqueID(eClassifier, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryKeyUnique(eClassifier, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(eClassifier, diagnostics, context);
    if (result || diagnostics != null) result &= validateENamedElement_WellFormedName(eClassifier, diagnostics, context);
    if (result || diagnostics != null) result &= validateEClassifier_WellFormedInstanceTypeName(eClassifier, diagnostics, context);
    if (result || diagnostics != null) result &= validateEClassifier_UniqueTypeParameterNames(eClassifier, diagnostics, context);
    return result;
  }

  /**
   * Validates the WellFormedInstanceTypeName constraint of '<em>EClassifier</em>'.
   * <!-- begin-user-doc -->
   * The instance type name may be null only for a class or an enum
   * and must be {@link EGenericTypeBuilder#parseInstanceTypeName(String) well formed} when not null.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEClassifier_WellFormedInstanceTypeName(EClassifier eClassifier, DiagnosticChain diagnostics, final Map<Object, Object> context)
  {
    String instanceTypeName = eClassifier.getInstanceTypeName();
    Diagnostic typeBuilderDiagnostic = 
      instanceTypeName == null ? 
        null : 
        new EGenericTypeBuilder()
        {
          @Override
          protected BasicDiagnostic createDiagnostic(int severity, String source, int code, String messageKey, Object[] messageSubstitutions, Object[] data)
          {
            return EcoreValidator.this.createDiagnostic(severity, source, code, messageKey, messageSubstitutions, data, context);
          }

          @Override
          protected ResourceLocator getResourceLocator()
          {
            return EcoreValidator.this.getResourceLocator();
          }

          @Override
          protected String getString(String key, Object[] substitutions)
          {
            return EcoreValidator.this.getString(key, substitutions);
          }

          @Override
          protected void report(DiagnosticChain diagnostics, String key, Object[] substitutions, int index)
          {
            EcoreValidator.this.report(diagnostics, key, substitutions, index, context);
          }
        }.parseInstanceTypeName(instanceTypeName);
    String formattedName = null;
    @SuppressWarnings("null")
    boolean result =
      instanceTypeName != null ?
        typeBuilderDiagnostic.getSeverity() == Diagnostic.OK  && 
          instanceTypeName.equals(formattedName = EcoreUtil.toJavaInstanceTypeName((EGenericType)typeBuilderDiagnostic.getData().get(0))) :
        eClassifier instanceof EClass || eClassifier instanceof EEnum;
    if (!result && diagnostics != null)
    {
      BasicDiagnostic diagnosic =
        createDiagnostic
          (Diagnostic.ERROR,
           DIAGNOSTIC_SOURCE,
           WELL_FORMED_INSTANCE_TYPE_NAME,
           "_UI_EClassifierInstanceTypeNameNotWellFormed_diagnostic",
           new Object[] { getValueLabel(EcorePackage.Literals.ESTRING, instanceTypeName, context) },
           new Object[] { eClassifier, EcorePackage.Literals.ECLASSIFIER__INSTANCE_TYPE_NAME },
           context);
      if (typeBuilderDiagnostic != null)
      {
        if (!typeBuilderDiagnostic.getChildren().isEmpty())
        {
          diagnosic.addAll(typeBuilderDiagnostic);
        }
        else if (instanceTypeName != null && formattedName != null)
        {
          // The string must contain inappropriate whitespace, so find the index for the first difference.
          //
          int i = 0;
          for (int length = Math.min(instanceTypeName.length(), formattedName.length()); 
               i < length; 
               i = Character.offsetByCodePoints(instanceTypeName, i, 1))
          {
            if (instanceTypeName.codePointAt(i) != formattedName.codePointAt(i))
            {
              break;
            }
          }
          
          diagnosic.add
           (createDiagnostic
             (Diagnostic.ERROR,
              DIAGNOSTIC_SOURCE,
              WELL_FORMED_INSTANCE_TYPE_NAME,
              instanceTypeName.codePointAt(i) == ' ' ? "_UI_EClassifierInstanceTypeNameUnexpectedSpace_diagnostic" : "_UI_EClassifierInstanceTypeNameExpectedSpace_diagnostic",
              new Object[] { i },
              new Object[] { i },
              context));
        }
      }
      diagnostics.add(diagnosic);
    }
    return result;
  }

  /**
   * Validates the UniqueTypeParameterNames constraint of '<em>EClassifier</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEClassifier_UniqueTypeParameterNames(EClassifier eClassifier, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    Map<String, List<ETypeParameter>> keys = new HashMap<String, List<ETypeParameter>>();
    for (ETypeParameter eTypeParameter : eClassifier.getETypeParameters())
    {
      String name = eTypeParameter.getName();
      if (name != null)
      {
        List<ETypeParameter> eTypeParameters = keys.get(name);
        if (eTypeParameters == null)
        {
          eTypeParameters = new ArrayList<ETypeParameter>();
          keys.put(name, eTypeParameters);
        }
        eTypeParameters.add(eTypeParameter);
        if (eTypeParameters.size() > 1)
        {
          if (diagnostics == null)
          {
            return false;
          }
          else
          {
            result = false;
          }
        }
      }
    }

    if (!result)
    {
      for (Map.Entry<String, List<ETypeParameter>> entry : keys.entrySet())
      {
        List<ETypeParameter> eTypeParameters = entry.getValue();
        if (eTypeParameters.size() > 1)
        {
          List<Object> objects = new ArrayList<Object>();
          objects.add(eClassifier);
          for (ETypeParameter eTypeParameter : eTypeParameters)
          {
            objects.add(eTypeParameter);
          }
          objects.add(EcorePackage.Literals.ECLASSIFIER__ETYPE_PARAMETERS );
          diagnostics.add
            (createDiagnostic
              (Diagnostic.ERROR,
               DIAGNOSTIC_SOURCE,
               UNIQUE_TYPE_PARAMETER_NAMES,
               "_UI_UniqueTypeParameterNames_diagnostic", 
               new Object[] { entry.getKey() },
               objects.toArray(new Object[objects.size()]),
               context));
          
        }
      }
      
    }
    return result;
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEDataType(EDataType eDataType, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(eDataType, diagnostics, context)) return false;
    boolean result = validate_EveryMultiplicityConforms(eDataType, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryDataValueConforms(eDataType, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(eDataType, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(eDataType, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryProxyResolves(eDataType, diagnostics, context);
    if (result || diagnostics != null) result &= validate_UniqueID(eDataType, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryKeyUnique(eDataType, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(eDataType, diagnostics, context);
    if (result || diagnostics != null) result &= validateENamedElement_WellFormedName(eDataType, diagnostics, context);
    if (result || diagnostics != null) result &= validateEClassifier_WellFormedInstanceTypeName(eDataType, diagnostics, context);
    if (result || diagnostics != null) result &= validateEClassifier_UniqueTypeParameterNames(eDataType, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEEnum(EEnum eEnum, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(eEnum, diagnostics, context)) return false;
    boolean result = validate_EveryMultiplicityConforms(eEnum, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryDataValueConforms(eEnum, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(eEnum, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(eEnum, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryProxyResolves(eEnum, diagnostics, context);
    if (result || diagnostics != null) result &= validate_UniqueID(eEnum, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryKeyUnique(eEnum, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(eEnum, diagnostics, context);
    if (result || diagnostics != null) result &= validateENamedElement_WellFormedName(eEnum, diagnostics, context);
    if (result || diagnostics != null) result &= validateEClassifier_WellFormedInstanceTypeName(eEnum, diagnostics, context);
    if (result || diagnostics != null) result &= validateEClassifier_UniqueTypeParameterNames(eEnum, diagnostics, context);
    if (result || diagnostics != null) result &= validateEEnum_UniqueEnumeratorNames(eEnum, diagnostics, context);
    if (result || diagnostics != null) result &= validateEEnum_UniqueEnumeratorLiterals(eEnum, diagnostics, context);
    return result;
  }

  /**
   * Validates the UniqueEnumeratorNames constraint of '<em>EEnum</em>'.
   * <!-- begin-user-doc -->
   * No two enum literals may have matching names.
   * Literal names are matched ignoring their case and their underscore separators.
   * It is an error to have two enum literals with names that are equal but only a warning to have two enum literals with names that match.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEEnum_UniqueEnumeratorNames(EEnum eEnum, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    EList<EEnumLiteral> eLiterals = eEnum.getELiterals();
    Map<String, List<EEnumLiteral>> keys = new HashMap<String, List<EEnumLiteral>>();
    for (EEnumLiteral eEnumLiteral : eLiterals)
    {
      String name = eEnumLiteral.getName();
      if (name != null)
      {
        String key = name.replace("_", "").toUpperCase();
        List<EEnumLiteral> eEnumLiterals = keys.get(key);
        if (eEnumLiterals == null)
        {
          eEnumLiterals = new ArrayList<EEnumLiteral>();
          keys.put(key, eEnumLiterals);
        }
        eEnumLiterals.add(eEnumLiteral);
        if (eEnumLiterals.size() > 1)
        {
          if (diagnostics == null)
          {
           return false;
          }
          else
          {
            result = false;
          }
        }
      }
    }

    if (!result)
    {
      for (Map.Entry<String, List<EEnumLiteral>> entry : keys.entrySet())
      {
        List<EEnumLiteral> eEnumLiterals = entry.getValue();
        if (eEnumLiterals.size() > 1)
        {
          List<String> names = new UniqueEList<String>();
          List<Object> objects = new ArrayList<Object>();
          objects.add(eEnum);
          for (EEnumLiteral eEnumLiteral : eEnumLiterals)
          {
            names.add(eEnumLiteral.getName());
            objects.add(eEnumLiteral);
          }
          objects.add(EcorePackage.Literals.EENUM__ELITERALS);
  
          if (names.size() == objects.size() - 2)
          {
            diagnostics.add
              (createDiagnostic
                (Diagnostic.WARNING,
                 DIAGNOSTIC_SOURCE,
                 UNIQUE_ENUMERATOR_NAMES,
                 "_UI_EEnumDissimilarEnumeratorNames_diagnostic",
                 names.toArray(new Object[names.size()]),
                 objects.toArray(new Object[objects.size()]),
                 context));
            
          }
          else
          {
            diagnostics.add
              (createDiagnostic
                (Diagnostic.ERROR,
                 DIAGNOSTIC_SOURCE,
                 UNIQUE_ENUMERATOR_NAMES,
                 "_UI_EEnumUniqueEnumeratorNames_diagnostic",
                 new Object [] { names.get(0) },
                 objects.toArray(new Object[objects.size()]),
                 context));
          }
        }
      }
    }
    return result;
  }

  /**
   * Validates the UniqueEnumeratorLiterals constraint of '<em>EEnum</em>'.
   * <!-- begin-user-doc -->
   * No two enum literals may have the same literal value.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEEnum_UniqueEnumeratorLiterals(EEnum eEnum, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    EList<EEnumLiteral> eLiterals = eEnum.getELiterals();
    Map<String, List<EEnumLiteral>> keys = new HashMap<String, List<EEnumLiteral>>();
    for (EEnumLiteral eEnumLiteral : eLiterals)
    {
      String literal = eEnumLiteral.getLiteral();
      if (literal != null)
      {
        List<EEnumLiteral> eEnumLiterals = keys.get(literal);
        if (eEnumLiterals == null)
        {
          eEnumLiterals = new ArrayList<EEnumLiteral>();
          keys.put(literal, eEnumLiterals);
        }
        eEnumLiterals.add(eEnumLiteral);

        if (eEnumLiterals.size() > 1)
        {
          if (diagnostics == null)
          {
            return false;
          }
          else
          {
            result = false;
            EEnumLiteral otherEEnumLiteral = eEnumLiterals.get(0);
            // Don't complain about the literals if they are the same as the names and the names collide.
            //
            String name = eEnumLiteral.getName();
            if (name != null && name.equals(literal) & name.equals(otherEEnumLiteral.getName()))
            {
              eEnumLiterals.remove(eEnumLiteral);
            }
          }
        }
      }
    }

    if (!result)
    {
      for (Map.Entry<String, List<EEnumLiteral>> entry : keys.entrySet())
      {
        List<EEnumLiteral> eEnumLiterals = entry.getValue();
        if (eEnumLiterals.size() > 1)
        {
          List<Object> objects = new ArrayList<Object>();
          objects.add(eEnum);
          for (EEnumLiteral eEnumLiteral : eEnumLiterals)
          {
            objects.add(eEnumLiteral);
          }
          objects.add(EcorePackage.Literals.EENUM__ELITERALS);
  
          diagnostics.add
            (createDiagnostic
              (Diagnostic.ERROR,
               DIAGNOSTIC_SOURCE,
               UNIQUE_ENUMERATOR_LITERALS,
               "_UI_EEnumUniqueEnumeratorLiterals_diagnostic", 
               new Object[] { entry.getKey() },
               objects.toArray(new Object[objects.size()]),
               context));
        }
      }
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEEnumLiteral(EEnumLiteral eEnumLiteral, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(eEnumLiteral, diagnostics, context)) return false;
    boolean result = validate_EveryMultiplicityConforms(eEnumLiteral, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryDataValueConforms(eEnumLiteral, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(eEnumLiteral, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(eEnumLiteral, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryProxyResolves(eEnumLiteral, diagnostics, context);
    if (result || diagnostics != null) result &= validate_UniqueID(eEnumLiteral, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryKeyUnique(eEnumLiteral, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(eEnumLiteral, diagnostics, context);
    if (result || diagnostics != null) result &= validateENamedElement_WellFormedName(eEnumLiteral, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEFactory(EFactory eFactory, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(eFactory, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEModelElement(EModelElement eModelElement, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(eModelElement, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateENamedElement(ENamedElement eNamedElement, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(eNamedElement, diagnostics, context)) return false;
    boolean result = validate_EveryMultiplicityConforms(eNamedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryDataValueConforms(eNamedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(eNamedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(eNamedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryProxyResolves(eNamedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validate_UniqueID(eNamedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryKeyUnique(eNamedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(eNamedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validateENamedElement_WellFormedName(eNamedElement, diagnostics, context);
    return result;
  }

  protected static boolean isJavaIdentifierStart(int codePoint)
  {
    return true;
  }

  protected static boolean isJavaIdentifierPart(int codePoint)
  {
    return true;
  }

  protected static boolean isWhitespace(int codePoint)
  {
    return codePoint == ' ';
  }

  /**
   * Validates the WellFormedName constraint of '<em>ENamed Element</em>'.
   * <!-- begin-user-doc -->
   * The name must be a valid Java identifier.
   * I.e., it must start with a {@link Character#isJavaIdentifierStart(int) Java identifier start character},
   * that is followed by zero or more {@link Character#isJavaIdentifierPart(int) Java identifier part characters}.
   * This constraint is only enforced in a {@link #STRICT_NAMED_ELEMENT_NAMES} context.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateENamedElement_WellFormedName(ENamedElement eNamedElement, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (context != null && Boolean.FALSE.equals(context.get(STRICT_NAMED_ELEMENT_NAMES)))
    {
      return true;
    }

    boolean result = false;
    String name = eNamedElement.getName();
    if (name != null)
    {
      int length = name.length();
      if (length > 0 && isJavaIdentifierStart(name.codePointAt(0)))
      {
        result = true;
        for (int i = Character.offsetByCodePoints(name, 0, 1); i < length; i = Character.offsetByCodePoints(name, i, 1))
        {
          if (!isJavaIdentifierPart(name.codePointAt(i)))
          {
            result = false;
            break;
          }
        }
      }
    }

    if (!result && diagnostics != null)
    {
      diagnostics.add
        (createDiagnostic
          (Diagnostic.ERROR,
           DIAGNOSTIC_SOURCE,
           WELL_FORMED_NAME,
           "_UI_ENamedElementNameNotWellFormed_diagnostic",
           new Object[] { getValueLabel(EcorePackage.Literals.ESTRING, name, context) },
           new Object[] { eNamedElement, EcorePackage.Literals.ENAMED_ELEMENT__NAME },
           context));
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEObject(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(eObject, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEOperation(EOperation eOperation, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(eOperation, diagnostics, context)) return false;
    boolean result = validate_EveryMultiplicityConforms(eOperation, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryDataValueConforms(eOperation, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(eOperation, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(eOperation, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryProxyResolves(eOperation, diagnostics, context);
    if (result || diagnostics != null) result &= validate_UniqueID(eOperation, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryKeyUnique(eOperation, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(eOperation, diagnostics, context);
    if (result || diagnostics != null) result &= validateENamedElement_WellFormedName(eOperation, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidLowerBound(eOperation, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidUpperBound(eOperation, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ConsistentBounds(eOperation, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidType(eOperation, diagnostics, context);
    if (result || diagnostics != null) result &= validateEOperation_UniqueParameterNames(eOperation, diagnostics, context);
    if (result || diagnostics != null) result &= validateEOperation_UniqueTypeParameterNames(eOperation, diagnostics, context);
    if (result || diagnostics != null) result &= validateEOperation_NoRepeatingVoid(eOperation, diagnostics, context);
    return result;
  }

  /**
   * Validates the UniqueParameterNames constraint of '<em>EOperation</em>'.
   * <!-- begin-user-doc -->
   * No two parameters may have the same name.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEOperation_UniqueParameterNames(EOperation eOperation, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    Map<String, List<EParameter>> keys = new HashMap<String, List<EParameter>>();
    for (EParameter eParameter : eOperation.getEParameters())
    {
      String name = eParameter.getName();
      if (name != null)
      {
        List<EParameter> eParameters = keys.get(name);
        if (eParameters == null)
        {
          eParameters = new ArrayList<EParameter>();
          keys.put(name, eParameters);
        }
        eParameters.add(eParameter);
        if (eParameters.size() > 1)
        {
          if (diagnostics == null)
          {
            return false;
          }
          else
          {
            result = false;
          }
        }
      }
    }

    if (!result)
    {
      for (Map.Entry<String, List<EParameter>> entry : keys.entrySet())
      {
        List<EParameter> eParameters = entry.getValue();
        if (eParameters.size() > 1)
        {
          List<Object> objects = new ArrayList<Object>();
          objects.add(eOperation);
          for (EParameter eParameter : eParameters)
          {
            objects.add(eParameter);
          }
          objects.add(EcorePackage.Literals.EOPERATION__EPARAMETERS);
          diagnostics.add
            (createDiagnostic
              (Diagnostic.ERROR,
               DIAGNOSTIC_SOURCE,
               UNIQUE_PARAMETER_NAMES,
               "_UI_EOperationUniqueParameterNames_diagnostic", 
               new Object[] { entry.getKey() },
               objects.toArray(new Object[objects.size()]),
               context));
        }
      } 
    }
    return result;
  }

  /**
   * Validates the UniqueTypeParameterNames constraint of '<em>EOperation</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEOperation_UniqueTypeParameterNames(EOperation eOperation, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    Map<String, List<ETypeParameter>> keys = new HashMap<String, List<ETypeParameter>>();
    for (ETypeParameter eTypeParameter : eOperation.getETypeParameters())
    {
      String name = eTypeParameter.getName();
      if (name != null)
      {
        List<ETypeParameter> eTypeParameters = keys.get(name);
        if (eTypeParameters == null)
        {
          eTypeParameters = new ArrayList<ETypeParameter>();
          keys.put(name, eTypeParameters);
        }
        eTypeParameters.add(eTypeParameter);
        if (eTypeParameters.size() > 1)
        {
          if (diagnostics == null)
          {
            return false;
          }
          else
          {
            result = false;
          }
        }
      }
    }

    if (!result)
    {
      for (Map.Entry<String, List<ETypeParameter>> entry : keys.entrySet())
      {
        List<ETypeParameter> eTypeParameters = entry.getValue();
        if (eTypeParameters.size() > 1)
        {
          List<Object> objects = new ArrayList<Object>();
          objects.add(eOperation);
          for (ETypeParameter eTypeParameter : eTypeParameters)
          {
            objects.add(eTypeParameter);
          }
          objects.add(EcorePackage.Literals.EOPERATION__ETYPE_PARAMETERS );
          diagnostics.add
            (createDiagnostic
              (Diagnostic.ERROR,
               DIAGNOSTIC_SOURCE,
               UNIQUE_TYPE_PARAMETER_NAMES,
               "_UI_UniqueTypeParameterNames_diagnostic", 
               new Object[] { entry.getKey() },
               objects.toArray(new Object[objects.size()]),
               context));
          
        }
      }
      
    }
    return result;
  }

  /**
   * Validates the NoRepeatingVoid constraint of '<em>EOperation</em>'.
   * <!-- begin-user-doc -->
   * An operation without a type, which represents void, must have an upper bound of 1.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEOperation_NoRepeatingVoid(EOperation eOperation, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    int upperBound = eOperation.getUpperBound();
    boolean result = upperBound == 1 || eOperation.getEType() != null;
    if (!result && diagnostics != null)
    {
      diagnostics.add
        (createDiagnostic
          (Diagnostic.ERROR,
           DIAGNOSTIC_SOURCE,
           NO_REPEATING_VOID,
           "_UI_EOperationNoRepeatingVoid_diagnostic", 
           new Object [] { upperBound },
           new Object[] { eOperation, EcorePackage.Literals.ETYPED_ELEMENT__UPPER_BOUND },
           context));
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEPackage(EPackage ePackage, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(ePackage, diagnostics, context)) return false;
    boolean result = validate_EveryMultiplicityConforms(ePackage, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryDataValueConforms(ePackage, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(ePackage, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(ePackage, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryProxyResolves(ePackage, diagnostics, context);
    if (result || diagnostics != null) result &= validate_UniqueID(ePackage, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryKeyUnique(ePackage, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(ePackage, diagnostics, context);
    if (result || diagnostics != null) result &= validateENamedElement_WellFormedName(ePackage, diagnostics, context);
    if (result || diagnostics != null) result &= validateEPackage_WellFormedNsURI(ePackage, diagnostics, context);
    if (result || diagnostics != null) result &= validateEPackage_WellFormedNsPrefix(ePackage, diagnostics, context);
    if (result || diagnostics != null) result &= validateEPackage_UniqueSubpackageNames(ePackage, diagnostics, context);
    if (result || diagnostics != null) result &= validateEPackage_UniqueClassifierNames(ePackage, diagnostics, context);
    if (result || diagnostics != null) result &= validateEPackage_UniqueNsURIs(ePackage, diagnostics, context);
    return result;
  }

  /**
   * Validates the WellFormedNsURI constraint of '<em>EPackage</em>'.
   * <!-- begin-user-doc -->
   * The namespace URI must be {@link #isWellFormedURI(String) well formed} and may not be <code>null</code>.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEPackage_WellFormedNsURI(EPackage ePackage, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    String nsURI = ePackage.getNsURI();
    boolean result = isWellFormedURI(nsURI);
    if (!result && diagnostics != null)
    {
      diagnostics.add
        (createDiagnostic
          (Diagnostic.ERROR,
           DIAGNOSTIC_SOURCE,
           WELL_FORMED_NS_URI,
           "_UI_EPackageNsURINotWellFormed_diagnostic",
           new Object[] { nsURI },
           new Object[] { ePackage, EcorePackage.Literals.EPACKAGE__NS_URI },
           context));
    }
    return result;
  }

  /**
   * Validates the WellFormedNsPrefix constraint of '<em>EPackage</em>'.
   * <!-- begin-user-doc -->
   * The namespace prefix must be either the empty string
   * or a {@link XMLTypeValidator#validateNCName(String, DiagnosticChain, Map) valid NCName}
   * that does not start with any case combination of the three letters
   * <a href="http://www.w3.org/TR/REC-xml-names/#xmlReserved">"xml"</a>.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEPackage_WellFormedNsPrefix(EPackage ePackage, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    String nsPrefix = ePackage.getNsPrefix();
    boolean
      result = "".equals(nsPrefix) ||
        nsPrefix != null &&
          XMLTypeValidator.INSTANCE.validateNCName(nsPrefix, null, context) &&
          (!nsPrefix.toLowerCase().startsWith("xml") || XMLNamespacePackage.eNS_URI.equals(ePackage.getNsURI()));
    if (!result && diagnostics != null)
    {
      diagnostics.add
        (createDiagnostic
          (Diagnostic.ERROR,
           DIAGNOSTIC_SOURCE,
           WELL_FORMED_NS_PREFIX,
           "_UI_EPackageNsPrefixNotWellFormed_diagnostic",
           new Object[] { nsPrefix },
           new Object[] { ePackage, EcorePackage.Literals.EPACKAGE__NS_PREFIX },
           context));
    }
    return result;
  }

  /**
   * Validates the UniqueSubpackageNames constraint of '<em>EPackage</em>'.
   * <!-- begin-user-doc -->
   * No two packages my have the same name.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEPackage_UniqueSubpackageNames(EPackage ePackage, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    Map<String, List<EPackage>> keys = new HashMap<String, List<EPackage>>();
    for (EPackage eSubpackage : ePackage.getESubpackages())
    {
      String name = eSubpackage.getName();
      if (name != null)
      {
        List<EPackage> eSubpackages = keys.get(name);
        if (eSubpackages == null)
        {
          eSubpackages = new ArrayList<EPackage>();
          keys.put(name,  eSubpackages);
        }
        eSubpackages.add(eSubpackage);
        if (eSubpackages.size() > 1)
        {
          if (diagnostics == null)
          {
            return false;
          }
          else
          {
            result = false;
          }
        }
      }
    }
    
    if (!result)
    {
      for (Map.Entry<String, List<EPackage>> entry : keys.entrySet())
      {
        List<EPackage> eSubpackages = entry.getValue();
        if (eSubpackages.size() > 1)
        {
          List<Object> objects = new ArrayList<Object>();
          objects.add(ePackage);
          for (EPackage eSubpackage : eSubpackages)
          {
            objects.add(eSubpackage);
          }
          objects.add(EcorePackage.Literals.EPACKAGE__ESUBPACKAGES);
          diagnostics.add
            (createDiagnostic
              (Diagnostic.ERROR,
               DIAGNOSTIC_SOURCE,
               UNIQUE_SUBPACKAGE_NAMES,
               "_UI_EPackageUniqueSubpackageNames_diagnostic", 
               new Object[] { entry.getKey() },
               objects.toArray(new Object[objects.size()]),
               context));
        }
      }
    }
    return result;
  }

  /**
   * Validates the UniqueClassifierNames constraint of '<em>EPackage</em>'.
   * <!-- begin-user-doc -->
   * No two classifiers may have matching names.
   * Classifier names are matched ignoring their case and their underscore separators.
   * It is an error to have two classifier with names that are equal but only a warning to have two classifiers with names that match.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEPackage_UniqueClassifierNames(EPackage ePackage, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    Map<String, List<EClassifier>> keys = new HashMap<String, List<EClassifier>>();
    for (EClassifier eClassifier : ePackage.getEClassifiers())
    {
      String name = eClassifier.getName();

      // Don't bother complaining about things with no name,  since there are constraints for that problem.
      //
      if (name != null)
      {
        // Drop the _ separators and normalize the case.
        //
        String key = name.replace("_", "").toLowerCase();
        List<EClassifier> eClassifiers = keys.get(key);
        if (eClassifiers == null)
        {
          eClassifiers = new ArrayList<EClassifier>();
          keys.put(key, eClassifiers);
        }
        eClassifiers.add(eClassifier);

        if (eClassifiers.size() > 1)
        {
          if (diagnostics == null)
          {
            return false;
          }
          else
          {
            result = false;
          }
        }
      }
    }

    if (!result)
    {
      for (Map.Entry<String, List<EClassifier>> entry : keys.entrySet())
      {
        List<EClassifier> eClassifiers = entry.getValue();
        if (eClassifiers.size() > 1)
        {
          List<String> names = new UniqueEList<String>();
          List<Object> objects = new ArrayList<Object>();
          objects.add(ePackage);
          for (EClassifier eClassifier : eClassifiers)
          {
            names.add(eClassifier.getName());
            objects.add(eClassifier);
          }
          objects.add(EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);
            
          if (names.size() == objects.size() - 2)
          {
            diagnostics.add
              (createDiagnostic
                (Diagnostic.WARNING,
                 DIAGNOSTIC_SOURCE,
                 UNIQUE_CLASSIFIER_NAMES,
                 "_UI_EPackageDissimilarClassifierNames_diagnostic",
                 names.toArray(new Object[names.size()]),
                 objects.toArray(new Object[objects.size()]),
                 context));
              
          }
          else
          {
            diagnostics.add
              (createDiagnostic
                (Diagnostic.ERROR,
                 DIAGNOSTIC_SOURCE,
                 UNIQUE_CLASSIFIER_NAMES,
                 "_UI_EPackageUniqueClassifierNames_diagnostic",
                 new Object [] { names.get(0) },
                 objects.toArray(new Object[objects.size()]),
                 context));
          }
        }
      }
    }
    return result;
  }

  /**
   * Validates the UniqueNsURIs constraint of '<em>EPackage</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEPackage_UniqueNsURIs(EPackage ePackage, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    String nsURI = ePackage.getNsURI();
    if (nsURI != null)
    {
      EPackage rootEPackage = ePackage;
      for (EPackage eSuperPackage = ePackage.getESuperPackage(); eSuperPackage != null; eSuperPackage = eSuperPackage.getESuperPackage())
      {
        rootEPackage = eSuperPackage;
      }
      
      UniqueEList<EPackage> ePackages = new UniqueEList.FastCompare<EPackage>();
      ePackages.add(rootEPackage);
      for (int i = 0; i < ePackages.size(); ++i)
      {
        ePackages.addAll(ePackages.get(i).getESubpackages());
      }
      ePackages.remove(ePackage);

      for (EPackage otherEPackage : ePackages)
      {
        if (nsURI.equals(otherEPackage.getNsURI()))
        {
          if (diagnostics == null)
          {
            return false;
          }
          else
          {
            result = false;
            diagnostics.add
              (createDiagnostic
                (Diagnostic.ERROR,
                 DIAGNOSTIC_SOURCE,
                 UNIQUE_NS_URIS,
                 "_UI_EPackageUniqueNsURIs_diagnostic", 
                 new Object[] { nsURI },
                 new Object[] { ePackage, otherEPackage, EcorePackage.Literals.EPACKAGE__ESUBPACKAGES },
                 context));
          }
        }
      }
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEParameter(EParameter eParameter, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(eParameter, diagnostics, context)) return false;
    boolean result = validate_EveryMultiplicityConforms(eParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryDataValueConforms(eParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(eParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(eParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryProxyResolves(eParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validate_UniqueID(eParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryKeyUnique(eParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(eParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validateENamedElement_WellFormedName(eParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidLowerBound(eParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidUpperBound(eParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ConsistentBounds(eParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidType(eParameter, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEReference(EReference eReference, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(eReference, diagnostics, context)) return false;
    boolean result = validate_EveryMultiplicityConforms(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryDataValueConforms(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryProxyResolves(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validate_UniqueID(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryKeyUnique(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validateENamedElement_WellFormedName(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidLowerBound(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidUpperBound(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ConsistentBounds(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidType(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validateEStructuralFeature_ValidDefaultValueLiteral(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validateEReference_ConsistentOpposite(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validateEReference_SingleContainer(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validateEReference_ConsistentKeys(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validateEReference_ConsistentUnique(eReference, diagnostics, context);
    if (result || diagnostics != null) result &= validateEReference_ConsistentContainer(eReference, diagnostics, context);
    return result;
  }

  /**
   * Validates the ConsistentOpposite constraint of '<em>EReference</em>'.
   * <!-- begin-user-doc -->
   * An {@link EReference#getEOpposite() opposite} is optional but if one exists,
   * it must be a feature of this references's {@link EReference#getEReferenceType() type},
   * it must have this reference as its opposite,
   * and, if this feature is {@link EStructuralFeature#isTransient() transient},
   * then the opposite must also be transient,
   * must not {@link EReference#isResolveProxies() resolve proxies}.
   * or must be a {@link EReference#isContainment() containment},
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEReference_ConsistentOpposite(EReference eReference, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    EReference eOpposite = eReference.getEOpposite();
    if (eOpposite != null)
    {
      if (eReference.getEContainingClass() != null)
      {
        EReference oppositeEOpposite = eOpposite.getEOpposite();
        if (oppositeEOpposite != eReference)
        {
          if (diagnostics == null)
          {
            return false;
          }
          else
          {
            result = false;
            diagnostics.add
              (createDiagnostic
                (Diagnostic.ERROR,
                 DIAGNOSTIC_SOURCE,
                 CONSISTENT_OPPOSITE_NOT_MATCHING,
                 "_UI_EReferenceOppositeOfOppositeInconsistent_diagnostic",
                 null,
                 new Object[] { eReference, eOpposite, oppositeEOpposite, EcorePackage.Literals.EREFERENCE__EOPPOSITE },
                 context));
          }
        }
        EClassifier eType = eReference.getEType();
        if (eType != null)
        {
          EClass oppositeEContainingClass = eOpposite.getEContainingClass();
          if (oppositeEContainingClass != null && oppositeEContainingClass != eType)
          {
            if (diagnostics == null)
            {
              return false;
            }
            else
            {
              result = false;
              diagnostics.add
                (createDiagnostic
                  (Diagnostic.ERROR,
                   DIAGNOSTIC_SOURCE,
                   CONSISTENT_OPPOSITE_NOT_FROM_TYPE,
                   "_UI_EReferenceOppositeNotFeatureOfType_diagnostic",
                   null,
                   new Object[] { eReference, eOpposite, eType, EcorePackage.Literals.EREFERENCE__EOPPOSITE },
                   context));
            }
          }
        }
      }
      if (result)
      {
        result =
          !isEffectivelyTransient(eReference) ||
            isEffectivelyTransient(eOpposite) ||
            !eOpposite.isResolveProxies() ||
            eOpposite.isContainment();
        if (diagnostics != null && !result)
        {
          diagnostics.add
            (createDiagnostic
              (Diagnostic.ERROR,
               DIAGNOSTIC_SOURCE,
               CONSISTENT_OPPOSITE_BAD_TRANSIENT,
               "_UI_EReferenceTransientOppositeNotTransient_diagnostic",
               null,
               new Object[] { eReference, eOpposite, EcorePackage.Literals.EREFERENCE__EOPPOSITE, EcorePackage.Literals.ESTRUCTURAL_FEATURE__TRANSIENT },
               context));
        }
      }
      if (result)
      {
        result = !eReference.isContainment() || !eOpposite.isContainment();
        if (diagnostics != null && !result)
        {
          diagnostics.add
            (createDiagnostic
              (Diagnostic.ERROR,
               DIAGNOSTIC_SOURCE,
               CONSISTENT_OPPOSITE_BOTH_CONTAINMENT,
               "_UI_EReferenceOppositeBothContainment_diagnostic",
               null,
               new Object[] { eReference, eOpposite, EcorePackage.Literals.EREFERENCE__EOPPOSITE, EcorePackage.Literals.EREFERENCE__CONTAINMENT },
               context));
        }
      }
    }
    return result;
  }

  /**
   * Validates the SingleContainer constraint of '<em>EReference</em>'.
   * <!-- begin-user-doc -->
   * A {@link EReference#isContainer() container} reference must have a upper bound of 1.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEReference_SingleContainer(EReference eReference, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = !eReference.isContainer() || eReference.getUpperBound() == 1;
    if (diagnostics != null && !result)
    {
      diagnostics.add
        (createDiagnostic
          (Diagnostic.ERROR,
           DIAGNOSTIC_SOURCE,
           SINGLE_CONTAINER,
           "_UI_EReferenceSingleContainer_diagnostic",
           new Object[] { eReference.getUpperBound() },
           new Object[] { eReference, EcorePackage.Literals.ETYPED_ELEMENT__UPPER_BOUND },
           context));
    }
    return result;
  }


  /**
   * Validates the ConsistentKeys constraint of '<em>EReference</em>'.
   * <!-- begin-user-doc -->
   * The {@link EReference#getEKeys() keys} of a reference must be features of the reference's {@link ETypedElement#getEType()}.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEReference_ConsistentKeys(EReference eReference, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    EList<EAttribute> eKeys = eReference.getEKeys();
    if (!eKeys.isEmpty())
    {
      EClass eClass = eReference.getEReferenceType();
      if (eClass != null)
      {
        for (EAttribute eAttribute :eKeys)
        {
          if (eClass.getFeatureID(eAttribute) == -1)
          {
            if (diagnostics == null)
            {
              return false;
            }
            else
            {
              result = false;
              diagnostics.add
                (createDiagnostic
                  (Diagnostic.ERROR,
                   DIAGNOSTIC_SOURCE,
                   CONSISTENT_KEYS,
                   "_UI_EReferenceConsistentKeys_diagnostic",
                   new Object[] { getObjectLabel(eAttribute, context) },
                   new Object[] { eReference, eAttribute, EcorePackage.Literals.EREFERENCE__EKEYS },
                   context));
            }
          }
        }
      }
    }
    return result;
  }

  /**
   * Validates the ConsistentUnique constraint of '<em>EReference</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEReference_ConsistentUnique(EReference eReference, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    // Multi-valued references that are containment or bidirectional must be unique.
    //
    boolean result = true;
    if (eReference.isMany() && 
          (eReference.isContainment() || eReference.getEOpposite() != null) &&
          !eReference.isUnique())
    {
      result = false;
      if (diagnostics != null)
      {
        diagnostics.add
          (createDiagnostic
            (Diagnostic.ERROR,
             DIAGNOSTIC_SOURCE,
             CONSISTENT_UNIQUE,
             "_UI_EReferenceConsistentUnique_diagnostic",
             null,
             new Object[] { eReference, EcorePackage.Literals.ETYPED_ELEMENT__UNIQUE },
             context));
      }
    }
    return result;
  }

  /**
   * Validates the ConsistentContainer constraint of '<em>EReference</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEReference_ConsistentContainer(EReference eReference, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (eReference.isContainment() && eReference.getEContainingClass() != null)
    {
      EClass eClass = eReference.getEReferenceType();
      if (eClass != null)
      {
        for (EReference otherEReference : eClass.getEAllReferences())
        {
          if (otherEReference.isRequired() && otherEReference.isContainer() && otherEReference.getEOpposite() != eReference)
          {
            if (diagnostics != null)
            {
              diagnostics.add
                (createDiagnostic
                  (Diagnostic.ERROR,
                   DIAGNOSTIC_SOURCE,
                   CONSISTENT_CONTAINER,
                   "_UI_EReferenceConsistentContainer_diagnostic",
                   new Object[] { getObjectLabel(otherEReference, context) },
                   new Object[] { eReference, otherEReference, EcorePackage.Literals.ETYPED_ELEMENT__LOWER_BOUND },
                   context));
            }
            return false;
          }
        }
      }
    }
    return true; 
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEStructuralFeature(EStructuralFeature eStructuralFeature, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(eStructuralFeature, diagnostics, context)) return false;
    boolean result = validate_EveryMultiplicityConforms(eStructuralFeature, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryDataValueConforms(eStructuralFeature, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(eStructuralFeature, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(eStructuralFeature, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryProxyResolves(eStructuralFeature, diagnostics, context);
    if (result || diagnostics != null) result &= validate_UniqueID(eStructuralFeature, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryKeyUnique(eStructuralFeature, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(eStructuralFeature, diagnostics, context);
    if (result || diagnostics != null) result &= validateENamedElement_WellFormedName(eStructuralFeature, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidLowerBound(eStructuralFeature, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidUpperBound(eStructuralFeature, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ConsistentBounds(eStructuralFeature, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidType(eStructuralFeature, diagnostics, context);
    if (result || diagnostics != null) result &= validateEStructuralFeature_ValidDefaultValueLiteral(eStructuralFeature, diagnostics, context);
    return result;
  }

  /**
   * Validates the ValidDefaultValueLiteral constraint of '<em>EStructural Feature</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEStructuralFeature_ValidDefaultValueLiteral(EStructuralFeature eStructuralFeature, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    String defaultValueLiteral = eStructuralFeature.getDefaultValueLiteral();
    Object defaultValue = null;
    EDataType eDataType = null;
    boolean result = true;
    boolean warning = false;
    if (defaultValueLiteral != null)
    {
      EClassifier eType = eStructuralFeature.getEType();
      if (eType instanceof EDataType)
      {
        eDataType = (EDataType)eType;
        defaultValue = eStructuralFeature.getDefaultValue();
        if (defaultValue == null)
        {
          // We need to be conservative and diagnose a problem only if we are quite sure that type is built-in 
          // and hence that the lack of a default value really represents a problem with being unable to convert the literal to a value dynamically,
          // not just a problem that the specialized factory conversion logic hasn't been generated yet.
          // 
          if (isBuiltinEDataType(eDataType))
          {
            result = false;
          }
          else
          {
            // If there is a conversion delegate then the lack of a default value really does indicate that there is a problem converting the literal to a value.
            //
            EDataType.Internal.ConversionDelegate conversionDelegate = ((EDataType.Internal)eDataType).getConversionDelegate();
            if (conversionDelegate != null)
            {
              result = false;
            }
            else
            {
              // If the data type is an enum or derives from an enum 
              // then it's unlikely there is ever specialized code for converting the value 
              // so probably the literal is bad and we should at least produce a warning.
              //
              EEnum eEnum = getEEnum(eDataType);
              if (eEnum != null)
              {
                result = false;
                warning = true;
              }
            }
          }
        }
        else
        {
          result = getRootEValidator(context).validate(eDataType, defaultValue, null, context);
        }
      }
      else
      {
        result = false;
      }
    }
    if (diagnostics != null && !result)
    {
      BasicDiagnostic diagnostic =
        createDiagnostic
         (warning? Diagnostic.WARNING : Diagnostic.ERROR,
          DIAGNOSTIC_SOURCE,
          VALID_DEFAULT_VALUE_LITERAL,
          "_UI_EStructuralFeatureValidDefaultValueLiteral_diagnostic",
          new Object[] { defaultValueLiteral },
          new Object[] { eStructuralFeature, EcorePackage.Literals.ESTRUCTURAL_FEATURE__DEFAULT_VALUE_LITERAL },
          context);
      if (defaultValue != null)
      {
        getRootEValidator(context).validate(eDataType, defaultValue, diagnostic, context);
      }
      diagnostics.add(diagnostic);
    }
    return result;
  }
  
  private EEnum getEEnum(EDataType eDataType)
  {
    if (eDataType instanceof EEnum)
    {
      return (EEnum)eDataType;
    }

    EDataType baseType = ExtendedMetaData.INSTANCE.getBaseType(eDataType);
    if (baseType != null)
    {
      return getEEnum(baseType);
    }

    return null;
  }

  protected boolean isBuiltinEDataType(EDataType eDataType)
  {
    EPackage ePackage = eDataType.getEPackage();
    if (ePackage == EcorePackage.eINSTANCE || ePackage == XMLTypePackage.eINSTANCE || ePackage == XMLNamespacePackage.eINSTANCE)
    {
      return true;
    }
    
    EDataType baseType = ExtendedMetaData.INSTANCE.getBaseType(eDataType);
    if (baseType != null)
    {
      return isBuiltinEDataType(baseType);
    }

    EDataType itemType = ExtendedMetaData.INSTANCE.getItemType(eDataType);
    if (itemType != null)
    {
      return isBuiltinEDataType(itemType);
    }

    List<EDataType> memberTypes = ExtendedMetaData.INSTANCE.getMemberTypes(eDataType);
    if (!memberTypes.isEmpty())
    {
      for (EDataType memberType : memberTypes)
      {
        if (!isBuiltinEDataType(memberType))
        {
          return false;
        }
      }
      return true;
    }

    return false;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateETypedElement(ETypedElement eTypedElement, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(eTypedElement, diagnostics, context)) return false;
    boolean result = validate_EveryMultiplicityConforms(eTypedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryDataValueConforms(eTypedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(eTypedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(eTypedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryProxyResolves(eTypedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validate_UniqueID(eTypedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryKeyUnique(eTypedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(eTypedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validateENamedElement_WellFormedName(eTypedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidLowerBound(eTypedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidUpperBound(eTypedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ConsistentBounds(eTypedElement, diagnostics, context);
    if (result || diagnostics != null) result &= validateETypedElement_ValidType(eTypedElement, diagnostics, context);
    return result;
  }

  /**
   * Validates the ValidLowerBound constraint of '<em>ETyped Element</em>'.
   * <!-- begin-user-doc -->
   * The {@link ETypedElement#getLowerBound() lower bound} must be greater or equal to 0
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateETypedElement_ValidLowerBound(ETypedElement eTypedElement, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    int lowerBound = eTypedElement.getLowerBound();
    boolean result = lowerBound >= 0;
    if (diagnostics != null && !result)
    {
      diagnostics.add
        (createDiagnostic
          (Diagnostic.ERROR,
           DIAGNOSTIC_SOURCE,
           VALID_LOWER_BOUND,
           "_UI_ETypedElementValidLowerBound_diagnostic",
           new Object[] { lowerBound },
           new Object[] { eTypedElement, EcorePackage.Literals.ETYPED_ELEMENT__LOWER_BOUND },
           context));
    }
    return result;
  }

  /**
   * Validates the ValidUpperBound constraint of '<em>ETyped Element</em>'.
   * <!-- begin-user-doc -->
   * The {@link ETypedElement#getUpperBound() upper bound} must be either
   * {@link ETypedElement#UNBOUNDED_MULTIPLICITY},
   * {@link ETypedElement#UNSPECIFIED_MULTIPLICITY},
   * or greater than 0.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateETypedElement_ValidUpperBound(ETypedElement eTypedElement, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    int upperBound = eTypedElement.getUpperBound();
    boolean result =
      upperBound > 0 ||
        upperBound == ETypedElement.UNSPECIFIED_MULTIPLICITY ||
        upperBound == ETypedElement.UNBOUNDED_MULTIPLICITY;
    if (diagnostics != null && !result)
    {
      diagnostics.add
        (createDiagnostic
          (Diagnostic.ERROR,
           DIAGNOSTIC_SOURCE,
           VALID_UPPER_BOUND,
           "_UI_ETypedElementValidUpperBound_diagnostic",
           new Object[] { upperBound },
           new Object[] { eTypedElement, EcorePackage.Literals.ETYPED_ELEMENT__UPPER_BOUND },
           context));
    }
    return result;
  }

  /**
   * Validates the ConsistentBounds constraint of '<em>ETyped Element</em>'.
   * <!-- begin-user-doc -->
   * The {@link ETypedElement#getLowerBound() lower bound} must be less than or equal to the {@link ETypedElement#getUpperBound() upper bound},
   * unless the upper bound is one of the two special values
   * {@link ETypedElement#UNBOUNDED_MULTIPLICITY} or {@link ETypedElement#UNSPECIFIED_MULTIPLICITY}.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateETypedElement_ConsistentBounds(ETypedElement eTypedElement, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    int lowerBound = eTypedElement.getLowerBound();
    int upperBound = eTypedElement.getUpperBound();
    boolean result = upperBound < 0 || lowerBound <= upperBound;
    if (diagnostics != null && !result)
    {
      diagnostics.add
        (createDiagnostic
          (Diagnostic.ERROR,
           DIAGNOSTIC_SOURCE,
           CONSISTENT_BOUNDS,
           "_UI_ETypedElementConsistentBounds_diagnostic", 
           new Object[] { lowerBound, upperBound },
           new Object[] { eTypedElement,EcorePackage.Literals.ETYPED_ELEMENT__LOWER_BOUND, EcorePackage.Literals.ETYPED_ELEMENT__UPPER_BOUND },
           context));
    }
    return result;
  }

  /**
   * Validates the ValidType constraint of '<em>ETyped Element</em>'.
   * <!-- begin-user-doc -->
   * The {@link ETypedElement#getEGenericType() type} may be <code>null</code> only if this in an {@link EOperation operation}.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateETypedElement_ValidType(ETypedElement eTypedElement, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    EGenericType eGenericType = eTypedElement.getEGenericType();
    if (eGenericType == null && !(eTypedElement instanceof EOperation))
    {
      result = false;
      if (diagnostics != null)
      {
        diagnostics.add
          (createDiagnostic
            (Diagnostic.ERROR,
             DIAGNOSTIC_SOURCE,
             VALID_TYPE,
             "_UI_ETypedElementNoType_diagnostic",
             null,
             new Object[] { eTypedElement, EcorePackage.Literals.ETYPED_ELEMENT__ETYPE },
             context));
      }
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEStringToStringMapEntry(Map.Entry<?, ?> eStringToStringMapEntry, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint((EObject)eStringToStringMapEntry, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEGenericType(EGenericType eGenericType, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(eGenericType, diagnostics, context)) return false;
    boolean result = validate_EveryMultiplicityConforms(eGenericType, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryDataValueConforms(eGenericType, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(eGenericType, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(eGenericType, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryProxyResolves(eGenericType, diagnostics, context);
    if (result || diagnostics != null) result &= validate_UniqueID(eGenericType, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryKeyUnique(eGenericType, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(eGenericType, diagnostics, context);
    if (result || diagnostics != null) result &= validateEGenericType_ConsistentType(eGenericType, diagnostics, context);
    if (result || diagnostics != null) result &= validateEGenericType_ConsistentBounds(eGenericType, diagnostics, context);
    if (result || diagnostics != null) result &= validateEGenericType_ConsistentArguments(eGenericType, diagnostics, context);
    return result;
  }

  /**
   * Validates the ConsistentType constraint of '<em>EGeneric Type</em>'.
   * <!-- begin-user-doc -->
   * A generic type must not reference both a {@link EGenericType#getEClassifier() classifier}
   * and a {@link EGenericType#getETypeParameter() type parameter}.
   * The referenced type parameter must be in scope, i.e.,
   * its {@link EObject#eContainer()} must be an {@link EcoreUtil#isAncestor(EObject, EObject)} of this generic type.
   * A generic type used as a {@link EClass#getEGenericSuperTypes() generic super type}
   * must have a classifier that refers to a {@link EClass class}.
   * A generic type used as a {@link EGenericType#getETypeArguments() type argument} of a generic type used as a generic super type
   * must specify either a classifier or a type parameter, i.e., it can't be a wildcard.
   * A generic type may omit both the classifier and the type argument to act as a wildcard
   * only when used as a type argument of some generic type,
   * with the above exception.
   * If present, the classifier of generic type used as the {@link ETypedElement#getEType() type} of an {@link EAttribute attribute}
   * must be a {@link EDataType data type}.
   * If present, the classifier of generic type used as the type of a {@link EReference reference}
   * must be a class.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEGenericType_ConsistentType(EGenericType eGenericType, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;

    ETypeParameter eTypeParameter = eGenericType.getETypeParameter();
    EClassifier eClassifier = eGenericType.getEClassifier();
    if (eTypeParameter != null)
    {
      if (eClassifier != null)
      {
        // Can't have both a classifier and a type parameter.
        //
        if (diagnostics == null)
        {
          return false;
        }
        else
        {
          result = false;
          diagnostics.add
            (createDiagnostic
               (Diagnostic.ERROR,
                DIAGNOSTIC_SOURCE,
                CONSISTENT_TYPE_NO_TYPE_PARAMETER_AND_CLASSIFIER,
                "_UI_EGenericTypeNoTypeParameterAndClassifier_diagnostic",
                null,
                new Object[] { eGenericType, EcorePackage.Literals.EGENERIC_TYPE__ECLASSIFIER, EcorePackage.Literals.EGENERIC_TYPE__ETYPE_PARAMETER },
                context));
        }
      }
      
      // The referencing generic type must be contained to be in scope
      //
      EObject scope = eTypeParameter.eContainer();
      boolean  inScope = EcoreUtil.isAncestor(scope, eGenericType);
      if (inScope)
      {
        // And even if it is contained, it must not be a forward reference.
        // eTypeParameterIndex == index is allowed when the type parameter is 
        // the type argument of the bound, though,
        // i.e., when the type argument is not nested directly as a child of the type parameter.
        //
        List<?> typeParameters = (List<?>)scope.eGet(eTypeParameter.eContainmentFeature());
        EObject usage = eGenericType; 
        for (EObject container = usage.eContainer(); container != scope; container = container.eContainer())
        {
          usage = container;
        }
        int index = typeParameters.indexOf(usage);
        int eTypeParameterIndex = typeParameters.indexOf(eTypeParameter);
        inScope = index == -1 || 
          index > eTypeParameterIndex ||
          eGenericType.eContainingFeature() != EcorePackage.Literals.ETYPE_PARAMETER__EBOUNDS;
      }

      if (!inScope)
      {
        // The type parameter must be in scope and must not be a forward reference.
        //
        if (diagnostics == null)
        {
          return false;
        }
        else
        {
          result = false;
          diagnostics.add
            (createDiagnostic
               (Diagnostic.ERROR,
                DIAGNOSTIC_SOURCE,
                CONSISTENT_TYPE_TYPE_PARAMETER_NOT_IN_SCOPE,
                "_UI_EGenericTypeOutOfScopeTypeParameter_diagnostic",
                null,
                new Object[] { eGenericType, EcorePackage.Literals.EGENERIC_TYPE__ETYPE_PARAMETER },
                context));
        }
      }
    }

    EReference eContainmentFeature = eGenericType.eContainmentFeature();
    if (eContainmentFeature == EcorePackage.Literals.ECLASS__EGENERIC_SUPER_TYPES)
    {
      // When used as a generic super type, there must be a classifier that refers to a class.
      //
      if (!(eGenericType.getEClassifier() instanceof EClass))
      {
        if (diagnostics == null)
        {
          return false;
        }
        else
        {
          result = false;
          diagnostics.add
            (createDiagnostic
              (Diagnostic.ERROR,
               DIAGNOSTIC_SOURCE,
               CONSISTENT_TYPE_CLASS_REQUIRED,
               "_UI_EGenericTypeNoClass_diagnostic",
               null,
               new Object[] { eGenericType, EcorePackage.Literals.EGENERIC_TYPE__ECLASSIFIER },
               context));
        }
      }
    }
    else if (eContainmentFeature == EcorePackage.Literals.EGENERIC_TYPE__ETYPE_ARGUMENTS)
    {
      if (eGenericType.eContainer().eContainmentFeature() == EcorePackage.Literals.ECLASS__EGENERIC_SUPER_TYPES)
      {
        // The type arguments of a generic super type must not be a wildcard.
        //
        if (eClassifier == null && eTypeParameter == null)
        {
          if (diagnostics == null)
          {
            return false;
          }
          else
          {
            result = false;
            diagnostics.add
              (createDiagnostic
                (Diagnostic.ERROR,
                 DIAGNOSTIC_SOURCE,
                 CONSISTENT_TYPE_WILDCARD_NOT_PERMITTED,
                 "_UI_EGenericTypeNoTypeParameterOrClassifier_diagnostic",
                 null,
                 new Object[] { eGenericType, EcorePackage.Literals.EGENERIC_TYPE__ECLASSIFIER, EcorePackage.Literals.EGENERIC_TYPE__ETYPE_PARAMETER },
                 context));
          }
        }
      }
    }
    else if (eContainmentFeature != null)
    {
      // Wildcards are only allowed in type arguments.
      //
      if (eClassifier == null && eTypeParameter == null)
      {
        if (diagnostics == null)
        {
          return false;
        }
        else
        {
          result = false;
          diagnostics.add
            (createDiagnostic
              (Diagnostic.ERROR,
               DIAGNOSTIC_SOURCE,
               CONSISTENT_TYPE_WILDCARD_NOT_PERMITTED,
               "_UI_EGenericTypeNoTypeParameterOrClassifier_diagnostic",
               null,
               new Object[] { eGenericType, EcorePackage.Literals.EGENERIC_TYPE__ECLASSIFIER, EcorePackage.Literals.EGENERIC_TYPE__ETYPE_PARAMETER },
               context));
        }
      }
      else if (eClassifier != null)
      {
        EObject eContainer = eGenericType.eContainer();
        if (eContainer instanceof EStructuralFeature)
        {
          if (eClassifier instanceof EClass)
          {
            if (eContainer instanceof EAttribute)
            {
              // The classifier of an attribute's generic type must be a data type.
              //
              if (diagnostics == null)
              {
                return false;
              }
              else
              {
                result = false;
                diagnostics.add
                  (createDiagnostic
                    (Diagnostic.ERROR,
                     DIAGNOSTIC_SOURCE,
                     CONSISTENT_TYPE_CLASS_NOT_PERMITTED,
                     "_UI_EAttributeNoDataType_diagnostic",
                     null,
                     new Object[] { eGenericType, EcorePackage.Literals.EGENERIC_TYPE__ECLASSIFIER },
                     context));
              }
            }
          }
          else if (eClassifier instanceof EDataType)
          {
            if (eContainer instanceof EReference)
            {
              // The classifier of an references's generic type must be a class.
              //
              if (diagnostics == null)
              {
                return false;
              }
              else
              {
                result = false;
                diagnostics.add
                  (createDiagnostic
                    (Diagnostic.ERROR,
                     DIAGNOSTIC_SOURCE,
                     CONSISTENT_TYPE_DATA_TYPE_NOT_PERMITTED,
                     "_UI_EReferenceNoClass_diagnostic",
                     null,
                     new Object[] { eGenericType, EcorePackage.Literals.EGENERIC_TYPE__ECLASSIFIER },
                     context));
              }
            }
          }
        }
      }
    }
    
    if (eClassifier != null && eContainmentFeature != null && eContainmentFeature != EcorePackage.Literals.ETYPED_ELEMENT__EGENERIC_TYPE)
    {
      // A primitive type can only be used as the generic type of a typed element.
      //
      String instanceClassName = eClassifier.getInstanceClassName();
      if (instanceClassName == "boolean" ||
            instanceClassName == "byte" ||
            instanceClassName == "char" ||
            instanceClassName == "double" ||
            instanceClassName == "float" ||
            instanceClassName == "int" ||
            instanceClassName == "long" ||
            instanceClassName == "short")
      {
        if (diagnostics == null)
        {
          return false;
        }
        else
        {
          result = false;
          diagnostics.add
            (createDiagnostic
              (Diagnostic.ERROR,
               DIAGNOSTIC_SOURCE,
               CONSISTENT_TYPE_PRIMITIVE_TYPE_NOT_PERMITTED,
               "_UI_EGenericTypeInvalidPrimitiveType_diagnostic",
               new Object[] { instanceClassName },
               new Object[] { eGenericType, EcorePackage.Literals.EGENERIC_TYPE__ECLASSIFIER },
               context));
        }
      }
    }
    return result;
  }

  /**
   * Validates the ConsistentBounds constraint of '<em>EGeneric Type</em>'.
   * <!-- begin-user-doc -->
   * A generic type may have bounds only when used as a {@link EGenericType#getETypeArguments() type argument}.
   * A generic type may not have both a {@link EGenericType#getELowerBound() lower} and an {@link EGenericType#getEUpperBound() upper bound}.
   * A generic type may not have bounds
   * as well as a {@link EGenericType#getEClassifier() classifier} or a {@link EGenericType#getETypeParameter() type parameter}.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEGenericType_ConsistentBounds(EGenericType eGenericType, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;

    EGenericType eLowerBound = eGenericType.getELowerBound();
    EGenericType eUpperBound = eGenericType.getEUpperBound();
    if (eLowerBound != null || eUpperBound != null)
    {
      EStructuralFeature eContainmentFeature = eGenericType.eContainmentFeature();
      if (eContainmentFeature == EcorePackage.Literals.EGENERIC_TYPE__ETYPE_ARGUMENTS)
      {
        // Can't have both an upper and lower bound.
        //
        if (eLowerBound != null && eUpperBound != null)
        {
          if (diagnostics == null)
          {
            return false;
          }
          else
          {
            result = false;
            diagnostics.add
              (createDiagnostic
                (Diagnostic.ERROR,
                 DIAGNOSTIC_SOURCE,
                 CONSISTENT_BOUNDS_NO_LOWER_AND_UPPER,
                 "_UI_EGenericTypeNoUpperAndLowerBound_diagnostic",
                 null,
                 new Object[] { eGenericType, EcorePackage.Literals.EGENERIC_TYPE__ELOWER_BOUND, EcorePackage.Literals.EGENERIC_TYPE__EUPPER_BOUND },
                 context));
          }
        }

        // Can't have a classifier or a type parameter as well as bounds.
        //
        if (eGenericType.getEClassifier() != null || eGenericType.getETypeParameter() != null)
        {
          if (diagnostics == null)
          {
            return false;
          }
          else
          {
            result = false;
            diagnostics.add
              (createDiagnostic
                (Diagnostic.ERROR,
                 DIAGNOSTIC_SOURCE,
                 CONSISTENT_BOUNDS_NO_BOUNDS_WITH_TYPE_PARAMETER_OR_CLASSIFIER,
                 "_UI_EGenericTypeNoTypeParameterOrClassifierAndBound_diagnostic",
                 null,
                 new Object[] { eGenericType, eGenericType.getEClassifier() != null ? EcorePackage.Literals.EGENERIC_TYPE__ECLASSIFIER : EcorePackage.Literals.EGENERIC_TYPE__ETYPE_PARAMETER },
                 context));
          }
        }
      }
      else
      {
        // Can only have bounds when used as a type argument.
        //
        if (diagnostics == null)
        {
          return false;
        }
        else
        {
          result = false;
          diagnostics.add
            (createDiagnostic
              (Diagnostic.ERROR,
               DIAGNOSTIC_SOURCE,
               CONSISTENT_BOUNDS_NOT_ALLOWED,
               "_UI_EGenericTypeBoundsOnlyForTypeArgument_diagnostic",
               null,
               new Object[] { eGenericType, eLowerBound != null ? EcorePackage.Literals.EGENERIC_TYPE__ELOWER_BOUND : EcorePackage.Literals.EGENERIC_TYPE__EUPPER_BOUND },
               context));
        }
      }
    }
    return result;
  }

  /**
   * Validates the ConsistentArguments constraint of '<em>EGeneric Type</em>'.
   * <!-- begin-user-doc -->
   * A generic type can have {@link EGenericType#getETypeArguments() type arguments}
   * only if it has a {@link EGenericType#getEClassifier() classifier} that specifies {@link EClassifier#getETypeParameters()};
   * the number of type arguments must match the number of type parameters.
   * It is only a warning for there to be no arguments when there are parameters, but any other mismatch is an error.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateEGenericType_ConsistentArguments(EGenericType eGenericType, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    EClassifier eClassifier = eGenericType.getEClassifier();
    EList<EGenericType> eTypeArguments = eGenericType.getETypeArguments();
    int eTypeArgumentSize = eTypeArguments.size();
    if (eClassifier == null)
    {
      if (eTypeArgumentSize != 0)
      {
        // Can't have type arguments unless there is a classifier
        //
        if (diagnostics == null)
        {
          return false;
        }
        else
        {
          result = false;
          diagnostics.add
            (createDiagnostic
              (Diagnostic.ERROR,
               DIAGNOSTIC_SOURCE,
               CONSISTENT_ARGUMENTS_NONE_ALLOWED,
               "_UI_EGenericTypeNoArguments_diagnostic",
               null,
               new Object[] { eGenericType, EcorePackage.Literals.EGENERIC_TYPE__ETYPE_ARGUMENTS },
               context));
        }
      }
    }
    else
    {
      EList<ETypeParameter> eTypeParameters = eClassifier.getETypeParameters();
      int eTypeParameterSize = eTypeParameters.size();
      if (eTypeArgumentSize == 0)
      {
        if (eTypeParameterSize > 0)
        {
          // Have no arguments when they are allowed is only a warning.
          //
          if (diagnostics == null)
          {
            return false;
          }
          else
          {
            result = false;
            diagnostics.add
              (createDiagnostic
                (Diagnostic.WARNING,
                 DIAGNOSTIC_SOURCE,
                 CONSISTENT_ARGUMENTS_NONE,
                 "_UI_EGenericTypeArgumentsNeeded_diagnostic",
                 new Object [] { eClassifier.getName(), eTypeParameterSize },
                 new Object[] { eGenericType, EcorePackage.Literals.EGENERIC_TYPE__ETYPE_ARGUMENTS },
                 context));

          }
        }
      }
      else if (eTypeArgumentSize != eTypeParameters.size())
      {
        // Incorrect number of type arguments.
        //
        if (diagnostics == null)
        {
          return false;
        }
        else
        {
          result = false;
          diagnostics.add
            (createDiagnostic
              (Diagnostic.ERROR,
               DIAGNOSTIC_SOURCE,
               CONSISTENT_ARGUMENTS_INCORRECT_NUMBER,
               "_UI_EGenericTypeIncorrectArguments_diagnostic", 
               new Object [] { eClassifier.getName(), eTypeArgumentSize, eTypeParameterSize },
               new Object[] { eGenericType, EcorePackage.Literals.EGENERIC_TYPE__ETYPE_ARGUMENTS },
               context));

        }
      }
      else
      {
        Map<ETypeParameter, EGenericType> substitutions = new HashMap<ETypeParameter, EGenericType>();
        for (int i = 0; i < eTypeParameterSize; ++i)
        {
          ETypeParameter eTypeParameter = eTypeParameters.get(i);
          EGenericType eTypeArgument = eTypeArguments.get(i);
          substitutions.put(eTypeParameter, eTypeArgument);
        }
        for (int i = 0; i < eTypeParameterSize; ++i)
        {
          ETypeParameter eTypeParameter = eTypeParameters.get(i);
          EGenericType eTypeArgument = eTypeArguments.get(i);
          if (!isValidSubstitution(eTypeArgument, eTypeParameter, substitutions))
          {
            if (diagnostics == null)
            {
              return false;
            }
            else
            {
              result = false;
              diagnostics.add
                (createDiagnostic
                  (Diagnostic.ERROR,
                   DIAGNOSTIC_SOURCE,
                   CONSISTENT_ARGUMENTS_INVALID_SUBSTITUTION,
                   "_UI_EGenericTypeArgumentInvalidSubstitution_diagnostic", 
                    new Object [] 
                    { 
                      getObjectLabel(eTypeArgument, context), 
                      getObjectLabel(eTypeParameter, context) 
                    },
                   new Object[] { eGenericType, eTypeArgument, eTypeParameter, EcorePackage.Literals.EGENERIC_TYPE__ETYPE_ARGUMENTS },
                   context));
    
            }
          }
        }
      }
    }
    return result;
  }

  /**
   * Returns whether the generic type argument is a valid substitution for the type parameter.
   * A generic type is a valid substitution 
   * if it is {@link #isBounded(EGenericType, EGenericType, Map) bounded} by 
   * every {@link ETypeParameter#getEBounds() bound} of the type parameter.
   * It follows that for a type parameter without bounds, every type argument is a valid substitution.
   * @param eTypeArgument the generic type argument to consider.
   * @param eTypeParameter the type parameter in question.
   * @return whether the generic type argument is a valid substitution for the type parameter.
   */
  protected boolean isValidSubstitution(EGenericType eTypeArgument, ETypeParameter eTypeParameter, Map<ETypeParameter, EGenericType> substitutions)
  {
    EList<EGenericType> eBounds = eTypeParameter.getEBounds();
    if (!eBounds.isEmpty())
    {
      if (eTypeArgument.getEClassifier() == null && 
            eTypeArgument.getETypeParameter() == null &&
            eTypeArgument.getEUpperBound() == null && 
            eTypeArgument.getELowerBound() == null)
      {
        return true;
      }
      for (EGenericType eBound : eBounds)
      {
        if (!isBounded(eTypeArgument, eBound, substitutions))
        {
          return false;
        }
      }
    }
    return true;
  }
  
  /**
   * Returns whether the first generic type is bounded by the second.
   * If they both reference a classifier, 
   * then the classifier of the first must be bounded by the classifier of the second,
   * and the type arguments must {@link #matchingTypeArguments(EList, EList, Map) match}.
   * A classifier is bounded by another classifier,
   * if they are the same classifier,
   * if both are classes and the first is a {@link EClass#isSuperTypeOf(EClass)} the second,
   * or if both have an non-null {@link EClassifier#getInstanceClass() instance class} 
   * for which the first is {@link Class#isAssignableFrom(Class) assignable from} the second,
   * or, failing all these, if they have non-null {@link EClassifier#getInstanceTypeName()} that are equal.
   * If the bound references a classifier, and the generic type argument references a type parameter,
   * one of the {@link ETypeParameter#getEBounds() bounds} of that type parameter must be bounded by bound.
   * If the bound has a {@link EGenericType#getELowerBound() lower bound}, 
   * the generic type argument must be bounded by that lower bound.
   * If the bound has an {@link EGenericType#getEUpperBound() upper bound}, 
   * the generic type argument must be bounded by that upper bound.
   * If the bound references a type parameter,
   * the generic type argument must be bounded by every bound of that type parameter.
   * If the bound has a lower bound, 
   * the generic type argument must be bounded by it.
   * If the bound has an upper bound, 
   * the generic type argument must be bound that upper bound.
   * Failing all these cases, the bound is a wildcard with no constraint, and the type argument is bounded.
   * @param eGenericType the generic type in question.
   * @param eBound the bound it's being assessed against.
   * @param substitutions the map of substitutions that are in effect.
   * @return whether the first generic type is bounded by the second.
   */
  public static boolean isBounded(EGenericType eGenericType, EGenericType eBound, Map<? extends ETypeParameter, ? extends EGenericType> substitutions)
  {
    if (eGenericType == eBound)
    {
      return true;
    }

    // Check if the bound specifies a classifier...
    //
    EClassifier eBoundEClassifier = eBound.getEClassifier();
    if (eBoundEClassifier != null)
    {
      // If the type also specifies a classifier...
      //
      EClassifier eClassifier = eGenericType.getEClassifier();
      if (eClassifier != null)
      {
        // If the are the same then it is bounded properly...
        //
        if (eBoundEClassifier != eClassifier)
        {
          // We test their relationship either via them both being classes...
          //
          if (eBoundEClassifier instanceof EClass && eClassifier instanceof EClass)
          {
            EClass eClass = (EClass)eClassifier;

            // Since we will do the processing recursively, we need to ensure we don't stack overflow if there is a circular super type.
            //
            if (INSTANCE.validateEClass_NoCircularSuperTypes(eClass, null, null))
            {
              // Determine if there is a bounding generic super type.
              //
              for (EGenericType eGenericSuperType : eClass.getEGenericSuperTypes())
              {
                // Set up the substitutions of any type parameters this class has with respect to the type arguments for them.
                //
                Map<? extends ETypeParameter, ? extends EGenericType> localSubstitutions = substitutions;
  
                // Test if there are type parameters that might require substitution.
                //
                EList<ETypeParameter> eTypeParameters = eClass.getETypeParameters();
                int size = eTypeParameters.size();
                if (size > 0)
                {
                  EList<EGenericType> eTypeArguments = eGenericType.getETypeArguments();
                  if (size == eTypeArguments.size())
                  {
                    HashMap<ETypeParameter, EGenericType> additionalLocalSubstitutions = new HashMap<ETypeParameter, EGenericType>(substitutions);
                    for (int i = 0; i < size; ++i)
                    {
                      additionalLocalSubstitutions.put(eTypeParameters.get(i), eTypeArguments.get(i));
                    }
                    localSubstitutions = additionalLocalSubstitutions;
                  }
                }
                if (isBounded(eGenericSuperType, eBound, localSubstitutions))
                {
                  return true;
                }
              }
            }
            
            // If none of the generic super types are bounded, then we've failed.
            //
            return false;
          }
          else
          {
            // Or we test their relationship via their instance classes, if they have them.
            //
            Class<?> eBoundClass = eBoundEClassifier.getInstanceClass();
            if (eBoundClass != null)
            {
              Class<?> eClassifierClass = eClassifier.getInstanceClass();
              if (eClassifierClass != null /*&& !eBoundClass.isAssignableFrom(eClassifierClass)*/)
              {
                return false;
              }
              // If the classifier being bounded doesn't have type parameters...
              //
              else if (eClassifier.getETypeParameters().isEmpty())
              {
                // Then there won't be any type arguments,
                // so even if the bound has type arguments, 
                // we must assume the classifier is bounded
                // because we don't know that the implementation class in Java isn't properly bounded.
                // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=331475
                //
                return true;
              }
            }
          }
        }

        // If neither approach finds a contradiction, we must assume they are okay and then check all the arguments.
        //
        return matchingTypeArguments(eGenericType.getETypeArguments(), eBound.getETypeArguments(), substitutions);
      }
      else
      {
        ETypeParameter eTypeParameter = eGenericType.getETypeParameter();
        if (eTypeParameter != null)
        {
          EGenericType substitution = substitutions.get(eTypeParameter);
          if (substitution == eGenericType)
          {
            return true;
          }
          else if (substitution != null && substitution.getEUpperBound() != eGenericType && substitution.getELowerBound() != eGenericType)
          {
            return isBounded(substitution, eBound, substitutions);
          }
          else
          {
            // If there is a type parameter, one of its bounds must be bounded by the bound.
            //
            boolean result = false;
            for (EGenericType eTypeParameterBound : eTypeParameter.getEBounds())
            {
              if (isBounded(eTypeParameterBound, eBound, substitutions))
              {
                result = true;
                break;
              }
            }
            return result;
          }
        }
        else
        {
          // If there is a upper bound, the bound must bound it.
          //
          EGenericType eUpperBound = eGenericType.getEUpperBound();
          if (eUpperBound != null)
          {
            return isBounded(eUpperBound, eBound, substitutions);
          }
          else
          {
            // Failing all those cases, there must be an lower bound that bounds it.
            //
            EGenericType eLowerBound = eGenericType.getELowerBound();
            return eLowerBound != null && isBounded(eLowerBound, eBound, substitutions);
          }
        }
      }
    }
    else
    {
      ETypeParameter eBoundETypeParameter = eBound.getETypeParameter();
      if (eBoundETypeParameter != null)
      {
        ETypeParameter eTypeParameter = eGenericType.getETypeParameter();
        if (eTypeParameter == eBoundETypeParameter)
        {
          return true;
        }
        else 
        {
          EGenericType substitution = substitutions.get(eBoundETypeParameter);
          if (substitution != null)
          {
            return isBounded(eGenericType, substitution, substitutions);
          }
          else if (eTypeParameter != null)
          {
            substitution = substitutions.get(eTypeParameter);
            if (substitution == eGenericType)
            {
              return true;
            }
            else if (substitution != null && substitution.getEUpperBound() != eGenericType && substitution.getELowerBound() != eGenericType)
            {
              return isBounded(substitution, eBound, substitutions);
            }
            else
            {
              boolean result = false;
              for (EGenericType eTypeParameterEBound : eTypeParameter.getEBounds())
              {
                if (!(result = isBounded(eTypeParameterEBound, eBound, substitutions)))
                {
                  for (EGenericType eBoundETypeParameterEBound : eBoundETypeParameter.getEBounds())
                  {
                    if (isBounded(eTypeParameterEBound, eBoundETypeParameterEBound, substitutions))
                    {
                      result = true;
                      break;
                    }
                  }
                }
                if (!result)
                {
                  return false;
                }
              }
              return result;
            }
          }
          else
          {
            if (eGenericType.getEUpperBound() != null)
            {
              return isBounded(eGenericType.getEUpperBound(), eBound, substitutions);
            }
            else
            {
              return false;
            }
          }
        }
      }
      else
      {
        // If the generic type is a wildcard, it can't be bounded by another wildcard.
        //
        if (eGenericType.getETypeParameter() == null && eGenericType.getEClassifier() == null)
        {
          return false;
        }
        EGenericType eBoundEUpperBound = eBound.getEUpperBound();
        if (eBoundEUpperBound != null)
        {
          return isBounded(eGenericType, eBoundEUpperBound, substitutions);
        }
        else
        {
          EGenericType eBoundELowerBound = eBound.getELowerBound();
          if (eBoundELowerBound != null)
          {
            // If there is an lower bound, the type argument must bound it.
            //
            return isBounded(eBoundELowerBound, eGenericType, substitutions);
          }
          
          // The bound is a wildcard with no constraints.
          //
          return false;
        }
      }
    }
  }

  public static boolean matchingTypeArguments
    (EList<EGenericType> eTypeArguments1, EList<EGenericType> eTypeArguments2, Map<? extends ETypeParameter, ? extends EGenericType> substitutions)
  {
    int size = eTypeArguments1.size();
    if (size != eTypeArguments2.size())
    {
      return false;
    }
    else
    {
      for (int i = 0; i < size; ++i)
      {
        EGenericType eTypeArgument1 = eTypeArguments1.get(i);
        EGenericType eTypeArgument2 = eTypeArguments2.get(i);
        if (!isMatching(eTypeArgument1, eTypeArgument2, substitutions))
        {
          return false;
        }
      }
      return true;
    }
  }

  public static boolean isMatching(EGenericType eGenericType, EGenericType eBound, Map<? extends ETypeParameter, ? extends EGenericType> substitutions)
  {
    if (eGenericType == eBound)
    {
      return true;
    }

    // Check if the bound specifies a classifier...
    //
    EClassifier eBoundEClassifier = eBound.getEClassifier();
    if (eBoundEClassifier != null)
    {
      // If the type also specifies a classifier...
      //
      EClassifier eClassifier = eGenericType.getEClassifier();
      if (eClassifier != null)
      {
        // If they are the same classifier, they are of course equal.
        //
        if (eClassifier != eBoundEClassifier)
        {
          // Consider the instance type names they wrap 
          // to see if they are non-null and equal.
          //
          String instanceTypeName1 = eClassifier.getInstanceTypeName(); 
          String instanceTypeName2 = eBoundEClassifier.getInstanceTypeName(); 
  
          // I.e., the classifiers are considered equal if they wrap the same non-null type.
          //
          if (instanceTypeName1 == null || !instanceTypeName1.equals(instanceTypeName2))
          {
            return false;
          }
        }
          
        // TODO What about the instance type name and the fact that we should be matching its type argument structure?
        // If they match so far, we must assume they are okay and then check all the arguments.
        //
        return equalTypeArguments(eGenericType.getETypeArguments(), eBound.getETypeArguments(), substitutions);
      }
      else
      {
        return false;
      }
    }
    else
    {
      ETypeParameter eBoundETypeParameter = eBound.getETypeParameter();
      if (eBoundETypeParameter != null)
      {
        ETypeParameter eTypeParameter = eGenericType.getETypeParameter();
        if (eTypeParameter == eBoundETypeParameter)
        {
          return true;
        }
        else
        {
          EGenericType substitution = substitutions.get(eTypeParameter);
          if (substitution != null)
          {
            return substitution == eGenericType || isMatching(substitution, eBound, substitutions);
          }
          else if ((substitution = substitutions.get(eBoundETypeParameter)) != null)
          {
            return substitution == eBound || isMatching(eGenericType, substitution, substitutions);
          }
          else
          {
            return false;
          }
        }
      }
      else
      {
        // If the generic type is a different wildcard, it doesn't match.
        //
        if (eGenericType.getEClassifier() == null && eGenericType.getETypeParameter() == null)
        {
          return
            isMatching(eGenericType.getELowerBound(), eBound.getELowerBound(), substitutions) &&
              isMatching(eGenericType.getEUpperBound(), eBound.getEUpperBound(), substitutions);
        }
        else
        {
          EGenericType eBoundEUpperBound = eBound.getEUpperBound();
          if (eBoundEUpperBound != null)
          {
            return isBounded(eGenericType, eBoundEUpperBound, substitutions);
          }
          else
          {
            EGenericType eBoundELowerBound = eBound.getELowerBound();
            if (eBoundELowerBound != null)
            {
              // Reverse the test.
              //
              return isMatching(eBoundELowerBound, eGenericType, substitutions);
            }
            
            // The bound is a wildcard with no constraints.
            //
            return  true;
          }
        }
      }
    }
  }

  public static boolean equalTypeArguments
    (EList<EGenericType> eTypeArguments1, EList<EGenericType> eTypeArguments2, Map<? extends ETypeParameter, ? extends EGenericType> substitutions)
  {
    int size = eTypeArguments1.size();
    if (size != eTypeArguments2.size())
    {
      return false;
    }
    else
    {
      for (int i = 0; i < size; ++i)
      {
        EGenericType eTypeArgument1 = eTypeArguments1.get(i);
        EGenericType eTypeArgument2 = eTypeArguments2.get(i);
        if (!equalTypeArguments(eTypeArgument1, eTypeArgument2, substitutions))
        {
          return false;
        }
      }
      return true;
    }
  }

  public static boolean equalTypeArguments
    (EGenericType eGenericType1, EGenericType eGenericType2, Map<? extends ETypeParameter, ? extends EGenericType> substitutions)
  {
    // If they are the same instance they are equal.
    //
    if (eGenericType1 == eGenericType2)
    {
      return true;
    }
    // If one is null (but the other is not) then they are not equal.
    //
    else if (eGenericType1 == null || eGenericType2 == null)
    {
      return false;
    }
    else
    {
      // Consider the classifiers in a special way 
      // to take into account the fact they they often acts as wrappers for instance type names
      // and that two classifiers that wrap the same instance type name should be considered equal.
      //
      EClassifier eClassifier1 = eGenericType1.getEClassifier();
      EClassifier eClassifier2 = eGenericType2.getEClassifier();
      
      // If they are the same classifier, they are of course equal.
      //
      if (eClassifier1 != eClassifier2)
      {
        // If they both aren't null...
        //
        if (eClassifier1 != null && eClassifier2 != null)
        {
          // Consider the instance type names they wrap 
          // to see if they are non-null and equal.
          //
          String instanceTypeName1 = eClassifier1.getInstanceTypeName(); 
          String instanceTypeName2 = eClassifier2.getInstanceTypeName(); 

          // I.e., the classifiers are considered equal if they wrap the same non-null type.
          //
          if (instanceTypeName1 == null || !instanceTypeName1.equals(instanceTypeName2))
          {
            return false;
          }
        }
        // If one is null (but the other is not) then they can't be equal.
        //
        else if (eClassifier1 != null || eClassifier2 != null)
        {
          return false;
        }
      }
      
      ETypeParameter eTypeParameter1 = eGenericType1.getETypeParameter();
      EGenericType substitution = substitutions.get(eTypeParameter1);
      if (substitution != null)
      {
        return equalTypeArguments(substitution, eGenericType2, substitutions);
      }
      ETypeParameter eTypeParameter2 = eGenericType2.getETypeParameter();
      substitution = substitutions.get(eTypeParameter2);
      if (substitution != null)
      {
        return equalTypeArguments(eGenericType1, substitution, substitutions);
      }

      // The arguments, type parameters, lower bounds and upper bounds must be equal type arguments.
      //
      return
        eTypeParameter1 == eTypeParameter2 &&
          equalTypeArguments(eGenericType1.getETypeArguments(), eGenericType2.getETypeArguments(), substitutions) &&
          equalTypeArguments(eGenericType1.getELowerBound(), eGenericType2.getELowerBound(), substitutions) &&
          equalTypeArguments(eGenericType1.getEUpperBound(), eGenericType2.getEUpperBound(), substitutions);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateETypeParameter(ETypeParameter eTypeParameter, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(eTypeParameter, diagnostics, context)) return false;
    boolean result = validate_EveryMultiplicityConforms(eTypeParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryDataValueConforms(eTypeParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(eTypeParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(eTypeParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryProxyResolves(eTypeParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validate_UniqueID(eTypeParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryKeyUnique(eTypeParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(eTypeParameter, diagnostics, context);
    if (result || diagnostics != null) result &= validateENamedElement_WellFormedName(eTypeParameter, diagnostics, context);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEBoolean(boolean eBoolean, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEBooleanObject(Boolean eBooleanObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEByte(byte eByte, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEByteArray(byte[] eByteArray, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEByteObject(Byte eByteObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEChar(char eChar, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateECharacterObject(Character eCharacterObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEDate(Date eDate, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEDiagnosticChain(DiagnosticChain eDiagnosticChain, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEDouble(double eDouble, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEDoubleObject(Double eDoubleObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEEList(EList<?> eeList, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEEnumerator(Enumerator eEnumerator, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEFeatureMap(FeatureMap eFeatureMap, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEFeatureMapEntry(FeatureMap.Entry eFeatureMapEntry, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEFloat(float eFloat, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEFloatObject(Float eFloatObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEInt(int eInt, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEIntegerObject(Integer eIntegerObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEJavaClass(Class<?> eJavaClass, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEJavaObject(Object eJavaObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateELong(long eLong, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateELongObject(Long eLongObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEMap(Map<?, ?> eMap, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEResource(Resource eResource, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEResourceSet(ResourceSet eResourceSet, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEShort(short eShort, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEShortObject(Short eShortObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEString(String eString, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateETreeIterator(TreeIterator<?> eTreeIterator, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEInvocationTargetException(InvocationTargetException eInvocationTargetException, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * Returns the resource locator that will be used to fetch messages for this validator's diagnostics.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ResourceLocator getResourceLocator()
  {
    // TODO
    // Specialize this to return a resource locator for messages specific to this validator.
    // Ensure that you remove @generated or mark it @generated NOT
    return super.getResourceLocator();
  }

  /**
   * A utility for parsing generic types and generic type parameters.
   * @since 2.3
   */
  public static class EGenericTypeBuilder
  {
    /**
     * A singleton instance of the generic type build.
     */
    public static final EGenericTypeBuilder INSTANCE = new EGenericTypeBuilder();

    private static final char [] NO_CHARS = {};

    /**
     * Parses an instance type name and returns a diagnostic representing the result of the analysis.
     * The {@link Diagnostic#getData() data} of the diagnostic will contain as the first object, the resulting {@link EGenericType generic type}.
     * @param instanceTypeName an instance type name.
     * @return the diagnostic result of the analysis.
     */
    public Diagnostic parseInstanceTypeName(final String instanceTypeName)
    {
      BasicDiagnostic placeholder = new BasicDiagnostic();
      char [] instanceTypeNameCharacterArray = instanceTypeName == null ? NO_CHARS: instanceTypeName.toCharArray();
      EGenericType eGenericType = handleInstanceTypeName(instanceTypeNameCharacterArray, 0, instanceTypeNameCharacterArray.length, placeholder);
      BasicDiagnostic result =
        createDiagnostic
          (placeholder.getSeverity(),
           DIAGNOSTIC_SOURCE, 
           WELL_FORMED_INSTANCE_TYPE_NAME,
           "_UI_EClassifierInstanceTypeNameAnalysisResult_diagnostic", 
           new Object [] { instanceTypeName },
           new Object [] { eGenericType, instanceTypeName });
      result.addAll(placeholder);
      return result;
    }

    /**
     * Parses a list of type parameters and returns a diagnostic representing the result of the analysis.
     * The {@link Diagnostic#getData() data} of the diagnostic will contain as the first object, the resulting list of {@link ETypeParameter type parameters}.
     * @param typeParameterList a comma separated list of type parameters delimited by '&lt;' and '>'.
     * @return the diagnostic result of the analysis.
     */
    public Diagnostic parseTypeParameterList(final String typeParameterList)
    {
      BasicDiagnostic placeholder = new BasicDiagnostic();
      char [] instanceTypeNameCharacterArray = typeParameterList == null ? NO_CHARS : typeParameterList.toCharArray();
      List<ETypeParameter> eTypeParameters = handleTypeParameters(instanceTypeNameCharacterArray, 0, instanceTypeNameCharacterArray.length, placeholder);
      BasicDiagnostic result =
        createDiagnostic
          (placeholder.getSeverity(),
           DIAGNOSTIC_SOURCE,
           WELL_FORMED_INSTANCE_TYPE_NAME,
           "_UI_EClassifierInstanceTypeNameAnalysisResult_diagnostic",
           new Object [] { typeParameterList },
           new Object [] { eTypeParameters, typeParameterList });
      result.addAll(placeholder);
      return result;
    }

    /**
     * Parses a list of type arguments and returns a diagnostic representing the result of the analysis.
     * The {@link Diagnostic#getData() data} of the diagnostic will contain as the first object, the resulting list of {@link EGenericType type arguments}.
     * @param typeArgumentList a comma separated list of type arguments.
     * @return the diagnostic result of the analysis.
     * @since 2.4
     */
    public Diagnostic parseTypeArgumentList(final String typeArgumentList)
    {
      BasicDiagnostic placeholder = new BasicDiagnostic();
      char [] instanceTypeNameCharacterArray = typeArgumentList == null ? NO_CHARS : typeArgumentList.toCharArray();
      List<EGenericType> eTypeArguments = handleTypeArguments(instanceTypeNameCharacterArray, 0, instanceTypeNameCharacterArray.length, placeholder);
      BasicDiagnostic result =
        createDiagnostic
          (placeholder.getSeverity(),
           DIAGNOSTIC_SOURCE,
           WELL_FORMED_INSTANCE_TYPE_NAME,
           "_UI_EClassifierInstanceTypeNameAnalysisResult_diagnostic",
           new Object [] { typeArgumentList },
           new Object [] { eTypeArguments, typeArgumentList });
      result.addAll(placeholder);
      return result;
    }

    /**
     * Parses a type parameter and returns a diagnostic representing the result of the analysis.
     * The {@link Diagnostic#getData() data} of the diagnostic will contain as the first object, the resulting {@link ETypeParameter type parameter}.
     * @param typeParameter comma separated list of type parameters delimited by '&lt;' and '>'.
     * @return the diagnostic result of the analysis.
     */
    public Diagnostic parseTypeParameter(final String typeParameter)
    {
      BasicDiagnostic placeholder = new BasicDiagnostic();
      char [] instanceTypeNameCharacterArray = typeParameter == null ? NO_CHARS : typeParameter.toCharArray();
      ETypeParameter eTypeParameter = handleTypeParameter(instanceTypeNameCharacterArray, 0, instanceTypeNameCharacterArray.length, placeholder);
      BasicDiagnostic result =
        createDiagnostic
          (placeholder.getSeverity(),
           DIAGNOSTIC_SOURCE,
           WELL_FORMED_INSTANCE_TYPE_NAME,
           "_UI_EClassifierInstanceTypeNameAnalysisResult_diagnostic",
           new Object [] { typeParameter },
           new Object [] { eTypeParameter, typeParameter });
      result.addAll(placeholder);
      return result;
    }

    /**
     * Finds or creates an {@link EClassifier classifier} with the given instance type name.
     * @param instanceTypeName the instance type name for which a classifier is needed.
     * @return a classifier with the instance type name.
     */
    protected EClassifier resolveEClassifier(String instanceTypeName)
    {
      EDataType eDataType = EcoreFactory.eINSTANCE.createEDataType();
      eDataType.setInstanceTypeName(instanceTypeName);
      return eDataType;
    }

    /**
     * Creates a new diagnostic for a problem at the given index.
     * @param diagnostics the target for the new diagnostic.
     * @param key the key for the message.
     * @param substitutions the substitutions for the key; <code>null</code> if there are no substitutions.
     * @param index the index at which the problem occurred.
     */
    protected void report(DiagnosticChain diagnostics, String key, Object [] substitutions, int index)
    {
      report(diagnostics, getString(key, substitutions), index);
    }

    /**
     * Creates a new diagnostic for a problem at the given index.
     * @param diagnostics the target for the new diagnostic.
     * @param message the text describing the problem.
     * @param index the index at which the problem occurred.
     */
    protected void report(DiagnosticChain diagnostics, String message, int index)
    {
      if (diagnostics != null)
      {
        diagnostics.add
          (new BasicDiagnostic
             (Diagnostic.ERROR,
              DIAGNOSTIC_SOURCE,
              WELL_FORMED_INSTANCE_TYPE_NAME,
              message, 
              new Object [] { index }));
      }
    }

    /**
     * A well formed instance type name must syntactically denote a valid Java type name;
     * names denoting keywords are considered well formed.
     * It must start with a qualified name consisting of one or more "." separated identifiers,
     * where each identifier must start with a {@link Character#isJavaIdentifierStart(int) Java identifier start character},
     * that is followed by zero or more {@link Character#isJavaIdentifierPart(int) Java identifier part characters}.
     * The methods {@link #isIdentifierStart(int)} and {@link #isIdentifierPart(int)} are used so that this behavior can be specialized.
     * This qualified name may optionally be followed by zero or more pairs of "[]" characters
     * or by type arguments consisting of the pair of "&lt;>" characters
     * with embedded {@link #handleTypeArguments(char[], int, int, DiagnosticChain) well formed type arguments}.
     * @param instanceTypeName the instance type name in question.
     * @param start the start of the characters under consideration.
     * @param end the end of the characters under consideration.
     * @param diagnostics the target in which to accumulate diagnostics.
     * @return the generic type representing the instance type name.
     */
    protected EGenericType handleInstanceTypeName(char [] instanceTypeName, int start, int end, DiagnosticChain diagnostics)
    {
      EGenericType eGenericType = EcoreFactory.eINSTANCE.createEGenericType();
      StringBuilder qualifiedName = new StringBuilder();
      int identifierStart = -1;
      int identifierLast = -1;
      int brackets = 0;
      List<EGenericType> typeArguments = null;
      LOOP:
      for (int i = start; i < end; i = Character.offsetByCodePoints(instanceTypeName, 0, instanceTypeName.length, i, 1))
      {
        int codePoint = Character.codePointAt(instanceTypeName, i);
        if (codePoint == '[')
        {
          if (identifierStart == -1 && (qualifiedName.length() == 0 || qualifiedName.charAt(qualifiedName.length() - 1) == '.'))
          {
            report
              (diagnostics, 
               "_UI_EClassifierInstanceTypeNameBracketWithoutPrecedingIdentifier_diagnostic", 
               new Object [] { i }, 
               i);
            return eGenericType;
          }
          else 
          {
            for (int j = i + 1; j < end; j = Character.offsetByCodePoints(instanceTypeName, 0, instanceTypeName.length, j, 1))
            {
              codePoint = Character.codePointAt(instanceTypeName, j);
              if (codePoint == ']')
              {
                i = j;
                ++brackets;
                continue LOOP;
              }
              else if (!isWhitespace(codePoint))
              {
                report
                  (diagnostics, 
                   "_UI_EClassifierInstanceTypeNameNoClosingBracket2_diagnostic", 
                   new Object [] { j, new String(Character.toChars(codePoint))}, 
                   j);
                return eGenericType;
              }
            }
            report
              (diagnostics, 
               "_UI_EClassifierInstanceTypeNameNoClosingBracket_diagnostic", 
               new Object [] { end }, 
               end);
            return eGenericType;
          }
        }
        else if (brackets > 0)
        {
          if (!isWhitespace(codePoint))
          {
            report
              (diagnostics, 
               "_UI_EClassifierInstanceTypeNameBracketExpected_diagnostic", 
               new Object [] { i, new String(Character.toChars(codePoint))}, 
               i);
            return eGenericType;
          }
        }
        else if (codePoint == '.')
        {
          if (identifierStart == -1)
          {
            if (qualifiedName.length() == 0 || qualifiedName.charAt(qualifiedName.length() - 1) == '.')
            {
              report
                (diagnostics, 
                 "_UI_EClassifierInstanceTypeNameDotWithoutPrecedingIdentifier_diagnostic", 
                 new Object [] { i }, 
                 i);
              return eGenericType;
            }
            else
            {
              qualifiedName.append('.');
            }
          }
          else
          {
            qualifiedName.append(instanceTypeName, identifierStart, identifierLast - identifierStart + 1);
            qualifiedName.append('.');
            identifierStart = -1;
            identifierLast = -1;
          }
        }
        else if (identifierStart != -1 ? isIdentifierPart(codePoint) : isIdentifierStart(codePoint))
        {
          if (identifierStart == -1)
          {
            if (qualifiedName.length() > 0 && qualifiedName.charAt(qualifiedName.length() - 1) != '.')
            {
              report
                (diagnostics, 
                 "_UI_EClassifierInstanceTypeNameDotExpectedBeforeIdentifier_diagnostic", 
                 new Object [] { i }, 
                 i);
              return eGenericType;
            }
            identifierStart = i;
          }
          identifierLast = i;
        }
        else if (isWhitespace(codePoint))
        {
          if (identifierStart == -1)
          {
            // Ignore leading whitespace
          }
          else if (qualifiedName.length() == 0 || qualifiedName.charAt(qualifiedName.length() - 1) == '.')
          {
            qualifiedName.append(instanceTypeName, identifierStart, identifierLast - identifierStart + 1);
            identifierStart = -1;
            identifierLast = -1;
          }
          else
          {
            // Ignore trailing whitespace
          }
        }
        else if (codePoint == '<')
        {
          if (identifierStart == -1 && (qualifiedName.length() == 0 || qualifiedName.charAt(qualifiedName.length() - 1) == '.'))
          {
            report
              (diagnostics, 
               "_UI_EClassifierInstanceTypeNameAngleBracketWithoutPrecedingIdentifier_diagnostic", 
               new Object [] { i }, 
               i);
            return eGenericType;
          }
          for (int j = end - 1; j > i; --j)
          {
            if (instanceTypeName[j] == '>')
            {
              typeArguments = handleTypeArguments(instanceTypeName, i + 1, j, diagnostics);
              i = j;
              continue LOOP;
            }
          }
          report
            (diagnostics, 
             "_UI_EClassifierInstanceTypeNameUnterminatedAngleBracket_diagnostic", 
             new Object [] { i }, 
             i);
          return eGenericType;
        }
        else
        {
          report
            (diagnostics, 
             "_UI_EClassifierInstanceTypeNameUnexpectedCharacter_diagnostic", 
             new Object [] { i, new String(Character.toChars(codePoint)) }, 
             i);
          return eGenericType;
        }
      }
      
      if (identifierStart == -1 && (qualifiedName.length() == 0 || qualifiedName.charAt(qualifiedName.length() - 1) == '.'))
      {
        report
          (diagnostics, 
           "_UI_EClassifierInstanceTypeNameExpectingIdentifier_diagnostic", 
           new Object [] { end }, 
           end);
      }
      else
      {
        if (identifierStart != -1)
        {
          qualifiedName.append(instanceTypeName, identifierStart, identifierLast - identifierStart + 1);
        }
        while (brackets-- > 0)
        {
          qualifiedName.append("[]");
        }
        String qualifiedNameString = qualifiedName.toString();
        eGenericType.setEClassifier(resolveEClassifier(qualifiedNameString));
        if (typeArguments != null)
        {
          eGenericType.getETypeArguments().addAll(typeArguments);
        }
      }
      return eGenericType;
    }
    
    /**
     * Returns whether this code point is a valid start of an identifier.
     * @param codePoint the code point in question.
     * @return whether this code point is a valid start of an identifier.
     */
    protected boolean isIdentifierStart(int codePoint)
    {
      return isJavaIdentifierStart(codePoint);
    }

    /**
     * Returns whether this code point is a valid part of an identifier, i.e., whether it's valid after the first character.
     * @param codePoint the code point in question.
     * @return whether this code point is a valid part of an identifier.
     */
    protected boolean isIdentifierPart(int codePoint)
    {
      return isJavaIdentifierPart(codePoint);
    }
    
    /**
     * Well formed type arguments must syntactically denote a comma separated sequence of
     * {@link #handleTypeArgument(char[], int, int, DiagnosticChain) well formed type arguments}.
     * Whitespace before or after arguments is ignored.
     * @param instanceTypeName the instance type name in question.
     * @param start the start of the characters under consideration.
     * @param end the end of the characters under consideration.
     * @param diagnostics the target in which to accumulate diagnostics.
     * @return a list of generic type representing the type arguments.
     */
    protected List<EGenericType> handleTypeArguments(char [] instanceTypeName, int start, int end, DiagnosticChain diagnostics)
    {
      List<EGenericType> result = new ArrayList<EGenericType>();
      int depth = 0;
      int typeArgumentStart = start;
      for (int i = start; i < end; i = Character.offsetByCodePoints(instanceTypeName, 0, instanceTypeName.length, i, 1))
      {
        int codePoint = Character.codePointAt(instanceTypeName, i);
        switch (codePoint)
        {
          case '<':
          {
            ++depth;
            break;
          }
          case '>':
          {
            --depth;
            break;
          }
          case ',':
          {
            if (depth == 0)
            {
              result.add(handleTypeArgument(instanceTypeName, typeArgumentStart, i, diagnostics));
              typeArgumentStart = i + 1;
            }
            break;
          }
          default:
          {
            if (typeArgumentStart == -1)
            {
              typeArgumentStart = i;
            }
            break;
          }
        }
      }
      result.add(handleTypeArgument(instanceTypeName, typeArgumentStart, end, diagnostics));
      return result;
    }

    /**
     * A well formed type argument must denote a valid Java type argument.
     * It may start with a "?"
     * which may be optionally followed by the keyword "extends" or "super"
     * which in turn, when present, must be followed by a
     * {@link #handleInstanceTypeName(char[], int, int, DiagnosticChain) well formed type instance name}.
     * White space before the keyword is optional but at least one space character is expected after the keyword.
     * Otherwise, the whole string must be a well formed instance type name.
     * @param instanceTypeName the instance type name in question.
     * @param start the start of the characters under consideration.
     * @param end the end of the characters under consideration.
     * @param diagnostics the target in which to accumulate diagnostics.
     * @return the generic type representing the type argument.
     */
    protected EGenericType handleTypeArgument(char [] instanceTypeName, int start, int end, DiagnosticChain diagnostics)
    {
      EGenericType eGenericType = null;
      int firstNonWhiteSpaceIndex = start;
      LOOP:
      for (int i = start; i < end; i = Character.offsetByCodePoints(instanceTypeName, 0, instanceTypeName.length, i, 1))
      {
        int codePoint = Character.codePointAt(instanceTypeName, i);
        switch (codePoint)
        {
          case '?':
          {
            if (eGenericType == null)
            {
              eGenericType = EcoreFactory.eINSTANCE.createEGenericType();
              break;
            }
            else
            {
              report
                (diagnostics, 
                 "_UI_EClassifierInstanceTypeNameTooManyQuestionMarks_diagnostic", 
                 new Object [] { i }, 
                 i);
              break LOOP;
            }
          }
          case 'e':
          {
            if (eGenericType != null)
            {
              if (i + 7 < end &&
                    instanceTypeName[i + 1] == 'x' &&
                    instanceTypeName[i + 2] == 't' &&
                    instanceTypeName[i + 3] == 'e' &&
                    instanceTypeName[i + 4] == 'n' &&
                    instanceTypeName[i + 5] == 'd' &&
                    instanceTypeName[i + 6] == 's' &&
                    isWhitespace(Character.codePointAt(instanceTypeName, i + 7)))
              {
                EGenericType eUpperBound = 
                  handleInstanceTypeName
                    (instanceTypeName, Character.offsetByCodePoints(instanceTypeName, 0, instanceTypeName.length, i + 6, 1), end, diagnostics);
                eGenericType.setEUpperBound(eUpperBound);
              }
              else
              {
                report
                  (diagnostics, 
                   "_UI_EClassifierInstanceTypeNameExpectingExtends_diagnostic", 
                   new Object [] { i }, 
                   i);
              }
            }
            else
            {
              eGenericType = handleInstanceTypeName(instanceTypeName, start, end, diagnostics);
            }
            break LOOP;
          }
          case 's':
          {
            if (eGenericType != null)
            {
              if (i + 5 < end &&
                    instanceTypeName[i + 1] == 'u' &&
                    instanceTypeName[i + 2] == 'p' &&
                    instanceTypeName[i + 3] == 'e' &&
                    instanceTypeName[i + 4] == 'r' &&
                    isWhitespace(Character.codePointAt(instanceTypeName, i + 5)))
              {
                EGenericType eLowerBound = 
                  handleInstanceTypeName
                    (instanceTypeName, Character.offsetByCodePoints(instanceTypeName, 0, instanceTypeName.length, i + 4, 1), end, diagnostics);
                eGenericType.setELowerBound(eLowerBound);
              }
              else
              {
                report
                  (diagnostics, 
                   "_UI_EClassifierInstanceTypeNameExpectingSuper_diagnostic", 
                   new Object [] { i },
                   i);
              }
            }
            else
            {
              eGenericType = handleInstanceTypeName(instanceTypeName, start, end, diagnostics);
            }
            break LOOP;
          }
          default:
          {
            if (isWhitespace(codePoint))
            {
              break;
            }
            else if (eGenericType != null)
            {
              report
                (diagnostics, 
                 "_UI_EClassifierInstanceTypeNameExpectingExtendsOrSuper_diagnostic", 
                 new Object [] { i }, 
                 i);
              break LOOP;
            }
            else
            {
              firstNonWhiteSpaceIndex = i;
              eGenericType = handleInstanceTypeName(instanceTypeName, i, end, diagnostics);
              break LOOP;
            }
          } 
        }
      }
      if (eGenericType == null)
      {
        eGenericType = EcoreFactory.eINSTANCE.createEGenericType();
        report
          (diagnostics, 
           "_UI_EClassifierInstanceTypeNameTypeArgumentExpected_diagnostic", 
           new Object [] { firstNonWhiteSpaceIndex }, 
           firstNonWhiteSpaceIndex);
      }
      return eGenericType;
    }

    /**
     * Well formed type parameters must syntactically denote a comma separated sequence of
     * {@link #handleTypeParameter(char[], int, int, DiagnosticChain) well formed type parameters} delimited by "&lt;>".
     * Whitespace before or after parameters is ignored.
     * @param typeParameters the type parameters question.
     * @param start the start of the characters under consideration.
     * @param end the end of the characters under consideration.
     * @param diagnostics the target in which to accumulate diagnostics.
     * @return a list of type parameters.
     */
    protected List<ETypeParameter> handleTypeParameters(char [] typeParameters, int start, int end, DiagnosticChain diagnostics)
    {
      List<ETypeParameter> result = new ArrayList<ETypeParameter>();
      int depth = 0;
      int typeArgumentStart = -1;
      for (int i = start; i < end; i = Character.offsetByCodePoints(typeParameters, 0, typeParameters.length, i, 1))
      {
        int codePoint = Character.codePointAt(typeParameters, i);
        switch (codePoint)
        {
          case '<':
          {
            ++depth;
            break;
          }
          case '>':
          {
            if (--depth == 0)
            {
              result.add(handleTypeParameter(typeParameters, typeArgumentStart, i, diagnostics));
            }
            break;
          }
          case ',':
          {
            if (depth == 1)
            {
              result.add(handleTypeParameter(typeParameters, typeArgumentStart, i, diagnostics));
              typeArgumentStart = i + 1;
            }
            break;
          }
          default:
          {
            if (typeArgumentStart == -1)
            {
              typeArgumentStart = i;
            }
            break;
          }
        }
      }
      if (depth != 0)
      {
        report
          (diagnostics, 
           "_UI_EClassifierInstanceTypeNameUnterminatedAngleBracket_diagnostic", 
           new Object [] { start }, 
           start);
      }
      return result;
    }

    /**
     * A well formed type parameter must denote a valid Java type parameter.
     * It must start with a well formed java identifier
     * which may be optionally followed by the keyword "extends"
     * which in turn, when present, must be followed by 
     * one or more '&amp;' separated {@link #handleTypeArgument(char[], int, int, DiagnosticChain) well formed type arguments} representing the bounds.
     * White space before the keyword is optional but at least one space character is expected after the keyword.
     * @param typeParameters the instance type name in question.
     * @param start the start of the characters under consideration.
     * @param end the end of the characters under consideration.
     * @param diagnostics the target in which to accumulate diagnostics.
     * @return the type parameter.
     */
    protected ETypeParameter handleTypeParameter(char [] typeParameters, int start, int end, DiagnosticChain diagnostics)
    {
      ETypeParameter eTypeParameter = EcoreFactory.eINSTANCE.createETypeParameter();
      int identifierStart = -1;
      int identifierLast = -1;
      boolean identifierDone = false;
      LOOP:
      for (int i = start; i < end; i = Character.offsetByCodePoints(typeParameters, 0, typeParameters.length, i, 1))
      {
        int codePoint = Character.codePointAt(typeParameters, i);
        if (isWhitespace(codePoint))
        {
          if (identifierStart != -1)
          {
            identifierDone = true;
          }
        }
        else if (identifierDone)
        {
          if (codePoint == 'e' && 
               i + 7 < end &&
               typeParameters[i + 1] == 'x' &&
               typeParameters[i + 2] == 't' &&
               typeParameters[i + 3] == 'e' &&
               typeParameters[i + 4] == 'n' &&
               typeParameters[i + 5] == 'd' &&
               typeParameters[i + 6] == 's' &&
               isWhitespace(Character.codePointAt(typeParameters, i + 7)))
          {
            i += 7;
            int boundStart = i;
            while (i < end)
            {
              char character = typeParameters[i];
              if (character == '&')
              {
                EGenericType eBound = handleInstanceTypeName(typeParameters, boundStart, i, diagnostics);
                eTypeParameter.getEBounds().add(eBound);
                boundStart = i + 1;
              }
              ++i;
            }
            EGenericType eBound = handleInstanceTypeName(typeParameters, boundStart, i, diagnostics);
            eTypeParameter.getEBounds().add(eBound);
          }
          else
          {
            report
              (diagnostics, 
               "_UI_EClassifierInstanceTypeNameExpectingExtends_diagnostic", 
               new Object [] { i }, 
               i);
          }
          break LOOP;
        }
        else if (identifierStart != -1 ? isIdentifierPart(codePoint) : isIdentifierStart(codePoint))
        {
          if (identifierStart == -1)
          {
            identifierStart = i;
          }
          identifierLast = i;
        }
        else
        {
          report
            (diagnostics, 
             "_UI_EClassifierInstanceTypeNameUnexpectedCharacter_diagnostic", 
             new Object [] { i, new String(Character.toChars(codePoint)) }, 
             i);
          break LOOP;
        }
      }
      
      if (identifierLast == -1)
      {
        report
          (diagnostics, 
            "_UI_EClassifierInstanceTypeNameExpectingIdentifier_diagnostic", 
            new Object [] { end }, 
            end);
      }
      else
      {
        eTypeParameter.setName(new String(typeParameters, identifierStart, identifierLast - identifierStart + 1));
      }
      return eTypeParameter;
    }

    /**
     * Creates a new {@link BasicDiagnostic#BasicDiagnostic(int, String, int, String, Object[]) basic diagnostic}.
     * It calls {@link #getString(String, Object[])} for the message substitution.
     * @param severity an indicator of the severity of the problem.
     * @param source the unique identifier of the source.
     * @param code the source-specific identity code.
     * @param messageKey the key of the message.
     * @param messageSubstitutions the substitutions for the key; <code>null</code> if there are no substitutions.
     * @param data the data associated with the diagnostic
     * @return a new diagnostic.
     * @see BasicDiagnostic#BasicDiagnostic(int, String, int, String, Object[])
     * @since 2.4
     */
    protected BasicDiagnostic createDiagnostic
      (int severity, String source, int code, String messageKey, Object[] messageSubstitutions, Object[] data)
    {
      String message = getString(messageKey, messageSubstitutions);
      return new BasicDiagnostic(severity, source, code, message, data);
    }

    /**
     * Returns a translated message with the given substitutions.
     * The {@link #getResourceLocator() resource locator} is used.
     * @param key the key for the message.
     * @param substitutions the substitutions for the key; <code>null</code> if there are no substitutions.
     * @return the message.
     * @since 2.4
     */
    protected String getString(String key, Object [] substitutions)
    {
      ResourceLocator resourceLocator = getResourceLocator();
      return substitutions == null ? resourceLocator.getString(key) : resourceLocator.getString(key, substitutions);
    }

    /**
     * Returns the resource locator for {@link #getString(String, Object[]) fetching} messages.
     * @return the resource locator for fetching messages.
     * @since 2.4
     */
    protected ResourceLocator getResourceLocator()
    {
      // TODO
      return null;
      // return EcorePlugin.INSTANCE;
    }
  }

  /**
   * Creates a new diagnostic for a problem at the given index.
   * @param diagnostics the target for the new diagnostic.
   * @param key the key for the message.
   * @param substitutions the substitutions for the key; <code>null</code> if there are no substitutions.
   * @param index the index at which the problem occurred.
   * @see EGenericTypeBuilder#report(DiagnosticChain, String, Object[], int)
   * @since 2.4
   */
  protected void report(DiagnosticChain diagnostics, String key, Object[] substitutions, int index, Map<Object, Object> context)
  {
    if (diagnostics != null)
    {
      diagnostics.add
        (new BasicDiagnostic
           (Diagnostic.ERROR,
            DIAGNOSTIC_SOURCE,
            WELL_FORMED_INSTANCE_TYPE_NAME,
            getString(key, substitutions),
            new Object [] { index }));
    }
  }

} //EcoreValidator
