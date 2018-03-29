/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.migration.cli;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ToolConfig {

    private static final String HELP_HEADER = null;
    private static final String HELP_FOOTER = null;

    private static final String BATCH_SHORT = "b";
    private static final String BATCH_LONG = "batch";
    private static final String BATCH_DESCRIPTION = "Set this mode to ignore prompts. WARNING: you will not be prompted to make backups!";

    private static final String TARGET_SHORT = "t";
    private static final String TARGET_LONG = "target";
    private static final String TARGET_DESCRIPTION = "Target directory containing workbench projects and repositories being migrated.";
    private static final String TARGET_ARG_NAME = "DIRECTORY";

    private static final Options OPTIONS = new Options().addOption(Option.builder(TARGET_SHORT)
                                                                         .argName(TARGET_ARG_NAME)
                                                                         .longOpt(TARGET_LONG)
                                                                         .hasArg()
                                                                         .numberOfArgs(1)
                                                                         .desc(TARGET_DESCRIPTION)
                                                                         .required()
                                                                         .build())
                                                        .addOption(Option.builder(BATCH_SHORT)
                                                                         .longOpt(BATCH_LONG)
                                                                         .hasArg(false)
                                                                         .desc(BATCH_DESCRIPTION)
                                                                         .build());

    private CommandLine cli;

    public ToolConfig(CommandLine cli) {
        this.cli = cli;
    }

    public Path getTarget() {
        return Optional
                       .ofNullable(cli.getOptionValue(TARGET_SHORT))
                       .map(str -> Paths.get(str).toAbsolutePath().normalize())
                       .orElseThrow(() -> new IllegalArgumentException("A target must be specified."));
    }

    public boolean isBatch() {
        return cli.hasOption(BATCH_SHORT);
    }

    public static ToolConfig parse(String[] args) throws ParseException {
        Options opts = OPTIONS;
        return new ToolConfig(new DefaultParser().parse(opts, args));
    }

    public static void printHelp(PrintStream stream, String app) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(new PrintWriter(stream, true),
                            formatter.getWidth(),
                            app,
                            HELP_HEADER,
                            OPTIONS,
                            formatter.getLeftPadding(),
                            formatter.getDescPadding(),
                            HELP_FOOTER,
                            true);
    }

    public static interface ToolConfigFactory {
        ToolConfig parse(String[] args) throws ParseException;
        void printHelp(PrintStream stream, String app);
    }

    public static class DefaultFactory implements ToolConfigFactory {

        @Override
        public ToolConfig parse(String[] args) throws ParseException {
            return ToolConfig.parse(args);
        }

        @Override
        public void printHelp(PrintStream stream, String app) {
            ToolConfig.printHelp(stream, app);
        }
    }

}
