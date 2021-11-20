/*
 * Copyright (C) 2012 tamtam180
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
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

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

}
