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
package lupos.engine.operators.singleinput.sort;

import java.util.Iterator;

import lupos.datastructures.bindings.Bindings;
import lupos.datastructures.queryresult.QueryResult;
import lupos.datastructures.queryresult.QueryResultDebug;
import lupos.engine.operators.Operator;
import lupos.engine.operators.OperatorIDTuple;
import lupos.engine.operators.messages.ComputeIntermediateResultMessage;
import lupos.engine.operators.messages.EndOfEvaluationMessage;
import lupos.engine.operators.messages.Message;
import lupos.misc.debug.DebugStep;
import lupos.sparql1_1.Node;

/**
 * This class represents a static List which is used to sort every incoming
 * binding into a List given by this class.
 */

public class InsertionSort extends CollectionSort {

	// List to fill
	protected QueryResult list = getQR();

	public InsertionSort(final Node node) {
		super(node);
	}

	public InsertionSort() {
		// nothing to init...
	}

	/**
	 * this method checks if the static List has already been created,
	 * 
	 * @return true if the List had already bin created, false if not.
	 */

	public boolean exists() {
		return !(this.list == null);
	}

	/**
	 * Adds the given binding in correct order to the List (String comparison)
	 * 
	 * @param binding
	 *            the binding to add
	 * @return true if successfully added
	 */
	@Override
	protected QueryResult postProcess(final QueryResult bindings, final int id) {
		for (final Iterator<Bindings> iter = bindings.iterator(); iter
				.hasNext();) {
			/* if( bindings.getSorted() ){merge( bindings ); return null; } */
			final Bindings binding = iter.next();
			try {

				if (this.list.isEmpty()) {
					this.list.add(binding);
					break;
				}

				if (this.list.size() == 1) {
					if (this.comparator.compare(binding, this.list.get(0)) > 0) {
						this.list.addLast(binding);
						break;
					} else {
						this.list.addFirst(binding);
						break;
					}
				} else {

					int min = 0;
					int max = this.list.size() - 1;
					while (min != max) {

						int pos = (max + min) / 2;
						if (this.comparator.compare(this.list.get(pos), binding) >= 0) {
							if (this.comparator.compare(this.list.get(pos), binding) == 0) {
								this.list.add(pos, binding);
								getSortedQueryResult();
							}
							if (max == pos) {
								max = max--;
							} else {
								max = pos;
							}
						} else {
							if (min == pos) {
								min = ++pos;
							} else {
								min = pos;
							}
						}
					}
					if (max == this.list.size() - 1
							&& this.comparator.compare(this.list.get(max), binding) < 0) {
						this.list.add(binding);
						break;
					}
					this.list.add(max, binding);
				}
			} catch (final Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		// return getSortedQueryResult( );
		return null;
	}

	@Override
	protected QueryResult merge(final QueryResult bind) {
		final Iterator<Bindings> iterBind = bind.iterator();
		Bindings binding = iterBind.next();
		for (int i = 0; i < this.list.size(); i++) {
			if (this.comparator.compare(binding, this.list.get(i)) <= 0) {
				this.list.add(i, binding);
				i++;
				if (iterBind.hasNext())
					binding = iterBind.next();
				else
					break;
			}
		}
		while (iterBind.hasNext()) {
			this.list.add(iterBind.next());
		}
		return getSortedQueryResult();
	}

	@Override
	public Message preProcessMessage(final EndOfEvaluationMessage msg) {
		for (final Bindings b: this.list) {
			for (final OperatorIDTuple opId: this.succeedingOperators) {
				opId.processAll(b);
			}
		}
		this.list = QueryResult.createInstance();
		return msg;
	}

	@Override
	public Message preProcessMessage(final ComputeIntermediateResultMessage msg) {
		preProcessMessage(new EndOfEvaluationMessage());
		return msg;
	}

	/**
	 * @return the List in descending order
	 */
	public QueryResult getSortedQueryResult() {
		return this.list;
	}

	@Override
	public QueryResult deleteQueryResult(final QueryResult queryResult,
			final int operandID) {
		// problem: it does not count the number of occurences of a binding
		// i.e. { ?a=<a> }, { ?a=<a> } and delete { ?a=<a> } will result in
		// {} instead of { ?a=<a> }!!!!!!
		final Iterator<Bindings> itb = queryResult.oneTimeIterator();
		while (itb.hasNext())
			this.list.remove(itb.next());
		return null;
	}

	@Override
	public void deleteAll(final int operandID) {
		this.list.release();
		this.list = QueryResult.createInstance();
	}

	@Override
	protected boolean isPipelineBreaker() {
		return true;
	}
	
	@Override
	public Message preProcessMessageDebug(
			final ComputeIntermediateResultMessage msg,
			final DebugStep debugstep) {
		preProcessMessageDebug(new EndOfEvaluationMessage(), debugstep);
		return msg;
	}
	
	@Override
	public Message preProcessMessageDebug(final EndOfEvaluationMessage msg,
			final DebugStep debugstep) {
		for (final OperatorIDTuple opId: this.succeedingOperators) {
			final QueryResultDebug qrDebug = new QueryResultDebug(this.list,
					debugstep, this, opId.getOperator(), true);
			((Operator) opId.getOperator()).processAllDebug(qrDebug, opId
					.getId(), debugstep);
		}
		this.list = QueryResult.createInstance();
		return msg;
	}
}
