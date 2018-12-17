package model;

import javafx.util.Pair;
import sun.security.krb5.SCDynamicStoreConfig;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public abstract class Tokenizer {
  protected Map<String, Integer> keywords = new LinkedHashMap<String, Integer>();
  protected int symbolCategories[] = new int[255];
  protected Map<Integer, List<Integer>> twoSymbolsDelimiters = new LinkedHashMap<Integer, List<Integer>>();
  private int identifiersCount = 1000;
  private int digitCount = 500;

  public int getDigitCount() {
    ++digitCount;
    if(digitCount>=600) throw new RuntimeException();
    return digitCount;
  }

  public int getIdentifiersCount() {
    return ++identifiersCount;
  }

  public Tokenizer(Map<String, Integer> keywords, List<Integer> delimiters) {
    initSymbolCategories(delimiters);
    initKeywordTable(keywords);

  }

  private void initSymbolCategories(List<Integer> delimiters) {
    for(int i =0;i<255;i++) {
      symbolCategories[i] = SymbolCategory.WRONG;
    }
    for (int i = 8; i <= 14; i++) {
      symbolCategories[i] = SymbolCategory.SPACE;
    }
    for (int i = 48; i <= 57; i++) {
      symbolCategories[i] = SymbolCategory.DIGIT;
    }
    for (int i = 65; i <= 122; i++) {
      symbolCategories[i] = SymbolCategory.LETTER;
    }
    symbolCategories[32] = SymbolCategory.SPACE;

    for(Integer symbol : delimiters) {
      symbolCategories[symbol] = SymbolCategory.ONE_SYMBOL_DELIMITER;
    }
    //TODO redo comments abstraction
    symbolCategories[40] = SymbolCategory.COMMENT_BEGIN;
    twoSymbolsDelimiters.put((int)'.', new ArrayList<Integer>());
    twoSymbolsDelimiters.get((int)'.').add((int) '.');
  }

  private void initKeywordTable(Map<String, Integer> keywords) {
    this.keywords.putAll(keywords);
  }

  protected int getSymbolCategory(int ch) {
    return symbolCategories[ch];
  }

  protected boolean isKeyword(Token token) {
    return keywords.containsKey(token.getValue());
  }

  protected int getKeyWordCode(String key) {
    List<String> arrayList = new ArrayList<String>(keywords.keySet());
    return 400 + arrayList.indexOf(key);

  }
}
