package org.example.hogwartsartifactsonline.system;

import org.example.hogwartsartifactsonline.artifact.Artifact;
import org.example.hogwartsartifactsonline.artifact.ArtifactRepository;
import org.example.hogwartsartifactsonline.hogwartsUser.HogwartsUser;
import org.example.hogwartsartifactsonline.hogwartsUser.UserRepository;
import org.example.hogwartsartifactsonline.wizard.Wizard;
import org.example.hogwartsartifactsonline.wizard.WizardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DBDataInitializer implements CommandLineRunner {
    private final ArtifactRepository artifactRepository;
    private final WizardRepository wizardRepository;
    private final UserRepository userRepository;

    public DBDataInitializer(ArtifactRepository artifactRepository, WizardRepository wizardRepository, UserRepository userRepository) {
        this.artifactRepository = artifactRepository;
        this.wizardRepository = wizardRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Artifact a1 = new Artifact();
        a1.setId("1");
        a1.setName("Deliminator");
        a1.setDescription("Deliminator description");
        a1.setImageUrl("ImageUrl");

        Artifact a2 = new Artifact();
        a2.setId("2");
        a2.setName("Invisibility");
        a2.setDescription("Invisibility description");
        a2.setImageUrl("ImageUrl");

        Artifact a3 = new Artifact();
        a3.setId("3");
        a3.setName("Shadow");
        a3.setDescription("Deliminator description");
        a3.setImageUrl("ImageUrl");

        Artifact a4 = new Artifact();
        a4.setId("4");
        a4.setName("Water Boom");
        a4.setDescription("Boom description");
        a4.setImageUrl("ImageUrl");

        Artifact a5 = new Artifact();
        a5.setId("5");
        a5.setName("Fire style");
        a5.setDescription("Boom description");
        a5.setImageUrl("ImageUrl");

        Artifact a6 = new Artifact();
        a6.setId("6");
        a6.setName("Speed");
        a6.setDescription("Boom description");
        a6.setImageUrl("ImageUrl");

        Wizard wizard1 = new Wizard();
        wizard1.setId(1);
        wizard1.setName("Harry Potter");
        wizard1.addArtifact(a1);
        wizard1.addArtifact(a3);

        Wizard wizard2 = new Wizard();
        wizard2.setId(2);
        wizard2.setName("Albus Dumbledore");
        wizard2.addArtifact(a2);
        wizard2.addArtifact(a4);

        Wizard wizard3 = new Wizard();
        wizard3.setId(3);
        wizard3.setName("Malefoy Drago");
        wizard3.addArtifact(a5);

        HogwartsUser user1 = new HogwartsUser();
        user1.setUserId(1);
        user1.setUsername("Eren");
        user1.setPassword("123456");
        user1.setEnabled(true);
        user1.setRoles("admin user");

        HogwartsUser user2 = new HogwartsUser();
        user2.setUserId(2);
        user2.setUsername("Mikasa");
        user2.setPassword("654321");
        user2.setEnabled(true);
        user2.setRoles("user");

        HogwartsUser user3 = new HogwartsUser();
        user3.setUserId(3);
        user3.setUsername("Armin");
        user3.setPassword("blablabla");
        user3.setEnabled(true);
        user3.setRoles("user");

        /* Save objets into repository.
           With cascade PERSIST and Merge we can save wizard and its artifacts are saved as well
         */
        wizardRepository.save(wizard1);
        wizardRepository.save(wizard2);
        wizardRepository.save(wizard3);

        artifactRepository.save(a6);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
    }
}
