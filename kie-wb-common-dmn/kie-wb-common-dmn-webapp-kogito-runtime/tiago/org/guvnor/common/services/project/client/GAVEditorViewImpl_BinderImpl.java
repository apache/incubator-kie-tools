// .ui.xml template last modified: 1607093019771
package org.guvnor.common.services.project.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class GAVEditorViewImpl_BinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.guvnor.common.services.project.client.GAVEditorViewImpl>, org.guvnor.common.services.project.client.GAVEditorViewImpl.Binder {

  interface Template extends SafeHtmlTemplates {
    @Template("<span id='{0}'></span>")
    SafeHtml html1(String arg0);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.guvnor.common.services.project.client.GAVEditorViewImpl owner) {


    return new Widgets(owner).get_f_HTMLPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.guvnor.common.services.project.client.GAVEditorViewImpl owner;


    final com.google.gwt.event.dom.client.KeyUpHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.KeyUpHandler() {
      public void onKeyUp(com.google.gwt.event.dom.client.KeyUpEvent event) {
        owner.onGroupIdChange((com.google.gwt.event.dom.client.KeyUpEvent) event);
      }
    };

    final com.google.gwt.event.dom.client.KeyUpHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.KeyUpHandler() {
      public void onKeyUp(com.google.gwt.event.dom.client.KeyUpEvent event) {
        owner.onArtifactIdChange((com.google.gwt.event.dom.client.KeyUpEvent) event);
      }
    };

    final com.google.gwt.event.dom.client.KeyUpHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3 = new com.google.gwt.event.dom.client.KeyUpHandler() {
      public void onKeyUp(com.google.gwt.event.dom.client.KeyUpEvent event) {
        owner.onVersionChange((com.google.gwt.event.dom.client.KeyUpEvent) event);
      }
    };

    public Widgets(final org.guvnor.common.services.project.client.GAVEditorViewImpl owner) {
      this.owner = owner;
      build_i18n();  // more than one getter call detected. Type: IMPORTED, precedence: 1
      build_domId0();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId0Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
    }

    SafeHtml template_html1() {
      return template.html1(get_domId0());
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.guvnor.common.services.project.client.GAVEditorViewImpl_BinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.guvnor.common.services.project.client.GAVEditorViewImpl_BinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.guvnor.common.services.project.client.GAVEditorViewImpl_BinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.guvnor.common.services.project.client.GAVEditorViewImpl_BinderImpl_GenBundle) GWT.create(org.guvnor.common.services.project.client.GAVEditorViewImpl_BinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for i18n called 15 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.guvnor.common.services.project.client.resources.i18n.ProjectConstants i18n;
    private org.guvnor.common.services.project.client.resources.i18n.ProjectConstants get_i18n() {
      return i18n;
    }
    private org.guvnor.common.services.project.client.resources.i18n.ProjectConstants build_i18n() {
      // Creation section.
      i18n = (org.guvnor.common.services.project.client.resources.i18n.ProjectConstants) GWT.create(org.guvnor.common.services.project.client.resources.i18n.ProjectConstants.class);
      // Setup section.

      return i18n;
    }

    /**
     * Getter for resources called 0 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.guvnor.common.services.project.client.resources.ProjectResources get_resources() {
      return build_resources();
    }
    private org.guvnor.common.services.project.client.resources.ProjectResources build_resources() {
      // Creation section.
      final org.guvnor.common.services.project.client.resources.ProjectResources resources = (org.guvnor.common.services.project.client.resources.ProjectResources) GWT.create(org.guvnor.common.services.project.client.resources.ProjectResources.class);
      // Setup section.

      return resources;
    }

    /**
     * Getter for f_HTMLPanel1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_f_HTMLPanel1() {
      return build_f_HTMLPanel1();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_f_HTMLPanel1() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template_html1().asString());
      // Setup section.

      {
        // Attach section.
        UiBinderUtil.TempAttachment __attachRecord__ = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());

        get_domId0Element().get();

        // Detach section.
        __attachRecord__.detach();
      }
      f_HTMLPanel1.addAndReplaceElement(get_f_Form2(), get_domId0Element().get());

      return f_HTMLPanel1;
    }

    /**
     * Getter for domId0 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
     */
    private java.lang.String domId0;
    private java.lang.String get_domId0() {
      return domId0;
    }
    private java.lang.String build_domId0() {
      // Creation section.
      domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.

      return domId0;
    }

    /**
     * Getter for f_Form2 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Form get_f_Form2() {
      return build_f_Form2();
    }
    private org.gwtbootstrap3.client.ui.Form build_f_Form2() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Form f_Form2 = (org.gwtbootstrap3.client.ui.Form) GWT.create(org.gwtbootstrap3.client.ui.Form.class);
      // Setup section.
      f_Form2.add(get_groupIdGroup());
      f_Form2.add(get_artifactIdGroup());
      f_Form2.add(get_versionGroup());
      f_Form2.setType(org.gwtbootstrap3.client.ui.constants.FormType.HORIZONTAL);

      return f_Form2;
    }

    /**
     * Getter for groupIdGroup called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.FormGroup get_groupIdGroup() {
      return build_groupIdGroup();
    }
    private org.gwtbootstrap3.client.ui.FormGroup build_groupIdGroup() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.FormGroup groupIdGroup = (org.gwtbootstrap3.client.ui.FormGroup) GWT.create(org.gwtbootstrap3.client.ui.FormGroup.class);
      // Setup section.
      groupIdGroup.add(get_f_FormLabelHelp3());
      groupIdGroup.add(get_f_Column4());

      this.owner.groupIdGroup = groupIdGroup;

      return groupIdGroup;
    }

    /**
     * Getter for f_FormLabelHelp3 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.uberfire.client.views.pfly.widgets.FormLabelHelp get_f_FormLabelHelp3() {
      return build_f_FormLabelHelp3();
    }
    private org.uberfire.client.views.pfly.widgets.FormLabelHelp build_f_FormLabelHelp3() {
      // Creation section.
      final org.uberfire.client.views.pfly.widgets.FormLabelHelp f_FormLabelHelp3 = (org.uberfire.client.views.pfly.widgets.FormLabelHelp) GWT.create(org.uberfire.client.views.pfly.widgets.FormLabelHelp.class);
      // Setup section.
      f_FormLabelHelp3.addStyleName("col-md-4");
      f_FormLabelHelp3.setHelpTitle("" + get_i18n().MoreInfo() + "");
      f_FormLabelHelp3.setFor("groupIdTextBox");
      f_FormLabelHelp3.setText("" + get_i18n().GroupID() + "");
      f_FormLabelHelp3.setHelpContent("" + get_i18n().GroupIdMoreInfo() + "");

      return f_FormLabelHelp3;
    }

    /**
     * Getter for f_Column4 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Column get_f_Column4() {
      return build_f_Column4();
    }
    private org.gwtbootstrap3.client.ui.Column build_f_Column4() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column f_Column4 = new org.gwtbootstrap3.client.ui.Column("MD_8");
      // Setup section.
      f_Column4.add(get_groupIdTextBox());
      f_Column4.add(get_f_HelpBlock5());
      f_Column4.add(get_groupIdHelpBlock());

      return f_Column4;
    }

    /**
     * Getter for groupIdTextBox called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.TextBox get_groupIdTextBox() {
      return build_groupIdTextBox();
    }
    private org.gwtbootstrap3.client.ui.TextBox build_groupIdTextBox() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.TextBox groupIdTextBox = (org.gwtbootstrap3.client.ui.TextBox) GWT.create(org.gwtbootstrap3.client.ui.TextBox.class);
      // Setup section.
      groupIdTextBox.setPlaceholder("" + get_i18n().EnterAGroupID() + "");
      groupIdTextBox.setId("groupIdTextBox");
      groupIdTextBox.addKeyUpHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

      this.owner.groupIdTextBox = groupIdTextBox;

      return groupIdTextBox;
    }

    /**
     * Getter for f_HelpBlock5 called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.HelpBlock get_f_HelpBlock5() {
      return build_f_HelpBlock5();
    }
    private org.gwtbootstrap3.client.ui.HelpBlock build_f_HelpBlock5() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.HelpBlock f_HelpBlock5 = (org.gwtbootstrap3.client.ui.HelpBlock) GWT.create(org.gwtbootstrap3.client.ui.HelpBlock.class);
      // Setup section.
      f_HelpBlock5.setText("" + get_i18n().GroupIdExample() + "");

      return f_HelpBlock5;
    }

    /**
     * Getter for groupIdHelpBlock called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.HelpBlock get_groupIdHelpBlock() {
      return build_groupIdHelpBlock();
    }
    private org.gwtbootstrap3.client.ui.HelpBlock build_groupIdHelpBlock() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.HelpBlock groupIdHelpBlock = (org.gwtbootstrap3.client.ui.HelpBlock) GWT.create(org.gwtbootstrap3.client.ui.HelpBlock.class);
      // Setup section.

      this.owner.groupIdHelpBlock = groupIdHelpBlock;

      return groupIdHelpBlock;
    }

    /**
     * Getter for artifactIdGroup called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.FormGroup get_artifactIdGroup() {
      return build_artifactIdGroup();
    }
    private org.gwtbootstrap3.client.ui.FormGroup build_artifactIdGroup() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.FormGroup artifactIdGroup = (org.gwtbootstrap3.client.ui.FormGroup) GWT.create(org.gwtbootstrap3.client.ui.FormGroup.class);
      // Setup section.
      artifactIdGroup.add(get_f_FormLabelHelp6());
      artifactIdGroup.add(get_f_Column7());

      this.owner.artifactIdGroup = artifactIdGroup;

      return artifactIdGroup;
    }

    /**
     * Getter for f_FormLabelHelp6 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.uberfire.client.views.pfly.widgets.FormLabelHelp get_f_FormLabelHelp6() {
      return build_f_FormLabelHelp6();
    }
    private org.uberfire.client.views.pfly.widgets.FormLabelHelp build_f_FormLabelHelp6() {
      // Creation section.
      final org.uberfire.client.views.pfly.widgets.FormLabelHelp f_FormLabelHelp6 = (org.uberfire.client.views.pfly.widgets.FormLabelHelp) GWT.create(org.uberfire.client.views.pfly.widgets.FormLabelHelp.class);
      // Setup section.
      f_FormLabelHelp6.addStyleName("col-md-4");
      f_FormLabelHelp6.setHelpTitle("" + get_i18n().MoreInfo() + "");
      f_FormLabelHelp6.setFor("artifactIdTextBox");
      f_FormLabelHelp6.setText("" + get_i18n().ArtifactID() + "");
      f_FormLabelHelp6.setHelpContent("" + get_i18n().ArtifactIDMoreInfo() + "");

      return f_FormLabelHelp6;
    }

    /**
     * Getter for f_Column7 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Column get_f_Column7() {
      return build_f_Column7();
    }
    private org.gwtbootstrap3.client.ui.Column build_f_Column7() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column f_Column7 = new org.gwtbootstrap3.client.ui.Column("MD_8");
      // Setup section.
      f_Column7.add(get_artifactIdTextBox());
      f_Column7.add(get_f_HelpBlock8());
      f_Column7.add(get_artifactIdHelpBlock());

      return f_Column7;
    }

    /**
     * Getter for artifactIdTextBox called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.TextBox get_artifactIdTextBox() {
      return build_artifactIdTextBox();
    }
    private org.gwtbootstrap3.client.ui.TextBox build_artifactIdTextBox() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.TextBox artifactIdTextBox = (org.gwtbootstrap3.client.ui.TextBox) GWT.create(org.gwtbootstrap3.client.ui.TextBox.class);
      // Setup section.
      artifactIdTextBox.setPlaceholder("" + get_i18n().EnterAnArtifactID() + "");
      artifactIdTextBox.setId("artifactIdTextBox");
      artifactIdTextBox.addKeyUpHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

      this.owner.artifactIdTextBox = artifactIdTextBox;

      return artifactIdTextBox;
    }

    /**
     * Getter for f_HelpBlock8 called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.HelpBlock get_f_HelpBlock8() {
      return build_f_HelpBlock8();
    }
    private org.gwtbootstrap3.client.ui.HelpBlock build_f_HelpBlock8() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.HelpBlock f_HelpBlock8 = (org.gwtbootstrap3.client.ui.HelpBlock) GWT.create(org.gwtbootstrap3.client.ui.HelpBlock.class);
      // Setup section.
      f_HelpBlock8.setText("" + get_i18n().ArtifactIDExample() + "");

      return f_HelpBlock8;
    }

    /**
     * Getter for artifactIdHelpBlock called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.HelpBlock get_artifactIdHelpBlock() {
      return build_artifactIdHelpBlock();
    }
    private org.gwtbootstrap3.client.ui.HelpBlock build_artifactIdHelpBlock() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.HelpBlock artifactIdHelpBlock = (org.gwtbootstrap3.client.ui.HelpBlock) GWT.create(org.gwtbootstrap3.client.ui.HelpBlock.class);
      // Setup section.

      this.owner.artifactIdHelpBlock = artifactIdHelpBlock;

      return artifactIdHelpBlock;
    }

    /**
     * Getter for versionGroup called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.FormGroup get_versionGroup() {
      return build_versionGroup();
    }
    private org.gwtbootstrap3.client.ui.FormGroup build_versionGroup() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.FormGroup versionGroup = (org.gwtbootstrap3.client.ui.FormGroup) GWT.create(org.gwtbootstrap3.client.ui.FormGroup.class);
      // Setup section.
      versionGroup.add(get_f_FormLabelHelp9());
      versionGroup.add(get_f_Column10());

      this.owner.versionGroup = versionGroup;

      return versionGroup;
    }

    /**
     * Getter for f_FormLabelHelp9 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.uberfire.client.views.pfly.widgets.FormLabelHelp get_f_FormLabelHelp9() {
      return build_f_FormLabelHelp9();
    }
    private org.uberfire.client.views.pfly.widgets.FormLabelHelp build_f_FormLabelHelp9() {
      // Creation section.
      final org.uberfire.client.views.pfly.widgets.FormLabelHelp f_FormLabelHelp9 = (org.uberfire.client.views.pfly.widgets.FormLabelHelp) GWT.create(org.uberfire.client.views.pfly.widgets.FormLabelHelp.class);
      // Setup section.
      f_FormLabelHelp9.addStyleName("col-md-4");
      f_FormLabelHelp9.setHelpTitle("" + get_i18n().MoreInfo() + "");
      f_FormLabelHelp9.setFor("versionTextBox");
      f_FormLabelHelp9.setText("" + get_i18n().Version() + "");
      f_FormLabelHelp9.setHelpContent("" + get_i18n().VersionMoreInfo() + "");

      return f_FormLabelHelp9;
    }

    /**
     * Getter for f_Column10 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Column get_f_Column10() {
      return build_f_Column10();
    }
    private org.gwtbootstrap3.client.ui.Column build_f_Column10() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column f_Column10 = new org.gwtbootstrap3.client.ui.Column("MD_8");
      // Setup section.
      f_Column10.add(get_versionTextBox());
      f_Column10.add(get_f_HelpBlock11());
      f_Column10.add(get_versionHelpBlock());

      return f_Column10;
    }

    /**
     * Getter for versionTextBox called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.TextBox get_versionTextBox() {
      return build_versionTextBox();
    }
    private org.gwtbootstrap3.client.ui.TextBox build_versionTextBox() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.TextBox versionTextBox = (org.gwtbootstrap3.client.ui.TextBox) GWT.create(org.gwtbootstrap3.client.ui.TextBox.class);
      // Setup section.
      versionTextBox.setPlaceholder("" + get_i18n().EnterAVersion() + "");
      versionTextBox.setId("versionTextBox");
      versionTextBox.addKeyUpHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3);

      this.owner.versionTextBox = versionTextBox;

      return versionTextBox;
    }

    /**
     * Getter for f_HelpBlock11 called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.HelpBlock get_f_HelpBlock11() {
      return build_f_HelpBlock11();
    }
    private org.gwtbootstrap3.client.ui.HelpBlock build_f_HelpBlock11() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.HelpBlock f_HelpBlock11 = (org.gwtbootstrap3.client.ui.HelpBlock) GWT.create(org.gwtbootstrap3.client.ui.HelpBlock.class);
      // Setup section.
      f_HelpBlock11.setText("" + get_i18n().VersionExample() + "");

      return f_HelpBlock11;
    }

    /**
     * Getter for versionHelpBlock called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.HelpBlock get_versionHelpBlock() {
      return build_versionHelpBlock();
    }
    private org.gwtbootstrap3.client.ui.HelpBlock build_versionHelpBlock() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.HelpBlock versionHelpBlock = (org.gwtbootstrap3.client.ui.HelpBlock) GWT.create(org.gwtbootstrap3.client.ui.HelpBlock.class);
      // Setup section.

      this.owner.versionHelpBlock = versionHelpBlock;

      return versionHelpBlock;
    }

    /**
     * Getter for domId0Element called 2 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId0Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId0Element() {
      return domId0Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId0Element() {
      // Creation section.
      domId0Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId0());
      // Setup section.

      return domId0Element;
    }
  }
}
