package model;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TokenParser extends Tokenizer {
  private List<Token> lexemes = new ArrayList<Token>();
  private List<Token> identifiers = new ArrayList<Token>();
  private List<Error> errors = new ArrayList<Error>();
  private List<Token> constants = new ArrayList<Token>();

  private int buffer;

  public TokenParser(Map<String, Integer> keywords, List<Integer> delimiters) {
    super(keywords, delimiters);
  }

  public List<Error> getErrors() {
    return errors;
  }

  public List<Token> getLexemes() {
    return lexemes;
  }

  public List<Token> getIdentifiers() {
    return identifiers;
  }

  public List<Token> getConstants() {
    return constants;
  }

  public void parse(String fileName) throws IOException {
    int line = 1;
    int position = 1;
    File file = new File(fileName);
    InputStream inputStreamReader = new FileInputStream(file);
    Reader reader = new InputStreamReader(inputStreamReader,
        Charset.defaultCharset());
    Reader bufferedReader = new BufferedReader(reader);

    int ch;
    buffer = bufferedReader.read();
    while (buffer != -1) {
      int category = getSymbolCategory(buffer);
      if (buffer == '\n') {
        line++;
        position = 1;
        buffer = bufferedReader.read();
        continue;
      }
      if (category == SymbolCategory.SPACE) {
        buffer = bufferedReader.read();
        position++;
        continue;
      }
      if (category == SymbolCategory.LETTER) {
        Token token = new Token(buffer, position, line, category);
        do {
          buffer = bufferedReader.read();
          position++;
          if (buffer == -1) {
            break;
          }
          if(buffer == '\n') {
            line++;
            position++;
          }
          category = getSymbolCategory(buffer);
          if (category == SymbolCategory.LETTER ||
              category == SymbolCategory.DIGIT) {
            token.addCharacterToValue(buffer);
          }
        } while (category == SymbolCategory.LETTER ||
            category == SymbolCategory.DIGIT);

        if (isKeyword(token)) {
          token.setCode(getKeyWordCode(token.getValue()));
          lexemes.add(token);
        } else {
          addTokenToIdentifiers(token);
        }
        continue;
      }
      if (category == SymbolCategory.COMMENT_BEGIN) {
        int commentBeginPos = position;
        int lineBeginPos = line;
        ch = bufferedReader.read();
        position++;
        if (ch == '*') {
          buffer = bufferedReader.read();
          position++;
          do {
            if (buffer == -1) {
              errors.add(new Error(position, line,
                  String.format("Comment is not end at line: %d position: %s",
                      lineBeginPos, commentBeginPos)));
              break;
            }
            if(buffer == '\n') {
              line++;
              position=1;
            }
            if ((char) buffer == '*') {
              position++;
              buffer = bufferedReader.read();
              if (buffer == ')') {
                buffer = bufferedReader.read();
                break;
              }
              continue;
            }
            if (getSymbolCategory(buffer) == SymbolCategory.SPACE) {
              buffer = bufferedReader.read();
              position++;
              continue;
            }
            buffer = bufferedReader.read();
            position++;
          } while (true);
        } else {
          lexemes.add(
              new Token(buffer, position - 1, line, category, buffer));
          buffer = ch;
          continue;
        }
      }

      if (twoSymbolsDelimiters.containsKey(buffer)) {
        ch = bufferedReader.read();
        position++;
        if (twoSymbolsDelimiters.get(buffer).contains(ch)) {
          Token token = new Token(ch, position, line, category, 301);
          token.addCharacterToValue(ch);
          lexemes.add(token);
          buffer = bufferedReader.read();
          continue;
        } else {
          if(category == SymbolCategory.ONE_SYMBOL_DELIMITER) {
            lexemes.add(new Token(buffer, position, line, category, buffer));
          }else {
            errors.add(new Error(position, line, String.format(
                "Incorrect two symbol delimiter at line: %d pos: %d", line,
                position)));
          }
          buffer = ch;
          continue;
        }
      }
      if (category == SymbolCategory.ONE_SYMBOL_DELIMITER) {
        lexemes.add(new Token(buffer, position, line, category, buffer));
        buffer = bufferedReader.read();
        continue;
      }
      if (category == SymbolCategory.DIGIT) {
        Token token = new Token(buffer, position, line, category);
        do {
          buffer = bufferedReader.read();
          position++;
          if (buffer == -1) {
            break;
          }
          category = getSymbolCategory(buffer);
          if (category == SymbolCategory.DIGIT) {
            token.addCharacterToValue(buffer);
          } else if(category == SymbolCategory.LETTER) {
            errors.add(new Error(position, line, "Error digit declaration"));
          }
        } while (category == SymbolCategory.DIGIT);
        addConstant(token);
      }
      if(category == SymbolCategory.WRONG) {
        errors.add(new Error(position, line, "Unexcpected symbol error"));
        buffer = bufferedReader.read();
        position++;
      }
    }
  }

  private void addTokenToIdentifiers(Token token) {
    boolean isContain = false;
    for(Token t : identifiers) {
      if(token.getValue().equals(t.getValue())) {
        isContain = true;
        token.setCode(t.getCode());
        break;
      }
    }
    if(!isContain) {
      token.setCode(getIdentifiersCount());
      identifiers.add(token);
    }
    lexemes.add(token);

  }

  private void addConstant(Token token) {
    boolean isContain = false;
    for(Token t : constants) {
      if(token.getValue().equals(t.getValue())) {
        isContain = true;
        token.setCode(t.getCode());
        break;
      }
    }
    if(!isContain) {
      token.setCode(getDigitCount());
      constants.add(token);
    }
    lexemes.add(token);

  }

}
