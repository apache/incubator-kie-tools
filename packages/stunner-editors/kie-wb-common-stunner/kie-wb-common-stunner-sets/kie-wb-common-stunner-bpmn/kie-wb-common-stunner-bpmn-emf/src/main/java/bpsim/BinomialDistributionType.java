/**
 */
package bpsim;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Binomial Distribution Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link bpsim.BinomialDistributionType#getProbability <em>Probability</em>}</li>
 *   <li>{@link bpsim.BinomialDistributionType#getTrials <em>Trials</em>}</li>
 * </ul>
 *
 * @see bpsim.BpsimPackage#getBinomialDistributionType()
 * @model extendedMetaData="name='BinomialDistribution_._type' kind='empty'"
 * @generated
 */
public interface BinomialDistributionType extends DistributionParameter {
	/**
	 * Returns the value of the '<em><b>Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Probability</em>' attribute.
	 * @see #isSetProbability()
	 * @see #unsetProbability()
	 * @see #setProbability(double)
	 * @see bpsim.BpsimPackage#getBinomialDistributionType_Probability()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double"
	 *        extendedMetaData="kind='attribute' name='probability'"
	 * @generated
	 */
	double getProbability();

	/**
	 * Sets the value of the '{@link bpsim.BinomialDistributionType#getProbability <em>Probability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Probability</em>' attribute.
	 * @see #isSetProbability()
	 * @see #unsetProbability()
	 * @see #getProbability()
	 * @generated
	 */
	void setProbability(double value);

	/**
	 * Unsets the value of the '{@link bpsim.BinomialDistributionType#getProbability <em>Probability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetProbability()
	 * @see #getProbability()
	 * @see #setProbability(double)
	 * @generated
	 */
	void unsetProbability();

	/**
	 * Returns whether the value of the '{@link bpsim.BinomialDistributionType#getProbability <em>Probability</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Probability</em>' attribute is set.
	 * @see #unsetProbability()
	 * @see #getProbability()
	 * @see #setProbability(double)
	 * @generated
	 */
	boolean isSetProbability();

	/**
	 * Returns the value of the '<em><b>Trials</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Trials</em>' attribute.
	 * @see #isSetTrials()
	 * @see #unsetTrials()
	 * @see #setTrials(long)
	 * @see bpsim.BpsimPackage#getBinomialDistributionType_Trials()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Long"
	 *        extendedMetaData="kind='attribute' name='trials'"
	 * @generated
	 */
	long getTrials();

	/**
	 * Sets the value of the '{@link bpsim.BinomialDistributionType#getTrials <em>Trials</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Trials</em>' attribute.
	 * @see #isSetTrials()
	 * @see #unsetTrials()
	 * @see #getTrials()
	 * @generated
	 */
	void setTrials(long value);

	/**
	 * Unsets the value of the '{@link bpsim.BinomialDistributionType#getTrials <em>Trials</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetTrials()
	 * @see #getTrials()
	 * @see #setTrials(long)
	 * @generated
	 */
	void unsetTrials();

	/**
	 * Returns whether the value of the '{@link bpsim.BinomialDistributionType#getTrials <em>Trials</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Trials</em>' attribute is set.
	 * @see #unsetTrials()
	 * @see #getTrials()
	 * @see #setTrials(long)
	 * @generated
	 */
	boolean isSetTrials();

} // BinomialDistributionType
