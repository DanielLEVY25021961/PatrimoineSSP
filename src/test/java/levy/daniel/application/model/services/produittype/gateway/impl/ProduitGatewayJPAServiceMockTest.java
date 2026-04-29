/* ********************************************************************* */
/* ********************* TEST MOCKITO GATEWAY JPA ********************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.gateway.impl; // NOPMD by danyl on 27/04/2026 22:15

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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE ProduitGatewayJPAServiceMockTest.java :
 * </p>
 * <p>
 * Tests unitaires Mockito (DAO mockés) du service ADAPTER GATEWAY
 * {@link ProduitGatewayJPAService}.
 * </p>
 *
 * <p style="font-weight:bold;">CONTEXTE :</p>
 * <ul>
 * <li>Utilisation de Mockito pour simuler les dépendances.</li>
 * <li>Vérification des comportements applicatifs et techniques.</li>
 * <li>Respect strict des contrats définis dans le PORT GATEWAY
 * {@link ProduitGatewayIService}.</li>
 * <li>Le service testé manipule un objet métier {@link Produit}
 * rattaché à un parent {@link SousTypeProduit}.</li>
 * <li>Le test mocke donc le DAO de l'objet métier {@link Produit}
 * et le DAO du parent {@link SousTypeProduit}.</li>
 * <li>Contrairement à {@link SousTypeProduitGatewayJPAServiceMockTest},
 * cette classe ne mocke pas d'EntityManager :
 * {@link ProduitGatewayJPAService} ne l'utilise pas directement.</li>
 * </ul>
 *
 * <p style="font-weight:bold;">GARANTIES :</p>
 * <ul>
 * <li>Aucune <code>NullPointerException</code> levée.</li>
 * <li>Toutes les ressources Mockito sont correctement fermées.</li>
 * <li>Les vérifications portent uniquement sur les collaborateurs réels
 * du service testé.</li>
 * <li>Les appels critiques au stockage sont contrôlés via les DAO mockés.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 19 janvier 2026
 */
@ExtendWith(MockitoExtension.class)
public class ProduitGatewayJPAServiceMockTest {

    // ************************* CONSTANTES ******************************/
    
    /**
     * "resource"
     */
    public static final String RESOURCE = "resource";
    
    /** "servicesGateway-Creer" */
    public static final String TAG_CREER = "servicesGateway-Creer";

    /** "servicesGateway-Rechercher" */
    public static final String TAG_RECHERCHER = "servicesGateway-Rechercher";

    /** "servicesGateway-Update" */
    public static final String TAG_UPDATE = "servicesGateway-Update";

    /** "servicesGateway-Delete" */
    public static final String TAG_DELETE = "servicesGateway-Delete";

    /** "servicesGateway-Count" */
    public static final String TAG_COUNT = "servicesGateway-Count";

    /** "servicesGateway-Pagination" */
    public static final String TAG_PAGINATION = "servicesGateway-Pagination";

    /** "servicesGateway-Beton" */
    public static final String TAG_BETON = "servicesGateway-Beton";

    /** "vêtement" */
    public static final String VETEMENT = "vêtement";

    /** "vêtement pour homme" */
    public static final String VETEMENT_HOMME = "vêtement pour homme";

    /** "vêtement pour femme" */
    public static final String VETEMENT_FEMME = "vêtement pour femme";

    /** "chemise" */
    public static final String CHEMISE = "chemise";

    /** "chemise à manches longues pour homme" */
    public static final String CHEMISE_ML_HOMME 
    	= "chemise à manches longues pour homme";

    /** "chemise à manches courtes pour homme" */
    public static final String CHEMISE_MC_HOMME 
    	= "chemise à manches courtes pour homme";
    
    /**
     * "chemise à manches longues pour femme"
     */
    public static final String CHEMISE_ML_FEMME 
    	= "chemise à manches longues pour femme";

    /** "sweatshirt pour homme" */
    public static final String SWEAT_HOMME = "sweatshirt pour homme";

    /** "   " */
    public static final String BLANK = "   ";

    /** "boom" */
    public static final String BOOM = "boom";

    /** " (modifié)" */
    public static final String SUFFIX_MODIF = " (modifié)";

    /** "produit" */
    public static final String PROP_TRI_PRODUIT = "produit";

    /** 0 */
    public static final int PAGE_0 = 0;

    /** 5 */
    public static final int SIZE_5 = 5;

    /** 10L */
    public static final long TOTAL_10 = 10L;

    /** "Erreur Technique lors du stockage : " */
    public static final String MSG_PREFIX_ERREUR_TECH 
    	= ProduitGatewayIService.ERREUR_TECHNIQUE_STOCKAGE;

    /** "Erreur Technique - Le stockage a retourné null." */
    public static final String MSG_ERREUR_TECH_KO_STOCKAGE 
    	= ProduitGatewayIService.ERREUR_TECHNIQUE_KO_STOCKAGE;

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
	 * <p>""Anomalie applicative - 
	 * le parent de l'objet que vous voulez créer n'existe
	 * pas déjà dans le stockage : "</p>
	 * </div> 
     */
    public static final String MSG_CREER_KO_PARENT_NON_PERSISTENT
        = ProduitGatewayIService.MESSAGE_CREER_KO_PARENT_NON_PERSISTENT;

    /** "X".repeat(10_000) */
    public static final String LIBELLE_TROP_LONG = "X".repeat(10_000);

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
    public static final String MSG_UPDATE_PREFIX_NON_PERSISTENT 
    	= ProduitGatewayIService.MESSAGE_UPDATE_KO_NON_PERSISTENT;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un parent null."</p>
	 * </div>
     */
    public static final String MSG_UPDATE_KO_PARENT_NULL 
    	= ProduitGatewayIService.MESSAGE_UPDATE_KO_PARENT_NULL;

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
     * "Anomalie applicative - le parent de l'objet n'existait pas déjà dans le stockage : vêtement pour homme"
     */
    public static final String ANOMALIE_APPLI_PAS_PARENT 
    	= "Anomalie applicative - le parent de l'objet n'existait pas déjà dans le stockage : vêtement pour homme";

    // ************************** ATTRIBUTS ******************************/
    
    /**
     * <div> 
     * <p>Locale par défaut = {@code Locale.getDefault()} </p>
     * </div> 
     */
    public static final Locale LOCALE_DEFAUT = Locale.getDefault();

    /**
     * <div>
     * <p>DAO mocké pour l'objet métier Produit.</p>
     * </div>
     */
    @Mock
    private ProduitDaoJPA produitDaoJPA;

    /**
     * <div>
     * <p>DAO mocké pour le parent SousTypeProduit.</p>
     * </div>
     */
    @Mock
    private SousTypeProduitDaoJPA sousTypeProduitDaoJPA;

    /**
     * <div>
     * <p>Service {@link ProduitGatewayJPAService}  
     * <span style="font-weight:bold;">réel</span> 
     * testé (avec injection des mocks).</p>
     * <ul>
     * <li>le service réel est instancié dans la méthode {@link #init()} 
     * avant chaque test (@BeforeEach).</li>
     * <li>@InjectMocks demande à Mockito de créer l’instance du service 
     * puis d’y injecter automatiquement les dépendances mockées déclarées 
     * dans la classe de test, typiquement les champs annotés avec @Mock.</li> 
     * <li>@InjectMocks ne crée donc pas un mock du service. 
     * Il crée ou prépare le service réel, puis il y injecte 
     * les mocks disponibles.</li>
     * </ul>
     * </div>
     */
    @InjectMocks
    private ProduitGatewayJPAService service;
    
    

    // ************************* CONSTRUCTEURS ***************************/

    
    
    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
     * </div>
     */
    public ProduitGatewayJPAServiceMockTest() {
        super();
    }

    

    // ============================== INIT ================================

    
    
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
    }

    

    // **************************** TESTS ********************************/
    
    
    
    // =============================== CREER ==============================


    
    /**
     * <div>
     * <p>garantit que creer(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_CREER_KO_PARAM_NULL} ;</li>
     * <li>n'appelle ni le DAO parent, ni le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(null) - jette ExceptionAppliParamNull (contrat du port)")
    @Test
    public void testCreerNull() {

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel service.creer(...)
         * avec un paramètre null :
         * - jette une ExceptionAppliParamNull ;
         * - émet le message MSG_CREER_KO_PARAM_NULL
         *   (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.creer(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_CREER_KO_PARAM_NULL);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * - sousTypeProduitDaoJPA n'a jamais été appelé ;
         * - produitDaoJPA n'a jamais été appelé ;
         */
        /* - verify(..., never()).méthode(...) = preuve ciblée 
         * sur une méthode critique précise.
         * - verifyNoInteractions(mock) = preuve globale 
         * que le mock entier n'a pas été touché.*/
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que creer(libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_CREER_KO_LIBELLE_BLANK} ;</li>
     * <li>n'appelle ni le DAO parent, ni le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testCreerLibelleBlank() {

        /* ARRANGE :
         * prépare un produit dont le libellé est blank,
         * avec un parent persistant,
         * afin de vérifier le contrôle applicatif
         * effectué avant toute tentative d'accès au stockage.
         */
        final SousTypeProduitI parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit produit = new Produit(null, BLANK, parent);

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel service.creer(...)
         * avec un objet métier ayant un libellé blank
         * jette une ExceptionAppliLibelleBlank
         * avec le message MSG_CREER_KO_LIBELLE_BLANK
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.creer(produit))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_CREER_KO_LIBELLE_BLANK);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * - sousTypeProduitDaoJPA n'a jamais été appelé ;
         * - produitDaoJPA n'a jamais été appelé ;
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que creer(parent null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParentNull} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_CREER_KO_PARENT_NULL} ;</li>
     * <li>n'appelle ni le DAO parent, ni le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(parent null) - jette ExceptionAppliParentNull (contrat du port)")
    @Test
    public void testCreerParentNull() {

        /* ARRANGE :
         * prépare un produit avec un libellé valide,
         * mais sans parent SousTypeProduit,
         * afin de vérifier le contrôle applicatif
         * effectué avant toute tentative d'accès au stockage.
         */
        final Produit produit = new Produit();
        produit.setProduit(CHEMISE_ML_HOMME);
        produit.setSousTypeProduit(null);

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel service.creer(...)
         * avec un objet métier sans parent
         * jette une ExceptionAppliParentNull
         * avec le message MSG_CREER_KO_PARENT_NULL
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.creer(produit))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_CREER_KO_PARENT_NULL);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * - sousTypeProduitDaoJPA n'a jamais été appelé ;
         * - produitDaoJPA n'a jamais été appelé ;
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que creer(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'appelle ni le DAO parent, ni le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testCreerParentLibelleBlank() {

        /* ARRANGE :
         * prépare un objet métier avec un libellé valide,
         * mais avec un parent dont le libellé est blank,
         * afin de vérifier le contrôle applicatif
         * effectué avant toute tentative d'accès au stockage.
         */
        final SousTypeProduitI parent =
                this.fabriquerParentMetierPersistant(BLANK);

        final Produit produit = new Produit();
        produit.setProduit(CHEMISE_ML_HOMME);
        produit.setSousTypeProduit(parent);

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel service.creer(...)
         * avec un objet métier dont le parent a un libellé blank
         * jette une ExceptionAppliLibelleBlank
         * avec le message MSG_CREER_KO_LIBELLE_PARENT_BLANK
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.creer(produit))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_CREER_KO_LIBELLE_PARENT_BLANK);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * - sousTypeProduitDaoJPA n'a jamais été appelé ;
         * - produitDaoJPA n'a jamais été appelé ;
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>garantit que creer(parent ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_CREER_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent ;</li>
     * <li>n'appelle ni le DAO parent, ni le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(parent ID null) - jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testCreerParentIdNull() {

        /* ARRANGE :
         * prépare un objet métier avec un libellé valide,
         * mais avec un parent dont l'identifiant est null,
         * afin de vérifier le contrôle de persistance
         * effectué avant toute recherche DAO.
         */
        final SousTypeProduit parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        parent.setIdSousTypeProduit(null);

        final Produit produit = new Produit();
        produit.setProduit(CHEMISE_ML_HOMME);
        produit.setSousTypeProduit(parent);

        /* ACT - ASSERT :
         * vérifie que l'appel service.creer(...)
         * avec un objet métier dont le parent n'est pas persistant (ID null)
         * jette une ExceptionTechniqueGatewayNonPersistent
         * avec le message MSG_CREER_KO_PARENT_NON_PERSISTENT
         * suivi du libellé du parent
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.creer(produit))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(MSG_CREER_KO_PARENT_NON_PERSISTENT + VETEMENT_HOMME);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par le contrôle de persistance du parent avant tout appel des DAO.
         * - sousTypeProduitDaoJPA n'a jamais été appelé ;
         * - produitDaoJPA n'a jamais été appelé ;
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que creer(parent absent DAO) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_CREER_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent ;</li>
     * <li>appelle le DAO parent ;</li>
     * <li>n'appelle pas le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(parent absent DAO) - jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testCreerParentAbsent() {
    	
        /* ARRANGE :
         * prépare un objet métier avec un libellé valide,
         * et un parent persistant en apparence,
         * mais absent du DAO parent mocké avec Mockito.
         */
        final SousTypeProduitI parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit produit = new Produit();
        produit.setProduit(CHEMISE_ML_HOMME);
        produit.setSousTypeProduit(parent);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(1L)
         * sur le DAO parent mocké retourne Optional.empty().
         */
        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.empty());

        /* ACT - ASSERT :
         * vérifie que l'appel service.creer(...)
         * avec un objet métier dont le parent est absent du DAO
         * jette une ExceptionTechniqueGatewayNonPersistent
         * avec le message MSG_CREER_KO_PARENT_NON_PERSISTENT
         * suivi du libellé du parent
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.creer(produit))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(MSG_CREER_KO_PARENT_NON_PERSISTENT + VETEMENT_HOMME);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findById(1L) a été appelé une fois ;
         * - produitDaoJPA n'a jamais été appelé.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verifyNoInteractions(this.produitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que creer(DAO parent jette RuntimeException avec message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>contient le message technique d'origine ;</li>
     * <li>propage la cause technique d'origine ;</li>
     * <li>appelle le DAO parent ;</li>
     * <li>n'appelle pas le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(KO DAO parent message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testCreerParentDaoExceptionMessageNonNull() {

        /* ARRANGE :
         * prépare un objet métier avec un libellé valide,
         * et un parent persistant en apparence,
         * puis configure le DAO parent mocké avec Mockito
         * pour jeter une RuntimeException avec message non null.
         */
        final SousTypeProduitI parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit produit = new Produit();
        produit.setProduit(CHEMISE_ML_HOMME);
        produit.setSousTypeProduit(parent);

        final RuntimeException ex = new RuntimeException(BOOM);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(1L)
         * sur le DAO parent mocké jette l'Exception ex.
         */
        when(this.sousTypeProduitDaoJPA.findById(1L)).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.creer(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.creer(...),
         * - intercepte toute exception éventuellement levée,
         * - puis stocke cette exception dans la variable throwable
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.creer(produit));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel,
         * le message technique d'origine
         * et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable).hasMessageContaining(BOOM);
        assertThat(throwable.getCause()).isSameAs(ex);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findById(1L) a été appelé une fois ;
         * - produitDaoJPA n'a jamais été appelé.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que creer(DAO parent jette RuntimeException avec message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage la cause technique d'origine ;</li>
     * <li>appelle le DAO parent ;</li>
     * <li>n'appelle pas le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(KO DAO parent message null) - jette ExceptionTechniqueGateway")
    @Test
    public void testCreerParentDaoExceptionMessageNull() {

        /* ARRANGE :
         * prépare un objet métier avec un libellé valide,
         * et un parent persistant en apparence,
         * puis configure le DAO parent mocké avec Mockito
         * pour jeter une RuntimeException sans message.
         */
        final SousTypeProduitI parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit produit = new Produit();
        produit.setProduit(CHEMISE_ML_HOMME);
        produit.setSousTypeProduit(parent);

        final RuntimeException ex = new RuntimeException((String) null);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(1L)
         * sur le DAO parent mocké jette l'Exception ex.
         */
        when(this.sousTypeProduitDaoJPA.findById(1L)).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.creer(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.creer(...),
         * - intercepte toute exception éventuellement levée,
         * - puis stocke cette exception dans la variable throwable
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.creer(produit));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel
         * et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findById(1L) a été appelé une fois ;
         * - produitDaoJPA n'a jamais été appelé.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que creer(DAO.save(...) retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle le DAO parent ;</li>
     * <li>appelle le DAO objet métier pour sauvegarde.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(DAO.save(...) retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testCreerSaveRetourneNull() {

        /* ARRANGE :
         * prépare un objet métier avec un libellé valide,
         * et un parent persistant en apparence,
         * afin de provoquer ensuite un retour null
         * du DAO objet métier lors de la sauvegarde.
         */
        final SousTypeProduitI parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit produit = new Produit();
        produit.setProduit(CHEMISE_ML_HOMME);
        produit.setSousTypeProduit(parent);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(1L)
         * sur le DAO parent mocké retourne le parent JPA persistant.
         */
        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(this.fabriquerParentJPAPersistant(VETEMENT_HOMME)));

        /* Condition du Mock :
         * L'appel produitDaoJPA.save(...)
         * sur le DAO objet métier mocké retourne null.
         */
        when(this.produitDaoJPA.save(any(ProduitJPA.class))).thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que l'appel service.creer(...)
         * avec un stockage retournant null à la sauvegarde
         * jette une ExceptionTechniqueGateway
         * avec le message MSG_ERREUR_TECH_KO_STOCKAGE
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.creer(produit))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findById(1L) a été appelé une fois ;
         * - produitDaoJPA.save(...) a été appelé une fois.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).save(any(ProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que creer(DAO save jette RuntimeException avec message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>contient le message technique d'origine ;</li>
     * <li>propage la cause technique d'origine ;</li>
     * <li>appelle le DAO parent ;</li>
     * <li>appelle le DAO objet métier pour sauvegarde.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(KO DAO save message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testCreerDaoSaveExceptionMessageNonNull() {

        /* ARRANGE :
         * prépare un objet métier avec un libellé valide,
         * et un parent persistant en apparence,
         * puis configure le DAO objet métier mocké avec Mockito
         * pour jeter une RuntimeException avec message non null
         * lors de la sauvegarde.
         */
        final SousTypeProduitI parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit produit = new Produit();
        produit.setProduit(CHEMISE_ML_HOMME);
        produit.setSousTypeProduit(parent);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(1L)
         * sur le DAO parent mocké retourne le parent JPA persistant.
         */
        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(this.fabriquerParentJPAPersistant(VETEMENT_HOMME)));

        final RuntimeException ex = new RuntimeException(BOOM);

        /* Condition du Mock :
         * L'appel produitDaoJPA.save(...)
         * sur le DAO objet métier mocké jette l'Exception ex.
         */
        when(this.produitDaoJPA.save(any(ProduitJPA.class))).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.creer(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.creer(...),
         * - intercepte toute exception éventuellement levée,
         * - puis stocke cette exception dans la variable throwable
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.creer(produit));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel,
         * le message technique d'origine
         * et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable).hasMessageContaining(BOOM);
        assertThat(throwable.getCause()).isSameAs(ex);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findById(1L) a été appelé une fois ;
         * - produitDaoJPA.save(...) a été appelé une fois.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).save(any(ProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que creer(DAO save jette RuntimeException avec message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage la cause technique d'origine ;</li>
     * <li>appelle le DAO parent ;</li>
     * <li>appelle le DAO objet métier pour sauvegarde.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(KO DAO save message null) - jette ExceptionTechniqueGateway")
    @Test
    public void testCreerDaoSaveExceptionMessageNull() {

        /* ARRANGE :
         * prépare un objet métier avec un libellé valide,
         * et un parent persistant en apparence,
         * puis configure le DAO objet métier mocké avec Mockito
         * pour jeter une RuntimeException sans message
         * lors de la sauvegarde.
         */
        final SousTypeProduitI parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit produit = new Produit();
        produit.setProduit(CHEMISE_ML_HOMME);
        produit.setSousTypeProduit(parent);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(1L)
         * sur le DAO parent mocké retourne le parent JPA persistant.
         */
        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(this.fabriquerParentJPAPersistant(VETEMENT_HOMME)));

        final RuntimeException ex = new RuntimeException((String) null);

        /* Condition du Mock :
         * L'appel produitDaoJPA.save(...)
         * sur le DAO objet métier mocké jette l'Exception ex.
         */
        when(this.produitDaoJPA.save(any(ProduitJPA.class))).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.creer(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.creer(...),
         * - intercepte toute exception éventuellement levée,
         * - puis stocke cette exception dans la variable throwable
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.creer(produit));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel
         * et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findById(1L) a été appelé une fois ;
         * - produitDaoJPA.save(...) a été appelé une fois.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).save(any(ProduitJPA.class));

    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>garantit que si le stockage refuse creer(...) pour cause de doublon fonctionnel :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO parent ;</li>
     * <li>appelle le DAO objet métier pour sauvegarde.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(doublon) - jette ExceptionTechniqueGateway")
    @Test
    public void testCreerDoublon() {

        /* ARRANGE :
         * prépare un objet métier valide,
         * avec un parent persistant en apparence.
         * Le doublon n'est pas simulé au niveau métier,
         * mais au niveau du stockage au moment du save(...).
         */
        final SousTypeProduitI parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit produit = new Produit();
        produit.setProduit(CHEMISE_ML_HOMME);
        produit.setSousTypeProduit(parent);

        final SousTypeProduitJPA parentJPA =
                this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(1L)
         * sur le DAO parent mocké retourne le parent JPA persistant,
         * afin d'atteindre réellement la tentative de sauvegarde
         * de l'objet métier.
         */
        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));

        /* Prépare l'exception technique d'intégrité levée par le stockage :
         * elle représente ici un refus de création
         * pour cause de doublon fonctionnel sur le couple
         * produit / sous-type produit.
         */
        final String messageTechnique = "contrainte d'unicité violée";
        final DataIntegrityViolationException causeDao =
                new DataIntegrityViolationException(messageTechnique);

        /* Condition du Mock :
         * L'appel produitDaoJPA.save(...)
         * sur le DAO objet métier mocké jette l'Exception causeDao.
         */
        when(this.produitDaoJPA.save(any(ProduitJPA.class))).thenThrow(causeDao);

        /* ACT :
         * sollicite la méthode service.creer(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.creer(...),
         * - intercepte toute exception éventuellement levée,
         * - puis stocke cette exception dans la variable throwable
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.creer(produit));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel,
         * le message technique d'origine
         * et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable).hasMessageContaining(messageTechnique);
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findById(1L) a été appelé une fois ;
         * - produitDaoJPA.save(...) a été appelé une fois.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).save(any(ProduitJPA.class));

    } // __________________________________________________________________
    

    
    /**
     * <div>
     * <p>garantit que creer(parent avec caractères spéciaux) :</p>
     * <ul>
     * <li>retourne un objet métier non null ;</li>
     * <li>retourne le libellé objet métier attendu ;</li>
     * <li>retourne un parent non null ;</li>
     * <li>retourne le libellé parent attendu ;</li>
     * <li>appelle le DAO parent ;</li>
     * <li>appelle le DAO objet métier pour sauvegarde.</li>
     * </ul>
     * </div>
     * @throws Exception 
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(parent avec caractères spéciaux) - retourne un objet métier cohérent")
    @Test
    public void testCreerParentCaracteresSpeciaux() throws Exception {

        /* ARRANGE :
         * prépare un scénario nominal
         * avec un parent contenant des caractères spéciaux,
         * afin de vérifier que le service ne déforme pas
         * les libellés métier.
         */
        final String libelleParentSpecial =
                "vêtement/homme_été-2026";

        final SousTypeProduitI parent =
                this.fabriquerParentMetierPersistant(libelleParentSpecial);

        final Produit produit = new Produit();
        produit.setProduit(CHEMISE_ML_HOMME);
        produit.setSousTypeProduit(parent);

        final SousTypeProduitJPA parentJPA =
                this.fabriquerParentJPAPersistant(libelleParentSpecial);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(1L)
         * sur le DAO parent mocké retourne le parent JPA persistant.
         */
        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));

        final ProduitJPA sauvegardeJPA =
                this.fabriquerProduitJPA(CHEMISE_ML_HOMME, libelleParentSpecial);
        sauvegardeJPA.setIdProduit(1L);

        /* Condition du Mock :
         * L'appel produitDaoJPA.save(...)
         * sur le DAO objet métier mocké retourne un ProduitJPA.
         */
        when(this.produitDaoJPA.save(any(ProduitJPA.class))).thenReturn(sauvegardeJPA);

        /* ACT :
         * sollicite la méthode creer(...)
         * dans un scénario nominal complet.
         */
        final Produit retour = this.service.creer(produit);

        /* ASSERT :
         * vérifie que l'objet métier retourné
         * est cohérent avec les données sauvegardées.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getIdProduit()).isEqualTo(1L);
        assertThat(retour.getProduit()).isEqualTo(CHEMISE_ML_HOMME);
        assertThat(retour.getSousTypeProduit()).isNotNull();
        assertThat(retour.getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(libelleParentSpecial);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findById(1L) a été appelé une fois ;
         * - produitDaoJPA.save(...) a été appelé une fois.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).save(any(ProduitJPA.class));

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>garantit que creer(nominal) :</p>
     * <ul>
     * <li>retourne un objet métier non null ;</li>
     * <li>retourne l'identifiant objet métier attendu ;</li>
     * <li>retourne le libellé objet métier attendu ;</li>
     * <li>retourne un parent non null ;</li>
     * <li>retourne le libellé parent attendu ;</li>
     * <li>appelle le DAO parent ;</li>
     * <li>appelle le DAO objet métier pour sauvegarde.</li>
     * </ul>
     * </div>
     * @throws Exception 
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(nominal) - retourne un objet métier cohérent")
    @Test
    public void testCreerNominal() throws Exception {

        /* ARRANGE :
         * prépare un scénario nominal complet
         * avec un parent persistant et un save(...) réussi.
         */
        final SousTypeProduitI parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit produit = new Produit();
        produit.setProduit(CHEMISE_ML_HOMME);
        produit.setSousTypeProduit(parent);

        final SousTypeProduitJPA parentJPA =
                this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(1L)
         * sur le DAO parent mocké retourne le parent JPA persistant.
         */
        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));

        final ProduitJPA sauvegardeJPA =
                this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        sauvegardeJPA.setIdProduit(1L);

        /* Condition du Mock :
         * L'appel produitDaoJPA.save(...)
         * sur le DAO objet métier mocké retourne un ProduitJPA.
         */
        when(this.produitDaoJPA.save(any(ProduitJPA.class))).thenReturn(sauvegardeJPA);

        /* ACT :
         * sollicite la méthode creer(...)
         * dans un scénario nominal complet.
         */
        final Produit retour = this.service.creer(produit);

        /* ASSERT :
         * vérifie que l'objet métier retourné
         * est cohérent avec les données sauvegardées.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getIdProduit()).isEqualTo(1L);
        assertThat(retour.getProduit()).isEqualTo(CHEMISE_ML_HOMME);
        assertThat(retour.getSousTypeProduit()).isNotNull();
        assertThat(retour.getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findById(1L) a été appelé une fois ;
         * - produitDaoJPA.save(...) a été appelé une fois.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).save(any(ProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    // ======================== RechercherTous ============================



    /**
     * <div>
     * <p>garantit que si DAO.findAll() retourne null :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle le DAO objet métier une fois via {@code findAll()} ;</li>
     * <li>n'appelle pas le DAO parent.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(DAO.findAll() retourne null) - jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testRechercherTousDAORetourneNull() {

        /* ARRANGE :
         * configure le DAO objet métier mocké avec Mockito
         * pour que DAO.findAll() retourne null au lieu d'une liste.
         */
        when(this.produitDaoJPA.findAll()).thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que :
         * this.service.rechercherTous() avec DAO.findAll() retourne null
         * - jette une ExceptionTechniqueGateway
         * - avec un message MSG_ERREUR_TECH_KO_STOCKAGE
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.rechercherTous())
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - produitDaoJPA.findAll() a été appelé une fois ;
         * - le DAO parent n'a jamais été appelé.
         */
        verify(this.produitDaoJPA).findAll();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que si DAO.findAll() retourne une liste vide :</p>
     * <ul>
     * <li>retourne une {@link List} non null ;</li>
     * <li>retourne une liste vide ;</li>
     * <li>ne jette aucune exception ;</li>
     * <li>appelle le DAO objet métier une fois via {@code findAll()} ;</li>
     * <li>n'appelle pas le DAO parent.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(DAO.findAll() retourne liste vide) : retourne une liste vide non null")
    @Test
    public void testRechercherTousDAORetourneVide() throws Exception {

        /* ARRANGE :
         * configure le DAO objet métier mocké avec Mockito
         * pour que DAO.findAll() retourne une liste vide.
         */
        when(this.produitDaoJPA.findAll()).thenReturn(Collections.emptyList());

        /* ACT :
         * appelle this.service.rechercherTous()
         * dans le scénario où le stockage est vide.
         */
        final List<Produit> retour = this.service.rechercherTous();

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * une liste non nulle, mais vide.
         */
        assertThat(retour).isNotNull().isEmpty();

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - produitDaoJPA.findAll() a été appelé une fois ;
         * - le DAO parent n'a jamais été appelé.
         */
        verify(this.produitDaoJPA).findAll();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que rechercherTous(KO DAO message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO objet métier une fois via {@code findAll()} ;</li>
     * <li>n'appelle pas le DAO parent.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(KO DAO message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testRechercherTousDaoExceptionMessageNonNull() {

        /* ARRANGE :
         * configure le DAO objet métier mocké avec Mockito
         * pour que DAO.findAll()
         * jette une RuntimeException avec message non null.
         */
        final RuntimeException ex = new RuntimeException(BOOM);
        when(this.produitDaoJPA.findAll()).thenThrow(ex);

        /* ACT :
         * sollicite la méthode rechercherTous()
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.rechercherTous(),
         * - intercepte toute exception éventuellement levée,
         * - puis stocke cette exception dans la variable throwable
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.rechercherTous());

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel,
         * le message technique d'origine
         * et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable).hasMessageContaining(BOOM);
        assertThat(throwable.getCause()).isSameAs(ex);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - produitDaoJPA.findAll() a été appelé une fois ;
         * - le DAO parent n'a jamais été appelé.
         */
        verify(this.produitDaoJPA).findAll();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    
      
    /**
     * <div>
     * <p>garantit que si DAO.findAll() jette une exception technique 
     * avec message null :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non null dérivé de l'exception technique ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO objet métier une fois via {@code findAll()} ;</li>
     * <li>n'appelle pas le DAO parent.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(KO DAO message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testRechercherTousDaoExceptionMessageNull() {

        /* ARRANGE :
         * configure le DAO objet métier mocké avec Mockito
         * pour que DAO.findAll()
         * jette une RuntimeException sans message.
         */
        final RuntimeException ex = new RuntimeException((String) null);
        when(this.produitDaoJPA.findAll()).thenThrow(ex);

        /* ACT :
         * sollicite la méthode rechercherTous()
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.rechercherTous(),
         * - intercepte toute exception éventuellement levée,
         * - puis stocke cette exception dans la variable throwable
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.rechercherTous());

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel,
         * le message sûr non null
         * et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable).hasMessageContaining(RuntimeException.class.getName());
        assertThat(throwable.getCause()).isSameAs(ex);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - produitDaoJPA.findAll() a été appelé une fois ;
         * - le DAO parent n'a jamais été appelé.
         */
        verify(this.produitDaoJPA).findAll();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que si DAO.findAll() retourne une liste
     * contenant des nulls, des doublons fonctionnels
     * et deux couples métier distincts partageant le même libellé produit :</p>
     * <ul>
     * <li>retourne une {@link List} non null ;</li>
     * <li>filtre les éléments null ;</li>
     * <li>dédoublonne les résultats au sens métier
     * sur le couple produit / sous-type produit ;</li>
     * <li>conserve deux produits portant le même libellé
     * lorsqu'ils appartiennent à deux parents différents ;</li>
     * <li>trie les résultats par parent puis par libellé produit ;</li>
     * <li>appelle le DAO objet métier une fois via {@code findAll()} ;</li>
     * <li>n'appelle pas le DAO parent.</li>
     * </ul>
     * <p>Ce test prouve explicitement la règle contractuelle
     * d'unicité métier sur le couple produit / sous-type produit.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(nulls + doublons de couple) : filtre, dédoublonne et trie")
    @Test
    public void testRechercherTousTriDedoublonnage() throws Exception {

        /* ARRANGE :
         * prépare une liste renvoyée par le DAO objet métier mocké
         * contenant :
         * - des valeurs null à filtrer ;
         * - un doublon fonctionnel sur le couple
         *   produit / sous-type produit ;
         * - deux produits ayant le même libellé,
         *   mais des parents différents, donc non doublons ;
         * - un ordre initial volontairement non trié.
         */
        final ProduitJPA chemiseHomme =
                this.fabriquerProduitJPA(CHEMISE, VETEMENT_HOMME);
        chemiseHomme.setIdProduit(20L);

        final ProduitJPA doublonChemiseHomme =
                this.fabriquerProduitJPA(CHEMISE, VETEMENT_HOMME);
        doublonChemiseHomme.setIdProduit(999L);

        final ProduitJPA chemiseFemme =
                this.fabriquerProduitJPA(CHEMISE, VETEMENT_FEMME);
        chemiseFemme.setIdProduit(10L);

        final ProduitJPA sweatFemme =
                this.fabriquerProduitJPA(SWEAT_HOMME, VETEMENT_FEMME);
        sweatFemme.setIdProduit(11L);

        final ProduitJPA chemiseMcHomme =
                this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        chemiseMcHomme.setIdProduit(21L);

        final List<ProduitJPA> entities = new ArrayList<ProduitJPA>();
        entities.add(chemiseMcHomme);
        entities.add(null);
        entities.add(doublonChemiseHomme);
        entities.add(sweatFemme);
        entities.add(chemiseHomme);
        entities.add(null);
        entities.add(chemiseFemme);

        /* Condition du Mock :
         * L'appel produitDaoJPA.findAll()
         * sur le DAO objet métier mocké retourne la liste entities.
         */
        when(this.produitDaoJPA.findAll()).thenReturn(entities);

        /* ACT :
         * sollicite la méthode service.rechercherTous()
         * dans un scénario contenant nulls, doublons et tri métier.
         */
        final List<Produit> retour = this.service.rechercherTous();

        /* ASSERT :
         * vérifie d'abord que la méthode retourne
         * une liste exploitable, filtrée et dédoublonnée.
         */
        assertThat(retour).isNotNull();
        assertThat(retour).doesNotContainNull();
        assertThat(retour).hasSize(4);

        /* Vérifie ensuite l'ordre final attendu :
         * - parent vêtement pour femme / produit chemise ;
         * - parent vêtement pour femme / produit sweatshirt pour homme ;
         * - parent vêtement pour homme / produit chemise ;
         * - parent vêtement pour homme / produit chemise à manches courtes pour homme.
         */
        assertThat(retour.get(0).getSousTypeProduit()).isNotNull();
        assertThat(retour.get(0).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_FEMME);
        assertThat(retour.get(0).getProduit()).isEqualTo(CHEMISE);

        assertThat(retour.get(1).getSousTypeProduit()).isNotNull();
        assertThat(retour.get(1).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_FEMME);
        assertThat(retour.get(1).getProduit()).isEqualTo(SWEAT_HOMME);

        assertThat(retour.get(2).getSousTypeProduit()).isNotNull();
        assertThat(retour.get(2).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);
        assertThat(retour.get(2).getProduit()).isEqualTo(CHEMISE);

        assertThat(retour.get(3).getSousTypeProduit()).isNotNull();
        assertThat(retour.get(3).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);
        assertThat(retour.get(3).getProduit()).isEqualTo(CHEMISE_MC_HOMME);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - produitDaoJPA.findAll() a été appelé une fois ;
         * - le DAO parent n'a jamais été appelé.
         */
        verify(this.produitDaoJPA).findAll();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que rechercherTous(nominal) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>filtre les valeurs null ;</li>
     * <li>dédoublonne les doublons fonctionnels ;</li>
     * <li>retourne une liste triée par parent puis par libellé produit ;</li>
     * <li>conserve un parent non null sur les objets métier retournés ;</li>
     * <li>appelle le DAO objet métier une fois via {@code findAll()} ;</li>
     * <li>n'appelle pas le DAO parent.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(nominal) - filtre, trie et dédoublonne")
    @Test
    public void testRechercherTousNominal() throws Exception {

        /* ARRANGE :
         * prépare une liste renvoyée par le DAO objet métier mocké
         * contenant :
         * - des valeurs null à filtrer ;
         * - deux doublons fonctionnels sur le même libellé produit
         *   et le même parent ;
         * - un ordre initial non trié.
         *
         * Ce scénario permet de vérifier en une seule fois
         * le filtrage, le tri, le dédoublonnage
         * et la conservation du parent métier.
         */
        final ProduitJPA femmeChemise =
                this.fabriquerProduitJPA(CHEMISE, VETEMENT_FEMME);
        femmeChemise.setIdProduit(10L);

        final ProduitJPA femmeSweat =
                this.fabriquerProduitJPA(SWEAT_HOMME, VETEMENT_FEMME);
        femmeSweat.setIdProduit(11L);

        final ProduitJPA hommeChemiseMc =
                this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        hommeChemiseMc.setIdProduit(20L);

        final ProduitJPA hommeChemiseMl =
                this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        hommeChemiseMl.setIdProduit(21L);

        final ProduitJPA doublonHommeChemiseMc =
                this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        doublonHommeChemiseMc.setIdProduit(999L);

        final List<ProduitJPA> entities = new ArrayList<ProduitJPA>();
        entities.add(null);
        entities.add(hommeChemiseMl);
        entities.add(doublonHommeChemiseMc);
        entities.add(femmeSweat);
        entities.add(hommeChemiseMc);
        entities.add(null);
        entities.add(femmeChemise);

        /* Condition du Mock :
         * L'appel produitDaoJPA.findAll()
         * sur le DAO objet métier mocké retourne la liste entities.
         */
        when(this.produitDaoJPA.findAll()).thenReturn(entities);

        /* ACT :
         * sollicite la méthode service.rechercherTous()
         * dans un scénario nominal complet.
         */
        final List<Produit> retour = this.service.rechercherTous();

        /* ASSERT :
         * vérifie d'abord que la méthode retourne
         * une liste exploitable, filtrée et dédoublonnée.
         */
        assertThat(retour).isNotNull();
        assertThat(retour).doesNotContainNull();
        assertThat(retour).hasSize(4);

        /* Vérifie ensuite l'ordre final attendu :
         * - parent vêtement pour femme / produit chemise ;
         * - parent vêtement pour femme / produit sweatshirt pour homme ;
         * - parent vêtement pour homme / produit chemise à manches courtes pour homme ;
         * - parent vêtement pour homme / produit chemise à manches longues pour homme.
         */
        /* 
         * La lambda ci-dessous est appliquée à chaque Produit p de la liste retour.
         *
         * Pour chaque Produit :
         * - p représente l'objet métier Produit courant ;
         * - p.getSousTypeProduit() récupère son parent métier SousTypeProduit ;
         * - p.getSousTypeProduit().getSousTypeProduit() récupère le libellé
         *   de ce parent.
         *
         * L'assertion extrait donc la liste des libellés des parents
         * des produits retournés, dans leur ordre réel de retour.
         */
        assertThat(retour)
            .extracting(p -> p.getSousTypeProduit().getSousTypeProduit())
            .containsExactly(
                    VETEMENT_FEMME,
                    VETEMENT_FEMME,
                    VETEMENT_HOMME,
                    VETEMENT_HOMME);

        /*
         * Produit::getProduit est une référence de méthode.
         * Produit::getProduit est équivalent à p -> p.getProduit()
         *
         * Elle est appliquée à chaque Produit de la liste retour.
         * Pour chaque Produit courant, elle appelle getProduit()
         * afin d'extraire uniquement le libellé du produit.
         *
         * L'assertion compare donc la liste des libellés produits
         * retournés, dans leur ordre réel, avec l'ordre attendu.
         */
        assertThat(retour)
            .extracting(Produit::getProduit)
            .containsExactly(
                    CHEMISE,
                    SWEAT_HOMME,
                    CHEMISE_MC_HOMME,
                    CHEMISE_ML_HOMME);

        /* Vérifie enfin que les objets métier retournés
         * conservent un parent non null.
         */
        assertThat(retour)
            .extracting(Produit::getSousTypeProduit)
            .doesNotContainNull();

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - produitDaoJPA.findAll() a été appelé une fois ;
         * - le DAO parent n'a jamais été appelé.
         */
        verify(this.produitDaoJPA).findAll();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    

            
    // ================== rechercherTousParPage ===========================
    
    
    
    /**
     * <div>
     * <p>garantit que si 
     * {@code rechercherTousParPage(RequetePage null)} est appelé :</p>
     * <ul>
     * <li>le service ne rejette pas l'appel ;</li>
     * <li>il remplace la requête absente par une pagination par défaut ;</li>
     * <li>il transmet cette pagination par défaut au DAO ;</li>
     * <li>il retourne un {@link ResultatPage} cohérent avec la page renvoyée par le DAO.</li>
     * </ul>
     * <p>Ce test ne prouve donc pas seulement que l'appel fonctionne :
     * il prouve aussi quelle pagination concrète est réellement envoyée au DAO
     * quand la requête d'entrée vaut {@code null}.</p>
     * </div>
     */
	@Tag(TAG_PAGINATION)
	@DisplayName("rechercherTousParPage(RequetePage null) : garantit l'usage de la pagination par défaut sans Exception")
	@Test
	public void testRechercherTousParPageNull() throws Exception {

        /* ARRANGE :
         * prépare un contenu JPA simple,
         * une Page Spring cohérente avec la pagination par défaut,
         * et un captor Mockito pour récupérer le Pageable
         * réellement transmis par le service au DAO.
         */
        final List<ProduitJPA> contenuJPA = Arrays.asList(
                this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME),
                this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME));

        final Page<ProduitJPA> page
            = new PageImpl<ProduitJPA>(
                    contenuJPA,
                    PageRequest.of(
                            RequetePage.PAGE_DEFAUT,
                            RequetePage.TAILLE_DEFAUT),
                    2L);

        /* 
         * Crée un ArgumentCaptor Mockito capable de capturer
         * l'argument de type Pageable réellement transmis au DAO.
         *
         * Ici, le test ne veut pas seulement vérifier que
         * produitDaoJPA.findAll(...) a été appelé.
         * Il veut aussi inspecter le Pageable construit par le service :
         * - numéro de page ;
         * - taille de page ;
         * - présence ou absence de tri.
         *
         * ArgumentCaptor.forClass(Pageable.class) indique donc à Mockito
         * quel type d'argument devra être capturé lors du verify(...).
         *
         * Le captor est indispensable ici :
         * sans lui, on pourrait seulement vérifier que findAll(...) a été appelé ;
         * avec lui, on peut contrôler concrètement
         * le numéro de page, la taille et l'absence de tri.
         */
        final ArgumentCaptor<Pageable> captor
            = ArgumentCaptor.forClass(Pageable.class);

        /* 
         * Configuration du Mock :
         * Simule un DAO qui renvoie une page valide
         * quel que soit le Pageable reçu.
         *
         * Le but du test n'est pas de vérifier le DAO,
         * mais de prouver comment le service compense
         * la requête null avant de déléguer au stockage.
         */
        when(this.produitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * appelle le service avec une requête null.
         *
         * Le contrat impose alors :
         * - aucune Exception ;
         * - utilisation d'une pagination par défaut ;
         * - délégation au DAO avec un Pageable cohérent.
         */
        final ResultatPage<Produit> resultat
            = this.service.rechercherTousParPage(null);

        /* ASSERT :
         * garantit d'abord que le service a bien appelé le DAO,
         * puis permet d'inspecter le Pageable réellement transmis.
         */
        verify(this.produitDaoJPA).findAll(captor.capture());

        final Pageable pageable = captor.getValue();

        /* Garantit que lorsque l'entrée vaut null,
         * le service fabrique bien une pagination par défaut
         * avant l'appel DAO.
         */
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(pageable.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(pageable.getSort().isSorted()).isFalse();

        /* Garantit que le service restitue ensuite
         * un ResultatPage cohérent avec la page DAO reçue :
         * même pagination, bon total, bon contenu métier.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(resultat.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(resultat.getTotalElements()).isEqualTo(2L);
        assertThat(resultat.getContent())
            .extracting(Produit::getProduit)
            .containsExactly(CHEMISE_ML_HOMME, CHEMISE_MC_HOMME);

        /* Garantit que les objets métier retournés
         * conservent un parent non null et cohérent.
         */
        assertThat(resultat.getContent().get(0).getSousTypeProduit()).isNotNull();
        assertThat(resultat.getContent().get(0).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        assertThat(resultat.getContent().get(1).getSousTypeProduit()).isNotNull();
        assertThat(resultat.getContent().get(1).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        /* 
         * Vérifie que le DAO Parent mocké n'a pas été appelé.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
	    
	} // __________________________________________________________________
	


	/**
     * <div>
     * <p>garantit que si le DAO renvoie {@code null} au lieu d'une page Spring :</p>
     * <ul>
     * <li>le service ne considère pas ce retour comme un résultat vide valide ;</li>
     * <li>il interprète ce {@code null} comme une anomalie technique de stockage ;</li>
     * <li>il jette alors une {@link ExceptionTechniqueGateway}
     * avec le message contractuel KO_STOCKAGE.</li>
     * </ul>
     * <p>Ce test vérifie donc un défaut du stockage,
     * et non un défaut du paramètre d'entrée :
     * la requête transmise au service est volontairement valide,
     * afin de prouver que l'échec vient bien du DAO.</p>
     * </div>
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(DAO retourne null) : garantit ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testRechercherTousParPageDAORetourneNull() {

        /* ARRANGE :
         * Configuration du Mock :
         * prépare une situation anormale côté DAO :
         * au lieu de renvoyer une Page Spring,
         * le stockage renvoie null.
         *
         * Ce scénario ne correspond pas à un résultat métier vide ;
         * il correspond à un dysfonctionnement technique
         * que le service doit refuser explicitement.
         */
        when(this.produitDaoJPA.findAll(any(Pageable.class))).thenReturn(null);

        /* ACT - ASSERT :
         * appelle le service avec une requête valide
         * pour isoler précisément le cas testé :
         * l'erreur doit provenir du retour DAO null,
         * et non d'un problème de paramètre d'entrée.
         *
         * Le contrat impose alors au service de jeter
         * une ExceptionTechniqueGateway
         * avec le message ERREUR_TECHNIQUE_KO_STOCKAGE.
         */
        assertThatThrownBy(() -> this.service.rechercherTousParPage(new RequetePage()))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* ASSERT :
         * garantit que le service a bien interrogé une fois 
         * le DAO objet métier via findAll(...).
         *
         * Le but de ce test n'est pas de contrôler le détail du Pageable,
         * mais de prouver la réaction contractuelle du service
         * quand le DAO répond null.
         */
        verify(this.produitDaoJPA, times(1)).findAll(any(Pageable.class));

        /* 
         * Vérifie que le DAO Parent mocké n'a pas été appelé.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que si le DAO renvoie bien une {@link Page},
     * mais que le contenu de cette page vaut {@code null} :</p>
     * <ul>
     * <li>le service ne considère pas cette page comme exploitable ;</li>
     * <li>il interprète ce {@code null} interne comme une anomalie technique de stockage ;</li>
     * <li>il jette alors une {@link ExceptionTechniqueGateway}
     * avec le message contractuel KO_STOCKAGE.</li>
     * </ul>
     * <p>Ce test distingue donc clairement deux niveaux d'anomalie :</p>
     * <ul>
     * <li>dans un autre test, le DAO peut lui-même renvoyer {@code null} ;</li>
     * <li>ici, le DAO renvoie un objet {@link Page}, mais cet objet est incohérent
     * car son contenu est absent.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(retourne contenu null) : garantit ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testRechercherTousParPageContenuNull() {

        /* ARRANGE :
         * Configuration du Mock :
         * fabrique une fausse Page Spring avec Mockito.
         *
         * Le but n'est pas de construire une vraie page valide,
         * mais de simuler un retour DAO techniquement incohérent :
         * l'objet Page existe, mais son contenu interne vaut null.
         *
         * Cela permet de prouver que le service contrôle aussi
         * la cohérence interne de la page reçue,
         * et pas seulement l'existence de l'objet Page lui-même.
         */
        final Page<ProduitJPA> pageMock
            = org.mockito.Mockito.mock(Page.class);
        when(pageMock.getContent()).thenReturn(null);

        /* Simule ensuite un DAO qui renvoie cette page incohérente
         * lorsque le service l'appelle en pagination.
         *
         * Le scénario préparé est donc :
         * DAO.findAll(...) ne renvoie pas null,
         * mais renvoie une Page inutilisable car son contenu est null.
         */
        when(this.produitDaoJPA.findAll(any(Pageable.class))).thenReturn(pageMock);

        /* ACT - ASSERT :
         * appelle le service avec une requête valide
         * pour isoler précisément l'anomalie testée.
         *
         * Le contrat impose alors qu'une page dont le contenu vaut null
         * ne soit pas acceptée comme une page vide normale,
         * mais traitée comme un défaut technique de stockage.
         */
        assertThatThrownBy(() -> this.service.rechercherTousParPage(new RequetePage()))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* ASSERT :
         * garantit que le service a bien interrogé une fois 
         * le DAO objet métier via findAll(...).
         *
         * Le cœur du test n'est pas ici le détail du Pageable,
         * mais la réaction contractuelle du service
         * face à une Page existante mais incohérente (contenu null).
         */
        verify(this.produitDaoJPA, times(1)).findAll(any(Pageable.class));

        /* 
         * Vérifie que le DAO Parent mocké n'a pas été appelé.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que si une erreur technique survient pendant l'accès au DAO :</p>
     * <ul>
     * <li>le service ne laisse pas remonter l'Exception brute du stockage ;</li>
     * <li>il la transforme en {@link ExceptionTechniqueGateway} ;</li>
     * <li>il construit un message technique conforme au contrat ;</li>
     * <li>il y ajoute un message sûr dérivé de l'Exception cause ;</li>
     * <li>il conserve l'Exception technique initiale comme cause.</li>
     * </ul>
     * <p>Ce test prouve donc la réaction contractuelle du service
     * face à une panne technique du stockage,
     * et non un simple échec fonctionnel métier.</p>
     * </div>
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(KO DAO) : garantit ExceptionTechniqueGateway avec message sûr et cause propagée")
    @Test
    public void testRechercherTousParPageDAOExceptionMessageNonNull() {

        /* ARRANGE :
         * configure le DAO objet métier mocké avec Mockito
         * pour que DAO.findAll(Pageable)
         * jette une RuntimeException avec message non null.
         */
        final RuntimeException causeDao = new RuntimeException(BOOM);

        when(this.produitDaoJPA.findAll(any(Pageable.class))).thenThrow(causeDao);

        /* ACT :
         * sollicite la méthode rechercherTousParPage(...)
         * dans les conditions imposées par le mock.
         *
         * Stocke l'Exception levée afin de vérifier :
         * - son type ;
         * - son message ;
         * - sa cause.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.rechercherTousParPage(new RequetePage()));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel,
         * le message technique d'origine
         * et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable).hasMessageContaining(BOOM);
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /*
         * Vérifie que le DAO objet métier mocké
         * a été appelé une seule fois via findAll(Pageable).
         */
        verify(this.produitDaoJPA, times(1)).findAll(any(Pageable.class));

        /* 
         * Vérifie que le DAO Parent mocké n'a pas été appelé.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    
    
	/**
	 * <div>
	 * <p>garantit que si une erreur technique survient pendant l'accès au DAO
	 * et que cette erreur ne porte aucun message :</p>
	 * <ul>
	 * <li>le service ne laisse pas remonter l'Exception brute du stockage ;</li>
	 * <li>il la transforme en {@link ExceptionTechniqueGateway} ;</li>
	 * <li>il construit un message technique conforme au contrat ;</li>
	 * <li>il y ajoute un message sûr non nul dérivé de l'Exception cause ;</li>
	 * <li>il conserve l'Exception technique initiale comme cause.</li>
	 * </ul>
	 * <p>Ce test complète le cas KO DAO avec message non nul :</p>
	 * <ul>
	 * <li>l'autre test prouve la conservation d'un message existant ;</li>
	 * <li>celui-ci prouve le comportement de sécurisation
	 * quand le message d'origine vaut {@code null}.</li>
	 * </ul>
	 * </div>
	 */
	@Tag(TAG_PAGINATION)
	@DisplayName("rechercherTousParPage(KO DAO message null) : garantit ExceptionTechniqueGateway avec message sûr non nul")
	@Test
	public void testRechercherTousParPageDAOExceptionMessageNull() {
	
	    /* ARRANGE :
	     * configure le DAO objet métier mocké avec Mockito
	     * pour que DAO.findAll(Pageable)
	     * jette une RuntimeException sans message.
	     */
	    final RuntimeException causeDao = new RuntimeException((String) null);
	
	    when(this.produitDaoJPA.findAll(any(Pageable.class))).thenThrow(causeDao);
	
	    /* ACT :
	     * sollicite la méthode rechercherTousParPage(...)
	     * dans les conditions imposées par le mock.
	     *
	     * Stocke l'Exception levée afin de vérifier :
	     * - son type ;
	     * - son message sûr non null ;
	     * - sa cause.
	     */
	    final Throwable throwable =
	            Assertions.catchThrowable(
	                    () -> this.service.rechercherTousParPage(new RequetePage()));
	
	    /* ASSERT :
	     * vérifie l'exception technique observable,
	     * son préfixe contractuel,
	     * le message sûr non null
	     * et la cause propagée.
	     */
	    assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
	    assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
	    assertThat(throwable).hasMessageContaining(RuntimeException.class.getName());
	    assertThat(throwable.getCause()).isSameAs(causeDao);
	
	    /*
	     * Vérifie que le DAO objet métier mocké
	     * a été appelé une seule fois via findAll(Pageable).
	     */
	    verify(this.produitDaoJPA, times(1)).findAll(any(Pageable.class));
	
	    /* 
	     * Vérifie que le DAO Parent mocké n'a pas été appelé.
	     */
	    verifyNoInteractions(this.sousTypeProduitDaoJPA);
	
	} // __________________________________________________________________
	
	
	
	/**
     * <div>
     * <p>garantit que si l'appelant construit une {@link RequetePage}
     * avec {@code pageSize == 0} :</p>
     * <ul>
     * <li>la taille invalide n'est pas conservée telle quelle ;</li>
     * <li>la {@link RequetePage} normalise en amont cette taille
     * vers {@link RequetePage#TAILLE_DEFAUT} ;</li>
     * <li>le service transmet donc au DAO un {@link Pageable}
     * cohérent avec cette taille par défaut ;</li>
     * <li>il retourne enfin un {@link ResultatPage} cohérent
     * avec la page renvoyée par le DAO.</li>
     * </ul>
     * <p>Ce test verrouille donc le comportement réel observé
     * sur l'entrée publique {@code new RequetePage(0, 0, ...)} :
     * le {@code pageSize} nul est absorbé par la normalisation
     * de {@link RequetePage} avant la délégation au stockage.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(pageSize == 0) : garantit la normalisation en taille par défaut avant l'appel DAO")
    @Test
    public void testRechercherTousParPagePageSizeZero() throws Exception {

        /* ARRANGE :
         * prépare une RequetePage construite avec pageSize == 0.
         *
         * Le constructeur de RequetePage normalise cette taille invalide
         * vers RequetePage.TAILLE_DEFAUT avant l'appel du service.
         */
        final RequetePage requete
            = new RequetePage(PAGE_0, 0, new ArrayList<TriSpec>());

        /* Prépare une Page Spring cohérente
         * avec la pagination attendue après normalisation.
         */
        final List<ProduitJPA> contenuJPA = Arrays.asList(
                this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME),
                this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME));

        final Page<ProduitJPA> page
            = new PageImpl<ProduitJPA>(
                    contenuJPA,
                    PageRequest.of(
                            RequetePage.PAGE_DEFAUT,
                            RequetePage.TAILLE_DEFAUT),
                    2L);

        /*
         * Crée un ArgumentCaptor Mockito capable de capturer
         * l'argument de type Pageable réellement transmis au DAO.
         */
        final ArgumentCaptor<Pageable> captor
            = ArgumentCaptor.forClass(Pageable.class);

        /*
         * Configuration du Mock :
         * L'appel produitDaoJPA.findAll(Pageable)
         * sur le DAO objet métier mocké retourne la page préparée.
         */
        when(this.produitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * appelle le service avec une RequetePage
         * dont la taille initiale demandée vaut 0.
         */
        final ResultatPage<Produit> resultat
            = this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * capture le Pageable transmis au DAO objet métier.
         */
        verify(this.produitDaoJPA).findAll(captor.capture());

        final Pageable pageable = captor.getValue();

        /* Vérifie que pageSize == 0 a été normalisé
         * en taille par défaut avant l'appel au stockage.
         */
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(pageable.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(pageable.getSort().isSorted()).isFalse();

        /* Vérifie que le ResultatPage retourné
         * est cohérent avec la Page renvoyée par le DAO.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(resultat.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(resultat.getTotalElements()).isEqualTo(2L);
        assertThat(resultat.getContent())
            .extracting(Produit::getProduit)
            .containsExactly(CHEMISE_ML_HOMME, CHEMISE_MC_HOMME);

        /* Vérifie que les Produits métier retournés
         * conservent leur parent SousTypeProduit.
         */
        assertThat(resultat.getContent().get(0).getSousTypeProduit()).isNotNull();
        assertThat(resultat.getContent().get(0).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        assertThat(resultat.getContent().get(1).getSousTypeProduit()).isNotNull();
        assertThat(resultat.getContent().get(1).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        /* 
         * Vérifie que le DAO Parent mocké n'a pas été appelé.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que si une {@link RequetePage}
     * contient des consignes de tri :</p>
     * <ul>
     * <li>le service ignore les consignes de tri inutilisables
     * (entrée {@code null}, propriété vide) ;</li>
     * <li>le service convertit les consignes de tri valides
     * en {@link Sort} Spring ;</li>
     * <li>il transmet au DAO un {@link Pageable} cohérent
     * avec la page, la taille et les tris demandés ;</li>
     * <li>il retourne un {@link ResultatPage} cohérent
     * avec la page renvoyée par le DAO ;</li>
     * <li>il conserve le parent métier des produits retournés ;</li>
     * <li>il n'appelle pas le DAO parent.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(avec tri) : garantit la conversion de TriSpec en Sort Spring")
    @Test
    public void testRechercherTousParPageAvecTri() throws Exception {

        /* ARRANGE :
         * prépare une requête paginée contenant :
         * - des tris invalides à ignorer ;
         * - des tris valides à convertir en Sort Spring.
         */
        final String propTriIdProduit = "idProduit";

        final List<TriSpec> tris = new ArrayList<TriSpec>();
        tris.add(null);
        tris.add(new TriSpec(BLANK, DirectionTri.ASC));
        tris.add(new TriSpec(PROP_TRI_PRODUIT, DirectionTri.ASC));
        tris.add(new TriSpec(propTriIdProduit, DirectionTri.DESC));

        final RequetePage requete = new RequetePage(1, 3, tris);

        /* Prépare une Page Spring cohérente
         * avec la pagination explicitement demandée.
         */
        final ProduitJPA p1 =
                this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        p1.setIdProduit(1L);

        final ProduitJPA p2 =
                this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        p2.setIdProduit(2L);

        final List<ProduitJPA> contenuJPA = Arrays.asList(p1, p2);

        final Sort sort = Sort.by(
                Sort.Order.asc(PROP_TRI_PRODUIT),
                Sort.Order.desc(propTriIdProduit));

        final Page<ProduitJPA> page
            = new PageImpl<ProduitJPA>(
                    contenuJPA,
                    PageRequest.of(1, 3, sort),
                    TOTAL_10);

        /*
         * Crée un ArgumentCaptor Mockito capable de capturer
         * le Pageable transmis au DAO objet métier.
         */
        final ArgumentCaptor<Pageable> captor
            = ArgumentCaptor.forClass(Pageable.class);

        /*
         * Configuration du Mock :
         * L'appel produitDaoJPA.findAll(Pageable)
         * sur le DAO objet métier mocké retourne la page préparée.
         */
        when(this.produitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * appelle le service avec une RequetePage
         * portant des tris invalides et des tris valides.
         */
        final ResultatPage<Produit> resultat
            = this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * capture le Pageable transmis au DAO objet métier.
         */
        verify(this.produitDaoJPA, times(1)).findAll(captor.capture());

        final Pageable pageable = captor.getValue();

        /* Vérifie que la pagination demandée
         * est transmise au stockage.
         */
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(3);

        /* Vérifie que les tris invalides sont ignorés
         * et que les TriSpec valides sont convertis en Sort Spring.
         */
        final Sort sortCapture = pageable.getSort();

        assertThat(sortCapture.isSorted()).isTrue();

        assertThat(sortCapture)
            .extracting(Sort.Order::getProperty)
            .containsExactly(PROP_TRI_PRODUIT, propTriIdProduit);

        assertThat(sortCapture.getOrderFor(BLANK)).isNull();

        assertThat(sortCapture.getOrderFor(PROP_TRI_PRODUIT)).isNotNull();
        assertThat(sortCapture.getOrderFor(PROP_TRI_PRODUIT).getDirection())
            .isEqualTo(Sort.Direction.ASC);

        assertThat(sortCapture.getOrderFor(propTriIdProduit)).isNotNull();
        assertThat(sortCapture.getOrderFor(propTriIdProduit).getDirection())
            .isEqualTo(Sort.Direction.DESC);

        /* Vérifie que le ResultatPage retourné
         * est cohérent avec la Page renvoyée par le DAO.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getPageNumber()).isEqualTo(1);
        assertThat(resultat.getPageSize()).isEqualTo(3);
        assertThat(resultat.getTotalElements()).isEqualTo(page.getTotalElements());

        assertThat(resultat.getContent()).isNotNull();
        assertThat(resultat.getContent())
            .extracting(Produit::getProduit)
            .containsExactly(CHEMISE_MC_HOMME, CHEMISE_ML_HOMME);

        /* Vérifie que les Produits métier retournés
         * conservent leur parent SousTypeProduit.
         */
        assertThat(resultat.getContent().get(0).getSousTypeProduit()).isNotNull();
        assertThat(resultat.getContent().get(0).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        assertThat(resultat.getContent().get(1).getSousTypeProduit()).isNotNull();
        assertThat(resultat.getContent().get(1).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        /*
         * Vérifie que le DAO Parent mocké n'a pas été appelé.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que si la page renvoyée par le DAO contient
     * des éléments {@code null} au milieu d'Entities {@link ProduitJPA}
     * valides :</p>
     * <ul>
     * <li>le service ne propage pas ces {@code null}
     * dans le contenu métier retourné ;</li>
     * <li>il conserve uniquement les {@link ProduitJPA}
     * effectivement convertissables ;</li>
     * <li>il retourne un contenu métier propre,
     * sans élément {@code null} parasite ;</li>
     * <li>il conserve les métadonnées de pagination
     * portées par la {@link Page} renvoyée par le DAO ;</li>
     * <li>il conserve le parent métier des produits retournés.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(contenu avec nulls) : garantit l'exclusion des nulls lors de la conversion")
    @Test
    public void testRechercherTousParPageContenuAvecNulls() throws Exception {

        /* ARRANGE :
         * prépare une Page Spring contenant :
         * - un ProduitJPA valide ;
         * - un élément null ;
         * - un autre ProduitJPA valide.
         */
        final ProduitJPA p1 =
                this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        p1.setIdProduit(1L);

        final ProduitJPA p2 =
                this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        p2.setIdProduit(2L);

        final List<ProduitJPA> contenuJPA = Arrays.asList(p1, null, p2);

        final Page<ProduitJPA> page
            = new PageImpl<ProduitJPA>(
                    contenuJPA,
                    PageRequest.of(
                            RequetePage.PAGE_DEFAUT,
                            RequetePage.TAILLE_DEFAUT),
                    contenuJPA.size());

        /*
         * Crée un ArgumentCaptor Mockito capable de capturer
         * l'argument de type Pageable réellement transmis au DAO.
         */
        final ArgumentCaptor<Pageable> captor
            = ArgumentCaptor.forClass(Pageable.class);

        /*
         * Configuration du Mock :
         * L'appel produitDaoJPA.findAll(Pageable)
         * sur le DAO objet métier mocké retourne la page préparée.
         */
        when(this.produitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * appelle le service avec une RequetePage présente mais neutre
         * (sans aucun paramètre : new RequetePage()).
         */
        final ResultatPage<Produit> resultat
            = this.service.rechercherTousParPage(new RequetePage());

        /* ASSERT :
         * capture le Pageable transmis au DAO objet métier.
         */
        verify(this.produitDaoJPA).findAll(captor.capture());

        final Pageable pageable = captor.getValue();

        /* Vérifie que la RequetePage présente mais neutre
         * est convertie en pagination Spring par défaut.
         */
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(pageable.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(pageable.getSort().isSorted()).isFalse();

        /* Vérifie que le ResultatPage retourné
         * conserve les métadonnées de la Page renvoyée par le DAO.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(resultat.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(resultat.getTotalElements()).isEqualTo(page.getTotalElements());

        /* Vérifie que le contenu métier retourné
         * ne contient pas le null présent dans la Page DAO.
         */
        assertThat(resultat.getContent()).isNotNull();
        assertThat(resultat.getContent()).doesNotContainNull();
        assertThat(resultat.getContent()).hasSize(2);
        assertThat(resultat.getContent())
            .extracting(Produit::getProduit)
            .containsExactly(CHEMISE_ML_HOMME, CHEMISE_MC_HOMME);

        /* Vérifie que les Produits métier retournés
         * conservent leur parent SousTypeProduit.
         */
        assertThat(resultat.getContent().get(0).getSousTypeProduit()).isNotNull();
        assertThat(resultat.getContent().get(0).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        assertThat(resultat.getContent().get(1).getSousTypeProduit()).isNotNull();
        assertThat(resultat.getContent().get(1).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        /* 
         * Vérifie que le DAO Parent mocké n'a pas été appelé.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que si le DAO renvoie une {@link Page}
     * non null avec un contenu vide :</p>
     * <ul>
     * <li>le service retourne un {@link ResultatPage} non null ;</li>
     * <li>le contenu métier retourné est non null et vide ;</li>
     * <li>les métadonnées de pagination sont cohérentes
     * avec la {@link Page} renvoyée par le DAO ;</li>
     * <li>le DAO objet métier est appelé une seule fois
     * via {@code findAll(Pageable)} ;</li>
     * <li>le DAO parent n'est pas appelé.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(retourne page vide) - retourne une page vide cohérente")
    @Test
    public void testRechercherTousParPagePageVide() throws Exception {

        /* ARRANGE :
         * prépare une Page Spring non null,
         * avec un contenu vide et des métadonnées cohérentes.
         */
        final Page<ProduitJPA> page
            = new PageImpl<ProduitJPA>(
                    Collections.emptyList(),
                    PageRequest.of(
                            RequetePage.PAGE_DEFAUT,
                            RequetePage.TAILLE_DEFAUT),
                    0L);

        /*
         * Configuration du Mock :
         * L'appel produitDaoJPA.findAll(Pageable)
         * sur le DAO objet métier mocké retourne la page vide préparée.
         */
        when(this.produitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * appelle le service avec une RequetePage présente mais neutre
         * (sans aucun paramètre : new RequetePage()).
         */
        final ResultatPage<Produit> resultat
            = this.service.rechercherTousParPage(new RequetePage());

        /* ASSERT :
         * vérifie que le DAO objet métier mocké
         * a été appelé une seule fois via findAll(Pageable).
         */
        verify(this.produitDaoJPA, times(1)).findAll(any(Pageable.class));

        /* Vérifie que le ResultatPage retourné
         * est cohérent avec la Page vide renvoyée par le DAO.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(resultat.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(resultat.getTotalElements()).isEqualTo(0L);

        /* Vérifie que le contenu métier retourné
         * est non null et vide.
         */
        assertThat(resultat.getContent()).isNotNull();
        assertThat(resultat.getContent()).isEmpty();

        /* 
         * Vérifie que le DAO Parent mocké n'a pas été appelé.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
	 * <div>
	 * <p>garantit que si {@code rechercherTousParPage(new RequetePage())}
	 * est appelé avec une requête non nulle mais 
	 * neutre (sans aucun paramètre : new RequetePage()) :</p>
	 * <ul>
	 * <li>le service convertit cette RequetePage présente mais neutre
	 * (sans aucun paramètre : new RequetePage()) 
	 * en un {@link Pageable} Spring cohérent ;</li>
	 * <li>il transmet au DAO la pagination par défaut
	 * portée par cette RequetePage présente mais neutre
	 * (sans aucun paramètre : new RequetePage()) ;</li>
	 * <li>il ne force aucun tri lorsqu'aucune consigne de tri
	 * n'est demandée ;</li>
	 * <li>il retourne un {@link ResultatPage} cohérent
	 * avec la page renvoyée par le DAO ;</li>
	 * <li>il conserve le parent des objets métier retournés.</li>
	 * </ul>
	 * <p>Ce test complète le cas {@code rechercherTousParPage(null)} :</p>
	 * <ul>
	 * <li>le test {@code null} prouve le remplacement d'une requête absente ;</li>
	 * <li>celui-ci prouve la conversion correcte
	 * d'une RequetePage présente mais neutre
	 * (sans aucun paramètre : new RequetePage()).</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(new RequetePage()) : garantit la conversion en Pageable Spring par défaut sans tri")
    @Test
    public void testRechercherTousParPageNominalRequeteNeutre() throws Exception {

        /* ARRANGE :
         * Prépare une RequetePage présente,
         * instanciée avec le constructeur d'arité nulle.
         */
        final RequetePage requete = new RequetePage();

        /* Prépare une Page Spring cohérente
         * avec la pagination par défaut.
         */
        final List<ProduitJPA> contenuJPA = Arrays.asList(
                this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME),
                this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME));

        final Page<ProduitJPA> page
            = new PageImpl<ProduitJPA>(
                    contenuJPA,
                    PageRequest.of(
                            RequetePage.PAGE_DEFAUT,
                            RequetePage.TAILLE_DEFAUT),
                    2L);

        /*
         * Crée un ArgumentCaptor Mockito capable de capturer
         * le Pageable transmis au DAO objet métier.
         */
        final ArgumentCaptor<Pageable> captor
            = ArgumentCaptor.forClass(Pageable.class);

        /*
         * Configuration du Mock :
         * L'appel produitDaoJPA.findAll(Pageable)
         * sur le DAO objet métier mocké retourne la page préparée.
         */
        when(this.produitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * Appelle le service avec new RequetePage().
         */
        final ResultatPage<Produit> resultat
            = this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * Capture le Pageable transmis au DAO objet métier.
         */
        verify(this.produitDaoJPA, times(1)).findAll(captor.capture());

        final Pageable pageable = captor.getValue();

        /* Vérifie que new RequetePage()
         * est convertie en pagination Spring par défaut sans tri.
         */
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(pageable.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(pageable.getSort().isSorted()).isFalse();

        /* Vérifie que le ResultatPage retourné
         * conserve les métadonnées de la Page renvoyée par le DAO.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(resultat.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(resultat.getTotalElements()).isEqualTo(page.getTotalElements());

        /* Vérifie le contenu Produit retourné.
         */
        assertThat(resultat.getContent()).isNotNull();
        assertThat(resultat.getContent())
            .extracting(Produit::getProduit)
            .containsExactly(CHEMISE_ML_HOMME, CHEMISE_MC_HOMME);

        /* Vérifie que les Produits métier retournés
         * conservent leur parent SousTypeProduit.
         */
        assertThat(resultat.getContent().get(0).getSousTypeProduit()).isNotNull();
        assertThat(resultat.getContent().get(0).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        assertThat(resultat.getContent().get(1).getSousTypeProduit()).isNotNull();
        assertThat(resultat.getContent().get(1).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        /* 
         * Vérifie que le DAO Parent mocké n'a pas été appelé.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
	
	
    /**
     * <div>
     * <p>garantit que si {@code rechercherTousParPage(...)}
     * est appelé avec une {@link RequetePage} explicite :</p>
     * <ul>
     * <li>le service convertit cette {@link RequetePage}
     * en {@link Pageable} Spring ;</li>
     * <li>il transmet au DAO le numéro de page demandé ;</li>
     * <li>il transmet au DAO la taille de page demandée ;</li>
     * <li>il ne transmet aucun tri si la requête ne contient aucun tri ;</li>
     * <li>il retourne un {@link ResultatPage} cohérent
     * avec la {@link Page} renvoyée par le DAO ;</li>
     * <li>il conserve le parent métier des produits retournés ;</li>
     * <li>il n'appelle pas le DAO parent.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(nominal) : garantit la pagination explicite sans tri")
    @Test
    public void testRechercherTousParPageNominal() throws Exception {

        /* ARRANGE :
         * Prépare une RequetePage explicite :
         * - page demandée : 1 ;
         * - taille demandée : 2 ;
         * - aucun tri.
         */
        final RequetePage requete
            = new RequetePage(1, 2, new ArrayList<TriSpec>());

        /* Prépare une Page Spring cohérente
         * avec la pagination explicitement demandée.
         */
        final ProduitJPA p1 =
                this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        p1.setIdProduit(1L);

        final ProduitJPA p2 =
                this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        p2.setIdProduit(2L);

        final List<ProduitJPA> contenuJPA = Arrays.asList(p1, p2);

        final Page<ProduitJPA> page
            = new PageImpl<ProduitJPA>(
                    contenuJPA,
                    PageRequest.of(1, 2),
                    TOTAL_10);

        /*
         * Crée un ArgumentCaptor Mockito capable de capturer
         * le Pageable transmis au DAO objet métier.
         */
        final ArgumentCaptor<Pageable> captor
            = ArgumentCaptor.forClass(Pageable.class);

        /*
         * Configuration du Mock :
         * L'appel produitDaoJPA.findAll(Pageable)
         * sur le DAO objet métier mocké retourne la page préparée.
         */
        when(this.produitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * Appelle le service avec une RequetePage explicite.
         */
        final ResultatPage<Produit> resultat
            = this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * Capture le Pageable transmis au DAO objet métier.
         */
        verify(this.produitDaoJPA, times(1)).findAll(captor.capture());

        final Pageable pageable = captor.getValue();

        /* Vérifie que la RequetePage explicite
         * est convertie en Pageable Spring avec la page
         * et la taille demandées.
         */
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(2);
        assertThat(pageable.getSort().isSorted()).isFalse();

        /* Vérifie que le ResultatPage retourné
         * conserve les métadonnées de la Page renvoyée par le DAO.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getPageNumber()).isEqualTo(1);
        assertThat(resultat.getPageSize()).isEqualTo(2);
        assertThat(resultat.getTotalElements()).isEqualTo(page.getTotalElements());

        /* Vérifie le contenu Produit retourné.
         */
        assertThat(resultat.getContent()).isNotNull();
        assertThat(resultat.getContent())
            .extracting(Produit::getProduit)
            .containsExactly(CHEMISE_MC_HOMME, CHEMISE_ML_HOMME);

        /* Vérifie que les Produits métier retournés
         * conservent leur parent SousTypeProduit.
         */
        assertThat(resultat.getContent().get(0).getSousTypeProduit()).isNotNull();
        assertThat(resultat.getContent().get(0).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        assertThat(resultat.getContent().get(1).getSousTypeProduit()).isNotNull();
        assertThat(resultat.getContent().get(1).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        /* 
         * Vérifie que le DAO Parent mocké n'a pas été appelé.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
	
    
    // ======================== findByObjetMetier =========================
    
    
    
	/**
     * <div>
     * <p>findByObjetMetier(null) lève ExceptionAppliParamNull.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByObjetMetier(null) - ExceptionAppliParamNull")
    @Test
    public void testFindByObjetMetierNull() {

        assertThatThrownBy(() -> this.service.findByObjetMetier(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL);

        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>findByObjetMetier(libellé blank) lève ExceptionAppliLibelleBlank.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByObjetMetier(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testFindByObjetMetierLibelleBlank() {

        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = new Produit();
        p.setProduit(BLANK);
        p.setSousTypeProduit(parent);

        assertThatThrownBy(() -> this.service.findByObjetMetier(p))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);

        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>findByObjetMetier(parent null) lève ExceptionAppliParentNull.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByObjetMetier(parent null) - ExceptionAppliParentNull")
    @Test
    public void testFindByObjetMetierParentNullExceptionAppliParentNull() {

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(null);

        assertThatThrownBy(() -> this.service.findByObjetMetier(p))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL);

        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>findByObjetMetier(parent libellé blank) lève ExceptionAppliLibelleBlank.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByObjetMetier(parent libellé blank) - ExceptionAppliLibelleBlank")
    @Test
    public void testFindByObjetMetierParentLibelleBlankExceptionAppliLibelleBlank() {

        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(BLANK);

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        assertThatThrownBy(() -> this.service.findByObjetMetier(p))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK);

        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>findByObjetMetier(parent id null) lève ExceptionTechniqueGatewayNonPersistent.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByObjetMetier(parent id null) - ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testFindByObjetMetierParentIdNullExceptionTechniqueGatewayNonPersistent() {

        final SousTypeProduit parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        parent.setIdSousTypeProduit(null); // Parent NON persistant

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        assertThatThrownBy(() -> this.service.findByObjetMetier(p))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT + VETEMENT_HOMME);

        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>findByObjetMetier(parent absent DAO) lève ExceptionTechniqueGatewayNonPersistent.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByObjetMetier(parent absent DAO) - ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testFindByObjetMetierParentAbsentExceptionTechniqueGatewayNonPersistent() {

        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.service.findByObjetMetier(p))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT + VETEMENT_HOMME);

        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>findByObjetMetier(DAO parent jette Exception) lève ExceptionTechniqueGateway.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByObjetMetier(DAO parent jette Exception) - ExceptionTechniqueGateway")
    @Test
    public void testFindByObjetMetierParentDaoJetteExceptionTechniqueGateway() {

        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenThrow(new RuntimeException(BOOM));

        assertThatThrownBy(() -> this.service.findByObjetMetier(p))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);

        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>findByObjetMetier(DAO enfant retourne null) lève ExceptionTechniqueGateway KO_STOCKAGE.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByObjetMetier(DAO enfant null) - ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testFindByObjetMetierDaoRetourneNullExceptionTechniqueGateway() {

        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        final SousTypeProduitJPA parentJPA = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(parentJPA));
        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA)).thenReturn(null);

        assertThatThrownBy(() -> this.service.findByObjetMetier(p))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findAllBySousTypeProduit(parentJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>findByObjetMetier(DAO enfant jette Exception) lève ExceptionTechniqueGateway (wrap).</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByObjetMetier(DAO enfant jette Exception) - ExceptionTechniqueGateway (wrap)")
    @Test
    public void testFindByObjetMetierDaoJetteExceptionTechniqueGateway() {

        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        final SousTypeProduitJPA parentJPA = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(parentJPA));
        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA)).thenThrow(new RuntimeException(BOOM));

        assertThatThrownBy(() -> this.service.findByObjetMetier(p))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);

        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findAllBySousTypeProduit(parentJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>findByObjetMetier(pas trouvé) retourne null.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByObjetMetier(pas trouvé) - retourne null")
    @Test
    public void testFindByObjetMetierPasTrouveRetourneNull() throws Exception {

        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        final SousTypeProduitJPA parentJPA = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(parentJPA));

        final List<ProduitJPA> entities = new ArrayList<>();
        entities.add(null);
        entities.add(this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME));
        entities.add(this.fabriquerProduitJPA(CHEMISE_ML_FEMME, VETEMENT_FEMME));

        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA)).thenReturn(entities);

        final Produit retour = this.service.findByObjetMetier(p);

        assertThat(retour).isNull();

        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findAllBySousTypeProduit(parentJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>findByObjetMetier(trouvé) retourne objet métier non null.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByObjetMetier(trouvé) - retourne objet non null")
    @Test
    public void testFindByObjetMetierTrouveOk() throws Exception {

        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME.toUpperCase(Locale.ROOT));
        p.setSousTypeProduit(parent);

        final SousTypeProduitJPA parentJPA = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(parentJPA));

        final List<ProduitJPA> entities = new ArrayList<>();
        entities.add(null);
        entities.add(this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME));
        entities.add(this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME));

        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA)).thenReturn(entities);

        final Produit retour = this.service.findByObjetMetier(p);

        assertThat(retour).isNotNull();
        assertThat(retour.getProduit()).isEqualTo(CHEMISE_ML_HOMME);
        assertThat(retour.getSousTypeProduit()).isNotNull();
        assertThat(retour.getSousTypeProduit().getSousTypeProduit()).isEqualTo(VETEMENT_HOMME);

        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findAllBySousTypeProduit(parentJPA);

    } // __________________________________________________________________
    

    
    // ========================== findByLibelle ===========================



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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>findByLibelle(null) lève ExceptionAppliLibelleBlank (null est blank).</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelle(null) - ExceptionAppliLibelleBlank")
    @Test
    public void testFindByLibelleParamNullExceptionAppliLibelleBlank() {

        assertThatThrownBy(() -> this.service.findByLibelle(null))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK);

        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>findByLibelle(DAO retourne liste vide) retourne liste vide non null.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelle(DAO vide) - retourne liste vide")
    @Test
    public void testFindByLibelleDaoVideRetourListeVideOk() throws Exception {

        when(this.produitDaoJPA.findByProduitIgnoreCase(CHEMISE_ML_HOMME))
            .thenReturn(new ArrayList<ProduitJPA>());

        final List<Produit> retour = this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(retour).isNotNull();
        assertThat(retour).isEmpty();

        verify(this.produitDaoJPA, times(1)).findByProduitIgnoreCase(CHEMISE_ML_HOMME);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>findByLibelle(DAO jette Exception) wrap en ExceptionTechniqueGateway.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelle(DAO jette Exception) - ExceptionTechniqueGateway (wrap)")
    @Test
    public void testFindByLibelleDaoJetteExceptionTechniqueGateway() {

        when(this.produitDaoJPA.findByProduitIgnoreCase(CHEMISE_ML_HOMME))
            .thenThrow(new RuntimeException(BOOM));

        assertThatThrownBy(() -> this.service.findByLibelle(CHEMISE_ML_HOMME))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);

        verify(this.produitDaoJPA, times(1)).findByProduitIgnoreCase(CHEMISE_ML_HOMME);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>findByLibelle(nominal) filtre les nulls, trie (parent), dédoublonne.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelle(nominal) - filtre/trie/dédoublonne")
    @Test
    public void testFindByLibelleNominalFiltreTrieDedoublonneOk() throws Exception {

        final ProduitJPA femme1 = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_FEMME);
        femme1.setIdProduit(101L);

        final ProduitJPA homme1 = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        homme1.setIdProduit(201L);

        /* Doublon métier (même libellé Produit + même parent) -> doit être dédoublonné. */
        final ProduitJPA doublonHomme = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        doublonHomme.setIdProduit(999L);

        final List<ProduitJPA> entities = Arrays.asList(
                homme1,
                null,
                doublonHomme,
                femme1);

        when(this.produitDaoJPA.findByProduitIgnoreCase(CHEMISE_ML_HOMME))
            .thenReturn(entities);

        final List<Produit> retour = this.service.findByLibelle(CHEMISE_ML_HOMME);

        assertThat(retour).isNotNull();
        assertThat(retour).doesNotContainNull();
        assertThat(retour).hasSize(2);

        /* Ordre : parent puis libellé (ici libellé identique -> ordre parent). */
        assertThat(retour)
            .extracting(p -> p.getSousTypeProduit().getSousTypeProduit())
            .containsExactly(VETEMENT_FEMME, VETEMENT_HOMME);

        verify(this.produitDaoJPA, times(1)).findByProduitIgnoreCase(CHEMISE_ML_HOMME);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
   
    

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
        
    } // __________________________________________________________________
    
    

    // ======================== findByLibelleRapide =======================
    
    
    
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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>findByLibelleRapide(blank) délègue à rechercherTous() qui peut échouer si DAO null.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelleRapide(blank + DAO.findAll null) - ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testFindByLibelleRapideBlankDaoFindAllNullExceptionTechniqueGateway() {
    	
        when(this.produitDaoJPA.findAll()).thenReturn(null);

        assertThatThrownBy(() -> this.service.findByLibelleRapide(BLANK))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        verify(this.produitDaoJPA, times(1)).findAll();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>findByLibelleRapide(DAO jette Exception) wrap en ExceptionTechniqueGateway.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelleRapide(DAO jette Exception) - ExceptionTechniqueGateway (wrap)")
    @Test
    public void testFindByLibelleRapideDaoJetteExceptionTechniqueGateway() {
    	
        when(this.produitDaoJPA.findByProduitContainingIgnoreCase(CHEMISE))
            .thenThrow(new RuntimeException(BOOM));

        assertThatThrownBy(() -> this.service.findByLibelleRapide(CHEMISE))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);

        verify(this.produitDaoJPA, times(1)).findByProduitContainingIgnoreCase(CHEMISE);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>findByLibelleRapide(nominal) filtre les nulls, trie (parent puis libellé), dédoublonne.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelleRapide(nominal) - filtre/trie/dédoublonne")
    @Test
    public void testFindByLibelleRapideNominalFiltreTrieDedoublonneOk() throws Exception {
    	
        final ProduitJPA femme = this.fabriquerProduitJPA(CHEMISE_ML_FEMME, VETEMENT_FEMME);
        femme.setIdProduit(11L);

        final ProduitJPA homme = this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        homme.setIdProduit(21L);

        /* Doublon métier (même libellé Produit + même parent) -> doit être dédoublonné. */
        final ProduitJPA doublonHomme = this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        doublonHomme.setIdProduit(999L);

        when(this.produitDaoJPA.findByProduitContainingIgnoreCase(CHEMISE))
            .thenReturn(Arrays.asList(homme, null, doublonHomme, femme));

        final List<Produit> retour = this.service.findByLibelleRapide(CHEMISE);

        assertThat(retour).isNotNull();
        assertThat(retour).doesNotContainNull();
        assertThat(retour).hasSize(2);

        /* Ordre : parent puis libellé. */
        assertThat(retour)
            .extracting(p -> p.getSousTypeProduit().getSousTypeProduit())
            .containsExactly(VETEMENT_FEMME, VETEMENT_HOMME);

        assertThat(retour)
            .extracting(Produit::getProduit)
            .containsExactly(CHEMISE_ML_FEMME, CHEMISE_MC_HOMME);

        verify(this.produitDaoJPA, times(1)).findByProduitContainingIgnoreCase(CHEMISE);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

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
	    
	} // __________________________________________________________________
    
    
    
    // ========================= findAllByParent ==========================
    
    
    
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
        
    } // __________________________________________________________________
    
    

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
            .hasMessage(ANOMALIE_APPLI_PAS_PARENT);
        
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);
        
    } // __________________________________________________________________
    
    

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
            .hasMessage(ANOMALIE_APPLI_PAS_PARENT);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verifyNoInteractions(this.produitDaoJPA);
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>findAllByParent(parent libellé blank) lève ExceptionAppliLibelleBlank.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findAllByParent(parent libellé blank) - ExceptionAppliLibelleBlank")
    @Test
    public void testFindAllByParentParentLibelleBlankExceptionAppliLibelleBlank() {

        final SousTypeProduit parent = this.fabriquerParentMetierPersistant(BLANK);

        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK);

        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>findAllByParent(DAO retourne liste vide) retourne liste vide non null.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findAllByParent(DAO vide) - retourne liste vide")
    @Test
    public void testFindAllByParentDaoVideRetourListeVideOk() throws Exception {

        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final SousTypeProduitJPA parentJPA = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(parentJPA));
        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA)).thenReturn(new ArrayList<ProduitJPA>());

        final List<Produit> retour = this.service.findAllByParent((SousTypeProduit) parent);

        assertThat(retour).isNotNull();
        assertThat(retour).isEmpty();

        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findAllBySousTypeProduit(parentJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>findAllByParent(DAO jette Exception) wrap en ExceptionTechniqueGateway.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findAllByParent(DAO jette Exception) - ExceptionTechniqueGateway (wrap)")
    @Test
    public void testFindAllByParentDaoJetteExceptionTechniqueGateway() {

        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final SousTypeProduitJPA parentJPA = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(parentJPA));
        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA)).thenThrow(new RuntimeException(BOOM));

        assertThatThrownBy(() -> this.service.findAllByParent((SousTypeProduit) parent))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);

        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findAllBySousTypeProduit(parentJPA);

    } // __________________________________________________________________



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
	    
	} // __________________________________________________________________
	
	

	/**
     * <div>
     * <p>findAllByParent(nominal) filtre les nulls, trie (parent puis libellé), dédoublonne.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findAllByParent(nominal) - filtre/trie/dédoublonne")
    @Test
    public void testFindAllByParentNominalFiltreTrieDedoublonneOk() throws Exception {

        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final SousTypeProduitJPA parentJPA = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        final ProduitJPA p1 = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        p1.setIdProduit(1L);

        final ProduitJPA p2 = this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        p2.setIdProduit(2L);

        /* Doublon métier (même libellé Produit + même parent) -> doit être dédoublonné. */
        final ProduitJPA doublon = this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        doublon.setIdProduit(999L);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(parentJPA));
        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA))
            .thenReturn(Arrays.asList(p2, null, doublon, p1));

        final List<Produit> retour = this.service.findAllByParent((SousTypeProduit) parent);

        assertThat(retour).isNotNull();
        assertThat(retour).doesNotContainNull();
        assertThat(retour).hasSize(2);

        /* Ordre : parent puis libellé (ici parent identique -> ordre libellé). */
        assertThat(retour)
            .extracting(Produit::getProduit)
            .containsExactly(CHEMISE_MC_HOMME, CHEMISE_ML_HOMME);

        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findAllBySousTypeProduit(parentJPA);

    } // __________________________________________________________________
    

    
    // ============================ findById ==============================



    /**
     * <div>
     * <p>findById(null) lève ExceptionAppliParamNull.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(null) - ExceptionAppliParamNull")
    @Test
    public void testFindByIdParamNullExceptionAppliParamNull() {

        assertThatThrownBy(() -> this.service.findById(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDBYID_KO_PARAM_NULL);

        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>findById(DAO retourne null) lève ExceptionTechniqueGateway KO_STOCKAGE.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(DAO null) - ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testFindByIdDaoNullExceptionTechniqueGateway() {

        when(this.produitDaoJPA.findById(1L)).thenReturn(null);

        assertThatThrownBy(() -> this.service.findById(1L))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        verify(this.produitDaoJPA, times(1)).findById(1L);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>findById(nominal trouvé) retourne l'objet métier correspondant.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(nominal trouvé) - retourne l'objet métier")
    @Test
    public void testFindByIdNominalTrouveRetourneObjetMetier() throws Exception {

        final ProduitJPA entity = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        entity.setIdProduit(1L);

        when(this.produitDaoJPA.findById(1L)).thenReturn(Optional.of(entity));

        final Produit retour = this.service.findById(1L);

        assertThat(retour).isNotNull();
        assertThat(retour.getIdProduit()).isEqualTo(1L);
        assertThat(retour.getProduit()).isEqualTo(CHEMISE_ML_HOMME);
        assertThat(retour.getSousTypeProduit()).isNotNull();
        assertThat(retour.getSousTypeProduit().getIdSousTypeProduit()).isEqualTo(1L);
        assertThat(retour.getSousTypeProduit().getSousTypeProduit()).isEqualTo(VETEMENT_HOMME);

        verify(this.produitDaoJPA, times(1)).findById(1L);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>findById(Optional.empty) retourne null.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(Optional.empty) - retourne null")
    @Test
    public void testFindByIdNonTrouveRetourneNull() throws Exception {

        when(this.produitDaoJPA.findById(1L)).thenReturn(Optional.empty());

        final Produit retour = this.service.findById(1L);

        assertThat(retour).isNull();

        verify(this.produitDaoJPA, times(1)).findById(1L);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>findById(DAO jette Exception) wrap en ExceptionTechniqueGateway.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(DAO jette Exception) - ExceptionTechniqueGateway (wrap)")
    @Test
    public void testFindByIdDaoJetteExceptionTechniqueGateway() {

        when(this.produitDaoJPA.findById(1L)).thenThrow(new RuntimeException(BOOM));

        assertThatThrownBy(() -> this.service.findById(1L))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);

        verify(this.produitDaoJPA, times(1)).findById(1L);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    

    
    // ============================= update ===============================
    
    
    
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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    

    
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
	    
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>update(parent libellé blank) lève ExceptionAppliLibelleBlank.</p>
	 * </div>
	 */
	@Tag(TAG_UPDATE)
	@DisplayName("update(parent libellé blank) - ExceptionAppliLibelleBlank")
	@Test
	public void testUpdateParentLibelleBlankExceptionAppliLibelleBlank() {

	    final SousTypeProduitI parent = new SousTypeProduit();
	    parent.setIdSousTypeProduit(1L);
	    parent.setSousTypeProduit(BLANK);

	    final Produit p = new Produit();
	    p.setProduit(CHEMISE_ML_HOMME);
	    p.setSousTypeProduit(parent);
	    p.setIdProduit(1L);

	    assertThatThrownBy(() -> this.service.update(p))
	        .isInstanceOf(ExceptionAppliLibelleBlank.class)
	        .hasMessage(ProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK);

	    verifyNoInteractions(this.produitDaoJPA);
	    verifyNoInteractions(this.sousTypeProduitDaoJPA);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(parent id null) lève ExceptionTechniqueGatewayNonPersistent.</p>
	 * </div>
	 */
	@Tag(TAG_UPDATE)
	@DisplayName("update(parent id null) - ExceptionTechniqueGatewayNonPersistent")
	@Test
	public void testUpdateParentIdNullExceptionTechniqueGatewayNonPersistent() {

	    final SousTypeProduitI parent = new SousTypeProduit();
	    parent.setIdSousTypeProduit(null);
	    parent.setSousTypeProduit(VETEMENT_HOMME);

	    final Produit p = new Produit();
	    p.setProduit(CHEMISE_ML_HOMME);
	    p.setSousTypeProduit(parent);
	    p.setIdProduit(1L);

	    assertThatThrownBy(() -> this.service.update(p))
	        .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
	        .hasMessage(ProduitGatewayIService.MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT + VETEMENT_HOMME);

	    verifyNoInteractions(this.produitDaoJPA);
	    verifyNoInteractions(this.sousTypeProduitDaoJPA);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(DAO.findById retourne null) lève ExceptionTechniqueGateway KO_STOCKAGE.</p>
	 * </div>
	 */
	@Tag(TAG_UPDATE)
	@DisplayName("update(DAO findById null) - ExceptionTechniqueGateway KO_STOCKAGE")
	@Test
	public void testUpdateDaoFindByIdNullExceptionTechniqueGateway() {

	    final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

	    final Produit p = new Produit();
	    p.setProduit(CHEMISE_ML_HOMME + SUFFIX_MODIF);
	    p.setSousTypeProduit(parent);
	    p.setIdProduit(1L);

	    final SousTypeProduitJPA parentJPA = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);
	    when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(parentJPA));

	    when(this.produitDaoJPA.findById(1L)).thenReturn(null);

	    assertThatThrownBy(() -> this.service.update(p))
	        .isInstanceOf(ExceptionTechniqueGateway.class)
	        .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

	    verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
	    verify(this.produitDaoJPA, times(1)).findById(1L);
	    verify(this.produitDaoJPA, times(0)).save(any(ProduitJPA.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(DAO.save retourne null) lève ExceptionTechniqueGateway KO_STOCKAGE.</p>
	 * </div>
	 */
	@Tag(TAG_UPDATE)
	@DisplayName("update(DAO save null) - ExceptionTechniqueGateway KO_STOCKAGE")
	@Test
	public void testUpdateDaoSaveNullExceptionTechniqueGateway() throws Exception {

	    final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

	    final Produit p = new Produit();
	    p.setProduit(CHEMISE_ML_HOMME + SUFFIX_MODIF);
	    p.setSousTypeProduit(parent);
	    p.setIdProduit(1L);

	    final SousTypeProduitJPA parentJPA = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);
	    when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(parentJPA));

	    final ProduitJPA persiste = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
	    persiste.setIdProduit(1L);
	    when(this.produitDaoJPA.findById(1L)).thenReturn(Optional.of(persiste));

	    when(this.produitDaoJPA.save(any(ProduitJPA.class))).thenReturn(null);

	    assertThatThrownBy(() -> this.service.update(p))
	        .isInstanceOf(ExceptionTechniqueGateway.class)
	        .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

	    verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
	    verify(this.produitDaoJPA, times(1)).findById(1L);
	    verify(this.produitDaoJPA, times(1)).save(any(ProduitJPA.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>update(DAO.findById jette Exception) wrap 
	 * en ExceptionTechniqueGateway.</p>
	 * </div>
	 */
	@Tag(TAG_UPDATE)
	@DisplayName("update(DAO findById jette Exception) - ExceptionTechniqueGateway (wrap)")
	@Test
	public void testUpdateDaoFindByIdJetteExceptionTechniqueGateway() {

	    final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

	    final Produit p = new Produit();
	    p.setProduit(CHEMISE_ML_HOMME + SUFFIX_MODIF);
	    p.setSousTypeProduit(parent);
	    p.setIdProduit(1L);

	    final SousTypeProduitJPA parentJPA = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);
	    when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(parentJPA));

	    when(this.produitDaoJPA.findById(1L)).thenThrow(new RuntimeException(BOOM));

	    assertThatThrownBy(() -> this.service.update(p))
	        .isInstanceOf(ExceptionTechniqueGateway.class)
	        .hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH)
	        .hasMessageContaining(BOOM);

	    verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
	    verify(this.produitDaoJPA, times(1)).findById(1L);
	    verify(this.produitDaoJPA, times(0)).save(any(ProduitJPA.class));

	} // __________________________________________________________________
    

    
    // ============================= delete ===============================
    
    
    
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

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>delete(id null) lève ExceptionAppliParamNonPersistent.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(id null) - ExceptionAppliParamNonPersistent")
    @Test
    public void testDeleteIdNullExceptionAppliParamNonPersistent() {

        final SousTypeProduitI parent 
        	= this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);
        p.setIdProduit(null);

        assertThatThrownBy(() -> this.service.delete(p))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(MSG_DELETE_KO_ID_NULL);
        
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>delete(DAO findById retourne null Optional) 
     * lève ExceptionTechniqueGateway KO_STOCKAGE
     * et ne supprime pas.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(DAO findById null) - ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testDeleteDaoFindByIdRetourneNullOptionalExceptionTechniqueGateway() {

        final SousTypeProduitI parent 
        	= this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);
        p.setIdProduit(71L);

        when(this.produitDaoJPA.findById(71L)).thenReturn(null);

        assertThatThrownBy(() -> this.service.delete(p))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        verify(this.produitDaoJPA, times(1)).findById(71L);
        verify(this.produitDaoJPA, never()).delete(any(ProduitJPA.class));
        verify(this.produitDaoJPA, never()).flush();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>delete(KO DAO sur findById) wrappe 
     * en ExceptionTechniqueGateway.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO DAO sur findById) - ExceptionTechniqueGateway (wrap)")
    @Test
    public void testDeleteDaoFindByIdJetteExceptionWrapExceptionTechniqueGateway() {

        final SousTypeProduitI parent 
        	= this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);
        p.setIdProduit(71L);

        when(this.produitDaoJPA.findById(71L))
            .thenThrow(new RuntimeException(BOOM));

        assertThatThrownBy(() -> this.service.delete(p))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);

        verify(this.produitDaoJPA, times(1)).findById(71L);
        verify(this.produitDaoJPA, never()).delete(any(ProduitJPA.class));
        verify(this.produitDaoJPA, never()).flush();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________


    
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

        final SousTypeProduitI parent 
        	= this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);
        p.setIdProduit(71L);

        when(this.produitDaoJPA.findById(71L)).thenReturn(Optional.empty());

        this.service.delete(p);

        verify(this.produitDaoJPA, times(1)).findById(71L);
        verify(this.produitDaoJPA, never()).delete(any(ProduitJPA.class));
        verify(this.produitDaoJPA, never()).flush();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________

    

    /**
     * <div>
     * <p>delete(nominal) supprime via DAO.delete() + DAO.flush().</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(nominal) - DAO.delete + flush")
    @Test
    public void testDeleteNominalOk() throws Exception {

        final Long id = 70L;
        final ProduitJPA entity 
        	= this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        entity.setIdProduit(id);

        final Produit metier 
        	= this.fabriquerProduitMetier(CHEMISE_ML_HOMME, id,
            this.fabriquerParentMetierPersistant(VETEMENT_HOMME));

        when(this.produitDaoJPA.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(this.produitDaoJPA).delete(any(ProduitJPA.class));
        doNothing().when(this.produitDaoJPA).flush();

        this.service.delete(metier);

        verify(this.produitDaoJPA, times(1)).findById(id);
        verify(this.produitDaoJPA, times(1)).delete(entity);
        verify(this.produitDaoJPA, times(1)).flush();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>Test béton : delete() wrappe correctement 
     * une Exception levée par DAO.delete().</p>
     * </div>
     */
    @Tag(TAG_BETON)
    @DisplayName("delete(DAO.delete jette Exception) - ExceptionTechniqueGateway (wrap)")
    @Test
    public void testDeleteDaoDeleteJetteExceptionWrapExceptionTechniqueGateway() {

        final Long id = 70L;

        final Produit metier = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, id,
            this.fabriquerParentMetierPersistant(VETEMENT_HOMME));

        final ProduitJPA entity = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        entity.setIdProduit(id);

        when(this.produitDaoJPA.findById(id)).thenReturn(Optional.of(entity));
        doThrow(new RuntimeException(BOOM))
            .when(this.produitDaoJPA).delete(any(ProduitJPA.class));

        assertThatThrownBy(() -> this.service.delete(metier))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);

        verify(this.produitDaoJPA, times(1)).findById(id);
        verify(this.produitDaoJPA, times(1)).delete(entity);
        verify(this.produitDaoJPA, never()).flush();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>Test béton : delete() wrappe correctement 
     * une Exception levée par DAO.flush().</p>
     * </div>
     */
    @Tag(TAG_BETON)
    @DisplayName("delete(DAO.flush jette Exception) - ExceptionTechniqueGateway (wrap)")
    @Test
    public void testDeleteDaoFlushJetteExceptionWrapExceptionTechniqueGateway() {

        final Long id = 70L;

        final Produit metier 
        	= this.fabriquerProduitMetier(CHEMISE_ML_HOMME, id,
            this.fabriquerParentMetierPersistant(VETEMENT_HOMME));

        final ProduitJPA entity 
        	= this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        entity.setIdProduit(id);

        when(this.produitDaoJPA.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(this.produitDaoJPA).delete(any(ProduitJPA.class));
        doThrow(new RuntimeException(BOOM))
            .when(this.produitDaoJPA).flush();

        assertThatThrownBy(() -> this.service.delete(metier))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);

        verify(this.produitDaoJPA, times(1)).findById(id);
        verify(this.produitDaoJPA, times(1)).delete(entity);
        verify(this.produitDaoJPA, times(1)).flush();
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    

    // ============================== Count ===============================
    
    
    
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
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

    // ============== TESTS BETON (sanity / invariants) ===================
    

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
        
    } // __________________________________________________________________



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
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Fabrique un parent métier persistant.</p>
     * </div>
     *
     * @param pLibelleSousType : String
     * @return SousTypeProduitI
     */
    private SousTypeProduit fabriquerParentMetierPersistant(
    		final String pLibelleSousType) {
    	
        final TypeProduit typeProduit = new TypeProduit();
        typeProduit.setIdTypeProduit(1L);
        typeProduit.setTypeProduit(VETEMENT);

        final SousTypeProduit parent = new SousTypeProduit();
        parent.setIdSousTypeProduit(1L);
        parent.setSousTypeProduit(pLibelleSousType);
        parent.setTypeProduit(typeProduit);

        return parent;
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Fabrique un parent JPA persistant.</p>
     * </div>
     *
     * @param pLibelleSousType : String
     * @return SousTypeProduitJPA
     */
    private SousTypeProduitJPA fabriquerParentJPAPersistant(
    		final String pLibelleSousType) {
    	
        final TypeProduitJPA typeProduitJPA = new TypeProduitJPA();
        typeProduitJPA.setIdTypeProduit(1L);
        typeProduitJPA.setTypeProduit(VETEMENT);

        final SousTypeProduitJPA parentJPA = new SousTypeProduitJPA();
        parentJPA.setIdSousTypeProduit(1L);
        parentJPA.setSousTypeProduit(pLibelleSousType);
        parentJPA.setTypeProduit(typeProduitJPA);

        return parentJPA;
        
    } // __________________________________________________________________
    
    

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
        
    } // __________________________________________________________________
    
    
    
} // FIN DE LA CLASSE ProduitGatewayJPAServiceMockTest.--------------------
