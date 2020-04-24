package htmlProcessing;

import java.util.HashMap;


// Clasa ajutatoare pentru indexarea directa (indecsi relativ la document)

public class DirectIndexObject {
	
	String file;
	HashMap<String, Integer> words;
	
	public DirectIndexObject() {
		file = null;
		words = null;
	}
	public DirectIndexObject(String file, HashMap<String, Integer> words) {
		this.file = file;
		this.words = words;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public HashMap<String, Integer> getWords() {
		return words;
	}
	public void setWords(HashMap<String, Integer> words) {
		this.words = words;
	}

}
