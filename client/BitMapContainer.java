package client;

import lib.Debug;
import java.io.*;

public class BitMapContainer {
    public static final int pieceLength = 256;
    public boolean[] bitmap;
    public byte[][] data;
    public long size;
    public int final_piece_size;

    public BitMapContainer(long size) {
    	this.size = size;
    	int n = (int) (size / pieceLength) + 1;
        data = new byte[n][pieceLength];
        bitmap = new boolean[n];
        final_piece_size = (int) (size % pieceLength);
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
        size = f.length();
        final_piece_size = (int) size % pieceLength;
        int length = (int) Math.ceil(size/((float)pieceLength));
        data = new byte[length][pieceLength];
        for(int i = 0; i < length; i++) {
            in.read(data[i]);
        }

        bitmap = new boolean[length];
        for(int i = 0; i < length; i++)
            bitmap[i] = true;
    }
    
    public byte[] getData(int index) {
    	if (index >= data.length || index < 0) {
    		return null;
    	}
    	return data[index];
    }
    
    public int getPieceLength(int index) {
    	if (index >= data.length || index < 0) {
    		return -1;
    	}
    	else if (index != data.length - 1) {
    		return data.length;
    	}
    	else {
    		return final_piece_size;
    	}
    }

    public static boolean[] bitmapFromString(String s) {
        boolean[] bitmap = new boolean[s.length()];
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '1') {
                bitmap[i] = true;
            }
            else {
                bitmap[i] = false;
            }
        }
        return bitmap;
    }

    public static String stringFromBitmap(boolean[] bitmap) {
        StringBuilder b = new StringBuilder();
        for(int i = 0; i < bitmap.length; i++) {
            if(bitmap[i]) {
                b.append("1");
            }
            else {
                b.append("0");
            }
        }
        return b.toString();
    }
}
