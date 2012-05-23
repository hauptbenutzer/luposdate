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
package lupos.engine.operators.singleinput.modifiers;

import java.util.Iterator;

import lupos.datastructures.bindings.Bindings;
import lupos.datastructures.queryresult.QueryResult;
import lupos.engine.operators.BasicOperator;
import lupos.engine.operators.singleinput.SingleInputOperator;

public class Limit extends SingleInputOperator {

	private int limit;
	private int pos = 0;

	/**
	 * Constructs a limit-operator
	 * 
	 * @param limit
	 *            limitation of return values
	 */
	public Limit(final int limit) {
		this.limit = limit;
		if (limit < 0) {
			System.out
					.println("Error: The value of limit has to be positive or zero!");
		}
	}

	@Override
	public void cloneFrom(final BasicOperator op) {
		limit = ((Limit) op).limit;
	}

	/**
	 * @return the given list of bindings cut to limit entries.
	 */
	@Override
	public QueryResult process(final QueryResult bindings, final int operandID) {
		if (pos >= limit || bindings.size() == 0)
			return null; // to do: close evaluation of query!
		if (pos + bindings.size() < limit) {
			pos += bindings.size();
			return bindings;
		}
		final QueryResult ret = QueryResult.createInstance();
		final Iterator<Bindings> itb = bindings.oneTimeIterator();
		while (itb.hasNext()) {
			if (pos < limit)
				ret.add(itb.next());
			else
				break;
			pos++;
		}
		return ret;
	}

	public int getLimit() {
		return limit;
	}

	@Override
	public String toString() {
		return super.toString() + " " + limit;
	}
}
