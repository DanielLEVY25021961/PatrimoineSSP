package levy.daniel.application.persistence.metier.produittype.dao.daosJPA;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.ProduitJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.SousTypeProduitJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.TypeProduitJPA;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE DaoJPATestConfig.java :
 * </p>
 *
 * <p>Uniquement didactique car non utilisé dans ce projet.</p>
 * <p>
 * Configuration Spring de test partagée par les tests directs DAO JPA
 * du package {@code levy.daniel.application.persistence.metier.produittype.dao.daosJPA}.
 * </p>
 *
 * <p style="font-weight:bold;">Pourquoi cette classe existe :</p>
 * <ul>
 * <li>les tests directs {@code TypeProduitDaoJPATest},
 * {@code SousTypeProduitDaoJPATest} et {@code ProduitDaoJPATest}
 * testent tous des repositories situés dans le même package ;</li>
 * <li>une classe interne {@code ConfigTest} propre à chaque test,
 * avec son propre {@link EnableJpaRepositories}, peut rescanner le même
 * package de repositories ;</li>
 * <li>en exécution groupée STS, plusieurs configurations peuvent alors
 * essayer d'enregistrer les mêmes beans Spring Data JPA
 * ({@code TypeProduitDaoJPA}, {@code SousTypeProduitDaoJPA},
 * {@code ProduitDaoJPA}) ;</li>
 * <li>comme l'override de beans est désactivé, Spring refuse à juste titre
 * de démarrer le contexte de test.</li>
 * </ul>
 *
 * <p style="font-weight:bold;">Principe du correctif :</p>
 * <ul>
 * <li>déclarer une seule configuration de test DAO JPA ;</li>
 * <li>faire pointer les trois tests DAO directs vers cette même classe ;</li>
 * <li>scanner une seule fois le package des repositories ;</li>
 * <li>scanner une seule fois le package des entities ;</li>
 * <li>ne pas masquer le problème avec
 * {@code spring.main.allow-bean-definition-overriding=true}.</li>
 * </ul>
 *
 * <p>
 * Le choix de {@code basePackageClasses = TypeProduitDaoJPA.class}
 * est volontairement didactique : Spring Data JPA scanne le package
 * qui contient cette classe. Les repositories {@code TypeProduitDaoJPA},
 * {@code SousTypeProduitDaoJPA} et {@code ProduitDaoJPA} étant dans ce même
 * package, ils sont tous disponibles sans redéclarer plusieurs scans
 * concurrents.
 * </p>
 *
 * <p>
 * Le choix de {@code basePackageClasses = TypeProduitJPA.class} suit
 * la même logique pour les entities : {@link TypeProduitJPA},
 * {@link SousTypeProduitJPA} et {@link ProduitJPA} sont dans le même package
 * d'entities JPA, donc un seul point d'entrée suffit pour exposer le graphe
 * persistant complet.
 * </p>
 *
 * <p>
 * Cette configuration reste strictement limitée aux tests directs DAO JPA.
 * Elle n'importe aucun Gateway et ne porte aucune règle applicative métier.
 * </p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 09 mai 2026
 */
@TestConfiguration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackageClasses = TypeProduitDaoJPA.class)
@EntityScan(basePackageClasses = TypeProduitJPA.class)
public class DaoJPATestConfig {

    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
     * <p>
     * Constructeur public requis par Spring pour instancier cette
     * configuration de test partagée.
     * </p>
     * </div>
     */
    public DaoJPATestConfig() {
        super();
    }

} // FIN DE LA CLASSE DaoJPATestConfig.-----------------------------------