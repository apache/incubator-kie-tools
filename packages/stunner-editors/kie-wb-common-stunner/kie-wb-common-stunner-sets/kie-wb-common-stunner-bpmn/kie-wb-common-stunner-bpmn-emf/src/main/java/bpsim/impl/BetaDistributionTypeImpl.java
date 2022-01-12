/**
 */
package bpsim.impl;

import bpsim.BetaDistributionType;
import bpsim.BpsimPackage;

import com.google.gwt.user.client.rpc.GwtTransient;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Beta Distribution Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link bpsim.impl.BetaDistributionTypeImpl#getScale <em>Scale</em>}</li>
 *   <li>{@link bpsim.impl.BetaDistributionTypeImpl#getShape <em>Shape</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BetaDistributionTypeImpl extends DistributionParameterImpl implements BetaDistributionType {
	/**
	 * The default value of the '{@link #getScale() <em>Scale</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScale()
	 * @generated
	 * @ordered
	 */
	protected static final double SCALE_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getScale() <em>Scale</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScale()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected double scale = SCALE_EDEFAULT;

	/**
	 * This is true if the Scale attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected boolean scaleESet;

	/**
	 * The default value of the '{@link #getShape() <em>Shape</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getShape()
	 * @generated
	 * @ordered
	 */
	protected static final double SHAPE_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getShape() <em>Shape</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getShape()
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected double shape = SHAPE_EDEFAULT;

	/**
	 * This is true if the Shape attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	@GwtTransient
	protected boolean shapeESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected BetaDistributionTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BpsimPackage.Literals.BETA_DISTRIBUTION_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getScale() {
		return scale;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setScale(double newScale) {
		double oldScale = scale;
		scale = newScale;
		boolean oldScaleESet = scaleESet;
		scaleESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.BETA_DISTRIBUTION_TYPE__SCALE, oldScale, scale, !oldScaleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetScale() {
		double oldScale = scale;
		boolean oldScaleESet = scaleESet;
		scale = SCALE_EDEFAULT;
		scaleESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, BpsimPackage.BETA_DISTRIBUTION_TYPE__SCALE, oldScale, SCALE_EDEFAULT, oldScaleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetScale() {
		return scaleESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getShape() {
		return shape;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setShape(double newShape) {
		double oldShape = shape;
		shape = newShape;
		boolean oldShapeESet = shapeESet;
		shapeESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.BETA_DISTRIBUTION_TYPE__SHAPE, oldShape, shape, !oldShapeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetShape() {
		double oldShape = shape;
		boolean oldShapeESet = shapeESet;
		shape = SHAPE_EDEFAULT;
		shapeESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, BpsimPackage.BETA_DISTRIBUTION_TYPE__SHAPE, oldShape, SHAPE_EDEFAULT, oldShapeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetShape() {
		return shapeESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case BpsimPackage.BETA_DISTRIBUTION_TYPE__SCALE:
				return getScale();
			case BpsimPackage.BETA_DISTRIBUTION_TYPE__SHAPE:
				return getShape();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case BpsimPackage.BETA_DISTRIBUTION_TYPE__SCALE:
				setScale((Double)newValue);
				return;
			case BpsimPackage.BETA_DISTRIBUTION_TYPE__SHAPE:
				setShape((Double)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case BpsimPackage.BETA_DISTRIBUTION_TYPE__SCALE:
				unsetScale();
				return;
			case BpsimPackage.BETA_DISTRIBUTION_TYPE__SHAPE:
				unsetShape();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case BpsimPackage.BETA_DISTRIBUTION_TYPE__SCALE:
				return isSetScale();
			case BpsimPackage.BETA_DISTRIBUTION_TYPE__SHAPE:
				return isSetShape();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (scale: ");
		if (scaleESet) result.append(scale); else result.append("<unset>");
		result.append(", shape: ");
		if (shapeESet) result.append(shape); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //BetaDistributionTypeImpl
