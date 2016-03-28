/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.jcr2vfsmigration;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class JcrExporterLauncher {

    private Weld weld;
    private WeldContainer weldContainer;
    private JcrExporter exporter;

    public static void main( String[] args ) {
        try {
            new JcrExporterLauncher().run( args );
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( -1 );
        }
    }

    /**
     * Use this method instead of #main() when you want to use the app directly from code. Method does not use System.exit()
     * and instead throws {@link RuntimeException} when an error occurs.
     * @param args application arguments - same as for #main() method
     */
    public void run( String... args ) {
        startUp();
        try {
            launchExport( args );
        } finally {
            shutdown();
        }
    }

    private void launchExport( String... args ) {
        if ( exporter.parseArgs( args ) ) {
            exporter.exportAll();
        }
    }

    private void shutdown() {
        weld.shutdown();
    }

    private void startUp() {
        // As per BRDRLPersistence.marshalRHS()
        disableAnnoyingJackrabbitSysouts();
        String dateFormatProperty = System.getProperty( "drools.dateformat" );
        if (dateFormatProperty == null || dateFormatProperty.length() == 0) System.setProperty( "drools.dateformat", "dd-MM-yyyy" );
        weld = new Weld();
        weldContainer = weld.initialize();
        exporter = weldContainer.instance().select( JcrExporter.class ).get();
    }

    private void disableAnnoyingJackrabbitSysouts() {
        final PrintStream origSysout = System.out;
        System.setOut(new JackrabbitFilteredPrintStream(origSysout));
    }

    /**
     * This class is used to filter out annoying sysouts coming from the Jackrabbit (JR). During migration JR prints
     * "=============== session-guest-X" messages which aren't relevant. This class is basically a hack, which enables
     * ignoring sysouts starting with "=============== session-guest". Other messages are passed to the delegate
     * (which should the original value of System.out).
     *
     * Note that only the println(String) method is filtered (it is enough for the JR use case).
     */
    private static class JackrabbitFilteredPrintStream extends PrintStream {
        private final PrintStream delegate;

        private JackrabbitFilteredPrintStream(PrintStream delegate) {
            // pass NO-OP instance as it won't be used
            super(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    throw new IllegalStateException("NO-OP OutputStream.write() method called!");
                }
            });
            this.delegate = delegate;
        }

        @Override public void flush() { delegate.flush(); }
        @Override public void close() { delegate.close(); }
        @Override public void write(int b) { delegate.write(b); }
        @Override public void write(byte[] b) throws IOException {delegate.write(b);}
        @Override public void write(byte[] buf, int off, int len) {delegate.write(buf, off, len);}
        @Override public void print(boolean b) {delegate.print(b);}
        @Override public void print(char c) {delegate.print(c);}
        @Override public void print(int i) {delegate.print(i);}
        @Override public void print(long l) {delegate.print(l);}
        @Override public void print(float f) {delegate.print(f);}
        @Override public void print(double d) {delegate.print(d);}
        @Override public void print(char[] s) {delegate.print(s);}
        @Override public void print(String s) {delegate.print(s);}
        @Override public void print(Object obj) {delegate.print(obj);}
        @Override public void println() {delegate.println();}
        @Override public void println(boolean b) {delegate.println(b);}
        @Override public void println(char c) {delegate.println(c);}
        @Override public void println(int i) {delegate.println(i);}
        @Override public void println(long l) {delegate.println(l);}
        @Override public void println(float f) {delegate.println(f);}
        @Override public void println(double d) {delegate.println(d);}
        @Override public void println(char[] s) {delegate.println(s);}
        @Override public void println(Object obj) {delegate.println(obj);}
        @Override public java.io.PrintStream printf(String format, Object... args) { return delegate.printf(format, args); }
        @Override public java.io.PrintStream printf(java.util.Locale l, String format, Object... args) { return delegate.printf(l, format, args); }
        @Override public java.io.PrintStream format(String format, Object... args) { return delegate.format(format, args); }
        @Override public java.io.PrintStream format(java.util.Locale l, String format, Object... args) { return delegate.format(l, format, args); }
        @Override public java.io.PrintStream append(CharSequence csq) { return delegate.append(csq); }
        @Override public java.io.PrintStream append(CharSequence csq, int start, int end) { return delegate.append(csq, start, end); }
        @Override public java.io.PrintStream append(char c) { return delegate.append(c); }

        @Override
        public void println(String msg) {
            if (msg.startsWith("=============== session-guest")) {
                return;
            }
            delegate.println(msg);
        }
    }
 }
