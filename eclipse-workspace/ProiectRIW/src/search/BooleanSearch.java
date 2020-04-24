package search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import htmlProcessing.ExceptionsAndStopwords;
import htmlProcessing.IndirectIndexObject;
import htmlProcessing.Parser;
import stemmer.Stemmer;


public class BooleanSearch {
	private static HashSet<IndirectIndexObject> indirectIndex;
	
	// incarca indexul indirect
	private static void loadIndirectIndexFromJsonFile()
	{
		Gson gson = new Gson();
		String json = Parser.readFromFile("indexIndirectMongoDB.json");
		indirectIndex = gson.fromJson(json, new TypeToken<HashSet<IndirectIndexObject>>() { //convert  json into hashSet
		}.getType());
	}
	
	// citeste inputul de la user
	public static String readString() {
		String input;
		Scanner scanner = new Scanner(System.in);
		System.out.print("Insert the string with words and operands (+, -, / ): ");
		input = scanner.nextLine();
		scanner.close();
		return input;
	}
	
	// desparte operatorii
	private static List<String> splitOperators(String input)
	{
		List<String> list = new ArrayList<String>();
		int len = input.length();
		char c;
		for (int i = 0; i < len; i++) {
			c = input.charAt(i);
			if ((c == '+') || (c == '-') || (c == '*')) {
				list.add(c + "");
			}
		}
		return list;
	}
	
	// desparte operanzii
	private static List<String> splitOperands(String input) throws IOException {
		List<String> list = new ArrayList<String>();
		String[] words = input.split("[-|+|\\s]");
		ExceptionsAndStopwords es = new ExceptionsAndStopwords();
		
		for (String string : words) {
			if (es.isException(string)) {
				string = string.toLowerCase();
				list.add(string);
			} 
			else
			{
				string = string.toLowerCase();
				if (ExceptionsAndStopwords.isStopword(string)) 
				{
					continue;
				} 
				else
				{
					Stemmer s = new Stemmer();
					char[] chs = string.toCharArray();
					s.add(chs, chs.length);
					s.stem();
					string = s.toString();
					list.add(string);
				}
			}
		}
		return list;
	}
	
	// functie pentru AND ---> returneaza documentele care contin ambele cuvinte de input
	private static HashSet<String> resolveIntersection(HashSet<String> string1, HashSet<String> string2)
	{
		HashSet<String> result = new HashSet<String>();
		int len1 = string1.size();
		int len2 = string2.size();
		if(!string2.isEmpty() && !string1.isEmpty())
		{
			if(len1 < len2)
			{
				for(String string : string1)
				{
					if(string2.contains(string)) {
						result.add(string);
					}
				}
			}
			else
			{
				for(String string : string2)
				{
					if(string1.contains(string)) {
						result.add(string);
					}
				}
			}
		}
		else
		{
			System.out.println("Acesti termeni nu exista!");
			System.exit(-1);
		}
		return result;
	}
	
	// functie pentru OR ---> returneaza documentele care contin cel putin unul dintre cuvintele de input
	private static HashSet<String> resolveReunion(HashSet<String> string1, HashSet<String> string2)
	{
		HashSet<String> result = new HashSet<String>();
		int len1 = string1.size();
		int len2 = string2.size();
		if(!string2.isEmpty() && !string1.isEmpty())
		{
			if(len1 < len2)
			{
				result.addAll(string2);
				for(String string : string1)
				{
					result.add(string);
				}
			}
			else
			{
				result.addAll(string1);
				for(String string : string2)
				{
					result.add(string);
				}
			}
		}
		else
		{
			System.out.println("Acesti termeni nu exista!");
			System.exit(-1);
		}
		return result;
	}
	
	// functie pentru NOT ---> returneaza documentele care contin primul cuvant dar nu pe al doilea
	private static HashSet<String> resolveNegate(HashSet<String> string1, HashSet<String> string2)
	{
		HashSet<String> result = new HashSet<String>();
		if(!string2.isEmpty() && !string1.isEmpty())
		{
			for(String string : string1)
			{
				if(!string2.contains(string))
				{
					result.add(string);
				}
			}
		}
		else
		{
			System.out.println("Acesti termeni nu exista!");
			System.exit(-1);
		}
		return result;
	}

	
	private static HashSet<String> getDocumentsForWord(String word)
	{
		for(IndirectIndexObject wordsFormat : indirectIndex)
		{
			if(wordsFormat.getWord().equals(word)) {
				return wordsFormat.getOnlyDocuments();
			}
		}
		return null;
	}
	
	 private static HashSet<String> getWordsIndirectIndex()
	{
		HashSet<String> words = new HashSet<String>();
		Iterator<IndirectIndexObject> iter = indirectIndex.iterator();
		while (iter.hasNext()) {
		    words.add(iter.next().getWord());
		}
		return words;
	}
	
	public static HashSet<String> booleanSearch(String input) throws IOException
	{
		HashSet<String> rezultat = new HashSet<String>();
		loadIndirectIndexFromJsonFile();
		HashSet<String> words = getWordsIndirectIndex();
		
		List<String> operators = splitOperators(input);
		List<String> operands = splitOperands(input);
		
		List<String> operators2 = new ArrayList<String>();
		List<String> operands2 = new ArrayList<String>();
		
		if(words.contains(operands.get(0)))
		{
			operands2.add(operands.get(0));
		}
		for(int i = 0; i < operators.size(); i++)
		{
			
			if(words.contains(operands.get(i+1)))
			{
				if(!operands2.isEmpty())
				{
					operators2.add(operators.get(i));
				}
				operands2.add(operands.get(i+1));
			} 
		}
		if(operands2.isEmpty() || (operands2.size() == 1))
		{
			System.out.println("Nu exista asemenea termeni!");
			System.exit(-1);
		}
		rezultat = getDocumentsForWord(operands2.get(0));
		for(int i = 0; i < operators2.size(); i++)
		{
			switch (operators2.get(i)) {
			case "+":
				rezultat=resolveIntersection(rezultat, getDocumentsForWord(operands2.get(i+1)));
				break;
			case "-":
				rezultat=resolveNegate(rezultat, getDocumentsForWord(operands2.get(i+1)));
				break;
			case "*":
				rezultat=resolveReunion(rezultat, getDocumentsForWord(operands2.get(i+1)));
				break;
			}
		}
		return rezultat;
	}
}