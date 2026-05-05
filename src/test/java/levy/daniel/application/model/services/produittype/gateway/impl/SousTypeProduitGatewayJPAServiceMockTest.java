/* ********************************************************************* */
/* ********************* TEST MOCKITO GATEWAY JPA ********************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.gateway.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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

import jakarta.persistence.EntityManager;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliLibelleBlank;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNonPersistent;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNull;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParentNull;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionTechniqueGateway;
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
 * CLASSE SousTypeProduitGatewayJPAServiceMockTest.java :
 * </p>
 * <p>
 * Tests unitaires Mockito (DAO mockés) du service ADAPTER GATEWAY
 * {@link SousTypeProduitGatewayJPAService}.
 * </p>
 *
 * <p style="font-weight:bold;">CONTEXTE :</p>
 * <ul>
 * <li>Utilisation de Mockito pour simuler les dépendances.</li>
 * <li>Vérification des comportements applicatifs et techniques.</li>
 * <li>Respect strict des contrats définis dans le PORT GATEWAY.</li>
 * </ul>
 *
 * <p style="font-weight:bold;">GARANTIES :</p>
 * <ul>
 * <li>Aucune <code>NullPointerException</code> levée.</li>
 * <li>Toutes les ressources Mockito sont correctement fermées.</li>
 * <li>Les vérifications couvrent 100% des appels critiques.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 3.1
 * @since 07 février 2026
 */
@ExtendWith(MockitoExtension.class)
public class SousTypeProduitGatewayJPAServiceMockTest {

    // *************************** CONSTANTES ****************************/

    /** Locale par défaut. */
    public static final Locale LOCALE_DEFAUT = Locale.getDefault();
    
    /**
     * "resource"
     */
    public static final String RESOURCE = "resource";

    /** "" */
    public static final String CHAINE_VIDE = "";

    /** "SousTypeProduit_1" */
    public static final String LIBELLE_ENFANT_1 = "SousTypeProduit_1";

    /** "SousTypeProduit_2" */
    public static final String LIBELLE_ENFANT_2 = "SousTypeProduit_2";

    /** "SousTypeProduit_3" */
    public static final String LIBELLE_ENFANT_3 = "SousTypeProduit_3";

    /** "TypeProduit_1" */
    public static final String LIBELLE_PARENT_1 = "TypeProduit_1";

    /** "TypeProduit_2" */
    public static final String LIBELLE_PARENT_2 = "TypeProduit_2";

    /** Contenu partiel : "produit". */
    public static final String CONTENU_PARTIEL = "produit";

    /** "   " */
    public static final String BLANK = "   ";

    /** 1L */
    public static final Long ID_1 = Long.valueOf(1L);

    /** 2L */
    public static final Long ID_2 = Long.valueOf(2L);

    /** 3L */
    public static final Long ID_3 = Long.valueOf(3L);

    /** 0 */
    public static final int PAGE_0 = 0;

    /** 5 */
    public static final int SIZE_5 = 5;

    /** 10L */
    public static final long TOTAL_10 = 10L;
        
    /**
     * "boom"
     */
    public static final String MSG_BOOM = "boom";

    /** "Erreur Technique lors du stockage : " */
    public static final String MSG_PREFIX_ERREUR_TECH =
            SousTypeProduitGatewayIService.ERREUR_TECHNIQUE_STOCKAGE;

    /** "Erreur Technique - Le stockage a retourné null." */
    public static final String MSG_ERREUR_TECH_KO_STOCKAGE =
            SousTypeProduitGatewayIService.ERREUR_TECHNIQUE_KO_STOCKAGE;

    /** "Anomalie applicative - l'objet métier passé en paramètre est null." */
    public static final String MSG_CREER_KO_PARAM_NULL =
            SousTypeProduitGatewayIService.MESSAGE_CREER_KO_PARAM_NULL;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un libellé blank
	 * (null ou que des espaces)."</p>
	 * </div>
     */
    public static final String MSG_CREER_KO_LIBELLE_BLANK =
            SousTypeProduitGatewayIService.MESSAGE_CREER_KO_LIBELLE_BLANK;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un parent null."</p>
	 * </div>
     */
    public static final String MSG_CREER_KO_PARENT_NULL =
            SousTypeProduitGatewayIService.MESSAGE_CREER_KO_PARENT_NULL;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - le parent de l'objet à créer a un libellé blank
	 * (null ou que des espaces)."</p>
	 * </div> 
     */
    public static final String MSG_CREER_KO_LIBELLE_PARENT_BLANK =
            SousTypeProduitGatewayIService.MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK;

    /** 
     * <div>
	 * <p>"Anomalie applicative -
	 * le parent de l'objet que vous voulez créer n'existe
	 * pas déjà dans le stockage : "</p>
	 * </div>
     */
    public static final String MSG_CREER_PREFIX_PARENT_NON_PERSISTENT =
            SousTypeProduitGatewayIService.MESSAGE_CREER_KO_PARENT_NON_PERSISTENT;
    
    /** 
     * <div>
	 * <p>"Anomalie applicative
	 *  - le paramètre pObject ne doit pas être null."</p>
	 * </div>
     */
    public static final String MSG_FINDBYOBJETMETIER_KO_PARAM_NULL =
            SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 *  - le libellé de pObject passé en paramètre
	 *  ne doit pas être blank."</p>
	 * </div>
     */
    public static final String MSG_FINDBYOBJETMETIER_KO_LIBELLE_BLANK =
            SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un parent null."</p>
	 * </div>
     */
    public static final String MSG_FINDBYOBJETMETIER_KO_PARENT_NULL =
            SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - le parent de l'objet à rechercher a un libellé blank
	 * (null ou que des espaces)."</p>
	 * </div> 
     */
    public static final String MSG_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK =
            SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - le parent de l'objet que vous voulez créer n'existe
	 * pas déjà dans le stockage : "</p>
	 * </div>
     */
    public static final String MSG_FINDBYOBJETMETIER_PREFIX_PARENT_NON_PERSISTENT =
            SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - le parent de l'objet que vous voulez créer n'existe
	 * pas déjà dans le stockage : "</p>
	 * </div>
     */
    public static final String MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK =
            SousTypeProduitGatewayIService.MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK;

    /** 
     * "Anomalie applicative - le contenu passé en paramètre est null."
     */
    public static final String MSG_FINDBYLIBELLERAPIDE_KO_PARAM_NULL =
            SousTypeProduitGatewayIService.MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL;

    /** 
     * "Anomalie applicative - l'objet métier passé en paramètre est null."
     */
    public static final String MSG_FINDALLBYPARENT_KO_PARAM_NULL =
            SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - le parent de l'objet passé en paramètre a un libellé blank
	 * (null ou que des espaces)."</p>
	 * </div>
     */
    public static final String MSG_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK =
            SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - le parent de l'objet n'existait
	 * pas déjà dans le stockage : "</p>
	 * </div>
     */
    public static final String MSG_FINDALLBYPARENT_PREFIX_PARENT_NON_PERSISTENT =
            SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - l'identifiant passé en paramètre est null."</p>
	 * </div>
     */
    public static final String MSG_FINDBYID_KO_PARAM_NULL =
            SousTypeProduitGatewayIService.MESSAGE_FINDBYID_KO_PARAM_NULL;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre est null."</p>
	 * </div> 
     */
    public static final String MSG_UPDATE_KO_PARAM_NULL =
            SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARAM_NULL;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un libellé blank
	 * (null ou que des espaces)."</p>
	 * </div>
     */
    public static final String MSG_UPDATE_KO_LIBELLE_BLANK =
            SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_BLANK;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - l'objet que vous voulez modifier n'est pas persistant
	 * (ID null) : "</p>
	 * </div> 
     */
    public static final String MSG_UPDATE_PREFIX_NON_PERSISTENT =
            SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_NON_PERSISTENT;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un parent null."</p>
	 * </div>
     */
    public static final String MSG_UPDATE_KO_PARENT_NULL =
            SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARENT_NULL;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - le parent de l'objet à modifier a un libellé blank
	 * (null ou que des espaces)."</p>
	 * </div> 
     */
    public static final String MSG_UPDATE_KO_LIBELLE_PARENT_BLANK =
            SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - le parent de l'objet que vous voulez modifier n'existe
	 * pas déjà dans le stockage : "</p>
	 * </div>
     */
    public static final String MSG_UPDATE_PREFIX_PARENT_NON_PERSISTENT =
            SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT;

    /** "Anomalie applicative - l'objet métier passé en paramètre est null." */
    public static final String MSG_DELETE_KO_PARAM_NULL =
            SousTypeProduitGatewayIService.MESSAGE_DELETE_KO_PARAM_NULL;

    /** 
     * <div>
	 * <p>"Anomalie applicative
	 * - l'objet métier passé en paramètre a un ID null."</p>
	 * </div>
     */
    public static final String MSG_DELETE_KO_ID_NULL =
            SousTypeProduitGatewayIService.MESSAGE_DELETE_KO_ID_NULL;
    
    /** "servicesGateway-Creer" */
    public static final String TAG_CREER = "servicesGateway-Creer";

    /**
     * "creer(null) : jette ExceptionAppliParamNull (contrat du port)"
     */
    public static final String DN_CREER_NULL =
            "creer(null) : jette ExceptionAppliParamNull (contrat du port)";

    /**
     * "creer(blank) : jette ExceptionAppliLibelleBlank (contrat du port)"
     */
    public static final String DN_CREER_BLANK =
            "creer(blank) : jette ExceptionAppliLibelleBlank (contrat du port)";
    
    /**
     * "servicesGateway-RechercherTous"
     */
    public static final String TAG_RECHERCHERTOUS 
    	= "servicesGateway-RechercherTous";
    
    /**
     * "servicesGateway-RechercherTousParPage"
     */
    public static final String TAG_RECHERCHERTOUSPARPAGE 
    	= "servicesGateway-RechercherTousParPage";
    
    /**
     * "servicesGateway-FindByObjetMetier"
     */
    public static final String TAG_FINDBYOBJETMETIER 
    	= "servicesGateway-FindByObjetMetier";
    
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
    
    /**
     * "servicesGateway-FindById"
     */
    public static final String TAG_FINDBYID
    	= "servicesGateway-FindById";
    
    /**
     * "servicesGateway-Update"
     */
    public static final String TAG_UPDATE
    	= "servicesGateway-Update";
    
    /**
     * "servicesGateway-Delete"
     */
    public static final String TAG_DELETE
    	= "servicesGateway-Delete";
    
    /**
     * "servicesGateway-Count"
     */
    public static final String TAG_COUNT
    	= "servicesGateway-Count";
    
    /**
     * "servicesGateway-Sanity"
     */
    public static final String TAG_SANITY
    	= "servicesGateway-Sanity";
    
    /**
     * "servicesGateway-CasLimites"
     */
    public static final String TAG_CAS_LIMITES
    	= "servicesGateway-CasLimites";
    
    // *************************** ATTRIBUTS *****************************/

    /**
     * <div>
     * <p>Mock du DAO pour l'objet métier 
     * {@link SousTypeProduit} (enfant).</p>
     * </div>
     */
    @Mock
    private SousTypeProduitDaoJPA sousTypeProduitDaoJPA;

    /**
     * <div>
     * <p>Mock du DAO pour le parent {@link TypeProduit}.</p>
     * </div>
     */
    @Mock
    private TypeProduitDaoJPA typeProduitDaoJPA;

    /**
     * <div>
     * <p>Mock de l'EntityManager pour la gestion du cache Hibernate.</p>
     * </div>
     */
    @Mock
    private EntityManager entityManager;

    /**
     * <div>
     * <p>Service {@link SousTypeProduitGatewayJPAService}  
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
     * <li>Initialise le service réel avec ses deux DAO mockés, 
     * puis injecte explicitement
     * l'EntityManager mocké parce que SousTypeProduitGatewayJPAService l'utilise
     * comme collaborateur technique pour certains scénarios Hibernate/cache.
     * Dans cette classe, vérifier verifyNoInteractions(entityManager) 
     * est donc probant : le mock de l'EntityManager est réellement 
     * relié au service testé.</li>
     * </ul>
     * </div>
     */
    @InjectMocks
    private SousTypeProduitGatewayJPAService service;
    
    

    // ************************* CONSTRUCTEURS ***************************/

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">CONSTRUCTEUR D'ARITE NULLE.</p>
     * <p>Nécessaire pour PMD.</p>
     * </div>
     */
    public SousTypeProduitGatewayJPAServiceMockTest() {
        super();
    }

        

    // ============================== INIT ================================

    
    
    /**
     * <div>
     * <p>Initialise le service avec des DAO mockés.</p>
     * </div>
     */
    @BeforeEach
    public void init() {
        this.service = new SousTypeProduitGatewayJPAService(
                this.sousTypeProduitDaoJPA,
                this.typeProduitDaoJPA);
        this.service.setEntityManager(this.entityManager);
    }

    

    // **************************** TESTS ********************************/
    
    
    
    // =============================== CREER ==============================



    /**
     * <div>
     * <p>garantit que creer(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message 
     * {@link SousTypeProduitGatewayIService#MESSAGE_CREER_KO_PARAM_NULL} ;</li>
     * <li>n'appelle ni le DAO parent, ni le DAO objet métier (enfant), 
     * ni l'EntityManager.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_NULL)
    @Test
    public void testCreerNull() {

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel service.creer(...) avec un paramètre null
         * jette une ExceptionAppliParamNull
         * avec le message MSG_CREER_KO_PARAM_NULL 
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.creer(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_CREER_KO_PARAM_NULL);

        /* 
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité 
         * par la gestion des mauvais paramètres avant tout appel des DAO
         * ou de l'EntityManager.
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelé.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelé.
         * - entityManager n'a jamais été appelé.
         */
        /* - verify(..., never()).méthode(...) = preuve ciblée 
         * sur une méthode critique précise.
         * - verifyNoInteractions(mock) = preuve globale 
         * que le mock entier n'a pas été touché.*/
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que creer(libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message 
     * {@link SousTypeProduitGatewayIService#MESSAGE_CREER_KO_LIBELLE_BLANK} ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_BLANK)
    @Test
    public void testCreerLibelleBlank() {

        /* ARRANGE :
         * prépare un objet métier dont le libellé est blank,
         * afin de vérifier le contrôle applicatif
         * effectué avant toute tentative d'accès au stockage.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(BLANK, null, parent);

        /* ACT - ASSERT :
         * vérifie que l'appel service.creer(...) 
         * avec un objet métier ayant un libellé blank
         * jette une ExceptionAppliLibelleBlank
         * avec le message MSG_CREER_KO_LIBELLE_BLANK 
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_CREER_KO_LIBELLE_BLANK);

        /* 
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité 
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelé.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(parent null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParentNull} ;</li>
     * <li>émet le message 
     * {@link SousTypeProduitGatewayIService#MESSAGE_CREER_KO_PARENT_NULL} ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(parent null) : jette ExceptionAppliParentNull (contrat du port)")
    @Test
    public void testCreerParentNull() {

        /* ARRANGE :
         * prépare un objet métier sans parent,
         * afin de vérifier le contrôle applicatif
         * imposé par le contrat du port.
         */
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, null);

        /* ACT - ASSERT :
         * vérifie que l'appel service.creer(...) 
         * avec un objet ayant un parent null
         * jette une ExceptionAppliParentNull
         * avec le message MSG_CREER_KO_PARENT_NULL 
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_CREER_KO_PARENT_NULL);

        /* 
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité 
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelé.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO objet métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(parent libellé blank) : jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testCreerParentLibelleBlank() {

        /* ARRANGE :
         * prépare un parent dont le libellé est blank,
         * afin de vérifier le contrôle applicatif
         * effectué avant toute recherche réelle du parent.
         */
        final TypeProduit parent = fabriquerTypeProduit(BLANK, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        /* ACT - ASSERT :
         * vérifie que l'appel service.creer(...) 
         * avec un objet dont le parent a un libellé parent blank
         * jette une ExceptionAppliLibelleBlank
         * avec le message MSG_CREER_KO_LIBELLE_PARENT_BLANK 
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_CREER_KO_LIBELLE_PARENT_BLANK);

        /* 
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité 
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelé.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(parent ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_CREER_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent ;</li>
     * <li>n'appelle pas les DAO.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(parent ID null) : jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testCreerParentIdNull() {

        /* ARRANGE :
         * prépare un parent non persistant
         * dont l'identifiant est null,
         * afin de vérifier le contrôle de persistance
         * effectué avant toute recherche DAO.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, null);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        /* ACT - ASSERT :
         * vérifie que l'appel service.creer(...) 
         * avec un objet dont le parent n'est pas persistant (id null)
         * jette une ExceptionTechniqueGatewayNonPersistent
         * avec un message contenant MSG_CREER_PREFIX_PARENT_NON_PERSISTENT 
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(construireMessageNonPersistent(
                    MSG_CREER_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1));

        /* 
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité 
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelé.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(parent absent DAO) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_CREER_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent ;</li>
     * <li>appelle le DAO parent ;</li>
     * <li>n'appelle pas save(...).</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(parent absent DAO) : jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testCreerParentAbsent() {

        /* ARRANGE :
         * prépare un parent persistant en apparence,
         * mais absent du DAO mocké avec Mockito.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        /* Condition du Mock :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké 
         * retourne Optional.empty().
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.empty());

        /* ACT - ASSERT :
         * vérifie que :
         * this.service.creer(stp) avec un parent présent dans l'objet métier,
         * mais absent du stockage
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - avec un message contenant MSG_CREER_PREFIX_PARENT_NON_PERSISTENT 
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(construireMessageNonPersistent(
                    MSG_CREER_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1));

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que : 
         * - typeProduitDaoJPA.findById(ID_1) a été appelé une fois.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(DAO parent jette RuntimeException avec message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>contient le message technique d'origine ;</li>
     * <li>propage une cause non null ;</li>
     * <li>n'appelle pas save(...).</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(KO DAO parent message non null) : jette ExceptionTechniqueGateway")
    @Test
    public void testCreerParentDAOExceptionMessageNonNull() {

        /* ARRANGE :
         * prépare un parent persistant en apparence,
         * puis configure le DAO parent mocké avec Mockito
         * pour jeter une RuntimeException avec message non null.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final RuntimeException ex = new RuntimeException(MSG_BOOM);
        
        /* Condition du Mock :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké 
         * jette l'Exception ex.
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.creer(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.creer(...), 
         * - intercepte toute exception éventuellement levée, 
         * - puis stocke cette exception dans la variable throwable 
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.creer(stp));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel,
         * le message technique d'origine
         * et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable).hasMessageContaining(MSG_BOOM);
        assertThat(throwable.getCause()).isSameAs(ex);

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que : 
         * - typeProduitDaoJPA.findById(ID_1) a été appelé une fois.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(DAO parent jette RuntimeException avec message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>n'appelle pas save(...).</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(KO DAO parent message null) : jette ExceptionTechniqueGateway")
    @Test
    public void testCreerParentDAOExceptionMessageNull() {

        /* ARRANGE :
         * prépare un parent persistant en apparence,
         * puis configure le DAO parent mocké avec Mockito
         * pour jeter une RuntimeException sans message.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final RuntimeException ex = new RuntimeException((String) null);
        
        /* Condition du Mock :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké 
         * jette l'Exception ex.
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.creer(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.creer(...), 
         * - intercepte toute exception éventuellement levée, 
         * - puis stocke cette exception dans la variable throwable 
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.creer(stp));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que : 
         * - typeProduitDaoJPA.findById(ID_1) a été appelé une fois.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(DAO.save(...) retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle findById(...) sur le parent puis save(...).</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(DAO.save(...) retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testCreerDAOSaveRetourneNull() {

        /* ARRANGE :
         * prépare un scénario où le parent existe,
         * mais où le DAO objet métier mocké avec Mockito
         * retourne null lors du save(...).
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        
        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké 
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.save(...) sur le DAO mocké 
         * retourne null.
         */
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que l'appel service.creer(...) 
         * jette une ExceptionTechniqueGateway
         * avec le message MSG_ERREUR_TECH_KO_STOCKAGE.
         */
        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - typeProduitDaoJPA.findById(ID_1) a été appelé une fois ;
         * - sousTypeProduitDaoJPA.save(...) a été appelé une fois.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(DAO.save(...) jette RuntimeException avec message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>contient le message technique d'origine ;</li>
     * <li>propage une cause non null.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(KO DAO save message non null) : jette ExceptionTechniqueGateway")
    @Test
    public void testCreerDAOSaveExceptionMessageNonNull() {

        /* ARRANGE :
         * prépare un parent existant,
         * puis configure le DAO objet métier mocké avec Mockito
         * pour jeter une RuntimeException avec message non null
         * lors du save(...).
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final RuntimeException ex = new RuntimeException(MSG_BOOM);
        
        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké 
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.save(...) sur le DAO mocké 
         * jette l'Exception ex.
         */
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.creer(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.creer(...), 
         * - intercepte toute exception éventuellement levée, 
         * - puis stocke cette exception dans la variable throwable 
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.creer(stp));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel,
         * le message technique d'origine
         * et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable).hasMessageContaining(MSG_BOOM);
        assertThat(throwable.getCause()).isSameAs(ex);

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que : 
         * - typeProduitDaoJPA.findById(ID_1) a été appelé une fois.
         * - sousTypeProduitDaoJPA.save(...) a été appelé une fois.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que creer(DAO.save(...) jette RuntimeException avec message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message sûr non null ;</li>
     * <li>propage une cause non null.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(KO DAO save message null) : jette ExceptionTechniqueGateway")
    @Test
    public void testCreerDAOSaveExceptionMessageNull() {

        /* ARRANGE :
         * prépare un parent existant,
         * puis configure le DAO objet métier mocké avec Mockito
         * pour jeter une RuntimeException sans message
         * lors du save(...).
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final RuntimeException ex = new RuntimeException((String) null);
        
        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké 
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.save(...) sur le DAO mocké 
         * jette l'Exception ex.
         */
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.creer(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.creer(...), 
         * - intercepte toute exception éventuellement levée, 
         * - puis stocke cette exception dans la variable throwable 
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.creer(stp));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son message sûr non null,
         * son préfixe contractuel
         * et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable.getMessage()).isNotNull();
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que : 
         * - typeProduitDaoJPA.findById(ID_1) a été appelé une fois.
         * - sousTypeProduitDaoJPA.save(...) a été appelé une fois.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>garantit que si le stockage refuse creer(...) pour cause de doublon fonctionnel :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO parent ;</li>
     * <li>appelle le DAO objet métier pour sauvegarde.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(doublon) : jette ExceptionTechniqueGateway")
    @Test
    public void testCreerDoublon() {

        /* ARRANGE :
         * prépare un objet métier valide,
         * avec un parent persistant en apparence.
         * Le doublon n'est pas simulé au niveau métier,
         * mais au niveau du stockage au moment du save(...).
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1)
         * sur le DAO parent mocké retourne Optional.of(parentJPA),
         * afin d'atteindre réellement la tentative de sauvegarde
         * de l'objet métier.
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Prépare l'exception technique d'intégrité levée par le stockage :
         * elle représente ici un refus de création
         * pour cause de doublon fonctionnel sur le couple
         * [parent / libellé objet métier].
         */
        final String messageTechnique = "contrainte d'unicité violée";
        final DataIntegrityViolationException causeDao =
                new DataIntegrityViolationException(messageTechnique);

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.save(...)
         * sur le DAO objet métier mocké jette l'Exception causeDao.
         */
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class)))
            .thenThrow(causeDao);

        /* ACT :
         * sollicite la méthode service.creer(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.creer(...),
         * - intercepte toute exception éventuellement levée,
         * - puis stocke cette exception dans la variable throwable
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.creer(stp));

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
         * - typeProduitDaoJPA.findById(ID_1) a été appelé une fois ;
         * - sousTypeProduitDaoJPA.save(...) a été appelé une fois.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que creer(parent avec caractères spéciaux) :</p>
     * <ul>
     * <li>retourne un objet métier non null ;</li>
     * <li>retourne l'identifiant objet métier issu du stockage ;</li>
     * <li>retourne le libellé objet métier attendu ;</li>
     * <li>retourne le libellé parent attendu ;</li>
     * <li>appelle findById(...) sur le parent puis save(...).</li>
     * </ul>
     * </div>
     * @throws Exception 
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(parent avec caractères spéciaux) : retourne un objet métier cohérent")
    @Test
    public void testCreerParentCaracteresSpeciaux() throws Exception {

        /* ARRANGE :
         * prépare un scénario nominal
         * avec un parent contenant des caractères spéciaux,
         * afin de vérifier que le service ne déforme pas
         * les libellés métier.
         */
        final TypeProduit parent = fabriquerTypeProduit("Type/Produit_1", ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA("Type/Produit_1", ID_1);
        
        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké 
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final SousTypeProduitJPA sauvegardeJPA =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        
        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.save(...) sur le DAO mocké 
         * retourne un SousTypeProduitJPA.
         */
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenReturn(sauvegardeJPA);

        /* ACT :
         * sollicite la méthode creer(...)
         * dans un scénario nominal complet.
         */
        final SousTypeProduit retour = this.service.creer(stp);

        /* ASSERT :
         * vérifie que l'objet métier retourné
         * est cohérent avec les données sauvegardées.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getIdSousTypeProduit()).isEqualTo(ID_2);
        assertThat(retour.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_1);
        assertThat(retour.getTypeProduit()).isNotNull();
        assertThat(retour.getTypeProduit().getTypeProduit()).isEqualTo("Type/Produit_1");

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que : 
         * - typeProduitDaoJPA.findById(ID_1) a été appelé une fois.
         * - sousTypeProduitDaoJPA.save(...) a été appelé une fois.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(OK) :</p>
     * <ul>
     * <li>retourne un objet métier non null ;</li>
     * <li>retourne l'identifiant objet métier issu du stockage ;</li>
     * <li>retourne le libellé objet métier attendu ;</li>
     * <li>retourne un parent non null ;</li>
     * <li>retourne le libellé parent attendu ;</li>
     * <li>appelle findById(...) sur le parent puis save(...).</li>
     * </ul>
     * </div>
     * @throws Exception 
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(OK) : retourne un objet métier cohérent")
    @Test
    public void testCreerNominal() throws Exception {

        /* ARRANGE :
         * prépare un scénario nominal complet
         * avec un parent persistant et un save(...) réussi.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        
        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké 
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final SousTypeProduitJPA sauvegardeJPA =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        
        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.save(...) sur le DAO mocké 
         * retourne un SousTypeProduitJPA.
         */
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenReturn(sauvegardeJPA);

        /* ACT :
         * sollicite la méthode creer(...)
         * dans un scénario nominal complet.
         */
        final SousTypeProduit retour = this.service.creer(stp);

        /* ASSERT :
         * vérifie que l'objet métier retourné
         * est cohérent avec les données sauvegardées.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getIdSousTypeProduit()).isEqualTo(ID_2);
        assertThat(retour.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_1);
        assertThat(retour.getTypeProduit()).isNotNull();
        assertThat(retour.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_1);

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que : 
         * - typeProduitDaoJPA.findById(ID_1) a été appelé une fois.
         * - sousTypeProduitDaoJPA.save(...) a été appelé une fois.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    // ======================== RechercherTous ============================



    /**
     * <div>
     * <p>garantit que si DAO.findAll() retourne null :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle le DAO objet métier une fois via {@code findAll()} ;</li>
     * <li>n'appelle ni le DAO parent, ni l'EntityManager.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHERTOUS)
    @DisplayName("rechercherTous(DAO.findAll() retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testRechercherTousDAORetourneNull() {

        /* ARRANGE :
         * configure le DAO objet métier mocké avec Mockito
         * pour que DAO.findAll() retourne null au lieu d'une liste.
         */
        when(this.sousTypeProduitDaoJPA.findAll()).thenReturn(null);

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
         * - sousTypeProduitDaoJPA.findAll() a été appelé une fois ;
         * - le DAO parent n'a jamais été appelé ;
         * - l'EntityManager n'a jamais été appelé.
         */
        verify(this.sousTypeProduitDaoJPA).findAll();
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que si DAO.findAll() retourne une liste vide :</p>
     * <ul>
     * <li>retourne une {@link List} non null ;</li>
     * <li>retourne une liste vide ;</li>
     * <li>ne jette aucune exception ;</li>
     * <li>appelle le DAO objet métier une fois via {@code findAll()} ;</li>
     * <li>n'appelle ni le DAO parent, ni l'EntityManager.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHERTOUS)
    @DisplayName("rechercherTous(DAO.findAll() retourne liste vide) : retourne une liste vide non null")
    @Test
    public void testRechercherTousDAORetourneVide() throws Exception {

        /* ARRANGE :
         * configure le DAO objet métier mocké avec Mockito
         * pour que DAO.findAll() retourne une liste vide.
         */
        when(this.sousTypeProduitDaoJPA.findAll())
            .thenReturn(new ArrayList<SousTypeProduitJPA>());

        /* ACT :
         * appelle this.service.rechercherTous()
         * dans le scénario où le stockage est vide.
         */
        final List<SousTypeProduit> retour = this.service.rechercherTous();

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * une liste non nulle, mais vide.
         */
        assertThat(retour).isNotNull().isEmpty();

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findAll() a été appelé une fois ;
         * - le DAO parent n'a jamais été appelé ;
         * - l'EntityManager n'a jamais été appelé.
         */
        verify(this.sousTypeProduitDaoJPA).findAll();
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    

        
    /**
     * <div>
     * <p>garantit que si DAO.findAll() jette une exception technique avec message non null :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO objet métier une fois via {@code findAll()} ;</li>
     * <li>n'appelle ni le DAO parent, ni l'EntityManager.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHERTOUS)
    @DisplayName("rechercherTous(KO DAO message non null) : jette ExceptionTechniqueGateway")
    @Test
    public void testRechercherTousDAOExceptionMessageNonNull() {

        /* ARRANGE :
         * configure le DAO objet métier mocké avec Mockito
         * pour que DAO.findAll()
         * jette une RuntimeException avec message non null.
         */
        final RuntimeException ex = new RuntimeException(LIBELLE_ENFANT_1);
        when(this.sousTypeProduitDaoJPA.findAll()).thenThrow(ex);

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
        assertThat(throwable).hasMessageContaining(LIBELLE_ENFANT_1);
        assertThat(throwable.getCause()).isSameAs(ex);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findAll() a été appelé une fois ;
         * - le DAO parent n'a jamais été appelé ;
         * - l'EntityManager n'a jamais été appelé.
         */
        verify(this.sousTypeProduitDaoJPA).findAll();
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que si DAO.findAll() jette une exception technique 
     * avec message null :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non null dérivé de l'exception technique ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO objet métier une fois via {@code findAll()} ;</li>
     * <li>n'appelle ni le DAO parent, ni l'EntityManager.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHERTOUS)
    @DisplayName("rechercherTous(KO DAO message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testRechercherTousDAOExceptionMessageNull() {

        /* ARRANGE :
         * configure le DAO objet métier mocké avec Mockito
         * pour que DAO.findAll()
         * jette une RuntimeException sans message.
         */
        final RuntimeException ex = new RuntimeException((String) null);
        when(this.sousTypeProduitDaoJPA.findAll()).thenThrow(ex);

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
         * - sousTypeProduitDaoJPA.findAll() a été appelé une fois ;
         * - le DAO parent n'a jamais été appelé ;
         * - l'EntityManager n'a jamais été appelé.
         */
        verify(this.sousTypeProduitDaoJPA).findAll();
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    

    
    /**
     * <div>
     * <p>garantit que si DAO.findAll() retourne une liste
     * contenant des nulls, des doublons fonctionnels
     * et deux objets métier distincts partageant le même libellé objet métier :</p>
     * <ul>
     * <li>retourne une {@link List} non null ;</li>
     * <li>filtre les éléments null ;</li>
     * <li>dédoublonne les résultats au sens métier
     * sur le couple [parent / libellé objet métier] ;</li>
     * <li>conserve deux objets métier portant le même libellé objet métier
     * lorsqu'ils appartiennent à deux parents différents ;</li>
     * <li>trie les résultats par parent puis par libellé objet métier ;</li>
     * <li>appelle le DAO objet métier une fois ;</li>
     * <li>n'appelle ni le DAO parent, ni l'EntityManager.</li>
     * </ul>
     * <p>Ce test prouve explicitement la règle contractuelle
     * d'unicité métier sur le couple [parent / libellé objet métier].</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHERTOUS)
    @DisplayName("rechercherTous(nulls + doublons de couple) : filtre, dédoublonne et trie")
    @Test
    public void testRechercherTousTriDedoublonnage() throws Exception {

        /* ARRANGE :
         * prépare une liste renvoyée par le DAO objet métier mocké
         * contenant :
         * - une valeur null à filtrer ;
         * - un doublon fonctionnel sur l'objet métier (couple
         *   [parent / libellé objet métier]) ;
         * - deux objets métier ayant le même libellé objet métier,
         *   mais des parents différents, donc non doublons ;
         * - un ordre initial volontairement non trié.
         */
        final TypeProduitJPA parentJPA1 =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA2 =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_2, ID_2);

        final SousTypeProduitJPA enfant2Parent1 =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_2, ID_1, parentJPA1);
        final SousTypeProduitJPA enfant1Parent2 =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA2);
        final SousTypeProduitJPA enfant1Parent1 =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_3, parentJPA1);
        final SousTypeProduitJPA doublonEnfant1Parent1 =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_3, parentJPA1);

        final List<SousTypeProduitJPA> entities =
                new ArrayList<SousTypeProduitJPA>();
        entities.add(enfant2Parent1);
        entities.add(null);
        entities.add(enfant1Parent2);
        entities.add(doublonEnfant1Parent1);
        entities.add(enfant1Parent1);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findAll()
         * sur le DAO objet métier mocké retourne la liste entities.
         */
        when(this.sousTypeProduitDaoJPA.findAll()).thenReturn(entities);

        /* ACT :
         * sollicite la méthode service.rechercherTous()
         * dans un scénario contenant nulls, doublons et tri métier.
         */
        final List<SousTypeProduit> retour = this.service.rechercherTous();

        /* ASSERT :
         * vérifie d'abord que la méthode retourne
         * une liste exploitable, filtrée et dédoublonnée.
         */
        assertThat(retour).isNotNull();
        assertThat(retour).hasSize(3);

        /* Vérifie ensuite l'ordre final attendu :
         * - parent TypeProduit_1 / enfant SousTypeProduit_1 ;
         * - parent TypeProduit_1 / enfant SousTypeProduit_2 ;
         * - parent TypeProduit_2 / enfant SousTypeProduit_1.
         */
        assertThat(retour.get(0).getTypeProduit()).isNotNull();
        assertThat(retour.get(0).getTypeProduit().getTypeProduit())
            .isEqualTo(LIBELLE_PARENT_1);
        assertThat(retour.get(0).getSousTypeProduit())
            .isEqualTo(LIBELLE_ENFANT_1);

        assertThat(retour.get(1).getTypeProduit()).isNotNull();
        assertThat(retour.get(1).getTypeProduit().getTypeProduit())
            .isEqualTo(LIBELLE_PARENT_1);
        assertThat(retour.get(1).getSousTypeProduit())
            .isEqualTo(LIBELLE_ENFANT_2);

        assertThat(retour.get(2).getTypeProduit()).isNotNull();
        assertThat(retour.get(2).getTypeProduit().getTypeProduit())
            .isEqualTo(LIBELLE_PARENT_2);
        assertThat(retour.get(2).getSousTypeProduit())
            .isEqualTo(LIBELLE_ENFANT_1);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findAll() a été appelé une fois ;
         * - le DAO parent n'a jamais été appelé ;
         * - l'EntityManager n'a jamais été appelé.
         */
        verify(this.sousTypeProduitDaoJPA).findAll();
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    


    /**
     * <div>
     * <p>garantit que rechercherTous(OK) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>filtre les valeurs null ;</li>
     * <li>dédoublonne les doublons fonctionnels ;</li>
     * <li>retourne une liste triée ;</li>
     * <li>conserve un parent non null sur les objets métier retournés ;</li>
     * <li>appelle le DAO objet métier une fois via {@code findAll()} ;</li>
     * <li>n'appelle ni le DAO parent, ni l'EntityManager.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHERTOUS)
    @DisplayName("rechercherTous(OK) : filtre, trie et dédoublonne")
    @Test
    public void testRechercherTousNominal() throws Exception {

        /* ARRANGE :
         * prépare une liste renvoyée par le DAO objet métier mocké
         * contenant :
         * - une valeur null à filtrer ;
         * - deux doublons fonctionnels sur le même libellé objet métier
         *   et le même parent ;
         * - un ordre initial non trié.
         *
         * Ce scénario permet de vérifier en une seule fois
         * le filtrage, le tri, le dédoublonnage
         * et la conservation du parent métier.
         */
        final TypeProduitJPA parentJPA =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final SousTypeProduitJPA e1 =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_2, ID_1, parentJPA);
        final SousTypeProduitJPA e2 =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        final SousTypeProduitJPA e3 =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_3, parentJPA);

        final List<SousTypeProduitJPA> entities =
                new ArrayList<SousTypeProduitJPA>();
        entities.add(null);
        entities.add(e1);
        entities.add(e2);
        entities.add(e3);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findAll()
         * sur le DAO objet métier mocké retourne la liste entities.
         */
        when(this.sousTypeProduitDaoJPA.findAll()).thenReturn(entities);

        /* ACT :
         * sollicite la méthode service.rechercherTous()
         * dans un scénario nominal complet.
         */
        final List<SousTypeProduit> retour = this.service.rechercherTous();

        /* ASSERT :
         * vérifie d'abord que la méthode retourne
         * une liste exploitable.
         */
        assertThat(retour).isNotNull();
        assertThat(retour).hasSize(2);

        /* Vérifie ensuite que :
         * - les doublons fonctionnels ont bien été supprimés ;
         * - l'ordre final est trié par libellé objet métier.
         */
        assertThat(retour)
            .extracting(SousTypeProduit::getSousTypeProduit)
            .containsExactly(LIBELLE_ENFANT_1, LIBELLE_ENFANT_2);

        /* Vérifie enfin que les objets métier retournés
         * conservent un parent non null et cohérent.
         */
        assertThat(retour.get(0).getTypeProduit()).isNotNull();
        assertThat(retour.get(0).getTypeProduit().getTypeProduit())
            .isEqualTo(LIBELLE_PARENT_1);

        assertThat(retour.get(1).getTypeProduit()).isNotNull();
        assertThat(retour.get(1).getTypeProduit().getTypeProduit())
            .isEqualTo(LIBELLE_PARENT_1);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findAll() a été appelé une fois ;
         * - le DAO parent n'a jamais été appelé ;
         * - l'EntityManager n'a jamais été appelé.
         */
        verify(this.sousTypeProduitDaoJPA).findAll();
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

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
	    final TypeProduitJPA parentJPA
	        = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

	    final List<SousTypeProduitJPA> contenuJPA
	        = new ArrayList<SousTypeProduitJPA>();
	    contenuJPA.add(fabriquerSousTypeProduitJPA(
	            LIBELLE_ENFANT_1, ID_1, parentJPA));
	    contenuJPA.add(fabriquerSousTypeProduitJPA(
	            LIBELLE_ENFANT_2, ID_2, parentJPA));

	    final Page<SousTypeProduitJPA> page
	        = new PageImpl<SousTypeProduitJPA>(
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
	     * sousTypeProduitDaoJPA.findAll(...) a été appelé.
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
	    when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

	    /* ACT :
	     * appelle le service avec une requête null.
	     *
	     * Le contrat impose alors :
	     * - aucune Exception ;
	     * - utilisation d'une pagination par défaut ;
	     * - délégation au DAO avec un Pageable cohérent.
	     */
	    final ResultatPage<SousTypeProduit> resultat
	        = this.service.rechercherTousParPage(null);

	    /* ASSERT :
	     * garantit d'abord que le service a bien appelé le DAO,
	     * puis permet d'inspecter le Pageable réellement transmis.
	     */
	    verify(this.sousTypeProduitDaoJPA).findAll(captor.capture());

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
	        .extracting(SousTypeProduit::getSousTypeProduit)
	        .containsExactly(LIBELLE_ENFANT_1, LIBELLE_ENFANT_2);

	    /* Garantit que les objets métier retournés
	     * conservent un parent non null et cohérent.
	     */
	    assertThat(resultat.getContent().get(0).getTypeProduit()).isNotNull();
	    assertThat(resultat.getContent().get(0).getTypeProduit().getTypeProduit())
	        .isEqualTo(LIBELLE_PARENT_1);

	    assertThat(resultat.getContent().get(1).getTypeProduit()).isNotNull();
	    assertThat(resultat.getContent().get(1).getTypeProduit().getTypeProduit())
	        .isEqualTo(LIBELLE_PARENT_1);

	    /* Garantit que service.rechercherTousParPage(null)
	     * avec un DAO qui retourne une page valide
	     * ne sollicite ni le DAO parent ni l'EntityManager.
	     */
	    verifyNoInteractions(this.typeProduitDaoJPA);
	    verifyNoInteractions(this.entityManager);

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
        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(null);

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
         * garantit que le service a bien tenté
         * d'interroger le DAO objet métier via findAll(...).
         *
         * Le but de ce test n'est pas de contrôler le détail du Pageable,
         * mais de prouver la réaction contractuelle du service
         * quand le DAO répond null.
         */
        verify(this.sousTypeProduitDaoJPA).findAll(any(Pageable.class));

        /* Garantit que service.rechercherTousParPage(...)
         * avec un DAO qui retourne null
         * ne sollicite ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

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
        final Page<SousTypeProduitJPA> pageMock
            = org.mockito.Mockito.mock(Page.class);
        when(pageMock.getContent()).thenReturn(null);

        /* Simule ensuite un DAO qui renvoie cette page incohérente
         * lorsque le service l'appelle en pagination.
         *
         * Le scénario préparé est donc :
         * DAO.findAll(...) ne renvoie pas null,
         * mais renvoie une Page inutilisable car son contenu est null.
         */
        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(pageMock);

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
         * garantit que le service a bien interrogé
         * le DAO objet métier.
         *
         * Le cœur du test n'est pas ici le détail du Pageable,
         * mais la réaction contractuelle du service
         * face à une Page existante mais incohérente.
         */
        verify(this.sousTypeProduitDaoJPA).findAll(any(Pageable.class));

        /* Garantit que service.rechercherTousParPage(...)
         * avec une Page Spring dont le contenu est null
         * ne sollicite ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
	
	
    /**
     * <div>
     * <p>garantit que si une erreur technique survient pendant l'accès au DAO :</p>
     * <ul>
     * <li>le service ne laisse pas remonter l'Exception brute du stockage ;</li>
     * <li>il la transforme en {@link ExceptionTechniqueGateway} ;</li>
     * <li>il construit un message technique conforme au contrat ;</li>
     * <li>il conserve le message technique d'origine ;</li>
     * <li>il conserve l'Exception technique initiale comme cause ;</li>
     * <li>il n'appelle ni le DAO parent, ni l'EntityManager.</li>
     * </ul>
     * <p>Ce test prouve donc la réaction contractuelle du service
     * face à une panne technique du stockage,
     * et non un simple échec fonctionnel métier.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHERTOUSPARPAGE)
    @DisplayName("rechercherTousParPage(KO DAO message non null) : garantit ExceptionTechniqueGateway avec message et cause propagés")
    @Test
    public void testRechercherTousParPageDAOExceptionMessageNonNull() {

        /* ARRANGE :
         * Configuration du Mock :
         * simule une panne technique côté DAO objet métier.
         *
         * Le DAO ne renvoie ni page valide ni valeur null :
         * il échoue brutalement en lançant une RuntimeException.
         *
         * Le message LIBELLE_ENFANT_1 est volontairement non null,
         * afin de prouver que le service l'intègre bien
         * dans le message final sécurisé de l'ExceptionTechniqueGateway.
         *
         * La cause est stockée dans une variable dédiée
         * pour pouvoir vérifier ensuite que l'ExceptionTechniqueGateway
         * propage exactement cette instance,
         * et pas seulement une RuntimeException quelconque.
         */
        final RuntimeException causeDao = new RuntimeException(LIBELLE_ENFANT_1);

        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class)))
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois le service avec une requête valide
         * afin d'isoler précisément le scénario testé :
         * l'échec doit venir du stockage,
         * pas du paramètre d'entrée.
         *
         * On capture ensuite l'exception réellement levée
         * pour contrôler séparément son type, son message
         * et sa cause technique d'origine.
         */
        final Throwable throwable
            = Assertions.catchThrowable(
                    () -> this.service.rechercherTousParPage(new RequetePage()));

        /* ASSERT :
         * garantit que le service :
         * - requalifie l'exception brute en ExceptionTechniqueGateway ;
         * - conserve le préfixe technique attendu ;
         * - conserve le message technique d'origine LIBELLE_ENFANT_1.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(LIBELLE_ENFANT_1);

        /* Garantit que la cause technique initiale
         * reste bien propagée exactement.
         *
         * isSameAs(causeDao) est plus fort que
         * hasCauseInstanceOf(RuntimeException.class) :
         * on ne prouve pas seulement le type de la cause,
         * on prouve que la cause est l'instance exacte
         * jetée par le DAO mocké.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit que le service a bien tenté
         * d'accéder au stockage via le DAO objet métier.
         */
        verify(this.sousTypeProduitDaoJPA).findAll(any(Pageable.class));

        /* Garantit que service.rechercherTousParPage(...)
         * avec un DAO qui jette une exception technique avec message non null
         * ne sollicite ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
	
		
    /**
     * <div>
     * <p>garantit que si une erreur technique survient pendant l'accès au DAO
     * et que cette erreur ne porte aucun message :</p>
     * <ul>
     * <li>le service ne laisse pas remonter l'Exception brute du stockage ;</li>
     * <li>il la transforme en {@link ExceptionTechniqueGateway} ;</li>
     * <li>il construit un message technique conforme au contrat ;</li>
     * <li>il fabrique un message sûr non null dérivé de l'Exception cause ;</li>
     * <li>il conserve l'Exception technique initiale comme cause ;</li>
     * <li>il n'appelle ni le DAO parent, ni l'EntityManager.</li>
     * </ul>
     * <p>Ce test complète le cas KO DAO avec message non null :</p>
     * <ul>
     * <li>l'autre test prouve la conservation d'un message existant ;</li>
     * <li>celui-ci prouve le comportement de sécurisation
     * quand le message d'origine vaut {@code null}.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHERTOUSPARPAGE)
    @DisplayName("rechercherTousParPage(KO DAO message null) : garantit ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testRechercherTousParPageDAOExceptionMessageNull() {

        /* ARRANGE :
         * Configuration du Mock :
         * simule une panne technique côté DAO objet métier
         * dont le message d'origine vaut null.
         *
         * Ce cas est important car le contrat n'autorise jamais
         * un message final null dans l'ExceptionTechniqueGateway :
         * le service doit donc construire un message sûr.
         *
         * La cause est stockée dans une variable dédiée
         * pour pouvoir vérifier ensuite que l'ExceptionTechniqueGateway
         * propage exactement cette instance,
         * et pas seulement une RuntimeException quelconque.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class)))
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois le service avec une requête valide
         * afin d'isoler précisément le scénario testé :
         * l'échec doit venir du stockage,
         * pas du paramètre d'entrée.
         *
         * On capture ensuite l'exception réellement levée
         * pour contrôler séparément son type, son message
         * et sa cause technique d'origine.
         */
        final Throwable throwable
            = Assertions.catchThrowable(
                    () -> this.service.rechercherTousParPage(new RequetePage()));

        /* ASSERT :
         * garantit que le service :
         * - requalifie l'exception brute en ExceptionTechniqueGateway ;
         * - conserve le préfixe technique attendu ;
         * - fabrique malgré tout un message sûr non null.
         *
         * Avec l'implémentation actuelle de safeMessage(e),
         * ce texte sûr provient au minimum de e.toString().
         * Pour une RuntimeException sans message,
         * cela contient au moins le nom de classe.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(RuntimeException.class.getName());

        /* Garantit que la cause technique initiale
         * reste bien propagée exactement.
         *
         * isSameAs(causeDao) est plus fort que
         * hasCauseInstanceOf(RuntimeException.class) :
         * on ne prouve pas seulement le type de la cause,
         * on prouve que la cause est l'instance exacte
         * jetée par le DAO mocké.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit que le service a bien tenté
         * d'accéder au stockage via le DAO objet métier.
         */
        verify(this.sousTypeProduitDaoJPA).findAll(any(Pageable.class));

        /* Garantit que service.rechercherTousParPage(...)
         * avec un DAO qui jette une exception technique avec message null
         * ne sollicite ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

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
         * prépare une requête métier dont l'appelant demande
         * explicitement une taille zéro.
         *
         * Le point important ici est le comportement réel
         * du Service :
         * la RequetePage ne conserve pas la taille 0,
         * le service la remplace par la taille par défaut.
         */
        final RequetePage requete
            = new RequetePage(0, 0, new ArrayList<TriSpec>());

        /* Prépare ensuite une page DAO cohérente avec cette taille par défaut,
         * ainsi qu'un captor Mockito pour récupérer le Pageable
         * réellement transmis au DAO.
         *
         * Le captor permet de prouver concrètement que le DAO mocké avec Mockito
         * ne reçoit jamais pageSize == 0,
         * mais bien la taille normalisée.
         */
        final TypeProduitJPA parentJPA
            = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final List<SousTypeProduitJPA> contenuJPA
            = new ArrayList<SousTypeProduitJPA>();
        contenuJPA.add(fabriquerSousTypeProduitJPA(
                LIBELLE_ENFANT_1, ID_1, parentJPA));
        contenuJPA.add(fabriquerSousTypeProduitJPA(
                LIBELLE_ENFANT_2, ID_2, parentJPA));

        final Page<SousTypeProduitJPA> page
            = new PageImpl<SousTypeProduitJPA>(
                    contenuJPA,
                    PageRequest.of(
                            RequetePage.PAGE_DEFAUT,
                            RequetePage.TAILLE_DEFAUT),
                    2L);

        final ArgumentCaptor<Pageable> captor
            = ArgumentCaptor.forClass(Pageable.class);

        /* Simule un DAO qui renvoie une page valide
         * quel que soit le Pageable reçu.
         *
         * Le but n'est pas de tester Spring Data,
         * mais de verrouiller ce que le service envoie réellement
         * au DAO après normalisation de la requête.
         */
        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock.
         *
         * Le scénario prouve ici qu'une demande initiale pageSize == 0
         * n'entraîne pas d'Exception
         * et aboutit à une pagination exploitable.
         */
        final ResultatPage<SousTypeProduit> resultat
            = this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * garantit d'abord que le DAO mocké avec Mockito
         * a bien été interrogé,
         * puis permet d'inspecter le Pageable réellement transmis.
         */
        verify(this.sousTypeProduitDaoJPA).findAll(captor.capture());

        final Pageable pageable = captor.getValue();

        /* Garantit que le cœur du comportement réel est respecté :
         * la taille zéro demandée par l'appelant
         * a déjà été normalisée en taille par défaut
         * avant l'appel au stockage.
         */
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(pageable.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(pageable.getSort().isSorted()).isFalse();

        /* Garantit que le service restitue ensuite
         * un ResultatPage métier cohérent
         * avec la page DAO renvoyée.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(resultat.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(resultat.getTotalElements()).isEqualTo(2L);
        assertThat(resultat.getContent())
            .extracting(SousTypeProduit::getSousTypeProduit)
            .containsExactly(LIBELLE_ENFANT_1, LIBELLE_ENFANT_2);

        /* Garantit que les objets métier retournés
         * conservent un parent non null et cohérent.
         */
        assertThat(resultat.getContent().get(0).getTypeProduit()).isNotNull();
        assertThat(resultat.getContent().get(0).getTypeProduit().getTypeProduit())
            .isEqualTo(LIBELLE_PARENT_1);

        assertThat(resultat.getContent().get(1).getTypeProduit()).isNotNull();
        assertThat(resultat.getContent().get(1).getTypeProduit().getTypeProduit())
            .isEqualTo(LIBELLE_PARENT_1);

        /* Garantit que service.rechercherTousParPage(...)
         * avec une RequetePage dont la taille demandée vaut 0
         * et un DAO qui retourne une page valide
         * ne sollicite ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
		
	
    /**
     * <div>
     * <p>garantit que si une {@link RequetePage} contient des consignes de tri :</p>
     * <ul>
     * <li>le service convertit les consignes de tri valides en Sort Spring ;</li>
     * <li>il ignore les consignes de tri inutilisables
     * (entrée {@code null}, propriété vide) ;</li>
     * <li>il transmet au DAO un {@link Pageable} cohérent
     * avec la page, la taille et le tri demandés ;</li>
     * <li>il retourne enfin un {@link ResultatPage} cohérent
     * avec la page renvoyée par le DAO.</li>
     * </ul>
     * <p>Ce test prouve donc à la fois :</p>
     * <ul>
     * <li>la conversion technique de {@link TriSpec} vers Sort Spring ;</li>
     * <li>le filtrage des tris invalides avant l'appel DAO ;</li>
     * <li>la cohérence du résultat métier restitué par le service.</li>
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
         * prépare une requête paginée contenant à la fois :
         * - des tris invalides à ignorer ;
         * - des tris valides à convertir en Sort Spring.
         *
         * Prépare aussi une page DAO cohérente
         * et un captor Mockito pour récupérer le Pageable
         * réellement transmis par le service au DAO.
         *
         * Le captor est utile ici pour prouver concrètement
         * la conversion de la RequetePage :
         * bonne page, bonne taille, bon tri Spring.
         */
        final String propSousTypeProduit = "sousTypeProduit";
        final String propIdSousTypeProduit = "idSousTypeProduit";

        final List<TriSpec> tris = new ArrayList<TriSpec>();
        tris.add(null);
        tris.add(new TriSpec(BLANK, DirectionTri.ASC));
        tris.add(new TriSpec(propSousTypeProduit, DirectionTri.ASC));
        tris.add(new TriSpec(propIdSousTypeProduit, DirectionTri.DESC));

        final RequetePage requete = new RequetePage(1, 3, tris);

        final TypeProduitJPA parentJPA
            = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final List<SousTypeProduitJPA> contenuJPA
            = new ArrayList<SousTypeProduitJPA>();
        contenuJPA.add(fabriquerSousTypeProduitJPA(
                LIBELLE_ENFANT_1, ID_1, parentJPA));

        final Page<SousTypeProduitJPA> page
            = new PageImpl<SousTypeProduitJPA>(
                    contenuJPA,
                    PageRequest.of(1, 3),
                    TOTAL_10);

        final ArgumentCaptor<Pageable> captor
            = ArgumentCaptor.forClass(Pageable.class);

        /*
         * Configuration du Mock :
         * Simule un DAO qui renvoie une page techniquement exploitable
         * (numéro de page, taille, total, contenu non null)
         * quel que soit le Pageable reçu.
         *
         * Le but du test n'est pas d'éprouver le DAO,
         * mais de vérifier la manière dont le service
         * construit l'appel DAO puis reconstruit le résultat métier.
         */
        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * appelle le service avec une requête paginée
         * qui contient volontairement des tris invalides et valides.
         *
         * Le test prouve ainsi que le service
         * ne recopie pas aveuglément la liste des tris reçus :
         * il la nettoie puis construit un Pageable exploitable par Spring Data.
         */
        final ResultatPage<SousTypeProduit> resultat
            = this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * garantit d'abord que le DAO a bien été appelé,
         * puis permet d'inspecter le Pageable transmis.
         *
         * C'est le cœur de la preuve :
         * on ne vérifie pas seulement que le service fonctionne,
         * on vérifie précisément ce qu'il envoie au stockage.
         */
        verify(this.sousTypeProduitDaoJPA).findAll(captor.capture());

        final Pageable pageable = captor.getValue();

        /* Garantit que la pagination a été correctement transmise :
         * le service conserve la page et la taille demandées.
         */
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(3);

        /* Garantit que les TriSpec valides
         * ont été convertis en Sort Spring
         * et que les entrées nulles ou techniquement inutilisables
         * n'ont pas pollué le tri final.
         */
        final org.springframework.data.domain.Sort sort = pageable.getSort();

        assertThat(sort.isSorted()).isTrue();

        assertThat(sort.getOrderFor(propSousTypeProduit)).isNotNull();
        assertThat(sort.getOrderFor(propSousTypeProduit).getDirection())
            .isEqualTo(org.springframework.data.domain.Sort.Direction.ASC);

        assertThat(sort.getOrderFor(propIdSousTypeProduit)).isNotNull();
        assertThat(sort.getOrderFor(propIdSousTypeProduit).getDirection())
            .isEqualTo(org.springframework.data.domain.Sort.Direction.DESC);

        assertThat(sort.getOrderFor(BLANK)).isNull();

        /* Garantit que le service restitue ensuite
         * un ResultatPage métier cohérent
         * avec la page DAO reçue en retour.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getPageNumber()).isEqualTo(1);
        assertThat(resultat.getPageSize()).isEqualTo(3);
        assertThat(resultat.getTotalElements()).isEqualTo(TOTAL_10);
        assertThat(resultat.getContent()).hasSize(1);
        assertThat(resultat.getContent().get(0).getSousTypeProduit())
            .isEqualTo(LIBELLE_ENFANT_1);

        /* Garantit que l'objet métier retourné
         * conserve un parent non null et cohérent.
         */
        assertThat(resultat.getContent().get(0).getTypeProduit()).isNotNull();
        assertThat(resultat.getContent().get(0).getTypeProduit().getTypeProduit())
            .isEqualTo(LIBELLE_PARENT_1);

        /* Garantit que service.rechercherTousParPage(...)
         * avec une RequetePage contenant des tris valides et invalides
         * et un DAO qui retourne une page valide
         * ne sollicite ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que si la page renvoyée par le DAO contient
     * des éléments {@code null} au milieu d'Entities valides :</p>
     * <ul>
     * <li>le service ne propage pas ces {@code null}
     * dans le contenu métier retourné ;</li>
     * <li>il conserve uniquement les {@link SousTypeProduitJPA}
     * effectivement convertissables ;</li>
     * <li>il conserve les parents {@link TypeProduitJPA}
     * portés par les Entities valides ;</li>
     * <li>il retourne donc un contenu métier propre,
     * sans élément {@code null} parasite ;</li>
     * <li>il n'appelle ni le DAO parent, ni l'EntityManager.</li>
     * </ul>
     * <p>Ce test documente ainsi une règle de robustesse
     * pendant la conversion de la page DAO vers la page métier.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHERTOUSPARPAGE)
    @DisplayName("rechercherTousParPage(contenu avec nulls) : garantit l'exclusion des nulls lors de la conversion")
    @Test
    public void testRechercherTousParPageContenuAvecNulls() throws Exception {

        /* ARRANGE :
         * prépare une page DAO techniquement exploitable
         * (numéro de page, taille, total, contenu non null),
         * mais volontairement "sale" :
         * elle contient deux Entities valides
         * et un élément null intercalé.
         *
         * Le but du test est de prouver que le service
         * nettoie ce contenu pendant la conversion métier,
         * au lieu de recopier aveuglément le null.
         */
        final TypeProduitJPA parentJPA
            = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final List<SousTypeProduitJPA> contenuJPA
            = new ArrayList<SousTypeProduitJPA>();
        contenuJPA.add(fabriquerSousTypeProduitJPA(
                LIBELLE_ENFANT_1, ID_1, parentJPA));
        contenuJPA.add(null);
        contenuJPA.add(fabriquerSousTypeProduitJPA(
                LIBELLE_ENFANT_2, ID_2, parentJPA));

        final Page<SousTypeProduitJPA> page
            = new PageImpl<SousTypeProduitJPA>(
                    contenuJPA,
                    PageRequest.of(
                            RequetePage.PAGE_DEFAUT,
                            RequetePage.TAILLE_DEFAUT),
                    3L);

        /*
         * Configuration du Mock :
         * Simule un DAO qui renvoie une page non null,
         * dont le contenu est lui-même non null,
         * mais contient un élément null parasite.
         *
         * Ce scénario n'est donc pas un KO technique du stockage :
         * la Page existe, son content existe,
         * mais le service doit filtrer les éléments non convertissables.
         */
        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * appelle le service avec une requête neutre (RequetePage sans aucun paramètre : new RequetePage()).
         *
         * Le point observé ici est la conversion métier :
         * les SousTypeProduitJPA valides doivent être convertis,
         * tandis que les entrées null doivent être ignorées.
         */
        final ResultatPage<SousTypeProduit> resultat
            = this.service.rechercherTousParPage(new RequetePage());

        /* ASSERT :
         * garantit d'abord que le service
         * a bien interrogé le DAO objet métier.
         */
        verify(this.sousTypeProduitDaoJPA).findAll(any(Pageable.class));

        /* Garantit que le contenu métier final
         * ne contient plus le null parasite,
         * mais seulement les objets métier réellement convertis.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull();
        assertThat(resultat.getContent()).hasSize(2);

        assertThat(resultat.getContent())
            .extracting(SousTypeProduit::getSousTypeProduit)
            .containsExactly(LIBELLE_ENFANT_1, LIBELLE_ENFANT_2);

        /* Garantit que les objets métier retournés
         * conservent un parent non null et cohérent.
         */
        assertThat(resultat.getContent().get(0).getTypeProduit()).isNotNull();
        assertThat(resultat.getContent().get(0).getTypeProduit().getTypeProduit())
            .isEqualTo(LIBELLE_PARENT_1);

        assertThat(resultat.getContent().get(1).getTypeProduit()).isNotNull();
        assertThat(resultat.getContent().get(1).getTypeProduit().getTypeProduit())
            .isEqualTo(LIBELLE_PARENT_1);

        /* Garantit que les métadonnées de pagination
         * restent cohérentes avec la Page Spring renvoyée par le DAO.
         *
         * Le totalElements reste celui annoncé par la Page DAO :
         * il représente la métadonnée de pagination du stockage,
         * pas la taille du contenu métier après filtrage des nulls.
         */
        assertThat(resultat.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(resultat.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(resultat.getTotalElements()).isEqualTo(3L);

        /* Garantit que service.rechercherTousParPage(...)
         * avec un DAO qui retourne une Page Spring contenant des valeurs null
         * ne sollicite ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que si le DAO retourne une Page Spring non null
     * contenant une liste vide :</p>
     * <ul>
     * <li>retourne un {@link ResultatPage} non null ;</li>
     * <li>retourne un contenu non null ;</li>
     * <li>retourne un contenu vide ;</li>
     * <li>retourne des métadonnées cohérentes avec la page vide
     * renvoyée par le stockage (valeurs par défaut) ;</li>
     * <li>appelle une seule fois la méthode findAll(Pageable)
     * du DAO mocké avec Mockito ;</li>
     * <li>n'appelle ni le DAO parent, ni l'EntityManager.</li>
     * </ul>
     * <p>Ce test distingue explicitement une page vide valide
     * d'une anomalie technique de stockage :</p>
     * <ul>
     * <li>une Page Spring {@code null} est un KO technique ;</li>
     * <li>un contenu de Page {@code null} est un KO technique ;</li>
     * <li>une Page Spring non null contenant une liste vide
     * est un résultat valide.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHERTOUSPARPAGE)
    @DisplayName("rechercherTousParPage(retourne page vide) : retourne une page vide cohérente")
    @Test
    public void testRechercherTousParPagePageVide() throws Exception {

        /* ARRANGE :
         * prépare une Page Spring non null,
         * techniquement exploitable (numéro de page, taille, ...),
         * mais dont le contenu métier est vide.
         *
         * Ce scénario ne représente pas une anomalie du stockage :
         * le DAO répond correctement avec une page existante,
         * contenant simplement zéro élément.
         */
        final List<SousTypeProduitJPA> contenu
            = new ArrayList<SousTypeProduitJPA>();

        final Page<SousTypeProduitJPA> page
            = new PageImpl<SousTypeProduitJPA>(
                    contenu,
                    PageRequest.of(
                            RequetePage.PAGE_DEFAUT,
                            RequetePage.TAILLE_DEFAUT),
                    0L);

        /*
         * Configuration du Mock :
         * Simule un DAO qui renvoie une page vide valide
         * lors de l'appel paginé DAO.findAll(Pageable).
         */
        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * appelle le service avec une requête neutre 
         * (RequetePage sans aucun paramètre : new RequetePage()) : 
         * service.rechercherTousParPage(new RequetePage()).
         *
         * Le service doit accepter ce retour du stockage
         * et le convertir en ResultatPage métier vide,
         * sans jeter d'ExceptionTechniqueGateway.
         */
        final ResultatPage<SousTypeProduit> resultat
            = this.service.rechercherTousParPage(new RequetePage());

        /* ASSERT :
         * garantit que le DAO mocké avec Mockito
         * a bien été interrogé une fois via findAll(Pageable).
         */
        verify(this.sousTypeProduitDaoJPA).findAll(any(Pageable.class));

        /* Garantit que la page métier retournée est exploitable :
         * elle existe, son contenu existe,
         * mais ce contenu est vide.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().isEmpty();

        /* Garantit que les métadonnées de pagination
         * restent cohérentes (valeurs par défaut) 
         * avec la page vide renvoyée par le stockage (DAO).
         */
        assertThat(resultat.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(resultat.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(resultat.getTotalElements()).isZero();

        /* Garantit que 
         * service.rechercherTousParPage(...) avec un DAO qui retourne un ResultatPage vide 
         * ne sollicite ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

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
	     * prépare un contenu JPA simple,
	     * une Page Spring cohérente avec la pagination par défaut,
	     * et un captor Mockito pour récupérer le Pageable
	     * réellement transmis par le service au DAO.
	     *
	     * Le captor est utile ici pour prouver qu'une RequetePage
	     * présente mais neutre (sans aucun paramètre : new RequetePage()) 
	     * est correctement convertie
	     * en pagination Spring exploitable par le stockage.
	     */
	    final TypeProduitJPA parentJPA
	        = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

	    final List<SousTypeProduitJPA> contenuJPA
	        = new ArrayList<SousTypeProduitJPA>();
	    contenuJPA.add(fabriquerSousTypeProduitJPA(
	            LIBELLE_ENFANT_1, ID_1, parentJPA));
	    contenuJPA.add(fabriquerSousTypeProduitJPA(
	            LIBELLE_ENFANT_2, ID_2, parentJPA));
	
	    final Page<SousTypeProduitJPA> page
	        = new PageImpl<SousTypeProduitJPA>(
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
         * sousTypeProduitDaoJPA.findAll(...) a été appelé.
         * Il veut aussi inspecter le Pageable construit par le service :
         * - numéro de page ;
         * - taille de page ;
         * - tri réellement transmis au stockage.
         */
	    final ArgumentCaptor<Pageable> captor
	        = ArgumentCaptor.forClass(Pageable.class);
	
	    /* 
	     * Configuration du Mock : 
	     * Simule un DAO qui renvoie une page techniquement exploitable
         * (numéro de page, taille, total, contenu non null).
	     *
	     * Le but du test n'est pas d'éprouver le DAO,
	     * mais de vérifier la conversion :
	     * RequetePage présente mais neutre
	     * (sans aucun paramètre : new RequetePage())
	     * -> Pageable Spring -> ResultatPage métier.
	     */
	    when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);
	
	    /* ACT :
	     * appelle le service avec une RequetePage présente,
	     * mais laissée dans son état neutre
	     * (sans aucun paramètre : new RequetePage()) par défaut.
	     *
	     * Le contrat impose alors au service de la convertir
	     * en Pageable cohérent, sans Exception.
	     */
	    final ResultatPage<SousTypeProduit> resultat
	        = this.service.rechercherTousParPage(new RequetePage());
	
	    /* ASSERT :
	     * garantit d'abord que le service a bien interrogé le DAO,
	     * puis permet d'inspecter concrètement le Pageable transmis.
	     */
	    verify(this.sousTypeProduitDaoJPA).findAll(captor.capture());
	
	    final Pageable pageable = captor.getValue();
	
	    /* Garantit que le cœur du scénario nominal est respecté :
	     * une RequetePage présente mais neutre
	     * (sans aucun paramètre : new RequetePage())
	     * est bien convertie
	     * en pagination Spring par défaut, sans tri imposé.
	     */
	    assertThat(pageable).isNotNull();
	    assertThat(pageable.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
	    assertThat(pageable.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
	    assertThat(pageable.getSort().isSorted()).isFalse();
	
	    /* Garantit que le service reconstruit ensuite
	     * un ResultatPage métier cohérent
	     * avec les informations de pagination et le contenu DAO.
	     */
	    assertThat(resultat).isNotNull();
	    assertThat(resultat.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
	    assertThat(resultat.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
	    assertThat(resultat.getTotalElements()).isEqualTo(2L);
	    assertThat(resultat.getContent())
	        .extracting(SousTypeProduit::getSousTypeProduit)
	        .containsExactly(LIBELLE_ENFANT_1, LIBELLE_ENFANT_2);

	    /* Garantit que les objets métier retournés
	     * conservent un parent non null et cohérent.
	     */
	    assertThat(resultat.getContent().get(0).getTypeProduit()).isNotNull();
	    assertThat(resultat.getContent().get(0).getTypeProduit().getTypeProduit())
	        .isEqualTo(LIBELLE_PARENT_1);

	    assertThat(resultat.getContent().get(1).getTypeProduit()).isNotNull();
	    assertThat(resultat.getContent().get(1).getTypeProduit().getTypeProduit())
	        .isEqualTo(LIBELLE_PARENT_1);

	    /* Garantit que service.rechercherTousParPage(new RequetePage())
	     * avec une requête neutre (RequetePage sans aucun paramètre : new RequetePage())
	     * et un DAO qui retourne une page valide
	     * ne sollicite ni le DAO parent ni l'EntityManager.
	     */
	    verifyNoInteractions(this.typeProduitDaoJPA);
	    verifyNoInteractions(this.entityManager);
	
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(OK) avec une requête explicite :</p>
	 * <ul>
	 * <li>convertit une {@link RequetePage} non nulle,
	 * non neutre, en {@link Pageable} Spring ;</li>
	 * <li>transmet au DAO la page et la taille explicitement demandées ;</li>
	 * <li>ne force aucun tri lorsqu'aucune consigne de tri
	 * n'est demandée ;</li>
	 * <li>retourne un {@link ResultatPage} non null ;</li>
	 * <li>retourne des métadonnées cohérentes avec la page
	 * renvoyée par le stockage ;</li>
	 * <li>retourne le contenu métier converti attendu ;</li>
	 * <li>conserve le parent des objets métier retournés ;</li>
	 * <li>appelle une seule fois la méthode findAll(Pageable)
	 * du DAO mocké avec Mockito ;</li>
	 * <li>n'appelle ni le DAO parent, ni l'EntityManager.</li>
	 * </ul>
	 * <p>Ce test complète les scénarios précédents :</p>
	 * <ul>
	 * <li>{@code rechercherTousParPage(null)} prouve le remplacement
	 * d'une requête absente ;</li>
	 * <li>{@code rechercherTousParPage(new RequetePage())} prouve
	 * la conversion d'une RequetePage présente mais neutre
	 * (sans aucun paramètre : new RequetePage()) ;</li>
	 * <li>ce test prouve le cas nominal avec une pagination
	 * explicitement demandée par l'appelant.</li>
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
	     * prépare une requête paginée explicite.
	     *
	     * Contrairement au test rechercherTousParPage(new RequetePage()),
	     * qui utilise une RequetePage présente mais neutre
	     * (sans aucun paramètre : new RequetePage()),
	     * l'appelant demande ici volontairement :
	     * - la page 1 ;
	     * - une taille de page de 2 ;
	     * - aucun tri.
	     *
	     * Ce scénario prouve donc la conversion nominale
	     * d'une RequetePage réellement renseignée,
	     * sans anomalie de stockage et sans tri à convertir.
	     */
	    final RequetePage requete
	        = new RequetePage(1, 2, new ArrayList<TriSpec>());
	    
	    /* Prépare une page DAO techniquement exploitable
	     * (numéro de page, taille, total, contenu non null)
	     * correspondant à la pagination explicitement demandée.
	     *
	     * Le contenu est volontairement simple et propre :
	     * ce test ne porte ni sur le filtrage des nulls,
	     * ni sur le tri,
	     * ni sur le dédoublonnage.
	     * Ces comportements sont déjà prouvés par d'autres tests.
	     */
	    final TypeProduitJPA parentJPA
	        = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

	    final List<SousTypeProduitJPA> contenuJPA
	        = new ArrayList<SousTypeProduitJPA>();
	    contenuJPA.add(fabriquerSousTypeProduitJPA(
	            LIBELLE_ENFANT_1, ID_1, parentJPA));
	    contenuJPA.add(fabriquerSousTypeProduitJPA(
	            LIBELLE_ENFANT_2, ID_2, parentJPA));
	    
	    final Page<SousTypeProduitJPA> page
	        = new PageImpl<SousTypeProduitJPA>(
	                contenuJPA,
	                PageRequest.of(1, 2),
	                TOTAL_10);
	    
	    /*
	     * Crée un ArgumentCaptor Mockito capable de capturer
	     * l'argument de type Pageable réellement transmis au DAO.
	     *
	     * Ici, le captor permet de prouver concrètement
	     * que la RequetePage explicite a bien été convertie
	     * en Pageable Spring avec :
	     * - le bon numéro de page ;
	     * - la bonne taille de page ;
	     * - aucun tri parasite.
	     */
	    final ArgumentCaptor<Pageable> captor
	        = ArgumentCaptor.forClass(Pageable.class);
	    
	    /*
	     * Configuration du Mock :
	     * Simule un DAO qui renvoie une page valide
	     * lorsque le service interroge le stockage en pagination.
	     */
	    when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);
	    
	    /* ACT :
	     * appelle le service avec une requête paginée explicite.
	     *
	     * Le service doit convertir cette requête en Pageable,
	     * interroger le DAO,
	     * puis reconstruire un ResultatPage métier cohérent.
	     */
	    final ResultatPage<SousTypeProduit> resultat
	        = this.service.rechercherTousParPage(requete);
	    
	    /* ASSERT :
	     * garantit d'abord que le DAO mocké avec Mockito
	     * a bien été interrogé une fois,
	     * puis récupère le Pageable réellement transmis.
	     */
	    verify(this.sousTypeProduitDaoJPA).findAll(captor.capture());
	    
	    final Pageable pageable = captor.getValue();
	    
	    /* Garantit que la pagination explicite demandée
	     * par l'appelant est bien celle transmise au stockage.
	     */
	    assertThat(pageable).isNotNull();
	    assertThat(pageable.getPageNumber()).isEqualTo(1);
	    assertThat(pageable.getPageSize()).isEqualTo(2);
	    assertThat(pageable.getSort().isSorted()).isFalse();
	    
	    /* Garantit que le service retourne une page métier exploitable
	     * et cohérente avec la page renvoyée par le stockage.
	     */
	    assertThat(resultat).isNotNull();
	    assertThat(resultat.getPageNumber()).isEqualTo(1);
	    assertThat(resultat.getPageSize()).isEqualTo(2);
	    assertThat(resultat.getTotalElements()).isEqualTo(TOTAL_10);
	    
	    /* Garantit que le contenu JPA retourné par le DAO
	     * est correctement converti en contenu métier,
	     * sans modifier l'ordre fourni par la page du stockage.
	     */
	    assertThat(resultat.getContent())
	        .extracting(SousTypeProduit::getSousTypeProduit)
	        .containsExactly(LIBELLE_ENFANT_1, LIBELLE_ENFANT_2);

	    /* Garantit que les objets métier retournés
	     * conservent un parent non null et cohérent.
	     */
	    assertThat(resultat.getContent().get(0).getTypeProduit()).isNotNull();
	    assertThat(resultat.getContent().get(0).getTypeProduit().getTypeProduit())
	        .isEqualTo(LIBELLE_PARENT_1);

	    assertThat(resultat.getContent().get(1).getTypeProduit()).isNotNull();
	    assertThat(resultat.getContent().get(1).getTypeProduit().getTypeProduit())
	        .isEqualTo(LIBELLE_PARENT_1);

	    /* Garantit que service.rechercherTousParPage(requete)
	     * avec une requête paginée explicite
	     * et un DAO qui retourne une page valide
	     * ne sollicite ni le DAO parent ni l'EntityManager.
	     */
	    verifyNoInteractions(this.typeProduitDaoJPA);
	    verifyNoInteractions(this.entityManager);
	    
	} // __________________________________________________________________
    
    
                
    // ======================== findByObjetMetier =========================
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL} ;</li>
     * <li>n'appelle ni le DAO parent, ni le DAO objet métier,
     * ni l'EntityManager.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(null) : jette ExceptionAppliParamNull (contrat du port)")
    @Test
    public void testFindByObjetMetierNull() {

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel service.findByObjetMetier(...) 
         * avec un objet métier null
         * jette une ExceptionAppliParamNull
         * avec le message MSG_FINDBYOBJETMETIER_KO_PARAM_NULL
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_PARAM_NULL);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel
         * des DAO ou de l'EntityManager.
         * - typeProduitDaoJPA n'a jamais été appelé ;
         * - sousTypeProduitDaoJPA n'a jamais été appelé ;
         * - entityManager n'a jamais été appelé.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK} ;</li>
     * <li>n'appelle ni le DAO parent, ni le DAO objet métier,
     * ni l'EntityManager.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(libellé blank) : jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testFindByObjetMetierLibelleBlank() {

        /* ARRANGE :
         * prépare un objet métier dont le libellé est blank,
         * afin de vérifier le contrôle applicatif
         * effectué avant toute recherche réelle.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(BLANK, null, parent);

        /* ACT - ASSERT :
         * vérifie que l'appel service.findByObjetMetier(...) 
         * avec un objet métier avec un libellé blank
         * jette une ExceptionAppliLibelleBlank
         * avec le message MSG_FINDBYOBJETMETIER_KO_LIBELLE_BLANK
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel
         * des DAO ou de l'EntityManager.
         * - typeProduitDaoJPA n'a jamais été appelé ;
         * - sousTypeProduitDaoJPA n'a jamais été appelé ;
         * - entityManager n'a jamais été appelé.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParentNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL} ;</li>
     * <li>n'appelle ni le DAO parent, ni le DAO objet métier,
     * ni l'EntityManager.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(parent null) : jette ExceptionAppliParentNull (contrat du port)")
    @Test
    public void testFindByObjetMetierParentNull() {

        /* ARRANGE :
         * prépare un objet métier sans parent,
         * afin de vérifier le contrôle applicatif
         * avant tout appel de DAO imposé par le contrat du port.
         */
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, null);

        /* ACT - ASSERT :
         * vérifie que l'appel service.findByObjetMetier(...) 
         * avec un objet métier avec un parent null
         * jette une ExceptionAppliParentNull
         * avec le message MSG_FINDBYOBJETMETIER_KO_PARENT_NULL
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_PARENT_NULL);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel
         * des DAO ou de l'EntityManager.
         * - typeProduitDaoJPA n'a jamais été appelé ;
         * - sousTypeProduitDaoJPA n'a jamais été appelé ;
         * - entityManager n'a jamais été appelé.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'appelle ni le DAO parent, ni le DAO objet métier,
     * ni l'EntityManager.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(parent libellé blank) : jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testFindByObjetMetierParentLibelleBlank() {

        /* ARRANGE :
         * prépare un objet métier dont le parent porte un libellé blank,
         * afin de vérifier le contrôle applicatif
         * avant tout appel de DAO imposé par le contrat du port.
         */
        final TypeProduit parent = fabriquerTypeProduit(BLANK, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        /* ACT - ASSERT :
         * vérifie que l'appel service.findByObjetMetier(...) 
         * avec un objet métier dont le parent a un libellé blank
         * jette une ExceptionAppliLibelleBlank
         * avec le message MSG_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel
         * des DAO ou de l'EntityManager.
         * - typeProduitDaoJPA n'a jamais été appelé ;
         * - sousTypeProduitDaoJPA n'a jamais été appelé ;
         * - entityManager n'a jamais été appelé.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent ;</li>
     * <li>n'appelle ni le DAO parent, ni le DAO objet métier,
     * ni l'EntityManager.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(parent ID null) : jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testFindByObjetMetierParentIdNull() {

        /* ARRANGE :
         * prépare un objet métier dont le parent n'est pas persistant
         * car son identifiant est null,
         * afin de vérifier le contrôle de persistance
         * avant tout appel de DAO imposé par le contrat du port.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, null);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        /* ACT - ASSERT :
         * vérifie que l'appel service.findByObjetMetier(...) 
         * avec un objet métier dont le parent est non persistant
         * car son identifiant est null
         * jette une ExceptionTechniqueGatewayNonPersistent
         * avec le message MSG_FINDBYOBJETMETIER_PREFIX_PARENT_NON_PERSISTENT
         * complété par le libellé du parent
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(construireMessageNonPersistent(
                    MSG_FINDBYOBJETMETIER_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1));

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel
         * des DAO ou de l'EntityManager.
         * - typeProduitDaoJPA n'a jamais été appelé ;
         * - sousTypeProduitDaoJPA n'a jamais été appelé ;
         * - entityManager n'a jamais été appelé.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent absent DAO) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent ;</li>
     * <li>appelle le DAO parent une fois avec l'identifiant du parent ;</li>
     * <li>n'appelle ni le DAO objet métier, ni l'EntityManager.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(parent absent DAO) : jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testFindByObjetMetierParentAbsent() {

        /* ARRANGE :
         * prépare un objet métier dont le parent est persistant
         * en apparence, mais absent du DAO parent mocké
         * avec Mockito.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        /* Condition du Mock :
         * L'appel typeProduitDaoJPA.findById(ID_1)
         * sur le DAO parent mocké retourne Optional.empty().
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.empty());

        /* ACT - ASSERT :
         * vérifie que l'appel service.findByObjetMetier(...) 
         * avec un objet métier dont le parent est absent du stockage
         * jette une ExceptionTechniqueGatewayNonPersistent
         * avec le message MSG_FINDBYOBJETMETIER_PREFIX_PARENT_NON_PERSISTENT
         * complété par le libellé du parent
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(construireMessageNonPersistent(
                    MSG_FINDBYOBJETMETIER_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1));

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - typeProduitDaoJPA.findById(ID_1) a été appelé une fois ;
         * - sousTypeProduitDaoJPA n'a jamais été appelé ;
         * - entityManager n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
        
    /**
     * <div>
     * <p>garantit que si typeProduitDaoJPA.findById(ID_1)
     * jette une exception technique avec message non null :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine du DAO parent ;</li>
     * <li>propage comme cause l'exception technique d'origine ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>n'appelle ni le DAO objet métier, ni l'EntityManager.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(KO DAO parent message non null) : jette ExceptionTechniqueGateway")
    @Test
    public void testFindByObjetMetierParentDAOExceptionMessageNonNull() {

        /* ARRANGE :
         * prépare un objet métier valide
         * afin d'atteindre réellement la vérification du parent
         * dans le stockage.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        /* Configure le DAO parent mocké avec Mockito
         * pour que findById(ID_1)
         * jette une RuntimeException avec message non null.
         */
        final RuntimeException causeDao = new RuntimeException(LIBELLE_PARENT_1);

        when(this.typeProduitDaoJPA.findById(ID_1)).thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois service.findByObjetMetier(stp)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable
            = Assertions.catchThrowable(
                    () -> this.service.findByObjetMetier(stp));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son message contractuel sécurisé
         * et le message technique d'origine du DAO parent.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(LIBELLE_PARENT_1);

        /* Vérifie que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - le DAO parent a été appelé une fois avec ID_1 ;
         * - le DAO objet métier n'a jamais été appelé ;
         * - l'EntityManager n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que si typeProduitDaoJPA.findById(ID_1)
     * jette une exception technique avec message null :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non null dérivé de l'exception technique ;</li>
     * <li>propage comme cause l'exception technique d'origine ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>n'appelle ni le DAO objet métier, ni l'EntityManager.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(KO DAO parent message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testFindByObjetMetierParentDAOExceptionMessageNull() {

        /* ARRANGE :
         * prépare un objet métier valide
         * afin d'atteindre réellement la vérification du parent
         * dans le stockage.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        /* Configure le DAO parent mocké avec Mockito
         * pour que findById(ID_1)
         * jette une RuntimeException sans message.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.typeProduitDaoJPA.findById(ID_1)).thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois service.findByObjetMetier(stp)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable
            = Assertions.catchThrowable(
                    () -> this.service.findByObjetMetier(stp));

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
         * - le DAO parent a été appelé une fois avec ID_1 ;
         * - le DAO objet métier n'a jamais été appelé ;
         * - l'EntityManager n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que si sousTypeProduitDaoJPA
     * .findAllByTypeProduit(parentJPA) retourne null :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé ;</li>
     * <li>n'appelle pas l'EntityManager.</li>
     * </ul>
     * <p>Ce test distingue un retour {@code null} du DAO objet métier
     * d'une liste vide valide : ici, le retour {@code null}
     * est traité comme une anomalie technique de stockage.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(findAllByTypeProduit retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testFindByObjetMetierDAORetourneNull() {

        /* ARRANGE :
         * prépare un objet métier valide
         * afin d'atteindre réellement la recherche
         * de l'objet métier dans le stockage.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        /* Configure le DAO parent mocké avec Mockito
         * pour que findById(ID_1)
         * retourne le parent JPA attendu.
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Configure le DAO objet métier mocké avec Mockito
         * pour que findAllByTypeProduit(parentJPA)
         * retourne null.
         */
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que le retour null du DAO objet métier
         * n'est pas interprété comme une absence de résultat,
         * mais comme une anomalie technique de stockage.
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - le DAO parent a été appelé une fois avec ID_1 ;
         * - le DAO objet métier a été appelé une fois avec parentJPA ;
         * - l'EntityManager n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, times(1)).findAllByTypeProduit(parentJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que si sousTypeProduitDaoJPA
     * .findAllByTypeProduit(parentJPA) jette une exception technique
     * avec message non null :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine du DAO objet métier ;</li>
     * <li>propage comme cause l'exception technique d'origine ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé ;</li>
     * <li>n'appelle pas l'EntityManager.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(KO DAO objet métier message non null) : jette ExceptionTechniqueGateway")
    @Test
    public void testFindByObjetMetierDAOExceptionMessageNonNull() {

        /* ARRANGE :
         * prépare un objet métier valide
         * afin d'atteindre réellement la recherche
         * de l'objet métier dans le stockage.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        /* Configure le DAO parent mocké avec Mockito
         * pour que findById(ID_1)
         * retourne le parent JPA attendu.
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Configure le DAO objet métier mocké avec Mockito
         * pour que findAllByTypeProduit(parentJPA)
         * jette une RuntimeException avec message non null.
         */
        final RuntimeException causeDao = new RuntimeException(LIBELLE_ENFANT_1);

        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois service.findByObjetMetier(stp)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable
            = Assertions.catchThrowable(
                    () -> this.service.findByObjetMetier(stp));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son message contractuel sécurisé
         * et le message technique d'origine du DAO objet métier.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(LIBELLE_ENFANT_1);

        /* Vérifie que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - le DAO parent a été appelé une fois avec ID_1 ;
         * - le DAO objet métier a été appelé une fois avec parentJPA ;
         * - l'EntityManager n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, times(1)).findAllByTypeProduit(parentJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que si sousTypeProduitDaoJPA
     * .findAllByTypeProduit(parentJPA) jette une exception technique
     * avec message null :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non null dérivé de l'exception technique ;</li>
     * <li>propage comme cause l'exception technique d'origine ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé ;</li>
     * <li>n'appelle pas l'EntityManager.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(KO DAO objet métier message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testFindByObjetMetierDAOExceptionMessageNull() {

        /* ARRANGE :
         * prépare un objet métier valide
         * afin d'atteindre réellement la recherche
         * de l'objet métier dans le stockage.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        /* Configure le DAO parent mocké avec Mockito
         * pour que findById(ID_1)
         * retourne le parent JPA attendu.
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Configure le DAO objet métier mocké avec Mockito
         * pour que findAllByTypeProduit(parentJPA)
         * jette une RuntimeException sans message.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois service.findByObjetMetier(stp)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable
            = Assertions.catchThrowable(
                    () -> this.service.findByObjetMetier(stp));

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
         * - le DAO parent a été appelé une fois avec ID_1 ;
         * - le DAO objet métier a été appelé une fois avec parentJPA ;
         * - l'EntityManager n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, times(1)).findAllByTypeProduit(parentJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que si sousTypeProduitDaoJPA
     * .findAllByTypeProduit(parentJPA) retourne une liste vide :</p>
     * <ul>
     * <li>ne jette aucune exception ;</li>
     * <li>retourne {@code null} car aucun objet métier
     * n'est attaché au parent persistant ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé ;</li>
     * <li>n'appelle pas l'EntityManager.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(parent sans objet métier) : retourne null")
    @Test
    public void testFindByObjetMetierParentSansObjetMetier() throws Exception {

        /* ARRANGE :
         * prépare un objet métier valide
         * afin d'atteindre réellement la recherche
         * de l'objet métier dans le stockage.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        /* Configure le DAO parent mocké avec Mockito
         * pour que findById(ID_1)
         * retourne le parent JPA attendu.
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Configure le DAO objet métier mocké avec Mockito
         * pour que findAllByTypeProduit(parentJPA)
         * retourne une liste vide non null.
         */
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA))
            .thenReturn(new ArrayList<SousTypeProduitJPA>());

        /* ACT :
         * appelle service.findByObjetMetier(stp)
         * dans le scénario où aucun objet métier
         * n'est attaché au parent persistant.
         */
        final SousTypeProduit retour = this.service.findByObjetMetier(stp);

        /* ASSERT :
         * vérifie que la méthode retourne null
         * lorsque le DAO objet métier retourne une liste vide non null.
         */
        assertThat(retour).isNull();

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - le DAO parent a été appelé une fois avec ID_1 ;
         * - le DAO objet métier a été appelé une fois avec parentJPA ;
         * - l'EntityManager n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, times(1)).findAllByTypeProduit(parentJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(non trouvé) :</p>
     * <ul>
     * <li>ne jette aucune exception ;</li>
     * <li>retourne {@code null} lorsque la liste non null
     * retournée par le DAO objet métier ne contient pas
     * le libellé recherché ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé ;</li>
     * <li>n'appelle pas l'EntityManager.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(non trouvé) : délègue au DAO et retourne null")
    @Test
    public void testFindByObjetMetierNonTrouve() throws Exception {

        /* ARRANGE :
         * prépare un objet métier valide
         * afin d'atteindre réellement la recherche
         * de l'objet métier dans le stockage.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_3, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        /* Configure le DAO parent mocké avec Mockito
         * pour que findById(ID_1)
         * retourne le parent JPA attendu.
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final List<SousTypeProduitJPA> entities = new ArrayList<SousTypeProduitJPA>();
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA));
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_2, ID_3, parentJPA));

        /* Configure le DAO objet métier mocké avec Mockito
         * pour que findAllByTypeProduit(parentJPA)
         * retourne une liste non null
         * ne contenant pas le libellé recherché.
         */
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenReturn(entities);

        /* ACT :
         * appelle service.findByObjetMetier(stp)
         * dans le scénario où aucun élément ne correspond.
         */
        final SousTypeProduit retour = this.service.findByObjetMetier(stp);

        /* ASSERT :
         * vérifie que la méthode retourne null
         * lorsque le libellé recherché n'est pas trouvé
         * dans la liste retournée par le DAO objet métier.
         */
        assertThat(retour).isNull();

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - le DAO parent a été appelé une fois avec ID_1 ;
         * - le DAO objet métier a été appelé une fois avec parentJPA ;
         * - l'EntityManager n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, times(1)).findAllByTypeProduit(parentJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(OK) :</p>
     * <ul>
     * <li>ne jette aucune exception ;</li>
     * <li>recherche l'objet métier dans la liste non null
     * retournée par le DAO objet métier sans tenir compte de la casse ;</li>
     * <li>ignore les éléments {@code null} présents dans la liste retournée ;</li>
     * <li>retourne un objet métier converti portant le bon identifiant ;</li>
     * <li>retourne un objet métier converti portant le libellé issu du stockage ;</li>
     * <li>retourne un parent non null portant le bon identifiant ;</li>
     * <li>retourne un parent portant le bon libellé ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé ;</li>
     * <li>n'appelle pas l'EntityManager.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(OK casse différente) : délègue aux DAO et retourne l'objet métier converti")
    @Test
    public void testFindByObjetMetierNominal() throws Exception {

        /* ARRANGE :
         * prépare un objet métier valide
         * dont le libellé recherché utilise volontairement
         * une casse différente de celle présente dans le stockage,
         * afin de prouver la recherche sans tenir compte de la casse.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(
                LIBELLE_ENFANT_2.toUpperCase(Locale.ROOT), null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        /* Configure le DAO parent mocké avec Mockito
         * pour que findById(ID_1)
         * retourne le parent JPA attendu.
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final List<SousTypeProduitJPA> entities = new ArrayList<SousTypeProduitJPA>();
        entities.add(null);
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA));
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_2, ID_3, parentJPA));

        /* Configure le DAO objet métier mocké avec Mockito
         * pour que findAllByTypeProduit(parentJPA)
         * retourne une liste non null
         * contenant un élément null,
         * un élément non correspondant,
         * puis l'élément recherché avec une casse différente
         * de celle portée par l'objet métier d'entrée.
         */
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenReturn(entities);

        /* ACT :
         * appelle service.findByObjetMetier(stp)
         * dans le scénario nominal où l'élément recherché est présent
         * avec une casse différente.
         */
        final SousTypeProduit retour = this.service.findByObjetMetier(stp);

        /* ASSERT :
         * vérifie que la méthode retourne un objet métier converti
         * portant l'identifiant et le libellé issus du stockage.
         *
         * L'objet d'entrée porte LIBELLE_ENFANT_2 en majuscules,
         * alors que l'Entity trouvée porte LIBELLE_ENFANT_2.
         * Cette assertion prouve donc que la recherche a bien été faite
         * sans tenir compte de la casse.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getIdSousTypeProduit()).isEqualTo(ID_3);
        assertThat(retour.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_2);

        /* Vérifie que le parent de l'objet métier retourné
         * est non null et cohérent avec le parent JPA trouvé.
         */
        assertThat(retour.getTypeProduit()).isNotNull();
        assertThat(retour.getTypeProduit().getIdTypeProduit()).isEqualTo(ID_1);
        assertThat(retour.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_1);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - le DAO parent a été appelé une fois avec ID_1 ;
         * - le DAO objet métier a été appelé une fois avec parentJPA ;
         * - l'EntityManager n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, times(1)).findAllByTypeProduit(parentJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    

    
    // ========================== findByLibelle ===========================



    /**
     * <div>
     * <p>garantit que findByLibelle(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK} ;</li>
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
         * le traitement avant tout appel au DAO parent,
         * au DAO objet métier ou à l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK} ;</li>
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
         * le traitement avant tout appel au DAO parent,
         * au DAO objet métier ou à l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(DAO retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon libellé exact ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>n'appelle jamais la recherche rapide DAO ;</li>
     * <li>ne sollicite ni le DAO parent, ni l'EntityManager.</li>
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
         * mocké avec Mockito avec le libellé LIBELLE_ENFANT_1 via
         * findBySousTypeProduitIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra répondre null".
         *
         * On simule donc volontairement un défaut technique du stockage :
         * pour une recherche par libellé exact non blank,
         * le DAO objet métier ne retourne pas une liste vide valide,
         * mais une valeur null interdite par le contrat.
         */
        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1))
            .thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que le retour null du DAO objet métier
         * n'est pas interprété comme une absence de résultat,
         * mais comme une anomalie technique de stockage.
         */
        assertThatThrownBy(() -> this.service.findByLibelle(LIBELLE_ENFANT_1))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon libellé exact.
         */
        verify(this.sousTypeProduitDaoJPA, times(1))
            .findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.sousTypeProduitDaoJPA, never()).findAll();

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via la recherche rapide dédiée
         * findBySousTypeProduitContainingIgnoreCase(...).
         */
        verify(this.sousTypeProduitDaoJPA, never())
            .findBySousTypeProduitContainingIgnoreCase(any(String.class));

        /* Garantit que ce scénario ne sollicite
         * ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(KO DAO message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon libellé exact ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>n'appelle jamais la recherche rapide DAO ;</li>
     * <li>ne sollicite ni le DAO parent, ni l'EntityManager.</li>
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
         * mocké avec Mockito avec le libellé LIBELLE_ENFANT_1 via
         * findBySousTypeProduitIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra jeter
         * une RuntimeException avec message non null".
         *
         * On simule donc volontairement une panne technique du stockage
         * lors de la recherche exacte DAO.
         */
        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1))
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois this.service.findByLibelle(LIBELLE_ENFANT_1)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.findByLibelle(LIBELLE_ENFANT_1));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel,
         * le message technique d'origine
         * et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable).hasMessageContaining(MSG_BOOM);
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon libellé exact.
         */
        verify(this.sousTypeProduitDaoJPA, times(1))
            .findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.sousTypeProduitDaoJPA, never()).findAll();

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via la recherche rapide dédiée
         * findBySousTypeProduitContainingIgnoreCase(...).
         */
        verify(this.sousTypeProduitDaoJPA, never())
            .findBySousTypeProduitContainingIgnoreCase(any(String.class));

        /* Garantit que ce scénario ne sollicite
         * ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non null dérivé de l'exception technique ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon libellé exact ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>n'appelle jamais la recherche rapide DAO ;</li>
     * <li>ne sollicite ni le DAO parent, ni l'EntityManager.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLE)
    @DisplayName("findByLibelle(KO DAO message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testFindByLibelleDAOExceptionMessageNull() {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito avec le libellé LIBELLE_ENFANT_1 via
         * findBySousTypeProduitIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra jeter
         * une RuntimeException sans message".
         *
         * On simule donc volontairement une panne technique du stockage
         * dont le message d'origine vaut null.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1))
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois this.service.findByLibelle(LIBELLE_ENFANT_1)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.findByLibelle(LIBELLE_ENFANT_1));

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
        verify(this.sousTypeProduitDaoJPA, times(1))
            .findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.sousTypeProduitDaoJPA, never()).findAll();

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via la recherche rapide dédiée
         * findBySousTypeProduitContainingIgnoreCase(...).
         */
        verify(this.sousTypeProduitDaoJPA, never())
            .findBySousTypeProduitContainingIgnoreCase(any(String.class));

        /* Garantit que ce scénario ne sollicite
         * ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

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
     * <li>ne sollicite ni le DAO parent, ni l'EntityManager.</li>
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
         * mocké avec Mockito avec le libellé LIBELLE_ENFANT_1 via
         * findBySousTypeProduitIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra répondre
         * une liste vide".
         *
         * On simule donc volontairement un stockage
         * dans lequel aucun objet métier ne correspond au libellé exact recherché.
         */
        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1))
            .thenReturn(new ArrayList<SousTypeProduitJPA>());

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock.
         */
        final List<SousTypeProduit> resultat =
                this.service.findByLibelle(LIBELLE_ENFANT_1);

        /* ASSERT */
        /* Garantit que this.service.findByLibelle(LIBELLE_ENFANT_1)
         * retourne une liste non null et vide
         * lorsque le DAO objet métier ne trouve aucun résultat.
         */
        assertThat(resultat).isNotNull().isEmpty();

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon libellé exact.
         */
        verify(this.sousTypeProduitDaoJPA, times(1))
            .findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.sousTypeProduitDaoJPA, never()).findAll();

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via la recherche rapide dédiée
         * findBySousTypeProduitContainingIgnoreCase(...).
         */
        verify(this.sousTypeProduitDaoJPA, never())
            .findBySousTypeProduitContainingIgnoreCase(any(String.class));

        /* Garantit que ce scénario ne sollicite
         * ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(nominal) :</p>
     * <ul>
     * <li>délègue au DAO objet métier la recherche exacte via
     * {@code findBySousTypeProduitIgnoreCase(...)} ;</li>
     * <li>retourne une {@link List} non null ;</li>
     * <li>filtre les éléments null issus du stockage ;</li>
     * <li>dédoublonne les résultats au sens métier
     * sur l'objet métier [parent / libellé objet métier] ;</li>
     * <li>conserve deux objets métier portant le même libellé enfant
     * lorsqu'ils appartiennent à deux parents différents ;</li>
     * <li>retourne les objets métier triés par parent
     * puis par libellé enfant ;</li>
     * <li>conserve le parent des objets métier retournés ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon libellé exact ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>n'appelle jamais la recherche rapide DAO ;</li>
     * <li>ne sollicite ni le DAO parent, ni l'EntityManager.</li>
     * </ul>
     * <p>Ce test prouve directement au niveau de la méthode publique
     * le filtrage des null, l'insensibilité à la casse,
     * le dédoublonnage métier sur le couple parent / objet métier
     * et le tri final par parent puis par libellé enfant.</p>
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
         * mocké avec Mockito avec le libellé LIBELLE_ENFANT_1 via
         * findBySousTypeProduitIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra répondre
         * une collection persistante non triée,
         * contenant un null
         * et un doublon métier sur le couple parent / objet métier".
         *
         * On simule donc volontairement un stockage
         * qui retourne :
         * - un élément null ;
         * - deux objets métier fonctionnellement identiques
         *   ne différant que par la casse du libellé de l'objet métier ;
         * - deux parents distincts ;
         * - plusieurs valeurs non triées.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réponse
         * afin de prouver comment le service réagit
         * au cas contractuel "scénario nominal complet".
         */
        final TypeProduitJPA parentJPA1 =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA2 =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_2, ID_2);

        final SousTypeProduitJPA enfantParent2 =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_3, parentJPA2);

        final SousTypeProduitJPA enfantParent1 =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_1, parentJPA1);

        final SousTypeProduitJPA doublonParent1 =
                fabriquerSousTypeProduitJPA(
                        LIBELLE_ENFANT_1.toUpperCase(Locale.ROOT),
                        ID_2,
                        parentJPA1);

        final List<SousTypeProduitJPA> entities = new ArrayList<SousTypeProduitJPA>();
        entities.add(enfantParent2);
        entities.add(null);
        entities.add(doublonParent1);
        entities.add(enfantParent1);

        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1))
            .thenReturn(entities);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock.
         */
        final List<SousTypeProduit> resultat =
                this.service.findByLibelle(LIBELLE_ENFANT_1);

        /* ASSERT */
        /* Garantit que this.service.findByLibelle(LIBELLE_ENFANT_1)
         * - retourne une liste non null ;
         * - ne contient plus aucun élément null ;
         * - dédoublonne les résultats au sens métier
         *   sur l'objet métier [couple parent / libellé objet métier] ;
         * - conserve deux objets métier portant le même libellé enfant
         *   lorsqu'ils appartiennent à deux parents différents ;
         * - restitue le résultat final trié
         *   par parent puis par libellé objet métier.
         */
        assertThat(resultat).isNotNull().doesNotContainNull().hasSize(2);

        assertThat(resultat)
            .allSatisfy(sousTypeProduit ->
                assertThat(sousTypeProduit.getTypeProduit()).isNotNull());

        assertThat(resultat)
            .extracting(sousTypeProduit ->
                sousTypeProduit.getTypeProduit().getTypeProduit())
            .containsExactly(
                LIBELLE_PARENT_1,
                LIBELLE_PARENT_2);

        /* L'extraction normalisée en trim + lowerCase(Locale.ROOT)
         * est volontaire :
         * elle permet de prouver directement le dédoublonnage métier
         * sans tenir compte de la casse.
         */
        assertThat(resultat)
            .extracting(sousTypeProduit -> sousTypeProduit.getSousTypeProduit()
                .trim()
                .toLowerCase(Locale.ROOT))
            .containsExactly(
                LIBELLE_ENFANT_1.toLowerCase(Locale.ROOT),
                LIBELLE_ENFANT_1.toLowerCase(Locale.ROOT));

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon libellé exact.
         */
        verify(this.sousTypeProduitDaoJPA, times(1))
            .findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.sousTypeProduitDaoJPA, never()).findAll();

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via la recherche rapide dédiée
         * findBySousTypeProduitContainingIgnoreCase(...).
         */
        verify(this.sousTypeProduitDaoJPA, never())
            .findBySousTypeProduitContainingIgnoreCase(any(String.class));

        /* Garantit que ce scénario ne sollicite
         * ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    

    // ======================== findByLibelleRapide =======================
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL} ;</li>
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
         * le traitement avant tout appel au DAO parent,
         * au DAO objet métier ou à l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(blank) :</p>
     * <ul>
     * <li>délègue au DAO la recherche complète via {@code findAll()} ;</li>
     * <li>retourne une {@link List} non null ;</li>
     * <li>retourne tous les objets métier issus du stockage ;</li>
     * <li>conserve le parent des objets métier retournés ;</li>
     * <li>n'appelle jamais la recherche rapide DAO dédiée.</li>
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
         * une liste complète de SousTypeProduitJPA persistants".
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
        final TypeProduitJPA parentJPA =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final List<SousTypeProduitJPA> entities =
                new ArrayList<SousTypeProduitJPA>();
        entities.add(fabriquerSousTypeProduitJPA(
                LIBELLE_ENFANT_1, ID_1, parentJPA));
        entities.add(fabriquerSousTypeProduitJPA(
                LIBELLE_ENFANT_2, ID_2, parentJPA));

        when(this.sousTypeProduitDaoJPA.findAll()).thenReturn(entities);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE). */
        final List<SousTypeProduit> resultat =
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
            .extracting(SousTypeProduit::getSousTypeProduit)
            .containsExactly(LIBELLE_ENFANT_1, LIBELLE_ENFANT_2);

        /* Garantit que les objets métier retournés
         * conservent un parent non null et cohérent.
         */
        assertThat(resultat.get(0).getTypeProduit()).isNotNull();
        assertThat(resultat.get(0).getTypeProduit().getTypeProduit())
            .isEqualTo(LIBELLE_PARENT_1);

        assertThat(resultat.get(1).getTypeProduit()).isNotNull();
        assertThat(resultat.get(1).getTypeProduit().getTypeProduit())
            .isEqualTo(LIBELLE_PARENT_1);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * via findAll().
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findAll();

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via la recherche rapide dédiée
         * findBySousTypeProduitContainingIgnoreCase(...).
         */
        verify(this.sousTypeProduitDaoJPA, never())
            .findBySousTypeProduitContainingIgnoreCase(any());

        /* Garantit que ce scénario ne sollicite
         * ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(DAO retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle le DAO objet métier une fois avec le bon contenu de recherche rapide ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>ne sollicite ni le DAO parent, ni l'EntityManager.</li>
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
         * mocké avec Mockito avec le contenu CONTENU_PARTIEL via
         * findBySousTypeProduitContainingIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra répondre null".
         *
         * On simule donc volontairement un stockage
         * qui retourne null au lieu d'une collection persistante.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réponse
         * afin de prouver comment le service réagit
         * au cas contractuel "DAO retourne null".
         */
        when(this.sousTypeProduitDaoJPA
                .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL))
            .thenReturn(null);

        /* ACT - ASSERT */
        /* Garantit que this.service.findByLibelleRapide(CONTENU_PARTIEL)
         * - jette une ExceptionTechniqueGateway
         * - émet exactement le message ERREUR_TECHNIQUE_KO_STOCKAGE.
         */
        assertThatThrownBy(() -> this.service.findByLibelleRapide(CONTENU_PARTIEL))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon contenu de recherche rapide.
         */
        verify(this.sousTypeProduitDaoJPA, times(1))
            .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.sousTypeProduitDaoJPA, never()).findAll();

        /* Garantit que ce scénario ne sollicite
         * ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(KO DAO message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine du DAO objet métier ;</li>
     * <li>propage comme cause l'exception technique d'origine ;</li>
     * <li>appelle le DAO objet métier une fois avec le bon contenu de recherche rapide ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>ne sollicite ni le DAO parent, ni l'EntityManager.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(KO DAO message non null) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testFindByLibelleRapideDAOExceptionMessageNonNull() {

        /* ARRANGE :
         * configure ici le comportement du DAO objet métier mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito avec le contenu CONTENU_PARTIEL via
         * findBySousTypeProduitContainingIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra lancer
         * une RuntimeException portant le message CONTENU_PARTIEL".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant la recherche rapide par contenu.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réaction
         * afin de prouver comment le service réagit
         * au cas contractuel "KO DAO message non null".
         */
        final RuntimeException causeDao = new RuntimeException(CONTENU_PARTIEL);

        when(this.sousTypeProduitDaoJPA
                .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL))
            .thenThrow(causeDao);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        /* Exécute une seule fois this.service.findByLibelleRapide(CONTENU_PARTIEL)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = Assertions.catchThrowable(
                    () -> this.service.findByLibelleRapide(CONTENU_PARTIEL));

        /* ASSERT */
        /* Garantit que this.service.findByLibelleRapide(CONTENU_PARTIEL)
         * - jette une ExceptionTechniqueGateway
         * - émet un message commençant par ERREUR_TECHNIQUE_STOCKAGE
         * - conserve le message technique d'origine CONTENU_PARTIEL.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(CONTENU_PARTIEL);

        /* Garantit que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon contenu de recherche rapide.
         */
        verify(this.sousTypeProduitDaoJPA, times(1))
            .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.sousTypeProduitDaoJPA, never()).findAll();

        /* Garantit que ce scénario ne sollicite
         * ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique ;</li>
     * <li>propage comme cause l'exception technique d'origine ;</li>
     * <li>appelle le DAO objet métier une fois avec le bon contenu de recherche rapide ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>ne sollicite ni le DAO parent, ni l'EntityManager.</li>
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
         * mocké avec Mockito avec le contenu CONTENU_PARTIEL via
         * findBySousTypeProduitContainingIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra lancer
         * une RuntimeException sans message".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant la recherche rapide par contenu,
         * avec un message technique d'origine null.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réaction
         * afin de prouver comment le service réagit
         * au cas contractuel "KO DAO message null".
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.sousTypeProduitDaoJPA
                .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL))
            .thenThrow(causeDao);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        /* Exécute une seule fois this.service.findByLibelleRapide(CONTENU_PARTIEL)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = Assertions.catchThrowable(
                    () -> this.service.findByLibelleRapide(CONTENU_PARTIEL));

        /* ASSERT */
        /* Garantit que this.service.findByLibelleRapide(CONTENU_PARTIEL)
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

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon contenu de recherche rapide.
         */
        verify(this.sousTypeProduitDaoJPA, times(1))
            .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.sousTypeProduitDaoJPA, never()).findAll();

        /* Garantit que ce scénario ne sollicite
         * ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>Test didactique non contractuel : il documente que le service
     * transmet tel quel le contenu partiel au DAO objet métier,
     * y compris lorsque ce contenu contient un caractère spécial.</p>
     * <p>garantit que findByLibelleRapide(
     * contenu partiel avec caractères spéciaux) :</p>
     * <ul>
     * <li>délègue au DAO objet métier la recherche rapide via
     * {@code findBySousTypeProduitContainingIgnoreCase(...)} ;</li>
     * <li>ne jette aucune exception ;</li>
     * <li>retourne une {@link List} non null ;</li>
     * <li>retourne l'objet métier issu du stockage ;</li>
     * <li>conserve le parent de l'objet métier retourné ;</li>
     * <li>n'appelle jamais DAO.{@code findAll()} ;</li>
     * <li>ne sollicite ni le DAO parent, ni l'EntityManager.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(contenu partiel avec caractères spéciaux) : retourne une liste métier cohérente")
    @Test
    public void testFindByLibelleRapideContenuSpeciauxNominal() throws Exception {

        /* ARRANGE :
         * Crée un parent et une liste d'objets métier pour le test.
         */
        final String contenu = "prod/uit";

        /* Crée un parent. */
        final TypeProduitJPA parentJPA =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        /* Crée une liste d'objets métier enfant du parent. */
        final List<SousTypeProduitJPA> entities =
                new ArrayList<SousTypeProduitJPA>();
        entities.add(fabriquerSousTypeProduitJPA(contenu, ID_1, parentJPA));

        /*
         * Configuration du Mock :
         * configure ici le comportement du DAO objet métier 
         * mocké avec Mockito lors d'un appel 
         * DAO.findBySousTypeProduitContainingIgnoreCase(
         * contenu avec caractères spéciaux) 
         * qui retourne une liste d'objets métier.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito via
         * findBySousTypeProduitContainingIgnoreCase(prod/uit),
         * alors le DAO objet métier mocké avec Mockito retournera
         * une collection persistante contenant un objet métier".
         *
         * On simule donc volontairement un stockage
         * contenant un objet métier dont le libellé "prod/uit"
         * contient un caractère spécial.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de vérifier que le service transmet correctement le contenu
         * et convertit proprement le résultat retourné.
         */
        when(this.sousTypeProduitDaoJPA
                .findBySousTypeProduitContainingIgnoreCase(contenu))
            .thenReturn(entities);

        /* ACT */
        /* Sollicite service.findByLibelleRapide(...)
         * dans les conditions voulues par le Mock (condition when).
         */
        final List<SousTypeProduit> resultat =
                this.service.findByLibelleRapide(contenu);

        /* ASSERT */
        /* Garantit que this.service.findByLibelleRapide(contenu)
         * retourne une liste métier exploitable,
         * contenant l'objet métier issu du stockage.
         */
        assertThat(resultat).isNotNull().hasSize(1);
        assertThat(resultat.get(0).getSousTypeProduit()).isEqualTo(contenu);

        /* Garantit que l'objet métier retourné
         * conserve un parent non null et cohérent.
         */
        assertThat(resultat.get(0).getTypeProduit()).isNotNull();
        assertThat(resultat.get(0).getTypeProduit().getTypeProduit())
            .isEqualTo(LIBELLE_PARENT_1);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le contenu partiel attendu.
         */
        verify(this.sousTypeProduitDaoJPA, times(1))
            .findBySousTypeProduitContainingIgnoreCase(contenu);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.sousTypeProduitDaoJPA, never()).findAll();

        /* Garantit que ce scénario ne sollicite
         * ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(non trouvé) :</p>
     * <ul>
     * <li>délègue au DAO objet métier la recherche rapide via
     * {@code findBySousTypeProduitContainingIgnoreCase(...)} ;</li>
     * <li>retourne une {@link List} non null ;</li>
     * <li>retourne une liste vide ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>ne sollicite ni le DAO parent, ni l'EntityManager.</li>
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
         * mocké avec Mockito avec le contenu CONTENU_PARTIEL via
         * findBySousTypeProduitContainingIgnoreCase(...),
         * alors le DAO objet métier mocké avec Mockito devra répondre
         * une liste vide".
         *
         * On simule donc volontairement un stockage
         * qui ne trouve aucun enregistrement correspondant.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réponse
         * afin de prouver comment le service réagit
         * au cas contractuel "non trouvé".
         */
        when(this.sousTypeProduitDaoJPA
                .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL))
            .thenReturn(new ArrayList<SousTypeProduitJPA>());

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        final List<SousTypeProduit> resultat =
                this.service.findByLibelleRapide(CONTENU_PARTIEL);

        /* ASSERT */
        /* Garantit que this.service.findByLibelleRapide(CONTENU_PARTIEL)
         * - retourne une liste non null
         * - retourne une liste vide
         *   lorsque le stockage ne trouve aucun résultat.
         */
        assertThat(resultat).isNotNull().isEmpty();

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon contenu de recherche rapide.
         */
        verify(this.sousTypeProduitDaoJPA, times(1))
            .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.sousTypeProduitDaoJPA, never()).findAll();

        /* Garantit que ce scénario ne sollicite
         * ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelleRapide(nominal) :</p>
     * <ul>
     * <li>délègue au DAO objet métier la recherche rapide via
     * {@code findBySousTypeProduitContainingIgnoreCase(...)} ;</li>
     * <li>retourne une {@link List} non null ;</li>
     * <li>filtre les éléments null issus du stockage ;</li>
     * <li>dédoublonne les résultats au sens métier
     * sur le couple parent / objet métier ;</li>
     * <li>retourne les objets métier triés par parent
     * puis par libellé objet métier ;</li>
     * <li>conserve le parent des objets métier retournés ;</li>
     * <li>n'appelle jamais {@code findAll()} ;</li>
     * <li>ne sollicite ni le DAO parent, ni l'EntityManager.</li>
     * </ul>
     * <p>Ce test prouve directement au niveau de la méthode publique
     * le filtrage des null,
     * le dédoublonnage métier sur le couple parent / objet métier
     * et le tri final par parent puis par libellé objet métier.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(nominal) : filtre les null, trie et dédoublonne par couple parent / objet métier")
    @Test
    public void testFindByLibelleRapideNominal() throws Exception {

        /* ARRANGE :
         * Crée deux parent et une liste d'objets métier pour le test.
         */
    	/* Crée des parents. */
        final TypeProduitJPA parentJPA1 =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA2 =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_2, ID_2);

        /* Crée des ojets métier et d'une liste d'objets métier. */
        final String libelleEnfantDoublon =
                LIBELLE_ENFANT_2.toUpperCase(Locale.ROOT);

        final List<SousTypeProduitJPA> entities =
                new ArrayList<SousTypeProduitJPA>();
        entities.add(fabriquerSousTypeProduitJPA(
                LIBELLE_ENFANT_1, ID_1, parentJPA2));
        entities.add(null);
        entities.add(fabriquerSousTypeProduitJPA(
                LIBELLE_ENFANT_2, ID_2, parentJPA1));
        entities.add(fabriquerSousTypeProduitJPA(
                libelleEnfantDoublon, ID_3, parentJPA1));
        entities.add(fabriquerSousTypeProduitJPA(
                LIBELLE_ENFANT_1, ID_1, parentJPA1));

        /*
         * Configuration du Mock :
         * configure ici le comportement du DAO objet métier mocké 
         * avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO objet métier
         * mocké avec Mockito via
         * findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL),
         * alors le DAO objet métier mocké avec Mockito devra répondre
         * une collection persistante non triée,
         * contenant un null
         * et un doublon d'objets métier 
         * (même couple [parent / libellé objet métier])".
         *
         * On simule donc volontairement un stockage
         * qui retourne :
         * - un élément null ;
         * - deux objets métier fonctionnellement identiques
         *   ne différant que par la casse du libellé objet métier ;
         * - deux parents distincts ;
         * - plusieurs valeurs non triées.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réponse
         * afin de prouver comment le service réagit
         * au cas contractuel "scénario nominal complet".
         */
        when(this.sousTypeProduitDaoJPA
                .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL))
            .thenReturn(entities);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock.
         */
        final List<SousTypeProduit> resultat =
                this.service.findByLibelleRapide(CONTENU_PARTIEL);

        /* ASSERT */
        /* Garantit que this.service.findByLibelleRapide(CONTENU_PARTIEL)
         * - retourne une liste non null ;
         * - ne contient plus aucun élément null ;
         * - dédoublonne les résultats au sens métier
         *   sur le couple parent / objet métier ;
         * - restitue le résultat final trié
         *   par parent puis par libellé objet métier.
         *
         * L'extraction normalisée en trim + lowerCase(Locale.ROOT)
         * est volontaire :
         * elle permet de prouver directement le dédoublonnage métier
         * sans sur-contraindre artificiellement
         * la casse conservée par l'implémentation.
         */
        assertThat(resultat).isNotNull().doesNotContainNull().hasSize(3);

        assertThat(resultat)
            .allSatisfy(objetMetier ->
                assertThat(objetMetier.getTypeProduit()).isNotNull());

        assertThat(resultat)
            .extracting(objetMetier ->
                objetMetier.getTypeProduit().getTypeProduit())
            .containsExactly(
                LIBELLE_PARENT_1,
                LIBELLE_PARENT_1,
                LIBELLE_PARENT_2);

        assertThat(resultat)
            .extracting(objetMetier -> objetMetier.getSousTypeProduit()
                .trim()
                .toLowerCase(Locale.ROOT))
            .containsExactly(
                LIBELLE_ENFANT_1.toLowerCase(Locale.ROOT),
                LIBELLE_ENFANT_2.toLowerCase(Locale.ROOT),
                LIBELLE_ENFANT_1.toLowerCase(Locale.ROOT));

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon contenu de recherche rapide.
         */
        verify(this.sousTypeProduitDaoJPA, times(1))
            .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL);

        /* Garantit que le DAO objet métier mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.sousTypeProduitDaoJPA, never()).findAll();

        /* Garantit que ce scénario ne sollicite
         * ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    // ========================= findAllByParent ==========================
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParentNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL} ;</li>
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
         * le traitement avant tout appel au DAO parent,
         * au DAO objet métier ou à l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK} ;</li>
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
        final TypeProduit parent = fabriquerTypeProduit(BLANK, ID_1);

        /* ACT - ASSERT :
         * vérifie que l'appel this.service.findAllByParent(parent)
         * avec un parent ayant un libellé blank
         * jette une ExceptionAppliLibelleBlank
         * avec le message MSG_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK);

        /* Vérifie qu'aucun accès au stockage n'a été tenté.
         * Le contrôle applicatif sur le libellé blank doit interrompre
         * le traitement avant tout appel au DAO parent,
         * au DAO objet métier ou à l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(parent ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT}
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
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, null);

        /* ACT - ASSERT :
         * vérifie que l'appel this.service.findAllByParent(parent)
         * avec un parent dont l'identifiant est null
         * jette une ExceptionTechniqueGatewayNonPersistent
         * avec le message MSG_FINDALLBYPARENT_PREFIX_PARENT_NON_PERSISTENT
         * complété par le libellé du parent.
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(construireMessageNonPersistent(
                    MSG_FINDALLBYPARENT_PREFIX_PARENT_NON_PERSISTENT,
                    LIBELLE_PARENT_1));

        /* Vérifie qu'aucun accès au stockage n'a été tenté.
         * Le contrôle de persistance du parent doit interrompre
         * le traitement avant tout appel au DAO parent,
         * au DAO objet métier ou à l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(parent absent du stockage) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>n'appelle pas le DAO objet métier ;</li>
     * <li>ne sollicite pas l'EntityManager.</li>
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
        final TypeProduit parent =
                fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);

        /* Condition du Mock :
         * L'appel typeProduitDaoJPA.findById(ID_1)
         * sur le DAO parent mocké retourne Optional.empty().
         */
        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.empty());

        /* ACT - ASSERT :
         * vérifie que l'appel this.service.findAllByParent(parent)
         * avec un parent absent du stockage
         * jette une ExceptionTechniqueGatewayNonPersistent
         * avec le message MSG_FINDALLBYPARENT_PREFIX_PARENT_NON_PERSISTENT
         * complété par le libellé du parent.
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(construireMessageNonPersistent(
                    MSG_FINDALLBYPARENT_PREFIX_PARENT_NON_PERSISTENT,
                    LIBELLE_PARENT_1));

        /* Garantit que le DAO parent mocké a bien été appelé une fois
         * avec le bon identifiant parent.
         */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);

        /* Garantit que le DAO objet métier n'a jamais été appelé.
         */
        verify(this.sousTypeProduitDaoJPA, never())
            .findAllByTypeProduit(any(TypeProduitJPA.class));

        /* Garantit que ce scénario ne sollicite pas l'EntityManager.
         */
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(KO DAO parent message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine du DAO parent ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>n'appelle pas le DAO objet métier ;</li>
     * <li>ne sollicite pas l'EntityManager.</li>
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
        final TypeProduit parent =
                fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);

        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        when(this.typeProduitDaoJPA.findById(ID_1))
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
        assertThat(throwable).hasMessageContaining(MSG_BOOM);
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit que le DAO parent mocké a bien été appelé une fois
         * avec le bon identifiant parent.
         */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);

        /* Garantit que le DAO objet métier n'a jamais été appelé.
         */
        verify(this.sousTypeProduitDaoJPA, never())
            .findAllByTypeProduit(any(TypeProduitJPA.class));

        /* Garantit que ce scénario ne sollicite pas l'EntityManager.
         */
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(KO DAO parent message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>n'appelle pas le DAO objet métier ;</li>
     * <li>ne sollicite pas l'EntityManager.</li>
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
        final TypeProduit parent =
                fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);

        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.typeProduitDaoJPA.findById(ID_1))
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
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);

        /* Garantit que le DAO objet métier n'a jamais été appelé.
         */
        verify(this.sousTypeProduitDaoJPA, never())
            .findAllByTypeProduit(any(TypeProduitJPA.class));

        /* Garantit que ce scénario ne sollicite pas l'EntityManager.
         */
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(DAO objet métier retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé ;</li>
     * <li>ne sollicite pas l'EntityManager.</li>
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
        final TypeProduit parent =
                fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(parentJPA));

        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA))
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
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le parent JPA trouvé.
         */
        verify(this.sousTypeProduitDaoJPA, times(1))
            .findAllByTypeProduit(parentJPA);

        /* Garantit que ce scénario ne sollicite pas l'EntityManager.
         */
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(KO DAO objet métier message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine du DAO objet métier ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé ;</li>
     * <li>ne sollicite pas l'EntityManager.</li>
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
        final TypeProduit parent =
                fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(parentJPA));

        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA))
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
        assertThat(throwable).hasMessageContaining(MSG_BOOM);
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit que le DAO parent mocké a bien été appelé une fois.
         */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le parent JPA trouvé.
         */
        verify(this.sousTypeProduitDaoJPA, times(1))
            .findAllByTypeProduit(parentJPA);

        /* Garantit que ce scénario ne sollicite pas l'EntityManager.
         */
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(KO DAO objet métier message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé ;</li>
     * <li>ne sollicite pas l'EntityManager.</li>
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
        final TypeProduit parent =
                fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(parentJPA));

        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA))
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
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le parent JPA trouvé.
         */
        verify(this.sousTypeProduitDaoJPA, times(1))
            .findAllByTypeProduit(parentJPA);

        /* Garantit que ce scénario ne sollicite pas l'EntityManager.
         */
        verifyNoInteractions(this.entityManager);

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
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé ;</li>
     * <li>ne sollicite pas l'EntityManager.</li>
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
        final TypeProduit parent =
                fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(parentJPA));

        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA))
            .thenReturn(new ArrayList<SousTypeProduitJPA>());

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock.
         */
        final List<SousTypeProduit> resultat =
                this.service.findAllByParent(parent);

        /* ASSERT */
        /* Garantit que this.service.findAllByParent(parent)
         * retourne une liste non null et vide
         * lorsque le DAO objet métier ne trouve aucun résultat.
         */
        assertThat(resultat).isNotNull().isEmpty();

        /* Garantit que le DAO parent mocké a bien été appelé une fois.
         */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le parent JPA trouvé.
         */
        verify(this.sousTypeProduitDaoJPA, times(1))
            .findAllByTypeProduit(parentJPA);

        /* Garantit que ce scénario ne sollicite pas l'EntityManager.
         */
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
	 * <div>
	 * <p>Test didactique non contractuel.</p>
	 * <p>garantit que findAllByParent(avec doublons fonctionnels) :</p>
	 * <ul>
	 * <li>retourne une liste non null ;</li>
	 * <li>retourne une liste dédoublonnée ;</li>
	 * <li>retourne les libellés enfants attendus ;</li>
	 * <li>appelle la méthode typeProduitDaoJPA.findById(...)
	 * du DAO parent mocké avec Mockito ;</li>
	 * <li>appelle la méthode sousTypeProduitDaoJPA.findAllByTypeProduit(...)
	 * du DAO enfant mocké avec Mockito.</li>
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
	     * et une liste renvoyée par le DAO enfant
	     * contenant deux sous-types portant le même libellé,
	     * afin de vérifier le dédoublonnage fonctionnel.
	     */
	    final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
	    final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
	
	    final List<SousTypeProduitJPA> entities = new ArrayList<SousTypeProduitJPA>();
	    entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_1, parentJPA));
	    entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA));
	    entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_2, ID_3, parentJPA));
	
	    /* Condition du Mock typeProduitDaoJPA :
	     * L'appel typeProduitDaoJPA.findById(ID_1)
	     * sur le DAO mocké retourne Optional.of(parentJPA).
	     */
	    when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));
	
	    /* Condition du Mock sousTypeProduitDaoJPA :
	     * L'appel sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)
	     * sur le DAO mocké retourne la liste entities.
	     */
	    when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenReturn(entities);
	
	    /* ACT :
	     * appelle this.service.findAllByParent(parent)
	     * dans le scénario où le DAO retourne
	     * des doublons fonctionnels.
	     */
	    final List<SousTypeProduit> retour = this.service.findAllByParent(parent);
	
	    /* ASSERT :
	     * vérifie que la méthode retourne bien
	     * une liste métier cohérente et dédoublonnée.
	     */
	    assertThat(retour).isNotNull().hasSize(2);
	    assertThat(retour)
	        .extracting(SousTypeProduit::getSousTypeProduit)
	        .containsExactly(LIBELLE_ENFANT_1, LIBELLE_ENFANT_2);
	
	    /* Vérifie ensuite les interactions réelles
	     * avec les dépendances mockées.
	     * Assure que :
	     * - typeProduitDaoJPA.findById(ID_1) a été appelée une fois.
	     * - sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA) a été appelée une fois.
	     */
	    verify(this.typeProduitDaoJPA).findById(ID_1);
	    verify(this.sousTypeProduitDaoJPA).findAllByTypeProduit(parentJPA);
	
	} // __________________________________________________________________



	/**
     * <div>
     * <p>garantit que findAllByParent(OK) :</p>
     * <ul>
     * <li>délègue au DAO objet métier la recherche par parent via
     * {@code findAllByTypeProduit(...)} ;</li>
     * <li>retourne une {@link List} non null ;</li>
     * <li>filtre les éléments null issus du stockage ;</li>
     * <li>dédoublonne les résultats au sens métier
     * sur le couple objet métier / parent ;</li>
     * <li>retourne les objets métier triés par libellé objet métier
     * pour le parent demandé ;</li>
     * <li>conserve le parent des objets métier retournés ;</li>
     * <li>appelle le DAO parent une fois avec le bon identifiant parent ;</li>
     * <li>appelle le DAO objet métier une fois avec le parent JPA trouvé ;</li>
     * <li>ne sollicite pas l'EntityManager.</li>
     * </ul>
     * <p>Ce test prouve directement au niveau de la méthode publique
     * le filtrage des null,
     * le dédoublonnage métier sur le couple objet métier / parent
     * et le tri final des objets métier rattachés au parent demandé.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(OK) : filtre les null, trie et dédoublonne par couple métier")
    @Test
    public void testFindAllByParentNominal() throws Exception {

        /* ARRANGE :
         * configure ici le comportement des DAO mockés avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service vérifie d'abord
         * l'existence du parent via typeProduitDaoJPA.findById(ID_1),
         * alors le DAO parent mocké avec Mockito devra répondre
         * Optional.of(parentJPA)".
         *
         * Puis :
         * "si le service appelle ensuite le DAO objet métier
         * mocké avec Mockito via findAllByTypeProduit(parentJPA),
         * alors le DAO objet métier mocké avec Mockito devra répondre
         * une collection persistante non triée,
         * contenant un null
         * et un doublon métier sur le couple objet métier / parent".
         *
         * Le but n'est pas de tester les DAO réels,
         * mais de maîtriser leurs réponses
         * afin de prouver comment le service réagit
         * au cas contractuel "scénario nominal complet".
         */
        final TypeProduit parent =
                fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(parentJPA));

        final String libelleDoublon =
                LIBELLE_ENFANT_1.toUpperCase(Locale.ROOT);

        final List<SousTypeProduitJPA> entities =
                new ArrayList<SousTypeProduitJPA>();
        entities.add(fabriquerSousTypeProduitJPA(
                LIBELLE_ENFANT_2, ID_2, parentJPA));
        entities.add(null);
        entities.add(fabriquerSousTypeProduitJPA(
                libelleDoublon, ID_3, parentJPA));
        entities.add(fabriquerSousTypeProduitJPA(
                LIBELLE_ENFANT_1, ID_1, parentJPA));

        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA))
            .thenReturn(entities);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock.
         */
        final List<SousTypeProduit> resultat =
                this.service.findAllByParent(parent);

        /* ASSERT */
        /* Garantit que this.service.findAllByParent(parent)
         * - retourne une liste non null
         * - ne contient plus aucun élément null
         * - dédoublonne les résultats au sens métier
         *   sur le couple objet métier / parent
         * - restitue le résultat final trié par libellé objet métier
         *   pour le parent demandé.
         */
        assertThat(resultat).isNotNull().doesNotContainNull().hasSize(2);

        assertThat(resultat)
            .allSatisfy(objetMetier ->
                assertThat(objetMetier.getTypeProduit()).isNotNull());

        assertThat(resultat)
            .extracting(objetMetier ->
                objetMetier.getTypeProduit().getTypeProduit())
            .containsExactly(
                LIBELLE_PARENT_1,
                LIBELLE_PARENT_1);

        /* L'extraction normalisée en trim + lowerCase(Locale.ROOT)
         * est volontaire :
         * elle permet de prouver directement le dédoublonnage métier
         * sans sur-contraindre artificiellement
         * la casse conservée par l'implémentation.
         */
        assertThat(resultat)
            .extracting(objetMetier -> objetMetier.getSousTypeProduit()
                .trim()
                .toLowerCase(Locale.ROOT))
            .containsExactly(
                LIBELLE_ENFANT_1.toLowerCase(Locale.ROOT),
                LIBELLE_ENFANT_2.toLowerCase(Locale.ROOT));

        /* Garantit que le DAO parent mocké a bien été appelé une fois
         * avec le bon identifiant parent.
         */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le parent JPA trouvé.
         */
        verify(this.sousTypeProduitDaoJPA, times(1))
            .findAllByTypeProduit(parentJPA);

        /* Garantit que ce scénario ne sollicite pas l'EntityManager.
         */
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    

    
    // ============================ findById ==============================
    
    
    
    /**
     * <div>
     * <p>garantit que findById(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYID_KO_PARAM_NULL} ;</li>
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
         * au DAO objet métier, au DAO parent ou à l'EntityManager.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findById(DAO retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon identifiant ;</li>
     * <li>ne sollicite ni le DAO parent, ni l'EntityManager.</li>
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
         * mocké avec Mockito avec l'identifiant ID_2 via findById(...),
         * alors le DAO objet métier mocké avec Mockito devra répondre null".
         *
         * On simule donc volontairement un stockage
         * qui retourne null au lieu d'un Optional<SousTypeProduitJPA>.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que le retour null du DAO objet métier
         * n'est pas interprété comme une absence de résultat,
         * mais comme une anomalie technique de stockage.
         */
        assertThatThrownBy(() -> this.service.findById(ID_2))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon identifiant.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);

        /* Garantit que ce scénario ne sollicite
         * ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findById(KO DAO message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon identifiant ;</li>
     * <li>ne sollicite ni le DAO parent, ni l'EntityManager.</li>
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
         * mocké avec Mockito avec l'identifiant ID_2 via findById(...),
         * alors le DAO objet métier mocké avec Mockito devra lancer
         * une RuntimeException avec message non null".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant la recherche par identifiant.
         */
        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois this.service.findById(ID_2)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.findById(ID_2));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel,
         * le message technique d'origine
         * et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable).hasMessageContaining(MSG_BOOM);
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon identifiant.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);

        /* Garantit que ce scénario ne sollicite
         * ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findById(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non null dérivé de l'exception technique ;</li>
     * <li>propage l'exception technique cause ;</li>
     * <li>appelle le DAO objet métier une fois
     * avec le bon identifiant ;</li>
     * <li>ne sollicite ni le DAO parent, ni l'EntityManager.</li>
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
         * mocké avec Mockito avec l'identifiant ID_2 via findById(...),
         * alors le DAO objet métier mocké avec Mockito devra lancer
         * une RuntimeException sans message".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant la recherche par identifiant,
         * avec un message technique d'origine null.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois this.service.findById(ID_2)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.findById(ID_2));

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
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);

        /* Garantit que ce scénario ne sollicite
         * ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

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
     * <li>ne sollicite ni le DAO parent, ni l'EntityManager.</li>
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
         * mocké avec Mockito avec l'identifiant ID_3 via findById(...),
         * alors le DAO objet métier mocké avec Mockito devra répondre
         * Optional.empty()".
         *
         * On simule donc volontairement un stockage
         * qui ne trouve aucun objet métier persistant
         * pour cet identifiant.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_3))
            .thenReturn(Optional.empty());

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock.
         */
        final SousTypeProduit resultat = this.service.findById(ID_3);

        /* ASSERT */
        /* Garantit que this.service.findById(ID_3)
         * retourne null
         * lorsque le stockage ne trouve aucun objet persistant
         * pour l'identifiant demandé.
         */
        assertThat(resultat).isNull();

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon identifiant.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_3);

        /* Garantit que ce scénario ne sollicite
         * ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

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
     * <li>appelle le DAO objet métier une fois
     * avec le bon identifiant ;</li>
     * <li>ne sollicite ni le DAO parent, ni l'EntityManager.</li>
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
         * mocké avec Mockito avec l'identifiant ID_2 via findById(...),
         * alors le DAO objet métier mocké avec Mockito devra répondre
         * Optional.of(...) contenant un SousTypeProduitJPA persistant
         * portant l'identifiant ID_2, le libellé LIBELLE_ENFANT_1
         * et un parent TypeProduitJPA persistant".
         *
         * On simule donc volontairement un stockage
         * qui trouve un objet métier persistant pour cet identifiant.
         */
        final TypeProduitJPA parentJPA =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduitJPA entityJPA =
                fabriquerSousTypeProduitJPA(
                        LIBELLE_ENFANT_1, ID_2, parentJPA);

        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(entityJPA));

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock.
         */
        final SousTypeProduit resultat = this.service.findById(ID_2);

        /* ASSERT */
        /* Garantit que this.service.findById(ID_2)
         * - délègue la recherche au DAO objet métier avec l'identifiant ID_2 ;
         * - retourne un objet métier non null ;
         * - convertit correctement l'identifiant et le libellé
         *   de l'objet persistant trouvé ;
         * - convertit et conserve le parent persistant.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdSousTypeProduit()).isEqualTo(ID_2);
        assertThat(resultat.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_1);
        assertThat(resultat.getTypeProduit()).isNotNull();
        assertThat(resultat.getTypeProduit().getIdTypeProduit()).isEqualTo(ID_1);
        assertThat(resultat.getTypeProduit().getTypeProduit())
            .isEqualTo(LIBELLE_PARENT_1);

        /* Garantit que le DAO objet métier mocké a bien été appelé une fois
         * avec le bon identifiant.
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);

        /* Garantit que ce scénario ne sollicite
         * ni le DAO parent ni l'EntityManager.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    

    
    // ============================= update ===============================


    
	/**
     * <div>
     * <p>garantit que update(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull}</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_PARAM_NULL}</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant</li>
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
        verifyNoInteractions(this.typeProduitDaoJPA, this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(libellé null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank}</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_BLANK}</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(libellé null) : jette ExceptionAppliLibelleBlank et n'appelle pas les DAO")
    @Test
    public void testUpdateLibelleNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(null, ID_1, parent);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(stp)
         * - jette une ExceptionAppliLibelleBlank
         * - émet un message MSG_UPDATE_KO_LIBELLE_BLANK.
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_UPDATE_KO_LIBELLE_BLANK);

        /* Garantit que les DAO mockés n'ont pas été appelés. */
        verifyNoInteractions(this.typeProduitDaoJPA, this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank}</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_BLANK}</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(libellé blank) : jette ExceptionAppliLibelleBlank et n'appelle pas les DAO")
    @Test
    public void testUpdateLibelleBlank() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(BLANK, ID_1, parent);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(stp)
         * - jette une ExceptionAppliLibelleBlank
         * - émet un message MSG_UPDATE_KO_LIBELLE_BLANK.
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_UPDATE_KO_LIBELLE_BLANK);

        /* Garantit que les DAO mockés n'ont pas été appelés. */
        verifyNoInteractions(this.typeProduitDaoJPA, this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNonPersistent}</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_NON_PERSISTENT}
     * suivi du libellé de l'objet métier</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(ID null) : jette ExceptionAppliParamNonPersistent et n'appelle pas les DAO")
    @Test
    public void testUpdateIdNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(stp)
         * - jette une ExceptionAppliParamNonPersistent
         * - émet un message MSG_UPDATE_PREFIX_NON_PERSISTENT
         *   suivi du libellé enfant.
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(MSG_UPDATE_PREFIX_NON_PERSISTENT + safeMessage(LIBELLE_ENFANT_1));

        /* Garantit que les DAO mockés n'ont pas été appelés. */
        verifyNoInteractions(this.typeProduitDaoJPA, this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(parent null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParentNull}</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_PARENT_NULL}</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent null) : jette ExceptionAppliParentNull et n'appelle pas les DAO")
    @Test
    public void testUpdateParentNull() {

        /* ARRANGE */
        final SousTypeProduit stp
            = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_1, null);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(stp)
         * - jette une ExceptionAppliParentNull
         * - émet un message MSG_UPDATE_KO_PARENT_NULL.
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_UPDATE_KO_PARENT_NULL);

        /* Garantit que les DAO mockés n'ont pas été appelés. */
        verifyNoInteractions(this.typeProduitDaoJPA, this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank}</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK}</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent libellé blank) : jette ExceptionAppliLibelleBlank et n'appelle pas les DAO")
    @Test
    public void testUpdateParentLibelleBlank() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(BLANK, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_1, parent);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(stp)
         * - jette une ExceptionAppliLibelleBlank
         * - émet un message MSG_UPDATE_KO_LIBELLE_PARENT_BLANK.
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_UPDATE_KO_LIBELLE_PARENT_BLANK);

        /* Garantit que les DAO mockés n'ont pas été appelés. */
        verifyNoInteractions(this.typeProduitDaoJPA, this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(parent ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent}</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent ID null) : jette ExceptionTechniqueGatewayNonPersistent et n'appelle pas les DAO")
    @Test
    public void testUpdateParentIdNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, null);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_1, parent);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(stp)
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet un message MSG_UPDATE_PREFIX_PARENT_NON_PERSISTENT
         *   suivi du libellé parent.
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(construireMessageNonPersistent(
                    MSG_UPDATE_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1));

        /* Garantit que les DAO mockés n'ont pas été appelés. */
        verifyNoInteractions(this.typeProduitDaoJPA, this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(parent absent du stockage) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent}</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>n'appelle pas le DAO enfant</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent absent du stockage) : jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testUpdateParentAbsent() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        /* Configure le DAO parent pour simuler un parent absent du stockage. */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.empty());

        /* ACT - ASSERT */
        /* Garantit que this.service.update(stp)
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - émet un message MSG_UPDATE_PREFIX_PARENT_NON_PERSISTENT
         *   suivi du libellé parent.
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(construireMessageNonPersistent(
                    MSG_UPDATE_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1));

        /* Garantit que le DAO parent a bien été appelé une fois. */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);

        /* Garantit que l'absence du parent arrête le traitement
         * avant tout accès au DAO enfant.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(KO DAO parent message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>n'appelle pas le DAO enfant</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO parent message non null) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testUpdateParentDAOExceptionMessageNonNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenThrow(causeDao);

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.update(stp));

        /* ASSERT */
        /* Garantit que this.service.update(stp)
         * - jette une ExceptionTechniqueGateway
         * - conserve le message technique d'origine MSG_BOOM
         * - propage la cause DAO.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit que le DAO parent a bien été appelé une fois. */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);

        /* Garantit que l'échec technique du DAO parent
         * arrête le traitement avant tout accès au DAO enfant.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(KO DAO parent message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non null dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>n'appelle pas le DAO enfant</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO parent message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testUpdateParentDAOExceptionMessageNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenThrow(causeDao);

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.update(stp));

        /* ASSERT */
        /* Garantit que this.service.update(stp)
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
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);

        /* Garantit que l'échec technique du DAO parent
         * arrête le traitement avant tout accès au DAO enfant.
         */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(DAO.findById(...) enfant retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE}</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>appelle le DAO enfant une fois via {@code findById(...)}</li>
     * <li>ne déclenche aucune sauvegarde</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(DAO.findById(...) enfant retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testUpdateDAOFindByIdRetourneNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(parentJPA));

        /* Simule un stockage qui retourne null au lieu
         * d'un Optional<SousTypeProduitJPA>.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(null);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(stp)
         * - jette une ExceptionTechniqueGateway
         * - émet un message ERREUR_TECHNIQUE_KO_STOCKAGE.
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée. */
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(objet métier absent du stockage) :</p>
     * <ul>
     * <li>retourne {@code null}</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>appelle le DAO enfant une fois via {@code findById(...)}</li>
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
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(parentJPA));

        /* Simule un stockage qui ne trouve aucun objet métier
         * à modifier pour l'identifiant demandé.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.empty());

        /* ACT */
        final SousTypeProduit resultat = this.service.update(stp);

        /* ASSERT */
        /* Garantit que this.service.update(stp)
         * retourne null lorsque l'objet métier est absent du stockage.
         */
        assertThat(resultat).isNull();

        /* Garantit les appels DAO réellement attendus. */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée. */
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(KO DAO enfant findById message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>appelle le DAO enfant une fois via {@code findById(...)}</li>
     * <li>ne déclenche aucune sauvegarde</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO enfant findById message non null) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testUpdateDAOFindByIdExceptionMessageNonNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(parentJPA));
        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenThrow(causeDao);

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.update(stp));

        /* ASSERT */
        /* Garantit que this.service.update(stp)
         * - jette une ExceptionTechniqueGateway
         * - conserve le message technique d'origine MSG_BOOM
         * - propage la cause DAO.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée. */
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(KO DAO enfant findById message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non null dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>appelle le DAO enfant une fois via {@code findById(...)}</li>
     * <li>ne déclenche aucune sauvegarde</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO enfant findById message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testUpdateDAOFindByIdExceptionMessageNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(parentJPA));
        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenThrow(causeDao);

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.update(stp));

        /* ASSERT */
        /* Garantit que this.service.update(stp)
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
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée. */
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(DAO.save(...) retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE}</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>appelle le DAO enfant une fois via {@code findById(...)}</li>
     * <li>déclenche une tentative de sauvegarde via {@code save(...)}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(DAO.save(...) retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testUpdateDAOSaveRetourneNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_2, ID_2, parent);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduitJPA persiste
            = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(parentJPA));
        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(persiste));
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class)))
            .thenReturn(null);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(stp)
         * - jette une ExceptionTechniqueGateway
         * - émet un message ERREUR_TECHNIQUE_KO_STOCKAGE
         *   lorsque le stockage retourne null sur save(...).
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);
        verify(this.sousTypeProduitDaoJPA, times(1)).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(KO DAO sur save message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>appelle le DAO enfant une fois via {@code findById(...)}</li>
     * <li>déclenche une tentative de sauvegarde via {@code save(...)}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO sur save message non null) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testUpdateDAOSaveExceptionMessageNonNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_2, ID_2, parent);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduitJPA persiste
            = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(parentJPA));
        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(persiste));
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class)))
            .thenThrow(causeDao);

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.update(stp));

        /* ASSERT */
        /* Garantit que this.service.update(stp)
         * - jette une ExceptionTechniqueGateway
         * - conserve le message technique d'origine MSG_BOOM
         * - propage la cause DAO.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);
        verify(this.sousTypeProduitDaoJPA, times(1)).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(KO DAO sur save message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non null dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>appelle le DAO enfant une fois via {@code findById(...)}</li>
     * <li>déclenche une tentative de sauvegarde via {@code save(...)}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO sur save message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testUpdateDAOSaveExceptionMessageNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_2, ID_2, parent);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduitJPA persiste
            = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(parentJPA));
        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(persiste));
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class)))
            .thenThrow(causeDao);

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.update(stp));

        /* ASSERT */
        /* Garantit que this.service.update(stp)
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
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);
        verify(this.sousTypeProduitDaoJPA, times(1)).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(sans modification) :</p>
     * <ul>
     * <li>ne lève aucune exception</li>
     * <li>retourne l'objet persistant inchangé</li>
     * <li>appelle le DAO parent une fois via {@code findById(...)}</li>
     * <li>appelle le DAO enfant une fois via {@code findById(...)}</li>
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
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduitJPA persiste
            = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(parentJPA));
        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(persiste));

        /* ACT */
        final SousTypeProduit resultat = this.service.update(stp);

        /* ASSERT */
        /* Garantit que this.service.update(stp)
         * retourne l'objet persistant inchangé.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdSousTypeProduit()).isEqualTo(ID_2);
        assertThat(resultat.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_1);
        assertThat(resultat.getTypeProduit()).isNotNull();
        assertThat(resultat.getTypeProduit().getIdTypeProduit()).isEqualTo(ID_1);
        assertThat(resultat.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_1);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée. */
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(parent avec différence de casse seulement) :</p>
     * <ul>
     * <li>retourne un objet métier non null</li>
     * <li>retourne le libellé enfant attendu</li>
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
        final TypeProduit parent
            = fabriquerTypeProduit(
                    LIBELLE_PARENT_1.toUpperCase(Locale.ROOT), ID_1);
        final SousTypeProduit stp
            = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final TypeProduitJPA parentJPA
            = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduitJPA persiste
            = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(parentJPA));
        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(persiste));

        /* ACT */
        final SousTypeProduit resultat = this.service.update(stp);

        /* ASSERT */
        /* Garantit que la différence de casse du libellé parent demandé
         * ne déclenche pas de sauvegarde lorsque l'identifiant parent
         * reste le même.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_1);
        assertThat(resultat.getTypeProduit()).isNotNull();
        assertThat(resultat.getTypeProduit().getIdTypeProduit()).isEqualTo(ID_1);
        assertThat(resultat.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_1);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée. */
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(modification de casse) :</p>
     * <ul>
     * <li>effectue une modification dans le stockage</li>
     * <li>retourne l'objet persistant modifié</li>
     * <li>préserve exactement la casse du nouveau libellé enfant</li>
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
        final String nouveauLibelle = LIBELLE_ENFANT_1.toUpperCase(Locale.ROOT);
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(nouveauLibelle, ID_2, parent);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduitJPA persiste
            = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        final SousTypeProduitJPA sauvegarde
            = fabriquerSousTypeProduitJPA(nouveauLibelle, ID_2, parentJPA);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(parentJPA));
        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(persiste));
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class)))
            .thenReturn(sauvegarde);

        /* ACT */
        final SousTypeProduit resultat = this.service.update(stp);

        /* ASSERT */
        /* Garantit que this.service.update(stp)
         * retourne l'objet persistant modifié
         * en préservant exactement la casse demandée.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdSousTypeProduit()).isEqualTo(ID_2);
        assertThat(resultat.getSousTypeProduit()).isEqualTo(nouveauLibelle);
        assertThat(resultat.getTypeProduit()).isNotNull();
        assertThat(resultat.getTypeProduit().getIdTypeProduit()).isEqualTo(ID_1);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);

        /* Garantit ce qui est envoyé au stockage. */
        final ArgumentCaptor<SousTypeProduitJPA> captor
            = ArgumentCaptor.forClass(SousTypeProduitJPA.class);
        verify(this.sousTypeProduitDaoJPA, times(1)).save(captor.capture());
        assertThat(captor.getValue().getIdSousTypeProduit()).isEqualTo(ID_2);
        assertThat(captor.getValue().getSousTypeProduit()).isEqualTo(nouveauLibelle);
        assertThat(captor.getValue().getTypeProduit()).isNotNull();
        assertThat(captor.getValue().getTypeProduit().getIdTypeProduit()).isEqualTo(ID_1);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(parent modifié) :</p>
     * <ul>
     * <li>effectue une modification dans le stockage</li>
     * <li>retourne l'objet persistant modifié</li>
     * <li>retourne le nouveau parent attendu</li>
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
        final TypeProduit parentNouveau = fabriquerTypeProduit(LIBELLE_PARENT_2, ID_2);
        final SousTypeProduit stp
            = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_3, parentNouveau);

        final TypeProduitJPA parentJPA1 = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA2 = fabriquerTypeProduitJPA(LIBELLE_PARENT_2, ID_2);
        final SousTypeProduitJPA persiste
            = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_3, parentJPA1);
        final SousTypeProduitJPA sauvegarde
            = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_3, parentJPA2);

        when(this.typeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(parentJPA2));
        when(this.sousTypeProduitDaoJPA.findById(ID_3))
            .thenReturn(Optional.of(persiste));
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class)))
            .thenReturn(sauvegarde);

        /* ACT */
        final SousTypeProduit resultat = this.service.update(stp);

        /* ASSERT */
        /* Garantit que this.service.update(stp)
         * retourne l'objet persistant modifié
         * avec le nouveau parent demandé.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdSousTypeProduit()).isEqualTo(ID_3);
        assertThat(resultat.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_1);
        assertThat(resultat.getTypeProduit()).isNotNull();
        assertThat(resultat.getTypeProduit().getIdTypeProduit()).isEqualTo(ID_2);
        assertThat(resultat.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_2);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_2);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_3);

        /* Garantit ce qui est envoyé au stockage. */
        final ArgumentCaptor<SousTypeProduitJPA> captor
            = ArgumentCaptor.forClass(SousTypeProduitJPA.class);
        verify(this.sousTypeProduitDaoJPA, times(1)).save(captor.capture());
        assertThat(captor.getValue().getIdSousTypeProduit()).isEqualTo(ID_3);
        assertThat(captor.getValue().getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_1);
        assertThat(captor.getValue().getTypeProduit()).isNotNull();
        assertThat(captor.getValue().getTypeProduit().getIdTypeProduit()).isEqualTo(ID_2);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que update(OK) :</p>
     * <ul>
     * <li>charge le parent persistant via le DAO parent</li>
     * <li>charge l'objet persistant courant via le DAO enfant</li>
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
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_2, ID_2, parent);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduitJPA persiste
            = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        final SousTypeProduitJPA sauvegarde
            = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_2, ID_2, parentJPA);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(parentJPA));
        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(persiste));
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class)))
            .thenReturn(sauvegarde);

        /* ACT */
        final SousTypeProduit resultat = this.service.update(stp);

        /* ASSERT */
        /* Garantit que this.service.update(stp)
         * retourne l'objet persistant modifié
         * avec le nouveau libellé demandé.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdSousTypeProduit()).isEqualTo(ID_2);
        assertThat(resultat.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_2);
        assertThat(resultat.getTypeProduit()).isNotNull();
        assertThat(resultat.getTypeProduit().getIdTypeProduit()).isEqualTo(ID_1);
        assertThat(resultat.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_1);

        /* Garantit les appels DAO réellement attendus. */
        verify(this.typeProduitDaoJPA, times(1)).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);

        /* Garantit ce qui est envoyé au stockage. */
        final ArgumentCaptor<SousTypeProduitJPA> captor
            = ArgumentCaptor.forClass(SousTypeProduitJPA.class);
        verify(this.sousTypeProduitDaoJPA, times(1)).save(captor.capture());
        assertThat(captor.getValue().getIdSousTypeProduit()).isEqualTo(ID_2);
        assertThat(captor.getValue().getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_2);
        assertThat(captor.getValue().getTypeProduit()).isNotNull();
        assertThat(captor.getValue().getTypeProduit().getIdTypeProduit()).isEqualTo(ID_1);

    } // __________________________________________________________________
    

    
    // ============================= delete ===============================
    
    
    
    /**
     * <div>
     * <p>garantit que delete(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull}</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_DELETE_KO_PARAM_NULL}</li>
     * <li>n'appelle ni le DAO objet métier ni l'EntityManager</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_DELETE)
    @DisplayName("delete(null) : jette ExceptionAppliParamNull et n'appelle pas le DAO ni l'EntityManager")
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

        /* Garantit qu'aucun accès au stockage n'a été tenté. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNonPersistent}</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_DELETE_KO_ID_NULL}</li>
     * <li>n'appelle ni le DAO objet métier ni l'EntityManager</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_DELETE)
    @DisplayName("delete(ID null) : jette ExceptionAppliParamNonPersistent et n'appelle pas le DAO ni l'EntityManager")
    @Test
    public void testDeleteIdNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp
            = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        /* ACT - ASSERT */
        /* Garantit que this.service.delete(stp)
         * - jette une ExceptionAppliParamNonPersistent
         * - émet un message MSG_DELETE_KO_ID_NULL.
         */
        assertThatThrownBy(() -> this.service.delete(stp))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(MSG_DELETE_KO_ID_NULL);

        /* Garantit qu'aucun accès au stockage n'a été tenté. */
        verifyNoInteractions(this.sousTypeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(DAO.findById(...) retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE}</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>ne déclenche ni {@code remove(...)} ni {@code flush()}</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_DELETE)
    @DisplayName("delete(DAO.findById(...) retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testDeleteDAOFindByIdRetourneNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp
            = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_2 via findById(...),
         * alors le DAO mocké avec Mockito devra répondre null".
         *
         * On simule donc volontairement un stockage
         * qui retourne null au lieu d'un Optional<SousTypeProduitJPA>,
         * ce qui doit être interprété comme une anomalie technique
         * de stockage.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(null);

        /* ACT - ASSERT */
        /* Garantit que this.service.delete(stp)
         * - jette une ExceptionTechniqueGateway
         * - émet un message ERREUR_TECHNIQUE_KO_STOCKAGE
         *   lorsque le stockage retourne null sur findById(...).
         */
        assertThatThrownBy(() -> this.service.delete(stp))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);

        /* Garantit que l'échec technique sur findById(...)
         * arrête le traitement avant toute suppression.
         */
        verify(this.entityManager, never()).remove(any(SousTypeProduitJPA.class));
        verify(this.entityManager, never()).flush();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(objet métier absent du stockage) :</p>
     * <ul>
     * <li>ne lève aucune exception</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>ne déclenche ni {@code remove(...)} ni {@code flush()}</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_DELETE)
    @DisplayName("delete(objet métier absent du stockage) : ne fait rien")
    @Test
    public void testDeleteAbsent() throws Exception {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp
            = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_2 via findById(...),
         * alors le DAO mocké avec Mockito devra répondre Optional.empty()".
         *
         * On simule donc volontairement un stockage
         * qui ne trouve aucun SousTypeProduit persistant
         * pour l'identifiant demandé.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.empty());

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        this.service.delete(stp);

        /* ASSERT */
        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);

        /* Garantit qu'aucune suppression n'a été déclenchée,
         * puisqu'aucun objet persistant n'a été trouvé
         * à supprimer dans le stockage.
         */
        verify(this.entityManager, never()).remove(any(SousTypeProduitJPA.class));
        verify(this.entityManager, never()).flush();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(KO DAO sur findById message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>ne déclenche ni {@code remove(...)} ni {@code flush()}</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO DAO sur findById message non null) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testDeleteDAOFindByIdExceptionMessageNonNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp
            = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_2 via findById(...),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException portant le message MSG_BOOM".
         */
        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenThrow(causeDao);

        /* ACT */
        /* Exécute une seule fois this.service.delete(stp)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.delete(stp));

        /* ASSERT */
        /* Garantit que this.service.delete(stp)
         * - jette une ExceptionTechniqueGateway
         * - émet un message commençant par ERREUR_TECHNIQUE_STOCKAGE
         * - conserve le message technique d'origine MSG_BOOM.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);

        /* Garantit que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);

        /* Garantit que l'échec technique sur findById(...)
         * arrête le traitement avant toute suppression.
         */
        verify(this.entityManager, never()).remove(any(SousTypeProduitJPA.class));
        verify(this.entityManager, never()).flush();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(KO DAO sur findById message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non null dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>ne déclenche ni {@code remove(...)} ni {@code flush()}</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO DAO sur findById message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testDeleteDAOFindByIdExceptionMessageNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp
            = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_2 via findById(...),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException sans message".
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenThrow(causeDao);

        /* ACT */
        /* Exécute une seule fois this.service.delete(stp)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.delete(stp));

        /* ASSERT */
        /* Garantit que this.service.delete(stp)
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

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);

        /* Garantit que l'échec technique sur findById(...)
         * arrête le traitement avant toute suppression.
         */
        verify(this.entityManager, never()).remove(any(SousTypeProduitJPA.class));
        verify(this.entityManager, never()).flush();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(KO EntityManager.remove message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>appelle {@code entityManager.remove(...)}</li>
     * <li>ne déclenche pas {@code entityManager.flush()}</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO EntityManager.remove message non null) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testDeleteEntityManagerRemoveExceptionMessageNonNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp
            = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final SousTypeProduitJPA entity
            = fabriquerSousTypeProduitJPA(
                    LIBELLE_ENFANT_1,
                    ID_2,
                    fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1));

        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(entity));

        /* Configure ici le comportement de l'EntityManager mocké avec Mockito.
         *
         * La formule doThrow(...).when(...) signifie :
         * "si, pendant le test, le service appelle l'EntityManager mocké
         * via remove(...),
         * alors l'EntityManager mocké devra lancer
         * une RuntimeException portant le message MSG_BOOM".
         */
        final RuntimeException causeEntityManager = new RuntimeException(MSG_BOOM);

        doThrow(causeEntityManager)
            .when(this.entityManager).remove(entity);

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.delete(stp));

        /* ASSERT */
        /* Garantit que this.service.delete(stp)
         * - jette une ExceptionTechniqueGateway
         * - conserve le message technique d'origine MSG_BOOM
         * - propage la cause d'origine.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);

        assertThat(throwable.getCause()).isSameAs(causeEntityManager);

        /* Garantit les interactions réellement attendues. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);
        verify(this.entityManager, times(1)).remove(entity);

        /* Garantit que l'échec sur remove(...)
         * arrête le traitement avant flush().
         */
        verify(this.entityManager, never()).flush();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(KO EntityManager.remove message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non null dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>appelle {@code entityManager.remove(...)}</li>
     * <li>ne déclenche pas {@code entityManager.flush()}</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO EntityManager.remove message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testDeleteEntityManagerRemoveExceptionMessageNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp
            = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final SousTypeProduitJPA entity
            = fabriquerSousTypeProduitJPA(
                    LIBELLE_ENFANT_1,
                    ID_2,
                    fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1));

        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(entity));

        /* Simule une panne technique de remove(...) sans message. */
        final RuntimeException causeEntityManager
            = new RuntimeException((String) null);

        doThrow(causeEntityManager)
            .when(this.entityManager).remove(entity);

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.delete(stp));

        /* ASSERT */
        /* Garantit que this.service.delete(stp)
         * - jette une ExceptionTechniqueGateway
         * - émet un message sûr non null
         * - propage la cause d'origine.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(RuntimeException.class.getName());

        assertThat(throwable.getCause()).isSameAs(causeEntityManager);

        /* Garantit les interactions réellement attendues. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);
        verify(this.entityManager, times(1)).remove(entity);

        /* Garantit que l'échec sur remove(...)
         * arrête le traitement avant flush().
         */
        verify(this.entityManager, never()).flush();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(KO EntityManager.flush message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>appelle {@code entityManager.remove(...)}</li>
     * <li>appelle {@code entityManager.flush()}</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO EntityManager.flush message non null) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testDeleteEntityManagerFlushExceptionMessageNonNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp
            = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final SousTypeProduitJPA entity
            = fabriquerSousTypeProduitJPA(
                    LIBELLE_ENFANT_1,
                    ID_2,
                    fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1));

        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(entity));

        doNothing().when(this.entityManager).remove(entity);

        /* Simule une panne technique de flush(...) avec message non null. */
        final RuntimeException causeEntityManager = new RuntimeException(MSG_BOOM);

        doThrow(causeEntityManager).when(this.entityManager).flush();

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.delete(stp));

        /* ASSERT */
        /* Garantit que this.service.delete(stp)
         * - jette une ExceptionTechniqueGateway
         * - conserve le message technique d'origine MSG_BOOM
         * - propage la cause d'origine.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);

        assertThat(throwable.getCause()).isSameAs(causeEntityManager);

        /* Garantit les interactions réellement attendues. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);
        verify(this.entityManager, times(1)).remove(entity);
        verify(this.entityManager, times(1)).flush();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(KO EntityManager.flush message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non null dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>appelle {@code entityManager.remove(...)}</li>
     * <li>appelle {@code entityManager.flush()}</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO EntityManager.flush message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testDeleteEntityManagerFlushExceptionMessageNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp
            = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final SousTypeProduitJPA entity
            = fabriquerSousTypeProduitJPA(
                    LIBELLE_ENFANT_1,
                    ID_2,
                    fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1));

        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(entity));

        doNothing().when(this.entityManager).remove(entity);

        /* Simule une panne technique de flush(...) sans message. */
        final RuntimeException causeEntityManager
            = new RuntimeException((String) null);

        doThrow(causeEntityManager).when(this.entityManager).flush();

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.delete(stp));

        /* ASSERT */
        /* Garantit que this.service.delete(stp)
         * - jette une ExceptionTechniqueGateway
         * - émet un message sûr non null
         * - propage la cause d'origine.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(RuntimeException.class.getName());

        assertThat(throwable.getCause()).isSameAs(causeEntityManager);

        /* Garantit les interactions réellement attendues. */
        verify(this.sousTypeProduitDaoJPA, times(1)).findById(ID_2);
        verify(this.entityManager, times(1)).remove(entity);
        verify(this.entityManager, times(1)).flush();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(vérification post-suppression échoue) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message indiquant l'échec de la suppression</li>
     * <li>appelle {@code entityManager.remove(...)}</li>
     * <li>appelle {@code entityManager.flush()}</li>
     * <li>appelle deux fois le DAO via {@code findById(...)}</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_DELETE)
    @DisplayName("delete(vérification post-suppression échoue) : jette ExceptionTechniqueGateway")
    @Test
    public void testDeleteVerificationPostSuppressionEchoue() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp
            = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final SousTypeProduitJPA entity
            = fabriquerSousTypeProduitJPA(
                    LIBELLE_ENFANT_1,
                    ID_2,
                    fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1));

        /* Le premier appel findById(...) trouve l'objet persistant.
         * Le second appel findById(...) le retrouve encore après suppression :
         * cela simule une suppression non effective.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(entity))
            .thenReturn(Optional.of(entity));

        doNothing().when(this.entityManager).remove(entity);
        doNothing().when(this.entityManager).flush();

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.delete(stp));

        /* ASSERT */
        /* Garantit que l'exception technique observable
         * signale bien l'échec de la suppression.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining("Échec de la suppression")
            .hasMessageContaining(ID_2.toString());

        /* Garantit les interactions réellement attendues. */
        verify(this.entityManager, times(1)).remove(entity);
        verify(this.entityManager, times(1)).flush();
        verify(this.sousTypeProduitDaoJPA, times(2)).findById(ID_2);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(vérification post-suppression KO DAO message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle {@code entityManager.remove(...)}</li>
     * <li>appelle {@code entityManager.flush()}</li>
     * <li>appelle deux fois le DAO via {@code findById(...)}</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_DELETE)
    @DisplayName("delete(vérification post-suppression KO DAO message non null) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testDeleteVerificationPostSuppressionDAOExceptionMessageNonNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp
            = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final SousTypeProduitJPA entity
            = fabriquerSousTypeProduitJPA(
                    LIBELLE_ENFANT_1,
                    ID_2,
                    fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1));

        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        /* Le premier appel findById(...) trouve l'objet persistant.
         * Le second appel findById(...) échoue pendant la vérification
         * post-suppression.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(entity))
            .thenThrow(causeDao);

        doNothing().when(this.entityManager).remove(entity);
        doNothing().when(this.entityManager).flush();

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.delete(stp));

        /* ASSERT */
        /* Garantit que this.service.delete(stp)
         * - jette une ExceptionTechniqueGateway
         * - conserve le message technique d'origine MSG_BOOM
         * - propage la cause DAO.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);

        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit les interactions réellement attendues. */
        verify(this.entityManager, times(1)).remove(entity);
        verify(this.entityManager, times(1)).flush();
        verify(this.sousTypeProduitDaoJPA, times(2)).findById(ID_2);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(vérification post-suppression KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non null dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle {@code entityManager.remove(...)}</li>
     * <li>appelle {@code entityManager.flush()}</li>
     * <li>appelle deux fois le DAO via {@code findById(...)}</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_DELETE)
    @DisplayName("delete(vérification post-suppression KO DAO message null) : jette ExceptionTechniqueGateway avec message sûr non null")
    @Test
    public void testDeleteVerificationPostSuppressionDAOExceptionMessageNull() {

        /* ARRANGE */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp
            = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final SousTypeProduitJPA entity
            = fabriquerSousTypeProduitJPA(
                    LIBELLE_ENFANT_1,
                    ID_2,
                    fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1));

        final RuntimeException causeDao = new RuntimeException((String) null);

        /* Le premier appel findById(...) trouve l'objet persistant.
         * Le second appel findById(...) échoue pendant la vérification
         * post-suppression.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(entity))
            .thenThrow(causeDao);

        doNothing().when(this.entityManager).remove(entity);
        doNothing().when(this.entityManager).flush();

        /* ACT */
        final Throwable throwable
            = Assertions.catchThrowable(() -> this.service.delete(stp));

        /* ASSERT */
        /* Garantit que this.service.delete(stp)
         * - jette une ExceptionTechniqueGateway
         * - émet un message sûr non null
         * - propage la cause DAO.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(RuntimeException.class.getName());

        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit les interactions réellement attendues. */
        verify(this.entityManager, times(1)).remove(entity);
        verify(this.entityManager, times(1)).flush();
        verify(this.sousTypeProduitDaoJPA, times(2)).findById(ID_2);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(OK) :</p>
     * <ul>
     * <li>charge l'objet persistant courant via {@code findById(...)}</li>
     * <li>déclenche la suppression via {@code entityManager.remove(...)}</li>
     * <li>force l'exécution via {@code entityManager.flush()}</li>
     * <li>vérifie que l'objet métier n'est plus retrouvé après suppression</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_DELETE)
    @DisplayName("delete(OK) : supprime l'objet persistant et vérifie la suppression")
    @Test
    public void testDeleteNominal() throws Exception {

        /* ARRANGE */
        final Long id = ID_2;

        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp
            = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, id, parent);

        final SousTypeProduitJPA entity
            = fabriquerSousTypeProduitJPA(
                    LIBELLE_ENFANT_1,
                    id,
                    fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1));

        /* Le premier appel findById(...) trouve l'objet persistant.
         * Le second appel findById(...) retourne Optional.empty()
         * après suppression.
         */
        when(this.sousTypeProduitDaoJPA.findById(id))
            .thenReturn(Optional.of(entity))
            .thenReturn(Optional.empty());

        doNothing().when(this.entityManager).remove(entity);
        doNothing().when(this.entityManager).flush();

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        this.service.delete(stp);

        /* ASSERT */
        /* Garantit que l'objet persistant a bien été supprimé
         * et que la suppression a été forcée.
         */
        verify(this.entityManager, times(1)).remove(entity);
        verify(this.entityManager, times(1)).flush();

        /* Garantit que le DAO a été appelé deux fois :
         * - une fois avant suppression ;
         * - une fois après suppression pour vérification.
         */
        verify(this.sousTypeProduitDaoJPA, times(2)).findById(id);

    } // __________________________________________________________________
    
    

    // ============================== Count ===============================
    
    
    
    /**
     * <div>
     * <p>garantit que count(stockage vide) :</p>
     * <ul>
     * <li>retourne {@code 0} ;</li>
     * <li>appelle le DAO objet métier une fois via {@code count()} ;</li>
     * <li>n'appelle ni le DAO parent ni l'EntityManager ;</li>
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
        when(this.sousTypeProduitDaoJPA.count()).thenReturn(0L);

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
        verify(this.sousTypeProduitDaoJPA, times(1)).count();

        /* Garantit que le DAO parent et l'EntityManager
         * ne participent pas au comptage.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que count(KO DAO message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine du DAO ;</li>
     * <li>propage comme cause l'exception technique d'origine ;</li>
     * <li>appelle le DAO objet métier une fois via {@code count()} ;</li>
     * <li>n'appelle ni le DAO parent ni l'EntityManager.</li>
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
         * une RuntimeException portant le message MSG_BOOM".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant le comptage.
         */
        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        when(this.sousTypeProduitDaoJPA.count()).thenThrow(causeDao);

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
         * - conserve le message technique d'origine MSG_BOOM.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);

        /* Garantit que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /* Garantit que le DAO objet métier mocké
         * a bien été appelé une fois via la méthode count().
         */
        verify(this.sousTypeProduitDaoJPA, times(1)).count();

        /* Garantit que le DAO parent et l'EntityManager
         * ne participent pas au comptage.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que count(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non null dérivé de l'exception technique ;</li>
     * <li>propage comme cause l'exception technique d'origine ;</li>
     * <li>appelle le DAO objet métier une fois via {@code count()} ;</li>
     * <li>n'appelle ni le DAO parent ni l'EntityManager.</li>
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

        when(this.sousTypeProduitDaoJPA.count()).thenThrow(causeDao);

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
        verify(this.sousTypeProduitDaoJPA, times(1)).count();

        /* Garantit que le DAO parent et l'EntityManager
         * ne participent pas au comptage.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que count(OK) :</p>
     * <ul>
     * <li>retourne le nombre d'objets métier présents dans le stockage ;</li>
     * <li>appelle le DAO objet métier une fois via {@code count()} ;</li>
     * <li>n'appelle ni le DAO parent ni l'EntityManager ;</li>
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
         * plusieurs SousTypeProduit persistants.
         */
        when(this.sousTypeProduitDaoJPA.count()).thenReturn(TOTAL_10);

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
        verify(this.sousTypeProduitDaoJPA, times(1)).count();

        /* Garantit que le DAO parent et l'EntityManager
         * ne participent pas au comptage.
         */
        verifyNoInteractions(this.typeProduitDaoJPA);
        verifyNoInteractions(this.entityManager);
        
    } // __________________________________________________________________
    
 
    
    // ================= TESTS BETON (sanity / invariants) ================



    /**
     * <div>
     * <p>garantit que toUpperCase(Locale) sur un libellé de référence :</p>
     * <ul>
     * <li>retourne une chaîne non null ;</li>
     * <li>retourne le libellé attendu en majuscules ;</li>
     * <li>reste explicitement dépendant de {@link #LOCALE_DEFAUT}.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_SANITY)
    @DisplayName("sanity(toUpperCase avec Locale) - retourne le libellé attendu en majuscules")
    @Test
    public void testSanityLocaleToUpperCase() throws Exception {

        /* ACT :
         * appelle LIBELLE_PARENT_1.toUpperCase(LOCALE_DEFAUT)
         * pour vérifier l'invariant global du fichier :
         * toute mise en majuscules doit rester explicitement liée à une Locale.
         */
        final String retour = LIBELLE_PARENT_1.toUpperCase(LOCALE_DEFAUT);

        /* ASSERT :
         * vérifie que l'appel retourne bien
         * une chaîne non nulle et conforme à la valeur attendue.
         */
        assertThat(retour).isNotNull();
        assertThat(retour).isEqualTo("TYPEPRODUIT_1");

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que safeMessage(...) :</p>
     * <ul>
     * <li>retourne {@link #CHAINE_VIDE} pour une entrée null ;</li>
     * <li>retourne {@link #CHAINE_VIDE} pour une entrée vide ;</li>
     * <li>retourne le message inchangé pour une entrée non vide.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_SANITY)
    @DisplayName("sanity(safeMessage) - normalise null et vide sans altérer un message nominal")
    @Test
    public void testSanitySafeMessage() throws Exception {

        /* ACT - ASSERT :
         * vérifie directement le comportement du helper safeMessage(...)
         * sur les trois cas utiles au reste du fichier :
         * - null ;
         * - chaîne vide ;
         * - message nominal.
         */
        assertThat(safeMessage(null)).isEqualTo(CHAINE_VIDE);
        assertThat(safeMessage(CHAINE_VIDE)).isEqualTo(CHAINE_VIDE);
        assertThat(safeMessage(LIBELLE_ENFANT_1)).isEqualTo(LIBELLE_ENFANT_1);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que isBlankButNotNull(...) :</p>
     * <ul>
     * <li>retourne {@code true} pour {@link #BLANK} ;</li>
     * <li>retourne {@code true} pour {@link #CHAINE_VIDE} ;</li>
     * <li>retourne {@code false} pour {@code null} ;</li>
     * <li>retourne {@code false} pour un libellé nominal.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_SANITY)
    @DisplayName("sanity(isBlankButNotNull) - distingue blank et null")
    @Test
    public void testSanityIsBlankButNotNull() throws Exception {

        /* ACT - ASSERT :
         * vérifie directement le comportement du helper
         * utilisé notamment par findByLibelleRapide(...).
         */
        assertThat(isBlankButNotNull(BLANK)).isTrue();
        assertThat(isBlankButNotNull(CHAINE_VIDE)).isTrue();
        assertThat(isBlankButNotNull(null)).isFalse();
        assertThat(isBlankButNotNull(LIBELLE_ENFANT_1)).isFalse();

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que construireMessageNonPersistent(...) :</p>
     * <ul>
     * <li>concatène correctement le préfixe et le libellé ;</li>
     * <li>utilise {@link #safeMessage(String)} pour neutraliser un libellé null ;</li>
     * <li>retourne toujours une chaîne non null.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_SANITY)
    @DisplayName("sanity(construireMessageNonPersistent) - concatène le préfixe et protège contre null")
    @Test
    public void testSanityConstruireMessageNonPersistent() throws Exception {

        /* ACT :
         * appelle le helper construireMessageNonPersistent(...)
         * avec un libellé nominal puis avec un libellé null.
         */
        final String messageNominal =
                construireMessageNonPersistent(
                        MSG_UPDATE_PREFIX_NON_PERSISTENT, LIBELLE_ENFANT_1);

        final String messageNull =
                construireMessageNonPersistent(
                        MSG_UPDATE_PREFIX_NON_PERSISTENT, null);

        /* ASSERT :
         * vérifie que le helper construit bien
         * le message attendu dans les deux situations.
         */
        assertThat(messageNominal).isNotNull();
        assertThat(messageNominal)
            .isEqualTo(MSG_UPDATE_PREFIX_NON_PERSISTENT + LIBELLE_ENFANT_1);

        assertThat(messageNull).isNotNull();
        assertThat(messageNull)
            .isEqualTo(MSG_UPDATE_PREFIX_NON_PERSISTENT + CHAINE_VIDE); // NOPMD by danyl on 21/04/2026 21:19

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que la valeur par défaut Mockito {@code null} sur findById(...) :</p>
     * <ul>
     * <li>est bien requalifiée en {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle une seule fois la méthode sousTypeProduitDaoJPA.findById(...).</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_SANITY)
    @DisplayName("sanity(Mockito Optional null) - findById(...) le requalifie en ExceptionTechniqueGateway")
    @Test
    public void testSanityMockitoDefaultNullOnOptionalIsHandled() {

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(ID_1)
         * sur le DAO mocké retourne null
         * au lieu d'un Optional<SousTypeProduitJPA>.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_1)).thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que :
         * this.service.findById(ID_1)
         * avec sousTypeProduitDaoJPA.findById(ID_1) qui retourne null
         * - jette une ExceptionTechniqueGateway
         * - avec un message MSG_ERREUR_TECH_KO_STOCKAGE.
         */
        assertThatThrownBy(() -> this.service.findById(ID_1))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Vérifie que la méthode sousTypeProduitDaoJPA.findById(ID_1)
         * du DAO mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findById(ID_1);

    } // __________________________________________________________________
        
    
    
	// ============================== OUTILS ===============================

    
    
	/**
	 * <div>
	 * <p>Fabrique un {@link TypeProduit} minimal.</p>
	 * </div>
	 *
	 * @param pLibelle : String
	 * @param pId : Long
	 * @return TypeProduit
	 */
	private static TypeProduit fabriquerTypeProduit(
	        final String pLibelle, final Long pId) {
		
	    return new TypeProduit(pId, pLibelle);
	    
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>Fabrique un {@link TypeProduitJPA} minimal.</p>
	 * </div>
	 *
	 * @param pLibelle : String
	 * @param pId : Long
	 * @return TypeProduitJPA
	 */
	private static TypeProduitJPA fabriquerTypeProduitJPA(
	        final String pLibelle, final Long pId) {
		
	    return new TypeProduitJPA(pId, pLibelle);
	    
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>Fabrique un {@link SousTypeProduit} minimal.</p>
	 * </div>
	 *
	 * @param pLibelle : String
	 * @param pId : Long
	 * @param pParent : TypeProduit
	 * @return SousTypeProduit
	 */
	private static SousTypeProduit fabriquerSousTypeProduit(
	        final String pLibelle, final Long pId, final TypeProduit pParent) {
		
	    return new SousTypeProduit(pId, pLibelle, pParent);
	    
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>Fabrique un {@link SousTypeProduitJPA} minimal.</p>
	 * </div>
	 *
	 * @param pLibelle : String
	 * @param pId : Long
	 * @param pParent : TypeProduitJPA
	 * @return SousTypeProduitJPA
	 */
	private static SousTypeProduitJPA fabriquerSousTypeProduitJPA(
	        final String pLibelle, final Long pId, final TypeProduitJPA pParent) {
		
	    return new SousTypeProduitJPA(pId, pLibelle, pParent);
	    
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>Retourne {@code true} si pString est blank mais pas null.</p>
	 * </div>
	 *
	 * @param pString : String
	 * @return boolean
	 */
	private static boolean isBlankButNotNull(final String pString) {
		
	    return pString != null && pString.isBlank();
	    
	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>Construit un message d'Exception non persistant (préfixe + libellé).</p>
	 * </div>
	 *
	 * @param pPrefix : String
	 * @param pLibelle : String
	 * @return String
	 */
	private static String construireMessageNonPersistent(
	        final String pPrefix, final String pLibelle) {
		
	    return pPrefix + safeMessage(pLibelle);
	    
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>safeMessage local test (même sémantique que l'ADAPTER : jamais null).</p>
	 * </div>
	 *
	 * @param p : Object
	 * @return String
	 */
	private static String safeMessage(final Object p) {
		
	    if (p == null) {
	        return CHAINE_VIDE;
	    }
	    
	    final String s = p.toString();
	    return (s != null) ? s : CHAINE_VIDE;
	    
	} // __________________________________________________________________
	
	
	
} // FIN DE LA CLASSE SousTypeProduitGatewayJPAServiceMockTest.------------
