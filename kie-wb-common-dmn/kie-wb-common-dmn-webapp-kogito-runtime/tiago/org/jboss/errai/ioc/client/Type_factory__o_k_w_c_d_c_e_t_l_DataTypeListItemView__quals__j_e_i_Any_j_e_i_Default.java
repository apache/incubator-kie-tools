package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
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
import org.kie.workbench.common.dmn.client.api.ReadOnlyProviderImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeListItemView> { public interface o_k_w_c_d_c_e_t_l_DataTypeListItemViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListItemView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListItemView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeListItemView.class, "Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListItemView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeListItemView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2018 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DataTypeListItemView.\"].list-group-item {\n  height: 70px;\n  background: none;\n  border: none;\n  padding-left: 25px;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] [data-type-field=\"arrow-button\"] {\n  cursor: pointer;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .data-type-icon {\n  border: 2px solid #39a5dc;\n  font-size: 1.3em;\n  height: 30px;\n  line-height: 30px;\n  width: 30px;\n  border-radius: 50%;\n  padding-left: 4px;\n  margin-left: 20px;\n  position: relative;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .data-type-icon:before {\n  top: -1px;\n  color: #adadad;\n  position: absolute;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .fa-caret-right:before,\n[data-i18n-prefix=\"DataTypeListItemView.\"] .fa-caret-down:before {\n  font-size: 1.4em;\n  top: 15px;\n  position: absolute;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .list-view-pf-actions button:hover,\n[data-i18n-prefix=\"DataTypeListItemView.\"] .list-view-pf-actions .dropdown-kebab-pf .btn.btn-link.dropdown-toggle {\n  outline: none;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .list-view-pf-main-info {\n  text-align: left;\n  position: absolute;\n  width: calc(100% - 210px);\n  height: 50px;\n  padding: 0;\n  left: 25px;\n  top: 8px;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .list-view-pf-main-info .list-view-pf-body {\n  padding-left: 24px;\n  position: relative;\n  white-space: nowrap;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .list-view-pf-main-info .list-view-pf-body > div {\n  padding-top: 4px;\n  display: inline-block;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .list-view-pf-main-info [data-type-field=\"arrow-button\"] {\n  position: absolute;\n  left: 3px;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .list-view-pf-main-info [data-type-field=\"type\"] {\n  display: inline-block;\n  padding-right: 30px;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .collection-component {\n  font-size: 11px;\n  padding: 4px 30px 0;\n  width: 180px;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .collection-component > div {\n  margin: 0 5px;\n  display: inline-block;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .collection-component span + div {\n  margin-top: -4px;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .constraint-component {\n  margin-top: -8px;\n  padding-top: 2px;\n  height: 40px;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .list-view-pf-actions {\n  text-align: right;\n  position: absolute;\n  padding: 7px;\n  margin: 0;\n  width: 160px;\n  height: 50px;\n  right: 15px;\n  top: 8px;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .list-view-pf-actions button.btn {\n  box-shadow: none;\n  background: none;\n  font-size: 17px;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .name-text {\n  font-weight: bold;\n  padding: 0 5px;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .name-input {\n  margin-right: 5px;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .data-type-label {\n  display: block;\n  font-weight: initial;\n  opacity: .75;\n  padding-bottom: 3px;\n  margin-top: -10px;\n  font-weight: 600;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .data-type-label:before {\n  content: \"* \";\n  color: #A93C3C;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] [data-type-field=\"save-button\"],\n[data-i18n-prefix=\"DataTypeListItemView.\"] [data-type-field=\"close-button\"] {\n  margin-top: 2px;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] [data-type-field=\"save-button\"] .fa {\n  color: #0088CE;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] ul.dropdown-menu {\n  padding: 3px 0;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .dropdown-kebab-container {\n  float: right;\n  margin: 0;\n  width: 20px;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .kie-data-type-list {\n  display: inline;\n  margin-right: 5px;\n  font-size: 13px;\n  color: #555;\n  margin-left: 10px;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] .kie-data-type-list .fa.fa-th-list {\n  margin-right: 5px;\n}\n[data-i18n-prefix=\"DataTypeListItemView.\"] button {\n  outline: none;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] div[data-row-uuid] {\n  border: 1px solid #e2e4e3;\n  height: 71px;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] div[data-row-uuid]:hover {\n  background: #eff8ff;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] div[data-row-uuid].read-only {\n  cursor: not-allowed;\n  pointer-events: none;\n  filter: brightness(0.98);\n  opacity: .75;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] div[data-row-uuid].read-only.kie-dnd-hover {\n  background-color: #FFF;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] div[data-row-uuid].focused-data-type {\n  background: #eff8ff;\n  height: 72px;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] div[data-row-uuid].focused-data-type .list-group-item .constraint-component {\n  padding-top: 5px;\n  white-space: nowrap;\n  overflow: hidden;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] div[data-row-uuid].focused-data-type .list-group-item .collection-component {\n  padding-top: 10px;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] div[data-row-uuid].key-highlight {\n  background: #E5EEF5;\n}\n@media (max-width: 1470px) {\n  [data-i18n-prefix=\"DataTypeListItemView.\"] .collection-component > div {\n    margin: 0;\n  }\n  [data-i18n-prefix=\"DNDListComponentView.\"] div[data-row-uuid].focused-data-type .list-group-item .constraint-component [data-field=\"constraints-tooltip\"] {\n    display: none;\n  }\n  [data-i18n-prefix=\"DNDListComponentView.\"] div[data-row-uuid].focused-data-type .collection-component {\n    padding: 4px 0 0;\n    width: 70px;\n  }\n  [data-i18n-prefix=\"DNDListComponentView.\"] div[data-row-uuid].focused-data-type .collection-component [data-i18n-key=\"List\"] {\n    display: none;\n  }\n  [data-i18n-prefix=\"DataTypeConstraintView.\"] [data-field=\"constraints-anchor-container\"] {\n    font-size: 10px;\n  }\n}\n@media (max-width: 1350px) {\n  [data-i18n-prefix=\"DataTypeListItemView.\"] .list-view-pf-main-info .list-view-pf-body .name-input {\n    width: 110px;\n  }\n  [data-i18n-prefix=\"DataTypeListItemView.\"] .list-view-pf-main-info .list-view-pf-body .bootstrap-select {\n    width: 130px;\n  }\n  [data-i18n-prefix=\"DataTypeSearchBarView.\"] .kie-data-type-search {\n    width: 250px;\n  }\n}\n@media (max-width: 1200px) {\n  [data-i18n-prefix=\"DataTypeListItemView.\"] .list-view-pf-main-info .list-view-pf-body [data-field=\"constraints-anchor-text\"] {\n    display: none;\n  }\n  [data-i18n-prefix=\"DataTypeListView.\"] .data-type-button .expand-collapse {\n    display: none;\n  }\n}\n\n");
  }

  public DataTypeListItemView createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_1 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final ReadOnlyProvider _readOnlyProvider_2 = (ReadOnlyProviderImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final HTMLDivElement _view_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DataTypeListItemView instance = new DataTypeListItemView(_view_0, _translationService_1, _readOnlyProvider_2);
    registerDependentScopedReference(instance, _translationService_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_l_DataTypeListItemViewTemplateResource templateForDataTypeListItemView = GWT.create(o_k_w_c_d_c_e_t_l_DataTypeListItemViewTemplateResource.class);
    Element parentElementForTemplateOfDataTypeListItemView = TemplateUtil.getRootTemplateParentElement(templateForDataTypeListItemView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListItemView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListItemView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeListItemView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeListItemView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("view", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListItemView_HTMLDivElement_view(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "view");
    templateFieldsMap.put("view", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListItemView_HTMLDivElement_view(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeListItemView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DataTypeListItemView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DataTypeListItemView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLDivElement DataTypeListItemView_HTMLDivElement_view(DataTypeListItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView::view;
  }-*/;

  native static void DataTypeListItemView_HTMLDivElement_view(DataTypeListItemView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView::view = value;
  }-*/;
}