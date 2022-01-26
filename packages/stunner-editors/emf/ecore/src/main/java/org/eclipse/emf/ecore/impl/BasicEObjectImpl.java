/**
 * Copyright (c) 2002-2012 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 *   Christian Damus (Zeligsoft) - 255469
 */
package org.eclipse.emf.ecore.impl;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.BasicNotifierImpl;
import org.eclipse.emf.common.util.AbstractTreeIterator;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.Callback;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.InvocationTargetException;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.ECrossReferenceEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * An implementation of the model object '<em><b>EObject</b></em>'.
 */
public class BasicEObjectImpl extends BasicNotifierImpl implements EObject, InternalEObject, IsSerializable
{
  /**
   * An internal interface for holding less frequently members variables.
   */
  protected interface EPropertiesHolder extends EStructuralFeature.Internal.DynamicValueHolder
  {
    EClass getEClass();
    void setEClass(EClass eClass);

    URI getEProxyURI();
    void setEProxyURI(URI eProxyURI);

    Resource.Internal getEResource();
    void setEResource(Resource.Internal eResource);

    EList<EObject> getEContents();
    void setEContents(EList<EObject> eContents);

    EList<EObject> getECrossReferences();
    void setECrossReferences(EList<EObject> eCrossReferences);

    boolean hasSettings();
    void allocateSettings(int dynamicFeatureCount);
  }

  /**
   * An internal class for holding the least frequently members variables.
   */
  protected static class EPropertiesHolderBaseImpl implements EPropertiesHolder
  {
    protected EClass eClass;
    protected Resource.Internal eResource;
    protected Object [] eSettings;

    protected static final Object [] NO_SETTINGS = new Object [0];

    public EClass getEClass()
    {
      return eClass;
    }

    public void setEClass(EClass eClass)
    {
      this.eClass = eClass;
    }

    public URI getEProxyURI()
    {
      throw new UnsupportedOperationException();
    }

    public void setEProxyURI(URI eProxyURI)
    {
      throw new UnsupportedOperationException();
    }

    public Resource.Internal getEResource()
    {
      return eResource;
    }

    public void setEResource(Resource.Internal eResource)
    {
      this.eResource = eResource;
    }

    public EList<EObject> getEContents()
    {
      throw new UnsupportedOperationException();
    }

    public void setEContents(EList<EObject> eContents)
    {
      throw new UnsupportedOperationException();
    }

    public EList<EObject> getECrossReferences()
    {
      throw new UnsupportedOperationException();
    }

    public void setECrossReferences(EList<EObject> eCrossReferences)
    {
      throw new UnsupportedOperationException();
    }

    public boolean hasSettings()
    {
      return eSettings != null;
    }

    public void allocateSettings(int dynamicFeatureCount)
    {
      eSettings = dynamicFeatureCount == 0 ? NO_SETTINGS : new Object [dynamicFeatureCount];
    }

    public Object dynamicGet(int dynamicFeatureID)
    {
      return eSettings[dynamicFeatureID];
    }

    public void dynamicSet(int dynamicFeatureID, Object value)
    {
      eSettings[dynamicFeatureID] = value;
    }

    public void dynamicUnset(int dynamicFeatureID)
    {
      eSettings[dynamicFeatureID] = null;
    }
  }

  /**
   * An internal class for holding less frequently members variables.
   */
  protected static class EPropertiesHolderImpl extends EPropertiesHolderBaseImpl
  {
    protected URI eProxyURI;
    protected EList<EObject> eContents;
    protected EList<EObject> eCrossReferences;

    @Override
    public URI getEProxyURI()
    {
      return eProxyURI;
    }

    @Override
    public void setEProxyURI(URI eProxyURI)
    {
      this.eProxyURI = eProxyURI;
    }

    @Override
    public EList<EObject> getEContents()
    {
      return eContents;
    }

    @Override
    public void setEContents(EList<EObject> eContents)
    {
      this.eContents = eContents;
    }

    @Override
    public EList<EObject> getECrossReferences()
    {
      return eCrossReferences;
    }

    @Override
    public void setECrossReferences(EList<EObject> eCrossReferences)
    {
      this.eCrossReferences = eCrossReferences;
    }
  }

  /**
   * Creates a basic EObject.
   */
  protected BasicEObjectImpl() 
  {
    super();
  }

  protected int eStaticFeatureCount()
  {
    return eStaticClass().getFeatureCount();
  }

  protected int eStaticOperationCount()
  {
    return eStaticClass().getOperationCount();
  }

  protected EPropertiesHolder eProperties()
  {
    throw new UnsupportedOperationException();
    // if (eProperties == null)
    // {
      // eProperties = new EPropertiesHolderImpl();
    // }
    // return eProperties;
  }

  protected EPropertiesHolder eBasicProperties()
  {
    throw new UnsupportedOperationException();
    // return eProperties;
  }

  protected boolean eHasSettings()
  {
    EPropertiesHolder eProperties = eBasicProperties();
    return eProperties != null && eProperties.hasSettings();
  }

  protected EStructuralFeature.Internal.DynamicValueHolder eSettings()
  {
    if (!eHasSettings())
    {
      int size =  eClass().getFeatureCount() - eStaticFeatureCount();
      eProperties().allocateSettings(size);
    }

    return eBasicProperties();
  }

  protected int eDynamicFeatureID(EStructuralFeature eStructuralFeature)
  {
    return eClass().getFeatureID(eStructuralFeature) - eStaticFeatureCount();
  }

  protected EStructuralFeature eDynamicFeature(int dynamicFeatureID)
  {
    return eClass().getEStructuralFeature(dynamicFeatureID + eStaticFeatureCount());
  }

  public String eURIFragmentSegment(EStructuralFeature eStructuralFeature, EObject eObject)
  {
    if (eStructuralFeature == null)
    {
      for (@SuppressWarnings("unchecked") EContentsEList.FeatureIterator<EObject> crossReferences = 
             (EContentsEList.FeatureIterator<EObject>)((InternalEList<?>)eCrossReferences()).basicIterator(); 
           crossReferences.hasNext(); )
      {
        EObject crossReference = crossReferences.next();
        if (crossReference == eObject)
        {
          eStructuralFeature = crossReferences.feature();
        }
      }
    }
    
    StringBuilder result = new StringBuilder();
    result.append('@');
    result.append(eStructuralFeature.getName());

    if (eStructuralFeature instanceof EAttribute)
    {
      FeatureMap featureMap = (FeatureMap)eGet(eStructuralFeature, false);
      for (int i = 0, size = featureMap.size(); i < size; ++i)
      {
        if (featureMap.getValue(i) == eObject)
        {
          EStructuralFeature entryFeature = featureMap.getEStructuralFeature(i);
          if (entryFeature instanceof EReference && ((EReference)entryFeature).isContainment())
          {
            result.append('.');
            result.append(i);
            return result.toString();
          }
        }
      }
      result.append(".-1");
    }
    else if (eStructuralFeature.isMany())
    {
      EList<EAttribute> eKeys = ((EReference)eStructuralFeature).getEKeys();
      if (eKeys.isEmpty())
      {
        EList<?> eList = (EList<?>)eGet(eStructuralFeature, false);
        int index = eList.indexOf(eObject);
        result.append('.');
        result.append(index);
      }
      else 
      {
        EAttribute [] eAttributes = (EAttribute[])((BasicEList<?>)eKeys).data();
        result.append('[');
        for (int i = 0, size = eAttributes.length; i < size; ++i)
        {
          EAttribute eAttribute = eAttributes[i];
          if (eAttribute == null)
          {
            break;
          }
          else 
          {
            if (i != 0)
            {
              result.append(',');
            }
            result.append(eAttribute.getName());
            result.append('=');
            EDataType eDataType = eAttribute.getEAttributeType();
            EFactory eFactory = eDataType.getEPackage().getEFactoryInstance();
            if (eAttribute.isMany())
            {
              List<?> values = (List<?>)eObject.eGet(eAttribute);
              result.append('[');
              if (!values.isEmpty())
              {
                Iterator<?> j = values.iterator();
                eEncodeValue(result, eFactory, eDataType, j.next());
                while (j.hasNext())
                {
                  result.append(',');
                  eEncodeValue(result, eFactory, eDataType, j.next());
                }
              }
              result.append(']');
            }
            else
            {
              eEncodeValue(result, eFactory, eDataType, eObject.eGet(eAttribute));
            }
          }
        }
        result.append(']');
      }
    }
    
    return result.toString();
  }
  
  private static final String [] ESCAPE =
   {
     "%00",
     "%01",
     "%02",
     "%03",
     "%04",
     "%05",
     "%06",
     "%07",
     "%08",
     "%09",
     "%0A",
     "%0B",
     "%0C",
     "%0D",
     "%0E",
     "%0F",
     "%10",
     "%11",
     "%12",
     "%13",
     "%14",
     "%15",
     "%16",
     "%17",
     "%18",
     "%19",
     "%1A",
     "%1B",
     "%1C",
     "%1D",
     "%1E",
     "%1F",
     "%20",
     null,
     "%22",
     "%23",
     null,
     "%25",
     "%26",
     "%27",
     null,
     null,
     null,
     null,
     "%2C",
     null,
     null,
     "%2F",
     null,
     null,
     null,
     null,
     null,
     null,
     null,
     null,
     null,
     null,
     "%3A",
     null,
     "%3C",
     null,
     "%3E",
     null,
   };
  
  private static void eEncodeValue(StringBuilder result, EFactory eFactory, EDataType eDataType, Object value)
  {
    String stringValue = eFactory.convertToString(eDataType, value);
    if (stringValue == null)
    {
      result.append("null");
    }
    else
    {
      int length = stringValue.length();
      result.ensureCapacity(result.length() + length + 2);
      result.append('\'');
      for (int i = 0; i < length; ++i)
      {
        char character = stringValue.charAt(i);
        if (character < ESCAPE.length)
        {
          String escape = ESCAPE[character];
          if (escape != null)
          {
            result.append(escape);
            continue;
          }
        }
        result.append(character);
      }
      result.append('\'');
    }
  }
  
  /**
   * Returns the encoded value or the original, if no encoding was needed.
   * @see EModelElementImpl#eURIFragmentSegment(EStructuralFeature, EObject)
   * @param value the value to be encoded.
   * @return the encoded value or the original, if no encoding was needed.
   */
  static String eEncodeValue(String value)
  {
    int length = value.length();
    StringBuilder result = null;
    for (int i = 0; i < length; ++i)
    {
      char character = value.charAt(i);
      if (character < ESCAPE.length)
      {
        String escape = ESCAPE[character];
        if (escape != null)
        {
          if (result == null)
          {
            result = new StringBuilder(length + 2);
            result.append(value, 0, i);
          }
          result.append(escape);
          continue;
        }
      }
      if (result != null)
      {
        result.append(character);
      }
    }
    return result == null ? value : result.toString();
  }

  public EObject eObjectForURIFragmentSegment(String uriFragmentSegment)
  {
    int lastIndex = uriFragmentSegment.length() - 1;
    char lastChar = uriFragmentSegment.charAt(lastIndex);
    if (lastChar == ']')
    {      
      int index = uriFragmentSegment.indexOf('[');
      if (index >= 0)
      {
        EReference eReference = eReference(uriFragmentSegment.substring(1, index));
        String predicate = uriFragmentSegment.substring(index + 1, lastIndex);
        return eObjectForURIFragmentPredicate(predicate, eReference);
      }
    }
    else
    {
      int dotIndex = -1;
      if (Character.isDigit(lastChar))
      {
        dotIndex = uriFragmentSegment.lastIndexOf('.', lastIndex - 1);
        if (dotIndex >= 0)
        {
          EList<?> eList = (EList<?>)eGet(eStructuralFeature(uriFragmentSegment.substring(1, dotIndex)), false);
          int position = 0;
          try
          {
            position = Integer.parseInt(uriFragmentSegment.substring(dotIndex + 1));
          }
          catch (NumberFormatException exception)
          {
            throw new WrappedException(exception);
          }
          if (position < eList.size())
          {
            Object result = eList.get(position);
            if (result instanceof FeatureMap.Entry)
            {
              result = ((FeatureMap.Entry)result).getValue();
            }
            return (EObject)result;
          }
        }
      }

      if (dotIndex < 0)
      {
        return (EObject)eGet(eStructuralFeature(uriFragmentSegment.substring(1)), false);      
      }
    }

    return null;
  }

  private EObject eObjectForURIFragmentPredicate(String predicate, EReference eReference)
  {
    ArrayList<FeatureMap.Entry> featureMapEntries = new ArrayList<FeatureMap.Entry>();
    int length = predicate.length();
    EClass eReferenceType = eReference.getEReferenceType();
    for (int i = 0; i < length; ++i)
    {
      int index = predicate.indexOf('=', i);
      EAttribute eAttribute = eAttribute(eReferenceType, predicate.substring(i, index));
      EDataType eDataType = eAttribute.getEAttributeType();
      EFactory eFactory = eDataType.getEPackage().getEFactoryInstance();
      switch (predicate.charAt(++index))
      {
        case '\'':
        {
          int end = predicate.indexOf('\'', ++index);
          addEntry(featureMapEntries, eAttribute, eDecodeValue(predicate.substring(index, end), eFactory, eDataType));
          i = end + 1;
          break;
        }
        case '"':
        {
          int end = predicate.indexOf('"', ++index);
          addEntry(featureMapEntries, eAttribute, eDecodeValue(predicate.substring(index, end), eFactory, eDataType));
          i = end + 1;
          break;
        }
        case '[':
        {
          ArrayList<Object> values = new ArrayList<Object>();
          addEntry(featureMapEntries, eAttribute, values);
          LOOP:
          for (;;)
          {
            switch (predicate.charAt(++index))
            {
              case '\'':
              {
                int end = predicate.indexOf('\'', ++index);
                values.add(eDecodeValue(predicate.substring(index, end), eFactory, eDataType));
                index = end + 1;
                break;
              }
              case '"':
              {
                int end = predicate.indexOf('"', ++index);
                values.add(eDecodeValue(predicate.substring(index, end), eFactory, eDataType));
                index = end + 1;
                break;
              }
              case 'n':
              {
                ++index;
                if (predicate.indexOf("ull", index) == index)
                {
                  values.add(null);
                }
                else
                {
                  throw new RuntimeException("Expecting null");
                }
                index += 3;
                break;
              }
            }
            
            if (index < length)
            {
              switch (predicate.charAt(index))
              {
                case ',':
                {
                  break;
                }
                case ']':
                {
                  break LOOP;
                }
                default:
                {
                  throw new RuntimeException("Expecting , or ]");
                }
              }
            }
            else
            {
              break;
            }
          }
          i = index + 1;
          break;
        }
        case 'n':
        {
          ++index;
          if (predicate.indexOf("ull", index) == index)
          {
            addEntry(featureMapEntries, eAttribute, null);
          }
          else
          {
            throw new RuntimeException("Expecting null");
          }
          i = index + 3;
          break;
        }
      }
      if (i < length)
      {
        if (predicate.charAt(i) != ',')
        {
          throw new RuntimeException("Expecting ,");
        }
      }
      else
      {
        break;
      }
    }
    
    return eObjectForURIFragmentPredicate(featureMapEntries, eReference);
  }
  
  private static final void addEntry(List<FeatureMap.Entry> featureMapEntries, final EAttribute eAttribute, final Object value)
  {
    featureMapEntries.add
      (new FeatureMap.Entry()
       {
         public EStructuralFeature getEStructuralFeature()
         {
           return eAttribute;
         }
 
         public Object getValue()
         {
           return value;
         }
       });
  }

  private static Object eDecodeValue(String encodedValue, EFactory eFactory, EDataType eDataType)
  {
    String literal = URI.decode(encodedValue);
    Object value = eFactory.createFromString(eDataType, literal);
    return value;
  }
  
  private EObject eObjectForURIFragmentPredicate(List<FeatureMap.Entry> predicate, EReference eReference)
  {
    int size = predicate.size();
    @SuppressWarnings("unchecked") EList<EObject> list = ((EList<EObject>)eGet(eReference, false));
    LOOP:
    for (EObject eObject : list)
    {
      for (int i = 0; i < size; ++i)
      {
        FeatureMap.Entry entry = predicate.get(i);
        Object entryValue = entry.getValue();
        EStructuralFeature entryFeature = entry.getEStructuralFeature();
        Object actualValue = eObject.eGet(entryFeature, false);
        if (entryValue == null ? actualValue != null : !entryValue.equals(actualValue))
        {
          continue LOOP;
        }
      }
      return eObject;
    }
    return null;
  }
  
  private EStructuralFeature eStructuralFeature(String name) throws IllegalArgumentException
  {
    EStructuralFeature eStructuralFeature = eClass().getEStructuralFeature(name);
    if (eStructuralFeature == null)
    {
      throw new IllegalArgumentException("The feature '" + name + "' is not a valid feature");
    }
    return eStructuralFeature;
  }

  private EReference eReference(String name) throws IllegalArgumentException
  {
    EStructuralFeature eStructuralFeature = eClass().getEStructuralFeature(name);
    if (eStructuralFeature instanceof EReference)
    {
      return (EReference)eStructuralFeature;
    }
    throw new IllegalArgumentException("The feature '" + name + "' is not a valid reference");
  }
  
  private EAttribute eAttribute(EClass eClass, String name) throws IllegalArgumentException
  {
    EStructuralFeature eStructuralFeature = eClass.getEStructuralFeature(name);
    if (eStructuralFeature instanceof EAttribute)
    {
      return (EAttribute)eStructuralFeature;
    }
    throw new IllegalArgumentException("The feature '" + name + "' is not a valid attribute");
  }

  public boolean eContains(EObject eObject)
  {
    return EcoreUtil.isAncestor(this, eObject);
  }

  public EObject eContainer()
  {
    InternalEObject result = eInternalContainer();
    if (result != null) 
    {
      int eContainerFeatureID = eContainerFeatureID();
      if (result.eIsProxy())
      {
        EObject resolved = eResolveProxy(result);
        if (resolved != result)
        {
          NotificationChain notificationChain = eBasicRemoveFromContainer(null);
          eBasicSetContainer((InternalEObject)resolved, eContainerFeatureID);
          if (notificationChain != null)
          {
            notificationChain.dispatch();
          }
          if (eNotificationRequired() && eContainerFeatureID > EOPPOSITE_FEATURE_BASE)
          {
            eNotify(new ENotificationImpl(this, Notification.RESOLVE, eContainerFeatureID, result, resolved));
          }
          return resolved;
        }
      }
    }
    return result;
  }

  public InternalEObject eInternalContainer()
  {
    throw new UnsupportedOperationException();
    //return eContainer;
  }

  public int eContainerFeatureID()
  {
    throw new UnsupportedOperationException();
    // return eContainerFeatureID;
  }

  protected void eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID)
  {
    throw new UnsupportedOperationException();
    // eContainer = newContainer;
    // eContainerFeatureID = newContainerFeatureID;
  }

  public EList<EObject> eContents()
  {
    EList<EObject> result = eProperties().getEContents();
    if (result == null)
    {
      eBasicProperties().setEContents(result = EContentsEList.createEContentsEList(this));
    }

    return result;
  }

  public EList<EObject> eCrossReferences()
  {
    EList<EObject> result = eProperties().getECrossReferences();
    if (result == null)
    {
      eBasicProperties().setECrossReferences(result = ECrossReferenceEList.createECrossReferenceEList(this));
    }

    return result;
  }

  public TreeIterator<EObject> eAllContents()
  {
    return 
      new AbstractTreeIterator<EObject>(this, false)
      {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<EObject> getChildren(Object object)
        {
          return ((EObject)object).eContents().iterator();
        }
      };
  }

  public EReference eContainmentFeature()
  {
    return eContainmentFeature(this, eInternalContainer(), eContainerFeatureID());
  }
  
  protected static EReference eContainmentFeature(EObject eObject, EObject eContainer, int eContainerFeatureID)
  {
    if (eContainer == null)
    {
      return null;
    }
    else
    {
      if (eContainerFeatureID <= EOPPOSITE_FEATURE_BASE)
      {
        EStructuralFeature eFeature =  eContainer.eClass().getEStructuralFeature(EOPPOSITE_FEATURE_BASE - eContainerFeatureID);
        if (eFeature instanceof EReference)
        {
          return (EReference)eFeature;
        }
        else
        {
          FeatureMap featureMap = (FeatureMap)eContainer.eGet(eFeature);
          for (int i = 0, size = featureMap.size(); i < size; ++i)
          {
            if (featureMap.getValue(i) == eObject)
            {
              EStructuralFeature entryFeature = featureMap.getEStructuralFeature(i);
              if (entryFeature instanceof EReference)
              {
                EReference entryReference = (EReference)entryFeature;
                if (entryReference.isContainment())
                {
                  return entryReference;
                }
              }
            }
          }
          throw new IllegalStateException("The containment feature could not be located");
        }
      }
      else
      {
        return ((EReference)eObject.eClass().getEStructuralFeature(eContainerFeatureID)).getEOpposite();
      }
    }
  }

  public EStructuralFeature eContainingFeature()
  {
    EObject eContainer = eInternalContainer();
    if (eContainer == null)
    {
      return null;
    }
    else
    {
      int eContainerFeatureID = eContainerFeatureID();
      return
        eContainerFeatureID <= EOPPOSITE_FEATURE_BASE ?
          eContainer.eClass().getEStructuralFeature(EOPPOSITE_FEATURE_BASE - eContainerFeatureID) :
          ((EReference)eClass().getEStructuralFeature(eContainerFeatureID)).getEOpposite();
    }
  }

  public Resource.Internal eDirectResource()
  {
    EPropertiesHolder eProperties = eBasicProperties();
    return eProperties == null ? null : eProperties.getEResource();
  }

  public Resource eResource()
  {
    return eInternalResource();
  }

  public Resource.Internal eInternalResource()
  {
    Resource.Internal result = eDirectResource();
    if (result == null)
    {
      int count = 0;
      for (InternalEObject eContainer = eInternalContainer(); eContainer != null; eContainer = eContainer.eInternalContainer())
      {
        // Since the cycle is detected by checking if we hit "this" again, after many iterations we'll call this method recursively 
        // in case we started with something that wasn't part of a cycle but later traversed up to a cycle.
        //
        if (++count > 100000)
        {
          return eContainer.eInternalResource();
        }
        result = eContainer.eDirectResource();
        if (result != null || eContainer == this)
        {
          break;
        }
      }
    }
    return result;
  }

  public NotificationChain eSetResource(Resource.Internal resource, NotificationChain notifications)
  {
    Resource.Internal oldResource = eDirectResource();
    // When setting the resource to null we assume that detach has already been called in the resource implementation
    //
    if (oldResource != null && resource != null)
    {
      notifications = ((InternalEList<?>)oldResource.getContents()).basicRemove(this, notifications);
      oldResource.detached(this);
    }
    InternalEObject oldContainer = eInternalContainer();
    if (oldContainer != null)
    {
      if (eContainmentFeature().isResolveProxies())
      {
        Resource.Internal oldContainerResource = oldContainer.eInternalResource();
        if (oldContainerResource != null)
        {
          // If we're not setting a new resource, attach it to the old container's resource.
          if (resource == null)
          {
            oldContainerResource.attached(this);
          }
          // If we didn't detach it from an old resource already, detach it from the old container's resource.
          //
          else if (oldResource == null)
          {
            oldContainerResource.detached(this);
          }
        }
      }
      else
      {
        notifications = eBasicRemoveFromContainer(notifications);
        notifications = eBasicSetContainer(null, -1, notifications);
      }
    }

    eSetDirectResource(resource);

    return notifications;
  }

  protected void eSetDirectResource(Resource.Internal resource)
  {
    eProperties().setEResource(resource);
  }

  public Object eGet(EStructuralFeature eFeature)
  {
    return eGet(eFeature, true);
  }

  public Object eGet(EStructuralFeature eFeature, boolean resolve)
  {
    return eGet(eFeature, resolve, true);
  }

  public Object eGet(EStructuralFeature eFeature, boolean resolve, boolean coreType)
  {
    int featureID = eDerivedStructuralFeatureID(eFeature);
    if (featureID >= 0)
    {
      return eGet(featureID, resolve, coreType);
    }
    else
    {
      return eOpenGet(eFeature, resolve);
    }
  }

  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    EStructuralFeature eFeature = eClass().getEStructuralFeature(featureID);
    assert eFeature != null : "Invalid featureID: " + featureID;
    int dynamicFeatureID = featureID - eStaticFeatureCount();
      
    return dynamicFeatureID < 0 ?
      eGet(eFeature, resolve) /* backward compatibility with old generated overrides */ : 
      eSettingDelegate(eFeature).dynamicGet(this, eSettings(), dynamicFeatureID, resolve, coreType);
  }
  
  public Object eDynamicGet(EStructuralFeature eFeature, boolean resolve)
  {
    return eDynamicGet(eDynamicFeatureID(eFeature), eFeature, resolve, true);
  }
  
  public Object eDynamicGet(int featureID, boolean resolve, boolean coreType)
  {
    return eDynamicGet(featureID - eStaticFeatureCount(), eClass().getEStructuralFeature(featureID), resolve, coreType);
  }

  protected Object eDynamicGet(int dynamicFeatureID, EStructuralFeature eFeature, boolean resolve, boolean coreType)
  {
    return
      dynamicFeatureID < 0 ?
        eOpenGet(eFeature, resolve) :
        eSettingDelegate(eFeature).dynamicGet(this, eSettings(), dynamicFeatureID, resolve, coreType);
  }

  public Object eOpenGet(EStructuralFeature eFeature, boolean resolve)
  {
    EStructuralFeature openFeature = ExtendedMetaData.INSTANCE.getAffiliation(eClass(), eFeature);
    if (openFeature != null)
    {
      if (!FeatureMapUtil.isFeatureMap(openFeature))
      {
        openFeature = ExtendedMetaData.INSTANCE.getGroup(openFeature);
      }
      FeatureMap featureMap = (FeatureMap)eGet(openFeature);
      return ((FeatureMap.Internal)featureMap).get(eFeature, resolve);
    }
    else
    {
      throw new IllegalArgumentException("The feature '" + eFeature.getName() + "' is not a valid feature");
    }
  }

  public void eSet(EStructuralFeature eFeature, Object newValue) 
  {
    int featureID = eDerivedStructuralFeatureID(eFeature);
    if (featureID >= 0)
    {
      eSet(featureID, newValue);
    }
    else
    {
      eOpenSet(eFeature, newValue);
    }
  }

  public void eSet(int featureID, Object newValue)
  {
    EStructuralFeature eFeature = eClass().getEStructuralFeature(featureID);
    int dynamicFeatureID = featureID - eStaticFeatureCount();
    if (dynamicFeatureID < 0)
    {
      if (eFeature == null)
      {
        throw new IllegalArgumentException("The feature ID" + featureID + " is not a valid feature ID");
      }
      else if (!eFeature.isChangeable())
      {
        throw new IllegalArgumentException("The feature '" + eFeature.getName() + "' is not a valid changeable feature");
      }
      else
      {
        eSet(eFeature, newValue); /* backward compatibility with old generated overrides */ 
      }
    }
    else
    {
      assert eFeature != null : "Invalid featureID: " + featureID;
      eDynamicSet(dynamicFeatureID, eFeature, newValue);
    }
  }

  public void eDynamicSet(EStructuralFeature eFeature, Object newValue) 
  {
    eDynamicSet(eDynamicFeatureID(eFeature), eFeature, newValue);
  }

  public void eDynamicSet(int featureID, Object newValue)
  {
    eDynamicSet(featureID - eStaticFeatureCount(), eClass().getEStructuralFeature(featureID), newValue);
  }

  protected void eDynamicSet(int dynamicFeatureID, EStructuralFeature eFeature, Object newValue)
  {
    if (dynamicFeatureID < 0)
    {
      eOpenSet(eFeature, newValue);
    }
    else
    {
      if (!eFeature.isChangeable())
      {
        throw new IllegalArgumentException("The feature '" + eFeature.getName() + "' is not a valid changeable feature");
      }
      eSettingDelegate(eFeature).dynamicSet(this, eSettings(), dynamicFeatureID, newValue);
    }
  }

  public void eOpenSet(EStructuralFeature eFeature, Object newValue) 
  {
    EStructuralFeature openFeature = ExtendedMetaData.INSTANCE.getAffiliation(eClass(), eFeature);
    if (openFeature != null)
    {
      if (!FeatureMapUtil.isFeatureMap(openFeature))
      {
        openFeature = ExtendedMetaData.INSTANCE.getGroup(openFeature);
      }
      FeatureMap featureMap = (FeatureMap)eGet(openFeature);
      ((FeatureMap.Internal)featureMap).set(eFeature, newValue);
    }
    else
    {
      throw new IllegalArgumentException("The feature '" + eFeature.getName() + "' is not a valid changeable feature");
    }
  }

  public void eUnset(EStructuralFeature eFeature) 
  {
    int featureID = eDerivedStructuralFeatureID(eFeature);
    if (featureID >= 0)
    {
      eUnset(featureID);
    }
    else
    {
      eOpenUnset(eFeature);
    }
  }

  public void eUnset(int featureID)
  {
    EStructuralFeature eFeature = eClass().getEStructuralFeature(featureID);
    int dynamicFeatureID = featureID - eStaticFeatureCount();
    if (dynamicFeatureID < 0)
    {
      if (eFeature == null)
      {
        throw new IllegalArgumentException("The feature ID" + featureID + " is not a valid feature ID");
      }
      else if (!eFeature.isChangeable())
      {
        throw new IllegalArgumentException("The feature '" + eFeature.getName() + "' is not a valid changeable feature");
      }
      else
      {
        eUnset(eFeature); /* backward compatibility with old generated overrides */ 
      }
    }
    else
    {
      assert eFeature != null : "Invalid featureID: " + featureID;
      eDynamicUnset(dynamicFeatureID, eFeature);
    }
  }

  public void eDynamicUnset(EStructuralFeature eFeature) 
  {
    eDynamicUnset(eDynamicFeatureID(eFeature), eFeature);
  }

  public void eDynamicUnset(int featureID)
  {
    eDynamicUnset(featureID - eStaticFeatureCount(), eClass().getEStructuralFeature(featureID));
  }

  protected void eDynamicUnset(int dynamicFeatureID, EStructuralFeature eFeature)
  {
    if (dynamicFeatureID < 0)
    {
      eOpenUnset(eFeature);
    }
    else
    {
      if (!eFeature.isChangeable())
      {
        throw new IllegalArgumentException("The feature '" + eFeature.getName() + "' is not a valid changeable feature");
      }
      eSettingDelegate(eFeature).dynamicUnset(this, eSettings(), dynamicFeatureID);
    }
  }

  public void eOpenUnset(EStructuralFeature eFeature) 
  {
    EStructuralFeature openFeature = ExtendedMetaData.INSTANCE.getAffiliation(eClass(), eFeature);
    if (openFeature != null)
    {
      if (!FeatureMapUtil.isFeatureMap(openFeature))
      {
        openFeature = ExtendedMetaData.INSTANCE.getGroup(openFeature);
      }
      FeatureMap featureMap = (FeatureMap)eGet(openFeature);
      ((FeatureMap.Internal)featureMap).unset(eFeature);
    }
    else
    {
      throw new IllegalArgumentException("The feature '" + eFeature.getName() + "' is not a valid changeable feature");
    }
  }

  public boolean eIsSet(EStructuralFeature eFeature) 
  {
    int featureID = eDerivedStructuralFeatureID(eFeature);
    if (featureID >= 0)
    {
      return eIsSet(featureID);
    }
    else
    {
      return eOpenIsSet(eFeature);
    }
  }

  public boolean eIsSet(int featureID)
  {
    EStructuralFeature eFeature = eClass().getEStructuralFeature(featureID);
    assert eFeature != null : "Invalid featureID: " + featureID;
    int dynamicFeatureID = featureID - eStaticFeatureCount();
      
    return dynamicFeatureID < 0 ?
      eIsSet(eFeature) /* backward compatibility with old generated overrides */ : 
      eDynamicIsSet(dynamicFeatureID, eFeature);
  }

  public boolean eDynamicIsSet(EStructuralFeature eFeature) 
  {
    return eDynamicIsSet(eDynamicFeatureID(eFeature), eFeature);
  }

  public boolean eDynamicIsSet(int featureID)
  {
    return eDynamicIsSet(featureID - eStaticFeatureCount(), eClass().getEStructuralFeature(featureID));
  }

  protected boolean eDynamicIsSet(int dynamicFeatureID, EStructuralFeature eFeature)
  {
    return
      dynamicFeatureID < 0 ?
        eOpenIsSet(eFeature) :
        eSettingDelegate(eFeature).dynamicIsSet(this, eSettings(), dynamicFeatureID);
  }

  public boolean eOpenIsSet(EStructuralFeature eFeature) 
  {
    EStructuralFeature openFeature = ExtendedMetaData.INSTANCE.getAffiliation(eClass(), eFeature);
    if (openFeature != null)
    {
      if (!FeatureMapUtil.isFeatureMap(openFeature))
      {
        openFeature = ExtendedMetaData.INSTANCE.getGroup(openFeature);
      }
      FeatureMap featureMap = (FeatureMap)eGet(openFeature);
      return ((FeatureMap.Internal)featureMap).isSet(eFeature);
    }
    else
    {
      throw new IllegalArgumentException("The feature '" + eFeature.getName() + "' is not a valid feature");
    }
  }

  public NotificationChain eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID, NotificationChain msgs)
  {
    InternalEObject oldContainer = eInternalContainer();
    Resource.Internal oldResource = this.eDirectResource();
    Resource.Internal newResource = null;
    if (oldResource != null)
    {
      if (newContainer != null && !eContainmentFeature(this, newContainer, newContainerFeatureID).isResolveProxies())
      {
        msgs = ((InternalEList<?>)oldResource.getContents()).basicRemove(this, msgs);
        eSetDirectResource(null);
        newResource = newContainer.eInternalResource();
      }
      else
      {
        oldResource = null;
      }
    }
    else 
    {
      if (oldContainer != null)
      {
        oldResource = oldContainer.eInternalResource();
      }
      if (newContainer != null)
      {
        newResource = newContainer.eInternalResource();
      }
    }

    if (oldResource != newResource && oldResource != null)
    {
      oldResource.detached(this);
    }

    int oldContainerFeatureID = eContainerFeatureID();
    eBasicSetContainer(newContainer, newContainerFeatureID);

    if (oldResource != newResource && newResource != null)
    {
      newResource.attached(this);
    }

    if (eNotificationRequired())
    {
      if (oldContainer != null && oldContainerFeatureID >=0 && oldContainerFeatureID != newContainerFeatureID)
      {
        ENotificationImpl notification =
          new ENotificationImpl
           (this,
            Notification.SET,
            oldContainerFeatureID, 
            oldContainer,
            null);
        if (msgs == null)
        {
          msgs = notification;
        }
        else
        {
          msgs.add(notification);
        }
      }
      if (newContainerFeatureID >= 0)
      {
        ENotificationImpl notification =
          new ENotificationImpl
           (this,
            Notification.SET,
            newContainerFeatureID, 
            oldContainerFeatureID == newContainerFeatureID ? oldContainer : null,
            newContainer);
        if (msgs == null)
        {
          msgs = notification;
        }
        else
        {
          msgs.add(notification);
        }
      }
    }
    return msgs;
  }

  public NotificationChain eBasicRemoveFromContainer(NotificationChain msgs)
  {
    int eContainerFeatureID = eContainerFeatureID();
    if (eContainerFeatureID >= 0)
    {
      return eBasicRemoveFromContainerFeature(msgs);
    }
    else 
    {
      return eInternalContainer().eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
    }
  }
  
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    return eDynamicBasicRemoveFromContainer(msgs);
  }

  public NotificationChain eDynamicBasicRemoveFromContainer(NotificationChain msgs)
  {
    EReference inverseFeature = ((EReference)eClass().getEStructuralFeature(eContainerFeatureID())).getEOpposite();
    return eInternalContainer().eInverseRemove(this, inverseFeature.getFeatureID(), inverseFeature.getContainerClass(), msgs);
  }

  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class<?> baseClass, NotificationChain msgs)
  {
    if (featureID >= 0)
    {
      return eInverseAdd(otherEnd, eDerivedStructuralFeatureID(featureID, baseClass), msgs);
    }
    else
    {
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return eBasicSetContainer(otherEnd, featureID, msgs);
    }
  }
  
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    return eDynamicInverseAdd(otherEnd, featureID, msgs);
  }

  public NotificationChain eDynamicInverseAdd(InternalEObject otherEnd, int featureID, Class<?> inverseClass, NotificationChain msgs)
  {
    return eDynamicInverseAdd(otherEnd, featureID, msgs);
  }
  
  protected NotificationChain eDynamicInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    EStructuralFeature.Internal feature = (EStructuralFeature.Internal)eClass().getEStructuralFeature(featureID);
    return feature.getSettingDelegate().dynamicInverseAdd(this, eSettings(), featureID - eStaticFeatureCount(), otherEnd, msgs);
  }

  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class<?> baseClass, NotificationChain msgs)
  {
    if (featureID >= 0)
    {
      return eInverseRemove(otherEnd, eDerivedStructuralFeatureID(featureID, baseClass), msgs);
    }
    else
    {
      return eBasicSetContainer(null, featureID, msgs);
    }
  }
  
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
  }

  public NotificationChain eDynamicInverseRemove(InternalEObject otherEnd, int featureID, Class<?> inverseClass, NotificationChain msgs)
  {
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
  }
  
  protected NotificationChain eDynamicInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    EStructuralFeature.Internal feature = (EStructuralFeature.Internal)eClass().getEStructuralFeature(featureID);
    return feature.getSettingDelegate().dynamicInverseRemove(this, eSettings(), featureID - eStaticFeatureCount(), otherEnd, msgs);
  }

  public URI eProxyURI()
  {
    return eBasicProperties() == null ? null : eBasicProperties().getEProxyURI();
  }

  public void eSetProxyURI(URI uri)
  {
    eProperties().setEProxyURI(uri);
  }

  public EObject eResolveProxy(InternalEObject proxy)
  {
    final EObject result = EcoreUtil.resolve(proxy, this);
    return result;
  }

  public boolean eIsProxy()
  {
    return eBasicProperties() != null && eBasicProperties().getEProxyURI() != null;
  }

  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    return derivedFeatureID;
  }

  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    return baseFeatureID;
  }

  public int eDerivedStructuralFeatureID(EStructuralFeature eStructuralFeature)
  {
    Class<?> containerClass = eStructuralFeature.getContainerClass();
    if (containerClass == null)
    {
      return eClass().getFeatureID(eStructuralFeature);
    }
    else
    {
      assert eClass().getEAllStructuralFeatures().contains(eStructuralFeature) : "The feature '" + eStructuralFeature.getName() + "' is not a valid feature";
      return eDerivedStructuralFeatureID(eStructuralFeature.getFeatureID(), containerClass);
    }
  }

  public int eDerivedOperationID(int baseOperationID, Class<?> baseClass)
  {
    return baseOperationID;
  }

  public int eDerivedOperationID(EOperation eOperation)
  {
    Class<?> containerClass = eOperation.getEContainingClass().getInstanceClass();
    if (containerClass == null)
    {
      EClass eClass = eClass();
      EOperation override = eClass.getOverride(eOperation);
      return eClass.getOperationID(override != null ? override : eOperation);
    }
    else
    {
      assert eClass().getEAllOperations().contains(eOperation) : "The operation '" + eOperation.getName() + "' is not a valid operation";
      return eDerivedOperationID(eOperation.getOperationID(), containerClass);
    }
  }

  public EClass eClass()
  {
    if (eBasicProperties() != null)
    {
      EClass result = eBasicProperties().getEClass();
      if (result != null)
      {
        return result;
      }
    }
    return eStaticClass();
  }

  // Subclasses MUST override this function
  protected EClass eStaticClass()
  {
    return EcorePackage.eINSTANCE.getEObject();
  }

  protected EClass eDynamicClass()
  {
    return 
      eBasicProperties() == null ?
        null : 
        eBasicProperties().getEClass();

  }

  public void eSetClass(EClass eClass)
  {
    eProperties().setEClass(eClass);
  }

  protected EStructuralFeature.Internal.SettingDelegate eSettingDelegate(EStructuralFeature eFeature)
  {
    return ((EStructuralFeature.Internal)eFeature).getSettingDelegate();
  }

  public EStructuralFeature.Setting eSetting(final EStructuralFeature eFeature)
  {
    EClass eClass = eClass();
    int index = eClass.getFeatureID(eFeature);
    int dynamicIndex = eStaticFeatureCount();
    if (index >= dynamicIndex)
    {
      return eSettingDelegate(eFeature).dynamicSetting(this, eSettings(), index - dynamicIndex);
    }
    else if (index <= -1)
    {
      EStructuralFeature openFeature = ExtendedMetaData.INSTANCE.getAffiliation(eClass, eFeature);
      if (openFeature != null)
      {
        if (!FeatureMapUtil.isFeatureMap(openFeature))
        {
          openFeature = ExtendedMetaData.INSTANCE.getGroup(openFeature);
        }
        FeatureMap featureMap = (FeatureMap)eGet(openFeature);
        int upperBound = openFeature.getUpperBound();
        if (upperBound > 1 || upperBound == ETypedElement.UNBOUNDED_MULTIPLICITY)
        {
          return (EStructuralFeature.Setting)((FeatureMap.Internal)featureMap).get(eFeature, false);
        }
      }
      else
      {
        throw new IllegalArgumentException("The feature '" + eFeature.getName() + "' is not a valid feature");
      }
    }
    else if (eFeature.isMany())
    {
      return (EStructuralFeature.Setting)eGet(eFeature, false);
    }

    EStructuralFeature.Setting setting =
      new EStructuralFeature.Setting()
      {
        public EObject getEObject()
        {
          return BasicEObjectImpl.this;
        }

        public EStructuralFeature getEStructuralFeature()
        {
          return eFeature;
        }

        public Object get(boolean resolve)
        {
          return BasicEObjectImpl.this.eGet(eFeature, resolve);
        }

        public void set(Object newValue)
        {
          BasicEObjectImpl.this.eSet(eFeature, newValue);
        }

        public boolean isSet()
        {
          return BasicEObjectImpl.this.eIsSet(eFeature);
        }

        public void unset()
        {
          BasicEObjectImpl.this.eUnset(eFeature);
        }
      };
    return setting;
  }

  protected EOperation.Internal.InvocationDelegate eInvocationDelegate(EOperation eOperation)
  {
    return ((EOperation.Internal)eOperation).getInvocationDelegate();
  }

  public InternalEObject.EStore eStore()
  {
    return null;
  }

  public void eSetStore(InternalEObject.EStore store)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the {@link #eBasicAdapterArray() adapter array} of the {@link #eInternalContainer() container}.
   * @return the adapter array of the container.
   */
  protected Adapter [] eContainerAdapterArray()
  {
    InternalEObject eInternalContainer = eInternalContainer();
    return
      eInternalContainer instanceof BasicEObjectImpl ?
        ((BasicEObjectImpl)eInternalContainer).eBasicAdapterArray() :
        null;
  }

  /**
   * Returns the number of bits that are on in the two's complement bit pattern.
   * This is used to maintain the bit set representing which feature IDs
   * currently have and index allocated in the {@link #eVirtualValues() virtual values}.
   * @param value a two's complement bit pattern.
   * @return the number of bits that are on.
   */
  protected static int eVirtualBitCount(int value)
  {
    value -= value >>> 1 & 0x55555555;
    value = (value & 0x33333333) + (value >>> 2 & 0x33333333);
    value = (value + (value >>> 4)) & 0x0F0F0F0F;
    value += value >>> 8;
    value += value >>> 16;
    return value & 0x3F;
  }

  /**
   * Returns the bit pattern at the given offset within the sequence of bit patterns 
   * representing which features are assigned an index in the {@link #eVirtualValues() virtual values}.
   * @param offset the offset within the bit sequence of bit patterns.
   * @return the bit pattern at the offset.
   */
  protected int eVirtualIndexBits(int offset)
  {
    // return eVirtualIndexBits[offset];
    throw new UnsupportedOperationException();
  }

  /**
   * Sets the bit pattern at the given offset within the sequence of bit patterns 
   * representing which features are assigned an index in the {@link #eVirtualValues() virtual values}.
   * @param offset the offset within the bit sequence of bit patterns.
   * @param newIndexBits the new bit pattern at the offset.
   */
  protected void eSetVirtualIndexBits(int offset, int newIndexBits)
  {
    // eVirtualIndexBits[offset] = newIndexBits;
    throw new UnsupportedOperationException();
  }
  
  /**
   * An action code indicating that an {@link #eVirtualIndex(int, int) index} 
   * needs to be computed to perform a {@link #eVirtualSet(int, Object)} set.
   */
  protected static final int EVIRTUAL_SET = 0;

  /**
   * An action code indicating that an {@link #eVirtualIndex(int, int)} index 
   * needs to be computed to perform an {@link #eVirtualUnset(int) unset}.
   */
  protected static final int EVIRTUAL_UNSET = 1;

  /**
   * An action code indicating that an {@link #eVirtualIndex(int, int) index} 
   * needs to be computed to perform a {@link #eVirtualGet(int) get}.
   */
  protected static final int EVIRTUAL_GET = 2;

  /**
   * An action code indicating that an {@link #eVirtualIndex(int, int) index} 
   * needs to be computed to perform an {@link #eVirtualIsSet(int) isSet}.
   */
  protected static final int EVIRTUAL_IS_SET = 3;
  
  /**
   * Returns the index in the {@link #eVirtualValues() virtual values} for the feature ID,
   * with the side effect of toggling the necessary bits to suit the action code.
   * A result of less than zero indicates that the feature ID is not assigned an index.
   * In the case of a set action, when the value was not previously set,
   * the result will be the complement of the assigned index,
   * which can be corrected by <code>~index</code>.
   * @param eDerivedStructuralFeatureID the ID of a feature of the class.
   * @param action the reason for computing the ID.
   * @return the virtual index.
   */
  protected int eVirtualIndex(int eDerivedStructuralFeatureID, int action)
  {
    // Compute the offset in the sequence of bit patterns for this feature ID
    // and then get the bit pattern at that index.
    //
    int offset = eDerivedStructuralFeatureID >>> 5;
    int bits = eVirtualIndexBits(offset);
    
    // Compute the index within that bit pattern for this feature ID
    // and fetch that bit at that index.
    //
    int bitIndex = eDerivedStructuralFeatureID & 31;
    int bit = bits >>> bitIndex & 1;

    switch (action)
    {
      case EVIRTUAL_IS_SET:
      {
        // For isSet, we only need to check the bit and return -1 when the bit is 0.
        //
        return bit - 1;
      }
      case EVIRTUAL_GET:
      case EVIRTUAL_UNSET:
      {
        if (bit == 0)
        {
          // If the value index set, there's no index to return.
          //
          return -1;
        }
        // Continue to compute the offset.
      }
      case EVIRTUAL_SET:
      default:
      {
        // Depending on the action and the current state, we'll toggle the state.
        // i.e., for unset, we need to turn it off if it's on, 
        // and for set we need to turn it on if it's off.
        //
        if (bit == action)
        {
          eSetVirtualIndexBits(offset, bits ^ (1 << bitIndex));
        }

        // Count just the bits up to this one.
        // Note that shifting 32 bits is a no op.
        //
        int result = eVirtualBitCount(bitIndex == 0 ? 0 : bits << 32 - bitIndex);

        // Count all the bits in the bit patterns up to this one in the sequence of bit patterns.
        //
        for (int i = offset; --i >= 0;)
        {
          result += eVirtualBitCount(eVirtualIndexBits(i));
        }

        // If the index was previously assigned, return it.
        // Otherwise, return a negative result that encodes the newly assigned index.
        //
        return bit != 0 ? result : ~result;
      }
    }
  }

  /**
   * Returns the array of virtual values of the features that are current set.
   * @return the array of virtual values of the features that are current set.
   */
  protected Object[] eVirtualValues()
  {
    // return eVirtualValues;
    throw new UnsupportedOperationException();
  }

  /**
   * Sets the array of virtual values of the features that are current set.
   * @param newValues the new array of virtual values.
   */
  protected void eSetVirtualValues(Object[] newValues)
  {
    // eVirtualValues = newValues;
    throw new UnsupportedOperationException();
  }
  
  /**
   * Returns the value at the index.
   * @param index the {@link #eVirtualIndex(int, int) index} in the {@link #eVirtualValues() virtual values}.
   * @return the value at the index.
   */
  protected Object eVirtualValue(int index)
  {
    return eVirtualValues()[index];
  }
  
  /**
   * Sets the value at the index.
   * @param index the {@link #eVirtualIndex(int, int) index} in the {@link #eVirtualValues() virtual values}.
   * @return the previous value at the index.
   */
  protected Object eSetVirtualValue(int index, Object value)
  {
    Object[] values = eVirtualValues();
    Object oldValue = values[index];
    values[index] = value;
    return oldValue;
  }

  /**
   * This method controls the growth of the {@link #eVirtualValues() virtual values} by returning the new capacity
   * that should be allocated for the given minimum required capacity. Subclasses can override this to be more or
   * less liberal in growth.
   * @param minimumCapacity the minimum number of virtual value entries required.
   * @return the actual number of entries to allocate space for, including a growth factor.
   */
  protected int eComputeVirtualValuesCapacity(int minimumCapacity)
  {
    // return minimumCapacity;
    return minimumCapacity + (minimumCapacity >> 3) + 2;
  }
  
  /**
   * Adds the value at the index.
   * @param index the {@link #eVirtualIndex(int, int) index} in the {@link #eVirtualValues() virtual values}.
   */
  protected void eAddVirtualValue(int index, Object value)
  {
    Object[] values = eVirtualValues();
    if (values == null)
    {
      int newLength = eComputeVirtualValuesCapacity(1);
      values = new Object [newLength];
      values[0] = value;
      for (int i = 1; i < newLength; ++i)
      {
        values[i] = EVIRTUAL_NO_VALUE;
      }
      eSetVirtualValues(values);
    }
    else
    {
      int length = values.length;
      if (values[length - 1] == EVIRTUAL_NO_VALUE)
      {
        if (index + 1 < length)
        {
          System.arraycopy(values, index, values, index + 1, length - index - 1);
        }
  
        values[index] = value;
      }
      else
      {
        int newLength = eComputeVirtualValuesCapacity(length + 1);
        Object[] newValues = new Object [newLength];
  
        for (int i = length; ++i < newLength;)
        {
          newValues[i] = EVIRTUAL_NO_VALUE;
        }
  
        if (index > 0)
        {
          System.arraycopy(values, 0, newValues, 0, index);
        }
  
        if (index < length)
        {
          System.arraycopy(values, index, newValues, index + 1, length - index);
        }
  
        newValues[index] = value;
        eSetVirtualValues(newValues);
      }
    }
  }

  /**
   * Removes the value at the index.
   * @param index the {@link #eVirtualIndex(int, int) index} in the {@link #eVirtualValues() virtual values}.
   * @return the value at the index.
   */
  protected Object eRemoveVirtualValue(int index)
  {
    Object[] values = eVirtualValues();
    Object oldValue = values[index];
    int length = values.length - 1;
    
    if (index == 0 && (length == 0 || values[1] == EVIRTUAL_NO_VALUE))
    {
      eSetVirtualValues(null);
    }
    else
    {
      if (index < length)
      {
        System.arraycopy(values, index + 1, values, index, length - index);
      }
    }
    return oldValue;
  }
  
  /**
   * Returns the value for the feature ID, or <code>null</code>, if there isn't one.
   * @param eDerivedStructuralFeatureID the feature ID to fetch.
   * @return the value for the feature ID.
   */
  public Object eVirtualGet(int eDerivedStructuralFeatureID)
  {
    return eVirtualGet(eDerivedStructuralFeatureID, null);
  }
  
  /**
   * Returns the value for the feature ID, or the default value, if there isn't one.
   * @param eDerivedStructuralFeatureID the feature ID to fetch.
   * @param defaultValue the default value.
   * @return the value for the feature ID.
   */
  public Object eVirtualGet(int eDerivedStructuralFeatureID, Object defaultValue)
  {
    // Determine the index for the feature and return the value at that index, if an index is assigned.
    //
    int index = eVirtualIndex(eDerivedStructuralFeatureID, EVIRTUAL_GET);
    return index < 0 ? defaultValue : eVirtualValue(index);
  }

  /**
   * Returns whether there is a value set for the feature ID.
   * @param eDerivedStructuralFeatureID the feature ID to test.
   * @return whether there is a value set for the feature ID.
   */
  public boolean eVirtualIsSet(int eDerivedStructuralFeatureID)
  {
    // Determine if an index is assigned.
    //
    return eVirtualIndex(eDerivedStructuralFeatureID, EVIRTUAL_IS_SET) >= 0;
  }

  protected static final Object EVIRTUAL_NO_VALUE = new Object();

  /**
   * Sets the value for the feature ID.
   * @param eDerivedStructuralFeatureID the feature ID to set.
   * @return the previous value for the feature ID or {@link #EVIRTUAL_NO_VALUE}.
   */
  public Object eVirtualSet(int eDerivedStructuralFeatureID, Object value)
  {
    // Determine the index.
    //
    int index = eVirtualIndex(eDerivedStructuralFeatureID, EVIRTUAL_SET);
    if (index < 0)
    {
      // If it's newly allocated, add a new value, and indicate there was no previous value.
      //
      eAddVirtualValue(~index, value);
      return EVIRTUAL_NO_VALUE;
    }
    else
    {
      // Set the value at the previously allocated index and return the previous value there.
      //
      return eSetVirtualValue(index, value);
    }
  }
  
  /**
   * Unsets the value for the feature ID.
   * @param eDerivedStructuralFeatureID the feature ID to unset.
   * @return the previous value for the feature ID or {@link #EVIRTUAL_NO_VALUE}.
   */
  public Object eVirtualUnset(int eDerivedStructuralFeatureID)
  {
    int index = eVirtualIndex(eDerivedStructuralFeatureID, EVIRTUAL_UNSET);
    if (index < 0)
    {
      return EVIRTUAL_NO_VALUE;
    }
    else
    {
      return eRemoveVirtualValue(index);
    }
  }
  
  public Object eInvoke(EOperation eOperation, EList<?> arguments) throws InvocationTargetException
  {
    int operationID = eDerivedOperationID(eOperation);
    if (operationID >= 0)
    {
      return eInvoke(operationID, arguments);
    }
    else
    {
      throw new IllegalArgumentException("The operation '" + eOperation.getName() + "' is not a valid operation");
    }
  }

  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    EOperation eOperation = eClass().getEOperation(operationID);
    assert eOperation != null : "Invalid operationID: " + operationID;
      
    return eInvocationDelegate(eOperation).dynamicInvoke(this, arguments);
  }
  
  public Object eDynamicInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    return eDynamicInvoke(eClass().getEOperation(operationID), arguments);
  }

  protected Object eDynamicInvoke(EOperation eOperation, EList<?> arguments) throws InvocationTargetException
  {
    return eInvocationDelegate(eOperation).dynamicInvoke(this, arguments);
  }
  
  @Override
  public String toString()
  {
    StringBuilder result = new StringBuilder(getClass().getName());
    result.append('@');
    result.append(Integer.toHexString(hashCode()));

    if (eIsProxy())
    {
      result.append(" (eProxyURI: ");
      result.append(eProxyURI());
      if (eDynamicClass() != null)
      {
        result.append(" eClass: ");
        result.append(eDynamicClass());
      }
      result.append(')');
    }
    else if (eDynamicClass() != null)
    {
      result.append(" (eClass: ");
      result.append(eDynamicClass());
      result.append(')');
    }

    return result.toString();
  }
}
