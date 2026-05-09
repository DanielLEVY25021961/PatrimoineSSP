/* ********************************************************************* */
/* ***************************** TEST JUNIT **************************** */
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliLibelleBlank;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNonPersistent;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNull;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParentNull;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionTechniqueGatewayNonPersistent;
import levy.daniel.application.model.services.produittype.gateway.SousTypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.pagination.DirectionTri;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;
import levy.daniel.application.model.services.produittype.pagination.TriSpec;
import levy.daniel.application.persistence.metier.produittype.dao.daosJPA.SousTypeProduitDaoJPA;
import levy.daniel.application.persistence.metier.produittype.dao.daosJPA.TypeProduitDaoJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.SousTypeProduitJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.TypeProduitJPA;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE SousTypeProduitGatewayJPAServiceIntegrationTest.java :
 * </p>
 * <p>
 * Tests d'intégration Spring (@DataJpaTest) / H2 in-memory du service ADAPTER GATEWAY
 * {@link SousTypeProduitGatewayJPAService}.
 * </p>
 * <p>
 * Ce test JUnit autonome utilise :
 * </p>
 * <ul>
 * <li>Un stockage H2 en mémoire</li>
 * <li>Une configuration autonome Spring Boot définie directement
 * dans le présent test via {@link ConfigTest}</li>
 * <li>Des scripts SQL pour initialiser le stockage :
 * <code>truncate-test.sql</code> puis <code>data-test.sql</code></li>
 * <li>Un profile group SPRING "test-jpa" activé par le test.</li>
 * </ul>
 *
 * <p>
 * Objectif : vérifier le comportement observable (JPA/H2) du gateway.
 * </p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 2.0
 * @since 01 février 2026
 */
@SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
@Sql(
    scripts = {SousTypeProduitGatewayJPAServiceIntegrationTest.CLASSPATH_TRUNCATE_SQL,
            SousTypeProduitGatewayJPAServiceIntegrationTest.CLASSPATH_DATA_SQL},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@DataJpaTest
@ActiveProfiles({ "test-jpa" })
@Import(SousTypeProduitGatewayJPAService.class)
@ContextConfiguration(classes = SousTypeProduitGatewayJPAServiceIntegrationTest.ConfigTest.class)
public class SousTypeProduitGatewayJPAServiceIntegrationTest {

    // ************************* CONSTANTES ******************************/

    /** "test" */
    public static final String PROFILE_TEST = "test";

    /** "classpath:truncate-test.sql" */
    public static final String CLASSPATH_TRUNCATE_SQL 
    	= "classpath:truncate-test.sql";

    /** "classpath:data-test.sql" */
    public static final String CLASSPATH_DATA_SQL 
    	= "classpath:data-test.sql";

    /** "SousTypeProduitGatewayJPAService" */
    public static final String QUALIFIER_SERVICE 
    	= "SousTypeProduitGatewayJPAService";

    /** "servicesGateway-Creer" */
    public static final String TAG_CREER = "servicesGateway-Creer";

    /** "servicesGateway-Rechercher" */
    public static final String TAG_RECHERCHER = "servicesGateway-Rechercher";

    /** "servicesGateway-FindByObjetMetier" */
    public static final String TAG_FINDBYOBJETMETIER 
    	= "servicesGateway-FindByObjetMetier";

    /** "servicesGateway-RechercherRapide" */
    public static final String TAG_RECHERCHER_RAPIDE 
    	= "servicesGateway-RechercherRapide";

    /** "servicesGateway-Pagination" */
    public static final String TAG_PAGINATION = "servicesGateway-Pagination";

    /** "servicesGateway-Update" */
    public static final String TAG_UPDATE = "servicesGateway-Update";

    /** "servicesGateway-Delete" */
    public static final String TAG_DELETE = "servicesGateway-Delete";

    /** "servicesGateway-Count" */
    public static final String TAG_COUNT = "servicesGateway-Count";

    /** "" */
    public static final String CHAINE_VIDE = "";

    /** "   " */
    public static final String BLANK = "   ";
    
    /** "Inexistant" */
    public static final String LIBELLE_INEXISTANT = "Inexistant";
    
    /**
     * "sousTypeProduit"
     */
    public static final String SOUS_TYPE_PRODUIT = "sousTypeProduit";

    /** "xyz" */
    public static final String CONTENU_PARTIEL_INEXISTANT = "xyz";

    /** "vêtement" */
    public static final String LIBELLE_PARENT_VETEMENT = "vêtement";

    /** "Chaussure" */
    public static final String LIBELLE_PARENT_CHAUSSURE = "Chaussure";

    /** "vêtement pour homme" */
    public static final String LIBELLE_ENFANT_VETEMENT_HOMME 
    	= "vêtement pour homme";

    /** "vêtement pour femme" */
    public static final String LIBELLE_ENFANT_VETEMENT_FEMME 
    	= "vêtement pour femme";

    /** "vêtement pour enfant" */
    public static final String LIBELLE_ENFANT_VETEMENT_ENFANT 
    	= "vêtement pour enfant";

    /** "vêt" */
    public static final String CONTENU_PARTIEL_VET = "vêt";

    /** "sh" */
    public static final String CONTENU_PARTIEL_SH = "sh";

    /** "Pull" */
    public static final String LIBELLE_NOUVEAU_PULL = "Pull";

    /** "vêtement pour femme modifié" */
    public static final String LIBELLE_MODIFIE_FEMME 
    	= "vêtement pour femme modifié";

    /** "à supprimer" */
    public static final String LIBELLE_A_SUPPRIMER = "à supprimer";

    /** 999_999L */
    public static final Long ID_INEXISTANT = Long.valueOf(999_999L);

    /** "creer(null) - jette ExceptionAppliParamNull (contrat du port)" */
    public static final String DN_CREER_NULL 
    	= "creer(null) - jette ExceptionAppliParamNull (contrat du port)";
    
    /** "creer(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
    public static final String DN_CREER_PARENT_NON_PERSISTENT
        = "creer(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** "creer(libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_CREER_LIBELLE_NULL 
    	= "creer(libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "creer(blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_CREER_BLANK 
    	= "creer(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "creer(parent null) - jette ExceptionAppliParentNull (contrat du port)" */
    public static final String DN_CREER_PARENT_NULL 
    	= "creer(parent null) - jette ExceptionAppliParentNull (contrat du port)";

    /** "creer(parent libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_CREER_PARENT_LIBELLE_NULL 
    	= "creer(parent libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "creer(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_CREER_PARENT_LIBELLE_BLANK 
    	= "creer(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "creer(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
    public static final String DN_CREER_PARENT_NON_PERSISTANT 
    	= "creer(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** "creer(OK) - ajoute un élément, le rend retrouvable et conserve (ne wipe pas) les données seedées" */
    public static final String DN_CREER_NOMINAL
        = "creer(OK) - ajoute un élément, le rend retrouvable et conserve (ne wipe pas) les données seedées";
    
    /** "rechercherTous(stockage seedé) - retourne exactement l'état physique trié et sans doublon" */
    public static final String DN_RECHERCHER_TOUS 
    	= "rechercherTous(stockage seedé) - retourne exactement l'état physique trié et sans doublon";
    
    /** 
	 * "rechercherTousParPage(avec tri) - retourne une page triée" 
	 */
	public static final String DN_RECHERCHER_TOUS_PAR_PAGE_TRI 
		= "rechercherTousParPage(avec tri) - retourne une page triée";

	/** 
	 * "rechercherTousParPage(page vide) - retourne une page vide" 
	 */
	public static final String DN_RECHERCHER_TOUS_PAR_PAGE_VIDE 
		= "rechercherTousParPage(page vide) - retourne une page vide";

    /** "findByObjetMetier(null) - jette ExceptionAppliParamNull (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_NULL 
    	= "findByObjetMetier(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** "findByObjetMetier(libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_LIBELLE_NULL 
    	= "findByObjetMetier(libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findByObjetMetier(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_BLANK 
    	= "findByObjetMetier(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findByObjetMetier(parent null) - jette ExceptionAppliParentNull (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_PARENT_NULL 
    	= "findByObjetMetier(parent null) - jette ExceptionAppliParentNull (contrat du port)";

    /** "findByObjetMetier(parent libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_PARENT_LIBELLE_NULL 
    	= "findByObjetMetier(parent libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findByObjetMetier(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_PARENT_LIBELLE_BLANK 
    	= "findByObjetMetier(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";
    
    /** "findByObjetMetier(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_PARENT_NON_PERSISTANT
        = "findByObjetMetier(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

	/** "findByObjetMetier(id ignoré et casse ignorée) - retrouve l'objet métier correspondant" */
	public static final String DN_FINDBYOBJETMETIER_ID_IGNORE_CASSE_IGNOREE
	    = "findByObjetMetier(id ignoré et casse ignorée) - retrouve l'objet métier correspondant";

	/** "findByObjetMetier(non trouvé) - retourne null sans altérer le stockage" */
    public static final String DN_FINDBYOBJETMETIER_NON_TROUVE 
    	= "findByObjetMetier(non trouvé) - retourne null sans altérer le stockage";

    /** "findByObjetMetier(OK) - retourne l'objet métier correspondant" */
    public static final String DN_FINDBYOBJETMETIER_TROUVE 
    	= "findByObjetMetier(OK) - retourne l'objet métier correspondant";
    
    /**
     * "findByLibelle(null) - jette ExceptionAppliLibelleBlank (contrat du port)"
     */
    public static final String DN_FINDBYLIBELLE_NULL
        = "findByLibelle(null) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findByLibelle(blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDBYLIBELLE_BLANK 
    	= "findByLibelle(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findByLibelle(non trouvé) - retourne une liste vide" */
	public static final String DN_FINDBYLIBELLE_NON_TROUVE 
		= "findByLibelle(non trouvé) - retourne une liste vide";

    /** "findByLibelle(OK) - retourne la liste des correspondances" */
    public static final String DN_FINDBYLIBELLE_NOMINAL 
    	= "findByLibelle(OK) - retourne la liste des correspondances";
    
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
    public static final String DN_FINDALLBYPARENT_LIBELLE_NULL 
    	= "findAllByParent(parent libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findAllByParent(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDALLBYPARENT_LIBELLE_BLANK 
    	= "findAllByParent(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findAllByParent(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
    public static final String DN_FINDALLBYPARENT_NON_PERSISTANT 
    	= "findAllByParent(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

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

    /** "findById(trouvé) - retourne l'objet métier" */
    public static final String DN_FINDBYID_TROUVE 
    	= "findById(trouvé) - retourne l'objet métier";
    
    /** "update(null) - jette ExceptionAppliParamNull (contrat du port)" */
    public static final String DN_UPDATE_NULL 
    	= "update(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** "update(libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_UPDATE_LIBELLE_NULL 
    	= "update(libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "update(blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_UPDATE_BLANK 
    	= "update(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

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

    /** "update(parent ID null) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
    public static final String DN_UPDATE_PARENT_ID_NULL 
    	= "update(parent ID null) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** "update(parent absent) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
    public static final String DN_UPDATE_PARENT_ABSENT 
    	= "update(parent absent) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** "update(absent) - retourne null" */
    public static final String DN_UPDATE_ABSENT 
    	= "update(absent) - retourne null";

    /** "update(sans modification) - retourne l'objet persistant inchangé" */
    public static final String DN_UPDATE_SANS_MODIFICATION 
    	= "update(sans modification) - retourne l'objet persistant inchangé";

    /** "update(parent modifié) - met à jour le parent" */
	public static final String DN_UPDATE_PARENT_MODIFIE 
		= "update(parent modifié) - met à jour le parent";

	/** "update(nominal) - modifie le stockage et retourne l'objet modifié" */
    public static final String DN_UPDATE_NOMINAL 
    	= "update(nominal) - modifie le stockage et retourne l'objet modifié";

    /** "update(doublon métier) - jette DataIntegrityViolationException et ne modifie pas le stockage" */
    public static final String DN_UPDATE_DOUBLON_METIER 
    	= "update(doublon métier) - jette DataIntegrityViolationException et ne modifie pas le stockage";
    
    /** "delete(null) - jette ExceptionAppliParamNull (contrat du port)" */
    public static final String DN_DELETE_NULL 
    	= "delete(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** "delete(id null) - jette ExceptionAppliParamNonPersistent (contrat du port)" */
    public static final String DN_DELETE_ID_NULL 
    	= "delete(id null) - jette ExceptionAppliParamNonPersistent (contrat du port)";

    /** "delete(absent) - ne fait rien" */
    public static final String DN_DELETE_ABSENT 
    	= "delete(absent) - ne fait rien";

    /** "delete(OK) - supprime l'objet métier créé et le rend introuvable" */
    public static final String DN_DELETE_NOMINAL 
    	= "delete(OK) - supprime l'objet métier créé et le rend introuvable";

    /** "delete(double suppression) - ne lève pas d'exception au second appel" */
    public static final String DN_DELETE_DOUBLE_SUPPRESSION 
    	= "delete(double suppression) - ne lève pas d'exception au second appel";

    /** "count() - cohérent avec SQL et rechercherTous()" */
    public static final String DN_COUNT_NOMINAL 
    	= "count() - cohérent avec SQL et rechercherTous()";
        
    /**
     * "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT"
     */
    public static final String SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT 
    	= "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT";
    
    /**
     * "SELECT SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?"
     */
    public static final String SELECT_STP_FROM_STP_WHERE_ID 
    	= "SELECT SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?";
    
    /**
     * "SELECT TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?"
     */
    public static final String SELECT_TP_FROM_STP_WHERE_ID 
    	= "SELECT TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?";
    
    /**
     * "SELECT TYPE_PRODUIT FROM TYPES_PRODUIT WHERE ID_TYPE_PRODUIT = ?"
     */
    public static final String SELECT_PARAM_TP_FROM_TP_WHERE_ID 
    	= "SELECT TYPE_PRODUIT FROM TYPES_PRODUIT WHERE ID_TYPE_PRODUIT = ?";
    
    /**
     * "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT WHERE UPPER(SOUS_TYPE_PRODUIT) = UPPER(?) AND TYPE_PRODUIT = ?"
     */
    public static final String SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE_AND_PARENT 
    	= "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT WHERE UPPER(SOUS_TYPE_PRODUIT) = UPPER(?) AND TYPE_PRODUIT = ?";

    /**
     * "SELECT ID_SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE UPPER(SOUS_TYPE_PRODUIT) = UPPER(?) AND TYPE_PRODUIT = ?"
     */
    public static final String SELECT_PARAM_ID_FROM_STP_WHERE_LIBELLE_AND_PARENT 
    	= "SELECT ID_SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE UPPER(SOUS_TYPE_PRODUIT) = UPPER(?) AND TYPE_PRODUIT = ?";
    
    /**
     * "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT WHERE UPPER(SOUS_TYPE_PRODUIT) = UPPER(?)"
     */
    public static final String SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE
        = "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT WHERE UPPER(SOUS_TYPE_PRODUIT) = UPPER(?)";

    /**
     * "SELECT ID_SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE UPPER(SOUS_TYPE_PRODUIT) = UPPER(?) ORDER BY ID_SOUS_TYPE_PRODUIT"
     */
    public static final String SELECT_PARAM_IDS_FROM_STP_WHERE_LIBELLE
        = "SELECT ID_SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE UPPER(SOUS_TYPE_PRODUIT) = UPPER(?) ORDER BY ID_SOUS_TYPE_PRODUIT";
    
    /**
     * "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT WHERE UPPER(SOUS_TYPE_PRODUIT) LIKE UPPER(?)"
     */
    public static final String SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE_LIKE
        = "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT WHERE UPPER(SOUS_TYPE_PRODUIT) LIKE UPPER(?)";

    /**
     * "SELECT ID_SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE UPPER(SOUS_TYPE_PRODUIT) LIKE UPPER(?) ORDER BY ID_SOUS_TYPE_PRODUIT"
     */
    public static final String SELECT_PARAM_IDS_FROM_STP_WHERE_LIBELLE_LIKE
        = "SELECT ID_SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE UPPER(SOUS_TYPE_PRODUIT) LIKE UPPER(?) ORDER BY ID_SOUS_TYPE_PRODUIT";
    
    /**
     * "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT WHERE TYPE_PRODUIT = ?"
     */
    public static final String SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_PARENT
        = "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT WHERE TYPE_PRODUIT = ?";

    /**
     * "SELECT ID_SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE TYPE_PRODUIT = ? ORDER BY ID_SOUS_TYPE_PRODUIT"
     */
    public static final String SELECT_PARAM_IDS_FROM_STP_WHERE_PARENT
        = "SELECT ID_SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT WHERE TYPE_PRODUIT = ? ORDER BY ID_SOUS_TYPE_PRODUIT";
    
    /**
     * "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?"
     */
    public static final String SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID
        = "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?";

    /**
     * "INSERT INTO TYPES_PRODUIT (TYPE_PRODUIT) VALUES (?)"
     */
    public static final String INSERT_PARAM_INTO_TP 
    	= "INSERT INTO TYPES_PRODUIT (TYPE_PRODUIT) VALUES (?)";
    
    /**
     * "DELETE FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?"
     */
    public static final String DELETE_FROM_STP_WHERE_ID_STP 
    	= "DELETE FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?";

    // ************************* ATTRIBUTS *******************************/


    /**
     * <div> 
     * <p>Locale par défaut : {@code Locale.getDefault()} </p>
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
    private SousTypeProduitGatewayIService service;

    /**
     * <div>
     * <p>DAO parent 
     * (accès aux IDs persistés pour préparer les objets métier).</p>
     * </div>
     */
    @Autowired
    private TypeProduitDaoJPA typeProduitDaoJPA;

    /**
     * <div>
     * <p>DAO objet métier (enfant) utilisé pour les contrôles directs
     * de comptage et de cohérence avec le stockage.</p>
     * </div>
     */
    @Autowired
    private SousTypeProduitDaoJPA sousTypeProduitDaoJPA;

    /**
     * <div>
     * <p>EntityManager pour le rafraîchissement du cache Hibernate.</p>
     * </div>
     */
    @Autowired
    private EntityManager entityManager;
    
    /**
     * <div>
     * <p>JdbcTemplate pour manipuler directement le stockage en SQL
     * sans passer par Hibernate, afin d'éviter les biais possibles
     * liés au cache Hibernate.</p>
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
    public SousTypeProduitGatewayJPAServiceIntegrationTest() {
        super();
    }

    
    
    // ===================== CONFIGURATION SPRING =======================//

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * Classe interne de configuration Spring.
     * </p>
     * <ul>
     * <li>@Configuration</li>
     * <li>@EnableJpaRepositories(basePackageClasses = {SousTypeProduitDaoJPA.class, TypeProduitDaoJPA.class})</li>
     * <li>@EntityScan(basePackageClasses = {SousTypeProduitJPA.class, TypeProduitJPA.class})</li>
     * </ul>
     * </div>
     *
     * @author Daniel Lévy
     * @version 1.0
     * @since 01 février 2026
     */
    @Configuration
    @EnableJpaRepositories(basePackageClasses = {SousTypeProduitDaoJPA.class, TypeProduitDaoJPA.class})
    @EntityScan(basePackageClasses = {SousTypeProduitJPA.class, TypeProduitJPA.class})
    public static final class ConfigTest { // NOPMD by danyl on 01/02/2026 00:00
    }
    

    
    // =========================== TESTS ================================//
    
    
    
    // =============================== CREER ==============================



    /**
     * <div>
     * <p>garantit que creer(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_CREER_KO_PARAM_NULL} ;</li>
     * <li>n'écrit rien dans le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_NULL)
    @Test
    public void testCreerNull() {

        /* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service afin de pouvoir prouver ensuite
         * qu'aucune écriture n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_CREER_KO_PARAM_NULL);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.creer(null)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
     * {@link SousTypeProduitGatewayIService#MESSAGE_CREER_KO_LIBELLE_BLANK} ;</li>
     * <li>n'écrit rien dans le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_LIBELLE_NULL)
    @Test
    public void testCreerLibelleNull() {

        /* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service,
         * afin de pouvoir prouver ensuite
         * qu'aucune écriture n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* 
         * prépare un objet métier avec un libellé enfant null
         * et un parent persistant.
         */
        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp = new SousTypeProduit(null, null, parent);

        /* ACT - ASSERT :
         * garantit que service.creer(stp)
         * - jette une ExceptionAppliLibelleBlank
         * - émet un message MESSAGE_CREER_KO_LIBELLE_BLANK 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_CREER_KO_LIBELLE_BLANK);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.creer(stp)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
     * {@link SousTypeProduitGatewayIService#MESSAGE_CREER_KO_LIBELLE_BLANK} ;</li>
     * <li>n'écrit rien dans le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_BLANK)
    @Test
    public void testCreerLibelleBlank() {
    	
    	/* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service,
         * afin de pouvoir prouver ensuite
         * qu'aucune écriture n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* 
         * prépare un objet métier valide sur le parent,
         * mais avec un libellé enfant blank,
         * afin de vérifier le contrôle applicatif du PORT
         * avant toute tentative d'accès au stockage.
         */
        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp = new SousTypeProduit(null, BLANK, parent);

        /* ACT - ASSERT :
         * garantit que service.creer(stp)
         * - jette une ExceptionAppliLibelleBlank
         * - émet un message MESSAGE_CREER_KO_LIBELLE_BLANK 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_CREER_KO_LIBELLE_BLANK);
        
        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.creer(stp)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
     * {@link SousTypeProduitGatewayIService#MESSAGE_CREER_KO_PARENT_NULL} ;</li>
     * <li>n'écrit rien dans le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_NULL)
    @Test
    public void testCreerParentNull() {

    	/* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service,
         * afin de pouvoir prouver ensuite
         * qu'aucune écriture n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* 
         * prépare un objet métier à créer sans parent,
         * afin de vérifier le contrôle contractuel
         * du parent obligatoire.
         */
        final SousTypeProduit stp 
        	= new SousTypeProduit(null, LIBELLE_NOUVEAU_PULL, null);

        /* ACT - ASSERT :
         * garantit que l'appel service.creer(stp)
         * - jette une ExceptionAppliParentNull
         * - émet un message MESSAGE_CREER_KO_PARENT_NULL 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_CREER_KO_PARENT_NULL);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.creer(stp)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
     * {@link SousTypeProduitGatewayIService#MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'écrit rien dans le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_LIBELLE_NULL)
    @Test
    public void testCreerParentLibelleNull() {

    	/* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service,
         * afin de pouvoir prouver ensuite
         * qu'aucune écriture n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* 
         * prépare un parent avec libellé null,
         * afin de vérifier le contrôle contractuel
         * sur le parent de l'objet à créer.
         */
        final TypeProduit parent 
        	= new TypeProduit(Long.valueOf(1L), null);
        final SousTypeProduit stp 
        	= new SousTypeProduit(null, LIBELLE_NOUVEAU_PULL, parent);

        /* ACT - ASSERT :
         * garantit que l'appel service.creer(stp)
         * - jette une ExceptionAppliLibelleBlank
         * - émet un message MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.creer(stp)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
     * {@link SousTypeProduitGatewayIService#MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'écrit rien dans le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_LIBELLE_BLANK)
    @Test
    public void testCreerParentLibelleBlank() {

    	/* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service,
         * afin de pouvoir prouver ensuite
         * qu'aucune écriture n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* 
         * prépare un parent avec libellé blank,
         * afin de vérifier le contrôle contractuel
         * sur le parent de l'objet à créer.
         */
        final TypeProduit parent 
        	= new TypeProduit(Long.valueOf(1L), BLANK);
        final SousTypeProduit stp 
        	= new SousTypeProduit(null, LIBELLE_NOUVEAU_PULL, parent);

        /* ACT - ASSERT :
         * garantit que l'appel service.creer(stp)
         * - jette une ExceptionAppliLibelleBlank
         * - émet un message MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.creer(stp)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
     * {@link SousTypeProduitGatewayIService#MESSAGE_CREER_KO_PARENT_NON_PERSISTENT} ;</li>
     * <li>n'écrit rien dans le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_NON_PERSISTANT)
    @Test
    public void testCreerParentIdNull() {

    	/* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service,
         * afin de pouvoir prouver ensuite
         * qu'aucune écriture n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* 
         * prépare un parent non persistant
         * dont l'identifiant est null,
         * afin de vérifier le contrôle de persistance
         * sur le parent.
         */
        final TypeProduit parent 
        	= new TypeProduit(null, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp 
        	= new SousTypeProduit(null, LIBELLE_NOUVEAU_PULL, parent);

        /* ACT - ASSERT :
         * garantit que l'appel service.creer(stp)
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet un message MESSAGE_CREER_KO_PARENT_NON_PERSISTENT 
         * + LIBELLE_PARENT_VETEMENT (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(
                    SousTypeProduitGatewayIService.MESSAGE_CREER_KO_PARENT_NON_PERSISTENT
                            + LIBELLE_PARENT_VETEMENT);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.creer(stp)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
     * {@link SousTypeProduitGatewayIService#MESSAGE_CREER_KO_PARENT_NON_PERSISTENT} ;</li>
     * <li>prouve que le parent est absent du stockage avant l'appel ;</li>
     * <li>n'écrit rien dans le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_NON_PERSISTANT)
    @Test
    public void testCreerParentAbsent() {

    	/* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service,
         * afin de pouvoir prouver ensuite
         * qu'aucune écriture n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* 
         * Vérifie directement dans le stockage
         * qu'aucun parent ne porte l'identifiant ID_INEXISTANT.
         */
        final Long countParentStockage = this.jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM TYPES_PRODUIT WHERE ID_TYPE_PRODUIT = ?", // NOPMD by danyl on 06/05/2026 18:09
                Long.class,
                ID_INEXISTANT);

        assertThat(countParentStockage).isNotNull().isZero();

        /* 
         * prépare un parent portant un identifiant inexistant,
         * afin de vérifier le contrôle de persistance
         * du parent dans le stockage.
         */
        final TypeProduit parent 
        	= new TypeProduit(ID_INEXISTANT, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp 
        	= new SousTypeProduit(null, LIBELLE_NOUVEAU_PULL, parent);

        /* ACT - ASSERT :
         * garantit que l'appel service.creer(stp)
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet un message MESSAGE_CREER_KO_PARENT_NON_PERSISTENT 
         * + LIBELLE_PARENT_VETEMENT (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(
                    SousTypeProduitGatewayIService.MESSAGE_CREER_KO_PARENT_NON_PERSISTENT
                            + LIBELLE_PARENT_VETEMENT);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.creer(stp)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
     * est une {@link UnexpectedRollbackException} ;</li>
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
    @DisplayName("creer(doublon) - jette UnexpectedRollbackException et ne crée aucun nouvel enregistrement")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCreerDoublon() throws Exception {

        /* ARRANGE :
         * lit d'abord le stockage par SQL direct afin de disposer 
         * d'une preuve indépendante du contexte Hibernate.
         *
         * Le doublon testé ici porte sur le couple :
         * - parent = vêtement
         * - objet métier = vêtement pour homme
         */
        final Long idParentVetement =
                retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);
        
        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* 
         * Lit directement (en SQL) dans le stockage le nombre 
         * d'enregistrements correspondant déjà à l'objet métier :
         * - objet métier = LIBELLE_ENFANT_VETEMENT_HOMME
         * - parent = idParentVetement
         *
         * La comparaison sur le libellé est faite
         * sans tenir compte de la casse,
         * afin de vérifier l'existence réelle d'un doublon fonctionnel
         * avant l'appel du service.
         * 
         * - LIBELLE_ENFANT_VETEMENT_HOMME injecté dans le premier ?
         * - idParentVetement injecté dans le second ?
         */
        final Long countCoupleAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE_AND_PARENT,
                Long.class,
                LIBELLE_ENFANT_VETEMENT_HOMME,
                idParentVetement);

        final Long idSeedAvant = this.jdbcTemplate.queryForObject(
                SELECT_PARAM_ID_FROM_STP_WHERE_LIBELLE_AND_PARENT,
                Long.class,
                LIBELLE_ENFANT_VETEMENT_HOMME,
                idParentVetement);

        final SousTypeProduit aCreer = new SousTypeProduit(
                null,
                LIBELLE_ENFANT_VETEMENT_HOMME,
                new TypeProduit(idParentVetement, LIBELLE_PARENT_VETEMENT));

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
            .isInstanceOf(UnexpectedRollbackException.class);

        /* ASSERT :
         * contrôle ensuite par SQL direct
         * qu'aucun effet de bord n'a été produit dans le stockage.
         *
         * On évite volontairement tout raisonnement 
         * basé sur le cache Hibernate.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final Long countCoupleApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE_AND_PARENT,
                Long.class,
                LIBELLE_ENFANT_VETEMENT_HOMME,
                idParentVetement);

        final Long idSeedApres = this.jdbcTemplate.queryForObject(
                SELECT_PARAM_ID_FROM_STP_WHERE_LIBELLE_AND_PARENT,
                Long.class,
                LIBELLE_ENFANT_VETEMENT_HOMME,
                idParentVetement);

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
     * <li>retourne un {@link SousTypeProduit} persistant ;</li>
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
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);

        final SousTypeProduit aCreer = new SousTypeProduit(
                null,
                LIBELLE_NOUVEAU_PULL,
                new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT));

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        Long idCree = null;

        try {

            /* ACT :
             * appelle service.creer(aCreer).
             *
             * Le try/finally encadre la création réelle,
             * afin de garantir le nettoyage défensif
             * même si une assertion échoue après cette écriture.
             */
            final SousTypeProduit cree = this.service.creer(aCreer);

            /* ASSERT :
             * garantit d'abord que l'objet métier retourné
             * est bien persistant et correctement renseigné.
             */
            assertThat(cree).isNotNull();
            assertThat(cree.getIdSousTypeProduit()).isNotNull().isPositive();
            assertThat(cree.getSousTypeProduit()).isEqualTo(LIBELLE_NOUVEAU_PULL);
            assertThat(cree.getTypeProduit()).isNotNull();
            assertThat(cree.getTypeProduit().getIdTypeProduit()).isEqualTo(idParent);
            assertThat(cree.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_VETEMENT);

            idCree = cree.getIdSousTypeProduit();

            /* ASSERT :
             * contrôle ensuite physiquement le stockage par SQL direct,
             * pour prouver l'écriture réelle dans le stockage
             * et non un simple effet de cache Hibernate.
             */
            final Long countApres = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                    Long.class);

            final Integer countEnStockage = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                    Integer.class,
                    idCree);

            final String libelleStockage = this.jdbcTemplate.queryForObject(
                    SELECT_STP_FROM_STP_WHERE_ID,
                    String.class,
                    idCree);

            final Long parentStockage = this.jdbcTemplate.queryForObject(
                    SELECT_TP_FROM_STP_WHERE_ID,
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
            assertThat(libelleStockage).isEqualTo(LIBELLE_NOUVEAU_PULL);

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
            final SousTypeProduit relu = this.service.findById(idCree);
            assertThat(relu).isNotNull();
            assertThat(relu.getIdSousTypeProduit()).isEqualTo(idCree);
            assertThat(relu.getSousTypeProduit()).isEqualTo(LIBELLE_NOUVEAU_PULL);
            assertThat(relu.getTypeProduit()).isNotNull();
            assertThat(relu.getTypeProduit().getIdTypeProduit()).isEqualTo(idParent);

            /*
             * Garantit enfin que les données seedées
             * restent présentes après l'appel service.creer(aCreer).
             */
            final List<SousTypeProduit> liste = this.service.rechercherTous();
            assertThat(liste)
                .extracting(SousTypeProduit::getSousTypeProduit)
                .contains(
                        LIBELLE_ENFANT_VETEMENT_HOMME,
                        LIBELLE_ENFANT_VETEMENT_FEMME,
                        LIBELLE_ENFANT_VETEMENT_ENFANT,
                        LIBELLE_NOUVEAU_PULL);

        } finally {

            /* Nettoyage défensif :
             * si l'enregistrement créé existe encore dans le stockage
             * après une éventuelle assertion en échec,
             * le supprime explicitement afin de garantir l'isolation du test.
             */
            if (idCree != null) {
                final Integer countLigne = this.jdbcTemplate.queryForObject(
                        SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                        Integer.class,
                        idCree);

                if ((countLigne != null) && (countLigne.intValue() == 1)) {
                    this.jdbcTemplate.update(
                            DELETE_FROM_STP_WHERE_ID_STP,
                            idCree);
                }
            }

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
    @DisplayName("creer(plusieurs créations) : crée plusieurs objets métier distincts et tous retrouvables")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCreerPlusieurs() throws Exception {

        /* ARRANGE :
         * lit d'abord l'état physique du stockage,
         * retrouve un parent persistant,
         * puis prépare deux créations nominales distinctes
         * sous ce même parent.
         *
         * Le test est volontairement exécuté hors transaction de test
         * pour prouver des écritures réelles dans le stockage,
         * puis réaliser un nettoyage physique explicite en finally.
         */
        final Long countAvantStockage = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countAvantStockage).isNotNull().isNotZero();

        final long countAvant = this.service.count();
        assertThat(countAvant).isEqualTo(countAvantStockage.longValue());

        final Long idParent =
                retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);

        assertThat(idParent).isNotNull();

        final String libelleNouveauPull2 = LIBELLE_NOUVEAU_PULL + " 2";

        Long id1 = null;
        Long id2 = null;

        try {

            /* ACT :
             * exécute deux créations successives
             * sur deux libellés enfants différents,
             * sous le même parent persistant.
             */
            final SousTypeProduit cree1 = this.service.creer(
                    new SousTypeProduit(
                            null,
                            LIBELLE_NOUVEAU_PULL,
                            new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT)));

            final SousTypeProduit cree2 = this.service.creer(
                    new SousTypeProduit(
                            null,
                            libelleNouveauPull2,
                            new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT)));

            /* ASSERT :
             * garantit d'abord que les deux objets métier retournés
             * sont persistants, correctement renseignés et distincts.
             */
            assertThat(cree1).isNotNull();
            assertThat(cree2).isNotNull();

            assertThat(cree1.getIdSousTypeProduit()).isNotNull().isPositive();
            assertThat(cree2.getIdSousTypeProduit()).isNotNull().isPositive();
            assertThat(cree1.getIdSousTypeProduit())
                .isNotEqualTo(cree2.getIdSousTypeProduit());

            assertThat(cree1.getSousTypeProduit()).isEqualTo(LIBELLE_NOUVEAU_PULL);
            assertThat(cree2.getSousTypeProduit()).isEqualTo(libelleNouveauPull2);

            assertThat(cree1.getTypeProduit()).isNotNull();
            assertThat(cree2.getTypeProduit()).isNotNull();
            assertThat(cree1.getTypeProduit().getIdTypeProduit()).isEqualTo(idParent);
            assertThat(cree2.getTypeProduit().getIdTypeProduit()).isEqualTo(idParent);
            assertThat(cree1.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_VETEMENT);
            assertThat(cree2.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_VETEMENT);

            id1 = cree1.getIdSousTypeProduit();
            id2 = cree2.getIdSousTypeProduit();

            /* Garantit que le compteur total
             * augmente exactement de deux enregistrements.
             */
            final long countApres = this.service.count();

            final Long countApresStockage = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                    Long.class);

            assertThat(countApres).isEqualTo(countAvant + 2L);
            assertThat(countApresStockage).isNotNull();
            assertThat(countApresStockage).isEqualTo(countAvantStockage + 2L);
            assertThat(countApres).isEqualTo(countApresStockage.longValue());

            /* Garantit physiquement dans le stockage
             * que chaque identifiant correspond à un enregistrement réel.
             */
            final Integer countEnStockage1 = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                    Integer.class,
                    id1);

            final Integer countEnStockage2 = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                    Integer.class,
                    id2);

            assertThat(countEnStockage1).isNotNull().isEqualTo(1);
            assertThat(countEnStockage2).isNotNull().isEqualTo(1);

            /* Garantit physiquement dans le stockage
             * que chaque enregistrement porte le bon libellé enfant.
             */
            final String libelleStockage1 = this.jdbcTemplate.queryForObject(
                    SELECT_STP_FROM_STP_WHERE_ID,
                    String.class,
                    id1);

            final String libelleStockage2 = this.jdbcTemplate.queryForObject(
                    SELECT_STP_FROM_STP_WHERE_ID,
                    String.class,
                    id2);

            assertThat(libelleStockage1).isEqualTo(LIBELLE_NOUVEAU_PULL);
            assertThat(libelleStockage2).isEqualTo(libelleNouveauPull2);

            /* Garantit physiquement dans le stockage
             * que chaque enregistrement porte la bonne clé étrangère parent.
             */
            final Long parentStockage1 = this.jdbcTemplate.queryForObject(
                    SELECT_TP_FROM_STP_WHERE_ID,
                    Long.class,
                    id1);

            final Long parentStockage2 = this.jdbcTemplate.queryForObject(
                    SELECT_TP_FROM_STP_WHERE_ID,
                    Long.class,
                    id2);

            assertThat(parentStockage1).isEqualTo(idParent);
            assertThat(parentStockage2).isEqualTo(idParent);

            /*
             * Neutralise explicitement le contexte Hibernate
             * avant toute relecture via le service pour éviter
             * d'être leurré par le cache Hibernate.
             */
            this.entityManager.clear();

            /* Garantit que chaque création
             * est retrouvable séparément via le service.
             */
            final SousTypeProduit relu1 = this.service.findById(id1);
            final SousTypeProduit relu2 = this.service.findById(id2);

            assertThat(relu1).isNotNull();
            assertThat(relu2).isNotNull();

            assertThat(relu1.getIdSousTypeProduit()).isEqualTo(id1);
            assertThat(relu2.getIdSousTypeProduit()).isEqualTo(id2);

            assertThat(relu1.getSousTypeProduit()).isEqualTo(LIBELLE_NOUVEAU_PULL);
            assertThat(relu2.getSousTypeProduit()).isEqualTo(libelleNouveauPull2);

            assertThat(relu1.getTypeProduit()).isNotNull();
            assertThat(relu2.getTypeProduit()).isNotNull();
            assertThat(relu1.getTypeProduit().getIdTypeProduit()).isEqualTo(idParent);
            assertThat(relu2.getTypeProduit().getIdTypeProduit()).isEqualTo(idParent);

            /* Garantit enfin que la liste globale
             * contient à la fois les données seedées
             * et les deux nouveaux objets métier créés.
             */
            final List<SousTypeProduit> liste = this.service.rechercherTous();

            assertThat(liste)
                .extracting(SousTypeProduit::getSousTypeProduit)
                .contains(
                        LIBELLE_ENFANT_VETEMENT_HOMME,
                        LIBELLE_ENFANT_VETEMENT_FEMME,
                        LIBELLE_ENFANT_VETEMENT_ENFANT,
                        LIBELLE_NOUVEAU_PULL,
                        libelleNouveauPull2);

        } finally {

            /* Nettoyage défensif :
             * supprime explicitement les enregistrements créés,
             * en commençant par le second puis le premier,
             * uniquement s'ils existent encore dans le stockage,
             * afin de garantir l'isolation du test.
             */
            if (id2 != null) {
                final Integer countLigne2 = this.jdbcTemplate.queryForObject(
                        SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                        Integer.class,
                        id2);

                if ((countLigne2 != null) && (countLigne2.intValue() == 1)) {
                    this.jdbcTemplate.update(
                            DELETE_FROM_STP_WHERE_ID_STP,
                            id2);
                }
            }

            if (id1 != null) {
                final Integer countLigne1 = this.jdbcTemplate.queryForObject(
                        SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                        Integer.class,
                        id1);

                if ((countLigne1 != null) && (countLigne1.intValue() == 1)) {
                    this.jdbcTemplate.update(
                            DELETE_FROM_STP_WHERE_ID_STP,
                            id1);
                }
            }

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
    @DisplayName("rechercherTous(stockage vide) - retourne une liste vide non null")
    @Test
    @Sql(
        scripts = SousTypeProduitGatewayJPAServiceIntegrationTest.CLASSPATH_TRUNCATE_SQL,
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
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
        final List<SousTypeProduit> resultats = this.service.rechercherTous();

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
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que rechercherTous(OK) sur le stockage seedé :</p>
     * <ul>
     * <li>retourne une liste non null et non vide ;</li>
     * <li>retourne exactement les libellés physiquement 
     * présents dans le stockage ;</li>
     * <li>retourne autant d'objets métier que 
     * d'enregistrements présents dans le stockage ;</li>
     * <li>retourne une liste triée par libellé et sans doublon ;</li>
     * <li>retourne des objets métier portant chacun un parent non null ;</li>
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
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isPositive();
        
        /* 
         * récupère ensuite (en SQL) la liste des libellés 
         * des enregistrements dans le stockage.
         */
        final List<String> libellesEnBase = this.jdbcTemplate.queryForList(
                "SELECT SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT",
                String.class);

        /* assure que le stockage n'est pas null ni vide. */
        assertThat(libellesEnBase).isNotNull().isNotEmpty();

        /* 
         * Ordonne le retour SQL
         * selon le même critère métier attendu côté service :
         * tri alphabétique insensible à la casse.
         */
        libellesEnBase.sort(String.CASE_INSENSITIVE_ORDER);

        /* ACT :
         * sollicite service.rechercherTous()
         * sur le stockage seedé.
         */
        final List<SousTypeProduit> resultats 
        	= this.service.rechercherTous();

        /* ASSERT :
         * vérifie d'abord que service.rechercherTous() retourne
         * une liste exploitable (non null et non vide).
         */
        assertThat(resultats).isNotNull().isNotEmpty();

        /* 
         * Vérifie ensuite que le nombre d'objets métier retournés
         * par service.rechercherTous() correspond exactement 
         * au nombre d'enregistrements dans le stockage (récupéré par SQL).
         */
        assertThat(resultats).hasSize(countAvant.intValue());

        /*
         * Extrait les libellés des objets métier retournés par le service :
         * - resultats contient la liste des SousTypeProduit renvoyés
         *   par service.rechercherTous() ;
         * - stream() parcourt cette liste élément par élément ;
         * - map(SousTypeProduit::getSousTypeProduit) transforme chaque
         *   SousTypeProduit en son libellé enfant ;
         * - toList() reconstruit une liste ne contenant que ces libellés.
         *
         * Cette liste de libellés permet ensuite de comparer simplement
         * le résultat du service avec les libellés lus directement
         * dans le stockage.
         */
        final List<String> libellesResultats = resultats.stream()
                .map(SousTypeProduit::getSousTypeProduit)
                .toList();

        /* 
         * Vérifie que les libellés renvoyés par le service
         * correspondent exactement aux libellés physiquement 
         * présents dans le stockage.
         */
        assertThat(libellesResultats).containsExactlyElementsOf(libellesEnBase);

        /*
         * Vérifie les propriétés métier générales de la liste retournée :
         * - doesNotHaveDuplicates() garantit que le service ne renvoie pas
         *   deux fois le même libellé enfant ;
         * - isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER) garantit
         *   que les libellés sont triés alphabétiquement sans tenir compte
         *   des majuscules/minuscules ;
         * - allSatisfy(...) applique les assertions à chaque SousTypeProduit
         *   retourné par service.rechercherTous() ;
         * - stp représente l'objet métier courant pendant le parcours ;
         * - on vérifie que chaque objet métier est non null ;
         * - on vérifie que chaque objet métier porte un parent non null ;
         * - on vérifie que chaque parent possède un identifiant persistant ;
         * - on vérifie que chaque parent possède un libellé exploitable.
         *
         * Cette vérification garantit donc que la liste retournée :
         * - est triée,
         * - sans doublon de libellé enfant,
         * - et composée d'objets métier complets côté relation parent.
         */
        assertThat(libellesResultats).doesNotHaveDuplicates();
        assertThat(libellesResultats).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);
        assertThat(resultats)
            .allSatisfy(stp -> {
                assertThat(stp).isNotNull();
                assertThat(stp.getTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getIdTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getTypeProduit()).isNotBlank();
            });
        
        /* ASSERT :
         * compte finalement (en SQL)
         * le nombre d'enregistrements dans le stockage
         * pour prouver que service.rechercherTous()
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    
    

    // ===================== rechercherTousParPage =====================



    /**
     * <div>
     * <p>garantit que rechercherTousParPage(null) :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>retourne un contenu non null ;</li>
     * <li>retourne autant d'objets métier que 
     * d'enregistrements présents dans le stockage ;</li>
     * <li>applique les paramètres par défaut de pagination ;</li>
     * <li>retourne des objets métier portant chacun un parent non null ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(null) - applique la pagination par défaut et reste cohérent avec le stockage")
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
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isPositive();

        /* ACT :
         * sollicite la pagination avec une requête null,
         * ce qui doit conduire le service
         * à appliquer les paramètres par défaut 
         * (PAGE_DEFAUT, TAILLE_DEFAUT).
         */
        final ResultatPage<SousTypeProduit> resultat =
                this.service.rechercherTousParPage(null);

        /* ASSERT :
         * vérifie d'abord la cohérence générale de la page retournée.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull();
        assertThat(resultat.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(resultat.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(resultat.getTotalElements()).isEqualTo(countAvant);

        /* Vérifie ensuite que la taille du contenu retourné
         * correspond exactement à la taille de la première page attendue.
         */
        assertThat(resultat.getContent())
            .hasSize(Math.min(countAvant.intValue(), RequetePage.TAILLE_DEFAUT));

        /* Vérifie enfin que les objets métier retournés
         * dans la page "null" sont :
         * - non null
         * - avec un libellé non blank
         * - avec un parent persistant.
         */
        assertThat(resultat.getContent())
            .allSatisfy(stp -> {
                assertThat(stp).isNotNull();
                assertThat(stp.getSousTypeProduit()).isNotBlank();
                assertThat(stp.getTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getIdTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getTypeProduit()).isNotBlank();
            });

        /* ASSERT :
         * compte finalement (en SQL)
         * le nombre d'enregistrements dans le stockage
         * pour prouver que service.rechercherTousParPage(null)
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>garantit que rechercherTousParPage(avec tri) :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>retourne un contenu non null de taille 2 ;</li>
     * <li>conserve le numéro de page demandé ;</li>
     * <li>conserve la taille de page demandée ;</li>
     * <li>retourne un total d'enregistrements égal 
     * au nombre d'enregistrements présents dans le stockage ;</li>
     * <li>retourne exactement les deux premiers libellés lus en SQL
     * dans le stockage avec le tri demandé ;</li>
     * <li>retourne des objets métier portant chacun un parent non null
     * avec un identifiant persistant ;</li>
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
         * lit d'abord (en SQL) les libellés présents dans le stockage,
         * triés selon le même tri que celui demandé au service :
         * SOUS_TYPE_PRODUIT ascendant sans tenir compte de la casse.
         */
        final List<String> libellesStockage = this.jdbcTemplate.queryForList(
                "SELECT SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT ORDER BY UPPER(SOUS_TYPE_PRODUIT) ASC",
                String.class);

        /* 
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service afin de pouvoir prouver ensuite
         * que service.rechercherTousParPage(requete)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final int pageSize = 2;

        /* 
         * Vérifie que le stockage contient assez d'enregistrements
         * pour contrôler une page de taille 2.
         */
        assertThat(libellesStockage).isNotNull().hasSizeGreaterThanOrEqualTo(pageSize);
        assertThat(countAvant).isNotNull().isGreaterThanOrEqualTo(Long.valueOf(pageSize));
        assertThat(libellesStockage).hasSize(countAvant.intValue());

        /* 
         * construit une liste de tris explicite,
         * puis l'injecte dans la RequetePage.
         *
         * Ne pas utiliser requete.getTris().add(...),
         * car getTris() retourne une copie défensive.
         */
        final List<TriSpec> tris = new ArrayList<TriSpec>();
        tris.add(new TriSpec(SOUS_TYPE_PRODUIT, DirectionTri.ASC));

        /* 
         * construit une requête demandant :
         * - la première page ;
         * - une taille de page égale à 2 ;
         * - un tri ascendant sur le libellé enfant.
         */
        final RequetePage requete = new RequetePage(0, pageSize, tris);

        /* ACT :
         * appelle service.rechercherTousParPage(requete)
         * avec page 0, taille 2 et tri ascendant sur le libellé enfant.
         */
        final ResultatPage<SousTypeProduit> resultat =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie le numéro de page,
         * la taille de page,
         * le total d'enregistrements
         * et le nombre d'objets métier retournés.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().hasSize(pageSize);
        assertThat(resultat.getPageNumber()).isEqualTo(0);
        assertThat(resultat.getPageSize()).isEqualTo(pageSize);
        assertThat(resultat.getTotalElements()).isEqualTo(countAvant);

        /*
         * Extrait les libellés des objets métier retournés dans la page :
         * - resultat.getContent() fournit la liste des SousTypeProduit
         *   contenus dans la page retournée par le service ;
         * - stream() parcourt cette liste élément par élément ;
         * - map(SousTypeProduit::getSousTypeProduit) transforme chaque
         *   SousTypeProduit en son libellé enfant ;
         * - toList() reconstruit une liste contenant uniquement
         *   les libellés retournés dans la page.
         *
         * Cette liste de libellés pourra ensuite être comparée
         * aux libellés lus directement dans le stockage.
         */
        final List<String> libellesPage = resultat.getContent().stream()
                .map(SousTypeProduit::getSousTypeProduit)
                .toList();

        /* 
         * Vérifie que le service retourne exactement
         * les deux premiers libellés lus en SQL dans le stockage
         * avec le tri demandé.
         */
        assertThat(libellesPage)
            .containsExactlyElementsOf(libellesStockage.subList(0, pageSize));

        /* 
         * Vérifie que les libellés retournés sont triés alphabétiquement
         * sans tenir compte des majuscules/minuscules.
         */
        assertThat(libellesPage).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);

        /* 
         * Vérifie que chaque objet métier retourné porte :
         * - un parent non null ;
         * - un parent avec un identifiant persistant.
         */
        assertThat(resultat.getContent())
            .allSatisfy(stp -> {
                assertThat(stp).isNotNull();
                assertThat(stp.getTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getIdTypeProduit()).isNotNull();
            });

        /* ASSERT :
         * compte finalement (en SQL)
         * le nombre d'enregistrements dans le stockage
         * pour prouver que service.rechercherTousParPage(requete)
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que rechercherTousParPage(...) sur un stockage vide :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>retourne un contenu non null et vide ;</li>
     * <li>conserve le numéro de page demandé ;</li>
     * <li>conserve la taille de page demandée ;</li>
     * <li>retourne un total d'enregistrements égal à zéro ;</li>
     * <li>n'altère pas le stockage vide.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName(DN_RECHERCHER_TOUS_PAR_PAGE_VIDE)
    @Test
    @Sql(
        scripts = SousTypeProduitGatewayJPAServiceIntegrationTest.CLASSPATH_TRUNCATE_SQL,
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    public void testRechercherTousParPageStockageVide() throws Exception {

        /* ARRANGE :
         * remplace pour ce test la préparation standard
         * par le seul script de vidage
         * afin d'obtenir un stockage vide.
         *
         * Compte ensuite directement (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service, afin de pouvoir prouver
         * que service.rechercherTousParPage(requete)
         * conserve ce stockage vide.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /*
         * Assure que le stockage est vide
         * avant l'appel service.rechercherTousParPage(requete).
         */
        assertThat(countAvant).isNotNull().isZero();

        /*
         * Construit une requête demandant :
         * - la première page ;
         * - une taille de page égale à 10.
         */
        final RequetePage requete = new RequetePage();
        requete.setPageNumber(0);
        requete.setPageSize(10);

        /* ACT :
         * appelle service.rechercherTousParPage(requete)
         * sur le stockage vide.
         */
        final ResultatPage<SousTypeProduit> resultat =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie que service.rechercherTousParPage(requete) retourne :
         * - une page non null ;
         * - un contenu non null et vide ;
         * - le numéro de page demandé ;
         * - la taille de page demandée ;
         * - un total d'enregistrements égal à zéro.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().isEmpty();
        assertThat(resultat.getPageNumber()).isEqualTo(0);
        assertThat(resultat.getPageSize()).isEqualTo(10);
        assertThat(resultat.getTotalElements()).isZero();

        /* ASSERT :
         * compte finalement (en SQL)
         * le nombre d'enregistrements dans le stockage
         * pour prouver que service.rechercherTousParPage(requete)
         * n'a pas modifié le stockage vide.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que rechercherTousParPage(
     * taille supérieure au total d'enregistrements dans le stockage) :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>retourne un contenu non null et non vide ;</li>
     * <li>retourne tout le contenu disponible dans le stockage ;</li>
     * <li>retourne un total d'enregistrements égal 
     * au nombre total d'enregistrements dans le stockage ;</li>
     * <li>conserve les paramètres demandés de pagination ;</li>
     * <li>retourne exactement l'état physique trié attendu ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(taille supérieure au total) - retourne tout le contenu disponible")
    @Test
    public void testRechercherTousParPageTailleSuperieureAuTotal() throws Exception {

        /* ARRANGE :
         * lit d'abord (en SQL) directement dans le stockage
         * les libellés physiquement présents,
         * déjà ordonnés selon le tri métier demandé,
         * afin de disposer d'une référence indépendante d'Hibernate.
         */
        final List<String> libellesStockage = this.jdbcTemplate.queryForList(
                "SELECT SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT ORDER BY UPPER(SOUS_TYPE_PRODUIT) ASC",
                String.class);

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /*
         * Assure que le stockage n'est pas vide.
         */
        assertThat(countAvant).isNotNull().isPositive();
        assertThat(libellesStockage).isNotNull().isNotEmpty();
        assertThat(libellesStockage).hasSize(countAvant.intValue());

        final int pageSize = countAvant.intValue() + 10;

        /* 
         * construit une liste de tris explicite,
         * puis l'injecte dans la RequetePage.
         *
         * Ne pas utiliser requete.getTris().add(...),
         * car getTris() retourne une copie défensive.
         */
        final List<TriSpec> tris = new ArrayList<TriSpec>();
        tris.add(new TriSpec(SOUS_TYPE_PRODUIT, DirectionTri.ASC));

        /* 
         * construit une requête dont la taille de page pageSize
         * dépasse le nombre total d'enregistrements disponibles 
         * dans le stockage.
         */
        final RequetePage requete = new RequetePage(0, pageSize, tris);

        /* ACT :
         * appelle service.rechercherTousParPage(requete)
         * avec une taille supérieure au total disponible.
         */
        final ResultatPage<SousTypeProduit> resultat =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie d'abord la cohérence générale de la page.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().isNotEmpty();
        assertThat(resultat.getPageNumber()).isEqualTo(0);
        assertThat(resultat.getPageSize()).isEqualTo(pageSize);
        assertThat(resultat.getTotalElements()).isEqualTo(countAvant);

        /* Vérifie ensuite que tout le contenu disponible
         * dans le stockage est retourné.
         */
        assertThat(resultat.getContent()).hasSize(countAvant.intValue());

        final List<String> libellesPage = resultat.getContent().stream()
                .map(SousTypeProduit::getSousTypeProduit)
                .toList();

        /* 
         * Vérifie que le contenu retourné
         * correspond exactement aux enregistrements 
         * présents dans le stockage triés.
         */
        assertThat(libellesPage).containsExactlyElementsOf(libellesStockage);

        /* 
         * Vérifie enfin le tri alphabétique du contenu retourné
         * et la cohérence des parents portés par les objets métier.
         */
        assertThat(libellesPage).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);
        assertThat(resultat.getContent())
            .allSatisfy(stp -> {
                assertThat(stp).isNotNull();
                assertThat(stp.getSousTypeProduit()).isNotBlank();
                assertThat(stp.getTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getIdTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getTypeProduit()).isNotBlank();
            });

        /* ASSERT :
         * compte finalement (en SQL)
         * le nombre d'enregistrements dans le stockage
         * pour prouver que service.rechercherTousParPage(...)
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
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
     * <li>retourne un total d'enregistrements égal
     * au nombre d'enregistrements présents dans le stockage ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(page hors bornes) - retourne une page vide")
    @Test
    public void testRechercherTousParPagePageHorsBorne() throws Exception {

        /* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service afin de pouvoir prouver ensuite
         * que service.rechercherTousParPage(requete)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /*
         * Assure que le stockage n'est pas vide.
         */
        assertThat(countAvant).isNotNull().isPositive();

        /* 
         * construit une liste de tris explicite,
         * puis l'injecte dans la RequetePage.
         *
         * Ne pas utiliser requete.getTris().add(...),
         * car getTris() retourne une copie défensive.
         */
        final List<TriSpec> tris = new ArrayList<TriSpec>();
        tris.add(new TriSpec(SOUS_TYPE_PRODUIT, DirectionTri.ASC));

        /* 
         * construit une requête demandant :
         * - une page très au-delà des enregistrements disponibles ;
         * - une taille de page égale à 2 ;
         * - un tri ascendant sur le libellé enfant.
         */
        final RequetePage requete = new RequetePage(999, 2, tris);

        /* ACT :
         * appelle service.rechercherTousParPage(requete)
         * avec un numéro de page hors bornes.
         */
        final ResultatPage<SousTypeProduit> resultat =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie que service.rechercherTousParPage(requete) retourne :
         * - une page non null ;
         * - un contenu non null et vide ;
         * - le numéro de page demandé ;
         * - la taille de page demandée ;
         * - le nombre total d'enregistrements présents dans le stockage.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().isEmpty();
        assertThat(resultat.getPageNumber()).isEqualTo(999);
        assertThat(resultat.getPageSize()).isEqualTo(2);
        assertThat(resultat.getTotalElements()).isEqualTo(countAvant);

        /* ASSERT :
         * compte finalement (en SQL)
         * le nombre d'enregistrements dans le stockage
         * pour prouver que service.rechercherTousParPage(requete)
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>garantit que rechercherTousParPage(taille zéro) :</p>
     * <ul>
     * <li>normalise la taille demandée à zéro vers la taille par défaut ;</li>
     * <li>retourne une page non null ;</li>
     * <li>retourne un contenu non null et non vide ;</li>
     * <li>retourne le premier segment de l'état physique trié ;</li>
     * <li>retourne un total d'enregistrements égal 
     * au nombre total d'enregistrements dans le stockage ;</li>
     * <li>conserve le numéro de page demandé ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(taille zéro) - normalise la taille et retourne la première page cohérente")
    @Test
    public void testRechercherTousParPageTailleZero() throws Exception {

        /* ARRANGE :
         * lit d'abord (en SQL) directement dans le stockage
         * les libellés physiquement présents,
         * déjà ordonnés selon le tri métier demandé,
         * afin de disposer d'une référence indépendante d'Hibernate.
         */
        final List<String> libellesStockage = this.jdbcTemplate.queryForList(
                "SELECT SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT ORDER BY UPPER(SOUS_TYPE_PRODUIT) ASC",
                String.class);

        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /*
         * Assure que le stockage n'est pas vide.
         */
        assertThat(countAvant).isNotNull().isPositive();
        assertThat(libellesStockage).isNotNull().isNotEmpty();
        assertThat(libellesStockage).hasSize(countAvant.intValue());

        /* 
         * construit une liste de tris explicite,
         * puis l'injecte dans la RequetePage.
         *
         * Ne pas utiliser requete.getTris().add(...),
         * car getTris() retourne une copie défensive.
         */
        final List<TriSpec> tris = new ArrayList<TriSpec>();
        tris.add(new TriSpec(SOUS_TYPE_PRODUIT, DirectionTri.ASC));

        /*
         * Construit une requête avec une taille demandée à zéro.
         *
         * Cette taille n'a pas de sens métier comme taille de page :
         * elle sert ici à vérifier la normalisation contractuelle
         * de RequetePage vers RequetePage.TAILLE_DEFAUT.
         */
        final RequetePage requete = new RequetePage(0, 0, tris);

        final int tailleAttendue = Math.min(
                RequetePage.TAILLE_DEFAUT,
                countAvant.intValue());

        /* ACT :
         * appelle service.rechercherTousParPage(requete)
         * avec une requête dont la taille initialement fournie vaut zéro.
         */
        final ResultatPage<SousTypeProduit> resultat =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie d'abord que la page retournée
         * est cohérente avec la taille normalisée.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().isNotEmpty();
        assertThat(resultat.getPageNumber()).isEqualTo(0);
        assertThat(resultat.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(resultat.getTotalElements()).isEqualTo(countAvant);

        /*
         * Vérifie que le contenu retourné correspond
         * à la première page attendue après normalisation
         * de la taille à RequetePage.TAILLE_DEFAUT.
         */
        assertThat(resultat.getContent()).hasSize(tailleAttendue);

        /*
         * Extrait les libellés des objets métier retournés dans la page :
         * - resultat.getContent() fournit la liste des SousTypeProduit
         *   contenus dans la page retournée par le service ;
         * - stream() parcourt cette liste élément par élément ;
         * - map(SousTypeProduit::getSousTypeProduit) transforme chaque
         *   SousTypeProduit en son libellé enfant ;
         * - toList() reconstruit une liste contenant uniquement
         *   les libellés retournés dans la page.
         *
         * Cette liste de libellés pourra ensuite être comparée
         * aux libellés lus directement dans le stockage.
         */
        final List<String> libellesPage = resultat.getContent().stream()
                .map(SousTypeProduit::getSousTypeProduit)
                .toList();

        /* 
         * Vérifie que le contenu retourné
         * correspond exactement au premier segment
         * des enregistrements présents dans le stockage triés.
         */
        assertThat(libellesPage)
            .containsExactlyElementsOf(libellesStockage.subList(0, tailleAttendue));

        /* 
         * Vérifie enfin le tri alphabétique du contenu retourné
         * et la cohérence des parents portés par les objets métier.
         */
        assertThat(libellesPage).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);
        assertThat(resultat.getContent())
            .allSatisfy(stp -> {
                assertThat(stp).isNotNull();
                assertThat(stp.getSousTypeProduit()).isNotBlank();
                assertThat(stp.getTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getIdTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getTypeProduit()).isNotBlank();
            });

        /* ASSERT :
         * compte finalement (en SQL)
         * le nombre d'enregistrements dans le stockage
         * pour prouver que service.rechercherTousParPage(...)
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que rechercherTousParPage(OK) sur le stockage seedé :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>retourne un contenu non null et non vide ;</li>
     * <li>respecte le numéro de page demandé ;</li>
     * <li>respecte la taille de page demandée ;</li>
     * <li>retourne un total cohérent avec l'état physique du stockage ;</li>
     * <li>retourne des objets métier réellement présents dans le stockage ;</li>
     * <li>retourne des objets métier portant chacun un parent non null
     * avec un identifiant persistant ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(OK) - retourne une page métier cohérente avec le stockage seedé")
    @Test
    public void testRechercherTousParPageNominal() throws Exception {

        /* ARRANGE :
         * lit d'abord l'état physique actuel du stockage
         * via JdbcTemplate, afin de disposer d'une référence
         * indépendante d'Hibernate.
         *
         * Prépare ensuite une requête paginée explicite,
         * sans consigne de tri,
         * afin de contrôler le scénario métier nominal
         * le plus utilisé : une page demandée avec une taille demandée.
         *
         * Le test nominal ne force pas de tri :
         * l'ordre exact est donc laissé au comportement réel
         * de la pagination sans consigne de tri.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final List<Long> idsStockage = this.jdbcTemplate.queryForList(
                "SELECT ID_SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT",
                Long.class);

        assertThat(countAvant).isNotNull().isPositive();
        assertThat(idsStockage).isNotNull().isNotEmpty();
        assertThat(idsStockage).hasSize(countAvant.intValue());

        final int pageNumber = 1;
        final int pageSize = 2;
        final int offset = pageNumber * pageSize;

        /*
         * Vérifie que le stockage seedé contient assez d'enregistrements
         * pour que la page nominale demandée contienne au moins un objet métier.
         */
        assertThat(countAvant).isGreaterThan(Long.valueOf(offset));

        final int tailleAttendue = Math.min(
                pageSize,
                countAvant.intValue() - offset);

        final RequetePage requete =
                new RequetePage(pageNumber, pageSize, new ArrayList<TriSpec>());

        /* ACT :
         * sollicite la pagination avec une RequetePage explicite
         * représentant le cas nominal métier.
         */
        final ResultatPage<SousTypeProduit> resultat =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie d'abord la cohérence générale
         * de l'enveloppe métier paginée retournée.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().isNotEmpty();
        assertThat(resultat.getPageNumber()).isEqualTo(requete.getPageNumber());
        assertThat(resultat.getPageSize()).isEqualTo(requete.getPageSize());
        assertThat(resultat.getTotalElements()).isEqualTo(countAvant);
        assertThat(resultat.getContent()).hasSize(tailleAttendue);

        /*
         * Extrait les identifiants des objets métier retournés dans la page :
         * - resultat.getContent() fournit les SousTypeProduit contenus
         *   dans la page retournée par le service ;
         * - stream() parcourt cette liste élément par élément ;
         * - map(SousTypeProduit::getIdSousTypeProduit) récupère
         *   l'identifiant persistant de chaque objet métier ;
         * - toList() reconstruit la liste des identifiants retournés.
         */
        final List<Long> idsPage = resultat.getContent().stream()
                .map(SousTypeProduit::getIdSousTypeProduit)
                .toList();

        /* Vérifie que chaque objet métier retourné
         * correspond à un identifiant physiquement présent
         * dans le stockage.
         */
        assertThat(idsPage).isNotNull().isNotEmpty();
        assertThat(idsPage).doesNotHaveDuplicates();
        assertThat(idsStockage).containsAll(idsPage);

        /* Vérifie physiquement dans le stockage
         * que chaque identifiant retourné
         * correspond à exactement un enregistrement.
         */
        for (final Long idPage : idsPage) {
            final Long countLigne = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                    Long.class,
                    idPage);

            assertThat(countLigne).isNotNull().isEqualTo(1L);
        }

        /* Vérifie enfin que chaque objet métier retourné porte :
         * - un libellé enfant exploitable ;
         * - un parent non null ;
         * - un parent avec un identifiant persistant ;
         * - un parent avec un libellé exploitable.
         */
        assertThat(resultat.getContent())
            .allSatisfy(stp -> {
                assertThat(stp).isNotNull();
                assertThat(stp.getSousTypeProduit()).isNotBlank();
                assertThat(stp.getTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getIdTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getTypeProduit()).isNotBlank();
            });

        /* ASSERT :
         * compte finalement (en SQL)
         * le nombre d'enregistrements dans le stockage
         * pour prouver que service.rechercherTousParPage(requete)
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    
    
        
    // ======================== findByObjetMetier =========================



    /**
     * <div>
     * <p>garantit que findByObjetMetier(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_NULL)
    @Test
    public void testFindByObjetMetierNull() {

    	/* ARRANGE :
         * Compte directement (en SQL) le nombre d'enregistrements 
         * dans le stockage via JdbcTemplate afin de disposer 
         * d'une preuve indépendante du contexte Hibernate.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* ACT - ASSERT :
         * garantit que service.findByObjetMetier(null)
         * - jette une ExceptionAppliParamNull
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.findByObjetMetier(null)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_LIBELLE_NULL)
    @Test
    public void testFindByObjetMetierLibelleNull() {

    	/* ARRANGE :
         * Compte directement (en SQL) le nombre d'enregistrements 
         * dans le stockage via JdbcTemplate afin de disposer 
         * d'une preuve indépendante du contexte Hibernate.
         */        
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);
        
        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* 
         * prépare un objet métier dont le libellé enfant est null,
         * avec un parent persistant.
         */
        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp = new SousTypeProduit(null, null, parent);

        /* ACT - ASSERT :
         * garantit que service.findByObjetMetier(stp)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.findByObjetMetier(stp)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_BLANK)
    @Test
    public void testFindByObjetMetierLibelleBlank() {

    	/* ARRANGE :
         * Compte directement (en SQL) le nombre d'enregistrements 
         * dans le stockage via JdbcTemplate afin de disposer 
         * d'une preuve indépendante du contexte Hibernate.
         */        
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);
        
        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* 
         * prépare un objet métier dont le libellé enfant est blank,
         * avec un parent persistant.
         */
        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp = new SousTypeProduit(null, BLANK, parent);

        /* ACT - ASSERT :
         * garantit que service.findByObjetMetier(stp)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.findByObjetMetier(stp)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_PARENT_NULL)
    @Test
    public void testFindByObjetMetierParentNull() {

    	/* ARRANGE :
         * Compte directement (en SQL) le nombre d'enregistrements 
         * dans le stockage via JdbcTemplate afin de disposer 
         * d'une preuve indépendante du contexte Hibernate.
         */ 
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();
        
        /* 
         * prépare un objet métier sans parent.
         */
        final SousTypeProduit stp =
                new SousTypeProduit(null, LIBELLE_ENFANT_VETEMENT_HOMME, null);

        /* ACT - ASSERT :
         * garantit que service.findByObjetMetier(stp)
         * - jette une ExceptionAppliParentNull
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.findByObjetMetier(stp)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_PARENT_LIBELLE_NULL)
    @Test
    public void testFindByObjetMetierParentLibelleNull() {

    	/* ARRANGE :
         * Compte directement (en SQL) le nombre d'enregistrements 
         * dans le stockage via JdbcTemplate afin de disposer 
         * d'une preuve indépendante du contexte Hibernate.
         */ 
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);
        
        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* 
         * prépare un objet métier dont le parent a un libellé null.
         */
        final TypeProduit parent = new TypeProduit(Long.valueOf(1L), null);
        final SousTypeProduit stp =
                new SousTypeProduit(null, LIBELLE_ENFANT_VETEMENT_HOMME, parent);

        /* ACT - ASSERT :
         * garantit que service.findByObjetMetier(stp)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.findByObjetMetier(stp)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_PARENT_LIBELLE_BLANK)
    @Test
    public void testFindByObjetMetierParentLibelleBlank() {

    	/* ARRANGE :
         * Compte directement (en SQL) le nombre d'enregistrements 
         * dans le stockage via JdbcTemplate afin de disposer 
         * d'une preuve indépendante du contexte Hibernate.
         */ 
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);
        
        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* 
         * prépare un objet métier dont le parent a un libellé blank.
         */
        final TypeProduit parent = new TypeProduit(Long.valueOf(1L), BLANK);
        final SousTypeProduit stp =
                new SousTypeProduit(null, LIBELLE_ENFANT_VETEMENT_HOMME, parent);

        /* ACT - ASSERT :
         * garantit que service.findByObjetMetier(stp)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.findByObjetMetier(stp)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT} ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_PARENT_NON_PERSISTANT)
    @Test
    public void testFindByObjetMetierParentIdNull() {

    	/* ARRANGE :
         * Compte directement (en SQL) le nombre d'enregistrements 
         * dans le stockage via JdbcTemplate afin de disposer 
         * d'une preuve indépendante du contexte Hibernate.
         */         
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();
        
        /* 
         * prépare un objet métier dont le parent n'est pas persistant
         * car son identifiant est null.
         */
        final TypeProduit parent = new TypeProduit(null, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp =
                new SousTypeProduit(null, LIBELLE_ENFANT_VETEMENT_HOMME, parent);

        /* ACT - ASSERT :
         * garantit que service.findByObjetMetier(stp)
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet le préfixe contractuel suivi du libellé du parent 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(
                    SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT
                            + LIBELLE_PARENT_VETEMENT);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.findByObjetMetier(stp)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent absent dans le stockage) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT} ;</li>
     * <li>prouve que le parent est absent du stockage avant l'appel ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(parent absent) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)")
    @Test
    public void testFindByObjetMetierParentAbsent() {

    	/* ARRANGE :
         * Compte directement (en SQL) le nombre d'enregistrements 
         * dans le stockage via JdbcTemplate afin de disposer 
         * d'une preuve indépendante du contexte Hibernate.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* 
         * Vérifie directement dans le stockage
         * qu'aucun parent ne porte l'identifiant ID_INEXISTANT.
         */
        final Long countParentStockage = this.jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM TYPES_PRODUIT WHERE ID_TYPE_PRODUIT = ?",
                Long.class,
                ID_INEXISTANT);

        assertThat(countParentStockage).isNotNull().isZero();
        
        /* 
         * prépare un objet métier dont le parent 
         * porte un identifiant inexistant afin de vérifier 
         * que service.findByObjetMetier(...) jettera 
         * une ExceptionTechniqueGatewayNonPersistent.
         */
        final TypeProduit parent = new TypeProduit(ID_INEXISTANT, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp =
                new SousTypeProduit(null, LIBELLE_ENFANT_VETEMENT_HOMME, parent);

        /* ACT - ASSERT :
         * garantit que service.findByObjetMetier(stp)
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT 
         * + LIBELLE_PARENT_VETEMENT
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(
                    SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT
                            + LIBELLE_PARENT_VETEMENT);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel
         * afin de prouver que l'appel service.findByObjetMetier(stp)
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que findByObjetMetier(non trouvé
     * sous un parent persistant) :</p>
     * <ul>
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
         * Compte directement (en SQL) le nombre d'enregistrements 
         * dans le stockage via JdbcTemplate afin de disposer 
         * d'une preuve indépendante du contexte Hibernate.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();
        
        /* 
         * prépare un objet métier portant un parent persistant
         * mais un libellé enfant inexistant sous ce parent.
         */
        final Long idParent 
        	= retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);

        final Long countCoupleAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE_AND_PARENT,
                Long.class,
                LIBELLE_INEXISTANT,
                idParent);
        
        /* 
         * Assure que l'objet métier avec LIBELLE_INEXISTANT 
         * n'existe pas dans le stockage. */
        assertThat(countCoupleAvant).isNotNull().isZero();

        final SousTypeProduit probe = new SousTypeProduit(
                null,
                LIBELLE_INEXISTANT,
                new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT));

        /* Neutralise explicitement le contexte Hibernate
         * avant l'appel service.findByObjetMetier(...)
         * afin d'éviter tout raisonnement biaisé par le cache.
         */
        this.entityManager.clear();

        /* ACT :
         * appelle service.findByObjetMetier(...)
         * sur un objet métier absent du stockage.
         */
        final SousTypeProduit resultat = this.service.findByObjetMetier(probe);

        /* ASSERT :
         * garantit que service.findByObjetMetier(...) retourne null
         * lorsque l'objet métier recherché n'existe pas dans le stockage.
         */
        assertThat(resultat).isNull();

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'appel de lecture
         * afin de prouver que service.findByObjetMetier(...)
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que findByObjetMetier(OK) :</p>
     * <ul>
     * <li>retourne l'objet métier correspondant à l'objet métier passé en paramètre ;</li>
     * <li>retourne l'identifiant de l'enregistrement attendu dans le stockage ;</li>
     * <li>retourne le bon libellé enfant et le bon parent ;</li>
     * <li>n'altère pas le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_TROUVE)
    @Test
    public void testFindByObjetMetierNominal() throws Exception {

        /* ARRANGE :
         * lit d'abord (en SQL) directement dans le stockage
         * pour retrouver le parent persistant
         * puis l'identifiant exact de l'objet métier attendu.
         *
         * Cette référence SQL sert ensuite à comparer 
         * le résultat de service.findByObjetMetier(...)
         * à l'enregistrement attendu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /* recherche (en SQL) un objet métier de test dans le stockage. */
        final Long idParent 
        	= retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);

        final Long countCoupleAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE_AND_PARENT,
                Long.class,
                LIBELLE_ENFANT_VETEMENT_HOMME,
                idParent);

        final Long idTrouveEnBase = this.jdbcTemplate.queryForObject(
                SELECT_PARAM_ID_FROM_STP_WHERE_LIBELLE_AND_PARENT,
                Long.class,
                LIBELLE_ENFANT_VETEMENT_HOMME,
                idParent);

        assertThat(countCoupleAvant).isEqualTo(1L);
        assertThat(idTrouveEnBase).isNotNull();

        final SousTypeProduit probe = new SousTypeProduit(
                null,
                LIBELLE_ENFANT_VETEMENT_HOMME,
                new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT));

        /* 
         * Neutralise explicitement le contexte Hibernate
         * avant la lecture via service.findByObjetMetier(...)
         * afin d'éviter des effets indésirables du cache Hibernate.
         */
        this.entityManager.clear();

        /* ACT :
         * appelle service.findByObjetMetier(...)
         * sur un objet métier présent dans le stockage.
         */
        final SousTypeProduit trouve = this.service.findByObjetMetier(probe);

        /* ASSERT :
         * vérifie d'abord que le service retourne bien
         * un objet métier non null.
         */
        assertThat(trouve).isNotNull();

        /* Vérifie ensuite que l'identifiant retourné,
         * le libellé enfant et le parent
         * correspondent exactement à l'enregistrement attendu dans le stockage.
         */
        assertThat(trouve.getIdSousTypeProduit()).isEqualTo(idTrouveEnBase);
        assertThat(trouve.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);
        assertThat(trouve.getTypeProduit()).isNotNull();
        assertThat(trouve.getTypeProduit().getIdTypeProduit()).isEqualTo(idParent);
        assertThat(trouve.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_VETEMENT);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'appel de lecture
         * afin de prouver que service.findByObjetMetier(...)
         * n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isNotNull().isNotZero();
        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    
    
      
    /**
     * <div>
     * <p>Test didactique non contractuel.</p>
     * <p>garantit que findByObjetMetier(id ignoré et casse ignorée) :</p>
     * <ul>
     * <li>ignore l'identifiant porté par l'objet métier passé en paramètre ;</li>
     * <li>effectue la recherche sur le parent TypeProduit
     * et le libellé de l'objet métier ;</li>
     * <li>reste insensible à la casse du libellé
     * de l'objet métier passé en paramètre ;</li>
     * <li>retourne dans les deux variantes le même objet métier persistant ;</li>
     * <li>retourne un objet métier complet avec son parent TypeProduit ;</li>
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
         * compte directement (en SQL) le nombre d'enregistrements
         * dans le stockage via JdbcTemplate afin de disposer
         * d'une preuve indépendante du contexte Hibernate.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countAvant).isNotNull().isNotZero();

        /*
         * Retrouve l'identifiant persistant du parent TypeProduit
         * utilisé par l'objet métier seedé de référence.
         *
         * Pour SousTypeProduit, la clé métier recherchée par
         * findByObjetMetier(...) est :
         * - parent TypeProduit ;
         * - libellé de l'objet métier.
         */
        final Long idParent =
                retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);

        /*
         * Vérifie directement dans le stockage que le couple métier
         * parent TypeProduit + libellé objet métier existe exactement
         * une fois avant l'appel du service.
         */
        final Long countCoupleAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE_AND_PARENT,
                Long.class,
                LIBELLE_ENFANT_VETEMENT_HOMME,
                idParent);

        /*
         * Relit l'identifiant réellement stocké pour l'objet métier
         * attendu. Cet identifiant servira ensuite à prouver que
         * l'identifiant fourni par la sonde est ignoré.
         */
        final Long idTrouveStockage = this.jdbcTemplate.queryForObject(
                SELECT_PARAM_ID_FROM_STP_WHERE_LIBELLE_AND_PARENT,
                Long.class,
                LIBELLE_ENFANT_VETEMENT_HOMME,
                idParent);

        /*
         * Vérifie que l'objet métier de test existe exactement une fois
         * dans le stockage avant l'appel de lecture.
         */
        assertThat(countCoupleAvant).isNotNull().isEqualTo(1L);
        assertThat(idTrouveStockage).isNotNull();

        /*
         * Prépare le parent TypeProduit persistant de la sonde.
         */
        final TypeProduit parent =
                new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);

        /*
         * Prépare une première sonde équivalente à l'objet métier attendu,
         * mais avec un identifiant volontairement faux.
         *
         * Le test prouve ainsi que findByObjetMetier(...)
         * ignore l'identifiant porté par la sonde et recherche bien
         * par clé métier.
         */
        final SousTypeProduit avecIdFaux = new SousTypeProduit(
                ID_INEXISTANT,
                LIBELLE_ENFANT_VETEMENT_HOMME,
                parent);

        /*
         * Prépare une seconde sonde équivalente à l'objet métier attendu,
         * mais avec une casse différente sur le libellé.
         *
         * Le test prouve ainsi que findByObjetMetier(...)
         * recherche le libellé de l'objet métier sans tenir compte
         * de la casse.
         */
        final SousTypeProduit casse = new SousTypeProduit(
                null,
                LIBELLE_ENFANT_VETEMENT_HOMME.toUpperCase(LOCALE_DEFAUT),
                parent);

        /*
         * Neutralise explicitement le contexte Hibernate
         * avant la lecture via service.findByObjetMetier(...)
         * afin d'éviter tout raisonnement biaisé par le cache.
         */
        this.entityManager.clear();

        /* ACT :
         * appelle service.findByObjetMetier(...)
         * dans les deux variantes métier à contrôler :
         * - ID fourni ignoré ;
         * - casse du libellé ignorée.
         */
        final SousTypeProduit retourAvecIdFaux =
                this.service.findByObjetMetier(avecIdFaux);

        final SousTypeProduit retourCasse =
                this.service.findByObjetMetier(casse);

        /* ASSERT :
         * vérifie d'abord que les deux recherches aboutissent.
         */
        assertThat(retourAvecIdFaux).isNotNull();
        assertThat(retourCasse).isNotNull();

        /*
         * Vérifie que l'identifiant fourni par l'appelant
         * n'influence pas la recherche : le service retourne
         * l'identifiant réellement présent dans le stockage.
         */
        assertThat(retourAvecIdFaux.getIdSousTypeProduit())
            .isEqualTo(idTrouveStockage);
        assertThat(retourAvecIdFaux.getSousTypeProduit())
            .isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);
        assertThat(retourAvecIdFaux.getTypeProduit()).isNotNull();
        assertThat(retourAvecIdFaux.getTypeProduit().getIdTypeProduit())
            .isEqualTo(idParent);
        assertThat(retourAvecIdFaux.getTypeProduit().getTypeProduit())
            .isEqualTo(LIBELLE_PARENT_VETEMENT);

        /*
         * Vérifie que la recherche insensible à la casse retrouve
         * exactement le même objet métier persistant.
         */
        assertThat(retourCasse.getIdSousTypeProduit())
            .isEqualTo(idTrouveStockage);
        assertThat(retourCasse.getSousTypeProduit())
            .isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);
        assertThat(retourCasse.getTypeProduit()).isNotNull();
        assertThat(retourCasse.getTypeProduit().getIdTypeProduit())
            .isEqualTo(idParent);
        assertThat(retourCasse.getTypeProduit().getTypeProduit())
            .isEqualTo(LIBELLE_PARENT_VETEMENT);

        /*
         * Vérifie que les deux variantes retournent bien
         * le même objet métier persistant.
         */
        assertThat(retourCasse.getIdSousTypeProduit())
            .isEqualTo(retourAvecIdFaux.getIdSousTypeProduit());

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'appel de lecture afin de prouver que
         * service.findByObjetMetier(...) n'a pas touché au stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
   * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK} ;</li>
   * <li>n'altère pas le stockage.</li>
   * </ul>
   * </div>
   */
  @Tag(TAG_RECHERCHER)
  @DisplayName(DN_FINDBYLIBELLE_NULL)
  @Test
  public void testFindByLibelleNull() {

      /* ARRANGE :
       * compte d'abord (en SQL) le nombre d'enregistrements 
       * dans le stockage afin de pouvoir prouver ensuite
       * que service.findByLibelle(...) ne produit aucune écriture 
       * dans le stockage.
       */
      final Long countAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      /* ACT - ASSERT :
       * garantit que service.findByLibelle(null)
       * - jette une ExceptionAppliLibelleBlank
       * - émet le message MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK 
       * (message contractuel).
       */
      assertThatThrownBy(() -> this.service.findByLibelle(null))
          .isInstanceOf(ExceptionAppliLibelleBlank.class)
          .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK);

      /* ASSERT :
       * compte finalement (en SQL) le nombre d'enregistrements 
       * dans le stockage pour prouver que service.findByLibelle(...) 
       * n'a pas touché au stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
   * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK} ;</li>
   * <li>n'altère pas le stockage.</li>
   * </ul>
   * </div>
   */
  @Tag(TAG_RECHERCHER)
  @DisplayName(DN_FINDBYLIBELLE_BLANK)
  @Test
  public void testFindByLibelleBlank() {

      /* ARRANGE :
       * compte d'abord (en SQL) le nombre d'enregistrements 
       * dans le stockage afin de pouvoir prouver ensuite
       * que service.findByLibelle(...) ne produit aucune écriture 
       * dans le stockage.
       */
      final Long countAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      /* ACT - ASSERT :
       * garantit que service.findByLibelle(BLANK)
       * - jette une ExceptionAppliLibelleBlank
       * - émet le message MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK 
       * (message contractuel).
       */
      assertThatThrownBy(() -> this.service.findByLibelle(BLANK))
          .isInstanceOf(ExceptionAppliLibelleBlank.class)
          .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK);

      /* ASSERT :
       * compte finalement (en SQL) le nombre d'enregistrements 
       * dans le stockage pour prouver que service.findByLibelle(...) 
       * n'a pas touché au stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      assertThat(countApres).isNotNull().isNotZero();
      assertThat(countApres).isEqualTo(countAvant);

  } // __________________________________________________________________
  


  /**
   * <div>
   * <p>garantit que findByLibelle(non trouvé) :</p>
   * <ul>
   * <li>retourne une liste non null et vide ;</li>
   * <li>reste cohérent avec l'absence physique de ligne correspondante
   * dans le stockage ;</li>
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
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      /* 
       * Vérifie par SQL direct qu'aucun objet métier ne porte
       * le libellé LIBELLE_INEXISTANT dans le stockage,
       * sans tenir compte des majuscules/minuscules.
       */
      final Long countCorrespondancesStockage = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE,
              Long.class,
              LIBELLE_INEXISTANT);
      
      /* Assure que countCorrespondancesStockage
       * n'est pas null mais vaut zéro. */
      assertThat(countCorrespondancesStockage).isNotNull().isZero();

      /* Neutralise explicitement le contexte Hibernate
       * avant l'appel de lecture,
       * afin d'éviter tout raisonnement biaisé par le cache.
       */
      this.entityManager.clear();

      /* ACT :
       * sollicite service.findByLibelle(...)
       * avec le libellé LIBELLE_INEXISTANT,
       * absent du stockage (prouvé par SQL).
       */
      final List<SousTypeProduit> liste 
      	= this.service.findByLibelle(LIBELLE_INEXISTANT);

      /* ASSERT :
       * vérifie que le service retourne une liste vide (pas null)
       * lors de l'appel service.findByLibelle(...) 
       * avec un libellé non trouvé dans le stockage.
       */
      assertThat(liste).isNotNull().isEmpty();

      /* 
       * compte finalement (en SQL) le nombre d'enregistrements 
       * dans le stockage pour prouver que service.findByLibelle(...) 
       * n'a pas touché au stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      assertThat(countApres).isNotNull().isNotZero();
      assertThat(countApres).isEqualTo(countAvant);

  } // __________________________________________________________________
  


  /**
   * <div>
   * <p>garantit que findByLibelle(case-insensitive) :</p>
   * <ul>
   * <li>retourne une liste non null et non vide ;</li>
   * <li>retrouve les objets métier même lorsque le libellé recherché
   * est fourni avec une casse différente de celle du stockage ;</li>
   * <li>retourne exactement les identifiants présents dans le stockage
   * pour ce libellé ;</li>
   * <li>retourne uniquement le libellé réellement stocké ;</li>
   * <li>retourne des objets métier dont le parent correspond au stockage ;</li>
   * <li>n'altère pas le stockage.</li>
   * </ul>
   * </div>
   *
   * @throws Exception
   */
  @Tag(TAG_RECHERCHER)
  @DisplayName("findByLibelle(case-insensitive) - retrouve le libellé malgré une casse différente")
  @Test
  public void testFindByLibelleCaseInsensitive() throws Exception {

      /* ARRANGE :
       * compte d'abord (en SQL) le nombre d'enregistrements 
       * dans le stockage afin de pouvoir prouver ensuite
       * que service.findByLibelle(...) ne produit aucune écriture 
       * dans le stockage.
       */
      final Long countAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
              LIBELLE_ENFANT_VETEMENT_HOMME.toUpperCase(LOCALE_DEFAUT);

      /* 
       * Recherche (en SQL) dans le stockage tous les objets métier
       * ayant pour libellé LIBELLE_ENFANT_VETEMENT_HOMME,
       * sans tenir compte des majuscules/minuscules.
       */
      final Long countCorrespondancesStockage = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE,
              Long.class,
              libelleMajuscule);

      final List<Long> idsStockage = this.jdbcTemplate.queryForList(
              SELECT_PARAM_IDS_FROM_STP_WHERE_LIBELLE,
              Long.class,
              libelleMajuscule);

      /* 
       * vérifie que des objets métier avec LIBELLE_ENFANT_VETEMENT_HOMME 
       * existent dans le stockage.
       */
      assertThat(countCorrespondancesStockage).isNotNull().isPositive();
      assertThat(idsStockage).isNotNull().isNotEmpty();

      /* 
       * Neutralise explicitement le contexte Hibernate
       * avant l'appel de lecture afin d'éviter tout 
       * raisonnement biaisé par le cache.
       */
      this.entityManager.clear();

      /* ACT :
       * sollicite service.findByLibelle(...)
       * avec le libellé en majuscules.
       */
      final List<SousTypeProduit> liste =
              this.service.findByLibelle(libelleMajuscule);

      /* ASSERT :
       * vérifie d'abord que la méthode retourne
       * une liste non null et non vide 
       * de même taille que la liste trouvée par SQL.
       */
      assertThat(liste).isNotNull().isNotEmpty();
      assertThat(liste).hasSize(countCorrespondancesStockage.intValue());

      final List<Long> idsRetournes = liste.stream()
              .map(SousTypeProduit::getIdSousTypeProduit)
              .sorted()
              .toList();

      /* 
       * Vérifie ensuite que les identifiants renvoyés par le service
       * correspondent exactement aux identifiants présents dans le stockage
       * pour le libellé recherché, malgré la casse différente.
       */
      assertThat(idsRetournes).containsExactlyElementsOf(idsStockage);

      /* Vérifie aussi que tous les objets métier retournés
       * portent bien le libellé réellement stocké
       * et un parent cohérent avec le stockage.
       */
      assertThat(liste)
          .allSatisfy(stp -> {
              assertThat(stp).isNotNull();
              assertThat(stp.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);
              assertThat(stp.getTypeProduit()).isNotNull();

              final Long idParentStockage = this.jdbcTemplate.queryForObject(
                      SELECT_TP_FROM_STP_WHERE_ID,
                      Long.class,
                      stp.getIdSousTypeProduit());

              assertThat(idParentStockage).isNotNull();
              assertThat(stp.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentStockage);

              final String libelleParentStockage = this.jdbcTemplate.queryForObject(
                      SELECT_PARAM_TP_FROM_TP_WHERE_ID,
                      String.class,
                      idParentStockage);

              assertThat(libelleParentStockage).isNotBlank();
              assertThat(stp.getTypeProduit().getTypeProduit()).isEqualTo(libelleParentStockage);
          });

      /* 
       * compte finalement (en SQL) le nombre d'enregistrements 
       * dans le stockage pour prouver que service.findByLibelle(...) 
       * n'a pas touché au stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      assertThat(countApres).isNotNull().isNotZero();
      assertThat(countApres).isEqualTo(countAvant);

  } // __________________________________________________________________
  


  /**
   * <div>
   * <p>garantit que findByLibelle(OK) :</p>
   * <ul>
   * <li>retourne une liste non null et non vide ;</li>
   * <li>retourne exactement les identifiants présents dans le stockage
   * pour ce libellé ;</li>
   * <li>retourne uniquement le libellé recherché ;</li>
   * <li>retourne des objets métier dont le parent correspond au stockage ;</li>
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
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      /* 
       * Recherche (en SQL) dans le stockage tous les objets métier
       * ayant pour libellé LIBELLE_ENFANT_VETEMENT_HOMME.
       */
      final Long countCorrespondancesStockage = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE,
              Long.class,
              LIBELLE_ENFANT_VETEMENT_HOMME);

      final List<Long> idsStockage = this.jdbcTemplate.queryForList(
              SELECT_PARAM_IDS_FROM_STP_WHERE_LIBELLE,
              Long.class,
              LIBELLE_ENFANT_VETEMENT_HOMME);

      /* 
       * vérifie que des objets métier avec LIBELLE_ENFANT_VETEMENT_HOMME 
       * existent dans le stockage.
       */
      assertThat(countCorrespondancesStockage).isNotNull().isPositive();
      assertThat(idsStockage).isNotNull().isNotEmpty();

      /* 
       * Neutralise explicitement le contexte Hibernate
       * avant l'appel de lecture afin d'éviter tout 
       * raisonnement biaisé par le cache.
       */
      this.entityManager.clear();

      /* ACT :
       * sollicite service.findByLibelle(...)
       * avec le libellé LIBELLE_ENFANT_VETEMENT_HOMME.
       */
      final List<SousTypeProduit> liste =
              this.service.findByLibelle(LIBELLE_ENFANT_VETEMENT_HOMME);

      /* ASSERT :
       * vérifie d'abord que la méthode retourne
       * une liste non null et non vide 
       * de même taille que la liste trouvée par SQL.
       */
      assertThat(liste).isNotNull().isNotEmpty();
      assertThat(liste).hasSize(countCorrespondancesStockage.intValue());

      final List<Long> idsRetournes = liste.stream()
              .map(SousTypeProduit::getIdSousTypeProduit)
              .sorted()
              .toList();

      /* 
       * Vérifie ensuite que les identifiants renvoyés par le service
       * correspondent exactement aux identifiants présents dans le stockage
       * pour le libellé LIBELLE_ENFANT_VETEMENT_HOMME.
       */
      assertThat(idsRetournes).containsExactlyElementsOf(idsStockage);

      /* Vérifie aussi que tous les objets métier retournés
       * portent bien le libellé recherché
       * et un parent cohérent avec le stockage.
       */
      assertThat(liste)
          .allSatisfy(stp -> {
              assertThat(stp).isNotNull();
              assertThat(stp.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);
              assertThat(stp.getTypeProduit()).isNotNull();

              final Long idParentStockage = this.jdbcTemplate.queryForObject(
                      SELECT_TP_FROM_STP_WHERE_ID,
                      Long.class,
                      stp.getIdSousTypeProduit());

              assertThat(idParentStockage).isNotNull();
              assertThat(stp.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentStockage);

              final String libelleParentStockage = this.jdbcTemplate.queryForObject(
                      SELECT_PARAM_TP_FROM_TP_WHERE_ID,
                      String.class,
                      idParentStockage);

              assertThat(libelleParentStockage).isNotBlank();
              assertThat(stp.getTypeProduit().getTypeProduit()).isEqualTo(libelleParentStockage);
          });

      /* 
       * compte finalement (en SQL) le nombre d'enregistrements 
       * dans le stockage pour prouver que service.findByLibelle(...) 
       * n'a pas touché au stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
  * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL} ;</li>
  * <li>n'altère pas le stockage.</li>
  * </ul>
  * </div>
  */
 @Tag(TAG_RECHERCHER_RAPIDE)
 @DisplayName(DN_FINDBYLIBELLERAPIDE_NULL)
 @Test
 public void testFindByLibelleRapideNull() {

 	/* ARRANGE :
      * compte d'abord (en SQL) le nombre d'enregistrements 
      * dans le stockage afin de pouvoir prouver ensuite
      * que service.findByLibelleRapide(...) ne produit aucune écriture 
      * dans le stockage.
      */
     final Long countAvant = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
             Long.class);

     /* vérifie que le stockage n'est pas vide. */
     assertThat(countAvant).isNotNull().isNotZero();

     /* ACT - ASSERT :
      * garantit que service.findByLibelleRapide(null)
      * - jette une ExceptionAppliParamNull
      * - émet le message MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL
      * (message contractuel).
      */
     assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
         .isInstanceOf(ExceptionAppliParamNull.class)
         .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL);

     /* ASSERT :
      * compte finalement (en SQL) le nombre d'enregistrements 
      * dans le stockage pour prouver que service.findByLibelleRapide(...) 
      * n'a pas touché au stockage.
      */
     final Long countApres = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
             Long.class);

     /* vérifie que le stockage n'est pas vide. */
     assertThat(countAvant).isNotNull().isNotZero();

     /* Neutralise explicitement le contexte Hibernate
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
     final List<SousTypeProduit> tous = this.service.rechercherTous();
     final List<SousTypeProduit> rapide = this.service.findByLibelleRapide(BLANK);

     /* ASSERT :
      * vérifie que service.findByLibelleRapide(BLANK)
      * retourne exactement la même liste que service.rechercherTous().
      */
     assertThat(tous).isNotNull().hasSize(countAvant.intValue());
     assertThat(rapide).isNotNull().hasSize(countAvant.intValue());

     final List<Long> idsTous = tous.stream()
             .map(SousTypeProduit::getIdSousTypeProduit)
             .toList();

     final List<Long> idsRapide = rapide.stream()
             .map(SousTypeProduit::getIdSousTypeProduit)
             .toList();

     assertThat(idsRapide).containsExactlyElementsOf(idsTous);
     assertThat(idsRapide).doesNotHaveDuplicates();

     /* Vérifie aussi que tous les objets métier retournés
      * portent un libellé exploitable et un parent cohérent.
      */
     assertThat(rapide)
         .allSatisfy(stp -> {
             assertThat(stp).isNotNull();
             assertThat(stp.getSousTypeProduit()).isNotBlank();
             assertThat(stp.getTypeProduit()).isNotNull();
             assertThat(stp.getTypeProduit().getIdTypeProduit()).isNotNull();
             assertThat(stp.getTypeProduit().getTypeProduit()).isNotBlank();
         });

     /* 
      * compte finalement (en SQL) le nombre d'enregistrements 
      * dans le stockage pour prouver que service.findByLibelleRapide(...) 
      * n'a pas touché au stockage.
      */
     final Long countApres = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
             Long.class);

     assertThat(countApres).isNotNull().isNotZero();
     assertThat(countApres).isEqualTo(countAvant);

 } // __________________________________________________________________
 


 /**
  * <div>
  * <p>garantit que findByLibelleRapide(non trouvé) :</p>
  * <ul>
  * <li>retourne une liste non null et vide ;</li>
  * <li>reste cohérent avec l'absence physique de ligne correspondante
  * dans le stockage ;</li>
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
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
             Long.class);

     /* vérifie que le stockage n'est pas vide. */
     assertThat(countAvant).isNotNull().isNotZero();

     /* 
      * vérifie (en SQL)
      * qu'aucun enregistrement ne contient CONTENU_PARTIEL_INEXISTANT,
      * sans tenir compte des majuscules/minuscules.
      */
     final Long countStockage = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE_LIKE,
             Long.class,
             "%" + CONTENU_PARTIEL_INEXISTANT + "%");

     /* Assure que countStockage 
      * n'est pas null mais vaut zéro. */
     assertThat(countStockage).isNotNull().isZero();

     /* Neutralise explicitement le contexte Hibernate
      * avant l'appel de lecture,
      * afin d'éviter tout raisonnement biaisé par le cache.
      */
     this.entityManager.clear();

     /* ACT :
      * sollicite service.findByLibelleRapide(...)
      * avec le contenu CONTENU_PARTIEL_INEXISTANT,
      * absent du stockage (prouvé par SQL).
      */
     final List<SousTypeProduit> liste =
             this.service.findByLibelleRapide(CONTENU_PARTIEL_INEXISTANT);

     /* ASSERT :
      * vérifie que le service retourne une liste vide (pas null)
      * lors de l'appel service.findByLibelleRapide(...) 
      * avec un contenu non trouvé dans le stockage.
      */
     assertThat(liste).isNotNull().isEmpty();

     /* 
      * compte finalement (en SQL) le nombre d'enregistrements 
      * dans le stockage pour prouver que service.findByLibelleRapide(...) 
      * n'a pas touché au stockage.
      */
     final Long countApres = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
             Long.class);

     assertThat(countApres).isNotNull().isNotZero();
     assertThat(countApres).isEqualTo(countAvant);

 } // __________________________________________________________________
 


 /**
  * <div>
  * <p>garantit que findByLibelleRapide(case-insensitive) :</p>
  * <ul>
  * <li>reste insensible à la casse du contenu recherché ;</li>
  * <li>retourne le même contenu quelle que soit la casse du motif ;</li>
  * <li>retourne exactement les identifiants présents dans le stockage
  * pour ce motif ;</li>
  * <li>retourne un contenu trié selon l'ordre métier parent puis libellé ;</li>
  * <li>retourne un contenu sans doublon ;</li>
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
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
             Long.class);

     /* vérifie que le stockage n'est pas vide. */
     assertThat(countAvant).isNotNull().isNotZero();

     final String contenuMajuscule = CONTENU_PARTIEL_VET.toUpperCase(LOCALE_DEFAUT);

     /* 
      * Recherche (en SQL) dans le stockage tous les objets métier
      * dont le libellé contient CONTENU_PARTIEL_VET,
      * sans tenir compte des majuscules/minuscules,
      * et dans l'ordre métier réellement attendu :
      * parent puis libellé de l'objet métier.
      */
     final Long countStockage = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE_LIKE,
             Long.class,
             "%" + CONTENU_PARTIEL_VET + "%");

     final List<Long> idsStockage = this.jdbcTemplate.queryForList(
             "SELECT STP.ID_SOUS_TYPE_PRODUIT " // NOPMD by danyl on 06/05/2026 13:15
                     + "FROM SOUS_TYPES_PRODUIT STP " // NOPMD by danyl on 06/05/2026 13:15
                     + "INNER JOIN TYPES_PRODUIT TP " // NOPMD by danyl on 06/05/2026 13:15
                     + "ON STP.TYPE_PRODUIT = TP.ID_TYPE_PRODUIT " // NOPMD by danyl on 06/05/2026 13:15
                     + "WHERE UPPER(STP.SOUS_TYPE_PRODUIT) LIKE UPPER(?) "
                     + "ORDER BY UPPER(TP.TYPE_PRODUIT) ASC, " // NOPMD by danyl on 06/05/2026 13:15
                     + "UPPER(STP.SOUS_TYPE_PRODUIT) ASC", // NOPMD by danyl on 06/05/2026 13:15
             Long.class,
             "%" + CONTENU_PARTIEL_VET + "%");

     /* 
      * vérifie que des objets métier contenant CONTENU_PARTIEL_VET
      * existent dans le stockage.
      */
     assertThat(countStockage).isNotNull().isPositive();
     assertThat(idsStockage).isNotNull().isNotEmpty();
     assertThat(idsStockage).hasSize(countStockage.intValue());

     /* Neutralise explicitement le contexte Hibernate
      * avant les appels de lecture,
      * afin d'éviter tout raisonnement biaisé par le cache.
      */
     this.entityManager.clear();

     /* ACT :
      * sollicite deux fois la recherche rapide
      * avec deux casses différentes du même motif.
      */
     final List<SousTypeProduit> retourMin =
             this.service.findByLibelleRapide(CONTENU_PARTIEL_VET);

     final List<SousTypeProduit> retourMaj =
             this.service.findByLibelleRapide(contenuMajuscule);

     /* ASSERT :
      * vérifie que les deux recherches aboutissent
      * et retournent le même nombre d'objets métier
      * que le stockage interrogé en SQL.
      */
     assertThat(retourMin).isNotNull().isNotEmpty();
     assertThat(retourMaj).isNotNull().isNotEmpty();
     assertThat(retourMin).hasSize(countStockage.intValue());
     assertThat(retourMaj).hasSize(countStockage.intValue());

     final List<Long> idsRetourMin = retourMin.stream()
             .map(SousTypeProduit::getIdSousTypeProduit)
             .toList();

     final List<Long> idsRetourMaj = retourMaj.stream()
             .map(SousTypeProduit::getIdSousTypeProduit)
             .toList();

     /* Vérifie que les deux résultats
      * correspondent exactement à la même référence physique
      * et dans le même ordre métier.
      */
     assertThat(idsRetourMin).containsExactlyElementsOf(idsStockage);
     assertThat(idsRetourMaj).containsExactlyElementsOf(idsStockage);
     assertThat(idsRetourMin).containsExactlyElementsOf(idsRetourMaj);

     /* Vérifie enfin que la recherche est bien insensible à la casse,
      * avec des résultats sans doublon.
      */
     assertThat(idsRetourMin).doesNotHaveDuplicates();
     assertThat(idsRetourMaj).doesNotHaveDuplicates();

     /* Vérifie aussi que tous les objets métier retournés
      * portent un libellé contenant le motif recherché
      * et un parent cohérent avec le stockage.
      */
     assertThat(retourMin)
         .allSatisfy(stp -> {
             assertThat(stp).isNotNull();
             assertThat(stp.getSousTypeProduit()).isNotBlank();
             assertThat(stp.getSousTypeProduit().toUpperCase(LOCALE_DEFAUT))
                 .contains(CONTENU_PARTIEL_VET.toUpperCase(LOCALE_DEFAUT));
             assertThat(stp.getTypeProduit()).isNotNull();

             final Long idParentStockage = this.jdbcTemplate.queryForObject(
                     SELECT_TP_FROM_STP_WHERE_ID,
                     Long.class,
                     stp.getIdSousTypeProduit());

             assertThat(idParentStockage).isNotNull();
             assertThat(stp.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentStockage);

             final String libelleParentStockage = this.jdbcTemplate.queryForObject(
                     SELECT_PARAM_TP_FROM_TP_WHERE_ID,
                     String.class,
                     idParentStockage);

             assertThat(libelleParentStockage).isNotBlank();
             assertThat(stp.getTypeProduit().getTypeProduit()).isEqualTo(libelleParentStockage);
         });

     /* 
      * compte finalement (en SQL) le nombre d'enregistrements 
      * dans le stockage pour prouver que service.findByLibelleRapide(...) 
      * n'a pas touché au stockage.
      */
     final Long countApres = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
  * <li>ne retourne pas deux fois le même couple métier parent/libellé ;</li>
  * <li>retourne un contenu trié selon l'ordre métier parent puis libellé ;</li>
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
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
             Long.class);

     /* vérifie que le stockage n'est pas vide. */
     assertThat(countAvant).isNotNull().isNotZero();

     /* 
      * Prépare une recherche dont le motif correspond
      * à plusieurs lignes seedées.
      */
     final Long countStockage = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE_LIKE,
             Long.class,
             "%" + CONTENU_PARTIEL_VET + "%");

     final List<Long> idsStockage = this.jdbcTemplate.queryForList(
             "SELECT STP.ID_SOUS_TYPE_PRODUIT "
                     + "FROM SOUS_TYPES_PRODUIT STP "
                     + "INNER JOIN TYPES_PRODUIT TP "
                     + "ON STP.TYPE_PRODUIT = TP.ID_TYPE_PRODUIT "
                     + "WHERE UPPER(STP.SOUS_TYPE_PRODUIT) LIKE UPPER(?) "
                     + "ORDER BY UPPER(TP.TYPE_PRODUIT) ASC, "
                     + "UPPER(STP.SOUS_TYPE_PRODUIT) ASC",
             Long.class,
             "%" + CONTENU_PARTIEL_VET + "%");

     assertThat(countStockage).isNotNull().isPositive();
     assertThat(idsStockage).isNotNull().isNotEmpty();
     assertThat(idsStockage).hasSize(countStockage.intValue());

     /* Neutralise explicitement le contexte Hibernate
      * avant l'appel de lecture,
      * afin d'éviter tout raisonnement biaisé par le cache.
      */
     this.entityManager.clear();

     /* ACT :
      * sollicite service.findByLibelleRapide(...)
      * avec un motif correspondant à plusieurs objets métier.
      */
     final List<SousTypeProduit> retour =
             this.service.findByLibelleRapide(CONTENU_PARTIEL_VET);

     /* ASSERT :
      * vérifie que la recherche retourne une liste exploitable.
      */
     assertThat(retour).isNotNull().isNotEmpty();
     assertThat(retour).hasSize(countStockage.intValue());

     final List<Long> idsRetournes = retour.stream()
             .map(SousTypeProduit::getIdSousTypeProduit)
             .toList();

     /* Vérifie que le service retourne les identifiants attendus
      * dans l'ordre métier parent puis libellé.
      */
     assertThat(idsRetournes).containsExactlyElementsOf(idsStockage);

     /* Vérifie qu'aucun identifiant persistant
      * n'est retourné deux fois.
      */
     assertThat(idsRetournes).doesNotHaveDuplicates();

     /* Vérifie chaque objet métier avant de construire
      * la clé métier de dédoublonnage.
      */
     assertThat(retour)
         .allSatisfy(stp -> {
             assertThat(stp).isNotNull();
             assertThat(stp.getIdSousTypeProduit()).isNotNull();
             assertThat(stp.getSousTypeProduit()).isNotBlank();
             assertThat(stp.getTypeProduit()).isNotNull();
             assertThat(stp.getTypeProduit().getIdTypeProduit()).isNotNull();
             assertThat(stp.getTypeProduit().getTypeProduit()).isNotBlank();
         });

     final List<String> clesMetier = retour.stream()
             .map(stp -> stp.getTypeProduit().getIdTypeProduit()
                     + "|"
                     + stp.getSousTypeProduit().toUpperCase(LOCALE_DEFAUT))
             .toList();

     /* Vérifie qu'aucun couple métier parent/libellé
      * n'est retourné deux fois.
      */
     assertThat(clesMetier).doesNotHaveDuplicates();

     /* 
      * compte finalement (en SQL) le nombre d'enregistrements 
      * dans le stockage pour prouver que service.findByLibelleRapide(...) 
      * n'a pas touché au stockage.
      */
     final Long countApres = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
             Long.class);

     assertThat(countApres).isNotNull().isNotZero();
     assertThat(countApres).isEqualTo(countAvant);

 } // __________________________________________________________________
 


 /**
  * <div>
  * <p>garantit que findByLibelleRapide(OK) :</p>
  * <ul>
  * <li>retourne une liste non null et non vide ;</li>
  * <li>retourne exactement les identifiants présents dans le stockage
  * pour le contenu recherché ;</li>
  * <li>retourne un contenu trié selon l'ordre métier parent puis libellé ;</li>
  * <li>retourne des objets métier dont le parent correspond au stockage ;</li>
  * <li>retourne un contenu sans doublon ;</li>
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
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
             Long.class);

     /* vérifie que le stockage n'est pas vide. */
     assertThat(countAvant).isNotNull().isNotZero();

     /* 
      * Recherche (en SQL) dans le stockage tous les objets métier
      * dont le libellé contient CONTENU_PARTIEL_VET,
      * sans tenir compte des majuscules/minuscules,
      * et dans l'ordre métier réellement attendu :
      * parent puis libellé de l'objet métier.
      */
     final Long countStockage = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE_LIKE,
             Long.class,
             "%" + CONTENU_PARTIEL_VET + "%");

     final List<Long> idsStockage = this.jdbcTemplate.queryForList(
             "SELECT STP.ID_SOUS_TYPE_PRODUIT "
                     + "FROM SOUS_TYPES_PRODUIT STP "
                     + "INNER JOIN TYPES_PRODUIT TP "
                     + "ON STP.TYPE_PRODUIT = TP.ID_TYPE_PRODUIT "
                     + "WHERE UPPER(STP.SOUS_TYPE_PRODUIT) LIKE UPPER(?) "
                     + "ORDER BY UPPER(TP.TYPE_PRODUIT) ASC, "
                     + "UPPER(STP.SOUS_TYPE_PRODUIT) ASC",
             Long.class,
             "%" + CONTENU_PARTIEL_VET + "%");

     /* 
      * vérifie que des objets métier contenant CONTENU_PARTIEL_VET
      * existent dans le stockage.
      */
     assertThat(countStockage).isNotNull().isPositive();
     assertThat(idsStockage).isNotNull().isNotEmpty();
     assertThat(idsStockage).hasSize(countStockage.intValue());

     /* Neutralise explicitement le contexte Hibernate
      * avant l'appel de lecture,
      * afin d'éviter tout raisonnement biaisé par le cache.
      */
     this.entityManager.clear();

     /* ACT :
      * sollicite service.findByLibelleRapide(...)
      * avec CONTENU_PARTIEL_VET.
      */
     final List<SousTypeProduit> liste =
             this.service.findByLibelleRapide(CONTENU_PARTIEL_VET);

     /* ASSERT :
      * vérifie que l'appel service.findByLibelleRapide(...) 
      * retourne le même nombre d'enregistrements 
      * que le stockage interrogé en SQL.
      */
     assertThat(liste).isNotNull().isNotEmpty();
     assertThat(liste).hasSize(countStockage.intValue());

     final List<Long> idsRetournes = liste.stream()
             .map(SousTypeProduit::getIdSousTypeProduit)
             .toList();

     /* 
      * Vérifie ensuite que les identifiants renvoyés par le service
      * correspondent exactement aux identifiants présents dans le stockage
      * pour le contenu CONTENU_PARTIEL_VET,
      * dans l'ordre métier parent puis libellé.
      */
     assertThat(idsRetournes).containsExactlyElementsOf(idsStockage);
     assertThat(idsRetournes).doesNotHaveDuplicates();

     /* Vérifie que tous les objets métier retournés
      * contiennent bien le motif recherché
      * et portent un parent cohérent avec le stockage.
      */
     assertThat(liste)
         .allSatisfy(stp -> {
             assertThat(stp).isNotNull();
             assertThat(stp.getSousTypeProduit()).isNotBlank();
             assertThat(stp.getSousTypeProduit().toUpperCase(LOCALE_DEFAUT))
                 .contains(CONTENU_PARTIEL_VET.toUpperCase(LOCALE_DEFAUT));
             assertThat(stp.getTypeProduit()).isNotNull();

             final Long idParentStockage = this.jdbcTemplate.queryForObject(
                     SELECT_TP_FROM_STP_WHERE_ID,
                     Long.class,
                     stp.getIdSousTypeProduit());

             assertThat(idParentStockage).isNotNull();
             assertThat(stp.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentStockage);

             final String libelleParentStockage = this.jdbcTemplate.queryForObject(
                     SELECT_PARAM_TP_FROM_TP_WHERE_ID,
                     String.class,
                     idParentStockage);

             assertThat(libelleParentStockage).isNotBlank();
             assertThat(stp.getTypeProduit().getTypeProduit()).isEqualTo(libelleParentStockage);
         });

     final List<String> clesMetier = liste.stream()
             .map(stp -> stp.getTypeProduit().getIdTypeProduit()
                     + "|"
                     + stp.getSousTypeProduit().toUpperCase(LOCALE_DEFAUT))
             .toList();

     /* Vérifie que le résultat métier final
      * ne contient pas de doublon parent/libellé.
      */
     assertThat(clesMetier).doesNotHaveDuplicates();

     /* 
      * compte finalement (en SQL) le nombre d'enregistrements 
      * dans le stockage pour prouver que service.findByLibelleRapide(...) 
      * n'a pas touché au stockage.
      */
     final Long countApres = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
	 * {@link SousTypeProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL} ;</li>
	 * <li>n'altère pas le stockage.</li>
	 * </ul>
	 * </div>
	 */
	@Tag(TAG_RECHERCHER)
	@DisplayName(DN_FINDALLBYPARENT_NULL)
	@Test
	public void testFindAllByParentNull() {
	
	    /* ARRANGE :
	     * compte d'abord (en SQL) le nombre d'enregistrements 
	     * dans le stockage afin de pouvoir prouver ensuite
	     * que service.findAllByParent(...) ne produit aucune écriture 
	     * dans le stockage.
	     */
	    final Long countAvant = this.jdbcTemplate.queryForObject(
	            SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
	        .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL);
	
	    /* ASSERT :
	     * compte finalement (en SQL) le nombre d'enregistrements 
	     * dans le stockage pour prouver que service.findAllByParent(...) 
	     * n'a pas touché au stockage.
	     */
	    final Long countApres = this.jdbcTemplate.queryForObject(
	            SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
	 * {@link SousTypeProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK} ;</li>
	 * <li>n'altère pas le stockage.</li>
	 * </ul>
	 * </div>
	 */
	@Tag(TAG_RECHERCHER)
	@DisplayName(DN_FINDALLBYPARENT_LIBELLE_NULL)
	@Test
	public void testFindAllByParentParentLibelleNull() {
	
	    /* ARRANGE :
	     * compte d'abord (en SQL) le nombre d'enregistrements 
	     * dans le stockage afin de pouvoir prouver ensuite
	     * que service.findAllByParent(...) ne produit aucune écriture 
	     * dans le stockage.
	     */
	    final Long countAvant = this.jdbcTemplate.queryForObject(
	            SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
	            Long.class);
	
	    /* vérifie que le stockage n'est pas vide. */
	    assertThat(countAvant).isNotNull().isNotZero();
	
	    /* 
	     * prépare un parent dont le libellé est null.
	     */
	    final TypeProduit parent = new TypeProduit(Long.valueOf(1L), null);
	
	    /* ACT - ASSERT :
	     * garantit que service.findAllByParent(parent)
	     * - jette une ExceptionAppliLibelleBlank
	     * - émet le message MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK
	     * (message contractuel attendu).
	     */
	    assertThatThrownBy(() -> this.service.findAllByParent(parent))
	        .isInstanceOf(ExceptionAppliLibelleBlank.class)
	        .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK);
	
	    /* ASSERT :
	     * compte finalement (en SQL) le nombre d'enregistrements 
	     * dans le stockage pour prouver que service.findAllByParent(...) 
	     * n'a pas touché au stockage.
	     */
	    final Long countApres = this.jdbcTemplate.queryForObject(
	            SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
	 * {@link SousTypeProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK} ;</li>
	 * <li>n'altère pas le stockage.</li>
	 * </ul>
	 * </div>
	 */
	@Tag(TAG_RECHERCHER)
	@DisplayName(DN_FINDALLBYPARENT_LIBELLE_BLANK)
	@Test
	public void testFindAllByParentParentLibelleBlank() {
	
	    /* ARRANGE :
	     * compte d'abord (en SQL) le nombre d'enregistrements 
	     * dans le stockage afin de pouvoir prouver ensuite
	     * que service.findAllByParent(...) ne produit aucune écriture 
	     * dans le stockage.
	     */
	    final Long countAvant = this.jdbcTemplate.queryForObject(
	            SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
	            Long.class);
	
	    /* vérifie que le stockage n'est pas vide. */
	    assertThat(countAvant).isNotNull().isNotZero();
	
	    /* 
	     * prépare un parent dont le libellé est blank.
	     */
	    final TypeProduit parent = new TypeProduit(Long.valueOf(1L), BLANK);
	
	    /* ACT - ASSERT :
	     * garantit que service.findAllByParent(parent)
	     * - jette une ExceptionAppliLibelleBlank
	     * - émet le message MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK
	     * (message contractuel attendu).
	     */
	    assertThatThrownBy(() -> this.service.findAllByParent(parent))
	        .isInstanceOf(ExceptionAppliLibelleBlank.class)
	        .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK);
	
	    /* ASSERT :
	     * compte finalement (en SQL) le nombre d'enregistrements 
	     * dans le stockage pour prouver que service.findAllByParent(...) 
	     * n'a pas touché au stockage.
	     */
	    final Long countApres = this.jdbcTemplate.queryForObject(
	            SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
	 * {@link SousTypeProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT}
	 * + {@link #LIBELLE_PARENT_VETEMENT} ;</li>
	 * <li>n'altère pas le stockage.</li>
	 * </ul>
	 * </div>
	 */
	@Tag(TAG_RECHERCHER)
	@DisplayName(DN_FINDALLBYPARENT_NON_PERSISTANT)
	@Test
	public void testFindAllByParentParentIdNull() {
	
	    /* ARRANGE :
	     * compte d'abord (en SQL) le nombre d'enregistrements 
	     * dans le stockage afin de pouvoir prouver ensuite
	     * que service.findAllByParent(...) ne produit aucune écriture 
	     * dans le stockage.
	     */
	    final Long countAvant = this.jdbcTemplate.queryForObject(
	            SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
	            Long.class);
	
	    /* vérifie que le stockage n'est pas vide. */
	    assertThat(countAvant).isNotNull().isNotZero();
	
	    /* 
	     * prépare un parent non persistant
	     * car son identifiant est null.
	     */
	    final TypeProduit parent = new TypeProduit(null, LIBELLE_PARENT_VETEMENT);
	
	    /* ACT - ASSERT :
	     * garantit que service.findAllByParent(parent)
	     * - jette une ExceptionTechniqueGatewayNonPersistent
	     * - émet le message MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
	     * + LIBELLE_PARENT_VETEMENT
	     * (message contractuel attendu).
	     */
	    assertThatThrownBy(() -> this.service.findAllByParent(parent))
	        .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
	        .hasMessage(
	                SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
	                        + LIBELLE_PARENT_VETEMENT);
	
	    /* ASSERT :
	     * compte finalement (en SQL) le nombre d'enregistrements 
	     * dans le stockage pour prouver que service.findAllByParent(...) 
	     * n'a pas touché au stockage.
	     */
	    final Long countApres = this.jdbcTemplate.queryForObject(
	            SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
	            Long.class);
	
	    assertThat(countApres).isNotNull().isNotZero();
	    assertThat(countApres).isEqualTo(countAvant);
	
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findAllByParent(parent absent) :</p>
	 * <ul>
	 * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
	 * <li>émet le message
	 * {@link SousTypeProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT}
	 * + {@link #LIBELLE_PARENT_VETEMENT} ;</li>
	 * <li>prouve que le parent est absent du stockage avant l'appel ;</li>
	 * <li>n'altère pas le stockage.</li>
	 * </ul>
	 * </div>
	 */
	@Tag(TAG_RECHERCHER)
	@DisplayName(DN_FINDALLBYPARENT_NON_PERSISTANT)
	@Test
	public void testFindAllByParentParentAbsent() {
	
	    /* ARRANGE :
	     * compte d'abord (en SQL) le nombre d'enregistrements 
	     * dans le stockage afin de pouvoir prouver ensuite
	     * que service.findAllByParent(...) ne produit aucune écriture 
	     * dans le stockage.
	     */
	    final Long countAvant = this.jdbcTemplate.queryForObject(
	            SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
	            Long.class);
	
	    /* vérifie que le stockage n'est pas vide. */
	    assertThat(countAvant).isNotNull().isNotZero();
	
	    /* 
	     * Vérifie directement dans le stockage
	     * qu'aucun parent ne porte l'identifiant ID_INEXISTANT.
	     */
	    final Long countParentStockage = this.jdbcTemplate.queryForObject(
	            "SELECT COUNT(*) FROM TYPES_PRODUIT WHERE ID_TYPE_PRODUIT = ?",
	            Long.class,
	            ID_INEXISTANT);
	
	    assertThat(countParentStockage).isNotNull().isZero();
	
	    /* 
	     * prépare un parent portant un identifiant inexistant,
	     * donc absent du stockage.
	     */
	    final TypeProduit parent = new TypeProduit(ID_INEXISTANT, LIBELLE_PARENT_VETEMENT);
	
	    /* ACT - ASSERT :
	     * garantit que service.findAllByParent(parent)
	     * - jette une ExceptionTechniqueGatewayNonPersistent
	     * - émet le message MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
	     * + LIBELLE_PARENT_VETEMENT
	     * (message contractuel attendu).
	     */
	    assertThatThrownBy(() -> this.service.findAllByParent(parent))
	        .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
	        .hasMessage(
	                SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
	                        + LIBELLE_PARENT_VETEMENT);
	
	    /* ASSERT :
	     * compte finalement (en SQL) le nombre d'enregistrements 
	     * dans le stockage pour prouver que service.findAllByParent(...) 
	     * n'a pas touché au stockage.
	     */
	    final Long countApres = this.jdbcTemplate.queryForObject(
	            SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
	            Long.class);
	
	    assertThat(countApres).isNotNull().isNotZero();
	    assertThat(countApres).isEqualTo(countAvant);
	
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findAllByParent(parent sans enfant) :</p>
	 * <ul>
	 * <li>retourne une liste non null et vide ;</li>
	 * <li>prouve que le parent existe dans le stockage ;</li>
	 * <li>prouve qu'aucun enfant n'existe dans le stockage pour ce parent ;</li>
	 * <li>n'altère pas le stockage des objets métier.</li>
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
	     * dans le stockage des objets métier afin de pouvoir prouver ensuite
	     * que service.findAllByParent(...) ne produit aucune écriture 
	     * dans ce stockage.
	     */
	    final Long countAvant = this.jdbcTemplate.queryForObject(
	            SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
	            Long.class);
	
	    /* vérifie que le stockage n'est pas vide. */
	    assertThat(countAvant).isNotNull().isNotZero();
	
	    /* 
	     * prépare un parent persistant sans enfant dans le stockage.
	     */
	    this.jdbcTemplate.update(
	            INSERT_PARAM_INTO_TP,
	            LIBELLE_PARENT_CHAUSSURE);
	
	    final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_CHAUSSURE);
	    final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_CHAUSSURE);
	
	    /* 
	     * Vérifie directement dans le stockage
	     * que le parent dédié existe physiquement.
	     */
	    final Long countParentStockage = this.jdbcTemplate.queryForObject(
	            "SELECT COUNT(*) FROM TYPES_PRODUIT WHERE ID_TYPE_PRODUIT = ?",
	            Long.class,
	            idParent);
	
	    assertThat(countParentStockage).isNotNull().isEqualTo(1L);
	
	    /* 
	     * vérifie (en SQL)
	     * qu'aucun enfant n'existe dans le stockage pour ce parent.
	     */
	    final Long countEnStockage = this.jdbcTemplate.queryForObject(
	            SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_PARENT,
	            Long.class,
	            idParent);
	
	    assertThat(countEnStockage).isNotNull().isZero();
	
	    /* Neutralise explicitement le contexte Hibernate
	     * avant l'appel de lecture,
	     * afin d'éviter tout raisonnement biaisé par le cache.
	     */
	    this.entityManager.clear();
	
	    /* ACT :
	     * appelle service.findAllByParent(parent)
	     * sur un parent persistant sans enfant.
	     */
	    final List<SousTypeProduit> liste = this.service.findAllByParent(parent);
	
	    /* ASSERT :
	     * vérifie que la liste retournée est non null et vide.
	     */
	    assertThat(liste).isNotNull().isEmpty();
	
	    /* 
	     * compte finalement (en SQL) le nombre d'enregistrements 
	     * dans le stockage des objets métier pour prouver que 
	     * service.findAllByParent(...) n'a pas touché à ce stockage.
	     */
	    final Long countApres = this.jdbcTemplate.queryForObject(
	            SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
	            Long.class);
	
	    assertThat(countApres).isNotNull().isNotZero();
	    assertThat(countApres).isEqualTo(countAvant);
	
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findAllByParent(OK) :</p>
	 * <ul>
	 * <li>retourne une liste non null et non vide ;</li>
	 * <li>retourne exactement les identifiants présents 
	 * dans le stockage pour ce parent ;</li>
	 * <li>retourne les objets métier dans l'ordre métier attendu
	 * pour ce parent ;</li>
	 * <li>retourne uniquement des enfants du parent demandé ;</li>
	 * <li>retourne des objets métier dont le libellé et le parent
	 * correspondent au stockage ;</li>
	 * <li>retourne un contenu sans doublon ;</li>
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
	            SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
	            Long.class);
	
	    /* vérifie que le stockage n'est pas vide. */
	    assertThat(countAvant).isNotNull().isNotZero();
	
	    /* trouve un parent persistant dans le stockage. */
	    final Long idParent 
	    	= retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
	    final TypeProduit parent 
	    	= new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
	
	    /* 
	     * recherche (en SQL) les enfants présents dans le stockage
	     * pour le parent LIBELLE_PARENT_VETEMENT,
	     * dans l'ordre métier réellement attendu :
	     * parent puis libellé de l'objet métier.
	     */
	    final Long countEnStockage = this.jdbcTemplate.queryForObject(
	            SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_PARENT,
	            Long.class,
	            idParent);
	
	    final List<Long> idsStockage = this.jdbcTemplate.queryForList(
	            "SELECT STP.ID_SOUS_TYPE_PRODUIT "
	                    + "FROM SOUS_TYPES_PRODUIT STP "
	                    + "INNER JOIN TYPES_PRODUIT TP "
	                    + "ON STP.TYPE_PRODUIT = TP.ID_TYPE_PRODUIT "
	                    + "WHERE STP.TYPE_PRODUIT = ? "
	                    + "ORDER BY UPPER(TP.TYPE_PRODUIT) ASC, "
	                    + "UPPER(STP.SOUS_TYPE_PRODUIT) ASC",
	            Long.class,
	            idParent);
	
	    final List<String> libellesStockage = this.jdbcTemplate.queryForList(
	            "SELECT STP.SOUS_TYPE_PRODUIT "
	                    + "FROM SOUS_TYPES_PRODUIT STP "
	                    + "INNER JOIN TYPES_PRODUIT TP "
	                    + "ON STP.TYPE_PRODUIT = TP.ID_TYPE_PRODUIT "
	                    + "WHERE STP.TYPE_PRODUIT = ? "
	                    + "ORDER BY UPPER(TP.TYPE_PRODUIT) ASC, "
	                    + "UPPER(STP.SOUS_TYPE_PRODUIT) ASC",
	            String.class,
	            idParent);
	
	    /* 
	     * Assure que le parent persistant a des enfants 
	     * dans le stockage.
	     */
	    assertThat(countEnStockage).isNotNull().isPositive();
	    assertThat(idsStockage).isNotNull().isNotEmpty();
	    assertThat(idsStockage).hasSize(countEnStockage.intValue());
	    assertThat(idsStockage).doesNotHaveDuplicates();
	    assertThat(libellesStockage).isNotNull().isNotEmpty();
	    assertThat(libellesStockage).hasSize(countEnStockage.intValue());
	    assertThat(libellesStockage).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);
	
	    /* 
	     * Neutralise explicitement le contexte Hibernate
	     * avant l'appel de lecture afin d'éviter tout raisonnement 
	     * biaisé par le cache.
	     */
	    this.entityManager.clear();
	
	    /* ACT :
	     * appelle service.findAllByParent(parent)
	     * sur un parent persistant présent dans le stockage.
	     */
	    final List<SousTypeProduit> liste = this.service.findAllByParent(parent);
	
	    /* ASSERT :
	     * vérifie que la liste retournée
	     * contient le bon nombre d'objets métier.
	     */
	    assertThat(liste).isNotNull().isNotEmpty();
	    assertThat(liste).hasSize(countEnStockage.intValue());
	
	    final List<Long> idsRetournes = liste.stream()
	            .map(SousTypeProduit::getIdSousTypeProduit)
	            .toList();
	
	    final List<String> libellesRetournes = liste.stream()
	            .map(SousTypeProduit::getSousTypeProduit)
	            .toList();
	
	    /* Vérifie que les identifiants retournés
	     * correspondent exactement aux identifiants présents dans le stockage,
	     * dans l'ordre métier attendu par le service.
	     */
	    assertThat(idsRetournes).containsExactlyElementsOf(idsStockage);
	    assertThat(idsRetournes).doesNotHaveDuplicates();
	
	    /* Vérifie que les libellés retournés
	     * correspondent exactement aux libellés présents dans le stockage,
	     * dans l'ordre métier attendu par le service.
	     */
	    assertThat(libellesRetournes).containsExactlyElementsOf(libellesStockage);
	    assertThat(libellesRetournes).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);
	
	    /* Vérifie chaque objet métier retourné par le service :
	     * - l'objet métier existe physiquement dans le stockage ;
	     * - son libellé correspond au libellé lu dans le stockage ;
	     * - sa clé étrangère parent correspond au parent demandé ;
	     * - le parent métier retourné correspond au parent lu dans le stockage.
	     */
	    assertThat(liste)
	        .allSatisfy(stp -> {
	            assertThat(stp).isNotNull();
	            assertThat(stp.getIdSousTypeProduit()).isNotNull();
	            assertThat(stp.getSousTypeProduit()).isNotBlank();
	            assertThat(stp.getTypeProduit()).isNotNull();
	
	            final Long countLigneStockage = this.jdbcTemplate.queryForObject(
	                    SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
	                    Long.class,
	                    stp.getIdSousTypeProduit());
	
	            assertThat(countLigneStockage).isNotNull().isEqualTo(1L);
	
	            final String libelleStockage = this.jdbcTemplate.queryForObject(
	                    SELECT_STP_FROM_STP_WHERE_ID,
	                    String.class,
	                    stp.getIdSousTypeProduit());
	
	            assertThat(libelleStockage).isNotBlank();
	            assertThat(stp.getSousTypeProduit()).isEqualTo(libelleStockage);
	
	            final Long idParentStockage = this.jdbcTemplate.queryForObject(
	                    SELECT_TP_FROM_STP_WHERE_ID,
	                    Long.class,
	                    stp.getIdSousTypeProduit());
	
	            assertThat(idParentStockage).isNotNull();
	            assertThat(idParentStockage).isEqualTo(idParent);
	            assertThat(stp.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentStockage);
	
	            final String libelleParentStockage = this.jdbcTemplate.queryForObject(
	                    SELECT_PARAM_TP_FROM_TP_WHERE_ID,
	                    String.class,
	                    idParentStockage);
	
	            assertThat(libelleParentStockage).isNotBlank();
	            assertThat(libelleParentStockage).isEqualTo(LIBELLE_PARENT_VETEMENT);
	            assertThat(stp.getTypeProduit().getTypeProduit()).isEqualTo(libelleParentStockage);
	        });
	
	    final List<String> clesMetier = liste.stream()
	            .map(stp -> stp.getTypeProduit().getIdTypeProduit()
	                    + "|"
	                    + stp.getSousTypeProduit().toUpperCase(LOCALE_DEFAUT))
	            .toList();
	
	    /* Vérifie que le résultat métier final
	     * ne contient pas de doublon parent/libellé.
	     */
	    assertThat(clesMetier).doesNotHaveDuplicates();
	
	    /* 
	     * compte finalement (en SQL) le nombre d'enregistrements 
	     * dans le stockage pour prouver que service.findAllByParent(...) 
	     * n'a pas touché au stockage.
	     */
	    final Long countApres = this.jdbcTemplate.queryForObject(
	            SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
    * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYID_KO_PARAM_NULL} ;</li>
    * <li>n'altère pas le stockage.</li>
    * </ul>
    * </div>
    */
   @Tag(TAG_RECHERCHER)
   @DisplayName(DN_FINDBYID_NULL)
   @Test
   public void testFindByIdNull() {

       /* ARRANGE :
        * compte d'abord (en SQL) le nombre d'enregistrements 
        * dans le stockage afin de pouvoir prouver ensuite
        * que service.findById(...) ne produit aucune écriture 
        * dans le stockage.
        */
       final Long countAvant = this.jdbcTemplate.queryForObject(
               SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
           .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYID_KO_PARAM_NULL);

       /* ASSERT :
        * compte finalement (en SQL) le nombre d'enregistrements 
        * dans le stockage pour prouver que service.findById(...) 
        * n'a pas touché au stockage.
        */
       final Long countApres = this.jdbcTemplate.queryForObject(
               SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
               Long.class);

       assertThat(countApres).isNotNull().isNotZero();
       assertThat(countApres).isEqualTo(countAvant);

   } // __________________________________________________________________
   


   /**
    * <div>
    * <p>garantit que findById(non trouvé) :</p>
    * <ul>
    * <li>retourne {@code null} ;</li>
    * <li>reste cohérent avec l'absence physique de ligne correspondante
    * dans le stockage ;</li>
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
               SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
               Long.class);

       /* vérifie que le stockage n'est pas vide. */
       assertThat(countAvant).isNotNull().isNotZero();

       /* 
        * vérifie (en SQL)
        * qu'aucun enregistrement dans le stockage
        * ne porte l'identifiant ID_INEXISTANT.
        */
       final Long countStockage = this.jdbcTemplate.queryForObject(
               SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
               Long.class,
               ID_INEXISTANT);

       assertThat(countStockage).isNotNull().isZero();

       /* Neutralise explicitement le contexte Hibernate
        * avant l'appel de lecture,
        * afin d'éviter tout raisonnement biaisé par le cache.
        */
       this.entityManager.clear();

       /* ACT :
        * appelle service.findById(ID_INEXISTANT)
        * avec un identifiant absent du stockage.
        */
       final SousTypeProduit resultat = this.service.findById(ID_INEXISTANT);

       /* ASSERT :
        * vérifie que service.findById(ID_INEXISTANT) retourne null
        * lorsque l'identifiant est absent du stockage.
        */
       assertThat(resultat).isNull();

       /* 
        * compte finalement (en SQL) le nombre d'enregistrements 
        * dans le stockage pour prouver que service.findById(...) 
        * n'a pas touché au stockage.
        */
       final Long countApres = this.jdbcTemplate.queryForObject(
               SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
               Long.class);

       assertThat(countApres).isNotNull().isNotZero();
       assertThat(countApres).isEqualTo(countAvant);

   } // __________________________________________________________________
   


   /**
    * <div>
    * <p>garantit que findById(OK) :</p>
    * <ul>
    * <li>retourne un objet métier non null ;</li>
    * <li>retourne l'identifiant demandé ;</li>
    * <li>retourne le libellé réellement présent dans le stockage ;</li>
    * <li>retourne le parent réellement présent dans le stockage ;</li>
    * <li>n'altère pas le stockage.</li>
    * </ul>
    * </div>
    *
    * @throws Exception
    */
   @Tag(TAG_RECHERCHER)
   @DisplayName(DN_FINDBYID_TROUVE)
   @Test
   public void testFindByIdNominal() throws Exception {

       /* ARRANGE :
        * compte d'abord (en SQL) le nombre d'enregistrements 
        * dans le stockage afin de pouvoir prouver ensuite
        * que service.findById(...) ne produit aucune écriture 
        * dans le stockage.
        */
       final Long countAvant = this.jdbcTemplate.queryForObject(
               SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
               Long.class);

       /* vérifie que le stockage n'est pas vide. */
       assertThat(countAvant).isNotNull().isNotZero();

       /*
        * Retrouve l'identifiant persistant de l'objet métier
        * LIBELLE_ENFANT_VETEMENT_HOMME.
        */
       final Long idEnfant =
               retrouverIdEnfantPersistantParLibelle(LIBELLE_ENFANT_VETEMENT_HOMME);

       /* 
        * vérifie (en SQL)
        * qu'un seul enregistrement dans le stockage
        * porte l'identifiant idEnfant.
        */
       final Long countEnregistrement = this.jdbcTemplate.queryForObject(
               SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
               Long.class,
               idEnfant);

       assertThat(countEnregistrement).isNotNull().isEqualTo(1L);

       /* 
        * Lit le libellé enfant dans le stockage
        * pour l'identifiant idEnfant.
        */
       final String libelleEnfantStockage = this.jdbcTemplate.queryForObject(
               SELECT_STP_FROM_STP_WHERE_ID,
               String.class,
               idEnfant);

       assertThat(libelleEnfantStockage).isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);

       /* 
        * Lit l'identifiant du parent dans le stockage
        * pour l'identifiant idEnfant.
        */
       final Long idParentStockage = this.jdbcTemplate.queryForObject(
               SELECT_TP_FROM_STP_WHERE_ID,
               Long.class,
               idEnfant);

       assertThat(idParentStockage).isNotNull();

       /*
        * Lit le libellé du parent dans le stockage
        * pour l'identifiant idParentStockage.
        */
       final String libelleParentStockage = this.jdbcTemplate.queryForObject(
               SELECT_PARAM_TP_FROM_TP_WHERE_ID,
               String.class,
               idParentStockage);

       assertThat(libelleParentStockage).isEqualTo(LIBELLE_PARENT_VETEMENT);

       /* 
        * Neutralise explicitement le contexte Hibernate
        * avant l'appel de lecture,
        * afin d'éviter tout raisonnement biaisé par le cache.
        */
       this.entityManager.clear();

       /* ACT :
        * appelle service.findById(idEnfant)
        * avec un identifiant réellement présent dans le stockage.
        */
       final SousTypeProduit resultat = this.service.findById(idEnfant);

       /* ASSERT :
        * vérifie que l'objet retourné n'est pas null
        * et porte le bon identifiant.
        */
       assertThat(resultat).isNotNull();
       assertThat(resultat.getIdSousTypeProduit()).isEqualTo(idEnfant);

       /* 
        * Vérifie que l'objet retourné
        * porte le bon libellé enfant.
        */
       assertThat(resultat.getSousTypeProduit()).isEqualTo(libelleEnfantStockage);

       /* 
        * Vérifie que l'objet retourné
        * porte le bon parent :
        * - parent non null ;
        * - identifiant du parent égal à celui lu dans le stockage ;
        * - libellé du parent égal à celui lu dans le stockage.
        */
       assertThat(resultat.getTypeProduit()).isNotNull();
       assertThat(resultat.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentStockage);
       assertThat(resultat.getTypeProduit().getTypeProduit()).isEqualTo(libelleParentStockage);

       /* 
        * compte finalement (en SQL) le nombre d'enregistrements 
        * dans le stockage pour prouver que service.findById(...) 
        * n'a pas touché au stockage.
        */
       final Long countApres = this.jdbcTemplate.queryForObject(
               SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
               Long.class);

       assertThat(countApres).isNotNull().isNotZero();
       assertThat(countApres).isEqualTo(countAvant);

   } // __________________________________________________________________
   


   /**
    * <div>
    * <p>garantit que findById(ID créé) :</p>
    * <ul>
    * <li>retrouve un objet métier nouvellement créé ;</li>
    * <li>retourne l'identifiant créé ;</li>
    * <li>retourne le libellé réellement persisté dans le stockage ;</li>
    * <li>retourne le parent réellement persisté dans le stockage ;</li>
    * <li>nettoie défensivement l'enregistrement créé.</li>
    * </ul>
    * </div>
    *
    * @throws Exception
    */
   @Tag(TAG_RECHERCHER)
   @DisplayName("findById(ID créé) - retrouve l'objet métier nouvellement persisté")
   @Test
   @Transactional(propagation = Propagation.NOT_SUPPORTED)
   public void testFindByIdIdCree() throws Exception {

       /* ARRANGE :
        * lit d'abord l'état physique du stockage,
        * retrouve un parent persistant,
        * puis prépare une création nominale sous ce parent.
        *
        * Le test est volontairement exécuté hors transaction de test
        * pour prouver une écriture réelle dans le stockage,
        * puis réaliser un nettoyage physique explicite en finally.
        */
       final Long countAvant = this.jdbcTemplate.queryForObject(
               SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
               Long.class);

       assertThat(countAvant).isNotNull().isNotZero();

       final Long idParent =
               retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);

       assertThat(idParent).isNotNull();

       final SousTypeProduit aCreer = new SousTypeProduit(
               null,
               LIBELLE_NOUVEAU_PULL,
               new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT));

       Long idCree = null;

       try {

           /* ACT :
            * crée réellement un nouvel objet métier,
            * afin de vérifier ensuite que la recherche par identifiant
            * fonctionne aussi sur une donnée créée pendant le test.
            */
           final SousTypeProduit cree = this.service.creer(aCreer);

           /* ASSERT :
            * garantit d'abord que l'objet métier retourné par creer(...)
            * est bien persistant et correctement renseigné.
            */
           assertThat(cree).isNotNull();
           assertThat(cree.getIdSousTypeProduit()).isNotNull().isPositive();
           assertThat(cree.getSousTypeProduit()).isEqualTo(LIBELLE_NOUVEAU_PULL);
           assertThat(cree.getTypeProduit()).isNotNull();
           assertThat(cree.getTypeProduit().getIdTypeProduit()).isEqualTo(idParent);
           assertThat(cree.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_VETEMENT);

           idCree = cree.getIdSousTypeProduit();

           /* ASSERT :
            * contrôle ensuite physiquement le stockage par SQL direct,
            * pour prouver l'écriture réelle dans le stockage
            * et non un simple effet de cache Hibernate.
            */
           final Long countApresCreation = this.jdbcTemplate.queryForObject(
                   SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                   Long.class);

           final Long countLigneStockage = this.jdbcTemplate.queryForObject(
                   SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                   Long.class,
                   idCree);

           final String libelleStockage = this.jdbcTemplate.queryForObject(
                   SELECT_STP_FROM_STP_WHERE_ID,
                   String.class,
                   idCree);

           final Long idParentStockage = this.jdbcTemplate.queryForObject(
                   SELECT_TP_FROM_STP_WHERE_ID,
                   Long.class,
                   idCree);

           final String libelleParentStockage = this.jdbcTemplate.queryForObject(
                   SELECT_PARAM_TP_FROM_TP_WHERE_ID,
                   String.class,
                   idParentStockage);

           /* Garantit que la création augmente bien
            * le nombre d'enregistrements dans le stockage.
            */
           assertThat(countApresCreation).isNotNull().isEqualTo(countAvant + 1L);

           /* Garantit qu'un seul enregistrement
            * porte l'identifiant créé dans le stockage.
            */
           assertThat(countLigneStockage).isNotNull().isEqualTo(1L);

           /* Garantit que l'enregistrement créé
            * porte le bon libellé objet métier dans le stockage.
            */
           assertThat(libelleStockage).isEqualTo(LIBELLE_NOUVEAU_PULL);

           /* Garantit que l'enregistrement créé
            * porte la bonne clé étrangère parent dans le stockage.
            */
           assertThat(idParentStockage).isNotNull().isEqualTo(idParent);

           /* Garantit que le parent de l'enregistrement créé
            * porte le bon libellé dans le stockage.
            */
           assertThat(libelleParentStockage).isEqualTo(LIBELLE_PARENT_VETEMENT);

           /*
            * Neutralise explicitement le contexte Hibernate
            * avant toute relecture via le service pour éviter
            * d'être leurré par le cache Hibernate.
            */
           this.entityManager.clear();

           /* ACT :
            * sollicite la recherche par identifiant
            * avec l'ID de l'objet métier nouvellement créé.
            */
           final SousTypeProduit retour = this.service.findById(idCree);

           /* ASSERT :
            * vérifie que le service retrouve bien
            * l'enregistrement nouvellement persisté.
            */
           assertThat(retour).isNotNull();
           assertThat(retour.getIdSousTypeProduit()).isEqualTo(idCree);
           assertThat(retour.getSousTypeProduit()).isEqualTo(libelleStockage);
           assertThat(retour.getTypeProduit()).isNotNull();
           assertThat(retour.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentStockage);
           assertThat(retour.getTypeProduit().getTypeProduit()).isEqualTo(libelleParentStockage);

           /* ASSERT :
            * compte finalement (en SQL) le nombre d'enregistrements 
            * dans le stockage pour prouver que service.findById(...) 
            * n'a pas touché au stockage après la création.
            */
           final Long countApresLecture = this.jdbcTemplate.queryForObject(
                   SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                   Long.class);

           assertThat(countApresLecture).isNotNull().isEqualTo(countApresCreation);

       } finally {

           /* Nettoyage défensif :
            * si l'enregistrement créé existe encore dans le stockage
            * après une éventuelle assertion en échec,
            * le supprime explicitement afin de garantir l'isolation du test.
            */
           if (idCree != null) {
               final Long countLigne = this.jdbcTemplate.queryForObject(
                       SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                       Long.class,
                       idCree);

               if ((countLigne != null) && (countLigne.longValue() == 1L)) {
                   this.jdbcTemplate.update(
                           DELETE_FROM_STP_WHERE_ID_STP,
                           idCree);
               }
           }

       }

   } // __________________________________________________________________

   
   
   // =============================== update =============================



  /**
   * <div>
   * <p>garantit que update(null) :</p>
   * <ul>
   * <li>jette une {@link ExceptionAppliParamNull} ;</li>
   * <li>émet le message
   * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_PARAM_NULL} ;</li>
   * <li>n'altère pas le stockage.</li>
   * </ul>
   * </div>
   */
  @Tag(TAG_UPDATE)
  @DisplayName(DN_UPDATE_NULL)
  @Test
  public void testUpdateNull() {

      /* ARRANGE :
       * compte d'abord (en SQL)
       * le nombre d'enregistrements dans le stockage
       * avant l'appel du service,
       * afin de pouvoir prouver ensuite
       * qu'aucune écriture n'a eu lieu dans le stockage.
       */
      final Long countAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      /* ACT - ASSERT :
       * garantit que service.update(null)
       * - jette une ExceptionAppliParamNull
       * - émet le message MESSAGE_UPDATE_KO_PARAM_NULL
       * (message contractuel attendu).
       */
      assertThatThrownBy(() -> this.service.update(null))
          .isInstanceOf(ExceptionAppliParamNull.class)
          .hasMessage(SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARAM_NULL);

      /* ASSERT :
       * compte finalement (en SQL)
       * le nombre d'enregistrements dans le stockage
       * pour prouver que service.update(...)
       * n'a pas touché au stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
   * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_BLANK} ;</li>
   * <li>n'altère pas le stockage.</li>
   * </ul>
   * </div>
   */
  @Tag(TAG_UPDATE)
  @DisplayName(DN_UPDATE_LIBELLE_NULL)
  @Test
  public void testUpdateLibelleNull() {

      /* ARRANGE :
       * compte d'abord (en SQL)
       * le nombre d'enregistrements dans le stockage
       * avant l'appel du service,
       * afin de pouvoir prouver ensuite
       * qu'aucune écriture n'a eu lieu dans le stockage.
       */
      final Long countAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      /* 
       * prépare un objet métier avec un libellé enfant null
       * et un parent persistant.
       */
      final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
      final SousTypeProduit stp =
              new SousTypeProduit(
                      Long.valueOf(1L),
                      null,
                      new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT));

      /* ACT - ASSERT :
       * garantit que service.update(stp)
       * - jette une ExceptionAppliLibelleBlank
       * - émet le message MESSAGE_UPDATE_KO_LIBELLE_BLANK
       * (message contractuel attendu).
       */
      assertThatThrownBy(() -> this.service.update(stp))
          .isInstanceOf(ExceptionAppliLibelleBlank.class)
          .hasMessage(SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_BLANK);

      /* ASSERT :
       * compte finalement (en SQL)
       * le nombre d'enregistrements dans le stockage
       * pour prouver que service.update(...)
       * n'a pas touché au stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      assertThat(countApres).isNotNull().isNotZero();
      assertThat(countApres).isEqualTo(countAvant);

  } // __________________________________________________________________
  


  /**
   * <div>
   * <p>garantit que update(blank) :</p>
   * <ul>
   * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
   * <li>émet le message
   * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_BLANK} ;</li>
   * <li>n'altère pas le stockage.</li>
   * </ul>
   * </div>
   */
  @Tag(TAG_UPDATE)
  @DisplayName(DN_UPDATE_BLANK)
  @Test
  public void testUpdateLibelleBlank() {

      /* ARRANGE :
       * compte d'abord (en SQL)
       * le nombre d'enregistrements dans le stockage
       * avant l'appel du service,
       * afin de pouvoir prouver ensuite
       * qu'aucune écriture n'a eu lieu dans le stockage.
       */
      final Long countAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      /* 
       * prépare un objet métier avec un libellé enfant blank
       * et un parent persistant.
       */
      final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
      final SousTypeProduit stp =
              new SousTypeProduit(
                      Long.valueOf(1L),
                      BLANK,
                      new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT));

      /* ACT - ASSERT :
       * garantit que service.update(stp)
       * - jette une ExceptionAppliLibelleBlank
       * - émet le message MESSAGE_UPDATE_KO_LIBELLE_BLANK
       * (message contractuel attendu).
       */
      assertThatThrownBy(() -> this.service.update(stp))
          .isInstanceOf(ExceptionAppliLibelleBlank.class)
          .hasMessage(SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_BLANK);

      /* ASSERT :
       * compte finalement (en SQL)
       * le nombre d'enregistrements dans le stockage
       * pour prouver que service.update(...)
       * n'a pas touché au stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
   * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_NON_PERSISTENT}
   * + {@link #LIBELLE_ENFANT_VETEMENT_HOMME} ;</li>
   * <li>n'altère pas le stockage.</li>
   * </ul>
   * </div>
   */
  @Tag(TAG_UPDATE)
  @DisplayName(DN_UPDATE_ID_NULL)
  @Test
  public void testUpdateIdNull() {

      /* ARRANGE :
       * compte d'abord (en SQL)
       * le nombre d'enregistrements dans le stockage
       * avant l'appel du service,
       * afin de pouvoir prouver ensuite
       * qu'aucune écriture n'a eu lieu dans le stockage.
       */
      final Long countAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      /* 
       * prépare un objet métier non persistant
       * car son identifiant est null.
       */
      final Long idParent 
      	= retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
      
      final SousTypeProduit stp =
              new SousTypeProduit(
                      null,
                      LIBELLE_ENFANT_VETEMENT_HOMME,
                      new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT));

      /* ACT - ASSERT :
       * garantit que service.update(stp)
       * - jette une ExceptionAppliParamNonPersistent
       * - émet le message MESSAGE_UPDATE_KO_NON_PERSISTENT
       * + LIBELLE_ENFANT_VETEMENT_HOMME
       * (message contractuel attendu).
       */
      assertThatThrownBy(() -> this.service.update(stp))
          .isInstanceOf(ExceptionAppliParamNonPersistent.class)
          .hasMessage(
                  SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_NON_PERSISTENT
                          + LIBELLE_ENFANT_VETEMENT_HOMME);

      /* ASSERT :
       * compte finalement (en SQL)
       * le nombre d'enregistrements dans le stockage
       * pour prouver que service.update(...)
       * n'a pas touché au stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
   * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_PARENT_NULL} ;</li>
   * <li>n'altère pas le stockage.</li>
   * </ul>
   * </div>
   */
  @Tag(TAG_UPDATE)
  @DisplayName(DN_UPDATE_PARENT_NULL)
  @Test
  public void testUpdateParentNull() {

      /* ARRANGE :
       * compte d'abord (en SQL)
       * le nombre d'enregistrements dans le stockage
       * avant l'appel du service,
       * afin de pouvoir prouver ensuite
       * qu'aucune écriture n'a eu lieu dans le stockage.
       */
      final Long countAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      /* 
       * prépare un objet métier sans parent.
       */
      final SousTypeProduit stp =
              new SousTypeProduit(Long.valueOf(1L), LIBELLE_ENFANT_VETEMENT_HOMME, null);

      /* ACT - ASSERT :
       * garantit que service.update(stp)
       * - jette une ExceptionAppliParentNull
       * - émet le message MESSAGE_UPDATE_KO_PARENT_NULL
       * (message contractuel attendu).
       */
      assertThatThrownBy(() -> this.service.update(stp))
          .isInstanceOf(ExceptionAppliParentNull.class)
          .hasMessage(SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARENT_NULL);

      /* ASSERT :
       * compte finalement (en SQL)
       * le nombre d'enregistrements dans le stockage
       * pour prouver que service.update(...)
       * n'a pas touché au stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
   * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK} ;</li>
   * <li>n'altère pas le stockage.</li>
   * </ul>
   * </div>
   */
  @Tag(TAG_UPDATE)
  @DisplayName(DN_UPDATE_PARENT_LIBELLE_NULL)
  @Test
  public void testUpdateParentLibelleNull() {

      /* ARRANGE :
       * compte d'abord (en SQL)
       * le nombre d'enregistrements dans le stockage
       * avant l'appel du service,
       * afin de pouvoir prouver ensuite
       * qu'aucune écriture n'a eu lieu dans le stockage.
       */
      final Long countAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      /* 
       * prépare un objet métier dont le parent a un libellé null.
       */
      final SousTypeProduit stp =
              new SousTypeProduit(
                      Long.valueOf(1L),
                      LIBELLE_ENFANT_VETEMENT_HOMME,
                      new TypeProduit(Long.valueOf(1L), null));

      /* ACT - ASSERT :
       * garantit que service.update(stp)
       * - jette une ExceptionAppliLibelleBlank
       * - émet le message MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK
       * (message contractuel attendu).
       */
      assertThatThrownBy(() -> this.service.update(stp))
          .isInstanceOf(ExceptionAppliLibelleBlank.class)
          .hasMessage(SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK);

      /* ASSERT :
       * compte finalement (en SQL)
       * le nombre d'enregistrements dans le stockage
       * pour prouver que service.update(...)
       * n'a pas touché au stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
   * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK} ;</li>
   * <li>n'altère pas le stockage.</li>
   * </ul>
   * </div>
   */
  @Tag(TAG_UPDATE)
  @DisplayName(DN_UPDATE_PARENT_LIBELLE_BLANK)
  @Test
  public void testUpdateParentLibelleBlank() {

      /* ARRANGE :
       * compte d'abord (en SQL)
       * le nombre d'enregistrements dans le stockage
       * avant l'appel du service,
       * afin de pouvoir prouver ensuite
       * qu'aucune écriture n'a eu lieu dans le stockage.
       */
      final Long countAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      /* 
       * prépare un objet métier dont le parent a un libellé blank.
       */
      final SousTypeProduit stp =
              new SousTypeProduit(
                      Long.valueOf(1L),
                      LIBELLE_ENFANT_VETEMENT_HOMME,
                      new TypeProduit(Long.valueOf(1L), BLANK));

      /* ACT - ASSERT :
       * garantit que service.update(stp)
       * - jette une ExceptionAppliLibelleBlank
       * - émet le message MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK
       * (message contractuel attendu).
       */
      assertThatThrownBy(() -> this.service.update(stp))
          .isInstanceOf(ExceptionAppliLibelleBlank.class)
          .hasMessage(SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK);

      /* ASSERT :
       * compte finalement (en SQL)
       * le nombre d'enregistrements dans le stockage
       * pour prouver que service.update(...)
       * n'a pas touché au stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
   * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT}
   * + {@link #LIBELLE_PARENT_VETEMENT} ;</li>
   * <li>n'altère pas le stockage.</li>
   * </ul>
   * </div>
   */
  @Tag(TAG_UPDATE)
  @DisplayName(DN_UPDATE_PARENT_ID_NULL)
  @Test
  public void testUpdateParentIdNull() {

      /* ARRANGE :
       * compte d'abord (en SQL)
       * le nombre d'enregistrements dans le stockage
       * avant l'appel du service,
       * afin de pouvoir prouver ensuite
       * qu'aucune écriture n'a eu lieu dans le stockage.
       */
      final Long countAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      /* 
       * prépare un objet métier dont le parent
       * n'est pas persistant car son identifiant est null.
       */
      final SousTypeProduit stp =
              new SousTypeProduit(
                      Long.valueOf(1L),
                      LIBELLE_ENFANT_VETEMENT_HOMME,
                      new TypeProduit(null, LIBELLE_PARENT_VETEMENT));

      /* ACT - ASSERT :
       * garantit que service.update(stp)
       * - jette une ExceptionTechniqueGatewayNonPersistent
       * - émet le message MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT
       * + LIBELLE_PARENT_VETEMENT
       * (message contractuel attendu).
       */
      assertThatThrownBy(() -> this.service.update(stp))
          .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
          .hasMessage(
                  SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT
                          + LIBELLE_PARENT_VETEMENT);

      /* ASSERT :
       * compte finalement (en SQL)
       * le nombre d'enregistrements dans le stockage
       * pour prouver que service.update(...)
       * n'a pas touché au stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      assertThat(countApres).isNotNull().isNotZero();
      assertThat(countApres).isEqualTo(countAvant);

  } // __________________________________________________________________
  


  /**
   * <div>
   * <p>garantit que update(parent absent) :</p>
   * <ul>
   * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
   * <li>émet le message
   * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT}
   * + {@link #LIBELLE_PARENT_VETEMENT} ;</li>
   * <li>n'altère pas le stockage.</li>
   * </ul>
   * </div>
   */
  @Tag(TAG_UPDATE)
  @DisplayName(DN_UPDATE_PARENT_ABSENT)
  @Test
  public void testUpdateParentAbsent() {

      /* ARRANGE :
       * compte d'abord (en SQL)
       * le nombre d'enregistrements dans le stockage
       * avant l'appel du service,
       * afin de pouvoir prouver ensuite
       * qu'aucune écriture n'a eu lieu dans le stockage.
       */
      final Long countAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      /* 
       * Vérifie directement dans le stockage
       * qu'aucun parent ne porte l'identifiant ID_INEXISTANT.
       */
      final Long countParentStockage = this.jdbcTemplate.queryForObject(
              "SELECT COUNT(*) FROM TYPES_PRODUIT WHERE ID_TYPE_PRODUIT = ?",
              Long.class,
              ID_INEXISTANT);

      assertThat(countParentStockage).isNotNull().isZero();

      /* 
       * prépare un objet métier dont le parent
       * porte un identifiant inexistant,
       * donc absent du stockage.
       */
      final SousTypeProduit stp =
              new SousTypeProduit(
                      Long.valueOf(1L),
                      LIBELLE_ENFANT_VETEMENT_HOMME,
                      new TypeProduit(ID_INEXISTANT, LIBELLE_PARENT_VETEMENT));

      /* ACT - ASSERT :
       * garantit que service.update(stp)
       * - jette une ExceptionTechniqueGatewayNonPersistent
       * - émet le message MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT
       * + LIBELLE_PARENT_VETEMENT
       * (message contractuel attendu).
       */
      assertThatThrownBy(() -> this.service.update(stp))
          .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
          .hasMessage(
                  SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT
                          + LIBELLE_PARENT_VETEMENT);

      /* ASSERT :
       * compte finalement (en SQL)
       * le nombre d'enregistrements dans le stockage
       * pour prouver que service.update(...)
       * n'a pas touché au stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      assertThat(countApres).isNotNull().isNotZero();
      assertThat(countApres).isEqualTo(countAvant);

  } // __________________________________________________________________
  


  /**
   * <div>
   * <p>garantit que update(absent) :</p>
   * <ul>
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
       * compte d'abord (en SQL)
       * le nombre d'enregistrements dans le stockage
       * avant l'appel du service,
       * afin de pouvoir prouver ensuite
       * qu'aucune écriture n'a eu lieu dans le stockage.
       */
      final Long countAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      final Long idParent 
      	= retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);

      /* Crée un objet métier qui ne peut pas exister 
       * dans le stockage (ID inexistant)*/
      final SousTypeProduit stp =
              new SousTypeProduit(
                      ID_INEXISTANT,
                      LIBELLE_ENFANT_VETEMENT_HOMME,
                      new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT));

      /* 
       * vérifie (en SQL)
       * qu'aucun enregistrement dans le stockage
       * ne porte l'identifiant ID_INEXISTANT.
       */
      final Long countEnBase = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
              Long.class,
              ID_INEXISTANT);

      assertThat(countEnBase).isNotNull().isZero();

      /* Neutralise explicitement le contexte Hibernate
       * avant l'appel de lecture,
       * afin d'éviter tout raisonnement biaisé par le cache.
       */
      this.entityManager.clear();

      /* ACT :
       * appelle service.update(stp).
       */
      final SousTypeProduit retour = this.service.update(stp);

      /* ASSERT :
       * vérifie que service.update(stp) retourne null.
       */
      assertThat(retour).isNull();

      /* ASSERT :
       * compte finalement (en SQL)
       * le nombre d'enregistrements dans le stockage
       * pour prouver que service.update(...)
       * n'a pas touché au stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      assertThat(countApres).isNotNull().isNotZero();
      assertThat(countApres).isEqualTo(countAvant);

  } // __________________________________________________________________
  
   
   
  /**
   * <div>
   * <p>garantit que update(sans modification) :</p>
   * <ul>
   * <li>retourne un objet non null ;</li>
   * <li>retourne l'objet persistant inchangé ;</li>
   * <li>ne modifie ni le libellé ni le parent dans le stockage ;</li>
   * <li>reste cohérent avec une relecture par le service ;</li>
   * <li>n'ajoute ni ne supprime aucun enregistrement.</li>
   * </ul>
   * </div>
   *
   * @throws Exception
   */
  @Tag(TAG_UPDATE)
  @DisplayName(DN_UPDATE_SANS_MODIFICATION)
  @Test
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public void testUpdateSansModification() throws Exception {

      /* ARRANGE :
       * compte d'abord (en SQL)
       * le nombre d'enregistrements dans le stockage
       * avant l'appel du service,
       * afin de pouvoir prouver ensuite
       * qu'aucune écriture n'a eu lieu dans le stockage.
       */
      final Long countAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      /* OBJET METIER. */
      /* Trouve un objet métier LIBELLE_ENFANT_VETEMENT_HOMME 
       * existant dans le stockage. */
      final Long idEnfant =
              retrouverIdEnfantPersistantParLibelle(LIBELLE_ENFANT_VETEMENT_HOMME);

      /* 
       * Lit (en SQL) le libellé enfant dans le stockage
       * pour l'identifiant idEnfant.
       */
      final String libelleAvant = this.jdbcTemplate.queryForObject(
              SELECT_STP_FROM_STP_WHERE_ID,
              String.class,
              idEnfant);

      /* 
       * Assure que le libellé de l'objet métier de test
       * est LIBELLE_ENFANT_VETEMENT_HOMME dans le stockage.
       */
      assertThat(libelleAvant).isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);

      /* PARENT. */
      /* 
       * Lit (en SQL) l'identifiant du parent dans le stockage
       * pour l'objet métier de test.
       */
      final Long idParentAvant = this.jdbcTemplate.queryForObject(
              SELECT_TP_FROM_STP_WHERE_ID,
              Long.class,
              idEnfant);
      
      /* assure que le parent existe et est persistant dans le stockage. */
      assertThat(idParentAvant).isNotNull();

      /* 
       * Lit (en SQL) le libellé du parent dans le stockage
       * pour l'objet métier de test.
       */
      final String libelleParentAvant= this.jdbcTemplate.queryForObject(
              SELECT_PARAM_TP_FROM_TP_WHERE_ID, 
              String.class,
              idParentAvant); 
      
      /* 
       * Assure que le libellé du parent de l'objet métier de test
       * est LIBELLE_PARENT_VETEMENT dans le stockage.
       */
      assertThat(libelleParentAvant).isEqualTo(LIBELLE_PARENT_VETEMENT);

      /* OBJET METIER DE TEST. */
      /* Instancie un objet métier existant forcément dans le stockage. */
      final SousTypeProduit stp = new SousTypeProduit(
              idEnfant,
              libelleAvant,
              new TypeProduit(idParentAvant, LIBELLE_PARENT_VETEMENT));

      /* 
       * Neutralise explicitement le contexte Hibernate
       * avant l'appel service.update(stp),
       * afin d'éviter tout raisonnement biaisé par le cache.
       */
      this.entityManager.clear();

      /* ACT :
       * appelle service.update(stp)
       * sans modifier les données persistées.
       */
      final SousTypeProduit retour = this.service.update(stp);

      /* ASSERT :
       * vérifie que l'objet retourné est inchangé.
       */
      assertThat(retour).isNotNull();
      assertThat(retour.getIdSousTypeProduit()).isEqualTo(idEnfant);
      assertThat(retour.getSousTypeProduit()).isEqualTo(libelleAvant);
      assertThat(retour.getTypeProduit()).isNotNull();
      assertThat(retour.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentAvant);
      assertThat(retour.getTypeProduit().getTypeProduit()).isEqualTo(libelleParentAvant);

      /* 
       * Lit (en SQL) le libellé enfant dans le stockage
       * après service.update(stp).
       */
      final String libelleApres = this.jdbcTemplate.queryForObject(
              SELECT_STP_FROM_STP_WHERE_ID,
              String.class,
              idEnfant);

      /* 
       * Lit (en SQL) l'identifiant du parent dans le stockage
       * après service.update(stp).
       */
      final Long idParentApres = this.jdbcTemplate.queryForObject(
              SELECT_TP_FROM_STP_WHERE_ID,
              Long.class,
              idEnfant);

      final String libelleParentApres = this.jdbcTemplate.queryForObject(
              SELECT_PARAM_TP_FROM_TP_WHERE_ID,
              String.class,
              idParentApres);

      assertThat(libelleApres).isEqualTo(libelleAvant);
      assertThat(idParentApres).isEqualTo(idParentAvant);
      assertThat(libelleParentApres).isEqualTo(libelleParentAvant);

      /* 
       * Neutralise explicitement le contexte Hibernate
       * avant la relecture via service.findById(idEnfant).
       */
      this.entityManager.clear();

      /* Relit l'objet via le service. */
      final SousTypeProduit relu = this.service.findById(idEnfant);

      /* Assure que l'objet métier relu via this.service.findById(idEnfant) 
       * est toujours l'objet métier de test. */
      assertThat(relu).isNotNull();
      assertThat(relu.getIdSousTypeProduit()).isEqualTo(idEnfant);
      assertThat(relu.getSousTypeProduit()).isEqualTo(libelleAvant);
      assertThat(relu.getTypeProduit()).isNotNull();
      assertThat(relu.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentAvant);
      assertThat(relu.getTypeProduit().getTypeProduit()).isEqualTo(libelleParentAvant);

      /* ASSERT :
       * compte finalement (en SQL)
       * le nombre d'enregistrements dans le stockage
       * pour prouver que service.update(...)
       * n'a pas touché au stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      assertThat(countApres).isNotNull().isNotZero();
      assertThat(countApres).isEqualTo(countAvant);

  } // __________________________________________________________________
  
   
   
  /**
   * <div>
   * <p>garantit que update(nominal) :</p>
   * <ul>
   * <li>retourne un objet non null ;</li>
   * <li>conserve le même identifiant ;</li>
   * <li>modifie le libellé dans le stockage ;</li>
   * <li>rend la modification retrouvable via le service ;</li>
   * <li>n'ajoute ni ne supprime aucun enregistrement.</li>
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

      /* ARRANGE :
       * compte d'abord (en SQL)
       * le nombre d'enregistrements dans le stockage
       * avant l'appel du service,
       * afin de pouvoir prouver ensuite
       * qu'aucune création ni suppression n'a eu lieu dans le stockage.
       */
      final Long countAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      /* OBJET METIER. */
      /* Trouve un objet métier existant dans le stockage. */
      final Long idEnfant =
              retrouverIdEnfantPersistantParLibelle(LIBELLE_ENFANT_VETEMENT_FEMME);

      /* 
       * Lit (en SQL) le libellé enfant initial dans le stockage
       * pour l'identifiant idEnfant.
       */
      final String libelleAvant = this.jdbcTemplate.queryForObject(
              SELECT_STP_FROM_STP_WHERE_ID,
              String.class,
              idEnfant);

      /* 
       * Assure que le libellé initial de l'objet métier de test
       * est LIBELLE_ENFANT_VETEMENT_FEMME.
       */
      assertThat(libelleAvant).isEqualTo(LIBELLE_ENFANT_VETEMENT_FEMME);

      /* PARENT. */
      /* 
       * Lit (en SQL) l'identifiant du parent dans le stockage
       * pour l'identifiant idEnfant.
       */
      final Long idParentAvant = this.jdbcTemplate.queryForObject(
              SELECT_TP_FROM_STP_WHERE_ID,
              Long.class,
              idEnfant);

      /* assure que le parent existe et est persistant dans le stockage. */
      assertThat(idParentAvant).isNotNull();
      /* 
       * Lit (en SQL) le libellé du parent dans le stockage
       * pour l'objet métier de test.
       */
      final String libelleParentAvant= this.jdbcTemplate.queryForObject(
   		   SELECT_PARAM_TP_FROM_TP_WHERE_ID, 
   		   String.class
   		   , idParentAvant); 
      
      /* 
       * Assure que le libellé du parent de l'objet métier de test
       * est LIBELLE_PARENT_VETEMENT dans le stockage.
       */
      assertThat(libelleParentAvant).isEqualTo(LIBELLE_PARENT_VETEMENT);

      /* OBJET METIER DE TEST. */
      /* 
       * prépare un objet métier existant
       * avec un nouveau libellé enfant LIBELLE_MODIFIE_FEMME
       * et le même parent.
       */
      final SousTypeProduit stp =
              new SousTypeProduit(
                      idEnfant,
                      LIBELLE_MODIFIE_FEMME,
                      new TypeProduit(idParentAvant, LIBELLE_PARENT_VETEMENT));

      /* 
       * Neutralise explicitement le contexte Hibernate
       * avant l'appel service.update(stp),
       * afin d'éviter tout raisonnement biaisé par le cache.
       */
      this.entityManager.clear();

      /* ACT :
       * appelle service.update(stp).
       */
      final SousTypeProduit retour = this.service.update(stp);

      /* ASSERT :
       * vérifie que l'objet retourné est non null,
       * conserve le même identifiant,
       * porte le libellé modifié LIBELLE_MODIFIE_FEMME
       * et conserve le même parent.
       */
      assertThat(retour).isNotNull();
      assertThat(retour.getIdSousTypeProduit()).isEqualTo(idEnfant);
      assertThat(retour.getSousTypeProduit()).isEqualTo(LIBELLE_MODIFIE_FEMME);
      assertThat(retour.getTypeProduit()).isNotNull();
      assertThat(retour.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentAvant);
      assertThat(retour.getTypeProduit().getTypeProduit()).isEqualTo(libelleParentAvant);

      /* 
       * Lit (en SQL) le libellé enfant dans le stockage
       * après service.update(stp).
       */
      final String libelleEnBase = this.jdbcTemplate.queryForObject(
              SELECT_STP_FROM_STP_WHERE_ID,
              String.class,
              idEnfant);

      /* 
       * Lit (en SQL) l'identifiant du parent dans le stockage
       * après service.update(stp).
       */
      final Long idParentEnBase = this.jdbcTemplate.queryForObject(
              SELECT_TP_FROM_STP_WHERE_ID,
              Long.class,
              idEnfant);

      final String libelleParentStockage = this.jdbcTemplate.queryForObject(
              SELECT_PARAM_TP_FROM_TP_WHERE_ID,
              String.class,
              idParentEnBase);

      /* Assure que le libellé de l'objet métier 
       * a été modifié dans le stockage. */
      assertThat(libelleEnBase).isEqualTo(LIBELLE_MODIFIE_FEMME);
      assertThat(idParentEnBase).isEqualTo(idParentAvant);
      assertThat(libelleParentStockage).isEqualTo(libelleParentAvant);

      /* 
       * Neutralise explicitement le contexte Hibernate
       * avant la relecture via service.findById(idEnfant).
       */
      this.entityManager.clear();

      /* Relit l'objet via le service. */
      final SousTypeProduit relu = this.service.findById(idEnfant);

      /* Garantit que l'objet modifié relu est bien l'objet métier 
       * modifié de test. */
      assertThat(relu).isNotNull();
      assertThat(relu.getIdSousTypeProduit()).isEqualTo(idEnfant);
      assertThat(relu.getSousTypeProduit()).isEqualTo(LIBELLE_MODIFIE_FEMME);
      assertThat(relu.getTypeProduit()).isNotNull();
      assertThat(relu.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentAvant);
      assertThat(relu.getTypeProduit().getTypeProduit()).isEqualTo(libelleParentAvant);

      /* ASSERT :
       * compte finalement (en SQL)
       * le nombre d'enregistrements dans le stockage
       * pour prouver que service.update(...)
       * n'a créé ni supprimé aucun enregistrement.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      assertThat(countApres).isNotNull().isNotZero();
      assertThat(countApres).isEqualTo(countAvant);

  } // __________________________________________________________________
  

     
  /**
   * <div>
   * <p>garantit que update(parent modifié) :</p>
   * <ul>
   * <li>retourne un objet métier non null ;</li>
   * <li>conserve le même identifiant pour cet objet métier;</li>
   * <li>conserve le même libellé de l'objet métier (enfant) ;</li>
   * <li>modifie le parent de l'objet métier dans le stockage ;</li>
   * <li>rend la modification retrouvable via le service ;</li>
   * <li>n'ajoute ni ne supprime aucun enregistrement 
   * dans le stockage.</li>
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

      /* ARRANGE :
       * compte d'abord (en SQL)
       * le nombre d'enregistrements dans le stockage des objets métier
       * avant l'appel du service,
       * afin de pouvoir prouver ensuite
       * qu'aucune création ni suppression n'a eu lieu
       * dans ce stockage.
       */
      final Long countAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      /* OBJET METIER. */
      /* Trouve un objet métier existant dans le stockage. */
      final Long idEnfant =
              retrouverIdEnfantPersistantParLibelle(
           		   LIBELLE_ENFANT_VETEMENT_HOMME);

      /* 
       * Lit (en SQL) le libellé enfant initial dans le stockage
       * pour l'identifiant idEnfant.
       */
      final String libelleAvant = this.jdbcTemplate.queryForObject(
              SELECT_STP_FROM_STP_WHERE_ID,
              String.class,
              idEnfant);

      /* 
       * Assure que le libellé initial de l'objet métier de test
       * est LIBELLE_ENFANT_VETEMENT_HOMME.
       */
      assertThat(libelleAvant).isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);

      /* PARENT ACTUEL. */
      /* 
       * Lit (en SQL) l'identifiant du parent actuel dans le stockage
       * pour l'objet métier de test.
       */
      final Long idParentAvant = this.jdbcTemplate.queryForObject(
              SELECT_TP_FROM_STP_WHERE_ID,
              Long.class,
              idEnfant);

      /* assure que le parent actuel existe 
       * et est persistant dans le stockage. */
      assertThat(idParentAvant).isNotNull();

      /* 
       * Lit (en SQL) le libellé du parent actuel dans le stockage
       * pour l'objet métier de test.
       */
      final String libelleParentAvant = this.jdbcTemplate.queryForObject(
              SELECT_PARAM_TP_FROM_TP_WHERE_ID,
              String.class,
              idParentAvant);

      /* 
       * Assure que le libellé du parent actuel de l'objet métier de test
       * est LIBELLE_PARENT_VETEMENT dans le stockage.
       */
      assertThat(libelleParentAvant).isEqualTo(LIBELLE_PARENT_VETEMENT);

      /* NOUVEAU PARENT. */
      /* 
       * Crée (en SQL) un nouveau parent persistant dans le stockage.
       */
      this.jdbcTemplate.update(
   		   INSERT_PARAM_INTO_TP,
              LIBELLE_PARENT_CHAUSSURE);

      final Long idNouveauParent =
              retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_CHAUSSURE);

      /* assure que :
       * - est persistant dans le stockage.
       * - est différent du parent précédent,
       */
      assertThat(idNouveauParent).isNotNull();
      assertThat(idNouveauParent).isNotEqualTo(idParentAvant);

      /* 
       * Lit (en SQL) le libellé du nouveau parent dans le stockage.
       */
      final String libelleNouveauParent = this.jdbcTemplate.queryForObject(
              SELECT_PARAM_TP_FROM_TP_WHERE_ID,
              String.class,
              idNouveauParent);

      /* 
       * Assure que le libellé du nouveau parent
       * est LIBELLE_PARENT_CHAUSSURE dans le stockage.
       */
      assertThat(libelleNouveauParent).isEqualTo(LIBELLE_PARENT_CHAUSSURE);

      /* OBJET METIER DE TEST. */
      /* 
       * prépare un objet métier existant
       * avec le même libellé enfant
       * mais avec un nouveau parent.
       */
      final SousTypeProduit stp =
              new SousTypeProduit(
                      idEnfant,
                      libelleAvant,
                      new TypeProduit(idNouveauParent, LIBELLE_PARENT_CHAUSSURE));

      /* 
       * Neutralise explicitement le contexte Hibernate
       * avant l'appel service.update(stp),
       * afin d'éviter tout raisonnement biaisé par le cache.
       */
      this.entityManager.clear();

      /* ACT :
       * appelle service.update(stp).
       */
      final SousTypeProduit retour = this.service.update(stp);

      /* ASSERT :
       * vérifie que l'objet retourné est non null,
       * conserve le même identifiant,
       * conserve le même libellé enfant
       * et porte le nouveau parent.
       */
      assertThat(retour).isNotNull();
      assertThat(retour.getIdSousTypeProduit()).isEqualTo(idEnfant);
      assertThat(retour.getSousTypeProduit()).isEqualTo(libelleAvant);
      assertThat(retour.getTypeProduit()).isNotNull();
      assertThat(retour.getTypeProduit().getIdTypeProduit()).isEqualTo(idNouveauParent);
      assertThat(retour.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_CHAUSSURE);

      /* 
       * Lit (en SQL) le libellé enfant dans le stockage
       * après service.update(stp).
       */
      final String libelleEnBase = this.jdbcTemplate.queryForObject(
              SELECT_STP_FROM_STP_WHERE_ID,
              String.class,
              idEnfant);

      /* 
       * Lit (en SQL) l'identifiant du parent dans le stockage
       * après service.update(stp).
       */
      final Long idParentEnBase = this.jdbcTemplate.queryForObject(
              SELECT_TP_FROM_STP_WHERE_ID,
              Long.class,
              idEnfant);

      /* Assure que le libellé enfant est resté inchangé dans le stockage. */
      assertThat(libelleEnBase).isEqualTo(libelleAvant);

      /* Assure que le parent a été modifié dans le stockage. */
      assertThat(idParentEnBase).isEqualTo(idNouveauParent);

      /* 
       * Neutralise explicitement le contexte Hibernate
       * avant la relecture via service.findById(idEnfant).
       */
      this.entityManager.clear();

      /* Relit l'objet via le service. */
      final SousTypeProduit relu = this.service.findById(idEnfant);

      /* Garantit que l'objet relu est bien l'objet métier de test
       * modifié avec son nouveau parent.
       */
      assertThat(relu).isNotNull();
      assertThat(relu.getIdSousTypeProduit()).isEqualTo(idEnfant);
      assertThat(relu.getSousTypeProduit()).isEqualTo(libelleAvant);
      assertThat(relu.getTypeProduit()).isNotNull();
      assertThat(relu.getTypeProduit().getIdTypeProduit()).isEqualTo(idNouveauParent);
      assertThat(relu.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_CHAUSSURE);

      /* ASSERT :
       * compte finalement (en SQL)
       * le nombre d'enregistrements dans le stockage des objets metier
       * pour prouver que service.update(...)
       * n'a créé ni supprimé aucun enregistrement
       * dans ce stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      assertThat(countApres).isNotNull().isNotZero();
      assertThat(countApres).isEqualTo(countAvant);

  } // __________________________________________________________________

  
  
  /**
   * <div>
   * <p>garantit que update(doublon métier) :</p>
   * <ul>
   * <li>refuse de modifier un objet métier vers un objet métier 
   * (couple parent + libellé enfant) déjà présent dans le stockage ;</li>
   * <li>jette une {@link DataIntegrityViolationException} ;</li>
   * <li>ne crée aucun nouvel enregistrement dans le stockage ;</li>
   * <li>conserve inchangé l'enregistrement source tenté en modification ;</li>
   * <li>conserve inchangé l'enregistrement cible déjà présent ;</li>
   * <li>n'ajoute ni ne supprime aucun enregistrement.</li>
   * </ul>
   * </div>
   *
   * @throws Exception
   */
  @Tag(TAG_UPDATE)
  @DisplayName(DN_UPDATE_DOUBLON_METIER)
  @Test
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public void testUpdateDoublonMetier() throws Exception {

      /* ARRANGE :
       * compte d'abord (en SQL)
       * le nombre d'enregistrements dans le stockage
       * avant l'appel du service,
       * afin de pouvoir prouver ensuite
       * que service.update(...)
       * n'a créé ni supprimé aucun enregistrement.
       */
      final Long countAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* vérifie que le stockage n'est pas vide. */
      assertThat(countAvant).isNotNull().isNotZero();

      /* PARENT. */
      /* Trouve un parent persistant dans le stockage. */
      final Long idParent =
              retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);

      /* CIBLE DOUBLON. */
      /*
       * Vérifie (en SQL) que le couple cible existe déjà
       * une seule fois dans le stockage :
       * - parent = LIBELLE_PARENT_VETEMENT
       * - enfant = LIBELLE_ENFANT_VETEMENT_HOMME
       */
      final Long countDoublonAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE_AND_PARENT,
              Long.class,
              LIBELLE_ENFANT_VETEMENT_HOMME,
              idParent);

      final Long idDoublonAvant = this.jdbcTemplate.queryForObject(
              SELECT_PARAM_ID_FROM_STP_WHERE_LIBELLE_AND_PARENT,
              Long.class,
              LIBELLE_ENFANT_VETEMENT_HOMME,
              idParent);

      assertThat(countDoublonAvant).isNotNull().isEqualTo(1L);
      assertThat(idDoublonAvant).isNotNull();

      /* SOURCE A MODIFIER. */
      /*
       * Vérifie (en SQL) qu'un autre objet métier du même parent
       * existe dans le stockage et peut servir de source de modification :
       * - parent = LIBELLE_PARENT_VETEMENT
       * - enfant = LIBELLE_ENFANT_VETEMENT_FEMME
       */
      final Long countSourceAvant = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE_AND_PARENT,
              Long.class,
              LIBELLE_ENFANT_VETEMENT_FEMME,
              idParent);

      final Long idSource = this.jdbcTemplate.queryForObject(
              SELECT_PARAM_ID_FROM_STP_WHERE_LIBELLE_AND_PARENT,
              Long.class,
              LIBELLE_ENFANT_VETEMENT_FEMME,
              idParent);

      assertThat(countSourceAvant).isNotNull().isEqualTo(1L);
      assertThat(idSource).isNotNull();
      assertThat(idSource).isNotEqualTo(idDoublonAvant);

      /*
       * Lit (en SQL) l'état initial de l'objet métier source
       * avant la tentative de modification.
       */
      final String libelleSourceAvant = this.jdbcTemplate.queryForObject(
              SELECT_STP_FROM_STP_WHERE_ID,
              String.class,
              idSource);

      final Long idParentSourceAvant = this.jdbcTemplate.queryForObject(
              SELECT_TP_FROM_STP_WHERE_ID,
              Long.class,
              idSource);

      assertThat(libelleSourceAvant).isEqualTo(LIBELLE_ENFANT_VETEMENT_FEMME);
      assertThat(idParentSourceAvant).isEqualTo(idParent);

      /*
       * Prépare un objet métier source portant son identifiant persistant,
       * mais avec le libellé enfant déjà utilisé par un autre objet métier
       * sous le même parent dans le stockage.
       */
      final SousTypeProduit aModifier = new SousTypeProduit(
              idSource,
              LIBELLE_ENFANT_VETEMENT_HOMME,
              new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT));

      /*
       * Neutralise explicitement le contexte Hibernate
       * avant l'appel service.update(aModifier),
       * afin d'éviter tout raisonnement biaisé par le cache.
       */
      this.entityManager.clear();

      /* ACT - ASSERT :
       * tente de modifier l'objet métier source
       * vers un objet métier (couple parent + libellé enfant) 
       * déjà présent dans le stockage.
       *
       * Le refus du doublon jette une DataIntegrityViolationException.
       */
      assertThatThrownBy(() -> this.service.update(aModifier))
          .isInstanceOf(DataIntegrityViolationException.class);

      /*
       * Neutralise explicitement le contexte Hibernate
       * après l'échec transactionnel,
       * avant les vérifications SQL et service.
       */
      this.entityManager.clear();

      /* ASSERT :
       * compte finalement (en SQL)
       * le nombre d'enregistrements dans le stockage.
       */
      final Long countApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
              Long.class);

      /* Assure que le nombre d'enregistrements dans le stockage 
       * n'a pas changé. */
      assertThat(countApres).isNotNull().isNotZero();
      assertThat(countApres).isEqualTo(countAvant);

      /*
       * Vérifie que le doublon cible de service.update(aModifier)
       * existe toujours une seule fois dans le stockage.
       */
      final Long countDoublonApres = this.jdbcTemplate.queryForObject(
              SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE_AND_PARENT,
              Long.class,
              LIBELLE_ENFANT_VETEMENT_HOMME,
              idParent);

      final Long idDoublonApres = this.jdbcTemplate.queryForObject(
              SELECT_PARAM_ID_FROM_STP_WHERE_LIBELLE_AND_PARENT,
              Long.class,
              LIBELLE_ENFANT_VETEMENT_HOMME,
              idParent);

      assertThat(countDoublonApres).isNotNull().isEqualTo(1L);
      assertThat(idDoublonApres).isNotNull();
      assertThat(idDoublonApres).isEqualTo(idDoublonAvant);

      /*
       * Vérifie que l'objet métier source
       * est resté inchangé dans le stockage.
       */
      final String libelleSourceApres = this.jdbcTemplate.queryForObject(
              SELECT_STP_FROM_STP_WHERE_ID,
              String.class,
              idSource);

      final Long idParentSourceApres = this.jdbcTemplate.queryForObject(
              SELECT_TP_FROM_STP_WHERE_ID,
              Long.class,
              idSource);

      assertThat(libelleSourceApres).isEqualTo(libelleSourceAvant);
      assertThat(idParentSourceApres).isEqualTo(idParentSourceAvant);

      /*
       * Vérifie enfin que l'objet métier source
       * reste retrouvable via le service dans son état initial.
       */
      final SousTypeProduit relu = this.service.findById(idSource);

      assertThat(relu).isNotNull();
      assertThat(relu.getIdSousTypeProduit()).isEqualTo(idSource);
      assertThat(relu.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_VETEMENT_FEMME);
      assertThat(relu.getTypeProduit()).isNotNull();
      assertThat(relu.getTypeProduit().getIdTypeProduit()).isEqualTo(idParent);

  } // __________________________________________________________________
   

    

  // ============================= delete ===============================



 /**
  * <div>
  * <p>garantit que delete(null) :</p>
  * <ul>
  * <li>jette une {@link ExceptionAppliParamNull} ;</li>
  * <li>émet le message
  * {@link SousTypeProduitGatewayIService#MESSAGE_DELETE_KO_PARAM_NULL} ;</li>
  * <li>n'altère pas le stockage.</li>
  * </ul>
  * </div>
  */
 @Tag(TAG_DELETE)
 @DisplayName(DN_DELETE_NULL)
 @Test
 public void testDeleteNull() {

     /* ARRANGE :
      * compte d'abord (en SQL)
      * le nombre d'enregistrements dans le stockage
      * avant l'appel du service,
      * afin de pouvoir prouver ensuite
      * qu'aucune écriture n'a eu lieu dans le stockage.
      */
     final Long countAvant = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
             Long.class);

     /* vérifie que le stockage n'est pas vide. */
     assertThat(countAvant).isNotNull().isNotZero();

     /* ACT - ASSERT :
      * garantit que service.delete(null)
      * - jette une ExceptionAppliParamNull
      * - émet le message MESSAGE_DELETE_KO_PARAM_NULL
      * (message contractuel attendu).
      */
     assertThatThrownBy(() -> this.service.delete(null))
         .isInstanceOf(ExceptionAppliParamNull.class)
         .hasMessage(SousTypeProduitGatewayIService.MESSAGE_DELETE_KO_PARAM_NULL);

     /* ASSERT :
      * compte finalement (en SQL)
      * le nombre d'enregistrements dans le stockage
      * pour prouver que service.delete(...)
      * n'a pas touché au stockage.
      */
     final Long countApres = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
  * {@link SousTypeProduitGatewayIService#MESSAGE_DELETE_KO_ID_NULL} ;</li>
  * <li>n'altère pas le stockage.</li>
  * </ul>
  * </div>
  */
 @Tag(TAG_DELETE)
 @DisplayName(DN_DELETE_ID_NULL)
 @Test
 public void testDeleteIdNull() {

     /* ARRANGE :
      * compte d'abord (en SQL)
      * le nombre d'enregistrements dans le stockage
      * avant l'appel du service,
      * afin de pouvoir prouver ensuite
      * qu'aucune écriture n'a eu lieu dans le stockage.
      */
     final Long countAvant = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
             Long.class);

     /* vérifie que le stockage n'est pas vide. */
     assertThat(countAvant).isNotNull().isNotZero();

     /* 
      * prépare un objet métier non persistant
      * car son identifiant est null.
      */
     final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
     final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
     final SousTypeProduit stp = new SousTypeProduit(null, LIBELLE_A_SUPPRIMER, parent);

     /* ACT - ASSERT :
      * garantit que service.delete(stp)
      * - jette une ExceptionAppliParamNonPersistent
      * - émet le message MESSAGE_DELETE_KO_ID_NULL
      * (message contractuel attendu).
      */
     assertThatThrownBy(() -> this.service.delete(stp))
         .isInstanceOf(ExceptionAppliParamNonPersistent.class)
         .hasMessage(SousTypeProduitGatewayIService.MESSAGE_DELETE_KO_ID_NULL);

     /* ASSERT :
      * compte finalement (en SQL)
      * le nombre d'enregistrements dans le stockage
      * pour prouver que service.delete(...)
      * n'a pas touché au stockage.
      */
     final Long countApres = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
             Long.class);

     assertThat(countApres).isNotNull().isNotZero();
     assertThat(countApres).isEqualTo(countAvant);

 } // __________________________________________________________________
 


 /**
  * <div>
  * <p>garantit que delete(absent) :</p>
  * <ul>
  * <li>ne fait rien ;</li>
  * <li>ne jette pas d'exception ;</li>
  * <li>laisse l'identifiant absent dans le stockage ;</li>
  * <li>n'altère pas le stockage.</li>
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
      * compte d'abord (en SQL)
      * le nombre d'enregistrements dans le stockage
      * avant l'appel du service,
      * afin de pouvoir prouver ensuite
      * qu'aucune écriture n'a eu lieu dans le stockage.
      */
     final Long countAvant = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
             Long.class);

     /* vérifie que le stockage n'est pas vide. */
     assertThat(countAvant).isNotNull().isNotZero();

     /* 
      * vérifie (en SQL)
      * qu'aucun enregistrement dans le stockage
      * ne porte l'identifiant ID_INEXISTANT.
      */
     final Long countAvantDelete = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
             Long.class,
             ID_INEXISTANT);

     assertThat(countAvantDelete).isNotNull().isZero();

     /* 
      * prépare un objet métier absent du stockage.
      */
     final SousTypeProduit stpAbsent =
             new SousTypeProduit(ID_INEXISTANT, LIBELLE_INEXISTANT, null);

     /* 
      * Neutralise explicitement le contexte Hibernate
      * avant l'appel de suppression,
      * afin d'éviter tout raisonnement biaisé par le cache.
      */
     this.entityManager.clear();

     /* ACT :
      * appelle service.delete(stpAbsent)
      * sur un objet métier absent du stockage.
      */
     this.service.delete(stpAbsent);

     /* 
      * vérifie (en SQL)
      * que l'identifiant ID_INEXISTANT
      * reste absent du stockage après l'appel.
      */
     final Long countApresDelete = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
             Long.class,
             ID_INEXISTANT);

     assertThat(countApresDelete).isNotNull().isZero();

     /* ASSERT :
      * compte finalement (en SQL)
      * le nombre d'enregistrements dans le stockage
      * pour prouver que service.delete(stpAbsent)
      * n'a pas touché au stockage.
      */
     final Long countApres = this.jdbcTemplate.queryForObject(
             SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
             Long.class);

     assertThat(countApres).isNotNull().isNotZero();
     assertThat(countApres).isEqualTo(countAvant);

 } // __________________________________________________________________
 


 /**
  * <div>
  * <p>garantit que delete(OK) :</p>
  * <ul>
  * <li>crée un objet métier dédié au test ;</li>
  * <li>prouve physiquement son libellé enfant et son parent dans le stockage ;</li>
  * <li>supprime réellement cet enregistrement dans le stockage ;</li>
  * <li>rend l'objet métier supprimé introuvable via le service ;</li>
  * <li>ramène le nombre total d'enregistrements à son niveau initial.</li>
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

     Long idSupprime = null;

     try {

         /* ARRANGE :
          * compte d'abord (en SQL)
          * le nombre d'enregistrements dans le stockage
          * avant la création de l'objet métier dédié au test,
          * afin de pouvoir prouver ensuite
          * que service.delete(...)
          * ramène le stockage à son état initial.
          */
         final Long countAvant = this.jdbcTemplate.queryForObject(
                 SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                 Long.class);

         /* vérifie que le stockage n'est pas vide. */
         assertThat(countAvant).isNotNull().isNotZero();

         /* 
          * Trouve un parent persistant dans le stockage
          * pour créer l'objet métier dédié au test.
          */
         final Long idParent =
                 retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);

         final TypeProduit parent =
                 new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);

         /* 
          * Crée un objet métier dédié au test
          * via un appel à service.creer(...).
          */
         final SousTypeProduit cree = this.service.creer(
                 new SousTypeProduit(null, LIBELLE_A_SUPPRIMER, parent));

         /* Assure que l'objet métier de test a été créé. */
         assertThat(cree).isNotNull();
         assertThat(cree.getIdSousTypeProduit()).isNotNull().isPositive();
         assertThat(cree.getSousTypeProduit()).isEqualTo(LIBELLE_A_SUPPRIMER);
         assertThat(cree.getTypeProduit()).isNotNull();
         assertThat(cree.getTypeProduit().getIdTypeProduit()).isEqualTo(idParent);
         assertThat(cree.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_VETEMENT);

         idSupprime = cree.getIdSousTypeProduit();

         /* 
          * Lit (en SQL) le nombre d'enregistrements dans le stockage
          * après la création de l'objet métier dédié au test.
          */
         final Long countApresCreation = this.jdbcTemplate.queryForObject(
                 SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                 Long.class);

         /* 
          * Lit (en SQL) le nombre d'enregistrements dans le stockage
          * portant l'identifiant créé.
          */
         final Long countLigneAvantDelete = this.jdbcTemplate.queryForObject(
                 SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                 Long.class,
                 idSupprime);

         /*
          * Lit (en SQL) le libellé enfant réellement stocké
          * pour l'objet métier dédié au test.
          */
         final String libelleStockageAvantDelete = this.jdbcTemplate.queryForObject(
                 SELECT_STP_FROM_STP_WHERE_ID,
                 String.class,
                 idSupprime);

         /*
          * Lit (en SQL) la clé étrangère parent réellement stockée
          * pour l'objet métier dédié au test.
          */
         final Long idParentStockageAvantDelete = this.jdbcTemplate.queryForObject(
                 SELECT_TP_FROM_STP_WHERE_ID,
                 Long.class,
                 idSupprime);

         /*
          * Lit (en SQL) le libellé du parent réellement stocké
          * pour l'objet métier dédié au test.
          */
         final String libelleParentStockageAvantDelete = this.jdbcTemplate.queryForObject(
                 SELECT_PARAM_TP_FROM_TP_WHERE_ID,
                 String.class,
                 idParentStockageAvantDelete);

         /* Assure que service.creer(...) a ajouté un enregistrement. */
         assertThat(countApresCreation).isNotNull().isEqualTo(countAvant + 1L);

         /* Assure que l'objet métier de test à supprimer
          * est unique dans le stockage. */
         assertThat(countLigneAvantDelete).isNotNull().isEqualTo(1L);

         /* Assure que le libellé enfant stocké
          * correspond à l'objet métier dédié au test. */
         assertThat(libelleStockageAvantDelete).isEqualTo(LIBELLE_A_SUPPRIMER);

         /* Assure que la clé étrangère parent stockée
          * correspond au parent attendu. */
         assertThat(idParentStockageAvantDelete).isNotNull().isEqualTo(idParent);

         /* Assure que le libellé parent stocké
          * correspond au parent attendu. */
         assertThat(libelleParentStockageAvantDelete).isEqualTo(LIBELLE_PARENT_VETEMENT);

         /* 
          * Neutralise explicitement le contexte Hibernate
          * avant l'appel de suppression,
          * afin d'éviter tout raisonnement biaisé par le cache.
          */
         this.entityManager.clear();

         /* ACT :
          * appelle service.delete(cree)
          * sur l'objet métier créé pour le test.
          */
         this.service.delete(cree);

         /* 
          * Neutralise explicitement le contexte Hibernate
          * avant la vérification de suppression,
          * afin d'éviter tout raisonnement biaisé par le cache.
          */
         this.entityManager.clear();

         /* ASSERT :
          * vérifie que service.delete(...)
          * a supprimé l'enregistrement créé dans le stockage
          * et que l'objet métier est introuvable via le service.
          */
         this.verifierSuppressionDansStockage(idSupprime);

         /* 
          * Compte finalement (en SQL)
          * le nombre d'enregistrements dans le stockage
          * après la suppression.
          */
         final Long countApresDelete = this.jdbcTemplate.queryForObject(
                 SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                 Long.class);

         /* Assure que le nombre d'enregistrements dans le stockage
          * après la destruction de l'objet métier de test
          * est revenu à sa valeur initiale. */
         assertThat(countApresDelete).isNotNull().isNotZero();
         assertThat(countApresDelete).isEqualTo(countAvant);

     } finally {

         /*
          * Nettoyage de sécurité en SQL :
          * supprime l'enregistrement s'il existe encore.
          */
         if (idSupprime != null) {
             final Integer countRestant = this.jdbcTemplate.queryForObject(
                     SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                     Integer.class,
                     idSupprime);

             if (countRestant != null && countRestant.intValue() > 0) {
                 this.jdbcTemplate.update(
                         DELETE_FROM_STP_WHERE_ID_STP,
                         idSupprime);
             }
         }

     }

 } // __________________________________________________________________
 


 /**
  * <div>
  * <p>garantit que delete(double suppression) :</p>
  * <ul>
  * <li>supprime réellement l'enregistrement lors du premier appel ;</li>
  * <li>ne lève pas d'exception lors du second appel ;</li>
  * <li>ne recrée rien dans le stockage ;</li>
  * <li>laisse le nombre total d'enregistrements stable après le second appel.</li>
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

     Long idSupprime = null;

     try {

         /* ARRANGE :
          * compte d'abord (en SQL)
          * le nombre d'enregistrements dans le stockage
          * avant la création de l'objet métier dédié au test,
          * afin de pouvoir prouver ensuite
          * que le premier service.delete(...)
          * ramène le stockage à son état initial.
          */
         final Long countAvant = this.jdbcTemplate.queryForObject(
                 SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                 Long.class);

         /* vérifie que le stockage n'est pas vide. */
         assertThat(countAvant).isNotNull().isNotZero();

         /* PARENT. */
         /*
          * Retrouve un parent forcément existant dans le stockage.
          */
         final Long idParent =
                 retrouverIdParentPersistantParLibelle(
                         LIBELLE_PARENT_VETEMENT);

         final TypeProduit parent =
                 new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);

         /* OBJET METIER. */
         /*
          * Crée un objet métier persistant à supprimer
          * dédié au test.
          */
         final SousTypeProduit cree = this.service.creer(
                 new SousTypeProduit(null, LIBELLE_A_SUPPRIMER, parent));

         assertThat(cree).isNotNull();
         assertThat(cree.getIdSousTypeProduit()).isNotNull().isPositive();
         assertThat(cree.getSousTypeProduit()).isEqualTo(LIBELLE_A_SUPPRIMER);
         assertThat(cree.getTypeProduit()).isNotNull();
         assertThat(cree.getTypeProduit().getIdTypeProduit()).isEqualTo(idParent);
         assertThat(cree.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_VETEMENT);

         idSupprime = cree.getIdSousTypeProduit();

         /*
          * Lit (en SQL) le nombre d'enregistrements dans le stockage
          * après la création de l'objet métier dédié au test.
          */
         final Long countApresCreation = this.jdbcTemplate.queryForObject(
                 SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                 Long.class);

         /*
          * Lit (en SQL) le nombre d'enregistrements dans le stockage
          * portant l'identifiant créé.
          */
         final Long countLigneAvantDelete = this.jdbcTemplate.queryForObject(
                 SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                 Long.class,
                 idSupprime);

         /*
          * Lit (en SQL) le libellé enfant réellement stocké
          * pour l'objet métier dédié au test.
          */
         final String libelleStockageAvantDelete = this.jdbcTemplate.queryForObject(
                 SELECT_STP_FROM_STP_WHERE_ID,
                 String.class,
                 idSupprime);

         /*
          * Lit (en SQL) la clé étrangère parent réellement stockée
          * pour l'objet métier dédié au test.
          */
         final Long idParentStockageAvantDelete = this.jdbcTemplate.queryForObject(
                 SELECT_TP_FROM_STP_WHERE_ID,
                 Long.class,
                 idSupprime);

         /*
          * Lit (en SQL) le libellé du parent réellement stocké
          * pour l'objet métier dédié au test.
          */
         final String libelleParentStockageAvantDelete = this.jdbcTemplate.queryForObject(
                 SELECT_PARAM_TP_FROM_TP_WHERE_ID,
                 String.class,
                 idParentStockageAvantDelete);

         /* Assure que l'objet métier de test
          * a été créé dans le stockage. */
         assertThat(countApresCreation).isNotNull().isEqualTo(countAvant + 1L);
         assertThat(countLigneAvantDelete).isNotNull().isEqualTo(1L);

         /* Assure que le libellé enfant stocké
          * correspond à l'objet métier dédié au test. */
         assertThat(libelleStockageAvantDelete).isEqualTo(LIBELLE_A_SUPPRIMER);

         /* Assure que la clé étrangère parent stockée
          * correspond au parent attendu. */
         assertThat(idParentStockageAvantDelete).isNotNull().isEqualTo(idParent);

         /* Assure que le libellé parent stocké
          * correspond au parent attendu. */
         assertThat(libelleParentStockageAvantDelete).isEqualTo(LIBELLE_PARENT_VETEMENT);

         /*
          * Neutralise explicitement le contexte Hibernate
          * avant le premier appel de suppression,
          * afin d'éviter tout raisonnement biaisé par le cache.
          */
         this.entityManager.clear();

         /* ACT :
          * appelle une première fois service.delete(cree)
          * sur l'objet métier créé pour le test.
          */
         this.service.delete(cree);

         /*
          * Neutralise explicitement le contexte Hibernate
          * avant la vérification de suppression,
          * afin d'éviter tout raisonnement biaisé par le cache.
          */
         this.entityManager.clear();

         /* ASSERT :
          * vérifie la suppression réelle dans le stockage.
          */
         this.verifierSuppressionDansStockage(idSupprime);

         /*
          * Compte (en SQL) le nombre d'enregistrements
          * dans le stockage après la première suppression.
          */
         final Long countApresPremiereSuppression =
                 this.jdbcTemplate.queryForObject(
                         SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                         Long.class);

         /* Assure que le premier appel à service.delete(cree)
          * a supprimé l'objet métier de test du stockage. */
         assertThat(countApresPremiereSuppression).isNotNull().isNotZero();
         assertThat(countApresPremiereSuppression).isEqualTo(countAvant);

         /* ACT :
          * appelle une seconde fois service.delete(cree)
          * sur le même objet déjà supprimé.
          */
         this.service.delete(cree);

         /*
          * Neutralise explicitement le contexte Hibernate
          * avant la seconde vérification de suppression,
          * afin d'éviter tout raisonnement biaisé par le cache.
          */
         this.entityManager.clear();

         /* ASSERT :
          * vérifie que l'enregistrement reste absent du stockage
          * après le second appel.
          */
         this.verifierSuppressionDansStockage(idSupprime);

         /*
          * Compte finalement (en SQL)
          * le nombre d'enregistrements dans le stockage
          * après la seconde suppression.
          */
         final Long countApresSecondeSuppression =
                 this.jdbcTemplate.queryForObject(
                         SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                         Long.class);

         /* Assure que le deuxième appel à service.delete(cree)
          * ne modifie pas le nombre d'enregistrements dans le stockage. */
         assertThat(countApresSecondeSuppression).isNotNull().isNotZero();
         assertThat(countApresSecondeSuppression)
             .isEqualTo(countApresPremiereSuppression);

     } finally {

         /* Nettoyage de sécurité :
          * supprime l'enregistrement s'il existe encore dans le stockage.
          */
         if (idSupprime != null) {
             final Integer countRestant = this.jdbcTemplate.queryForObject(
                     SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                     Integer.class,
                     idSupprime);

             if (countRestant != null && countRestant.intValue() > 0) {
                 this.jdbcTemplate.update(
                         DELETE_FROM_STP_WHERE_ID_STP,
                         idSupprime);
             }
         }

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
	@DisplayName("count(stockage vide) - retourne 0")
	@Test
	@Sql(
	    scripts = SousTypeProduitGatewayJPAServiceIntegrationTest.CLASSPATH_TRUNCATE_SQL,
	    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
	)
	public void testCountStockageVide() throws Exception {
	
	    /* ARRANGE :
	     * lit (en SQL) le nombre d'enregistrements
	     * dans le stockage.
	     */
	    final Long countStockage = this.jdbcTemplate.queryForObject(
	            SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
	    final long viaDao = this.sousTypeProduitDaoJPA.count();
	    final List<SousTypeProduit> liste = this.service.rechercherTous();
	
	    /* Vérifie que count() retourne 0. */
	    assertThat(viaService).isZero();
	
	    /* Vérifie que la méthode publique rechercherTous()
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
	            SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
	    final long viaDao = this.sousTypeProduitDaoJPA.count();
	    final List<SousTypeProduit> liste = this.service.rechercherTous();
	
	    /* ASSERT :
	     * vérifie que count() retourne
	     * un total strictement positif.
	     */
	    assertThat(viaService).isPositive();
	
	    /* Vérifie que la méthode publique rechercherTous()
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
	 * <p>garantit que count() suit l'état du stockage :</p>
	 * <ul>
	 * <li>retourne le total initial du stockage ;</li>
	 * <li>augmente de 1 après creer(...) ;</li>
	 * <li>prouve physiquement la ligne créée dans le stockage ;</li>
	 * <li>revient au total initial après delete(...) ;</li>
	 * <li>prouve physiquement l'absence de la ligne supprimée
	 * dans le stockage ;</li>
	 * <li>reste cohérent avec les lectures SQL directes ;</li>
	 * <li>reste cohérent avec la méthode publique rechercherTous().</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_COUNT)
	@DisplayName("count(après création puis suppression) - suit l'état du stockage")
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
	                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
	                Long.class);
	
	        /* vérifie que le stockage n'est pas vide. */
	        assertThat(countAvantSql).isNotNull().isNotZero();
	
	        /* 
	         * Neutralise explicitement le contexte Hibernate
	         * avant les lectures initiales via service.count()
	         * et service.rechercherTous().
	         */
	        this.entityManager.clear();
	
	        /* Lit le nombre d'enregistrements
	         * dans le stockage via le service
	         * et via la méthode publique rechercherTous().
	         */
	        final long countAvantService = this.service.count();
	        final List<SousTypeProduit> listeAvant = this.service.rechercherTous();
	
	        /* Garantit que service.count() retourne le même 
	         * nombre d'enregistrements que la requête SQL directe
	         * et que la méthode publique rechercherTous().
	         */
	        assertThat(listeAvant).isNotNull().isNotEmpty();
	        assertThat(countAvantService).isEqualTo(countAvantSql.longValue());
	        assertThat(countAvantService).isEqualTo(listeAvant.size());
	
	        
	        /* ****** CREATION D'UN OBJET METIER *******/
	        /* Recherche un parent forcément existant dans le stockage. */
	        final Long idParent =
	                retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
	
	        final TypeProduit parent =
	                new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
	
	        /* ACT :
	         * appelle service.creer(...)
	         * pour ajouter un nouvel objet métier dans le stockage.
	         */
	        final SousTypeProduit cree = this.service.creer(
	                new SousTypeProduit(null, LIBELLE_A_SUPPRIMER, parent));
	
	        /* Assure que l'objet métier a été créé dans le stockage. */
	        assertThat(cree).isNotNull();
	        assertThat(cree.getIdSousTypeProduit()).isNotNull();
	
	        idCree = cree.getIdSousTypeProduit();
	
	        /* ASSERT :
	         * lit (en SQL) le nombre d'enregistrements
	         * dans le stockage après création.
	         */
	        final Long countApresCreationSql = this.jdbcTemplate.queryForObject(
	                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
	                Long.class);
	
	        /* Lit (en SQL) le nombre d'enregistrements
	         * portant l'identifiant nouvellement créé.
	         */
	        final Long countLigneApresCreation = this.jdbcTemplate.queryForObject(
	                SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
	                Long.class,
	                idCree);
	
	        /* 
	         * Neutralise explicitement le contexte Hibernate
	         * avant les lectures après création via service.count()
	         * et service.rechercherTous().
	         */
	        this.entityManager.clear();
	
	        /* Lit le nombre d'enregistrements
	         * dans le stockage via le service après création.
	         */
	        final long countApresCreationService = this.service.count();
	        final List<SousTypeProduit> listeApresCreation = this.service.rechercherTous();
	
	        /* Vérifie que service.count() retourne un nombre 
	         * d'enregistrements dans le stockage 
	         * incrémenté de 1 après une création.
	         */
	        assertThat(countApresCreationSql).isNotNull();
	        assertThat(countApresCreationSql).isEqualTo(countAvantSql + 1L);
	        assertThat(countApresCreationService).isEqualTo(countApresCreationSql.longValue());
	
	        /* Vérifie que la ligne créée existe physiquement
	         * une seule fois dans le stockage.
	         */
	        assertThat(countLigneApresCreation).isNotNull().isEqualTo(1L);
	
	        /* Vérifie que count() reste cohérent
	         * avec la méthode publique rechercherTous() après création.
	         */
	        assertThat(listeApresCreation).isNotNull().isNotEmpty();
	        assertThat(countApresCreationService).isEqualTo(listeApresCreation.size());
	
	        
	        /* ****** DESTRUCTION D'UN OBJET METIER *******/
	        /* ACT :
	         * appelle service.delete(cree).
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
	                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
	                Long.class);
	
	        /* Lit (en SQL) le nombre d'enregistrements
	         * portant l'identifiant supprimé.
	         */
	        final Long countLigneApresSuppression = this.jdbcTemplate.queryForObject(
	                SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
	                Long.class,
	                idCree);
	
	        /* Lit le nombre d'enregistrements
	         * dans le stockage via le service après suppression.
	         */
	        final long countApresSuppressionService = this.service.count();
	        final List<SousTypeProduit> listeApresSuppression = this.service.rechercherTous();
	
	        /* Vérifie que service.count() retourne un nombre 
	         * d'enregistrements dans le stockage 
	         * diminué de 1 après une suppression validée par 
	         * l'interrogation directe du stockage en SQL.
	         */
	        assertThat(countApresSuppressionSql).isNotNull();
	        assertThat(countApresSuppressionSql).isEqualTo(countAvantSql);
	        assertThat(countApresSuppressionService).isEqualTo(countApresSuppressionSql.longValue());
	
	        /* Vérifie que la ligne supprimée
	         * est physiquement absente du stockage.
	         */
	        assertThat(countLigneApresSuppression).isNotNull().isZero();
	
	        /* Vérifie que count() reste cohérent
	         * avec la méthode publique rechercherTous() après suppression.
	         */
	        assertThat(listeApresSuppression).isNotNull().isNotEmpty();
	        assertThat(countApresSuppressionService).isEqualTo(listeApresSuppression.size());
	
	    } finally {
	
	        /* Nettoyage de sécurité :
	         * supprime l'enregistrement s'il existe encore dans le stockage.
	         */
	        if (idCree != null) {
	            final Integer countRestant = this.jdbcTemplate.queryForObject(
	                    SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
	                    Integer.class,
	                    idCree);
	
	            if (countRestant != null && countRestant.intValue() > 0) {
	                this.jdbcTemplate.update(
	                        DELETE_FROM_STP_WHERE_ID_STP,
	                        idCree);
	            }
	        }
	
	    }
	
	} // __________________________________________________________________
   
    
    
    // ============================ OUTILS ================================



   /**
    * <div>
    * <p>Vérifie au moyen d'assertions AssertJ
    * la suppression dans le stockage directement via {@link JdbcTemplate},
    * sans passer par Hibernate.</p>
    * <ul>
    * <li>assure que l'enregistrement idSupprime est absent du stockage 
    * (vérification directe via SQL).</li>
    * <li>assure que service.findById(idSupprime) ne 
    * trouve plus l'enregistrement dans le stockage.</li>
    * </ul>
    * </div>
    *
    * @param idSupprime : Long : identifiant de l'objet métier supprimé
    * @throws Exception
    */
   private void verifierSuppressionDansStockage(
		   final Long idSupprime) throws Exception {

       /*
        * Vérifie directement (en SQL)
        * que l'enregistrement supprimé
        * est absent du stockage.
        */
       final Integer count = this.jdbcTemplate.queryForObject(
               SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
               Integer.class,
               idSupprime);

       assertThat(count)
           .as("L'enregistrement doit être physiquement absent du stockage "
                   + "(expected: 0, actual: " + count + ")")
           .isNotNull()
           .isZero();

       /*
        * Vérifie aussi que le service
        * ne retrouve plus l'objet métier supprimé.
        */
       assertThat(this.service.findById(idSupprime))
           .as("Le service ne doit plus trouver l'objet métier supprimé")
           .isNull();

   } // __________________________________________________________________
	
	
	
	/**
     * <div>
     * <p>Retrouve l'ID persistant d'un {@link TypeProduitJPA} 
     * par libellé.</p>
     * </div>
     *
     * @param pLibelleParent : String : 
     * libellé du TypeProduit
     * @return Long :  ID persistant
     */
    private Long retrouverIdParentPersistantParLibelle(
    		final String pLibelleParent) {
    	
        final TypeProduitJPA parent 
        = this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(pLibelleParent);
        
        assertThat(parent).isNotNull();
        assertThat(parent.getIdTypeProduit()).isNotNull();
        
        return parent.getIdTypeProduit();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Retrouve l'ID persistant d'un {@link SousTypeProduitJPA} 
     * par libellé.</p>
     * </div>
     *
     * @param pLibelleEnfant : String
     * @return Long
     */
    private Long retrouverIdEnfantPersistantParLibelle(
    		final String pLibelleEnfant) {
    	
        final List<SousTypeProduitJPA> enfants 
        = this.sousTypeProduitDaoJPA.findBySousTypeProduitIgnoreCase(pLibelleEnfant);
        
        assertThat(enfants).isNotNull().isNotEmpty();
        
        final SousTypeProduitJPA enfant = enfants.get(0);
        assertThat(enfant).isNotNull();
        assertThat(enfant.getIdSousTypeProduit()).isNotNull();
        
        return enfant.getIdSousTypeProduit();
        
    } // __________________________________________________________________
    

    
} // FIN DE LA CLASSE SousTypeProduitGatewayJPAServiceIntegrationTest.-----
