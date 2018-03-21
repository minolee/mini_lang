package parser;

import automaton.Automaton;
import automaton.RegexElement;
import automaton.RegexOperation;
import automaton.RegexTree;
import exception.ParseException;
import exception.ScannerException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import structure.Keyword;

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
	private final Map<Keyword, Set<Keyword>> firstSet;
	private final Map<Keyword, Set<Keyword>> followSet;
	private final Map<String, Keyword> keywordDict;
	@Getter
	private final Set<Closure> closures;
	private Closure root;
	private Map<Keyword, Integer> tempCount = new HashMap<>();

	private Parser()
	{
		keywordDict = new HashMap<>();
		grammar = new HashMap<>();
		firstSet = new HashMap<>();
		followSet = new HashMap<>();
		closures = new HashSet<>();
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
		while (( line = reader.readLine() ) != null)
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

		}
		//cleanup
		p.tempCount = null;
		reader.close();

		//after parsing grammar
		p.mergeDuplicateGrammar();
		p.generateFirstSet();
		p.generateFollowSet();
		p.generateDFA();
		p.generateParseTable();

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
		keywordDict.put("<EOF>", Keyword.EOF);
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
		StringBuilder buf;
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
		List<List<Keyword>> left;
		List<Keyword> temp = new ArrayList<>();
		List<ProductionRule> elems;
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
				 *  A -> C' | e
				 *  C'-> CC' | C
				 */
				if (keywords.left.op != RegexOperation.NONE)
					throw new ParseException(ParseException.ExceptionType.ILLEGAL_GRAMMAR);

				surface = keywords.left.data.getKeyword();
				reusecount = tempCount.get(keywordDict.get(surface));
				String s = String.format("%s:%d", surface, reusecount);
				tempCount.put(keywordDict.get(surface), reusecount + 1);

				Keyword k = new Keyword(s, false, false);// C'
				keywordDict.put(s, k);

				left = translateToSimpleForm(keywords.left); // C
				for (List<Keyword> rhs : left)
				{
					rhs.add(k);// CC'
				}
				elems = new ArrayList<>();
				translateToSimpleForm(keywords.left).forEach(k1 -> elems.add(new ProductionRule(k, false, k1)));
				for (List<Keyword> rhs : left)
				{
					elems.add(new ProductionRule(k, false, rhs));
				}
				grammar.put(k, elems);
				temp.add(k);
				elements.add(new ArrayList<>());
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
				/* A -> AB?C 를 다음과 같이 변경
				 * A -> ABC | AC
				 */
				if (keywords.left.op != RegexOperation.NONE && keywords.left.op != RegexOperation.DUMMY)
					throw new ParseException(ParseException.ExceptionType.ILLEGAL_GRAMMAR);

				left = translateToSimpleForm(keywords.left); // B
				elems = new ArrayList<>();
				for (List<Keyword> l : left)
				{
					elems.add(new ProductionRule(keywords.left.data, true, l));
				}
				elements.add(new ArrayList<>());
				temp.add(keywords.left.data);
				elements.add(temp);
				break;
			case DUMMY: //()
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

	private void generateFirstSet()
	{
		keywordDict.forEach((s, k) ->
		{
			if (k.isTerminal()) firstSet.put(k, Collections.singleton(k));
		});
		grammar.keySet().forEach(k -> firstSet.put(k, getFirst(k)));
	}

	private Set<Keyword> getFirst(Keyword k)
	{
		Set<Keyword> result = new HashSet<>();
		FirstSetParseTree parent = null;
		List<FirstSetParseTree> queue = new ArrayList<>();
		grammar.get(k).forEach(p -> queue.add(new FirstSetParseTree(p, null, 0)));
		while (queue.size() > 0)
		{
			FirstSetParseTree current = queue.remove(0);
			Keyword first = current.getNext();
			if (first == null)
			{
				if (current.parent == null)
				{
					result.add(Keyword.EPSILON);
					continue;
				}
				if (current.parent.parent == null) continue;
				queue.add(0, new FirstSetParseTree(current.parent.rule, current.parent.parent, current.parent.startIndex + 1));
				continue;
			}
			if (first.isTerminal()) result.add(first);
			else
			{
				for (ProductionRule p : grammar.get(first))
				{
					queue.add(new FirstSetParseTree(p, current, 0));
				}
			}
		}
		return result;
	}

	private void generateFollowSet()
	{
		keywordDict.forEach((s, k) ->
		{
			if (!k.isTerminal())
				followSet.put(k, new HashSet<>());
		});
		followSet.get(keywordDict.get("PROGRAM")).add(Keyword.EOF);
		boolean changed;
		do
		{
			changed = false;
			for (Keyword k : keywordDict.values())
			{
				if (k.isTerminal()) continue;
				for (ProductionRule rule : grammar.get(k))
				{
					for (int i = 0; i < rule.rhs.size(); i++)
					{
						Keyword current = rule.rhs.get(i);
						Keyword next = i + 1 < rule.rhs.size() ? rule.rhs.get(i + 1) : null;
						if (current.isTerminal()) continue;
						if (next != null)
						{
							for (Keyword first : firstSet.get(next))
							{
								changed |= followSet.get(current).add(first);
							}
						}
						else
						{
							for (Keyword follow : followSet.get(rule.getGeneratingKeyword()))
							{
								changed |= followSet.get(current).add(follow);
							}
						}
					}
				}
			}
		}
		while (changed);

	}

	/**
	 * item list를 만든다. grammar가 확정된 이후 items에 가능한 모든 item을 저장한다.
	 * 사실 필요한건지 잘 모르겠다... 그냥 item set에서 순차적으로 만드는게 낫지 않을까?
	 */
	@Deprecated
	private void generateTable()
	{
		List<Closure> queue = new ArrayList<>();
		Set<Item> initialItems = new HashSet<>();
		grammar.get(keywordDict.get("PROGRAM")).forEach(r -> initialItems.add(new Item(r, Keyword.EOF)));


		queue.add(generateClosure(initialItems));
		boolean init = false;
		while (queue.size() > 0)
		{
			Closure currentSet = queue.remove(0);
			if (!init)
			{
				root = currentSet;
				init = true;
			}

			if (closures.contains(currentSet)) continue;
			closures.add(currentSet);
			Set<Keyword> next = new HashSet<>();
			//transition 후보들을 얻어옴
			for (Item item : currentSet.getItems())
			{
				Keyword k = item.getNext();
				if (k != null) next.add(k);
			}

			//각각의 후보에 대해 transition된 item을 만들고, 그 item을 새로운 partition으로 만듬

			for (Keyword transitionWord : next)
			{
				//일단 해당 keyword로 transition될 모든 item을 모은다
				Set<Item> generateSet = new HashSet<>();
				for (Item item : currentSet.getItems())
				{
					if (item.getNext() != null && item.getNext().equals(transitionWord))
					{
						try
						{
							generateSet.add(item.nextItem());
						}
						catch (Exception e)
						{
							e.printStackTrace();
							System.exit(1);
						}
					}
				}

				if (generateSet.size() > 0)
				{

					Closure newClosure = generateClosure(generateSet);
					currentSet.getShift().put(transitionWord, newClosure);
					if (queue.contains(newClosure) || closures.contains(newClosure)) continue;
					queue.add(newClosure);
				}
			}
		}
	}

	private Closure generateClosure(Collection<Item> item)
	{
		Set<Item> result = new HashSet<>(item);
		Set<Item> temp = new HashSet<>();
		boolean changed;
		do
		{
			changed = false;
			temp.clear();
			for (Item i : result)
			{
				Keyword next = i.getNext();
				if (next == null || next.isTerminal()) continue;
				for (ProductionRule rule : grammar.get(next))
				{
					for (Keyword lookahead : firstSet.get(i.getAfter()))
					{
						temp.add(new Item(rule, lookahead));
					}
				}
			}
			for (Item i : temp)
			{
				changed |= result.add(i);
			}
		}
		while (changed);
		Closure c = new Closure(result);
		for (Closure cc : closures)
		{
			if (cc.equals(c))
				return cc;
		}
		return c;
	}

	private Closure gotoClosure(Closure c, @NotNull Keyword k)
	{
		if (c.getShift().containsKey(k)) return c.getShift().get(k);
		Set<Item> temp = new HashSet<>();
		for (Item i : c.getItems())
		{
			if (i.getNext() == k)
			{
				try
				{
					temp.add(i.nextItem());
				}
				catch (Exception e)
				{
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
		Closure newClosure = generateClosure(temp);
		c.getShift().put(k, newClosure);
		return newClosure;
	}

	private void generateDFA()
	{
		Set<Item> initialItems = new HashSet<>();
		grammar.get(keywordDict.get("PROGRAM")).forEach(r -> initialItems.add(new Item(r, Keyword.EOF)));
		root = generateClosure(initialItems);
		closures.add(root);
		boolean changed;
		do
		{
			Set<Closure> copy = new HashSet<>(closures);
			changed = false;
			for (Closure c : copy)
			{
				for (Item i : c.getItems())
				{
					if(i.getNext() != null)
						changed |= closures.add(gotoClosure(c, i.getNext()));
				}
			}
		}
		while (changed);
	}

	private void generateParseTable()
	{

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
	public void parse(File file, scanner.Scanner scanner) throws IOException, ScannerException
	{
		List<Keyword> keywordSequence = scanner.scan(file);
		keywordSequence.add(Keyword.EOF);
		ParseState initial = new ParseState(root);
		Stack<ParseState> context = new Stack<>();
		context.push(initial);
		keywordSequence.forEach(k ->ParseState.feed(context, k));
	}

	//first set을 만들기 위한 일회용 class
	//지금은 first set을 안 만들기 때문에 필요없음
	private class FirstSetParseTree
	{
		ProductionRule rule;
		FirstSetParseTree parent;
		List<FirstSetParseTree> children;
		int startIndex;

		private FirstSetParseTree(ProductionRule rule, FirstSetParseTree parent, int startIndex)
		{
			this.rule = rule;
			this.parent = parent;
			this.children = new ArrayList<>();
			this.startIndex = startIndex;
			if (parent != null) parent.children.add(this);
		}

		private Keyword getNext()
		{
			return rule.rhs.size() > startIndex ? rule.rhs.get(startIndex) : null;
		}
	}


}
