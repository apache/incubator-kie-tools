package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.jsbridge.client.loading.LazyLoadingScreen;

public class Type_factory__o_u_j_c_l_LazyLoadingScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<LazyLoadingScreen> { public interface o_u_j_c_l_LazyLoadingScreenTemplateResource extends Template, ClientBundle { @Source("org/uberfire/jsbridge/client/loading/lazy-loading.html") public TextResource getContents(); }
  public Type_factory__o_u_j_c_l_LazyLoadingScreen__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LazyLoadingScreen.class, "Type_factory__o_u_j_c_l_LazyLoadingScreen__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LazyLoadingScreen.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.elemental2.IsElement.class });
  }

  public LazyLoadingScreen createInstance(final ContextManager contextManager) {
    final LazyLoadingScreen instance = new LazyLoadingScreen();
    setIncompleteInstance(instance);
    o_u_j_c_l_LazyLoadingScreenTemplateResource templateForLazyLoadingScreen = GWT.create(o_u_j_c_l_LazyLoadingScreenTemplateResource.class);
    Element parentElementForTemplateOfLazyLoadingScreen = TemplateUtil.getRootTemplateParentElement(templateForLazyLoadingScreen.getContents().getText(), "org/uberfire/jsbridge/client/loading/lazy-loading.html", "");
    TemplateUtil.translateTemplate("org/uberfire/jsbridge/client/loading/lazy-loading.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLazyLoadingScreen));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLazyLoadingScreen));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(0);
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLazyLoadingScreen), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LazyLoadingScreen) instance, contextManager);
  }

  public void destroyInstanceHelper(final LazyLoadingScreen instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final LazyLoadingScreen instance) {
    instance.init();
  }
}