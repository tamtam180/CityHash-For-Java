/*
 * Copyright (C) 2012 tamtam180
 * Copyright (C) 2021 nikitasius
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.orz.hash;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * @author tamtam180 - kirscheless at gmail.com
 */
public class CityHashTest {

    final long k0 = 0xc3a5c85c97cb3127L;
    final long kSeed0 = 1234567;
    final long kSeed1 = k0;
    final int kDataSize = 1 << 20;
    final int kTestSize = CityHashTestData.testdata.length;

    private byte[] data = new byte[kDataSize];

    @Before
    public void setup() {
        long a = 9;
        long b = 777;
        for (int i = 0; i < data.length; i++) {
            a = (a ^ (a >>> 41)) * k0 + b;
            b = (b ^ (b >>> 41)) * k0 + i;
            int u = (int) ((b >>> 37) & 0xFF);
            data[i] = (byte) u;
        }
    }

    private void testImpl(long[] expected, int offset, int len) {

        System.out.println("********* " + offset + " " + len);

        long[] u = CityHash.cityHash128(data, offset, len);
        long[] v = CityHash.cityHash128WithSeed(data, offset, len, kSeed0, kSeed1);

//        byte[] b = new byte[32];
//        System.arraycopy(data, 0, b, 0, 32);
//        long[] u1 = CityHash.cityHash128(b, 0, 32);
//        long[] v1 = CityHash.cityHash128WithSeed(data, 0, 32, kSeed0, kSeed1);
//        System.out.printf("b: `%s`%n", new String(Base64.getEncoder().encode(b), StandardCharsets.UTF_8));
//        System.out.printf("offset: `%d`%n", 0);
//        System.out.printf("kSeed0: `%d`%n", kSeed0);
//        System.out.printf("kSeed1: `%d`%n", kSeed1);
//        System.out.printf("v1[0]: `%d`%n", v1[0]);
//        System.out.printf("v1[1]: `%d`%n", v1[1]);
//        System.out.printf("u1[0]: `%d`%n", u1[0]);
//        System.out.printf("u1[1]: `%d`%n", u1[1]);

        assertThat(CityHash.cityHash64(data, offset, len), is(expected[0]));
        assertThat(CityHash.cityHash64WithSeed(data, offset, len, kSeed0), is(expected[1]));
        assertThat(CityHash.cityHash64WithSeeds(data, offset, len, kSeed0, kSeed1), is(expected[2]));
        assertThat(u[0], is(expected[3]));
        assertThat(u[1], is(expected[4]));
        assertThat(v[0], is(expected[5]));
        assertThat(v[1], is(expected[6]));

//		long[] y = CityHash.cityHashCrc128(data, offset, len);
//		long[] z = CityHash.cityHashCrc128WithSeed(data, offset, len, kSeed0, kSeed1);
//		assertThat(y[0], is(expected[7]));
//		assertThat(y[1], is(expected[8]));
//		assertThat(z[0], is(expected[9]));
//		assertThat(z[1], is(expected[10]));

//		long[] crc256_result = CityHash.cityHashCrc256(data, offset, len);
//		for (int i = 0; i < 4; i++) {
//			assertThat(crc256_result[i], is(expected[11+i]));
//		}

    }

    @Test
    public void main_test() {

        int i = 0;
        for (; i < kTestSize - 1; i++) {
            testImpl(CityHashTestData.testdata[i], i * i, i);
        }
        testImpl(CityHashTestData.testdata[i], 0, kDataSize);

    }

    @Test
    public void testCityHash64b() {
        ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
        bb.putLong(3201855508716831834L);
        assertArrayEquals(bb.array(), CityHash.cityHash64b(new byte[]{0x64, 0x65, 0x66}));
    }

    @Test
    public void testCityHash64() {
        assertEquals(3201855508716831834L, CityHash.cityHash64(new byte[]{0x64, 0x65, 0x66}));
    }

    @Test
    public void testCityHash128WithSeedb() {
        byte[] data = Base64.getDecoder().decode("CO/gF1XbcFM8/lwolkC37C5mVx6Yf/CRNXaRJl0dYRs=");
        byte[][] b = CityHash.cityHash128WithSeedb(data, 1234567L, -4348849565147123417L);
        assertArrayEquals(new byte[]{0x58, (byte) 0xC2, (byte) 0xDF, 0x05, 0x05, (byte) 0x82, 0x6A, 0x7B}, b[0]);
        assertArrayEquals(new byte[]{(byte) 0xf4, 0x2b, 0x62, 0x1e, (byte) 0xe8, (byte) 0x98, 0x26, 0x32}, b[1]);
    }

    @Test
    public void testCityHash128WithSeed() {
        byte[] data = Base64.getDecoder().decode("CO/gF1XbcFM8/lwolkC37C5mVx6Yf/CRNXaRJl0dYRs=");
        assertArrayEquals(new long[]{6395919633479789179L, -852479819565750734L}, CityHash.cityHash128WithSeed(data, 0, 32, 1234567L, -4348849565147123417L));
    }

    @Test
    public void testCityHash128b() {
        byte[] data = Base64.getDecoder().decode("CO/gF1XbcFM8/lwolkC37C5mVx6Yf/CRNXaRJl0dYRs=");
        byte[][] b = CityHash.cityHash128b(data);
        assertArrayEquals(new byte[]{0x5F, (byte) 0xCA, (byte) 0xEE, (byte) 0xF9, (byte) 0xB7, 0x1E, 0x14, 0x11}, b[0]);
        assertArrayEquals(new byte[]{0x2E, 0x50, 0x2B, (byte) 0x97, 0x20, (byte) 0xDA, 0x22, (byte) 0xC9}, b[1]);
    }

    @Test
    public void testCityHash128() {
        byte[] data = Base64.getDecoder().decode("CO/gF1XbcFM8/lwolkC37C5mVx6Yf/CRNXaRJl0dYRs=");
        assertArrayEquals(new long[]{6902592135185175569L, 3337215251972760265L}, CityHash.cityHash128(data));

    }
}
