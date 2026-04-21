package levy.daniel.application.model.services.produittype.gateway.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Comparator;
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

import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliLibelleBlank;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNonPersistent;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNull;
import levy.daniel.application.model.services.produittype.gateway.TypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.pagination.DirectionTri;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;
import levy.daniel.application.model.services.produittype.pagination.TriSpec;
import levy.daniel.application.persistence.metier.produittype.dao.daosJPA.TypeProduitDaoJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.TypeProduitJPA;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * TEST D'INTEGRATION JUnit JUPITER 5
 * </p>
 *
 * <p>
 * Test du Service Gateway
 * <code style="font-weight:bold;">TypeProduitGatewayJPAService</code>.
 * </p>
 *
 * <p>Ce test JUnit autonome utilise :</p>
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
 * @version 4.0
 * @since 2 février 2026
 */
@SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
@Sql(
    scripts = {TypeProduitGatewayJPAServiceIntegrationTest.CLASSPATH_TRUNCATE_SQL,
            TypeProduitGatewayJPAServiceIntegrationTest.CLASSPATH_DATA_SQL},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@DataJpaTest
@ActiveProfiles({ "test-jpa" })
@Import(TypeProduitGatewayJPAService.class)
@ContextConfiguration(classes = TypeProduitGatewayJPAServiceIntegrationTest.ConfigTest.class)
public class TypeProduitGatewayJPAServiceIntegrationTest {

    // *************************** CONSTANTES ******************************/

    /** "test" */
    public static final String PROFILE_TEST = "test";

    /** "classpath:truncate-test.sql". */
    public static final String CLASSPATH_TRUNCATE_SQL = "classpath:truncate-test.sql";

    /** "classpath:data-test.sql" */
    public static final String CLASSPATH_DATA_SQL = "classpath:data-test.sql";

    /** "TypeProduitGatewayJPAService" */
    public static final String QUALIFIER_SERVICE = "TypeProduitGatewayJPAService";

    /** "servicesGateway-Creer" */
    public static final String TAG_CREER = "servicesGateway-Creer";

    /** "servicesGateway-Rechercher" */
    public static final String TAG_RECHERCHER = "servicesGateway-Rechercher";

    /** "servicesGateway-FindByObjetMetier" */
    public static final String TAG_FINDBYOBJETMETIER = "servicesGateway-FindByObjetMetier";

    /** "servicesGateway-RechercherRapide" */
    public static final String TAG_RECHERCHER_RAPIDE = "servicesGateway-RechercherRapide";

    /** "servicesGateway-Pagination" */
    public static final String TAG_PAGINATION = "servicesGateway-Pagination";

    /** "servicesGateway-Update" */
    public static final String TAG_UPDATE = "servicesGateway-Update";

    /** "servicesGateway-Delete" */
    public static final String TAG_DELETE = "servicesGateway-Delete";

    /** "servicesGateway-Count" */
    public static final String TAG_COUNT = "servicesGateway-Count";

    /** 
     * "rechercherTous() - retourne la liste seedée (triée, sans doublons)" 
     */
    public static final String DN_RECHERCHER_TOUS 
    	= "rechercherTous() - retourne la liste seedée (triée, sans doublons)";

    /** 
     * "creer(OK) - ajoute un élément, le rend retrouvable et ne wipe pas les seedés"
     */
    public static final String DN_CREER_OK 
    	= "creer(OK) - ajoute un élément, le rend retrouvable et ne wipe pas les seedés";

    /** 
     * "creer(null) - jette ExceptionAppliParamNull (contrat du port)"
     */
    public static final String DN_CREER_NULL 
    	= "creer(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** 
     * "creer(blank) - jette ExceptionAppliLibelleBlank (contrat du port)"
     */
    public static final String DN_CREER_BLANK 
    	= "creer(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** 
     * "findByObjetMetier(null) - jette ExceptionAppliParamNull (contrat du port)"
     */
    public static final String DN_FINDBYOBJETMETIER_NULL 
    	= "findByObjetMetier(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** 
     * "findByObjetMetier(blank) - jette ExceptionAppliLibelleBlank (contrat du port)"
     */
    public static final String DN_FINDBYOBJETMETIER_BLANK 
    	= "findByObjetMetier(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** 
     * "findByObjetMetier(non trouvé) - retourne null"
     */
    public static final String DN_FINDBYOBJETMETIER_NON_TROUVE 
    	= "findByObjetMetier(non trouvé) - retourne null";

    /** 
     * "findByObjetMetier(trouvé) - retourne l'objet métier" 
     */
    public static final String DN_FINDBYOBJETMETIER_TROUVE 
    	= "findByObjetMetier(trouvé) - retourne l'objet métier";

    /** 
     * "findByObjetMetier(béton) - insensible à l'ID fourni et insensible à la casse (case-insensitive)" 
     */
    public static final String DN_FINDBYOBJETMETIER_BETON 
    	= "findByObjetMetier(béton) - insensible à l'ID fourni et insensible à la casse (case-insensitive)";

    /** 
     * "findByLibelle(blank) - jette ExceptionAppliLibelleBlank (contrat du port)"
     */
    public static final String DN_FINDBYLIBELLE_BLANK 
    	= "findByLibelle(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** 
     * "findByLibelle(non trouvé) - retourne null"
     */
    public static final String DN_FINDBYLIBELLE_NON_TROUVE 
    	= "findByLibelle(non trouvé) - retourne null";

    /** 
     * "findByLibelle(trouvé) - retourne l'objet métier"
     */
    public static final String DN_FINDBYLIBELLE_TROUVE 
    	= "findByLibelle(trouvé) - retourne l'objet métier";

    /** 
     * "findByLibelleRapide(null) - jette ExceptionAppliParamNull (contrat du port)" 
     */
    public static final String DN_RAPIDE_NULL 
    	= "findByLibelleRapide(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** 
     * "findByLibelleRapide(blank) - délègue rechercherTous()"
     */
    public static final String DN_RAPIDE_BLANK 
    	= "findByLibelleRapide(blank) - délègue rechercherTous()";

    /** 
     * "findByLibelleRapide(OK) - retourne les correspondances"
     */
    public static final String DN_RAPIDE_OK 
    	= "findByLibelleRapide(OK) - retourne les correspondances";

    /** 
     * "findById(trouvé) - retourne l'objet métier" 
     */
    public static final String DN_FINDBYID_TROUVE 
    	= "findById(trouvé) - retourne l'objet métier";

    /** 
     * "findById(non trouvé) - retourne null"
     */
    public static final String DN_FINDBYID_NON_TROUVE 
    	= "findById(non trouvé) - retourne null";

    /** 
     * "findById(null) - jette ExceptionAppliParamNull (contrat du port)"
     */
    public static final String DN_FINDBYID_NULL 
    	= "findById(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** 
     * "rechercherTousParPage(null) - applique la pagination par défaut"
     */
    public static final String DN_PAGE_NULL 
    	= "rechercherTousParPage(null) - applique la pagination par défaut";

    /** 
     * "rechercherTousParPage(avec tri) - respecte la pagination et le tri (béton)"
     */
    public static final String DN_PAGE_TRI = "rechercherTousParPage(avec tri) - respecte la pagination et le tri (béton)";

    /** 
     * "update(null) - jette ExceptionAppliParamNull (contrat du port)"
     */
    public static final String DN_UPDATE_NULL 
    	= "update(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** 
     * "update(blank) - jette ExceptionAppliLibelleBlank (contrat du port)"
     */
    public static final String DN_UPDATE_BLANK 
    	= "update(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** 
     * "update(ID null) - jette ExceptionAppliParamNonPersistent (contrat du port)"
     */
    public static final String DN_UPDATE_ID_NULL 
    	= "update(ID null) - jette ExceptionAppliParamNonPersistent (contrat du port)";

    /** 
     * "update(entity inexistante) - retourne null" 
     */
    public static final String DN_UPDATE_INEXISTANTE 
    	= "update(entity inexistante) - retourne null";

    /** 
     * "update(sans modification) - retourne un objet métier équivalent et ne modifie pas le stockage" 
     */
    public static final String DN_UPDATE_SANS_MODIF 
    	= "update(sans modification) - retourne un objet métier équivalent et ne modifie pas le stockage";

    /** 
     * "update(modification) - modifie le stockage et retourne l'objet modifié"
     */
    public static final String DN_UPDATE_AVEC_MODIF 
    	= "update(modification) - modifie le stockage et retourne l'objet modifié";
    
    /** 
     * "update(libellé existant) - vérifie le comportement en cas de doublon" 
     */
    public static final String DN_UPDATE_LIBELLE_EXISTANT =
            "update(libellé existant) - vérifie le comportement en cas de doublon";

    /** 
     * "delete(null) - jette ExceptionAppliParamNull (contrat du port)"
     */
    public static final String DN_DELETE_NULL 
    	= "delete(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** 
     * "delete(OK) - supprime un type créé et le rend introuvable"
     */
    public static final String DN_DELETE_OK 
    	= "delete(OK) - supprime un type créé et le rend introuvable";

    /** 
     * "count() - cohérent avec rechercherTous()" 
     */
    public static final String DN_COUNT_OK 
    	= "count() - cohérent avec rechercherTous()";

    /** 
     * "delete(ID inexistant) - ne lève pas d'exception" 
     */
    public static final String DN_DELETE_ID_INEXISTANT 
    	= "delete(ID inexistant) - ne lève pas d'exception";
    
    /** 
     * "delete(double suppression) - ne lève pas d'exception"
     */
    public static final String DN_DELETE_DOUBLE =
            "delete(double suppression) - ne lève pas d'exception";

    /** 
     * "findByLibelleRapide(case-insensitive) - recherche insensible à la casse"
     */
    public static final String DN_RAPIDE_CASE_INSENSITIVE 
    	= "findByLibelleRapide(case-insensitive) - recherche insensible à la casse";

    /** 
     * "findByLibelleRapide(dédoublonnage) - pas de doublons dans les résultats"
     */
    public static final String DN_RAPIDE_DEDOUBLONNAGE 
    	= "findByLibelleRapide(dédoublonnage) - pas de doublons dans les résultats";

    /** 
     * "rechercherTousParPage(vide) - retourne une page vide si la base est vide"
     */
    public static final String DN_PAGE_VIDE 
    	= "rechercherTousParPage(vide) - retourne une page vide si la base est vide";

    /** 
     * "rechercherTousParPage(taille > total) - retourne tous les éléments" 
     */
    public static final String DN_PAGE_TAILLE_SUP 
    	= "rechercherTousParPage(taille > total) - retourne tous les éléments";
    
    /** 
     * "findByLibelleRapide(aucun résultat) - retourne une liste vide" 
     */
    public static final String DN_RAPIDE_AUCUN =
            "findByLibelleRapide(aucun résultat) - retourne une liste vide";

    /** 
     * "zzz"
     */
    public static final String RECHERCHE_AUCUN = "zzz";
    
    /** 
     * "rechercherTousParPage(page hors bornes) - retourne une page vide cohérente"
     */
    public static final String DN_PAGE_HORS_BORNES =
            "rechercherTousParPage(page hors bornes) - retourne une page vide cohérente";
    
    /** 
     * "rechercherTousParPage(taille zéro) - normalise la taille et retourne la première page cohérente"
     */
    public static final String DN_PAGE_TAILLE_ZERO =
            "rechercherTousParPage(taille zéro) - normalise la taille et retourne la première page cohérente";

    /** "   " */
    public static final String BLANK = "   ";

    /** "vêtement" */
    public static final String VETEMENT = "vêtement";

    /** "bazar" */
    public static final String BAZAR = "bazar";

    /** "outillage" */
    public static final String OUTILLAGE = "outillage";

    /** "tourisme" */
    public static final String TOURISME = "tourisme";

    /** "vestons" */
    public static final String VESTONS = "vestons";

    /** "peinture" */
    public static final String NOUVEAU_TYPE_1 = "peinture";

    /** "électronique" */
    public static final String NOUVEAU_TYPE_2 = "électronique";

    /** "-modif" */
    public static final String SUFFIX_MODIF = "-modif";

    /** "a-supprimer" */
    public static final String TEMP_A_SUPPRIMER = "a-supprimer";

    /** "me" */
    public static final String RECHERCHE_ME = "me";

    /** "ve" */
    public static final String RECHERCHE_VE = "ve";

    /** "VE" */
    public static final String RECHERCHE_VE_MAJ = "VE";

    /** 999_999L */
    public static final Long ID_INEXISTANTE = Long.valueOf(999_999L);

    /** "typeProduit" */
    public static final String PROP_TYPEPRODUIT = "typeProduit";

    /** 
     * "findByLibelle(null) - jette ExceptionAppliLibelleBlank (contrat du port)"
     */
    public static final String DN_FINDBYLIBELLE_NULL 
    	= "findByLibelle(null) - jette ExceptionAppliLibelleBlank (contrat du port)";
    
    /**
     * "SELECT COUNT(*) FROM TYPES_PRODUIT"
     */
    public static final String SELECT_COUNT_FROM_TYPES_PRODUIT 
    	= "SELECT COUNT(*) FROM TYPES_PRODUIT";
    
    /**
     * "SELECT TYPE_PRODUIT FROM TYPES_PRODUIT"
     */
    public static final String SELECT_TYPEPRODUIT_FROM_TYPES_PRODUIT 
    	= "SELECT TYPE_PRODUIT FROM TYPES_PRODUIT";
    
    /**
     * "SELECT ID_TYPE_PRODUIT FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) = UPPER(?)"
     */
    public static final String SELECT_PARAM_ID_FROM_TYPES_PRODUIT 
    	= "SELECT ID_TYPE_PRODUIT FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) = UPPER(?)";
    
    /**
     * "SELECT COUNT(*) FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) = UPPER(?)"
     */
    public static final String SELECT_COUNT_PARAM_TYPEPRODUIT_FROM_TYPES_PRODUIT
    	= "SELECT COUNT(*) FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) = UPPER(?)";
    
    /**
     * "SELECT TYPE_PRODUIT FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) LIKE UPPER(?)"
     */
    public static final String SELECT_PARAM_TYPEPRODUIT_FROM_TYPES_PRODUIT_LIKE
    	= "SELECT TYPE_PRODUIT FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) LIKE UPPER(?)";
    
    /**
     * "update(libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)"
     */
    public static final String DN_UPDATE_LIBELLE_NULL
    	= "update(libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)";

    // *************************** ATTRIBUTS *******************************/

    /**
     * <div>
     * <p>SERVICE
     * <code style="font-weight:bold;">TypeProduitGatewayJPAService</code>
     * injecté par SPRING dans le constructeur.</p>
     * </div>
     */
    private final TypeProduitGatewayIService service;
    
    /**
     * <div>
     * <p>JdbcTemplate (Spring) pour lire la base directement
     * et prouver physiquement les écritures (bypass Hibernate).</p>
     * </div>
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ************************* METHODES **********************************/
    

    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE 1.</p>
     * <p>Constructeur d'injection (Spring Test + JUnit Jupiter).</p>
     * </div>
     *
     * @param pService :
     * service gateway injecté par Spring
     */
    public TypeProduitGatewayJPAServiceIntegrationTest(
            @Qualifier(QUALIFIER_SERVICE)
            final TypeProduitGatewayIService pService) {
        this.service = pService;
    }

    
    
 // =============================== CONFIGURATION =========================
    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * Classe interne de configuration Spring.
     * </p>
     * <ul>
     * <li>@Configuration</li>
     * <li>@EnableJpaRepositories(basePackageClasses = TypeProduitDaoJPA.class)</li>
     * <li>@EntityScan(basePackageClasses = TypeProduitJPA.class)</li>
     * </ul>
     * </div>
     *
     * @author Daniel Lévy
     * @version 1.0
     * @since 24 janvier 2026
     */
    @Configuration
    @EnableJpaRepositories(basePackageClasses = TypeProduitDaoJPA.class)
    @EntityScan(basePackageClasses = TypeProduitJPA.class)
    public static final class ConfigTest { // NOPMD by danyl on 24/01/2026 00:00
    }

    
    
    // =========================== OUTILS TESTS =========================

    
    
    /**
     * <div>
     * <p>Vérifie que la liste est triée par libellé (ordre naturel).</p>
     * </div>
     *
     * @param pListe : List&lt;TypeProduit&gt
     */
    private static void assertListeTrieeParLibelle(
    		final List<TypeProduit> pListe) {
    	
        if ((pListe == null) || (pListe.size() < 2)) {
            return;
        }

        for (int i = 0; i < pListe.size() - 1; i++) {
            final String courant = pListe.get(i).getTypeProduit();
            final String suivant = pListe.get(i + 1).getTypeProduit();
            assertThat(Comparator.<String>naturalOrder().compare(courant, suivant))
                .isLessThanOrEqualTo(0);
        }
    } // __________________________________________________________________
    
    
    
    // ========================= HELPERS ===================================
    
  
    
    /**
     * <div>
     * <p>Lit le libellé TYPE_PRODUIT physiquement 
     * en base (bypass Hibernate).</p>
     * </div>
     *
     * @param pId Long : ID_TYPE_PRODUIT.
     * @return String : valeur de la colonne TYPE_PRODUIT.
     */
    private String lireLibelleTypeProduitEnBase(final Long pId) {
    	
        return this.jdbcTemplate.queryForObject(
            "SELECT TYPE_PRODUIT FROM TYPES_PRODUIT WHERE ID_TYPE_PRODUIT = ?",
            String.class, pId
        );
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Compte physiquement en base les lignes 
     * TYPES_PRODUIT pour un ID donné.</p>
     * </div>
     *
     * @param pId Long : ID_TYPE_PRODUIT.
     * @return Long : nombre de lignes (0 ou 1 attendu).
     */
    private Long compterTypeProduitEnBase(final Long pId) {
    	
        return this.jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM TYPES_PRODUIT WHERE ID_TYPE_PRODUIT = ?",
            Long.class, pId
        );
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Restaure physiquement en base un TypeProduit 
     * (libellé) pour isoler les tests.</p>
     * </div>
     *
     * @param pId Long : ID_TYPE_PRODUIT.
     * @param pLibelle String : TYPE_PRODUIT.
     */
    private void restaurerTypeProduitEnBase(
            final Long pId,
            final String pLibelle) {

        final int updated = this.jdbcTemplate.update(
            "UPDATE TYPES_PRODUIT SET TYPE_PRODUIT = ? WHERE ID_TYPE_PRODUIT = ?",
            pLibelle, pId
        );

        assertThat(updated)
            .as("La restauration en base doit modifier exactement 1 ligne.")
            .isEqualTo(1);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Supprime physiquement en base 
     * un TypeProduit par ID (nettoyage test).</p>
     * </div>
     *
     * @param pId Long : ID_TYPE_PRODUIT.
     */
    private void supprimerTypeProduitEnBase(final Long pId) {

        final int deleted = this.jdbcTemplate.update(
            "DELETE FROM TYPES_PRODUIT WHERE ID_TYPE_PRODUIT = ?",
            pId
        );

        assertThat(deleted)
            .as("La suppression en base doit modifier exactement 1 ligne.")
            .isEqualTo(1);
        
    } // __________________________________________________________________

    

    // =============================== TESTS ===============================
    
    

    // ============================ CREER ================================
    
    
	
    /**
     * <div>
     * <p>garantit que creer(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull}</li>
     * <li>émet un message
     * {@link TypeProduitGatewayIService#MESSAGE_CREER_KO_PARAM_NULL}</li>
     * <li>n'écrit rien dans le stockage</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_NULL)
    @Test
    public void testCreerNull() {
    	
    	/* ARRANGE - ACT - ASSERT */
    	/* Garantit que this.service.creer(null)
    	 * - jette une ExceptionAppliParamNull
    	 * - émet un message MESSAGE_CREER_KO_PARAM_NULL.
    	 */
        assertThatThrownBy(() -> this.service.creer(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_CREER_KO_PARAM_NULL);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que creer(blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank}</li>
     * <li>émet un message
     * {@link TypeProduitGatewayIService#MESSAGE_CREER_KO_LIBELLE_BLANK}</li>
     * <li>n'écrit rien dans le stockage</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_BLANK)
    @Test
    public void testCreerBlank() {
    	
    	/* ARRANGE :
    	 * prépare un objet métier dont le libellé est blank,
    	 * afin de vérifier le contrôle applicatif du PORT
    	 * avant toute tentative d'accès au stockage réel.
    	 */
        final TypeProduit blank = new TypeProduit(BLANK);
        
        /* ACT - ASSERT :
         * garantit que this.service.creer(blank)
         * - jette une ExceptionAppliLibelleBlank
         * - émet un message MESSAGE_CREER_KO_LIBELLE_BLANK.
         */
        assertThatThrownBy(() -> this.service.creer(blank))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_CREER_KO_LIBELLE_BLANK);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que si l'appelant tente creer(...)
     * avec un libellé déjà présent en base :</p>
     * <ul>
     * <li>le stockage réel refuse la création du doublon ;</li>
     * <li>l'exception observable en intégration
     * est une {@link org.springframework.transaction.UnexpectedRollbackException} ;</li>
     * <li>aucune nouvelle ligne n'est créée en base ;</li>
     * <li>l'unique ligne seedée portant déjà ce libellé
     * reste inchangée.</li>
     * </ul>
     * <p>Ce test vérifie donc le comportement réellement visible
     * à travers le proxy transactionnel Spring/Hibernate,
     * sans réinterroger JPA après l'échec attendu.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(libellé existant) : jette UnexpectedRollbackException et ne crée aucune nouvelle ligne")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCreerLibelleExistant() throws Exception {

        /* ARRANGE :
         * lit d'abord l'état physique initial de la base
         * par SQL direct, afin de disposer d'une preuve indépendante
         * du contexte Hibernate.
         *
         * On vérifie ici :
         * - le nombre total de lignes ;
         * - le nombre de lignes portant déjà le libellé VETEMENT ;
         * - l'identifiant de la ligne seedée existante.
         */
        final Long countAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        final Long countLibelleAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_TYPEPRODUIT_FROM_TYPES_PRODUIT,
                Long.class,
                VETEMENT);

        final Long idSeedAvant = this.jdbcTemplate.queryForObject(
                SELECT_PARAM_ID_FROM_TYPES_PRODUIT,
                Long.class,
                VETEMENT);

        final TypeProduit aCreer = new TypeProduit(VETEMENT);

        assertThat(countAvant).isNotNull();
        assertThat(countLibelleAvant).isEqualTo(1L);
        assertThat(idSeedAvant).isNotNull();

        /* ACT - ASSERT :
         * sollicite la méthode creer(...)
         * avec un libellé déjà présent.
         *
         * Au comportement réel observé en intégration,
         * le rollback transactionnel est visible côté test
         * sous la forme d'une UnexpectedRollbackException.
         */        
        assertThatThrownBy(() -> this.service.creer(aCreer))
            .isInstanceOf(org.springframework.transaction.UnexpectedRollbackException.class);
        
        /* ASSERT :
         * contrôle ensuite par SQL direct
         * qu'aucun effet de bord n'a été produit en base.
         *
         * On évite volontairement tout nouvel appel JPA ici,
         * pour ne pas réutiliser un contexte de persistance
         * potentiellement marqué en erreur après l'échec attendu.
         */
        final Long countApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        final Long countLibelleApres = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_TYPEPRODUIT_FROM_TYPES_PRODUIT,
                Long.class,
                VETEMENT);

        final Long idSeedApres = this.jdbcTemplate.queryForObject(
                SELECT_PARAM_ID_FROM_TYPES_PRODUIT,
                Long.class,
                VETEMENT);

        /* Garantit qu'aucune nouvelle ligne
         * n'a été créée dans la table.
         */
        assertThat(countApres).isEqualTo(countAvant);

        /* Garantit qu'il n'existe toujours
         * qu'une seule ligne portant ce libellé.
         */
        assertThat(countLibelleApres).isEqualTo(1L);

        /* Garantit enfin que la ligne seedée initiale
         * est restée strictement la même.
         */
        assertThat(idSeedApres).isEqualTo(idSeedAvant);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que creer(OK) :</p>
     * <ul>
     * <li>crée réellement une ligne en base</li>
     * <li>retourne un {@link TypeProduit} persistant</li>
     * <li>retourne un objet métier portant un identifiant généré</li>
     * <li>rend la donnée retrouvable en base et via le service</li>
     * <li>ne supprime ni n'altère les données seedées</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_OK)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCreerOK() throws Exception {
    	
    	/* ARRANGE :
    	 * prépare un objet métier valide à créer
    	 * et mémorise le nombre de lignes avant création.
    	 *
    	 * Le test est volontairement exécuté hors transaction de test
    	 * pour prouver une écriture physique réelle en base,
    	 * puis réaliser un nettoyage physique explicite en finally.
    	 */
        final long countAvant = this.service.count();
        final TypeProduit aCreer = new TypeProduit(NOUVEAU_TYPE_1);
        
        /* ACT :
         * sollicite la méthode creer(...)
         * dans un scénario nominal complet de persistance réelle.
         */
        final TypeProduit cree = this.service.creer(aCreer);
        
        /* ASSERT :
         * garantit d'abord que l'objet métier retourné
         * est bien persistant et correctement renseigné.
         */
        assertThat(cree).isNotNull();
        assertThat(cree.getIdTypeProduit()).isNotNull().isPositive();
        assertThat(cree.getTypeProduit()).isEqualTo(NOUVEAU_TYPE_1);
        
        final Long id = cree.getIdTypeProduit();
        
        try {
        	
        	/* Garantit que la création augmente bien le nombre total
        	 * de lignes dans le stockage réel.
        	 */
            final long countApres = this.service.count();
            assertThat(countApres).isEqualTo(countAvant + 1L);
            
            /* Garantit physiquement en base
             * qu'une seule ligne porte bien l'identifiant créé.
             */
            assertThat(compterTypeProduitEnBase(id)).isEqualTo(1L);
            
            /* Garantit physiquement en base
             * que la colonne TYPE_PRODUIT a bien été écrite
             * avec le libellé métier attendu.
             */
            final String libelleEnBase = lireLibelleTypeProduitEnBase(id);
            assertThat(libelleEnBase).isEqualTo(NOUVEAU_TYPE_1);
            
            /* Garantit que l'objet nouvellement créé
             * est bien retrouvable via le service.
             */
            final TypeProduit relu = this.service.findById(id);
            assertThat(relu).isNotNull();
            assertThat(relu.getIdTypeProduit()).isEqualTo(id);
            assertThat(relu.getTypeProduit()).isEqualTo(NOUVEAU_TYPE_1);
            
            /* Garantit enfin que les données seedées
             * restent présentes après la création.
             */
            final List<TypeProduit> liste = this.service.rechercherTous();
            assertThat(liste)
                .extracting(TypeProduit::getTypeProduit)
                .contains(VETEMENT, BAZAR, OUTILLAGE, TOURISME, VESTONS, NOUVEAU_TYPE_1);
            
        } finally {
        	
        	/* Nettoyage physique :
        	 * supprime explicitement la ligne créée,
        	 * afin de garantir l'isolation du test
        	 * même en cas d'échec d'assertion en amont.
        	 */
            supprimerTypeProduitEnBase(id);
            
        }
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que plusieurs appels successifs à creer(...) :</p>
     * <ul>
     * <li>créent plusieurs lignes distinctes en base</li>
     * <li>attribuent des identifiants différents à chaque objet créé</li>
     * <li>rendent chaque création retrouvable séparément</li>
     * <li>augmentent le compteur total du nombre exact de créations</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(plusieurs créations) : crée plusieurs lignes distinctes et toutes retrouvables")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCreerPlusieurs() throws Exception {
    	
    	/* ARRANGE :
    	 * mémorise le nombre total avant création
    	 * puis prépare deux créations nominales distinctes.
    	 */
        final long countAvant = this.service.count();
        
        Long id1 = null;
        Long id2 = null;
        
        try {
        	
        	/* ACT :
        	 * exécute deux créations successives
        	 * sur deux libellés différents.
        	 */
            final TypeProduit cree1 = this.service.creer(new TypeProduit(NOUVEAU_TYPE_1));
            final TypeProduit cree2 = this.service.creer(new TypeProduit(NOUVEAU_TYPE_2));
            
            /* ASSERT :
             * garantit d'abord que les deux objets retournés
             * sont persistants et distincts.
             */
            assertThat(cree1).isNotNull();
            assertThat(cree2).isNotNull();
            assertThat(cree1.getIdTypeProduit()).isNotNull().isPositive();
            assertThat(cree2.getIdTypeProduit()).isNotNull().isPositive();
            assertThat(cree1.getIdTypeProduit()).isNotEqualTo(cree2.getIdTypeProduit());
            assertThat(cree1.getTypeProduit()).isEqualTo(NOUVEAU_TYPE_1);
            assertThat(cree2.getTypeProduit()).isEqualTo(NOUVEAU_TYPE_2);
            
            id1 = cree1.getIdTypeProduit();
            id2 = cree2.getIdTypeProduit();
            
            /* Garantit que le compteur total
             * augmente exactement de deux lignes.
             */
            final long countApres = this.service.count();
            assertThat(countApres).isEqualTo(countAvant + 2L);
            
            /* Garantit physiquement en base
             * que chaque identifiant correspond à une ligne réelle.
             */
            assertThat(compterTypeProduitEnBase(id1)).isEqualTo(1L);
            assertThat(compterTypeProduitEnBase(id2)).isEqualTo(1L);
            
            /* Garantit physiquement en base
             * que chaque ligne porte le bon libellé métier.
             */
            assertThat(lireLibelleTypeProduitEnBase(id1)).isEqualTo(NOUVEAU_TYPE_1);
            assertThat(lireLibelleTypeProduitEnBase(id2)).isEqualTo(NOUVEAU_TYPE_2);
            
            /* Garantit que chaque création
             * est retrouvable séparément via le service.
             */
            final TypeProduit relu1 = this.service.findById(id1);
            final TypeProduit relu2 = this.service.findById(id2);
            assertThat(relu1).isNotNull();
            assertThat(relu2).isNotNull();
            assertThat(relu1.getTypeProduit()).isEqualTo(NOUVEAU_TYPE_1);
            assertThat(relu2.getTypeProduit()).isEqualTo(NOUVEAU_TYPE_2);
            
            /* Garantit enfin que la liste globale
             * contient à la fois les données seedées
             * et les deux nouveaux types créés.
             */
            final List<TypeProduit> liste = this.service.rechercherTous();
            assertThat(liste)
                .extracting(TypeProduit::getTypeProduit)
                .contains(
                        VETEMENT,
                        BAZAR,
                        OUTILLAGE,
                        TOURISME,
                        VESTONS,
                        NOUVEAU_TYPE_1,
                        NOUVEAU_TYPE_2);
            
        } finally {
        	
        	/* Nettoyage physique :
        	 * supprime explicitement les lignes créées,
        	 * en commençant par la seconde puis la première,
        	 * afin de garantir l'isolation du test.
        	 */
            if (id2 != null) {
                supprimerTypeProduitEnBase(id2);
            }
            if (id1 != null) {
                supprimerTypeProduitEnBase(id1);
            }
            
        }
        
    } // __________________________________________________________________


    
    // ======================== RechercherTous ============================

    
    
    /**
     * <div>
     * <p>garantit que rechercherTous() sur la base seedée :</p>
     * <ul>
     * <li>retourne une liste non null et non vide ;</li>
     * <li>retourne exactement les libellés physiquement présents en base ;</li>
     * <li>retourne autant d'objets métier que de lignes présentes en base ;</li>
     * <li>retourne une liste triée par libellé et sans doublon.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(base seedée) - retourne exactement l'état physique trié et sans doublon")
    @Test
    public void testRechercherTous() throws Exception {
    	
        /* ARRANGE :
         * lit d'abord l'état physique actuel de la base
         * via JdbcTemplate, afin de disposer d'une référence
         * indépendante d'Hibernate.
         *
         * Ce test vérifie ensuite que la liste renvoyée par le service
         * correspond exactement à cet état physique.
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        final List<String> libellesEnBase = this.jdbcTemplate.queryForList(
                SELECT_TYPEPRODUIT_FROM_TYPES_PRODUIT,
                String.class);

        assertThat(countEnBase).isNotNull().isPositive();
        assertThat(libellesEnBase).isNotNull().isNotEmpty();

        /* Trie la référence lue en base
         * pour la comparer au résultat métier attendu,
         * puisque rechercherTous() doit renvoyer une liste triée.
         */
        libellesEnBase.sort(Comparator.naturalOrder());

        /* ACT :
         * sollicite la méthode rechercherTous()
         * dans le scénario nominal de la base seedée.
         */
        final List<TypeProduit> resultats = this.service.rechercherTous();

        /* ASSERT :
         * vérifie d'abord que la méthode retourne bien
         * une liste métier exploitable.
         */
        assertThat(resultats).isNotNull().isNotEmpty();
        assertThat((long) resultats.size()).isEqualTo(countEnBase.longValue());

        final List<String> libellesResultats = resultats.stream()
                .map(TypeProduit::getTypeProduit)
                .toList();

        /* Vérifie ensuite que les libellés métier retournés
         * correspondent exactement aux libellés physiquement présents en base,
         * une fois l'ordre naturel appliqué.
         */
        assertThat(libellesResultats)
            .containsExactlyElementsOf(libellesEnBase);

        /* Vérifie que les données seedées attendues
         * sont bien présentes dans le résultat.
         */
        assertThat(libellesResultats)
            .contains(VETEMENT, BAZAR, OUTILLAGE, TOURISME, VESTONS);

        /* Vérifie enfin les propriétés attendues côté service :
         * pas de doublon métier et ordre alphabétique.
         */
        assertThat(libellesResultats).doesNotHaveDuplicates();
        assertListeTrieeParLibelle(resultats);
        
    } // ________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTous() sur une base vidée :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>retourne une liste vide ;</li>
     * <li>reste cohérent avec l'état physique vide de la base.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(base vide) - retourne une liste vide non null")
    @Test
    @Sql(
        scripts = TypeProduitGatewayJPAServiceIntegrationTest.CLASSPATH_TRUNCATE_SQL,
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    public void testRechercherTousBaseVide() throws Exception {
    	
        /* ARRANGE :
         * remplace pour ce test la préparation standard
         * par le seul script de vidage,
         * afin d'obtenir une base réellement vide.
         *
         * Ce test vérifie ensuite que le service
         * reste cohérent avec cet état physique.
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        assertThat(countEnBase).isNotNull().isZero();

        /* ACT :
         * sollicite la recherche complète
         * sur une base ne contenant aucune ligne.
         */
        final List<TypeProduit> resultats = this.service.rechercherTous();

        /* ASSERT :
         * vérifie que la méthode ne retourne jamais null,
         * même lorsque le stockage réel est vide.
         */
        assertThat(resultats).isNotNull().isEmpty();
        
    } // ________________________________________________________________


    
    // ================== rechercherTousParPage ===========================
    
    

    /**
     * <div>
     * <p>garantit que rechercherTousParPage(null) :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>applique les paramètres par défaut
     * de {@link RequetePage} ;</li>
     * <li>retourne un contenu cohérent avec l'état physique de la base ;</li>
     * <li>retourne un contenu trié par libellé.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName(DN_PAGE_NULL)
    @Test
    public void testRechercherTousParPageNull() throws Exception {
    	
        /* ARRANGE :
         * lit d'abord l'état physique de la base,
         * afin de comparer le résultat paginé
         * à une référence indépendante d'Hibernate.
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        final List<String> libellesEnBase = this.jdbcTemplate.queryForList(
                SELECT_TYPEPRODUIT_FROM_TYPES_PRODUIT,
                String.class);

        assertThat(countEnBase).isNotNull().isPositive();
        assertThat(libellesEnBase).isNotNull().isNotEmpty();

        libellesEnBase.sort(Comparator.naturalOrder());

        final int tailleAttendue = Math.min(
                RequetePage.TAILLE_DEFAUT,
                libellesEnBase.size());

        /* ACT :
         * sollicite la méthode avec une requête nulle,
         * ce qui doit conduire à l'usage
         * de la pagination par défaut.
         */
        final ResultatPage<TypeProduit> page =
                this.service.rechercherTousParPage(null);

        /* ASSERT :
         * vérifie d'abord la cohérence générale
         * de l'enveloppe paginée renvoyée.
         */
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotNull();
        assertThat(page.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(page.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(page.getTotalElements()).isEqualTo(countEnBase.longValue());
        assertThat(page.getContent()).hasSize(tailleAttendue);

        final List<String> libellesPage = page.getContent().stream()
                .map(TypeProduit::getTypeProduit)
                .toList();

        /* Vérifie ensuite que la première page retournée
         * correspond exactement au début de l'état physique trié.
         */
        assertThat(libellesPage)
            .containsExactlyElementsOf(
                    libellesEnBase.subList(0, tailleAttendue));

        /* Vérifie enfin que le contenu est trié.
         */
        assertListeTrieeParLibelle(page.getContent());
        
    } // ________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTousParPage(avec tri) :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>respecte le numéro de page et la taille demandés ;</li>
     * <li>respecte le tri ascendant demandé sur le libellé ;</li>
     * <li>retourne un contenu cohérent avec l'état physique de la base.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName(DN_PAGE_TRI)
    @Test
    public void testRechercherTousParPageAvecTri() throws Exception {
    	
        /* ARRANGE :
         * prépare une requête paginée explicite
         * avec un tri ascendant sur le libellé métier.
         *
         * Le test vérifie ensuite que la page retournée
         * correspond exactement au segment attendu
         * de l'état physique trié.
         */
        final List<String> libellesEnBase = this.jdbcTemplate.queryForList(
                SELECT_TYPEPRODUIT_FROM_TYPES_PRODUIT,
                String.class);

        assertThat(libellesEnBase).isNotNull().isNotEmpty();
        libellesEnBase.sort(Comparator.naturalOrder());

        final List<TriSpec> tris = new ArrayList<TriSpec>();
        tris.add(new TriSpec(PROP_TYPEPRODUIT, DirectionTri.ASC));

        final RequetePage requete = new RequetePage(0, 3, tris);

        /* ACT :
         * sollicite la pagination sur la première page
         * avec taille 3 et tri ascendant.
         */
        final ResultatPage<TypeProduit> page =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie la cohérence générale de la page.
         */
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotNull().hasSize(3);
        assertThat(page.getPageNumber()).isEqualTo(0);
        assertThat(page.getPageSize()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(libellesEnBase.size());

        final List<String> libellesPage = page.getContent().stream()
                .map(TypeProduit::getTypeProduit)
                .toList();

        /* Vérifie que le contenu retourné
         * correspond exactement aux trois premiers libellés
         * de l'état physique trié.
         */
        assertThat(libellesPage)
            .containsExactlyElementsOf(libellesEnBase.subList(0, 3));

        /* Vérifie enfin le tri alphabétique du contenu.
         */
        assertListeTrieeParLibelle(page.getContent());
        
    } // ________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTousParPage(...) sur une base vidée :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>retourne un contenu vide ;</li>
     * <li>retourne un total à zéro ;</li>
     * <li>reste cohérent avec l'état physique vide de la base.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName(DN_PAGE_VIDE)
    @Test
    @Sql(
        scripts = TypeProduitGatewayJPAServiceIntegrationTest.CLASSPATH_TRUNCATE_SQL,
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    public void testRechercherTousParPageVide() throws Exception {
    	
        /* ARRANGE :
         * prépare pour ce test une base réellement vide
         * en ne rejouant que le script de vidage.
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        assertThat(countEnBase).isNotNull().isZero();

        final RequetePage requete = new RequetePage(0, 10, new ArrayList<>());

        /* ACT :
         * sollicite la pagination
         * sur une base ne contenant aucune ligne.
         */
        final ResultatPage<TypeProduit> page =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie que la page retournée
         * reste exploitable et cohérente.
         */
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotNull().isEmpty();
        assertThat(page.getPageNumber()).isEqualTo(0);
        assertThat(page.getPageSize()).isEqualTo(10);
        assertThat(page.getTotalElements()).isZero();
        
    } // ________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTousParPage(taille &gt; total) :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>retourne tous les éléments disponibles ;</li>
     * <li>retourne un total cohérent avec l'état physique de la base ;</li>
     * <li>retourne un contenu trié par libellé.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName(DN_PAGE_TAILLE_SUP)
    @Test
    public void testRechercherTousParPageTailleSuperieure() throws Exception {
    	
        /* ARRANGE :
         * lit l'état physique de la base
         * puis construit une requête dont la taille
         * dépasse le nombre total de lignes.
         */
        final List<String> libellesEnBase = this.jdbcTemplate.queryForList(
                SELECT_TYPEPRODUIT_FROM_TYPES_PRODUIT,
                String.class);

        assertThat(libellesEnBase).isNotNull().isNotEmpty();
        libellesEnBase.sort(Comparator.naturalOrder());

        final RequetePage requete =
                new RequetePage(0, libellesEnBase.size() + 10, new ArrayList<>());

        /* ACT :
         * sollicite la pagination
         * avec une taille supérieure au total disponible.
         */
        final ResultatPage<TypeProduit> page =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie que tous les éléments sont renvoyés.
         */
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotNull().hasSize(libellesEnBase.size());
        assertThat(page.getPageNumber()).isEqualTo(0);
        assertThat(page.getPageSize()).isEqualTo(libellesEnBase.size() + 10);
        assertThat(page.getTotalElements()).isEqualTo(libellesEnBase.size());

        final List<String> libellesPage = page.getContent().stream()
                .map(TypeProduit::getTypeProduit)
                .toList();

        assertThat(libellesPage).containsExactlyElementsOf(libellesEnBase);
        assertListeTrieeParLibelle(page.getContent());
        
    } // _________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTousParPage(page hors bornes) :</p>
     * <ul>
     * <li>retourne une page non null ;</li>
     * <li>retourne un contenu vide ;</li>
     * <li>conserve un total cohérent avec l'état physique de la base ;</li>
     * <li>reste cohérent malgré un index de page très au-delà du total.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName(DN_PAGE_HORS_BORNES)
    @Test
    public void testRechercherTousParPageHorsBornes() throws Exception {
	
        /* ARRANGE :
         * lit le nombre réel de lignes présentes en base
         * puis prépare une requête avec un index de page
         * volontairement très élevé.
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        assertThat(countEnBase).isNotNull().isPositive();

        final RequetePage requete =
                new RequetePage(9999, 10, new ArrayList<>());

        /* ACT :
         * sollicite la pagination très au-delà
         * du nombre réel de pages disponibles.
         */
        final ResultatPage<TypeProduit> page =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie que le contenu est vide
         * tout en conservant les métadonnées cohérentes.
         */
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotNull().isEmpty();
        assertThat(page.getPageNumber()).isEqualTo(9999);
        assertThat(page.getPageSize()).isEqualTo(10);
        assertThat(page.getTotalElements()).isEqualTo(countEnBase.longValue());
        
    } // _________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTousParPage(taille zéro) :</p>
     * <ul>
     * <li>normalise la taille demandée ;</li>
     * <li>retourne une page non null ;</li>
     * <li>retourne un contenu cohérent avec la première page attendue ;</li>
     * <li>retourne un total cohérent avec l'état physique de la base.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName(DN_PAGE_TAILLE_ZERO)
    @Test
    public void testRechercherTousParPageTailleZero() throws Exception {
	
        /* ARRANGE :
         * lit l'état physique de la base
         * puis prépare une requête avec une taille à zéro.
         *
         * Le comportement réel observé passe par la normalisation
         * de la taille au niveau de RequetePage.
         */
        final List<String> libellesEnBase = this.jdbcTemplate.queryForList(
                SELECT_TYPEPRODUIT_FROM_TYPES_PRODUIT,
                String.class);

        assertThat(libellesEnBase).isNotNull().isNotEmpty();
        libellesEnBase.sort(Comparator.naturalOrder());

        final RequetePage requete =
                new RequetePage(0, 0, new ArrayList<>());

        final int tailleAttendue = Math.min(
                requete.getPageSize(),
                libellesEnBase.size());

        /* ACT :
         * sollicite la pagination
         * avec une taille initialement nulle.
         */
        final ResultatPage<TypeProduit> page =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie que la taille renvoyée
         * correspond à la taille normalisée de la requête
         * et que le contenu correspond à la première page attendue.
         */
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotNull().hasSize(tailleAttendue);
        assertThat(page.getPageNumber()).isEqualTo(requete.getPageNumber());
        assertThat(page.getPageSize()).isEqualTo(requete.getPageSize());
        assertThat(page.getTotalElements()).isEqualTo(libellesEnBase.size());

        final List<String> libellesPage = page.getContent().stream()
                .map(TypeProduit::getTypeProduit)
                .toList();

        assertThat(libellesPage)
            .containsExactlyElementsOf(
                    libellesEnBase.subList(0, tailleAttendue));

        assertListeTrieeParLibelle(page.getContent());
	
    } // _________________________________________________________________
    
    
    
    // ======================== findByObjetMetier =========================
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL}.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_NULL)
    @Test
    public void testFindByObjetMetierNull() {
    	
        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel avec un paramètre null
         * jette une ExceptionAppliParamNull
         * avec un message MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(libellé null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK}.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testFindByObjetMetierLibelleNull() {
    	
        /* ARRANGE :
         * prépare un objet métier dont le libellé est null,
         * afin de vérifier le contrôle applicatif
         * effectué avant toute recherche réelle.
         */
        final TypeProduit libelleNull = new TypeProduit(null);
        
        /* ACT - ASSERT :
         * vérifie que l'appel avec un libellé null
         * jette une ExceptionAppliLibelleBlank
         * avec un message MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(libelleNull))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK}.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_BLANK)
    @Test
    public void testFindByObjetMetierBlank() {
    	
        /* ARRANGE :
         * prépare un objet métier dont le libellé est blank (espaces),
         * afin de vérifier le contrôle applicatif
         * effectué avant toute recherche réelle.
         */
        final TypeProduit blank = new TypeProduit(BLANK);
        
        /* ACT - ASSERT :
         * vérifie que l'appel avec un libellé blank (espaces)
         * jette une ExceptionAppliLibelleBlank
         * avec un message MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(blank))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(non trouvé) :</p>
     * <ul>
     * <li>retourne {@code null} ;</li>
     * <li>reste cohérent avec l'absence physique de ligne correspondante
     * dans le stockage.</li>
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
         * vérifie d'abord par SQL direct
         * qu'aucune ligne ne porte ce libellé.
         *
         * Le test compare ensuite le résultat du service
         * à cet état physique de référence.
         */
        final String inexistantLibelle = "inexistant";

        /*
         * Compte combien de lignes de la table TYPES_PRODUIT 
         * ont une valeur de colonne TYPE_PRODUIT égale à la 
         * valeur fournie en paramètre, 
         * sans tenir compte des majuscules/minuscules.
         * - SELECT COUNT(*) → demande un nombre de lignes.
         * - FROM TYPES_PRODUIT → dans la table TYPES_PRODUIT
         * - WHERE UPPER(TYPE_PRODUIT) = UPPER(?) on compare :
         * -- la valeur de la colonne TYPE_PRODUIT, convertie en majuscules
         * -- avec le paramètre préparé fourni par JdbcTemplate ?, lui aussi converti en majuscules
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_TYPEPRODUIT_FROM_TYPES_PRODUIT,
                Long.class,
                inexistantLibelle);

        /* Assure que la requête SQL n'a pas retourné null 
         * mais que son résultat est zéro. */
        assertThat(countEnBase).isNotNull().isZero();

        final TypeProduit inexistant = new TypeProduit(inexistantLibelle);

        /* ACT :
         * sollicite la recherche métier
         * avec un libellé absent du stockage réel.
         */
        final TypeProduit retour = this.service.findByObjetMetier(inexistant);

        /* ASSERT :
         * vérifie que le service reste cohérent
         * avec l'absence physique de résultat en base.
         * Assure que service.findByObjetMetier(inexistant) retourne null.
         */
        assertThat(retour).isNull();
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(trouvé) :</p>
     * <ul>
     * <li>retourne un objet métier non null ;</li>
     * <li>retourne l'identifiant réellement présent en base ;</li>
     * <li>retourne le libellé réellement présent en base.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_TROUVE)
    @Test
    public void testFindByObjetMetierTrouve() throws Exception {
    	
        /* ARRANGE :
         * lit d'abord l'état physique réel de la base
         * pour le libellé seedé recherché.
         *
         * Le test compare ensuite la réponse du service
         * à cette référence indépendante d'Hibernate.
         */
        final Long idEnBase = this.jdbcTemplate.queryForObject(
                SELECT_PARAM_ID_FROM_TYPES_PRODUIT,
                Long.class,
                VETEMENT);

        assertThat(idEnBase).isNotNull();

        final String libelleEnBase = lireLibelleTypeProduitEnBase(idEnBase);
        assertThat(libelleEnBase).isEqualTo(VETEMENT);

        final TypeProduit metier = new TypeProduit(VETEMENT);

        /* ACT :
         * sollicite la recherche métier
         * avec un objet portant le libellé seedé.
         */
        final TypeProduit retour = this.service.findByObjetMetier(metier);

        /* ASSERT :
         * vérifie que l'objet métier retourné
         * correspond exactement à la ligne physique attendue.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getIdTypeProduit()).isEqualTo(idEnBase);
        assertThat(retour.getTypeProduit()).isEqualTo(libelleEnBase);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(béton) :</p>
     * <ul>
     * <li>ignore l'identifiant porté par l'objet d'entrée ;</li>
     * <li>effectue la recherche sur le seul libellé métier ;</li>
     * <li>reste insensible à la casse ;</li>
     * <li>retourne dans les deux variantes
     * le même objet persistant attendu.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_BETON)
    @Test
    public void testFindByObjetMetierBeton() throws Exception {
    	
        /* ARRANGE :
         * lit d'abord la ligne physique de référence
         * correspondant au libellé seedé.
         *
         * Le test vérifie ensuite deux variantes :
         * - un objet d'entrée avec un faux ID ;
         * - un objet d'entrée avec le bon libellé en majuscules.
         */
        final Long idEnBase = this.jdbcTemplate.queryForObject(
                SELECT_PARAM_ID_FROM_TYPES_PRODUIT,
                Long.class,
                VETEMENT);

        assertThat(idEnBase).isNotNull();

        final String libelleEnBase = lireLibelleTypeProduitEnBase(idEnBase);
        assertThat(libelleEnBase).isEqualTo(VETEMENT);

        final TypeProduit avecIdFaux = new TypeProduit(ID_INEXISTANTE, VETEMENT);
        final TypeProduit casse = new TypeProduit(VETEMENT.toUpperCase(Locale.getDefault()));

        /* ACT :
         * sollicite deux fois la méthode,
         * dans les deux variantes métier à contrôler.
         */
        final TypeProduit retourAvecIdFaux = this.service.findByObjetMetier(avecIdFaux);
        final TypeProduit retourCasse = this.service.findByObjetMetier(casse);

        /* ASSERT :
         * vérifie d'abord que les deux recherches aboutissent.
         */
        assertThat(retourAvecIdFaux).isNotNull();
        assertThat(retourCasse).isNotNull();

        /* Vérifie ensuite que l'identifiant fourni par l'appelant
         * n'influence pas la recherche réelle.
         */
        assertThat(retourAvecIdFaux.getIdTypeProduit()).isEqualTo(idEnBase);
        assertThat(retourAvecIdFaux.getTypeProduit()).isEqualTo(libelleEnBase);

        /* Vérifie enfin que la recherche est insensible à la casse
         * et retrouve la même ligne persistante.
         */
        assertThat(retourCasse.getIdTypeProduit()).isEqualTo(idEnBase);
        assertThat(retourCasse.getTypeProduit()).isEqualTo(libelleEnBase);

    } // __________________________________________________________________
    
    
    
    // ========================== findByLibelle ===========================
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelle(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK}.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_NULL)
    @Test
    public void testFindByLibelleNull() {
    	
        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel avec un libellé null
         * jette une ExceptionAppliLibelleBlank
         * avec un message MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByLibelle(null))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelle(blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK}.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_BLANK)
    @Test
    public void testFindByLibelleBlank() {
    	
        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel avec un libellé blank (espaces)
         * jette une ExceptionAppliLibelleBlank
         * avec un message MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByLibelle(BLANK))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelle(non trouvé) :</p>
     * <ul>
     * <li>retourne {@code null} ;</li>
     * <li>reste cohérent avec l'absence physique de ligne correspondante
     * dans le stockage.</li>
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
         * vérifie d'abord par SQL direct
         * qu'aucune ligne ne porte ce libellé,
         * sans tenir compte des majuscules/minuscules.
         *
         * Le test compare ensuite le résultat du service
         * à cet état physique de référence.
         */
        final String inexistantLibelle = "inexistant";

        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_PARAM_TYPEPRODUIT_FROM_TYPES_PRODUIT,
                Long.class,
                inexistantLibelle);

        /* Assure que la requête SQL n'a pas retourné null
         * mais que son résultat est zéro.
         */
        assertThat(countEnBase).isNotNull().isZero();

        /* ACT :
         * sollicite la recherche par libellé exact
         * avec un libellé absent du stockage réel.
         */
        final TypeProduit retour = this.service.findByLibelle(inexistantLibelle);

        /* ASSERT :
         * vérifie que le service reste cohérent
         * avec l'absence physique de résultat en base.
         * Assure que service.findByLibelle(inexistantLibelle) retourne null.
         */
        assertThat(retour).isNull();
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelle(trouvé) :</p>
     * <ul>
     * <li>retourne un objet métier non null ;</li>
     * <li>retourne l'identifiant réellement présent en base ;</li>
     * <li>retourne le libellé réellement présent en base.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_TROUVE)
    @Test
    public void testFindByLibelleTrouve() throws Exception {
    	
        /* ARRANGE :
         * lit d'abord l'état physique réel de la base
         * pour le libellé seedé recherché.
         *
         * Le test compare ensuite la réponse du service
         * à cette référence indépendante d'Hibernate.
         */
        final Long idEnBase = this.jdbcTemplate.queryForObject(
                SELECT_PARAM_ID_FROM_TYPES_PRODUIT,
                Long.class,
                VETEMENT);

        assertThat(idEnBase).isNotNull();

        final String libelleEnBase = lireLibelleTypeProduitEnBase(idEnBase);
        assertThat(libelleEnBase).isEqualTo(VETEMENT);

        /* ACT :
         * sollicite la recherche par libellé exact
         * avec le libellé seedé attendu.
         */
        final TypeProduit retour = this.service.findByLibelle(VETEMENT);

        /* ASSERT :
         * vérifie que l'objet métier retourné
         * correspond exactement à la ligne physique attendue.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getIdTypeProduit()).isEqualTo(idEnBase);
        assertThat(retour.getTypeProduit()).isEqualTo(libelleEnBase);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelle(case-insensitive) :</p>
     * <ul>
     * <li>reste insensible à la casse ;</li>
     * <li>retourne l'identifiant réellement présent en base ;</li>
     * <li>retourne le libellé réellement persisté.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelle(case-insensitive) - retourne le même objet persistant quelle que soit la casse")
    @Test
    public void testFindByLibelleCaseInsensitive() throws Exception {
    	
        /* ARRANGE :
         * lit d'abord la ligne physique de référence
         * correspondant au libellé seedé.
         *
         * Le test vérifie ensuite qu'une recherche
         * avec le même libellé en majuscules
         * retrouve bien cette même ligne persistante.
         */
        final Long idEnBase = this.jdbcTemplate.queryForObject(
                SELECT_PARAM_ID_FROM_TYPES_PRODUIT,
                Long.class,
                VETEMENT);

        assertThat(idEnBase).isNotNull();

        final String libelleEnBase = lireLibelleTypeProduitEnBase(idEnBase);
        assertThat(libelleEnBase).isEqualTo(VETEMENT);

        final String libelleMajuscule =
                VETEMENT.toUpperCase(Locale.getDefault());

        /* ACT :
         * sollicite la recherche par libellé exact
         * avec une casse différente.
         */
        final TypeProduit retour = this.service.findByLibelle(libelleMajuscule);

        /* ASSERT :
         * vérifie que la recherche est insensible à la casse
         * et retrouve la même ligne persistante.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getIdTypeProduit()).isEqualTo(idEnBase);
        assertThat(retour.getTypeProduit()).isEqualTo(libelleEnBase);
        
    } // __________________________________________________________________
    
    
    
    // ======================== findByLibelleRapide =======================
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL}.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_NULL)
    @Test
    public void testFindByLibelleRapideNull() {
    	
        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel avec un paramètre null
         * jette une ExceptionAppliParamNull
         * avec un message MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(blank) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>retourne le même contenu que rechercherTous() ;</li>
     * <li>retourne un contenu trié et sans doublon.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_BLANK)
    @Test
    public void testFindByLibelleRapideBlank() throws Exception {
    	
        /* ARRANGE :
         * lit d'abord le résultat de référence
         * via rechercherTous(),
         * car le contrat attendu ici est une délégation
         * vers la recherche complète.
         */
        final List<TypeProduit> tous = this.service.rechercherTous();

        assertThat(tous).isNotNull().isNotEmpty();

        final List<String> libellesAttendues = tous.stream()
                .map(TypeProduit::getTypeProduit)
                .toList();

        /* ACT :
         * sollicite la recherche rapide
         * avec un contenu blank.
         */
        final List<TypeProduit> retour = this.service.findByLibelleRapide(BLANK);

        /* ASSERT :
         * vérifie que le résultat retourné
         * correspond exactement à celui de rechercherTous().
         */
        assertThat(retour).isNotNull();

        final List<String> libellesRetour = retour.stream()
                .map(TypeProduit::getTypeProduit)
                .toList();

        assertThat(libellesRetour).containsExactlyElementsOf(libellesAttendues);
        assertThat(libellesRetour).doesNotHaveDuplicates();
        assertListeTrieeParLibelle(retour);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(OK) :</p>
     * <ul>
     * <li>retourne une liste non null et non vide ;</li>
     * <li>retourne des libellés contenant le motif demandé ;</li>
     * <li>retourne un contenu cohérent avec l'état physique de la base ;</li>
     * <li>retourne un contenu trié et sans doublon.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_OK)
    @Test
    public void testFindByLibelleRapideOK() throws Exception {
    	
        /* ARRANGE :
         * lit d'abord par SQL direct les libellés physiques
         * contenant le motif recherché,
         * sans tenir compte des majuscules/minuscules.
         *
         * Le test compare ensuite la réponse du service
         * à cette référence indépendante d'Hibernate.
         */
        final List<String> libellesEnBase = this.jdbcTemplate.queryForList(
                SELECT_PARAM_TYPEPRODUIT_FROM_TYPES_PRODUIT_LIKE,
                String.class,
                "%" + RECHERCHE_ME + "%");

        assertThat(libellesEnBase).isNotNull().isNotEmpty();
        libellesEnBase.sort(Comparator.naturalOrder());

        /* ACT :
         * sollicite la recherche rapide
         * avec un motif réellement présent en base.
         */
        final List<TypeProduit> retour = this.service.findByLibelleRapide(RECHERCHE_ME);

        /* ASSERT :
         * vérifie d'abord que le service retourne
         * une liste exploitable et non vide.
         */
        assertThat(retour).isNotNull().isNotEmpty();

        final List<String> libellesRetour = retour.stream()
                .map(TypeProduit::getTypeProduit)
                .toList();

        /* Vérifie ensuite que le contenu retourné
         * correspond exactement à la référence SQL attendue.
         */
        assertThat(libellesRetour).containsExactlyElementsOf(libellesEnBase);

        /* Vérifie enfin les propriétés attendues côté service :
         * tous les libellés contiennent le motif,
         * le résultat est trié et sans doublon.
         */
        assertThat(libellesRetour)
            .allMatch(libelle -> libelle != null
                    && libelle.toUpperCase(Locale.getDefault())
                        .contains(RECHERCHE_ME.toUpperCase(Locale.getDefault())));
        assertThat(libellesRetour).doesNotHaveDuplicates();
        assertListeTrieeParLibelle(retour);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(case-insensitive) :</p>
     * <ul>
     * <li>reste insensible à la casse ;</li>
     * <li>retourne le même contenu quelle que soit la casse du motif ;</li>
     * <li>retourne un contenu trié et sans doublon.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_CASE_INSENSITIVE)
    @Test
    public void testFindByLibelleRapideCaseInsensitive() throws Exception {
    	
        /* ARRANGE :
         * prépare deux recherches portant sur le même motif,
         * l'une en minuscules et l'autre en majuscules.
         */
        final List<String> libellesEnBase = this.jdbcTemplate.queryForList(
                SELECT_PARAM_TYPEPRODUIT_FROM_TYPES_PRODUIT_LIKE,
                String.class,
                "%" + RECHERCHE_VE + "%");

        assertThat(libellesEnBase).isNotNull().isNotEmpty();
        libellesEnBase.sort(Comparator.naturalOrder());

        /* ACT :
         * sollicite deux fois la recherche rapide
         * avec deux casses différentes du même motif.
         */
        final List<TypeProduit> retourMin = this.service.findByLibelleRapide(RECHERCHE_VE);
        final List<TypeProduit> retourMaj = this.service.findByLibelleRapide(RECHERCHE_VE_MAJ);

        /* ASSERT :
         * vérifie d'abord que les deux recherches aboutissent.
         */
        assertThat(retourMin).isNotNull().isNotEmpty();
        assertThat(retourMaj).isNotNull().isNotEmpty();

        final List<String> libellesRetourMin = retourMin.stream()
                .map(TypeProduit::getTypeProduit)
                .toList();

        final List<String> libellesRetourMaj = retourMaj.stream()
                .map(TypeProduit::getTypeProduit)
                .toList();

        /* Vérifie ensuite que les deux résultats
         * correspondent exactement à la même référence physique.
         */
        assertThat(libellesRetourMin).containsExactlyElementsOf(libellesEnBase);
        assertThat(libellesRetourMaj).containsExactlyElementsOf(libellesEnBase);

        /* Vérifie enfin que la recherche est bien insensible à la casse,
         * avec des résultats triés et sans doublon.
         */
        assertThat(libellesRetourMin).containsExactlyElementsOf(libellesRetourMaj);
        assertThat(libellesRetourMin).doesNotHaveDuplicates();
        assertThat(libellesRetourMaj).doesNotHaveDuplicates();
        assertListeTrieeParLibelle(retourMin);
        assertListeTrieeParLibelle(retourMaj);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(dédoublonnage) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>ne retourne pas de doublons ;</li>
     * <li>retourne un contenu trié.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_DEDOUBLONNAGE)
    @Test
    public void testFindByLibelleRapideDedoublonnage() throws Exception {
    	
        /* ARRANGE :
         * prépare une recherche dont le motif
         * correspond à plusieurs lignes seedées.
         */
        final List<TypeProduit> retour = this.service.findByLibelleRapide(RECHERCHE_ME);

        /* ASSERT :
         * vérifie que la recherche retourne bien une liste exploitable,
         * puis qu'aucun doublon n'est présent dans les libellés métier.
         */
        assertThat(retour).isNotNull().isNotEmpty();

        final List<String> libellesRetour = retour.stream()
                .map(TypeProduit::getTypeProduit)
                .toList();

        assertThat(libellesRetour).doesNotHaveDuplicates();
        assertListeTrieeParLibelle(retour);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(aucun résultat) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>retourne une liste vide ;</li>
     * <li>reste cohérent avec l'absence physique de correspondance
     * dans le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_AUCUN)
    @Test
    public void testFindByLibelleRapideAucunResultat() throws Exception {

        /* ARRANGE :
         * vérifie d'abord par SQL direct
         * qu'aucune ligne ne correspond au motif recherché,
         * sans tenir compte des majuscules/minuscules.
         */
        final List<String> libellesEnBase = this.jdbcTemplate.queryForList(
                SELECT_PARAM_TYPEPRODUIT_FROM_TYPES_PRODUIT_LIKE,
                String.class,
                "%" + RECHERCHE_AUCUN + "%");

        assertThat(libellesEnBase).isNotNull().isEmpty();

        /* ACT :
         * sollicite la recherche rapide
         * avec un motif absent du stockage réel.
         */
        final List<TypeProduit> retour =
                this.service.findByLibelleRapide(RECHERCHE_AUCUN);

        /* ASSERT :
         * vérifie que le service reste cohérent
         * avec l'absence physique de correspondance en base.
         */
        assertThat(retour).isNotNull().isEmpty();
        
    } // __________________________________________________________________
    
    
    
    // ============================ findById ==============================
    
    
    
    /**
     * <div>
     * <p>garantit que findById(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#MESSAGE_FINDBYID_KO_PARAM_NULL}.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_NULL)
    @Test
    public void testFindByIdNull() {
    	
        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel avec un identifiant null
         * jette une ExceptionAppliParamNull
         * avec un message MESSAGE_FINDBYID_KO_PARAM_NULL
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findById(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYID_KO_PARAM_NULL);
        
    } // ________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findById(non trouvé) :</p>
     * <ul>
     * <li>retourne {@code null} ;</li>
     * <li>reste cohérent avec l'absence physique de ligne correspondante
     * dans le stockage.</li>
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
         * vérifie d'abord par SQL direct
         * qu'aucune ligne ne porte cet identifiant.
         *
         * Le test compare ensuite le résultat du service
         * à cet état physique de référence.
         */
        final Long countEnBase = compterTypeProduitEnBase(ID_INEXISTANTE);

        /* Assure que la lecture SQL directe
         * n'a pas retourné null
         * et que cet identifiant est absent de la base.
         */
        assertThat(countEnBase).isNotNull().isZero();

        /* ACT :
         * sollicite la recherche par identifiant
         * avec un ID absent du stockage réel.
         */
        final TypeProduit retour = this.service.findById(ID_INEXISTANTE);

        /* ASSERT :
         * vérifie que le service reste cohérent
         * avec l'absence physique de résultat en base.
         * Assure que service.findById(ID_INEXISTANTE) retourne null.
         */
        assertThat(retour).isNull();
        
    } // ________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findById(trouvé) :</p>
     * <ul>
     * <li>retourne un objet métier non null ;</li>
     * <li>retourne l'identifiant réellement présent en base ;</li>
     * <li>retourne le libellé réellement présent en base.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_TROUVE)
    @Test
    public void testFindByIdTrouve() throws Exception {
    	
        /* ARRANGE :
         * lit d'abord l'identifiant physique réel
         * de la ligne seedée recherchée,
         * puis relit son libellé directement en base.
         *
         * Le test compare ensuite la réponse du service
         * à cette référence indépendante d'Hibernate.
         */
        final Long idEnBase = this.jdbcTemplate.queryForObject(
                SELECT_PARAM_ID_FROM_TYPES_PRODUIT,
                Long.class,
                VETEMENT);

        assertThat(idEnBase).isNotNull();

        final Long countEnBase = compterTypeProduitEnBase(idEnBase);
        assertThat(countEnBase).isNotNull().isEqualTo(1L);

        final String libelleEnBase = lireLibelleTypeProduitEnBase(idEnBase);
        assertThat(libelleEnBase).isEqualTo(VETEMENT);

        /* ACT :
         * sollicite la recherche par identifiant
         * avec l'ID réellement présent en base.
         */
        final TypeProduit retour = this.service.findById(idEnBase);

        /* ASSERT :
         * vérifie que l'objet métier retourné
         * correspond exactement à la ligne physique attendue.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getIdTypeProduit()).isEqualTo(idEnBase);
        assertThat(retour.getTypeProduit()).isEqualTo(libelleEnBase);
        
    } // ________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findById(ID créé) :</p>
     * <ul>
     * <li>retrouve un objet nouvellement créé ;</li>
     * <li>retourne l'identifiant créé ;</li>
     * <li>retourne le libellé réellement persisté en base.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(ID créé) - retrouve l'objet nouvellement persisté")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testFindByIdIdCree() throws Exception {
    	
        /* ARRANGE :
         * crée d'abord physiquement un nouveau type produit,
         * afin de vérifier ensuite que la recherche par identifiant
         * fonctionne aussi sur une donnée créée pendant le test.
         */
        final TypeProduit cree = this.service.creer(new TypeProduit(NOUVEAU_TYPE_1));

        assertThat(cree).isNotNull();
        assertThat(cree.getIdTypeProduit()).isNotNull().isPositive();

        final Long id = cree.getIdTypeProduit();

        try {
        	
            final Long countEnBase = compterTypeProduitEnBase(id);
            assertThat(countEnBase).isNotNull().isEqualTo(1L);

            final String libelleEnBase = lireLibelleTypeProduitEnBase(id);
            assertThat(libelleEnBase).isEqualTo(NOUVEAU_TYPE_1);

            /* ACT :
             * sollicite la recherche par identifiant
             * avec l'ID de l'objet nouvellement créé.
             */
            final TypeProduit retour = this.service.findById(id);

            /* ASSERT :
             * vérifie que le service retrouve bien
             * la ligne nouvellement persistée.
             */
            assertThat(retour).isNotNull();
            assertThat(retour.getIdTypeProduit()).isEqualTo(id);
            assertThat(retour.getTypeProduit()).isEqualTo(libelleEnBase);
        	
        } finally {
        	
            /* Nettoyage physique :
             * supprime explicitement la ligne créée,
             * afin de garantir l'isolation du test.
             */
            supprimerTypeProduitEnBase(id);
        	
        }
        
    } // ________________________________________________________________
    
    
    
    // ============================= update ===============================
    
    
    
    /**
     * <div>
     * <p>garantit que update(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#MESSAGE_UPDATE_KO_PARAM_NULL}.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_NULL)
    @Test
    public void testUpdateNull() {
    	
        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel avec un paramètre null
         * jette une ExceptionAppliParamNull
         * avec un message MESSAGE_UPDATE_KO_PARAM_NULL
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.update(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARAM_NULL);
        
    } // _________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(libellé null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_BLANK}.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_LIBELLE_NULL)
    @Test
    public void testUpdateLibelleNull() {
    	
        /* ARRANGE :
         * prépare un objet métier persistant en apparence
         * dont le libellé vaut null,
         * afin de vérifier le contrôle applicatif
         * effectué avant toute mise à jour réelle.
         */
        final TypeProduit libelleNull = new TypeProduit(Long.valueOf(1L), null);
        
        /* ACT - ASSERT :
         * vérifie que l'appel avec un libellé null
         * jette une ExceptionAppliLibelleBlank
         * avec un message MESSAGE_UPDATE_KO_LIBELLE_BLANK
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.update(libelleNull))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_BLANK);
        
    } // _________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_BLANK}.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_BLANK)
    @Test
    public void testUpdateBlank() {
    	
        /* ARRANGE :
         * prépare un objet métier persistant en apparence
         * dont le libellé est blank (espaces),
         * afin de vérifier le contrôle applicatif
         * effectué avant toute mise à jour réelle.
         */
        final TypeProduit blank = new TypeProduit(Long.valueOf(1L), BLANK);
        
        /* ACT - ASSERT :
         * vérifie que l'appel avec un libellé blank (espaces)
         * jette une ExceptionAppliLibelleBlank
         * avec un message MESSAGE_UPDATE_KO_LIBELLE_BLANK
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.update(blank))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_BLANK);
        
    } // _________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNonPersistent} ;</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#MESSAGE_UPDATE_KO_NON_PERSISTENT}
     * suivi du libellé porté par l'objet.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_ID_NULL)
    @Test
    public void testUpdateIdNull() {
    	
        /* ARRANGE :
         * prépare un objet métier non persistant
         * dont l'identifiant est null,
         * afin de vérifier le contrôle de persistance
         * imposé par le contrat du port.
         */
        final TypeProduit nonPersistant = new TypeProduit(VETEMENT);
        
        /* ACT - ASSERT :
         * vérifie que l'appel avec un ID null
         * jette une ExceptionAppliParamNonPersistent
         * avec un message MESSAGE_UPDATE_KO_NON_PERSISTENT + VETEMENT
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.update(nonPersistant))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_UPDATE_KO_NON_PERSISTENT + VETEMENT);
        
    } // _________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(entity inexistante) :</p>
     * <ul>
     * <li>retourne {@code null} ;</li>
     * <li>reste cohérent avec l'absence physique de ligne correspondante
     * dans le stockage.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_INEXISTANTE)
    @Test
    public void testUpdateEntityInexistante() throws Exception {
    	
        /* ARRANGE :
         * vérifie d'abord par SQL direct
         * qu'aucune ligne ne porte cet identifiant.
         *
         * Le test compare ensuite le résultat du service
         * à cet état physique de référence.
         */
        final Long countEnBase = compterTypeProduitEnBase(ID_INEXISTANTE);

        assertThat(countEnBase).isNotNull().isZero();

        final TypeProduit inexistante = new TypeProduit(ID_INEXISTANTE, VETEMENT);

        /* ACT :
         * sollicite la mise à jour
         * d'un objet dont l'identifiant n'existe pas en base.
         */
        final TypeProduit retour = this.service.update(inexistante);

        /* ASSERT :
         * vérifie que le service reste cohérent
         * avec l'absence physique de ligne correspondante.
         * Assure que service.update(inexistante) retourne null.
         */
        assertThat(retour).isNull();
        
    } // _________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(sans modification) :</p>
     * <ul>
     * <li>retourne un objet métier non null ;</li>
     * <li>retourne un objet distinct de l'objet fourni en entrée ;</li>
     * <li>ne modifie pas l'état physique de la base ;</li>
     * <li>reste cohérent avec une relecture par le service.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_SANS_MODIF)
    @Test
    public void testUpdateSansModification() throws Exception {
    	
        /* ARRANGE :
         * lit d'abord la ligne seedée de référence,
         * puis mémorise son état physique en base.
         *
         * Le test vérifie ensuite qu'une mise à jour sans modification
         * ne change pas le stockage réel.
         */
        final TypeProduit seed = this.service.findByLibelle(VETEMENT);
        assertThat(seed).isNotNull();
        assertThat(seed.getIdTypeProduit()).isNotNull();

        final Long id = seed.getIdTypeProduit();
        final String libelleAvant = lireLibelleTypeProduitEnBase(id);
        assertThat(libelleAvant).isEqualTo(VETEMENT);

        final TypeProduit sansModif = new TypeProduit(id, VETEMENT);

        /* ACT :
         * sollicite la mise à jour
         * avec exactement les mêmes données métier.
         */
        final TypeProduit retour = this.service.update(sansModif);

        /* ASSERT :
         * vérifie d'abord que le service retourne
         * un objet métier exploitable et distinct de l'entrée.
         */
        assertThat(retour).isNotNull();
        assertThat(retour).isNotSameAs(sansModif);
        assertThat(retour.getIdTypeProduit()).isEqualTo(id);
        assertThat(retour.getTypeProduit()).isEqualTo(VETEMENT);

        /* Vérifie ensuite que l'état physique en base
         * n'a subi aucune modification.
         */
        final String libelleApres = lireLibelleTypeProduitEnBase(id);
        assertThat(libelleApres).isEqualTo(libelleAvant);

        /* Vérifie enfin que la relecture par le service
         * reste cohérente avec l'état physique inchangé.
         */
        final TypeProduit relu = this.service.findById(id);
        assertThat(relu).isNotNull();
        assertThat(relu.getIdTypeProduit()).isEqualTo(id);
        assertThat(relu.getTypeProduit()).isEqualTo(libelleAvant);
        
    } // _________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(modification) :</p>
     * <ul>
     * <li>retourne un objet non {@code null} portant le même ID ;</li>
     * <li>modifie réellement la colonne TYPE_PRODUIT en base ;</li>
     * <li>reste cohérent avec les relectures JDBC et service ;</li>
     * <li>accepte aussi une seconde mise à jour en majuscules
     * sur le même enregistrement.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_AVEC_MODIF)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testUpdateAvecModification() throws Exception {

        /* ARRANGE :
         * lit d'abord la ligne seedée de référence,
         * puis mémorise son état physique initial en base.
         *
         * Le test est exécuté hors transaction de test
         * pour prouver une écriture physique réelle,
         * puis restaurer explicitement l'état initial en finally.
         */
        final TypeProduit seed = this.service.findByLibelle(TOURISME);
        assertThat(seed).isNotNull();
        assertThat(seed.getIdTypeProduit()).isNotNull();

        final Long id = seed.getIdTypeProduit();

        final String libelleAvant = lireLibelleTypeProduitEnBase(id);
        assertThat(libelleAvant).isEqualTo(TOURISME);

        final String nouveauLibelle = TOURISME + SUFFIX_MODIF;

        try {

            /* ACT :
             * sollicite une première mise à jour
             * vers un nouveau libellé métier.
             */
            final TypeProduit aModifier = new TypeProduit(id, nouveauLibelle);
            final TypeProduit retour = this.service.update(aModifier);

            /* ASSERT :
             * vérifie d'abord que le service retourne
             * un objet cohérent avec la ligne modifiée.
             */
            assertThat(retour).isNotNull();
            assertThat(retour.getIdTypeProduit()).isEqualTo(id);
            assertThat(retour.getTypeProduit()).isEqualTo(nouveauLibelle);

            /* Vérifie ensuite physiquement en base
             * que la colonne TYPE_PRODUIT a bien été modifiée.
             */
            final String libelleEnBase = lireLibelleTypeProduitEnBase(id);
            assertThat(libelleEnBase).isEqualTo(nouveauLibelle);

            /* Vérifie enfin la cohérence
             * de la relecture via le service.
             */
            final TypeProduit relu = this.service.findById(id);
            assertThat(relu).isNotNull();
            assertThat(relu.getIdTypeProduit()).isEqualTo(id);
            assertThat(relu.getTypeProduit()).isEqualTo(nouveauLibelle);

            /* ACT :
             * sollicite ensuite une seconde mise à jour
             * vers le même libellé en majuscules,
             * afin de vérifier le comportement réel
             * sur une nouvelle modification du même enregistrement.
             */
            final String upper = nouveauLibelle.toUpperCase(Locale.getDefault());
            final TypeProduit aModifierCS = new TypeProduit(id, upper);
            final TypeProduit retourCS = this.service.update(aModifierCS);

            /* ASSERT :
             * vérifie que cette seconde mise à jour
             * est elle aussi répercutée en base et côté service.
             */
            assertThat(retourCS).isNotNull();
            assertThat(retourCS.getIdTypeProduit()).isEqualTo(id);
            assertThat(retourCS.getTypeProduit()).isEqualTo(upper);

            final String libelleEnBaseCS = lireLibelleTypeProduitEnBase(id);
            assertThat(libelleEnBaseCS).isEqualTo(upper);

            final TypeProduit reluApresCS = this.service.findById(id);
            assertThat(reluApresCS).isNotNull();
            assertThat(reluApresCS.getIdTypeProduit()).isEqualTo(id);
            assertThat(reluApresCS.getTypeProduit()).isEqualTo(upper);

        } finally {

            /* Nettoyage physique :
             * restaure explicitement le libellé initial,
             * afin de garantir l'isolation du test.
             */
            restaurerTypeProduitEnBase(id, libelleAvant);

        }

    } // ________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(libellé existant) :</p>
     * <ul>
     * <li>ne lève pas d'exception ;</li>
     * <li>ne modifie pas la ligne ciblée ;</li>
     * <li>retourne l'état persistant inchangé de la ligne ciblée ;</li>
     * <li>conserve inchangée la ligne portant déjà le libellé demandé.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_LIBELLE_EXISTANT)
    @Test
    public void testUpdateLibelleExistant() throws Exception {

        /* ARRANGE :
         * lit d'abord les deux lignes seedées impliquées :
         * - la ligne qui porte déjà le libellé cible ;
         * - la ligne que l'on tente de modifier.
         *
         * Le test vérifie ensuite qu'une tentative de collision
         * ne modifie aucun des deux états persistants.
         */
        final Long idVETEMENT = this.jdbcTemplate.queryForObject(
                SELECT_PARAM_ID_FROM_TYPES_PRODUIT,
                Long.class,
                VETEMENT);

        final Long idBAZAR = this.jdbcTemplate.queryForObject(
                SELECT_PARAM_ID_FROM_TYPES_PRODUIT,
                Long.class,
                BAZAR);

        assertThat(idVETEMENT).isNotNull();
        assertThat(idBAZAR).isNotNull();

        final String libelleAvantVETEMENT = lireLibelleTypeProduitEnBase(idVETEMENT);
        final String libelleAvantBAZAR = lireLibelleTypeProduitEnBase(idBAZAR);

        assertThat(libelleAvantVETEMENT).isEqualTo(VETEMENT);
        assertThat(libelleAvantBAZAR).isEqualTo(BAZAR);

        final TypeProduit aModifier = new TypeProduit(idBAZAR, VETEMENT);

        /* ACT :
         * sollicite la mise à jour
         * en tentant d'attribuer à BAZAR
         * un libellé déjà porté par VETEMENT.
         */
        final TypeProduit retour = this.service.update(aModifier);

        /* ASSERT :
         * vérifie d'abord qu'aucune exception n'est levée
         * et que le service retourne l'état inchangé
         * de la ligne ciblée.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getIdTypeProduit()).isEqualTo(idBAZAR);
        assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);

        /* Vérifie ensuite physiquement en base
         * que la ligne ciblée n'a pas été modifiée.
         */
        final String libelleApresBAZAR = lireLibelleTypeProduitEnBase(idBAZAR);
        assertThat(libelleApresBAZAR).isEqualTo(libelleAvantBAZAR);

        /* Vérifie aussi que la ligne portant déjà
         * le libellé demandé reste inchangée.
         */
        final String libelleApresVETEMENT = lireLibelleTypeProduitEnBase(idVETEMENT);
        assertThat(libelleApresVETEMENT).isEqualTo(libelleAvantVETEMENT);

        /* Vérifie enfin la cohérence
         * des relectures via le service.
         */
        final TypeProduit reluBAZAR = this.service.findById(idBAZAR);
        assertThat(reluBAZAR).isNotNull();
        assertThat(reluBAZAR.getIdTypeProduit()).isEqualTo(idBAZAR);
        assertThat(reluBAZAR.getTypeProduit()).isEqualTo(BAZAR);

        final TypeProduit reluVETEMENT = this.service.findById(idVETEMENT);
        assertThat(reluVETEMENT).isNotNull();
        assertThat(reluVETEMENT.getIdTypeProduit()).isEqualTo(idVETEMENT);
        assertThat(reluVETEMENT.getTypeProduit()).isEqualTo(VETEMENT);

    } // ________________________________________________________________
    
    
    
    // ============================= delete ===============================
    
    
    
    /**
     * <div>
     * <p>garantit que delete(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#MESSAGE_DELETE_KO_PARAM_NULL}.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_NULL)
    @Test
    public void testDeleteNull() {
    	
        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel avec un objet métier null
         * jette une ExceptionAppliParamNull
         * avec un message MESSAGE_DELETE_KO_PARAM_NULL
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.delete(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_DELETE_KO_PARAM_NULL);
        
    } // _________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNonPersistent} ;</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#MESSAGE_DELETE_KO_ID_NULL}.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(ID null) - jette ExceptionAppliParamNonPersistent (contrat du port)")
    @Test
    public void testDeleteIdNull() {
    	
        /* ARRANGE :
         * prépare un objet métier non persistant
         * dont l'identifiant est null,
         * afin de vérifier le contrôle de persistance
         * imposé par le contrat du port.
         */
        final TypeProduit nonPersistant = new TypeProduit(VETEMENT);
        
        /* ACT - ASSERT :
         * vérifie que l'appel avec un ID null
         * jette une ExceptionAppliParamNonPersistent
         * avec un message MESSAGE_DELETE_KO_ID_NULL
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.delete(nonPersistant))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_DELETE_KO_ID_NULL);
        
    } // _________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(OK) :</p>
     * <ul>
     * <li>supprime réellement une ligne créée pour le test ;</li>
     * <li>rend cet identifiant introuvable en base ;</li>
     * <li>rend cet identifiant introuvable via le service ;</li>
     * <li>diminue le compteur total d'une unité.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_OK)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testDeleteOK() throws Exception {
    	
        /* ARRANGE :
         * crée d'abord physiquement une ligne dédiée au test,
         * afin de vérifier ensuite sa suppression réelle
         * en passant à delete(...) un objet métier persistant.
         */
        final long countAvantCreation = this.service.count();

        final TypeProduit cree = this.service.creer(new TypeProduit(TEMP_A_SUPPRIMER));

        assertThat(cree).isNotNull();
        assertThat(cree.getIdTypeProduit()).isNotNull().isPositive();
        assertThat(cree.getTypeProduit()).isEqualTo(TEMP_A_SUPPRIMER);

        final Long id = cree.getIdTypeProduit();

        final Long countLigneAvantDelete = compterTypeProduitEnBase(id);
        assertThat(countLigneAvantDelete).isNotNull().isEqualTo(1L);

        final String libelleAvantDelete = lireLibelleTypeProduitEnBase(id);
        assertThat(libelleAvantDelete).isEqualTo(TEMP_A_SUPPRIMER);

        final long countAvantDelete = this.service.count();
        assertThat(countAvantDelete).isEqualTo(countAvantCreation + 1L);

        final TypeProduit aSupprimer = new TypeProduit(id, TEMP_A_SUPPRIMER);

        /* ACT :
         * sollicite la suppression
         * de la ligne nouvellement créée,
         * en fournissant l'objet métier persistant correspondant.
         */
        this.service.delete(aSupprimer);

        /* ASSERT :
         * vérifie d'abord physiquement en base
         * que la ligne n'existe plus.
         */
        final Long countLigneApresDelete = compterTypeProduitEnBase(id);
        assertThat(countLigneApresDelete).isNotNull().isZero();

        /* Vérifie ensuite que la relecture via le service
         * ne retrouve plus cet identifiant.
         */
        final TypeProduit relu = this.service.findById(id);
        assertThat(relu).isNull();

        /* Vérifie enfin que le compteur total
         * est revenu à son niveau initial.
         */
        final long countApresDelete = this.service.count();
        assertThat(countApresDelete).isEqualTo(countAvantCreation);
        
    } // _________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(ID inexistant) :</p>
     * <ul>
     * <li>ne lève pas d'exception ;</li>
     * <li>ne modifie pas l'état physique de la base ;</li>
     * <li>laisse le compteur total inchangé.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_ID_INEXISTANT)
    @Test
    public void testDeleteIdInexistant() throws Exception {
    	
        /* ARRANGE :
         * vérifie d'abord par SQL direct
         * qu'aucune ligne ne porte cet identifiant,
         * puis mémorise l'état global du stockage.
         */
        final Long countLigneAvant = compterTypeProduitEnBase(ID_INEXISTANTE);
        assertThat(countLigneAvant).isNotNull().isZero();

        final long countAvant = this.service.count();

        final TypeProduit inexistant = new TypeProduit(ID_INEXISTANTE, TEMP_A_SUPPRIMER);

        /* ACT :
         * sollicite la suppression
         * avec un objet métier portant un identifiant
         * absent du stockage réel.
         *
         * Le scénario attendu est l'absence d'exception.
         */
        this.service.delete(inexistant);

        /* ASSERT :
         * vérifie que l'état physique reste inchangé.
         */
        final Long countLigneApres = compterTypeProduitEnBase(ID_INEXISTANTE);
        assertThat(countLigneApres).isNotNull().isZero();

        /* Vérifie enfin que le compteur global
         * n'a pas été modifié.
         */
        final long countApres = this.service.count();
        assertThat(countApres).isEqualTo(countAvant);
        
    } // _________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(double suppression) :</p>
     * <ul>
     * <li>ne lève pas d'exception lors du second appel ;</li>
     * <li>la première suppression efface bien la ligne créée ;</li>
     * <li>la seconde suppression ne recrée rien ;</li>
     * <li>le compteur total reste stable après le second appel.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_DOUBLE)
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testDeleteDoubleSuppression() throws Exception {
    	
        /* ARRANGE :
         * crée d'abord physiquement une ligne dédiée au test,
         * afin de vérifier ensuite deux suppressions successives
         * sur le même objet métier persistant.
         */
        final long countAvantCreation = this.service.count();

        final TypeProduit cree = this.service.creer(new TypeProduit(TEMP_A_SUPPRIMER));

        assertThat(cree).isNotNull();
        assertThat(cree.getIdTypeProduit()).isNotNull().isPositive();

        final Long id = cree.getIdTypeProduit();

        final Long countLigneAvant = compterTypeProduitEnBase(id);
        assertThat(countLigneAvant).isNotNull().isEqualTo(1L);

        final TypeProduit aSupprimer = new TypeProduit(id, TEMP_A_SUPPRIMER);

        /* ACT :
         * sollicite une première suppression
         * qui doit effacer physiquement la ligne.
         */
        this.service.delete(aSupprimer);

        /* ASSERT :
         * vérifie que la première suppression
         * a bien supprimé la ligne.
         */
        final Long countLigneApresPremierDelete = compterTypeProduitEnBase(id);
        assertThat(countLigneApresPremierDelete).isNotNull().isZero();

        final long countApresPremierDelete = this.service.count();
        assertThat(countApresPremierDelete).isEqualTo(countAvantCreation);

        /* ACT :
         * sollicite ensuite une seconde suppression
         * sur le même objet déjà supprimé.
         *
         * Le scénario attendu est l'absence d'exception.
         */
        this.service.delete(aSupprimer);

        /* ASSERT :
         * vérifie que la seconde suppression
         * ne modifie pas l'état physique déjà vide.
         */
        final Long countLigneApresSecondDelete = compterTypeProduitEnBase(id);
        assertThat(countLigneApresSecondDelete).isNotNull().isZero();

        /* Vérifie enfin que le compteur global
         * reste stable après ce second appel.
         */
        final long countApresSecondDelete = this.service.count();
        assertThat(countApresSecondDelete).isEqualTo(countAvantCreation);

        final TypeProduit relu = this.service.findById(id);
        assertThat(relu).isNull();
        
    } // _________________________________________________________________
    
    
    
    // ============================== count ===============================
    
    
    
    /**
     * <div>
     * <p>garantit que count() sur la base seedée :</p>
     * <ul>
     * <li>retourne un nombre strictement positif ;</li>
     * <li>retourne le même total que la lecture SQL directe ;</li>
     * <li>retourne le même total que rechercherTous().</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName(DN_COUNT_OK)
    @Test
    public void testCountOK() throws Exception {
    	
        /* ARRANGE :
         * lit d'abord l'état physique réel de la base
         * via JdbcTemplate,
         * puis prépare une relecture métier complète
         * via rechercherTous().
         *
         * Le test vérifie ensuite que count()
         * reste cohérent avec ces deux références.
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        final List<TypeProduit> liste = this.service.rechercherTous();

        assertThat(countEnBase).isNotNull().isPositive();
        assertThat(liste).isNotNull().isNotEmpty();

        /* ACT :
         * sollicite le comptage métier
         * dans le scénario nominal de la base seedée.
         */
        final long count = this.service.count();

        /* ASSERT :
         * vérifie d'abord que count()
         * retourne un total positif.
         */
        assertThat(count).isPositive();

        /* Vérifie ensuite la cohérence
         * avec la lecture physique de la base.
         */
        assertThat(count).isEqualTo(countEnBase.longValue());

        /* Vérifie enfin la cohérence
         * avec la recherche complète métier.
         */
        assertThat(count).isEqualTo(liste.size());
        
    } // ________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que count() sur une base vidée :</p>
     * <ul>
     * <li>retourne zéro ;</li>
     * <li>retourne le même total que la lecture SQL directe ;</li>
     * <li>retourne le même total que rechercherTous().</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName("count(base vide) - retourne zéro et reste cohérent avec SQL et rechercherTous()")
    @Test
    @Sql(
        scripts = TypeProduitGatewayJPAServiceIntegrationTest.CLASSPATH_TRUNCATE_SQL,
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    public void testCountBaseVide() throws Exception {
    	
        /* ARRANGE :
         * prépare pour ce test une base réellement vide
         * en ne rejouant que le script de vidage,
         * puis lit cet état physique directement en base.
         */
        final Long countEnBase = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        final List<TypeProduit> liste = this.service.rechercherTous();

        assertThat(countEnBase).isNotNull().isZero();
        assertThat(liste).isNotNull().isEmpty();

        /* ACT :
         * sollicite le comptage métier
         * sur une base ne contenant aucune ligne.
         */
        final long count = this.service.count();

        /* ASSERT :
         * vérifie d'abord que count()
         * retourne bien zéro.
         */
        assertThat(count).isZero();

        /* Vérifie ensuite la cohérence
         * avec la lecture physique de la base.
         */
        assertThat(count).isEqualTo(countEnBase.longValue());

        /* Vérifie enfin la cohérence
         * avec la recherche complète métier.
         */
        assertThat(count).isEqualTo(liste.size());
        
    } // ________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que count() après création puis suppression :</p>
     * <ul>
     * <li>augmente d'une unité après creer(...)</li>
     * <li>revient à sa valeur initiale après delete(...)</li>
     * <li>reste cohérent avec la lecture SQL directe
     * à chaque étape.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName("count(après création puis suppression) - suit exactement l'état physique de la base")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCountApresCreationPuisSuppression() throws Exception {
    	
        /* ARRANGE :
         * lit d'abord l'état physique initial de la base,
         * puis crée une ligne dédiée au test,
         * afin de vérifier que count()
         * suit exactement les variations du stockage réel.
         */
        final Long countEnBaseAvant = this.jdbcTemplate.queryForObject(
                SELECT_COUNT_FROM_TYPES_PRODUIT,
                Long.class);

        assertThat(countEnBaseAvant).isNotNull().isPositive();

        final long countAvant = this.service.count();
        assertThat(countAvant).isEqualTo(countEnBaseAvant.longValue());

        final TypeProduit cree = this.service.creer(new TypeProduit(TEMP_A_SUPPRIMER));

        assertThat(cree).isNotNull();
        assertThat(cree.getIdTypeProduit()).isNotNull().isPositive();
        assertThat(cree.getTypeProduit()).isEqualTo(TEMP_A_SUPPRIMER);

        final Long id = cree.getIdTypeProduit();

        try {
        	
            /* ACT :
             * sollicite un premier comptage
             * après création effective d'une ligne.
             */
            final long countApresCreation = this.service.count();

            /* ASSERT :
             * vérifie d'abord la cohérence
             * avec la lecture SQL directe après création.
             */
            final Long countEnBaseApresCreation = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_TYPES_PRODUIT,
                    Long.class);

            assertThat(countEnBaseApresCreation).isNotNull();
            assertThat(countApresCreation).isEqualTo(countAvant + 1L);
            assertThat(countApresCreation).isEqualTo(countEnBaseApresCreation.longValue());

            final TypeProduit aSupprimer = new TypeProduit(id, TEMP_A_SUPPRIMER);

            /* ACT :
             * supprime ensuite la ligne créée,
             * puis sollicite un second comptage.
             */
            this.service.delete(aSupprimer);
            final long countApresSuppression = this.service.count();

            /* ASSERT :
             * vérifie enfin que count()
             * revient exactement à sa valeur initiale
             * et reste cohérent avec SQL après suppression.
             */
            final Long countEnBaseApresSuppression = this.jdbcTemplate.queryForObject(
                    SELECT_COUNT_FROM_TYPES_PRODUIT,
                    Long.class);

            assertThat(countEnBaseApresSuppression).isNotNull();
            assertThat(countApresSuppression).isEqualTo(countAvant);
            assertThat(countApresSuppression).isEqualTo(countEnBaseApresSuppression.longValue());

            final TypeProduit relu = this.service.findById(id);
            assertThat(relu).isNull();
        	
        } finally {
        	
            /* Nettoyage défensif :
             * si la suppression métier n'a pas eu lieu
             * avant une éventuelle assertion en échec,
             * supprime explicitement la ligne restante.
             */
            final Long countLigne = compterTypeProduitEnBase(id);
            if ((countLigne != null) && (countLigne.longValue() == 1L)) {
                supprimerTypeProduitEnBase(id);
            }
        	
        }
        
    } // ________________________________________________________________

    
    
}
