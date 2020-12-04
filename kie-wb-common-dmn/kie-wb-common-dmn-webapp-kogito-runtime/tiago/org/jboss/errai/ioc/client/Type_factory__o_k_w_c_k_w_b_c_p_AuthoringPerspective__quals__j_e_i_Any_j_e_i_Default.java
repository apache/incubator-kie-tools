package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.showcase.client.editor.DMNDiagramEditor;
import org.kie.workbench.common.kogito.webapp.base.client.editor.KogitoScreen;
import org.kie.workbench.common.kogito.webapp.base.client.perspectives.AuthoringPerspective;
import org.kie.workbench.common.kogito.webapp.base.client.perspectives.PerspectiveConfiguration;
import org.uberfire.workbench.model.PerspectiveDefinition;

public class Type_factory__o_k_w_c_k_w_b_c_p_AuthoringPerspective__quals__j_e_i_Any_j_e_i_Default extends Factory<AuthoringPerspective> { private class Type_factory__o_k_w_c_k_w_b_c_p_AuthoringPerspective__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends AuthoringPerspective implements Proxy<AuthoringPerspective> {
    private final ProxyHelper<AuthoringPerspective> proxyHelper = new ProxyHelperImpl<AuthoringPerspective>("Type_factory__o_k_w_c_k_w_b_c_p_AuthoringPerspective__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final AuthoringPerspective instance) {

    }

    public AuthoringPerspective asBeanType() {
      return this;
    }

    public void setInstance(final AuthoringPerspective instance) {
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

    @Override public PerspectiveDefinition buildPerspective() {
      if (proxyHelper != null) {
        final AuthoringPerspective proxiedInstance = proxyHelper.getInstance(this);
        final PerspectiveDefinition retVal = proxiedInstance.buildPerspective();
        return retVal;
      } else {
        return super.buildPerspective();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final AuthoringPerspective proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_k_w_b_c_p_AuthoringPerspective__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AuthoringPerspective.class, "Type_factory__o_k_w_c_k_w_b_c_p_AuthoringPerspective__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AuthoringPerspective.class, Object.class });
  }

  public AuthoringPerspective createInstance(final ContextManager contextManager) {
    final AuthoringPerspective instance = new AuthoringPerspective();
    setIncompleteInstance(instance);
    final DMNDiagramEditor AuthoringPerspective_kogitoScreen = (DMNDiagramEditor) contextManager.getInstance("Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditor__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_a_DiagramEditor");
    AuthoringPerspective_KogitoScreen_kogitoScreen(instance, AuthoringPerspective_kogitoScreen);
    final PerspectiveConfiguration AuthoringPerspective_perspectiveConfiguration = (PerspectiveConfiguration) contextManager.getInstance("Type_factory__o_k_w_c_k_w_b_c_p_PerspectiveConfiguration__quals__j_e_i_Any_j_e_i_Default");
    AuthoringPerspective_PerspectiveConfiguration_perspectiveConfiguration(instance, AuthoringPerspective_perspectiveConfiguration);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<AuthoringPerspective> proxyImpl = new Type_factory__o_k_w_c_k_w_b_c_p_AuthoringPerspective__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static KogitoScreen AuthoringPerspective_KogitoScreen_kogitoScreen(AuthoringPerspective instance) /*-{
    return instance.@org.kie.workbench.common.kogito.webapp.base.client.perspectives.AuthoringPerspective::kogitoScreen;
  }-*/;

  native static void AuthoringPerspective_KogitoScreen_kogitoScreen(AuthoringPerspective instance, KogitoScreen value) /*-{
    instance.@org.kie.workbench.common.kogito.webapp.base.client.perspectives.AuthoringPerspective::kogitoScreen = value;
  }-*/;

  native static PerspectiveConfiguration AuthoringPerspective_PerspectiveConfiguration_perspectiveConfiguration(AuthoringPerspective instance) /*-{
    return instance.@org.kie.workbench.common.kogito.webapp.base.client.perspectives.AuthoringPerspective::perspectiveConfiguration;
  }-*/;

  native static void AuthoringPerspective_PerspectiveConfiguration_perspectiveConfiguration(AuthoringPerspective instance, PerspectiveConfiguration value) /*-{
    instance.@org.kie.workbench.common.kogito.webapp.base.client.perspectives.AuthoringPerspective::perspectiveConfiguration = value;
  }-*/;
}