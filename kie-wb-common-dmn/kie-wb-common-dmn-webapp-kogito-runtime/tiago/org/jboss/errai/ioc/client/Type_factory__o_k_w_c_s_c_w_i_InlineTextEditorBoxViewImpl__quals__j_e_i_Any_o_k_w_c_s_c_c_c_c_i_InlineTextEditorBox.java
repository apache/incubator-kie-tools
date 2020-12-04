package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.stunner.client.widgets.inlineeditor.AbstractInlineTextEditorBoxView;
import org.kie.workbench.common.stunner.client.widgets.inlineeditor.InlineEditorBoxView;
import org.kie.workbench.common.stunner.client.widgets.inlineeditor.InlineTextEditorBoxViewImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.InlineTextEditorBox;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;

public class Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox extends Factory<InlineTextEditorBoxViewImpl> { public interface o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/stunner/client/widgets/inlineeditor/InlineTextEditorBox.html") public TextResource getContents();
  @Source("org/kie/workbench/common/stunner/client/widgets/inlineeditor/InlineTextEditorBox.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox() {
    super(new FactoryHandleImpl(InlineTextEditorBoxViewImpl.class, "Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { InlineTextEditorBoxViewImpl.class, AbstractInlineTextEditorBoxView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, InlineEditorBoxView.class, UberElement.class, HasPresenter.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new InlineTextEditorBox() {
        public Class annotationType() {
          return InlineTextEditorBox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.InlineTextEditorBox()";
        }
    } });
  }

  public void init(final Context context) {
    ((o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImplTemplateResource) GWT.create(o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public InlineTextEditorBoxViewImpl createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final InlineTextEditorBoxViewImpl instance = new InlineTextEditorBoxViewImpl(_translationService_0);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    final Div AbstractInlineTextEditorBoxView_editNameBox = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AbstractInlineTextEditorBoxView_editNameBox);
    AbstractInlineTextEditorBoxView_Div_editNameBox(instance, AbstractInlineTextEditorBoxView_editNameBox);
    final Div InlineTextEditorBoxViewImpl_nameField = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, InlineTextEditorBoxViewImpl_nameField);
    InlineTextEditorBoxViewImpl_Div_nameField(instance, InlineTextEditorBoxViewImpl_nameField);
    o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImplTemplateResource templateForInlineTextEditorBoxViewImpl = GWT.create(o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImplTemplateResource.class);
    Element parentElementForTemplateOfInlineTextEditorBoxViewImpl = TemplateUtil.getRootTemplateParentElement(templateForInlineTextEditorBoxViewImpl.getContents().getText(), "org/kie/workbench/common/stunner/client/widgets/inlineeditor/InlineTextEditorBox.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/client/widgets/inlineeditor/InlineTextEditorBox.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfInlineTextEditorBoxViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfInlineTextEditorBoxViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("editNameBox", new DataFieldMeta());
    dataFieldMetas.put("nameField", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.inlineeditor.InlineTextEditorBoxViewImpl", "org/kie/workbench/common/stunner/client/widgets/inlineeditor/InlineTextEditorBox.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractInlineTextEditorBoxView_Div_editNameBox(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "editNameBox");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.inlineeditor.InlineTextEditorBoxViewImpl", "org/kie/workbench/common/stunner/client/widgets/inlineeditor/InlineTextEditorBox.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(InlineTextEditorBoxViewImpl_Div_nameField(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "nameField");
    templateFieldsMap.put("editNameBox", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractInlineTextEditorBoxView_Div_editNameBox(instance))));
    templateFieldsMap.put("nameField", ElementWrapperWidget.getWidget(TemplateUtil.asElement(InlineTextEditorBoxViewImpl_Div_nameField(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfInlineTextEditorBoxViewImpl), templateFieldsMap.values());
    TemplateUtil.setupNativeEventListener(instance, (ElementWrapperWidget) templateFieldsMap.get("nameField"), new EventListener() {
      public void onBrowserEvent(Event event) {
        InlineTextEditorBoxViewImpl_onChangeName_Event(instance, event);
      }
    }, 4992);
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((InlineTextEditorBoxViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final InlineTextEditorBoxViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final InlineTextEditorBoxViewImpl instance) {
    instance.initialize();
  }

  native static Div AbstractInlineTextEditorBoxView_Div_editNameBox(AbstractInlineTextEditorBoxView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.inlineeditor.AbstractInlineTextEditorBoxView::editNameBox;
  }-*/;

  native static void AbstractInlineTextEditorBoxView_Div_editNameBox(AbstractInlineTextEditorBoxView instance, Div value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.inlineeditor.AbstractInlineTextEditorBoxView::editNameBox = value;
  }-*/;

  native static Div InlineTextEditorBoxViewImpl_Div_nameField(InlineTextEditorBoxViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.inlineeditor.InlineTextEditorBoxViewImpl::nameField;
  }-*/;

  native static void InlineTextEditorBoxViewImpl_Div_nameField(InlineTextEditorBoxViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.inlineeditor.InlineTextEditorBoxViewImpl::nameField = value;
  }-*/;

  public native static void InlineTextEditorBoxViewImpl_onChangeName_Event(InlineTextEditorBoxViewImpl instance, Event a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.inlineeditor.InlineTextEditorBoxViewImpl::onChangeName(Lcom/google/gwt/user/client/Event;)(a0);
  }-*/;
}