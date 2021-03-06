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
package lupos.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.plaf.FontUIResource;

import lupos.autocomplete.gui.JTextPanePreparer;
import lupos.autocomplete.strategies.StrategyManager;
import lupos.datastructures.items.literal.LiteralFactory;
import lupos.datastructures.items.literal.URILiteral;
import lupos.datastructures.queryresult.QueryResult;
import lupos.engine.evaluators.CommonCoreQueryEvaluator;
import lupos.engine.evaluators.JenaQueryEvaluator;
import lupos.engine.evaluators.MemoryIndexQueryEvaluator;
import lupos.engine.evaluators.QueryEvaluator;
import lupos.engine.evaluators.QueryEvaluator.DEBUG;
import lupos.engine.evaluators.RDF3XQueryEvaluator;
import lupos.engine.evaluators.SesameQueryEvaluator;
import lupos.engine.evaluators.StreamQueryEvaluator;
import lupos.engine.operators.BasicOperator;
import lupos.engine.operators.application.CollectRIFResult;
import lupos.engine.operators.application.IterateOneTimeThrough;
import lupos.engine.operators.index.Indices;
import lupos.engine.operators.singleinput.Result;
import lupos.engine.operators.singleinput.federated.FederatedQueryBitVectorJoin;
import lupos.engine.operators.singleinput.federated.FederatedQueryBitVectorJoinNonStandardSPARQL;
import lupos.gui.DebugViewerCreator.RulesGetter;
import lupos.gui.anotherSyntaxHighlighting.LANGUAGE;
import lupos.gui.anotherSyntaxHighlighting.LinePainter;
import lupos.gui.anotherSyntaxHighlighting.LuposDocument;
import lupos.gui.anotherSyntaxHighlighting.LuposDocumentReader;
import lupos.gui.anotherSyntaxHighlighting.LuposJTextPane;
import lupos.gui.anotherSyntaxHighlighting.javacc.RIFParser;
import lupos.gui.anotherSyntaxHighlighting.javacc.SPARQLParser;
import lupos.gui.anotherSyntaxHighlighting.javacc.TurtleParser;
import lupos.gui.debug.EvaluationDemoToolBar;
import lupos.gui.debug.ShowResult;
import lupos.gui.operatorgraph.graphwrapper.GraphWrapperBasicOperator;
import lupos.gui.operatorgraph.viewer.Viewer;
import lupos.gui.operatorgraph.viewer.ViewerPrefix;
import lupos.gui.operatorgraph.visualeditor.dataeditor.DataEditor;
import lupos.gui.operatorgraph.visualeditor.dataeditor.IDataEditor;
import lupos.gui.operatorgraph.visualeditor.dataeditor.datageneralizer.CondensedViewToolBar;
import lupos.gui.operatorgraph.visualeditor.dataeditor.datageneralizer.CondensedViewViewer;
import lupos.gui.operatorgraph.visualeditor.queryeditor.AdvancedQueryEditor;
import lupos.gui.operatorgraph.visualeditor.queryeditor.IQueryEditor;
import lupos.gui.operatorgraph.visualeditor.visualrif.VisualRifEditor;
import lupos.misc.FileHelper;
import lupos.misc.debug.BasicOperatorByteArray;
import lupos.optimizations.logical.rules.DebugContainer;
import lupos.rif.BasicIndexRuleEvaluator;
import lupos.rif.datatypes.RuleResult;
import lupos.sparql1_1.Node;
import lupos.sparql1_1.ParseException;
import lupos.sparql1_1.TokenMgrError;
import lupos.sparql1_1.operatorgraph.ServiceApproaches;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.rio.RDFParseException;

import xpref.IXPref;
import xpref.XPref;
import xpref.datatypes.BooleanDatatype;
import xpref.datatypes.CollectionDatatype;
import xpref.datatypes.ColorDatatype;
import xpref.datatypes.FontDatatype;
import xpref.datatypes.IntegerDatatype;

import com.hp.hpl.jena.n3.turtle.TurtleParseException;
import com.hp.hpl.jena.query.QueryParseException;

public class Demo_Applet extends JApplet implements IXPref, IDataEditor, IQueryEditor {
	// set webdemo to false in the case that you want to run it under eclipse
	// webdemo=true => example files are read from the current jar-file
	protected enum DEMO_ENUM {
		ECLIPSE, TUTORIAL, TUTORIAL2, PROJECT_DEMO, LOCALONEJAR
	}

	private static final long serialVersionUID = -2726848841473438879L;

	protected final String TAB_TITLE_RULES = "RIF rules";
	protected final String TAB_TITLE_QUERY = "SPARQL query";
	protected final String TAB_TITLE_DATA = "RDF data";
	protected final String TAB_TITLE_RESULT = "Evaluation result";

	protected DEMO_ENUM webdemo = DEMO_ENUM.ECLIPSE;
	protected String PATH_QUERIES;
	protected String PATH_RULES;
	protected String PATH_DATA;
	protected LuposJTextPane tp_queryInput;
	protected LuposJTextPane tp_rifInput;
	protected LuposJTextPane tp_dataInput;
	protected LinePainter lp_queryInput;
	protected LinePainter lp_rifInput;
	protected LinePainter lp_dataInput;
	protected Color lp_color;
	protected JTextArea ta_rifInputErrors;
	protected JTextArea ta_queryInputErrors;
	protected JTextArea ta_dataInputErrors;
	protected JComboBox cobo_evaluator;
	protected JPanel resultpanel = null;
	protected String query = "";
	protected String data = "";
	protected ViewerPrefix prefixInstance = null;
	protected BooleanReference usePrefixes = new BooleanReference(true);
	protected DebugViewerCreator debugViewerCreator = null;
	protected List<DebugContainer<BasicOperatorByteArray>> ruleApplications = null;
	protected List<DebugContainer<BasicOperatorByteArray>> ruleApplicationsForMaterialization = null;
	protected DebugViewerCreator materializationInfo = null;
	protected RuleResult errorsInOntology = null;
	protected String inferenceRules = null;
	protected JPanel masterpanel = null;
	protected JTabbedPane tabbedPane_globalMainpane = null;
	protected boolean isApplet = false;
	protected JFrame frame = null;
	protected XPref preferences;
	protected JScrollPane rifInputSP;
	protected JScrollPane queryInputSP;
	protected JScrollPane dataInputSP;
	protected Font defaultFont = null;
	protected Collection<URILiteral> defaultGraphs;
	protected Demo_Applet myself;
	protected JButton bt_evalDemo;
	protected JButton bt_evaluate;
	protected JButton bt_MeasureExecutionTimes;
	protected JButton bt_rifEvalDemo;
	protected JButton bt_rifEvaluate;
	protected JButton bt_rifMeasureExecutionTimes;
	protected Viewer operatorGraphViewer = null;
	protected JComboBox comboBox_sparqlInference;
	protected JComboBox comboBox_sparqlInferenceMaterialization;
	protected JComboBox comboBox_sparqlInferenceGenerated;
	protected JCheckBox checkBox_sparqlInferenceCheckInconsistency;
	protected QueryResult[] resultQueryEvaluator;

	/**
	 * The following code is just for the possibility to register new query evaluators, which can be also used in this demo...
	 */
	private static List<lupos.misc.Triple<String, Class<? extends QueryEvaluator<Node>>, boolean[]>> registeredEvaluators = new LinkedList<lupos.misc.Triple<String, Class<? extends QueryEvaluator<Node>>, boolean[]>>();

	public static void registerEvaluator(final String evaluatorName, final Class<? extends QueryEvaluator<Node>> evaluatorClass){
		registeredEvaluators.add(new lupos.misc.Triple<String, Class<? extends QueryEvaluator<Node>>, boolean[]>(evaluatorName, evaluatorClass, new boolean[]{true, true, true, true}));
	}


	public static void registerEvaluator(final String evaluatorName, final Class<? extends QueryEvaluator<Node>> evaluatorClass, final boolean[] enabled){
		registeredEvaluators.add(new lupos.misc.Triple<String, Class<? extends QueryEvaluator<Node>>, boolean[]>(evaluatorName, evaluatorClass, enabled));
	}

	{
		final boolean[] allEnabled = new boolean[]{true, true, true, true};
		final boolean[] partlyEnabled = new boolean[]{true, false, true, true};
		Demo_Applet.registerEvaluator("MemoryIndex", MemoryIndexQueryEvaluator.class, allEnabled);
		Demo_Applet.registerEvaluator("RDF3X", RDF3XQueryEvaluator.class, allEnabled);
		Demo_Applet.registerEvaluator("Stream", StreamQueryEvaluator.class, allEnabled);
		Demo_Applet.registerEvaluator("Jena", JenaQueryEvaluator.class, partlyEnabled);
		Demo_Applet.registerEvaluator("Sesame", SesameQueryEvaluator.class, partlyEnabled);
	}

	private int getCaseIndex(){
		if (!this.isApplet && this.webdemo != DEMO_ENUM.ECLIPSE) {
			if (this.webdemo == DEMO_ENUM.LOCALONEJAR) {
				return 0;
			} else {
				return 1;
			}
		} else if (this.webdemo == DEMO_ENUM.LOCALONEJAR) {
			return 2;
		} else {
			return 3;
		}
	}

	protected String[] getEvaluators() {
		// started with Java Web Start? Java Web start has a more restrictive
		// rights management, i.e. Jena and Sesame do not work with Java Web Start...
		final int caseIndex = this.getCaseIndex();
		final LinkedList<String> evals = new LinkedList<String>();
		for(final lupos.misc.Triple<String, Class<? extends QueryEvaluator<Node>>, boolean[]> entry: Demo_Applet.registeredEvaluators){
			if(entry.getThird()[caseIndex]){
				evals.add(entry.getFirst());
			}
		}
		return evals.toArray(new String[0]);
	}

	protected Class<? extends QueryEvaluator<Node>> getEvaluatorClass(final int index) {
		final int caseIndex = this.getCaseIndex();
		final Iterator<lupos.misc.Triple<String, Class<? extends QueryEvaluator<Node>>, boolean[]>> entryIt = Demo_Applet.registeredEvaluators.iterator();
		for(int k=0; true; k++){
			lupos.misc.Triple<String, Class<? extends QueryEvaluator<Node>>, boolean[]> entry = entryIt.next();
			while(!entry.getThird()[caseIndex]){
				entry = entryIt.next();
			}
			if(k==index){
				return entry.getSecond();
			}
		}
	}

	public static void main(final String args[]) {
		final Demo_Applet applet = new Demo_Applet();
		if (args.length > 0) {
			Demo_Applet.startDemoAsApplication(args[0], applet);
		} else {
			Demo_Applet.startDemoAsApplication(applet);
		}
	}

	public static void startDemoAsApplication(final Demo_Applet applet){
		Demo_Applet.startDemoAsApplication(null, applet);
	}

	public static void startDemoAsApplication(final String type, final Demo_Applet applet){
		if (type!=null) {
			applet.setType(type);
		}

		final JPanel panel = applet.initialise();
		applet.frame = new JFrame();

		applet.frame.setIconImage(getIcon(applet.webdemo));

		if (applet.webdemo == DEMO_ENUM.TUTORIAL
				|| applet.webdemo == DEMO_ENUM.TUTORIAL2) {
			applet.frame.setTitle("SPARQL Tutorial using LUPOSDATE");
		} else {
			applet.frame.setTitle("LUPOSDATE Demonstration");
		}

		applet.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		applet.frame.setContentPane(panel);
		applet.frame.setSize(800, 905);
		applet.frame.setLocationRelativeTo(null);

		applet.preferencesChanged();

		applet.frame.setVisible(true);
	}

	protected static Image getIcon(final DEMO_ENUM webdemo) {
		if (webdemo != DEMO_ENUM.ECLIPSE) {
			return new ImageIcon(Demo_Applet.class.getResource("/icons/demo.gif")).getImage();
		} else {
			return new ImageIcon(Demo_Applet.class.getResource("/icons/demo.gif").getFile()).getImage();
		}
	}

	/**
	 * Main Method.
	 */
	@Override
	public void init() {
		this.isApplet = true;

		this.setType(this.getParameter("type"));

		this.setSize(800, 905);

		final JPanel panel = this.initialise();

		this.add(panel);

		this.preferencesChanged();
	}

	protected void setType(final String type) {
		if (type != null) {
			if (type.toLowerCase().compareTo("demo") == 0) {
				this.webdemo = DEMO_ENUM.PROJECT_DEMO;
			} else if (type.toLowerCase().compareTo("tutorial") == 0) {
				this.webdemo = DEMO_ENUM.TUTORIAL;
			} else if (type.toLowerCase().compareTo("tutorial2") == 0) {
				this.webdemo = DEMO_ENUM.TUTORIAL2;
			} else if (type.toLowerCase().compareTo("eclipse") == 0) {
				this.webdemo = DEMO_ENUM.ECLIPSE;
			} else if (type.toLowerCase().compareTo("localonejar") == 0) {
				this.webdemo = DEMO_ENUM.LOCALONEJAR;
			}
		}
	}

	public JPanel initialise() {
		try {
			this.myself = this;

			if (this.isApplet) {
				System.out.println("starting as applet...");
			} else {
				System.out.println("starting as program...");
			}

			if (this.webdemo != DEMO_ENUM.ECLIPSE) {
				this.preferences = XPref.getInstance(Demo_Applet.class.getResource("/preferencesMenu.xml"));
			} else {
				this.preferences = XPref.getInstance(new URL("file:"+Demo_Applet.class.getResource("/preferencesMenu.xml").getFile()));
			}

			this.preferences.registerComponent(this);

			this.usePrefixes = new BooleanReference(BooleanDatatype.getValues("applet_usePrefixes").get(0).booleanValue());

			this.PATH_QUERIES = "/sparql/";

			this.PATH_RULES = "/rif/";

			this.PATH_DATA = "/data/";

			this.masterpanel = new JPanel();
			this.masterpanel.setLayout(new BorderLayout());

			// create a tabbed pane inside main window to display query input,
			// data input, ontology input and evaluation results
			this.tabbedPane_globalMainpane = new JTabbedPane();
			this.masterpanel.add(this.tabbedPane_globalMainpane,
					BorderLayout.CENTER);

			this.generateEvaluatorChooseAndPreferences();

			final JSplitPane splitPane_queryInput = generateJSplitPane(
					this.generateQueryTab(), this.generateQueryInputErrorBox());

			final JPanel queryInputTab = new JPanel(new BorderLayout());

			final JPanel evalPanel = this.generateEvalpanel();

			queryInputTab.add(splitPane_queryInput, BorderLayout.CENTER);
			queryInputTab.add(evalPanel, BorderLayout.SOUTH);

			// add a new tab for query input to the tabbed pane
			this.tabbedPane_globalMainpane.add(this.TAB_TITLE_QUERY, queryInputTab);

			final JSplitPane splitPane_rifInput = generateJSplitPane(
					this.generateRifTab(), this.generateRifInputErrorBox());

			final JPanel rifInputTab = new JPanel(new BorderLayout());

			final JPanel rifEvalPanel = this.generateRifEvalPanel();

			rifInputTab.add(splitPane_rifInput, BorderLayout.CENTER);
			rifInputTab.add(rifEvalPanel, BorderLayout.SOUTH);

			this.tabbedPane_globalMainpane.add(this.TAB_TITLE_RULES, rifInputTab);

			final JSplitPane splitPane_dataInput = generateJSplitPane(
					this.generateDataTab(), this.generateDataInputErrorBox());

			// add a new tab for data input to the tabbed pane
			this.tabbedPane_globalMainpane.add(this.TAB_TITLE_DATA,
					splitPane_dataInput);

			// add a new tab for evaluation results to the tabbed pane
			this.tabbedPane_globalMainpane.add(this.TAB_TITLE_RESULT, new JPanel());

			this.masterpanel.add(this.tabbedPane_globalMainpane,
					BorderLayout.CENTER);

			this.masterpanel.setVisible(true);

			// System.setErr(new PrintStream(new OutputStream() {
			// public void write(int arg0) throws IOException {
			// StringBuffer s = new StringBuffer();
			//
			// for(char c : Character.toChars(arg0))
			// s.append(c);
			//
			// ta_errors.setText(ta_errors.getText() + s.toString());
			// }
			// }, true));
		} catch (final Throwable th) {
			th.printStackTrace();
		}

		return this.masterpanel;
	}

	private static JSplitPane generateJSplitPane(final Component topComponent,
			final Component bottomComponent) {
		final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setPreferredSize(new Dimension(790, 600));
		splitPane.setDividerLocation(400);
		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);
		splitPane.setTopComponent(topComponent);
		splitPane.setBottomComponent(bottomComponent);
		return splitPane;
	}

	public static LinkedList<String> generateLookAndFeelList() {
		final LinkedList<String> lafList = new LinkedList<String>();

		final UIManager.LookAndFeelInfo[] lafInfo = UIManager
				.getInstalledLookAndFeels();

		for (int i = 0; i < lafInfo.length; i++) {
			lafList.add(lafInfo[i].getName());
			if(lafInfo[i].getName().compareTo("Metal")==0){
				lafList.add("Metal Ocean");
			}
		}

		return lafList;
	}

	/**
	 * Generate the stuff to choose a evaluator.
	 */
	protected void generateEvaluatorChooseAndPreferences() {
		final JPanel rowpanel = new JPanel(new BorderLayout());

		final JPanel leftpanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		leftpanel.setBounds(0, 5, 505, 30);

		final JLabel info = new JLabel();
		info.setText("Choose an evaluator:\t");
		leftpanel.add(info);

		// create combobox for the evaluators, fill it and add it to Applet...
		this.cobo_evaluator = new JComboBox(this.getEvaluators());
		this.cobo_evaluator.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Demo_Applet.this.enableOrDisableButtons(false);
				Demo_Applet.this.enableOrDisableButtons(true);
			}

		});
		leftpanel.add(this.cobo_evaluator);

		rowpanel.add(leftpanel, BorderLayout.WEST);

		rowpanel.add(this.generatePreferencesButton(), BorderLayout.EAST);

		this.masterpanel.add(rowpanel, BorderLayout.NORTH);
	}

	protected JButton generatePreferencesButton(){
		final JButton preferencesButton = new JButton("Preferences");
		preferencesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ae) {
				final String[] ids = new String[] { "lookAndFeel",
						"syntaxHighlighting", "operatorGraph_useStyledBoxes",
						"ast_useStyledBoxes", "queryEditor_useStyledBoxes",
						"documentEditorPane_useStyledBoxes",
						"dataEditor_useStyledBoxes",
						"condensedViewViewer_useStyledBoxes",
						"serviceCallApproach"};

				final LinkedList<String> idList = new LinkedList<String>();

				for (int i = 0; i < ids.length; ++i) {
					idList.add(ids[i]);
				}
				try {
					Demo_Applet.this.preferences.showDialog(false, idList);
				} catch (final Exception e) {
					System.err.println(e);
					e.printStackTrace();
				}
			}
		});
		return preferencesButton;
	}

	protected void enableOrDisableEvaluationDemoButtonSPARQL() {
		final String chosen = (String) this.cobo_evaluator.getSelectedItem();
		if (chosen.compareTo("Jena") == 0 || chosen.compareTo("Sesame") == 0) {
			this.bt_evalDemo.setEnabled(false);
		} else {
			this.bt_evalDemo.setEnabled(true);
		}
	}

	protected boolean isEvaluatorWithSupportOfRIFChosen(){
		final String chosen = (String) this.cobo_evaluator.getSelectedItem();
		return (chosen.compareTo("Jena") != 0 && chosen.compareTo("Sesame") != 0);
	}

	protected void enableOrDisableEvaluationButtonsRIF() {
		final boolean enable=this.isEvaluatorWithSupportOfRIFChosen();
		this.bt_rifEvaluate.setEnabled(enable);
		this.bt_rifMeasureExecutionTimes.setEnabled(enable);
	}

	protected void enableOrDisableEvaluationDemoButtonRIF() {
		this.bt_rifEvalDemo.setEnabled(this.isEvaluatorWithSupportOfRIFChosen());
	}

	protected void enableOrDisableButtons(final boolean queryOrRif) {
		if(queryOrRif){
			this.enableOrDisableEvaluationDemoButtonSPARQL();
			this.bt_evaluate.setEnabled(true);
			this.bt_MeasureExecutionTimes.setEnabled(true);
		} else {
			this.enableOrDisableEvaluationDemoButtonRIF();
			this.enableOrDisableEvaluationButtonsRIF();
		}
	}

	protected class LineNumbers extends JLabel {
		private static final long serialVersionUID = 1L;
		private int lWidth = 15;
		private final JTextPane pane;
		private final Font font;
		private final FontMetrics fm;
		private final int h;

		public LineNumbers(final JTextPane jTextPane) {
			this.pane = jTextPane;
			this.font = this.pane.getFont();
			this.fm = this.pane.getFontMetrics(this.font);
			this.h = this.fm.getHeight();

			this.calculateWidth();
		}

		private void calculateWidth() {
			int i = this.h;
			int row = 1;

			while (i < this.pane.getHeight()) {
				final String s = Integer.toString(row);

				if (this.fm.stringWidth(s) > this.lWidth) {
					this.lWidth = this.fm.stringWidth(s);
				}

				i += this.h;
				row++;
			}
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(this.lWidth + 1, (int) this.pane
					.getPreferredSize().getHeight());
		}

		@Override
		public void paintComponent(final Graphics g) {
			super.paintComponents(g);

			g.setFont(this.font);

			this.calculateWidth();

			int i = this.h;
			int row = 1;

			while (i < this.pane.getHeight()) {
				final String s = Integer.toString(row);

				g.drawString(s, 0 + this.lWidth - this.fm.stringWidth(s), i);

				i += this.h;
				row++;
			}
		}
	}

	/**
	 * Generate the stuff for the query input.
	 */
	protected JPanel generateQueryTab() {

		final JButton bt_visualEdit = new JButton("Visual Edit");
		bt_visualEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				new AdvancedQueryEditor(Demo_Applet.this.tp_queryInput.getText(), Demo_Applet.this.tp_dataInput.getText(), Demo_Applet.this.myself, getIcon(Demo_Applet.this.webdemo));
			}
		});

		final LuposDocument document = new LuposDocument();
		this.tp_queryInput = new LuposJTextPane(document);
		document.init(SPARQLParser.createILuposParser(new LuposDocumentReader(document)), true, 100);
		new JTextPanePreparer(this.tp_queryInput, StrategyManager.LANGUAGE.SPARQL, document);

		this.queryInputSP = new JScrollPane(this.tp_queryInput);

		return this.generateInputTab(bt_visualEdit, null,
				"Choose a SPARQL query:\t", this.getQueries(), this.PATH_QUERIES,
				"Clear query field", this.tp_queryInput, this.queryInputSP);
	}

	/**
	 * Generate the stuff for the query input.
	 */
	protected JPanel generateRifTab() {

		final JButton bt_visualEdit = new JButton("Visual Edit");
		bt_visualEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				new VisualRifEditor(Demo_Applet.this.tp_rifInput.getText(), Demo_Applet.getIcon(Demo_Applet.this.webdemo));
			}
		});

		final LuposDocument document = new LuposDocument();
		this.tp_rifInput = new LuposJTextPane(document);
		document.init(RIFParser.createILuposParser(new LuposDocumentReader(document)), true, 100);
		new JTextPanePreparer(this.tp_rifInput, StrategyManager.LANGUAGE.RIF, document);

		this.rifInputSP = new JScrollPane(this.tp_rifInput);

		return this.generateInputTab(bt_visualEdit, null, "Choose a RIF query:\t",
				this.getRuleFiles(), this.PATH_RULES, "Clear rule field",
				this.tp_rifInput, this.rifInputSP);
	}

	/**
	 * Generate the stuff for error output on the SPARQL query tab.
	 */
	protected JPanel generateQueryInputErrorBox() {
		this.ta_queryInputErrors = new JTextArea();
		return this.generateInputErrorBox(this.tp_queryInput,
				this.ta_queryInputErrors);
	}

	/**
	 * Generate the stuff for error output on the RDF Data tab.
	 */
	protected JPanel generateDataInputErrorBox() {
		this.ta_dataInputErrors = new JTextArea();
		return this.generateInputErrorBox(this.tp_dataInput, this.ta_dataInputErrors);
	}

	/**
	 * Generate the stuff for error output on the RIF tab.
	 */
	protected JPanel generateRifInputErrorBox() {
		this.ta_rifInputErrors = new JTextArea();
		return this.generateInputErrorBox(this.tp_rifInput, this.ta_rifInputErrors);
	}

	/**
	 * Generate the stuff for error output on the RDF Data tab.
	 */
	protected JPanel generateInputErrorBox(
			final LuposJTextPane input, final JTextArea inputErrors) {
		final JPanel rowpanel = new JPanel(new BorderLayout());

		final JLabel info = new JLabel();
		info.setText("Errors detected:");
		rowpanel.add(info, BorderLayout.WEST);

		final JPanel clearpanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		// create clear-button, add actionListener and add it to Applet...
		final JButton bt_clear = new JButton("Clear error field");
		bt_clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				inputErrors.setText("");
				input.disableErrorLine();
			}
		});

		clearpanel.add(bt_clear);

		rowpanel.add(clearpanel, BorderLayout.EAST);

		final JPanel masterpanel1 = new JPanel(new BorderLayout());
		masterpanel1.add(rowpanel, BorderLayout.NORTH);

		inputErrors.setFont(new Font("Courier New", Font.PLAIN, 12));
		inputErrors.setEditable(false);

		final JScrollPane scroll = new JScrollPane(inputErrors);

		masterpanel1.add(scroll, BorderLayout.CENTER);

		return masterpanel1;
	}

	/**
	 * Generate the stuff for the data input.
	 */
	protected JPanel generateDataTab() {

		final JButton bt_visualEdit = new JButton("Visual Edit");
		bt_visualEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				new DataEditor(Demo_Applet.this.tp_dataInput.getText(), Demo_Applet.this.myself, getIcon(Demo_Applet.this.webdemo));
			}
		});

		final JButton bt_CondensedView = new JButton("Condense Data");
		bt_CondensedView.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ae) {
				if (Demo_Applet.this.prefixInstance == null) {
					Demo_Applet.this.prefixInstance = new ViewerPrefix(Demo_Applet.this.usePrefixes.isTrue(), null);
				}
				final CondensedViewToolBar toolBar = new CondensedViewToolBar(
						Demo_Applet.this.tp_dataInput.getText(),
						Demo_Applet.this.prefixInstance);
				final CondensedViewViewer operatorGraphViewer1 = new CondensedViewViewer(
						Demo_Applet.this.prefixInstance, false, getIcon(Demo_Applet.this.webdemo), toolBar);
				toolBar.setOperatorGraphViewer(operatorGraphViewer1);

				Demo_Applet.this.repaint();
			}
		});

		final LuposDocument document_data = new LuposDocument();
		this.tp_dataInput = new LuposJTextPane(document_data);
		document_data.init(TurtleParser.createILuposParser(new LuposDocumentReader(document_data)), true, 100);
		new JTextPanePreparer(this.tp_dataInput, StrategyManager.LANGUAGE.RDF, document_data);

		this.dataInputSP = new JScrollPane(this.tp_dataInput);

		return this.generateInputTab(bt_visualEdit, bt_CondensedView,
				"Choose RDF data:\t", this.getDataFiles(), this.PATH_DATA,
				"Clear data field", this.tp_dataInput, this.dataInputSP);
	}

	public String getResourceAsString(final String resource){
		final URL url = Demo_Applet.class.getResource(resource);

		return FileHelper.readFile(resource, new FileHelper.GetReader() {

				@Override
				public Reader getReader(final String filename) throws FileNotFoundException {
					try {
						InputStream stream = null;
						stream = this.getClass().getResourceAsStream(filename);
						return new java.io.InputStreamReader(stream);
					} catch(final Exception e){
						return new FileReader(url.getFile());
					}
				}
			});
	}

	protected JPanel generateInputTab(final JButton bt_visualEdit,
			final JButton bt_CondensedView, final String chooseText,
			final String[] toChoose, final String PATH, final String clearText,
			final LuposJTextPane tp_input,
			final JScrollPane inputSP) {
		final JPanel rowpanel = new JPanel(new BorderLayout());

		final JPanel choosepanel = new JPanel(new FlowLayout(FlowLayout.LEFT,
				5, 0));

		// create buttons 'visual edit' and 'condensed data'
		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,
				10, 0));

		if (bt_visualEdit != null) {
			buttonPanel.add(bt_visualEdit);
		}

		if (bt_CondensedView != null) {
			buttonPanel.add(bt_CondensedView);
		}

		rowpanel.add(buttonPanel, BorderLayout.WEST);

		final JLabel info2 = new JLabel(chooseText);

		choosepanel.add(info2);

		// create combobox for query files, fill it and add it to Applet...
		final JComboBox cb_data = new JComboBox(toChoose);
		cb_data.setPreferredSize(new Dimension(130, 20));

		choosepanel.add(cb_data);

		// create select-button, add actionListener and add it to Applet...
		final JButton bt_select = new JButton("Select");
		bt_select.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				// open file and write content to textarea...
				tp_input.setText(Demo_Applet.this.getResourceAsString(PATH + cb_data.getSelectedItem().toString()));
				tp_input.disableErrorLine();
				tp_input.grabFocus();
			}
		});

		choosepanel.add(bt_select);

		final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		panel.add(choosepanel);

		// create clear-button, add actionListener and add it to Applet...
		final JButton bt_clear = new JButton(clearText);
		bt_clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				tp_input.setText("");
				tp_input.disableErrorLine();
			}
		});

		panel.add(bt_clear);

		rowpanel.add(panel, BorderLayout.EAST);

		final JPanel mainpanel = new JPanel(new BorderLayout());
		mainpanel.add(rowpanel, BorderLayout.NORTH);
		mainpanel.add(inputSP, BorderLayout.CENTER);

		return mainpanel;
	}

	public class RuleSets {
		public String getRIF(){
			return Demo_Applet.this.tp_rifInput.getText();
		}
		public String getRDFS(){
			switch((GENERATION)Demo_Applet.this.comboBox_sparqlInferenceGenerated.getSelectedItem()){
			case FIXED:
				return Demo_Applet.this.getResourceAsString(Demo_Applet.this.PATH_RULES + "rule_rdfs.rif");
			case GENERATED:
				return InferenceHelper.getRIFInferenceRulesForRDFSOntology(Demo_Applet.this.tp_dataInput.getText());
			default:
			case GENERATEDOPT:
				return InferenceHelper.getRIFInferenceRulesForRDFSOntologyAlternative(Demo_Applet.this.tp_dataInput.getText());
			}
		}
		public String getOWL2RL(){
			switch((GENERATION)Demo_Applet.this.comboBox_sparqlInferenceGenerated.getSelectedItem()){
			case FIXED:
				if(Demo_Applet.this.checkBox_sparqlInferenceCheckInconsistency.isSelected()){
					return Demo_Applet.this.getResourceAsString(Demo_Applet.this.PATH_RULES + "rule_owl2rl.rif");
				} else {
					return Demo_Applet.this.getResourceAsString(Demo_Applet.this.PATH_RULES + "rule_owl2rlNoInconsistencyRules.rif");
				}
			case GENERATED:
				if(Demo_Applet.this.checkBox_sparqlInferenceCheckInconsistency.isSelected()){
					return InferenceHelper.getRIFInferenceRulesForOWL2Ontology(Demo_Applet.this.tp_dataInput.getText());
				} else {
					return InferenceHelper.getRIFInferenceRulesForOWL2OntologyWithoutCheckingInconsistencies(Demo_Applet.this.tp_dataInput.getText());
				}
			default:
			case GENERATEDOPT:
				if(Demo_Applet.this.checkBox_sparqlInferenceCheckInconsistency.isSelected()){
					return InferenceHelper.getRIFInferenceRulesForOWL2OntologyAlternative(Demo_Applet.this.tp_dataInput.getText());
				} else {
					return InferenceHelper.getRIFInferenceRulesForOWL2OntologyAlternativeWithoutCheckingInconsistencies(Demo_Applet.this.tp_dataInput.getText());
				}
			}
		}
	}

	protected final RuleSets rulesets = new RuleSets();

	protected enum SPARQLINFERENCE {
		NONE(){
			@Override
			public String toString(){
				return "None";
			}
			@Override
			public boolean isMaterializationChoice() {
				return false;
			}
			@Override
			public boolean isGeneratedChoice() {
				return false;
			}
			@Override
			public String getRuleSet(final RuleSets rulesets) {
				return null;
			}
		},
		RIF(){
			@Override
			public boolean isMaterializationChoice() {
				return true;
			}
			@Override
			public boolean isGeneratedChoice() {
				return false;
			}
			@Override
			public String getRuleSet(final RuleSets rulesets) {
				return rulesets.getRIF();
			}
		},
		RDFS(){
			@Override
			public boolean isMaterializationChoice() {
				return true;
			}
			@Override
			public boolean isGeneratedChoice() {
				return true;
			}
			@Override
			public String getRuleSet(final RuleSets rulesets) {
				return rulesets.getRDFS();
			}
		},
		OWL2RL{
			@Override
			public String toString(){
				return "OWL2 RL";
			}
			@Override
			public boolean isMaterializationChoice() {
				return true;
			}
			@Override
			public boolean isGeneratedChoice() {
				return true;
			}
			@Override
			public String getRuleSet(final RuleSets rulesets) {
				return rulesets.getOWL2RL();
			}

			@Override
			public boolean isCheckInconsistenciesChoice() {
				return true;
			}
		};

		public abstract boolean isMaterializationChoice();

		public abstract boolean isGeneratedChoice();

		public abstract String getRuleSet(RuleSets rulesets);

		public boolean isCheckInconsistenciesChoice() {
			return false;
		}
	}

	protected static enum SPARQLINFERENCEMATERIALIZATION {
		COMBINEDQUERYOPTIMIZATION(){
			@Override
			public String toString(){
				return "On Demand";
			}
		},
		MATERIALIZEALL(){
			@Override
			public String toString(){
				return "Materialize";
			}
		}
	}

	protected static enum GENERATION {
		GENERATEDOPT(){
			@Override
			public String toString(){
				return "Gen. Alt.";
			}
		},
		GENERATED(){
			@Override
			public String toString(){
				return "Generated";
			}
		},
		FIXED(){
			@Override
			public String toString(){
				return "Fixed";
			}
		}
	}

	protected JPanel generateEvalpanel() {

		this.comboBox_sparqlInferenceMaterialization = new JComboBox();

		for (int i = 0; i < SPARQLINFERENCEMATERIALIZATION.values().length; i++) {
			this.comboBox_sparqlInferenceMaterialization.addItem(SPARQLINFERENCEMATERIALIZATION.values()[i]);
		}

		this.comboBox_sparqlInferenceMaterialization.setSelectedIndex(0);

		this.comboBox_sparqlInferenceGenerated = new JComboBox();
		for(final GENERATION generation: GENERATION.values()){
			this.comboBox_sparqlInferenceGenerated.addItem(generation);
		}

		this.comboBox_sparqlInference  = new JComboBox();

		for (int i = 0; i < SPARQLINFERENCE.values().length; i++) {
			this.comboBox_sparqlInference.addItem(SPARQLINFERENCE.values()[i]);
		}

		this.checkBox_sparqlInferenceCheckInconsistency = new JCheckBox("Check Inconsistencies");

		this.comboBox_sparqlInference.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ae) {
				final SPARQLINFERENCE sparqlinference = (SPARQLINFERENCE)Demo_Applet.this.comboBox_sparqlInference.getSelectedItem();
				Demo_Applet.this.comboBox_sparqlInferenceMaterialization.setEnabled(sparqlinference.isMaterializationChoice());
				Demo_Applet.this.comboBox_sparqlInferenceGenerated.setEnabled(sparqlinference.isGeneratedChoice());
				Demo_Applet.this.checkBox_sparqlInferenceCheckInconsistency.setEnabled(sparqlinference.isCheckInconsistenciesChoice());
			}
		});

		this.comboBox_sparqlInference.setSelectedIndex(0);

		// create evaluate-button, add actionListener and add it to Applet...
		this.bt_evaluate = new JButton("Evaluate");
		this.bt_evaluate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ae) {

				if (Demo_Applet.this.prepareForEvaluation(false)) {

					Demo_Applet.this.evaluateSPARQLQuery(EvaluationMode.RESULT);

					Demo_Applet.this.repaint();
				}
			}
		});

		this.bt_evalDemo = this.createEvaluationDemoButton(true);
		this.bt_evalDemo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ae) {
				if (Demo_Applet.this.prepareForEvaluation(false)) {

					Demo_Applet.this.evaluateSPARQLQuery(EvaluationMode.DEMO);

					Demo_Applet.this.repaint();
				}
			}
		});

		this.bt_MeasureExecutionTimes = new JButton("Execution Times");
		this.bt_MeasureExecutionTimes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ae) {
				if (Demo_Applet.this.prepareForEvaluation(false)) {
					Demo_Applet.this.evaluateSPARQLQuery(EvaluationMode.TIMES);

					Demo_Applet.this.repaint();
				}
			}
		});

		final JPanel panel1 = generateEvalpanel(new JLabel("Inference:"), this.comboBox_sparqlInference, this.comboBox_sparqlInferenceMaterialization, this.comboBox_sparqlInferenceGenerated, this.checkBox_sparqlInferenceCheckInconsistency);
		final JPanel panel2 = generateEvalpanel(new JLabel("Evaluation:"), this.bt_evaluate, this.bt_evalDemo, this.bt_MeasureExecutionTimes);

		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(panel1, BorderLayout.NORTH);
		panel.add(panel2, BorderLayout.SOUTH);
		return panel;
	}

	protected JPanel generateRifEvalPanel() {
		// create evaluate-button, add actionListener and add it to Applet...
		this.bt_rifEvaluate = new JButton("Evaluate");
		this.bt_rifEvaluate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ae) {

				if (Demo_Applet.this.prepareForEvaluation(false)) {

					Demo_Applet.this.evaluateRIFRule(EvaluationMode.RESULT);

					Demo_Applet.this.repaint();
				}
			}
		});

		this.bt_rifEvalDemo = this.createEvaluationDemoButton(true);
		this.bt_rifEvalDemo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ae) {
				if (Demo_Applet.this.prepareForEvaluation(false)) {

					Demo_Applet.this.evaluateRIFRule(EvaluationMode.DEMO);

					Demo_Applet.this.repaint();
				}
			}
		});

		this.bt_rifMeasureExecutionTimes = new JButton("Execution Times");
		this.bt_rifMeasureExecutionTimes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ae) {
				if (Demo_Applet.this.prepareForEvaluation(false)) {

					Demo_Applet.this.evaluateRIFRule(EvaluationMode.TIMES);

					Demo_Applet.this.repaint();
				}
			}
		});

		return generateEvalpanel(this.bt_rifEvaluate, this.bt_rifEvalDemo,
				this.bt_rifMeasureExecutionTimes);
	}

	protected static JPanel generateEvalpanel(final JComponent... components ) {
		final JPanel evalpanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
		for(final JComponent component: components){
			evalpanel.add(component);
		}
		return evalpanel;
	}

	protected boolean prepareForEvaluation(final boolean rif) {
		if (this.operatorGraphViewer != null && this.operatorGraphViewer.isVisible()) {
			if (JOptionPane
					.showConfirmDialog(
							null,
							"This operation first closes the existing Evaluation Demo...",
							"Closing existing Evaluation Demo",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE) != 0) {
				return false;
			}
			this.operatorGraphViewer.processWindowEvent(new WindowEvent(
					this.operatorGraphViewer, WindowEvent.WINDOW_CLOSING));
		}
//		if (Indices.operatorGraphViewer != null
//				&& Indices.operatorGraphViewer.isVisible()) {
//			if (JOptionPane
//					.showConfirmDialog(
//							null,
//							"This operation first closes the existing Materialization Demo...",
//							"Closing existing Materialization Demo",
//							JOptionPane.OK_CANCEL_OPTION,
//							JOptionPane.WARNING_MESSAGE) != 0)
//				return false;
//			Indices.operatorGraphViewer.processWindowEvent(new WindowEvent(
//					Indices.operatorGraphViewer, WindowEvent.WINDOW_CLOSING));
//		}
		try {
			if (BooleanDatatype.getValues("clearErrorField").get(0)
					.booleanValue()) {
				this.displayErrorMessage("", false);
			}
		} catch (final Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}

		if (rif) {
			this.tp_rifInput.disableErrorLine();
		} else {
			this.tp_queryInput.disableErrorLine();
		}
		this.tp_dataInput.disableErrorLine();

		if (this.resultpanel != null) {
			this.remove(this.resultpanel);
		}

		this.data = this.tp_dataInput.getText();

		return true;
	}

	/*
	 * Create Button to display RDFS Materialization when using an external
	 * Ontology file
	 */
//	protected JButton createRDFSMaterializationButton() {
//		final JButton bt_RDFSMat = new JButton("Show RDFS Materialization");
//
//		bt_RDFSMat.setMargin(new Insets(0, 0, 0, 0));
//		final boolean enabled = StaticDataHolder.getOperatorGraphRules() != null;
//		bt_RDFSMat.setEnabled(enabled);
//
//		if (enabled) {
//			bt_RDFSMat.addActionListener(new ActionListener() {
//
//				public void actionPerformed(final ActionEvent arg0) {
//					if (StaticDataHolder.getOperatorGraphRules() != null)
//						new Viewer(StaticDataHolder.getOperatorGraphRules(),
//								"RDFS materialization", false,
//								webdemo != DEMO_ENUM.ECLIPSE, StaticDataHolder
//										.getPrefixInstance());
//				}
//
//			});
//		}
//
//		return bt_RDFSMat;
//	}

	/**
	 * create the button to show the operator graph.
	 */
	protected JButton createEvaluationDemoButton(final Boolean enabled) {
		// create OperatorGraph-button, add actionListener and add it to
		// Applet...
		final JButton bt_evalDemo_local = new JButton("Eval. Demo");
		bt_evalDemo_local.setEnabled(enabled);
		return bt_evalDemo_local;
	}

	protected String[] getFiles(final String path, final String suffix){
		// create array list for files...
		final ArrayList<String> tmp = new ArrayList<String>();

		// load files...
		final String[] tmp_lst = new File(Demo_Applet.class.getResource(path).getFile()).list();

		if(tmp_lst!=null) {
			// walk through files...
			for (int i = 0; i < tmp_lst.length; i++) {
				// if file ends with suffix...
				if (tmp_lst[i].endsWith(suffix)) {
					tmp.add(tmp_lst[i]); // add file to file array list
				}
			}
		}

		// return list of available queries...
		return tmp.toArray(new String[tmp.size()]);
	}

	/**
	 * Get Query Files.
	 *
	 * @return string array of available queries
	 */
	protected String[] getQueries() {
		switch (this.webdemo) {
		case LOCALONEJAR:
		case PROJECT_DEMO:
			// started with Java Web Start?
			// Java Web start has a more restrictive rights
			// management, i.e. sp2b and lubm queries > 7 do not work with Java
			// Web Start...
			if (!this.isApplet && this.webdemo != DEMO_ENUM.ECLIPSE
					&& this.webdemo != DEMO_ENUM.LOCALONEJAR) {
				return new String[] { "lubm_asktest.sparql",
						"lubm_constructtest.sparql", "lubm_query1.sparql",
						"lubm_query2.sparql", "lubm_query3.sparql",
						"lubm_query4.sparql", "lubm_query5.sparql",
						"lubm_query6.sparql", "lubm_query7.sparql" };
			}

			// if we are in a jar for the project demo...
			return new String[] { "lubm_query1.sparql", "lubm_query2.sparql",
					"lubm_query3.sparql", "lubm_query4.sparql",
					"lubm_query5.sparql", "lubm_query6.sparql",
					"lubm_query7.sparql", "lubm_query8.sparql",
					"lubm_query9.sparql", "lubm_asktest.sparql",
					"lubm_constructtest.sparql", "sp2b_q1.sparql",
					"sp2b_q2.sparql", "sp2b_q3a.sparql", "sp2b_q3b.sparql",
					"sp2b_q3c.sparql", "sp2b_q4.sparql", "sp2b_q5a.sparql",
					"sp2b_q5b.sparql", "sp2b_q6.sparql", "sp2b_q7.sparql",
					"sp2b_q8.sparql", "sp2b_q9.sparql", "sp2b_q10.sparql",
					"sp2b_q11.sparql", "sp2b_q12a.sparql", "sp2b_q12b.sparql",
					"sp2b_q12c.sparql", "sp2b_E1.sparql", "sp2b_E2.sparql",
					"sp2b_E3.sparql", "sp2b_E4.sparql", "lubm_test.sparql",
					"yago_q1.sparql", "yago_q2.sparql" };
		case TUTORIAL:
			return new String[] { "lubm_test.sparql", "lubm_asktest.sparql",
					"lubm_constructtest.sparql" };
		case TUTORIAL2:
			return new String[] { "sp2b_q1.sparql", "sp2b_q2.sparql",
					"sp2b_q3a.sparql", "sp2b_q3b.sparql", "sp2b_q3c.sparql",
					"sp2b_q4.sparql", "sp2b_q5a.sparql", "sp2b_q5b.sparql",
					"sp2b_q6.sparql", "sp2b_q7.sparql", "sp2b_q8.sparql",
					"sp2b_q9.sparql", "sp2b_q10.sparql", "sp2b_q11.sparql",
					"sp2b_q12a.sparql", "sp2b_q12b.sparql", "sp2b_q12c.sparql" };
		default:
		case ECLIPSE:
			return this.getFiles(this.PATH_QUERIES, ".sparql");
		}
	}

	protected String[] getRuleFiles() {
		switch (this.webdemo) {
		default:
			return new String[] { "rule_And.rif", "rule_assignment.rif",
					"rule_comparsion.rif", "rule_comparsion.rif",
					"rule_equality.rif", "rule_exists.rif",
					"rule_fibonacci.rif", "rule_functional.rif", "rule_Or.rif",
					"rule_owl2rl.rif", "rule_owl2rlNoInconsistencyRules.rif",
					"rule_parent_discount.rif", "rule_predicates.rif",
					"rule_rdfs.rif" };
		case TUTORIAL2:
			return new String[] { "facts.rif" };
		case ECLIPSE:
			return this.getFiles(this.PATH_RULES, ".rif");
		}
	}

	/**
	 * Get Data Files.
	 *
	 * @return string array of available data files
	 */
	protected String[] getDataFiles() {
		switch (this.webdemo) {
		case LOCALONEJAR:
		case PROJECT_DEMO:
			// started with Java Web Start?
			// Java Web start has a more restrictive rights
			// management, i.e. sp2b does not work with Java
			// Web Start...
			if (!this.isApplet && this.webdemo != DEMO_ENUM.ECLIPSE
					&& this.webdemo != DEMO_ENUM.LOCALONEJAR) {
				return new String[] { "lubm_demo.n3" };
			}

			// if we are in a jar for the project demo...
			return new String[] { "lubm_demo.n3", "sp2b_demo.n3", "yagodata.n3" };
		case TUTORIAL:
			return new String[] { "lubm_demo.n3" };
		case TUTORIAL2:
			return new String[] { "sp2b.n3" };
		default:
		case ECLIPSE:
			return this.getFiles(this.PATH_DATA, ".n3");
		}
	}

	@SuppressWarnings("unchecked")
	public QueryEvaluator<Node> setupEvaluator(final EvaluationMode mode)
			throws Throwable {
		final ServiceApproaches serviceApproach = xpref.datatypes.EnumDatatype.getFirstValue("serviceCallApproach");
		final FederatedQueryBitVectorJoin.APPROACH bitVectorApproach = xpref.datatypes.EnumDatatype.getFirstValue("serviceCallBitVectorApproach");
		bitVectorApproach.setup();
		serviceApproach.setup();
		FederatedQueryBitVectorJoin.substringSize = xpref.datatypes.IntegerDatatype.getFirstValue("serviceCallBitVectorSize");
		FederatedQueryBitVectorJoinNonStandardSPARQL.bitvectorSize = FederatedQueryBitVectorJoin.substringSize;
		LiteralFactory.semanticInterpretationOfLiterals = xpref.datatypes.BooleanDatatype.getFirstValue("semanticInterpretationOfDatatypes");
		// use static method "newInstance()" for instantiation if available
		QueryEvaluator<Node> evaluator = null;
		final Class<? extends QueryEvaluator<Node>> evalClass = this.getEvaluatorClass(this.cobo_evaluator.getSelectedIndex());
		try {
			Method m;
			if ((m  = evalClass.getDeclaredMethod("newInstance")) != null && (m.getModifiers() & Modifier.STATIC) != 0) {
				final Object instance = m.invoke(evalClass);
				if (instance instanceof QueryEvaluator) {
					evaluator = (QueryEvaluator<Node>) instance;
				}
			}
		} catch (NoSuchMethodException | SecurityException e) {
			evaluator = null;
		}
		if (evaluator == null) {
			// otherwise use standard constructor
			evaluator = evalClass.newInstance();
		}
		evaluator.setupArguments();
		evaluator.getArgs().set("debug", DEBUG.ALL);
		evaluator.getArgs().set("result", QueryResult.TYPE.MEMORY);
		evaluator.getArgs().set("codemap", LiteralFactory.MapType.TRIEMAP);
		// evaluator.getArgs().set("codemap", LiteralFactory.MapType.LAZYLITERAL);
		// evaluator.getArgs().set("optimization", MemoryIndexQueryEvaluator.Optimizations.BINARYSTATICANALYSIS);
		evaluator.getArgs().set("distinct", CommonCoreQueryEvaluator.DISTINCT.HASHSET);
		evaluator.getArgs().set("optional", CommonCoreQueryEvaluator.JOIN.HASHMAPINDEX);

		final String engine = (String) this.cobo_evaluator.getSelectedItem();

		if (engine.compareTo("Jena") == 0) {
				evaluator.getArgs().set("RDFS", JenaQueryEvaluator.ONTOLOGY.NONE);
		} else if (engine.compareTo("Sesame") == 0) {
				evaluator.getArgs().set("RDFS", SesameQueryEvaluator.ONTOLOGY.NONE);
		} else {
			evaluator.getArgs().set("RDFS", CommonCoreQueryEvaluator.RDFS.NONE);
		}
		// started with Java Web Start?
		// Java Web start has a more restrictive rights
		// management, i.e. JenaN3 does not work with Java Web
		// Start...
		if (engine.compareTo("Jena") == 0
				|| engine.compareTo("Sesame") == 0
				|| (!this.isApplet && this.webdemo != DEMO_ENUM.ECLIPSE && this.webdemo != DEMO_ENUM.LOCALONEJAR)) {
			evaluator.getArgs().set("type", "N3");
		} else {
			evaluator.getArgs().set("type", "Turtle");
			// evaluator.getArgs().set("codemap", LiteralFactory.MapType.NOCODEMAP);
			evaluator.getArgs().set("core", true);
		}

		if (evaluator instanceof RDF3XQueryEvaluator) {
			evaluator.getArgs().set("datastructure", Indices.DATA_STRUCT.BPTREE);
		} else {
			evaluator.getArgs().set("datastructure", Indices.DATA_STRUCT.HASHMAP);
		}

		try {
			evaluator.init();
		} catch (final Throwable t) {
			// can be only rdf data error!
			this.dealWithThrowable(t, mode, true);
			throw t;
		}

		return evaluator;
	}

	protected static enum EvaluationMode {
		RESULT, TIMES, DEMO
	}

	protected abstract class Evaluation {

		public Evaluation(){
			Demo_Applet.this.errorsInOntology = null;
		}

		public abstract String getQuery();

		public abstract long compileQuery(String queryParameter) throws Exception;

		public abstract DebugViewerCreator compileQueryDebugByteArray(String queryParameter) throws Exception;

		public abstract JButton getButtonEvaluate();

		public abstract JButton getButtonEvalDemo();

		public abstract JButton getButtonMeasureExecutionTimes();

		public abstract QueryEvaluator<Node> getEvaluator();

		public void enableButtons(){
			this.enableDemoButtonDependingOnEvaluator();
			this.enableEvaluateButtonDependingOnEvaluator();
		}

		public void enableDemoButtonDependingOnEvaluator(){
			this.getButtonEvalDemo().setEnabled(this.evaluatorSuitableForDemo((String)Demo_Applet.this.cobo_evaluator.getSelectedItem()));
		}

		public abstract boolean evaluatorSuitableForDemo(String s);

		public void enableEvaluateButtonDependingOnEvaluator(){
			final boolean enable = this.evaluatorSuitableForEvaluation((String)Demo_Applet.this.cobo_evaluator.getSelectedItem());
			this.getButtonEvaluate().setEnabled(enable);
			this.getButtonMeasureExecutionTimes().setEnabled(enable);
		}

		public abstract boolean evaluatorSuitableForEvaluation(String s);

		public abstract long prepareInputData(Collection<URILiteral> defaultGraphsParameter, LinkedList<URILiteral> namedGraphs) throws Exception;
	}

	public class SPARQLEvaluation extends Evaluation {
		private final QueryEvaluator<Node> evaluator;
		private BasicOperator rootInference;
		private Result resultInference;

		public SPARQLEvaluation(final QueryEvaluator<Node> evaluator) {
			super();
			this.evaluator = evaluator;
		}

		@Override
		public String getQuery() {
			return Demo_Applet.this.tp_queryInput.getText();
		}

		@Override
		public long compileQuery(final String queryParameter) throws Exception {
			final long a = (new Date()).getTime();
			this.evaluator.compileQuery(queryParameter);
			this.integrateInferenceOperatorgraph();
			return (new Date()).getTime() - a;
		}

		@Override
		public DebugViewerCreator compileQueryDebugByteArray(final String queryParameter)
		throws Exception {
			final DebugViewerCreator result = new SPARQLDebugViewerCreator(Demo_Applet.this.webdemo != DEMO_ENUM.ECLIPSE, Demo_Applet.this.prefixInstance, Demo_Applet.this.usePrefixes,
					new RulesGetter(){
						@Override
						public List<DebugContainer<BasicOperatorByteArray>> getRuleApplications() {
							return Demo_Applet.this.ruleApplications;
						}
					}, Demo_Applet.getIcon(Demo_Applet.this.webdemo), this.evaluator.compileQueryDebugByteArray(queryParameter, Demo_Applet.this.prefixInstance));
			this.integrateInferenceOperatorgraph();
			return result;
		}

		@Override
		public JButton getButtonEvaluate() {
			return Demo_Applet.this.bt_evaluate;
		}

		@Override
		public JButton getButtonEvalDemo() {
			return Demo_Applet.this.bt_evalDemo;
		}

		@Override
		public JButton getButtonMeasureExecutionTimes() {
			return Demo_Applet.this.bt_MeasureExecutionTimes;
		}


		@Override
		public QueryEvaluator<Node> getEvaluator() {
			return this.evaluator;
		}

		@Override
		public boolean evaluatorSuitableForDemo(final String s) {
			return s.compareTo("Jena")!=0 && s.compareTo("Sesame")!=0;
		}

		@Override
		public boolean evaluatorSuitableForEvaluation(final String s) {
			return true;
		}

		private void setupInference(){
			final Object chosen=Demo_Applet.this.comboBox_sparqlInference.getSelectedItem();
			if(chosen != SPARQLINFERENCE.NONE && (this.evaluator instanceof JenaQueryEvaluator || this.evaluator instanceof SesameQueryEvaluator)){
				if(this.evaluator instanceof JenaQueryEvaluator && (chosen == SPARQLINFERENCE.OWL2RL)
				   || chosen == SPARQLINFERENCE.RDFS) {
					JOptionPane.showMessageDialog(Demo_Applet.this,
							"Jena and Sesame evaluators do not support different rulesets and materialization strategies!\nUsing their standard inference for "+chosen+"...",
							"Ontology support of Jena and Sesame evaluators",
							JOptionPane.INFORMATION_MESSAGE);
					if(this.evaluator instanceof JenaQueryEvaluator){
						if(chosen == SPARQLINFERENCE.OWL2RL){
							((JenaQueryEvaluator)this.evaluator).setOntology(JenaQueryEvaluator.ONTOLOGY.OWL);
						} else {
							((JenaQueryEvaluator)this.evaluator).setOntology(JenaQueryEvaluator.ONTOLOGY.RDFS);
						}
						return;
					} else if(this.evaluator instanceof SesameQueryEvaluator){
						((SesameQueryEvaluator)this.evaluator).setOntology(SesameQueryEvaluator.ONTOLOGY.RDFS);
						return;
					}
				}
				JOptionPane.showMessageDialog(Demo_Applet.this,
						"The "+((this.evaluator instanceof JenaQueryEvaluator)?"Jena":"Sesame")+" evaluator does not support this type of inference ("+chosen+")...\nEvaluate query without considering inference!",
						"Inference support of Jena and Sesame evaluators",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}

		private void inference() throws Exception {
			if(this.evaluator instanceof JenaQueryEvaluator || this.evaluator instanceof SesameQueryEvaluator){
				return;
			}
			Demo_Applet.this.ruleApplicationsForMaterialization = null;
			Demo_Applet.this.materializationInfo = null;
			Demo_Applet.this.inferenceRules = ((SPARQLINFERENCE)Demo_Applet.this.comboBox_sparqlInference.getSelectedItem()).getRuleSet(Demo_Applet.this.rulesets);
			if(Demo_Applet.this.inferenceRules != null){
				final BasicIndexRuleEvaluator birqe = new BasicIndexRuleEvaluator((CommonCoreQueryEvaluator<Node>)this.evaluator);
				birqe.compileQuery(Demo_Applet.this.inferenceRules);
				Demo_Applet.this.materializationInfo = new RIFDebugViewerCreator(Demo_Applet.this.webdemo != DEMO_ENUM.ECLIPSE, Demo_Applet.this.prefixInstance, Demo_Applet.this.usePrefixes,
						new RulesGetter(){
							@Override
							public List<DebugContainer<BasicOperatorByteArray>> getRuleApplications() {
								return Demo_Applet.this.ruleApplications;
							}
						}, Demo_Applet.getIcon(Demo_Applet.this.webdemo), birqe.getCompilationUnit(), birqe.getDocument());

				// TODO improve RIF logical optimization such that it is fast enough for large operator graphs!
				// as workaround here only use the logical optimization of the underlying evaluator!
				System.out.println("Logical optimization...");
				if (Demo_Applet.this.ruleApplicationsForMaterialization != null) {
					Demo_Applet.this.ruleApplicationsForMaterialization.addAll(this.evaluator.logicalOptimizationDebugByteArray(Demo_Applet.this.prefixInstance));
				} else {
					Demo_Applet.this.ruleApplicationsForMaterialization = this.evaluator.logicalOptimizationDebugByteArray(Demo_Applet.this.prefixInstance);
				}

				System.out.println("Physical optimization...");

				if (Demo_Applet.this.ruleApplicationsForMaterialization != null) {
					Demo_Applet.this.ruleApplicationsForMaterialization.addAll(birqe.physicalOptimizationDebugByteArray(Demo_Applet.this.prefixInstance));
				} else {
					Demo_Applet.this.ruleApplicationsForMaterialization = birqe.physicalOptimizationDebugByteArray(Demo_Applet.this.prefixInstance);
				}

				if(Demo_Applet.this.comboBox_sparqlInferenceMaterialization.getSelectedItem() == SPARQLINFERENCEMATERIALIZATION.MATERIALIZEALL){
					Demo_Applet.this.errorsInOntology = birqe.inferTriplesAndStoreInDataset();
				} else {
					this.rootInference = birqe.getRootNode();
					this.resultInference = birqe.getResultOperator();
				}
			}
		}

		private void integrateInferenceOperatorgraph() throws Exception{
			if(this.evaluator instanceof JenaQueryEvaluator || this.evaluator instanceof SesameQueryEvaluator){
				return;
			}
			if(Demo_Applet.this.comboBox_sparqlInference.getSelectedItem() != SPARQLINFERENCE.NONE &&
					Demo_Applet.this.comboBox_sparqlInferenceMaterialization.getSelectedItem() == SPARQLINFERENCEMATERIALIZATION.COMBINEDQUERYOPTIMIZATION){
				final CommonCoreQueryEvaluator<Node> commonCoreQueryEvaluator = (CommonCoreQueryEvaluator<Node>)this.evaluator;
				BasicIndexRuleEvaluator.integrateInferenceOperatorgraphIntoQueryOperatorgraph(this.rootInference, this.resultInference, commonCoreQueryEvaluator.getRootNode(), commonCoreQueryEvaluator.getResultOperator());
				commonCoreQueryEvaluator.setBindingsVariablesBasedOnOperatorgraph();
			}
		}

		@Override
		public long prepareInputData(final Collection<URILiteral> defaultGraphsParameter, final LinkedList<URILiteral> namedGraphs) throws Exception {
			final long a = (new Date()).getTime();
			this.setupInference();
			this.evaluator.prepareInputData(defaultGraphsParameter, namedGraphs);
			this.inference();
			return ((new Date()).getTime() - a);
		}
	}

	public class RIFEvaluation extends Evaluation {
		private BasicIndexRuleEvaluator ruleEvaluator;

		public RIFEvaluation(final QueryEvaluator<Node> evaluator) {
			super();
			try {
				this.ruleEvaluator = new BasicIndexRuleEvaluator((CommonCoreQueryEvaluator<Node>)evaluator);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public String getQuery() {
			return Demo_Applet.this.tp_rifInput.getText();
		}

		@Override
		public long compileQuery(final String rule) throws Exception {
			final long a = (new Date()).getTime();
			this.ruleEvaluator.compileQuery(rule);
			return (new Date().getTime()) - a;
		}

		@Override
		public DebugViewerCreator compileQueryDebugByteArray(final String rule)
				throws Exception {
			this.ruleEvaluator.compileQuery(rule);
			return new RIFDebugViewerCreator(Demo_Applet.this.webdemo != DEMO_ENUM.ECLIPSE, Demo_Applet.this.prefixInstance, Demo_Applet.this.usePrefixes,
					new RulesGetter(){
						@Override
						public List<DebugContainer<BasicOperatorByteArray>> getRuleApplications() {
							return Demo_Applet.this.ruleApplications;
						}
					}, Demo_Applet.getIcon(Demo_Applet.this.webdemo),
					this.ruleEvaluator.getCompilationUnit(),
					this.ruleEvaluator.getDocument());
		}

		@Override
		public JButton getButtonEvaluate() {
			return Demo_Applet.this.bt_rifEvaluate;
		}

		@Override
		public JButton getButtonEvalDemo() {
			return Demo_Applet.this.bt_rifEvalDemo;
		}

		@Override
		public JButton getButtonMeasureExecutionTimes() {
			return Demo_Applet.this.bt_rifMeasureExecutionTimes;
		}

		@Override
		public QueryEvaluator<Node> getEvaluator() {
			return this.ruleEvaluator;
		}

		@Override
		public boolean evaluatorSuitableForDemo(final String s) {
			return s.compareTo("Jena")!=0 && s.compareTo("Sesame")!=0;
		}

		@Override
		public boolean evaluatorSuitableForEvaluation(final String s) {
			return this.evaluatorSuitableForDemo(s);
		}

		@Override
		public long prepareInputData(final Collection<URILiteral> defaultGraphsParameter, final LinkedList<URILiteral> namedGraphs) throws Exception {
			Demo_Applet.this.materializationInfo = null;
			return this.ruleEvaluator.prepareInputData(defaultGraphsParameter, namedGraphs);
		}
	}

	/**
	 * Evaluates the given query on the given data.
	 */
	protected void evaluateSPARQLQuery(final EvaluationMode mode) {
		try {
			this.evaluate(new SPARQLEvaluation(this.setupEvaluator(mode)), mode);
		} catch (final Throwable t) {
			// ignore forwarded Throwable!
			this.enableOrDisableButtons(true);
		}
	}

	protected void evaluateRIFRule(final EvaluationMode mode) {
		try {
			this.evaluate(new RIFEvaluation(this.setupEvaluator(mode)), mode);
		} catch (final Throwable t) {
			// ignore forwarded Throwable!
			this.enableOrDisableButtons(false);
		}
	}

	@SuppressWarnings("rawtypes")
	protected void evaluate(final Evaluation evaluation, final EvaluationMode mode) {

		this.resultpanel = new JPanel(new BorderLayout());
		// set the empty resultpanel as component for the result tab
		this.tabbedPane_globalMainpane.setComponentAt(
				this.tabbedPane_globalMainpane.indexOfTab(this.TAB_TITLE_RESULT),
				this.resultpanel);

		evaluation.getButtonEvaluate().setEnabled(false);
		evaluation.getButtonEvalDemo().setEnabled(false);
		evaluation.getButtonMeasureExecutionTimes().setEnabled(false);

		this.query = evaluation.getQuery(); // get query

		if (this.query.compareTo("") == 0) { // no query given...
			this.displayErrorMessage("Error: empty query", true);
			evaluation.enableButtons();
		} else { // evaluate query...
			try {
				try {
					final QueryEvaluator<Node> evaluator = evaluation.getEvaluator();
					final URILiteral rdfURL = LiteralFactory.createStringURILiteral("<inlinedata:" + this.data + ">");
					this.defaultGraphs = new LinkedList<URILiteral>();
					this.defaultGraphs.add(rdfURL);

					if (mode == EvaluationMode.DEMO
							|| mode == EvaluationMode.TIMES) {
						this.prefixInstance = new ViewerPrefix(this.usePrefixes.isTrue());
						final long prepareInputData = evaluation.prepareInputData(this.defaultGraphs, new LinkedList<URILiteral>());

						try {
							System.out.println("Compile query...");
							try {
								final long compileQuery = evaluation.compileQuery(this.query);

								System.out.println("Logical optimization...");
								final long logicalOptimization = evaluator.logicalOptimization();

								System.out.println("Physical optimization...");
								final long physicalOptimization = evaluator.physicalOptimization();

								if (mode == EvaluationMode.DEMO) {
									final EvaluationDemoToolBar bottomToolBar = new EvaluationDemoToolBar(this.webdemo != DEMO_ENUM.ECLIPSE);

									final Result result = (evaluator instanceof BasicIndexRuleEvaluator)? ((BasicIndexRuleEvaluator)evaluator).getResultOperator() : ((CommonCoreQueryEvaluator<Node>)evaluator).getResultOperator();

									final ShowResult sr = new ShowResult(bottomToolBar, result);

									evaluator.prepareForQueryDebugSteps(bottomToolBar);

									System.out.println("Evaluate query ...");
									final Thread thread = new Thread() {
										@Override
										public void run() {
											try {
												evaluator.evaluateQueryDebugSteps(bottomToolBar, sr);
												bottomToolBar.endOfEvaluation();
												Demo_Applet.this.enableOrDisableButtons(evaluation instanceof SPARQLEvaluation);
											} catch (final Exception e) {
												System.err
												.println(e);
												e.printStackTrace();
											}
										}
									};
									bottomToolBar.setEvaluationThread(thread);
									thread.start();

									final BasicOperator root = (evaluator instanceof BasicIndexRuleEvaluator)? ((BasicIndexRuleEvaluator)evaluator).getRootNode() :((CommonCoreQueryEvaluator<Node>) evaluator).getRootNode();

									this.operatorGraphViewer = new Viewer(
											new GraphWrapperBasicOperator(
													root),
													this.prefixInstance,
													"Evaluation Demo",
													false,
													this.webdemo != DEMO_ENUM.ECLIPSE,
													bottomToolBar);
									bottomToolBar.setOperatorGraphViewer(this.operatorGraphViewer);
								} else {

									final JTextArea ta_prefixes = new JTextArea();
									ta_prefixes.setEditable(false);
									ta_prefixes.setFont(new Font("Courier New", Font.PLAIN, 12));

									System.out.println("Evaluate query ...");
									if(evaluator instanceof CommonCoreQueryEvaluator){
										((CommonCoreQueryEvaluator)evaluator).getResultOperator().addApplication(new IterateOneTimeThrough());
									}

									final long evaluateQuery = evaluator.evaluateQuery();
									final int times = xpref.datatypes.IntegerDatatype.getFirstValue("repetitionsOfExecution");
									if(times >1){
										long compileQueryTime = 0;
										long logicalOptimizationTime = 0;
										long physicalOptimizationTime = 0;
										long evaluateQueryTime = 0;
										long totalTime = 0;
										final long[] compileQueryTimeArray = new long[times];
										final long[] logicalOptimizationTimeArray = new long[times];
										final long[] physicalOptimizationTimeArray = new long[times];
										final long[] evaluateQueryTimeArray = new long[times];
										final long[] totalTimeArray = new long[times];
										for (int i = 0; i < times; i++) {
											compileQueryTimeArray[i] = evaluator.compileQuery(this.query);
											compileQueryTime += compileQueryTimeArray[i];
											logicalOptimizationTimeArray[i] = evaluator.logicalOptimization();
											logicalOptimizationTime += logicalOptimizationTimeArray[i];
											physicalOptimizationTimeArray[i] = evaluator.physicalOptimization();
											physicalOptimizationTime += physicalOptimizationTimeArray[i];
											if(evaluator instanceof CommonCoreQueryEvaluator){
												((CommonCoreQueryEvaluator)evaluator).getResultOperator().addApplication(new IterateOneTimeThrough());
											}
											evaluateQueryTimeArray[i] = evaluator.evaluateQuery();
											evaluateQueryTime += evaluateQueryTimeArray[i];
											totalTimeArray[i] = compileQueryTimeArray[i] + logicalOptimizationTimeArray[i] + physicalOptimizationTimeArray[i] + evaluateQueryTimeArray[i];
											totalTime += totalTimeArray[i];
										}
										String result = "Evaluator " + this.cobo_evaluator.getSelectedItem().toString() + "\n\nBuild indices              : " + ((double) prepareInputData / 1000);
										result += "\n\n(I) Time in seconds to compile query:\nAvg" + QueryEvaluator.toString(compileQueryTimeArray) + "/1000 = " + (((double) compileQueryTime) / times) / 1000;
										result += "\nStandard deviation of the sample: " + QueryEvaluator.computeStandardDeviationOfTheSample(compileQueryTimeArray) / 1000;
										result += "\nSample standard deviation       : " + QueryEvaluator.computeSampleStandardDeviation(compileQueryTimeArray) / 1000;
										result += "\n\n(II) Time in seconds used for logical optimization:\nAvg" + QueryEvaluator.toString(logicalOptimizationTimeArray) + "/1000 = " + (((double) logicalOptimizationTime) / times) / 1000;
										result += "\nStandard deviation of the sample: " + QueryEvaluator.computeStandardDeviationOfTheSample(logicalOptimizationTimeArray) / 1000;
										result += "\nSample standard deviation       : " + QueryEvaluator.computeSampleStandardDeviation(logicalOptimizationTimeArray) / 1000;
										result += "\n\n(III) Time in seconds used for physical optimization:\nAvg" + QueryEvaluator.toString(physicalOptimizationTimeArray) + "/1000 = " + (((double) physicalOptimizationTime) / times) / 1000;
										result += "\nStandard deviation of the sample: " + QueryEvaluator.computeStandardDeviationOfTheSample(physicalOptimizationTimeArray) / 1000;
										result += "\nSample standard deviation       : " + QueryEvaluator.computeSampleStandardDeviation(physicalOptimizationTimeArray) / 1000;
										result += "\n\n(IV) Time in seconds to evaluate query:\nAvg" + QueryEvaluator.toString(evaluateQueryTimeArray) + "/1000 = " + (((double) evaluateQueryTime) / times) / 1000;
										result += "\nStandard deviation of the sample: " + QueryEvaluator.computeStandardDeviationOfTheSample(evaluateQueryTimeArray) / 1000;
										result += "\nSample standard deviation       : " + QueryEvaluator.computeSampleStandardDeviation(evaluateQueryTimeArray) / 1000;
										result += "\n\nTotal time in seconds (I)+(II)+(III)+(IV):\nAvg" + QueryEvaluator.toString(totalTimeArray) + "/1000 = " + (((double) totalTime) / times) / 1000;
										result += "\nStandard deviation of the sample: " + QueryEvaluator.computeStandardDeviationOfTheSample(totalTimeArray) / 1000;
										result += "\nSample standard deviation       : " + QueryEvaluator.computeSampleStandardDeviation(totalTimeArray) / 1000;
										ta_prefixes.setText(result);
									} else {
										ta_prefixes.setText("Evaluator "
												+ this.cobo_evaluator.getSelectedItem().toString()
												+ "\n\nBuild indices              : " + ((double) prepareInputData / 1000)
												+ "\n\nTotal time query processing: " + ((double) (compileQuery + logicalOptimization + physicalOptimization + evaluateQuery) / 1000)
												+ ((evaluator instanceof JenaQueryEvaluator || evaluator instanceof SesameQueryEvaluator) ? ""
														: "\n    - Compile query        : "
														+ ((double) compileQuery / 1000)
														+ "\n    - Logical optimization : "
														+ ((double) logicalOptimization / 1000)
														+ "\n    - Physical optimization: "
														+ ((double) physicalOptimization / 1000)
														+ "\n    - Evaluation           : "
														+ ((double) evaluateQuery / 1000)));
									}
									final JFrame frame1 = new JFrame("Execution times in seconds");
									frame1.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
									frame1.setLocationRelativeTo(Demo_Applet.this);

									final JScrollPane scroll = new JScrollPane(ta_prefixes);

									frame1.add(scroll);
									frame1.pack();
									frame1.setVisible(true);
								}
							} catch (final Throwable t) {
								this.dealWithThrowable(t, mode, evaluation instanceof SPARQLEvaluation);
							}
						} catch (final Throwable t) {
							this.dealWithThrowable(t, mode, evaluation instanceof SPARQLEvaluation);
						}
						evaluation.enableButtons();

					} else {

						try {
							this.prefixInstance = new ViewerPrefix(this.usePrefixes.isTrue());
							evaluation.prepareInputData(this.defaultGraphs, new LinkedList<URILiteral>());

							System.out.println("Compile query...");
							try {
								try {

									this.debugViewerCreator = evaluation.compileQueryDebugByteArray(this.query);

									if (this.debugViewerCreator != null) {
										this.ruleApplications = this.debugViewerCreator.getCorrectOperatorGraphRules();
									} else {
										this.ruleApplications = null;
									}

									System.out.println("Logical optimization...");

									if (this.ruleApplications != null) {
										this.ruleApplications.addAll(evaluator.logicalOptimizationDebugByteArray(this.prefixInstance));
									} else {
										this.ruleApplications = evaluator.logicalOptimizationDebugByteArray(this.prefixInstance);
									}

									System.out.println("Physical optimization...");

									if (this.ruleApplications != null) {
										this.ruleApplications.addAll(evaluator.physicalOptimizationDebugByteArray(this.prefixInstance));
									} else {
										this.ruleApplications = evaluator.physicalOptimizationDebugByteArray(this.prefixInstance);
									}

									System.out.println("Evaluate query ...");

									if (evaluator instanceof CommonCoreQueryEvaluator || evaluator instanceof BasicIndexRuleEvaluator) {
										final CollectRIFResult crr = new CollectRIFResult(false);
										final Result resultOperator = (evaluator instanceof CommonCoreQueryEvaluator)?((CommonCoreQueryEvaluator<Node>)evaluator).getResultOperator(): ((BasicIndexRuleEvaluator)evaluator).getResultOperator();
										resultOperator.addApplication(crr);
										evaluator.evaluateQuery();
										this.resultQueryEvaluator = crr.getQueryResults();
									} else {
										this.resultQueryEvaluator = new QueryResult[1];
										this.resultQueryEvaluator[0] = evaluator.getResult();
									}

									System.out.println("\nQuery Result:");
									for (final QueryResult qr : this.resultQueryEvaluator) {
										System.out.println(qr);
									}
									System.out.println("----------------Done.");

									if (this.isApplet) {
										this.setSize(800, 905);
										this.repaint();
									}

									this.outputResult();

									// set resultpanel as component for the result tab and make the tab active
									this.tabbedPane_globalMainpane.setComponentAt(
											this.tabbedPane_globalMainpane.indexOfTab(this.TAB_TITLE_RESULT),
											this.resultpanel);
									this.tabbedPane_globalMainpane.setSelectedComponent(this.resultpanel);
								} catch (final Throwable t) {
									this.dealWithThrowable(t, mode, evaluation instanceof SPARQLEvaluation);
								}
							} catch (final Throwable t) {
								this.dealWithThrowable(t, mode, evaluation instanceof SPARQLEvaluation);
							}
						} catch (final Throwable t) {
							this.dealWithThrowable(t, mode, evaluation instanceof SPARQLEvaluation);
						}

						evaluation.enableButtons();
					}
				} catch (final Throwable t) {
					// ignore forwarded Throwable!
					evaluation.enableButtons();
				}
			} catch (final Throwable t) {
				this.dealWithThrowable(t, mode, evaluation instanceof SPARQLEvaluation);

				evaluation.enableButtons();
			}
			// append an empty string to the error textarea to force the system to flush the System.err
			this.displayErrorMessage("", true);
		}
	}

	protected boolean dealWithThrowableFromQueryParser(final Throwable e,
			final EvaluationMode mode, final boolean queryOrRif) {
		if (e instanceof TokenMgrError) {
			final TokenMgrError tme = (TokenMgrError) e;
			this.displayErrorMessage(tme.getMessage(), false, queryOrRif);

			// create the pattern to match
			// and create a matcher against the string
			final Pattern pattern = Pattern
					.compile("line (\\d+), column (\\d+)");
			final Matcher matcher = pattern.matcher(tme.getMessage());

			// try to find the pattern in the message...
			if (matcher.find() == true) {
				// get matches...
				final int line = Integer.parseInt(matcher.group(1));
				final int column = Integer.parseInt(matcher.group(2));

				this.setErrorPosition(line, column, queryOrRif);
			}
			if (mode == EvaluationMode.DEMO) {
				this.enableOrDisableButtons(queryOrRif);
			}
			return true;
		} else	if (e instanceof lupos.rif.generated.parser.TokenMgrError) {
			final lupos.rif.generated.parser.TokenMgrError tme = (lupos.rif.generated.parser.TokenMgrError) e;
			this.displayErrorMessage(tme.getMessage(), false, queryOrRif);

			// create the pattern to match
			// and create a matcher against the string
			final Pattern pattern = Pattern
					.compile("line (\\d+), column (\\d+)");
			final Matcher matcher = pattern.matcher(tme.getMessage());

			// try to find the pattern in the message...
			if (matcher.find() == true) {
				// get matches...
				final int line = Integer.parseInt(matcher.group(1));
				final int column = Integer.parseInt(matcher.group(2));

				this.setErrorPosition(line, column, queryOrRif);
			}
			if (mode == EvaluationMode.DEMO) {
				this.enableOrDisableButtons(queryOrRif);
			}
			return true;
		} else if (e instanceof ParseException) {
			final ParseException pe = (ParseException) e;
			this.displayErrorMessage(pe.getMessage(), false, queryOrRif);

			int line;
			int column;

			// get precise line and column...
			if (pe.currentToken.next == null) {
				line = pe.currentToken.beginLine;
				column = pe.currentToken.beginColumn;
			} else {
				line = pe.currentToken.next.beginLine;
				column = pe.currentToken.next.beginColumn;
			}

			this.setErrorPosition(line, column, queryOrRif);
			if (mode == EvaluationMode.DEMO) {
				this.enableOrDisableButtons(queryOrRif);
			}
			return true;
		} else if(e instanceof lupos.rif.generated.parser.ParseException){
			final lupos.rif.generated.parser.ParseException pe = (lupos.rif.generated.parser.ParseException) e;
			this.displayErrorMessage(pe.getMessage(), false, queryOrRif);

			int line;
			int column;

			// get precise line and column...
			if (pe.currentToken.next == null) {
				line = pe.currentToken.beginLine;
				column = pe.currentToken.beginColumn;
			} else {
				line = pe.currentToken.next.beginLine;
				column = pe.currentToken.next.beginColumn;
			}

			this.setErrorPosition(line, column, queryOrRif);
			if (mode == EvaluationMode.DEMO) {
				this.enableOrDisableButtons(queryOrRif);
			}
			return true;
		} else if (e instanceof QueryParseException) {
			final QueryParseException qpe = (QueryParseException) e;
			this.displayErrorMessage(qpe.getMessage(), false, queryOrRif);

			// create the pattern to match
			// and create a matcher against the string
			final Pattern pattern = Pattern.compile(
					"line (\\d+), column (\\d+)", Pattern.CASE_INSENSITIVE);
			final Matcher matcher = pattern.matcher(qpe.getMessage());

			// try to find the pattern in the message...
			if (matcher.find() == true) {
				// get matches...
				final int line = Integer.parseInt(matcher.group(1));
				final int column = Integer.parseInt(matcher.group(2));

				this.setErrorPosition(line, column, queryOrRif);
			}
			if (mode == EvaluationMode.DEMO) {
				this.enableOrDisableButtons(queryOrRif);
			}
			return true;
		} else if (e instanceof MalformedQueryException) {
			final MalformedQueryException mqe = (MalformedQueryException) e;
			this.displayErrorMessage(mqe.getMessage(), false, queryOrRif);

			// create the pattern to match
			// and create a matcher against the string
			final Pattern pattern = Pattern
					.compile("line (\\d+), column (\\d+)");
			final Matcher matcher = pattern.matcher(mqe.getMessage());

			// try to find the pattern in the message...
			if (matcher.find() == true) {
				// get matches...
				final int line = Integer.parseInt(matcher.group(1));
				final int column = Integer.parseInt(matcher.group(2));

				this.setErrorPosition(line, column, queryOrRif);
			}
			if (mode == EvaluationMode.DEMO) {
				this.enableOrDisableButtons(queryOrRif);
			}
			return true;
		}
		return false;
	}

	protected void setErrorPosition(final int line, final int column, final boolean queryOrRif){
		if(queryOrRif){
			this.tp_queryInput.setErrorPosition(line, column);
		} else {
			this.tp_rifInput.setErrorPosition(line, column);
		}
	}

	protected void dealWithThrowable(final Throwable e, final EvaluationMode mode, final boolean queryOrRif) {
		if (this.dealWithThrowableFromQueryParser(e, mode, queryOrRif)) {
			return;
		}
		if (e instanceof TurtleParseException) {
			final TurtleParseException n3e = (TurtleParseException) e;
			this.displayDataErrorMessage(n3e.getMessage(), false);

			// create the pattern to match
			// and create a matcher against the string
			final Pattern pattern = Pattern.compile("\\[(\\d+):(\\d+)\\]");
			final Matcher matcher = pattern.matcher(n3e.getMessage());

			final Pattern pattern2 = Pattern.compile("Line (\\d+): ");
			final Matcher matcher2 = pattern2.matcher(n3e.getMessage());

			int line = -1;
			int column = -1;

			// try to find the pattern in the message...
			if (matcher.find() == true) {
				// get matches...
				line = Integer.parseInt(matcher.group(1));
				column = Integer.parseInt(matcher.group(2));
			} else if (matcher2.find() == true) {
				// get matches....
				line = Integer.parseInt(matcher2.group(1));
				column = 1;
			}

			if (line != -1 && column != -1) {
				this.tp_dataInput.setErrorPosition(line, column);
			}
			if (mode == EvaluationMode.DEMO) {
				this.enableOrDisableButtons(queryOrRif);
			}
		} else if (e instanceof RDFParseException) {
			final RDFParseException rdfpe = (RDFParseException) e;
			this.displayDataErrorMessage(rdfpe.getMessage(), false);

			// get precise line and column...
			final int line = rdfpe.getLineNumber();
			int column = rdfpe.getColumnNumber();

			if (column == -1) {
				column = 1;
			}

			this.tp_dataInput.setErrorPosition(line, column);
			if (mode == EvaluationMode.DEMO) {
				this.enableOrDisableButtons(queryOrRif);
			}
		} else {
			// do not use System.err.println(...) as Java
			// Web
			// Start
			// forbids to redirect System.err, such that
			// nor error message would be printed!
			this.displayErrorMessage(e.toString(), false, queryOrRif);
			e.printStackTrace();
			if (mode == EvaluationMode.DEMO) {
				this.enableOrDisableButtons(queryOrRif);
			}
		}

	}

	protected void outputResult(){
		try{
			final Container contentPane = (this.isApplet) ? this.getContentPane() : this.frame.getContentPane();

			ResultPanelHelper.setupResultPanel(this.resultpanel, this.resultQueryEvaluator, this.debugViewerCreator, this.materializationInfo, this.inferenceRules, this.ruleApplicationsForMaterialization, this.errorsInOntology, this.usePrefixes, this.prefixInstance, contentPane );

		} catch (final Exception ex) {
			// this.ta_errors.setText(ex.toString());
			this.displayErrorMessage(ex.toString(), false);

			ex.printStackTrace();
		}

		// append an empty string to the error textarea to get the system to
		// flush the System.err
		// this.ta_errors.append("");
		this.displayErrorMessage("", true);
	}



	protected void setGlobalFont(final Font font) {
		final Enumeration<Object> keys = UIManager.getDefaults().keys();

		while (keys.hasMoreElements()) {
			final Object key = keys.nextElement();

			if (UIManager.get(key) instanceof javax.swing.plaf.FontUIResource) {
				UIManager.put(key, new FontUIResource(font));
			}
		}
	}

	@Override
	public void preferencesChanged() {
		final int lp_query_pos = this.tp_queryInput.getCaretPosition();
		final int lp_data_pos = this.tp_dataInput.getCaretPosition();
		final int lp_rif_pos = this.tp_rifInput.getCaretPosition();

		this.removeLinePainterAndErrorLinePainter();
		this.loadLookAndFeel();
		this.loadMainFont();

		this.loadSyntaxHighlighting();
		this.loadTextFieldFont();
		this.loadLineNumbers();

		if (this.isApplet) {
			SwingUtilities.updateComponentTreeUI(this);
		} else {
			SwingUtilities.updateComponentTreeUI(this.frame);
		}

		this.tp_queryInput.setCaretPosition(lp_query_pos);
		this.tp_rifInput.setCaretPosition(lp_rif_pos);
		this.tp_dataInput.setCaretPosition(lp_data_pos);

		this.loadCurrentLineColor();
		this.loadErrorLineColor();
	}

	protected void loadMainFont() {
		try {
			if (BooleanDatatype.getValues("standardFont.fontEnable").get(0)
					.booleanValue()) {
				this.setGlobalFont(FontDatatype.getValues("standardFont.font")
						.get(0));
			}
		} catch (final Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	protected void loadLookAndFeel() {
		if (this.defaultFont != null) {
			this.setGlobalFont(this.defaultFont);
		}

		final HashMap<String, String> classesLAF = new HashMap<String, String>();

//		classesLAF.put("Acryl", "com.jtattoo.plaf.acryl.AcrylLookAndFeel");
//		classesLAF.put("Aero", "com.jtattoo.plaf.aero.AeroLookAndFeel");
//		classesLAF.put("Aluminium",
//				"com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
//		classesLAF.put("Bernstein",
//				"com.jtattoo.plaf.bernstein.BernsteinLookAndFeel");
//		classesLAF.put("Fast", "com.jtattoo.plaf.fast.FastLookAndFeel");
//		classesLAF.put("HiFi", "com.jtattoo.plaf.hifi.HiFiLookAndFeel");
//		classesLAF.put("Luna", "com.jtattoo.plaf.luna.LunaLookAndFeel");
//		classesLAF.put("McWin", "com.jtattoo.plaf.mcwin.McWinLookAndFeel");
//		classesLAF.put("Mint", "com.jtattoo.plaf.mint.MintLookAndFeel");
//		classesLAF.put("Noire", "com.jtattoo.plaf.noire.NoireLookAndFeel");
//		classesLAF.put("Smart", "com.jtattoo.plaf.smart.SmartLookAndFeel");

		final UIManager.LookAndFeelInfo[] lafInfo = UIManager
				.getInstalledLookAndFeels();

		for (int i = 0; i < lafInfo.length; i++) {
			classesLAF.put(lafInfo[i].getName(), lafInfo[i].getClassName());
		}
		try {
			String chosen = CollectionDatatype.getValues("lookAndFeel").get(0);
			try {
				// reset to default theme
				if (chosen.compareTo("Metal") == 0) {
					javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new javax.swing.plaf.metal.DefaultMetalTheme());
				} else if (chosen.compareTo("Metal Ocean") == 0) {
					javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new javax.swing.plaf.metal.OceanTheme());
					chosen = "Metal";
				}
//				} else if (chosen.compareTo("Fast") == 0) {
//					com.jtattoo.plaf.fast.FastLookAndFeel.setTheme("Default");
//				} else if (chosen.compareTo("Smart") == 0) {
//					com.jtattoo.plaf.smart.SmartLookAndFeel.setTheme("Default");
//				} else if (chosen.compareTo("Acryl") == 0) {
//					com.jtattoo.plaf.acryl.AcrylLookAndFeel.setTheme("Default");
//				} else if (chosen.compareTo("Aero") == 0) {
//					com.jtattoo.plaf.aero.AeroLookAndFeel.setTheme("Default");
//				} else if (chosen.compareTo("Bernstein") == 0) {
//					com.jtattoo.plaf.bernstein.BernsteinLookAndFeel
//							.setTheme("Default");
//				} else if (chosen.compareTo("Aluminium") == 0) {
//					com.jtattoo.plaf.aluminium.AluminiumLookAndFeel
//							.setTheme("Default");
//				} else if (chosen.compareTo("McWin") == 0) {
//					com.jtattoo.plaf.mcwin.McWinLookAndFeel.setTheme("Default");
//				} else if (chosen.compareTo("Mint") == 0) {
//					com.jtattoo.plaf.mint.MintLookAndFeel.setTheme("Default");
//				} else if (chosen.compareTo("Hifi") == 0) {
//					com.jtattoo.plaf.hifi.HiFiLookAndFeel.setTheme("Default");
//				} else if (chosen.compareTo("Noire") == 0) {
//					com.jtattoo.plaf.noire.NoireLookAndFeel.setTheme("Default");
//				} else if (chosen.compareTo("Luna") == 0) {
//					com.jtattoo.plaf.luna.LunaLookAndFeel.setTheme("Default");
//				}

				final String lookAndFeel = classesLAF.get(chosen);
				UIManager.setLookAndFeel(lookAndFeel);

				this.defaultFont = UIManager.getFont("Label.font");

				this.repaint();
			} catch (final ClassNotFoundException e) {
				System.err
						.println("Couldn't find class for specified look and feel:"
								+ classesLAF.get(chosen));
				System.err
						.println("Did you include the L&F library in the class path?");
				System.err.println("Using the default look and feel.");

				e.printStackTrace();
			} catch (final UnsupportedLookAndFeelException e) {
				System.err.println("Can't use the specified look and feel ("
						+ classesLAF.get(chosen) + ") on this platform.");
				System.err.println("Using the default look and feel.");

				e.printStackTrace();
			} catch (final Exception e) {
				System.err.println("Couldn't get specified look and feel ("
						+ classesLAF.get(chosen) + "), for some reason.");
				System.err.println("Using the default look and feel.");

				e.printStackTrace();
			}
		} catch (final Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	protected void removeLinePainterAndErrorLinePainter() {
		removeLinePainter(this.lp_queryInput, this.tp_queryInput);
		removeLinePainter(this.lp_rifInput, this.tp_rifInput);
		removeLinePainter(this.lp_dataInput, this.tp_dataInput);

		this.tp_queryInput.disableErrorLine();
		this.tp_dataInput.disableErrorLine();
	}

	protected void loadCurrentLineColor() {
		try {
			if (BooleanDatatype.getValues("currentLineColor.colorEnable")
					.get(0).booleanValue()) {
				final int alphaValue = IntegerDatatype
						.getValues("currentLineColor.colorTransparency").get(0)
						.intValue();

				final Color color = ColorDatatype.getValues(
						"currentLineColor.color").get(0);

				this.lp_color = new Color(color.getRed(), color.getGreen(),
						color.getBlue(), alphaValue);

				this.lp_queryInput = new LinePainter(this.tp_queryInput,
						this.lp_color);
				this.lp_rifInput = new LinePainter(this.tp_rifInput,
						this.lp_color);
				this.lp_dataInput = new LinePainter(this.tp_dataInput,
						this.lp_color);
			} else {
				removeLinePainter(this.lp_queryInput, this.tp_queryInput);
				removeLinePainter(this.lp_rifInput, this.tp_rifInput);
				removeLinePainter(this.lp_dataInput, this.tp_dataInput);
			}
		} catch (final Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	protected static void removeLinePainter(final LinePainter lp, final JTextPane tp) {
		if (lp != null) {
			lp.removeLinePainter();
			tp.revalidate();
			tp.repaint();
		}
	}

	protected void loadErrorLineColor() {
		try {
			if (BooleanDatatype.getValues("errorLine.colorEnable").get(0)
					.booleanValue()) {
				this.tp_queryInput.setErrorLineStatus(true);
				this.tp_dataInput.setErrorLineStatus(true);

				final int alphaValue = IntegerDatatype
						.getValues("errorLine.colorTransparency").get(0)
						.intValue();

				final Color color = ColorDatatype.getValues("errorLine.color")
						.get(0);

				final Color errorLineColor = new Color(color.getRed(),
						color.getGreen(), color.getBlue(), alphaValue);

				this.tp_queryInput.setErrorLineColor(errorLineColor);
				this.tp_dataInput.setErrorLineColor(errorLineColor);
			} else {
				this.tp_queryInput.setErrorLineStatus(false);
				this.tp_dataInput.setErrorLineStatus(false);
			}
		} catch (final Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	protected void loadTextFieldFont() {
		try {
			Font tfFont;

			if (BooleanDatatype.getValues("textFieldFont.fontEnable").get(0)
					.booleanValue()) {
				tfFont = FontDatatype.getValues("textFieldFont.font").get(0);
			} else {
				tfFont = UIManager.getFont("TextPane.font");
			}

			this.tp_queryInput.setFont(tfFont);
			this.tp_rifInput.setFont(tfFont);
			this.tp_dataInput.setFont(tfFont);
			// this.ta_errors.setFont(tfFont);
			this.ta_dataInputErrors.setFont(tfFont);
			this.ta_queryInputErrors.setFont(tfFont);
			this.ta_rifInputErrors.setFont(tfFont);
		} catch (final Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	protected void loadLineNumbers() {
		try {
			if (BooleanDatatype.getValues("lineCount").get(0).booleanValue()) {
				this.queryInputSP.setRowHeaderView(new LineNumbers(
						this.tp_queryInput));
				this.rifInputSP.setRowHeaderView(new LineNumbers(
						this.tp_rifInput));
				this.dataInputSP.setRowHeaderView(new LineNumbers(
						this.tp_dataInput));
			} else {
				this.queryInputSP.setRowHeaderView(new JLabel());
				this.rifInputSP.setRowHeaderView(new JLabel());
				this.dataInputSP.setRowHeaderView(new JLabel());
			}
		} catch (final Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	protected void loadSyntaxHighlighting() {
		try {
			final boolean highlighterStatus = BooleanDatatype
					.getValues("syntaxHighlighting").get(0).booleanValue();

			if (!highlighterStatus) { // no syntax highlighting...
				LANGUAGE.SEMANTIC_WEB.setBlankStyles();
				((LuposDocument) this.tp_queryInput.getDocument()).colorOneTimeAll(); // highlight
				((LuposDocument) this.tp_queryInput.getDocument()).setIgnoreColoring(true); // disable highlighter

				((LuposDocument) this.tp_rifInput.getDocument()).colorOneTimeAll(); // highlight
				((LuposDocument) this.tp_rifInput.getDocument()).setIgnoreColoring(true); // disable highlighter

				((LuposDocument) this.tp_dataInput.getDocument()).colorOneTimeAll(); // highlight
				((LuposDocument) this.tp_dataInput.getDocument()).setIgnoreColoring(true); // disable highlighter
			} else { // syntax highlighting...
				LANGUAGE.SEMANTIC_WEB.setStyles();
				((LuposDocument) this.tp_queryInput.getDocument()).setIgnoreColoring(false); // enable highlighter
				((LuposDocument) this.tp_queryInput.getDocument()).colorOneTimeAll(); // highlight

				((LuposDocument) this.tp_rifInput.getDocument()).setIgnoreColoring(false); // enable highlighter
				((LuposDocument) this.tp_rifInput.getDocument()).colorOneTimeAll(); // highlight

				((LuposDocument) this.tp_dataInput.getDocument()).setIgnoreColoring(false); // enable highlighter
				((LuposDocument) this.tp_dataInput.getDocument()).colorOneTimeAll(); // highlight
			}
		} catch (final Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	public LuposJTextPane getTp_dataInput() {
		return this.tp_dataInput;
	}

	@Override
	public void setSerializedData(final String n3daten) {
		this.tp_dataInput.setText(n3daten);
	}

	@Override
	public void setSerializedQuery(final String query) {
		this.tp_queryInput.setText(query);
	}

	@Override
	public String getData() {
		return this.tp_dataInput.getText();
	}

	/*
	 * Display an error message inside both the 'SPARQL query' tab and the 'RDF
	 * data' tab
	 *
	 * @param error message to display
	 *
	 * @param append if true, the message will be appended to the current
	 * content of the error box if false, the previous content of the error box
	 * is deleted
	 */
	protected void displayErrorMessage(final String error, final boolean append) {
		this.displayDataErrorMessage(error, append);
		this.displayRifErrorMessage(error, append);
		this.displayQueryErrorMessage(error, append);
	}

	protected void displayErrorMessage(final String error, final boolean append,
			final JTextArea ta_inputErrors, final int index) {
		if (ta_inputErrors != null) {
			if (append) {
				ta_inputErrors.append(error);
			} else {
				ta_inputErrors.setText(error);
			}
		}
		if (error.compareTo("") != 0) {
			this.tabbedPane_globalMainpane.setSelectedIndex(index);
		}
	}

	/*
	 * Display an error message in the error box inside the 'RDF data' tab
	 *
	 * @param dataError message to display
	 *
	 * @param append if true, the message will be appended to the current
	 * content of the error box if false, the previous content of the error box
	 * is deleted
	 */
	protected void displayDataErrorMessage(final String dataError,
			final boolean append) {
		this.displayErrorMessage(dataError, append, this.ta_dataInputErrors, 2);
	}

	protected void displayErrorMessage(final String queryError, final boolean append, final boolean queryOrRif){
		if(queryOrRif){
			this.displayQueryErrorMessage(queryError, append);
		} else {
			this.displayRifErrorMessage(queryError, append);
		}
	}

	/*
	 * Display an error message in the error box inside the 'SPARQL query' tab
	 *
	 * @param queryError message to display
	 *
	 * @param append if true, the message will be appended to the current
	 * content of the error box if false, the previous content of the error box
	 * is deleted
	 */
	protected void displayQueryErrorMessage(final String queryError,
			final boolean append) {
		this.displayErrorMessage(queryError, append, this.ta_queryInputErrors, 0);
	}

	/*
	 * Display an error message in the error box inside the 'RIF rules' tab
	 *
	 * @param ruleError message to display
	 *
	 * @param append if true, the message will be appended to the current
	 * content of the error box if false, the previous content of the error box
	 * is deleted
	 */
	protected void displayRifErrorMessage(final String rifError,
			final boolean append) {
		this.displayErrorMessage(rifError, append, this.ta_rifInputErrors, 1);
	}

}