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
package lupos.gui.anotherSyntaxHighlighting.javacc;

import java.io.InputStream;
import java.io.Reader;

import lupos.gui.anotherSyntaxHighlighting.ILuposParser;
import lupos.gui.anotherSyntaxHighlighting.LANGUAGE.TYPE__SemanticWeb;
import lupos.gui.anotherSyntaxHighlighting.LuposDocumentReader;
import lupos.gui.anotherSyntaxHighlighting.javacc.JAVACCParser.TOKEN;
import lupos.sparql1_1.SPARQL1_1Parser;
import lupos.sparql1_1.SPARQL1_1ParserConstants;
import lupos.sparql1_1.Token;

public class SPARQLParser extends SemanticWebParser {

	private static TYPE__SemanticWeb[] TOKEN_MAP;
	private final SPARQL1_1Parser parser;

	private SPARQLParser(final LuposDocumentReader reader){
		this.parser = new SPARQL1_1Parser(reader);
	}

	public static ILuposParser createILuposParser(final LuposDocumentReader reader){
		return new JAVACCParser(reader, new SPARQLParser(reader));
	}

	@Override
	public TOKEN getNextToken() {
		final Token token = this.parser.getNextToken();
		if(token==null){
			return null;
		} else {
			return new SPARQLToken(token);
		}
	}

	@Override
	public TYPE__SemanticWeb[] getTokenMap() {
		return SPARQLParser.TOKEN_MAP;
	}

	public static TYPE__SemanticWeb[] getStaticTokenMap() {
		return SPARQLParser.TOKEN_MAP;
	}

	@Override
	public void ReInit(final Reader reader) {
		this.parser.ReInit(reader);
	}

	@Override
	public void ReInit(final InputStream inputstream) {
		this.parser.ReInit(inputstream);
	}

	@Override
	public boolean isStartOfComment(final String content, final int beginChar){
		return content.charAt(beginChar)=='#';
	}

	@Override
	public SemanticWebToken handleComment(final String content, final int beginChar){
		int endOfComment = beginChar+1;
		while(endOfComment<content.length() && content.charAt(endOfComment)!='\n'){
			endOfComment++;
		}
		return new SemanticWebToken(TYPE__SemanticWeb.COMMENT, content.substring(beginChar, endOfComment), beginChar);
	}

	@Override
	public boolean endOfSearchOfComment(final String content, final int beginChar){
		return content.charAt(beginChar)=='\n';
	}

	static {
		SPARQLParser.TOKEN_MAP = new TYPE__SemanticWeb[SPARQL1_1ParserConstants.tokenImage.length];

		final String[] reservedWords = {
		    "\"FILTER\"",
		    "\"a\"",
		    "\"BASE\"",
		    "\"PREFIX\"",
		    "\"SELECT\"",
		    "\"DISTINCT\"",
		    "\"REDUCED\"",
		    "\"AS\"",
		    "\"CONSTRUCT\"",
		    "\"WHERE\"",
		    "\"DESCRIBE\"",
		    "\"ASK\"",
		    "\"FROM\"",
		    "\"NAMED\"",
		    "\"GROUP\"",
		    "\"BY\"",
		    "\"HAVING\"",
		    "\"ORDER\"",
		    "\"ASC\"",
		    "\"DESC\"",
		    "\"LIMIT\"",
		    "\"OFFSET\"",
		    "\"VALUES\"",
		    "\"UNDEF\"",
		    "\"INSERT\"",
		    "\"DATA\"",
		    "\"DELETE\"",
		    "\"LOAD\"",
		    "\"SILENT\"",
		    "\"INTO\"",
		    "\"CLEAR\"",
		    "\"DROP\"",
		    "\"CREATE\"",
		    "\"ADD\"",
		    "\"TO\"",
		    "\"MOVE\"",
		    "\"COPY\"",
		    "\"WITH\"",
		    "\"USING\"",
		    "\"DEFAULT\"",
		    "\"GRAPH\"",
		    "\"ALL\"",
		    "\"OPTIONAL\"",
		    "\"SERVICE\"",
		    "\"BIND\"",
		    "\"MINUS\"",
		    "\"UNION\"",
		    "\"IN\"",
		    "\"NOT\"",
		    "\"STR\"",
		    "\"LANG\"",
		    "\"LANGMATCHES\"",
		    "\"DATATYPE\"",
		    "\"BOUND\"",
		    "\"IRI\"",
		    "\"URI\"",
		    "\"BNODE\"",
		    "\"RAND\"",
		    "\"ABS\"",
		    "\"CEIL\"",
		    "\"FLOOR\"",
		    "\"ROUND\"",
		    "\"CONCAT\"",
		    "\"STRLEN\"",
		    "\"UCASE\"",
		    "\"LCASE\"",
		    "\"ENCODE_FOR_URI\"",
		    "\"CONTAINS\"",
		    "\"STRSTARTS\"",
		    "\"STRENDS\"",
		    "\"STRBEFORE\"",
		    "\"STRAFTER\"",
		    "\"YEAR\"",
		    "\"MONTH\"",
		    "\"DAY\"",
		    "\"HOURS\"",
		    "\"MINUTES\"",
		    "\"SECONDS\"",
		    "\"TIMEZONE\"",
		    "\"TZ\"",
		    "\"NOW\"",
		    "\"UUID\"",
		    "\"STRUUID\"",
		    "\"MD5\"",
		    "\"SHA1\"",
		    "\"SHA256\"",
		    "\"SHA384\"",
		    "\"SHA512\"",
		    "\"COALESCE\"",
		    "\"IF\"",
		    "\"STRLANG\"",
		    "\"STRDT\"",
		    "\"sameTerm\"",
		    "\"isIRI\"",
		    "\"isURI\"",
		    "\"isBLANK\"",
		    "\"isLITERAL\"",
		    "\"isNUMERIC\"",
		    "\"REGEX\"",
		    "\"SUBSTR\"",
		    "\"REPLACE\"",
		    "\"EXISTS\"",
		    "\"COUNT\"",
		    "\"SUM\"",
		    "\"MIN\"",
		    "\"MAX\"",
		    "\"AVG\"",
		    "\"SAMPLE\"",
		    "\"GROUP_CONCAT\"",
		    "\"SEPARATOR\"",
		};

		insertIntoTokenMap(reservedWords, TYPE__SemanticWeb.RESERVEDWORD);

		final String[] operators = {
			    "\"*\"",
			    "\"|\"",
			    "\"/\"",
			    "\"^\"",
			    "\"?\"",
			    "\"+\"",
			    "\"!\"",
			    "\"||\"",
			    "\"&&\"",
			    "\"=\"",
			    "\"!=\"",
			    "\"<\"",
			    "\">\"",
			    "\"<=\"",
			    "\">=\"",
			    "\"-\"",
			    "\"^^\"",
				};

		insertIntoTokenMap(operators, TYPE__SemanticWeb.OPERATOR);

		insertIntoTokenMap(new String[]{"\"{\"", "\".\"", "\"}\"", "<NIL>", "\";\"", "\"(\"", "\")\"", "\",\"", "\"[\"", "\"]\"", "<ANON>"}, TYPE__SemanticWeb.SEPARATOR);

		insertIntoTokenMap(new String[]{"<BLANK_NODE_LABEL>"}, TYPE__SemanticWeb.BLANKNODE);

		insertIntoTokenMap(new String[]{ "<VARNAME>", "<VAR>", "<VAR1>", "<VAR2>"}, TYPE__SemanticWeb.VARIABLE);

		insertIntoTokenMap(new String[]{ "\"true\"", "\"false\""}, TYPE__SemanticWeb.BOOLEAN);

		insertIntoTokenMap(new String[]{ "<INTEGER>"}, TYPE__SemanticWeb.INTEGER);

		insertIntoTokenMap(new String[]{ "<DECIMAL>", "<DOUBLE>", "<EXPONENT>"}, TYPE__SemanticWeb.DECIMAL);

		insertIntoTokenMap(new String[]{ "<LANGTAG>"}, TYPE__SemanticWeb.LANGTAG);

		insertIntoTokenMap(new String[]{ "<PNAME_LN>"}, TYPE__SemanticWeb.QUALIFIEDURI);

		insertIntoTokenMap(new String[]{ "<PNAME_NS>", "<PN_CHARS_BASE>", "<PN_CHARS_U>", "<PN_CHARS>", "<PN_PREFIX>", "<PN_LOCAL>", "<PLX>", "<PERCENT>", "<HEX>", "<PN_LOCAL_ESC>"}, TYPE__SemanticWeb.PREFIXLABEL);

		insertIntoTokenMap(new String[]{ "<IRI_REF>"}, TYPE__SemanticWeb.URI);

		insertIntoTokenMap(new String[]{ "<STRING_LITERAL1>", "<STRING_LITERAL2>", "<STRING_LITERAL_LONG1>", "<STRING_LITERAL_LONG2>"}, TYPE__SemanticWeb.LITERAL);

		insertIntoTokenMap(new String[]{ "<EOF>", "\" \"", "\"\\t\"", "\"\\n\"", "\"\\r\"", "<WS>"}, TYPE__SemanticWeb.WHITESPACE);

		insertIntoTokenMap(new String[]{ "<token of kind 10>"}, TYPE__SemanticWeb.COMMENT);

		// TYPE_SemanticWeb.IDENTIFIER not used, reasons?
		checkTopicMap();
	}

	protected static void insertIntoTokenMap(final String[] imagesToSet, final TYPE__SemanticWeb type){
		JAVACCParser.insertIntoTokenMap(SPARQL1_1ParserConstants.tokenImage, SPARQLParser.TOKEN_MAP, imagesToSet, type);
	}

	protected static void checkTopicMap(){
		JAVACCParser.checkTopicMap(SPARQL1_1ParserConstants.tokenImage, SPARQLParser.TOKEN_MAP);
	}

	public static class SPARQLToken implements TOKEN {

		private final Token sparql1_1token;

		public SPARQLToken(final Token sparql1_1token){
			this.sparql1_1token = sparql1_1token;
		}

		@Override
		public int getKind() {
			return this.sparql1_1token.kind;
		}

		@Override
		public String getImage() {
			return this.sparql1_1token.image;
		}
	}
}
