package edu.grinnell.csc207.compression;

public class Node {
    short value; // the value of this node
    int frequency; // the frequency of this node
    Node left; // the left child of this node
    Node right; // the right child of this node

    /**
     * Constructs a new Node with the given value and frequency.
     * 
     * @param value
     * @param frequency
     */
    public Node(short value, int frequency) {
        this.value = value;
        this.frequency = frequency;
    }

    /**
     * Constructs a new Node with the given left and right children.
     * 
     * @param left
     * @param right
     */
    Node(Node left, Node right) {
        this.left = left;
        this.right = right;
        this.frequency = left.frequency + right.frequency;
    }

    /**
     * Checks if is leaf
     * 
     * @return
     */
    boolean isLeaf() {
        return left == null && right == null;
    }
}
