package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.components.columns.Column;
import org.uberfire.ext.layout.editor.client.components.rows.RowDnDEvent;
import org.uberfire.ext.layout.editor.client.components.rows.RowDrop.Orientation;
import org.uberfire.ext.layout.editor.client.infra.DnDManager;

public class Type_factory__o_u_e_l_e_c_i_DnDManager__quals__j_e_i_Any_j_e_i_Default extends Factory<DnDManager> { private class Type_factory__o_u_e_l_e_c_i_DnDManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DnDManager implements Proxy<DnDManager> {
    private final ProxyHelper<DnDManager> proxyHelper = new ProxyHelperImpl<DnDManager>("Type_factory__o_u_e_l_e_c_i_DnDManager__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DnDManager instance) {

    }

    public DnDManager asBeanType() {
      return this;
    }

    public void setInstance(final DnDManager instance) {
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

    @Override public void beginRowMove(String rowIdBegin) {
      if (proxyHelper != null) {
        final DnDManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.beginRowMove(rowIdBegin);
      } else {
        super.beginRowMove(rowIdBegin);
      }
    }

    @Override public void endRowMove(String rowIdEnd, Orientation orientation) {
      if (proxyHelper != null) {
        final DnDManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.endRowMove(rowIdEnd, orientation);
      } else {
        super.endRowMove(rowIdEnd, orientation);
      }
    }

    @Override public void dragEndMove() {
      if (proxyHelper != null) {
        final DnDManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.dragEndMove();
      } else {
        super.dragEndMove();
      }
    }

    @Override public Column getDraggedColumn() {
      if (proxyHelper != null) {
        final DnDManager proxiedInstance = proxyHelper.getInstance(this);
        final Column retVal = proxiedInstance.getDraggedColumn();
        return retVal;
      } else {
        return super.getDraggedColumn();
      }
    }

    @Override public void endComponentMove() {
      if (proxyHelper != null) {
        final DnDManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.endComponentMove();
      } else {
        super.endComponentMove();
      }
    }

    @Override public boolean isOnRowMove() {
      if (proxyHelper != null) {
        final DnDManager proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isOnRowMove();
        return retVal;
      } else {
        return super.isOnRowMove();
      }
    }

    @Override public boolean isOnComponentMove() {
      if (proxyHelper != null) {
        final DnDManager proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isOnComponentMove();
        return retVal;
      } else {
        return super.isOnComponentMove();
      }
    }

    @Override public boolean canMoveRow() {
      if (proxyHelper != null) {
        final DnDManager proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.canMoveRow();
        return retVal;
      } else {
        return super.canMoveRow();
      }
    }

    @Override public void dragComponent(LayoutComponent layoutComponentMove, String rowId, Column draggedColumn) {
      if (proxyHelper != null) {
        final DnDManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.dragComponent(layoutComponentMove, rowId, draggedColumn);
      } else {
        super.dragComponent(layoutComponentMove, rowId, draggedColumn);
      }
    }

    @Override public String getRowId() {
      if (proxyHelper != null) {
        final DnDManager proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getRowId();
        return retVal;
      } else {
        return super.getRowId();
      }
    }

    @Override public void dragEndComponent() {
      if (proxyHelper != null) {
        final DnDManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.dragEndComponent();
      } else {
        super.dragEndComponent();
      }
    }

    @Override public LayoutComponent getLayoutComponentMove() {
      if (proxyHelper != null) {
        final DnDManager proxiedInstance = proxyHelper.getInstance(this);
        final LayoutComponent retVal = proxiedInstance.getLayoutComponentMove();
        return retVal;
      } else {
        return super.getLayoutComponentMove();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DnDManager proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_l_e_c_i_DnDManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DnDManager.class, "Type_factory__o_u_e_l_e_c_i_DnDManager__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DnDManager.class, Object.class });
  }

  public DnDManager createInstance(final ContextManager contextManager) {
    final DnDManager instance = new DnDManager();
    setIncompleteInstance(instance);
    final Event DnDManager_rowDnDEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RowDnDEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, DnDManager_rowDnDEvent);
    DnDManager_Event_rowDnDEvent(instance, DnDManager_rowDnDEvent);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DnDManager> proxyImpl = new Type_factory__o_u_e_l_e_c_i_DnDManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static Event DnDManager_Event_rowDnDEvent(DnDManager instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.infra.DnDManager::rowDnDEvent;
  }-*/;

  native static void DnDManager_Event_rowDnDEvent(DnDManager instance, Event<RowDnDEvent> value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.infra.DnDManager::rowDnDEvent = value;
  }-*/;
}