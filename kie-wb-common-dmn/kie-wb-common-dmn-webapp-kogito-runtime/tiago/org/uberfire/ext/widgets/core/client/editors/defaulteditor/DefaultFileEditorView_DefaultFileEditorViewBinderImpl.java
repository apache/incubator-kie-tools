// .ui.xml template last modified: 1607096095640
package org.uberfire.ext.widgets.core.client.editors.defaulteditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import org.gwtbootstrap3.client.ui.Container;

public class DefaultFileEditorView_DefaultFileEditorViewBinderImpl implements UiBinder<org.gwtbootstrap3.client.ui.Container, org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView>, org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView.DefaultFileEditorViewBinder {


  public org.gwtbootstrap3.client.ui.Container createAndBindUi(final org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView owner) {


    return new Widgets(owner).get_f_Container1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView owner;


    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.handleClick((com.google.gwt.event.dom.client.ClickEvent) event);
      }
    };

    public Widgets(final org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView owner) {
      this.owner = owner;
      build_style();  // generated css resource must be always created. Type: GENERATED_CSS. Precedence: 1
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 1 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView_DefaultFileEditorViewBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView_DefaultFileEditorViewBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView_DefaultFileEditorViewBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView_DefaultFileEditorViewBinderImpl_GenBundle) GWT.create(org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView_DefaultFileEditorViewBinderImpl_GenBundle.class);
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
     * Getter for style called 2 times. Type: GENERATED_CSS. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView_DefaultFileEditorViewBinderImpl_GenCss_style style;
    private org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView_DefaultFileEditorViewBinderImpl_GenCss_style get_style() {
      return style;
    }
    private org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView_DefaultFileEditorViewBinderImpl_GenCss_style build_style() {
      // Creation section.
      style = get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay().style();
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
      f_Container1.add(get_f_Row4());
      f_Container1.addStyleName("" + get_style().topMargin() + "");
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
      f_Column3.add(get_fileUpload());

      return f_Column3;
    }

    /**
     * Getter for fileUpload called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorFileUpload get_fileUpload() {
      return build_fileUpload();
    }
    private org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorFileUpload build_fileUpload() {
      // Creation section.
      final org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorFileUpload fileUpload = (org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorFileUpload) GWT.create(org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorFileUpload.class);
      // Setup section.
      fileUpload.addStyleName("" + get_style().editor() + "");

      this.owner.fileUpload = fileUpload;

      return fileUpload;
    }

    /**
     * Getter for f_Row4 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Row get_f_Row4() {
      return build_f_Row4();
    }
    private org.gwtbootstrap3.client.ui.Row build_f_Row4() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Row f_Row4 = (org.gwtbootstrap3.client.ui.Row) GWT.create(org.gwtbootstrap3.client.ui.Row.class);
      // Setup section.
      f_Row4.add(get_f_Column5());

      return f_Row4;
    }

    /**
     * Getter for f_Column5 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.Column get_f_Column5() {
      return build_f_Column5();
    }
    private org.gwtbootstrap3.client.ui.Column build_f_Column5() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column f_Column5 = new org.gwtbootstrap3.client.ui.Column("MD_12");
      // Setup section.
      f_Column5.add(get_downloadButton());

      return f_Column5;
    }

    /**
     * Getter for downloadButton called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Button get_downloadButton() {
      return build_downloadButton();
    }
    private org.gwtbootstrap3.client.ui.Button build_downloadButton() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Button downloadButton = (org.gwtbootstrap3.client.ui.Button) GWT.create(org.gwtbootstrap3.client.ui.Button.class);
      // Setup section.
      downloadButton.setText("" + get_i18n().Download() + "");
      downloadButton.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

      this.owner.downloadButton = downloadButton;

      return downloadButton;
    }
  }
}
