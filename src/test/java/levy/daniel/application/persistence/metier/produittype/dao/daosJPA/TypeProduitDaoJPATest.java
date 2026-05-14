package levy.daniel.application.persistence.metier.produittype.dao.daosJPA;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.TypeProduitJPA;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE TypeProduitDaoJPATest.java :
 * </p>
 *
 * <p>
 * Test JUnit didactique direct du DAO Spring Data JPA
 * {@link TypeProduitDaoJPA}.
 * </p>
 *
 * <p>Ce test ne passe volontairement par aucun Gateway.</p>
 * <ul>
 * <li>Il injecte directement le repository {@link TypeProduitDaoJPA}.</li>
 * <li>Il utilise un stockage H2 en mémoire via {@link DataJpaTest}.</li>
 * <li>Il initialise le stockage avec
 * <code>truncate-test.sql</code> puis <code>data-test.sql</code>.</li>
 * <li>Il relit certaines références directement en SQL avec
 * {@link JdbcTemplate}, afin de comparer le résultat DAO avec une preuve
 * indépendante du repository testé.</li>
 * </ul>
 *
 * <p style="font-weight:bold;">Objectif didactique :</p>
 * <ul>
 * <li>montrer comment tester directement une méthode dérivée Spring Data
 * <code>findBy...IgnoreCase(...)</code> ;</li>
 * <li>montrer comment tester directement une méthode dérivée Spring Data
 * <code>findBy...ContainingIgnoreCase(...)</code> ;</li>
 * <li>séparer clairement le comportement technique du DAO des règles
 * applicatives portées par les services Gateway ;</li>
 * <li>prouver que le DAO lit le stockage sans modifier les données seedées.</li>
 * </ul>
 *
 * <p style="font-weight:bold;">Contexte DAO slice :</p>
 * <ul>
 * <li>{@link DataJpaTest} démarre un contexte Spring réduit (slice), chargé
 * uniquement pour tester la couche JPA / repository / DAO ;</li>
 * <li>ce contexte réduit évite de démarrer toute l'application ;</li>
 * <li>il ne charge volontairement pas les Gateway, Controllers,
 * services applicatifs ni leurs configurations d'intégration ;</li>
 * <li>il laisse Spring Boot configurer la tranche JPA utile au test :
 * repositories Spring Data JPA, entities JPA, transactions,
 * stockage de test, {@link JdbcTemplate} et infrastructure JPA ;</li>
 * <li>le test conserve donc le slice JPA, mais fournit une configuration
 * Spring locale minimale pour être autonome dans STS.</li>
 * </ul>
 *
 * <p style="font-weight:bold;">Configuration autonome du test :</p>
 * <ul>
 * <li>ce test déclare une classe interne {@link ConfigTest}
 * explicitement chargée par {@link ContextConfiguration} ;</li>
 * <li>cette configuration locale fournit le point d'entrée
 * {@link SpringBootConfiguration} que Spring Boot ne trouvait pas
 * automatiquement en remontant les packages ;</li>
 * <li>elle indique à l'auto-configuration Spring Boot, via
 * {@link AutoConfigurationPackage}, le package du DAO
 * {@link TypeProduitDaoJPA} et le package de l'entity
 * {@link TypeProduitJPA} ;</li>
 * <li>elle ne déclare ni {@code @EnableJpaRepositories}
 * ni {@code @EntityScan} ;</li>
 * <li>elle ne scanne pas explicitement les repositories et n'autorise pas
 * l'override des beans ;</li>
 * <li>elle permet donc au test de rester autonome tout en évitant les
 * collisions de beans observées avec les configurations repository
 * explicites.</li>
 * </ul>
 *
 * <p>
 * Ce fichier est un exemple pédagogique pour les tests directs DAO.
 * Il ne remplace pas les tests Gateway validés du package
 * <code>src/test/java/levy/daniel/application/model/services/produittype/gateway/impl</code>.
 * </p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 09 mai 2026
 */
@SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
@Sql(
    scripts = {
            TypeProduitDaoJPATest.CLASSPATH_TRUNCATE_SQL,
            TypeProduitDaoJPATest.CLASSPATH_DATA_SQL
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@DataJpaTest
@ActiveProfiles({ "test-jpa" })
@ContextConfiguration(classes = TypeProduitDaoJPATest.ConfigTest.class)
public class TypeProduitDaoJPATest {

    // ************************* CONSTANTES ******************************/

    /** "classpath:truncate-test.sql" */
    public static final String CLASSPATH_TRUNCATE_SQL
        = "classpath:truncate-test.sql";

    /** "classpath:data-test.sql" */
    public static final String CLASSPATH_DATA_SQL
        = "classpath:data-test.sql";

    /** "TypeProduitDaoJPA" */
    public static final String QUALIFIER_DAO
        = "TypeProduitDaoJPA";

    /** "dao-TypeProduit" */
    public static final String TAG_DAO_TYPE_PRODUIT
        = "dao-TypeProduit";

    /** "vêtement" */
    public static final String VETEMENT = "vêtement";

    /** "bazar" */
    public static final String BAZAR = "bazar";

    /** "tourisme" */
    public static final String TOURISME = "tourisme";

    /** "###___introuvable___###" */
    public static final String INTROUVABLE = "###___introuvable___###";

    /** "me" */
    public static final String CONTENU_PARTIEL_ME = "me";

    /** "findByTypeProduitIgnoreCase(OK) - retrouve le libellé exact sans tenir compte de la casse" */
    public static final String DN_FIND_BY_TYPE_PRODUIT_IGNORE_CASE_OK
        = "findByTypeProduitIgnoreCase(OK) - retrouve le libellé exact sans tenir compte de la casse";

    /** "findByTypeProduitIgnoreCase(non trouvé) - retourne null" */
    public static final String DN_FIND_BY_TYPE_PRODUIT_IGNORE_CASE_NON_TROUVE
        = "findByTypeProduitIgnoreCase(non trouvé) - retourne null";

    /** "findByTypeProduitContainingIgnoreCase(OK) - retrouve les libellés contenant le motif sans tenir compte de la casse" */
    public static final String DN_FIND_BY_TYPE_PRODUIT_CONTAINING_IGNORE_CASE_OK
        = "findByTypeProduitContainingIgnoreCase(OK) - retrouve les libellés contenant le motif sans tenir compte de la casse";

    /** "findByTypeProduitContainingIgnoreCase(non trouvé) - retourne une liste vide non null" */
    public static final String DN_FIND_BY_TYPE_PRODUIT_CONTAINING_IGNORE_CASE_NON_TROUVE
        = "findByTypeProduitContainingIgnoreCase(non trouvé) - retourne une liste vide non null";

    /** "SELECT COUNT(*) FROM TYPES_PRODUIT" */
    public static final String SELECT_COUNT_FROM_TYPES_PRODUIT
        = "SELECT COUNT(*) FROM TYPES_PRODUIT";

    /** "SELECT COUNT(*) FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) = UPPER(?)" */
    public static final String SELECT_COUNT_FROM_TYPES_PRODUIT_WHERE_LIBELLE
        = "SELECT COUNT(*) FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) = UPPER(?)";

    /** "SELECT ID_TYPE_PRODUIT FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) = UPPER(?)" */
    public static final String SELECT_ID_FROM_TYPES_PRODUIT_WHERE_LIBELLE
        = "SELECT ID_TYPE_PRODUIT FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) = UPPER(?)";

    /** "SELECT TYPE_PRODUIT FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) LIKE UPPER(?)" */
    public static final String SELECT_LIBELLES_FROM_TYPES_PRODUIT_WHERE_LIKE
        = "SELECT TYPE_PRODUIT FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) LIKE UPPER(?)";

    // *************************** ATTRIBUTS *****************************/

    /**
     * <div>
     * <p>Locale neutre utilisée pour les transformations de casse
     * déterministes.</p>
     * <p>
     * Déterministe signifie ici : à entrée identique, résultat identique,
     * quel que soit le poste, la JVM, la langue système ou la configuration
     * locale de l'environnement d'exécution.
     * </p>
     * <ul>
     * <li>{@code Locale.getDefault()} utilise la locale courante de la JVM :
     * poste développeur, CI, configuration système, options JVM, etc.
     * Il peut donc varier selon l'environnement et ne doit être utilisé que
     * si le test veut volontairement dépendre de la locale utilisateur/JVM.</li>
     * <li>{@code Locale.ROOT} est une locale neutre, stable, non liée
     * à un pays ou à une langue utilisateur. Elle est adaptée aux
     * transformations techniques, aux tests déterministes et aux
     * comparaisons case-insensitive indépendantes de la langue utilisateur.</li>
     * </ul>
     * </div>
     */
    public static final Locale LOCALE_ROOT = Locale.ROOT;

    /**
     * <div>
     * <p>DAO Spring Data JPA testé directement.</p>
     * <p>
     * Ce test injecte le repository lui-même, sans passer par
     * <code>TypeProduitGatewayJPAService</code>, afin de vérifier
     * uniquement le comportement technique des méthodes dérivées Spring Data.
     * </p>
     * <p>
     * L'injection est fournie par le contexte DAO slice de
     * {@link DataJpaTest}. La configuration locale {@link ConfigTest}
     * fournit uniquement le point d'entrée Spring Boot nécessaire
     * au démarrage autonome du test.
     * </p>
     * </div>
     */
    @Autowired
    @Qualifier(QUALIFIER_DAO)
    private TypeProduitDaoJPA typeProduitDaoJPA;

    /**
     * <div>
     * <p>JdbcTemplate utilisé pour relire directement le stockage.</p>
     * <p>
     * Les requêtes SQL directes servent de preuve indépendante :
     * elles permettent de comparer le résultat du DAO avec les lignes
     * réellement seedées par <code>data-test.sql</code>.
     * </p>
     * <p>
     * {@link JdbcTemplate} est également fourni par le contexte DAO slice
     * de {@link DataJpaTest}. Il permet de contrôler le stockage sans passer
     * par le repository testé.
     * </p>
     * </div>
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ************************ CONSTRUCTEUR *****************************/

    
    
    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
     * </div>
     */
    public TypeProduitDaoJPATest() {
        super();
    }



    // ===================== CONFIGURATION SPRING =======================//

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * Classe interne de configuration Spring du test direct DAO.
     * </p>
     *
     * <p>
     * Cette classe rend le test autonome : elle fournit au bootstrap Spring
     * Boot une configuration locale explicite, au lieu de dépendre d'une
     * configuration applicative située ailleurs dans le projet.
     * </p>
     *
     * <p style="font-weight:bold;">Pourquoi cette configuration est nécessaire :</p>
     * <ul>
     * <li>{@link DataJpaTest} conserve le bon modèle de test : un contexte
     * DAO slice, c'est-à-dire un contexte Spring réduit chargé uniquement
     * pour tester la couche JPA / repository / DAO ;</li>
     * <li>dans ce projet, {@link DataJpaTest} ne trouve pas tout seul une
     * classe racine {@link SpringBootConfiguration} en remontant les packages
     * depuis ce test ;</li>
     * <li>{@link ContextConfiguration} charge donc explicitement cette classe
     * interne locale ;</li>
     * <li>{@link SpringBootConfiguration} fournit le point d'entrée attendu
     * par Spring Boot ;</li>
     * <li>{@link AutoConfigurationPackage} indique explicitement à
     * l'auto-configuration Spring Boot les packages nécessaires au test :
     * le package du DAO {@link TypeProduitDaoJPA} et le package de l'entity
     * {@link TypeProduitJPA} ;</li>
     * <li>le test ne déclare pas {@code @EnableJpaRepositories} :
     * les repositories restent pris en charge par le slice
     * {@link DataJpaTest} ;</li>
     * <li>le test ne déclare pas {@code @EntityScan} :
     * le package de l'entity JPA est fourni par
     * {@link AutoConfigurationPackage} avec {@link TypeProduitJPA}.</li>
     * </ul>
     *
     * <p style="font-weight:bold;">Ce que cette configuration ne fait pas :</p>
     * <ul>
     * <li>elle ne déclare pas {@code @EnableJpaRepositories} ;</li>
     * <li>elle ne déclare pas {@code @EntityScan} ;</li>
     * <li>elle ne force aucun scan manuel des repositories ;</li>
     * <li>elle ne charge aucun Gateway ;</li>
     * <li>elle ne charge aucun Controller ;</li>
     * <li>elle ne charge aucun service applicatif ;</li>
     * <li>elle n'active pas
     * {@code spring.main.allow-bean-definition-overriding=true}.</li>
     * </ul>
     *
     * <p>
     * Le repository testé et l'entity JPA sont découverts dans le périmètre
     * explicite déclaré pour ce test. Le test reste donc un test direct DAO,
     * autonome, et limité à la persistance JPA.
     * </p>
     * </div>
     *
     * @author Daniel Lévy
     * @version 1.0
     * @since 09 mai 2026
     */
    @SpringBootConfiguration(proxyBeanMethods = false)
    @AutoConfigurationPackage(basePackageClasses = {
            TypeProduitDaoJPA.class,
            TypeProduitJPA.class
    })
    public static final class ConfigTest { // NOPMD by danyl on 09/05/2026 17:33

        /**
         * <div>
         * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
         * </div>
         */
        public ConfigTest() {
            super();
        }

    } // FIN DE LA CLASSE INTERNE ConfigTest.------------------------------



    // ==================== findByTypeProduitIgnoreCase ==================//

    
    
    /**
     * <div>
     * <p>garantit que {@link TypeProduitDaoJPA#findByTypeProduitIgnoreCase(String)}
     * avec un libellé existant fourni dans une casse différente :</p>
     * <ul>
     * <li>retourne une entity {@link TypeProduitJPA} non null ;</li>
     * <li>retrouve la ligne seedée portant le libellé exact attendu ;</li>
     * <li>ignore la casse du paramètre fourni ;</li>
     * <li>retourne l'identifiant réellement présent dans le stockage ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DAO_TYPE_PRODUIT)
    @DisplayName(DN_FIND_BY_TYPE_PRODUIT_IGNORE_CASE_OK)
    @Test
    public void testFindByTypeProduitIgnoreCaseTrouve() throws Exception {

        /* ARRANGE :
         * compte d'abord le nombre total de lignes dans le stockage.
         *
         * Comme la méthode DAO testée est une méthode de lecture,
         * ce compteur permettra de prouver en fin de test
         * que l'appel n'a pas modifié les données seedées.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        assertThat(countAvant).isNotNull().isPositive();

        /*
         * Vérifie par SQL direct que le libellé de référence existe
         * exactement une fois dans le stockage, sans tenir compte
         * des majuscules/minuscules.
         */
        final Long countLibelle = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT_WHERE_LIBELLE,
                Long.class,
                VETEMENT);

        assertThat(countLibelle).isNotNull().isEqualTo(1L);

        /*
         * Relit l'identifiant technique réellement stocké
         * pour le libellé attendu.
         *
         * Cette valeur servira de preuve indépendante pour contrôler
         * l'entity retournée par le DAO.
         */
        final Long idStockage = this.jdbcTemplate.queryForObject(
                SELECT_ID_FROM_TYPES_PRODUIT_WHERE_LIBELLE,
                Long.class,
                VETEMENT);

        assertThat(idStockage).isNotNull();

        /*
         * Prépare le paramètre DAO avec une casse différente.
         *
         * Le libellé seedé est "vêtement".
         * Le paramètre envoyé au DAO est volontairement fourni
         * en majuscules afin de tester directement le suffixe
         * Spring Data IgnoreCase.
         */
        final String libelleRecherche =
                VETEMENT.toUpperCase(LOCALE_ROOT);

        /* ACT :
         * appelle directement le DAO, sans passer par le Gateway.
         */
        final TypeProduitJPA resultat =
                this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(
                        libelleRecherche);

        /* ASSERT :
         * vérifie que le DAO retourne bien une entity JPA exploitable.
         */
        assertThat(resultat).isNotNull();

        /*
         * Vérifie que l'entity retournée correspond exactement
         * à la ligne SQL attendue :
         * - même identifiant technique ;
         * - même libellé persistant.
         */
        assertThat(resultat.getIdTypeProduit()).isEqualTo(idStockage);
        assertThat(resultat.getTypeProduit()).isEqualTo(VETEMENT);

        /*
         * Compte finalement le stockage pour prouver que la méthode DAO
         * de lecture n'a pas ajouté, supprimé ni modifié de ligne.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que {@link TypeProduitDaoJPA#findByTypeProduitIgnoreCase(String)}
     * avec un libellé absent :</p>
     * <ul>
     * <li>reste cohérent avec l'absence de ligne dans le stockage ;</li>
     * <li>retourne {@code null}, conformément au comportement Spring Data
     * attendu pour cette signature mono-résultat ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DAO_TYPE_PRODUIT)
    @DisplayName(DN_FIND_BY_TYPE_PRODUIT_IGNORE_CASE_NON_TROUVE)
    @Test
    public void testFindByTypeProduitIgnoreCaseNonTrouve() throws Exception {

        /* ARRANGE :
         * compte d'abord le stockage pour vérifier ensuite
         * que l'appel de lecture ne modifie aucune donnée seedée.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        assertThat(countAvant).isNotNull().isPositive();

        /*
         * Vérifie par SQL direct qu'aucune ligne ne porte
         * le libellé volontairement introuvable.
         */
        final Long countLibelle = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT_WHERE_LIBELLE,
                Long.class,
                INTROUVABLE);

        assertThat(countLibelle).isNotNull().isZero();

        /* ACT :
         * appelle directement le DAO avec un libellé absent du stockage.
         */
        final TypeProduitJPA resultat =
                this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(
                        INTROUVABLE);

        /* ASSERT :
         * vérifie que la signature mono-résultat retourne null
         * lorsque le stockage ne contient aucune ligne correspondante.
         */
        assertThat(resultat).isNull();

        /*
         * Vérifie enfin que cette lecture infructueuse
         * n'a pas altéré le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isEqualTo(countAvant);

    } // __________________________________________________________________



    // ============ findByTypeProduitContainingIgnoreCase =================//

    
    
    /**
     * <div>
     * <p>garantit que
     * {@link TypeProduitDaoJPA#findByTypeProduitContainingIgnoreCase(String)}
     * avec un contenu partiel présent :</p>
     * <ul>
     * <li>retourne une liste non null et non vide ;</li>
     * <li>retrouve les libellés contenant le motif demandé ;</li>
     * <li>ignore la casse du motif fourni ;</li>
     * <li>retourne le même ensemble que la requête SQL de référence ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DAO_TYPE_PRODUIT)
    @DisplayName(DN_FIND_BY_TYPE_PRODUIT_CONTAINING_IGNORE_CASE_OK)
    @Test
    public void testFindByTypeProduitContainingIgnoreCaseTrouve()
            throws Exception {

        /* ARRANGE :
         * compte d'abord le stockage pour prouver ensuite
         * que la recherche partielle DAO reste une lecture pure.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        assertThat(countAvant).isNotNull().isPositive();

        /*
         * Lit par SQL direct les libellés réellement présents
         * qui contiennent le motif demandé, sans tenir compte
         * des majuscules/minuscules.
         *
         * Cette requête SQL constitue la référence indépendante
         * du repository testé.
         */
        final List<String> libellesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_LIBELLES_FROM_TYPES_PRODUIT_WHERE_LIKE,
                        String.class,
                        "%" + CONTENU_PARTIEL_ME + "%");

        assertThat(libellesStockage).isNotNull().isNotEmpty();

        /*
         * Prépare le motif avec une casse différente.
         *
         * Le contenu seedé contient le motif "me".
         * Le paramètre envoyé au DAO est fourni en majuscules
         * afin de tester directement le suffixe Spring Data IgnoreCase.
         */
        final String contenuRecherche =
                CONTENU_PARTIEL_ME.toUpperCase(LOCALE_ROOT);

        /* ACT :
         * appelle directement le DAO avec un motif partiel.
         */
        final List<TypeProduitJPA> resultats =
                this.typeProduitDaoJPA.findByTypeProduitContainingIgnoreCase(
                        contenuRecherche);

        /* ASSERT :
         * vérifie d'abord que Spring Data retourne une liste exploitable.
         */
        assertThat(resultats).isNotNull().isNotEmpty();

        /*
         * Extrait les libellés persistants retournés par le DAO.
         */
        final List<String> libellesResultats = resultats.stream()
                .map(TypeProduitJPA::getTypeProduit)
                .toList();

        /*
         * Compare l'ensemble retourné par le DAO avec la référence SQL.
         *
         * On utilise containsExactlyInAnyOrderElementsOf(...)
         * car cette méthode DAO ne porte aucune clause de tri explicite :
         * le test contrôle ici le contenu, pas l'ordre.
         */
        assertThat(libellesResultats)
            .containsExactlyInAnyOrderElementsOf(libellesStockage);

        /*
         * Vérifie explicitement que tous les libellés retournés
         * contiennent bien le motif demandé, sans tenir compte
         * de la casse.
         */
        assertThat(libellesResultats)
            .allSatisfy(libelle -> assertThat(libelle)
                    .isNotNull()
                    .containsIgnoringCase(CONTENU_PARTIEL_ME));

        /*
         * Vérifie aussi que le jeu seedé attendu pour ce motif
         * contient bien les exemples lisibles du développeur humain.
         */
        assertThat(libellesResultats)
            .contains(VETEMENT, TOURISME);

        /*
         * Compte finalement le stockage pour prouver que la recherche
         * partielle n'a modifié aucune donnée seedée.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que
     * {@link TypeProduitDaoJPA#findByTypeProduitContainingIgnoreCase(String)}
     * avec un contenu partiel absent :</p>
     * <ul>
     * <li>reste cohérent avec l'absence de correspondance dans le stockage ;</li>
     * <li>retourne une liste non null ;</li>
     * <li>retourne une liste vide, conformément au comportement Spring Data
     * attendu pour cette signature liste ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DAO_TYPE_PRODUIT)
    @DisplayName(DN_FIND_BY_TYPE_PRODUIT_CONTAINING_IGNORE_CASE_NON_TROUVE)
    @Test
    public void testFindByTypeProduitContainingIgnoreCaseNonTrouve()
            throws Exception {

        /* ARRANGE :
         * compte d'abord le stockage pour vérifier ensuite
         * que la recherche sans résultat ne modifie aucune donnée seedée.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        assertThat(countAvant).isNotNull().isPositive();

        /*
         * Vérifie par SQL direct qu'aucun libellé seedé
         * ne contient le motif volontairement introuvable.
         */
        final List<String> libellesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_LIBELLES_FROM_TYPES_PRODUIT_WHERE_LIKE,
                        String.class,
                        "%" + INTROUVABLE + "%");

        assertThat(libellesStockage).isNotNull().isEmpty();

        /* ACT :
         * appelle directement le DAO avec un motif absent du stockage.
         */
        final List<TypeProduitJPA> resultats =
                this.typeProduitDaoJPA.findByTypeProduitContainingIgnoreCase(
                        INTROUVABLE);

        /* ASSERT :
         * vérifie que la signature liste ne retourne pas null,
         * mais une liste vide.
         */
        assertThat(resultats).isNotNull().isEmpty();

        /*
         * Vérifie enfin que cette recherche infructueuse
         * n'a pas altéré le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isEqualTo(countAvant);

    } // __________________________________________________________________
    
    

} // FIN DE LA CLASSE TypeProduitDaoJPATest.-------------------------------