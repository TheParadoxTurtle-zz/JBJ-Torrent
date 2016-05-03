package client;

import lib.Debug;
import java.io.*;

public class BitMapContainer {
    public static final int pieceLength = 256;
    public boolean[] bitmap;
    public byte[][] data;

    public BitMapContainer(int length) {
        data = new byte[length][pieceLength];
    }
    
    public BitMapContainer(String fileName) throws IOException {
        makeData(fileName);
    }

    // adds piece to data and updates bitmap
    public boolean addPiece(byte[] piece, int index) {
        if(bitmap[index]) {
            Debug.print("Index already filled");
            return false;
        }
        if(piece.length != pieceLength) {
            Debug.print("Invalid piece length");
            return false;
        }

        bitmap[index] = true;
        data[index] = piece;
        return true;
    }
    
    // makes data from file
    public void makeData(String fileName) throws IOException {
        FileInputStream in = new FileInputStream(fileName);
        File f = new File(fileName);
        int length = (int) Math.ceil(f.length()/((float)pieceLength));
        data = new byte[length][pieceLength];
        for(int i = 0; i < length; i++) {
            in.read(data[i]);
        }

        bitmap = new boolean[length];
        for(int i = 0; i < length; i++)
            bitmap[i] = true;
    }

}
