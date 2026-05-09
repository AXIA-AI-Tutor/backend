package com.ax.avatarcoach.domain.corpus.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("local")
public class FakeCorpusEmbeddingClient implements CorpusEmbeddingClient {

    private static final int DIMENSION = 1024;

    @Override
    public List<Double> embed(String text) {
        byte[] digest = sha256(text == null ? "" : text);
        List<Double> embedding = new ArrayList<>(DIMENSION);

        for (int i = 0; i < DIMENSION; i++) {
            int value = digest[i % digest.length] & 0xff;
            embedding.add((value / 255.0) - 0.5);
        }

        return embedding;
    }

    private byte[] sha256(String text) {
        try {
            return MessageDigest.getInstance("SHA-256")
                .digest(text.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available", e);
        }
    }
}
