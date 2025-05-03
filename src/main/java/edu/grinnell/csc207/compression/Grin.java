package edu.grinnell.csc207.compression;

import java.io.IOException;
import java.util.Map;

/**
 * The driver for the Grin compression program.
 */
public class Grin {
    
    /**
     * Decodes the .grin file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     * @param infile the file to decode
     * @param outfile the file to ouptut to
     * @throws IOException 
     */
    public static void decode(String infile, String outfile) throws IOException {
        BitInputStream in = new BitInputStream(infile);
        BitOutputStream out = new BitOutputStream(outfile);
        int value = in.readBits(32);
        if (value != 0x736) {
            throw new IllegalArgumentException
                ("File is not a .grin file!!! Your value was: " + value);
        }
        HuffmanTree huffman = new HuffmanTree(in);
        huffman.decode(in, out);
    }

    /**
     * Creates a mapping from 8-bit sequences to number-of-occurrences of
     * those sequences in the given file. To do this, read the file using a
     * BitInputStream, consuming 8 bits at a time.
     * @param file the file to read
     * @return a freqency map for the given file
     * @throws IOException 
     */
    public static Map<Short, Integer> createFrequencyMap(String file) throws IOException {
        BitInputStream in = new BitInputStream(file);
        Map<Short, Integer> freqMap = new java.util.HashMap<>();
        boolean map = true;
        while (map) {
            short value = (short) in.readBits(8);
            if (value == -1) {
                map = false;
            }
            if (freqMap.containsKey(value)) {
                freqMap.put(value, freqMap.get(value) + 1);
            } else {
                freqMap.put(value, 1);
            }
        }
        return freqMap;
    }

    /**
     * Encodes the given file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     * @param infile the file to encode.
     * @param outfile the file to write the output to.
     */
    public static void encode(String infile, String outfile) {
        
    }

    /**
     * The entry point to the program.
     * @param args the command-line arguments.
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Usage: java Grin <encode|decode> <infile> <outfile>");
        String code = "decode"; //args[0];
        String infile = "huffman-example.grin"; //args[1]; C:\Users\lacke\Desktop\github\grin-compression\files\huffman-example.grin
        String outfile = "huffman-example.txt"; //args[2];
        if (code.equals("encode")) {
            encode(infile, outfile);
        } else if (code.equals("decode")) {
            decode(infile, outfile);
        } else {
            System.out.println("Your first argument must be either encode or decode");
        }
    }
}
