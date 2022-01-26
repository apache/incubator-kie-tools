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
package org.eclipse.emf.ecore;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.util.FeatureMap;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EStructural Feature</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.EStructuralFeature#isChangeable <em>Changeable</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EStructuralFeature#isVolatile <em>Volatile</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EStructuralFeature#isTransient <em>Transient</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EStructuralFeature#getDefaultValueLiteral <em>Default Value Literal</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EStructuralFeature#getDefaultValue <em>Default Value</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EStructuralFeature#isUnsettable <em>Unsettable</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EStructuralFeature#isDerived <em>Derived</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EStructuralFeature#getEContainingClass <em>EContaining Class</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.EcorePackage#getEStructuralFeature()
 * @model abstract="true"
 *        annotation="http://www.eclipse.org/emf/2002/Ecore constraints='ValidDefaultValueLiteral'"
 * @generated
 */
public interface EStructuralFeature extends ETypedElement
{
  /**
   * Returns the value of the '<em><b>Transient</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Transient</em>' attribute.
   * @see #setTransient(boolean)
   * @see org.eclipse.emf.ecore.EcorePackage#getEStructuralFeature_Transient()
   * @model
   * @generated
   */
  boolean isTransient();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EStructuralFeature#isTransient <em>Transient</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Transient</em>' attribute.
   * @see #isTransient()
   * @generated
   */
  void setTransient(boolean value);

  /**
   * Returns the value of the '<em><b>Volatile</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Volatile</em>' attribute.
   * @see #setVolatile(boolean)
   * @see org.eclipse.emf.ecore.EcorePackage#getEStructuralFeature_Volatile()
   * @model
   * @generated
   */
  boolean isVolatile();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EStructuralFeature#isVolatile <em>Volatile</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Volatile</em>' attribute.
   * @see #isVolatile()
   * @generated
   */
  void setVolatile(boolean value);

  /**
   * Returns the value of the '<em><b>Changeable</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Changeable</em>' attribute.
   * @see #setChangeable(boolean)
   * @see org.eclipse.emf.ecore.EcorePackage#getEStructuralFeature_Changeable()
   * @model default="true"
   * @generated
   */
  boolean isChangeable();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EStructuralFeature#isChangeable <em>Changeable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Changeable</em>' attribute.
   * @see #isChangeable()
   * @generated
   */
  void setChangeable(boolean value);

  /**
   * Returns the value of the '<em><b>Default Value Literal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the serialized form of the default value.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Default Value Literal</em>' attribute.
   * @see #setDefaultValueLiteral(String)
   * @see org.eclipse.emf.ecore.EcorePackage#getEStructuralFeature_DefaultValueLiteral()
   * @model
   * @generated
   */
  String getDefaultValueLiteral();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EStructuralFeature#getDefaultValueLiteral <em>Default Value Literal</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Default Value Literal</em>' attribute.
   * @see #getDefaultValueLiteral()
   * @generated
   */
  void setDefaultValueLiteral(String value);

  /**
   * Returns the value of the '<em><b>Default Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the default value that feature must take on when an
   * explicit value has not been set.  Specifically, it may be non-null if
   * the feature has an {@link ETypedElement#getEType eType}.  If the
   * {@link #getDefaultValueLiteral defaultValueLiteral} is null, it is
   * simply the <code>eType</code>'s intrinsic {@link
   * EClassifier#getDefaultValue default value}.  Otherwise, if the 
   * <code>eType</code> is an {@link EDataType} and the
   * <code>defaultValueLiteral</code> is non-null, it is the object created
   * by the factory's {@link EFactory#createFromString} method when invoked
   * with those two objects as parameters.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Default Value</em>' attribute.
   * @see org.eclipse.emf.ecore.EcorePackage#getEStructuralFeature_DefaultValue()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Object getDefaultValue();

  /**
   * A convenience method for setting the '<em><b>Default Value Literal</b></em>' attribute.
   * <p>
   * If the feature has an {@link ETypedElement#getEType eType} that is an
   * {@link EDataType}, the specified <code>value</code> is converted to
   * a string using the factory's {@link EFactory#convertToString} 
   * method, and the {@link #getDefaultValueLiteral defaultValueLiteral} is
   * set to the result.
   * </p>
   * @see #setDefaultValueLiteral
   */
  void setDefaultValue(Object value);

  /**
   * Returns the value of the '<em><b>Unsettable</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * An unsettable feature explicitly models the state of being set verses being unset
   * and so provides a direct implementation for the reflective {@link EObject#eIsSet eIsSet}.
   * It is only applicable {@link ETypedElement#isMany single-valued} features.
   * One effect of this setting is that,
   * in addition to generating the methods <code>getXyz</code>
   * and <code>setXyz</code> (if the feature is {@link #isChangeable changeable}),
   * a reflective generator will generate the methods <code>isSetXyz</code> and <code>unsetXyz</code>.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Unsettable</em>' attribute.
   * @see #setUnsettable(boolean)
   * @see org.eclipse.emf.ecore.EcorePackage#getEStructuralFeature_Unsettable()
   * @model
   * @generated
   */
  boolean isUnsettable();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EStructuralFeature#isUnsettable <em>Unsettable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Unsettable</em>' attribute.
   * @see #isUnsettable()
   * @generated
   */
  void setUnsettable(boolean value);

  /**
   * Returns the value of the '<em><b>Derived</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * A derived feature typically computes its value from those of other features.
   * It will typically be {@link #isTransient transient} 
   * and will often be {@link #isVolatile volatile} and not {@link #isChangeable changeable}.
   * The default {@link org.eclipse.emf.ecore.util.EcoreUtil.Copier copier} won't copy it.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Derived</em>' attribute.
   * @see #setDerived(boolean)
   * @see org.eclipse.emf.ecore.EcorePackage#getEStructuralFeature_Derived()
   * @model
   * @generated
   */
  boolean isDerived();

  /**
   * Sets the value of the '{@link org.eclipse.emf.ecore.EStructuralFeature#isDerived <em>Derived</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Derived</em>' attribute.
   * @see #isDerived()
   * @generated
   */
  void setDerived(boolean value);

  /**
   * Returns the value of the '<em><b>EContaining Class</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.ecore.EClass#getEStructuralFeatures <em>EStructural Features</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the containing class of this feature.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EContaining Class</em>' container reference.
   * @see org.eclipse.emf.ecore.EcorePackage#getEStructuralFeature_EContainingClass()
   * @see org.eclipse.emf.ecore.EClass#getEStructuralFeatures
   * @model opposite="eStructuralFeatures" resolveProxies="false" changeable="false"
   * @generated
   */
  EClass getEContainingClass();

  /**
   * <!-- begin-user-doc -->
   * Returns the ID relative to the {@link #getEContainingClass containing} class.
   * @return  the relative ID.
   * <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  int getFeatureID();

  /**
   * <!-- begin-user-doc -->
   * Returns the {@link EClassifier#getInstanceClass instance class} of the {@link #getEContainingClass containing} class.
   * @return the instance class of the containing class.
   * <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  Class<?> getContainerClass();

  /**
   * A representation of a value held by a feature of an object.
   * In the case of a {@link org.eclipse.emf.ecore.InternalEObject#eSetClass dynamic} instance,
   * a setting will be the only actual representation of the object-feature-value association.
   */
  interface Setting
  {
    /**
     * Returns the object holding a value.
     * @return the object holding a value.
     */
    EObject getEObject();

    /**
     * Returns the specific feature holding a value for the object.
     * @return the specific feature holding a value for the object.
     */
    EStructuralFeature getEStructuralFeature();

    /**
     * Returns the value held by the feature of the object;
     * it optionally {@link org.eclipse.emf.ecore.util.EcoreUtil#resolve(EObject, EObject) resolves} the value.
     * @param resolve whether to resolve.
     * @return the value held by the feature of the object.
     * @see EObject#eGet(EStructuralFeature, boolean)
     */
    Object get(boolean resolve);

    /**
     * Set the value held by the feature of the object.
     * If the new value is this setting itself, or is modified as a side effect of modifying this setting
     * (i.e., if it is a view on the setting),
     * the behavior is undefined and will likely result in simply clearing the list.
     * @param newValue the newValue.
     * @see EObject#eSet(EStructuralFeature, Object)
     */
    void set(Object newValue);

    /**
     * Returns whether a value is held by the feature of the object.
     * @return whether a value is held by the feature of the object.
     * @see EObject#eIsSet(EStructuralFeature)
     */
    boolean isSet();

    /**
     * Unsets the value held by the feature of the object.
     * @see EObject#eUnset(EStructuralFeature)
     */
    void unset();
  }

  /**
   * Internal API implemented by all features. 
   */
  interface Internal extends EStructuralFeature, InternalEObject
  {
    /**
     * An API for the backing store that holds dynamic values.
     */
    interface DynamicValueHolder
    {
      /**
       * An object use to represent being set to null <code>null</code>  
       * as opposed to having no value and hence being in the default state.
       */
      Object NIL = new Object();

      /**
       * Returns the value of the feature.
       * @param dynamicFeatureID the feature's index in the cached dynamic values.
       * @return the value of the feature.
       */
      Object dynamicGet(int dynamicFeatureID);

      /**
       * Sets the value of the feature to the new value.
       * @param dynamicFeatureID the feature's index in the cached dynamic values.
       * @param newValue the new value for the feature.
       */
      void dynamicSet(int dynamicFeatureID, Object newValue);

      /**
       * Unsets the value of the feature.
       * @param dynamicFeatureID the feature's index in the cached dynamic values.
       */
      void dynamicUnset(int dynamicFeatureID);
    }

    /**
     * An API for delegating reflective access to settings. 
     */
    interface SettingDelegate
    {
      /**
       * A factory for creating setting delegates.
       * @since 2.6
       */
      interface Factory
      {
        /**
         * Creates a setting delegate for the given feature.
         * @param eStructuralFeature the feature for which a setting delegate is to be created.
         * @return a new a setting delegate for the given feature.
         */
        SettingDelegate createSettingDelegate(EStructuralFeature eStructuralFeature);

        /**
         * A <code>Factory</code> wrapper that is used by the {@link Factory.Registry}.
         */
        interface Descriptor
        {
          Factory getFactory();
        }

        /**
         * A registry of factories for creating setting delegates.
         */
        interface Registry extends Map<String, Object>
        {
          Registry INSTANCE = new Impl();

          Factory getFactory(String uri);

          class Impl extends HashMap<String, Object> implements Registry
          {
            private static final long serialVersionUID = 1L;

            @Override
            public Object get(Object key)
            {
              Object factory = super.get(key);
              if (factory instanceof Descriptor)
              {
                Descriptor factoryDescriptor = (Descriptor)factory;
                factory = factoryDescriptor.getFactory();
                put((String)key, factory);
                return factory;
              }
              else
              {
                return factory;
              }
            }

            public Factory getFactory(String uri)
            {
              return (Factory)get(uri);
            }
          }
        }
      }

      /**
       * Returns a setting that can be used to access the owner's feature.
       * @param owner the owner of the feature.
       * @param settings the owner's array of cached values.
       * @param dynamicFeatureID the feature's index in the owner's cached dynamic values.
       * @return a setting that can be used to access the owner's feature.
       * @see InternalEObject#eSetting(EStructuralFeature)
       */
      Setting dynamicSetting(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID);

      /**
       * Returns the value of the feature of this owner.
       * @param owner the owner of the feature.
       * @param settings the owner's array of cached values.
       * @param dynamicFeatureID the feature's index in the owner's cached dynamic values.
       * @param resolve whether to resolve.
       * @param coreType return the core EMF object if value is a non-EMF wrapper/view.
       * @return the value of the feature of this owner.
       * @see EObject#eGet(EStructuralFeature, boolean)
       */
      Object dynamicGet(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID, boolean resolve, boolean coreType);

      /**
       * Sets the value of the given feature of the owner to the new value.
       * @param owner the owner of the feature.
       * @param settings the owner's array of cached values.
       * @param dynamicFeatureID the feature's index in the owner's cached dynamic values.
       * @param newValue the new value for the feature.
       * @see EObject#eSet(EStructuralFeature, Object)
       */
      void dynamicSet(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID, Object newValue);

      /**
       * Returns whether the feature of the owner is considered to be set.
       * @param owner the owner of the feature.
       * @param settings the owner's array of cached values.
       * @param dynamicFeatureID the feature's index in the owner's cached dynamic values.
       * @return whether the feature of the owner is considered to be set.
       * @see EObject#eIsSet(EStructuralFeature)
       */
      boolean dynamicIsSet(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID);

      /**
       * Unsets the value of the given feature of the owner.
       * @param owner the owner of the feature.
       * @param settings the owner's array of cached values.
       * @param dynamicFeatureID the feature's index in the owner's cached dynamic values.
       * @see EObject#eUnset(EStructuralFeature)
       */
      void dynamicUnset(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID);

      /**
       * Adds the object at the other end of a bidirectional reference to the appropriate feature
       * and returns accumulated notifications.
       * @param owner the owner of the feature.
       * @param settings the owner's array of cached values.
       * @param dynamicFeatureID the feature's index in the owner's cached dynamic values.
       * @param otherEnd the object to inverse add.
       * @param notifications the incoming accumulated notifications.
       * @return accumulated notifications.
       * @see InternalEObject#eInverseAdd
       */
      NotificationChain dynamicInverseAdd
        (InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID, InternalEObject otherEnd, NotificationChain notifications);

      /**
       * Removes the object at the other end of a bidirectional reference from the appropriate feature
       * and returns accumulated notifications.
       * @param owner the owner of the feature.
       * @param settings the owner's array of cached values.
       * @param dynamicFeatureID the feature's index in the owner's cached dynamic values.
       * @param otherEnd the object to inverse remove.
       * @param notifications the incoming accumulated notifications.
       * @return accumulated notifications.
       * @see InternalEObject#eInverseRemove
       */
      NotificationChain dynamicInverseRemove
        (InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID, InternalEObject otherEnd, NotificationChain notifications);
    }

    /**
     * Returns the delegate for this feature.
     * A default delegate implementation should always be available,
     * so this should never return <code>null</code>.
     * @return the delegate for this feature.
     */
    SettingDelegate getSettingDelegate();

    /**
     * Sets the specialized delegate for this feature.
     * @param settingDelegate the specialized delegate.
     */
    void setSettingDelegate(SettingDelegate settingDelegate);

    /**
     * Returns whether the type is a feature map.
     * @return whether the type is a feature map.
     */
    boolean isFeatureMap();

    /**
     * Returns the appropriately behaving feature map entry for this feature.
     * @return the feature map entry prototype.
     */
    FeatureMap.Entry.Internal getFeatureMapEntryPrototype();

    /**
     * Sets the appropriately behaving feature map entry for this feature.
     * @param prototype the feature map entry prototype.
     */
    void setFeatureMapEntryPrototype(FeatureMap.Entry.Internal prototype);
    
    /**
     * Returns whether this is an {@link EAttribute attribute} that is an {@link EAttribute#isID() ID}.
     * @return whether this is an attribute that is an ID.
     */
    public boolean isID();

    /**
     * Returns whether this is a {@link EReference reference} that {@link EReference#isResolveProxies() resolves proxies}.
     * @return whether this is a reference that resolves proxies.
     */
    public boolean isResolveProxies();

    /**
     * Returns whether this is a {@link EReference reference} that is a {@link EReference#isContainer() container}.
     * @return whether this is a reference that is a container. 
     */
    public boolean isContainer();

    /**
     * Returns whether this is a {@link EReference reference} that is a {@link EReference#isContainment() containment}.
     * @return whether this is a reference that is a containment.
     */
    public boolean isContainment();

    /**
     * Returns the {@link EReference#getEOpposite() opposite} if this is {@link EReference reference} that has one.
     * @return the opposite if this is reference that has one.
     */
    public EReference getEOpposite();
  }
}
