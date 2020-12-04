// .ui.xml template last modified: 1607093019059
package org.guvnor.messageconsole.client.console;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class MessageConsoleViewImpl_MessageConsoleViewImplWidgetBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.guvnor.messageconsole.client.console.MessageConsoleViewImpl>, org.guvnor.messageconsole.client.console.MessageConsoleViewImpl.MessageConsoleViewImplWidgetBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.guvnor.messageconsole.client.console.MessageConsoleViewImpl owner) {


    return new Widgets(owner).get_f_Container1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.guvnor.messageconsole.client.console.MessageConsoleViewImpl owner;


    public Widgets(final org.guvnor.messageconsole.client.console.MessageConsoleViewImpl owner) {
      this.owner = owner;
      build_style();  // generated css resource must be always created. Type: GENERATED_CSS. Precedence: 1
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 1 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.guvnor.messageconsole.client.console.MessageConsoleViewImpl_MessageConsoleViewImplWidgetBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.guvnor.messageconsole.client.console.MessageConsoleViewImpl_MessageConsoleViewImplWidgetBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.guvnor.messageconsole.client.console.MessageConsoleViewImpl_MessageConsoleViewImplWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.guvnor.messageconsole.client.console.MessageConsoleViewImpl_MessageConsoleViewImplWidgetBinderImpl_GenBundle) GWT.create(org.guvnor.messageconsole.client.console.MessageConsoleViewImpl_MessageConsoleViewImplWidgetBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for style called 1 times. Type: GENERATED_CSS. Build precedence: 1.
     */
    private org.guvnor.messageconsole.client.console.MessageConsoleViewImpl_MessageConsoleViewImplWidgetBinderImpl_GenCss_style get_style() {
      return build_style();
    }
    private org.guvnor.messageconsole.client.console.MessageConsoleViewImpl_MessageConsoleViewImplWidgetBinderImpl_GenCss_style build_style() {
      // Creation section.
      final org.guvnor.messageconsole.client.console.MessageConsoleViewImpl_MessageConsoleViewImplWidgetBinderImpl_GenCss_style style = get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay().style();
      // Setup section.
      style.ensureInjected();

      return style;
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
      final org.gwtbootstrap3.client.ui.Column f_Column3 = new org.gwtbootstrap3.client.ui.Column("MD_12");
      // Setup section.
      f_Column3.add(get_msgArea());
      f_Column3.add(get_dataGrid());

      return f_Column3;
    }

    /**
     * Getter for msgArea called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private com.google.gwt.user.client.ui.TextArea get_msgArea() {
      return build_msgArea();
    }
    private com.google.gwt.user.client.ui.TextArea build_msgArea() {
      // Creation section.
      final com.google.gwt.user.client.ui.TextArea msgArea = (com.google.gwt.user.client.ui.TextArea) GWT.create(com.google.gwt.user.client.ui.TextArea.class);
      // Setup section.
      msgArea.addStyleName("" + get_style().textAreaHidden() + "");
      msgArea.setReadOnly(true);

      this.owner.msgArea = msgArea;

      return msgArea;
    }

    /**
     * Getter for dataGrid called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.guvnor.messageconsole.client.console.widget.MessageTableWidget get_dataGrid() {
      return build_dataGrid();
    }
    private org.guvnor.messageconsole.client.console.widget.MessageTableWidget build_dataGrid() {
      // Creation section.
      final org.guvnor.messageconsole.client.console.widget.MessageTableWidget dataGrid = owner.dataGrid;
      assert dataGrid != null : "UiField dataGrid with 'provided = true' was null";
      // Setup section.

      return dataGrid;
    }
  }
}
