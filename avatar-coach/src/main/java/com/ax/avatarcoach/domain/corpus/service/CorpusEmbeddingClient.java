package com.ax.avatarcoach.domain.corpus.service;

import java.util.List;

public interface CorpusEmbeddingClient {

    List<Double> embed(String text);
}
