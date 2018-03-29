package org.kie.workbench.common.project.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.ParseException;
import org.apache.maven.cli.CLIManager;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.migration.cli.ToolConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ToolConfigTest {

    private ToolConfig.DefaultFactory defaultFactory;

    @Before
    public void init() {
        defaultFactory = new ToolConfig.DefaultFactory();
    }

    @Test(expected = ParseException.class)
    public void testNoTargetParameter() throws ParseException {
        final String[] args = {};

        defaultFactory.parse(args);
    }

    @Test(expected = MissingArgumentException.class)
    public void testNoTargetArgument() throws ParseException {
        final String[] args = {"-t"};

        defaultFactory.parse(args);
    }

    @Test
    public void testBatchArgument() throws ParseException {
        final String[] args = {"-t", "/fake/dir", "-b", ""};

        assertTrue(defaultFactory.parse(args).isBatch());
    }

    @Test
    public void testPrintHelp() throws IOException {
        final String APP_NAME = "myAppName";

        File tf = File.createTempFile("printTestFile", ".txt");

        try (PrintStream ps = new PrintStream(tf);
             BufferedReader br = new BufferedReader(new FileReader(tf))) {
            defaultFactory.printHelp(ps, APP_NAME);
            assertTrue(br.readLine().contains(APP_NAME));
        }
    }

    @Test
    public void testGetTarget() throws ParseException {
        final String[] args = {"-t", "/fake/dir"};

        CommandLine cl = new CLIManager().parse(args);
        Path path = new ToolConfig(cl).getTarget();
        assertEquals(2, path.getNameCount());
        assertEquals("fake", path.getName(0).toString());
    }
}
