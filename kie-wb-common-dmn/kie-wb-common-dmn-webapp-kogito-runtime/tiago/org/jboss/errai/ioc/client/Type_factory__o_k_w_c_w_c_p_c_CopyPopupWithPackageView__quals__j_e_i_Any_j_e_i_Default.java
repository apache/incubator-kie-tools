package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.widgets.client.handlers.PackageListBox;
import org.kie.workbench.common.widgets.client.handlers.PathLabel;
import org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter.View;

public class Type_factory__o_k_w_c_w_c_p_c_CopyPopupWithPackageView__quals__j_e_i_Any_j_e_i_Default extends Factory<CopyPopupWithPackageView> { public interface o_k_w_c_w_c_p_c_CopyPopupWithPackageViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/widgets/client/popups/copy/CopyPopupWithPackageView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_w_c_p_c_CopyPopupWithPackageView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CopyPopupWithPackageView.class, "Type_factory__o_k_w_c_w_c_p_c_CopyPopupWithPackageView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CopyPopupWithPackageView.class, Object.class, View.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public CopyPopupWithPackageView createInstance(final ContextManager contextManager) {
    final CopyPopupWithPackageView instance = new CopyPopupWithPackageView();
    setIncompleteInstance(instance);
    final TranslationService CopyPopupWithPackageView_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, CopyPopupWithPackageView_translationService);
    CopyPopupWithPackageView_TranslationService_translationService(instance, CopyPopupWithPackageView_translationService);
    final PathLabel CopyPopupWithPackageView_newNameLabel = (PathLabel) contextManager.getInstance("Type_factory__o_k_w_c_w_c_h_PathLabel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, CopyPopupWithPackageView_newNameLabel);
    CopyPopupWithPackageView_Label_newNameLabel(instance, CopyPopupWithPackageView_newNameLabel);
    final TextBox CopyPopupWithPackageView_newNameTextBox = (TextBox) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_TextBox__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, CopyPopupWithPackageView_newNameTextBox);
    CopyPopupWithPackageView_TextBox_newNameTextBox(instance, CopyPopupWithPackageView_newNameTextBox);
    final Span CopyPopupWithPackageView_errorMessage = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, CopyPopupWithPackageView_errorMessage);
    CopyPopupWithPackageView_Span_errorMessage(instance, CopyPopupWithPackageView_errorMessage);
    final Div CopyPopupWithPackageView_error = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, CopyPopupWithPackageView_error);
    CopyPopupWithPackageView_Div_error(instance, CopyPopupWithPackageView_error);
    final PackageListBox CopyPopupWithPackageView_packageListBox = (PackageListBox) contextManager.getInstance("Type_factory__o_k_w_c_w_c_h_PackageListBox__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, CopyPopupWithPackageView_packageListBox);
    CopyPopupWithPackageView_PackageListBox_packageListBox(instance, CopyPopupWithPackageView_packageListBox);
    final WorkspaceProjectContext CopyPopupWithPackageView_context = (WorkspaceProjectContext) contextManager.getInstance("Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_Default");
    CopyPopupWithPackageView_WorkspaceProjectContext_context(instance, CopyPopupWithPackageView_context);
    final HelpBlock CopyPopupWithPackageView_packageHelpInline = (HelpBlock) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_HelpBlock__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, CopyPopupWithPackageView_packageHelpInline);
    CopyPopupWithPackageView_HelpBlock_packageHelpInline(instance, CopyPopupWithPackageView_packageHelpInline);
    final Div CopyPopupWithPackageView_body = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, CopyPopupWithPackageView_body);
    CopyPopupWithPackageView_Div_body(instance, CopyPopupWithPackageView_body);
    o_k_w_c_w_c_p_c_CopyPopupWithPackageViewTemplateResource templateForCopyPopupWithPackageView = GWT.create(o_k_w_c_w_c_p_c_CopyPopupWithPackageViewTemplateResource.class);
    Element parentElementForTemplateOfCopyPopupWithPackageView = TemplateUtil.getRootTemplateParentElement(templateForCopyPopupWithPackageView.getContents().getText(), "org/kie/workbench/common/widgets/client/popups/copy/CopyPopupWithPackageView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/widgets/client/popups/copy/CopyPopupWithPackageView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCopyPopupWithPackageView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCopyPopupWithPackageView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(7);
    dataFieldMetas.put("body", new DataFieldMeta());
    dataFieldMetas.put("newNameTextBox", new DataFieldMeta());
    dataFieldMetas.put("newNameLabel", new DataFieldMeta());
    dataFieldMetas.put("error", new DataFieldMeta());
    dataFieldMetas.put("errorMessage", new DataFieldMeta());
    dataFieldMetas.put("packageListBox", new DataFieldMeta());
    dataFieldMetas.put("packageHelpInline", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView", "org/kie/workbench/common/widgets/client/popups/copy/CopyPopupWithPackageView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CopyPopupWithPackageView_Div_body(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "body");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView", "org/kie/workbench/common/widgets/client/popups/copy/CopyPopupWithPackageView.html", new Supplier<Widget>() {
      public Widget get() {
        return CopyPopupWithPackageView_TextBox_newNameTextBox(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "newNameTextBox");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView", "org/kie/workbench/common/widgets/client/popups/copy/CopyPopupWithPackageView.html", new Supplier<Widget>() {
      public Widget get() {
        return CopyPopupWithPackageView_Label_newNameLabel(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "newNameLabel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView", "org/kie/workbench/common/widgets/client/popups/copy/CopyPopupWithPackageView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CopyPopupWithPackageView_Div_error(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "error");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView", "org/kie/workbench/common/widgets/client/popups/copy/CopyPopupWithPackageView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CopyPopupWithPackageView_Span_errorMessage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "errorMessage");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView", "org/kie/workbench/common/widgets/client/popups/copy/CopyPopupWithPackageView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(CopyPopupWithPackageView_PackageListBox_packageListBox(instance).getElement());
      }
    }, dataFieldElements, dataFieldMetas, "packageListBox");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView", "org/kie/workbench/common/widgets/client/popups/copy/CopyPopupWithPackageView.html", new Supplier<Widget>() {
      public Widget get() {
        return CopyPopupWithPackageView_HelpBlock_packageHelpInline(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "packageHelpInline");
    templateFieldsMap.put("body", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CopyPopupWithPackageView_Div_body(instance))));
    templateFieldsMap.put("newNameTextBox", CopyPopupWithPackageView_TextBox_newNameTextBox(instance).asWidget());
    templateFieldsMap.put("newNameLabel", CopyPopupWithPackageView_Label_newNameLabel(instance).asWidget());
    templateFieldsMap.put("error", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CopyPopupWithPackageView_Div_error(instance))));
    templateFieldsMap.put("errorMessage", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CopyPopupWithPackageView_Span_errorMessage(instance))));
    templateFieldsMap.put("packageListBox", ElementWrapperWidget.getWidget(CopyPopupWithPackageView_PackageListBox_packageListBox(instance).getElement()));
    templateFieldsMap.put("packageHelpInline", CopyPopupWithPackageView_HelpBlock_packageHelpInline(instance).asWidget());
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCopyPopupWithPackageView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CopyPopupWithPackageView) instance, contextManager);
  }

  public void destroyInstanceHelper(final CopyPopupWithPackageView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Span CopyPopupWithPackageView_Span_errorMessage(CopyPopupWithPackageView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::errorMessage;
  }-*/;

  native static void CopyPopupWithPackageView_Span_errorMessage(CopyPopupWithPackageView instance, Span value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::errorMessage = value;
  }-*/;

  native static TranslationService CopyPopupWithPackageView_TranslationService_translationService(CopyPopupWithPackageView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::translationService;
  }-*/;

  native static void CopyPopupWithPackageView_TranslationService_translationService(CopyPopupWithPackageView instance, TranslationService value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::translationService = value;
  }-*/;

  native static TextBox CopyPopupWithPackageView_TextBox_newNameTextBox(CopyPopupWithPackageView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::newNameTextBox;
  }-*/;

  native static void CopyPopupWithPackageView_TextBox_newNameTextBox(CopyPopupWithPackageView instance, TextBox value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::newNameTextBox = value;
  }-*/;

  native static HelpBlock CopyPopupWithPackageView_HelpBlock_packageHelpInline(CopyPopupWithPackageView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::packageHelpInline;
  }-*/;

  native static void CopyPopupWithPackageView_HelpBlock_packageHelpInline(CopyPopupWithPackageView instance, HelpBlock value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::packageHelpInline = value;
  }-*/;

  native static Div CopyPopupWithPackageView_Div_body(CopyPopupWithPackageView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::body;
  }-*/;

  native static void CopyPopupWithPackageView_Div_body(CopyPopupWithPackageView instance, Div value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::body = value;
  }-*/;

  native static Label CopyPopupWithPackageView_Label_newNameLabel(CopyPopupWithPackageView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::newNameLabel;
  }-*/;

  native static void CopyPopupWithPackageView_Label_newNameLabel(CopyPopupWithPackageView instance, Label value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::newNameLabel = value;
  }-*/;

  native static PackageListBox CopyPopupWithPackageView_PackageListBox_packageListBox(CopyPopupWithPackageView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::packageListBox;
  }-*/;

  native static void CopyPopupWithPackageView_PackageListBox_packageListBox(CopyPopupWithPackageView instance, PackageListBox value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::packageListBox = value;
  }-*/;

  native static WorkspaceProjectContext CopyPopupWithPackageView_WorkspaceProjectContext_context(CopyPopupWithPackageView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::context;
  }-*/;

  native static void CopyPopupWithPackageView_WorkspaceProjectContext_context(CopyPopupWithPackageView instance, WorkspaceProjectContext value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::context = value;
  }-*/;

  native static Div CopyPopupWithPackageView_Div_error(CopyPopupWithPackageView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::error;
  }-*/;

  native static void CopyPopupWithPackageView_Div_error(CopyPopupWithPackageView instance, Div value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView::error = value;
  }-*/;
}