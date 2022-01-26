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
package org.eclipse.emf.ecore.impl;


import com.google.gwt.user.client.rpc.GwtTransient;
import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.util.Array;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.InvocationTargetException;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.DelegatingEcoreEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
//import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.ecore.util.InternalEList;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EClass</b></em>'.
 * @extends ESuperAdapter.Holder
 * @ignore
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassImpl#isAbstract <em>Abstract</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassImpl#isInterface <em>Interface</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassImpl#getESuperTypes <em>ESuper Types</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassImpl#getEOperations <em>EOperations</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassImpl#getEAllAttributes <em>EAll Attributes</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassImpl#getEAllReferences <em>EAll References</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassImpl#getEReferences <em>EReferences</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassImpl#getEAttributes <em>EAttributes</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassImpl#getEAllContainments <em>EAll Containments</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassImpl#getEAllOperations <em>EAll Operations</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassImpl#getEAllStructuralFeatures <em>EAll Structural Features</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassImpl#getEAllSuperTypes <em>EAll Super Types</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassImpl#getEIDAttribute <em>EID Attribute</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassImpl#getEStructuralFeatures <em>EStructural Features</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassImpl#getEGenericSuperTypes <em>EGeneric Super Types</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EClassImpl#getEAllGenericSuperTypes <em>EAll Generic Super Types</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EClassImpl extends EClassifierImpl implements EClass, ESuperAdapter.Holder
{
  public interface FeatureSubsetSupplier
  {
    EStructuralFeature [] containments();
    EStructuralFeature [] crossReferences();
    EStructuralFeature [] features();
  }

  @GwtTransient
  protected EAttribute eIDAttribute;
  @GwtTransient
  protected BasicEList<EAttribute> eAllAttributes;
  @GwtTransient
  protected BasicEList<EReference> eAllReferences;
  @GwtTransient
  protected BasicEList<EStructuralFeature> eAllStructuralFeatures;
  @GwtTransient
  protected EStructuralFeature[] eAllStructuralFeaturesData;
  @GwtTransient
  protected BasicEList<EReference> eAllContainments;  
  @GwtTransient
  protected BasicEList<EOperation> eAllOperations;
  @GwtTransient
  protected EOperation[] eAllOperationsData;
  @GwtTransient
  protected BasicEList<EClass> eAllSuperTypes;
  @GwtTransient
  protected BasicEList<EGenericType> eAllGenericSuperTypes;
  @GwtTransient
  protected Map<String, EStructuralFeature> eNameToFeatureMap;
  @GwtTransient
  protected Map<EOperation, EOperation> eOperationToOverrideMap;
  @GwtTransient
  protected ESuperAdapter eSuperAdapter;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EClassImpl()
  {
    super();
  }

  @Override
  protected void freeze()
  {
    getEAllAttributes();
    getEAllReferences();
    getEAllContainments();
    getEAllOperations();
    getEAllStructuralFeatures();
    getEAllSuperTypes();
    getEAllGenericSuperTypes();

    getESuperAdapter().getSubclasses().clear();
    
    if (eStructuralFeatures != null)
    {
      for (int i = 0, size = eStructuralFeatures.size(); i < size; ++i)
      {
        freeze(eStructuralFeatures.get(i));
      }
    }
    if (eOperations != null)
    {
      for (int i = 0, size = eOperations.size(); i < size; ++i)
      {
        freeze(eOperations.get(i));
      }
    }
    super.freeze();
  }
  
  @Override
  public boolean isFrozen()
  {
    return super.isFrozen();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EcorePackage.Literals.ECLASS;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EAttribute getEIDAttribute()
  {
    getEAllAttributes();
    return eIDAttribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<EStructuralFeature> getEStructuralFeatures()
  {
    if (eStructuralFeatures == null)
    {
      eStructuralFeatures = new EObjectContainmentWithInverseEList<EStructuralFeature>(EStructuralFeature.class, this, EcorePackage.ECLASS__ESTRUCTURAL_FEATURES, EcorePackage.ESTRUCTURAL_FEATURE__ECONTAINING_CLASS);
    }
    return eStructuralFeatures;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<EGenericType> getEGenericSuperTypes()
  {
    if (eGenericSuperTypes == null)
    {
      getESuperAdapter();
      eGenericSuperTypes = 
        new EObjectContainmentEList.Unsettable<EGenericType>(EGenericType.class, this, EcorePackage.ECLASS__EGENERIC_SUPER_TYPES)
        {
          private static final long serialVersionUID = 1L;

          @Override
          public boolean isSet()
          {
            for (EGenericType eGenericType : this)
            {
              if (eGenericType.getETypeParameter() != null || !eGenericType.getETypeArguments().isEmpty())
              {
                return true;
              }
            }
            return false;
          }
          
          protected EClass unwrap(EGenericType eGenericType)
          {
            EClassifier result = eGenericType.getERawType();
            if (result instanceof EClass)
            {
              return (EClass)result;
            }
            else
            {
              return EcorePackage.Literals.EOBJECT;
            }
          }

          @Override
          protected boolean hasShadow()
          {
            return true;
          }
          
          @Override
          protected NotificationChain shadowAdd(EGenericType eGenericType, NotificationChain notifications)
          {
            ENotificationImpl notification =
              new ENotificationImpl
                (owner, Notification.ADD, EcorePackage.ECLASS__ESUPER_TYPES, null, unwrap(eGenericType), indexOf(eGenericType), false);
            if (notifications == null)
            {
              notifications = notification;
            }
            else
            {
              notifications.add(notification);
            }
            return notifications;
          }
          
          @Override
          protected NotificationChain shadowRemove(EGenericType eGenericType, NotificationChain notifications)
          {
            ENotificationImpl notification =
              new ENotificationImpl
                (owner, Notification.REMOVE, EcorePackage.ECLASS__ESUPER_TYPES, unwrap(eGenericType), null, indexOf(eGenericType), false);
            if (notifications == null)
            {
              notifications = notification;
            }
            else
            {
              notifications.add(notification);
            }
            return notifications;
          }
          
          @Override
          protected NotificationChain shadowSet(EGenericType oldEGenericType, EGenericType newEGenericType, NotificationChain notifications)
          {
            ENotificationImpl notification =
              new ENotificationImpl
                (owner, 
                 Notification.SET, 
                 EcorePackage.ECLASS__ESUPER_TYPES, 
                 unwrap(oldEGenericType), 
                 unwrap(newEGenericType), 
                 indexOf(oldEGenericType), 
                 false);
            if (notifications == null)
            {
              notifications = notification;
            }
            else
            {
              notifications.add(notification);
            }
            return notifications;
          }
          
          @Override
          public EGenericType move(int targetIndex, int sourceIndex)
          {
            EGenericType result = super.move(targetIndex, sourceIndex);
            if (isNotificationRequired())
            {
              dispatchNotification
                (new ENotificationImpl
                   (EClassImpl.this,
                    Notification.MOVE, 
                    EcorePackage.Literals.ECLASS__ESUPER_TYPES,
                    sourceIndex,
                    unwrap(result), 
                    targetIndex));
            }
            return result;
          }

          @Override
          public void unset()
          {
            // Don't really unset it.
            clear();
          }

          @Override
          protected NotificationImpl createNotification(int eventType, Object oldObject, Object newObject, int index, boolean wasSet)
          {
            switch (eventType)
            {
              case Notification.ADD:
              {
                return super.createNotification(eventType, oldObject, newObject, index, size > 1);
              }
              case Notification.ADD_MANY:
              {
                return super.createNotification(eventType, oldObject, newObject, index, size - ((List<?>)newObject).size() > 0);
              }
              default:
              {
                return super.createNotification(eventType, oldObject, newObject, index, true);
              }
            }
          }
        };
      getESuperTypes();
    }
    return eGenericSuperTypes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void unsetEGenericSuperTypes()
  {
    if (eGenericSuperTypes != null) ((InternalEList.Unsettable<?>)eGenericSuperTypes).unset();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isSetEGenericSuperTypes()
  {
    return eGenericSuperTypes != null && ((InternalEList.Unsettable<?>)eGenericSuperTypes).isSet();
  }

  static class MyHashSet extends HashSet<EClass>
  {
    private static final long serialVersionUID = 1L;

    public MyHashSet get()
    {
      return this;
    }
  }
  
  private static final MyHashSet COMPUTATION_IN_PROGRESS = new MyHashSet();
      
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<EGenericType> getEAllGenericSuperTypes()
  {
    if (eAllGenericSuperTypes == null)
    {
      class EGenericSuperTypeEList extends UniqueEList<EGenericType>
      {
        private static final long serialVersionUID = 1L;
 
        @Override
        protected Object [] newData(int capacity)
        {
          return new EGenericType [capacity];
        }
 
        @Override
        protected boolean useEquals()
        {
          return false;
        }

        public void eliminateEquivalentDuplicates()
        {
          EGenericType [] eGenericTypes  = (EGenericType[])data;
          for (int i = size - 1; i >= 0; --i)
          {
            EGenericType eGenericType = eGenericTypes[i];
            for (int j = 0; j < i; ++j)
            {
              EGenericType otherEGenericType = eGenericTypes[j];
              if (equivalent(eGenericType, otherEGenericType))
              {
                remove(i);
                break;
              }
            }
          }
        }

        public boolean equivalent(EGenericType eGenericType, EGenericType otherEGenericType)
        {
          if (eGenericType == otherEGenericType)
          {
            return true;
          }
          else
          {
            eGenericType = resolve(eGenericType);
            otherEGenericType = resolve(otherEGenericType);
            EClassifier eClassifier = eGenericType.getEClassifier();
            if (eClassifier != null)
            {
              EClassifier otherEClassifier = otherEGenericType.getEClassifier();
              if (otherEClassifier != eClassifier)
              {
                if (otherEClassifier == null)
                {
                  return false;
                }
                else
                { 
                  String instanceTypeName = eClassifier.getInstanceTypeName();
                  String otherInstanceTypeName = otherEClassifier.getInstanceTypeName();
                  return instanceTypeName == otherInstanceTypeName && instanceTypeName != null;
                }
              }
              else
              {
                EList<EGenericType> eTypeArguments = eGenericType.getETypeArguments();
                int eTypeArgumentSize = eTypeArguments.size();
                EList<EGenericType> otherETypeArguments = otherEGenericType.getETypeArguments();
                if (eTypeArgumentSize == otherETypeArguments.size())
                {
                  for (int j = 0; j < eTypeArgumentSize; ++j)
                  {
                    EGenericType eTypeArgument = eTypeArguments.get(j);
                    EGenericType otherETypeArgument = otherETypeArguments.get(j);
                    if (!equivalent(eTypeArgument, otherETypeArgument))
                    {
                      return false;
                    }
                  }
                }
                return true;
              }
            }
            else
            {
              ETypeParameter eTypeParameter = eGenericType.getETypeParameter();
              ETypeParameter otherETypeParameter = otherEGenericType.getETypeParameter();
              return eTypeParameter == otherETypeParameter;
            }
          }
        }
 
        public EGenericType resolve(EGenericType eGenericType)
        {
          ETypeParameter eTypeParameter = eGenericType.getETypeParameter();
          if (eTypeParameter != null)
          {
            EObject eContainer = eTypeParameter.eContainer();
            EGenericType [] eGenericTypes  = (EGenericType[])data;
            for (int i = 0; i < size; ++i)
            {
              EGenericType otherEGenericType = eGenericTypes[i];
              if (otherEGenericType.getEClassifier() == eContainer)
              {
                EList<EGenericType> eTypeArguments = otherEGenericType.getETypeArguments();
                int index = ((List<?>)eContainer.eGet(eTypeParameter.eContainmentFeature())).indexOf(eTypeParameter);
                if (index < eTypeArguments.size())
                {
                  return resolve(eTypeArguments.get(index));
                }
              }
            }
          }
          return eGenericType;
        }
      }
      EGenericSuperTypeEList  result = new EGenericSuperTypeEList();

      Set<EClass> computationInProgress = COMPUTATION_IN_PROGRESS.get();
      if (computationInProgress.add(this))
      {
        for (EGenericType eGenericSuperType : getEGenericSuperTypes())
        {
          EClassifier eSuperType = eGenericSuperType.getERawType();
          if (eSuperType instanceof EClass)
          {
            result.addAll(((EClass)eSuperType).getEAllGenericSuperTypes());
          }
          result.add(eGenericSuperType);
        }
        computationInProgress.remove(this);
      }

      result.eliminateEquivalentDuplicates();
      
      result.shrink();
      eAllGenericSuperTypes = 
        new EcoreEList.UnmodifiableEList.FastCompare<EGenericType>
          (this, EcorePackage.eINSTANCE.getEClass_EAllGenericSuperTypes(), result.size(), result.data());
      getESuperAdapter().setAllSuperCollectionModified(false);
    }

    return eAllGenericSuperTypes;
  }

  public EList<EAttribute> getEAllAttributes()
  {
    if (eAllAttributes == null)
    {
      eIDAttribute = null;

      BasicEList<EAttribute> result =
        new UniqueEList<EAttribute>()
        {
          private static final long serialVersionUID = 1L;

          @Override
          protected Object [] newData(int capacity)
          {
            return new EAttribute [capacity];
          }

          @Override
          protected boolean useEquals()
          {
            return false;
          }

          @Override
          protected void didAdd(int index, EAttribute eAttribute)
          {
            if (eAttribute.isID() && eIDAttribute == null) 
            {
              eIDAttribute = eAttribute;
            }
          }
        };

      BasicEList<EAttribute> attributes = 
        new UniqueEList<EAttribute>()
        {
          private static final long serialVersionUID = 1L;

          @Override
          protected Object [] newData(int capacity)
          {
            return new EAttribute [capacity];
          }

          @Override
          protected boolean useEquals()
          {
            return false;
          }
        };

      Set<EClass> computationInProgress = COMPUTATION_IN_PROGRESS.get();
      if (computationInProgress.add(this))
      {
        for (EClass eSuperType : getESuperTypes())
        {
          result.addAll(eSuperType.getEAllAttributes());
        }
        computationInProgress.remove(this);
      }
      for (EStructuralFeature eStructuralFeature : getEStructuralFeatures())
      {
        if (eStructuralFeature instanceof EAttribute)
        {
          attributes.add((EAttribute)eStructuralFeature);
        }
      }

      attributes.shrink();
      eAttributes = 
        new EcoreEList.UnmodifiableEList.FastCompare<EAttribute>
          (this, EcorePackage.eINSTANCE.getEClass_EAttributes(), attributes.size(), attributes.data())
        {
          private static final long serialVersionUID = 1L;

          @SuppressWarnings("unchecked")
          @Override
          public void addUnique(EAttribute object)
          {
            ((InternalEList<EAttribute>)(InternalEList<?>)getEStructuralFeatures()).addUnique(object);
          }

          @Override
          public boolean add(EAttribute object)
          {
            System.err.println("Please fix your code to add using EClass.getEStructuralFeatures() instead of EClass.getEAttributes()");
            return getEStructuralFeatures().add(object);
          }
        };

      result.addAll(eAttributes);
      result.shrink();
      eAllAttributes = 
        new EcoreEList.UnmodifiableEList.FastCompare<EAttribute>
          (this, EcorePackage.eINSTANCE.getEClass_EAllAttributes(), result.size(), result.data());
      getESuperAdapter().setAllAttributesCollectionModified(false);
    }

    return eAllAttributes;
  }

  public EList<EReference> getEAllReferences()
  {
    if (eAllReferences == null)
    {
      class ReferenceList extends UniqueEList<EReference>
      {
        private static final long serialVersionUID = 1L;

        public ReferenceList()
        {
          super();
        }

        @Override
        protected Object [] newData(int capacity)
        {
          return new EReference [capacity];
        }

        @Override
        protected boolean useEquals()
        {
          return false;
        }
      }
      BasicEList<EReference> result = new ReferenceList();
      BasicEList<EReference> references = new ReferenceList();

      Set<EClass> computationInProgress = COMPUTATION_IN_PROGRESS.get();
      if (computationInProgress.add(this))
      {
        for (EClass eSuperType : getESuperTypes())
        {
          result.addAll(eSuperType.getEAllReferences());
        }
        computationInProgress.remove(this);
      }
      for (EStructuralFeature eStructuralFeature : getEStructuralFeatures())
      {
        if (eStructuralFeature instanceof EReference)
        {
          references.add((EReference)eStructuralFeature);
        }
      }

      references.shrink();
      eReferences = 
        new EcoreEList.UnmodifiableEList.FastCompare<EReference>
          (this, EcorePackage.eINSTANCE.getEClass_EReferences(), references.size(), references.data())
        {
          private static final long serialVersionUID = 1L;

          @SuppressWarnings("unchecked")
          @Override
          public void addUnique(EReference object)
          {
            ((InternalEList<EReference>)(InternalEList<?>)getEStructuralFeatures()).addUnique(object);
          }

          @Override
          public boolean add(EReference object)
          {
            System.err.println("Please fix your code to add using EClass.getEStructuralFeatures() instead of EClass.getEReferences()");
            return getEStructuralFeatures().add(object);
          }
        };

      result.addAll(eReferences);
      result.shrink();
      eAllReferences = 
        new EcoreEList.UnmodifiableEList.FastCompare<EReference>
          (this, EcorePackage.eINSTANCE.getEClass_EAllReferences(), result.size(), result.data());
      getESuperAdapter().setAllReferencesCollectionModified(false);
    }

    return eAllReferences;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<EReference> getEReferences()
  {
    getEAllReferences();
    return eReferences;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<EAttribute> getEAttributes()
  {
    getEAllAttributes();
    return eAttributes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated modifiable
   */
  public EList<EStructuralFeature> getEAllStructuralFeatures() 
  {
    // The algorithm for the order of the features in this list should never change.
    // Also, the fact that a new list is created whenever the contents change 
    // is something else that should never change.
    // There are clients who rely on both these behaviors, 
    // and they will need to agree to any change,
    // so that they can adjust their own code.
    //
    if (eAllStructuralFeatures == null)
    {
      class EStructuralFeatureUniqueEList extends UniqueEList<EStructuralFeature>
      {
        private static final long serialVersionUID = 1L;

        @Override
        protected Object [] newData(int capacity)
        {
          return new EStructuralFeature [capacity];
        }

        @Override
        protected boolean useEquals()
        {
          return false;
        }
      }

      BasicEList<EStructuralFeature> result = new EStructuralFeatureUniqueEList();

      Set<EClass> computationInProgress = COMPUTATION_IN_PROGRESS.get();
      if (computationInProgress.add(this))
      {
        for (EClass eSuperType : getESuperTypes())
        {
          result.addAll(eSuperType.getEAllStructuralFeatures());
        }
        computationInProgress.remove(this);
      }
      int featureID = result.size();
      for (Iterator<EStructuralFeature> i = getEStructuralFeatures().iterator(); i.hasNext(); ++featureID)
      {
        ((EStructuralFeatureImpl)i.next()).setFeatureID(featureID);
      }
      result.addAll(getEStructuralFeatures());

      class EAllStructuralFeaturesList extends EcoreEList.UnmodifiableEList.FastCompare<EStructuralFeature> implements FeatureSubsetSupplier
      {
        private static final long serialVersionUID = 1L;

        protected EStructuralFeature [] containments;
        protected EStructuralFeature [] crossReferences;

        public EAllStructuralFeaturesList(BasicEList<EStructuralFeature> eAllStructuralFeatures)
        {
          super
            (EClassImpl.this, 
             EcorePackage.eINSTANCE.getEClass_EAllStructuralFeatures(), 
             eAllStructuralFeatures.size(), 
             eAllStructuralFeatures.data());
        }

        private void init()
        {
          BasicEList<EStructuralFeature> containmentsList = new EStructuralFeatureUniqueEList();
          BasicEList<EStructuralFeature> crossReferencesList = new EStructuralFeatureUniqueEList();
          boolean isMixed = "mixed".equals(EcoreUtil.getAnnotation(EClassImpl.this, ExtendedMetaData.ANNOTATION_URI, "kind"));
          for (int i = 0;  i < size; ++i)
          {
            // Skip derived features.
            //
            EStructuralFeature eStructuralFeature = (EStructuralFeature)data[i];
            if (eStructuralFeature instanceof EReference)
            {
              EReference eReference = (EReference)eStructuralFeature;
              if (eReference.isContainment())
              {
                // Include derived relations only if they won't also come from mixed or a group.
                //
                if (!eReference.isDerived() || 
                      !isMixed && EcoreUtil.getAnnotation(eReference, ExtendedMetaData.ANNOTATION_URI, "group") == null)
                {
                  containmentsList.add(eReference);
                }
              }
              else if (!eReference.isContainer())
              {
                // Include derived relations only if they won't also come from mixed or a group.
                //
                if (!eReference.isDerived() || 
                      !isMixed && EcoreUtil.getAnnotation(eReference, ExtendedMetaData.ANNOTATION_URI, "group") == null)
                {
                  crossReferencesList.add(eReference);
                }
              }
            }
            else if (FeatureMapUtil.isFeatureMap(eStructuralFeature))
            {
              if (!eStructuralFeature.isDerived())
              {
                containmentsList.add(eStructuralFeature);
                crossReferencesList.add(eStructuralFeature);
              }
            }
          }
          containmentsList.shrink();
          crossReferencesList.shrink();
          containments = (EStructuralFeature [])containmentsList.data();
          crossReferences = (EStructuralFeature [])crossReferencesList.data();
        }

        public EStructuralFeature [] containments()
        {
          if (containments == null)
          {
            init();
          }
          return containments;
        }

        public EStructuralFeature [] crossReferences()
        {
          if (crossReferences == null)
          {
            init();
          }
          return crossReferences;
        }

        public EStructuralFeature [] features()
        {
          return (EStructuralFeature [])data;
        }

        @Override
        public int indexOf(Object object)
        {
          if (object instanceof EStructuralFeature)
          {
            EStructuralFeature eStructuralFeature = (EStructuralFeature)object;
            int index = eStructuralFeature.getFeatureID();
            if (index != -1)
            {
              for (int last = this.size; index < last; ++index)
              {
                if (data[index] == object)
                {
                  return index;
                }
              }
            }
          }
          return -1;
        }
      }

      result.shrink();
      eAllStructuralFeatures = new EAllStructuralFeaturesList(result);
      eAllStructuralFeaturesData = (EStructuralFeature[])result.data();
      if (eAllStructuralFeaturesData == null)
      {
        eAllStructuralFeaturesData = NO_EALL_STRUCTURE_FEATURES_DATA;
      }

      eNameToFeatureMap = null; 
      
      getESuperAdapter().setAllStructuralFeaturesCollectionModified(false);
    }

    return eAllStructuralFeatures;
  }
  
  private static final EStructuralFeature[] NO_EALL_STRUCTURE_FEATURES_DATA = {};

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated modifiable
   */
  public EList<EOperation> getEAllOperations()
  {
    if (eAllOperations == null)
    {
      BasicEList<EOperation> result = 
        new UniqueEList<EOperation>()
        {
          private static final long serialVersionUID = 1L;

          @Override
          protected Object [] newData(int capacity)
          {
            return new EOperation [capacity];
          }

          @Override
          protected boolean useEquals()
          {
            return false;
          }
        };

      Set<EClass> computationInProgress = COMPUTATION_IN_PROGRESS.get();
      if (computationInProgress.add(this))
      {
        for (EClass eSuperType : getESuperTypes())
        {
          result.addAll(eSuperType.getEAllOperations());
        }
        computationInProgress.remove(this);
      }
      int operationID = result.size();
      for (Iterator<EOperation> i = getEOperations().iterator(); i.hasNext(); ++operationID)
      {
        ((EOperationImpl)i.next()).setOperationID(operationID);
      }
      result.addAll(getEOperations());
      result.shrink();
      eAllOperations = 
        new EcoreEList.UnmodifiableEList.FastCompare<EOperation>
          (this, EcorePackage.eINSTANCE.getEClass_EAllOperations(), result.size(), result.data());
      eAllOperationsData = (EOperation[])result.data();
      if (eAllOperationsData == null)
      {
        eAllOperationsData = NO_EALL_OPERATIONS_DATA;
      }

      eOperationToOverrideMap = null; 

      getESuperAdapter().setAllOperationsCollectionModified(false);
    }

    return eAllOperations;
  }

  private static final EOperation[] NO_EALL_OPERATIONS_DATA = {};

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
      case EcorePackage.ECLASS__EANNOTATIONS:
        return getEAnnotations();
      case EcorePackage.ECLASS__NAME:
        return getName();
      case EcorePackage.ECLASS__INSTANCE_CLASS_NAME:
        return getInstanceClassName();
      case EcorePackage.ECLASS__INSTANCE_CLASS:
        return getInstanceClass();
      case EcorePackage.ECLASS__DEFAULT_VALUE:
        return getDefaultValue();
      case EcorePackage.ECLASS__INSTANCE_TYPE_NAME:
        return getInstanceTypeName();
      case EcorePackage.ECLASS__EPACKAGE:
        if (resolve) return getEPackage();
        return basicGetEPackage();
      case EcorePackage.ECLASS__ETYPE_PARAMETERS:
        return getETypeParameters();
      case EcorePackage.ECLASS__ABSTRACT:
        return isAbstract();
      case EcorePackage.ECLASS__INTERFACE:
        return isInterface();
      case EcorePackage.ECLASS__ESUPER_TYPES:
        return getESuperTypes();
      case EcorePackage.ECLASS__EOPERATIONS:
        return getEOperations();
      case EcorePackage.ECLASS__EALL_ATTRIBUTES:
        return getEAllAttributes();
      case EcorePackage.ECLASS__EALL_REFERENCES:
        return getEAllReferences();
      case EcorePackage.ECLASS__EREFERENCES:
        return getEReferences();
      case EcorePackage.ECLASS__EATTRIBUTES:
        return getEAttributes();
      case EcorePackage.ECLASS__EALL_CONTAINMENTS:
        return getEAllContainments();
      case EcorePackage.ECLASS__EALL_OPERATIONS:
        return getEAllOperations();
      case EcorePackage.ECLASS__EALL_STRUCTURAL_FEATURES:
        return getEAllStructuralFeatures();
      case EcorePackage.ECLASS__EALL_SUPER_TYPES:
        return getEAllSuperTypes();
      case EcorePackage.ECLASS__EID_ATTRIBUTE:
        return getEIDAttribute();
      case EcorePackage.ECLASS__ESTRUCTURAL_FEATURES:
        return getEStructuralFeatures();
      case EcorePackage.ECLASS__EGENERIC_SUPER_TYPES:
        return getEGenericSuperTypes();
      case EcorePackage.ECLASS__EALL_GENERIC_SUPER_TYPES:
        return getEAllGenericSuperTypes();
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
      case EcorePackage.ECLASS__EANNOTATIONS:
        getEAnnotations().clear();
        getEAnnotations().addAll((Collection<? extends EAnnotation>)newValue);
        return;
      case EcorePackage.ECLASS__NAME:
        setName((String)newValue);
        return;
      case EcorePackage.ECLASS__INSTANCE_CLASS_NAME:
        setInstanceClassName((String)newValue);
        return;
      case EcorePackage.ECLASS__INSTANCE_TYPE_NAME:
        setInstanceTypeName((String)newValue);
        return;
      case EcorePackage.ECLASS__ETYPE_PARAMETERS:
        getETypeParameters().clear();
        getETypeParameters().addAll((Collection<? extends ETypeParameter>)newValue);
        return;
      case EcorePackage.ECLASS__ABSTRACT:
        setAbstract((Boolean)newValue);
        return;
      case EcorePackage.ECLASS__INTERFACE:
        setInterface((Boolean)newValue);
        return;
      case EcorePackage.ECLASS__ESUPER_TYPES:
        getESuperTypes().clear();
        getESuperTypes().addAll((Collection<? extends EClass>)newValue);
        return;
      case EcorePackage.ECLASS__EOPERATIONS:
        getEOperations().clear();
        getEOperations().addAll((Collection<? extends EOperation>)newValue);
        return;
      case EcorePackage.ECLASS__ESTRUCTURAL_FEATURES:
        getEStructuralFeatures().clear();
        getEStructuralFeatures().addAll((Collection<? extends EStructuralFeature>)newValue);
        return;
      case EcorePackage.ECLASS__EGENERIC_SUPER_TYPES:
        getEGenericSuperTypes().clear();
        getEGenericSuperTypes().addAll((Collection<? extends EGenericType>)newValue);
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
      case EcorePackage.ECLASS__EANNOTATIONS:
        getEAnnotations().clear();
        return;
      case EcorePackage.ECLASS__NAME:
        setName(NAME_EDEFAULT);
        return;
      case EcorePackage.ECLASS__INSTANCE_CLASS_NAME:
        unsetInstanceClassName();
        return;
      case EcorePackage.ECLASS__INSTANCE_TYPE_NAME:
        unsetInstanceTypeName();
        return;
      case EcorePackage.ECLASS__ETYPE_PARAMETERS:
        getETypeParameters().clear();
        return;
      case EcorePackage.ECLASS__ABSTRACT:
        setAbstract(ABSTRACT_EDEFAULT);
        return;
      case EcorePackage.ECLASS__INTERFACE:
        setInterface(INTERFACE_EDEFAULT);
        return;
      case EcorePackage.ECLASS__ESUPER_TYPES:
        unsetESuperTypes();
        return;
      case EcorePackage.ECLASS__EOPERATIONS:
        getEOperations().clear();
        return;
      case EcorePackage.ECLASS__ESTRUCTURAL_FEATURES:
        getEStructuralFeatures().clear();
        return;
      case EcorePackage.ECLASS__EGENERIC_SUPER_TYPES:
        unsetEGenericSuperTypes();
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
      case EcorePackage.ECLASS__EANNOTATIONS:
        return eAnnotations != null && !eAnnotations.isEmpty();
      case EcorePackage.ECLASS__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case EcorePackage.ECLASS__INSTANCE_CLASS_NAME:
        return isSetInstanceClassName();
      case EcorePackage.ECLASS__INSTANCE_CLASS:
        return getInstanceClass() != null;
      case EcorePackage.ECLASS__DEFAULT_VALUE:
        return DEFAULT_VALUE_EDEFAULT == null ? getDefaultValue() != null : !DEFAULT_VALUE_EDEFAULT.equals(getDefaultValue());
      case EcorePackage.ECLASS__INSTANCE_TYPE_NAME:
        return isSetInstanceTypeName();
      case EcorePackage.ECLASS__EPACKAGE:
        return basicGetEPackage() != null;
      case EcorePackage.ECLASS__ETYPE_PARAMETERS:
        return eTypeParameters != null && !eTypeParameters.isEmpty();
      case EcorePackage.ECLASS__ABSTRACT:
        return ((eFlags & ABSTRACT_EFLAG) != 0) != ABSTRACT_EDEFAULT;
      case EcorePackage.ECLASS__INTERFACE:
        return ((eFlags & INTERFACE_EFLAG) != 0) != INTERFACE_EDEFAULT;
      case EcorePackage.ECLASS__ESUPER_TYPES:
        return isSetESuperTypes();
      case EcorePackage.ECLASS__EOPERATIONS:
        return eOperations != null && !eOperations.isEmpty();
      case EcorePackage.ECLASS__EALL_ATTRIBUTES:
        return !getEAllAttributes().isEmpty();
      case EcorePackage.ECLASS__EALL_REFERENCES:
        return !getEAllReferences().isEmpty();
      case EcorePackage.ECLASS__EREFERENCES:
        return !getEReferences().isEmpty();
      case EcorePackage.ECLASS__EATTRIBUTES:
        return !getEAttributes().isEmpty();
      case EcorePackage.ECLASS__EALL_CONTAINMENTS:
        return !getEAllContainments().isEmpty();
      case EcorePackage.ECLASS__EALL_OPERATIONS:
        return !getEAllOperations().isEmpty();
      case EcorePackage.ECLASS__EALL_STRUCTURAL_FEATURES:
        return !getEAllStructuralFeatures().isEmpty();
      case EcorePackage.ECLASS__EALL_SUPER_TYPES:
        return !getEAllSuperTypes().isEmpty();
      case EcorePackage.ECLASS__EID_ATTRIBUTE:
        return getEIDAttribute() != null;
      case EcorePackage.ECLASS__ESTRUCTURAL_FEATURES:
        return eStructuralFeatures != null && !eStructuralFeatures.isEmpty();
      case EcorePackage.ECLASS__EGENERIC_SUPER_TYPES:
        return isSetEGenericSuperTypes();
      case EcorePackage.ECLASS__EALL_GENERIC_SUPER_TYPES:
        return !getEAllGenericSuperTypes().isEmpty();
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
      case EcorePackage.ECLASS___GET_EANNOTATION__STRING:
        return getEAnnotation((String)arguments.get(0));
      case EcorePackage.ECLASS___IS_INSTANCE__OBJECT:
        return isInstance(arguments.get(0));
      case EcorePackage.ECLASS___GET_CLASSIFIER_ID:
        return getClassifierID();
      case EcorePackage.ECLASS___IS_SUPER_TYPE_OF__ECLASS:
        return isSuperTypeOf((EClass)arguments.get(0));
      case EcorePackage.ECLASS___GET_FEATURE_COUNT:
        return getFeatureCount();
      case EcorePackage.ECLASS___GET_ESTRUCTURAL_FEATURE__INT:
        return getEStructuralFeature((Integer)arguments.get(0));
      case EcorePackage.ECLASS___GET_FEATURE_ID__ESTRUCTURALFEATURE:
        return getFeatureID((EStructuralFeature)arguments.get(0));
      case EcorePackage.ECLASS___GET_ESTRUCTURAL_FEATURE__STRING:
        return getEStructuralFeature((String)arguments.get(0));
      case EcorePackage.ECLASS___GET_OPERATION_COUNT:
        return getOperationCount();
      case EcorePackage.ECLASS___GET_EOPERATION__INT:
        return getEOperation((Integer)arguments.get(0));
      case EcorePackage.ECLASS___GET_OPERATION_ID__EOPERATION:
        return getOperationID((EOperation)arguments.get(0));
      case EcorePackage.ECLASS___GET_OVERRIDE__EOPERATION:
        return getOverride((EOperation)arguments.get(0));
    }
    return eDynamicInvoke(operationID, arguments);
  }

  public EList<EReference> getEAllContainments()
  {
    if (eAllContainments == null)
    {
      BasicEList<EReference> result = 
        new UniqueEList<EReference>()
        {
          private static final long serialVersionUID = 1L;

          @Override
          protected Object [] newData(int capacity)
          {
            return new EReference [capacity];
          }

          @Override
          protected boolean useEquals()
          {
            return false;
          }
        };

      for (EReference eReference : getEAllReferences())
      {
        if (eReference.isContainment())
        {
          result.add(eReference);
        }
      }

      result.shrink();
      eAllContainments = 
        new EcoreEList.UnmodifiableEList.FastCompare<EReference>
          (this, EcorePackage.eINSTANCE.getEClass_EAllContainments(), result.size(), result.data()); 
      getESuperAdapter().setAllContainmentsCollectionModified(false);
    }

    return eAllContainments;
  }

  public EStructuralFeature getEStructuralFeature(String name)
  {
    getFeatureCount();
    if (eNameToFeatureMap == null)
    {
      Map<String, EStructuralFeature> result = new HashMap<String, EStructuralFeature>(3 * eAllStructuralFeatures.size() / 2 + 1);
      for (EStructuralFeature eStructuralFeature : eAllStructuralFeatures)
      {
        result.put(eStructuralFeature.getName(), eStructuralFeature);
      }
      eNameToFeatureMap = result;
    }
    return eNameToFeatureMap.get(name);
  }

  protected EOperation[] getEAllOperationsData()
  {
    if (eAllOperationsData == null)
    {
      getEAllOperations();
    }
    return eAllOperationsData;
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public int getOperationCount()
  {
    return getEAllOperationsData().length;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EOperation getEOperation(int operationID)
  {
    EOperation [] eAllOperationsData  = getEAllOperationsData();
    return 
      operationID >= 0 && operationID < eAllOperationsData.length ? 
        eAllOperationsData[operationID] : 
        null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public int getOperationID(EOperation operation)
  {
    EOperation [] eAllOperationsData  = getEAllOperationsData();
    int index = operation.getOperationID();
    if (index != -1)
    {
      for (int last = eAllOperationsData.length; index < last; ++index)
      {
        if (eAllOperationsData[index] == operation)
        {
          return index;
        }
      }
    }
    return -1;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EOperation getOverride(EOperation operation)
  {
    EOperation [] eAllOperationsData  = getEAllOperationsData();
    if (eOperationToOverrideMap == null)
    {
      Map<EOperation, EOperation> result = new HashMap<EOperation, EOperation>();
      int length = eAllOperationsData.length;
      for (int i = 0; i < length; ++i)
      {
        for (int j = length - 1; j > i; --j)
        {
          if (eAllOperationsData[j].isOverrideOf(eAllOperationsData[i]))
          {
            result.put(eAllOperationsData[i], eAllOperationsData[j]);
            break;
          }
        }
      }
      eOperationToOverrideMap = result;
    }
    return eOperationToOverrideMap.get(operation);
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
      case EcorePackage.ECLASS__EANNOTATIONS:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getEAnnotations()).basicAdd(otherEnd, msgs);
      case EcorePackage.ECLASS__EPACKAGE:
        if (eInternalContainer() != null)
          msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, EcorePackage.ECLASS__EPACKAGE, msgs);
      case EcorePackage.ECLASS__EOPERATIONS:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getEOperations()).basicAdd(otherEnd, msgs);
      case EcorePackage.ECLASS__ESTRUCTURAL_FEATURES:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getEStructuralFeatures()).basicAdd(otherEnd, msgs);
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
      case EcorePackage.ECLASS__EANNOTATIONS:
        return ((InternalEList<?>)getEAnnotations()).basicRemove(otherEnd, msgs);
      case EcorePackage.ECLASS__EPACKAGE:
        return eBasicSetContainer(null, EcorePackage.ECLASS__EPACKAGE, msgs);
      case EcorePackage.ECLASS__ETYPE_PARAMETERS:
        return ((InternalEList<?>)getETypeParameters()).basicRemove(otherEnd, msgs);
      case EcorePackage.ECLASS__EOPERATIONS:
        return ((InternalEList<?>)getEOperations()).basicRemove(otherEnd, msgs);
      case EcorePackage.ECLASS__ESTRUCTURAL_FEATURES:
        return ((InternalEList<?>)getEStructuralFeatures()).basicRemove(otherEnd, msgs);
      case EcorePackage.ECLASS__EGENERIC_SUPER_TYPES:
        return ((InternalEList<?>)getEGenericSuperTypes()).basicRemove(otherEnd, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
  }

  protected EStructuralFeature[] getEAllStructuralFeaturesData()
  {
    if (eAllStructuralFeaturesData == null)
    {
      getEAllStructuralFeatures();
    }
    return eAllStructuralFeaturesData;
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public int getFeatureCount()
  {
    return getEAllStructuralFeaturesData().length;
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EStructuralFeature getEStructuralFeature(int featureID) 
  {
    EStructuralFeature [] eAllStructuralFeaturesData  = getEAllStructuralFeaturesData();
    return 
      featureID >= 0 && featureID < eAllStructuralFeaturesData.length ? 
        eAllStructuralFeaturesData[featureID] : 
        null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public int getFeatureID(EStructuralFeature feature)
  {
    EStructuralFeature [] eAllStructuralFeaturesData  = getEAllStructuralFeaturesData();
    int index = feature.getFeatureID();
    if (index != -1)
    {
      for (int last = eAllStructuralFeaturesData.length; index < last; ++index)
      {
        if (eAllStructuralFeaturesData[index] == feature)
        {
          return index;
        }
      }
    }
    return -1;
  }

  /**
   * The default value of the '{@link #isAbstract() <em>Abstract</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isAbstract()
   * @generated
   * @ordered
   */
  protected static final boolean ABSTRACT_EDEFAULT = false;

  /**
   * The flag representing the value of the '{@link #isAbstract() <em>Abstract</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isAbstract()
   * @generated
   * @ordered
   */
  protected static final int ABSTRACT_EFLAG = 1 << 8;

  /**
   * The default value of the '{@link #isInterface() <em>Interface</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isInterface()
   * @generated
   * @ordered
   */
  protected static final boolean INTERFACE_EDEFAULT = false;

  /**
   * The flag representing the value of the '{@link #isInterface() <em>Interface</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isInterface()
   * @generated
   * @ordered
   */
  protected static final int INTERFACE_EFLAG = 1 << 9;

  /**
   * The cached value of the '{@link #getESuperTypes() <em>ESuper Types</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getESuperTypes()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EList<EClass> eSuperTypes;

  /**
   * The cached value of the '{@link #getEOperations() <em>EOperations</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEOperations()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EList<EOperation> eOperations;

  /**
   * The cached value of the '{@link #getEReferences() <em>EReferences</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEReferences()
   * @generated NOT
   * @ordered
   */
  @GwtTransient
  protected BasicEList<EReference> eReferences = null;

  /**
   * The cached value of the '{@link #getEAttributes() <em>EAttributes</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEAttributes()
   * @generated NOT
   * @ordered
   */
  @GwtTransient
  protected BasicEList<EAttribute> eAttributes = null;

  /**
   * The cached value of the '{@link #getEStructuralFeatures() <em>EStructural Features</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEStructuralFeatures()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EList<EStructuralFeature> eStructuralFeatures;

  /**
   * The cached value of the '{@link #getEGenericSuperTypes() <em>EGeneric Super Types</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEGenericSuperTypes()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EList<EGenericType> eGenericSuperTypes;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isAbstract()
  {
    return (eFlags & ABSTRACT_EFLAG) != 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAbstract(boolean newAbstract)
  {
    boolean oldAbstract = (eFlags & ABSTRACT_EFLAG) != 0;
    if (newAbstract) eFlags |= ABSTRACT_EFLAG; else eFlags &= ~ABSTRACT_EFLAG;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.ECLASS__ABSTRACT, oldAbstract, newAbstract));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isInterface()
  {
    return (eFlags & INTERFACE_EFLAG) != 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setInterface(boolean newInterface)
  {
    boolean oldInterface = (eFlags & INTERFACE_EFLAG) != 0;
    if (newInterface) eFlags |= INTERFACE_EFLAG; else eFlags &= ~INTERFACE_EFLAG;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.ECLASS__INTERFACE, oldInterface, newInterface));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (abstract: ");
    result.append((eFlags & ABSTRACT_EFLAG) != 0);
    result.append(", interface: ");
    result.append((eFlags & INTERFACE_EFLAG) != 0);
    result.append(')');
    return result.toString();
  }

  public EList<EClass> getESuperTypes()
  {
    if (eSuperTypes == null)
    {
      getESuperAdapter();
      eSuperTypes = 
        new DelegatingEcoreEList<EClass>(this)
        {
          private static final long serialVersionUID = 1L;

          @Override
          protected List<EClass> delegateList()
          {
            return null;
          }

          @Override
          protected List<EClass> delegateBasicList()
          {
            return 
              new AbstractSequentialList<EClass>() 
              {
                @Override
                public ListIterator<EClass> listIterator(int index)
                {
                  return basicListIterator(index);
                }

                @Override
                public int size()
                {
                  return delegateSize();
                }
              };
          }

          @Override
          protected Iterator<EClass> delegateIterator()
          {
            return iterator();
          }

          @Override
          protected ListIterator<EClass> delegateListIterator()
          {
            return listIterator();
          }

          protected EGenericType wrap(EClass eClass)
          {
            EGenericType eGenericType = EcoreFactory.eINSTANCE.createEGenericType();
            eGenericType.setEClassifier(eClass);
            return eGenericType;
          }

          protected EClass unwrap(EGenericType eGenericType)
          {
            EClassifier result = ((EGenericTypeImpl)eGenericType).basicGetERawType();
            if (result instanceof EClass)
            {
              return (EClass)result;
            }
            else
            {
              return EcorePackage.Literals.EOBJECT;
            }
          }

          @Override
          protected void delegateAdd(int index, EClass eClass)
          {
            getEGenericSuperTypes().add(index, wrap(eClass));
          }

          @Override
          protected void delegateClear()
          {
            getEGenericSuperTypes().clear();
          }

          @Override
          protected void delegateAdd(EClass eClass)
          {
            getEGenericSuperTypes().add(wrap(eClass));
          }

          @Override
          protected boolean delegateContains(Object object)
          {
            for (EClass eClass : this)
            {
              if (object == eClass)
              {
                return true;
              }
            }
            return false;
          }

          @Override
          protected boolean delegateContainsAll(Collection<?> collection)
          {
            for (Object object : collection)
            {
              if (!delegateContains(object))
              {
                return false;
              }
            }
            return true;
          }

          @Override
          protected boolean delegateEquals(Object object)
          {
            if (object instanceof List<?>)
            {
              List<?> list = (List<?>)object;
              if (list.size() == delegateSize())
              {
                for (Iterator<?> i = list.iterator(), j = iterator(); i.hasNext(); )
                {
                  if (i.next() != j.next())
                  {
                    return false;
                  }
                }
                return true;
              }
            }
            return false;
          }

          @Override
          protected EClass delegateGet(int index)
          {
            EGenericType eGenericType = getEGenericSuperTypes().get(index);
            return unwrap(eGenericType);
          }

          @Override
          protected int delegateHashCode()
          {
            int hashCode = 1;
            for (EGenericType eGenericType : getEGenericSuperTypes())
            {
              Object object = unwrap(eGenericType);
              hashCode = 31 * hashCode + (object == null ? 0 : object.hashCode());
            }
            return hashCode;
          }

          @Override
          protected int delegateIndexOf(Object object)
          {
            int index = 0;
            for (EGenericType eGenericType : getEGenericSuperTypes())
            {
              if (object == unwrap(eGenericType))
              {
                return index;
              }
              ++index;
            }
            return -1;
          }

          @Override
          protected boolean delegateIsEmpty()
          {
            return getEGenericSuperTypes().isEmpty();
          }

          @Override
          protected int delegateLastIndexOf(Object object)
          {
            EList<EGenericType> eGenericSuperTypes = getEGenericSuperTypes();
            for (int i = eGenericSuperTypes.size() - 1; i >= 0; --i)
            {
              if (unwrap(eGenericSuperTypes.get(i)) == object)
              {
                return i;
              }
            }
            return -1;
          }

          @Override
          protected EClass delegateRemove(int index)
          {
            EGenericType eGenericType = getEGenericSuperTypes().remove(index);
            return unwrap(eGenericType);
          }

          @Override
          protected EClass delegateSet(int index, EClass eClass)
          {
            EGenericType eGenericType = getEGenericSuperTypes().get(index);
            EClass result = unwrap(eGenericType);

            // If this is just a proxy being resolved...
            //
            if (resolveProxy(result) == eClass)
            {
              // Force the raw type to be resolved so we don't resolve this endlessly.
              //
              eGenericType.getERawType();
            }
            else
            {
              // Update the classifier and hence the raw type as normal.
              //
              eGenericType.setEClassifier(eClass);
            }
            return result;
          }

          @Override
          protected int delegateSize()
          {
            return getEGenericSuperTypes().size();
          }

          @Override
          protected Object[] delegateToArray()
          {
            int size = delegateSize();
            Object[] result = new Object[size];
            
            int index = 0;
            for (EGenericType eGenericType : getEGenericSuperTypes())
            {
              result[index++] = unwrap(eGenericType);
            }
            return result;
          }

          @Override
          protected <T> T[] delegateToArray(T[] array)
          {
            int size = delegateSize();
            if (array.length < size)
            {
              @SuppressWarnings("unchecked") T[] newArray = (T[])Array.newInstance(array.getClass().getComponentType(), size);
              array = newArray;
            }
          
            if (array.length > size)
            {
              array[size] = null;
            }

            int index = 0;
            for (EGenericType eGenericType : getEGenericSuperTypes())
            {
              @SuppressWarnings("unchecked") T rawType = (T)unwrap(eGenericType);
              array[index++] = rawType;
            }

            return array;
          }

          @Override
          protected String delegateToString()
          {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("[");
            EList<EGenericType> eGenericSuperTypes = getEGenericSuperTypes();
            for (int i = 0, size = delegateSize(); i < size; )
            {
              stringBuffer.append(String.valueOf(unwrap(eGenericSuperTypes.get(i))));
              if (++i < size)
              {
                stringBuffer.append(", ");
              }
            }
            stringBuffer.append("]");
            return stringBuffer.toString();
          }

          @Override
          protected boolean isInstance(Object object)
          {
            return object instanceof EClass;
          }

          @Override
          public int getFeatureID()
          {
            return EcorePackage.ECLASS__ESUPER_TYPES;
          }

          @Override
          protected boolean useEquals()
          {
            return true;
          }

          @Override
          protected boolean canContainNull()
          {
            return false;
          }

          @Override
          protected boolean isUnique()
          {
            return true;
          }

          @Override
          protected boolean hasInverse()
          {
            return false;
          }

          @Override
          protected boolean hasManyInverse()
          {
            return false;
          }

          @Override
          protected boolean hasNavigableInverse()
          {
            return false;
          }

          @Override
          protected boolean isEObject()
          {
            return true;
          }

          @Override
          protected boolean isContainment()
          {
            return false;
          }

          @Override
          protected boolean hasProxies()
          {
            return true;
          }

          @Override
          protected boolean hasInstanceClass()
          {
            return true;
          }
          
          @Override
          public boolean isSet()
          {
            return isSetESuperTypes();
          }
          
          @Override
          protected NotificationImpl createNotification(int eventType, Object oldObject, Object newObject, int index, boolean wasSet)
          {
            // The notification for this list is being thrown by the
            // delegating list
            //
            return null;
          }
          
          @Override
          protected void dispatchNotification(Notification notification)
          {
            // Do nothing
          }
        };
    }
    return eSuperTypes;
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void unsetESuperTypes()
  {
    if (eSuperTypes != null) ((InternalEList.Unsettable<?>)eSuperTypes).unset();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean isSetESuperTypes()
  {
    return  eSuperTypes != null && !eSuperTypes.isEmpty() && !isSetEGenericSuperTypes();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<EOperation> getEOperations()
  {
    if (eOperations == null)
    {
      eOperations = new EObjectContainmentWithInverseEList<EOperation>(EOperation.class, this, EcorePackage.ECLASS__EOPERATIONS, EcorePackage.EOPERATION__ECONTAINING_CLASS);
    }
    return eOperations;
  }

  /**
   * Determines if the class or interface represented by this Class object is either
   * the same as, or is a super class or super interface of, the class or interface
   * represented by the specified someClass parameter.  Semantics are the same as
   * java.lang.Class#isAssignableFrom
   */
  public boolean isSuperTypeOf(EClass someClass)
  {
    return someClass == this || someClass.getEAllSuperTypes().contains(this);
  }

  /**
   * Returns all the super types in the hierarchy.
   */
  public EList<EClass> getEAllSuperTypes()
  {
    if (eAllSuperTypes == null)
    {
      BasicEList<EClass> result = 
        new UniqueEList<EClass>()
        {
          private static final long serialVersionUID = 1L;

          @Override
          protected Object [] newData(int capacity)
          {
            return new EClassifier [capacity];
          }

          @Override
          protected boolean useEquals()
          {
            return false;
          }
        };

      Set<EClass> computationInProgress = COMPUTATION_IN_PROGRESS.get();
      if (computationInProgress.add(this))
      {
        for (EClass eSuperType : getESuperTypes())
        {
          EList<EClass> higherSupers = eSuperType.getEAllSuperTypes();
          result.addAll(higherSupers);
          result.add(eSuperType);
        }
        computationInProgress.remove(this);
      }

      result.shrink();
      eAllSuperTypes = 
        new EcoreEList.UnmodifiableEList.FastCompare<EClass>
          (this, EcorePackage.eINSTANCE.getEClass_EAllSuperTypes(), result.size(), result.data());
      getESuperAdapter().setAllSuperCollectionModified(false);
    }

    return eAllSuperTypes;
  }

  @Override
  protected boolean dynamicIsInstance(EObject eObject)
  {
    return isSuperTypeOf(eObject.eClass());
  }

  public ESuperAdapter getESuperAdapter()
  {
    if (eSuperAdapter == null)
    {
      eSuperAdapter = 
        new ESuperAdapter()
        {
          @Override
          void setFlags(int featureId)
          {
            super.setFlags(featureId);
            
            if (isAllAttributesCollectionModified())
            {
              eAllAttributes = null;
            }
            if (isAllReferencesCollectionModified())
            {
              eAllReferences = null;
            }
            if (isAllStructuralFeaturesCollectionModified())
            {
              eAllStructuralFeatures = null;
              eAllStructuralFeaturesData = null;
            }
            if (isAllOperationsCollectionModified())
            {
              eAllOperations = null;
              eAllOperationsData = null;
            }
            if (isAllContainmentsCollectionModified())
            {
              eAllContainments = null;
            }
            if (isAllSuperCollectionModified())
            {
              eAllSuperTypes = null;
              eAllGenericSuperTypes = null;
            }
          }
        };
      eAdapters().add(0, eSuperAdapter);
    }
    return eSuperAdapter;
  }

  @Override
  public void eSetDeliver(boolean deliver)
  {
    super.eSetDeliver(deliver);

    if (deliver)
    {
      for (EClass eSuperType : getESuperTypes())
      {
        ESuperAdapter eSuperAdapter = ((ESuperAdapter.Holder)eSuperType).getESuperAdapter();
        eSuperAdapter.getSubclasses().add(this);
      }
    }
  }

  @Override
  public EObject eObjectForURIFragmentSegment(String uriFragmentSegment)
  {
    EObject result = eAllStructuralFeaturesData == null || eOperations != null && !eOperations.isEmpty() ? null : getEStructuralFeature(uriFragmentSegment);
    return result != null ? result : super.eObjectForURIFragmentSegment(uriFragmentSegment);
  }

}
