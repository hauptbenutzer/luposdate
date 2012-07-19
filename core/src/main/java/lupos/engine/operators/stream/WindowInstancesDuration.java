/**
 * Copyright (c) 2012, Institute of Information Systems (Sven Groppe), University of Luebeck
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 	- Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * 	  disclaimer.
 * 	- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * 	  following disclaimer in the documentation and/or other materials provided with the distribution.
 * 	- Neither the name of the University of Luebeck nor the names of its contributors may be used to endorse or promote
 * 	  products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package lupos.engine.operators.stream;

import java.util.LinkedList;

import lupos.datastructures.items.TimestampedTriple;
import lupos.datastructures.items.Triple;
import lupos.datastructures.items.literal.Literal;
import lupos.engine.operators.messages.Message;
import lupos.engine.operators.messages.StartOfEvaluationMessage;
import lupos.misc.debug.DebugStep;
import lupos.rdf.Prefix;

public class WindowInstancesDuration extends WindowInstances {

	private final int duration;
	private LinkedList<TimestampedTriple> tripleList;

	public WindowInstancesDuration(final int duration, Literal instanceClass) {
		super(instanceClass);
		this.duration = duration;
	}

	@Override
	public Message preProcessMessage(final StartOfEvaluationMessage message) {
		tripleList = new LinkedList<TimestampedTriple>();
		return message;
	}

	@Override
	public void consume(final Triple triple) {
		final TimestampedTriple timestampedTriple = (TimestampedTriple) triple;
		final long now = timestampedTriple.getTimestamp();
		int index = 0;
		for (final TimestampedTriple t : tripleList) {
			if (now - t.getTimestamp() >= duration) {
				this.deleteTriple(t);
				index++;
			} else
				break;
		}
		while (index > 0) {
			this.tripleList.remove(0);
			index--;
		}
		tripleList.add(timestampedTriple);
		super.consume(timestampedTriple);
	}
	
	@Override
	public void consumeDebug(final Triple triple, final DebugStep debugstep) {
		final TimestampedTriple timestampedTriple = (TimestampedTriple) triple;
		final long now = timestampedTriple.getTimestamp();
		int index = 0;
		for (final TimestampedTriple t : tripleList) {
			if (now - t.getTimestamp() >= duration) {
				this.deleteTripleDebug(t, debugstep);
				index++;
			} else
				break;
		}
		while (index > 0) {
			this.tripleList.remove(0);
			index--;
		}
		tripleList.add(timestampedTriple);
		super.consumeDebug(timestampedTriple, debugstep);
	}

	@Override
	public String toString() {
		return super.toString()+" " + this.duration;
	}
	
	@Override
	public String toString(Prefix prefixInstance) {
		return super.toString(prefixInstance) + " " + this.duration;
	}
}
