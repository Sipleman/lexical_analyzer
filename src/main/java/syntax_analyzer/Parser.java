package syntax_analyzer;

import model.Delimiter;
import model.SymbolCategory;
import model.Token;
import model.TokenParser;
import model.exceptions.EmptyException;
import model.exceptions.IdentifierExpectedException;
import model.exceptions.SpecialTokenExpected;
import sun.awt.Symbol;
import sun.rmi.runtime.NewThreadAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Parser {

  private SimpleTree tree;

  private final Stack lexemes;
  private final List<Token> identifiers;
  private final List<Token> constants;
  private final Map<String, Integer> keywords;
  private final List<String> finalKeywordsTable;
  private final int delimiters[];

  public SimpleTree getTree() {
    return tree;
  }

  public Parser(List<Token> lexemes, List<Token> identifiers,
      List<Token> constants, Map<String, Integer> keywords,
      List<Integer> delimiters) {
    this.lexemes = new Stack(lexemes);
    this.identifiers = identifiers;
    this.constants = constants;
    this.keywords = keywords;
    this.finalKeywordsTable = new ArrayList<String>(keywords.keySet());
    this.delimiters = getArrayFromList(delimiters);
  }

  private int[] getArrayFromList(List<Integer> list) {
    int arr[] = new int[255];
    int counter = 1;
    for (int i = 0; i < list.size(); i++) {
      arr[list.get(i)] = counter;
    }
    return arr;
  }

  private int getKeywordCodeByString(String keyword) {
    return 400 + finalKeywordsTable.indexOf(keyword);
  }

  private String getKeywordByCode(int code) {
    if (code < 255 && delimiters[code] != 0) {
      return String.valueOf((char) code);
    }
    if (code - 400 < finalKeywordsTable.size()) {
      return finalKeywordsTable.get(code - 400);
    }
    throw new RuntimeException("No such code exist in keywords table");
  }

  public void parse() {
    try {
      parseSignal();
    } catch (SpecialTokenExpected specialTokenExpected) {
      throw new RuntimeException(
          String.format("Got wrong symbol: %s", specialTokenExpected.getToken().getValue()));
    }
  }

  public SimpleTree parseSignal() throws SpecialTokenExpected {
    tree = new SimpleTree(new Token(0, 0, 0, "<signal-program>"));
    parseProgram(tree.getRoot());
    return tree;
  }

  public void parseProgram(SimpleTree.Node node) throws SpecialTokenExpected {
    parseTokenByCode(node, getKeywordCodeByString(Keywords.PROGRAM));
    try {
      parseProcedureIdentifiers(node);
    } catch (IdentifierExpectedException e) {
      throw new RuntimeException(
          String.format("Expected identifier, instead got: %s", e.getToken().getValue()));
    }
    parseTokenByCode(node, Delimiters.SEMICOLON);
    parseBlock(node);
    parseTokenByCode(node, Delimiters.DOT);
  }

  public void parseBlock(SimpleTree.Node node) {
    SimpleTree.Node block = new SimpleTree.Node(new Token(0, 0, 0, "<block>"));
    node.add(block);
    parseDeclarations(block);
    try {
      parseTokenByCode(block, getKeywordCodeByString(Keywords.BEGIN));
    } catch (SpecialTokenExpected specialTokenExpected) {
      throw new RuntimeException(
          String.format("Expected keyword %s, instead got %s", Keywords.BEGIN, specialTokenExpected.getToken().getValue()));
    }
    parseStatemetsList(block);
    try {
      parseTokenByCode(block, getKeywordCodeByString(Keywords.END));
    } catch (SpecialTokenExpected specialTokenExpected) {
      throw new RuntimeException(
          String.format("Expected keyword %s, instead got %s", Keywords.END, specialTokenExpected.getToken().getValue()));
    }
  }

  private void parseStatemetsList(SimpleTree.Node node) {
    SimpleTree.Node newNode = new SimpleTree.Node(
        new Token(0, 0, 0, "<statements-list>"));
    node.add(newNode);
    newNode.add(new SimpleTree.Node(new Token(0,0,0, "<empty>")));
  }

  private void parseDeclarations(SimpleTree.Node node) {
    SimpleTree.Node newNode = new SimpleTree.Node(
        new Token(0, 0, 0, "<declarations>"));
    node.add(newNode);
    parseVariableDeclarations(newNode);
  }

  private void parseVariableDeclarations(SimpleTree.Node node) {
    SimpleTree.Node newNode = new SimpleTree.Node(
        new Token(0, 0, 0, "<variable-declarations>"));
    node.add(newNode);
//    boolean isVarNext = checkIdentifier(getKeywordCodeByString(Keywords.VAR));
    try {
      parseTokenByCode(newNode, getKeywordCodeByString(Keywords.VAR));
      parseDeclarationsList(newNode);
    } catch (SpecialTokenExpected specialTokenExpected) {
      lexemes.add(specialTokenExpected.getToken());
      SimpleTree.Node empty = new SimpleTree.Node(
          new Token(0, 0, 0, "<empty>"));
      newNode.add(empty);
    }
  }

  private boolean checkIdentifier(int keywordCode) {
    Token token = lexemes.getNext();
    if (token.getCode() == keywordCode) {
      lexemes.add(token);
      return true;
    }
    lexemes.add(token);
    return false;
  }

  private void parseDeclarationsList(SimpleTree.Node node) {
    SimpleTree.Node newNode = new SimpleTree.Node(
        new Token(0, 0, 0, "<declarations-list>"));
//    node.add(newNode);

    try {
      parseDeclaration(newNode);
      node.add(newNode);
    } catch (IdentifierExpectedException e) {
      lexemes.add(e.getToken());
      SimpleTree.Node empty = new SimpleTree.Node(
          new Token(0, 0, 0, "<empty>"));
      newNode.add(empty);
      node.add(newNode);
      return;
    }
    parseDeclarationsList(newNode);
  }

  private void parseDeclaration(SimpleTree.Node node)
      throws IdentifierExpectedException {
    SimpleTree.Node newNode = new SimpleTree.Node(
        new Token(0, 0, 0, "<declaration>"));
    node.add(newNode);
//    node.add(newNode);
    parseVariableIdentifier(newNode);
    parseIdentifiersList(newNode);
    try {
      parseTokenByCode(newNode, Delimiters.COLON);
    } catch (SpecialTokenExpected ex) {
      throw new RuntimeException(
          "Wrong token, expected ':'");
    }
    try {
      parseAttribute(newNode);
    } catch (SpecialTokenExpected specialTokenExpected) {
      throw new RuntimeException(
          String.format("Attribute expected, instead got %s", specialTokenExpected.getToken().getValue()));
    }
    parseAttributesList(newNode);
    try {
      parseTokenByCode(newNode, Delimiters.SEMICOLON);
    } catch (SpecialTokenExpected specialTokenExpected) {
      throw new RuntimeException(
          String.format("Delimiter  %s expected, instead got %s", Delimiters.SEMICOLON, specialTokenExpected.getToken().getValue()));
    }
  }

  private void parseAttributesList(SimpleTree.Node node) {
    SimpleTree.Node newNode = new SimpleTree.Node(
        new Token(0, 0, 0, "<attributes-list>"));
    node.add(newNode);
    try {
      parseAttribute(newNode);
    } catch (SpecialTokenExpected ex) {
      lexemes.add(ex.getToken());
      SimpleTree.Node empty = new SimpleTree.Node(
          new Token(0, 0, 0, "<empty>"));
      newNode.add(empty);
      return;
    }
    parseAttributesList(newNode);
  }

  private void parseAttribute(SimpleTree.Node node)
      throws SpecialTokenExpected {
    Token token = lexemes.getNext();
    SimpleTree.Node newNode = new SimpleTree.Node(
        new Token(0, 0, 0, "<attribute>"));
    if (isAttribute(token)) {
      newNode.add(token);
    } else {
      lexemes.add(token);
      parseTokenByCode(node, Delimiters.LEFT_BRACE);
      parseRange(node);
      parseRangesList(node);
      try {
        parseTokenByCode(node, Delimiters.RIGHT_BRACE);
      } catch (SpecialTokenExpected ex) {
        throw new RuntimeException(
            String.format("Unexpected symbol at line: %d, pos: %d. Value: %s",
                ex.getToken().getLine(), ex.getToken().getPosition(),
                ex.getToken().getValue()));
      }
    }
    node.add(newNode);

  }

  private void parseRangesList(SimpleTree.Node node) {
    SimpleTree.Node newNode = new SimpleTree.Node(
        new Token(0, 0, 0, "<ranges-list>"));
    try {
      parseTokenByCode(newNode, Delimiters.COMA);
    } catch (SpecialTokenExpected specialTokenExpected) {
      lexemes.add(specialTokenExpected.getToken());
      node.add(new SimpleTree.Node(new Token(0,0,0, "<empty>")));
      return;
    }
    parseRange(newNode);
    parseRangesList(newNode);
  }

  private void parseRange(SimpleTree.Node node) {
    SimpleTree.Node newNode = new SimpleTree.Node(
        new Token(0, 0, 0, "<range>"));
    parseUnsignedInteger(newNode);
    try {
      parseTwoDelimiterToken(newNode, Delimiters.DOUBLE_DOT);
    } catch (SpecialTokenExpected specialTokenExpected) {
      throw new RuntimeException(specialTokenExpected.getMessage());
    }
    parseUnsignedInteger(newNode);
    node.add(newNode);
  }

  private void parseTwoDelimiterToken(SimpleTree.Node node, String doubleDot)
      throws SpecialTokenExpected {
    Token next = lexemes.getNext();
    if (next.getValue().equals(doubleDot)) {
      node.add(next);
    } else {
      throw new SpecialTokenExpected("Expected two delimiter", next);
    }
  }

  private void parseUnsignedInteger(SimpleTree.Node node) {
    SimpleTree.Node newNode = new SimpleTree.Node(
        new Token(0, 0, 0, "<unsigned-integer>"));
    node.add(newNode);
    try {
      parseDigit(newNode);
    } catch (SpecialTokenExpected specialTokenExpected) {
      throw new RuntimeException(
          String.format("Expected digit, instead got: %s",
              specialTokenExpected.getToken().getValue()));
    }
    parseDigitsString(newNode);
  }

  private void parseDigitsString(SimpleTree.Node node) {
    SimpleTree.Node newNode = new SimpleTree.Node(
        new Token(0, 0, 0, "<digits-string>"));
    node.add(newNode);
    try {
      parseDigit(newNode);
    } catch (SpecialTokenExpected specialTokenExpected) {
      lexemes.add(specialTokenExpected.getToken());
      newNode.add(new SimpleTree.Node(new Token(0, 0, 0, "<empty>")));
      return;
    }
    parseDigitsString(newNode);
  }

  private void parseDigit(SimpleTree.Node node) throws SpecialTokenExpected {
    Token next = lexemes.getNext();
    if (isDigit(next)) {
      SimpleTree.Node newNode = new SimpleTree.Node(
          new Token(0, 0, 0, "<digit>"));

      newNode.add(new SimpleTree.Node(next));
      node.add(newNode);
    } else {
      throw new SpecialTokenExpected(
          String.format("Expected digit, instead got: %s", next.getValue()),
          next);
    }
  }

  private boolean isDigit(Token next) {
    return next.getCategory() == SymbolCategory.DIGIT;
  }

  private boolean isAttribute(Token token) {
    return Keywords.getAttributes().containsKey(token.getValue());
  }

  private void parseIdentifiersList(SimpleTree.Node node) {
    SimpleTree.Node newNode = new SimpleTree.Node(
        new Token(0, 0, 0, "<identifiers-list>"));
    node.add(newNode);
    try {
      Token comaToken = parseTokenByCode(newNode, Delimiters.COMA);

      parseVariableIdentifier(newNode);
    }
    catch (SpecialTokenExpected exp) {
      lexemes.add(exp.getToken());
      newNode.add(new Token(0, 0, 0, "<empty>"));
      return;
    }
    catch (IdentifierExpectedException ex) {
      throw new RuntimeException(
          String.format(
              "Wrong token, expected variable identifier, got token with category: %s with value: %s",
              ex.getToken().getCategory(), ex.getToken().getValue()));
    }
    parseIdentifiersList(newNode);
  }

  private void parseVariableIdentifier(SimpleTree.Node node)
      throws IdentifierExpectedException {
    SimpleTree.Node nextNewNode = new SimpleTree.Node(
        new Token(0, 0, 0, "<identifier>"));

    parseIdentifier(nextNewNode);

    SimpleTree.Node newNode = new SimpleTree.Node(
        new Token(0, 0, 0, "<variable-identifier>"));
    newNode.add(nextNewNode);
    node.add(newNode);
  }

  private boolean isIdentifier(Token token) {
    return token.getCode() > 1000 && token.getCode() < 2000;
  }

  private void parseProcedureIdentifiers(SimpleTree.Node node)
      throws IdentifierExpectedException {
    SimpleTree.Node newNode = new SimpleTree.Node(
        new Token(0, 0, 0, "<procedure-identifier>"));
    node.add(newNode);
    parseIdentifier(newNode);
  }

  private void parseIdentifier(SimpleTree.Node node)
      throws IdentifierExpectedException {
    // TODO write error analyzer
    Token token = lexemes.getNext();
    if (isIdentifier(token)) {
      node.add(token);
    } else {
      throw new IdentifierExpectedException(
          String.format("Wrong character at line %d pos %d", token.getLine(),
              token.getPosition()), token);
    }
  }

  private Token parseTokenByCode(SimpleTree.Node node, int tokenCode)
      throws SpecialTokenExpected {
    Token token = this.lexemes.getNext();
    if (token.getCode() == tokenCode) {
      node.add(token);
      return token;
    } else
      throw new SpecialTokenExpected(
          String.format("Wrong token! Got: %s, Expected: %s",
              getKeywordByCode(tokenCode),
              getKeywordByCode(token.getCode())), token);

  }

  private class Stack {
    private List<Token> list;
    private int counter = 0;

    public Stack(List<Token> list) {
      this.list = list;
    }

    public Token getNext() {
      if (list.size() == 0) {
        // TODO return value must be more valuable, but not exception
        throw new RuntimeException("Empty stack");
      }
      return list.remove(0);
    }

    public void add(Token token) {
      list.add(0, token);
    }
  }
}
