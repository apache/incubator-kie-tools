/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.backend.server;

import com.thoughtworks.xstream.XStream;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.PathImpl;
import org.uberfire.backend.workbench.WorkbenchServices;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.kie.commons.java.nio.file.NoSuchFileException;
import org.uberfire.security.Identity;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

/**
 * Workbench services
 */
@Service
@ApplicationScoped
public class WorkbenchServicesImpl
        implements
        WorkbenchServices {

    @Inject
    private VFSService vfsService;

    @Inject
    @Named("fs")
    private ActiveFileSystems fileSystems;


    @Inject
    @SessionScoped
    private Identity identity;

    private XStream xs = new XStream();

    private Path bootstrapRoot = null;

    @PostConstruct
    public void init() {
        this.bootstrapRoot = fileSystems.getBootstrapFileSystem().getRootDirectories().get(0);
    }

    @Override
    public void save(final PerspectiveDefinition perspective) {
        final String xml = xs.toXML(perspective);

        final String rootURI = bootstrapRoot.toURI();

        vfsService.write(new PathImpl(rootURI + "/.metadata/.users/" + identity.getName() + "/.perspectives/" + perspective.getName() + ".perspective"), xml);
    }

    @Override
    public PerspectiveDefinition load(final String perspectiveName) {
        final String rootURI = bootstrapRoot.toURI();
        final Path path = new PathImpl(rootURI + "/.metadata/.users/" + identity.getName() + "/.perspectives/" + perspectiveName + ".perspective");

        if (vfsService.exists(path)) {
            final String xml = vfsService.readAllString(path);
            return (PerspectiveDefinition) xs.fromXML(xml);
        }

        return null;
    }

    @Override
    public HashMap<String, String> loadDefaultEditorsMap() {

        HashMap<String, String> map = new HashMap<String, String>();
        try {

            PathImpl path = getPathToDefaultEditors();
            if (vfsService.exists(path)) {
                for (String line : vfsService.readAllLines(path)) {
                    if (!line.trim().startsWith("#")) {
                        String[] split = line.split("=");
                        map.put(split[0], split[1]);
                    }
                }
            }

            return map;

        } catch (NoSuchFileException e) {
            e.printStackTrace();
            return map;
        }
    }

    @Override
    public void saveDefaultEditors(Map<String, String> properties) {
        StringBuilder text = new StringBuilder();
        for (String key : properties.keySet()) {
            text.append(String.format("%s=%s", key, properties.get(key)));
        }

        vfsService.write(getPathToDefaultEditors(), text.toString());
    }

    private PathImpl getPathToDefaultEditors() {
        return new PathImpl(bootstrapRoot.toURI() + "/.metadata/.users/" + identity.getName() + "/.defaultEditors");
    }
}
