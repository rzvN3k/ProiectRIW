package htmlProcessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class DirectIndex {

	private static DirectIndexObject getFileTemplate(String path, Writer writer) throws IOException {
		
		DirectIndexObject dio = new DirectIndexObject();
		Writer inWriter = null;
		File file = new File(path);
		String filename = file.getName();   // numele fisierului cu tot cu extensie
		//System.out.println(filename);
		String[] aux = filename.split("\\.");   // array de stringuri, desparte numele de extensie
		/*for(String s : aux) {
			System.out.println(s);
		}*/
		String absolutePath = file.getAbsolutePath();  // calea absoluta catre fisier
		//System.out.println(absolutePath);
		String filePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));  // calea catre director
		//System.out.println(filePath);
		filename = aux[0];  // retinem numele fisierului
		
		// daca este de tip "html"
		if(aux[1].equals("html")) {
			dio.setFile(absolutePath); // setam calea catre fisierul html
			inWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath + "/" + filename + ".txt"), "utf-8"));
			Parser.parseInformations(inWriter, file);
			File f = new File(filePath + "/" + filename + ".txt");
			HashMap<String, Integer> w = Parser.parseText(f);
			dio.setWords(w);
			
			writer.write(file.getName() + "--->indexDirect.json\n");
			inWriter.close();
		}
		return dio;
	}
	
	private static List<DirectIndexObject> getListOfFileTemplates(List<String> paths, Writer writer) throws IOException {
		
		List<DirectIndexObject> listOfFileTemplates = new ArrayList<DirectIndexObject>();
		for (String path : paths) {
			DirectIndexObject dio = DirectIndex.getFileTemplate(path, writer);
			if(dio.getFile() != null && dio.getWords() != null) {
				listOfFileTemplates.add(dio);
			}
		}
		return listOfFileTemplates;
	}
	
	// functie ce scrie in format json indexul direct
	public static void writeDirectIndexToJsonFile(List<DirectIndexObject> listOfDirectIndexObjects) {
		Gson gs = new GsonBuilder().setPrettyPrinting().create();
		String strJson = gs.toJson(listOfDirectIndexObjects);
		Writer writer = null;
		try {
			writer = new FileWriter("indexDirect.json");
			writer.write(strJson);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void directIndex(String dirName) throws IOException {
		
		List<String> paths = Parser.getFilesFromDirectory(new File(dirName));
		Writer writer = null;
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("info.txt"), "utf-8"));
		
		List<DirectIndexObject> listOfFileTemplates = getListOfFileTemplates(paths, writer);
		writeDirectIndexToJsonFile(listOfFileTemplates);
		
		System.out.println("[INDEX DIRECT] S-a rulat functia de indexare directa!");
	
		writer.close();
	}
}
