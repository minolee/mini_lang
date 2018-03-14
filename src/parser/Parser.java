package parser;

import automaton.Automaton;
import automaton.RegexElement;
import automaton.RegexOperation;
import automaton.RegexTree;
import error.ParseException;
import error.ScannerException;
import structure.Keyword;
import structure.Node;

import java.io.*;
import java.util.*;

/**
 * Created by pathm on 2017-08-28.
 * parse grammar
 */
public class Parser
{
	public final Map<Keyword, List<ParseElement>> grammar;
	private final List<Keyword> legalKeywords;
	private final Map<String, Keyword> keywordDict;
	private List<ParseState> states;
	private ParseState currentState;
	private Map<Keyword, Integer> tempCount = new HashMap<>();
	private int tempKeywordCount = 0;

	private Parser()
	{
		states = new ArrayList<>();
		legalKeywords = new ArrayList<>();
		keywordDict = new HashMap<>();
		grammar = new HashMap<>();
	}

	/**
	 * grammar file input을 받아서 grammar를 가진 parser를 만들어준다.
	 * @param grammarFile grammar file
	 * @return parser with grammar
	 * @throws IOException
	 * @throws ParseException
	 * @throws ScannerException
	 */
	public static Parser generateParser(File grammarFile) throws IOException, ParseException, ScannerException
	{
		Parser p = new Parser();
		p.generateKeywords(grammarFile);
		List<RegexTree<Keyword>> grammars = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(grammarFile));
		String line;
		while (( line = reader.readLine() ) != null)
		{
			if (line.startsWith("#") || line.length() == 0) continue;
			String[] x = line.split(" : ");
			String lhs = x[0];
			RegexTree<Keyword> rhs = Automaton.parseRegex(p.translateFunction(x[1]), null);
			Keyword key = p.keywordDict.get(lhs);
			List<List<Keyword>> simpleForm = p.translateToSimpleForm(rhs);
			List<ParseElement> elements = new ArrayList<>();
			for (List<Keyword> element : simpleForm)
			{
				elements.add(new ParseElement(key, true, element));
			}

			p.grammar.put(p.keywordDict.get(lhs), elements);
			p.mergeDuplicateGrammar();
		}

		return p;
	}

	///////////////////////////////
	//Parser generating functions//
	///////////////////////////////

	private void generateKeywords(File grammarFile) throws IOException, ParseException
	{

		BufferedReader reader = new BufferedReader(new FileReader(grammarFile));
		String line;
		Map<String, Keyword> nonTerminals = new HashMap<>();
		while (( line = reader.readLine() ) != null)
		{
			if (line.startsWith("#")) continue;
			String key = null;
			StringBuilder keyword = new StringBuilder();
			StringBuilder grammarData = new StringBuilder();
			char[] x = line.toCharArray();
			int state = 0;

			for (int i = 0; i < x.length; i++)
			{
				switch (state)
				{
					case 0:
						if (x[i] == ' ')
						{
							Keyword nt = new Keyword(keyword.toString(), false);
							keywordDict.putIfAbsent(keyword.toString(), nt);
							key = keyword.toString();
							if (nonTerminals.containsKey(keyword.toString()))
							{
								//warning-재정의
							}
							nonTerminals.put(keyword.toString(), nt);

							keyword = new StringBuilder();
							state++;
							break;
						}
						keyword.append(x[i]);
						break;
					case 1:
						if (x[i] == ':')
						{
							state++;
							break;
						}
						throw new ParseException(ParseException.ExceptionType.ILLEGAL_GRAMMAR);
					case 2:
						if (x[i] == ' ')
						{
							state++;
							break;
						}
						throw new ParseException(ParseException.ExceptionType.ILLEGAL_GRAMMAR);
					case 3:
						grammarData.append(x[i]);
						switch (x[i])
						{
							case '[':
								keyword = new StringBuilder();
								do
								{
									i++;
									keyword.append(x[i]);
								}
								while (x[i + 1] != ']');
								i++;
								grammarData.append(keyword);
								grammarData.append(']');
								keywordDict.putIfAbsent(keyword.toString(), new Keyword(keyword.toString(), false));
								break;
							case '<':
								keyword = new StringBuilder();
								do
								{
									i++;
									keyword.append(x[i]);
								}
								while (x[i + 1] != '>');
								i++;
								grammarData.append(keyword);
								grammarData.append('>');
								keywordDict.putIfAbsent(keyword.toString(), new Keyword(keyword.toString(), true));

								break;
							default:
								//()*?|
								break;
						}
						grammarData.append(x[i]);
						break;
				}
			}
		}
		keywordDict.forEach((s, k) -> tempCount.put(k, 0));
		reader.close();
	}

	private List<RegexElement<Keyword>> translateFunction(String line)
	{
		char[] x = line.toCharArray();
		StringBuilder buf = new StringBuilder();
		List<RegexElement<Keyword>> result = new ArrayList<>();
		for (int i = 0; i < x.length; i++)
		{
			switch (x[i])
			{
				case '[':
					buf = new StringBuilder();
					do
					{
						i++;
						buf.append(x[i]);
					}
					while (x[i + 1] != ']');
					i++;
					result.add(new RegexElement<>(keywordDict.get(buf.toString())));
					break;
				case '<':
					buf = new StringBuilder();
					do
					{
						i++;
						buf.append(x[i]);
					}
					while (x[i + 1] != '>');
					i++;
					result.add(new RegexElement<>(keywordDict.get(buf.toString())));
					break;
				default:
					result.add(new RegexElement<>(x[i], true));
					break;
			}
		}
		return result;
	}

	/**
	 * keyword에 있는 *, ?, () 등을 없애서 좀 더 간단한 form으로 만든다.
	 * a -> b | c | AB | ...
	 *
	 * @param keywords
	 * @return
	 */
	private List<List<Keyword>> translateToSimpleForm(RegexTree<Keyword> keywords) throws ParseException
	{
		List<List<Keyword>> elements = new ArrayList<>(); //result
		List<List<Keyword>> left, right;
		List<Keyword> temp = new ArrayList<>();
		List<ParseElement> elems;
		int reusecount;
		String surface;
		switch (keywords.op)
		{
			case NONE:
				temp.add(keywordDict.get(( keywords.data.getKeyword() )));
				elements.add(temp);
				break;
			case OR:
				elements.addAll(translateToSimpleForm(keywords.left));
				elements.addAll(translateToSimpleForm(keywords.right));
				break;
			case REPEAT:
				/*  A -> C* 를 다음과 같이 변환
				 *  A -> C'
				 *  C'-> CC' | e
				 */
				if (keywords.left.op != RegexOperation.NONE)
					throw new ParseException(ParseException.ExceptionType.ILLEGAL_GRAMMAR);

				surface = keywords.left.data.getKeyword();
				reusecount = tempCount.get(keywordDict.get(surface));
				String s = String.format("%s:%d", surface, reusecount);// C'
				tempCount.put(keywordDict.get(surface), reusecount + 1);

				Keyword k = new Keyword(s, false);
				keywordDict.put(s, k);
				legalKeywords.add(k);

				left = translateToSimpleForm(keywords.left); // C
				for (List<Keyword> rhs : left)
				{
					rhs.add(k);
				}
				left.add(new ArrayList<>());//epsilon
				elems = new ArrayList<>();
				for (List<Keyword> rhs : left)
				{
					elems.add(new ParseElement(k, false, rhs));
				}
				grammar.put(k, elems);
				temp.add(k);
				elements.add(temp);
				break;
			case CONCAT:
				for (List<Keyword> l : translateToSimpleForm(keywords.left))
				{
					for (List<Keyword> r : translateToSimpleForm(keywords.right))
					{
						List<Keyword> x = new ArrayList<>(l);
						x.addAll(r);
						elements.add(x);
					}
				}
				break;
			case ONCE:
				/* A -> B? 를 다음과 같이 변경
				 * A -> B'
				 * B'-> B | e
				 */
				if (keywords.left.op != RegexOperation.NONE)
					throw new ParseException(ParseException.ExceptionType.ILLEGAL_GRAMMAR);
				surface = keywords.left.data.getKeyword();
				reusecount = tempCount.get(keywordDict.get(surface));

				s = String.format("%s:%d", surface, reusecount);// C'
				tempCount.put(keywordDict.get(surface), reusecount + 1);
				k = new Keyword(s, false);
				keywordDict.put(s, k);

				legalKeywords.add(k);
				left = translateToSimpleForm(keywords.left); // C
				left.add(new ArrayList<>());//epsilon
				elems = new ArrayList<>();
				for (List<Keyword> rhs : left)
				{
					elems.add(new ParseElement(k, false, rhs));
				}
				grammar.put(k, elems);
				temp.add(k);
				elements.add(temp);
				break;
			case DUMMY:
				elements = translateToSimpleForm(keywords.left);
				break;
			default:
				throw new ParseException(ParseException.ExceptionType.ILLEGAL_GRAMMAR);
		}
		return elements;
	}

	private void mergeDuplicateGrammar()
	{
		Map<Keyword, List<ParseElement>> temp = new HashMap<>();
		Map<Keyword, Keyword> redirection = new HashMap<>();
		Keyword redirect = null;
		for (Keyword k1 : grammar.keySet())
		{
			boolean hasDuplicate = false;
			for (Keyword k2 : temp.keySet())
			{
				if (grammar.get(k1).size() != temp.get(k2).size()) continue;
				if (temp.get(k2).containsAll(grammar.get(k1)))
				{
					hasDuplicate = true;
					redirect = k2;
				}
			}
			if (!hasDuplicate)
			{
				temp.put(k1, grammar.get(k1));
				continue;
			}
			redirection.put(k1, redirect);
			System.out.println(String.format("%s is same with %s", k1, redirect));
		}
		temp.forEach((k, l) -> l.forEach(p ->p.rhs.replaceAll(k1 -> redirection.getOrDefault(k1, k1))));
		grammar.clear();
		grammar.putAll(temp);
	}


	//end parser generating functions

	/////////////////////////////
	//Parsing related functions//
	/////////////////////////////

	private void generateItems() throws ParseException
	{
		List<Item> items = new ArrayList<>();
		Map<Keyword, List<Keyword>> lookaheads = new HashMap<>(legalKeywords.size());
		for (Keyword keyword : legalKeywords)
			lookaheads.put(keyword, new ArrayList<>());
		lookaheads.get(keywordDict.get("PROGRAM")).add(Keyword.EOF);
	}

	private void feed(Keyword k)
	{

	}

	private void shift()
	{

	}

	private void reduce()
	{

	}

	/**
	 * 프로그램을 parse하여 하나의 node tree로 만든다.
	 *
	 * @param file
	 * @return
	 */
	public Node parse(String file)
	{
		return null;
	}
}
