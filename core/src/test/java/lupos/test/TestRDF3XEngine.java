/**
 * 
 */
package lupos.test;

import java.util.Iterator;

import lupos.datastructures.bindings.Bindings;
import lupos.datastructures.items.Variable;
import lupos.datastructures.queryresult.QueryResult;
import lupos.engine.evaluators.BasicIndexQueryEvaluator;
import lupos.engine.evaluators.CommonCoreQueryEvaluator;
import lupos.engine.evaluators.QueryEvaluator;
import lupos.engine.evaluators.RDF3XQueryEvaluator;
import lupos.sparql1_1.Node;

/**
 * This class shows how to use the RDF3X Query Evaluator to process a SPARQL
 * query
 */
public class TestRDF3XEngine {

	private final static String query1 = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE{ ?s rdf:type ?o. }";
	private final static String query2 = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE{ ?s rdf:type ?class. ?s ?p ?o.}";
	private final static String updateQuery1 = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> INSERT DATA { <a> rdf:type <b> }";
	private final static String query3 = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE{ <a> rdf:type ?o. }";
	private final static String updateQuery2 = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> DELETE DATA { <a> rdf:type <b> }";
	private final static String updateQuery3 = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> INSERT { ?s rdf:type2 ?o } WHERE { ?s rdf:type ?o. }";
	private final static String query4 = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE{ ?s rdf:type2 ?o. }";

	/**
	 * Remark: Before using this program, please import
	 * your input data and construct the indices by using
	 * the class lupos.engine.indexconstruction.RDF3XIndexConstruction
	 * 
	 * Command line entry point of the program: 
	 * The command line parameter is
	 * <directory for indices>
	 * for the directory, where the indices are stored.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		try {
			if(args.length<1){
				System.err.println("Usage:\njava -Xmx768M lupos.test.TestRDF3XEngine <directory for indices>");
				return;
			}
			
			final String dir = args[0];
	
			// set up parameters of evaluator and initialize the evaluator...
			final RDF3XQueryEvaluator evaluator = new RDF3XQueryEvaluator();
			evaluator.loadLargeScaleIndices(dir);
		} catch (final Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}

	}
	
	public final static void test(BasicIndexQueryEvaluator evaluator){
		try {
			
			System.out.println("Evaluate queries...");
			
			System.out.println("Query 1...");
			// evaluate first query and print out the result!
			evaluateQueryAndPrintOut(evaluator, query1);
			
			System.out.println("Query 2...");
			// evaluate second query and print out the result!
			evaluateQueryAndPrintOut(evaluator, query2);
			
			System.out.println("Update Query 1...");
			// evaluate first update query and print out the result!
			evaluateQueryAndPrintOut(evaluator, updateQuery1);
			
			System.out.println("Query 3...");
			// evaluate third query and print out the result!
			evaluateQueryAndPrintOut(evaluator, query3);
			
			System.out.println("Update Query 2...");
			// evaluate second update query and print out the result!
			evaluateQueryAndPrintOut(evaluator, updateQuery2);
			
			System.out.println("Query 3...");
			// evaluate third query and print out the result!
			evaluateQueryAndPrintOut(evaluator, query3);
			
			System.out.println("Update Query 3...");
			// evaluate third update query and print out the result!
			evaluateQueryAndPrintOut(evaluator, updateQuery3);
			
			System.out.println("Query 4...");
			// evaluate first query and print out the result!
			evaluateQueryAndPrintOut(evaluator, query4);
						
			// important!
			// The following command writes out all modified pages,
			// which are still in the buffer manager...
			// Without this command (or when the system crashes 
			// before or during this command),
			// the indices may remain in an inconsistent state
			// and must be build from scratch in these rare cases...
			CommonCoreQueryEvaluator.writeOutModifiedPages(evaluator);
			
			// the whole database is dumped into files...
			// (10000 triples in one file...)
			evaluator.dump("D://dump", 10000);
			
		} catch (final Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	private final static void evaluateQueryAndPrintOut(
			final QueryEvaluator<Node> evaluator, final String query)
			throws Exception {
		// evaluate query:
		final QueryResult qr = evaluator.getResult(query);

		// use oneTimeIterator() whenever the result need to
		// be iterated only once, otherwise iterator()...
		final Iterator<Bindings> it_query = qr.oneTimeIterator();
		while (it_query.hasNext()) {
			// get next solution of the query...
			final Bindings bindings = it_query.next();
			// print out the bound values of all bindings!
			StringBuilder result=new StringBuilder("{");
			boolean first=true;
			for (final Variable v : bindings.getVariableSet()) {
				if(first){
					first=false;
				} else {
					result.append(", ");
				}
				result.append(v);
				result.append('=');
				result.append(bindings.get(v));
			}
			result.append("}");
			System.out.println(result.toString());
		}
	}
}