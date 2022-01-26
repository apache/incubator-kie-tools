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
package org.eclipse.emf.ecore.xml.type.impl;


import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.List;

import org.eclipse.emf.common.util.Reflect;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.xml.type.AnyType;
import org.eclipse.emf.ecore.xml.type.ProcessingInstruction;
import org.eclipse.emf.ecore.xml.type.SimpleAnyType;
import org.eclipse.emf.ecore.xml.type.XMLTypeDocumentRoot;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.ecore.xml.type.util.XMLTypeValidator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class XMLTypePackageImpl extends EPackageImpl implements XMLTypePackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass anyTypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass processingInstructionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass simpleAnyTypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass xmlTypeDocumentRootEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType anySimpleTypeEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType anyURIEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType base64BinaryEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType booleanEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType booleanObjectEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType decimalEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType integerEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType intObjectEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType longEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType longObjectEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType intEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType shortEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType shortObjectEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType byteEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType byteObjectEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType dateEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType dateTimeEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType stringEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType doubleEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType doubleObjectEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType durationEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType entitiesBaseEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType normalizedStringEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType tokenEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType nameEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType ncNameEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType entityEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType entitiesEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType floatEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType floatObjectEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType gDayEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType gMonthEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType gMonthDayEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType gYearEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType gYearMonthEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType hexBinaryEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType idEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType idrefEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType idrefsBaseEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType idrefsEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType languageEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType nonPositiveIntegerEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType negativeIntegerEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType nmtokenEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType nmtokensBaseEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType nmtokensEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType nonNegativeIntegerEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType notationEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType positiveIntegerEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType qNameEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType timeEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType unsignedLongEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType unsignedIntEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType unsignedIntObjectEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType unsignedShortEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType unsignedShortObjectEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType unsignedByteEDataType = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType unsignedByteObjectEDataType = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.ecore.xml.type.XMLTypePackage#eNS_URI
   * @see #init()
   * @generated
   */
  private XMLTypePackageImpl()
  {
    super(eNS_URI, XMLTypeFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * <p>This method is used to initialize {@link XMLTypePackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static XMLTypePackage init()
  {
    if (isInited) return (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);

    initializeRegistryHelpers();

    // Obtain or create and register package
    XMLTypePackageImpl theXMLTypePackage = (XMLTypePackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof XMLTypePackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new XMLTypePackageImpl());

    isInited = true;

    // Create package meta-data objects
    theXMLTypePackage.createPackageContents();

    // Initialize created meta-data
    theXMLTypePackage.initializePackageContents();

    // Register package validator
    EValidator.Registry.INSTANCE.put
      (theXMLTypePackage, 
       new EValidator.Descriptor()
       {
         public EValidator getEValidator()
         {
           return XMLTypeValidator.INSTANCE;
         }
       });

    // Mark meta-data to indicate it can't be changed
    theXMLTypePackage.freeze();

  
    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(XMLTypePackage.eNS_URI, theXMLTypePackage);
    return theXMLTypePackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static void initializeRegistryHelpers()
  {
    Reflect.register
      (AnyType.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof AnyType;
         }

         public Object newArrayInstance(int size)
         {
           return new AnyType[size];
         }
       });
    Reflect.register
      (ProcessingInstruction.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof ProcessingInstruction;
         }

         public Object newArrayInstance(int size)
         {
           return new ProcessingInstruction[size];
         }
       });
    Reflect.register
      (SimpleAnyType.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof SimpleAnyType;
         }

         public Object newArrayInstance(int size)
         {
           return new SimpleAnyType[size];
         }
       });
    Reflect.register
      (XMLTypeDocumentRoot.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof XMLTypeDocumentRoot;
         }

         public Object newArrayInstance(int size)
         {
           return new XMLTypeDocumentRoot[size];
         }
       });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (byte[].class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof byte[];
         }

         public Object newArrayInstance(int size)
         {
           return new byte[size][];
         }
    });
    Reflect.register
      (Boolean.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof Boolean;
         }

         public Object newArrayInstance(int size)
         {
           return new Boolean[size];
         }
    });
    Reflect.register
      (Byte.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof Byte;
         }

         public Object newArrayInstance(int size)
         {
           return new Byte[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (Double.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof Double;
         }

         public Object newArrayInstance(int size)
         {
           return new Double[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (List.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof List<?>;
         }

         public Object newArrayInstance(int size)
         {
           return new List[size];
         }
    });
    Reflect.register
      (List.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof List<?>;
         }

         public Object newArrayInstance(int size)
         {
           return new List[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (Float.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof Float;
         }

         public Object newArrayInstance(int size)
         {
           return new Float[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (byte[].class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof byte[];
         }

         public Object newArrayInstance(int size)
         {
           return new byte[size][];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (List.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof List<?>;
         }

         public Object newArrayInstance(int size)
         {
           return new List[size];
         }
    });
    Reflect.register
      (List.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof List<?>;
         }

         public Object newArrayInstance(int size)
         {
           return new List[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (Integer.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof Integer;
         }

         public Object newArrayInstance(int size)
         {
           return new Integer[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (Long.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof Long;
         }

         public Object newArrayInstance(int size)
         {
           return new Long[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (List.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof List<?>;
         }

         public Object newArrayInstance(int size)
         {
           return new List[size];
         }
    });
    Reflect.register
      (List.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof List<?>;
         }

         public Object newArrayInstance(int size)
         {
           return new List[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (Short.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof Short;
         }

         public Object newArrayInstance(int size)
         {
           return new Short[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (Short.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof Short;
         }

         public Object newArrayInstance(int size)
         {
           return new Short[size];
         }
    });
    Reflect.register
      (Long.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof Long;
         }

         public Object newArrayInstance(int size)
         {
           return new Long[size];
         }
    });
    Reflect.register
      (String.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof String;
         }

         public Object newArrayInstance(int size)
         {
           return new String[size];
         }
    });
    Reflect.register
      (Integer.class, 
       new Reflect.Helper() 
       {
         public boolean isInstance(Object instance)
         {
           return instance instanceof Integer;
         }

         public Object newArrayInstance(int size)
         {
           return new Integer[size];
         }
    });
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static class WhiteList implements IsSerializable, EBasicWhiteList
  {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected AnyType anyType;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ProcessingInstruction processingInstruction;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SimpleAnyType simpleAnyType;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected XMLTypeDocumentRoot xmlTypeDocumentRoot;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String anyURI;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected byte[] base64Binary;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected boolean boolean_;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected Boolean booleanObject;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected byte byte_;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected Byte byteObject;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String date;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String dateTime;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String decimal;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected double double_;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected Double doubleObject;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String duration;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected List<?> entities;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected List<?> entitiesBase;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String entity;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected float float_;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected Float floatObject;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String gDay;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String gMonth;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String gMonthDay;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String gYear;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String gYearMonth;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected byte[] hexBinary;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String id;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String idref;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected List<?> idrefs;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected List<?> idrefsBase;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected int int_;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String integer;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected Integer intObject;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String language;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected long long_;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected Long longObject;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String name;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String ncName;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String negativeInteger;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String nmtoken;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected List<?> nmtokens;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected List<?> nmtokensBase;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String nonNegativeInteger;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String nonPositiveInteger;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String normalizedString;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String notation;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String positiveInteger;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String qName;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected short short_;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected Short shortObject;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String string;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String time;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String token;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected short unsignedByte;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected Short unsignedByteObject;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected long unsignedInt;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected Long unsignedIntObject;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected String unsignedLong;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected int unsignedShort;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected Integer unsignedShortObject;

  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAnyType()
  {
    return anyTypeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAnyType_Mixed()
  {
    return (EAttribute)anyTypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAnyType_Any()
  {
    return (EAttribute)anyTypeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAnyType_AnyAttribute()
  {
    return (EAttribute)anyTypeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getProcessingInstruction()
  {
    return processingInstructionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getProcessingInstruction_Data()
  {
    return (EAttribute)processingInstructionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getProcessingInstruction_Target()
  {
    return (EAttribute)processingInstructionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSimpleAnyType()
  {
    return simpleAnyTypeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSimpleAnyType_RawValue()
  {
    return (EAttribute)simpleAnyTypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSimpleAnyType_Value()
  {
    return (EAttribute)simpleAnyTypeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSimpleAnyType_InstanceType()
  {
    return (EReference)simpleAnyTypeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getXMLTypeDocumentRoot()
  {
    return xmlTypeDocumentRootEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXMLTypeDocumentRoot_Mixed()
  {
    return (EAttribute)xmlTypeDocumentRootEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXMLTypeDocumentRoot_XMLNSPrefixMap()
  {
    return (EReference)xmlTypeDocumentRootEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXMLTypeDocumentRoot_XSISchemaLocation()
  {
    return (EReference)xmlTypeDocumentRootEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXMLTypeDocumentRoot_CDATA()
  {
    return (EAttribute)xmlTypeDocumentRootEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXMLTypeDocumentRoot_Comment()
  {
    return (EAttribute)xmlTypeDocumentRootEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXMLTypeDocumentRoot_Text()
  {
    return (EAttribute)xmlTypeDocumentRootEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXMLTypeDocumentRoot_ProcessingInstruction()
  {
    return (EReference)xmlTypeDocumentRootEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getAnySimpleType()
  {
    return anySimpleTypeEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getAnyURI()
  {
    return anyURIEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getBase64Binary()
  {
    return base64BinaryEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getBoolean()
  {
    return booleanEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getBooleanObject()
  {
    return booleanObjectEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getDecimal()
  {
    return decimalEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getInteger()
  {
    return integerEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getIntObject()
  {
    return intObjectEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getLong()
  {
    return longEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getLongObject()
  {
    return longObjectEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getInt()
  {
    return intEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getShort()
  {
    return shortEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getShortObject()
  {
    return shortObjectEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getByte()
  {
    return byteEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getByteObject()
  {
    return byteObjectEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getDate()
  {
    return dateEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getDateTime()
  {
    return dateTimeEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getString()
  {
    return stringEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getDouble()
  {
    return doubleEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getDoubleObject()
  {
    return doubleObjectEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getDuration()
  {
    return durationEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getENTITIESBase()
  {
    return entitiesBaseEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getNormalizedString()
  {
    return normalizedStringEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getToken()
  {
    return tokenEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getName_()
  {
    return nameEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getNCName()
  {
    return ncNameEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getENTITY()
  {
    return entityEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getENTITIES()
  {
    return entitiesEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getFloat()
  {
    return floatEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getFloatObject()
  {
    return floatObjectEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getGDay()
  {
    return gDayEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getGMonth()
  {
    return gMonthEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getGMonthDay()
  {
    return gMonthDayEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getGYear()
  {
    return gYearEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getGYearMonth()
  {
    return gYearMonthEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getHexBinary()
  {
    return hexBinaryEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getID()
  {
    return idEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getIDREF()
  {
    return idrefEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getIDREFSBase()
  {
    return idrefsBaseEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getIDREFS()
  {
    return idrefsEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getLanguage()
  {
    return languageEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getNonPositiveInteger()
  {
    return nonPositiveIntegerEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getNegativeInteger()
  {
    return negativeIntegerEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getNMTOKEN()
  {
    return nmtokenEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getNMTOKENSBase()
  {
    return nmtokensBaseEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getNMTOKENS()
  {
    return nmtokensEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getNonNegativeInteger()
  {
    return nonNegativeIntegerEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getNOTATION()
  {
    return notationEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getPositiveInteger()
  {
    return positiveIntegerEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getQName()
  {
    return qNameEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getTime()
  {
    return timeEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getUnsignedLong()
  {
    return unsignedLongEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getUnsignedInt()
  {
    return unsignedIntEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getUnsignedIntObject()
  {
    return unsignedIntObjectEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getUnsignedShort()
  {
    return unsignedShortEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getUnsignedShortObject()
  {
    return unsignedShortObjectEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getUnsignedByte()
  {
    return unsignedByteEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getUnsignedByteObject()
  {
    return unsignedByteObjectEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XMLTypeFactory getXMLTypeFactory()
  {
    return (XMLTypeFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    anyTypeEClass = createEClass(ANY_TYPE);
    createEAttribute(anyTypeEClass, ANY_TYPE__MIXED);
    createEAttribute(anyTypeEClass, ANY_TYPE__ANY);
    createEAttribute(anyTypeEClass, ANY_TYPE__ANY_ATTRIBUTE);

    processingInstructionEClass = createEClass(PROCESSING_INSTRUCTION);
    createEAttribute(processingInstructionEClass, PROCESSING_INSTRUCTION__DATA);
    createEAttribute(processingInstructionEClass, PROCESSING_INSTRUCTION__TARGET);

    simpleAnyTypeEClass = createEClass(SIMPLE_ANY_TYPE);
    createEAttribute(simpleAnyTypeEClass, SIMPLE_ANY_TYPE__RAW_VALUE);
    createEAttribute(simpleAnyTypeEClass, SIMPLE_ANY_TYPE__VALUE);
    createEReference(simpleAnyTypeEClass, SIMPLE_ANY_TYPE__INSTANCE_TYPE);

    xmlTypeDocumentRootEClass = createEClass(XML_TYPE_DOCUMENT_ROOT);
    createEAttribute(xmlTypeDocumentRootEClass, XML_TYPE_DOCUMENT_ROOT__MIXED);
    createEReference(xmlTypeDocumentRootEClass, XML_TYPE_DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
    createEReference(xmlTypeDocumentRootEClass, XML_TYPE_DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
    createEAttribute(xmlTypeDocumentRootEClass, XML_TYPE_DOCUMENT_ROOT__CDATA);
    createEAttribute(xmlTypeDocumentRootEClass, XML_TYPE_DOCUMENT_ROOT__COMMENT);
    createEReference(xmlTypeDocumentRootEClass, XML_TYPE_DOCUMENT_ROOT__PROCESSING_INSTRUCTION);
    createEAttribute(xmlTypeDocumentRootEClass, XML_TYPE_DOCUMENT_ROOT__TEXT);

    // Create data types
    anySimpleTypeEDataType = createEDataType(ANY_SIMPLE_TYPE);
    anyURIEDataType = createEDataType(ANY_URI);
    base64BinaryEDataType = createEDataType(BASE64_BINARY);
    booleanEDataType = createEDataType(BOOLEAN);
    booleanObjectEDataType = createEDataType(BOOLEAN_OBJECT);
    byteEDataType = createEDataType(BYTE);
    byteObjectEDataType = createEDataType(BYTE_OBJECT);
    dateEDataType = createEDataType(DATE);
    dateTimeEDataType = createEDataType(DATE_TIME);
    decimalEDataType = createEDataType(DECIMAL);
    doubleEDataType = createEDataType(DOUBLE);
    doubleObjectEDataType = createEDataType(DOUBLE_OBJECT);
    durationEDataType = createEDataType(DURATION);
    entitiesEDataType = createEDataType(ENTITIES);
    entitiesBaseEDataType = createEDataType(ENTITIES_BASE);
    entityEDataType = createEDataType(ENTITY);
    floatEDataType = createEDataType(FLOAT);
    floatObjectEDataType = createEDataType(FLOAT_OBJECT);
    gDayEDataType = createEDataType(GDAY);
    gMonthEDataType = createEDataType(GMONTH);
    gMonthDayEDataType = createEDataType(GMONTH_DAY);
    gYearEDataType = createEDataType(GYEAR);
    gYearMonthEDataType = createEDataType(GYEAR_MONTH);
    hexBinaryEDataType = createEDataType(HEX_BINARY);
    idEDataType = createEDataType(ID);
    idrefEDataType = createEDataType(IDREF);
    idrefsEDataType = createEDataType(IDREFS);
    idrefsBaseEDataType = createEDataType(IDREFS_BASE);
    intEDataType = createEDataType(INT);
    integerEDataType = createEDataType(INTEGER);
    intObjectEDataType = createEDataType(INT_OBJECT);
    languageEDataType = createEDataType(LANGUAGE);
    longEDataType = createEDataType(LONG);
    longObjectEDataType = createEDataType(LONG_OBJECT);
    nameEDataType = createEDataType(NAME);
    ncNameEDataType = createEDataType(NC_NAME);
    negativeIntegerEDataType = createEDataType(NEGATIVE_INTEGER);
    nmtokenEDataType = createEDataType(NMTOKEN);
    nmtokensEDataType = createEDataType(NMTOKENS);
    nmtokensBaseEDataType = createEDataType(NMTOKENS_BASE);
    nonNegativeIntegerEDataType = createEDataType(NON_NEGATIVE_INTEGER);
    nonPositiveIntegerEDataType = createEDataType(NON_POSITIVE_INTEGER);
    normalizedStringEDataType = createEDataType(NORMALIZED_STRING);
    notationEDataType = createEDataType(NOTATION);
    positiveIntegerEDataType = createEDataType(POSITIVE_INTEGER);
    qNameEDataType = createEDataType(QNAME);
    shortEDataType = createEDataType(SHORT);
    shortObjectEDataType = createEDataType(SHORT_OBJECT);
    stringEDataType = createEDataType(STRING);
    timeEDataType = createEDataType(TIME);
    tokenEDataType = createEDataType(TOKEN);
    unsignedByteEDataType = createEDataType(UNSIGNED_BYTE);
    unsignedByteObjectEDataType = createEDataType(UNSIGNED_BYTE_OBJECT);
    unsignedIntEDataType = createEDataType(UNSIGNED_INT);
    unsignedIntObjectEDataType = createEDataType(UNSIGNED_INT_OBJECT);
    unsignedLongEDataType = createEDataType(UNSIGNED_LONG);
    unsignedShortEDataType = createEDataType(UNSIGNED_SHORT);
    unsignedShortObjectEDataType = createEDataType(UNSIGNED_SHORT_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    XMLTypePackage theXMLTypePackage_1 = (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    simpleAnyTypeEClass.getESuperTypes().add(this.getAnyType());

    // Initialize classes and features; add operations and parameters
    initEClass(anyTypeEClass, AnyType.class, "AnyType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAnyType_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, AnyType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAnyType_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, AnyType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getAnyType_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, AnyType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(processingInstructionEClass, ProcessingInstruction.class, "ProcessingInstruction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getProcessingInstruction_Data(), this.getString(), "data", null, 0, 1, ProcessingInstruction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProcessingInstruction_Target(), this.getString(), "target", null, 1, 1, ProcessingInstruction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(simpleAnyTypeEClass, SimpleAnyType.class, "SimpleAnyType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSimpleAnyType_RawValue(), theXMLTypePackage_1.getString(), "rawValue", null, 0, 1, SimpleAnyType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getSimpleAnyType_Value(), theXMLTypePackage_1.getAnySimpleType(), "value", null, 0, 1, SimpleAnyType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getSimpleAnyType_InstanceType(), ecorePackage.getEDataType(), null, "instanceType", null, 1, 1, SimpleAnyType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xmlTypeDocumentRootEClass, XMLTypeDocumentRoot.class, "XMLTypeDocumentRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getXMLTypeDocumentRoot_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXMLTypeDocumentRoot_XMLNSPrefixMap(), ecorePackage.getEStringToStringMapEntry(), null, "xMLNSPrefixMap", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXMLTypeDocumentRoot_XSISchemaLocation(), ecorePackage.getEStringToStringMapEntry(), null, "xSISchemaLocation", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXMLTypeDocumentRoot_CDATA(), this.getString(), "cDATA", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getXMLTypeDocumentRoot_Comment(), this.getString(), "comment", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getXMLTypeDocumentRoot_ProcessingInstruction(), this.getProcessingInstruction(), null, "processingInstruction", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getXMLTypeDocumentRoot_Text(), this.getString(), "text", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    // Initialize data types
    initEDataType(anySimpleTypeEDataType, Object.class, "AnySimpleType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(anyURIEDataType, String.class, "AnyURI", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(base64BinaryEDataType, byte[].class, "Base64Binary", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(booleanEDataType, boolean.class, "Boolean", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(booleanObjectEDataType, Boolean.class, "BooleanObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(byteEDataType, byte.class, "Byte", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(byteObjectEDataType, Byte.class, "ByteObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(dateEDataType, String.class, "Date", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(dateTimeEDataType, String.class, "DateTime", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(decimalEDataType, String.class, "Decimal", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(doubleEDataType, double.class, "Double", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(doubleObjectEDataType, Double.class, "DoubleObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(durationEDataType, String.class, "Duration", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(entitiesEDataType, List.class, "ENTITIES", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(entitiesBaseEDataType, List.class, "ENTITIESBase", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(entityEDataType, String.class, "ENTITY", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(floatEDataType, float.class, "Float", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(floatObjectEDataType, Float.class, "FloatObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(gDayEDataType, String.class, "GDay", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(gMonthEDataType, String.class, "GMonth", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(gMonthDayEDataType, String.class, "GMonthDay", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(gYearEDataType, String.class, "GYear", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(gYearMonthEDataType, String.class, "GYearMonth", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(hexBinaryEDataType, byte[].class, "HexBinary", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(idEDataType, String.class, "ID", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(idrefEDataType, String.class, "IDREF", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(idrefsEDataType, List.class, "IDREFS", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(idrefsBaseEDataType, List.class, "IDREFSBase", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(intEDataType, int.class, "Int", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(integerEDataType, String.class, "Integer", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(intObjectEDataType, Integer.class, "IntObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(languageEDataType, String.class, "Language", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(longEDataType, long.class, "Long", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(longObjectEDataType, Long.class, "LongObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(nameEDataType, String.class, "Name", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(ncNameEDataType, String.class, "NCName", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(negativeIntegerEDataType, String.class, "NegativeInteger", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(nmtokenEDataType, String.class, "NMTOKEN", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(nmtokensEDataType, List.class, "NMTOKENS", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(nmtokensBaseEDataType, List.class, "NMTOKENSBase", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(nonNegativeIntegerEDataType, String.class, "NonNegativeInteger", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(nonPositiveIntegerEDataType, String.class, "NonPositiveInteger", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(normalizedStringEDataType, String.class, "NormalizedString", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(notationEDataType, String.class, "NOTATION", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(positiveIntegerEDataType, String.class, "PositiveInteger", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(qNameEDataType, String.class, "QName", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(shortEDataType, short.class, "Short", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(shortObjectEDataType, Short.class, "ShortObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(stringEDataType, String.class, "String", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(timeEDataType, String.class, "Time", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(tokenEDataType, String.class, "Token", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(unsignedByteEDataType, short.class, "UnsignedByte", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(unsignedByteObjectEDataType, Short.class, "UnsignedByteObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(unsignedIntEDataType, long.class, "UnsignedInt", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(unsignedIntObjectEDataType, Long.class, "UnsignedIntObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(unsignedLongEDataType, String.class, "UnsignedLong", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(unsignedShortEDataType, int.class, "UnsignedShort", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(unsignedShortObjectEDataType, Integer.class, "UnsignedShortObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);

    // Create annotations
    // http:///org/eclipse/emf/ecore/util/ExtendedMetaData
    createExtendedMetaDataAnnotations();
  }

  /**
   * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createExtendedMetaDataAnnotations()
  {
    String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";		
    addAnnotation
      (anySimpleTypeEDataType, 
       source, 
       new String[] 
       {
       "name", "anySimpleType"
       });		
    addAnnotation
      (anyTypeEClass, 
       source, 
       new String[] 
       {
       "name", "anyType",
       "kind", "mixed"
       });		
    addAnnotation
      (getAnyType_Mixed(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "name", ":mixed"
       });		
    addAnnotation
      (getAnyType_Any(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "wildcards", "##any",
       "name", ":1",
       "processing", "lax"
       });		
    addAnnotation
      (getAnyType_AnyAttribute(), 
       source, 
       new String[] 
       {
       "kind", "attributeWildcard",
       "wildcards", "##any",
       "name", ":2",
       "processing", "lax"
       });		
    addAnnotation
      (anyURIEDataType, 
       source, 
       new String[] 
       {
       "name", "anyURI",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (base64BinaryEDataType, 
       source, 
       new String[] 
       {
       "name", "base64Binary",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (booleanEDataType, 
       source, 
       new String[] 
       {
       "name", "boolean",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (booleanObjectEDataType, 
       source, 
       new String[] 
       {
       "name", "boolean:Object",
       "baseType", "boolean"
       });		
    addAnnotation
      (byteEDataType, 
       source, 
       new String[] 
       {
       "name", "byte"
       });		
    addAnnotation
      (byteObjectEDataType, 
       source, 
       new String[] 
       {
       "name", "byte:Object",
       "baseType", "byte"
       });		
    addAnnotation
      (dateEDataType, 
       source, 
       new String[] 
       {
       "name", "date",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (dateTimeEDataType, 
       source, 
       new String[] 
       {
       "name", "dateTime",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (decimalEDataType, 
       source, 
       new String[] 
       {
       "name", "decimal",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (doubleEDataType, 
       source, 
       new String[] 
       {
       "name", "double",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (doubleObjectEDataType, 
       source, 
       new String[] 
       {
       "name", "double:Object",
       "baseType", "double"
       });		
    addAnnotation
      (durationEDataType, 
       source, 
       new String[] 
       {
       "name", "duration",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (entitiesEDataType, 
       source, 
       new String[] 
       {
       "name", "ENTITIES",
       "baseType", "ENTITIES_._base",
       "minLength", "1"
       });		
    addAnnotation
      (entitiesBaseEDataType, 
       source, 
       new String[] 
       {
       "name", "ENTITIES_._base",
       "itemType", "ENTITY"
       });		
    addAnnotation
      (entityEDataType, 
       source, 
       new String[] 
       {
       "name", "ENTITY",
       "baseType", "NCName"
       });		
    addAnnotation
      (floatEDataType, 
       source, 
       new String[] 
       {
       "name", "float",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (floatObjectEDataType, 
       source, 
       new String[] 
       {
       "name", "float:Object",
       "baseType", "float"
       });		
    addAnnotation
      (gDayEDataType, 
       source, 
       new String[] 
       {
       "name", "gDay",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (gMonthEDataType, 
       source, 
       new String[] 
       {
       "name", "gMonth",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (gMonthDayEDataType, 
       source, 
       new String[] 
       {
       "name", "gMonthDay",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (gYearEDataType, 
       source, 
       new String[] 
       {
       "name", "gYear",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (gYearMonthEDataType, 
       source, 
       new String[] 
       {
       "name", "gYearMonth",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (hexBinaryEDataType, 
       source, 
       new String[] 
       {
       "name", "hexBinary",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (idEDataType, 
       source, 
       new String[] 
       {
       "name", "ID",
       "baseType", "NCName"
       });		
    addAnnotation
      (idrefEDataType, 
       source, 
       new String[] 
       {
       "name", "IDREF",
       "baseType", "NCName"
       });		
    addAnnotation
      (idrefsEDataType, 
       source, 
       new String[] 
       {
       "name", "IDREFS",
       "baseType", "IDREFS_._base",
       "minLength", "1"
       });		
    addAnnotation
      (idrefsBaseEDataType, 
       source, 
       new String[] 
       {
       "name", "IDREFS_._base",
       "itemType", "IDREF"
       });		
    addAnnotation
      (intEDataType, 
       source, 
       new String[] 
       {
       "name", "int"
       });		
    addAnnotation
      (integerEDataType, 
       source, 
       new String[] 
       {
       "name", "integer"
       });		
    addAnnotation
      (intObjectEDataType, 
       source, 
       new String[] 
       {
       "name", "int:Object",
       "baseType", "int"
       });		
    addAnnotation
      (languageEDataType, 
       source, 
       new String[] 
       {
       "name", "language",
       "baseType", "token",
       "pattern", "[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*"
       });		
    addAnnotation
      (longEDataType, 
       source, 
       new String[] 
       {
       "name", "long"
       });		
    addAnnotation
      (longObjectEDataType, 
       source, 
       new String[] 
       {
       "name", "long:Object",
       "baseType", "long"
       });		
    addAnnotation
      (nameEDataType, 
       source, 
       new String[] 
       {
       "name", "Name",
       "baseType", "token",
       "pattern", "\\i\\c*"
       });		
    addAnnotation
      (ncNameEDataType, 
       source, 
       new String[] 
       {
       "name", "NCName",
       "baseType", "Name",
       "pattern", "[\\i-[:]][\\c-[:]]*"
       });		
    addAnnotation
      (negativeIntegerEDataType, 
       source, 
       new String[] 
       {
       "name", "negativeInteger",
       "baseType", "nonPositiveInteger",
       "maxInclusive", "-1"
       });		
    addAnnotation
      (nmtokenEDataType, 
       source, 
       new String[] 
       {
       "name", "NMTOKEN",
       "baseType", "token",
       "pattern", "\\c+"
       });		
    addAnnotation
      (nmtokensEDataType, 
       source, 
       new String[] 
       {
       "name", "NMTOKENS",
       "baseType", "NMTOKENS_._base",
       "minLength", "1"
       });		
    addAnnotation
      (nmtokensBaseEDataType, 
       source, 
       new String[] 
       {
       "name", "NMTOKENS_._base",
       "itemType", "NMTOKEN"
       });		
    addAnnotation
      (nonNegativeIntegerEDataType, 
       source, 
       new String[] 
       {
       "name", "nonNegativeInteger",
       "baseType", "integer",
       "minInclusive", "0"
       });		
    addAnnotation
      (nonPositiveIntegerEDataType, 
       source, 
       new String[] 
       {
       "name", "nonPositiveInteger",
       "baseType", "integer",
       "maxInclusive", "0"
       });		
    addAnnotation
      (normalizedStringEDataType, 
       source, 
       new String[] 
       {
       "name", "normalizedString",
       "baseType", "string",
       "whiteSpace", "replace"
       });		
    addAnnotation
      (notationEDataType, 
       source, 
       new String[] 
       {
       "name", "NOTATION",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (positiveIntegerEDataType, 
       source, 
       new String[] 
       {
       "name", "positiveInteger",
       "baseType", "nonNegativeInteger",
       "minInclusive", "1"
       });		
    addAnnotation
      (processingInstructionEClass, 
       source, 
       new String[] 
       {
       "name", "processingInstruction_._type",
       "kind", "empty"
       });		
    addAnnotation
      (getProcessingInstruction_Data(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "data"
       });		
    addAnnotation
      (getProcessingInstruction_Target(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "target"
       });		
    addAnnotation
      (qNameEDataType, 
       source, 
       new String[] 
       {
       "name", "QName",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (shortEDataType, 
       source, 
       new String[] 
       {
       "name", "short"
       });		
    addAnnotation
      (shortObjectEDataType, 
       source, 
       new String[] 
       {
       "name", "short:Object",
       "baseType", "short"
       });		
    addAnnotation
      (simpleAnyTypeEClass, 
       source, 
       new String[] 
       {
       "name", "simpleAnyType",
       "kind", "simple"
       });		
    addAnnotation
      (getSimpleAnyType_RawValue(), 
       source, 
       new String[] 
       {
       "name", ":3",
       "kind", "simple"
       });		
    addAnnotation
      (getSimpleAnyType_Value(), 
       source, 
       new String[] 
       {
       "name", ":4",
       "kind", "simple"
       });		
    addAnnotation
      (getSimpleAnyType_InstanceType(), 
       source, 
       new String[] 
       {
       "name", ":5",
       "kind", "simple"
       });		
    addAnnotation
      (stringEDataType, 
       source, 
       new String[] 
       {
       "name", "string",
       "whiteSpace", "preserve"
       });		
    addAnnotation
      (timeEDataType, 
       source, 
       new String[] 
       {
       "name", "time",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (tokenEDataType, 
       source, 
       new String[] 
       {
       "name", "token",
       "baseType", "normalizedString",
       "whiteSpace", "collapse"
       });		
    addAnnotation
      (unsignedByteEDataType, 
       source, 
       new String[] 
       {
       "name", "unsignedByte",
       "maxInclusive", "255",
       "minInclusive", "0"
       });		
    addAnnotation
      (unsignedByteObjectEDataType, 
       source, 
       new String[] 
       {
       "name", "unsignedByte:Object",
       "baseType", "unsignedByte"
       });		
    addAnnotation
      (unsignedIntEDataType, 
       source, 
       new String[] 
       {
       "name", "unsignedInt",
       "maxInclusive", "4294967295",
       "minInclusive", "0"
       });		
    addAnnotation
      (unsignedIntObjectEDataType, 
       source, 
       new String[] 
       {
       "name", "unsignedInt:Object",
       "baseType", "unsignedInt"
       });		
    addAnnotation
      (unsignedLongEDataType, 
       source, 
       new String[] 
       {
       "name", "unsignedLong",
       "baseType", "nonNegativeInteger",
       "maxInclusive", "18446744073709551615",
       "minInclusive", "0"
       });		
    addAnnotation
      (unsignedShortEDataType, 
       source, 
       new String[] 
       {
       "name", "unsignedShort",
       "maxInclusive", "65535",
       "minInclusive", "0"
       });		
    addAnnotation
      (unsignedShortObjectEDataType, 
       source, 
       new String[] 
       {
       "name", "unsignedShort:Object",
       "baseType", "unsignedShort"
       });		
    addAnnotation
      (xmlTypeDocumentRootEClass, 
       source, 
       new String[] 
       {
       "name", "",
       "kind", "mixed"
       });		
    addAnnotation
      (getXMLTypeDocumentRoot_Mixed(), 
       source, 
       new String[] 
       {
       "kind", "elementWildcard",
       "name", ":mixed"
       });		
    addAnnotation
      (getXMLTypeDocumentRoot_XMLNSPrefixMap(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "xmlns:prefix"
       });		
    addAnnotation
      (getXMLTypeDocumentRoot_XSISchemaLocation(), 
       source, 
       new String[] 
       {
       "kind", "attribute",
       "name", "xsi:schemaLocation"
       });		
    addAnnotation
      (getXMLTypeDocumentRoot_CDATA(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "cDATA",
       "namespace", "##targetNamespace"
       });			
    addAnnotation
      (getXMLTypeDocumentRoot_Comment(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "comment",
       "namespace", "##targetNamespace"
       });			
    addAnnotation
      (getXMLTypeDocumentRoot_ProcessingInstruction(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "processingInstruction",
       "namespace", "##targetNamespace"
       });			
    addAnnotation
      (getXMLTypeDocumentRoot_Text(), 
       source, 
       new String[] 
       {
       "kind", "element",
       "name", "text",
       "namespace", "##targetNamespace"
       });	
  }

  /**
   * Initializes the annotations for <b>null</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected void createNullAnnotations()
  {
    // Don't create anything.
  }
} //XMLTypePackageImpl
