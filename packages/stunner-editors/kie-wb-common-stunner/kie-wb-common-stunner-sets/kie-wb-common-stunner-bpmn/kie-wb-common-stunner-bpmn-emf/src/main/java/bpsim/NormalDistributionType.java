/**
 */
package bpsim;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Normal Distribution Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link bpsim.NormalDistributionType#getMean <em>Mean</em>}</li>
 *   <li>{@link bpsim.NormalDistributionType#getStandardDeviation <em>Standard Deviation</em>}</li>
 * </ul>
 *
 * @see bpsim.BpsimPackage#getNormalDistributionType()
 * @model extendedMetaData="name='NormalDistribution_._type' kind='empty'"
 * @generated
 */
public interface NormalDistributionType extends DistributionParameter {
	/**
	 * Returns the value of the '<em><b>Mean</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mean</em>' attribute.
	 * @see #isSetMean()
	 * @see #unsetMean()
	 * @see #setMean(double)
	 * @see bpsim.BpsimPackage#getNormalDistributionType_Mean()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double"
	 *        extendedMetaData="kind='attribute' name='mean'"
	 * @generated
	 */
	double getMean();

	/**
	 * Sets the value of the '{@link bpsim.NormalDistributionType#getMean <em>Mean</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mean</em>' attribute.
	 * @see #isSetMean()
	 * @see #unsetMean()
	 * @see #getMean()
	 * @generated
	 */
	void setMean(double value);

	/**
	 * Unsets the value of the '{@link bpsim.NormalDistributionType#getMean <em>Mean</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMean()
	 * @see #getMean()
	 * @see #setMean(double)
	 * @generated
	 */
	void unsetMean();

	/**
	 * Returns whether the value of the '{@link bpsim.NormalDistributionType#getMean <em>Mean</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Mean</em>' attribute is set.
	 * @see #unsetMean()
	 * @see #getMean()
	 * @see #setMean(double)
	 * @generated
	 */
	boolean isSetMean();

	/**
	 * Returns the value of the '<em><b>Standard Deviation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Standard Deviation</em>' attribute.
	 * @see #isSetStandardDeviation()
	 * @see #unsetStandardDeviation()
	 * @see #setStandardDeviation(double)
	 * @see bpsim.BpsimPackage#getNormalDistributionType_StandardDeviation()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double"
	 *        extendedMetaData="kind='attribute' name='standardDeviation'"
	 * @generated
	 */
	double getStandardDeviation();

	/**
	 * Sets the value of the '{@link bpsim.NormalDistributionType#getStandardDeviation <em>Standard Deviation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Standard Deviation</em>' attribute.
	 * @see #isSetStandardDeviation()
	 * @see #unsetStandardDeviation()
	 * @see #getStandardDeviation()
	 * @generated
	 */
	void setStandardDeviation(double value);

	/**
	 * Unsets the value of the '{@link bpsim.NormalDistributionType#getStandardDeviation <em>Standard Deviation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetStandardDeviation()
	 * @see #getStandardDeviation()
	 * @see #setStandardDeviation(double)
	 * @generated
	 */
	void unsetStandardDeviation();

	/**
	 * Returns whether the value of the '{@link bpsim.NormalDistributionType#getStandardDeviation <em>Standard Deviation</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Standard Deviation</em>' attribute is set.
	 * @see #unsetStandardDeviation()
	 * @see #getStandardDeviation()
	 * @see #setStandardDeviation(double)
	 * @generated
	 */
	boolean isSetStandardDeviation();

} // NormalDistributionType
