package syntax_analyzer;

import model.Token;

import java.util.ArrayList;
import java.util.List;

public class SimpleTree {
  private Node root;

  public SimpleTree(Token rootData) {
    root = new Node(rootData);
    root.data = rootData;
    root.children = new ArrayList<Node>();
  }
  public Node getRoot(){
    return root;
  }

  public static class Node {
    private Token data;
    private Node parent;
    private List<Node> children;

    public Node(Token data) {
      this.data = data;
      children = new ArrayList<Node>();
    }

    public void add(Node node) {
      children.add(node);
    }

    public void add(Token token) {
      children.add(new Node(token));
    }
    public List<Node> getChildren() {
      return children;
    }

    public Node removePrev() {
      return children.remove(children.size()-1);
    }
    public void printPretty(String indent, Boolean last)
    {
      System.out.print(indent);
      if (last)
      {
        System.out.print("\\-");
        indent += "  ";
      }
      else
      {
        System.out.print("|-");
        indent += "| ";
      }
      System.out.println(this.data.getValue());

      for (int i = 0; i < children.size(); i++)
        children.get(i).printPretty(indent, i == children.size() - 1);
    }
  }
}
