// .ui.xml template last modified: 1607100756231
package org.uberfire.ext.widgets.core.client.wizards;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class WizardPageTitle_WizardPageTitleViewBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.widgets.core.client.wizards.WizardPageTitle>, org.uberfire.ext.widgets.core.client.wizards.WizardPageTitle.WizardPageTitleViewBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.widgets.core.client.wizards.WizardPageTitle owner) {


    return new Widgets(owner).get_container();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.widgets.core.client.wizards.WizardPageTitle owner;


    public Widgets(final org.uberfire.ext.widgets.core.client.wizards.WizardPageTitle owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.core.client.wizards.WizardPageTitle_WizardPageTitleViewBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.widgets.core.client.wizards.WizardPageTitle_WizardPageTitleViewBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.widgets.core.client.wizards.WizardPageTitle_WizardPageTitleViewBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.widgets.core.client.wizards.WizardPageTitle_WizardPageTitleViewBinderImpl_GenBundle) GWT.create(org.uberfire.ext.widgets.core.client.wizards.WizardPageTitle_WizardPageTitleViewBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for images called 0 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.core.client.resources.CoreImages get_images() {
      return build_images();
    }
    private org.uberfire.ext.widgets.core.client.resources.CoreImages build_images() {
      // Creation section.
      final org.uberfire.ext.widgets.core.client.resources.CoreImages images = (org.uberfire.ext.widgets.core.client.resources.CoreImages) GWT.create(org.uberfire.ext.widgets.core.client.resources.CoreImages.class);
      // Setup section.

      return images;
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
     * Getter for container called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private org.gwtbootstrap3.client.ui.AnchorListItem get_container() {
      return build_container();
    }
    private org.gwtbootstrap3.client.ui.AnchorListItem build_container() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.AnchorListItem container = (org.gwtbootstrap3.client.ui.AnchorListItem) GWT.create(org.gwtbootstrap3.client.ui.AnchorListItem.class);
      // Setup section.
      container.setIcon(org.gwtbootstrap3.client.ui.constants.IconType.SQUARE_O);
      container.setIconFixedWidth(true);

      this.owner.container = container;

      return container;
    }
  }
}
