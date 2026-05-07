package levy.daniel.application.model.services.produittype.gateway.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
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
import levy.daniel.application.model.services.produittype.pagination.DirectionTri;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;
import levy.daniel.application.model.services.produittype.pagination.TriSpec;
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
	 * <p>"produit"</p>
	 * </div>
	 */
	public static final String PROP_TRI_PRODUIT = "produit";

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

	/** 999_999L */
	public static final Long ID_INEXISTANT = Long.valueOf(999_999L);
	
	/**
	 * "idProduit"
	 */
	public static final String IDPRODUIT = "idProduit";

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

	/** "creer(libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)" */
	public static final String DN_CREER_LIBELLE_NULL
	    = "creer(libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)";

	/** "creer(parent libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)" */
	public static final String DN_CREER_PARENT_LIBELLE_NULL
	    = "creer(parent libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)";

	/** "creer(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
	public static final String DN_CREER_PARENT_LIBELLE_BLANK
	    = "creer(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

	/**
	 * <div>
	 * <p>"creer(parent null) - jette ExceptionAppliParentNull (contrat du port)"</p>
	 * </div>
	 */
	public static final String DN_CREER_PARENT_NULL 
		= "creer(parent null) - jette ExceptionAppliParentNull (contrat du port)";

	/** "creer(parent id null) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
	public static final String DN_CREER_PARENT_ID_NULL
	    = "creer(parent id null) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

	/** "creer(parent absent) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
	public static final String DN_CREER_PARENT_ABSENT
	    = "creer(parent absent) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

	/** "creer(save jette Exception) - wrap en ExceptionTechniqueGateway (contrat du port)" */
	public static final String DN_CREER_SAVE_EXCEPTION
	    = "creer(save jette Exception) - wrap en ExceptionTechniqueGateway (contrat du port)";

	/** "creer(doublon) - jette UnexpectedRollbackException et ne crée aucun nouvel enregistrement" */
	public static final String DN_CREER_DOUBLON
	    = "creer(doublon) - jette UnexpectedRollbackException et ne crée aucun nouvel enregistrement";

	/**
	 * <div>
	 * <p>"creer(nominal) - ajoute un élément, le rend retrouvable et ne wipe pas les seedés"</p>
	 * </div>
	 */
	public static final String DN_CREER_NOMINAL 
		= "creer(nominal) - ajoute un élément, le rend retrouvable et ne wipe pas les seedés";

	/** "creer(plusieurs créations) : crée plusieurs objets métier distincts et tous retrouvables" */
	public static final String DN_CREER_PLUSIEURS
	    = "creer(plusieurs créations) : crée plusieurs objets métier distincts et tous retrouvables";

	/**
     * <div>
     * <p>"rechercherTous(stockage seedé) - retourne exactement l'état du stockage trié et sans doublon"</p>
     * </div>
     */
    public static final String DN_RECHERCHER_TOUS
        = "rechercherTous(stockage seedé) - retourne exactement l'état du stockage trié et sans doublon";
 
    /**
     * <div>
     * <p>"rechercherTous(stockage vide) - retourne une liste vide non null"</p>
     * </div>
     */
    public static final String DN_RECHERCHER_TOUS_STOCKAGE_VIDE
        = "rechercherTous(stockage vide) - retourne une liste vide non null";

    /**
     * <div>
     * <p>"rechercherTousParPage(null) - applique la pagination par défaut et reste cohérent avec le stockage"</p>
     * </div>
     */
    public static final String DN_RECHERCHER_TOUS_PAR_PAGE_NULL
        = "rechercherTousParPage(null) - applique la pagination par défaut et reste cohérent avec le stockage";

    /**
     * <div>
     * <p>"rechercherTousParPage(avec tri) - respecte le tri demandé sur produit"</p>
     * </div>
     */
    public static final String DN_RECHERCHER_TOUS_PAR_PAGE_TRI
        = "rechercherTousParPage(avec tri) - respecte le tri demandé sur produit";

    /**
     * <div>
     * <p>"rechercherTousParPage(stockage vide) - retourne une page vide cohérente"</p>
     * </div>
     */
    public static final String DN_RECHERCHER_TOUS_PAR_PAGE_VIDE
        = "rechercherTousParPage(stockage vide) - retourne une page vide cohérente";

    /**
     * <div>
     * <p>"rechercherTousParPage(taille supérieure au total) - retourne tous les objets métier disponibles"</p>
     * </div>
     */
    public static final String DN_RECHERCHER_TOUS_PAR_PAGE_TAILLE_SUPERIEURE
        = "rechercherTousParPage(taille supérieure au total) - retourne tous les objets métier disponibles";

    /**
     * <div>
     * <p>"rechercherTousParPage(page hors bornes) - retourne une page vide"</p>
     * </div>
     */
    public static final String DN_RECHERCHER_TOUS_PAR_PAGE_HORS_BORNE
        = "rechercherTousParPage(page hors bornes) - retourne une page vide";

    /**
     * <div>
     * <p>"rechercherTousParPage(taille zéro) - normalise la taille et retourne la première page cohérente"</p>
     * </div>
     */
    public static final String DN_RECHERCHER_TOUS_PAR_PAGE_TAILLE_ZERO
        = "rechercherTousParPage(taille zéro) - normalise la taille et retourne la première page cohérente";

    /**
     * <div>
     * <p>"rechercherTousParPage(nominal) - retourne la page demandée cohérente avec le stockage"</p>
     * </div>
     */
    public static final String DN_RECHERCHER_TOUS_PAR_PAGE_NOMINAL
        = "rechercherTousParPage(nominal) - retourne la page demandée cohérente avec le stockage";
    
    /** "findByObjetMetier(null) - jette ExceptionAppliParamNull (contrat du port)" */
	public static final String DN_FINDBYOBJETMETIER_NULL 
		= "findByObjetMetier(null) - jette ExceptionAppliParamNull (contrat du port)";

	/** "findByObjetMetier(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
	public static final String DN_FINDBYOBJETMETIER_BLANK 
		= "findByObjetMetier(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findByObjetMetier(libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_LIBELLE_NULL
        = "findByObjetMetier(libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findByObjetMetier(parent libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_PARENT_LIBELLE_NULL
        = "findByObjetMetier(parent libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findByObjetMetier(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_PARENT_LIBELLE_BLANK
        = "findByObjetMetier(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findByObjetMetier(parent id null) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_PARENT_ID_NULL
        = "findByObjetMetier(parent id null) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** "findByObjetMetier(parent absent) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_PARENT_ABSENT
        = "findByObjetMetier(parent absent) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** "findByObjetMetier(non trouvé) - retourne null sans modifier le stockage" */
    public static final String DN_FINDBYOBJETMETIER_NON_TROUVE
        = "findByObjetMetier(non trouvé) - retourne null sans modifier le stockage";

    /** "findByObjetMetier(béton) - ignore l'id de la sonde et recherche sans tenir compte de la casse" */
    public static final String DN_FINDBYOBJETMETIER_BETON
        = "findByObjetMetier(béton) - ignore l'id de la sonde et recherche sans tenir compte de la casse";

    /** "findByObjetMetier(parent null) - jette ExceptionAppliParentNull (contrat du port)" */
	public static final String DN_FINDBYOBJETMETIER_PARENT_NULL 
		= "findByObjetMetier(parent null) - jette ExceptionAppliParentNull (contrat du port)";

	/** "findByObjetMetier(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
	public static final String DN_FINDBYOBJETMETIER_PARENT_NON_PERSISTANT 
		= "findByObjetMetier(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

	/** "findByObjetMetier(nominal) - retourne l'objet métier correspondant" */
	public static final String DN_FINDBYOBJETMETIER_NOMINAL 
		= "findByObjetMetier(nominal) - retourne l'objet métier correspondant";

    /** "findByLibelle(null) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDBYLIBELLE_NULL
        = "findByLibelle(null) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findByLibelle(blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDBYLIBELLE_BLANK
        = "findByLibelle(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findByLibelle(non trouvé) - retourne une liste vide" */
    public static final String DN_FINDBYLIBELLE_NON_TROUVE
        = "findByLibelle(non trouvé) - retourne une liste vide";

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

	/** "findByLibelle(case-insensitive) - retrouve le libellé malgré une casse différente" */
    public static final String DN_FINDBYLIBELLE_CASE_INSENSITIVE
        = "findByLibelle(case-insensitive) - retrouve le libellé malgré une casse différente";

    /** "findByLibelle(OK) - retourne les correspondances exactes" */
    public static final String DN_FINDBYLIBELLE_NOMINAL
        = "findByLibelle(OK) - retourne les correspondances exactes";
    
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
	 * <p>"update(parent modifié) - met à jour le parent"</p>
	 * </div>
	 */
	public static final String DN_UPDATE_PARENT_MODIFIE 
		= "update(parent modifié) - met à jour le parent";

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
	 * <p>Message d'erreur : MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK.</p>
	 * </div>
	 */
	public static final String MSG_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK
	    = ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK;

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
    
    /**
     * <div>
     * <p>Projection SQL complète des Produits avec leur parent
     * SousTypeProduit et le parent TypeProduit, ordonnée par libellé Produit.</p>
     * </div>
     */
    public static final String SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_TRI_PRODUIT_ASC
        = """
          SELECT
              p.ID_PRODUIT AS ID_PRODUIT,
              p.PRODUIT AS PRODUIT,
              p.SOUS_TYPE_PRODUIT AS ID_SOUS_TYPE_PRODUIT,
              stp.SOUS_TYPE_PRODUIT AS SOUS_TYPE_PRODUIT,
              stp.TYPE_PRODUIT AS ID_TYPE_PRODUIT,
              tp.TYPE_PRODUIT AS TYPE_PRODUIT
          FROM PRODUITS p
          JOIN SOUS_TYPES_PRODUIT stp
              ON p.SOUS_TYPE_PRODUIT = stp.ID_SOUS_TYPE_PRODUIT
          JOIN TYPES_PRODUIT tp
              ON stp.TYPE_PRODUIT = tp.ID_TYPE_PRODUIT
          ORDER BY
              p.PRODUIT ASC,
              p.ID_PRODUIT ASC
          """;

    // *************************** ATTRIBUTS *******************************/

    /**
	 * <div>
	 * <p>Projection SQL complète des Produits avec leur parent
	 * SousTypeProduit et le parent TypeProduit.</p>
	 * </div>
	 */
	public static final String SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_ORDONNES
	    = """
	      SELECT
	          p.ID_PRODUIT AS ID_PRODUIT,
	          p.PRODUIT AS PRODUIT,
	          p.SOUS_TYPE_PRODUIT AS ID_SOUS_TYPE_PRODUIT,
	          stp.SOUS_TYPE_PRODUIT AS SOUS_TYPE_PRODUIT,
	          stp.TYPE_PRODUIT AS ID_TYPE_PRODUIT,
	          tp.TYPE_PRODUIT AS TYPE_PRODUIT
	      FROM PRODUITS p
	      JOIN SOUS_TYPES_PRODUIT stp
	          ON p.SOUS_TYPE_PRODUIT = stp.ID_SOUS_TYPE_PRODUIT
	      JOIN TYPES_PRODUIT tp
	          ON stp.TYPE_PRODUIT = tp.ID_TYPE_PRODUIT
	      ORDER BY
	          LOWER(stp.SOUS_TYPE_PRODUIT),
	          LOWER(p.PRODUIT),
	          p.ID_PRODUIT
	      """;

	/** "SELECT COUNT(*) FROM PRODUITS" */
	public static final String SELECT_COUNT_FROM_PRODUITS
	    = "SELECT COUNT(*) FROM PRODUITS";

	/** "SELECT PRODUIT FROM PRODUITS WHERE ID_PRODUIT = ?" */
	public static final String SELECT_PRODUIT_FROM_PRODUITS_WHERE_ID
	    = "SELECT PRODUIT FROM PRODUITS WHERE ID_PRODUIT = ?";

	/** "SELECT SOUS_TYPE_PRODUIT FROM PRODUITS WHERE ID_PRODUIT = ?" */
	public static final String SELECT_PARENT_FROM_PRODUITS_WHERE_ID
	    = "SELECT SOUS_TYPE_PRODUIT FROM PRODUITS WHERE ID_PRODUIT = ?";

	/** "SELECT COUNT(*) FROM PRODUITS WHERE LOWER(PRODUIT) = LOWER(?) AND SOUS_TYPE_PRODUIT = ?" */
	public static final String SELECT_COUNT_PRODUITS_WHERE_LIBELLE_AND_PARENT
	    = "SELECT COUNT(*) FROM PRODUITS WHERE LOWER(PRODUIT) = LOWER(?) AND SOUS_TYPE_PRODUIT = ?";

	/** "SELECT ID_PRODUIT FROM PRODUITS WHERE LOWER(PRODUIT) = LOWER(?) AND SOUS_TYPE_PRODUIT = ?" */
	public static final String SELECT_ID_PRODUIT_WHERE_LIBELLE_AND_PARENT
	    = "SELECT ID_PRODUIT FROM PRODUITS WHERE LOWER(PRODUIT) = LOWER(?) AND SOUS_TYPE_PRODUIT = ?";

	/** "DELETE FROM PRODUITS WHERE ID_PRODUIT = ?" */
	public static final String DELETE_FROM_PRODUITS_WHERE_ID
	    = "DELETE FROM PRODUITS WHERE ID_PRODUIT = ?";

	/** "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?" */
	public static final String SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT_WHERE_ID
	    = "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?";

	/**
	 * <div>
	 * <p>Projection SQL complète d'un Produit par libellé,
	 * avec son parent SousTypeProduit et le parent TypeProduit.</p>
	 * </div>
	 */
	public static final String SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE
	    = """
	      SELECT
	          p.ID_PRODUIT AS ID_PRODUIT,
	          p.PRODUIT AS PRODUIT,
	          p.SOUS_TYPE_PRODUIT AS ID_SOUS_TYPE_PRODUIT,
	          stp.SOUS_TYPE_PRODUIT AS SOUS_TYPE_PRODUIT,
	          stp.TYPE_PRODUIT AS ID_TYPE_PRODUIT,
	          tp.TYPE_PRODUIT AS TYPE_PRODUIT
	      FROM PRODUITS p
	      JOIN SOUS_TYPES_PRODUIT stp
	          ON p.SOUS_TYPE_PRODUIT = stp.ID_SOUS_TYPE_PRODUIT
	      JOIN TYPES_PRODUIT tp
	          ON stp.TYPE_PRODUIT = tp.ID_TYPE_PRODUIT
	      WHERE LOWER(p.PRODUIT) = LOWER(?)
	      ORDER BY
	          p.ID_PRODUIT ASC
	      """;

    /** "SELECT COUNT(*) FROM PRODUITS WHERE LOWER(PRODUIT) = LOWER(?)" */
    public static final String SELECT_COUNT_PRODUITS_WHERE_LIBELLE
        = "SELECT COUNT(*) FROM PRODUITS WHERE LOWER(PRODUIT) = LOWER(?)";

    /**
     * <div>
     * <p>Projection SQL complète des Produits par libellé,
     * avec leur parent SousTypeProduit et le parent TypeProduit,
     * ordonnée comme le service findByLibelle(...).</p>
     * </div>
     */
    public static final String SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE_ORDONNES
        = """
          SELECT
              p.ID_PRODUIT AS ID_PRODUIT,
              p.PRODUIT AS PRODUIT,
              p.SOUS_TYPE_PRODUIT AS ID_SOUS_TYPE_PRODUIT,
              stp.SOUS_TYPE_PRODUIT AS SOUS_TYPE_PRODUIT,
              stp.TYPE_PRODUIT AS ID_TYPE_PRODUIT,
              tp.TYPE_PRODUIT AS TYPE_PRODUIT
          FROM PRODUITS p
          JOIN SOUS_TYPES_PRODUIT stp
              ON p.SOUS_TYPE_PRODUIT = stp.ID_SOUS_TYPE_PRODUIT
          JOIN TYPES_PRODUIT tp
              ON stp.TYPE_PRODUIT = tp.ID_TYPE_PRODUIT
          WHERE LOWER(p.PRODUIT) = LOWER(?)
          ORDER BY
              LOWER(stp.SOUS_TYPE_PRODUIT),
              LOWER(p.PRODUIT),
              p.ID_PRODUIT
          """;
    
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
     * <p>garantit que creer(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_CREER_KO_PARAM_NULL} ;</li>
     * <li>n'écrit rien dans le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_NULL)
    @Test
    public void testCreerNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service afin de pouvoir prouver ensuite
         * qu'aucune écriture n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* ACT - ASSERT :
         * garantit que service.creer(null)
         * - jette une ExceptionAppliParamNull
         * - émet un message MESSAGE_CREER_KO_PARAM_NULL
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.creer(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_CREER_KO_PARAM_NULL);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.creer(null)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(libellé null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_CREER_KO_LIBELLE_BLANK} ;</li>
     * <li>n'écrit rien dans le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_LIBELLE_NULL)
    @Test
    public void testCreerLibelleNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service,
         * afin de pouvoir prouver ensuite
         * qu'aucune écriture n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * récupère un parent persistant depuis un objet métier seedé,
         * afin de tester uniquement le contrôle du libellé
         * de l'objet métier à créer.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final SousTypeProduitI parent =
                produitsExistants.get(0).getSousTypeProduit();

        assertThat(parent).isNotNull();
        assertThat(parent.getIdSousTypeProduit()).isNotNull();
        assertThat(parent.getSousTypeProduit()).isNotBlank();

        final Produit aCreer = new Produit(null, null, parent);

        /* ACT - ASSERT :
         * garantit que service.creer(aCreer)
         * - jette une ExceptionAppliLibelleBlank
         * - émet un message MESSAGE_CREER_KO_LIBELLE_BLANK
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.creer(aCreer))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_CREER_KO_LIBELLE_BLANK);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.creer(aCreer)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_CREER_KO_LIBELLE_BLANK} ;</li>
     * <li>n'écrit rien dans le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_BLANK)
    @Test
    public void testCreerLibelleBlank() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service,
         * afin de pouvoir prouver ensuite
         * qu'aucune écriture n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * récupère un parent persistant depuis un objet métier seedé,
         * afin de tester uniquement le contrôle du libellé
         * de l'objet métier à créer.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final SousTypeProduitI parent =
                produitsExistants.get(0).getSousTypeProduit();

        assertThat(parent).isNotNull();
        assertThat(parent.getIdSousTypeProduit()).isNotNull();
        assertThat(parent.getSousTypeProduit()).isNotBlank();

        final Produit aCreer = new Produit(null, BLANK, parent);

        /* ACT - ASSERT :
         * garantit que service.creer(aCreer)
         * - jette une ExceptionAppliLibelleBlank
         * - émet un message MESSAGE_CREER_KO_LIBELLE_BLANK
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.creer(aCreer))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_CREER_KO_LIBELLE_BLANK);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.creer(aCreer)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(parent null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParentNull} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_CREER_KO_PARENT_NULL} ;</li>
     * <li>n'écrit rien dans le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_NULL)
    @Test
    public void testCreerParentNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service,
         * afin de pouvoir prouver ensuite
         * qu'aucune écriture n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * prépare un objet métier à créer sans parent,
         * afin de vérifier le contrôle contractuel
         * du parent obligatoire.
         */
        final Produit aCreer =
                new Produit(null, TEMP_PRODUIT_A_SUPPRIMER, null);

        /* ACT - ASSERT :
         * garantit que l'appel service.creer(aCreer)
         * - jette une ExceptionAppliParentNull
         * - émet un message MESSAGE_CREER_KO_PARENT_NULL
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.creer(aCreer))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_CREER_KO_PARENT_NULL);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.creer(aCreer)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(parent libellé null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'écrit rien dans le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_LIBELLE_NULL)
    @Test
    public void testCreerParentLibelleNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service,
         * afin de pouvoir prouver ensuite
         * qu'aucune écriture n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * récupère un identifiant parent réellement persistant,
         * puis prépare un parent avec libellé null,
         * afin de vérifier le contrôle contractuel
         * sur le parent de l'objet métier à créer.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final SousTypeProduitI parentSeed =
                produitsExistants.get(0).getSousTypeProduit();

        assertThat(parentSeed).isNotNull();
        assertThat(parentSeed.getIdSousTypeProduit()).isNotNull();

        final SousTypeProduit parent =
                new SousTypeProduit(parentSeed.getIdSousTypeProduit(), null, null);

        final Produit aCreer =
                new Produit(null, TEMP_PRODUIT_A_SUPPRIMER, parent);

        /* ACT - ASSERT :
         * garantit que l'appel service.creer(aCreer)
         * - jette une ExceptionAppliLibelleBlank
         * - émet un message MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.creer(aCreer))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_CREER_KO_LIBELLE_PARENT_BLANK);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.creer(aCreer)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'écrit rien dans le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_LIBELLE_BLANK)
    @Test
    public void testCreerParentLibelleBlank() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service,
         * afin de pouvoir prouver ensuite
         * qu'aucune écriture n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * récupère un identifiant parent réellement persistant,
         * puis prépare un parent avec libellé blank,
         * afin de vérifier le contrôle contractuel
         * sur le parent de l'objet métier à créer.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final SousTypeProduitI parentSeed =
                produitsExistants.get(0).getSousTypeProduit();

        assertThat(parentSeed).isNotNull();
        assertThat(parentSeed.getIdSousTypeProduit()).isNotNull();

        final SousTypeProduit parent =
                new SousTypeProduit(parentSeed.getIdSousTypeProduit(), BLANK, null);

        final Produit aCreer =
                new Produit(null, TEMP_PRODUIT_A_SUPPRIMER, parent);

        /* ACT - ASSERT :
         * garantit que l'appel service.creer(aCreer)
         * - jette une ExceptionAppliLibelleBlank
         * - émet un message MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.creer(aCreer))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_CREER_KO_LIBELLE_PARENT_BLANK);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.creer(aCreer)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(parent ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#MESSAGE_CREER_KO_PARENT_NON_PERSISTENT} ;</li>
     * <li>n'écrit rien dans le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_ID_NULL)
    @Test
    public void testCreerParentIdNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service,
         * afin de pouvoir prouver ensuite
         * qu'aucune écriture n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * récupère le libellé d'un parent persistant seedé,
         * puis prépare un parent dont l'identifiant est null,
         * afin de vérifier le contrôle de persistance du parent.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final SousTypeProduitI parentSeed =
                produitsExistants.get(0).getSousTypeProduit();

        assertThat(parentSeed).isNotNull();
        assertThat(parentSeed.getSousTypeProduit()).isNotBlank();

        final String libelleParent = parentSeed.getSousTypeProduit();

        final SousTypeProduit parent =
                new SousTypeProduit(null, libelleParent, null);

        final Produit aCreer =
                new Produit(null, TEMP_PRODUIT_A_SUPPRIMER, parent);

        /* ACT - ASSERT :
         * garantit que l'appel service.creer(aCreer)
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet un message MESSAGE_CREER_KO_PARENT_NON_PERSISTENT
         * + libelleParent (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.creer(aCreer))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(MSG_CREER_KO_PARENT_NON_PERSISTENT + libelleParent);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.creer(aCreer)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(parent absent) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#MESSAGE_CREER_KO_PARENT_NON_PERSISTENT} ;</li>
     * <li>prouve que le parent est absent du stockage avant l'appel ;</li>
     * <li>n'écrit rien dans le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_ABSENT)
    @Test
    public void testCreerParentAbsent() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service,
         * afin de pouvoir prouver ensuite
         * qu'aucune écriture n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * récupère le libellé d'un parent seedé,
         * afin de construire un parent absent portant un libellé valide.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final SousTypeProduitI parentSeed =
                produitsExistants.get(0).getSousTypeProduit();

        assertThat(parentSeed).isNotNull();
        assertThat(parentSeed.getSousTypeProduit()).isNotBlank();

        final String libelleParent = parentSeed.getSousTypeProduit();

        /*
         * Vérifie directement dans le stockage
         * qu'aucun parent ne porte l'identifiant ID_INEXISTANT.
         */
        final Long countParentStockage = this.jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?",
                Long.class,
                ID_INEXISTANT);

        assertThat(countParentStockage).isNotNull().isZero();

        /*
         * prépare un parent portant un identifiant inexistant,
         * afin de vérifier le contrôle de persistance
         * du parent dans le stockage.
         */
        final SousTypeProduit parent =
                new SousTypeProduit(ID_INEXISTANT, libelleParent, null);

        final Produit aCreer =
                new Produit(null, TEMP_PRODUIT_A_SUPPRIMER, parent);

        /* ACT - ASSERT :
         * garantit que l'appel service.creer(aCreer)
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet un message MESSAGE_CREER_KO_PARENT_NON_PERSISTENT
         * + libelleParent (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.creer(aCreer))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(MSG_CREER_KO_PARENT_NON_PERSISTENT + libelleParent);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.creer(aCreer)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que si l'appelant tente creer(...)
     * avec un libellé déjà présent pour le même parent (doublon) :</p>
     * <ul>
     * <li>le stockage refuse la création du doublon ;</li>
     * <li>l'exception observable en intégration
     * est une {@link org.springframework.transaction.UnexpectedRollbackException} ;</li>
     * <li>aucun nouvel enregistrement n'est créé dans le stockage ;</li>
     * <li>l'unique enregistrement dans le stockage correspondant à l'objet métier doublon reste inchangé.</li>
     * </ul>
     * <p>Ce test vérifie donc le comportement réellement visible
     * à travers le proxy transactionnel Spring/Hibernate,
     * sans se contenter de l'état du cache JPA.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_DOUBLON)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCreerDoublon() throws Exception {

        /* ARRANGE :
         * lit d'abord le stockage par SQL direct afin de disposer
         * d'une preuve indépendante du contexte Hibernate.
         *
         * Le doublon testé ici porte sur le couple :
         * - parent = parent seedé de CHEMISE_ML_HOMME ;
         * - objet métier = CHEMISE_ML_HOMME.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final SousTypeProduitI parentSeed =
                produitsExistants.get(0).getSousTypeProduit();

        final SousTypeProduit parentComplet =
                construireParentCompletDepuisSeed(parentSeed);

        final Long idParent = parentComplet.getIdSousTypeProduit();

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Lit directement (en SQL) dans le stockage le nombre
         * d'enregistrements correspondant déjà à l'objet métier :
         * - objet métier = CHEMISE_ML_HOMME ;
         * - parent = idParent.
         *
         * La comparaison sur le libellé est faite
         * sans tenir compte de la casse,
         * afin de vérifier l'existence réelle d'un doublon fonctionnel
         * avant l'appel du service.
         */
        final Long countCoupleAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PRODUITS_WHERE_LIBELLE_AND_PARENT,
                Long.class,
                CHEMISE_ML_HOMME,
                idParent);

        final Long idSeedAvant = this.jdbcTemplate.queryForObject(
                SELECT_ID_PRODUIT_WHERE_LIBELLE_AND_PARENT,
                Long.class,
                CHEMISE_ML_HOMME,
                idParent);

        final Produit aCreer =
                new Produit(null, CHEMISE_ML_HOMME, parentComplet);

        /*
         * Assure que l'objet métier doublon existe déjà
         * une seule fois dans le stockage avant l'appel service.creer(...).
         */
        assertThat(countCoupleAvant).isNotNull().isEqualTo(1L);
        assertThat(idSeedAvant).isNotNull();

        /* ACT - ASSERT :
         * sollicite la méthode creer(...)
         * avec un objet métier déjà présent.
         *
         * Le rollback transactionnel est visible dans le test
         * sous la forme d'une UnexpectedRollbackException.
         */
        assertThatThrownBy(() -> this.service.creer(aCreer))
            .isInstanceOf(org.springframework.transaction.UnexpectedRollbackException.class);

        /* ASSERT :
         * contrôle ensuite par SQL direct
         * qu'aucun effet de bord n'a été produit dans le stockage.
         *
         * On évite volontairement tout raisonnement
         * basé sur le cache Hibernate.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        final Long countCoupleApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PRODUITS_WHERE_LIBELLE_AND_PARENT,
                Long.class,
                CHEMISE_ML_HOMME,
                idParent);

        final Long idSeedApres = this.jdbcTemplate.queryForObject(
                SELECT_ID_PRODUIT_WHERE_LIBELLE_AND_PARENT,
                Long.class,
                CHEMISE_ML_HOMME,
                idParent);

        /* Garantit qu'aucun nouvel enregistrement
         * n'a été créé dans le stockage.
         */
        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

        /* Garantit qu'il n'existe toujours
         * qu'un seul enregistrement pour cet objet métier.
         */
        assertThat(countCoupleApres).isNotNull().isEqualTo(1L);

        /* Garantit enfin que l'enregistrement seedé initial
         * est resté strictement le même.
         */
        assertThat(idSeedApres).isNotNull();
        assertThat(idSeedApres).isEqualTo(idSeedAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(OK) :</p>
     * <ul>
     * <li>crée réellement un enregistrement dans le stockage ;</li>
     * <li>retourne un {@link Produit} persistant ;</li>
     * <li>retourne un objet métier portant un identifiant généré ;</li>
     * <li>écrit le bon libellé enfant et la bonne clé étrangère parent
     * dans le stockage ;</li>
     * <li>rend la donnée retrouvable après neutralisation explicite du contexte Hibernate ;</li>
     * <li>ne supprime ni n'altère les données seedées.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_NOMINAL)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCreerNominal() throws Exception {

        /* ARRANGE :
         * Lit directement (en SQL) dans le stockage
         * via JdbcTemplate, afin de disposer d'une preuve indépendante
         * du contexte Hibernate.
         *
         * Le test est volontairement exécuté hors transaction de test
         * pour prouver une écriture dans le stockage,
         * puis réaliser un nettoyage physique explicite en finally.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final SousTypeProduitI parentSeed =
                produitsExistants.get(0).getSousTypeProduit();

        final SousTypeProduit parentComplet =
                construireParentCompletDepuisSeed(parentSeed);

        final Long idParent = parentComplet.getIdSousTypeProduit();
        final String libelleParent = parentComplet.getSousTypeProduit();

        final Produit aCreer =
                new Produit(
                        null,
                        TEMP_PRODUIT_A_SUPPRIMER,
                        parentComplet);

        Long idCree = null;

        try {

            /* ACT :
             * appelle service.creer(aCreer).
             *
             * Le try/finally encadre la création réelle,
             * afin de garantir le nettoyage défensif
             * même si une assertion échoue après cette écriture.
             */
            final Produit cree = this.service.creer(aCreer);

            /* ASSERT :
             * garantit d'abord que l'objet métier retourné
             * est bien persistant et correctement renseigné.
             */
            assertThat(cree).isNotNull();
            assertThat(cree.getIdProduit()).isNotNull().isPositive();
            assertThat(cree.getProduit()).isEqualTo(TEMP_PRODUIT_A_SUPPRIMER);
            assertThat(cree.getSousTypeProduit()).isNotNull();
            assertThat(cree.getSousTypeProduit().getIdSousTypeProduit()).isEqualTo(idParent);
            assertThat(cree.getSousTypeProduit().getSousTypeProduit()).isEqualTo(libelleParent);
            assertThat(cree.getSousTypeProduit().getTypeProduit()).isNotNull();
            assertThat(cree.getSousTypeProduit().getTypeProduit().getIdTypeProduit())
                .isEqualTo(parentComplet.getTypeProduit().getIdTypeProduit());

            idCree = cree.getIdProduit();

            /* ASSERT :
             * contrôle ensuite physiquement le stockage par SQL direct,
             * pour prouver l'écriture réelle dans le stockage
             * et non un simple effet de cache Hibernate.
             */
            final Long countApres = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            final Integer countEnStockage = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS_WHERE,
                    Integer.class,
                    idCree);

            final String libelleStockage = this.jdbcTemplate.queryForObject(
                    SELECT_PRODUIT_FROM_PRODUITS_WHERE_ID,
                    String.class,
                    idCree);

            final Long parentStockage = this.jdbcTemplate.queryForObject(
                    SELECT_PARENT_FROM_PRODUITS_WHERE_ID,
                    Long.class,
                    idCree);

            /*
             * Garantit que l'appel service.creer(aCreer) augmente bien
             * le nombre total d'enregistrements.
             */
            assertThat(countApres).isNotNull().isEqualTo(countAvant + 1L);

            /*
             * Garantit physiquement qu'un seul enregistrement
             * dans le stockage porte bien l'identifiant créé.
             */
            assertThat(countEnStockage)
                .as("L'enregistrement doit exister physiquement dans le stockage après service.creer(...) : ")
                .isNotNull()
                .isEqualTo(1);

            /*
             * Assure l'écriture du bon libellé de l'objet métier
             * dans le stockage.
             */
            assertThat(libelleStockage).isEqualTo(TEMP_PRODUIT_A_SUPPRIMER);

            /*
             * Assure l'écriture de la bonne clé étrangère parent
             * de l'objet métier dans le stockage.
             */
            assertThat(parentStockage).isEqualTo(idParent);

            /*
             * Neutralise explicitement le contexte Hibernate
             * avant toute relecture via le service pour éviter
             * d'être leurré par le cache Hibernate.
             */
            this.entityManager.clear();

            /*
             * Garantit que l'objet nouvellement créé
             * est bien retrouvable via l'appel service.findById(idCree)
             * après neutralisation du contexte de persistance.
             */
            final Produit relu = this.service.findById(idCree);
            assertThat(relu).isNotNull();
            assertThat(relu.getIdProduit()).isEqualTo(idCree);
            assertThat(relu.getProduit()).isEqualTo(TEMP_PRODUIT_A_SUPPRIMER);
            assertThat(relu.getSousTypeProduit()).isNotNull();
            assertThat(relu.getSousTypeProduit().getIdSousTypeProduit()).isEqualTo(idParent);
            assertThat(relu.getSousTypeProduit().getSousTypeProduit()).isEqualTo(libelleParent);
            assertThat(relu.getSousTypeProduit().getTypeProduit()).isNotNull();
            assertThat(relu.getSousTypeProduit().getTypeProduit().getIdTypeProduit())
                .isEqualTo(parentComplet.getTypeProduit().getIdTypeProduit());

            /*
             * Garantit enfin que les données seedées
             * restent présentes après l'appel service.creer(aCreer).
             */
            final List<Produit> liste = this.service.rechercherTous();
            assertThat(liste)
                .extracting(Produit::getProduit)
                .contains(
                        CHEMISE_ML_HOMME,
                        CHEMISE_MC_HOMME,
                        SWEAT_HOMME,
                        TEMP_PRODUIT_A_SUPPRIMER);

        } finally {

            /* Nettoyage défensif :
             * si l'enregistrement créé existe encore dans le stockage
             * après une éventuelle assertion en échec,
             * le supprime explicitement afin de garantir l'isolation du test.
             */
            if (idCree != null) {
                final Integer countLigne = this.jdbcTemplate.queryForObject(
                        SELECT_COUNT_FROM_PRODUITS_WHERE,
                        Integer.class,
                        idCree);

                if ((countLigne != null) && (countLigne.intValue() == 1)) {
                    this.jdbcTemplate.update(
                            DELETE_FROM_PRODUITS_WHERE_ID,
                            idCree);
                }
            }

            this.entityManager.clear();

        }

    } // __________________________________________________________________



    /**
     * <div>
     * <p>Test didactique non contractuel.</p>
     * <p>garantit que plusieurs appels successifs à creer(...) :</p>
     * <ul>
     * <li>créent plusieurs enregistrements distincts dans le stockage ;</li>
     * <li>attribuent des identifiants différents à chaque objet métier créé ;</li>
     * <li>écrivent pour chaque création le bon libellé enfant
     * et la bonne clé étrangère parent dans le stockage ;</li>
     * <li>rendent chaque création retrouvable séparément via le service ;</li>
     * <li>augmentent le compteur total du nombre exact de créations ;</li>
     * <li>ne suppriment ni n'altèrent les données seedées.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PLUSIEURS)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCreerPlusieurs() throws Exception {

        /* ARRANGE :
         * lit d'abord l'état physique du stockage,
         * retrouve un parent persistant,
         * puis prépare deux créations nominales distinctes.
         *
         * Le test est volontairement exécuté hors transaction de test
         * pour prouver des écritures réelles dans le stockage,
         * puis réaliser un nettoyage physique explicite en finally.
         */
        final Long countAvantStockage = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvantStockage).isNotNull().isNotZero();

        final long countAvant = this.service.count();
        assertThat(countAvant).isEqualTo(countAvantStockage.longValue());

        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final SousTypeProduitI parentSeed =
                produitsExistants.get(0).getSousTypeProduit();

        final SousTypeProduit parentComplet1 =
                construireParentCompletDepuisSeed(parentSeed);

        final SousTypeProduit parentComplet2 =
                construireParentCompletDepuisSeed(parentSeed);

        final Long idParent = parentComplet1.getIdSousTypeProduit();
        final String libelleProduit1 = TEMP_PRODUIT_A_SUPPRIMER;
        final String libelleProduit2 = TEMP_PRODUIT_A_SUPPRIMER + " 2";

        Long id1 = null;
        Long id2 = null;

        try {

            /* ACT :
             * exécute deux créations successives
             * sur deux libellés enfants différents.
             *
             * Chaque création utilise un parent complet distinct
             * afin d'éviter de partager une collection métier modifiée
             * par le setter canonique Produit -> SousTypeProduit.
             */
            final Produit cree1 = this.service.creer(
                    new Produit(
                            null,
                            libelleProduit1,
                            parentComplet1));

            final Produit cree2 = this.service.creer(
                    new Produit(
                            null,
                            libelleProduit2,
                            parentComplet2));

            /* ASSERT :
             * vérifie que les deux objets métier créés
             * sont persistants et distincts.
             */
            assertThat(cree1).isNotNull();
            assertThat(cree2).isNotNull();
            assertThat(cree1.getIdProduit()).isNotNull().isPositive();
            assertThat(cree2.getIdProduit()).isNotNull().isPositive();
            assertThat(cree1.getIdProduit()).isNotEqualTo(cree2.getIdProduit());

            id1 = cree1.getIdProduit();
            id2 = cree2.getIdProduit();

            /*
             * Contrôle physiquement que les deux enregistrements
             * existent dans le stockage.
             */
            final Integer countLigne1 = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS_WHERE,
                    Integer.class,
                    id1);

            final Integer countLigne2 = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS_WHERE,
                    Integer.class,
                    id2);

            assertThat(countLigne1).isNotNull().isEqualTo(1);
            assertThat(countLigne2).isNotNull().isEqualTo(1);

            /*
             * Contrôle physiquement que chaque création a écrit
             * le bon libellé et la bonne clé étrangère parent.
             */
            assertThat(this.jdbcTemplate.queryForObject(
                    SELECT_PRODUIT_FROM_PRODUITS_WHERE_ID,
                    String.class,
                    id1)).isEqualTo(libelleProduit1);

            assertThat(this.jdbcTemplate.queryForObject(
                    SELECT_PRODUIT_FROM_PRODUITS_WHERE_ID,
                    String.class,
                    id2)).isEqualTo(libelleProduit2);

            assertThat(this.jdbcTemplate.queryForObject(
                    SELECT_PARENT_FROM_PRODUITS_WHERE_ID,
                    Long.class,
                    id1)).isEqualTo(idParent);

            assertThat(this.jdbcTemplate.queryForObject(
                    SELECT_PARENT_FROM_PRODUITS_WHERE_ID,
                    Long.class,
                    id2)).isEqualTo(idParent);

            /*
             * Neutralise explicitement le contexte Hibernate
             * avant les relectures via le service.
             */
            this.entityManager.clear();

            final Produit relu1 = this.service.findById(id1);
            final Produit relu2 = this.service.findById(id2);

            assertThat(relu1).isNotNull();
            assertThat(relu2).isNotNull();
            assertThat(relu1.getProduit()).isEqualTo(libelleProduit1);
            assertThat(relu2.getProduit()).isEqualTo(libelleProduit2);
            assertThat(relu1.getSousTypeProduit()).isNotNull();
            assertThat(relu2.getSousTypeProduit()).isNotNull();
            assertThat(relu1.getSousTypeProduit().getIdSousTypeProduit()).isEqualTo(idParent);
            assertThat(relu2.getSousTypeProduit().getIdSousTypeProduit()).isEqualTo(idParent);
            assertThat(relu1.getSousTypeProduit().getTypeProduit()).isNotNull();
            assertThat(relu2.getSousTypeProduit().getTypeProduit()).isNotNull();

            /*
             * Vérifie que le compteur total a augmenté exactement
             * du nombre de créations réellement effectuées.
             */
            final Long countApresStockage = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            final long countApres = this.service.count();

            assertThat(countApresStockage).isNotNull();
            assertThat(countApresStockage).isEqualTo(countAvantStockage + 2L);
            assertThat(countApres).isEqualTo(countApresStockage.longValue());

            /*
             * Garantit enfin que les données seedées
             * restent présentes après les appels service.creer(...).
             */
            final List<Produit> liste = this.service.rechercherTous();
            assertThat(liste)
                .extracting(Produit::getProduit)
                .contains(
                        CHEMISE_ML_HOMME,
                        CHEMISE_MC_HOMME,
                        SWEAT_HOMME,
                        libelleProduit1,
                        libelleProduit2);

        } finally {

            /* Nettoyage défensif :
             * supprime chaque enregistrement créé
             * s'il existe encore dans le stockage.
             */
            if (id1 != null) {
                final Integer countLigne1 = this.jdbcTemplate.queryForObject(
                        SELECT_COUNT_FROM_PRODUITS_WHERE,
                        Integer.class,
                        id1);

                if ((countLigne1 != null) && (countLigne1.intValue() == 1)) {
                    this.jdbcTemplate.update(
                            DELETE_FROM_PRODUITS_WHERE_ID,
                            id1);
                }
            }

            if (id2 != null) {
                final Integer countLigne2 = this.jdbcTemplate.queryForObject(
                        SELECT_COUNT_FROM_PRODUITS_WHERE,
                        Integer.class,
                        id2);

                if ((countLigne2 != null) && (countLigne2.intValue() == 1)) {
                    this.jdbcTemplate.update(
                            DELETE_FROM_PRODUITS_WHERE_ID,
                            id2);
                }
            }

            this.entityManager.clear();

        }

    } // __________________________________________________________________
    
    
    
    // ======================== RechercherTous ============================



    /**
     * <div>
     * <p>garantit que rechercherTous() sur un stockage vide :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>retourne une liste vide ;</li>
     * <li>n'altère pas le stockage vide.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_RECHERCHER_TOUS_STOCKAGE_VIDE)
    @Test
    @Sql(
        scripts = ProduitGatewayJPAServiceIntegrationTest.CLASSPATH_TRUNCATE_SQL,
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    public void testRechercherTousStockageVide() throws Exception {

        /* ARRANGE :
         * remplace pour ce test la préparation standard
         * par le seul script de vidage
         * afin d'obtenir un stockage vide.
         *
         * Compte ensuite directement (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service, afin de pouvoir prouver
         * que service.rechercherTous() conserve ce stockage vide.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /*
         * Assure que le stockage est vide
         * avant l'appel service.rechercherTous().
         */
        assertThat(countAvant).isNotNull().isZero();

        /* ACT :
         * appelle service.rechercherTous()
         * sur le stockage vide.
         */
        final List<Produit> resultats = this.service.rechercherTous();

        /* ASSERT :
         * - assure que service.rechercherTous() ne retourne jamais null,
         *   même lorsque le stockage est vide ;
         * - assure que service.rechercherTous() retourne une liste vide
         *   lorsque le stockage est vide.
         */
        assertThat(resultats).isNotNull().isEmpty();

        /* ASSERT :
         * compte finalement (en SQL)
         * le nombre d'enregistrements dans le stockage
         * pour prouver que service.rechercherTous()
         * n'a pas modifié le stockage vide.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTous(OK) sur le stockage seedé :</p>
     * <ul>
     * <li>retourne une liste non null et non vide ;</li>
     * <li>retourne exactement les objets métier physiquement
     * présents dans le stockage ;</li>
     * <li>retourne autant d'objets métier que d'enregistrements
     * présents dans le stockage ;</li>
     * <li>retourne une liste triée par parent puis par libellé enfant ;</li>
     * <li>retourne une liste sans doublon métier ;</li>
     * <li>retourne des objets métier complets avec le graphe
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_RECHERCHER_TOUS)
    @Test
    public void testRechercherTousNominal() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service afin de pouvoir prouver ensuite
         * qu'aucune écriture n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isPositive();

        /*
         * Lit ensuite directement (en SQL) l'état physique complet
         * du stockage :
         * - Produit ;
         * - parent SousTypeProduit ;
         * - parent TypeProduit du SousTypeProduit.
         *
         * La projection SQL est ordonnée selon le même ordre métier
         * que le service :
         * - libellé du parent SousTypeProduit ;
         * - libellé du Produit ;
         * - identifiant technique en ultime stabilisateur d'ordre SQL.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_ORDONNES);

        /* assure que le stockage n'est pas null ni vide. */
        assertThat(lignesStockage).isNotNull().isNotEmpty();
        assertThat(lignesStockage).hasSize(countAvant.intValue());

        /*
         * Extrait les clés métier de la projection SQL.
         *
         * La clé métier Produit ne se limite pas au libellé du Produit :
         * elle inclut aussi le parent SousTypeProduit et le TypeProduit
         * du parent, conformément à Produit.equals(...).
         */
        final List<String> clesMetierStockage = lignesStockage.stream()
                .map(ligne -> String.valueOf(ligne.get("TYPE_PRODUIT")) // NOPMD by danyl on 06/05/2026 22:11
                        .toLowerCase(LOCALE_DEFAUT)
                        + "|"
                        + String.valueOf(ligne.get("SOUS_TYPE_PRODUIT")) // NOPMD by danyl on 06/05/2026 22:11
                                .toLowerCase(LOCALE_DEFAUT)
                        + "|"
                        + String.valueOf(ligne.get("PRODUIT")) // NOPMD by danyl on 06/05/2026 22:11
                                .toLowerCase(LOCALE_DEFAUT))
                .toList();

        /*
         * Assure que le jeu de données seedé ne contient pas
         * de doublon métier Produit.
         *
         * Cette assertion permet ensuite de comparer directement
         * le nombre d'enregistrements dans le stockage
         * et le nombre d'objets métier retournés par le service,
         * puisque le service dédoublonne par égalité métier.
         */
        assertThat(clesMetierStockage).doesNotHaveDuplicates();

        /* ACT :
         * sollicite service.rechercherTous()
         * sur le stockage seedé.
         */
        final List<Produit> resultats = this.service.rechercherTous();

        /* ASSERT :
         * vérifie d'abord que service.rechercherTous() retourne
         * une liste exploitable (non null et non vide).
         */
        assertThat(resultats).isNotNull().isNotEmpty();

        /*
         * Vérifie ensuite que le nombre d'objets métier retournés
         * par service.rechercherTous() correspond exactement
         * au nombre d'enregistrements dans le stockage.
         */
        assertThat(resultats).hasSize(countAvant.intValue());
        assertThat(resultats).hasSize(lignesStockage.size());

        /*
         * Compare ligne à ligne le résultat du service
         * avec la projection SQL ordonnée.
         *
         * Cette boucle constitue la preuve principale :
         * elle vérifie simultanément le contenu, l'ordre,
         * les identifiants et le graphe parent complet.
         */
        for (int index = 0; index < resultats.size(); index++) {

            final Produit produit = resultats.get(index);
            final java.util.Map<String, Object> ligneStockage =
                    lignesStockage.get(index);

            final Number idProduitStockage =
                    (Number) ligneStockage.get("ID_PRODUIT");

            final String libelleProduitStockage =
                    String.valueOf(ligneStockage.get("PRODUIT"));

            final Number idSousTypeProduitStockage =
                    (Number) ligneStockage.get("ID_SOUS_TYPE_PRODUIT"); // NOPMD by danyl on 06/05/2026 23:36

            final String libelleSousTypeProduitStockage =
                    String.valueOf(ligneStockage.get("SOUS_TYPE_PRODUIT"));

            final Number idTypeProduitStockage =
                    (Number) ligneStockage.get("ID_TYPE_PRODUIT");

            final String libelleTypeProduitStockage =
                    String.valueOf(ligneStockage.get("TYPE_PRODUIT"));

            /*
             * Vérifie l'objet métier Produit retourné
             * pour la ligne courante.
             */
            assertThat(produit).isNotNull();
            assertThat(produit.getIdProduit())
                .isEqualTo(Long.valueOf(idProduitStockage.longValue()));
            assertThat(produit.getProduit()).isEqualTo(libelleProduitStockage);

            /*
             * Vérifie le parent SousTypeProduit du Produit.
             */
            assertThat(produit.getSousTypeProduit()).isNotNull();
            assertThat(produit.getSousTypeProduit().getIdSousTypeProduit())
                .isEqualTo(Long.valueOf(idSousTypeProduitStockage.longValue()));
            assertThat(produit.getSousTypeProduit().getSousTypeProduit())
                .isEqualTo(libelleSousTypeProduitStockage);

            /*
             * Vérifie le parent TypeProduit du SousTypeProduit.
             *
             * Cette vérification est indispensable pour Produit :
             * le graphe métier complet attendu est
             * Produit -> SousTypeProduit -> TypeProduit.
             */
            assertThat(produit.getSousTypeProduit().getTypeProduit()).isNotNull();
            assertThat(produit.getSousTypeProduit().getTypeProduit().getIdTypeProduit())
                .isEqualTo(Long.valueOf(idTypeProduitStockage.longValue()));
            assertThat(produit.getSousTypeProduit().getTypeProduit().getTypeProduit())
                .isEqualTo(libelleTypeProduitStockage);

        }

        /*
         * Extrait les clés métier des objets retournés par le service.
         *
         * Cette extraction permet de vérifier explicitement
         * le dédoublonnage métier après conversion JPA -> métier.
         */
        final List<String> clesMetierResultats = resultats.stream()
                .map(produit -> produit.getSousTypeProduit()
                        .getTypeProduit()
                        .getTypeProduit()
                        .toLowerCase(LOCALE_DEFAUT)
                        + "|"
                        + produit.getSousTypeProduit()
                                .getSousTypeProduit()
                                .toLowerCase(LOCALE_DEFAUT)
                        + "|"
                        + produit.getProduit()
                                .toLowerCase(LOCALE_DEFAUT))
                .toList();

        /*
         * Vérifie que les objets métier retournés :
         * - correspondent exactement aux clés métier lues dans le stockage ;
         * - ne contiennent aucun doublon métier.
         */
        assertThat(clesMetierResultats)
            .containsExactlyElementsOf(clesMetierStockage);

        assertThat(clesMetierResultats).doesNotHaveDuplicates();

        /*
         * Vérifie les propriétés métier générales de la liste retournée :
         * - chaque Produit est complet ;
         * - chaque Produit porte un libellé exploitable ;
         * - chaque parent SousTypeProduit est complet ;
         * - chaque parent TypeProduit est complet.
         */
        assertThat(resultats)
            .allSatisfy(produit -> {
                assertThat(produit).isNotNull();
                assertThat(produit.getIdProduit()).isNotNull();
                assertThat(produit.getProduit()).isNotBlank();

                assertThat(produit.getSousTypeProduit()).isNotNull();
                assertThat(produit.getSousTypeProduit().getIdSousTypeProduit()).isNotNull();
                assertThat(produit.getSousTypeProduit().getSousTypeProduit()).isNotBlank();

                assertThat(produit.getSousTypeProduit().getTypeProduit()).isNotNull();
                assertThat(produit.getSousTypeProduit().getTypeProduit().getIdTypeProduit()).isNotNull();
                assertThat(produit.getSousTypeProduit().getTypeProduit().getTypeProduit()).isNotBlank();
            });

        /*
         * Vérifie que les données seedées attendues
         * sont bien présentes dans le résultat.
         */
        assertThat(resultats)
            .extracting(Produit::getProduit)
            .contains(
                    CHEMISE_ML_HOMME,
                    CHEMISE_MC_HOMME,
                    SWEAT_HOMME);

        /* ASSERT :
         * compte finalement (en SQL)
         * le nombre d'enregistrements dans le stockage
         * pour prouver que service.rechercherTous()
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isPositive();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    
    

    // ===================== rechercherTousParPage =====================



    /**
     * <div>
     * <p>garantit que rechercherTousParPage(null) :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>retourne un contenu non null ;</li>
     * <li>applique les paramètres par défaut de {@code RequetePage} ;</li>
     * <li>retourne un total cohérent avec le stockage ;</li>
     * <li>retourne des objets métier complets avec le graphe
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName(DN_RECHERCHER_TOUS_PAR_PAGE_NULL)
    @Test
    public void testRechercherTousParPageNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service afin de pouvoir prouver ensuite
         * que service.rechercherTousParPage(null)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isPositive();

        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_ORDONNES);

        assertThat(lignesStockage).isNotNull().isNotEmpty();
        assertThat(lignesStockage).hasSize(countAvant.intValue());

        final List<String> clesMetierStockage =
                construireClesMetierDepuisLignesStockage(lignesStockage);

        final int tailleAttendue = Math.min(
                countAvant.intValue(),
                RequetePage.TAILLE_DEFAUT);

        /* ACT :
         * sollicite la pagination avec une requête null,
         * ce qui doit conduire le service à appliquer
         * les paramètres par défaut.
         */
        final ResultatPage<Produit> resultat =
                this.service.rechercherTousParPage(null);

        /* ASSERT :
         * vérifie d'abord la cohérence générale
         * de l'enveloppe paginée retournée.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull();
        assertThat(resultat.getPageNumber())
            .isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(resultat.getPageSize())
            .isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(resultat.getTotalElements()).isEqualTo(countAvant.longValue());
        assertThat(resultat.getContent()).hasSize(tailleAttendue);

        /*
         * Vérifie que chaque objet métier retourné
         * est complet et correspond à un enregistrement réel du stockage.
         *
         * Le cas null n'impose pas de tri métier explicite :
         * on vérifie donc l'appartenance au stockage,
         * la taille et le graphe complet, sans déduire un ordre
         * non spécifié par la requête.
         */
        assertProduitsComplets(resultat.getContent());

        final List<String> clesMetierResultats =
                construireClesMetierDepuisProduits(resultat.getContent());

        assertThat(clesMetierStockage).containsAll(clesMetierResultats);
        assertThat(clesMetierResultats).doesNotHaveDuplicates();

        /* ASSERT :
         * compte finalement (en SQL)
         * le nombre d'enregistrements dans le stockage
         * pour prouver que service.rechercherTousParPage(null)
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isPositive();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTousParPage(avec tri) :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>respecte le numéro de page et la taille demandés ;</li>
     * <li>respecte le tri ascendant demandé sur le libellé Produit ;</li>
     * <li>retourne exactement le segment attendu du stockage ;</li>
     * <li>retourne des objets métier complets avec le graphe
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName(DN_RECHERCHER_TOUS_PAR_PAGE_TRI)
    @Test
    public void testRechercherTousParPageAvecTri() throws Exception {

        /* ARRANGE :
         * lit d'abord directement le stockage avec le même tri
         * que celui demandé au service :
         * - propriété métier "produit" en ordre ascendant ;
         * - identifiant "idProduit" en stabilisateur technique.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_TRI_PRODUIT_ASC);

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        final int pageNumber = 0;
        final int pageSize = 5;

        assertThat(countAvant).isNotNull().isPositive();
        assertThat(lignesStockage).isNotNull().hasSize(countAvant.intValue());

        /*
         * Construit une liste de tris explicite,
         * puis l'injecte dans la RequetePage.
         *
         * Ne pas utiliser requete.getTris().add(...),
         * car getTris() retourne une copie défensive.
         *
         * Le tri principal porte sur le libellé Produit.
         * Le tri secondaire sur idProduit stabilise l'ordre
         * pour permettre une comparaison ligne à ligne
         * avec la projection SQL ordonnée.
         */
        final java.util.List<TriSpec> tris =
                new ArrayList<TriSpec>();

        tris.add(new TriSpec(
                PROP_TRI_PRODUIT,
                DirectionTri.ASC));

        tris.add(new TriSpec(
                IDPRODUIT,
                DirectionTri.ASC));

        final RequetePage requete =
                new RequetePage(
                        pageNumber,
                        pageSize,
                        tris);

        final int tailleAttendue = Math.min(pageSize, countAvant.intValue());

        /* ACT :
         * appelle service.rechercherTousParPage(requete)
         * avec page 0, taille 5 et tri ascendant stabilisé.
         */
        final ResultatPage<Produit> resultat =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie la cohérence générale de la page.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().hasSize(tailleAttendue);
        assertThat(resultat.getPageNumber()).isEqualTo(pageNumber);
        assertThat(resultat.getPageSize()).isEqualTo(pageSize);
        assertThat(resultat.getTotalElements()).isEqualTo(countAvant.longValue());

        /*
         * Compare ligne à ligne le contenu retourné par le service
         * avec les premières lignes du stockage ordonné par Produit
         * et stabilisé par ID_PRODUIT.
         */
        final List<java.util.Map<String, Object>> lignesAttendues =
                lignesStockage.subList(0, tailleAttendue);

        for (int index = 0; index < resultat.getContent().size(); index++) {
            assertProduitConformeALigneStockage(
                    resultat.getContent().get(index),
                    lignesAttendues.get(index));
        }

        /*
         * Vérifie explicitement le tri demandé sur la page retournée.
         */
        assertThat(resultat.getContent())
            .extracting(Produit::getProduit)
            .isSorted();

        assertProduitsComplets(resultat.getContent());

        /* ASSERT :
         * compte finalement (en SQL)
         * le nombre d'enregistrements dans le stockage
         * pour prouver que service.rechercherTousParPage(requete)
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isPositive();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTousParPage(...) sur un stockage vide :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>retourne un contenu non null et vide ;</li>
     * <li>retourne un total à zéro ;</li>
     * <li>conserve le stockage vide.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName(DN_RECHERCHER_TOUS_PAR_PAGE_VIDE)
    @Test
    @Sql(
        scripts = ProduitGatewayJPAServiceIntegrationTest.CLASSPATH_TRUNCATE_SQL,
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    public void testRechercherTousParPageStockageVide() throws Exception {

        /* ARRANGE :
         * prépare pour ce test un stockage réellement vide
         * en ne rejouant que le script de vidage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isZero();

        final RequetePage requete =
                new RequetePage(
                        0,
                        5);

        /* ACT :
         * sollicite la pagination sur un stockage vide.
         */
        final ResultatPage<Produit> resultat =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie que la page retournée reste exploitable
         * et cohérente avec le stockage vide.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().isEmpty();
        assertThat(resultat.getPageNumber()).isEqualTo(0);
        assertThat(resultat.getPageSize()).isEqualTo(5);
        assertThat(resultat.getTotalElements()).isZero();

        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTousParPage(taille supérieure au total) :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>retourne tous les objets métier disponibles ;</li>
     * <li>retourne un total cohérent avec le stockage ;</li>
     * <li>retourne des objets métier complets ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName(DN_RECHERCHER_TOUS_PAR_PAGE_TAILLE_SUPERIEURE)
    @Test
    public void testRechercherTousParPageTailleSuperieureAuTotal() throws Exception {

        /* ARRANGE :
         * lit le stockage trié par libellé Produit
         * et stabilisé par ID_PRODUIT,
         * puis construit une requête dont la taille
         * dépasse le nombre total d'enregistrements.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_TRI_PRODUIT_ASC);

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isPositive();
        assertThat(lignesStockage).isNotNull().hasSize(countAvant.intValue());

        final int pageSize = countAvant.intValue() + 10;

        final List<TriSpec> tris =
                new ArrayList<TriSpec>();

        tris.add(new TriSpec(
                PROP_TRI_PRODUIT,
                DirectionTri.ASC));

        tris.add(new TriSpec(
                IDPRODUIT,
                DirectionTri.ASC));

        final RequetePage requete =
                new RequetePage(
                        0,
                        pageSize,
                        tris);

        /* ACT :
         * sollicite la pagination avec une taille supérieure
         * au total disponible.
         */
        final ResultatPage<Produit> resultat =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie que tous les objets métier disponibles
         * sont retournés.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().hasSize(countAvant.intValue());
        assertThat(resultat.getPageNumber()).isEqualTo(0);
        assertThat(resultat.getPageSize()).isEqualTo(pageSize);
        assertThat(resultat.getTotalElements()).isEqualTo(countAvant.longValue());

        for (int index = 0; index < resultat.getContent().size(); index++) {
            assertProduitConformeALigneStockage(
                    resultat.getContent().get(index),
                    lignesStockage.get(index));
        }

        assertProduitsComplets(resultat.getContent());

        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isPositive();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTousParPage(page hors bornes) :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>retourne un contenu non null et vide ;</li>
     * <li>conserve le numéro de page demandé ;</li>
     * <li>conserve la taille de page demandée ;</li>
     * <li>retourne un total cohérent avec le stockage ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName(DN_RECHERCHER_TOUS_PAR_PAGE_HORS_BORNE)
    @Test
    public void testRechercherTousParPagePageHorsBorne() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isPositive();

        final int pageNumber = 9999;
        final int pageSize = 10;

        final List<TriSpec> tris =
                new java.util.ArrayList<TriSpec>();

        tris.add(new TriSpec(
                PROP_TRI_PRODUIT,
                DirectionTri.ASC));

        final RequetePage requete =
                new RequetePage(
                        pageNumber,
                        pageSize,
                        tris);

        /* ACT :
         * appelle service.rechercherTousParPage(requete)
         * avec un numéro de page hors bornes.
         */
        final ResultatPage<Produit> resultat =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie que le service retourne une page vide,
         * tout en conservant le total d'enregistrements disponibles.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().isEmpty();
        assertThat(resultat.getPageNumber()).isEqualTo(pageNumber);
        assertThat(resultat.getPageSize()).isEqualTo(pageSize);
        assertThat(resultat.getTotalElements()).isEqualTo(countAvant.longValue());

        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isPositive();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTousParPage(taille zéro) :</p>
     * <ul>
     * <li>normalise la taille demandée à zéro vers la taille par défaut ;</li>
     * <li>retourne une page non null ;</li>
     * <li>retourne un contenu non null et non vide ;</li>
     * <li>retourne le premier segment du stockage trié ;</li>
     * <li>retourne un total cohérent avec le stockage ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName(DN_RECHERCHER_TOUS_PAR_PAGE_TAILLE_ZERO)
    @Test
    public void testRechercherTousParPageTailleZero() throws Exception {

        /* ARRANGE :
         * lit d'abord le stockage ordonné par Produit
         * et stabilisé par ID_PRODUIT,
         * afin de disposer d'une référence indépendante d'Hibernate.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_TRI_PRODUIT_ASC);

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isPositive();
        assertThat(lignesStockage).isNotNull().hasSize(countAvant.intValue());

        final List<TriSpec> tris =
                new java.util.ArrayList<TriSpec>();

        tris.add(new TriSpec(
                PROP_TRI_PRODUIT,
                DirectionTri.ASC));

        tris.add(new TriSpec(
                IDPRODUIT,
                DirectionTri.ASC));

        /*
         * Construit une requête avec une taille demandée à zéro.
         *
         * Cette taille est normalisée par RequetePage
         * vers RequetePage.TAILLE_DEFAUT.
         */
        final RequetePage requete =
                new RequetePage(
                        0,
                        0,
                        tris);

        assertThat(requete.getPageSize())
            .isEqualTo(RequetePage.TAILLE_DEFAUT);

        final int tailleAttendue = Math.min(
                countAvant.intValue(),
                RequetePage.TAILLE_DEFAUT);

        /* ACT :
         * appelle service.rechercherTousParPage(requete)
         * avec une taille initialement fournie à zéro.
         */
        final ResultatPage<Produit> resultat =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie que la page retournée est cohérente
         * avec la taille normalisée.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().isNotEmpty();
        assertThat(resultat.getPageNumber()).isEqualTo(0);
        assertThat(resultat.getPageSize())
            .isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(resultat.getTotalElements()).isEqualTo(countAvant.longValue());
        assertThat(resultat.getContent()).hasSize(tailleAttendue);

        final List<java.util.Map<String, Object>> lignesAttendues =
                lignesStockage.subList(0, tailleAttendue);

        for (int index = 0; index < resultat.getContent().size(); index++) {
            assertProduitConformeALigneStockage(
                    resultat.getContent().get(index),
                    lignesAttendues.get(index));
        }

        assertProduitsComplets(resultat.getContent());

        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isPositive();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTousParPage(nominal) :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>retourne le segment demandé du stockage ;</li>
     * <li>respecte la page, la taille et le tri demandés ;</li>
     * <li>retourne des objets métier complets avec le graphe
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>retourne une page sans doublon métier ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName(DN_RECHERCHER_TOUS_PAR_PAGE_NOMINAL)
    @Test
    public void testRechercherTousParPageNominal() throws Exception {

        /* ARRANGE :
         * lit l'état complet du stockage ordonné par Produit
         * et stabilisé par ID_PRODUIT,
         * puis prépare une requête nominale explicite.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_TRI_PRODUIT_ASC);

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        final int pageNumber = 0;
        final int pageSize = 3;

        assertThat(countAvant).isNotNull().isPositive();
        assertThat(lignesStockage).isNotNull().hasSize(countAvant.intValue());

        final List<TriSpec> tris =
                new java.util.ArrayList<TriSpec>();

        tris.add(new TriSpec(
                PROP_TRI_PRODUIT,
                DirectionTri.ASC));

        tris.add(new TriSpec(
                IDPRODUIT,
                DirectionTri.ASC));

        final RequetePage requete =
                new RequetePage(
                        pageNumber,
                        pageSize,
                        tris);

        final int tailleAttendue = Math.min(pageSize, countAvant.intValue());

        /* ACT :
         * appelle service.rechercherTousParPage(requete)
         * avec une requête nominale explicite.
         */
        final ResultatPage<Produit> resultat =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie l'enveloppe paginée.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().hasSize(tailleAttendue);
        assertThat(resultat.getPageNumber()).isEqualTo(pageNumber);
        assertThat(resultat.getPageSize()).isEqualTo(pageSize);
        assertThat(resultat.getTotalElements()).isEqualTo(countAvant.longValue());

        final List<java.util.Map<String, Object>> lignesAttendues =
                lignesStockage.subList(0, tailleAttendue);

        /*
         * Compare ligne à ligne la page retournée
         * avec le segment SQL attendu.
         */
        for (int index = 0; index < resultat.getContent().size(); index++) {
            assertProduitConformeALigneStockage(
                    resultat.getContent().get(index),
                    lignesAttendues.get(index));
        }

        assertProduitsComplets(resultat.getContent());

        final List<String> clesMetierResultats =
                construireClesMetierDepuisProduits(resultat.getContent());

        assertThat(clesMetierResultats).doesNotHaveDuplicates();

        /*
         * Vérifie que les données seedées principales
         * restent accessibles dans l'état du stockage.
         */
        assertThat(lignesStockage.stream()
                .map(ligne -> String.valueOf(ligne.get("PRODUIT")))
                .toList())
            .contains(
                    CHEMISE_ML_HOMME,
                    CHEMISE_MC_HOMME,
                    SWEAT_HOMME);

        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isPositive();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    
    
    
    // ======================== findByObjetMetier =========================



    /**
     * <div>
     * <p>garantit que findByObjetMetier(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_NULL)
    @Test
    public void testFindByObjetMetierNull() throws Exception {

        /* ARRANGE :
         * compte directement (en SQL) le nombre d'enregistrements
         * dans le stockage afin de disposer d'une preuve indépendante
         * du contexte Hibernate.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        /* ACT - ASSERT :
         * garantit que service.findByObjetMetier(null)
         * - jette une ExceptionAppliParamNull
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_PARAM_NULL);

        /* ASSERT :
         * compte ensuite (en SQL) le nombre d'enregistrements
         * dans le stockage afin de prouver que l'appel
         * service.findByObjetMetier(null) n'a rien modifié.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(libellé null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_LIBELLE_NULL)
    @Test
    public void testFindByObjetMetierLibelleNull() throws Exception {

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        final java.util.Map<String, Object> ligneStockage =
                lignesStockage.get(0);

        final Number idSousTypeProduitStockage =
                (Number) ligneStockage.get("ID_SOUS_TYPE_PRODUIT");

        final String libelleSousTypeProduitStockage =
                String.valueOf(ligneStockage.get("SOUS_TYPE_PRODUIT"));

        final SousTypeProduit parent =
                new SousTypeProduit(
                        Long.valueOf(idSousTypeProduitStockage.longValue()),
                        libelleSousTypeProduitStockage,
                        null);

        final Produit probe = new Produit(null, null, parent);

        assertThatThrownBy(() -> this.service.findByObjetMetier(probe))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);

        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_BLANK)
    @Test
    public void testFindByObjetMetierLibelleBlank() throws Exception {

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        final java.util.Map<String, Object> ligneStockage =
                lignesStockage.get(0);

        final Number idSousTypeProduitStockage =
                (Number) ligneStockage.get("ID_SOUS_TYPE_PRODUIT");

        final String libelleSousTypeProduitStockage =
                String.valueOf(ligneStockage.get("SOUS_TYPE_PRODUIT"));

        final SousTypeProduit parent =
                new SousTypeProduit(
                        Long.valueOf(idSousTypeProduitStockage.longValue()),
                        libelleSousTypeProduitStockage,
                        null);

        final Produit probe = new Produit(null, BLANK, parent);

        assertThatThrownBy(() -> this.service.findByObjetMetier(probe))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);

        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParentNull} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_PARENT_NULL)
    @Test
    public void testFindByObjetMetierParentNull() throws Exception {

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        final Produit probe = new Produit(null, CHEMISE_ML_HOMME, null);

        assertThatThrownBy(() -> this.service.findByObjetMetier(probe))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_PARENT_NULL);

        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent libellé null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_PARENT_LIBELLE_NULL)
    @Test
    public void testFindByObjetMetierParentLibelleNull() throws Exception {

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        final Number idSousTypeProduitStockage =
                (Number) lignesStockage.get(0).get("ID_SOUS_TYPE_PRODUIT");

        final SousTypeProduit parent =
                new SousTypeProduit(
                        Long.valueOf(idSousTypeProduitStockage.longValue()),
                        null,
                        null);

        final Produit probe = new Produit(null, CHEMISE_ML_HOMME, parent);

        assertThatThrownBy(() -> this.service.findByObjetMetier(probe))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK);

        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_PARENT_LIBELLE_BLANK)
    @Test
    public void testFindByObjetMetierParentLibelleBlank() throws Exception {

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        final Number idSousTypeProduitStockage =
                (Number) lignesStockage.get(0).get("ID_SOUS_TYPE_PRODUIT");

        final SousTypeProduit parent =
                new SousTypeProduit(
                        Long.valueOf(idSousTypeProduitStockage.longValue()),
                        BLANK,
                        null);

        final Produit probe = new Produit(null, CHEMISE_ML_HOMME, parent);

        assertThatThrownBy(() -> this.service.findByObjetMetier(probe))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK);

        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé parent ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_PARENT_ID_NULL)
    @Test
    public void testFindByObjetMetierParentIdNull() throws Exception {

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        final String libelleSousTypeProduitStockage =
                String.valueOf(lignesStockage.get(0).get("SOUS_TYPE_PRODUIT"));

        final SousTypeProduit parent =
                new SousTypeProduit(null, libelleSousTypeProduitStockage, null);

        final Produit probe = new Produit(null, CHEMISE_ML_HOMME, parent);

        assertThatThrownBy(() -> this.service.findByObjetMetier(probe))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTANT
                    + libelleSousTypeProduitStockage);

        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent absent) :</p>
     * <ul>
     * <li>prouve d'abord que le parent est absent du stockage ;</li>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé parent ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_PARENT_ABSENT)
    @Test
    public void testFindByObjetMetierParentAbsent() throws Exception {

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        final String libelleSousTypeProduitStockage =
                String.valueOf(lignesStockage.get(0).get("SOUS_TYPE_PRODUIT"));

        final Long countParentStockage = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT_WHERE_ID,
                Long.class,
                ID_INEXISTANT);

        assertThat(countParentStockage).isNotNull().isZero();

        final SousTypeProduit parent =
                new SousTypeProduit(
                        ID_INEXISTANT,
                        libelleSousTypeProduitStockage,
                        null);

        final Produit probe = new Produit(null, CHEMISE_ML_HOMME, parent);

        assertThatThrownBy(() -> this.service.findByObjetMetier(probe))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTANT
                    + libelleSousTypeProduitStockage);

        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(non trouvé) :</p>
     * <ul>
     * <li>cherche sous un parent réellement persistant ;</li>
     * <li>prouve que le couple parent + libellé Produit
     * est absent du stockage avant l'appel ;</li>
     * <li>retourne {@code null} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_NON_TROUVE)
    @Test
    public void testFindByObjetMetierNonTrouve() throws Exception {

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        final java.util.Map<String, Object> ligneStockage =
                lignesStockage.get(0);

        final Number idSousTypeProduitStockage =
                (Number) ligneStockage.get("ID_SOUS_TYPE_PRODUIT");

        final String libelleSousTypeProduitStockage =
                String.valueOf(ligneStockage.get("SOUS_TYPE_PRODUIT"));

        final Long idParent =
                Long.valueOf(idSousTypeProduitStockage.longValue());

        final Long countCoupleAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PRODUITS_WHERE_LIBELLE_AND_PARENT,
                Long.class,
                INTROUVABLE,
                idParent);

        assertThat(countCoupleAvant).isNotNull().isZero();

        final SousTypeProduit parent =
                new SousTypeProduit(idParent, libelleSousTypeProduitStockage, null);

        final Produit probe = new Produit(null, INTROUVABLE, parent);

        this.entityManager.clear();

        final Produit trouve = this.service.findByObjetMetier(probe);

        assertThat(trouve).isNull();

        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(nominal) :</p>
     * <ul>
     * <li>retrouve l'objet métier correspondant au couple
     * parent SousTypeProduit + libellé Produit ;</li>
     * <li>retourne l'objet métier réellement présent dans le stockage ;</li>
     * <li>retourne le graphe complet
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_NOMINAL)
    @Test
    public void testFindByObjetMetierNominal() throws Exception {

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        final java.util.Map<String, Object> ligneStockage =
                lignesStockage.get(0);

        final Number idSousTypeProduitStockage =
                (Number) ligneStockage.get("ID_SOUS_TYPE_PRODUIT");

        final String libelleSousTypeProduitStockage =
                String.valueOf(ligneStockage.get("SOUS_TYPE_PRODUIT"));

        final Long idParent =
                Long.valueOf(idSousTypeProduitStockage.longValue());

        final Long countCoupleAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PRODUITS_WHERE_LIBELLE_AND_PARENT,
                Long.class,
                CHEMISE_ML_HOMME,
                idParent);

        assertThat(countCoupleAvant).isNotNull().isEqualTo(1L);

        final SousTypeProduit parent =
                new SousTypeProduit(idParent, libelleSousTypeProduitStockage, null);

        final Produit probe = new Produit(null, CHEMISE_ML_HOMME, parent);

        this.entityManager.clear();

        final Produit trouve = this.service.findByObjetMetier(probe);

        assertProduitConformeALigneStockage(trouve, ligneStockage);
        assertProduitsComplets(List.of(trouve));

        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>Test didactique non contractuel.</p>
     * <p>garantit que findByObjetMetier(béton) :</p>
     * <ul>
     * <li>ignore l'identifiant porté par la sonde d'entrée ;</li>
     * <li>recherche sans tenir compte de la casse du libellé Produit ;</li>
     * <li>recherche bien sur le couple parent SousTypeProduit
     * + libellé Produit ;</li>
     * <li>retourne le même objet métier persistant dans les deux cas ;</li>
     * <li>retourne un graphe complet
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_BETON)
    @Test
    public void testFindByObjetMetierBeton() throws Exception {

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        final java.util.Map<String, Object> ligneStockage =
                lignesStockage.get(0);

        final Number idProduitStockage =
                (Number) ligneStockage.get("ID_PRODUIT");

        final Number idSousTypeProduitStockage =
                (Number) ligneStockage.get("ID_SOUS_TYPE_PRODUIT");

        final String libelleSousTypeProduitStockage =
                String.valueOf(ligneStockage.get("SOUS_TYPE_PRODUIT"));

        final Long idParent =
                Long.valueOf(idSousTypeProduitStockage.longValue());

        final SousTypeProduit parent =
                new SousTypeProduit(idParent, libelleSousTypeProduitStockage, null);

        final Produit probeIdIgnore =
                new Produit(ID_INEXISTANT, CHEMISE_ML_HOMME, parent);

        final Produit probeCasseIgnoree =
                new Produit(
                        ID_INEXISTANT,
                        CHEMISE_ML_HOMME.toUpperCase(LOCALE_DEFAUT),
                        parent);

        this.entityManager.clear();

        final Produit trouveIdIgnore =
                this.service.findByObjetMetier(probeIdIgnore);

        final Produit trouveCasseIgnoree =
                this.service.findByObjetMetier(probeCasseIgnoree);

        assertProduitConformeALigneStockage(trouveIdIgnore, ligneStockage);
        assertProduitConformeALigneStockage(trouveCasseIgnoree, ligneStockage);

        assertThat(trouveIdIgnore.getIdProduit())
            .isEqualTo(Long.valueOf(idProduitStockage.longValue()));

        assertThat(trouveCasseIgnoree.getIdProduit())
            .isEqualTo(Long.valueOf(idProduitStockage.longValue()));

        assertThat(trouveCasseIgnoree.getIdProduit())
            .isEqualTo(trouveIdIgnore.getIdProduit());

        assertProduitsComplets(List.of(trouveIdIgnore, trouveCasseIgnoree));

        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    

    
    // ========================== findByLibelle ===========================



    /**
     * <div>
     * <p>garantit que findByLibelle(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_NULL)
    @Test
    public void testFindByLibelleNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByLibelle(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* ACT - ASSERT :
         * garantit que service.findByLibelle(null)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByLibelle(null))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByLibelle(...)
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_BLANK)
    @Test
    public void testFindByLibelleBlank() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByLibelle(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* ACT - ASSERT :
         * garantit que service.findByLibelle(BLANK)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByLibelle(BLANK))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByLibelle(...)
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(non trouvé) :</p>
     * <ul>
     * <li>prouve d'abord l'absence du libellé recherché dans le stockage ;</li>
     * <li>retourne une liste non null et vide ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_NON_TROUVE)
    @Test
    public void testFindByLibelleNonTrouve() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByLibelle(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Vérifie par SQL direct qu'aucun Produit ne porte
         * le libellé INTROUVABLE dans le stockage,
         * sans tenir compte des majuscules/minuscules.
         */
        final Long countCorrespondancesStockage =
                this.jdbcTemplate.queryForObject(
                        SELECT_COUNT_PRODUITS_WHERE_LIBELLE,
                        Long.class,
                        INTROUVABLE);

        assertThat(countCorrespondancesStockage).isNotNull().isZero();

        /*
         * Neutralise explicitement le contexte Hibernate
         * avant l'appel de lecture,
         * afin d'éviter tout raisonnement biaisé par le cache.
         */
        this.entityManager.clear();

        /* ACT :
         * sollicite service.findByLibelle(...)
         * avec le libellé INTROUVABLE,
         * absent du stockage (prouvé par SQL).
         */
        final List<Produit> resultats =
                this.service.findByLibelle(INTROUVABLE);

        /* ASSERT :
         * vérifie que le service retourne une liste vide (pas null)
         * lors de l'appel service.findByLibelle(...)
         * avec un libellé non trouvé dans le stockage.
         */
        assertThat(resultats).isNotNull().isEmpty();

        /*
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByLibelle(...)
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(case-insensitive) :</p>
     * <ul>
     * <li>retrouve les objets métier même lorsque le libellé recherché
     * est fourni avec une casse différente de celle du stockage ;</li>
     * <li>retourne exactement les enregistrements présents dans le stockage
     * pour ce libellé ;</li>
     * <li>retourne des objets métier complets avec le graphe
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_CASE_INSENSITIVE)
    @Test
    public void testFindByLibelleCaseInsensitive() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByLibelle(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Prépare le même libellé que celui du stockage,
         * mais avec une casse différente,
         * afin de vérifier le comportement case-insensitive
         * de service.findByLibelle(...).
         */
        final String libelleMajuscule =
                CHEMISE_ML_HOMME.toUpperCase(LOCALE_DEFAUT);

        /*
         * Lit directement dans le stockage les lignes qui doivent être
         * retournées par le service pour ce libellé, sans tenir compte
         * des majuscules/minuscules.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE_ORDONNES,
                        libelleMajuscule);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        final Long countCorrespondancesStockage =
                this.jdbcTemplate.queryForObject(
                        SELECT_COUNT_PRODUITS_WHERE_LIBELLE,
                        Long.class,
                        libelleMajuscule);

        assertThat(countCorrespondancesStockage).isNotNull().isPositive();
        assertThat(lignesStockage).hasSize(countCorrespondancesStockage.intValue());

        final List<String> clesMetierStockage =
                construireClesMetierDepuisLignesStockage(lignesStockage);

        assertThat(clesMetierStockage).doesNotHaveDuplicates();

        /*
         * Neutralise explicitement le contexte Hibernate
         * avant l'appel de lecture,
         * afin d'éviter tout raisonnement biaisé par le cache.
         */
        this.entityManager.clear();

        /* ACT :
         * sollicite service.findByLibelle(...)
         * avec le libellé en majuscules.
         */
        final List<Produit> resultats =
                this.service.findByLibelle(libelleMajuscule);

        /* ASSERT :
         * vérifie d'abord que la méthode retourne
         * une liste non null et non vide de même taille
         * que la projection SQL attendue.
         */
        assertThat(resultats).isNotNull().isNotEmpty();
        assertThat(resultats).hasSize(lignesStockage.size());

        /*
         * Compare ligne à ligne le résultat du service
         * avec la projection SQL ordonnée.
         */
        for (int index = 0; index < resultats.size(); index++) {
            assertProduitConformeALigneStockage(
                    resultats.get(index),
                    lignesStockage.get(index));
        }

        assertProduitsComplets(resultats);

        final List<String> clesMetierResultats =
                construireClesMetierDepuisProduits(resultats);

        assertThat(clesMetierResultats)
            .containsExactlyElementsOf(clesMetierStockage);

        assertThat(clesMetierResultats).doesNotHaveDuplicates();

        /*
         * Vérifie que le libellé retourné est bien le libellé stocké,
         * et non la casse fournie en entrée.
         */
        assertThat(resultats)
            .extracting(Produit::getProduit)
            .allMatch(CHEMISE_ML_HOMME::equals);

        /*
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByLibelle(...)
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(OK) :</p>
     * <ul>
     * <li>retourne une liste non null et non vide ;</li>
     * <li>retourne exactement les enregistrements présents dans le stockage
     * pour ce libellé ;</li>
     * <li>retourne uniquement le libellé recherché ;</li>
     * <li>retourne des objets métier complets avec le graphe
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>retourne une liste sans doublon métier ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_NOMINAL)
    @Test
    public void testFindByLibelleNominal() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByLibelle(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Lit directement dans le stockage les lignes qui doivent être
         * retournées par le service pour le libellé recherché.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE_ORDONNES,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        final Long countCorrespondancesStockage =
                this.jdbcTemplate.queryForObject(
                        SELECT_COUNT_PRODUITS_WHERE_LIBELLE,
                        Long.class,
                        CHEMISE_ML_HOMME);

        assertThat(countCorrespondancesStockage).isNotNull().isPositive();
        assertThat(lignesStockage).hasSize(countCorrespondancesStockage.intValue());

        final List<String> clesMetierStockage =
                construireClesMetierDepuisLignesStockage(lignesStockage);

        assertThat(clesMetierStockage).doesNotHaveDuplicates();

        /*
         * Neutralise explicitement le contexte Hibernate
         * avant l'appel de lecture,
         * afin d'éviter tout raisonnement biaisé par le cache.
         */
        this.entityManager.clear();

        /* ACT :
         * sollicite service.findByLibelle(...)
         * avec le libellé CHEMISE_ML_HOMME.
         */
        final List<Produit> resultats =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        /* ASSERT :
         * vérifie d'abord que la méthode retourne
         * une liste non null et non vide de même taille
         * que la projection SQL attendue.
         */
        assertThat(resultats).isNotNull().isNotEmpty();
        assertThat(resultats).hasSize(lignesStockage.size());

        /*
         * Compare ligne à ligne le résultat du service
         * avec la projection SQL ordonnée.
         */
        for (int index = 0; index < resultats.size(); index++) {
            assertProduitConformeALigneStockage(
                    resultats.get(index),
                    lignesStockage.get(index));
        }

        assertProduitsComplets(resultats);

        final List<String> clesMetierResultats =
                construireClesMetierDepuisProduits(resultats);

        assertThat(clesMetierResultats)
            .containsExactlyElementsOf(clesMetierStockage);

        assertThat(clesMetierResultats).doesNotHaveDuplicates();

        /*
         * Vérifie que tous les objets métier retournés
         * portent bien le libellé recherché.
         */
        assertThat(resultats)
            .extracting(Produit::getProduit)
            .allMatch(CHEMISE_ML_HOMME::equals);

        /*
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByLibelle(...)
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

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
     * <p>Reconstruit un parent {@link SousTypeProduit} complet
     * à partir d'un parent seedé relu via le service.</p>
     * <p>Un {@link Produit} nominal ne peut pas être construit
     * avec un parent {@link SousTypeProduit} partiel, car la conversion
     * métier vers JPA convertit aussi le graphe parent :</p>
     * <pre>
     * Produit -> SousTypeProduit -> TypeProduit
     * </pre>
     * <p>Cette méthode garantit donc que le parent de l'objet métier
     * à créer porte bien :</p>
     * <ul>
     * <li>son identifiant de stockage ;</li>
     * <li>son libellé métier ;</li>
     * <li>son parent {@link TypeProduit} complet.</li>
     * </ul>
     * </div>
     *
     * @param pParentSeed SousTypeProduitI : parent issu d'une donnée seedée.
     * @return SousTypeProduit : parent métier complet.
     */
    private SousTypeProduit construireParentCompletDepuisSeed(
            final SousTypeProduitI pParentSeed) {

        assertThat(pParentSeed).isNotNull();
        assertThat(pParentSeed.getIdSousTypeProduit()).isNotNull();
        assertThat(pParentSeed.getSousTypeProduit()).isNotNull().isNotBlank();
        assertThat(pParentSeed.getTypeProduit()).isNotNull();
        assertThat(pParentSeed.getTypeProduit().getIdTypeProduit()).isNotNull();
        assertThat(pParentSeed.getTypeProduit().getTypeProduit()).isNotNull().isNotBlank();

        final TypeProduit typeProduitParent = new TypeProduit(
                pParentSeed.getTypeProduit().getIdTypeProduit(),
                pParentSeed.getTypeProduit().getTypeProduit());

        return new SousTypeProduit(
                pParentSeed.getIdSousTypeProduit(),
                pParentSeed.getSousTypeProduit(),
                typeProduitParent);

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

    
    
    /**
     * <div>
     * <p>Vérifie qu'un {@link Produit} retourné par le service
     * correspond exactement à une ligne de projection SQL.</p>
     * <p>Le parent TypeProduit est contrôlé ici pour garantir
     * le graphe métier complet, pas comme critère direct
     * de clé métier Produit.</p>
     * </div>
     *
     * @param pProduit Produit : objet métier retourné par le service.
     * @param pLigneStockage Map : ligne SQL projetée depuis le stockage.
     */
    private void assertProduitConformeALigneStockage(
            final Produit pProduit,
            final java.util.Map<String, Object> pLigneStockage) {

        assertThat(pProduit).isNotNull();
        assertThat(pLigneStockage).isNotNull();

        final Number idProduitStockage =
                (Number) pLigneStockage.get("ID_PRODUIT");

        final String libelleProduitStockage =
                String.valueOf(pLigneStockage.get("PRODUIT"));

        final Number idSousTypeProduitStockage =
                (Number) pLigneStockage.get("ID_SOUS_TYPE_PRODUIT");

        final String libelleSousTypeProduitStockage =
                String.valueOf(pLigneStockage.get("SOUS_TYPE_PRODUIT"));

        final Number idTypeProduitStockage =
                (Number) pLigneStockage.get("ID_TYPE_PRODUIT");

        final String libelleTypeProduitStockage =
                String.valueOf(pLigneStockage.get("TYPE_PRODUIT"));

        assertThat(pProduit.getIdProduit())
            .isEqualTo(Long.valueOf(idProduitStockage.longValue()));
        assertThat(pProduit.getProduit()).isEqualTo(libelleProduitStockage);

        assertThat(pProduit.getSousTypeProduit()).isNotNull();
        assertThat(pProduit.getSousTypeProduit().getIdSousTypeProduit())
            .isEqualTo(Long.valueOf(idSousTypeProduitStockage.longValue()));
        assertThat(pProduit.getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(libelleSousTypeProduitStockage);

        assertThat(pProduit.getSousTypeProduit().getTypeProduit()).isNotNull();
        assertThat(pProduit.getSousTypeProduit().getTypeProduit().getIdTypeProduit())
            .isEqualTo(Long.valueOf(idTypeProduitStockage.longValue()));
        assertThat(pProduit.getSousTypeProduit().getTypeProduit().getTypeProduit())
            .isEqualTo(libelleTypeProduitStockage);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>Vérifie que tous les {@link Produit} retournés
     * portent le graphe métier complet :</p>
     * <pre>
     * Produit -> SousTypeProduit -> TypeProduit
     * </pre>
     * <p>Le contrôle du TypeProduit sert ici uniquement
     * à prouver que le parent SousTypeProduit est complet.</p>
     * </div>
     *
     * @param pProduits List&lt;Produit&gt; : objets métier à contrôler.
     */
    private void assertProduitsComplets(final List<Produit> pProduits) {

        assertThat(pProduits).isNotNull();

        assertThat(pProduits)
            .allSatisfy(produit -> {
                assertThat(produit).isNotNull();
                assertThat(produit.getIdProduit()).isNotNull();
                assertThat(produit.getProduit()).isNotBlank();

                assertThat(produit.getSousTypeProduit()).isNotNull();
                assertThat(produit.getSousTypeProduit().getIdSousTypeProduit()).isNotNull();
                assertThat(produit.getSousTypeProduit().getSousTypeProduit()).isNotBlank();

                assertThat(produit.getSousTypeProduit().getTypeProduit()).isNotNull();
                assertThat(produit.getSousTypeProduit().getTypeProduit().getIdTypeProduit()).isNotNull();
                assertThat(produit.getSousTypeProduit().getTypeProduit().getTypeProduit()).isNotBlank();
            });

    } // __________________________________________________________________



    /**
     * <div>
     * <p>Construit les clés de contrôle métier Produit
     * à partir d'une projection SQL.</p>
     * <p>La clé de contrôle métier utilisée par le test est :</p>
     * <pre>
     * SousTypeProduit parent + libellé Produit
     * </pre>
     * <p>Le TypeProduit n'est pas intégré à cette clé :
     * il est contrôlé séparément comme partie du graphe complet
     * du parent SousTypeProduit.</p>
     * </div>
     *
     * @param pLignesStockage List&lt;Map&lt;String, Object&gt;&gt; :
     * lignes SQL projetées depuis le stockage.
     * @return List&lt;String&gt; : clés métier normalisées.
     */
    private List<String> construireClesMetierDepuisLignesStockage(
            final List<java.util.Map<String, Object>> pLignesStockage) {

        assertThat(pLignesStockage).isNotNull();

        return pLignesStockage.stream()
                .map(ligne -> {
                    final Number idSousTypeProduit =
                            (Number) ligne.get("ID_SOUS_TYPE_PRODUIT");

                    return idSousTypeProduit.longValue()
                            + "|"
                            + String.valueOf(ligne.get("PRODUIT"))
                                    .toLowerCase(LOCALE_DEFAUT);
                })
                .toList();

    } // __________________________________________________________________



    /**
     * <div>
     * <p>Construit les clés de contrôle métier Produit
     * à partir d'une liste d'objets métier.</p>
     * <p>La clé de contrôle métier utilisée par le test est :</p>
     * <pre>
     * SousTypeProduit parent + libellé Produit
     * </pre>
     * <p>Le TypeProduit n'est pas intégré à cette clé :
     * il est contrôlé séparément comme partie du graphe complet
     * du parent SousTypeProduit.</p>
     * </div>
     *
     * @param pProduits List&lt;Produit&gt; : objets métier.
     * @return List&lt;String&gt; : clés métier normalisées.
     */
    private List<String> construireClesMetierDepuisProduits(
            final List<Produit> pProduits) {

        assertProduitsComplets(pProduits);

        return pProduits.stream()
                .map(produit -> produit.getSousTypeProduit()
                                .getIdSousTypeProduit()
                        + "|"
                        + produit.getProduit()
                                .toLowerCase(LOCALE_DEFAUT))
                .toList();

    } // __________________________________________________________________
    

    
} // FIN DE LA CLASSE ProduitGatewayJPAServiceIntegrationTest.-------------

