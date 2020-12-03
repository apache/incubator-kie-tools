package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChanger;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter.View;

public class Type_factory__o_k_w_c_d_c_e_d_DRDNameChangerView__quals__j_e_i_Any_j_e_i_Default extends Factory<DRDNameChangerView> { public interface o_k_w_c_d_c_e_d_DRDNameChangerViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/drd/DRDNameChangerView.html") public TextResource getContents(); }
  private class Type_factory__o_k_w_c_d_c_e_d_DRDNameChangerView__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DRDNameChangerView implements Proxy<DRDNameChangerView> {
    private final ProxyHelper<DRDNameChangerView> proxyHelper = new ProxyHelperImpl<DRDNameChangerView>("Type_factory__o_k_w_c_d_c_e_d_DRDNameChangerView__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_d_DRDNameChangerView__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null, null, null, null, null);
    }

    public void initProxyProperties(final DRDNameChangerView instance) {

    }

    public DRDNameChangerView asBeanType() {
      return this;
    }

    public void setInstance(final DRDNameChangerView instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public void setSessionPresenterView(View sessionPresenterView) {
      if (proxyHelper != null) {
        final DRDNameChangerView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setSessionPresenterView(sessionPresenterView);
      } else {
        super.setSessionPresenterView(sessionPresenterView);
      }
    }

    @Override public void showDRDNameChanger() {
      if (proxyHelper != null) {
        final DRDNameChangerView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.showDRDNameChanger();
      } else {
        super.showDRDNameChanger();
      }
    }

    @Override public void hideDRDNameChanger() {
      if (proxyHelper != null) {
        final DRDNameChangerView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.hideDRDNameChanger();
      } else {
        super.hideDRDNameChanger();
      }
    }

    @Override public HTMLElement getElement() {
      if (proxyHelper != null) {
        final DRDNameChangerView proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = proxiedInstance.getElement();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DRDNameChangerView proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_d_DRDNameChangerView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DRDNameChangerView.class, "Type_factory__o_k_w_c_d_c_e_d_DRDNameChangerView__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DRDNameChangerView.class, Object.class, DRDNameChanger.class, IsElement.class, org.jboss.errai.common.client.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected", new AbstractCDIEventCallback<DMNDiagramSelected>() {
      public void fireEvent(final DMNDiagramSelected event) {
        final DRDNameChangerView instance = Factory.maybeUnwrapProxy((DRDNameChangerView) context.getInstance("Type_factory__o_k_w_c_d_c_e_d_DRDNameChangerView__quals__j_e_i_Any_j_e_i_Default"));
        DRDNameChangerView_onSettingCurrentDMNDiagramElement_DMNDiagramSelected(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected []";
      }
    });
    StyleInjector.inject("/*\n * Copyright 2020 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DRDNameChangerView.\"] .return-to-drg-link {\n  margin: 5px;\n}\n[data-i18n-prefix=\"DRDNameChangerView.\"] .return-to-drg-link i {\n  padding: 0 9px 0 7px;\n}\n[data-i18n-prefix=\"DRDNameChangerView.\"] .drd-name-editor {\n  font-size: larger;\n  margin-left: 10px;\n  margin-bottom: 5px;\n  cursor: pointer;\n}\n[data-i18n-prefix=\"DRDNameChangerView.\"] .drd-name-editor .drd-name {\n  font-weight: 600;\n}\n[data-i18n-prefix=\"DRDNameChangerView.\"] .drd-name-editor .drd-description {\n  margin-left: 5px;\n  font-style: italic;\n  color: gray;\n}\n[data-i18n-prefix=\"DRDNameChangerView.\"] .drd-name-editor .drd-name-input {\n  outline: none;\n  border-color: #9adcff;\n  border-radius: 2px;\n}\n\n");
  }

  public DRDNameChangerView createInstance(final ContextManager contextManager) {
    final Span _drdName_5 = (Span) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_h_Span__quals__j_e_i_Any_j_e_i_Default");
    final DivElement _editMode_3 = (DivElement) contextManager.getInstance("ExtensionProvided_factory__c_g_g_d_c_DivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final Event<DMNDiagramSelected> _selectedEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { DMNDiagramSelected.class }, new Annotation[] { });
    final HTMLAnchorElement _returnToDRG_4 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final InputElement _drdNameInput_6 = (InputElement) contextManager.getInstance("ExtensionProvided_factory__c_g_g_d_c_InputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DMNDiagramsSession _dmnDiagramsSession_0 = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    final DivElement _viewMode_2 = (DivElement) contextManager.getInstance("ExtensionProvided_factory__c_g_g_d_c_DivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DRDNameChangerView instance = new DRDNameChangerView(_dmnDiagramsSession_0, _selectedEvent_1, _viewMode_2, _editMode_3, _returnToDRG_4, _drdName_5, _drdNameInput_6);
    registerDependentScopedReference(instance, _drdName_5);
    registerDependentScopedReference(instance, _editMode_3);
    registerDependentScopedReference(instance, _selectedEvent_1);
    registerDependentScopedReference(instance, _returnToDRG_4);
    registerDependentScopedReference(instance, _drdNameInput_6);
    registerDependentScopedReference(instance, _viewMode_2);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_d_DRDNameChangerViewTemplateResource templateForDRDNameChangerView = GWT.create(o_k_w_c_d_c_e_d_DRDNameChangerViewTemplateResource.class);
    Element parentElementForTemplateOfDRDNameChangerView = TemplateUtil.getRootTemplateParentElement(templateForDRDNameChangerView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/drd/DRDNameChangerView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/drd/DRDNameChangerView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDRDNameChangerView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDRDNameChangerView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(5);
    dataFieldMetas.put("viewMode", new DataFieldMeta());
    dataFieldMetas.put("editMode", new DataFieldMeta());
    dataFieldMetas.put("returnToDRG", new DataFieldMeta());
    dataFieldMetas.put("drdName", new DataFieldMeta());
    dataFieldMetas.put("drdNameInput", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView", "org/kie/workbench/common/dmn/client/editors/drd/DRDNameChangerView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(DRDNameChangerView_DivElement_viewMode(instance));
      }
    }, dataFieldElements, dataFieldMetas, "viewMode");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView", "org/kie/workbench/common/dmn/client/editors/drd/DRDNameChangerView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(DRDNameChangerView_DivElement_editMode(instance));
      }
    }, dataFieldElements, dataFieldMetas, "editMode");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView", "org/kie/workbench/common/dmn/client/editors/drd/DRDNameChangerView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DRDNameChangerView_HTMLAnchorElement_returnToDRG(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "returnToDRG");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView", "org/kie/workbench/common/dmn/client/editors/drd/DRDNameChangerView.html", new Supplier<Widget>() {
      public Widget get() {
        return DRDNameChangerView_Span_drdName(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "drdName");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView", "org/kie/workbench/common/dmn/client/editors/drd/DRDNameChangerView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(DRDNameChangerView_InputElement_drdNameInput(instance));
      }
    }, dataFieldElements, dataFieldMetas, "drdNameInput");
    templateFieldsMap.put("viewMode", ElementWrapperWidget.getWidget(DRDNameChangerView_DivElement_viewMode(instance)));
    templateFieldsMap.put("editMode", ElementWrapperWidget.getWidget(DRDNameChangerView_DivElement_editMode(instance)));
    templateFieldsMap.put("returnToDRG", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DRDNameChangerView_HTMLAnchorElement_returnToDRG(instance))));
    templateFieldsMap.put("drdName", DRDNameChangerView_Span_drdName(instance).asWidget());
    templateFieldsMap.put("drdNameInput", ElementWrapperWidget.getWidget(DRDNameChangerView_InputElement_drdNameInput(instance)));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDRDNameChangerView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("returnToDRG"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        DRDNameChangerView_onClickReturnToDRG_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("drdNameInput"), new KeyDownHandler() {
      public void onKeyDown(KeyDownEvent event) {
        DRDNameChangerView_onInputTextKeyPress_KeyDownEvent(instance, event);
      }
    }, KeyDownEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("viewMode"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        DRDNameChangerView_enableEdit_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("drdNameInput"), new BlurHandler() {
      public void onBlur(BlurEvent event) {
        DRDNameChangerView_onInputTextBlur_BlurEvent(instance, event);
      }
    }, BlurEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DRDNameChangerView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DRDNameChangerView instance, final ContextManager contextManager) {
    ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(DRDNameChangerView_DivElement_viewMode(instance)));
    ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(DRDNameChangerView_DivElement_editMode(instance)));
    ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(DRDNameChangerView_InputElement_drdNameInput(instance)));
    TemplateUtil.cleanupTemplated(instance);
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_d_DRDNameChangerView__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView ([org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession, javax.enterprise.event.Event, com.google.gwt.dom.client.DivElement, com.google.gwt.dom.client.DivElement, elemental2.dom.HTMLAnchorElement, org.gwtbootstrap3.client.ui.html.Span, com.google.gwt.dom.client.InputElement])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DRDNameChangerView> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static InputElement DRDNameChangerView_InputElement_drdNameInput(DRDNameChangerView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView::drdNameInput;
  }-*/;

  native static void DRDNameChangerView_InputElement_drdNameInput(DRDNameChangerView instance, InputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView::drdNameInput = value;
  }-*/;

  native static DivElement DRDNameChangerView_DivElement_editMode(DRDNameChangerView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView::editMode;
  }-*/;

  native static void DRDNameChangerView_DivElement_editMode(DRDNameChangerView instance, DivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView::editMode = value;
  }-*/;

  native static DivElement DRDNameChangerView_DivElement_viewMode(DRDNameChangerView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView::viewMode;
  }-*/;

  native static void DRDNameChangerView_DivElement_viewMode(DRDNameChangerView instance, DivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView::viewMode = value;
  }-*/;

  native static HTMLAnchorElement DRDNameChangerView_HTMLAnchorElement_returnToDRG(DRDNameChangerView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView::returnToDRG;
  }-*/;

  native static void DRDNameChangerView_HTMLAnchorElement_returnToDRG(DRDNameChangerView instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView::returnToDRG = value;
  }-*/;

  native static Span DRDNameChangerView_Span_drdName(DRDNameChangerView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView::drdName;
  }-*/;

  native static void DRDNameChangerView_Span_drdName(DRDNameChangerView instance, Span value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView::drdName = value;
  }-*/;

  public native static void DRDNameChangerView_onInputTextBlur_BlurEvent(DRDNameChangerView instance, BlurEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView::onInputTextBlur(Lcom/google/gwt/event/dom/client/BlurEvent;)(a0);
  }-*/;

  public native static void DRDNameChangerView_onClickReturnToDRG_ClickEvent(DRDNameChangerView instance, ClickEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView::onClickReturnToDRG(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void DRDNameChangerView_onInputTextKeyPress_KeyDownEvent(DRDNameChangerView instance, KeyDownEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView::onInputTextKeyPress(Lcom/google/gwt/event/dom/client/KeyDownEvent;)(a0);
  }-*/;

  public native static void DRDNameChangerView_onSettingCurrentDMNDiagramElement_DMNDiagramSelected(DRDNameChangerView instance, DMNDiagramSelected a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView::onSettingCurrentDMNDiagramElement(Lorg/kie/workbench/common/dmn/client/docks/navigator/drds/DMNDiagramSelected;)(a0);
  }-*/;

  public native static void DRDNameChangerView_enableEdit_ClickEvent(DRDNameChangerView instance, ClickEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView::enableEdit(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;
}