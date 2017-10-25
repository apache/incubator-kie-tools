/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.ala.openshift.dns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.RouteList;
import sun.net.spi.nameservice.NameService;

/**
 * OpenShiftNameService.
 */
@SuppressWarnings("restriction")
public class OpenShiftNameService implements NameService {

    private static final Map<String, InetAddress> ROUTING = Collections.synchronizedMap(new TreeMap<String, InetAddress>());

    public static void setRoutes(RouteList routeList, String routerHost) {
        InetAddress routerAddr;
        try {
            routerAddr = routerHost == null ? null : InetAddress.getByName(routerHost);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP for router host", e);
        }
        if (routeList != null) {
            synchronized (ROUTING) {
                for (Route route : routeList.getItems()) {
                    String host = route.getSpec().getHost();
                    if (routerAddr != null) {
                        System.out.println(String.format("Adding route (router -> host): %s -> %s", routerHost, host));
                        ROUTING.put(host, routerAddr);
                    } else {
                        ROUTING.remove(host);
                    }
                }
            }
        }
    }

    public static Set<String> getHosts() {
        synchronized (ROUTING) {
            return Collections.unmodifiableSet(ROUTING.keySet());
        }
    }

    public static boolean isHostRegistered(String host) {
        synchronized (ROUTING) {
            return ROUTING.containsKey(host);
        }
    }

    @Override
    public InetAddress[] lookupAllHostAddr(String host) throws UnknownHostException {
        synchronized (ROUTING) {
            if (host != null) {
                InetAddress routerAddr = ROUTING.get(host);
                if (routerAddr != null) {
                    return new InetAddress[] { InetAddress.getByAddress(host, routerAddr.getAddress()) };
                }
            }
            throw new UnknownHostException(host);
        }
    }

    @Override
    public String getHostByAddr(byte[] addr) throws UnknownHostException {
        throw new UnknownHostException();
    }

}
