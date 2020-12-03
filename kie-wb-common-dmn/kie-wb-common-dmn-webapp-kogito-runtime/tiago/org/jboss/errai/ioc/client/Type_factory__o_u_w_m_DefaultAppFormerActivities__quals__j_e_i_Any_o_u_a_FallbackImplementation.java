package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.annotations.FallbackImplementation;
import org.uberfire.workbench.model.AppFormerActivities;
import org.uberfire.workbench.model.DefaultAppFormerActivities;

public class Type_factory__o_u_w_m_DefaultAppFormerActivities__quals__j_e_i_Any_o_u_a_FallbackImplementation extends Factory<DefaultAppFormerActivities> { private class Type_factory__o_u_w_m_DefaultAppFormerActivities__quals__j_e_i_Any_o_u_a_FallbackImplementationProxyImpl extends DefaultAppFormerActivities implements Proxy<DefaultAppFormerActivities> {
    private final ProxyHelper<DefaultAppFormerActivities> proxyHelper = new ProxyHelperImpl<DefaultAppFormerActivities>("Type_factory__o_u_w_m_DefaultAppFormerActivities__quals__j_e_i_Any_o_u_a_FallbackImplementation");
    public void initProxyProperties(final DefaultAppFormerActivities instance) {

    }

    public DefaultAppFormerActivities asBeanType() {
      return this;
    }

    public void setInstance(final DefaultAppFormerActivities instance) {
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

    @Override public List getAllEditorIds() {
      if (proxyHelper != null) {
        final DefaultAppFormerActivities proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getAllEditorIds();
        return retVal;
      } else {
        return super.getAllEditorIds();
      }
    }

    @Override public List getAllPerpectivesIds() {
      if (proxyHelper != null) {
        final DefaultAppFormerActivities proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getAllPerpectivesIds();
        return retVal;
      } else {
        return super.getAllPerpectivesIds();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DefaultAppFormerActivities proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_w_m_DefaultAppFormerActivities__quals__j_e_i_Any_o_u_a_FallbackImplementation() {
    super(new FactoryHandleImpl(DefaultAppFormerActivities.class, "Type_factory__o_u_w_m_DefaultAppFormerActivities__quals__j_e_i_Any_o_u_a_FallbackImplementation", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultAppFormerActivities.class, Object.class, AppFormerActivities.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new FallbackImplementation() {
        public Class annotationType() {
          return FallbackImplementation.class;
        }
        public String toString() {
          return "@org.uberfire.annotations.FallbackImplementation()";
        }
    } });
  }

  public DefaultAppFormerActivities createInstance(final ContextManager contextManager) {
    final DefaultAppFormerActivities instance = DefaultAppFormerActivities_();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DefaultAppFormerActivities> proxyImpl = new Type_factory__o_u_w_m_DefaultAppFormerActivities__quals__j_e_i_Any_o_u_a_FallbackImplementationProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static DefaultAppFormerActivities DefaultAppFormerActivities_() /*-{
    return @org.uberfire.workbench.model.DefaultAppFormerActivities::new()();
  }-*/;
}