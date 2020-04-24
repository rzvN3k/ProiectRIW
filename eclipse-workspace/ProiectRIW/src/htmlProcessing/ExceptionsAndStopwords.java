package htmlProcessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import stemmer.Stemmer;

public class ExceptionsAndStopwords {
	
	private static File exceptionsFile;
	private static File stopwordsFile;
	
	private static HashSet<String> exceptions;
	private static HashSet<String> stopwords;
	
	public ExceptionsAndStopwords() throws IOException {
		
		exceptionsFile = new File("exceptions.txt");    // fisierul cu cuvintele catalogate ca fiind "exceptii"
		stopwordsFile = new File ("stopwords.txt");     // fisierul cu cuvintele catalogate ca fiind "stopwords"
		
		// Folosim HashSet pentru a adauga exceptiile si stopwordurile (set pentru ca intrarile sunt unice)
		exceptions = new HashSet<String>(); 
		stopwords = new HashSet<String>();
		
		// Pentru citire din fisier
		BufferedReader buffReader = null;  
		FileReader fileReader = null;
		
		String line;
		fileReader = new FileReader(exceptionsFile);
		buffReader = new BufferedReader(fileReader);
			
		// cat timp mai avem linii in fisier, adauga la exceptii
		while ((line = buffReader.readLine()) != null) {
			exceptions.add(line);
		}
			
		fileReader = new FileReader(stopwordsFile);
		buffReader = new BufferedReader(fileReader);
			
		// cat timp mai avem lini in fisier, adauga la stopwords
		while ((line = buffReader.readLine()) != null) {
			stopwords.add(line);
		}
		
		buffReader.close();
	}
	
	// functie pentru preluarea titlului documentului HTML
	public static String getTitle(Document document){
		
		// functii specifice librariei JSOUP
		if(!document.title().isEmpty()) {
			return document.title() + "\n";
		}
		else {
			return null;
		}
	}
	
	// functie pentru preluarea continutului atributului content al tag-ului <META>, unde atributul name = keywords || description || robots
	public static List<Metadata> getMetadata(Document document) {
		
		List<Metadata> listOfMetadatas = new ArrayList<Metadata>(); // lista de metadata
		Elements metaTags = document.select("meta");   // retinem elementele HTML care au tag-ul "meta"
		
		// pentru fiecare element ce contine tag-ul "meta"
		for (Element metatag : metaTags) 
		{
			// daca au atributul "name"
			if(metatag.hasAttr("name")) {
				
				// aducem totul la lowercase pentru a verifica daca atributul "name" corespunde cu ce ne intereseaza
				String name = metatag.attr("name").toLowerCase();
				
				// daca atributul "name" este keywords || description || robots
				if(name.equals("keywords") || name.equals("description") || name.equals("robots")){
					
					//adauga la lista de metadata declarata la inceput un nou metadata cu numele construit mai devreme si cu continutul tag-ului "content"
					listOfMetadatas.add(new Metadata(name, metatag.attr("content")));
				}
			}
		}
		return listOfMetadatas;
	}
	
	// functie care returneaza link-uri sub forma de URL-uri absolute
	public static HashSet<String> getLinks(Document document) {
		
		HashSet<String> links = new HashSet<String>();  // structura de date care retine link-urile
		Elements href = document.select("a");           // retinem elementele HTML cu tag-ul "a"
		
		// pentru fiecare element cu tag-ul "a"
		for (Element hre : href)
		{
			// construieste url cu forma absoluta 
			String url = hre.attr("abs:href");     
			
			// daca url-ul contine "#"
			if(url.contains("#")) {      
				
				// preiau pozitia lui "#" in url
				int index = url.indexOf("#");
				
				// daca avem caractere pana la "#"
				if(url.substring(0,index).length() != 0){
					
					// adaugam la structura de date declarata la inceput textul link-ului impreuna cu link-ul efectiv pana la "#"
					links.add(hre.text() + "-> " + url.substring(0, index));
				}

			}
			// altfel adaugam textul link-ului impreuna cu link-ul efectiv
			else{
				links.add(hre.text() + "-> " + url);
			}
		}
		return links;
	}
	
	// functie ce returnaza textul dintr-un document HTML
	public static String getText(Document document){
		return document.body().text();
	}
	
	// functie ce verifica daca un cuvant este exceptie
	public static boolean isException(String word){
		return exceptions.contains(word);
	}
	
	// functie ce verifica daca un cuvant este stopword
	public static boolean isStopword(String word){
		return stopwords.contains(word);
	}
	
	public void countTheWordsFromAText(String text, HashMap<String,Integer> hashMapOfWords) {
		char character;
		String word = "";
		
		// calculam lungimea textului
		int len = text.length(); 
		
		// pentru fiecare caracter din text
		for(int i = 0; i < len; i++) {
			
			// retinem caracterul de la pozitia i
			character = text.charAt(i);
			
			// verificam daca acest caracter este litera mica, litera mare sau cifra (verificare simpla al codului ASCII)
			// daca nu se respecta niciuna dintre aceste conditii, inseamna ca suntem la sfarsitul unui cuvant
			if((character<'a'|| character>'z') && ( character<'A' || character>'Z') && (character<'0' || character>'9')) {
				
				// daca avem un cuvant ce contine caractere
				if(word != "") {
					
					// verificam daca este exceptie: daca da, il retinem in hashMap. daca exista deja in hashMap, marim doar numarul de aparitii al acestui cuvant
					if(isException(word)) {
						if(hashMapOfWords.containsKey(word)) {
							word = word.toLowerCase();
							hashMapOfWords.put(word, hashMapOfWords.get(word) + 1);  // incrementam numarul de aparitii al cuvantului
						}
						else{
							word = word.toLowerCase();
							hashMapOfWords.put(word, 1);
						}
						word=""; // reinitializare cuvant
					}
					else {
						word = word.toLowerCase();
						
						// daca este stopword, reinitializam cuvantul
						if(isStopword(word)) {
							word="";
						}
						
                        // altfel, aplicam algoritmul de stemming si se aplica regula ca la exceptii, cu numarul de aparitii
						else {
							
						    Stemmer stemm = new Stemmer();
							char[] characters = word.toCharArray();
							
							// Se adauga cuvantul pentru stemming
							stemm.add(characters, characters.length);
							
							// Aplica algoritmul de stemming
							stemm.stem();
							
							// Retine rezultatul algoritmului
							word = stemm.toString();
							
							// Adauga la hashMap sau actualizeaza numarul de aparitii
							if(hashMapOfWords.containsKey(word)){
								hashMapOfWords.put(word, hashMapOfWords.get(word) + 1);
							}
							else {
								hashMapOfWords.put(word, 1);
							}
							
							word = ""; // reinitializeaza cuvantul
						}
					}
				}
			}
			
			// daca una dintre conditiile primului if se respecta, se formeaza cuvantul prin adaugarea literei sau cifrei la acesta
			else {
				word += character;
			}
		}
	}
}
