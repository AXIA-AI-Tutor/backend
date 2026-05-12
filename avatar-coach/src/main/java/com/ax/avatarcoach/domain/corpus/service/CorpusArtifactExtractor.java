package com.ax.avatarcoach.domain.corpus.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class CorpusArtifactExtractor {

    public Path extract(Path artifactZipPath) {
        try {
            Path outputDirectory = Files.createTempDirectory("corpus-artifact-");

            try (InputStream inputStream = Files.newInputStream(artifactZipPath);
                 ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

                ZipEntry entry;
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    Path outputPath = outputDirectory.resolve(entry.getName()).normalize();

                    if (!outputPath.startsWith(outputDirectory)) {
                        throw new IllegalArgumentException("Invalid zip entry path: " + entry.getName());
                    }

                    if (entry.isDirectory()) {
                        Files.createDirectories(outputPath);
                    } else {
                        Files.createDirectories(outputPath.getParent());
                        Files.copy(zipInputStream, outputPath);
                    }

                    zipInputStream.closeEntry();
                }
            }

            return outputDirectory;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to extract corpus artifact: " + artifactZipPath, exception);
        }
    }

    public List<Path> findCorpusRoots(Path extractedDirectory) {
        try (var paths = Files.walk(extractedDirectory)) {
            List<Path> corpusRoots = paths
                .filter(Files::isDirectory)
                .filter(this::containsCorpusTargetDirectories)
                .toList();

            if (corpusRoots.isEmpty()) {
                throw new IllegalArgumentException(
                    "Corpus root directories not found: " + extractedDirectory
                );
            }

            return corpusRoots;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to inspect extracted corpus artifact: " + extractedDirectory, exception);
        }
    }

    private boolean containsCorpusTargetDirectories(Path directory) {
        try (var children = Files.list(directory)) {
            return children
                .filter(Files::isDirectory)
                .anyMatch(this::isCorpusTargetDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to inspect corpus directory: " + directory, exception);
        }
    }

    private boolean isCorpusTargetDirectory(Path directory) {
        return Files.exists(directory.resolve("records.jsonl"));
    }
}
