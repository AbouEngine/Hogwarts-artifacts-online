package org.example.hogwartsartifactsonline.artifact;

import jakarta.transaction.Transactional;
import org.example.hogwartsartifactsonline.artifact.exception.ArtifcatNotFoundException;
import org.example.hogwartsartifactsonline.artifact.utils.IdWorker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ArtifactService {
    private final ArtifactRepository artifactRepository;

    private final IdWorker idWorker;

    public ArtifactService(ArtifactRepository artifactRepository, IdWorker idWorker) {
        this.artifactRepository = artifactRepository;
        this.idWorker = idWorker;
    }

    public Artifact findById(String artifactId) {
        return artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ArtifcatNotFoundException(artifactId));
    }

    public List<Artifact> findAll() {
        return artifactRepository.findAll();
    }

    public Artifact save(Artifact artifact) {
        artifact.setId(idWorker.nextId() + "");
        return this.artifactRepository.save(artifact);
    }

    public Artifact update(String artifactId, Artifact update) {
        return this.artifactRepository.findById(artifactId)
                .map(oldArtifact -> {
                    oldArtifact.setName(update.getName());
                    oldArtifact.setDescription(update.getDescription());
                    oldArtifact.setImageUrl(update.getImageUrl());
                    return this.artifactRepository.save(oldArtifact);
                })
                .orElseThrow(() -> new ArtifcatNotFoundException(artifactId));
    }

    public void delete(String artifactId) {
        this.artifactRepository.findById(artifactId).orElseThrow(() -> new ArtifcatNotFoundException(artifactId));
        this.artifactRepository.deleteById(artifactId);

    }
}
