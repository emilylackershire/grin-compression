package edu.grinnell.csc207.compression;

import java.util.Map;
import java.util.PriorityQueue;

/**
 * A HuffmanTree derives a space-efficient coding of a collection of byte
 * values.
 *
 * The huffman tree encodes values in the range 0--255 which would normally
 * take 8 bits. However, we also need to encode a special EOF character to
 * denote the end of a .grin file. Thus, we need 9 bits to store each
 * byte value. This is fine for file writing (modulo the need to write in
 * byte chunks to the file), but Java does not have a 9-bit data type.
 * Instead, we use the next larger primitive integral type, short, to store
 * our byte values.
 */
public class HuffmanTree {
    private static Node root;

    /**
     * gets the root
     * 
     * @return root
     */
    public static Node getRoot() {
        return root;
    }

    /**
     * builds huffman tree
     * 
     * @param freqs
     */
    public HuffmanTree(Map<Short, Integer> freqs) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        for (Map.Entry<Short, Integer> entry : freqs.entrySet()) {
            queue.add(new Node(entry.getKey(), entry.getValue()));
        }
        while (queue.size() > 1) {
            Node left = queue.poll();
            Node right = queue.poll();
            Node parent = new Node(left, right);
            queue.add(parent);
        }
        Node root = queue.poll();
    }

    /**
     * Constructs a new HuffmanTree from the given file.
     * 
     * @param in the input file (as a BitInputStream)
     */
    public HuffmanTree(BitInputStream in) {
        root = huffmanTreeHelper(in);
    }

    private Node huffmanTreeHelper(BitInputStream in) {
        int bit = in.readBit();
        if (bit == 0) {
            short value = (short) in.readBits(9);
            return new Node(value, 0);
        } else {
            Node left = huffmanTreeHelper(in);
            Node right = huffmanTreeHelper(in);
            return new Node(left, right);
        }
    }

    /**
     * decode helper
     * 
     * @param in   the bitstream file to decode
     * @param current the root of the tree, then updated 
     * @return
     */
    public void decodeHelper(BitInputStream in, Node current) {
        int bit = in.readBit();
        short value;
        if (bit == 0) {
            value = (short) in.readBits(9);
            current = new Node(value, 1);
        } else {
            current = new Node((short) -1, 0);
            decodeHelper(in, current.left);
            decodeHelper(in, current.right);
        }
    }

    /**
     * Writes this HuffmanTree to the given file as a stream of bits in a
     * serialized format.
     * 
     * @param node the root of the tree to serialize
     * @param out the output file as a BitOutputStream
     */
    public static void serializeHelper(Node node, BitOutputStream out) {
        if (node.isLeaf()) {
            out.writeBit(0);
            out.writeBits(node.value, 9);
        } else {
            out.writeBit(1);
            serializeHelper(node.left, out);
            serializeHelper(node.right, out);
        }
    }

    /**
     * Writes this HuffmanTree to the given file as a stream of bits in a
     * serialized format.
     * 
     * @param out the output file as a BitOutputStream
     */
    public static void serialize(BitOutputStream out) {
        serializeHelper(root, out);
    }

    /**
     * Encodes the file given as a stream of bits into a compressed format
     * using this Huffman tree. The encoded values are written, bit-by-bit
     * to the given BitOuputStream.
     * 
     * @param in  the file to compress.
     * @param out the file to write the compressed output to.
     */
    public void encode(BitInputStream in, BitOutputStream out) {
        while (true) {
            int value = in.readBits(9);
            if (value == 256) {
                break;
            }
            Node current = root;
            while (!current.isLeaf()) {
                if (current.left.value == value) {
                    out.writeBit(0);
                    current = current.left;
                } else {
                    out.writeBit(1);
                    current = current.right;
                }
            }
        }
        out.writeBit(0); 
    }

    /**
     * Decodes a stream of huffman codes from a file given as a stream of
     * bits into their uncompressed form, saving the results to the given
     * output stream. Note that the EOF character is not written to out
     * because it is not a valid 8-bit chunk (it is 9 bits).
     * 
     * @param in  the file to decompress.
     * @param out the file to write the decompressed output to.
     */
    public void decode(BitInputStream in, BitOutputStream out) {
        boolean isNotEof = true;
        while (isNotEof) {
            Node current = root;
            while (!current.isLeaf()) {
                int bit = in.readBit();
                if (bit == 0) {
                    current = current.left;
                } else {
                    current = current.right;
                }
            }
            if (current.value == 256) {
                isNotEof = false;
            } else {
                out.writeBits(current.value, 8);
            }
        }
    }
}
