/**
 * Copyright (c) 2002-2012 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIHandler;

/**
 * A collection of platform-neutral static utilities
 * as well as Eclipse support utilities.
 */
public class EcorePlugin  extends EMFPlugin
{
  /**
   * The singleton instance of the plugin.
   */
  public static final EcorePlugin INSTANCE = new EcorePlugin();

  /**
   * Creates the singleton instance.
   */
  private EcorePlugin()
  {
    super(new ResourceLocator[] {});
  }

  @Override
  public ResourceLocator getPluginResourceLocator()
  {
    return null;
  }
  
  protected static final EcorePluginProperties PROPERTIES;
  static
  {
    // TODO I guess servers can't translate
    // 
    PROPERTIES = (null);
  }
  

  @Override
  public String getString(String key, boolean translate)
  {
    if ("_UI_EMFDiagnostic_marker".equals(key)) return PROPERTIES.emfDiagnosticMarker();
    else if ("_UI_CircularContainment_diagnostic".equals(key)) return PROPERTIES.circularContainmentDiagnostic();
    else if ("parser.parse.1".equals(key)) return PROPERTIES.parserParse1();
    else if ("parser.parse.2".equals(key)) return PROPERTIES.parserParse2();
    else if ("parser.next.1".equals(key)) return PROPERTIES.parserNext1();
    else if ("parser.next.2".equals(key)) return PROPERTIES.parserNext2();
    else if ("parser.next.3".equals(key)) return PROPERTIES.parserNext3();
    else if ("parser.next.4".equals(key)) return PROPERTIES.parserNext4();
    else if ("parser.factor.1".equals(key)) return PROPERTIES.parserFactor1();
    else if ("parser.factor.2".equals(key)) return PROPERTIES.parserFactor2();
    else if ("parser.factor.3".equals(key)) return PROPERTIES.parserFactor3();
    else if ("parser.factor.4".equals(key)) return PROPERTIES.parserFactor4();
    else if ("parser.factor.5".equals(key)) return PROPERTIES.parserFactor5();
    else if ("parser.factor.6".equals(key)) return PROPERTIES.parserFactor6();
    else if ("parser.atom.1".equals(key)) return PROPERTIES.parserAtom1();
    else if ("parser.atom.2".equals(key)) return PROPERTIES.parserAtom2();
    else if ("parser.atom.3".equals(key)) return PROPERTIES.parserAtom3();
    else if ("parser.atom.4".equals(key)) return PROPERTIES.parserAtom4();
    else if ("parser.atom.5".equals(key)) return PROPERTIES.parserAtom5();
    else if ("parser.cc.1".equals(key)) return PROPERTIES.parserCc1();
    else if ("parser.cc.2".equals(key)) return PROPERTIES.parserCc2();
    else if ("parser.cc.3".equals(key)) return PROPERTIES.parserCc3();
    else if ("parser.cc.4".equals(key)) return PROPERTIES.parserCc4();
    else if ("parser.cc.5".equals(key)) return PROPERTIES.parserCc5();
    else if ("parser.cc.6".equals(key)) return PROPERTIES.parserCc6();
    else if ("parser.cc.7".equals(key)) return PROPERTIES.parserCc7();
    else if ("parser.cc.8".equals(key)) return PROPERTIES.parserCc8();
    else if ("parser.ope.1".equals(key)) return PROPERTIES.parserOpe1();
    else if ("parser.ope.2".equals(key)) return PROPERTIES.parserOpe2();
    else if ("parser.ope.3".equals(key)) return PROPERTIES.parserOpe3();
    else if ("parser.descape.1".equals(key)) return PROPERTIES.parserDescape1();
    else if ("parser.descape.2".equals(key)) return PROPERTIES.parserDescape2();
    else if ("parser.descape.3".equals(key)) return PROPERTIES.parserDescape3();
    else if ("parser.descape.4".equals(key)) return PROPERTIES.parserDescape4();
    else if ("parser.descape.5".equals(key)) return PROPERTIES.parserDescape5();
    else if ("parser.process.1".equals(key)) return PROPERTIES.parserProcess1();
    else if ("parser.quantifier.1".equals(key)) return PROPERTIES.parserQuantifier1();
    else if ("parser.quantifier.2".equals(key)) return PROPERTIES.parserQuantifier2();
    else if ("parser.quantifier.3".equals(key)) return PROPERTIES.parserQuantifier3();
    else if ("parser.quantifier.4".equals(key)) return PROPERTIES.parserQuantifier4();
    else if ("parser.quantifier.5".equals(key)) return PROPERTIES.parserQuantifier5();
    else if ("_UI_PackageRegistry_extensionpoint".equals(key)) return PROPERTIES.packageRegistryExtensionpoint();
    else if ("_UI_DynamicPackageRegistry_extensionpoint".equals(key)) return PROPERTIES.dynamicPackageRegistryExtensionpoint();
    else if ("_UI_FactoryRegistry_extensionpoint".equals(key)) return PROPERTIES.factoryRegistryExtensionpoint();
    else if ("_UI_URIExtensionParserRegistry_extensionpoint".equals(key)) return PROPERTIES.uriExtensionParserRegistryExtensionpoint();
    else if ("_UI_URIProtocolParserRegistry_extensionpoint".equals(key)) return PROPERTIES.uriProtocolParserRegistryExtensionpoint();
    else if ("_UI_URIContentParserRegistry_extensionpoint".equals(key)) return PROPERTIES.uriContentParserRegistryExtensionpoint();
    else if ("_UI_ContentHandlerRegistry_extensionpoint".equals(key)) return PROPERTIES.contentHandlerRegistryExtensionpoint();
    else if ("_UI_URIMappingRegistry_extensionpoint".equals(key)) return PROPERTIES.uriMappingRegistryExtensionpoint();
    else if ("_UI_PackageRegistryImplementation_extensionpoint".equals(key)) return PROPERTIES.packageRegistryImplementationExtensionpoint();
    else if ("_UI_ValidationDelegateRegistry_extensionpoint".equals(key)) return PROPERTIES.validationDelegateRegistryExtensionpoint();
    else if ("_UI_SettingDelegateRegistry_extensionpoint".equals(key)) return PROPERTIES.settingDelegateRegistryExtensionpoint();
    else if ("_UI_InvocationDelegateRegistry_extensionpoint".equals(key)) return PROPERTIES.invocationDelegateRegistryExtensionpoint();
    else if ("_UI_EClassInterfaceNotAbstract_diagnostic".equals(key)) return PROPERTIES.eClassInterfaceNotAbstractDiagnostic();
    else if ("_UI_EClassNoCircularSuperTypes_diagnostic".equals(key)) return PROPERTIES.eClassNoCircularSuperTypesDiagnostic();
    else if ("_UI_EClassNotWellFormedMapEntryNoInstanceClassName_diagnostic".equals(key)) return PROPERTIES.eClassNotWellFormedMapEntryNoInstanceClassNameDiagnostic();
    else if ("_UI_EReferenceOppositeOfOppositeInconsistent_diagnostic".equals(key)) return PROPERTIES.eReferenceOppositeOfOppositeInconsistentDiagnostic();
    else if ("_UI_EReferenceOppositeNotFeatureOfType_diagnostic".equals(key)) return PROPERTIES.eReferenceOppositeNotFeatureOfTypeDiagnostic();
    else if ("_UI_EReferenceTransientOppositeNotTransient_diagnostic".equals(key)) return PROPERTIES.eReferenceTransientOppositeNotTransientDiagnostic();
    else if ("_UI_EReferenceOppositeBothContainment_diagnostic".equals(key)) return PROPERTIES.eReferenceOppositeBothContainmentDiagnostic();
    else if ("_UI_EReferenceConsistentUnique_diagnostic".equals(key)) return PROPERTIES.eReferenceConsistentUniqueDiagnostic();
    else if ("_UI_ETypedElementNoType_diagnostic".equals(key)) return PROPERTIES.eTypedElementNoTypeDiagnostic();
    else if ("_UI_EAttributeNoDataType_diagnostic".equals(key)) return PROPERTIES.eAttributeNoDataTypeDiagnostic();
    else if ("_UI_EReferenceNoClass_diagnostic".equals(key)) return PROPERTIES.eReferenceNoClassDiagnostic();
    else if ("_UI_EGenericTypeNoTypeParameterAndClassifier_diagnostic".equals(key)) return PROPERTIES.eGenericTypeNoTypeParameterAndClassifierDiagnostic();
    else if ("_UI_EGenericTypeNoClass_diagnostic".equals(key)) return PROPERTIES.eGenericTypeNoClassDiagnostic();
    else if ("_UI_EGenericTypeNoTypeParameterOrClassifier_diagnostic".equals(key)) return PROPERTIES.eGenericTypeNoTypeParameterOrClassifierDiagnostic();
    else if ("_UI_EGenericTypeBoundsOnlyForTypeArgument_diagnostic".equals(key)) return PROPERTIES.eGenericTypeBoundsOnlyForTypeArgumentDiagnostic();
    else if ("_UI_EGenericTypeNoUpperAndLowerBound_diagnostic".equals(key)) return PROPERTIES.eGenericTypeNoUpperAndLowerBoundDiagnostic();
    else if ("_UI_EGenericTypeNoTypeParameterOrClassifierAndBound_diagnostic".equals(key)) return PROPERTIES.eGenericTypeNoTypeParameterOrClassifierAndBoundDiagnostic();
    else if ("_UI_EGenericTypeNoArguments_diagnostic".equals(key)) return PROPERTIES.eGenericTypeNoArgumentsDiagnostic();
    else if ("_UI_EGenericTypeOutOfScopeTypeParameter_diagnostic".equals(key)) return PROPERTIES.eGenericTypeOutOfScopeTypeParameterDiagnostic();
    else return key;
  }

  @Override
  public String getString(String key, Object [] substitutions, boolean translate)
  {
    if ("_UI_DiagnosticRoot_diagnostic".equals(key)) return PROPERTIES.diagnosticRootDiagnostic(substitutions[0]);
    else if ("_UI_RequiredFeatureMustBeSet_diagnostic".equals(key)) return PROPERTIES.requiredFeatureMustBeSetDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_FeatureHasTooFewValues_diagnostic".equals(key)) return PROPERTIES.featureHasTooFewValuesDiagnostic(substitutions[0], substitutions[1], substitutions[2], substitutions[3]);
    else if ("_UI_FeatureHasTooManyValues_diagnostic".equals(key)) return PROPERTIES.featureHasTooManyValuesDiagnostic(substitutions[0], substitutions[1], substitutions[2], substitutions[3]);
    else if ("_UI_DocumentRootMustHaveOneElement_diagnostic".equals(key)) return PROPERTIES.documentRootMustHaveOneElementDiagnostic(substitutions[0], substitutions[1], substitutions[2]);
    else if ("_UI_UnresolvedProxy_diagnostic".equals(key)) return PROPERTIES.unresolvedProxyDiagnostic(substitutions[0], substitutions[1], substitutions[2]);
    else if ("_UI_DanglingReference_diagnostic".equals(key)) return PROPERTIES.danglingReferenceDiagnostic(substitutions[0], substitutions[1], substitutions[2]);
    else if ("_UI_UnpairedBidirectionalReference_diagnostic".equals(key)) return PROPERTIES.unpairedBidirectionalReferenceDiagnostic(substitutions[0], substitutions[1], substitutions[2], substitutions[3]);
    else if ("_UI_BadDataValue_diagnostic".equals(key)) return PROPERTIES.badDataValueDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_MinInclusiveConstraint_diagnostic".equals(key)) return PROPERTIES.minInclusiveConstraintDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_MinExclusiveConstraint_diagnostic".equals(key)) return PROPERTIES.minExclusiveConstraintDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_MaxInclusiveConstraint_diagnostic".equals(key)) return PROPERTIES.maxInclusiveConstraintDiagnostic(substitutions[0], substitutions[2]);
    else if ("_UI_MaxExclusiveConstraint_diagnostic".equals(key)) return PROPERTIES.maxExclusiveConstraintDiagnostic(substitutions[0], substitutions[2]);
    else if ("_UI_MinLengthConstraint_diagnostic".equals(key)) return PROPERTIES.minLengthConstraintDiagnostic(substitutions[0], substitutions[1], substitutions[2]);
    else if ("_UI_MaxLengthConstraint_diagnostic".equals(key)) return PROPERTIES.maxLengthConstraintDiagnostic(substitutions[0], substitutions[1], substitutions[2]);
    else if ("_UI_EnumerationConstraint_diagnostic".equals(key)) return PROPERTIES.enumerationConstraintDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_PatternConstraint_diagnostic".equals(key)) return PROPERTIES.patternConstraintDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_TotalDigitsConstraint_diagnostic".equals(key)) return PROPERTIES.totalDigitsConstraintDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_FractionDigitsConstraint_diagnostic".equals(key)) return PROPERTIES.fractionDigitsConstraintDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_ListHead_composition".equals(key)) return PROPERTIES.listHeadComposition(substitutions[0]);
    else if ("_UI_ListTail_composition".equals(key)) return PROPERTIES.listTailComposition(substitutions[0], substitutions[1]);
    else if ("_UI_BadDataValueType_diagnostic".equals(key)) return PROPERTIES.badDataValueTypeDiagnostic(substitutions[0], substitutions[1], substitutions[2]);
    else if ("_UI_DuplicateID_diagnostic".equals(key)) return PROPERTIES.duplicateIDDiagnostic(substitutions[0], substitutions[1], substitutions[2]);
    else if ("_UI_DuplicateKey_diagnostic".equals(key)) return PROPERTIES.duplicateKeyDiagnostic(substitutions[0], substitutions[1], substitutions[2], substitutions[3]);
    else if ("_UI_DuplicateMapEntry_diagnostic".equals(key)) return PROPERTIES.duplicateMapEntryDiagnostic(substitutions[0], substitutions[1], substitutions[2]);
    else if ("_UI_BadXMLGregorianCalendar_diagnostic".equals(key)) return PROPERTIES.badXMLGregorianCalendarDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_GenericInvariant_diagnostic".equals(key)) return PROPERTIES.genericInvariantDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_GenericConstraint_diagnostic".equals(key)) return PROPERTIES.genericConstraintDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_EAnnotationSourceURINotWellFormed_diagnostic".equals(key)) return PROPERTIES.eAnnotationSourceURINotWellFormedDiagnostic(substitutions[0]);
    else if ("_UI_EAttributeConsistentTransient_diagnostic".equals(key)) return PROPERTIES.eAttributeConsistentTransientDiagnostic(substitutions[0]);
    else if ("_UI_ENamedElementNameNotWellFormed_diagnostic".equals(key)) return PROPERTIES.eNamedElementNameNotWellFormedDiagnostic(substitutions[0]);
    else if ("_UI_EClassifierInstanceTypeNameNotWellFormed_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameNotWellFormedDiagnostic(substitutions[0]);
    else if ("_UI_EClassAtMostOneID_diagnostic".equals(key)) return PROPERTIES.eClassAtMostOneIDDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_EClassUniqueEStructuralFeatureName_diagnostic".equals(key)) return PROPERTIES.eClassUniqueEStructuralFeatureNameDiagnostic(substitutions[0]);
    else if ("_UI_EClassDissimilarEStructuralFeatureName_diagnostic".equals(key)) return PROPERTIES.eClassDissimilarEStructuralFeatureNameDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_EClassUniqueEOperationSignatures_diagnostic".equals(key)) return PROPERTIES.eClassUniqueEOperationSignaturesDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_EClassDisjointFeatureAndOperationSignatures_diagnostic".equals(key)) return PROPERTIES.eClassDisjointFeatureAndOperationSignaturesDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_EClassNotWellFormedMapEntry_diagnostic".equals(key)) return PROPERTIES.eClassNotWellFormedMapEntryDiagnostic(substitutions[0]);
    else if ("_UI_EEnumUniqueEnumeratorNames_diagnostic".equals(key)) return PROPERTIES.eEnumUniqueEnumeratorNamesDiagnostic(substitutions[0]);
    else if ("_UI_EEnumDissimilarEnumeratorNames_diagnostic".equals(key)) return PROPERTIES.eEnumDissimilarEnumeratorNamesDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_EEnumUniqueEnumeratorLiterals_diagnostic".equals(key)) return PROPERTIES.eEnumUniqueEnumeratorLiteralsDiagnostic(substitutions[0]);
    else if ("_UI_UniqueTypeParameterNames_diagnostic".equals(key)) return PROPERTIES.uniqueTypeParameterNamesDiagnostic(substitutions[0]);
    else if ("_UI_EOperationUniqueParameterNames_diagnostic".equals(key)) return PROPERTIES.eOperationUniqueParameterNamesDiagnostic(substitutions[0]);
    else if ("_UI_EOperationNoRepeatingVoid_diagnostic".equals(key)) return PROPERTIES.eOperationNoRepeatingVoidDiagnostic(substitutions[0]);
    else if ("_UI_EPackageNsURINotWellFormed_diagnostic".equals(key)) return PROPERTIES.ePackageNsURINotWellFormedDiagnostic(substitutions[0]);
    else if ("_UI_EPackageNsPrefixNotWellFormed_diagnostic".equals(key)) return PROPERTIES.ePackageNsPrefixNotWellFormedDiagnostic(substitutions[0]);
    else if ("_UI_EPackageUniqueSubpackageNames_diagnostic".equals(key)) return PROPERTIES.ePackageUniqueSubpackageNamesDiagnostic(substitutions[0]);
    else if ("_UI_EPackageUniqueClassifierNames_diagnostic".equals(key)) return PROPERTIES.ePackageUniqueClassifierNamesDiagnostic(substitutions[0]);
    else if ("_UI_EPackageUniqueNsURIs_diagnostic".equals(key)) return PROPERTIES.ePackageUniqueNsURIsDiagnostic(substitutions[0]);
    else if ("_UI_EPackageDissimilarClassifierNames_diagnostic".equals(key)) return PROPERTIES.ePackageDissimilarClassifierNamesDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_EReferenceSingleContainer_diagnostic".equals(key)) return PROPERTIES.eReferenceSingleContainerDiagnostic(substitutions[0]);
    else if ("_UI_EReferenceConsistentKeys_diagnostic".equals(key)) return PROPERTIES.eReferenceConsistentKeysDiagnostic(substitutions[0]);
    else if ("_UI_ETypedElementValidLowerBound_diagnostic".equals(key)) return PROPERTIES.eTypedElementValidLowerBoundDiagnostic(substitutions[0]);
    else if ("_UI_ETypedElementValidUpperBound_diagnostic".equals(key)) return PROPERTIES.eTypedElementValidUpperBoundDiagnostic(substitutions[0]);
    else if ("_UI_ETypedElementConsistentBounds_diagnostic".equals(key)) return PROPERTIES.eTypedElementConsistentBoundsDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_EGenericTypeArgumentsNeeded_diagnostic".equals(key)) return PROPERTIES.eGenericTypeArgumentsNeededDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_EGenericTypeArgumentInvalidSubstitution_diagnostic".equals(key)) return PROPERTIES.eGenericTypeArgumentInvalidSubstitutionDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_EGenericTypeIncorrectArguments_diagnostic".equals(key)) return PROPERTIES.eGenericTypeIncorrectArgumentsDiagnostic(substitutions[0], substitutions[1], substitutions[2]);
    else if ("_UI_EGenericTypeInvalidPrimitiveType_diagnostic".equals(key)) return PROPERTIES.eGenericTypeInvalidPrimitiveTypeDiagnostic(substitutions[0]);
    else if ("_UI_EClassNoDuplicateSuperTypes_diagnostic".equals(key)) return PROPERTIES.eClassNoDuplicateSuperTypesDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_EClassConsistentSuperTypes_diagnostic".equals(key)) return PROPERTIES.eClassConsistentSuperTypesDiagnostic(substitutions[0]);
    else if ("_UI_EStructuralFeatureValidDefaultValueLiteral_diagnostic".equals(key)) return PROPERTIES.eStructuralFeatureValidDefaultValueLiteralDiagnostic(substitutions[0]);
    else if ("_UI_EClassifierInstanceTypeNameAnalysisResult_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameAnalysisResultDiagnostic(substitutions[0]);
    else if ("_UI_EClassifierInstanceTypeNameBracketWithoutPrecedingIdentifier_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameBracketWithoutPrecedingIdentifierDiagnostic(substitutions[0]);
    else if ("_UI_EClassifierInstanceTypeNameNoClosingBracket_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameNoClosingBracketDiagnostic(substitutions[0]);
    else if ("_UI_EClassifierInstanceTypeNameNoClosingBracket2_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameNoClosingBracket2Diagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_EClassifierInstanceTypeNameBracketExpected_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameBracketExpectedDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_EClassifierInstanceTypeNameDotWithoutPrecedingIdentifier_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameDotWithoutPrecedingIdentifierDiagnostic(substitutions[0]);
    else if ("_UI_EClassifierInstanceTypeNameDotExpectedBeforeIdentifier_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameDotExpectedBeforeIdentifierDiagnostic(substitutions[0]);
    else if ("_UI_EClassifierInstanceTypeNameAngleBracketWithoutPrecedingIdentifier_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameAngleBracketWithoutPrecedingIdentifierDiagnostic(substitutions[0]);
    else if ("_UI_EClassifierInstanceTypeNameUnterminatedAngleBracket_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameUnterminatedAngleBracketDiagnostic(substitutions[0]);
    else if ("_UI_EClassifierInstanceTypeNameUnexpectedCharacter_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameUnexpectedCharacterDiagnostic(substitutions[0], substitutions[1]);
    else if ("_UI_EClassifierInstanceTypeNameTooManyQuestionMarks_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameTooManyQuestionMarksDiagnostic(substitutions[0]);
    else if ("_UI_EClassifierInstanceTypeNameExpectingExtends_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameExpectingExtendsDiagnostic(substitutions[0]);
    else if ("_UI_EClassifierInstanceTypeNameExpectingSuper_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameExpectingSuperDiagnostic(substitutions[0]);
    else if ("_UI_EClassifierInstanceTypeNameExpectingExtendsOrSuper_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameExpectingExtendsOrSuperDiagnostic(substitutions[0]);
    else if ("_UI_EClassifierInstanceTypeNameTypeArgumentExpected_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameTypeArgumentExpectedDiagnostic(substitutions[0]);
    else if ("_UI_EClassifierInstanceTypeNameExpectingIdentifier_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameExpectingIdentifierDiagnostic(substitutions[0]);
    else if ("_UI_EClassifierInstanceTypeNameExpectedSpace_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameExpectedSpaceDiagnostic(substitutions[0]);
    else if ("_UI_EClassifierInstanceTypeNameUnexpectedSpace_diagnostic".equals(key)) return PROPERTIES.eClassifierInstanceTypeNameUnexpectedSpaceDiagnostic(substitutions[0]);
    else if ("_UI_InvariantDelegateException_diagnostic".equals(key)) return PROPERTIES.invariantDelegateExceptionDiagnostic(substitutions[0], substitutions[1], substitutions[2]);
    else if ("_UI_InvariantDelegateNotFound_diagnostic".equals(key)) return PROPERTIES.invariantDelegateNotFoundDiagnostic(substitutions[0], substitutions[1], substitutions[2]);
    else if ("_UI_ConstraintDelegateException_diagnostic".equals(key)) return PROPERTIES.constraintDelegateExceptionDiagnostic(substitutions[0], substitutions[1], substitutions[2]);
    else if ("_UI_ConstraintDelegateNotFound_diagnostic".equals(key)) return PROPERTIES.constraintDelegateNotFoundDiagnostic(substitutions[0], substitutions[1], substitutions[2]);
    else if ("_UI_EReferenceConsistentContainer_diagnostic".equals(key)) return PROPERTIES.consistentContainerDiagnostic(substitutions[0]);
    else return key;
  }

  /**
   * Returns the platform resource map.
   * <p>
   * This map is from {@link String} to {@link URI}.
   * This map is used to {@link #resolvePlatformResourcePath resolve} a platform resource path,
   * and thereby supports relocatable projects in a manner that is transparently the same as an Eclipse workspace.
   * </p>
   * @return the platform resource map.
   * @see #resolvePlatformResourcePath
   */
  public static Map<String, URI> getPlatformResourceMap()
  {
    if (platformResourceMap == null)
    {
      platformResourceMap = new HashMap<String, URI>();
    }
    return platformResourceMap;
  }

  /**
   * Resolves a platform resource path of the form <code>"/project/path"</code> 
   * against the platform resource map.
   * <p>
   * The first segment of the path, i.e., the <em>project name</em>,
   * is used to get a URI from the {@link #getPlatformResourceMap() map}.
   * If a URI results, the remaining segments are {@link URI#resolve(URI) resolved} against it
   * and that is the result.
   * Otherwise, the result is <code>null</code>.
   * For example, given this mapping
   *<pre>
   *  EcoreUtil.getPlatformResourceMap().put
   *    ("project", URI.createURI("file:///C:/location/"));
   *</pre>
   * the following transformation would result:
   *<pre>
   *  /project/directory/file
   *    ->
   *  file:///C:/location/directory/file
   *</pre>
   * </p>
   * @return the resolved URI or <code>null</code>.
   */
  public static URI resolvePlatformResourcePath(String platformResourcePath)
  {
    if (platformResourceMap != null)
    {
      int index = platformResourcePath.indexOf("/", 1);
      String rootContainerName = platformResourcePath.substring(1, index);
      String relativeName = platformResourcePath.substring(index + 1);
      URI rootContainerLocation = getPlatformResourceMap().get(rootContainerName);
      if (rootContainerLocation != null)
      {
        return URI.createURI(relativeName).resolve(rootContainerLocation);
      }
    }
    return null;
  }

  /**
   * Handles recognized platform resource arguments and returns the stripped result.
   * <p>
   * Recognized arguments are of this form:
   *<pre>
   *  -platformResource ( &lt;project-name> &lt;file-or-URI> )+
   *</pre>
   * E.g., This these arguments
   *<pre>
   *  -platformResource project file:///C:/location/
   *</pre>
   * will produce this effect:
   *<pre>
   *  EcoreUtil.getPlatformResourceMap().put
   *    ("project", URI.createURI("file:///C:/location/"));
   *</pre>
   * This mechanism supports relocatable projects outside of Eclipse.
   * </p>
   * @param arguments an array of "command line" options.
   * @return the arguments stripped of those recognized as platform resource options.
   */
  public static String [] handlePlatformResourceOptions(String [] arguments)
  {
    getPlatformResourceMap();

    for (int i = 0; i < arguments.length; ++i)
    {
      if (arguments[i].equalsIgnoreCase("-platformResource"))
      {
        int start = i;
        while (++i < arguments.length && !arguments[i].startsWith("-"))
        {
          String rootContainerName = arguments[i];
          if (++i < arguments.length)
          {
            String rootContainerLocation = arguments[i];

            // This let's us test whether the string exists as a file.
            // If not, we try as a URI.
            //
            URI uri = URI.createURI(rootContainerLocation);

            platformResourceMap.put(rootContainerName, uri);
          }
        }

        String [] remainingArguments = new String [arguments.length - (i - start)];
        System.arraycopy(arguments, 0, remainingArguments, 0, start);
        System.arraycopy(arguments, i, remainingArguments, start, arguments.length - i);
        return remainingArguments;
      }
    }

    return arguments;
  }
  
  /**
   * The platform resource map.
   * @see #getPlatformResourceMap
   */
  private static Map<String, URI> platformResourceMap;

  /**
   * @see {@link URIHandler#DEFAULT_HANDLERS}
   */
  public static List<URIHandler> DEFAULT_URI_HANDLERS = new ArrayList<URIHandler>();
}