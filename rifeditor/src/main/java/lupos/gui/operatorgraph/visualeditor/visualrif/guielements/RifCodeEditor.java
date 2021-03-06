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
package lupos.gui.operatorgraph.visualeditor.visualrif.guielements;







import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import lupos.gui.anotherSyntaxHighlighting.LinePainter;
import lupos.gui.anotherSyntaxHighlighting.LuposDocument;
import lupos.gui.anotherSyntaxHighlighting.LuposDocumentReader;
import lupos.gui.operatorgraph.visualeditor.visualrif.util.TextLineNumber;
import lupos.gui.operatorgraph.visualeditor.visualrif.util.VisualRifJTextPane;

import org.json.JSONException;
import org.json.JSONObject;





public class RifCodeEditor  {


	private VisualRifJTextPane tp_rifInput;
	private LinePainter lp_rifInput;
	private JTextArea ta_rifInputErrors;

	private JScrollPane rifInputSP;

	public RifCodeEditor(){
		super();

		final LuposDocument document = new LuposDocument();
		this.tp_rifInput = new VisualRifJTextPane(document);
		this.tp_rifInput.setFont(new Font("Courier", Font.PLAIN, 12));
		this.tp_rifInput.setTabWidth(4);
		document.init(lupos.gui.anotherSyntaxHighlighting.javacc.RIFParser.createILuposParser(new LuposDocumentReader(document)), true, 100);
		this.setRifInputSP(new JScrollPane(this.tp_rifInput));
		final TextLineNumber tln = new TextLineNumber(this.tp_rifInput);
		this.rifInputSP.setRowHeaderView( tln );
	}




	public void insertText(final int position, final String text){
		final StringBuilder oldtext = new StringBuilder(this.tp_rifInput.getText());
		oldtext.insert(position, text);
		final String newText = oldtext.toString();
		this.tp_rifInput.setText(newText);
	}

	public JSONObject toJSON() throws JSONException {
		final JSONObject saveObject = new JSONObject();



		saveObject.put("RIF CODE", this.tp_rifInput.getText());





		return saveObject;
	}

	public void fromJSON(final JSONObject loadObject) {
		try {
			this.tp_rifInput.setText(loadObject.getString("RIF CODE"));
		} catch (final JSONException e) {
			System.err.println(e);
			e.printStackTrace();
		}

	}


	public VisualRifJTextPane getTp_rifInput() {
		return this.tp_rifInput;
	}

	public void setTp_rifInput(final VisualRifJTextPane tp_rifInput) {
		this.tp_rifInput = tp_rifInput;
	}

	public LinePainter getLp_rifInput() {
		return this.lp_rifInput;
	}

	public void setLp_rifInput(final LinePainter lp_rifInput) {
		this.lp_rifInput = lp_rifInput;
	}


	public JTextArea getTa_rifInputErrors() {
		return this.ta_rifInputErrors;
	}


	public void setTa_rifInputErrors(final JTextArea ta_rifInputErrors) {
		this.ta_rifInputErrors = ta_rifInputErrors;
	}


	public JScrollPane getRifInputSP() {
		return this.rifInputSP;
	}


	public void setRifInputSP(final JScrollPane rifInputSP) {
		this.rifInputSP = rifInputSP;
	}










}