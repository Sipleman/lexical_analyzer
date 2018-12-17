package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryParser {
  public static Map<String, Integer> readKeywordsFile(String keywordsFile)
      throws IOException {
    Map<String, Integer> keywords = new HashMap<String, Integer>();
    File file = new File(keywordsFile);
    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
    String keyword = "";
    int i = 1000;
    while((keyword = bufferedReader.readLine())!=null) {
      ++i;
      keywords.put(keyword, i);
    }
    return keywords;
  }

  public static List<Integer> readDelimiters(String delimitersFile)
      throws IOException {
    List<Integer> delimiters = new ArrayList<Integer>();
    File file = new File(delimitersFile);
    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
    int delimiter;
    while((delimiter = bufferedReader.read())!=-1) {
      if(!isSpace(delimiter))
        delimiters.add(delimiter);
    }
    return delimiters;
  }

  private static boolean isSpace(int ch) {
    for (int i = 8; i <= 14; i++) {
      if(ch==i){
        return true;
      }
    }
    return false;
  }

}
