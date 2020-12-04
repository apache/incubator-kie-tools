// .ui.xml template last modified: 1607097769903
package org.uberfire.client.views.pfly.dnd;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.PopupPanel;

public class CompassWidgetImpl_CompassWidgetBinderImpl implements UiBinder<com.google.gwt.user.client.ui.PopupPanel, org.uberfire.client.views.pfly.dnd.CompassWidgetImpl>, org.uberfire.client.views.pfly.dnd.CompassWidgetImpl.CompassWidgetBinder {


  public com.google.gwt.user.client.ui.PopupPanel createAndBindUi(final org.uberfire.client.views.pfly.dnd.CompassWidgetImpl owner) {


    return new Widgets(owner).get_popup();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.client.views.pfly.dnd.CompassWidgetImpl owner;


    public Widgets(final org.uberfire.client.views.pfly.dnd.CompassWidgetImpl owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.client.views.pfly.dnd.CompassWidgetImpl_CompassWidgetBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.client.views.pfly.dnd.CompassWidgetImpl_CompassWidgetBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.client.views.pfly.dnd.CompassWidgetImpl_CompassWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.client.views.pfly.dnd.CompassWidgetImpl_CompassWidgetBinderImpl_GenBundle) GWT.create(org.uberfire.client.views.pfly.dnd.CompassWidgetImpl_CompassWidgetBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for popup called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.PopupPanel get_popup() {
      return build_popup();
    }
    private com.google.gwt.user.client.ui.PopupPanel build_popup() {
      // Creation section.
      final com.google.gwt.user.client.ui.PopupPanel popup = (com.google.gwt.user.client.ui.PopupPanel) GWT.create(com.google.gwt.user.client.ui.PopupPanel.class);
      // Setup section.
      popup.add(get_container());
      popup.addStyleName("uf-drop-target-compass");
      popup.setWidth("100px");
      popup.setHeight("100px");

      this.owner.popup = popup;

      return popup;
    }

    /**
     * Getter for container called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.DockLayoutPanel get_container() {
      return build_container();
    }
    private com.google.gwt.user.client.ui.DockLayoutPanel build_container() {
      // Creation section.
      final com.google.gwt.user.client.ui.DockLayoutPanel container = new com.google.gwt.user.client.ui.DockLayoutPanel(com.google.gwt.dom.client.Style.Unit.PCT);
      // Setup section.
      container.addSouth(get_south(), 25);
      container.addNorth(get_north(), 25);
      container.addWest(get_west(), 30);
      container.addEast(get_east(), 30);
      container.add(get_centre());

      this.owner.container = container;

      return container;
    }

    /**
     * Getter for south called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.html.Div get_south() {
      return build_south();
    }
    private org.gwtbootstrap3.client.ui.html.Div build_south() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.html.Div south = (org.gwtbootstrap3.client.ui.html.Div) GWT.create(org.gwtbootstrap3.client.ui.html.Div.class);
      // Setup section.
      south.add(get_f_Icon1());
      south.addStyleName("uf-drop-target-compass-south");

      this.owner.south = south;

      return south;
    }

    /**
     * Getter for f_Icon1 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Icon get_f_Icon1() {
      return build_f_Icon1();
    }
    private org.gwtbootstrap3.client.ui.Icon build_f_Icon1() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Icon f_Icon1 = new org.gwtbootstrap3.client.ui.Icon(org.gwtbootstrap3.client.ui.constants.IconType.CHEVRON_DOWN);
      // Setup section.

      return f_Icon1;
    }

    /**
     * Getter for north called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.html.Div get_north() {
      return build_north();
    }
    private org.gwtbootstrap3.client.ui.html.Div build_north() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.html.Div north = (org.gwtbootstrap3.client.ui.html.Div) GWT.create(org.gwtbootstrap3.client.ui.html.Div.class);
      // Setup section.
      north.add(get_f_Icon2());
      north.addStyleName("uf-drop-target-compass-north");

      this.owner.north = north;

      return north;
    }

    /**
     * Getter for f_Icon2 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Icon get_f_Icon2() {
      return build_f_Icon2();
    }
    private org.gwtbootstrap3.client.ui.Icon build_f_Icon2() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Icon f_Icon2 = new org.gwtbootstrap3.client.ui.Icon(org.gwtbootstrap3.client.ui.constants.IconType.CHEVRON_UP);
      // Setup section.

      return f_Icon2;
    }

    /**
     * Getter for west called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.html.Div get_west() {
      return build_west();
    }
    private org.gwtbootstrap3.client.ui.html.Div build_west() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.html.Div west = (org.gwtbootstrap3.client.ui.html.Div) GWT.create(org.gwtbootstrap3.client.ui.html.Div.class);
      // Setup section.
      west.add(get_f_Icon3());
      west.addStyleName("uf-drop-target-compass-west");

      this.owner.west = west;

      return west;
    }

    /**
     * Getter for f_Icon3 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Icon get_f_Icon3() {
      return build_f_Icon3();
    }
    private org.gwtbootstrap3.client.ui.Icon build_f_Icon3() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Icon f_Icon3 = new org.gwtbootstrap3.client.ui.Icon(org.gwtbootstrap3.client.ui.constants.IconType.CHEVRON_LEFT);
      // Setup section.

      return f_Icon3;
    }

    /**
     * Getter for east called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.html.Div get_east() {
      return build_east();
    }
    private org.gwtbootstrap3.client.ui.html.Div build_east() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.html.Div east = (org.gwtbootstrap3.client.ui.html.Div) GWT.create(org.gwtbootstrap3.client.ui.html.Div.class);
      // Setup section.
      east.add(get_f_Icon4());
      east.addStyleName("uf-drop-target-compass-east");

      this.owner.east = east;

      return east;
    }

    /**
     * Getter for f_Icon4 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Icon get_f_Icon4() {
      return build_f_Icon4();
    }
    private org.gwtbootstrap3.client.ui.Icon build_f_Icon4() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Icon f_Icon4 = new org.gwtbootstrap3.client.ui.Icon(org.gwtbootstrap3.client.ui.constants.IconType.CHEVRON_RIGHT);
      // Setup section.

      return f_Icon4;
    }

    /**
     * Getter for centre called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.html.Div get_centre() {
      return build_centre();
    }
    private org.gwtbootstrap3.client.ui.html.Div build_centre() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.html.Div centre = (org.gwtbootstrap3.client.ui.html.Div) GWT.create(org.gwtbootstrap3.client.ui.html.Div.class);
      // Setup section.
      centre.add(get_f_Icon5());
      centre.addStyleName("uf-drop-target-compass-centre");

      this.owner.centre = centre;

      return centre;
    }

    /**
     * Getter for f_Icon5 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Icon get_f_Icon5() {
      return build_f_Icon5();
    }
    private org.gwtbootstrap3.client.ui.Icon build_f_Icon5() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Icon f_Icon5 = new org.gwtbootstrap3.client.ui.Icon(org.gwtbootstrap3.client.ui.constants.IconType.SQUARE);
      // Setup section.
      f_Icon5.setSize(org.gwtbootstrap3.client.ui.constants.IconSize.LARGE);

      return f_Icon5;
    }
  }
}
