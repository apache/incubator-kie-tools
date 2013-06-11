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

    /**
     * To run this in development:
     * Either use the unit test Jcr2VfsMigrationAppTest (recommended): it sets up the input and output dirs for you.
     * Or run it and fill in -i and -o correctly.
     * @param args never null
     */
    public static void main(String... args) {
        Weld weld = new Weld();
        WeldContainer weldContainer = weld.initialize();

        Jcr2VfsMigrater migrater = weldContainer.instance().select(Jcr2VfsMigrater.class).get();
        if(migrater.parseArgs(args)) {
            migrater.migrateAll();
        }

        weld.shutdown();
        System.out.println("Migration ended");
        System.exit(0);
    }

}
