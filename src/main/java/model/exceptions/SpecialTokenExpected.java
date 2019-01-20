package model.exceptions;

import model.Token;

public class SpecialTokenExpected extends Exception {

  Token token;
  Token expected;

  public SpecialTokenExpected(String message) {
    super(message);
  }
  public SpecialTokenExpected(String message, Token token) {
    super(message);
    this.token = token;
  }
  public SpecialTokenExpected(String message, Token token, Token expected) {
    super(message);
    this.token = token;
    this.expected = expected;
  }

  public Token getToken() {
    return token;
  }
}
