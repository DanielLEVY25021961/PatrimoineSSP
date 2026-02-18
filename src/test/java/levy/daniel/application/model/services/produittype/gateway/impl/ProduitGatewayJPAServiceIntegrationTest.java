package levy.daniel.application.model.services.produittype.gateway.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.SousTypeProduitI;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliLibelleBlank;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNonPersistent;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNull;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParentNull;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionTechniqueGatewayNonPersistent;
import levy.daniel.application.model.services.produittype.gateway.ProduitGatewayIService;
import levy.daniel.application.persistence.metier.produittype.dao.daosJPA.ProduitDaoJPA;
import levy.daniel.application.persistence.metier.produittype.dao.daosJPA.SousTypeProduitDaoJPA;
import levy.daniel.application.persistence.metier.produittype.dao.daosJPA.TypeProduitDaoJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.ProduitJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.SousTypeProduitJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.TypeProduitJPA;

/**
 * <div>
 * <p style="font-weight:bold;">TEST JUnit d'INTEGRATION JUnit JUPITER 5</p>
 *
 * <p>Test du Service Gateway <code>ProduitGatewayJPAService</code>.</p>
 *
 * <p>Objectif : tester le gateway en conditions réelles (H2 in-memory) avec les scripts SQL :</p>
 * <ul>
 * <li><code>truncate-test.sql</code></li>
 * <li><code>data-test.sql</code></li>
 * </ul>
 *
 * <p>Baseline : SousTypeProduitGatewayJPAServiceIntegrationTest</p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 2.0
 * @since 19 février 2026
 */
@SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
@Sql(
    scripts = {"classpath:truncate-test.sql", "classpath:data-test.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@DataJpaTest
@ActiveProfiles("test")
@Import(ProduitGatewayJPAService.class)
@ContextConfiguration(classes = ProduitGatewayJPAServiceIntegrationTest.ConfigTest.class)
public class ProduitGatewayJPAServiceIntegrationTest {

    // *************************** CONSTANTES ******************************/

    /**
     * <div>
     * <p>Profil Spring : "test".</p>
     * </div>
     */
    public static final String PROFILE_TEST = "test";

    /**
     * <div>
     * <p>Script SQL truncate (classpath).</p>
     * </div>
     */
    public static final String CLASSPATH_TRUNCATE_SQL = "classpath:truncate-test.sql";

    /**
     * <div>
     * <p>Script SQL data (classpath).</p>
     * </div>
     */
    public static final String CLASSPATH_DATA_SQL = "classpath:data-test.sql";

    /**
     * <div>
     * <p>Qualifier Spring du service gateway.</p>
     * </div>
     */
    public static final String QUALIFIER_SERVICE = "ProduitGatewayJPAService";

    /**
     * <div>
     * <p>Tag JUnit : tests de création.</p>
     * </div>
     */
    public static final String TAG_CREER = "servicesGateway-Creer";

    /**
     * <div>
     * <p>Tag JUnit : tests de recherche.</p>
     * </div>
     */
    public static final String TAG_RECHERCHER = "servicesGateway-Rechercher";

    /**
     * <div>
     * <p>Tag JUnit : tests de recherche par objet métier.</p>
     * </div>
     */
    public static final String TAG_FINDBYOBJETMETIER = "servicesGateway-FindByObjetMetier";

    /**
     * <div>
     * <p>Tag JUnit : tests de recherche rapide.</p>
     * </div>
     */
    public static final String TAG_RECHERCHER_RAPIDE = "servicesGateway-RechercherRapide";

    /**
     * <div>
     * <p>Tag JUnit : tests d'update.</p>
     * </div>
     */
    public static final String TAG_UPDATE = "servicesGateway-Update";

    /**
     * <div>
     * <p>Tag JUnit : tests de delete.</p>
     * </div>
     */
    public static final String TAG_DELETE = "servicesGateway-Delete";

    /**
     * <div>
     * <p>Tag JUnit : tests de count.</p>
     * </div>
     */
    public static final String TAG_COUNT = "servicesGateway-Count";

    /**
     * <div>
     * <p>Tag JUnit : tests de pagination.</p>
     * </div>
     */
    public static final String TAG_PAGINATION = "servicesGateway-Pagination";

    /**
     * <div>
     * <p>Tag JUnit : tests béton.</p>
     * </div>
     */
    public static final String TAG_BETON = "servicesGateway-Beton";

    /**
     * <div>
     * <p>Locale par défaut.</p>
     * </div>
     */
    public static final Locale LOCALE_DEFAUT = Locale.getDefault();

    /**
     * <div>
     * <p>Chaîne vide : "".</p>
     * </div>
     */
    public static final String CHAINE_VIDE = "";

    /**
     * <div>
     * <p>"   ".</p>
     * </div>
     */
    public static final String BLANK = "   ";

    /**
     * <div>
     * <p>"chemise".</p>
     * </div>
     */
    public static final String CHEMISE = "chemise";

    /**
     * <div>
     * <p>"chemise à manches longues pour homme".</p>
     * </div>
     */
    public static final String CHEMISE_ML_HOMME = "chemise à manches longues pour homme";

    /**
     * <div>
     * <p>"chemise à manches courtes pour homme".</p>
     * </div>
     */
    public static final String CHEMISE_MC_HOMME = "chemise à manches courtes pour homme";

    /**
     * <div>
     * <p>"sweatshirt pour homme".</p>
     * </div>
     */
    public static final String SWEAT_HOMME = "sweatshirt pour homme";

    /**
     * <div>
     * <p>Suffix de modif.</p>
     * </div>
     */
    public static final String SUFFIX_MODIF = " (modifié)";

    /**
     * <div>
     * <p>Produit temporaire à supprimer.</p>
     * </div>
     */
    public static final String TEMP_PRODUIT_A_SUPPRIMER = "temp-produit-a-supprimer";

    /**
     * <div>
     * <p>Produit temporaire à modifier.</p>
     * </div>
     */
    public static final String TEMP_PRODUIT_A_MODIFIER = "temp-produit-a-modifier";

    /**
     * <div>
     * <p>Valeur introuvable (pour forcer PAS_DE_RESULTAT).</p>
     * </div>
     */
    public static final String INTROUVABLE = "###___introuvable___###";

    /**
     * <div>
     * <p>Propriété de tri (Entity) : "produit".</p>
     * </div>
     */
    public static final String PROP_TRI_PRODUIT = "produit";

    /**
     * <div>
     * <p>DisplayName : "creer(null) - jette ExceptionAppliParamNull (contrat du port)".</p>
     * </div>
     */
    public static final String DN_CREER_NULL = "creer(null) - jette ExceptionAppliParamNull (contrat du port)";

    /**
     * <div>
     * <p>DisplayName : "creer(blank) - jette ExceptionAppliLibelleBlank (contrat du port)".</p>
     * </div>
     */
    public static final String DN_CREER_BLANK = "creer(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /**
     * <div>
     * <p>DisplayName : "creer(parent null) - jette ExceptionAppliParentNull (contrat du port)".</p>
     * </div>
     */
    public static final String DN_CREER_PARENT_NULL = "creer(parent null) - jette ExceptionAppliParentNull (contrat du port)";

    /**
     * <div>
     * <p>DisplayName : "creer(nominal) - ajoute un élément, le rend retrouvable et ne wipe pas les seedés".</p>
     * </div>
     */
    public static final String DN_CREER_NOMINAL = "creer(nominal) - ajoute un élément, le rend retrouvable et ne wipe pas les seedés";

    /**
     * <div>
     * <p>DisplayName : "rechercherTous() - retourne la liste seedée (triée, sans doublons)".</p>
     * </div>
     */
    public static final String DN_RECHERCHER_TOUS = "rechercherTous() - retourne la liste seedée (triée, sans doublons)";

    /**
     * <div>
     * <p>DisplayName : "findByLibelle(inexistant) - retourne une liste vide".</p>
     * </div>
     */
    public static final String DN_FINDBYLIBELLE_INEXISTANT = "findByLibelle(inexistant) - retourne une liste vide";

    /**
     * <div>
     * <p>DisplayName : "findByLibelleRapide(contenu inexistant) - retourne une liste vide".</p>
     * </div>
     */
    public static final String DN_FINDBYLIBELLERAPIDE_INEXISTANT = "findByLibelleRapide(contenu inexistant) - retourne une liste vide";

    /**
     * <div>
     * <p>DisplayName : "update(parent modifié) - met à jour le parent".</p>
     * </div>
     */
    public static final String DN_UPDATE_PARENT_MODIFIE = "update(parent modifié) - met à jour le parent";

    /**
     * <div>
     * <p>DisplayName : "findAllByParent(nominal) - retourne les enfants du parent".</p>
     * </div>
     */
    public static final String DN_FINDALLBYPARENT_NOMINAL = "findAllByParent(nominal) - retourne les enfants du parent";

    /**
     * <div>
     * <p>DisplayName : "findById(nominal) - retourne l'objet métier correspondant".</p>
     * </div>
     */
    public static final String DN_FINDBYID_NOMINAL = "findById(nominal) - retourne l'objet métier correspondant";

    /**
     * <div>
     * <p>DisplayName : "update(nominal) - modifie le stockage et retourne l'objet modifié".</p>
     * </div>
     */
    public static final String DN_UPDATE_NOMINAL = "update(nominal) - modifie le stockage et retourne l'objet modifié";

    /** DisplayName : "update(null) - jette ExceptionAppliParamNull (contrat du port)". */
    public static final String DN_UPDATE_NULL = "update(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** DisplayName : "update(blank) - jette ExceptionAppliLibelleBlank (contrat du port)". */
    public static final String DN_UPDATE_BLANK = "update(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /**
     * <div>
     * <p>DisplayName : "delete(nominal) - supprime l'élément et le rend introuvable".</p>
     * </div>
     */
    public static final String DN_DELETE_NOMINAL = "delete(nominal) - supprime l'élément et le rend introuvable";

    /**
     * <div>
     * <p>DisplayName : "count() - cohérent avec le DAO".</p>
     * </div>
     */
    public static final String DN_COUNT_NOMINAL = "count() - cohérent avec le DAO";

    // Messages d'erreur (alignés sur ProduitGatewayIService)
    /**
     * <div>
     * <p>Message d'erreur : MESSAGE_CREER_KO_PARAM_NULL.</p>
     * </div>
     */
    public static final String MSG_CREER_KO_PARAM_NULL = ProduitGatewayIService.MESSAGE_CREER_KO_PARAM_NULL;

    /**
     * <div>
     * <p>Message d'erreur : MESSAGE_CREER_KO_LIBELLE_BLANK.</p>
     * </div>
     */
    public static final String MSG_CREER_KO_LIBELLE_BLANK = ProduitGatewayIService.MESSAGE_CREER_KO_LIBELLE_BLANK;

    /**
     * <div>
     * <p>Message d'erreur : MESSAGE_CREER_KO_PARENT_NULL.</p>
     * </div>
     */
    public static final String MSG_CREER_KO_PARENT_NULL = ProduitGatewayIService.MESSAGE_CREER_KO_PARENT_NULL;

    /**
     * <div>
     * <p>Message d'erreur : MESSAGE_FINDBYID_KO_PARAM_NULL.</p>
     * </div>
     */
    public static final String MSG_FINDBYID_KO_PARAM_NULL = ProduitGatewayIService.MESSAGE_FINDBYID_KO_PARAM_NULL;

    /**
     * <div>
     * <p>Message d'erreur : MESSAGE_UPDATE_KO_PARAM_NULL.</p>
     * </div>
     */
    public static final String MSG_UPDATE_KO_PARAM_NULL = ProduitGatewayIService.MESSAGE_UPDATE_KO_PARAM_NULL;

    /**
     * <div>
     * <p>Message d'erreur : MESSAGE_UPDATE_KO_LIBELLE_BLANK.</p>
     * </div>
     */
    public static final String MSG_UPDATE_KO_LIBELLE_BLANK = ProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_BLANK;

    /**
     * <div>
     * <p>Message d'erreur : MESSAGE_UPDATE_KO_NON_PERSISTENT.</p>
     * </div>
     */
    public static final String MSG_UPDATE_KO_NON_PERSISTENT = ProduitGatewayIService.MESSAGE_UPDATE_KO_NON_PERSISTENT;

    /**
     * <div>
     * <p>Message d'erreur : MESSAGE_DELETE_KO_PARAM_NULL.</p>
     * </div>
     */
    public static final String MSG_DELETE_KO_PARAM_NULL = ProduitGatewayIService.MESSAGE_DELETE_KO_PARAM_NULL;

    /**
     * <div>
     * <p>Message d'erreur : MESSAGE_DELETE_KO_ID_NULL.</p>
     * </div>
     */
    public static final String MSG_DELETE_KO_ID_NULL = ProduitGatewayIService.MESSAGE_DELETE_KO_ID_NULL;

    /**
     * <div>
     * <p>Message d'erreur : MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK.</p>
     * </div>
     */
    public static final String MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK = ProduitGatewayIService.MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK;

    /**
     * <div>
     * <p>Message d'erreur : MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL.</p>
     * </div>
     */
    public static final String MSG_FINDBYLIBELLERAPIDE_KO_PARAM_NULL = ProduitGatewayIService.MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL;

    /**
     * <div>
     * <p>Message d'erreur : MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL.</p>
     * </div>
     */
    public static final String MSG_FINDALLBYPARENT_KO_PARAM_NULL = ProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL;
    
    /** DisplayName : "findByObjetMetier(null) - jette ExceptionAppliParamNull (contrat du port)". */
    public static final String DN_FINDBYOBJETMETIER_NULL = "findByObjetMetier(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** DisplayName : "findByObjetMetier(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)". */
    public static final String DN_FINDBYOBJETMETIER_BLANK = "findByObjetMetier(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** DisplayName : "findByObjetMetier(parent null) - jette ExceptionAppliParentNull (contrat du port)". */
    public static final String DN_FINDBYOBJETMETIER_PARENT_NULL = "findByObjetMetier(parent null) - jette ExceptionAppliParentNull (contrat du port)";

    /** DisplayName : "findByObjetMetier(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)". */
    public static final String DN_FINDBYOBJETMETIER_PARENT_NON_PERSISTANT = "findByObjetMetier(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** DisplayName : "findByObjetMetier(nominal) - retourne l'objet métier correspondant". */
    public static final String DN_FINDBYOBJETMETIER_NOMINAL = "findByObjetMetier(nominal) - retourne l'objet métier correspondant";

    /** Message d'erreur : MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL. */
    public static final String MSG_FINDBYOBJETMETIER_KO_PARAM_NULL = ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL;

    /** Message d'erreur : MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK. */
    public static final String MSG_FINDBYOBJETMETIER_KO_LIBELLE_BLANK = ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK;

    /** Message d'erreur : MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL. */
    public static final String MSG_FINDBYOBJETMETIER_KO_PARENT_NULL = ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL;

    /** Message d'erreur : MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT. */
    public static final String MSG_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTANT = ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT;

    // *************************** ATTRIBUTS *******************************/

    /**
     * <div>
     * <p>Service testé.</p>
     * </div>
     */
    @Autowired
    @Qualifier(QUALIFIER_SERVICE)
    private ProduitGatewayIService service;

    /**
     * <div>
     * <p>DAO parent (accès aux IDs persistés pour préparer les objets métier).</p>
     * </div>
     */
    @Autowired
    private SousTypeProduitDaoJPA sousTypeProduitDaoJPA;

    /**
     * <div>
     * <p>DAO enfant (contrôles béton : compter / retrouver IDs).</p>
     * </div>
     */
    @Autowired
    private ProduitDaoJPA produitDaoJPA;

    /**
     * <div>
     * <p>EntityManager pour le rafraîchissement du cache Hibernate.</p>
     * </div>
     */
    @Autowired
    private EntityManager entityManager;

    /**
     * <div>
     * <p>JdbcTemplate pour vérifications directes en base.</p>
     * </div>
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ************************* METHODES **********************************/

    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
     * </div>
     */
    public ProduitGatewayJPAServiceIntegrationTest() {
        super();
    }

    // ===================== CONFIGURATION SPRING =====================

    /**
     * <div>
     * <p style="font-weight:bold;">Classe interne de configuration Spring.</p>
     * <ul>
     * <li>@Configuration</li>
     * <li>@EnableJpaRepositories</li>
     * <li>@EntityScan</li>
     * </ul>
     * </div>
     */
    @Configuration
    @EnableJpaRepositories(basePackageClasses = {
        ProduitDaoJPA.class,
        SousTypeProduitDaoJPA.class,
        TypeProduitDaoJPA.class
    })
    @EntityScan(basePackageClasses = {
        ProduitJPA.class,
        SousTypeProduitJPA.class,
        TypeProduitJPA.class
    })
    public static final class ConfigTest { // NOPMD by danyl on 03/02/2026 05:03
        // Configuration minimale
    }

    // ============================== OUTILS ===============================

    /**
     * <div>
     * <p>Retrouve l'ID persistant d'un {@link SousTypeProduitJPA} par libellé.</p>
     * </div>
     *
     * @param pLibelleParent : String : 
     * libellé du SousTypeProduit
     * @return Long : ID persistant
     */
    private Long retrouverIdParentPersistantParLibelle(
    		final String pLibelleParent) {
    	
        final SousTypeProduitJPA enfant = this.sousTypeProduitDaoJPA
            .findBySousTypeProduitIgnoreCase(pLibelleParent).get(0);
        assertThat(enfant).isNotNull();
        assertThat(enfant.getIdSousTypeProduit()).isNotNull();
        return enfant.getIdSousTypeProduit();
    }

    
    
    /**
     * <div>
     * <p>Vérifie la suppression physique en base via JdbcTemplate.</p>
     * </div>
     *
     * @param pId Long ID de l'entité supprimée
     */
    private void verifierSuppressionEnBase(final Long pId) {
        final Integer count = this.jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM PRODUITS WHERE ID_PRODUIT = ?",
            Integer.class, pId
        );
        assertThat(count)
            .as("L'enregistrement doit être physiquement supprimé de la base")
            .isEqualTo(0);
    }

    // =============================== TESTS ===============================

    // ===================== CREER =====================

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de null sur creer(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>creer(null) jette {@link ExceptionAppliParamNull} avec MSG_CREER_KO_PARAM_NULL.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_NULL)
    @Test
    public void testCreerParamNullExceptionAppliParamNull() throws Exception {
        assertThatThrownBy(() -> this.service.creer(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_CREER_KO_PARAM_NULL);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de libellé blank sur creer(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>creer(libellé blank) jette {@link ExceptionAppliLibelleBlank} avec MSG_CREER_KO_LIBELLE_BLANK.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_BLANK)
    @Test
    public void testCreerLibelleBlankExceptionAppliLibelleBlank() throws Exception {
        // On récupère directement un produit existant pour obtenir son parent
        final List<Produit> produitsExistants = this.service.findByLibelle(CHEMISE_ML_HOMME);
        assertThat(produitsExistants).isNotEmpty();
        final SousTypeProduitI parent = produitsExistants.get(0).getSousTypeProduit();

        final Produit aCreer = new Produit();
        aCreer.setProduit(BLANK);
        aCreer.setSousTypeProduit(parent);

        assertThatThrownBy(() -> this.service.creer(aCreer))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_CREER_KO_LIBELLE_BLANK);
    }
    
    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de parent null sur creer(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>creer(parent null) jette {@link ExceptionAppliParentNull} avec MSG_CREER_KO_PARENT_NULL.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_NULL)
    @Test
    public void testCreerParentNullExceptionAppliParentNull() throws Exception {
        final Produit produit = new Produit(null, TEMP_PRODUIT_A_SUPPRIMER, null);

        assertThatThrownBy(() -> this.service.creer(produit))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_CREER_KO_PARENT_NULL);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de persistance du parent sur creer(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>creer(parent non persistant) jette {@link ExceptionTechniqueGatewayNonPersistent}.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(parent non persistant) - ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testCreerParentNonPersistantExceptionTechniqueGatewayNonPersistent() throws Exception {
        final SousTypeProduit parent = new SousTypeProduit();
        parent.setIdSousTypeProduit(999L); // ID inexistant
        parent.setSousTypeProduit("Parent inexistant");

        final Produit produit = new Produit(null, TEMP_PRODUIT_A_SUPPRIMER, parent);

        assertThatThrownBy(() -> this.service.creer(produit))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le fonctionnement nominal de creer(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>creer(nominal) retourne un {@link Produit} non null avec ID.</p>
     * <p style="font-weight:bold;">GARANTIES :</p>
     * <p>Le count() augmente de 1 et l'objet est retrouvable.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_NOMINAL)
    @Test
    public void testCreerNominalOk() throws Exception {
        final long avant = this.service.count();

        // On récupère directement un produit existant pour obtenir son parent
        final List<Produit> produitsExistants = this.service.findByLibelle(CHEMISE_ML_HOMME);
        assertThat(produitsExistants).isNotEmpty();
        final SousTypeProduitI parent = produitsExistants.get(0).getSousTypeProduit();

        final Produit aCreer = new Produit();
        aCreer.setProduit(TEMP_PRODUIT_A_SUPPRIMER);
        aCreer.setSousTypeProduit(parent);

        final Produit cree = this.service.creer(aCreer);

        assertThat(cree).isNotNull();
        assertThat(cree.getIdProduit()).isNotNull();
        assertThat(cree.getProduit()).isEqualTo(TEMP_PRODUIT_A_SUPPRIMER);

        final long apres = this.service.count();
        assertThat(apres).isEqualTo(avant + 1L);

        final Produit relu = this.service.findById(cree.getIdProduit());
        assertThat(relu).isNotNull();
        assertThat(relu.getProduit()).isEqualTo(TEMP_PRODUIT_A_SUPPRIMER);
    }
    
    // ===================== RECHERCHER =====================

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier rechercherTous() sur base initialisée.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>rechercherTous() retourne une liste non null et non vide.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_RECHERCHER_TOUS)
    @Test
    public void testRechercherTousNominalOk() throws Exception {
        final List<Produit> resultats = this.service.rechercherTous();

        assertThat(resultats).isNotNull().isNotEmpty();
        assertThat(resultats)
            .extracting(Produit::getProduit)
            .contains(CHEMISE_ML_HOMME, CHEMISE_MC_HOMME);

        // Vérification de la cohérence du graphe
        resultats.forEach(p -> {
            assertThat(p.getIdProduit()).isNotNull();
            assertThat(p.getProduit()).isNotBlank();
            assertThat(p.getSousTypeProduit()).isNotNull();
            assertThat(p.getSousTypeProduit().getIdSousTypeProduit()).isNotNull();
        });
    }
    
    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de null sur findByObjetMetier(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByObjetMetier(null) jette {@link ExceptionAppliParamNull} avec MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(null) - jette ExceptionAppliParamNull (contrat du port)")
    @Test
    public void testFindByObjetMetierParamNullExceptionAppliParamNull() throws Exception {
        assertThatThrownBy(() -> this.service.findByObjetMetier(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de libellé blank sur findByObjetMetier(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByObjetMetier(libellé blank) jette {@link ExceptionAppliLibelleBlank} avec MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testFindByObjetMetierLibelleBlankExceptionAppliLibelleBlank() throws Exception {
        // On récupère un parent existant
        final List<Produit> produitsExistants = this.service.findByLibelle(CHEMISE_ML_HOMME);
        assertThat(produitsExistants).isNotEmpty();
        final SousTypeProduitI parent = produitsExistants.get(0).getSousTypeProduit();

        final Produit probe = new Produit();
        probe.setProduit(BLANK);
        probe.setSousTypeProduit(parent);

        assertThatThrownBy(() -> this.service.findByObjetMetier(probe))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de parent null sur findByObjetMetier(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByObjetMetier(parent null) jette {@link ExceptionAppliParentNull} avec MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(parent null) - jette ExceptionAppliParentNull (contrat du port)")
    @Test
    public void testFindByObjetMetierParentNullExceptionAppliParentNull() throws Exception {
        final Produit probe = new Produit();
        probe.setProduit(CHEMISE_ML_HOMME);
        probe.setSousTypeProduit(null);

        assertThatThrownBy(() -> this.service.findByObjetMetier(probe))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de persistance du parent sur findByObjetMetier(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByObjetMetier(parent non persistant) jette {@link ExceptionTechniqueGatewayNonPersistent} avec MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)")
    @Test
    public void testFindByObjetMetierParentNonPersistantExceptionTechniqueGatewayNonPersistent() throws Exception {
        final SousTypeProduit parent = new SousTypeProduit();
        parent.setIdSousTypeProduit(999L); // ID inexistant
        parent.setSousTypeProduit("Parent inexistant");

        final Produit probe = new Produit();
        probe.setProduit(CHEMISE_ML_HOMME);
        probe.setSousTypeProduit(parent);

        assertThatThrownBy(() -> this.service.findByObjetMetier(probe))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT + "Parent inexistant");
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findByObjetMetier(nominal) sur donnée seed.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByObjetMetier(trouvé) retourne un {@link Produit} non null.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Libellé et parent cohérents.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(nominal) - retourne l'objet métier correspondant")
    @Test
    public void testFindByObjetMetierNominalOk() throws Exception {
        // On récupère un produit existant pour construire la probe
        final List<Produit> produitsExistants = this.service.findByLibelle(CHEMISE_ML_HOMME);
        assertThat(produitsExistants).isNotEmpty();
        final Produit seed = produitsExistants.get(0);

        final Produit probe = new Produit();
        probe.setProduit(seed.getProduit());
        probe.setSousTypeProduit(seed.getSousTypeProduit());

        final Produit trouve = this.service.findByObjetMetier(probe);

        assertThat(trouve).isNotNull();
        assertThat(trouve.getProduit()).isEqualTo(CHEMISE_ML_HOMME);
        assertThat(trouve.getSousTypeProduit()).isNotNull();
        assertThat(trouve.getSousTypeProduit().getIdSousTypeProduit()).isEqualTo(seed.getSousTypeProduit().getIdSousTypeProduit());
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findByLibelle(libellé exact).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByLibelle(nominal) retourne une liste non vide.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelle(nominal) - retrouve un produit seedé via le libellé exact")
    @Test
    public void testFindByLibelleNominalOk() throws Exception {
        final List<Produit> resultats = this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(resultats).isNotNull().isNotEmpty();
        assertThat(resultats.get(0).getProduit()).isEqualTo(CHEMISE_ML_HOMME);
        assertThat(resultats.get(0).getSousTypeProduit()).isNotNull();
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de blank sur findByLibelle(pLibelle).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByLibelle(blank) jette {@link ExceptionAppliLibelleBlank} avec MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_INEXISTANT)
    @Test
    public void testFindByLibelleBlankExceptionAppliLibelleBlank() throws Exception {
        assertThatThrownBy(() -> this.service.findByLibelle(BLANK))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findByLibelle(inexistant).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByLibelle(inexistant) retourne une liste vide.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_INEXISTANT)
    @Test
    public void testFindByLibelleInexistantVideOk() throws Exception {
        final List<Produit> resultats = this.service.findByLibelle(INTROUVABLE);
        assertThat(resultats).isNotNull().isEmpty();
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findByLibelleRapide(blank) -> rechercherTous().</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByLibelleRapide(blank) retourne tous les éléments.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_FINDBYLIBELLERAPIDE_INEXISTANT)
    @Test
    public void testFindByLibelleRapideBlankRetourneTous() throws Exception {
        final List<Produit> tous = this.service.rechercherTous();
        final List<Produit> rapides = this.service.findByLibelleRapide(BLANK);

        assertThat(rapides).isNotNull();
        assertThat(rapides).hasSameSizeAs(tous);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findByLibelleRapide(contenu partiel).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByLibelleRapide(partiel) retourne une liste non vide.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName("findByLibelleRapide(nominal) - retourne les correspondances partielles")
    @Test
    public void testFindByLibelleRapideNominalOk() throws Exception {
        final List<Produit> resultats = this.service.findByLibelleRapide(CHEMISE);

        assertThat(resultats).isNotNull().isNotEmpty();
        assertThat(resultats)
            .extracting(Produit::getProduit)
            .allMatch(libelle -> libelle.toLowerCase(LOCALE_DEFAUT).contains(CHEMISE));
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de null sur findByLibelleRapide(pLibelle).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByLibelleRapide(null) jette {@link ExceptionAppliParamNull} avec MSG_FINDBYLIBELLERAPIDE_KO_PARAM_NULL.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName("findByLibelleRapide(null) - ExceptionAppliParamNull")
    @Test
    public void testFindByLibelleRapideNullExceptionAppliParamNull() throws Exception {
        assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_FINDBYLIBELLERAPIDE_KO_PARAM_NULL);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findAllByParent(nominal).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findAllByParent(nominal) retourne une liste non vide.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_NOMINAL)
    @Test
    public void testFindAllByParentNominalOk() throws Exception {
        // On récupère directement un produit existant pour obtenir son parent
        final List<Produit> produitsExistants = this.service.findByLibelle(CHEMISE_ML_HOMME);
        assertThat(produitsExistants).isNotEmpty();
        final SousTypeProduitI parent = produitsExistants.get(0).getSousTypeProduit();

        final List<Produit> enfants = this.service.findAllByParent((SousTypeProduit) parent);

        assertThat(enfants).isNotNull();
        assertThat(enfants)
            .extracting(Produit::getSousTypeProduit)
            .extracting(SousTypeProduitI::getIdSousTypeProduit)
            .containsOnly(parent.getIdSousTypeProduit());
    }
    
    
    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de null sur findAllByParent(pParent).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findAllByParent(null) jette {@link ExceptionAppliParentNull} avec MSG_FINDALLBYPARENT_KO_PARAM_NULL.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findAllByParent(null) - ExceptionAppliParentNull")
    @Test
    public void testFindAllByParentNullExceptionAppliParentNull() throws Exception {
        assertThatThrownBy(() -> this.service.findAllByParent(null))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_FINDALLBYPARENT_KO_PARAM_NULL);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findById(nominal).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findById(trouvé) retourne un {@link Produit} non null.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_NOMINAL)
    @Test
    public void testFindByIdNominalOk() throws Exception {
        final Produit seed = this.service.findByLibelle(CHEMISE_ML_HOMME).get(0);
        final Produit relu = this.service.findById(seed.getIdProduit());

        assertThat(relu).isNotNull();
        assertThat(relu.getIdProduit()).isEqualTo(seed.getIdProduit());
        assertThat(relu.getProduit()).isEqualTo(CHEMISE_ML_HOMME);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de null sur findById(pId).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findById(null) jette {@link ExceptionAppliParamNull} avec MSG_FINDBYID_KO_PARAM_NULL.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(null) - ExceptionAppliParamNull")
    @Test
    public void testFindByIdNullExceptionAppliParamNull() throws Exception {
        assertThatThrownBy(() -> this.service.findById(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_FINDBYID_KO_PARAM_NULL);
    }

    // ===================== UPDATE =====================

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de null sur update(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>update(null) jette {@link ExceptionAppliParamNull} avec MSG_UPDATE_KO_PARAM_NULL.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_NULL)
    @Test
    public void testUpdateNullExceptionAppliParamNull() throws Exception {
        assertThatThrownBy(() -> this.service.update(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_UPDATE_KO_PARAM_NULL);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de libellé blank sur update(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>update(libellé blank) jette {@link ExceptionAppliLibelleBlank} avec MSG_UPDATE_KO_LIBELLE_BLANK.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_BLANK)
    @Test
    public void testUpdateLibelleBlankExceptionAppliLibelleBlank() throws Exception {
        final Produit seed = this.service.findByLibelle(CHEMISE_ML_HOMME).get(0);
        final Produit aModifier = new Produit(seed.getIdProduit(), BLANK, seed.getSousTypeProduit());

        assertThatThrownBy(() -> this.service.update(aModifier))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_UPDATE_KO_LIBELLE_BLANK);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de non-persistance sur update(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>update(id null) jette {@link ExceptionAppliParamNonPersistent} avec MSG_UPDATE_KO_NON_PERSISTENT.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(id null) - ExceptionAppliParamNonPersistent")
    @Test
    public void testUpdateIdNullExceptionAppliParamNonPersistent() throws Exception {
        // On récupère directement un produit existant pour obtenir son parent
        final List<Produit> produitsExistants = this.service.findByLibelle(CHEMISE_ML_HOMME);
        assertThat(produitsExistants).isNotEmpty();
        final SousTypeProduitI parent = produitsExistants.get(0).getSousTypeProduit();

        final Produit aModifier = new Produit();
        aModifier.setProduit(TEMP_PRODUIT_A_MODIFIER);
        aModifier.setSousTypeProduit(parent);
        // Pas d'ID pour simuler un objet non persistant

        assertThatThrownBy(() -> this.service.update(aModifier))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(MSG_UPDATE_KO_NON_PERSISTENT + TEMP_PRODUIT_A_MODIFIER);
    }


    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier update(nominal) avec modification du libellé.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>update(nominal) retourne un {@link Produit} avec libellé modifié.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_NOMINAL)
    @Test
    public void testUpdateNominalOk() throws Exception {
        final Produit seed = this.service.findByLibelle(CHEMISE_ML_HOMME).get(0);
        final Produit aModifier = new Produit(
            seed.getIdProduit(),
            seed.getProduit() + SUFFIX_MODIF,
            seed.getSousTypeProduit()
        );

        final Produit modifie = this.service.update(aModifier);

        assertThat(modifie).isNotNull();
        assertThat(modifie.getProduit()).isEqualTo(seed.getProduit() + SUFFIX_MODIF);

        final Produit relu = this.service.findById(seed.getIdProduit());
        assertThat(relu.getProduit()).isEqualTo(seed.getProduit() + SUFFIX_MODIF);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier update(nominal) avec changement de parent.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>update(parent modifié) retourne un {@link Produit} avec le nouveau parent.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_PARENT_MODIFIE)
    @Test
    public void testUpdateParentModifieOk() throws Exception {
        // Récupération d'un produit existant
        final List<Produit> produitsExistants = this.service.findByLibelle(CHEMISE_ML_HOMME);
        assertThat(produitsExistants).isNotEmpty();
        final Produit seed = produitsExistants.get(0);

        // Récupération d'un autre parent existant
        final List<Produit> autresProduits = this.service.findByLibelle(CHEMISE_MC_HOMME);
        assertThat(autresProduits).isNotEmpty();
        final SousTypeProduitI nouveauParent = autresProduits.get(0).getSousTypeProduit();

        final Produit aModifier = new Produit();
        aModifier.setIdProduit(seed.getIdProduit());
        aModifier.setProduit(seed.getProduit());
        aModifier.setSousTypeProduit(nouveauParent);

        final Produit modifie = this.service.update(aModifier);

        assertThat(modifie).isNotNull();
        assertThat(modifie.getSousTypeProduit().getIdSousTypeProduit())
            .isEqualTo(nouveauParent.getIdSousTypeProduit());

        final Produit relu = this.service.findById(seed.getIdProduit());
        assertThat(relu.getSousTypeProduit().getIdSousTypeProduit())
            .isEqualTo(nouveauParent.getIdSousTypeProduit());
    }
    
    // ===================== DELETE =====================

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de null sur delete(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>delete(null) jette {@link ExceptionAppliParamNull} avec MSG_DELETE_KO_PARAM_NULL.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(null) - ExceptionAppliParamNull")
    @Test
    public void testDeleteNullExceptionAppliParamNull() throws Exception {
        assertThatThrownBy(() -> this.service.delete(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_DELETE_KO_PARAM_NULL);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de non-persistance sur delete(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>delete(id null) jette {@link ExceptionAppliParamNonPersistent} avec MSG_DELETE_KO_ID_NULL.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(id null) - ExceptionAppliParamNonPersistent")
    @Test
    public void testDeleteIdNullExceptionAppliParamNonPersistent() throws Exception {
        final SousTypeProduitI parent = new SousTypeProduit();   
        parent.setSousTypeProduit(CHEMISE_ML_HOMME);
        final Produit aSupprimer = new Produit(null, TEMP_PRODUIT_A_SUPPRIMER, parent);

        assertThatThrownBy(() -> this.service.delete(aSupprimer))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(MSG_DELETE_KO_ID_NULL);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier delete(absent) ne supprime rien.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>delete(absent) n'échoue pas et ne modifie pas le count.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(absent) - ne fait rien")
    @Test
    public void testDeleteAbsentNeFaitRien() throws Exception {
        final long avant = this.service.count();

        // On récupère directement un produit existant pour obtenir son parent
        final List<Produit> produitsExistants = this.service.findByLibelle(CHEMISE_ML_HOMME);
        assertThat(produitsExistants).isNotEmpty();
        final SousTypeProduitI parent = produitsExistants.get(0).getSousTypeProduit();

        final Produit inexistant = new Produit();
        inexistant.setIdProduit(999L);
        inexistant.setProduit(INTROUVABLE);
        inexistant.setSousTypeProduit(parent);

        this.service.delete(inexistant);

        final long apres = this.service.count();
        assertThat(apres).isEqualTo(avant);
    }
    
    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Test d'intégration pour vérifier le comportement nominal de la suppression.</p>
     * <p style="font-weight:bold;">SCÉNARIO TESTÉ :</p>
     * <ol>
     * <li>Création d'un nouvel enregistrement</li>
     * <li>Suppression de l'enregistrement créé</li>
     * <li>Vérification physique de la suppression en base</li>
     * </ol>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_NOMINAL)
    @Test
    @Transactional(propagation = Propagation.NEVER)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeleteNominalOk() throws Exception {
        // On récupère directement un produit existant pour obtenir son parent
        final List<Produit> produitsExistants = this.service.findByLibelle(CHEMISE_ML_HOMME);
        assertThat(produitsExistants).isNotEmpty();
        final SousTypeProduitI parent = produitsExistants.get(0).getSousTypeProduit();

        final Produit aCreer = new Produit();
        aCreer.setProduit(TEMP_PRODUIT_A_SUPPRIMER);
        aCreer.setSousTypeProduit(parent);

        final Produit cree = this.service.creer(aCreer);

        assertThat(cree).isNotNull();
        assertThat(cree.getIdProduit()).isNotNull();

        // Vérification avant suppression
        final Integer countAvant = this.jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM PRODUITS WHERE ID_PRODUIT = ?",
            Integer.class, cree.getIdProduit()
        );
        assertThat(countAvant).isEqualTo(1);

        // Suppression
        this.service.delete(cree);

        // Vérification après suppression
        final Integer countApres = this.jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM PRODUITS WHERE ID_PRODUIT = ?",
            Integer.class, cree.getIdProduit()
        );
        assertThat(countApres).isEqualTo(0);

        // Vérification via service
        assertThat(this.service.findById(cree.getIdProduit())).isNull();
    }
    
    
}

