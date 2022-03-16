package com.example.section8springbatch.ChunkListener;

import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterChunkError;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

public class CustomChunkListener {
    @BeforeChunk
    public void beforeChunk(ChunkContext chunkContext) {
        System.out.println(">> before Chunk");
    }

    @AfterChunk
    public void afterChunk(ChunkContext chunkContext) {
        System.out.println(">> After Chunk");
    }

    @AfterChunkError
    public void afterChunkError(ChunkContext chunkContext) {
        System.out.println(">> After Chunk Error");
    }
}
