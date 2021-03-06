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
package lupos.rif.operator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import lupos.datastructures.bindings.Bindings;
import lupos.datastructures.queryresult.QueryResult;
import lupos.engine.operators.index.BasicIndexScan;
import lupos.engine.operators.index.Dataset;
import lupos.engine.operators.index.Indices;
import lupos.engine.operators.index.Root;
import lupos.misc.debug.DebugStep;

public class InitializeDatasetIndexScan extends BasicIndexScan {
	private final Collection<BindableIndexScan> listenerIndexes = new ArrayList<BindableIndexScan>();

	public InitializeDatasetIndexScan(final Root root) {
		super(root);
		this.triplePatterns = Arrays.asList();
	}

	public void addBindableIndex(final BindableIndexScan index) {
		this.listenerIndexes.add(index);
	}

	public boolean isEmpty() {
		return this.listenerIndexes.isEmpty();
	}

	@Override
	public QueryResult process(final Dataset dataset) {
		for (final BindableIndexScan index : this.listenerIndexes) {
			index.process(dataset);
		}
		return null;
	}

	public QueryResult processDebug(final int opt, final Dataset dataset,
			final DebugStep debugstep) {
		for (final BindableIndexScan index : this.listenerIndexes) {
			index.startProcessingDebug(dataset, debugstep);
		}
		return null;
	}


	@Override
	public QueryResult join(final Indices indices, final Bindings bindings) {
		return null;
	}

	@Override
	public String toString() {
		return "DataSetIndex";
	}

	@Override
	public boolean joinOrderToBeOptimized(){
		return false;
	}
}