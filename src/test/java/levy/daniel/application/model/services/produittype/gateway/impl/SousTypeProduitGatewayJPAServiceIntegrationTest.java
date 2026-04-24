/* ********************************************************************* */
/* ***************************** TEST JUNIT **************************** */
/* ********************************************************************* */
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
 * <li>Une BDD H2 en mode Memory</li>
 * <li>Une configuration autonome Spring Boot définie directement
 * dans le présent test via {@link ConfigTest}</li>
 * <li>Des scripts SQL pour initialiser la base :
 * <code>truncate-test.sql</code> puis <code>data-test.sql</code></li>
 * <li>Un profil SPRING "test" activé par le test.</li>
 * </ul>
 *
 * <p>
 * Objectif : vérifier le comportement réel (JPA/H2) du gateway.
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

    /** "xyz" */
    public static final String CONTENU_PARTIEL_INEXISTANT = "xyz";

    /** 
     * "rechercherTousParPage(tris valides) - retourne une page triée" 
     */
    public static final String DN_RECHERCHER_TOUS_PAR_PAGE_TRI 
    	= "rechercherTousParPage(tris valides) - retourne une page triée";

    /** 
     * "rechercherTousParPage(page vide) - retourne une page vide" 
     */
    public static final String DN_RECHERCHER_TOUS_PAR_PAGE_VIDE 
    	= "rechercherTousParPage(page vide) - retourne une page vide";

    /** 
     * "findByLibelle(inexistant) - retourne une liste vide" */
    public static final String DN_FINDBYLIBELLE_INEXISTANT 
    	= "findByLibelle(inexistant) - retourne une liste vide";

    /** "findByLibelleRapide(contenu inexistant) - retourne une liste vide" */
    public static final String DN_FINDBYLIBELLERAPIDE_INEXISTANT 
    	= "findByLibelleRapide(contenu inexistant) - retourne une liste vide";

    /** "update(parent modifié) - met à jour le parent" */
    public static final String DN_UPDATE_PARENT_MODIFIE 
    	= "update(parent modifié) - met à jour le parent";

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

    /** "creer(blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_CREER_BLANK 
    	= "creer(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "creer(parent null) - jette ExceptionAppliParentNull (contrat du port)" */
    public static final String DN_CREER_PARENT_NULL 
    	= "creer(parent null) - jette ExceptionAppliParentNull (contrat du port)";

    /** "creer(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_CREER_PARENT_LIBELLE_BLANK 
    	= "creer(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "creer(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
    public static final String DN_CREER_PARENT_NON_PERSISTANT 
    	= "creer(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** "creer(nominal) - ajoute un élément, le rend retrouvable et ne wipe pas les seedés" */
    public static final String DN_CREER_NOMINAL 
    	= "creer(nominal) - ajoute un élément, le rend retrouvable et ne wipe pas les seedés";

    /** "rechercherTous() - retourne la liste seedée (triée, sans doublons)" */
    public static final String DN_RECHERCHER_TOUS 
    	= "rechercherTous() - retourne la liste seedée (triée, sans doublons)";

    /** "findByObjetMetier(null) - jette ExceptionAppliParamNull (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_NULL 
    	= "findByObjetMetier(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** "findByObjetMetier(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_BLANK 
    	= "findByObjetMetier(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findByObjetMetier(parent null) - jette ExceptionAppliParentNull (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_PARENT_NULL 
    	= "findByObjetMetier(parent null) - jette ExceptionAppliParentNull (contrat du port)";

    /** "findByObjetMetier(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_PARENT_LIBELLE_BLANK 
    	= "findByObjetMetier(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findByObjetMetier(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
    public static final String DN_FINDBYOBJETMETIER_PARENT_NON_PERSISTANT 
    	= "findByObjetMetier(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** "findByObjetMetier(nominal) - retourne l'objet métier correspondant" */
    public static final String DN_FINDBYOBJETMETIER_NOMINAL 
    	= "findByObjetMetier(nominal) - retourne l'objet métier correspondant";

    /** "findByLibelle(blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDBYLIBELLE_BLANK 
    	= "findByLibelle(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findByLibelle(nominal) - retourne la liste des correspondances" */
    public static final String DN_FINDBYLIBELLE_NOMINAL 
    	= "findByLibelle(nominal) - retourne la liste des correspondances";

    /** "findByLibelleRapide(null) - jette ExceptionAppliParamNull (contrat du port)" */
    public static final String DN_FINDBYLIBELLERAPIDE_NULL 
    	= "findByLibelleRapide(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** "findByLibelleRapide(blank) - délègue à rechercherTous()" */
    public static final String DN_FINDBYLIBELLERAPIDE_BLANK 
    	= "findByLibelleRapide(blank) - délègue à rechercherTous()";

    /** "findByLibelleRapide(nominal) - retourne les correspondances partielles" */
    public static final String DN_FINDBYLIBELLERAPIDE_NOMINAL 
    	= "findByLibelleRapide(nominal) - retourne les correspondances partielles";

    /** "findAllByParent(null) - jette ExceptionAppliParentNull (contrat du port)" */
    public static final String DN_FINDALLBYPARENT_NULL 
    	= "findAllByParent(null) - jette ExceptionAppliParentNull (contrat du port)";

    /** "findAllByParent(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_FINDALLBYPARENT_LIBELLE_BLANK 
    	= "findAllByParent(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "findAllByParent(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)" */
    public static final String DN_FINDALLBYPARENT_NON_PERSISTANT 
    	= "findAllByParent(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** "findAllByParent(nominal) - retourne les enfants du parent" */
    public static final String DN_FINDALLBYPARENT_NOMINAL 
    	= "findAllByParent(nominal) - retourne les enfants du parent";

    /** "findById(null) - jette ExceptionAppliParamNull (contrat du port)" */
    public static final String DN_FINDBYID_NULL 
    	= "findById(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** "findById(absent) - retourne null" */
    public static final String DN_FINDBYID_ABSENT 
    	= "findById(absent) - retourne null";

    /** "findById(nominal) - retourne l'objet métier correspondant" */
    public static final String DN_FINDBYID_NOMINAL 
    	= "findById(nominal) - retourne l'objet métier correspondant";

    /** "update(null) - jette ExceptionAppliParamNull (contrat du port)" */
    public static final String DN_UPDATE_NULL 
    	= "update(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** "update(blank) - jette ExceptionAppliLibelleBlank (contrat du port)" */
    public static final String DN_UPDATE_BLANK 
    	= "update(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** "update(id null) - jette ExceptionAppliParamNonPersistent (contrat du port)" */
    public static final String DN_UPDATE_ID_NULL 
    	= "update(id null) - jette ExceptionAppliParamNonPersistent (contrat du port)";

    /** "update(absent) - retourne null" */
    public static final String DN_UPDATE_ABSENT 
    	= "update(absent) - retourne null";

    /** "update(nominal) - modifie le stockage et retourne l'objet modifié" */
    public static final String DN_UPDATE_NOMINAL 
    	= "update(nominal) - modifie le stockage et retourne l'objet modifié";

    /** "delete(null) - jette ExceptionAppliParamNull (contrat du port)" */
    public static final String DN_DELETE_NULL 
    	= "delete(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** "delete(id null) - jette ExceptionAppliParamNonPersistent (contrat du port)" */
    public static final String DN_DELETE_ID_NULL 
    	= "delete(id null) - jette ExceptionAppliParamNonPersistent (contrat du port)";

    /** "delete(absent) - ne fait rien" */
    public static final String DN_DELETE_ABSENT 
    	= "delete(absent) - ne fait rien";

    /** "delete(nominal) - supprime l'élément et le rend introuvable" */
    public static final String DN_DELETE_NOMINAL 
    	= "delete(nominal) - supprime l'élément et le rend introuvable";

    /** "count() - cohérent avec le DAO" */
    public static final String DN_COUNT_NOMINAL = "count() - cohérent avec le DAO";
    
    /**
     * "findByLibelle(null) - jette ExceptionAppliLibelleBlank (contrat du port)"
     */
    public static final String DN_FINDBYLIBELLE_NULL
        = "findByLibelle(null) - jette ExceptionAppliLibelleBlank (contrat du port)";
    
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
     * <p>DAO enfant (contrôles béton : compter / retrouver IDs).</p>
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
     * <p>JdbcTemplate pour manimuler directement la base en SQL 
     * sans passer par Hibernate (risques avec le cache Hibernate).</p>
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
    public void testCreerParamNullExceptionAppliParamNull() {

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
         * garantit que this.service.creer(null)
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
    public void testCreerLibelleBlankExceptionAppliLibelleBlank() {
    	
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
    public void testCreerParentNullExceptionAppliParentNull() {

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
         * prépare un sous-type à créer sans parent,
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
    public void testCreerParentLibelleBlankExceptionAppliLibelleBlank() {

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
    public void testCreerParentIdNullExceptionTechniqueGatewayNonPersistent() {

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
     * <li>n'écrit rien dans le stockage.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_NON_PERSISTANT)
    @Test
    public void testCreerParentAbsentExceptionTechniqueGatewayNonPersistent() {

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
    public void testCreerLibelleExistant() throws Exception {

        /* ARRANGE :
         * lit d'abord le stockage par SQL direct afin de disposer 
         * d'une preuve indépendante du contexte Hibernate.
         *
         * Le doublon testé ici porte sur le couple :
         * - parent = vêtement
         * - sous-type = vêtement pour homme
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
         * - sous-type = LIBELLE_ENFANT_VETEMENT_HOMME
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

       
        assertThat(countCoupleAvant).isEqualTo(1L);
        assertThat(idSeedAvant).isNotNull();

        /* ACT - ASSERT :
         * sollicite la méthode creer(...)
         * avec un objet métier déjà présent.
         *
         * le rollback transactionnel est visible dans le test
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
         * n'a été créée dans le stockage.
         */
        assertThat(countApres).isEqualTo(countAvant);

        /* Garantit qu'il n'existe toujours
         * qu'un seul enregistrement pour cet objet métier.
         */
        assertThat(countCoupleApres).isEqualTo(1L);

        /* Garantit enfin que la ligne seedée initiale
         * est restée strictement la même.
         */
        assertThat(idSeedApres).isEqualTo(idSeedAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(nominal) :</p>
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
    public void testCreerNominalOk() throws Exception {

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

        /* ACT :
         * appelle service.creer(aCreer)
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

        final Long idCree = cree.getIdSousTypeProduit();

        try {

            /* ASSERT :
             * contrôle ensuite physiquement le stockage par SQL direct,
             * pour prouver l'écriture réelle dans le stockage 
             * et non un simple effet de cache Hibernate.
             */
            final Long countApres = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                    Long.class);

            final Integer countEnBase = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                    Integer.class,
                    idCree);

            final String libelleEnBase = this.jdbcTemplate.queryForObject(
                    SELECT_STP_FROM_STP_WHERE_ID,
                    String.class,
                    idCree);

            final Long parentEnBase = this.jdbcTemplate.queryForObject(
                    SELECT_TP_FROM_STP_WHERE_ID,
                    Long.class,
                    idCree);

            /* 
             * Garantit que l'appel service.creer(aCreer) augmente bien
             * le nombre total de lignes.
             */
            assertThat(countApres).isEqualTo(countAvant + 1L);

            /* 
             * Garantit physiquement qu'un seul enregistrement 
             * dans le stockage porte bien l'identifiant créé.
             */
            assertThat(countEnBase)
                .as("L'enregistrement doit exister physiquement dans le stockage après service.creer(...) : ")
                .isEqualTo(1);

            /* 
             * Assure l'écriture du bon libellé de l'objet métier 
             * dans le stockage.
             */
            assertThat(libelleEnBase).isEqualTo(LIBELLE_NOUVEAU_PULL);

            /* 
             * Assure l'écriture de la bonne clé étrangère parent 
             * de l'objet métier dans le stockage.
             */
            assertThat(parentEnBase).isEqualTo(idParent);

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

            /* Nettoyage physique :
             * supprime l'enregistrement créé dans le stockage,
             * afin de garantir l'isolation du test
             * même en cas d'échec d'assertion en amont.
             */
            if (idCree != null) {
                this.jdbcTemplate.update(
                        DELETE_FROM_STP_WHERE_ID_STP,
                        idCree);
            }

        }

    } // __________________________________________________________________
    
    
    
    // ======================== RechercherTous ============================



    /**
     * <div>
     * <p>garantit que rechercherTous() sur le stockage seedé :</p>
     * <ul>
     * <li>retourne une liste non null et non vide ;</li>
     * <li>retourne exactement les libellés physiquement 
     * présents dans le stockage ;</li>
     * <li>retourne autant d'objets métier que 
     * d'enregistrements présents dans le stockage ;</li>
     * <li>retourne une liste triée par libellé et sans doublon ;</li>
     * <li>retourne des objets métier portant chacun un parent non null.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_RECHERCHER_TOUS)
    @Test
    public void testRechercherTousNominalOk() throws Exception {

        /* ARRANGE :
         * Lit directement (en SQL) dans le stockage
         * via JdbcTemplate afin de disposer d'une preuve indépendante
         * du contexte Hibernate.
         *
         * Ce test vérifie ensuite que la liste renvoyée par le service
         * correspond exactement à cet état physique.
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final List<String> libellesEnBase = this.jdbcTemplate.queryForList(
                "SELECT SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT",
                String.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countEnBase).isNotNull().isPositive();
        assertThat(libellesEnBase).isNotNull().isNotEmpty();

        /* 
         * Ordonne la référence SQL
         * selon le même critère métier attendu côté service :
         * tri alphabétique insensible à la casse.
         */
        libellesEnBase.sort(String.CASE_INSENSITIVE_ORDER);

        /* ACT :
         * sollicite service.rechercherTous()
         * sur le stockage seedé.
         */
        final List<SousTypeProduit> resultats = this.service.rechercherTous();

        /* ASSERT :
         * vérifie d'abord que la méthode retourne
         * une liste exploitable (non null et non vide).
         */
        assertThat(resultats).isNotNull().isNotEmpty();

        /* 
         * Vérifie ensuite que le nombre d'objets métier retournés
         * correspond exactement au nombre d'enregistrements 
         * dans le stockage.
         */
        assertThat(resultats).hasSize(countEnBase.intValue());

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
         * Vérifie enfin les propriétés attendues côté service :
         * pas de doublon métier, ordre alphabétique
         * et parent non null sur chaque objet métier.
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

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTous() sur un stockage vidé :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>retourne une liste vide ;</li>
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
    public void testRechercherTousBaseVide() throws Exception {

        /* ARRANGE :
         * remplace pour ce test la préparation standard
         * par le seul script de vidage 
         * afin d'obtenir un stockage vide.
         *
         * Ce test vérifie ensuite que le service
         * reste cohérent avec cet état physique.
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /*
         * Assure que le stockage est vide.
         */
        assertThat(countEnBase).isNotNull().isZero();

        /* ACT :
         * appelle service.rechercherTous()
         * sur le stockage vide.
         */
        final List<SousTypeProduit> resultats = this.service.rechercherTous();

        /* ASSERT :
         * - assure que service.rechercherTous() ne retourne jamais null,
         * même lorsque le stockage est vide.
         * - assure que service.rechercherTous() retourne une liste vide 
         * lorsque le stockage est vide.
         */
        assertThat(resultats).isNotNull().isEmpty();

    } // __________________________________________________________________    
    
    

    // ===================== rechercherTousParPage =====================



    /**
     * <div>
     * <p>garantit que rechercherTousParPage(null) :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>retourne un contenu non null ;</li>
     * <li>retourne un total cohérent avec l'état physique de la base ;</li>
     * <li>applique les paramètres par défaut de pagination ;</li>
     * <li>retourne des objets métier portant chacun un parent non null.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(null) - applique la pagination par défaut et reste cohérent avec le stockage")
    @Test
    public void testRechercherTousParPageParamNullNominalOk() throws Exception {

        /* ARRANGE :
         * Compte directement (en SQL) le nombre d'enregistrements 
         * dans le stockage via JdbcTemplate afin de disposer 
         * d'une preuve indépendante du contexte Hibernate.
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* vérifie que le stockage n'est pas vide. */
        assertThat(countEnBase).isNotNull().isPositive();

        /* ACT :
         * sollicite la pagination avec une requête null,
         * ce qui doit conduire le service
         * à appliquer les paramètres par défaut.
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
        assertThat(resultat.getTotalElements()).isEqualTo(countEnBase);

        /* Vérifie ensuite que la taille du contenu retourné
         * correspond exactement à la taille de la première page attendue.
         */
        assertThat(resultat.getContent())
            .hasSize(Math.min(countEnBase.intValue(), RequetePage.TAILLE_DEFAUT));

        /* Vérifie enfin que les objets métier retournés
         * restent cohérents et exploitables.
         */
        assertThat(resultat.getContent())
            .allSatisfy(stp -> {
                assertThat(stp).isNotNull();
                assertThat(stp.getSousTypeProduit()).isNotBlank();
                assertThat(stp.getTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getIdTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getTypeProduit()).isNotBlank();
            });

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTousParPage(tris valides) :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>retourne un contenu trié selon la spécification demandée ;</li>
     * <li>retourne un total cohérent avec l'état physique du stockage ;</li>
     * <li>retourne exactement le segment attendu de l'état physique trié.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName(DN_RECHERCHER_TOUS_PAR_PAGE_TRI)
    @Test
    public void testRechercherTousParPageTrisValidesOk() throws Exception {

        /* ARRANGE :
         * lit d'abord (en SQL) directement dans le stockage
         * les libellés physiquement présents,
         * déjà ordonnés selon le tri métier demandé,
         * afin de disposer d'une référence indépendante d'Hibernate.
         */
        final List<String> libellesEnBase = this.jdbcTemplate.queryForList(
                "SELECT SOUS_TYPE_PRODUIT FROM SOUS_TYPES_PRODUIT ORDER BY UPPER(SOUS_TYPE_PRODUIT) ASC",
                String.class);

        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* Vérifie que le stockage n'est pas vide. */
        assertThat(libellesEnBase).isNotNull().isNotEmpty();
        assertThat(countEnBase).isNotNull().isPositive();

        final RequetePage requete = new RequetePage();
        requete.setPageNumber(0);
        requete.setPageSize(2);
        requete.getTris().add(new TriSpec("sousTypeProduit", DirectionTri.ASC));

        /* ACT :
         * sollicite la pagination sur la première page
         * avec taille 2 et tri ascendant sur le libellé enfant.
         */
        final ResultatPage<SousTypeProduit> resultat =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie d'abord la cohérence générale de la page.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().hasSize(2);
        assertThat(resultat.getPageNumber()).isEqualTo(0);
        assertThat(resultat.getPageSize()).isEqualTo(2);
        assertThat(resultat.getTotalElements()).isEqualTo(countEnBase);

        final List<String> libellesPage = resultat.getContent().stream()
                .map(SousTypeProduit::getSousTypeProduit)
                .toList();

        /* 
         * Vérifie ensuite que le contenu retourné
         * correspond exactement aux deux premiers libellés
         * de l'état physique trié.
         */
        assertThat(libellesPage)
            .containsExactlyElementsOf(libellesEnBase.subList(0, 2));

        /* 
         * Vérifie enfin le tri alphabétique du contenu retourné
         * et la cohérence des parents portés par les objets métier.
         */
        assertThat(libellesPage).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);
        assertThat(resultat.getContent())
            .allSatisfy(stp -> {
                assertThat(stp).isNotNull();
                assertThat(stp.getTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getIdTypeProduit()).isNotNull();
            });

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTousParPage(page hors bornes) :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>retourne un contenu vide ;</li>
     * <li>retourne un total égal au nombre d'enregistrements 
     * dans le stockage ;</li>
     * <li>conserve les paramètres demandés de pagination.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(page hors bornes) - retourne une page vide cohérente")
    @Test
    public void testRechercherTousParPagePageHorsBorne() throws Exception {

    	/* ARRANGE :
         * Compte directement (en SQL) le nombre d'enregistrements 
         * dans le stockage via JdbcTemplate afin de disposer 
         * d'une preuve indépendante du contexte Hibernate.
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /*
         * Assure que le stockage n'est pas vide.
         */
        assertThat(countEnBase).isNotNull().isPositive();

        /* 
         * construit une requête dont le numéro de page
         * dépasse très largement le nombre de pages disponibles.
         */
        final RequetePage requete = new RequetePage();
        requete.setPageNumber(999);
        requete.setPageSize(2);
        requete.getTris().add(new TriSpec("sousTypeProduit", DirectionTri.ASC));

        /* ACT :
         * sollicite service.rechercherTousParPage(requete) 
         * sur une page hors bornes.
         */
        final ResultatPage<SousTypeProduit> resultat =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie que la page retournée
         * reste exploitable et cohérente.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().isEmpty();
        assertThat(resultat.getPageNumber()).isEqualTo(999);
        assertThat(resultat.getPageSize()).isEqualTo(2);
        assertThat(resultat.getTotalElements()).isEqualTo(countEnBase);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTousParPage(...) sur un stockage vide :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>retourne un contenu vide ;</li>
     * <li>retourne un total d'enregistrements dans 
     * le stockage égal à zéro ;</li>
     * <li>reste cohérent avec le stockage vide.</li>
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
    public void testRechercherTousParPageBaseVide() throws Exception {

        /* ARRANGE :
         * remplace pour ce test la préparation standard
         * par le seul script de vidage afin d'obtenir un stockage vide.
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /*
         * Assure que le stockage est vide.
         */
        assertThat(countEnBase).isNotNull().isZero();

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
         * vérifie que la page retournée
         * reste exploitable et cohérente.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().isEmpty();
        assertThat(resultat.getPageNumber()).isEqualTo(0);
        assertThat(resultat.getPageSize()).isEqualTo(10);
        assertThat(resultat.getTotalElements()).isZero();

    } // __________________________________________________________________
    
    
        
    // ======================== findByObjetMetier =========================



    /**
     * <div>
     * <p>garantit que findByObjetMetier(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL} ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_NULL)
    @Test
    public void testFindByObjetMetierParamNullExceptionAppliParamNull() {

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
         * garantit que this.service.findByObjetMetier(null)
         * - jette une ExceptionAppliParamNull
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL);

        /* ASSERT :
         * Compte ensuite directement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver l'absence de toute écriture réelle.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK} ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_BLANK)
    @Test
    public void testFindByObjetMetierLibelleBlankExceptionAppliLibelleBlank() {

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
         * avec un parent réellement persistant.
         */
        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp = new SousTypeProduit(null, BLANK, parent);

        /* ACT - ASSERT :
         * garantit que this.service.findByObjetMetier(stp)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);

        /* ASSERT :
         * Compte ensuite directement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver l'absence de toute écriture réelle.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParentNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL} ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_PARENT_NULL)
    @Test
    public void testFindByObjetMetierParentNullExceptionAppliParentNull() {

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
         * garantit que this.service.findByObjetMetier(stp)
         * - jette une ExceptionAppliParentNull
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL);

        /* ASSERT :
         * Compte ensuite directement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver l'absence de toute écriture réelle.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_PARENT_LIBELLE_BLANK)
    @Test
    public void testFindByObjetMetierParentLibelleBlankExceptionAppliLibelleBlank() {

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
         * puis prépare un objet métier dont le parent a un libellé blank.
         */
        final TypeProduit parent = new TypeProduit(Long.valueOf(1L), BLANK);
        final SousTypeProduit stp =
                new SousTypeProduit(null, LIBELLE_ENFANT_VETEMENT_HOMME, parent);

        /* ACT - ASSERT :
         * garantit que this.service.findByObjetMetier(stp)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK);

        /* ASSERT :
         * Compte ensuite directement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver l'absence de toute écriture réelle.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT} ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_PARENT_NON_PERSISTANT)
    @Test
    public void testFindByObjetMetierParentIdNullExceptionTechniqueGatewayNonPersistent() {

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
         * garantit que this.service.findByObjetMetier(stp)
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
         * Compte ensuite directement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver l'absence de toute écriture réelle.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent absent dans le stockage) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT} ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(parent absent) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)")
    @Test
    public void testFindByObjetMetierParentAbsentExceptionTechniqueGatewayNonPersistent() {

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
         * prépare un objet métier dont le parent 
         * porte un identifiant inexistant,
         * afin de vérifier le contrôle de 
         * persistance réelle dans le stockage.
         */
        final TypeProduit parent = new TypeProduit(ID_INEXISTANT, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp =
                new SousTypeProduit(null, LIBELLE_ENFANT_VETEMENT_HOMME, parent);

        /* ACT - ASSERT :
         * garantit que this.service.findByObjetMetier(stp)
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
         * Compte ensuite directement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver l'absence de toute écriture réelle.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(objet introuvable sous un parent persistant) :</p>
     * <ul>
     * <li>retourne {@code null} ;</li>
     * <li>n'altère pas le stockage réel ;</li>
     * <li>reste cohérent avec l'absence physique du couple métier en base.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(introuvable) - retourne null sans altérer la base")
    @Test
    public void testFindByObjetMetierAbsentRetourneNull() throws Exception {

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
         * prépare un objet métier portant un parent réellement persistant
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
        assertThat(countCoupleAvant).isZero();

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
         * Compte ensuite directement (en SQL) le nombre d'enregistrements
         * dans le stockage pour prouver l'absence de toute écriture réelle.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(nominal) :</p>
     * <ul>
     * <li>retourne l'objet métier correspondant au couple recherché ;</li>
     * <li>retourne l'identifiant physiquement présent en base ;</li>
     * <li>retourne le bon libellé enfant et le bon parent ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_NOMINAL)
    @Test
    public void testFindByObjetMetierNominalOk() throws Exception {

        /* ARRANGE :
         * lit d'abord (en SQL) directement dans le stockage
         * pour retrouver le parent persistant
         * puis l'identifiant exact du sous-type attendu.
         *
         * Cette référence SQL sert ensuite à comparer 
         * le résultat de service.findByObjetMetier(...)
         * à l'état réel du stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

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

        final SousTypeProduit probe = new SousTypeProduit(
                null,
                LIBELLE_ENFANT_VETEMENT_HOMME,
                new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT));

        assertThat(countAvant).isNotNull();
        assertThat(countCoupleAvant).isEqualTo(1L);
        assertThat(idTrouveEnBase).isNotNull();

        /* 
         * Neutralise explicitement le contexte Hibernate
         * avant la lecture via service.findByObjetMetier(...)
         * afin d'éviter des effets indésirables du cache Hibernate.
         */
        this.entityManager.clear();

        /* ACT :
         * appelle service.findByObjetMetier(...)
         * sur un objet métier réellement présent dans le stockage.
         */
        final SousTypeProduit trouve = this.service.findByObjetMetier(probe);

        /* ASSERT :
         * vérifie d'abord que le service retourne bien
         * un objet métier exploitable.
         */
        assertThat(trouve).isNotNull();

        /* Vérifie ensuite que l'identifiant retourné,
         * le libellé enfant et le parent
         * correspondent exactement à l'état physique de la base.
         */
        assertThat(trouve.getIdSousTypeProduit()).isEqualTo(idTrouveEnBase);
        assertThat(trouve.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);
        assertThat(trouve.getTypeProduit()).isNotNull();
        assertThat(trouve.getTypeProduit().getIdTypeProduit()).isEqualTo(idParent);
        assertThat(trouve.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_VETEMENT);

        /* Vérifie enfin qu'une méthode de lecture
         * n'a pas altéré le stockage réel.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

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
     * <li>n'altère pas le stockage réel.</li>
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

        assertThat(countAvant).isNotNull();

        /* ACT - ASSERT :
         * garantit que this.service.findByLibelle(null)
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

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK} ;</li>
     * <li>n'altère pas le stockage réel.</li>
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

        assertThat(countAvant).isNotNull();

        /* ACT - ASSERT :
         * garantit que this.service.findByLibelle(BLANK)
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

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(inexistant) :</p>
     * <ul>
     * <li>retourne une liste non null et vide ;</li>
     * <li>reste cohérent avec l'absence physique de ligne correspondante
     * dans le stockage ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_INEXISTANT)
    @Test
    public void testFindByLibelleInexistantVideOk() throws Exception {

        /* ARRANGE :
         * vérifie d'abord par SQL direct
         * qu'aucune ligne ne porte le libellé LIBELLE_INEXISTANT,
         * sans tenir compte des majuscules/minuscules.
         *
         * Le test compare ensuite le résultat du service
         * à cet état physique de référence.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* countCorrespondancesEnBase = nombre d'objets trouvés en base 
         * possédant un libellé LIBELLE_INEXISTANT directement par SQL */
        final Long countCorrespondancesEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE,
                Long.class,
                LIBELLE_INEXISTANT);

        assertThat(countAvant).isNotNull();
        
        /* Assure que countCorrespondancesEnBase 
         * n'est pas null mais vaut zéro. */
        assertThat(countCorrespondancesEnBase).isNotNull().isZero();

        /* Neutralise explicitement le contexte Hibernate
         * avant l'appel de lecture,
         * afin d'éviter tout raisonnement biaisé par le cache.
         */
        this.entityManager.clear();

        /* ACT :
         * sollicite service.findByLibelle(...)
         * avec le libellé LIBELLE_INEXISTANT,
         * absent du stockage réel (prouvé pae SQL).
         */
        final List<SousTypeProduit> liste 
        	= this.service.findByLibelle(LIBELLE_INEXISTANT);

        /* ASSERT :
         * vérifie que le service retourne une liste vide (pas null)
         * lors de service.findByLibelle(...) avec un libellé inexistant 
         * dans le stockage.
         */
        assertThat(liste).isNotNull().isEmpty();

        /* 
         * Vérifie enfin que service.findByLibelle(...)
         * n'a pas altéré le stockage réel.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(trouvé) :</p>
     * <ul>
     * <li>retourne une liste non null et non vide ;</li>
     * <li>retourne exactement les identifiants physiquement présents en base
     * pour ce libellé ;</li>
     * <li>retourne uniquement le libellé recherché ;</li>
     * <li>retourne des objets métier portant chacun un parent non null ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_NOMINAL)
    @Test
    public void testFindByLibelleNominalOk() throws Exception {

        /* ARRANGE :
         * lit d'abord l'état physique réel de la base
         * pour le libellé exact LIBELLE_ENFANT_VETEMENT_HOMME.
         *
         * Le test compare ensuite la réponse du service
         * à cette référence indépendante d'Hibernate.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final Long countCorrespondancesEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE,
                Long.class,
                LIBELLE_ENFANT_VETEMENT_HOMME);

        final List<Long> idsEnBase = this.jdbcTemplate.queryForList(
                SELECT_PARAM_IDS_FROM_STP_WHERE_LIBELLE,
                Long.class,
                LIBELLE_ENFANT_VETEMENT_HOMME);

        assertThat(countAvant).isNotNull();
        assertThat(countCorrespondancesEnBase).isNotNull().isPositive();
        assertThat(idsEnBase).isNotNull().isNotEmpty();

        /* Neutralise explicitement le contexte Hibernate
         * avant l'appel de lecture,
         * afin d'éviter tout raisonnement biaisé par le cache.
         */
        this.entityManager.clear();

        /* ACT :
         * sollicite la recherche par libellé exact
         * avec le libellé LIBELLE_ENFANT_VETEMENT_HOMME.
         */
        final List<SousTypeProduit> liste =
                this.service.findByLibelle(LIBELLE_ENFANT_VETEMENT_HOMME);

        /* ASSERT :
         * vérifie d'abord que la méthode retourne
         * une liste exploitable.
         */
        assertThat(liste).isNotNull().isNotEmpty();
        assertThat(liste).hasSize(countCorrespondancesEnBase.intValue());

        final List<Long> idsRetournes = liste.stream()
                .map(SousTypeProduit::getIdSousTypeProduit)
                .sorted()
                .toList();

        /* Vérifie ensuite que les identifiants renvoyés par le service
         * correspondent exactement aux identifiants physiquement présents en base
         * pour le libellé LIBELLE_ENFANT_VETEMENT_HOMME.
         */
        assertThat(idsRetournes).containsExactlyElementsOf(idsEnBase);

        /* Vérifie aussi que tous les objets métier retournés
         * portent bien le libellé recherché
         * et un parent non null.
         */
        assertThat(liste)
            .allSatisfy(stp -> {
                assertThat(stp).isNotNull();
                assertThat(stp.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);
                assertThat(stp.getTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getIdTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getTypeProduit()).isNotBlank();
            });

        /* 
         * Vérifie enfin que service.findByLibelle(...)
         * n'a pas altéré le stockage réel.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

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
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_FINDBYLIBELLERAPIDE_NULL)
    @Test
    public void testFindByLibelleRapideParamNullExceptionAppliParamNull() {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countAvant).isNotNull();

        /* ACT - ASSERT :
         * appelle this.service.findByLibelleRapide(null)
         * et vérifie que l'appel
         * - jette une ExceptionAppliParamNull
         * - émet le message MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL);

        /* ASSERT :
         * relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelleRapide(contenu inexistant) :</p>
     * <ul>
     * <li>retourne une liste non null et vide ;</li>
     * <li>reste cohérent avec l'absence de ligne correspondante en base ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_FINDBYLIBELLERAPIDE_INEXISTANT)
    @Test
    public void testFindByLibelleRapideInexistantVideOk() throws Exception {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* Lit le nombre de lignes dont le libellé contient
         * CONTENU_PARTIEL_INEXISTANT, sans tenir compte de la casse.
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE_LIKE,
                Long.class,
                "%" + CONTENU_PARTIEL_INEXISTANT + "%");

        assertThat(countAvant).isNotNull();
        assertThat(countEnBase).isNotNull().isZero();

        /* ACT :
         * appelle this.service.findByLibelleRapide(CONTENU_PARTIEL_INEXISTANT).
         */
        final List<SousTypeProduit> liste =
                this.service.findByLibelleRapide(CONTENU_PARTIEL_INEXISTANT);

        /* ASSERT :
         * vérifie que la liste retournée est vide.
         */
        assertThat(liste).isNotNull().isEmpty();

        /* Relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelleRapide(blank) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>retourne la même liste que rechercherTous() ;</li>
     * <li>reste cohérent avec le nombre de lignes présentes en base ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_FINDBYLIBELLERAPIDE_BLANK)
    @Test
    public void testFindByLibelleRapideBlankRetourneTous() throws Exception {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countAvant).isNotNull().isPositive();

        /* ACT :
         * appelle rechercherTous()
         * puis appelle findByLibelleRapide(BLANK).
         */
        final List<SousTypeProduit> tous = this.service.rechercherTous();
        final List<SousTypeProduit> rapide = this.service.findByLibelleRapide(BLANK);

        /* ASSERT :
         * vérifie que findByLibelleRapide(BLANK)
         * retourne la même liste que rechercherTous().
         */
        assertThat(rapide).isNotNull();
        assertThat(rapide).hasSize(countAvant.intValue());
        assertThat(rapide)
            .extracting(SousTypeProduit::getIdSousTypeProduit)
            .containsExactlyElementsOf(
                    tous.stream()
                        .map(SousTypeProduit::getIdSousTypeProduit)
                        .toList());

        /* Relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelleRapide(nominal) :</p>
     * <ul>
     * <li>retourne une liste non null et non vide ;</li>
     * <li>retourne exactement les IDs présents en base
     * pour le contenu recherché ;</li>
     * <li>reste insensible à la casse ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_FINDBYLIBELLERAPIDE_NOMINAL)
    @Test
    public void testFindByLibelleRapideNominalOk() throws Exception {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* Lit le nombre de lignes dont le libellé contient
         * CONTENU_PARTIEL_VET, sans tenir compte de la casse.
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_LIBELLE_LIKE,
                Long.class,
                "%" + CONTENU_PARTIEL_VET + "%");

        /* Lit les IDs présents en base
         * pour le contenu CONTENU_PARTIEL_VET.
         */
        final List<Long> idsEnBase = this.jdbcTemplate.queryForList(
                SELECT_PARAM_IDS_FROM_STP_WHERE_LIBELLE_LIKE,
                Long.class,
                "%" + CONTENU_PARTIEL_VET + "%");

        final String contenuMaj = CONTENU_PARTIEL_VET.toUpperCase(LOCALE_DEFAUT);

        assertThat(countAvant).isNotNull();
        assertThat(countEnBase).isNotNull().isPositive();
        assertThat(idsEnBase).isNotNull().isNotEmpty();

        /* ACT :
         * appelle findByLibelleRapide(CONTENU_PARTIEL_VET)
         * puis appelle findByLibelleRapide(contenuMaj).
         */
        final List<SousTypeProduit> liste =
                this.service.findByLibelleRapide(CONTENU_PARTIEL_VET);

        final List<SousTypeProduit> listeMaj =
                this.service.findByLibelleRapide(contenuMaj);

        /* ASSERT :
         * vérifie que les deux appels retournent
         * le même nombre de lignes que la base.
         */
        assertThat(liste).isNotNull().hasSize(countEnBase.intValue());
        assertThat(listeMaj).isNotNull().hasSize(countEnBase.intValue());

        final List<Long> idsRetournes = liste.stream()
                .map(SousTypeProduit::getIdSousTypeProduit)
                .sorted()
                .toList();

        final List<Long> idsRetournesMaj = listeMaj.stream()
                .map(SousTypeProduit::getIdSousTypeProduit)
                .sorted()
                .toList();

        /* Vérifie que les IDs retournés
         * correspondent aux IDs présents en base.
         */
        assertThat(idsRetournes).containsExactlyElementsOf(idsEnBase);
        assertThat(idsRetournesMaj).containsExactlyElementsOf(idsEnBase);

        /* Vérifie que chaque libellé retourné
         * contient bien CONTENU_PARTIEL_VET.
         */
        assertThat(liste)
            .allSatisfy(stp -> {
                assertThat(stp).isNotNull();
                assertThat(stp.getSousTypeProduit().toUpperCase(LOCALE_DEFAUT))
                    .contains(CONTENU_PARTIEL_VET.toUpperCase(LOCALE_DEFAUT));
                assertThat(stp.getTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getIdTypeProduit()).isNotNull();
            });

        /* Relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

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
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_NULL)
    @Test
    public void testFindAllByParentParamNullExceptionAppliParentNull() {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countAvant).isNotNull();

        /* ACT - ASSERT :
         * appelle this.service.findAllByParent(null)
         * et vérifie que l'appel
         * - jette une ExceptionAppliParentNull
         * - émet le message MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findAllByParent(null))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL);

        /* ASSERT :
         * relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findAllByParent(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_LIBELLE_BLANK)
    @Test
    public void testFindAllByParentParentLibelleBlankExceptionAppliLibelleBlank() {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final TypeProduit parent = new TypeProduit(Long.valueOf(1L), BLANK);

        assertThat(countAvant).isNotNull();

        /* ACT - ASSERT :
         * appelle this.service.findAllByParent(parent)
         * et vérifie que l'appel
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK);

        /* ASSERT :
         * relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

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
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_NON_PERSISTANT)
    @Test
    public void testFindAllByParentParentIdNullExceptionTechniqueGatewayNonPersistent() {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final TypeProduit parent = new TypeProduit(null, LIBELLE_PARENT_VETEMENT);

        assertThat(countAvant).isNotNull();

        /* ACT - ASSERT :
         * appelle this.service.findAllByParent(parent)
         * et vérifie que l'appel
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet le message MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
         *   + LIBELLE_PARENT_VETEMENT
         *   (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(
                    SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
                            + LIBELLE_PARENT_VETEMENT);

        /* ASSERT :
         * relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

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
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_NON_PERSISTANT)
    @Test
    public void testFindAllByParentParentAbsentExceptionTechniqueGatewayNonPersistent() {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final TypeProduit parent = new TypeProduit(ID_INEXISTANT, LIBELLE_PARENT_VETEMENT);

        assertThat(countAvant).isNotNull();

        /* ACT - ASSERT :
         * appelle this.service.findAllByParent(parent)
         * et vérifie que l'appel
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet le message MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
         *   + LIBELLE_PARENT_VETEMENT
         *   (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(
                    SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
                            + LIBELLE_PARENT_VETEMENT);

        /* ASSERT :
         * relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findAllByParent(parent sans enfant) :</p>
     * <ul>
     * <li>retourne une liste non null et vide ;</li>
     * <li>reste cohérent avec l'absence d'enfant en base pour ce parent ;</li>
     * <li>n'altère pas le stockage réel des sous-types.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findAllByParent(parent sans enfant) - retourne une liste vide")
    @Test
    public void testFindAllByParentParentSansEnfantRetourneListeVide() throws Exception {

        /* ARRANGE :
         * lit le nombre de sous-types avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countAvant).isNotNull();

        /* Crée un parent persistant sans enfant. */
        this.jdbcTemplate.update(
                "INSERT INTO TYPES_PRODUIT (TYPE_PRODUIT) VALUES (?)",
                LIBELLE_PARENT_CHAUSSURE);

        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_CHAUSSURE);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_CHAUSSURE);

        /* Lit le nombre d'enfants en base pour ce parent. */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_PARENT,
                Long.class,
                idParent);

        assertThat(countEnBase).isNotNull().isZero();

        /* ACT :
         * appelle this.service.findAllByParent(parent).
         */
        final List<SousTypeProduit> liste = this.service.findAllByParent(parent);

        /* ASSERT :
         * vérifie que la liste retournée est vide.
         */
        assertThat(liste).isNotNull().isEmpty();

        /* Relit le nombre de sous-types après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que findAllByParent(nominal) :</p>
     * <ul>
     * <li>retourne une liste non null et non vide ;</li>
     * <li>retourne exactement les IDs présents en base pour ce parent ;</li>
     * <li>retourne uniquement des enfants du parent demandé ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_NOMINAL)
    @Test
    public void testFindAllByParentNominalOk() throws Exception {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);

        /* Lit le nombre d'enfants en base
         * pour le parent LIBELLE_PARENT_VETEMENT.
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_PARENT,
                Long.class,
                idParent);

        /* Lit les IDs présents en base
         * pour le parent LIBELLE_PARENT_VETEMENT.
         */
        final List<Long> idsEnBase = this.jdbcTemplate.queryForList(
                SELECT_PARAM_IDS_FROM_STP_WHERE_PARENT,
                Long.class,
                idParent);

        assertThat(countAvant).isNotNull();
        assertThat(countEnBase).isNotNull().isPositive();
        assertThat(idsEnBase).isNotNull().isNotEmpty();

        /* ACT :
         * appelle this.service.findAllByParent(parent).
         */
        final List<SousTypeProduit> liste = this.service.findAllByParent(parent);

        /* ASSERT :
         * vérifie que la liste retournée
         * contient le bon nombre d'éléments.
         */
        assertThat(liste).isNotNull().hasSize(countEnBase.intValue());

        final List<Long> idsRetournes = liste.stream()
                .map(SousTypeProduit::getIdSousTypeProduit)
                .sorted()
                .toList();

        /* Vérifie que les IDs retournés
         * correspondent aux IDs présents en base.
         */
        assertThat(idsRetournes).containsExactlyElementsOf(idsEnBase);

        /* Vérifie que tous les objets retournés
         * portent bien le parent demandé.
         */
        assertThat(liste)
            .allSatisfy(stp -> {
                assertThat(stp).isNotNull();
                assertThat(stp.getTypeProduit()).isNotNull();
                assertThat(stp.getTypeProduit().getIdTypeProduit()).isEqualTo(idParent);
                assertThat(stp.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_VETEMENT);
            });

        /* Relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

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
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_NULL)
    @Test
    public void testFindByIdNull() {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countAvant).isNotNull();

        /* ACT - ASSERT :
         * appelle this.service.findById(null)
         * et vérifie que l'appel
         * - jette une ExceptionAppliParamNull
         * - émet le message MESSAGE_FINDBYID_KO_PARAM_NULL
         *   (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.findById(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYID_KO_PARAM_NULL);

        /* ASSERT :
         * relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findById(absent) :</p>
     * <ul>
     * <li>retourne {@code null} ;</li>
     * <li>reste cohérent avec l'absence de ligne en base
     * pour l'identifiant demandé ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_ABSENT)
    @Test
    public void testFindByIdAbsentRetourneNull() throws Exception {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* Lit le nombre de lignes en base
         * pour l'identifiant ID_INEXISTANT.
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                Long.class,
                ID_INEXISTANT);

        assertThat(countAvant).isNotNull();
        assertThat(countEnBase).isNotNull().isZero();

        /* ACT :
         * appelle this.service.findById(ID_INEXISTANT).
         */
        final SousTypeProduit resultat = this.service.findById(ID_INEXISTANT);

        /* ASSERT :
         * vérifie que la méthode retourne null.
         */
        assertThat(resultat).isNull();

        /* Relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findById(nominal) :</p>
     * <ul>
     * <li>retourne un objet métier non null ;</li>
     * <li>retourne l'identifiant demandé ;</li>
     * <li>retourne le bon libellé enfant ;</li>
     * <li>retourne le bon parent ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_NOMINAL)
    @Test
    public void testFindByIdNominalOk() throws Exception {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final Long idEnfant = retrouverIdEnfantPersistantParLibelle(LIBELLE_ENFANT_VETEMENT_HOMME);

        /* Lit le nombre de lignes en base
         * pour l'identifiant idEnfant.
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                Long.class,
                idEnfant);

        /* Lit le libellé enfant en base
         * pour l'identifiant idEnfant.
         */
        final String libelleEnBase = this.jdbcTemplate.queryForObject(
                SELECT_STP_FROM_STP_WHERE_ID,
                String.class,
                idEnfant);

        /* Lit l'identifiant du parent en base
         * pour l'identifiant idEnfant.
         */
        final Long idParentEnBase = this.jdbcTemplate.queryForObject(
                SELECT_TP_FROM_STP_WHERE_ID,
                Long.class,
                idEnfant);

        assertThat(countAvant).isNotNull();
        assertThat(countEnBase).isNotNull().isEqualTo(1L);
        assertThat(libelleEnBase).isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);
        assertThat(idParentEnBase).isNotNull();

        /* ACT :
         * appelle this.service.findById(idEnfant).
         */
        final SousTypeProduit resultat = this.service.findById(idEnfant);

        /* ASSERT :
         * vérifie que l'objet retourné n'est pas null
         * et porte le bon identifiant.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdSousTypeProduit()).isEqualTo(idEnfant);

        /* Vérifie que l'objet retourné
         * porte le bon libellé enfant.
         */
        assertThat(resultat.getSousTypeProduit()).isEqualTo(libelleEnBase);

        /* Vérifie que l'objet retourné
         * porte le bon parent.
         */
        assertThat(resultat.getTypeProduit()).isNotNull();
        assertThat(resultat.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentEnBase);
        assertThat(resultat.getTypeProduit().getTypeProduit()).isNotBlank();

        /* Relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    

    
    // =============================== update =============================



    /**
     * <div>
     * <p>garantit que update(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_PARAM_NULL} ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_NULL)
    @Test
    public void testUpdateParamNullExceptionAppliParamNull() {

    	/* ARRANGE :
         * compte d'abord (en SQL)
         * le nombre d'enregistrements dans le stockage
         * avant l'appel du service,
         * afin de pouvoir prouver ensuite
         * qu'aucune écriture réelle n'a eu lieu dans le stockage.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countAvant).isNotNull();

        /* ACT - ASSERT :
         * appelle this.service.update(null)
         * et vérifie que l'appel
         * - jette une ExceptionAppliParamNull
         * - émet le message MESSAGE_UPDATE_KO_PARAM_NULL 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.update(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARAM_NULL);

        /* ASSERT :
         * compte ensuite (en SQL)
         * le nombre d'enregistrements dans le stockage
         * après l'échec contractuel,
         * afin de prouver que l'appel au service
         * n'a produit aucune écriture dans le stockage.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_BLANK} ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_BLANK)
    @Test
    public void testUpdateLibelleBlankExceptionAppliLibelleBlank() {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp =
                new SousTypeProduit(
                        Long.valueOf(1L),
                        BLANK,
                        new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT));

        assertThat(countAvant).isNotNull();

        /* ACT - ASSERT :
         * appelle this.service.update(stp)
         * et vérifie que l'appel
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_UPDATE_KO_LIBELLE_BLANK 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_BLANK);

        /* ASSERT :
         * relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

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
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_ID_NULL)
    @Test
    public void testUpdateIdNullExceptionAppliParamNonPersistent() {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp =
                new SousTypeProduit(
                        null,
                        LIBELLE_ENFANT_VETEMENT_HOMME,
                        new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT));

        assertThat(countAvant).isNotNull();

        /* ACT - ASSERT :
         * appelle this.service.update(stp)
         * et vérifie que l'appel
         * - jette une ExceptionAppliParamNonPersistent
         * - émet le message MESSAGE_UPDATE_KO_NON_PERSISTENT
         *   + LIBELLE_ENFANT_VETEMENT_HOMME
         *   (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(
                    SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_NON_PERSISTENT
                            + LIBELLE_ENFANT_VETEMENT_HOMME);

        /* ASSERT :
         * relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(parent null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParentNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_PARENT_NULL} ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent null) - jette ExceptionAppliParentNull (contrat du port)")
    @Test
    public void testUpdateParentNullExceptionAppliParentNull() {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final SousTypeProduit stp =
                new SousTypeProduit(Long.valueOf(1L), LIBELLE_ENFANT_VETEMENT_HOMME, null);

        assertThat(countAvant).isNotNull();

        /* ACT - ASSERT :
         * appelle this.service.update(stp)
         * et vérifie que l'appel
         * - jette une ExceptionAppliParentNull
         * - émet le message MESSAGE_UPDATE_KO_PARENT_NULL 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARENT_NULL);

        /* ASSERT :
         * relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testUpdateParentLibelleBlankExceptionAppliLibelleBlank() {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final SousTypeProduit stp =
                new SousTypeProduit(
                        Long.valueOf(1L),
                        LIBELLE_ENFANT_VETEMENT_HOMME,
                        new TypeProduit(Long.valueOf(1L), BLANK));

        assertThat(countAvant).isNotNull();

        /* ACT - ASSERT :
         * appelle this.service.update(stp)
         * et vérifie que l'appel
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK);

        /* ASSERT :
         * relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

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
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent ID null) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)")
    @Test
    public void testUpdateParentIdNullExceptionTechniqueGatewayNonPersistent() {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final SousTypeProduit stp =
                new SousTypeProduit(
                        Long.valueOf(1L),
                        LIBELLE_ENFANT_VETEMENT_HOMME,
                        new TypeProduit(null, LIBELLE_PARENT_VETEMENT));

        assertThat(countAvant).isNotNull();

        /* ACT - ASSERT :
         * appelle this.service.update(stp)
         * et vérifie que l'appel
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet le message MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT
         *   + LIBELLE_PARENT_VETEMENT
         *   (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(
                    SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT
                            + LIBELLE_PARENT_VETEMENT);

        /* ASSERT :
         * relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

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
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent absent) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)")
    @Test
    public void testUpdateParentAbsentExceptionTechniqueGatewayNonPersistent() {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final SousTypeProduit stp =
                new SousTypeProduit(
                        Long.valueOf(1L),
                        LIBELLE_ENFANT_VETEMENT_HOMME,
                        new TypeProduit(ID_INEXISTANT, LIBELLE_PARENT_VETEMENT));

        assertThat(countAvant).isNotNull();

        /* ACT - ASSERT :
         * appelle this.service.update(stp)
         * et vérifie que l'appel
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet le message MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT
         *   + LIBELLE_PARENT_VETEMENT
         *   (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(
                    SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT
                            + LIBELLE_PARENT_VETEMENT);

        /* ASSERT :
         * relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(absent) :</p>
     * <ul>
     * <li>retourne {@code null} ;</li>
     * <li>reste cohérent avec l'absence de ligne en base
     * pour l'identifiant demandé ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_ABSENT)
    @Test
    public void testUpdateAbsentRetourneNull() throws Exception {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp =
                new SousTypeProduit(
                        ID_INEXISTANT,
                        LIBELLE_ENFANT_VETEMENT_HOMME,
                        new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT));

        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                Long.class,
                ID_INEXISTANT);

        assertThat(countAvant).isNotNull();
        assertThat(countEnBase).isNotNull().isZero();

        /* ACT :
         * appelle this.service.update(stp).
         */
        final SousTypeProduit retour = this.service.update(stp);

        /* ASSERT :
         * vérifie que la méthode retourne null.
         */
        assertThat(retour).isNull();

        /* Relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>garantit que update(sans modification) :</p>
     * <ul>
     * <li>retourne un objet non null ;</li>
     * <li>retourne l'objet persistant inchangé ;</li>
     * <li>ne modifie ni le libellé ni le parent en base ;</li>
     * <li>n'ajoute ni ne supprime aucune ligne.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(sans modification) - retourne l'objet persistant inchangé")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testUpdateSansModificationOk() throws Exception {

        /* ARRANGE :
         * lit (en SQL) le nombre d'enregistrements dans le stockage 
         * avant l'appel du service.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* Trouve un ID existant dans le stockage. */
        final Long idEnfant 
        	= retrouverIdEnfantPersistantParLibelle(
        			LIBELLE_ENFANT_VETEMENT_HOMME);

        /* 
         * Lit (en SQL) le libellé enfant dans le stockage 
         * pour l'ID testé. */
        final String libelleAvant = this.jdbcTemplate.queryForObject(
                SELECT_STP_FROM_STP_WHERE_ID,
                String.class,
                idEnfant);

        /* Lit l'ID parent existant dans le stockage pour l'ID testé. */
        final Long idParentAvant = this.jdbcTemplate.queryForObject(
                SELECT_TP_FROM_STP_WHERE_ID,
                Long.class,
                idEnfant);

        /* Instancie un objet métier existant forcément dans le stockage. */
        final SousTypeProduit stp = new SousTypeProduit(
                idEnfant,
                libelleAvant,
                new TypeProduit(idParentAvant, LIBELLE_PARENT_VETEMENT));

        assertThat(countAvant).isNotNull();
        assertThat(libelleAvant).isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);
        assertThat(idParentAvant).isNotNull();

        /* ACT :
         * appelle this.service.update(stp)
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

        /* Lit le libellé enfant en base après l'appel. */
        final String libelleApres = this.jdbcTemplate.queryForObject(
                SELECT_STP_FROM_STP_WHERE_ID,
                String.class,
                idEnfant);

        /* Lit l'ID parent en base après l'appel. */
        final Long idParentApres = this.jdbcTemplate.queryForObject(
                SELECT_TP_FROM_STP_WHERE_ID,
                Long.class,
                idEnfant);

        assertThat(libelleApres).isEqualTo(libelleAvant);
        assertThat(idParentApres).isEqualTo(idParentAvant);

        this.entityManager.clear();

        /* Relit l'objet via le service. */
        final SousTypeProduit relu = this.service.findById(idEnfant);

        assertThat(relu).isNotNull();
        assertThat(relu.getIdSousTypeProduit()).isEqualTo(idEnfant);
        assertThat(relu.getSousTypeProduit()).isEqualTo(libelleAvant);
        assertThat(relu.getTypeProduit()).isNotNull();
        assertThat(relu.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentAvant);

        /* Relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(sans modification) :</p>
     * <ul>
     * <li>retourne un objet métier non null ;</li>
     * <li>retourne l'objet persistant inchangé ;</li>
     * <li>ne modifie rien dans le stockage ;</li>
     * <li>reste cohérent avec une relecture par le service.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(sans modification) - retourne l'objet persistant inchangé")
    @Test
    public void testUpdateSansModification() throws Exception {

        /* ARRANGE :
         * trouve un objet existant dans le stockage.
         */
        final Long idEnfant =
                retrouverIdEnfantPersistantParLibelle(LIBELLE_ENFANT_VETEMENT_HOMME);

        /* Lit (en SQL) le libellé enfant dans le stockage. */
        final String libelleAvant = this.jdbcTemplate.queryForObject(
                SELECT_STP_FROM_STP_WHERE_ID,
                String.class,
                idEnfant);

        /* Lit (en SQL) l'ID du parent dans le stockage. */
        final Long idParentAvant = this.jdbcTemplate.queryForObject(
                SELECT_TP_FROM_STP_WHERE_ID,
                Long.class,
                idEnfant);

        /* Lit (en SQL) le nombre d'enregistrements dans le stockage. */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);
        
        assertThat(libelleAvant).isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);
        assertThat(idParentAvant).isNotNull();
        assertThat(countAvant).isNotNull();

        /* Instancie un objet métier identique à l'existant. */
        final SousTypeProduit sansModification = new SousTypeProduit(
                idEnfant,
                libelleAvant,
                new TypeProduit(idParentAvant, LIBELLE_PARENT_VETEMENT));

        /* ACT :
         * appelle this.service.update(sansModification)
         * sans la moindre modification par rapport à l'existant.
         */
        final SousTypeProduit retour = this.service.update(sansModification);

        /* ASSERT :
         * vérifie que l'application ne réagit pas
         * et ne modifie rien.
         */
        assertThat(retour).isNotNull();
        assertThat(retour).isNotSameAs(sansModification);
        assertThat(retour.getIdSousTypeProduit()).isEqualTo(idEnfant);
        assertThat(retour.getSousTypeProduit()).isEqualTo(libelleAvant);
        assertThat(retour.getTypeProduit()).isNotNull();
        assertThat(retour.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentAvant);

        /* Lit (en SQL) le libellé enfant dans le stockage après update(...). */
        final String libelleApres = this.jdbcTemplate.queryForObject(
                SELECT_STP_FROM_STP_WHERE_ID,
                String.class,
                idEnfant);

        /* Lit (en SQL) l'ID du parent dans le stockage après update(...). */
        final Long idParentApres = this.jdbcTemplate.queryForObject(
                SELECT_TP_FROM_STP_WHERE_ID,
                Long.class,
                idEnfant);

        /* Lit (en SQL) le nombre d'enregistrements dans le stockage après update(...). */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(libelleApres).isEqualTo(libelleAvant);
        assertThat(idParentApres).isEqualTo(idParentAvant);
        assertThat(countApres).isEqualTo(countAvant);

        this.entityManager.clear();

        /* Relit l'objet par le service. */
        final SousTypeProduit relu = this.service.findById(idEnfant);

        assertThat(relu).isNotNull();
        assertThat(relu.getIdSousTypeProduit()).isEqualTo(idEnfant);
        assertThat(relu.getSousTypeProduit()).isEqualTo(libelleAvant);
        assertThat(relu.getTypeProduit()).isNotNull();
        assertThat(relu.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentAvant);

    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que update(nominal) :</p>
     * <ul>
     * <li>retourne un objet non null ;</li>
     * <li>conserve le même identifiant ;</li>
     * <li>modifie le libellé en base ;</li>
     * <li>rend la modification retrouvable via le service ;</li>
     * <li>n'ajoute ni ne supprime aucune ligne.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_NOMINAL)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testUpdateNominalOk() throws Exception {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final Long idEnfant = retrouverIdEnfantPersistantParLibelle(LIBELLE_ENFANT_VETEMENT_FEMME);
        final Long idParentAvant = this.jdbcTemplate.queryForObject(
                SELECT_TP_FROM_STP_WHERE_ID,
                Long.class,
                idEnfant);

        final SousTypeProduit stp =
                new SousTypeProduit(
                        idEnfant,
                        LIBELLE_MODIFIE_FEMME,
                        new TypeProduit(idParentAvant, LIBELLE_PARENT_VETEMENT));

        assertThat(countAvant).isNotNull();
        assertThat(idParentAvant).isNotNull();

        /* ACT :
         * appelle this.service.update(stp).
         */
        final SousTypeProduit retour = this.service.update(stp);

        /* ASSERT :
         * vérifie l'objet retourné.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getIdSousTypeProduit()).isEqualTo(idEnfant);
        assertThat(retour.getSousTypeProduit()).isEqualTo(LIBELLE_MODIFIE_FEMME);
        assertThat(retour.getTypeProduit()).isNotNull();
        assertThat(retour.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentAvant);

        /* Lit le libellé en base après l'update. */
        final String libelleEnBase = this.jdbcTemplate.queryForObject(
                SELECT_STP_FROM_STP_WHERE_ID,
                String.class,
                idEnfant);

        /* Lit le parent en base après l'update. */
        final Long idParentEnBase = this.jdbcTemplate.queryForObject(
                SELECT_TP_FROM_STP_WHERE_ID,
                Long.class,
                idEnfant);

        assertThat(libelleEnBase).isEqualTo(LIBELLE_MODIFIE_FEMME);
        assertThat(idParentEnBase).isEqualTo(idParentAvant);

        this.entityManager.clear();

        /* Relit l'objet via le service. */
        final SousTypeProduit relu = this.service.findById(idEnfant);

        assertThat(relu).isNotNull();
        assertThat(relu.getIdSousTypeProduit()).isEqualTo(idEnfant);
        assertThat(relu.getSousTypeProduit()).isEqualTo(LIBELLE_MODIFIE_FEMME);
        assertThat(relu.getTypeProduit()).isNotNull();
        assertThat(relu.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentAvant);

        /* Relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>garantit que update(parent modifié) :</p>
     * <ul>
     * <li>retourne un objet non null ;</li>
     * <li>conserve le même identifiant ;</li>
     * <li>modifie la clé étrangère parent en base ;</li>
     * <li>rend la modification retrouvable via le service ;</li>
     * <li>n'ajoute ni ne supprime aucune ligne.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_PARENT_MODIFIE)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testUpdateParentModifieOk() throws Exception {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final Long idEnfant = retrouverIdEnfantPersistantParLibelle(LIBELLE_ENFANT_VETEMENT_HOMME);

        /* Crée un nouveau parent persistant. */
        this.jdbcTemplate.update(
                "INSERT INTO TYPES_PRODUIT (TYPE_PRODUIT) VALUES (?)",
                LIBELLE_PARENT_CHAUSSURE);

        final Long idNouveauParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_CHAUSSURE);

        final SousTypeProduit stp =
                new SousTypeProduit(
                        idEnfant,
                        LIBELLE_ENFANT_VETEMENT_HOMME,
                        new TypeProduit(idNouveauParent, LIBELLE_PARENT_CHAUSSURE));

        assertThat(countAvant).isNotNull();
        assertThat(idNouveauParent).isNotNull();

        /* ACT :
         * appelle this.service.update(stp).
         */
        final SousTypeProduit retour = this.service.update(stp);

        /* ASSERT :
         * vérifie l'objet retourné.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getIdSousTypeProduit()).isEqualTo(idEnfant);
        assertThat(retour.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);
        assertThat(retour.getTypeProduit()).isNotNull();
        assertThat(retour.getTypeProduit().getIdTypeProduit()).isEqualTo(idNouveauParent);
        assertThat(retour.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_CHAUSSURE);

        /* Lit le parent en base après l'update. */
        final Long idParentEnBase = this.jdbcTemplate.queryForObject(
                SELECT_TP_FROM_STP_WHERE_ID,
                Long.class,
                idEnfant);

        /* Lit le libellé en base après l'update. */
        final String libelleEnBase = this.jdbcTemplate.queryForObject(
                SELECT_STP_FROM_STP_WHERE_ID,
                String.class,
                idEnfant);

        assertThat(idParentEnBase).isEqualTo(idNouveauParent);
        assertThat(libelleEnBase).isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);

        this.entityManager.clear();

        /* Relit l'objet via le service. */
        final SousTypeProduit relu = this.service.findById(idEnfant);

        assertThat(relu).isNotNull();
        assertThat(relu.getIdSousTypeProduit()).isEqualTo(idEnfant);
        assertThat(relu.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);
        assertThat(relu.getTypeProduit()).isNotNull();
        assertThat(relu.getTypeProduit().getIdTypeProduit()).isEqualTo(idNouveauParent);
        assertThat(relu.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_CHAUSSURE);

        /* Relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    

    
    // ============================= delete ===============================



    /**
     * <div>
     * <p>garantit que delete(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_DELETE_KO_PARAM_NULL} ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_NULL)
    @Test
    public void testDeleteParamNullExceptionAppliParamNull() {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countAvant).isNotNull();

        /* ACT - ASSERT :
         * appelle this.service.delete(null)
         * et vérifie que l'appel
         * - jette une ExceptionAppliParamNull
         * - émet le message MESSAGE_DELETE_KO_PARAM_NULL 
         * (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.delete(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_DELETE_KO_PARAM_NULL);

        /* ASSERT :
         * relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(id null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNonPersistent} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_DELETE_KO_ID_NULL} ;</li>
     * <li>n'altère pas le stockage réel.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_ID_NULL)
    @Test
    public void testDeleteIdNullExceptionAppliParamNonPersistent() {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp = new SousTypeProduit(null, LIBELLE_A_SUPPRIMER, parent);

        assertThat(countAvant).isNotNull();

        /* ACT - ASSERT :
         * appelle this.service.delete(stp)
         * et vérifie que l'appel
         * - jette une ExceptionAppliParamNonPersistent
         * - émet le message MESSAGE_DELETE_KO_ID_NULL
         *   (message contractuel attendu).
         */
        assertThatThrownBy(() -> this.service.delete(stp))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_DELETE_KO_ID_NULL);

        /* ASSERT :
         * relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que delete(absent) :</p>
     * <ul>
     * <li>ne jette pas d'exception ;</li>
     * <li>ne supprime aucune ligne ;</li>
     * <li>reste cohérent avec l'absence de ligne en base
     * pour l'identifiant demandé.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_ABSENT)
    @Test
    public void testDeleteAbsentNeFaitRien() throws Exception {

        /* ARRANGE :
         * lit le nombre de lignes avant l'appel.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                Long.class,
                ID_INEXISTANT);

        final SousTypeProduit stp =
                new SousTypeProduit(ID_INEXISTANT, LIBELLE_INEXISTANT, null);

        assertThat(countAvant).isNotNull();
        assertThat(countEnBase).isNotNull().isZero();

        /* ACT :
         * appelle this.service.delete(stp).
         */
        this.service.delete(stp);

        /* ASSERT :
         * relit le nombre de lignes après l'appel
         * et vérifie que le total n'a pas changé.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        assertThat(countApres).isEqualTo(countAvant);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(nominal) :</p>
     * <ul>
     * <li>crée une ligne à supprimer ;</li>
     * <li>supprime réellement cette ligne en base ;</li>
     * <li>rend l'objet introuvable via le service ;</li>
     * <li>ramène le nombre total de lignes à son niveau initial.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_NOMINAL)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testDeleteNominalOk() throws Exception {

        Long idSupprime = null;

        try {

            /* ARRANGE :
             * lit (en SQL) le nombre de lignes dans le stockage 
             * avant la création.
             */
            final Long countAvant = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                    Long.class);

            final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
            final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);

            assertThat(countAvant).isNotNull();

            /* 
             * Crée un objet métier dédié au test 
             * via un appel à service.creer(...). 
             */
            final SousTypeProduit cree = this.service.creer(
                    new SousTypeProduit(null, LIBELLE_A_SUPPRIMER, parent));

            assertThat(cree).isNotNull();
            assertThat(cree.getIdSousTypeProduit()).isNotNull();

            idSupprime = cree.getIdSousTypeProduit();

            /* Lit (en SQL) le nombre de lignes dans le stockage 
             * après la création d'un objet métier. */
            final Long countApresCreation = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                    Long.class);

            /* Lit (en SQL) le nombre de lignes dans le stockage 
             * possédant l'ID créé. */
            final Long countEnBaseAvantDelete = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
                    Long.class,
                    idSupprime);

            assertThat(countApresCreation).isEqualTo(countAvant + 1L);
            assertThat(countEnBaseAvantDelete).isEqualTo(1L);

            /* ACT :
             * appelle this.service.delete(cree).
             */
            this.service.delete(cree);

            /* ASSERT :
             * vérifie que service.delete(...) 
             * a détruit la ligne crée dans le stockage.
             */
            this.verifierSuppressionEnBase(idSupprime);

            /* Relit (en SQL) le nombre de lignes après la suppression
             * et vérifie que le total revient au niveau initial.
             */
            final Long countApresDelete = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                    Long.class);

            assertThat(countApresDelete).isEqualTo(countAvant);

        } finally {

            /* 
             * Nettoyage de sécurité :
             * supprime la ligne si elle existe encore.
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
     * <li>supprime réellement la ligne lors du premier appel ;</li>
     * <li>ne réagit pas lors du second appel ;</li>
     * <li>ne recrée rien dans le stockage ;</li>
     * <li>laisse le nombre total d'enregistrements inchangé après le second appel.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(double suppression) - ne réagit pas au second appel")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testDeleteDoubleSuppression() throws Exception {

        Long idSupprime = null;

        try {

            /* ARRANGE :
             * lit (en SQL) le nombre d'enregistrements
             * dans le stockage avant création.
             */
            final Long countAvant = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                    Long.class);
            
            assertThat(countAvant).isNotNull();

            /* 
             * Crée un parent de l'objet métier 
             * forcément existant dans le stockage. */
            final Long idParent =
                    retrouverIdParentPersistantParLibelle(
                    		LIBELLE_PARENT_VETEMENT);

            final TypeProduit parent =
                    new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
           

            /* 
             * Crée un objet métier persistant 
             * dédié au test (à supprimer). */
            final SousTypeProduit cree = this.service.creer(
                    new SousTypeProduit(null, LIBELLE_A_SUPPRIMER, parent));

            assertThat(cree).isNotNull();
            assertThat(cree.getIdSousTypeProduit()).isNotNull();

            idSupprime = cree.getIdSousTypeProduit();

            /* ACT :
             * appelle une première fois this.service.delete(cree).
             */
            this.service.delete(cree);

            /* ASSERT :
             * vérifie la suppression réelle dans le stockage.
             */
            this.verifierSuppressionEnBase(idSupprime);

            final Long countApresPremiereSuppression 
            	= this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                    Long.class);

            assertThat(countApresPremiereSuppression).isEqualTo(countAvant);

            /* ACT :
             * appelle une seconde fois this.service.delete(cree)
             * sur le même objet déjà supprimé.
             */
            this.service.delete(cree);

            /* ASSERT :
             * vérifie que le stockage reste inchangé
             * après ce second appel.
             */
            this.verifierSuppressionEnBase(idSupprime);

            final Long countApresSecondeSuppression 
            	= this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                    Long.class);

            assertThat(countApresSecondeSuppression)
            	.isEqualTo(countApresPremiereSuppression);

        } finally {

            /* Nettoyage de sécurité :
             * supprime la ligne si elle existe encore dans le stockage.
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
     * <p>garantit que count() sur la base seedée :</p>
     * <ul>
     * <li>retourne un total >= 0 ;</li>
     * <li>retourne le même total que le DAO ;</li>
     * <li>retourne le même total que le stockage réel lu en SQL.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName(DN_COUNT_NOMINAL)
    @Test
    public void testCountNominalOk() throws Exception {

        /* ARRANGE :
         * lit le total en base via SQL.
         */
        final Long viaSql = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* ACT :
         * lit le total via le service
         * puis via le DAO.
         */
        final long viaService = this.service.count();
        final long viaDao = this.sousTypeProduitDaoJPA.count();

        /* ASSERT :
         * vérifie que count() retourne un total >= 0.
         */
        assertThat(viaService).isGreaterThanOrEqualTo(0L);

        /* Vérifie que count() retourne
         * le même total que le DAO.
         */
        assertThat(viaService).isEqualTo(viaDao);

        /* Vérifie que count() retourne
         * le même total que le stockage réel.
         */
        assertThat(viaSql).isNotNull();
        assertThat(viaService).isEqualTo(viaSql.longValue());

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que count() sur une base vide :</p>
     * <ul>
     * <li>retourne zéro ;</li>
     * <li>retourne le même total que le DAO ;</li>
     * <li>retourne le même total que le stockage réel lu en SQL.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName("count(base vide) - retourne 0")
    @Test
    @Sql(
        scripts = SousTypeProduitGatewayJPAServiceIntegrationTest.CLASSPATH_TRUNCATE_SQL,
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    public void testCountZero() throws Exception {

        /* ARRANGE :
         * lit le total en base via SQL.
         */
        final Long viaSql = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                Long.class);

        /* ACT :
         * lit le total via le service
         * puis via le DAO.
         */
        final long viaService = this.service.count();
        final long viaDao = this.sousTypeProduitDaoJPA.count();

        /* ASSERT :
         * vérifie que le stockage réel est vide.
         */
        assertThat(viaSql).isNotNull().isZero();

        /* Vérifie que count() retourne 0. */
        assertThat(viaService).isZero();

        /* Vérifie que count() retourne
         * le même total que le DAO.
         */
        assertThat(viaService).isEqualTo(viaDao);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que count() suit l'état du stockage :</p>
     * <ul>
     * <li>retourne le total initial du stockage ;</li>
     * <li>augmente de 1 après creer(...) ;</li>
     * <li>revient au total initial après delete(...) ;</li>
     * <li>reste cohérent avec les lectures SQL directes.</li>
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

            /* Lit le nombre d'enregistrements
             * dans le stockage via le service.
             */
            final long countAvantService = this.service.count();
            
            assertThat(countAvantSql).isNotNull();
            assertThat(countAvantService).isEqualTo(countAvantSql.longValue());

            /* Recherche un parent forcément existant dans le stockage. */
            final Long idParent =
                    retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
            final TypeProduit parent =
                    new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);

            /* ACT :
             * appelle this.service.creer(...)
             * pour ajouter un nouvel objet dans le stockage.
             */
            final SousTypeProduit cree = this.service.creer(
                    new SousTypeProduit(null, LIBELLE_A_SUPPRIMER, parent));

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

            /* Lit le nombre d'enregistrements
             * dans le stockage via le service après création.
             */
            final long countApresCreationService = this.service.count();

            assertThat(countApresCreationSql).isNotNull();
            assertThat(countApresCreationSql).isEqualTo(countAvantSql + 1L);
            assertThat(countApresCreationService).isEqualTo(countApresCreationSql.longValue());

            /* ACT :
             * appelle this.service.delete(cree).
             */
            this.service.delete(cree);

            /* ASSERT :
             * lit (en SQL) le nombre d'enregistrements
             * dans le stockage après suppression.
             */
            final Long countApresSuppressionSql = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
                    Long.class);

            /* Lit le nombre d'enregistrements
             * dans le stockage via le service après suppression.
             */
            final long countApresSuppressionService = this.service.count();

            assertThat(countApresSuppressionSql).isNotNull();
            assertThat(countApresSuppressionSql).isEqualTo(countAvantSql);
            assertThat(countApresSuppressionService).isEqualTo(countApresSuppressionSql.longValue());

        } finally {

            /* Nettoyage de sécurité :
             * supprime la ligne si elle existe encore dans le stockage.
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
	 * Vérifie au moyen d'assertions assertJ 
	 * la suppression directement en base via JdbcTemplate
	 * (contourne complètement Hibernate et son cache)
	 *
	 * @param idSupprime L'ID de l'entité supprimée à vérifier
	 * @throws Exception
	 */
	private void verifierSuppressionEnBase(final Long idSupprime) throws Exception {
		
	    // Vérification de l'absence de l'enregistrement
	    final Integer count = jdbcTemplate.queryForObject(
	        SELECT_COUNT_PARAM_STP_FROM_STP_WHERE_ID,
	        Integer.class, idSupprime
	    );
	
	    assertThat(count)
	        .as("L'enregistrement doit être physiquement supprimé de la base (expected: 0, actual: " + count + ")")
	        .isEqualTo(0);
	
	    // Vérification supplémentaire via le service
	    assertThat(this.service.findById(idSupprime))
	        .as("Le service ne doit plus trouver l'objet supprimé")
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
