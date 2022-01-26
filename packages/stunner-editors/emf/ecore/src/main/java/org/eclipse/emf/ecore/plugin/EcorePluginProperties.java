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
package org.eclipse.emf.ecore.plugin;

import org.gwtproject.i18n.client.Messages;

public interface EcorePluginProperties extends Messages
{
  @DefaultMessage("EMF Problem")
  String emfDiagnosticMarker();

  @Key("_UI_DiagnosticRoot_diagnostic")
  @DefaultMessage("Diagnosis of {0}")
  String diagnosticRootDiagnostic(Object substitution);

  @Key("_UI_RequiredFeatureMustBeSet_diagnostic")
  @DefaultMessage("The required feature ''{0}'' of ''{1}'' must be set")
  String requiredFeatureMustBeSetDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_FeatureHasTooFewValues_diagnostic")
  @DefaultMessage("The feature ''{0}'' of ''{1}'' with {2} values must have at least {3} values")
  String featureHasTooFewValuesDiagnostic(Object substitution0, Object substitution1, Object substitution2, Object substitution3);

  @Key("_UI_FeatureHasTooManyValues_diagnostic")
  @DefaultMessage("The feature ''{0}'' of ''{1}'' with {2} values may have at most {3} values")
  String featureHasTooManyValuesDiagnostic(Object substitution0, Object substitution1, Object substitution2, Object substitution3);

  @Key("_UI_DocumentRootMustHaveOneElement_diagnostic")
  @DefaultMessage("The feature ''{0}'' of ''{1}'' with {2} element values must have exactly 1 element value")
  String documentRootMustHaveOneElementDiagnostic(Object substitution0, Object substitution1, Object substitution2);

  @Key("_UI_UnresolvedProxy_diagnostic")
  @DefaultMessage("The feature ''{0}'' of ''{1}'' contains an unresolved proxy ''{2}''")
  String unresolvedProxyDiagnostic(Object substitution0, Object substitution1, Object substitution2);

  @Key("_UI_DanglingReference_diagnostic")
  @DefaultMessage("The feature ''{0}'' of ''{1}'' contains a dangling reference ''{2}''")
  String danglingReferenceDiagnostic(Object substitution0, Object substitution1, Object substitution2);

  @Key("_UI_UnpairedBidirectionalReference_diagnostic")
  @DefaultMessage("The opposite features ''{0}'' of ''{1}'' and ''{2}'' of ''{3}'' do not refer to each other")
  String unpairedBidirectionalReferenceDiagnostic(Object substitution0, Object substitution1, Object substitution2, Object substitution3);

  @Key("_UI_BadDataValue_diagnostic")
  @DefaultMessage("The feature ''{0}'' of ''{1}'' contains a bad value")
  String badDataValueDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_MinInclusiveConstraint_diagnostic")
  @DefaultMessage("The value ''{0}'' must be greater than or equal to ''{1}'' ")
  String minInclusiveConstraintDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_MinExclusiveConstraint_diagnostic")
  @DefaultMessage("The value ''{0}'' must be greater than ''{1}'' ")
  String minExclusiveConstraintDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_MaxInclusiveConstraint_diagnostic")
  @DefaultMessage("The value ''{0}'' must be less than or equal to ''{1}'' ")
  String maxInclusiveConstraintDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_MaxExclusiveConstraint_diagnostic")
  @DefaultMessage("The value ''{0}'' must be less than ''{1}'' ")
  String maxExclusiveConstraintDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_MinLengthConstraint_diagnostic")
  @DefaultMessage("The value ''{0}'' with length {1} must have at least length {2} ")
  String minLengthConstraintDiagnostic(Object substitution0, Object substitution1, Object substitution2);

  @Key("_UI_MaxLengthConstraint_diagnostic")
  @DefaultMessage("The value ''{0}'' with length {1} may have at most length {2} ")
  String maxLengthConstraintDiagnostic(Object substitution0, Object substitution1, Object substitution2);

  @Key("_UI_EnumerationConstraint_diagnostic")
  @DefaultMessage("The value ''{0}'' must be one of '{'{1}'}'")
  String enumerationConstraintDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_PatternConstraint_diagnostic")
  @DefaultMessage("The value ''{0}'' must be match one of '{'{1}'}'")
  String patternConstraintDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_TotalDigitsConstraint_diagnostic")
  @DefaultMessage("The value ''{0}'' may have at most {1} digits")
  String totalDigitsConstraintDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_FractionDigitsConstraint_diagnostic")
  @DefaultMessage("The value ''{0}'' may have at most {1} fraction digits")
  String fractionDigitsConstraintDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_ListHead_composition")
  @DefaultMessage("''{0}''")
  String listHeadComposition(Object substitution);

  @Key("_UI_ListTail_composition")
  @DefaultMessage("{0}, ''{1}''")
  String listTailComposition(Object substitution0, Object substitution1);

  @Key("_UI_BadDataValueType_diagnostic")
  @DefaultMessage("The value ''{0}'' of type ''{1}'' must be of type ''{2}''")
  String badDataValueTypeDiagnostic(Object substitution0, Object substitution1, Object substitution2);

  @Key("_UI_DuplicateID_diagnostic")
  @DefaultMessage("The ID ''{0}'' of ''{1}'' collides with that of ''{2}''")
  String duplicateIDDiagnostic(Object substitution0, Object substitution1, Object substitution2);

  @Key("_UI_DuplicateKey_diagnostic")
  @DefaultMessage("The feature ''{0}'' has key {1} for ''{2}'' which collides with that of ''{3}''")
  String duplicateKeyDiagnostic(Object substitution0, Object substitution1, Object substitution2, Object substitution3);

  @Key("_UI_DuplicateMapEntry_diagnostic")
  @DefaultMessage("The feature ''{0}'' has a map entry at index {1} with a key that collides with that of the map entry at index {2}")
  String duplicateMapEntryDiagnostic(Object substitution0, Object substitution1, Object substitution2);

  @Key("_UI_CircularContainment_diagnostic")
  @DefaultMessage("An object may not circularly contain itself")
  String circularContainmentDiagnostic();

  @Key("_UI_BadXMLGregorianCalendar_diagnostic")
  @DefaultMessage("The value ''{0}'' is not a well formed instance of the {1} XML Schema data type")
  String badXMLGregorianCalendarDiagnostic(Object substitution0, Object substitution1);

  @Key("parser.parse.1")
  @DefaultMessage("Wrong character.")
  String parserParse1();

  @Key("parser.parse.2")
  @DefaultMessage("Invalid reference number.")
  String parserParse2();

  @Key("parser.next.1")
  @DefaultMessage("A character is required after \\.")
  String parserNext1();

  @Key("parser.next.2")
  @DefaultMessage("''?'' is not expected.  ''(?:'' or ''(?='' or ''(?!'' or ''(?<'' or ''(?#'' or ''(?>''?")
  String parserNext2();

  @Key("parser.next.3")
  @DefaultMessage("''(?<'' or ''(?<!'' is expected.")
  String parserNext3();

  @Key("parser.next.4")
  @DefaultMessage("A comment is not terminated.")
  String parserNext4();

  @Key("parser.factor.1")
  @DefaultMessage("'')'' is expected.")
  String parserFactor1();

  @Key("parser.factor.2")
  @DefaultMessage("Unexpected end of the pattern in a modifier group.")
  String parserFactor2();

  @Key("parser.factor.3")
  @DefaultMessage("'':'' is expected.")
  String parserFactor3();

  @Key("parser.factor.4")
  @DefaultMessage("Unexpected end of the pattern in a conditional group.")
  String parserFactor4();

  @Key("parser.factor.5")
  @DefaultMessage("A back reference or an anchor or a lookahead or a look-behind is expected in a conditional pattern.")
  String parserFactor5();

  @Key("parser.factor.6")
  @DefaultMessage("There are more than three choices in a conditional group.")
  String parserFactor6();

  @Key("parser.atom.1")
  @DefaultMessage("A character in U+0040-U+005f must follow \\c.")
  String parserAtom1();

  @Key("parser.atom.2")
  @DefaultMessage("A '''{''' is required before a character category.")
  String parserAtom2();

  @Key("parser.atom.3")
  @DefaultMessage("A property name is not closed by '''}'''.")
  String parserAtom3();

  @Key("parser.atom.4")
  @DefaultMessage("Unexpected meta character.")
  String parserAtom4();

  @Key("parser.atom.5")
  @DefaultMessage("Unknown property.")
  String parserAtom5();

  @Key("parser.cc.1")
  @DefaultMessage("A POSIX character class must be closed by '':]''.")
  String parserCc1();

  @Key("parser.cc.2")
  @DefaultMessage("Unexpected end of the pattern in a character class.")
  String parserCc2();

  @Key("parser.cc.3")
  @DefaultMessage("Unknown name for a POSIX character class.")
  String parserCc3();

  @Key("parser.cc.4")
  @DefaultMessage("''-'' is invalid here.")
  String parserCc4();

  @Key("parser.cc.5")
  @DefaultMessage("'']'' is expected.")
  String parserCc5();

  @Key("parser.cc.6")
  @DefaultMessage("''['' is invalid in a character class.  Write ''\\[''.")
  String parserCc6();

  @Key("parser.cc.7")
  @DefaultMessage("'']'' is invalid in a character class.  Write ''\\]''.")
  String parserCc7();

  @Key("parser.cc.8")
  @DefaultMessage("''-'' is an invalid character range. Write ''\\-''.")
  String parserCc8();

  @Key("parser.ope.1")
  @DefaultMessage("''['' is expected.")
  String parserOpe1();

  @Key("parser.ope.2")
  @DefaultMessage("'')'' or ''-['' or ''+['' or ''&['' is expected.")
  String parserOpe2();

  @Key("parser.ope.3")
  @DefaultMessage("The range end code point is less than the start code point.")
  String parserOpe3();

  @Key("parser.descape.1")
  @DefaultMessage("Invalid Unicode hex notation.")
  String parserDescape1();

  @Key("parser.descape.2")
  @DefaultMessage("Overflow in a hex notation.")
  String parserDescape2();

  @Key("parser.descape.3")
  @DefaultMessage("''\\x'{''' must be closed by '''}'''.")
  String parserDescape3();

  @Key("parser.descape.4")
  @DefaultMessage("Invalid Unicode code point.")
  String parserDescape4();

  @Key("parser.descape.5")
  @DefaultMessage("An anchor must not be here.")
  String parserDescape5();

  @Key("parser.process.1")
  @DefaultMessage("This expression is not supported in the current option setting.")
  String parserProcess1();

  @Key("parser.quantifier.1")
  @DefaultMessage("Invalid quantifier. A digit is expected.")
  String parserQuantifier1();

  @Key("parser.quantifier.2")
  @DefaultMessage("Invalid quantifier. Invalid quantity or a ''}'' is missing.")
  String parserQuantifier2();

  @Key("parser.quantifier.3")
  @DefaultMessage("Invalid quantifier. A digit or ''}'' is expected.")
  String parserQuantifier3();

  @Key("parser.quantifier.4")
  @DefaultMessage("Invalid quantifier. A min quantity must be <= a max quantity.")
  String parserQuantifier4();

  @Key("parser.quantifier.5")
  @DefaultMessage("Invalid quantifier. A quantity value overflow.")
  String parserQuantifier5();

  @Key("_UI_PackageRegistry_extensionpoint")
  @DefaultMessage("Ecore Package Registry for Generated Packages")
  String packageRegistryExtensionpoint();

  @Key("_UI_DynamicPackageRegistry_extensionpoint")
  @DefaultMessage("Ecore Package Registry for Dynamic Packages")
  String dynamicPackageRegistryExtensionpoint();

  @Key("_UI_FactoryRegistry_extensionpoint")
  @DefaultMessage("Ecore Factory Override Registry")
  String factoryRegistryExtensionpoint();

  @Key("_UI_URIExtensionParserRegistry_extensionpoint")
  @DefaultMessage("URI Extension Parser Registry")
  String uriExtensionParserRegistryExtensionpoint();

  @Key("_UI_URIProtocolParserRegistry_extensionpoint")
  @DefaultMessage("URI Protocol Parser Registry")
  String uriProtocolParserRegistryExtensionpoint();

  @Key("_UI_URIContentParserRegistry_extensionpoint")
  @DefaultMessage("URI Content Parser Registry")
  String uriContentParserRegistryExtensionpoint();

  @Key("_UI_ContentHandlerRegistry_extensionpoint")
  @DefaultMessage("Content Handler Registry")
  String contentHandlerRegistryExtensionpoint();

  @Key("_UI_URIMappingRegistry_extensionpoint")
  @DefaultMessage("URI Converter Mapping Registry")
  String uriMappingRegistryExtensionpoint();

  @Key("_UI_PackageRegistryImplementation_extensionpoint")
  @DefaultMessage("Ecore Package Registry Implementation")
  String packageRegistryImplementationExtensionpoint();

  @Key("_UI_ValidationDelegateRegistry_extensionpoint")
  @DefaultMessage("Validation Delegate Registry")
  String validationDelegateRegistryExtensionpoint();

  @Key("_UI_SettingDelegateRegistry_extensionpoint")
  @DefaultMessage("Feature Setting Delegate Factory Registry")
  String settingDelegateRegistryExtensionpoint();

  @Key("_UI_InvocationDelegateRegistry_extensionpoint")
  @DefaultMessage("Operation Invocation Delegate Factory Registry")
  String invocationDelegateRegistryExtensionpoint();

  @Key("_UI_GenericInvariant_diagnostic")
  @DefaultMessage("The ''{0}'' invariant is violated on ''{1}''")
  String genericInvariantDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_GenericConstraint_diagnostic")
  @DefaultMessage("The ''{0}'' constraint is violated on ''{1}''")
  String genericConstraintDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_EAnnotationSourceURINotWellFormed_diagnostic")
  @DefaultMessage("The source URI ''{0}'' is not well formed")
  String eAnnotationSourceURINotWellFormedDiagnostic(Object substitution);

  @Key("_UI_EAttributeConsistentTransient_diagnostic")
  @DefaultMessage("The attribute ''{0}'' is not transient so it must have a data type that is serializable")
  String eAttributeConsistentTransientDiagnostic(Object substitution);

  @Key("_UI_ENamedElementNameNotWellFormed_diagnostic")
  @DefaultMessage("The name ''{0}'' is not well formed")
  String eNamedElementNameNotWellFormedDiagnostic(Object substitution);

  @Key("_UI_EClassifierInstanceTypeNameNotWellFormed_diagnostic")
  @DefaultMessage("The instance type name ''{0}'' is not well formed")
  String eClassifierInstanceTypeNameNotWellFormedDiagnostic(Object substitution);

  @Key("_UI_EClassInterfaceNotAbstract_diagnostic")
  @DefaultMessage("A class that is an interface must also be abstract")
  String eClassInterfaceNotAbstractDiagnostic();

  @Key("_UI_EClassAtMostOneID_diagnostic")
  @DefaultMessage("The features ''{0}'' and ''{1}'' cannot both be IDs")
  String eClassAtMostOneIDDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_EClassUniqueEStructuralFeatureName_diagnostic")
  @DefaultMessage("There may not be two features named ''{0}''")
  String eClassUniqueEStructuralFeatureNameDiagnostic(Object substitution);

  @Key("_UI_EClassDissimilarEStructuralFeatureName_diagnostic")
  @DefaultMessage("There should not be a feature named ''{0}'' as well a feature named ''{1}''")
  String eClassDissimilarEStructuralFeatureNameDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_EClassUniqueEOperationSignatures_diagnostic")
  @DefaultMessage("There may not be two operations ''{0}'' and ''{1}'' with the same signature")
  String eClassUniqueEOperationSignaturesDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_EClassDisjointFeatureAndOperationSignatures_diagnostic")
  @DefaultMessage("There may not be an operation ''{0}'' with the same signature as an accessor method for feature ''{1}''")
  String eClassDisjointFeatureAndOperationSignaturesDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_EClassNoCircularSuperTypes_diagnostic")
  @DefaultMessage("A class may not be a super type of itself")
  String eClassNoCircularSuperTypesDiagnostic();

  @Key("_UI_EClassNotWellFormedMapEntry_diagnostic")
  @DefaultMessage("A map entry class must have a feature called ''{0}''")
  String eClassNotWellFormedMapEntryDiagnostic(Object substitution);

  @Key("_UI_EClassNotWellFormedMapEntryNoInstanceClassName_diagnostic")
  @DefaultMessage("A class that inherits from a map entry class must have instance class name ''java.util.Map$Entry''")
  String eClassNotWellFormedMapEntryNoInstanceClassNameDiagnostic();

  @Key("_UI_EEnumUniqueEnumeratorNames_diagnostic")
  @DefaultMessage("There may not be two enumerators named ''{0}''")
  String eEnumUniqueEnumeratorNamesDiagnostic(Object substitution);

  @Key("_UI_EEnumDissimilarEnumeratorNames_diagnostic")
  @DefaultMessage("There should not be an enumerator named ''{0}'' as well an enumerator named ''{1}''")
  String eEnumDissimilarEnumeratorNamesDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_EEnumUniqueEnumeratorLiterals_diagnostic")
  @DefaultMessage("There may not be two enumerators with literal value ''{0}''")
  String eEnumUniqueEnumeratorLiteralsDiagnostic(Object substitution);

  @Key("_UI_UniqueTypeParameterNames_diagnostic")
  @DefaultMessage("There may not be two type parameters named ''{0}''")
  String uniqueTypeParameterNamesDiagnostic(Object substitution);

  @Key("_UI_EOperationUniqueParameterNames_diagnostic")
  @DefaultMessage("There may not be two parameters named ''{0}''")
  String eOperationUniqueParameterNamesDiagnostic(Object substitution);

  @Key("_UI_EOperationNoRepeatingVoid_diagnostic")
  @DefaultMessage("An operation with void return type must have an upper bound of 1 not {0}")
  String eOperationNoRepeatingVoidDiagnostic(Object substitution);

  @Key("_UI_EPackageNsURINotWellFormed_diagnostic")
  @DefaultMessage("The namespace URI ''{0}'' is not well formed")
  String ePackageNsURINotWellFormedDiagnostic(Object substitution);

  @Key("_UI_EPackageNsPrefixNotWellFormed_diagnostic")
  @DefaultMessage("The namespace prefix ''{0}'' is not well formed")
  String ePackageNsPrefixNotWellFormedDiagnostic(Object substitution);

  @Key("_UI_EPackageUniqueSubpackageNames_diagnostic")
  @DefaultMessage("There may not be two packages named ''{0}''")
  String ePackageUniqueSubpackageNamesDiagnostic(Object substitution);

  @Key("_UI_EPackageUniqueClassifierNames_diagnostic")
  @DefaultMessage("There may not be two classifiers named ''{0}''")
  String ePackageUniqueClassifierNamesDiagnostic(Object substitution);

  @Key("_UI_EPackageUniqueNsURIs_diagnostic")
  @DefaultMessage("There may not be two packages with namespace URI ''{0}''")
  String ePackageUniqueNsURIsDiagnostic(Object substitution);

  @Key("_UI_EPackageDissimilarClassifierNames_diagnostic")
  @DefaultMessage("There may not be a classifier named ''{0}'' as well a classifier named ''{1}''")
  String ePackageDissimilarClassifierNamesDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_EReferenceOppositeOfOppositeInconsistent_diagnostic")
  @DefaultMessage("The opposite of the opposite may not be a reference different from this one")
  String eReferenceOppositeOfOppositeInconsistentDiagnostic();

  @Key("_UI_EReferenceOppositeNotFeatureOfType_diagnostic")
  @DefaultMessage("The opposite must be a feature of the reference''s type")
  String eReferenceOppositeNotFeatureOfTypeDiagnostic();

  @Key("_UI_EReferenceTransientOppositeNotTransient_diagnostic")
  @DefaultMessage("The opposite of a transient reference must be transient if it is proxy resolving")
  String eReferenceTransientOppositeNotTransientDiagnostic();

  @Key("_UI_EReferenceOppositeBothContainment_diagnostic")
  @DefaultMessage("The opposite of a containment reference must not be a containment reference")
  String eReferenceOppositeBothContainmentDiagnostic();

  @Key("_UI_EReferenceSingleContainer_diagnostic")
  @DefaultMessage("A container reference must have upper bound of 1 not {0}")
  String eReferenceSingleContainerDiagnostic(Object substitution);

  @Key("_UI_EReferenceConsistentKeys_diagnostic")
  @DefaultMessage("The key ''{0}'' must be feature of the reference''s type")
  String eReferenceConsistentKeysDiagnostic(Object substitution);

  @Key("_UI_EReferenceConsistentUnique_diagnostic")
  @DefaultMessage("A containment or bidirectional reference must be unique if its upper bound is different from 1")
  String eReferenceConsistentUniqueDiagnostic();

  @Key("_UI_ETypedElementValidLowerBound_diagnostic")
  @DefaultMessage("The lower bound {0} must be greater than or equal to 0")
  String eTypedElementValidLowerBoundDiagnostic(Object substitution);

  @Key("_UI_ETypedElementValidUpperBound_diagnostic")
  @DefaultMessage("The upper bound {0} must be -2, -1, or greater than 0")
  String eTypedElementValidUpperBoundDiagnostic(Object substitution);

  @Key("_UI_ETypedElementConsistentBounds_diagnostic")
  @DefaultMessage("The lower bound {0} must be less than or equal to the upper bound {1}")
  String eTypedElementConsistentBoundsDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_ETypedElementNoType_diagnostic")
  @DefaultMessage("The typed element must have a type")
  String eTypedElementNoTypeDiagnostic();

  @Key("_UI_EAttributeNoDataType_diagnostic")
  @DefaultMessage("The generic attribute type must not refer to a class")
  String eAttributeNoDataTypeDiagnostic();

  @Key("_UI_EReferenceNoClass_diagnostic")
  @DefaultMessage("The generic reference type must not refer to a data type")
  String eReferenceNoClassDiagnostic();

  @Key("_UI_EGenericTypeNoTypeParameterAndClassifier_diagnostic")
  @DefaultMessage("A generic type can''t refer to both a type parameter and a classifier")
  String eGenericTypeNoTypeParameterAndClassifierDiagnostic();

  @Key("_UI_EGenericTypeNoClass_diagnostic")
  @DefaultMessage("A generic super type must refer to a class")
  String eGenericTypeNoClassDiagnostic();

  @Key("_UI_EGenericTypeNoTypeParameterOrClassifier_diagnostic")
  @DefaultMessage("A generic type in this context must refer to a classifier or a type parameter")
  String eGenericTypeNoTypeParameterOrClassifierDiagnostic();

  @Key("_UI_EGenericTypeBoundsOnlyForTypeArgument_diagnostic")
  @DefaultMessage("A generic type may have bounds only when used as a type argument")
  String eGenericTypeBoundsOnlyForTypeArgumentDiagnostic();

  @Key("_UI_EGenericTypeNoUpperAndLowerBound_diagnostic")
  @DefaultMessage("A generic type must not have both a lower and an upper bound")
  String eGenericTypeNoUpperAndLowerBoundDiagnostic();

  @Key("_UI_EGenericTypeNoTypeParameterOrClassifierAndBound_diagnostic")
  @DefaultMessage("A generic type with bounds must not also refer to a type parameter or classifier")
  String eGenericTypeNoTypeParameterOrClassifierAndBoundDiagnostic();

  @Key("_UI_EGenericTypeNoArguments_diagnostic")
  @DefaultMessage("A generic type may have arguments only if it refers to a classifier")
  String eGenericTypeNoArgumentsDiagnostic();

  @Key("_UI_EGenericTypeArgumentsNeeded_diagnostic")
  @DefaultMessage("The generic type associated with the ''{0}'' classifier should have {1} type argument(s) to match the number of type parameter(s) of the classifier ")
  String eGenericTypeArgumentsNeededDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_EGenericTypeArgumentInvalidSubstitution_diagnostic")
  @DefaultMessage("The generic type ''{0}'' is not a valid substitution for type parameter ''{1}''")
  String eGenericTypeArgumentInvalidSubstitutionDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_EGenericTypeIncorrectArguments_diagnostic")
  @DefaultMessage("The generic type associated with the ''{0}'' classifier must not have {1} argument(s) when the classifier has {2} type parameter(s)")
  String eGenericTypeIncorrectArgumentsDiagnostic(Object substitution0, Object substitution1, Object substitution2);

  @Key("_UI_EGenericTypeOutOfScopeTypeParameter_diagnostic")
  @DefaultMessage("A generic type may only refer to a type parameter that is in scope")
  String eGenericTypeOutOfScopeTypeParameterDiagnostic();

  @Key("_UI_EGenericTypeInvalidPrimitiveType_diagnostic")
  @DefaultMessage("The primitive type ''{0}'' cannot be used in this context")
  String eGenericTypeInvalidPrimitiveTypeDiagnostic(Object substitution);

  @Key("_UI_EClassNoDuplicateSuperTypes_diagnostic")
  @DefaultMessage("The generic super types at index ''{0}'' and ''{1}'' must not be duplicates")
  String eClassNoDuplicateSuperTypesDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_EClassConsistentSuperTypes_diagnostic")
  @DefaultMessage("The generic super types instantiate ''{0}'' inconsistently")
  String eClassConsistentSuperTypesDiagnostic(Object substitution);

  @Key("_UI_EStructuralFeatureValidDefaultValueLiteral_diagnostic")
  @DefaultMessage("The default value literal ''{0}'' must be a valid literal of the attribute''s type")
  String eStructuralFeatureValidDefaultValueLiteralDiagnostic(Object substitution);

  @Key("_UI_EClassifierInstanceTypeNameAnalysisResult_diagnostic")
  @DefaultMessage("Analysis result for the instance type name ''{0}''")
  String eClassifierInstanceTypeNameAnalysisResultDiagnostic(Object substitution);

  @Key("_UI_EClassifierInstanceTypeNameBracketWithoutPrecedingIdentifier_diagnostic")
  @DefaultMessage("The ''['' at index {0} must be preceded by an identifier")
  String eClassifierInstanceTypeNameBracketWithoutPrecedingIdentifierDiagnostic(Object substitution);

  @Key("_UI_EClassifierInstanceTypeNameNoClosingBracket_diagnostic")
  @DefaultMessage("A '']'' is expected at index {0}")
  String eClassifierInstanceTypeNameNoClosingBracketDiagnostic(Object substitution);

  @Key("_UI_EClassifierInstanceTypeNameNoClosingBracket2_diagnostic")
  @DefaultMessage("A '']'' is expected at index {0} not ''{1}''")
  String eClassifierInstanceTypeNameNoClosingBracket2Diagnostic(Object substitution0, Object substitution1);

  @Key("_UI_EClassifierInstanceTypeNameBracketExpected_diagnostic")
  @DefaultMessage("A ''['' is expected at index {0} not ''{1}''")
  String eClassifierInstanceTypeNameBracketExpectedDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_EClassifierInstanceTypeNameDotWithoutPrecedingIdentifier_diagnostic")
  @DefaultMessage("The ''.'' at index {0} must be preceded by an identifier")
  String eClassifierInstanceTypeNameDotWithoutPrecedingIdentifierDiagnostic(Object substitution);

  @Key("_UI_EClassifierInstanceTypeNameDotExpectedBeforeIdentifier_diagnostic")
  @DefaultMessage("A ''.'' is expected before the start of another identifier at index {0} ")
  String eClassifierInstanceTypeNameDotExpectedBeforeIdentifierDiagnostic(Object substitution);

  @Key("_UI_EClassifierInstanceTypeNameAngleBracketWithoutPrecedingIdentifier_diagnostic")
  @DefaultMessage("The ''<'' at index {0} must be preceded by an identifier")
  String eClassifierInstanceTypeNameAngleBracketWithoutPrecedingIdentifierDiagnostic(Object substitution);

  @Key("_UI_EClassifierInstanceTypeNameUnterminatedAngleBracket_diagnostic")
  @DefaultMessage("The ''<'' at index {0} must be terminated before the end of the string")
  String eClassifierInstanceTypeNameUnterminatedAngleBracketDiagnostic(Object substitution);

  @Key("_UI_EClassifierInstanceTypeNameUnexpectedCharacter_diagnostic")
  @DefaultMessage("The ''{1}'' at index {0} is not expected")
  String eClassifierInstanceTypeNameUnexpectedCharacterDiagnostic(Object substitution0, Object substitution1);

  @Key("_UI_EClassifierInstanceTypeNameTooManyQuestionMarks_diagnostic")
  @DefaultMessage("Another ''?'' is not permitted at index {0}")
  String eClassifierInstanceTypeNameTooManyQuestionMarksDiagnostic(Object substitution);

  @Key("_UI_EClassifierInstanceTypeNameExpectingExtends_diagnostic")
  @DefaultMessage("Expecting ''extends'' at index {0}")
  String eClassifierInstanceTypeNameExpectingExtendsDiagnostic(Object substitution);

  @Key("_UI_EClassifierInstanceTypeNameExpectingSuper_diagnostic")
  @DefaultMessage("Expecting ''super'' at index {0}")
  String eClassifierInstanceTypeNameExpectingSuperDiagnostic(Object substitution);

  @Key("_UI_EClassifierInstanceTypeNameExpectingExtendsOrSuper_diagnostic")
  @DefaultMessage("Expecting ''extends'' or ''super'' at index {0}")
  String eClassifierInstanceTypeNameExpectingExtendsOrSuperDiagnostic(Object substitution);

  @Key("_UI_EClassifierInstanceTypeNameTypeArgumentExpected_diagnostic")
  @DefaultMessage("A type argument is expected at index {0}")
  String eClassifierInstanceTypeNameTypeArgumentExpectedDiagnostic(Object substitution);

  @Key("_UI_EClassifierInstanceTypeNameExpectingIdentifier_diagnostic")
  @DefaultMessage("Expecting an identifier at index {0}")
  String eClassifierInstanceTypeNameExpectingIdentifierDiagnostic(Object substitution);

  @Key("_UI_EClassifierInstanceTypeNameExpectedSpace_diagnostic")
  @DefaultMessage("Expecting '' '' at index {0}")
  String eClassifierInstanceTypeNameExpectedSpaceDiagnostic(Object substitution);

  @Key("_UI_EClassifierInstanceTypeNameUnexpectedSpace_diagnostic")
  @DefaultMessage("Unexpected '' '' at index {0}")
  String eClassifierInstanceTypeNameUnexpectedSpaceDiagnostic(Object substitution);

  @Key("_UI_InvariantDelegateException_diagnostic")
  @DefaultMessage("An exception occurred while delegating evaluation of the ''{0}'' invariant on ''{1}'': {2}")
  String invariantDelegateExceptionDiagnostic(Object substitution0, Object substitution1, Object substitution2);

  @Key("_UI_InvariantDelegateNotFound_diagnostic")
  @DefaultMessage("Unable to find delegate to evaluate the ''{0}'' invariant on ''{1}'': {2}")
  String invariantDelegateNotFoundDiagnostic(Object substitution0, Object substitution1, Object substitution2);

  @Key("_UI_ConstraintDelegateException_diagnostic")
  @DefaultMessage("An exception occurred while delegating evaluation of the ''{0}'' constraint on ''{1}'': {2}")
  String constraintDelegateExceptionDiagnostic(Object substitution0, Object substitution1, Object substitution2);

  @Key("_UI_ConstraintDelegateNotFound_diagnostic")
  @DefaultMessage("Unable to find delegate to evaluate the ''{0}'' constraint on ''{1}'': {2}")
  String constraintDelegateNotFoundDiagnostic(Object substitution0, Object substitution1, Object substitution2);
  
  @Key("_UI_EReferenceConsistentContainer_diagnostic")
  @DefaultMessage("A containment reference of a type with a container feature {0} that requires instances to be contained elsewhere cannot be populated")
  String consistentContainerDiagnostic(Object substitution0);
}
