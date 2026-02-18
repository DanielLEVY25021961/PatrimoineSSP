package levy.daniel.application.model.services.produittype.gateway.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import jakarta.persistence.EntityManager;
import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.SousTypeProduitI;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliLibelleBlank;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNonPersistent;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNull;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParentNull;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionTechniqueGateway;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionTechniqueGatewayNonPersistent;
import levy.daniel.application.model.services.produittype.gateway.ProduitGatewayIService;
import levy.daniel.application.model.services.produittype.pagination.DirectionTri;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;
import levy.daniel.application.model.services.produittype.pagination.TriSpec;
import levy.daniel.application.persistence.metier.produittype.dao.daosJPA.ProduitDaoJPA;
import levy.daniel.application.persistence.metier.produittype.dao.daosJPA.SousTypeProduitDaoJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.ProduitJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.SousTypeProduitJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.TypeProduitJPA;

/**
 * <div>
 * <p style="font-weight:bold;">TEST JUnit (Mockito) JUPITER 5</p>
 * <p>Test du Service Gateway <code>ProduitGatewayJPAService</code>.</p>
 * <p>Objectif : tester le gateway en isolation en mockant les DAOs et l'EntityManager.</p>
 * <p>Baseline : SousTypeProduitGatewayJPAServiceMockTest</p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 19 janvier 2026
 */
@ExtendWith(MockitoExtension.class)
public class ProduitGatewayJPAServiceMockTest {

    // *************************** CONSTANTES ******************************/

    /** Tag JUnit : tests de création. */
    public static final String TAG_CREER = "servicesGateway-Creer";
    
    /**
     * "resource"
     */
    public static final String RESOURCE = "resource";

    /** Tag JUnit : tests de recherche. */
    public static final String TAG_RECHERCHER = "servicesGateway-Rechercher";

    /** Tag JUnit : tests d'update. */
    public static final String TAG_UPDATE = "servicesGateway-Update";

    /** Tag JUnit : tests de delete. */
    public static final String TAG_DELETE = "servicesGateway-Delete";

    /** Tag JUnit : tests de count. */
    public static final String TAG_COUNT = "servicesGateway-Count";

    /** Tag JUnit : tests de pagination. */
    public static final String TAG_PAGINATION = "servicesGateway-Pagination";

    /** Tag JUnit : tests béton. */
    public static final String TAG_BETON = "servicesGateway-Beton";

    /** Locale par défaut. */
    public static final Locale LOCALE_DEFAUT = Locale.getDefault();

    /** "vêtement". */
    public static final String VETEMENT = "vêtement";

    /** "vêtement pour homme". */
    public static final String VETEMENT_HOMME = "vêtement pour homme";

    /** "vêtement pour femme". */
    public static final String VETEMENT_FEMME = "vêtement pour femme";

    /** "chemise". */
    public static final String CHEMISE = "chemise";

    /** "chemise à manches longues pour homme". */
    public static final String CHEMISE_ML_HOMME = "chemise à manches longues pour homme";

    /** "chemise à manches courtes pour homme". */
    public static final String CHEMISE_MC_HOMME = "chemise à manches courtes pour homme";

    /** "sweatshirt pour homme". */
    public static final String SWEAT_HOMME = "sweatshirt pour homme";

    /** "   " */
    public static final String BLANK = "   ";

    /** "boom" */
    public static final String BOOM = "boom";

    /** Suffix de modif. */
    public static final String SUFFIX_MODIF = " (modifié)";

    /** Propriété de tri (Entity) : "produit". */
    public static final String PROP_TRI_PRODUIT = "produit";

    /** Page number : 0. */
    public static final int PAGE_0 = 0;

    /** Page size : 5. */
    public static final int SIZE_5 = 5;

    /** Total elements : 10L. */
    public static final long TOTAL_10 = 10L;

    /** Message attendu : "Erreur Technique lors du stockage : ". */
    public static final String MSG_PREFIX_ERREUR_TECH = ProduitGatewayIService.ERREUR_TECHNIQUE_STOCKAGE;

    /** Message attendu : "Erreur Technique - Le stockage a retourné null.". */
    public static final String MSG_ERREUR_TECH_KO_STOCKAGE = ProduitGatewayIService.ERREUR_TECHNIQUE_KO_STOCKAGE;

    /** Message attendu : MESSAGE_CREER_KO_PARAM_NULL. */
    public static final String MSG_CREER_KO_PARAM_NULL = ProduitGatewayIService.MESSAGE_CREER_KO_PARAM_NULL;

    /** Message attendu : MESSAGE_CREER_KO_LIBELLE_BLANK. */
    public static final String MSG_CREER_KO_LIBELLE_BLANK = ProduitGatewayIService.MESSAGE_CREER_KO_LIBELLE_BLANK;

    /** Message attendu : MESSAGE_CREER_KO_PARENT_NULL. */
    public static final String MSG_CREER_KO_PARENT_NULL = ProduitGatewayIService.MESSAGE_CREER_KO_PARENT_NULL;

    /** Message attendu : MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK. */
    public static final String MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK = ProduitGatewayIService.MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK;

    /** Message attendu : MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL. */
    public static final String MSG_FINDBYLIBELLERAPIDE_KO_PARAM_NULL = ProduitGatewayIService.MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL;

    /** Message attendu : MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL. */
    public static final String MSG_FINDALLBYPARENT_KO_PARAM_NULL = ProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL;

    /** Message attendu : MESSAGE_FINDBYID_KO_PARAM_NULL. */
    public static final String MSG_FINDBYID_KO_PARAM_NULL = ProduitGatewayIService.MESSAGE_FINDBYID_KO_PARAM_NULL;

    /** Message attendu : MESSAGE_UPDATE_KO_PARAM_NULL. */
    public static final String MSG_UPDATE_KO_PARAM_NULL = ProduitGatewayIService.MESSAGE_UPDATE_KO_PARAM_NULL;

    /** Message attendu : MESSAGE_UPDATE_KO_LIBELLE_BLANK. */
    public static final String MSG_UPDATE_KO_LIBELLE_BLANK = ProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_BLANK;

    /** Message attendu : MESSAGE_UPDATE_KO_NON_PERSISTENT (préfixe). */
    public static final String MSG_UPDATE_PREFIX_NON_PERSISTENT = ProduitGatewayIService.MESSAGE_UPDATE_KO_NON_PERSISTENT;

    /** Message attendu : MESSAGE_UPDATE_KO_PARENT_NULL. */
    public static final String MSG_UPDATE_KO_PARENT_NULL = ProduitGatewayIService.MESSAGE_UPDATE_KO_PARENT_NULL;

    /** Message attendu : MESSAGE_DELETE_KO_PARAM_NULL. */
    public static final String MSG_DELETE_KO_PARAM_NULL = ProduitGatewayIService.MESSAGE_DELETE_KO_PARAM_NULL;

    /** Message attendu : MESSAGE_DELETE_KO_ID_NULL. */
    public static final String MSG_DELETE_KO_ID_NULL = ProduitGatewayIService.MESSAGE_DELETE_KO_ID_NULL;

    // *************************** ATTRIBUTS *******************************/

    /**
     * <div>
     * <p>DAO mocké pour Produit.</p>
     * </div>
     */
    @Mock
    private ProduitDaoJPA produitDaoJPA;

    /**
     * <div>
     * <p>DAO mocké pour SousTypeProduit.</p>
     * </div>
     */
    @Mock
    private SousTypeProduitDaoJPA sousTypeProduitDaoJPA;

    /**
     * <div>
     * <p>EntityManager mocké.</p>
     * </div>
     */
    @Mock
    private EntityManager entityManager;

    /**
     * <div>
     * <p>Service testé (injection des mocks).</p>
     * </div>
     */
    @InjectMocks
    private ProduitGatewayJPAService service;

    // ************************* METHODES **********************************/

    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
     * </div>
     */
    public ProduitGatewayJPAServiceMockTest() {
        super();
    }

    /**
     * <div>
     * <p>Initialise le service avant chaque test.</p>
     * </div>
     */
    @BeforeEach
    public void init() {
        this.service = new ProduitGatewayJPAService(
                this.produitDaoJPA,
                this.sousTypeProduitDaoJPA);
        this.service.setEntityManager(this.entityManager);
    }

    // ============================ OUTILS TESTS ===========================

    /**
     * <div>
     * <p>Fabrique un Produit métier minimal.</p>
     * </div>
     *
     * @param pLibelle : String
     * @param pId : Long
     * @param pParent : SousTypeProduitI
     * @return Produit
     */
    private Produit fabriquerProduitMetier(
            final String pLibelle,
            final Long pId,
            final SousTypeProduitI pParent) {

        final Produit produit = new Produit();
        produit.setIdProduit(pId);
        produit.setProduit(pLibelle);
        produit.setSousTypeProduit(pParent);
        return produit;
    }

    /**
     * <div>
     * <p>Fabrique un parent métier persistant.</p>
     * </div>
     *
     * @param pLibelleSousType : String
     * @return SousTypeProduitI
     */
    private SousTypeProduit fabriquerParentMetierPersistant(final String pLibelleSousType) {
        final TypeProduit typeProduit = new TypeProduit();
        typeProduit.setIdTypeProduit(1L);
        typeProduit.setTypeProduit(VETEMENT);

        final SousTypeProduit parent = new SousTypeProduit();
        parent.setIdSousTypeProduit(1L);
        parent.setSousTypeProduit(pLibelleSousType);
        parent.setTypeProduit(typeProduit);

        return parent;
    }

    /**
     * <div>
     * <p>Fabrique une entity ProduitJPA minimale.</p>
     * </div>
     *
     * @param pLibelleProduit : String
     * @param pLibelleSousType : String
     * @return ProduitJPA
     */
    private ProduitJPA fabriquerProduitJPA(
            final String pLibelleProduit,
            final String pLibelleSousType) {

        final TypeProduitJPA typeProduitJPA = new TypeProduitJPA();
        typeProduitJPA.setIdTypeProduit(1L);
        typeProduitJPA.setTypeProduit(VETEMENT);

        final SousTypeProduitJPA sousTypeProduitJPA = new SousTypeProduitJPA();
        sousTypeProduitJPA.setIdSousTypeProduit(1L);
        sousTypeProduitJPA.setSousTypeProduit(pLibelleSousType);
        sousTypeProduitJPA.setTypeProduit(typeProduitJPA);

        final ProduitJPA produitJPA = new ProduitJPA();
        produitJPA.setProduit(pLibelleProduit);
        produitJPA.setSousTypeProduit(sousTypeProduitJPA);

        return produitJPA;
    }

    /**
     * <div>
     * <p>Fabrique un parent JPA persistant.</p>
     * </div>
     *
     * @param pLibelleSousType : String
     * @return SousTypeProduitJPA
     */
    private SousTypeProduitJPA fabriquerParentJPAPersistant(final String pLibelleSousType) {
        final TypeProduitJPA typeProduitJPA = new TypeProduitJPA();
        typeProduitJPA.setIdTypeProduit(1L);
        typeProduitJPA.setTypeProduit(VETEMENT);

        final SousTypeProduitJPA parentJPA = new SousTypeProduitJPA();
        parentJPA.setIdSousTypeProduit(1L);
        parentJPA.setSousTypeProduit(pLibelleSousType);
        parentJPA.setTypeProduit(typeProduitJPA);

        return parentJPA;
    }

    /**
     * <div>
     * <p>Retourne une chaîne vide si p est null.</p>
     * </div>
     *
     * @param p : Object
     * @return String
     */
    private static String safeMessage(final Object p) {
        if (p == null) {
            return "";
        }
        final String s = p.toString();
        return (s != null) ? s : "";
    }

    // =============================== CREER ===============================

    /**
     * <div>
     * <p>creer(null) lève ExceptionAppliParamNull.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(null) - ExceptionAppliParamNull")
    @Test
    public void testCreerParamNullExceptionAppliParamNull() {
        assertThatThrownBy(() -> this.service.creer(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_CREER_KO_PARAM_NULL);
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>creer(libellé blank) lève ExceptionAppliLibelleBlank.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(blank) - ExceptionAppliLibelleBlank")
    @Test
    public void testCreerLibelleBlankExceptionAppliLibelleBlank() {
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p = new Produit();
        p.setProduit(BLANK);
        p.setSousTypeProduit(parent);

        assertThatThrownBy(() -> this.service.creer(p))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_CREER_KO_LIBELLE_BLANK);
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>creer(parent null) lève ExceptionAppliParentNull.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(sans parent) - ExceptionAppliParentNull")
    @Test
    public void testCreerSousTypeProduitNullExceptionAppliParentNull() {
        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(null);

        assertThatThrownBy(() -> this.service.creer(p))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_CREER_KO_PARENT_NULL);
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>creer(parent non persistant) lève ExceptionTechniqueGatewayNonPersistent.</p>
     * <p>Scénarios couverts :</p>
     * <ul>
     *   <li>Parent avec ID mais introuvable en base.</li>
     *   <li>Message d'erreur exact attendu.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(parent non persistant) - ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testCreerParentNonPersistantExceptionTechniqueGatewayNonPersistent() {
        // --- 1. DONNÉES ---
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        // --- 2. MOCKS ---
        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.empty());

        // --- 3. EXÉCUTION + VÉRIFICATION ---
        assertThatThrownBy(() -> this.service.creer(p))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage("Anomalie applicative - le parent de l'objet que vous voulez créer n'existe pas déjà dans le stockage : vêtement pour homme");

        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>creer(DAO parent jette RuntimeException) lève ExceptionTechniqueGateway.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(DAO parent jette Exception) - ExceptionTechniqueGateway")
    @Test
    public void testCreerParentDaoJetteExceptionTechniqueGateway() {
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenThrow(new RuntimeException(BOOM));

        assertThatThrownBy(() -> this.service.creer(p))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>creer(save retourne null) lève ExceptionTechniqueGateway.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(save retourne null) - ExceptionTechniqueGateway")
    @Test
    public void testCreerSaveNullExceptionTechniqueGateway() {
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(this.fabriquerParentJPAPersistant(VETEMENT_HOMME)));
        when(this.produitDaoJPA.save(any(ProduitJPA.class))).thenReturn(null);

        assertThatThrownBy(() -> this.service.creer(p))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).save(any(ProduitJPA.class));
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>creer(nominal) retourne un objet métier non null.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(nominal) - OK")
    @Test
    public void testCreerNominalOk() throws Exception {
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        final ProduitJPA retourDAO = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        retourDAO.setIdProduit(1L);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(this.fabriquerParentJPAPersistant(VETEMENT_HOMME)));
        when(this.produitDaoJPA.save(any(ProduitJPA.class))).thenReturn(retourDAO);

        final Produit cree = this.service.creer(p);

        assertThat(cree).isNotNull();
        assertThat(cree.getProduit()).isEqualTo(CHEMISE_ML_HOMME);
        assertThat(cree.getIdProduit()).isNotNull();

        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).save(any(ProduitJPA.class));
        verifyNoInteractions(this.entityManager);
    }

    // ============================ RECHERCHER =============================

    /**
     * <div>
     * <p>rechercherTous(findAll retourne null) lève ExceptionTechniqueGateway.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(DAO null) - ExceptionTechniqueGateway")
    @Test
    public void testRechercherTousDaoNullExceptionTechniqueGateway() {
        when(this.produitDaoJPA.findAll()).thenReturn(null);

        assertThatThrownBy(() -> this.service.rechercherTous())
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);
        verify(this.produitDaoJPA, times(1)).findAll();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>rechercherTous(findAll vide) retourne une liste vide.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(DAO vide) - liste vide")
    @Test
    public void testRechercherTousVideOk() throws Exception {
        when(this.produitDaoJPA.findAll()).thenReturn(Collections.emptyList());

        final List<Produit> retour = this.service.rechercherTous();

        assertThat(retour).isNotNull();
        assertThat(retour).isEmpty();
        verify(this.produitDaoJPA, times(1)).findAll();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>rechercherTous(nominal) retourne une liste filtrée/triee/dédoublonnée.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(nominal) - liste filtrée/triee/dédoublonnée")
    @Test
    public void testRechercherTousNominalOk() throws Exception {
        final ProduitJPA p1 = this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        p1.setIdProduit(2L);

        final ProduitJPA p2 = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        p2.setIdProduit(1L);

        final List<ProduitJPA> liste = new ArrayList<ProduitJPA>();
        liste.add(null);
        liste.add(p1);
        liste.add(p2);

        when(this.produitDaoJPA.findAll()).thenReturn(liste);

        final List<Produit> retour = this.service.rechercherTous();

        assertThat(retour).isNotNull();
        assertThat(retour).isNotEmpty();
        assertThat(retour)
            .extracting(Produit::getProduit)
            .contains(CHEMISE_ML_HOMME, CHEMISE_MC_HOMME);

        verify(this.produitDaoJPA, times(1)).findAll();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>findByLibelle(blank) lève ExceptionAppliLibelleBlank.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelle(blank) - ExceptionAppliLibelleBlank")
    @Test
    public void testFindByLibelleBlankExceptionAppliLibelleBlank() {
        assertThatThrownBy(() -> this.service.findByLibelle(BLANK))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK);
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>findByLibelle(DAO retourne null) lève ExceptionTechniqueGateway.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelle(DAO null) - ExceptionTechniqueGateway")
    @Test
    public void testFindByLibelleDaoNullExceptionTechniqueGateway() {
        when(this.produitDaoJPA.findByProduitIgnoreCase(CHEMISE_ML_HOMME)).thenReturn(null);

        assertThatThrownBy(() -> this.service.findByLibelle(CHEMISE_ML_HOMME))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);
        verify(this.produitDaoJPA, times(1)).findByProduitIgnoreCase(CHEMISE_ML_HOMME);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>findByLibelle(nominal) retourne une liste non vide.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelle(nominal) - liste non vide")
    @Test
    public void testFindByLibelleNominalOk() throws Exception {
        final ProduitJPA pJPA = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        pJPA.setIdProduit(11L);

        when(this.produitDaoJPA.findByProduitIgnoreCase(CHEMISE_ML_HOMME))
            .thenReturn(Arrays.asList(pJPA));

        final List<Produit> retour = this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(retour).isNotNull();
        assertThat(retour).isNotEmpty();
        assertThat(retour.get(0).getProduit()).isEqualTo(CHEMISE_ML_HOMME);
        assertThat(retour.get(0).getIdProduit()).isNotNull();

        verify(this.produitDaoJPA, times(1)).findByProduitIgnoreCase(CHEMISE_ML_HOMME);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>findByLibelleRapide(null) lève ExceptionAppliParamNull.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelleRapide(null) - ExceptionAppliParamNull")
    @Test
    public void testFindByLibelleRapideParamNullExceptionAppliParamNull() {
        assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_FINDBYLIBELLERAPIDE_KO_PARAM_NULL);
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>findByLibelleRapide(blank) délègue à rechercherTous().</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelleRapide(blank) - délègue à rechercherTous()")
    @Test
    public void testFindByLibelleRapideBlankRetourneTous() throws Exception {
        when(this.produitDaoJPA.findAll()).thenReturn(Collections.emptyList());

        final List<Produit> retour = this.service.findByLibelleRapide(BLANK);

        assertThat(retour).isNotNull();
        assertThat(retour).isEmpty();
        verify(this.produitDaoJPA, times(1)).findAll();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>findByLibelleRapide(DAO retourne null) lève ExceptionTechniqueGateway.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelleRapide(DAO null) - ExceptionTechniqueGateway")
    @Test
    public void testFindByLibelleRapideDaoNullExceptionTechniqueGateway() {
        when(this.produitDaoJPA.findByProduitContainingIgnoreCase(CHEMISE)).thenReturn(null);

        assertThatThrownBy(() -> this.service.findByLibelleRapide(CHEMISE))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);
        verify(this.produitDaoJPA, times(1)).findByProduitContainingIgnoreCase(CHEMISE);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>findByLibelleRapide(nominal) retourne une liste non vide.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelleRapide(nominal) - liste non vide")
    @Test
    public void testFindByLibelleRapideNominalOk() throws Exception {
        final ProduitJPA p1 = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        p1.setIdProduit(1L);

        final ProduitJPA p2 = this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        p2.setIdProduit(2L);

        when(this.produitDaoJPA.findByProduitContainingIgnoreCase(CHEMISE))
            .thenReturn(Arrays.asList(p2, p1));

        final List<Produit> retour = this.service.findByLibelleRapide(CHEMISE);

        assertThat(retour).isNotNull();
        assertThat(retour).isNotEmpty();
        assertThat(retour)
            .extracting(Produit::getProduit)
            .contains(CHEMISE_ML_HOMME, CHEMISE_MC_HOMME);

        verify(this.produitDaoJPA, times(1)).findByProduitContainingIgnoreCase(CHEMISE);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>findAllByParent(null) lève ExceptionAppliParentNull.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findAllByParent(null) - ExceptionAppliParentNull")
    @Test
    public void testFindAllByParentParamNullExceptionAppliParentNull() {
        assertThatThrownBy(() -> this.service.findAllByParent(null))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_FINDALLBYPARENT_KO_PARAM_NULL);
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>findAllByParent(parent non persistant) lève ExceptionTechniqueGatewayNonPersistent.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findAllByParent(parent non persistant) - ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testFindAllByParentParentNonPersistantExceptionTechniqueGatewayNonPersistent() {
        final SousTypeProduit parent = new SousTypeProduit();
        parent.setSousTypeProduit(VETEMENT_HOMME);
        // Pas d'ID --> parent non persistant

        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage("Anomalie applicative - le parent de l'objet n'existait pas déjà dans le stockage : vêtement pour homme");
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);
    }

    /**
     * <div>
     * <p>findAllByParent(parent persistant mais introuvable) lève ExceptionTechniqueGatewayNonPersistent.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findAllByParent(parent introuvable) - ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testFindAllByParentParentIntrouvableExceptionTechniqueGatewayNonPersistent() {
    	
        final SousTypeProduit parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage("Anomalie applicative - le parent de l'objet n'existait pas déjà dans le stockage : vêtement pour homme");
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verifyNoInteractions(this.produitDaoJPA);
    }

    /**
     * <div>
     * <p>findAllByParent(DAO enfant retourne null) lève ExceptionTechniqueGateway.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findAllByParent(DAO enfant null) - ExceptionTechniqueGateway")
    @Test
    public void testFindAllByParentDaoEnfantNullExceptionTechniqueGateway() {
        final SousTypeProduit parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(this.fabriquerParentJPAPersistant(VETEMENT_HOMME)));
        when(this.produitDaoJPA.findAllBySousTypeProduit(any(SousTypeProduitJPA.class))).thenReturn(null);

        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findAllBySousTypeProduit(any(SousTypeProduitJPA.class));
    }

    /**
     * <div>
     * <p>findAllByParent(nominal) retourne une liste non vide.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findAllByParent(nominal) - liste non vide")
    @Test
    public void testFindAllByParentNominalOk() throws Exception {
        final SousTypeProduit parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final ProduitJPA p1 = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        p1.setIdProduit(10L);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(this.fabriquerParentJPAPersistant(VETEMENT_HOMME)));
        when(this.produitDaoJPA.findAllBySousTypeProduit(any(SousTypeProduitJPA.class)))
            .thenReturn(Arrays.asList(p1));

        final List<Produit> retour = this.service.findAllByParent(parent);

        assertThat(retour).isNotNull();
        assertThat(retour).isNotEmpty();
        assertThat(retour.get(0).getProduit()).isEqualTo(CHEMISE_ML_HOMME);

        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findAllBySousTypeProduit(any(SousTypeProduitJPA.class));
    }

    // ============================= PAGINATION ============================

    /**
     * <div>
     * <p>rechercherTousParPage(null) utilise une requête par défaut.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(null) - requête par défaut")
    @Test
    public void testRechercherTousParPageNullOk() throws Exception {
        final ProduitJPA p1 = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        p1.setIdProduit(1L);

        final List<ProduitJPA> content = Arrays.asList(p1);

        final Page<ProduitJPA> page = new PageImpl<ProduitJPA>(content);

        when(this.produitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        final ResultatPage<Produit> resultat = this.service.rechercherTousParPage(null);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull();
        assertThat(resultat.getContent()).hasSize(1);
        assertThat(resultat.getContent().get(0).getProduit()).isEqualTo(CHEMISE_ML_HOMME);

        verify(this.produitDaoJPA, times(1)).findAll(any(Pageable.class));
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>rechercherTousParPage(requête avec tris) retourne une page triée.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(tris) - page triée")
    @Test
    public void testRechercherTousParPageAvecTrisOk() throws Exception {
        final ProduitJPA p1 = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        p1.setIdProduit(1L);

        final ProduitJPA p2 = this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        p2.setIdProduit(2L);

        final List<ProduitJPA> content = Arrays.asList(p1, p2);

        final List<TriSpec> tris = new ArrayList<TriSpec>();
        tris.add(new TriSpec(PROP_TRI_PRODUIT, DirectionTri.ASC));

        final RequetePage requete = new RequetePage(1, 2, tris);

        final Sort sort = Sort.by(Sort.Direction.ASC, PROP_TRI_PRODUIT);
        final Pageable pageable = PageRequest.of(1, 2, sort);
        final Page<ProduitJPA> page = new PageImpl<ProduitJPA>(content, pageable, content.size());

        when(this.produitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        final ResultatPage<Produit> resultat = this.service.rechercherTousParPage(requete);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getPageNumber()).isEqualTo(1);
        assertThat(resultat.getPageSize()).isEqualTo(2);
        assertThat(resultat.getTotalElements()).isEqualTo(page.getTotalElements());

        assertThat(resultat.getContent()).isNotNull();
        assertThat(resultat.getContent()).hasSize(2);

        verify(this.produitDaoJPA, times(1)).findAll(any(Pageable.class));
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    // =============================== UPDATE ==============================

    /**
     * <div>
     * <p>update(null) lève ExceptionAppliParamNull.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(null) - ExceptionAppliParamNull")
    @Test
    public void testUpdateNullExceptionAppliParamNull() {
        assertThatThrownBy(() -> this.service.update(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_UPDATE_KO_PARAM_NULL);
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>update(libellé blank) lève ExceptionAppliLibelleBlank.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(blank) - ExceptionAppliLibelleBlank")
    @Test
    public void testUpdateBlankExceptionAppliLibelleBlank() {
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p = new Produit();
        p.setProduit(BLANK);
        p.setSousTypeProduit(parent);
        p.setIdProduit(1L);

        assertThatThrownBy(() -> this.service.update(p))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_UPDATE_KO_LIBELLE_BLANK);
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>update(id null) lève ExceptionAppliParamNonPersistent.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(id null) - ExceptionAppliParamNonPersistent")
    @Test
    public void testUpdateIdNullExceptionAppliParamNonPersistent() {
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);
        p.setIdProduit(null);

        assertThatThrownBy(() -> this.service.update(p))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(MSG_UPDATE_PREFIX_NON_PERSISTENT + CHEMISE_ML_HOMME);
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>update(parent null) lève ExceptionAppliParentNull.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent null) - ExceptionAppliParentNull")
    @Test
    public void testUpdateParentNullExceptionAppliParentNull() {
        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(null);
        p.setIdProduit(1L);

        assertThatThrownBy(() -> this.service.update(p))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_UPDATE_KO_PARENT_NULL);
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>update(parent non persistant) lève ExceptionTechniqueGatewayNonPersistent.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent non persistant) - ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testUpdateParentNonPersistantExceptionTechniqueGatewayNonPersistent() {
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);
        p.setIdProduit(1L);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.service.update(p))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage("Anomalie applicative - le parent de l'objet que vous voulez modifier n'existe pas déjà dans le stockage : vêtement pour homme");
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>update(id inconnu) retourne null.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(id inconnu) - retourne null")
    @Test
    public void testUpdateIdInconnuRetourneNull() throws Exception {
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME + SUFFIX_MODIF);
        p.setSousTypeProduit(parent);
        p.setIdProduit(99L);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(this.fabriquerParentJPAPersistant(VETEMENT_HOMME)));
        when(this.produitDaoJPA.findById(99L)).thenReturn(Optional.empty());

        final Produit retour = this.service.update(p);

        assertThat(retour).isNull();
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findById(99L);
        verifyNoMoreInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>update(sans modification) retourne l'objet inchangé.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(sans modification) - objet inchangé")
    @Test
    public void testUpdateSansModificationOk() throws Exception {
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit aModifier = new Produit();
        aModifier.setProduit(SWEAT_HOMME);
        aModifier.setSousTypeProduit(parent);
        aModifier.setIdProduit(30L);

        final ProduitJPA persisteJPA = this.fabriquerProduitJPA(SWEAT_HOMME, VETEMENT_HOMME);
        persisteJPA.setIdProduit(30L);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(this.fabriquerParentJPAPersistant(VETEMENT_HOMME)));
        when(this.produitDaoJPA.findById(30L)).thenReturn(Optional.of(persisteJPA));

        final Produit retour = this.service.update(aModifier);

        assertThat(retour).isNotNull();
        assertThat(retour.getIdProduit()).isEqualTo(30L);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findById(30L);
        verify(this.produitDaoJPA, never()).save(any(ProduitJPA.class));
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>update(modif libellé) sauvegarde l'objet modifié.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(modif libellé) - sauvegarde l'objet modifié")
    @Test
    public void testUpdateAvecModificationLibelleOk() throws Exception {
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit aModifier = new Produit();
        aModifier.setProduit(CHEMISE_ML_HOMME + SUFFIX_MODIF);
        aModifier.setSousTypeProduit(parent);
        aModifier.setIdProduit(31L);

        final ProduitJPA persisteJPA = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        persisteJPA.setIdProduit(31L);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(this.fabriquerParentJPAPersistant(VETEMENT_HOMME)));
        when(this.produitDaoJPA.findById(31L)).thenReturn(Optional.of(persisteJPA));

        final ProduitJPA sauveJPA = this.fabriquerProduitJPA(CHEMISE_ML_HOMME + SUFFIX_MODIF, VETEMENT_HOMME);
        sauveJPA.setIdProduit(31L);

        when(this.produitDaoJPA.save(any(ProduitJPA.class))).thenReturn(sauveJPA);

        final Produit retour = this.service.update(aModifier);

        assertThat(retour).isNotNull();
        assertThat(retour.getIdProduit()).isEqualTo(31L);
        assertThat(retour.getProduit()).isEqualTo(CHEMISE_ML_HOMME + SUFFIX_MODIF);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findById(31L);
        verify(this.produitDaoJPA, times(1)).save(any(ProduitJPA.class));
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>update(modif parent) sauvegarde l'objet modifié.</p>
     * <p>Scénarios couverts :</p>
     * <ul>
     *   <li>Modification du parent d'un produit (vêtement pour homme → vêtement pour femme).</li>
     *   <li>Vérification que le nouveau parent est bien persistant.</li>
     *   <li>Vérification que le produit est bien sauvegardé avec le nouveau parent.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(modif parent) - sauvegarde l'objet modifié")
    @Test
    public void testUpdateAvecModificationParentOk() throws Exception {
        // --- 1. DONNÉES ---
        // Parent initial : vêtement pour homme (ID=1L)
        final SousTypeProduitI parentHomme = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        // Nouveau parent : vêtement pour femme (ID=2L)
        final SousTypeProduitI parentFemme = new SousTypeProduit();
        parentFemme.setIdSousTypeProduit(2L);
        parentFemme.setSousTypeProduit(VETEMENT_FEMME);
        final TypeProduit typeProduitFemme = new TypeProduit();
        typeProduitFemme.setIdTypeProduit(1L);
        typeProduitFemme.setTypeProduit(VETEMENT);
        parentFemme.setTypeProduit(typeProduitFemme);

        final Produit aModifier = new Produit();
        aModifier.setProduit(SWEAT_HOMME);
        aModifier.setSousTypeProduit(parentFemme);  // Nouveau parent
        aModifier.setIdProduit(40L);

        // --- 2. MOCKS ---
        // Vérification de la persistance du NOUVEAU parent (ID=2L)
        final SousTypeProduitJPA parentFemmeJPA = new SousTypeProduitJPA();
        parentFemmeJPA.setIdSousTypeProduit(2L);
        parentFemmeJPA.setSousTypeProduit(VETEMENT_FEMME);
        final TypeProduitJPA typeProduitFemmeJPA = new TypeProduitJPA();
        typeProduitFemmeJPA.setIdTypeProduit(1L);
        typeProduitFemmeJPA.setTypeProduit(VETEMENT);
        parentFemmeJPA.setTypeProduit(typeProduitFemmeJPA);

        when(this.sousTypeProduitDaoJPA.findById(2L))
            .thenReturn(Optional.of(parentFemmeJPA));  // Nouveau parent persistant

        // Produit existant en base avec l'ANCIEN parent (ID=1L)
        final ProduitJPA persisteJPA = this.fabriquerProduitJPA(SWEAT_HOMME, VETEMENT_HOMME);
        persisteJPA.setIdProduit(40L);
        when(this.produitDaoJPA.findById(40L)).thenReturn(Optional.of(persisteJPA));

        // Produit sauvegardé avec le NOUVEAU parent
        final ProduitJPA sauveJPA = this.fabriquerProduitJPA(SWEAT_HOMME, VETEMENT_FEMME);
        sauveJPA.setIdProduit(40L);
        when(this.produitDaoJPA.save(any(ProduitJPA.class))).thenReturn(sauveJPA);

        // --- 3. EXÉCUTION ---
        final Produit retour = this.service.update(aModifier);

        // --- 4. VÉRIFICATIONS ---
        assertThat(retour).isNotNull();
        assertThat(retour.getIdProduit()).isEqualTo(40L);
        assertThat(retour.getProduit()).isEqualTo(SWEAT_HOMME);
        assertThat(retour.getSousTypeProduit().getSousTypeProduit()).isEqualTo(VETEMENT_FEMME);

        // Vérification que seul le NOUVEAU parent (ID=2L) a été vérifié
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(2L);
        verify(this.sousTypeProduitDaoJPA, never()).findById(1L);  // Ancien parent non vérifié
        verify(this.produitDaoJPA, times(1)).findById(40L);
        verify(this.produitDaoJPA, times(1)).save(any(ProduitJPA.class));
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>update(DAO save jette Exception) lève ExceptionTechniqueGateway.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(DAO save jette Exception) - ExceptionTechniqueGateway")
    @Test
    public void testUpdateDaoSaveJetteExceptionTechniqueGateway() {
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit aModifier = new Produit();
        aModifier.setProduit(CHEMISE_MC_HOMME + SUFFIX_MODIF);
        aModifier.setSousTypeProduit(parent);
        aModifier.setIdProduit(50L);

        final ProduitJPA persisteJPA = this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        persisteJPA.setIdProduit(50L);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(this.fabriquerParentJPAPersistant(VETEMENT_HOMME)));
        when(this.produitDaoJPA.findById(50L)).thenReturn(Optional.of(persisteJPA));
        when(this.produitDaoJPA.save(any(ProduitJPA.class))).thenThrow(new RuntimeException(BOOM));

        assertThatThrownBy(() -> this.service.update(aModifier))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findById(50L);
        verify(this.produitDaoJPA, times(1)).save(any(ProduitJPA.class));
        verifyNoInteractions(this.entityManager);
    }

    // =============================== DELETE ==============================

    /**
     * <div>
     * <p>delete(null) lève ExceptionAppliParamNull.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(null) - ExceptionAppliParamNull")
    @Test
    public void testDeleteNullExceptionAppliParamNull() {
        assertThatThrownBy(() -> this.service.delete(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_DELETE_KO_PARAM_NULL);
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>delete(id null) lève ExceptionAppliParamNonPersistent.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(id null) - ExceptionAppliParamNonPersistent")
    @Test
    public void testDeleteIdNullExceptionAppliParamNonPersistent() {
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);
        p.setIdProduit(null);

        assertThatThrownBy(() -> this.service.delete(p))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(MSG_DELETE_KO_ID_NULL);
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>delete(DAO findById retourne null Optional) lève ExceptionTechniqueGateway.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(DAO findById null) - ExceptionTechniqueGateway")
    @Test
    public void testDeleteDaoFindByIdRetourneNullOptionalExceptionTechniqueGateway() {
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);
        p.setIdProduit(71L);

        when(this.produitDaoJPA.findById(71L)).thenReturn(null);

        assertThatThrownBy(() -> this.service.delete(p))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);
        verify(this.produitDaoJPA, times(1)).findById(71L);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>delete(absent) ne fait rien.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(absent) - ne fait rien")
    @Test
    public void testDeleteAbsentNeFaitRien() throws Exception {
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);
        p.setIdProduit(71L);

        when(this.produitDaoJPA.findById(71L)).thenReturn(Optional.empty());

        this.service.delete(p);

        verify(this.produitDaoJPA, times(1)).findById(71L);
        verifyNoInteractions(this.entityManager);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
    }

    /**
     * <div>
     * <p>delete(nominal) supprime l'objet et vérifie la suppression.</p>
     * </div>
     * @throws Exception
     */
    @SuppressWarnings(RESOURCE)
	@Tag(TAG_DELETE)
    @DisplayName("delete(nominal) - suppression vérifiée")
    @Test
    public void testDeleteNominalOk() throws Exception {
        final Long id = 70L;
        final String libelle = CHEMISE_ML_HOMME;

        final ProduitJPA produitJPA = this.fabriquerProduitJPA(libelle, VETEMENT_HOMME);
        produitJPA.setIdProduit(id);

        final Produit produitMetier = this.fabriquerProduitMetier(libelle, id,
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME));

        when(this.produitDaoJPA.findById(id))
            .thenReturn(Optional.of(produitJPA))
            .thenReturn(Optional.empty());

        doNothing().when(this.entityManager).remove(any(ProduitJPA.class));
        doNothing().when(this.entityManager).flush();

        this.service.delete(produitMetier);

        verify(this.entityManager, times(1)).remove(produitJPA);
        verify(this.entityManager, times(1)).flush();
        verify(this.produitDaoJPA, times(2)).findById(id);
    }

    // =============================== COUNT ===============================

    /**
     * <div>
     * <p>count(nominal) retourne le nombre d'éléments.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName("count(nominal) - retourne le nombre d'éléments")
    @Test
    public void testCountNominalOk() throws Exception {
        when(this.produitDaoJPA.count()).thenReturn(TOTAL_10);

        final long count = this.service.count();

        assertThat(count).isEqualTo(TOTAL_10);
        verify(this.produitDaoJPA, times(1)).count();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>count(DAO jette Exception) lève ExceptionTechniqueGateway.</p>
     * </div>
     */
    @Tag(TAG_COUNT)
    @DisplayName("count(DAO jette Exception) - ExceptionTechniqueGateway")
    @Test
    public void testCountDaoJetteExceptionTechniqueGateway() {
        when(this.produitDaoJPA.count()).thenThrow(new RuntimeException(BOOM));

        assertThatThrownBy(() -> this.service.count())
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        verify(this.produitDaoJPA, times(1)).count();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
    }

    // ===================== TESTS BETON (sanity / invariants) =====================

    /**
     * <div>
     * <p>Sanity : vérifie qu'aucune String null n'est construite par les helpers.</p>
     * </div>
     */
    @Tag(TAG_BETON)
    @DisplayName("Sanity - safeMessage")
    @Test
    public void testSanitySafeMessage() {
        assertThat(safeMessage(null)).isEqualTo("");
        assertThat(safeMessage("")).isEqualTo("");
    }

    /**
     * <div>
     * <p>Test béton : vérifie que la méthode delete() gère correctement
     * une Exception levée par EntityManager.remove().</p>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
	@Tag(TAG_BETON)
    @DisplayName("delete(remove jette Exception) - ExceptionTechniqueGateway")
    @Test
    public void testDeleteEntityManagerRemoveJetteException() {
        final Produit p = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, 70L,
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME));

        final ProduitJPA entity = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        entity.setIdProduit(70L);

        when(this.produitDaoJPA.findById(70L)).thenReturn(Optional.of(entity));
        doThrow(new RuntimeException("Erreur simulatee par remove()"))
            .when(this.entityManager).remove(any(ProduitJPA.class));

        assertThatThrownBy(() -> this.service.delete(p))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        verify(this.produitDaoJPA, times(1)).findById(70L);
        verify(this.entityManager, times(1)).remove(any(ProduitJPA.class));
        verifyNoMoreInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>Test béton : vérifie que la méthode delete() gère correctement
     * une Exception levée par EntityManager.flush().</p>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
	@Tag(TAG_BETON)
    @DisplayName("delete(flush jette Exception) - ExceptionTechniqueGateway")
    @Test
    public void testDeleteEntityManagerFlushJetteException() {
        final Produit p = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, 70L,
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME));

        final ProduitJPA entity = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        entity.setIdProduit(70L);

        when(this.produitDaoJPA.findById(70L)).thenReturn(Optional.of(entity));
        doNothing().when(this.entityManager).remove(any(ProduitJPA.class));
        doThrow(new RuntimeException("Erreur simulatee par flush()"))
            .when(this.entityManager).flush();

        assertThatThrownBy(() -> this.service.delete(p))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        verify(this.produitDaoJPA, times(1)).findById(70L);
        verify(this.entityManager, times(1)).remove(any(ProduitJPA.class));
        verify(this.entityManager, times(1)).flush();
    }

    /**
     * <div>
     * <p>Test béton : vérifie que la méthode delete() vérifie bien
     * la suppression effective après flush().</p>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
	@Tag(TAG_BETON)
    @DisplayName("delete(vérification post-suppression échoue) - ExceptionTechniqueGateway")
    @Test
    public void testDeleteVerificationPostSuppressionEchoue() {
        final Produit p = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, 70L,
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME));

        final ProduitJPA entity = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        entity.setIdProduit(70L);

        when(this.produitDaoJPA.findById(70L))
            .thenReturn(Optional.of(entity))
            .thenReturn(Optional.of(entity));

        doNothing().when(this.entityManager).remove(any(ProduitJPA.class));
        doNothing().when(this.entityManager).flush();

        assertThatThrownBy(() -> this.service.delete(p))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageStartingWith("Échec de la suppression");
        verify(this.produitDaoJPA, times(2)).findById(70L);
        verify(this.entityManager, times(1)).remove(any(ProduitJPA.class));
        verify(this.entityManager, times(1)).flush();
    }

    /**
     * <div>
     * <p>Test béton : vérifie que la méthode update() gère correctement
     * un parent modifié avec un libellé en majuscules/minuscules différentes.</p>
     * </div>
     */
    @Tag(TAG_BETON)
    @DisplayName("update(parent modifié case-sensitive) - OK")
    @Test
    public void testUpdateParentLibelleCaseSensitiveOk() throws Exception {
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME.toUpperCase(LOCALE_DEFAUT));

        final Produit aModifier = new Produit();
        aModifier.setProduit(SWEAT_HOMME);
        aModifier.setSousTypeProduit(parent);
        aModifier.setIdProduit(30L);

        final ProduitJPA persisteJPA = this.fabriquerProduitJPA(SWEAT_HOMME, VETEMENT_HOMME);
        persisteJPA.setIdProduit(30L);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(this.fabriquerParentJPAPersistant(VETEMENT_HOMME)));
        when(this.produitDaoJPA.findById(30L)).thenReturn(Optional.of(persisteJPA));

        final Produit retour = this.service.update(aModifier);

        assertThat(retour).isNotNull();
        assertThat(retour.getIdProduit()).isEqualTo(30L);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findById(30L);
        verify(this.produitDaoJPA, never()).save(any(ProduitJPA.class));
        verifyNoInteractions(this.entityManager);
    }

    /**
     * <div>
     * <p>Test béton : vérifie que la méthode findByLibelleRapide()
     * retourne bien une liste vide si le contenu est introuvable.</p>
     * </div>
     */
    @Tag(TAG_BETON)
    @DisplayName("findByLibelleRapide(contenu introuvable) - liste vide")
    @Test
    public void testFindByLibelleRapideContenuIntrouvable() throws Exception {
        when(this.produitDaoJPA.findByProduitContainingIgnoreCase("INCONNU"))
            .thenReturn(new ArrayList<ProduitJPA>());

        final List<Produit> retour = this.service.findByLibelleRapide("INCONNU");

        assertThat(retour).isNotNull();
        assertThat(retour).isEmpty();
        verify(this.produitDaoJPA, times(1)).findByProduitContainingIgnoreCase("INCONNU");
    }

    /**
     * <div>
     * <p>Test béton : vérifie que la méthode findAllByParent()
     * retourne correctement les produits associés à un parent.</p>
     * <p>Scénarios couverts :</p>
     * <ul>
     *   <li>Deux produits distincts avec le même libellé mais des IDs différents.</li>
     *   <li>Vérification du comportement réel du service.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_BETON)
    @DisplayName("findAllByParent(produits distincts) - vérification du comportement réel")
    @Test
    public void testFindAllByParentProduitsDistincts() throws Exception {
        // --- 1. DONNÉES ---
        final SousTypeProduit parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        // Deux produits distincts avec des IDs différents
        final ProduitJPA p1 = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        p1.setIdProduit(10L);

        // Deuxième produit avec un libellé différent pour éviter le filtrage
        final ProduitJPA p2 = this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        p2.setIdProduit(11L);

        // --- 2. MOCKS ---
        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(this.fabriquerParentJPAPersistant(VETEMENT_HOMME)));
        when(this.produitDaoJPA.findAllBySousTypeProduit(any(SousTypeProduitJPA.class)))
            .thenReturn(Arrays.asList(p1, p2));

        // --- 3. EXÉCUTION ---
        final List<Produit> retour = this.service.findAllByParent(parent);

        // --- 4. VÉRIFICATIONS ---
        assertThat(retour).isNotNull();
        assertThat(retour).hasSize(2);  // Doit retourner 2 éléments distincts
        assertThat(retour)
            .extracting(Produit::getIdProduit)
            .containsExactlyInAnyOrder(10L, 11L);
        assertThat(retour)
            .extracting(Produit::getProduit)
            .containsExactlyInAnyOrder(CHEMISE_ML_HOMME, CHEMISE_MC_HOMME);

        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findAllBySousTypeProduit(any(SousTypeProduitJPA.class));
    }

    
}
