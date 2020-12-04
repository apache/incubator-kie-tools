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

public class POMEditorPanelViewImpl_GroupArtifactVersionEditorPanelViewImplBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.guvnor.common.services.project.client.POMEditorPanelViewImpl>, org.guvnor.common.services.project.client.POMEditorPanelViewImpl.GroupArtifactVersionEditorPanelViewImplBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("" + "{0}" + "")
    SafeHtml html1(String arg0);
     
    @Template("" + "{0}" + "")
    SafeHtml html2(String arg0);
     
    @Template("" + "{0}" + "")
    SafeHtml html3(String arg0);
     
    @Template("<span id='{0}'></span>")
    SafeHtml html4(String arg0);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.guvnor.common.services.project.client.POMEditorPanelViewImpl owner) {


    return new Widgets(owner).get_f_HTMLPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.guvnor.common.services.project.client.POMEditorPanelViewImpl owner;


    final com.google.gwt.event.dom.client.KeyUpHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.KeyUpHandler() {
      public void onKeyUp(com.google.gwt.event.dom.client.KeyUpEvent event) {
        owner.onNameChange((com.google.gwt.event.dom.client.KeyUpEvent) event);
      }
    };

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.onOpenProjectContext((com.google.gwt.event.dom.client.ClickEvent) event);
      }
    };

    final com.google.gwt.event.logical.shared.ValueChangeHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3 = new com.google.gwt.event.logical.shared.ValueChangeHandler() {
      public void onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent event) {
        owner.onDescriptionChange((com.google.gwt.event.logical.shared.ValueChangeEvent<java.lang.String>) event);
      }
    };

    public Widgets(final org.guvnor.common.services.project.client.POMEditorPanelViewImpl owner) {
      this.owner = owner;
      build_i18n();  // more than one getter call detected. Type: IMPORTED, precedence: 1
      build_domId0();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId0Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
    }

    SafeHtml template_html1() {
      return template.html1("" + get_i18n().ProjectGeneralSettings() + "");
    }
    SafeHtml template_html2() {
      return template.html2("" + get_i18n().ParentsGroupArtifactVersion() + "");
    }
    SafeHtml template_html3() {
      return template.html3("" + get_i18n().GroupArtifactVersion() + "");
    }
    SafeHtml template_html4() {
      return template.html4(get_domId0());
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.guvnor.common.services.project.client.POMEditorPanelViewImpl_GroupArtifactVersionEditorPanelViewImplBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.guvnor.common.services.project.client.POMEditorPanelViewImpl_GroupArtifactVersionEditorPanelViewImplBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.guvnor.common.services.project.client.POMEditorPanelViewImpl_GroupArtifactVersionEditorPanelViewImplBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.guvnor.common.services.project.client.POMEditorPanelViewImpl_GroupArtifactVersionEditorPanelViewImplBinderImpl_GenBundle) GWT.create(org.guvnor.common.services.project.client.POMEditorPanelViewImpl_GroupArtifactVersionEditorPanelViewImplBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for i18n called 7 times. Type: IMPORTED. Build precedence: 1.
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
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template_html4().asString());
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
      f_Form2.add(get_f_FieldSet3());
      f_Form2.add(get_parentGavEditorFieldSet());
      f_Form2.add(get_f_FieldSet11());
      f_Form2.setType(org.gwtbootstrap3.client.ui.constants.FormType.HORIZONTAL);

      return f_Form2;
    }

    /**
     * Getter for f_FieldSet3 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.FieldSet get_f_FieldSet3() {
      return build_f_FieldSet3();
    }
    private org.gwtbootstrap3.client.ui.FieldSet build_f_FieldSet3() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.FieldSet f_FieldSet3 = (org.gwtbootstrap3.client.ui.FieldSet) GWT.create(org.gwtbootstrap3.client.ui.FieldSet.class);
      // Setup section.
      f_FieldSet3.add(get_f_Legend4());
      f_FieldSet3.add(get_pomNameGroup());
      f_FieldSet3.add(get_f_FormGroup7());

      return f_FieldSet3;
    }

    /**
     * Getter for f_Legend4 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Legend get_f_Legend4() {
      return build_f_Legend4();
    }
    private org.gwtbootstrap3.client.ui.Legend build_f_Legend4() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Legend f_Legend4 = (org.gwtbootstrap3.client.ui.Legend) GWT.create(org.gwtbootstrap3.client.ui.Legend.class);
      // Setup section.
      f_Legend4.setHTML(template_html1().asString());

      return f_Legend4;
    }

    /**
     * Getter for pomNameGroup called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.FormGroup get_pomNameGroup() {
      return build_pomNameGroup();
    }
    private org.gwtbootstrap3.client.ui.FormGroup build_pomNameGroup() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.FormGroup pomNameGroup = (org.gwtbootstrap3.client.ui.FormGroup) GWT.create(org.gwtbootstrap3.client.ui.FormGroup.class);
      // Setup section.
      pomNameGroup.add(get_f_FormLabel5());
      pomNameGroup.add(get_f_Column6());

      this.owner.pomNameGroup = pomNameGroup;

      return pomNameGroup;
    }

    /**
     * Getter for f_FormLabel5 called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.FormLabel get_f_FormLabel5() {
      return build_f_FormLabel5();
    }
    private org.gwtbootstrap3.client.ui.FormLabel build_f_FormLabel5() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.FormLabel f_FormLabel5 = (org.gwtbootstrap3.client.ui.FormLabel) GWT.create(org.gwtbootstrap3.client.ui.FormLabel.class);
      // Setup section.
      f_FormLabel5.addStyleName("col-md-4");
      f_FormLabel5.setText("" + get_i18n().ProjectName() + "");

      return f_FormLabel5;
    }

    /**
     * Getter for f_Column6 called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.Column get_f_Column6() {
      return build_f_Column6();
    }
    private org.gwtbootstrap3.client.ui.Column build_f_Column6() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column f_Column6 = new org.gwtbootstrap3.client.ui.Column("MD_8");
      // Setup section.
      f_Column6.add(get_pomNameTextBox());
      f_Column6.add(get_pomNameHelpBlock());

      return f_Column6;
    }

    /**
     * Getter for pomNameTextBox called 1 times. Type: DEFAULT. Build precedence: 6.
     */
    private org.gwtbootstrap3.client.ui.TextBox get_pomNameTextBox() {
      return build_pomNameTextBox();
    }
    private org.gwtbootstrap3.client.ui.TextBox build_pomNameTextBox() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.TextBox pomNameTextBox = (org.gwtbootstrap3.client.ui.TextBox) GWT.create(org.gwtbootstrap3.client.ui.TextBox.class);
      // Setup section.
      pomNameTextBox.setPlaceholder("" + get_i18n().ProjectNamePlaceHolder() + "");
      pomNameTextBox.addKeyUpHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

      this.owner.pomNameTextBox = pomNameTextBox;

      return pomNameTextBox;
    }

    /**
     * Getter for pomNameHelpBlock called 1 times. Type: DEFAULT. Build precedence: 6.
     */
    private org.gwtbootstrap3.client.ui.HelpBlock get_pomNameHelpBlock() {
      return build_pomNameHelpBlock();
    }
    private org.gwtbootstrap3.client.ui.HelpBlock build_pomNameHelpBlock() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.HelpBlock pomNameHelpBlock = (org.gwtbootstrap3.client.ui.HelpBlock) GWT.create(org.gwtbootstrap3.client.ui.HelpBlock.class);
      // Setup section.

      this.owner.pomNameHelpBlock = pomNameHelpBlock;

      return pomNameHelpBlock;
    }

    /**
     * Getter for f_FormGroup7 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.FormGroup get_f_FormGroup7() {
      return build_f_FormGroup7();
    }
    private org.gwtbootstrap3.client.ui.FormGroup build_f_FormGroup7() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.FormGroup f_FormGroup7 = (org.gwtbootstrap3.client.ui.FormGroup) GWT.create(org.gwtbootstrap3.client.ui.FormGroup.class);
      // Setup section.
      f_FormGroup7.add(get_f_FormLabel8());
      f_FormGroup7.add(get_f_Column9());

      return f_FormGroup7;
    }

    /**
     * Getter for f_FormLabel8 called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.FormLabel get_f_FormLabel8() {
      return build_f_FormLabel8();
    }
    private org.gwtbootstrap3.client.ui.FormLabel build_f_FormLabel8() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.FormLabel f_FormLabel8 = (org.gwtbootstrap3.client.ui.FormLabel) GWT.create(org.gwtbootstrap3.client.ui.FormLabel.class);
      // Setup section.
      f_FormLabel8.addStyleName("col-md-4");
      f_FormLabel8.setText("" + get_i18n().ProjectDescription() + "");

      return f_FormLabel8;
    }

    /**
     * Getter for f_Column9 called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.Column get_f_Column9() {
      return build_f_Column9();
    }
    private org.gwtbootstrap3.client.ui.Column build_f_Column9() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column f_Column9 = new org.gwtbootstrap3.client.ui.Column("MD_8");
      // Setup section.
      f_Column9.add(get_pomDescriptionTextArea());

      return f_Column9;
    }

    /**
     * Getter for pomDescriptionTextArea called 1 times. Type: DEFAULT. Build precedence: 6.
     */
    private org.gwtbootstrap3.client.ui.TextArea get_pomDescriptionTextArea() {
      return build_pomDescriptionTextArea();
    }
    private org.gwtbootstrap3.client.ui.TextArea build_pomDescriptionTextArea() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.TextArea pomDescriptionTextArea = (org.gwtbootstrap3.client.ui.TextArea) GWT.create(org.gwtbootstrap3.client.ui.TextArea.class);
      // Setup section.
      pomDescriptionTextArea.setPlaceholder("" + get_i18n().ProjectDescriptionPlaceHolder() + "");
      pomDescriptionTextArea.addValueChangeHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3);

      this.owner.pomDescriptionTextArea = pomDescriptionTextArea;

      return pomDescriptionTextArea;
    }

    /**
     * Getter for parentGavEditorFieldSet called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.FieldSet get_parentGavEditorFieldSet() {
      return build_parentGavEditorFieldSet();
    }
    private org.gwtbootstrap3.client.ui.FieldSet build_parentGavEditorFieldSet() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.FieldSet parentGavEditorFieldSet = (org.gwtbootstrap3.client.ui.FieldSet) GWT.create(org.gwtbootstrap3.client.ui.FieldSet.class);
      // Setup section.
      parentGavEditorFieldSet.add(get_f_Legend10());
      parentGavEditorFieldSet.add(get_openProjectContext());
      parentGavEditorFieldSet.add(get_parentGavEditor());
      parentGavEditorFieldSet.setVisible(false);

      this.owner.parentGavEditorFieldSet = parentGavEditorFieldSet;

      return parentGavEditorFieldSet;
    }

    /**
     * Getter for f_Legend10 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Legend get_f_Legend10() {
      return build_f_Legend10();
    }
    private org.gwtbootstrap3.client.ui.Legend build_f_Legend10() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Legend f_Legend10 = (org.gwtbootstrap3.client.ui.Legend) GWT.create(org.gwtbootstrap3.client.ui.Legend.class);
      // Setup section.
      f_Legend10.setHTML(template_html2().asString());

      return f_Legend10;
    }

    /**
     * Getter for openProjectContext called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Button get_openProjectContext() {
      return build_openProjectContext();
    }
    private org.gwtbootstrap3.client.ui.Button build_openProjectContext() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Button openProjectContext = (org.gwtbootstrap3.client.ui.Button) GWT.create(org.gwtbootstrap3.client.ui.Button.class);
      // Setup section.
      openProjectContext.addStyleName("btn-mini");
      openProjectContext.setText("Open Project Context");
      openProjectContext.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

      return openProjectContext;
    }

    /**
     * Getter for parentGavEditor called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.guvnor.common.services.project.client.GAVEditor get_parentGavEditor() {
      return build_parentGavEditor();
    }
    private org.guvnor.common.services.project.client.GAVEditor build_parentGavEditor() {
      // Creation section.
      final org.guvnor.common.services.project.client.GAVEditor parentGavEditor = owner.parentGavEditor;
      assert parentGavEditor != null : "UiField parentGavEditor with 'provided = true' was null";
      // Setup section.

      return parentGavEditor;
    }

    /**
     * Getter for f_FieldSet11 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.FieldSet get_f_FieldSet11() {
      return build_f_FieldSet11();
    }
    private org.gwtbootstrap3.client.ui.FieldSet build_f_FieldSet11() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.FieldSet f_FieldSet11 = (org.gwtbootstrap3.client.ui.FieldSet) GWT.create(org.gwtbootstrap3.client.ui.FieldSet.class);
      // Setup section.
      f_FieldSet11.add(get_f_Legend12());
      f_FieldSet11.add(get_gavEditor());

      return f_FieldSet11;
    }

    /**
     * Getter for f_Legend12 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Legend get_f_Legend12() {
      return build_f_Legend12();
    }
    private org.gwtbootstrap3.client.ui.Legend build_f_Legend12() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Legend f_Legend12 = (org.gwtbootstrap3.client.ui.Legend) GWT.create(org.gwtbootstrap3.client.ui.Legend.class);
      // Setup section.
      f_Legend12.setHTML(template_html3().asString());

      return f_Legend12;
    }

    /**
     * Getter for gavEditor called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.guvnor.common.services.project.client.GAVEditor get_gavEditor() {
      return build_gavEditor();
    }
    private org.guvnor.common.services.project.client.GAVEditor build_gavEditor() {
      // Creation section.
      final org.guvnor.common.services.project.client.GAVEditor gavEditor = owner.gavEditor;
      assert gavEditor != null : "UiField gavEditor with 'provided = true' was null";
      // Setup section.

      return gavEditor;
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
