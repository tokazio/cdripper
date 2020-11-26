package org.justcodecs.dsd;

import org.justcodecs.dsd.Decoder.DecodeException;

import java.io.IOException;
import java.util.Arrays;

public class DATAChunk {
    public int signature = Utils.bytesToInt((byte) 'd', (byte) 'a', (byte) 't', (byte) 'a');
    public long chunkSize;
    long dataStart, dataEnd;
    byte[][] data;

    protected DATAChunk(DSDStream ds) throws DecodeException {
        try {
            if (ds.readInt(true) != signature)
                throw new DecodeException("Invalid signature for the chunk 'data'", null);
            chunkSize = ds.readLong(false);
            dataStart = ds.getFilePointer();
            dataEnd = dataStart + chunkSize - 12;
        } catch (IOException ioe) {
            throw new DecodeException("I/O exception " + ioe, ioe);
        }
    }

    public static DATAChunk read(DSDStream ds) throws DecodeException {
        return new DATAChunk(ds);
    }

    @Override
    public String toString() {
        return "DATAChunk [signature=" + signature + ", chunkSize=" + chunkSize + ", data=" + Arrays.toString(data)
                + "]";
    }

}
