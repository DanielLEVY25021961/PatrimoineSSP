/* ********************************************************************* */
/* ******************* TEST INTEGRATION GATEWAY JPA ******************** */
/* ********************************************************************* */
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
     * <p>"classpath:truncate-test.sql"</p>
     * </div>
     */
    public static final String CLASSPATH_TRUNCATE_SQL 
    	= "classpath:truncate-test.sql";

    /**
     * <div>
     * <p>"ProduitGatewayJPAService"</p>
     * </div>
     */
    public static final String QUALIFIER_SERVICE 
    	= "ProduitGatewayJPAService";

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
     * <p>"temp-parent-sans-enfant-produit"</p>
     * </div>
     */
    public static final String TEMP_PARENT_SANS_ENFANT_PRODUIT
        = "temp-parent-sans-enfant-produit";

    /**
     * <div>
     * <p>"vêtement"</p>
     * </div>
     */
    public static final String TYPE_PRODUIT_VETEMENT = "vêtement";
    
    /**
     * <div>
     * <p>"vêtement pour femme"</p>
     * </div>
     */
    public static final String SOUS_TYPE_PRODUIT_FEMME
        = "vêtement pour femme";
    
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
	 * "ID_TYPE_PRODUIT"
	 */
	public static final String ID_TYPE_PRODUIT = "ID_TYPE_PRODUIT";
	
	/**
	 * "TYPE_PRODUIT"
	 */
	public static final String TYPE_PRODUIT = "TYPE_PRODUIT";

	/**
	 * "ID_SOUS_TYPE_PRODUIT"
	 */
	public static final String ID_SOUS_TYPE_PRODUIT = "ID_SOUS_TYPE_PRODUIT";
	
	/**
	 * "SOUS_TYPE_PRODUIT"
	 */
	public static final String SOUS_TYPE_PRODUIT = "SOUS_TYPE_PRODUIT";
	
	/**
	 * "ID_PRODUIT"
	 */
	public static final String ID_PRODUIT = "ID_PRODUIT";

	/**
	 * "PRODUIT"
	 */
	public static final String PRODUIT = "PRODUIT";

    // ============================ Tags ================================//
    
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

    // ======================= DisplayNames =============================//
    
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

	/** "creer(doublon) - jette UnexpectedRollbackException et ne crée aucun nouvel enregistrement" */
	public static final String DN_CREER_DOUBLON
	    = "creer(doublon) - jette UnexpectedRollbackException et ne crée aucun nouvel enregistrement";

    /**
     * <div>
     * <p>"creer(nominal) - ajoute un élément, le rend retrouvable et conserve (ne wipe pas) les données seedées"</p>
     * </div>
     */
    public static final String DN_CREER_NOMINAL 
        = "creer(nominal) - ajoute un élément, le rend retrouvable et conserve (ne wipe pas) les données seedées";

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

    /** "findByObjetMetier(id ignoré et casse ignorée) - retrouve l'objet métier correspondant" */
    public static final String DN_FINDBYOBJETMETIER_ID_IGNORE_CASSE_IGNOREE
        = "findByObjetMetier(id ignoré et casse ignorée) - retrouve l'objet métier correspondant";
    
    /** "findByObjetMetier(parent null) - jette ExceptionAppliParentNull (contrat du port)" */
	public static final String DN_FINDBYOBJETMETIER_PARENT_NULL 
		= "findByObjetMetier(parent null) - jette ExceptionAppliParentNull (contrat du port)";

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

	/** "findByLibelle(case-insensitive) - retrouve le libellé malgré une casse différente" */
    public static final String DN_FINDBYLIBELLE_CASE_INSENSITIVE
        = "findByLibelle(case-insensitive) - retrouve le libellé malgré une casse différente";

    /** "findByLibelle(OK) - retourne les correspondances exactes" */
    public static final String DN_FINDBYLIBELLE_NOMINAL
        = "findByLibelle(OK) - retourne les correspondances exactes";
    
    /** "findByLibelleRapide(null) - jette ExceptionAppliParamNull (contrat du port)" */
    public static final String DN_FINDBYLIBELLERAPIDE_NULL
        = "findByLibelleRapide(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** "findByLibelleRapide(blank) - délègue à rechercherTous()" */
    public static final String DN_FINDBYLIBELLERAPIDE_BLANK
        = "findByLibelleRapide(blank) - délègue à rechercherTous()";

    /** "findByLibelleRapide(non trouvé) - retourne une liste vide" */
    public static final String DN_FINDBYLIBELLERAPIDE_NON_TROUVE
        = "findByLibelleRapide(non trouvé) - retourne une liste vide";

    /** "findByLibelleRapide(case-insensitive) - recherche insensible à la casse" */
    public static final String DN_FINDBYLIBELLERAPIDE_CASE_INSENSITIVE
        = "findByLibelleRapide(case-insensitive) - recherche insensible à la casse";

    /** "findByLibelleRapide(dédoublonnage) - pas de doublons dans les résultats" */
    public static final String DN_FINDBYLIBELLERAPIDE_DEDOUBLONNAGE
        = "findByLibelleRapide(dédoublonnage) - pas de doublons dans les résultats";

    /** "findByLibelleRapide(OK) - retourne les correspondances partielles" */
    public static final String DN_FINDBYLIBELLERAPIDE_NOMINAL
        = "findByLibelleRapide(OK) - retourne les correspondances partielles";

    /** "findAllByParent(null) - jette ExceptionAppliParentNull (contrat du port)" */
    public static final String DN_FINDALLBYPARENT_NULL
        = "findAllByParent(null) - jette ExceptionAppliParentNull (contrat du port)";

    /** "findAllByParent(parent libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDALLBYPARENT_PARENT_LIBELLE_NULL
        = "findAllByParent(parent libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findAllByParent(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDALLBYPARENT_PARENT_LIBELLE_BLANK
        = "findAllByParent(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findAllByParent(parent id null) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
    public static final String DN_FINDALLBYPARENT_PARENT_ID_NULL
        = "findAllByParent(parent id null) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** "findAllByParent(parent absent) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
    public static final String DN_FINDALLBYPARENT_PARENT_ABSENT
        = "findAllByParent(parent absent) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** "findAllByParent(parent sans enfant) - retourne une liste vide" */
    public static final String DN_FINDALLBYPARENT_PARENT_SANS_ENFANT
        = "findAllByParent(parent sans enfant) - retourne une liste vide";

    /** "findAllByParent(OK) - retourne les enfants du parent" */
    public static final String DN_FINDALLBYPARENT_NOMINAL
        = "findAllByParent(OK) - retourne les enfants du parent";
    
    /** "findById(null) - jette ExceptionAppliParamNull (contrat du port)" */
    public static final String DN_FINDBYID_NULL
        = "findById(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** "findById(non trouvé) - retourne null" */
    public static final String DN_FINDBYID_NON_TROUVE
        = "findById(non trouvé) - retourne null";

    /** "findById(OK) - retourne l'objet métier correspondant" */
    public static final String DN_FINDBYID_NOMINAL
        = "findById(OK) - retourne l'objet métier correspondant";

    /** "findById(ID créé) - retrouve l'objet métier nouvellement persisté" */
    public static final String DN_FINDBYID_ID_CREE
        = "findById(ID créé) - retrouve l'objet métier nouvellement persisté";
    
    /** "update(null) - jette ExceptionAppliParamNull (contrat du port)" */
    public static final String DN_UPDATE_NULL
        = "update(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** "update(libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_UPDATE_LIBELLE_NULL
        = "update(libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "update(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_UPDATE_BLANK
        = "update(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "update(id null) - jette ExceptionAppliParamNonPersistent (contrat du port)" */
    public static final String DN_UPDATE_ID_NULL
        = "update(id null) - jette ExceptionAppliParamNonPersistent (contrat du port)";

    /** "update(parent null) - jette ExceptionAppliParentNull (contrat du port)" */
    public static final String DN_UPDATE_PARENT_NULL
        = "update(parent null) - jette ExceptionAppliParentNull (contrat du port)";

    /** "update(parent libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_UPDATE_PARENT_LIBELLE_NULL
        = "update(parent libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "update(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_UPDATE_PARENT_LIBELLE_BLANK
        = "update(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "update(parent id null) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
    public static final String DN_UPDATE_PARENT_ID_NULL
        = "update(parent id null) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** "update(parent absent) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
    public static final String DN_UPDATE_PARENT_ABSENT
        = "update(parent absent) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** "update(absent) - retourne null" */
    public static final String DN_UPDATE_ABSENT
        = "update(absent) - retourne null";

    /** "update(sans modification) - retourne l'objet persistant inchangé" */
    public static final String DN_UPDATE_SANS_MODIFICATION
        = "update(sans modification) - retourne l'objet persistant inchangé";

    /** "update(OK) - modifie le libellé et retourne l'objet modifié" */
    public static final String DN_UPDATE_NOMINAL
        = "update(OK) - modifie le libellé et retourne l'objet modifié";

    /** "update(parent modifié) - modifie le parent et retourne l'objet modifié" */
    public static final String DN_UPDATE_PARENT_MODIFIE
        = "update(parent modifié) - modifie le parent et retourne l'objet modifié";
    
    /** "delete(null) - jette ExceptionAppliParamNull (contrat du port)" */
    public static final String DN_DELETE_NULL
        = "delete(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** "delete(id null) - jette ExceptionAppliParamNonPersistent (contrat du port)" */
    public static final String DN_DELETE_ID_NULL
        = "delete(id null) - jette ExceptionAppliParamNonPersistent (contrat du port)";

    /** "delete(absent) - ne modifie pas le stockage" */
    public static final String DN_DELETE_ABSENT
        = "delete(absent) - ne modifie pas le stockage";

    /** "delete(OK) - supprime l'objet métier du stockage" */
    public static final String DN_DELETE_NOMINAL
        = "delete(OK) - supprime l'objet métier du stockage";

    /** "delete(double suppression) - le second appel ne modifie pas le stockage" */
    public static final String DN_DELETE_DOUBLE_SUPPRESSION
        = "delete(double suppression) - le second appel ne modifie pas le stockage";
    
    /** "count(stockage vide) - retourne 0" */
    public static final String DN_COUNT_STOCKAGE_VIDE
        = "count(stockage vide) - retourne 0";

    /** "count() - cohérent avec SQL et rechercherTous()" */
    public static final String DN_COUNT_NOMINAL
        = "count() - cohérent avec SQL et rechercherTous()";

    /** "count(après création puis suppression) - suit l'état du stockage" */
    public static final String DN_COUNT_APRES_CREATION_PUIS_SUPPRESSION
        = "count(après création puis suppression) - suit l'état du stockage";
    
    // Messages d'erreur (alignés sur ProduitGatewayIService) ***********//
    
    /**
     * <div>
	 * <p>"Anomalie applicative 
	 * - l'objet métier passé en paramètre est null."</p>
	 * </div>
     */
    public static final String MSG_CREER_KO_PARAM_NULL 
    	= ProduitGatewayIService.MESSAGE_CREER_KO_PARAM_NULL;

    /**
     * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un libellé blank
	 * (null ou que des espaces)."</p>
	 * </div>
     */
    public static final String MSG_CREER_KO_LIBELLE_BLANK 
    	= ProduitGatewayIService.MESSAGE_CREER_KO_LIBELLE_BLANK;

    /**
     * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un parent null."</p>
	 * </div>
     */
    public static final String MSG_CREER_KO_PARENT_NULL 
    	= ProduitGatewayIService.MESSAGE_CREER_KO_PARENT_NULL;
    
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
     * <p>"Anomalie applicative - 
     * le parent de l'objet que vous voulez créer n'existe
     * pas déjà dans le stockage : "</p>
     * </div> 
     */
    public static final String MSG_CREER_KO_PARENT_NON_PERSISTENT
        = ProduitGatewayIService.MESSAGE_CREER_KO_PARENT_NON_PERSISTENT;
    
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
	 * - le parent de l'objet à créer a un libellé blank
	 * (null ou que des espaces)."</p>
	 * </div>
	 */
	public static final String MSG_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK
	    = ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK;

	/** 
     * <div>
     * <p>"Anomalie applicative 
     * - le parent de l'objet que vous voulez rechercher n'existe
     * pas déjà dans le stockage : "</p>
     * </div>
     */
    public static final String MSG_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT 
        = ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT;
    
    /**
     * <div>
	 * <p>"Anomalie applicative
	 * - le parent de l'objet passé en paramètre a un libellé blank
	 * (null ou que des espaces)."</p>
	 * </div>
     */
    public static final String MSG_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK
        = ProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK;

    /**
     * <div>
	 * <p>"Anomalie applicative 
	 * - le parent de l'objet n'existait
	 * pas déjà dans le stockage : "</p>
	 * </div>
     */
    public static final String MSG_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
        = ProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT;
    
    /**
     * <div>
     * <p>"Anomalie applicative - l'objet métier passé en paramètre a un parent null."</p>
     * </div>
     */
    public static final String MSG_UPDATE_KO_PARENT_NULL
        = ProduitGatewayIService.MESSAGE_UPDATE_KO_PARENT_NULL;

    /**
     * <div>
     * <p>"Anomalie applicative - le parent de l'objet à modifier a un libellé blank (null ou que des espaces)."</p>
     * </div>
     */
    public static final String MSG_UPDATE_KO_LIBELLE_PARENT_BLANK
        = ProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK;

    /**
     * <div>
     * <p>"Anomalie applicative - le parent de l'objet que vous voulez modifier n'existe pas déjà dans le stockage : "</p>
     * </div>
     */
    public static final String MSG_UPDATE_KO_PARENT_NON_PERSISTENT
        = ProduitGatewayIService.MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT;

    // ======================== CLAUSES SQL =============================//
    
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

    /** "SELECT COUNT(*) FROM PRODUITS WHERE LOWER(PRODUIT) LIKE LOWER(?)" */
    public static final String SELECT_COUNT_PRODUITS_WHERE_LIBELLE_CONTIENT
        = "SELECT COUNT(*) FROM PRODUITS WHERE LOWER(PRODUIT) LIKE LOWER(?)";

    /**
     * <div>
     * <p>Projection SQL complète des Produits par contenu de libellé,
     * avec leur parent SousTypeProduit et le parent TypeProduit,
     * ordonnée comme le service findByLibelleRapide(...).</p>
     * </div>
     */
    public static final String SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE_CONTIENT_ORDONNES
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
          WHERE LOWER(p.PRODUIT) LIKE LOWER(?)
          ORDER BY
              LOWER(stp.SOUS_TYPE_PRODUIT),
              LOWER(p.PRODUIT),
              p.ID_PRODUIT
          """;
    
    /** "SELECT COUNT(*) FROM PRODUITS WHERE SOUS_TYPE_PRODUIT = ?" */
    public static final String SELECT_COUNT_PRODUITS_WHERE_PARENT
        = "SELECT COUNT(*) FROM PRODUITS WHERE SOUS_TYPE_PRODUIT = ?";

    /** "SELECT ID_TYPE_PRODUIT FROM TYPES_PRODUIT WHERE TYPE_PRODUIT = ?" */
    public static final String SELECT_ID_TYPE_PRODUIT_WHERE_LIBELLE
        = "SELECT ID_TYPE_PRODUIT FROM TYPES_PRODUIT WHERE TYPE_PRODUIT = ?";

    /** "SELECT ID_SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE SOUS_TYPE_PRODUIT = ?" */
    public static final String SELECT_ID_SOUS_TYPE_PRODUIT_WHERE_LIBELLE
        = "SELECT ID_SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE SOUS_TYPE_PRODUIT = ?";

    /** "INSERT INTO SOUS_TYPES_PRODUIT (SOUS_TYPE_PRODUIT, TYPE_PRODUIT) VALUES (?, ?)" */
    public static final String INSERT_SOUS_TYPE_PRODUIT
        = "INSERT INTO SOUS_TYPES_PRODUIT (SOUS_TYPE_PRODUIT, TYPE_PRODUIT) VALUES (?, ?)";

    /** "DELETE FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?" */
    public static final String DELETE_FROM_SOUS_TYPES_PRODUIT_WHERE_ID
        = "DELETE FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?";

    /**
     * <div>
     * <p>Projection SQL complète des Produits par parent SousTypeProduit,
     * avec leur parent SousTypeProduit et le parent TypeProduit,
     * ordonnée comme le service findAllByParent(...).</p>
     * </div>
     */
    public static final String SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_PARENT_ORDONNES
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
          WHERE p.SOUS_TYPE_PRODUIT = ?
          ORDER BY
              LOWER(stp.SOUS_TYPE_PRODUIT),
              LOWER(p.PRODUIT),
              p.ID_PRODUIT
          """;
    
    /**
     * <div>
     * <p>Projection SQL complète d'un Produit par identifiant,
     * avec son parent SousTypeProduit et le parent TypeProduit.</p>
     * </div>
     */
    public static final String SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_ID
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
          WHERE p.ID_PRODUIT = ?
          ORDER BY
              p.ID_PRODUIT
          """;
    
    /** "UPDATE PRODUITS SET PRODUIT = ?, SOUS_TYPE_PRODUIT = ? WHERE ID_PRODUIT = ?" */
    public static final String UPDATE_PRODUITS_SET_LIBELLE_PARENT_WHERE_ID
        = "UPDATE PRODUITS SET PRODUIT = ?, SOUS_TYPE_PRODUIT = ? WHERE ID_PRODUIT = ?";

    // ************************* ATTRIBUTS *******************************/

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
     * <p>DAO objet métier (enfant du parent SousTypeProduit) 
     * utilisé pour les contrôles directs
     * de comptage et de cohérence avec le stockage.</p>
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
     * <p>JdbcTemplate pour vérifications directes dans le stockage.</p>
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
     * 
     * @throws Exception
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
     * 
     * @throws Exception
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
     * 
     * @throws Exception
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
     * 
     * @throws Exception
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
     * 
     * @throws Exception
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
     * 
     * @throws Exception
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
     * 
     * @throws Exception
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
     * 
     * @throws Exception
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
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT_WHERE_ID,
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
         * Lit ensuite directement (en SQL) dans le stockage :
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
         * La clé métier Produit est :
         * - parent SousTypeProduit ;
         * - libellé Produit.
         *
         * Le TypeProduit n'est pas intégré à cette clé. Il est contrôlé
         * séparément comme partie du graphe complet du parent
         * SousTypeProduit.
         */
        final List<String> clesMetierStockage =
                construireClesMetierDepuisLignesStockage(lignesStockage);

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

        /*
         * Neutralise explicitement le contexte Hibernate
         * avant l'appel de lecture,
         * afin d'éviter tout raisonnement biaisé par le cache.
         */
        this.entityManager.clear();

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
            assertProduitConformeALigneStockage(
                    resultats.get(index),
                    lignesStockage.get(index));
        }

        /*
         * Vérifie les propriétés métier générales de la liste retournée :
         * - chaque Produit est complet ;
         * - chaque Produit porte un libellé exploitable ;
         * - chaque parent SousTypeProduit est complet ;
         * - chaque parent TypeProduit est complet.
         */
        assertProduitsComplets(resultats);

        /*
         * Extrait les clés métier des objets retournés par le service.
         *
         * Cette extraction permet de vérifier explicitement
         * le dédoublonnage métier après conversion JPA -> métier,
         * sans intégrer le TypeProduit dans la clé métier directe
         * du Produit.
         */
        final List<String> clesMetierResultats =
                construireClesMetierDepuisProduits(resultats);

        /*
         * Vérifie que les objets métier retournés :
         * - correspondent exactement aux clés métier lues dans le stockage ;
         * - ne contiennent aucun doublon métier.
         */
        assertThat(clesMetierResultats)
            .containsExactlyElementsOf(clesMetierStockage);

        assertThat(clesMetierResultats).doesNotHaveDuplicates();

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
    
    

    // ===================== rechercherTousParPage ========================



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
                .map(ligne -> String.valueOf(ligne.get(PRODUIT)))
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

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByObjetMetier(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Lit directement dans le stockage un Produit seedé
         * afin de récupérer un parent SousTypeProduit réellement persistant.
         *
         * Le test veut isoler le contrôle contractuel du libellé Produit :
         * le parent de la sonde doit donc être valide.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        /*
         * Retient la ligne de stockage utilisée comme source
         * du parent persistant de la sonde.
         */
        final java.util.Map<String, Object> ligneStockage =
                lignesStockage.get(0);

        /*
         * Extrait l'identifiant du parent SousTypeProduit
         * depuis la projection SQL.
         */
        final Number idSousTypeProduitStockage =
                (Number) ligneStockage.get(ID_SOUS_TYPE_PRODUIT);

        /*
         * Extrait le libellé du parent SousTypeProduit.
         */
        final String libelleSousTypeProduitStockage =
                String.valueOf(ligneStockage.get(SOUS_TYPE_PRODUIT));

        /*
         * Prépare un parent métier valide et persistant.
         *
         * Le TypeProduit n'est pas nécessaire dans la sonde d'entrée :
         * ce test cible uniquement le rejet du libellé Produit null.
         */
        final SousTypeProduit parent =
                new SousTypeProduit(
                        Long.valueOf(idSousTypeProduitStockage.longValue()),
                        libelleSousTypeProduitStockage,
                        null);

        /*
         * Prépare une sonde dont le libellé Produit est null.
         *
         * Tous les autres éléments utiles sont valides afin que
         * l'exception vérifiée porte uniquement sur le libellé Produit.
         */
        final Produit probe = new Produit(null, null, parent);

        /* ACT - ASSERT :
         * garantit que service.findByObjetMetier(probe)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(probe))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);

        /*
         * Compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByObjetMetier(...)
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

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByObjetMetier(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Lit directement dans le stockage un Produit seedé
         * afin de récupérer un parent SousTypeProduit réellement persistant.
         *
         * Le test veut isoler le contrôle contractuel du libellé Produit :
         * le parent de la sonde doit donc être valide.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        /*
         * Retient la ligne de stockage utilisée comme source
         * du parent persistant de la sonde.
         */
        final java.util.Map<String, Object> ligneStockage =
                lignesStockage.get(0);

        /*
         * Extrait l'identifiant du parent SousTypeProduit
         * depuis la projection SQL.
         */
        final Number idSousTypeProduitStockage =
                (Number) ligneStockage.get(ID_SOUS_TYPE_PRODUIT);

        /*
         * Extrait le libellé du parent SousTypeProduit.
         */
        final String libelleSousTypeProduitStockage =
                String.valueOf(ligneStockage.get(SOUS_TYPE_PRODUIT));

        /*
         * Prépare un parent métier valide et persistant.
         *
         * Le TypeProduit n'est pas nécessaire dans la sonde d'entrée :
         * ce test cible uniquement le rejet du libellé Produit blank.
         */
        final SousTypeProduit parent =
                new SousTypeProduit(
                        Long.valueOf(idSousTypeProduitStockage.longValue()),
                        libelleSousTypeProduitStockage,
                        null);

        /*
         * Prépare une sonde dont le libellé Produit est blank.
         *
         * Tous les autres éléments utiles sont valides afin que
         * l'exception vérifiée porte uniquement sur le libellé Produit.
         */
        final Produit probe = new Produit(null, BLANK, parent);

        /* ACT - ASSERT :
         * garantit que service.findByObjetMetier(probe)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(probe))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);

        /*
         * Compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByObjetMetier(...)
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

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByObjetMetier(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Prépare une sonde avec un libellé Produit valide
         * mais sans parent SousTypeProduit.
         *
         * Le test cible uniquement le contrôle contractuel
         * du parent obligatoire.
         */
        final Produit probe = new Produit(null, CHEMISE_ML_HOMME, null);

        /* ACT - ASSERT :
         * garantit que service.findByObjetMetier(probe)
         * - jette une ExceptionAppliParentNull
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(probe))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_PARENT_NULL);

        /*
         * Compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByObjetMetier(...)
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

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByObjetMetier(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Lit directement dans le stockage un Produit seedé
         * afin de récupérer l'identifiant d'un parent SousTypeProduit
         * réellement persistant.
         *
         * Le libellé du parent sera volontairement remplacé par null
         * dans la sonde.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        /*
         * Extrait l'identifiant du parent SousTypeProduit
         * pour construire une sonde dont seul le libellé parent est invalide.
         */
        final Number idSousTypeProduitStockage =
                (Number) lignesStockage.get(0).get(ID_SOUS_TYPE_PRODUIT);

        /*
         * Prépare un parent portant un identifiant persistant,
         * mais un libellé null.
         *
         * Le test cible uniquement le contrôle contractuel
         * du libellé du parent.
         */
        final SousTypeProduit parent =
                new SousTypeProduit(
                        Long.valueOf(idSousTypeProduitStockage.longValue()),
                        null,
                        null);

        /*
         * Prépare une sonde avec libellé Produit valide
         * et parent à libellé null.
         */
        final Produit probe = new Produit(null, CHEMISE_ML_HOMME, parent);

        /* ACT - ASSERT :
         * garantit que service.findByObjetMetier(probe)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(probe))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK);

        /*
         * Compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByObjetMetier(...)
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

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByObjetMetier(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Lit directement dans le stockage un Produit seedé
         * afin de récupérer l'identifiant d'un parent SousTypeProduit
         * réellement persistant.
         *
         * Le libellé du parent sera volontairement remplacé par blank
         * dans la sonde.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        /*
         * Extrait l'identifiant du parent SousTypeProduit
         * pour construire une sonde dont seul le libellé parent est invalide.
         */
        final Number idSousTypeProduitStockage =
                (Number) lignesStockage.get(0).get(ID_SOUS_TYPE_PRODUIT);

        /*
         * Prépare un parent portant un identifiant persistant,
         * mais un libellé blank.
         *
         * Le test cible uniquement le contrôle contractuel
         * du libellé du parent.
         */
        final SousTypeProduit parent =
                new SousTypeProduit(
                        Long.valueOf(idSousTypeProduitStockage.longValue()),
                        BLANK,
                        null);

        /*
         * Prépare une sonde avec libellé Produit valide
         * et parent à libellé blank.
         */
        final Produit probe = new Produit(null, CHEMISE_ML_HOMME, parent);

        /* ACT - ASSERT :
         * garantit que service.findByObjetMetier(probe)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(probe))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK);

        /*
         * Compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByObjetMetier(...)
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

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByObjetMetier(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Lit directement dans le stockage un Produit seedé
         * afin de récupérer un libellé parent valide.
         *
         * L'identifiant du parent sera volontairement remplacé par null
         * dans la sonde.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        /*
         * Extrait le libellé du parent SousTypeProduit
         * depuis la projection SQL.
         */
        final String libelleSousTypeProduitStockage =
                String.valueOf(lignesStockage.get(0).get(SOUS_TYPE_PRODUIT));

        /*
         * Prépare un parent avec libellé valide mais identifiant null.
         *
         * Le test cible uniquement le contrôle de persistance
         * du parent SousTypeProduit.
         */
        final SousTypeProduit parent =
                new SousTypeProduit(null, libelleSousTypeProduitStockage, null);

        /*
         * Prépare une sonde avec libellé Produit valide
         * et parent non persistant car ID null.
         */
        final Produit probe = new Produit(null, CHEMISE_ML_HOMME, parent);

        /* ACT - ASSERT :
         * garantit que service.findByObjetMetier(probe)
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT
         * + libelleSousTypeProduitStockage.
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(probe))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT
                    + libelleSousTypeProduitStockage);

        /*
         * Compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByObjetMetier(...)
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

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByObjetMetier(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Lit directement dans le stockage un Produit seedé
         * afin de récupérer un libellé parent valide.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        /*
         * Extrait le libellé du parent SousTypeProduit.
         *
         * Le parent préparé ensuite portera ce libellé valide,
         * mais un identifiant volontairement absent du stockage.
         */
        final String libelleSousTypeProduitStockage =
                String.valueOf(lignesStockage.get(0).get(SOUS_TYPE_PRODUIT));

        /*
         * Vérifie directement dans le stockage
         * qu'aucun parent SousTypeProduit ne porte ID_INEXISTANT.
         */
        final Long countParentStockage = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT_WHERE_ID,
                Long.class,
                ID_INEXISTANT);

        assertThat(countParentStockage).isNotNull().isZero();

        /*
         * Prépare un parent avec libellé valide mais identifiant absent.
         *
         * Le test cible le contrôle de persistance réelle
         * du parent dans le stockage.
         */
        final SousTypeProduit parent =
                new SousTypeProduit(
                        ID_INEXISTANT,
                        libelleSousTypeProduitStockage,
                        null);

        /*
         * Prépare une sonde avec libellé Produit valide
         * et parent absent du stockage.
         */
        final Produit probe = new Produit(null, CHEMISE_ML_HOMME, parent);

        /* ACT - ASSERT :
         * garantit que service.findByObjetMetier(probe)
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT
         * + libelleSousTypeProduitStockage.
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(probe))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT
                    + libelleSousTypeProduitStockage);

        /*
         * Compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByObjetMetier(...)
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

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByObjetMetier(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Lit directement dans le stockage un Produit seedé
         * afin de récupérer un parent SousTypeProduit réellement persistant.
         *
         * Le test cherche ensuite un libellé Produit absent
         * sous ce parent valide.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        /*
         * Retient la ligne de stockage utilisée comme source
         * du parent persistant.
         */
        final java.util.Map<String, Object> ligneStockage =
                lignesStockage.get(0);

        /*
         * Extrait l'identifiant du parent SousTypeProduit.
         */
        final Number idSousTypeProduitStockage =
                (Number) ligneStockage.get(ID_SOUS_TYPE_PRODUIT);

        /*
         * Extrait le libellé du parent SousTypeProduit.
         */
        final String libelleSousTypeProduitStockage =
                String.valueOf(ligneStockage.get(SOUS_TYPE_PRODUIT));

        final Long idParent =
                Long.valueOf(idSousTypeProduitStockage.longValue());

        /*
         * Vérifie directement dans le stockage que le couple métier
         * parent SousTypeProduit + libellé Produit INTROUVABLE
         * n'existe pas.
         */
        final Long countCoupleAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PRODUITS_WHERE_LIBELLE_AND_PARENT,
                Long.class,
                INTROUVABLE,
                idParent);

        assertThat(countCoupleAvant).isNotNull().isZero();

        /*
         * Prépare un parent persistant minimal.
         *
         * Le TypeProduit n'est pas nécessaire dans la sonde d'entrée :
         * la recherche porte sur parent SousTypeProduit + libellé Produit.
         */
        final SousTypeProduit parent =
                new SousTypeProduit(idParent, libelleSousTypeProduitStockage, null);

        /*
         * Prépare une sonde avec un libellé Produit prouvé absent
         * pour le parent persistant retenu.
         */
        final Produit probe = new Produit(null, INTROUVABLE, parent);

        /*
         * Neutralise explicitement le contexte Hibernate
         * avant l'appel de lecture afin d'éviter tout raisonnement
         * biaisé par le cache.
         */
        this.entityManager.clear();

        /* ACT :
         * appelle service.findByObjetMetier(probe)
         * avec une clé métier absente du stockage.
         */
        final Produit trouve = this.service.findByObjetMetier(probe);

        /* ASSERT :
         * vérifie que le service retourne null
         * lorsque le couple parent + libellé Produit est absent.
         */
        assertThat(trouve).isNull();

        /*
         * Compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByObjetMetier(...)
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

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByObjetMetier(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Lit directement dans le stockage le Produit seedé attendu.
         *
         * Cette projection SQL constitue la vérité de comparaison
         * indépendante du contexte Hibernate.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        /*
         * Retient la ligne SQL correspondant au Produit recherché.
         */
        final java.util.Map<String, Object> ligneStockage =
                lignesStockage.get(0);

        /*
         * Extrait l'identifiant du parent SousTypeProduit
         * depuis la ligne SQL attendue.
         */
        final Number idSousTypeProduitStockage =
                (Number) ligneStockage.get(ID_SOUS_TYPE_PRODUIT);

        /*
         * Extrait le libellé du parent SousTypeProduit.
         */
        final String libelleSousTypeProduitStockage =
                String.valueOf(ligneStockage.get(SOUS_TYPE_PRODUIT));

        final Long idParent =
                Long.valueOf(idSousTypeProduitStockage.longValue());

        /*
         * Vérifie directement dans le stockage que le couple métier
         * parent SousTypeProduit + libellé Produit recherché
         * existe exactement une fois.
         */
        final Long countCoupleAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PRODUITS_WHERE_LIBELLE_AND_PARENT,
                Long.class,
                CHEMISE_ML_HOMME,
                idParent);

        assertThat(countCoupleAvant).isNotNull().isEqualTo(1L);

        /*
         * Prépare un parent persistant minimal.
         *
         * Le TypeProduit n'est pas nécessaire dans la sonde d'entrée :
         * findByObjetMetier(...) recherche sur parent SousTypeProduit
         * + libellé Produit.
         */
        final SousTypeProduit parent =
                new SousTypeProduit(idParent, libelleSousTypeProduitStockage, null);

        /*
         * Prépare une sonde nominale portant la clé métier existante :
         * parent SousTypeProduit + libellé Produit.
         */
        final Produit probe = new Produit(null, CHEMISE_ML_HOMME, parent);

        /*
         * Neutralise explicitement le contexte Hibernate
         * avant l'appel de lecture afin d'éviter tout raisonnement
         * biaisé par le cache.
         */
        this.entityManager.clear();

        /* ACT :
         * appelle service.findByObjetMetier(probe)
         * avec une clé métier réellement présente dans le stockage.
         */
        final Produit trouve = this.service.findByObjetMetier(probe);

        /* ASSERT :
         * compare l'objet retourné par le service à la ligne SQL attendue.
         *
         * Ce helper prouve simultanément :
         * - l'identifiant Produit ;
         * - le libellé Produit ;
         * - le parent SousTypeProduit ;
         * - le parent TypeProduit du SousTypeProduit.
         */
        assertProduitConformeALigneStockage(trouve, ligneStockage);

        /*
         * Vérifie que le résultat porte le graphe complet attendu :
         * Produit -> SousTypeProduit -> TypeProduit.
         */
        assertProduitsComplets(List.of(trouve));

        /*
         * Compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByObjetMetier(...)
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
     * <p>Test didactique non contractuel.</p>
     * <p>garantit que findByObjetMetier(id ignoré et casse ignorée) :</p>
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
    @DisplayName(DN_FINDBYOBJETMETIER_ID_IGNORE_CASSE_IGNOREE)
    @Test
    public void testFindByObjetMetierIdIgnoreCasseIgnoree() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByObjetMetier(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Lit directement dans le stockage le Produit seedé
         * qui sert de vérité de comparaison.
         *
         * La projection SQL contient le graphe complet :
         * Produit -> SousTypeProduit -> TypeProduit.
         *
         * Cette lecture SQL est indépendante du contexte Hibernate
         * et permet de vérifier ensuite que les deux sondes retrouvent
         * exactement l'objet métier réellement présent dans le stockage.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        /*
         * Retient la première ligne de stockage correspondant
         * au libellé seedé CHEMISE_ML_HOMME.
         */
        final java.util.Map<String, Object> ligneStockage =
                lignesStockage.get(0);

        /*
         * Extrait l'identifiant Produit attendu.
         *
         * Cet identifiant sert ensuite à prouver que le service
         * retrouve l'objet persistant du stockage, et non l'identifiant
         * volontairement faux porté par les sondes.
         */
        final Number idProduitStockage =
                (Number) ligneStockage.get(ID_PRODUIT);

        /*
         * Extrait l'identifiant du parent SousTypeProduit.
         *
         * Le contrat findByObjetMetier(...) recherche un Produit
         * par clé métier :
         * - parent SousTypeProduit ;
         * - libellé Produit.
         */
        final Number idSousTypeProduitStockage =
                (Number) ligneStockage.get(ID_SOUS_TYPE_PRODUIT);

        /*
         * Extrait le libellé du parent SousTypeProduit.
         *
         * Le parent transmis à la sonde doit porter un libellé non blank
         * pour passer les contrôles applicatifs du Gateway.
         */
        final String libelleSousTypeProduitStockage =
                String.valueOf(ligneStockage.get(SOUS_TYPE_PRODUIT));

        final Long idParent =
                Long.valueOf(idSousTypeProduitStockage.longValue());

        /*
         * Prépare un parent métier minimal mais persistant.
         *
         * Le TypeProduit n'est pas nécessaire dans la sonde d'entrée :
         * pour findByObjetMetier(...), le Gateway vérifie la persistance
         * du parent SousTypeProduit et recherche ensuite le Produit
         * par parent SousTypeProduit + libellé Produit.
         *
         * Le graphe complet Produit -> SousTypeProduit -> TypeProduit
         * sera en revanche exigé sur le résultat retourné par le service.
         */
        final SousTypeProduit parent =
                new SousTypeProduit(idParent, libelleSousTypeProduitStockage, null);

        /*
         * Prépare une première sonde avec un identifiant volontairement faux.
         *
         * Le test prouve ainsi que findByObjetMetier(...)
         * ignore l'identifiant porté par la sonde et recherche bien
         * par clé métier.
         */
        final Produit probeIdIgnore =
                new Produit(ID_INEXISTANT, CHEMISE_ML_HOMME, parent);

        /*
         * Prépare une seconde sonde avec le même parent,
         * le même identifiant volontairement faux,
         * mais un libellé Produit fourni avec une casse différente.
         *
         * Le test prouve ainsi que findByObjetMetier(...)
         * recherche le libellé Produit sans tenir compte de la casse.
         */
        final Produit probeCasseIgnoree =
                new Produit(
                        ID_INEXISTANT,
                        CHEMISE_ML_HOMME.toUpperCase(LOCALE_DEFAUT),
                        parent);

        /*
         * Neutralise explicitement le contexte Hibernate avant les appels
         * de lecture afin d'éviter tout raisonnement biaisé par le cache.
         */
        this.entityManager.clear();

        /* ACT :
         * recherche une première fois avec une sonde portant un ID faux,
         * puis une seconde fois avec une sonde portant un ID faux
         * et une casse de libellé différente.
         */
        final Produit trouveIdIgnore =
                this.service.findByObjetMetier(probeIdIgnore);

        final Produit trouveCasseIgnoree =
                this.service.findByObjetMetier(probeCasseIgnoree);

        /* ASSERT :
         * compare chaque résultat à la ligne SQL de référence.
         *
         * Ce helper prouve simultanément :
         * - l'identifiant Produit ;
         * - le libellé Produit ;
         * - le parent SousTypeProduit ;
         * - le parent TypeProduit du SousTypeProduit.
         */
        assertProduitConformeALigneStockage(trouveIdIgnore, ligneStockage);
        assertProduitConformeALigneStockage(trouveCasseIgnoree, ligneStockage);

        /*
         * Vérifie que la première recherche retourne l'identifiant
         * réellement présent dans le stockage.
         *
         * Comme la sonde portait ID_INEXISTANT, cette assertion prouve
         * que l'identifiant de la sonde a bien été ignoré.
         */
        assertThat(trouveIdIgnore.getIdProduit())
            .isEqualTo(Long.valueOf(idProduitStockage.longValue()));

        /*
         * Vérifie que la seconde recherche retourne elle aussi
         * l'identifiant réellement présent dans le stockage.
         *
         * Comme cette sonde portait une casse différente,
         * cette assertion participe à prouver la recherche
         * insensible à la casse.
         */
        assertThat(trouveCasseIgnoree.getIdProduit())
            .isEqualTo(Long.valueOf(idProduitStockage.longValue()));

        /*
         * Vérifie que les deux recherches retournent exactement
         * le même objet métier persistant.
         */
        assertThat(trouveCasseIgnoree.getIdProduit())
            .isEqualTo(trouveIdIgnore.getIdProduit());

        /*
         * Vérifie que les deux objets métier retournés
         * portent le graphe complet attendu :
         * Produit -> SousTypeProduit -> TypeProduit.
         */
        assertProduitsComplets(List.of(trouveIdIgnore, trouveCasseIgnoree));

        /*
         * Compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByObjetMetier(...)
         * n'a pas touché au stockage.
         */
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
     * <p>garantit que findByLibelleRapide(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_FINDBYLIBELLERAPIDE_NULL)
    @Test
    public void testFindByLibelleRapideNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByLibelleRapide(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* ACT - ASSERT :
         * garantit que service.findByLibelleRapide(null)
         * - jette une ExceptionAppliParamNull
         * - émet le message MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_FINDBYLIBELLERAPIDE_KO_PARAM_NULL);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByLibelleRapide(...)
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
     * <p>garantit que findByLibelleRapide(blank) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>retourne exactement la même liste que rechercherTous() ;</li>
     * <li>retourne des objets métier complets avec le graphe
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>retourne un contenu trié et sans doublon ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_FINDBYLIBELLERAPIDE_BLANK)
    @Test
    public void testFindByLibelleRapideBlank() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByLibelleRapide(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Neutralise explicitement le contexte Hibernate
         * avant les appels de lecture,
         * afin d'éviter tout raisonnement biaisé par le cache.
         */
        this.entityManager.clear();

        /* ACT :
         * appelle service.rechercherTous()
         * puis appelle service.findByLibelleRapide(BLANK).
         *
         * Le contrat attendu ici est une délégation
         * vers la recherche complète.
         */
        final List<Produit> tous = this.service.rechercherTous();

        this.entityManager.clear();

        final List<Produit> rapide = this.service.findByLibelleRapide(BLANK);

        /* ASSERT :
         * vérifie que service.findByLibelleRapide(BLANK)
         * retourne exactement la même liste que service.rechercherTous().
         */
        assertThat(tous).isNotNull().isNotEmpty();
        assertThat(tous).hasSize(countAvant.intValue());
        assertThat(rapide).isNotNull().isNotEmpty();
        assertThat(rapide).hasSize(countAvant.intValue());
        assertThat(rapide).hasSize(tous.size());

        assertProduitsComplets(tous);
        assertProduitsComplets(rapide);

        final List<Long> idsTous = tous.stream()
                .map(Produit::getIdProduit)
                .toList();

        final List<Long> idsRapide = rapide.stream()
                .map(Produit::getIdProduit)
                .toList();

        assertThat(idsRapide).containsExactlyElementsOf(idsTous);
        assertThat(idsRapide).doesNotHaveDuplicates();

        final List<String> clesMetierTous =
                construireClesMetierDepuisProduits(tous);

        final List<String> clesMetierRapide =
                construireClesMetierDepuisProduits(rapide);

        assertThat(clesMetierRapide)
            .containsExactlyElementsOf(clesMetierTous);

        assertThat(clesMetierRapide).doesNotHaveDuplicates();

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByLibelleRapide(...)
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
     * <p>garantit que findByLibelleRapide(non trouvé) :</p>
     * <ul>
     * <li>prouve d'abord l'absence du contenu recherché dans le stockage ;</li>
     * <li>retourne une liste non null et vide ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_FINDBYLIBELLERAPIDE_NON_TROUVE)
    @Test
    public void testFindByLibelleRapideNonTrouve() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByLibelleRapide(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Vérifie par SQL direct qu'aucun Produit ne contient
         * le contenu INTROUVABLE dans son libellé,
         * sans tenir compte des majuscules/minuscules.
         */
        final Long countCorrespondancesStockage =
                this.jdbcTemplate.queryForObject(
                        SELECT_COUNT_PRODUITS_WHERE_LIBELLE_CONTIENT,
                        Long.class,
                        "%" + INTROUVABLE + "%");

        assertThat(countCorrespondancesStockage).isNotNull().isZero();

        /*
         * Neutralise explicitement le contexte Hibernate
         * avant l'appel de lecture,
         * afin d'éviter tout raisonnement biaisé par le cache.
         */
        this.entityManager.clear();

        /* ACT :
         * sollicite service.findByLibelleRapide(...)
         * avec le contenu INTROUVABLE,
         * absent du stockage (prouvé par SQL).
         */
        final List<Produit> resultats =
                this.service.findByLibelleRapide(INTROUVABLE);

        /* ASSERT :
         * vérifie que le service retourne une liste vide (pas null)
         * lors de l'appel service.findByLibelleRapide(...)
         * avec un contenu non trouvé dans le stockage.
         */
        assertThat(resultats).isNotNull().isEmpty();

        /*
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByLibelleRapide(...)
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
     * <p>garantit que findByLibelleRapide(case-insensitive) :</p>
     * <ul>
     * <li>retrouve les objets métier même lorsque le contenu recherché
     * est fourni avec une casse différente de celle du stockage ;</li>
     * <li>retourne exactement les enregistrements présents dans le stockage
     * pour ce contenu ;</li>
     * <li>retourne des objets métier complets avec le graphe
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_FINDBYLIBELLERAPIDE_CASE_INSENSITIVE)
    @Test
    public void testFindByLibelleRapideCaseInsensitive() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByLibelleRapide(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Prépare le même contenu que celui utilisé dans le stockage,
         * mais avec une casse différente,
         * afin de vérifier le comportement case-insensitive
         * de service.findByLibelleRapide(...).
         */
        final String contenuMajuscule = CHEMISE.toUpperCase(LOCALE_DEFAUT);

        /*
         * Lit directement dans le stockage les lignes qui doivent être
         * retournées par le service pour ce contenu, sans tenir compte
         * des majuscules/minuscules.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE_CONTIENT_ORDONNES,
                        "%" + contenuMajuscule + "%");

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        final Long countCorrespondancesStockage =
                this.jdbcTemplate.queryForObject(
                        SELECT_COUNT_PRODUITS_WHERE_LIBELLE_CONTIENT,
                        Long.class,
                        "%" + contenuMajuscule + "%");

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
         * sollicite service.findByLibelleRapide(...)
         * avec le contenu en majuscules.
         */
        final List<Produit> resultats =
                this.service.findByLibelleRapide(contenuMajuscule);

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
         * Vérifie que tous les libellés retournés contiennent bien
         * le contenu recherché, sans tenir compte de la casse.
         */
        assertThat(resultats)
            .extracting(Produit::getProduit)
            .allMatch(libelle -> libelle != null
                    && libelle.toLowerCase(LOCALE_DEFAUT).contains(CHEMISE));

        /*
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByLibelleRapide(...)
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
     * <p>garantit que findByLibelleRapide(dédoublonnage) :</p>
     * <ul>
     * <li>retourne une liste non null et non vide ;</li>
     * <li>ne retourne pas deux fois le même identifiant persistant ;</li>
     * <li>ne retourne pas deux fois la même clé métier Produit ;</li>
     * <li>retourne des objets métier complets avec le graphe
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_FINDBYLIBELLERAPIDE_DEDOUBLONNAGE)
    @Test
    public void testFindByLibelleRapideDedoublonnage() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByLibelleRapide(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Lit directement dans le stockage les lignes qui doivent être
         * retournées par le service pour le contenu CHEMISE.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE_CONTIENT_ORDONNES,
                        "%" + CHEMISE + "%");

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        final List<Long> idsStockage = lignesStockage.stream()
                .map(ligne -> (Number) ligne.get(ID_PRODUIT))
                .map(nombre -> Long.valueOf(nombre.longValue()))
                .toList();

        assertThat(idsStockage).doesNotHaveDuplicates();

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
         * sollicite service.findByLibelleRapide(...)
         * avec un contenu correspondant à plusieurs Produits seedés.
         */
        final List<Produit> resultats =
                this.service.findByLibelleRapide(CHEMISE);

        /* ASSERT :
         * vérifie que le service retourne une liste exploitable
         * et de même taille que la projection SQL attendue.
         */
        assertThat(resultats).isNotNull().isNotEmpty();
        assertThat(resultats).hasSize(lignesStockage.size());

        assertProduitsComplets(resultats);

        final List<Long> idsResultats = resultats.stream()
                .map(Produit::getIdProduit)
                .toList();

        assertThat(idsResultats).containsExactlyElementsOf(idsStockage);
        assertThat(idsResultats).doesNotHaveDuplicates();

        final List<String> clesMetierResultats =
                construireClesMetierDepuisProduits(resultats);

        assertThat(clesMetierResultats)
            .containsExactlyElementsOf(clesMetierStockage);

        assertThat(clesMetierResultats).doesNotHaveDuplicates();

        /*
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByLibelleRapide(...)
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
     * <p>garantit que findByLibelleRapide(OK) :</p>
     * <ul>
     * <li>retourne une liste non null et non vide ;</li>
     * <li>retourne exactement les enregistrements présents dans le stockage
     * pour le contenu recherché ;</li>
     * <li>retourne uniquement des libellés contenant le contenu recherché ;</li>
     * <li>retourne des objets métier complets avec le graphe
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>retourne une liste sans doublon métier ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_FINDBYLIBELLERAPIDE_NOMINAL)
    @Test
    public void testFindByLibelleRapideNominal() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findByLibelleRapide(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Lit directement dans le stockage les lignes qui doivent être
         * retournées par le service pour le contenu recherché.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE_CONTIENT_ORDONNES,
                        "%" + CHEMISE + "%");

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        final Long countCorrespondancesStockage =
                this.jdbcTemplate.queryForObject(
                        SELECT_COUNT_PRODUITS_WHERE_LIBELLE_CONTIENT,
                        Long.class,
                        "%" + CHEMISE + "%");

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
         * sollicite service.findByLibelleRapide(...)
         * avec le contenu CHEMISE.
         */
        final List<Produit> resultats =
                this.service.findByLibelleRapide(CHEMISE);

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
         * portent un libellé contenant le contenu recherché.
         */
        assertThat(resultats)
            .extracting(Produit::getProduit)
            .allMatch(libelle -> libelle != null
                    && libelle.toLowerCase(LOCALE_DEFAUT).contains(CHEMISE));

        /*
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findByLibelleRapide(...)
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    
    
       
    // ========================= findAllByParent ==========================



    /**
     * <div>
     * <p>garantit que findAllByParent(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParentNull} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_NULL)
    @Test
    public void testFindAllByParentNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findAllByParent(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* ACT - ASSERT :
         * garantit que service.findAllByParent(null)
         * - jette une ExceptionAppliParentNull
         * - émet le message MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findAllByParent(null))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_FINDALLBYPARENT_KO_PARAM_NULL);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findAllByParent(...)
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
     * <p>garantit que findAllByParent(parent libellé null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_PARENT_LIBELLE_NULL)
    @Test
    public void testFindAllByParentParentLibelleNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findAllByParent(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * prépare un parent avec identifiant non null
         * et libellé null, afin de tester uniquement
         * le contrôle contractuel du libellé parent.
         */
        final SousTypeProduit parent =
                new SousTypeProduit(Long.valueOf(1L), null, null);

        /* ACT - ASSERT :
         * garantit que service.findAllByParent(parent)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findAllByParent(...)
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
     * <p>garantit que findAllByParent(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_PARENT_LIBELLE_BLANK)
    @Test
    public void testFindAllByParentParentLibelleBlank() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findAllByParent(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * prépare un parent avec identifiant non null
         * et libellé blank, afin de tester uniquement
         * le contrôle contractuel du libellé parent.
         */
        final SousTypeProduit parent =
                new SousTypeProduit(Long.valueOf(1L), BLANK, null);

        /* ACT - ASSERT :
         * garantit que service.findAllByParent(parent)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findAllByParent(...)
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
     * <p>garantit que findAllByParent(parent ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé parent ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_PARENT_ID_NULL)
    @Test
    public void testFindAllByParentParentIdNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findAllByParent(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * récupère le libellé d'un parent réellement persistant,
         * puis prépare un parent dont l'identifiant est null,
         * afin de vérifier le contrôle de persistance du parent.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        final String libelleParent =
                String.valueOf(lignesStockage.get(0).get(SOUS_TYPE_PRODUIT));

        assertThat(libelleParent).isNotBlank();

        final SousTypeProduit parent =
                new SousTypeProduit(null, libelleParent, null);

        /* ACT - ASSERT :
         * garantit que service.findAllByParent(parent)
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet le message MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
         * + libelleParent (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(MSG_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
                    + libelleParent);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findAllByParent(...)
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
     * <p>garantit que findAllByParent(parent absent) :</p>
     * <ul>
     * <li>prouve d'abord que le parent est absent du stockage ;</li>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé parent ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_PARENT_ABSENT)
    @Test
    public void testFindAllByParentParentAbsent() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findAllByParent(...) ne produit aucune écriture
         * dans le stockage.
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
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        final String libelleParent =
                String.valueOf(lignesStockage.get(0).get(SOUS_TYPE_PRODUIT));

        assertThat(libelleParent).isNotBlank();

        /*
         * Vérifie directement dans le stockage
         * qu'aucun parent ne porte l'identifiant ID_INEXISTANT.
         */
        final Long countParentStockage = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT_WHERE_ID,
                Long.class,
                ID_INEXISTANT);

        assertThat(countParentStockage).isNotNull().isZero();

        /*
         * prépare un parent portant un identifiant inexistant,
         * afin de vérifier le contrôle de persistance du parent.
         */
        final SousTypeProduit parent =
                new SousTypeProduit(ID_INEXISTANT, libelleParent, null);

        /* ACT - ASSERT :
         * garantit que service.findAllByParent(parent)
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet le message MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
         * + libelleParent (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(MSG_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
                    + libelleParent);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findAllByParent(...)
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
     * <p>garantit que findAllByParent(parent sans enfant) :</p>
     * <ul>
     * <li>prépare un parent SousTypeProduit persistant sans Produit enfant ;</li>
     * <li>prouve que ce parent existe dans le stockage ;</li>
     * <li>prouve qu'aucun Produit n'existe dans le stockage pour ce parent ;</li>
     * <li>retourne une liste non null et vide ;</li>
     * <li>n'altère pas le stockage des Produits.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_PARENT_SANS_ENFANT)
    @Test
    public void testFindAllByParentParentSansEnfant() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage des Produits afin de pouvoir prouver ensuite
         * que service.findAllByParent(...) ne produit aucune écriture
         * dans ce stockage.
         */
        final Long countAvantProduits = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage des Produits n'est pas vide. */
        assertThat(countAvantProduits).isNotNull().isNotZero();

        Long idParentCree = null;

        try {

            /*
             * Récupère un TypeProduit persistant afin de créer
             * un parent SousTypeProduit dédié au test,
             * volontairement sans Produit enfant.
             */
            final Long idTypeProduit = this.jdbcTemplate.queryForObject(
                    SELECT_ID_TYPE_PRODUIT_WHERE_LIBELLE,
                    Long.class,
                    TYPE_PRODUIT_VETEMENT);

            assertThat(idTypeProduit).isNotNull();

            this.jdbcTemplate.update(
                    INSERT_SOUS_TYPE_PRODUIT,
                    TEMP_PARENT_SANS_ENFANT_PRODUIT,
                    idTypeProduit);

            idParentCree = this.jdbcTemplate.queryForObject(
                    SELECT_ID_SOUS_TYPE_PRODUIT_WHERE_LIBELLE,
                    Long.class,
                    TEMP_PARENT_SANS_ENFANT_PRODUIT);

            assertThat(idParentCree).isNotNull();

            /*
             * Vérifie directement dans le stockage
             * que le parent dédié existe physiquement.
             */
            final Long countParentStockage = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT_WHERE_ID,
                    Long.class,
                    idParentCree);

            assertThat(countParentStockage).isNotNull().isEqualTo(1L);

            /*
             * Vérifie directement dans le stockage des Produits
             * qu'aucun Produit n'est rattaché à ce parent.
             */
            final Long countEnfantsStockage = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_PRODUITS_WHERE_PARENT,
                    Long.class,
                    idParentCree);

            assertThat(countEnfantsStockage).isNotNull().isZero();

            final SousTypeProduit parent =
                    new SousTypeProduit(
                            idParentCree,
                            TEMP_PARENT_SANS_ENFANT_PRODUIT,
                            null);

            /*
             * Neutralise explicitement le contexte Hibernate
             * avant l'appel de lecture,
             * afin d'éviter tout raisonnement biaisé par le cache.
             */
            this.entityManager.clear();

            /* ACT :
             * appelle service.findAllByParent(parent)
             * sur un parent persistant sans Produit enfant.
             */
            final List<Produit> resultats =
                    this.service.findAllByParent(parent);

            /* ASSERT :
             * vérifie que la liste retournée est non null et vide.
             */
            assertThat(resultats).isNotNull().isEmpty();

            /*
             * compte finalement (en SQL) le nombre d'enregistrements
             * dans le stockage des Produits pour prouver que
             * service.findAllByParent(...) n'a pas touché à ce stockage.
             */
            final Long countApresProduits = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            assertThat(countApresProduits).isNotNull().isNotZero();
            assertThat(countApresProduits).isEqualTo(countAvantProduits);

        } finally {

            /*
             * Nettoyage défensif du parent temporaire créé pour le test.
             * Le parent est supprimable car aucun Produit ne lui est rattaché.
             */
            if (idParentCree != null) {
                this.jdbcTemplate.update(
                        DELETE_FROM_SOUS_TYPES_PRODUIT_WHERE_ID,
                        idParentCree);
            }

            this.entityManager.clear();

        }

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findAllByParent(OK) :</p>
     * <ul>
     * <li>retourne une liste non null et non vide ;</li>
     * <li>retourne exactement les Produits présents dans le stockage
     * pour ce parent ;</li>
     * <li>retourne uniquement des Produits rattachés au parent demandé ;</li>
     * <li>retourne des objets métier complets avec le graphe
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>retourne une liste triée et sans doublon métier ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_NOMINAL)
    @Test
    public void testFindAllByParentNominal() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findAllByParent(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Récupère le parent SousTypeProduit d'un Produit seedé,
         * puis lit directement dans le stockage tous les Produits
         * attendus pour ce parent, avec le graphe complet :
         * Produit -> SousTypeProduit -> TypeProduit.
         */
        final List<java.util.Map<String, Object>> lignesSeed =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesSeed).isNotNull().isNotEmpty();

        final Number idParentStockage =
                (Number) lignesSeed.get(0).get(ID_SOUS_TYPE_PRODUIT);

        final String libelleParentStockage =
                String.valueOf(lignesSeed.get(0).get(SOUS_TYPE_PRODUIT));

        final Long idParent =
                Long.valueOf(idParentStockage.longValue());

        assertThat(libelleParentStockage).isNotBlank();

        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_PARENT_ORDONNES,
                        idParent);

        assertThat(lignesStockage).isNotNull().isNotEmpty();

        final Long countEnfantsStockage = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PRODUITS_WHERE_PARENT,
                Long.class,
                idParent);

        assertThat(countEnfantsStockage).isNotNull().isPositive();
        assertThat(lignesStockage).hasSize(countEnfantsStockage.intValue());

        final List<String> clesMetierStockage =
                construireClesMetierDepuisLignesStockage(lignesStockage);

        assertThat(clesMetierStockage).doesNotHaveDuplicates();

        final SousTypeProduit parent =
                new SousTypeProduit(idParent, libelleParentStockage, null);

        /*
         * Neutralise explicitement le contexte Hibernate
         * avant l'appel de lecture,
         * afin d'éviter tout raisonnement biaisé par le cache.
         */
        this.entityManager.clear();

        /* ACT :
         * appelle service.findAllByParent(parent)
         * sur un parent persistant présent dans le stockage.
         */
        final List<Produit> resultats =
                this.service.findAllByParent(parent);

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

        /*
         * Vérifie que tous les Produits retournés
         * sont rattachés au parent demandé.
         */
        assertThat(resultats)
            .allSatisfy(produit -> assertThat(
                    produit.getSousTypeProduit().getIdSousTypeProduit())
                .isEqualTo(idParent));

        final List<String> clesMetierResultats =
                construireClesMetierDepuisProduits(resultats);

        assertThat(clesMetierResultats)
            .containsExactlyElementsOf(clesMetierStockage);

        assertThat(clesMetierResultats).doesNotHaveDuplicates();

        /*
         * Vérifie que les données seedées attendues
         * pour ce parent sont bien présentes.
         */
        assertThat(resultats)
            .extracting(Produit::getProduit)
            .contains(
                    CHEMISE_ML_HOMME,
                    CHEMISE_MC_HOMME,
                    SWEAT_HOMME);

        /*
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findAllByParent(...)
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    
    
    
    // ============================== findById ============================



    /**
     * <div>
     * <p>garantit que findById(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYID_KO_PARAM_NULL} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_NULL)
    @Test
    public void testFindByIdNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findById(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* ACT - ASSERT :
         * garantit que service.findById(null)
         * - jette une ExceptionAppliParamNull
         * - émet le message MESSAGE_FINDBYID_KO_PARAM_NULL
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findById(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_FINDBYID_KO_PARAM_NULL);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findById(...)
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
     * <p>garantit que findById(non trouvé) :</p>
     * <ul>
     * <li>prouve d'abord que l'identifiant recherché est absent du stockage ;</li>
     * <li>retourne {@code null} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_NON_TROUVE)
    @Test
    public void testFindByIdNonTrouve() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findById(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Vérifie directement dans le stockage
         * qu'aucun Produit ne porte l'identifiant ID_INEXISTANT.
         */
        final Long countEnregistrement = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS_WHERE,
                Long.class,
                ID_INEXISTANT);

        assertThat(countEnregistrement).isNotNull().isZero();

        /*
         * Neutralise explicitement le contexte Hibernate
         * avant l'appel de lecture,
         * afin d'éviter tout raisonnement biaisé par le cache.
         */
        this.entityManager.clear();

        /* ACT :
         * appelle service.findById(ID_INEXISTANT)
         * avec un identifiant absent du stockage.
         */
        final Produit resultat = this.service.findById(ID_INEXISTANT);

        /* ASSERT :
         * vérifie que service.findById(ID_INEXISTANT)
         * retourne null lorsque l'identifiant est absent du stockage.
         */
        assertThat(resultat).isNull();

        /*
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findById(...)
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
     * <p>garantit que findById(OK) :</p>
     * <ul>
     * <li>retrouve l'objet métier correspondant à l'identifiant demandé ;</li>
     * <li>retourne exactement le Produit présent dans le stockage
     * pour cet identifiant ;</li>
     * <li>retourne le graphe complet
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_NOMINAL)
    @Test
    public void testFindByIdNominal() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.findById(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Lit directement dans le stockage le Produit seedé
         * recherché, avec le graphe complet :
         * Produit -> SousTypeProduit -> TypeProduit.
         */
        final List<java.util.Map<String, Object>> lignesSeed =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesSeed).isNotNull().hasSize(1);

        final java.util.Map<String, Object> ligneSeed =
                lignesSeed.get(0);

        final Number idProduitStockage =
                (Number) ligneSeed.get(ID_PRODUIT);

        final Long idProduit =
                Long.valueOf(idProduitStockage.longValue());

        /*
         * Vérifie directement dans le stockage
         * qu'un seul Produit porte cet identifiant.
         */
        final Long countEnregistrement = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS_WHERE,
                Long.class,
                idProduit);

        assertThat(countEnregistrement).isNotNull().isEqualTo(1L);

        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_ID,
                        idProduit);

        assertThat(lignesStockage).isNotNull().hasSize(1);

        /*
         * Neutralise explicitement le contexte Hibernate
         * avant l'appel de lecture,
         * afin d'éviter tout raisonnement biaisé par le cache.
         */
        this.entityManager.clear();

        /* ACT :
         * appelle service.findById(idProduit)
         * avec un identifiant réellement présent dans le stockage.
         */
        final Produit resultat = this.service.findById(idProduit);

        /* ASSERT :
         * compare le résultat du service avec la projection SQL complète.
         */
        assertProduitConformeALigneStockage(resultat, lignesStockage.get(0));
        assertProduitsComplets(List.of(resultat));

        /*
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.findById(...)
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
     * <p>Test didactique non contractuel.</p>
     * <p>garantit que findById(ID créé) :</p>
     * <ul>
     * <li>retrouve un Produit nouvellement créé ;</li>
     * <li>retourne l'identifiant créé ;</li>
     * <li>retourne exactement le libellé et le parent présents
     * dans le stockage ;</li>
     * <li>retourne le graphe complet
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>n'altère pas le stockage après la création.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_ID_CREE)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testFindByIdIdCree() throws Exception {

        Long idCree = null;

        try {

            /* ARRANGE :
             * compte d'abord (en SQL) le nombre d'enregistrements
             * dans le stockage, puis prépare une création réelle
             * pour vérifier ensuite la recherche par identifiant
             * sur une donnée créée pendant le test.
             */
            final Long countAvant = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            assertThat(countAvant).isNotNull().isNotZero();

            final List<Produit> produitsExistants =
                    this.service.findByLibelle(CHEMISE_ML_HOMME);

            assertThat(produitsExistants).isNotNull().isNotEmpty();

            final SousTypeProduitI parentSeed =
                    produitsExistants.get(0).getSousTypeProduit();

            final SousTypeProduit parentComplet =
                    construireParentCompletDepuisSeed(parentSeed);

            final Produit aCreer =
                    new Produit(
                            null,
                            TEMP_PRODUIT_A_SUPPRIMER,
                            parentComplet);

            /* ACT :
             * crée réellement un Produit temporaire
             * afin de disposer d'un identifiant nouveau
             * à rechercher par service.findById(...).
             */
            final Produit cree = this.service.creer(aCreer);

            assertThat(cree).isNotNull();
            assertThat(cree.getIdProduit()).isNotNull().isPositive();

            idCree = cree.getIdProduit();

            final Long countApresCreation = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            assertThat(countApresCreation).isNotNull();
            assertThat(countApresCreation).isEqualTo(countAvant + 1L);

            /*
             * Relit directement dans le stockage la ligne nouvellement créée,
             * avec le graphe complet :
             * Produit -> SousTypeProduit -> TypeProduit.
             */
            final List<java.util.Map<String, Object>> lignesStockage =
                    this.jdbcTemplate.queryForList(
                            SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_ID,
                            idCree);

            assertThat(lignesStockage).isNotNull().hasSize(1);
            assertProduitConformeALigneStockage(cree, lignesStockage.get(0));

            /*
             * Neutralise explicitement le contexte Hibernate
             * avant toute relecture via le service pour éviter
             * d'être leurré par le cache Hibernate.
             */
            this.entityManager.clear();

            /* ACT :
             * sollicite la recherche par identifiant
             * avec l'ID du Produit nouvellement créé.
             */
            final Produit retour = this.service.findById(idCree);

            /* ASSERT :
             * vérifie que le service retrouve bien
             * l'enregistrement nouvellement persisté.
             */
            assertProduitConformeALigneStockage(retour, lignesStockage.get(0));
            assertProduitsComplets(List.of(retour));

            /*
             * compte finalement (en SQL) le nombre d'enregistrements
             * dans le stockage pour prouver que service.findById(...)
             * n'a pas touché au stockage après la création.
             */
            final Long countApresLecture = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            assertThat(countApresLecture).isNotNull();
            assertThat(countApresLecture).isEqualTo(countApresCreation);

        } finally {

            /*
             * Nettoyage défensif :
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
    

    
    // =============================== update =============================



    /**
     * <div>
     * <p>garantit que update(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_UPDATE_KO_PARAM_NULL} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_NULL)
    @Test
    public void testUpdateNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.update(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        /* ACT - ASSERT :
         * garantit que service.update(null)
         * - jette une ExceptionAppliParamNull
         * - émet le message MESSAGE_UPDATE_KO_PARAM_NULL
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.update(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_UPDATE_KO_PARAM_NULL);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.update(...)
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
     * <p>garantit que update(libellé null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_BLANK} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_LIBELLE_NULL)
    @Test
    public void testUpdateLibelleNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.update(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * récupère un objet métier persistant afin de tester uniquement
         * le contrôle contractuel du libellé de l'objet à modifier.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final Produit seed = produitsExistants.get(0);

        final Produit aModifier =
                new Produit(
                        seed.getIdProduit(),
                        null,
                        seed.getSousTypeProduit());

        /* ACT - ASSERT :
         * garantit que service.update(aModifier)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_UPDATE_KO_LIBELLE_BLANK
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.update(aModifier))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_UPDATE_KO_LIBELLE_BLANK);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.update(...)
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
     * <p>garantit que update(libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_BLANK} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_BLANK)
    @Test
    public void testUpdateLibelleBlank() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.update(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * récupère un objet métier persistant afin de tester uniquement
         * le contrôle contractuel du libellé de l'objet à modifier.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final Produit seed = produitsExistants.get(0);

        final Produit aModifier =
                new Produit(
                        seed.getIdProduit(),
                        BLANK,
                        seed.getSousTypeProduit());

        /* ACT - ASSERT :
         * garantit que service.update(aModifier)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_UPDATE_KO_LIBELLE_BLANK
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.update(aModifier))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_UPDATE_KO_LIBELLE_BLANK);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.update(...)
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
     * <p>garantit que update(id null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNonPersistent} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_UPDATE_KO_NON_PERSISTENT}
     * suivi du libellé Produit ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_ID_NULL)
    @Test
    public void testUpdateIdNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.update(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * récupère un parent persistant afin de construire
         * un objet métier valide sauf sur son identifiant.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final SousTypeProduitI parent =
                produitsExistants.get(0).getSousTypeProduit();

        assertThat(parent).isNotNull();

        final Produit aModifier =
                new Produit(
                        null,
                        TEMP_PRODUIT_A_MODIFIER,
                        parent);

        /* ACT - ASSERT :
         * garantit que service.update(aModifier)
         * - jette une ExceptionAppliParamNonPersistent
         * - émet le message MESSAGE_UPDATE_KO_NON_PERSISTENT
         * + TEMP_PRODUIT_A_MODIFIER.
         */
        assertThatThrownBy(() -> this.service.update(aModifier))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(MSG_UPDATE_KO_NON_PERSISTENT
                    + TEMP_PRODUIT_A_MODIFIER);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.update(...)
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
     * <p>garantit que update(parent null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParentNull} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_UPDATE_KO_PARENT_NULL} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_PARENT_NULL)
    @Test
    public void testUpdateParentNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.update(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * récupère un objet métier persistant,
         * puis prépare une modification portant un parent null.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final Produit seed = produitsExistants.get(0);

        final Produit aModifier =
                new Produit(
                        seed.getIdProduit(),
                        seed.getProduit(),
                        null);

        /* ACT - ASSERT :
         * garantit que service.update(aModifier)
         * - jette une ExceptionAppliParentNull
         * - émet le message MESSAGE_UPDATE_KO_PARENT_NULL
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.update(aModifier))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_UPDATE_KO_PARENT_NULL);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.update(...)
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
     * <p>garantit que update(parent libellé null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_PARENT_LIBELLE_NULL)
    @Test
    public void testUpdateParentLibelleNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.update(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * récupère un identifiant parent réellement persistant,
         * puis prépare un parent avec libellé null,
         * afin de vérifier le contrôle contractuel
         * sur le parent de l'objet métier à modifier.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final Produit seed = produitsExistants.get(0);
        final SousTypeProduitI parentSeed = seed.getSousTypeProduit();

        assertThat(parentSeed).isNotNull();
        assertThat(parentSeed.getIdSousTypeProduit()).isNotNull();

        final SousTypeProduit parent =
                new SousTypeProduit(
                        parentSeed.getIdSousTypeProduit(),
                        null,
                        null);

        final Produit aModifier =
                new Produit(
                        seed.getIdProduit(),
                        seed.getProduit(),
                        parent);

        /* ACT - ASSERT :
         * garantit que service.update(aModifier)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.update(aModifier))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_UPDATE_KO_LIBELLE_PARENT_BLANK);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.update(...)
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
     * <p>garantit que update(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_PARENT_LIBELLE_BLANK)
    @Test
    public void testUpdateParentLibelleBlank() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.update(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * récupère un identifiant parent réellement persistant,
         * puis prépare un parent avec libellé blank,
         * afin de vérifier le contrôle contractuel
         * sur le parent de l'objet métier à modifier.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final Produit seed = produitsExistants.get(0);
        final SousTypeProduitI parentSeed = seed.getSousTypeProduit();

        assertThat(parentSeed).isNotNull();
        assertThat(parentSeed.getIdSousTypeProduit()).isNotNull();

        final SousTypeProduit parent =
                new SousTypeProduit(
                        parentSeed.getIdSousTypeProduit(),
                        BLANK,
                        null);

        final Produit aModifier =
                new Produit(
                        seed.getIdProduit(),
                        seed.getProduit(),
                        parent);

        /* ACT - ASSERT :
         * garantit que service.update(aModifier)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.update(aModifier))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_UPDATE_KO_LIBELLE_PARENT_BLANK);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.update(...)
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
     * <p>garantit que update(parent ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé parent ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_PARENT_ID_NULL)
    @Test
    public void testUpdateParentIdNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.update(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * récupère le libellé d'un parent réellement persistant,
         * puis prépare un parent dont l'identifiant est null,
         * afin de vérifier le contrôle de persistance du parent.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final Produit seed = produitsExistants.get(0);
        final SousTypeProduitI parentSeed = seed.getSousTypeProduit();

        assertThat(parentSeed).isNotNull();
        assertThat(parentSeed.getSousTypeProduit()).isNotBlank();

        final String libelleParent = parentSeed.getSousTypeProduit();

        final SousTypeProduit parent =
                new SousTypeProduit(null, libelleParent, null);

        final Produit aModifier =
                new Produit(
                        seed.getIdProduit(),
                        seed.getProduit(),
                        parent);

        /* ACT - ASSERT :
         * garantit que service.update(aModifier)
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet le message MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT
         * + libelleParent.
         */
        assertThatThrownBy(() -> this.service.update(aModifier))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(MSG_UPDATE_KO_PARENT_NON_PERSISTENT
                    + libelleParent);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.update(...)
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
     * <p>garantit que update(parent absent) :</p>
     * <ul>
     * <li>prouve d'abord que le parent est absent du stockage ;</li>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé parent ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_PARENT_ABSENT)
    @Test
    public void testUpdateParentAbsent() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.update(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * récupère le libellé d'un parent seedé,
         * afin de construire un parent absent portant un libellé valide.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final Produit seed = produitsExistants.get(0);
        final SousTypeProduitI parentSeed = seed.getSousTypeProduit();

        assertThat(parentSeed).isNotNull();
        assertThat(parentSeed.getSousTypeProduit()).isNotBlank();

        final String libelleParent = parentSeed.getSousTypeProduit();

        /*
         * Vérifie directement dans le stockage
         * qu'aucun parent ne porte l'identifiant ID_INEXISTANT.
         */
        final Long countParentStockage = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT_WHERE_ID,
                Long.class,
                ID_INEXISTANT);

        assertThat(countParentStockage).isNotNull().isZero();

        final SousTypeProduit parent =
                new SousTypeProduit(
                        ID_INEXISTANT,
                        libelleParent,
                        null);

        final Produit aModifier =
                new Produit(
                        seed.getIdProduit(),
                        seed.getProduit(),
                        parent);

        /* ACT - ASSERT :
         * garantit que service.update(aModifier)
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet le message MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT
         * + libelleParent.
         */
        assertThatThrownBy(() -> this.service.update(aModifier))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(MSG_UPDATE_KO_PARENT_NON_PERSISTENT
                    + libelleParent);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.update(...)
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
     * <p>garantit que update(absent) :</p>
     * <ul>
     * <li>prouve d'abord que l'identifiant recherché est absent du stockage ;</li>
     * <li>retourne {@code null} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_ABSENT)
    @Test
    public void testUpdateAbsent() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.update(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Vérifie directement dans le stockage
         * qu'aucun Produit ne porte l'identifiant ID_INEXISTANT.
         */
        final Long countEnregistrement = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS_WHERE,
                Long.class,
                ID_INEXISTANT);

        assertThat(countEnregistrement).isNotNull().isZero();

        /*
         * récupère un parent persistant complet,
         * afin que le test porte uniquement sur l'absence
         * de l'objet métier à modifier.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final SousTypeProduit parentComplet =
                construireParentCompletDepuisSeed(
                        produitsExistants.get(0).getSousTypeProduit());

        final Produit aModifier =
                new Produit(
                        ID_INEXISTANT,
                        TEMP_PRODUIT_A_MODIFIER,
                        parentComplet);

        this.entityManager.clear();

        /* ACT :
         * appelle service.update(aModifier)
         * avec un identifiant absent du stockage.
         */
        final Produit resultat = this.service.update(aModifier);

        /* ASSERT :
         * vérifie que le service retourne null
         * lorsque l'objet métier à modifier est absent du stockage.
         */
        assertThat(resultat).isNull();

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.update(...)
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
     * <p>garantit que update(sans modification) :</p>
     * <ul>
     * <li>retourne l'objet persistant inchangé ;</li>
     * <li>retourne exactement le Produit présent dans le stockage
     * pour cet identifiant ;</li>
     * <li>retourne le graphe complet
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_SANS_MODIFICATION)
    @Test
    public void testUpdateSansModification() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.update(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Lit directement dans le stockage le Produit seedé
         * à soumettre sans modification, avec le graphe complet :
         * Produit -> SousTypeProduit -> TypeProduit.
         */
        final List<java.util.Map<String, Object>> lignesStockage =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                        CHEMISE_ML_HOMME);

        assertThat(lignesStockage).isNotNull().hasSize(1);

        final java.util.Map<String, Object> ligneStockage =
                lignesStockage.get(0);

        final Number idProduitStockage =
                (Number) ligneStockage.get(ID_PRODUIT);

        final Number idParentStockage =
                (Number) ligneStockage.get(ID_SOUS_TYPE_PRODUIT);

        final String libelleProduitStockage =
                String.valueOf(ligneStockage.get(PRODUIT));

        final String libelleParentStockage =
                String.valueOf(ligneStockage.get(SOUS_TYPE_PRODUIT));

        final Produit aModifier =
                new Produit(
                        Long.valueOf(idProduitStockage.longValue()),
                        libelleProduitStockage,
                        new SousTypeProduit(
                                Long.valueOf(idParentStockage.longValue()),
                                libelleParentStockage,
                                null));

        this.entityManager.clear();

        /* ACT :
         * appelle service.update(aModifier)
         * avec un objet métier identique à l'état du stockage.
         */
        final Produit resultat = this.service.update(aModifier);

        /* ASSERT :
         * vérifie que le service retourne l'objet persistant inchangé,
         * avec son graphe métier complet.
         */
        assertProduitConformeALigneStockage(resultat, ligneStockage);
        assertProduitsComplets(List.of(resultat));

        final List<java.util.Map<String, Object>> lignesApres =
                this.jdbcTemplate.queryForList(
                        SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_ID,
                        Long.valueOf(idProduitStockage.longValue()));

        assertThat(lignesApres).isNotNull().hasSize(1);
        assertThat(lignesApres.get(0)).isEqualTo(ligneStockage);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.update(...)
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
     * <p>garantit que update(OK) :</p>
     * <ul>
     * <li>modifie réellement le libellé Produit dans le stockage ;</li>
     * <li>conserve le même identifiant Produit ;</li>
     * <li>conserve le même parent SousTypeProduit ;</li>
     * <li>retourne le graphe complet
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>ne crée ni ne supprime aucun Produit.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_NOMINAL)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testUpdateNominal() throws Exception {

        Long idProduit = null;
        String libelleAvant = null;
        Long idParentAvant = null;

        try {

            /* ARRANGE :
             * compte d'abord (en SQL) le nombre d'enregistrements
             * dans le stockage afin de pouvoir prouver ensuite
             * que service.update(...) ne crée ni ne supprime de Produit.
             */
            final Long countAvant = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            assertThat(countAvant).isNotNull().isNotZero();

            /*
             * Lit directement dans le stockage le Produit seedé
             * à modifier, avec le graphe complet :
             * Produit -> SousTypeProduit -> TypeProduit.
             */
            final List<java.util.Map<String, Object>> lignesAvant =
                    this.jdbcTemplate.queryForList(
                            SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                            CHEMISE_ML_HOMME);

            assertThat(lignesAvant).isNotNull().hasSize(1);

            final java.util.Map<String, Object> ligneAvant =
                    lignesAvant.get(0);

            final Number idProduitStockage =
                    (Number) ligneAvant.get(ID_PRODUIT);

            final Number idParentStockage =
                    (Number) ligneAvant.get(ID_SOUS_TYPE_PRODUIT);

            idProduit = Long.valueOf(idProduitStockage.longValue());
            idParentAvant = Long.valueOf(idParentStockage.longValue());
            libelleAvant = String.valueOf(ligneAvant.get(PRODUIT));

            final String nouveauLibelle =
                    libelleAvant + SUFFIX_MODIF;

            final SousTypeProduit parent =
                    new SousTypeProduit(
                            idParentAvant,
                            String.valueOf(ligneAvant.get(SOUS_TYPE_PRODUIT)),
                            null);

            final Produit aModifier =
                    new Produit(
                            idProduit,
                            nouveauLibelle,
                            parent);

            this.entityManager.clear();

            /* ACT :
             * appelle service.update(aModifier)
             * avec un nouveau libellé et le même parent.
             */
            final Produit modifie = this.service.update(aModifier);

            /* ASSERT :
             * relit directement le stockage après update(...)
             * et compare le retour service avec la ligne réellement modifiée.
             */
            final List<java.util.Map<String, Object>> lignesApres =
                    this.jdbcTemplate.queryForList(
                            SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_ID,
                            idProduit);

            assertThat(lignesApres).isNotNull().hasSize(1);

            final java.util.Map<String, Object> ligneApres =
                    lignesApres.get(0);

            assertProduitConformeALigneStockage(modifie, ligneApres);
            assertProduitsComplets(List.of(modifie));

            assertThat(ligneApres.get(PRODUIT)).isEqualTo(nouveauLibelle);
            assertThat(((Number) ligneApres.get(ID_SOUS_TYPE_PRODUIT)).longValue())
                .isEqualTo(idParentAvant.longValue());

            /*
             * Neutralise explicitement le contexte Hibernate
             * avant la relecture via findById(...).
             */
            this.entityManager.clear();

            final Produit relu = this.service.findById(idProduit);

            assertProduitConformeALigneStockage(relu, ligneApres);
            assertProduitsComplets(List.of(relu));

            /*
             * Vérifie que la modification de libellé
             * n'a créé ni supprimé aucun Produit.
             */
            final Long countApres = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            assertThat(countApres).isNotNull().isEqualTo(countAvant);

        } finally {

            /*
             * Nettoyage défensif :
             * restaure le libellé et le parent initiaux
             * si l'identifiant du Produit seedé a été déterminé.
             */
            if (idProduit != null
                    && libelleAvant != null
                    && idParentAvant != null) {
                this.jdbcTemplate.update(
                        UPDATE_PRODUITS_SET_LIBELLE_PARENT_WHERE_ID,
                        libelleAvant,
                        idParentAvant,
                        idProduit);
            }

            this.entityManager.clear();

        }

    } // __________________________________________________________________



    /**
     * <div>
     * <p>Test didactique non contractuel.</p>
     * <p>garantit que update(parent modifié) :</p>
     * <ul>
     * <li>modifie réellement le parent dans le stockage ;</li>
     * <li>conserve le même identifiant Produit ;</li>
     * <li>conserve le même libellé Produit ;</li>
     * <li>retourne le graphe complet
     * Produit -&gt; SousTypeProduit -&gt; TypeProduit ;</li>
     * <li>ne crée ni ne supprime aucun Produit.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_PARENT_MODIFIE)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testUpdateParentModifie() throws Exception {

        Long idProduit = null;
        String libelleAvant = null;
        Long idParentAvant = null;

        try {

            /* ARRANGE :
             * compte d'abord (en SQL) le nombre d'enregistrements
             * dans le stockage afin de pouvoir prouver ensuite
             * que service.update(...) ne crée ni ne supprime de Produit.
             */
            final Long countAvant = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            assertThat(countAvant).isNotNull().isNotZero();

            /*
             * Lit directement dans le stockage le Produit seedé
             * à changer de parent, avec le graphe complet :
             * Produit -> SousTypeProduit -> TypeProduit.
             */
            final List<java.util.Map<String, Object>> lignesAvant =
                    this.jdbcTemplate.queryForList(
                            SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_LIBELLE,
                            CHEMISE_ML_HOMME);

            assertThat(lignesAvant).isNotNull().hasSize(1);

            final java.util.Map<String, Object> ligneAvant =
                    lignesAvant.get(0);

            final Number idProduitStockage =
                    (Number) ligneAvant.get(ID_PRODUIT);

            final Number idParentStockage =
                    (Number) ligneAvant.get(ID_SOUS_TYPE_PRODUIT);

            idProduit = Long.valueOf(idProduitStockage.longValue());
            idParentAvant = Long.valueOf(idParentStockage.longValue());
            libelleAvant = String.valueOf(ligneAvant.get(PRODUIT));

            final Long idNouveauParent = this.jdbcTemplate.queryForObject(
                    SELECT_ID_SOUS_TYPE_PRODUIT_WHERE_LIBELLE,
                    Long.class,
                    SOUS_TYPE_PRODUIT_FEMME);

            assertThat(idNouveauParent).isNotNull();
            assertThat(idNouveauParent).isNotEqualTo(idParentAvant);

            final SousTypeProduit nouveauParent =
                    new SousTypeProduit(
                            idNouveauParent,
                            SOUS_TYPE_PRODUIT_FEMME,
                            null);

            final Produit aModifier =
                    new Produit(
                            idProduit,
                            libelleAvant,
                            nouveauParent);

            this.entityManager.clear();

            /* ACT :
             * appelle service.update(aModifier)
             * avec le même libellé Produit et un parent différent.
             */
            final Produit modifie = this.service.update(aModifier);

            /* ASSERT :
             * relit directement le stockage après update(...)
             * et compare le retour service avec la ligne réellement modifiée.
             */
            final List<java.util.Map<String, Object>> lignesApres =
                    this.jdbcTemplate.queryForList(
                            SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_ID,
                            idProduit);

            assertThat(lignesApres).isNotNull().hasSize(1);

            final java.util.Map<String, Object> ligneApres =
                    lignesApres.get(0);

            assertProduitConformeALigneStockage(modifie, ligneApres);
            assertProduitsComplets(List.of(modifie));

            assertThat(ligneApres.get(PRODUIT)).isEqualTo(libelleAvant);
            assertThat(((Number) ligneApres.get(ID_SOUS_TYPE_PRODUIT)).longValue())
                .isEqualTo(idNouveauParent.longValue());
            assertThat(ligneApres.get(SOUS_TYPE_PRODUIT))
                .isEqualTo(SOUS_TYPE_PRODUIT_FEMME);

            /*
             * Neutralise explicitement le contexte Hibernate
             * avant la relecture via findById(...).
             */
            this.entityManager.clear();

            final Produit relu = this.service.findById(idProduit);

            assertProduitConformeALigneStockage(relu, ligneApres);
            assertProduitsComplets(List.of(relu));

            /*
             * Vérifie que la modification de parent
             * n'a créé ni supprimé aucun Produit.
             */
            final Long countApres = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            assertThat(countApres).isNotNull().isEqualTo(countAvant);

        } finally {

            /*
             * Nettoyage défensif :
             * restaure le libellé et le parent initiaux
             * si l'identifiant du Produit seedé a été déterminé.
             */
            if (idProduit != null
                    && libelleAvant != null
                    && idParentAvant != null) {
                this.jdbcTemplate.update(
                        UPDATE_PRODUITS_SET_LIBELLE_PARENT_WHERE_ID,
                        libelleAvant,
                        idParentAvant,
                        idProduit);
            }

            this.entityManager.clear();

        }

    } // __________________________________________________________________
    

    
    // ============================= delete ===============================



    /**
     * <div>
     * <p>garantit que delete(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_DELETE_KO_PARAM_NULL} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_NULL)
    @Test
    public void testDeleteNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.delete(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        /* ACT - ASSERT :
         * garantit que service.delete(null)
         * - jette une ExceptionAppliParamNull
         * - émet le message MESSAGE_DELETE_KO_PARAM_NULL
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.delete(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_DELETE_KO_PARAM_NULL);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.delete(...)
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
     * <p>garantit que delete(id null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNonPersistent} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_DELETE_KO_ID_NULL} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_ID_NULL)
    @Test
    public void testDeleteIdNull() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.delete(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * récupère un parent persistant complet,
         * afin de construire un objet métier valide sauf sur son identifiant.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final SousTypeProduit parentComplet =
                construireParentCompletDepuisSeed(
                        produitsExistants.get(0).getSousTypeProduit());

        final Produit aSupprimer =
                new Produit(
                        null,
                        TEMP_PRODUIT_A_SUPPRIMER,
                        parentComplet);

        /* ACT - ASSERT :
         * garantit que service.delete(aSupprimer)
         * - jette une ExceptionAppliParamNonPersistent
         * - émet le message MESSAGE_DELETE_KO_ID_NULL
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.delete(aSupprimer))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(MSG_DELETE_KO_ID_NULL);

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.delete(...)
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
     * <p>garantit que delete(absent) :</p>
     * <ul>
     * <li>prouve d'abord que l'identifiant est absent du stockage ;</li>
     * <li>ne lève aucune exception ;</li>
     * <li>laisse l'identifiant absent du stockage ;</li>
     * <li>n'altère pas le nombre total de Produits dans le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_ABSENT)
    @Test
    public void testDeleteAbsent() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL) le nombre d'enregistrements
         * dans le stockage afin de pouvoir prouver ensuite
         * que service.delete(...) ne produit aucune écriture
         * dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Vérifie directement dans le stockage
         * qu'aucun Produit ne porte l'identifiant ID_INEXISTANT.
         */
        final Long countAbsentAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS_WHERE,
                Long.class,
                ID_INEXISTANT);

        assertThat(countAbsentAvant).isNotNull().isZero();

        /*
         * récupère un parent persistant complet,
         * afin que le test porte uniquement sur l'absence
         * de l'objet métier à supprimer.
         */
        final List<Produit> produitsExistants =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(produitsExistants).isNotNull().isNotEmpty();

        final SousTypeProduit parentComplet =
                construireParentCompletDepuisSeed(
                        produitsExistants.get(0).getSousTypeProduit());

        final Produit absent =
                new Produit(
                        ID_INEXISTANT,
                        INTROUVABLE,
                        parentComplet);

        this.entityManager.clear();

        /* ACT :
         * appelle service.delete(absent)
         * avec un identifiant absent du stockage.
         */
        this.service.delete(absent);

        /* ASSERT :
         * vérifie que l'identifiant reste absent du stockage
         * après l'appel service.delete(...).
         */
        final Long countAbsentApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS_WHERE,
                Long.class,
                ID_INEXISTANT);

        assertThat(countAbsentApres).isNotNull().isZero();

        /* ASSERT :
         * compte finalement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver que service.delete(...)
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
     * <p>garantit que delete(OK) :</p>
     * <ul>
     * <li>crée d'abord un Produit temporaire dédié au test ;</li>
     * <li>prouve que ce Produit existe réellement dans le stockage ;</li>
     * <li>supprime réellement ce Produit du stockage ;</li>
     * <li>rend cet identifiant introuvable dans le stockage ;</li>
     * <li>rend cet identifiant introuvable via le service ;</li>
     * <li>ramène le nombre total de Produits à sa valeur initiale.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_NOMINAL)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testDeleteNominal() throws Exception {

        Long idCree = null;

        try {

            /* ARRANGE :
             * compte d'abord (en SQL) le nombre d'enregistrements
             * dans le stockage avant toute création.
             */
            final Long countAvantCreation = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            assertThat(countAvantCreation).isNotNull().isNotZero();

            /*
             * récupère un parent persistant complet,
             * afin de créer un Produit temporaire supprimable.
             */
            final List<Produit> produitsExistants =
                    this.service.findByLibelle(CHEMISE_ML_HOMME);

            assertThat(produitsExistants).isNotNull().isNotEmpty();

            final SousTypeProduit parentComplet =
                    construireParentCompletDepuisSeed(
                            produitsExistants.get(0).getSousTypeProduit());

            final Produit aCreer =
                    new Produit(
                            null,
                            TEMP_PRODUIT_A_SUPPRIMER,
                            parentComplet);

            /* ACT :
             * crée réellement un Produit temporaire,
             * afin de disposer d'un objet persistant
             * à supprimer dans le stockage.
             */
            final Produit cree = this.service.creer(aCreer);

            assertThat(cree).isNotNull();
            assertThat(cree.getIdProduit()).isNotNull().isPositive();

            idCree = cree.getIdProduit();

            /*
             * Vérifie immédiatement par SQL direct
             * que la création a bien ajouté une ligne réelle
             * dans le stockage.
             */
            final Long countApresCreation = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            final Long countLigneAvantDelete = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS_WHERE,
                    Long.class,
                    idCree);

            assertThat(countApresCreation).isNotNull();
            assertThat(countApresCreation).isEqualTo(countAvantCreation + 1L);
            assertThat(countLigneAvantDelete).isNotNull().isEqualTo(1L);

            final List<java.util.Map<String, Object>> lignesAvantDelete =
                    this.jdbcTemplate.queryForList(
                            SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_ID,
                            idCree);

            assertThat(lignesAvantDelete).isNotNull().hasSize(1);
            assertProduitConformeALigneStockage(cree, lignesAvantDelete.get(0));
            assertProduitsComplets(List.of(cree));

            /*
             * Neutralise explicitement le contexte Hibernate
             * avant la suppression.
             */
            this.entityManager.clear();

            /* ACT :
             * appelle service.delete(...)
             * sur l'objet métier nouvellement créé.
             */
            this.service.delete(cree);

            this.entityManager.clear();

            /* ASSERT :
             * vérifie d'abord par SQL direct
             * que l'enregistrement n'existe plus dans le stockage.
             */
            final Long countApresDelete = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            final Long countLigneApresDelete = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS_WHERE,
                    Long.class,
                    idCree);

            assertThat(countApresDelete).isNotNull();
            assertThat(countApresDelete).isEqualTo(countAvantCreation);
            assertThat(countLigneApresDelete).isNotNull().isZero();

            /*
             * Vérifie enfin que la relecture via le service
             * ne retrouve plus cet identifiant.
             */
            final Produit relu = this.service.findById(idCree);

            assertThat(relu).isNull();

        } finally {

            /*
             * Nettoyage défensif :
             * si une assertion échoue après la création
             * mais avant la suppression effective,
             * supprime explicitement la ligne créée
             * afin de garantir l'isolation du test.
             */
            if (idCree != null) {
                final Long countLigne = this.jdbcTemplate.queryForObject(
                        SELECT_COUNT_FROM_PRODUITS_WHERE,
                        Long.class,
                        idCree);

                if ((countLigne != null) && (countLigne.longValue() == 1L)) {
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
     * <p>garantit que delete(double suppression) :</p>
     * <ul>
     * <li>crée d'abord un Produit temporaire dédié au test ;</li>
     * <li>la première suppression efface réellement le Produit
     * dans le stockage ;</li>
     * <li>le second appel ne lève aucune exception ;</li>
     * <li>le second appel ne recrée rien ;</li>
     * <li>le nombre total de Produits reste stable après le second appel.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_DOUBLE_SUPPRESSION)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testDeleteDoubleSuppression() throws Exception {

        Long idCree = null;

        try {

            /* ARRANGE :
             * compte d'abord (en SQL) le nombre d'enregistrements
             * dans le stockage avant toute création.
             */
            final Long countAvantCreation = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            assertThat(countAvantCreation).isNotNull().isNotZero();

            /*
             * récupère un parent persistant complet,
             * afin de créer un Produit temporaire supprimable.
             */
            final List<Produit> produitsExistants =
                    this.service.findByLibelle(CHEMISE_ML_HOMME);

            assertThat(produitsExistants).isNotNull().isNotEmpty();

            final SousTypeProduit parentComplet =
                    construireParentCompletDepuisSeed(
                            produitsExistants.get(0).getSousTypeProduit());

            final Produit aCreer =
                    new Produit(
                            null,
                            TEMP_PRODUIT_A_SUPPRIMER,
                            parentComplet);

            /* ACT :
             * crée réellement un Produit temporaire,
             * afin de disposer d'un objet persistant
             * à essayer de supprimer deux fois dans le stockage.
             */
            final Produit cree = this.service.creer(aCreer);

            assertThat(cree).isNotNull();
            assertThat(cree.getIdProduit()).isNotNull().isPositive();

            idCree = cree.getIdProduit();

            /*
             * Vérifie immédiatement par SQL direct
             * que la création a bien ajouté une ligne réelle
             * dans le stockage.
             */
            final Long countApresCreation = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            final Long countLigneAvantDelete = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS_WHERE,
                    Long.class,
                    idCree);

            assertThat(countApresCreation).isNotNull();
            assertThat(countApresCreation).isEqualTo(countAvantCreation + 1L);
            assertThat(countLigneAvantDelete).isNotNull().isEqualTo(1L);

            final List<java.util.Map<String, Object>> lignesAvantDelete =
                    this.jdbcTemplate.queryForList(
                            SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_ID,
                            idCree);

            assertThat(lignesAvantDelete).isNotNull().hasSize(1);
            assertProduitConformeALigneStockage(cree, lignesAvantDelete.get(0));
            assertProduitsComplets(List.of(cree));

            /*
             * Neutralise explicitement le contexte Hibernate
             * avant la première suppression.
             */
            this.entityManager.clear();

            /* ACT :
             * première suppression effective.
             */
            this.service.delete(cree);

            this.entityManager.clear();

            /* ASSERT :
             * vérifie que la première suppression
             * a bien retiré l'enregistrement du stockage.
             */
            final Long countApresPremierDelete = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            final Long countLigneApresPremierDelete =
                    this.jdbcTemplate.queryForObject(
                            SELECT_COUNT_FROM_PRODUITS_WHERE,
                            Long.class,
                            idCree);

            assertThat(countApresPremierDelete).isNotNull();
            assertThat(countApresPremierDelete).isEqualTo(countAvantCreation);
            assertThat(countLigneApresPremierDelete).isNotNull().isZero();

            /*
             * ACT :
             * second appel delete(...) sur le même objet métier.
             *
             * Le contrat attendu est l'absence d'exception :
             * l'objet est absent du stockage, donc le service ne fait rien.
             */
            this.service.delete(cree);

            this.entityManager.clear();

            /* ASSERT :
             * vérifie que le second appel n'a rien recréé
             * et n'a pas modifié le nombre total de Produits.
             */
            final Long countApresSecondDelete = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            final Long countLigneApresSecondDelete =
                    this.jdbcTemplate.queryForObject(
                            SELECT_COUNT_FROM_PRODUITS_WHERE,
                            Long.class,
                            idCree);

            assertThat(countApresSecondDelete).isNotNull();
            assertThat(countApresSecondDelete).isEqualTo(countAvantCreation);
            assertThat(countLigneApresSecondDelete).isNotNull().isZero();

            final Produit relu = this.service.findById(idCree);

            assertThat(relu).isNull();

        } finally {

            /*
             * Nettoyage défensif :
             * si une assertion échoue après la création
             * mais avant la suppression effective,
             * supprime explicitement la ligne créée
             * afin de garantir l'isolation du test.
             */
            if (idCree != null) {
                final Long countLigne = this.jdbcTemplate.queryForObject(
                        SELECT_COUNT_FROM_PRODUITS_WHERE,
                        Long.class,
                        idCree);

                if ((countLigne != null) && (countLigne.longValue() == 1L)) {
                    this.jdbcTemplate.update(
                            DELETE_FROM_PRODUITS_WHERE_ID,
                            idCree);
                }
            }

            this.entityManager.clear();

        }

    } // __________________________________________________________________



    // ============================== Count ===============================



    /**
     * <div>
     * <p>garantit que count() sur un stockage vide :</p>
     * <ul>
     * <li>retourne zéro ;</li>
     * <li>retourne le même total que le DAO ;</li>
     * <li>retourne le même total que le stockage lu en SQL ;</li>
     * <li>retourne le même total que la méthode publique
     * {@link #service}.rechercherTous().</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName(DN_COUNT_STOCKAGE_VIDE)
    @Test
    @Sql(
        scripts = ProduitGatewayJPAServiceIntegrationTest.CLASSPATH_TRUNCATE_SQL,
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    public void testCountStockageVide() throws Exception {

        /* ARRANGE :
         * lit (en SQL) le nombre d'enregistrements
         * dans le stockage.
         */
        final Long countStockage = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage est vide. */
        assertThat(countStockage).isNotNull().isZero();

        /*
         * Neutralise explicitement le contexte Hibernate
         * avant les lectures via service.count()
         * et service.rechercherTous(),
         * afin d'éviter tout raisonnement biaisé par le cache.
         */
        this.entityManager.clear();

        /* ACT :
         * lit le nombre d'enregistrements
         * via le service,
         * via le DAO,
         * puis via la méthode publique de lecture rechercherTous().
         */
        final long viaService = this.service.count();
        final long viaDao = this.produitDaoJPA.count();
        final List<Produit> liste = this.service.rechercherTous();

        /* Vérifie que count() retourne 0. */
        assertThat(viaService).isZero();

        /*
         * Vérifie que la méthode publique rechercherTous()
         * retourne une liste non null et vide.
         */
        assertThat(liste).isNotNull().isEmpty();

        /* Vérifie que count() retourne
         * le même total que le DAO.
         */
        assertThat(viaService).isEqualTo(viaDao);

        /* Vérifie que count() retourne
         * le même total que le stockage lu en SQL.
         */
        assertThat(viaService).isEqualTo(countStockage.longValue());

        /* Vérifie que count() retourne
         * le même total que la méthode publique rechercherTous().
         */
        assertThat(viaService).isEqualTo(liste.size());

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que count() sur le stockage seedé :</p>
     * <ul>
     * <li>retourne un total strictement positif ;</li>
     * <li>retourne le même total que le DAO ;</li>
     * <li>retourne le même total que le stockage lu en SQL ;</li>
     * <li>retourne le même total que la méthode publique
     * {@link #service}.rechercherTous().</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName(DN_COUNT_NOMINAL)
    @Test
    public void testCountNominal() throws Exception {

        /* ARRANGE :
         * lit (en SQL) le nombre d'enregistrements
         * dans le stockage.
         */
        final Long countStockage = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_PRODUITS,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countStockage).isNotNull().isNotZero();

        /*
         * Neutralise explicitement le contexte Hibernate
         * avant les lectures via service.count()
         * et service.rechercherTous(),
         * afin d'éviter tout raisonnement biaisé par le cache.
         */
        this.entityManager.clear();

        /* ACT :
         * lit le nombre d'enregistrements
         * via le service,
         * via le DAO,
         * puis via la méthode publique de lecture rechercherTous().
         */
        final long viaService = this.service.count();
        final long viaDao = this.produitDaoJPA.count();
        final List<Produit> liste = this.service.rechercherTous();

        /* ASSERT :
         * vérifie que count() retourne
         * un total strictement positif.
         */
        assertThat(viaService).isPositive();

        /*
         * Vérifie que la méthode publique rechercherTous()
         * retourne une liste non null et non vide.
         */
        assertThat(liste).isNotNull().isNotEmpty();

        /* Vérifie que count() retourne
         * le même total que le DAO.
         */
        assertThat(viaService).isEqualTo(viaDao);

        /* Vérifie que count() retourne
         * le même total que le stockage lu en SQL.
         */
        assertThat(viaService).isEqualTo(countStockage.longValue());

        /* Vérifie que count() retourne
         * le même total que la méthode publique rechercherTous().
         */
        assertThat(viaService).isEqualTo(liste.size());

    } // __________________________________________________________________



    /**
     * <div>
     * <p>Test didactique non contractuel.</p>
     * <p>garantit que count() suit l'état du stockage :</p>
     * <ul>
     * <li>retourne le total initial du stockage ;</li>
     * <li>augmente de 1 après creer(...) ;</li>
     * <li>prouve la ligne créée dans le stockage ;</li>
     * <li>revient au total initial après delete(...) ;</li>
     * <li>prouve l'absence de la ligne supprimée dans le stockage ;</li>
     * <li>reste cohérent avec les lectures SQL directes ;</li>
     * <li>reste cohérent avec la méthode publique rechercherTous().</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName(DN_COUNT_APRES_CREATION_PUIS_SUPPRESSION)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCountApresCreationPuisSuppression() throws Exception {

        Long idCree = null;

        try {

            /* ARRANGE :
             * lit (en SQL) le nombre d'enregistrements
             * dans le stockage avant création.
             */
            final Long countAvantSql = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            /* vérifie que le stockage n'est pas vide. */
            assertThat(countAvantSql).isNotNull().isNotZero();

            /*
             * Neutralise explicitement le contexte Hibernate
             * avant les lectures initiales via service.count()
             * et service.rechercherTous().
             */
            this.entityManager.clear();

            /*
             * Lit le nombre d'enregistrements
             * dans le stockage via le service
             * et via la méthode publique rechercherTous().
             */
            final long countAvantService = this.service.count();
            final List<Produit> listeAvant = this.service.rechercherTous();

            /*
             * Garantit que service.count() retourne le même
             * nombre d'enregistrements que la requête SQL directe
             * et que la méthode publique rechercherTous().
             */
            assertThat(listeAvant).isNotNull().isNotEmpty();
            assertThat(countAvantService).isEqualTo(countAvantSql.longValue());
            assertThat(countAvantService).isEqualTo(listeAvant.size());

            /*
             * Récupère un parent persistant complet,
             * afin de créer un Produit temporaire comptable.
             */
            final List<Produit> produitsExistants =
                    this.service.findByLibelle(CHEMISE_ML_HOMME);

            assertThat(produitsExistants).isNotNull().isNotEmpty();

            final SousTypeProduit parentComplet =
                    construireParentCompletDepuisSeed(
                            produitsExistants.get(0).getSousTypeProduit());

            final Produit aCreer =
                    new Produit(
                            null,
                            TEMP_PRODUIT_A_SUPPRIMER,
                            parentComplet);

            /* ACT :
             * appelle service.creer(...)
             * pour ajouter un nouvel objet métier dans le stockage.
             */
            final Produit cree = this.service.creer(aCreer);

            /* Assure que l'objet métier a été créé dans le stockage. */
            assertThat(cree).isNotNull();
            assertThat(cree.getIdProduit()).isNotNull().isPositive();

            idCree = cree.getIdProduit();

            /*
             * Relit directement dans le stockage la ligne créée,
             * avec le graphe complet :
             * Produit -> SousTypeProduit -> TypeProduit.
             */
            final List<java.util.Map<String, Object>> lignesApresCreation =
                    this.jdbcTemplate.queryForList(
                            SELECT_PRODUITS_AVEC_PARENT_ET_TYPE_WHERE_ID,
                            idCree);

            assertThat(lignesApresCreation).isNotNull().hasSize(1);
            assertProduitConformeALigneStockage(
                    cree,
                    lignesApresCreation.get(0));
            assertProduitsComplets(List.of(cree));

            /* ASSERT :
             * lit (en SQL) le nombre d'enregistrements
             * dans le stockage après création.
             */
            final Long countApresCreationSql = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            /*
             * Lit (en SQL) le nombre d'enregistrements
             * portant l'identifiant nouvellement créé.
             */
            final Long countLigneApresCreation = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS_WHERE,
                    Long.class,
                    idCree);

            /*
             * Neutralise explicitement le contexte Hibernate
             * avant les lectures après création via service.count()
             * et service.rechercherTous().
             */
            this.entityManager.clear();

            /*
             * Lit le nombre d'enregistrements
             * dans le stockage via le service après création.
             */
            final long countApresCreationService = this.service.count();
            final List<Produit> listeApresCreation = this.service.rechercherTous();

            /*
             * Vérifie que service.count() retourne un nombre
             * d'enregistrements dans le stockage
             * incrémenté de 1 après une création.
             */
            assertThat(countApresCreationSql).isNotNull();
            assertThat(countApresCreationSql).isEqualTo(countAvantSql + 1L);
            assertThat(countApresCreationService)
                .isEqualTo(countApresCreationSql.longValue());

            /*
             * Vérifie que la ligne créée existe une seule fois
             * dans le stockage.
             */
            assertThat(countLigneApresCreation).isNotNull().isEqualTo(1L);

            /*
             * Vérifie que count() reste cohérent
             * avec la méthode publique rechercherTous() après création.
             */
            assertThat(listeApresCreation).isNotNull().isNotEmpty();
            assertThat(countApresCreationService).isEqualTo(listeApresCreation.size());

            /* ACT :
             * supprime ensuite l'enregistrement de test créé.
             */
            this.service.delete(cree);

            /*
             * Neutralise explicitement le contexte Hibernate
             * avant les lectures après suppression.
             */
            this.entityManager.clear();

            /* ASSERT :
             * lit (en SQL) le nombre d'enregistrements
             * dans le stockage après suppression.
             */
            final Long countApresSuppressionSql = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_PRODUITS,
                    Long.class);

            /*
             * Lit (en SQL) le nombre d'enregistrements
             * portant l'identifiant supprimé.
             */
            final Long countLigneApresSuppression =
                    this.jdbcTemplate.queryForObject(
                            SELECT_COUNT_FROM_PRODUITS_WHERE,
                            Long.class,
                            idCree);

            /*
             * Lit le nombre d'enregistrements
             * dans le stockage via le service après suppression.
             */
            final long countApresSuppressionService = this.service.count();
            final List<Produit> listeApresSuppression = this.service.rechercherTous();

            /*
             * Vérifie que service.count() retourne un nombre
             * d'enregistrements dans le stockage
             * diminué de 1 après une suppression validée par
             * l'interrogation directe du stockage en SQL.
             */
            assertThat(countApresSuppressionSql).isNotNull();
            assertThat(countApresSuppressionSql).isEqualTo(countAvantSql);
            assertThat(countApresSuppressionService)
                .isEqualTo(countApresSuppressionSql.longValue());

            /*
             * Vérifie que la ligne supprimée
             * est absente du stockage.
             */
            assertThat(countLigneApresSuppression).isNotNull().isZero();

            /*
             * Vérifie que count() reste cohérent
             * avec la méthode publique rechercherTous() après suppression.
             */
            assertThat(listeApresSuppression).isNotNull().isNotEmpty();
            assertThat(countApresSuppressionService)
                .isEqualTo(listeApresSuppression.size());

            /*
             * Vérifie enfin que la relecture via le service
             * ne retrouve plus cet identifiant.
             */
            final Produit relu = this.service.findById(idCree);

            assertThat(relu).isNull();

        } finally {

            /*
             * Nettoyage défensif :
             * supprime l'enregistrement s'il existe encore dans le stockage.
             */
            if (idCree != null) {
                final Long countRestant = this.jdbcTemplate.queryForObject(
                        SELECT_COUNT_FROM_PRODUITS_WHERE,
                        Long.class,
                        idCree);

                if ((countRestant != null)
                        && (countRestant.longValue() == 1L)) {
                    this.jdbcTemplate.update(
                            DELETE_FROM_PRODUITS_WHERE_ID,
                            idCree);
                }
            }

            this.entityManager.clear();

        }

    } // __________________________________________________________________
    
    
    
    // ====================== OUTILS HELPERS ==============================



    /**
     * <div>
     * <p>Reconstruit un parent {@link SousTypeProduit} complet
     * à partir d'un parent déjà relu via le service.</p>
     *
     * <p>Ce helper est utilisé par les tests qui créent, modifient
     * ou suppriment réellement un {@link Produit}. Dans ces cas,
     * l'objet métier transmis au Gateway doit porter un graphe parent
     * exploitable par la conversion métier vers JPA :</p>
     *
     * <pre>
     * Produit -> SousTypeProduit -> TypeProduit
     * </pre>
     *
     * <p>Le helper ne recherche rien dans le stockage. Il sécurise
     * uniquement la copie du parent déjà relu en vérifiant que ce parent
     * contient :</p>
     * <ul>
     * <li>l'identifiant persistant du {@link SousTypeProduit} ;</li>
     * <li>le libellé métier du {@link SousTypeProduit} ;</li>
     * <li>le parent {@link TypeProduit} complet ;</li>
     * <li>l'identifiant persistant et le libellé métier du
     * {@link TypeProduit}.</li>
     * </ul>
     * </div>
     *
     * @param pParentSeed SousTypeProduitI : parent relu depuis une donnée
     * seedée par le service.
     * @return SousTypeProduit : copie métier complète du parent.
     */
    private SousTypeProduit construireParentCompletDepuisSeed(
            final SousTypeProduitI pParentSeed) {

        /*
         * Vérifie d'abord que le parent reçu est exploitable.
         * Le test échoue immédiatement si le service fournit
         * un parent incomplet, car une création ou une modification
         * de Produit ne doit pas être construite sur un graphe partiel.
         */
        assertThat(pParentSeed).isNotNull();
        assertThat(pParentSeed.getIdSousTypeProduit()).isNotNull();
        assertThat(pParentSeed.getSousTypeProduit()).isNotNull().isNotBlank();

        /*
         * Vérifie ensuite que le parent TypeProduit est lui aussi complet.
         * Pour Produit, le TypeProduit n'est pas une clé métier directe,
         * mais il appartient au graphe métier attendu.
         */
        assertThat(pParentSeed.getTypeProduit()).isNotNull();
        assertThat(pParentSeed.getTypeProduit().getIdTypeProduit()).isNotNull();
        assertThat(pParentSeed.getTypeProduit().getTypeProduit())
            .isNotNull()
            .isNotBlank();

        /*
         * Reconstruit explicitement le TypeProduit parent afin de ne pas
         * réutiliser par référence l'objet reçu depuis la donnée seedée.
         */
        final TypeProduit typeProduitParent = new TypeProduit(
                pParentSeed.getTypeProduit().getIdTypeProduit(),
                pParentSeed.getTypeProduit().getTypeProduit());

        /*
         * Retourne un SousTypeProduit complet, prêt à être utilisé
         * dans un Produit transmis au Gateway.
         */
        return new SousTypeProduit(
                pParentSeed.getIdSousTypeProduit(),
                pParentSeed.getSousTypeProduit(),
                typeProduitParent);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>Compare un {@link Produit} retourné par le service
     * avec une ligne SQL projetée directement depuis le stockage.</p>
     *
     * <p>La projection SQL attendue doit contenir le graphe complet :</p>
     *
     * <pre>
     * Produit -> SousTypeProduit -> TypeProduit
     * </pre>
     *
     * <p>Ce helper constitue la preuve ligne à ligne utilisée par
     * les tests d'intégration : il vérifie simultanément l'identifiant
     * Produit, le libellé Produit, le parent SousTypeProduit et le
     * parent TypeProduit du SousTypeProduit.</p>
     *
     * <p>Le {@link TypeProduit} est contrôlé ici comme partie du graphe
     * complet du parent. Il n'est pas ajouté à la clé métier directe
     * du Produit.</p>
     * </div>
     *
     * @param pProduit Produit : objet métier retourné par le service.
     * @param pLigneStockage Map&lt;String, Object&gt; : ligne SQL projetée
     * depuis le stockage.
     */
    private void assertProduitConformeALigneStockage(
            final Produit pProduit,
            final java.util.Map<String, Object> pLigneStockage) {

        /*
         * Vérifie les deux sources de comparaison avant toute extraction :
         * - l'objet métier retourné par le service ;
         * - la ligne SQL relue directement dans le stockage.
         */
        assertThat(pProduit).isNotNull();
        assertThat(pLigneStockage).isNotNull();

        /*
         * Extrait les colonnes Produit attendues depuis la projection SQL.
         */
        final Number idProduitStockage =
                (Number) pLigneStockage.get(ID_PRODUIT);

        final String libelleProduitStockage =
                String.valueOf(pLigneStockage.get(PRODUIT));

        /*
         * Extrait les colonnes du parent SousTypeProduit.
         */
        final Number idSousTypeProduitStockage =
                (Number) pLigneStockage.get(ID_SOUS_TYPE_PRODUIT);

        final String libelleSousTypeProduitStockage =
                String.valueOf(pLigneStockage.get(SOUS_TYPE_PRODUIT));

        /*
         * Extrait les colonnes du parent TypeProduit.
         */
        final Number idTypeProduitStockage =
                (Number) pLigneStockage.get(ID_TYPE_PRODUIT);

        final String libelleTypeProduitStockage =
                String.valueOf(pLigneStockage.get(TYPE_PRODUIT));

        /*
         * Vérifie l'objet métier Produit lui-même :
         * identifiant persistant et libellé métier.
         */
        assertThat(pProduit.getIdProduit())
            .isEqualTo(Long.valueOf(idProduitStockage.longValue()));
        assertThat(pProduit.getProduit()).isEqualTo(libelleProduitStockage);

        /*
         * Vérifie le parent SousTypeProduit rattaché au Produit.
         */
        assertThat(pProduit.getSousTypeProduit()).isNotNull();
        assertThat(pProduit.getSousTypeProduit().getIdSousTypeProduit())
            .isEqualTo(Long.valueOf(idSousTypeProduitStockage.longValue()));
        assertThat(pProduit.getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(libelleSousTypeProduitStockage);

        /*
         * Vérifie le TypeProduit porté par le parent SousTypeProduit.
         * Cette vérification garantit que le graphe métier complet
         * a bien été reconstruit par le Gateway.
         */
        assertThat(pProduit.getSousTypeProduit().getTypeProduit()).isNotNull();
        assertThat(pProduit.getSousTypeProduit().getTypeProduit().getIdTypeProduit())
            .isEqualTo(Long.valueOf(idTypeProduitStockage.longValue()));
        assertThat(pProduit.getSousTypeProduit().getTypeProduit().getTypeProduit())
            .isEqualTo(libelleTypeProduitStockage);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>Vérifie que tous les {@link Produit} fournis portent
     * un graphe métier complet.</p>
     *
     * <p>Le graphe complet attendu dans cette classe de test est :</p>
     *
     * <pre>
     * Produit -> SousTypeProduit -> TypeProduit
     * </pre>
     *
     * <p>Ce helper ne compare pas les objets métier avec une ligne précise
     * du stockage. Il garantit seulement que chaque objet retourné
     * par le service est exploitable métier :</p>
     * <ul>
     * <li>Produit persistant avec libellé non blank ;</li>
     * <li>parent SousTypeProduit persistant avec libellé non blank ;</li>
     * <li>parent TypeProduit persistant avec libellé non blank.</li>
     * </ul>
     * </div>
     *
     * @param pProduits List&lt;Produit&gt; : objets métier à contrôler.
     */
    private void assertProduitsComplets(final List<Produit> pProduits) {

        /*
         * Vérifie que la liste existe.
         * Une liste vide est acceptable pour ce helper :
         * les tests qui attendent du contenu contrôlent eux-mêmes
         * isNotEmpty() avant ou après l'appel.
         */
        assertThat(pProduits).isNotNull();

        /*
         * Contrôle chaque Produit fourni.
         */
        assertThat(pProduits)
            .allSatisfy(produit -> {

                /*
                 * Contrôle l'objet métier Produit.
                 */
                assertThat(produit).isNotNull();
                assertThat(produit.getIdProduit()).isNotNull();
                assertThat(produit.getProduit()).isNotBlank();

                /*
                 * Contrôle le parent SousTypeProduit.
                 */
                assertThat(produit.getSousTypeProduit()).isNotNull();
                assertThat(produit.getSousTypeProduit().getIdSousTypeProduit())
                    .isNotNull();
                assertThat(produit.getSousTypeProduit().getSousTypeProduit())
                    .isNotBlank();

                /*
                 * Contrôle le parent TypeProduit du SousTypeProduit.
                 */
                assertThat(produit.getSousTypeProduit().getTypeProduit()).isNotNull();
                assertThat(produit.getSousTypeProduit().getTypeProduit().getIdTypeProduit())
                    .isNotNull();
                assertThat(produit.getSousTypeProduit().getTypeProduit().getTypeProduit())
                    .isNotBlank();
            });

    } // __________________________________________________________________



    /**
     * <div>
     * <p>Construit les clés de contrôle métier Produit
     * à partir d'une projection SQL relue dans le stockage.</p>
     *
     * <p>La clé de contrôle utilisée dans les tests Produit est :</p>
     *
     * <pre>
     * SousTypeProduit parent + libellé Produit
     * </pre>
     *
     * <p>Le TypeProduit n'est pas intégré à cette clé. Il est contrôlé
     * séparément comme partie du graphe complet du parent
     * SousTypeProduit.</p>
     *
     * <p>La normalisation en minuscules permet de comparer les résultats
     * des recherches insensibles à la casse sans confondre cette clé
     * de contrôle avec une clé technique SQL.</p>
     * </div>
     *
     * @param pLignesStockage List&lt;Map&lt;String, Object&gt;&gt; :
     * lignes SQL projetées depuis le stockage.
     * @return List&lt;String&gt; : clés métier normalisées.
     */
    private List<String> construireClesMetierDepuisLignesStockage(
            final List<java.util.Map<String, Object>> pLignesStockage) {

        /*
         * Vérifie que la projection SQL existe.
         * Une projection vide reste acceptable pour les tests
         * qui contrôlent explicitement les cas sans résultat.
         */
        assertThat(pLignesStockage).isNotNull();

        /*
         * Construit une clé stable à partir :
         * - de l'identifiant persistant du parent SousTypeProduit ;
         * - du libellé Produit normalisé.
         */
        return pLignesStockage.stream()
                .map(ligne -> {
                    assertThat(ligne).isNotNull();

                    final Number idSousTypeProduit =
                            (Number) ligne.get(ID_SOUS_TYPE_PRODUIT);

                    final String libelleProduit =
                            String.valueOf(ligne.get(PRODUIT));

                    assertThat(idSousTypeProduit).isNotNull();
                    assertThat(libelleProduit).isNotBlank();

                    return idSousTypeProduit.longValue()
                            + "|"
                            + libelleProduit.toLowerCase(LOCALE_DEFAUT);
                })
                .toList();

    } // __________________________________________________________________



    /**
     * <div>
     * <p>Construit les clés de contrôle métier Produit
     * à partir d'une liste d'objets métier retournés par le service.</p>
     *
     * <p>La clé de contrôle utilisée dans les tests Produit est :</p>
     *
     * <pre>
     * SousTypeProduit parent + libellé Produit
     * </pre>
     *
     * <p>Le TypeProduit n'est pas intégré à cette clé. Il est déjà contrôlé
     * par {@link #assertProduitsComplets(List)} comme partie du graphe
     * complet du parent SousTypeProduit.</p>
     * </div>
     *
     * @param pProduits List&lt;Produit&gt; : objets métier retournés par le service.
     * @return List&lt;String&gt; : clés métier normalisées.
     */
    private List<String> construireClesMetierDepuisProduits(
            final List<Produit> pProduits) {

        /*
         * Vérifie d'abord que tous les Produits fournis portent
         * le graphe métier complet attendu par les tests d'intégration.
         */
        assertProduitsComplets(pProduits);

        /*
         * Construit une clé stable à partir :
         * - de l'identifiant persistant du parent SousTypeProduit ;
         * - du libellé Produit normalisé.
         */
        return pProduits.stream()
                .map(produit -> produit.getSousTypeProduit()
                                .getIdSousTypeProduit()
                        + "|"
                        + produit.getProduit()
                                .toLowerCase(LOCALE_DEFAUT))
                .toList();

    } // __________________________________________________________________
    

    
} // FIN DE LA CLASSE ProduitGatewayJPAServiceIntegrationTest.-------------

