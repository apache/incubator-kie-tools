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
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter.View;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpView;

public class Type_factory__o_u_e_e_c_c_f_p_SavePopUpView__quals__j_e_i_Any_j_e_i_Default extends Factory<SavePopUpView> { public interface o_u_e_e_c_c_f_p_SavePopUpViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/ext/editor/commons/client/file/popups/SavePopUpView.html") public TextResource getContents(); }
  public Type_factory__o_u_e_e_c_c_f_p_SavePopUpView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SavePopUpView.class, "Type_factory__o_u_e_e_c_c_f_p_SavePopUpView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SavePopUpView.class, Object.class, View.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public SavePopUpView createInstance(final ContextManager contextManager) {
    final SavePopUpView instance = new SavePopUpView();
    setIncompleteInstance(instance);
    final TranslationService SavePopUpView_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, SavePopUpView_translationService);
    SavePopUpView_TranslationService_translationService(instance, SavePopUpView_translationService);
    final Div SavePopUpView_body = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, SavePopUpView_body);
    SavePopUpView_Div_body(instance, SavePopUpView_body);
    final TextBox SavePopUpView_commentTextBox = (TextBox) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_TextBox__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, SavePopUpView_commentTextBox);
    SavePopUpView_TextBox_commentTextBox(instance, SavePopUpView_commentTextBox);
    o_u_e_e_c_c_f_p_SavePopUpViewTemplateResource templateForSavePopUpView = GWT.create(o_u_e_e_c_c_f_p_SavePopUpViewTemplateResource.class);
    Element parentElementForTemplateOfSavePopUpView = TemplateUtil.getRootTemplateParentElement(templateForSavePopUpView.getContents().getText(), "org/uberfire/ext/editor/commons/client/file/popups/SavePopUpView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/editor/commons/client/file/popups/SavePopUpView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSavePopUpView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSavePopUpView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("body", new DataFieldMeta());
    dataFieldMetas.put("commentTextBox", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.SavePopUpView", "org/uberfire/ext/editor/commons/client/file/popups/SavePopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SavePopUpView_Div_body(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "body");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.SavePopUpView", "org/uberfire/ext/editor/commons/client/file/popups/SavePopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return SavePopUpView_TextBox_commentTextBox(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "commentTextBox");
    templateFieldsMap.put("body", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SavePopUpView_Div_body(instance))));
    templateFieldsMap.put("commentTextBox", SavePopUpView_TextBox_commentTextBox(instance).asWidget());
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSavePopUpView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((SavePopUpView) instance, contextManager);
  }

  public void destroyInstanceHelper(final SavePopUpView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static TranslationService SavePopUpView_TranslationService_translationService(SavePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.SavePopUpView::translationService;
  }-*/;

  native static void SavePopUpView_TranslationService_translationService(SavePopUpView instance, TranslationService value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.SavePopUpView::translationService = value;
  }-*/;

  native static TextBox SavePopUpView_TextBox_commentTextBox(SavePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.SavePopUpView::commentTextBox;
  }-*/;

  native static void SavePopUpView_TextBox_commentTextBox(SavePopUpView instance, TextBox value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.SavePopUpView::commentTextBox = value;
  }-*/;

  native static Div SavePopUpView_Div_body(SavePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.SavePopUpView::body;
  }-*/;

  native static void SavePopUpView_Div_body(SavePopUpView instance, Div value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.SavePopUpView::body = value;
  }-*/;
}