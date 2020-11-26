package org.justcodecs.dsd;

import org.justcodecs.dsd.Decoder.DecodeException;

import java.io.IOException;

public class ChunkFS extends BaseChunk {
    int sampleRate;

    @Override
    void read(DSDStream ds) throws DecodeException {
        super.read(ds);
        try {
            sampleRate = ds.readInt(true);
            //System.out.printf("Rate %d%n", sampleRate);
        } catch (IOException e) {
            throw new DecodeException("", e);
        }
    }
}
