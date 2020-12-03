// .ui.xml template last modified: 1607021623860
package org.uberfire.ext.widgets.common.client.common.popups.footers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class ModalFooterForceSaveReOpenCancelButtons_ModalFooterForceSaveReOpenCancelButtonsBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterForceSaveReOpenCancelButtons>, org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterForceSaveReOpenCancelButtons.ModalFooterForceSaveReOpenCancelButtonsBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterForceSaveReOpenCancelButtons owner) {


    return new Widgets(owner).get_f_FlowPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterForceSaveReOpenCancelButtons owner;


    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.onForceSaveButtonClick((com.google.gwt.event.dom.client.ClickEvent) event);
      }
    };

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.onReOpenButtonClick((com.google.gwt.event.dom.client.ClickEvent) event);
      }
    };

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.onCancelButtonClick((com.google.gwt.event.dom.client.ClickEvent) event);
      }
    };

    public Widgets(final org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterForceSaveReOpenCancelButtons owner) {
      this.owner = owner;
      build_i18nCommon();  // more than one getter call detected. Type: IMPORTED, precedence: 1
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterForceSaveReOpenCancelButtons_ModalFooterForceSaveReOpenCancelButtonsBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterForceSaveReOpenCancelButtons_ModalFooterForceSaveReOpenCancelButtonsBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterForceSaveReOpenCancelButtons_ModalFooterForceSaveReOpenCancelButtonsBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterForceSaveReOpenCancelButtons_ModalFooterForceSaveReOpenCancelButtonsBinderImpl_GenBundle) GWT.create(org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterForceSaveReOpenCancelButtons_ModalFooterForceSaveReOpenCancelButtonsBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for i18nCommon called 3 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants i18nCommon;
    private org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants get_i18nCommon() {
      return i18nCommon;
    }
    private org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants build_i18nCommon() {
      // Creation section.
      i18nCommon = (org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants) GWT.create(org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants.class);
      // Setup section.

      return i18nCommon;
    }

    /**
     * Getter for f_FlowPanel1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.FlowPanel get_f_FlowPanel1() {
      return build_f_FlowPanel1();
    }
    private com.google.gwt.user.client.ui.FlowPanel build_f_FlowPanel1() {
      // Creation section.
      final com.google.gwt.user.client.ui.FlowPanel f_FlowPanel1 = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
      // Setup section.
      f_FlowPanel1.add(get_forceSaveButton());
      f_FlowPanel1.add(get_reopenButton());
      f_FlowPanel1.add(get_cancelButton());

      return f_FlowPanel1;
    }

    /**
     * Getter for forceSaveButton called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Button get_forceSaveButton() {
      return build_forceSaveButton();
    }
    private org.gwtbootstrap3.client.ui.Button build_forceSaveButton() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Button forceSaveButton = (org.gwtbootstrap3.client.ui.Button) GWT.create(org.gwtbootstrap3.client.ui.Button.class);
      // Setup section.
      forceSaveButton.setText("" + get_i18nCommon().ForceSave() + "");
      forceSaveButton.setType(org.gwtbootstrap3.client.ui.constants.ButtonType.DANGER);
      forceSaveButton.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

      this.owner.forceSaveButton = forceSaveButton;

      return forceSaveButton;
    }

    /**
     * Getter for reopenButton called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Button get_reopenButton() {
      return build_reopenButton();
    }
    private org.gwtbootstrap3.client.ui.Button build_reopenButton() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Button reopenButton = (org.gwtbootstrap3.client.ui.Button) GWT.create(org.gwtbootstrap3.client.ui.Button.class);
      // Setup section.
      reopenButton.setText("" + get_i18nCommon().ReOpen() + "");
      reopenButton.setType(org.gwtbootstrap3.client.ui.constants.ButtonType.PRIMARY);
      reopenButton.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

      this.owner.reopenButton = reopenButton;

      return reopenButton;
    }

    /**
     * Getter for cancelButton called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Button get_cancelButton() {
      return build_cancelButton();
    }
    private org.gwtbootstrap3.client.ui.Button build_cancelButton() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Button cancelButton = (org.gwtbootstrap3.client.ui.Button) GWT.create(org.gwtbootstrap3.client.ui.Button.class);
      // Setup section.
      cancelButton.setText("" + get_i18nCommon().Cancel() + "");
      cancelButton.setType(org.gwtbootstrap3.client.ui.constants.ButtonType.DEFAULT);
      cancelButton.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3);

      this.owner.cancelButton = cancelButton;

      return cancelButton;
    }
  }
}
