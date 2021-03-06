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
package lupos.engine.operators;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lupos.datastructures.items.Variable;
import lupos.datastructures.queryresult.QueryResult;
import lupos.datastructures.queryresult.QueryResultDebug;
import lupos.engine.operators.messages.BindingsFactoryMessage;
import lupos.engine.operators.messages.BoundVariablesMessage;
import lupos.engine.operators.messages.ComputeIntermediateResultMessage;
import lupos.engine.operators.messages.EndOfEvaluationMessage;
import lupos.engine.operators.messages.Message;
import lupos.engine.operators.messages.StartOfEvaluationMessage;
import lupos.misc.debug.DebugStep;

/**
 * All operators are derived from this class. This class provides fundamental
 * methods for all operators.
 */
public class BasicOperator implements Cloneable, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1437787386470746802L;

	/**
	 * succeedingOperators contains all succeeding operators (together with an
	 * operand number) of this operator. Let us assume that the current operator
	 * is C, another operator is A and a succeeding operator is B = A OPTIONAL
	 * C. Note that it is important to know that A is the left operand and C is
	 * the right operand of B. Then a succeeding operator of C is B with operand
	 * number 1 (A has a succeeding operator B with operand number 0).
	 */
	protected List<OperatorIDTuple> succeedingOperators = new LinkedList<OperatorIDTuple>();

	/**
	 * The preceding operators are stored in this variable.
	 */
	protected List<BasicOperator> precedingOperators = new LinkedList<BasicOperator>();

	/**
	 * There exists a message system, which allows to send messages in the
	 * operator graph. A message is only sent to the succeeding operators after
	 * all operands (except those, where this operator is the loop head of) of
	 * this operator have sent a message with the same message id. Before
	 * sending a message to the succeeding operators, the messages with the same
	 * message id are merged and processed by the current operator. After
	 * sending the message, a message is received from the succeeding operators,
	 * which is post-processed.
	 */
	protected Map<String, Map<BasicOperator, Message>> messages = new HashMap<String, Map<BasicOperator, Message>>();

	/**
	 * This variable contains all preceding operators, which have this operator
	 * as ancestor, i.e. this operator is the head of a loop.
	 *
	 * @seealso messages
	 */
	protected HashSet<BasicOperator> cycleOperands = new HashSet<BasicOperator>();

	/**
	 * This variable contains the intersection of the used variables of its
	 * preceding operators (and maybe newly bound variables).
	 */
	protected Collection<Variable> intersectionVariables;

	/**
	 * This variable contains the union of all used variables of the preceding
	 * operators (and maybe newly bound variables).
	 */
	protected Collection<Variable> unionVariables;

	/**
	 * The standard constructor
	 */
	public BasicOperator() {
		// no initializations...
	}

	/**
	 * The constructor, which sets the succeeding operators
	 *
	 * @param succeedingOperators
	 *            the succeeding operators
	 */
	public BasicOperator(final List<OperatorIDTuple> succeedingOperators) {
		this.succeedingOperators = succeedingOperators;
	}

	/**
	 * This constructor can be used to set only one succeeding operator
	 *
	 * @param succeedingOperator
	 *            the one and only succeeding operator
	 */
	public BasicOperator(final OperatorIDTuple succeedingOperator) {
		this.succeedingOperators = new LinkedList<OperatorIDTuple>();
		if (succeedingOperator != null) {
			this.succeedingOperators.add(succeedingOperator);
		}
	}

	/**
	 * Replaces this operator with a replacement operator
	 *
	 * @param replacement
	 *            The replacement operator
	 */
	public void replaceWith(final BasicOperator replacement) {
		for (final BasicOperator preOp : this.precedingOperators) {
			for (final OperatorIDTuple opid : preOp.succeedingOperators) {
				if (opid.getOperator().equals(this)) {
					opid.setOperator(replacement);
				}
			}
		}
		for (final OperatorIDTuple sucOp : this.succeedingOperators) {
			for (int i = 0; i < sucOp.getOperator().precedingOperators.size(); i++) {
				if (sucOp.getOperator().precedingOperators.get(i).equals(this)) {
					sucOp.getOperator().precedingOperators.set(i, replacement);
				}
			}
		}
	}

	/**
	 * This method can be used to set only one succeeding operator
	 *
	 * @param succeedingOperator
	 *            the one and only succeeding operator
	 */
	public void setSucceedingOperator(final OperatorIDTuple succeedingOperator) {
		this.succeedingOperators = new LinkedList<OperatorIDTuple>();
		this.succeedingOperators.add(succeedingOperator);
	}

	/**
	 * This method can be used to the succeeding operators
	 *
	 * @param succeedingOperators
	 *            list of succeeding operators
	 */
	public void setSucceedingOperators(
			final List<OperatorIDTuple> succeedingOperators) {
		this.succeedingOperators = succeedingOperators;
	}

	/**
	 * This method adds one succeeding operator
	 *
	 * @param succeedingOperator
	 *            the succeeding operator
	 */
	public void addSucceedingOperator(final OperatorIDTuple succeedingOperator) {
		this.succeedingOperators.add(succeedingOperator);
	}

	public void addSucceedingOperator(final BasicOperator succeedingOperator) {
		this.addSucceedingOperator(new OperatorIDTuple(succeedingOperator, 0));
	}

	public void addSucceedingOperator(final BasicOperator succeedingOperator, final int operandID) {
		this.addSucceedingOperator(new OperatorIDTuple(succeedingOperator, operandID));
	}

	/**
	 * This method adds all of a list of succeeding operators
	 *
	 * @param succeedingOperatorsParamter
	 *            list of succeeding operators
	 */
	public void addSucceedingOperators(final List<OperatorIDTuple> succeedingOperatorsParamter) {
		this.succeedingOperators.addAll(succeedingOperatorsParamter);
	}

	/**
	 * This method returns the succeeding operators
	 *
	 * @return the succeeding operators
	 */
	public List<OperatorIDTuple> getSucceedingOperators() {
		return this.succeedingOperators;
	}

	/**
	 * This method adds one preceding operator
	 *
	 * @param precedingOperator
	 *            the preceding operator
	 */
	public void addPrecedingOperator(final BasicOperator precedingOperator) {
		if (precedingOperator != null) {
			this.precedingOperators.add(precedingOperator);
		}
	}

	/**
	 * This method adds a collection of preceding operators
	 *
	 * @param precedingOperatorsParameter
	 *            the preceding operators
	 */
	public void addPrecedingOperators(final Collection<BasicOperator> precedingOperatorsParameter) {
		for (final BasicOperator precedingOperator : precedingOperatorsParameter) {
			this.precedingOperators.add(precedingOperator);
		}
	}

	/**
	 * This method removes the given preceding operator
	 *
	 * @param precedingOperator
	 *            the preceding operator to be removed
	 */
	public void removePrecedingOperator(final BasicOperator precedingOperator) {
		if (precedingOperator != null) {
			this.precedingOperators.remove(precedingOperator);
		}
	}

	/**
	 * This method removes the succeeding operator
	 *
	 * @param succeedingOperator
	 *            the succeeding operator to be removed
	 */
	public void removeSucceedingOperator(final BasicOperator succeedingOperator) {
		if (succeedingOperator != null) {
			boolean change = true;
			while (change) {
				change = false;
				for (final OperatorIDTuple oit : this.succeedingOperators) {
					if (oit.getOperator().equals(succeedingOperator)) {
						this.succeedingOperators.remove(oit);
						change = true;
						break;
					}
				}
			}
		}
	}

	public void removeSucceedingOperator(final OperatorIDTuple opIDT) {
		while(this.succeedingOperators.remove(opIDT)){
			// action plus break conditions is in the loop head!
		}
	}

	/**
	 * This method sets the given preceding operator as the only preceding
	 * operator
	 *
	 * @param precedingOperator
	 *            the one and only preceding operator
	 */
	public void setPrecedingOperator(final BasicOperator precedingOperator) {
		this.precedingOperators = new LinkedList<BasicOperator>();
		if (precedingOperator != null) {
			this.precedingOperators.add(precedingOperator);
		}
	}

	/**
	 * This method sets the preceding operators
	 *
	 * @param precedingOperators
	 *            the preceding operators
	 */
	public void setPrecedingOperators(
			final List<BasicOperator> precedingOperators) {
		if (precedingOperators != null) {
			this.precedingOperators = precedingOperators;
		} else {
			this.precedingOperators = new LinkedList<BasicOperator>();
		}
	}

	/**
	 * This method returns the preceding operators
	 *
	 * @return the preceding operators
	 */
	public List<BasicOperator> getPrecedingOperators() {
		return this.precedingOperators;
	}

	/**
	 * This method returns the first found preceding operator, which is connected to this operator with the given id
	 * @param id the id of the OperatorIDTuple
	 * @return the first found preceding operator, which is connected to this operator with the given id, otherwise null
	 */
	public BasicOperator getPrecedingOperatorWithID(final int id){
		for(final BasicOperator prec: this.getPrecedingOperators()){
			if(prec.getOperatorIDTuple(this).getId() == id){
				return prec;
			}
		}
		return null;
	}

	/**
	 * This method returns the first OperatorIDTuple in the list of succeeding
	 * operators, which contains the given operator
	 *
	 * @param op
	 *            the operator to besearched for in the list of succeeding
	 *            operators.
	 * @return the first OperatorIDTuple, which contains op
	 */
	public OperatorIDTuple getOperatorIDTuple(final BasicOperator op) {
		for (final OperatorIDTuple opid : this.succeedingOperators) {
			if (opid.getOperator().equals(op)) {
				return opid;
			}
		}
		return null;
	}

	/**
	 * This method replaces one OperatorIDTuple with another in the list of
	 * succeeding operators.
	 *
	 * @param oit1
	 *            the OperatorIDTuple to be replaced
	 * @param oit2
	 *            The OperatorIDTuple to be added
	 */
	public void replaceOperatorIDTuple(final OperatorIDTuple oit1,
			final OperatorIDTuple oit2) {
		this.succeedingOperators.remove(oit1);
		this.succeedingOperators.add(oit2);
	}

	/**
	 * All preceding operators are recursively (i.e. in the whole operator
	 * graph) deleted.
	 */
	public void deleteParents() {
		final SimpleOperatorGraphVisitor sogv = new SimpleOperatorGraphVisitor() {
			/**
			 *
			 */
			private static final long serialVersionUID = 6919722882143749999L;

			@Override
			public Object visit(final BasicOperator basicOperator) {
				basicOperator.precedingOperators = new LinkedList<BasicOperator>();
				return null;
			}
		};
		this.visit(sogv);
	}

	/**
	 * This method sets the preceding operators recursively in the whole
	 * operator graph: If A has the succeeding operator B, then A is added to
	 * the list of preceding operators in B.
	 */
	public void setParents() {
		final SimpleOperatorGraphVisitor sogv = new SimpleOperatorGraphVisitor() {
			/**
			 *
			 */
			private static final long serialVersionUID = -3649188246478511485L;

			@Override
			public Object visit(final BasicOperator basicOperator) {
				for (final OperatorIDTuple opid : basicOperator.succeedingOperators) {
					final BasicOperator op = opid.getOperator();
					if (!op.precedingOperators.contains(basicOperator)) {
						op.precedingOperators.add(basicOperator);
					}
				}
				return null;
			}
		};
		this.visit(sogv);
	}

	/**
	 * This method is applied whenever messages are received by this operator.
	 *
	 * @param msg
	 *            the message itself
	 * @param from
	 *            the operator from which the message has been sent
	 * @return the post-processed message after processing and forwarding the
	 *         merged message of all operands, which has been received from the
	 *         succeeding operators
	 */
	public Message receive(final Message message, final BasicOperator from) {
		Message msg = message;
		if (from != null) {
			Map<BasicOperator, Message> received = this.messages.get(msg.getId());
			if (received == null) {
				received = new HashMap<BasicOperator, Message>();
				this.messages.put(msg.getId(), received);
			}
			received.put(from, msg);

			final HashSet<BasicOperator> operatorsWithoutCycles = new HashSet<BasicOperator>();
			operatorsWithoutCycles.addAll(this.precedingOperators);
			operatorsWithoutCycles.removeAll(this.cycleOperands);

			if (!received.keySet().containsAll(operatorsWithoutCycles)) {
				return null;
			}

			if (received.keySet().containsAll(this.precedingOperators)) {
				this.messages.remove(msg.getId());
			}
			msg = msg.merge(received.values(), this);
		}
		msg = msg.preProcess(this);
		msg = this.forwardMessage(msg);
		if (msg == null) {
			return null;
		} else {
			return msg.postProcess(this);
		}
	}

	/**
	 * This method forwards the message to the succeeding operators.
	 *
	 * @param msg
	 *            the message to be forwarded
	 * @return a message received from the succeeding operators
	 */
	protected Message forwardMessage(final Message msg) {
		msg.setVisited(this);
		Message result = msg;
		for (final OperatorIDTuple opid : this.succeedingOperators) {
			if (!msg.hasVisited(opid.getOperator())) {
				final Message msg2 = msg.clone();
				result = opid.getOperator().receive(msg2, this);
			}
		}
		return result;
	}

	/**
	 * This method merges several received messages
	 *
	 * @param messagesParameter
	 *            the received messages
	 * @param msg
	 *            the last received message
	 * @return the merged message
	 */
	public Message mergeMessages(final Collection<Message> messagesParameter, final Message msg) {
		return msg;
	}

	/**
	 * This method pre-processes a merged message
	 *
	 * @param msg
	 *            the message to be pre-processed
	 * @return the pre-processed message
	 */
	public Message preProcessMessage(final Message msg) {
		return msg;
	}

	/**
	 * This method post-processes a message
	 *
	 * @param msg
	 *            the message to be post-processed
	 * @return the post-processed message
	 */
	public Message postProcessMessage(final Message msg) {
		return msg;
	}

	@SuppressWarnings("unused")
	public Message preProcessMessageDebug(final Message msg, final DebugStep debugstep) {
		return this.preProcessMessage(msg);
	}

	@SuppressWarnings("unused")
	public Message preProcessMessageDebug(final StartOfEvaluationMessage msg, final DebugStep debugstep) {
		return this.preProcessMessage(msg);
	}

	@SuppressWarnings("unused")
	public Message preProcessMessageDebug(final EndOfEvaluationMessage msg, final DebugStep debugstep) {
		return this.preProcessMessage(msg);
	}

	@SuppressWarnings("unused")
	public Message preProcessMessageDebug(final BoundVariablesMessage msg, final DebugStep debugstep) {
		return this.preProcessMessage(msg);
	}

	@SuppressWarnings("unused")
	public Message postProcessMessageDebug(final Message msg, final DebugStep debugstep) {
		return this.postProcessMessage(msg);
	}

	@SuppressWarnings("unused")
	public Message postProcessMessageDebug(final StartOfEvaluationMessage msg, final DebugStep debugstep) {
		return this.postProcessMessage(msg);
	}

	@SuppressWarnings("unused")
	public Message postProcessMessageDebug(final EndOfEvaluationMessage msg, final DebugStep debugstep) {
		return this.postProcessMessage(msg);
	}

	@SuppressWarnings("unused")
	public Message postProcessMessageDebug(final BoundVariablesMessage msg, final DebugStep debugstep) {
		return this.postProcessMessage(msg);
	}

	/**
	 * This method is for merging the BindingsFactoryMessage
	 *
	 * @param messagesParameter
	 *            the messages received from the operands of this operator
	 * @param msg
	 *            the last received message
	 * @return the merged message
	 */
	public Message mergeMessages(final Collection<Message> messagesParameter, final BindingsFactoryMessage msg) {
		return msg;
	}

	/**
	 * This method pre-processes the BindingsFactoryMessage
	 * Some operates override this method just to save the BindingsFactory in a local variable for later usage.
	 *
	 * @param msg
	 *            the message to be pre-processed
	 * @return the pre-processed message
	 */
	public Message preProcessMessage(final BindingsFactoryMessage msg) {
		return msg;
	}

	/**
	 * This method post-processes the BindingsFactoryMessage
	 *
	 * @param msg
	 *            the message to be post-processed
	 * @return the post-processed message
	 */
	public Message postProcessMessage(final BindingsFactoryMessage msg) {
		return msg;
	}

	/**
	 * This method computes the intersection and union of used variables. Note
	 * that some operators override this method.
	 *
	 * @param messagesParameter
	 *            the messages received from the operands of this operator
	 * @param msg
	 *            the last received message
	 * @return the merged message
	 */
	public Message mergeMessages(final Collection<Message> messagesParameter, final BoundVariablesMessage msg) {
		this.intersectionVariables = new HashSet<Variable>();
		this.intersectionVariables.addAll(msg.getVariables());
		for (final Message m : messagesParameter) {
			this.intersectionVariables.retainAll(((BoundVariablesMessage) m)
					.getVariables());
		}
		this.unionVariables = new HashSet<Variable>();
		for (final Message m : messagesParameter) {
			this.unionVariables.addAll(((BoundVariablesMessage) m).getVariables());
		}
		final BoundVariablesMessage msg2 = new BoundVariablesMessage(msg);
		msg2.setVariables(this.unionVariables);
		return msg2;
	}

	/**
	 * This method pre-processes the BoundVariablesMessage
	 *
	 * @param msg
	 *            the message to be pre-processed
	 * @return the pre-processed message
	 */
	public Message preProcessMessage(final BoundVariablesMessage msg) {
		return msg;
	}

	/**
	 * This method post-processes the BoundVariablesMessage
	 *
	 * @param msg
	 *            the message to be post-processed
	 * @return the post-processed message
	 */
	public Message postProcessMessage(final BoundVariablesMessage msg) {
		return msg;
	}

	/**
	 * This method pre-processes the EndOfStreamMessage
	 *
	 * @param msg
	 *            the message to be pre-processed
	 * @return the pre-processed message
	 */
	public Message preProcessMessage(final EndOfEvaluationMessage msg) {
		return msg;
	}

	/**
	 * This method merges several received messages
	 *
	 * @param messages
	 *            the received messages
	 * @param msg
	 *            the last received message
	 * @return the merged message
	 */
	@SuppressWarnings("unused")
	public Message mergeMessages(final Collection<Message> messagesParameter, final EndOfEvaluationMessage msg) {
		return msg;
	}

	/**
	 * This method pre-processes the StartOfStreamMessage
	 *
	 * @param msg
	 *            the message to be pre-processed
	 * @return the pre-processed message
	 */
	public Message preProcessMessage(final StartOfEvaluationMessage msg) {
		return msg;
	}

	/**
	 * This method post-processes the StartOfStreamMessage
	 *
	 * @param msg
	 *            the message to be post-processed
	 * @return the post-processed message
	 */
	public Message postProcessMessage(final StartOfEvaluationMessage msg) {
		return msg;
	}

	/**
	 * This method merges several received messages
	 *
	 * @param messages
	 *            the received messages
	 * @param msg
	 *            the last received message
	 * @return the merged message
	 */
	@SuppressWarnings("unused")
	public Message mergeMessages(final Collection<Message> messagesParameter, final StartOfEvaluationMessage msg) {
		return msg;
	}

	public Message preProcessMessage(final ComputeIntermediateResultMessage msg) {
		return msg;
	}

	/**
	 * All implementation classes that replace a super class which already holds
	 * relevant state, have to override this method to copy that state from the
	 * superclass. Make sure to call super so that children and parents don't
	 * get lost
	 *
	 * @param op
	 *            The BasicOperator to copy state from
	 */
	public void cloneFrom(final BasicOperator op) {
		if (op.succeedingOperators != null) {
			this.succeedingOperators = new LinkedList<OperatorIDTuple>();
			this.succeedingOperators.addAll(op.succeedingOperators);
		}
		if (op.precedingOperators != null) {
			this.precedingOperators = new LinkedList<BasicOperator>();
			this.precedingOperators.addAll(op.precedingOperators);
		}
		this.messages = op.messages;
		if (op.intersectionVariables != null) {
			this.intersectionVariables = new LinkedList<Variable>();
			this.intersectionVariables.addAll(op.intersectionVariables);
		}
		if (op.unionVariables != null) {
			this.unionVariables = new LinkedList<Variable>();
			this.unionVariables.addAll(op.unionVariables);
		}
	}

	/**
	 * The clone method to clone the current operator
	 *
	 * @return the cloned operator
	 */
	@Override
	public BasicOperator clone() {
		BasicOperator result = null;
		try {
			result = (BasicOperator) super.clone();
			result.cloneFrom(this);
		} catch (final CloneNotSupportedException ex) {
			// just return null;
		}
		return result;
	}

	/**
	 * This method clones not only the current operator, but all of its
	 * succeeding operators recursively.
	 *
	 * @return the cloned operator
	 */
	public BasicOperator deepClone() {
		final BasicOperator op = (BasicOperator) this.visit(new SimpleOperatorGraphVisitor() {
			/**
			 *
			 */
			private static final long serialVersionUID = -2374279115052843835L;

			final Map<BasicOperator, BasicOperator> clones = new HashMap<BasicOperator, BasicOperator>();

			@Override
			public Object visit(final BasicOperator basicOperator) {
				BasicOperator cloneCurrent;
				if (this.clones.containsKey(basicOperator)) {
					cloneCurrent = this.clones.get(basicOperator);
				} else {
					cloneCurrent = basicOperator.clone();
					this.clones.put(basicOperator, cloneCurrent);
				}

				final LinkedList<OperatorIDTuple> newSucc = new LinkedList<OperatorIDTuple>();

				for (final OperatorIDTuple opid : basicOperator.succeedingOperators) {
					BasicOperator clone = null;
					if (this.clones.containsKey(opid.getOperator())) {
						clone = this.clones.get(opid.getOperator());
					} else {
						clone = opid.getOperator().clone();
						this.clones.put(opid.getOperator(), clone);
					}
					newSucc.add(new OperatorIDTuple(clone, opid.getId()));
				}

				cloneCurrent.setSucceedingOperators(newSucc);
				return cloneCurrent;
			}

		});

		op.deleteParents();
		op.setParents();

		return op;
	}

	/**
	 * This method sets the union of used variables
	 *
	 * @param unionVariables
	 *            the union of used variables
	 */
	public void setUnionVariables(final Collection<Variable> unionVariables) {
		this.unionVariables = unionVariables;
	}

	/**
	 * This method returns the union of used variables
	 *
	 * @return the union of used variables
	 */
	public Collection<Variable> getUnionVariables() {
		return this.unionVariables;
	}

	/**
	 * This method sets the intersection of used variables.
	 *
	 * @param sortCriterium
	 *            the intersection of used variables
	 */
	public void setIntersectionVariables(final Collection<Variable> sortCriterium) {
		this.intersectionVariables = sortCriterium;
	}

	/**
	 * This method returns the intersection of used variables.
	 *
	 * @return the intersection of used variables
	 */
	public Collection<Variable> getIntersectionVariables() {
		return this.intersectionVariables;
	}

	/**
	 * This method sends a message.
	 *
	 * @param msg
	 *            the message to be sent
	 * @return the received (and post-processed) message from the succeeding
	 *         operators
	 */
	public Message sendMessage(final Message msg) {
		final Message msgResult = this.forwardMessage(msg.preProcess(this));
		// postprocess anyway one message, does not matter if the resultant msg is null or not to allow processing right after initialization
		if(msgResult == null) {
			return msg.postProcess(this);
		} else {
			return msgResult.postProcess(this);
		}
	}

	/**
	 * This method starts processing a simple visitor in the whole operator
	 * graph. Depth-first visit is applied.
	 *
	 * @param visitor
	 *            The visitor to be applied to each node in the operator graph
	 * @return The object retrieved from processing the visitor on this
	 *         operator.
	 */
	public Object visit(final SimpleOperatorGraphVisitor visitor) {
		return this.visit(visitor, new HashSet<BasicOperator>());
	}

	/**
	 * This is a helper method of the method Object
	 * visit(SimpleOperatorGraphVisitor visitor)
	 *
	 * @param visitor
	 *            The visitor to be applied to each node
	 * @param hs
	 *            the already visited operators
	 * @return The object retrieved from processing the visitor on this
	 *         operator.
	 */
	private Object visit(final SimpleOperatorGraphVisitor visitor,
			final HashSet<BasicOperator> hs) {
		if (hs.contains(this)) {
			return null;
		}
		hs.add(this);
		final Object result = visitor.visit(this);
		for (final OperatorIDTuple opid : this.succeedingOperators) {
			opid.getOperator().visit(visitor, hs);
		}
		return result;
	}

	/**
	 * This method starts processing a simple visitor in the whole operator
	 * graph. Depth-first visit is applied.
	 * This visitor stops when the first result is not null.
	 *
	 * @param visitor
	 *            The visitor to be applied to each node in the operator graph
	 * @return The object retrieved from processing the visitor on this
	 *         operator.
	 */
	public Object visitAndStop(final SimpleOperatorGraphVisitor visitor) {
		return this.visitAndStop(visitor, new HashSet<BasicOperator>());
	}

	/**
	 * This is a helper method of the method Object
	 * visit(SimpleOperatorGraphVisitor visitor)
	 *
	 * @param visitor
	 *            The visitor to be applied to each node
	 * @param hs
	 *            the already visited operators
	 * @return The object retrieved from processing the visitor on this
	 *         operator.
	 */
	private Object visitAndStop(final SimpleOperatorGraphVisitor visitor,
			final HashSet<BasicOperator> hs) {
		if (hs.contains(this)) {
			return null;
		}

		hs.add(this);

		Object result = visitor.visit(this);

		if(result != null) {
			return result;
		}

		for (final OperatorIDTuple opid : this.succeedingOperators) {
			result = opid.getOperator().visitAndStop(visitor, hs);

			if(result != null) {
				return result;
			}
		}

		return result;
	}

	/**
	 * This method starts processing a visitor in the whole operator graph.
	 * Depth-first visit is applied. In comparison to visit(final
	 * SimpleOperatorGraphVisitor visitor), the used visitor allows to have an
	 * additional parameter for data for the visit-method.
	 *
	 * @param visitor
	 *            The visitor to be applied to each node in the operator graph
	 * @return The object retrieved from processing the visitor on this
	 *         operator.
	 */
	public<T> Object visit(final OperatorGraphVisitor<T> visitor, final T data) {
		return this.visit(visitor, data, new HashSet<BasicOperator>());
	}

	/**
	 * This is a helper method of the method Object
	 * visit(SimpleOperatorGraphVisitor visitor)
	 *
	 * @param visitor
	 *            The visitor to be applied to each node
	 * @param hs
	 *            the already visited operators
	 * @return The object retrieved from processing the visitor on this
	 *         operator.
	 */
	private<T> Object visit(final OperatorGraphVisitor<T> visitor, final T data,
			final HashSet<BasicOperator> hs) {
		if (hs.contains(this)) {
			return null;
		}
		hs.add(this);
		final T result = visitor.visit(this, data);
		for (final OperatorIDTuple opid : this.succeedingOperators) {
			opid.getOperator().visit(visitor, result, hs);
		}
		return result;
	}

	/**
	 * This method detects cycles and stores the last operator of a cycle in the
	 * loop head.
	 */
	public void detectCycles() {
		final SimpleOperatorGraphVisitor deleteOldDetectedCycles = new SimpleOperatorGraphVisitor() {
			/**
			 *
			 */
			private static final long serialVersionUID = 5010278675133006511L;

			@Override
			public Object visit(final BasicOperator basicOperator) {
				basicOperator.cycleOperands.clear();
				return null;
			}
		};
		this.visit(deleteOldDetectedCycles);
		this.detectCycles(new LinkedList<BasicOperator>(),
				new HashSet<BasicOperator>());
	}

	/**
	 * This method is a helper method of detectCycles.
	 *
	 * @seealso detectCycles
	 */
	private void detectCycles(final List<BasicOperator> hs,
			final Set<BasicOperator> visited) {
		// System.out.println(this);
		if (visited.contains(this)) {
			return;
		}
		hs.add(this);
		visited.add(this);
		for (final OperatorIDTuple opid : this.succeedingOperators) {
			final int i = hs.indexOf(opid.getOperator());
			if (i >= 0) {
				opid.getOperator().cycleOperands.add(this);
				// cycleOperands.add(opid.getOperator());
				for (int j = i + 1; j < hs.size(); j++) {
					hs.get(j).cycleOperands.add(hs.get(j - 1));
				}
			}
		}
		for (final OperatorIDTuple opid : this.succeedingOperators) {
			opid.getOperator().detectCycles(hs, visited);
		}
		hs.remove(this);
	}

	public HashSet<BasicOperator> getCycleOperands() {
		return this.cycleOperands;
	}

	public void setCycleOperands(final HashSet<BasicOperator> cycleOperands) {
		this.cycleOperands = cycleOperands;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	@SuppressWarnings("unused")
	public String toString(final lupos.rdf.Prefix prefixInstance) {
		return this.toString();
	}


	public void dump(final String prefix, final HashSet<BasicOperator> visited) {
		if (visited.contains(this)) {
			return;
		}

		visited.add(this);

		System.out.println(prefix + this.toString());

		for (final OperatorIDTuple childIDT : this.succeedingOperators) {
			final BasicOperator child = childIDT.getOperator();

			child.dump(prefix + "-", visited);
		}
	}

	public void removeFromOperatorGraph() {
		for (final OperatorIDTuple oidtuple : this.getSucceedingOperators()) {
			oidtuple.getOperator().removePrecedingOperator(this);
			oidtuple.getOperator().addPrecedingOperators(
					this.getPrecedingOperators());
		}
		for (final BasicOperator prec : this.getPrecedingOperators()) {
			prec.removeSucceedingOperator(this);
			prec.addSucceedingOperators(this.getSucceedingOperators());
		}
	}

	public void removeFromOperatorGraphWithoutConnectingPrecedingWithSucceedingOperators() {
		for (final OperatorIDTuple oidtuple : this.getSucceedingOperators()) {
			oidtuple.getOperator().removePrecedingOperator(this);
		}
		this.succeedingOperators.clear();
		for (final BasicOperator prec : this.getPrecedingOperators()) {
			prec.removeSucceedingOperator(this);
		}
		this.precedingOperators.clear();
	}

	protected Message forwardMessageDebug(final Message msg,
			final DebugStep debugstep) {
		msg.setVisited(this);
		Message result = msg;
		for (final OperatorIDTuple opid : this.succeedingOperators) {
			if (!msg.hasVisited(opid.getOperator())) {
				final Message msg2 = msg.clone();
				result = opid.getOperator().receiveDebug(msg2, this, debugstep);
			}
		}
		return result;
	}

	@SuppressWarnings("unused")
	public Message preProcessMessageDebug(final ComputeIntermediateResultMessage msg, final DebugStep debugstep) {
		return msg;
	}

	public Message receiveDebug(final Message message, final BasicOperator from, final DebugStep debugstep) {
		Message msg = message;
		if (from != null) {
			debugstep.stepMessage(from, this, msg);
			Map<BasicOperator, Message> received = this.messages.get(msg.getId());
			if (received == null) {
				received = new HashMap<BasicOperator, Message>();
				this.messages.put(msg.getId(), received);
			}
			received.put(from, msg);

			final HashSet<BasicOperator> operatorsWithoutCycles = new HashSet<BasicOperator>();
			operatorsWithoutCycles.addAll(this.precedingOperators);
			operatorsWithoutCycles.removeAll(this.cycleOperands);

			if (!received.keySet().containsAll(operatorsWithoutCycles)) {
				return null;
			}

			if (received.keySet().containsAll(this.precedingOperators)) {
				this.messages.remove(msg.getId());
			}
			msg = msg.merge(received.values(), this);
		}
		msg = msg.preProcessDebug(this, debugstep);
		msg = this.forwardMessageDebug(msg, debugstep);
		if (msg == null) {
			return null;
		} else {
			return msg.postProcessDebug(this, debugstep);
		}
	}

	public Message sendMessageDebug(final Message msg, final DebugStep debugstep) {
		final Message msgResult = this.forwardMessageDebug(msg.preProcessDebug(this, debugstep), debugstep);
		// postprocess anyway one message, does not matter if the resultant msg is null or not to allow processing right after initialization
		if(msgResult == null) {
			return msg.postProcessDebug(this, debugstep);
		} else {
			return msgResult.postProcessDebug(this, debugstep);
		}
	}

	/**
	 * Processes the given queryresult at all of its succeeding operators...
	 *
	 * @param qr the queryresult ro be processed at the succeeding operators
	 */
	public void processAtSucceedingOperators(final QueryResult qr){
		for (final OperatorIDTuple succOperator : this.succeedingOperators) {
			((Operator) succOperator.getOperator()).processAll(qr, succOperator.getId());
		}
	}

	/**
	 * Processes the given queryresult at all of its succeeding operators logging debug steps...
	 *
	 * @param qr the queryresult ro be processed at the succeeding operators
	 * @param debugstep the object for logging debug steps
	 */
	public void processAtSucceedingOperatorsDebug(final QueryResult qr, final DebugStep debugstep){
		for (final OperatorIDTuple succOperator : this.succeedingOperators) {
			((Operator) succOperator.getOperator()).processAll(new QueryResultDebug(qr, debugstep, this, succOperator.getOperator(), true), succOperator.getId());
		}
	}

	@SuppressWarnings("unused")
	public boolean remainsSortedData(final Collection<Variable> sortCriterium){
		return false;
	}

	public Collection<Variable> transformSortCriterium(final Collection<Variable> sortCriterium){
		return sortCriterium;
	}

	/**
	 * just to enable access to the root node in a SubgraphContainer in module distributed for BasicOperatorByteArray...
	 *
	 * @return the root node of the contained graph
	 */
	public BasicOperator getContainedGraph(){
		return null;
	}
}
