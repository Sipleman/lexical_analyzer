package model;

public class Token {
  private int code;
  private int symbol;
  private int position;
  private int line;
  private String value = "";
  private int category;

  public int getCode() {
    if(code>500 && code<600) {
      return Integer.parseInt(value);
    }
    return code;
  }

  public Token(int symbol, int colon, int line, String value) {
    this.symbol = symbol;
    this.position = colon;
    this.line = line;
    this.value = value;
  }

  public Token(int symbol, int colon, int line, int category, int code) {
    this.symbol = symbol;
    this.position = colon;
    this.line = line;
    this.category = category;
    this.code = code;
    addCharacterToValue(symbol);
  }

  public void setCode(int code) {
    this.code = code;
  }

  public Token(int symbol, int colon, int line, int category) {
    this.symbol = symbol;
    this.position = colon;
    this.line = line;
    this.category = category;
    addCharacterToValue(symbol);
  }

//  public Token(int symbol, int colon, int line, int category, int code) {
//    this.symbol = symbol;
//    this.position = colon;
//    this.line = line;
//    this.category = category;
//    addCharacterToValue(symbol);
//  }

  public int getSymbol() {
    return symbol;
  }

  public int getPosition() {
    return position;
  }

  public int getLine() {
    return line;
  }

  public String getValue() {
    return value;
  }

  public void addCharacterToValue(int ch) {
    char c = (char) ch;
    value += c;
  }

  public int getCategory() {
    return category;
  }
}
