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
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
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
import org.uberfire.ext.editor.commons.client.file.popups.RestorePopUpPresenter.View;
import org.uberfire.ext.editor.commons.client.file.popups.RestorePopUpView;

public class Type_factory__o_u_e_e_c_c_f_p_RestorePopUpView__quals__j_e_i_Any_j_e_i_Default extends Factory<RestorePopUpView> { public interface o_u_e_e_c_c_f_p_RestorePopUpViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/ext/editor/commons/client/file/popups/RestorePopUpView.html") public TextResource getContents(); }
  public Type_factory__o_u_e_e_c_c_f_p_RestorePopUpView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(RestorePopUpView.class, "Type_factory__o_u_e_e_c_c_f_p_RestorePopUpView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { RestorePopUpView.class, Object.class, View.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public RestorePopUpView createInstance(final ContextManager contextManager) {
    final RestorePopUpView instance = new RestorePopUpView();
    setIncompleteInstance(instance);
    final Div RestorePopUpView_body = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, RestorePopUpView_body);
    RestorePopUpView_Div_body(instance, RestorePopUpView_body);
    final TextBox RestorePopUpView_commentTextBox = (TextBox) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_TextBox__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, RestorePopUpView_commentTextBox);
    RestorePopUpView_TextBox_commentTextBox(instance, RestorePopUpView_commentTextBox);
    final TranslationService RestorePopUpView_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, RestorePopUpView_translationService);
    RestorePopUpView_TranslationService_translationService(instance, RestorePopUpView_translationService);
    o_u_e_e_c_c_f_p_RestorePopUpViewTemplateResource templateForRestorePopUpView = GWT.create(o_u_e_e_c_c_f_p_RestorePopUpViewTemplateResource.class);
    Element parentElementForTemplateOfRestorePopUpView = TemplateUtil.getRootTemplateParentElement(templateForRestorePopUpView.getContents().getText(), "org/uberfire/ext/editor/commons/client/file/popups/RestorePopUpView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/editor/commons/client/file/popups/RestorePopUpView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfRestorePopUpView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfRestorePopUpView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("body", new DataFieldMeta());
    dataFieldMetas.put("commentTextBox", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.RestorePopUpView", "org/uberfire/ext/editor/commons/client/file/popups/RestorePopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(RestorePopUpView_Div_body(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "body");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.RestorePopUpView", "org/uberfire/ext/editor/commons/client/file/popups/RestorePopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return RestorePopUpView_TextBox_commentTextBox(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "commentTextBox");
    templateFieldsMap.put("body", ElementWrapperWidget.getWidget(TemplateUtil.asElement(RestorePopUpView_Div_body(instance))));
    templateFieldsMap.put("commentTextBox", RestorePopUpView_TextBox_commentTextBox(instance).asWidget());
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfRestorePopUpView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((RestorePopUpView) instance, contextManager);
  }

  public void destroyInstanceHelper(final RestorePopUpView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static TextBox RestorePopUpView_TextBox_commentTextBox(RestorePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.RestorePopUpView::commentTextBox;
  }-*/;

  native static void RestorePopUpView_TextBox_commentTextBox(RestorePopUpView instance, TextBox value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.RestorePopUpView::commentTextBox = value;
  }-*/;

  native static TranslationService RestorePopUpView_TranslationService_translationService(RestorePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.RestorePopUpView::translationService;
  }-*/;

  native static void RestorePopUpView_TranslationService_translationService(RestorePopUpView instance, TranslationService value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.RestorePopUpView::translationService = value;
  }-*/;

  native static Div RestorePopUpView_Div_body(RestorePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.RestorePopUpView::body;
  }-*/;

  native static void RestorePopUpView_Div_body(RestorePopUpView instance, Div value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.RestorePopUpView::body = value;
  }-*/;
}