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
/* Generated By:JJTree: Do not edit this line. ASTAggregation.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package lupos.sparql1_1;

import java.util.Iterator;

import lupos.datastructures.bindings.Bindings;
import lupos.engine.operators.singleinput.NotBoundException;
import lupos.engine.operators.singleinput.TypeErrorException;
import lupos.engine.operators.singleinput.ExpressionEvaluation.EvaluationVisitor;

public
class ASTAggregation extends SimpleNode {

	public enum TYPE {COUNT, SUM, MIN, MAX, AVG, SAMPLE, GROUP_CONCAT};
	
	private TYPE type;
	
	private boolean distinct = false;
	
  public ASTAggregation(int id) {
    super(id);
  }

  public ASTAggregation(SPARQL1_1Parser p, int id) {
    super(p, id);
  }
  
  public void setDistinct(){
	  distinct=true;
  }

  public boolean isDistinct(){
	  return distinct;
  }
  
  public void setTYPE(TYPE type){
	  this.type=type;
  }
  
  public TYPE getTYPE(){
	  return type;
  }
  
  public String toString(){
	  return type.toString() + (distinct?" DISTINCT":"");
  }

  /** Accept the visitor. **/
    public String accept(lupos.optimizations.sparql2core_sparql.SPARQL1_1ParserVisitorStringGenerator visitor) {
    return visitor.visit(this);
  }

  public Object jjtAccept(SPARQL1_1ParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Object accept(EvaluationVisitor visitor, Bindings b, Object data) throws NotBoundException, TypeErrorException {
	    return visitor.evaluate(this, b, data);
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Object applyAggregation(EvaluationVisitor visitor, Iterator<? extends Object> values){
	  switch(type){
	  default:
	  case COUNT:
		  return visitor.applyAggregationCOUNT(values);
	  case SUM:
		  return visitor.applyAggregationSUM(values);
	  case MIN:
		  return visitor.applyAggregationMIN(values);
	  case MAX:
		  return visitor.applyAggregationMAX(values);
	  case AVG:
		  return visitor.applyAggregationAVG(values);
	  case SAMPLE:
		  return visitor.applyAggregationSAMPLE(values);
	  case GROUP_CONCAT:
		  final String separator;
		  if(this.jjtGetNumChildren()==1){
			  separator = " "; 
		  } else {
			  String s = ((ASTStringLiteral)this.jjtGetChild(1)).getStringLiteral();
			  separator = s.substring(1, s.length()-1);
		  }
		  return visitor.applyAggregationGROUP_CONCAT(values, separator);
	  }
  }
}
/* JavaCC - OriginalChecksum=619ad62842466c526143b32f4ac44927 (do not edit this line) */
