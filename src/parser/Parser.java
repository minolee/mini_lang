package parser;

import automaton.Automaton;
import automaton.RegexElement;
import automaton.RegexOperation;
import automaton.RegexTree;
import error.ParseException;
import error.ScannerException;
import lombok.Getter;
import structure.Keyword;
import structure.Node;

import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Created by pathm on 2017-08-28.
 * parse grammar
 */
public class Parser
{
	private final Map<Keyword, List<ProductionRule>> grammar;
	private final List<Keyword> legalKeywords;
	private final List<Item> items;
	private final Map<String, Keyword> keywordDict;
	private List<ParseState> states;
	@Getter
	private final List<Partition> partitions;
	private ParseState currentState;
	private Map<Keyword, Integer> tempCount = new HashMap<>();

	private Parser()
	{
		states = new ArrayList<>();
		items = new ArrayList<>();
		legalKeywords = new ArrayList<>();
		keywordDict = new HashMap<>();
		grammar = new HashMap<>();
		partitions = new ArrayList<>();
	}

	/**
	 * grammar file input을 받아서 grammar를 가진 parser를 만들어준다.
	 *
	 * @param grammarFile grammar file
	 * @return parser with grammar
	 * @throws IOException
	 * @throws ParseException
	 * @throws ScannerException
	 */
	public static Parser GenerateParser(File grammarFile) throws IOException, ParseException, ScannerException
	{
		Parser p = new Parser();
		p.generateKeywords(grammarFile);
		List<RegexTree<Keyword>> grammars = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(grammarFile));
		String line;
		while ((line = reader.readLine()) != null)
		{
			if (line.startsWith("#") || line.length() == 0) continue;
			String[] x = line.split(" : ");
			String lhs = x[0];
			RegexTree<Keyword> rhs = Automaton.parseRegex(p.translateFunction(x[1]), null);
			Keyword key = p.keywordDict.get(lhs);
			List<List<Keyword>> simpleForm = p.translateToSimpleForm(rhs);
			List<ProductionRule> elements = new ArrayList<>();
			for (List<Keyword> element : simpleForm)
			{
				elements.add(new ProductionRule(key, true, element));
			}

			p.grammar.put(p.keywordDict.get(lhs), elements);
			p.mergeDuplicateGrammar();
		}

		p.generateItems();
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
		while ((line = reader.readLine()) != null)
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
		List<ProductionRule> elems;
		int reusecount;
		String surface;
		switch (keywords.op)
		{
			case NONE:
				temp.add(keywordDict.get((keywords.data.getKeyword())));
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
					elems.add(new ProductionRule(k, false, rhs));
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
					elems.add(new ProductionRule(k, false, rhs));
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
		Map<Keyword, List<ProductionRule>> temp = new HashMap<>();
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
		}
		temp.forEach((k, l) -> l.forEach(p -> p.rhs.replaceAll(k1 -> redirection.getOrDefault(k1, k1))));
		grammar.clear();
		grammar.putAll(temp);
	}

	private void generateParseTable()
	{
		Item head = items.get(0);
		boolean end = false;
	}

	/**
	 * item list를 만든다. grammar가 확정된 이후 items에 가능한 모든 item을 저장한다.
	 * 사실 필요한건지 잘 모르겠다... 그냥 item set에서 순차적으로 만드는게 낫지 않을까?
	 *
	 */
	private void generateItems() throws ParseException
	{
		List<Partition> queue = new ArrayList<>();
		Partition initial = generatePartition(Arrays.asList(new Item(grammar.get(keywordDict.get("PROGRAM")).get(0), Keyword.EOF)));
		queue.add(initial);
		while(queue.size() > 0)
		{
			Partition currentSet = queue.remove(0);
			partitions.add(currentSet);
			Set<Keyword> next = new HashSet<>();
			//transition 후보들을 얻어옴
			for(Item item : currentSet.getItems())
			{
				Keyword k = item.getNext();
				if (k != null) next.add(k);
			}

			//각각의 후보에 대해 transition된 item을 만들고, 그 item을 새로운 partition으로 만듬

			for(Keyword transitionWord : next)
			{
				//일단 해당 keyword로 transition될 모든 item을 모은다
				Set<Item> generateSet = new HashSet<>();
				for (Item item : currentSet.getItems())
				{
					if (item.getNext() != null && item.getNext().equals(transitionWord))
					{
						generateSet.add(item.nextItem());
					}
				}

				if(generateSet.size() > 0)
				{

					Partition newPartition = generatePartition(generateSet);
					for(Item item : newPartition.getItems())
					{
						if(currentSet.getItems().contains(item))
						{
							throw new ParseException(ParseException.ExceptionType.AMBIGUOUS_GRAMMAR, item.toString());
						}
					}
					currentSet.getShift().put(transitionWord, newPartition);
					queue.add(newPartition);
				}
			}
		}
	}

	private Partition generatePartition(Collection<Item> item)
	{
		Set<Item> result = new HashSet<>();
		List<Item> queue = new ArrayList<>(item);
		Item current;
		while (queue.size() > 0)
		{
			current = queue.remove(0);
			result.add(current);
			Keyword next = current.getNext();
			if(next != null && !next.isTerminal())
			{
				for (ProductionRule rule : grammar.get(next))
				{
					Item i = new Item(rule, current.lookahead);
					queue.add(i);
				}
			}
//			for (ProductionRule rule : grammar.get(current.name))
//			{
//				//get right after keyword, and if it is non-terminal, add new item to partition
//				if(rule.rhs.size() > 0 && !rule.rhs.get(current.position).isTerminal())
//				{
//					Item i = new Item(rule, 0);
//					queue.add(i);
//				}
//			}
		}
		return new Partition(result);
	}


	//end parser generating functions

	/////////////////////////////
	//Parsing related functions//
	/////////////////////////////

	/**
	 * 프로그램을 parse하여 하나의 node tree로 만든다.
	 *
	 * @param file source code
	 * @return root node
	 */
	public Node parse(String file, scanner.Scanner scanner)
	{
		return null;
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
}
