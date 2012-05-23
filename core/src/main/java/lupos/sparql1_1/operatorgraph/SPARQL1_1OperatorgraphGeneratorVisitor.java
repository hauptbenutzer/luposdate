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
/* Generated By:JavaCC: Do not edit this line. SPARQL1_1ParserVisitor.java Version 5.0 */
package lupos.sparql1_1.operatorgraph;

import lupos.datastructures.items.Item;
import lupos.datastructures.items.Variable;
import lupos.engine.operators.BasicOperator;
import lupos.sparql1_1.*;
import lupos.sparql1_1.operatorgraph.helper.OperatorConnection;

public interface SPARQL1_1OperatorgraphGeneratorVisitor
{
  public void visit(ASTGroupConstraint node, OperatorConnection connection, Item graphConstraint);
  public void visit(ASTQuery node, OperatorConnection connection);
  public void visit(ASTSelectQuery node, OperatorConnection connection, Item graphConstraint);
  public void visit(ASTSelectQuery node, OperatorConnection connection);
  public void visit(ASTConstructQuery node, OperatorConnection connection);
  public void visit(ASTAskQuery node, OperatorConnection connection);
  public void visit(ASTOrderConditions node, OperatorConnection connection);
  public void visit(ASTLimit node, OperatorConnection connection);
  public void visit(ASTOffset node, OperatorConnection connection);
  public void visit(ASTLoad node, OperatorConnection connection);
  public void visit(ASTClear node, OperatorConnection connection);
  public void visit(ASTDrop node, OperatorConnection connection);
  public void visit(ASTCreate node, OperatorConnection connection);
  public void visit(ASTInsert node, OperatorConnection connection);
  public void visit(ASTDelete node, OperatorConnection connection);
  public void visit(ASTModify node, OperatorConnection connection);
  public void visit(ASTGraphConstraint node, OperatorConnection connection);
  public void visit(ASTOptionalConstraint node, OperatorConnection connection, Item graphConstraint);
  public void visit(ASTService node, OperatorConnection connection);
  public void visit(ASTMinus node, OperatorConnection connection, Item graphConstraint);
  public void visit(ASTUnionConstraint node, OperatorConnection connection, Item graphConstraint);
  public void visit(ASTFilterConstraint node, OperatorConnection connection, Item graphConstraint);
  public BasicOperator visit(ASTArbitraryOccurences node, OperatorConnection connection, Item graphConstraint, Variable subject, Variable object, Node subjectNode, Node objectNode);
  public BasicOperator visit(ASTOptionalOccurence node, OperatorConnection connection, Item graphConstraint, Variable subject, Variable object, Node subjectNode, Node objectNode);
  public BasicOperator visit(ASTArbitraryOccurencesNotZero node, OperatorConnection connection, Item graphConstraint, Variable subject, Variable object, Node subjectNode, Node objectNode);
  public BasicOperator visit(ASTGivenOccurences node, OperatorConnection connection, Item graphConstraint, Variable subject, Variable object, Node subjectNode, Node objectNode);
  public BasicOperator visit(ASTPathSequence node, OperatorConnection connection, Item graphConstraint, Variable subject, Variable object, Node subjectNode, Node objectNode);
  public BasicOperator visit(ASTPathAlternative node, OperatorConnection connection, Item graphConstraint, Variable subject, Variable object, Node subjectNode, Node objectNode);
  public BasicOperator visit(ASTNegatedPath node, OperatorConnection connection, Item graphConstraint, Variable subject, Variable object, Node subjectNode, Node objectNode);
  public BasicOperator visit(ASTInvers node, OperatorConnection connection, Item graphConstraint, Variable subject, Variable object, Node subjectNode, Node objectNode);
  public BasicOperator visit(ASTQuotedURIRef node, OperatorConnection connection, Item graphConstraint, Variable subject, Variable object, Node subjectNode, Node objectNode);
  public void visit(ASTDefaultGraph node, OperatorConnection connection);
  public void visit(ASTNamedGraph node, OperatorConnection connection);
  // for stream-based evaluation:
  public void visit(final ASTWindow node, OperatorConnection connection, Item graphConstraint);
}
