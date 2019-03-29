/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.kmodule.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.core.util.AbstractXStreamConverter;
import org.guvnor.common.services.project.model.WorkItemHandlerModel;
import org.kie.workbench.common.services.shared.kmodule.ClockTypeOption;
import org.kie.workbench.common.services.shared.kmodule.ConsoleLogger;
import org.kie.workbench.common.services.shared.kmodule.FileLogger;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;
import org.kie.workbench.common.services.shared.kmodule.ListenerModel;

public class FileLoggerConverter
        extends AbstractXStreamConverter {

    public FileLoggerConverter() {
        super(FileLogger.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        FileLogger logger = (FileLogger) value;
        writer.addAttribute("name", logger.getName());
        writer.addAttribute("file", logger.getFile());
        writer.addAttribute("threaded", Boolean.toString(logger.isThreaded()));
        writer.addAttribute("interval", Integer.toString(logger.getInterval()));

    }

    public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final FileLogger logger = new FileLogger();
        logger.setName(reader.getAttribute("name"));
        logger.setFile(reader.getAttribute("file"));
        logger.setThreaded(Boolean.parseBoolean(reader.getAttribute("threaded")));
        logger.setInterval(Integer.parseInt(reader.getAttribute("interval")));

        return logger;
    }
}
