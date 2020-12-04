package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLLabelElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.widgets.client.widget.KSessionSelectorView;
import org.kie.workbench.common.widgets.client.widget.KSessionSelectorViewImpl;
import org.kie.workbench.common.widgets.client.widget.KieSelectElement;

public class Type_factory__o_k_w_c_w_c_w_KSessionSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<KSessionSelectorViewImpl> { public interface o_k_w_c_w_c_w_KSessionSelectorViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/widgets/client/widget/KSessionSelectorViewImpl.html") public TextResource getContents();
  @Source("org/kie/workbench/common/widgets/client/widget/KSessionSelectorViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_w_c_w_KSessionSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KSessionSelectorViewImpl.class, "Type_factory__o_k_w_c_w_c_w_KSessionSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KSessionSelectorViewImpl.class, Object.class, KSessionSelectorView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_w_c_w_KSessionSelectorViewImplTemplateResource) GWT.create(o_k_w_c_w_c_w_KSessionSelectorViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public KSessionSelectorViewImpl createInstance(final ContextManager contextManager) {
    final HTMLLabelElement _warningLabel_3 = (HTMLLabelElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLLabelElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final KieSelectElement _kbaseSelect_1 = (KieSelectElement) contextManager.getInstance("Type_factory__o_k_w_c_w_c_w_KieSelectElement__quals__j_e_i_Any_j_e_i_Default");
    final HTMLDocument _document_0 = (HTMLDocument) contextManager.getInstance("Producer_factory__e_d_HTMLDocument__quals__j_e_i_Any_j_e_i_Default");
    final KieSelectElement _ksessionSelect_2 = (KieSelectElement) contextManager.getInstance("Type_factory__o_k_w_c_w_c_w_KieSelectElement__quals__j_e_i_Any_j_e_i_Default");
    final KSessionSelectorViewImpl instance = new KSessionSelectorViewImpl(_document_0, _kbaseSelect_1, _ksessionSelect_2, _warningLabel_3);
    registerDependentScopedReference(instance, _warningLabel_3);
    registerDependentScopedReference(instance, _kbaseSelect_1);
    registerDependentScopedReference(instance, _document_0);
    registerDependentScopedReference(instance, _ksessionSelect_2);
    setIncompleteInstance(instance);
    final HTMLLabelElement KSessionSelectorViewImpl_warningLabel = (HTMLLabelElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLLabelElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, KSessionSelectorViewImpl_warningLabel);
    KSessionSelectorViewImpl_HTMLLabelElement_warningLabel(instance, KSessionSelectorViewImpl_warningLabel);
    final KieSelectElement KSessionSelectorViewImpl_ksessionSelect = (KieSelectElement) contextManager.getInstance("Type_factory__o_k_w_c_w_c_w_KieSelectElement__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, KSessionSelectorViewImpl_ksessionSelect);
    KSessionSelectorViewImpl_KieSelectElement_ksessionSelect(instance, KSessionSelectorViewImpl_ksessionSelect);
    o_k_w_c_w_c_w_KSessionSelectorViewImplTemplateResource templateForKSessionSelectorViewImpl = GWT.create(o_k_w_c_w_c_w_KSessionSelectorViewImplTemplateResource.class);
    Element parentElementForTemplateOfKSessionSelectorViewImpl = TemplateUtil.getRootTemplateParentElement(templateForKSessionSelectorViewImpl.getContents().getText(), "org/kie/workbench/common/widgets/client/widget/KSessionSelectorViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/widgets/client/widget/KSessionSelectorViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKSessionSelectorViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKSessionSelectorViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("kbaseSelectContainer", new DataFieldMeta());
    dataFieldMetas.put("ksessionSelectContainer", new DataFieldMeta());
    dataFieldMetas.put("warningLabel", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.widget.KSessionSelectorViewImpl", "org/kie/workbench/common/widgets/client/widget/KSessionSelectorViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(KSessionSelectorViewImpl_KieSelectElement_kbaseSelect(instance).getElement(), null);
      }
    }, dataFieldElements, dataFieldMetas, "kbaseSelectContainer");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.widget.KSessionSelectorViewImpl", "org/kie/workbench/common/widgets/client/widget/KSessionSelectorViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(KSessionSelectorViewImpl_KieSelectElement_ksessionSelect(instance).getElement(), null);
      }
    }, dataFieldElements, dataFieldMetas, "ksessionSelectContainer");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.widget.KSessionSelectorViewImpl", "org/kie/workbench/common/widgets/client/widget/KSessionSelectorViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(KSessionSelectorViewImpl_HTMLLabelElement_warningLabel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "warningLabel");
    templateFieldsMap.put("kbaseSelectContainer", ElementWrapperWidget.getWidget(KSessionSelectorViewImpl_KieSelectElement_kbaseSelect(instance).getElement(), null));
    templateFieldsMap.put("ksessionSelectContainer", ElementWrapperWidget.getWidget(KSessionSelectorViewImpl_KieSelectElement_ksessionSelect(instance).getElement(), null));
    templateFieldsMap.put("warningLabel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(KSessionSelectorViewImpl_HTMLLabelElement_warningLabel(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKSessionSelectorViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((KSessionSelectorViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final KSessionSelectorViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static KieSelectElement KSessionSelectorViewImpl_KieSelectElement_kbaseSelect(KSessionSelectorViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.widget.KSessionSelectorViewImpl::kbaseSelect;
  }-*/;

  native static void KSessionSelectorViewImpl_KieSelectElement_kbaseSelect(KSessionSelectorViewImpl instance, KieSelectElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.widget.KSessionSelectorViewImpl::kbaseSelect = value;
  }-*/;

  native static HTMLLabelElement KSessionSelectorViewImpl_HTMLLabelElement_warningLabel(KSessionSelectorViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.widget.KSessionSelectorViewImpl::warningLabel;
  }-*/;

  native static void KSessionSelectorViewImpl_HTMLLabelElement_warningLabel(KSessionSelectorViewImpl instance, HTMLLabelElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.widget.KSessionSelectorViewImpl::warningLabel = value;
  }-*/;

  native static KieSelectElement KSessionSelectorViewImpl_KieSelectElement_ksessionSelect(KSessionSelectorViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.widget.KSessionSelectorViewImpl::ksessionSelect;
  }-*/;

  native static void KSessionSelectorViewImpl_KieSelectElement_ksessionSelect(KSessionSelectorViewImpl instance, KieSelectElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.widget.KSessionSelectorViewImpl::ksessionSelect = value;
  }-*/;
}