package lupos.rdf.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import lupos.datastructures.items.Triple;
import lupos.datastructures.items.literal.AnonymousLiteral;
import lupos.datastructures.items.literal.LanguageTaggedLiteralOriginalLanguage;
import lupos.datastructures.items.literal.Literal;
import lupos.datastructures.items.literal.LiteralFactory;
import lupos.datastructures.items.literal.TypedLiteralOriginalContent;
import lupos.engine.operators.tripleoperator.TripleConsumer;

import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

public class SesameturtleParser {
	
	public static int parseRDFData(final InputStream in, final TripleConsumer tc,
			final String encoding) throws UnsupportedEncodingException {
		return SesameturtleParser.readTriplesUsingSesameParser(in, tc, SesameParserType.TURTLE, encoding);
	}
	
	public enum SesameParserType {
		NTRIPLES, TURTLE, RDFXML
	};
	
	public static class CounterRDFHandler implements RDFHandler {
		
		private final TripleConsumer tc;
		
		public CounterRDFHandler(TripleConsumer tc){
			this.tc=tc;
		}
		
		private int counter = 0;

		public int getTripleNumber() {
			return counter;
		}

		public void endRDF() throws RDFHandlerException {
		}

		public void handleComment(final String arg0)
				throws RDFHandlerException {
		}

		public void handleNamespace(final String arg0, final String arg1)
				throws RDFHandlerException {
		}

		public void handleStatement(final org.openrdf.model.Statement arg0)
				throws RDFHandlerException {
			tc.consume(transformSesameStatementToTriple(arg0));
		}

		public void startRDF() throws RDFHandlerException {
		}	
	}
	

	public static int readTriplesUsingSesameParser(
			final InputStream in, final TripleConsumer tc,
			final SesameParserType parserType, final String encoding) {
		final CounterRDFHandler handler = new CounterRDFHandler(tc);
		try {
			final InputStreamReader isr = new InputStreamReader(in, encoding);
			if (parserType == SesameParserType.NTRIPLES) {
				final org.openrdf.rio.ntriples.NTriplesParser parser = new org.openrdf.rio.ntriples.NTriplesParser();
				parser.setRDFHandler(handler);
				try {
					parser.parse(isr, "");
				} catch (final RDFParseException e) {
					System.err.println(e);
					e.printStackTrace();
				} catch (final RDFHandlerException e) {
					System.err.println(e);
					e.printStackTrace();
				} catch (final IOException e) {
					System.err.println(e);
					e.printStackTrace();
				}
			} else if (parserType == SesameParserType.RDFXML) {
				final org.openrdf.rio.rdfxml.RDFXMLParser parser = new org.openrdf.rio.rdfxml.RDFXMLParser();
				parser.setRDFHandler(handler);
				try {
					parser.parse(isr, "");
				} catch (final RDFParseException e) {
					System.err.println(e);
					e.printStackTrace();
				} catch (final RDFHandlerException e) {
					System.err.println(e);
					e.printStackTrace();
				} catch (final IOException e) {
					System.err.println(e);
					e.printStackTrace();
				}
			} else if (parserType == SesameParserType.TURTLE) {
				final org.openrdf.rio.turtle.TurtleParser parser = new org.openrdf.rio.turtle.TurtleParser();
				parser.setRDFHandler(handler);
				try {
					parser.parse(isr, "");
				} catch (final RDFParseException e) {
					System.err.println(e);
					e.printStackTrace();
				} catch (final RDFHandlerException e) {
					System.err.println(e);
					e.printStackTrace();
				} catch (final IOException e) {
					System.err.println(e);
					e.printStackTrace();
				}
			}
			return handler.getTripleNumber();
		} catch (final UnsupportedEncodingException e1) {
			System.err.println(e1);
			e1.printStackTrace();
			return 0;
		}
	}

	public static Triple transformSesameStatementToTriple(
			final org.openrdf.model.Statement arg0) {
		Literal subj = null, pred = null, obj = null;
		final org.openrdf.model.Resource subject = arg0.getSubject();
		if (subject instanceof URI) {
			try {
				subj = LiteralFactory.createURILiteral("<"
						+ ((URI) subject).getNamespace()
						+ ((URI) subject).getLocalName() + ">");
			} catch (final URISyntaxException e) {
				System.err.println(e);
				e.printStackTrace();
			}
		} else {
			subj = new AnonymousLiteral("_:b" + ((BNode) subject).getID());
		}
		try {
			pred = LiteralFactory.createURILiteral("<"
					+ arg0.getPredicate().getNamespace()
					+ arg0.getPredicate().getLocalName() + ">");
		} catch (final URISyntaxException e) {
			System.err.println(e);
			e.printStackTrace();
		}
		final Value object = arg0.getObject();
		if (object instanceof URI) {
			try {
				obj = LiteralFactory.createURILiteral("<"
						+ ((URI) object).getNamespace()
						+ ((URI) object).getLocalName() + ">");
			} catch (final URISyntaxException e) {
				System.err.println(e);
				e.printStackTrace();
			}
		} else if (object instanceof BNode) {
			obj = new AnonymousLiteral("_:b" + ((BNode) object).getID());
		} else if (object instanceof org.openrdf.model.Literal) {
			final org.openrdf.model.Literal lit = (org.openrdf.model.Literal) object;
			if (lit.getDatatype() != null) {
				try {
					obj = TypedLiteralOriginalContent.createTypedLiteral("\""
							+ lit.getLabel() + "\"", "<"
							+ lit.getDatatype().getNamespace()
							+ lit.getDatatype().getLocalName() + ">");
				} catch (final URISyntaxException e) {
					System.err.println(e);
					e.printStackTrace();
				}
			} else if (lit.getLanguage() != null) {
				obj = LanguageTaggedLiteralOriginalLanguage
						.createLanguageTaggedLiteral("\"" + lit.getLabel()
								+ "\"", lit.getLanguage());
			} else
				obj = LiteralFactory
						.createLiteral("\"" + lit.getLabel() + "\"");
		} else
			System.err.println("Unknown type of data: " + object.getClass());
		return new Triple(subj, pred, obj);
	}
}