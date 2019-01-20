package syntax_analyzer;

import java.util.HashMap;
import java.util.Map;

public abstract class Keywords {
  public static final String PROGRAM = "PROGRAM";
  public static final String BEGIN = "BEGIN";
  public static final String END = "END";
  public static final String VAR = "VAR";
  public static final String SIGNAL = "SIGNAL";
  public static final String COMPLEX = "COMPLEX";
  public static final String INTEGER = "INTEGER";
  public static final String FLOAT = "FLOAT";
  public static final String BLOCKFLOAT = "BLOCKFLOAT";
  public static final String EXT = "EXT";

  public static Map<String, Integer> getAttributes(){
    HashMap<String, Integer> map = new HashMap<String, Integer>();
    map.put(SIGNAL, 0);
    map.put(COMPLEX, 0);
    map.put(INTEGER, 0);
    map.put(FLOAT, 0);
    map.put(BLOCKFLOAT, 0);
    map.put(EXT, 0);
    return map;
  }
}
