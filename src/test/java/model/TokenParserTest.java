package model;

import static java.lang.System.err;
import static org.junit.Assert.*;

import org.junit.Test;
import utils.DictionaryParser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.lang.System.exit;

public class TokenParserTest {

  @Test
  public void case1_test() throws IOException {
    final int ident = 2;
    final int lexemes = 7;
    final int errors = 0;
    final int constants = 0;
    Map<String, Integer> keywords = readKeywords();
    List<Integer> delims = readDelims();
    TokenParser parser = new TokenParser(keywords, delims);
    parser.parse("src/test/java/testcases/case1");

    assertEquals(parser.getIdentifiers().size(), ident);
    assertEquals(parser.getLexemes().size(), lexemes);
    assertEquals(parser.getErrors().size(), errors);
    assertEquals(parser.getConstants().size(), constants);

  }

  @Test
  public void case2_test() throws IOException {
    final int ident = 1;
    final int lexemes = 8;
    final int errors = 0;
    final int constants = 1;
    Map<String, Integer> keywords = readKeywords();
    List<Integer> delims = readDelims();
    TokenParser parser = new TokenParser(keywords, delims);
    parser.parse("src/test/java/testcases/case2");

    assertEquals(parser.getIdentifiers().size(), ident);
    assertEquals(parser.getLexemes().size(), lexemes);
    assertEquals(parser.getErrors().size(), errors);
    assertEquals(parser.getConstants().size(), constants);

  }

  @Test
  public void case3_test() throws IOException {
    final int ident = 4;
    final int lexemes = 18;
    final int errors = 2;
    int constants = 2;
    Map<String, Integer> keywords = readKeywords();
    List<Integer> delims = readDelims();
    TokenParser parser = new TokenParser(keywords, delims);
    parser.parse("src/test/java/testcases/case3");

    assertEquals(parser.getIdentifiers().size(), ident);
    assertEquals(parser.getLexemes().size(), lexemes);
    assertEquals(parser.getErrors().size(), errors);
    assertEquals(parser.getConstants().size(), constants);
  }

  @Test
  public void case4_test() throws IOException {
    final int ident = 4;
    final int lexemes = 19;
    final int errors = 2;
    int constants = 1;
    Map<String, Integer> keywords = readKeywords();
    List<Integer> delims = readDelims();

    TokenParser parser = new TokenParser(keywords, delims);
    parser.parse("src/test/java/testcases/case4");

    assertEquals(parser.getIdentifiers().size(), ident);
    assertEquals(parser.getLexemes().size(), lexemes);
    assertEquals(parser.getErrors().size(), errors);
    assertEquals(parser.getConstants().size(), constants);

  }

  private Map<String, Integer> readKeywords() {
    Map<String, Integer> keywords = null;
    try {
      keywords = DictionaryParser.readKeywordsFile("gramma/keywords");
    } catch (IOException e) {
      exit(-1);
    }
    return keywords;
  }
  private List<Integer> readDelims() {
    List<Integer> delims = null;
    try {
      delims = DictionaryParser.readDelimiters("gramma/one_symbol_delimiters");
    } catch (IOException e) {
      exit(-1);
    }
    return delims;
  }
}
