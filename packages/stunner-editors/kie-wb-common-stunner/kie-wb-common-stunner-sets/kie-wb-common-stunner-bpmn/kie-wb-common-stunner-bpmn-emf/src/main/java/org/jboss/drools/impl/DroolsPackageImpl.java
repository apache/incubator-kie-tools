/**
 */
package org.jboss.drools.impl;

import java.math.BigInteger;

import com.google.gwt.user.client.rpc.IsSerializable;
import org.eclipse.emf.common.util.Reflect;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.jboss.drools.DocumentRoot;
import org.jboss.drools.DroolsFactory;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.GlobalType;
import org.jboss.drools.ImportType;
import org.jboss.drools.MetaDataType;
import org.jboss.drools.OnEntryScriptType;
import org.jboss.drools.OnExitScriptType;
import org.jboss.drools.util.DroolsValidator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class DroolsPackageImpl extends EPackageImpl implements DroolsPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass documentRootEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass globalTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass importTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass metaDataTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass onEntryScriptTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass onExitScriptTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType packageNameTypeEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType priorityTypeEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType ruleFlowGroupTypeEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType taskNameTypeEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType versionTypeEDataType = null;

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
	 * @see org.jboss.drools.DroolsPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private DroolsPackageImpl() {
		super(eNS_URI, DroolsFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link DroolsPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static DroolsPackage init() {
		if (isInited) return (DroolsPackage)EPackage.Registry.INSTANCE.getEPackage(DroolsPackage.eNS_URI);

		initializeRegistryHelpers();

		// Obtain or create and register package
		Object registeredDroolsPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		DroolsPackageImpl theDroolsPackage = registeredDroolsPackage instanceof DroolsPackageImpl ? (DroolsPackageImpl)registeredDroolsPackage : new DroolsPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		XMLTypePackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theDroolsPackage.createPackageContents();

		// Initialize created meta-data
		theDroolsPackage.initializePackageContents();

		// Register package validator
		EValidator.Registry.INSTANCE.put
			(theDroolsPackage,
			 new EValidator.Descriptor() {
				 @Override
				 public EValidator getEValidator() {
					 return DroolsValidator.INSTANCE;
				 }
			 });

		// Mark meta-data to indicate it can't be changed
		theDroolsPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(DroolsPackage.eNS_URI, theDroolsPackage);
		return theDroolsPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void initializeRegistryHelpers() {
		Reflect.register
			(DocumentRoot.class,
			 new Reflect.Helper() {
				 public boolean isInstance(Object instance) {
					 return instance instanceof DocumentRoot;
				 }

				 public Object newArrayInstance(int size) {
					 return new DocumentRoot[size];
				 }
			 });
		Reflect.register
			(GlobalType.class,
			 new Reflect.Helper() {
				 public boolean isInstance(Object instance) {
					 return instance instanceof GlobalType;
				 }

				 public Object newArrayInstance(int size) {
					 return new GlobalType[size];
				 }
			 });
		Reflect.register
			(ImportType.class,
			 new Reflect.Helper() {
				 public boolean isInstance(Object instance) {
					 return instance instanceof ImportType;
				 }

				 public Object newArrayInstance(int size) {
					 return new ImportType[size];
				 }
			 });
		Reflect.register
			(MetaDataType.class,
			 new Reflect.Helper() {
				 public boolean isInstance(Object instance) {
					 return instance instanceof MetaDataType;
				 }

				 public Object newArrayInstance(int size) {
					 return new MetaDataType[size];
				 }
			 });
		Reflect.register
			(OnEntryScriptType.class,
			 new Reflect.Helper() {
				 public boolean isInstance(Object instance) {
					 return instance instanceof OnEntryScriptType;
				 }

				 public Object newArrayInstance(int size) {
					 return new OnEntryScriptType[size];
				 }
			 });
		Reflect.register
			(OnExitScriptType.class,
			 new Reflect.Helper() {
				 public boolean isInstance(Object instance) {
					 return instance instanceof OnExitScriptType;
				 }

				 public Object newArrayInstance(int size) {
					 return new OnExitScriptType[size];
				 }
			 });
		Reflect.register
			(String.class,
			 new Reflect.Helper() {
				 public boolean isInstance(Object instance) {
					 return instance instanceof String;
				 }

				 public Object newArrayInstance(int size) {
					 return new String[size];
				 }
		});
		Reflect.register
			(BigInteger.class,
			 new Reflect.Helper() {
				 public boolean isInstance(Object instance) {
					 return instance instanceof BigInteger;
				 }

				 public Object newArrayInstance(int size) {
					 return new BigInteger[size];
				 }
		});
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static class AllowList implements IsSerializable, EBasicWhiteList {
		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected DocumentRoot documentRoot;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected GlobalType globalType;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ImportType importType;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected MetaDataType metaDataType;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected OnEntryScriptType onEntryScriptType;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected OnExitScriptType onExitScriptType;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected String packageNameType;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected BigInteger priorityType;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected String ruleFlowGroupType;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected String taskNameType;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected String versionType;

	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDocumentRoot() {
		return documentRootEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDocumentRoot_Mixed() {
		return (EAttribute)documentRootEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_XMLNSPrefixMap() {
		return (EReference)documentRootEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_XSISchemaLocation() {
		return (EReference)documentRootEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Global() {
		return (EReference)documentRootEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Import() {
		return (EReference)documentRootEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_MetaData() {
		return (EReference)documentRootEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_OnEntryScript() {
		return (EReference)documentRootEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_OnExitScript() {
		return (EReference)documentRootEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDocumentRoot_PackageName() {
		return (EAttribute)documentRootEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDocumentRoot_Priority() {
		return (EAttribute)documentRootEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDocumentRoot_RuleFlowGroup() {
		return (EAttribute)documentRootEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDocumentRoot_TaskName() {
		return (EAttribute)documentRootEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDocumentRoot_Version() {
		return (EAttribute)documentRootEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getGlobalType() {
		return globalTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getGlobalType_Identifier() {
		return (EAttribute)globalTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getGlobalType_Type() {
		return (EAttribute)globalTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getImportType() {
		return importTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getImportType_Name() {
		return (EAttribute)importTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMetaDataType() {
		return metaDataTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMetaDataType_MetaValue() {
		return (EAttribute)metaDataTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMetaDataType_Name() {
		return (EAttribute)metaDataTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOnEntryScriptType() {
		return onEntryScriptTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOnEntryScriptType_Script() {
		return (EAttribute)onEntryScriptTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOnEntryScriptType_ScriptFormat() {
		return (EAttribute)onEntryScriptTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOnExitScriptType() {
		return onExitScriptTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOnExitScriptType_Script() {
		return (EAttribute)onExitScriptTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOnExitScriptType_ScriptFormat() {
		return (EAttribute)onExitScriptTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getPackageNameType() {
		return packageNameTypeEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getPriorityType() {
		return priorityTypeEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getRuleFlowGroupType() {
		return ruleFlowGroupTypeEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getTaskNameType() {
		return taskNameTypeEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getVersionType() {
		return versionTypeEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DroolsFactory getDroolsFactory() {
		return (DroolsFactory)getEFactoryInstance();
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
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		documentRootEClass = createEClass(DOCUMENT_ROOT);
		createEAttribute(documentRootEClass, DOCUMENT_ROOT__MIXED);
		createEReference(documentRootEClass, DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
		createEReference(documentRootEClass, DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__GLOBAL);
		createEReference(documentRootEClass, DOCUMENT_ROOT__IMPORT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__META_DATA);
		createEReference(documentRootEClass, DOCUMENT_ROOT__ON_ENTRY_SCRIPT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__ON_EXIT_SCRIPT);
		createEAttribute(documentRootEClass, DOCUMENT_ROOT__PACKAGE_NAME);
		createEAttribute(documentRootEClass, DOCUMENT_ROOT__PRIORITY);
		createEAttribute(documentRootEClass, DOCUMENT_ROOT__RULE_FLOW_GROUP);
		createEAttribute(documentRootEClass, DOCUMENT_ROOT__TASK_NAME);
		createEAttribute(documentRootEClass, DOCUMENT_ROOT__VERSION);

		globalTypeEClass = createEClass(GLOBAL_TYPE);
		createEAttribute(globalTypeEClass, GLOBAL_TYPE__IDENTIFIER);
		createEAttribute(globalTypeEClass, GLOBAL_TYPE__TYPE);

		importTypeEClass = createEClass(IMPORT_TYPE);
		createEAttribute(importTypeEClass, IMPORT_TYPE__NAME);

		metaDataTypeEClass = createEClass(META_DATA_TYPE);
		createEAttribute(metaDataTypeEClass, META_DATA_TYPE__META_VALUE);
		createEAttribute(metaDataTypeEClass, META_DATA_TYPE__NAME);

		onEntryScriptTypeEClass = createEClass(ON_ENTRY_SCRIPT_TYPE);
		createEAttribute(onEntryScriptTypeEClass, ON_ENTRY_SCRIPT_TYPE__SCRIPT);
		createEAttribute(onEntryScriptTypeEClass, ON_ENTRY_SCRIPT_TYPE__SCRIPT_FORMAT);

		onExitScriptTypeEClass = createEClass(ON_EXIT_SCRIPT_TYPE);
		createEAttribute(onExitScriptTypeEClass, ON_EXIT_SCRIPT_TYPE__SCRIPT);
		createEAttribute(onExitScriptTypeEClass, ON_EXIT_SCRIPT_TYPE__SCRIPT_FORMAT);

		// Create data types
		packageNameTypeEDataType = createEDataType(PACKAGE_NAME_TYPE);
		priorityTypeEDataType = createEDataType(PRIORITY_TYPE);
		ruleFlowGroupTypeEDataType = createEDataType(RULE_FLOW_GROUP_TYPE);
		taskNameTypeEDataType = createEDataType(TASK_NAME_TYPE);
		versionTypeEDataType = createEDataType(VERSION_TYPE);
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
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		XMLTypePackage theXMLTypePackage = (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(documentRootEClass, DocumentRoot.class, "DocumentRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDocumentRoot_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_XMLNSPrefixMap(), ecorePackage.getEStringToStringMapEntry(), null, "xMLNSPrefixMap", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_XSISchemaLocation(), ecorePackage.getEStringToStringMapEntry(), null, "xSISchemaLocation", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Global(), this.getGlobalType(), null, "global", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Import(), this.getImportType(), null, "import", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_MetaData(), this.getMetaDataType(), null, "metaData", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_OnEntryScript(), this.getOnEntryScriptType(), null, "onEntryScript", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_OnExitScript(), this.getOnExitScriptType(), null, "onExitScript", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getDocumentRoot_PackageName(), this.getPackageNameType(), "packageName", null, 0, 1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDocumentRoot_Priority(), this.getPriorityType(), "priority", null, 0, 1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDocumentRoot_RuleFlowGroup(), this.getRuleFlowGroupType(), "ruleFlowGroup", null, 0, 1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDocumentRoot_TaskName(), this.getTaskNameType(), "taskName", null, 0, 1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDocumentRoot_Version(), this.getVersionType(), "version", null, 0, 1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(globalTypeEClass, GlobalType.class, "GlobalType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getGlobalType_Identifier(), theXMLTypePackage.getString(), "identifier", null, 1, 1, GlobalType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGlobalType_Type(), theXMLTypePackage.getString(), "type", null, 1, 1, GlobalType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(importTypeEClass, ImportType.class, "ImportType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getImportType_Name(), theXMLTypePackage.getString(), "name", null, 1, 1, ImportType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(metaDataTypeEClass, MetaDataType.class, "MetaDataType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMetaDataType_MetaValue(), theXMLTypePackage.getString(), "metaValue", null, 1, 1, MetaDataType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMetaDataType_Name(), theXMLTypePackage.getString(), "name", null, 1, 1, MetaDataType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(onEntryScriptTypeEClass, OnEntryScriptType.class, "OnEntryScriptType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getOnEntryScriptType_Script(), theXMLTypePackage.getString(), "script", null, 1, 1, OnEntryScriptType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getOnEntryScriptType_ScriptFormat(), theXMLTypePackage.getString(), "scriptFormat", null, 1, 1, OnEntryScriptType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(onExitScriptTypeEClass, OnExitScriptType.class, "OnExitScriptType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getOnExitScriptType_Script(), theXMLTypePackage.getString(), "script", null, 1, 1, OnExitScriptType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getOnExitScriptType_ScriptFormat(), theXMLTypePackage.getString(), "scriptFormat", null, 1, 1, OnExitScriptType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize data types
		initEDataType(packageNameTypeEDataType, String.class, "PackageNameType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(priorityTypeEDataType, BigInteger.class, "PriorityType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(ruleFlowGroupTypeEDataType, String.class, "RuleFlowGroupType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(taskNameTypeEDataType, String.class, "TaskNameType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(versionTypeEDataType, String.class, "VersionType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

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
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
		addAnnotation
		  (documentRootEClass,
		   source,
		   new String[] {
			   "name", "",
			   "kind", "mixed"
		   });
		addAnnotation
		  (getDocumentRoot_Mixed(),
		   source,
		   new String[] {
			   "kind", "elementWildcard",
			   "name", ":mixed"
		   });
		addAnnotation
		  (getDocumentRoot_XMLNSPrefixMap(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "xmlns:prefix"
		   });
		addAnnotation
		  (getDocumentRoot_XSISchemaLocation(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "xsi:schemaLocation"
		   });
		addAnnotation
		  (getDocumentRoot_Global(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "global",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getDocumentRoot_Import(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "import",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getDocumentRoot_MetaData(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "metaData",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getDocumentRoot_OnEntryScript(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "onEntry-script",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getDocumentRoot_OnExitScript(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "onExit-script",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getDocumentRoot_PackageName(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "packageName",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getDocumentRoot_Priority(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "priority",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getDocumentRoot_RuleFlowGroup(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "ruleFlowGroup",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getDocumentRoot_TaskName(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "taskName",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getDocumentRoot_Version(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "version",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (globalTypeEClass,
		   source,
		   new String[] {
			   "name", "global_._type",
			   "kind", "empty"
		   });
		addAnnotation
		  (getGlobalType_Identifier(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "identifier"
		   });
		addAnnotation
		  (getGlobalType_Type(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "type"
		   });
		addAnnotation
		  (importTypeEClass,
		   source,
		   new String[] {
			   "name", "import_._type",
			   "kind", "empty"
		   });
		addAnnotation
		  (getImportType_Name(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "name"
		   });
		addAnnotation
		  (metaDataTypeEClass,
		   source,
		   new String[] {
			   "name", "metaData_._type",
			   "kind", "elementOnly"
		   });
		addAnnotation
		  (getMetaDataType_MetaValue(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "metaValue",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getMetaDataType_Name(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "name"
		   });
		addAnnotation
		  (onEntryScriptTypeEClass,
		   source,
		   new String[] {
			   "name", "onEntry-script_._type",
			   "kind", "elementOnly"
		   });
		addAnnotation
		  (getOnEntryScriptType_Script(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "script",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getOnEntryScriptType_ScriptFormat(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "scriptFormat"
		   });
		addAnnotation
		  (onExitScriptTypeEClass,
		   source,
		   new String[] {
			   "name", "onExit-script_._type",
			   "kind", "elementOnly"
		   });
		addAnnotation
		  (getOnExitScriptType_Script(),
		   source,
		   new String[] {
			   "kind", "element",
			   "name", "script",
			   "namespace", "##targetNamespace"
		   });
		addAnnotation
		  (getOnExitScriptType_ScriptFormat(),
		   source,
		   new String[] {
			   "kind", "attribute",
			   "name", "scriptFormat"
		   });
		addAnnotation
		  (packageNameTypeEDataType,
		   source,
		   new String[] {
			   "name", "packageName_._type",
			   "baseType", "http://www.eclipse.org/emf/2003/XMLType#string"
		   });
		addAnnotation
		  (priorityTypeEDataType,
		   source,
		   new String[] {
			   "name", "priority_._type",
			   "baseType", "http://www.eclipse.org/emf/2003/XMLType#integer",
			   "minInclusive", "1"
		   });
		addAnnotation
		  (ruleFlowGroupTypeEDataType,
		   source,
		   new String[] {
			   "name", "ruleFlowGroup_._type",
			   "baseType", "http://www.eclipse.org/emf/2003/XMLType#string"
		   });
		addAnnotation
		  (taskNameTypeEDataType,
		   source,
		   new String[] {
			   "name", "taskName_._type",
			   "baseType", "http://www.eclipse.org/emf/2003/XMLType#string"
		   });
		addAnnotation
		  (versionTypeEDataType,
		   source,
		   new String[] {
			   "name", "version_._type",
			   "baseType", "http://www.eclipse.org/emf/2003/XMLType#string"
		   });
	}

} //DroolsPackageImpl
