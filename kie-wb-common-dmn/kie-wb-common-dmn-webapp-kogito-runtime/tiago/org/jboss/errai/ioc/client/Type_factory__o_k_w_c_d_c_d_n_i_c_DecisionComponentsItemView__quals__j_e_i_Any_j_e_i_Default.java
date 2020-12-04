package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.HTMLParagraphElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.DMNShapeSet;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItem.View;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItemView;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.BuildCanvasShapeEvent;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.workbench.events.NotificationEvent;

public class Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentsItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionComponentsItemView> { public interface o_k_w_c_d_c_d_n_i_c_DecisionComponentsItemViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/docks/navigator/included/components/DecisionComponentsItemView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentsItemView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecisionComponentsItemView.class, "Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentsItemView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecisionComponentsItemView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DecisionComponentsItemView.\"] {\n  background: #FFF;\n  border: 1px solid #D7D7D7;\n  margin: 10px 0 0;\n  padding: 10px;\n  height: 55px;\n  -webkit-user-select: none;\n  -moz-user-select: none;\n  -ms-user-select: none;\n  user-select: none;\n}\n[data-i18n-prefix=\"DecisionComponentsItemView.\"] img {\n  width: 20px;\n  float: left;\n  height: 20px;\n  margin: 6px 15px 0 5px;\n}\n[data-i18n-prefix=\"DecisionComponentsItemView.\"] h5 {\n  font-weight: bold;\n  margin: 0 0 2px 0;\n  color: #444;\n  max-width: 300px;\n  white-space: nowrap;\n  overflow: hidden;\n  text-overflow: ellipsis;\n}\n[data-i18n-prefix=\"DecisionComponentsItemView.\"] p {\n  margin: 0;\n  color: #555;\n}\n\n");
  }

  public DecisionComponentsItemView createInstance(final ContextManager contextManager) {
    final HTMLHeadingElement _name_1 = (HTMLHeadingElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLHeadingElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named_1");
    final HTMLParagraphElement _file_2 = (HTMLParagraphElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLParagraphElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLImageElement _icon_0 = (HTMLImageElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLImageElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ClientTranslationService _clientTranslationService_9 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final HTMLDivElement _decisionComponentItem_7 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ShapeGlyphDragHandler _shapeGlyphDragHandler_5 = (ShapeGlyphDragHandler) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_g_ShapeGlyphDragHandler__quals__j_e_i_Any_j_e_i_Default");
    final Event<BuildCanvasShapeEvent> _buildCanvasShapeEvent_6 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { BuildCanvasShapeEvent.class }, new Annotation[] { });
    final DMNShapeSet _dmnShapeSet_3 = (DMNShapeSet) contextManager.getInstance("Type_factory__o_k_w_c_d_c_DMNShapeSet__quals__j_e_i_Any_j_e_i_Default");
    final SessionManager _sessionManager_4 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final Event<NotificationEvent> _notificationEvent_8 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NotificationEvent.class }, new Annotation[] { });
    final DecisionComponentsItemView instance = new DecisionComponentsItemView(_icon_0, _name_1, _file_2, _dmnShapeSet_3, _sessionManager_4, _shapeGlyphDragHandler_5, _buildCanvasShapeEvent_6, _decisionComponentItem_7, _notificationEvent_8, _clientTranslationService_9);
    registerDependentScopedReference(instance, _name_1);
    registerDependentScopedReference(instance, _file_2);
    registerDependentScopedReference(instance, _icon_0);
    registerDependentScopedReference(instance, _decisionComponentItem_7);
    registerDependentScopedReference(instance, _shapeGlyphDragHandler_5);
    registerDependentScopedReference(instance, _buildCanvasShapeEvent_6);
    registerDependentScopedReference(instance, _notificationEvent_8);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_d_n_i_c_DecisionComponentsItemViewTemplateResource templateForDecisionComponentsItemView = GWT.create(o_k_w_c_d_c_d_n_i_c_DecisionComponentsItemViewTemplateResource.class);
    Element parentElementForTemplateOfDecisionComponentsItemView = TemplateUtil.getRootTemplateParentElement(templateForDecisionComponentsItemView.getContents().getText(), "org/kie/workbench/common/dmn/client/docks/navigator/included/components/DecisionComponentsItemView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/docks/navigator/included/components/DecisionComponentsItemView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDecisionComponentsItemView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDecisionComponentsItemView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(4);
    dataFieldMetas.put("icon", new DataFieldMeta());
    dataFieldMetas.put("name", new DataFieldMeta());
    dataFieldMetas.put("decision-component-item", new DataFieldMeta());
    dataFieldMetas.put("file", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItemView", "org/kie/workbench/common/dmn/client/docks/navigator/included/components/DecisionComponentsItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsItemView_HTMLImageElement_icon(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "icon");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItemView", "org/kie/workbench/common/dmn/client/docks/navigator/included/components/DecisionComponentsItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsItemView_HTMLHeadingElement_name(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "name");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItemView", "org/kie/workbench/common/dmn/client/docks/navigator/included/components/DecisionComponentsItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsItemView_HTMLDivElement_decisionComponentItem(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "decision-component-item");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItemView", "org/kie/workbench/common/dmn/client/docks/navigator/included/components/DecisionComponentsItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsItemView_HTMLParagraphElement_file(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "file");
    templateFieldsMap.put("icon", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsItemView_HTMLImageElement_icon(instance))));
    templateFieldsMap.put("name", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsItemView_HTMLHeadingElement_name(instance))));
    templateFieldsMap.put("decision-component-item", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsItemView_HTMLDivElement_decisionComponentItem(instance))));
    templateFieldsMap.put("file", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsItemView_HTMLParagraphElement_file(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDecisionComponentsItemView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("decision-component-item"), new MouseDownHandler() {
      public void onMouseDown(MouseDownEvent event) {
        instance.decisionComponentItemMouseDown(event);
      }
    }, MouseDownEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DecisionComponentsItemView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DecisionComponentsItemView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLImageElement DecisionComponentsItemView_HTMLImageElement_icon(DecisionComponentsItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItemView::icon;
  }-*/;

  native static void DecisionComponentsItemView_HTMLImageElement_icon(DecisionComponentsItemView instance, HTMLImageElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItemView::icon = value;
  }-*/;

  native static HTMLDivElement DecisionComponentsItemView_HTMLDivElement_decisionComponentItem(DecisionComponentsItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItemView::decisionComponentItem;
  }-*/;

  native static void DecisionComponentsItemView_HTMLDivElement_decisionComponentItem(DecisionComponentsItemView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItemView::decisionComponentItem = value;
  }-*/;

  native static HTMLHeadingElement DecisionComponentsItemView_HTMLHeadingElement_name(DecisionComponentsItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItemView::name;
  }-*/;

  native static void DecisionComponentsItemView_HTMLHeadingElement_name(DecisionComponentsItemView instance, HTMLHeadingElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItemView::name = value;
  }-*/;

  native static HTMLParagraphElement DecisionComponentsItemView_HTMLParagraphElement_file(DecisionComponentsItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItemView::file;
  }-*/;

  native static void DecisionComponentsItemView_HTMLParagraphElement_file(DecisionComponentsItemView instance, HTMLParagraphElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItemView::file = value;
  }-*/;
}