/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.contenthandler;

import org.drools.repository.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This configures the content handlers based on a props file.
 */
public class ContentManager {

    private static final Logger log = LoggerFactory.getLogger(ContentManager.class);
    private static final String CONTENT_CONFIG_PROPERTIES = "/contenthandler.properties";
    private static ContentManager INSTANCE;

    /**
     * This is a map of the contentHandlers to use.
     */
    private final Map<String, ContentHandler> contentHandlers = new HashMap<String, ContentHandler>();


    @SuppressWarnings("rawtypes")
    ContentManager(String configPath) {
        log.debug("Loading content properties");
        Properties props = new Properties();
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream(configPath);
            props.load(in);
            for (Object o : props.keySet()) {
                String contentHandler = (String) o;
                String val = props.getProperty(contentHandler);

                contentHandlers.put(contentHandler, loadContentHandlerImplementation(val));
            }
        } catch (IOException e) {
            log.error("UNABLE to load content handlers. Ahem, nothing will actually work. Ignore subsequent errors until this is resolved.", e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Return the content handlers.
     */
    public Map<String, ContentHandler> getContentHandlers() {

        return contentHandlers;
    }


    private ContentHandler loadContentHandlerImplementation(String val) throws IOException {

        try {
            return (ContentHandler) Thread.currentThread().getContextClassLoader().loadClass(val).newInstance();

        } catch (InstantiationException e) {
            log.error("Unable to load content handler implementation.", e);
            return null;
        } catch (IllegalAccessException e) {
            log.error("Unable to load content handler implementation.", e);
            return null;
        } catch (ClassNotFoundException e) {
            log.error("Unable to load content handler implementation.", e);
            return null;
        }

    }

    public static ContentManager getInstance() {
        if (INSTANCE == null) {
            //have to do this annoying thing, as in some cases, letting the classloader
            //load it up means that it will fail as the classes aren't yet available.
            //so have to use this nasty anti-pattern here. Sorry.
            synchronized (ContentManager.class) {
                ContentManager.INSTANCE = new ContentManager(CONTENT_CONFIG_PROPERTIES);
            }
        }
        return INSTANCE;
    }

    public static ContentHandler getHandler(String format) {
        ContentHandler h = ContentManager.getInstance().getContentHandlers().get(format);
        if (h == null)
            h = new DefaultContentHandler();//throw new IllegalArgumentException("Unable to handle the content type: " + format);
        return h;
    }
}
