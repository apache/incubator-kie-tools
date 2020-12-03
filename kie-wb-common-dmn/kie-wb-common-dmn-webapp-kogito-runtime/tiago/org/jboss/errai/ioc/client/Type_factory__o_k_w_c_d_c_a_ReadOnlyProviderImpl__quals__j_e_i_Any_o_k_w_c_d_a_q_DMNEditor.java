package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import org.appformer.client.context.EditorContextProvider;
import org.appformer.kogito.bridge.client.context.impl.KogitoEditorContextProviderImpl;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.api.ReadOnlyProviderImpl;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;

public class Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<ReadOnlyProviderImpl> { private class Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditorProxyImpl extends ReadOnlyProviderImpl implements Proxy<ReadOnlyProviderImpl> {
    private final ProxyHelper<ReadOnlyProviderImpl> proxyHelper = new ProxyHelperImpl<ReadOnlyProviderImpl>("Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    public Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditorProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final ReadOnlyProviderImpl instance) {

    }

    public ReadOnlyProviderImpl asBeanType() {
      return this;
    }

    public void setInstance(final ReadOnlyProviderImpl instance) {
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

    @Override public boolean isReadOnlyDiagram() {
      if (proxyHelper != null) {
        final ReadOnlyProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isReadOnlyDiagram();
        return retVal;
      } else {
        return super.isReadOnlyDiagram();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ReadOnlyProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor() {
    super(new FactoryHandleImpl(ReadOnlyProviderImpl.class, "Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ReadOnlyProviderImpl.class, Object.class, ReadOnlyProvider.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
    } });
  }

  public ReadOnlyProviderImpl createInstance(final ContextManager contextManager) {
    final EditorContextProvider _contextProvider_0 = (KogitoEditorContextProviderImpl) contextManager.getInstance("Type_factory__o_a_k_b_c_c_i_KogitoEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_Default");
    final ReadOnlyProviderImpl instance = new ReadOnlyProviderImpl(_contextProvider_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditorProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.api.ReadOnlyProviderImpl an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.api.ReadOnlyProviderImpl ([org.appformer.client.context.EditorContextProvider])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ReadOnlyProviderImpl> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}