import dnl.utils.text.table.TextTable;
import model.Error;
import model.Token;
import model.TokenParser;
import syntax_analyzer.Parser;
import syntax_analyzer.SimpleTree;
import utils.DictionaryParser;

import javax.swing.table.TableModel;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.System.exit;

public class Main {
  public static void s(String[] args) {
    SimpleTree tree = new SimpleTree(new Token(41, 1, 1, "Signal-program"));
    tree.getRoot().add(new SimpleTree.Node(new Token(31, 2, 1, "1")));
    tree.getRoot().add(new SimpleTree.Node(new Token(31, 2, 1, "2")));
    tree.getRoot().add(new SimpleTree.Node(new Token(31, 2, 1, "3")));
    tree.getRoot().getChildren().get(0).add(new SimpleTree.Node(new Token(31, 2, 1, "11")));
    tree.getRoot().getChildren().get(0).add(new SimpleTree.Node(new Token(31, 2, 1, "11")));
    tree.getRoot().getChildren().get(0).add(new SimpleTree.Node(new Token(31, 2, 1, "11")));
    tree.getRoot().getChildren().get(0).add(new SimpleTree.Node(new Token(31, 2, 1, "11")));
    tree.getRoot().getChildren().get(1).add(new SimpleTree.Node(new Token(31, 2, 1, "22")));
    tree.getRoot().printPretty(" ", true);
//    tree.getRoot().add(new SimpleTree.Node<String>("kek"));
//    tree.getRoot().add(new SimpleTree.Node<String>("kek"));
//    tree.getRoot().add(new SimpleTree.Node<String>("kek"));
//    tree.getRoot().add(new SimpleTree.Node<String>("kek"));
//    tree.getRoot().getChildren().get(0).add(new SimpleTree.Node<String>("kek"));
//    tree.getRoot().getChildren().get(0).add(new SimpleTree.Node<String>("kek"));
//    tree.getRoot().getChildren().get(1).add(new SimpleTree.Node<String>("kek"));
//    tree.getRoot().getChildren().get(1).add(new SimpleTree.Node<String>("kek"));
//    tree.getRoot().getChildren().get(1).add(new SimpleTree.Node<String>("kek"));
//    tree.getRoot().getChildren().get(2).add(new SimpleTree.Node<String>("kek"));
//    tree.getRoot().add(new SimpleTree.Node<String>("kek"));
//    tree.getRoot().add(new SimpleTree.Node<String>("kek"));
//    tree.getRoot().printPretty("", true);

  }
  public static void main(String[] args) {

    Map<String, Integer> keywords = null;
    List<Integer> delims = null;
    try {
      keywords = DictionaryParser.readKeywordsFile("gramma/keywords");
      delims = DictionaryParser.readDelimiters("gramma/one_symbol_delimiters");
    } catch (IOException e) {
      exit(-1);
    }
    TokenParser tokenParser = new TokenParser(keywords, delims);
    try {
      tokenParser.parse("input");
    } catch (IOException e) {
      e.printStackTrace();
    }
//    System.out.println(Arrays.toString(tokenParser.getIdentifiers().toArray()));

//    System.out.println("Identifiers");
//    System.out.println(String.format("%7s %7s", "value", "code"));
//    for (Token token : tokenParser.getIdentifiers()) {
//      System.out.println(String.format("%7s %7d",
//          token.getValue(), token.getCode()));
//    }
//
//    System.out.println();
//    System.out.println("Constants");
//    System.out.println(String.format("%7s %7s", "value", "code"));
//
//    for (Token token : tokenParser.getConstants()) {
//      System.out.println(String.format("%7s %7d",
//          token.getValue(), token.getCode()
//      ));
//    }
//
    System.out.println();
    System.out.println("Lexemes");
    System.out.println(String.format("%7s %7s %7s %7s", "value", "line", "position", "code"));

    for (Token token : tokenParser.getLexemes()) {
      System.out.println(String.format("%7s %7d %7d %7d",
          token.getValue(), token.getLine(), token.getPosition(), token.getCode()
      ));
    }
//
//    System.out.println();
//    if(tokenParser.getErrors().size()>0) {
//      System.out.println("Errors: ");
//      for (Error token : tokenParser.getErrors()) {
//        System.out.println(String.format("pos %d line %d value %s",
//            token.getPosition(), token.getLine(), token.getMessage()));
//      }
//    }
    Parser parser = new Parser(tokenParser.getLexemes(),
        tokenParser.getIdentifiers(), tokenParser.getConstants(), keywords,
        delims);
    parser.parse();
    parser.getTree().getRoot().printPretty("", true);
  }
}
