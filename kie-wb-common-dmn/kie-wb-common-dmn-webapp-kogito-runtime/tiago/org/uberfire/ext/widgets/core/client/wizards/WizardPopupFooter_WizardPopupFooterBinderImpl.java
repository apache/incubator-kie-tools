// .ui.xml template last modified: 1607100756231
package org.uberfire.ext.widgets.core.client.wizards;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class WizardPopupFooter_WizardPopupFooterBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.widgets.core.client.wizards.WizardPopupFooter>, org.uberfire.ext.widgets.core.client.wizards.WizardPopupFooter.WizardPopupFooterBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.widgets.core.client.wizards.WizardPopupFooter owner) {


    return new Widgets(owner).get_f_ModalFooter1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.widgets.core.client.wizards.WizardPopupFooter owner;


    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.onPreviousButtonClick((com.google.gwt.event.dom.client.ClickEvent) event);
      }
    };

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.onNextButtonClick((com.google.gwt.event.dom.client.ClickEvent) event);
      }
    };

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.onCancelButtonClick((com.google.gwt.event.dom.client.ClickEvent) event);
      }
    };

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames4 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.onFinishButtonClick((com.google.gwt.event.dom.client.ClickEvent) event);
      }
    };

    public Widgets(final org.uberfire.ext.widgets.core.client.wizards.WizardPopupFooter owner) {
      this.owner = owner;
      build_i18n();  // more than one getter call detected. Type: IMPORTED, precedence: 1
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.core.client.wizards.WizardPopupFooter_WizardPopupFooterBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.widgets.core.client.wizards.WizardPopupFooter_WizardPopupFooterBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.widgets.core.client.wizards.WizardPopupFooter_WizardPopupFooterBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.widgets.core.client.wizards.WizardPopupFooter_WizardPopupFooterBinderImpl_GenBundle) GWT.create(org.uberfire.ext.widgets.core.client.wizards.WizardPopupFooter_WizardPopupFooterBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for i18n called 4 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants i18n;
    private org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants get_i18n() {
      return i18n;
    }
    private org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants build_i18n() {
      // Creation section.
      i18n = (org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants) GWT.create(org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants.class);
      // Setup section.

      return i18n;
    }

    /**
     * Getter for f_ModalFooter1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private org.gwtbootstrap3.client.ui.ModalFooter get_f_ModalFooter1() {
      return build_f_ModalFooter1();
    }
    private org.gwtbootstrap3.client.ui.ModalFooter build_f_ModalFooter1() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.ModalFooter f_ModalFooter1 = (org.gwtbootstrap3.client.ui.ModalFooter) GWT.create(org.gwtbootstrap3.client.ui.ModalFooter.class);
      // Setup section.
      f_ModalFooter1.add(get_btnPrevious());
      f_ModalFooter1.add(get_btnNext());
      f_ModalFooter1.add(get_btnCancel());
      f_ModalFooter1.add(get_btnFinish());

      return f_ModalFooter1;
    }

    /**
     * Getter for btnPrevious called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Button get_btnPrevious() {
      return build_btnPrevious();
    }
    private org.gwtbootstrap3.client.ui.Button build_btnPrevious() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Button btnPrevious = (org.gwtbootstrap3.client.ui.Button) GWT.create(org.gwtbootstrap3.client.ui.Button.class);
      // Setup section.
      btnPrevious.setIconPosition(org.gwtbootstrap3.client.ui.constants.IconPosition.LEFT);
      btnPrevious.setIcon(org.gwtbootstrap3.client.ui.constants.IconType.ANGLE_LEFT);
      btnPrevious.setText("" + get_i18n().Previous() + "");
      btnPrevious.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

      this.owner.btnPrevious = btnPrevious;

      return btnPrevious;
    }

    /**
     * Getter for btnNext called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Button get_btnNext() {
      return build_btnNext();
    }
    private org.gwtbootstrap3.client.ui.Button build_btnNext() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Button btnNext = (org.gwtbootstrap3.client.ui.Button) GWT.create(org.gwtbootstrap3.client.ui.Button.class);
      // Setup section.
      btnNext.setIconPosition(org.gwtbootstrap3.client.ui.constants.IconPosition.RIGHT);
      btnNext.setIcon(org.gwtbootstrap3.client.ui.constants.IconType.ANGLE_RIGHT);
      btnNext.setText("" + get_i18n().Next() + "");
      btnNext.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

      this.owner.btnNext = btnNext;

      return btnNext;
    }

    /**
     * Getter for btnCancel called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Button get_btnCancel() {
      return build_btnCancel();
    }
    private org.gwtbootstrap3.client.ui.Button build_btnCancel() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Button btnCancel = (org.gwtbootstrap3.client.ui.Button) GWT.create(org.gwtbootstrap3.client.ui.Button.class);
      // Setup section.
      btnCancel.setText("" + get_i18n().cancel() + "");
      btnCancel.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3);

      this.owner.btnCancel = btnCancel;

      return btnCancel;
    }

    /**
     * Getter for btnFinish called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Button get_btnFinish() {
      return build_btnFinish();
    }
    private org.gwtbootstrap3.client.ui.Button build_btnFinish() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Button btnFinish = (org.gwtbootstrap3.client.ui.Button) GWT.create(org.gwtbootstrap3.client.ui.Button.class);
      // Setup section.
      btnFinish.setIcon(org.gwtbootstrap3.client.ui.constants.IconType.CHECK);
      btnFinish.setText("" + get_i18n().Finish() + "");
      btnFinish.setType(org.gwtbootstrap3.client.ui.constants.ButtonType.PRIMARY);
      btnFinish.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames4);

      this.owner.btnFinish = btnFinish;

      return btnFinish;
    }
  }
}
