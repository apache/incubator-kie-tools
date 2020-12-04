package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Span;
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
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitionsProducer;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelector;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView.Presenter;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanelContainer;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;

public class Type_factory__o_k_w_c_d_c_e_e_ExpressionEditorViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ExpressionEditorViewImpl> { public interface o_k_w_c_d_c_e_e_ExpressionEditorViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/expressions/ExpressionEditorViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_e_ExpressionEditorViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ExpressionEditorViewImpl.class, "Type_factory__o_k_w_c_d_c_e_e_ExpressionEditorViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ExpressionEditorViewImpl.class, Object.class, ExpressionEditorView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, UberElement.class, HasPresenter.class, SessionAware.class, RequiresResize.class, ProvidesResize.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2018 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n.kie-dmn-container {\n  display: flex;\n  flex-direction: column;\n  padding: 10px;\n  width: 100%;\n  height: 100%;\n}\n.kie-dmn-return-to-link {\n  height: 32px;\n  display: block;\n}\n.kie-dmn-return-to-link i {\n  padding: 0 9px 0 7px;\n}\n.kie-dmn-expression-type {\n  height: 32px;\n  display: block;\n}\n.kie-dmn-expression-type #name {\n  font-size: large;\n}\n.kie-dmn-expression-type #type {\n  font-size: larger;\n  font-style: italic;\n  color: grey;\n}\n.kie-dmn-expression-editor {\n  display: flex;\n  width: 100%;\n  height: calc(100% - 64px);\n  top: 64px px;\n}\n\n");
  }

  public ExpressionEditorViewImpl createInstance(final ContextManager contextManager) {
    final DMNGridPanelContainer _gridPanelContainer_3 = (DMNGridPanelContainer) contextManager.getInstance("Type_factory__o_k_w_c_d_c_w_p_DMNGridPanelContainer__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final DefaultCanvasCommandFactory _canvasCommandFactory_8 = (DefaultCanvasCommandFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_c_f_DefaultCanvasCommandFactory__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final Supplier<ExpressionEditorDefinitions> _expressionEditorDefinitionsSupplier_9 = (ExpressionEditorDefinitionsProducer) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final Span _expressionType_2 = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TranslationService _translationService_4 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final Event<DomainObjectSelectionEvent> _domainObjectSelectionEvent_11 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { DomainObjectSelectionEvent.class }, new Annotation[] { });
    final SessionManager _sessionManager_6 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final Anchor _returnToLink_0 = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final Presenter _listSelector_5 = (ListSelector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelector__quals__j_e_i_Any_j_e_i_Default");
    final Span _expressionName_1 = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final SessionCommandManager<AbstractCanvasHandler> _sessionCommandManager_7 = (ApplicationCommandManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    final Event<RefreshFormPropertiesEvent> _refreshFormPropertiesEvent_10 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RefreshFormPropertiesEvent.class }, new Annotation[] { });
    final ExpressionEditorViewImpl instance = new ExpressionEditorViewImpl(_returnToLink_0, _expressionName_1, _expressionType_2, _gridPanelContainer_3, _translationService_4, _listSelector_5, _sessionManager_6, _sessionCommandManager_7, _canvasCommandFactory_8, _expressionEditorDefinitionsSupplier_9, _refreshFormPropertiesEvent_10, _domainObjectSelectionEvent_11);
    registerDependentScopedReference(instance, _gridPanelContainer_3);
    registerDependentScopedReference(instance, _expressionType_2);
    registerDependentScopedReference(instance, _translationService_4);
    registerDependentScopedReference(instance, _domainObjectSelectionEvent_11);
    registerDependentScopedReference(instance, _returnToLink_0);
    registerDependentScopedReference(instance, _listSelector_5);
    registerDependentScopedReference(instance, _expressionName_1);
    registerDependentScopedReference(instance, _refreshFormPropertiesEvent_10);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_e_ExpressionEditorViewImplTemplateResource templateForExpressionEditorViewImpl = GWT.create(o_k_w_c_d_c_e_e_ExpressionEditorViewImplTemplateResource.class);
    Element parentElementForTemplateOfExpressionEditorViewImpl = TemplateUtil.getRootTemplateParentElement(templateForExpressionEditorViewImpl.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/expressions/ExpressionEditorViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/expressions/ExpressionEditorViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfExpressionEditorViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfExpressionEditorViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(4);
    dataFieldMetas.put("returnToLink", new DataFieldMeta());
    dataFieldMetas.put("expressionName", new DataFieldMeta());
    dataFieldMetas.put("expressionType", new DataFieldMeta());
    dataFieldMetas.put("dmn-table", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl", "org/kie/workbench/common/dmn/client/editors/expressions/ExpressionEditorViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExpressionEditorViewImpl_Anchor_returnToLink(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "returnToLink");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl", "org/kie/workbench/common/dmn/client/editors/expressions/ExpressionEditorViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExpressionEditorViewImpl_Span_expressionName(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "expressionName");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl", "org/kie/workbench/common/dmn/client/editors/expressions/ExpressionEditorViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExpressionEditorViewImpl_Span_expressionType(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "expressionType");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl", "org/kie/workbench/common/dmn/client/editors/expressions/ExpressionEditorViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ExpressionEditorViewImpl_DMNGridPanelContainer_gridPanelContainer(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "dmn-table");
    templateFieldsMap.put("returnToLink", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExpressionEditorViewImpl_Anchor_returnToLink(instance))));
    templateFieldsMap.put("expressionName", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExpressionEditorViewImpl_Span_expressionName(instance))));
    templateFieldsMap.put("expressionType", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExpressionEditorViewImpl_Span_expressionType(instance))));
    templateFieldsMap.put("dmn-table", ExpressionEditorViewImpl_DMNGridPanelContainer_gridPanelContainer(instance).asWidget());
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfExpressionEditorViewImpl), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("returnToLink"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        ExpressionEditorViewImpl_onClickReturnToLink_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ExpressionEditorViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ExpressionEditorViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Span ExpressionEditorViewImpl_Span_expressionType(ExpressionEditorViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl::expressionType;
  }-*/;

  native static void ExpressionEditorViewImpl_Span_expressionType(ExpressionEditorViewImpl instance, Span value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl::expressionType = value;
  }-*/;

  native static Anchor ExpressionEditorViewImpl_Anchor_returnToLink(ExpressionEditorViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl::returnToLink;
  }-*/;

  native static void ExpressionEditorViewImpl_Anchor_returnToLink(ExpressionEditorViewImpl instance, Anchor value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl::returnToLink = value;
  }-*/;

  native static DMNGridPanelContainer ExpressionEditorViewImpl_DMNGridPanelContainer_gridPanelContainer(ExpressionEditorViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl::gridPanelContainer;
  }-*/;

  native static void ExpressionEditorViewImpl_DMNGridPanelContainer_gridPanelContainer(ExpressionEditorViewImpl instance, DMNGridPanelContainer value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl::gridPanelContainer = value;
  }-*/;

  native static Span ExpressionEditorViewImpl_Span_expressionName(ExpressionEditorViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl::expressionName;
  }-*/;

  native static void ExpressionEditorViewImpl_Span_expressionName(ExpressionEditorViewImpl instance, Span value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl::expressionName = value;
  }-*/;

  public native static void ExpressionEditorViewImpl_onClickReturnToLink_ClickEvent(ExpressionEditorViewImpl instance, ClickEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl::onClickReturnToLink(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;
}