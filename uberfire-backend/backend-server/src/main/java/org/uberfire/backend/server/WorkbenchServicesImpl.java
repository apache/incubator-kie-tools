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
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.security.Identity;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

    public void save(final PerspectiveDefinition perspective) {
        final String xml = xs.toXML(perspective);

        final String rootURI = bootstrapRoot.toURI();

        vfsService.write(new PathImpl(rootURI + "/.metadata/.users/" + identity.getName() + "/.perspectives/" + perspective.getName() + ".perspective"), xml);
    }

    public PerspectiveDefinition load(final String perspectiveName) {
        final String rootURI = bootstrapRoot.toURI();
        final Path path = new PathImpl(rootURI + "/.metadata/.users/" + identity.getName() + "/.perspectives/" + perspectiveName + ".perspective");

        if (vfsService.exists(path)) {
            final String xml = vfsService.readAllString(path);
            return (PerspectiveDefinition) xs.fromXML(xml);
        }

        return null;
    }


    public Map<String, String> loadDefaultEditorsMap() {
        HashMap<String, String> map = new HashMap<String, String>();

        Properties properties = loadDefaultEditorProperties();
        for (Object key : properties.keySet()) {
            String key1 = (String) key;
            String property = properties.getProperty(key1);
            System.out.println("K,V: "+key1 +" , " +property);
            map.put(key1, property);
        }

        return map;
    }

    @Override
    public void saveDefaultEditors(Map<String, String> propertiesMap) {

        System.out.println( "HEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHERE");
        System.out.println( "HEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHERE");
        System.out.println( "HEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHERE");
        System.out.println( "HEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHEREHERE");

        Properties properties = new Properties();

        for (String key : propertiesMap.keySet()) {
            properties.setProperty(key, propertiesMap.get(key));
        }

        try {
            saveDefaultEditorList(properties);
            System.out.println( "SAVED : " + properties.toString());

        } catch (IOException e) {
            System.out.println( "FAILED");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public Properties loadDefaultEditorProperties() {

        System.out.println( "LOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOAD");
        System.out.println( "LOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOAD");
        System.out.println( "LOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOAD");
        System.out.println( "LOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOADLOAD");
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(vfsService.readAllString(getPathToDefaultEditors())));

            for(Object key:properties.keySet()){
                System.out.println( "**** " + key +" "+properties.get(key));
            }
            System.out.println( "LOADED");

            return properties;
        } catch (NoSuchFileException e) {
            System.out.println( "NSFE");
            e.printStackTrace();
            return new Properties();
        } catch (IOException e) {
            System.out.println( "IOE");
            e.printStackTrace();
            return new Properties();
        }
    }

    private void saveDefaultEditorList(Properties properties) throws IOException {
        vfsService.write(getPathToDefaultEditors(), properties.toString());
    }

    private PathImpl getPathToDefaultEditors() {
        String uri = bootstrapRoot.toURI() + "/.metadata/.users/" + identity.getName() + "/.defaultEditors";
        System.out.println( "SAVING TO PATH : " + uri);
        return new PathImpl(uri);
    }
}
