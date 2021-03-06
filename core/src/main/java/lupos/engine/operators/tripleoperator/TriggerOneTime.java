/**
 * Copyright (c) 2013, Institute of Information Systems (Sven Groppe and contributors of LUPOSDATE), University of Luebeck
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
package lupos.engine.operators.tripleoperator;

import java.util.HashSet;

import lupos.datastructures.bindings.BindingsFactory;
import lupos.datastructures.items.Triple;
import lupos.datastructures.items.Variable;
import lupos.datastructures.queryresult.QueryResult;
import lupos.engine.operators.Operator;
import lupos.engine.operators.OperatorIDTuple;
import lupos.engine.operators.messages.BindingsFactoryMessage;
import lupos.engine.operators.messages.Message;
import lupos.engine.operators.messages.StartOfEvaluationMessage;

public class TriggerOneTime extends TripleOperator implements TripleConsumer {

	protected boolean firstTime = true;
	protected final boolean addEmptyBindings;
	protected BindingsFactory bindingsFactory;

	public TriggerOneTime() {
		this(true);
	}

	public TriggerOneTime(final boolean addEmptyBindings) {
		this.unionVariables = new HashSet<Variable>();
		this.intersectionVariables = this.unionVariables;
		this.addEmptyBindings = addEmptyBindings;
	}

	@Override
	public Message preProcessMessage(final BindingsFactoryMessage msg){
		this.bindingsFactory = msg.getBindingsFactory();
		return msg;
	}

	@Override
	public void consume(final Triple triple) {
		this.trigger();
	}

	@Override
	public Message preProcessMessage(final StartOfEvaluationMessage msg) {
		this.firstTime=true;
		return msg;
	}

	@Override
	public Message postProcessMessage(final StartOfEvaluationMessage msg) {
		this.trigger();
		return msg;
	}

	public void trigger(){
		if (this.firstTime) {
			final QueryResult ll = QueryResult.createInstance();
			if(this.addEmptyBindings) {
				ll.add(this.bindingsFactory.createInstance());
			}
			for (final OperatorIDTuple op : this.getSucceedingOperators()) {
				((Operator) op.getOperator()).processAll(ll, op.getId());
			}
			this.firstTime = false;
		}
	}

	@Override
	public String toString(){
		return super.toString()+" "+((this.addEmptyBindings)?"1 empty bindings":"1 empty queryresult");
	}
}
