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
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Paragraph;
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
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter.View;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView;

public class Type_factory__o_u_e_e_c_c_f_p_DeletePopUpView__quals__j_e_i_Any_j_e_i_Default extends Factory<DeletePopUpView> { public interface o_u_e_e_c_c_f_p_DeletePopUpViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/ext/editor/commons/client/file/popups/DeletePopUpView.html") public TextResource getContents(); }
  public Type_factory__o_u_e_e_c_c_f_p_DeletePopUpView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DeletePopUpView.class, "Type_factory__o_u_e_e_c_c_f_p_DeletePopUpView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DeletePopUpView.class, Object.class, View.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public DeletePopUpView createInstance(final ContextManager contextManager) {
    final DeletePopUpView instance = new DeletePopUpView();
    setIncompleteInstance(instance);
    final Div DeletePopUpView_body = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DeletePopUpView_body);
    DeletePopUpView_Div_body(instance, DeletePopUpView_body);
    final Span DeletePopUpView_errorMessage = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DeletePopUpView_errorMessage);
    DeletePopUpView_Span_errorMessage(instance, DeletePopUpView_errorMessage);
    final TranslationService DeletePopUpView_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DeletePopUpView_translationService);
    DeletePopUpView_TranslationService_translationService(instance, DeletePopUpView_translationService);
    final Paragraph DeletePopUpView_bodyText = (Paragraph) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Paragraph__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DeletePopUpView_bodyText);
    DeletePopUpView_Paragraph_bodyText(instance, DeletePopUpView_bodyText);
    final Div DeletePopUpView_error = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DeletePopUpView_error);
    DeletePopUpView_Div_error(instance, DeletePopUpView_error);
    o_u_e_e_c_c_f_p_DeletePopUpViewTemplateResource templateForDeletePopUpView = GWT.create(o_u_e_e_c_c_f_p_DeletePopUpViewTemplateResource.class);
    Element parentElementForTemplateOfDeletePopUpView = TemplateUtil.getRootTemplateParentElement(templateForDeletePopUpView.getContents().getText(), "org/uberfire/ext/editor/commons/client/file/popups/DeletePopUpView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/editor/commons/client/file/popups/DeletePopUpView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDeletePopUpView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDeletePopUpView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(4);
    dataFieldMetas.put("body", new DataFieldMeta());
    dataFieldMetas.put("bodyText", new DataFieldMeta());
    dataFieldMetas.put("error", new DataFieldMeta());
    dataFieldMetas.put("errorMessage", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView", "org/uberfire/ext/editor/commons/client/file/popups/DeletePopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DeletePopUpView_Div_body(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "body");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView", "org/uberfire/ext/editor/commons/client/file/popups/DeletePopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DeletePopUpView_Paragraph_bodyText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "bodyText");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView", "org/uberfire/ext/editor/commons/client/file/popups/DeletePopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DeletePopUpView_Div_error(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "error");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView", "org/uberfire/ext/editor/commons/client/file/popups/DeletePopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DeletePopUpView_Span_errorMessage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "errorMessage");
    templateFieldsMap.put("body", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DeletePopUpView_Div_body(instance))));
    templateFieldsMap.put("bodyText", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DeletePopUpView_Paragraph_bodyText(instance))));
    templateFieldsMap.put("error", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DeletePopUpView_Div_error(instance))));
    templateFieldsMap.put("errorMessage", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DeletePopUpView_Span_errorMessage(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDeletePopUpView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DeletePopUpView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DeletePopUpView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static TranslationService DeletePopUpView_TranslationService_translationService(DeletePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView::translationService;
  }-*/;

  native static void DeletePopUpView_TranslationService_translationService(DeletePopUpView instance, TranslationService value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView::translationService = value;
  }-*/;

  native static Div DeletePopUpView_Div_error(DeletePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView::error;
  }-*/;

  native static void DeletePopUpView_Div_error(DeletePopUpView instance, Div value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView::error = value;
  }-*/;

  native static Span DeletePopUpView_Span_errorMessage(DeletePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView::errorMessage;
  }-*/;

  native static void DeletePopUpView_Span_errorMessage(DeletePopUpView instance, Span value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView::errorMessage = value;
  }-*/;

  native static Paragraph DeletePopUpView_Paragraph_bodyText(DeletePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView::bodyText;
  }-*/;

  native static void DeletePopUpView_Paragraph_bodyText(DeletePopUpView instance, Paragraph value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView::bodyText = value;
  }-*/;

  native static Div DeletePopUpView_Div_body(DeletePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView::body;
  }-*/;

  native static void DeletePopUpView_Div_body(DeletePopUpView instance, Div value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView::body = value;
  }-*/;
}