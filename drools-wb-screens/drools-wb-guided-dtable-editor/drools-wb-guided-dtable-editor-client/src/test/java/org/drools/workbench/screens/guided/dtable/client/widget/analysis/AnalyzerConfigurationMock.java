/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.drools.verifier.core.checks.base.JavaCheckRunner;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.configuration.CheckConfiguration;
import org.drools.verifier.core.configuration.DateTimeFormatProvider;
import org.drools.verifier.core.index.keys.UUIDKeyProvider;

public class AnalyzerConfigurationMock
        extends AnalyzerConfiguration {

    public AnalyzerConfigurationMock() {
        super("UUID",
              new DateTimeFormatProvider() {
                  @Override
                  public String format(final Date dateValue) {
                      return DateTimeFormat.getFormat("dd-MMM-yyyy")
                              .format(dateValue);
                  }

                  @Override
                  public Date parse(String dateValue) {
                      return DateTimeFormat.getFormat("dd-MMM-yyyy")
                              .parse(dateValue);
                  }
              },
              new UUIDKeyProvider() {

                  private long index = Long.SIZE;

                  @Override
                  protected String newUUID() {
                      return Long.toString(index--);
                  }
              },
              CheckConfiguration.newDefault(),
              new JavaCheckRunner());
    }
}
