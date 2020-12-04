package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
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
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.client.central.screen.PreferencesRootScreen.View;
import org.uberfire.ext.preferences.client.central.screen.PreferencesRootView;

public class Type_factory__o_u_e_p_c_c_s_PreferencesRootView__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferencesRootView> { public interface o_u_e_p_c_c_s_PreferencesRootViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/ext/preferences/client/central/screen/PreferencesRootView.html") public TextResource getContents(); }
  public Type_factory__o_u_e_p_c_c_s_PreferencesRootView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PreferencesRootView.class, "Type_factory__o_u_e_p_c_c_s_PreferencesRootView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreferencesRootView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, View.class, UberElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2017 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n.preferences-root {\n  height: 100%;\n}\n.preferences-root .preferences-col {\n  padding: 0;\n  height: 100%;\n}\n.preferences-root .preferences-navbar {\n  overflow: auto;\n}\n.preferences-root .preferences-editor {\n  height: calc(100% - 80px);\n  overflow: auto;\n}\n.preferences-root .preferences-actions {\n  height: 80px;\n}\n\n");
  }

  public PreferencesRootView createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final PreferencesRootView instance = new PreferencesRootView(_translationService_0);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    final Div PreferencesRootView_editor = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, PreferencesRootView_editor);
    PreferencesRootView_Div_editor(instance, PreferencesRootView_editor);
    final Div PreferencesRootView_navbar = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, PreferencesRootView_navbar);
    PreferencesRootView_Div_navbar(instance, PreferencesRootView_navbar);
    final Div PreferencesRootView_actions = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, PreferencesRootView_actions);
    PreferencesRootView_Div_actions(instance, PreferencesRootView_actions);
    o_u_e_p_c_c_s_PreferencesRootViewTemplateResource templateForPreferencesRootView = GWT.create(o_u_e_p_c_c_s_PreferencesRootViewTemplateResource.class);
    Element parentElementForTemplateOfPreferencesRootView = TemplateUtil.getRootTemplateParentElement(templateForPreferencesRootView.getContents().getText(), "org/uberfire/ext/preferences/client/central/screen/PreferencesRootView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/preferences/client/central/screen/PreferencesRootView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPreferencesRootView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPreferencesRootView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("preferences-navbar", new DataFieldMeta());
    dataFieldMetas.put("preferences-editor", new DataFieldMeta());
    dataFieldMetas.put("preferences-actions", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.central.screen.PreferencesRootView", "org/uberfire/ext/preferences/client/central/screen/PreferencesRootView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(PreferencesRootView_Div_navbar(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "preferences-navbar");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.central.screen.PreferencesRootView", "org/uberfire/ext/preferences/client/central/screen/PreferencesRootView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(PreferencesRootView_Div_editor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "preferences-editor");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.central.screen.PreferencesRootView", "org/uberfire/ext/preferences/client/central/screen/PreferencesRootView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(PreferencesRootView_Div_actions(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "preferences-actions");
    templateFieldsMap.put("preferences-navbar", ElementWrapperWidget.getWidget(TemplateUtil.asElement(PreferencesRootView_Div_navbar(instance))));
    templateFieldsMap.put("preferences-editor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(PreferencesRootView_Div_editor(instance))));
    templateFieldsMap.put("preferences-actions", ElementWrapperWidget.getWidget(TemplateUtil.asElement(PreferencesRootView_Div_actions(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPreferencesRootView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((PreferencesRootView) instance, contextManager);
  }

  public void destroyInstanceHelper(final PreferencesRootView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div PreferencesRootView_Div_actions(PreferencesRootView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.screen.PreferencesRootView::actions;
  }-*/;

  native static void PreferencesRootView_Div_actions(PreferencesRootView instance, Div value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.screen.PreferencesRootView::actions = value;
  }-*/;

  native static Div PreferencesRootView_Div_editor(PreferencesRootView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.screen.PreferencesRootView::editor;
  }-*/;

  native static void PreferencesRootView_Div_editor(PreferencesRootView instance, Div value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.screen.PreferencesRootView::editor = value;
  }-*/;

  native static Div PreferencesRootView_Div_navbar(PreferencesRootView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.screen.PreferencesRootView::navbar;
  }-*/;

  native static void PreferencesRootView_Div_navbar(PreferencesRootView instance, Div value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.screen.PreferencesRootView::navbar = value;
  }-*/;
}