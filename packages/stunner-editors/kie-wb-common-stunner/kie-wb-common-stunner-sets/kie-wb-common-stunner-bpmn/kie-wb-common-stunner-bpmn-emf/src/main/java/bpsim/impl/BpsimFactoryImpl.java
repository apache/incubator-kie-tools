/**
 */
package bpsim.impl;

import bpsim.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class BpsimFactoryImpl extends EFactoryImpl implements BpsimFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static BpsimFactory init() {
		try {
			BpsimFactory theBpsimFactory = (BpsimFactory)EPackage.Registry.INSTANCE.getEFactory(BpsimPackage.eNS_URI);
			if (theBpsimFactory != null) {
				return theBpsimFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new BpsimFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BpsimFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case BpsimPackage.BETA_DISTRIBUTION_TYPE: return createBetaDistributionType();
			case BpsimPackage.BINOMIAL_DISTRIBUTION_TYPE: return createBinomialDistributionType();
			case BpsimPackage.BOOLEAN_PARAMETER_TYPE: return createBooleanParameterType();
			case BpsimPackage.BP_SIM_DATA_TYPE: return createBPSimDataType();
			case BpsimPackage.CALENDAR: return createCalendar();
			case BpsimPackage.CONSTANT_PARAMETER: return createConstantParameter();
			case BpsimPackage.CONTROL_PARAMETERS: return createControlParameters();
			case BpsimPackage.COST_PARAMETERS: return createCostParameters();
			case BpsimPackage.DATE_TIME_PARAMETER_TYPE: return createDateTimeParameterType();
			case BpsimPackage.DISTRIBUTION_PARAMETER: return createDistributionParameter();
			case BpsimPackage.DOCUMENT_ROOT: return createDocumentRoot();
			case BpsimPackage.DURATION_PARAMETER_TYPE: return createDurationParameterType();
			case BpsimPackage.ELEMENT_PARAMETERS: return createElementParameters();
			case BpsimPackage.ELEMENT_PARAMETERS_TYPE: return createElementParametersType();
			case BpsimPackage.ENUM_PARAMETER_TYPE: return createEnumParameterType();
			case BpsimPackage.ERLANG_DISTRIBUTION_TYPE: return createErlangDistributionType();
			case BpsimPackage.EXPRESSION_PARAMETER_TYPE: return createExpressionParameterType();
			case BpsimPackage.FLOATING_PARAMETER_TYPE: return createFloatingParameterType();
			case BpsimPackage.GAMMA_DISTRIBUTION_TYPE: return createGammaDistributionType();
			case BpsimPackage.LOG_NORMAL_DISTRIBUTION_TYPE: return createLogNormalDistributionType();
			case BpsimPackage.NEGATIVE_EXPONENTIAL_DISTRIBUTION_TYPE: return createNegativeExponentialDistributionType();
			case BpsimPackage.NORMAL_DISTRIBUTION_TYPE: return createNormalDistributionType();
			case BpsimPackage.NUMERIC_PARAMETER_TYPE: return createNumericParameterType();
			case BpsimPackage.PARAMETER: return createParameter();
			case BpsimPackage.PARAMETER_VALUE: return createParameterValue();
			case BpsimPackage.POISSON_DISTRIBUTION_TYPE: return createPoissonDistributionType();
			case BpsimPackage.PRIORITY_PARAMETERS: return createPriorityParameters();
			case BpsimPackage.PROPERTY_PARAMETERS: return createPropertyParameters();
			case BpsimPackage.PROPERTY_TYPE: return createPropertyType();
			case BpsimPackage.RESOURCE_PARAMETERS: return createResourceParameters();
			case BpsimPackage.SCENARIO: return createScenario();
			case BpsimPackage.SCENARIO_PARAMETERS: return createScenarioParameters();
			case BpsimPackage.SCENARIO_PARAMETERS_TYPE: return createScenarioParametersType();
			case BpsimPackage.STRING_PARAMETER_TYPE: return createStringParameterType();
			case BpsimPackage.TIME_PARAMETERS: return createTimeParameters();
			case BpsimPackage.TRIANGULAR_DISTRIBUTION_TYPE: return createTriangularDistributionType();
			case BpsimPackage.TRUNCATED_NORMAL_DISTRIBUTION_TYPE: return createTruncatedNormalDistributionType();
			case BpsimPackage.UNIFORM_DISTRIBUTION_TYPE: return createUniformDistributionType();
			case BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE: return createUserDistributionDataPointType();
			case BpsimPackage.USER_DISTRIBUTION_TYPE: return createUserDistributionType();
			case BpsimPackage.VENDOR_EXTENSION: return createVendorExtension();
			case BpsimPackage.WEIBULL_DISTRIBUTION_TYPE: return createWeibullDistributionType();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case BpsimPackage.RESULT_TYPE:
				return createResultTypeFromString(eDataType, initialValue);
			case BpsimPackage.TIME_UNIT:
				return createTimeUnitFromString(eDataType, initialValue);
			case BpsimPackage.RESULT_TYPE_OBJECT:
				return createResultTypeObjectFromString(eDataType, initialValue);
			case BpsimPackage.TIME_UNIT_OBJECT:
				return createTimeUnitObjectFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case BpsimPackage.RESULT_TYPE:
				return convertResultTypeToString(eDataType, instanceValue);
			case BpsimPackage.TIME_UNIT:
				return convertTimeUnitToString(eDataType, instanceValue);
			case BpsimPackage.RESULT_TYPE_OBJECT:
				return convertResultTypeObjectToString(eDataType, instanceValue);
			case BpsimPackage.TIME_UNIT_OBJECT:
				return convertTimeUnitObjectToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BetaDistributionType createBetaDistributionType() {
		BetaDistributionTypeImpl betaDistributionType = new BetaDistributionTypeImpl();
		return betaDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BinomialDistributionType createBinomialDistributionType() {
		BinomialDistributionTypeImpl binomialDistributionType = new BinomialDistributionTypeImpl();
		return binomialDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BooleanParameterType createBooleanParameterType() {
		BooleanParameterTypeImpl booleanParameterType = new BooleanParameterTypeImpl();
		return booleanParameterType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BPSimDataType createBPSimDataType() {
		BPSimDataTypeImpl bpSimDataType = new BPSimDataTypeImpl();
		return bpSimDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Calendar createCalendar() {
		CalendarImpl calendar = new CalendarImpl();
		return calendar;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ConstantParameter createConstantParameter() {
		ConstantParameterImpl constantParameter = new ConstantParameterImpl();
		return constantParameter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ControlParameters createControlParameters() {
		ControlParametersImpl controlParameters = new ControlParametersImpl();
		return controlParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CostParameters createCostParameters() {
		CostParametersImpl costParameters = new CostParametersImpl();
		return costParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DateTimeParameterType createDateTimeParameterType() {
		DateTimeParameterTypeImpl dateTimeParameterType = new DateTimeParameterTypeImpl();
		return dateTimeParameterType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DistributionParameter createDistributionParameter() {
		DistributionParameterImpl distributionParameter = new DistributionParameterImpl();
		return distributionParameter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DocumentRoot createDocumentRoot() {
		DocumentRootImpl documentRoot = new DocumentRootImpl();
		return documentRoot;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DurationParameterType createDurationParameterType() {
		DurationParameterTypeImpl durationParameterType = new DurationParameterTypeImpl();
		return durationParameterType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ElementParameters createElementParameters() {
		ElementParametersImpl elementParameters = new ElementParametersImpl();
		return elementParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ElementParametersType createElementParametersType() {
		ElementParametersTypeImpl elementParametersType = new ElementParametersTypeImpl();
		return elementParametersType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EnumParameterType createEnumParameterType() {
		EnumParameterTypeImpl enumParameterType = new EnumParameterTypeImpl();
		return enumParameterType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ErlangDistributionType createErlangDistributionType() {
		ErlangDistributionTypeImpl erlangDistributionType = new ErlangDistributionTypeImpl();
		return erlangDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ExpressionParameterType createExpressionParameterType() {
		ExpressionParameterTypeImpl expressionParameterType = new ExpressionParameterTypeImpl();
		return expressionParameterType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FloatingParameterType createFloatingParameterType() {
		FloatingParameterTypeImpl floatingParameterType = new FloatingParameterTypeImpl();
		return floatingParameterType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public GammaDistributionType createGammaDistributionType() {
		GammaDistributionTypeImpl gammaDistributionType = new GammaDistributionTypeImpl();
		return gammaDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public LogNormalDistributionType createLogNormalDistributionType() {
		LogNormalDistributionTypeImpl logNormalDistributionType = new LogNormalDistributionTypeImpl();
		return logNormalDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NegativeExponentialDistributionType createNegativeExponentialDistributionType() {
		NegativeExponentialDistributionTypeImpl negativeExponentialDistributionType = new NegativeExponentialDistributionTypeImpl();
		return negativeExponentialDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NormalDistributionType createNormalDistributionType() {
		NormalDistributionTypeImpl normalDistributionType = new NormalDistributionTypeImpl();
		return normalDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NumericParameterType createNumericParameterType() {
		NumericParameterTypeImpl numericParameterType = new NumericParameterTypeImpl();
		return numericParameterType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Parameter createParameter() {
		ParameterImpl parameter = new ParameterImpl();
		return parameter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ParameterValue createParameterValue() {
		ParameterValueImpl parameterValue = new ParameterValueImpl();
		return parameterValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PoissonDistributionType createPoissonDistributionType() {
		PoissonDistributionTypeImpl poissonDistributionType = new PoissonDistributionTypeImpl();
		return poissonDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PriorityParameters createPriorityParameters() {
		PriorityParametersImpl priorityParameters = new PriorityParametersImpl();
		return priorityParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PropertyParameters createPropertyParameters() {
		PropertyParametersImpl propertyParameters = new PropertyParametersImpl();
		return propertyParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PropertyType createPropertyType() {
		PropertyTypeImpl propertyType = new PropertyTypeImpl();
		return propertyType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResourceParameters createResourceParameters() {
		ResourceParametersImpl resourceParameters = new ResourceParametersImpl();
		return resourceParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Scenario createScenario() {
		ScenarioImpl scenario = new ScenarioImpl();
		return scenario;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ScenarioParameters createScenarioParameters() {
		ScenarioParametersImpl scenarioParameters = new ScenarioParametersImpl();
		return scenarioParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ScenarioParametersType createScenarioParametersType() {
		ScenarioParametersTypeImpl scenarioParametersType = new ScenarioParametersTypeImpl();
		return scenarioParametersType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public StringParameterType createStringParameterType() {
		StringParameterTypeImpl stringParameterType = new StringParameterTypeImpl();
		return stringParameterType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TimeParameters createTimeParameters() {
		TimeParametersImpl timeParameters = new TimeParametersImpl();
		return timeParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TriangularDistributionType createTriangularDistributionType() {
		TriangularDistributionTypeImpl triangularDistributionType = new TriangularDistributionTypeImpl();
		return triangularDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TruncatedNormalDistributionType createTruncatedNormalDistributionType() {
		TruncatedNormalDistributionTypeImpl truncatedNormalDistributionType = new TruncatedNormalDistributionTypeImpl();
		return truncatedNormalDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public UniformDistributionType createUniformDistributionType() {
		UniformDistributionTypeImpl uniformDistributionType = new UniformDistributionTypeImpl();
		return uniformDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public UserDistributionDataPointType createUserDistributionDataPointType() {
		UserDistributionDataPointTypeImpl userDistributionDataPointType = new UserDistributionDataPointTypeImpl();
		return userDistributionDataPointType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public UserDistributionType createUserDistributionType() {
		UserDistributionTypeImpl userDistributionType = new UserDistributionTypeImpl();
		return userDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public VendorExtension createVendorExtension() {
		VendorExtensionImpl vendorExtension = new VendorExtensionImpl();
		return vendorExtension;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public WeibullDistributionType createWeibullDistributionType() {
		WeibullDistributionTypeImpl weibullDistributionType = new WeibullDistributionTypeImpl();
		return weibullDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ResultType createResultTypeFromString(EDataType eDataType, String initialValue) {
		ResultType result = ResultType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertResultTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TimeUnit createTimeUnitFromString(EDataType eDataType, String initialValue) {
		TimeUnit result = TimeUnit.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTimeUnitToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ResultType createResultTypeObjectFromString(EDataType eDataType, String initialValue) {
		return createResultTypeFromString(BpsimPackage.Literals.RESULT_TYPE, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertResultTypeObjectToString(EDataType eDataType, Object instanceValue) {
		return convertResultTypeToString(BpsimPackage.Literals.RESULT_TYPE, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TimeUnit createTimeUnitObjectFromString(EDataType eDataType, String initialValue) {
		return createTimeUnitFromString(BpsimPackage.Literals.TIME_UNIT, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTimeUnitObjectToString(EDataType eDataType, Object instanceValue) {
		return convertTimeUnitToString(BpsimPackage.Literals.TIME_UNIT, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BpsimPackage getBpsimPackage() {
		return (BpsimPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static BpsimPackage getPackage() {
		return BpsimPackage.eINSTANCE;
	}

} //BpsimFactoryImpl
