package htmlProcessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class Parser {
	
	public static void parseInformations(Writer writer, File input) throws IOException {
			
		// Parsarea titlului documentului HTML
		Document document = Jsoup.parse(input, "UTF-8");
		if(ExceptionsAndStopwords.getTitle(document) != null) {
			String title = ExceptionsAndStopwords.getTitle(document);
			writer.write(title);
		}
		
		// Parsarea metadatelor documentului HTML
		List<Metadata> metadatas = ExceptionsAndStopwords.getMetadata(document);
		for (Metadata metadata : metadatas) {
			if(metadata.getName() != "robots") {
				writer.write(metadata.getContent());
			}
		}
		
		// Parsarea continutului documentului HTML
		String text = ExceptionsAndStopwords.getText(document);
		writer.write(text);
	}
	
	public void parseLinks(Writer writer, File input) throws IOException {
		
		// Parsarea linkurilor documentului HTML
		Document document;
		document = Jsoup.parse(input, null);
		HashSet<String> urls = ExceptionsAndStopwords.getLinks(document);
	    for (String element : urls) {
			writer.write(element + "\n");
		}
	}
	
	public static String readFromFile(String file) {
		
		// Functie de citire din fisier
		String doc = " ";
		BufferedReader buffReader = null;
		FileReader fileReader = null;
		try {
			String  line ;
			fileReader = new FileReader(new File(file));
			buffReader = new BufferedReader(fileReader);
			while ((line = buffReader.readLine()) != null) {
				doc += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (buffReader != null)
					buffReader.close();
				if (fileReader != null)
					fileReader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return doc;
	}
	
	public static HashMap<String, Integer> parseText(File file) throws IOException{
		
		// Parsarea cuvintelor si al numarului de aparitii din document
		ExceptionsAndStopwords es = new ExceptionsAndStopwords();
		HashMap <String,Integer> wordsWithTheirCounter = new HashMap<String,Integer>();
		String doc = readFromFile(file.getAbsolutePath());
		es.countTheWordsFromAText(doc, wordsWithTheirCounter);
		return wordsWithTheirCounter;
	}
	
	public static List<String> getFilesFromDirectory(File directory) {
		
		// Functie ce parcurge un director. Returneaza toate fisierele din acel director
		// Lista de path-uri catre fisierele din director
		List<String> paths = new ArrayList<String>();  
		
		// Stiva in care adaugam fiecare obiect gasit (fisier/director).
		Stack<File> stack = new Stack<File>();         
		stack.push(directory);
		while(!stack.isEmpty()) {
			File childObject = stack.pop();
			
			//Daca obiectul este director, adaugam la stiva toate obiectele parinte
			if (childObject.isDirectory()) {
				for(File f : childObject.listFiles()) {
					stack.push(f);
				}
			}
			
			//Daca este fisier, adaugam pathul fisierului la lista declarata mai sus
			else if (childObject.isFile()) {
				paths.add(childObject.getPath());
			}
		}
		return paths;
	}
}
