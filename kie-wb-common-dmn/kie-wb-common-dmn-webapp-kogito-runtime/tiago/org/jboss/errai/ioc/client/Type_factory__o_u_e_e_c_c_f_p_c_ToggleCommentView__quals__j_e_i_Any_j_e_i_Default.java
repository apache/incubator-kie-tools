package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.common.client.api.elemental2.IsElement;
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
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter.View;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentView;

public class Type_factory__o_u_e_e_c_c_f_p_c_ToggleCommentView__quals__j_e_i_Any_j_e_i_Default extends Factory<ToggleCommentView> { public interface o_u_e_e_c_c_f_p_c_ToggleCommentViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/ext/editor/commons/client/file/popups/commons/ToggleCommentView.html") public TextResource getContents(); }
  public Type_factory__o_u_e_e_c_c_f_p_c_ToggleCommentView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ToggleCommentView.class, "Type_factory__o_u_e_e_c_c_f_p_c_ToggleCommentView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ToggleCommentView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class });
  }

  public ToggleCommentView createInstance(final ContextManager contextManager) {
    final HTMLAnchorElement _addComment_1 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TextBox _commentTextBox_2 = (TextBox) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_TextBox__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_3 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final HTMLDivElement _view_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ToggleCommentView instance = new ToggleCommentView(_view_0, _addComment_1, _commentTextBox_2, _translationService_3);
    registerDependentScopedReference(instance, _addComment_1);
    registerDependentScopedReference(instance, _commentTextBox_2);
    registerDependentScopedReference(instance, _translationService_3);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    o_u_e_e_c_c_f_p_c_ToggleCommentViewTemplateResource templateForToggleCommentView = GWT.create(o_u_e_e_c_c_f_p_c_ToggleCommentViewTemplateResource.class);
    Element parentElementForTemplateOfToggleCommentView = TemplateUtil.getRootTemplateParentElement(templateForToggleCommentView.getContents().getText(), "org/uberfire/ext/editor/commons/client/file/popups/commons/ToggleCommentView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/editor/commons/client/file/popups/commons/ToggleCommentView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfToggleCommentView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfToggleCommentView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("view", new DataFieldMeta());
    dataFieldMetas.put("addComment", new DataFieldMeta());
    dataFieldMetas.put("commentTextBox", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentView", "org/uberfire/ext/editor/commons/client/file/popups/commons/ToggleCommentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ToggleCommentView_HTMLDivElement_view(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "view");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentView", "org/uberfire/ext/editor/commons/client/file/popups/commons/ToggleCommentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ToggleCommentView_HTMLAnchorElement_addComment(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "addComment");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentView", "org/uberfire/ext/editor/commons/client/file/popups/commons/ToggleCommentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ToggleCommentView_TextBox_commentTextBox(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "commentTextBox");
    templateFieldsMap.put("view", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ToggleCommentView_HTMLDivElement_view(instance))));
    templateFieldsMap.put("addComment", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ToggleCommentView_HTMLAnchorElement_addComment(instance))));
    templateFieldsMap.put("commentTextBox", ToggleCommentView_TextBox_commentTextBox(instance).asWidget());
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfToggleCommentView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("addComment"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.addComment(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ToggleCommentView) instance, contextManager);
  }

  public void destroyInstanceHelper(final ToggleCommentView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final ToggleCommentView instance) {
    instance.setup();
  }

  native static HTMLAnchorElement ToggleCommentView_HTMLAnchorElement_addComment(ToggleCommentView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentView::addComment;
  }-*/;

  native static void ToggleCommentView_HTMLAnchorElement_addComment(ToggleCommentView instance, HTMLAnchorElement value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentView::addComment = value;
  }-*/;

  native static HTMLDivElement ToggleCommentView_HTMLDivElement_view(ToggleCommentView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentView::view;
  }-*/;

  native static void ToggleCommentView_HTMLDivElement_view(ToggleCommentView instance, HTMLDivElement value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentView::view = value;
  }-*/;

  native static TextBox ToggleCommentView_TextBox_commentTextBox(ToggleCommentView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentView::commentTextBox;
  }-*/;

  native static void ToggleCommentView_TextBox_commentTextBox(ToggleCommentView instance, TextBox value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentView::commentTextBox = value;
  }-*/;
}