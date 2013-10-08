/*
 * Copyright 2012 JBoss Inc
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

public class Jcr2VfsMigrationApp {
    public static boolean hasErrors = false;
    public static boolean hasWarnings = false;

    /**
     * This method should not be called directly from Java code as it uses System.exit() which may cause problems
     * (e.g for surefire). Use 'new Jcr2VfsMigrationApp().run(String... args)' when using this class directly.
     *
     * @param args application arguments, never null
     */
    public static void main(String... args) {
        // git daemon thread is not needed for migration tool, so disable it
        System.setProperty("org.kie.nio.git.daemon.enabled", "false");
        try {
            new Jcr2VfsMigrationApp().run(args);
        } catch (Exception e) {
            // stacktrace should not be printed to stdout (the exception should be already logged)
            System.exit(-1);
        }
    }

    /**
     * Use this method instead of #main() when you want to use the app directly from code. Method does not use System.exit()
     * and instead throws {@link RuntimeException} when an error occurs.
     *
     * @param args application arguments - same as for #main() method
     */
    public void run(String... args) {
        Weld weld = new Weld();
        try {
            WeldContainer weldContainer = weld.initialize();

            Jcr2VfsMigrater migrater = weldContainer.instance().select(Jcr2VfsMigrater.class).get();
            if (migrater.parseArgs(args)) {
                migrater.migrateAll();
            }
        } finally {
            weld.shutdown();
        }

        if (hasErrors) {
            throw new RuntimeException("Migration ended with errors - see log for more details.");
        }
    }

}
