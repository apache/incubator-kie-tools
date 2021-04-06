/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.marshalling.tests.res;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */

public class User {
  private int id;
  private String name;
  private List<Group> groups;
  private Map<User, String> userStringMap;
  private Map<String, User> userMapString;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Group> getGroups() {
    return groups;
  }

  public void setGroups(List<Group> groups) {
    this.groups = groups;
  }

  public Map<User, String> getUserStringMap() {
    return userStringMap;
  }

  public void setUserStringMap(Map<User, String> userStringMap) {
    this.userStringMap = userStringMap;
  }

  public Map<String, User> getUserMapString() {
    return userMapString;
  }

  public void setUserMapString(Map<String, User> userMapString) {
    this.userMapString = userMapString;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User)) return false;

    User user = (User) o;

    if (id != user.id) return false;
    if (groups != null ? !groups.equals(user.groups) : user.groups != null) return false;
    if (name != null ? !name.equals(user.name) : user.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (groups != null ? groups.hashCode() : 0);
    return result;
  }

  public String toString() {
    return "User:[id:" + id + ";name:" + name + ";groups:" + groups.size() + ";userStringMap:" + userStringMap.size() + ";userMapString:" + userMapString.size() + "]";
  }

  public static User create() {
    User user = new User();
    user.id = 1;
    user.name = "mrfoo";

    user.groups = new ArrayList<Group>();

    Group adminGroup = new Group();
    adminGroup.setSubGroup(adminGroup);
    adminGroup.setGroupId(1);
    adminGroup.setName("Admin");
    adminGroup.setUsersInGroup(new ArrayList<User>());

    user.groups.add(adminGroup);
    adminGroup.getUsersInGroup().add(user);

    Map<Group, User> groupUser = new HashMap<Group, User>();
    groupUser.put(adminGroup, user);

    adminGroup.setGroupUserMap(groupUser);

    user.userStringMap = new HashMap<User, String>();
    user.userStringMap.put(user, "foo");

    user.userMapString = new HashMap<String, User>();
    user.userMapString.put("bar", user);

    return user;
  }
}
