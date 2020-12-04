// .ui.xml template last modified: 1607092936728
package org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class ActivityNotFoundView_ActivityNotFoundViewBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundView>, org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundView.ActivityNotFoundViewBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundView owner) {


    return new Widgets(owner).get_f_HorizontalPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundView owner;


    public Widgets(final org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundView owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundView_ActivityNotFoundViewBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundView_ActivityNotFoundViewBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundView_ActivityNotFoundViewBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundView_ActivityNotFoundViewBinderImpl_GenBundle) GWT.create(org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundView_ActivityNotFoundViewBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for i18n called 1 times. Type: IMPORTED. Build precedence: 1.
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
     * Getter for images called 1 times. Type: IMPORTED. Build precedence: 1.
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
     * Getter for f_HorizontalPanel1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.HorizontalPanel get_f_HorizontalPanel1() {
      return build_f_HorizontalPanel1();
    }
    private com.google.gwt.user.client.ui.HorizontalPanel build_f_HorizontalPanel1() {
      // Creation section.
      final com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel1 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
      // Setup section.
      f_HorizontalPanel1.add(get_f_Image2());
      f_HorizontalPanel1.add(get_f_Label3());

      return f_HorizontalPanel1;
    }

    /**
     * Getter for f_Image2 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.Image get_f_Image2() {
      return build_f_Image2();
    }
    private com.google.gwt.user.client.ui.Image build_f_Image2() {
      // Creation section.
      final com.google.gwt.user.client.ui.Image f_Image2 = new com.google.gwt.user.client.ui.Image(get_images().warningLarge());
      // Setup section.

      return f_Image2;
    }

    /**
     * Getter for f_Label3 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.Label get_f_Label3() {
      return build_f_Label3();
    }
    private com.google.gwt.user.client.ui.Label build_f_Label3() {
      // Creation section.
      final com.google.gwt.user.client.ui.Label f_Label3 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
      // Setup section.
      f_Label3.setText("" + get_i18n().activityNotFound() + "");

      return f_Label3;
    }
  }
}
