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
package lupos.datastructures.sort.run.memorysort;

import java.util.Iterator;
import java.util.List;

import lupos.datastructures.dbmergesortedds.heap.Heap;
import lupos.datastructures.dbmergesortedds.tosort.ToSort.TOSORT;
import lupos.datastructures.sort.run.Run;
import lupos.datastructures.sort.run.Runs;

public class MemorySortRuns implements Runs {
	
	protected final TOSORT sortType;
	protected final int numberOfElements;
	protected final boolean set;
	
	public MemorySortRuns(final TOSORT sortType, final int numberOfElements, final boolean set){
		this.sortType = sortType;
		this.numberOfElements = numberOfElements;
		this.set = set;
	}

	@Override
	public Run merge(final List<Run> runs, final boolean inmemory) {
		@SuppressWarnings("unchecked")
		final Iterator<String>[] iterators = new Iterator[runs.size()];
		// a heap is used to get every time the smallest under the current elements of the different runs
		// (this allows to have logarithmical complexity (concerning the number of runs) for getting the next smallest element)
		final Heap<Container> heap = Heap.createInstance(runs.size(), true, Heap.HEAPTYPE.OPTIMIZEDSEQUENTIAL);
		for(int i=0; i<runs.size(); i++){
			iterators[i] = runs.get(i).iterator();
			// assuming the different runs are non-empty!
			heap.add(new Container(i, iterators[i].next()));
		}
		Iterator<String> iterator = new Iterator<String>(){
			
			@Override
			public boolean hasNext() {
				return !heap.isEmpty();
			}

			@Override
			public String next() {
				if(this.hasNext()){
					Container next = heap.pop();
					if(iterators[next.fromRun].hasNext()){
						heap.add(new Container(next.fromRun, iterators[next.fromRun].next()));
					}
					return next.content;
				} else {
					return null;
				}						
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}			
		};
		
		return (inmemory)? new MemorySortSortedRun(iterator, this.set) : new MemorySortSortedRunOnDisk(iterator, this.set);
	}

	@Override
	public Run createRun() {		
		return new MemorySortRun(this.sortType, this.numberOfElements, this.set);
	}

	/**
	 * just to store from which run the element (i.e., content) is, such that the next element from this run can be read when this element is consumed... 
	 */
	public static class Container implements Comparable<Container>{
		
		public final int fromRun;
		public final String content;
		
		public Container(final int fromRun, final String content){
			this.fromRun = fromRun;
			this.content = content;
		}

		@Override
		public int compareTo(Container container) {
			return this.content.compareTo(container.content);
		}		
	}
}
