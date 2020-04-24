package htmlProcessing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

// Clasa ajutatoare pentru indexarea indirecta ( documente relative la indecsi ) 

public class IndirectIndexObject {
	
	String word;
	HashMap<String, Integer> documents;
	
	public IndirectIndexObject() {
		word = " ";
		documents = new HashMap<String, Integer>();
	}
	
	public IndirectIndexObject(String word, HashMap<String, Integer> documents) {
		this.word = word;
		this.documents = documents;
	}
	
	public String getWord() {
		return word;
	}
	
	public void setWord(String word) {
		this.word = word;
	}
	
	public HashMap<String, Integer> getDocuments() {
		return documents;
	}
	
	public void setDocuments(HashMap<String, Integer> docs) {
		this.documents = docs;
	}
	
	public void add(String file, Integer index){
		documents.put(file, index);
	}
	
	public HashSet<String> getOnlyDocuments() {
		HashSet<String> documents = new HashSet<String>();
		Iterator<Entry<String, Integer>> mapIterator = this.documents.entrySet().iterator();
	    while (mapIterator.hasNext()) {
	        Map.Entry<String,Integer> pair = (Map.Entry<String,Integer>)mapIterator.next();
	        documents.add(pair.getKey());
	        mapIterator.remove(); 
	    }
		return documents;
	}	
}