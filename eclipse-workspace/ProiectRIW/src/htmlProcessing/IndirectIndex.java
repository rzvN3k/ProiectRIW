package htmlProcessing;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public class IndirectIndex {
	
	private static List<IndirectIndexObject> reverse(String filename) {
		
		List<IndirectIndexObject> list = new ArrayList<IndirectIndexObject>();
        Gson gson = new Gson();
        String json = Parser.readFromFile(filename);
        List<DirectIndexObject> listOfFiles = gson.fromJson(json, new TypeToken<List<DirectIndexObject>>() {}.getType());
        for (DirectIndexObject di : listOfFiles) {
			for (Map.Entry<String,Integer> word : di.getWords().entrySet()) {
				IndirectIndexObject indirectIndex = new IndirectIndexObject();
				indirectIndex.setWord(word.getKey());
				indirectIndex.add(di.getFile(), word.getValue()); 
				list.add(indirectIndex);
			}
		}
        return list;
	}
	
	private static List<IndirectIndexObject> gather(List<IndirectIndexObject> list){
		
		List<IndirectIndexObject> finalList = new ArrayList<IndirectIndexObject>();
		finalList.add(list.get(0));
		int last = 0;
		for (IndirectIndexObject wordsTemplate : list) {
			if(finalList.get(last).getWord().equals(wordsTemplate.getWord())){
				finalList.get(last).getDocuments().putAll(wordsTemplate.getDocuments());
			}
			else{
				finalList.add(wordsTemplate);
				++last;
			}
		}
		return finalList;
	}

	private static void sort(List<IndirectIndexObject> list) {
        list.sort(Comparator.comparing(IndirectIndexObject::getWord));
    }
	
	public HashMap<String, Integer> getDocumentsFromWord(String word, List<IndirectIndexObject> list)
	{
		HashMap<String, Integer> docs = new HashMap<String, Integer>();
		for (IndirectIndexObject wordsTemplate : list) {
			if(word==wordsTemplate.getWord())
			{
				docs = wordsTemplate.getDocuments();
			}
			else
			{
				System.out.println("Nu am gasit documente!");
				return null;
			}
		}
		return docs;
	}
	
	
	public static void writeIndirectIndexToFile(List<IndirectIndexObject> listOfIndirectObjects){

		Gson gs = new GsonBuilder().setPrettyPrinting().create();
		String strJson = gs.toJson(listOfIndirectObjects);
		
		Writer writer = null;
		try {
			writer = new FileWriter("indexIndirect.json");
			writer.write(strJson);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void indirectIndex(){
		List<IndirectIndexObject> list = IndirectIndex.reverse("indexDirect.json");
		sort(list);
		List<IndirectIndexObject> finalList = gather(list);
		writeIndirectIndexToFile(finalList);
		System.out.println("[INDEX INDIRECT] S-a rulat functia de indexare inversa!");
	}
}