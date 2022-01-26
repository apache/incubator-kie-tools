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
package org.eclipse.emf.ecore.impl;


import com.google.gwt.user.client.rpc.GwtTransient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.InvocationTargetException;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.xml.type.util.XMLTypeUtil;
import org.gwtproject.i18n.shared.DateTimeFormat;
import org.gwtproject.regexp.shared.RegExp;
import org.gwtproject.regexp.shared.SplitResult;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EFactory</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.impl.EFactoryImpl#getEPackage <em>EPackage</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EFactoryImpl extends EModelElementImpl implements EFactory
{
  /**
   * The cached value of the '{@link #getEPackage() <em>EPackage</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEPackage()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EPackage ePackage;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EFactoryImpl()
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
    return EcorePackage.Literals.EFACTORY;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EPackage getEPackage()
  {
    return ePackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEPackage(EPackage newEPackage)
  {
    if (newEPackage != ePackage)
    {
      NotificationChain msgs = null;
      if (ePackage != null)
        msgs = ((InternalEObject)ePackage).eInverseRemove(this, EcorePackage.EPACKAGE__EFACTORY_INSTANCE, EPackage.class, msgs);
      if (newEPackage != null)
        msgs = ((InternalEObject)newEPackage).eInverseAdd(this, EcorePackage.EPACKAGE__EFACTORY_INSTANCE, EPackage.class, msgs);
      msgs = basicSetEPackage(newEPackage, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.EFACTORY__EPACKAGE, newEPackage, newEPackage));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetEPackage(EPackage newEPackage, NotificationChain msgs)
  {
    EPackage oldEPackage = ePackage;
    ePackage = newEPackage;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EcorePackage.EFACTORY__EPACKAGE, oldEPackage, newEPackage);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
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
      case EcorePackage.EFACTORY__EANNOTATIONS:
        return getEAnnotations();
      case EcorePackage.EFACTORY__EPACKAGE:
        return getEPackage();
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
      case EcorePackage.EFACTORY__EANNOTATIONS:
        getEAnnotations().clear();
        getEAnnotations().addAll((Collection<? extends EAnnotation>)newValue);
        return;
      case EcorePackage.EFACTORY__EPACKAGE:
        setEPackage((EPackage)newValue);
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
      case EcorePackage.EFACTORY__EANNOTATIONS:
        getEAnnotations().clear();
        return;
      case EcorePackage.EFACTORY__EPACKAGE:
        setEPackage((EPackage)null);
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
      case EcorePackage.EFACTORY__EANNOTATIONS:
        return eAnnotations != null && !eAnnotations.isEmpty();
      case EcorePackage.EFACTORY__EPACKAGE:
        return ePackage != null;
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
      case EcorePackage.EFACTORY___GET_EANNOTATION__STRING:
        return getEAnnotation((String)arguments.get(0));
      case EcorePackage.EFACTORY___CREATE__ECLASS:
        return create((EClass)arguments.get(0));
      case EcorePackage.EFACTORY___CREATE_FROM_STRING__EDATATYPE_STRING:
        return createFromString((EDataType)arguments.get(0), (String)arguments.get(1));
      case EcorePackage.EFACTORY___CONVERT_TO_STRING__EDATATYPE_OBJECT:
        return convertToString((EDataType)arguments.get(0), arguments.get(1));
    }
    return eDynamicInvoke(operationID, arguments);
  }

  /**
   * @generated modifiable
   */
  public EObject create(EClass eClass) 
  {
    if (getEPackage() != eClass.getEPackage() || eClass.isAbstract())
    {
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }

    for (List<EClass> eSuperTypes = eClass.getESuperTypes(); !eSuperTypes.isEmpty(); )
    {
      EClass eSuperType = eSuperTypes.get(0);
      if (eSuperType.getInstanceClass() != null)
      {
        EObject result = eSuperType.getEPackage().getEFactoryInstance().create(eSuperType);
        ((InternalEObject)result).eSetClass(eClass);
        return result;
      }
      eSuperTypes = eSuperType.getESuperTypes();
    }

    return basicCreate(eClass);
  }

  protected EObject basicCreate(EClass eClass) 
  {
    return
       eClass.getInstanceClassName() == "java.util.Map$Entry" ?
         new DynamicEObjectImpl.BasicEMapEntry<String, String>(eClass) :
         new DynamicEObjectImpl(eClass);
  }

  /**
   * @generated NOT
   */
  public Object createFromString(EDataType eDataType, String stringValue) 
  {
    if (stringValue == null)
    {
      return null;
    }

    if (getEPackage() != eDataType.getEPackage())
    {
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }

    if (eDataType instanceof EEnum)
    {
      Object result = ((EEnum)eDataType).getEEnumLiteralByLiteral(stringValue);
      if (result == null)
      {
        throw new IllegalArgumentException("The value '" + stringValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
      }
      return result;
    }

    switch (ExtendedMetaData.INSTANCE.getWhiteSpaceFacet(eDataType))
    {
      case ExtendedMetaData.REPLACE_WHITE_SPACE:
      {
        stringValue = replaceWhiteSpace(stringValue);
        break;
      }
      case ExtendedMetaData.COLLAPSE_WHITE_SPACE:
      {
        stringValue = collapseWhiteSpace(stringValue);
        break;
      }
    }
    
    EDataType baseType = ExtendedMetaData.INSTANCE.getBaseType(eDataType);
    if (baseType != null)
    {
      return EcoreUtil.createFromString(baseType, stringValue);
    }

    EDataType itemType = ExtendedMetaData.INSTANCE.getItemType(eDataType);
    if (itemType != null)
    {
      List<Object> result = new ArrayList<Object>();
      for (String item : split(stringValue))
      {
        result.add(EcoreUtil.createFromString(itemType, item));
      }
      return result;
    }

    List<EDataType> memberTypes = ExtendedMetaData.INSTANCE.getMemberTypes(eDataType);
    if (!memberTypes.isEmpty())
    {
      for (EDataType memberType : memberTypes)
      {
        try
        {
          Object result = EcoreUtil.createFromString(memberType, stringValue);
          if (result != null)
          {
            return result;
          }
        }
        catch (RuntimeException exception)
        {
          // Keep trying until all else has failed.
        }
      }
      throw new IllegalArgumentException("The value '" + stringValue + "' does not match any member types of the union datatype '" + eDataType.getName() + "'");
    }

    EDataType.Internal.ConversionDelegate conversionDelegate = ((EDataType.Internal)eDataType).getConversionDelegate();
    if (conversionDelegate != null)
    {
      return conversionDelegate.createFromString(stringValue);
    }

    Class<?> c = EcoreUtil.wrapperClassFor(eDataType.getInstanceClass());
    if (c == null) return null;

    if (c == Character.class)
    {
      char charValue = 0;
      try
      {
        charValue = (char)Integer.parseInt(stringValue);
      }
      catch (NumberFormatException e)
      {
        char[] carray = stringValue.toCharArray();
        charValue = carray[0];
      }

      return charValue;
    }

    if (c == Date.class)
    {
      for (int i = 0; i < EDATE_FORMATS.length; ++i)
      {
        try
        {
          return EDATE_FORMATS[i].parse(stringValue);
        }
        catch (IllegalArgumentException parseException)
        {
          // Keep trying until all else has failed.
        }
      }
      throw new IllegalArgumentException("The value '" + stringValue + "' is not a date formatted string of the form yyyy-MM-dd'T'HH:mm:ss'.'SSSZ or a valid subset thereof");
    }

    /*
    Class<String> stringClass = String.class;
    Class<?>[] signature = { stringClass };

    Constructor<?> ctor = null;
    try
    {
      ctor = c.getConstructor(signature);
    }
    catch (NoSuchMethodException e)
    {
      // Continue to try a different approach.
    }
    Throwable formatException = null;
    try
    {
      if (ctor != null)
      {
        Object[] ctorArgs = {stringValue};
        return ctor.newInstance(ctorArgs);
      }
    }
    catch (InstantiationException e)
    {
      formatException = e;
    }
    catch (InvocationTargetException e)
    {
      formatException = e.getCause();
    }
    catch (IllegalAccessException e)
    {
      formatException = e;
    }
    
    Method valueOf = null;
    try
    {
      valueOf = c.getMethod("valueOf", signature);
    }
    catch (NoSuchMethodException e)
    {
      // Continue to try a different approach.
    }

    try
    {
      if (valueOf != null)
      {
        Object[] valueOfArgs = {stringValue};
        return valueOf.invoke(null, valueOfArgs);
      }
    }
    catch (IllegalArgumentException e)
    {
      formatException = e;
    }
    catch (InvocationTargetException e)
    {
      formatException = e.getCause();
    }
    catch (IllegalAccessException e)
    {
      formatException = e;
    }
    String exceptionString = formatException != null ? formatException.toString() : "";
    throw new IllegalArgumentException("The value '" + stringValue + "' is invalid. " + exceptionString, formatException);
    */
    throw new IllegalArgumentException("The value '" + stringValue + "' is invalid. ");
  }

  /**
   * @generated modifiable
   */
  public String convertToString(EDataType eDataType, Object objectValue)
  {
    if (getEPackage() != eDataType.getEPackage())
    {
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }

    EDataType baseType = ExtendedMetaData.INSTANCE.getBaseType(eDataType);
    if (baseType != null)
    {
      return EcoreUtil.convertToString(baseType, objectValue);
    }

    EDataType itemType = ExtendedMetaData.INSTANCE.getItemType(eDataType);
    if (itemType != null)
    {
      if (objectValue == null)
      {
        return null;
      }
      List<?> list = (List<?>)objectValue;
      if (list.isEmpty())
      {
        return "";
      }
      StringBuffer result = new StringBuffer();
      for (Object item : list)
      {
        result.append(EcoreUtil.convertToString(itemType, item));
        result.append(' ');
      }
      return result.substring(0, result.length() - 1);
    }

    List<EDataType> memberTypes = ExtendedMetaData.INSTANCE.getMemberTypes(eDataType);
    if (!memberTypes.isEmpty())
    {
      for (EDataType memberType : memberTypes)
      {
        if (memberType.isInstance(objectValue))
        {
          try
          {
            String result = EcoreUtil.convertToString(memberType, objectValue);
            if (result != null)
            {
              return result;
            }
          }
          catch (Exception e)
          {
            // Keep trying until all else false.
          }
        }
      }
      throw new IllegalArgumentException("Invalid value: '" + objectValue + "' for datatype :"+eDataType.getName());
    }

    EDataType.Internal.ConversionDelegate conversionDelegate = ((EDataType.Internal)eDataType).getConversionDelegate();
    if (conversionDelegate != null)
    {
      return conversionDelegate.convertToString(objectValue);
    }

    if (objectValue == null)
    {
      return null;
    }
    else if (objectValue instanceof Character)
    {
      return Integer.toString((Character)objectValue);
    }
    else if (objectValue.getClass() == Date.class)
    {
      return EDATE_FORMATS[0].format((Date)objectValue);
    }
    else
    {
      return objectValue.toString();
    }
  }
  
  byte[] hexStringToBytes(String initialValue)
  {
    if (initialValue == null)
    {
      return null;
    }

    int size = initialValue.length();
    int limit = (size + 1) / 2;
    byte [] result = new byte[limit];
    if (size % 2 != 0)
    {
      result[--limit] = hexCharToByte(initialValue.charAt(size - 1));
    }
    
    for (int i = 0, j = 0; i < limit; ++i)
    {
      byte high = hexCharToByte(initialValue.charAt(j++));
      byte low = hexCharToByte(initialValue.charAt(j++));
      result[i] = (byte)(high << 4 | low);
    }
    return result;
  }

  static byte hexCharToByte(char character)
  {
    switch (character)
    {
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
      {
        return (byte)(character - '0');
      }
      case 'a':
      case 'b':
      case 'c':
      case 'd':
      case 'e':
      case 'f':
      {
        return (byte)(character - 'a' + 10);
      }
      case 'A':
      case 'B':
      case 'C':
      case 'D':
      case 'E':
      case 'F':
      {
        return (byte)(character - 'A' + 10);
      }
      default:
      {
        throw new NumberFormatException("Invalid hexadecimal");
      }
    }
  }

  static final char [] HEX_DIGITS = 
    { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

  String bytesToHexString(byte [] bytes, int count)
  {
    if (bytes == null)
    {
      return null;
    }
    else
    {
      char [] result = new char[2 * count];
      for (int i = 0, j = 0; i < count; ++i)
      {
        int high = (bytes[i] >> 4) & 0xF;
        int low = bytes[i] & 0xF;
        result[j++] = HEX_DIGITS[high];
        result[j++] = HEX_DIGITS[low];
      }
      return new String(result);
    }
  }

  protected Object createFromString(String hexString)
  {
    if (hexString == null)
    {
      return null;
    }
    else
    {
      // TODO
      return null;
      /*
      byte [] byteValue = hexStringToBytes(hexString);
      ByteArrayInputStream bytes = new ByteArrayInputStream(byteValue);
      try
      {
        ObjectInputStream in = new ObjectInputStream(bytes);
        return in.readObject();
      }
      catch (IOException exception)
      {
        throw new RuntimeException(exception);
      }
      catch (ClassNotFoundException exception)
      {
        throw new RuntimeException(exception);
      }
      */
    }
  }

  protected String convertToString(Object instanceValue)
  {
    /*
    ByteArrayOutputStream bytes = 
      new ByteArrayOutputStream()
      {
        @Override
        public String toString()
        {
          return bytesToHexString(buf, count);
        }
      };
    try
    {
      ObjectOutputStream out = new ObjectOutputStream(bytes);
      out.writeObject(instanceValue);
      out.close();
    }
    catch (IOException exception)
    {
      throw new RuntimeException(exception);
    }
    return bytes.toString();
    */
    return instanceValue.toString();
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
      case EcorePackage.EFACTORY__EANNOTATIONS:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getEAnnotations()).basicAdd(otherEnd, msgs);
      case EcorePackage.EFACTORY__EPACKAGE:
        if (ePackage != null)
          msgs = ((InternalEObject)ePackage).eInverseRemove(this, EcorePackage.EPACKAGE__EFACTORY_INSTANCE, EPackage.class, msgs);
        return basicSetEPackage((EPackage)otherEnd, msgs);
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
      case EcorePackage.EFACTORY__EANNOTATIONS:
        return ((InternalEList<?>)getEAnnotations()).basicRemove(otherEnd, msgs);
      case EcorePackage.EFACTORY__EPACKAGE:
        return basicSetEPackage(null, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
  }

  protected String replaceWhiteSpace(String value)
  {
    return XMLTypeUtil.normalize(value, false);
  }

  protected String collapseWhiteSpace(String value)
  {
    return XMLTypeUtil.normalize(value, true);
  }

  private static final RegExp WHITE_SPACE = RegExp.compile("[ \t\n\r\f]+");

  protected String [] split(String value)
  {
    SplitResult split = WHITE_SPACE.split(value);
    int length = split.length();
    String [] result = new String [length];
    for (int i = 0; i < length; ++i)
    {
      result[i] = split.get(i);
    }
    return result;
  }

  public interface InternalEDateTimeFormat
  {
    Date parse(String value);
    String format(Date value);
  }

  public static InternalEDateTimeFormat [] EDATE_FORMATS;

  static 
  {
    // This only works on the client.
    //
    try
    {
      class ClientInternalEDateTimeFormat implements InternalEDateTimeFormat
      {
        DateTimeFormat dateTimeFormat;
        ClientInternalEDateTimeFormat(DateTimeFormat dateTimeFormat)
        {
          this.dateTimeFormat = dateTimeFormat;
        }

        public Date parse(String value)
        {
          return dateTimeFormat.parse(value);
        }
        public String format(Date value)
        {
          return dateTimeFormat.format(value);
        }
      }
      EDATE_FORMATS =
        new InternalEDateTimeFormat[] 
        {
          new ClientInternalEDateTimeFormat(DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSSZ")),
          new ClientInternalEDateTimeFormat(DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSS")),
          new ClientInternalEDateTimeFormat(DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss")),
          new ClientInternalEDateTimeFormat(DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm")),
          new ClientInternalEDateTimeFormat(DateTimeFormat.getFormat("yyyy-MM-dd"))
        };
    }
    catch (Throwable exception)
    {
      // Ignore.
    }
  }
}
