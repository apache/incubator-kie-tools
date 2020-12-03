// .ui.xml template last modified: 1607021623860
package org.uberfire.ext.widgets.common.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class FileUpload_FileUploadBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.widgets.common.client.common.FileUpload>, org.uberfire.ext.widgets.common.client.common.FileUpload.FileUploadBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.widgets.common.client.common.FileUpload owner) {


    return new Widgets(owner).get_f_InputGroup1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.widgets.common.client.common.FileUpload owner;


    public Widgets(final org.uberfire.ext.widgets.common.client.common.FileUpload owner) {
      this.owner = owner;
      build_i18n();  // more than one getter call detected. Type: IMPORTED, precedence: 1
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.common.client.common.FileUpload_FileUploadBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.widgets.common.client.common.FileUpload_FileUploadBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.widgets.common.client.common.FileUpload_FileUploadBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.widgets.common.client.common.FileUpload_FileUploadBinderImpl_GenBundle) GWT.create(org.uberfire.ext.widgets.common.client.common.FileUpload_FileUploadBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for i18n called 2 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants i18n;
    private org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants get_i18n() {
      return i18n;
    }
    private org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants build_i18n() {
      // Creation section.
      i18n = (org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants) GWT.create(org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants.class);
      // Setup section.

      return i18n;
    }

    /**
     * Getter for f_InputGroup1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private org.gwtbootstrap3.client.ui.InputGroup get_f_InputGroup1() {
      return build_f_InputGroup1();
    }
    private org.gwtbootstrap3.client.ui.InputGroup build_f_InputGroup1() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.InputGroup f_InputGroup1 = (org.gwtbootstrap3.client.ui.InputGroup) GWT.create(org.gwtbootstrap3.client.ui.InputGroup.class);
      // Setup section.
      f_InputGroup1.add(get_file());
      f_InputGroup1.add(get_fileText());
      f_InputGroup1.add(get_chooseButton());
      f_InputGroup1.add(get_uploadButton());

      return f_InputGroup1;
    }

    /**
     * Getter for file called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Input get_file() {
      return build_file();
    }
    private org.gwtbootstrap3.client.ui.Input build_file() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Input file = new org.gwtbootstrap3.client.ui.Input(org.gwtbootstrap3.client.ui.constants.InputType.FILE);
      // Setup section.
      file.setVisible(false);

      this.owner.file = file;

      return file;
    }

    /**
     * Getter for fileText called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Input get_fileText() {
      return build_fileText();
    }
    private org.gwtbootstrap3.client.ui.Input build_fileText() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Input fileText = new org.gwtbootstrap3.client.ui.Input(org.gwtbootstrap3.client.ui.constants.InputType.TEXT);
      // Setup section.

      this.owner.fileText = fileText;

      return fileText;
    }

    /**
     * Getter for chooseButton called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.InputGroupAddon get_chooseButton() {
      return build_chooseButton();
    }
    private org.gwtbootstrap3.client.ui.InputGroupAddon build_chooseButton() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.InputGroupAddon chooseButton = (org.gwtbootstrap3.client.ui.InputGroupAddon) GWT.create(org.gwtbootstrap3.client.ui.InputGroupAddon.class);
      // Setup section.
      chooseButton.addStyleName("btn");
      chooseButton.addStyleName("btn-default");
      chooseButton.setIconPosition(org.gwtbootstrap3.client.ui.constants.IconPosition.RIGHT);
      chooseButton.setIcon(org.gwtbootstrap3.client.ui.constants.IconType.FILE_O);
      chooseButton.setTitle("" + get_i18n().ChooseFile() + "");

      this.owner.chooseButton = chooseButton;

      return chooseButton;
    }

    /**
     * Getter for uploadButton called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.InputGroupAddon get_uploadButton() {
      return build_uploadButton();
    }
    private org.gwtbootstrap3.client.ui.InputGroupAddon build_uploadButton() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.InputGroupAddon uploadButton = (org.gwtbootstrap3.client.ui.InputGroupAddon) GWT.create(org.gwtbootstrap3.client.ui.InputGroupAddon.class);
      // Setup section.
      uploadButton.addStyleName("btn");
      uploadButton.addStyleName("btn-default");
      uploadButton.setIconPosition(org.gwtbootstrap3.client.ui.constants.IconPosition.RIGHT);
      uploadButton.setIcon(org.gwtbootstrap3.client.ui.constants.IconType.UPLOAD);
      uploadButton.setTitle("" + get_i18n().Upload() + "");

      this.owner.uploadButton = uploadButton;

      return uploadButton;
    }
  }
}
