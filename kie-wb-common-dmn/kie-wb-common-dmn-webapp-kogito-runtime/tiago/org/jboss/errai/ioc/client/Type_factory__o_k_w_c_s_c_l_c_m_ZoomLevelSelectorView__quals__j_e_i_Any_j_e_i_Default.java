package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelector.View;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorItem;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberView;

public class Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelectorView__quals__j_e_i_Any_j_e_i_Default extends Factory<ZoomLevelSelectorView> { public interface o_k_w_c_s_c_l_c_m_ZoomLevelSelectorViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorView.html") public TextResource getContents();
  @Source("org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelectorView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ZoomLevelSelectorView.class, "Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelectorView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ZoomLevelSelectorView.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, View.class, UberView.class, HasPresenter.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_s_c_l_c_m_ZoomLevelSelectorViewTemplateResource) GWT.create(o_k_w_c_s_c_l_c_m_ZoomLevelSelectorViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public ZoomLevelSelectorView createInstance(final ContextManager contextManager) {
    final ZoomLevelSelectorView instance = new ZoomLevelSelectorView();
    setIncompleteInstance(instance);
    final Div ZoomLevelSelectorView_dropDownPanel = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ZoomLevelSelectorView_dropDownPanel);
    ZoomLevelSelectorView_Div_dropDownPanel(instance, ZoomLevelSelectorView_dropDownPanel);
    final Button ZoomLevelSelectorView_decreaseButton = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ZoomLevelSelectorView_decreaseButton);
    ZoomLevelSelectorView_Button_decreaseButton(instance, ZoomLevelSelectorView_decreaseButton);
    final Div ZoomLevelSelectorView_dropDownPanelGroup = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ZoomLevelSelectorView_dropDownPanelGroup);
    ZoomLevelSelectorView_Div_dropDownPanelGroup(instance, ZoomLevelSelectorView_dropDownPanelGroup);
    final ClientTranslationService ZoomLevelSelectorView_translationService = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    ZoomLevelSelectorView_ClientTranslationService_translationService(instance, ZoomLevelSelectorView_translationService);
    final Button ZoomLevelSelectorView_increaseButton = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ZoomLevelSelectorView_increaseButton);
    ZoomLevelSelectorView_Button_increaseButton(instance, ZoomLevelSelectorView_increaseButton);
    final Button ZoomLevelSelectorView_dropDownButton = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ZoomLevelSelectorView_dropDownButton);
    ZoomLevelSelectorView_Button_dropDownButton(instance, ZoomLevelSelectorView_dropDownButton);
    final UnorderedList ZoomLevelSelectorView_dropDownMenu = (UnorderedList) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_UnorderedList__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ZoomLevelSelectorView_dropDownMenu);
    ZoomLevelSelectorView_UnorderedList_dropDownMenu(instance, ZoomLevelSelectorView_dropDownMenu);
    final ManagedInstance ZoomLevelSelectorView_items = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ZoomLevelSelectorItem.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    registerDependentScopedReference(instance, ZoomLevelSelectorView_items);
    ZoomLevelSelectorView_ManagedInstance_items(instance, ZoomLevelSelectorView_items);
    final Button ZoomLevelSelectorView_resetButton = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ZoomLevelSelectorView_resetButton);
    ZoomLevelSelectorView_Button_resetButton(instance, ZoomLevelSelectorView_resetButton);
    final Span ZoomLevelSelectorView_dropDownText = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ZoomLevelSelectorView_dropDownText);
    ZoomLevelSelectorView_Span_dropDownText(instance, ZoomLevelSelectorView_dropDownText);
    o_k_w_c_s_c_l_c_m_ZoomLevelSelectorViewTemplateResource templateForZoomLevelSelectorView = GWT.create(o_k_w_c_s_c_l_c_m_ZoomLevelSelectorViewTemplateResource.class);
    Element parentElementForTemplateOfZoomLevelSelectorView = TemplateUtil.getRootTemplateParentElement(templateForZoomLevelSelectorView.getContents().getText(), "org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfZoomLevelSelectorView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfZoomLevelSelectorView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(8);
    dataFieldMetas.put("decreaseButton", new DataFieldMeta());
    dataFieldMetas.put("increaseButton", new DataFieldMeta());
    dataFieldMetas.put("resetButton", new DataFieldMeta());
    dataFieldMetas.put("dropDownPanelGroup", new DataFieldMeta());
    dataFieldMetas.put("dropDownPanel", new DataFieldMeta());
    dataFieldMetas.put("dropDownButton", new DataFieldMeta());
    dataFieldMetas.put("dropDownText", new DataFieldMeta());
    dataFieldMetas.put("dropDownMenu", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView", "org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorView_Button_decreaseButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "decreaseButton");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView", "org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorView_Button_increaseButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "increaseButton");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView", "org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorView_Button_resetButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "resetButton");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView", "org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorView_Div_dropDownPanelGroup(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dropDownPanelGroup");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView", "org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorView_Div_dropDownPanel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dropDownPanel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView", "org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorView_Button_dropDownButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dropDownButton");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView", "org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorView_Span_dropDownText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dropDownText");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView", "org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorView_UnorderedList_dropDownMenu(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dropDownMenu");
    templateFieldsMap.put("decreaseButton", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorView_Button_decreaseButton(instance))));
    templateFieldsMap.put("increaseButton", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorView_Button_increaseButton(instance))));
    templateFieldsMap.put("resetButton", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorView_Button_resetButton(instance))));
    templateFieldsMap.put("dropDownPanelGroup", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorView_Div_dropDownPanelGroup(instance))));
    templateFieldsMap.put("dropDownPanel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorView_Div_dropDownPanel(instance))));
    templateFieldsMap.put("dropDownButton", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorView_Button_dropDownButton(instance))));
    templateFieldsMap.put("dropDownText", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorView_Span_dropDownText(instance))));
    templateFieldsMap.put("dropDownMenu", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorView_UnorderedList_dropDownMenu(instance))));
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfZoomLevelSelectorView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("dropDownButton"), new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent event) {
        ZoomLevelSelectorView_onDropDownKeyUp_KeyUpEvent(instance, event);
      }
    }, KeyUpEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("dropDownButton"), new KeyDownHandler() {
      public void onKeyDown(KeyDownEvent event) {
        ZoomLevelSelectorView_onDropDownKeyDown_KeyDownEvent(instance, event);
      }
    }, KeyDownEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("decreaseButton"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        ZoomLevelSelectorView_onDecreaseLevel_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("resetButton"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        ZoomLevelSelectorView_onReset_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("dropDownButton"), new KeyPressHandler() {
      public void onKeyPress(KeyPressEvent event) {
        ZoomLevelSelectorView_onDropDownKeyPress_KeyPressEvent(instance, event);
      }
    }, KeyPressEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("increaseButton"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        ZoomLevelSelectorView_onIncreaseLevel_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ZoomLevelSelectorView) instance, contextManager);
  }

  public void destroyInstanceHelper(final ZoomLevelSelectorView instance, final ContextManager contextManager) {
    instance.destroy();
    TemplateUtil.cleanupWidget(instance);
  }

  native static Div ZoomLevelSelectorView_Div_dropDownPanel(ZoomLevelSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::dropDownPanel;
  }-*/;

  native static void ZoomLevelSelectorView_Div_dropDownPanel(ZoomLevelSelectorView instance, Div value) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::dropDownPanel = value;
  }-*/;

  native static ClientTranslationService ZoomLevelSelectorView_ClientTranslationService_translationService(ZoomLevelSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::translationService;
  }-*/;

  native static void ZoomLevelSelectorView_ClientTranslationService_translationService(ZoomLevelSelectorView instance, ClientTranslationService value) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::translationService = value;
  }-*/;

  native static Button ZoomLevelSelectorView_Button_decreaseButton(ZoomLevelSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::decreaseButton;
  }-*/;

  native static void ZoomLevelSelectorView_Button_decreaseButton(ZoomLevelSelectorView instance, Button value) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::decreaseButton = value;
  }-*/;

  native static Div ZoomLevelSelectorView_Div_dropDownPanelGroup(ZoomLevelSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::dropDownPanelGroup;
  }-*/;

  native static void ZoomLevelSelectorView_Div_dropDownPanelGroup(ZoomLevelSelectorView instance, Div value) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::dropDownPanelGroup = value;
  }-*/;

  native static Span ZoomLevelSelectorView_Span_dropDownText(ZoomLevelSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::dropDownText;
  }-*/;

  native static void ZoomLevelSelectorView_Span_dropDownText(ZoomLevelSelectorView instance, Span value) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::dropDownText = value;
  }-*/;

  native static UnorderedList ZoomLevelSelectorView_UnorderedList_dropDownMenu(ZoomLevelSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::dropDownMenu;
  }-*/;

  native static void ZoomLevelSelectorView_UnorderedList_dropDownMenu(ZoomLevelSelectorView instance, UnorderedList value) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::dropDownMenu = value;
  }-*/;

  native static ManagedInstance ZoomLevelSelectorView_ManagedInstance_items(ZoomLevelSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::items;
  }-*/;

  native static void ZoomLevelSelectorView_ManagedInstance_items(ZoomLevelSelectorView instance, ManagedInstance<ZoomLevelSelectorItem> value) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::items = value;
  }-*/;

  native static Button ZoomLevelSelectorView_Button_resetButton(ZoomLevelSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::resetButton;
  }-*/;

  native static void ZoomLevelSelectorView_Button_resetButton(ZoomLevelSelectorView instance, Button value) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::resetButton = value;
  }-*/;

  native static Button ZoomLevelSelectorView_Button_dropDownButton(ZoomLevelSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::dropDownButton;
  }-*/;

  native static void ZoomLevelSelectorView_Button_dropDownButton(ZoomLevelSelectorView instance, Button value) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::dropDownButton = value;
  }-*/;

  native static Button ZoomLevelSelectorView_Button_increaseButton(ZoomLevelSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::increaseButton;
  }-*/;

  native static void ZoomLevelSelectorView_Button_increaseButton(ZoomLevelSelectorView instance, Button value) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::increaseButton = value;
  }-*/;

  public native static void ZoomLevelSelectorView_onIncreaseLevel_ClickEvent(ZoomLevelSelectorView instance, ClickEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::onIncreaseLevel(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void ZoomLevelSelectorView_onDecreaseLevel_ClickEvent(ZoomLevelSelectorView instance, ClickEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::onDecreaseLevel(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void ZoomLevelSelectorView_onDropDownKeyDown_KeyDownEvent(ZoomLevelSelectorView instance, KeyDownEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::onDropDownKeyDown(Lcom/google/gwt/event/dom/client/KeyDownEvent;)(a0);
  }-*/;

  public native static void ZoomLevelSelectorView_onDropDownKeyUp_KeyUpEvent(ZoomLevelSelectorView instance, KeyUpEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::onDropDownKeyUp(Lcom/google/gwt/event/dom/client/KeyUpEvent;)(a0);
  }-*/;

  public native static void ZoomLevelSelectorView_onReset_ClickEvent(ZoomLevelSelectorView instance, ClickEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::onReset(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void ZoomLevelSelectorView_onDropDownKeyPress_KeyPressEvent(ZoomLevelSelectorView instance, KeyPressEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView::onDropDownKeyPress(Lcom/google/gwt/event/dom/client/KeyPressEvent;)(a0);
  }-*/;
}