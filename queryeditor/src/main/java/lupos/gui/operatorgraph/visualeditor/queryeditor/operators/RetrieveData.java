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
package lupos.gui.operatorgraph.visualeditor.queryeditor.operators;

import java.util.HashSet;
import java.util.LinkedList;

import lupos.gui.operatorgraph.prefix.Prefix;
import lupos.gui.operatorgraph.visualeditor.operators.Operator;
import lupos.gui.operatorgraph.visualeditor.queryeditor.guielements.RetrieveDataPanel;
import lupos.gui.operatorgraph.visualeditor.util.ModificationException;
import lupos.misc.util.OperatorIDTuple;
import lupos.sparql1_1.SPARQL1_1Parser;

public abstract class RetrieveData extends Operator {
	private LinkedList<String> fromList = new LinkedList<String>();
	private LinkedList<String> fromNamedList = new LinkedList<String>();
	protected Prefix prefix;


	protected RetrieveData(Prefix prefix) {
		this.prefix = prefix;
	}


	public void addFromItem(String fromItem) throws ModificationException {
		try {
			SPARQL1_1Parser.parseSourceSelector(fromItem, this.prefix.getPrefixNames());

			this.fromList.add(fromItem);
		}
		catch(Throwable t) {
			this.handleParseError(t);
		}
	}

	public void setFromItem(int index, String fromItem) throws ModificationException {
		try {
			// new element...
			if(this.fromList.size() == index) {
				this.fromList.add("");
			}

			// parse new value
			SPARQL1_1Parser.parseSourceSelector(fromItem, this.prefix.getPrefixNames());

			// remove old value...
			if(this.fromList.get(index) != null) {
				this.fromList.remove(index);
			}

			// add new value...
			this.fromList.add(index, fromItem);
		}
		catch(Throwable t) {
			this.handleParseError(t);
		}
	}

	public void removeFromItem(int index) {
		if(index == this.fromList.size()) {
			return;
		}

		this.fromList.remove(index);
	}

	public LinkedList<String> getFromList() {
		return this.fromList;
	}

	public void clearFromList() {
		this.fromList = new LinkedList<String>();
	}


	public void addFromNamedItem(String fromNamedItem) throws ModificationException {
		try {
			SPARQL1_1Parser.parseSourceSelector(fromNamedItem, this.prefix.getPrefixNames());

			this.fromNamedList.add(fromNamedItem);
		}
		catch(Throwable t) {
			this.handleParseError(t);
		}
	}

	public void setFromNamedItem(int index, String fromNamedItem) throws ModificationException {
		try {
			// new element...
			if(this.fromNamedList.size() == index) {
				this.fromNamedList.add("");
			}

			// parse new value
			SPARQL1_1Parser.parseSourceSelector(fromNamedItem, this.prefix.getPrefixNames());

			// remove old value...
			if(this.fromNamedList.get(index) != null) {
				this.fromNamedList.remove(index);
			}

			// add new value...
			this.fromNamedList.add(index, fromNamedItem);
		}
		catch(Throwable t) {
			this.handleParseError(t);
		}
	}

	public void removeFromNamedItem(int index) {
		if(index == this.fromNamedList.size())
			return;

		this.fromNamedList.remove(index);
	}

	public LinkedList<String> getFromNamedList() {
		return this.fromNamedList;
	}

	public void clearFromNamedList() {
		this.fromNamedList = new LinkedList<String>();
	}


	public void prefixAdded() {
		((RetrieveDataPanel) this.panel).prefixAdded();
	}

	public void prefixModified(String oldPrefix, String newPrefix) {
		((RetrieveDataPanel) this.panel).prefixModified(oldPrefix, newPrefix);
	}

	public void prefixRemoved(String prefix, String namespace) {
		((RetrieveDataPanel) this.panel).prefixRemoved(prefix, namespace);
	}


	public StringBuffer serializeOperator() {
		StringBuffer ret = new StringBuffer();

		ret.append(this.getClass().getSimpleName().toUpperCase() + " ");

		return ret;
	}

	public StringBuffer serializeDatasetClause() {
		StringBuffer ret = new StringBuffer();

		// FROM
		if(((RetrieveDataPanel) this.panel).getElementStatus("from")) {
			for(String fromElement : this.fromList) {
				ret.append("FROM " + this.prefix.add(fromElement) + "\n");
			}
		}

		// FROM NAMED
		if(((RetrieveDataPanel) this.panel).getElementStatus("fromNamed")) {
			for(String fromNamedElement : this.fromNamedList) {
				ret.append("FROM NAMED " + this.prefix.add(fromNamedElement) + "\n");
			}
		}

		return ret;
	}

	public StringBuffer serializeDatasetClauseAndWhereClause(HashSet<Operator> visited) {
		StringBuffer ret = this.serializeDatasetClause();

		// WHERE
		ret.append("WHERE {\n");

		for(OperatorIDTuple<Operator> opIDT : this.succeedingOperators) {
			ret.append(opIDT.getOperator().serializeOperatorAndTree(visited));
		}

		ret.append("}\n");

		return ret;
	}
	
	@Override
	public String getXPrefID(){
		return "queryEditor_style_retrievedata";
	}
}