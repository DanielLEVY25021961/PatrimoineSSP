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
    
    /** "servicesGateway-RechercherTous" */
    public static final String TAG_RECHERCHER_TOUS 
    	= "servicesGateway-RechercherTous";    
    
    /**
     * "servicesGateway-RechercherTousParPage"
     */
    public static final String TAG_RECHERCHERTOUSPARPAGE 
    	= "servicesGateway-RechercherTousParPage";
       
    /**
     * "servicesGateway-FindByLibelle"
     */
    public static final String TAG_FINDBYLIBELLE
        = "servicesGateway-FindByLibelle"; 
    
    /**
     * "servicesGateway-FindByLibelleRapide"
     */
    public static final String TAG_FINDBYLIBELLERAPIDE
    	= "servicesGateway-FindByLibelleRapide";
    
    /**
     * "servicesGateway-FindAllByParent"
     */
    public static final String TAG_FINDALLBYPARENT
    	= "servicesGateway-FindAllByParent";
    
    /** "servicesGateway-FindByObjetMetier" */
    public static final String TAG_FINDBYOBJETMETIER 
        = "servicesGateway-FindByObjetMetier";
        
    /**
     * "servicesGateway-FindById"
     */
    public static final String TAG_FINDBYID
    	= "servicesGateway-FindById";

    /** "servicesGateway-Update" */
    public static final String TAG_UPDATE = "servicesGateway-Update";

    /** "servicesGateway-Delete" */
    public static final String TAG_DELETE = "servicesGateway-Delete";

    /** "servicesGateway-Count" */
    public static final String TAG_COUNT = "servicesGateway-Count";
    
    /** "servicesGateway-Sanity" */
    public static final String TAG_SANITY = "servicesGateway-Sanity";

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
    public void testCreerParentDAOExceptionMessageNonNull() {

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
    public void testCreerParentDAOExceptionMessageNull() {

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
    public void testCreerDAOSaveRetourneNull() {

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
    public void testCreerDAOSaveExceptionMessageNonNull() {

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
    public void testCreerDAOSaveExceptionMessageNull() {

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
    @Tag(TAG_RECHERCHER_TOUS)
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
    @Tag(TAG_RECHERCHER_TOUS)
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
    @Tag(TAG_RECHERCHER_TOUS)
    @DisplayName("rechercherTous(KO DAO message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testRechercherTousDAOExceptionMessageNonNull() {

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
    @Tag(TAG_RECHERCHER_TOUS)
    @DisplayName("rechercherTous(KO DAO message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testRechercherTousDAOExceptionMessageNull() {

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
    @Tag(TAG_RECHERCHER_TOUS)
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
    @Tag(TAG_RECHERCHER_TOUS)
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
	@Tag(TAG_RECHERCHERTOUSPARPAGE)
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
    @Tag(TAG_RECHERCHERTOUSPARPAGE)
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
    @Tag(TAG_RECHERCHERTOUSPARPAGE)
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
    @Tag(TAG_RECHERCHERTOUSPARPAGE)
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
	@Tag(TAG_RECHERCHERTOUSPARPAGE)
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
    @Tag(TAG_RECHERCHERTOUSPARPAGE)
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
    @Tag(TAG_RECHERCHERTOUSPARPAGE)
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
    @Tag(TAG_RECHERCHERTOUSPARPAGE)
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
    @Tag(TAG_RECHERCHERTOUSPARPAGE)
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
    @Tag(TAG_RECHERCHERTOUSPARPAGE)
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
    @Tag(TAG_RECHERCHERTOUSPARPAGE)
    @DisplayName("rechercherTousParPage(OK) : garantit le bon fonctionnement de la pagination")
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
     * <p>garantit que findByObjetMetier(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL} ;</li>
     * <li>n'appelle ni le DAO parent, ni le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(null) - jette ExceptionAppliParamNull (contrat du port)")
    @Test
    public void testFindByObjetMetierNull() {

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel avec un objet métier null
         * jette une ExceptionAppliParamNull
         * avec le message MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel
         * des DAO.
         * - sousTypeProduitDaoJPA n'a jamais été appelé ;
         * - produitDaoJPA n'a jamais été appelé.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que findByObjetMetier(libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK} ;</li>
     * <li>n'appelle ni le DAO parent, ni le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testFindByObjetMetierLibelleBlank() {

        /* ARRANGE :
         * prépare un objet métier dont le libellé est blank,
         * afin de vérifier le contrôle applicatif
         * effectué avant toute recherche réelle.
         */
        final SousTypeProduitI parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = new Produit();
        p.setProduit(BLANK);
        p.setSousTypeProduit(parent);

        /* ACT - ASSERT :
         * vérifie que l'appel avec un libellé blank
         * jette une ExceptionAppliLibelleBlank
         * avec le message MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(p))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel
         * des DAO.
         * - sousTypeProduitDaoJPA n'a jamais été appelé ;
         * - produitDaoJPA n'a jamais été appelé.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParentNull} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL} ;</li>
     * <li>n'appelle ni le DAO parent, ni le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(parent null) - jette ExceptionAppliParentNull (contrat du port)")
    @Test
    public void testFindByObjetMetierParentNull() {

        /* ARRANGE :
         * prépare un objet métier avec un libellé valide,
         * mais sans parent,
         * afin de vérifier le contrôle applicatif
         * effectué avant toute recherche réelle.
         */
        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(null);

        /* ACT - ASSERT :
         * vérifie que l'appel avec un parent null
         * jette une ExceptionAppliParentNull
         * avec le message MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(p))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel
         * des DAO.
         * - sousTypeProduitDaoJPA n'a jamais été appelé ;
         * - produitDaoJPA n'a jamais été appelé.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'appelle ni le DAO parent, ni le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testFindByObjetMetierParentLibelleBlank() {

        /* ARRANGE :
         * prépare un objet métier avec un libellé valide,
         * mais avec un parent dont le libellé est blank,
         * afin de vérifier le contrôle applicatif
         * effectué avant toute recherche réelle.
         */
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(BLANK);

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        /* ACT - ASSERT :
         * vérifie que l'appel avec un parent dont le libellé est blank
         * jette une ExceptionAppliLibelleBlank
         * avec le message MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(p))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel
         * des DAO.
         * - sousTypeProduitDaoJPA n'a jamais été appelé ;
         * - produitDaoJPA n'a jamais été appelé.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent ;</li>
     * <li>n'appelle ni le DAO parent, ni le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(parent ID null) - jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testFindByObjetMetierParentIdNull() {

        /* ARRANGE :
         * prépare un objet métier dont le parent n'est pas persistant
         * car son identifiant est null,
         * afin de vérifier le contrôle de persistance
         * avant tout appel de DAO imposé par le contrat du port.
         */
        final SousTypeProduit parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        parent.setIdSousTypeProduit(null);

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        /* ACT - ASSERT :
         * vérifie que l'appel avec un parent non persistant
         * car son identifiant est null
         * jette une ExceptionTechniqueGatewayNonPersistent
         * avec le message MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT
         * complété par le libellé du parent
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(p))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT
                    + VETEMENT_HOMME);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par le contrôle de persistance du parent avant tout appel
         * des DAO.
         * - sousTypeProduitDaoJPA n'a jamais été appelé ;
         * - produitDaoJPA n'a jamais été appelé.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent absent du stockage) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>n'appelle pas le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(parent absent du stockage) - jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testFindByObjetMetierParentAbsent() {

        /* ARRANGE :
         * prépare un objet métier avec un libellé valide,
         * et un parent persistant en apparence,
         * mais absent du DAO parent mocké avec Mockito.
         */
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        /* Configure le DAO parent mocké avec Mockito
         * pour que findById(1L)
         * retourne Optional.empty().
         */
        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.empty());

        /* ACT - ASSERT :
         * vérifie que l'appel avec un parent persistant en apparence
         * mais absent du stockage
         * jette une ExceptionTechniqueGatewayNonPersistent
         * avec le message MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT
         * complété par le libellé du parent
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(p))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT
                    + VETEMENT_HOMME);

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
     * <p>garantit que findByObjetMetier(KO DAO parent message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine du DAO parent ;</li>
     * <li>propage comme cause l'exception technique d'origine ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>n'appelle pas le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(KO DAO parent message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByObjetMetierParentDAOExceptionMessageNonNull() {

        /* ARRANGE :
         * prépare un objet métier valide
         * afin d'atteindre réellement la vérification du parent
         * dans le stockage.
         */
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        /* Configure le DAO parent mocké avec Mockito
         * pour que findById(1L)
         * jette une RuntimeException avec message non null.
         */
        final RuntimeException causeDao = new RuntimeException(BOOM);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois service.findByObjetMetier(p)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable
            = Assertions.catchThrowable(
                    () -> this.service.findByObjetMetier(p));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son message contractuel sécurisé
         * et le message technique d'origine du DAO parent.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);

        /* Vérifie que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - le DAO parent a été appelé une fois avec 1L ;
         * - le DAO objet métier n'a jamais été appelé.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(KO DAO parent message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique ;</li>
     * <li>propage comme cause l'exception technique d'origine ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>n'appelle pas le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(KO DAO parent message null) - jette ExceptionTechniqueGateway avec message sûr non nul")
    @Test
    public void testFindByObjetMetierParentDAOExceptionMessageNull() {

        /* ARRANGE :
         * prépare un objet métier valide
         * afin d'atteindre réellement la vérification du parent
         * dans le stockage.
         */
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        /* Configure le DAO parent mocké avec Mockito
         * pour que findById(1L)
         * jette une RuntimeException sans message.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.sousTypeProduitDaoJPA.findById(1L)).thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois service.findByObjetMetier(p)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable
            = Assertions.catchThrowable(
                    () -> this.service.findByObjetMetier(p));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son message contractuel sécurisé
         * et le message sûr dérivé de l'exception sans message.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(RuntimeException.class.getName());

        /* Vérifie que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - le DAO parent a été appelé une fois avec 1L ;
         * - le DAO objet métier n'a jamais été appelé.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que findByObjetMetier(findAllBySousTypeProduit retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé.</li>
     * </ul>
     * <p>Ce test distingue un retour {@code null} du DAO objet métier
     * d'une liste vide valide : ici, le retour {@code null}
     * est traité comme une anomalie technique de stockage.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(findAllBySousTypeProduit retourne null) - jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testFindByObjetMetierDAORetourneNull() {

        /* ARRANGE :
         * prépare un objet métier valide
         * afin d'atteindre réellement la recherche
         * de l'objet métier dans le stockage.
         */
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        final SousTypeProduitJPA parentJPA = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        /* Configure le DAO parent mocké avec Mockito
         * pour que findById(1L)
         * retourne le parent JPA attendu.
         */
        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(parentJPA));

        /* Configure le DAO objet métier mocké avec Mockito
         * pour que findAllBySousTypeProduit(parentJPA)
         * retourne null.
         */
        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA)).thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que le retour null du DAO objet métier
         * n'est pas interprété comme une absence de résultat,
         * mais comme une anomalie technique de stockage.
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(p))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - le DAO parent a été appelé une fois avec 1L ;
         * - le DAO objet métier a été appelé une fois avec parentJPA.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findAllBySousTypeProduit(parentJPA);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que findByObjetMetier(KO DAO objet métier message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine du DAO objet métier ;</li>
     * <li>propage comme cause l'exception technique d'origine ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(KO DAO objet métier message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByObjetMetierDAOExceptionMessageNonNull() {

        /* ARRANGE :
         * prépare un objet métier valide
         * afin d'atteindre réellement la recherche
         * de l'objet métier dans le stockage.
         */
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        final SousTypeProduitJPA parentJPA = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        /* Configure le DAO parent mocké avec Mockito
         * pour que findById(1L)
         * retourne le parent JPA attendu.
         */
        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(parentJPA));

        /* Configure le DAO objet métier mocké avec Mockito
         * pour que findAllBySousTypeProduit(parentJPA)
         * jette une RuntimeException avec message non null.
         */
        final RuntimeException causeDao = new RuntimeException(BOOM);

        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA)).thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois service.findByObjetMetier(p)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable
            = Assertions.catchThrowable(
                    () -> this.service.findByObjetMetier(p));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son message contractuel sécurisé
         * et le message technique d'origine du DAO objet métier.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);

        /* Vérifie que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - le DAO parent a été appelé une fois avec 1L ;
         * - le DAO objet métier a été appelé une fois avec parentJPA.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findAllBySousTypeProduit(parentJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByObjetMetier(KO DAO objet métier message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique ;</li>
     * <li>propage comme cause l'exception technique d'origine ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(KO DAO objet métier message null) - jette ExceptionTechniqueGateway avec message sûr non nul")
    @Test
    public void testFindByObjetMetierDAOExceptionMessageNull() {

        /* ARRANGE :
         * prépare un objet métier valide
         * afin d'atteindre réellement la recherche
         * de l'objet métier dans le stockage.
         */
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        final SousTypeProduitJPA parentJPA = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        /* Configure le DAO parent mocké avec Mockito
         * pour que findById(1L)
         * retourne le parent JPA attendu.
         */
        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(parentJPA));

        /* Configure le DAO objet métier mocké avec Mockito
         * pour que findAllBySousTypeProduit(parentJPA)
         * jette une RuntimeException sans message.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA)).thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois service.findByObjetMetier(p)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable
            = Assertions.catchThrowable(
                    () -> this.service.findByObjetMetier(p));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son message contractuel sécurisé
         * et le message sûr dérivé de l'exception sans message.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(RuntimeException.class.getName());

        /* Vérifie que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - le DAO parent a été appelé une fois avec 1L ;
         * - le DAO objet métier a été appelé une fois avec parentJPA.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findAllBySousTypeProduit(parentJPA);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que findByObjetMetier(non trouvé) :</p>
     * <ul>
     * <li>ne jette aucune exception ;</li>
     * <li>retourne {@code null} lorsque la liste non null
     * retournée par le DAO objet métier ne contient pas
     * le libellé recherché ;</li>
     * <li>ignore les éléments {@code null} présents dans la liste retournée ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(non trouvé) - délègue aux DAO et retourne null")
    @Test
    public void testFindByObjetMetierNonTrouve() throws Exception {

        /* ARRANGE :
         * prépare un objet métier valide
         * afin d'atteindre réellement la recherche
         * de l'objet métier dans le stockage.
         */
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME);
        p.setSousTypeProduit(parent);

        final SousTypeProduitJPA parentJPA = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        /* Configure le DAO parent mocké avec Mockito
         * pour que findById(1L)
         * retourne le parent JPA attendu.
         */
        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(parentJPA));

        final List<ProduitJPA> entities = new ArrayList<ProduitJPA>();
        entities.add(null);
        entities.add(this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME));
        entities.add(this.fabriquerProduitJPA(SWEAT_HOMME, VETEMENT_HOMME));

        /* Configure le DAO objet métier mocké avec Mockito
         * pour que findAllBySousTypeProduit(parentJPA)
         * retourne une liste non null
         * contenant un élément null
         * et des objets métier ne correspondant pas
         * au libellé recherché.
         */
        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA)).thenReturn(entities);

        /* ACT :
         * appelle service.findByObjetMetier(p)
         * dans le scénario où aucun objet métier correspondant
         * n'est présent dans la liste retournée.
         */
        final Produit retour = this.service.findByObjetMetier(p);

        /* ASSERT :
         * vérifie que l'absence d'objet métier correspondant
         * est représentée par un retour null,
         * sans exception.
         */
        assertThat(retour).isNull();

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - le DAO parent a été appelé une fois avec 1L ;
         * - le DAO objet métier a été appelé une fois avec parentJPA.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findAllBySousTypeProduit(parentJPA);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que findByObjetMetier(OK) :</p>
     * <ul>
     * <li>ne jette aucune exception ;</li>
     * <li>retourne un {@link Produit} non null lorsque la liste non null
     * retournée par le DAO objet métier contient le libellé recherché ;</li>
     * <li>ignore les éléments {@code null} présents dans la liste retournée ;</li>
     * <li>recherche le libellé sans tenir compte de la casse ;</li>
     * <li>retourne l'identifiant objet métier attendu ;</li>
     * <li>retourne le libellé objet métier attendu ;</li>
     * <li>retourne un parent non null ;</li>
     * <li>retourne l'identifiant parent attendu ;</li>
     * <li>retourne le libellé parent attendu ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(OK) - retourne l'objet métier correspondant")
    @Test
    public void testFindByObjetMetierNominal() throws Exception {

        /* ARRANGE :
         * prépare un objet métier valide
         * avec un libellé volontairement en majuscules,
         * afin de vérifier la recherche nominale
         * sans tenir compte de la casse.
         */
        final SousTypeProduitI parent = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = new Produit();
        p.setProduit(CHEMISE_ML_HOMME.toUpperCase(Locale.ROOT));
        p.setSousTypeProduit(parent);

        final SousTypeProduitJPA parentJPA = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        /* Configure le DAO parent mocké avec Mockito
         * pour que findById(1L)
         * retourne le parent JPA attendu.
         */
        when(this.sousTypeProduitDaoJPA.findById(1L)).thenReturn(Optional.of(parentJPA));

        final ProduitJPA autreProduitJPA =
                this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        autreProduitJPA.setIdProduit(2L);

        final ProduitJPA produitTrouveJPA =
                this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        produitTrouveJPA.setIdProduit(3L);

        final List<ProduitJPA> entities = new ArrayList<ProduitJPA>();
        entities.add(null);
        entities.add(autreProduitJPA);
        entities.add(produitTrouveJPA);

        /* Configure le DAO objet métier mocké avec Mockito
         * pour que findAllBySousTypeProduit(parentJPA)
         * retourne une liste non null
         * contenant un élément null,
         * un objet métier non correspondant,
         * puis l'objet métier correspondant.
         */
        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA)).thenReturn(entities);

        /* ACT :
         * appelle service.findByObjetMetier(p)
         * dans le scénario nominal où l'objet métier correspondant
         * est présent dans la liste retournée.
         */
        final Produit retour = this.service.findByObjetMetier(p);

        /* ASSERT :
         * vérifie que l'objet métier correspondant
         * est retrouvé, converti et retourné.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getIdProduit()).isEqualTo(3L);
        assertThat(retour.getProduit()).isEqualTo(CHEMISE_ML_HOMME);

        /* Vérifie ensuite que le parent métier retourné
         * est non null et cohérent avec le parent JPA trouvé.
         */
        assertThat(retour.getSousTypeProduit()).isNotNull();
        assertThat(retour.getSousTypeProduit().getIdSousTypeProduit()).isEqualTo(1L);
        assertThat(retour.getSousTypeProduit().getSousTypeProduit()).isEqualTo(VETEMENT_HOMME);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - le DAO parent a été appelé une fois avec 1L ;
         * - le DAO objet métier a été appelé une fois avec parentJPA.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findAllBySousTypeProduit(parentJPA);

    } // __________________________________________________________________
    

    
    // ========================== findByLibelle ===========================
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelle(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK} ;</li>
     * <li>n'interagit avec aucun composant de stockage mocké.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLE)
    @DisplayName("findByLibelle(null) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testFindByLibelleNull() {

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel this.service.findByLibelle(null)
         * jette une ExceptionAppliLibelleBlank
         * avec le message MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.findByLibelle(null))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK);

        /* Vérifie qu'aucun accès au stockage n'a été tenté.
         * Le contrôle applicatif sur le libellé null doit interrompre
         * le traitement avant tout appel au DAO parent
         * ou au DAO objet métier.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelle(blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK} ;</li>
     * <li>n'interagit avec aucun composant de stockage mocké.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLE)
    @DisplayName("findByLibelle(blank) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testFindByLibelleBlank() {

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel this.service.findByLibelle(BLANK)
         * jette une ExceptionAppliLibelleBlank
         * avec le message MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.findByLibelle(BLANK))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK);

        /* Vérifie qu'aucun accès au stockage n'a été tenté.
         * Le contrôle applicatif sur le libellé blank doit interrompre
         * le traitement avant tout appel au DAO parent
         * ou au DAO objet métier.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelle(DAO retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon libellé exact ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>n'appelle jamais la recherche rapide DAO ;</li>
     * <li>ne sollicite pas le DAO parent.</li>
     * </ul>
     * <p>Ce test distingue un retour {@code null} du DAO objet métier
     * d'une liste vide valide : ici, le retour {@code null}
     * est traité comme une anomalie technique de stockage.</p>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLE)
    @DisplayName("findByLibelle(DAO retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testFindByLibelleDAORetourneNull() {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito avec le libellé CHEMISE_ML_HOMME via
         * findByProduitIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra répondre null".
         *
         * On simule donc volontairement un défaut technique du stockage :
         * pour une recherche par libellé exact non blank,
         * le DAO objet métier ne retourne pas une liste vide valide,
         * mais une valeur null interdite par le contrat.
         */
        when(this.produitDaoJPA.findByProduitIgnoreCase(CHEMISE_ML_HOMME))
            .thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que le retour null du DAO objet métier
         * n'est pas interprété comme une absence de résultat,
         * mais comme une anomalie technique de stockage.
         */
        assertThatThrownBy(() -> this.service.findByLibelle(CHEMISE_ML_HOMME))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon libellé exact.
         */
        verify(this.produitDaoJPA, times(1))
            .findByProduitIgnoreCase(CHEMISE_ML_HOMME);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.produitDaoJPA, never()).findAll();

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via la recherche rapide dédiée
         * findByProduitContainingIgnoreCase(...).
         */
        verify(this.produitDaoJPA, never())
            .findByProduitContainingIgnoreCase(any(String.class));

        /* Garantit que ce scénario ne sollicite pas le DAO parent.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelle(KO DAO message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon libellé exact ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>n'appelle jamais la recherche rapide DAO ;</li>
     * <li>ne sollicite pas le DAO parent.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLE)
    @DisplayName("findByLibelle(KO DAO message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByLibelleDAOExceptionMessageNonNull() {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito avec le libellé CHEMISE_ML_HOMME via
         * findByProduitIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra jeter
         * une RuntimeException avec message non null".
         *
         * On simule donc volontairement une panne technique du stockage
         * lors de la recherche exacte DAO.
         */
        final RuntimeException causeDao = new RuntimeException(BOOM);

        when(this.produitDaoJPA.findByProduitIgnoreCase(CHEMISE_ML_HOMME))
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois this.service.findByLibelle(CHEMISE_ML_HOMME)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.findByLibelle(CHEMISE_ML_HOMME));

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

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon libellé exact.
         */
        verify(this.produitDaoJPA, times(1))
            .findByProduitIgnoreCase(CHEMISE_ML_HOMME);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.produitDaoJPA, never()).findAll();

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via la recherche rapide dédiée
         * findByProduitContainingIgnoreCase(...).
         */
        verify(this.produitDaoJPA, never())
            .findByProduitContainingIgnoreCase(any(String.class));

        /* Garantit que ce scénario ne sollicite pas le DAO parent.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelle(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon libellé exact ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>n'appelle jamais la recherche rapide DAO ;</li>
     * <li>ne sollicite pas le DAO parent.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLE)
    @DisplayName("findByLibelle(KO DAO message null) : jette ExceptionTechniqueGateway avec message sûr non nul")
    @Test
    public void testFindByLibelleDAOExceptionMessageNull() {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito avec le libellé CHEMISE_ML_HOMME via
         * findByProduitIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra jeter
         * une RuntimeException sans message".
         *
         * On simule donc volontairement une panne technique du stockage
         * dont le message d'origine vaut null.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.produitDaoJPA.findByProduitIgnoreCase(CHEMISE_ML_HOMME))
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois this.service.findByLibelle(CHEMISE_ML_HOMME)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.findByLibelle(CHEMISE_ML_HOMME));

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

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon libellé exact.
         */
        verify(this.produitDaoJPA, times(1))
            .findByProduitIgnoreCase(CHEMISE_ML_HOMME);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.produitDaoJPA, never()).findAll();

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via la recherche rapide dédiée
         * findByProduitContainingIgnoreCase(...).
         */
        verify(this.produitDaoJPA, never())
            .findByProduitContainingIgnoreCase(any(String.class));

        /* Garantit que ce scénario ne sollicite pas le DAO parent.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelle(non trouvé) :</p>
     * <ul>
     * <li>ne jette aucune exception ;</li>
     * <li>retourne une {@link List} non null ;</li>
     * <li>retourne une liste vide lorsque le DAO objet métier
     * retourne une liste vide ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon libellé exact ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>n'appelle jamais la recherche rapide DAO ;</li>
     * <li>ne sollicite pas le DAO parent.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYLIBELLE)
    @DisplayName("findByLibelle(non trouvé) : retourne une liste vide non null")
    @Test
    public void testFindByLibelleNonTrouve() throws Exception {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito avec le libellé CHEMISE_ML_HOMME via
         * findByProduitIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra répondre
         * une liste vide".
         *
         * On simule donc volontairement un stockage
         * dans lequel aucun objet métier ne correspond au libellé exact recherché.
         */
        when(this.produitDaoJPA.findByProduitIgnoreCase(CHEMISE_ML_HOMME))
            .thenReturn(new ArrayList<ProduitJPA>());

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock.
         */
        final List<Produit> resultat =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        /* ASSERT */
        /* Garantit que this.service.findByLibelle(CHEMISE_ML_HOMME)
         * retourne une liste non null et vide
         * lorsque le DAO objet métier ne trouve aucun résultat.
         */
        assertThat(resultat).isNotNull().isEmpty();

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon libellé exact.
         */
        verify(this.produitDaoJPA, times(1))
            .findByProduitIgnoreCase(CHEMISE_ML_HOMME);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.produitDaoJPA, never()).findAll();

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via la recherche rapide dédiée
         * findByProduitContainingIgnoreCase(...).
         */
        verify(this.produitDaoJPA, never())
            .findByProduitContainingIgnoreCase(any(String.class));

        /* Garantit que ce scénario ne sollicite pas le DAO parent.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelle(nominal) :</p>
     * <ul>
     * <li>délègue au DAO objet métier la recherche exacte via
     * {@code findByProduitIgnoreCase(...)} ;</li>
     * <li>retourne une {@link List} non null ;</li>
     * <li>filtre les éléments null issus du stockage ;</li>
     * <li>dédoublonne les résultats au sens métier
     * sur le couple produit / sous-type produit ;</li>
     * <li>retourne les objets métier triés par parent
     * puis par libellé produit ;</li>
     * <li>conserve le parent des objets métier retournés ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon libellé exact ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>n'appelle jamais la recherche rapide DAO ;</li>
     * <li>ne sollicite pas le DAO parent.</li>
     * </ul>
     * <p>Ce test prouve directement au niveau de la méthode publique
     * le filtrage des null,
     * le dédoublonnage métier sur le couple produit / sous-type produit
     * et le tri final par parent puis par libellé produit.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYLIBELLE)
    @DisplayName("findByLibelle(nominal) : filtre les null, trie et dédoublonne par couple métier")
    @Test
    public void testFindByLibelleNominal() throws Exception {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito avec le libellé CHEMISE_ML_HOMME via
         * findByProduitIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra répondre
         * une collection persistante non triée,
         * contenant un null
         * et un doublon métier sur le couple produit / sous-type produit".
         *
         * On simule donc volontairement un stockage
         * qui retourne :
         * - un élément null ;
         * - deux produits fonctionnellement identiques
         *   ne différant que par la casse du libellé ;
         * - deux parents distincts ;
         * - plusieurs valeurs non triées.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réponse
         * afin de prouver comment le service réagit
         * au cas contractuel "scénario nominal complet".
         */
        final ProduitJPA femmeChemiseLongues =
                this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_FEMME);
        femmeChemiseLongues.setIdProduit(11L);

        final ProduitJPA hommeChemiseLongues =
                this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        hommeChemiseLongues.setIdProduit(21L);

        final ProduitJPA doublonHommeChemiseLongues =
                this.fabriquerProduitJPA(
                        CHEMISE_ML_HOMME.toUpperCase(Locale.ROOT),
                        VETEMENT_HOMME);
        doublonHommeChemiseLongues.setIdProduit(999L);

        final List<ProduitJPA> entities = new ArrayList<ProduitJPA>();
        entities.add(hommeChemiseLongues);
        entities.add(null);
        entities.add(doublonHommeChemiseLongues);
        entities.add(femmeChemiseLongues);

        when(this.produitDaoJPA.findByProduitIgnoreCase(CHEMISE_ML_HOMME))
            .thenReturn(entities);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock.
         */
        final List<Produit> resultat =
                this.service.findByLibelle(CHEMISE_ML_HOMME);

        /* ASSERT */
        /* Garantit que this.service.findByLibelle(CHEMISE_ML_HOMME)
         * - retourne une liste non null
         * - ne contient plus aucun élément null
         * - dédoublonne les résultats au sens métier
         *   sur le couple produit / sous-type produit
         * - restitue le résultat final trié
         *   par parent puis par libellé produit.
         */
        assertThat(resultat).isNotNull().doesNotContainNull().hasSize(2);

        assertThat(resultat)
            .allSatisfy(produit ->
                assertThat(produit.getSousTypeProduit()).isNotNull());

        assertThat(resultat)
            .extracting(produit ->
                produit.getSousTypeProduit().getSousTypeProduit())
            .containsExactly(
                VETEMENT_FEMME,
                VETEMENT_HOMME);

        /* L'extraction normalisée en trim + lowerCase(Locale.ROOT)
         * est volontaire :
         * elle permet de prouver directement le dédoublonnage métier
         * sans sur-contraindre artificiellement
         * la casse conservée par l'implémentation.
         */
        assertThat(resultat)
            .extracting(produit -> produit.getProduit()
                .trim()
                .toLowerCase(Locale.ROOT))
            .containsExactly(
                CHEMISE_ML_HOMME.toLowerCase(Locale.ROOT),
                CHEMISE_ML_HOMME.toLowerCase(Locale.ROOT));

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon libellé exact.
         */
        verify(this.produitDaoJPA, times(1))
            .findByProduitIgnoreCase(CHEMISE_ML_HOMME);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.produitDaoJPA, never()).findAll();

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via la recherche rapide dédiée
         * findByProduitContainingIgnoreCase(...).
         */
        verify(this.produitDaoJPA, never())
            .findByProduitContainingIgnoreCase(any(String.class));

        /* Garantit que ce scénario ne sollicite pas le DAO parent.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    

    // ======================== findByLibelleRapide =======================
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL} ;</li>
     * <li>n'interagit avec aucun composant de stockage mocké.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(null) - jette ExceptionAppliParamNull (contrat du port)")
    @Test
    public void testFindByLibelleRapideNull() {

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel this.service.findByLibelleRapide(null)
         * jette une ExceptionAppliParamNull
         * avec le message MSG_FINDBYLIBELLERAPIDE_KO_PARAM_NULL
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_FINDBYLIBELLERAPIDE_KO_PARAM_NULL);

        /* Vérifie qu'aucun accès au stockage n'a été tenté.
         * Le contrôle applicatif sur le paramètre null doit interrompre
         * le traitement avant tout appel au DAO parent
         * ou au DAO objet métier.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que findByLibelleRapide(blank) :</p>
     * <ul>
     * <li>délègue au DAO objet métier la recherche complète 
     * via {@code findAll()} ;</li>
     * <li>retourne une {@link List} non null ;</li>
     * <li>retourne tous les objets métier issus du stockage ;</li>
     * <li>conserve le parent des objets métier retournés ;</li>
     * <li>n'appelle jamais la recherche rapide DAO dédiée 
     * DAO.findByProduitContainingIgnoreCase(...).</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(blank) : délègue à findAll()")
    @Test
    public void testFindByLibelleRapideBlank() throws Exception {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito via findAll(),
         * alors le DAO objet métier mocké avec Mockito devra répondre
         * une liste complète de ProduitJPA persistants".
         *
         * On simule donc volontairement un stockage
         * contenant plusieurs objets persistants,
         * afin de prouver que, lorsque le libellé rapide est blank,
         * le service ne lance pas une recherche filtrée
         * mais délègue bien au DAO objet métier une recherche complète.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réponse
         * afin de prouver comment le service réagit
         * au cas contractuel "blank délègue findAll()".
         */
        final ProduitJPA produitJPA1 =
                this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        produitJPA1.setIdProduit(1L);

        final ProduitJPA produitJPA2 =
                this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        produitJPA2.setIdProduit(2L);

        when(this.produitDaoJPA.findAll())
            .thenReturn(Arrays.asList(produitJPA1, produitJPA2));

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        final List<Produit> resultat =
                this.service.findByLibelleRapide(BLANK);

        /* ASSERT */
        /* Garantit que this.service.findByLibelleRapide(BLANK)
         * - retourne une liste non null
         * - retourne tous les objets métier issus du stockage
         *   lorsque le libellé rapide est blank.
         */
        assertThat(resultat).isNotNull().hasSize(2);

        /* Garantit que le contenu métier retourné
         * correspond exactement aux objets persistants issus du stockage
         * après conversion par le SERVICE GATEWAY.
         */
        assertThat(resultat)
            .extracting(Produit::getProduit)
            .containsExactly(CHEMISE_MC_HOMME, CHEMISE_ML_HOMME);

        /* Garantit que les objets métier retournés
         * conservent un parent non null et cohérent.
         */
        assertThat(resultat.get(0).getSousTypeProduit()).isNotNull();
        assertThat(resultat.get(0).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        assertThat(resultat.get(1).getSousTypeProduit()).isNotNull();
        assertThat(resultat.get(1).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * via findAll().
         */
        verify(this.produitDaoJPA, times(1)).findAll();

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via la recherche rapide dédiée
         * findByProduitContainingIgnoreCase(...).
         */
        verify(this.produitDaoJPA, never())
            .findByProduitContainingIgnoreCase(any(String.class));

        /* Garantit que ce scénario ne sollicite pas le DAO parent.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que findByLibelleRapide(DAO retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon contenu de recherche rapide ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>ne sollicite pas le DAO parent.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(DAO retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testFindByLibelleRapideDAORetourneNull() {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito avec le contenu CHEMISE via
         * findByProduitContainingIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra répondre null".
         *
         * On simule donc volontairement un défaut technique du stockage :
         * pour une recherche rapide non blank,
         * le DAO objet métier ne retourne pas une liste vide valide,
         * mais une valeur null interdite par le contrat.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réponse
         * afin de prouver comment le service réagit
         * au cas contractuel "DAO retourne null".
         */
        when(this.produitDaoJPA.findByProduitContainingIgnoreCase(CHEMISE))
            .thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que l'appel this.service.findByLibelleRapide(CHEMISE)
         * jette une ExceptionTechniqueGateway
         * avec le message MSG_ERREUR_TECH_KO_STOCKAGE
         * lorsque le DAO objet métier retourne null.
         */
        assertThatThrownBy(() -> this.service.findByLibelleRapide(CHEMISE))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon contenu de recherche rapide.
         */
        verify(this.produitDaoJPA, times(1))
            .findByProduitContainingIgnoreCase(CHEMISE);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.produitDaoJPA, never()).findAll();

        /* Garantit que ce scénario ne sollicite pas le DAO parent.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(KO DAO message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon contenu de recherche rapide ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>ne sollicite pas le DAO parent.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(KO DAO message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByLibelleRapideDAOExceptionMessageNonNull() {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito avec le contenu CHEMISE via
         * findByProduitContainingIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra jeter
         * une RuntimeException avec message non null".
         *
         * On simule donc volontairement une panne technique du stockage
         * lors de la recherche rapide DAO.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réponse
         * afin de prouver comment le service réagit
         * au cas contractuel "KO DAO message non null".
         */
        final RuntimeException causeDao = new RuntimeException(BOOM);

        when(this.produitDaoJPA.findByProduitContainingIgnoreCase(CHEMISE))
            .thenThrow(causeDao);

        /* ACT :
         * sollicite la méthode findByLibelleRapide(...)
         * dans les conditions imposées par le mock.
         *
         * Stocke l'Exception levée afin de vérifier :
         * - son type ;
         * - son message ;
         * - sa cause.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.findByLibelleRapide(CHEMISE));

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

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon contenu de recherche rapide.
         */
        verify(this.produitDaoJPA, times(1))
            .findByProduitContainingIgnoreCase(CHEMISE);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.produitDaoJPA, never()).findAll();

        /* Garantit que ce scénario ne sollicite pas le DAO parent.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelleRapide(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon contenu de recherche rapide ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>ne sollicite pas le DAO parent.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(KO DAO message null) : jette ExceptionTechniqueGateway avec message sûr non nul")
    @Test
    public void testFindByLibelleRapideDAOExceptionMessageNull() {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito avec le contenu CHEMISE via
         * findByProduitContainingIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra jeter
         * une RuntimeException sans message".
         *
         * On simule donc volontairement une panne technique du stockage
         * dont le message d'origine vaut null.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réponse
         * afin de prouver comment le service sécurise le message
         * au cas contractuel "KO DAO message null".
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.produitDaoJPA.findByProduitContainingIgnoreCase(CHEMISE))
            .thenThrow(causeDao);

        /* ACT :
         * sollicite la méthode findByLibelleRapide(...)
         * dans les conditions imposées par le mock.
         *
         * Stocke l'Exception levée afin de vérifier :
         * - son type ;
         * - son message sûr non null ;
         * - sa cause.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.findByLibelleRapide(CHEMISE));

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

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon contenu de recherche rapide.
         */
        verify(this.produitDaoJPA, times(1))
            .findByProduitContainingIgnoreCase(CHEMISE);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.produitDaoJPA, never()).findAll();

        /* Garantit que ce scénario ne sollicite pas le DAO parent.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelleRapide(non trouvé) :</p>
     * <ul>
     * <li>ne jette aucune exception ;</li>
     * <li>retourne une {@link List} non null ;</li>
     * <li>retourne une liste vide lorsque le DAO objet métier
     * retourne une liste vide ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon contenu de recherche rapide ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>ne sollicite pas le DAO parent.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(non trouvé) : retourne une liste vide non null")
    @Test
    public void testFindByLibelleRapideNonTrouve() throws Exception {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito avec le contenu CHEMISE via
         * findByProduitContainingIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra répondre
         * une liste vide".
         *
         * On simule donc volontairement un stockage
         * dans lequel aucun objet métier ne correspond au contenu recherché.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réponse
         * afin de prouver comment le service réagit
         * au cas contractuel "non trouvé".
         */
        when(this.produitDaoJPA.findByProduitContainingIgnoreCase(CHEMISE))
            .thenReturn(new ArrayList<ProduitJPA>());

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        final List<Produit> resultat =
                this.service.findByLibelleRapide(CHEMISE);

        /* ASSERT */
        /* Garantit que this.service.findByLibelleRapide(CHEMISE)
         * retourne une liste non null et vide
         * lorsque le DAO objet métier ne trouve aucun résultat.
         */
        assertThat(resultat).isNotNull().isEmpty();

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon contenu de recherche rapide.
         */
        verify(this.produitDaoJPA, times(1))
            .findByProduitContainingIgnoreCase(CHEMISE);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.produitDaoJPA, never()).findAll();

        /* Garantit que ce scénario ne sollicite pas le DAO parent.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelleRapide(nominal) :</p>
     * <ul>
     * <li>délègue au DAO objet métier la recherche rapide via
     * {@code findByProduitContainingIgnoreCase(...)} ;</li>
     * <li>retourne une {@link List} non null ;</li>
     * <li>filtre les éléments null issus du stockage ;</li>
     * <li>dédoublonne les résultats au sens métier
     * sur le couple produit / sous-type produit ;</li>
     * <li>retourne les objets métier triés par parent
     * puis par libellé produit ;</li>
     * <li>conserve le parent des objets métier retournés ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>ne sollicite pas le DAO parent.</li>
     * </ul>
     * <p>Ce test prouve directement au niveau de la méthode publique
     * le filtrage des null,
     * le dédoublonnage métier sur le couple produit / sous-type produit
     * et le tri final par parent puis par libellé produit.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(nominal) : filtre les null, trie et dédoublonne par couple métier")
    @Test
    public void testFindByLibelleRapideNominal() throws Exception {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito avec le contenu CHEMISE via
         * findByProduitContainingIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra répondre
         * une collection persistante non triée,
         * contenant un null
         * et un doublon métier sur le couple produit / sous-type produit".
         *
         * On simule donc volontairement un stockage
         * qui retourne :
         * - un élément null,
         * - deux produits fonctionnellement identiques
         *   ne différant que par la casse du libellé,
         * - deux parents distincts,
         * - plusieurs valeurs non triées.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réponse
         * afin de prouver comment le service réagit
         * au cas contractuel "scénario nominal complet".
         */
        final ProduitJPA femmeChemiseLongues =
                this.fabriquerProduitJPA(CHEMISE_ML_FEMME, VETEMENT_FEMME);
        femmeChemiseLongues.setIdProduit(11L);

        final ProduitJPA hommeChemiseCourtes =
                this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        hommeChemiseCourtes.setIdProduit(21L);

        final ProduitJPA doublonHommeChemiseCourtes =
                this.fabriquerProduitJPA(
                        CHEMISE_MC_HOMME.toUpperCase(Locale.ROOT),
                        VETEMENT_HOMME);
        doublonHommeChemiseCourtes.setIdProduit(999L);

        final ProduitJPA hommeChemiseLongues =
                this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        hommeChemiseLongues.setIdProduit(22L);

        final List<ProduitJPA> entities = new ArrayList<ProduitJPA>();
        entities.add(hommeChemiseLongues);
        entities.add(null);
        entities.add(hommeChemiseCourtes);
        entities.add(doublonHommeChemiseCourtes);
        entities.add(femmeChemiseLongues);

        when(this.produitDaoJPA.findByProduitContainingIgnoreCase(CHEMISE))
            .thenReturn(entities);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        final List<Produit> resultat =
                this.service.findByLibelleRapide(CHEMISE);

        /* ASSERT */
        /* Garantit que this.service.findByLibelleRapide(CHEMISE)
         * - retourne une liste non null
         * - ne contient plus aucun élément null
         * - dédoublonne les résultats au sens métier
         *   sur le couple produit / sous-type produit
         * - restitue le résultat final trié
         *   par parent puis par libellé produit.
         */
        assertThat(resultat).isNotNull().doesNotContainNull().hasSize(3);

        assertThat(resultat)
            .allSatisfy(produit ->
                assertThat(produit.getSousTypeProduit()).isNotNull());

        assertThat(resultat)
            .extracting(produit ->
                produit.getSousTypeProduit().getSousTypeProduit())
            .containsExactly(
                VETEMENT_FEMME,
                VETEMENT_HOMME,
                VETEMENT_HOMME);

        /* L'extraction normalisée en trim + lowerCase(Locale.ROOT)
         * est volontaire :
         * elle permet de prouver directement le dédoublonnage métier
         * sans sur-contraindre artificiellement
         * la casse conservée par l'implémentation.
         */
        assertThat(resultat)
            .extracting(produit -> produit.getProduit()
                .trim()
                .toLowerCase(Locale.ROOT))
            .containsExactly(
                CHEMISE_ML_FEMME.toLowerCase(Locale.ROOT),
                CHEMISE_MC_HOMME.toLowerCase(Locale.ROOT),
                CHEMISE_ML_HOMME.toLowerCase(Locale.ROOT));

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon contenu de recherche rapide.
         */
        verify(this.produitDaoJPA, times(1))
            .findByProduitContainingIgnoreCase(CHEMISE);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.produitDaoJPA, never()).findAll();

        /* Garantit que ce scénario ne sollicite pas le DAO parent.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    
    
    // ========================= findAllByParent ==========================
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParentNull} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL} ;</li>
     * <li>n'interagit avec aucun composant de stockage mocké.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(null) - jette ExceptionAppliParentNull (contrat du port)")
    @Test
    public void testFindAllByParentNull() {

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel this.service.findAllByParent(null)
         * jette une ExceptionAppliParentNull
         * avec le message MSG_FINDALLBYPARENT_KO_PARAM_NULL
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.findAllByParent(null))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_FINDALLBYPARENT_KO_PARAM_NULL);

        /* Vérifie qu'aucun accès au stockage n'a été tenté.
         * Le contrôle applicatif sur le parent null doit interrompre
         * le traitement avant tout appel au DAO parent
         * ou au DAO objet métier.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'interagit avec aucun composant de stockage mocké.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testFindAllByParentParentLibelleBlank() {

        /* ARRANGE :
         * prépare un parent dont le libellé est blank,
         * afin de vérifier le contrôle applicatif
         * effectué avant toute recherche réelle du parent.
         */
        final SousTypeProduit parent =
                this.fabriquerParentMetierPersistant(BLANK);

        /* ACT - ASSERT :
         * vérifie que l'appel this.service.findAllByParent(parent)
         * avec un parent ayant un libellé blank
         * jette une ExceptionAppliLibelleBlank
         * avec le message contractuel du port.
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(
                ProduitGatewayIService
                    .MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK);

        /* Vérifie qu'aucun accès au stockage n'a été tenté.
         * Le contrôle applicatif sur le libellé blank doit interrompre
         * le traitement avant tout appel au DAO parent
         * ou au DAO objet métier.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(parent ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent ;</li>
     * <li>n'interagit avec aucun composant de stockage mocké.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(parent ID null) - jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testFindAllByParentParentIdNull() {

        /* ARRANGE :
         * prépare un parent non persistant
         * dont l'identifiant est null,
         * afin de vérifier le contrôle de persistance
         * effectué avant toute recherche DAO.
         */
        final SousTypeProduit parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        parent.setIdSousTypeProduit(null);

        /* ACT - ASSERT :
         * vérifie que l'appel this.service.findAllByParent(parent)
         * avec un parent dont l'identifiant est null
         * jette une ExceptionTechniqueGatewayNonPersistent
         * avec le message contractuel complété par le libellé du parent.
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(
                ProduitGatewayIService
                    .MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
                + VETEMENT_HOMME);

        /* Vérifie qu'aucun accès au stockage n'a été tenté.
         * Le contrôle de persistance du parent doit interrompre
         * le traitement avant tout appel au DAO parent
         * ou au DAO objet métier.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(parent absent du stockage) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>n'appelle pas le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(parent absent du stockage) - jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testFindAllByParentParentAbsent() {

        /* ARRANGE :
         * prépare un parent persistant en apparence,
         * mais absent du DAO parent mocké avec Mockito.
         */
        final SousTypeProduit parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(1L)
         * sur le DAO parent mocké retourne Optional.empty().
         */
        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.empty());

        /* ACT - ASSERT :
         * vérifie que l'appel this.service.findAllByParent(parent)
         * avec un parent absent du stockage
         * jette une ExceptionTechniqueGatewayNonPersistent
         * avec le message contractuel complété par le libellé du parent.
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(
                ProduitGatewayIService
                    .MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
                + VETEMENT_HOMME);

        /* Garantit que le DAO parent mocké a bien été appelé une fois
         * avec le bon identifiant parent.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);

        /* Garantit que le DAO objet métier n'a jamais été appelé.
         */
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(KO DAO parent message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine du DAO parent ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>n'appelle pas le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(KO DAO parent message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindAllByParentParentDAOExceptionMessageNonNull() {

        /* ARRANGE :
         * prépare un parent persistant en apparence,
         * puis configure le DAO parent mocké avec Mockito
         * pour jeter une RuntimeException avec message non null.
         */
        final SousTypeProduit parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final RuntimeException causeDao = new RuntimeException(BOOM);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois this.service.findAllByParent(parent)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.findAllByParent(parent));

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

        /* Garantit que le DAO parent mocké a bien été appelé une fois
         * avec le bon identifiant parent.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);

        /* Garantit que le DAO objet métier n'a jamais été appelé.
         */
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(KO DAO parent message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>n'appelle pas le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(KO DAO parent message null) : jette ExceptionTechniqueGateway avec message sûr non nul")
    @Test
    public void testFindAllByParentParentDAOExceptionMessageNull() {

        /* ARRANGE :
         * prépare un parent persistant en apparence,
         * puis configure le DAO parent mocké avec Mockito
         * pour jeter une RuntimeException sans message.
         */
        final SousTypeProduit parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois this.service.findAllByParent(parent)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.findAllByParent(parent));

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

        /* Garantit que le DAO parent mocké a bien été appelé une fois
         * avec le bon identifiant parent.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);

        /* Garantit que le DAO objet métier n'a jamais été appelé.
         */
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(DAO objet métier retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé.</li>
     * </ul>
     * <p>Ce test distingue un retour {@code null} du DAO objet métier
     * d'une liste vide valide : ici, le retour {@code null}
     * est traité comme une anomalie technique de stockage.</p>
     * </div>
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(DAO objet métier retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testFindAllByParentDAORetourneNull() {

        /* ARRANGE :
         * prépare un parent persistant en apparence
         * afin d'atteindre réellement la recherche
         * des objets métier rattachés à ce parent.
         */
        final SousTypeProduit parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final SousTypeProduitJPA parentJPA =
                this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));

        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA))
            .thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que le retour null du DAO objet métier
         * n'est pas interprété comme une absence de résultat,
         * mais comme une anomalie technique de stockage.
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit que le DAO parent mocké a bien été appelé une fois.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le parent JPA trouvé.
         */
        verify(this.produitDaoJPA, times(1))
            .findAllBySousTypeProduit(parentJPA);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.produitDaoJPA, never()).findAll();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(KO DAO objet métier message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine du DAO objet métier ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(KO DAO objet métier message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindAllByParentDAOExceptionMessageNonNull() {

        /* ARRANGE :
         * prépare un parent persistant en apparence,
         * puis configure le DAO objet métier mocké avec Mockito
         * pour jeter une RuntimeException avec message non null.
         */
        final SousTypeProduit parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final SousTypeProduitJPA parentJPA =
                this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));

        final RuntimeException causeDao = new RuntimeException(BOOM);

        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA))
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois this.service.findAllByParent(parent)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.findAllByParent(parent));

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

        /* Garantit que le DAO parent mocké a bien été appelé une fois.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le parent JPA trouvé.
         */
        verify(this.produitDaoJPA, times(1))
            .findAllBySousTypeProduit(parentJPA);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.produitDaoJPA, never()).findAll();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(KO DAO objet métier message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(KO DAO objet métier message null) : jette ExceptionTechniqueGateway avec message sûr non nul")
    @Test
    public void testFindAllByParentDAOExceptionMessageNull() {

        /* ARRANGE :
         * prépare un parent persistant en apparence,
         * puis configure le DAO objet métier mocké avec Mockito
         * pour jeter une RuntimeException sans message.
         */
        final SousTypeProduit parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final SousTypeProduitJPA parentJPA =
                this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));

        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA))
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois this.service.findAllByParent(parent)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.findAllByParent(parent));

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

        /* Garantit que le DAO parent mocké a bien été appelé une fois.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le parent JPA trouvé.
         */
        verify(this.produitDaoJPA, times(1))
            .findAllBySousTypeProduit(parentJPA);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.produitDaoJPA, never()).findAll();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(non trouvé) :</p>
     * <ul>
     * <li>ne jette aucune exception ;</li>
     * <li>retourne une {@link List} non null ;</li>
     * <li>retourne une liste vide lorsque le DAO objet métier
     * retourne une liste vide ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(non trouvé) : retourne une liste vide non null")
    @Test
    public void testFindAllByParentNonTrouve() throws Exception {

        /* ARRANGE :
         * prépare un parent persistant en apparence,
         * puis configure le DAO objet métier mocké avec Mockito
         * pour retourner une liste vide.
         */
        final SousTypeProduit parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final SousTypeProduitJPA parentJPA =
                this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));

        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA))
            .thenReturn(new ArrayList<ProduitJPA>());

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock.
         */
        final List<Produit> resultat =
                this.service.findAllByParent(parent);

        /* ASSERT */
        /* Garantit que this.service.findAllByParent(parent)
         * retourne une liste non null et vide
         * lorsque le DAO objet métier ne trouve aucun résultat.
         */
        assertThat(resultat).isNotNull().isEmpty();

        /* Garantit que le DAO parent mocké a bien été appelé une fois.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le parent JPA trouvé.
         */
        verify(this.produitDaoJPA, times(1))
            .findAllBySousTypeProduit(parentJPA);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.produitDaoJPA, never()).findAll();

    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>Test didactique non contractuel.</p>
     * <p>garantit que findAllByParent(avec doublons fonctionnels) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>retourne une liste dédoublonnée ;</li>
     * <li>retourne les libellés produits attendus ;</li>
     * <li>conserve le parent des objets métier retournés ;</li>
     * <li>appelle la méthode sousTypeProduitDaoJPA.findById(...)
     * du DAO parent mocké avec Mockito ;</li>
     * <li>appelle la méthode produitDaoJPA.findAllBySousTypeProduit(...)
     * du DAO objet métier mocké avec Mockito ;</li>
     * <li>n'appelle jamais {@code findAll()}.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(avec doublons fonctionnels) - dédoublonne le résultat")
    @Test
    public void testFindAllByParentAvecDoublons() throws Exception {

        /* ARRANGE :
         * prépare un parent persistant
         * et une liste renvoyée par le DAO objet métier
         * contenant deux produits portant le même libellé
         * pour le même parent,
         * afin de vérifier le dédoublonnage fonctionnel.
         */
        final SousTypeProduit parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final SousTypeProduitJPA parentJPA =
                this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        final ProduitJPA produit1 =
                this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        produit1.setIdProduit(1L);

        final ProduitJPA produit2 =
                this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        produit2.setIdProduit(2L);

        final ProduitJPA produit3 =
                this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        produit3.setIdProduit(3L);

        final List<ProduitJPA> entities = new ArrayList<ProduitJPA>();
        entities.add(produit1);
        entities.add(produit2);
        entities.add(produit3);

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findById(1L)
         * sur le DAO parent mocké retourne Optional.of(parentJPA).
         */
        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));

        /* Condition du Mock produitDaoJPA :
         * L'appel produitDaoJPA.findAllBySousTypeProduit(parentJPA)
         * sur le DAO objet métier mocké retourne la liste entities.
         */
        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA))
            .thenReturn(entities);

        /* ACT :
         * appelle this.service.findAllByParent(parent)
         * dans le scénario où le DAO retourne
         * des doublons fonctionnels.
         */
        final List<Produit> retour = this.service.findAllByParent(parent);

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * une liste métier cohérente et dédoublonnée.
         */
        assertThat(retour).isNotNull().hasSize(2);

        assertThat(retour)
            .extracting(Produit::getProduit)
            .containsExactly(CHEMISE_MC_HOMME, CHEMISE_ML_HOMME);

        /* Vérifie que les objets métier retournés
         * conservent un parent non null et cohérent.
         */
        assertThat(retour.get(0).getSousTypeProduit()).isNotNull();
        assertThat(retour.get(0).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        assertThat(retour.get(1).getSousTypeProduit()).isNotNull();
        assertThat(retour.get(1).getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        /* Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findById(1L) a été appelée une fois.
         * - produitDaoJPA.findAllBySousTypeProduit(parentJPA) a été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1))
            .findAllBySousTypeProduit(parentJPA);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.produitDaoJPA, never()).findAll();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(nominal) :</p>
     * <ul>
     * <li>délègue au DAO objet métier la recherche par parent via
     * {@code findAllBySousTypeProduit(...)} ;</li>
     * <li>retourne une {@link List} non null ;</li>
     * <li>filtre les éléments null issus du stockage ;</li>
     * <li>dédoublonne les résultats au sens métier
     * sur le couple produit / sous-type produit ;</li>
     * <li>retourne les objets métier triés par libellé produit
     * pour le parent demandé ;</li>
     * <li>conserve le parent des objets métier retournés ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé.</li>
     * </ul>
     * <p>Ce test prouve directement au niveau de la méthode publique
     * le filtrage des null,
     * le dédoublonnage métier sur le couple produit / sous-type produit
     * et le tri final des produits rattachés au parent demandé.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(nominal) : filtre les null, trie et dédoublonne par couple métier")
    @Test
    public void testFindAllByParentNominal() throws Exception {

        /* ARRANGE :
         * configure ici le comportement des DAO mockés avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service vérifie d'abord
         * l'existence du parent via sousTypeProduitDaoJPA.findById(1L),
         * alors le DAO parent mocké avec Mockito devra répondre
         * Optional.of(parentJPA)".
         *
         * Puis :
         * "si le service appelle ensuite le DAO objet métier
         * mocké avec Mockito via findAllBySousTypeProduit(parentJPA),
         * alors le DAO objet métier mocké avec Mockito devra répondre
         * une collection persistante non triée,
         * contenant un null
         * et un doublon métier sur le couple produit / sous-type produit".
         *
         * Le but n'est pas de tester les DAO réels,
         * mais de maîtriser leurs réponses
         * afin de prouver comment le service réagit
         * au cas contractuel "scénario nominal complet".
         */
        final SousTypeProduit parent =
                this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final SousTypeProduitJPA parentJPA =
                this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));

        final ProduitJPA produitManchesLongues =
                this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        produitManchesLongues.setIdProduit(1L);

        final ProduitJPA produitManchesCourtes =
                this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        produitManchesCourtes.setIdProduit(2L);

        final ProduitJPA doublonManchesCourtes =
                this.fabriquerProduitJPA(
                        CHEMISE_MC_HOMME.toUpperCase(Locale.ROOT),
                        VETEMENT_HOMME);
        doublonManchesCourtes.setIdProduit(999L);

        final List<ProduitJPA> entities =
                new ArrayList<ProduitJPA>();
        entities.add(produitManchesLongues);
        entities.add(null);
        entities.add(doublonManchesCourtes);
        entities.add(produitManchesCourtes);

        when(this.produitDaoJPA.findAllBySousTypeProduit(parentJPA))
            .thenReturn(entities);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock.
         */
        final List<Produit> resultat =
                this.service.findAllByParent(parent);

        /* ASSERT */
        /* Garantit que this.service.findAllByParent(parent)
         * - retourne une liste non null
         * - ne contient plus aucun élément null
         * - dédoublonne les résultats au sens métier
         *   sur le couple produit / sous-type produit
         * - restitue le résultat final trié par libellé produit
         *   pour le parent demandé.
         */
        assertThat(resultat).isNotNull().doesNotContainNull().hasSize(2);

        assertThat(resultat)
            .allSatisfy(produit ->
                assertThat(produit.getSousTypeProduit()).isNotNull());

        assertThat(resultat)
            .extracting(produit ->
                produit.getSousTypeProduit().getSousTypeProduit())
            .containsExactly(
                VETEMENT_HOMME,
                VETEMENT_HOMME);

        /* L'extraction normalisée en trim + lowerCase(Locale.ROOT)
         * est volontaire :
         * elle permet de prouver directement le dédoublonnage métier
         * sans sur-contraindre artificiellement
         * la casse conservée par l'implémentation.
         */
        assertThat(resultat)
            .extracting(produit -> produit.getProduit()
                .trim()
                .toLowerCase(Locale.ROOT))
            .containsExactly(
                CHEMISE_MC_HOMME.toLowerCase(Locale.ROOT),
                CHEMISE_ML_HOMME.toLowerCase(Locale.ROOT));

        /* Garantit que le DAO parent mocké a bien été appelé une fois
         * avec le bon identifiant parent.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le parent JPA trouvé.
         */
        verify(this.produitDaoJPA, times(1))
            .findAllBySousTypeProduit(parentJPA);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.produitDaoJPA, never()).findAll();

    } // __________________________________________________________________
    

    
    // ============================ findById ==============================
    
    
    
    /**
     * <div>
     * <p>garantit que findById(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_FINDBYID_KO_PARAM_NULL} ;</li>
     * <li>n'interagit avec aucun composant de stockage mocké.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYID)
    @DisplayName("findById(null) - jette ExceptionAppliParamNull (contrat du port)")
    @Test
    public void testFindByIdNull() {

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel this.service.findById(null)
         * jette une ExceptionAppliParamNull
         * avec le message MSG_FINDBYID_KO_PARAM_NULL
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.findById(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_FINDBYID_KO_PARAM_NULL);

        /* Vérifie qu'aucun accès au stockage n'a été tenté.
         * Le contrôle applicatif sur l'identifiant null
         * doit interrompre le traitement avant tout appel
         * au DAO objet métier ou au DAO parent.
         */
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findById(DAO retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon identifiant ;</li>
     * <li>ne sollicite pas le DAO parent.</li>
     * </ul>
     * <p>Ce test distingue un retour {@code null} du DAO objet métier
     * d'un {@code Optional.empty()} valide : ici, le retour {@code null}
     * est traité comme une anomalie technique de stockage.</p>
     * </div>
     */
    @Tag(TAG_FINDBYID)
    @DisplayName("findById(DAO retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testFindByIdDAORetourneNull() {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito avec l'identifiant 1L via findById(...),
         * alors le DAO objet métier mocké avec Mockito devra répondre null".
         *
         * On simule donc volontairement un stockage
         * qui retourne null au lieu d'un Optional<ProduitJPA>.
         */
        when(this.produitDaoJPA.findById(1L)).thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que le retour null du DAO objet métier
         * n'est pas interprété comme une absence de résultat,
         * mais comme une anomalie technique de stockage.
         */
        assertThatThrownBy(() -> this.service.findById(1L))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon identifiant.
         */
        verify(this.produitDaoJPA, times(1)).findById(1L);

        /* Garantit que ce scénario ne sollicite pas le DAO parent.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findById(KO DAO message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon identifiant ;</li>
     * <li>ne sollicite pas le DAO parent.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYID)
    @DisplayName("findById(KO DAO message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByIdDAOExceptionMessageNonNull() {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito avec l'identifiant 1L via findById(...),
         * alors le DAO objet métier mocké avec Mockito devra lancer
         * une RuntimeException avec message non null".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant la recherche par identifiant.
         */
        final RuntimeException causeDao = new RuntimeException(BOOM);

        when(this.produitDaoJPA.findById(1L))
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois this.service.findById(1L)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.findById(1L));

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

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon identifiant.
         */
        verify(this.produitDaoJPA, times(1)).findById(1L);

        /* Garantit que ce scénario ne sollicite pas le DAO parent.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findById(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non null dérivé de l'exception technique ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon identifiant ;</li>
     * <li>ne sollicite pas le DAO parent.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYID)
    @DisplayName("findById(KO DAO message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testFindByIdDAOExceptionMessageNull() {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito avec l'identifiant 1L via findById(...),
         * alors le DAO objet métier mocké avec Mockito devra lancer
         * une RuntimeException sans message".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant la recherche par identifiant,
         * avec un message technique d'origine null.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.produitDaoJPA.findById(1L))
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois this.service.findById(1L)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.findById(1L));

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

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon identifiant.
         */
        verify(this.produitDaoJPA, times(1)).findById(1L);

        /* Garantit que ce scénario ne sollicite pas le DAO parent.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findById(non trouvé) :</p>
     * <ul>
     * <li>ne jette aucune exception ;</li>
     * <li>retourne {@code null}
     * lorsque le DAO objet métier retourne {@code Optional.empty()} ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon identifiant ;</li>
     * <li>ne sollicite pas le DAO parent.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYID)
    @DisplayName("findById(non trouvé) : retourne null")
    @Test
    public void testFindByIdNonTrouve() throws Exception {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito avec l'identifiant 999_999L via findById(...),
         * alors le DAO objet métier mocké avec Mockito devra répondre
         * Optional.empty()".
         *
         * On simule donc volontairement un stockage
         * qui ne trouve aucun objet métier persistant
         * pour cet identifiant.
         */
        when(this.produitDaoJPA.findById(999_999L))
            .thenReturn(Optional.empty());

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock.
         */
        final Produit resultat = this.service.findById(999_999L);

        /* ASSERT */
        /* Garantit que this.service.findById(999_999L)
         * retourne null
         * lorsque le stockage ne trouve aucun objet persistant
         * pour l'identifiant demandé.
         */
        assertThat(resultat).isNull();

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon identifiant.
         */
        verify(this.produitDaoJPA, times(1)).findById(999_999L);

        /* Garantit que ce scénario ne sollicite pas le DAO parent.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findById(OK) :</p>
     * <ul>
     * <li>délègue la recherche au DAO objet métier
     * avec le bon identifiant ;</li>
     * <li>retourne un objet métier non null ;</li>
     * <li>convertit correctement l'identifiant objet métier persistant ;</li>
     * <li>convertit correctement le libellé objet métier persistant ;</li>
     * <li>convertit et conserve le parent persistant ;</li>
     * <li>convertit et conserve le type du parent persistant ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon identifiant ;</li>
     * <li>ne sollicite pas le DAO parent.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYID)
    @DisplayName("findById(OK) : retourne l'objet métier converti avec son parent")
    @Test
    public void testFindByIdNominal() throws Exception {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito avec l'identifiant 1L via findById(...),
         * alors le DAO objet métier mocké avec Mockito devra répondre
         * Optional.of(...) contenant un ProduitJPA persistant
         * portant l'identifiant 1L, le libellé CHEMISE_ML_HOMME
         * et un parent SousTypeProduitJPA persistant".
         *
         * On simule donc volontairement un stockage
         * qui trouve un objet métier persistant pour cet identifiant.
         */
        final ProduitJPA entityJPA =
                this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        entityJPA.setIdProduit(1L);

        when(this.produitDaoJPA.findById(1L))
            .thenReturn(Optional.of(entityJPA));

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock.
         */
        final Produit resultat = this.service.findById(1L);

        /* ASSERT */
        /* Garantit que this.service.findById(1L)
         * - délègue la recherche au DAO objet métier avec l'identifiant 1L ;
         * - retourne un objet métier non null ;
         * - convertit correctement l'identifiant et le libellé
         *   de l'objet persistant trouvé ;
         * - convertit et conserve le parent persistant ;
         * - convertit et conserve le type du parent persistant.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdProduit()).isEqualTo(1L);
        assertThat(resultat.getProduit()).isEqualTo(CHEMISE_ML_HOMME);

        assertThat(resultat.getSousTypeProduit()).isNotNull();
        assertThat(resultat.getSousTypeProduit().getIdSousTypeProduit())
            .isEqualTo(1L);
        assertThat(resultat.getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        assertThat(resultat.getSousTypeProduit().getTypeProduit()).isNotNull();
        assertThat(resultat.getSousTypeProduit().getTypeProduit().getIdTypeProduit())
            .isEqualTo(1L);
        assertThat(resultat.getSousTypeProduit().getTypeProduit().getTypeProduit())
            .isEqualTo(VETEMENT);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon identifiant.
         */
        verify(this.produitDaoJPA, times(1)).findById(1L);

        /* Garantit que ce scénario ne sollicite pas le DAO parent.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    

    
    // ============================= update ===============================



    /**
     * <div>
     * <p>garantit que update(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull}</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_UPDATE_KO_PARAM_NULL}</li>
     * <li>n'appelle ni le DAO parent ni le DAO objet métier</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(null) : jette ExceptionAppliParamNull et n'appelle pas les DAO")
    @Test
    public void testUpdateNull() {

        /* ARRANGE - ACT - ASSERT */
        /* Garantit que this.service.update(null)
         * - jette une ExceptionAppliParamNull
         * - émet un message MSG_UPDATE_KO_PARAM_NULL.
         */
        assertThatThrownBy(() -> this.service.update(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_UPDATE_KO_PARAM_NULL);

        /* Garantit que les DAO mockés n'ont pas été appelés. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA, this.produitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(libellé null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank}</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_BLANK}</li>
     * <li>n'appelle ni le DAO parent ni le DAO objet métier</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(libellé null) : jette ExceptionAppliLibelleBlank et n'appelle pas les DAO")
    @Test
    public void testUpdateLibelleNull() {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p
            = this.fabriquerProduitMetier(null, 1L, parent);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(p)
         * - jette une ExceptionAppliLibelleBlank
         * - émet un message MSG_UPDATE_KO_LIBELLE_BLANK.
         */
        assertThatThrownBy(() -> this.service.update(p))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_UPDATE_KO_LIBELLE_BLANK);

        /* Garantit que les DAO mockés n'ont pas été appelés. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA, this.produitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank}</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_BLANK}</li>
     * <li>n'appelle ni le DAO parent ni le DAO objet métier</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(libellé blank) : jette ExceptionAppliLibelleBlank et n'appelle pas les DAO")
    @Test
    public void testUpdateLibelleBlank() {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p
            = this.fabriquerProduitMetier(BLANK, 1L, parent);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(p)
         * - jette une ExceptionAppliLibelleBlank
         * - émet un message MSG_UPDATE_KO_LIBELLE_BLANK.
         */
        assertThatThrownBy(() -> this.service.update(p))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_UPDATE_KO_LIBELLE_BLANK);

        /* Garantit que les DAO mockés n'ont pas été appelés. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA, this.produitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNonPersistent}</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_UPDATE_KO_NON_PERSISTENT}
     * suivi du libellé de l'objet métier</li>
     * <li>n'appelle ni le DAO parent ni le DAO objet métier</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(ID null) : jette ExceptionAppliParamNonPersistent et n'appelle pas les DAO")
    @Test
    public void testUpdateIdNull() {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p
            = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, null, parent);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(p)
         * - jette une ExceptionAppliParamNonPersistent
         * - émet un message MSG_UPDATE_PREFIX_NON_PERSISTENT
         *   suivi du libellé de l'objet métier.
         */
        assertThatThrownBy(() -> this.service.update(p))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(MSG_UPDATE_PREFIX_NON_PERSISTENT
                    + safeMessage(CHEMISE_ML_HOMME));

        /* Garantit que les DAO mockés n'ont pas été appelés. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA, this.produitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(parent null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParentNull}</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_UPDATE_KO_PARENT_NULL}</li>
     * <li>n'appelle ni le DAO parent ni le DAO objet métier</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent null) : jette ExceptionAppliParentNull et n'appelle pas les DAO")
    @Test
    public void testUpdateParentNull() {

        /* ARRANGE */
        final Produit p
            = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, 1L, null);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(p)
         * - jette une ExceptionAppliParentNull
         * - émet un message MSG_UPDATE_KO_PARENT_NULL.
         */
        assertThatThrownBy(() -> this.service.update(p))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_UPDATE_KO_PARENT_NULL);

        /* Garantit que les DAO mockés n'ont pas été appelés. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA, this.produitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank}</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK}</li>
     * <li>n'appelle ni le DAO parent ni le DAO objet métier</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent libellé blank) : jette ExceptionAppliLibelleBlank et n'appelle pas les DAO")
    @Test
    public void testUpdateParentLibelleBlank() {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(BLANK);
        final Produit p
            = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, 1L, parent);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(p)
         * - jette une ExceptionAppliLibelleBlank
         * - émet le message contractuel du port
         *   pour un libellé parent blank.
         */
        assertThatThrownBy(() -> this.service.update(p))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK);

        /* Garantit que les DAO mockés n'ont pas été appelés. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA, this.produitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(parent ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent}</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent</li>
     * <li>n'appelle ni le DAO parent ni le DAO objet métier</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent ID null) : jette ExceptionTechniqueGatewayNonPersistent et n'appelle pas les DAO")
    @Test
    public void testUpdateParentIdNull() {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        parent.setIdSousTypeProduit(null);

        final Produit p
            = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, 1L, parent);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(p)
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet un message MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT
         *   suivi du libellé parent.
         */
        assertThatThrownBy(() -> this.service.update(p))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT
                    + VETEMENT_HOMME);

        /* Garantit que les DAO mockés n'ont pas été appelés. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA, this.produitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(parent absent du stockage) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent}</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>n'appelle pas le DAO objet métier</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent absent du stockage) : jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testUpdateParentAbsent() {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p
            = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, 1L, parent);

        /* Simule un parent absent du stockage. */
        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.empty());

        /* ACT - ASSERT */
        /* Garantit que this.service.update(p)
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet un message MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT
         *   suivi du libellé parent.
         */
        assertThatThrownBy(() -> this.service.update(p))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(ProduitGatewayIService.MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT
                    + VETEMENT_HOMME);

        /* Garantit que le DAO parent a bien été appelé une fois. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);

        /* Garantit que l'absence du parent arrête le traitement
         * avant tout accès au DAO objet métier.
         */
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(KO DAO parent message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>n'appelle pas le DAO objet métier</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO parent message non null) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testUpdateParentDAOExceptionMessageNonNull() {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p
            = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, 1L, parent);

        final RuntimeException causeDao = new RuntimeException(BOOM);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenThrow(causeDao);

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.update(p));

        /* ASSERT */
        /* Garantit que this.service.update(p)
         * - jette une ExceptionTechniqueGateway
         * - conserve le message technique d'origine BOOM
         * - propage la cause DAO.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit que le DAO parent a bien été appelé une fois. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);

        /* Garantit que l'échec technique du DAO parent
         * arrête le traitement avant tout accès au DAO objet métier.
         */
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(KO DAO parent message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non null dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>n'appelle pas le DAO objet métier</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO parent message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testUpdateParentDAOExceptionMessageNull() {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p
            = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, 1L, parent);

        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenThrow(causeDao);

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.update(p));

        /* ASSERT */
        /* Garantit que this.service.update(p)
         * - jette une ExceptionTechniqueGateway
         * - émet un message sûr non null
         * - propage la cause DAO.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(RuntimeException.class.getName());
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit que le DAO parent a bien été appelé une fois. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);

        /* Garantit que l'échec technique du DAO parent
         * arrête le traitement avant tout accès au DAO objet métier.
         */
        verifyNoInteractions(this.produitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(DAO.findById(...) objet métier retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE}</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>appelle le DAO objet métier une fois via {@code findById(...)}</li>
     * <li>ne déclenche aucune sauvegarde</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(DAO.findById(...) objet métier retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testUpdateDAOFindByIdRetourneNull() {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p
            = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, 1L, parent);

        final SousTypeProduitJPA parentJPA
            = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));

        /* Simule un stockage qui retourne null au lieu
         * d'un Optional<ProduitJPA>.
         */
        when(this.produitDaoJPA.findById(1L)).thenReturn(null);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(p)
         * - jette une ExceptionTechniqueGateway
         * - émet un message ERREUR_TECHNIQUE_KO_STOCKAGE.
         */
        assertThatThrownBy(() -> this.service.update(p))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findById(1L);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée. */
        verify(this.produitDaoJPA, never()).save(any(ProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(objet métier absent du stockage) :</p>
     * <ul>
     * <li>retourne {@code null}</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>appelle le DAO objet métier une fois via {@code findById(...)}</li>
     * <li>ne déclenche aucune sauvegarde</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(objet métier absent du stockage) : retourne null et ne sauvegarde pas")
    @Test
    public void testUpdateAbsent() throws Exception {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p
            = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, 1L, parent);

        final SousTypeProduitJPA parentJPA
            = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));

        /* Simule un stockage qui ne trouve aucun objet métier
         * à modifier pour l'identifiant demandé.
         */
        when(this.produitDaoJPA.findById(1L))
            .thenReturn(Optional.empty());

        /* ACT */
        final Produit resultat = this.service.update(p);

        /* ASSERT */
        /* Garantit que this.service.update(p)
         * retourne null lorsque l'objet métier est absent du stockage.
         */
        assertThat(resultat).isNull();

        /* Garantit les appels DAO réellement attendus. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findById(1L);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée. */
        verify(this.produitDaoJPA, never()).save(any(ProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(KO DAO objet métier findById message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>appelle le DAO objet métier une fois via {@code findById(...)}</li>
     * <li>ne déclenche aucune sauvegarde</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO objet métier findById message non null) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testUpdateDAOFindByIdExceptionMessageNonNull() {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p
            = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, 1L, parent);

        final SousTypeProduitJPA parentJPA
            = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);
        final RuntimeException causeDao = new RuntimeException(BOOM);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));
        when(this.produitDaoJPA.findById(1L))
            .thenThrow(causeDao);

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.update(p));

        /* ASSERT */
        /* Garantit que this.service.update(p)
         * - jette une ExceptionTechniqueGateway
         * - conserve le message technique d'origine BOOM
         * - propage la cause DAO.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findById(1L);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée. */
        verify(this.produitDaoJPA, never()).save(any(ProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(KO DAO objet métier findById message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non null dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>appelle le DAO objet métier une fois via {@code findById(...)}</li>
     * <li>ne déclenche aucune sauvegarde</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO objet métier findById message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testUpdateDAOFindByIdExceptionMessageNull() {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p
            = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, 1L, parent);

        final SousTypeProduitJPA parentJPA
            = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));
        when(this.produitDaoJPA.findById(1L))
            .thenThrow(causeDao);

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.update(p));

        /* ASSERT */
        /* Garantit que this.service.update(p)
         * - jette une ExceptionTechniqueGateway
         * - émet un message sûr non null
         * - propage la cause DAO.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(RuntimeException.class.getName());
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findById(1L);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée. */
        verify(this.produitDaoJPA, never()).save(any(ProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(DAO.save(...) retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE}</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>appelle le DAO objet métier une fois via {@code findById(...)}</li>
     * <li>déclenche une tentative de sauvegarde via {@code save(...)}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(DAO.save(...) retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testUpdateDAOSaveRetourneNull() {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p
            = this.fabriquerProduitMetier(
                    CHEMISE_ML_HOMME + SUFFIX_MODIF, 1L, parent);

        final SousTypeProduitJPA parentJPA
            = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        final ProduitJPA persiste
            = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        persiste.setIdProduit(1L);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));
        when(this.produitDaoJPA.findById(1L))
            .thenReturn(Optional.of(persiste));
        when(this.produitDaoJPA.save(any(ProduitJPA.class)))
            .thenReturn(null);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(p)
         * - jette une ExceptionTechniqueGateway
         * - émet un message ERREUR_TECHNIQUE_KO_STOCKAGE
         *   lorsque le stockage retourne null sur save(...).
         */
        assertThatThrownBy(() -> this.service.update(p))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).save(any(ProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(KO DAO sur save message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>appelle le DAO objet métier une fois via {@code findById(...)}</li>
     * <li>déclenche une tentative de sauvegarde via {@code save(...)}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO sur save message non null) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testUpdateDAOSaveExceptionMessageNonNull() {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p
            = this.fabriquerProduitMetier(
                    CHEMISE_ML_HOMME + SUFFIX_MODIF, 1L, parent);

        final SousTypeProduitJPA parentJPA
            = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        final ProduitJPA persiste
            = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        persiste.setIdProduit(1L);

        final RuntimeException causeDao = new RuntimeException(BOOM);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));
        when(this.produitDaoJPA.findById(1L))
            .thenReturn(Optional.of(persiste));
        when(this.produitDaoJPA.save(any(ProduitJPA.class)))
            .thenThrow(causeDao);

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.update(p));

        /* ASSERT */
        /* Garantit que this.service.update(p)
         * - jette une ExceptionTechniqueGateway
         * - conserve le message technique d'origine BOOM
         * - propage la cause DAO.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).save(any(ProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(KO DAO sur save message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non null dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>appelle le DAO objet métier une fois via {@code findById(...)}</li>
     * <li>déclenche une tentative de sauvegarde via {@code save(...)}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO sur save message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testUpdateDAOSaveExceptionMessageNull() {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p
            = this.fabriquerProduitMetier(
                    CHEMISE_ML_HOMME + SUFFIX_MODIF, 1L, parent);

        final SousTypeProduitJPA parentJPA
            = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        final ProduitJPA persiste
            = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        persiste.setIdProduit(1L);

        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));
        when(this.produitDaoJPA.findById(1L))
            .thenReturn(Optional.of(persiste));
        when(this.produitDaoJPA.save(any(ProduitJPA.class)))
            .thenThrow(causeDao);

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.update(p));

        /* ASSERT */
        /* Garantit que this.service.update(p)
         * - jette une ExceptionTechniqueGateway
         * - émet un message sûr non null
         * - propage la cause DAO.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(RuntimeException.class.getName());
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).save(any(ProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(sans modification) :</p>
     * <ul>
     * <li>ne lève aucune exception</li>
     * <li>retourne l'objet persistant inchangé</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>appelle le DAO objet métier une fois via {@code findById(...)}</li>
     * <li>ne déclenche aucune sauvegarde</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(sans modification) : retourne l'objet persistant inchangé sans save()")
    @Test
    public void testUpdateSansModification() throws Exception {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p
            = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, 1L, parent);

        final SousTypeProduitJPA parentJPA
            = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        final ProduitJPA persiste
            = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        persiste.setIdProduit(1L);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));
        when(this.produitDaoJPA.findById(1L))
            .thenReturn(Optional.of(persiste));

        /* ACT */
        final Produit resultat = this.service.update(p);

        /* ASSERT */
        /* Garantit que this.service.update(p)
         * retourne l'objet persistant inchangé.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdProduit()).isEqualTo(1L);
        assertThat(resultat.getProduit()).isEqualTo(CHEMISE_ML_HOMME);
        assertThat(resultat.getSousTypeProduit()).isNotNull();
        assertThat(resultat.getSousTypeProduit().getIdSousTypeProduit()).isEqualTo(1L);
        assertThat(resultat.getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findById(1L);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée. */
        verify(this.produitDaoJPA, never()).save(any(ProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(parent avec différence de casse seulement) :</p>
     * <ul>
     * <li>retourne un objet métier non null</li>
     * <li>retourne le libellé objet métier attendu</li>
     * <li>retourne le libellé parent réellement persistant</li>
     * <li>ne déclenche aucune sauvegarde</li>
     * </ul>
     * <p>Ce test est didactique : il documente que le parent est comparé
     * par identifiant et que le libellé réellement retourné est celui
     * du parent persistant.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent avec différence de casse seulement) : ne déclenche pas de save()")
    @Test
    public void testUpdateParentLibelleCaseSensitive() throws Exception {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(
                    VETEMENT_HOMME.toUpperCase(Locale.ROOT));
        final Produit p
            = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, 1L, parent);

        final SousTypeProduitJPA parentJPA
            = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        final ProduitJPA persiste
            = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        persiste.setIdProduit(1L);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));
        when(this.produitDaoJPA.findById(1L))
            .thenReturn(Optional.of(persiste));

        /* ACT */
        final Produit resultat = this.service.update(p);

        /* ASSERT */
        /* Garantit que la différence de casse du libellé parent demandé
         * ne déclenche pas de sauvegarde lorsque l'identifiant parent
         * reste le même.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getProduit()).isEqualTo(CHEMISE_ML_HOMME);
        assertThat(resultat.getSousTypeProduit()).isNotNull();
        assertThat(resultat.getSousTypeProduit().getIdSousTypeProduit()).isEqualTo(1L);
        assertThat(resultat.getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findById(1L);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée. */
        verify(this.produitDaoJPA, never()).save(any(ProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(modification de casse) :</p>
     * <ul>
     * <li>effectue une modification dans le stockage</li>
     * <li>retourne l'objet persistant modifié</li>
     * <li>préserve exactement la casse du nouveau libellé objet métier</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(modification de casse) : sauvegarde et préserve exactement la casse du nouveau libellé")
    @Test
    public void testUpdateModificationCasse() throws Exception {

        /* ARRANGE */
        final String nouveauLibelle
            = CHEMISE_ML_HOMME.toUpperCase(Locale.ROOT);

        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p
            = this.fabriquerProduitMetier(nouveauLibelle, 1L, parent);

        final SousTypeProduitJPA parentJPA
            = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        final ProduitJPA persiste
            = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        persiste.setIdProduit(1L);

        final ProduitJPA sauvegarde
            = this.fabriquerProduitJPA(nouveauLibelle, VETEMENT_HOMME);
        sauvegarde.setIdProduit(1L);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));
        when(this.produitDaoJPA.findById(1L))
            .thenReturn(Optional.of(persiste));
        when(this.produitDaoJPA.save(any(ProduitJPA.class)))
            .thenReturn(sauvegarde);

        /* ACT */
        final Produit resultat = this.service.update(p);

        /* ASSERT */
        /* Garantit que this.service.update(p)
         * retourne l'objet persistant modifié
         * en préservant exactement la casse demandée.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdProduit()).isEqualTo(1L);
        assertThat(resultat.getProduit()).isEqualTo(nouveauLibelle);
        assertThat(resultat.getSousTypeProduit()).isNotNull();
        assertThat(resultat.getSousTypeProduit().getIdSousTypeProduit()).isEqualTo(1L);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findById(1L);

        /* Garantit ce qui est envoyé au stockage. */
        final ArgumentCaptor<ProduitJPA> captor
            = ArgumentCaptor.forClass(ProduitJPA.class);
        verify(this.produitDaoJPA, times(1)).save(captor.capture());
        assertThat(captor.getValue().getIdProduit()).isEqualTo(1L);
        assertThat(captor.getValue().getProduit()).isEqualTo(nouveauLibelle);
        assertThat(captor.getValue().getSousTypeProduit()).isNotNull();
        assertThat(captor.getValue().getSousTypeProduit().getIdSousTypeProduit())
            .isEqualTo(1L);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(parent modifié) :</p>
     * <ul>
     * <li>effectue une modification dans le stockage ;</li>
     * <li>retourne l'objet persistant modifié ;</li>
     * <li>retourne le nouveau parent attendu ;</li>
     * <li>appelle les DAO parent et objet métier.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent modifié) : sauvegarde dans le stockage et retourne le nouveau parent")
    @Test
    public void testUpdateParentModifie() throws Exception {

        /* ARRANGE */
        final SousTypeProduit parentNouveau
            = this.fabriquerParentMetierPersistant(VETEMENT_FEMME);
        parentNouveau.setIdSousTypeProduit(2L);

        final Produit p
            = this.fabriquerProduitMetier(CHEMISE_ML_HOMME, 1L, parentNouveau);

        final SousTypeProduitJPA parentJPA2
            = this.fabriquerParentJPAPersistant(VETEMENT_FEMME);
        parentJPA2.setIdSousTypeProduit(2L);

        final ProduitJPA persiste
            = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        persiste.setIdProduit(1L);

        final ProduitJPA sauvegarde
            = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_FEMME);
        sauvegarde.setIdProduit(1L);
        sauvegarde.setSousTypeProduit(parentJPA2);

        when(this.sousTypeProduitDaoJPA.findById(2L))
            .thenReturn(Optional.of(parentJPA2));
        when(this.produitDaoJPA.findById(1L))
            .thenReturn(Optional.of(persiste));
        when(this.produitDaoJPA.save(any(ProduitJPA.class)))
            .thenReturn(sauvegarde);

        /* ACT */
        final Produit resultat = this.service.update(p);

        /* ASSERT */
        /* Garantit que this.service.update(p)
         * retourne l'objet persistant modifié
         * avec le nouveau parent demandé.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdProduit()).isEqualTo(1L);
        assertThat(resultat.getProduit()).isEqualTo(CHEMISE_ML_HOMME);
        assertThat(resultat.getSousTypeProduit()).isNotNull();
        assertThat(resultat.getSousTypeProduit().getIdSousTypeProduit()).isEqualTo(2L);
        assertThat(resultat.getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_FEMME);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(2L);
        verify(this.produitDaoJPA, times(1)).findById(1L);

        /* Garantit ce qui est envoyé au stockage. */
        final ArgumentCaptor<ProduitJPA> captor
            = ArgumentCaptor.forClass(ProduitJPA.class);
        verify(this.produitDaoJPA, times(1)).save(captor.capture());
        assertThat(captor.getValue().getIdProduit()).isEqualTo(1L);
        assertThat(captor.getValue().getProduit()).isEqualTo(CHEMISE_ML_HOMME);
        assertThat(captor.getValue().getSousTypeProduit()).isNotNull();
        assertThat(captor.getValue().getSousTypeProduit().getIdSousTypeProduit())
            .isEqualTo(2L);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(OK) :</p>
     * <ul>
     * <li>charge le parent persistant via le DAO parent</li>
     * <li>charge l'objet persistant courant via le DAO objet métier</li>
     * <li>déclenche une modification dans le stockage via {@code save(...)}</li>
     * <li>retourne l'objet persistant modifié</li>
     * <li>conserve le même identifiant</li>
     * <li>applique le nouveau libellé demandé</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(OK) : sauvegarde dans le stockage et retourne l'objet persistant modifié")
    @Test
    public void testUpdateNominal() throws Exception {

        /* ARRANGE */
        final SousTypeProduit parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);
        final Produit p
            = this.fabriquerProduitMetier(CHEMISE_MC_HOMME, 1L, parent);

        final SousTypeProduitJPA parentJPA
            = this.fabriquerParentJPAPersistant(VETEMENT_HOMME);

        final ProduitJPA persiste
            = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        persiste.setIdProduit(1L);

        final ProduitJPA sauvegarde
            = this.fabriquerProduitJPA(CHEMISE_MC_HOMME, VETEMENT_HOMME);
        sauvegarde.setIdProduit(1L);

        when(this.sousTypeProduitDaoJPA.findById(1L))
            .thenReturn(Optional.of(parentJPA));
        when(this.produitDaoJPA.findById(1L))
            .thenReturn(Optional.of(persiste));
        when(this.produitDaoJPA.save(any(ProduitJPA.class)))
            .thenReturn(sauvegarde);

        /* ACT */
        final Produit resultat = this.service.update(p);

        /* ASSERT */
        /* Garantit que this.service.update(p)
         * retourne l'objet persistant modifié
         * avec le nouveau libellé demandé.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdProduit()).isEqualTo(1L);
        assertThat(resultat.getProduit()).isEqualTo(CHEMISE_MC_HOMME);
        assertThat(resultat.getSousTypeProduit()).isNotNull();
        assertThat(resultat.getSousTypeProduit().getIdSousTypeProduit()).isEqualTo(1L);
        assertThat(resultat.getSousTypeProduit().getSousTypeProduit())
            .isEqualTo(VETEMENT_HOMME);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(1L);
        verify(this.produitDaoJPA, times(1)).findById(1L);

        /* Garantit ce qui est envoyé au stockage. */
        final ArgumentCaptor<ProduitJPA> captor
            = ArgumentCaptor.forClass(ProduitJPA.class);
        verify(this.produitDaoJPA, times(1)).save(captor.capture());
        assertThat(captor.getValue().getIdProduit()).isEqualTo(1L);
        assertThat(captor.getValue().getProduit()).isEqualTo(CHEMISE_MC_HOMME);
        assertThat(captor.getValue().getSousTypeProduit()).isNotNull();
        assertThat(captor.getValue().getSousTypeProduit().getIdSousTypeProduit())
            .isEqualTo(1L);

    } // __________________________________________________________________
    

    
    // ============================= delete ===============================
    
    
    
    /**
     * <div>
     * <p>garantit que delete(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull}</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_DELETE_KO_PARAM_NULL}</li>
     * <li>n'appelle ni le DAO objet métier ni le DAO parent</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(null) : jette ExceptionAppliParamNull et n'appelle pas les DAO")
    @Test
    public void testDeleteNull() {

        /* ARRANGE - ACT - ASSERT */
        /* Garantit que this.service.delete(null)
         * - jette une ExceptionAppliParamNull
         * - émet un message MSG_DELETE_KO_PARAM_NULL.
         */
        assertThatThrownBy(() -> this.service.delete(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_DELETE_KO_PARAM_NULL);

        /* Garantit que les DAO mockés n'ont pas été appelés. */
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNonPersistent}</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#MESSAGE_DELETE_KO_ID_NULL}</li>
     * <li>n'appelle ni le DAO objet métier ni le DAO parent</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(ID null) : jette ExceptionAppliParamNonPersistent et n'appelle pas les DAO")
    @Test
    public void testDeleteIdNull() {

        /* ARRANGE */
        final SousTypeProduitI parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = this.fabriquerProduitMetier(
                CHEMISE_ML_HOMME, null, parent);

        /* ACT - ASSERT */
        /* Garantit que this.service.delete(p)
         * - jette une ExceptionAppliParamNonPersistent
         * - émet un message MSG_DELETE_KO_ID_NULL.
         */
        assertThatThrownBy(() -> this.service.delete(p))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(MSG_DELETE_KO_ID_NULL);

        /* Garantit que les DAO mockés n'ont pas été appelés. */
        verifyNoInteractions(this.produitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(DAO.findById(...) retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet le message
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE}</li>
     * <li>appelle le DAO objet métier une fois via {@code findById(...)}</li>
     * <li>ne déclenche ni {@code delete(...)} ni {@code flush()}</li>
     * <li>n'appelle pas le DAO parent</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(DAO.findById(...) retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testDeleteDAOFindByIdRetourneNull() {

        /* ARRANGE */
        final Long id = 71L;

        final SousTypeProduitI parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = this.fabriquerProduitMetier(
                CHEMISE_ML_HOMME, id, parent);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant id via findById(...),
         * alors le DAO mocké avec Mockito devra répondre null".
         *
         * On simule donc volontairement un stockage
         * qui retourne null au lieu d'un Optional<ProduitJPA>,
         * ce qui doit être interprété comme une anomalie technique
         * de stockage.
         */
        when(this.produitDaoJPA.findById(id)).thenReturn(null);

        /* ACT - ASSERT */
        /* Garantit que this.service.delete(p)
         * - jette une ExceptionTechniqueGateway
         * - émet un message ERREUR_TECHNIQUE_KO_STOCKAGE
         *   lorsque le stockage retourne null sur findById(...).
         */
        assertThatThrownBy(() -> this.service.delete(p))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit que le DAO objet métier a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.produitDaoJPA, times(1)).findById(id);

        /* Garantit que l'échec technique sur findById(...)
         * arrête le traitement avant toute suppression et avant flush().
         */
        verify(this.produitDaoJPA, never()).delete(any(ProduitJPA.class));
        verify(this.produitDaoJPA, never()).flush();

        /* Garantit que le DAO parent n'a pas été appelé. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(objet métier absent du stockage) :</p>
     * <ul>
     * <li>ne lève aucune exception</li>
     * <li>appelle le DAO objet métier une fois via {@code findById(...)}</li>
     * <li>ne déclenche ni {@code delete(...)} ni {@code flush()}</li>
     * <li>n'appelle pas le DAO parent</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(objet métier absent du stockage) : ne fait rien et ne supprime pas")
    @Test
    public void testDeleteAbsent() throws Exception {

        /* ARRANGE */
        final Long id = 71L;

        final SousTypeProduitI parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = this.fabriquerProduitMetier(
                CHEMISE_ML_HOMME, id, parent);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant id via findById(...),
         * alors le DAO mocké avec Mockito devra répondre Optional.empty()".
         *
         * On simule donc volontairement un stockage
         * qui ne trouve aucun Produit persistant
         * pour l'identifiant demandé.
         */
        when(this.produitDaoJPA.findById(id)).thenReturn(Optional.empty());

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        this.service.delete(p);

        /* ASSERT */
        /* Garantit que le DAO objet métier a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.produitDaoJPA, times(1)).findById(id);

        /* Garantit qu'aucune suppression n'a été déclenchée,
         * puisqu'aucun objet persistant n'a été trouvé
         * à supprimer dans le stockage.
         */
        verify(this.produitDaoJPA, never()).delete(any(ProduitJPA.class));
        verify(this.produitDaoJPA, never()).flush();

        /* Garantit que le DAO parent n'a pas été appelé. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(KO DAO sur findById message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO objet métier une fois via {@code findById(...)}</li>
     * <li>ne déclenche ni {@code delete(...)} ni {@code flush()}</li>
     * <li>n'appelle pas le DAO parent</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO DAO sur findById message non null) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testDeleteDAOFindByIdExceptionMessageNonNull() {

        /* ARRANGE */
        final Long id = 71L;

        final SousTypeProduitI parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = this.fabriquerProduitMetier(
                CHEMISE_ML_HOMME, id, parent);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant id via findById(...),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException portant le message BOOM".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant le chargement de l'objet persistant à supprimer.
         */
        final RuntimeException causeDao = new RuntimeException(BOOM);

        when(this.produitDaoJPA.findById(id)).thenThrow(causeDao);

        /* ACT */
        /* Exécute une seule fois this.service.delete(p)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.delete(p));

        /* ASSERT */
        /* Garantit que this.service.delete(p)
         * - jette une ExceptionTechniqueGateway
         * - émet un message commençant par ERREUR_TECHNIQUE_STOCKAGE
         * - conserve le message technique d'origine BOOM.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);

        /* Garantit que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit que le DAO objet métier a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.produitDaoJPA, times(1)).findById(id);

        /* Garantit que l'échec technique sur findById(...)
         * arrête le traitement avant toute suppression et avant flush().
         */
        verify(this.produitDaoJPA, never()).delete(any(ProduitJPA.class));
        verify(this.produitDaoJPA, never()).flush();

        /* Garantit que le DAO parent n'a pas été appelé. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(KO DAO sur findById message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non null dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO objet métier une fois via {@code findById(...)}</li>
     * <li>ne déclenche ni {@code delete(...)} ni {@code flush()}</li>
     * <li>n'appelle pas le DAO parent</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO DAO sur findById message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testDeleteDAOFindByIdExceptionMessageNull() {

        /* ARRANGE */
        final Long id = 71L;

        final SousTypeProduitI parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit p = this.fabriquerProduitMetier(
                CHEMISE_ML_HOMME, id, parent);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant id via findById(...),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException sans message".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant le chargement de l'objet persistant à supprimer,
         * avec un message technique d'origine null.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.produitDaoJPA.findById(id)).thenThrow(causeDao);

        /* ACT */
        /* Exécute une seule fois this.service.delete(p)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.delete(p));

        /* ASSERT */
        /* Garantit que this.service.delete(p)
         * - jette une ExceptionTechniqueGateway
         * - émet un message commençant par ERREUR_TECHNIQUE_STOCKAGE
         * - n'émet pas un message null
         * - utilise un texte sûr dérivé de l'exception technique.
         *
         * Ici, avec l'implémentation actuelle de safeMessage(e),
         * le texte sûr dérivé provient de e.toString().
         * Pour une RuntimeException sans message,
         * cela donne au minimum le nom de classe java.lang.RuntimeException.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(RuntimeException.class.getName());

        /* Garantit que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit que le DAO objet métier a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.produitDaoJPA, times(1)).findById(id);

        /* Garantit que l'échec technique sur findById(...)
         * arrête le traitement avant toute suppression et avant flush().
         */
        verify(this.produitDaoJPA, never()).delete(any(ProduitJPA.class));
        verify(this.produitDaoJPA, never()).flush();

        /* Garantit que le DAO parent n'a pas été appelé. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(KO DAO sur delete message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO objet métier une fois via {@code findById(...)}</li>
     * <li>déclenche une tentative de suppression via {@code delete(...)}</li>
     * <li>ne déclenche pas {@code flush()}</li>
     * <li>n'appelle pas le DAO parent</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO DAO sur delete message non null) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testDeleteDAODeleteExceptionMessageNonNull() {

        /* ARRANGE */
        final Long id = 70L;

        final SousTypeProduitI parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit metier = this.fabriquerProduitMetier(
                CHEMISE_ML_HOMME, id, parent);

        final ProduitJPA entity
            = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        entity.setIdProduit(id);

        when(this.produitDaoJPA.findById(id)).thenReturn(Optional.of(entity));

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule doThrow(...).when(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * via delete(...),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException portant le message BOOM".
         *
         * On simule donc volontairement une panne technique du stockage
         * au moment de la suppression de l'objet persistant.
         */
        final RuntimeException causeDao = new RuntimeException(BOOM);

        doThrow(causeDao)
            .when(this.produitDaoJPA).delete(any(ProduitJPA.class));

        /* ACT */
        /* Exécute une seule fois this.service.delete(metier)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.delete(metier));

        /* ASSERT */
        /* Garantit que this.service.delete(metier)
         * - jette une ExceptionTechniqueGateway
         * - émet un message commençant par ERREUR_TECHNIQUE_STOCKAGE
         * - conserve le message technique d'origine BOOM.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);

        /* Garantit que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.produitDaoJPA, times(1)).findById(id);
        verify(this.produitDaoJPA, times(1)).delete(entity);

        /* Garantit que l'échec sur delete(...)
         * arrête le traitement avant flush().
         */
        verify(this.produitDaoJPA, never()).flush();

        /* Garantit que le DAO parent n'a pas été appelé. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(KO DAO sur delete message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non null dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO objet métier une fois via {@code findById(...)}</li>
     * <li>déclenche une tentative de suppression via {@code delete(...)}</li>
     * <li>ne déclenche pas {@code flush()}</li>
     * <li>n'appelle pas le DAO parent</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO DAO sur delete message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testDeleteDAODeleteExceptionMessageNull() {

        /* ARRANGE */
        final Long id = 70L;

        final SousTypeProduitI parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit metier = this.fabriquerProduitMetier(
                CHEMISE_ML_HOMME, id, parent);

        final ProduitJPA entity
            = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        entity.setIdProduit(id);

        when(this.produitDaoJPA.findById(id)).thenReturn(Optional.of(entity));

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule doThrow(...).when(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * via delete(...),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException sans message".
         *
         * On simule donc volontairement une panne technique du stockage
         * au moment de la suppression de l'objet persistant,
         * avec un message technique d'origine null.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        doThrow(causeDao)
            .when(this.produitDaoJPA).delete(any(ProduitJPA.class));

        /* ACT */
        /* Exécute une seule fois this.service.delete(metier)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.delete(metier));

        /* ASSERT */
        /* Garantit que this.service.delete(metier)
         * - jette une ExceptionTechniqueGateway
         * - émet un message commençant par ERREUR_TECHNIQUE_STOCKAGE
         * - n'émet pas un message null
         * - utilise un texte sûr dérivé de l'exception technique.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(RuntimeException.class.getName());

        /* Garantit que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.produitDaoJPA, times(1)).findById(id);
        verify(this.produitDaoJPA, times(1)).delete(entity);

        /* Garantit que l'échec sur delete(...)
         * arrête le traitement avant flush().
         */
        verify(this.produitDaoJPA, never()).flush();

        /* Garantit que le DAO parent n'a pas été appelé. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(KO DAO sur flush message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO objet métier une fois via {@code findById(...)}</li>
     * <li>déclenche une suppression via {@code delete(...)}</li>
     * <li>déclenche {@code flush()}</li>
     * <li>n'appelle pas le DAO parent</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO DAO sur flush message non null) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testDeleteDAOFlushExceptionMessageNonNull() {

        /* ARRANGE */
        final Long id = 70L;

        final SousTypeProduitI parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit metier = this.fabriquerProduitMetier(
                CHEMISE_ML_HOMME, id, parent);

        final ProduitJPA entity
            = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        entity.setIdProduit(id);

        when(this.produitDaoJPA.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(this.produitDaoJPA).delete(any(ProduitJPA.class));

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule doThrow(...).when(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * via flush(),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException portant le message BOOM".
         *
         * On simule donc volontairement une panne technique du stockage
         * au moment du flush() suivant la suppression.
         */
        final RuntimeException causeDao = new RuntimeException(BOOM);

        doThrow(causeDao).when(this.produitDaoJPA).flush();

        /* ACT */
        /* Exécute une seule fois this.service.delete(metier)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.delete(metier));

        /* ASSERT */
        /* Garantit que this.service.delete(metier)
         * - jette une ExceptionTechniqueGateway
         * - émet un message commençant par ERREUR_TECHNIQUE_STOCKAGE
         * - conserve le message technique d'origine BOOM.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);

        /* Garantit que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.produitDaoJPA, times(1)).findById(id);
        verify(this.produitDaoJPA, times(1)).delete(entity);
        verify(this.produitDaoJPA, times(1)).flush();

        /* Garantit que le DAO parent n'a pas été appelé. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(KO DAO sur flush message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non null dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO objet métier une fois via {@code findById(...)}</li>
     * <li>déclenche une suppression via {@code delete(...)}</li>
     * <li>déclenche {@code flush()}</li>
     * <li>n'appelle pas le DAO parent</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO DAO sur flush message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testDeleteDAOFlushExceptionMessageNull() {

        /* ARRANGE */
        final Long id = 70L;

        final SousTypeProduitI parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit metier = this.fabriquerProduitMetier(
                CHEMISE_ML_HOMME, id, parent);

        final ProduitJPA entity
            = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        entity.setIdProduit(id);

        when(this.produitDaoJPA.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(this.produitDaoJPA).delete(any(ProduitJPA.class));

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule doThrow(...).when(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * via flush(),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException sans message".
         *
         * On simule donc volontairement une panne technique du stockage
         * au moment du flush() suivant la suppression,
         * avec un message technique d'origine null.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        doThrow(causeDao).when(this.produitDaoJPA).flush();

        /* ACT */
        /* Exécute une seule fois this.service.delete(metier)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.delete(metier));

        /* ASSERT */
        /* Garantit que this.service.delete(metier)
         * - jette une ExceptionTechniqueGateway
         * - émet un message commençant par ERREUR_TECHNIQUE_STOCKAGE
         * - n'émet pas un message null
         * - utilise un texte sûr dérivé de l'exception technique.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(RuntimeException.class.getName());

        /* Garantit que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.produitDaoJPA, times(1)).findById(id);
        verify(this.produitDaoJPA, times(1)).delete(entity);
        verify(this.produitDaoJPA, times(1)).flush();

        /* Garantit que le DAO parent n'a pas été appelé. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(OK) :</p>
     * <ul>
     * <li>charge l'objet persistant courant via {@code findById(...)}</li>
     * <li>déclenche la suppression dans le stockage via {@code delete(...)}</li>
     * <li>force l'exécution via {@code flush()}</li>
     * <li>n'appelle pas le DAO parent</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(OK) : supprime l'objet persistant et déclenche flush()")
    @Test
    public void testDeleteNominal() throws Exception {

        /* ARRANGE */
        final Long id = 70L;

        final SousTypeProduitI parent
            = this.fabriquerParentMetierPersistant(VETEMENT_HOMME);

        final Produit metier = this.fabriquerProduitMetier(
                CHEMISE_ML_HOMME, id, parent);

        final ProduitJPA entity
            = this.fabriquerProduitJPA(CHEMISE_ML_HOMME, VETEMENT_HOMME);
        entity.setIdProduit(id);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * Le premier appel findById(...) trouve l'objet persistant courant.
         * La suppression et le flush() sont simulés comme réussis.
         */
        when(this.produitDaoJPA.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(this.produitDaoJPA).delete(any(ProduitJPA.class));
        doNothing().when(this.produitDaoJPA).flush();

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        this.service.delete(metier);

        /* ASSERT */
        /* Garantit que le DAO objet métier a bien :
         * - chargé l'objet persistant ;
         * - reçu l'ordre de suppression ;
         * - forcé l'exécution via flush().
         */
        verify(this.produitDaoJPA, times(1)).findById(id);
        verify(this.produitDaoJPA, times(1)).delete(entity);
        verify(this.produitDaoJPA, times(1)).flush();

        /* Garantit que le DAO parent n'a pas été appelé. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________
    
    

    // ============================== Count ===============================
    
    
    
    /**
     * <div>
     * <p>garantit que count(stockage vide) :</p>
     * <ul>
     * <li>retourne {@code 0} ;</li>
     * <li>appelle le DAO objet métier une fois via {@code count()} ;</li>
     * <li>n'appelle pas le DAO parent ;</li>
     * <li>ne jette aucune exception.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName("count(stockage vide) : retourne 0")
    @Test
    public void testCountStockageVide() throws Exception {
    	
        /* ARRANGE */
        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito via count(),
         * alors le DAO mocké avec Mockito devra répondre 0L".
         *
         * On simule donc volontairement un stockage vide.
         */
        when(this.produitDaoJPA.count()).thenReturn(0L);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        final long resultat = this.service.count();

        /* ASSERT */
        /* Garantit que this.service.count()
         * retourne exactement le nombre d'objets indiqué
         * par le stockage.
         */
        assertThat(resultat).isEqualTo(0L);

        /* Garantit que le DAO objet métier mocké
         * a bien été appelé une fois via la méthode count().
         */
        verify(this.produitDaoJPA, times(1)).count();

        /* Garantit que le DAO parent
         * ne participe pas au comptage.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que count(KO DAO message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine du DAO ;</li>
     * <li>propage comme cause l'exception technique d'origine ;</li>
     * <li>appelle le DAO objet métier une fois via {@code count()} ;</li>
     * <li>n'appelle pas le DAO parent.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_COUNT)
    @DisplayName("count(KO DAO message non null) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testCountDAOExceptionMessageNonNull() {
    	
        /* ARRANGE */
        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito via count(),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException portant le message BOOM".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant le comptage.
         */
        final RuntimeException causeDao = new RuntimeException(BOOM);

        when(this.produitDaoJPA.count()).thenThrow(causeDao);

        /* ACT */
        /* Exécute une seule fois this.service.count()
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.count());

        /* ASSERT */
        /* Garantit que this.service.count()
         * - jette une ExceptionTechniqueGateway
         * - émet un message commençant par ERREUR_TECHNIQUE_STOCKAGE
         * - conserve le message technique d'origine BOOM.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(BOOM);

        /* Garantit que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit que le DAO objet métier mocké
         * a bien été appelé une fois via la méthode count().
         */
        verify(this.produitDaoJPA, times(1)).count();

        /* Garantit que le DAO parent
         * ne participe pas au comptage.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que count(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link ProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non null dérivé de l'exception technique ;</li>
     * <li>propage comme cause l'exception technique d'origine ;</li>
     * <li>appelle le DAO objet métier une fois via {@code count()} ;</li>
     * <li>n'appelle pas le DAO parent.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_COUNT)
    @DisplayName("count(KO DAO message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testCountDAOExceptionMessageNull() {
    	
        /* ARRANGE */
        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito via count(),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException sans message".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant le comptage,
         * avec un message technique d'origine null.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.produitDaoJPA.count()).thenThrow(causeDao);

        /* ACT */
        /* Exécute une seule fois this.service.count()
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.count());

        /* ASSERT */
        /* Garantit que this.service.count()
         * - jette une ExceptionTechniqueGateway
         * - émet un message commençant par ERREUR_TECHNIQUE_STOCKAGE
         * - n'émet pas un message null
         * - utilise un texte sûr dérivé de l'exception technique.
         *
         * Ici, avec l'implémentation actuelle de safeMessage(e),
         * le texte sûr dérivé provient de e.toString().
         * Pour une RuntimeException sans message,
         * cela donne au minimum le nom de classe java.lang.RuntimeException.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(RuntimeException.class.getName());

        /* Garantit que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit que le DAO objet métier mocké
         * a bien été appelé une fois via la méthode count().
         */
        verify(this.produitDaoJPA, times(1)).count();

        /* Garantit que le DAO parent
         * ne participe pas au comptage.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que count(OK) :</p>
     * <ul>
     * <li>retourne le nombre d'objets métier présents dans le stockage ;</li>
     * <li>appelle le DAO objet métier une fois via {@code count()} ;</li>
     * <li>n'appelle pas le DAO parent ;</li>
     * <li>ne jette aucune exception.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName("count(OK) : retourne le nombre d'objets métier présents dans le stockage")
    @Test
    public void testCountNominal() throws Exception {
    	
        /* ARRANGE */
        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito via count(),
         * alors le DAO mocké avec Mockito devra répondre TOTAL_10".
         *
         * On simule donc volontairement un stockage contenant
         * plusieurs Produit persistants.
         */
        when(this.produitDaoJPA.count()).thenReturn(TOTAL_10);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        final long resultat = this.service.count();

        /* ASSERT */
        /* Garantit que this.service.count()
         * retourne exactement le nombre d'objets indiqué
         * par le stockage.
         */
        assertThat(resultat).isEqualTo(TOTAL_10);

        /* Garantit que le DAO objet métier mocké
         * a bien été appelé une fois via la méthode count().
         */
        verify(this.produitDaoJPA, times(1)).count();

        /* Garantit que le DAO parent
         * ne participe pas au comptage.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    // ============== TESTS BETON (sanity / invariants) ===================
    

    /**
     * <div>
     * <p>Sanity : vérifie qu'aucune String null n'est construite par les helpers.</p>
     * </div>
     */
    @Tag(TAG_SANITY)
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
