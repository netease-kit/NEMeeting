// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.log;

import java.io.Serializable;
import java.io.Writer;

public class StringBuilderWriter extends Writer implements Serializable {

  private final StringBuilder builder;

  public StringBuilderWriter() {
    this.builder = new StringBuilder();
  }

  public StringBuilderWriter(int capacity) {
    this.builder = new StringBuilder(capacity);
  }

  public StringBuilderWriter(StringBuilder builder) {
    this.builder = builder != null ? builder : new StringBuilder();
  }

  public Writer append(char value) {
    this.builder.append(value);
    return this;
  }

  public Writer append(CharSequence value) {
    this.builder.append(value);
    return this;
  }

  public Writer append(CharSequence value, int start, int end) {
    this.builder.append(value, start, end);
    return this;
  }

  public void close() {}

  public void flush() {}

  public void write(String value) {
    if (value != null) {
      this.builder.append(value);
    }
  }

  public void write(char[] value, int offset, int length) {
    if (value != null) {
      this.builder.append(value, offset, length);
    }
  }

  public StringBuilder getBuilder() {
    return this.builder;
  }

  public String toString() {
    return this.builder.toString();
  }
}
