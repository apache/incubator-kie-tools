// .ui.xml template last modified: 1607096095640
package org.uberfire.ext.widgets.core.client.wizards;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class WizardViewImpl_WizardActivityViewImplBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.widgets.core.client.wizards.WizardViewImpl>, org.uberfire.ext.widgets.core.client.wizards.WizardViewImpl.WizardActivityViewImplBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.widgets.core.client.wizards.WizardViewImpl owner) {


    return new Widgets(owner).get_f_Container1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.widgets.core.client.wizards.WizardViewImpl owner;


    public Widgets(final org.uberfire.ext.widgets.core.client.wizards.WizardViewImpl owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.core.client.wizards.WizardViewImpl_WizardActivityViewImplBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.widgets.core.client.wizards.WizardViewImpl_WizardActivityViewImplBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.widgets.core.client.wizards.WizardViewImpl_WizardActivityViewImplBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.widgets.core.client.wizards.WizardViewImpl_WizardActivityViewImplBinderImpl_GenBundle) GWT.create(org.uberfire.ext.widgets.core.client.wizards.WizardViewImpl_WizardActivityViewImplBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for i18n called 0 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants get_i18n() {
      return build_i18n();
    }
    private org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants build_i18n() {
      // Creation section.
      final org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants i18n = (org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants) GWT.create(org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants.class);
      // Setup section.

      return i18n;
    }

    /**
     * Getter for res called 0 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.core.client.resources.WizardResources get_res() {
      return build_res();
    }
    private org.uberfire.ext.widgets.core.client.resources.WizardResources build_res() {
      // Creation section.
      final org.uberfire.ext.widgets.core.client.resources.WizardResources res = (org.uberfire.ext.widgets.core.client.resources.WizardResources) GWT.create(org.uberfire.ext.widgets.core.client.resources.WizardResources.class);
      // Setup section.

      return res;
    }

    /**
     * Getter for f_Container1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private org.gwtbootstrap3.client.ui.Container get_f_Container1() {
      return build_f_Container1();
    }
    private org.gwtbootstrap3.client.ui.Container build_f_Container1() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Container f_Container1 = (org.gwtbootstrap3.client.ui.Container) GWT.create(org.gwtbootstrap3.client.ui.Container.class);
      // Setup section.
      f_Container1.add(get_f_Row2());
      f_Container1.setFluid(true);

      return f_Container1;
    }

    /**
     * Getter for f_Row2 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Row get_f_Row2() {
      return build_f_Row2();
    }
    private org.gwtbootstrap3.client.ui.Row build_f_Row2() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Row f_Row2 = (org.gwtbootstrap3.client.ui.Row) GWT.create(org.gwtbootstrap3.client.ui.Row.class);
      // Setup section.
      f_Row2.add(get_f_Column3());
      f_Row2.add(get_body());

      return f_Row2;
    }

    /**
     * Getter for f_Column3 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.Column get_f_Column3() {
      return build_f_Column3();
    }
    private org.gwtbootstrap3.client.ui.Column build_f_Column3() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column f_Column3 = new org.gwtbootstrap3.client.ui.Column("MD_3");
      // Setup section.
      f_Column3.add(get_sideBar());

      return f_Column3;
    }

    /**
     * Getter for sideBar called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.NavPills get_sideBar() {
      return build_sideBar();
    }
    private org.gwtbootstrap3.client.ui.NavPills build_sideBar() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.NavPills sideBar = (org.gwtbootstrap3.client.ui.NavPills) GWT.create(org.gwtbootstrap3.client.ui.NavPills.class);
      // Setup section.
      sideBar.setStacked(true);

      this.owner.sideBar = sideBar;

      return sideBar;
    }

    /**
     * Getter for body called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.Column get_body() {
      return build_body();
    }
    private org.gwtbootstrap3.client.ui.Column build_body() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column body = new org.gwtbootstrap3.client.ui.Column("MD_9");
      // Setup section.

      this.owner.body = body;

      return body;
    }
  }
}
