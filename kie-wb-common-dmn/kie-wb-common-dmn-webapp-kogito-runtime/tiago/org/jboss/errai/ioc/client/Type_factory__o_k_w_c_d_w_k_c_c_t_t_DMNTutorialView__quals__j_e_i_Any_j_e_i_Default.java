package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.tutorial.DMNTutorial;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.tutorial.DMNTutorial.View;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.tutorial.DMNTutorialView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorialView__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNTutorialView> { public interface o_k_w_c_d_w_k_c_c_t_t_DMNTutorialViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/webapp/kogito/common/client/tour/tutorial/DMNTutorialView.html") public TextResource getContents(); }
  private class Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorialView__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNTutorialView implements Proxy<DMNTutorialView> {
    private final ProxyHelper<DMNTutorialView> proxyHelper = new ProxyHelperImpl<DMNTutorialView>("Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorialView__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DMNTutorialView instance) {

    }

    public DMNTutorialView asBeanType() {
      return this;
    }

    public void setInstance(final DMNTutorialView instance) {
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

    @Override public void init(DMNTutorial presenter) {
      if (proxyHelper != null) {
        final DMNTutorialView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init(presenter);
      } else {
        super.init(presenter);
      }
    }

    @Override public String getStepContent(int step) {
      if (proxyHelper != null) {
        final DMNTutorialView proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getStepContent(step);
        return retVal;
      } else {
        return super.getStepContent(step);
      }
    }

    @Override public HTMLElement getElement() {
      if (proxyHelper != null) {
        final DMNTutorialView proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = proxiedInstance.getElement();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNTutorialView proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorialView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNTutorialView.class, "Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorialView__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNTutorialView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public DMNTutorialView createInstance(final ContextManager contextManager) {
    final DMNTutorialView instance = new DMNTutorialView();
    setIncompleteInstance(instance);
    o_k_w_c_d_w_k_c_c_t_t_DMNTutorialViewTemplateResource templateForDMNTutorialView = GWT.create(o_k_w_c_d_w_k_c_c_t_t_DMNTutorialViewTemplateResource.class);
    Element parentElementForTemplateOfDMNTutorialView = TemplateUtil.getRootTemplateParentElement(templateForDMNTutorialView.getContents().getText(), "org/kie/workbench/common/dmn/webapp/kogito/common/client/tour/tutorial/DMNTutorialView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/webapp/kogito/common/client/tour/tutorial/DMNTutorialView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDMNTutorialView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDMNTutorialView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(0);
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDMNTutorialView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DMNTutorialView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DMNTutorialView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNTutorialView> proxyImpl = new Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorialView__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}