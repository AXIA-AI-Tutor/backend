package com.ax.avatarcoach.domain.corpus.client;

import java.util.List;

public record OllamaEmbedRequest(
    String model,
    List<String> input
) {
}
