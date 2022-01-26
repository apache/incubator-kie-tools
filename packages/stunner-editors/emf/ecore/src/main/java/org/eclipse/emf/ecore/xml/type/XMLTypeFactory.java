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
package org.eclipse.emf.ecore.xml.type;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.ecore.xml.type.XMLTypePackage
 * @generated
 */
public interface XMLTypeFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  XMLTypeFactory eINSTANCE = org.eclipse.emf.ecore.xml.type.impl.XMLTypeFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Any Type</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Any Type</em>'.
   * @generated
   */
  AnyType createAnyType();

  /**
   * Returns a new object of class '<em>Processing Instruction</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Processing Instruction</em>'.
   * @generated
   */
  ProcessingInstruction createProcessingInstruction();

  /**
   * Returns a new object of class '<em>Simple Any Type</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Simple Any Type</em>'.
   * @generated
   */
  SimpleAnyType createSimpleAnyType();

  /**
   * Returns a new object of class '<em>Document Root</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Document Root</em>'.
   * @generated
   */
  XMLTypeDocumentRoot createXMLTypeDocumentRoot();

  /**
   * Returns an instance of data type '<em>Any Simple Type</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  Object createAnySimpleType(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Any Simple Type</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertAnySimpleType(Object instanceValue);

  /**
   * Returns an instance of data type '<em>Any URI</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createAnyURI(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Any URI</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertAnyURI(String instanceValue);

  /**
   * Returns an instance of data type '<em>Base64 Binary</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  byte[] createBase64Binary(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Base64 Binary</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertBase64Binary(byte[] instanceValue);

  /**
   * Returns an instance of data type '<em>Boolean</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  boolean createBoolean(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Boolean</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertBoolean(boolean instanceValue);

  /**
   * Returns an instance of data type '<em>Boolean Object</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  Boolean createBooleanObject(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Boolean Object</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertBooleanObject(Boolean instanceValue);

  /**
   * Returns an instance of data type '<em>Byte</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  byte createByte(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Byte</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertByte(byte instanceValue);

  /**
   * Returns an instance of data type '<em>Byte Object</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  Byte createByteObject(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Byte Object</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertByteObject(Byte instanceValue);

  /**
   * Returns an instance of data type '<em>Date</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createDate(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Date</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertDate(String instanceValue);

  /**
   * Returns an instance of data type '<em>Date Time</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createDateTime(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Date Time</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertDateTime(String instanceValue);

  /**
   * Returns an instance of data type '<em>Decimal</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createDecimal(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Decimal</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertDecimal(String instanceValue);

  /**
   * Returns an instance of data type '<em>Double</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  double createDouble(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Double</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertDouble(double instanceValue);

  /**
   * Returns an instance of data type '<em>Double Object</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  Double createDoubleObject(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Double Object</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertDoubleObject(Double instanceValue);

  /**
   * Returns an instance of data type '<em>Duration</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createDuration(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Duration</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertDuration(String instanceValue);

  /**
   * Returns an instance of data type '<em>ENTITIES</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  List<String> createENTITIES(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>ENTITIES</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertENTITIES(List<? extends String> instanceValue);

  /**
   * Returns an instance of data type '<em>ENTITIES Base</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  List<String> createENTITIESBase(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>ENTITIES Base</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertENTITIESBase(List<? extends String> instanceValue);

  /**
   * Returns an instance of data type '<em>ENTITY</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createENTITY(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>ENTITY</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertENTITY(String instanceValue);

  /**
   * Returns an instance of data type '<em>Float</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  float createFloat(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Float</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertFloat(float instanceValue);

  /**
   * Returns an instance of data type '<em>Float Object</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  Float createFloatObject(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Float Object</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertFloatObject(Float instanceValue);

  /**
   * Returns an instance of data type '<em>GDay</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createGDay(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>GDay</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertGDay(String instanceValue);

  /**
   * Returns an instance of data type '<em>GMonth</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createGMonth(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>GMonth</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertGMonth(String instanceValue);

  /**
   * Returns an instance of data type '<em>GMonth Day</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createGMonthDay(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>GMonth Day</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertGMonthDay(String instanceValue);

  /**
   * Returns an instance of data type '<em>GYear</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createGYear(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>GYear</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertGYear(String instanceValue);

  /**
   * Returns an instance of data type '<em>GYear Month</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createGYearMonth(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>GYear Month</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertGYearMonth(String instanceValue);

  /**
   * Returns an instance of data type '<em>Hex Binary</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  byte[] createHexBinary(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Hex Binary</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertHexBinary(byte[] instanceValue);

  /**
   * Returns an instance of data type '<em>ID</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createID(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>ID</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertID(String instanceValue);

  /**
   * Returns an instance of data type '<em>IDREF</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createIDREF(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>IDREF</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertIDREF(String instanceValue);

  /**
   * Returns an instance of data type '<em>IDREFS</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  List<String> createIDREFS(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>IDREFS</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertIDREFS(List<? extends String> instanceValue);

  /**
   * Returns an instance of data type '<em>IDREFS Base</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  List<String> createIDREFSBase(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>IDREFS Base</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertIDREFSBase(List<? extends String> instanceValue);

  /**
   * Returns an instance of data type '<em>Int</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  int createInt(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Int</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertInt(int instanceValue);

  /**
   * Returns an instance of data type '<em>Integer</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createInteger(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Integer</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertInteger(String instanceValue);

  /**
   * Returns an instance of data type '<em>Int Object</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  Integer createIntObject(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Int Object</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertIntObject(Integer instanceValue);

  /**
   * Returns an instance of data type '<em>Language</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createLanguage(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Language</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertLanguage(String instanceValue);

  /**
   * Returns an instance of data type '<em>Long</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  long createLong(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Long</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertLong(long instanceValue);

  /**
   * Returns an instance of data type '<em>Long Object</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  Long createLongObject(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Long Object</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertLongObject(Long instanceValue);

  /**
   * Returns an instance of data type '<em>Name</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createName(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Name</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertName(String instanceValue);

  /**
   * Returns an instance of data type '<em>NC Name</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createNCName(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>NC Name</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertNCName(String instanceValue);

  /**
   * Returns an instance of data type '<em>Negative Integer</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createNegativeInteger(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Negative Integer</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertNegativeInteger(String instanceValue);

  /**
   * Returns an instance of data type '<em>NMTOKEN</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createNMTOKEN(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>NMTOKEN</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertNMTOKEN(String instanceValue);

  /**
   * Returns an instance of data type '<em>NMTOKENS</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  List<String> createNMTOKENS(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>NMTOKENS</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertNMTOKENS(List<? extends String> instanceValue);

  /**
   * Returns an instance of data type '<em>NMTOKENS Base</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  List<String> createNMTOKENSBase(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>NMTOKENS Base</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertNMTOKENSBase(List<? extends String> instanceValue);

  /**
   * Returns an instance of data type '<em>Non Negative Integer</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createNonNegativeInteger(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Non Negative Integer</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertNonNegativeInteger(String instanceValue);

  /**
   * Returns an instance of data type '<em>Non Positive Integer</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createNonPositiveInteger(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Non Positive Integer</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertNonPositiveInteger(String instanceValue);

  /**
   * Returns an instance of data type '<em>Normalized String</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createNormalizedString(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Normalized String</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertNormalizedString(String instanceValue);

  /**
   * Returns an instance of data type '<em>NOTATION</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createNOTATION(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>NOTATION</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertNOTATION(String instanceValue);

  /**
   * Returns an instance of data type '<em>Positive Integer</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createPositiveInteger(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Positive Integer</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertPositiveInteger(String instanceValue);

  /**
   * Returns an instance of data type '<em>QName</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * The literal must be of the form "prefix:localPart" where the "prefix:" is optional.
   * It's expected that this method will be used only to deserialize literals produced by {@link #convertQName(QName)}
   * and that subsequent processing to resolve the prefix will create a new QName that specifies the namespace URI, local part, and prefix.
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createQName(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>QName</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertQName(String instanceValue);

  /**
   * Returns an instance of data type '<em>Short</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  short createShort(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Short</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertShort(short instanceValue);

  /**
   * Returns an instance of data type '<em>Short Object</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  Short createShortObject(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Short Object</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertShortObject(Short instanceValue);

  /**
   * Returns an instance of data type '<em>String</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createString(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>String</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertString(String instanceValue);

  /**
   * Returns an instance of data type '<em>Time</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createTime(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Time</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertTime(String instanceValue);

  /**
   * Returns an instance of data type '<em>Token</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createToken(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Token</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertToken(String instanceValue);

  /**
   * Returns an instance of data type '<em>Unsigned Byte</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  short createUnsignedByte(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Unsigned Byte</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertUnsignedByte(short instanceValue);

  /**
   * Returns an instance of data type '<em>Unsigned Byte Object</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  Short createUnsignedByteObject(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Unsigned Byte Object</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertUnsignedByteObject(Short instanceValue);

  /**
   * Returns an instance of data type '<em>Unsigned Int</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  long createUnsignedInt(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Unsigned Int</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertUnsignedInt(long instanceValue);

  /**
   * Returns an instance of data type '<em>Unsigned Int Object</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  Long createUnsignedIntObject(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Unsigned Int Object</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertUnsignedIntObject(Long instanceValue);

  /**
   * Returns an instance of data type '<em>Unsigned Long</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  String createUnsignedLong(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Unsigned Long</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertUnsignedLong(String instanceValue);

  /**
   * Returns an instance of data type '<em>Unsigned Short</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  int createUnsignedShort(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Unsigned Short</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertUnsignedShort(int instanceValue);

  /**
   * Returns an instance of data type '<em>Unsigned Short Object</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  Integer createUnsignedShortObject(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Unsigned Short Object</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertUnsignedShortObject(Integer instanceValue);

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  XMLTypePackage getXMLTypePackage();

} //XMLTypeFactory
