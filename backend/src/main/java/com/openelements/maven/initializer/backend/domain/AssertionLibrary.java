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
 * software distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.openelements.maven.initializer.backend.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Represents the assertion library to use in tests. */
public enum AssertionLibrary {
  /** AssertJ assertion library. */
  ASSERTJ("assertj"),
  /** Hamcrest assertion library. */
  HAMCREST("hamcrest"),
  /** No assertion library - use JUnit assertions only. */
  NONE("none");

  private final String value;

  AssertionLibrary(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static AssertionLibrary fromValue(String value) {
    if (value == null) {
      return NONE;
    }
    for (AssertionLibrary library : AssertionLibrary.values()) {
      if (library.value.equalsIgnoreCase(value)) {
        return library;
      }
    }
    return NONE;
  }
}
