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
package lupos.datastructures.smallerinmemorylargerondisk;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

import lupos.datastructures.items.literal.Literal;
import lupos.datastructures.paged_dbbptree.DBBPTree;
import lupos.datastructures.paged_dbbptree.node.nodedeserializer.StandardNodeDeSerializer;

public class ReachabilityMap<K extends Comparable<K> & Serializable, V extends Set<Literal> & Serializable> extends MapImplementation<K,V> {

	private int elementsInMemory = 0;

	public ReachabilityMap(){
		super();
	}

	@Override
	public void clear() {
		super.clear();
		this.elementsInMemory = 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V put(final K arg0, final V arg1) {
		if(this.memoryMap.containsKey(arg0)){
			this.elementsInMemory -= this.memoryMap.remove(arg0).size();
		}
		if(this.elementsInMemory+arg1.size()<MAXMEMORYMAPENTRIES){
			this.elementsInMemory += arg1.size();
			return this.memoryMap.put(arg0,arg1);
		}
		if (this.diskMap == null){
			final Entry<K, V> entry=this.memoryMap.entrySet().iterator().next();
			try {
				System.out.println("Writing to disk");
				this.diskMap = new DBBPTree<K, V>(100000, 100000, new StandardNodeDeSerializer<K, V>((Class<? extends K>)entry.getKey().getClass(),(Class<? extends V>) entry.getValue().getClass()));
			} catch (final IOException e) {
				System.err.println(e);
				e.printStackTrace();
			}
		}
		return this.diskMap.put(arg0, arg1);
	}

	@Override
	public V remove(final Object arg0) {
		final V v = super.remove(arg0);
		if(v!=null){
			this.elementsInMemory -= v.size();
		}
		return v;
	}
}
