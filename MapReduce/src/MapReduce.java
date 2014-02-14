import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;


public class MapReduce {
	public static void main(String[] args) {
		ArrayList<Pairs> words_list = new ArrayList<Pairs>();
		Map mapper = new Map();
		Reduce reducer = new Reduce();
		File input_file = new File("test");
		//File input_file = new File("C:/Users/Malathy/workspace/MapReduce/src/word_count");
		File output_file = new File("op");
		if (0 < args.length) {
		    String filename = args[0];
		    input_file = new File(filename);
		  }

		try {
			BufferedReader fr = new BufferedReader(new InputStreamReader(new FileInputStream(input_file), "ASCII"));
			Pairs word;
			while(true)
			{
				String line = fr.readLine();
				if(line==null)
					break;
				mapper.mapper(line,words_list);
				}
			mapper.print_mapper(words_list);

			HashMap<String,Integer> final_list = new HashMap<String,Integer>();
			reducer.reducer(words_list,final_list);
			
			System.out.println("\nPath of the output file : " + output_file.getAbsolutePath());
			PrintWriter writer = new PrintWriter(output_file, "UTF-8");
			for (Entry<String, Integer> entry : final_list.entrySet()) {
				String key = entry.getKey();
				Integer value = entry.getValue();
				writer.println(key+"-"+value);
			}
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
class Map
{
	public void mapper(String line, ArrayList<Pairs> words_list)
	{
		String[] words = line.split(" ");//those are your words
		Pairs word;
		System.out.println(words.length);
		for (int i = 0; i < words.length; i++) {
			if (!(words[i].equals("")))
			{
				word = new Pairs(words[i],1);
				words_list.add(word);
				
			}

		}
	}
	public void print_mapper( ArrayList<Pairs> words_list){
		System.out.println("Mapped");
		for (int i = 0; i <words_list.size(); i++) {
			words_list.get(i).display();
		}
	}
}

class Reduce
{
	public void reducer( ArrayList<Pairs> words_list, HashMap<String, Integer> final_list)
	{
		for (int i = 0; i <words_list.size(); i++) {
			String key = words_list.get(i).getKey();
			Integer value = words_list.get(i).getValue();
			if (final_list.containsKey(key)){
				Integer previous_value = final_list.get(key);
				final_list.put(key,previous_value+value);}
			else
				final_list.put(key,value);
		}
		print_reducer(final_list);
	}
	public void print_reducer(HashMap<String, Integer> final_list){
		System.out.println("\n\nReduced");
		for (Entry<String, Integer> entry : final_list.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();
			System.out.println(key+"-"+value);
		}
	}
}
class Pairs
{

	public String key;
	public Integer value;

	public Pairs(String key, Integer value)
	{
		this.key = key;
		this.value = value;
	}
	public  void display()
	{
		System.out.println(key+"-"+value);  
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}

}