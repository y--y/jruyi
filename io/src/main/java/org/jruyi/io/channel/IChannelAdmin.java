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
package org.jruyi.io.channel;

import org.jruyi.timeoutadmin.ITimeoutNotifier;

import java.nio.ByteBuffer;

public interface IChannelAdmin {

	public void onRegisterRequired(ISelectableChannel channel);

	public void onConnectRequired(ISelectableChannel channel);

	public void onAccept(ISelectableChannel channel);

	public void performIoTask(IIoTask task, Object msg);

	public ITimeoutNotifier createTimeoutNotifier(ISelectableChannel channel);

	public ByteBuffer recvDirectBuffer();

	public ByteBuffer sendDirectBuffer();
}
