package model.exceptions;

import model.Token;

public class IdentifierExpectedException extends Exception{
  Token token;
  public IdentifierExpectedException(String message) {
    super(message);
  }

  public Token getToken() {
    return token;
  }

  public IdentifierExpectedException(String message, Token token) {
    super(message);
    this.token = token;
  }
}
