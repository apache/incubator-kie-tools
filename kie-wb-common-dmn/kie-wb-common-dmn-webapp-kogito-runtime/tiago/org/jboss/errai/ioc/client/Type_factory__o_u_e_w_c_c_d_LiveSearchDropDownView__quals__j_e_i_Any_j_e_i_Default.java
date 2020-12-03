package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown.View;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView;
import org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooter;
import org.uberfire.ext.widgets.common.client.dropdown.noItems.NoItemsComponent;

public class Type_factory__o_u_e_w_c_c_d_LiveSearchDropDownView__quals__j_e_i_Any_j_e_i_Default extends Factory<LiveSearchDropDownView> { public interface o_u_e_w_c_c_d_LiveSearchDropDownViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/widgets/common/client/dropdown/LiveSearchDropDownView.html") public TextResource getContents();
  @Source("org/uberfire/ext/widgets/common/client/dropdown/LiveSearchDropDownView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_w_c_c_d_LiveSearchDropDownView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LiveSearchDropDownView.class, "Type_factory__o_u_e_w_c_c_d_LiveSearchDropDownView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LiveSearchDropDownView.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, View.class, UberView.class, HasPresenter.class });
  }

  public void init(final Context context) {
    ((o_u_e_w_c_c_d_LiveSearchDropDownViewTemplateResource) GWT.create(o_u_e_w_c_c_d_LiveSearchDropDownViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public LiveSearchDropDownView createInstance(final ContextManager contextManager) {
    final LiveSearchDropDownView instance = new LiveSearchDropDownView();
    setIncompleteInstance(instance);
    final Div LiveSearchDropDownView_spinnerPanel = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchDropDownView_spinnerPanel);
    LiveSearchDropDownView_Div_spinnerPanel(instance, LiveSearchDropDownView_spinnerPanel);
    final Div LiveSearchDropDownView_mainPanel = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchDropDownView_mainPanel);
    LiveSearchDropDownView_Div_mainPanel(instance, LiveSearchDropDownView_mainPanel);
    final Button LiveSearchDropDownView_dropDownButton = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchDropDownView_dropDownButton);
    LiveSearchDropDownView_Button_dropDownButton(instance, LiveSearchDropDownView_dropDownButton);
    final Div LiveSearchDropDownView_dropDownPanel = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchDropDownView_dropDownPanel);
    LiveSearchDropDownView_Div_dropDownPanel(instance, LiveSearchDropDownView_dropDownPanel);
    final NoItemsComponent LiveSearchDropDownView_noItems = (NoItemsComponent) contextManager.getInstance("Type_factory__o_u_e_w_c_c_d_n_NoItemsComponent__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, LiveSearchDropDownView_noItems);
    LiveSearchDropDownView_NoItemsComponent_noItems(instance, LiveSearchDropDownView_noItems);
    final Span LiveSearchDropDownView_spinnerText = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchDropDownView_spinnerText);
    LiveSearchDropDownView_Span_spinnerText(instance, LiveSearchDropDownView_spinnerText);
    final Div LiveSearchDropDownView_searchPanel = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchDropDownView_searchPanel);
    LiveSearchDropDownView_Div_searchPanel(instance, LiveSearchDropDownView_searchPanel);
    final UnorderedList LiveSearchDropDownView_dropDownMenu = (UnorderedList) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_UnorderedList__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchDropDownView_dropDownMenu);
    LiveSearchDropDownView_UnorderedList_dropDownMenu(instance, LiveSearchDropDownView_dropDownMenu);
    final Span LiveSearchDropDownView_dropDownText = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchDropDownView_dropDownText);
    LiveSearchDropDownView_Span_dropDownText(instance, LiveSearchDropDownView_dropDownText);
    final Input LiveSearchDropDownView_searchInput = (Input) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Input__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchDropDownView_searchInput);
    LiveSearchDropDownView_Input_searchInput(instance, LiveSearchDropDownView_searchInput);
    final LiveSearchFooter LiveSearchDropDownView_liveSearchFooter = (LiveSearchFooter) contextManager.getInstance("Type_factory__o_u_e_w_c_c_d_f_LiveSearchFooter__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, LiveSearchDropDownView_liveSearchFooter);
    LiveSearchDropDownView_LiveSearchFooter_liveSearchFooter(instance, LiveSearchDropDownView_liveSearchFooter);
    o_u_e_w_c_c_d_LiveSearchDropDownViewTemplateResource templateForLiveSearchDropDownView = GWT.create(o_u_e_w_c_c_d_LiveSearchDropDownViewTemplateResource.class);
    Element parentElementForTemplateOfLiveSearchDropDownView = TemplateUtil.getRootTemplateParentElement(templateForLiveSearchDropDownView.getContents().getText(), "org/uberfire/ext/widgets/common/client/dropdown/LiveSearchDropDownView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/widgets/common/client/dropdown/LiveSearchDropDownView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLiveSearchDropDownView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLiveSearchDropDownView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(11);
    dataFieldMetas.put("mainPanel", new DataFieldMeta());
    dataFieldMetas.put("dropDownPanel", new DataFieldMeta());
    dataFieldMetas.put("dropDownButton", new DataFieldMeta());
    dataFieldMetas.put("dropDownText", new DataFieldMeta());
    dataFieldMetas.put("searchPanel", new DataFieldMeta());
    dataFieldMetas.put("searchInput", new DataFieldMeta());
    dataFieldMetas.put("dropDownMenu", new DataFieldMeta());
    dataFieldMetas.put("spinnerPanel", new DataFieldMeta());
    dataFieldMetas.put("spinnerText", new DataFieldMeta());
    dataFieldMetas.put("liveSearchFooter", new DataFieldMeta());
    dataFieldMetas.put("noItems", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView", "org/uberfire/ext/widgets/common/client/dropdown/LiveSearchDropDownView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_Div_mainPanel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "mainPanel");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView", "org/uberfire/ext/widgets/common/client/dropdown/LiveSearchDropDownView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_Div_dropDownPanel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dropDownPanel");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView", "org/uberfire/ext/widgets/common/client/dropdown/LiveSearchDropDownView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_Button_dropDownButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dropDownButton");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView", "org/uberfire/ext/widgets/common/client/dropdown/LiveSearchDropDownView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_Span_dropDownText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dropDownText");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView", "org/uberfire/ext/widgets/common/client/dropdown/LiveSearchDropDownView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_Div_searchPanel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "searchPanel");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView", "org/uberfire/ext/widgets/common/client/dropdown/LiveSearchDropDownView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_Input_searchInput(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "searchInput");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView", "org/uberfire/ext/widgets/common/client/dropdown/LiveSearchDropDownView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_UnorderedList_dropDownMenu(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dropDownMenu");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView", "org/uberfire/ext/widgets/common/client/dropdown/LiveSearchDropDownView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_Div_spinnerPanel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "spinnerPanel");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView", "org/uberfire/ext/widgets/common/client/dropdown/LiveSearchDropDownView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_Span_spinnerText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "spinnerText");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView", "org/uberfire/ext/widgets/common/client/dropdown/LiveSearchDropDownView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(LiveSearchDropDownView_LiveSearchFooter_liveSearchFooter(instance).getElement());
      }
    }, dataFieldElements, dataFieldMetas, "liveSearchFooter");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView", "org/uberfire/ext/widgets/common/client/dropdown/LiveSearchDropDownView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(LiveSearchDropDownView_NoItemsComponent_noItems(instance).getElement());
      }
    }, dataFieldElements, dataFieldMetas, "noItems");
    templateFieldsMap.put("mainPanel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_Div_mainPanel(instance))));
    templateFieldsMap.put("dropDownPanel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_Div_dropDownPanel(instance))));
    templateFieldsMap.put("dropDownButton", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_Button_dropDownButton(instance))));
    templateFieldsMap.put("dropDownText", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_Span_dropDownText(instance))));
    templateFieldsMap.put("searchPanel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_Div_searchPanel(instance))));
    templateFieldsMap.put("searchInput", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_Input_searchInput(instance))));
    templateFieldsMap.put("dropDownMenu", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_UnorderedList_dropDownMenu(instance))));
    templateFieldsMap.put("spinnerPanel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_Div_spinnerPanel(instance))));
    templateFieldsMap.put("spinnerText", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchDropDownView_Span_spinnerText(instance))));
    templateFieldsMap.put("liveSearchFooter", ElementWrapperWidget.getWidget(LiveSearchDropDownView_LiveSearchFooter_liveSearchFooter(instance).getElement()));
    templateFieldsMap.put("noItems", ElementWrapperWidget.getWidget(LiveSearchDropDownView_NoItemsComponent_noItems(instance).getElement()));
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLiveSearchDropDownView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("dropDownButton"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        LiveSearchDropDownView_onDropDownClick_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("searchInput"), new MouseOverHandler() {
      public void onMouseOver(MouseOverEvent event) {
        LiveSearchDropDownView_onSearchOver_MouseOverEvent(instance, event);
      }
    }, MouseOverEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("searchInput"), new KeyDownHandler() {
      public void onKeyDown(KeyDownEvent event) {
        LiveSearchDropDownView_onSearchOver_KeyDownEvent(instance, event);
      }
    }, KeyDownEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("searchInput"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        LiveSearchDropDownView_onSearchClick_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("searchInput"), new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent event) {
        LiveSearchDropDownView_onSearchChanged_KeyUpEvent(instance, event);
      }
    }, KeyUpEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LiveSearchDropDownView) instance, contextManager);
  }

  public void destroyInstanceHelper(final LiveSearchDropDownView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  public void invokePostConstructs(final LiveSearchDropDownView instance) {
    instance.initialize();
  }

  native static Div LiveSearchDropDownView_Div_dropDownPanel(LiveSearchDropDownView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::dropDownPanel;
  }-*/;

  native static void LiveSearchDropDownView_Div_dropDownPanel(LiveSearchDropDownView instance, Div value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::dropDownPanel = value;
  }-*/;

  native static NoItemsComponent LiveSearchDropDownView_NoItemsComponent_noItems(LiveSearchDropDownView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::noItems;
  }-*/;

  native static void LiveSearchDropDownView_NoItemsComponent_noItems(LiveSearchDropDownView instance, NoItemsComponent value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::noItems = value;
  }-*/;

  native static LiveSearchFooter LiveSearchDropDownView_LiveSearchFooter_liveSearchFooter(LiveSearchDropDownView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::liveSearchFooter;
  }-*/;

  native static void LiveSearchDropDownView_LiveSearchFooter_liveSearchFooter(LiveSearchDropDownView instance, LiveSearchFooter value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::liveSearchFooter = value;
  }-*/;

  native static Div LiveSearchDropDownView_Div_searchPanel(LiveSearchDropDownView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::searchPanel;
  }-*/;

  native static void LiveSearchDropDownView_Div_searchPanel(LiveSearchDropDownView instance, Div value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::searchPanel = value;
  }-*/;

  native static Div LiveSearchDropDownView_Div_mainPanel(LiveSearchDropDownView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::mainPanel;
  }-*/;

  native static void LiveSearchDropDownView_Div_mainPanel(LiveSearchDropDownView instance, Div value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::mainPanel = value;
  }-*/;

  native static Span LiveSearchDropDownView_Span_spinnerText(LiveSearchDropDownView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::spinnerText;
  }-*/;

  native static void LiveSearchDropDownView_Span_spinnerText(LiveSearchDropDownView instance, Span value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::spinnerText = value;
  }-*/;

  native static Button LiveSearchDropDownView_Button_dropDownButton(LiveSearchDropDownView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::dropDownButton;
  }-*/;

  native static void LiveSearchDropDownView_Button_dropDownButton(LiveSearchDropDownView instance, Button value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::dropDownButton = value;
  }-*/;

  native static Span LiveSearchDropDownView_Span_dropDownText(LiveSearchDropDownView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::dropDownText;
  }-*/;

  native static void LiveSearchDropDownView_Span_dropDownText(LiveSearchDropDownView instance, Span value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::dropDownText = value;
  }-*/;

  native static UnorderedList LiveSearchDropDownView_UnorderedList_dropDownMenu(LiveSearchDropDownView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::dropDownMenu;
  }-*/;

  native static void LiveSearchDropDownView_UnorderedList_dropDownMenu(LiveSearchDropDownView instance, UnorderedList value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::dropDownMenu = value;
  }-*/;

  native static Div LiveSearchDropDownView_Div_spinnerPanel(LiveSearchDropDownView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::spinnerPanel;
  }-*/;

  native static void LiveSearchDropDownView_Div_spinnerPanel(LiveSearchDropDownView instance, Div value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::spinnerPanel = value;
  }-*/;

  native static Input LiveSearchDropDownView_Input_searchInput(LiveSearchDropDownView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::searchInput;
  }-*/;

  native static void LiveSearchDropDownView_Input_searchInput(LiveSearchDropDownView instance, Input value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::searchInput = value;
  }-*/;

  public native static void LiveSearchDropDownView_onSearchClick_ClickEvent(LiveSearchDropDownView instance, ClickEvent a0) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::onSearchClick(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void LiveSearchDropDownView_onSearchOver_KeyDownEvent(LiveSearchDropDownView instance, KeyDownEvent a0) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::onSearchOver(Lcom/google/gwt/event/dom/client/KeyDownEvent;)(a0);
  }-*/;

  public native static void LiveSearchDropDownView_onSearchChanged_KeyUpEvent(LiveSearchDropDownView instance, KeyUpEvent a0) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::onSearchChanged(Lcom/google/gwt/event/dom/client/KeyUpEvent;)(a0);
  }-*/;

  public native static void LiveSearchDropDownView_onDropDownClick_ClickEvent(LiveSearchDropDownView instance, ClickEvent a0) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::onDropDownClick(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void LiveSearchDropDownView_onSearchOver_MouseOverEvent(LiveSearchDropDownView instance, MouseOverEvent a0) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView::onSearchOver(Lcom/google/gwt/event/dom/client/MouseOverEvent;)(a0);
  }-*/;
}