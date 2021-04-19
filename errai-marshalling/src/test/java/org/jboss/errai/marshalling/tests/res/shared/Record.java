/*
 * Copyright (C) 2010 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.marshalling.tests.res.shared;

import java.util.*;

//@Portable
public class Record {
  private int recordId;
  private String name;
  private float balance;
  private Date accountOpened;
  private RecordType type;


  private Set<Item> stuff;
  private Map<String, String> properties;

  public Record() {
  }

  public Record(int recordId, String name, float balance, Date accountOpened, RecordType type, Item[] stuff, String[][] properties) {
    this.recordId = recordId;
    this.name = name;
    this.balance = balance;
    this.accountOpened = accountOpened;

    this.stuff = new HashSet<Item>();
    this.stuff.addAll(Arrays.asList(stuff));

    this.type = type;

    this.properties = new HashMap<String, String>();
    for (String[] s : properties) {
      this.properties.put(s[0], s[1]);
    }
  }

  public int getRecordId() {
    return recordId;
  }

  public void setRecordId(int recordId) {
    this.recordId = recordId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public float getBalance() {
    return balance;
  }

  public void setBalance(float balance) {
    this.balance = balance;
  }

  public Date getAccountOpened() {
    return accountOpened;
  }

  public void setAccountOpened(Date accountOpened) {
    this.accountOpened = accountOpened;
  }

  public RecordType getType() {
    return type;
  }

  public void setType(RecordType type) {
    this.type = type;
  }

  public Set<Item> getStuff() {
    return stuff;
  }

  public void setStuff(Set<Item> stuff) {
    this.stuff = stuff;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }

  @Override
  public String toString() {
    return "Record{" +
        "recordId=" + recordId +
        ", name='" + name + '\'' +
        ", balance=" + balance +
        ", accountOpened=" + accountOpened +
        ", stuff=" + stuff +
        '}';
  }
}
