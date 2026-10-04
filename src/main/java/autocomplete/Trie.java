package autocomplete;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Trie {
    private static class Node{
        final Map<Character, Node> children = new TreeMap<>();
        boolean isWord;
    }

    private final Node root= new Node();

    public void insert(String word){
        Node node = root;
        for(char c: word.toCharArray()){
            node = node.children.computeIfAbsent(c, k -> new Node());
        }
        node.isWord=true;
    }

    public List<String> startsWith(String prefix){
        Node node = root;
        for(char c: prefix.toCharArray()){
            node = node.children.get(c);
            if (node == null) {
                return List.of();
            }
        }

        List<String> res = new ArrayList<>();
        collect(node, new StringBuilder(prefix), res);
        return res;
    }

    private void collect(Node node, StringBuilder path, List<String> results){
        if (node.isWord) {
            results.add(path.toString());
        }

        for(Map.Entry<Character, Node> entry: node.children.entrySet()){
            path.append(entry.getKey());
            collect(entry.getValue(), path, results);
            path.deleteCharAt(path.length()-1);
        }
    }
}
