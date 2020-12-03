package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
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
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.views.pfly.splash.SplashModalFooter;

public class Type_factory__o_u_c_v_p_s_SplashModalFooter__quals__j_e_i_Any_j_e_i_Default extends Factory<SplashModalFooter> { public interface o_u_c_v_p_s_SplashModalFooterTemplateResource extends Template, ClientBundle { @Source("org/uberfire/client/views/pfly/splash/SplashModalFooter.html") public TextResource getContents(); }
  public Type_factory__o_u_c_v_p_s_SplashModalFooter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SplashModalFooter.class, "Type_factory__o_u_c_v_p_s_SplashModalFooter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SplashModalFooter.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class });
  }

  public SplashModalFooter createInstance(final ContextManager contextManager) {
    final SplashModalFooter instance = new SplashModalFooter();
    setIncompleteInstance(instance);
    final Button SplashModalFooter_closeButton = (Button) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_Button__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, SplashModalFooter_closeButton);
    SplashModalFooter_Button_closeButton(instance, SplashModalFooter_closeButton);
    final CheckBox SplashModalFooter_show = (CheckBox) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_CheckBox__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, SplashModalFooter_show);
    SplashModalFooter_CheckBox_show(instance, SplashModalFooter_show);
    o_u_c_v_p_s_SplashModalFooterTemplateResource templateForSplashModalFooter = GWT.create(o_u_c_v_p_s_SplashModalFooterTemplateResource.class);
    Element parentElementForTemplateOfSplashModalFooter = TemplateUtil.getRootTemplateParentElement(templateForSplashModalFooter.getContents().getText(), "org/uberfire/client/views/pfly/splash/SplashModalFooter.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/splash/SplashModalFooter.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSplashModalFooter));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSplashModalFooter));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("show", new DataFieldMeta());
    dataFieldMetas.put("closeButton", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.splash.SplashModalFooter", "org/uberfire/client/views/pfly/splash/SplashModalFooter.html", new Supplier<Widget>() {
      public Widget get() {
        return SplashModalFooter_CheckBox_show(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "show");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.splash.SplashModalFooter", "org/uberfire/client/views/pfly/splash/SplashModalFooter.html", new Supplier<Widget>() {
      public Widget get() {
        return SplashModalFooter_Button_closeButton(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "closeButton");
    templateFieldsMap.put("show", SplashModalFooter_CheckBox_show(instance).asWidget());
    templateFieldsMap.put("closeButton", SplashModalFooter_Button_closeButton(instance).asWidget());
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSplashModalFooter), templateFieldsMap.values());
    ((HasClickHandlers) templateFieldsMap.get("closeButton")).addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onOKButtonClick(event);
      }
    });
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((SplashModalFooter) instance, contextManager);
  }

  public void destroyInstanceHelper(final SplashModalFooter instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  public void invokePostConstructs(final SplashModalFooter instance) {
    SplashModalFooter_setup(instance);
  }

  native static CheckBox SplashModalFooter_CheckBox_show(SplashModalFooter instance) /*-{
    return instance.@org.uberfire.client.views.pfly.splash.SplashModalFooter::show;
  }-*/;

  native static void SplashModalFooter_CheckBox_show(SplashModalFooter instance, CheckBox value) /*-{
    instance.@org.uberfire.client.views.pfly.splash.SplashModalFooter::show = value;
  }-*/;

  native static Button SplashModalFooter_Button_closeButton(SplashModalFooter instance) /*-{
    return instance.@org.uberfire.client.views.pfly.splash.SplashModalFooter::closeButton;
  }-*/;

  native static void SplashModalFooter_Button_closeButton(SplashModalFooter instance, Button value) /*-{
    instance.@org.uberfire.client.views.pfly.splash.SplashModalFooter::closeButton = value;
  }-*/;

  public native static void SplashModalFooter_setup(SplashModalFooter instance) /*-{
    instance.@org.uberfire.client.views.pfly.splash.SplashModalFooter::setup()();
  }-*/;
}