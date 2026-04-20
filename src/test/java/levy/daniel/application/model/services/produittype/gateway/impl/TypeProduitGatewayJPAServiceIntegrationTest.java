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
     * "SELECT COUNT(*) FROM TYPES_PRODUIT"
     */
    public static final String SELECT_COUNT_FROM_TYPES_PRODUIT 
    	= "SELECT COUNT(*) FROM TYPES_PRODUIT";
    
    /**
     * "SELECT TYPE_PRODUIT FROM TYPES_PRODUIT"
     */
    public static final String SELECT_TYPEPRODUIT_FROM_TYPES_PRODUIT 
    	= "SELECT TYPE_PRODUIT FROM TYPES_PRODUIT";

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
                "SELECT COUNT(*) FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) = UPPER(?)",
                Long.class,
                VETEMENT);

        final Long idSeedAvant = this.jdbcTemplate.queryForObject(
                "SELECT ID_TYPE_PRODUIT FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) = UPPER(?)",
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
                "SELECT COUNT(*) FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) = UPPER(?)",
                Long.class,
                VETEMENT);

        final Long idSeedApres = this.jdbcTemplate.queryForObject(
                "SELECT ID_TYPE_PRODUIT FROM TYPES_PRODUIT WHERE UPPER(TYPE_PRODUIT) = UPPER(?)",
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
     * <p>Vérifie que `findByObjetMetier(null)` respecte le contrat du port :
     * jette une {@link ExceptionAppliParamNull}.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_NULL)
    @Test
    public void testFindByObjetMetierNull() {
    	
        assertThatThrownBy(() -> this.service.findByObjetMetier(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL);
        
    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>Vérifie que `findByObjetMetier(libellé null)` 
     * respecte le contrat du port :
     * jette une {@link ExceptionAppliLibelleBlank}.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testFindByObjetMetierLibelleNull() {
    	
        final TypeProduit libelleNull = new TypeProduit(null);
        assertThatThrownBy(() -> this.service.findByObjetMetier(libelleNull))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `findByObjetMetier(blank)` 
     * respecte le contrat du port :
     * jette une {@link ExceptionAppliLibelleBlank}.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_BLANK)
    @Test
    public void testFindByObjetMetierBlank() {
    	
        final TypeProduit blank = new TypeProduit(BLANK);
        assertThatThrownBy(() -> this.service.findByObjetMetier(blank))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `findByObjetMetier(non trouvé)` 
     * retourne {@code null}.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_NON_TROUVE)
    @Test
    public void testFindByObjetMetierNonTrouve() throws Exception {
    	
        final TypeProduit inexistant = new TypeProduit("inexistant");
        final TypeProduit retour = this.service.findByObjetMetier(inexistant);
        assertThat(retour).isNull();
        
    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>Vérifie que `findByObjetMetier(trouvé)` 
     * retourne un objet métier cohérent.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_TROUVE)
    @Test
    public void testFindByObjetMetierTrouve() throws Exception {
    	
        final TypeProduit metier = new TypeProduit(VETEMENT);
        final TypeProduit retour = this.service.findByObjetMetier(metier);
        assertThat(retour).isNotNull();
        assertThat(retour.getIdTypeProduit()).isNotNull();
        assertThat(retour.getTypeProduit()).isEqualTo(VETEMENT);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>
     * Test "béton" :
     * </p>
     * <ul>
     * <li>l'ID du paramètre ne doit pas influencer 
     * la recherche (recherche par libellé)</li>
     * <li>la recherche est insensible à la casse : 
     * upper == lower (retour non null)</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_BETON)
    @Test
    public void testFindByObjetMetierBeton() throws Exception {
    	
        final TypeProduit seed = this.service.findByLibelle(VETEMENT);
        assertThat(seed).isNotNull();
        assertThat(seed.getIdTypeProduit()).isNotNull();
        final TypeProduit avecIdFaux = new TypeProduit(ID_INEXISTANTE, VETEMENT);
        final TypeProduit retour = this.service.findByObjetMetier(avecIdFaux);
        assertThat(retour).isNotNull();
        assertThat(retour.getIdTypeProduit()).isEqualTo(seed.getIdTypeProduit());
        assertThat(retour.getTypeProduit()).isEqualTo(VETEMENT);
        final String upper = VETEMENT.toUpperCase(Locale.getDefault());
        final TypeProduit casse = new TypeProduit(upper);
        final TypeProduit retourCasse = this.service.findByObjetMetier(casse);
        assertThat(retourCasse).isNotNull();
        
    } // __________________________________________________________________
    
    

    // ========================== findByLibelle ===========================

    
    
	/**
     * <div>
     * <p>Vérifie que `findByLibelle(blank)` respecte le contrat du port :
     * jette une {@link ExceptionAppliLibelleBlank}.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_BLANK)
    @Test
    public void testFindByLibelleBlank() {
    	
        assertThatThrownBy(() -> this.service.findByLibelle(BLANK))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `findByLibelle(non trouvé)` retourne {@code null}.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_NON_TROUVE)
    @Test
    public void testFindByLibelleNonTrouve() throws Exception {
    	
        final TypeProduit retour = this.service.findByLibelle("inexistant");
        assertThat(retour).isNull();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `findByLibelle(trouvé)` 
     * retourne un objet métier cohérent.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_TROUVE)
    @Test
    public void testFindByLibelleTrouve() throws Exception {
    	
        final TypeProduit retour = this.service.findByLibelle(VETEMENT);
        assertThat(retour).isNotNull();
        assertThat(retour.getIdTypeProduit()).isNotNull();
        assertThat(retour.getTypeProduit()).isEqualTo(VETEMENT);
        
    } // __________________________________________________________________
    
    

    // ======================== findByLibelleRapide =======================
    
    

    /**
     * <div>
     * <p>Vérifie que `findByLibelleRapide(null)` 
     * respecte le contrat du port :
     * jette une {@link ExceptionAppliParamNull}.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_NULL)
    @Test
    public void testFindByLibelleRapideNull() {
    	
        assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `findByLibelleRapide(blank)` 
     * retourne tous les enregistrements.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_BLANK)
    @Test
    public void testFindByLibelleRapideBlank() throws Exception {
    	
        final List<TypeProduit> tous = this.service.rechercherTous();
        assertThat(tous).isNotNull();
        final List<TypeProduit> retour = this.service.findByLibelleRapide(BLANK);
        assertThat(retour).isNotNull();
        assertThat(retour)
            .extracting(TypeProduit::getTypeProduit)
            .containsExactlyElementsOf(
                tous.stream().map(TypeProduit::getTypeProduit).toList());
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `findByLibelleRapide(OK)` 
     * retourne des correspondances.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_OK)
    @Test
    public void testFindByLibelleRapideOK() throws Exception {
    	
        final List<TypeProduit> retour = this.service.findByLibelleRapide(RECHERCHE_ME);
        assertThat(retour).isNotNull().isNotEmpty();
        assertThat(retour)
            .extracting(TypeProduit::getTypeProduit)
            .allMatch(s -> s != null && s.contains(RECHERCHE_ME));
        
    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>Vérifie que `findByLibelleRapide(case-insensitive)` 
     * est insensible à la casse.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_CASE_INSENSITIVE)
    @Test
    public void testFindByLibelleRapideCaseInsensitive() throws Exception {
        final List<TypeProduit> retourMin = this.service.findByLibelleRapide(RECHERCHE_VE);
        final List<TypeProduit> retourMaj = this.service.findByLibelleRapide(RECHERCHE_VE_MAJ);
        assertThat(retourMin).isNotNull();
        assertThat(retourMaj).isNotNull();
        assertThat(retourMin)
            .extracting(TypeProduit::getTypeProduit)
            .containsExactlyInAnyOrderElementsOf(
                retourMaj.stream().map(TypeProduit::getTypeProduit).toList());
        
    } // ________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `findByLibelleRapide(dédoublonnage)` 
     * ne retourne pas de doublons.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_DEDOUBLONNAGE)
    @Test
    public void testFindByLibelleRapideDedoublonnage() throws Exception {
    	
        final List<TypeProduit> retour = this.service.findByLibelleRapide(RECHERCHE_ME);
        assertThat(retour)
            .extracting(TypeProduit::getTypeProduit)
            .doesNotHaveDuplicates();
        
    } // ________________________________________________________________


    
    /**
     * <div>
     * <p>
     * Vérifie que `findByLibelleRapide(aucun résultat)`
     * retourne une liste vide (pas {@code null}).
     * </p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_AUCUN)
    @Test
    public void testFindByLibelleRapideAucunResultat() throws Exception {

        /* Recherche avec un motif absent. */
        final List<TypeProduit> retour =
                this.service.findByLibelleRapide(RECHERCHE_AUCUN);

        /* Vérifie liste non nulle et vide. */
        assertThat(retour).isNotNull().isEmpty();
        
    } // ________________________________________________________________
    

    
    // ============================ findById ==============================

    
    
    /**
     * <div>
     * <p>Vérifie que `findById(null)` respecte le contrat du port :
     * jette une {@link ExceptionAppliParamNull}.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_NULL)
    @Test
    public void testFindByIdNull() {
    	
        assertThatThrownBy(() -> this.service.findById(null)
            ).isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYID_KO_PARAM_NULL);
        
    } // ________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `findById(non trouvé)` retourne {@code null}.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_NON_TROUVE)
    @Test
    public void testFindByIdNonTrouve() throws Exception {
    	
        final TypeProduit retour = this.service.findById(ID_INEXISTANTE);
        assertThat(retour).isNull();
        
    } // ________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `findById(trouvé)` 
     * retourne un objet métier cohérent.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_TROUVE)
    @Test
    public void testFindByIdTrouve() throws Exception {
    	
        final TypeProduit seed = this.service.findByLibelle(VETEMENT);
        assertThat(seed).isNotNull();
        assertThat(seed.getIdTypeProduit()).isNotNull();
        final TypeProduit relu = this.service.findById(seed.getIdTypeProduit());
        assertThat(relu).isNotNull();
        assertThat(relu.getIdTypeProduit()).isEqualTo(seed.getIdTypeProduit());
        assertThat(relu.getTypeProduit()).isEqualTo(VETEMENT);
        
    } // ________________________________________________________________
    
    

    // ============================= update ===============================
    
    

    /**
     * <div>
     * <p>Vérifie que `update(null)` respecte le contrat du port :
     * jette une {@link ExceptionAppliParamNull}.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_NULL)
    @Test
    public void testUpdateNull() {
    	
        assertThatThrownBy(() -> this.service.update(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARAM_NULL);
        
    } // _________________________________________________________________

    
    
    /**
     * <div>
     * <p>Vérifie que `update(blank)` respecte le contrat du port :
     * jette une {@link ExceptionAppliLibelleBlank}.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_BLANK)
    @Test
    public void testUpdateBlank() {
    	
        final TypeProduit blank = new TypeProduit(Long.valueOf(1L), BLANK);
        assertThatThrownBy(() -> this.service.update(blank))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_BLANK);
        
    } // _________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `update(ID null)` respecte le contrat du port :
     * jette une {@link ExceptionAppliParamNonPersistent}.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_ID_NULL)
    @Test
    public void testUpdateIdNull() {
    	
        final TypeProduit nonPersistant = new TypeProduit(VETEMENT);
        assertThatThrownBy(() -> this.service.update(nonPersistant))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_UPDATE_KO_NON_PERSISTENT + VETEMENT);
        
    } // _________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `update(entity inexistante)` retourne {@code null}.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_INEXISTANTE)
    @Test
    public void testUpdateEntityInexistante() throws Exception {
    	
        final TypeProduit inexistante = new TypeProduit(ID_INEXISTANTE, VETEMENT);
        final TypeProduit retour = this.service.update(inexistante);
        assertThat(retour).isNull();
        
    } // _________________________________________________________________
    
    

    /**
     * <div>
     * <p>
     * Vérifie que `update(sans modification)` 
     * retourne un objet métier équivalent
     * (contrat de l'implémentation) et ne modifie pas le stockage.
     * </p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_SANS_MODIF)
    @Test
    public void testUpdateSansModification() throws Exception {
    	
        final TypeProduit seed = this.service.findByLibelle(VETEMENT);
        assertThat(seed).isNotNull();
        assertThat(seed.getIdTypeProduit()).isNotNull();
        final Long id = seed.getIdTypeProduit();
        final TypeProduit sansModif = new TypeProduit(id, VETEMENT);
        final TypeProduit retour = this.service.update(sansModif);
        assertThat(retour).isNotNull();
        assertThat(retour).isNotSameAs(sansModif);
        assertThat(retour.getIdTypeProduit()).isEqualTo(id);
        assertThat(retour.getTypeProduit()).isEqualTo(VETEMENT);
        final TypeProduit relu = this.service.findById(id);
        assertThat(relu).isNotNull();
        assertThat(relu.getTypeProduit()).isEqualTo(VETEMENT);
        
    } // _________________________________________________________________

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>
     * Vérifier que <code>update(modification)</code> modifie réellement la base,
     * puis que les relectures (JDBC et service) reflètent cette modification.
     * </p>
     *
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <ul>
     * <li><code>update</code> retourne un objet non {@code null} portant le même ID.</li>
     * <li>La colonne <code>TYPE_PRODUIT</code> est réellement mise à jour en base
     * (preuve par lecture SQL directe via {@link #lireLibelleTypeProduitEnBase(Long)}).</li>
     * <li><code>findById</code> est cohérent avec l'état physique en base.</li>
     * </ul>
     *
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <ul>
     * <li>Test hors transaction de test
     * (<code>@Transactional(NOT_SUPPORTED)</code>) pour verrouiller le diagnostic transactionnel
     * et éviter l'effet “cache ORM”.</li>
     * <li>Restauration physique du libellé en <code>finally</code> (isolation),
     * car le test n'est pas rollbacké par la transaction de test.</li>
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

        final TypeProduit seed = this.service.findByLibelle(TOURISME);
        assertThat(seed).isNotNull();
        assertThat(seed.getIdTypeProduit()).isNotNull();

        final Long id = seed.getIdTypeProduit();

        /* PREUVE BD : lecture SQL directe AVANT update (point de référence). */
        final String libelleAvant = lireLibelleTypeProduitEnBase(id);
        assertThat(libelleAvant).isEqualTo(TOURISME);

        final String nouveauLibelle = TOURISME + SUFFIX_MODIF;

        /* IMPORTANT : test hors transaction de test -> on doit restaurer physiquement en base. */
        try {

            final TypeProduit aModifier = new TypeProduit(id, nouveauLibelle);
            final TypeProduit retour = this.service.update(aModifier);

            assertThat(retour).isNotNull();
            assertThat(retour.getIdTypeProduit()).isEqualTo(id);

            /* PREUVE BD INATTAQUABLE :
             * lecture SQL directe APRES update (bypass Hibernate). */
            final String libelleEnBase = lireLibelleTypeProduitEnBase(id);
            assertThat(libelleEnBase).isEqualTo(nouveauLibelle);

            /* Cohérence : le service doit refléter la base (pas l'inverse). */
            final TypeProduit relu = this.service.findById(id);
            assertThat(relu).isNotNull();
            assertThat(relu.getTypeProduit()).isEqualTo(nouveauLibelle);

            /* Variante : mise à jour en majuscules (diagnostic CASE + preuve base). */
            final String upper = nouveauLibelle.toUpperCase(Locale.getDefault());
            final TypeProduit aModifierCS = new TypeProduit(id, upper);

            final TypeProduit retourCS = this.service.update(aModifierCS);

            assertThat(retourCS).isNotNull();
            assertThat(retourCS.getIdTypeProduit()).isEqualTo(id);

            final String libelleEnBaseCS = lireLibelleTypeProduitEnBase(id);
            assertThat(libelleEnBaseCS).isEqualTo(upper);

            final TypeProduit reluApresCS = this.service.findById(id);
            assertThat(reluApresCS).isNotNull();
            assertThat(reluApresCS.getTypeProduit()).isEqualTo(upper);

        } finally {

            /* Isolation : restauration physique en base.
             * On ne dépend pas uniquement de truncate-test.sql. */
            restaurerTypeProduitEnBase(id, libelleAvant);

        }

    } // ________________________________________________________________
    

    
    /**
     * <div>
     * <p>
     * Vérifie le comportement lorsque l'on modifie
     * un type vers un libellé déjà existant.
     * </p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_LIBELLE_EXISTANT)
    @Test
    public void testUpdateLibelleExistant() throws Exception {

        final TypeProduit t1 = this.service.findByLibelle(VETEMENT);
        final TypeProduit t2 = this.service.findByLibelle(BAZAR);

        assertThat(t1).isNotNull();
        assertThat(t2).isNotNull();

        final Long id2 = t2.getIdTypeProduit();
        assertThat(id2).isNotNull();

        /* Tentative de collision de libellé. */
        final TypeProduit aModifier =
                new TypeProduit(id2, VETEMENT);

        /* Comportement réel : pas d'exception et pas de modification. */
        final TypeProduit retour = this.service.update(aModifier);

        assertThat(retour).isNotNull();
        assertThat(retour.getIdTypeProduit()).isEqualTo(id2);
        assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);

        final TypeProduit relu = this.service.findById(id2);
        assertThat(relu).isNotNull();
        assertThat(relu.getIdTypeProduit()).isEqualTo(id2);
        assertThat(relu.getTypeProduit()).isEqualTo(BAZAR);

    } // ________________________________________________________________
    
    

    // ============================= delete ===============================
    
    

    /**
     * <div>
     * <p>Vérifie que `delete(null)` respecte le contrat du port :
     * jette une {@link ExceptionAppliParamNull}.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_NULL)
    @Test
    public void testDeleteNull() {
    	
        assertThatThrownBy(() -> this.service.delete(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_DELETE_KO_PARAM_NULL);
        
    } // ________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `delete(ID null)` respecte le contrat du port :
     * jette une {@link ExceptionAppliParamNonPersistent}.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(ID null) - jette ExceptionAppliParamNonPersistent (contrat du port)")
    @Test
    public void testDeleteIdNull() {
    	
        final TypeProduit idNull = new TypeProduit(null, VETEMENT);
        assertThatThrownBy(() -> this.service.delete(idNull))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_DELETE_KO_ID_NULL);
        
    } // ________________________________________________________________

    
    
    /**
     * <div>
     * <p>Vérifie que `delete(ID inexistant)` ne lève pas d'exception.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_ID_INEXISTANT)
    @Test
    public void testDeleteIdInexistant() throws Exception {
    	
        final TypeProduit inexistant = new TypeProduit(ID_INEXISTANTE, VETEMENT);
        this.service.delete(inexistant); // Pas d'exception attendue
        
    } // ________________________________________________________________

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>
     * Vérifier que <code>delete(OK)</code> supprime réellement une ligne en base,
     * et que l'objet devient introuvable.
     * </p>
     *
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <ul>
     * <li>Après création, la ligne existe physiquement en base
     * (preuve via {@link #compterTypeProduitEnBase(Long)}).</li>
     * <li>Après delete, la ligne n'existe plus physiquement en base (preuve JDBC).</li>
     * <li><code>findById</code> retourne {@code null}.</li>
     * </ul>
     *
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <ul>
     * <li>Test hors transaction de test
     * (<code>@Transactional(NOT_SUPPORTED)</code>) pour éviter tout effet de cache ORM.</li>
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

        final long countAvant = this.service.count();

        final TypeProduit cree = this.service.creer(new TypeProduit(TEMP_A_SUPPRIMER));
        assertThat(cree).isNotNull();
        assertThat(cree.getIdTypeProduit()).isNotNull();

        final Long id = cree.getIdTypeProduit();

        /* PREUVE BD : la ligne existe physiquement après création. */
        assertThat(compterTypeProduitEnBase(id)).isEqualTo(1L);

        final long countApresCreation = this.service.count();
        assertThat(countApresCreation).isEqualTo(countAvant + 1L);

        this.service.delete(cree);

        /* PREUVE BD INATTAQUABLE : la ligne n'existe plus physiquement après delete. */
        assertThat(compterTypeProduitEnBase(id)).isZero();

        final long countApresDelete = this.service.count();
        assertThat(countApresDelete).isEqualTo(countAvant);

        /* Cohérence : le service ne doit plus retrouver l'objet. */
        final TypeProduit relu = this.service.findById(id);
        assertThat(relu).isNull();

    } // ________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>
     * Vérifie qu'une suppression répétée du même objet
     * ne provoque pas d'exception.
     * </p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_DOUBLE)
    @Test
    public void testDeleteDouble() throws Exception {

        /* Création d'un objet temporaire. */
        final TypeProduit cree =
                this.service.creer(new TypeProduit(TEMP_A_SUPPRIMER));

        assertThat(cree).isNotNull();

        /* Première suppression. */
        this.service.delete(cree);

        /* Seconde suppression : ne doit rien faire. */
        this.service.delete(cree);
        
    } // ________________________________________________________________
    
    

    // ============================== Count ===============================
    
    

    /**
     * <div>
     * <p>Vérifie que `count()` est cohérent avec `rechercherTous()`.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName(DN_COUNT_OK)
    @Test
    public void testCountOK() throws Exception {
    	
        final long count = this.service.count();
        final List<TypeProduit> liste = this.service.rechercherTous();
        assertThat(liste).isNotNull();
        assertThat(count).isEqualTo(liste.size());
        
    } // ________________________________________________________________

    
    
}
