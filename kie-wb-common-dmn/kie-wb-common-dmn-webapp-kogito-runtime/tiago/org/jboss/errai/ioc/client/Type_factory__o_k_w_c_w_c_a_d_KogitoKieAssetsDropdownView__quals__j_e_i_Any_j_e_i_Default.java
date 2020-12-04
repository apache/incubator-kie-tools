package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdown.View;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownView;
import org.kie.workbench.common.widgets.client.assets.dropdown.KogitoKieAssetsDropdownView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_w_c_a_d_KogitoKieAssetsDropdownView__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoKieAssetsDropdownView> { public interface o_k_w_c_w_c_a_d_KogitoKieAssetsDropdownViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/widgets/client/assets/dropdown/KogitoKieAssetsDropdownView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_w_c_a_d_KogitoKieAssetsDropdownView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KogitoKieAssetsDropdownView.class, "Type_factory__o_k_w_c_w_c_a_d_KogitoKieAssetsDropdownView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KogitoKieAssetsDropdownView.class, KieAssetsDropdownView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class, org.kie.workbench.common.widgets.client.assets.dropdown.KogitoKieAssetsDropdown.View.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"KogitoKieAssetsDropdownView.\"] label,\n[data-i18n-prefix=\"KogitoKieAssetsDropdownView.\"] .bootstrap-select,\n[data-i18n-prefix=\"KogitoKieAssetsDropdownView.\"] .bootstrap-select:not([class*=col-]):not([class*=form-control]):not(.input-group-btn) {\n  width: 100%;\n  margin: 0;\n}\n[data-i18n-prefix=\"KogitoKieAssetsDropdownView.\"] .bootstrap-select .dropdown-menu .text {\n  font-weight: 600;\n}\n[data-i18n-prefix=\"KogitoKieAssetsDropdownView.\"] .bootstrap-select .dropdown-menu .text-muted,\n[data-i18n-prefix=\"KogitoKieAssetsDropdownView.\"] .bootstrap-select .dropdown-menu .disabled .text {\n  font-weight: normal;\n}\n\n");
  }

  public KogitoKieAssetsDropdownView createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_2 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final HTMLOptionElement _htmlOptionElement_1 = (HTMLOptionElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLOptionElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLSelectElement _nativeSelect_0 = (HTMLSelectElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLSelectElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final KogitoKieAssetsDropdownView instance = new KogitoKieAssetsDropdownView(_nativeSelect_0, _htmlOptionElement_1, _translationService_2);
    registerDependentScopedReference(instance, _translationService_2);
    registerDependentScopedReference(instance, _htmlOptionElement_1);
    registerDependentScopedReference(instance, _nativeSelect_0);
    setIncompleteInstance(instance);
    o_k_w_c_w_c_a_d_KogitoKieAssetsDropdownViewTemplateResource templateForKogitoKieAssetsDropdownView = GWT.create(o_k_w_c_w_c_a_d_KogitoKieAssetsDropdownViewTemplateResource.class);
    Element parentElementForTemplateOfKogitoKieAssetsDropdownView = TemplateUtil.getRootTemplateParentElement(templateForKogitoKieAssetsDropdownView.getContents().getText(), "org/kie/workbench/common/widgets/client/assets/dropdown/KogitoKieAssetsDropdownView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/widgets/client/assets/dropdown/KogitoKieAssetsDropdownView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKogitoKieAssetsDropdownView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKogitoKieAssetsDropdownView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("native-select", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.assets.dropdown.KogitoKieAssetsDropdownView", "org/kie/workbench/common/widgets/client/assets/dropdown/KogitoKieAssetsDropdownView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(KieAssetsDropdownView_HTMLSelectElement_nativeSelect(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "native-select");
    templateFieldsMap.put("native-select", ElementWrapperWidget.getWidget(TemplateUtil.asElement(KieAssetsDropdownView_HTMLSelectElement_nativeSelect(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKogitoKieAssetsDropdownView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((KogitoKieAssetsDropdownView) instance, contextManager);
  }

  public void destroyInstanceHelper(final KogitoKieAssetsDropdownView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final KogitoKieAssetsDropdownView instance) {
    instance.init();
  }

  native static HTMLSelectElement KieAssetsDropdownView_HTMLSelectElement_nativeSelect(KieAssetsDropdownView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownView::nativeSelect;
  }-*/;

  native static void KieAssetsDropdownView_HTMLSelectElement_nativeSelect(KieAssetsDropdownView instance, HTMLSelectElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownView::nativeSelect = value;
  }-*/;
}