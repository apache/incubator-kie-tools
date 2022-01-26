/**
 * Copyright (c) 2007-2012 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore.resource.impl;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.ecore.util.InternalEList;


/**
 * An API for efficiently producing and consuming a compact binary serialization that's suitable for long term storage.
 * @since 2.4
 */
public class BinaryResourceImpl extends ResourceImpl
{
  /**
   * A save option to specify the {@link Version} to be used for the serialization.
   * @see Version
   * @since 2.7
   */
  public static final String OPTION_VERSION = "VERSION";

  /**
   * A Boolean save option to specify whether float and double values
   * are encoded using {@link Float#floatToIntBits(float)} and {@link Double#doubleToLongBits(double)} respectively,
   * rather than a string representation.
   * The default is false because GWT's client doesn't support this method.
   * This style option is only supported for serializations with {@link Version#VERSION_1_1 version 1.1} or higher.
   * @see BinaryIO#STYLE_BINARY_FLOATING_POINT
   * @since 2.7
   */
  public static final String OPTION_STYLE_BINARY_FLOATING_POINT = "BINARY_FLOATING_POINT ";

  /**
   * A Boolean save option to specify whether {@link Date date} values will be serialized using {@link Date#getTime()} rather than a string representation.
   * This style option is only supported for serializations with {@link Version#VERSION_1_1 version 1.1} or higher.
   * The default is false.
   * @see BinaryIO#STYLE_BINARY_DATE
   * @since 2.7
   */
  public static final String OPTION_STYLE_BINARY_DATE = "BINARY_DATE";

  /**
   * A Boolean save option to specify whether serialized proxies will include the serialization of their attribute values.
   * This style option is only supported for serializations with {@link Version#VERSION_1_1 version 1.1} or higher.
   * The default is false.
   * @see BinaryIO#STYLE_PROXY_ATTRIBUTES
   * @since 2.7
   */
  public static final String OPTION_STYLE_PROXY_ATTRIBUTES = "PROXY_ATTRIBUTES";

  /**
   * A Boolean save option to specify whether {@link Enumerator enumerator} values will be serialized using {@link Enumerator#getValue()} rather than a string representation.
   * This style option is only supported for serializations with {@link Version#VERSION_1_1 version 1.1} or higher.
   * The default is false.
   * @see BinaryIO#STYLE_BINARY_ENUMERATOR
   * @since 2.8
   */
  public static final String OPTION_STYLE_BINARY_ENUMERATOR = "BINARY_ENUMERATOR";

  /**
   * Specify the capacity of the buffered stream
   * used when {@link #doSave(OutputStream, Map) saving} or {@link #doLoad(InputStream, Map) loading} the resource content.
   * The value must be an integer.
   * If not specified, {@link #DEFAULT_BUFFER_CAPACITY} is used.
   * A value less than one disables the cache.
   * @since 2.6
   */
  public static final String OPTION_BUFFER_CAPACITY = "BUFFER_CAPACITY";

  /**
   * The default {@link #OPTION_BUFFER_CAPACITY} capacity of the buffered stream
   * used when {@link #doSave(OutputStream, Map) saving} or {@link #doLoad(InputStream, Map) loading} the resource content.
   * @since 2.6
   */
  public static final int DEFAULT_BUFFER_CAPACITY = 1024;

  /**
   * Extract the {@link #OPTION_BUFFER_CAPACITY} from the options.
   * @param options a map of options.
   * @return the value associated with the {@link #OPTION_BUFFER_CAPACITY} key in the options map.
   * @since 2.6
   */
  public static int getBufferCapacity(Map<?, ?> options)
  {
    if (options != null)
    {
      Integer capacity = (Integer)options.get(OPTION_BUFFER_CAPACITY);
      if (capacity != null)
      {
        return capacity;
      }
    }
    return DEFAULT_BUFFER_CAPACITY;
  }

  public BinaryResourceImpl()
  {
    super();
  }

  public BinaryResourceImpl(URI uri)
  {
    super(uri);
  }

  @Override
  protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException
  {
    if (outputStream instanceof URIConverter.Saveable)
    {
      ((URIConverter.Saveable)outputStream).saveResource(this);
    }
    else
    {
      EObjectOutputStream eObjectOutputStream = new EObjectOutputStream(outputStream, options);
      eObjectOutputStream.saveResource(this);
    }
  }

  @Override
  protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException
  {
    if (inputStream instanceof URIConverter.Loadable)
    {
      ((URIConverter.Loadable)inputStream).loadResource(this);
    }
    else
    {
      EObjectInputStream eObjectInputStream = new EObjectInputStream(inputStream, options);
      eObjectInputStream.loadResource(this);
    }
  }

  public static class BinaryIO
  {
    public enum Version
    {
      VERSION_1_0,

      /**
       * This version supports styles.
       * An extra integer value encoding the style is written after the version number so that deserialization will respect the styles used during serialization.
       * @since 2.7
       */
      VERSION_1_1
    }

    /**
     * @see BinaryResourceImpl#OPTION_STYLE_BINARY_FLOATING_POINT
     * @since 2.7
     */
    public static final int STYLE_BINARY_FLOATING_POINT = 0x1;

    /**
     * @see BinaryResourceImpl#OPTION_STYLE_BINARY_DATE
     * @since 2.7
     */
    public static final int STYLE_BINARY_DATE = 0x2;

    /**
     * @see BinaryResourceImpl#OPTION_STYLE_PROXY_ATTRIBUTES
     * @since 2.7
     */
    public static final int STYLE_PROXY_ATTRIBUTES = 0x4;

    /**
     * @see BinaryResourceImpl#OPTION_STYLE_BINARY_ENUMERATOR
     * @since 2.8
     */
    public static final int STYLE_BINARY_ENUMERATOR = 0x8;

    protected Version version;

    /**
     * @since 2.7
     */
    protected int style;
    protected Resource resource;
    protected URI baseURI;
    protected Map<?, ?> options;
    protected char[] characters;
    protected InternalEObject[][] internalEObjectDataArrayBuffer = new InternalEObject[50][];
    protected int internalEObjectDataArrayBufferCount = -1;

    protected static int getStyle(Map<?, ?> options)
    {
      int result = 0;
      if (options != null)
      {
        if (Boolean.TRUE.equals(options.get(OPTION_STYLE_BINARY_FLOATING_POINT)))
        {
          result |= STYLE_BINARY_FLOATING_POINT;
        }
        if (Boolean.TRUE.equals(options.get(OPTION_STYLE_BINARY_DATE)))
        {
          result |= STYLE_BINARY_DATE;
        }
        if (Boolean.TRUE.equals(options.get(OPTION_STYLE_PROXY_ATTRIBUTES)))
        {
          result |= STYLE_PROXY_ATTRIBUTES;
        }
        if (Boolean.TRUE.equals(options.get(OPTION_STYLE_BINARY_ENUMERATOR)))
        {
          result |= STYLE_BINARY_ENUMERATOR;
        }
      }
      return result;
    }

    protected URI resolve(URI uri)
    {
      return baseURI != null && uri.isRelative() && uri.hasRelativePath() ? uri.resolve(baseURI) : uri;
    }

    protected URI deresolve(URI uri)
    {
      if (baseURI != null && !uri.isRelative())
      {
        URI deresolvedURI = uri.deresolve(baseURI, true, true, false);
        if (deresolvedURI.hasRelativePath() && (!uri.isPlatform() || uri.segment(0).equals(baseURI.segment(0))))
        {
          uri = deresolvedURI;
        }
      }
      return uri;
    }

    protected InternalEObject [] allocateInternalEObjectArray(int length)
    {
      if (internalEObjectDataArrayBufferCount == -1)
      {
        return new InternalEObject[length];
      }
      else
      {
        InternalEObject [] buffer = internalEObjectDataArrayBuffer[internalEObjectDataArrayBufferCount];
        internalEObjectDataArrayBuffer[internalEObjectDataArrayBufferCount--] = null;
        return buffer.length >= length ? buffer :  new InternalEObject[length];
      }
    }

    protected void recycle(InternalEObject[] values)
    {
      if (++internalEObjectDataArrayBufferCount >= internalEObjectDataArrayBuffer.length)
      {
        InternalEObject [][] newInternalEObjectDataArrayBuffer = new InternalEObject[internalEObjectDataArrayBufferCount * 2][];
        System.arraycopy(internalEObjectDataArrayBuffer, 0, newInternalEObjectDataArrayBuffer, 0, internalEObjectDataArrayBufferCount);
        internalEObjectDataArrayBuffer = newInternalEObjectDataArrayBuffer;
      }
      internalEObjectDataArrayBuffer[internalEObjectDataArrayBufferCount] = values;
    }

    protected FeatureMap.Entry.Internal[][] featureMapEntryDataArrayBuffer = new FeatureMap.Entry.Internal[50][];
    protected int featureMapEntryDataArrayBufferCount = -1;

    protected FeatureMap.Entry.Internal [] allocateFeatureMapEntryArray(int length)
    {
      if (featureMapEntryDataArrayBufferCount == -1)
      {
        return new FeatureMap.Entry.Internal[length];
      }
      else
      {
        FeatureMap.Entry.Internal [] buffer = featureMapEntryDataArrayBuffer[featureMapEntryDataArrayBufferCount];
        featureMapEntryDataArrayBuffer[featureMapEntryDataArrayBufferCount--] = null;
        return buffer.length >= length ? buffer :  new FeatureMap.Entry.Internal[length];
      }
    }

    protected void recycle(FeatureMap.Entry.Internal[] values)
    {
      if (++featureMapEntryDataArrayBufferCount >= featureMapEntryDataArrayBuffer.length)
      {
        FeatureMap.Entry.Internal [][] newFeatureMapEntryDataArrayBuffer = new FeatureMap.Entry.Internal[featureMapEntryDataArrayBufferCount * 2][];
        System.arraycopy(featureMapEntryDataArrayBuffer, 0, newFeatureMapEntryDataArrayBuffer, 0, featureMapEntryDataArrayBufferCount);
        featureMapEntryDataArrayBuffer = newFeatureMapEntryDataArrayBuffer;
      }
      featureMapEntryDataArrayBuffer[featureMapEntryDataArrayBufferCount] = values;
    }

    protected enum FeatureKind
    {
      EOBJECT_CONTAINER,
      EOBJECT_CONTAINER_PROXY_RESOLVING,

      EOBJECT,
      EOBJECT_PROXY_RESOLVING,

      EOBJECT_LIST,
      EOBJECT_LIST_PROXY_RESOLVING,

      EOBJECT_CONTAINMENT,
      EOBJECT_CONTAINMENT_PROXY_RESOLVING,

      EOBJECT_CONTAINMENT_LIST,
      EOBJECT_CONTAINMENT_LIST_PROXY_RESOLVING,

      BOOLEAN,
      BYTE,
      CHAR,
      DOUBLE,
      FLOAT,
      INT,
      LONG,
      SHORT,
      STRING,

      /**
       * @since 2.7
       */
      DATE,

      /**
       * @since 2.8
       */
      ENUMERATOR,

      DATA,
      DATA_LIST,

      FEATURE_MAP;

      public static FeatureKind get(EStructuralFeature eStructuralFeature)
      {
        if (eStructuralFeature instanceof EReference)
        {
          EReference eReference = (EReference)eStructuralFeature;
          if (eReference.isContainment())
          {
            if (eReference.isResolveProxies())
            {
              if (eReference.isMany())
              {
                return EOBJECT_CONTAINMENT_LIST_PROXY_RESOLVING;
              }
              else
              {
                return EOBJECT_CONTAINMENT_PROXY_RESOLVING;
              }
            }
            else
            {
              if (eReference.isMany())
              {
                return EOBJECT_CONTAINMENT_LIST;
              }
              else
              {
                return EOBJECT_CONTAINMENT;
              }
            }
          }
          else if (eReference.isContainer())
          {
            if (eReference.isResolveProxies())
            {
              return EOBJECT_CONTAINER_PROXY_RESOLVING;
            }
            else
            {
              return EOBJECT_CONTAINER;
            }
          }
          else if (eReference.isResolveProxies())
          {
            if (eReference.isMany())
            {
              return EOBJECT_LIST_PROXY_RESOLVING;
            }
            else
            {
              return EOBJECT_PROXY_RESOLVING;
            }
          }
          else
          {
            if (eReference.isMany())
            {
              return EOBJECT_LIST;
            }
            else
            {
              return EOBJECT;
            }
          }
        }
        else
        {
          EAttribute eAttribute = (EAttribute)eStructuralFeature;
          EDataType eDataType = eAttribute.getEAttributeType();
          String instanceClassName = eDataType.getInstanceClassName();
          if (instanceClassName == "org.eclipse.emf.ecore.util.FeatureMap$Entry")
          {
            return FEATURE_MAP;
          }
          else if (eAttribute.isMany())
          {
            return DATA_LIST;
          }
          else if (instanceClassName == "java.lang.String")
          {
            return STRING;
          }
          else if (instanceClassName == "boolean")
          {
            return BOOLEAN;
          }
          else if (instanceClassName == "byte")
          {
            return BYTE;
          }
          else if (instanceClassName == "char")
          {
            return CHAR;
          }
          else if (instanceClassName == "double")
          {
            return DOUBLE;
          }
          else if (instanceClassName == "float")
          {
            return FLOAT;
          }
          else if (instanceClassName == "int")
          {
            return INT;
          }
          else if (instanceClassName == "long")
          {
            return LONG;
          }
          else if (instanceClassName == "short")
          {
            return SHORT;
          }
          else if (instanceClassName == "java.util.Date")
          {
            return DATE;
          }
          else if (eDataType instanceof EEnum)
          {
            return ENUMERATOR;
          }
          else
          {
            return DATA;
          }
        }
      }
    }
  }

  public static class EObjectOutputStream extends BinaryIO
  {
    public enum Check
    {
      NOTHING,
      DIRECT_RESOURCE,
      RESOURCE,
      CONTAINER
    }

    protected static class EPackageData
    {
      public int id;
      public EClassData[] eClassData;

      public final int allocateEClassID()
      {
        for (int i = 0, length = eClassData.length; i < length; ++i)
        {
          EClassData eClassData = this.eClassData[i];
          if (eClassData == null)
          {
            return i;
          }
        }
        return -1;
      }
    }

    protected static class EClassData
    {
      public int ePackageID;
      public int id;
      public EStructuralFeatureData[] eStructuralFeatureData;
    }

    protected static class EStructuralFeatureData
    {
      public String name;
      public boolean isTransient;
      /**
       * @since 2.7
       */
      public boolean isProxyTransient;
      public FeatureKind kind;
      public EFactory eFactory;
      public EDataType eDataType;
    }

    protected OutputStream outputStream;
    protected Map<EPackage, EPackageData> ePackageDataMap = new HashMap<EPackage, EPackageData>();
    protected Map<EClass, EClassData> eClassDataMap = new HashMap<EClass, EClassData>();
    protected Map<EObject, Integer> eObjectIDMap = new HashMap<EObject, Integer>();
    protected Map<URI, Integer> uriToIDMap = new HashMap<URI, Integer>();

    public EObjectOutputStream(OutputStream outputStream, Map<?, ?> options) throws IOException
    {
      this(outputStream, options, options != null && options.containsKey(OPTION_VERSION)? (Version)options.get(OPTION_VERSION) : Version.VERSION_1_1);
    }

    public EObjectOutputStream(OutputStream outputStream, Map<?, ?> options, Version version) throws IOException
    {
      this(outputStream, options, version, version.ordinal() > 0 ? getStyle(options) : STYLE_BINARY_FLOATING_POINT);
    }

    /**
     * @since 2.7
     */
    public EObjectOutputStream(OutputStream outputStream, Map<?, ?> options, Version version, int style) throws IOException
    {
      this.outputStream = outputStream;
      this.options = options;
      this.version = version;
      this.style = style;
      writeSignature();
      writeVersion();
      if (version.ordinal() > 0)
      {
        writeStyle();
      }
    }

    protected void writeSignature() throws IOException
    {
      // Write a signature that will be obviously corrupt
      // if the binary contents end up being UTF-8 encoded
      // or altered by line feed or carriage return changes.
      //
      writeByte('\211');
      writeByte('e');
      writeByte('m');
      writeByte('f');
      writeByte('\n');
      writeByte('\r');
      writeByte('\032');
      writeByte('\n');
    }

    protected void writeVersion() throws IOException
    {
      writeByte(version.ordinal());
    }

    /**
     * @since 2.7
     */
    protected void writeStyle() throws IOException
    {
      writeInt(style);
    }

    protected EPackageData writeEPackage(EPackage ePackage) throws IOException
    {
      EPackageData ePackageData = ePackageDataMap.get(ePackage);
      if (ePackageData == null)
      {
        ePackageData = new EPackageData();
        int id = ePackageDataMap.size();
        ePackageData.id = id;
        ePackageData.eClassData = new EClassData[ePackage.getEClassifiers().size()];
        writeCompressedInt(id);
        writeString(ePackage.getNsURI());
        writeURI(EcoreUtil.getURI(ePackage));
        ePackageDataMap.put(ePackage, ePackageData);
      }
      else
      {
        writeCompressedInt(ePackageData.id);
      }
      return ePackageData;
    }

    protected EClassData writeEClass(EClass eClass) throws IOException
    {
      EClassData eClassData = eClassDataMap.get(eClass);
      if (eClassData == null)
      {
        eClassData = new EClassData();
        EPackageData ePackageData = writeEPackage(eClass.getEPackage());
        eClassData.ePackageID = ePackageData.id;
        writeCompressedInt(eClassData.id = ePackageData.allocateEClassID());
        writeString(eClass.getName());
        int featureCount = eClass.getFeatureCount();
        EStructuralFeatureData [] eStructuralFeaturesData = eClassData.eStructuralFeatureData = new EStructuralFeatureData[featureCount];
        for (int i = 0; i < featureCount; ++i)
        {
          EStructuralFeatureData eStructuralFeatureData = eStructuralFeaturesData[i] = new EStructuralFeatureData();
          EStructuralFeature.Internal eStructuralFeature = (EStructuralFeature.Internal)eClass.getEStructuralFeature(i);
          eStructuralFeatureData.name = eStructuralFeature.getName();
          eStructuralFeatureData.isTransient = eStructuralFeature.isTransient() || eStructuralFeature.isContainer() && !eStructuralFeature.isResolveProxies();
          eStructuralFeatureData.kind = FeatureKind.get(eStructuralFeature);
          if (eStructuralFeature instanceof EAttribute)
          {
            EAttribute eAttribute = (EAttribute)eStructuralFeature;
            EDataType eDataType = eAttribute.getEAttributeType();
            eStructuralFeatureData.eDataType = eDataType;
            eStructuralFeatureData.eFactory = eDataType.getEPackage().getEFactoryInstance();
            eStructuralFeatureData.isProxyTransient = eStructuralFeatureData.kind == FeatureKind.FEATURE_MAP;
          }
          else
          {
            eStructuralFeatureData.isProxyTransient = true;
          }
        }
        ePackageData.eClassData[eClassData.id] = eClassData;
        eClassDataMap.put(eClass, eClassData);
      }
      else
      {
        writeCompressedInt(eClassData.ePackageID);
        writeCompressedInt(eClassData.id);
      }
      return eClassData;
    }

    protected EStructuralFeatureData writeEStructuralFeature(EStructuralFeature eStructuralFeature) throws IOException
    {
      EClass eClass = eStructuralFeature.getEContainingClass();
      EClassData eClassData = writeEClass(eClass);
      int featureID = eClass.getFeatureID(eStructuralFeature);
      EStructuralFeatureData eStructuralFeatureData = eClassData.eStructuralFeatureData[featureID];
      writeCompressedInt(featureID);
      if (eStructuralFeatureData.name != null)
      {
        writeString(eStructuralFeatureData.name);
        eStructuralFeatureData.name = null;
      }
      return eStructuralFeatureData;
    }

    public void saveResource(Resource resource) throws IOException
    {
      this.resource = resource;
      URI uri = resource.getURI();
      if (uri != null && uri.isHierarchical() && !uri.isRelative())
      {
        baseURI = uri;
      }
      @SuppressWarnings("unchecked")
      InternalEList<? extends InternalEObject> internalEList = (InternalEList<? extends InternalEObject>)(InternalEList<?>)resource.getContents();
      saveEObjects(internalEList, Check.CONTAINER);
    }

    public void saveEObjects(InternalEList<? extends InternalEObject> internalEObjects, Check check) throws IOException
    {
      int size = internalEObjects.size();
      InternalEObject [] values = allocateInternalEObjectArray(size);
      internalEObjects.basicToArray(values);
      writeCompressedInt(size);
      for (int i = 0; i < size; ++i)
      {
        InternalEObject internalEObject = values[i];
        saveEObject(internalEObject, check);
      }
      recycle(values);
    }

    public void saveFeatureMap(FeatureMap.Internal featureMap) throws IOException
    {
      int size = featureMap.size();
      FeatureMap.Entry.Internal [] values = allocateFeatureMapEntryArray(size);
      featureMap.toArray(values);
      writeCompressedInt(size);
      for (int i = 0; i < size; ++i)
      {
        FeatureMap.Entry.Internal entry = values[i];
        saveFeatureMapEntry(entry);
      }
      recycle(values);
    }

    public void saveFeatureMapEntry(FeatureMap.Entry.Internal entry) throws IOException
    {
      EStructuralFeatureData eStructuralFeatureData = writeEStructuralFeature(entry.getEStructuralFeature());
      Object value = entry.getValue();
      switch (eStructuralFeatureData.kind)
      {
        case EOBJECT:
        case EOBJECT_LIST:
        case EOBJECT_CONTAINMENT:
        case EOBJECT_CONTAINMENT_LIST:
        {
          saveEObject((InternalEObject)value, Check.NOTHING);
          break;
        }
        case EOBJECT_CONTAINMENT_PROXY_RESOLVING:
        case EOBJECT_CONTAINMENT_LIST_PROXY_RESOLVING:
        {
          saveEObject((InternalEObject)value, Check.DIRECT_RESOURCE);
          break;
        }
        case EOBJECT_PROXY_RESOLVING:
        case EOBJECT_LIST_PROXY_RESOLVING:
        {
          saveEObject((InternalEObject)value, Check.RESOURCE);
          break;
        }
        case BOOLEAN:
        {
          writeBoolean((Boolean)value);
          break;
        }
        case BYTE:
        {
          writeByte((Byte)value);
          break;
        }
        case CHAR:
        {
          writeChar((Character)value);
          break;
        }
        case DOUBLE:
        {
          writeDouble((Double)value);
          break;
        }
        case FLOAT:
        {
          writeFloat((Float)value);
          break;
        }
        case INT:
        {
          writeInt((Integer)value);
          break;
        }
        case LONG:
        {
          writeLong((Long)value);
          break;
        }
        case SHORT:
        {
          writeShort((Short)value);
          break;
        }
        case STRING:
        {
          writeString((String)value);
          break;
        }
        case DATE:
        {
          if ((style & STYLE_BINARY_DATE) != 0)
          {
            writeDate((Date)value);
          }
          else
          {
            writeString(eStructuralFeatureData.eFactory.convertToString(eStructuralFeatureData.eDataType, value));
          }
          break;
        }
        case ENUMERATOR:
        {
          if ((style & STYLE_BINARY_ENUMERATOR) != 0)
          {
            writeInt(((Enumerator)value).getValue());
          }
          else
          {
            writeString(eStructuralFeatureData.eFactory.convertToString(eStructuralFeatureData.eDataType, value));
          }
          break;
        }
        case DATA:
        case DATA_LIST:
        {
          String literal = eStructuralFeatureData.eFactory.convertToString(eStructuralFeatureData.eDataType, value);
          writeString(literal);
          break;
        }
        default:
        {
          throw new IOException("Unhandled case " + eStructuralFeatureData.kind);
        }
      }
    }

    public void saveEObject(InternalEObject internalEObject, Check check) throws IOException
    {
      if (internalEObject == null)
      {
        writeCompressedInt(-1);
      }
      else
      {
        Integer id = eObjectIDMap.get(internalEObject);
        if (id == null)
        {
          int idValue = eObjectIDMap.size();
          writeCompressedInt(idValue);
          eObjectIDMap.put(internalEObject, idValue);
          EClass eClass = internalEObject.eClass();
          EClassData eClassData = writeEClass(eClass);
          boolean checkIsTransientProxy = false;
          switch (check)
          {
            case DIRECT_RESOURCE:
            {
              Internal resource = internalEObject.eDirectResource();
              if (resource != null)
              {
                writeCompressedInt(-1);
                writeURI(resource.getURI(), resource.getURIFragment(internalEObject));
                if ((style & STYLE_PROXY_ATTRIBUTES) == 0)
                {
                  return;
                }
                checkIsTransientProxy = true;
              }
              else if (internalEObject.eIsProxy())
              {
                writeCompressedInt(-1);
                writeURI(internalEObject.eProxyURI());
                if ((style & STYLE_PROXY_ATTRIBUTES) == 0)
                {
                  return;
                }
                checkIsTransientProxy = true;
              }
              break;
            }
            case RESOURCE:
            {
              Resource resource = internalEObject.eResource();
              if (resource != this.resource && resource != null)
              {
                writeCompressedInt(-1);
                writeURI(resource.getURI(), resource.getURIFragment(internalEObject));
                if ((style & STYLE_PROXY_ATTRIBUTES) == 0)
                {
                  return;
                }
                checkIsTransientProxy = true;
              }
              else if (internalEObject.eIsProxy())
              {
                writeCompressedInt(-1);
                writeURI(internalEObject.eProxyURI());
                if ((style & STYLE_PROXY_ATTRIBUTES) == 0)
                {
                  return;
                }
                checkIsTransientProxy = true;
              }
              break;
            }
            case NOTHING:
            case CONTAINER:
            {
              break;
            }
          }
          EStructuralFeatureData [] eStructuralFeatureData = eClassData.eStructuralFeatureData;
          for (int i = 0, length = eStructuralFeatureData.length; i < length; ++i)
          {
            EStructuralFeatureData structuralFeatureData = eStructuralFeatureData[i];
            if (!structuralFeatureData.isTransient &&
                  (structuralFeatureData.kind != FeatureKind.EOBJECT_CONTAINER_PROXY_RESOLVING || check == Check.CONTAINER) &&
                  (!checkIsTransientProxy || !structuralFeatureData.isProxyTransient))
            {
              saveFeatureValue(internalEObject, i, structuralFeatureData);
            }
          }
          writeCompressedInt(0);
        }
        else
        {
          writeCompressedInt(id);
        }
      }
    }

    protected void saveFeatureValue(InternalEObject internalEObject, int featureID, EStructuralFeatureData eStructuralFeatureData) throws IOException
    {
      if (internalEObject.eIsSet(featureID))
      {
        writeCompressedInt(featureID + 1);
        if (eStructuralFeatureData.name != null)
        {
          writeString(eStructuralFeatureData.name);
          eStructuralFeatureData.name = null;
        }
        Object value = internalEObject.eGet(featureID, false, true);
        switch (eStructuralFeatureData.kind)
        {
          case EOBJECT:
          case EOBJECT_CONTAINMENT:
          {
            saveEObject((InternalEObject)value, Check.NOTHING);
            break;
          }
          case EOBJECT_CONTAINER_PROXY_RESOLVING:
          {
            saveEObject((InternalEObject)value, Check.RESOURCE);
            break;
          }
          case EOBJECT_CONTAINMENT_PROXY_RESOLVING:
          {
            saveEObject((InternalEObject)value, Check.DIRECT_RESOURCE);
            break;
          }
          case EOBJECT_PROXY_RESOLVING:
          {
            saveEObject((InternalEObject)value, Check.RESOURCE);
            break;
          }
          case EOBJECT_LIST:
          case EOBJECT_CONTAINMENT_LIST:
          {
            @SuppressWarnings("unchecked")
            InternalEList<? extends InternalEObject> internalEList = (InternalEList<? extends InternalEObject>)value;
            saveEObjects(internalEList, Check.NOTHING);
            break;
          }
          case EOBJECT_CONTAINMENT_LIST_PROXY_RESOLVING:
          {
            @SuppressWarnings("unchecked")
            InternalEList<? extends InternalEObject> internalEList = (InternalEList<? extends InternalEObject>)value;
            saveEObjects(internalEList, Check.DIRECT_RESOURCE);
            break;
          }
          case EOBJECT_LIST_PROXY_RESOLVING:
          {
            @SuppressWarnings("unchecked")
            InternalEList<? extends InternalEObject> internalEList = (InternalEList<? extends InternalEObject>)value;
            saveEObjects(internalEList, Check.RESOURCE);
            break;
          }
          case BOOLEAN:
          {
            writeBoolean((Boolean)value);
            break;
          }
          case BYTE:
          {
            writeByte((Byte)value);
            break;
          }
          case CHAR:
          {
            writeChar((Character)value);
            break;
          }
          case DOUBLE:
          {
            writeDouble((Double)value);
            break;
          }
          case FLOAT:
          {
            writeFloat((Float)value);
            break;
          }
          case INT:
          {
            writeInt((Integer)value);
            break;
          }
          case LONG:
          {
            writeLong((Long)value);
            break;
          }
          case SHORT:
          {
            writeShort((Short)value);
            break;
          }
          case STRING:
          {
            writeString((String)value);
            break;
          }
          case FEATURE_MAP:
          {
            FeatureMap.Internal featureMap = (FeatureMap.Internal)value;
            saveFeatureMap(featureMap);
            break;
          }
          case DATE:
          {
            if ((style & STYLE_BINARY_DATE) != 0)
            {
              writeDate((Date)value);
            }
            else
            {
              writeString(eStructuralFeatureData.eFactory.convertToString(eStructuralFeatureData.eDataType, value));
            }
            break;
          }
          case ENUMERATOR:
          {
            if ((style & STYLE_BINARY_ENUMERATOR) != 0)
            {
              writeInt(((Enumerator)value).getValue());
            }
            else
            {
              writeString(eStructuralFeatureData.eFactory.convertToString(eStructuralFeatureData.eDataType, value));
            }
            break;
          }
          case DATA:
          {
            String literal = eStructuralFeatureData.eFactory.convertToString(eStructuralFeatureData.eDataType, value);
            writeString(literal);
            break;
          }
          case DATA_LIST:
          {
            List<?> dataValues = (List<?>)value;
            int length = dataValues.size();
            writeCompressedInt(length);
            for (int j = 0; j < length; ++j)
            {
              String literal = eStructuralFeatureData.eFactory.convertToString(eStructuralFeatureData.eDataType, dataValues.get(j));
              writeString(literal);
            }
            break;
          }
          default:
          {
            throw new IOException("Unhandled case " + eStructuralFeatureData.kind);
          }
        }
      }
    }

    public void writeByte(int value) throws IOException
    {
      outputStream.write(value);
    }

    public void writeBoolean(boolean value) throws IOException
    {
      writeByte(value ? 1 : 0);
    }

    public void writeChar(int value) throws IOException
    {
      writeByte((byte)(value >> 8 & 0xFF));
      writeByte((byte)(value & 0xFF));
    }

    public void writeShort(int value) throws IOException
    {
      writeByte((byte)(value >> 8 & 0xFF));
      writeByte((byte)(value & 0xFF));
    }

    public void writeInt(int value) throws IOException
    {
      writeByte((byte)(value >> 24 & 0xFF));
      writeByte((byte)(value >> 16 & 0xFF));
      writeByte((byte)(value >> 8 & 0xFF));
      writeByte((byte)(value & 0xFF));
    }

    public void writeLong(long value) throws IOException
    {
      writeInt((int)(value >> 32));
      writeInt((int)value);
    }

    public void writeFloat(float value) throws IOException
    {
      if ((style & STYLE_BINARY_FLOATING_POINT) != 0)
      {
        throw new IOException("Binary serialization of floats is not supported for GWT");
      }
      else
      {
        writeString(Float.toString(value));
      }
    }

    public void writeDouble(double value) throws IOException
    {
      if ((style & STYLE_BINARY_FLOATING_POINT) != 0)
      {
        throw new IOException("Binary serialization of floats is not supported for GWT");
      }
      else
      {
        writeString(Double.toString(value));
      }
    }

    public void writeCompressedInt(int value) throws IOException
    {
      ++value;
      int firstByte = value >> 24 & 0xFF;
      int secondByte = value >> 16 & 0xFF;
      int thirdByte = value >> 8 & 0xFF;
      int fourthBtye = value & 0xFF;
      if (firstByte > 0x3F)
      {
        handleInvalidValue(value);
      }
      else if (firstByte != 0 || secondByte > 0x3F)
      {
        writeByte(firstByte | 0xC0);
        writeByte(secondByte);
        writeByte(thirdByte);
        writeByte(fourthBtye);
      }
      else if (secondByte != 0 || thirdByte > 0x3F)
      {
        writeByte(secondByte | 0x80);
        writeByte(thirdByte);
        writeByte(fourthBtye);
      }
      else if (thirdByte != 0 || fourthBtye > 0x3F)
      {
        writeByte(thirdByte | 0x40);
        writeByte(fourthBtye);
      }
      else
      {
        writeByte(fourthBtye);
      }
    }

    private final void handleInvalidValue(int value) throws IOException
    {
      throw new IOException("Invalid value " + value);
    }

    public void writeString(String value) throws IOException
    {
      if (value == null)
      {
        writeCompressedInt(-1);
      }
      else
      {
        int length = value.length();
        writeCompressedInt(length);
        if (characters == null || characters.length < length)
        {
          characters = new char[length];
        }
        value.getChars(0, length, characters, 0);
        LOOP:
        for (int i = 0; i < length; ++i)
        {
          char character = characters[i];
          if (character == 0 || character > 0xFF)
          {
            writeByte((byte)0);
            writeChar(character);
            while (++i < length)
            {
              writeChar(characters[i]);
            }
            break LOOP;
          }
          else
          {
            writeByte((byte)character);
          }
        }
      }
    }

    public void writeDate(Date date) throws IOException
    {
      writeLong(date.getTime());
    }

    public void writeURI(URI uri) throws IOException
    {
      writeURI(uri.trimFragment(), uri.fragment());
    }

    public void writeURI(URI uri, String fragment) throws IOException
    {
      if (uri == null)
      {
        writeCompressedInt(-1);
      }
      else
      {
        assert uri.fragment() == null;
        Integer id = uriToIDMap.get(uri);
        if (id == null)
        {
          int idValue = uriToIDMap.size();
          uriToIDMap.put(uri, idValue);
          writeCompressedInt(idValue);
          writeString(deresolve(uri).toString());
        }
        else
        {
          writeCompressedInt(id);
        }
        writeString(fragment);
      }
    }
  }

  public static class EObjectInputStream extends BinaryIO
  {
    protected static class EPackageData
    {
      public EPackage ePackage;
      public EClassData[] eClassData;

      public final int allocateEClassID()
      {
        for (int i = 0, length = eClassData.length; i < length; ++i)
        {
          EClassData eClassData = this.eClassData[i];
          if (eClassData == null)
          {
            return i;
          }
        }
        return -1;
      }
    }

    protected static class EClassData
    {
      public EClass eClass;
      public EFactory eFactory;
      public EStructuralFeatureData[] eStructuralFeatureData;

    }

    protected static class EStructuralFeatureData
    {
      public int featureID;
      public EStructuralFeature eStructuralFeature;
      public FeatureKind kind;
      public EFactory eFactory;
      public EDataType eDataType;
    }

    protected ResourceSet resourceSet;
    protected InputStream inputStream;
    protected List<EPackageData> ePackageDataList = new ArrayList<EPackageData>();
    protected List<EClassData> eClassDataList = new ArrayList<EClassData>();
    protected List<InternalEObject> eObjectList = new ArrayList<InternalEObject>();
    protected List<URI> uriList = new ArrayList<URI>();

    protected BasicEList<InternalEObject> internalEObjectList = new BasicEList<InternalEObject>();
    protected BasicEList<Object> dataValueList = new BasicEList<Object>();

    public EObjectInputStream(InputStream inputStream, Map<?, ?> options) throws IOException
    {
      this.inputStream = inputStream;
      this.options = options;
      readSignature();
      readVersion();
      if (version.ordinal() > 0)
      {
        readStyle();
      }
    }

    protected void readSignature() throws IOException
    {
      if (readByte() != (byte)'\211' ||
           readByte() != 'e' ||
           readByte() != 'm' ||
           readByte() != 'f' ||
           readByte() != '\n' ||
           readByte() != '\r' ||
           readByte() != '\032' ||
           readByte() != '\n')
      {
        throw new IOException("Invalid signature for a binary EMF serialization");
      }
    }

    protected void readVersion() throws IOException
    {
      version = Version.values()[readByte()];
    }

    protected void readStyle() throws IOException
    {
      style = readInt();
    }

    protected int[][] intDataArrayBuffer = new int[50][];
    protected int intDataArrayBufferCount = -1;

    protected int [] allocateIntArray(int length)
    {
      if (intDataArrayBufferCount == -1)
      {
        return new int[length];
      }
      else
      {
        int[] buffer = intDataArrayBuffer[intDataArrayBufferCount];
        intDataArrayBuffer[intDataArrayBufferCount--] = null;
        return buffer.length >= length ? buffer :  new int[length];
      }
    }

    protected void recycle(int[] values)
    {
      if (++intDataArrayBufferCount >= intDataArrayBuffer.length)
      {
        int [][] newIntDataArrayBuffer = new int[intDataArrayBufferCount * 2][];
        System.arraycopy(intDataArrayBuffer, 0, newIntDataArrayBuffer, 0, intDataArrayBufferCount);
        intDataArrayBuffer = newIntDataArrayBuffer;
      }
      intDataArrayBuffer[intDataArrayBufferCount] = values;
    }

    protected EPackageData readEPackage() throws IOException
    {
      int id = readCompressedInt();
      if (ePackageDataList.size() <= id)
      {
        EPackageData ePackageData = new EPackageData();
        String nsURI = readString();
        URI uri = readURI();
        if (resourceSet != null)
        {
          ePackageData.ePackage = EPackage.Registry.INSTANCE.getEPackage(nsURI);
          if (ePackageData.ePackage == null)
          {
            ePackageData.ePackage = (EPackage)resourceSet.getEObject(uri, true);
          }
        }
        else
        {
          ePackageData.ePackage = EPackage.Registry.INSTANCE.getEPackage(nsURI);
        }
        ePackageData.eClassData = new EClassData [ePackageData.ePackage.getEClassifiers().size()];
        ePackageDataList.add(ePackageData);
        return ePackageData;
      }
      else
      {
        return ePackageDataList.get(id);
      }
    }

    protected EClassData readEClass() throws IOException
    {
      EPackageData ePackageData = readEPackage();
      int id = readCompressedInt();
      EClassData eClassData = ePackageData.eClassData[id];
      if (eClassData == null)
      {
        eClassData = ePackageData.eClassData[id] = new EClassData();
        String name = readString();
        eClassData.eClass = (EClass)ePackageData.ePackage.getEClassifier(name);
        eClassData.eFactory = ePackageData.ePackage.getEFactoryInstance();
        eClassData.eStructuralFeatureData = new EStructuralFeatureData [eClassData.eClass.getFeatureCount()];
      }
      return eClassData;
    }

    protected EStructuralFeatureData readEStructuralFeature() throws IOException
    {
      EClassData eClassData = readEClass();
      int featureID = readCompressedInt();
      return getEStructuralFeatureData(eClassData, featureID);
    }

    protected EStructuralFeatureData getEStructuralFeatureData(EClassData eClassData, int featureID) throws IOException
    {
      EStructuralFeatureData eStructuralFeatureData = eClassData.eStructuralFeatureData[featureID];
      if (eStructuralFeatureData == null)
      {
        eStructuralFeatureData = eClassData.eStructuralFeatureData[featureID] = new EStructuralFeatureData();
        String name = readString();
        eStructuralFeatureData.eStructuralFeature = eClassData.eClass.getEStructuralFeature(name);
        eStructuralFeatureData.featureID = eClassData.eClass.getFeatureID(eStructuralFeatureData.eStructuralFeature);
        eStructuralFeatureData.kind = FeatureKind.get(eStructuralFeatureData.eStructuralFeature);
        if (eStructuralFeatureData.eStructuralFeature instanceof EAttribute)
        {
          EAttribute eAttribute = (EAttribute)eStructuralFeatureData.eStructuralFeature;
          eStructuralFeatureData.eDataType = eAttribute.getEAttributeType();
          eStructuralFeatureData.eFactory = eStructuralFeatureData.eDataType.getEPackage().getEFactoryInstance();
        }
      }
      return eStructuralFeatureData;
    }

    public void loadResource(Resource resource) throws IOException
    {
      this.resource = resource;
      this.resourceSet = resource.getResourceSet();
      URI uri = resource.getURI();
      if (uri != null && uri.isHierarchical() && !uri.isRelative())
      {
        baseURI = uri;
      }
      int size = readCompressedInt();
      InternalEObject[] values = allocateInternalEObjectArray(size);
      for (int i = 0; i < size; ++i)
      {
        values[i] = loadEObject();
      }
      internalEObjectList.setData(size, values);
      @SuppressWarnings("unchecked")
      InternalEList<InternalEObject> internalEObjects = (InternalEList<InternalEObject>)(InternalEList<?>)resource.getContents();
      internalEObjects.addAllUnique(internalEObjectList);
      recycle(values);
    }

    public void loadEObjects(InternalEList<InternalEObject> internalEObjects) throws IOException
    {
      // Read all the values into an array.
      //
      int size = readCompressedInt();
      InternalEObject[] values = allocateInternalEObjectArray(size);
      for (int i = 0; i < size; ++i)
      {
        values[i] = loadEObject();
      }
      int existingSize = internalEObjects.size();

      // If the list is empty, we need to add all the objects,
      // otherwise, the reference is bidirectional and the list is at least partially populated.
      //
      if (existingSize == 0)
      {
        internalEObjectList.setData(size, values);
        internalEObjects.addAllUnique(internalEObjectList);
      }
      else
      {
        InternalEObject [] existingValues = allocateInternalEObjectArray(existingSize);
        internalEObjects.basicToArray(existingValues);
        int [] indices = allocateIntArray(existingSize);
        int duplicateCount = 0;
        LOOP:
        for (int i = 0; i < size; ++i)
        {
          InternalEObject internalEObject = values[i];
          for (int j = 0, count = 0; j < existingSize; ++j)
          {
            InternalEObject existingInternalEObject = existingValues[j];
            if (existingInternalEObject == internalEObject)
            {
              if (duplicateCount != count)
              {
                internalEObjects.move(duplicateCount, count);
              }
              indices[duplicateCount] = i;
              ++count;
              ++duplicateCount;
              existingValues[j] = null;
              continue LOOP;
            }
            else if (existingInternalEObject != null)
            {
              ++count;
            }
          }

          values[i - duplicateCount] = internalEObject;
        }

        size -= existingSize;
        internalEObjectList.setData(size, values);
        internalEObjects.addAllUnique(0, internalEObjectList);
        for (int i = 0; i < existingSize; ++i)
        {
          int newPosition = indices[i];
          int oldPosition = size + i;
          if (newPosition != oldPosition)
          {
            internalEObjects.move(newPosition, oldPosition);
          }
        }
        recycle(existingValues);
        recycle(indices);
      }
      recycle(values);
    }

    public void loadFeatureMap(FeatureMap.Internal featureMap) throws IOException
    {
      // Read all the values into an array.
      //
      int size = readCompressedInt();
      FeatureMap.Entry.Internal[] values = allocateFeatureMapEntryArray(size);
      for (int i = 0; i < size; ++i)
      {
        values[i] = loadFeatureMapEntry();
      }
      int existingSize = featureMap.size();

      // If the list is empty, we need to add all the objects,
      // otherwise, the reference is bidirectional and the list is at least partially populated.
      //
      if (existingSize == 0)
      {
        featureMap.addAllUnique(values, 0, size);
      }
      else
      {
        FeatureMap.Entry.Internal [] existingValues = allocateFeatureMapEntryArray(existingSize);
        featureMap.basicToArray(existingValues);
        int [] indices = allocateIntArray(existingSize);
        int duplicateCount = 0;
        LOOP:
        for (int i = 0; i < size; ++i)
        {
          FeatureMap.Entry.Internal entry = values[i];
          for (int j = 0, count = 0; j < existingSize; ++j)
          {
            FeatureMap.Entry.Internal existingEntry = existingValues[j];
            if (entry.equals(existingEntry))
            {
              if (duplicateCount != count)
              {
                featureMap.move(duplicateCount, count);
              }
              indices[duplicateCount] = i;
              ++count;
              ++duplicateCount;
              existingValues[j] = null;
              continue LOOP;
            }
            else if (existingEntry != null)
            {
              ++count;
            }
          }

          values[i - duplicateCount] = entry;
        }

        size -= existingSize;
        internalEObjectList.setData(size, values);
        featureMap.addAllUnique(0, values, 0, size);
        for (int i = 0; i < existingSize; ++i)
        {
          int newPosition = indices[i];
          int oldPosition = size + i;
          if (newPosition != oldPosition)
          {
            featureMap.move(newPosition, oldPosition);
          }
        }
        recycle(existingValues);
        recycle(indices);
      }
      recycle(values);
    }

    public FeatureMap.Entry.Internal loadFeatureMapEntry() throws IOException
    {
      EStructuralFeatureData eStructuralFeatureData = readEStructuralFeature();
      Object value;
      switch (eStructuralFeatureData.kind)
      {
        case EOBJECT_CONTAINER:
        case EOBJECT_CONTAINER_PROXY_RESOLVING:
        case EOBJECT:
        case EOBJECT_LIST:
        case EOBJECT_PROXY_RESOLVING:
        case EOBJECT_LIST_PROXY_RESOLVING:
        case EOBJECT_CONTAINMENT:
        case EOBJECT_CONTAINMENT_LIST:
        case EOBJECT_CONTAINMENT_PROXY_RESOLVING:
        case EOBJECT_CONTAINMENT_LIST_PROXY_RESOLVING:
        {
          value = loadEObject();
          break;
        }
        case STRING:
        {
          value = readString();
          break;
        }
        case DATE:
        {
          if ((style & STYLE_BINARY_DATE) != 0)
          {
            value = readDate();
          }
          else
          {
            value = eStructuralFeatureData.eFactory.createFromString(eStructuralFeatureData.eDataType, readString());
          }
          break;
        }
        case ENUMERATOR:
        {
          if ((style & STYLE_BINARY_ENUMERATOR) != 0)
          {
            value = ((EEnum)eStructuralFeatureData.eDataType).getEEnumLiteral(readInt()).getInstance();
          }
          else
          {
            value = eStructuralFeatureData.eFactory.createFromString(eStructuralFeatureData.eDataType, readString());
          }
          break;
        }
        case DATA:
        case DATA_LIST:
        {
          String literal = readString();
          value = eStructuralFeatureData.eFactory.createFromString(eStructuralFeatureData.eDataType, literal);
          break;
        }
        case BOOLEAN:
        {
          value = readBoolean();
          break;
        }
        case BYTE:
        {
          value = readByte();
          break;
        }
        case CHAR:
        {
          value = readChar();
          break;
        }
        case DOUBLE:
        {
          value = readDouble();
          break;
        }
        case FLOAT:
        {
          value = readFloat();
          break;
        }
        case INT:
        {
          value = readInt();
          break;
        }
        case LONG:
        {
          value = readLong();
          break;
        }
        case SHORT:
        {
          value = readShort();
          break;
        }
        default:
        {
          throw new IOException("Unhandled case " + eStructuralFeatureData.kind);
        }
      }
      return FeatureMapUtil.createRawEntry(eStructuralFeatureData.eStructuralFeature, value);
    }

    public InternalEObject loadEObject() throws IOException
    {
      int id = readCompressedInt();
      if (id == -1)
      {
        return null;
      }
      else
      {
        if (eObjectList.size() <= id)
        {
          EClassData eClassData = readEClass();
          InternalEObject internalEObject =  (InternalEObject)eClassData.eFactory.create(eClassData.eClass);
          eObjectList.add(internalEObject);
          for (;;)
          {
            int featureID = readCompressedInt() - 1;
            if (featureID == -1)
            {
              break;
            }
            else if (featureID == -2)
            {
              internalEObject.eSetProxyURI(readURI());
              if ((style & STYLE_PROXY_ATTRIBUTES) == 0)
              {
                break;
              }
            }
            else
            {
              EStructuralFeatureData eStructuralFeatureData = getEStructuralFeatureData(eClassData, featureID);
              loadFeatureValue(internalEObject, eStructuralFeatureData);
            }
          }
          return internalEObject;
        }
        else
        {
          return eObjectList.get(id);
        }
      }
    }

    protected void loadFeatureValue(InternalEObject internalEObject, EStructuralFeatureData eStructuralFeatureData) throws IOException
    {
      switch (eStructuralFeatureData.kind)
      {
        case EOBJECT_CONTAINER:
        case EOBJECT_CONTAINER_PROXY_RESOLVING:
        case EOBJECT:
        case EOBJECT_PROXY_RESOLVING:
        case EOBJECT_CONTAINMENT:
        case EOBJECT_CONTAINMENT_PROXY_RESOLVING:
        {
          internalEObject.eSet(eStructuralFeatureData.featureID, loadEObject());
          break;
        }
        case EOBJECT_LIST:
        case EOBJECT_LIST_PROXY_RESOLVING:
        case EOBJECT_CONTAINMENT_LIST:
        case EOBJECT_CONTAINMENT_LIST_PROXY_RESOLVING:
        {
          @SuppressWarnings("unchecked")
          InternalEList<InternalEObject> internalEList = (InternalEList<InternalEObject>)internalEObject.eGet(eStructuralFeatureData.featureID, false, true);
          loadEObjects(internalEList);
          break;
        }
        case STRING:
        {
          internalEObject.eSet(eStructuralFeatureData.featureID, readString());
          break;
        }
        case FEATURE_MAP:
        {
          FeatureMap.Internal featureMap = (FeatureMap.Internal)internalEObject.eGet(eStructuralFeatureData.featureID, false, true);
          loadFeatureMap(featureMap);
          break;
        }
        case DATE:
        {
          if ((style & STYLE_BINARY_DATE) != 0)
          {
            internalEObject.eSet(eStructuralFeatureData.featureID, readDate());
          }
          else
          {
            internalEObject.eSet(eStructuralFeatureData.featureID, eStructuralFeatureData.eFactory.createFromString(eStructuralFeatureData.eDataType, readString()));
          }
          break;
        }
        case ENUMERATOR:
        {
          if ((style & STYLE_BINARY_ENUMERATOR) != 0)
          {
            internalEObject.eSet(eStructuralFeatureData.featureID, ((EEnum)eStructuralFeatureData.eDataType).getEEnumLiteral(readInt()).getInstance());
          }
          else
          {
            internalEObject.eSet(eStructuralFeatureData.featureID, eStructuralFeatureData.eFactory.createFromString(eStructuralFeatureData.eDataType, readString()));
          }
          break;
        }
        case DATA:
        {
          String literal = readString();
          internalEObject.eSet(eStructuralFeatureData.featureID, eStructuralFeatureData.eFactory.createFromString(eStructuralFeatureData.eDataType, literal));
          break;
        }
        case DATA_LIST:
        {
          int size = readCompressedInt();
          dataValueList.grow(size);
          Object[] dataValues = dataValueList.data();
          for (int i = 0; i < size; ++i)
          {
            String literal = readString();
            dataValues[i] = eStructuralFeatureData.eFactory.createFromString(eStructuralFeatureData.eDataType, literal);
          }
          dataValueList.setData(size, dataValues);
          @SuppressWarnings("unchecked")
          List<Object> values = (List<Object>)internalEObject.eGet(eStructuralFeatureData.featureID, false, true);
          values.addAll(dataValueList);
          break;
        }
        case BOOLEAN:
        {
          internalEObject.eSet(eStructuralFeatureData.featureID, readBoolean());
          break;
        }
        case BYTE:
        {
          internalEObject.eSet(eStructuralFeatureData.featureID, readByte());
          break;
        }
        case CHAR:
        {
          internalEObject.eSet(eStructuralFeatureData.featureID, readChar());
          break;
        }
        case DOUBLE:
        {
          internalEObject.eSet(eStructuralFeatureData.featureID, readDouble());
          break;
        }
        case FLOAT:
        {
          internalEObject.eSet(eStructuralFeatureData.featureID, readFloat());
          break;
        }
        case INT:
        {
          internalEObject.eSet(eStructuralFeatureData.featureID, readInt());
          break;
        }
        case LONG:
        {
          internalEObject.eSet(eStructuralFeatureData.featureID, readLong());
          break;
        }
        case SHORT:
        {
          internalEObject.eSet(eStructuralFeatureData.featureID, readShort());
          break;
        }
        default:
        {
          throw new IOException("Unhandled case " + eStructuralFeatureData.kind);
        }
      }
    }

    public byte readByte() throws IOException
    {
      int result = inputStream.read();
      if (result == -1)
      {
        throw new IOException("Unexpected end of stream");
      }
      return (byte)result;
    }

    public boolean readBoolean() throws IOException
    {
      return readByte() != 0;
    }

    public char readChar() throws IOException
    {
      return (char)((readByte() << 8) & 0xFF00 | readByte() & 0xFF);
    }

    public short readShort() throws IOException
    {
      return (short)((readByte()<< 8) & 0xFF00 | readByte() & 0xFF);
    }

    public int readInt() throws IOException
    {
      return (readByte() << 24) | (readByte() << 16) & 0xFF0000 | (readByte() << 8) & 0xFF00 | readByte() & 0xFF;
    }

    public long readLong() throws IOException
    {
      return (long)readInt() << 32 | readInt() & 0xFFFFFFFFL;
    }

    public float readFloat() throws IOException
    {
      if ((style & STYLE_BINARY_FLOATING_POINT) != 0)
      {
        throw new IOException("Binary deserialization of floats is not supported for GWT");
      }
      else
      {
        return Float.parseFloat(readString());
      }
    }

    public double readDouble() throws IOException
    {
      if ((style & STYLE_BINARY_FLOATING_POINT) != 0)
      {
        throw new IOException("Binary deserialization of floats is not supported for GWT");
      }
      else
      {
        return Double.parseDouble(readString());
      }
    }

    public int readCompressedInt() throws IOException
    {
      byte initialByte = readByte();
      int code = (initialByte >> 6) & 0x3;
      switch (code)
      {
        case 0:
        {
          return initialByte - 1;
        }
        case 1:
        {
          return (initialByte << 8 & 0x3F00 | readByte() & 0xFF) - 1;
        }
        case 2:
        {
          return ((initialByte << 16) & 0x3F0000 | (readByte() << 8) & 0xFF00 | readByte() & 0xFF) - 1;
        }
        default:
        {
          return ((initialByte << 24) & 0x3F000000 | (readByte() << 16) & 0xFF0000 | (readByte() << 8) & 0xFF00 | readByte() & 0xFF) - 1;
        }
      }
    }

    public String readString() throws IOException
    {
      int length = readCompressedInt();
      if (length == -1)
      {
        return null;
      }
      else
      {
        if (characters == null || characters.length < length)
        {
          characters = new char[length];
        }
        LOOP:
        for (int i = 0; i < length; ++i)
        {
          byte value = readByte();
          if (value == 0)
          {
            do
            {
              characters[i] = readChar();
            }
            while (++i < length);
            break LOOP;
          }
          else
          {
            characters[i] = (char)(value & 0xFF);
          }
        }
        return new String(characters, 0, length);
      }
    }

    public Date readDate() throws IOException
    {
      long time = readLong();
      return new Date(time);
    }

    public URI readURI() throws IOException
    {
      int id = readCompressedInt();
      if (id == -1)
      {
        return null;
      }
      else
      {
        URI uri;
        if (uriList.size() <= id)
        {
          String value = readString();
          uri = resolve(URI.createURI(value));
          uriList.add(uri);
        }
        else
        {
          uri = uriList.get(id);
        }
        String fragment = readString();
        if (fragment != null)
        {
          uri = uri.appendFragment(fragment);
        }
        return uri;
      }
    }
  }
}
