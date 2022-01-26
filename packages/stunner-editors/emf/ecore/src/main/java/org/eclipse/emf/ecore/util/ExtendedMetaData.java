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
package org.eclipse.emf.ecore.util;


import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;


/**
 * Interface for accessing and setting extended metadata on Ecore model elements. Such metadata is primarily used to
 * support structures defined in XML schema and to retain additional information that a resource requires to produce
 * conforming serializations. However, non-schema-based models may use this interface to support constructs such as
 * mixed content, repeating model groups, and wildcard features, as well as to customize model serialization.
 * 
 * <p>
 * The extended metadata is stored on an Ecore model element as an annotation with source {@link #ANNOTATION_URI}. Each
 * piece of information is recorded as a key-value pair of strings in its details map. This interface provides
 * the following utility methods to set and query those annotations:
 * 
 * <ul>
 * <li>{@link #getNamespace(EPackage) getNamespace} (package)</li>
 * <li>{@link #isQualified isQualified}/{@link #setQualified setQualified}</li>
 * <li>{@link #getName(EClassifier) getName}/{@link #setName(EClassifier, String) setName} (classifier)</li>
 * <li>{@link #getNamespace(EClassifier) getNamespace} (classifier)</li>
 * <li>{@link #getName(EStructuralFeature getName) getName}/{@link #setName(EStructuralFeature, String) setName} (structural feature)</li>
 * <li>{@link #getNamespace(EStructuralFeature) getNamespace}/{@link #setNamespace(EStructuralFeature, String) setNamespace}</li>
 * <li>{@link #getFeatureKind getFeatureKind}/{@link #setFeatureKind setFeatureKind}</li>
 * <li>{@link #getContentKind getContentKind}/{@link #setContentKind setContentKind}</li>
 * <li>{@link #getBaseType getBaseType}/{@link #setBaseType setBaseType}</li>
 * <li>{@link #getItemType getItemType}/{@link #setItemType setItemType}</li>
 * <li>{@link #getMemberTypes getMemberTypes}/{@link #setMemberTypes setMemberTypes}</li>
 * <li>{@link #getWildcards getWildcards}/{@link #setWildcards setWildcards}</li>
 * <li>{@link #getProcessingKind getProcessingKind}/{@link #setProcessingKind setProcessingKind}</li>
 * <li>{@link #getAffiliation(EClass, EStructuralFeature) getAffiliation}/{@link #setAffiliation setAffiliation}</li>
 * <li>{@link #getGroup getGroup}/{@link #setGroup setGroup}</li>
 * <li>{@link #getWhiteSpaceFacet getWhiteSpaceFacet}/{@link #setWhiteSpaceFacet setWhiteSpaceFacet}</li>
 * <li>{@link #getEnumerationFacet getEnumerationFacet}/{@link #setEnumerationFacet setEnumerationFacet}</li>
 * <li>{@link #getPatternFacet getPatternFacet}/{@link #setPatternFacet setPatternFacet}</li>
 * <li>{@link #getTotalDigitsFacet getTotalDigitsFacet}/{@link #setTotalDigitsFacet setTotalDigitsFacet}</li>
 * <li>{@link #getFractionDigitsFacet getFractionDigitsFacet}/{@link #setFractionDigitsFacet setFractionDigitsFacet}</li>
 * <li>{@link #getLengthFacet getLengthFacet}/{@link #setLengthFacet setLengthFacet}</li>
 * <li>{@link #getMinLengthFacet getMinLengthFacet}/{@link #setMinLengthFacet setMinLengthFacet}</li>
 * <li>{@link #getMaxLengthFacet getMaxLengthFacet}/{@link #setMaxLengthFacet setMaxLengthFacet}</li>
 * <li>{@link #getMinExclusiveFacet getMinExclusiveFacet}/{@link #setMinExclusiveFacet setMinExclusiveFacet}</li>
 * <li>{@link #getMaxExclusiveFacet getMaxExclusiveFacet}/{@link #setMaxExclusiveFacet setMaxExclusiveFacet}</li>
 * <li>{@link #getMinInclusiveFacet getMinInclusiveFacet}/{@link #setMinInclusiveFacet setMinInclusiveFacet}</li>
 * <li>{@link #getMaxInclusiveFacet getMaxInclusiveFacet}/{@link #setMaxInclusiveFacet setMaxInclusiveFacet}</li>
 * </ul>
 * 
 * <p>
 * In additional, a number of convenience lookup methods are provided.
 * 
 * <p>
 * A single instance can be used to handle all related (or unrelated) metadata, as its only state is the package
 * registry from which to obtain metadata, and the demand package registry, which stores demand-created metadata when
 * processing data for which metadata has not already been defined.
 */
public interface ExtendedMetaData
{
  /**
   * The URI used as the annotation source: "http:///org/eclipse/emf/ecore/util/ExtendedMetaData".
   */
  String ANNOTATION_URI = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";

  /**
   * The namespace URI for XML namespaces: "http://www.w3.org/2000/xmlns/".
   */
  String XMLNS_URI = "http://www.w3.org/2000/xmlns/";

  /**
   * The XML namespace URI: "http://www.w3.org/XML/1998/namespace".
   */
  String XML_URI = "http://www.w3.org/XML/1998/namespace";

  /**
   * The XML Schema instance namespace URI: "http://www.w3.org/2001/XMLSchema-instance".
   */
  String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";

  /**
   * The XML Schema namespace URI: "http://www.w3.org/2001/XMLSchema".
   */
  String XML_SCHEMA_URI = "http://www.w3.org/2001/XMLSchema";

  /**
   * The XMI namespace URI: "http://www.omg.org/XMI".
   */
  String XMI_URI = "http://www.omg.org/XMI";

  /**
   * The prefix reserved for XML namespaces: "xmlns".
   */
  String XMLNS_PREFIX = "xmlns";

  /**
   * The prefix used for the XML Schema instance namespace.
   */
  String XSI_PREFIX = "xsi";

  /**
   * Retrieves the package with the specified namespace URI from the package registry associated with this instance. 
   */
  EPackage getPackage(String namespace);

  /**
   * Adds the package to the package registry associated with this instance, keyed by the given namespace URI.
   */
  void putPackage(String namespace, EPackage ePackage);

  /**
   * Returns the class corresponding to the document root in the given package. The document root class holds attributes
   * and references corresponding to attributes and elements declared globally in a schema. It is identified by its
   * XML name, an empty string.
   */
  EClass getDocumentRoot(EPackage ePackage);

  /**
   * Sets the specified class to be a document root. The document root class holds attributes and references
   * corresponding to attributes and elements declared globally in a schema. Its XML name is set to an empty string,
   * and its content kind to mixed content.
   */  
  void setDocumentRoot(EClass eClass);

  /**
   * Returns whether the given class is a document root, i.e., whether it has an empty string as its XML Name.
   * @since 2.4
   */
  boolean isDocumentRoot(EClass eClass);

  /**
   * If the given class has mixed content type, returns the "xmlns:prefix" feature, which is used to store namespace
   * prefix-to-URI mappings.
   */
  EReference getXMLNSPrefixMapFeature(EClass eClass);

  /**
   * If the given class has mixed content type, returns the "xsi:schemaLocation" feature, which is used to store
   * namespace URI-schema location pairs.
   */
  EReference getXSISchemaLocationMapFeature(EClass eClass);

  /**
   * Returns whether model instance serializations of the specified package should use namespace qualification.
   * <p>details key: "qualified"
   */
  boolean isQualified(EPackage ePackage);

  /**
   * Sets whether model instance serializations of the specified package should use namespace qualification. 
   * <p>details key: "qualified"
   */
  void setQualified(EPackage ePackage, boolean isQualified);

  /**
   * Returns the namespace to use for model instance serializations of the specified package. If namespace qualification
   * is being used, this is the package's namespace URI; if not, it is simply null. 
   * <p>details key: "namespace"
   */
  String getNamespace(EPackage ePackage);

  /**
   * Returns the namespace to use for instances of the given classifier, the same namespace as for the package that
   * contains it.
   */
  String getNamespace(EClassifier eClassifier);

  /**
   * Returns the namespace associated with the specified structural feature. This allows features to be included in a
   * class that correspond to elements and attributes defined in another schema (i.e. an attribute or element
   * reference).
   */
  String getNamespace(EStructuralFeature eStructuralFeature);

  /**
   * Sets the namespace associated with the specified structural feature. This allows features to be included in a
   * class that correspond to elements and attributes defined in another schema (i.e. an attribute or element
   * reference).
   */
  void setNamespace(EStructuralFeature eStructuralFeature, String namespace);

  /**
   * Returns the XML name for a classifier. This is the original name specified for the type in the schema, which
   * may be mapped into a valid and conventional Java class name for the Ecore class.
   * <p>details key: "name"
   */
  String getName(EClassifier eClassifier);

  /**
   * Sets the XML name for a classifier.  This should be the original name specified for the type in the schema, which
   * may be mapped into a valid and conventional Java class name for the Ecore class.
   * <p>details key: "name"
   */
  void setName(EClassifier eClassifier, String name);

  /**
   * Returns whether the given classifier's XML name contains "_._", which is used to indicate an anonymous type
   * declaration. 
   */
  boolean isAnonymous(EClassifier eClassifier);

  /**
   * Returns the XML name for a structural feature. This is the original name specified for the element or attribute
   * in the schema, which may be mapped into a valid and conventional Java field name for the Ecore structural feature.
   * This is also the name which should be used for the element or attribute in instance documents.
   * <p>details key: "name"
   */
  String getName(EStructuralFeature eStructuralFeature);

  /**
   * Set the XML name for a structural feature. This should be the original name specified for the element or attribute
   * in the schema, which may be mapped into a valid and conventional Java field name for the Ecore structural feature.
   * This is also the name which should be used for the element or attribute in instance documents.
   * <p>details key: "name"
   */
  void setName(EStructuralFeature eStructuralFeature, String name);

  /**
   * Returns the classifier with the given XML name within the package with the given namespace.
   */
  EClassifier getType(String namespace, String name);

  /**
   * Returns the structural feature with the given XML name that corresponds to a global attribute within the package
   * with the given namespace.
   */
  EStructuralFeature getAttribute(String namespace, String name);

  /**
   * Returns the structural feature with the given XML name that corresponds to a global element within the package
   * with the given namespace.
   */
  EStructuralFeature getElement(String namespace, String name);

  /**
   * Returns the classifier with the given XML name within the given package.
   */
  EClassifier getType(EPackage ePackage, String name);

  /**
   * Returns a structural feature within a class, corresponding to a local attribute with the given namespace and name,
   * or, failing that, a document root feature corresponding to a global attribute with the given namespace and name
   * that is {@link #getAffiliation(EClass, EStructuralFeature) affiliated} with a feature in the class. 
   */
  EStructuralFeature getAttribute(EClass eClass, String namespace, String name);

  /**
   * Returns a structural feature within a class, corresponding to a local element with the given namespace and name,
   * or, failing that, a document root feature corresponding to a global element with the given namespace and name
   * that is {@link #getAffiliation(EClass, EStructuralFeature) affiliated} with a feature in the class. 
   */
  EStructuralFeature getElement(EClass eClass, String namespace, String name);

  /**
   * If the given class represents simple content, returns the simple feature used to store its content.
   */
  EStructuralFeature getSimpleFeature(EClass eClass);

  /**
   * If the given class represents mixed content, returns the wildcard element feature used to store its content.
   */
  EAttribute getMixedFeature(EClass eClass);

  /**
   * The feature kind ID for an unspecified kind.
   * @see #getFeatureKind
   * @see #setFeatureKind 
   */
  int UNSPECIFIED_FEATURE = 0;

  /**
   * The feature kind ID representing a simple value.
   * @see #getFeatureKind
   * @see #setFeatureKind 
   */
  int SIMPLE_FEATURE = 1;

  /**
   * The feature kind ID representing an attribute.
   * @see #getFeatureKind
   * @see #setFeatureKind 
   */
  int ATTRIBUTE_FEATURE = 2;

  /**
   * The feature kind ID representing an anyAttribute wildcard.
   * @see #getFeatureKind
   * @see #setFeatureKind 
   */
  int ATTRIBUTE_WILDCARD_FEATURE = 3;

  /**
   * The feature kind ID representing an element.
   * @see #getFeatureKind
   * @see #setFeatureKind 
   */
  int ELEMENT_FEATURE = 4;

  /**
   * The feature kind ID representing an any element wildcard.
   * @see #getFeatureKind
   * @see #setFeatureKind 
   */
  int ELEMENT_WILDCARD_FEATURE = 5;

  /**
   * The feature kind ID representing a repeating model group.
   * @see #getFeatureKind
   * @see #setFeatureKind 
   */
  int GROUP_FEATURE = 6;

  /**
   * The strings used to represent the various feature kinds in extended metadata annotations, indexed by kind ID. 
   * @see #getFeatureKind
   * @see #setFeatureKind 
   */
  String [] FEATURE_KINDS = { "unspecified", "simple", "attribute", "attributeWildcard", "element", "elementWildcard", "group" };

  /**
   * Returns the kind of XML structure that should be used to represent the given structural feature.
   * <p>details key: "kind"
   * @see #UNSPECIFIED_FEATURE
   * @see #SIMPLE_FEATURE
   * @see #ATTRIBUTE_FEATURE
   * @see #ATTRIBUTE_WILDCARD_FEATURE
   * @see #ELEMENT_FEATURE
   * @see #ELEMENT_WILDCARD_FEATURE
   * @see #GROUP_FEATURE
   */
  int getFeatureKind(EStructuralFeature eStructuralFeature);

  /**
   * Sets the kind of XML structure that should be used to represent the given structural feature.
   * <p>details key: "kind"
   * @see #UNSPECIFIED_FEATURE
   * @see #SIMPLE_FEATURE
   * @see #ATTRIBUTE_FEATURE
   * @see #ATTRIBUTE_WILDCARD_FEATURE
   * @see #ELEMENT_FEATURE
   * @see #ELEMENT_WILDCARD_FEATURE
   * @see #GROUP_FEATURE
   */
  void setFeatureKind(EStructuralFeature eStructuralFeature, int kind);

  /**
   * The content kind ID for an unspecified kind.
   * @see #getContentKind
   * @see #setContentKind
   */
  int UNSPECIFIED_CONTENT = 0;

  /**
   * The content kind ID specifying empty content.
   * @see #getContentKind
   * @see #setContentKind
   */
  int EMPTY_CONTENT = 1;

  /**
   * The content kind ID specifying simple content.
   * @see #getContentKind
   * @see #setContentKind
   */
  int SIMPLE_CONTENT = 2;

  /**
   * The content kind ID specifying mixed content.
   * @see #getContentKind
   * @see #setContentKind
   */
  int MIXED_CONTENT = 3;

  /**
   * The content kind ID specifying element content.
   * @see #getContentKind
   * @see #setContentKind
   */
  int ELEMENT_ONLY_CONTENT = 4;

  /**
   * The strings used to represent the various content kinds in extended metadata annotations, indexed by kind ID. 
   * @see #getContentKind
   * @see #setContentKind 
   */  
  String [] CONTENT_KINDS = { "unspecified", "empty", "simple", "mixed", "elementOnly" };

  /**
   * Returns the kind of XML content of the type corresponding to the given class.
   * <p>details key: "kind"
   * @see #UNSPECIFIED_CONTENT
   * @see #EMPTY_CONTENT
   * @see #SIMPLE_CONTENT
   * @see #MIXED_CONTENT
   * @see #ELEMENT_ONLY_CONTENT
   */
  int getContentKind(EClass eClass);

  /**
   * Sets the kind of XML content of the type corresponding to the given class.
   * <p>details key: "kind"
   * @see #UNSPECIFIED_CONTENT
   * @see #EMPTY_CONTENT
   * @see #SIMPLE_CONTENT
   * @see #MIXED_CONTENT
   * @see #ELEMENT_ONLY_CONTENT
   */
  void setContentKind(EClass eClass, int kind);

  /**
   * The derivation kind ID for an unspecified derivation.
   * @see #getDerivationKind
   */
  int UNSPECIFIED_DERIVATION = 0;

  /**
   * The derivation kind ID for restriction.
   * @see #getDerivationKind
   */
  int RESTRICTION_DERIVATION = 1;

  /**
   * The derivation kind ID for list.
   * @see #getDerivationKind
   */
  int LIST_DERIVATION = 2;

  /**
   * The derivation kind ID for union.
   * @see #getDerivationKind
   */
  int UNION_DERIVATION = 3;

  /**
   * The strings used to represent the various derivation kinds , indexed by kind ID.
   * @see #getDerivationKind
   */
  String [] DERIVATION_KINDS = { "unspecified", "restriction", "list", "union" };

  /**
   * Returns the derivation kind of the simple type corresponding to the given data type.
   * @see #UNSPECIFIED_DERIVATION
   * @see #RESTRICTION_DERIVATION
   * @see #LIST_DERIVATION
   * @see #UNION_DERIVATION
   */
  int getDerivationKind(EDataType eDataType);

  /**
   * If a data type corresponds to simple type that derives from another by restriction, returns the data type
   * corresponding to the base type.
   * <p>details key: "baseType" 
   */
  EDataType getBaseType(EDataType eDataType);

  /**
   * Sets the base type for a data type, indicating that the data type corresponds to a simple type that derives from
   * another by restriction.
   * <p>details key: "baseType"
   */
  void setBaseType(EDataType eDataType, EDataType baseType);

  /**
   * If a data type corresponds to a list type, returns the data type corresponding to its item type.
   * <p>details key: "itemType"
   */
  EDataType getItemType(EDataType eDataType);

  /**
   * Sets the item type for a data type, indicating that the data type corresponds to a list type. 
   * <p>details key: "itemType"
   */
  void setItemType(EDataType eDataType, EDataType itemType);

  /**
   * If a data type corresponds to a union type, returns the data types corresponding to its member types. 
   * <p>details key: "memberTypes"
   */
  List<EDataType> getMemberTypes(EDataType eDataType);

  /**
   * Sets the member types for a data type, indicating that the data type corresponds to a union type. 
   * <p>details key: "memberTypes"
   */
  void setMemberTypes(EDataType eDataType, List<EDataType> memberTypes);

  /**
   * Returns all the structural features of the given class, and its super classes, corresponding to XML attributes
   * and attribute wildcards.
   */
  List<EStructuralFeature> getAllAttributes(EClass eClass);

  /**
   * Returns all the structural features of the given class, and its super classes, corresponding to elements, element
   * wildards, and model groups.
   */
  List<EStructuralFeature> getAllElements(EClass eClass);

  /**
   * Returns the structural features of the given class corresponding to XML attributes and attribute wildcards.
   */
  List<EStructuralFeature> getAttributes(EClass eClass);

  /**
   * Returns the structural features of the given class corresponding to elements, element wildcards, and model groups.
   */
  List<EStructuralFeature> getElements(EClass eClass);

  /**
   * Tests whether any of a list of wildcards matches a given namespace.
   */
  boolean matches(List<String> wildcards, String namespace);

  /**
   * Tests whether a wildcard matches a given namespace. 
   */
  boolean matches(String wildcard, String namespace);

  /**
   * Returns the allowable namespace patterns for a structural feature corresponding to an any or anyAttribute wildcard.
   * <p>details key: "wildcards" 
   */
  List<String> getWildcards(EStructuralFeature eStructuralFeature);

  /**
   * Sets the allowable namespace patterns for a structural feature corresponding to an any or anyAttribute wildcard.
   * <p>details key: "wildcards"
   */
  void setWildcards(EStructuralFeature eStructuralFeature, List<String> wildcards);

  /**
   * The processing kind ID for unspecified processing.
   * @see #getProcessingKind
   * @see #setProcessingKind
   */
  int UNSPECIFIED_PROCESSING = 0;

  /**
   * The processing kind ID for strict processing. This requires that metadata be available in order to parse content.
   * @see #getProcessingKind
   * @see #setProcessingKind
   */
  int STRICT_PROCESSING = 1;

  /**
   * The processing kind ID for lax processing.
   * @see #getProcessingKind
   * @see #setProcessingKind
   */
  int LAX_PROCESSING = 2;

  /**
   * The processing kind ID for skip processing.
   * @see #getProcessingKind
   * @see #setProcessingKind
   */
  int SKIP_PROCESSING = 3;

  /**
   * The strings used to represent the various processing kinds in extended metadata annotations, indexed by kind ID. 
   * @see #getProcessingKind
   * @see #setProcessingKind
   */
  String [] PROCESSING_KINDS = { "unspecified", "strict", "lax", "skip" };

  /**
   * Returns the kind of contents processing to be used for a structural feature corresponding to an any wildcard.
   * <p>details key: "processing"
   * @see #UNSPECIFIED_PROCESSING
   * @see #STRICT_PROCESSING
   * @see #LAX_PROCESSING
   * @see #SKIP_PROCESSING
   */
  int getProcessingKind(EStructuralFeature eStructuralFeature);

  /**
   * Sets the kind of contents processing to be used for a structural feature corresponding to an any wildcard.
   * <p>details key: "processing"
   * @see #UNSPECIFIED_PROCESSING
   * @see #STRICT_PROCESSING
   * @see #LAX_PROCESSING
   * @see #SKIP_PROCESSING
   */
  void setProcessingKind(EStructuralFeature eStructuralFeature, int processingKind);

  /**
   * Returns the substitution group affiliation for the given structural feature, that is, the feature corresponding to
   * the head element of the substitution group to which the element corresponding to the given feature belongs.
   * <p>details key: "affiliation"
   */
  EStructuralFeature getAffiliation(EStructuralFeature eStructuralFeature);

  /**
   * Sets the substitution group affiliation for the given structural feature. Both feature and its affiliation feature
   * should be in a document root class, such that they correspond to global structural feature declarations.
   * <p>details key: "affiliation"
   */
  void setAffiliation(EStructuralFeature eStructuralFeature, EStructuralFeature affiliation);

  /**
   * Returns the feature respresenting the model group or subtitution group head feature to which the implementation
   * of the given feature is delegated.
   * <p>details key: "group"
   */
  EStructuralFeature getGroup(EStructuralFeature eStructuralFeature);

  /**
   * Sets a group or substitution group head feature, to which the implementation of the given feature should be
   * delegated.
   * <p>details key: "group"
   */
  void setGroup(EStructuralFeature eStructuralFeature, EStructuralFeature group);

  /**
   * Returns a structural feature within the given class that is or is associated with the given structural feature.
   * If the feature corresponds to an attribute, it will return the given feature itself, a feature matching the given
   * one in the document root (corresponding to a reference to the global attribute), or a wildcard attribute feature
   * appropriate for the namespace of the given feature. If it corresponds to an element, it will return the feature
   * itself, a feature matching it in the document root or its affiliations (corresponding to a reference to the global
   * element, or its head, or its head's head, etc.), the mixed content feature for an XML type (text, CDATA, or
   * comment), or a wildcard element feature appropriate for the namespace of the given feature.
   */
  EStructuralFeature getAffiliation(EClass eClass, EStructuralFeature eStructuralFeature);

  /**
   * Returns a wildcard feature in the given class that allows allows attribute values from the given namespace.
   */
  EStructuralFeature getAttributeWildcardAffiliation(EClass eClass, String namespace, String name);

  /**
   * Returns a wildcard feature in the given class that allows allows element values from the given namespace.
   */
  EStructuralFeature getElementWildcardAffiliation(EClass eClass, String namespace, String name);

  /**
   * The white space kind ID for an unspecified kind.
   * @see #getWhiteSpaceFacet
   * @see #setWhiteSpaceFacet 
   */
  int UNSPECIFIED_WHITE_SPACE = 0;

  /**
   * The white space kind ID for preserve.
   * @see #getWhiteSpaceFacet
   * @see #setWhiteSpaceFacet 
   */
  int PRESERVE_WHITE_SPACE = 1;

  /**
   * The white space kind ID for replace (each white space character by a space).
   * @see #getWhiteSpaceFacet
   * @see #setWhiteSpaceFacet 
   */
  int REPLACE_WHITE_SPACE = 2;

  /**
   * The white space kind ID for collapse (all contiguous white space characters to a single space).
   * @see #getWhiteSpaceFacet
   * @see #setWhiteSpaceFacet 
   */
  int COLLAPSE_WHITE_SPACE = 3;

  /**
   * The strings used to represent the various white space kinds in extended metadata annotations, indexed by kind ID. 
   * @see #getWhiteSpaceFacet
   * @see #setWhiteSpaceFacet
   */
  String [] WHITE_SPACE_KINDS = { "unspecified", "preserve", "replace", "collapse" };

  /**
   * Returns the white space constraint on the given data type.
   * <p>details key: "whiteSpace"
   * @see #UNSPECIFIED_WHITE_SPACE
   * @see #PRESERVE_WHITE_SPACE
   * @see #REPLACE_WHITE_SPACE
   * @see #COLLAPSE_WHITE_SPACE
   */
  int getWhiteSpaceFacet(EDataType eDataType);

  /**
   * Sets the white space constraint on the given data type.
   * <p>details key: "whiteSpace"
   * @see #UNSPECIFIED_WHITE_SPACE
   * @see #PRESERVE_WHITE_SPACE
   * @see #REPLACE_WHITE_SPACE
   * @see #COLLAPSE_WHITE_SPACE
   */
  void setWhiteSpaceFacet(EDataType eDataType, int whiteSpace);

  /**
   * Returns the enumeration constraint on the given data type.
   * <p>details key: "enumeration"
   */
  List<String> getEnumerationFacet(EDataType eDataType);

  /**
   * Sets the enumeration constraint on the given data type.
   * <p>details key: "enumeration"
   */
  void setEnumerationFacet(EDataType eDataType, List<String> literals);

  /**
   * Returns the pattern constraint on the given data type.
   * <p>details key: "pattern"
   */
  List<String> getPatternFacet(EDataType eDataType);

  /**
   * Sets the pattern constraint on the given data type.
   * <p>details key: "pattern"
   */
  void setPatternFacet(EDataType eDataType, List<String> pattern);

  /**
   * Returns the total digits constraint on the given data type.
   * <p>details key: "totalDigits"
   */
  int getTotalDigitsFacet(EDataType eDataType);

  /**
   * Sets the total digits constraint on the given data type.
   * <p>details key: "totalDigits"
   */
  void setTotalDigitsFacet(EDataType eDataType, int digits);

  /**
   * Returns the fraction digits constraint on the given data type.
   * <p>details key: "fractionDigits"
   */
  int getFractionDigitsFacet(EDataType eDataType);

  /**
   * Sets the fraction digits constraint on the given data type.
   * <p>details key: "fractionDigits"
   */
  void setFractionDigitsFacet(EDataType eDataType, int digits);

  /**
   * Returns the length constraint on the given data type.
   * <p>details key: "length"
   */
  int getLengthFacet(EDataType eDataType);

  /**
   * Sets the length constraint on the given data type.
   * <p>details key: "length"
   */
  void setLengthFacet(EDataType eDataType, int length);

  /**
   * Returns the minumum length constraint on the given data type.
   * <p>details key: "minLength"
   */
  int getMinLengthFacet(EDataType eDataType);

  /**
   * Sets the minimum length constraint on the given data type.
   * <p>details key: "minLength"
   */
  void setMinLengthFacet(EDataType eDataType, int length);

  /**
   * Returns the maximum length constraint on the given data type.
   * <p>details key: "maxLength"
   */
  int getMaxLengthFacet(EDataType eDataType);

  /**
   * Sets the maximum length constraint on the given data type.
   * <p>details key: "maxLength"
   */
  void setMaxLengthFacet(EDataType eDataType, int length);

  /**
   * Returns the minimum (exclusive) constraint on the given data type.
   * <p>details key: "minExclusive"
   */
  String getMinExclusiveFacet(EDataType eDataType);

  /**
   * Sets the minimum (exclusive) constraint on the given data type.
   * <p>details key: "minExclusive"
   */
  void setMinExclusiveFacet(EDataType eDataType, String literal);

  /**
   * Returns the maximum (exclusive) constraint on the given data type.
   * <p>details key: "maxExclusive"
   */
  String getMaxExclusiveFacet(EDataType eDataType);

  /**
   * Sets the maximum (exclusive) constraint on the given data type.
   * <p>details key: "maxExclusive"
   */
  void setMaxExclusiveFacet(EDataType eDataType, String literal);

  /**
   * Returns the minimum (inclusive) constraint on the given data type.
   * <p>details key: "minInclusive"
   */
  String getMinInclusiveFacet(EDataType eDataType);

  /**
   * Sets the minimum (inclusive) constraint on the given data type.
   * <p>details key: "minInclusive"
   */
  void setMinInclusiveFacet(EDataType eDataType, String literal);

  /**
   * Returns the maximum (inclusive) constraint on the given data type.
   * <p>details key: "maxInclusive"
   */
  String getMaxInclusiveFacet(EDataType eDataType);

  /**
   * Sets the maximum (inclusive) constraint on the given data type.
   * <p>details key: "maxInclusive"
   */
  void setMaxInclusiveFacet(EDataType eDataType, String literal);

  /**
   * Returns a package from the demand package registry, creating it (with a document root class) if necessary.
   */
  EPackage demandPackage(String namespace);

  /**
   * Returns a class from the namespace-specified package in the demand package registry, demand creating it, and the
   * package, if necessary.
   */
  EClassifier demandType(String namespace, String name);

  /**
   * Returns a feature corresponding to a global element or attribute from the namespace-specified package in the
   * demand package registry, creating it, and the package, if necessary. Elements are created as references and
   * attributes as attributes.
   */
  EStructuralFeature demandFeature(String namespace, String name, boolean isElement);

  /**
   * Returns a feature corresponding to a global element or attribute from the namespace-specified package in the
   * demand package registry, creating it, and the package, if necessary.
   */
  EStructuralFeature demandFeature(String namespace, String name, boolean isElement, boolean isReference);

  /**
   * Returns the instance's collection of demand-created packages.
   */
  Collection<EPackage> demandedPackages();

  /**
   * The default ExtendedMetaData instance, which obtains packages from the global package registry, {@link
   * org.eclipse.emf.ecore.EPackage.Registry#INSTANCE EPackage.Registry.INSTANCE}.
   */
  ExtendedMetaData INSTANCE = new BasicExtendedMetaData();
}
