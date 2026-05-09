package com.ax.avatarcoach.domain.corpus.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CorpusEmbeddingVectorValidator {

    public List<Double> validateAndNormalize(List<Double> embedding, int expectedDimension) {
        if (embedding == null || embedding.size() != expectedDimension) {
            throw new IllegalArgumentException("Embedding dimension must be " + expectedDimension);
        }

        double sum = 0.0;

        for (Double value : embedding) {
            if (value == null || !Double.isFinite(value)) {
                throw new IllegalArgumentException("Embedding contains non-finite value");
            }
            sum += value * value;
        }

        double norm = Math.sqrt(sum);

        if (norm <= 0.0) {
            throw new IllegalArgumentException("Embedding norm must be greater than zero");
        }

        List<Double> normalized = new ArrayList<>(embedding.size());

        for (Double value : embedding) {
            normalized.add(value / norm);
        }

        return normalized;
    }
}
