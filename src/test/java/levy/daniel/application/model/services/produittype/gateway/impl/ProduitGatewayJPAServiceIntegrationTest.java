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
import levy.daniel.application.model.metier.produittype.TypeProduit;
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
@ActiveProfiles({ "test-jpa" })
@Import(ProduitGatewayJPAService.class)
@ContextConfiguration(classes = ProduitGatewayJPAServiceIntegrationTest.ConfigTest.class)
public class ProduitGatewayJPAServiceIntegrationTest {

    // ************************* CONSTANTES ******************************/

    /**
     * <div>
     * <p>"test"</p>
     * </div>
     */
    public static final String PROFILE_TEST = "test";

    /**
     * <div>
     * <p>"classpath:truncate-test.sql"</p>
     * </div>
     */
    public static final String CLASSPATH_TRUNCATE_SQL 
    	= "classpath:truncate-test.sql";

    /**
     * <div>
     * <p>"classpath:data-test.sql"</p>
     * </div>
     */
    public static final String CLASSPATH_DATA_SQL 
    	= "classpath:data-test.sql";

    /**
     * <div>
     * <p>"ProduitGatewayJPAService"</p>
     * </div>
     */
    public static final String QUALIFIER_SERVICE 
    	= "ProduitGatewayJPAService";

    /**
     * <div>
     * <p>"servicesGateway-Creer"</p>
     * </div>
     */
    public static final String TAG_CREER = "servicesGateway-Creer";

    /**
     * <div>
     * <p>"servicesGateway-Rechercher"</p>
     * </div>
     */
    public static final String TAG_RECHERCHER = "servicesGateway-Rechercher";

    /**
     * <div>
     * <p>"servicesGateway-FindByObjetMetier"</p>
     * </div>
     */
    public static final String TAG_FINDBYOBJETMETIER 
    	= "servicesGateway-FindByObjetMetier";

    /**
     * <div>
     * <p>"servicesGateway-RechercherRapide"</p>
     * </div>
     */
    public static final String TAG_RECHERCHER_RAPIDE 
    	= "servicesGateway-RechercherRapide";

    /**
     * <div>
     * <p>"servicesGateway-Update"</p>
     * </div>
     */
    public static final String TAG_UPDATE = "servicesGateway-Update";

    /**
     * <div>
     * <p>"servicesGateway-Delete"</p>
     * </div>
     */
    public static final String TAG_DELETE = "servicesGateway-Delete";

    /**
     * <div>
     * <p>"servicesGateway-Count"</p>
     * </div>
     */
    public static final String TAG_COUNT = "servicesGateway-Count";

    /**
     * <div>
     * <p>"servicesGateway-Pagination"</p>
     * </div>
     */
    public static final String TAG_PAGINATION = "servicesGateway-Pagination";

    /**
     * <div>
     * <p>"servicesGateway-Beton"</p>
     * </div>
     */
    public static final String TAG_BETON = "servicesGateway-Beton";

    /**
     * <div>
     * <p>""</p>
     * </div>
     */
    public static final String CHAINE_VIDE = "";

    /**
     * <div>
     * <p>"   "</p>
     * </div>
     */
    public static final String BLANK = "   ";

    /**
     * <div>
     * <p>"chemise"</p>
     * </div>
     */
    public static final String CHEMISE = "chemise";

    /**
     * <div>
     * <p>"chemise à manches longues pour homme"</p>
     * </div>
     */
    public static final String CHEMISE_ML_HOMME 
    	= "chemise à manches longues pour homme";

    /**
     * <div>
     * <p>"chemise à manches courtes pour homme"</p>
     * </div>
     */
    public static final String CHEMISE_MC_HOMME 
    	= "chemise à manches courtes pour homme";

    /**
     * <div>
     * <p>"sweatshirt pour homme"</p>
     * </div>
     */
    public static final String SWEAT_HOMME = "sweatshirt pour homme";

    /**
     * <div>
     * <p>" (modifié)"</p>
     * </div>
     */
    public static final String SUFFIX_MODIF = " (modifié)";

    /**
     * <div>
     * <p>"temp-produit-a-supprimer"</p>
     * </div>
     */
    public static final String TEMP_PRODUIT_A_SUPPRIMER 
    	= "temp-produit-a-supprimer";

    /**
     * <div>
     * <p>"temp-produit-a-modifier"</p>
     * </div>
     */
    public static final String TEMP_PRODUIT_A_MODIFIER 
    	= "temp-produit-a-modifier";

    /**
     * <div>
     * <p>"###___introuvable___###"</p>
     * </div>
     */
    public static final String INTROUVABLE = "###___introuvable___###";

    /**
     * <div>
     * <p>"produit"</p>
     * </div>
     */
    public static final String PROP_TRI_PRODUIT = "produit";

    /**
     * <div>
     * <p>"creer(null) - jette ExceptionAppliParamNull (contrat du port)"</p>
     * </div>
     */
    public static final String DN_CREER_NULL 
    	= "creer(null) - jette ExceptionAppliParamNull (contrat du port)";

    /**
     * <div>
     * <p>"creer(blank) - jette ExceptionAppliLibelleBlank (contrat du port)"</p>
     * </div>
     */
    public static final String DN_CREER_BLANK 
    	= "creer(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /**
     * <div>
     * <p>"creer(parent null) - jette ExceptionAppliParentNull (contrat du port)"</p>
     * </div>
     */
    public static final String DN_CREER_PARENT_NULL 
    	= "creer(parent null) - jette ExceptionAppliParentNull (contrat du port)";

    /**
     * <div>
     * <p>"creer(nominal) - ajoute un élément, le rend retrouvable et ne wipe pas les seedés"</p>
     * </div>
     */
    public static final String DN_CREER_NOMINAL 
    	= "creer(nominal) - ajoute un élément, le rend retrouvable et ne wipe pas les seedés";

    /**
     * <div>
     * <p>"rechercherTous() - retourne la liste seedée (triée, sans doublons)"</p>
     * </div>
     */
    public static final String DN_RECHERCHER_TOUS 
    	= "rechercherTous() - retourne la liste seedée (triée, sans doublons)";

    /**
     * <div>
     * <p>"findByLibelle(inexistant) - retourne une liste vide"</p>
     * </div>
     */
    public static final String DN_FINDBYLIBELLE_INEXISTANT 
    	= "findByLibelle(inexistant) - retourne une liste vide";

    /**
     * <div>
     * <p>"findByLibelleRapide(contenu inexistant) - retourne une liste vide"</p>
     * </div>
     */
    public static final String DN_FINDBYLIBELLERAPIDE_INEXISTANT 
    	= "findByLibelleRapide(contenu inexistant) - retourne une liste vide";

    /**
     * <div>
     * <p>"update(parent modifié) - met à jour le parent"</p>
     * </div>
     */
    public static final String DN_UPDATE_PARENT_MODIFIE 
    	= "update(parent modifié) - met à jour le parent";

    /**
     * <div>
     * <p>"findAllByParent(nominal) - retourne les enfants du parent"</p>
     * </div>
     */
    public static final String DN_FINDALLBYPARENT_NOMINAL 
    	= "findAllByParent(nominal) - retourne les enfants du parent";

    /**
     * <div>
     * <p>"findById(nominal) - retourne l'objet métier correspondant"</p>
     * </div>
     */
    public static final String DN_FINDBYID_NOMINAL 
    	= "findById(nominal) - retourne l'objet métier correspondant";

    /**
     * <div>
     * <p>"update(nominal) - modifie le stockage et retourne l'objet modifié"</p>
     * </div>
     */
    public static final String DN_UPDATE_NOMINAL 
    	= "update(nominal) - modifie le stockage et retourne l'objet modifié";

    /** "update(null) - jette ExceptionAppliParamNull (contrat du port)" */
    public static final String DN_UPDATE_NULL 
    	= "update(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** "update(blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_UPDATE_BLANK 
    	= "update(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /**
     * <div>
     * <p>"delete(nominal) - supprime l'élément et le rend introuvable"</p>
     * </div>
     */
    public static final String DN_DELETE_NOMINAL 
    	= "delete(nominal) - supprime l'élément et le rend introuvable";

    /**
     * <div>
     * <p>"count() - cohérent avec le DAO"</p>
     * </div>
     */
    public static final String DN_COUNT_NOMINAL 
    	= "count() - cohérent avec le DAO";

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
    
    /** "creer(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_CREER_PARENT_LIBELLE_BLANK
        = "creer(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "creer(parent id null) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
    public static final String DN_CREER_PARENT_ID_NULL
        = "creer(parent id null) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** "creer(save jette Exception) - wrap en ExceptionTechniqueGateway (contrat du port)" */
    public static final String DN_CREER_SAVE_EXCEPTION
        = "creer(save jette Exception) - wrap en ExceptionTechniqueGateway (contrat du port)";

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - le parent de l'objet à créer a un libellé blank
	 * (null ou que des espaces)."</p>
	 * </div> 
     */
    public static final String MSG_CREER_KO_LIBELLE_PARENT_BLANK
        = ProduitGatewayIService.MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK;

    /** 
     * <div>
	 * <p>""Anomalie applicative - 
	 * le parent de l'objet que vous voulez créer n'existe
	 * pas déjà dans le stockage : "</p>
	 * </div> 
     */
    public static final String MSG_CREER_KO_PARENT_NON_PERSISTENT
        = ProduitGatewayIService.MESSAGE_CREER_KO_PARENT_NON_PERSISTENT;

    /** 
     * <div>
	 * <p>"Erreur Technique lors du stockage : "</p>
	 * </div> 
     */
    public static final String MSG_PREFIX_ERREUR_TECH
        = ProduitGatewayIService.ERREUR_TECHNIQUE_STOCKAGE;

    /** "X".repeat(10_000) */
    public static final String LIBELLE_TROP_LONG = "X".repeat(10_000);
    
    /**
     * <div>
	 * <p>"Anomalie applicative 
	 * - l'identifiant passé en paramètre est null."</p>
	 * </div>
     */
    public static final String MSG_FINDBYID_KO_PARAM_NULL 
    	= ProduitGatewayIService.MESSAGE_FINDBYID_KO_PARAM_NULL;

    /**
     * <div>
	 * <p>"Anomalie applicative 
	 * - l'objet métier passé en paramètre est null."</p>
	 * </div>
     */
    public static final String MSG_UPDATE_KO_PARAM_NULL 
    	= ProduitGatewayIService.MESSAGE_UPDATE_KO_PARAM_NULL;

    /**
     * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un libellé blank
	 * (null ou que des espaces)."</p>
	 * </div>
     */
    public static final String MSG_UPDATE_KO_LIBELLE_BLANK 
    	= ProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_BLANK;

    /**
     * <div>
	 * <p>"Anomalie applicative 
	 * - l'objet que vous voulez modifier n'est pas persistant
	 * (ID null) : "</p>
	 * </div>
     */
    public static final String MSG_UPDATE_KO_NON_PERSISTENT 
    	= ProduitGatewayIService.MESSAGE_UPDATE_KO_NON_PERSISTENT;

    /**
     * <div>
	 * <p>"Anomalie applicative 
	 * - l'objet métier passé en paramètre est null."</p>
	 * </div>
     */
    public static final String MSG_DELETE_KO_PARAM_NULL 
    	= ProduitGatewayIService.MESSAGE_DELETE_KO_PARAM_NULL;

    /**
     * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un ID null."</p>
	 * </div>
     */
    public static final String MSG_DELETE_KO_ID_NULL 
    	= ProduitGatewayIService.MESSAGE_DELETE_KO_ID_NULL;

    /**
     * <div>
	 * <p>"Anomalie applicative
	 * - le libellé passé en paramètre est un 
	 * libellé blank (null ou que des espaces)."</p>
	 * </div>
     */
    public static final String MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK 
    	= ProduitGatewayIService.MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK;

    /**
     * <div>
	 * <p>"Anomalie applicative 
	 * - le contenu passé en paramètre est null."</p>
	 * </div>
     */
    public static final String MSG_FINDBYLIBELLERAPIDE_KO_PARAM_NULL 
    	= ProduitGatewayIService.MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL;

    /**
     * <div>
	 * <p>"Anomalie applicative 
	 * - l'objet métier passé en paramètre est null."</p>
	 * </div>
     */
    public static final String MSG_FINDALLBYPARENT_KO_PARAM_NULL 
    	= ProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL;
    
    /** "findByObjetMetier(null) - jette ExceptionAppliParamNull (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_NULL 
    	= "findByObjetMetier(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** "findByObjetMetier(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_BLANK 
    	= "findByObjetMetier(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findByObjetMetier(parent null) - jette ExceptionAppliParentNull (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_PARENT_NULL 
    	= "findByObjetMetier(parent null) - jette ExceptionAppliParentNull (contrat du port)";

    /** "findByObjetMetier(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_PARENT_NON_PERSISTANT 
    	= "findByObjetMetier(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** "findByObjetMetier(nominal) - retourne l'objet métier correspondant" */
    public static final String DN_FINDBYOBJETMETIER_NOMINAL 
    	= "findByObjetMetier(nominal) - retourne l'objet métier correspondant";

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 *  - le paramètre pObject ne doit pas être null."</p>
	 * </div> 
     */
    public static final String MSG_FINDBYOBJETMETIER_KO_PARAM_NULL 
    	= ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 *  - le libellé de pObject passé en paramètre
	 *  ne doit pas être blank."</p>
	 * </div> 
     */
    public static final String MSG_FINDBYOBJETMETIER_KO_LIBELLE_BLANK 
    	= ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un parent null."</p>
	 * </div> 
     */
    public static final String MSG_FINDBYOBJETMETIER_KO_PARENT_NULL 
    	= ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL;

    /** 
     * <div>
	 * <p>"Anomalie applicative 
	 * - le parent de l'objet que vous voulez créer n'existe
	 * pas déjà dans le stockage : "</p>
	 * </div>
     */
    public static final String MSG_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTANT 
    	= ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT;
    
    /**
     * "SELECT COUNT(*) FROM PRODUITS WHERE ID_PRODUIT = ?"
     */
    public static final String SELECT_COUNT_FROM_PRODUITS_WHERE 
    	= "SELECT COUNT(*) FROM PRODUITS WHERE ID_PRODUIT = ?";

    // *************************** ATTRIBUTS *******************************/

    /**
     * <div>
     * <p>Locale par défaut = {@code Locale.getDefault()}</p>
     * </div>
     */
    public static final Locale LOCALE_DEFAUT = Locale.getDefault();

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
     * <p>DAO parent 
     * (accès aux IDs persistés pour préparer les objets métier).</p>
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


    
    // ************************ CONSTRUCTEUR *****************************/

    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
     * </div>
     */
    public ProduitGatewayJPAServiceIntegrationTest() {
        super();
    }

    
    
    // ===================== CONFIGURATION SPRING =======================//

    
    
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
    
    

    // =============================== TESTS ===============================
    
    
    
    // =============================== CREER ==============================



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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    
    
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
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de libellé blank du parent sur creer(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>creer(parent libellé blank) jette {@link ExceptionAppliLibelleBlank} avec MSG_CREER_KO_LIBELLE_PARENT_BLANK.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_LIBELLE_BLANK)
    @Test
    public void testCreerParentLibelleBlankExceptionAppliLibelleBlank() throws Exception {

        // Parent existant (seed) pour récupérer un ID persistant
        final List<Produit> produitsExistants = this.service.findByLibelle(CHEMISE_ML_HOMME);
        assertThat(produitsExistants).isNotEmpty();
        final SousTypeProduitI parentSeed = produitsExistants.get(0).getSousTypeProduit();

        final SousTypeProduit parentBlank = new SousTypeProduit();
        parentBlank.setIdSousTypeProduit(parentSeed.getIdSousTypeProduit());
        parentBlank.setSousTypeProduit(BLANK);

        final Produit aCreer = new Produit();
        aCreer.setProduit(TEMP_PRODUIT_A_SUPPRIMER);
        aCreer.setSousTypeProduit(parentBlank);

        assertThatThrownBy(() -> this.service.creer(aCreer))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_CREER_KO_LIBELLE_PARENT_BLANK);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Provoquer une erreur de stockage sur creer(pObject) pour vérifier le wrap.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>creer(save jette Exception) wrap en ExceptionTechniqueGateway avec prefix ERREUR_TECHNIQUE_STOCKAGE.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_SAVE_EXCEPTION)
    @Test
    public void testCreerDaoSaveJetteExceptionTechniqueGateway() throws Exception {

        // Parent existant (seed)
        final List<Produit> produitsExistants = this.service.findByLibelle(CHEMISE_ML_HOMME);
        assertThat(produitsExistants).isNotEmpty();
        final SousTypeProduitI parent = produitsExistants.get(0).getSousTypeProduit();

        // Libellé volontairement trop long pour faire échouer l'INSERT (contrainte longueur colonne)
        final Produit aCreer = new Produit();
        aCreer.setProduit(LIBELLE_TROP_LONG);
        aCreer.setSousTypeProduit(parent);

        assertThatThrownBy(() -> this.service.creer(aCreer))
            .isInstanceOf(levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionTechniqueGateway.class)
            .hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de non-persistance du parent (ID null) sur creer(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>creer(parent ID null) jette {@link ExceptionTechniqueGatewayNonPersistent} avec
     * MESSAGE_CREER_KO_PARENT_NON_PERSISTENT + libelléParent.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_ID_NULL)
    @Test
    public void testCreerParentIdNullExceptionTechniqueGatewayNonPersistent() throws Exception {

        final List<Produit> produitsExistants = this.service.findByLibelle(CHEMISE_ML_HOMME);
        assertThat(produitsExistants).isNotEmpty();
        final SousTypeProduitI parentSeed = produitsExistants.get(0).getSousTypeProduit();
        final String libelleParent = parentSeed.getSousTypeProduit();

        final SousTypeProduit parentIdNull = new SousTypeProduit();
        parentIdNull.setIdSousTypeProduit(null); // NON persistant
        parentIdNull.setSousTypeProduit(libelleParent);

        final Produit aCreer = new Produit();
        aCreer.setProduit(TEMP_PRODUIT_A_SUPPRIMER);
        aCreer.setSousTypeProduit(parentIdNull);

        assertThatThrownBy(() -> this.service.creer(aCreer))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(MSG_CREER_KO_PARENT_NON_PERSISTENT + libelleParent);

    } // __________________________________________________________________
    

    
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
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le fonctionnement nominal de creer(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>creer(nominal) retourne un {@link Produit} non null avec ID.</p>
     * <p style="font-weight:bold;">GARANTIES :</p>
     * <p>Le count() augmente de 1 et l'objet est retrouvable.</p>
     * <ul>
     * <li>Preuve “BD” : lecture SQL directe (JdbcTemplate) après l’appel (contourne Hibernate).</li>
     * <li>Test hors transaction de test : {@code @Transactional(NOT_SUPPORTED)}.</li>
     * <li>Nettoyage physique en finally (isolation), même si une assertion échoue.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_NOMINAL)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCreerNominalOk() throws Exception {
    	
        final long avant = this.service.count();

        // On récupère directement un produit existant pour obtenir son parent
        final List<Produit> produitsExistants = this.service.findByLibelle(CHEMISE_ML_HOMME);
        assertThat(produitsExistants).isNotEmpty();
        final SousTypeProduitI parent = produitsExistants.get(0).getSousTypeProduit();
        assertThat(parent).isNotNull();
        assertThat(parent.getIdSousTypeProduit()).isNotNull();
        final Long idParent = parent.getIdSousTypeProduit();

        final Produit aCreer = new Produit();
        aCreer.setProduit(TEMP_PRODUIT_A_SUPPRIMER);
        aCreer.setSousTypeProduit(parent);

        final Produit cree = this.service.creer(aCreer);

        assertThat(cree).isNotNull();
        assertThat(cree.getIdProduit()).isNotNull();
        assertThat(cree.getProduit()).isEqualTo(TEMP_PRODUIT_A_SUPPRIMER);

        final Long idCree = cree.getIdProduit();

        try {
        	
            final long apres = this.service.count();
            assertThat(apres).isEqualTo(avant + 1L);

            /* PREUVE BD INATTAQUABLE : lecture SQL directe (bypass Hibernate). */
            final Integer countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS_WHERE,
                Integer.class, idCree
            );
            assertThat(countEnBase)
                .as("La ligne doit exister physiquement en base après creer()")
                .isEqualTo(1);

            final String libelleEnBase = lireLibelleProduitEnBase(idCree);
            assertThat(libelleEnBase).isEqualTo(TEMP_PRODUIT_A_SUPPRIMER);

            final Long parentEnBase = lireIdParentEnBase(idCree);
            assertThat(parentEnBase).isEqualTo(idParent);

            /* Double-check via service (nouvelle transaction/requête). */
            this.entityManager.clear();
            final Produit relu = this.service.findById(idCree);
            assertThat(relu).isNotNull();
            assertThat(relu.getProduit()).isEqualTo(TEMP_PRODUIT_A_SUPPRIMER);

        } finally {

            /* Nettoyage physique : le test n'est pas rollbacké (NOT_SUPPORTED). */
            if (idCree != null) {
                this.jdbcTemplate.update(
                    "DELETE FROM PRODUITS WHERE ID_PRODUIT = ?",
                    idCree
                );
            }
            this.entityManager.clear();

        }
        
    } // __________________________________________________________________
    
    
    
    // ======================== RechercherTous ============================



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
        
    } // __________________________________________________________________
    
    

    // ===================== rechercherTousParPage =====================



    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier rechercherTousParPage(null) -> requête par défaut.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>Résultat non null, content non null, pageNumber/pageSize = valeurs par défaut.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>totalElements cohérent (strictement positif sur base seedée).</p>
     * </div>
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(null) - requête par défaut")
    @Test
    public void testRechercherTousParPageNullNominalOk() throws Exception {

        final levy.daniel.application.model.services.produittype.pagination.ResultatPage<Produit> page =
            this.service.rechercherTousParPage(null);

        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotNull();

        assertThat(page.getPageNumber())
            .isEqualTo(levy.daniel.application.model.services.produittype.pagination.RequetePage.PAGE_DEFAUT);
        assertThat(page.getPageSize())
            .isEqualTo(levy.daniel.application.model.services.produittype.pagination.RequetePage.TAILLE_DEFAUT);

        assertThat(page.getTotalElements()).isPositive();

    } // __________________________________________________________________



    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier rechercherTousParPage(avec tri) sur le champ "produit".</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>Résultat non null, contenu non vide, tri respecté.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Le contenu est trié selon les spécifications (CASE_INSENSITIVE).</p>
     * </div>
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(avec tri) - tri sur produit ASC")
    @Test
    public void testRechercherTousParPageAvecTriOk() throws Exception {

        final java.util.List<levy.daniel.application.model.services.produittype.pagination.TriSpec> tris =
            new java.util.ArrayList<levy.daniel.application.model.services.produittype.pagination.TriSpec>();

        tris.add(new levy.daniel.application.model.services.produittype.pagination.TriSpec(
            PROP_TRI_PRODUIT,
            levy.daniel.application.model.services.produittype.pagination.DirectionTri.ASC));

        final levy.daniel.application.model.services.produittype.pagination.RequetePage requete =
            new levy.daniel.application.model.services.produittype.pagination.RequetePage(0, 5, tris);

        final levy.daniel.application.model.services.produittype.pagination.ResultatPage<Produit> page =
            this.service.rechercherTousParPage(requete);

        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotNull().isNotEmpty();
        assertThat(page.getContent())
            .extracting(Produit::getProduit)
            .isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);

    } // __________________________________________________________________



    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier rechercherTousParPage(page très au-delà du nombre total).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>Contenu vide mais totalElements cohérent.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>totalElements = taille de rechercherTous().</p>
     * </div>
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(hors bornes) - contenu vide, total cohérent")
    @Test
    public void testRechercherTousParPageHorsBornesOk() throws Exception {

        final List<Produit> tous = this.service.rechercherTous();

        final levy.daniel.application.model.services.produittype.pagination.RequetePage requete =
            new levy.daniel.application.model.services.produittype.pagination.RequetePage(
                9999, 10, new java.util.ArrayList<>());

        final levy.daniel.application.model.services.produittype.pagination.ResultatPage<Produit> page =
            this.service.rechercherTousParPage(requete);

        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotNull().isEmpty();
        assertThat(page.getTotalElements()).isEqualTo(tous.size());

    } // __________________________________________________________________
    
    
    
    // ======================== findByObjetMetier =========================



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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    

    
    // ========================== findByLibelle ===========================



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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

    // ======================== findByLibelleRapide =======================



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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    
    
    // ========================= findAllByParent ==========================



    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findAllByParent(nominal).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findAllByParent(nominal) retourne une liste non vide.</p>
     * <p style="font-weight:bold;">GARANTIES :</p>
     * <ul>
     * <li>Preuve “BD” : lecture SQL directe (JdbcTemplate) après l’appel (contourne Hibernate).</li>
     * <li>Test hors transaction de test : {@code @Transactional(NOT_SUPPORTED)}.</li>
     * <li>entityManager.clear() avant l’appel pour éviter les illusions de cache.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_NOMINAL)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testFindAllByParentNominalOk() throws Exception {
    	
        /* Détermine un parent existant via SQL (bypass service/cache). */
        final List<Long> idsParents = this.jdbcTemplate.queryForList(
            "SELECT SOUS_TYPE_PRODUIT FROM PRODUITS WHERE PRODUIT = ?",
            Long.class, CHEMISE_ML_HOMME
        );
        assertThat(idsParents).isNotNull().isNotEmpty();

        final Long idParent = idsParents.get(0);
        assertThat(idParent).isNotNull();

        final List<String> libellesParents = this.jdbcTemplate.queryForList(
            "SELECT SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?",
            String.class, idParent
        );
        assertThat(libellesParents).isNotNull().isNotEmpty();

        final String libelleParent = libellesParents.get(0);
        assertThat(libelleParent).isNotNull();

        final SousTypeProduit parent = new SousTypeProduit();
        parent.setIdSousTypeProduit(idParent);
        parent.setSousTypeProduit(libelleParent);

        /* Neutralise toute illusion de persistence context avant l’appel. */
        this.entityManager.clear();

        final List<Produit> enfants = this.service.findAllByParent(parent);

        assertThat(enfants).isNotNull().isNotEmpty();

        /* Contrôles métier minimaux sur les objets retournés. */
        assertThat(enfants)
            .allSatisfy(p -> {
                assertThat(p).isNotNull();
                assertThat(p.getProduit()).isNotNull().isNotBlank();
                assertThat(p.getSousTypeProduit()).isNotNull();
                assertThat(p.getSousTypeProduit().getIdSousTypeProduit()).isEqualTo(idParent);
            });

        /* PREUVE BD INATTAQUABLE : résultat = projection SQL (case-insensitive) triée. */
        final Integer countDistinctEnBase = this.jdbcTemplate.queryForObject(
            "SELECT COUNT(DISTINCT LOWER(PRODUIT)) FROM PRODUITS WHERE SOUS_TYPE_PRODUIT = ?",
            Integer.class, idParent
        );
        assertThat(countDistinctEnBase).isNotNull();
        assertThat(enfants)
            .as("La liste retournée doit correspondre au contenu physique en base (dédoublonnage métier).")
            .hasSize(countDistinctEnBase.intValue());

        final List<String> libellesEnBase = this.jdbcTemplate.queryForList(
            "SELECT PRODUIT FROM PRODUITS WHERE SOUS_TYPE_PRODUIT = ? ORDER BY LOWER(PRODUIT), PRODUIT",
            String.class, idParent
        );
        assertThat(libellesEnBase).isNotNull().isNotEmpty();

        /* Normalise + dédoublonne SQL selon l’égalité métier (case-insensitive sur PRODUIT). */
        final List<String> attendusNormalises = new java.util.ArrayList<>();
        for (final String libelle : libellesEnBase) {
            if (libelle != null) {
                final String normalise = libelle.toLowerCase(LOCALE_DEFAUT);
                if (!attendusNormalises.contains(normalise)) {
                    attendusNormalises.add(normalise);
                }
            }
        }

        /* Normalise + dédoublonne la sortie service (case-insensitive sur PRODUIT). */
        final List<String> trouvesNormalises = new java.util.ArrayList<>();
        for (final Produit enfant : enfants) {
            final String libelle = enfant.getProduit();
            final String normalise = libelle.toLowerCase(LOCALE_DEFAUT);
            if (!trouvesNormalises.contains(normalise)) {
                trouvesNormalises.add(normalise);
            }
        }

        assertThat(trouvesNormalises)
            .as("La liste retournée par le service doit être identique (contenu + ordre) à la projection SQL.")
            .isEqualTo(attendusNormalises);
        
    } // __________________________________________________________________
    
    
    
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
        
    } // __________________________________________________________________
    
    
    
    // ============================== findById ============================



    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findById(nominal).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findById(trouvé) retourne un {@link Produit} non null.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <ul>
     * <li>Preuve “BD” : l'ID et les valeurs attendues sont lues 
     * en SQL direct (JdbcTemplate), puis comparées au retour service.</li>
     * <li>entityManager.clear() avant l’appel pour éviter 
     * toute illusion de cache/persistence context.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_NOMINAL)
    @Test
    public void testFindByIdNominalOk() throws Exception {
    	
        /* Détermine un ID existant via SQL (bypass service/cache). */
        final List<Long> ids = this.jdbcTemplate.queryForList(
            "SELECT ID_PRODUIT FROM PRODUITS WHERE PRODUIT = ?",
            Long.class, CHEMISE_ML_HOMME
        );
        assertThat(ids).isNotNull().isNotEmpty();

        final Long id = ids.get(0);
        assertThat(id).isNotNull();

        /* Attendus SQL (preuve BD). */
        final String libelleAttendu = lireLibelleProduitEnBase(id);
        assertThat(libelleAttendu).isEqualTo(CHEMISE_ML_HOMME);

        final Long idParentAttendu = lireIdParentEnBase(id);
        assertThat(idParentAttendu).isNotNull();

        final String libelleParentAttendu = this.jdbcTemplate.queryForObject(
            "SELECT SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?",
            String.class, idParentAttendu
        );
        assertThat(libelleParentAttendu).isNotNull();

        /* Anti-illusion cache avant l’appel. */
        this.entityManager.clear();

        final Produit relu = this.service.findById(id);

        assertThat(relu).isNotNull();
        assertThat(relu.getIdProduit()).isEqualTo(id);
        assertThat(relu.getProduit()).isEqualTo(libelleAttendu);
        assertThat(relu.getSousTypeProduit()).isNotNull();
        assertThat(relu.getSousTypeProduit().getIdSousTypeProduit()).isEqualTo(idParentAttendu);
        assertThat(relu.getSousTypeProduit().getSousTypeProduit()).isEqualTo(libelleParentAttendu);
        
    } // __________________________________________________________________    
    

    
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
        
    } // __________________________________________________________________
    

    
    // =============================== update =============================



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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    


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
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testUpdateNominalOk() throws Exception {
    	
        final Produit seed = this.service.findByLibelle(CHEMISE_ML_HOMME).get(0);
        assertThat(seed).isNotNull();
        assertThat(seed.getIdProduit()).isNotNull();

        final Long id = seed.getIdProduit();

        /* Lecture physique en base AVANT update. */
        final String libelleAvant = lireLibelleProduitEnBase(id);
        final Long parentAvant = lireIdParentEnBase(id);

        final String nouveauLibelle = libelleAvant + SUFFIX_MODIF;

        try {

            final Produit aModifier = new Produit(
                id,
                nouveauLibelle,
                seed.getSousTypeProduit()
            );

            final Produit modifie = this.service.update(aModifier);

            assertThat(modifie).isNotNull();
            assertThat(modifie.getProduit()).isEqualTo(nouveauLibelle);

            /* Preuve inattaquable : lecture physique en base APRES update. */
            final String libelleEnBase = lireLibelleProduitEnBase(id);
            assertThat(libelleEnBase)
                .as("La colonne PRODUIT doit être physiquement mise à jour en base.")
                .isEqualTo(nouveauLibelle);

            /* Double-check via service (nouvelle transaction/requête). */
            final Produit relu = this.service.findById(id);
            assertThat(relu).isNotNull();
            assertThat(relu.getProduit()).isEqualTo(nouveauLibelle);

        } finally {

            /* Isolation : restauration en base, même si le test échoue. */
            restaurerProduitEnBase(id, libelleAvant, parentAvant);
            this.entityManager.clear();

        }
        
    } // __________________________________________________________________    

    

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
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testUpdateParentModifieOk() throws Exception {
    	
        /* Produit seed (homme). */
        final List<Produit> produitsExistants = this.service.findByLibelle(CHEMISE_ML_HOMME);
        assertThat(produitsExistants).isNotEmpty();
        final Produit seed = produitsExistants.get(0);
        assertThat(seed.getIdProduit()).isNotNull();

        final Long idProduit = seed.getIdProduit();

        /* Lecture physique en base AVANT update. */
        final String libelleAvant = lireLibelleProduitEnBase(idProduit);
        final Long parentAvant = lireIdParentEnBase(idProduit);

        /* Nouveau parent = "vêtement pour femme" (différent du parent homme). */
        final String libelleNouveauParent = "vêtement pour femme";
        final Long idNouveauParent = retrouverIdParentPersistantParLibelleEnBase(libelleNouveauParent);

        assertThat(idNouveauParent).isNotNull();
        assertThat(idNouveauParent)
            .as("Le nouveau parent doit être différent du parent actuel.")
            .isNotEqualTo(parentAvant);

        final SousTypeProduit nouveauParent = new SousTypeProduit();
        nouveauParent.setIdSousTypeProduit(idNouveauParent);
        nouveauParent.setSousTypeProduit(libelleNouveauParent);

        try {

            final Produit aModifier = new Produit();
            aModifier.setIdProduit(idProduit);
            aModifier.setProduit(seed.getProduit());
            aModifier.setSousTypeProduit(nouveauParent);

            final Produit modifie = this.service.update(aModifier);

            assertThat(modifie).isNotNull();
            assertThat(modifie.getSousTypeProduit()).isNotNull();
            assertThat(modifie.getSousTypeProduit().getIdSousTypeProduit())
                .isEqualTo(idNouveauParent);

            /* Preuve inattaquable : lecture physique en base APRES update. */
            final Long parentEnBase = lireIdParentEnBase(idProduit);
            assertThat(parentEnBase)
                .as("La FK SOUS_TYPE_PRODUIT doit être physiquement mise à jour en base.")
                .isEqualTo(idNouveauParent);

            /* Double-check via service (nouvelle transaction/requête). */
            final Produit relu = this.service.findById(idProduit);
            assertThat(relu).isNotNull();
            assertThat(relu.getSousTypeProduit()).isNotNull();
            assertThat(relu.getSousTypeProduit().getIdSousTypeProduit())
                .isEqualTo(idNouveauParent);

        } finally {

            /* Isolation : restauration en base, même si le test échoue. */
            restaurerProduitEnBase(idProduit, libelleAvant, parentAvant);
            this.entityManager.clear();

        }
        
    } // __________________________________________________________________
    

    
    // ============================= delete ===============================



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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Test d'intégration “béton” : prouver que delete(nominal) retire physiquement la ligne en base.</p>
     * <p style="font-weight:bold;">SCÉNARIO TESTÉ :</p>
     * <ol>
     * <li>Détermination d'un parent existant via SQL (JdbcTemplate) (bypass service/cache).</li>
     * <li>Reconstruction complète du parent (SousTypeProduit + TypeProduit) pour satisfaire le convertisseur METIER→JPA.</li>
     * <li>Création d'un nouvel enregistrement.</li>
     * <li>Vérification physique en base avant suppression (COUNT(*)=1).</li>
     * <li>Suppression via le service.</li>
     * <li>Vérification physique en base après suppression (COUNT(*)=0).</li>
     * </ol>
     * <p style="font-weight:bold;">GARANTIES :</p>
     * <ul>
     * <li>Exécution hors transaction de test : {@code @Transactional(NOT_SUPPORTED)}.</li>
     * <li>Nettoyage physique en finally (isolation), même si une assertion échoue.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_NOMINAL)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeleteNominalOk() throws Exception {

        Long idCree = null;

        try {

            /* Parent via SQL direct (bypass service/cache). */
            final List<Long> idsParents = this.jdbcTemplate.queryForList(
                "SELECT SOUS_TYPE_PRODUIT FROM PRODUITS WHERE PRODUIT = ?",
                Long.class, CHEMISE_ML_HOMME
            );
            assertThat(idsParents).isNotNull().isNotEmpty();

            final Long idSousTypeProduit = idsParents.get(0);
            assertThat(idSousTypeProduit).isNotNull();

            final String libelleSousTypeProduit = this.jdbcTemplate.queryForObject(
                "SELECT SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?",
                String.class, idSousTypeProduit
            );
            assertThat(libelleSousTypeProduit).isNotNull();

            /* IMPORTANT : reconstruire aussi le TypeProduit (parent du SousTypeProduit). */
            final Long idTypeProduit = this.jdbcTemplate.queryForObject(
                "SELECT TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?",
                Long.class, idSousTypeProduit
            );
            assertThat(idTypeProduit).isNotNull();

            final String libelleTypeProduit = this.jdbcTemplate.queryForObject(
                "SELECT TYPE_PRODUIT FROM TYPES_PRODUIT WHERE ID_TYPE_PRODUIT = ?",
                String.class, idTypeProduit
            );
            assertThat(libelleTypeProduit).isNotNull();

            final TypeProduit typeProduit = new TypeProduit();
            typeProduit.setIdTypeProduit(idTypeProduit);
            typeProduit.setTypeProduit(libelleTypeProduit);

            final SousTypeProduit sousTypeProduit = new SousTypeProduit();
            sousTypeProduit.setIdSousTypeProduit(idSousTypeProduit);
            sousTypeProduit.setSousTypeProduit(libelleSousTypeProduit);
            sousTypeProduit.setTypeProduit(typeProduit);

            final Produit aCreer = new Produit();
            aCreer.setProduit(TEMP_PRODUIT_A_SUPPRIMER);
            aCreer.setSousTypeProduit(sousTypeProduit);

            final Produit cree = this.service.creer(aCreer);

            assertThat(cree).isNotNull();
            assertThat(cree.getIdProduit()).isNotNull();
            idCree = cree.getIdProduit();

            /* Vérification SQL avant suppression. */
            final Integer countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS_WHERE,
                Integer.class, idCree
            );
            assertThat(countAvant).isEqualTo(1);

            /* Anti-illusion cache avant delete + après. */
            this.entityManager.clear();

            /* Suppression. */
            this.service.delete(cree);

            this.entityManager.clear();

            /* Vérification SQL après suppression : preuve BD. */
            final Integer countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS_WHERE,
                Integer.class, idCree
            );
            assertThat(countApres).isEqualTo(0);

            /* Oracle secondaire (service). */
            assertThat(this.service.findById(idCree)).isNull();

        } finally {

            /* Cleanup physique (au cas où la suppression aurait échoué). */
            if (idCree != null) {
                this.jdbcTemplate.update(
                    "DELETE FROM PRODUITS WHERE ID_PRODUIT = ?",
                    idCree
                );
            }
            this.entityManager.clear();

        }

    } // __________________________________________________________________
    
    

    // ============================== Count ===============================



    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Test d'intégration “béton” : prouver que count() 
     * reflète exactement le nombre de lignes en base.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>count() retourne le nombre total de {@link Produit} persistés.</p>
     * <p style="font-weight:bold;">GARANTIES :</p>
     * <ul>
     * <li>Preuve “BD” : lecture SQL directe 
     * via {@link JdbcTemplate} (bypass Hibernate).</li>
     * <li>entityManager.clear() avant l’appel pour éviter 
     * toute illusion de cache/persistence context.</li>
     * <li>Test hors transaction de test : 
     * {@code @Transactional(NOT_SUPPORTED)}.</li>
     * </ul>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName(DN_COUNT_NOMINAL)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCountNominalOk() throws Exception {

        /* Anti-illusion cache avant l’appel. */
        this.entityManager.clear();

        final long countService = this.service.count();

        final Long countSql = this.jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM PRODUITS",
            Long.class
        );
        assertThat(countSql).isNotNull();

        assertThat(countService)
            .as("count() doit être strictement cohérent avec SELECT COUNT(*) FROM PRODUITS")
            .isEqualTo(countSql.longValue());

    } // __________________________________________________________________
    
    
    
    // ============================ OUTILS ================================



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
        
    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>Vérifie la suppression physique en base via JdbcTemplate.</p>
     * </div>
     *
     * @param pId Long ID de l'entité supprimée
     */
    private void verifierSuppressionEnBase(final Long pId) {
    	
        final Integer count = this.jdbcTemplate.queryForObject(
            SELECT_COUNT_FROM_PRODUITS_WHERE, // NOPMD by danyl on 26/02/2026 15:18
            Integer.class, pId
        );
        assertThat(count)
            .as("L'enregistrement doit être physiquement supprimé de la base")
            .isEqualTo(0);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>Lit le libellé PRODUIT physiquement en base (bypass Hibernate).</p>
     * </div>
     *
     * @param pId Long : ID_PRODUIT.
     * @return String : valeur de la colonne PRODUIT.
     */
    private String lireLibelleProduitEnBase(final Long pId) {
    	
        return this.jdbcTemplate.queryForObject(
            "SELECT PRODUIT FROM PRODUITS WHERE ID_PRODUIT = ?",
            String.class, pId
        );
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Lit l'ID du parent (SOUS_TYPE_PRODUIT) physiquement en base (bypass Hibernate).</p>
     * </div>
     *
     * @param pId Long : ID_PRODUIT.
     * @return Long : valeur de la colonne SOUS_TYPE_PRODUIT.
     */
    private Long lireIdParentEnBase(final Long pId) {
    	
        return this.jdbcTemplate.queryForObject(
            "SELECT SOUS_TYPE_PRODUIT FROM PRODUITS WHERE ID_PRODUIT = ?",
            Long.class, pId
        );
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Retrouve l'ID d'un SousTypeProduit persistant par son libellé (physiquement en base).</p>
     * </div>
     *
     * @param pLibelleParent String : SOUS_TYPE_PRODUIT.
     * @return Long : ID_SOUS_TYPE_PRODUIT.
     */
    private Long retrouverIdParentPersistantParLibelleEnBase(final String pLibelleParent) {
    	
        return this.jdbcTemplate.queryForObject(
            "SELECT ID_SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE SOUS_TYPE_PRODUIT = ?",
            Long.class, pLibelleParent
        );
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Restaure physiquement en base un Produit (libellé + parent) pour garantir l'isolation des tests.</p>
     * </div>
     *
     * @param pId Long : ID_PRODUIT.
     * @param pLibelle String : PRODUIT.
     * @param pIdParent Long : SOUS_TYPE_PRODUIT.
     */
    private void restaurerProduitEnBase(
            final Long pId,
            final String pLibelle,
            final Long pIdParent) {

        final int updated = this.jdbcTemplate.update(
            "UPDATE PRODUITS SET PRODUIT = ?, SOUS_TYPE_PRODUIT = ? WHERE ID_PRODUIT = ?",
            pLibelle, pIdParent, pId
        );

        assertThat(updated)
            .as("La restauration en base doit modifier exactement 1 ligne.")
            .isEqualTo(1);
        
    } // __________________________________________________________________


    
} // FIN DE LA CLASSE ProduitGatewayJPAServiceIntegrationTest.-------------

