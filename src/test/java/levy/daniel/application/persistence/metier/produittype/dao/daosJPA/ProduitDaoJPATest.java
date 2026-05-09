package levy.daniel.application.persistence.metier.produittype.dao.daosJPA;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.ProduitJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.SousTypeProduitJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.TypeProduitJPA;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE ProduitDaoJPATest.java :
 * </p>
 *
 * <p>
 * Test JUnit didactique direct du DAO Spring Data JPA
 * {@link ProduitDaoJPA}.
 * </p>
 *
 * <p>Ce test ne passe volontairement par aucun Gateway.</p>
 * <ul>
 * <li>Il injecte directement le repository {@link ProduitDaoJPA}.</li>
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
 * <code>findBy...IgnoreCase(...)</code> qui retourne une liste ;</li>
 * <li>montrer comment tester directement une méthode dérivée Spring Data
 * <code>findBy...ContainingIgnoreCase(...)</code> ;</li>
 * <li>montrer comment tester directement une méthode dérivée Spring Data
 * qui recherche les enfants d'un parent
 * <code>findAllBySousTypeProduit(...)</code> ;</li>
 * <li>montrer que, pour {@link ProduitJPA}, le libellé seul
 * n'est pas la clé métier complète : le parent
 * {@link SousTypeProduitJPA} fait partie du graphe à contrôler ;</li>
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
 * <li>elle indique à l'auto-configuration Spring Boot le package des DAO
 * avec {@link AutoConfigurationPackage}, sans déclarer
 * {@code @EnableJpaRepositories} ;</li>
 * <li>elle indique le package des entities JPA avec {@link EntityScan} ;</li>
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
            ProduitDaoJPATest.CLASSPATH_TRUNCATE_SQL,
            ProduitDaoJPATest.CLASSPATH_DATA_SQL
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@DataJpaTest
@ActiveProfiles({ "test-jpa" })
@ContextConfiguration(classes = ProduitDaoJPATest.ConfigTest.class)
public class ProduitDaoJPATest {

    // ************************* CONSTANTES ******************************/

    /** "classpath:truncate-test.sql" */
    public static final String CLASSPATH_TRUNCATE_SQL
        = "classpath:truncate-test.sql";

    /** "classpath:data-test.sql" */
    public static final String CLASSPATH_DATA_SQL
        = "classpath:data-test.sql";

    /** "ProduitDaoJPA" */
    public static final String QUALIFIER_DAO
        = "ProduitDaoJPA";

    /** "dao-Produit" */
    public static final String TAG_DAO_PRODUIT
        = "dao-Produit";

    /** "vêtement" */
    public static final String VETEMENT = "vêtement";

    /** "vêtement pour homme" */
    public static final String VETEMENT_HOMME = "vêtement pour homme";

    /** "chemise à manches longues pour homme" */
    public static final String CHEMISE_ML_HOMME
        = "chemise à manches longues pour homme";

    /** "chemise à manches courtes pour homme" */
    public static final String CHEMISE_MC_HOMME
        = "chemise à manches courtes pour homme";

    /** "sweatshirt pour homme" */
    public static final String SWEATSHIRT_HOMME
        = "sweatshirt pour homme";

    /** "teeshirt pour homme" */
    public static final String TEESHIRT_HOMME
        = "teeshirt pour homme";

    /** "chemise" */
    public static final String CONTENU_PARTIEL_CHEMISE = "chemise";

    /** "###___introuvable___###" */
    public static final String INTROUVABLE = "###___introuvable___###";

    /** 999_999L */
    public static final Long ID_INEXISTANT = Long.valueOf(999_999L);

    /** "ID_PRODUIT" */
    public static final String ID_PRODUIT = "ID_PRODUIT";

    /** "PRODUIT" */
    public static final String PRODUIT = "PRODUIT";

    /** "ID_SOUS_TYPE_PRODUIT" */
    public static final String ID_SOUS_TYPE_PRODUIT = "ID_SOUS_TYPE_PRODUIT";

    /** "SOUS_TYPE_PRODUIT" */
    public static final String SOUS_TYPE_PRODUIT = "SOUS_TYPE_PRODUIT";

    /** "ID_TYPE_PRODUIT" */
    public static final String ID_TYPE_PRODUIT = "ID_TYPE_PRODUIT";

    /** "TYPE_PRODUIT" */
    public static final String TYPE_PRODUIT = "TYPE_PRODUIT";

    /** "findByProduitIgnoreCase(OK) - retrouve le libellé exact sans tenir compte de la casse" */
    public static final String DN_FIND_BY_PRODUIT_IGNORE_CASE_OK
        = "findByProduitIgnoreCase(OK) - retrouve le libellé exact sans tenir compte de la casse";

    /** "findByProduitIgnoreCase(non trouvé) - retourne une liste vide non null" */
    public static final String DN_FIND_BY_PRODUIT_IGNORE_CASE_NON_TROUVE
        = "findByProduitIgnoreCase(non trouvé) - retourne une liste vide non null";

    /** "findByProduitContainingIgnoreCase(OK) - retrouve les libellés contenant le motif sans tenir compte de la casse" */
    public static final String DN_FIND_BY_PRODUIT_CONTAINING_IGNORE_CASE_OK
        = "findByProduitContainingIgnoreCase(OK) - retrouve les libellés contenant le motif sans tenir compte de la casse";

    /** "findByProduitContainingIgnoreCase(non trouvé) - retourne une liste vide non null" */
    public static final String DN_FIND_BY_PRODUIT_CONTAINING_IGNORE_CASE_NON_TROUVE
        = "findByProduitContainingIgnoreCase(non trouvé) - retourne une liste vide non null";

    /** "findAllBySousTypeProduit(OK) - retrouve les produits rattachés au parent demandé" */
    public static final String DN_FIND_ALL_BY_SOUS_TYPE_PRODUIT_OK
        = "findAllBySousTypeProduit(OK) - retrouve les produits rattachés au parent demandé";

    /** "findAllBySousTypeProduit(parent absent) - retourne une liste vide non null" */
    public static final String DN_FIND_ALL_BY_SOUS_TYPE_PRODUIT_PARENT_ABSENT
        = "findAllBySousTypeProduit(parent absent) - retourne une liste vide non null";

    /** "SELECT COUNT(*) FROM PRODUITS" */
    public static final String SELECT_COUNT_FROM_PRODUITS
        = "SELECT COUNT(*) FROM PRODUITS";

    /** "SELECT COUNT(*) FROM PRODUITS WHERE UPPER(PRODUIT) = UPPER(?)" */
    public static final String SELECT_COUNT_FROM_PRODUITS_WHERE_LIBELLE
        = "SELECT COUNT(*) FROM PRODUITS WHERE UPPER(PRODUIT) = UPPER(?)";

    /** "SELECT COUNT(*) FROM PRODUITS WHERE UPPER(PRODUIT) LIKE UPPER(?)" */
    public static final String SELECT_COUNT_FROM_PRODUITS_WHERE_LIKE
        = "SELECT COUNT(*) FROM PRODUITS WHERE UPPER(PRODUIT) LIKE UPPER(?)";

    /** "SELECT ID_TYPE_PRODUIT FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) = UPPER(?)" */
    public static final String SELECT_ID_FROM_TYPES_PRODUIT_WHERE_LIBELLE
        = "SELECT ID_TYPE_PRODUIT FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) = UPPER(?)";

    /** "SELECT ID_SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE UPPER(SOUS_TYPE_PRODUIT) = UPPER(?)" */
    public static final String SELECT_ID_FROM_SOUS_TYPES_PRODUIT_WHERE_LIBELLE
        = "SELECT ID_SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE UPPER(SOUS_TYPE_PRODUIT) = UPPER(?)";

    /** "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?" */
    public static final String SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT_WHERE_ID
        = "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?";

    /** "SELECT COUNT(*) FROM PRODUITS WHERE SOUS_TYPE_PRODUIT = ?" */
    public static final String SELECT_COUNT_FROM_PRODUITS_WHERE_PARENT
        = "SELECT COUNT(*) FROM PRODUITS WHERE SOUS_TYPE_PRODUIT = ?";

    /**
     * "SELECT P.ID_PRODUIT AS ID_PRODUIT,
     * P.PRODUIT AS PRODUIT,
     * STP.ID_SOUS_TYPE_PRODUIT AS ID_SOUS_TYPE_PRODUIT,
     * STP.SOUS_TYPE_PRODUIT AS SOUS_TYPE_PRODUIT,
     * TP.ID_TYPE_PRODUIT AS ID_TYPE_PRODUIT,
     * TP.TYPE_PRODUIT AS TYPE_PRODUIT
     * FROM PRODUITS P
     * JOIN SOUS_TYPES_PRODUIT STP ON P.SOUS_TYPE_PRODUIT = STP.ID_SOUS_TYPE_PRODUIT
     * JOIN TYPES_PRODUIT TP ON STP.TYPE_PRODUIT = TP.ID_TYPE_PRODUIT
     * WHERE UPPER(P.PRODUIT) = UPPER(?)"
     */
    public static final String SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE
        = """
          SELECT
              P.ID_PRODUIT AS ID_PRODUIT,
              P.PRODUIT AS PRODUIT,
              STP.ID_SOUS_TYPE_PRODUIT AS ID_SOUS_TYPE_PRODUIT,
              STP.SOUS_TYPE_PRODUIT AS SOUS_TYPE_PRODUIT,
              TP.ID_TYPE_PRODUIT AS ID_TYPE_PRODUIT,
              TP.TYPE_PRODUIT AS TYPE_PRODUIT
          FROM PRODUITS P
          JOIN SOUS_TYPES_PRODUIT STP
              ON P.SOUS_TYPE_PRODUIT = STP.ID_SOUS_TYPE_PRODUIT
          JOIN TYPES_PRODUIT TP
              ON STP.TYPE_PRODUIT = TP.ID_TYPE_PRODUIT
          WHERE UPPER(P.PRODUIT) = UPPER(?)
          """;

    /**
     * "SELECT P.ID_PRODUIT AS ID_PRODUIT,
     * P.PRODUIT AS PRODUIT,
     * STP.ID_SOUS_TYPE_PRODUIT AS ID_SOUS_TYPE_PRODUIT,
     * STP.SOUS_TYPE_PRODUIT AS SOUS_TYPE_PRODUIT,
     * TP.ID_TYPE_PRODUIT AS ID_TYPE_PRODUIT,
     * TP.TYPE_PRODUIT AS TYPE_PRODUIT
     * FROM PRODUITS P
     * JOIN SOUS_TYPES_PRODUIT STP ON P.SOUS_TYPE_PRODUIT = STP.ID_SOUS_TYPE_PRODUIT
     * JOIN TYPES_PRODUIT TP ON STP.TYPE_PRODUIT = TP.ID_TYPE_PRODUIT
     * WHERE UPPER(P.PRODUIT) LIKE UPPER(?)"
     */
    public static final String SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIKE
        = """
          SELECT
              P.ID_PRODUIT AS ID_PRODUIT,
              P.PRODUIT AS PRODUIT,
              STP.ID_SOUS_TYPE_PRODUIT AS ID_SOUS_TYPE_PRODUIT,
              STP.SOUS_TYPE_PRODUIT AS SOUS_TYPE_PRODUIT,
              TP.ID_TYPE_PRODUIT AS ID_TYPE_PRODUIT,
              TP.TYPE_PRODUIT AS TYPE_PRODUIT
          FROM PRODUITS P
          JOIN SOUS_TYPES_PRODUIT STP
              ON P.SOUS_TYPE_PRODUIT = STP.ID_SOUS_TYPE_PRODUIT
          JOIN TYPES_PRODUIT TP
              ON STP.TYPE_PRODUIT = TP.ID_TYPE_PRODUIT
          WHERE UPPER(P.PRODUIT) LIKE UPPER(?)
          """;

    /**
     * "SELECT P.ID_PRODUIT AS ID_PRODUIT,
     * P.PRODUIT AS PRODUIT,
     * STP.ID_SOUS_TYPE_PRODUIT AS ID_SOUS_TYPE_PRODUIT,
     * STP.SOUS_TYPE_PRODUIT AS SOUS_TYPE_PRODUIT,
     * TP.ID_TYPE_PRODUIT AS ID_TYPE_PRODUIT,
     * TP.TYPE_PRODUIT AS TYPE_PRODUIT
     * FROM PRODUITS P
     * JOIN SOUS_TYPES_PRODUIT STP ON P.SOUS_TYPE_PRODUIT = STP.ID_SOUS_TYPE_PRODUIT
     * JOIN TYPES_PRODUIT TP ON STP.TYPE_PRODUIT = TP.ID_TYPE_PRODUIT
     * WHERE STP.ID_SOUS_TYPE_PRODUIT = ?"
     */
    public static final String SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_PARENT
        = """
          SELECT
              P.ID_PRODUIT AS ID_PRODUIT,
              P.PRODUIT AS PRODUIT,
              STP.ID_SOUS_TYPE_PRODUIT AS ID_SOUS_TYPE_PRODUIT,
              STP.SOUS_TYPE_PRODUIT AS SOUS_TYPE_PRODUIT,
              TP.ID_TYPE_PRODUIT AS ID_TYPE_PRODUIT,
              TP.TYPE_PRODUIT AS TYPE_PRODUIT
          FROM PRODUITS P
          JOIN SOUS_TYPES_PRODUIT STP
              ON P.SOUS_TYPE_PRODUIT = STP.ID_SOUS_TYPE_PRODUIT
          JOIN TYPES_PRODUIT TP
              ON STP.TYPE_PRODUIT = TP.ID_TYPE_PRODUIT
          WHERE STP.ID_SOUS_TYPE_PRODUIT = ?
          """;

    // *************************** ATTRIBUTS *****************************/

    /**
     * <div>
     * <p>Locale neutre utilisée pour les transformations de casse
     * déterministes (prévisible, stable, reproductible partout).
     * A entrée identique, résultat identique, quel que soit le poste,
     * la JVM, la langue système ou la configuration locale.</p>
     * <ul>
     * <li>{@code Locale.getDefault()} utilise la locale courante de la JVM,
     * donc la locale de l’environnement d’exécution :
     * poste développeur, CI, configuration système, options JVM, ...
     * {@code Locale.getDefault()} peut valoir Locale.FRANCE, Locale.US,
     * une locale turque, ou autre chose selon la machine. -> A utiliser
     * uniquement si le test veut volontairement
     * dépendre de la locale utilisateur/JVM.</li>
     * <li>{@code Locale.ROOT} est une locale neutre, stable,
     * non liée à un pays ou à une langue utilisateur. -> à utiliser
     * pour des transformations techniques, tests déterministes,
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
     * <code>ProduitGatewayJPAService</code>, afin de vérifier
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
    private ProduitDaoJPA produitDaoJPA;

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
    public ProduitDaoJPATest() {
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
     * <li>{@link AutoConfigurationPackage} indique à l'auto-configuration
     * le package à utiliser pour la tranche JPA du test ;</li>
     * <li>{@link EntityScan} indique le package des entities JPA, situé dans
     * un autre package que les DAO.</li>
     * </ul>
     *
     * <p style="font-weight:bold;">Ce que cette configuration ne fait pas :</p>
     * <ul>
     * <li>elle ne déclare pas {@code @EnableJpaRepositories} ;</li>
     * <li>elle ne force aucun scan manuel des repositories ;</li>
     * <li>elle ne charge aucun Gateway ;</li>
     * <li>elle ne charge aucun Controller ;</li>
     * <li>elle ne charge aucun service applicatif ;</li>
     * <li>elle n'active pas
     * {@code spring.main.allow-bean-definition-overriding=true}.</li>
     * </ul>
     *
     * <p>
     * Le repository testé reste découvert par l'auto-configuration JPA du
     * slice {@link DataJpaTest}. Le test reste donc un test direct DAO,
     * autonome, et limité à la persistance JPA.
     * </p>
     * </div>
     *
     * @author Daniel Lévy
     * @version 1.0
     * @since 09 mai 2026
     */
    @SpringBootConfiguration(proxyBeanMethods = false)
    @AutoConfigurationPackage(basePackageClasses = ProduitDaoJPA.class)
    @EntityScan(basePackageClasses = ProduitJPA.class)
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



    // ======================= findByProduitIgnoreCase ===================//

    
    
    /**
     * <div>
     * <p>garantit que {@link ProduitDaoJPA#findByProduitIgnoreCase(String)}
     * avec un libellé existant fourni dans une casse différente :</p>
     * <ul>
     * <li>retourne une liste non null et non vide ;</li>
     * <li>retrouve la ligne seedée portant le libellé exact attendu ;</li>
     * <li>ignore la casse du paramètre fourni ;</li>
     * <li>retourne l'identifiant réellement présent dans le stockage ;</li>
     * <li>retourne l'objet métier avec son parent {@link SousTypeProduitJPA}
     * et le parent {@link TypeProduitJPA} du parent ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DAO_PRODUIT)
    @DisplayName(DN_FIND_BY_PRODUIT_IGNORE_CASE_OK)
    @Test
    public void testFindByProduitIgnoreCaseTrouve() throws Exception {

        /* ARRANGE :
         * compte d'abord le nombre total de lignes dans le stockage.
         *
         * Comme la méthode DAO testée est une méthode de lecture,
         * ce compteur permettra de prouver en fin de test
         * que l'appel n'a pas modifié les données seedées.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isPositive();

        /*
         * Vérifie par SQL direct que le libellé de référence existe
         * exactement une fois dans le stockage, sans tenir compte
         * des majuscules/minuscules.
         */
        final Long countLibelle = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS_WHERE_LIBELLE,
                Long.class,
                CHEMISE_ML_HOMME);

        assertThat(countLibelle).isNotNull().isEqualTo(1L);

        /*
         * Relit par SQL direct la ligne attendue avec son parent
         * SousTypeProduit et le TypeProduit de ce parent.
         *
         * Cette projection SQL constitue la preuve indépendante
         * du repository testé.
         */
        final List<Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().hasSize(1);

        final Map<String, Object> ligneStockage = lignesStockage.get(0);

        /*
         * Prépare le paramètre DAO avec une casse différente.
         *
         * Le libellé seedé est "chemise à manches longues pour homme".
         * Le paramètre envoyé au DAO est volontairement fourni
         * en majuscules afin de tester directement le suffixe
         * Spring Data IgnoreCase.
         */
        final String libelleRecherche =
                CHEMISE_ML_HOMME.toUpperCase(LOCALE_ROOT);

        /* ACT :
         * appelle directement le DAO, sans passer par le Gateway.
         */
        final List<ProduitJPA> resultats =
                this.produitDaoJPA.findByProduitIgnoreCase(libelleRecherche);

        /* ASSERT :
         * vérifie que le DAO retourne bien une liste exploitable.
         */
        assertThat(resultats).isNotNull().hasSize(1);

        final ProduitJPA resultat = resultats.get(0);

        /*
         * Vérifie que l'entity retournée correspond exactement
         * à la ligne SQL attendue :
         * - même identifiant technique ;
         * - même libellé persistant ;
         * - même parent SousTypeProduit ;
         * - même TypeProduit du parent.
         */
        assertThat(resultat.getIdProduit())
            .isEqualTo(Long.valueOf(
                    ((Number) ligneStockage.get(ID_PRODUIT)).longValue()));

        assertThat(resultat.getProduit())
            .isEqualTo(String.valueOf(ligneStockage.get(PRODUIT)));

        assertThat(resultat.getSousTypeProduit()).isNotNull();

        assertThat(resultat.getSousTypeProduit().getIdSousTypeProduit())
            .isEqualTo(Long.valueOf(
                    ((Number) ligneStockage.get(ID_SOUS_TYPE_PRODUIT))
                            .longValue()));

        assertThat(resultat.getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(String.valueOf(ligneStockage.get(SOUS_TYPE_PRODUIT)));

        assertThat(resultat.getSousTypeProduit().getTypeProduit()).isNotNull();

        assertThat(resultat.getSousTypeProduit().getTypeProduit().getIdTypeProduit())
            .isEqualTo(Long.valueOf(
                    ((Number) ligneStockage.get(ID_TYPE_PRODUIT)).longValue()));

        assertThat(resultat.getSousTypeProduit().getTypeProduit().getTypeProduit())
            .isEqualTo(String.valueOf(ligneStockage.get(TYPE_PRODUIT)));

        /*
         * Compte finalement le stockage pour prouver que la méthode DAO
         * de lecture n'a pas ajouté, supprimé ni modifié de ligne.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isEqualTo(countAvant);

    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>garantit que {@link ProduitDaoJPA#findByProduitIgnoreCase(String)}
     * avec un libellé absent :</p>
     * <ul>
     * <li>reste cohérent avec l'absence de ligne dans le stockage ;</li>
     * <li>retourne une liste non null ;</li>
     * <li>retourne une liste vide, conformément au comportement Spring Data
     * attendu pour cette signature liste ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DAO_PRODUIT)
    @DisplayName(DN_FIND_BY_PRODUIT_IGNORE_CASE_NON_TROUVE)
    @Test
    public void testFindByProduitIgnoreCaseNonTrouve() throws Exception {

        /* ARRANGE :
         * compte d'abord le stockage pour vérifier ensuite
         * que l'appel de lecture ne modifie aucune donnée seedée.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isPositive();

        /*
         * Vérifie par SQL direct qu'aucune ligne ne porte
         * le libellé volontairement introuvable.
         */
        final Long countLibelle = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS_WHERE_LIBELLE,
                Long.class,
                INTROUVABLE);

        assertThat(countLibelle).isNotNull().isZero();

        /* ACT :
         * appelle directement le DAO avec un libellé absent du stockage.
         */
        final List<ProduitJPA> resultats =
                this.produitDaoJPA.findByProduitIgnoreCase(INTROUVABLE);

        /* ASSERT :
         * vérifie que la signature liste ne retourne pas null,
         * mais une liste vide.
         */
        assertThat(resultats).isNotNull().isEmpty();

        /*
         * Vérifie enfin que cette lecture infructueuse
         * n'a pas altéré le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isEqualTo(countAvant);

    } // __________________________________________________________________

    
    
    // ================= findByProduitContainingIgnoreCase ===============//

    
    
    /**
     * <div>
     * <p>garantit que
     * {@link ProduitDaoJPA#findByProduitContainingIgnoreCase(String)}
     * avec un contenu partiel présent :</p>
     * <ul>
     * <li>retourne une liste non null et non vide ;</li>
     * <li>retrouve les libellés contenant le motif demandé ;</li>
     * <li>ignore la casse du motif fourni ;</li>
     * <li>retourne le même ensemble que la requête SQL de référence ;</li>
     * <li>retourne les objets métier avec leur parent
     * {@link SousTypeProduitJPA} et le parent {@link TypeProduitJPA}
     * du parent ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DAO_PRODUIT)
    @DisplayName(DN_FIND_BY_PRODUIT_CONTAINING_IGNORE_CASE_OK)
    @Test
    public void testFindByProduitContainingIgnoreCaseTrouve()
            throws Exception {

        /* ARRANGE :
         * compte d'abord le stockage pour prouver ensuite
         * que la recherche partielle DAO reste une lecture pure.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isPositive();

        /*
         * Vérifie par SQL direct que le motif demandé correspond
         * à au moins une ligne seedée.
         */
        final Long countMotif = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS_WHERE_LIKE,
                Long.class,
                "%" + CONTENU_PARTIEL_CHEMISE + "%");

        assertThat(countMotif).isNotNull().isPositive();

        /*
         * Lit par SQL direct les lignes réellement présentes
         * qui contiennent le motif demandé, sans tenir compte
         * des majuscules/minuscules.
         *
         * Cette projection SQL constitue la référence indépendante
         * du repository testé.
         */
        final List<Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIKE,
                        "%" + CONTENU_PARTIEL_CHEMISE + "%");

        assertThat(lignesStockage).isNotNull().isNotEmpty();
        assertThat(lignesStockage).hasSize(countMotif.intValue());

        /*
         * Extrait les clés parent SousTypeProduit + libellé Produit
         * depuis la référence SQL.
         *
         * Pour Produit, la clé métier directe est :
         * - parent SousTypeProduit ;
         * - libellé Produit.
         *
         * Le TypeProduit n'est pas intégré à cette clé directe,
         * mais il reste contrôlé comme partie du graphe complet.
         */
        final List<String> clesStockage = lignesStockage.stream()
                .map(ligne -> ligne.get(SOUS_TYPE_PRODUIT)
                        + "||"
                        + ligne.get(PRODUIT))
                .toList();

        /*
         * Prépare le motif avec une casse différente.
         *
         * Le contenu seedé contient le motif "chemise".
         * Le paramètre envoyé au DAO est fourni en majuscules
         * afin de tester directement le suffixe Spring Data IgnoreCase.
         */
        final String contenuRecherche =
                CONTENU_PARTIEL_CHEMISE.toUpperCase(LOCALE_ROOT);

        /* ACT :
         * appelle directement le DAO avec un motif partiel.
         */
        final List<ProduitJPA> resultats =
                this.produitDaoJPA.findByProduitContainingIgnoreCase(
                        contenuRecherche);

        /* ASSERT :
         * vérifie d'abord que Spring Data retourne une liste exploitable.
         */
        assertThat(resultats).isNotNull().isNotEmpty();
        assertThat(resultats).hasSize(lignesStockage.size());

        /*
         * Vérifie que chaque résultat porte un libellé et un parent.
         *
         * Ce contrôle est volontairement didactique :
         * le DAO retourne des entities ProduitJPA,
         * et l'objet métier enfant doit rester rattaché
         * à son parent SousTypeProduit puis au TypeProduit de ce parent.
         */
        assertThat(resultats).allSatisfy(resultat -> {
            assertThat(resultat.getProduit()).isNotBlank();
            assertThat(resultat.getSousTypeProduit()).isNotNull();
            assertThat(resultat.getSousTypeProduit().getSousTypeProduit())
                .isNotBlank();
            assertThat(resultat.getSousTypeProduit().getTypeProduit())
                .isNotNull();
            assertThat(resultat.getSousTypeProduit()
                    .getTypeProduit()
                    .getTypeProduit())
                .isNotBlank();
        });

        /*
         * Extrait les clés parent SousTypeProduit + libellé Produit
         * retournées par le DAO.
         */
        final List<String> clesResultats = resultats.stream()
                .map(resultat -> resultat.getSousTypeProduit().getSousTypeProduit()
                        + "||"
                        + resultat.getProduit())
                .toList();

        /*
         * Compare l'ensemble retourné par le DAO avec la référence SQL.
         *
         * On utilise containsExactlyInAnyOrderElementsOf(...)
         * car cette méthode DAO ne porte aucune clause de tri explicite :
         * le test contrôle ici le contenu, pas l'ordre.
         */
        assertThat(clesResultats)
            .containsExactlyInAnyOrderElementsOf(clesStockage);

        /*
         * Vérifie explicitement que tous les libellés retournés
         * contiennent bien le motif demandé, sans tenir compte
         * de la casse.
         */
        assertThat(resultats)
            .extracting(ProduitJPA::getProduit)
            .allSatisfy(libelle -> assertThat(libelle)
                    .isNotNull()
                    .containsIgnoringCase(CONTENU_PARTIEL_CHEMISE));

        /*
         * Vérifie aussi que le jeu seedé attendu pour ce motif
         * contient bien des exemples lisibles du développeur humain.
         */
        assertThat(resultats)
            .extracting(ProduitJPA::getProduit)
            .contains(
                    CHEMISE_ML_HOMME,
                    CHEMISE_MC_HOMME);

        /*
         * Compte finalement le stockage pour prouver que la recherche
         * partielle n'a modifié aucune donnée seedée.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isEqualTo(countAvant);

    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>garantit que
     * {@link ProduitDaoJPA#findByProduitContainingIgnoreCase(String)}
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
    @Tag(TAG_DAO_PRODUIT)
    @DisplayName(DN_FIND_BY_PRODUIT_CONTAINING_IGNORE_CASE_NON_TROUVE)
    @Test
    public void testFindByProduitContainingIgnoreCaseNonTrouve()
            throws Exception {

        /* ARRANGE :
         * compte d'abord le stockage pour vérifier ensuite
         * que la recherche sans résultat ne modifie aucune donnée seedée.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isPositive();

        /*
         * Vérifie par SQL direct qu'aucun libellé seedé
         * ne contient le motif volontairement introuvable.
         */
        final Long countMotif = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS_WHERE_LIKE,
                Long.class,
                "%" + INTROUVABLE + "%");

        assertThat(countMotif).isNotNull().isZero();

        /* ACT :
         * appelle directement le DAO avec un motif absent du stockage.
         */
        final List<ProduitJPA> resultats =
                this.produitDaoJPA.findByProduitContainingIgnoreCase(
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
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isEqualTo(countAvant);

    } // __________________________________________________________________

    
    
    // ======================= findAllBySousTypeProduit ==================//

    
    
    /**
     * <div>
     * <p>garantit que
     * {@link ProduitDaoJPA#findAllBySousTypeProduit(SousTypeProduitJPA)}
     * avec un parent existant portant des produits :</p>
     * <ul>
     * <li>retourne une liste non null et non vide ;</li>
     * <li>retourne les produits rattachés au parent
     * {@link SousTypeProduitJPA} demandé ;</li>
     * <li>retourne le même ensemble que la requête SQL de référence ;</li>
     * <li>retourne les produits avec leur parent {@link SousTypeProduitJPA}
     * et le parent {@link TypeProduitJPA} de ce parent ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DAO_PRODUIT)
    @DisplayName(DN_FIND_ALL_BY_SOUS_TYPE_PRODUIT_OK)
    @Test
    public void testFindAllBySousTypeProduitTrouve() throws Exception {

        /* ARRANGE :
         * compte d'abord le nombre total de lignes dans le stockage.
         *
         * Comme la méthode DAO testée est une méthode de lecture,
         * ce compteur permettra de prouver en fin de test
         * que l'appel n'a pas modifié les données seedées.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isPositive();

        /*
         * Retrouve directement dans le stockage l'identifiant
         * du grand-parent TypeProduit "vêtement".
         */
        final Long idTypeProduitStockage = this.jdbcTemplate.queryForObject(
                SELECT_ID_FROM_TYPES_PRODUIT_WHERE_LIBELLE,
                Long.class,
                VETEMENT);

        assertThat(idTypeProduitStockage).isNotNull();

        /*
         * Retrouve directement dans le stockage l'identifiant
         * du parent SousTypeProduit "vêtement pour homme".
         *
         * Ce parent est celui des produits seedés :
         * - chemise à manches longues pour homme ;
         * - chemise à manches courtes pour homme ;
         * - sweatshirt pour homme ;
         * - teeshirt pour homme.
         */
        final Long idParentStockage = this.jdbcTemplate.queryForObject(
                SELECT_ID_FROM_SOUS_TYPES_PRODUIT_WHERE_LIBELLE,
                Long.class,
                VETEMENT_HOMME);

        assertThat(idParentStockage).isNotNull();

        /*
         * Vérifie directement dans le stockage que ce parent porte
         * au moins un produit enfant.
         */
        final Long countProduitsParent = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS_WHERE_PARENT,
                Long.class,
                idParentStockage);

        assertThat(countProduitsParent).isNotNull().isPositive();

        /*
         * Lit par SQL direct les lignes rattachées au parent.
         *
         * Cette projection SQL constitue la preuve indépendante
         * du repository testé.
         */
        final List<Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_PARENT,
                        idParentStockage);

        assertThat(lignesStockage).isNotNull().isNotEmpty();
        assertThat(lignesStockage).hasSize(countProduitsParent.intValue());

        /*
         * Extrait les clés parent SousTypeProduit + libellé Produit
         * depuis la référence SQL.
         *
         * Pour Produit, on ne raisonne pas seulement sur le libellé :
         * le parent SousTypeProduit est aussi contrôlé.
         */
        final List<String> clesStockage = lignesStockage.stream()
                .map(ligne -> ligne.get(SOUS_TYPE_PRODUIT)
                        + "||"
                        + ligne.get(PRODUIT))
                .toList();

        /*
         * Prépare l'entity grand-parent puis l'entity parent
         * transmises au DAO.
         *
         * Le test passe volontairement une entity parent construite
         * avec l'identifiant réellement présent dans le stockage :
         * la méthode dérivée Spring Data doit rechercher les produits
         * rattachés à ce parent.
         */
        final TypeProduitJPA typeProduit =
                new TypeProduitJPA(idTypeProduitStockage, VETEMENT);

        final SousTypeProduitJPA parent =
                new SousTypeProduitJPA(
                        idParentStockage,
                        VETEMENT_HOMME,
                        typeProduit);

        /* ACT :
         * appelle directement le DAO, sans passer par le Gateway.
         */
        final List<ProduitJPA> resultats =
                this.produitDaoJPA.findAllBySousTypeProduit(parent);

        /* ASSERT :
         * vérifie d'abord que Spring Data retourne une liste exploitable.
         */
        assertThat(resultats).isNotNull().isNotEmpty();
        assertThat(resultats).hasSize(lignesStockage.size());

        /*
         * Vérifie que chaque résultat porte un libellé et le parent attendu.
         *
         * Ce contrôle est volontairement didactique :
         * le DAO retourne des entities ProduitJPA,
         * et l'objet métier enfant doit rester rattaché
         * au parent SousTypeProduit demandé.
         */
        assertThat(resultats).allSatisfy(resultat -> {
            assertThat(resultat.getProduit()).isNotBlank();
            assertThat(resultat.getSousTypeProduit()).isNotNull();
            assertThat(resultat.getSousTypeProduit().getIdSousTypeProduit())
                .isEqualTo(idParentStockage);
            assertThat(resultat.getSousTypeProduit().getSousTypeProduit())
                .isEqualTo(VETEMENT_HOMME);
            assertThat(resultat.getSousTypeProduit().getTypeProduit())
                .isNotNull();
            assertThat(resultat.getSousTypeProduit()
                    .getTypeProduit()
                    .getIdTypeProduit())
                .isEqualTo(idTypeProduitStockage);
            assertThat(resultat.getSousTypeProduit()
                    .getTypeProduit()
                    .getTypeProduit())
                .isEqualTo(VETEMENT);
        });

        /*
         * Extrait les clés parent SousTypeProduit + libellé Produit
         * retournées par le DAO.
         */
        final List<String> clesResultats = resultats.stream()
                .map(resultat -> resultat.getSousTypeProduit().getSousTypeProduit()
                        + "||"
                        + resultat.getProduit())
                .toList();

        /*
         * Compare l'ensemble retourné par le DAO avec la référence SQL.
         *
         * On utilise containsExactlyInAnyOrderElementsOf(...)
         * car cette méthode DAO ne porte aucune clause de tri explicite :
         * le test contrôle ici le contenu, pas l'ordre.
         */
        assertThat(clesResultats)
            .containsExactlyInAnyOrderElementsOf(clesStockage);

        /*
         * Vérifie aussi que le jeu seedé attendu pour ce parent
         * contient bien les exemples lisibles du développeur humain.
         */
        assertThat(resultats)
            .extracting(ProduitJPA::getProduit)
            .contains(
                    CHEMISE_ML_HOMME,
                    CHEMISE_MC_HOMME,
                    SWEATSHIRT_HOMME,
                    TEESHIRT_HOMME);

        /*
         * Compte finalement le stockage pour prouver que la méthode DAO
         * de lecture n'a pas ajouté, supprimé ni modifié de ligne.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isEqualTo(countAvant);

    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>garantit que
     * {@link ProduitDaoJPA#findAllBySousTypeProduit(SousTypeProduitJPA)}
     * avec un parent absent du stockage :</p>
     * <ul>
     * <li>reste cohérent avec l'absence de parent dans le stockage ;</li>
     * <li>retourne une liste non null ;</li>
     * <li>retourne une liste vide, conformément au comportement Spring Data
     * attendu pour cette signature liste ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DAO_PRODUIT)
    @DisplayName(DN_FIND_ALL_BY_SOUS_TYPE_PRODUIT_PARENT_ABSENT)
    @Test
    public void testFindAllBySousTypeProduitParentAbsent()
            throws Exception {

        /* ARRANGE :
         * compte d'abord le stockage pour vérifier ensuite
         * que la recherche sans résultat ne modifie aucune donnée seedée.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isPositive();

        /*
         * Vérifie directement dans le stockage qu'aucun parent
         * SousTypeProduit ne porte ID_INEXISTANT.
         */
        final Long countParentStockage = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT_WHERE_ID,
                Long.class,
                ID_INEXISTANT);

        assertThat(countParentStockage).isNotNull().isZero();

        /*
         * Prépare l'entity parent transmise au DAO.
         *
         * Le parent n'existe pas dans le stockage.
         * Le DAO direct ne porte pas la règle applicative Gateway
         * de non-persistance : il doit simplement retourner
         * une liste vide pour ce parent sans ligne correspondante.
         */
        final SousTypeProduitJPA parent =
                new SousTypeProduitJPA(
                        ID_INEXISTANT,
                        INTROUVABLE,
                        null);

        /* ACT :
         * appelle directement le DAO avec un parent absent du stockage.
         */
        final List<ProduitJPA> resultats =
                this.produitDaoJPA.findAllBySousTypeProduit(parent);

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
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isEqualTo(countAvant);

    } // __________________________________________________________________

    
    
} // FIN DE LA CLASSE ProduitDaoJPATest.----------------------------------