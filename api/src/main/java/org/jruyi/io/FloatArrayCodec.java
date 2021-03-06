/*
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

package org.jruyi.io;

import org.jruyi.io.internal.CodecProvider;

/**
 * This class is a factory class for float array codecs.
 *
 * @see Codec
 * @see ShortArrayCodec
 * @see IntArrayCodec
 * @see LongArrayCodec
 * @see DoubleArrayCodec
 * @see StringCodec
 * @see CharArrayCodec
 * @see CharSequenceCodec
 * @since 2.0
 */
public final class FloatArrayCodec {

	private static final IFloatArrayCodecProvider c_provider = CodecProvider.getInstance().getFloatArrayCodecProvider();

	/**
	 * This interface defines all the methods that a float array codec provider
	 * has to implement. It is used to separate the implementation provider from
	 * the API module.
	 */
	public interface IFloatArrayCodecProvider {

		/**
		 * Returns a big-endian float array codec.
		 *
		 * @return a big-endian float array codec
		 */
		public ICodec<float[]> bigEndian();

		/**
		 * Returns a little-endian float array codec.
		 *
		 * @return a little-endian float array codec
		 */
		public ICodec<float[]> littleEndian();
	}

	private FloatArrayCodec() {
	}

	/**
	 * Returns a big-endian float array codec.
	 *
	 * @return a big-endian float array codec
	 */
	public static ICodec<float[]> bigEndian() {
		return c_provider.bigEndian();
	}

	/**
	 * Returns a little-endian float array codec.
	 *
	 * @return a little-endian float array codec
	 */
	public static ICodec<float[]> littleEndian() {
		return c_provider.littleEndian();
	}
}
