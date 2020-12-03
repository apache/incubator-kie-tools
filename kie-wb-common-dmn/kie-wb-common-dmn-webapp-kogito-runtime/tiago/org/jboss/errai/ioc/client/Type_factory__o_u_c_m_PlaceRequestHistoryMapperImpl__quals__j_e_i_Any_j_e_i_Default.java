package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.PlaceRequestHistoryMapper;
import org.uberfire.client.mvp.PlaceRequestHistoryMapperImpl;

public class Type_factory__o_u_c_m_PlaceRequestHistoryMapperImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PlaceRequestHistoryMapperImpl> { public Type_factory__o_u_c_m_PlaceRequestHistoryMapperImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PlaceRequestHistoryMapperImpl.class, "Type_factory__o_u_c_m_PlaceRequestHistoryMapperImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PlaceRequestHistoryMapperImpl.class, Object.class, PlaceRequestHistoryMapper.class });
  }

  public PlaceRequestHistoryMapperImpl createInstance(final ContextManager contextManager) {
    final PlaceRequestHistoryMapperImpl instance = new PlaceRequestHistoryMapperImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}