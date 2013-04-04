/**
 * Copyright (c) 2013, Institute of Information Systems (Sven Groppe), University of Luebeck
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
package lupos.distributedendpoints.storage;

import java.util.List;

import lupos.datastructures.items.Triple;
import lupos.datastructures.queryresult.QueryResult;
import lupos.distributed.storage.distributionstrategy.BlockUpdatesStorageWithDistributionStrategy;
import lupos.distributed.storage.distributionstrategy.DistributionStrategyEnum;
import lupos.distributed.storage.distributionstrategy.HashingDistributionPipe;
import lupos.distributed.storage.distributionstrategy.IDistribution;
import lupos.distributedendpoints.storage.util.EndpointManagement;
import lupos.distributedendpoints.storage.util.QueryBuilder;
import lupos.engine.operators.tripleoperator.TriplePattern;

/**
 * This class contains the storage layer for our distributed SPARQL endpoint query evaluator.
 * This class handles the communication with the SPARQL endpoints during data manipulation and distributed querying.
 *
 * The data is distributed according to the distribution strategy given as parameter to the constructor.
 * Only relevant endpoints are asked during answering a given query.
 */
public class Storage_DE_DistributionStrategy extends BlockUpdatesStorageWithDistributionStrategy<Integer> {

	/**
	 *  for managing the registered endpoints and submitting queries to them
	 */
	protected final EndpointManagement endpointManagement;

	/**
	 * Constructor: The endpoint management is initialized (which reads in the configuration file with registered endpoints)
	 *
	 * @param distribution The distribution strategy to be used
	 */
	public Storage_DE_DistributionStrategy(final IDistribution<Integer> distribution) {
		this(new EndpointManagement(), distribution);
	}

	/**
	 * Constructor to set the distribution strategy as well as the endpoint management
	 *
	 * @param endpointManagement the endpoint management to be used
	 * @param distribution the distribution strategy to be used
	 */
	private Storage_DE_DistributionStrategy(final EndpointManagement endpointManagement, final IDistribution<Integer> distribution) {
		super(distribution);
		this.endpointManagement = endpointManagement;
	}

	/**
	 * Creates an instance of Storage_DE_DistributionStrategy based on a given distribution.
	 * The keys of the distribution are additionally transformed into integer values by hashing (and modulo calculation with the number of endpoints)
	 * @param distribution the distribution strategy
	 * @return an instance of Storage_DE_DistributionStrategy
	 */
	public static<K> Storage_DE_DistributionStrategy createInstance(final IDistribution<K> distribution){
		final EndpointManagement endpointManagement = new EndpointManagement();
		final IDistribution<Integer> outer_distribution = new HashingDistributionPipe<K>(distribution, endpointManagement.numberOfEndpoints());
		return new Storage_DE_DistributionStrategy(endpointManagement, outer_distribution);
	}

	/**
	 * Creates an instance of Storage_DE_DistributionStrategy based on a given distribution strategy.
	 * @param strategy the distribution strategy to be used
	 * @return an instance of Storage_DE_DistributionStrategy
	 */
	public static Storage_DE_DistributionStrategy createInstance(final DistributionStrategyEnum strategy){
		return Storage_DE_DistributionStrategy.createInstance(strategy.createInstance());
	}

	@Override
	public void storeBlock(final Integer key, final List<Triple> triples) {
		this.endpointManagement.submitSPARULQuery(QueryBuilder.buildInsertQuery(triples), key);
	}

	@Override
	public boolean containsTripleAfterAdding(final Integer key, final Triple triple) {
		return !this.endpointManagement.submitSPARQLQuery(QueryBuilder.buildQuery(triple), key).isEmpty();
	}

	@Override
	public void removeAfterAdding(final Integer key, final Triple triple) {
		this.endpointManagement.submitSPARULQuery(QueryBuilder.buildDeleteQuery(triple), key);
	}

	@Override
	public QueryResult evaluateTriplePatternAfterAdding(final Integer key, final TriplePattern triplePattern) {
		return this.endpointManagement.submitSPARQLQuery(QueryBuilder.buildQuery(triplePattern), key);
	}

	@Override
	public QueryResult evaluateTriplePatternAfterAdding(final TriplePattern triplePattern) {
		// in case of non-supported triple patterns ask all endpoints for their results!
		return this.endpointManagement.submitSPARQLQuery(QueryBuilder.buildQuery(triplePattern));
	}

	@Override
	public void blockInsert() {
		super.blockInsert();
		this.endpointManagement.waitForThreadPool();
	}
}