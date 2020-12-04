// .ui.xml template last modified: 1607097782370
package org.uberfire.ext.editor.commons.client.history;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class VersionMenuDropDownButtonViewImpl_BinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButtonViewImpl>, org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButtonViewImpl.Binder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButtonViewImpl owner) {


    return new Widgets(owner).get_f_ButtonGroup1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButtonViewImpl owner;


    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.handleClick((com.google.gwt.event.dom.client.ClickEvent) event);
      }
    };

    public Widgets(final org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButtonViewImpl owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButtonViewImpl_BinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButtonViewImpl_BinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButtonViewImpl_BinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButtonViewImpl_BinderImpl_GenBundle) GWT.create(org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButtonViewImpl_BinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for i18n called 1 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants get_i18n() {
      return build_i18n();
    }
    private org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants build_i18n() {
      // Creation section.
      final org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants i18n = (org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants) GWT.create(org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants.class);
      // Setup section.

      return i18n;
    }

    /**
     * Getter for f_ButtonGroup1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private org.gwtbootstrap3.client.ui.ButtonGroup get_f_ButtonGroup1() {
      return build_f_ButtonGroup1();
    }
    private org.gwtbootstrap3.client.ui.ButtonGroup build_f_ButtonGroup1() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.ButtonGroup f_ButtonGroup1 = (org.gwtbootstrap3.client.ui.ButtonGroup) GWT.create(org.gwtbootstrap3.client.ui.ButtonGroup.class);
      // Setup section.
      f_ButtonGroup1.add(get_button());
      f_ButtonGroup1.add(get_menuItems());

      return f_ButtonGroup1;
    }

    /**
     * Getter for button called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Button get_button() {
      return build_button();
    }
    private org.gwtbootstrap3.client.ui.Button build_button() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Button button = (org.gwtbootstrap3.client.ui.Button) GWT.create(org.gwtbootstrap3.client.ui.Button.class);
      // Setup section.
      button.setSize(org.gwtbootstrap3.client.ui.constants.ButtonSize.SMALL);
      button.setDataToggle(org.gwtbootstrap3.client.ui.constants.Toggle.DROPDOWN);
      button.setText("" + get_i18n().LatestVersion() + "");
      button.setToggleCaret(true);
      button.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

      this.owner.button = button;

      return button;
    }

    /**
     * Getter for menuItems called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.DropDownMenu get_menuItems() {
      return build_menuItems();
    }
    private org.gwtbootstrap3.client.ui.DropDownMenu build_menuItems() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.DropDownMenu menuItems = (org.gwtbootstrap3.client.ui.DropDownMenu) GWT.create(org.gwtbootstrap3.client.ui.DropDownMenu.class);
      // Setup section.
      menuItems.addStyleName("dropdown-menu-right");

      this.owner.menuItems = menuItems;

      return menuItems;
    }
  }
}
