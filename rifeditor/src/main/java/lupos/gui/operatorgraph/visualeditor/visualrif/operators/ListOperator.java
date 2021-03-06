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
package lupos.gui.operatorgraph.visualeditor.visualrif.operators;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lupos.gui.operatorgraph.graphwrapper.GraphWrapper;
import lupos.gui.operatorgraph.visualeditor.guielements.AbstractGuiComponent;
import lupos.gui.operatorgraph.visualeditor.guielements.VisualGraph;
import lupos.gui.operatorgraph.visualeditor.operators.Operator;
import lupos.gui.operatorgraph.visualeditor.visualrif.guielements.graphs.RuleGraph;
import lupos.gui.operatorgraph.visualeditor.visualrif.guielements.graphs.VisualRIFGraph;
import lupos.gui.operatorgraph.visualeditor.visualrif.guielements.operatorPanel.ListOperatorPanel;
import lupos.gui.operatorgraph.visualeditor.visualrif.util.Term;

import lupos.misc.util.OperatorIDTuple;




public class ListOperator extends AbstractTermOperator {

	
	private boolean open = false;


	@Override
	public AbstractGuiComponent<Operator> draw(GraphWrapper gw,
			VisualGraph<Operator> parent) {
		this.panel = new ListOperatorPanel(parent, gw, this,
				 this.startNode,
				this.alsoSubClasses,this.visualRifEditor);
		return this.panel;
	}


	@Override
	public StringBuffer serializeOperator() {
	
		StringBuffer sb = new StringBuffer("");
		if (this.isChild) {

			sb.append("List( ");

			for (int i = 0; i < this.terms.size(); i++) {
				
				// Variable
				if (this.terms.get(i).isVariable()) {
					if(this.isOpen() && (this.terms.size() - i == 1)){ sb.append(" | "); }
					sb.append("?" + this.terms.get(i).getValue() + " ");
					
				} 
				
				
				// Constant + BASE
				if (this.terms.get(i).getSelectedPrefix().equals("BASE")) {
					if(this.isOpen() && (this.terms.size() - i == 1)){ sb.append(" | "); }
					sb.append("<" + this.terms.get(i).getValue() + "> ");
				} 
		
				// Constant
				if (this.terms.get(i).isConstant()){
					if(this.isOpen() && (this.terms.size() - i == 1)){ sb.append(" | "); }
					if ( this.terms.get(i).getSelectedPrefix().equals("integer") ){
						sb.append(this.terms.get(i).getValue());
					}else
					if ( this.terms.get(i).getSelectedPrefix().endsWith("#string") || this.terms.get(i).getSelectedPrefix().endsWith("#integer") ){
						String[] tmp = new String[2];
						tmp = this.terms.get(i).getSelectedPrefix().split("#");
									String iri = "\""+this.terms.get(i).getValue()+"\"^^"+tmp[0]+":"+tmp[1]+" ";

						
						sb.append(iri);
					}else
					sb.append(this.terms.get(i).getSelectedPrefix() + ":"+ this.terms.get(i).getValue() + " ");
				}
				
				
				// Uniterm
				if ( this.terms.get(i).isUniterm() ){
					if(this.isOpen() && (this.terms.size() - i == 1)){ sb.append(" | "); }
					sb.append(this.terms.get(i).getAbstractTermOperator().serializeOperator());
				}
			
				// List 
				if (this.terms.get(i).isList()){
					if(this.isOpen() && (this.terms.size() - i == 1)){ sb.append(" | "); }
					sb.append(this.terms.get(i).getAbstractTermOperator().serializeOperator());
				}
				
							
			}
			sb.append(")");

		}
		return sb;
	}

	
	public StringBuffer serializeOperatorAndTree(HashSet<Operator> visited) {
		StringBuffer sb = new StringBuffer("");
		if(!this.isChild){

		sb.append("List( ");

		for (int i = 0; i < this.terms.size(); i++) {
			
			// Variable
			if (this.terms.get(i).isVariable()) {
				if(this.isOpen() && (this.terms.size() - i == 1)){ sb.append(" | "); }
				sb.append("?" + this.terms.get(i).getValue() + " ");
				
			} 
			
			
			// Constant + BASE
			if (this.terms.get(i).getSelectedPrefix().equals("BASE")) {
				if(this.isOpen() && (this.terms.size() - i == 1)){ sb.append(" | "); }
				sb.append("<" + this.terms.get(i).getValue() + "> ");
			} 
	
			// Constant
			if (this.terms.get(i).isConstant()){
				if(this.isOpen() && (this.terms.size() - i == 1)){ sb.append(" | "); }
				if ( this.terms.get(i).getSelectedPrefix().equals("integer") ){
					sb.append(this.terms.get(i).getValue());
				}else
				if ( this.terms.get(i).getSelectedPrefix().endsWith("#string") || this.terms.get(i).getSelectedPrefix().endsWith("#integer") ){
					String[] tmp = new String[2];
					tmp = this.terms.get(i).getSelectedPrefix().split("#");
								String iri = "\""+this.terms.get(i).getValue()+"\"^^"+tmp[0]+":"+tmp[1]+" ";

					
					sb.append(iri);
				}else
				sb.append(this.terms.get(i).getSelectedPrefix() + ":"+ this.terms.get(i).getValue() + " ");
			}
			
			
			// Uniterm
			if ( this.terms.get(i).isUniterm() ){
				if(this.isOpen() && (this.terms.size() - i == 1)){ sb.append(" | "); }
				sb.append(this.terms.get(i).getAbstractTermOperator().serializeOperator());
			}
		
			// List 
			if (this.terms.get(i).isList()){
				if(this.isOpen() && (this.terms.size() - i == 1)){ sb.append(" | "); }
				sb.append(this.terms.get(i).getAbstractTermOperator().serializeOperator());
			}
			
						
		}
		sb.append(")");

		
//		if (!this.isChild && this.getSucceedingOperators().size() != 0) {
//			sb.append(" " + this.selectedClassification + " ");
//			for (OperatorIDTuple<Operator> opIDT : this
//					.getSucceedingOperators()) {
//				sb.append(opIDT.getOperator().serializeOperator());
//
//			}
//		}
		
		if (!this.isChild && this.getSucceedingElementsWithoutTermSucceedingElements().size() != 0) {
			sb.append(" " + this.selectedClassification + " ");
			for (int j = 0; j < this.getSucceedingElementsWithoutTermSucceedingElements().size(); j++) {
				sb.append(this.getSucceedingElementsWithoutTermSucceedingElements().get(j).serializeOperator());
			}
		}
		
		}
		return sb;
	}

	
	public void fromJSON(JSONObject operatorObject, ListOperator listOperator,RuleGraph parent) throws JSONException {
		
		this.setVisualRifEditor(parent.getVisualRifEditor());
		
		
		boolean isConnected = operatorObject.getBoolean("ISCONNECTED");
		
		if (isConnected) {
			JSONObject loadObject = new JSONObject();
			loadObject = (JSONObject) operatorObject.get("CONNECTEDOPERATOR");
			listOperator.setSelectedClassification((String)operatorObject.get("SELECTEDCLASSIFICTION"));
			
		if (listOperator.getSelectedClassification().equals("=")){
							boolean[] equality = {true,false,false};
							listOperator.setSelectedRadioButton(equality);
	
		}else
	
			if (listOperator.getSelectedClassification().equals("#")){
							boolean[] membership = {false,true,false};
							listOperator.setSelectedRadioButton(membership);
			
			}else		
				if (listOperator.getSelectedClassification().equals("##")){
							boolean[] subclass = {false,false, true};
							listOperator.setSelectedRadioButton(subclass);
				}
			
			
			// Constant
			if ( loadObject.get("OP TYPE").equals("ConstantOperator") ){
				ConstantOperator child = new ConstantOperator();
				child.fromJSON(loadObject, child, parent);
				child.setChild(true);

				OperatorIDTuple<Operator> oidtConst = new OperatorIDTuple<Operator> (child, 0);
				listOperator.addSucceedingOperator(oidtConst);
				
				JSONArray positionArray = loadObject.getJSONArray("POSITION");
				parent.addOperator(positionArray.getInt(0), positionArray.getInt(1),
					child);

			} // end constant
			
			
			// Variable
			if ( loadObject.get("OP TYPE").equals("VariableOperator") ){
				VariableOperator child = new VariableOperator();
				child.fromJSON(loadObject, child, parent);
				child.setChild(true);

				OperatorIDTuple<Operator> oidtVar = new OperatorIDTuple<Operator> (child, 0);
				listOperator.addSucceedingOperator(oidtVar);
				
				JSONArray positionArray = loadObject.getJSONArray("POSITION");
				parent.addOperator(positionArray.getInt(0), positionArray.getInt(1),
					child);

			} // end variable
			
			// ListOperator
			if ( loadObject.get("OP TYPE").equals("ListOperator") ){
				ListOperator child = new ListOperator();
				JSONObject termsObject = null;
				
				child.fromJSON(loadObject, child, parent);
				child.setChild(true);
				child.setConstantComboBoxEntries(this.visualRifEditor.getDocumentContainer().getActiveDocument().getDocumentEditorPane().getPrefixList());
				child.setVisualRifEditor(visualRifEditor);
				child.setOpen(loadObject.getBoolean("ISOPEN"));
		
				if( loadObject.has("TERMS") )
					termsObject = loadObject.getJSONObject("TERMS");
				
				// get savedTerms
				HashMap<String,Term> unsortedTerms = this.getSavedTerms(termsObject,child);
				
				// sort terms
				LinkedList<Term> terms =  this.sortTerms(unsortedTerms);
				
				child.setTerms(terms);
		
				

				OperatorIDTuple<Operator> oidtVar = new OperatorIDTuple<Operator> (child, 0);
				listOperator.addSucceedingOperator(oidtVar);
				
				JSONArray positionArray = loadObject.getJSONArray("POSITION");
				parent.addOperator(positionArray.getInt(0), positionArray.getInt(1),
					child);

			} // end list
			
			
			
			// UnitermOperator
			if ( loadObject.get("OP TYPE").equals("UnitermOperator") ){
				UnitermOperator child = new UnitermOperator();
				JSONObject termsObject = null;
				
				child.fromJSON(loadObject, child, (RuleGraph) parent);
				child.setChild(true);
				child.setConstantComboBoxEntries(this.visualRifEditor.getDocumentContainer().getActiveDocument().getDocumentEditorPane().getPrefixList());
				child.setVisualRifEditor(visualRifEditor);

		
				child.setConstantComboBoxEntries(this.visualRifEditor.getDocumentContainer().getActiveDocument().getDocumentEditorPane().getPrefixList());
				child.setVisualRifEditor(visualRifEditor);
				child.setTermName(loadObject.getString("TERMNAME"));
				child.getUniTermComboBox().setSelectedItem(loadObject.getString("SELECTEDPREFIX"));
				child.setSelectedPrefix(loadObject.getString("SELECTEDPREFIX"));
				child.setExternal(loadObject.getBoolean("EXTERNAL"));
				child.setNamed(loadObject.getBoolean("NAMED"));
	
		
				if( loadObject.has("TERMS") )
					termsObject = loadObject.getJSONObject("TERMS");
				
				// get savedTerms
				HashMap<String,Term> unsortedTerms = this.getSavedTerms(termsObject,child);
				
				// sort terms
				LinkedList<Term> terms =  this.sortTerms(unsortedTerms);
				
				child.setTerms(terms);
		
				

				OperatorIDTuple<Operator> oidtVar = new OperatorIDTuple<Operator> (child, 0);
				listOperator.addSucceedingOperator(oidtVar);
				
				JSONArray positionArray = loadObject.getJSONArray("POSITION");
				parent.addOperator(positionArray.getInt(0), positionArray.getInt(1),
					child);

			} // end UnitermOperator
			
			
			
		}
		
	}
	
	
	
	
	/* ***************** **
	 * Getter and Setter **
	 * ***************** */

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}


	
	




}

