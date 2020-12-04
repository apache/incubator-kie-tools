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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentView;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;

public class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_MultipleInputComponentViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleInputComponentViewImpl> { public interface o_k_w_c_f_d_c_r_r_l_c_i_w_MultipleInputComponentViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/MultipleInputComponentViewImpl.html") public TextResource getContents();
  @Source("org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/MultipleInputComponentViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_MultipleInputComponentViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MultipleInputComponentViewImpl.class, "Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_MultipleInputComponentViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MultipleInputComponentViewImpl.class, Object.class, MultipleInputComponentView.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_f_d_c_r_r_l_c_i_w_MultipleInputComponentViewImplTemplateResource) GWT.create(o_k_w_c_f_d_c_r_r_l_c_i_w_MultipleInputComponentViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public MultipleInputComponentViewImpl createInstance(final ContextManager contextManager) {
    final MultipleInputComponentViewImpl instance = new MultipleInputComponentViewImpl();
    setIncompleteInstance(instance);
    final TranslationService MultipleInputComponentViewImpl_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, MultipleInputComponentViewImpl_translationService);
    MultipleInputComponentViewImpl_TranslationService_translationService(instance, MultipleInputComponentViewImpl_translationService);
    final Div MultipleInputComponentViewImpl_errorMessage = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MultipleInputComponentViewImpl_errorMessage);
    MultipleInputComponentViewImpl_Div_errorMessage(instance, MultipleInputComponentViewImpl_errorMessage);
    final Div MultipleInputComponentViewImpl_toolbar = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MultipleInputComponentViewImpl_toolbar);
    MultipleInputComponentViewImpl_Div_toolbar(instance, MultipleInputComponentViewImpl_toolbar);
    final Div MultipleInputComponentViewImpl_errorContainer = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MultipleInputComponentViewImpl_errorContainer);
    MultipleInputComponentViewImpl_Div_errorContainer(instance, MultipleInputComponentViewImpl_errorContainer);
    final Div MultipleInputComponentViewImpl_table = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MultipleInputComponentViewImpl_table);
    MultipleInputComponentViewImpl_Div_table(instance, MultipleInputComponentViewImpl_table);
    final Button MultipleInputComponentViewImpl_promoteButton = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MultipleInputComponentViewImpl_promoteButton);
    MultipleInputComponentViewImpl_Button_promoteButton(instance, MultipleInputComponentViewImpl_promoteButton);
    final Button MultipleInputComponentViewImpl_removeButton = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MultipleInputComponentViewImpl_removeButton);
    MultipleInputComponentViewImpl_Button_removeButton(instance, MultipleInputComponentViewImpl_removeButton);
    final Button MultipleInputComponentViewImpl_addButton = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MultipleInputComponentViewImpl_addButton);
    MultipleInputComponentViewImpl_Button_addButton(instance, MultipleInputComponentViewImpl_addButton);
    final Button MultipleInputComponentViewImpl_degradeButton = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MultipleInputComponentViewImpl_degradeButton);
    MultipleInputComponentViewImpl_Button_degradeButton(instance, MultipleInputComponentViewImpl_degradeButton);
    final Button MultipleInputComponentViewImpl_hideErrorButton = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MultipleInputComponentViewImpl_hideErrorButton);
    MultipleInputComponentViewImpl_Button_hideErrorButton(instance, MultipleInputComponentViewImpl_hideErrorButton);
    o_k_w_c_f_d_c_r_r_l_c_i_w_MultipleInputComponentViewImplTemplateResource templateForMultipleInputComponentViewImpl = GWT.create(o_k_w_c_f_d_c_r_r_l_c_i_w_MultipleInputComponentViewImplTemplateResource.class);
    Element parentElementForTemplateOfMultipleInputComponentViewImpl = TemplateUtil.getRootTemplateParentElement(templateForMultipleInputComponentViewImpl.getContents().getText(), "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/MultipleInputComponentViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/MultipleInputComponentViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultipleInputComponentViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultipleInputComponentViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(9);
    dataFieldMetas.put("toolbar", new DataFieldMeta());
    dataFieldMetas.put("addButton", new DataFieldMeta());
    dataFieldMetas.put("removeButton", new DataFieldMeta());
    dataFieldMetas.put("promoteButton", new DataFieldMeta());
    dataFieldMetas.put("degradeButton", new DataFieldMeta());
    dataFieldMetas.put("table", new DataFieldMeta());
    dataFieldMetas.put("errorContainer", new DataFieldMeta());
    dataFieldMetas.put("errorMessage", new DataFieldMeta());
    dataFieldMetas.put("hideErrorButton", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/MultipleInputComponentViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Div_toolbar(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "toolbar");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/MultipleInputComponentViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Button_addButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "addButton");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/MultipleInputComponentViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Button_removeButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "removeButton");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/MultipleInputComponentViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Button_promoteButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "promoteButton");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/MultipleInputComponentViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Button_degradeButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "degradeButton");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/MultipleInputComponentViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Div_table(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "table");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/MultipleInputComponentViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Div_errorContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "errorContainer");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/MultipleInputComponentViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Div_errorMessage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "errorMessage");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/MultipleInputComponentViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Button_hideErrorButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "hideErrorButton");
    templateFieldsMap.put("toolbar", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Div_toolbar(instance))));
    templateFieldsMap.put("addButton", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Button_addButton(instance))));
    templateFieldsMap.put("removeButton", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Button_removeButton(instance))));
    templateFieldsMap.put("promoteButton", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Button_promoteButton(instance))));
    templateFieldsMap.put("degradeButton", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Button_degradeButton(instance))));
    templateFieldsMap.put("table", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Div_table(instance))));
    templateFieldsMap.put("errorContainer", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Div_errorContainer(instance))));
    templateFieldsMap.put("errorMessage", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Div_errorMessage(instance))));
    templateFieldsMap.put("hideErrorButton", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputComponentViewImpl_Button_hideErrorButton(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultipleInputComponentViewImpl), templateFieldsMap.values());
    TemplateUtil.setupNativeEventListener(instance, (ElementWrapperWidget) templateFieldsMap.get("hideErrorButton"), new EventListener() {
      public void onBrowserEvent(Event event) {
        instance.onHideErrorButton(event);
      }
    }, 1);
    TemplateUtil.setupNativeEventListener(instance, (ElementWrapperWidget) templateFieldsMap.get("promoteButton"), new EventListener() {
      public void onBrowserEvent(Event event) {
        instance.onPromote(event);
      }
    }, 1);
    TemplateUtil.setupNativeEventListener(instance, (ElementWrapperWidget) templateFieldsMap.get("degradeButton"), new EventListener() {
      public void onBrowserEvent(Event event) {
        instance.onDegrade(event);
      }
    }, 1);
    TemplateUtil.setupNativeEventListener(instance, (ElementWrapperWidget) templateFieldsMap.get("removeButton"), new EventListener() {
      public void onBrowserEvent(Event event) {
        instance.onRemove(event);
      }
    }, 1);
    TemplateUtil.setupNativeEventListener(instance, (ElementWrapperWidget) templateFieldsMap.get("addButton"), new EventListener() {
      public void onBrowserEvent(Event event) {
        instance.onAdd(event);
      }
    }, 1);
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((MultipleInputComponentViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final MultipleInputComponentViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final MultipleInputComponentViewImpl instance) {
    instance.init();
  }

  native static Button MultipleInputComponentViewImpl_Button_removeButton(MultipleInputComponentViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::removeButton;
  }-*/;

  native static void MultipleInputComponentViewImpl_Button_removeButton(MultipleInputComponentViewImpl instance, Button value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::removeButton = value;
  }-*/;

  native static Button MultipleInputComponentViewImpl_Button_promoteButton(MultipleInputComponentViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::promoteButton;
  }-*/;

  native static void MultipleInputComponentViewImpl_Button_promoteButton(MultipleInputComponentViewImpl instance, Button value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::promoteButton = value;
  }-*/;

  native static TranslationService MultipleInputComponentViewImpl_TranslationService_translationService(MultipleInputComponentViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::translationService;
  }-*/;

  native static void MultipleInputComponentViewImpl_TranslationService_translationService(MultipleInputComponentViewImpl instance, TranslationService value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::translationService = value;
  }-*/;

  native static Button MultipleInputComponentViewImpl_Button_degradeButton(MultipleInputComponentViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::degradeButton;
  }-*/;

  native static void MultipleInputComponentViewImpl_Button_degradeButton(MultipleInputComponentViewImpl instance, Button value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::degradeButton = value;
  }-*/;

  native static Div MultipleInputComponentViewImpl_Div_toolbar(MultipleInputComponentViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::toolbar;
  }-*/;

  native static void MultipleInputComponentViewImpl_Div_toolbar(MultipleInputComponentViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::toolbar = value;
  }-*/;

  native static Div MultipleInputComponentViewImpl_Div_errorMessage(MultipleInputComponentViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::errorMessage;
  }-*/;

  native static void MultipleInputComponentViewImpl_Div_errorMessage(MultipleInputComponentViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::errorMessage = value;
  }-*/;

  native static Button MultipleInputComponentViewImpl_Button_hideErrorButton(MultipleInputComponentViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::hideErrorButton;
  }-*/;

  native static void MultipleInputComponentViewImpl_Button_hideErrorButton(MultipleInputComponentViewImpl instance, Button value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::hideErrorButton = value;
  }-*/;

  native static Div MultipleInputComponentViewImpl_Div_errorContainer(MultipleInputComponentViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::errorContainer;
  }-*/;

  native static void MultipleInputComponentViewImpl_Div_errorContainer(MultipleInputComponentViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::errorContainer = value;
  }-*/;

  native static Button MultipleInputComponentViewImpl_Button_addButton(MultipleInputComponentViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::addButton;
  }-*/;

  native static void MultipleInputComponentViewImpl_Button_addButton(MultipleInputComponentViewImpl instance, Button value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::addButton = value;
  }-*/;

  native static Div MultipleInputComponentViewImpl_Div_table(MultipleInputComponentViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::table;
  }-*/;

  native static void MultipleInputComponentViewImpl_Div_table(MultipleInputComponentViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl::table = value;
  }-*/;
}