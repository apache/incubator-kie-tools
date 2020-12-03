package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
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
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.views.pfly.notfound.ActivityNotFoundView;
import org.uberfire.client.workbench.widgets.notfound.ActivityNotFoundPresenter.View;

public class Type_factory__o_u_c_v_p_n_ActivityNotFoundView__quals__j_e_i_Any_j_e_i_Default extends Factory<ActivityNotFoundView> { public interface o_u_c_v_p_n_ActivityNotFoundViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/client/views/pfly/notfound/ActivityNotFoundView.html") public TextResource getContents(); }
  public Type_factory__o_u_c_v_p_n_ActivityNotFoundView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ActivityNotFoundView.class, "Type_factory__o_u_c_v_p_n_ActivityNotFoundView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ActivityNotFoundView.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, View.class, UberView.class, HasPresenter.class });
  }

  public ActivityNotFoundView createInstance(final ContextManager contextManager) {
    final ActivityNotFoundView instance = new ActivityNotFoundView();
    setIncompleteInstance(instance);
    final Span ActivityNotFoundView_identifier = (Span) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_h_Span__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ActivityNotFoundView_identifier);
    ActivityNotFoundView_Span_identifier(instance, ActivityNotFoundView_identifier);
    o_u_c_v_p_n_ActivityNotFoundViewTemplateResource templateForActivityNotFoundView = GWT.create(o_u_c_v_p_n_ActivityNotFoundViewTemplateResource.class);
    Element parentElementForTemplateOfActivityNotFoundView = TemplateUtil.getRootTemplateParentElement(templateForActivityNotFoundView.getContents().getText(), "org/uberfire/client/views/pfly/notfound/ActivityNotFoundView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/notfound/ActivityNotFoundView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfActivityNotFoundView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfActivityNotFoundView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("identifier", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.notfound.ActivityNotFoundView", "org/uberfire/client/views/pfly/notfound/ActivityNotFoundView.html", new Supplier<Widget>() {
      public Widget get() {
        return ActivityNotFoundView_Span_identifier(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "identifier");
    templateFieldsMap.put("identifier", ActivityNotFoundView_Span_identifier(instance).asWidget());
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfActivityNotFoundView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ActivityNotFoundView) instance, contextManager);
  }

  public void destroyInstanceHelper(final ActivityNotFoundView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  native static Span ActivityNotFoundView_Span_identifier(ActivityNotFoundView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.notfound.ActivityNotFoundView::identifier;
  }-*/;

  native static void ActivityNotFoundView_Span_identifier(ActivityNotFoundView instance, Span value) /*-{
    instance.@org.uberfire.client.views.pfly.notfound.ActivityNotFoundView::identifier = value;
  }-*/;
}