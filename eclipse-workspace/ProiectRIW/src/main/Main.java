package main;


import java.util.Scanner;
import java.io.IOException;
import java.util.HashSet;
import htmlProcessing.DirectIndex;
import htmlProcessing.IndirectIndex;
import search.BooleanSearch;

public class Main {

	public static void main(String[] args) throws IOException {
		
		Scanner in = new Scanner(System.in);
		System.out.println("1 - Indexare directa\n2 - Indexare indirecta\n3 - Cautare Booleana");
		int num = in.nextInt();
		switch(num)
		{
			case 1:
				DirectIndex.directIndex("test");
				break;
			case 2:
				IndirectIndex.indirectIndex();
				break;
			case 3:
				HashSet<String> result = new HashSet<String>();
				String query = BooleanSearch.readString();
				result = BooleanSearch.booleanSearch(query);
				for(String s : result) {
					System.out.println(s);
				}
				System.out.println((result.size()==0)?"Nu sunt documente":result + "\ncount:" + result.size());
				break;
			default:
				System.out.println("Optiune invalida!");
				break;
		}
		
		in.close();
		
	}
}
