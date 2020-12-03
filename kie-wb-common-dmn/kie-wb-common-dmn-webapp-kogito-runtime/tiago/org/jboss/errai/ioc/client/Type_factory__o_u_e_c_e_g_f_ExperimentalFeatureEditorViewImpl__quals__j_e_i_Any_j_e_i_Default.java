package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLLabelElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.jboss.errai.common.client.api.elemental2.IsElement;
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
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditorView;
import org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditorViewImpl;

public class Type_factory__o_u_e_c_e_g_f_ExperimentalFeatureEditorViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ExperimentalFeatureEditorViewImpl> { public interface o_u_e_c_e_g_f_ExperimentalFeatureEditorViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/experimental/client/editor/group/feature/ExperimentalFeatureEditorViewImpl.html") public TextResource getContents();
  @Source("org/uberfire/experimental/client/editor/group/feature/ExperimentalFeatureEditorViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_c_e_g_f_ExperimentalFeatureEditorViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ExperimentalFeatureEditorViewImpl.class, "Type_factory__o_u_e_c_e_g_f_ExperimentalFeatureEditorViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ExperimentalFeatureEditorViewImpl.class, Object.class, ExperimentalFeatureEditorView.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_c_e_g_f_ExperimentalFeatureEditorViewImplTemplateResource) GWT.create(o_u_e_c_e_g_f_ExperimentalFeatureEditorViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public ExperimentalFeatureEditorViewImpl createInstance(final ContextManager contextManager) {
    final ExperimentalFeatureEditorViewImpl instance = new ExperimentalFeatureEditorViewImpl();
    setIncompleteInstance(instance);
    final HTMLLabelElement ExperimentalFeatureEditorViewImpl_description = (HTMLLabelElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLLabelElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ExperimentalFeatureEditorViewImpl_description);
    ExperimentalFeatureEditorViewImpl_HTMLLabelElement_description(instance, ExperimentalFeatureEditorViewImpl_description);
    final ToggleSwitch ExperimentalFeatureEditorViewImpl_enabled = (ToggleSwitch) contextManager.getInstance("ExtensionProvided_factory__o_g_e_t_c_u_ToggleSwitch__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ExperimentalFeatureEditorViewImpl_enabled);
    ExperimentalFeatureEditorViewImpl_ToggleSwitch_enabled(instance, ExperimentalFeatureEditorViewImpl_enabled);
    final HTMLLabelElement ExperimentalFeatureEditorViewImpl_name = (HTMLLabelElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLLabelElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ExperimentalFeatureEditorViewImpl_name);
    ExperimentalFeatureEditorViewImpl_HTMLLabelElement_name(instance, ExperimentalFeatureEditorViewImpl_name);
    o_u_e_c_e_g_f_ExperimentalFeatureEditorViewImplTemplateResource templateForExperimentalFeatureEditorViewImpl = GWT.create(o_u_e_c_e_g_f_ExperimentalFeatureEditorViewImplTemplateResource.class);
    Element parentElementForTemplateOfExperimentalFeatureEditorViewImpl = TemplateUtil.getRootTemplateParentElement(templateForExperimentalFeatureEditorViewImpl.getContents().getText(), "org/uberfire/experimental/client/editor/group/feature/ExperimentalFeatureEditorViewImpl.html", "");
    TemplateUtil.translateTemplate("org/uberfire/experimental/client/editor/group/feature/ExperimentalFeatureEditorViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfExperimentalFeatureEditorViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfExperimentalFeatureEditorViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("name", new DataFieldMeta());
    dataFieldMetas.put("description", new DataFieldMeta());
    dataFieldMetas.put("enabled", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditorViewImpl", "org/uberfire/experimental/client/editor/group/feature/ExperimentalFeatureEditorViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExperimentalFeatureEditorViewImpl_HTMLLabelElement_name(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "name");
    TemplateUtil.compositeComponentReplace("org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditorViewImpl", "org/uberfire/experimental/client/editor/group/feature/ExperimentalFeatureEditorViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExperimentalFeatureEditorViewImpl_HTMLLabelElement_description(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "description");
    TemplateUtil.compositeComponentReplace("org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditorViewImpl", "org/uberfire/experimental/client/editor/group/feature/ExperimentalFeatureEditorViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ExperimentalFeatureEditorViewImpl_ToggleSwitch_enabled(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "enabled");
    templateFieldsMap.put("name", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExperimentalFeatureEditorViewImpl_HTMLLabelElement_name(instance))));
    templateFieldsMap.put("description", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExperimentalFeatureEditorViewImpl_HTMLLabelElement_description(instance))));
    templateFieldsMap.put("enabled", ExperimentalFeatureEditorViewImpl_ToggleSwitch_enabled(instance).asWidget());
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfExperimentalFeatureEditorViewImpl), templateFieldsMap.values());
    ((Widget) templateFieldsMap.get("enabled")).addDomHandler(new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        instance.onToggleChange(event);
      }
    }, ChangeEvent.getType());
    TemplateUtil.setupNativeEventListener(instance, (ElementWrapperWidget) templateFieldsMap.get("name"), new EventListener() {
      public void onBrowserEvent(Event event) {
        instance.onLoadName(event);
      }
    }, 16);
    TemplateUtil.setupNativeEventListener(instance, (ElementWrapperWidget) templateFieldsMap.get("description"), new EventListener() {
      public void onBrowserEvent(Event event) {
        instance.onLoadDescription(event);
      }
    }, 16);
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ExperimentalFeatureEditorViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ExperimentalFeatureEditorViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final ExperimentalFeatureEditorViewImpl instance) {
    instance.init();
  }

  native static ToggleSwitch ExperimentalFeatureEditorViewImpl_ToggleSwitch_enabled(ExperimentalFeatureEditorViewImpl instance) /*-{
    return instance.@org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditorViewImpl::enabled;
  }-*/;

  native static void ExperimentalFeatureEditorViewImpl_ToggleSwitch_enabled(ExperimentalFeatureEditorViewImpl instance, ToggleSwitch value) /*-{
    instance.@org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditorViewImpl::enabled = value;
  }-*/;

  native static HTMLLabelElement ExperimentalFeatureEditorViewImpl_HTMLLabelElement_description(ExperimentalFeatureEditorViewImpl instance) /*-{
    return instance.@org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditorViewImpl::description;
  }-*/;

  native static void ExperimentalFeatureEditorViewImpl_HTMLLabelElement_description(ExperimentalFeatureEditorViewImpl instance, HTMLLabelElement value) /*-{
    instance.@org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditorViewImpl::description = value;
  }-*/;

  native static HTMLLabelElement ExperimentalFeatureEditorViewImpl_HTMLLabelElement_name(ExperimentalFeatureEditorViewImpl instance) /*-{
    return instance.@org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditorViewImpl::name;
  }-*/;

  native static void ExperimentalFeatureEditorViewImpl_HTMLLabelElement_name(ExperimentalFeatureEditorViewImpl instance, HTMLLabelElement value) /*-{
    instance.@org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditorViewImpl::name = value;
  }-*/;
}