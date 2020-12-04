package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
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
import org.uberfire.annotations.FallbackImplementation;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter.View;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView;

public class Type_factory__o_u_e_e_c_c_f_p_CopyPopUpView__quals__j_e_i_Any_o_u_a_FallbackImplementation extends Factory<CopyPopUpView> { public interface o_u_e_e_c_c_f_p_CopyPopUpViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/ext/editor/commons/client/file/popups/CopyPopUpView.html") public TextResource getContents(); }
  public Type_factory__o_u_e_e_c_c_f_p_CopyPopUpView__quals__j_e_i_Any_o_u_a_FallbackImplementation() {
    super(new FactoryHandleImpl(CopyPopUpView.class, "Type_factory__o_u_e_e_c_c_f_p_CopyPopUpView__quals__j_e_i_Any_o_u_a_FallbackImplementation", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CopyPopUpView.class, Object.class, View.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new FallbackImplementation() {
        public Class annotationType() {
          return FallbackImplementation.class;
        }
        public String toString() {
          return "@org.uberfire.annotations.FallbackImplementation()";
        }
    } });
  }

  public CopyPopUpView createInstance(final ContextManager contextManager) {
    final CopyPopUpView instance = new CopyPopUpView();
    setIncompleteInstance(instance);
    final Div CopyPopUpView_error = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, CopyPopUpView_error);
    CopyPopUpView_Div_error(instance, CopyPopUpView_error);
    final TranslationService CopyPopUpView_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, CopyPopUpView_translationService);
    CopyPopUpView_TranslationService_translationService(instance, CopyPopUpView_translationService);
    final TextBox CopyPopUpView_newNameTextBox = (TextBox) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_TextBox__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, CopyPopUpView_newNameTextBox);
    CopyPopUpView_TextBox_newNameTextBox(instance, CopyPopUpView_newNameTextBox);
    final Span CopyPopUpView_errorMessage = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, CopyPopUpView_errorMessage);
    CopyPopUpView_Span_errorMessage(instance, CopyPopUpView_errorMessage);
    final Div CopyPopUpView_body = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, CopyPopUpView_body);
    CopyPopUpView_Div_body(instance, CopyPopUpView_body);
    o_u_e_e_c_c_f_p_CopyPopUpViewTemplateResource templateForCopyPopUpView = GWT.create(o_u_e_e_c_c_f_p_CopyPopUpViewTemplateResource.class);
    Element parentElementForTemplateOfCopyPopUpView = TemplateUtil.getRootTemplateParentElement(templateForCopyPopUpView.getContents().getText(), "org/uberfire/ext/editor/commons/client/file/popups/CopyPopUpView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/editor/commons/client/file/popups/CopyPopUpView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCopyPopUpView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCopyPopUpView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(4);
    dataFieldMetas.put("body", new DataFieldMeta());
    dataFieldMetas.put("newNameTextBox", new DataFieldMeta());
    dataFieldMetas.put("error", new DataFieldMeta());
    dataFieldMetas.put("errorMessage", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView", "org/uberfire/ext/editor/commons/client/file/popups/CopyPopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CopyPopUpView_Div_body(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "body");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView", "org/uberfire/ext/editor/commons/client/file/popups/CopyPopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return CopyPopUpView_TextBox_newNameTextBox(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "newNameTextBox");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView", "org/uberfire/ext/editor/commons/client/file/popups/CopyPopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CopyPopUpView_Div_error(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "error");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView", "org/uberfire/ext/editor/commons/client/file/popups/CopyPopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CopyPopUpView_Span_errorMessage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "errorMessage");
    templateFieldsMap.put("body", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CopyPopUpView_Div_body(instance))));
    templateFieldsMap.put("newNameTextBox", CopyPopUpView_TextBox_newNameTextBox(instance).asWidget());
    templateFieldsMap.put("error", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CopyPopUpView_Div_error(instance))));
    templateFieldsMap.put("errorMessage", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CopyPopUpView_Span_errorMessage(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCopyPopUpView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CopyPopUpView) instance, contextManager);
  }

  public void destroyInstanceHelper(final CopyPopUpView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Span CopyPopUpView_Span_errorMessage(CopyPopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView::errorMessage;
  }-*/;

  native static void CopyPopUpView_Span_errorMessage(CopyPopUpView instance, Span value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView::errorMessage = value;
  }-*/;

  native static TextBox CopyPopUpView_TextBox_newNameTextBox(CopyPopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView::newNameTextBox;
  }-*/;

  native static void CopyPopUpView_TextBox_newNameTextBox(CopyPopUpView instance, TextBox value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView::newNameTextBox = value;
  }-*/;

  native static Div CopyPopUpView_Div_error(CopyPopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView::error;
  }-*/;

  native static void CopyPopUpView_Div_error(CopyPopUpView instance, Div value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView::error = value;
  }-*/;

  native static Div CopyPopUpView_Div_body(CopyPopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView::body;
  }-*/;

  native static void CopyPopUpView_Div_body(CopyPopUpView instance, Div value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView::body = value;
  }-*/;

  native static TranslationService CopyPopUpView_TranslationService_translationService(CopyPopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView::translationService;
  }-*/;

  native static void CopyPopUpView_TranslationService_translationService(CopyPopUpView instance, TranslationService value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView::translationService = value;
  }-*/;
}