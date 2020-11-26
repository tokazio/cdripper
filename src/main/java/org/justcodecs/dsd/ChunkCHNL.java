package org.justcodecs.dsd;

import org.justcodecs.dsd.Decoder.DecodeException;

import java.io.IOException;

public class ChunkCHNL extends BaseChunk {
    short numChannels;
    String IDs[];

    @Override
    void read(DSDStream ds) throws DecodeException {
        super.read(ds);
        try {
            numChannels = ds.readShort(true);
            IDs = new String[numChannels];
            for (int i = 0; i < numChannels; i++) {
                ds.readFully(IDBuf, 0, 4);
                IDs[i] = new String(IDBuf);
            }
            //System.out.printf("Channels %s%n", Arrays.toString(IDs));
        } catch (IOException e) {
            throw new DecodeException("", e);
        }
    }
}
