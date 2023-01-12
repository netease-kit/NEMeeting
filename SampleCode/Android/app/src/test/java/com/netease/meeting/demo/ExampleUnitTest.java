// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.meeting.demo;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.Random;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
  @Test
  public void addition_isCorrect() {
    assertEquals(4, 2 + 2);
  }

  @Test
  public void testEncryptAndDecrypt() {
    final int BUFFER_SIZE = 1024;
    for (int i = 0; i < 10; i++) {
      ByteBuffer buffer = getRandomBuffer(BUFFER_SIZE);
      ByteBuffer encrypted = encrypt(buffer, BUFFER_SIZE);
      ByteBuffer decrypted = decrypt(encrypted, BUFFER_SIZE);
      assertArrayEquals(buffer.array(), decrypted.array());
    }
  }

  private ByteBuffer getRandomBuffer(int size) {
    Random random = new Random(System.currentTimeMillis());
    ByteBuffer buffer = ByteBuffer.allocate(size);
    for (int i = 0; i < size; i++) {
      buffer.put((byte) random.nextInt(255));
    }
    return buffer;
  }

  private ByteBuffer encrypt(ByteBuffer buffer, int size) {
    ByteBuffer target = ByteBuffer.allocate(size);
    for (int i = 0; i < size; i++) {
      byte b = buffer.get(i);
      target.put(i, (byte) ~(b & 0xFF));
    }
    return target;
  }

  private ByteBuffer decrypt(ByteBuffer buffer, int size) {
    ByteBuffer target = ByteBuffer.allocate(size);
    for (int i = 0; i < size; i++) {
      byte b = buffer.get(i);
      target.put(i, (byte) ~(b & 0xFF));
    }
    return target;
  }
}
