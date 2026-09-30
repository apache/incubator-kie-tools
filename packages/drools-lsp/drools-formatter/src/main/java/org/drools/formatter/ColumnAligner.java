/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.formatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Lays rows of cells out in columns of equal width, so a reader scanning a
 * {@code declare} block top-down gets a straight edge down each column.
 *
 * <p>A column is as wide as its longest cell in the block plus one, which leaves
 * at least one space between neighbouring cells. The last cell of a row is never
 * padded: nothing follows it, so padding would only add trailing whitespace.
 *
 * <p>Rows need not be the same length. A short row stops where its cells stop
 * rather than being padded out to the widest row, and it still contributes to the
 * width of the columns it does occupy.
 *
 * <p>The class is deliberately free of any knowledge of DRL — it takes cells and
 * returns lines. Deciding what a cell IS (a constant name, an argument, a trailing
 * comment) belongs to the caller, which is what keeps this testable on its own.
 */
final class ColumnAligner {

  private ColumnAligner() {}

  /** One line per row, cells padded into columns. */
  static List<String> align(List<List<String>> rows) {
    List<Integer> widths = columnWidths(rows);
    List<String> lines = new ArrayList<>(rows.size());
    for (List<String> row : rows) {
      StringBuilder line = new StringBuilder();
      for (int col = 0; col < row.size(); col++) {
        String cell = row.get(col);
        line.append(cell);
        if (col < row.size() - 1) {
          for (int pad = cell.length(); pad < widths.get(col); pad++) {
            line.append(' ');
          }
        }
      }
      lines.add(line.toString());
    }
    return lines;
  }

  private static List<Integer> columnWidths(List<List<String>> rows) {
    List<Integer> widths = new ArrayList<>();
    for (List<String> row : rows) {
      for (int col = 0; col < row.size(); col++) {
        int width = row.get(col).length() + 1;
        if (col < widths.size()) {
          widths.set(col, Math.max(widths.get(col), width));
        } else {
          widths.add(width);
        }
      }
    }
    return widths;
  }
}
