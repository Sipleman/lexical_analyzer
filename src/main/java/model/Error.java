package model;

public class Error {
  int position;
  int line;
  String message;

  public Error(int position, int line, String message) {
    this.position = position;
    this.line = line;
    this.message = message;
  }

  public int getPosition() {
    return position;
  }

  public int getLine() {
    return line;
  }

  public String getMessage() {
    return message;
  }
}
