package org.example.hogwartsartifactsonline.artifact;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.example.hogwartsartifactsonline.wizard.Wizard;

import java.io.Serializable;
@Entity
public class Artifact implements Serializable {
    @Id
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    // artifact belongs to a wizard
    @ManyToOne
    private Wizard owner;

    public Artifact() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Wizard getOwner() {
        return owner;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setOwner(Wizard owner) {
        this.owner = owner;
    }
}
