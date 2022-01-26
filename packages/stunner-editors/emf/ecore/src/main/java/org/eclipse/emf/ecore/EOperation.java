/**
 * Copyright (c) 2002-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 *   Christian Damus (Zeligsoft) - 255469
 */
package org.eclipse.emf.ecore;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.InvocationTargetException;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EOperation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.EOperation#getEContainingClass <em>EContaining Class</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EOperation#getETypeParameters <em>EType Parameters</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EOperation#getEParameters <em>EParameters</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EOperation#getEExceptions <em>EExceptions</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.EOperation#getEGenericExceptions <em>EGeneric Exceptions</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.ecore.EcorePackage#getEOperation()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='UniqueParameterNames UniqueTypeParameterNames NoRepeatingVoid'"
 * @generated
 */
public interface EOperation extends ETypedElement
{
  /**
   * Returns the value of the '<em><b>EContaining Class</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.ecore.EClass#getEOperations <em>EOperations</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * It represents a method in the Java sense.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EContaining Class</em>' container reference.
   * @see org.eclipse.emf.ecore.EcorePackage#getEOperation_EContainingClass()
   * @see org.eclipse.emf.ecore.EClass#getEOperations
   * @model opposite="eOperations" resolveProxies="false" changeable="false"
   * @generated
   */
  EClass getEContainingClass();

  /**
   * Returns the value of the '<em><b>EParameters</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EParameter}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.ecore.EParameter#getEOperation <em>EOperation</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the valid arguments for this operation.
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EParameters</em>' containment reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEOperation_EParameters()
   * @see org.eclipse.emf.ecore.EParameter#getEOperation
   * @model opposite="eOperation" containment="true"
   * @generated
   */
  EList<EParameter> getEParameters();

  /**
   * Returns the value of the '<em><b>EExceptions</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EClassifier}.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the exceptions thrown by this operation
   * and is derived from the {@link #getEGenericExceptions() generic exceptions}.
   * </p>
   * @see #getEGenericExceptions()
   * <!-- end-user-doc -->
   * @return the value of the '<em>EExceptions</em>' reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEOperation_EExceptions()
   * @model unsettable="true" suppressedIsSetVisibility="true" suppressedUnsetVisibility="true"
   * @generated
   */
  EList<EClassifier> getEExceptions();

  /**
   * Returns the value of the '<em><b>EGeneric Exceptions</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EGenericType}.
   * <!-- begin-user-doc -->
   * <p>
   * It represents the generic exceptions thrown by this operation.
   * The {@link #getEExceptions() exceptions} list is derived from this one, i.e., it represents the {@link EGenericType#getERawType() erasure} of each generic exception.
   * </p>
   * @see #getEExceptions()
   * @since 2.3
   * <!-- end-user-doc -->
   * @return the value of the '<em>EGeneric Exceptions</em>' containment reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEOperation_EGenericExceptions()
   * @model containment="true" unsettable="true" suppressedIsSetVisibility="true" suppressedUnsetVisibility="true"
   * @generated
   */
  EList<EGenericType> getEGenericExceptions();

  /**
   * <!-- begin-user-doc -->
   * Returns the ID relative to the {@link #getEContainingClass containing} class.
   * @return  the relative ID.
   * @since 2.6
   * <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  int getOperationID();

  /**
   * <!-- begin-user-doc -->
   * Returns whether this operation is an override of some other operation.
   * @param someOperation some other operation.
   * @return whether this operation is an override of some other operation.
   * @since 2.6
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  boolean isOverrideOf(EOperation someOperation);

  /**
   * Returns the value of the '<em><b>EType Parameters</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.ETypeParameter}.
   * <!-- begin-user-doc -->
   * <p>
   * An operation can optionally introduce type parameters.
   * </p>
   * @since 2.3
   * <!-- end-user-doc -->
   * @return the value of the '<em>EType Parameters</em>' containment reference list.
   * @see org.eclipse.emf.ecore.EcorePackage#getEOperation_ETypeParameters()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<ETypeParameter> getETypeParameters();

  /**
   * Internal API implemented by all operations.
   * 
   * @since 2.6
   */
  interface Internal extends EOperation, InternalEObject
  {
	/**
	 * A pluggable, dynamic implementation of operation behavior.
	 */
    interface InvocationDelegate
    {
      /**
       * A factory for creating invocation delegates.
       */
      interface Factory
      {
        /**
         * Creates the invocation delegate for the specified <tt>operation</tt>.
         * 
         * @param operation the operation
         * @return its invocation delegate
         */
        InvocationDelegate createInvocationDelegate(EOperation operation);

        /**
         * A <code>Factory</code> wrapper that is used by the {@link Factory.Registry}.
         */
        interface Descriptor
        {
          Factory getFactory();
        }

        /**
         * A registry of invocation-delegate factories.
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
       * Invokes the operation behaviour for the specified <tt>target</tt>
       * object.
       * 
       * @param target the object on which to invoke the operation
       * @param arguments the arguments for the operation parameters (an
       *    empty list if the operation has no parameters)
       * @return the operation's return result, or <code>null</code> if it is
       *    a void operation
       * @throws InvocationTargetException in case of failure to execute the
       *    operation behaviour, usually because of an exception
       */
      Object dynamicInvoke(InternalEObject target, EList<?> arguments) throws InvocationTargetException;
    }
    
    /**
     * Obtains the delegate for this operation.
     * A default delegate is always available, so this should not return
     * <code>null</code>.
     * 
     * @return the operation delegate
     */
    InvocationDelegate getInvocationDelegate();
    
    /**
     * Assigns a delegate to this operation.
     * 
     * @param invocationDelegate the new operation delegate
     */
    void setInvocationDelegate(InvocationDelegate invocationDelegate);
  }
} //EOperation
