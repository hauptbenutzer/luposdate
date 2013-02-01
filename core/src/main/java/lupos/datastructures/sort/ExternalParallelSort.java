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
package lupos.datastructures.sort;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import lupos.datastructures.dbmergesortedds.tosort.ToSort.TOSORT;
import lupos.datastructures.items.Triple;
import lupos.datastructures.items.literal.Literal;
import lupos.datastructures.parallel.BoundedBuffer;
import lupos.datastructures.sort.run.Run;
import lupos.datastructures.sort.run.Runs;
import lupos.datastructures.sort.run.memorysort.MemorySortRuns;
import lupos.datastructures.sort.run.trie.TrieBagRuns;
import lupos.datastructures.sort.run.trie.TrieSetRuns;
import lupos.engine.evaluators.CommonCoreQueryEvaluator;
import lupos.engine.operators.tripleoperator.TripleConsumer;
import lupos.misc.TimeInterval;

/**
 * Class for sorting a collection of strings or the RDF terms of RDF data using.
 * This sorting algorithm is optimized for sorting in computers with large main memories.
 * Initial runs (up to a certain size) are generated by threads in main memory.
 * The initial runs are merged in main memory (in an own thread) and only intermediately stored on disk if there is no free main memory any more.
 * Finally all remaining runs are merged into one run...
 * 
 * Runs can be trie sets, bags (using trie merge), or arrays (using normal merge or with duplicate elimination)
 */
public class ExternalParallelSort {
	
	public static enum SORTTYPE {
		SET {
			@Override
			public Runs createRuns(final int NUMBER_ELEMENTS_IN_INITIAL_RUNS){
				return new TrieSetRuns();
			}
		}, 
		BAG {
			@Override
			public Runs createRuns(final int NUMBER_ELEMENTS_IN_INITIAL_RUNS){
				return new TrieBagRuns();
			}
		}, 
		MERGESORTSET {
			@Override
			public Runs createRuns(final int NUMBER_ELEMENTS_IN_INITIAL_RUNS){
				return new MemorySortRuns(TOSORT.MERGESORT, NUMBER_ELEMENTS_IN_INITIAL_RUNS, true);
			}
		}, 
		MERGESORTBAG {
			@Override
			public Runs createRuns(final int NUMBER_ELEMENTS_IN_INITIAL_RUNS){
				return new MemorySortRuns(TOSORT.MERGESORT, NUMBER_ELEMENTS_IN_INITIAL_RUNS, false);
			}
		}, 
		PARALLELMERGESORTSET {
			@Override
			public Runs createRuns(final int NUMBER_ELEMENTS_IN_INITIAL_RUNS){
				return new MemorySortRuns(TOSORT.PARALLELMERGESORT, NUMBER_ELEMENTS_IN_INITIAL_RUNS, true);
			}
		}, 
		PARALLELMERGESORTBAG {
			@Override
			public Runs createRuns(final int NUMBER_ELEMENTS_IN_INITIAL_RUNS){
				return new MemorySortRuns(TOSORT.PARALLELMERGESORT, NUMBER_ELEMENTS_IN_INITIAL_RUNS, false);
			}
		}, 
		QUICKSORTSET {
			@Override
			public Runs createRuns(final int NUMBER_ELEMENTS_IN_INITIAL_RUNS){
				return new MemorySortRuns(TOSORT.QUICKSORT, NUMBER_ELEMENTS_IN_INITIAL_RUNS, true);
			}
		}, 
		QUICKSORTBAG {
			@Override
			public Runs createRuns(final int NUMBER_ELEMENTS_IN_INITIAL_RUNS){
				return new MemorySortRuns(TOSORT.QUICKSORT, NUMBER_ELEMENTS_IN_INITIAL_RUNS, false);
			}
		}, 
		HEAPSORTSET {
			@Override
			public Runs createRuns(final int NUMBER_ELEMENTS_IN_INITIAL_RUNS){
				return new MemorySortRuns(TOSORT.HEAPSORT, NUMBER_ELEMENTS_IN_INITIAL_RUNS, true);
			}
		}, 
		HEAPSORTBAG {
			@Override
			public Runs createRuns(final int NUMBER_ELEMENTS_IN_INITIAL_RUNS){
				return new MemorySortRuns(TOSORT.HEAPSORT, NUMBER_ELEMENTS_IN_INITIAL_RUNS, false);
			}
		};
		
		public abstract Runs createRuns(final int NUMBER_ELEMENTS_IN_INITIAL_RUNS);
	}
	
	// garbage collection cycles are once per minute per default, with
	// java -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 ...
	// it can be set to whatever you like (here 1 hour)...
	
	/**
	 * Main method to start sorting the RDF terms of RDF data
	 * @param args command line arguments
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Sorting Strings or RDF terms of large RDF data in computers with large main memory...");
		System.out.println("Call:");
		System.out.println("java lupos.datastructures.sort.ExternalParallelSort DATAFILE FORMAT [SORTTYPE NUMBER_INITIAL_RUN_GENERATION_THREADS NUMBER_ELEMENTS_IN_INITIAL_RUNS NUMBER_OF_RUNS_TO_JOIN FREE_MEMORY_LIMIT]");
		System.out.println("FORMAT can be STRING for a large collection of strings in one file, or an RDF format like N3");
		System.out.println("SORTTYPE can be "+Arrays.toString(SORTTYPE.values()));
		System.out.println("Example:");
		System.out.println("java -server -XX:+UseParallelGC -XX:+AggressiveOpts -Xms60G -Xmx60G lupos.test.ExternalParallelMergeSort SomeFiles.txt MULTIPLEN3 BAG 8 1000 2 100000");
		if(!(args.length==2 || args.length==7)){
			System.err.println("Wrong number of arguments!");
			return;
		}
		Date start = new Date();
		System.out.println("\nStart processing:"+start+"\n");
		final int NUMBER_ELEMENTS_IN_INITIAL_RUNS = Integer.parseInt(args[4]);
		ExternalParallelSort sorter = (args.length==2)?
				new ExternalParallelSort():					
				new ExternalParallelSort(Integer.parseInt(args[3]), NUMBER_ELEMENTS_IN_INITIAL_RUNS, Integer.parseInt(args[5]), Long.parseLong(args[6]), SORTTYPE.valueOf(args[2]).createRuns(NUMBER_ELEMENTS_IN_INITIAL_RUNS));
				
		System.out.println("\nParameters:\n" + sorter.parametersToString() + "\n");
		sorter.sort(new BufferedInputStream(new FileInputStream(args[0])), args[1]);
		Date end = new Date();
		System.out.println("\nEnd processing:"+end);		
		System.out.println("\nDuration:   " + (end.getTime() - start.getTime())/1000.0 + " seconds\n          = "+new TimeInterval(start, end));
		
		System.out.println("\nNumber of waits of initial run generators:" + sorter.getNumberOfWaitsOfInitialRunGenerator());
		System.out.println("Number of waits of merger thread:" + sorter.getNumberOfWaitsOfMerger());
	}
	
	/**
	 * Constructor to set a lot of parameters
	 * @param NUMBER_INITIAL_RUN_GENERATION_THREADS
	 * @param NUMBER_ELEMENTS_IN_INITIAL_RUNS
	 * @param NUMBER_OF_RUNS_TO_JOIN
	 * @param FREE_MEMORY_LIMIT
	 */
	public ExternalParallelSort(final int NUMBER_INITIAL_RUN_GENERATION_THREADS, final int NUMBER_ELEMENTS_IN_INITIAL_RUNS, final int NUMBER_OF_RUNS_TO_JOIN, final long FREE_MEMORY_LIMIT, final Runs runs){
		this.NUMBER_INITIAL_RUN_GENERATION_THREADS = NUMBER_INITIAL_RUN_GENERATION_THREADS;
		this.NUMBER_ELEMENTS_IN_INITIAL_RUNS = NUMBER_ELEMENTS_IN_INITIAL_RUNS;
		this.NUMBER_OF_RUNS_TO_JOIN = NUMBER_OF_RUNS_TO_JOIN;
		this.FREE_MEMORY_LIMIT = FREE_MEMORY_LIMIT;
		this.runs = runs;
	}
	
	/**
	 * Default constructor using default parameters...
	 */
	public ExternalParallelSort(){
		this(8, 1000, 2, 100000, new TrieBagRuns());
	}
	
	private final int NUMBER_INITIAL_RUN_GENERATION_THREADS;
	
	private final int NUMBER_ELEMENTS_IN_INITIAL_RUNS;
	
	private final int NUMBER_OF_RUNS_TO_JOIN;
	
	private final long FREE_MEMORY_LIMIT;

	private final LinkedList<Run> runsOnDisk = new LinkedList<Run>();
	
	private int numberOfWaitsOfInitialRunGenerator = 0;
	
	private int numberOfWaitsOfMerger = 0;
	
	private final Runs runs;
	
	/**
	 * sorting algorithm
	 * @param dataFiles the data
	 * @param format the format of the data
	 * @throws Exception if something went wrong
	 */
	public void sort(final InputStream dataFiles, String format) throws Exception {
		final BoundedBuffer<String> buffer = new BoundedBuffer<String>();
		
		// initialize threads for generating initial runs
		final BoundedBuffer<Run> initialRunsLevel0 = new BoundedBuffer<Run>(Math.max(this.NUMBER_INITIAL_RUN_GENERATION_THREADS, this.NUMBER_OF_RUNS_TO_JOIN)*3);
		
		InitialRunGenerator[] initialRunGenerationThreads = new InitialRunGenerator[this.NUMBER_INITIAL_RUN_GENERATION_THREADS];
		
		for(int i=0; i<this.NUMBER_INITIAL_RUN_GENERATION_THREADS; i++){
			initialRunGenerationThreads[i] = new InitialRunGenerator(buffer, initialRunsLevel0);
			initialRunGenerationThreads[i].start();
		}
		
		// start the merge thread...
		final ArrayList<LinkedList<Run>> levels = new ArrayList<LinkedList<Run>>();
		Merger merger = new Merger(initialRunsLevel0, levels);
		merger.start();
		
		if(format.toUpperCase().compareTo("STRING")==0){
			// read in the strings...
			try{
				BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(dataFiles)));
				String strLine;
				//Read File Line By Line
				while ((strLine = br.readLine()) != null)   {
					buffer.put(strLine);
				}
				//Close the input stream
				br.close();
			}catch (Exception e){
				System.err.println(e);
				e.printStackTrace();
			}
		} else {		
			// now parse the data...
			CommonCoreQueryEvaluator.readTriples(format, dataFiles, 
					new TripleConsumer(){
						@Override
						public void consume(Triple triple) {
							for(Literal literal: triple){
								try {
									buffer.put(literal.originalString());
								} catch (InterruptedException e) {
									System.err.println(e);
									e.printStackTrace();
								}
							}
						}
			});
		}
		
		// signal that the all data is parsed (and nothing will be put into the buffer any more) 
		buffer.endOfData();
		
		// wait for threads to finish generating initial runs...  
		for(int i=0; i<this.NUMBER_INITIAL_RUN_GENERATION_THREADS; i++){
			try {
				initialRunGenerationThreads[i].join();
			} catch (InterruptedException e) {
				System.err.println(e);
				e.printStackTrace();
			}
		}
		
		// start remaining merging phase...
		// signal no initial run will be generated any more
		initialRunsLevel0.endOfData();
		
		// wait for merger thread!
		try {
			merger.join();
		} catch (InterruptedException e) {
			System.err.println(e);
			e.printStackTrace();
		}
		
		// final merge phase: merge all runs (which are still in memory or stored on disk)
		// first collect all remaining runs!
		LinkedList<Run> runstoBeFinallyMerged = new LinkedList<Run>(this.runsOnDisk);
		for(LinkedList<Run> level: levels){
			runstoBeFinallyMerged.addAll(level);
		}
		Run result; 
		if(runstoBeFinallyMerged.size()==0){
			System.err.println("No runs there to be merged!");
			return;
		} else if(runstoBeFinallyMerged.size()==1){
			// already merge in main memory?
			result = runstoBeFinallyMerged.get(0);
		} else {
			result = this.runs.merge(runstoBeFinallyMerged, false);
		}
		
		// just access all elements in the bag by iterating one time through
		Iterator<String> it = result.iterator();
		int i=0;
		while(it.hasNext()){
			it.next();
			i++;
			// System.out.println((++i)+":"+it.next());
		}
		System.out.println("Number of sorted RDF terms:"+i);
		System.out.println("Only if bags are used: There should be " + (i/3) + " triples read!");
	}
	
	/**
	 * @return a string describing the current parameters of this object
	 */
	public String parametersToString(){
		return 
			"NUMBER_INITIAL_RUN_GENERATION_THREADS:" + this.NUMBER_INITIAL_RUN_GENERATION_THREADS +			
			"\nNUMBER_ELEMENTS_IN_INITIAL_RUNS      :" + this.NUMBER_ELEMENTS_IN_INITIAL_RUNS +			
			"\nNUMBER_OF_RUNS_TO_JOIN               :" + this.NUMBER_OF_RUNS_TO_JOIN +			
			"\nFREE_MEMORY_LIMIT                    :" + this.FREE_MEMORY_LIMIT;
	}
	
	public int getNumberOfWaitsOfInitialRunGenerator() {
		return this.numberOfWaitsOfInitialRunGenerator;
	}

	public int getNumberOfWaitsOfMerger() {
		return this.numberOfWaitsOfMerger;
	}

	/**
	 * Thread for generating the initial runs... 
	 */
	public class InitialRunGenerator extends Thread {
		
		private final BoundedBuffer<String> buffer;
		private Run run;
		private final BoundedBuffer<Run> initialRunsLevel0;
		
		public InitialRunGenerator(final BoundedBuffer<String> buffer, final BoundedBuffer<Run> initialRunsLevel0){
			this.buffer = buffer;
			this.run = ExternalParallelSort.this.runs.createRun();
			this.initialRunsLevel0 = initialRunsLevel0;
		}
		
		@Override
		public void run(){
			try {
				int numberInRun = 0;
				while(true) {
					String item = this.buffer.get();
					if(item==null){
						break;
					}
					if(this.run.add(item)){
						numberInRun++;
					}
					if(numberInRun >= ExternalParallelSort.this.NUMBER_ELEMENTS_IN_INITIAL_RUNS){
						// this run exceeds the limit for number of elements and becomes therefore a new initial run
						this.finishInitialRun();
						numberInRun =0;
					}
				}
				// the (not full) run becomes an initial run because all RDF terms have been consumed
				this.finishInitialRun();
			} catch (InterruptedException e) {
				System.err.println(e);
				e.printStackTrace();
			}
		}
		
		public void finishInitialRun() throws InterruptedException{
			if(!this.run.isEmpty()){
				Run sortedRun = this.run.sort();
				if(this.initialRunsLevel0.isCurrentlyFull()){
					ExternalParallelSort.this.numberOfWaitsOfInitialRunGenerator++;
				}
				this.initialRunsLevel0.put(sortedRun);
				this.run = ExternalParallelSort.this.runs.createRun();
			}
		}
	}
	
	public class Merger extends Thread {
		
		private final BoundedBuffer<Run> initialRunsLevel0;
		private final ArrayList<LinkedList<Run>> levels;
		
		public Merger(final BoundedBuffer<Run> initialRunsLevel0, final ArrayList<LinkedList<Run>> levels){
			this.initialRunsLevel0 = initialRunsLevel0; 
			this.levels = levels;
		}
		
		@Override
		public void run(){

			try {
				while(true){
					// get as many runs to merge as specified
					if(this.initialRunsLevel0.size()<ExternalParallelSort.this.NUMBER_OF_RUNS_TO_JOIN){
						ExternalParallelSort.this.numberOfWaitsOfMerger++;
					}
					Object[] bagsToMerge = this.initialRunsLevel0.get(ExternalParallelSort.this.NUMBER_OF_RUNS_TO_JOIN, ExternalParallelSort.this.NUMBER_OF_RUNS_TO_JOIN);
					
					this.checkMemory();
					
					if(bagsToMerge!=null && bagsToMerge.length>0){
						Run run = null;
						if(bagsToMerge.length>1){
							ArrayList<Run> toBeMerged = new ArrayList<Run>(bagsToMerge.length);
							for(Object bag: bagsToMerge){
								toBeMerged.add((Run) bag);
							}
							run = ExternalParallelSort.this.runs.merge(toBeMerged, true);
						} else if(bagsToMerge.length==1) {
							run = (Run) bagsToMerge[0];
						}
						this.addToLevel(0, run);
					} else {
						// no new initial runs any more => merge thread finishes
						break;
					}
				}
			} catch (InterruptedException e) {
				System.err.println(e);
				e.printStackTrace();
			}
		}
		
		/**
		 * This method adds merged runs (at a certain level) and merges the runs at this level if possible.
		 * Using levels has the effect that only ties of similar sizes are merged (and not large ones with small ones, which is not so fast (? to be checked!) because of an asymmetric situation)
		 * @param addLevel the level to which the merged run is added
		 * @param toBeAdded the merged run to be added
		 */
		public void addToLevel(final int addLevel, final Run toBeAdded) {
			
			this.checkMemory();
			
			while(this.levels.size()<=addLevel){
				this.levels.add(new LinkedList<Run>());
			}
			LinkedList<Run> level = this.levels.get(addLevel);
			level.add(toBeAdded);
			while(level.size()>=ExternalParallelSort.this.NUMBER_OF_RUNS_TO_JOIN){
				// we should merge the runs at this level
				
				ArrayList<Run> listOfRunsToBeMerged = new ArrayList<Run>(ExternalParallelSort.this.NUMBER_OF_RUNS_TO_JOIN);
				
				while(listOfRunsToBeMerged.size()<ExternalParallelSort.this.NUMBER_OF_RUNS_TO_JOIN){
					listOfRunsToBeMerged.add(level.remove());					
				}
				
				Run resultOfMerge = ExternalParallelSort.this.runs.merge(listOfRunsToBeMerged, true);
				
				// put the merged run to one level higher (and recursively merge there the runs if enough are there)
				this.addToLevel(addLevel + 1, resultOfMerge);				
			}
			
			this.checkMemory();
		}
		
		/**
		 * check free memory and intermediately store one of the biggest runs on disk if there is not enough memory any more 
		 */
		public void checkMemory() {
			// is there still enough memory available?
			if(Runtime.getRuntime().freeMemory()<ExternalParallelSort.this.FREE_MEMORY_LIMIT){
				// get one of the biggest runs for storing it on disk and free it in memory...
				int levelnr=this.levels.size();
				do {
					levelnr--;
				} while(levelnr>0 && this.levels.get(levelnr).size()==0);
				if(levelnr==0){
					System.err.println("ExternalParallelMergeSort: Heap space to low or FREE_MEMORY_LIMIT to high...");
					return;
				}
				LinkedList<Run> lastLevel = this.levels.get(levelnr);
				Run runOnDisk = lastLevel.remove().swapRun();
				System.gc();
				ExternalParallelSort.this.runsOnDisk.add(runOnDisk);
			}
		}
	}
}