/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.explorer.backend.server.restrictor;

import java.io.File;
import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.restriction.PathOperationRestriction;
import org.uberfire.ext.editor.commons.service.restrictor.DeleteRestrictor;
import org.uberfire.ext.editor.commons.service.restrictor.RenameRestrictor;

@ApplicationScoped
public class ProjectRequiredPathsRestrictor implements DeleteRestrictor,
                                                       RenameRestrictor {

    private enum Rule {

        POM_XML( RuleType.ENDS_WITH, "/pom.xml" ),
        SRC( RuleType.ENDS_WITH, "/src" ),
        SRC_MAIN( RuleType.ENDS_WITH, "/src/main" ),
        SRC_MAIN_JAVA( RuleType.ENDS_WITH, "/src/main/java" ),
        SRC_MAIN_RESOURCES( RuleType.ENDS_WITH, "/src/main/resources" ),
        SRC_MAIN_META_INF( RuleType.ENDS_WITH, "/src/main/resources/META-INF" ),
        SRC_MAIN_META_INF_KMODULE_XML( RuleType.ENDS_WITH, "/src/main/resources/META-INF/kmodule.xml" ),
        SRC_TEST( RuleType.ENDS_WITH, "/src/test" ),
        SRC_TEST_JAVA( RuleType.ENDS_WITH, "/src/test/java" ),
        SRC_TEST_RESOURCES( RuleType.ENDS_WITH, "/src/test/resources" );

        private RuleType type;

        private String expression;

        Rule( RuleType type, String expression ) {
            this.type = type;
            this.expression = expression;
        }

        public RuleType getType() {
            return this.type;
        }

        public String getExpression() {
            return this.expression;
        }

        public boolean check( String text ) {
            return type.check( text, expression );
        }
    }

    @Override
    public PathOperationRestriction hasRestriction( final Path path ) {
        if ( isRequiredPath( path ) ) {
            return new PathOperationRestriction() {
                @Override
                public String getMessage( final Path path ) {
                    return path.toURI() + " cannot be deleted, renamed or moved, because it is a required project file or directory.";
                }
            };
        }

        return null;
    }

    private boolean isRequiredPath( final Path path ) {
        final String text = removeLastSeparatorIfExists( path.toURI() );

        for ( Rule rule : Rule.values()) {
            if ( rule.check( text ) ) {
                return true;
            }
        }

        return false;
    }

    private String removeLastSeparatorIfExists( String text ) {
        if ( text.length() > 1 && text.endsWith( File.separator ) ) {
            text = text.substring( 0, text.length() - 1 );
        }

        return text;
    }

    private enum RuleType {
        STARTS_WITH {

            @Override
            public boolean check( String text, String expression ) {
                return text != null && text.startsWith( expression );
            }

        }, CONTAINS {

            @Override
            public boolean check( String text, String expression ) {
                return text != null && text.contains( expression );
            }

        }, ENDS_WITH {

            @Override
            public boolean check( String text, String expression ) {
                return text != null && text.endsWith( expression );
            }

        };

        public abstract boolean check( String text, String expression );
    }
}
